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

package vn.mbm.phimp.me.gallery3d.media;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;

import vn.mbm.phimp.me.gallery3d.cache.CacheService;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.provider.MediaStore.Video;
import android.util.Log;

public class LocalDataSource implements DataSource {
    private static final String TAG = "LocalDataSource";
    public static final String URI_ALL_MEDIA = MediaStore.Images.Media.EXTERNAL_CONTENT_URI.toString();
    public static final DiskCache sThumbnailCache = new DiskCache("local-image-thumbs");
    public static final DiskCache sThumbnailCacheVideo = new DiskCache("local-video-thumbs");

    public static final String CAMERA_STRING = "Camera";
    public static final String DOWNLOAD_STRING = "download";
    public static final String CAMERA_BUCKET_NAME = Environment.getExternalStorageDirectory().toString() + "/DCIM/" + CAMERA_STRING;
    public static final String DOWNLOAD_BUCKET_NAME = Environment.getExternalStorageDirectory().toString() + "/" + DOWNLOAD_STRING;
    public static final int CAMERA_BUCKET_ID = getBucketId(CAMERA_BUCKET_NAME);
    public static final int DOWNLOAD_BUCKET_ID = getBucketId(DOWNLOAD_BUCKET_NAME);
    
    /**
     * Matches code in MediaProvider.computeBucketValues. Should be a common
     * function.
     */
    public static int getBucketId(String path) {
        return (path.toLowerCase().hashCode());
    }
    
    private final String mUri;
    private final String mBucketId;
    private boolean mDone;;
    private final boolean mSingleUri;
    private final boolean mAllItems;
    private final boolean mFlattenAllItems;
    private final DiskCache mDiskCache;
    private boolean mIncludeImages;
    private boolean mIncludeVideos;
    private Context mContext;

    public LocalDataSource(final Context context, final String uri, final boolean flattenAllItems) {
        this.mUri = uri;
        mContext = context;
        mIncludeImages = true;
        mIncludeVideos = false;
        String bucketId = Uri.parse(uri).getQueryParameter("bucketId");
        if (bucketId != null && bucketId.length() > 0) {
            mBucketId = bucketId;
        } else {
            mBucketId = null;
        }
        mFlattenAllItems = flattenAllItems;
        if (mBucketId == null) {
            /*if (uri.equals(MediaStore.Images.Media.EXTERNAL_CONTENT_URI.toString())) {
                mAllItems = true;
            } else {*/
                mAllItems = false;
            /*}*/
        } else {
            mAllItems = false;
        }
        //mAllItems = false;
        mSingleUri = isSingleImageMode(uri) && mBucketId == null;
        mDone = false;
        mDiskCache = mUri.startsWith(MediaStore.Images.Media.EXTERNAL_CONTENT_URI.toString())
                || mUri.startsWith(MediaStore.Video.Media.EXTERNAL_CONTENT_URI.toString())
                || mUri.startsWith("file://") ? sThumbnailCache
                : null;
    }
    
    public void setMimeFilter(boolean includeImages, boolean includeVideos) {
        mIncludeImages = includeImages;
        mIncludeVideos = includeVideos;
    }

    public void shutdown() {

    }

    public boolean isSingleImage() {
        return mSingleUri;
    }

    private static boolean isSingleImageMode(String uriString) {
        return !uriString.equals(MediaStore.Images.Media.EXTERNAL_CONTENT_URI.toString())
                && !uriString.equals(MediaStore.Images.Media.INTERNAL_CONTENT_URI.toString());
        //return true;
    }

    public DiskCache getThumbnailCache() {
        return mDiskCache;
    }

