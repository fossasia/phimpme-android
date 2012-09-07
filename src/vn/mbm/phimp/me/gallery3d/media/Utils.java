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

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import vn.mbm.phimp.me.gallery3d.app.App;
import vn.mbm.phimp.me.gallery3d.app.Res;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.widget.Toast;

public class Utils {
    private static final int UNCONSTRAINED = -1;

    public static void playVideo(final Context context, final MediaItem item) {
        // this is a video
        App.get(context).getHandler().post(new Runnable() {
            @SuppressWarnings("static-access")
			public void run() {
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(item.mContentUri));
                    intent.setDataAndType(Uri.parse(item.mContentUri), item.mMimeType);
                    context.startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(context, context.getResources().getString(Res.string.video_err), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public static final void writeUTF(DataOutputStream dos, String string) throws IOException {
        if (string == null) {
            dos.writeUTF(new String());
        } else {
            dos.writeUTF(string);
        }
    }

    public static final String readUTF(DataInputStream dis) throws IOException {
        String retVal = dis.readUTF();
        if (retVal.length() == 0)
            return null;
        return retVal;
    }

    public static final Bitmap resizeBitmap(Bitmap bitmap, int maxSize) {
        int srcWidth = bitmap.getWidth();
        int srcHeight = bitmap.getHeight();
        int width = maxSize;
        int height = maxSize;
        boolean needsResize = false;
        if (srcWidth > srcHeight) {
            if (srcWidth > maxSize) {
                needsResize = true;
                height = ((maxSize * srcHeight) / srcWidth);
            }
        } else {
            if (srcHeight > maxSize) {
                needsResize = true;
                width = ((maxSize * srcWidth) / srcHeight);
            }
        }
        if (needsResize) {
            Bitmap retVal = Bitmap.createScaledBitmap(bitmap, width, height, true);
            return retVal;
        } else {
            return bitmap;
        }
    }

    private static final long POLY64REV = 0x95AC9329AC4BC9B5L;
    private static final long INITIALCRC = 0xFFFFFFFFFFFFFFFFL;

    private static boolean init = false;
    private static long[] CRCTable = new long[256];

    /**
     * A function thats returns a 64-bit crc for string
     * 
     * @param in
     *            : input string
     * @return 64-bit crc value
     */
    public static final long Crc64Long(String in) {
        if (in == null || in.length() == 0) {
            return 0;
        }
        // http://bioinf.cs.ucl.ac.uk/downloads/crc64/crc64.c
        long crc = INITIALCRC, part;
        if (!init) {
            for (int i = 0; i < 256; i++) {
                part = i;
                for (int j = 0; j < 8; j++) {
                    int value = ((int) part & 1);
                    if (value != 0)
                        part = (part >> 1) ^ POLY64REV;
                    else
                        part >>= 1;
                }
                CRCTable[i] = part;
            }
            init = true;
        }
        int length = in.length();
        for (int k = 0; k < length; ++k) {
            char c = in.charAt(k);
            crc = CRCTable[(((int) crc) ^ c) & 0xff] ^ (crc >> 8);
        }
        return crc;
    }

    /**
     * A function that returns a human readable hex string of a Crx64
     * 
     * @param in
     *            : input string
     * @return hex string of the 64-bit CRC value
     */
    public static final String Crc64(String in) {
        if (in == null)
            return null;
        long crc = Crc64Long(in);
        /*
         * The output is done in two parts to avoid problems with
         * architecture-dependent word order
         */
        int low = ((int) crc) & 0xffffffff;
        int high = ((int) (crc >> 32)) & 0xffffffff;
        String outVal = Integer.toHexString(high) + Integer.toHexString(low);
        return outVal;
    }

    public static long getBucketIdFromUri(final ContentResolver cr, final Uri uri) {
        if (uri.getScheme().equals("file")) {
            String string = "/";
            List<String> paths = uri.getPathSegments();
            int numPaths = paths.size();
            for (int i = 0; i < numPaths - 1; ++i) {
                string += paths.get(i);
                if (i != numPaths - 2)
                    string += "/";
            }
            return LocalDataSource.getBucketId(string);
        } else {
            Cursor cursor = null;
            try {
                long id = ContentUris.parseId(uri);
                cursor = cr.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        new String[] { MediaStore.Images.ImageColumns.BUCKET_ID }, MediaStore.Images.ImageColumns._ID + "=" + id,
                        null, null);
                if (cursor != null) {
                    if (cursor.moveToFirst()) {
                        long setVal = cursor.getLong(0);
                        cursor.close();
                        return setVal;
                    }
                }
                cursor = cr.query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                        new String[] { MediaStore.Video.VideoColumns.BUCKET_ID }, MediaStore.Images.ImageColumns._ID + "=" + id,
                        null, null);
                if (cursor != null) {
                    if (cursor.moveToFirst()) {
                        long setVal = cursor.getLong(0);
                        cursor.close();
                        return setVal;
                    }
                }
            } catch (Exception e) {
                ;
            }
            return Shared.INVALID;
        }
    }

    public static String getBucketNameFromUri(final ContentResolver cr, final Uri uri) {
        final long bucketId = getBucketIdFromUri(cr, uri);
        if (bucketId != Shared.INVALID) {
            try {
                Cursor cursor = cr.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        new String[] { MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME },
                        MediaStore.Images.ImageColumns.BUCKET_ID + "='" + bucketId + "'", null, null);
                if (cursor != null) {
                    if (cursor.moveToFirst()) {
                        String setName = cursor.getString(0);
                        cursor.close();
                        return setName;
                    }
                }
                cursor = cr.query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                        new String[] { MediaStore.Video.VideoColumns.BUCKET_DISPLAY_NAME }, MediaStore.Video.VideoColumns.BUCKET_ID
                                + "='" + bucketId + "'", null, null);
                if (cursor != null) {
                    if (cursor.moveToFirst()) {
                        String setName = cursor.getString(0);
                        cursor.close();
                        return setName;
                    }
                }
            } catch (Exception e) {
                ;
            }
        }
        return "";
    }

