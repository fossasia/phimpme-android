package org.fossasia.phimpme.opencamera.CameraController;

import android.annotation.TargetApi;
import android.content.Context;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.os.Build;
import android.util.Log;
import org.fossasia.phimpme.opencamera.Camera.MyDebug;

/** Provides support using Android 5's Camera 2 API android.hardware.camera2.*. */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class CameraControllerManager2 extends CameraControllerManager {
  private static final String TAG = "CControllerManager2";

  private final Context context;

  public CameraControllerManager2(Context context) {
    this.context = context;
  }

  @Override
  public int getNumberOfCameras() {
    CameraManager manager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
    try {
      return manager.getCameraIdList().length;
    } catch (CameraAccessException e) {
      if (MyDebug.LOG) Log.e(TAG, "exception trying to get camera ids");
      e.printStackTrace();
    } catch (AssertionError e) {
      // had reported java.lang.AssertionError on Google Play, "Expected to get non-empty
      // characteristics" from CameraManager.getOrCreateDeviceIdListLocked(CameraManager.java:465)
      // yes, in theory we shouldn't catch AssertionError as it represents a programming error,
      // however it's a programming error in the camera driver (a condition they thought couldn't
      // happen)
      if (MyDebug.LOG) Log.e(TAG, "assertion error trying to get camera ids");
      e.printStackTrace();
    }
    return 0;
  }

  @Override
  public boolean isFrontFacing(int cameraId) {
    CameraManager manager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
    try {
      String cameraIdS = manager.getCameraIdList()[cameraId];
      CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraIdS);
      return characteristics.get(CameraCharacteristics.LENS_FACING)
          == CameraMetadata.LENS_FACING_FRONT;
    } catch (CameraAccessException e) {
      if (MyDebug.LOG) Log.e(TAG, "exception trying to get camera characteristics");
      e.printStackTrace();
    }
    return false;
  }

  /* Returns true if the device supports the required hardware level, or better.
   * From http://msdx.github.io/androiddoc/docs//reference/android/hardware/camera2/CameraCharacteristics.html#INFO_SUPPORTED_HARDWARE_LEVEL
   * From Android N, higher levels than "FULL" are possible, that will have higher integer values.
   * Also see https://sourceforge.net/p/opencamera/tickets/141/ .
   */
  private boolean isHardwareLevelSupported(CameraCharacteristics c, int requiredLevel) {
    int deviceLevel = c.get(CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL);
    if (MyDebug.LOG) {
      if (deviceLevel == CameraMetadata.INFO_SUPPORTED_HARDWARE_LEVEL_LEGACY)
        Log.d(TAG, "Camera has LEGACY Camera2 support");
      else if (deviceLevel == CameraMetadata.INFO_SUPPORTED_HARDWARE_LEVEL_LIMITED)
        Log.d(TAG, "Camera has LIMITED Camera2 support");
      else if (deviceLevel == CameraMetadata.INFO_SUPPORTED_HARDWARE_LEVEL_FULL)
        Log.d(TAG, "Camera has FULL Camera2 support");
      else Log.d(TAG, "Camera has unknown Camera2 support: " + deviceLevel);
    }
    if (deviceLevel == CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_LEGACY) {
      return requiredLevel == deviceLevel;
    }
    // deviceLevel is not LEGACY, can use numerical sort
    return requiredLevel <= deviceLevel;
  }

  /* Rather than allowing Camera2 API on all Android 5+ devices, we restrict it to cases where all cameras have at least LIMITED support.
   * (E.g., Nexus 6 has FULL support on back camera, LIMITED support on front camera.)
   * For now, devices with only LEGACY support should still with the old API.
   */
  public boolean allowCamera2Support(int cameraId) {
    CameraManager manager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
    try {
      String cameraIdS = manager.getCameraIdList()[cameraId];
      CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraIdS);
      return isHardwareLevelSupported(
          characteristics, CameraMetadata.INFO_SUPPORTED_HARDWARE_LEVEL_LIMITED);
    } catch (Exception e) {
      // in theory we should only get CameraAccessException, but Google Play shows we can get a
      // variety of exceptions
      // from some devices, e.g., NumberFormatException, NullPointerException, so just catch
      // everything!
      if (MyDebug.LOG) Log.e(TAG, "exception trying to get camera characteristics");
      e.printStackTrace();
    }
    return false;
  }
}
