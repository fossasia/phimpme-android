package org.fossasia.phimpme.editor.filter;

import android.graphics.Bitmap;
import android.util.Log;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

public class PhotoProcessing {
  private static final String TAG = "PhotoProcessing";

  static {
    if (!OpenCVLoader.initDebug()) {
      Log.e(TAG + " - Error", "Unable to load OpenCV");
    } else {
      System.loadLibrary("nativeimageprocessing");
    }
  }

  public static Bitmap processImage(Bitmap bitmap, int effectType, int val) {
    Mat inputMat = new Mat(bitmap.getWidth(), bitmap.getHeight(), CvType.CV_8UC3);
    Mat outputMat = new Mat();
    Utils.bitmapToMat(bitmap, inputMat);
    if (!isEnhance(effectType))
      nativeApplyFilter(
          effectType % 100, val, inputMat.getNativeObjAddr(), outputMat.getNativeObjAddr());
    else
      nativeEnhanceImage(
          effectType % 100, val, inputMat.getNativeObjAddr(), outputMat.getNativeObjAddr());

    inputMat.release();

    if (outputMat != null) {
      Bitmap outbit =
          Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), bitmap.getConfig());
      Utils.matToBitmap(outputMat, outbit);
      outputMat.release();
      return outbit;
    }
    return bitmap.copy(bitmap.getConfig(), true);
  }

  private static native void nativeApplyFilter(int mode, int val, long inpAddr, long outAddr);

  private static native void nativeEnhanceImage(int mode, int val, long inpAddr, long outAddr);

  private static boolean isEnhance(int effectType) {
    return (effectType / 300 == 1);
  }
}
