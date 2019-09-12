package org.fossasia.phimpme.gallery.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.view.View;
import androidx.renderscript.Allocation;
import androidx.renderscript.Element;
import androidx.renderscript.RenderScript;
import androidx.renderscript.ScriptIntrinsicBlur;

/** Created by mayank on 3/11/17. */
public class BlurImageUtil {
  private static final float BITMAP_SCALE = 0.1f;
  private static final float BLUR_RADIUS = 7.5f;

  public static Bitmap blur(View v) {
    return blur(v.getContext(), getScreenshot(v));
  }

  public static Bitmap blur(Context ctx, Bitmap image) {
    int width = Math.round(image.getWidth() * BITMAP_SCALE);
    int height = Math.round(image.getHeight() * BITMAP_SCALE);

    Bitmap inputBitmap = Bitmap.createScaledBitmap(image, width, height, false);
    Bitmap outputBitmap = Bitmap.createBitmap(inputBitmap);

    RenderScript rs = RenderScript.create(ctx);
    ScriptIntrinsicBlur theIntrinsic = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
    Allocation tmpIn = Allocation.createFromBitmap(rs, inputBitmap);
    Allocation tmpOut = Allocation.createFromBitmap(rs, outputBitmap);
    theIntrinsic.setRadius(BLUR_RADIUS);
    theIntrinsic.setInput(tmpIn);
    theIntrinsic.forEach(tmpOut);
    tmpOut.copyTo(outputBitmap);

    return outputBitmap;
  }

  private static Bitmap getScreenshot(View v) {
    Bitmap b = Bitmap.createBitmap(v.getWidth(), v.getHeight(), Bitmap.Config.ARGB_8888);
    Canvas c = new Canvas(b);
    v.draw(c);
    return b;
  }
}
