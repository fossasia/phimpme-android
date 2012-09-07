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

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.content.AbstractThreadedSyncAdapter;
import android.content.BroadcastReceiver;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SyncResult;
import android.os.Bundle;

public class PicasaSyncAdapter extends AbstractThreadedSyncAdapter {
    private final Context mContext;
    public final static String TAG = "PicasaSyncAdapter";

    public PicasaSyncAdapter(Context applicationContext) {
        super(applicationContext, false);
        mContext = applicationContext;
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient providerClient,
            SyncResult syncResult) {
        if (extras.getBoolean(ContentResolver.SYNC_EXTRAS_INITIALIZE, false)) {
            try {
                Account[] picasaAccounts = AccountManager.get(getContext())
                        .getAccountsByTypeAndFeatures(
                        PicasaService.ACCOUNT_TYPE,
                        new String[] { PicasaService.FEATURE_SERVICE_NAME },
                        null /* callback */, null /* handler */).getResult();
                boolean isPicasaAccount = false;
                for (Account picasaAccount : picasaAccounts) {
                    if (account.equals(picasaAccount)) {
                        isPicasaAccount = true;
                        break;
                    }
                }
                if (isPicasaAccount) {
                    ContentResolver.setIsSyncable(account, authority, 1);
                    ContentResolver.setSyncAutomatically(account, authority, true);
                }
            } catch (OperationCanceledException e) {
                ;
            } catch (IOException e) {
                ;
            } catch (AuthenticatorException e) {
                ;
            }
            return;
        }
        try {
            PicasaService.performSync(mContext, account, extras, syncResult);
        } catch (Exception e) {
            // Report an error
            ++syncResult.stats.numIoExceptions;
        }
    }

    public static final class AccountChangeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO: Need to get account list change broadcast.
        }

    }
}
