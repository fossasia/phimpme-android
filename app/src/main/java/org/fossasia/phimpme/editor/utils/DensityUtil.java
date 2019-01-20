package org.fossasia.phimpme.editor.utils;

import android.content.Context;

/** Created by panyi on 17/2/11. */
public class DensityUtil {

  /** According to a resolution from the phone dp The unit Turn become px(Pixels) */
  public static int dip2px(Context context, float dpValue) {
    final float scale = context.getResources().getDisplayMetrics().density;
    return (int) (dpValue * scale + 0.5f);
  }

  /** According to a resolution from the phone px(Pixels) The units to be transferred dp */
  public static int px2dip(Context context, float pxValue) {
    final float scale = context.getResources().getDisplayMetrics().density;
    return (int) (pxValue / scale + 0.5f);
  }
}
