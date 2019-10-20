package org.fossasia.phimpme.gallery.util;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

/** Created by dnld on 01/04/16. */
public final class PermissionUtils {

  public static boolean checkPermission(Context context, String permission) {
    return ContextCompat.checkSelfPermission(context, permission)
        == PackageManager.PERMISSION_GRANTED;
  }

  public static boolean isDeviceInfoGranted(Context context) {
    return checkPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE);
  }

  public static void requestPermissions(
      Activity activity, int permissionId, String... permissions) {
    ActivityCompat.requestPermissions(activity, permissions, permissionId);
  }

  public static boolean shouldShowPermissionRationale(Activity activity, String permission) {
    return ActivityCompat.shouldShowRequestPermissionRationale(activity, permission);
  }
}
