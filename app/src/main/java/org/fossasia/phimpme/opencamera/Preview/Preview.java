package org.fossasia.phimpme.opencamera.Preview;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;
import static org.fossasia.phimpme.opencamera.Camera.CameraActivity.toggle;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.hardware.camera2.DngCreator;
import android.location.Location;
import android.media.CamcorderProfile;
import android.media.Image;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.util.Pair;
import android.view.Display;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.OrientationEventListener;
import android.view.ScaleGestureDetector;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.TextureView;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import org.fossasia.phimpme.R;
import org.fossasia.phimpme.opencamera.Camera.CameraActivity;
import org.fossasia.phimpme.opencamera.Camera.MyDebug;
import org.fossasia.phimpme.opencamera.Camera.TakePhoto;
import org.fossasia.phimpme.opencamera.Camera.ToastBoxer;
import org.fossasia.phimpme.opencamera.CameraController.CameraController;
import org.fossasia.phimpme.opencamera.CameraController.CameraController1;
import org.fossasia.phimpme.opencamera.CameraController.CameraController2;
import org.fossasia.phimpme.opencamera.CameraController.CameraControllerException;
import org.fossasia.phimpme.opencamera.CameraController.CameraControllerManager;
import org.fossasia.phimpme.opencamera.CameraController.CameraControllerManager1;
import org.fossasia.phimpme.opencamera.CameraController.CameraControllerManager2;
import org.fossasia.phimpme.opencamera.Preview.CameraSurface.CameraSurface;
import org.fossasia.phimpme.opencamera.Preview.CameraSurface.MySurfaceView;
import org.fossasia.phimpme.opencamera.Preview.CameraSurface.MyTextureView;
import org.fossasia.phimpme.opencamera.UI.PopupView;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;

/**
 * This class was originally named due to encapsulating the camera preview, but in practice it's
 * grown to more than this, and includes most of the operation of the camera. It exists at a higher
 * level than CameraController (i.e., this isn't merely a low level wrapper to the camera API, but
 * supports much of the Open Camera logic and functionality). Communication to the rest of the
 * application is available through ApplicationInterface. We could probably do with decoupling this
 * class into separate components!
 */
public class Preview implements SurfaceHolder.Callback, TextureView.SurfaceTextureListener {
  private static final String TAG = "Preview";

  private final boolean using_android_l;

  private final ApplicationInterface applicationInterface;
  private final CameraSurface cameraSurface;
  private CanvasView canvasView;
  private boolean set_preview_size;
  private int preview_w, preview_h;
  private boolean set_textureview_size;
  private int textureview_w, textureview_h;

  private final Matrix camera_to_preview_matrix = new Matrix();
  private final Matrix preview_to_camera_matrix = new Matrix();
  // private RectF face_rect = new RectF();
  private double preview_targetRatio;

  // private boolean ui_placement_right = true;

  private boolean app_is_paused = true;
  private boolean has_surface;
  private boolean has_aspect_ratio;
  private double aspect_ratio;
  private final CameraControllerManager camera_controller_manager;
  private CameraController camera_controller;
  private boolean has_permissions =
      true; // whether we have permissions necessary to operate the camera (camera, storage); assume
  // true until we've been denied one of them
  private boolean is_video;
  private volatile MediaRecorder
      video_recorder; // must be volatile for test project reading the state
  private volatile boolean
      video_start_time_set; // must be volatile for test project reading the state
  private long
      video_start_time; // when the video recording was started, or last resumed if it's was paused
  private long video_accumulated_time; // this time should be added to (System.currentTimeMillis() -
  // video_start_time) to find the true video duration, that takes into
  // account pausing/resuming, as well as any auto-restarts from max
  // filesize
  private boolean video_recorder_is_paused; // whether video_recorder is running but has paused

  private static final int PHASE_NORMAL = 0;
  private static final int PHASE_TIMER = 1;
  private static final int PHASE_TAKING_PHOTO = 2;
  private static final int PHASE_PREVIEW_PAUSED = 3; // the paused state after taking a photo
  private volatile int phase = PHASE_NORMAL; // must be volatile for test project reading the state
  private final Timer takePictureTimer = new Timer();
  private TimerTask takePictureTimerTask;
  private final Timer beepTimer = new Timer();
  private TimerTask beepTimerTask;
  private long take_photo_time;
  private int remaining_burst_photos;

  private boolean is_preview_started;

  private int current_orientation; // orientation received by onOrientationChanged
  private int current_rotation; // orientation relative to camera's orientation (used for
  // parameters.setRotation())
  private boolean has_level_angle;
  private double
      natural_level_angle; // "level" angle of device, before applying any calibration and without
  // accounting for screen orientation
  private double level_angle; // "level" angle of device, including calibration
  private double
      orig_level_angle; // "level" angle of device, including calibration, but without accounting
  // for screen orientation
  private boolean has_pitch_angle;
  private double pitch_angle;

  private boolean has_zoom;
  private int max_zoom_factor;
  private final GestureDetector gestureDetector;
  private final ScaleGestureDetector scaleGestureDetector;
  private List<Integer> zoom_ratios;
  private float minimum_focus_distance;
  private boolean touch_was_multitouch;
  private float touch_orig_x;
  private float touch_orig_y;

  private List<String> supported_flash_values; // our "values" format
  private int current_flash_index =
      -1; // this is an index into the supported_flash_values array, or -1 if no flash modes
  // available

  private List<String> supported_focus_values; // our "values" format
  private int current_focus_index =
      -1; // this is an index into the supported_focus_values array, or -1 if no focus modes
  // available
  private int max_num_focus_areas;
  private boolean continuous_focus_move_is_started;

  private boolean is_exposure_lock_supported;
  private boolean is_exposure_locked;

  private List<String> color_effects;
  private List<String> scene_modes;
  private List<String> white_balances;
  private List<String> isos;
  private boolean supports_white_balance_temperature;
  private int min_temperature;
  private int max_temperature;
  private boolean supports_iso_range;
  private int min_iso;
  private int max_iso;
  private boolean supports_exposure_time;
  private long min_exposure_time;
  private long max_exposure_time;
  private List<String> exposures;
  private int min_exposure;
  private int max_exposure;
  private float exposure_step;
  private boolean supports_expo_bracketing;
  private int max_expo_bracketing_n_images;
  private boolean supports_raw;
  private float view_angle_x;
  private float view_angle_y;

  private List<CameraController.Size> supported_preview_sizes;

  private List<CameraController.Size> sizes;
  private int current_size_index =
      -1; // this is an index into the sizes array, or -1 if sizes not yet set

  private Toast last_toast;
  private final ToastBoxer flash_toast = new ToastBoxer();
  private final ToastBoxer focus_toast = new ToastBoxer();
  private final ToastBoxer take_photo_toast = new ToastBoxer();
  private final ToastBoxer seekbar_toast = new ToastBoxer();

  private int ui_rotation;

  private boolean supports_face_detection;
  private boolean using_face_detection;
  private CameraController.Face[] faces_detected;
  private boolean supports_video_stabilization;
  private boolean can_disable_shutter_sound;
  private boolean has_focus_area;
  private int focus_screen_x;
  private int focus_screen_y;
  private long focus_complete_time = -1;
  private long focus_started_time = -1;
  private int focus_success = FOCUS_DONE;
  private static final int FOCUS_WAITING = 0;
  private static final int FOCUS_SUCCESS = 1;
  private static final int FOCUS_FAILED = 2;
  private static final int FOCUS_DONE = 3;
  private String set_flash_value_after_autofocus = "";
  private boolean
      take_photo_after_autofocus; // set to take a photo when the in-progress autofocus has
  // completed; if setting, remember to call
  // camera_controller.setCaptureFollowAutofocusHint()
  private boolean successfully_focused;
  private long successfully_focused_time = -1;
  private static final String SHOWCASE_ID = "1";

  // accelerometer and geomagnetic sensor info
  private static final float sensor_alpha = 0.8f; // for filter
  private boolean has_gravity;
  private final float[] gravity = new float[3];
  private boolean has_geomagnetic;
  private final float[] geomagnetic = new float[3];
  private final float[] deviceRotation = new float[9];
  private final float[] cameraRotation = new float[9];
  private final float[] deviceInclination = new float[9];
  private boolean has_geo_direction;
  private final float[] geo_direction = new float[3];
  private final float[] new_geo_direction = new float[3];

  private final DecimalFormat decimal_format_1dp = new DecimalFormat("#.#");
  private final DecimalFormat decimal_format_2dp = new DecimalFormat("#.##");

  /* If the user touches to focus in continuous mode, we switch the camera_controller to autofocus mode.
   * autofocus_in_continuous_mode is set to true when this happens; the runnable reset_continuous_focus_runnable
   * switches back to continuous mode.
   */
  private final Handler reset_continuous_focus_handler = new Handler();
  private Runnable reset_continuous_focus_runnable;
  private boolean autofocus_in_continuous_mode;

  // for testing; must be volatile for test project reading the state
  private boolean is_test; // whether called from OpenCamera.test testing
  public volatile int count_cameraStartPreview;
  public volatile int count_cameraAutoFocus;
  public volatile int count_cameraTakePicture;
  public volatile int count_cameraContinuousFocusMoving;
  public volatile boolean test_fail_open_camera;
  public volatile boolean test_ticker_called; // set from MySurfaceView or CanvasView

  private boolean enable_sound;
  private int colorNum = 0;

  public Preview(ApplicationInterface applicationInterface, ViewGroup parent) {
    if (MyDebug.LOG) {
      Log.d(TAG, "new Preview");
    }

    this.applicationInterface = applicationInterface;

    Activity activity = (Activity) this.getContext();
    if (activity.getIntent() != null && activity.getIntent().getExtras() != null) {
      // whether called from testing
      is_test = activity.getIntent().getExtras().getBoolean("test_project");
      if (MyDebug.LOG) Log.d(TAG, "is_test: " + is_test);
    }

    this.using_android_l =
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && applicationInterface.useCamera2();
    if (MyDebug.LOG) {
      Log.d(TAG, "using_android_l?: " + using_android_l);
    }

    boolean using_texture_view = false;
    if (using_android_l) {
      // use a TextureView for Android L - had bugs with SurfaceView not resizing properly on Nexus
      // 7; and good to use a TextureView anyway
      // ideally we'd use a TextureView for older camera API too, but sticking with SurfaceView to
      // avoid risk of breaking behaviour
      using_texture_view = true;
    }

    if (using_texture_view) {
      this.cameraSurface = new MyTextureView(getContext(), this);
      // a TextureView can't be used both as a camera preview, and used for drawing on, so we use a
      // separate CanvasView
      this.canvasView = new CanvasView(getContext(), this);
      camera_controller_manager = new CameraControllerManager2(getContext());
    } else {
      this.cameraSurface = new MySurfaceView(getContext(), this);
      camera_controller_manager = new CameraControllerManager1();
    }

    gestureDetector =
        new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener());
    gestureDetector.setOnDoubleTapListener(new DoubleTapListener());
    scaleGestureDetector = new ScaleGestureDetector(getContext(), new ScaleListener());