    public void loadItemsForSet(MediaFeed feed, MediaSet parentSet, int rangeStart, int rangeEnd) 
    {
    	//Log.d(TAG, "inside loadItemsForSet()");
        if (parentSet.mNumItemsLoaded > 0 && mDone) {
            return;
        }
        //Log.i(TAG, "mSingleUri: " + mSingleUri);
        //Log.i(TAG, "mDone: " + mDone);
        //Log.i(TAG, "mFlattenAllItems: " + mFlattenAllItems);
        if (mSingleUri && !mDone)
        {
            MediaItem item = new MediaItem();
            item.mId = 0;
            item.mFilePath = "";
            item.setMediaType((isImage(mUri)) ? MediaItem.MEDIA_TYPE_IMAGE : MediaItem.MEDIA_TYPE_VIDEO);
            if (mUri.startsWith(MediaStore.Images.Media.EXTERNAL_CONTENT_URI.toString())
                || mUri.startsWith(MediaStore.Video.Media.EXTERNAL_CONTENT_URI.toString())) 
            {
            	Log.i(TAG, "A.if Run first");
                MediaItem newItem = createMediaItemFromUri(mContext, Uri.parse(mUri), item.getMediaType());
                if (newItem != null) 
                {
                    item = newItem;
                    String fileUri = new File(item.mFilePath).toURI().toString();
                    parentSet.mName = Utils.getBucketNameFromUri(mContext.getContentResolver(), Uri.parse(fileUri));
                    parentSet.mId = Utils.getBucketIdFromUri(mContext.getContentResolver(), Uri.parse(fileUri));
                    parentSet.generateTitle(true);
                    feed.setSingleImageMode(true);
                }
            } 
            else if (mUri.startsWith("file://")) 
            {
            	Log.i(TAG, "A.mUri startswith file://");
                MediaItem newItem = null;
                int numRetries = 15;
                do 
                {
                    newItem = createMediaItemFromFileUri(mContext, mUri);
                    if (newItem == null) {
                        --numRetries;
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            ;
                        }
                    }
                } while (newItem == null && numRetries >= 0);
                if (newItem != null) 
                {
                    item = newItem;
                    feed.setSingleImageMode(true);
                } 
                else 
                {
                    item.mContentUri = mUri;
                    item.mThumbnailUri = mUri;
                    item.mScreennailUri = mUri;
                    feed.setSingleImageMode(true);
                }
            } 
            else 
            {
            	Log.i(TAG, "A. else");
                item.mContentUri = mUri;
                item.mThumbnailUri = mUri;
                item.mScreennailUri = mUri;
                feed.setSingleImageMode(true);
            }
            if (item != null) 
            {
            	Log.i(TAG, "B. item != null");
            	
                feed.addItemToMediaSet(item, parentSet);
                // Parse EXIF orientation if a local file.
                if (mUri.startsWith("file://")) {
                    try {
                        ExifInterface exif = new ExifInterface(Uri.parse(mUri).getPath());
                        item.mRotation = Shared.exifOrientationToDegrees(exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                                ExifInterface.ORIENTATION_NORMAL));
                    } catch (IOException e) {
                        Log.i(TAG, "Error reading Exif information, probably not a jpeg.");
                    }
                }
                // Try and get the date taken for this item.
                long dateTaken = CacheService.fetchDateTaken(item);
                if (dateTaken != -1L) {
                    item.mDateTakenInMs = dateTaken;
                }
                CacheService.loadMediaItemsIntoMediaFeed(mContext, feed, parentSet, rangeStart, rangeEnd, mIncludeImages, mIncludeVideos);
                ArrayList<MediaItem> items = parentSet.getItems();
                int numItems = items.size();
                if (numItems == 1 && parentSet.mNumItemsLoaded > 1) {
                    parentSet.mNumItemsLoaded = 1;
                }
                parentSet.removeDuplicate(item);
            }
            parentSet.updateNumExpectedItems();
            parentSet.generateTitle(true);
        } else if (mUri.equals(MediaStore.Images.Media.EXTERNAL_CONTENT_URI.toString()) & mFlattenAllItems) {
            final Uri uriImages = Images.Media.EXTERNAL_CONTENT_URI;
            final ContentResolver cr = mContext.getContentResolver();
            String where = null;
            try {
                Cursor cursor = cr.query(uriImages, CacheService.PROJECTION_IMAGES, where, null, null);
                if (cursor != null && cursor.moveToFirst()) {
                    parentSet.setNumExpectedItems(cursor.getCount());
                    do {
                        if (Thread.interrupted()) {
                            return;
                        }
                        final MediaItem item = new MediaItem();
                        CacheService.populateMediaItemFromCursor(item, cr, cursor, CacheService.BASE_CONTENT_STRING_IMAGES);
                        feed.addItemToMediaSet(item, parentSet);
                    } while (cursor.moveToNext());
                    if (cursor != null) {
                        cursor.close();
                        cursor = null;
                    }
                    parentSet.updateNumExpectedItems();
                    parentSet.generateTitle(true);
                }
            } catch (Exception e) {
                // If the database operation failed for any reason.
                ;
            }
        } else {
            CacheService.loadMediaItemsIntoMediaFeed(mContext, feed, parentSet, rangeStart, rangeEnd, mIncludeImages, mIncludeVideos);
        }
        Log.i(TAG, "ParentSet: " + parentSet.getNumItems());
        mDone = true;
    }

    private static boolean isImage(String uriString) {
        return !uriString.startsWith(MediaStore.Video.Media.EXTERNAL_CONTENT_URI.toString());
    }

    public void loadMediaSets(final MediaFeed feed) 
    {
        MediaSet set = null; // Dummy set.
        boolean loadOtherSets = true;
        Log.d("thong", "mSingleUri: " + mSingleUri);
        if (mSingleUri) {
            String name = Utils.getBucketNameFromUri(mContext.getContentResolver(), Uri.parse(mUri));
            long id = Utils.getBucketIdFromUri(mContext.getContentResolver(), Uri.parse(mUri));
            set = feed.addMediaSet(id, this);
            set.mName = name;
            set.mId = id;
            set.setNumExpectedItems(2);
            set.generateTitle(true);
            set.mPicasaAlbumId = Shared.INVALID;
            if (this.getThumbnailCache() != sThumbnailCache) 
            {
            	loadOtherSets = false;
            }
        } 
        else if (mBucketId == null) 
        {
            // All the buckets.
            if (mFlattenAllItems) 
            {
                set = feed.addMediaSet(0, this); // Create dummy set.
                set.mName = Utils.getBucketNameFromUri(mContext.getContentResolver(), Uri.parse(mUri));
                set.mId = getBucketId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI.toString() + "/" + set.mName);
                set.setNumExpectedItems(1);
                set.generateTitle(true);
                set.mPicasaAlbumId = Shared.INVALID;
            } 
            else 
            {
            	Log.d("thong", "LocalDataSource - loadMediaSets: 292");
                CacheService.loadMediaSets(mContext, feed, this, mIncludeImages, mIncludeVideos, true);
            }
        } 
        else 
        {
        	Log.d("thong", "LocalDataSource - loadMediaSets: 298");
            CacheService.loadMediaSet(mContext, feed, this, Long.parseLong(mBucketId));
            ArrayList<MediaSet> sets = feed.getMediaSets();
            if (sets.size() > 0)
                set = sets.get(0);
        }
        //TODO: Thong - Don't load other photos
        loadOtherSets = false;
        //TODO: Thong - Don't load other photos - Done
        // We also load the other MediaSets
        if (!mAllItems && set != null && loadOtherSets) 
        {
            final long setId = set.mId;
            if (!CacheService.isPresentInCache(setId)) 
            {
                CacheService.markDirty();
            }
            Log.d("thong", "LocalDataSource - loadMediaSets: 312");
            CacheService.loadMediaSets(mContext, feed, this, mIncludeImages, mIncludeVideos, false);

            // not re-ordering media sets in the case of displaying a single image
            if (!mSingleUri) {
                feed.moveSetToFront(set);
            }
        }
    }

    public boolean performOperation(int operation, ArrayList<MediaBucket> mediaBuckets, Object data) {
        int numBuckets = mediaBuckets.size();
        ContentResolver cr = mContext.getContentResolver();
        switch (operation) {
        case MediaFeed.OPERATION_DELETE:
            for (int i = 0; i < numBuckets; ++i) {
                MediaBucket bucket = mediaBuckets.get(i);
                MediaSet set = bucket.mediaSet;
                ArrayList<MediaItem> items = bucket.mediaItems;
                if (set != null && items == null) {
                    // TODO bulk delete
                    // remove the entire bucket
                    final Uri uriImages = Images.Media.EXTERNAL_CONTENT_URI;
                    final Uri uriVideos = Video.Media.EXTERNAL_CONTENT_URI;
                    final String whereImages = Images.ImageColumns.BUCKET_ID + "=" + Long.toString(set.mId);
                    final String whereVideos = Video.VideoColumns.BUCKET_ID + "=" + Long.toString(set.mId);
                    cr.delete(uriImages, whereImages, null);
                    cr.delete(uriVideos, whereVideos, null);
                    //CacheService.markDirty();
                }
                if (set != null && items != null) {
                    // We need to remove these items from the set.
                    int numItems = items.size();
                    try {
                        for (int j = 0; j < numItems; ++j) {
                            MediaItem item = items.get(j);
                            cr.delete(Uri.parse(item.mContentUri), null, null);
                        }
                    } catch (Exception e) {
                        // If the database operation failed for any reason.
                        ;
                    }
                    set.updateNumExpectedItems();
                    set.generateTitle(true);
                    //CacheService.markDirty(set.mId);
                }
            }
            break;
        case MediaFeed.OPERATION_ROTATE:
            for (int i = 0; i < numBuckets; ++i) {
                MediaBucket bucket = mediaBuckets.get(i);
                ArrayList<MediaItem> items = bucket.mediaItems;
                if (items == null) {
                    continue;
                }
                float angleToRotate = ((Float) data).floatValue();
                if (angleToRotate == 0) {
                    return true;
                }
                int numItems = items.size();
                for (int j = 0; j < numItems; ++j) {
                    rotateItem(items.get(j), angleToRotate);
                }
            }
            break;
        }
        return true;
    }

    private void rotateItem(final MediaItem item, float angleToRotate) {
        ContentResolver cr = mContext.getContentResolver();
        try {
            int currentOrientation = (int) item.mRotation;
            angleToRotate += currentOrientation;
            float rotation = Shared.normalizePositive(angleToRotate);
            String rotationString = Integer.toString((int) rotation);

            // Update the database entry.
            ContentValues values = new ContentValues();
            values.put(Images.ImageColumns.ORIENTATION, rotationString);
            try {
                cr.update(Uri.parse(item.mContentUri), values, null, null);
            } catch (Exception e) {
                // If the database operation fails for any reason.
                ;
            }

            // Update the file EXIF information.
            Uri uri = Uri.parse(item.mContentUri);
            String uriScheme = uri.getScheme();
            if (uriScheme.equals("file") || uriScheme.equals("content")) {
                final String path = (uriScheme.equals("file")) ? uri.getPath() : item.mFilePath;
                ExifInterface exif = new ExifInterface(path);
                exif.setAttribute(ExifInterface.TAG_ORIENTATION, Integer.toString(Shared.degreesToExifOrientation(rotation)));
                exif.saveAttributes();
            }

            // Invalidate the cache entry.
            CacheService.markDirty(item.mParentMediaSet.mId);

            // Update the object representation of the item.
            item.mRotation = rotation;
        } catch (Exception e) {
            // System.out.println("Apparently not a JPEG");
        }
    }
    
    public static MediaItem createMediaItemFromUri(Context context, Uri target, int mediaType) {
        MediaItem item = null;
        long id = ContentUris.parseId(target);
        ContentResolver cr = context.getContentResolver();
        String whereClause = Images.ImageColumns._ID + "=" + Long.toString(id);
        try {
            final Uri uri = (mediaType == MediaItem.MEDIA_TYPE_IMAGE)
                    ? Images.Media.EXTERNAL_CONTENT_URI
                    : Video.Media.EXTERNAL_CONTENT_URI;
            final String[] projection = (mediaType == MediaItem.MEDIA_TYPE_IMAGE)
                    ? CacheService.PROJECTION_IMAGES
                    : CacheService.PROJECTION_VIDEOS;
            Cursor cursor = cr.query(uri, projection, whereClause, null, null);
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    item = new MediaItem();
                    CacheService.populateMediaItemFromCursor(item, cr, cursor, uri.toString() + "/");
                }
                cursor.close();
                cursor = null;
            }
        } catch (Exception e) {
            // If the database operation failed for any reason.
            ;
        }
        item.mId = id;
        return item;
    }

    public static MediaItem createMediaItemFromFileUri(Context context, String fileUri) {
        MediaItem item = null;
        String filepath = new File(URI.create(fileUri)).toString();
        ContentResolver cr = context.getContentResolver();
        long bucketId = Utils.getBucketIdFromUri(context.getContentResolver(), Uri.parse(fileUri));
        String whereClause = Images.ImageColumns.BUCKET_ID + "=" + bucketId + " AND " + Images.ImageColumns.DATA + "='" + filepath
                + "'";
        try {
            Cursor cursor = cr.query(Images.Media.EXTERNAL_CONTENT_URI, CacheService.PROJECTION_IMAGES, whereClause, null, null);
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    item = new MediaItem();
                    CacheService.populateMediaItemFromCursor(item, cr, cursor, Images.Media.EXTERNAL_CONTENT_URI.toString() + "/");
                }
                cursor.close();
                cursor = null;
            }
        } catch (Exception e) {
            // If the database operation failed for any reason.
            ;
        }
        return item;
    }
    
    public String[] getDatabaseUris() {
        return new String[] {Images.Media.EXTERNAL_CONTENT_URI.toString(), Video.Media.EXTERNAL_CONTENT_URI.toString()};
    }

    public void refresh(final MediaFeed feed, final String[] databaseUris) {
        // We check to see what has changed.
        long[] ids = CacheService.computeDirtySets(mContext);
        int numDirtySets = ids.length;
        for (int i = 0; i < numDirtySets; ++i) {
            long setId = ids[i];
            if (feed.getMediaSet(setId) != null) {
                MediaSet newSet = feed.replaceMediaSet(setId, this);
                newSet.generateTitle(true);
            } else {
                MediaSet mediaSet = feed.addMediaSet(setId, this);
                if (setId == CAMERA_BUCKET_ID) {
                    mediaSet.mName = CAMERA_STRING;
                } else if (setId == DOWNLOAD_BUCKET_ID) {
                    mediaSet.mName = DOWNLOAD_STRING;
                }
                mediaSet.generateTitle(true);
            }
        }
    }

}
