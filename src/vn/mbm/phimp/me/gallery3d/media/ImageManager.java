/*
 * Copyright (C) 2007 The Android Open Source Project
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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.ExecutionException;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.provider.MediaStore.Images.ImageColumns;
import android.util.Log;

/**
 * ImageManager is used to retrieve and store images in the media content
 * provider.
 */
public class ImageManager {
    private static final String TAG = "ImageManager";

    private static final Uri STORAGE_URI = Images.Media.EXTERNAL_CONTENT_URI;

    /**
     * Enumerate type for the location of the images in gallery.
     */
    public static enum DataLocation {
        NONE, INTERNAL, EXTERNAL, ALL
    }

    public static final Bitmap DEFAULT_THUMBNAIL = Bitmap.createBitmap(32, 32, Bitmap.Config.RGB_565);
    public static final Bitmap NO_IMAGE_BITMAP = Bitmap.createBitmap(1, 1, Bitmap.Config.RGB_565);

    public static final int SORT_ASCENDING = 1;
    public static final int SORT_DESCENDING = 2;

    public static final int INCLUDE_IMAGES = (1 << 0);
    public static final int INCLUDE_DRM_IMAGES = (1 << 1);
    public static final int INCLUDE_VIDEOS = (1 << 2);

    public static final String CAMERA_IMAGE_BUCKET_NAME = Environment.getExternalStorageDirectory().toString() + "/DCIM/Camera";
    public static final String CAMERA_IMAGE_BUCKET_ID = getBucketId(CAMERA_IMAGE_BUCKET_NAME);

    /**
     * Matches code in MediaProvider.computeBucketValues. Should be a common
     * function.
     */
    public static String getBucketId(String path) {
        return String.valueOf(path.toLowerCase().hashCode());
    }

    /**
     * OSX requires plugged-in USB storage to have path /DCIM/NNNAAAAA to be
     * imported. This is a temporary fix for bug#1655552.
     */
    public static void ensureOSXCompatibleFolder() {
        File nnnAAAAA = new File(Environment.getExternalStorageDirectory().toString() + "/DCIM/100ANDRO");
        if ((!nnnAAAAA.exists()) && (!nnnAAAAA.mkdir())) {
            Log.e(TAG, "create NNNAAAAA file: " + nnnAAAAA.getPath() + " failed");
        }
    }

    public static int roundOrientation(int orientationInput) {
        int orientation = orientationInput;
        if (orientation == -1) {
            orientation = 0;
        }

        orientation = orientation % 360;
        int retVal;
        if (orientation < (0 * 90) + 45) {
            retVal = 0;
        } else if (orientation < (1 * 90) + 45) {
            retVal = 90;
        } else if (orientation < (2 * 90) + 45) {
            retVal = 180;
        } else if (orientation < (3 * 90) + 45) {
            retVal = 270;
        } else {
            retVal = 0;
        }

        return retVal;
    }

    /**
     * @return true if the mimetype is an image mimetype.
     */
    public static boolean isImageMimeType(String mimeType) {
        return mimeType.startsWith("image/");
    }

    /**
     * @return true if the mimetype is a video mimetype.
     */
    public static boolean isVideoMimeType(String mimeType) {
        return mimeType.startsWith("video/");
    }

    public static void setImageSize(ContentResolver cr, Uri uri, long size) {
        ContentValues values = new ContentValues();
        values.put(Images.Media.SIZE, size);
        cr.update(uri, values, null, null);
    }

    /**
     * Stores a bitmap or a jpeg byte array to a file (using the specified
     * directory and filename). Also add an entry to the media store for
     * this picture. The title, dateTaken, location are attributes for the
     * picture. The degree is a one element array which returns the orientation
     * of the picture.
    */
    public static Uri addImage(ContentResolver cr, String title, long dateAdded,
            long dateTaken, Double latitude, Double longitude, String directory,
            String filename, Bitmap source, byte[] jpegData, int[] degree) {
        // We should store image data earlier than insert it to ContentProvider,
        // otherwise we may not be able to generate thumbnail in time.
        OutputStream outputStream = null;
        String filePath = directory + "/" + filename;
        try {
            File dir = new File(directory);
            if (!dir.exists()) dir.mkdirs();
            File file = new File(directory, filename);
            outputStream = new FileOutputStream(file);
            if (source != null) {
                source.compress(CompressFormat.JPEG, 75, outputStream);
                degree[0] = 0;
            } else {
                outputStream.write(jpegData);
                degree[0] = getExifOrientation(filePath);
            }
        } catch (FileNotFoundException ex) {
            Log.w(TAG, ex);
            return null;
        } catch (IOException ex) {
            Log.w(TAG, ex);
            return null;
        } finally {
            Util.closeSilently(outputStream);
        }

        // Read back the compressed file size.
        long size = new File(directory, filename).length();

        ContentValues values = new ContentValues(11);
        values.put(Images.Media.TITLE, title);

        // That filename is what will be handed to Gmail when a user shares a
        // photo. Gmail gets the name of the picture attachment from the
        // "DISPLAY_NAME" field.
        values.put(Images.Media.DISPLAY_NAME, filename);
        values.put(Images.Media.DATE_TAKEN, dateTaken);
        values.put(Images.Media.DATE_MODIFIED, dateTaken);
        values.put(Images.Media.DATE_ADDED, dateAdded);
        values.put(Images.Media.MIME_TYPE, "image/jpeg");
        values.put(Images.Media.ORIENTATION, degree[0]);
        values.put(Images.Media.DATA, filePath);
        values.put(Images.Media.SIZE, size);

        if (latitude != null && longitude != null) {
            values.put(Images.Media.LATITUDE, latitude.floatValue());
            values.put(Images.Media.LONGITUDE, longitude.floatValue());
        }

        return cr.insert(STORAGE_URI, values);
    }

