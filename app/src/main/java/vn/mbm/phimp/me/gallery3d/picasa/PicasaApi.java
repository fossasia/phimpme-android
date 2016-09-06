/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package vn.mbm.phimp.me.gallery3d.picasa;

import java.io.IOException;
import java.net.SocketException;
import java.util.ArrayList;

import org.apache.http.HttpStatus;
import org.xml.sax.SAXException;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.app.Activity;
import android.content.Context;
import android.content.SyncResult;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.Xml;

public final class PicasaApi {
    public static final int RESULT_OK = 0;
    public static final int RESULT_NOT_MODIFIED = 1;
    public static final int RESULT_ERROR = 2;

    private static final String TAG = "PicasaAPI";
    private static final String BASE_URL = "http://picasaweb.google.com/data/feed/api/";
    private static final String BASE_QUERY_STRING;

    static {
        // Build the base query string using screen dimensions.
        final StringBuilder query = new StringBuilder("?imgmax=1024&max-results=1000&thumbsize=");
        final String thumbnailSize = "144u,";
        final String screennailSize = "1024u";
        query.append(thumbnailSize);
        query.append(screennailSize);
        BASE_QUERY_STRING = query.toString() + "&visibility=visible";
    }

    private final GDataClient mClient;
    private final GDataClient.Operation mOperation = new GDataClient.Operation();
    private final GDataParser mParser = new GDataParser();
    private final AlbumEntry mAlbumInstance = new AlbumEntry();
    private final PhotoEntry mPhotoInstance = new PhotoEntry();
    private AuthAccount mAuth;

    public static final class AuthAccount {
        public final String user;
        public final String authToken;
        public final Account account;

        public AuthAccount(String user, String authToken, Account account) {
            this.user = user;
            this.authToken = authToken;
            this.account = account;
        }
    }

    public static Account[] getAccounts(Context context) {
        // Return the list of accounts supporting the Picasa GData service.
        AccountManager accountManager = AccountManager.get(context);
        Account[] accounts = {};
        try {
            accounts = accountManager.getAccountsByTypeAndFeatures(PicasaService.ACCOUNT_TYPE,
                    new String[] { PicasaService.FEATURE_SERVICE_NAME }, null, null).getResult();
        } catch (OperationCanceledException e) {
        } catch (AuthenticatorException e) {
        } catch (IOException e) {
        } catch (Exception e) {
            ;
        }
        return accounts;
    }

    public static AuthAccount[] getAuthenticatedAccounts(Context context) {
        AccountManager accountManager = AccountManager.get(context);
        Account[] accounts = getAccounts(context);
        if (accounts == null)
            accounts = new Account[0];
        int numAccounts = accounts.length;

        ArrayList<AuthAccount> authAccounts = new ArrayList<AuthAccount>(numAccounts);
        for (int i = 0; i != numAccounts; ++i) {
            Account account = accounts[i];
            String authToken;
            try {
                // Get the token without user interaction.
                authToken = accountManager.blockingGetAuthToken(account, PicasaService.SERVICE_NAME, true);

                // TODO: Remove this once the build is signed by Google, since
                // we will always have permission.
                // This code requests permission from the user explicitly.
                if (context instanceof Activity) {
                    Bundle bundle = accountManager.getAuthToken(account, PicasaService.SERVICE_NAME, null, (Activity) context,
                            null, null).getResult();
                    authToken = bundle.getString("authtoken");
                    PicasaService.requestSync(context, PicasaService.TYPE_USERS_ALBUMS, -1);
                }

                // Add the account information to the list of accounts.
                if (authToken != null) {
                    String username = canonicalizeUsername(account.name);
                    authAccounts.add(new AuthAccount(username, authToken, account));
                }
            } catch (OperationCanceledException e) {
            } catch (IOException e) {
            } catch (AuthenticatorException e) {
            } catch (Exception e) {
                ;
            }
        }
        AuthAccount[] authArray = new AuthAccount[authAccounts.size()];
        authAccounts.toArray(authArray);
        return authArray;
    }

