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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SyncResult;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.util.Log;

public final class PicasaContentProvider extends TableContentProvider {
    public static final String AUTHORITY = "vn.mbm.phimp.me.gallery3d.picasa.contentprovider";
    public static final Uri BASE_URI = Uri.parse("content://" + AUTHORITY);
    public static final Uri PHOTOS_URI = Uri.withAppendedPath(BASE_URI, "photos");
    public static final Uri ALBUMS_URI = Uri.withAppendedPath(BASE_URI, "albums");

    private static final String TAG = "PicasaContentProvider";
    private static final String[] ID_EDITED_PROJECTION = { "_id", "date_edited" };
    private static final String[] ID_EDITED_INDEX_PROJECTION = { "_id", "date_edited", "display_index" };
    private static final String WHERE_ACCOUNT = "sync_account=?";
    private static final String WHERE_ALBUM_ID = "album_id=?";

    private final PhotoEntry mPhotoInstance = new PhotoEntry();
    private final AlbumEntry mAlbumInstance = new AlbumEntry();
    private SyncContext mSyncContext = null;
    private Account mActiveAccount;

    @Override
    public void attachInfo(Context context, ProviderInfo info) {
        // Initialize the provider and set the database.
        super.attachInfo(context, info);
        setDatabase(new Database(context, Database.DATABASE_NAME));

        // Add mappings for each of the exposed tables.
        addMapping(AUTHORITY, "photos", "vnd.cooliris.picasa.photo", PhotoEntry.SCHEMA);
        addMapping(AUTHORITY, "albums", "vnd.cooliris.picasa.album", AlbumEntry.SCHEMA);

        // Create the sync context.
        try {
            mSyncContext = new SyncContext();
        } catch (Exception e) {
            // The database wasn't created successfully, we create a memory backed database.
            setDatabase(new Database(context, null));
        }
    }

    public static final class Database extends SQLiteOpenHelper {
        public static final String DATABASE_NAME = "picasa.db";
        public static final int DATABASE_VERSION = 83;

