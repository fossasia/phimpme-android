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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.LongBuffer;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicReference;

import vn.mbm.phimp.me.gallery3d.app.App;
import vn.mbm.phimp.me.gallery3d.app.Res;
import vn.mbm.phimp.me.gallery3d.media.DataSource;
import vn.mbm.phimp.me.gallery3d.media.DiskCache;
import vn.mbm.phimp.me.gallery3d.media.Gallery;
import vn.mbm.phimp.me.gallery3d.media.LocalDataSource;
import vn.mbm.phimp.me.gallery3d.media.LongSparseArray;
import vn.mbm.phimp.me.gallery3d.media.MediaFeed;
import vn.mbm.phimp.me.gallery3d.media.MediaItem;
import vn.mbm.phimp.me.gallery3d.media.MediaSet;
import vn.mbm.phimp.me.gallery3d.media.Shared;
import vn.mbm.phimp.me.gallery3d.media.SortCursor;
import vn.mbm.phimp.me.gallery3d.media.Utils;
import android.app.IntentService;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.MergeCursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.os.Process;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.provider.MediaStore.Video;
import android.util.Log;
import android.widget.Toast;

public final class CacheService extends IntentService {
    public static final String ACTION_CACHE = "vn.mbm.phimp.me.gallery3d.cache.action.CACHE";
    public static final DiskCache sAlbumCache = new DiskCache("local-album-cache");
    public static final DiskCache sMetaAlbumCache = new DiskCache("local-meta-cache");
    public static final DiskCache sSkipThumbnailIds = new DiskCache("local-skip-cache");

    private static final String TAG = "CacheService";
    private static ImageList sList = null;
    private static final boolean DEBUG = true;

    // Wait 2 seconds to start the thumbnailer so that the application can load
    // without any overheads.
    private static final int THUMBNAILER_WAIT_IN_MS = 2000;
    private static final int DEFAULT_THUMBNAIL_WIDTH = 128;
    private static final int DEFAULT_THUMBNAIL_HEIGHT = 96;

    public static final String DEFAULT_IMAGE_SORT_ORDER = Images.ImageColumns.DATE_TAKEN + " ASC";
    public static final String DEFAULT_VIDEO_SORT_ORDER = Video.VideoColumns.DATE_TAKEN + " ASC";
    public static final String DEFAULT_BUCKET_SORT_ORDER = "upper(" + Images.ImageColumns.BUCKET_DISPLAY_NAME + ") ASC";

    // Must preserve order between these indices and the order of the terms in
    // BUCKET_PROJECTION_IMAGES, BUCKET_PROJECTION_VIDEOS.
    // Not using SortedHashMap for efficieny reasons.
    public static final int BUCKET_ID_INDEX = 0;
    public static final int BUCKET_NAME_INDEX = 1;
    public static final String[] BUCKET_PROJECTION_IMAGES = new String[] { Images.ImageColumns.BUCKET_ID,
            Images.ImageColumns.BUCKET_DISPLAY_NAME };

    public static final String[] BUCKET_PROJECTION_VIDEOS = new String[] { Video.VideoColumns.BUCKET_ID,
            Video.VideoColumns.BUCKET_DISPLAY_NAME };

    // Must preserve order between these indices and the order of the terms in
    // THUMBNAIL_PROJECTION.
    public static final int THUMBNAIL_ID_INDEX = 0;
    public static final int THUMBNAIL_DATE_MODIFIED_INDEX = 1;
    public static final int THUMBNAIL_DATA_INDEX = 2;
    public static final int THUMBNAIL_ORIENTATION_INDEX = 3;
    public static final String[] THUMBNAIL_PROJECTION = new String[] { Images.ImageColumns._ID, Images.ImageColumns.DATE_ADDED,
            Images.ImageColumns.DATA, Images.ImageColumns.ORIENTATION };

    public static final String[] SENSE_PROJECTION = new String[] { Images.ImageColumns.BUCKET_ID,
            "MAX(" + Images.ImageColumns.DATE_ADDED + "), COUNT(*)" };

    // Must preserve order between these indices and the order of the terms in
    // INITIAL_PROJECTION_IMAGES and
    // INITIAL_PROJECTION_VIDEOS.
    public static final int MEDIA_ID_INDEX = 0;
    public static final int MEDIA_CAPTION_INDEX = 1;
    public static final int MEDIA_MIME_TYPE_INDEX = 2;
    public static final int MEDIA_LATITUDE_INDEX = 3;
    public static final int MEDIA_LONGITUDE_INDEX = 4;
    public static final int MEDIA_DATE_TAKEN_INDEX = 5;
    public static final int MEDIA_DATE_ADDED_INDEX = 6;
    public static final int MEDIA_DATE_MODIFIED_INDEX = 7;
    public static final int MEDIA_DATA_INDEX = 8;
    public static final int MEDIA_ORIENTATION_OR_DURATION_INDEX = 9;
    public static final int MEDIA_BUCKET_ID_INDEX = 10;
    public static final String[] PROJECTION_IMAGES = new String[] { Images.ImageColumns._ID, Images.ImageColumns.TITLE,
            Images.ImageColumns.MIME_TYPE, Images.ImageColumns.LATITUDE, Images.ImageColumns.LONGITUDE,
            Images.ImageColumns.DATE_TAKEN, Images.ImageColumns.DATE_ADDED, Images.ImageColumns.DATE_MODIFIED,
            Images.ImageColumns.DATA, Images.ImageColumns.ORIENTATION, Images.ImageColumns.BUCKET_ID };

    public static final String[] PROJECTION_VIDEOS = new String[] { Video.VideoColumns._ID, Video.VideoColumns.TITLE,
            Video.VideoColumns.MIME_TYPE, Video.VideoColumns.LATITUDE, Video.VideoColumns.LONGITUDE, Video.VideoColumns.DATE_TAKEN,
            Video.VideoColumns.DATE_ADDED, Video.VideoColumns.DATE_MODIFIED, Video.VideoColumns.DATA, Video.VideoColumns.DURATION,
            Video.VideoColumns.BUCKET_ID };

    public static final String BASE_CONTENT_STRING_IMAGES = (Images.Media.EXTERNAL_CONTENT_URI).toString() + "/";
    public static final String BASE_CONTENT_STRING_VIDEOS = (Video.Media.EXTERNAL_CONTENT_URI).toString() + "/";
    private static final AtomicReference<Thread> THUMBNAIL_THREAD = new AtomicReference<Thread>();

