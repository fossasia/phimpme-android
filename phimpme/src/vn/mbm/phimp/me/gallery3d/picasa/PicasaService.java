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
import java.util.concurrent.atomic.AtomicBoolean;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.app.Service;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SyncResult;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Process;

public final class PicasaService extends Service {
    public static final String ACTION_SYNC = "vn.mbm.phimp.me.gallery3d.picasa.action.SYNC";
    public static final String ACTION_PERIODIC_SYNC = "vn.mbm.phimp.me.gallery3d.picasa.action.PERIODIC_SYNC";
    public static final String ACCOUNT_TYPE = "com.google";
    public static final String SERVICE_NAME = "lh2";
    public static final String FEATURE_SERVICE_NAME = "service_" + SERVICE_NAME;
    public static final String KEY_TYPE = "vn.thongtran.gallery3d.SYNC_TYPE";
    public static final String KEY_ID = "vn.thongtran.gallery3d.SYNC_ID";
    public static final int TYPE_USERS = 0;
    public static final int TYPE_USERS_ALBUMS = 1;
    public static final int TYPE_ALBUM_PHOTOS = 2;

    private final HandlerThread mSyncThread = new HandlerThread("PicasaSyncThread");
    private final Handler mSyncHandler;
    private static final AtomicBoolean sSyncPending = new AtomicBoolean(false);

    public static void requestSync(Context context, int type, long id) {
        Bundle extras = new Bundle();
        extras.putInt(KEY_TYPE, type);
        extras.putLong(KEY_ID, id);

        Account[] accounts = PicasaApi.getAccounts(context);
        for (Account account : accounts) {
            ContentResolver.requestSync(account, PicasaContentProvider.AUTHORITY, extras);
        }

        // context.startService(new Intent(context,
        // PicasaService.class).putExtras(extras));
    }

    public PicasaService() {
        super();
        mSyncThread.start();
        mSyncHandler = new Handler(mSyncThread.getLooper());
        mSyncHandler.post(new Runnable() {
            public void run() {
                Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
            }
        });
    }

    private static PicasaContentProvider getContentProvider(Context context) {
        ContentResolver cr = context.getContentResolver();
        ContentProviderClient client = cr.acquireContentProviderClient(PicasaContentProvider.AUTHORITY);
        return (PicasaContentProvider) client.getLocalContentProvider();
    }

    @Override
    public int onStartCommand(final Intent intent, int flags, final int startId) {
        mSyncHandler.post(new Runnable() {
            public void run() {
                performSync(PicasaService.this, null, intent.getExtras(), new SyncResult());
                stopSelf(startId);
            }
        });
        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new PicasaSyncAdapter(getApplicationContext()).getSyncAdapterBinder();
    }

    @Override
    public void onDestroy() {
        mSyncThread.quit();
    }

    public static boolean performSync(Context context, Account account, Bundle extras, SyncResult syncResult) {
        // Skip if another sync is pending.
        if (!sSyncPending.compareAndSet(false, true)) {
            return false;
        }

        // Perform the sync.
        performSyncImpl(context, account, extras, syncResult);

        // Mark sync as complete and notify all waiters.
        sSyncPending.set(false);
        synchronized (sSyncPending) {
            sSyncPending.notifyAll();
        }
        return true;
    }

    public static void waitForPerformSync() {
        synchronized (sSyncPending) {
            while (sSyncPending.get()) {
                try {
                    // Wait for the sync to complete.
                    sSyncPending.wait();
                } catch (InterruptedException e) {
                    // Stop waiting if interrupted.
                    break;
                }
            }
        }
    }

    private static void performSyncImpl(Context context, Account account, Bundle extras, SyncResult syncResult) {
        // Initialize newly added accounts to sync by default.
        String authority = PicasaContentProvider.AUTHORITY;
        if (extras.getBoolean(ContentResolver.SYNC_EXTRAS_INITIALIZE, false)) {
            if (account != null && ContentResolver.getIsSyncable(account, authority) < 0) {
                try {
                    ContentResolver.setIsSyncable(account, authority, getIsSyncable(context, account) ? 1 : 0);
                } catch (OperationCanceledException e) {
                } catch (IOException e) {
                }
            }
            return;
        }

        // Do nothing if sync is disabled for this account. TODO: is this
        // blocked in PicasaContentProvider too?
        if (account != null && ContentResolver.getIsSyncable(account, authority) < 0) {
            ++syncResult.stats.numSkippedEntries;
            return;
        }

        // Get the type of sync operation and the entity ID, if applicable.
        // Default to synchronize all.
        int type = extras.getInt(PicasaService.KEY_TYPE, PicasaService.TYPE_USERS_ALBUMS);
        long id = extras.getLong(PicasaService.KEY_ID, -1);

        // Get the content provider instance and reload the list of user
        // accounts.
        PicasaContentProvider provider = getContentProvider(context);
        provider.reloadAccounts();

        // Restrict sync to either a specific account or all accounts.
        provider.setActiveSyncAccount(account);

        // Perform the desired sync operation.
        switch (type) {
        case PicasaService.TYPE_USERS:
            provider.syncUsers(syncResult);
            break;
        case PicasaService.TYPE_USERS_ALBUMS:
            provider.syncUsersAndAlbums(true, syncResult);
            break;
        case PicasaService.TYPE_ALBUM_PHOTOS:
            provider.syncAlbumPhotos(id, true, syncResult);
            break;
        default:
            throw new IllegalArgumentException();
        }
    }

    private static boolean getIsSyncable(Context context, Account account) throws IOException, OperationCanceledException {
        try {
            Account[] picasaAccounts = AccountManager.get(context).getAccountsByTypeAndFeatures(ACCOUNT_TYPE,
                    new String[] { FEATURE_SERVICE_NAME }, null /* callback */, null /* handler */).getResult();
            for (Account picasaAccount : picasaAccounts) {
                if (account.equals(picasaAccount)) {
                    return true;
                }
            }
            return false;
        } catch (AuthenticatorException e) {
            throw new IOException(e.getMessage());
        }
    }
}