    /**
     * Returns a canonical username for a Gmail account.  Lowercases the username and
     * strips off a "gmail.com" or "googlemail.com" domain, but leaves other domains alone.
     *
     * e.g., Passing in "User@gmail.com: will return "user".
     *
     * @param username The username to be canonicalized.
     * @return The username, lowercased and possibly stripped of its domain if a "gmail.com" or
     * "googlemail.com" domain.
     */
    public static String canonicalizeUsername(String username) {
        username = username.toLowerCase();
        if (username.contains("@gmail.") || username.contains("@googlemail.")) {
            // Strip the domain from GMail accounts for
            // canonicalization. TODO: is there an official way?
            username = username.substring(0, username.indexOf('@'));
        }
        return username;
    }

    public PicasaApi() {
        mClient = new GDataClient();
    }

    public void setAuth(AuthAccount auth) {
        mAuth = auth;
        synchronized (mClient) {
            mClient.setAuthToken(auth.authToken);
        }
    }

    public int getAlbums(AccountManager accountManager, SyncResult syncResult, UserEntry user, GDataParser.EntryHandler handler) {
        // Construct the query URL for user albums.
        StringBuilder builder = new StringBuilder(BASE_URL);
        builder.append("user/");
        builder.append(Uri.encode(mAuth.user));
        builder.append(BASE_QUERY_STRING);
        builder.append("&kind=album");
        try {
            // Send the request.
            synchronized (mOperation) {
                GDataClient.Operation operation = mOperation;
                operation.inOutEtag = user.albumsEtag;
                boolean retry = false;
                int numRetries = 1;
                do {
                    retry = false;
                    synchronized (mClient) {
                        mClient.get(builder.toString(), operation);
                    }
                    switch (operation.outStatus) {
                    case HttpStatus.SC_OK:
                        break;
                    case HttpStatus.SC_NOT_MODIFIED:
                        return RESULT_NOT_MODIFIED;
                    case HttpStatus.SC_FORBIDDEN:
                    case HttpStatus.SC_UNAUTHORIZED:
                        if (!retry) {
                            accountManager.invalidateAuthToken(PicasaService.ACCOUNT_TYPE, mAuth.authToken);
                            retry = true;
                        }
                        if (numRetries == 0) {
                            ++syncResult.stats.numAuthExceptions;
                        }
                    default:
                        Log.i(TAG, "getAlbums uri " + builder.toString());
                        Log.e(TAG, "getAlbums: unexpected status code " + operation.outStatus + " data: "
                                + operation.outBody.toString());
                        ++syncResult.stats.numIoExceptions;
                        return RESULT_ERROR;
                    }
                    --numRetries;
                } while (retry && numRetries >= 0);

                // Store the new ETag for the user/albums feed.
                user.albumsEtag = operation.inOutEtag;

                // Parse the response.
                synchronized (mParser) {
                    GDataParser parser = mParser;
                    parser.setEntry(mAlbumInstance);
                    parser.setHandler(handler);
                    try {
                        Xml.parse(operation.outBody, Xml.Encoding.UTF_8, parser);
                    } catch (SocketException e) {
                        Log.e(TAG, "getAlbumPhotos: " + e);
                        ++syncResult.stats.numIoExceptions;
                        e.printStackTrace();
                        return RESULT_ERROR;
                    }
                }
            }
            return RESULT_OK;
        } catch (IOException e) {
            Log.e(TAG, "getAlbums: " + e);
            ++syncResult.stats.numIoExceptions;
        } catch (SAXException e) {
            Log.e(TAG, "getAlbums: " + e);
            ++syncResult.stats.numParseExceptions;
        }
        return RESULT_ERROR;
    }