    // Special indices in the Albumcache.
    private static final int ALBUM_CACHE_METADATA_INDEX = -1;
    private static final int ALBUM_CACHE_DIRTY_INDEX = -2;
    private static final int ALBUM_CACHE_DIRTY_BUCKET_INDEX = -4;
    private static final int ALBUM_CACHE_LOCALE_INDEX = -5;

    private static final DateFormat mDateFormat = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss");
    private static final DateFormat mAltDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    private static final byte[] sDummyData = new byte[] { 1 };
    private static final Object sCacheLock = new Object();

    public static final String getCachePath(final String subFolderName) {
        return Environment.getExternalStorageDirectory() + "/Android/data/vn.mbm.phimp.me.gallery3d.media/cache/" + subFolderName;
    }

    public static final void startCache(final Context context, final boolean checkthumbnails) {
        final Locale locale = getLocaleForAlbumCache();
        final Locale defaultLocale = Locale.getDefault();
        if (locale == null || !locale.equals(defaultLocale)) {
            sAlbumCache.deleteAll();
            putLocaleForAlbumCache(defaultLocale);
        }
        final Intent intent = new Intent(ACTION_CACHE, null, context, CacheService.class);
        intent.putExtra("checkthumbnails", checkthumbnails);
        context.startService(intent);
    }

    public static final boolean isCacheReady(final boolean onlyMediaSets) {
        if (onlyMediaSets) {
            return (sAlbumCache.get(ALBUM_CACHE_METADATA_INDEX, 0) != null && sAlbumCache.get(ALBUM_CACHE_DIRTY_INDEX, 0) == null);
        } else {
            return (sAlbumCache.get(ALBUM_CACHE_METADATA_INDEX, 0) != null && sAlbumCache.get(ALBUM_CACHE_DIRTY_INDEX, 0) == null && sAlbumCache
                    .get(ALBUM_CACHE_DIRTY_BUCKET_INDEX, 0) == null);
        }
    }

    public static final boolean isPresentInCache(final long setId) {
        return sAlbumCache.get(setId, 0) != null;
    }

    public static final void markDirty() {
        sList = null;
        synchronized (sCacheLock) {
            sAlbumCache.put(ALBUM_CACHE_DIRTY_INDEX, sDummyData, 0);
        }
    }

    public static final void markDirty(final long id) {
        if (id == Shared.INVALID) {
            return;
        }
        sList = null;
        synchronized (sCacheLock) {
            byte[] data = longToByteArray(id);
            final byte[] existingData = sAlbumCache.get(ALBUM_CACHE_DIRTY_BUCKET_INDEX, 0);
            if (existingData != null && existingData.length > 0) {
                final long[] ids = toLongArray(existingData);
                final int numIds = ids.length;
                for (int i = 0; i < numIds; ++i) {
                    if (ids[i] == id) {
                        return;
                    }
                }
                // Add this to the existing keys and concatenate the byte
                // arrays.
                data = concat(data, existingData);
            }
            sAlbumCache.put(ALBUM_CACHE_DIRTY_BUCKET_INDEX, data, 0);
        }
    }

    public static final boolean setHasItems(final ContentResolver cr, final long setId) {
        final Uri uriImages = Images.Media.EXTERNAL_CONTENT_URI;
        final Uri uriVideos = Video.Media.EXTERNAL_CONTENT_URI;
        final StringBuffer whereString = new StringBuffer(Images.ImageColumns.BUCKET_ID + "=" + setId);
        try {
            final Cursor cursorImages = cr.query(uriImages, BUCKET_PROJECTION_IMAGES, whereString.toString(), null, null);
            if (cursorImages != null && cursorImages.getCount() > 0) {
                cursorImages.close();
                return true;
            }
            final Cursor cursorVideos = cr.query(uriVideos, BUCKET_PROJECTION_VIDEOS, whereString.toString(), null, null);
            if (cursorVideos != null && cursorVideos.getCount() > 0) {
                cursorVideos.close();
                return true;
            }
        } catch (Exception e) {
            // If the database query failed for any reason
            ;
        }
        return false;
    }

    public static final void loadMediaSets(final Context context, final MediaFeed feed, final DataSource source,
            final boolean includeImages, final boolean includeVideos, final boolean moveCameraToFront) {
        // We check to see if the Cache is ready.
    	Log.d("thong", "loadMediaSets - 237");
        syncCache(context);
        final byte[] albumData = sAlbumCache.get(ALBUM_CACHE_METADATA_INDEX, 0);
        if (albumData != null && albumData.length > 0) {
            final DataInputStream dis = new DataInputStream(new BufferedInputStream(new ByteArrayInputStream(albumData), 256));
            try {
                final int numAlbums = dis.readInt();
                for (int i = 0; i < numAlbums; ++i) {
                    final long setId = dis.readLong();
                    final String name = Utils.readUTF(dis);
                    final boolean hasImages = dis.readBoolean();
                    final boolean hasVideos = dis.readBoolean();
                    MediaSet mediaSet = feed.getMediaSet(setId);
                    if (mediaSet == null) {
                        mediaSet = feed.addMediaSet(setId, source);
                    } else {
                        mediaSet.refresh();
                    }
                    if (moveCameraToFront && mediaSet.mId == LocalDataSource.CAMERA_BUCKET_ID) {
                        feed.moveSetToFront(mediaSet);
                    }
                    if ((includeImages && hasImages) || (includeVideos && hasVideos)) {
                        mediaSet.mName = name;
                        mediaSet.mHasImages = hasImages;
                        mediaSet.mHasVideos = hasVideos;
                        mediaSet.mPicasaAlbumId = Shared.INVALID;
                        mediaSet.generateTitle(true);
                    }
                }
            } catch (IOException e) {
                Log.e(TAG, "Error loading albums.");
                sAlbumCache.deleteAll();
                putLocaleForAlbumCache(Locale.getDefault());
            }
        } else {
            if (DEBUG)
                Log.d(TAG, "No albums found.");
        }
    }

