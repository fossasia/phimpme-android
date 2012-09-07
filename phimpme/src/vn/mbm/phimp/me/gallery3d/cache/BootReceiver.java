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

package vn.mbm.phimp.me.gallery3d.cache;

import vn.mbm.phimp.me.gallery3d.media.LocalDataSource;
import vn.mbm.phimp.me.gallery3d.media.PicasaDataSource;
import vn.mbm.phimp.me.gallery3d.media.Utils;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

public class BootReceiver extends BroadcastReceiver {
    private static final String TAG = "BootReceiver";
    
    @Override
    public void onReceive(final Context context, Intent intent) {
        final String action = intent.getAction();
        Log.i(TAG, "Got intent with action " + action);
        if (Intent.ACTION_MEDIA_SCANNER_FINISHED.equals(action)) {
            ;
        } else if (Intent.ACTION_MEDIA_MOUNTED.equals(action)) {
            ;
        } else if (action.equals(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)) {
            final Uri fileUri = intent.getData();
            final long bucketId = Utils.getBucketIdFromUri(context.getContentResolver(), fileUri);
            if (!CacheService.isPresentInCache(bucketId)) {
                CacheService.markDirty();
            }
        } else if (action.equals(Intent.ACTION_MEDIA_EJECT)) {
            LocalDataSource.sThumbnailCache.close();
            LocalDataSource.sThumbnailCacheVideo.close();
            PicasaDataSource.sThumbnailCache.close();
            CacheService.sAlbumCache.close();
            CacheService.sMetaAlbumCache.close();
            CacheService.sSkipThumbnailIds.flush();
        }
    }
}
