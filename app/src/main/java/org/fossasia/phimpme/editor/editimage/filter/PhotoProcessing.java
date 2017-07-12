package org.fossasia.phimpme.editor.editimage.filter;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.util.Log;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

public class PhotoProcessing {
    private static final String TAG = "PhotoProcessing";
    // /////////////////////////////////////////////
    static {
        if (!OpenCVLoader.initDebug()) {
            Log.e( TAG + " - Error", "Unable to load OpenCV");
        } else {
            System.loadLibrary("photoprocessing");
        }
    }


    public static Bitmap processImage(Bitmap bitmap, int effectType, int val) {
        Mat inputMat = new Mat(bitmap.getWidth(), bitmap.getHeight(), CvType.CV_8UC3);
        Mat outputMat = new Mat();
        Utils.bitmapToMat(bitmap, inputMat);
        if(!isEnhance(effectType))
            nativeApplyFilter(effectType % 100,val, inputMat.getNativeObjAddr(), outputMat.getNativeObjAddr());
        else
            nativeEnhanceImage(effectType % 100,val, inputMat.getNativeObjAddr(), outputMat.getNativeObjAddr());

        inputMat.release();

        if (outputMat !=null){
            Bitmap outbit = Bitmap.createBitmap(bitmap.getWidth(),bitmap.getHeight(),bitmap.getConfig());
            Utils.matToBitmap(outputMat,outbit);
            outputMat.release();
            return outbit;
        }
        return bitmap.copy(bitmap.getConfig(),true);
    }

    public static Bitmap filterPhoto(Bitmap bitmap, int position,int value) {
        if (bitmap != null && position != 0) {
            sendBitmapToNative(bitmap);
        }
        switch (position) {
            case 0: // Original
                break;
            case 1: // Instafix
                nativeApplyInstafix(value);
                break;
            case 2: // Testino
                nativeApplyTestino(value);
                break;
            case 3: // XPro
                nativeApplyXPro(value);
                break;
            case 4: // Retro
                nativeApplyRetro(value);
                break;
            case 5: // Sepia
                nativeApplySepia(value);
                break;
            case 6: // Cyano
                nativeApplyCyano(value);
                break;
            case 7: // Georgia
                nativeApplyGeorgia(value);
                break;
            case 8: // Sahara
                nativeApplySahara(value);
                break;
            case 9: // HDR
                nativeApplyHDR(value);
                break;
            case 10: // Black & White
                nativeApplyBW(value);
                break;
            case 11: // Ansel
                nativeApplyAnsel(value);
                break;
            case 12: // HistEqual
                nativeEqualizeHist(value);
                break;
            case 13: // Threshold
                nativeApplyThreshold(value);
                break;
            case 14: // Grain
                nativeApplyGrain(value);
                break;
        }
        Bitmap filteredBitmap = getBitmapFromNative(bitmap);
        nativeDeleteBitmap();
        return filteredBitmap;
    }

    private static native void nativeApplyFilter(int mode, int val, long inpAddr, long outAddr);
    private static native void nativeEnhanceImage(int mode, int val, long inpAddr, long outAddr);

    private static boolean isEnhance(int effectType) {
        return (effectType/300==1);
    }

    public static native int nativeInitBitmap(int width, int height);

    public static native void nativeGetBitmapRow(int y, int[] pixels);

    public static native void nativeSetBitmapRow(int y, int[] pixels);

    public static native int nativeGetBitmapWidth();

    public static native int nativeGetBitmapHeight();

    public static native void nativeDeleteBitmap();

    public static native void nativeApplyInstafix(int value);
    public static native void nativeApplyAnsel(int value);
    public static native void nativeApplyTestino(int value);
    public static native void nativeApplyXPro(int value);
    public static native void nativeApplyRetro(int value);
    public static native void nativeApplyBW(int value);
    public static native void nativeApplySepia(int value);
    public static native void nativeApplyCyano(int value);
    public static native void nativeApplyGeorgia(int value);
    public static native void nativeApplySahara(int value);
    public static native void nativeApplyHDR(int value);
    public static native void nativeApplyGrain(int value);
    public static native void nativeApplyThreshold(int value);
    public static native void nativeEqualizeHist(int value);

    private static void sendBitmapToNative(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        nativeInitBitmap(width, height);
        int[] pixels = new int[width];
        for (int y = 0; y < height; y++) {
            bitmap.getPixels(pixels, 0, width, 0, y, width, 1);
            nativeSetBitmapRow(y, pixels);
        }
    }

    private static Bitmap getBitmapFromNative(Bitmap bitmap) {
        int width = nativeGetBitmapWidth();
        int height = nativeGetBitmapHeight();

        if (bitmap == null || width != bitmap.getWidth()
                || height != bitmap.getHeight() || !bitmap.isMutable()) { // in
            Config config = Config.ARGB_8888;
            if (bitmap != null) {
                config = bitmap.getConfig();
                bitmap.recycle();

                if (width <= 0 )width = bitmap.getWidth();
                if (height <= 0 )height = bitmap.getHeight();
            }
            bitmap = Bitmap.createBitmap(width, height, config);
        }

        int[] pixels = new int[width];
        for (int y = 0; y < height; y++) {
            nativeGetBitmapRow(y, pixels);
            bitmap.setPixels(pixels, 0, width, 0, y, width, 1);
        }

        return bitmap;
    }

}// end class