        public Database(Context context, String name) {
            super(context, name, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            PhotoEntry.SCHEMA.createTables(db);
            AlbumEntry.SCHEMA.createTables(db);
            UserEntry.SCHEMA.createTables(db);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // No new versions yet, if we are asked to upgrade we just reset
            // everything.
            PhotoEntry.SCHEMA.dropTables(db);
            AlbumEntry.SCHEMA.dropTables(db);
            UserEntry.SCHEMA.dropTables(db);
            onCreate(db);
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Ensure that the URI is well-formed. We currently do not allow WHERE
        // clauses.
        List<String> path = uri.getPathSegments();
        if (path.size() != 2 || !uri.getAuthority().equals(AUTHORITY) || selection != null) {
            return 0;
        }

        // Get the sync context.
        SyncContext context = mSyncContext;

        // Determine if the URI refers to an album or photo.
        String type = path.get(0);
        long id = Long.parseLong(path.get(1));
        SQLiteDatabase db = context.db;
        if (type.equals("photos")) {
            // Retrieve the photo from the database to get the edit URI.
            PhotoEntry photo = mPhotoInstance;
            if (PhotoEntry.SCHEMA.queryWithId(db, id, photo)) {
                // Send a DELETE request to the API.
                if (context.login(photo.syncAccount)) {
                    if (context.api.deleteEntry(photo.editUri) == PicasaApi.RESULT_OK) {
                        deletePhoto(db, id);
                        context.photosChanged = true;
                        return 1;
                    }
                }
            }
        } else if (type.equals("albums")) {
            // Retrieve the album from the database to get the edit URI.
            AlbumEntry album = mAlbumInstance;
            if (AlbumEntry.SCHEMA.queryWithId(db, id, album)) {
                // Send a DELETE request to the API.
                if (context.login(album.syncAccount)) {
                    if (context.api.deleteEntry(album.editUri) == PicasaApi.RESULT_OK) {
                        deleteAlbum(db, id);
                        context.albumsChanged = true;
                        return 1;
                    }
                }
            }
        }
        context.finish();
        return 0;
    }

    public void reloadAccounts() {
        mSyncContext.reloadAccounts();
    }

    public void setActiveSyncAccount(Account account) {
        mActiveAccount = account;
    }

    public void syncUsers(SyncResult syncResult) {
        syncUsers(mSyncContext, syncResult);
    }

    public void syncUsersAndAlbums(final boolean syncAlbumPhotos, SyncResult syncResult) {
        SyncContext context = mSyncContext;

        // Synchronize users authenticated on the device.
        UserEntry[] users = syncUsers(context, syncResult);

        // Synchronize albums for each user.
        String activeUsername = null;
        if (mActiveAccount != null) {
            activeUsername = PicasaApi.canonicalizeUsername(mActiveAccount.name);
        }
        boolean didSyncActiveUserName = false;
        for (int i = 0, numUsers = users.length; i != numUsers; ++i) {
            if (activeUsername != null && !context.accounts[i].user.equals(activeUsername))
                continue;
            if (!ContentResolver.getSyncAutomatically(context.accounts[i].account, AUTHORITY))
                continue;
            didSyncActiveUserName = true;
            context.api.setAuth(context.accounts[i]);
            syncUserAlbums(context, users[i], syncResult);
            if (syncAlbumPhotos) {
                syncUserPhotos(context, users[i].account, syncResult);
            } else {
                // // Always sync added albums.
                // for (Long albumId : context.albumsAdded) {
                // syncAlbumPhotos(albumId, false);
                // }
            }
        }
        if (!didSyncActiveUserName) {
            ++syncResult.stats.numAuthExceptions;
        }
        context.finish();
    }

    public void syncAlbumPhotos(final long albumId, final boolean forceRefresh, SyncResult syncResult) {
        SyncContext context = mSyncContext;
        AlbumEntry album = new AlbumEntry();
        if (AlbumEntry.SCHEMA.queryWithId(context.db, albumId, album)) {
            if ((album.photosDirty || forceRefresh) && context.login(album.syncAccount)) {
                if (isSyncEnabled(album.syncAccount, context)) {
                    syncAlbumPhotos(context, album.syncAccount, album, syncResult);
                }
            }
        }
        context.finish();
    }

    public static boolean isSyncEnabled(String accountName, SyncContext context) {
        if (context.accounts == null) {
            context.reloadAccounts();
        }
        PicasaApi.AuthAccount[] accounts = context.accounts;
        int numAccounts = accounts.length;
        for (int i = 0; i < numAccounts; ++i) {
            PicasaApi.AuthAccount account = accounts[i];
            if (account.user.equals(accountName)) {
                return ContentResolver.getSyncAutomatically(account.account, AUTHORITY);
            }
        }
        return true;
    }

    private UserEntry[] syncUsers(SyncContext context, SyncResult syncResult) {
        // Get authorized accounts.
        context.reloadAccounts();
        PicasaApi.AuthAccount[] accounts = context.accounts;
        int numUsers = accounts.length;
        UserEntry[] users = new UserEntry[numUsers];

        // Scan existing accounts.
        EntrySchema schema = UserEntry.SCHEMA;
        SQLiteDatabase db = context.db;
        Cursor cursor = schema.queryAll(db);
        if (cursor.moveToFirst()) {
            do {
                // Read the current account.
                UserEntry entry = new UserEntry();
                schema.cursorToObject(cursor, entry);

                // Find the corresponding account, or delete the row if it does
                // not exist.
                int i;
                for (i = 0; i != numUsers; ++i) {
                    if (accounts[i].user.equals(entry.account)) {
                        users[i] = entry;
                        break;
                    }
                }
                if (i == numUsers) {
                    Log.e(TAG, "Deleting user " + entry.account);
                    entry.albumsEtag = null;
                    deleteUser(db, entry.account);
                }
            } while (cursor.moveToNext());
        } else {
            // Log.i(TAG, "No users in database yet");
        }
        cursor.close();

        // Add new accounts and synchronize user albums if recursive.
        for (int i = 0; i != numUsers; ++i) {
            UserEntry entry = users[i];
            PicasaApi.AuthAccount account = accounts[i];
            if (entry == null) {
                entry = new UserEntry();
                entry.account = account.user;
                users[i] = entry;
                Log.e(TAG, "Inserting user " + entry.account);
            }
        }
        return users;
    }

    private void syncUserAlbums(final SyncContext context, final UserEntry user, final SyncResult syncResult) {
        // Query existing album entry (id, dateEdited) sorted by ID.
        final SQLiteDatabase db = context.db;
        Cursor cursor = db.query(AlbumEntry.SCHEMA.getTableName(), ID_EDITED_PROJECTION, WHERE_ACCOUNT,
                new String[] { user.account }, null, null, AlbumEntry.Columns.DATE_EDITED);
        int localCount = cursor.getCount();

        // Build a sorted index with existing entry timestamps.
        final EntryMetadata local[] = new EntryMetadata[localCount];
        for (int i = 0; i != localCount; ++i) {
            cursor.moveToPosition(i); // TODO: throw exception here if returns
                                      // false?
            local[i] = new EntryMetadata(cursor.getLong(0), cursor.getLong(1), 0);
        }
        cursor.close();
        Arrays.sort(local);

        // Merge the truth from the API into the local database.
        final EntrySchema albumSchema = AlbumEntry.SCHEMA;
        final EntryMetadata key = new EntryMetadata();
        final AccountManager accountManager = AccountManager.get(getContext());
        int result = context.api.getAlbums(accountManager, syncResult, user, new GDataParser.EntryHandler() {
            public void handleEntry(Entry entry) {
                AlbumEntry album = (AlbumEntry) entry;
                long albumId = album.id;
                key.id = albumId;
                int index = Arrays.binarySearch(local, key);
                EntryMetadata metadata = index >= 0 ? local[index] : null;
                if (metadata == null || metadata.dateEdited < album.dateEdited) {
                    // Insert / update.
                    Log.i(TAG, "insert / update album " + album.title);
                    album.syncAccount = user.account;
                    album.photosDirty = true;
                    albumSchema.insertOrReplace(db, album);
                    if (metadata == null) {
                        context.albumsAdded.add(albumId);
                    }
                    ++syncResult.stats.numUpdates;
                } else {
                    // Up-to-date.
                    // Log.i(TAG, "up-to-date album " + album.title);
                }

                // Mark item as surviving so it is not deleted.
                if (metadata != null) {
                    metadata.survived = true;
                }
            }
        });

        // Return if not modified or on error.
        switch (result) {
        case PicasaApi.RESULT_ERROR:
            ++syncResult.stats.numParseExceptions;
        case PicasaApi.RESULT_NOT_MODIFIED:
            return;
        }

        // Update the user entry with the new ETag.
        UserEntry.SCHEMA.insertOrReplace(db, user);

        // Delete all entries not present in the API response.
        for (int i = 0; i != localCount; ++i) {
            EntryMetadata metadata = local[i];
            if (!metadata.survived) {
                deleteAlbum(db, metadata.id);
                ++syncResult.stats.numDeletes;
                Log.i(TAG, "delete album " + metadata.id);
            }
        }

        // Note that albums changed.
        context.albumsChanged = true;
    }

    private void syncUserPhotos(SyncContext context, String account, SyncResult syncResult) {
        // Synchronize albums with out-of-date photos.
        SQLiteDatabase db = context.db;
        Cursor cursor = db.query(AlbumEntry.SCHEMA.getTableName(), Entry.ID_PROJECTION, "sync_account=? AND photos_dirty=1",
                new String[] { account }, null, null, null);
        AlbumEntry album = new AlbumEntry();
        for (int i = 0, count = cursor.getCount(); i != count; ++i) {
            cursor.moveToPosition(i);
            if (AlbumEntry.SCHEMA.queryWithId(db, cursor.getLong(0), album)) {
                syncAlbumPhotos(context, account, album, syncResult);
            }

            // Abort if interrupted.
            if (Thread.interrupted()) {
                ++syncResult.stats.numIoExceptions;
                Log.e(TAG, "syncUserPhotos interrupted");
            }
        }
        cursor.close();
    }

    private void syncAlbumPhotos(SyncContext context, final String account, AlbumEntry album, final SyncResult syncResult) {
        Log.i(TAG, "Syncing Picasa album: " + album.title);
        // Query existing album entry (id, dateEdited) sorted by ID.
        final SQLiteDatabase db = context.db;
        long albumId = album.id;
        String[] albumIdArgs = { Long.toString(albumId) };
        Cursor cursor = db.query(PhotoEntry.SCHEMA.getTableName(), ID_EDITED_INDEX_PROJECTION, WHERE_ALBUM_ID, albumIdArgs, null,
                null, "date_edited");
        int localCount = cursor.getCount();

        // Build a sorted index with existing entry timestamps and display
        // indexes.
        final EntryMetadata local[] = new EntryMetadata[localCount];
        final EntryMetadata key = new EntryMetadata();
        for (int i = 0; i != localCount; ++i) {
            cursor.moveToPosition(i); // TODO: throw exception here if returns
                                      // false?
            local[i] = new EntryMetadata(cursor.getLong(0), cursor.getLong(1), cursor.getInt(2));
        }
        cursor.close();
        Arrays.sort(local);

        // Merge the truth from the API into the local database.
        final EntrySchema photoSchema = PhotoEntry.SCHEMA;
        final int[] displayIndex = { 0 };
        final AccountManager accountManager = AccountManager.get(getContext());
        int result = context.api.getAlbumPhotos(accountManager, syncResult, album, new GDataParser.EntryHandler() {
            public void handleEntry(Entry entry) {
                PhotoEntry photo = (PhotoEntry) entry;
                long photoId = photo.id;
                int newDisplayIndex = displayIndex[0];
                key.id = photoId;
                int index = Arrays.binarySearch(local, key);
                EntryMetadata metadata = index >= 0 ? local[index] : null;
                if (metadata == null || metadata.dateEdited < photo.dateEdited || metadata.displayIndex != newDisplayIndex) {

                    // Insert / update.
                    // Log.i(TAG, "insert / update photo " + photo.title);
                    photo.syncAccount = account;
                    photo.displayIndex = newDisplayIndex;
                    photoSchema.insertOrReplace(db, photo);
                    ++syncResult.stats.numUpdates;
                } else {
                    // Up-to-date.
                    // Log.i(TAG, "up-to-date photo " + photo.title);
                }

                // Mark item as surviving so it is not deleted.
                if (metadata != null) {
                    metadata.survived = true;
                }

                // Increment the display index.
                displayIndex[0] = newDisplayIndex + 1;
            }
        });

        // Return if not modified or on error.
        switch (result) {
        case PicasaApi.RESULT_ERROR:
            ++syncResult.stats.numParseExceptions;
            Log.e(TAG, "syncAlbumPhotos error");
        case PicasaApi.RESULT_NOT_MODIFIED:
            // Log.e(TAG, "result not modified");
            return;
        }

        // Delete all entries not present in the API response.
        for (int i = 0; i != localCount; ++i) {
            EntryMetadata metadata = local[i];
            if (!metadata.survived) {
                deletePhoto(db, metadata.id);
                ++syncResult.stats.numDeletes;
                // Log.i(TAG, "delete photo " + metadata.id);
            }
        }

        // Mark album as no longer dirty and store the new ETag.
        album.photosDirty = false;
        AlbumEntry.SCHEMA.insertOrReplace(db, album);
        // Log.i(TAG, "Clearing dirty bit on album " + albumId);

        // Mark that photos changed.
        // context.photosChanged = true;
        getContext().getContentResolver().notifyChange(ALBUMS_URI, null, false);
        getContext().getContentResolver().notifyChange(PHOTOS_URI, null, false);
    }

    private void deleteUser(SQLiteDatabase db, String account) {
        Log.w(TAG, "deleteUser(" + account + ")");

        // Select albums owned by the user.
        String albumTableName = AlbumEntry.SCHEMA.getTableName();
        String[] whereArgs = { account };
        Cursor cursor = db.query(AlbumEntry.SCHEMA.getTableName(), Entry.ID_PROJECTION, WHERE_ACCOUNT, whereArgs, null, null, null);

        // Delete contained photos for each album.
        if (cursor.moveToFirst()) {
            do {
                deleteAlbumPhotos(db, cursor.getLong(0));
            } while (cursor.moveToNext());
        }
        cursor.close();

        // Delete all albums.
        db.delete(albumTableName, WHERE_ACCOUNT, whereArgs);

        // Delete the user entry.
        db.delete(UserEntry.SCHEMA.getTableName(), "account=?", whereArgs);
    }

    private void deleteAlbum(SQLiteDatabase db, long albumId) {
        // Delete contained photos.
        deleteAlbumPhotos(db, albumId);

        // Delete the album.
        AlbumEntry.SCHEMA.deleteWithId(db, albumId);
    }

    private void deleteAlbumPhotos(SQLiteDatabase db, long albumId) {
        Log.v(TAG, "deleteAlbumPhotos(" + albumId + ")");
        String photoTableName = PhotoEntry.SCHEMA.getTableName();
        String[] whereArgs = { Long.toString(albumId) };
        Cursor cursor = db.query(photoTableName, Entry.ID_PROJECTION, WHERE_ALBUM_ID, whereArgs, null, null, null);

        // Delete cache entry for each photo.
        if (cursor.moveToFirst()) {
            do {
                deletePhotoCache(cursor.getLong(0));
            } while (cursor.moveToNext());
        }
        cursor.close();

        // Delete all photos.
        db.delete(photoTableName, WHERE_ALBUM_ID, whereArgs);
    }

    private void deletePhoto(SQLiteDatabase db, long photoId) {
        PhotoEntry.SCHEMA.deleteWithId(db, photoId);
        deletePhotoCache(photoId);
    }

    private void deletePhotoCache(long photoId) {
        // TODO: implement it.
    }

    private final class SyncContext {
        // List of all authenticated user accounts.
        public PicasaApi.AuthAccount[] accounts;

        // A connection to the Picasa API for a specific user account. Initially
        // null.
        public PicasaApi api = new PicasaApi();

        // A handle to the Picasa databse.
        public SQLiteDatabase db;

        // List of album IDs that were added during the sync.
        public final ArrayList<Long> albumsAdded = new ArrayList<Long>();

        // Set to true if albums were changed.
        public boolean albumsChanged = false;

        // Set to true if photos were changed.
        public boolean photosChanged = false;

        public SyncContext() {
            db = mDatabase.getWritableDatabase();
        }

        public void reloadAccounts() {
            accounts = PicasaApi.getAuthenticatedAccounts(getContext());
        }

        public void finish() {
            // Send notifications if needed and reset state.
            ContentResolver cr = getContext().getContentResolver();
            if (albumsChanged) {
                cr.notifyChange(ALBUMS_URI, null, false);
            }
            if (photosChanged) {
                cr.notifyChange(PHOTOS_URI, null, false);
            }
            albumsChanged = false;
            photosChanged = false;
        }

        public boolean login(String user) {
            if (accounts == null) {
                reloadAccounts();
            }
            final PicasaApi.AuthAccount[] authAccounts = accounts;
            for (PicasaApi.AuthAccount auth : authAccounts) {
                if (auth.user.equals(user)) {
                    api.setAuth(auth);
                    return true;
                }
            }
            return false;
        }
    }

    /**
     * Minimal metadata gathered during sync.
     */
    private static final class EntryMetadata implements Comparable<EntryMetadata> {
        public long id;
        public long dateEdited;
        public int displayIndex;
        public boolean survived = false;

        public EntryMetadata() {
        }

        public EntryMetadata(long id, long dateEdited, int displayIndex) {
            this.id = id;
            this.dateEdited = dateEdited;
            this.displayIndex = displayIndex;
        }

        public int compareTo(EntryMetadata other) {
            return Long.signum(id - other.id);
        }

    }
}
