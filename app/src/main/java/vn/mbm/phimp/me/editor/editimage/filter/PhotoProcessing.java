package vn.mbm.phimp.me.editor.editimage.filter;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;


/**
 * 图片处理类
 *
 * @author 潘易
 */
public class PhotoProcessing {
    private static final String TAG = "PhotoProcessing";

    // /////////////////////////////////////////////
    static {
        System.loadLibrary("photoprocessing");
    }


    public static Bitmap processImage(Bitmap srcBitmap, int effectType, int val) {
        if(!isEnhance(effectType))
            return filterPhoto(srcBitmap,effectType % 100, val);
        else
            return tunePhoto(srcBitmap,effectType % 100, val);
    }

    public static Bitmap filterPhoto(Bitmap bitmap, int position,int value) {
        if (bitmap != null) {
            sendBitmapToNative(bitmap);
        }
        switch (position) {
            case 0: // Original
                break;
            case 1: // Instafix
                nativeApplyInstafix(value);
                break;
            case 2: // Ansel
                nativeApplyAnsel(value);
                break;
            case 3: // Testino
                nativeApplyTestino(value);
                break;
            case 4: // XPro
                nativeApplyXPro(value);
                break;
            case 5: // Retro
                nativeApplyRetro(value);
                break;
            case 6: // Black & White
                nativeApplyBW(value);
                break;
            case 7: // Sepia
                nativeApplySepia(value);
                break;
            case 8: // Cyano
                nativeApplyCyano(value);
                break;
            case 9: // Georgia
                nativeApplyGeorgia(value);
                break;
            case 10: // Sahara
                nativeApplySahara(value);
                break;
            case 11: // HDR
                nativeApplyHDR(value);
                break;
        }
        Bitmap filteredBitmap = getBitmapFromNative(bitmap);
        nativeDeleteBitmap();
        return filteredBitmap;
    }

    private static Bitmap tunePhoto(Bitmap bitmap, int mode, int val) {
        if (bitmap != null) {
            sendBitmapToNative(bitmap);
        }
        switch (mode) {
            case 0:
                nativeTuneBrightness(val);
                break;
            case 1:
                nativeTuneContrast(val);
                break;
            case 2:
                nativeTuneHue(val);
                break;
            case 3:
                nativeTuneSaturation(val);
                break;
            case 4:
                nativeTuneTemperature(val);
                break;
            case 5:
                nativeTuneTint(val);
                break;
            case 6:
                nativeTuneVignette(val);
                break;
            case 7:
                nativeTuneSharpen(val);
                break;
            case 8:
                nativeTuneBlur(val);
                break;
        }
        Bitmap filteredBitmap = getBitmapFromNative(bitmap);
        nativeDeleteBitmap();
        return filteredBitmap;
    }

    private static boolean isEnhance(int effectType) {
        return (effectType/300==1);
    }


    public static native int nativeInitBitmap(int width, int height);

    public static native void nativeGetBitmapRow(int y, int[] pixels);

    public static native void nativeSetBitmapRow(int y, int[] pixels);

    public static native int nativeGetBitmapWidth();

    public static native int nativeGetBitmapHeight();

    public static native void nativeDeleteBitmap();

    public static native int nativeRotate90();

    public static native void nativeRotate180();

    public static native void nativeFlipHorizontally();

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

    public static native void nativeLoadResizedJpegBitmap(byte[] jpegData,
                                                          int size, int maxPixels);

    public static native void nativeTuneBrightness(int val);
    public static native void nativeTuneContrast(int val);
    public static native void nativeTuneHue(int val);
    public static native void nativeTuneSaturation(int val);
    public static native void nativeTuneTemperature(int val);
    public static native void nativeTuneVignette(int val);
    public static native void nativeTuneSharpen(int val);
    public static native void nativeTuneBlur(int val);
    public static native void nativeTuneTint(int val);

    public static native void nativeResizeBitmap(int newWidth, int newHeight);

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

    public static Bitmap makeBitmapMutable(Bitmap bitmap) {
        sendBitmapToNative(bitmap);
        return getBitmapFromNative(bitmap);
    }

    public static Bitmap rotate(Bitmap bitmap, int angle) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        Config config = bitmap.getConfig();
        nativeInitBitmap(width, height);
        sendBitmapToNative(bitmap);

        if (angle == 90) {
            nativeRotate90();
            bitmap.recycle();
            bitmap = Bitmap.createBitmap(height, width, config);
            bitmap = getBitmapFromNative(bitmap);
            nativeDeleteBitmap();
        } else if (angle == 180) {
            nativeRotate180();
            bitmap.recycle();
            bitmap = Bitmap.createBitmap(width, height, config);
            bitmap = getBitmapFromNative(bitmap);
            nativeDeleteBitmap();
        } else if (angle == 270) {
            nativeRotate180();
            nativeRotate90();
            bitmap.recycle();
            bitmap = Bitmap.createBitmap(height, width, config);
            bitmap = getBitmapFromNative(bitmap);
            nativeDeleteBitmap();
        }
        return bitmap;
    }

    public static Bitmap flipHorizontally(Bitmap bitmap) {
        nativeInitBitmap(bitmap.getWidth(), bitmap.getHeight());
        sendBitmapToNative(bitmap);
        nativeFlipHorizontally();
        bitmap = getBitmapFromNative(bitmap);
        nativeDeleteBitmap();
        return bitmap;
    }

}// end class