    // Copies src file to dst file.
    // If the dst file does not exist, it is created
    public static void Copy(File src, File dst) throws IOException {
        InputStream in = new FileInputStream(src);
        OutputStream out = new FileOutputStream(dst);
        copyStream(in, out);
    }
    
    public static void copyStream(InputStream in, OutputStream out) throws IOException {
        // Transfer bytes from in to out
        byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        in.close();
        out.close();
    }

    /*
     * Compute the sample size as a function of minSideLength
     * and maxNumOfPixels.
     * minSideLength is used to specify that minimal width or height of a
     * bitmap.
     * maxNumOfPixels is used to specify the maximal size in pixels that is
     * tolerable in terms of memory usage.
     *
     * The function returns a sample size based on the constraints.
     * Both size and minSideLength can be passed in as IImage.UNCONSTRAINED,
     * which indicates no care of the corresponding constraint.
     * The functions prefers returning a sample size that
     * generates a smaller bitmap, unless minSideLength = IImage.UNCONSTRAINED.
     *
     * Also, the function rounds up the sample size to a power of 2 or multiple
     * of 8 because BitmapFactory only honors sample size this way.
     * For example, BitmapFactory downsamples an image by 2 even though the
     * request is 3. So we round up the sample size to avoid OOM.
     */
    public static int computeSampleSize(BitmapFactory.Options options,
            int minSideLength, int maxNumOfPixels) {
        int initialSize = computeInitialSampleSize(options, minSideLength,
                maxNumOfPixels);

        int roundedSize;
        if (initialSize <= 8 ) {
            roundedSize = 1;
            while (roundedSize < initialSize) {
                roundedSize <<= 1;
            }
        } else {
            roundedSize = (initialSize + 7) / 8 * 8;
        }

        return roundedSize;
    }

    public static int computeInitialSampleSize(BitmapFactory.Options options,
            int minSideLength, int maxNumOfPixels) {
        double w = options.outWidth;
        double h = options.outHeight;

        int lowerBound = (maxNumOfPixels == UNCONSTRAINED) ? 1 :
                (int) Math.ceil(Math.sqrt(w * h / maxNumOfPixels));
        int upperBound = (minSideLength == UNCONSTRAINED) ? 128 :
                (int) Math.min(Math.floor(w / minSideLength),
                Math.floor(h / minSideLength));

        if (upperBound < lowerBound) {
            // return the larger one when there is no overlapping zone.
            return lowerBound;
        }

        if ((maxNumOfPixels == UNCONSTRAINED) &&
                (minSideLength == UNCONSTRAINED)) {
            return 1;
        } else if (minSideLength == UNCONSTRAINED) {
            return lowerBound;
        } else {
            return upperBound;
        }
    }

}
