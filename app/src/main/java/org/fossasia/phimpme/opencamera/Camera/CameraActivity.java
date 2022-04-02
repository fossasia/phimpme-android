package org.fossasia.phimpme.opencamera.Camera;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

import android.Manifest;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.VoiceInteractor;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.ParcelFileDescriptor;
import android.os.StatFs;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.provider.Settings;
import android.renderscript.RenderScript;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.OrientationEventListener;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.google.android.material.snackbar.Snackbar;
import com.mikepenz.iconics.view.IconicsImageView;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.fossasia.phimpme.MyApplication;
import org.fossasia.phimpme.R;
import org.fossasia.phimpme.base.ThemedActivity;
import org.fossasia.phimpme.gallery.activities.LFMainActivity;
import org.fossasia.phimpme.gallery.activities.SingleMediaActivity;
import org.fossasia.phimpme.gallery.util.ThemeHelper;
import org.fossasia.phimpme.opencamera.CameraController.CameraController;
import org.fossasia.phimpme.opencamera.CameraController.CameraControllerManager2;
import org.fossasia.phimpme.opencamera.Preview.Preview;
import org.fossasia.phimpme.opencamera.UI.FolderChooserDialog;
import org.fossasia.phimpme.opencamera.UI.MainUI;
import org.fossasia.phimpme.opencamera.UI.PopupView;
import org.fossasia.phimpme.utilities.ActivitySwitchHelper;
import org.fossasia.phimpme.utilities.BasicCallBack;
import org.fossasia.phimpme.utilities.Constants;
import org.fossasia.phimpme.utilities.SnackBarHandler;