    public static final void loadMediaSet(final Context context, final MediaFeed feed, final DataSource source, final long bucketId) {
    	//Log.d("thong", "loadMediaSets - 277");
        syncCache(context);
        final byte[] albumData = sAlbumCache.get(ALBUM_CACHE_METADATA_INDEX, 0);
        if (albumData != null && albumData.length > 0) {
            DataInputStream dis = new DataInputStream(new BufferedInputStream(new ByteArrayInputStream(albumData), 256));
            try {
                final int numAlbums = dis.readInt();
                for (int i = 0; i < numAlbums; ++i) {
                    final long setId = dis.readLong();
                    MediaSet mediaSet = null;
                    if (setId == bucketId) {
                        mediaSet = feed.getMediaSet(setId);
                        if (mediaSet == null) {
                            mediaSet = feed.addMediaSet(setId, source);
                        }
                    } else {
                        mediaSet = new MediaSet();
                    }
                    mediaSet.mName = Utils.readUTF(dis);
                    if (setId == bucketId) {
                        mediaSet.mPicasaAlbumId = Shared.INVALID;
                        mediaSet.generateTitle(true);
                        return;
                    }
                }
            } catch (IOException e) {
                Log.e(TAG, "Error finding album " + bucketId);
                sAlbumCache.deleteAll();
                putLocaleForAlbumCache(Locale.getDefault());
            }
        } else {
            if (DEBUG)
                Log.d(TAG, "No album found for album id " + bucketId);
        }
    }

    public static final void loadMediaItemsIntoMediaFeed(final Context context, final MediaFeed feed, final MediaSet set,
            final int rangeStart, final int rangeEnd, final boolean includeImages, final boolean includeVideos) {
    	Log.d("thong", "loadMediaSets - 314");
        syncCache(context);
        byte[] albumData = sAlbumCache.get(set.mId, 0);
        if (albumData != null && set.mNumItemsLoaded < set.getNumExpectedItems()) {
            final DataInputStream dis = new DataInputStream(new BufferedInputStream(new ByteArrayInputStream(albumData), 256));
            try {
                final int numItems = dis.readInt();
                set.setNumExpectedItems(numItems);
                set.mMinTimestamp = dis.readLong();
                set.mMaxTimestamp = dis.readLong();
                MediaItem reuseItem = null;
                for (int i = 0; i < numItems; ++i) {
                    MediaItem item = (reuseItem == null) ? new MediaItem() : reuseItem;
                    // Must preserve order with method that writes to cache.
                    item.mId = dis.readLong();
                    item.mCaption = Utils.readUTF(dis);
                    item.mMimeType = Utils.readUTF(dis);
                    item.setMediaType(dis.readInt());
                    item.mLatitude = dis.readDouble();
                    item.mLongitude = dis.readDouble();
                    item.mDateTakenInMs = dis.readLong();
                    item.mTriedRetrievingExifDateTaken = dis.readBoolean();
                    item.mDateAddedInSec = dis.readLong();
                    item.mDateModifiedInSec = dis.readLong();
                    item.mDurationInSec = dis.readInt();
                    item.mRotation = (float) dis.readInt();
                    item.mFilePath = Utils.readUTF(dis);

                    // We are done reading. Now lets check to see if this item
                    // is already present in the set.
                    boolean setLookupContainsItem = set.lookupContainsItem(item);
                    if (setLookupContainsItem) {
                        reuseItem = item;
                    } else {
                        reuseItem = null;
                    }
                    int itemMediaType = item.getMediaType();
                    if ((itemMediaType == MediaItem.MEDIA_TYPE_IMAGE && includeImages)
                            || (itemMediaType == MediaItem.MEDIA_TYPE_VIDEO && includeVideos)) {
                        String baseUri = (itemMediaType == MediaItem.MEDIA_TYPE_IMAGE) ? BASE_CONTENT_STRING_IMAGES
                                : BASE_CONTENT_STRING_VIDEOS;
                        item.mContentUri = baseUri + item.mId;
                        feed.addItemToMediaSet(item, set);
                    }
                }
                set.checkForDeletedItems();
                dis.close();
            } catch (IOException e) {
                Log.e(TAG, "Error loading items for album " + set.mName);
                sAlbumCache.deleteAll();
                putLocaleForAlbumCache(Locale.getDefault());
            }
        } else {
            if (DEBUG)
                Log.d(TAG, "No items found for album " + set.mName);
        }
        set.updateNumExpectedItems();
        set.generateTitle(true);
    }

    @SuppressWarnings("static-access")
	private static void syncCache(Context context) 
    {
        if (!isCacheReady(true)) {
            // In this case, we should try to show a toast
            if (context instanceof Gallery) 
            {
                App.get(context).showToast(context.getResources().getString(Res.string.loading_new), Toast.LENGTH_LONG);
            }
            if (DEBUG)
                Log.d(TAG, "Refreshing Cache for all items");
            refresh(context);
            sAlbumCache.delete(ALBUM_CACHE_DIRTY_INDEX);
            sAlbumCache.delete(ALBUM_CACHE_DIRTY_BUCKET_INDEX);
        } 
        else if (!isCacheReady(false)) 
        {
            if (DEBUG)
                Log.d(TAG, "Refreshing Cache for dirty items");
            refreshDirtySets(context);
            sAlbumCache.delete(ALBUM_CACHE_DIRTY_BUCKET_INDEX);
        }
    }

    public static final void populateVideoItemFromCursor(final MediaItem item, final ContentResolver cr, final Cursor cursor,
            final String baseUri) {
        item.setMediaType(MediaItem.MEDIA_TYPE_VIDEO);
        populateMediaItemFromCursor(item, cr, cursor, baseUri);
    }

    public static final void populateMediaItemFromCursor(final MediaItem item, final ContentResolver cr, final Cursor cursor,
            final String baseUri) {
        item.mId = cursor.getLong(CacheService.MEDIA_ID_INDEX);
        item.mCaption = cursor.getString(CacheService.MEDIA_CAPTION_INDEX);
        item.mMimeType = cursor.getString(CacheService.MEDIA_MIME_TYPE_INDEX);
        item.mLatitude = cursor.getDouble(CacheService.MEDIA_LATITUDE_INDEX);
        item.mLongitude = cursor.getDouble(CacheService.MEDIA_LONGITUDE_INDEX);
        item.mDateTakenInMs = cursor.getLong(CacheService.MEDIA_DATE_TAKEN_INDEX);
        item.mDateAddedInSec = cursor.getLong(CacheService.MEDIA_DATE_ADDED_INDEX);
        item.mDateModifiedInSec = cursor.getLong(CacheService.MEDIA_DATE_MODIFIED_INDEX);
        if (item.mDateTakenInMs == item.mDateModifiedInSec) {
            item.mDateTakenInMs = item.mDateModifiedInSec * 1000;
        }
        item.mFilePath = cursor.getString(CacheService.MEDIA_DATA_INDEX);
        if (baseUri != null)
            item.mContentUri = baseUri + item.mId;
        final int itemMediaType = item.getMediaType();
        final int orientationDurationValue = cursor.getInt(CacheService.MEDIA_ORIENTATION_OR_DURATION_INDEX);
        if (itemMediaType == MediaItem.MEDIA_TYPE_IMAGE) {
            item.mRotation = orientationDurationValue;
        } else {
            item.mDurationInSec = orientationDurationValue;
        }
    }

