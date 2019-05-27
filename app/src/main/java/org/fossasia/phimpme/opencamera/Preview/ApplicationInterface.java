package org.fossasia.phimpme.opencamera.Preview;

import android.content.Context;
import android.graphics.Canvas;
import android.hardware.camera2.DngCreator;
import android.location.Location;
import android.media.Image;
import android.util.Pair;
import android.view.MotionEvent;
import java.util.Date;
import java.util.List;

/**
 * Provides communication between the Preview and the rest of the application - so in theory one can
 * drop the Preview/ (and CameraController/) classes into a new application, by providing an
 * appropriate implementation of this ApplicationInterface.
 */
public interface ApplicationInterface {
  class NoFreeStorageException extends Exception {
    private static final long serialVersionUID = -2021932609486148748L;
  }

  // methods that request information
  Context getContext(); // get the application context

  boolean useCamera2(); // should Android 5's Camera 2 API be used?

  Location getLocation(); // get current location - null if not available (or you don't care about
  // geotagging)
  // for all of the get*Pref() methods, you can use Preview methods to get the supported values
  // (e.g., getSupportedSceneModes())
  // if you just want a default or don't really care, see the comments for each method for a default
  // or possible options
  // if Preview doesn't support the requested setting, it will check this, and choose its own
  int getCameraIdPref(); // camera to use, from 0 to
  // getCameraControllerManager().getNumberOfCameras()

  String getFlashPref(); // flash_off, flash_auto, flash_on, flash_torch, flash_red_eye

  String getFocusPref(
      boolean
          is_video); // focus_mode_auto, focus_mode_infinity, focus_mode_macro, focus_mode_locked,
  // focus_mode_fixed, focus_mode_manual2, focus_mode_edof,
  // focus_mode_continuous_video

  String getSceneModePref(); // "auto" for default (strings correspond to Android's scene mode
  // constants in android.hardware.Camera.Parameters)

  String getColorEffectPref(); // "node" for default (strings correspond to Android's color effect
  // constants in android.hardware.Camera.Parameters)

  String getWhiteBalancePref(); // "auto" for default (strings correspond to Android's white balance
  // constants in android.hardware.Camera.Parameters)

  int getWhiteBalanceTemperaturePref();

  String getISOPref(); // "auto" for auto-ISO, otherwise a numerical value; see documentation for
  // Preview.supportsISORange().

  int getExposureCompensationPref(); // 0 for default

  Pair<Integer, Integer> getCameraResolutionPref(); // return null to let Preview choose size

  int getImageQualityPref(); // jpeg quality for taking photos; "90" is a recommended default

  boolean getFaceDetectionPref(); // whether to use face detection mode

  String getPreviewSizePref(); // "preference_preview_size_wysiwyg" is recommended (preview matches
  // aspect ratio of photo resolution as close as possible), but can also
  // be "preference_preview_size_display" to maximise the preview size

  String getPreviewRotationPref(); // return "0" for default; use "180" to rotate the preview 180
  // degrees

  String
      getLockOrientationPref(); // return "none" for default; use "portrait" or "landscape" to lock
  // photos/videos to that orientation

  boolean getTouchCapturePref(); // whether to enable touch to capture

  boolean getDoubleTapCapturePref(); // whether to enable double-tap to capture

  boolean getPausePreviewPref(); // whether to pause the preview after taking a photo

  boolean getShowToastsPref();

  boolean getStartupFocusPref(); // whether to do autofocus on startup

  long getTimerPref(); // time in ms for timer (so 0 for off)

  String
      getRepeatPref(); // return number of times to repeat photo in a row (as a string), so "1" for
  // default; return "unlimited" for unlimited

  long getRepeatIntervalPref(); // time in ms between repeat

  boolean getGeotaggingPref(); // whether to geotag photos

  boolean getRequireLocationPref(); // if getGeotaggingPref() returns true, and this method returns
  // true, then phot/video will only be taken if location data is
  // available

  boolean getRecordAudioPref(); // whether to record audio when recording video

  String getRecordAudioChannelsPref(); // either "audio_default", "audio_mono" or "audio_stereo"

  String getRecordAudioSourcePref(); // "audio_src_camcorder" is recommended, but other options are:
  // "audio_src_mic", "audio_src_default",
  // "audio_src_voice_communication"; see corresponding values in
  // android.media.MediaRecorder.AudioSource

  int getZoomPref(); // index into Preview.getSupportedZoomRatios() array (each entry is the zoom
  // factor, scaled by 100; array is sorted from min to max zoom)

