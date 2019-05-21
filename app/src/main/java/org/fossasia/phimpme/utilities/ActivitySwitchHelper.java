package org.fossasia.phimpme.utilities;

import android.content.Context;

/** Created by manuja on 7/6/17. */
public class ActivitySwitchHelper {
  public static Context context;

  public static Context getContext() {
    return context;
  }

  public static void setContext(Context context) {
    ActivitySwitchHelper.context = context;
  }
}