    public static int getExifOrientation(String filepath) {
        int degree = 0;
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(filepath);
        } catch (IOException ex) {
            Log.e(TAG, "cannot read exif", ex);
        }
        if (exif != null) {
            int orientation = exif.getAttributeInt(
                ExifInterface.TAG_ORIENTATION, -1);
            if (orientation != -1) {
                // We only recognize a subset of orientation tag values.
                switch(orientation) {
                    case ExifInterface.ORIENTATION_ROTATE_90:
                        degree = 90;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_180:
                        degree = 180;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_270:
                        degree = 270;
                        break;
                }

            }
        }
        return degree;
    }
    private static class AddImageCancelable extends BaseCancelable<Void> {
        private final Uri mUri;
        private final ContentResolver mCr;
        private final byte[] mJpegData;

        public AddImageCancelable(Uri uri, ContentResolver cr, int orientation, Bitmap source, byte[] jpegData) {
            if (source == null && jpegData == null || uri == null) {
                throw new IllegalArgumentException("source cannot be null");
            }
            mUri = uri;
            mCr = cr;
            mJpegData = jpegData;
        }

        @Override
        protected Void execute() throws InterruptedException, ExecutionException {
            boolean complete = false;
            try {
                String[] projection = new String[] { ImageColumns._ID, ImageColumns.MINI_THUMB_MAGIC };
                Cursor c = mCr.query(mUri, projection, null, null, null);
                try {
                    c.moveToPosition(0);
                } finally {
                    c.close();
                }
                ContentValues values = new ContentValues();
                values.put(ImageColumns.MINI_THUMB_MAGIC, 0);
                mCr.update(mUri, values, null, null);
                OutputStream outputStream = null;
                try {
                    outputStream = mCr.openOutputStream(mUri);
                    if (outputStream != null) {
                        outputStream.write(mJpegData);
                    }
                } catch (IOException ex) {
                    // TODO: report error to caller
                    Log.e(TAG, "Cannot open file: " + mUri, ex);
                } finally {
                    Util.closeSilently(outputStream);
                }
                complete = true;
                return null;
            } finally {
                if (!complete) {
                    try {
                        mCr.delete(mUri, null, null);
                    } catch (Throwable t) {
                        // ignore it while clean up.
                    }
                }
            }
        }
    }

    public static Cancelable<Void> storeImage(Uri uri, ContentResolver cr, int orientation, Bitmap source, byte[] jpegData) {
        return new AddImageCancelable(uri, cr, orientation, source, jpegData);
    }

    static boolean isSingleImageMode(String uriString) {
        return !uriString.startsWith(MediaStore.Images.Media.EXTERNAL_CONTENT_URI.toString())
                && !uriString.startsWith(MediaStore.Images.Media.INTERNAL_CONTENT_URI.toString());
    }

    private static boolean checkFsWritable() {
        // Create a temporary file to see whether a volume is really writeable.
        // It's important not to put it in the root directory which may have a
        // limit on the number of files.
        String directoryName = Environment.getExternalStorageDirectory().toString() + "/DCIM";
        File directory = new File(directoryName);
        if (!directory.isDirectory()) {
            if (!directory.mkdirs()) {
                return false;
            }
        }
        return directory.canWrite();
    }

    public static boolean quickHasStorage() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }

    public static boolean hasStorage() {
        return hasStorage(true);
    }

    public static boolean hasStorage(boolean requireWriteAccess) {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            if (requireWriteAccess) {
                boolean writable = checkFsWritable();
                return writable;
            } else {
                return true;
            }
        } else if (!requireWriteAccess && Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

    private static final Cursor query(final ContentResolver resolver, final Uri uri, final String[] projection,
            final String selection, final String[] selectionArgs, final String sortOrder) {
        try {
            if (resolver == null) {
                return null;
            }
            return resolver.query(uri, projection, selection, selectionArgs, sortOrder);
        } catch (UnsupportedOperationException ex) {
            return null;
        }

    }

    public static final boolean isMediaScannerScanning(final ContentResolver cr) {
        boolean result = false;
        final Cursor cursor = query(cr, MediaStore.getMediaScannerUri(), new String[] { MediaStore.MEDIA_SCANNER_VOLUME }, null,
                null, null);
        if (cursor != null) {
            if (cursor.getCount() == 1) {
                cursor.moveToFirst();
                result = "external".equals(cursor.getString(0));
            }
            cursor.close();
        }
        return result;
    }

    public static String getLastImageThumbPath() {
        return Environment.getExternalStorageDirectory().toString() + "/DCIM/.thumbnails/image_last_thumb";
    }

    public static String getLastVideoThumbPath() {
        return Environment.getExternalStorageDirectory().toString() + "/DCIM/.thumbnails/video_last_thumb";
    }
}