  double getCalibratedLevelAngle(); // set to non-zero to calibrate the accelerometer used for the
  // level angles
  // Camera2 only modes:
  long getExposureTimePref(); // only called if getISOPref() is not "default"

  float getFocusDistancePref();

  boolean isExpoBracketingPref(); // whether to enable burst photos with expo bracketing

  int getExpoBracketingNImagesPref(); // how many images to take for exposure bracketing

  double getExpoBracketingStopsPref(); // stops per image for exposure bracketing

  boolean getOptimiseAEForDROPref(); // see CameraController doc for setOptimiseAEForDRO().

  boolean isRawPref(); // whether to enable RAW photos

  boolean useCamera2FakeFlash(); // whether to enable CameraController.setUseCamera2FakeFlash() for
  // Camera2 API

  boolean useCamera2FastBurst(); // whether to enable Camera2's captureBurst() for faster taking of
  // expo-bracketing photos (generally should be true, but some devices
  // have problems with captureBurst())

  // for testing purposes:
  boolean isTestAlwaysFocus(); // if true, pretend autofocus always successful

  // methods that transmit information/events (up to the Application whether to do anything or not)
  void
      cameraSetup(); // called when the camera is (re-)set up - should update UI elements/parameters
  // that depend on camera settings

  void touchEvent(MotionEvent event);

  void onFailedStartPreview(); // called if failed to start camera preview

  void onCameraError(); // called if the camera closes due to serious error.

  void onPhotoError(); // callback for failing to take a photo

  void onFailedReconnectError(); // failed to reconnect camera after stopping video recording

  void hasPausedPreview(
      boolean
          paused); // called when the preview is paused or unpaused (due to getPausePreviewPref())

  void cameraInOperation(
      boolean
          in_operation); // called when the camera starts/stops being operation (taking photos or
  // recording video, including if preview is paused after taking a photo),
  // use to disable GUI elements during camera operation

  void turnFrontScreenFlashOn(); // called when front-screen "flash" required (for modes
  // flash_frontscreen_auto, flash_frontscreen_on); the application
  // should light up the screen, until cameraInOperation(false) is
  // called

  void cameraClosed();

  void timerBeep(
      long remaining_time); // n.b., called once per second on timer countdown - so application can
  // beep, or do whatever it likes

  // methods that request actions
  void layoutUI(); // application should layout UI that's on top of the preview

  void multitouchZoom(int new_zoom); // zoom has changed due to multitouch gesture on preview
  // the set/clear*Pref() methods are called if Preview decides to override the requested pref
  // (because Camera device doesn't support requested pref) (clear*Pref() is called if the feature
  // isn't supported at all)
  // the application can use this information to update its preferences
  void setCameraIdPref(int cameraId);

  void setFlashPref(String flash_value);

  void setFocusPref(String focus_value, boolean is_video);

  void setSceneModePref(String scene_mode);

  void clearSceneModePref();

  void setColorEffectPref(String color_effect);

  void clearColorEffectPref();

  void setWhiteBalancePref(String white_balance);

  void clearWhiteBalancePref();

  void setWhiteBalanceTemperaturePref(int white_balance_temperature);

  void setISOPref(String iso);

  void clearISOPref();

  void setExposureCompensationPref(int exposure);

  void clearExposureCompensationPref();

  void setCameraResolutionPref(int width, int height);

  void setZoomPref(int zoom);

  void requestCameraPermission(); // for Android 6+: called when trying to open camera, but CAMERA
  // permission not available

  void requestStoragePermission(); // for Android 6+: called when trying to open camera, but
  // WRITE_EXTERNAL_STORAGE permission not available

  void requestRecordAudioPermission(); // for Android 6+: called when switching to (or starting up
  // in) video mode, but RECORD_AUDIO permission not available
  // Camera2 only modes:
  void setExposureTimePref(long exposure_time);

  void clearExposureTimePref();

  void setFocusDistancePref(float focus_distance);

  // callbacks
  void onDrawPreview(Canvas canvas);

  boolean onPictureTaken(byte[] data, Date current_date);

  boolean onBurstPictureTaken(List<byte[]> images, Date current_date);

  boolean onRawPictureTaken(DngCreator dngCreator, Image image, Date current_date);

  void onCaptureStarted(); // called immediately before we start capturing the picture

  void onPictureCompleted(); // called after all picture callbacks have been called and returned

  void onContinuousFocusMove(
      boolean start); // called when focusing starts/stop in continuous picture mode (in photo mode
  // only)
}