    // Returns -1 if we failed to examine EXIF information or EXIF parsing
    // failed.
    public static final long fetchDateTaken(final MediaItem item) {
        if (!item.isDateTakenValid() && !item.mTriedRetrievingExifDateTaken
                && (item.mFilePath.endsWith(".jpg") || item.mFilePath.endsWith(".jpeg"))) {
            try {
                if (DEBUG)
                    Log.i(TAG, "Parsing date taken from exif");
                final ExifInterface exif = new ExifInterface(item.mFilePath);
                final String dateTakenStr = exif.getAttribute(ExifInterface.TAG_DATETIME);
                if (dateTakenStr != null) {
                    try {
                        final Date dateTaken = mDateFormat.parse(dateTakenStr);
                        return dateTaken.getTime();
                    } catch (ParseException pe) {
                        try {
                            final Date dateTaken = mAltDateFormat.parse(dateTakenStr);
                            return dateTaken.getTime();
                        } catch (ParseException pe2) {
                            if (DEBUG)
                                Log.i(TAG, "Unable to parse date out of string - " + dateTakenStr);
                        }
                    }
                }
            } catch (Exception e) {
                if (DEBUG)
                    Log.i(TAG, "Error reading Exif information, probably not a jpeg.");
            }

            // Ensures that we only try retrieving EXIF date taken once.
            item.mTriedRetrievingExifDateTaken = true;
        }
        return -1L;
    }

    public static final byte[] queryThumbnail(final Context context, final long thumbId, final long origId, final boolean isVideo,
            final long timestamp) {
        final DiskCache thumbnailCache = (isVideo) ? LocalDataSource.sThumbnailCacheVideo : LocalDataSource.sThumbnailCache;
        return queryThumbnail(context, thumbId, origId, isVideo, thumbnailCache, timestamp);
    }

    public static final ImageList getImageList(final Context context) {
        if (sList != null)
            return sList;
        ImageList list = new ImageList();
        final Uri uriImages = Images.Media.EXTERNAL_CONTENT_URI;
        final ContentResolver cr = context.getContentResolver();
        try {
            final Cursor cursorImages = cr.query(uriImages, THUMBNAIL_PROJECTION, null, null, null);
            if (cursorImages != null && cursorImages.moveToFirst()) {
                final int size = cursorImages.getCount();
                final long[] ids = new long[size];
                final long[] thumbnailIds = new long[size];
                final long[] timestamp = new long[size];
                final int[] orientation = new int[size];
                int ctr = 0;
                do {
                    if (Thread.interrupted()) {
                        break;
                    }
                    ids[ctr] = cursorImages.getLong(THUMBNAIL_ID_INDEX);
                    timestamp[ctr] = cursorImages.getLong(THUMBNAIL_DATE_MODIFIED_INDEX);
                    thumbnailIds[ctr] = Utils.Crc64Long(cursorImages.getString(THUMBNAIL_DATA_INDEX));
                    orientation[ctr] = cursorImages.getInt(THUMBNAIL_ORIENTATION_INDEX);
                    ++ctr;
                } while (cursorImages.moveToNext());
                cursorImages.close();
                list.ids = ids;
                list.thumbids = thumbnailIds;
                list.timestamp = timestamp;
                list.orientation = orientation;
            }
        } catch (Exception e) {
            // If the database operation failed for any reason
            ;
        }
        if (sList == null) {
            sList = list;
        }
        return list;
    }

    private static final byte[] queryThumbnail(final Context context, final long thumbId, final long origId, final boolean isVideo,
            final DiskCache thumbnailCache, final long timestamp) {
        if (!App.get(context).isPaused()) {
            final Thread thumbnailThread = THUMBNAIL_THREAD.getAndSet(null);
            if (thumbnailThread != null) {
                thumbnailThread.interrupt();
            }
        }
        byte[] bitmap = thumbnailCache.get(thumbId, timestamp);
        if (bitmap == null) {
            final long time = SystemClock.uptimeMillis();
            bitmap = buildThumbnailForId(context, thumbnailCache, thumbId, origId, isVideo, DEFAULT_THUMBNAIL_WIDTH,
                    DEFAULT_THUMBNAIL_HEIGHT, timestamp);
            if (DEBUG)
                Log.i(TAG, "Built thumbnail and screennail for " + origId + " in " + (SystemClock.uptimeMillis() - time));
        }
        return bitmap;
    }

    private static final void buildThumbnails(final Context context) {
        if (DEBUG)
            Log.i(TAG, "Preparing DiskCache for all thumbnails.");
        ImageList list = getImageList(context);
        final int size = (list.ids == null) ? 0 : list.ids.length;
        final long[] ids = list.ids;
        final long[] timestamp = list.timestamp;
        final long[] thumbnailIds = list.thumbids;
        final DiskCache thumbnailCache = LocalDataSource.sThumbnailCache;
        for (int i = 0; i < size; ++i) {
            if (Thread.interrupted()) {
                return;
            }
            final long id = ids[i];
            final long timeModifiedInSec = timestamp[i];
            final long thumbnailId = thumbnailIds[i];
            if (!isInThumbnailerSkipList(thumbnailId)) {
                if (!thumbnailCache.isDataAvailable(thumbnailId, timeModifiedInSec * 1000)) {
                    byte[] retVal = buildThumbnailForId(context, thumbnailCache, thumbnailId, id, false, DEFAULT_THUMBNAIL_WIDTH,
                            DEFAULT_THUMBNAIL_HEIGHT, timeModifiedInSec * 1000);
                    if (retVal == null || retVal.length == 0) {
                        // There was an error in building the thumbnail.
                        // We record this thumbnail id
                        addToThumbnailerSkipList(thumbnailId);
                    }
                }
            }
        }
        thumbnailCache.flush();
        if (DEBUG)
            Log.i(TAG, "DiskCache ready for all thumbnails.");
    }

    private static void addToThumbnailerSkipList(long thumbnailId) {
        sSkipThumbnailIds.put(thumbnailId, sDummyData, 0);
        sSkipThumbnailIds.flush();
    }

