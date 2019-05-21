/*
 * Copyright (C) 2012 Lightbox
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

package org.fossasia.phimpme.editor.utils;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.os.Environment;
import android.util.Log;
import android.view.Display;
import android.view.View;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * BitmapUtils
 *
 * @author panyi
 */
public class BitmapUtils {
  /** Used to tag logs */
  @SuppressWarnings("unused")
  private static final String TAG = "BitmapUtils";

  public static final long MAX_SZIE = 1024 * 512; // 500KB

  public static int getOrientation(final String imagePath) {
    int rotate = 0;
    try {
      File imageFile = new File(imagePath);
      ExifInterface exif = new ExifInterface(imageFile.getAbsolutePath());
      int orientation =
          exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
      switch (orientation) {
        case ExifInterface.ORIENTATION_ROTATE_270:
          rotate = 270;
          break;
        case ExifInterface.ORIENTATION_ROTATE_180:
          rotate = 180;
          break;
        case ExifInterface.ORIENTATION_ROTATE_90:
          rotate = 90;
          break;
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return rotate;
  }

  public static BitmapSize getBitmapSize(String filePath) {
    Options options = new Options();
    options.inJustDecodeBounds = true;

    BitmapFactory.decodeFile(filePath, options);

    return new BitmapSize(options.outWidth, options.outHeight);
  }

  public static BitmapSize getScaledSize(int originalWidth, int originalHeight, int numPixels) {
    float ratio = (float) originalWidth / originalHeight;

    int scaledHeight = (int) Math.sqrt((float) numPixels / ratio);
    int scaledWidth = (int) (ratio * Math.sqrt((float) numPixels / ratio));

    return new BitmapSize(scaledWidth, scaledHeight);
  }

  public static class BitmapSize {
    public int width;
    public int height;

    public BitmapSize(int width, int height) {
      this.width = width;
      this.height = height;
    }
  }

  public static byte[] bitmapTobytes(Bitmap bitmap) {
    ByteArrayOutputStream a = new ByteArrayOutputStream();
    bitmap.compress(CompressFormat.PNG, 30, a);
    return a.toByteArray();
  }

  public static byte[] bitmapTobytesNoCompress(Bitmap bitmap) {
    ByteArrayOutputStream a = new ByteArrayOutputStream();
    bitmap.compress(CompressFormat.PNG, 100, a);
    return a.toByteArray();
  }

  public static Bitmap genRotateBitmap(byte[] data) {
    Bitmap bMap = BitmapFactory.decodeByteArray(data, 0, data.length);
    // 自定义相机拍照需要旋转90预览支持竖屏
    Matrix matrix = new Matrix(); // matrix
    matrix.reset(); // Set matrix
    matrix.postRotate(90); // 90 degrees
    Bitmap bMapRotate =
        Bitmap.createBitmap(bMap, 0, 0, bMap.getWidth(), bMap.getHeight(), matrix, true);
    bMap.recycle();
    bMap = null;
    System.gc();
    return bMapRotate;
  }

  public static Bitmap byteToBitmap(byte[] data) {
    return BitmapFactory.decodeByteArray(data, 0, data.length);
  }

  /**
   * he view into bitmap
   *
   * @param view
   * @return
   */
  public static Bitmap getBitmapFromView(View view) {
    Bitmap returnedBitmap =
        Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
    Canvas canvas = new Canvas(returnedBitmap);
    Drawable bgDrawable = view.getBackground();
    if (bgDrawable != null) bgDrawable.draw(canvas);
    else canvas.drawColor(Color.WHITE);
    view.draw(canvas);
    return returnedBitmap;
  }

  // According to size scaling
  public static Bitmap getImageCompress(final String srcPath) {
    Options newOpts = new Options();
    // Began to read picture，At this point the options.inJustDecodeBounds Set back to true
    newOpts.inJustDecodeBounds = true;
    Bitmap bitmap = BitmapFactory.decodeFile(srcPath, newOpts); // This will return null bitmap

    newOpts.inJustDecodeBounds = false;
    int w = newOpts.outWidth;
    int h = newOpts.outHeight;
    // Since most of the Mobile phone has resolution 800 * 480，So now we set the height and width
    float hh = 800f; // Here set the height to 800f
    float ww = 480f; // Here set the width to 480f
    // Zoom ratio,Because it is fixed scaling，Wherein only a high or a data width can be calculated
    int be = 1; // be=1 It means no scaling
    if (w > h && w > ww) { // If the width of the large width, then scaled to a fixed size
      be = (int) (newOpts.outWidth / ww);
    } else if (w < h && h > hh) { // If high, then scaled to the height of a fixed size width
      be = (int) (newOpts.outHeight / hh);
    }
    if (be <= 0) be = 1;
    newOpts.inSampleSize = be; // Set Scaling
    // Re-read picture，Note that this time has been put options.inJustDecodeBounds Set back to false
    bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
    return compressImage(
        bitmap); // The compression ratio of the size good quality after compression
  }

  // Image compression in proportion to the size of the
  public static Bitmap compress(Bitmap image) {

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    image.compress(CompressFormat.JPEG, 100, baos);
    if (baos.toByteArray().length / 1024
        > 1024) { // If the image is larger than judgment 1MB,Avoid generating compressed
      // picture（BitmapFactory.decodeStream）Overflow
      baos.reset(); // Reset empty baos i.e. baos
      image.compress(
          CompressFormat.JPEG, 50, baos); // Here Compression50%，Storing the compressed data to baos
    }
    ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
    Options newOpts = new Options();
    // Read picture，At this point the options.inJustDecodeBounds is set to true
    newOpts.inJustDecodeBounds = true;
    Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
    newOpts.inJustDecodeBounds = false;
    int w = newOpts.outWidth;
    int h = newOpts.outHeight;
    // SInce mainstream mobile phone is 800 * 480 resolution，So we set the height and width
    float hh = 800f; // Here the height to 800f
    float ww = 480f; // Here set the width to 480f
    // Zoom ratio Because it is fixed scaling，Wherein only a height or a data width can be
    // calculated
    int be = 1; // be= 1It means no scaling
    if (w > h && w > ww) { // If the width of the large width, then scaled to a fixed size
      be = (int) (newOpts.outWidth / ww);
    } else if (w < h && h > hh) { // If high, then scaled to the height of a fixed size width
      be = (int) (newOpts.outHeight / hh);
    }
    if (be <= 0) be = 1;
    newOpts.inSampleSize = be; // Set Scaling
    // Re-read picture，Note that this time has been put options.inJustDecodeBounds to false
    isBm = new ByteArrayInputStream(baos.toByteArray());
    bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
    return compressImage(
        bitmap); // The compression ratio of the size good quality after compression
  }

  // Picture quality compression
  private static Bitmap compressImage(Bitmap image) {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    image.compress(
        CompressFormat.JPEG,
        100,
        baos); // Quality compression method，Here 100 means no compression.
    int options = 100;

    while (baos.toByteArray().length / 1024
        > 100) { // If the compression cycle to determine whether the picture is larger than
      // 100kb,Greater than continue to compress
      baos.reset(); // Reset baos that cleared baos
      image.compress(
          CompressFormat.JPEG,
          options,
          baos); // Here Compression options%，Storing the compressed data to baos
      options -= 10; // Every reduction 10
    }
    ByteArrayInputStream isBm =
        new ByteArrayInputStream(
            baos.toByteArray()); // The compressed data baos To store ByteArrayInputStream
    Bitmap bitmap =
        BitmapFactory.decodeStream(
            isBm, null, null); // ByteArrayInputStream- Data generated picture
    return bitmap;
  }

  public void printscreen_share(View v, Activity context) {
    View view1 = context.getWindow().getDecorView();
    Display display = context.getWindowManager().getDefaultDisplay();
    view1.layout(0, 0, display.getWidth(), display.getHeight());
    view1.setDrawingCacheEnabled(true);
    Bitmap bitmap = Bitmap.createBitmap(view1.getDrawingCache());
  }

  // Pictures converted file
  public static boolean saveBitmap2file(Bitmap bmp, String filepath) {
    CompressFormat format = CompressFormat.PNG;
    int quality = 100;
    OutputStream stream = null;
    try {
      // Analyzing the state of SDcard
      if (!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
        // Error message
        return false;
      }

      // Check the SDcard space
      File SDCardRoot = Environment.getExternalStorageDirectory();
      if (SDCardRoot.getFreeSpace() < 10000) {
        // A dialog box prompts if the user enough space
        Log.e("Utils", "\n" + "Not enough storage space");
        return false;
      }

      // Creating folders and files in the SDcard
      File bitmapFile = new File(SDCardRoot.getPath() + filepath);
      bitmapFile.getParentFile().mkdirs(); // Create a folder
      stream = new FileOutputStream(SDCardRoot.getPath() + filepath); // "/sdcard/"
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
    return bmp.compress(format, quality, stream);
  }

  /**
   * 截屏
   *
   * @param activity
   * @return
   */
  public static Bitmap getScreenViewBitmap(Activity activity) {
    View view = activity.getWindow().getDecorView();
    view.setDrawingCacheEnabled(true);
    view.buildDrawingCache();
    Bitmap bitmap = view.getDrawingCache();
    return bitmap;
  }

  /**
   * 一个 View的图像
   *
   * @param view
   * @return
   */
  public static Bitmap getViewBitmap(View view) {
    view.setDrawingCacheEnabled(true);
    view.buildDrawingCache();
    Bitmap bitmap = view.getDrawingCache();
    return bitmap;
  }

  /**
   * Resize a bitmap object to fit the passed width and height
   *
   * @param input The bitmap to be resized
   * @param destWidth Desired maximum width of the result bitmap
   * @param destHeight Desired maximum height of the result bitmap
   * @return A new resized bitmap
   * @throws OutOfMemoryError if the operation exceeds the available vm memory
   */
  public static Bitmap resizeBitmap(final Bitmap input, int destWidth, int destHeight, int rotation)
      throws OutOfMemoryError {

    int dstWidth = destWidth;
    int dstHeight = destHeight;
    final int srcWidth = input.getWidth();
    final int srcHeight = input.getHeight();

    if (rotation == 90 || rotation == 270) {
      dstWidth = destHeight;
      dstHeight = destWidth;
    }

    boolean needsResize = false;
    float p;
    if ((srcWidth > dstWidth) || (srcHeight > dstHeight)) {
      needsResize = true;
      if ((srcWidth > srcHeight) && (srcWidth > dstWidth)) {
        p = (float) dstWidth / (float) srcWidth;
        dstHeight = (int) (srcHeight * p);
      } else {
        p = (float) dstHeight / (float) srcHeight;
        dstWidth = (int) (srcWidth * p);
      }
    } else {
      dstWidth = srcWidth;
      dstHeight = srcHeight;
    }

    if (needsResize || rotation != 0) {
      Bitmap output;

      if (rotation == 0) {
        output = Bitmap.createScaledBitmap(input, dstWidth, dstHeight, true);
      } else {
        Matrix matrix = new Matrix();
        matrix.postScale((float) dstWidth / srcWidth, (float) dstHeight / srcHeight);
        matrix.postRotate(rotation);
        output = Bitmap.createBitmap(input, 0, 0, srcWidth, srcHeight, matrix, true);
      }
      return output;
    } else return input;
  }

  /**
   * Resize a bitmap
   *
   * @param input
   * @param destWidth
   * @param destHeight
   * @return
   * @throws OutOfMemoryError
   */
  public static Bitmap resizeBitmap(final Bitmap input, int destWidth, int destHeight)
      throws OutOfMemoryError {
    return resizeBitmap(input, destWidth, destHeight, 0);
  }

  public static Bitmap getSampledBitmap(String filePath, int reqWidth, int reqHeight) {
    Options options = new Options();
    options.inJustDecodeBounds = true;
    BitmapFactory.decodeFile(filePath, options);
    int inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
    options.inSampleSize = inSampleSize;
    options.inPreferredConfig = Bitmap.Config.RGB_565;
    options.inJustDecodeBounds = false;
    return BitmapFactory.decodeFile(filePath, options);
  }

  public static int calculateInSampleSize(Options options, int reqWidth, int reqHeight) {
    // Raw height and width of image
    final int height = options.outHeight;
    final int width = options.outWidth;
    int inSampleSize = 1;

    if (height > reqHeight || width > reqWidth) {

      final int halfHeight = height / 2;
      final int halfWidth = width / 2;

      // Calculate the largest inSampleSize value that is a power of 2 and keeps both
      // height and width larger than the requested height and width.
      while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth) {
        inSampleSize *= 2;
      }
    }

    return inSampleSize;
  }

  /**
   * 保存Bitmap图片到指定文件
   *
   * @param bm
   */
  public static boolean saveBitmap(Bitmap bm, String filePath) {
    File f = new File(filePath);
    if (f.exists()) {
      f.delete();
    }
    try {
      FileOutputStream out = new FileOutputStream(f);
      bm.compress(CompressFormat.PNG, 90, out);
      out.flush();
      out.close();
      return true;
    } catch (FileNotFoundException e) {
      e.printStackTrace();
      return false;
    } catch (IOException e) {
      e.printStackTrace();
      return false;
    }
  }
}
