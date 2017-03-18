package vn.mbm.phimp.me;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.UserHandle;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.renderscript.ScriptIntrinsicConvolve3x3;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresPermission;
import android.support.annotation.StringDef;
import android.support.annotation.StyleRes;
import android.view.Display;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

import vn.mbm.phimp.me.gallery3d.media.CropImage;

/**
 * Created by madhav on 14/3/17.
 */

public abstract class ImagesFilter extends Context {

    public static Bitmap convertToBW(Bitmap sampleBitmap) {
        ColorMatrix matrix = new ColorMatrix();
        matrix.setSaturation(0);
        ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);
        Bitmap desBitmap = sampleBitmap.copy(Bitmap.Config.ARGB_8888, true);
        Paint p = new Paint();
        p.setColorFilter(filter);
        Canvas canvas = new Canvas(desBitmap);
        canvas.drawBitmap(desBitmap, 0, 0, p);
        return desBitmap;
    }
    public static Bitmap convertToSepia(Bitmap sampleBitmap) {
        ColorMatrix sepiaMatrix = new ColorMatrix();
        float[] sepMat = {0.3930000066757202f, 0.7689999938011169f,
                0.1889999955892563f, 0, 0, 0.3490000069141388f,
                0.6859999895095825f, 0.1679999977350235f, 0, 0,
                0.2720000147819519f, 0.5339999794960022f, 0.1309999972581863f,
                0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1};
        sepiaMatrix.set(sepMat);
        final ColorMatrixColorFilter colorFilter = new ColorMatrixColorFilter(
                sepiaMatrix);
        Bitmap rBitmap = sampleBitmap.copy(Bitmap.Config.ARGB_8888, true);
        Paint paint = new Paint();
        paint.setColorFilter(colorFilter);
        Canvas myCanvas = new Canvas(rBitmap);
        myCanvas.drawBitmap(rBitmap, 0, 0, paint);
        return rBitmap;
    }
    public static Bitmap convertToAlpha(Bitmap sampleBitmap) {
        ColorMatrix sepiaMatrix = new ColorMatrix();
        float[] sepMat = { 0,    0,    0, 0,   0,
                0.3f,    0,    0, 0,  50,
                0,    0,    0, 0, 255,
                0.2f, 0.4f, 0.4f, 0, -30 };
        sepiaMatrix.set(sepMat);
        final ColorMatrixColorFilter colorFilter = new ColorMatrixColorFilter(
                sepiaMatrix);
        Bitmap rBitmap = sampleBitmap.copy(Bitmap.Config.ARGB_8888, true);
        Paint paint = new Paint();
        paint.setColorFilter(colorFilter);
        Canvas myCanvas = new Canvas(rBitmap);
        myCanvas.drawBitmap(rBitmap, 0, 0, paint);
        return rBitmap;
    }
    public static Bitmap convertToPink(Bitmap sampleBitmap) {
        ColorMatrix sepiaMatrix = new ColorMatrix();
        float[] sepMat = { 0,    0,    0, 0, 255,
                0,    0,    0, 0,   0,
                0.2f,    0,    0, 0,  50,
                0.2f, 0.2f, 0.2f, 0, -20 };
        sepiaMatrix.set(sepMat);
        final ColorMatrixColorFilter colorFilter = new ColorMatrixColorFilter(
                sepiaMatrix);
        Bitmap rBitmap = sampleBitmap.copy(Bitmap.Config.ARGB_8888, true);
        Paint paint = new Paint();
        paint.setColorFilter(colorFilter);
        Canvas myCanvas = new Canvas(rBitmap);
        myCanvas.drawBitmap(rBitmap, 0, 0, paint);
        return rBitmap;
    }
    public static Bitmap convertToPolaroid(Bitmap sampleBitmap) {
        ColorMatrix sepiaMatrix = new ColorMatrix();
        float[] sepMat = { 1.438f,-0.062f,-0.062f,0,0,
                0.122f,1.378f,-0.122f,0,0,
                0.016f,-0.016f,1.483f,0,0,
                0 , 0 ,0 , 1 , 0 ,
                0.03f,0.05f,-0.02f,0,1 };
        sepiaMatrix.set(sepMat);
        final ColorMatrixColorFilter colorFilter = new ColorMatrixColorFilter(
                sepiaMatrix);
        Bitmap rBitmap = sampleBitmap.copy(Bitmap.Config.ARGB_8888, true);
        Paint paint = new Paint();
        paint.setColorFilter(colorFilter);
        Canvas myCanvas = new Canvas(rBitmap);
        myCanvas.drawBitmap(rBitmap, 0, 0, paint);
        return rBitmap;
    }
    public static Bitmap converttoBlur(Bitmap original, float radius,Context context) {

        Bitmap bitmap = Bitmap.createBitmap(
                original.getWidth(), original.getHeight(),
                Bitmap.Config.ARGB_8888);

        RenderScript rs = RenderScript.create(context);

        Allocation allocIn = Allocation.createFromBitmap(rs, original);
        Allocation allocOut = Allocation.createFromBitmap(rs, bitmap);

        ScriptIntrinsicBlur blur = ScriptIntrinsicBlur.create(
                rs, Element.U8_4(rs));
        blur.setInput(allocIn);
        blur.setRadius(radius);
        blur.forEach(allocOut);

        allocOut.copyTo(bitmap);
        rs.destroy();
        return bitmap;
    }
    public static Bitmap convertToSharp(Bitmap original,Context context) {
        Bitmap bitmap = Bitmap.createBitmap(
                original.getWidth(), original.getHeight(),
                Bitmap.Config.ARGB_8888);

        RenderScript rs = RenderScript.create(context);

        Allocation allocIn = Allocation.createFromBitmap(rs, original);
        Allocation allocOut = Allocation.createFromBitmap(rs, bitmap);

        float[] coefficients = {  0, -1,  0,
                -1 , 5, -1,
                0, -1,  0  };

        ScriptIntrinsicConvolve3x3 convolution
                = ScriptIntrinsicConvolve3x3.create(rs, Element.U8_4(rs));
        convolution.setInput(allocIn);
        convolution.setCoefficients(coefficients);
        convolution.forEach(allocOut);

        allocOut.copyTo(bitmap);         // {  0, -1,  0,
        rs.destroy();                    //   -1 , 5, -1,
        return bitmap;                   //    0, -1,  0  }
    }
    public static Bitmap convertToEdge(Bitmap original,Context context) {
        Bitmap bitmap = Bitmap.createBitmap(
                original.getWidth(), original.getHeight(),
                Bitmap.Config.ARGB_8888);

        RenderScript rs = RenderScript.create(context);

        Allocation allocIn = Allocation.createFromBitmap(rs, original);
        Allocation allocOut = Allocation.createFromBitmap(rs, bitmap);

        float[] coefficients=  { -1, -1, -1,
                -1 , 8, -1,
                -1, -1, -1  };

        ScriptIntrinsicConvolve3x3 convolution
                = ScriptIntrinsicConvolve3x3.create(rs, Element.U8_4(rs));
        convolution.setInput(allocIn);
        convolution.setCoefficients(coefficients);
        convolution.forEach(allocOut);

        allocOut.copyTo(bitmap);         // { -1, -1, -1,
        rs.destroy();                    //   -1 , 8, -1,
        return bitmap;                   //   -1, -1, -1  }
    }
    public static Bitmap convertToFuzz(Bitmap original,Context context) {
        Bitmap bitmap = Bitmap.createBitmap(
                original.getWidth(), original.getHeight(),
                Bitmap.Config.ARGB_8888);

        RenderScript rs = RenderScript.create(context);

        Allocation allocIn = Allocation.createFromBitmap(rs, original);
        Allocation allocOut = Allocation.createFromBitmap(rs, bitmap);

        float[] coefficients =   {  0,  20/3,  0,
                20/3, -59/3, 20/3,
                1/3,  13/3,  0  };

        ScriptIntrinsicConvolve3x3 convolution
                = ScriptIntrinsicConvolve3x3.create(rs, Element.U8_4(rs));
        convolution.setInput(allocIn);
        convolution.setCoefficients(coefficients);
        convolution.forEach(allocOut);

        allocOut.copyTo(bitmap);         // {  0,  20,  0,
        rs.destroy();                    //   20, -59, 20,
        return bitmap;                   //    1,  13,  0  } / 7
    }
    public static Bitmap applySnowEffect(Bitmap source) {
        // get image size
        int width = source.getWidth();
        int height = source.getHeight();
        int[] pixels = new int[width * height];
        // get pixel array from source
        source.getPixels(pixels, 0, width, 0, 0, width, height);
        // random object
        Random random = new Random();

        int R, G, B, index = 0, thresHold = 20;
        // iteration through pixels
        for(int y = 0; y < height; ++y) {
            for(int x = 0; x < width; ++x) {
                // get current index in 2D-matrix
                index = y * width + x;
                // get color
                R = Color.red(pixels[index]);
                G = Color.green(pixels[index]);
                B = Color.blue(pixels[index]);
                // generate threshold
                thresHold = random.nextInt(0xFF);
                if(R > thresHold && G > thresHold && B > thresHold) {
                    pixels[index] = Color.rgb(0xFF, 0xFF, 0xFF);
                }
            }
        }
        // output bitmap
        Bitmap bmOut = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        bmOut.setPixels(pixels, 0, width, 0, 0, width, height);
        return bmOut;
    }
    public static Bitmap roundCorner(Bitmap src, float round) {
        // image size
        int width = src.getWidth();
        int height = src.getHeight();
        // create bitmap output
        Bitmap result = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        // set canvas for painting
        Canvas canvas = new Canvas(result);
        canvas.drawARGB(0, 0, 0, 0);

        // config paint
        final Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.BLACK);

        // config rectangle for embedding
        final Rect rect = new Rect(0, 0, width, height);
        final RectF rectF = new RectF(rect);

        // draw rect to canvas
        canvas.drawRoundRect(rectF, round, round, paint);

        // create Xfer mode
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        // draw source image to canvas
        canvas.drawBitmap(src, rect, rect, paint);

        // return final image
        return result;
    }
    // This method is originally from this site:
    // http://android-code-space.blogspot.com/2010/08/convert-image-to-negative-in-android.html
    public static Bitmap convertToNegative(Bitmap sampleBitmap) {
        ColorMatrix negativeMatrix = new ColorMatrix();
        float[] negMat = { -1, 0, 0, 0, 255, 0, -1, 0, 0, 255, 0, 0, -1, 0,
                255, 0, 0, 0, 1, 0 };
        negativeMatrix.set(negMat);
        final ColorMatrixColorFilter colorFilter = new ColorMatrixColorFilter(
                negativeMatrix);
        Bitmap rBitmap = sampleBitmap.copy(Bitmap.Config.ARGB_8888, true);
        Paint paint = new Paint();
        paint.setColorFilter(colorFilter);
        Canvas myCanvas = new Canvas(rBitmap);
        myCanvas.drawBitmap(rBitmap, 0, 0, paint);
        return rBitmap;
    }
}