/** The main Activity for Open Camera. */
public class CameraActivity extends ThemedActivity
    implements AudioListener.AudioListenerCallback, ImageButton.OnClickListener {
  private static final String TAG = "CameraActivity";
  private SensorManager mSensorManager;
  private Sensor mSensorAccelerometer;
  private Sensor mSensorMagnetic;
  private MainUI mainUI;
  private TextFormatter textFormatter;
  private MyApplicationInterface applicationInterface;
  private Preview preview;
  private OrientationEventListener orientationEventListener;
  private boolean supports_auto_stabilise;
  private boolean supports_force_video_4k;
  private boolean supports_camera2;
  private SaveLocationHistory save_location_history; // save location for non-SAF
  private SaveLocationHistory
      save_location_history_saf; // save location for SAF (only initialised when SAF is used)
  private boolean
      saf_dialog_from_preferences; // if a SAF dialog is opened, this records whether we opened it
  // from the Preferences
  private boolean
      camera_in_background; // whether the camera is covered by a fragment/dialog (such as settings
  // or folder picker)
  private GestureDetector gestureDetector;
  private boolean
      screen_is_locked; // whether screen is "locked" - this is Open Camera's own lock to guard
  // against accidental presses, not the standard Android lock
  private final Map<Integer, Bitmap> preloaded_bitmap_resources = new Hashtable<>();
  private ValueAnimator gallery_save_anim;

  private SoundPool sound_pool;
  private SparseIntArray sound_ids;

  private TextToSpeech textToSpeech;
  private boolean textToSpeechSuccess;

  private AudioListener audio_listener;
  private int audio_noise_sensitivity = -1;
  private SpeechRecognizer speechRecognizer;
  private boolean speechRecognizerIsStarted;
  public static IconicsImageView toggle;
  // private boolean ui_placement_right = true;

  private final ToastBoxer screen_locked_toast = new ToastBoxer();
  private final ToastBoxer changed_auto_stabilise_toast = new ToastBoxer();
  private final ToastBoxer audio_control_toast = new ToastBoxer();
  private boolean block_startup_toast =
      false; // used when returning from Settings/Popup - if we're displaying a toast anyway, don't
  // want to display the info toast too

  private final int manual_n =
      1000; // the number of values on the seekbar used for manual focus distance, ISO or exposure
  // speed

  // for testing; must be volatile for test project reading the state
  public boolean is_test; // whether called from OpenCamera.test testing
  public volatile boolean test_low_memory;
  public volatile boolean test_have_angle;
  public volatile float test_angle;
  public volatile String test_last_saved_image;

  public ProgressDialog progressDialog;
  public boolean isFromOutside = false;
  private boolean permanentDenyPermission;

  @BindView(R.id.increase_zoom)
  ImageButton increaseZoom;

  @BindView(R.id.decrease_zoom)
  ImageButton decreaseZoom;

  // For Exposure Bracketing modes
  public int clicks_count;
  TinyDB bundle;
  final String REVIEW_ACTION = "com.android.camera.action.REVIEW";

  @Override
  public void onCreate(Bundle savedInstanceState) {
    long debug_time = 0;
    if (MyDebug.LOG) {
      Log.d(TAG, "onCreate");
      debug_time = System.currentTimeMillis();
    }
    super.onCreate(savedInstanceState);
    ButterKnife.bind(this);
    startSlideAnimation(0);
    ButterKnife.bind(this);
    PreferenceManager.setDefaultValues(
        this, R.xml.preferences, false); // initialise any unset preferences to their default values
    if (MyDebug.LOG)
      Log.d(
          TAG,
          "onCreate: time after setting default preference values: "
              + (System.currentTimeMillis() - debug_time));

    if (getIntent() != null && getIntent().getExtras() != null) {
      // whether called from testing
      is_test = getIntent().getExtras().getBoolean("test_project");
      if (MyDebug.LOG) Log.d(TAG, "is_test: " + is_test);
    }
    if (getIntent() != null && getIntent().getExtras() != null) {
      // whether called from Take Photo widget
      isFromOutside = true;
      hideNavigationBar();
      if (MyDebug.LOG)
        Log.d(TAG, "take_photo?: " + getIntent().getExtras().getBoolean(TakePhoto.TAKE_PHOTO));
    }
    SharedPreferences sharedPreferences = getDefaultSharedPreferences(this);
    bundle = new TinyDB(getBaseContext());

    // determine whether we should support "auto stabilise" feature
    // risk of running out of memory on lower end devices, due to manipulation of large bitmaps
    ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
    if (MyDebug.LOG) {
      Log.d(TAG, "standard max memory = " + activityManager.getMemoryClass() + "MB");
      Log.d(TAG, "large max memory = " + activityManager.getLargeMemoryClass() + "MB");
    }
    // if( activityManager.getMemoryClass() >= 128 ) { // test
    if (activityManager.getLargeMemoryClass() >= 128) {
      supports_auto_stabilise = true;
    }
    if (MyDebug.LOG) Log.d(TAG, "supports_auto_stabilise? " + supports_auto_stabilise);

    // hack to rule out phones unlikely to have 4K video, so no point even offering the option!
    // both S5 and Note 3 have 128MB standard and 512MB large heap (tested via Samsung RTL), as does
    // Galaxy K Zoom
    // also added the check for having 128MB standard heap, to support modded LG G2, which has 128MB
    // standard, 256MB large - see https://sourceforge.net/p/opencamera/tickets/9/
    if (activityManager.getMemoryClass() >= 128 || activityManager.getLargeMemoryClass() >= 512) {
      supports_force_video_4k = true;
    }
    if (MyDebug.LOG) Log.d(TAG, "supports_force_video_4k? " + supports_force_video_4k);

    // set up components
    mainUI = new MainUI(this);
    applicationInterface = new MyApplicationInterface(this, savedInstanceState);
    if (MyDebug.LOG)
      Log.d(
          TAG,
          "onCreate: time after creating application interface: "
              + (System.currentTimeMillis() - debug_time));
    textFormatter = new TextFormatter(this);

    // determine whether we support Camera2 API
    initCamera2Support();

    // set up window flags for normal operation
    setWindowFlagsForCamera();
    if (MyDebug.LOG)
      Log.d(
          TAG,
          "onCreate: time after setting window flags: "
              + (System.currentTimeMillis() - debug_time));

    save_location_history =
        new SaveLocationHistory(this, "save_location_history", getStorageUtils().getSaveLocation());
    if (applicationInterface.getStorageUtils().isUsingSAF()) {
      if (MyDebug.LOG) Log.d(TAG, "create new SaveLocationHistory for SAF");
      save_location_history_saf =
          new SaveLocationHistory(
              this, "save_location_history_saf", getStorageUtils().getSaveLocationSAF());
    }
    if (MyDebug.LOG)
      Log.d(
          TAG,
          "onCreate: time after updating folder history: "
              + (System.currentTimeMillis() - debug_time));

    // set up sensors
    mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

    // accelerometer sensor (for device orientation)
    if (mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
      if (MyDebug.LOG) Log.d(TAG, "found accelerometer");
      mSensorAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    } else {
      if (MyDebug.LOG) Log.d(TAG, "no support for accelerometer");
    }
    if (MyDebug.LOG)
      Log.d(
          TAG,
          "onCreate: time after creating accelerometer sensor: "
              + (System.currentTimeMillis() - debug_time));

    // magnetic sensor (for compass direction)
    if (mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD) != null) {
      if (MyDebug.LOG) Log.d(TAG, "found magnetic sensor");
      mSensorMagnetic = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
    } else {
      if (MyDebug.LOG) Log.d(TAG, "no support for magnetic sensor");
    }
    if (MyDebug.LOG)
      Log.d(
          TAG,
          "onCreate: time after creating magnetic sensor: "
              + (System.currentTimeMillis() - debug_time));

    // clear any seek bars (just in case??)
    mainUI.clearSeekBar();

    // set up the camera and its preview
    preview = new Preview(applicationInterface, ((ViewGroup) this.findViewById(R.id.preview)));
    if (MyDebug.LOG)
      Log.d(
          TAG,
          "onCreate: time after creating preview: " + (System.currentTimeMillis() - debug_time));

    // initialise on-screen button visibility
    View switchCameraButton = findViewById(R.id.switch_camera);
    switchCameraButton.setVisibility(
        preview.getCameraControllerManager().getNumberOfCameras() > 1 ? View.VISIBLE : View.GONE);
    View speechRecognizerButton = findViewById(R.id.audio_control);
    speechRecognizerButton.setVisibility(
        View.GONE); // disabled by default, until the speech recognizer is created
    if (MyDebug.LOG)
      Log.d(
          TAG,
          "onCreate: time after setting button visibility: "
              + (System.currentTimeMillis() - debug_time));

    // listen for orientation event change
    orientationEventListener =
        new OrientationEventListener(this) {
          @Override
          public void onOrientationChanged(int orientation) {
            CameraActivity.this.mainUI.onOrientationChanged(orientation);
          }
        };
    if (MyDebug.LOG)
      Log.d(
          TAG,
          "onCreate: time after setting orientation event listener: "
              + (System.currentTimeMillis() - debug_time));

    // listen for gestures
    gestureDetector = new GestureDetector(this, new MyGestureDetector());
    if (MyDebug.LOG)
      Log.d(
          TAG,
          "onCreate: time after creating gesture detector: "
              + (System.currentTimeMillis() - debug_time));

    // set up listener to handle immersive mode options
    View decorView = getWindow().getDecorView();
    decorView.setOnSystemUiVisibilityChangeListener(
        new View.OnSystemUiVisibilityChangeListener() {
          @Override
          public void onSystemUiVisibilityChange(int visibility) {
            // Note that system bars will only be "visible" if none of the
            // LOW_PROFILE, HIDE_NAVIGATION, or FULLSCREEN flags are set.
            if (!usingKitKatImmersiveMode()) return;
            if (MyDebug.LOG) Log.d(TAG, "onSystemUiVisibilityChange: " + visibility);
            if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
              if (MyDebug.LOG) Log.d(TAG, "system bars now visible");
              // The system bars are visible. Make any desired
              // adjustments to your UI, such as showing the action bar or
              // other navigational controls.
              mainUI.setImmersiveMode(false);
              setImmersiveTimer();
            } else {
              if (MyDebug.LOG) Log.d(TAG, "system bars now NOT visible");
              // The system bars are NOT visible. Make any desired
              // adjustments to your UI, such as hiding the action bar or
              // other navigational controls.
              mainUI.setImmersiveMode(true);
            }
          }
        });
    if (MyDebug.LOG)
      Log.d(
          TAG,
          "onCreate: time after setting immersive mode listener: "
              + (System.currentTimeMillis() - debug_time));

    // show "about" dialog for first time use; also set some per-device defaults
    boolean has_done_first_time =
        sharedPreferences.contains(PreferenceKeys.getFirstTimePreferenceKey());
    if (MyDebug.LOG) Log.d(TAG, "has_done_first_time: " + has_done_first_time);
    if (!has_done_first_time) {
      setDeviceDefaults(this);
    }
    if (!has_done_first_time) {
      setFirstTimeFlag();
    }

    setModeFromIntents(savedInstanceState);

    // load icon+s
    preloadIcons(R.array.flash_icons);
    preloadIcons(R.array.focus_mode_icons);
    if (MyDebug.LOG)
      Log.d(
          TAG,
          "onCreate: time after preloading icons: " + (System.currentTimeMillis() - debug_time));

    // initialise text to speech engine
    textToSpeechSuccess = false;
    // run in separate thread so as to not delay startup time
    new Thread(
            new Runnable() {
              public void run() {
                textToSpeech =
                    new TextToSpeech(
                        CameraActivity.this,
                        new TextToSpeech.OnInitListener() {
                          @Override
                          public void onInit(int status) {
                            if (MyDebug.LOG) Log.d(TAG, "TextToSpeech initialised");
                            if (status == TextToSpeech.SUCCESS) {
                              textToSpeechSuccess = true;
                              if (MyDebug.LOG) Log.d(TAG, "TextToSpeech succeeded");
                            } else {
                              if (MyDebug.LOG) Log.d(TAG, "TextToSpeech failed");
                            }
                          }
                        });
              }
            })
        .start();

    if (MyDebug.LOG)
      Log.d(
          TAG,
          "onCreate: total time for Activity startup: "
              + (System.currentTimeMillis() - debug_time));

    if (!isFromOutside) {
      BasicCallBack basicCallBack =
          new BasicCallBack() {
            @Override
            public void callBack(int status, Object path) {
              if (status == Constants.SUCCESS) {
                final String filepath = path.toString();
                final SharedPreferences sharedPreferences =
                    getDefaultSharedPreferences(CameraActivity.this);
                String mode =
                    sharedPreferences.getString(PreferenceKeys.getPhotoModePreferenceKey(), "");
                final String burst_mode =
                    sharedPreferences.getString(PreferenceKeys.getBurstModePreferenceKey(), "");

                if (clicks_count == 0) { // To start progress dialog once
                  Handler h = new Handler(Looper.getMainLooper());
                  h.post(
                      new Runnable() {
                        public void run() {
                          progressDialog = new ProgressDialog(CameraActivity.this);
                          progressDialog.setMessage("Generating image. Please wait...");
                          progressDialog.show();
                        }
                      });
                }

                clicks_count++; // Count till max defined image is captured and saved
                if (!("preference_photo_mode_expo_bracketing").equals(mode)
                    && Integer.parseInt(burst_mode) == 1) {
                  Boolean preference_pause_preview =
                      sharedPreferences.getBoolean("preference_pause_preview", true);
                  if (!preference_pause_preview) {
                    CameraActivity.this.runOnUiThread(
                        new Runnable() {
                          @Override
                          public void run() {
                            progressDialog.hide();
                          }
                        });
                    Thread t =
                        new Thread(
                            new Runnable() {
                              @Override
                              public void run() {
                                File f = new File(filepath);
                                FileOutputStream fileOutputStream;
                                try {
                                  fileOutputStream = openFileOutput(f.getName(), MODE_PRIVATE);
                                  fileOutputStream.write(f.getName().getBytes());
                                  fileOutputStream.close();
                                } catch (FileNotFoundException e) {
                                  e.printStackTrace();
                                } catch (IOException e) {
                                  e.printStackTrace();
                                }
                              }
                            });
                    t.start();
                  } else {
                    clicks_count = 0;
                    Intent intent = new Intent(CameraActivity.this, PhotoActivity.class);
                    intent.putExtra("filepath", filepath);
                    startActivity(intent);
                  }
                } else if (("preference_photo_mode_expo_bracketing").equals(mode)
                    && clicks_count
                        >= bundle.getInt(
                            "max_expo_bracketing_n_images")) { // Start Activity once when the third
                  // image is saved
                  clicks_count =
                      0; // Turn image count to zero in case user wants to click another set of
                  // photos.
                  Intent intent = new Intent(REVIEW_ACTION, Uri.fromFile(new File(filepath)));
                  intent.setClass(getApplicationContext(), SingleMediaActivity.class);
                  startActivity(intent);
                } else if (Integer.parseInt(burst_mode) > 1) {
                  clicks_count =
                      0; // Turn image count to zero in case user wants to click another set of
                  // photos.
                  Intent intent = new Intent(REVIEW_ACTION, Uri.fromFile(new File(filepath)));
                  intent.setClass(getApplicationContext(), SingleMediaActivity.class);
                  startActivity(intent);
                }
              }
            }
          };
      ImageSaver.setBasicCallBack(basicCallBack);
    }
    toggle = findViewById(R.id.toggle_button);
    increaseZoom.setOnClickListener(this);
    decreaseZoom.setOnClickListener(this);
  }

  /* This method sets the preference defaults which are set specific for a particular device.
   * This method should be called when Open Camera is run for the very first time after installation,
   * or when the user has requested to "Reset settings".
   */
  void setDeviceDefaults(Activity activity) {
    if (MyDebug.LOG) Log.d(TAG, "setDeviceDefaults");
    SharedPreferences sharedPreferences = getDefaultSharedPreferences(activity);
    boolean is_samsung = Build.MANUFACTURER.toLowerCase(Locale.US).contains("samsung");
    boolean is_oneplus = Build.MANUFACTURER.toLowerCase(Locale.US).contains("oneplus");
    // boolean is_nexus = Build.MODEL.toLowerCase(Locale.US).contains("nexus");
    // boolean is_nexus6 = Build.MODEL.toLowerCase(Locale.US).contains("nexus 6");
    // boolean is_pixel_phone = Build.DEVICE != null && Build.DEVICE.equals("sailfish");
    // boolean is_pixel_xl_phone = Build.DEVICE != null && Build.DEVICE.equals("marlin");
    if (MyDebug.LOG) {
      Log.d(TAG, "is_samsung? " + is_samsung);
      Log.d(TAG, "is_oneplus? " + is_oneplus);
      // Log.d(TAG, "is_nexus? " + is_nexus);
      // Log.d(TAG, "is_nexus6? " + is_nexus6);
      // Log.d(TAG, "is_pixel_phone? " + is_pixel_phone);
      // Log.d(TAG, "is_pixel_xl_phone? " + is_pixel_xl_phone);
    }
    if (is_samsung || is_oneplus) {
      // workaround needed for Samsung S7 at least (tested on Samsung RTL)
      // workaround needed for OnePlus 3 at least (see
      // http://forum.xda-developers.com/oneplus-3/help/camera2-support-t3453103 )
      // update for v1.37: significant improvements have been made for standard flash and Camera2
      // API. But OnePlus 3T still has problem
      // that photos come out with a blue tinge if flash is on, and the scene is bright enough not
      // to need it; Samsung devices also seem
      // to work okay, testing on S7 on RTL, but still keeping the fake flash mode in place for
      // these devices, until we're sure of good
      // behaviour
      if (MyDebug.LOG) Log.d(TAG, "set fake flash for camera2");
      SharedPreferences.Editor editor = sharedPreferences.edit();
      editor.putBoolean(PreferenceKeys.getCamera2FakeFlashPreferenceKey(), true);
      editor.apply();
    }
    /*if( is_nexus6 ) {
    	// Nexus 6 captureBurst() started having problems with Android 7 upgrade - images appeared in wrong order (and with wrong order of shutter speeds in exif info), as well as problems with the camera failing with serious errors
    	// we set this even for Nexus 6 devices not on Android 7, as at some point they'll likely be upgraded to Android 7
    	// Update: now fixed in v1.37, this was due to bug where we set RequestTag.CAPTURE for all captures in takePictureBurstExpoBracketing(), rather than just the last!
    	if( MyDebug.LOG )
    		Log.d(TAG, "disable fast burst for camera2");
    	SharedPreferences.Editor editor = sharedPreferences.edit();
    	editor.putBoolean(PreferenceKeys.getCamera2FastBurstPreferenceKey(), false);
    	editor.apply();
    }*/
  }

  /** Switches modes if required, if called from a relevant intent/tile. */
  private void setModeFromIntents(Bundle savedInstanceState) {
    if (MyDebug.LOG) Log.d(TAG, "setModeFromIntents");
    if (savedInstanceState != null) {
      // If we're restoring from a saved state, we shouldn't be resetting any modes
      if (MyDebug.LOG) Log.d(TAG, "restoring from saved state");
      return;
    }
    String action = this.getIntent().getAction();
    if (MyTileServiceFrontCamera.TILE_ID.equals(action)) {
      if (MyDebug.LOG)
        Log.d(TAG, "launching from quick settings tile for Open Camera: selfie mode");
      for (int i = 0; i < preview.getCameraControllerManager().getNumberOfCameras(); i++) {
        if (preview.getCameraControllerManager().isFrontFacing(i)) {
          if (MyDebug.LOG) Log.d(TAG, "found front camera: " + i);
          applicationInterface.setCameraIdPref(i);
          break;
        }
      }
    }
  }

  /** Determine whether we support Camera2 API. */
  private void initCamera2Support() {
    if (MyDebug.LOG) Log.d(TAG, "initCamera2Support");
    supports_camera2 = false;
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      CameraControllerManager2 manager2 = new CameraControllerManager2(this);
      supports_camera2 = true;
      if (manager2.getNumberOfCameras() == 0) {
        if (MyDebug.LOG) Log.d(TAG, "Camera2 reports 0 cameras");
        supports_camera2 = false;
      }
      for (int i = 0; i < manager2.getNumberOfCameras() && supports_camera2; i++) {
        if (!manager2.allowCamera2Support(i)) {
          if (MyDebug.LOG)
            Log.d(TAG, "camera " + i + " doesn't have limited or full support for Camera2 API");
          supports_camera2 = false;
        }
      }
    }
    if (MyDebug.LOG) Log.d(TAG, "supports_camera2? " + supports_camera2);
  }

  private void preloadIcons(int icons_id) {
    long debug_time = 0;
    if (MyDebug.LOG) {
      Log.d(TAG, "preloadIcons: " + icons_id);
      debug_time = System.currentTimeMillis();
    }
    String[] icons = getResources().getStringArray(icons_id);
    for (String icon : icons) {
      int resource =
          getResources().getIdentifier(icon, null, this.getApplicationContext().getPackageName());
      if (MyDebug.LOG) Log.d(TAG, "load resource: " + resource);
      Bitmap bm = BitmapFactory.decodeResource(getResources(), resource);
      this.preloaded_bitmap_resources.put(resource, bm);
    }
    if (MyDebug.LOG) {
      Log.d(
          TAG,
          "preloadIcons: total time for preloadIcons: "
              + (System.currentTimeMillis() - debug_time));
      Log.d(TAG, "size of preloaded_bitmap_resources: " + preloaded_bitmap_resources.size());
    }
  }

  @Override
  protected void onDestroy() {
    if (MyDebug.LOG) {
      Log.d(TAG, "onDestroy");
      Log.d(TAG, "size of preloaded_bitmap_resources: " + preloaded_bitmap_resources.size());
    }
    if (applicationInterface != null) {
      applicationInterface.onDestroy();
    }
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      // see note in HDRProcessor.onDestroy() - but from Android M, renderscript contexts are
      // released with releaseAllContexts()
      // doc for releaseAllContexts() says "If no contexts have been created this function does
      // nothing"
      RenderScript.releaseAllContexts();
    }
    // Need to recycle to avoid out of memory when running tests - probably good practice to do
    // anyway
    for (Map.Entry<Integer, Bitmap> entry : preloaded_bitmap_resources.entrySet()) {
      if (MyDebug.LOG) Log.d(TAG, "recycle: " + entry.getKey());
      entry.getValue().recycle();
    }
    preloaded_bitmap_resources.clear();
    if (textToSpeech != null) {
      // http://stackoverflow.com/questions/4242401/tts-error-leaked-serviceconnection-android-speech-tts-texttospeech-solved
      Log.d(TAG, "free textToSpeech");
      textToSpeech.stop();
      textToSpeech.shutdown();
      textToSpeech = null;
    }
    super.onDestroy();
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.main, menu);
    return true;
  }

  private void setFirstTimeFlag() {
    if (MyDebug.LOG) Log.d(TAG, "setFirstTimeFlag");
    SharedPreferences sharedPreferences = getDefaultSharedPreferences(this);
    SharedPreferences.Editor editor = sharedPreferences.edit();
    editor.putBoolean(PreferenceKeys.getFirstTimePreferenceKey(), true);
    editor.apply();
  }

  // for audio "noise" trigger option
  private int last_level = -1;
  private long time_quiet_loud = -1;
  private long time_last_audio_trigger_photo = -1;

  /**
   * Listens to audio noise and decides when there's been a "loud" noise to trigger taking a photo.
   */
  public void onAudio(int level) {
    boolean audio_trigger = false;
    /*if( level > 150 ) {
    	if( MyDebug.LOG )
    		Log.d(TAG, "loud noise!: " + level);
    	audio_trigger = true;
    }*/

    if (last_level == -1) {
      last_level = level;
      return;
    }
    int diff = level - last_level;

    if (MyDebug.LOG) Log.d(TAG, "noise_sensitivity: " + audio_noise_sensitivity);

    if (diff > audio_noise_sensitivity) {
      if (MyDebug.LOG)
        Log.d(TAG, "got louder!: " + last_level + " to " + level + " , diff: " + diff);
      time_quiet_loud = System.currentTimeMillis();
      if (MyDebug.LOG) Log.d(TAG, "    time: " + time_quiet_loud);
    } else if (diff < -audio_noise_sensitivity && time_quiet_loud != -1) {
      if (MyDebug.LOG)
        Log.d(TAG, "got quieter!: " + last_level + " to " + level + " , diff: " + diff);
      long time_now = System.currentTimeMillis();
      long duration = time_now - time_quiet_loud;
      if (MyDebug.LOG) {
        Log.d(TAG, "stopped being loud - was loud since :" + time_quiet_loud);
        Log.d(TAG, "    time_now: " + time_now);
        Log.d(TAG, "    duration: " + duration);
      }
      if (duration < 1500) {
        if (MyDebug.LOG) Log.d(TAG, "audio_trigger set");
        audio_trigger = true;
      }
      time_quiet_loud = -1;
    } else {
      if (MyDebug.LOG)
        Log.d(TAG, "audio level: " + last_level + " to " + level + " , diff: " + diff);
    }

    last_level = level;

    if (audio_trigger) {
      if (MyDebug.LOG) Log.d(TAG, "audio trigger");
      // need to run on UI thread so that this function returns quickly (otherwise we'll have lag in
      // processing the audio)
      // but also need to check we're not currently taking a photo or on timer, so we don't
      // repeatedly queue up takePicture() calls, or cancel a timer
      long time_now = System.currentTimeMillis();
      SharedPreferences sharedPreferences = getDefaultSharedPreferences(this);
      boolean want_audio_listener =
          sharedPreferences
              .getString(PreferenceKeys.getAudioControlPreferenceKey(), "none")
              .equals("noise");
      if (time_last_audio_trigger_photo != -1 && time_now - time_last_audio_trigger_photo < 5000) {
        // avoid risk of repeatedly being triggered - as well as problem of being triggered again by
        // the camera's own "beep"!
        if (MyDebug.LOG)
          Log.d(
              TAG,
              "ignore loud noise due to too soon since last audio triggerred photo:"
                  + (time_now - time_last_audio_trigger_photo));
      } else if (!want_audio_listener) {
        // just in case this is a callback from an AudioListener before it's been freed (e.g., if
        // there's a loud noise when exiting settings after turning the option off
        if (MyDebug.LOG) Log.d(TAG, "ignore loud noise due to audio listener option turned off");
      } else {
        if (MyDebug.LOG) Log.d(TAG, "audio trigger from loud noise");
        time_last_audio_trigger_photo = time_now;
        audioTrigger();
      }
    }
  }

  /* Audio trigger - either loud sound, or speech recognition.
   * This performs some additional checks before taking a photo.
   */
  private void audioTrigger() {
    if (MyDebug.LOG) Log.d(TAG, "ignore audio trigger due to popup open");
    if (popupIsOpen()) {
      if (MyDebug.LOG) Log.d(TAG, "ignore audio trigger due to popup open");
    } else if (camera_in_background) {
      if (MyDebug.LOG) Log.d(TAG, "ignore audio trigger due to camera in background");
    } else if (preview.isTakingPhotoOrOnTimer()) {
      if (MyDebug.LOG) Log.d(TAG, "ignore audio trigger due to already taking photo or on timer");
    } else {
      if (MyDebug.LOG) Log.d(TAG, "schedule take picture due to loud noise");
      // takePicture();
      this.runOnUiThread(
          new Runnable() {
            public void run() {
              if (MyDebug.LOG) Log.d(TAG, "taking picture due to audio trigger");
              takePicture();
            }
          });
    }
  }

  public boolean onKeyDown(int keyCode, KeyEvent event) {
    if (MyDebug.LOG) Log.d(TAG, "onKeyDown: " + keyCode);
    boolean handled = mainUI.onKeyDown(keyCode, event);
    if (handled) return true;
    return super.onKeyDown(keyCode, event);
  }

  public boolean onKeyUp(int keyCode, KeyEvent event) {
    if (MyDebug.LOG) Log.d(TAG, "onKeyUp: " + keyCode);
    mainUI.onKeyUp(keyCode, event);
    return super.onKeyUp(keyCode, event);
  }

  public void changeExposure(int change) {
    mainUI.changeSeekbar(R.id.exposure_seekbar, change);
  }

  public void changeISO(int change) {
    mainUI.changeSeekbar(R.id.iso_seekbar, change);
  }

  public void changeFocusDistance(int change) {
    mainUI.changeSeekbar(R.id.focus_seekbar, change);
  }

  private final SensorEventListener accelerometerListener =
      new SensorEventListener() {
        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {}

        @Override
        public void onSensorChanged(SensorEvent event) {
          preview.onAccelerometerSensorChanged(event);
        }
      };

  private final SensorEventListener magneticListener =
      new SensorEventListener() {
        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {}

        @Override
        public void onSensorChanged(SensorEvent event) {
          preview.onMagneticSensorChanged(event);
        }
      };

  @Override
  public void onResume() {
    if ("flash_frontscreen_on".equals(getPreview().getCurrentFlashValue())) {
      defaultBrightness();
    }
    ActivitySwitchHelper.setContext(this);
    if (progressDialog != null) {
      progressDialog.dismiss();
    }
    setNavigationBarColor(ThemeHelper.getPrimaryColor(this));
    long debug_time = 0;
    if (MyDebug.LOG) {
      Log.d(TAG, "onResume");
      debug_time = System.currentTimeMillis();
    }

    // hiding status bar
    getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

    super.onResume();

    // Set black window background; also needed if we hide the virtual buttons in immersive mode
    // Note that we do it here rather than customising the theme's android:windowBackground, so this
    // doesn't affect other views - in particular, the MyPreferenceFragment settings
    getWindow().getDecorView().getRootView().setBackgroundColor(Color.BLACK);

    mSensorManager.registerListener(
        accelerometerListener, mSensorAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    mSensorManager.registerListener(
        magneticListener, mSensorMagnetic, SensorManager.SENSOR_DELAY_NORMAL);
    orientationEventListener.enable();

    initSpeechRecognizer();
    initLocation();
    initSound();
    loadSound(R.raw.beep);
    loadSound(R.raw.beep_hi);

    mainUI.layoutUI();

    preview.onResume();

    if (MyDebug.LOG) {
      Log.d(TAG, "onResume: total time to resume: " + (System.currentTimeMillis() - debug_time));
    }
    try {
      if (CameraActivity.this.isVoiceInteraction()) {
        startVoiceTrigger();
      }
    } catch (NoSuchMethodError e) {
      e.printStackTrace();
    }
  }

  private void startSlideAnimation(int currentMenuItem) {

    if (((MyApplication) this.getApplication()).NavItem > currentMenuItem)
      overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    else if (((MyApplication) this.getApplication()).NavItem < currentMenuItem)
      overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    // Update the Global NavItem
    ((MyApplication) this.getApplication()).NavItem = currentMenuItem;
  }

  private void defaultBrightness() {
    WindowManager.LayoutParams layout = getWindow().getAttributes();
    layout.screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE;
    getWindow().setAttributes(layout);
  }

  private void startVoiceTrigger() {
    Log.d(TAG, "startVoiceTrigger: ");

    VoiceInteractor.PickOptionRequest.Option rear =
        new VoiceInteractor.PickOptionRequest.Option(
            getResources().getString(R.string.camera_rear), 0);
    rear.addSynonym(getResources().getString(R.string.rear));
    rear.addSynonym(getResources().getString(R.string.back));
    rear.addSynonym(getResources().getString(R.string.normal));

    VoiceInteractor.PickOptionRequest.Option front =
        new VoiceInteractor.PickOptionRequest.Option(
            getResources().getString(R.string.camera_front), 1);
    front.addSynonym(getResources().getString(R.string.front));
    front.addSynonym(getResources().getString(R.string.selfie_camera));
    front.addSynonym(getResources().getString(R.string.forward));

    CameraActivity.this
        .getVoiceInteractor()
        .submitRequest(
            new VoiceInteractor.PickOptionRequest(
                new VoiceInteractor.Prompt(getResources().getString(R.string.ask_question)),
                new VoiceInteractor.PickOptionRequest.Option[] {front, rear},
                null) {

              @Override
              public void onPickOptionResult(boolean finished, Option[] selections, Bundle result) {
                if (finished && selections.length == 1) {
                  Message message = Message.obtain();
                  message.obj = result;
                  if (selections[0].getIndex() == 0) {
                    rearCamera();
                    asktakePicture();
                  }
                  if (selections[0].getIndex() == 1) {
                    asktakePicture();
                  }
                } else {
                  getActivity().finish();
                }
              }

              @Override
              public void onCancel() {
                getActivity().finish();
              }
            });
  }

  private void rearCamera() {
    int cameraId = getNextCameraId();
    this.preview.setCamera(cameraId);
  }

  private void asktakePicture() {
    VoiceInteractor.PickOptionRequest.Option option =
        new VoiceInteractor.PickOptionRequest.Option(getResources().getString(R.string.cheese), 2);
    option.addSynonym(getResources().getString(R.string.ready));
    option.addSynonym(getResources().getString(R.string.go));
    option.addSynonym(getResources().getString(R.string.take));
    option.addSynonym(getResources().getString(R.string.ok));

    getVoiceInteractor()
        .submitRequest(
            new VoiceInteractor.PickOptionRequest(
                new VoiceInteractor.Prompt(getResources().getString(R.string.say_cheese)),
                new VoiceInteractor.PickOptionRequest.Option[] {option},
                null) {

              @Override
              public void onPickOptionResult(boolean finished, Option[] selections, Bundle result) {
                if (finished && selections.length == 1) {
                  Message message = Message.obtain();
                  message.obj = result;
                  takePicture();
                } else {
                  getActivity().finish();
                }
              }

              @Override
              public void onCancel() {
                getActivity().finish();
              }
            });
  }

  @Override
  public void onWindowFocusChanged(boolean hasFocus) {
    updateSaveFolder(MyPreferenceFragment.new_save_location);
    if (MyDebug.LOG) Log.d(TAG, "onWindowFocusChanged: " + hasFocus);
    super.onWindowFocusChanged(hasFocus);
    if (!this.camera_in_background && hasFocus) {
      // low profile mode is cleared when app goes into background
      // and for Kit Kat immersive mode, we want to set up the timer
      // we do in onWindowFocusChanged rather than onResume(), to also catch when window lost focus
      // due to notification bar being dragged down (which prevents resetting of immersive mode)
      initImmersiveMode();
    }
  }

  @Override
  public void onPause() {
    long debug_time = 0;
    if (MyDebug.LOG) {
      Log.d(TAG, "onPause");
      debug_time = System.currentTimeMillis();
    }
    waitUntilImageQueueEmpty(); // so we don't risk losing any images
    super.onPause(); // docs say to call this before freeing other things
    mainUI.destroyPopup();
    mSensorManager.unregisterListener(accelerometerListener);
    mSensorManager.unregisterListener(magneticListener);
    orientationEventListener.disable();
    freeAudioListener(false);
    freeSpeechRecognizer();
    applicationInterface.getLocationSupplier().freeLocationListeners();
    applicationInterface.getGyroSensor().stopRecording();
    releaseSound();
    applicationInterface
        .clearLastImages(); // this should happen when pausing the preview, but call explicitly just
    // to be safe
    preview.onPause();
    if (MyDebug.LOG) {
      Log.d(TAG, "onPause: total time to pause: " + (System.currentTimeMillis() - debug_time));
    }
  }

  @Override
  public int getContentViewId() {
    return R.layout.activity_camera;
  }

  @Override
  public int getNavigationMenuItemId() {
    return R.id.navigation_camera;
  }

  @Override
  public void onConfigurationChanged(Configuration newConfig) {
    if (MyDebug.LOG) Log.d(TAG, "onConfigurationChanged()");
    // configuration change can include screen orientation (landscape/portrait) when not locked
    // (when settings is open)
    // needed if app is paused/resumed when settings is open and device is in portrait mode
    preview.setCameraDisplayOrientation();
    super.onConfigurationChanged(newConfig);
  }

  public void waitUntilImageQueueEmpty() {
    if (MyDebug.LOG) Log.d(TAG, "waitUntilImageQueueEmpty");
    applicationInterface.getImageSaver().waitUntilDone();
  }

  public void clickedTakePhoto(View view) {
    SharedPreferences sharedPreferences = getDefaultSharedPreferences(this);
    PopupView.sound_index =
        sharedPreferences.getInt(PreferenceKeys.getSoundModePreferenceKey(), PopupView.sound_index);
    if (MyDebug.LOG) Log.d(TAG, "clickedTakePhoto");
    this.takePicture();
  }

  public void clickedAudioControl(View view) {
    if (MyDebug.LOG) Log.d(TAG, "clickedAudioControl");
    // check hasAudioControl just in case!
    if (!hasAudioControl()) {
      if (MyDebug.LOG) Log.e(TAG, "clickedAudioControl, but hasAudioControl returns false!");
      return;
    }
    this.closePopup();
    SharedPreferences sharedPreferences = getDefaultSharedPreferences(this);
    String audio_control =
        sharedPreferences.getString(PreferenceKeys.getAudioControlPreferenceKey(), "none");
    if (audio_control.equals("voice") && speechRecognizer != null) {
      if (speechRecognizerIsStarted) {
        speechRecognizer.stopListening();
        speechRecognizerStopped();
      } else {
        preview.showToast(audio_control_toast, R.string.speech_recognizer_started);
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE,
            "en_US"); // since we listen for "cheese", ensure this works even for devices with
        // different language settings
        speechRecognizer.startListening(intent);
        speechRecognizerStarted();
      }
    } else if (audio_control.equals("noise")) {
      if (audio_listener != null) {
        freeAudioListener(false);
      } else {
        preview.showToast(audio_control_toast, R.string.audio_listener_started);
        startAudioListener();
      }
    }
  }

  private void speechRecognizerStarted() {
    if (MyDebug.LOG) Log.d(TAG, "speechRecognizerStarted");
    mainUI.audioControlStarted();
    speechRecognizerIsStarted = true;
  }

  private void speechRecognizerStopped() {
    if (MyDebug.LOG) Log.d(TAG, "speechRecognizerStopped");
    mainUI.audioControlStopped();
    speechRecognizerIsStarted = false;
  }

  /* Returns the cameraId that the "Switch camera" button will switch to.
   */
  public int getNextCameraId() {
    if (MyDebug.LOG) Log.d(TAG, "getNextCameraId");
    int cameraId = preview.getCameraId();
    if (MyDebug.LOG) Log.d(TAG, "current cameraId: " + cameraId);
    if (this.preview.canSwitchCamera()) {
      int n_cameras = preview.getCameraControllerManager().getNumberOfCameras();
      cameraId = (cameraId + 1) % n_cameras;
    }
    if (MyDebug.LOG) Log.d(TAG, "next cameraId: " + cameraId);
    return cameraId;
  }

  public void clickedSwitchCamera(View view) {
    if (MyDebug.LOG) Log.d(TAG, "clickedSwitchCamera");
    this.closePopup();
    if (this.preview.canSwitchCamera()) {
      int cameraId = getNextCameraId();
      View switchCameraButton = findViewById(R.id.switch_camera);
      switchCameraButton.setEnabled(false); // prevent slowdown if user repeatedly clicks
      this.preview.setCamera(cameraId);
      switchCameraButton.setEnabled(true);
      mainUI.setSwitchCameraContentDescription();
    }
  }

  public void clickedExposure(View view) {
    if (MyDebug.LOG) Log.d(TAG, "clickedExposure");
    mainUI.toggleExposureUI();
  }

  private static double seekbarScaling(double frac) {
    // For various seekbars, we want to use a non-linear scaling, so user has more control over
    // smaller values
    return (Math.pow(100.0, frac) - 1.0) / 99.0;
  }

  private static double seekbarScalingInverse(double scaling) {
    return Math.log(99.0 * scaling + 1.0) / Math.log(100.0);
  }

  private void setProgressSeekbarScaled(
      SeekBar seekBar, double min_value, double max_value, double value) {
    seekBar.setMax(manual_n);
    double scaling = (value - min_value) / (max_value - min_value);
    double frac = CameraActivity.seekbarScalingInverse(scaling);
    int new_value = (int) (frac * manual_n + 0.5); // add 0.5 for rounding
    if (new_value < 0) new_value = 0;
    else if (new_value > manual_n) new_value = manual_n;
    seekBar.setProgress(new_value);
  }

  private static double exponentialScaling(double frac, double min, double max) {
    /* We use S(frac) = A * e^(s * frac)
     * We want S(0) = min, S(1) = max
     * So A = min
     * and Ae^s = max
     * => s = ln(max/min)
     */
    double s = Math.log(max / min);
    return min * Math.exp(s * frac);
  }

  private static double exponentialScalingInverse(double value, double min, double max) {
    double s = Math.log(max / min);
    return Math.log(value / min) / s;
  }

  public void setProgressSeekbarExponential(
      SeekBar seekBar, double min_value, double max_value, double value) {
    seekBar.setMax(manual_n);
    double frac = exponentialScalingInverse(value, min_value, max_value);
    int new_value = (int) (frac * manual_n + 0.5); // add 0.5 for rounding
    if (new_value < 0) new_value = 0;
    else if (new_value > manual_n) new_value = manual_n;
    seekBar.setProgress(new_value);
  }

  public boolean popupIsOpen() {
    return mainUI.popupIsOpen();
  }

  // for testing
  public View getPopupButton(String key) {
    return mainUI.getPopupButton(key);
  }

  public void closePopup() {
    mainUI.closePopup();
  }

  public Bitmap getPreloadedBitmap(int resource) {
    return this.preloaded_bitmap_resources.get(resource);
  }

  public void clickedPopupSettings(View view) {
    if (MyDebug.LOG) Log.d(TAG, "clickedPopupSettings");
    mainUI.togglePopupSettings();
  }

  public void getSettingDetail() {
    if (preview == null) return;

    if (MyDebug.LOG) Log.d(TAG, "openSettings");
    waitUntilImageQueueEmpty(); // in theory not needed as we could continue running in the
    // background, but best to be safe
    closePopup();
    preview
        .cancelTimer(); // best to cancel any timer, in case we take a photo while settings window
    // is open, or when changing settings
    stopAudioListeners();
    TinyDB bundle = new TinyDB(this);
    //		Bundle bundle = new Bundle();
    bundle.putInt("cameraId", this.preview.getCameraId());
    bundle.putInt("nCameras", preview.getCameraControllerManager().getNumberOfCameras());
    bundle.putString("camera_api", this.preview.getCameraAPI());
    bundle.putBoolean("using_android_l", this.preview.usingCamera2API());
    bundle.putBoolean("supports_auto_stabilise", this.supports_auto_stabilise);
    bundle.putBoolean("supports_force_video_4k", this.supports_force_video_4k);
    bundle.putBoolean("supports_camera2", this.supports_camera2);
    bundle.putBoolean("supports_face_detection", this.preview.supportsFaceDetection());
    bundle.putBoolean("supports_raw", this.preview.supportsRaw());
    bundle.putBoolean("supports_hdr", this.supportsHDR());
    bundle.putBoolean("supports_expo_bracketing", this.supportsExpoBracketing());
    bundle.putInt("max_expo_bracketing_n_images", this.maxExpoBracketingNImages());
    bundle.putBoolean("supports_exposure_compensation", this.preview.supportsExposures());
    bundle.putBoolean("supports_iso_range", this.preview.supportsISORange());
    bundle.putBoolean("supports_exposure_time", this.preview.supportsExposureTime());
    bundle.putBoolean(
        "supports_white_balance_temperature", this.preview.supportsWhiteBalanceTemperature());
    bundle.putBoolean("supports_video_stabilization", this.preview.supportsVideoStabilization());
    bundle.putBoolean("can_disable_shutter_sound", this.preview.canDisableShutterSound());
    /*
    putBundleExtra(bundle, "color_effects", this.preview.getSupportedColorEffects());
    putBundleExtra(bundle, "scene_modes", this.preview.getSupportedSceneModes());
    putBundleExtra(bundle, "white_balances", this.preview.getSupportedWhiteBalances());
    putBundleExtra(bundle, "isos", this.preview.getSupportedISOs());*/
    bundle.putString("iso_key", this.preview.getISOKey());
    if (this.preview.getCameraController() != null) {
      bundle.putString("parameters_string", preview.getCameraController().getParametersString());
    }

    List<CameraController.Size> preview_sizes = this.preview.getSupportedPreviewSizes();
    if (preview_sizes != null) {
      ArrayList<Integer> widths = new ArrayList<>();
      ArrayList<Integer> heights = new ArrayList<>();

      int i = 0;
      for (CameraController.Size size : preview_sizes) {
        widths.add(i, size.width);
        heights.add(i, size.height);
        i++;
      }
      bundle.putListInt("preview_widths", widths);
      bundle.putListInt("preview_heights", heights);
    }
    bundle.putInt("preview_width", preview.getCurrentPreviewSize().width);
    bundle.putInt("preview_height", preview.getCurrentPreviewSize().height);

    List<CameraController.Size> sizes = this.preview.getSupportedPictureSizes();
    if (sizes != null) {
      ArrayList<Integer> widths = new ArrayList<>();
      ArrayList<Integer> heights = new ArrayList<>();
      int i = 0;
      for (CameraController.Size size : sizes) {
        widths.add(i, size.width);
        heights.add(i, size.height);
        i++;
      }
      bundle.putListInt("resolution_widths", widths);
      bundle.putListInt("resolution_heights", heights);
    }
    if (preview.getCurrentPictureSize() != null) {
      bundle.putInt("resolution_width", preview.getCurrentPictureSize().width);
      bundle.putInt("resolution_height", preview.getCurrentPictureSize().height);
    }
  }

  public void openSettings() {
    setWindowFlagsForSettings();
    MyPreferenceFragment fragment = new MyPreferenceFragment();
    getFragmentManager()
        .beginTransaction()
        .add(R.id.prefs_container, fragment, "PREFERENCE_FRAGMENT")
        .addToBackStack(null)
        .commitAllowingStateLoss();
  }

  public void updateForSettings() {
    updateForSettings(null);
  }

  /**
   * Must be called when an settings (as stored in SharedPreferences) are made, so we can update the
   * camera, and make any other necessary changes.
   */
  public void updateForSettings(String toast_message) {
    if (MyDebug.LOG) {
      Log.d(TAG, "updateForSettings()");
      if (toast_message != null) {
        Log.d(TAG, "toast_message: " + toast_message);
      }
    }
    // make sure we're into continuous video mode
    // workaround for bug on Samsung Galaxy S5 with UHD, where if the user switches to another
    // (non-continuous-video) focus mode, then goes to Settings, then returns and records video, the
    // preview freezes and the video is corrupted
    // so to be safe, we always reset to continuous video mode, and then reset it afterwards
    String saved_focus_value =
        preview.updateFocusForVideo(); // n.b., may be null if focus mode not changed
    if (MyDebug.LOG) Log.d(TAG, "saved_focus_value: " + saved_focus_value);

    if (MyDebug.LOG) Log.d(TAG, "update folder history");
    save_location_history.updateFolderHistory(getStorageUtils().getSaveLocation());
    // no need to update save_location_history_saf, as we always do this in onActivityResult()

    // update camera for changes made in prefs - do this without closing and reopening the camera
    // app if possible for speed!
    // but need workaround for Nexus 7 bug, where scene mode doesn't take effect unless the camera
    // is restarted - I can reproduce this with other 3rd party camera apps, so may be a Nexus 7
    // issue...
    boolean need_reopen = false;
    if (preview.getCameraController() != null) {
      SharedPreferences sharedPreferences = getDefaultSharedPreferences(this);
      String scene_mode = preview.getCameraController().getSceneMode();
      if (MyDebug.LOG) Log.d(TAG, "scene mode was: " + scene_mode);
      String key = PreferenceKeys.getSceneModePreferenceKey();
      String value =
          sharedPreferences.getString(key, preview.getCameraController().getDefaultSceneMode());
      if (!value.equals(scene_mode)) {
        if (MyDebug.LOG) Log.d(TAG, "scene mode changed to: " + value);
        need_reopen = true;
      } else {
        if (applicationInterface.useCamera2()) {
          // need to reopen if fake flash mode changed, as it changes the available camera features,
          // and we can only set this after opening the camera
          boolean camera2_fake_flash = preview.getCameraController().getUseCamera2FakeFlash();
          if (MyDebug.LOG) Log.d(TAG, "camera2_fake_flash was: " + camera2_fake_flash);
          if (applicationInterface.useCamera2FakeFlash() != camera2_fake_flash) {
            if (MyDebug.LOG) Log.d(TAG, "camera2_fake_flash changed");
            need_reopen = true;
          }
        }
      }
    }

    mainUI.layoutUI(); // needed in case we've changed left/right handed UI
    SharedPreferences sharedPreferences = getDefaultSharedPreferences(this);
    if (sharedPreferences
        .getString(PreferenceKeys.getAudioControlPreferenceKey(), "none")
        .equals("none")) {
      // ensure icon is invisible if switching from audio control enabled to disabled
      // (if enabling it, we'll make the icon visible later on)
      View speechRecognizerButton = findViewById(R.id.audio_control);
      speechRecognizerButton.setVisibility(View.GONE);
    }
    initSpeechRecognizer(); // in case we've enabled or disabled speech recognizer
    initLocation(); // in case we've enabled or disabled GPS
    if (toast_message != null) block_startup_toast = true;
    if (need_reopen
        || preview.getCameraController()
            == null) { // if camera couldn't be opened before, might as well try again
      preview.onPause();
      preview.onResume();
    } else {
      preview.setCameraDisplayOrientation(); // need to call in case the preview rotation option was
      // changed
      preview.pausePreview();
      preview.setupCamera(false);
    }
    block_startup_toast = false;
    if (toast_message != null && toast_message.length() > 0) preview.showToast(null, toast_message);

    if (saved_focus_value != null) {
      if (MyDebug.LOG) Log.d(TAG, "switch focus back to: " + saved_focus_value);
      preview.updateFocus(saved_focus_value, true, false);
    }
  }

  private MyPreferenceFragment getPreferenceFragment() {
    return (MyPreferenceFragment) getFragmentManager().findFragmentByTag("PREFERENCE_FRAGMENT");
  }

  @Override
  public void onBackPressed() {
    final MyPreferenceFragment fragment = getPreferenceFragment();
    if (screen_is_locked) {
      preview.showToast(screen_locked_toast, R.string.screen_is_locked);
      return;
    }
    if (fragment != null) {
      if (MyDebug.LOG) Log.d(TAG, "close settings");
      setWindowFlagsForCamera();
      updateForSettings();
    } else {
      if (popupIsOpen()) {
        closePopup();
        return;
      }
    }
    Intent intent = new Intent(this, LFMainActivity.class);
    startActivity(intent);
    finish();
  }

  public boolean usingKitKatImmersiveMode() {
    // whether we are using a Kit Kat style immersive mode (either hiding GUI, or everything)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
      SharedPreferences sharedPreferences = getDefaultSharedPreferences(this);
      String immersive_mode =
          sharedPreferences.getString(
              PreferenceKeys.getImmersiveModePreferenceKey(), "immersive_mode_low_profile");
      if (immersive_mode.equals("immersive_mode_gui")
          || immersive_mode.equals("immersive_mode_everything")) return true;
    }
    return false;
  }

  public boolean usingKitKatImmersiveModeEverything() {
    // whether we are using a Kit Kat style immersive mode for everything
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
      SharedPreferences sharedPreferences = getDefaultSharedPreferences(this);
      String immersive_mode =
          sharedPreferences.getString(
              PreferenceKeys.getImmersiveModePreferenceKey(), "immersive_mode_low_profile");
      if (immersive_mode.equals("immersive_mode_everything")) return true;
    }
    return false;
  }

  private Handler immersive_timer_handler = null;
  private Runnable immersive_timer_runnable = null;

  private void setImmersiveTimer() {
    if (immersive_timer_handler != null && immersive_timer_runnable != null) {
      immersive_timer_handler.removeCallbacks(immersive_timer_runnable);
    }
    immersive_timer_handler = new Handler();
    immersive_timer_handler.postDelayed(
        immersive_timer_runnable =
            new Runnable() {
              @Override
              public void run() {
                if (MyDebug.LOG) Log.d(TAG, "setImmersiveTimer: run");
                if (!camera_in_background && !popupIsOpen() && usingKitKatImmersiveMode())
                  setImmersiveMode(true);
              }
            },
        5000);
  }

  public void initImmersiveMode() {
    if (!usingKitKatImmersiveMode()) {
      setImmersiveMode(true);
    } else {
      // don't start in immersive mode, only after a timer
      setImmersiveTimer();
    }
  }

  void setImmersiveMode(boolean on) {
    if (MyDebug.LOG) Log.d(TAG, "setImmersiveMode: " + on);
    // n.b., preview.setImmersiveMode() is called from onSystemUiVisibilityChange()
    if (on) {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && usingKitKatImmersiveMode()) {
        getWindow()
            .getDecorView()
            .setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN);
      } else {
        SharedPreferences sharedPreferences = getDefaultSharedPreferences(this);
        String immersive_mode =
            sharedPreferences.getString(
                PreferenceKeys.getImmersiveModePreferenceKey(), "immersive_mode_low_profile");
        if (immersive_mode.equals("immersive_mode_low_profile"))
          getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
        else getWindow().getDecorView().setSystemUiVisibility(0);
      }
    } else getWindow().getDecorView().setSystemUiVisibility(0);
  }

  /**
   * Sets the brightness level for normal operation (when camera preview is visible). If force_max
   * is true, this always forces maximum brightness; otherwise this depends on user preference.
   */
  /** Sets the window flags for normal operation (when camera preview is visible). */
  public void setWindowFlagsForCamera() {
    if (MyDebug.LOG) Log.d(TAG, "setWindowFlagsForCamera");
    /*{
    	Intent intent = new Intent(this, MyWidgetProvider.class);
    	intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
    	AppWidgetManager widgetManager = AppWidgetManager.getInstance(this);
    	ComponentName widgetComponent = new ComponentName(this, MyWidgetProvider.class);
    	int[] widgetIds = widgetManager.getAppWidgetIds(widgetComponent);
    	intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, widgetIds);
    	sendBroadcast(intent);
    }*/
    SharedPreferences sharedPreferences = getDefaultSharedPreferences(this);

    // force to landscape mode
    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    // setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE); // testing for
    // devices with unusual sensor orientation (e.g., Nexus 5X)
    // keep screen active - see http://stackoverflow.com/questions/2131948/force-screen-on
    if (sharedPreferences.getBoolean(PreferenceKeys.getKeepDisplayOnPreferenceKey(), true)) {
      if (MyDebug.LOG) Log.d(TAG, "do keep screen on");
      getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    } else {
      if (MyDebug.LOG) Log.d(TAG, "don't keep screen on");
      getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }
    if (sharedPreferences.getBoolean(PreferenceKeys.getShowWhenLockedPreferenceKey(), true)) {
      if (MyDebug.LOG) Log.d(TAG, "do show when locked");
      // keep Open Camera on top of screen-lock (will still need to unlock when going to gallery or
      // settings)
      getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
    } else {
      if (MyDebug.LOG) Log.d(TAG, "don't show when locked");
      getWindow().clearFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
    }

    initImmersiveMode();
    camera_in_background = false;
  }

  /** Sets the window flags for when the settings window is open. */
  public void setWindowFlagsForSettings() {
    // allow screen rotation
    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
    // revert to standard screen blank behaviour
    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    // settings should still be protected by screen lock
    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);

    {
      WindowManager.LayoutParams layout = getWindow().getAttributes();
      layout.screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE;
      getWindow().setAttributes(layout);
    }

    setImmersiveMode(false);
    camera_in_background = true;
  }

  public void showPreview(boolean show) {
    if (MyDebug.LOG) Log.d(TAG, "showPreview: " + show);
    final ViewGroup container = findViewById(R.id.hide_container);
    container.setBackgroundColor(Color.BLACK);
    container.setAlpha(show ? 0.0f : 1.0f);
  }

  void savingImage(final boolean started) {
    if (MyDebug.LOG) Log.d(TAG, "savingImage: " + started);

    this.runOnUiThread(
        new Runnable() {
          public void run() {
            if (started) {
              // galleryButton.setColorFilter(0x80ffffff, PorterDuff.Mode.MULTIPLY);
              if (gallery_save_anim == null) {
                gallery_save_anim =
                    ValueAnimator.ofInt(
                        Color.argb(200, 255, 255, 255), Color.argb(63, 255, 255, 255));
                gallery_save_anim.setEvaluator(new ArgbEvaluator());
                gallery_save_anim.setRepeatCount(ValueAnimator.INFINITE);
                gallery_save_anim.setRepeatMode(ValueAnimator.REVERSE);
                gallery_save_anim.setDuration(500);
              }
              gallery_save_anim.start();
            } else if (gallery_save_anim != null) {
              gallery_save_anim.cancel();
            }
          }
        });
  }

  public void clickedGallery(View view) {
    if (MyDebug.LOG) Log.d(TAG, "clickedGallery");
    // Intent intent = new Intent(Intent.ACTION_VIEW, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
    Uri uri = applicationInterface.getStorageUtils().getLastMediaScanned();
    if (uri == null) {
      if (MyDebug.LOG) Log.d(TAG, "go to latest media");
      StorageUtils.Media media = applicationInterface.getStorageUtils().getLatestMedia();
      if (media != null) {
        uri = media.uri;
      }
    }

    if (uri != null) {
      // check uri exists
      if (MyDebug.LOG) Log.d(TAG, "found most recent uri: " + uri);
      try {
        ContentResolver cr = getContentResolver();
        ParcelFileDescriptor pfd = cr.openFileDescriptor(uri, "r");
        if (pfd == null) {
          if (MyDebug.LOG) Log.d(TAG, "uri no longer exists (1): " + uri);
          uri = null;
        } else {
          pfd.close();
        }
      } catch (IOException e) {
        if (MyDebug.LOG) Log.d(TAG, "uri no longer exists (2): " + uri);
        uri = null;
      }
    }
    if (uri == null) {
      uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
    }
    if (!is_test) {
      // don't do if testing, as unclear how to exit activity to finish test (for testGallery())
      if (MyDebug.LOG) Log.d(TAG, "launch uri:" + uri);
      final String REVIEW_ACTION = "com.android.camera.action.REVIEW";
      try {
        // REVIEW_ACTION means we can view video files without autoplaying
        Intent intent = new Intent(REVIEW_ACTION, uri);
        intent.setClass(getApplicationContext(), SingleMediaActivity.class);
        this.startActivity(intent);
      } catch (ActivityNotFoundException e) {
        if (MyDebug.LOG) Log.d(TAG, "REVIEW_ACTION intent didn't work, try ACTION_VIEW");
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        // from http://stackoverflow.com/questions/11073832/no-activity-found-to-handle-intent -
        // needed to fix crash if no gallery app installed
        // Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("blah")); // test
        if (intent.resolveActivity(getPackageManager()) != null) {
          this.startActivity(intent);
        } else {
          preview.showToast(null, R.string.no_gallery_app);
        }
      }
    }
  }

  /**
   * Opens the Storage Access Framework dialog to select a folder.
   *
   * @param from_preferences Whether called from the Preferences
   */
  @TargetApi(Build.VERSION_CODES.LOLLIPOP)
  void openFolderChooserDialogSAF(boolean from_preferences) {
    if (MyDebug.LOG) Log.d(TAG, "openFolderChooserDialogSAF: " + from_preferences);
    this.saf_dialog_from_preferences = from_preferences;
    Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
    // Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
    // intent.addCategory(Intent.CATEGORY_OPENABLE);
    startActivityForResult(intent, 42);
  }

  /**
   * Call when the SAF save history has been updated. This is only public so we can call from
   * testing.
   *
   * @param save_folder The new SAF save folder Uri.
   */
  public void updateFolderHistorySAF(String save_folder) {
    if (MyDebug.LOG) Log.d(TAG, "updateSaveHistorySAF");
    if (save_location_history_saf == null) {
      save_location_history_saf =
          new SaveLocationHistory(this, "save_location_history_saf", save_folder);
    }
    save_location_history_saf.updateFolderHistory(save_folder);
  }

  /**
   * Listens for the response from the Storage Access Framework dialog to select a folder (as opened
   * with openFolderChooserDialogSAF()).
   */
  @TargetApi(Build.VERSION_CODES.LOLLIPOP)
  public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
    if (MyDebug.LOG) Log.d(TAG, "onActivityResult: " + requestCode);
    if (requestCode == 42) {
      if (resultCode == RESULT_OK && resultData != null) {
        Uri treeUri = resultData.getData();
        if (MyDebug.LOG) Log.d(TAG, "returned treeUri: " + treeUri);
        // from
        // https://developer.android.com/guide/topics/providers/document-provider.html#permissions :
        final int takeFlags =
            resultData.getFlags()
                & (Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        // Check for the freshest data.
        getContentResolver().takePersistableUriPermission(treeUri, takeFlags);
        SharedPreferences sharedPreferences = getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(PreferenceKeys.getSaveLocationSAFPreferenceKey(), treeUri.toString());
        editor.apply();

        if (MyDebug.LOG) Log.d(TAG, "update folder history for saf");
        updateFolderHistorySAF(treeUri.toString());

        File file = applicationInterface.getStorageUtils().getImageFolder();
        if (file != null) {
          preview.showToast(
              null,
              getResources().getString(R.string.changed_save_location)
                  + "\n"
                  + file.getAbsolutePath());
        }
      } else {
        if (MyDebug.LOG) Log.d(TAG, "SAF dialog cancelled");
        // cancelled - if the user had yet to set a save location, make sure we switch SAF back off
        SharedPreferences sharedPreferences = getDefaultSharedPreferences(this);
        String uri =
            sharedPreferences.getString(PreferenceKeys.getSaveLocationSAFPreferenceKey(), "");
        if (uri.length() == 0) {
          if (MyDebug.LOG) Log.d(TAG, "no SAF save location was set");
          SharedPreferences.Editor editor = sharedPreferences.edit();
          editor.putBoolean(PreferenceKeys.getUsingSAFPreferenceKey(), false);
          editor.apply();
          preview.showToast(null, R.string.saf_cancelled);
        }
      }

      if (!saf_dialog_from_preferences) {
        setWindowFlagsForCamera();
        showPreview(true);
      }
    }
  }

  void updateSaveFolder(String new_save_location) {
    if (MyDebug.LOG) Log.d(TAG, "updateSaveFolder: " + new_save_location);
    if (new_save_location != null) {
      SharedPreferences sharedPreferences = getDefaultSharedPreferences(this);
      String orig_save_location = this.applicationInterface.getStorageUtils().getSaveLocation();

      if (!orig_save_location.equals(new_save_location)) {
        if (MyDebug.LOG)
          Log.d(
              TAG,
              "changed save_folder to: "
                  + this.applicationInterface.getStorageUtils().getSaveLocation());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(PreferenceKeys.getSaveLocationPreferenceKey(), new_save_location);
        editor.apply();

        this.save_location_history.updateFolderHistory(this.getStorageUtils().getSaveLocation());
      }
    }
  }

  @Override
  public void onClick(View v) {
    if (v.getId() == R.id.increase_zoom) changeExposure(-1);
    else {
      changeExposure(1);
    }
  }

  public static class MyFolderChooserDialog extends FolderChooserDialog {
    @Override
    public void onDismiss(DialogInterface dialog) {
      if (MyDebug.LOG) Log.d(TAG, "FolderChooserDialog dismissed");
      // n.b., fragments have to be static (as they might be inserted into a new Activity - see
      // http://stackoverflow.com/questions/15571010/fragment-inner-class-should-be-static),
      // so we access the CameraActivity via the fragment's getActivity().
      CameraActivity main_activity = (CameraActivity) this.getActivity();
      main_activity.setWindowFlagsForCamera();
      main_activity.showPreview(true);
      String new_save_location = this.getChosenFolder();
      main_activity.updateSaveFolder(new_save_location);
      super.onDismiss(dialog);
    }
  }

  /** Opens Open Camera's own (non-Storage Access Framework) dialog to select a folder. */
  private void openFolderChooserDialog() {
    if (MyDebug.LOG) Log.d(TAG, "openFolderChooserDialog");
    showPreview(false);
    setWindowFlagsForSettings();
    FolderChooserDialog fragment = new MyFolderChooserDialog();
    fragment.show(getFragmentManager(), "FOLDER_FRAGMENT");
  }

  /**
   * User can long-click on gallery to select a recent save location from the history, of if not
   * available, go straight to the file dialog to pick a folder.
   */
  private void longClickedGallery() {
    if (MyDebug.LOG) Log.d(TAG, "longClickedGallery");
    if (applicationInterface.getStorageUtils().isUsingSAF()) {
      if (save_location_history_saf == null || save_location_history_saf.size() <= 1) {
        if (MyDebug.LOG) Log.d(TAG, "go straight to choose folder dialog for SAF");
        openFolderChooserDialogSAF(false);
        return;
      }
    } else {
      if (save_location_history.size() <= 1) {
        if (MyDebug.LOG) Log.d(TAG, "go straight to choose folder dialog");
        openFolderChooserDialog();
        return;
      }
    }
    final SaveLocationHistory history =
        applicationInterface.getStorageUtils().isUsingSAF()
            ? save_location_history_saf
            : save_location_history;
    showPreview(false);
    AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
    alertDialog.setTitle(R.string.choose_save_location);
    CharSequence[] items = new CharSequence[history.size() + 2];
    int index = 0;
    // history is stored in order most-recent-last
    for (int i = 0; i < history.size(); i++) {
      String folder_name = history.get(history.size() - 1 - i);
      if (applicationInterface.getStorageUtils().isUsingSAF()) {
        // try to get human readable form if possible
        File file =
            applicationInterface
                .getStorageUtils()
                .getFileFromDocumentUriSAF(Uri.parse(folder_name), true);
        if (file != null) {
          folder_name = file.getAbsolutePath();
        }
      }
      items[index++] = folder_name;
    }
    final int clear_index = index;
    items[index++] = getResources().getString(R.string.clear_folder_history);
    final int new_index = index;
    items[index++] = getResources().getString(R.string.choose_another_folder);
    alertDialog.setItems(
        items,
        new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            if (which == clear_index) {
              if (MyDebug.LOG) Log.d(TAG, "selected clear save history");
              new AlertDialog.Builder(CameraActivity.this)
                  .setIcon(android.R.drawable.ic_dialog_alert)
                  .setTitle(R.string.clear_folder_history)
                  .setMessage(R.string.clear_folder_history_question)
                  .setPositiveButton(
                      R.string.answer_yes,
                      new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                          if (MyDebug.LOG) Log.d(TAG, "confirmed clear save history");
                          if (applicationInterface.getStorageUtils().isUsingSAF())
                            clearFolderHistorySAF();
                          else clearFolderHistory();
                          setWindowFlagsForCamera();
                          showPreview(true);
                        }
                      })
                  .setNegativeButton(
                      R.string.answer_no,
                      new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                          if (MyDebug.LOG) Log.d(TAG, "don't clear save history");
                          setWindowFlagsForCamera();
                          showPreview(true);
                        }
                      })
                  .setOnCancelListener(
                      new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface arg0) {
                          if (MyDebug.LOG) Log.d(TAG, "cancelled clear save history");
                          setWindowFlagsForCamera();
                          showPreview(true);
                        }
                      })
                  .show();
            } else if (which == new_index) {
              if (MyDebug.LOG) Log.d(TAG, "selected choose new folder");
              if (applicationInterface.getStorageUtils().isUsingSAF()) {
                openFolderChooserDialogSAF(false);
              } else {
                openFolderChooserDialog();
              }
            } else {
              if (MyDebug.LOG) Log.d(TAG, "selected: " + which);
              if (which >= 0 && which < history.size()) {
                String save_folder = history.get(history.size() - 1 - which);
                if (MyDebug.LOG) Log.d(TAG, "changed save_folder from history to: " + save_folder);
                String save_folder_name = save_folder;
                if (applicationInterface.getStorageUtils().isUsingSAF()) {
                  // try to get human readable form if possible
                  File file =
                      applicationInterface
                          .getStorageUtils()
                          .getFileFromDocumentUriSAF(Uri.parse(save_folder), true);
                  if (file != null) {
                    save_folder_name = file.getAbsolutePath();
                  }
                }
                preview.showToast(
                    null,
                    getResources().getString(R.string.changed_save_location)
                        + "\n"
                        + save_folder_name);
                SharedPreferences sharedPreferences =
                    getDefaultSharedPreferences(CameraActivity.this);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                if (applicationInterface.getStorageUtils().isUsingSAF())
                  editor.putString(PreferenceKeys.getSaveLocationSAFPreferenceKey(), save_folder);
                else editor.putString(PreferenceKeys.getSaveLocationPreferenceKey(), save_folder);
                editor.apply();
                history.updateFolderHistory(save_folder); // to move new selection to most recent
              }
              setWindowFlagsForCamera();
              showPreview(true);
            }
          }
        });
    alertDialog.setOnCancelListener(
        new DialogInterface.OnCancelListener() {
          @Override
          public void onCancel(DialogInterface arg0) {
            setWindowFlagsForCamera();
            showPreview(true);
          }
        });
    alertDialog.show();
    // getWindow().setLayout(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
    setWindowFlagsForSettings();
  }

  /** Clears the non-SAF folder history. */
  public void clearFolderHistory() {
    if (MyDebug.LOG) Log.d(TAG, "clearFolderHistory");
    save_location_history.clearFolderHistory(getStorageUtils().getSaveLocation());
  }

  /** Clears the SAF folder history. */
  public void clearFolderHistorySAF() {
    if (MyDebug.LOG) Log.d(TAG, "clearFolderHistorySAF");
    save_location_history_saf.clearFolderHistory(getStorageUtils().getSaveLocationSAF());
  }

  private static void putBundleExtra(Bundle bundle, String key, List<String> values) {
    if (values != null) {
      String[] values_arr = new String[values.size()];
      int i = 0;
      for (String value : values) {
        values_arr[i] = value;
        i++;
      }
      bundle.putStringArray(key, values_arr);
    }
  }

  public void clickedShare(View view) {
    if (MyDebug.LOG) Log.d(TAG, "clickedShare");
    applicationInterface.shareLastImage();
  }

  public void clickedTrash(View view) {
    if (MyDebug.LOG) Log.d(TAG, "clickedTrash");
    applicationInterface.trashLastImage();
  }

  private final boolean test_panorama = false;

  /**
   * User has pressed the take picture button, or done an equivalent action to request this (e.g.,
   * volume buttons, audio trigger).
   */
  public void takePicture() {
    if (MyDebug.LOG) Log.d(TAG, "takePicture");

    if (test_panorama) {
      if (applicationInterface.getGyroSensor().isRecording()) {
        if (MyDebug.LOG) Log.d(TAG, "panorama complete");
        applicationInterface.stopPanorama();
        return;
      } else {
        if (MyDebug.LOG) Log.d(TAG, "start panorama");
        applicationInterface.startPanorama();
      }
    }

    this.takePicturePressed();
  }

  void takePicturePressed() {
    if ("flash_frontscreen_on".equals(getPreview().getCurrentFlashValue())) {
      enableMaxBrightness();
      if (MyDebug.LOG) Log.d(TAG, "Max brightness enabled");
    }
    if (MyDebug.LOG) Log.d(TAG, "takePicturePressed");
    closePopup();

    if (applicationInterface.getGyroSensor().isRecording()) {
      if (MyDebug.LOG) Log.d(TAG, "set next panorama point");
      applicationInterface.setNextPanoramaPoint();
    }

    this.preview.takePicturePressed();
  }

  private void enableMaxBrightness() {
    BatteryManager bm = (BatteryManager) getSystemService(BATTERY_SERVICE);
    int batLevel = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
    if (batLevel > 20) {
      WindowManager.LayoutParams layout = getWindow().getAttributes();
      layout.screenBrightness = 1F;
      getWindow().setAttributes(layout);
    }
  }

  /**
   * Lock the screen - this is Open Camera's own lock to guard against accidental presses, not the
   * standard Android lock.
   */
  void lockScreen() {
    findViewById(R.id.locker)
        .setOnTouchListener(
            new View.OnTouchListener() {
              @SuppressLint("ClickableViewAccessibility")
              @Override
              public boolean onTouch(View arg0, MotionEvent event) {
                return gestureDetector.onTouchEvent(event);
                // return true;
              }
            });
    screen_is_locked = true;
  }

  /** Unlock the screen (see lockScreen()). */
  void unlockScreen() {
    findViewById(R.id.locker).setOnTouchListener(null);
    screen_is_locked = false;
  }

  /** Whether the screen is locked (see lockScreen()). */
  public boolean isScreenLocked() {
    return screen_is_locked;
  }

  /** Listen for gestures. Doing a swipe will unlock the screen (see lockScreen()). */
  private class MyGestureDetector extends SimpleOnGestureListener {
    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
      try {
        if (MyDebug.LOG)
          Log.d(
              TAG,
              "from " + e1.getX() + " , " + e1.getY() + " to " + e2.getX() + " , " + e2.getY());
        final ViewConfiguration vc = ViewConfiguration.get(CameraActivity.this);
        // final int swipeMinDistance = 4*vc.getScaledPagingTouchSlop();
        final float scale = getResources().getDisplayMetrics().density;
        final int swipeMinDistance = (int) (160 * scale + 0.5f); // convert dps to pixels
        final int swipeThresholdVelocity = vc.getScaledMinimumFlingVelocity();
        if (MyDebug.LOG) {
          Log.d(
              TAG,
              "from " + e1.getX() + " , " + e1.getY() + " to " + e2.getX() + " , " + e2.getY());
          Log.d(TAG, "swipeMinDistance: " + swipeMinDistance);
        }
        float xdist = e1.getX() - e2.getX();
        float ydist = e1.getY() - e2.getY();
        float dist2 = xdist * xdist + ydist * ydist;
        float vel2 = velocityX * velocityX + velocityY * velocityY;
        if (dist2 > swipeMinDistance * swipeMinDistance
            && vel2 > swipeThresholdVelocity * swipeThresholdVelocity) {
          preview.showToast(screen_locked_toast, R.string.unlocked);
          unlockScreen();
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
      return false;
    }

    @Override
    public boolean onDown(MotionEvent e) {
      preview.showToast(screen_locked_toast, R.string.screen_is_locked);
      return true;
    }
  }

  @Override
  protected void onSaveInstanceState(Bundle state) {
    if (MyDebug.LOG) Log.d(TAG, "onSaveInstanceState");
    super.onSaveInstanceState(state);
    if (this.preview != null) {
      preview.onSaveInstanceState(state);
    }
    if (this.applicationInterface != null) {
      applicationInterface.onSaveInstanceState(state);
    }
  }

  public boolean supportsExposureButton() {
    if (preview.getCameraController() == null) return false;
    SharedPreferences sharedPreferences = getDefaultSharedPreferences(this);
    String iso_value =
        sharedPreferences.getString(
            PreferenceKeys.getISOPreferenceKey(), preview.getCameraController().getDefaultISO());
    boolean manual_iso = !iso_value.equals("auto");
    return preview.supportsExposures() || (manual_iso && preview.supportsISORange());
  }

  void cameraSetup() {
    long debug_time = 0;
    if (MyDebug.LOG) {
      Log.d(TAG, "cameraSetup");
      debug_time = System.currentTimeMillis();
    }
    if (this.supportsForceVideo4K() && preview.usingCamera2API()) {
      if (MyDebug.LOG) Log.d(TAG, "using Camera2 API, so can disable the force 4K option");
      this.disableForceVideo4K();
    }

    if (MyDebug.LOG)
      Log.d(
          TAG,
          "cameraSetup: time after handling Force 4K option: "
              + (System.currentTimeMillis() - debug_time));

    SharedPreferences sharedPreferences = getDefaultSharedPreferences(this);
    {
      if (MyDebug.LOG) Log.d(TAG, "set up zoom");
      if (MyDebug.LOG) Log.d(TAG, "has_zoom? " + preview.supportsZoom());
      if (MyDebug.LOG)
        Log.d(
            TAG,
            "cameraSetup: time after setting up zoom: "
                + (System.currentTimeMillis() - debug_time));

      View takePhotoButton = findViewById(R.id.take_photo);
      if (sharedPreferences.getBoolean(PreferenceKeys.getShowTakePhotoPreferenceKey(), true)) {
        if (!mainUI.inImmersiveMode()) {
          takePhotoButton.setVisibility(View.VISIBLE);
        }
      } else {
        takePhotoButton.setVisibility(View.INVISIBLE);
      }
    }
    {
      if (MyDebug.LOG) Log.d(TAG, "set up manual focus");
      SeekBar focusSeekBar = findViewById(R.id.focus_seekbar);
      focusSeekBar.setOnSeekBarChangeListener(
          null); // clear an existing listener - don't want to call the listener when setting up the
      // progress bar to match the existing state
      setProgressSeekbarScaled(
          focusSeekBar,
          0.0,
          preview.getMinimumFocusDistance(),
          preview.getCameraController().getFocusDistance());
      focusSeekBar.setOnSeekBarChangeListener(
          new OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
              double frac = progress / (double) manual_n;
              double scaling = CameraActivity.seekbarScaling(frac);
              float focus_distance = (float) (scaling * preview.getMinimumFocusDistance());
              preview.setFocusDistance(focus_distance);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
          });
      final int visibility =
          preview.getCurrentFocusValue() != null
                  && this.getPreview().getCurrentFocusValue().equals("focus_mode_manual2")
              ? View.VISIBLE
              : View.INVISIBLE;
      focusSeekBar.setVisibility(visibility);
    }
    if (MyDebug.LOG)
      Log.d(
          TAG,
          "cameraSetup: time after setting up manual focus: "
              + (System.currentTimeMillis() - debug_time));
    {
      if (preview.supportsISORange()) {
        if (MyDebug.LOG) Log.d(TAG, "set up iso");
        SeekBar iso_seek_bar = (findViewById(R.id.iso_seekbar));
        iso_seek_bar.setOnSeekBarChangeListener(
            null); // clear an existing listener - don't want to call the listener when setting up
        // the progress bar to match the existing state
        setProgressSeekbarExponential(
            iso_seek_bar,
            preview.getMinimumISO(),
            preview.getMaximumISO(),
            preview.getCameraController().getISO());
        iso_seek_bar.setOnSeekBarChangeListener(
            new OnSeekBarChangeListener() {
              @Override
              public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (MyDebug.LOG) Log.d(TAG, "iso seekbar onProgressChanged: " + progress);
                double frac = progress / (double) manual_n;
                if (MyDebug.LOG) Log.d(TAG, "exposure_time frac: " + frac);
                /*double scaling = CameraActivity.seekbarScaling(frac);
                if( MyDebug.LOG )
                	Log.d(TAG, "exposure_time scaling: " + scaling);
                int min_iso = preview.getMinimumISO();
                int max_iso = preview.getMaximumISO();
                int iso = min_iso + (int)(scaling * (max_iso - min_iso));*/
                int min_iso = preview.getMinimumISO();
                int max_iso = preview.getMaximumISO();
                int iso = (int) exponentialScaling(frac, min_iso, max_iso);
                preview.setISO(iso);
              }

              @Override
              public void onStartTrackingTouch(SeekBar seekBar) {}

              @Override
              public void onStopTrackingTouch(SeekBar seekBar) {}
            });
        if (preview.supportsExposureTime()) {
          if (MyDebug.LOG) Log.d(TAG, "set up exposure time");
          SeekBar exposure_time_seek_bar = (findViewById(R.id.exposure_time_seekbar));
          exposure_time_seek_bar.setOnSeekBarChangeListener(
              null); // clear an existing listener - don't want to call the listener when setting up
          // the progress bar to match the existing state
          setProgressSeekbarExponential(
              exposure_time_seek_bar,
              preview.getMinimumExposureTime(),
              preview.getMaximumExposureTime(),
              preview.getCameraController().getExposureTime());
          exposure_time_seek_bar.setOnSeekBarChangeListener(
              new OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                  if (MyDebug.LOG)
                    Log.d(TAG, "exposure_time seekbar onProgressChanged: " + progress);
                  double frac = progress / (double) manual_n;
                  if (MyDebug.LOG) Log.d(TAG, "exposure_time frac: " + frac);
                  // long exposure_time = min_exposure_time + (long)(frac * (max_exposure_time -
                  // min_exposure_time));
                  // double exposure_time_r = min_exposure_time_r + (frac * (max_exposure_time_r -
                  // min_exposure_time_r));
                  // long exposure_time = (long)(1.0 / exposure_time_r);
                  // we use the formula: [100^(percent/100) - 1]/99.0 rather than a simple linear
                  // scaling
                  /*double scaling = CameraActivity.seekbarScaling(frac);
                  if( MyDebug.LOG )
                  	Log.d(TAG, "exposure_time scaling: " + scaling);
                  long min_exposure_time = preview.getMinimumExposureTime();
                  long max_exposure_time = preview.getMaximumExposureTime();
                  long exposure_time = min_exposure_time + (long)(scaling * (max_exposure_time - min_exposure_time));*/
                  long min_exposure_time = preview.getMinimumExposureTime();
                  long max_exposure_time = preview.getMaximumExposureTime();
                  long exposure_time =
                      (long) exponentialScaling(frac, min_exposure_time, max_exposure_time);
                  preview.setExposureTime(exposure_time);
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {}

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {}
              });
        }
      }
    }
    if (preview.getSupportedWhiteBalances() != null && preview.supportsWhiteBalanceTemperature()) {
      if (MyDebug.LOG) Log.d(TAG, "set up manual white balance");
      SeekBar white_balance_seek_bar = (findViewById(R.id.white_balance_seekbar));
      white_balance_seek_bar.setOnSeekBarChangeListener(
          null); // clear an existing listener - don't want to call the listener when setting up the
      // progress bar to match the existing state
      final int minimum_temperature = preview.getMinimumWhiteBalanceTemperature();
      final int maximum_temperature = preview.getMaximumWhiteBalanceTemperature();
      // white balance should use linear scaling
      white_balance_seek_bar.setMax(maximum_temperature - minimum_temperature);
      white_balance_seek_bar.setProgress(
          preview.getCameraController().getWhiteBalanceTemperature() - minimum_temperature);
      white_balance_seek_bar.setOnSeekBarChangeListener(
          new OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
              if (MyDebug.LOG) Log.d(TAG, "white balance seekbar onProgressChanged: " + progress);
              int temperature = minimum_temperature + progress;
              preview.setWhiteBalanceTemperature(temperature);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
          });
    }
    if (MyDebug.LOG)
      Log.d(
          TAG,
          "cameraSetup: time after setting up iso: " + (System.currentTimeMillis() - debug_time));
    {
      if (preview.supportsExposures()) {
        if (MyDebug.LOG) Log.d(TAG, "set up exposure compensation");
        final int min_exposure = preview.getMinimumExposure();
        SeekBar exposure_seek_bar = (findViewById(R.id.exposure_seekbar));
        exposure_seek_bar.setOnSeekBarChangeListener(
            null); // clear an existing listener - don't want to call the listener when setting up
        // the progress bar to match the existing state
        exposure_seek_bar.setMax(preview.getMaximumExposure() - min_exposure);
        exposure_seek_bar.setProgress(preview.getCurrentExposure() - min_exposure);
        exposure_seek_bar.setOnSeekBarChangeListener(
            new OnSeekBarChangeListener() {
              @Override
              public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (MyDebug.LOG) Log.d(TAG, "exposure seekbar onProgressChanged: " + progress);
                preview.setExposure(min_exposure + progress);
              }

              @Override
              public void onStartTrackingTouch(SeekBar seekBar) {}

              @Override
              public void onStopTrackingTouch(SeekBar seekBar) {}
            });

        //				ZoomControls seek_bar_zoom = (ZoomControls)findViewById(R.id.exposure_seekbar_zoom);
        //				seek_bar_zoom.setOnZoomInClickListener(new View.OnClickListener(){
        //					public void onClick(View v){
        //						changeExposure(1);
        //					}
        //				});
        //				seek_bar_zoom.setOnZoomOutClickListener(new View.OnClickListener(){
        //					public void onClick(View v){
        //						changeExposure(-1);
        //					}
        //				});
      }
    }
    if (MyDebug.LOG)
      Log.d(
          TAG,
          "cameraSetup: time after setting up exposure: "
              + (System.currentTimeMillis() - debug_time));

    View exposureButton = findViewById(R.id.exposure);
    exposureButton.setVisibility(
        supportsExposureButton() && !mainUI.inImmersiveMode() ? View.VISIBLE : View.GONE);

    mainUI.setPopupIcon(); // needed so that the icon is set right even if no flash mode is set when
    // starting up camera (e.g., switching to front camera with no flash)
    if (MyDebug.LOG)
      Log.d(
          TAG,
          "cameraSetup: time after setting popup icon: "
              + (System.currentTimeMillis() - debug_time));

    mainUI.setSwitchCameraContentDescription();
    if (MyDebug.LOG)
      Log.d(
          TAG,
          "cameraSetup: time after setting take photo icon: "
              + (System.currentTimeMillis() - debug_time));

    if (MyDebug.LOG)
      Log.d(
          TAG,
          "cameraSetup: total time for cameraSetup: " + (System.currentTimeMillis() - debug_time));
  }

  public boolean supportsAutoStabilise() {
    return this.supports_auto_stabilise;
  }

  public boolean supportsDRO() {
    // require at least Android 5, for the Renderscript support in HDRProcessor
    return (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP);
  }

  public boolean supportsHDR() {
    // we also require the device have sufficient memory to do the processing, simplest to use the
    // same test as we do for auto-stabilise...
    // also require at least Android 5, for the Renderscript support in HDRProcessor
    return (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
        && this.supportsAutoStabilise()
        && preview.supportsExpoBracketing());
  }

  public boolean supportsExpoBracketing() {
    return preview.supportsExpoBracketing();
  }

  private int maxExpoBracketingNImages() {
    return preview.maxExpoBracketingNImages();
  }

  public boolean supportsForceVideo4K() {
    return this.supports_force_video_4k;
  }

  public boolean supportsCamera2() {
    return this.supports_camera2;
  }

  private void disableForceVideo4K() {
    this.supports_force_video_4k = false;
  }

  /** Return free memory in MB. */
  @SuppressWarnings("deprecation")
  public long freeMemory() { // return free memory in MB
    try {
      File folder = applicationInterface.getStorageUtils().getImageFolder();
      if (folder == null) {
        throw new IllegalArgumentException(); // so that we fall onto the backup
      }
      StatFs statFs = new StatFs(folder.getAbsolutePath());
      // cast to long to avoid overflow!
      long blocks = statFs.getAvailableBlocks();
      long size = statFs.getBlockSize();
      return (blocks * size) / 1048576;
    } catch (IllegalArgumentException e) {
      // this can happen if folder doesn't exist, or don't have read access
      // if the save folder is a subfolder of DCIM, we can just use that instead
      try {
        if (!applicationInterface.getStorageUtils().isUsingSAF()) {
          // StorageUtils.getSaveLocation() only valid if !isUsingSAF()
          String folder_name = applicationInterface.getStorageUtils().getSaveLocation();
          if (!folder_name.startsWith("/")) {
            File folder = StorageUtils.getBaseFolder();
            StatFs statFs = new StatFs(folder.getAbsolutePath());
            // cast to long to avoid overflow!
            long blocks = statFs.getAvailableBlocks();
            long size = statFs.getBlockSize();
            return (blocks * size) / 1048576;
          }
        }
      } catch (IllegalArgumentException e2) {
        // just in case
      }
    }
    return -1;
  }

  public static String getDonateLink() {
    return "https://play.google.com/store/apps/details?id=harman.mark.donation";
  }

  /*public static String getDonateMarketLink() {
  	return "market://details?id=harman.mark.donation";
  }*/

  public Preview getPreview() {
    return this.preview;
  }

  public MainUI getMainUI() {
    return this.mainUI;
  }

  public MyApplicationInterface getApplicationInterface() {
    return this.applicationInterface;
  }

  public TextFormatter getTextFormatter() {
    return this.textFormatter;
  }

  public LocationSupplier getLocationSupplier() {
    return this.applicationInterface.getLocationSupplier();
  }

  public StorageUtils getStorageUtils() {
    return this.applicationInterface.getStorageUtils();
  }

  public File getImageFolder() {
    return this.applicationInterface.getStorageUtils().getImageFolder();
  }

  public ToastBoxer getChangedAutoStabiliseToastBoxer() {
    return changed_auto_stabilise_toast;
  }

  /**
   * Displays a toast with information about the current preferences. If always_show is true, the
   * toast is always displayed; otherwise, we only display a toast if it's important to notify the
   * user (i.e., unusual non-default settings are set). We want a balance between not pestering the
   * user too much, whilst also reminding them if certain settings are on.
   */
  private void freeAudioListener(boolean wait_until_done) {
    if (MyDebug.LOG) Log.d(TAG, "freeAudioListener");
    if (audio_listener != null) {
      audio_listener.release(wait_until_done);
      audio_listener = null;
    }
    mainUI.audioControlStopped();
  }

  private void startAudioListener() {
    if (MyDebug.LOG) Log.d(TAG, "startAudioListener");
    audio_listener = new AudioListener(this);
    if (audio_listener.status()) {
      audio_listener.start();
      SharedPreferences sharedPreferences = getDefaultSharedPreferences(this);
      String sensitivity_pref =
          sharedPreferences.getString(
              PreferenceKeys.getAudioNoiseControlSensitivityPreferenceKey(), "0");
      switch (sensitivity_pref) {
        case "3":
          audio_noise_sensitivity = 50;
          break;
        case "2":
          audio_noise_sensitivity = 75;
          break;
        case "1":
          audio_noise_sensitivity = 125;
          break;
        case "-1":
          audio_noise_sensitivity = 150;
          break;
        case "-2":
          audio_noise_sensitivity = 200;
          break;
        default:
          // default
          audio_noise_sensitivity = 100;
          break;
      }
      mainUI.audioControlStarted();
    } else {
      audio_listener.release(true); // shouldn't be needed, but just to be safe
      audio_listener = null;
      preview.showToast(null, R.string.audio_listener_failed);
    }
  }

  private void initSpeechRecognizer() {
    if (MyDebug.LOG) Log.d(TAG, "initSpeechRecognizer");
    // in theory we could create the speech recognizer always (hopefully it shouldn't use battery
    // when not listening?), though to be safe, we only do this when the option is enabled (e.g.,
    // just in case this doesn't work on some devices!)
    SharedPreferences sharedPreferences = getDefaultSharedPreferences(this);
    boolean want_speech_recognizer =
        sharedPreferences
            .getString(PreferenceKeys.getAudioControlPreferenceKey(), "none")
            .equals("voice");
    if (speechRecognizer == null && want_speech_recognizer) {
      if (MyDebug.LOG) Log.d(TAG, "create new speechRecognizer");
      speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
      if (speechRecognizer != null) {
        speechRecognizerIsStarted = false;
        speechRecognizer.setRecognitionListener(
            new RecognitionListener() {
              @Override
              public void onBeginningOfSpeech() {
                if (MyDebug.LOG) Log.d(TAG, "RecognitionListener: onBeginningOfSpeech");
              }

              @Override
              public void onBufferReceived(byte[] buffer) {
                if (MyDebug.LOG) Log.d(TAG, "RecognitionListener: onBufferReceived");
              }

              @Override
              public void onEndOfSpeech() {
                if (MyDebug.LOG) Log.d(TAG, "RecognitionListener: onEndOfSpeech");
                speechRecognizerStopped();
              }

              @Override
              public void onError(int error) {
                if (MyDebug.LOG) Log.d(TAG, "RecognitionListener: onError: " + error);
                if (error != SpeechRecognizer.ERROR_NO_MATCH) {
                  // we sometime receive ERROR_NO_MATCH straight after listening starts
                  // it seems that the end is signalled either by ERROR_SPEECH_TIMEOUT or
                  // onEndOfSpeech()
                  speechRecognizerStopped();
                }
              }

              @Override
              public void onEvent(int eventType, Bundle params) {
                if (MyDebug.LOG) Log.d(TAG, "RecognitionListener: onEvent");
              }

              @Override
              public void onPartialResults(Bundle partialResults) {
                if (MyDebug.LOG) Log.d(TAG, "RecognitionListener: onPartialResults");
              }

              @Override
              public void onReadyForSpeech(Bundle params) {
                if (MyDebug.LOG) Log.d(TAG, "RecognitionListener: onReadyForSpeech");
              }

              public void onResults(Bundle results) {
                if (MyDebug.LOG) Log.d(TAG, "RecognitionListener: onResults");
                speechRecognizerStopped();
                ArrayList<String> list =
                    results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                boolean found = false;
                final String trigger = "cheese";
                // String debug_toast = "";
                for (int i = 0; list != null && i < list.size(); i++) {
                  String text = list.get(i);
                  if (MyDebug.LOG) {
                    float[] scores = results.getFloatArray(SpeechRecognizer.CONFIDENCE_SCORES);
                    if (scores != null) Log.d(TAG, "text: " + text + " score: " + scores[i]);
                  }
                  /*if( i > 0 )
                  	debug_toast += "\n";
                  debug_toast += text + " : " + results.getFloatArray(SpeechRecognizer.CONFIDENCE_SCORES)[i];*/
                  if (text.toLowerCase(Locale.US).contains(trigger)) {
                    found = true;
                  }
                }
                // preview.showToast(null, debug_toast); // debug only!
                if (found) {
                  if (MyDebug.LOG) Log.d(TAG, "audio trigger from speech recognition");
                  audioTrigger();
                } else if (list != null && list.size() > 0) {
                  String toast = list.get(0) + "?";
                  if (MyDebug.LOG) Log.d(TAG, "unrecognised: " + toast);
                  preview.showToast(audio_control_toast, toast);
                }
              }

              @Override
              public void onRmsChanged(float rmsdB) {}
            });
        if (!mainUI.inImmersiveMode()) {
          View speechRecognizerButton = findViewById(R.id.audio_control);
          speechRecognizerButton.setVisibility(View.VISIBLE);
        }
      }
    } else if (speechRecognizer != null && !want_speech_recognizer) {
      if (MyDebug.LOG) Log.d(TAG, "free existing SpeechRecognizer");
      freeSpeechRecognizer();
    }
  }

  private void freeSpeechRecognizer() {
    if (MyDebug.LOG) Log.d(TAG, "freeSpeechRecognizer");
    if (speechRecognizer != null) {
      speechRecognizerStopped();
      View speechRecognizerButton = findViewById(R.id.audio_control);
      speechRecognizerButton.setVisibility(View.GONE);
      speechRecognizer.destroy();
      speechRecognizer = null;
    }
  }

  public boolean hasAudioControl() {
    SharedPreferences sharedPreferences = getDefaultSharedPreferences(this);
    String audio_control =
        sharedPreferences.getString(PreferenceKeys.getAudioControlPreferenceKey(), "none");
    if (audio_control.equals("voice")) {
      return speechRecognizer != null;
    } else if (audio_control.equals("noise")) {
      return true;
    }
    return false;
  }

  /*void startAudioListeners() {
  	initAudioListener();
  	// no need to restart speech recognizer, as we didn't free it in stopAudioListeners(), and it's controlled by a user button
  }*/

  public void stopAudioListeners() {
    freeAudioListener(true);
    if (speechRecognizer != null) {
      // no need to free the speech recognizer, just stop it
      speechRecognizer.stopListening();
      speechRecognizerStopped();
    }
  }

  private void initLocation() {
    if (MyDebug.LOG) Log.d(TAG, "initLocation");
    if (!applicationInterface.getLocationSupplier().setupLocationListener()) {
      if (MyDebug.LOG) Log.d(TAG, "location permission not available, so request permission");
      requestLocationPermission();
    }
  }

  @SuppressWarnings("deprecation")
  private void initSound() {
    if (sound_pool == null) {
      if (MyDebug.LOG) Log.d(TAG, "create new sound_pool");
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        AudioAttributes audio_attributes =
            new AudioAttributes.Builder()
                .setLegacyStreamType(AudioManager.STREAM_SYSTEM)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();
        sound_pool =
            new SoundPool.Builder().setMaxStreams(1).setAudioAttributes(audio_attributes).build();
      } else {
        sound_pool = new SoundPool(1, AudioManager.STREAM_SYSTEM, 0);
      }
      sound_ids = new SparseIntArray();
    }
  }

  private void releaseSound() {
    if (sound_pool != null) {
      if (MyDebug.LOG) Log.d(TAG, "release sound_pool");
      sound_pool.release();
      sound_pool = null;
      sound_ids = null;
    }
  }

  // must be called before playSound (allowing enough time to load the sound)
  private void loadSound(int resource_id) {
    if (sound_pool != null) {
      if (MyDebug.LOG) Log.d(TAG, "loading sound resource: " + resource_id);
      int sound_id = sound_pool.load(this, resource_id, 1);
      if (MyDebug.LOG) Log.d(TAG, "    loaded sound: " + sound_id);
      sound_ids.put(resource_id, sound_id);
    }
  }

  // must call loadSound first (allowing enough time to load the sound)
  void playSound(int resource_id) {
    if (sound_pool != null) {
      if (sound_ids.indexOfKey(resource_id) < 0) {
        if (MyDebug.LOG) Log.d(TAG, "resource not loaded: " + resource_id);
      } else {
        int sound_id = sound_ids.get(resource_id);
        if (MyDebug.LOG) Log.d(TAG, "play sound: " + sound_id);
        sound_pool.play(sound_id, 1.0f, 1.0f, 0, 0, 1);
      }
    }
  }

  @SuppressWarnings("deprecation")
  void speak(String text) {
    if (textToSpeech != null && textToSpeechSuccess) {
      textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null);
    }
  }

  // Android 6+ permission handling:

  private final int MY_PERMISSIONS_REQUEST_CAMERA = 0;
  private final int MY_PERMISSIONS_REQUEST_STORAGE = 1;
  private final int MY_PERMISSIONS_REQUEST_RECORD_AUDIO = 2;
  private final int MY_PERMISSIONS_REQUEST_LOCATION = 3;

  /**
   * Show a "rationale" to the user for needing a particular permission, then request that
   * permission again once they close the dialog.
   */
  @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
  private void showRequestPermissionRationale(final int permission_code) {
    if (MyDebug.LOG) Log.d(TAG, "showRequestPermissionRational: " + permission_code);
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
      if (MyDebug.LOG) Log.e(TAG, "shouldn't be requesting permissions for pre-Android M!");
      return;
    }

    boolean ok = true;
    String[] permissions = null;
    int message_id = 0;
    if (permission_code == MY_PERMISSIONS_REQUEST_CAMERA) {
      if (MyDebug.LOG) Log.d(TAG, "display rationale for camera permission");
      permissions = new String[] {Manifest.permission.CAMERA};
      message_id = R.string.permission_rationale_camera;
    } else if (permission_code == MY_PERMISSIONS_REQUEST_STORAGE) {
      if (MyDebug.LOG) Log.d(TAG, "display rationale for storage permission");
      permissions = new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE};
      message_id = R.string.permission_rationale_storage;
    } else if (permission_code == MY_PERMISSIONS_REQUEST_RECORD_AUDIO) {
      if (MyDebug.LOG) Log.d(TAG, "display rationale for record audio permission");
      permissions = new String[] {Manifest.permission.RECORD_AUDIO};
      message_id = R.string.permission_rationale_record_audio;
    } else if (permission_code == MY_PERMISSIONS_REQUEST_LOCATION) {
      if (MyDebug.LOG) Log.d(TAG, "display rationale for location permission");
      permissions =
          new String[] {
            Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION
          };
      message_id = R.string.permission_rationale_location;
    } else {
      if (MyDebug.LOG)
        Log.e(TAG, "showRequestPermissionRational unknown permission_code: " + permission_code);
      ok = false;
    }

    if (ok) {
      final String[] permissions_f = permissions;
      new AlertDialog.Builder(this)
          .setTitle(R.string.permission_rationale_title)
          .setMessage(message_id)
          .setIcon(android.R.drawable.ic_dialog_alert)
          .setPositiveButton(
              android.R.string.ok,
              new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                  Intent intent = new Intent();
                  intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                  Uri uri = Uri.fromParts("package", getPackageName(), null);
                  intent.setData(uri);
                  startActivity(intent);
                }
              })
          .setNegativeButton(
              android.R.string.cancel,
              new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                  if (MyDebug.LOG) Log.d(TAG, "requesting permission...");
                  ActivityCompat.requestPermissions(
                      CameraActivity.this, permissions_f, permission_code);
                }
              })
          .setCancelable(false)
          .show();
    }
  }

  void requestCameraPermission() {
    if (MyDebug.LOG) Log.d(TAG, "requestCameraPermission");
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
      if (MyDebug.LOG) Log.e(TAG, "shouldn't be requesting permissions for pre-Android M!");
      return;
    }

    if (permanentDenyPermission) {
      return;
    }

    if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
      // Show an explanation to the user *asynchronously* -- don't block
      // this thread waiting for the user's response! After the user
      // sees the explanation, try again to request the permission.
      showRequestPermissionRationale(MY_PERMISSIONS_REQUEST_CAMERA);
    } else {
      // Can go ahead and request the permission
      if (MyDebug.LOG) Log.d(TAG, "requesting camera permission...");
      try {
        ActivityCompat.requestPermissions(
            this, new String[] {Manifest.permission.CAMERA}, MY_PERMISSIONS_REQUEST_CAMERA);
      } catch (SecurityException e) {
        SnackBarHandler.create(
                findViewById(android.R.id.content),
                "Please open camera in app once to allow permissions",
                Snackbar.LENGTH_SHORT)
            .show();
      }
    }
  }

  void requestStoragePermission() {
    if (MyDebug.LOG) Log.d(TAG, "requestStoragePermission");
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
      if (MyDebug.LOG) Log.e(TAG, "shouldn't be requesting permissions for pre-Android M!");
      return;
    }

    if (ActivityCompat.shouldShowRequestPermissionRationale(
        this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
      // Show an explanation to the user *asynchronously* -- don't block
      // this thread waiting for the user's response! After the user
      // sees the explanation, try again to request the permission.
      showRequestPermissionRationale(MY_PERMISSIONS_REQUEST_STORAGE);
    } else {
      // Can go ahead and request the permission
      if (MyDebug.LOG) Log.d(TAG, "requesting storage permission...");
      ActivityCompat.requestPermissions(
          this,
          new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE},
          MY_PERMISSIONS_REQUEST_STORAGE);
    }
  }

  void requestRecordAudioPermission() {
    if (MyDebug.LOG) Log.d(TAG, "requestRecordAudioPermission");
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
      if (MyDebug.LOG) Log.e(TAG, "shouldn't be requesting permissions for pre-Android M!");
      return;
    }

    if (ActivityCompat.shouldShowRequestPermissionRationale(
        this, Manifest.permission.RECORD_AUDIO)) {
      // Show an explanation to the user *asynchronously* -- don't block
      // this thread waiting for the user's response! After the user
      // sees the explanation, try again to request the permission.
      showRequestPermissionRationale(MY_PERMISSIONS_REQUEST_RECORD_AUDIO);
    } else {
      // Can go ahead and request the permission
      if (MyDebug.LOG) Log.d(TAG, "requesting record audio permission...");
      ActivityCompat.requestPermissions(
          this,
          new String[] {Manifest.permission.RECORD_AUDIO},
          MY_PERMISSIONS_REQUEST_RECORD_AUDIO);
    }
  }

  private void requestLocationPermission() {
    if (MyDebug.LOG) Log.d(TAG, "requestLocationPermission");
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
      if (MyDebug.LOG) Log.e(TAG, "shouldn't be requesting permissions for pre-Android M!");
      return;
    }

    if (ActivityCompat.shouldShowRequestPermissionRationale(
            this, Manifest.permission.ACCESS_FINE_LOCATION)
        || ActivityCompat.shouldShowRequestPermissionRationale(
            this, Manifest.permission.ACCESS_COARSE_LOCATION)) {
      // Show an explanation to the user *asynchronously* -- don't block
      // this thread waiting for the user's response! After the user
      // sees the explanation, try again to request the permission.
      showRequestPermissionRationale(MY_PERMISSIONS_REQUEST_LOCATION);
    } else {
      // Can go ahead and request the permission
      if (MyDebug.LOG) Log.d(TAG, "requesting loacation permissions...");
      ActivityCompat.requestPermissions(
          this,
          new String[] {
            Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION
          },
          MY_PERMISSIONS_REQUEST_LOCATION);
    }
  }

  @Override
  public void onRequestPermissionsResult(
      int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
    if (MyDebug.LOG) Log.d(TAG, "onRequestPermissionsResult: requestCode " + requestCode);
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
      if (MyDebug.LOG) Log.e(TAG, "shouldn't be requesting permissions for pre-Android M!");
      return;
    }

    switch (requestCode) {
      case MY_PERMISSIONS_REQUEST_CAMERA:
        {
          // If request is cancelled, the result arrays are empty.
          if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // permission was granted, yay! Do the
            // contacts-related task you need to do.
            if (MyDebug.LOG) Log.d(TAG, "camera permission granted");
            preview.retryOpenCamera();
          } else {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(
                CameraActivity.this, Manifest.permission.CAMERA)) permanentDenyPermission = true;

            if (MyDebug.LOG) Log.d(TAG, "camera permission denied");
            // permission denied, boo! Disable the
            // functionality that depends on this permission.
            // Open Camera doesn't need to do anything: the camera will remain closed
          }
          return;
        }
      case MY_PERMISSIONS_REQUEST_STORAGE:
        {
          // If request is cancelled, the result arrays are empty.
          if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // permission was granted, yay! Do the
            // contacts-related task you need to do.
            if (MyDebug.LOG) Log.d(TAG, "storage permission granted");
            preview.retryOpenCamera();
          } else {
            if (MyDebug.LOG) Log.d(TAG, "storage permission denied");
            // permission denied, boo! Disable the
            // functionality that depends on this permission.
            // Open Camera doesn't need to do anything: the camera will remain closed
          }
          return;
        }
      case MY_PERMISSIONS_REQUEST_RECORD_AUDIO:
        {
          // If request is cancelled, the result arrays are empty.
          if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // permission was granted, yay! Do the
            // contacts-related task you need to do.
            if (MyDebug.LOG) Log.d(TAG, "record audio permission granted");
            // no need to do anything
          } else {
            if (MyDebug.LOG) Log.d(TAG, "record audio permission denied");
            // permission denied, boo! Disable the
            // functionality that depends on this permission.
            // no need to do anything
            // note that we don't turn off record audio option, as user may then record video not
            // realising audio won't be recorded - best to be explicit each time
          }
          return;
        }
      case MY_PERMISSIONS_REQUEST_LOCATION:
        {
          // If request is cancelled, the result arrays are empty.
          if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // permission ̄was granted, yay! Do the
            // contacts-related task you need to do.
            if (MyDebug.LOG) Log.d(TAG, "location permission granted");
            initLocation();
          } else {
            if (MyDebug.LOG) Log.d(TAG, "location permission denied");
            // permission denied, boo! Disable the
            // functionality that depends on this permission.
            // for location, seems best to turn the option back off
            if (MyDebug.LOG)
              Log.d(TAG, "location permission not available, so switch location off");
            preview.showToast(null, R.string.permission_location_not_available);
            SharedPreferences settings = getDefaultSharedPreferences(this);
            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean(PreferenceKeys.getLocationPreferenceKey(), false);
            editor.apply();
          }
          return;
        }
      default:
        {
          if (MyDebug.LOG) Log.e(TAG, "unknown requestCode " + requestCode);
        }
    }
  }

  // for testing:
  public SaveLocationHistory getSaveLocationHistory() {
    return this.save_location_history;
  }

  public SaveLocationHistory getSaveLocationHistorySAF() {
    return this.save_location_history_saf;
  }

  public void usedFolderPicker() {
    if (applicationInterface.getStorageUtils().isUsingSAF()) {
      save_location_history_saf.updateFolderHistory(getStorageUtils().getSaveLocationSAF());
    } else {
      save_location_history.updateFolderHistory(getStorageUtils().getSaveLocation());
    }
  }

  public boolean hasThumbnailAnimation() {
    return this.applicationInterface.hasThumbnailAnimation();
  }
}