    public int getAlbumPhotos(AccountManager accountManager, SyncResult syncResult, AlbumEntry album,
            GDataParser.EntryHandler handler) {
        // Construct the query URL for user albums.
        StringBuilder builder = new StringBuilder(BASE_URL);
        builder.append("user/");
        builder.append(Uri.encode(mAuth.user));
        builder.append("/albumid/");
        builder.append(album.id);
        builder.append(BASE_QUERY_STRING);
        builder.append("&kind=photo");
        try {
            // Send the request.
            synchronized (mOperation) {
                GDataClient.Operation operation = mOperation;
                operation.inOutEtag = album.photosEtag;
                boolean retry = false;
                int numRetries = 1;
                do {
                    retry = false;
                    synchronized (mClient) {
                        mClient.get(builder.toString(), operation);
                    }
                    switch (operation.outStatus) {
                    case HttpStatus.SC_OK:
                        break;
                    case HttpStatus.SC_NOT_MODIFIED:
                        return RESULT_NOT_MODIFIED;
                    case HttpStatus.SC_FORBIDDEN:
                    case HttpStatus.SC_UNAUTHORIZED:
                        // We need to reset the authtoken and retry only once.
                        if (!retry) {
                            retry = true;
                            accountManager.invalidateAuthToken(PicasaService.SERVICE_NAME, mAuth.authToken);
                        }
                        if (numRetries == 0) {
                            ++syncResult.stats.numAuthExceptions;
                        }
                        break;
                    default:
                        Log.e(TAG, "getAlbumPhotos: " + builder.toString() + ", unexpected status code " + operation.outStatus);
                        ++syncResult.stats.numIoExceptions;
                        return RESULT_ERROR;
                    }
                    --numRetries;
                } while (retry && numRetries >= 0);

                // Store the new ETag for the album/photos feed.
                album.photosEtag = operation.inOutEtag;

                // Parse the response.
                synchronized (mParser) {
                    GDataParser parser = mParser;
                    parser.setEntry(mPhotoInstance);
                    parser.setHandler(handler);
                    try {
                        Xml.parse(operation.outBody, Xml.Encoding.UTF_8, parser);
                    } catch (SocketException e) {
                        Log.e(TAG, "getAlbumPhotos: " + e);
                        ++syncResult.stats.numIoExceptions;
                        e.printStackTrace();
                        return RESULT_ERROR;
                    }
                }
            }
            return RESULT_OK;
        } catch (IOException e) {
            Log.e(TAG, "getAlbumPhotos: " + e);
            ++syncResult.stats.numIoExceptions;
            e.printStackTrace();
        } catch (SAXException e) {
            Log.e(TAG, "getAlbumPhotos: " + e);
            ++syncResult.stats.numParseExceptions;
            e.printStackTrace();
        }
        return RESULT_ERROR;
    }

    public int deleteEntry(String editUri) {
        try {
            synchronized (mOperation) {
                GDataClient.Operation operation = mOperation;
                operation.inOutEtag = null;
                synchronized (mClient) {
                    mClient.delete(editUri, operation);
                }
                if (operation.outStatus == 200) {
                    return RESULT_OK;
                } else {
                    Log.e(TAG, "deleteEntry: failed with status code " + operation.outStatus);
                }
            }
        } catch (IOException e) {
            Log.e(TAG, "deleteEntry: " + e);
        }
        return RESULT_ERROR;
    }

    /**
     * Column names shared by multiple entry kinds.
     */
    public static class Columns {
        public static final String _ID = "_id";
        public static final String SYNC_ACCOUNT = "sync_account";
        public static final String EDIT_URI = "edit_uri";
        public static final String TITLE = "title";
        public static final String SUMMARY = "summary";
        public static final String DATE_PUBLISHED = "date_published";
        public static final String DATE_UPDATED = "date_updated";
        public static final String DATE_EDITED = "date_edited";
        public static final String THUMBNAIL_URL = "thumbnail_url";
        public static final String HTML_PAGE_URL = "html_page_url";
    }
}