    private static boolean isInThumbnailerSkipList(long thumbnailId) {
        if (sSkipThumbnailIds != null && sSkipThumbnailIds.isDataAvailable(thumbnailId, 0)) {
            byte[] data = sSkipThumbnailIds.get(thumbnailId, 0);
            if (data != null && data.length > 0) {
                return true;
            }
        }
        return false;
    }

    private static final byte[] buildThumbnailForId(final Context context, final DiskCache thumbnailCache, final long thumbId,
            final long origId, final boolean isVideo, final int thumbnailWidth, final int thumbnailHeight, final long timestamp) {
        if (origId == Shared.INVALID) {
            return null;
        }
        try {
            Bitmap bitmap = null;
            Thread.sleep(1);
            new Thread() {
                public void run() {
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        ;
                    }
                    try {
                        if (isVideo) {
                            MediaStore.Video.Thumbnails.cancelThumbnailRequest(context.getContentResolver(), origId);
                        } else {
                            MediaStore.Images.Thumbnails.cancelThumbnailRequest(context.getContentResolver(), origId);
                        }
                    } catch (Exception e) {
                        ;
                    }
                }
            }.start();
            if (isVideo) {
                bitmap = MediaStore.Video.Thumbnails.getThumbnail(context.getContentResolver(), origId,
                        MediaStore.Video.Thumbnails.MINI_KIND, null);
            } else {
                bitmap = MediaStore.Images.Thumbnails.getThumbnail(context.getContentResolver(), origId,
                        MediaStore.Images.Thumbnails.MINI_KIND, null);
            }
            if (bitmap == null) {
                return null;
            }
            final byte[] retVal = writeBitmapToCache(thumbnailCache, thumbId, origId, bitmap, thumbnailWidth, thumbnailHeight,
                    timestamp);
            return retVal;
        } catch (InterruptedException e) {
            return null;
        }
    }

    public static final byte[] writeBitmapToCache(final DiskCache thumbnailCache, final long thumbId, final long origId,
            final Bitmap bitmap, final int thumbnailWidth, final int thumbnailHeight, final long timestamp) {
        final int width = bitmap.getWidth();
        final int height = bitmap.getHeight();
        // Detect faces to find the focal point, otherwise fall back to the
        // image center.
        int focusX = width / 2;
        int focusY = height / 2;
        // We have commented out face detection since it slows down the
        // generation of the thumbnail and screennail.

        // final FaceDetector faceDetector = new FaceDetector(width, height, 1);
        // final FaceDetector.Face[] faces = new FaceDetector.Face[1];
        // final int numFaces = faceDetector.findFaces(bitmap, faces);
        // if (numFaces > 0 && faces[0].confidence() >=
        // FaceDetector.Face.CONFIDENCE_THRESHOLD) {
        // final PointF midPoint = new PointF();
        // faces[0].getMidPoint(midPoint);
        // focusX = (int) midPoint.x;
        // focusY = (int) midPoint.y;
        // }

        // Crop to thumbnail aspect ratio biased towards the focus point.
        int cropX;
        int cropY;
        int cropWidth;
        int cropHeight;
        float scaleFactor;
        if (thumbnailWidth * height < thumbnailHeight * width) {
            // Vertically constrained.
            cropWidth = thumbnailWidth * height / thumbnailHeight;
            cropX = Math.max(0, Math.min(focusX - cropWidth / 2, width - cropWidth));
            cropY = 0;
            cropHeight = height;
            scaleFactor = (float) thumbnailHeight / height;
        } else {
            // Horizontally constrained.
            cropHeight = thumbnailHeight * width / thumbnailWidth;
            cropY = Math.max(0, Math.min(focusY - cropHeight / 2, height - cropHeight));
            cropX = 0;
            cropWidth = width;
            scaleFactor = (float) thumbnailWidth / width;
        }
        final Bitmap finalBitmap = Bitmap.createBitmap(thumbnailWidth, thumbnailHeight, Bitmap.Config.RGB_565);
        final Canvas canvas = new Canvas(finalBitmap);
        final Paint paint = new Paint();
        paint.setDither(true);
        paint.setFilterBitmap(true);
        canvas.drawColor(0);
        canvas.drawBitmap(bitmap, new Rect(cropX, cropY, cropX + cropWidth, cropY + cropHeight), new Rect(0, 0, thumbnailWidth,
                thumbnailHeight), paint);
        bitmap.recycle();

        // Store (long thumbnailId, short focusX, short focusY, JPEG data).
        final ByteArrayOutputStream cacheOutput = new ByteArrayOutputStream(16384);
        final DataOutputStream dataOutput = new DataOutputStream(cacheOutput);
        byte[] retVal = null;
        try {
            dataOutput.writeLong(origId);
            dataOutput.writeShort((int) ((focusX - cropX) * scaleFactor));
            dataOutput.writeShort((int) ((focusY - cropY) * scaleFactor));
            dataOutput.flush();
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 80, cacheOutput);
            retVal = cacheOutput.toByteArray();
            synchronized (thumbnailCache) {
                thumbnailCache.put(thumbId, retVal, timestamp);
            }
            cacheOutput.close();
            finalBitmap.recycle();
        } catch (Exception e) {
            ;
        }
        return retVal;
    }

    public CacheService() {
        super("CacheService");
    }

    @Override
    protected void onHandleIntent(final Intent intent) {
        if (DEBUG)
            Log.i(TAG, "Starting CacheService");
        if (Environment.getExternalStorageState() == Environment.MEDIA_BAD_REMOVAL) {
            sAlbumCache.deleteAll();
            putLocaleForAlbumCache(Locale.getDefault());
        }
        Locale locale = getLocaleForAlbumCache();
        if (locale != null && locale.equals(Locale.getDefault())) {

        } else {
            // The locale has changed, we need to regenerate the strings.
            markDirty();
        }
        if (intent.getBooleanExtra("checkthumbnails", false)) {
            startNewThumbnailThread(this);
        } else {
            final Thread existingThread = THUMBNAIL_THREAD.getAndSet(null);
            if (existingThread != null) {
                existingThread.interrupt();
            }
        }
    }

    private static final void putLocaleForAlbumCache(final Locale locale) {
        final ByteArrayOutputStream bos = new ByteArrayOutputStream();
        final DataOutputStream dos = new DataOutputStream(bos);
        try {
            Utils.writeUTF(dos, locale.getCountry());
            Utils.writeUTF(dos, locale.getLanguage());
            Utils.writeUTF(dos, locale.getVariant());
            dos.flush();
            bos.flush();
            final byte[] data = bos.toByteArray();
            sAlbumCache.put(ALBUM_CACHE_LOCALE_INDEX, data, 0);
            sAlbumCache.flush();
            dos.close();
            bos.close();
        } catch (IOException e) {
            // Could not write locale to cache.
            if (DEBUG)
                Log.i(TAG, "Error writing locale to cache.");
            ;
        }
    }

    private static final Locale getLocaleForAlbumCache() {
        final byte[] data = sAlbumCache.get(ALBUM_CACHE_LOCALE_INDEX, 0);
        if (data != null && data.length > 0) {
            ByteArrayInputStream bis = new ByteArrayInputStream(data);
            DataInputStream dis = new DataInputStream(bis);
            try {
                String country = Utils.readUTF(dis);
                if (country == null)
                    country = "";
                String language = Utils.readUTF(dis);
                if (language == null)
                    language = "";
                String variant = Utils.readUTF(dis);
                if (variant == null)
                    variant = "";
                final Locale locale = new Locale(language, country, variant);
                dis.close();
                bis.close();
                return locale;
            } catch (IOException e) {
                // Could not read locale in cache.
                if (DEBUG)
                    Log.i(TAG, "Error reading locale from cache.");
                return null;
            }
        }
        return null;
    }

    private static final void restartThread(final AtomicReference<Thread> threadRef, final String name, final Runnable action) {
        // Create a new thread.
        final Thread newThread = new Thread() {
            public void run() {
                try {
                    action.run();
                } finally {
                    threadRef.compareAndSet(this, null);
                }
            }
        };
        newThread.setName(name);
        newThread.start();

        // Interrupt any existing thread.
        final Thread existingThread = threadRef.getAndSet(newThread);
        if (existingThread != null) {
            existingThread.interrupt();
        }
    }

    public static final void startNewThumbnailThread(final Context context) {
        restartThread(THUMBNAIL_THREAD, "ThumbnailRefresh", new Runnable() {
            public void run() {
                Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
                try {
                    // It is an optimization to prevent the thumbnailer from
                    // running while the application loads
                    Thread.sleep(THUMBNAILER_WAIT_IN_MS);
                } catch (InterruptedException e) {
                    return;
                }
                CacheService.buildThumbnails(context);
            }
        });
    }

    private static final byte[] concat(final byte[] A, final byte[] B) {
        final byte[] C = (byte[]) new byte[A.length + B.length];
        System.arraycopy(A, 0, C, 0, A.length);
        System.arraycopy(B, 0, C, A.length, B.length);
        return C;
    }

    private static final long[] toLongArray(final byte[] data) {
        final ByteBuffer bBuffer = ByteBuffer.wrap(data);
        final LongBuffer lBuffer = bBuffer.asLongBuffer();
        final int numLongs = lBuffer.capacity();
        final long[] retVal = new long[numLongs];
        for (int i = 0; i < numLongs; ++i) {
            retVal[i] = lBuffer.get(i);
        }
        return retVal;
    }

    private static final byte[] longToByteArray(final long l) {
        final byte[] bArray = new byte[8];
        final ByteBuffer bBuffer = ByteBuffer.wrap(bArray);
        final LongBuffer lBuffer = bBuffer.asLongBuffer();
        lBuffer.put(0, l);
        return bArray;
    }

    private static final byte[] longArrayToByteArray(final long[] l) {
        final byte[] bArray = new byte[8 * l.length];
        final ByteBuffer bBuffer = ByteBuffer.wrap(bArray);
        final LongBuffer lBuffer = bBuffer.asLongBuffer();
        int numLongs = l.length;
        for (int i = 0; i < numLongs; ++i) {
            lBuffer.put(i, l[i]);
        }
        return bArray;
    }

    @SuppressWarnings("static-access")
	private final static void refresh(final Context context) {
        // First we build the album cache.
        // This is the meta-data about the albums / buckets on the SD card.
        if (DEBUG)
            Log.i(TAG, "Refreshing cache.");
        synchronized (sCacheLock) {
            sAlbumCache.deleteAll();
            putLocaleForAlbumCache(Locale.getDefault());

            final ArrayList<MediaSet> sets = new ArrayList<MediaSet>();
            LongSparseArray<MediaSet> acceleratedSets = new LongSparseArray<MediaSet>();
            if (DEBUG)
                Log.i(TAG, "Building albums.");
            final Uri uriImages = Images.Media.EXTERNAL_CONTENT_URI.buildUpon().appendQueryParameter("distinct", "true").build();
            final Uri uriVideos = Video.Media.EXTERNAL_CONTENT_URI.buildUpon().appendQueryParameter("distinct", "true").build();
            final ContentResolver cr = context.getContentResolver();
            try {
                final Cursor cursorImages = cr.query(uriImages, BUCKET_PROJECTION_IMAGES, null, null, DEFAULT_BUCKET_SORT_ORDER);
                final Cursor cursorVideos = cr.query(uriVideos, BUCKET_PROJECTION_VIDEOS, null, null, DEFAULT_BUCKET_SORT_ORDER);
                Cursor[] cursors = new Cursor[2];
                cursors[0] = cursorImages;
                cursors[1] = cursorVideos;
                final SortCursor sortCursor = new SortCursor(cursors, Images.ImageColumns.BUCKET_DISPLAY_NAME,
                        SortCursor.TYPE_STRING, true);
                try {
                    if (sortCursor != null && sortCursor.moveToFirst()) {
                        sets.ensureCapacity(sortCursor.getCount());
                        acceleratedSets = new LongSparseArray<MediaSet>(sortCursor.getCount());
                        MediaSet cameraSet = new MediaSet();
                        cameraSet.mId = LocalDataSource.CAMERA_BUCKET_ID;
                        cameraSet.mName = context.getResources().getString(Res.string.camera);
                        sets.add(cameraSet);
                        acceleratedSets.put(cameraSet.mId, cameraSet);
                        do {
                            if (Thread.interrupted()) {
                                return;
                            }
                            long setId = sortCursor.getLong(BUCKET_ID_INDEX);
                            MediaSet mediaSet = findSet(setId, acceleratedSets);
                            if (mediaSet == null) {
                                mediaSet = new MediaSet();
                                mediaSet.mId = setId;
                                mediaSet.mName = sortCursor.getString(BUCKET_NAME_INDEX);
                                sets.add(mediaSet);
                                acceleratedSets.put(setId, mediaSet);
                            }
                            mediaSet.mHasImages |= (sortCursor.getCurrentCursorIndex() == 0);
                            mediaSet.mHasVideos |= (sortCursor.getCurrentCursorIndex() == 1);
                        } while (sortCursor.moveToNext());
                        sortCursor.close();
                    }
                } finally {
                    if (sortCursor != null)
                        sortCursor.close();
                }
                writeSetsToCache(sets);
                if (DEBUG)
                    Log.i(TAG, "Done building albums.");
                // Now we must cache the items contained in every album /
                // bucket.
                populateMediaItemsForSets(context, sets, acceleratedSets, false);
            } catch (Exception e) {
                // If the database operation failed for any reason.
                ;
            }
        }
    }

    private final static void refreshDirtySets(final Context context) {
        synchronized (sCacheLock) {
            final byte[] existingData = sAlbumCache.get(ALBUM_CACHE_DIRTY_BUCKET_INDEX, 0);
            if (existingData != null && existingData.length > 0) {
                final long[] ids = toLongArray(existingData);
                final int numIds = ids.length;
                if (numIds > 0) {
                    final ArrayList<MediaSet> sets = new ArrayList<MediaSet>(numIds);
                    final LongSparseArray<MediaSet> acceleratedSets = new LongSparseArray<MediaSet>(numIds);
                    for (int i = 0; i < numIds; ++i) {
                        final MediaSet set = new MediaSet();
                        set.mId = ids[i];
                        sets.add(set);
                        acceleratedSets.put(set.mId, set);
                    }
                    if (DEBUG)
                        Log.i(TAG, "Refreshing dirty albums");
                    populateMediaItemsForSets(context, sets, acceleratedSets, true);
                }
            }
            sAlbumCache.delete(ALBUM_CACHE_DIRTY_BUCKET_INDEX);
        }
    }

    public static final long[] computeDirtySets(final Context context) {
        final Uri uriImages = Images.Media.EXTERNAL_CONTENT_URI;
        final Uri uriVideos = Video.Media.EXTERNAL_CONTENT_URI;
        final ContentResolver cr = context.getContentResolver();
        final String where = Images.ImageColumns.BUCKET_ID + "!=0) GROUP BY (" + Images.ImageColumns.BUCKET_ID + " ";
        ArrayList<Long> retVal = new ArrayList<Long>();
        try {
            final Cursor cursorImages = cr.query(uriImages, SENSE_PROJECTION, where, null, null);
            final Cursor cursorVideos = cr.query(uriVideos, SENSE_PROJECTION, where, null, null);
            Cursor[] cursors = new Cursor[2];
            cursors[0] = cursorImages;
            cursors[1] = cursorVideos;
            final MergeCursor cursor = new MergeCursor(cursors);
            final ArrayList<Long> setIds = new ArrayList<Long>();
            final ArrayList<Long> maxAdded = new ArrayList<Long>();
            final ArrayList<Integer> counts = new ArrayList<Integer>();
            try {
                if (cursor.moveToFirst()) {
                    do {
                        final long setId = cursor.getLong(0);
                        final long maxAdd = cursor.getLong(1);
                        final int count = cursor.getInt(2);
                        // We check to see if this id is already present.
                        if (setIds.contains(setId)) {
                            int index = setIds.indexOf(setId);
                            if (maxAdded.get(index) < maxAdd) {
                                maxAdded.set(index, maxAdd);
                            }
                            counts.set(index, counts.get(index) + count);
                        } else {
                            setIds.add(setId);
                            maxAdded.add(maxAdd);
                            counts.add(count);
                        }
                    } while (cursor.moveToNext());
                }
                final int numSets = setIds.size();
                int ctr = 0;
                if (numSets > 0) {
                    boolean allDirty = false;
                    do {
                        long setId = setIds.get(ctr);
                        if (allDirty) {
                            addNoDupe(retVal, setId);
                        } else {
                            boolean contains = sAlbumCache.isDataAvailable(setId, 0);
                            if (!contains) {
                                // We need to refresh everything.
                                markDirty();
                                addNoDupe(retVal, setId);
                                allDirty = true;
                            }
                            if (!allDirty) {
                                long maxAdd = maxAdded.get(ctr);
                                int count = counts.get(ctr);
                                byte[] data = sMetaAlbumCache.get(setId, 0);
                                long[] dataLong = new long[2];
                                if (data != null) {
                                    dataLong = toLongArray(data);
                                }
                                long oldMaxAdded = dataLong[0];
                                long oldCount = dataLong[1];
                                if (maxAdd > oldMaxAdded || oldCount != count) {
                                    markDirty(setId);
                                    addNoDupe(retVal, setId);
                                    dataLong[0] = maxAdd;
                                    dataLong[1] = count;
                                    sMetaAlbumCache.put(setId, longArrayToByteArray(dataLong), 0);
                                }
                            }
                        }
                        ++ctr;
                    } while (ctr < numSets);
                    // We now check for any deleted sets.
                    final byte[] albumData = sAlbumCache.get(ALBUM_CACHE_METADATA_INDEX, 0);
                    if (albumData != null && albumData.length > 0) {
                        final DataInputStream dis = new DataInputStream(new BufferedInputStream(new ByteArrayInputStream(albumData), 256));
                        try {
                            final int numAlbums = dis.readInt();
                            for (int i = 0; i < numAlbums; ++i) {
                                final long setId = dis.readLong();
                                Utils.readUTF(dis);
                                dis.readBoolean();
                                dis.readBoolean();
                                if (!setIds.contains(setId)) {
                                    // This set was deleted, we need to recompute the cache.
                                    markDirty();
                                    break;
                                }
                            }
                        } catch (Exception e) {
                            ;
                        }
                    }
                }
            } finally {
                cursor.close();
            }
            sMetaAlbumCache.flush();
        } catch (Exception e) {
            // If the database operation failed for any reason.
            ;
        }
        int numIds = retVal.size();
        long retValIds[] = new long[numIds];
        for (int i = 0; i < numIds; ++i) {
            retValIds[i] = retVal.get(i);
        }
        return retValIds;
    }

    private static final void addNoDupe(ArrayList<Long> array, long value) {
        int size = array.size();
        for (int i = 0; i < size; ++i) {
            if (array.get(i).longValue() == value)
                return;
        }
        array.add(value);
    }

    private final static void populateMediaItemsForSets(final Context context, final ArrayList<MediaSet> sets,
            final LongSparseArray<MediaSet> acceleratedSets, boolean useWhere) {
        if (sets == null || sets.size() == 0 || Thread.interrupted()) {
            return;
        }
        if (DEBUG)
            Log.i(TAG, "Building items.");
        final Uri uriImages = Images.Media.EXTERNAL_CONTENT_URI;
        final Uri uriVideos = Video.Media.EXTERNAL_CONTENT_URI;
        final ContentResolver cr = context.getContentResolver();

        String whereClause = null;
        if (useWhere) {
            int numSets = sets.size();
            StringBuffer whereString = new StringBuffer(Images.ImageColumns.BUCKET_ID + " in (");
            for (int i = 0; i < numSets; ++i) {
                whereString.append(sets.get(i).mId);
                if (i != numSets - 1) {
                    whereString.append(",");
                }
            }
            whereString.append(")");
            whereClause = whereString.toString();
            if (DEBUG)
                Log.i(TAG, "Updating dirty albums where " + whereClause);
        }
        try {
            final Cursor cursorImages = cr.query(uriImages, PROJECTION_IMAGES, whereClause, null, DEFAULT_IMAGE_SORT_ORDER);
            final Cursor cursorVideos = cr.query(uriVideos, PROJECTION_VIDEOS, whereClause, null, DEFAULT_VIDEO_SORT_ORDER);
            final Cursor[] cursors = new Cursor[2];
            cursors[0] = cursorImages;
            cursors[1] = cursorVideos;
            final SortCursor sortCursor = new SortCursor(cursors, Images.ImageColumns.DATE_TAKEN, SortCursor.TYPE_NUMERIC, true);
            if (Thread.interrupted()) {
                return;
            }
            try {
                if (sortCursor != null && sortCursor.moveToFirst()) {
                    final int count = sortCursor.getCount();
                    final int numSets = sets.size();
                    final int approximateCountPerSet = count / numSets;
                    for (int i = 0; i < numSets; ++i) {
                        final MediaSet set = sets.get(i);
                        set.setNumExpectedItems(approximateCountPerSet);
                    }
                    do {
                        if (Thread.interrupted()) {
                            return;
                        }
                        final MediaItem item = new MediaItem();
                        final boolean isVideo = (sortCursor.getCurrentCursorIndex() == 1);
                        if (isVideo) {
                            populateVideoItemFromCursor(item, cr, sortCursor, CacheService.BASE_CONTENT_STRING_VIDEOS);
                        } else {
                            populateMediaItemFromCursor(item, cr, sortCursor, CacheService.BASE_CONTENT_STRING_IMAGES);
                        }
                        final long setId = sortCursor.getLong(MEDIA_BUCKET_ID_INDEX);
                        final MediaSet set = findSet(setId, acceleratedSets);
                        if (set != null) {
                            set.addItem(item);
                        }
                    } while (sortCursor.moveToNext());
                }
            } finally {
                if (sortCursor != null)
                    sortCursor.close();
            }
        } catch (Exception e) {
            // If the database operation failed for any reason
            ;
        }
        if (sets.size() > 0) {
            writeItemsToCache(sets);
            if (DEBUG)
                Log.i(TAG, "Done building items.");
        }
    }

    private static final void writeSetsToCache(final ArrayList<MediaSet> sets) {
        final ByteArrayOutputStream bos = new ByteArrayOutputStream();
        final int numSets = sets.size();
        final DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(bos, 256));
        try {
            dos.writeInt(numSets);
            for (int i = 0; i < numSets; ++i) {
                if (Thread.interrupted()) {
                    return;
                }
                final MediaSet set = sets.get(i);
                dos.writeLong(set.mId);
                Utils.writeUTF(dos, set.mName);
                dos.writeBoolean(set.mHasImages);
                dos.writeBoolean(set.mHasVideos);
            }
            dos.flush();
            sAlbumCache.put(ALBUM_CACHE_METADATA_INDEX, bos.toByteArray(), 0);
            dos.close();
            if (numSets == 0) {
                sAlbumCache.deleteAll();
                putLocaleForAlbumCache(Locale.getDefault());
            }
            sAlbumCache.flush();
        } catch (IOException e) {
            Log.e(TAG, "Error writing albums to diskcache.");
            sAlbumCache.deleteAll();
            putLocaleForAlbumCache(Locale.getDefault());
        }
    }

    private static final void writeItemsToCache(final ArrayList<MediaSet> sets) {
        final int numSets = sets.size();
        for (int i = 0; i < numSets; ++i) {
            if (Thread.interrupted()) {
                return;
            }
            writeItemsForASet(sets.get(i));
        }
        sAlbumCache.flush();
    }

    private static final void writeItemsForASet(final MediaSet set) {
        final ByteArrayOutputStream bos = new ByteArrayOutputStream();
        final DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(bos, 256));
        try {
            final ArrayList<MediaItem> items = set.getItems();
            final int numItems = items.size();
            dos.writeInt(numItems);
            dos.writeLong(set.mMinTimestamp);
            dos.writeLong(set.mMaxTimestamp);
            for (int i = 0; i < numItems; ++i) {
                MediaItem item = items.get(i);
                if (set.mId == LocalDataSource.CAMERA_BUCKET_ID || set.mId == LocalDataSource.DOWNLOAD_BUCKET_ID) {
                    // Reverse the display order for the camera bucket - want
                    // the latest first.
                    item = items.get(numItems - i - 1);
                }
                dos.writeLong(item.mId);
                Utils.writeUTF(dos, item.mCaption);
                Utils.writeUTF(dos, item.mMimeType);
                dos.writeInt(item.getMediaType());
                dos.writeDouble(item.mLatitude);
                dos.writeDouble(item.mLongitude);
                dos.writeLong(item.mDateTakenInMs);
                dos.writeBoolean(item.mTriedRetrievingExifDateTaken);
                dos.writeLong(item.mDateAddedInSec);
                dos.writeLong(item.mDateModifiedInSec);
                dos.writeInt(item.mDurationInSec);
                dos.writeInt((int) item.mRotation);
                Utils.writeUTF(dos, item.mFilePath);
            }
            dos.flush();
            sAlbumCache.put(set.mId, bos.toByteArray(), 0);
            dos.close();
        } catch (Exception e) {
            Log.e(TAG, "Error writing to diskcache for set " + set.mName);
            sAlbumCache.deleteAll();
            putLocaleForAlbumCache(Locale.getDefault());
        }
    }

    private static final MediaSet findSet(final long id, final LongSparseArray<MediaSet> acceleratedTable) {
        // This is the accelerated lookup table for the MediaSet based on set
        // id.
        return acceleratedTable.get(id);
    }
}