    parent.addView(cameraSurface.getView());
    if (canvasView != null) {
      parent.addView(canvasView);
    }
  }

  /*private void previewToCamera(float [] coords) {
  	float alpha = coords[0] / (float)this.getWidth();
  	float beta = coords[1] / (float)this.getHeight();
  	coords[0] = 2000.0f * alpha - 1000.0f;
  	coords[1] = 2000.0f * beta - 1000.0f;
  }*/

  /*private void cameraToPreview(float [] coords) {
  	float alpha = (coords[0] + 1000.0f) / 2000.0f;
  	float beta = (coords[1] + 1000.0f) / 2000.0f;
  	coords[0] = alpha * (float)this.getWidth();
  	coords[1] = beta * (float)this.getHeight();
  }*/

  private Resources getResources() {
    return cameraSurface.getView().getResources();
  }

  public View getView() {
    return cameraSurface.getView();
  }

  // If this code is changed, important to test that face detection and touch to focus still works
  // as expected, for front and back
  // cameras, for old and new API, including with zoom. Also test with
  // CameraActivity.setWindowFlagsForCamera() setting orientation as
  // SCREEN_ORIENTATION_REVERSE_LANDSCAPE,
  // and/or set "Rotate preview" option to 180 degrees.
  private void calculateCameraToPreviewMatrix() {
    if (MyDebug.LOG) Log.d(TAG, "calculateCameraToPreviewMatrix");
    if (camera_controller == null) return;
    camera_to_preview_matrix.reset();
    if (!using_android_l) {
      // from http://developer.android.com/reference/android/hardware/Camera.Face.html#rect
      // Need mirror for front camera
      boolean mirror = camera_controller.isFrontFacing();
      camera_to_preview_matrix.setScale(mirror ? -1 : 1, 1);
      // This is the value for android.hardware.Camera.setDisplayOrientation.
      int display_orientation = camera_controller.getDisplayOrientation();
      if (MyDebug.LOG) {
        Log.d(TAG, "orientation of display relative to camera orientaton: " + display_orientation);
      }
      camera_to_preview_matrix.postRotate(display_orientation);
    } else {
      // Unfortunately the transformation for Android L API isn't documented, but this seems to work
      // for Nexus 6.
      // This is the equivalent code for android.hardware.Camera.setDisplayOrientation, but we don't
      // actually use setDisplayOrientation()
      // for CameraController2, so instead this is the equivalent code to
      // https://developer.android.com/reference/android/hardware/Camera.html#setDisplayOrientation(int),
      // except testing on Nexus 6 shows that we shouldn't change "result" for front facing camera.
      boolean mirror = camera_controller.isFrontFacing();
      camera_to_preview_matrix.setScale(1, mirror ? -1 : 1);
      int degrees = getDisplayRotationDegrees();
      int result = (camera_controller.getCameraOrientation() - degrees + 360) % 360;
      if (MyDebug.LOG) {
        Log.d(TAG, "orientation of display relative to natural orientaton: " + degrees);
        Log.d(TAG, "orientation of display relative to camera orientaton: " + result);
      }
      camera_to_preview_matrix.postRotate(result);
    }
    // Camera driver coordinates range from (-1000, -1000) to (1000, 1000).
    // UI coordinates range from (0, 0) to (width, height).
    camera_to_preview_matrix.postScale(
        cameraSurface.getView().getWidth() / 2000f, cameraSurface.getView().getHeight() / 2000f);
    camera_to_preview_matrix.postTranslate(
        cameraSurface.getView().getWidth() / 2f, cameraSurface.getView().getHeight() / 2f);
  }

  private void calculatePreviewToCameraMatrix() {
    if (camera_controller == null) return;
    calculateCameraToPreviewMatrix();
    if (!camera_to_preview_matrix.invert(preview_to_camera_matrix)) {
      if (MyDebug.LOG) Log.d(TAG, "calculatePreviewToCameraMatrix failed to invert matrix!?");
    }
  }

  public Matrix getCameraToPreviewMatrix() {
    calculateCameraToPreviewMatrix();
    return camera_to_preview_matrix;
  }

  /*Matrix getPreviewToCameraMatrix() {
  	calculatePreviewToCameraMatrix();
  	return preview_to_camera_matrix;
  }*/

  private ArrayList<CameraController.Area> getAreas(float x, float y) {
    float[] coords = {x, y};
    calculatePreviewToCameraMatrix();
    preview_to_camera_matrix.mapPoints(coords);
    float focus_x = coords[0];
    float focus_y = coords[1];

    int focus_size = 50;
    if (MyDebug.LOG) {
      Log.d(TAG, "x, y: " + x + ", " + y);
      Log.d(TAG, "focus x, y: " + focus_x + ", " + focus_y);
    }
    Rect rect = new Rect();
    rect.left = (int) focus_x - focus_size;
    rect.right = (int) focus_x + focus_size;
    rect.top = (int) focus_y - focus_size;
    rect.bottom = (int) focus_y + focus_size;
    if (rect.left < -1000) {
      rect.left = -1000;
      rect.right = rect.left + 2 * focus_size;
    } else if (rect.right > 1000) {
      rect.right = 1000;
      rect.left = rect.right - 2 * focus_size;
    }
    if (rect.top < -1000) {
      rect.top = -1000;
      rect.bottom = rect.top + 2 * focus_size;
    } else if (rect.bottom > 1000) {
      rect.bottom = 1000;
      rect.top = rect.bottom - 2 * focus_size;
    }

    ArrayList<CameraController.Area> areas = new ArrayList<>();
    areas.add(new CameraController.Area(rect, 1000));
    return areas;
  }

  public boolean touchEvent(MotionEvent event) {
    if (MyDebug.LOG)
      Log.d(
          TAG,
          "touch event at : "
              + event.getX()
              + " , "
              + event.getY()
              + " at time "
              + event.getEventTime());
    if (gestureDetector.onTouchEvent(event)) {
      if (MyDebug.LOG) Log.d(TAG, "touch event handled by gestureDetector");
      return true;
    }
    scaleGestureDetector.onTouchEvent(event);
    if (camera_controller == null) {
      if (MyDebug.LOG) Log.d(TAG, "try to reopen camera due to touch");
      this.openCamera();
      return true;
    }
    applicationInterface.touchEvent(event);
    /*if( MyDebug.LOG ) {
    	Log.d(TAG, "touch event: " + event.getAction());
    }*/
    if (event.getPointerCount() != 1) {
      // multitouch_time = System.currentTimeMillis();
      touch_was_multitouch = true;
      return true;
    }
    if (event.getAction() != MotionEvent.ACTION_UP) {
      if (event.getAction() == MotionEvent.ACTION_DOWN && event.getPointerCount() == 1) {
        touch_was_multitouch = false;
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
          touch_orig_x = event.getX();
          touch_orig_y = event.getY();
          if (MyDebug.LOG) Log.d(TAG, "touch down at " + touch_orig_x + " , " + touch_orig_y);
        }
      }
      return true;
    }
    // now only have to handle MotionEvent.ACTION_UP from this point onwards

    if (touch_was_multitouch) {
      return true;
    }
    if (!this.is_video && this.isTakingPhotoOrOnTimer()) {
      // if video, okay to refocus when recording
      return true;
    }

    // ignore swipes
    {
      float x = event.getX();
      float y = event.getY();
      float diff_x = x - touch_orig_x;
      float diff_y = y - touch_orig_y;
      float dist2 = diff_x * diff_x + diff_y * diff_y;
      float scale = getResources().getDisplayMetrics().density;
      float tol = 31 * scale + 0.5f; // convert dps to pixels (about 0.5cm)
      if (MyDebug.LOG) {
        Log.d(TAG, "touched from " + touch_orig_x + " , " + touch_orig_y + " to " + x + " , " + y);
        Log.d(TAG, "dist: " + Math.sqrt(dist2));
        Log.d(TAG, "tol: " + tol);
      }
      if (dist2 > tol * tol) {
        if (MyDebug.LOG) Log.d(TAG, "touch was a swipe");
        return true;
      }
    }

    // note, we always try to force start the preview (in case is_preview_paused has become false)
    // except if recording video (firstly, the preview should be running; secondly, we don't want to
    // reset the phase!)
    if (!this.is_video) {
      startCameraPreview();
    }
    cancelAutoFocus();

    if (camera_controller != null && !this.using_face_detection) {
      this.has_focus_area = false;
      ArrayList<CameraController.Area> areas = getAreas(event.getX(), event.getY());
      if (camera_controller.setFocusAndMeteringArea(areas)) {
        if (MyDebug.LOG) Log.d(TAG, "set focus (and metering?) area");
        this.has_focus_area = true;
        this.focus_screen_x = (int) event.getX();
        this.focus_screen_y = (int) event.getY();
      } else {
        if (MyDebug.LOG) Log.d(TAG, "didn't set focus area in this mode, may have set metering");
        // don't set has_focus_area in this mode
      }
    }

    if (!this.is_video && applicationInterface.getTouchCapturePref()) {
      if (MyDebug.LOG) Log.d(TAG, "touch to capture");
      // interpret as if user had clicked take photo/video button, except that we set the
      // focus/metering areas
      this.takePicturePressed();
      return true;
    }

    tryAutoFocus(false, true);
    return true;
  }
  // @SuppressLint("ClickableViewAccessibility") @Override

  /** Handle multitouch zoom. */
  private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
    @Override
    public boolean onScale(ScaleGestureDetector detector) {
      if (Preview.this.camera_controller != null && Preview.this.has_zoom) {
        Preview.this.scaleZoom(detector.getScaleFactor());
      }
      return true;
    }
  }

  public boolean onDoubleTap() {
    if (MyDebug.LOG) Log.d(TAG, "onDoubleTap()");
    if (!is_video && applicationInterface.getDoubleTapCapturePref()) {
      if (MyDebug.LOG) Log.d(TAG, "double-tap to capture");
      // interpret as if user had clicked take photo/video button (don't need to set focus/metering,
      // as this was done in touchEvent() for the first touch of the double-tap)
      takePicturePressed();
    }
    return true;
  }

  private class DoubleTapListener extends GestureDetector.SimpleOnGestureListener {
    @Override
    public boolean onDoubleTap(MotionEvent e) {
      if (MyDebug.LOG) Log.d(TAG, "onDoubleTap()");
      return Preview.this.onDoubleTap();
    }
  }

  public void clearFocusAreas() {
    if (MyDebug.LOG) Log.d(TAG, "clearFocusAreas()");
    if (camera_controller == null) {
      if (MyDebug.LOG) Log.d(TAG, "camera not opened!");
      return;
    }
    // don't cancelAutoFocus() here, otherwise we get sluggish zoom behaviour on Camera2 API
    camera_controller.clearFocusAndMetering();
    has_focus_area = false;
    focus_success = FOCUS_DONE;
    successfully_focused = false;
  }

  public void getMeasureSpec(int[] spec, int widthSpec, int heightSpec) {
    if (!this.hasAspectRatio()) {
      spec[0] = widthSpec;
      spec[1] = heightSpec;
      return;
    }
    double aspect_ratio = this.getAspectRatio();

    int previewWidth = MeasureSpec.getSize(widthSpec);
    int previewHeight = MeasureSpec.getSize(heightSpec);

    // Get the padding of the border background.
    int hPadding =
        cameraSurface.getView().getPaddingLeft() + cameraSurface.getView().getPaddingRight();
    int vPadding =
        cameraSurface.getView().getPaddingTop() + cameraSurface.getView().getPaddingBottom();

    // Resize the preview frame with correct aspect ratio.
    previewWidth -= hPadding;
    previewHeight -= vPadding;

    boolean widthLonger = previewWidth > previewHeight;
    int longSide = (widthLonger ? previewWidth : previewHeight);
    int shortSide = (widthLonger ? previewHeight : previewWidth);
    if (longSide > shortSide * aspect_ratio) {
      shortSide = (int) ((double) longSide / aspect_ratio);
    } else {
      longSide = (int) ((double) shortSide * aspect_ratio);
    }
    if (widthLonger) {
      previewWidth = longSide;
      previewHeight = shortSide;
    } else {
      previewWidth = shortSide;
      previewHeight = longSide;
    }

    // Add the padding of the border.
    previewWidth += hPadding;
    previewHeight += vPadding;

    spec[0] = MeasureSpec.makeMeasureSpec(previewWidth, MeasureSpec.EXACTLY);
    spec[1] = MeasureSpec.makeMeasureSpec(previewHeight, MeasureSpec.EXACTLY);
  }

  private void mySurfaceCreated() {
    this.has_surface = true;
    this.openCamera();
  }

  private void mySurfaceDestroyed() {
    this.has_surface = false;
    this.closeCamera();
  }

  private void mySurfaceChanged() {
    // surface size is now changed to match the aspect ratio of camera preview - so we shouldn't
    // change the preview to match the surface size, so no need to restart preview here
    if (camera_controller == null) {
      if (MyDebug.LOG) Log.d(TAG, "camera not opened!");
      return;
    }

    // need to force a layoutUI update (e.g., so UI is oriented correctly when app goes idle, device
    // is then rotated, and app is then resumed)
    applicationInterface.layoutUI();
  }

  @Override
  public void surfaceCreated(SurfaceHolder holder) {
    if (MyDebug.LOG) Log.d(TAG, "surfaceCreated()");
    // The Surface has been created, acquire the camera and tell it where
    // to draw.
    mySurfaceCreated();
    cameraSurface.getView().setWillNotDraw(false); // see
    // http://stackoverflow.com/questions/2687015/extended-surfaceviews-ondraw-method-never-called
  }

  @Override
  public void surfaceDestroyed(SurfaceHolder holder) {
    if (MyDebug.LOG) Log.d(TAG, "surfaceDestroyed()");
    // Surface will be destroyed when we return, so stop the preview.
    // Because the CameraDevice object is not a shared resource, it's very
    // important to release it when the activity is paused.
    mySurfaceDestroyed();
  }

  @Override
  public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
    if (MyDebug.LOG) Log.d(TAG, "surfaceChanged " + w + ", " + h);
    if (holder.getSurface() == null) {
      // preview surface does not exist
      return;
    }
    mySurfaceChanged();
  }

  @Override
  public void onSurfaceTextureAvailable(SurfaceTexture arg0, int width, int height) {
    if (MyDebug.LOG) Log.d(TAG, "onSurfaceTextureAvailable()");
    this.set_textureview_size = true;
    this.textureview_w = width;
    this.textureview_h = height;
    mySurfaceCreated();
    configureTransform();
  }

  @Override
  public boolean onSurfaceTextureDestroyed(SurfaceTexture arg0) {
    if (MyDebug.LOG) Log.d(TAG, "onSurfaceTextureDestroyed()");
    this.set_textureview_size = false;
    this.textureview_w = 0;
    this.textureview_h = 0;
    mySurfaceDestroyed();
    return true;
  }

  @Override
  public void onSurfaceTextureSizeChanged(SurfaceTexture arg0, int width, int height) {
    if (MyDebug.LOG) Log.d(TAG, "onSurfaceTextureSizeChanged " + width + ", " + height);
    this.set_textureview_size = true;
    this.textureview_w = width;
    this.textureview_h = height;
    mySurfaceChanged();
    configureTransform();
  }

  @Override
  public void onSurfaceTextureUpdated(SurfaceTexture arg0) {}

  private void configureTransform() {
    if (MyDebug.LOG) Log.d(TAG, "configureTransform");
    if (camera_controller == null || !this.set_preview_size || !this.set_textureview_size) return;
    if (MyDebug.LOG) Log.d(TAG, "textureview size: " + textureview_w + ", " + textureview_h);
    int rotation = getDisplayRotation();
    Matrix matrix = new Matrix();
    RectF viewRect = new RectF(0, 0, this.textureview_w, this.textureview_h);
    RectF bufferRect = new RectF(0, 0, this.preview_h, this.preview_w);
    float centerX = viewRect.centerX();
    float centerY = viewRect.centerY();
    if (Surface.ROTATION_90 == rotation || Surface.ROTATION_270 == rotation) {
      bufferRect.offset(centerX - bufferRect.centerX(), centerY - bufferRect.centerY());
      matrix.setRectToRect(viewRect, bufferRect, Matrix.ScaleToFit.FILL);
      float scale = Math.max((float) textureview_h / preview_h, (float) textureview_w / preview_w);
      matrix.postScale(scale, scale, centerX, centerY);
      matrix.postRotate(90 * (rotation - 2), centerX, centerY);
    }
    cameraSurface.setTransform(matrix);
  }

  private Context getContext() {
    return applicationInterface.getContext();
  }

  private void reconnectCamera(boolean quiet) {
    if (MyDebug.LOG) Log.d(TAG, "reconnectCamera()");
    if (camera_controller != null) { // just to be safe
      try {
        camera_controller.reconnect();
        this.setPreviewPaused(false);
      } catch (CameraControllerException e) {
        if (MyDebug.LOG) Log.e(TAG, "failed to reconnect to camera");
        e.printStackTrace();
        applicationInterface.onFailedReconnectError();
        closeCamera();
      }
      try {
        tryAutoFocus(false, false);
      } catch (RuntimeException e) {
        if (MyDebug.LOG) Log.e(TAG, "tryAutoFocus() threw exception: " + e.getMessage());
        e.printStackTrace();
        // this happens on Nexus 7 if trying to record video at bitrate 50Mbits or higher - it's
        // fair enough that it fails, but we need to recover without a crash!
        // not safe to call closeCamera, as any call to getParameters may cause a RuntimeException
        // update: can no longer reproduce failures on Nexus 7?!
        this.is_preview_started = false;
        camera_controller.release();
        camera_controller = null;
        openCamera();
      }
    }
  }

  private void closeCamera() {
    long debug_time = 0;
    if (MyDebug.LOG) {
      Log.d(TAG, "closeCamera()");
      debug_time = System.currentTimeMillis();
    }
    removePendingContinuousFocusReset();
    has_focus_area = false;
    focus_success = FOCUS_DONE;
    focus_started_time = -1;
    synchronized (this) {
      // synchronise for consistency (keep FindBugs happy)
      take_photo_after_autofocus = false;
      // no need to call camera_controller.setCaptureFollowAutofocusHint() as we're closing the
      // camera
    }
    set_flash_value_after_autofocus = "";
    successfully_focused = false;
    preview_targetRatio = 0.0;
    // n.b., don't reset has_set_location, as we can remember the location when switching camera
    if (continuous_focus_move_is_started) {
      continuous_focus_move_is_started = false;
      applicationInterface.onContinuousFocusMove(false);
    }
    applicationInterface.cameraClosed();
    cancelTimer();
    if (camera_controller != null) {
      // make sure we're into continuous video mode for closing
      // workaround for bug on Samsung Galaxy S5 with UHD, where if the user switches to another
      // (non-continuous-video) focus mode, then goes to Settings, then returns and records video,
      // the preview freezes and the video is corrupted
      // so to be safe, we always reset to continuous video mode
      this.updateFocusForVideo();
      // need to check for camera being non-null again - if an error occurred stopping the video, we
      // will have closed the camera, and may not be able to reopen
      if (camera_controller != null) {
        // camera.setPreviewCallback(null);
        if (MyDebug.LOG) {
          Log.d(
              TAG,
              "closeCamera: about to pause preview: " + (System.currentTimeMillis() - debug_time));
        }
        pausePreview();
        if (MyDebug.LOG) {
          Log.d(
              TAG,
              "closeCamera: about to release camera controller: "
                  + (System.currentTimeMillis() - debug_time));
        }
        camera_controller.release();
        camera_controller = null;
      }
    }
    if (MyDebug.LOG) {
      Log.d(TAG, "closeCamera: total time: " + (System.currentTimeMillis() - debug_time));
    }
  }

  public void cancelTimer() {
    if (MyDebug.LOG) Log.d(TAG, "cancelTimer()");
    if (this.isOnTimer()) {
      takePictureTimerTask.cancel();
      takePictureTimerTask = null;
      if (beepTimerTask != null) {
        beepTimerTask.cancel();
        beepTimerTask = null;
      }
      /*is_taking_photo_on_timer = false;
      is_taking_photo = false;*/
      this.phase = PHASE_NORMAL;
      if (MyDebug.LOG) Log.d(TAG, "cancelled camera timer");
    }
  }

  public void pausePreview() {
    long debug_time = 0;
    if (MyDebug.LOG) {
      Log.d(TAG, "pausePreview()");
      debug_time = System.currentTimeMillis();
    }
    if (camera_controller == null) {
      if (MyDebug.LOG) Log.d(TAG, "camera not opened!");
      return;
    }
    // make sure we're into continuous video mode
    // workaround for bug on Samsung Galaxy S5 with UHD, where if the user switches to another
    // (non-continuous-video) focus mode, then goes to Settings, then returns and records video, the
    // preview freezes and the video is corrupted
    // so to be safe, we always reset to continuous video mode
    // although I've now fixed this at the level where we close the settings, I've put this guard
    // here, just in case the problem occurs from elsewhere
    this.updateFocusForVideo();
    this.setPreviewPaused(false);
    if (MyDebug.LOG) {
      Log.d(
          TAG, "pausePreview: about to stop preview: " + (System.currentTimeMillis() - debug_time));
    }
    camera_controller.stopPreview();
    this.phase = PHASE_NORMAL;
    this.is_preview_started = false;
    if (MyDebug.LOG) {
      Log.d(
          TAG,
          "pausePreview: about to call cameraInOperation: "
              + (System.currentTimeMillis() - debug_time));
    }
    applicationInterface.cameraInOperation(false);
    if (MyDebug.LOG) {
      Log.d(TAG, "pausePreview: total time: " + (System.currentTimeMillis() - debug_time));
    }
  }

  // private int debug_count_opencamera = 0; // see usage below

  /** Try to open the camera. Should only be called if camera_controller==null. */
  private void openCamera() {
    long debug_time = 0;
    if (MyDebug.LOG) {
      Log.d(TAG, "openCamera()");
      debug_time = System.currentTimeMillis();
    }
    // need to init everything now, in case we don't open the camera (but these may already be
    // initialised from an earlier call - e.g., if we are now switching to another camera)
    // n.b., don't reset has_set_location, as we can remember the location when switching camera
    is_preview_started =
        false; // theoretically should be false anyway, but I had one RuntimeException from
    // surfaceCreated()->openCamera()->setupCamera()->setPreviewSize() because
    // is_preview_started was true, even though the preview couldn't have been started
    set_preview_size = false;
    preview_w = 0;
    preview_h = 0;
    has_focus_area = false;
    focus_success = FOCUS_DONE;
    focus_started_time = -1;
    synchronized (this) {
      // synchronise for consistency (keep FindBugs happy)
      take_photo_after_autofocus = false;
      // no need to call camera_controller.setCaptureFollowAutofocusHint() as we're opening the
      // camera
    }
    set_flash_value_after_autofocus = "";
    successfully_focused = false;
    preview_targetRatio = 0.0;
    scene_modes = null;
    has_zoom = false;
    max_zoom_factor = 0;
    minimum_focus_distance = 0.0f;
    zoom_ratios = null;
    faces_detected = null;
    supports_face_detection = false;
    using_face_detection = false;
    supports_video_stabilization = false;
    can_disable_shutter_sound = false;
    color_effects = null;
    white_balances = null;
    isos = null;
    supports_white_balance_temperature = false;
    min_temperature = 0;
    max_temperature = 0;
    supports_iso_range = false;
    min_iso = 0;
    max_iso = 0;
    supports_exposure_time = false;
    min_exposure_time = 0L;
    max_exposure_time = 0L;
    exposures = null;
    min_exposure = 0;
    max_exposure = 0;
    exposure_step = 0.0f;
    supports_expo_bracketing = false;
    max_expo_bracketing_n_images = 0;
    supports_raw = false;
    view_angle_x = 55.0f; // set a sensible default
    view_angle_y = 43.0f; // set a sensible default
    sizes = null;
    current_size_index = -1;
    supported_flash_values = null;
    current_flash_index = -1;
    supported_focus_values = null;
    current_focus_index = -1;
    max_num_focus_areas = 0;
    applicationInterface.cameraInOperation(false);
    if (MyDebug.LOG) Log.d(TAG, "done showGUI");
    if (!this.has_surface) {
      if (MyDebug.LOG) {
        Log.d(TAG, "preview surface not yet available");
      }
      return;
    }
    if (this.app_is_paused) {
      if (MyDebug.LOG) {
        Log.d(TAG, "don't open camera as app is paused");
      }
      return;
    }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      // we restrict the checks to Android 6 or later just in case, see note in
      // LocationSupplier.setupLocationListener()
      if (MyDebug.LOG) Log.d(TAG, "check for permissions");
      if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA)
          != PackageManager.PERMISSION_GRANTED) {
        if (MyDebug.LOG) Log.d(TAG, "camera permission not available");
        has_permissions = false;
        applicationInterface.requestCameraPermission();
        // return for now - the application should try to reopen the camera if permission is granted
        return;
      }
      if (ContextCompat.checkSelfPermission(
              getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
          != PackageManager.PERMISSION_GRANTED) {
        if (MyDebug.LOG) Log.d(TAG, "storage permission not available");
        has_permissions = false;
        applicationInterface.requestStoragePermission();
        // return for now - the application should try to reopen the camera if permission is granted
        return;
      }
      if (MyDebug.LOG) Log.d(TAG, "permissions available");
    }
    // set in case this was previously set to false
    has_permissions = true;

    /*{
    	// debug
    	if( debug_count_opencamera++ == 0 ) {
    		if( MyDebug.LOG )
    			Log.d(TAG, "debug: don't open camera yet");
    		return;
    	}
    }*/
    try {
      int cameraId = applicationInterface.getCameraIdPref();
      if (cameraId < 0 || cameraId >= camera_controller_manager.getNumberOfCameras()) {
        if (MyDebug.LOG) Log.d(TAG, "invalid cameraId: " + cameraId);
        cameraId = 0;
        applicationInterface.setCameraIdPref(cameraId);
      }
      if (MyDebug.LOG) {
        Log.d(TAG, "try to open camera: " + cameraId);
        Log.d(
            TAG,
            "openCamera: time before opening camera: " + (System.currentTimeMillis() - debug_time));
      }
      if (test_fail_open_camera) {
        if (MyDebug.LOG) Log.d(TAG, "test failing to open camera");
        throw new CameraControllerException();
      }
      CameraController.ErrorCallback cameraErrorCallback =
          new CameraController.ErrorCallback() {
            public void onError() {
              if (MyDebug.LOG) Log.e(TAG, "error from CameraController: camera device failed");
              if (camera_controller != null) {
                camera_controller = null;
                applicationInterface.onCameraError();
              }
            }
          };
      if (using_android_l) {
        CameraController.ErrorCallback previewErrorCallback =
            new CameraController.ErrorCallback() {
              public void onError() {
                if (MyDebug.LOG) Log.e(TAG, "error from CameraController: preview failed to start");
                applicationInterface.onFailedStartPreview();
              }
            };
        camera_controller =
            new CameraController2(
                this.getContext(), cameraId, previewErrorCallback, cameraErrorCallback);
        if (applicationInterface.useCamera2FakeFlash()) {
          camera_controller.setUseCamera2FakeFlash(true);
        }
      } else camera_controller = new CameraController1(cameraId, cameraErrorCallback);
      // throw new CameraControllerException(); // uncomment to test camera not opening
    } catch (CameraControllerException e) {
      if (MyDebug.LOG) Log.e(TAG, "Failed to open camera: " + e.getMessage());
      e.printStackTrace();
      camera_controller = null;
    }
    if (MyDebug.LOG) {
      Log.d(
          TAG,
          "openCamera: time after opening camera: " + (System.currentTimeMillis() - debug_time));
    }
    boolean take_photo = false;
    if (camera_controller != null) {
      Activity activity = (Activity) this.getContext();
      if (MyDebug.LOG) Log.d(TAG, "intent: " + activity.getIntent());
      if (activity.getIntent() != null && activity.getIntent().getExtras() != null) {
        take_photo = activity.getIntent().getExtras().getBoolean(TakePhoto.TAKE_PHOTO);
        activity.getIntent().removeExtra(TakePhoto.TAKE_PHOTO);
      } else {
        if (MyDebug.LOG) Log.d(TAG, "no intent data");
      }
      if (MyDebug.LOG) Log.d(TAG, "take_photo?: " + take_photo);

      this.setCameraDisplayOrientation();
      new OrientationEventListener(activity) {
        @Override
        public void onOrientationChanged(int orientation) {
          Preview.this.onOrientationChanged(orientation);
        }
      }.enable();
      if (MyDebug.LOG) {
        Log.d(
            TAG,
            "openCamera: time after setting orientation: "
                + (System.currentTimeMillis() - debug_time));
      }

      if (MyDebug.LOG) Log.d(TAG, "call setPreviewDisplay");
      cameraSurface.setPreviewDisplay(camera_controller);
      if (MyDebug.LOG) {
        Log.d(
            TAG,
            "openCamera: time after setting preview display: "
                + (System.currentTimeMillis() - debug_time));
      }

      setupCamera(take_photo);
    }

    if (MyDebug.LOG) {
      Log.d(
          TAG,
          "openCamera: total time to open camera: " + (System.currentTimeMillis() - debug_time));
    }
    if (this.getContext() instanceof CameraActivity) {
      CameraActivity cameraActivity = (CameraActivity) this.getContext();
      cameraActivity.getSettingDetail();
    }
  }

  /**
   * Try to reopen the camera, if not currently open (e.g., permission wasn't granted, but now it
   * is).
   */
  public void retryOpenCamera() {
    if (MyDebug.LOG) Log.d(TAG, "retryOpenCamera()");
    if (camera_controller == null) {
      if (MyDebug.LOG) Log.d(TAG, "try to reopen camera");
      this.openCamera();
    } else {
      if (MyDebug.LOG) Log.d(TAG, "camera already open");
    }
  }

  /**
   * Returns false if we failed to open the camera because camera or storage permission wasn't
   * available.
   */
  public boolean hasPermissions() {
    return has_permissions;
  }

  /* Should only be called after camera first opened, or after preview is paused.
   * take_photo is true if we have been called from the TakePhoto widget (which means
   * we'll take a photo immediately after startup).
   */
  public void setupCamera(boolean take_photo) {
    if (MyDebug.LOG) Log.d(TAG, "setupCamera()");
    long debug_time = 0;
    if (MyDebug.LOG) {
      debug_time = System.currentTimeMillis();
    }
    if (camera_controller == null) {
      if (MyDebug.LOG) Log.d(TAG, "camera not opened!");
      return;
    }
    boolean do_startup_focus = !take_photo && applicationInterface.getStartupFocusPref();
    if (MyDebug.LOG) {
      Log.d(TAG, "take_photo? " + take_photo);
      Log.d(TAG, "do_startup_focus? " + do_startup_focus);
    }
    // make sure we're into continuous video mode for reopening
    // workaround for bug on Samsung Galaxy S5 with UHD, where if the user switches to another
    // (non-continuous-video) focus mode, then goes to Settings, then returns and records video, the
    // preview freezes and the video is corrupted
    // so to be safe, we always reset to continuous video mode
    // although I've now fixed this at the level where we close the settings, I've put this guard
    // here, just in case the problem occurs from elsewhere
    // we'll switch to the user-requested focus by calling setFocusPref() from
    // setupCameraParameters() below
    this.updateFocusForVideo();

    setupCameraParameters();

    // now switch to video if saved

    if (do_startup_focus && using_android_l && camera_controller.supportsAutoFocus()) {
      // need to switch flash off for autofocus - and for Android L, need to do this before starting
      // preview (otherwise it won't work in time); for old camera API, need to do this after
      // starting preview!
      set_flash_value_after_autofocus = "";
      String old_flash_value = camera_controller.getFlashValue();
      // getFlashValue() may return "" if flash not supported!
      // also set flash_torch - otherwise we get bug where torch doesn't turn on when starting up in
      // video mode (and it's not like we want to turn torch off for startup focus, anyway)
      if (old_flash_value.length() > 0
          && !old_flash_value.equals("flash_off")
          && !old_flash_value.equals("flash_torch")) {
        set_flash_value_after_autofocus = old_flash_value;
        camera_controller.setFlashValue("flash_off");
      }
      if (MyDebug.LOG)
        Log.d(TAG, "set_flash_value_after_autofocus is now: " + set_flash_value_after_autofocus);
    }

    if (this.supports_raw && applicationInterface.isRawPref()) {
      camera_controller.setRaw(true);
    } else {
      camera_controller.setRaw(false);
    }

    if (this.supports_expo_bracketing && applicationInterface.isExpoBracketingPref()) {
      camera_controller.setExpoBracketing(true);
      camera_controller.setExpoBracketingNImages(
          applicationInterface.getExpoBracketingNImagesPref());
      camera_controller.setExpoBracketingStops(applicationInterface.getExpoBracketingStopsPref());
      // setUseExpoFastBurst called when taking a photo
    } else {
      camera_controller.setExpoBracketing(false);
    }

    camera_controller.setOptimiseAEForDRO(applicationInterface.getOptimiseAEForDROPref());

    // Must set preview size before starting camera preview
    // and must do it after setting photo vs video mode
    setPreviewSize(); // need to call this when we switch cameras, not just when we run for the
    // first time
    if (MyDebug.LOG) {
      Log.d(
          TAG,
          "setupCamera: time after setting preview size: "
              + (System.currentTimeMillis() - debug_time));
    }
    // Must call startCameraPreview after checking if face detection is present - probably best to
    // call it after setting all parameters that we want
    startCameraPreview();
    if (MyDebug.LOG) {
      Log.d(
          TAG,
          "setupCamera: time after starting camera preview: "
              + (System.currentTimeMillis() - debug_time));
    }

    // must be done after setting parameters, as this function may set parameters
    // also needs to be done after starting preview for some devices (e.g., Nexus 7)
    if (this.has_zoom && applicationInterface.getZoomPref() != 0) {
      zoomTo(applicationInterface.getZoomPref());
      if (MyDebug.LOG) {
        Log.d(
            TAG,
            "setupCamera: total time after zoomTo: " + (System.currentTimeMillis() - debug_time));
      }
    }

    applicationInterface
        .cameraSetup(); // must call this after the above take_photo code for calling switchVideo
    if (MyDebug.LOG) {
      Log.d(
          TAG,
          "setupCamera: total time after cameraSetup: "
              + (System.currentTimeMillis() - debug_time));
    }

    if (take_photo) {
      // take photo after a delay - otherwise we sometimes get a black image?!
      // also need a longer delay for continuous picture focus, to allow a chance to focus - 1000ms
      // seems to work okay for Nexus 6, put 1500ms to be safe
      String focus_value = getCurrentFocusValue();
      final int delay =
          (focus_value != null && focus_value.equals("focus_mode_continuous_picture")) ? 1500 : 500;
      if (MyDebug.LOG) Log.d(TAG, "delay for take photo: " + delay);
      final Handler handler = new Handler();
      handler.postDelayed(
          new Runnable() {
            @Override
            public void run() {
              if (MyDebug.LOG) Log.d(TAG, "do automatic take picture");
              takePicture(false);
            }
          },
          delay);
    }

    if (do_startup_focus) {
      final Handler handler = new Handler();
      handler.postDelayed(
          new Runnable() {
            @Override
            public void run() {
              if (MyDebug.LOG) Log.d(TAG, "do startup autofocus");
              tryAutoFocus(
                  true,
                  false); // so we get the autofocus when starting up - we do this on a delay, as
              // calling it immediately means the autofocus doesn't seem to work
              // properly sometimes (at least on Galaxy Nexus)
            }
          },
          500);
    }

    if (MyDebug.LOG) {
      Log.d(
          TAG,
          "setupCamera: total time after setupCamera: "
              + (System.currentTimeMillis() - debug_time));
    }
  }

  private void setupCameraParameters() {
    if (MyDebug.LOG) Log.d(TAG, "setupCameraParameters()");
    long debug_time = 0;
    if (MyDebug.LOG) {
      debug_time = System.currentTimeMillis();
    }
    {
      // get available scene modes
      // important, from docs:
      // "Changing scene mode may override other parameters (such as flash mode, focus mode, white
      // balance).
      // For example, suppose originally flash mode is on and supported flash modes are on/off. In
      // night
      // scene mode, both flash mode and supported flash mode may be changed to off. After setting
      // scene
      // mode, applications should call getParameters to know if some parameters are changed."
      if (MyDebug.LOG) Log.d(TAG, "set up scene mode");
      String value = applicationInterface.getSceneModePref();
      if (MyDebug.LOG) Log.d(TAG, "saved scene mode: " + value);

      CameraController.SupportedValues supported_values = camera_controller.setSceneMode(value);
      if (supported_values != null) {
        scene_modes = supported_values.values;
        // now save, so it's available for PreferenceActivity
        applicationInterface.setSceneModePref(supported_values.selected_value);
      } else {
        // delete key in case it's present (e.g., if feature no longer available due to change in
        // OS, or switching APIs)
        applicationInterface.clearSceneModePref();
      }
    }
    if (MyDebug.LOG) {
      Log.d(
          TAG,
          "setupCameraParameters: time after setting scene mode: "
              + (System.currentTimeMillis() - debug_time));
    }

    {
      // grab all read-only info from parameters
      if (MyDebug.LOG) Log.d(TAG, "grab info from parameters");
      CameraController.CameraFeatures camera_features = camera_controller.getCameraFeatures();
      this.has_zoom = camera_features.is_zoom_supported;
      if (this.has_zoom) {
        this.max_zoom_factor = camera_features.max_zoom;
        this.zoom_ratios = camera_features.zoom_ratios;
      }
      this.minimum_focus_distance = camera_features.minimum_focus_distance;
      this.supports_face_detection = camera_features.supports_face_detection;
      this.sizes = camera_features.picture_sizes;
      supported_flash_values = camera_features.supported_flash_values;
      supported_focus_values = camera_features.supported_focus_values;
      this.max_num_focus_areas = camera_features.max_num_focus_areas;
      this.is_exposure_lock_supported = camera_features.is_exposure_lock_supported;
      this.supports_video_stabilization = camera_features.is_video_stabilization_supported;
      this.can_disable_shutter_sound = camera_features.can_disable_shutter_sound;
      this.supports_white_balance_temperature = camera_features.supports_white_balance_temperature;
      this.min_temperature = camera_features.min_temperature;
      this.max_temperature = camera_features.max_temperature;
      this.supports_iso_range = camera_features.supports_iso_range;
      this.min_iso = camera_features.min_iso;
      this.max_iso = camera_features.max_iso;
      this.supports_exposure_time = camera_features.supports_exposure_time;
      this.min_exposure_time = camera_features.min_exposure_time;
      this.max_exposure_time = camera_features.max_exposure_time;
      this.min_exposure = camera_features.min_exposure;
      this.max_exposure = camera_features.max_exposure;
      this.exposure_step = camera_features.exposure_step;
      this.supports_expo_bracketing = camera_features.supports_expo_bracketing;
      this.max_expo_bracketing_n_images = camera_features.max_expo_bracketing_n_images;
      this.supports_raw = camera_features.supports_raw;
      this.view_angle_x = camera_features.view_angle_x;
      this.view_angle_y = camera_features.view_angle_y;
      this.supported_preview_sizes = camera_features.preview_sizes;
    }
    if (MyDebug.LOG) {
      Log.d(
          TAG,
          "setupCameraParameters: time after getting read only info: "
              + (System.currentTimeMillis() - debug_time));
    }

    {
      if (MyDebug.LOG) Log.d(TAG, "set up face detection");
      // get face detection supported
      this.faces_detected = null;
      if (this.supports_face_detection) {
        this.using_face_detection = applicationInterface.getFaceDetectionPref();
      } else {
        this.using_face_detection = false;
      }
      if (MyDebug.LOG) {
        Log.d(TAG, "supports_face_detection?: " + supports_face_detection);
        Log.d(TAG, "using_face_detection?: " + using_face_detection);
      }
      if (this.using_face_detection) {
        class MyFaceDetectionListener implements CameraController.FaceDetectionListener {
          @Override
          public void onFaceDetection(CameraController.Face[] faces) {
            faces_detected = new CameraController.Face[faces.length];
            System.arraycopy(faces, 0, faces_detected, 0, faces.length);
          }
        }
        camera_controller.setFaceDetectionListener(new MyFaceDetectionListener());
      }
    }
    if (MyDebug.LOG) {
      Log.d(
          TAG,
          "setupCameraParameters: time after setting face detection: "
              + (System.currentTimeMillis() - debug_time));
    }

    {
      if (MyDebug.LOG) Log.d(TAG, "set up video stabilization");
    }
    if (MyDebug.LOG) {
      Log.d(
          TAG,
          "setupCameraParameters: time after video stabilization: "
              + (System.currentTimeMillis() - debug_time));
    }

    {
      if (MyDebug.LOG) Log.d(TAG, "set up color effect");
      String value = applicationInterface.getColorEffectPref();
      if (MyDebug.LOG) Log.d(TAG, "saved color effect: " + value);

      final Activity activity = (Activity) this.getContext();

      toggle.setOnClickListener(
          new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              SharedPreferences sharedPreferences = getDefaultSharedPreferences(activity);
              Boolean firstClick =
                  sharedPreferences.getBoolean(activity.getString(R.string.first_click), true);
              if (firstClick) {
                MaterialShowcaseView.resetSingleUse(activity, SHOWCASE_ID);
                new MaterialShowcaseView.Builder(activity)
                    .setTarget(toggle)
                    .setTitleText(R.string.toggle_button)
                    .setDismissText(R.string.ok_button)
                    .setContentText(
                        R.string.toggle_info) // optional but starting animations immediately in
                    // onCreate can make them choppy
                    .singleUse(
                        SHOWCASE_ID) // provide a unique ID used to ensure it is only shown once
                    .show();
              } else {
                try {
                  final List<String> colorEffect = getSupportedColorEffects();
                  colorNum++;
                  if (colorNum == colorEffect.size()) colorNum = 0;
                  final String color = colorEffect.get(colorNum);
                  CameraController.SupportedValues supported_values =
                      camera_controller.setColorEffect(color);
                  if (supported_values != null) {
                    color_effects = supported_values.values;
                    applicationInterface.setColorEffectPref(supported_values.selected_value);
                  } else {
                    applicationInterface.clearColorEffectPref();
                  }
                } catch (Exception e) {
                  Snackbar.make(
                          activity.findViewById(android.R.id.content),
                          "Your device does not support any filters",
                          Snackbar.LENGTH_SHORT)
                      .show();
                  Log.e(TAG, "ColorEffect List Size Is Null ");
                }
              }
              SharedPreferences.Editor editor = sharedPreferences.edit();
              editor.putBoolean(activity.getString(R.string.first_click), false);
              editor.apply();
            }
          });

      toggle.setOnLongClickListener(
          new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
              final List<String> colorEffect = getSupportedColorEffects();
              colorNum = 0;
              if (colorEffect != null) {
                CameraController.SupportedValues supported_values =
                    camera_controller.setColorEffect(colorEffect.get(0));
                applicationInterface.setColorEffectPref(supported_values.selected_value);
              }
              return true;
            }
          });
      CameraController.SupportedValues supported_values = camera_controller.setColorEffect(value);
      if (supported_values != null) {
        color_effects = supported_values.values;
        // now save, so it's available for PreferenceActivity
        applicationInterface.setColorEffectPref(supported_values.selected_value);
      } else {
        // delete key in case it's present (e.g., if feature no longer available due to change in
        // OS, or switching APIs)
        applicationInterface.clearColorEffectPref();
      }
    }
    if (MyDebug.LOG) {
      Log.d(
          TAG,
          "setupCameraParameters: time after color effect: "
              + (System.currentTimeMillis() - debug_time));
    }

    {
      if (MyDebug.LOG) Log.d(TAG, "set up white balance");
      String value = applicationInterface.getWhiteBalancePref();
      if (MyDebug.LOG) Log.d(TAG, "saved white balance: " + value);

      CameraController.SupportedValues supported_values = camera_controller.setWhiteBalance(value);
      if (supported_values != null) {
        white_balances = supported_values.values;
        // now save, so it's available for PreferenceActivity
        applicationInterface.setWhiteBalancePref(supported_values.selected_value);

        if (supported_values.selected_value.equals("manual")
            && this.supports_white_balance_temperature) {
          int temperature = applicationInterface.getWhiteBalanceTemperaturePref();
          camera_controller.setWhiteBalanceTemperature(temperature);
          if (MyDebug.LOG) Log.d(TAG, "saved white balance: " + value);
        }
      } else {
        // delete key in case it's present (e.g., if feature no longer available due to change in
        // OS, or switching APIs)
        applicationInterface.clearWhiteBalancePref();
      }
    }
    if (MyDebug.LOG) {
      Log.d(
          TAG,
          "setupCameraParameters: time after white balance: "
              + (System.currentTimeMillis() - debug_time));
    }

    // must be done before setting flash modes, as we may remove flash modes if in manual mode
    if (MyDebug.LOG) Log.d(TAG, "set up iso");
    String value = applicationInterface.getISOPref();
    if (MyDebug.LOG) Log.d(TAG, "saved iso: " + value);
    boolean is_manual_iso = false;
    if (supports_iso_range) {
      // in this mode, we can set any ISO value from min to max
      this.isos = null; // if supports_iso_range==true, caller shouldn't be using getSupportedISOs()

      // now set the desired ISO mode/value
      if (value.equals("auto")) {
        if (MyDebug.LOG) Log.d(TAG, "setting auto iso");
        camera_controller.setManualISO(false, 0);
      } else {
        // try to parse the supplied manual ISO value
        try {
          if (MyDebug.LOG) Log.d(TAG, "setting manual iso");
          is_manual_iso = true;
          int iso = Integer.parseInt(value);
          if (MyDebug.LOG) Log.d(TAG, "iso: " + iso);
          camera_controller.setManualISO(true, iso);
        } catch (NumberFormatException exception) {
          if (MyDebug.LOG) Log.d(TAG, "iso invalid format, can't parse to int");
          camera_controller.setManualISO(false, 0);
          value = "auto"; // so we switch the preferences back to auto mode, rather than the invalid
          // value
        }

        // now save, so it's available for PreferenceActivity
        applicationInterface.setISOPref(value);
      }
    } else {
      // in this mode, any support for ISO is only the specific ISOs offered by the CameraController
      CameraController.SupportedValues supported_values = camera_controller.setISO(value);
      if (supported_values != null) {
        isos = supported_values.values;
        if (!supported_values.selected_value.equals("auto")) {
          if (MyDebug.LOG) Log.d(TAG, "has manual iso");
          is_manual_iso = true;
        }
        // now save, so it's available for PreferenceActivity
        applicationInterface.setISOPref(supported_values.selected_value);

      } else {
        // delete key in case it's present (e.g., if feature no longer available due to change in
        // OS, or switching APIs)
        applicationInterface.clearISOPref();
      }
    }

    if (is_manual_iso) {
      if (supports_exposure_time) {
        long exposure_time_value = applicationInterface.getExposureTimePref();
        if (MyDebug.LOG) Log.d(TAG, "saved exposure_time: " + exposure_time_value);
        if (exposure_time_value < min_exposure_time) exposure_time_value = min_exposure_time;
        else if (exposure_time_value > max_exposure_time) exposure_time_value = max_exposure_time;
        camera_controller.setExposureTime(exposure_time_value);
        // now save
        applicationInterface.setExposureTimePref(exposure_time_value);
      } else {
        // delete key in case it's present (e.g., if feature no longer available due to change in
        // OS, or switching APIs)
        applicationInterface.clearExposureTimePref();
      }

      if (this.using_android_l && supported_flash_values != null) {
        // flash modes not supported when using Camera2 and manual ISO
        // (it's unclear flash is useful - ideally we'd at least offer torch, but ISO seems to reset
        // to 100 when flash/torch is on!)
        supported_flash_values = null;
        if (MyDebug.LOG) Log.d(TAG, "flash not supported in Camera2 manual mode");
      }
    }
    if (MyDebug.LOG) {
      Log.d(
          TAG,
          "setupCameraParameters: time after manual iso: "
              + (System.currentTimeMillis() - debug_time));
    }

    {
      if (MyDebug.LOG) {
        Log.d(TAG, "set up exposure compensation");
        Log.d(TAG, "min_exposure: " + min_exposure);
        Log.d(TAG, "max_exposure: " + max_exposure);
      }
      // get min/max exposure
      exposures = null;
      if (min_exposure != 0 || max_exposure != 0) {
        exposures = new ArrayList<>();
        for (int i = min_exposure; i <= max_exposure; i++) {
          exposures.add("" + i);
        }
        // if in manual ISO mode, we still want to get the valid exposure compensations, but
        // shouldn't set exposure compensation
        if (!is_manual_iso) {
          int exposure = applicationInterface.getExposureCompensationPref();
          if (exposure < min_exposure || exposure > max_exposure) {
            exposure = 0;
            if (MyDebug.LOG) Log.d(TAG, "saved exposure not supported, reset to 0");
            if (exposure < min_exposure || exposure > max_exposure) {
              if (MyDebug.LOG)
                Log.d(TAG, "zero isn't an allowed exposure?! reset to min " + min_exposure);
              exposure = min_exposure;
            }
          }
          camera_controller.setExposureCompensation(exposure);
          // now save, so it's available for PreferenceActivity
          applicationInterface.setExposureCompensationPref(exposure);
        }
      } else {
        // delete key in case it's present (e.g., if feature no longer available due to change in
        // OS, or switching APIs)
        applicationInterface.clearExposureCompensationPref();
      }
    }
    if (MyDebug.LOG) {
      Log.d(
          TAG,
          "setupCameraParameters: time after exposures: "
              + (System.currentTimeMillis() - debug_time));
    }

    {
      if (MyDebug.LOG) Log.d(TAG, "set up picture sizes");
      if (MyDebug.LOG) {
        for (int i = 0; i < sizes.size(); i++) {
          CameraController.Size size = sizes.get(i);
          Log.d(TAG, "supported picture size: " + size.width + " , " + size.height);
        }
      }
      current_size_index = -1;
      Pair<Integer, Integer> resolution = applicationInterface.getCameraResolutionPref();
      if (resolution != null) {
        int resolution_w = resolution.first;
        int resolution_h = resolution.second;
        // now find size in valid list
        for (int i = 0; i < sizes.size() && current_size_index == -1; i++) {
          CameraController.Size size = sizes.get(i);
          if (size.width == resolution_w && size.height == resolution_h) {
            current_size_index = i;
            if (MyDebug.LOG) Log.d(TAG, "set current_size_index to: " + current_size_index);
          }
        }
        if (current_size_index == -1) {
          if (MyDebug.LOG) Log.e(TAG, "failed to find valid size");
        }
      }

      if (current_size_index == -1) {
        // set to largest
        CameraController.Size current_size = null;
        for (int i = 0; i < sizes.size(); i++) {
          CameraController.Size size = sizes.get(i);
          if (current_size == null
              || size.width * size.height > current_size.width * current_size.height) {
            current_size_index = i;
            current_size = size;
          }
        }
      }
      if (current_size_index != -1) {
        CameraController.Size current_size = sizes.get(current_size_index);
        if (MyDebug.LOG)
          Log.d(
              TAG,
              "Current size index "
                  + current_size_index
                  + ": "
                  + current_size.width
                  + ", "
                  + current_size.height);

        // now save, so it's available for PreferenceActivity
        applicationInterface.setCameraResolutionPref(current_size.width, current_size.height);
      }
      // size set later in setPreviewSize()
    }
    if (MyDebug.LOG) {
      Log.d(
          TAG,
          "setupCameraParameters: time after picture sizes: "
              + (System.currentTimeMillis() - debug_time));
    }

    {
      int image_quality = applicationInterface.getImageQualityPref();
      if (MyDebug.LOG) Log.d(TAG, "set up jpeg quality: " + image_quality);
      camera_controller.setJpegQuality(image_quality);
    }
    if (MyDebug.LOG) {
      Log.d(
          TAG,
          "setupCameraParameters: time after jpeg quality: "
              + (System.currentTimeMillis() - debug_time));
    }

    if (MyDebug.LOG) {
      Log.d(
          TAG,
          "setupCameraParameters: time after video sizes: "
              + (System.currentTimeMillis() - debug_time));
    }

    {
      if (MyDebug.LOG) {
        Log.d(TAG, "set up flash");
        Log.d(TAG, "flash values: " + supported_flash_values);
      }
      current_flash_index = -1;
      if (supported_flash_values != null && supported_flash_values.size() > 1) {

        String flash_value = applicationInterface.getFlashPref();
        if (flash_value.length() > 0) {
          if (MyDebug.LOG) Log.d(TAG, "found existing flash_value: " + flash_value);
          if (!updateFlash(
              flash_value,
              false)) { // don't need to save, as this is the value that's already saved
            if (MyDebug.LOG) Log.d(TAG, "flash value no longer supported!");
            updateFlash(0, true);
          }
        } else {
          if (MyDebug.LOG) Log.d(TAG, "found no existing flash_value");
          // whilst devices with flash should support flash_auto, we'll also be in this codepath for
          // front cameras with
          // no flash, as instead the available options will be flash_off, flash_frontscreen_auto,
          // flash_frontscreen_on
          // see testTakePhotoFrontCameraScreenFlash
          if (supported_flash_values.contains("flash_auto")) updateFlash("flash_auto", true);
          else updateFlash("flash_off", true);
        }
      } else {
        if (MyDebug.LOG) Log.d(TAG, "flash not supported");
        supported_flash_values = null;
      }
    }
    if (MyDebug.LOG) {
      Log.d(
          TAG,
          "setupCameraParameters: time after setting up flash: "
              + (System.currentTimeMillis() - debug_time));
    }

    {
      if (MyDebug.LOG) Log.d(TAG, "set up focus");
      current_focus_index = -1;
      if (supported_focus_values != null && supported_focus_values.size() > 1) {
        if (MyDebug.LOG) Log.d(TAG, "focus values: " + supported_focus_values);

        setFocusPref(true);
      } else {
        if (MyDebug.LOG) Log.d(TAG, "focus not supported");
        supported_focus_values = null;
      }
      /*supported_focus_values = new ArrayList<>();
      supported_focus_values.add("focus_mode_auto");
      supported_focus_values.add("focus_mode_infinity");
      supported_focus_values.add("focus_mode_macro");
      supported_focus_values.add("focus_mode_locked");
      supported_focus_values.add("focus_mode_manual2");
      supported_focus_values.add("focus_mode_fixed");
      supported_focus_values.add("focus_mode_edof");
      supported_focus_values.add("focus_mode_continuous_video");*/
      /*View focusModeButton = (View) activity.findViewById(R.id.focus_mode);
      focusModeButton.setVisibility(supported_focus_values != null && !immersive_mode ? View.VISIBLE : View.GONE);*/
    }

    {
      float focus_distance_value = applicationInterface.getFocusDistancePref();
      if (MyDebug.LOG) Log.d(TAG, "saved focus_distance: " + focus_distance_value);
      if (focus_distance_value < 0.0f) focus_distance_value = 0.0f;
      else if (focus_distance_value > minimum_focus_distance)
        focus_distance_value = minimum_focus_distance;
      camera_controller.setFocusDistance(focus_distance_value);
      // now save
      applicationInterface.setFocusDistancePref(focus_distance_value);
    }
    if (MyDebug.LOG) {
      Log.d(
          TAG,
          "setupCameraParameters: time after setting up focus: "
              + (System.currentTimeMillis() - debug_time));
    }

    {
      if (MyDebug.LOG) Log.d(TAG, "set up exposure lock");
      // exposure lock should always default to false, as doesn't make sense to save it - we can't
      // really preserve a "lock" after the camera is reopened
      // also note that it isn't safe to lock the exposure before starting the preview
      is_exposure_locked = false;
    }

    if (MyDebug.LOG) {
      Log.d(
          TAG,
          "setupCameraParameters: total time for setting up camera parameters: "
              + (System.currentTimeMillis() - debug_time));
    }
  }

  private void setPreviewSize() {
    if (MyDebug.LOG) Log.d(TAG, "setPreviewSize()");
    // also now sets picture size
    if (camera_controller == null) {
      if (MyDebug.LOG) Log.d(TAG, "camera not opened!");
      return;
    }
    if (is_preview_started) {
      if (MyDebug.LOG) Log.d(TAG, "setPreviewSize() shouldn't be called when preview is running");
      throw new RuntimeException(); // throw as RuntimeException, as this is a programming error
    }
    if (!using_android_l) {
      // don't do for Android L, else this means we get flash on startup autofocus if flash is on
      this.cancelAutoFocus();
    }
    // first set picture size (for photo mode, must be done now so we can set the picture size from
    // this; for video, doesn't really matter when we set it)
    CameraController.Size new_size = null;
    {
      if (current_size_index != -1) {
        new_size = sizes.get(current_size_index);
      }
    }
    if (new_size != null) {
      camera_controller.setPictureSize(new_size.width, new_size.height);
    }
    // set optimal preview size
    if (supported_preview_sizes != null && supported_preview_sizes.size() > 0) {
      /*CameraController.Size best_size = supported_preview_sizes.get(0);
        for(CameraController.Size size : supported_preview_sizes) {
      if( MyDebug.LOG )
      	Log.d(TAG, "    supported preview size: " + size.width + ", " + size.height);
        	if( size.width*size.height > best_size.width*best_size.height ) {
        		best_size = size;
        	}
        }*/
      CameraController.Size best_size = getOptimalPreviewSize(supported_preview_sizes);
      camera_controller.setPreviewSize(best_size.width, best_size.height);
      this.set_preview_size = true;
      this.preview_w = best_size.width;
      this.preview_h = best_size.height;
      this.setAspectRatio(((double) best_size.width) / (double) best_size.height);
    }
  }

  private CamcorderProfile getCamcorderProfile(String quality) {
    if (MyDebug.LOG) Log.d(TAG, "getCamcorderProfile(): " + quality);
    if (camera_controller == null) {
      if (MyDebug.LOG) Log.d(TAG, "camera not opened!");
      return CamcorderProfile.get(0, CamcorderProfile.QUALITY_HIGH);
    }
    int cameraId = camera_controller.getCameraId();
    CamcorderProfile camcorder_profile =
        CamcorderProfile.get(cameraId, CamcorderProfile.QUALITY_HIGH); // default
    try {
      String profile_string = quality;
      int index = profile_string.indexOf('_');
      if (index != -1) {
        profile_string = quality.substring(0, index);
        if (MyDebug.LOG) Log.d(TAG, "    profile_string: " + profile_string);
      }
      int profile = Integer.parseInt(profile_string);
      camcorder_profile = CamcorderProfile.get(cameraId, profile);
      if (index != -1 && index + 1 < quality.length()) {
        String override_string = quality.substring(index + 1);
        if (MyDebug.LOG) Log.d(TAG, "    override_string: " + override_string);
        if (override_string.charAt(0) == 'r' && override_string.length() >= 4) {
          index = override_string.indexOf('x');
          if (index == -1) {
            if (MyDebug.LOG) Log.d(TAG, "override_string invalid format, can't find x");
          } else {
            String resolution_w_s = override_string.substring(1, index); // skip first 'r'
            String resolution_h_s = override_string.substring(index + 1);
            if (MyDebug.LOG) {
              Log.d(TAG, "resolution_w_s: " + resolution_w_s);
              Log.d(TAG, "resolution_h_s: " + resolution_h_s);
            }
            // copy to local variable first, so that if we fail to parse height, we don't set the
            // width either
            int resolution_w = Integer.parseInt(resolution_w_s);
            int resolution_h = Integer.parseInt(resolution_h_s);
            camcorder_profile.videoFrameWidth = resolution_w;
            camcorder_profile.videoFrameHeight = resolution_h;
          }
        } else {
          if (MyDebug.LOG)
            Log.d(TAG, "unknown override_string initial code, or otherwise invalid format");
        }
      }
    } catch (NumberFormatException e) {
      if (MyDebug.LOG) Log.e(TAG, "failed to parse video quality: " + quality);
      e.printStackTrace();
    }
    return camcorder_profile;
  }

  private static String formatFloatToString(final float f) {
    final int i = (int) f;
    if (f == i) return Integer.toString(i);
    return String.format(Locale.getDefault(), "%.2f", f);
  }

  private static int greatestCommonFactor(int a, int b) {
    while (b > 0) {
      int temp = b;
      b = a % b;
      a = temp;
    }
    return a;
  }

  private static String getAspectRatio(int width, int height) {
    int gcf = greatestCommonFactor(width, height);
    if (gcf > 0) {
      // had a Google Play crash due to gcf being 0!? Implies width must be zero
      width /= gcf;
      height /= gcf;
    }
    return width + ":" + height;
  }

  public static String getMPString(int width, int height) {
    float mp = (width * height) / 1000000.0f;
    return formatFloatToString(mp) + "MP";
  }

  public static String getAspectRatioMPString(int width, int height) {
    return "(" + getAspectRatio(width, height) + ", " + getMPString(width, height) + ")";
  }

  public String getCamcorderProfileDescriptionShort(String quality) {
    if (camera_controller == null) return "";
    CamcorderProfile profile = getCamcorderProfile(quality);
    return profile.videoFrameWidth
        + "x"
        + profile.videoFrameHeight
        + " "
        + getMPString(profile.videoFrameWidth, profile.videoFrameHeight);
  }

  public String getCamcorderProfileDescription(String quality) {
    if (camera_controller == null) return "";
    CamcorderProfile profile = getCamcorderProfile(quality);
    String highest = "";
    if (profile.quality == CamcorderProfile.QUALITY_HIGH) {
      highest = "Highest: ";
    }
    String type = "";
    if (profile.videoFrameWidth == 3840 && profile.videoFrameHeight == 2160) {
      type = "4K Ultra HD ";
    } else if (profile.videoFrameWidth == 1920 && profile.videoFrameHeight == 1080) {
      type = "Full HD ";
    } else if (profile.videoFrameWidth == 1280 && profile.videoFrameHeight == 720) {
      type = "HD ";
    } else if (profile.videoFrameWidth == 720 && profile.videoFrameHeight == 480) {
      type = "SD ";
    } else if (profile.videoFrameWidth == 640 && profile.videoFrameHeight == 480) {
      type = "VGA ";
    } else if (profile.videoFrameWidth == 352 && profile.videoFrameHeight == 288) {
      type = "CIF ";
    } else if (profile.videoFrameWidth == 320 && profile.videoFrameHeight == 240) {
      type = "QVGA ";
    } else if (profile.videoFrameWidth == 176 && profile.videoFrameHeight == 144) {
      type = "QCIF ";
    }
    return highest
        + type
        + profile.videoFrameWidth
        + "x"
        + profile.videoFrameHeight
        + " "
        + getAspectRatioMPString(profile.videoFrameWidth, profile.videoFrameHeight);
  }

  public double getTargetRatio() {
    return preview_targetRatio;
  }

  private double calculateTargetRatioForPreview(Point display_size) {
    double targetRatio;
    String preview_size = applicationInterface.getPreviewSizePref();
    // should always use wysiwig for video mode, otherwise we get incorrect aspect ratio shown when
    // recording video (at least on Galaxy Nexus, e.g., at 640x480)
    // also not using wysiwyg mode with video caused corruption on Samsung cameras (tested with
    // Samsung S3, Android 4.3, front camera, infinity focus)
    if (preview_size.equals("preference_preview_size_wysiwyg") || this.is_video) {
      {
        if (MyDebug.LOG) Log.d(TAG, "set preview aspect ratio from photo size (wysiwyg)");
        CameraController.Size picture_size = camera_controller.getPictureSize();
        if (MyDebug.LOG)
          Log.d(TAG, "picture_size: " + picture_size.width + " x " + picture_size.height);
        targetRatio = ((double) picture_size.width) / (double) picture_size.height;
      }
    } else {
      if (MyDebug.LOG) Log.d(TAG, "set preview aspect ratio from display size");
      // base target ratio from display size - means preview will fill the device's display as much
      // as possible
      // but if the preview's aspect ratio differs from the actual photo/video size, the preview
      // will show a cropped version of what is actually taken
      targetRatio = ((double) display_size.x) / (double) display_size.y;
    }
    this.preview_targetRatio = targetRatio;
    if (MyDebug.LOG) Log.d(TAG, "targetRatio: " + targetRatio);
    return targetRatio;
  }

  private CameraController.Size getClosestSize(
      List<CameraController.Size> sizes, double targetRatio) {
    if (MyDebug.LOG) Log.d(TAG, "getClosestSize()");
    CameraController.Size optimalSize = null;
    double minDiff = Double.MAX_VALUE;
    for (CameraController.Size size : sizes) {
      double ratio = (double) size.width / size.height;
      if (Math.abs(ratio - targetRatio) < minDiff) {
        optimalSize = size;
        minDiff = Math.abs(ratio - targetRatio);
      }
    }
    return optimalSize;
  }

  public CameraController.Size getOptimalPreviewSize(List<CameraController.Size> sizes) {
    if (MyDebug.LOG) Log.d(TAG, "getOptimalPreviewSize()");
    final double ASPECT_TOLERANCE = 0.05;
    if (sizes == null) return null;
    CameraController.Size optimalSize = null;
    double minDiff = Double.MAX_VALUE;
    Point display_size = new Point();
    Activity activity = (Activity) this.getContext();
    {
      Display display = activity.getWindowManager().getDefaultDisplay();
      display.getSize(display_size);
      if (MyDebug.LOG) Log.d(TAG, "display_size: " + display_size.x + " x " + display_size.y);
    }
    double targetRatio = calculateTargetRatioForPreview(display_size);
    int targetHeight = Math.min(display_size.y, display_size.x);
    if (targetHeight <= 0) {
      targetHeight = display_size.y;
    }
    // Try to find the size which matches the aspect ratio, and is closest match to display height
    for (CameraController.Size size : sizes) {
      if (MyDebug.LOG) Log.d(TAG, "    supported preview size: " + size.width + ", " + size.height);
      double ratio = (double) size.width / size.height;
      if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
      if (Math.abs(size.height - targetHeight) < minDiff) {
        optimalSize = size;
        minDiff = Math.abs(size.height - targetHeight);
      }
    }
    if (optimalSize == null) {
      // can't find match for aspect ratio, so find closest one
      if (MyDebug.LOG) Log.d(TAG, "no preview size matches the aspect ratio");
      optimalSize = getClosestSize(sizes, targetRatio);
    }
    if (MyDebug.LOG) {
      Log.d(TAG, "chose optimalSize: " + optimalSize.width + " x " + optimalSize.height);
      Log.d(TAG, "optimalSize ratio: " + ((double) optimalSize.width / optimalSize.height));
    }
    return optimalSize;
  }

  public CameraController.Size getOptimalVideoPictureSize(
      List<CameraController.Size> sizes, double targetRatio) {
    if (MyDebug.LOG) Log.d(TAG, "getOptimalVideoPictureSize()");
    final double ASPECT_TOLERANCE = 0.05;
    if (sizes == null) return null;
    CameraController.Size optimalSize = null;
    // Try to find largest size that matches aspect ratio
    for (CameraController.Size size : sizes) {
      if (MyDebug.LOG) Log.d(TAG, "    supported preview size: " + size.width + ", " + size.height);
      double ratio = (double) size.width / size.height;
      if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
      if (optimalSize == null || size.width > optimalSize.width) {
        optimalSize = size;
      }
    }
    if (optimalSize == null) {
      // can't find match for aspect ratio, so find closest one
      if (MyDebug.LOG) Log.d(TAG, "no picture size matches the aspect ratio");
      optimalSize = getClosestSize(sizes, targetRatio);
    }
    if (MyDebug.LOG) {
      Log.d(TAG, "chose optimalSize: " + optimalSize.width + " x " + optimalSize.height);
      Log.d(TAG, "optimalSize ratio: " + ((double) optimalSize.width / optimalSize.height));
    }
    return optimalSize;
  }

  private void setAspectRatio(double ratio) {
    if (ratio <= 0.0) throw new IllegalArgumentException();

    has_aspect_ratio = true;
    if (aspect_ratio != ratio) {
      aspect_ratio = ratio;
      if (MyDebug.LOG) Log.d(TAG, "new aspect ratio: " + aspect_ratio);
      cameraSurface.getView().requestLayout();
      if (canvasView != null) {
        canvasView.requestLayout();
      }
    }
  }

  private boolean hasAspectRatio() {
    return has_aspect_ratio;
  }

  private double getAspectRatio() {
    return aspect_ratio;
  }

  /** Returns the ROTATION_* enum of the display relative to the natural device orientation. */
  public int getDisplayRotation() {
    // gets the display rotation (as a Surface.ROTATION_* constant), taking into account the
    // getRotatePreviewPreferenceKey() setting
    Activity activity = (Activity) this.getContext();
    int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();

    String rotate_preview = applicationInterface.getPreviewRotationPref();
    if (MyDebug.LOG) Log.d(TAG, "    rotate_preview = " + rotate_preview);
    if (rotate_preview.equals("180")) {
      switch (rotation) {
        case Surface.ROTATION_0:
          rotation = Surface.ROTATION_180;
          break;
        case Surface.ROTATION_90:
          rotation = Surface.ROTATION_270;
          break;
        case Surface.ROTATION_180:
          rotation = Surface.ROTATION_0;
          break;
        case Surface.ROTATION_270:
          rotation = Surface.ROTATION_90;
          break;
        default:
          break;
      }
    }

    return rotation;
  }

  /** Returns the rotation in degrees of the display relative to the natural device orientation. */
  private int getDisplayRotationDegrees() {
    if (MyDebug.LOG) Log.d(TAG, "getDisplayRotationDegrees");
    int rotation = getDisplayRotation();
    int degrees = 0;
    switch (rotation) {
      case Surface.ROTATION_0:
        degrees = 0;
        break;
      case Surface.ROTATION_90:
        degrees = 90;
        break;
      case Surface.ROTATION_180:
        degrees = 180;
        break;
      case Surface.ROTATION_270:
        degrees = 270;
        break;
      default:
        break;
    }
    if (MyDebug.LOG) Log.d(TAG, "    degrees = " + degrees);
    return degrees;
  }

  // for the Preview - from
  // http://developer.android.com/reference/android/hardware/Camera.html#setDisplayOrientation(int)
  // note, if orientation is locked to landscape this is only called when setting up the activity,
  // and will always have the same orientation
  public void setCameraDisplayOrientation() {
    if (MyDebug.LOG) Log.d(TAG, "setCameraDisplayOrientation()");
    if (camera_controller == null) {
      if (MyDebug.LOG) Log.d(TAG, "camera not opened!");
      return;
    }
    if (using_android_l) {
      // need to configure the textureview
      configureTransform();
    } else {
      int degrees = getDisplayRotationDegrees();
      if (MyDebug.LOG) Log.d(TAG, "    degrees = " + degrees);
      // note the code to make the rotation relative to the camera sensor is done in
      // camera_controller.setDisplayOrientation()
      camera_controller.setDisplayOrientation(degrees);
    }
  }

  // for taking photos - from
  // http://developer.android.com/reference/android/hardware/Camera.Parameters.html#setRotation(int)
  private void onOrientationChanged(int orientation) {
    /*if( MyDebug.LOG ) {
    	Log.d(TAG, "onOrientationChanged()");
    	Log.d(TAG, "orientation: " + orientation);
    }*/
    if (orientation == OrientationEventListener.ORIENTATION_UNKNOWN) return;
    if (camera_controller == null) {
      /*if( MyDebug.LOG )
      Log.d(TAG, "camera not opened!");*/
      return;
    }
    orientation = (orientation + 45) / 90 * 90;
    this.current_orientation = orientation % 360;
    int new_rotation;
    int camera_orientation = camera_controller.getCameraOrientation();
    if (camera_controller.isFrontFacing()) {
      new_rotation = (camera_orientation - orientation + 360) % 360;
    } else {
      new_rotation = (camera_orientation + orientation) % 360;
    }
    if (new_rotation != current_rotation) {
      /*if( MyDebug.LOG ) {
      	Log.d(TAG, "    current_orientation is " + current_orientation);
      	Log.d(TAG, "    info orientation is " + camera_orientation);
      	Log.d(TAG, "    set Camera rotation from " + current_rotation + " to " + new_rotation);
      }*/
      this.current_rotation = new_rotation;
    }
  }

  private int getDeviceDefaultOrientation() {
    WindowManager windowManager =
        (WindowManager) this.getContext().getSystemService(Context.WINDOW_SERVICE);
    Configuration config = getResources().getConfiguration();
    int rotation = windowManager.getDefaultDisplay().getRotation();
    if (((rotation == Surface.ROTATION_0 || rotation == Surface.ROTATION_180)
            && config.orientation == Configuration.ORIENTATION_LANDSCAPE)
        || ((rotation == Surface.ROTATION_90 || rotation == Surface.ROTATION_270)
            && config.orientation == Configuration.ORIENTATION_PORTRAIT)) {
      return Configuration.ORIENTATION_LANDSCAPE;
    } else {
      return Configuration.ORIENTATION_PORTRAIT;
    }
  }

  /* Returns the rotation (in degrees) to use for images/videos, taking the preference_lock_orientation into account.
   */
  private int getImageVideoRotation() {
    if (MyDebug.LOG)
      Log.d(TAG, "getImageVideoRotation() from current_rotation " + current_rotation);
    String lock_orientation = applicationInterface.getLockOrientationPref();
    if (lock_orientation.equals("landscape")) {
      int camera_orientation = camera_controller.getCameraOrientation();
      int device_orientation = getDeviceDefaultOrientation();
      int result;
      if (device_orientation == Configuration.ORIENTATION_PORTRAIT) {
        // should be equivalent to onOrientationChanged(270)
        if (camera_controller.isFrontFacing()) {
          result = (camera_orientation + 90) % 360;
        } else {
          result = (camera_orientation + 270) % 360;
        }
      } else {
        // should be equivalent to onOrientationChanged(0)
        result = camera_orientation;
      }
      if (MyDebug.LOG) Log.d(TAG, "getImageVideoRotation() lock to landscape, returns " + result);
      return result;
    } else if (lock_orientation.equals("portrait")) {
      int camera_orientation = camera_controller.getCameraOrientation();
      int result;
      int device_orientation = getDeviceDefaultOrientation();
      if (device_orientation == Configuration.ORIENTATION_PORTRAIT) {
        // should be equivalent to onOrientationChanged(0)
        result = camera_orientation;
      } else {
        // should be equivalent to onOrientationChanged(90)
        if (camera_controller.isFrontFacing()) {
          result = (camera_orientation + 270) % 360;
        } else {
          result = (camera_orientation + 90) % 360;
        }
      }
      if (MyDebug.LOG) Log.d(TAG, "getImageVideoRotation() lock to portrait, returns " + result);
      return result;
    }
    if (MyDebug.LOG)
      Log.d(TAG, "getImageVideoRotation() returns current_rotation " + current_rotation);
    return this.current_rotation;
  }

  public void draw(Canvas canvas) {
    /*if( MyDebug.LOG )
    Log.d(TAG, "draw()");*/
    if (this.app_is_paused) {
      /*if( MyDebug.LOG )
      Log.d(TAG, "draw(): app is paused");*/
      return;
    }
    /*if( true ) // test
    return;*/
    /*if( MyDebug.LOG )
    Log.d(TAG, "ui_rotation: " + ui_rotation);*/
    /*if( MyDebug.LOG )
    Log.d(TAG, "canvas size " + canvas.getWidth() + " x " + canvas.getHeight());*/
    /*if( MyDebug.LOG )
    Log.d(TAG, "surface frame " + mHolder.getSurfaceFrame().width() + ", " + mHolder.getSurfaceFrame().height());*/

    if (this.focus_success != FOCUS_DONE) {
      if (focus_complete_time != -1 && System.currentTimeMillis() > focus_complete_time + 1000) {
        focus_success = FOCUS_DONE;
      }
    }
    applicationInterface.onDrawPreview(canvas);
  }

  public void scaleZoom(float scale_factor) {
    if (MyDebug.LOG) Log.d(TAG, "scaleZoom() " + scale_factor);
    if (this.camera_controller != null && this.has_zoom) {
      int zoom_factor = camera_controller.getZoom();
      float zoom_ratio = this.zoom_ratios.get(zoom_factor) / 100.0f;
      zoom_ratio *= scale_factor;

      int new_zoom_factor = zoom_factor;
      if (zoom_ratio <= 1.0f) {
        new_zoom_factor = 0;
      } else if (zoom_ratio >= zoom_ratios.get(max_zoom_factor) / 100.0f) {
        new_zoom_factor = max_zoom_factor;
      } else {
        // find the closest zoom level
        if (scale_factor > 1.0f) {
          // zooming in
          for (int i = zoom_factor; i < zoom_ratios.size(); i++) {
            if (zoom_ratios.get(i) / 100.0f >= zoom_ratio) {
              if (MyDebug.LOG)
                Log.d(
                    TAG,
                    "zoom int, found new zoom by comparing "
                        + zoom_ratios.get(i) / 100.0f
                        + " >= "
                        + zoom_ratio);
              new_zoom_factor = i;
              break;
            }
          }
        } else {
          // zooming out
          for (int i = zoom_factor; i >= 0; i--) {
            if (zoom_ratios.get(i) / 100.0f <= zoom_ratio) {
              if (MyDebug.LOG)
                Log.d(
                    TAG,
                    "zoom out, found new zoom by comparing "
                        + zoom_ratios.get(i) / 100.0f
                        + " <= "
                        + zoom_ratio);
              new_zoom_factor = i;
              break;
            }
          }
        }
      }
      if (MyDebug.LOG) {
        Log.d(TAG, "ScaleListener.onScale zoom_ratio is now " + zoom_ratio);
        Log.d(
            TAG,
            "    old zoom_factor "
                + zoom_factor
                + " ratio "
                + zoom_ratios.get(zoom_factor) / 100.0f);
        Log.d(
            TAG,
            "    chosen new zoom_factor "
                + new_zoom_factor
                + " ratio "
                + zoom_ratios.get(new_zoom_factor) / 100.0f);
      }
      zoomTo(new_zoom_factor);
      applicationInterface.multitouchZoom(new_zoom_factor);
    }
  }

  public void zoomTo(int new_zoom_factor) {
    if (MyDebug.LOG) Log.d(TAG, "ZoomTo(): " + new_zoom_factor);
    if (new_zoom_factor < 0) new_zoom_factor = 0;
    else if (new_zoom_factor > max_zoom_factor) new_zoom_factor = max_zoom_factor;
    // problem where we crashed due to calling this function with null camera should be fixed now,
    // but check again just to be safe
    if (camera_controller != null) {
      if (this.has_zoom) {
        // don't cancelAutoFocus() here, otherwise we get sluggish zoom behaviour on Camera2 API
        camera_controller.setZoom(new_zoom_factor);
        applicationInterface.setZoomPref(new_zoom_factor);
        clearFocusAreas();
      }
    }
  }

  public void setFocusDistance(float new_focus_distance) {
    if (MyDebug.LOG) Log.d(TAG, "setFocusDistance: " + new_focus_distance);
    if (camera_controller != null) {
      if (new_focus_distance < 0.0f) new_focus_distance = 0.0f;
      else if (new_focus_distance > minimum_focus_distance)
        new_focus_distance = minimum_focus_distance;
      if (camera_controller.setFocusDistance(new_focus_distance)) {
        // now save
        applicationInterface.setFocusDistancePref(new_focus_distance);
        {
          String focus_distance_s;
          if (new_focus_distance > 0.0f) {
            float real_focus_distance = 1.0f / new_focus_distance;
            focus_distance_s =
                decimal_format_2dp.format(real_focus_distance)
                    + getResources().getString(R.string.metres_abbreviation);
          } else {
            focus_distance_s = getResources().getString(R.string.infinite);
          }
          showToast(
              seekbar_toast,
              getResources().getString(R.string.focus_distance) + " " + focus_distance_s);
        }
      }
    }
  }

  public void setExposure(int new_exposure) {
    if (MyDebug.LOG) Log.d(TAG, "setExposure(): " + new_exposure);
    if (camera_controller != null && (min_exposure != 0 || max_exposure != 0)) {
      cancelAutoFocus();
      if (new_exposure < min_exposure) new_exposure = min_exposure;
      else if (new_exposure > max_exposure) new_exposure = max_exposure;
      if (camera_controller.setExposureCompensation(new_exposure)) {
        // now save
        applicationInterface.setExposureCompensationPref(new_exposure);
        showToast(seekbar_toast, getExposureCompensationString(new_exposure), 96);
      }
    }
  }

  /**
   * Set a manual white balance temperature. The white balance mode must be set to "manual" for this
   * to have an effect.
   */
  public void setWhiteBalanceTemperature(int new_temperature) {
    if (MyDebug.LOG) Log.d(TAG, "seWhiteBalanceTemperature(): " + new_temperature);
    if (camera_controller != null) {
      if (camera_controller.setWhiteBalanceTemperature(new_temperature)) {
        // now save
        applicationInterface.setWhiteBalanceTemperaturePref(new_temperature);
        showToast(
            seekbar_toast,
            getResources().getString(R.string.white_balance) + " " + new_temperature,
            96);
      }
    }
  }

  public void setISO(int new_iso) {
    if (MyDebug.LOG) Log.d(TAG, "setISO(): " + new_iso);
    if (camera_controller != null && supports_iso_range) {
      if (new_iso < min_iso) new_iso = min_iso;
      else if (new_iso > max_iso) new_iso = max_iso;
      if (camera_controller.setISO(new_iso)) {
        // now save
        applicationInterface.setISOPref("" + new_iso);
        showToast(seekbar_toast, getISOString(new_iso), 96);
      }
    }
  }

  public void setExposureTime(long new_exposure_time) {
    if (MyDebug.LOG) Log.d(TAG, "setExposureTime(): " + new_exposure_time);
    if (camera_controller != null && supports_exposure_time) {
      if (new_exposure_time < min_exposure_time) new_exposure_time = min_exposure_time;
      else if (new_exposure_time > max_exposure_time) new_exposure_time = max_exposure_time;
      if (camera_controller.setExposureTime(new_exposure_time)) {
        // now save
        applicationInterface.setExposureTimePref(new_exposure_time);
        showToast(seekbar_toast, getExposureTimeString(new_exposure_time), 96);
      }
    }
  }

  public String getExposureCompensationString(int exposure) {
    float exposure_ev = exposure * exposure_step;
    return getResources().getString(R.string.exposure_compensation)
        + " "
        + (exposure > 0 ? "+" : "")
        + decimal_format_2dp.format(exposure_ev)
        + " EV";
  }

  public String getISOString(int iso) {
    return getResources().getString(R.string.iso) + " " + iso;
  }

  public String getExposureTimeString(long exposure_time) {
    double exposure_time_s = exposure_time / 1000000000.0;
    String string;
    if (exposure_time >= 500000000) {
      // show exposure times of more than 0.5s directly
      string =
          decimal_format_1dp.format(exposure_time_s)
              + getResources().getString(R.string.seconds_abbreviation);
    } else {
      double exposure_time_r = 1.0 / exposure_time_s;
      string =
          " 1/"
              + decimal_format_1dp.format(exposure_time_r)
              + getResources().getString(R.string.seconds_abbreviation);
    }
    return string;
  }

  /*public String getFrameDurationString(long frame_duration) {
  	double frame_duration_s = frame_duration/1000000000.0;
  	double frame_duration_r = 1.0/frame_duration_s;
  	return getResources().getString(R.string.fps) + " " + decimal_format_1dp.format(frame_duration_r);
  }*/

  /*private String getFocusOneDistanceString(float dist) {
  	if( dist == 0.0f )
  		return "inf.";
  	float real_dist = 1.0f/dist;
  	return decimal_format_2dp.format(real_dist) + getResources().getString(R.string.metres_abbreviation);
  }

  public String getFocusDistanceString(float dist_min, float dist_max) {
  	String f_s = "f ";
  	//if( dist_min == dist_max )
  	//	return f_s + getFocusOneDistanceString(dist_min);
  	//return f_s + getFocusOneDistanceString(dist_min) + "-" + getFocusOneDistanceString(dist_max);
  	// just always show max for now
  	return f_s + getFocusOneDistanceString(dist_max);
  }*/

  public boolean canSwitchCamera() {
    if (this.phase == PHASE_TAKING_PHOTO) {
      // just to be safe - risk of cancelling the autofocus before taking a photo, or otherwise
      // messing things up
      if (MyDebug.LOG) Log.d(TAG, "currently taking a photo");
      return false;
    }
    int n_cameras = camera_controller_manager.getNumberOfCameras();
    if (MyDebug.LOG) Log.d(TAG, "found " + n_cameras + " cameras");
    if (n_cameras == 0) return false;
    return true;
  }

  public void setCamera(int cameraId) {
    if (MyDebug.LOG) Log.d(TAG, "setCamera()");
    if (cameraId < 0 || cameraId >= camera_controller_manager.getNumberOfCameras()) {
      if (MyDebug.LOG) Log.d(TAG, "invalid cameraId: " + cameraId);
      cameraId = 0;
    }
    if (canSwitchCamera()) {
      closeCamera();
      applicationInterface.setCameraIdPref(cameraId);
      this.openCamera();
    }
  }

  public static int[] matchPreviewFpsToVideo(List<int[]> fps_ranges, int video_frame_rate) {
    if (MyDebug.LOG) Log.d(TAG, "matchPreviewFpsToVideo()");
    int selected_min_fps = -1, selected_max_fps = -1, selected_diff = -1;
    for (int[] fps_range : fps_ranges) {
      if (MyDebug.LOG) {
        Log.d(TAG, "    supported fps range: " + fps_range[0] + " to " + fps_range[1]);
      }
      int min_fps = fps_range[0];
      int max_fps = fps_range[1];
      if (min_fps <= video_frame_rate && max_fps >= video_frame_rate) {
        int diff = max_fps - min_fps;
        if (selected_diff == -1 || diff < selected_diff) {
          selected_min_fps = min_fps;
          selected_max_fps = max_fps;
          selected_diff = diff;
        }
      }
    }
    if (selected_min_fps != -1) {
      if (MyDebug.LOG) {
        Log.d(TAG, "    chosen fps range: " + selected_min_fps + " to " + selected_max_fps);
      }
    } else {
      selected_diff = -1;
      int selected_dist = -1;
      for (int[] fps_range : fps_ranges) {
        int min_fps = fps_range[0];
        int max_fps = fps_range[1];
        int diff = max_fps - min_fps;
        int dist;
        if (max_fps < video_frame_rate) dist = video_frame_rate - max_fps;
        else dist = min_fps - video_frame_rate;
        if (MyDebug.LOG) {
          Log.d(
              TAG,
              "    supported fps range: "
                  + min_fps
                  + " to "
                  + max_fps
                  + " has dist "
                  + dist
                  + " and diff "
                  + diff);
        }
        if (selected_dist == -1
            || dist < selected_dist
            || (dist == selected_dist && diff < selected_diff)) {
          selected_min_fps = min_fps;
          selected_max_fps = max_fps;
          selected_dist = dist;
          selected_diff = diff;
        }
      }
      if (MyDebug.LOG)
        Log.d(
            TAG,
            "    can't find match for fps range, so choose closest: "
                + selected_min_fps
                + " to "
                + selected_max_fps);
    }
    return new int[] {selected_min_fps, selected_max_fps};
  }

  public static int[] chooseBestPreviewFps(List<int[]> fps_ranges) {
    if (MyDebug.LOG) Log.d(TAG, "chooseBestPreviewFps()");

    // find value with lowest min that has max >= 30; if more than one of these, pick the one with
    // highest max
    int selected_min_fps = -1, selected_max_fps = -1;
    for (int[] fps_range : fps_ranges) {
      if (MyDebug.LOG) {
        Log.d(TAG, "    supported fps range: " + fps_range[0] + " to " + fps_range[1]);
      }
      int min_fps = fps_range[0];
      int max_fps = fps_range[1];
      if (max_fps >= 30000) {
        if (selected_min_fps == -1 || min_fps < selected_min_fps) {
          selected_min_fps = min_fps;
          selected_max_fps = max_fps;
        } else if (min_fps == selected_min_fps && max_fps > selected_max_fps) {
          selected_min_fps = min_fps;
          selected_max_fps = max_fps;
        }
      }
    }

    if (selected_min_fps != -1) {
      if (MyDebug.LOG) {
        Log.d(TAG, "    chosen fps range: " + selected_min_fps + " to " + selected_max_fps);
      }
    } else {
      // just pick the widest range; if more than one, pick the one with highest max
      int selected_diff = -1;
      for (int[] fps_range : fps_ranges) {
        int min_fps = fps_range[0];
        int max_fps = fps_range[1];
        int diff = max_fps - min_fps;
        if (selected_diff == -1 || diff > selected_diff) {
          selected_min_fps = min_fps;
          selected_max_fps = max_fps;
          selected_diff = diff;
        } else if (diff == selected_diff && max_fps > selected_max_fps) {
          selected_min_fps = min_fps;
          selected_max_fps = max_fps;
          selected_diff = diff;
        }
      }
      if (MyDebug.LOG)
        Log.d(
            TAG,
            "    can't find fps range 30fps or better, so picked widest range: "
                + selected_min_fps
                + " to "
                + selected_max_fps);
    }
    return new int[] {selected_min_fps, selected_max_fps};
  }

  private void setFocusPref(boolean auto_focus) {
    if (MyDebug.LOG) Log.d(TAG, "setFocusPref()");
    String focus_value = applicationInterface.getFocusPref(is_video);
    if (focus_value.length() > 0) {
      if (MyDebug.LOG) Log.d(TAG, "found existing focus_value: " + focus_value);
      if (!updateFocus(
          focus_value,
          true,
          false,
          auto_focus)) { // don't need to save, as this is the value that's already saved
        if (MyDebug.LOG) Log.d(TAG, "focus value no longer supported!");
        updateFocus(0, true, true, auto_focus);
      }
    } else {
      if (MyDebug.LOG) Log.d(TAG, "found no existing focus_value");
      // here we set the default values for focus mode
      // note if updating default focus value for photo mode, also update
      // MainActivityTest.setToDefault()
      updateFocus(
          is_video ? "focus_mode_continuous_video" : "focus_mode_continuous_picture",
          true,
          true,
          auto_focus);
    }
  }

  /**
   * If in video mode, update the focus mode if necessary to be continuous video focus mode (if that
   * mode is available). Normally we remember the user-specified focus value. And even setting the
   * default is done in setFocusPref(). This method is used as a workaround for a bug on Samsung
   * Galaxy S5 with UHD, where if the user switches to another (non-continuous-video) focus mode,
   * then goes to Settings, then returns and records video, the preview freezes and the video is
   * corrupted.
   *
   * @return If the focus mode is changed, this returns the previous focus mode; else it returns
   *     null.
   */
  public String updateFocusForVideo() {
    if (MyDebug.LOG) Log.d(TAG, "updateFocusForVideo()");
    String old_focus_mode = null;
    if (this.supported_focus_values != null && camera_controller != null && is_video) {
      if (MyDebug.LOG) Log.d(TAG, "need to change focus mode");
      old_focus_mode = this.getCurrentFocusValue();
      updateFocus(
          "focus_mode_continuous_video",
          true,
          false,
          false); // don't save, as we're just changing focus mode temporarily for the Samsung S5
      // video hack
    }
    return old_focus_mode;
  }

  public void updateFlash(String focus_value) {
    if (MyDebug.LOG) Log.d(TAG, "updateFlash(): " + focus_value);
    if (this.phase == PHASE_TAKING_PHOTO && !is_video) {
      // just to be safe - risk of cancelling the autofocus before taking a photo, or otherwise
      // messing things up
      if (MyDebug.LOG) Log.d(TAG, "currently taking a photo");
      return;
    }
    updateFlash(focus_value, true);
  }

  private boolean updateFlash(String flash_value, boolean save) {
    if (MyDebug.LOG) Log.d(TAG, "updateFlash(): " + flash_value);
    if (supported_flash_values != null) {
      int new_flash_index = supported_flash_values.indexOf(flash_value);
      if (MyDebug.LOG) Log.d(TAG, "new_flash_index: " + new_flash_index);
      if (new_flash_index != -1) {
        updateFlash(new_flash_index, save);
        return true;
      }
    }
    return false;
  }

  private void updateFlash(int new_flash_index, boolean save) {
    if (MyDebug.LOG) Log.d(TAG, "updateFlash(): " + new_flash_index);
    // updates the Flash button, and Flash camera mode
    if (supported_flash_values != null && new_flash_index != current_flash_index) {
      boolean initial = current_flash_index == -1;
      current_flash_index = new_flash_index;
      if (MyDebug.LOG)
        Log.d(
            TAG,
            "    current_flash_index is now " + current_flash_index + " (initial " + initial + ")");

      // Activity activity = (Activity)this.getContext();
      String[] flash_entries = getResources().getStringArray(R.array.flash_entries);
      // String [] flash_icons = getResources().getStringArray(R.array.flash_icons);
      String flash_value = supported_flash_values.get(current_flash_index);
      if (MyDebug.LOG) Log.d(TAG, "    flash_value: " + flash_value);
      String[] flash_values = getResources().getStringArray(R.array.flash_values);
      for (int i = 0; i < flash_values.length; i++) {
        /*if( MyDebug.LOG )
        Log.d(TAG, "    compare to: " + flash_values[i]);*/
        if (flash_value.equals(flash_values[i])) {
          if (MyDebug.LOG) Log.d(TAG, "    found entry: " + i);
          if (!initial) {
            showToast(flash_toast, flash_entries[i]);
          }
          break;
        }
      }
      this.setFlash(flash_value);
      if (save) {
        // now save
        applicationInterface.setFlashPref(flash_value);
      }
    } else if (supported_flash_values != null
        && current_flash_index != -1
        && current_flash_index != supported_flash_values.indexOf("flash_off")) {
      // called when the user clicks on the same flash button again to disable it
      current_flash_index = supported_flash_values.indexOf("flash_off");
      if (MyDebug.LOG) Log.d(TAG, "    current_flash_index is now " + current_flash_index);
      String[] flash_entries = getResources().getStringArray(R.array.flash_entries);
      showToast(flash_toast, flash_entries[0]);
      this.setFlash("flash_off");
      if (save) {
        // now save
        applicationInterface.setFlashPref("flash_off");
      }
    }
  }

  private void setFlash(String flash_value) {
    if (MyDebug.LOG) Log.d(TAG, "setFlash() " + flash_value);
    set_flash_value_after_autofocus =
        ""; // this overrides any previously saved setting, for during the startup autofocus
    if (camera_controller == null) {
      if (MyDebug.LOG) Log.d(TAG, "camera not opened!");
      return;
    }
    cancelAutoFocus();
    camera_controller.setFlashValue(flash_value);
  }

  // this returns the flash value indicated by the UI, rather than from the camera parameters (may
  // be different, e.g., in startup autofocus!)
  public String getCurrentFlashValue() {
    if (this.current_flash_index == -1) return null;
    return this.supported_flash_values.get(current_flash_index);
  }

  // this returns the flash mode indicated by the UI, rather than from the camera parameters (may be
  // different, e.g., in startup autofocus!)
  /*public String getCurrentFlashMode() {
  	if( current_flash_index == -1 )
  		return null;
  	String flash_value = supported_flash_values.get(current_flash_index);
  	String flash_mode = convertFlashValueToMode(flash_value);
  	return flash_mode;
  }*/

  public void updateFocus(String focus_value, boolean quiet, boolean auto_focus) {
    if (MyDebug.LOG) Log.d(TAG, "updateFocus(): " + focus_value);
    if (this.phase == PHASE_TAKING_PHOTO) {
      // just to be safe - otherwise problem that changing the focus mode will cancel the autofocus
      // before taking a photo, so we never take a photo, but is_taking_photo remains true!
      if (MyDebug.LOG) Log.d(TAG, "currently taking a photo");
      return;
    }
    updateFocus(focus_value, quiet, true, auto_focus);
  }

  private boolean supportedFocusValue(String focus_value) {
    if (MyDebug.LOG) Log.d(TAG, "supportedFocusValue(): " + focus_value);
    if (this.supported_focus_values != null) {
      int new_focus_index = supported_focus_values.indexOf(focus_value);
      if (MyDebug.LOG) Log.d(TAG, "new_focus_index: " + new_focus_index);
      return new_focus_index != -1;
    }
    return false;
  }

  private boolean updateFocus(String focus_value, boolean quiet, boolean save, boolean auto_focus) {
    if (MyDebug.LOG) Log.d(TAG, "updateFocus(): " + focus_value);
    if (this.supported_focus_values != null) {
      int new_focus_index = supported_focus_values.indexOf(focus_value);
      if (MyDebug.LOG) Log.d(TAG, "new_focus_index: " + new_focus_index);
      if (new_focus_index != -1) {
        updateFocus(new_focus_index, quiet, save, auto_focus);
        return true;
      }
    }
    return false;
  }

  private String findEntryForValue(String value, int entries_id, int values_id) {
    String[] entries = getResources().getStringArray(entries_id);
    String[] values = getResources().getStringArray(values_id);
    for (int i = 0; i < values.length; i++) {
      if (MyDebug.LOG) Log.d(TAG, "    compare to value: " + values[i]);
      if (value.equals(values[i])) {
        if (MyDebug.LOG) Log.d(TAG, "    found entry: " + i);
        return entries[i];
      }
    }
    return null;
  }

  public String findFocusEntryForValue(String focus_value) {
    return findEntryForValue(focus_value, R.array.focus_mode_entries, R.array.focus_mode_values);
  }

  private void updateFocus(int new_focus_index, boolean quiet, boolean save, boolean auto_focus) {
    if (MyDebug.LOG)
      Log.d(
          TAG,
          "updateFocus(): " + new_focus_index + " current_focus_index: " + current_focus_index);
    // updates the Focus button, and Focus camera mode
    if (this.supported_focus_values != null && new_focus_index != current_focus_index) {
      current_focus_index = new_focus_index;
      if (MyDebug.LOG) Log.d(TAG, "    current_focus_index is now " + current_focus_index);

      String focus_value = supported_focus_values.get(current_focus_index);
      if (MyDebug.LOG) Log.d(TAG, "    focus_value: " + focus_value);
      if (!quiet) {
        String focus_entry = findFocusEntryForValue(focus_value);
        if (focus_entry != null) {
          showToast(focus_toast, focus_entry);
        }
      }
      this.setFocusValue(focus_value, auto_focus);

      if (save) {
        // now save
        applicationInterface.setFocusPref(focus_value, is_video);
      }
    } else if (this.supported_focus_values != null && current_focus_index != 0) {
      // called when the user tries to deselect an selected focus value.
      current_focus_index = 0;
      if (MyDebug.LOG) Log.d(TAG, "    current_focus_index is now " + current_focus_index);
      String focus_value = supported_focus_values.get(current_focus_index);
      if (MyDebug.LOG) Log.d(TAG, "    focus_value: " + focus_value);
      if (!quiet) {
        String focus_entry = findFocusEntryForValue(focus_value);
        if (focus_entry != null) {
          showToast(focus_toast, focus_entry);
        }
      }
      this.setFocusValue(focus_value, auto_focus);

      if (save) {
        // now save
        applicationInterface.setFocusPref(focus_value, is_video);
      }
    }
  }

  /** This returns the flash mode indicated by the UI, rather than from the camera parameters. */
  public String getCurrentFocusValue() {
    if (MyDebug.LOG) Log.d(TAG, "getCurrentFocusValue()");
    if (camera_controller == null) {
      if (MyDebug.LOG) Log.d(TAG, "camera not opened!");
      return null;
    }
    if (this.supported_focus_values != null && this.current_focus_index != -1)
      return this.supported_focus_values.get(current_focus_index);
    return null;
  }

  private void setFocusValue(String focus_value, boolean auto_focus) {
    if (MyDebug.LOG) Log.d(TAG, "setFocusValue() " + focus_value);
    if (camera_controller == null) {
      if (MyDebug.LOG) Log.d(TAG, "camera not opened!");
      return;
    }
    cancelAutoFocus();
    removePendingContinuousFocusReset(); // this isn't strictly needed as the
    // reset_continuous_focus_runnable will check the ui focus
    // mode when it runs, but good to remove it anyway
    autofocus_in_continuous_mode = false;
    camera_controller.setFocusValue(focus_value);
    setupContinuousFocusMove();
    clearFocusAreas();
    if (auto_focus && !focus_value.equals("focus_mode_locked")) {
      tryAutoFocus(false, false);
    }
  }

  private void setupContinuousFocusMove() {
    if (MyDebug.LOG) Log.d(TAG, "setupContinuousFocusMove()");
    if (continuous_focus_move_is_started) {
      continuous_focus_move_is_started = false;
      applicationInterface.onContinuousFocusMove(false);
    }
    String focus_value =
        current_focus_index != -1 ? supported_focus_values.get(current_focus_index) : null;
    if (MyDebug.LOG) Log.d(TAG, "focus_value is " + focus_value);
    if (camera_controller != null
        && focus_value != null
        && focus_value.equals("focus_mode_continuous_picture")
        && !this.is_video) {
      if (MyDebug.LOG) Log.d(TAG, "set continuous picture focus move callback");
      camera_controller.setContinuousFocusMoveCallback(
          new CameraController.ContinuousFocusMoveCallback() {
            @Override
            public void onContinuousFocusMove(boolean start) {
              if (start
                  != continuous_focus_move_is_started) { // filter out repeated calls with same
                // start value
                continuous_focus_move_is_started = start;
                count_cameraContinuousFocusMoving++;
                applicationInterface.onContinuousFocusMove(start);
              }
            }
          });
    } else if (camera_controller != null) {
      if (MyDebug.LOG) Log.d(TAG, "remove continuous picture focus move callback");
      camera_controller.setContinuousFocusMoveCallback(null);
    }
  }

  /** User has clicked the "take picture" button (or equivalent GUI operation). */
  public void takePicturePressed() {
    if (MyDebug.LOG) Log.d(TAG, "takePicturePressed");
    if (camera_controller == null) {
      if (MyDebug.LOG) Log.d(TAG, "camera not opened!");
      /*is_taking_photo_on_timer = false;
      is_taking_photo = false;*/
      this.phase = PHASE_NORMAL;
      return;
    }
    if (!this.has_surface) {
      if (MyDebug.LOG) Log.d(TAG, "preview surface not yet available");
      /*is_taking_photo_on_timer = false;
      is_taking_photo = false;*/
      this.phase = PHASE_NORMAL;
      return;
    }
    // if( is_taking_photo_on_timer ) {
    if (this.isOnTimer()) {
      cancelTimer();
      showToast(take_photo_toast, R.string.cancelled_timer);
      return;
    }
    // if( is_taking_photo ) {
    if (this.phase == PHASE_TAKING_PHOTO) {

      if (MyDebug.LOG) Log.d(TAG, "already taking a photo");
      if (remaining_burst_photos != 0) {
        remaining_burst_photos = 0;
        showToast(take_photo_toast, R.string.cancelled_burst_mode);
      }

      return;
    }

    // make sure that preview running (also needed to hide trash/share icons)
    this.startCameraPreview();

    // is_taking_photo = true;
    long timer_delay = applicationInterface.getTimerPref();

    String burst_mode_value = applicationInterface.getRepeatPref();
    if (burst_mode_value.equals("unlimited")) {
      if (MyDebug.LOG) Log.d(TAG, "unlimited burst");
      remaining_burst_photos = -1;
    } else {
      int n_burst;
      try {
        n_burst = Integer.parseInt(burst_mode_value);
        if (MyDebug.LOG) Log.d(TAG, "n_burst: " + n_burst);
      } catch (NumberFormatException e) {
        if (MyDebug.LOG)
          Log.e(TAG, "failed to parse preference_burst_mode value: " + burst_mode_value);
        e.printStackTrace();
        n_burst = 1;
      }
      remaining_burst_photos = n_burst - 1;
    }

    if (timer_delay == 0) {
      takePicture(false);
    } else {
      takePictureOnTimer(timer_delay, false);
    }
    if (MyDebug.LOG) Log.d(TAG, "takePicturePressed exit");
  }

  private void takePictureOnTimer(final long timer_delay, boolean repeated) {
    if (MyDebug.LOG) {
      Log.d(TAG, "takePictureOnTimer");
      Log.d(TAG, "timer_delay: " + timer_delay);
    }
    this.phase = PHASE_TIMER;
    class TakePictureTimerTask extends TimerTask {
      public void run() {
        if (beepTimerTask != null) {
          beepTimerTask.cancel();
          beepTimerTask = null;
        }
        Activity activity = (Activity) Preview.this.getContext();
        activity.runOnUiThread(
            new Runnable() {
              public void run() {
                // we run on main thread to avoid problem of camera closing at the same time
                // but still need to check that the camera hasn't closed or the task halted, since
                // TimerTask.run() started
                if (camera_controller != null && takePictureTimerTask != null) takePicture(false);
                else {
                  if (MyDebug.LOG)
                    Log.d(TAG, "takePictureTimerTask: don't take picture, as already cancelled");
                }
              }
            });
      }
    }
    take_photo_time = System.currentTimeMillis() + timer_delay;
    if (MyDebug.LOG) Log.d(TAG, "take photo at: " + take_photo_time);
    /*if( !repeated ) {
    	showToast(take_photo_toast, R.string.started_timer);
    }*/
    takePictureTimer.schedule(takePictureTimerTask = new TakePictureTimerTask(), timer_delay);

    class BeepTimerTask extends TimerTask {
      long remaining_time = timer_delay;

      public void run() {
        if (remaining_time > 0) { // check in case this isn't cancelled by time we take the photo
          applicationInterface.timerBeep(remaining_time);
        }
        remaining_time -= 1000;
      }
    }
    beepTimer.schedule(beepTimerTask = new BeepTimerTask(), 0, 1000);
  }

  /**
   * Initiate "take picture" command. In video mode this means starting video command. In photo mode
   * this may involve first autofocusing.
   */
  private void takePicture(boolean max_filesize_restart) {
    if (MyDebug.LOG) Log.d(TAG, "takePicture");
    // this.thumbnail_anim = false;
    this.phase = PHASE_TAKING_PHOTO;
    synchronized (this) {
      // synchronise for consistency (keep FindBugs happy)
      take_photo_after_autofocus = false;
    }
    if (camera_controller == null) {
      if (MyDebug.LOG) Log.d(TAG, "camera not opened!");
      /*is_taking_photo_on_timer = false;
      is_taking_photo = false;*/
      this.phase = PHASE_NORMAL;
      applicationInterface.cameraInOperation(false);
      return;
    }
    if (!this.has_surface) {
      if (MyDebug.LOG) Log.d(TAG, "preview surface not yet available");
      /*is_taking_photo_on_timer = false;
      is_taking_photo = false;*/
      this.phase = PHASE_NORMAL;
      applicationInterface.cameraInOperation(false);
      return;
    }

    boolean store_location = applicationInterface.getGeotaggingPref();
    if (store_location) {
      boolean require_location = applicationInterface.getRequireLocationPref();
      if (require_location) {
        if (applicationInterface.getLocation() != null) {
          // fine, we have location
        } else {
          if (MyDebug.LOG) Log.d(TAG, "location data required, but not available");
          showToast(null, R.string.location_not_available);
          this.phase = PHASE_NORMAL;
          applicationInterface.cameraInOperation(false);
          return;
        }
      }
    }

    takePhoto(false);
    if (MyDebug.LOG) Log.d(TAG, "takePicture exit");
  }

  /** Take photo. The caller should aready have set the phase to PHASE_TAKING_PHOTO. */
  private void takePhoto(boolean skip_autofocus) {
    if (MyDebug.LOG) Log.d(TAG, "takePhoto");
    applicationInterface.cameraInOperation(true);
    String current_ui_focus_value = getCurrentFocusValue();
    if (MyDebug.LOG) Log.d(TAG, "current_ui_focus_value is " + current_ui_focus_value);

    if (autofocus_in_continuous_mode) {
      if (MyDebug.LOG) Log.d(TAG, "continuous mode where user touched to focus");
      synchronized (this) {
        // as below, if an autofocus is in progress, then take photo when it's completed
        if (focus_success == FOCUS_WAITING) {
          if (MyDebug.LOG)
            Log.d(TAG, "autofocus_in_continuous_mode: take photo after current focus");
          take_photo_after_autofocus = true;
          camera_controller.setCaptureFollowAutofocusHint(true);
        } else {
          // when autofocus_in_continuous_mode==true, it means the user recently touched to focus in
          // continuous focus mode, so don't do another focus
          if (MyDebug.LOG) Log.d(TAG, "autofocus_in_continuous_mode: no need to refocus");
          takePhotoWhenFocused();
        }
      }
    } else if (camera_controller.focusIsContinuous()) {
      if (MyDebug.LOG) Log.d(TAG, "call autofocus for continuous focus mode");
      // we call via autoFocus(), to avoid risk of taking photo while the continuous focus is
      // focusing - risk of blurred photo, also sometimes get bug in such situations where we end of
      // repeatedly focusing
      // this is the case even if skip_autofocus is true (as we still can't guarantee that
      // continuous focusing might be occurring)
      // note: if the user touches to focus in continuous mode, we camera controller may be in auto
      // focus mode, so we should only enter this codepath if the camera_controller is in continuous
      // focus mode
      CameraController.AutoFocusCallback autoFocusCallback =
          new CameraController.AutoFocusCallback() {
            @Override
            public void onAutoFocus(boolean success) {
              if (MyDebug.LOG) Log.d(TAG, "continuous mode autofocus complete: " + success);
              takePhotoWhenFocused();
            }
          };
      camera_controller.autoFocus(autoFocusCallback, true);
    } else if (skip_autofocus || this.recentlyFocused()) {
      if (MyDebug.LOG) {
        if (skip_autofocus) {
          Log.d(TAG, "skip_autofocus flag set");
        } else {
          Log.d(TAG, "recently focused successfully, so no need to refocus");
        }
      }
      takePhotoWhenFocused();
    } else if (current_ui_focus_value != null
        && (current_ui_focus_value.equals("focus_mode_auto")
            || current_ui_focus_value.equals("focus_mode_macro"))) {
      // n.b., we check focus_value rather than camera_controller.supportsAutoFocus(), as we want to
      // discount focus_mode_locked
      synchronized (this) {
        if (focus_success == FOCUS_WAITING) {
          // Needed to fix bug (on Nexus 6, old camera API): if flash was on, pointing at a dark
          // scene, and we take photo when already autofocusing, the autofocus never returned so we
          // got stuck!
          // In general, probably a good idea to not redo a focus - just use the one that's already
          // in progress
          if (MyDebug.LOG) Log.d(TAG, "take photo after current focus");
          take_photo_after_autofocus = true;
          camera_controller.setCaptureFollowAutofocusHint(true);
        } else {
          focus_success = FOCUS_DONE; // clear focus rectangle for new refocus
          CameraController.AutoFocusCallback autoFocusCallback =
              new CameraController.AutoFocusCallback() {
                @Override
                public void onAutoFocus(boolean success) {
                  if (MyDebug.LOG) Log.d(TAG, "autofocus complete: " + success);
                  ensureFlashCorrect(); // need to call this in case user takes picture before
                  // startup focus completes!
                  prepareAutoFocusPhoto();
                  takePhotoWhenFocused();
                }
              };
          if (MyDebug.LOG) Log.d(TAG, "start autofocus to take picture");
          camera_controller.autoFocus(autoFocusCallback, true);
          count_cameraAutoFocus++;
        }
      }
    } else {
      takePhotoWhenFocused();
    }
  }

  /**
   * Should be called when taking a photo immediately after an autofocus. This is needed for a
   * workaround for Camera2 bug (at least on Nexus 6) where photos sometimes come out dark when
   * using flash auto, when the flash fires. This happens when taking a photo in autofocus mode
   * (including when continuous mode has transitioned to autofocus mode due to touching to focus).
   * Seems to happen with scenes that have bright and dark regions, i.e., on verge of flash firing.
   * Seems to be fixed if we have a short delay...
   */
  private void prepareAutoFocusPhoto() {
    if (MyDebug.LOG) Log.d(TAG, "prepareAutoFocusPhoto");
    if (using_android_l) {
      String flash_value = camera_controller.getFlashValue();
      // getFlashValue() may return "" if flash not supported!
      if (flash_value.length() > 0
          && (flash_value.equals("flash_auto") || flash_value.equals("flash_red_eye"))) {
        if (MyDebug.LOG) Log.d(TAG, "wait for a bit...");
        try {
          Thread.sleep(100);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    }
  }

  /**
   * Take photo, assumes any autofocus has already been taken care of, and that
   * applicationInterface.cameraInOperation(true) has already been called. Note that even if a
   * caller wants to take a photo without focusing, you probably want to call takePhoto() with
   * skip_autofocus set to true (so that things work okay in continuous picture focus mode).
   */
  private void takePhotoWhenFocused() {
    // should be called when auto-focused
    if (MyDebug.LOG) Log.d(TAG, "takePhotoWhenFocused");
    if (camera_controller == null) {
      if (MyDebug.LOG) Log.d(TAG, "camera not opened!");
      /*is_taking_photo_on_timer = false;
      is_taking_photo = false;*/
      this.phase = PHASE_NORMAL;
      applicationInterface.cameraInOperation(false);
      return;
    }
    if (!this.has_surface) {
      if (MyDebug.LOG) Log.d(TAG, "preview surface not yet available");
      /*is_taking_photo_on_timer = false;
      is_taking_photo = false;*/
      this.phase = PHASE_NORMAL;
      applicationInterface.cameraInOperation(false);
      return;
    }

    final String focus_value =
        current_focus_index != -1 ? supported_focus_values.get(current_focus_index) : null;
    if (MyDebug.LOG) {
      Log.d(TAG, "focus_value is " + focus_value);
      Log.d(TAG, "focus_success is " + focus_success);
    }

    if (focus_value != null
        && focus_value.equals("focus_mode_locked")
        && focus_success == FOCUS_WAITING) {
      // make sure there isn't an autofocus in progress - can happen if in locked mode we take a
      // photo while autofocusing - see testTakePhotoLockedFocus() (although that test doesn't
      // always properly test the bug...)
      // we only cancel when in locked mode and if still focusing, as I had 2 bug reports for v1.16
      // that the photo was being taken out of focus; both reports said it worked fine in 1.15, and
      // one confirmed that it was due to the cancelAutoFocus() line, and that it's now fixed with
      // this fix
      // they said this happened in every focus mode, including locked - so possible that on some
      // devices, cancelAutoFocus() actually pulls the camera out of focus, or reverts to preview
      // focus?
      cancelAutoFocus();
    }
    removePendingContinuousFocusReset(); // to avoid switching back to continuous focus mode while
    // taking a photo - instead we'll always make sure we
    // switch back after taking a photo
    updateParametersFromLocation(); // do this now, not before, so we don't set location parameters
    // during focus (sometimes get RuntimeException)

    focus_success = FOCUS_DONE; // clear focus rectangle if not already done
    successfully_focused = false; // so next photo taken will require an autofocus
    if (MyDebug.LOG) Log.d(TAG, "remaining_burst_photos: " + remaining_burst_photos);

    CameraController.PictureCallback pictureCallback =
        new CameraController.PictureCallback() {
          private boolean success = false; // whether jpeg callback succeeded
          private boolean has_date = false;
          private Date current_date = null;

          public void onStarted() {
            if (MyDebug.LOG) Log.d(TAG, "onStarted");
            applicationInterface.onCaptureStarted();
          }

          public void onCompleted() {
            if (MyDebug.LOG) Log.d(TAG, "onCompleted");
            applicationInterface.onPictureCompleted();
            if (!using_android_l) {
              is_preview_started =
                  false; // preview automatically stopped due to taking photo on original Camera API
            }
            phase =
                PHASE_NORMAL; // need to set this even if remaining burst photos, so we can restart
            // the preview
            if (remaining_burst_photos == -1 || remaining_burst_photos > 0) {
              if (!is_preview_started) {
                // we need to restart the preview; and we do this in the callback, as we need to
                // restart after saving the image
                // (otherwise this can fail, at least on Nexus 7)
                if (MyDebug.LOG)
                  Log.d(
                      TAG,
                      "burst mode photos remaining: onPictureTaken about to start preview: "
                          + remaining_burst_photos);
                startCameraPreview();
                if (MyDebug.LOG)
                  Log.d(
                      TAG,
                      "burst mode photos remaining: onPictureTaken started preview: "
                          + remaining_burst_photos);
              }
            } else {
              phase = PHASE_NORMAL;
              boolean pause_preview = applicationInterface.getPausePreviewPref();
              if (MyDebug.LOG) Log.d(TAG, "pause_preview? " + pause_preview);
              if (pause_preview && success) {
                if (is_preview_started) {
                  // need to manually stop preview on Android L Camera2
                  camera_controller.stopPreview();
                  is_preview_started = false;
                }
                setPreviewPaused(true);
              } else {
                if (!is_preview_started) {
                  // we need to restart the preview; and we do this in the callback, as we need to
                  // restart after saving the image
                  // (otherwise this can fail, at least on Nexus 7)
                  startCameraPreview();
                }
                applicationInterface.cameraInOperation(false);
                if (MyDebug.LOG) Log.d(TAG, "onPictureTaken started preview");
              }
            }
            continuousFocusReset(); // in case we took a photo after user had touched to focus
            // (causing us to switch from continuous to autofocus mode)
            if (camera_controller != null
                && focus_value != null
                && (focus_value.equals("focus_mode_continuous_picture")
                    || focus_value.equals("focus_mode_continuous_video"))) {
              if (MyDebug.LOG) Log.d(TAG, "cancelAutoFocus to restart continuous focusing");
              camera_controller.cancelAutoFocus(); // needed to restart continuous focusing
            }

            if (MyDebug.LOG)
              Log.d(
                  TAG,
                  "do we need to take another photo? remaining_burst_photos: "
                      + remaining_burst_photos);
            if (remaining_burst_photos == -1 || remaining_burst_photos > 0) {
              if (remaining_burst_photos > 0) remaining_burst_photos--;

              long timer_delay = applicationInterface.getRepeatIntervalPref();
              if (timer_delay == 0) {
                // we set skip_autofocus to go straight to taking a photo rather than refocusing,
                // for speed
                // need to manually set the phase
                phase = PHASE_TAKING_PHOTO;
                takePhoto(true);
              } else {
                takePictureOnTimer(timer_delay, true);
              }
            }
          }

          /**
           * Ensures we get the same date for both JPEG and RAW; and that we set the date ASAP so
           * that it corresponds to actual photo time.
           */
          private void initDate() {
            if (!has_date) {
              has_date = true;
              current_date = new Date();
              if (MyDebug.LOG) Log.d(TAG, "picture taken on date: " + current_date);
            }
          }

          public void onPictureTaken(byte[] data) {
            if (MyDebug.LOG) Log.d(TAG, "onPictureTaken");
            // n.b., this is automatically run in a different thread
            initDate();
            if (!applicationInterface.onPictureTaken(data, current_date)) {
              if (MyDebug.LOG) Log.e(TAG, "applicationInterface.onPictureTaken failed");
              success = false;
            } else {
              success = true;
            }
          }

          public void onRawPictureTaken(DngCreator dngCreator, Image image) {
            if (MyDebug.LOG) Log.d(TAG, "onRawPictureTaken");
            initDate();
            if (!applicationInterface.onRawPictureTaken(dngCreator, image, current_date)) {
              if (MyDebug.LOG) Log.e(TAG, "applicationInterface.onRawPictureTaken failed");
            }
          }

          public void onBurstPictureTaken(List<byte[]> images) {
            if (MyDebug.LOG) Log.d(TAG, "onBurstPictureTaken");
            // n.b., this is automatically run in a different thread
            initDate();

            success = true;
            if (!applicationInterface.onBurstPictureTaken(images, current_date)) {
              if (MyDebug.LOG) Log.e(TAG, "applicationInterface.onBurstPictureTaken failed");
              success = false;
            }
          }

          public void onFrontScreenTurnOn() {
            if (MyDebug.LOG) Log.d(TAG, "onFrontScreenTurnOn");
            applicationInterface.turnFrontScreenFlashOn();
          }
        };
    CameraController.ErrorCallback errorCallback =
        new CameraController.ErrorCallback() {
          public void onError() {
            if (MyDebug.LOG) Log.e(TAG, "error from takePicture");
            count_cameraTakePicture--; // cancel out the increment from after the takePicture() call
            applicationInterface.onPhotoError();
            phase = PHASE_NORMAL;
            startCameraPreview();
            applicationInterface.cameraInOperation(false);
          }
        };
    {
      camera_controller.setRotation(getImageVideoRotation());

      if (PopupView.sound_index == 0) {
        enable_sound = true;
      } else {
        enable_sound = false;
      }
      if (MyDebug.LOG) Log.d(TAG, "enable_sound? " + enable_sound);
      camera_controller.enableShutterSound(enable_sound);
      if (using_android_l) {
        boolean use_camera2_fast_burst = applicationInterface.useCamera2FastBurst();
        if (MyDebug.LOG) Log.d(TAG, "use_camera2_fast_burst? " + use_camera2_fast_burst);
        camera_controller.setUseExpoFastBurst(use_camera2_fast_burst);
      }
      if (MyDebug.LOG) Log.d(TAG, "about to call takePicture");
      camera_controller.takePicture(pictureCallback, errorCallback);
      count_cameraTakePicture++;
    }
    if (MyDebug.LOG) Log.d(TAG, "takePhotoWhenFocused exit");
  }

  /*void clickedShare() {
  	if( MyDebug.LOG )
  		Log.d(TAG, "clickedShare");
  	//if( is_preview_paused ) {
  	if( this.phase == PHASE_PREVIEW_PAUSED ) {
  		if( preview_image_name != null ) {
  			if( MyDebug.LOG )
  				Log.d(TAG, "Share: " + preview_image_name);
  			Intent intent = new Intent(Intent.ACTION_SEND);
  			intent.setType("image/jpeg");
  			intent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + preview_image_name));
  			Activity activity = (Activity)this.getContext();
  			activity.startActivity(Intent.createChooser(intent, "Photo"));
  		}
  		startCameraPreview();
  		tryAutoFocus(false, false);
  	}
  }

  void clickedTrash() {
  	if( MyDebug.LOG )
  		Log.d(TAG, "clickedTrash");
  	//if( is_preview_paused ) {
  	if( this.phase == PHASE_PREVIEW_PAUSED ) {
  		if( preview_image_name != null ) {
  			if( MyDebug.LOG )
  				Log.d(TAG, "Delete: " + preview_image_name);
  			File file = new File(preview_image_name);
  			if( !file.delete() ) {
  				if( MyDebug.LOG )
  					Log.e(TAG, "failed to delete " + preview_image_name);
  			}
  			else {
  				if( MyDebug.LOG )
  					Log.d(TAG, "successfully deleted " + preview_image_name);
     	    	    showToast(null, R.string.photo_deleted);
  				applicationInterface.broadcastFile(file, false, false);
  			}
  		}
  		startCameraPreview();
  		tryAutoFocus(false, false);
  	}
     }*/

  public void requestAutoFocus() {
    if (MyDebug.LOG) Log.d(TAG, "requestAutoFocus");
    cancelAutoFocus();
    tryAutoFocus(false, true);
  }

  private void tryAutoFocus(final boolean startup, final boolean manual) {
    // manual: whether user has requested autofocus (e.g., by touching screen, or volume focus, or
    // hardware focus button)
    // consider whether you want to call requestAutoFocus() instead (which properly cancels any
    // in-progress auto-focus first)
    if (MyDebug.LOG) {
      Log.d(TAG, "tryAutoFocus");
      Log.d(TAG, "startup? " + startup);
      Log.d(TAG, "manual? " + manual);
    }
    if (camera_controller == null) {
      if (MyDebug.LOG) Log.d(TAG, "camera not opened!");
    } else if (!this.has_surface) {
      if (MyDebug.LOG) Log.d(TAG, "preview surface not yet available");
    } else if (!this.is_preview_started) {
      if (MyDebug.LOG) Log.d(TAG, "preview not yet started");
    }
    // else if( is_taking_photo ) {
    else if (!(manual && this.is_video) && this.isTakingPhotoOrOnTimer()) {
      // if taking a video, we allow manual autofocuses
      // autofocus may cause problem if there is a video corruption problem, see
      // testTakeVideoBitrate() on Nexus 7 at 30Mbs or 50Mbs, where the startup autofocus would
      // cause a problem here
      if (MyDebug.LOG) Log.d(TAG, "currently taking a photo");
    } else {
      if (manual) {
        // remove any previous request to switch back to continuous
        removePendingContinuousFocusReset();
      }
      if (manual
          && !is_video
          && camera_controller.focusIsContinuous()
          && supportedFocusValue("focus_mode_auto")) {
        if (MyDebug.LOG) Log.d(TAG, "switch from continuous to autofocus mode for touch focus");
        camera_controller.setFocusValue("focus_mode_auto"); // switch to autofocus
        autofocus_in_continuous_mode = true;
        // we switch back to continuous via a new reset_continuous_focus_runnable in
        // autoFocusCompleted()
      }
      // it's only worth doing autofocus when autofocus has an effect (i.e., auto or macro mode)
      // but also for continuous focus mode, triggering an autofocus is still important to fire
      // flash when touching the screen
      if (camera_controller.supportsAutoFocus()) {
        if (MyDebug.LOG) Log.d(TAG, "try to start autofocus");
        if (!using_android_l) {
          set_flash_value_after_autofocus = "";
          String old_flash_value = camera_controller.getFlashValue();
          // getFlashValue() may return "" if flash not supported!
          if (startup
              && old_flash_value.length() > 0
              && !old_flash_value.equals("flash_off")
              && !old_flash_value.equals("flash_torch")) {
            set_flash_value_after_autofocus = old_flash_value;
            camera_controller.setFlashValue("flash_off");
          }
          if (MyDebug.LOG)
            Log.d(
                TAG, "set_flash_value_after_autofocus is now: " + set_flash_value_after_autofocus);
        }
        CameraController.AutoFocusCallback autoFocusCallback =
            new CameraController.AutoFocusCallback() {
              @Override
              public void onAutoFocus(boolean success) {
                if (MyDebug.LOG) Log.d(TAG, "autofocus complete: " + success);
                autoFocusCompleted(manual, success, false);
              }
            };

        this.focus_success = FOCUS_WAITING;
        if (MyDebug.LOG) Log.d(TAG, "set focus_success to " + focus_success);
        this.focus_complete_time = -1;
        this.successfully_focused = false;
        camera_controller.autoFocus(autoFocusCallback, false);
        count_cameraAutoFocus++;
        this.focus_started_time = System.currentTimeMillis();
        if (MyDebug.LOG) Log.d(TAG, "autofocus started, count now: " + count_cameraAutoFocus);
      } else if (has_focus_area) {
        // do this so we get the focus box, for focus modes that support focus area, but don't
        // support autofocus
        focus_success = FOCUS_SUCCESS;
        focus_complete_time = System.currentTimeMillis();
        // n.b., don't set focus_started_time as that may be used for application to show autofocus
        // animation
      }
    }
  }

  /**
   * If the user touches the screen in continuous focus mode, we switch the camera_controller to
   * autofocus mode. After the autofocus completes, we set a reset_continuous_focus_runnable to
   * switch back to the camera_controller back to continuous focus after a short delay. This
   * function removes any pending reset_continuous_focus_runnable.
   */
  private void removePendingContinuousFocusReset() {
    if (MyDebug.LOG) Log.d(TAG, "removePendingContinuousFocusReset");
    if (reset_continuous_focus_runnable != null) {
      if (MyDebug.LOG) Log.d(TAG, "remove pending reset_continuous_focus_runnable");
      reset_continuous_focus_handler.removeCallbacks(reset_continuous_focus_runnable);
      reset_continuous_focus_runnable = null;
    }
  }

  /**
   * If the user touches the screen in continuous focus mode, we switch the camera_controller to
   * autofocus mode. This function is called to see if we should switch from autofocus mode back to
   * continuous focus mode. If this isn't required, calling this function does nothing.
   */
  private void continuousFocusReset() {
    if (MyDebug.LOG) Log.d(TAG, "switch back to continuous focus after autofocus?");
    if (camera_controller != null && autofocus_in_continuous_mode) {
      autofocus_in_continuous_mode = false;
      // check again
      String current_ui_focus_value = getCurrentFocusValue();
      if (current_ui_focus_value != null
          && !camera_controller.getFocusValue().equals(current_ui_focus_value)
          && camera_controller.getFocusValue().equals("focus_mode_auto")) {
        camera_controller.cancelAutoFocus();
        camera_controller.setFocusValue(current_ui_focus_value);
      } else {
        if (MyDebug.LOG)
          Log.d(
              TAG,
              "no need to switch back to continuous focus after autofocus, mode already changed");
      }
    }
  }

  private void cancelAutoFocus() {
    if (MyDebug.LOG) Log.d(TAG, "cancelAutoFocus");
    if (camera_controller != null) {
      camera_controller.cancelAutoFocus();
      autoFocusCompleted(false, false, true);
    }
  }

  private void ensureFlashCorrect() {
    // ensures flash is in correct mode, in case where we had to turn flash temporarily off for
    // startup autofocus
    if (set_flash_value_after_autofocus.length() > 0 && camera_controller != null) {
      if (MyDebug.LOG) Log.d(TAG, "set flash back to: " + set_flash_value_after_autofocus);
      camera_controller.setFlashValue(set_flash_value_after_autofocus);
      set_flash_value_after_autofocus = "";
    }
  }

  private void autoFocusCompleted(boolean manual, boolean success, boolean cancelled) {
    if (MyDebug.LOG) {
      Log.d(TAG, "autoFocusCompleted");
      Log.d(TAG, "    manual? " + manual);
      Log.d(TAG, "    success? " + success);
      Log.d(TAG, "    cancelled? " + cancelled);
    }
    if (cancelled) {
      focus_success = FOCUS_DONE;
    } else {
      focus_success = success ? FOCUS_SUCCESS : FOCUS_FAILED;
      focus_complete_time = System.currentTimeMillis();
    }
    if (manual && !cancelled && (success || applicationInterface.isTestAlwaysFocus())) {
      successfully_focused = true;
      successfully_focused_time = focus_complete_time;
    }
    if (manual && camera_controller != null && autofocus_in_continuous_mode) {
      String current_ui_focus_value = getCurrentFocusValue();
      if (MyDebug.LOG) Log.d(TAG, "current_ui_focus_value: " + current_ui_focus_value);
      if (current_ui_focus_value != null
          && !camera_controller.getFocusValue().equals(current_ui_focus_value)
          && camera_controller.getFocusValue().equals("focus_mode_auto")) {
        reset_continuous_focus_runnable =
            new Runnable() {
              @Override
              public void run() {
                if (MyDebug.LOG) Log.d(TAG, "reset_continuous_focus_runnable running...");
                reset_continuous_focus_runnable = null;
                continuousFocusReset();
              }
            };
        reset_continuous_focus_handler.postDelayed(reset_continuous_focus_runnable, 3000);
      }
    }
    ensureFlashCorrect();
    if (this.using_face_detection && !cancelled) {
      // On some devices such as mtk6589, face detection does not resume as written in documentation
      // so we have
      // to cancelfocus when focus is finished
      if (camera_controller != null) {
        camera_controller.cancelAutoFocus();
      }
    }
    synchronized (this) {
      if (take_photo_after_autofocus) {
        if (MyDebug.LOG) Log.d(TAG, "take_photo_after_autofocus is set");
        take_photo_after_autofocus = false;
        prepareAutoFocusPhoto();
        takePhotoWhenFocused();
      }
    }
    if (MyDebug.LOG) Log.d(TAG, "autoFocusCompleted exit");
  }

  public void startCameraPreview() {
    long debug_time = 0;
    if (MyDebug.LOG) {
      Log.d(TAG, "startCameraPreview");
      debug_time = System.currentTimeMillis();
    }
    // if( camera != null && !is_taking_photo && !is_preview_started ) {
    if (camera_controller != null && !this.isTakingPhotoOrOnTimer() && !is_preview_started) {
      if (MyDebug.LOG) Log.d(TAG, "starting the camera preview");
      {
        if (MyDebug.LOG) Log.d(TAG, "setRecordingHint: " + is_video);
        camera_controller.setRecordingHint(this.is_video);
      }
      try {
        camera_controller.startPreview();
        count_cameraStartPreview++;
      } catch (CameraControllerException e) {
        if (MyDebug.LOG) Log.d(TAG, "CameraControllerException trying to startPreview");
        e.printStackTrace();
        applicationInterface.onFailedStartPreview();
        return;
      }
      this.is_preview_started = true;
      if (MyDebug.LOG) {
        Log.d(
            TAG,
            "startCameraPreview: time after starting camera preview: "
                + (System.currentTimeMillis() - debug_time));
      }
      if (this.using_face_detection) {
        if (MyDebug.LOG) Log.d(TAG, "start face detection");
        camera_controller.startFaceDetection();
        faces_detected = null;
      }
    }
    this.setPreviewPaused(false);
    this.setupContinuousFocusMove();
    if (MyDebug.LOG) {
      Log.d(
          TAG,
          "startCameraPreview: total time for startCameraPreview: "
              + (System.currentTimeMillis() - debug_time));
    }
  }

  private void setPreviewPaused(boolean paused) {
    if (MyDebug.LOG) Log.d(TAG, "setPreviewPaused: " + paused);
    applicationInterface.hasPausedPreview(paused);
    if (paused) {
      this.phase = PHASE_PREVIEW_PAUSED;
      // shouldn't call applicationInterface.cameraInOperation(true), as should already have done
      // when we started to take a photo (or above when exiting immersive mode)
    } else {
      this.phase = PHASE_NORMAL;
      applicationInterface.cameraInOperation(false);
    }
  }

  public void onAccelerometerSensorChanged(SensorEvent event) {
    /*if( MyDebug.LOG )
    Log.d(TAG, "onAccelerometerSensorChanged: " + event.values[0] + ", " + event.values[1] + ", " + event.values[2]);*/

    this.has_gravity = true;
    for (int i = 0; i < 3; i++) {
      // this.gravity[i] = event.values[i];
      this.gravity[i] = sensor_alpha * this.gravity[i] + (1.0f - sensor_alpha) * event.values[i];
    }
    calculateGeoDirection();

    double x = gravity[0];
    double y = gravity[1];
    double z = gravity[2];
    double mag = Math.sqrt(x * x + y * y + z * z);
    /*if( MyDebug.LOG )
    Log.d(TAG, "xyz: " + x + ", " + y + ", " + z);*/

    this.has_pitch_angle = false;
    if (mag > 1.0e-8) {
      this.has_pitch_angle = true;
      this.pitch_angle = Math.asin(-z / mag) * 180.0 / Math.PI;
      /*if( MyDebug.LOG )
      Log.d(TAG, "pitch: " + pitch_angle);*/

      if (!is_test && Math.abs(pitch_angle) > 70.0) {
        // level angle becomes unstable when device is near vertical
        // note that if is_test, we always set the level angle - since the device typically lies
        // face down when running tests...
        this.has_level_angle = false;
      } else {
        this.has_level_angle = true;
        this.natural_level_angle = Math.atan2(-x, y) * 180.0 / Math.PI;
        if (this.natural_level_angle < -0.0) {
          this.natural_level_angle += 360.0;
        }

        updateLevelAngles();
      }
    } else {
      Log.e(TAG, "accel sensor has zero mag: " + mag);
      this.has_level_angle = false;
    }
  }

  /**
   * This method should be called when the natural level angle, or the calibration angle, has been
   * updated, to update the other level angle variables.
   */
  public void updateLevelAngles() {
    if (has_level_angle) {
      this.level_angle = this.natural_level_angle;
      double calibrated_level_angle = applicationInterface.getCalibratedLevelAngle();
      this.level_angle -= calibrated_level_angle;
      this.orig_level_angle = this.level_angle;
      this.level_angle -= (float) this.current_orientation;
      if (this.level_angle < -180.0) {
        this.level_angle += 360.0;
      } else if (this.level_angle > 180.0) {
        this.level_angle -= 360.0;
      }
      /*if( MyDebug.LOG )
      Log.d(TAG, "level_angle is now: " + level_angle);*/
    }
  }

  public boolean hasLevelAngle() {
    return this.has_level_angle;
  }

  public double getLevelAngleUncalibrated() {
    return this.natural_level_angle - this.current_orientation;
  }

  public double getLevelAngle() {
    return this.level_angle;
  }

  public double getOrigLevelAngle() {
    return this.orig_level_angle;
  }

  public boolean hasPitchAngle() {
    return this.has_pitch_angle;
  }

  public double getPitchAngle() {
    return this.pitch_angle;
  }

  public void onMagneticSensorChanged(SensorEvent event) {
    this.has_geomagnetic = true;
    for (int i = 0; i < 3; i++) {
      // this.geomagnetic[i] = event.values[i];
      this.geomagnetic[i] =
          sensor_alpha * this.geomagnetic[i] + (1.0f - sensor_alpha) * event.values[i];
    }
    calculateGeoDirection();
  }

  private void calculateGeoDirection() {
    if (!this.has_gravity || !this.has_geomagnetic) {
      return;
    }
    if (!SensorManager.getRotationMatrix(
        this.deviceRotation, this.deviceInclination, this.gravity, this.geomagnetic)) {
      return;
    }
    SensorManager.remapCoordinateSystem(
        this.deviceRotation, SensorManager.AXIS_X, SensorManager.AXIS_Z, this.cameraRotation);
    boolean has_old_geo_direction = has_geo_direction;
    this.has_geo_direction = true;
    // SensorManager.getOrientation(cameraRotation, geo_direction);
    SensorManager.getOrientation(cameraRotation, new_geo_direction);
    /*if( MyDebug.LOG ) {
    	Log.d(TAG, "###");
    	Log.d(TAG, "old geo_direction: " + (geo_direction[0]*180/Math.PI) + ", " + (geo_direction[1]*180/Math.PI) + ", " + (geo_direction[2]*180/Math.PI));
    }*/
    for (int i = 0; i < 3; i++) {
      float old_compass = (float) Math.toDegrees(geo_direction[i]);
      float new_compass = (float) Math.toDegrees(new_geo_direction[i]);
      if (has_old_geo_direction) {
        float smoothFactorCompass = 0.1f;
        float smoothThresholdCompass = 10.0f;
        old_compass =
            lowPassFilter(old_compass, new_compass, smoothFactorCompass, smoothThresholdCompass);
      } else {
        old_compass = new_compass;
      }
      geo_direction[i] = (float) Math.toRadians(old_compass);
    }
    /*if( MyDebug.LOG ) {
    	Log.d(TAG, "new_geo_direction: " + (new_geo_direction[0]*180/Math.PI) + ", " + (new_geo_direction[1]*180/Math.PI) + ", " + (new_geo_direction[2]*180/Math.PI));
    	Log.d(TAG, "geo_direction: " + (geo_direction[0]*180/Math.PI) + ", " + (geo_direction[1]*180/Math.PI) + ", " + (geo_direction[2]*180/Math.PI));
    }*/
  }

  /**
   * Low pass filter, for angles.
   *
   * @param old_value Old value in degrees.
   * @param new_value New value in degrees.
   */
  private float lowPassFilter(
      float old_value, float new_value, float smoothFactorCompass, float smoothThresholdCompass) {
    // see
    // http://stackoverflow.com/questions/4699417/android-compass-orientation-on-unreliable-low-pass-filter
    // https://www.built.io/blog/applying-low-pass-filter-to-android-sensor-s-readings
    // http://stackoverflow.com/questions/27846604/how-to-get-smooth-orientation-data-in-android
    float diff = Math.abs(new_value - old_value);
    /*if( MyDebug.LOG )
    Log.d(TAG, "diff: " + diff);*/
    if (diff < 180) {
      if (diff > smoothThresholdCompass) {
        /*if( MyDebug.LOG )
        Log.d(TAG, "jump to new compass");*/
        old_value = new_value;
      } else {
        old_value = old_value + smoothFactorCompass * (new_value - old_value);
      }
    } else {
      if (360.0 - diff > smoothThresholdCompass) {
        /*if( MyDebug.LOG )
        Log.d(TAG, "jump to new compass");*/
        old_value = new_value;
      } else {
        if (old_value > new_value) {
          old_value =
              (old_value + smoothFactorCompass * ((360 + new_value - old_value) % 360) + 360) % 360;
        } else {
          old_value =
              (old_value - smoothFactorCompass * ((360 - new_value + old_value) % 360) + 360) % 360;
        }
      }
    }
    return old_value;
  }

  public boolean hasGeoDirection() {
    return has_geo_direction;
  }

  public double getGeoDirection() {
    return geo_direction[0];
  }

  public boolean supportsFaceDetection() {
    if (MyDebug.LOG) Log.d(TAG, "supportsFaceDetection");
    return supports_face_detection;
  }

  public boolean supportsVideoStabilization() {
    if (MyDebug.LOG) Log.d(TAG, "supportsVideoStabilization");
    return supports_video_stabilization;
  }

  public boolean canDisableShutterSound() {
    if (MyDebug.LOG) Log.d(TAG, "canDisableShutterSound");
    return can_disable_shutter_sound;
  }

  public List<String> getSupportedColorEffects() {
    if (MyDebug.LOG) Log.d(TAG, "getSupportedColorEffects");
    return this.color_effects;
  }

  public List<String> getSupportedSceneModes() {
    if (MyDebug.LOG) Log.d(TAG, "getSupportedSceneModes");
    return this.scene_modes;
  }

  public List<String> getSupportedWhiteBalances() {
    if (MyDebug.LOG) Log.d(TAG, "getSupportedWhiteBalances");
    return this.white_balances;
  }

  public String getISOKey() {
    if (MyDebug.LOG) Log.d(TAG, "getISOKey");
    return camera_controller == null ? "" : camera_controller.getISOKey();
  }

  /**
   * Whether manual white balance temperatures can be specified via setWhiteBalanceTemperature().
   */
  public boolean supportsWhiteBalanceTemperature() {
    if (MyDebug.LOG) Log.d(TAG, "supportsWhiteBalanceTemperature");
    return this.supports_white_balance_temperature;
  }

  /** Minimum allowed white balance temperature. */
  public int getMinimumWhiteBalanceTemperature() {
    if (MyDebug.LOG) Log.d(TAG, "getMinimumWhiteBalanceTemperature");
    return this.min_temperature;
  }

  /** Maximum allowed white balance temperature. */
  public int getMaximumWhiteBalanceTemperature() {
    if (MyDebug.LOG) Log.d(TAG, "getMaximumWhiteBalanceTemperature");
    return this.max_temperature;
  }

  /**
   * Returns whether a range of manual ISO values can be set. If this returns true, use
   * getMinimumISO() and getMaximumISO() to return the valid range of values. If this returns false,
   * getSupportedISOs() to find allowed ISO values.
   */
  public boolean supportsISORange() {
    if (MyDebug.LOG) Log.d(TAG, "supportsISORange");
    return this.supports_iso_range;
  }

  /**
   * If supportsISORange() returns false, use this method to return a list of supported ISO values:
   * - If this is null, then manual ISO isn't supported. - If non-null, this will include "auto" to
   * indicate auto-ISO, and one or more numerical ISO values. If supportsISORange() returns true,
   * then this method should not be used (and it will return null). Instead use getMinimumISO() and
   * getMaximumISO().
   */
  public List<String> getSupportedISOs() {
    if (MyDebug.LOG) Log.d(TAG, "getSupportedISOs");
    return this.isos;
  }

  /** Returns minimum ISO value. Only relevant if supportsISORange() returns true. */
  public int getMinimumISO() {
    if (MyDebug.LOG) Log.d(TAG, "getMinimumISO");
    return this.min_iso;
  }

  /** Returns maximum ISO value. Only relevant if supportsISORange() returns true. */
  public int getMaximumISO() {
    if (MyDebug.LOG) Log.d(TAG, "getMaximumISO");
    return this.max_iso;
  }

  public float getMinimumFocusDistance() {
    return this.minimum_focus_distance;
  }

  public boolean supportsExposureTime() {
    if (MyDebug.LOG) Log.d(TAG, "supportsExposureTime");
    return this.supports_exposure_time;
  }

  public long getMinimumExposureTime() {
    if (MyDebug.LOG) Log.d(TAG, "getMinimumExposureTime");
    return this.min_exposure_time;
  }

  public long getMaximumExposureTime() {
    if (MyDebug.LOG) Log.d(TAG, "getMaximumExposureTime");
    return this.max_exposure_time;
  }

  public boolean supportsExposures() {
    if (MyDebug.LOG) Log.d(TAG, "supportsExposures");
    return this.exposures != null;
  }

  public int getMinimumExposure() {
    if (MyDebug.LOG) Log.d(TAG, "getMinimumExposure");
    return this.min_exposure;
  }

  public int getMaximumExposure() {
    if (MyDebug.LOG) Log.d(TAG, "getMaximumExposure");
    return this.max_exposure;
  }

  public int getCurrentExposure() {
    if (MyDebug.LOG) Log.d(TAG, "getCurrentExposure");
    if (camera_controller == null) {
      if (MyDebug.LOG) Log.d(TAG, "camera not opened!");
      return 0;
    }
    return camera_controller.getExposureCompensation();
  }

  /*List<String> getSupportedExposures() {
  if( MyDebug.LOG )
  	Log.d(TAG, "getSupportedExposures");
    	return this.exposures;
    }*/

  public boolean supportsExpoBracketing() {
    /*if( MyDebug.LOG )
    Log.d(TAG, "supportsExpoBracketing");*/
    return this.supports_expo_bracketing;
  }

  public int maxExpoBracketingNImages() {
    if (MyDebug.LOG) Log.d(TAG, "maxExpoBracketingNImages");
    return this.max_expo_bracketing_n_images;
  }

  public boolean supportsRaw() {
    if (MyDebug.LOG) Log.d(TAG, "supportsRaw");
    return this.supports_raw;
  }

  /** Returns the horizontal angle of view in degrees (when unzoomed). */
  public float getViewAngleX() {
    return this.view_angle_x;
  }

  /** Returns the vertical angle of view in degrees (when unzoomed). */
  public float getViewAngleY() {
    return this.view_angle_y;
  }

  public List<CameraController.Size> getSupportedPreviewSizes() {
    if (MyDebug.LOG) Log.d(TAG, "getSupportedPreviewSizes");
    return this.supported_preview_sizes;
  }

  public CameraController.Size getCurrentPreviewSize() {
    return new CameraController.Size(preview_w, preview_h);
  }

  public List<CameraController.Size> getSupportedPictureSizes() {
    if (MyDebug.LOG) Log.d(TAG, "getSupportedPictureSizes");
    return this.sizes;
  }

  public int getCurrentPictureSizeIndex() {
    if (MyDebug.LOG) Log.d(TAG, "getCurrentPictureSizeIndex");
    return this.current_size_index;
  }

  public CameraController.Size getCurrentPictureSize() {
    if (current_size_index == -1 || sizes == null) return null;
    return sizes.get(current_size_index);
  }

  public List<String> getSupportedFlashValues() {
    return supported_flash_values;
  }

  public List<String> getSupportedFocusValues() {
    return supported_focus_values;
  }

  public int getCameraId() {
    if (camera_controller == null) return 0;
    return camera_controller.getCameraId();
  }

  public String getCameraAPI() {
    if (camera_controller == null) return "None";
    return camera_controller.getAPI();
  }

  public void onResume() {
    if (MyDebug.LOG) Log.d(TAG, "onResume");
    this.app_is_paused = false;
    cameraSurface.onResume();
    if (canvasView != null) canvasView.onResume();
    this.openCamera();
  }

  public void onPause() {
    if (MyDebug.LOG) Log.d(TAG, "onPause");
    this.app_is_paused = true;
    this.closeCamera();
    cameraSurface.onPause();
    if (canvasView != null) canvasView.onPause();
  }

  /*void updateUIPlacement() {
    	// we cache the preference_ui_placement to save having to check it in the draw() method
  SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this.getContext());
  String ui_placement = sharedPreferences.getString(CameraActivity.getUIPlacementPreferenceKey(), "ui_right");
  this.ui_placement_right = ui_placement.equals("ui_right");
    }*/

  public void onSaveInstanceState(Bundle state) {
    if (MyDebug.LOG) Log.d(TAG, "onSaveInstanceState");
  }

  public void showToast(final ToastBoxer clear_toast, final int message_id) {
    showToast(clear_toast, getResources().getString(message_id));
  }

  public void showToast(final ToastBoxer clear_toast, final String message) {
    showToast(clear_toast, message, 32);
  }

  private void showToast(
      final ToastBoxer clear_toast, final String message, final int offset_y_dp) {
    if (!applicationInterface.getShowToastsPref()) {
      return;
    }

    class RotatedTextView extends View {
      private String[] lines;
      private final Paint paint = new Paint();
      private final Rect bounds = new Rect();
      private final Rect sub_bounds = new Rect();
      private final RectF rect = new RectF();

      public RotatedTextView(String text, Context context) {
        super(context);

        this.lines = text.split("\n");
      }

      void setText(String text) {
        this.lines = text.split("\n");
      }

      @Override
      protected void onDraw(Canvas canvas) {
        final float scale = Preview.this.getResources().getDisplayMetrics().density;
        paint.setTextSize(14 * scale + 0.5f); // convert dps to pixels
        paint.setShadowLayer(1, 0, 1, Color.BLACK);
        // paint.getTextBounds(text, 0, text.length(), bounds);
        boolean first_line = true;
        for (String line : lines) {
          paint.getTextBounds(line, 0, line.length(), sub_bounds);
          /*if( MyDebug.LOG ) {
          	Log.d(TAG, "line: " + line + " sub_bounds: " + sub_bounds);
          }*/
          if (first_line) {
            bounds.set(sub_bounds);
            first_line = false;
          } else {
            bounds.top = Math.min(sub_bounds.top, bounds.top);
            bounds.bottom = Math.max(sub_bounds.bottom, bounds.bottom);
            bounds.left = Math.min(sub_bounds.left, bounds.left);
            bounds.right = Math.max(sub_bounds.right, bounds.right);
          }
        }
        // above we've worked out the maximum bounds of each line - this is useful for left/right,
        // but for the top/bottom
        // we would rather use a consistent height no matter what the text is (otherwise we have the
        // problem of varying
        // gap between lines, depending on what the characters are).
        final String reference_text = "Ap";
        paint.getTextBounds(reference_text, 0, reference_text.length(), sub_bounds);
        bounds.top = sub_bounds.top;
        bounds.bottom = sub_bounds.bottom;
        /*if( MyDebug.LOG ) {
        	Log.d(TAG, "bounds: " + bounds);
        }*/
        int height = bounds.bottom - bounds.top; // height of each line
        bounds.bottom += ((lines.length - 1) * height) / 2;
        bounds.top -= ((lines.length - 1) * height) / 2;
        final int padding =
            (int) (14 * scale + 0.5f); // padding for the shaded rectangle; convert dps to pixels
        final int offset_y = (int) (offset_y_dp * scale + 0.5f); // convert dps to pixels
        canvas.save();
        canvas.rotate(ui_rotation, canvas.getWidth() / 2.0f, canvas.getHeight() / 2.0f);

        rect.left = canvas.getWidth() / 2 - bounds.width() / 2 + bounds.left - padding;
        rect.top = canvas.getHeight() / 2 + bounds.top - padding + offset_y;
        rect.right = canvas.getWidth() / 2 - bounds.width() / 2 + bounds.right + padding;
        rect.bottom = canvas.getHeight() / 2 + bounds.bottom + padding + offset_y;

        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.rgb(50, 50, 50));
        // canvas.drawRect(rect, paint);
        final float radius = (24 * scale + 0.5f); // convert dps to pixels
        canvas.drawRoundRect(rect, radius, radius, paint);

        paint.setColor(Color.WHITE);
        int ypos = canvas.getHeight() / 2 + offset_y - ((lines.length - 1) * height) / 2;
        for (String line : lines) {
          canvas.drawText(line, canvas.getWidth() / 2 - bounds.width() / 2, ypos, paint);
          ypos += height;
        }
        canvas.restore();
      }
    }

    if (MyDebug.LOG) Log.d(TAG, "showToast: " + message);
    final Activity activity = (Activity) this.getContext();
    // We get a crash on emulator at least if Toast constructor isn't run on main thread (e.g., the
    // toast for taking a photo when on timer).
    // Also see http://stackoverflow.com/questions/13267239/toast-from-a-non-ui-thread
    activity.runOnUiThread(
        new Runnable() {
          public void run() {
            /*if( clear_toast != null && clear_toast.toast != null )
            	clear_toast.toast.cancel();

            Toast toast = new Toast(activity);
            if( clear_toast != null )
            	clear_toast.toast = toast;*/
            // This method is better, as otherwise a previous toast (with different or no
            // clear_toast) never seems to clear if we repeatedly issue new toasts - this doesn't
            // happen if we reuse existing toasts if possible
            // However should only do this if the previous toast was the most recent toast (to avoid
            // messing up ordering)
            Toast toast;
            if (clear_toast != null
                && clear_toast.toast != null
                && clear_toast.toast == last_toast) {
              if (MyDebug.LOG) Log.d(TAG, "reuse last toast: " + last_toast);
              toast = clear_toast.toast;
              // for performance, important to reuse the same view, instead of creating a new one
              // (otherwise we get jerky preview update e.g. for changing manual focus slider)
              RotatedTextView view = (RotatedTextView) toast.getView();
              view.setText(message);
              view.invalidate(); // make sure the toast is redrawn
              toast.setView(view);
            } else {
              if (clear_toast != null && clear_toast.toast != null) {
                if (MyDebug.LOG) Log.d(TAG, "cancel last toast: " + clear_toast.toast);
                clear_toast.toast.cancel();
              }
              toast = new Toast(activity);
              if (MyDebug.LOG) Log.d(TAG, "created new toast: " + toast);
              if (clear_toast != null) clear_toast.toast = toast;
              View text = new RotatedTextView(message, activity);
              toast.setView(text);
            }
            toast.setDuration(Toast.LENGTH_SHORT);
            toast.show();
            last_toast = toast;
          }
        });
  }

  public void setUIRotation(int ui_rotation) {
    if (MyDebug.LOG) Log.d(TAG, "setUIRotation");
    this.ui_rotation = ui_rotation;
  }

  public int getUIRotation() {
    return this.ui_rotation;
  }

  /** If geotagging is enabled, pass the location info to the camera controller (for photos). */
  private void updateParametersFromLocation() {
    if (MyDebug.LOG) Log.d(TAG, "updateParametersFromLocation");
    if (camera_controller != null) {
      boolean store_location = applicationInterface.getGeotaggingPref();
      if (store_location && applicationInterface.getLocation() != null) {
        Location location = applicationInterface.getLocation();
        if (MyDebug.LOG) {
          Log.d(TAG, "updating parameters from location...");
          Log.d(
              TAG,
              "lat "
                  + location.getLatitude()
                  + " long "
                  + location.getLongitude()
                  + " accuracy "
                  + location.getAccuracy()
                  + " timestamp "
                  + location.getTime());
        }
        camera_controller.setLocationInfo(location);
      } else {
        if (MyDebug.LOG) Log.d(TAG, "removing location data from parameters...");
        camera_controller.removeLocationInfo();
      }
    }
  }

  public boolean isVideo() {
    return is_video;
  }

  public boolean isVideoRecording() {
    return video_recorder != null && video_start_time_set;
  }

  public boolean isVideoRecordingPaused() {
    return isVideoRecording() && video_recorder_is_paused;
  }

  public long getVideoTime() {
    if (this.isVideoRecordingPaused()) {
      return video_accumulated_time;
    }
    long time_now = System.currentTimeMillis();
    return time_now - video_start_time + video_accumulated_time;
  }

  public long getVideoAccumulatedTime() {
    return video_accumulated_time;
  }

  public boolean isTakingPhoto() {
    return this.phase == PHASE_TAKING_PHOTO;
  }

  public boolean usingCamera2API() {
    return this.using_android_l;
  }

  public CameraController getCameraController() {
    return this.camera_controller;
  }

  public CameraControllerManager getCameraControllerManager() {
    return this.camera_controller_manager;
  }

  public boolean supportsFocus() {
    return this.supported_focus_values != null;
  }

  public boolean supportsFlash() {
    return this.supported_flash_values != null;
  }

  public boolean supportsExposureLock() {
    return this.is_exposure_lock_supported;
  }

  public boolean isExposureLocked() {
    return this.is_exposure_locked;
  }

  public boolean supportsZoom() {
    return this.has_zoom;
  }

  public int getMaxZoom() {
    return this.max_zoom_factor;
  }

  public boolean hasFocusArea() {
    return this.has_focus_area;
  }

  public Pair<Integer, Integer> getFocusPos() {
    return new Pair<>(focus_screen_x, focus_screen_y);
  }

  public int getMaxNumFocusAreas() {
    return this.max_num_focus_areas;
  }

  public boolean isTakingPhotoOrOnTimer() {
    // return this.is_taking_photo;
    return this.phase == PHASE_TAKING_PHOTO || this.phase == PHASE_TIMER;
  }

  public boolean isOnTimer() {
    // return this.is_taking_photo_on_timer;
    return this.phase == PHASE_TIMER;
  }

  public long getTimerEndTime() {
    return take_photo_time;
  }

  public boolean isPreviewPaused() {
    return this.phase == PHASE_PREVIEW_PAUSED;
  }

  public boolean isPreviewStarted() {
    return this.is_preview_started;
  }

  public boolean isFocusWaiting() {
    return focus_success == FOCUS_WAITING;
  }

  public boolean isFocusRecentSuccess() {
    return focus_success == FOCUS_SUCCESS;
  }

  public long timeSinceStartedAutoFocus() {
    if (focus_started_time != -1) return System.currentTimeMillis() - focus_started_time;
    return 0;
  }

  public boolean isFocusRecentFailure() {
    return focus_success == FOCUS_FAILED;
  }

  /** Whether we can skip the autofocus before taking a photo. */
  private boolean recentlyFocused() {
    return this.successfully_focused
        && System.currentTimeMillis() < this.successfully_focused_time + 5000;
  }

  public CameraController.Face[] getFacesDetected() {
    // FindBugs warns about returning the array directly, but in fact we need to return direct
    // access rather than copying, so that the on-screen display of faces rectangles updates
    return this.faces_detected;
  }

  /** Returns the current zoom factor of the camera. Always returns 1.0f if zoom isn't supported. */
  public float getZoomRatio() {
    if (zoom_ratios == null) return 1.0f;
    int zoom_factor = camera_controller.getZoom();
    return this.zoom_ratios.get(zoom_factor) / 100.0f;
  }
}
