package org.fossasia.phimpme.opencamera.CameraController;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.ImageFormat;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureFailure;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.DngCreator;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.MeteringRectangle;
import android.hardware.camera2.params.RggbChannelVector;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.location.Location;
import android.media.ExifInterface;
import android.media.Image;
import android.media.ImageReader;
import android.media.MediaActionSound;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.util.Log;
import android.util.Range;
import android.util.SizeF;
import android.view.Display;
import android.view.Surface;
import android.view.SurfaceHolder;

import org.fossasia.phimpme.opencamera.Camera.MyDebug;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/** Provides support using Android 5's Camera 2 API
 *  android.hardware.camera2.*.
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class CameraController2 extends CameraController {
	private static final String TAG = "CameraController2";

	private final Context context;
	private CameraDevice camera;
	private String cameraIdS;
	private CameraCharacteristics characteristics;
	private List<Integer> zoom_ratios;
	private int current_zoom_value;
	private boolean supports_face_detect_mode_simple;
	private boolean supports_face_detect_mode_full;
	private final ErrorCallback preview_error_cb;
	private final ErrorCallback camera_error_cb;
	private CameraCaptureSession captureSession;
	private CaptureRequest.Builder previewBuilder;
	private AutoFocusCallback autofocus_cb;
	private boolean capture_follows_autofocus_hint;
	private FaceDetectionListener face_detection_listener;
	private final Object image_reader_lock = new Object(); // lock to make sure we only handle one image being available at a time
	private final Object open_camera_lock = new Object(); // lock to wait for camera to be opened from CameraDevice.StateCallback
	private final Object create_capture_session_lock = new Object(); // lock to wait for capture session to be created from CameraCaptureSession.StateCallback
	private ImageReader imageReader;
	private boolean want_expo_bracketing;
	private final static int max_expo_bracketing_n_images = 5; // could be more, but limit to 5 for now
	private int expo_bracketing_n_images = 3;
	private double expo_bracketing_stops = 2.0;
	private boolean use_expo_fast_burst = true;
	private boolean optimise_ae_for_dro = false;
	private boolean want_raw;
	//private boolean want_raw = true;
	private android.util.Size raw_size;
	private ImageReader imageReaderRaw;
	private OnRawImageAvailableListener onRawImageAvailableListener;
	private PictureCallback jpeg_cb;
	private PictureCallback raw_cb;
	//private CaptureRequest pending_request_when_ready;
	private int n_burst; // number of expected burst images in this capture
	private final List<byte []> pending_burst_images = new ArrayList<>(); // burst images that have been captured so far, but not yet sent to the application
	private List<CaptureRequest> burst_capture_requests; // the set of burst capture requests - used when not using captureBurst() (i.e., when use_expo_fast_burst==false)
	private long burst_start_ms = 0; // time when burst started (used for measuring performance of captures when not using fast burst)
	private DngCreator pending_dngCreator;
	private Image pending_image;
	private ErrorCallback take_picture_error_cb;
	//private ImageReader previewImageReader;
	private SurfaceTexture texture;
	private Surface surface_texture;
	private HandlerThread thread;
	private Handler handler;
	
	private int preview_width;
	private int preview_height;
	
	private int picture_width;
	private int picture_height;
	
	private static final int STATE_NORMAL = 0;
	private static final int STATE_WAITING_AUTOFOCUS = 1;
	private static final int STATE_WAITING_PRECAPTURE_START = 2;
	private static final int STATE_WAITING_PRECAPTURE_DONE = 3;
	private static final int STATE_WAITING_FAKE_PRECAPTURE_START = 4;
	private static final int STATE_WAITING_FAKE_PRECAPTURE_DONE = 5;
	private int state = STATE_NORMAL;
	private long precapture_state_change_time_ms = -1; // time we changed state for precapture modes
	private static final long precapture_start_timeout_c = 2000;
	private static final long precapture_done_timeout_c = 3000;
	private boolean ready_for_capture;

	private boolean use_fake_precapture; // see CameraController.setUseCamera2FakeFlash() for details - this is the user/application setting, see use_fake_precapture_mode for whether fake precapture is enabled (as we may do this for other purposes, e.g., front screen flash)
	private boolean use_fake_precapture_mode; // true if either use_fake_precapture is true, or we're temporarily using fake precapture mode (e.g., for front screen flash or exposure bracketing)
	private boolean fake_precapture_torch_performed; // whether we turned on torch to do a fake precapture
	private boolean fake_precapture_torch_focus_performed; // whether we turned on torch to do an autofocus, in fake precapture mode
	private boolean fake_precapture_use_flash; // whether we decide to use flash in auto mode (if fake_precapture_use_autoflash_time_ms != -1)
	private long fake_precapture_use_flash_time_ms = -1; // when we last checked to use flash in auto mode

	private ContinuousFocusMoveCallback continuous_focus_move_callback;
	
	private final MediaActionSound media_action_sound = new MediaActionSound();
	private boolean sounds_enabled = true;

	private boolean capture_result_is_ae_scanning;
	private Integer capture_result_ae; // latest ae_state, null if not available
	private boolean is_flash_required; // whether capture_result_ae suggests FLASH_REQUIRED? Or in neither FLASH_REQUIRED nor CONVERGED, this stores the last known result
	private boolean capture_result_has_white_balance_rggb;
	private RggbChannelVector capture_result_white_balance_rggb;
	private boolean capture_result_has_iso;
	private int capture_result_iso;
	private boolean capture_result_has_exposure_time;
	private long capture_result_exposure_time;
	private boolean capture_result_has_frame_duration;
	private long capture_result_frame_duration ;
	/*private boolean capture_result_has_focus_distance;
	private float capture_result_focus_distance_min;
	private float capture_result_focus_distance_max;*/
	
	private enum RequestTag {
		CAPTURE
	}

	private final static int min_white_balance_temperature_c = 1000;
	private final static int max_white_balance_temperature_c = 15000;

	private class CameraSettings {
		// keys that we need to store, to pass to the stillBuilder, but doesn't need to be passed to previewBuilder (should set sensible defaults)
		private int rotation;
		private Location location;
		private byte jpeg_quality = 90;

		// keys that we have passed to the previewBuilder, that we need to store to also pass to the stillBuilder (should set sensible defaults, or use a has_ boolean if we don't want to set a default)
		private int scene_mode = CameraMetadata.CONTROL_SCENE_MODE_DISABLED;
		private int color_effect = CameraMetadata.CONTROL_EFFECT_MODE_OFF;
		private int white_balance = CameraMetadata.CONTROL_AWB_MODE_AUTO;
		private int white_balance_temperature = 5000; // used for white_balance == CONTROL_AWB_MODE_OFF
		private String flash_value = "flash_off";
		private boolean has_iso;
		//private int ae_mode = CameraMetadata.CONTROL_AE_MODE_ON;
		//private int flash_mode = CameraMetadata.FLASH_MODE_OFF;
		private int iso;
		private long exposure_time = EXPOSURE_TIME_DEFAULT;
		private Rect scalar_crop_region; // no need for has_scalar_crop_region, as we can set to null instead
		private boolean has_ae_exposure_compensation;
		private int ae_exposure_compensation;
		private boolean has_af_mode;
		private int af_mode = CaptureRequest.CONTROL_AF_MODE_AUTO;
		private float focus_distance; // actual value passed to camera device (set to 0.0 if in infinity mode)
		private float focus_distance_manual; // saved setting when in manual mode
		private boolean ae_lock;
		private MeteringRectangle[] af_regions; // no need for has_scalar_crop_region, as we can set to null instead
		private MeteringRectangle[] ae_regions; // no need for has_scalar_crop_region, as we can set to null instead
		private boolean has_face_detect_mode;
		private int face_detect_mode = CaptureRequest.STATISTICS_FACE_DETECT_MODE_OFF;
		private boolean video_stabilization;
		
		private int getExifOrientation() {
			int exif_orientation = ExifInterface.ORIENTATION_NORMAL;
			switch( (rotation + 360) % 360 ) {
				case 0:
					exif_orientation = ExifInterface.ORIENTATION_NORMAL;
					break;
				case 90:
					exif_orientation = isFrontFacing() ?
							ExifInterface.ORIENTATION_ROTATE_270 :
							ExifInterface.ORIENTATION_ROTATE_90;
					break;
				case 180:
					exif_orientation = ExifInterface.ORIENTATION_ROTATE_180;
					break;
				case 270:
					exif_orientation = isFrontFacing() ?
							ExifInterface.ORIENTATION_ROTATE_90 :
							ExifInterface.ORIENTATION_ROTATE_270;
					break;
				default:
					// leave exif_orientation unchanged
					if( MyDebug.LOG )
						Log.e(TAG, "unexpected rotation: " + rotation);
					break;
			}
			if( MyDebug.LOG ) {
				Log.d(TAG, "rotation: " + rotation);
				Log.d(TAG, "exif_orientation: " + exif_orientation);
			}
			return exif_orientation;
		}

		private void setupBuilder(CaptureRequest.Builder builder, boolean is_still) {
			//builder.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_ON);
			//builder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
			//builder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_AUTO);
			//builder.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH);
			//builder.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_ON_ALWAYS_FLASH);

			builder.set(CaptureRequest.CONTROL_AF_TRIGGER, CameraMetadata.CONTROL_AF_TRIGGER_IDLE);

			setSceneMode(builder);
			setColorEffect(builder);
			setWhiteBalance(builder);
			setAEMode(builder, is_still);
			setCropRegion(builder);
			setExposureCompensation(builder);
			setFocusMode(builder);
			setFocusDistance(builder);
			setAutoExposureLock(builder);
			setAFRegions(builder);
			setAERegions(builder);
			setFaceDetectMode(builder);
			setRawMode(builder);
			setVideoStabilization(builder);

			if( is_still ) {
				if( location != null ) {
					builder.set(CaptureRequest.JPEG_GPS_LOCATION, location);
				}
				builder.set(CaptureRequest.JPEG_ORIENTATION, rotation);
				builder.set(CaptureRequest.JPEG_QUALITY, jpeg_quality);
			}
		}

		private boolean setSceneMode(CaptureRequest.Builder builder) {
			if( MyDebug.LOG ) {
				Log.d(TAG, "setSceneMode");
				Log.d(TAG, "builder: " + builder);
			}
			/*if( builder.get(CaptureRequest.CONTROL_SCENE_MODE) == null && scene_mode == CameraMetadata.CONTROL_SCENE_MODE_DISABLED ) {
				// can leave off
			}
			else*/ if( builder.get(CaptureRequest.CONTROL_SCENE_MODE) == null || builder.get(CaptureRequest.CONTROL_SCENE_MODE) != scene_mode ) {
				if( MyDebug.LOG )
					Log.d(TAG, "setting scene mode: " + scene_mode);
				if( scene_mode == CameraMetadata.CONTROL_SCENE_MODE_DISABLED ) {
					builder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
				}
				else {
					builder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_USE_SCENE_MODE);
				}
				builder.set(CaptureRequest.CONTROL_SCENE_MODE, scene_mode);
				return true;
			}
			return false;
		}

		private boolean setColorEffect(CaptureRequest.Builder builder) {
			/*if( builder.get(CaptureRequest.CONTROL_EFFECT_MODE) == null && color_effect == CameraMetadata.CONTROL_EFFECT_MODE_OFF ) {
				// can leave off
			}
			else*/ if( builder.get(CaptureRequest.CONTROL_EFFECT_MODE) == null || builder.get(CaptureRequest.CONTROL_EFFECT_MODE) != color_effect ) {
				if( MyDebug.LOG )
					Log.d(TAG, "setting color effect: " + color_effect);
				builder.set(CaptureRequest.CONTROL_EFFECT_MODE, color_effect);
				return true;
			}
			return false;
		}

		private boolean setWhiteBalance(CaptureRequest.Builder builder) {
			boolean changed = false;
			/*if( builder.get(CaptureRequest.CONTROL_AWB_MODE) == null && white_balance == CameraMetadata.CONTROL_AWB_MODE_AUTO ) {
				// can leave off
			}
			else*/ if( builder.get(CaptureRequest.CONTROL_AWB_MODE) == null || builder.get(CaptureRequest.CONTROL_AWB_MODE) != white_balance ) {
				if( MyDebug.LOG )
					Log.d(TAG, "setting white balance: " + white_balance);
				builder.set(CaptureRequest.CONTROL_AWB_MODE, white_balance);
				changed = true;
			}
			if( white_balance == CameraMetadata.CONTROL_AWB_MODE_OFF ) {
				if( MyDebug.LOG )
					Log.d(TAG, "setting white balance temperature: " + white_balance_temperature);
				// manual white balance
				RggbChannelVector rggbChannelVector = convertTemperatureToRggb(white_balance_temperature);
				builder.set(CaptureRequest.COLOR_CORRECTION_MODE, CameraMetadata.COLOR_CORRECTION_MODE_TRANSFORM_MATRIX);
				builder.set(CaptureRequest.COLOR_CORRECTION_GAINS, rggbChannelVector);
				changed = true;
			}
			return changed;
		}

		private boolean setAEMode(CaptureRequest.Builder builder, boolean is_still) {
			if( MyDebug.LOG )
				Log.d(TAG, "setAEMode");
			if( has_iso ) {
				if( MyDebug.LOG ) {
					Log.d(TAG, "manual mode");
					Log.d(TAG, "iso: " + iso);
					Log.d(TAG, "exposure_time: " + exposure_time);
				}
				builder.set(CaptureRequest.CONTROL_AE_MODE, CameraMetadata.CONTROL_AE_MODE_OFF);
				builder.set(CaptureRequest.SENSOR_SENSITIVITY, iso);
				long actual_exposure_time = exposure_time;
				if( !is_still ) {
					// if this isn't for still capture, have a max exposure time of 1/12s
					actual_exposure_time = Math.min(exposure_time, 1000000000L/12);
					if( MyDebug.LOG )
						Log.d(TAG, "actually using exposure_time of: " + actual_exposure_time);
				}
				builder.set(CaptureRequest.SENSOR_EXPOSURE_TIME, actual_exposure_time);
				// for now, flash is disabled when using manual iso - it seems to cause ISO level to jump to 100 on Nexus 6 when flash is turned on!
				builder.set(CaptureRequest.FLASH_MODE, CameraMetadata.FLASH_MODE_OFF);
				// set flash via CaptureRequest.FLASH
		    	/*if( flash_value.equals("flash_off") ) {
					builder.set(CaptureRequest.FLASH_MODE, CameraMetadata.FLASH_MODE_OFF);
		    	}
		    	else if( flash_value.equals("flash_auto") ) {
					builder.set(CaptureRequest.FLASH_MODE, is_still ? CameraMetadata.FLASH_MODE_SINGLE : CameraMetadata.FLASH_MODE_OFF);
		    	}
		    	else if( flash_value.equals("flash_on") ) {
					builder.set(CaptureRequest.FLASH_MODE, is_still ? CameraMetadata.FLASH_MODE_SINGLE : CameraMetadata.FLASH_MODE_OFF);
		    	}
		    	else if( flash_value.equals("flash_torch") ) {
					builder.set(CaptureRequest.FLASH_MODE, CameraMetadata.FLASH_MODE_TORCH);
		    	}
		    	else if( flash_value.equals("flash_red_eye") ) {
					builder.set(CaptureRequest.FLASH_MODE, is_still ? CameraMetadata.FLASH_MODE_SINGLE : CameraMetadata.FLASH_MODE_OFF);
		    	}*/
			}
			else {
				if( MyDebug.LOG ) {
					Log.d(TAG, "auto mode");
					Log.d(TAG, "flash_value: " + flash_value);
				}
				// prefer to set flash via the ae mode (otherwise get even worse results), except for torch which we can't
				switch(flash_value) {
					case "flash_off":
						builder.set(CaptureRequest.CONTROL_AE_MODE, CameraMetadata.CONTROL_AE_MODE_ON);
						builder.set(CaptureRequest.FLASH_MODE, CameraMetadata.FLASH_MODE_OFF);
						break;
					case "flash_auto":
						// note we set this even in fake flash mode (where we manually turn torch on and off to simulate flash) so we
						// can read the FLASH_REQUIRED state to determine if flash is required
		    		/*if( use_fake_precapture || CameraController2.this.want_expo_bracketing )
			    		builder.set(CaptureRequest.CONTROL_AE_MODE, CameraMetadata.CONTROL_AE_MODE_ON);
		    		else*/
						builder.set(CaptureRequest.CONTROL_AE_MODE, CameraMetadata.CONTROL_AE_MODE_ON_AUTO_FLASH);
						builder.set(CaptureRequest.FLASH_MODE, CameraMetadata.FLASH_MODE_OFF);
						break;
					case "flash_on":
						// see note above for "flash_auto" for why we set this even fake flash mode - arguably we don't need to know
						// about FLASH_REQUIRED in flash_on mode, but we set it for consistency...
		    		/*if( use_fake_precapture || CameraController2.this.want_expo_bracketing )
			    		builder.set(CaptureRequest.CONTROL_AE_MODE, CameraMetadata.CONTROL_AE_MODE_ON);
		    		else*/
						builder.set(CaptureRequest.CONTROL_AE_MODE, CameraMetadata.CONTROL_AE_MODE_ON_ALWAYS_FLASH);
						builder.set(CaptureRequest.FLASH_MODE, CameraMetadata.FLASH_MODE_OFF);
						break;
					case "flash_torch":
						builder.set(CaptureRequest.CONTROL_AE_MODE, CameraMetadata.CONTROL_AE_MODE_ON);
						builder.set(CaptureRequest.FLASH_MODE, CameraMetadata.FLASH_MODE_TORCH);
						break;
					case "flash_red_eye":
						// not supported for expo bracketing
						if (CameraController2.this.want_expo_bracketing)
							builder.set(CaptureRequest.CONTROL_AE_MODE, CameraMetadata.CONTROL_AE_MODE_ON);
						else
							builder.set(CaptureRequest.CONTROL_AE_MODE, CameraMetadata.CONTROL_AE_MODE_ON_AUTO_FLASH_REDEYE);
						builder.set(CaptureRequest.FLASH_MODE, CameraMetadata.FLASH_MODE_OFF);
						break;
					case "flash_frontscreen_auto":
					case "flash_frontscreen_on":
						builder.set(CaptureRequest.CONTROL_AE_MODE, CameraMetadata.CONTROL_AE_MODE_ON);
						builder.set(CaptureRequest.FLASH_MODE, CameraMetadata.FLASH_MODE_OFF);
						break;
				}
			}
			return true;
		}

		private void setCropRegion(CaptureRequest.Builder builder) {
			if( scalar_crop_region != null ) {
				builder.set(CaptureRequest.SCALER_CROP_REGION, scalar_crop_region);
			}
		}

		private boolean setExposureCompensation(CaptureRequest.Builder builder) {
			if( !has_ae_exposure_compensation )
				return false;
			if( has_iso ) {
				if( MyDebug.LOG )
					Log.d(TAG, "don't set exposure compensation in manual iso mode");
				return false;
			}
			if( builder.get(CaptureRequest.CONTROL_AE_EXPOSURE_COMPENSATION) == null || ae_exposure_compensation != builder.get(CaptureRequest.CONTROL_AE_EXPOSURE_COMPENSATION) ) {
				if( MyDebug.LOG )
					Log.d(TAG, "change exposure to " + ae_exposure_compensation);
				builder.set(CaptureRequest.CONTROL_AE_EXPOSURE_COMPENSATION, ae_exposure_compensation);
	        	return true;
			}
			return false;
		}

		private void setFocusMode(CaptureRequest.Builder builder) {
			if( has_af_mode ) {
				if( MyDebug.LOG )
					Log.d(TAG, "change af mode to " + af_mode);
				builder.set(CaptureRequest.CONTROL_AF_MODE, af_mode);
			}
		}
		
		private void setFocusDistance(CaptureRequest.Builder builder) {
			if( MyDebug.LOG )
				Log.d(TAG, "change focus distance to " + focus_distance);
			builder.set(CaptureRequest.LENS_FOCUS_DISTANCE, focus_distance);
		}

		private void setAutoExposureLock(CaptureRequest.Builder builder) {
	    	builder.set(CaptureRequest.CONTROL_AE_LOCK, ae_lock);
		}

		private void setAFRegions(CaptureRequest.Builder builder) {
			if( af_regions != null && characteristics.get(CameraCharacteristics.CONTROL_MAX_REGIONS_AF) > 0 ) {
				builder.set(CaptureRequest.CONTROL_AF_REGIONS, af_regions);
			}
		}

		private void setAERegions(CaptureRequest.Builder builder) {
			if( ae_regions != null && characteristics.get(CameraCharacteristics.CONTROL_MAX_REGIONS_AE) > 0 ) {
				builder.set(CaptureRequest.CONTROL_AE_REGIONS, ae_regions);
			}
		}

		private void setFaceDetectMode(CaptureRequest.Builder builder) {
			if( has_face_detect_mode )
				builder.set(CaptureRequest.STATISTICS_FACE_DETECT_MODE, face_detect_mode);
			else
				builder.set(CaptureRequest.STATISTICS_FACE_DETECT_MODE, CaptureRequest.STATISTICS_FACE_DETECT_MODE_OFF);
		}
		
		private void setRawMode(CaptureRequest.Builder builder) {
			// DngCreator says "For best quality DNG files, it is strongly recommended that lens shading map output is enabled if supported"
			// docs also say "ON is always supported on devices with the RAW capability", so we don't check for STATISTICS_LENS_SHADING_MAP_MODE_ON being available
			if( want_raw ) {
				builder.set(CaptureRequest.STATISTICS_LENS_SHADING_MAP_MODE, CaptureRequest.STATISTICS_LENS_SHADING_MAP_MODE_ON);
			}
		}
		
		private void setVideoStabilization(CaptureRequest.Builder builder) {
			builder.set(CaptureRequest.CONTROL_VIDEO_STABILIZATION_MODE, video_stabilization ? CaptureRequest.CONTROL_VIDEO_STABILIZATION_MODE_ON : CaptureRequest.CONTROL_VIDEO_STABILIZATION_MODE_OFF);
		}
		
		// n.b., if we add more methods, remember to update setupBuilder() above!
	}

	/** Converts a white balance temperature to red, green even, green odd and blue components.
	 */
	private RggbChannelVector convertTemperatureToRggb(int temperature_kelvin) {
		float temperature = temperature_kelvin / 100.0f;
		float red;
		float green;
		float blue;

		if( temperature <= 66 ) {
			red = 255;
		}
		else {
			red = temperature - 60;
			red = (float) (329.698727446 * (Math.pow((double) red, -0.1332047592)));
			if( red < 0 )
				red = 0;
			if( red > 255 )
				red = 255;
		}

		if( temperature <= 66 ) {
			green = temperature;
			green = (float) (99.4708025861 * Math.log(green) - 161.1195681661);
			if( green < 0 )
				green = 0;
			if( green > 255 )
				green = 255;
		}
		else {
			green = temperature - 60;
			green = (float) (288.1221695283 * (Math.pow((double) green, -0.0755148492)));
			if (green < 0)
				green = 0;
			if (green > 255)
				green = 255;
		}

		if( temperature >= 66 )
			blue = 255;
		else if( temperature <= 19 )
			blue = 0;
		else {
			blue = temperature - 10;
			blue = (float) (138.5177312231 * Math.log(blue) - 305.0447927307);
			if( blue < 0 )
				blue = 0;
			if( blue > 255 )
				blue = 255;
		}

		if( MyDebug.LOG ) {
			Log.d(TAG, "red: " + red);
			Log.d(TAG, "green: " + green);
			Log.d(TAG, "blue: " + blue);
		}
		RggbChannelVector rggbChannelVector = new RggbChannelVector((red/255)*2,(green/255),(green/255),(blue/255)*2);
		return rggbChannelVector;
	}

	/** Converts a red, green even, green odd and blue components to a white balance temperature.
	 *  Note that this is not necessarily an inverse of convertTemperatureToRggb, since many rggb
	 *  values can map to the same temperature.
	 */
	private int convertRggbToTemperature(RggbChannelVector rggbChannelVector) {
		if( MyDebug.LOG ) {
			Log.d(TAG, "temperature:");
			Log.d(TAG, "    red: " + rggbChannelVector.getRed());
			Log.d(TAG, "    green even: " + rggbChannelVector.getGreenEven());
			Log.d(TAG, "    green odd: " + rggbChannelVector.getGreenOdd());
			Log.d(TAG, "    blue: " + rggbChannelVector.getBlue());
		}
		float red = rggbChannelVector.getRed();
		float green_even = rggbChannelVector.getGreenEven();
		float green_odd = rggbChannelVector.getGreenOdd();
		float blue = rggbChannelVector.getBlue();
		float green = 0.5f*(green_even + green_odd);

		float max = Math.max(red, blue);
		if( green > max )
			green = max;

		float scale = 255.0f/max;
		red *= scale;
		green *= scale;
		blue *= scale;

		int red_i = (int)red;
		int green_i = (int)green;
		int blue_i = (int)blue;
		int temperature;
		if( red_i == blue_i ) {
			temperature = 6600;
		}
		else if( red_i > blue_i ) {
			// temperature <= 6600
			int t_g = (int)( 100 * Math.exp((green_i + 161.1195681661) / 99.4708025861) );
			if( blue_i == 0 ) {
				temperature = t_g;
			}
			else {
				int t_b = (int)( 100 * (Math.exp((blue_i + 305.0447927307) / 138.5177312231) + 10) );
				temperature = (t_g + t_b)/2;
			}
		}
		else {
			// temperature >= 6700
			if( red_i <= 1 || green_i <= 1 ) {
				temperature = max_white_balance_temperature_c;
			}
			else {
				int t_r = (int) (100 * (Math.pow(red_i / 329.698727446, 1.0 / -0.1332047592) + 60.0));
				int t_g = (int) (100 * (Math.pow(green_i / 288.1221695283, 1.0 / -0.0755148492) + 60.0));
				temperature = (t_r + t_g) / 2;
			}
		}
		temperature = Math.max(temperature, min_white_balance_temperature_c);
		temperature = Math.min(temperature, max_white_balance_temperature_c);
		if( MyDebug.LOG ) {
			Log.d(TAG, "    temperature: " + temperature);
		}
		return temperature;
	}

	private class OnRawImageAvailableListener implements ImageReader.OnImageAvailableListener {
		private CaptureResult capture_result;
		private Image image;
		
		void setCaptureResult(CaptureResult capture_result) {
			if( MyDebug.LOG )
				Log.d(TAG, "setCaptureResult()");
			synchronized( image_reader_lock ) {
				/* synchronize, as we don't want to set the capture_result, at the same time that onImageAvailable() is called, as
				 * we'll end up calling processImage() both in onImageAvailable() and here.
				 */
				this.capture_result = capture_result;
				if( image != null ) {
					if( MyDebug.LOG )
						Log.d(TAG, "can now process the image");
					processImage();
				}
			}
		}
		
		void clear() {
			if( MyDebug.LOG )
				Log.d(TAG, "clear()");
			synchronized( image_reader_lock ) {
				// synchronize just to be safe?
				capture_result = null;
				image = null;
			}
		}
		
		private void processImage() {
			if( MyDebug.LOG )
				Log.d(TAG, "processImage()");
			if( capture_result == null ) {
				if( MyDebug.LOG )
					Log.d(TAG, "don't yet have still_capture_result");
				return;
			}
			if( image == null ) {
				if( MyDebug.LOG )
					Log.d(TAG, "don't have image?!");
				return;
			}
			if( MyDebug.LOG ) {
				Log.d(TAG, "now have all info to process raw image");
				Log.d(TAG, "image timestamp: " + image.getTimestamp());
			}
            DngCreator dngCreator = new DngCreator(characteristics, capture_result);
            // set fields
            dngCreator.setOrientation(camera_settings.getExifOrientation());
			if( camera_settings.location != null ) {
                dngCreator.setLocation(camera_settings.location);
			}
			
			pending_dngCreator = dngCreator;
			pending_image = image;
			
            PictureCallback cb = raw_cb;
            if( jpeg_cb == null ) {
				if( MyDebug.LOG )
					Log.d(TAG, "jpeg callback already done, so can go ahead with raw callback");
				takePendingRaw();
				if( MyDebug.LOG )
					Log.d(TAG, "all image callbacks now completed");
				cb.onCompleted();
            }
            else {
				if( MyDebug.LOG )
					Log.d(TAG, "need to wait for jpeg callback");
            }
			if( MyDebug.LOG )
				Log.d(TAG, "done processImage");
		}

		@Override
		public void onImageAvailable(ImageReader reader) {
			if( MyDebug.LOG )
				Log.d(TAG, "new still raw image available");
			if( raw_cb == null ) {
				if( MyDebug.LOG )
					Log.d(TAG, "no picture callback available");
				return;
			}
			synchronized( image_reader_lock ) {
				// see comment above in setCaptureResult() for why we sychonize
				image = reader.acquireNextImage();
				processImage();
			}
			if( MyDebug.LOG )
				Log.d(TAG, "done onImageAvailable");
		}
	}
	
	private final CameraSettings camera_settings = new CameraSettings();
	private boolean push_repeating_request_when_torch_off = false;
	private CaptureRequest push_repeating_request_when_torch_off_id = null;
	/*private boolean push_set_ae_lock = false;
	private CaptureRequest push_set_ae_lock_id = null;*/

	private CaptureRequest fake_precapture_turn_on_torch_id = null; // the CaptureRequest used to turn on torch when starting the "fake" precapture

	@Override
	public void onError() {
		Log.e(TAG, "onError");
		if( camera != null ) {
			onError(camera);
		}
	}

	private void onError(@NonNull CameraDevice cam) {
		Log.e(TAG, "onError");
		boolean camera_already_opened = this.camera != null;
		// need to set the camera to null first, as closing the camera may take some time, and we don't want any other operations to continue (if called from main thread)
		this.camera = null;
		if( MyDebug.LOG )
			Log.d(TAG, "onError: camera is now set to null");
		cam.close();
		if( MyDebug.LOG )
			Log.d(TAG, "onError: camera is now closed");

		if( camera_already_opened ) {
			// need to communicate the problem to the application
			// n.b., as this is potentially serious error, we always log even if MyDebug.LOG is false
			Log.e(TAG, "error occurred after camera was opened");
			camera_error_cb.onError();
		}
	}

	/** Opens the camera device.
	 * @param context Application context.
	 * @param cameraId Which camera to open (must be between 0 and CameraControllerManager2.getNumberOfCameras()-1).
	 * @param preview_error_cb onError() will be called if the preview stops due to error.
	 * @param camera_error_cb onError() will be called if the camera closes due to serious error. No more calls to the CameraController2 object should be made (though a new one can be created, to try reopening the camera).
	 * @throws CameraControllerException if the camera device fails to open.
     */
	public CameraController2(Context context, int cameraId, final ErrorCallback preview_error_cb, final ErrorCallback camera_error_cb) throws CameraControllerException {
		super(cameraId);
		if( MyDebug.LOG )
			Log.d(TAG, "create new CameraController2: " + cameraId);

		this.context = context;
		this.preview_error_cb = preview_error_cb;
		this.camera_error_cb = camera_error_cb;

		thread = new HandlerThread("CameraBackground");
		thread.start(); 
		handler = new Handler(thread.getLooper());

		final CameraManager manager = (CameraManager)context.getSystemService(Context.CAMERA_SERVICE);

		class MyStateCallback extends CameraDevice.StateCallback {
			boolean callback_done; // must sychronize on this and notifyAll when setting to true
			boolean first_callback = true; // Google Camera says we may get multiple callbacks, but only the first indicates the status of the camera opening operation
			@Override
			public void onOpened(@NonNull CameraDevice cam) {
				if( MyDebug.LOG )
					Log.d(TAG, "camera opened, first_callback? " + first_callback);
				/*if( true ) // uncomment to test timeout code
					return;*/
				if( first_callback ) {
					first_callback = false;

				    try {
				    	// we should be able to get characteristics at any time, but Google Camera only does so when camera opened - so do so similarly to be safe
						if( MyDebug.LOG )
							Log.d(TAG, "try to get camera characteristics");
						characteristics = manager.getCameraCharacteristics(cameraIdS);
						if( MyDebug.LOG )
							Log.d(TAG, "successfully obtained camera characteristics");

						CameraController2.this.camera = cam;

						// note, this won't start the preview yet, but we create the previewBuilder in order to start setting camera parameters
						createPreviewRequest();
				    }
				    catch(CameraAccessException e) {
						if( MyDebug.LOG ) {
							Log.e(TAG, "failed to get camera characteristics");
							Log.e(TAG, "reason: " + e.getReason());
							Log.e(TAG, "message: " + e.getMessage());
						}
						e.printStackTrace();
						// don't throw CameraControllerException here - instead error is handled by setting callback_done to callback_done, and the fact that camera will still be null
					}

					if( MyDebug.LOG )
						Log.d(TAG, "about to synchronize to say callback done");
				    synchronized( open_camera_lock ) {
				    	callback_done = true;
						if( MyDebug.LOG )
							Log.d(TAG, "callback done, about to notify");
						open_camera_lock.notifyAll();
						if( MyDebug.LOG )
							Log.d(TAG, "callback done, notification done");
				    }
				}
			}

			@Override
			public void onClosed(@NonNull CameraDevice cam) {
				if( MyDebug.LOG )
					Log.d(TAG, "camera closed, first_callback? " + first_callback);
				// caller should ensure camera variables are set to null
				if( first_callback ) {
					first_callback = false;
				}
			}

			@Override
			public void onDisconnected(@NonNull CameraDevice cam) {
				if( MyDebug.LOG )
					Log.d(TAG, "camera disconnected, first_callback? " + first_callback);
				if( first_callback ) {
					first_callback = false;
					// must call close() if disconnected before camera was opened
					// need to set the camera to null first, as closing the camera may take some time, and we don't want any other operations to continue (if called from main thread)
					CameraController2.this.camera = null;
					if( MyDebug.LOG )
						Log.d(TAG, "onDisconnected: camera is now set to null");
					cam.close();
					if( MyDebug.LOG )
						Log.d(TAG, "onDisconnected: camera is now closed");
					if( MyDebug.LOG )
						Log.d(TAG, "about to synchronize to say callback done");
				    synchronized( open_camera_lock ) {
				    	callback_done = true;
						if( MyDebug.LOG )
							Log.d(TAG, "callback done, about to notify");
						open_camera_lock.notifyAll();
						if( MyDebug.LOG )
							Log.d(TAG, "callback done, notification done");
				    }
				}
			}

			@Override
			public void onError(@NonNull CameraDevice cam, int error) {
				// n.b., as this is potentially serious error, we always log even if MyDebug.LOG is false
				Log.e(TAG, "camera error: " + error);
				if( MyDebug.LOG ) {
					Log.d(TAG, "received camera: " + cam);
					Log.d(TAG, "actual camera: " + CameraController2.this.camera);
					Log.d(TAG, "first_callback? " + first_callback);
				}
				if( first_callback ) {
					first_callback = false;
				}
				CameraController2.this.onError(cam);
				if( MyDebug.LOG )
					Log.d(TAG, "about to synchronize to say callback done");
			    synchronized( open_camera_lock ) {
			    	callback_done = true;
					if( MyDebug.LOG )
						Log.d(TAG, "callback done, about to notify");
					open_camera_lock.notifyAll();
					if( MyDebug.LOG )
						Log.d(TAG, "callback done, notification done");
			    }
			}
		}
		final MyStateCallback myStateCallback = new MyStateCallback();

		try {
			if( MyDebug.LOG )
				Log.d(TAG, "get camera id list");
			this.cameraIdS = manager.getCameraIdList()[cameraId];
			if( MyDebug.LOG )
				Log.d(TAG, "about to open camera: " + cameraIdS);
			manager.openCamera(cameraIdS, myStateCallback, handler);
			if( MyDebug.LOG )
				Log.d(TAG, "open camera request complete");
		}
		catch(CameraAccessException e) {
			if( MyDebug.LOG ) {
				Log.e(TAG, "failed to open camera: CameraAccessException");
				Log.e(TAG, "reason: " + e.getReason());
				Log.e(TAG, "message: " + e.getMessage());
			}
			e.printStackTrace();
			throw new CameraControllerException();
		}
		catch(UnsupportedOperationException e) {
			// Google Camera catches UnsupportedOperationException
			if( MyDebug.LOG ) {
				Log.e(TAG, "failed to open camera: UnsupportedOperationException");
				Log.e(TAG, "message: " + e.getMessage());
			}
			e.printStackTrace();
			throw new CameraControllerException();
		}
		catch(SecurityException e) {
			// Google Camera catches SecurityException
			if( MyDebug.LOG ) {
				Log.e(TAG, "failed to open camera: SecurityException");
				Log.e(TAG, "message: " + e.getMessage());
			}
			e.printStackTrace();
			throw new CameraControllerException();
		}

		// set up a timeout - sometimes if the camera has got in a state where it can't be opened until after a reboot, we'll never even get a myStateCallback callback called
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				if( MyDebug.LOG )
					Log.d(TAG, "check if camera has opened in reasonable time");
				synchronized( open_camera_lock ) {
					if( !myStateCallback.callback_done ) {
						// n.b., as this is potentially serious error, we always log even if MyDebug.LOG is false
						Log.e(TAG, "timeout waiting for camera callback");
						myStateCallback.first_callback = true;
						myStateCallback.callback_done = true;
						open_camera_lock.notifyAll();
					}
				}
			}
		}, 10000);

		if( MyDebug.LOG )
			Log.d(TAG, "wait until camera opened...");
		// need to wait until camera is opened
		synchronized( open_camera_lock ) {
			while( !myStateCallback.callback_done ) {
				try {
					// release the lock, and wait until myStateCallback calls notifyAll()
					open_camera_lock.wait();
				}
				catch(InterruptedException e) {
					if( MyDebug.LOG )
						Log.d(TAG, "interrupted while waiting until camera opened");
					e.printStackTrace();
				}
			}
		}
		if( camera == null ) {
			// n.b., as this is potentially serious error, we always log even if MyDebug.LOG is false
			Log.e(TAG, "camera failed to open");
			throw new CameraControllerException();
		}
		if( MyDebug.LOG )
			Log.d(TAG, "camera now opened: " + camera);

		/*{
			// test error handling
			final Handler handler = new Handler();
			handler.postDelayed(new Runnable() {
				@Override
				public void run() {
					if( MyDebug.LOG )
						Log.d(TAG, "test camera error");
					myStateCallback.onError(camera, CameraDevice.StateCallback.ERROR_CAMERA_DEVICE);
				}
			}, 5000);
		}*/

		/*CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraIdS);
	    StreamConfigurationMap configs = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
	    android.util.Size [] camera_picture_sizes = configs.getOutputSizes(ImageFormat.JPEG);
		imageReader = ImageReader.newInstance(camera_picture_sizes[0].getWidth(), , ImageFormat.JPEG, 2);*/
		
		// preload sounds to reduce latency - important so that START_VIDEO_RECORDING sound doesn't play after video has started (which means it'll be heard in the resultant video)
		media_action_sound.load(MediaActionSound.START_VIDEO_RECORDING);
		media_action_sound.load(MediaActionSound.STOP_VIDEO_RECORDING);
		media_action_sound.load(MediaActionSound.SHUTTER_CLICK);
	}

	@Override
	public void release() {
		if( MyDebug.LOG )
			Log.d(TAG, "release");
		if( thread != null ) {
			thread.quitSafely();
			try {
				thread.join();
				thread = null;
				handler = null;
			}
			catch(InterruptedException e) {
				e.printStackTrace();
			}
		}
		if( captureSession != null ) {
			captureSession.close();
			captureSession = null;
			//pending_request_when_ready = null;
		}
		previewBuilder = null;
		if( camera != null ) {
			camera.close();
			camera = null;
		}
		closePictureImageReader();
		/*if( previewImageReader != null ) {
			previewImageReader.close();
			previewImageReader = null;
		}*/
	}
	
	private void closePictureImageReader() {
		if( MyDebug.LOG )
			Log.d(TAG, "closePictureImageReader()");
		if( imageReader != null ) {
			imageReader.close();
			imageReader = null;
		}
		if( imageReaderRaw != null ) {
			imageReaderRaw.close();
			imageReaderRaw = null;
			onRawImageAvailableListener = null;
		}
	}

	private List<String> convertFocusModesToValues(int [] supported_focus_modes_arr, float minimum_focus_distance) {
		if( MyDebug.LOG )
			Log.d(TAG, "convertFocusModesToValues()");
		if( supported_focus_modes_arr.length == 0 )
			return null;
	    List<Integer> supported_focus_modes = new ArrayList<>();
		for(Integer supported_focus_mode : supported_focus_modes_arr)
			supported_focus_modes.add(supported_focus_mode);
	    List<String> output_modes = new ArrayList<>();
		// also resort as well as converting
		if( supported_focus_modes.contains(CaptureRequest.CONTROL_AF_MODE_AUTO) ) {
			output_modes.add("focus_mode_auto");
			if( MyDebug.LOG ) {
				Log.d(TAG, " supports focus_mode_auto");
			}
		}
		if( supported_focus_modes.contains(CaptureRequest.CONTROL_AF_MODE_MACRO) ) {
			output_modes.add("focus_mode_macro");
			if( MyDebug.LOG )
				Log.d(TAG, " supports focus_mode_macro");
		}
		if( supported_focus_modes.contains(CaptureRequest.CONTROL_AF_MODE_AUTO) ) {
			output_modes.add("focus_mode_locked");
			if( MyDebug.LOG ) {
				Log.d(TAG, " supports focus_mode_locked");
			}
		}
		if( supported_focus_modes.contains(CaptureRequest.CONTROL_AF_MODE_OFF) ) {
			output_modes.add("focus_mode_infinity");
			if( minimum_focus_distance > 0.0f ) {
				output_modes.add("focus_mode_manual2");
				if( MyDebug.LOG ) {
					Log.d(TAG, " supports focus_mode_manual2");
				}
			}
		}
		if( supported_focus_modes.contains(CaptureRequest.CONTROL_AF_MODE_EDOF) ) {
			output_modes.add("focus_mode_edof");
			if( MyDebug.LOG )
				Log.d(TAG, " supports focus_mode_edof");
		}
		if( supported_focus_modes.contains(CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE) ) {
			output_modes.add("focus_mode_continuous_picture");
			if( MyDebug.LOG )
				Log.d(TAG, " supports focus_mode_continuous_picture");
		}
		if( supported_focus_modes.contains(CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_VIDEO) ) {
			output_modes.add("focus_mode_continuous_video");
			if( MyDebug.LOG )
				Log.d(TAG, " supports focus_mode_continuous_video");
		}
		return output_modes;
	}

	public String getAPI() {
		return "Camera2 (Android L)";
	}
	
	@Override
	public CameraFeatures getCameraFeatures() {
		if( MyDebug.LOG )
			Log.d(TAG, "getCameraFeatures()");
		CameraFeatures camera_features = new CameraFeatures();
		if( MyDebug.LOG ) {
			int hardware_level = characteristics.get(CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL);
			if( hardware_level == CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_LEGACY )
				Log.d(TAG, "Hardware Level: LEGACY");
			else if( hardware_level == CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_LIMITED )
				Log.d(TAG, "Hardware Level: LIMITED");
			else if( hardware_level == CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_FULL )
				Log.d(TAG, "Hardware Level: FULL");
			else
				Log.e(TAG, "Unknown Hardware Level!");
		}

		float max_zoom = characteristics.get(CameraCharacteristics.SCALER_AVAILABLE_MAX_DIGITAL_ZOOM);
		camera_features.is_zoom_supported = max_zoom > 0.0f;
		if( MyDebug.LOG )
			Log.d(TAG, "max_zoom: " + max_zoom);
		if( camera_features.is_zoom_supported ) {
			// set 20 steps per 2x factor
			final int steps_per_2x_factor = 20;
			//final double scale_factor = Math.pow(2.0, 1.0/(double)steps_per_2x_factor);
			int n_steps =(int)( (steps_per_2x_factor * Math.log(max_zoom + 1.0e-11)) / Math.log(2.0));
			final double scale_factor = Math.pow(max_zoom, 1.0/(double)n_steps);
			if( MyDebug.LOG ) {
				Log.d(TAG, "n_steps: " + n_steps);
				Log.d(TAG, "scale_factor: " + scale_factor);
			}
			camera_features.zoom_ratios = new ArrayList<>();
			camera_features.zoom_ratios.add(100);
			double zoom = 1.0;
			for(int i=0;i<n_steps-1;i++) {
				zoom *= scale_factor;
				camera_features.zoom_ratios.add((int)(zoom*100));
			}
			camera_features.zoom_ratios.add((int)(max_zoom*100));
			camera_features.max_zoom = camera_features.zoom_ratios.size()-1;
			this.zoom_ratios = camera_features.zoom_ratios;
		}
		else {
			this.zoom_ratios = null;
		}

		int [] face_modes = characteristics.get(CameraCharacteristics.STATISTICS_INFO_AVAILABLE_FACE_DETECT_MODES);
		camera_features.supports_face_detection = false;
		supports_face_detect_mode_simple = false;
		supports_face_detect_mode_full = false;
		for(int face_mode : face_modes) {
			if( MyDebug.LOG )
				Log.d(TAG, "face detection mode: " + face_mode);
			// we currently only make use of the "SIMPLE" features, documented as:
			// "Return face rectangle and confidence values only."
			// note that devices that support STATISTICS_FACE_DETECT_MODE_FULL (e.g., Nexus 6) don't return
			// STATISTICS_FACE_DETECT_MODE_SIMPLE in the list, so we have check for either
			if( face_mode == CameraCharacteristics.STATISTICS_FACE_DETECT_MODE_SIMPLE ) {
				camera_features.supports_face_detection = true;
				supports_face_detect_mode_simple = true;
				if( MyDebug.LOG )
					Log.d(TAG, "supports simple face detection mode");
			}
			else if( face_mode == CameraCharacteristics.STATISTICS_FACE_DETECT_MODE_FULL ) {
				camera_features.supports_face_detection = true;
				supports_face_detect_mode_full = true;
				if( MyDebug.LOG )
					Log.d(TAG, "supports full face detection mode");
			}
		}
		if( camera_features.supports_face_detection ) {
			int face_count = characteristics.get(CameraCharacteristics.STATISTICS_INFO_MAX_FACE_COUNT);
			if( face_count <= 0 ) {
				camera_features.supports_face_detection = false;
				supports_face_detect_mode_simple = false;
				supports_face_detect_mode_full = false;
			}
		}

		if( MyDebug.LOG ) {
			int[] ois_modes = characteristics.get(CameraCharacteristics.LENS_INFO_AVAILABLE_OPTICAL_STABILIZATION); // may be null on some devices
			if (ois_modes != null) {
				for (int ois_mode : ois_modes) {
					if (MyDebug.LOG)
						Log.d(TAG, "ois mode: " + ois_mode);
					if (ois_mode == CameraCharacteristics.LENS_OPTICAL_STABILIZATION_MODE_ON) {
						if (MyDebug.LOG)
							Log.d(TAG, "supports ois");
					}
				}
			}
		}

		int [] capabilities = characteristics.get(CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES);
		boolean capabilities_raw = false;
		boolean capabilities_high_speed_video = false;
		for(int capability : capabilities) {
			if( capability == CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_RAW ) {
				capabilities_raw = true;
			}
			else if( capability == CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_CONSTRAINED_HIGH_SPEED_VIDEO ) {
				capabilities_high_speed_video = true;
			}
		}
		if( MyDebug.LOG ) {
			Log.d(TAG, "capabilities_raw?: " + capabilities_raw);
			Log.d(TAG, "capabilities_high_speed_video?: " + capabilities_high_speed_video);
		}

		StreamConfigurationMap configs = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);

	    android.util.Size [] camera_picture_sizes = configs.getOutputSizes(ImageFormat.JPEG);
		camera_features.picture_sizes = new ArrayList<>();
		for(android.util.Size camera_size : camera_picture_sizes) {
			if( MyDebug.LOG )
				Log.d(TAG, "picture size: " + camera_size.getWidth() + " x " + camera_size.getHeight());
			camera_features.picture_sizes.add(new CameraController.Size(camera_size.getWidth(), camera_size.getHeight()));
		}

    	raw_size = null;
    	if( capabilities_raw ) {
		    android.util.Size [] raw_camera_picture_sizes = configs.getOutputSizes(ImageFormat.RAW_SENSOR);
		    if( raw_camera_picture_sizes == null ) {
				if( MyDebug.LOG )
					Log.d(TAG, "RAW not supported, failed to get RAW_SENSOR sizes");
				want_raw = false; // just in case it got set to true somehow
		    }
		    else {
				for(android.util.Size size : raw_camera_picture_sizes) {
		        	if( raw_size == null || size.getWidth()*size.getHeight() > raw_size.getWidth()*raw_size.getHeight() ) {
		        		raw_size = size;
		        	}
		        }
				if( raw_size == null ) {
					if( MyDebug.LOG )
						Log.d(TAG, "RAW not supported, failed to find a raw size");
					want_raw = false; // just in case it got set to true somehow
				}
				else {
					if( MyDebug.LOG )
						Log.d(TAG, "raw supported, raw size: " + raw_size.getWidth() + " x " + raw_size.getHeight());
					camera_features.supports_raw = true;				
				}
			}
    	}
    	else {
			if( MyDebug.LOG )
				Log.d(TAG, "RAW capability not supported");
			want_raw = false; // just in case it got set to true somehow
    	}
		
	    android.util.Size [] camera_video_sizes = configs.getOutputSizes(MediaRecorder.class);
		camera_features.video_sizes = new ArrayList<>();
		for(android.util.Size camera_size : camera_video_sizes) {
			if( MyDebug.LOG )
				Log.d(TAG, "video size: " + camera_size.getWidth() + " x " + camera_size.getHeight());
			if( camera_size.getWidth() > 4096 || camera_size.getHeight() > 2160 )
				continue; // Nexus 6 returns these, even though not supported?!
			camera_features.video_sizes.add(new CameraController.Size(camera_size.getWidth(), camera_size.getHeight()));
		}

		if( capabilities_high_speed_video ) {
			android.util.Size[] camera_video_sizes_high_speed = configs.getHighSpeedVideoSizes();
			camera_features.video_sizes_high_speed = new ArrayList<>();
			for (android.util.Size camera_size : camera_video_sizes_high_speed) {
				if (MyDebug.LOG)
					Log.d(TAG, "high speed video size: " + camera_size.getWidth() + " x " + camera_size.getHeight());
				if (camera_size.getWidth() > 4096 || camera_size.getHeight() > 2160)
					continue; // just in case? see above
				camera_features.video_sizes_high_speed.add(new CameraController.Size(camera_size.getWidth(), camera_size.getHeight()));
			}
		}

		android.util.Size [] camera_preview_sizes = configs.getOutputSizes(SurfaceTexture.class);
		camera_features.preview_sizes = new ArrayList<>();
        Point display_size = new Point();
		Activity activity = (Activity)context;
        {
            Display display = activity.getWindowManager().getDefaultDisplay();
            display.getRealSize(display_size);
    		if( MyDebug.LOG )
    			Log.d(TAG, "display_size: " + display_size.x + " x " + display_size.y);
        }
		for(android.util.Size camera_size : camera_preview_sizes) {
			if( MyDebug.LOG )
				Log.d(TAG, "preview size: " + camera_size.getWidth() + " x " + camera_size.getHeight());
			if( camera_size.getWidth() > display_size.x || camera_size.getHeight() > display_size.y ) {
				// Nexus 6 returns these, even though not supported?! (get green corruption lines if we allow these)
				// Google Camera filters anything larger than height 1080, with a todo saying to use device's measurements
				continue;
			}
			camera_features.preview_sizes.add(new CameraController.Size(camera_size.getWidth(), camera_size.getHeight()));
		}
		
		if( characteristics.get(CameraCharacteristics.FLASH_INFO_AVAILABLE) ) {
			camera_features.supported_flash_values = new ArrayList<>();
			camera_features.supported_flash_values.add("flash_off");
			camera_features.supported_flash_values.add("flash_auto");
			camera_features.supported_flash_values.add("flash_on");
			camera_features.supported_flash_values.add("flash_torch");
			if( !use_fake_precapture ) {
				camera_features.supported_flash_values.add("flash_red_eye");
			}
		}
		else if( isFrontFacing() ) {
			camera_features.supported_flash_values = new ArrayList<>();
			camera_features.supported_flash_values.add("flash_off");
			camera_features.supported_flash_values.add("flash_frontscreen_auto");
			camera_features.supported_flash_values.add("flash_frontscreen_on");
		}

		Float minimum_focus_distance = characteristics.get(CameraCharacteristics.LENS_INFO_MINIMUM_FOCUS_DISTANCE); // may be null on some devices
		if( minimum_focus_distance != null ) {
			camera_features.minimum_focus_distance = minimum_focus_distance;
			if( MyDebug.LOG )
				Log.d(TAG, "minimum_focus_distance: " + camera_features.minimum_focus_distance);
		}
		else {
			camera_features.minimum_focus_distance = 0.0f;
		}

		int [] supported_focus_modes = characteristics.get(CameraCharacteristics.CONTROL_AF_AVAILABLE_MODES); // Android format
		camera_features.supported_focus_values = convertFocusModesToValues(supported_focus_modes, camera_features.minimum_focus_distance); // convert to our format (also resorts)
		camera_features.max_num_focus_areas = characteristics.get(CameraCharacteristics.CONTROL_MAX_REGIONS_AF);

		camera_features.is_exposure_lock_supported = true;
		
        camera_features.is_video_stabilization_supported = true;

		int [] white_balance_modes = characteristics.get(CameraCharacteristics.CONTROL_AWB_AVAILABLE_MODES);
		if( white_balance_modes != null ) {
			for(int value : white_balance_modes) {
				if( value == CameraMetadata.CONTROL_AWB_MODE_OFF && allowManualWB() ) {
					camera_features.supports_white_balance_temperature = true;
					camera_features.min_temperature = min_white_balance_temperature_c;
					camera_features.max_temperature = max_white_balance_temperature_c;
				}
			}
		}

		Range<Integer> iso_range = characteristics.get(CameraCharacteristics.SENSOR_INFO_SENSITIVITY_RANGE); // may be null on some devices
		if( iso_range != null ) {
			camera_features.supports_iso_range = true;
			camera_features.min_iso = iso_range.getLower();
			camera_features.max_iso = iso_range.getUpper();
			// we only expose exposure_time if iso_range is supported
			Range<Long> exposure_time_range = characteristics.get(CameraCharacteristics.SENSOR_INFO_EXPOSURE_TIME_RANGE); // may be null on some devices
			if( exposure_time_range != null ) {
				camera_features.supports_exposure_time = true;
				camera_features.supports_expo_bracketing = true;
				camera_features.max_expo_bracketing_n_images = max_expo_bracketing_n_images;
				camera_features.min_exposure_time = exposure_time_range.getLower();
				camera_features.max_exposure_time = exposure_time_range.getUpper();
			}
		}

		Range<Integer> exposure_range = characteristics.get(CameraCharacteristics.CONTROL_AE_COMPENSATION_RANGE);
		camera_features.min_exposure = exposure_range.getLower();
		camera_features.max_exposure = exposure_range.getUpper();
		camera_features.exposure_step = characteristics.get(CameraCharacteristics.CONTROL_AE_COMPENSATION_STEP).floatValue();

		camera_features.can_disable_shutter_sound = true;

		{
			// Calculate view angles
			// Note this is an approximation (see http://stackoverflow.com/questions/39965408/what-is-the-android-camera2-api-equivalent-of-camera-parameters-gethorizontalvie ).
			// Potentially we could do better, taking into account the aspect ratio of the current resolution.
			// Note that we'd want to distinguish between the field of view of the preview versus the photo (or view) (for example,
			// DrawPreview would want the preview's field of view).
			// Also if we wanted to do this, we'd need to make sure that this was done after the caller had set the desired preview
			// and photo/video resolutions.
			SizeF physical_size = characteristics.get(CameraCharacteristics.SENSOR_INFO_PHYSICAL_SIZE);
			float [] focal_lengths = characteristics.get(CameraCharacteristics.LENS_INFO_AVAILABLE_FOCAL_LENGTHS);
			camera_features.view_angle_x = (float) Math.toDegrees(2.0 * Math.atan2(physical_size.getWidth(), (2.0 * focal_lengths[0])));
			camera_features.view_angle_y = (float) Math.toDegrees(2.0 * Math.atan2(physical_size.getHeight(), (2.0 * focal_lengths[0])));
			if( MyDebug.LOG ) {
				Log.d(TAG, "view_angle_x: " + camera_features.view_angle_x);
				Log.d(TAG, "view_angle_y: " + camera_features.view_angle_y);
			}
		}

		return camera_features;
	}

	private String convertSceneMode(int value2) {
		String value;
		switch( value2 ) {
		case CameraMetadata.CONTROL_SCENE_MODE_ACTION:
			value = "action";
			break;
		case CameraMetadata.CONTROL_SCENE_MODE_BARCODE:
			value = "barcode";
			break;
		case CameraMetadata.CONTROL_SCENE_MODE_BEACH:
			value = "beach";
			break;
		case CameraMetadata.CONTROL_SCENE_MODE_CANDLELIGHT:
			value = "candlelight";
			break;
		case CameraMetadata.CONTROL_SCENE_MODE_DISABLED:
			value = "auto";
			break;
		case CameraMetadata.CONTROL_SCENE_MODE_FIREWORKS:
			value = "fireworks";
			break;
		// "hdr" no longer available in Camera2
		/*case CameraMetadata.CONTROL_SCENE_MODE_HIGH_SPEED_VIDEO:
			// new for Camera2
			value = "high-speed-video";
			break;*/
		case CameraMetadata.CONTROL_SCENE_MODE_LANDSCAPE:
			value = "landscape";
			break;
		case CameraMetadata.CONTROL_SCENE_MODE_NIGHT:
			value = "night";
			break;
		case CameraMetadata.CONTROL_SCENE_MODE_NIGHT_PORTRAIT:
			value = "night-portrait";
			break;
		case CameraMetadata.CONTROL_SCENE_MODE_PARTY:
			value = "party";
			break;
		case CameraMetadata.CONTROL_SCENE_MODE_PORTRAIT:
			value = "portrait";
			break;
		case CameraMetadata.CONTROL_SCENE_MODE_SNOW:
			value = "snow";
			break;
		case CameraMetadata.CONTROL_SCENE_MODE_SPORTS:
			value = "sports";
			break;
		case CameraMetadata.CONTROL_SCENE_MODE_STEADYPHOTO:
			value = "steadyphoto";
			break;
		case CameraMetadata.CONTROL_SCENE_MODE_SUNSET:
			value = "sunset";
			break;
		case CameraMetadata.CONTROL_SCENE_MODE_THEATRE:
			value = "theatre";
			break;
		default:
			if( MyDebug.LOG )
				Log.d(TAG, "unknown scene mode: " + value2);
			value = null;
			break;
		}
		return value;
	}

	@Override
	public SupportedValues setSceneMode(String value) {
		if( MyDebug.LOG )
			Log.d(TAG, "setSceneMode: " + value);
		// we convert to/from strings to be compatible with original Android Camera API
		String default_value = getDefaultSceneMode();
		int [] values2 = characteristics.get(CameraCharacteristics.CONTROL_AVAILABLE_SCENE_MODES);
		boolean has_disabled = false;
		List<String> values = new ArrayList<>();
		for(int value2 : values2) {
			if( value2 == CameraMetadata.CONTROL_SCENE_MODE_DISABLED )
				has_disabled = true;
			String this_value = convertSceneMode(value2);
			if( this_value != null ) {
				values.add(this_value);
			}
		}
		if( !has_disabled ) {
			values.add(0, "auto");
		}
		SupportedValues supported_values = checkModeIsSupported(values, value, default_value);
		if( supported_values != null ) {
			int selected_value2 = CameraMetadata.CONTROL_SCENE_MODE_DISABLED;
			switch(supported_values.selected_value) {
				case "action":
					selected_value2 = CameraMetadata.CONTROL_SCENE_MODE_ACTION;
					break;
				case "barcode":
					selected_value2 = CameraMetadata.CONTROL_SCENE_MODE_BARCODE;
					break;
				case "beach":
					selected_value2 = CameraMetadata.CONTROL_SCENE_MODE_BEACH;
					break;
				case "candlelight":
					selected_value2 = CameraMetadata.CONTROL_SCENE_MODE_CANDLELIGHT;
					break;
				case "auto":
					selected_value2 = CameraMetadata.CONTROL_SCENE_MODE_DISABLED;
					break;
				case "fireworks":
					selected_value2 = CameraMetadata.CONTROL_SCENE_MODE_FIREWORKS;
					break;
				// "hdr" no longer available in Camera2
				case "landscape":
					selected_value2 = CameraMetadata.CONTROL_SCENE_MODE_LANDSCAPE;
					break;
				case "night":
					selected_value2 = CameraMetadata.CONTROL_SCENE_MODE_NIGHT;
					break;
				case "night-portrait":
					selected_value2 = CameraMetadata.CONTROL_SCENE_MODE_NIGHT_PORTRAIT;
					break;
				case "party":
					selected_value2 = CameraMetadata.CONTROL_SCENE_MODE_PARTY;
					break;
				case "portrait":
					selected_value2 = CameraMetadata.CONTROL_SCENE_MODE_PORTRAIT;
					break;
				case "snow":
					selected_value2 = CameraMetadata.CONTROL_SCENE_MODE_SNOW;
					break;
				case "sports":
					selected_value2 = CameraMetadata.CONTROL_SCENE_MODE_SPORTS;
					break;
				case "steadyphoto":
					selected_value2 = CameraMetadata.CONTROL_SCENE_MODE_STEADYPHOTO;
					break;
				case "sunset":
					selected_value2 = CameraMetadata.CONTROL_SCENE_MODE_SUNSET;
					break;
				case "theatre":
					selected_value2 = CameraMetadata.CONTROL_SCENE_MODE_THEATRE;
					break;
				default:
					if (MyDebug.LOG)
						Log.d(TAG, "unknown selected_value: " + supported_values.selected_value);
					break;
			}

			camera_settings.scene_mode = selected_value2;
			if( camera_settings.setSceneMode(previewBuilder) ) {
				try {
					setRepeatingRequest();
				}
				catch(CameraAccessException e) {
					if( MyDebug.LOG ) {
						Log.e(TAG, "failed to set scene mode");
						Log.e(TAG, "reason: " + e.getReason());
						Log.e(TAG, "message: " + e.getMessage());
					}
					e.printStackTrace();
				} 
			}
		}
		return supported_values;
	}
	
	@Override
	public String getSceneMode() {
		if( previewBuilder.get(CaptureRequest.CONTROL_SCENE_MODE) == null )
			return null;
		int value2 = previewBuilder.get(CaptureRequest.CONTROL_SCENE_MODE);
		return convertSceneMode(value2);
	}

	private String convertColorEffect(int value2) {
		String value;
		switch( value2 ) {
		case CameraMetadata.CONTROL_EFFECT_MODE_AQUA:
			value = "aqua";
			break;
		case CameraMetadata.CONTROL_EFFECT_MODE_BLACKBOARD:
			value = "blackboard";
			break;
		case CameraMetadata.CONTROL_EFFECT_MODE_MONO:
			value = "mono";
			break;
		case CameraMetadata.CONTROL_EFFECT_MODE_NEGATIVE:
			value = "negative";
			break;
		case CameraMetadata.CONTROL_EFFECT_MODE_OFF:
			value = "none";
			break;
		case CameraMetadata.CONTROL_EFFECT_MODE_POSTERIZE:
			value = "posterize";
			break;
		case CameraMetadata.CONTROL_EFFECT_MODE_SEPIA:
			value = "sepia";
			break;
		case CameraMetadata.CONTROL_EFFECT_MODE_SOLARIZE:
			value = "solarize";
			break;
		case CameraMetadata.CONTROL_EFFECT_MODE_WHITEBOARD:
			value = "whiteboard";
			break;
		default:
			if( MyDebug.LOG )
				Log.d(TAG, "unknown effect mode: " + value2);
			value = null;
			break;
		}
		return value;
	}

	@Override
	public SupportedValues setColorEffect(String value) {
		if( MyDebug.LOG )
			Log.d(TAG, "setColorEffect: " + value);
		// we convert to/from strings to be compatible with original Android Camera API
		String default_value = getDefaultColorEffect();
		int [] values2 = characteristics.get(CameraCharacteristics.CONTROL_AVAILABLE_EFFECTS);
		List<String> values = new ArrayList<>();
		for(int value2 : values2) {
			String this_value = convertColorEffect(value2);
			if( this_value != null ) {
				values.add(this_value);
			}
		}
		SupportedValues supported_values = checkModeIsSupported(values, value, default_value);
		if( supported_values != null ) {
			int selected_value2 = CameraMetadata.CONTROL_EFFECT_MODE_OFF;
			switch(supported_values.selected_value) {
				case "aqua":
					selected_value2 = CameraMetadata.CONTROL_EFFECT_MODE_AQUA;
					break;
				case "blackboard":
					selected_value2 = CameraMetadata.CONTROL_EFFECT_MODE_BLACKBOARD;
					break;
				case "mono":
					selected_value2 = CameraMetadata.CONTROL_EFFECT_MODE_MONO;
					break;
				case "negative":
					selected_value2 = CameraMetadata.CONTROL_EFFECT_MODE_NEGATIVE;
					break;
				case "none":
					selected_value2 = CameraMetadata.CONTROL_EFFECT_MODE_OFF;
					break;
				case "posterize":
					selected_value2 = CameraMetadata.CONTROL_EFFECT_MODE_POSTERIZE;
					break;
				case "sepia":
					selected_value2 = CameraMetadata.CONTROL_EFFECT_MODE_SEPIA;
					break;
				case "solarize":
					selected_value2 = CameraMetadata.CONTROL_EFFECT_MODE_SOLARIZE;
					break;
				case "whiteboard":
					selected_value2 = CameraMetadata.CONTROL_EFFECT_MODE_WHITEBOARD;
					break;
				default:
					if (MyDebug.LOG)
						Log.d(TAG, "unknown selected_value: " + supported_values.selected_value);
					break;
			}

			camera_settings.color_effect = selected_value2;
			if( camera_settings.setColorEffect(previewBuilder) ) {
				try {
					setRepeatingRequest();
				}
				catch(CameraAccessException e) {
					if( MyDebug.LOG ) {
						Log.e(TAG, "failed to set color effect");
						Log.e(TAG, "reason: " + e.getReason());
						Log.e(TAG, "message: " + e.getMessage());
					}
					e.printStackTrace();
				} 
			}
		}
		return supported_values;
	}

	@Override
	public String getColorEffect() {
		if( previewBuilder.get(CaptureRequest.CONTROL_EFFECT_MODE) == null )
			return null;
		int value2 = previewBuilder.get(CaptureRequest.CONTROL_EFFECT_MODE);
		return convertColorEffect(value2);
	}

	private String convertWhiteBalance(int value2) {
		String value;
		switch( value2 ) {
		case CameraMetadata.CONTROL_AWB_MODE_AUTO:
			value = "auto";
			break;
		case CameraMetadata.CONTROL_AWB_MODE_CLOUDY_DAYLIGHT:
			value = "cloudy-daylight";
			break;
		case CameraMetadata.CONTROL_AWB_MODE_DAYLIGHT:
			value = "daylight";
			break;
		case CameraMetadata.CONTROL_AWB_MODE_FLUORESCENT:
			value = "fluorescent";
			break;
		case CameraMetadata.CONTROL_AWB_MODE_INCANDESCENT:
			value = "incandescent";
			break;
		case CameraMetadata.CONTROL_AWB_MODE_SHADE:
			value = "shade";
			break;
		case CameraMetadata.CONTROL_AWB_MODE_TWILIGHT:
			value = "twilight";
			break;
		case CameraMetadata.CONTROL_AWB_MODE_WARM_FLUORESCENT:
			value = "warm-fluorescent";
			break;
		case CameraMetadata.CONTROL_AWB_MODE_OFF:
			value = "manual";
			break;
		default:
			if( MyDebug.LOG )
				Log.d(TAG, "unknown white balance: " + value2);
			value = null;
			break;
		}
		return value;
	}

	/** Whether we should allow manual white balance, even if the device supports CONTROL_AWB_MODE_OFF.
	 */
	private boolean allowManualWB() {
		boolean is_nexus6 = Build.MODEL.toLowerCase(Locale.US).contains("nexus 6");
		// manual white balance doesn't seem to work on Nexus 6!
		return !is_nexus6;
	}

	@Override
	public SupportedValues setWhiteBalance(String value) {
		if( MyDebug.LOG )
			Log.d(TAG, "setWhiteBalance: " + value);
		// we convert to/from strings to be compatible with original Android Camera API
		String default_value = getDefaultWhiteBalance();
		int [] values2 = characteristics.get(CameraCharacteristics.CONTROL_AWB_AVAILABLE_MODES);
		List<String> values = new ArrayList<>();
		for(int value2 : values2) {
			String this_value = convertWhiteBalance(value2);
			if( this_value != null ) {
				if( value2 == CameraMetadata.CONTROL_AWB_MODE_OFF && !allowManualWB() ) {
					// filter
				}
				else {
					values.add(this_value);
				}
			}
		}
		{
			// re-order so that auto is first, manual is second
			boolean has_auto = values.remove("auto");
			boolean has_manual = values.remove("manual");
			if( has_manual )
				values.add(0, "manual");
			if( has_auto )
				values.add(0, "auto");
		}
		SupportedValues supported_values = checkModeIsSupported(values, value, default_value);
		if( supported_values != null ) {
			int selected_value2 = CameraMetadata.CONTROL_AWB_MODE_AUTO;
			switch(supported_values.selected_value) {
				case "auto":
					selected_value2 = CameraMetadata.CONTROL_AWB_MODE_AUTO;
					break;
				case "cloudy-daylight":
					selected_value2 = CameraMetadata.CONTROL_AWB_MODE_CLOUDY_DAYLIGHT;
					break;
				case "daylight":
					selected_value2 = CameraMetadata.CONTROL_AWB_MODE_DAYLIGHT;
					break;
				case "fluorescent":
					selected_value2 = CameraMetadata.CONTROL_AWB_MODE_FLUORESCENT;
					break;
				case "incandescent":
					selected_value2 = CameraMetadata.CONTROL_AWB_MODE_INCANDESCENT;
					break;
				case "shade":
					selected_value2 = CameraMetadata.CONTROL_AWB_MODE_SHADE;
					break;
				case "twilight":
					selected_value2 = CameraMetadata.CONTROL_AWB_MODE_TWILIGHT;
					break;
				case "warm-fluorescent":
					selected_value2 = CameraMetadata.CONTROL_AWB_MODE_WARM_FLUORESCENT;
					break;
				case "manual":
					selected_value2 = CameraMetadata.CONTROL_AWB_MODE_OFF;
					break;
				default:
					if (MyDebug.LOG)
						Log.d(TAG, "unknown selected_value: " + supported_values.selected_value);
					break;
			}

			camera_settings.white_balance = selected_value2;
			if( camera_settings.setWhiteBalance(previewBuilder) ) {
				try {
					setRepeatingRequest();
				}
				catch(CameraAccessException e) {
					if( MyDebug.LOG ) {
						Log.e(TAG, "failed to set white balance");
						Log.e(TAG, "reason: " + e.getReason());
						Log.e(TAG, "message: " + e.getMessage());
					}
					e.printStackTrace();
				} 
			}
		}
		return supported_values;
	}

	@Override
	public String getWhiteBalance() {
		if( previewBuilder.get(CaptureRequest.CONTROL_AWB_MODE) == null )
			return null;
		int value2 = previewBuilder.get(CaptureRequest.CONTROL_AWB_MODE);
		return convertWhiteBalance(value2);
	}

	@Override
	// Returns whether white balance temperature was modified
	public boolean setWhiteBalanceTemperature(int temperature) {
		if( MyDebug.LOG )
			Log.d(TAG, "setWhiteBalanceTemperature: " + temperature);
		if( camera_settings.white_balance == temperature ) {
			if( MyDebug.LOG )
				Log.d(TAG, "already set");
			return false;
		}
		try {
			temperature = Math.max(temperature, min_white_balance_temperature_c);
			temperature = Math.min(temperature, max_white_balance_temperature_c);
			camera_settings.white_balance_temperature = temperature;
			if( camera_settings.setWhiteBalance(previewBuilder) ) {
				setRepeatingRequest();
			}
		}
		catch(CameraAccessException e) {
			if( MyDebug.LOG ) {
				Log.e(TAG, "failed to set white balance temperature");
				Log.e(TAG, "reason: " + e.getReason());
				Log.e(TAG, "message: " + e.getMessage());
			}
			e.printStackTrace();
		}
		return true;
	}

	@Override
	public int getWhiteBalanceTemperature() {
		return camera_settings.white_balance_temperature;
	}

	@Override
	public SupportedValues setISO(String value) {
		// not supported for CameraController2 - but Camera2 devices that don't support manual ISO can call this,
		// so assume this is for auto ISO
		this.setManualISO(false, 0);
		return null;
	}

	@Override
	public String getISOKey() {
		return "";
	}

	@Override
	public void setManualISO(boolean manual_iso, int iso) {
		if( MyDebug.LOG )
			Log.d(TAG, "setManualISO" + manual_iso);
		try {
			if( manual_iso ) {
				if( MyDebug.LOG )
					Log.d(TAG, "switch to iso: " + iso);
				Range<Integer> iso_range = characteristics.get(CameraCharacteristics.SENSOR_INFO_SENSITIVITY_RANGE); // may be null on some devices
				if( iso_range == null ) {
					if( MyDebug.LOG )
						Log.d(TAG, "iso not supported");
					return;
				}
				if( MyDebug.LOG )
					Log.d(TAG, "iso range from " + iso_range.getLower() + " to " + iso_range.getUpper());

				camera_settings.has_iso = true;
				iso = Math.max(iso, iso_range.getLower());
				iso = Math.min(iso, iso_range.getUpper());
				camera_settings.iso = iso;
			}
			else {
				camera_settings.has_iso = false;
				camera_settings.iso = 0;
			}

			if( camera_settings.setAEMode(previewBuilder, false) ) {
				setRepeatingRequest();
			}
		}
		catch(CameraAccessException e) {
			if( MyDebug.LOG ) {
				Log.e(TAG, "failed to set ISO");
				Log.e(TAG, "reason: " + e.getReason());
				Log.e(TAG, "message: " + e.getMessage());
			}
			e.printStackTrace();
		}
	}

	@Override
	public boolean isManualISO() {
		return camera_settings.has_iso;
	}

	@Override
	// Returns whether ISO was modified
	// N.B., use setManualISO() to switch between auto and manual mode
	public boolean setISO(int iso) {
		if( MyDebug.LOG )
			Log.d(TAG, "setISO: " + iso);
		if( camera_settings.iso == iso ) {
			if( MyDebug.LOG )
				Log.d(TAG, "already set");
			return false;
		}
		try {
			camera_settings.iso = iso;
			if( camera_settings.setAEMode(previewBuilder, false) ) {
				setRepeatingRequest();
			}
		}
		catch(CameraAccessException e) {
			if( MyDebug.LOG ) {
				Log.e(TAG, "failed to set ISO");
				Log.e(TAG, "reason: " + e.getReason());
				Log.e(TAG, "message: " + e.getMessage());
			}
			e.printStackTrace();
		}
		return true;
	}

	@Override
	public int getISO() {
		return camera_settings.iso;
	}

	@Override
	public long getExposureTime() {
		return camera_settings.exposure_time;
	}

	@Override
	// Returns whether exposure time was modified
	// N.B., use setISO(String) to switch between auto and manual mode
	public boolean setExposureTime(long exposure_time) {
		if( MyDebug.LOG ) {
			Log.d(TAG, "setExposureTime: " + exposure_time);
			Log.d(TAG, "current exposure time: " + camera_settings.exposure_time);
		}
		if( camera_settings.exposure_time == exposure_time ) {
			if( MyDebug.LOG )
				Log.d(TAG, "already set");
			return false;
		}
		try {
			camera_settings.exposure_time = exposure_time;
			if( camera_settings.setAEMode(previewBuilder, false) ) {
		    	setRepeatingRequest();
			}
		}
		catch(CameraAccessException e) {
			if( MyDebug.LOG ) {
				Log.e(TAG, "failed to set exposure time");
				Log.e(TAG, "reason: " + e.getReason());
				Log.e(TAG, "message: " + e.getMessage());
			}
			e.printStackTrace();
		} 
		return true;
	}

	@Override
	public Size getPictureSize() {
		return new Size(picture_width, picture_height);
	}

	@Override
	public void setPictureSize(int width, int height) {
		if( MyDebug.LOG )
			Log.d(TAG, "setPictureSize: " + width + " x " + height);
		if( camera == null ) {
			if( MyDebug.LOG )
				Log.e(TAG, "no camera");
			return;
		}
		if( captureSession != null ) {
			// can only call this when captureSession not created - as the surface of the imageReader we create has to match the surface we pass to the captureSession
			if( MyDebug.LOG )
				Log.e(TAG, "can't set picture size when captureSession running!");
			throw new RuntimeException(); // throw as RuntimeException, as this is a programming error
		}
		this.picture_width = width;
		this.picture_height = height;
	}

	@Override
	public void setRaw(boolean want_raw) {
		if( MyDebug.LOG )
			Log.d(TAG, "setRaw: " + want_raw);
		if( camera == null ) {
			if( MyDebug.LOG )
				Log.e(TAG, "no camera");
			return;
		}
		if( this.want_raw == want_raw ) {
			return;
		}
		if( want_raw && this.raw_size == null ) {
			if( MyDebug.LOG )
				Log.e(TAG, "can't set raw when raw not supported");
			return;
		}
		if( captureSession != null ) {
			// can only call this when captureSession not created - as it affects how we create the imageReader
			if( MyDebug.LOG )
				Log.e(TAG, "can't set raw when captureSession running!");
			throw new RuntimeException(); // throw as RuntimeException, as this is a programming error
		}
		this.want_raw = want_raw;
	}

	@Override
	public void setExpoBracketing(boolean want_expo_bracketing) {
		if( MyDebug.LOG )
			Log.d(TAG, "setExpoBracketing: " + want_expo_bracketing);
		if( camera == null ) {
			if( MyDebug.LOG )
				Log.e(TAG, "no camera");
			return;
		}
		if( this.want_expo_bracketing == want_expo_bracketing ) {
			return;
		}
		if( captureSession != null ) {
			// can only call this when captureSession not created - as it affects how we create the imageReader
			if( MyDebug.LOG )
				Log.e(TAG, "can't set hdr when captureSession running!");
			throw new RuntimeException(); // throw as RuntimeException, as this is a programming error
		}
		this.want_expo_bracketing = want_expo_bracketing;
		updateUseFakePrecaptureMode(camera_settings.flash_value);
		camera_settings.setAEMode(previewBuilder, false); // need to set the ae mode, as flash is disabled for HDR mode
	}

	@Override
	public void setExpoBracketingNImages(int n_images) {
		if( MyDebug.LOG )
			Log.d(TAG, "setExpoBracketingNImages: " + n_images);
		if( n_images <= 1 || (n_images % 2) == 0 ) {
			if( MyDebug.LOG )
				Log.e(TAG, "n_images should be an odd number greater than 1");
			throw new RuntimeException(); // throw as RuntimeException, as this is a programming error
		}
		if( n_images > max_expo_bracketing_n_images ) {
			n_images = max_expo_bracketing_n_images;
			if( MyDebug.LOG )
				Log.e(TAG, "limiting n_images to max of " + n_images);
		}
		this.expo_bracketing_n_images = n_images;
	}

	@Override
	public void setExpoBracketingStops(double stops) {
		if( MyDebug.LOG )
			Log.d(TAG, "setExpoBracketingStops: " + stops);
		if( stops <= 0.0 ) {
			if( MyDebug.LOG )
				Log.e(TAG, "stops should be positive");
			throw new RuntimeException(); // throw as RuntimeException, as this is a programming error
		}
		this.expo_bracketing_stops = stops;
	}

	@Override
	public void setUseExpoFastBurst(boolean use_expo_fast_burst) {
		if( MyDebug.LOG )
			Log.d(TAG, "setUseExpoFastBurst: " + use_expo_fast_burst);
		this.use_expo_fast_burst = use_expo_fast_burst;
	}

	@Override
	public void setOptimiseAEForDRO(boolean optimise_ae_for_dro) {
		if( MyDebug.LOG )
			Log.d(TAG, "setOptimiseAEForDRO: " + optimise_ae_for_dro);
		boolean is_oneplus = Build.MANUFACTURER.toLowerCase(Locale.US).contains("oneplus");
		if( is_oneplus ) {
			// OnePlus 3T has preview corruption / camera freezing problems when using manual shutter speeds
			// So best not to modify auto-exposure for DRO
			this.optimise_ae_for_dro = false;
			if( MyDebug.LOG )
				Log.d(TAG, "don't modify ae for OnePlus");
		}
		else {
			this.optimise_ae_for_dro = optimise_ae_for_dro;
		}
	}

	@Override
	public void setUseCamera2FakeFlash(boolean use_fake_precapture) {
		if( MyDebug.LOG )
			Log.d(TAG, "setUseCamera2FakeFlash: " + use_fake_precapture);
		if( camera == null ) {
			if( MyDebug.LOG )
				Log.e(TAG, "no camera");
			return;
		}
		if( this.use_fake_precapture == use_fake_precapture ) {
			return;
		}
		this.use_fake_precapture = use_fake_precapture;
		this.use_fake_precapture_mode = use_fake_precapture;
		// no need to call updateUseFakePrecaptureMode(), as this method should only be called after first creating camera controller
	}
	
	@Override
	public boolean getUseCamera2FakeFlash() {
		return this.use_fake_precapture;
	}

	private void createPictureImageReader() {
		if( MyDebug.LOG )
			Log.d(TAG, "createPictureImageReader");
		if( captureSession != null ) {
			// can only call this when captureSession not created - as the surface of the imageReader we create has to match the surface we pass to the captureSession
			if( MyDebug.LOG )
				Log.e(TAG, "can't create picture image reader when captureSession running!");
			throw new RuntimeException(); // throw as RuntimeException, as this is a programming error
		}
		closePictureImageReader();
		if( picture_width == 0 || picture_height == 0 ) {
			if( MyDebug.LOG )
				Log.e(TAG, "application needs to call setPictureSize()");
			throw new RuntimeException(); // throw as RuntimeException, as this is a programming error
		}
		imageReader = ImageReader.newInstance(picture_width, picture_height, ImageFormat.JPEG, 2);
		if( MyDebug.LOG ) {
			Log.d(TAG, "created new imageReader: " + imageReader.toString());
			Log.d(TAG, "imageReader surface: " + imageReader.getSurface().toString());
		}
		imageReader.setOnImageAvailableListener(new ImageReader.OnImageAvailableListener() {
			@Override
			public void onImageAvailable(ImageReader reader) {
				if( MyDebug.LOG )
					Log.d(TAG, "new still image available");
				if( jpeg_cb == null ) {
					if( MyDebug.LOG )
						Log.d(TAG, "no picture callback available");
					return;
				}
				synchronized( image_reader_lock ) {
					/* Whilst in theory the two setOnImageAvailableListener methods (for JPEG and RAW) seem to be called separately, I don't know if this is always true;
					 * also, we may process the RAW image when the capture result is available (see
					 * OnRawImageAvailableListener.setCaptureResult()), which may be in a separate thread.
					 */
					Image image = reader.acquireNextImage();
					if( MyDebug.LOG )
						Log.d(TAG, "image timestamp: " + image.getTimestamp());
		            ByteBuffer buffer = image.getPlanes()[0].getBuffer();
		            byte [] bytes = new byte[buffer.remaining()]; 
					if( MyDebug.LOG )
						Log.d(TAG, "read " + bytes.length + " bytes");
		            buffer.get(bytes);
		            image.close();
		            if( want_expo_bracketing && n_burst > 1 ) {
		            	pending_burst_images.add(bytes);
		            	if( pending_burst_images.size() >= n_burst ) { // shouldn't ever be greater, but just in case
							if( MyDebug.LOG )
								Log.d(TAG, "all burst images available");
							if( pending_burst_images.size() > n_burst ) {
								Log.e(TAG, "pending_burst_images size " + pending_burst_images.size() + " is greater than n_burst " + n_burst);
							}
				            // need to set jpeg_cb etc to null before calling onCompleted, as that may reenter CameraController to take another photo (if in burst mode) - see testTakePhotoBurst()
				            PictureCallback cb = jpeg_cb;
				            jpeg_cb = null;
				            // take a copy, so that we can clear pending_burst_images
				            List<byte []> images = new ArrayList<>(pending_burst_images);
				            cb.onBurstPictureTaken(images);
				            pending_burst_images.clear();
							cb.onCompleted();
		            	}
		            	else {
							if( MyDebug.LOG )
								Log.d(TAG, "number of burst images is now: " + pending_burst_images.size());
							if( burst_capture_requests != null ) {
								if( MyDebug.LOG ) {
									Log.d(TAG, "need to execute the next capture");
									Log.d(TAG, "time since start: " + (System.currentTimeMillis() - burst_start_ms));
								}
								try {
									captureSession.capture(burst_capture_requests.get(pending_burst_images.size()), previewCaptureCallback, handler);
								}
								catch(CameraAccessException e) {
									if( MyDebug.LOG ) {
										Log.e(TAG, "failed to take next burst");
										Log.e(TAG, "reason: " + e.getReason());
										Log.e(TAG, "message: " + e.getMessage());
									}
									e.printStackTrace();
									jpeg_cb = null;
									if( take_picture_error_cb != null ) {
										take_picture_error_cb.onError();
										take_picture_error_cb = null;
									}
								}
							}
		            	}
		            }
		            else {
			            // need to set jpeg_cb etc to null before calling onCompleted, as that may reenter CameraController to take another photo (if in burst mode) - see testTakePhotoBurst()
			            PictureCallback cb = jpeg_cb;
			            jpeg_cb = null;
			            cb.onPictureTaken(bytes);
			            if( raw_cb == null ) {
							if( MyDebug.LOG )
								Log.d(TAG, "all image callbacks now completed");
							cb.onCompleted();
			            }
			            else if( pending_dngCreator != null ) {
							if( MyDebug.LOG )
								Log.d(TAG, "can now call pending raw callback");
		    				takePendingRaw();
							if( MyDebug.LOG )
								Log.d(TAG, "all image callbacks now completed");
							cb.onCompleted();
			            }
		            }
				}
				if( MyDebug.LOG )
					Log.d(TAG, "done onImageAvailable");
			}
		}, null);
		if( want_raw && raw_size != null ) {
			imageReaderRaw = ImageReader.newInstance(raw_size.getWidth(), raw_size.getHeight(), ImageFormat.RAW_SENSOR, 2);
			if( MyDebug.LOG ) {
				Log.d(TAG, "created new imageReaderRaw: " + imageReaderRaw.toString());
				Log.d(TAG, "imageReaderRaw surface: " + imageReaderRaw.getSurface().toString());
			}
			imageReaderRaw.setOnImageAvailableListener(onRawImageAvailableListener = new OnRawImageAvailableListener(), null);
		}
	}
	
	private void clearPending() {
		if( MyDebug.LOG )
			Log.d(TAG, "clearPending");
		pending_burst_images.clear();
		pending_dngCreator = null;
		pending_image = null;
		if( onRawImageAvailableListener != null ) {
			onRawImageAvailableListener.clear();
		}
		burst_capture_requests = null;
		n_burst = 0;
		burst_start_ms = 0;
	}
	
	private void takePendingRaw() {
		if( MyDebug.LOG )
			Log.d(TAG, "takePendingRaw");
		if( pending_dngCreator != null ) {
            PictureCallback cb = raw_cb;
            raw_cb = null;
            cb.onRawPictureTaken(pending_dngCreator, pending_image);
            // image and dngCreator should be closed by the application (we don't do it here, so that applications can keep hold of the data, e.g., in a queue for background processing)
            pending_dngCreator = null;
            pending_image = null;
			if( onRawImageAvailableListener != null ) {
				onRawImageAvailableListener.clear();
			}
		}
	}
	
	@Override
	public Size getPreviewSize() {
		return new Size(preview_width, preview_height);
	}

	@Override
	public void setPreviewSize(int width, int height) {
		if( MyDebug.LOG )
			Log.d(TAG, "setPreviewSize: " + width + " , " + height);
		/*if( texture != null ) {
			if( MyDebug.LOG )
				Log.d(TAG, "set size of preview texture");
			texture.setDefaultBufferSize(width, height);
		}*/
		preview_width = width;
		preview_height = height;
		/*if( previewImageReader != null ) {
			previewImageReader.close();
		}
		previewImageReader = ImageReader.newInstance(width, height, ImageFormat.YUV_420_888, 2); 
		*/
	}

	@Override
	public void setVideoStabilization(boolean enabled) {
		camera_settings.video_stabilization = enabled;
		camera_settings.setVideoStabilization(previewBuilder);
		try {
			setRepeatingRequest();
		}
		catch(CameraAccessException e) {
			if( MyDebug.LOG ) {
				Log.e(TAG, "failed to set video stabilization");
				Log.e(TAG, "reason: " + e.getReason());
				Log.e(TAG, "message: " + e.getMessage());
			}
			e.printStackTrace();
		} 
	}

	@Override
	public boolean getVideoStabilization() {
		return camera_settings.video_stabilization;
	}

	@Override
	public int getJpegQuality() {
		return this.camera_settings.jpeg_quality;
	}

	@Override
	public void setJpegQuality(int quality) {
		if( quality < 0 || quality > 100 ) {
			if( MyDebug.LOG )
				Log.e(TAG, "invalid jpeg quality" + quality);
			throw new RuntimeException(); // throw as RuntimeException, as this is a programming error
		}
		this.camera_settings.jpeg_quality = (byte)quality;
	}

	@Override
	public int getZoom() {
		return this.current_zoom_value;
	}

	@Override
	public void setZoom(int value) {
		if( zoom_ratios == null ) {
			if( MyDebug.LOG )
				Log.d(TAG, "zoom not supported");
			return;
		}
		if( value < 0 || value > zoom_ratios.size() ) {
			if( MyDebug.LOG )
				Log.e(TAG, "invalid zoom value" + value);
			throw new RuntimeException(); // throw as RuntimeException, as this is a programming error
		}
		float zoom = zoom_ratios.get(value)/100.0f;
		Rect sensor_rect = characteristics.get(CameraCharacteristics.SENSOR_INFO_ACTIVE_ARRAY_SIZE);
		int left = sensor_rect.width()/2;
		int right = left;
		int top = sensor_rect.height()/2;
		int bottom = top;
		int hwidth = (int)(sensor_rect.width() / (2.0*zoom));
		int hheight = (int)(sensor_rect.height() / (2.0*zoom));
		left -= hwidth;
		right += hwidth;
		top -= hheight;
		bottom += hheight;
		if( MyDebug.LOG ) {
			Log.d(TAG, "zoom: " + zoom);
			Log.d(TAG, "hwidth: " + hwidth);
			Log.d(TAG, "hheight: " + hheight);
			Log.d(TAG, "sensor_rect left: " + sensor_rect.left);
			Log.d(TAG, "sensor_rect top: " + sensor_rect.top);
			Log.d(TAG, "sensor_rect right: " + sensor_rect.right);
			Log.d(TAG, "sensor_rect bottom: " + sensor_rect.bottom);
			Log.d(TAG, "left: " + left);
			Log.d(TAG, "top: " + top);
			Log.d(TAG, "right: " + right);
			Log.d(TAG, "bottom: " + bottom);
			/*Rect current_rect = previewBuilder.get(CaptureRequest.SCALER_CROP_REGION);
			Log.d(TAG, "current_rect left: " + current_rect.left);
			Log.d(TAG, "current_rect top: " + current_rect.top);
			Log.d(TAG, "current_rect right: " + current_rect.right);
			Log.d(TAG, "current_rect bottom: " + current_rect.bottom);*/
		}
		camera_settings.scalar_crop_region = new Rect(left, top, right, bottom);
		camera_settings.setCropRegion(previewBuilder);
    	this.current_zoom_value = value;
    	try {
    		setRepeatingRequest();
    	}
		catch(CameraAccessException e) {
			if( MyDebug.LOG ) {
				Log.e(TAG, "failed to set zoom");
				Log.e(TAG, "reason: " + e.getReason());
				Log.e(TAG, "message: " + e.getMessage());
			}
			e.printStackTrace();
		} 
	}
	
	@Override
	public int getExposureCompensation() {
		if( previewBuilder.get(CaptureRequest.CONTROL_AE_EXPOSURE_COMPENSATION) == null )
			return 0;
		return previewBuilder.get(CaptureRequest.CONTROL_AE_EXPOSURE_COMPENSATION);
	}

	@Override
	// Returns whether exposure was modified
	public boolean setExposureCompensation(int new_exposure) {
		camera_settings.has_ae_exposure_compensation = true;
		camera_settings.ae_exposure_compensation = new_exposure;
		if( camera_settings.setExposureCompensation(previewBuilder) ) {
			try {
				setRepeatingRequest();
			}
			catch(CameraAccessException e) {
				if( MyDebug.LOG ) {
					Log.e(TAG, "failed to set exposure compensation");
					Log.e(TAG, "reason: " + e.getReason());
					Log.e(TAG, "message: " + e.getMessage());
				}
				e.printStackTrace();
			} 
        	return true;
		}
		return false;
	}
	
	@Override
	public void setPreviewFpsRange(int min, int max) {
		// TODO Auto-generated method stub

	}

	@Override
	public List<int[]> getSupportedPreviewFpsRange() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setFocusValue(String focus_value) {
		if( MyDebug.LOG )
			Log.d(TAG, "setFocusValue: " + focus_value);
		int focus_mode;
		switch(focus_value) {
			case "focus_mode_auto":
			case "focus_mode_locked":
				focus_mode = CaptureRequest.CONTROL_AF_MODE_AUTO;
				break;
			case "focus_mode_infinity":
				focus_mode = CaptureRequest.CONTROL_AF_MODE_OFF;
				camera_settings.focus_distance = 0.0f;
				break;
			case "focus_mode_manual2":
				focus_mode = CaptureRequest.CONTROL_AF_MODE_OFF;
				camera_settings.focus_distance = camera_settings.focus_distance_manual;
				break;
			case "focus_mode_macro":
				focus_mode = CaptureRequest.CONTROL_AF_MODE_MACRO;
				break;
			case "focus_mode_edof":
				focus_mode = CaptureRequest.CONTROL_AF_MODE_EDOF;
				break;
			case "focus_mode_continuous_picture":
				focus_mode = CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE;
				break;
			case "focus_mode_continuous_video":
				focus_mode = CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_VIDEO;
				break;
			default:
				if (MyDebug.LOG)
					Log.d(TAG, "setFocusValue() received unknown focus value " + focus_value);
				return;
		}
    	camera_settings.has_af_mode = true;
    	camera_settings.af_mode = focus_mode;
    	camera_settings.setFocusMode(previewBuilder);
    	camera_settings.setFocusDistance(previewBuilder); // also need to set distance, in case changed between infinity, manual or other modes
    	try {
    		setRepeatingRequest();
    	}
		catch(CameraAccessException e) {
			if( MyDebug.LOG ) {
				Log.e(TAG, "failed to set focus mode");
				Log.e(TAG, "reason: " + e.getReason());
				Log.e(TAG, "message: " + e.getMessage());
			}
			e.printStackTrace();
		} 
	}
	
	private String convertFocusModeToValue(int focus_mode) {
		if( MyDebug.LOG )
			Log.d(TAG, "convertFocusModeToValue: " + focus_mode);
		String focus_value = "";
		if( focus_mode == CaptureRequest.CONTROL_AF_MODE_AUTO ) {
    		focus_value = "focus_mode_auto";
    	}
		else if( focus_mode == CaptureRequest.CONTROL_AF_MODE_MACRO ) {
    		focus_value = "focus_mode_macro";
    	}
		else if( focus_mode == CaptureRequest.CONTROL_AF_MODE_EDOF ) {
    		focus_value = "focus_mode_edof";
    	}
		else if( focus_mode == CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE ) {
    		focus_value = "focus_mode_continuous_picture";
    	}
		else if( focus_mode == CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_VIDEO ) {
    		focus_value = "focus_mode_continuous_video";
    	}
		else if( focus_mode == CaptureRequest.CONTROL_AF_MODE_OFF ) {
    		focus_value = "focus_mode_manual2"; // n.b., could be infinity
		}
    	return focus_value;
	}
	
	@Override
	public String getFocusValue() {
		int focus_mode = previewBuilder.get(CaptureRequest.CONTROL_AF_MODE) != null ?
				previewBuilder.get(CaptureRequest.CONTROL_AF_MODE) : CaptureRequest.CONTROL_AF_MODE_AUTO;
		return convertFocusModeToValue(focus_mode);
	}

	@Override
	public float getFocusDistance() {
		return camera_settings.focus_distance;
	}

	@Override
	public boolean setFocusDistance(float focus_distance) {
		if( MyDebug.LOG )
			Log.d(TAG, "setFocusDistance: " + focus_distance);
		if( camera_settings.focus_distance == focus_distance ) {
			if( MyDebug.LOG )
				Log.d(TAG, "already set");
			return false;
		}
    	camera_settings.focus_distance = focus_distance;
    	camera_settings.focus_distance_manual = focus_distance;
    	camera_settings.setFocusDistance(previewBuilder);
    	try {
    		setRepeatingRequest();
    	}
		catch(CameraAccessException e) {
			if( MyDebug.LOG ) {
				Log.e(TAG, "failed to set focus distance");
				Log.e(TAG, "reason: " + e.getReason());
				Log.e(TAG, "message: " + e.getMessage());
			}
			e.printStackTrace();
		} 
    	return true;
	}

	/** Decides whether we should be using fake precapture mode.
	 */
	private void updateUseFakePrecaptureMode(String flash_value) {
		if( MyDebug.LOG )
			Log.d(TAG, "useFakePrecaptureMode: " + flash_value);
		boolean frontscreen_flash = flash_value.equals("flash_frontscreen_auto") || flash_value.equals("flash_frontscreen_on");
    	if( frontscreen_flash ) {
    		use_fake_precapture_mode = true;
    	}
    	else if( this.want_expo_bracketing )
    		use_fake_precapture_mode = true;
    	else {
    		use_fake_precapture_mode = use_fake_precapture;
    	}
		if( MyDebug.LOG )
			Log.d(TAG, "use_fake_precapture_mode set to: " + use_fake_precapture_mode);
	}

	@Override
	public void setFlashValue(String flash_value) {
		if( MyDebug.LOG )
			Log.d(TAG, "setFlashValue: " + flash_value);
		if( camera_settings.flash_value.equals(flash_value) ) {
			if( MyDebug.LOG )
				Log.d(TAG, "flash value already set");
			return;
		}

		try {
			updateUseFakePrecaptureMode(flash_value);
			
			if( camera_settings.flash_value.equals("flash_torch") && !flash_value.equals("flash_off") ) {
				// hack - if switching to something other than flash_off, we first need to turn torch off, otherwise torch remains on (at least on Nexus 6)
				camera_settings.flash_value = "flash_off";
				camera_settings.setAEMode(previewBuilder, false);
				CaptureRequest request = previewBuilder.build();
	
				// need to wait until torch actually turned off
		    	camera_settings.flash_value = flash_value;
				camera_settings.setAEMode(previewBuilder, false);
				push_repeating_request_when_torch_off = true;
				push_repeating_request_when_torch_off_id = request;
	
				setRepeatingRequest(request);
			}
			else {
				camera_settings.flash_value = flash_value;
				if( camera_settings.setAEMode(previewBuilder, false) ) {
			    	setRepeatingRequest();
				}
			}
		}
		catch(CameraAccessException e) {
			if( MyDebug.LOG ) {
				Log.e(TAG, "failed to set flash mode");
				Log.e(TAG, "reason: " + e.getReason());
				Log.e(TAG, "message: " + e.getMessage());
			}
			e.printStackTrace();
		} 
	}

	@Override
	public String getFlashValue() {
		// returns "" if flash isn't supported
		if( !characteristics.get(CameraCharacteristics.FLASH_INFO_AVAILABLE) ) {
			return "";
		}
		return camera_settings.flash_value;
	}

	@Override
	public void setRecordingHint(boolean hint) {
		// not relevant for CameraController2
	}

	@Override
	public void setRotation(int rotation) {
		this.camera_settings.rotation = rotation;
	}

	@Override
	public void setLocationInfo(Location location) {
		if( MyDebug.LOG )
			Log.d(TAG, "setLocationInfo: " + location.getLongitude() + " , " + location.getLatitude());
		this.camera_settings.location = location;
	}

	@Override
	public void removeLocationInfo() {
		this.camera_settings.location = null;
	}

	@Override
	public void enableShutterSound(boolean enabled) {
		this.sounds_enabled = enabled;
	}

	/** Returns the viewable rect - this is crop region if available.
	 *  We need this as callers will pass in (or expect returned) CameraController.Area values that
	 *  are relative to the current view (i.e., taking zoom into account) (the old Camera API in
	 *  CameraController1 always works in terms of the current view, whilst Camera2 works in terms
	 *  of the full view always). Similarly for the rect field in CameraController.Face.
	 */
	private Rect getViewableRect() {
		if( previewBuilder != null ) {
			Rect crop_rect = previewBuilder.get(CaptureRequest.SCALER_CROP_REGION);
			if( crop_rect != null ) {
				return crop_rect;
			}
		}
		Rect sensor_rect = characteristics.get(CameraCharacteristics.SENSOR_INFO_ACTIVE_ARRAY_SIZE);
		sensor_rect.right -= sensor_rect.left;
		sensor_rect.left = 0;
		sensor_rect.bottom -= sensor_rect.top;
		sensor_rect.top = 0;
		return sensor_rect;
	}
	
	private Rect convertRectToCamera2(Rect crop_rect, Rect rect) {
		// CameraController.Area is always [-1000, -1000] to [1000, 1000] for the viewable region
		// but for CameraController2, we must convert to be relative to the crop region
		double left_f = (rect.left+1000)/2000.0;
		double top_f = (rect.top+1000)/2000.0;
		double right_f = (rect.right+1000)/2000.0;
		double bottom_f = (rect.bottom+1000)/2000.0;
		int left = (int)(crop_rect.left + left_f * (crop_rect.width()-1));
		int right = (int)(crop_rect.left + right_f * (crop_rect.width()-1));
		int top = (int)(crop_rect.top + top_f * (crop_rect.height()-1));
		int bottom = (int)(crop_rect.top + bottom_f * (crop_rect.height()-1));
		left = Math.max(left, crop_rect.left);
		right = Math.max(right, crop_rect.left);
		top = Math.max(top, crop_rect.top);
		bottom = Math.max(bottom, crop_rect.top);
		left = Math.min(left, crop_rect.right);
		right = Math.min(right, crop_rect.right);
		top = Math.min(top, crop_rect.bottom);
		bottom = Math.min(bottom, crop_rect.bottom);

		return new Rect(left, top, right, bottom);
	}

	private MeteringRectangle convertAreaToMeteringRectangle(Rect sensor_rect, Area area) {
		Rect camera2_rect = convertRectToCamera2(sensor_rect, area.rect);
		return new MeteringRectangle(camera2_rect, area.weight);
	}

	private Rect convertRectFromCamera2(Rect crop_rect, Rect camera2_rect) {
		// inverse of convertRectToCamera2()
		double left_f = (camera2_rect.left-crop_rect.left)/(double)(crop_rect.width()-1);
		double top_f = (camera2_rect.top-crop_rect.top)/(double)(crop_rect.height()-1);
		double right_f = (camera2_rect.right-crop_rect.left)/(double)(crop_rect.width()-1);
		double bottom_f = (camera2_rect.bottom-crop_rect.top)/(double)(crop_rect.height()-1);
		int left = (int)(left_f * 2000) - 1000;
		int right = (int)(right_f * 2000) - 1000;
		int top = (int)(top_f * 2000) - 1000;
		int bottom = (int)(bottom_f * 2000) - 1000;

		left = Math.max(left, -1000);
		right = Math.max(right, -1000);
		top = Math.max(top, -1000);
		bottom = Math.max(bottom, -1000);
		left = Math.min(left, 1000);
		right = Math.min(right, 1000);
		top = Math.min(top, 1000);
		bottom = Math.min(bottom, 1000);

		return new Rect(left, top, right, bottom);
	}

	private Area convertMeteringRectangleToArea(Rect sensor_rect, MeteringRectangle metering_rectangle) {
		Rect area_rect = convertRectFromCamera2(sensor_rect, metering_rectangle.getRect());
		return new Area(area_rect, metering_rectangle.getMeteringWeight());
	}
	
	private CameraController.Face convertFromCameraFace(Rect sensor_rect, android.hardware.camera2.params.Face camera2_face) {
		Rect area_rect = convertRectFromCamera2(sensor_rect, camera2_face.getBounds());
		return new CameraController.Face(camera2_face.getScore(), area_rect);
	}

	@Override
	public boolean setFocusAndMeteringArea(List<Area> areas) {
		Rect sensor_rect = getViewableRect();
		if( MyDebug.LOG )
			Log.d(TAG, "sensor_rect: " + sensor_rect.left + " , " + sensor_rect.top + " x " + sensor_rect.right + " , " + sensor_rect.bottom);
		boolean has_focus = false;
		boolean has_metering = false;
		if( characteristics.get(CameraCharacteristics.CONTROL_MAX_REGIONS_AF) > 0 ) {
			has_focus = true;
			camera_settings.af_regions = new MeteringRectangle[areas.size()];
			int i = 0;
			for(CameraController.Area area : areas) {
				camera_settings.af_regions[i++] = convertAreaToMeteringRectangle(sensor_rect, area);
			}
			camera_settings.setAFRegions(previewBuilder);
		}
		else
			camera_settings.af_regions = null;
		if( characteristics.get(CameraCharacteristics.CONTROL_MAX_REGIONS_AE) > 0 ) {
			has_metering = true;
			camera_settings.ae_regions = new MeteringRectangle[areas.size()];
			int i = 0;
			for(CameraController.Area area : areas) {
				camera_settings.ae_regions[i++] = convertAreaToMeteringRectangle(sensor_rect, area);
			}
			camera_settings.setAERegions(previewBuilder);
		}
		else
			camera_settings.ae_regions = null;
		if( has_focus || has_metering ) {
			try {
				setRepeatingRequest();
			}
			catch(CameraAccessException e) {
				if( MyDebug.LOG ) {
					Log.e(TAG, "failed to set focus and/or metering regions");
					Log.e(TAG, "reason: " + e.getReason());
					Log.e(TAG, "message: " + e.getMessage());
				}
				e.printStackTrace();
			} 
		}
		return has_focus;
	}
	
	@Override
	public void clearFocusAndMetering() {
		Rect sensor_rect = getViewableRect();
		boolean has_focus = false;
		boolean has_metering = false;
		if( sensor_rect.width() <= 0 || sensor_rect.height() <= 0 ) {
			// had a crash on Google Play due to creating a MeteringRectangle with -ve width/height ?!
			camera_settings.af_regions = null;
			camera_settings.ae_regions = null;
		}
		else {
			if( characteristics.get(CameraCharacteristics.CONTROL_MAX_REGIONS_AF) > 0 ) {
				has_focus = true;
				camera_settings.af_regions = new MeteringRectangle[1];
				camera_settings.af_regions[0] = new MeteringRectangle(0, 0, sensor_rect.width()-1, sensor_rect.height()-1, 0);
				camera_settings.setAFRegions(previewBuilder);
			}
			else
				camera_settings.af_regions = null;
			if( characteristics.get(CameraCharacteristics.CONTROL_MAX_REGIONS_AE) > 0 ) {
				has_metering = true;
				camera_settings.ae_regions = new MeteringRectangle[1];
				camera_settings.ae_regions[0] = new MeteringRectangle(0, 0, sensor_rect.width()-1, sensor_rect.height()-1, 0);
				camera_settings.setAERegions(previewBuilder);
			}
			else
				camera_settings.ae_regions = null;
		}
		if( has_focus || has_metering ) {
			try {
				setRepeatingRequest();
			}
			catch(CameraAccessException e) {
				if( MyDebug.LOG ) {
					Log.e(TAG, "failed to clear focus and metering regions");
					Log.e(TAG, "reason: " + e.getReason());
					Log.e(TAG, "message: " + e.getMessage());
				}
				e.printStackTrace();
			} 
		}
	}

	@Override
	public boolean supportsAutoFocus() {
		if( previewBuilder.get(CaptureRequest.CONTROL_AF_MODE) == null )
			return false;
		int focus_mode = previewBuilder.get(CaptureRequest.CONTROL_AF_MODE);
		if( focus_mode == CaptureRequest.CONTROL_AF_MODE_AUTO || focus_mode == CaptureRequest.CONTROL_AF_MODE_MACRO )
			return true;
		return false;
	}

	@Override
	public boolean focusIsContinuous() {
		if( previewBuilder == null || previewBuilder.get(CaptureRequest.CONTROL_AF_MODE) == null )
			return false;
		int focus_mode = previewBuilder.get(CaptureRequest.CONTROL_AF_MODE);
		if( focus_mode == CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE || focus_mode == CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_VIDEO )
			return true;
		return false;
	}

	@Override
	public void setPreviewDisplay(SurfaceHolder holder) throws CameraControllerException {
		if( MyDebug.LOG ) {
			Log.d(TAG, "setPreviewDisplay");
			Log.e(TAG, "SurfaceHolder not supported for CameraController2!");
			Log.e(TAG, "Should use setPreviewTexture() instead");
		}
		throw new RuntimeException(); // throw as RuntimeException, as this is a programming error
	}

	@Override
	public void setPreviewTexture(SurfaceTexture texture) throws CameraControllerException {
		if( MyDebug.LOG )
			Log.d(TAG, "setPreviewTexture");
		if( this.texture != null ) {
			if( MyDebug.LOG )
				Log.d(TAG, "preview texture already set");
			throw new RuntimeException(); // throw as RuntimeException, as this is a programming error
		}
		this.texture = texture;
	}

	private void setRepeatingRequest() throws CameraAccessException {
		setRepeatingRequest(previewBuilder.build());
	}

	private void setRepeatingRequest(CaptureRequest request) throws CameraAccessException {
		if( MyDebug.LOG )
			Log.d(TAG, "setRepeatingRequest");
		if( camera == null || captureSession == null ) {
			if( MyDebug.LOG )
				Log.d(TAG, "no camera or capture session");
			return;
		}
		captureSession.setRepeatingRequest(request, previewCaptureCallback, handler);
		if( MyDebug.LOG )
			Log.d(TAG, "setRepeatingRequest done");
	}

	private void capture() throws CameraAccessException {
		capture(previewBuilder.build());
	}

	private void capture(CaptureRequest request) throws CameraAccessException {
		if( MyDebug.LOG )
			Log.d(TAG, "capture");
		if( camera == null || captureSession == null ) {
			if( MyDebug.LOG )
				Log.d(TAG, "no camera or capture session");
			return;
		}
		captureSession.capture(request, previewCaptureCallback, handler);
	}
	
	private void createPreviewRequest() {
		if( MyDebug.LOG )
			Log.d(TAG, "createPreviewRequest");
		if( camera == null  ) {
			if( MyDebug.LOG )
				Log.d(TAG, "camera not available!");
			return;
		}
		if( MyDebug.LOG )
			Log.d(TAG, "camera: " + camera);
		try {
			previewBuilder = camera.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
			previewBuilder.set(CaptureRequest.CONTROL_CAPTURE_INTENT, CaptureRequest.CONTROL_CAPTURE_INTENT_PREVIEW);
			camera_settings.setupBuilder(previewBuilder, false);
			if( MyDebug.LOG )
				Log.d(TAG, "successfully created preview request");
		}
		catch(CameraAccessException e) {
			//captureSession = null;
			if( MyDebug.LOG ) {
				Log.e(TAG, "failed to create capture request");
				Log.e(TAG, "reason: " + e.getReason());
				Log.e(TAG, "message: " + e.getMessage());
			}
			e.printStackTrace();
		} 
	}

	private Surface getPreviewSurface() {
		return surface_texture;
	}

	private void createCaptureSession(final MediaRecorder video_recorder) throws CameraControllerException {
		if( MyDebug.LOG )
			Log.d(TAG, "create capture session");
		
		if( previewBuilder == null ) {
			if( MyDebug.LOG )
				Log.d(TAG, "previewBuilder not present!");
			throw new RuntimeException(); // throw as RuntimeException, as this is a programming error
		}
		if( camera == null ) {
			if( MyDebug.LOG )
				Log.e(TAG, "no camera");
			return;
		}

		if( captureSession != null ) {
			if( MyDebug.LOG )
				Log.d(TAG, "close old capture session");
			captureSession.close();
			captureSession = null;
			//pending_request_when_ready = null;
		}

		try {
			if( video_recorder != null ) {
				closePictureImageReader();
			}
			else {
				// in some cases need to recreate picture imageReader and the texture default buffer size (e.g., see test testTakePhotoPreviewPaused())
				createPictureImageReader();
			}
			if( texture != null ) {
				// need to set the texture size
				if( MyDebug.LOG )
					Log.d(TAG, "set size of preview texture");
				if( preview_width == 0 || preview_height == 0 ) {
					if( MyDebug.LOG )
						Log.e(TAG, "application needs to call setPreviewSize()");
					throw new RuntimeException(); // throw as RuntimeException, as this is a programming error
				}
				texture.setDefaultBufferSize(preview_width, preview_height);
				// also need to create a new surface for the texture, in case the size has changed - but make sure we remove the old one first!
				if( surface_texture != null ) {
					if( MyDebug.LOG )
						Log.d(TAG, "remove old target: " + surface_texture);
					previewBuilder.removeTarget(surface_texture);
				}
				this.surface_texture = new Surface(texture);
				if( MyDebug.LOG )
					Log.d(TAG, "created new target: " + surface_texture);
			}
			if( video_recorder != null ) {
				if( MyDebug.LOG )
					Log.d(TAG, "creating capture session for video recording");
			}
			else {
				if( MyDebug.LOG )
					Log.d(TAG, "picture size: " + imageReader.getWidth() + " x " + imageReader.getHeight());
			}
			/*if( MyDebug.LOG )
			Log.d(TAG, "preview size: " + previewImageReader.getWidth() + " x " + previewImageReader.getHeight());*/
			if( MyDebug.LOG )
				Log.d(TAG, "preview size: " + this.preview_width + " x " + this.preview_height);

			class MyStateCallback extends CameraCaptureSession.StateCallback {
				private boolean callback_done; // must sychronize on this and notifyAll when setting to true
				@Override
				public void onConfigured(@NonNull CameraCaptureSession session) {
					if( MyDebug.LOG ) {
						Log.d(TAG, "onConfigured: " + session);
						Log.d(TAG, "captureSession was: " + captureSession);
					}
					if( camera == null ) {
						if( MyDebug.LOG ) {
							Log.d(TAG, "camera is closed");
						}
					    synchronized( create_capture_session_lock ) {
					    	callback_done = true;
							create_capture_session_lock.notifyAll();
					    }
						return;
					}
					captureSession = session;
		        	Surface surface = getPreviewSurface();
	        		previewBuilder.addTarget(surface);
	        		if( video_recorder != null )
	        			previewBuilder.addTarget(video_recorder.getSurface());
	        		try {
	        			setRepeatingRequest();
	        		}
					catch(CameraAccessException e) {
						if( MyDebug.LOG ) {
							Log.e(TAG, "failed to start preview");
							Log.e(TAG, "reason: " + e.getReason());
							Log.e(TAG, "message: " + e.getMessage());
						}
						e.printStackTrace();
						// we indicate that we failed to start the preview by setting captureSession back to null
						// this will cause a CameraControllerException to be thrown below
						captureSession = null;
					}
				    synchronized( create_capture_session_lock ) {
				    	callback_done = true;
						create_capture_session_lock.notifyAll();
				    }
				}

				@Override
				public void onConfigureFailed(@NonNull CameraCaptureSession session) {
					if( MyDebug.LOG ) {
						Log.d(TAG, "onConfigureFailed: " + session);
						Log.d(TAG, "captureSession was: " + captureSession);
					}
				    synchronized( create_capture_session_lock ) {
				    	callback_done = true;
						create_capture_session_lock.notifyAll();
				    }
					// don't throw CameraControllerException here, as won't be caught - instead we throw CameraControllerException below
				}

				/*@Override
				public void onReady(CameraCaptureSession session) {
					if( MyDebug.LOG )
						Log.d(TAG, "onReady: " + session);
					if( pending_request_when_ready != null ) {
						if( MyDebug.LOG )
							Log.d(TAG, "have pending_request_when_ready: " + pending_request_when_ready);
						CaptureRequest request = pending_request_when_ready;
						pending_request_when_ready = null;
						try {
							captureSession.capture(request, previewCaptureCallback, handler);
						}
						catch(CameraAccessException e) {
							if( MyDebug.LOG ) {
								Log.e(TAG, "failed to take picture");
								Log.e(TAG, "reason: " + e.getReason());
								Log.e(TAG, "message: " + e.getMessage());
							}
							e.printStackTrace();
							jpeg_cb = null;
							if( take_picture_error_cb != null ) {
								take_picture_error_cb.onError();
								take_picture_error_cb = null;
							}
						}
					}
				}*/
			}
			final MyStateCallback myStateCallback = new MyStateCallback();

        	Surface preview_surface = getPreviewSurface();
        	List<Surface> surfaces;
        	if( video_recorder != null ) {
        		surfaces = Arrays.asList(preview_surface, video_recorder.getSurface());
        	}
    		else if( imageReaderRaw != null ) {
        		surfaces = Arrays.asList(preview_surface, imageReader.getSurface(), imageReaderRaw.getSurface());
    		}
    		else {
        		surfaces = Arrays.asList(preview_surface, imageReader.getSurface());
    		}
			if( MyDebug.LOG ) {
				Log.d(TAG, "texture: " + texture);
				Log.d(TAG, "preview_surface: " + preview_surface);
				if( video_recorder == null ) {
					if( imageReaderRaw != null ) {
						Log.d(TAG, "imageReaderRaw: " + imageReaderRaw);
						Log.d(TAG, "imageReaderRaw: " + imageReaderRaw.getWidth());
						Log.d(TAG, "imageReaderRaw: " + imageReaderRaw.getHeight());
						Log.d(TAG, "imageReaderRaw: " + imageReaderRaw.getImageFormat());
					}
					else {
						Log.d(TAG, "imageReader: " + imageReader);
						Log.d(TAG, "imageReader: " + imageReader.getWidth());
						Log.d(TAG, "imageReader: " + imageReader.getHeight());
						Log.d(TAG, "imageReader: " + imageReader.getImageFormat());
					}
				}
			}
			camera.createCaptureSession(surfaces,
				myStateCallback,
		 		handler);
			if( MyDebug.LOG )
				Log.d(TAG, "wait until session created...");
			synchronized( create_capture_session_lock ) {
				while( !myStateCallback.callback_done ) {
					try {
						// release the lock, and wait until myStateCallback calls notifyAll()
						create_capture_session_lock.wait();
					}
					catch(InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
			if( MyDebug.LOG ) {
				Log.d(TAG, "created captureSession: " + captureSession);
			}
			if( captureSession == null ) {
				if( MyDebug.LOG )
					Log.e(TAG, "failed to create capture session");
				throw new CameraControllerException();
			}
		}
		catch(CameraAccessException e) {
			if( MyDebug.LOG ) {
				Log.e(TAG, "CameraAccessException trying to create capture session");
				Log.e(TAG, "reason: " + e.getReason());
				Log.e(TAG, "message: " + e.getMessage());
			}
			e.printStackTrace();
			throw new CameraControllerException();
		}
	}

	@Override
	public void startPreview() throws CameraControllerException {
		if( MyDebug.LOG )
			Log.d(TAG, "startPreview");
		if( captureSession != null ) {
			try {
				setRepeatingRequest();
			}
			catch(CameraAccessException e) {
				if( MyDebug.LOG ) {
					Log.e(TAG, "failed to start preview");
					Log.e(TAG, "reason: " + e.getReason());
					Log.e(TAG, "message: " + e.getMessage());
				}
				e.printStackTrace();
				// do via CameraControllerException instead of preview_error_cb, so caller immediately knows preview has failed
				throw new CameraControllerException();
			} 
			return;
		}
		createCaptureSession(null);
	}

	@Override
	public void stopPreview() {
		if( MyDebug.LOG )
			Log.d(TAG, "stopPreview");
		if( camera == null || captureSession == null ) {
			if( MyDebug.LOG )
				Log.d(TAG, "no camera or capture session");
			return;
		}
		try {
			//pending_request_when_ready = null;

			captureSession.stopRepeating();
			// although stopRepeating() alone will pause the preview, seems better to close captureSession altogether - this allows the app to make changes such as changing the picture size
			if( MyDebug.LOG )
				Log.d(TAG, "close capture session");
			captureSession.close();
			captureSession = null;
		}
		catch(CameraAccessException e) {
			if( MyDebug.LOG ) {
				Log.e(TAG, "failed to stop repeating");
				Log.e(TAG, "reason: " + e.getReason());
				Log.e(TAG, "message: " + e.getMessage());
			}
			e.printStackTrace();
		}
		// simulate CameraController1 behaviour where face detection is stopped when we stop preview
		if( camera_settings.has_face_detect_mode ) {
			if( MyDebug.LOG )
				Log.d(TAG, "cancel face detection");
			camera_settings.has_face_detect_mode = false;
	    	camera_settings.setFaceDetectMode(previewBuilder);
			// no need to call setRepeatingRequest(), we're just setting the camera_settings for when we restart the preview
		}
	}

	@Override
	public boolean startFaceDetection() {
		if( MyDebug.LOG )
			Log.d(TAG, "startFaceDetection");
    	if( previewBuilder.get(CaptureRequest.STATISTICS_FACE_DETECT_MODE) != null && previewBuilder.get(CaptureRequest.STATISTICS_FACE_DETECT_MODE) != CaptureRequest.STATISTICS_FACE_DETECT_MODE_OFF ) {
			if( MyDebug.LOG )
				Log.d(TAG, "face detection already enabled");
    		return false;
    	}
		if( supports_face_detect_mode_full ) {
			if( MyDebug.LOG )
				Log.d(TAG, "use full face detection");
			camera_settings.has_face_detect_mode = true;
			camera_settings.face_detect_mode = CaptureRequest.STATISTICS_FACE_DETECT_MODE_FULL;
		}
		else if( supports_face_detect_mode_simple ) {
			if( MyDebug.LOG )
				Log.d(TAG, "use simple face detection");
			camera_settings.has_face_detect_mode = true;
			camera_settings.face_detect_mode = CaptureRequest.STATISTICS_FACE_DETECT_MODE_SIMPLE;
		}
		else {
			Log.e(TAG, "startFaceDetection() called but face detection not available");
			return false;
		}
    	camera_settings.setFaceDetectMode(previewBuilder);
    	try {
    		setRepeatingRequest();
			return false;
    	}
		catch(CameraAccessException e) {
			if( MyDebug.LOG ) {
				Log.e(TAG, "failed to start face detection");
				Log.e(TAG, "reason: " + e.getReason());
				Log.e(TAG, "message: " + e.getMessage());
			}
			e.printStackTrace();
		} 
		return true;
	}
	
	@Override
	public void setFaceDetectionListener(final FaceDetectionListener listener) {
		this.face_detection_listener = listener;
	}

	/* If do_af_trigger_for_continuous is false, doing an autoFocus() in continuous focus mode just
	   means we call the autofocus callback the moment focus is not scanning (as with old Camera API).
	   If do_af_trigger_for_continuous is true, we set CONTROL_AF_TRIGGER_START, and wait for
	   CONTROL_AF_STATE_FOCUSED_LOCKED or CONTROL_AF_STATE_NOT_FOCUSED_LOCKED, similar to other focus
	   methods.
	   do_af_trigger_for_continuous==true has advantages:
	     - On Nexus 6 for flash auto, it means ae state is set to FLASH_REQUIRED if it is required
	       when it comes to taking the photo. If do_af_trigger_for_continuous==false, sometimes
	       it's set to CONTROL_AE_STATE_CONVERGED even for dark scenes, so we think we can skip
	       the precapture, causing photos to come out dark (or we can force always doing precapture,
	       but that makes things slower when flash isn't needed)
	     - On OnePlus 3T, with do_af_trigger_for_continuous==false photos come out with blue tinge
	       if the scene is not dark (but still dark enough that you'd want flash).
	       do_af_trigger_for_continuous==true fixes this for cases where the flash fires for autofocus.
	       Note that the problem is still not fixed for flash on where the scene is bright enough to
	       not need flash (and so we don't fire flash for autofocus).
	   do_af_trigger_for_continuous==true has disadvantage:
	     - On both Nexus 6 and OnePlus 3T, taking photos with flash is longer, as we have flash firing
	       for autofocus and precapture. Though note this is the case with autofocus mode anyway.
	   Note for fake flash mode, we still can use do_af_trigger_for_continuous==false (and doing the
	   af trigger for fake flash mode can sometimes mean flash fires for too long and we get a worse
	   result).
	 */
	//private final static boolean do_af_trigger_for_continuous = false;
	private final static boolean do_af_trigger_for_continuous = true;

	@Override
	public void autoFocus(final AutoFocusCallback cb, boolean capture_follows_autofocus_hint) {
		if( MyDebug.LOG ) {
			Log.d(TAG, "autoFocus");
			Log.d(TAG, "capture_follows_autofocus_hint? " + capture_follows_autofocus_hint);
		}
		fake_precapture_torch_focus_performed = false;
		if( camera == null || captureSession == null ) {
			if( MyDebug.LOG )
				Log.d(TAG, "no camera or capture session");
			// should call the callback, so the application isn't left waiting (e.g., when we autofocus before trying to take a photo)
			cb.onAutoFocus(false);
			return;
		}
		Integer focus_mode = previewBuilder.get(CaptureRequest.CONTROL_AF_MODE);
		if( focus_mode == null ) {
			// we preserve the old Camera API where calling autoFocus() on a device without autofocus immediately calls the callback
			// (unclear if Open Camera needs this, but just to be safe and consistent between camera APIs)
			cb.onAutoFocus(true);
			return;
		}
		else if( (!do_af_trigger_for_continuous || use_fake_precapture_mode) && focus_mode == CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE ) {
			// See note above for do_af_trigger_for_continuous
			this.capture_follows_autofocus_hint = capture_follows_autofocus_hint;
			this.autofocus_cb = cb;
			return;
		}
		/*if( state == STATE_WAITING_AUTOFOCUS ) {
			if( MyDebug.LOG )
				Log.d(TAG, "already waiting for an autofocus");
			// need to update the callback!
			this.capture_follows_autofocus_hint = capture_follows_autofocus_hint;
			this.autofocus_cb = cb;
			return;
		}*/
		CaptureRequest.Builder afBuilder = previewBuilder;
		if( MyDebug.LOG ) {
			{
				MeteringRectangle[] areas = afBuilder.get(CaptureRequest.CONTROL_AF_REGIONS);
				for(int i=0;areas != null && i<areas.length;i++) {
					Log.d(TAG, i + " focus area: " + areas[i].getX() + " , " + areas[i].getY() + " : " + areas[i].getWidth() + " x " + areas[i].getHeight() + " weight " + areas[i].getMeteringWeight());
				}
			}
			{
				MeteringRectangle[] areas = afBuilder.get(CaptureRequest.CONTROL_AE_REGIONS);
				for(int i=0;areas != null && i<areas.length;i++) {
					Log.d(TAG, i + " metering area: " + areas[i].getX() + " , " + areas[i].getY() + " : " + areas[i].getWidth() + " x " + areas[i].getHeight() + " weight " + areas[i].getMeteringWeight());
				}
			}
		}
		state = STATE_WAITING_AUTOFOCUS;
		precapture_state_change_time_ms = -1;
		this.capture_follows_autofocus_hint = capture_follows_autofocus_hint;
		this.autofocus_cb = cb;
		try {
			if( use_fake_precapture_mode && !camera_settings.has_iso ) {
				boolean want_flash = false;
				if( camera_settings.flash_value.equals("flash_auto") || camera_settings.flash_value.equals("flash_frontscreen_auto") ) {
					// calling fireAutoFlash() also caches the decision on whether to flash - otherwise if the flash fires now, we'll then think the scene is bright enough to not need the flash!
					if( fireAutoFlash() )
						want_flash = true;
				}
				else if( camera_settings.flash_value.equals("flash_on") ) {
					want_flash = true;
				}
				if( want_flash ) {
					if( MyDebug.LOG )
						Log.d(TAG, "turn on torch for fake flash");
					afBuilder.set(CaptureRequest.CONTROL_AE_MODE, CameraMetadata.CONTROL_AE_MODE_ON);
					afBuilder.set(CaptureRequest.FLASH_MODE, CameraMetadata.FLASH_MODE_TORCH);
					test_fake_flash_focus++;
					fake_precapture_torch_focus_performed = true;
					setRepeatingRequest(afBuilder.build());
					// We sleep for a short time as on some devices (e.g., OnePlus 3T), the torch will turn off when autofocus
					// completes even if we don't want that (because we'll be taking a photo).
					// Note that on other devices such as Nexus 6, this problem doesn't occur even if we don't have a separate
					// setRepeatingRequest.
					// Update for 1.37: now we do need this for Nexus 6 too, after switching to setting CONTROL_AE_MODE_ON_AUTO_FLASH
					// or CONTROL_AE_MODE_ON_ALWAYS_FLASH even for fake flash (see note in CameraSettings.setAEMode()) - and we
					// needed to increase to 200ms! Otherwise photos come out too dark for flash on if doing touch to focus then
					// quickly taking a photo. (It also work to previously switch to CONTROL_AE_MODE_ON/FLASH_MODE_OFF first,
					// but then the same problem shows up on OnePlus 3T again!)
					try {
						Thread.sleep(200);
					}
					catch(InterruptedException e) {
						e.printStackTrace();
					}
				}
			}

			// Camera2Basic sets a trigger with capture
			// Google Camera sets to idle with a repeating request, then sets af trigger to start with a capture
			afBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER, CameraMetadata.CONTROL_AF_TRIGGER_IDLE);
			setRepeatingRequest(afBuilder.build());
			afBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER, CameraMetadata.CONTROL_AF_TRIGGER_START);
			capture(afBuilder.build());
		}
		catch(CameraAccessException e) {
			if( MyDebug.LOG ) {
				Log.e(TAG, "failed to autofocus");
				Log.e(TAG, "reason: " + e.getReason());
				Log.e(TAG, "message: " + e.getMessage());
			}
			e.printStackTrace();
			state = STATE_NORMAL;
			precapture_state_change_time_ms = -1;
			autofocus_cb.onAutoFocus(false);
			autofocus_cb = null;
			this.capture_follows_autofocus_hint = false;
		}
		afBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER, CameraMetadata.CONTROL_AF_TRIGGER_IDLE); // ensure set back to idle
	}

	@Override
	public void setCaptureFollowAutofocusHint(boolean capture_follows_autofocus_hint) {
		if( MyDebug.LOG ) {
			Log.d(TAG, "setCaptureFollowAutofocusHint");
			Log.d(TAG, "capture_follows_autofocus_hint? " + capture_follows_autofocus_hint);
		}
		this.capture_follows_autofocus_hint = capture_follows_autofocus_hint;
	}

	@Override
	public void cancelAutoFocus() {
		if( MyDebug.LOG )
			Log.d(TAG, "cancelAutoFocus");
		if( camera == null || captureSession == null ) {
			if( MyDebug.LOG )
				Log.d(TAG, "no camera or capture session");
			return;
		}
    	previewBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER, CameraMetadata.CONTROL_AF_TRIGGER_CANCEL);
		// Camera2Basic does a capture then sets a repeating request - do the same here just to be safe
    	try {
    		capture();
    	}
		catch(CameraAccessException e) {
			if( MyDebug.LOG ) {
				Log.e(TAG, "failed to cancel autofocus [capture]");
				Log.e(TAG, "reason: " + e.getReason());
				Log.e(TAG, "message: " + e.getMessage());
			}
			e.printStackTrace();
		}
    	previewBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER, CameraMetadata.CONTROL_AF_TRIGGER_IDLE);
		this.autofocus_cb = null;
		this.capture_follows_autofocus_hint = false;
		state = STATE_NORMAL;
		precapture_state_change_time_ms = -1;
		try {
			setRepeatingRequest();
		}
		catch(CameraAccessException e) {
			if( MyDebug.LOG ) {
				Log.e(TAG, "failed to set repeating request after cancelling autofocus");
				Log.e(TAG, "reason: " + e.getReason());
				Log.e(TAG, "message: " + e.getMessage());
			}
			e.printStackTrace();
		} 
	}
	
	@Override
	public void setContinuousFocusMoveCallback(ContinuousFocusMoveCallback cb) {
		if( MyDebug.LOG )
			Log.d(TAG, "setContinuousFocusMoveCallback");
		this.continuous_focus_move_callback = cb;
	}

	static public double getScaleForExposureTime(long exposure_time, long fixed_exposure_time, long scaled_exposure_time, double full_exposure_time_scale) {
		if( MyDebug.LOG )
			Log.d(TAG, "getScaleForExposureTime");
		double alpha = (exposure_time - fixed_exposure_time) / (double) (scaled_exposure_time - fixed_exposure_time);
		if( alpha < 0.0 )
			alpha = 0.0;
		else if( alpha > 1.0 )
			alpha = 1.0;
		if( MyDebug.LOG ) {
			Log.d(TAG, "exposure_time: " + exposure_time);
			Log.d(TAG, "alpha: " + alpha);
		}
		// alpha==0 means exposure_time_scale==1; alpha==1 means exposure_time_scale==full_exposure_time_scale
		return (1.0 - alpha) + alpha * full_exposure_time_scale;
	}

	private void takePictureAfterPrecapture() {
		if( MyDebug.LOG )
			Log.d(TAG, "takePictureAfterPrecapture");
		if( want_expo_bracketing ) {
			takePictureBurstExpoBracketing();
			return;
		}
		if( camera == null || captureSession == null ) {
			if( MyDebug.LOG )
				Log.d(TAG, "no camera or capture session");
			return;
		}
		try {
			if( MyDebug.LOG ) {
				if( imageReaderRaw != null ) {
					Log.d(TAG, "imageReaderRaw: " + imageReaderRaw.toString());
					Log.d(TAG, "imageReaderRaw surface: " + imageReaderRaw.getSurface().toString());
				}
				else {
					Log.d(TAG, "imageReader: " + imageReader.toString());
					Log.d(TAG, "imageReader surface: " + imageReader.getSurface().toString());
				}
			}
			CaptureRequest.Builder stillBuilder = camera.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
			stillBuilder.set(CaptureRequest.CONTROL_CAPTURE_INTENT, CaptureRequest.CONTROL_CAPTURE_INTENT_STILL_CAPTURE);
			stillBuilder.setTag(RequestTag.CAPTURE);
			camera_settings.setupBuilder(stillBuilder, true);
			if( use_fake_precapture_mode && fake_precapture_torch_performed ) {
				if( MyDebug.LOG )
					Log.d(TAG, "setting torch for capture");
				stillBuilder.set(CaptureRequest.CONTROL_AE_MODE, CameraMetadata.CONTROL_AE_MODE_ON);
				stillBuilder.set(CaptureRequest.FLASH_MODE, CameraMetadata.FLASH_MODE_TORCH);
				test_fake_flash_photo++;
			}
			if( !camera_settings.has_iso && this.optimise_ae_for_dro && capture_result_has_exposure_time && (camera_settings.flash_value.equals("flash_off") || camera_settings.flash_value.equals("flash_auto") || camera_settings.flash_value.equals("flash_frontscreen_auto") ) ) {
				final double full_exposure_time_scale = Math.pow(2.0, -0.5);
				final long fixed_exposure_time = 1000000000L/60; // we only scale the exposure time at all if it's less than this value
				final long scaled_exposure_time = 1000000000L/120; // we only scale the exposure time by the full_exposure_time_scale if the exposure time is less than this value
				long exposure_time = capture_result_exposure_time;
				if( exposure_time <= fixed_exposure_time ) {
					Range<Long> exposure_time_range = characteristics.get(CameraCharacteristics.SENSOR_INFO_EXPOSURE_TIME_RANGE); // may be null on some devices
					if( exposure_time_range != null ) {
						double exposure_time_scale = getScaleForExposureTime(exposure_time, fixed_exposure_time, scaled_exposure_time, full_exposure_time_scale);
						if (MyDebug.LOG) {
							Log.d(TAG, "reduce exposure shutter speed further, was: " + exposure_time);
							Log.d(TAG, "exposure_time_scale: " + exposure_time_scale);
						}
						long min_exposure_time = exposure_time_range.getLower();
						long max_exposure_time = exposure_time_range.getUpper();
						exposure_time *= exposure_time_scale;
						if( exposure_time < min_exposure_time )
							exposure_time = min_exposure_time;
						if( exposure_time > max_exposure_time )
							exposure_time = max_exposure_time;
						if (MyDebug.LOG) {
							Log.d(TAG, "exposure_time: " + exposure_time);
						}
						stillBuilder.set(CaptureRequest.CONTROL_AE_MODE, CameraMetadata.CONTROL_AE_MODE_OFF);
						if( capture_result_has_iso )
							stillBuilder.set(CaptureRequest.SENSOR_SENSITIVITY, capture_result_iso );
						else
							stillBuilder.set(CaptureRequest.SENSOR_SENSITIVITY, 800);
						if( capture_result_has_frame_duration  )
							stillBuilder.set(CaptureRequest.SENSOR_FRAME_DURATION, capture_result_frame_duration);
						else
							stillBuilder.set(CaptureRequest.SENSOR_FRAME_DURATION, 1000000000L/30);
						stillBuilder.set(CaptureRequest.SENSOR_EXPOSURE_TIME, exposure_time);
					}
				}
			}
			//stillBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
			//stillBuilder.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH);
			clearPending();
        	Surface surface = getPreviewSurface();
        	stillBuilder.addTarget(surface); // Google Camera adds the preview surface as well as capture surface, for still capture
    		stillBuilder.addTarget(imageReader.getSurface());
        	if( imageReaderRaw != null )
    			stillBuilder.addTarget(imageReaderRaw.getSurface());

			captureSession.stopRepeating(); // need to stop preview before capture (as done in Camera2Basic; otherwise we get bugs such as flash remaining on after taking a photo with flash)
			if( jpeg_cb != null ) {
				if( MyDebug.LOG )
					Log.d(TAG, "call onStarted() in callback");
				jpeg_cb.onStarted();
			}
			if( MyDebug.LOG )
				Log.d(TAG, "capture with stillBuilder");
			//pending_request_when_ready = stillBuilder.build();
			captureSession.capture(stillBuilder.build(), previewCaptureCallback, handler);
			if( sounds_enabled ) // play shutter sound asap, otherwise user has the illusion of being slow to take photos
				media_action_sound.play(MediaActionSound.SHUTTER_CLICK);
		}
		catch(CameraAccessException e) {
			if( MyDebug.LOG ) {
				Log.e(TAG, "failed to take picture");
				Log.e(TAG, "reason: " + e.getReason());
				Log.e(TAG, "message: " + e.getMessage());
			}
			e.printStackTrace();
			jpeg_cb = null;
			if( take_picture_error_cb != null ) {
				take_picture_error_cb.onError();
				take_picture_error_cb = null;
			}
		}
	}

	private void takePictureBurstExpoBracketing() {
		if( MyDebug.LOG )
			Log.d(TAG, "takePictureBurstExpBracketing");
		if( MyDebug.LOG && !want_expo_bracketing ) {
			Log.e(TAG, "takePictureBurstExpoBracketing called but want_expo_bracketing is false");
		}
		if( camera == null || captureSession == null ) {
			if( MyDebug.LOG )
				Log.d(TAG, "no camera or capture session");
			return;
		}
		try {
			if( MyDebug.LOG ) {
				Log.d(TAG, "imageReader: " + imageReader.toString());
				Log.d(TAG, "imageReader surface: " + imageReader.getSurface().toString());
			}

			CaptureRequest.Builder stillBuilder = camera.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
			stillBuilder.set(CaptureRequest.CONTROL_CAPTURE_INTENT, CaptureRequest.CONTROL_CAPTURE_INTENT_STILL_CAPTURE);
			// n.b., don't set RequestTag.CAPTURE here - we only do it for the last of the burst captures (see below)
			camera_settings.setupBuilder(stillBuilder, true);
			clearPending();
        	Surface surface = getPreviewSurface();
        	stillBuilder.addTarget(surface); // Google Camera adds the preview surface as well as capture surface, for still capture
			stillBuilder.addTarget(imageReader.getSurface());
			// don't add target imageReaderRaw, as Raw not supported for burst

			List<CaptureRequest> requests = new ArrayList<>();

			/*stillBuilder.set(CaptureRequest.CONTROL_AE_MODE, CameraMetadata.CONTROL_AE_MODE_ON);
			stillBuilder.set(CaptureRequest.FLASH_MODE, CameraMetadata.FLASH_MODE_OFF);

			stillBuilder.set(CaptureRequest.CONTROL_AE_EXPOSURE_COMPENSATION, -6);
			requests.add( stillBuilder.build() );
			stillBuilder.set(CaptureRequest.CONTROL_AE_EXPOSURE_COMPENSATION, 0);
			requests.add( stillBuilder.build() );
			stillBuilder.set(CaptureRequest.CONTROL_AE_EXPOSURE_COMPENSATION, 6);
			requests.add( stillBuilder.build() );*/

			stillBuilder.set(CaptureRequest.CONTROL_AE_MODE, CameraMetadata.CONTROL_AE_MODE_OFF);
			if( use_fake_precapture_mode && fake_precapture_torch_performed ) {
				stillBuilder.set(CaptureRequest.FLASH_MODE, CameraMetadata.FLASH_MODE_TORCH);
				test_fake_flash_photo++;
			}
			// else don't turn torch off, as user may be in torch on mode

			// obtain current ISO/etc settings from the capture result - but if we're in manual ISO mode,
			// might as well use the settings the user has actually requested (also useful for workaround for
			// OnePlus 3T bug where the reported ISO and exposure_time are wrong in dark scenes)
			if( camera_settings.has_iso )
				stillBuilder.set(CaptureRequest.SENSOR_SENSITIVITY, camera_settings.iso );
			else if( capture_result_has_iso )
				stillBuilder.set(CaptureRequest.SENSOR_SENSITIVITY, capture_result_iso );
			else
				stillBuilder.set(CaptureRequest.SENSOR_SENSITIVITY, 800);
			if( capture_result_has_frame_duration  )
				stillBuilder.set(CaptureRequest.SENSOR_FRAME_DURATION, capture_result_frame_duration);
			else
				stillBuilder.set(CaptureRequest.SENSOR_FRAME_DURATION, 1000000000L/30);

			long base_exposure_time = 1000000000L/30;
			if( camera_settings.has_iso )
				base_exposure_time = camera_settings.exposure_time;
			else if( capture_result_has_exposure_time )
				base_exposure_time = capture_result_exposure_time;

			int n_half_images = expo_bracketing_n_images/2;
			long min_exposure_time = base_exposure_time;
			long max_exposure_time = base_exposure_time;
			final double scale = Math.pow(2.0, expo_bracketing_stops/(double)n_half_images);
			Range<Long> exposure_time_range = characteristics.get(CameraCharacteristics.SENSOR_INFO_EXPOSURE_TIME_RANGE); // may be null on some devices
			if( exposure_time_range != null ) {
				min_exposure_time = exposure_time_range.getLower();
				max_exposure_time = exposure_time_range.getUpper();
			}

			if( MyDebug.LOG ) {
				Log.d(TAG, "taking expo bracketing with n_images: " + expo_bracketing_n_images);
				Log.d(TAG, "ISO: " + stillBuilder.get(CaptureRequest.SENSOR_SENSITIVITY));
				Log.d(TAG, "Frame duration: " + stillBuilder.get(CaptureRequest.SENSOR_FRAME_DURATION));
				Log.d(TAG, "Base exposure time: " + base_exposure_time);
				Log.d(TAG, "Min exposure time: " + min_exposure_time);
				Log.d(TAG, "Max exposure time: " + max_exposure_time);
			}

			// darker images
			for(int i=0;i<n_half_images;i++) {
				long exposure_time = base_exposure_time;
				if( exposure_time_range != null ) {
					double this_scale = scale;
					for(int j=i;j<n_half_images-1;j++)
						this_scale *= scale;
					exposure_time /= this_scale;
					if( exposure_time < min_exposure_time )
						exposure_time = min_exposure_time;
					if( MyDebug.LOG ) {
						Log.d(TAG, "add burst request for " + i + "th dark image:");
						Log.d(TAG, "    this_scale: " + this_scale);
						Log.d(TAG, "    exposure_time: " + exposure_time);
					}
					stillBuilder.set(CaptureRequest.SENSOR_EXPOSURE_TIME, exposure_time);
					requests.add( stillBuilder.build() );
				}
			}
			
			// base image
			if( MyDebug.LOG )
				Log.d(TAG, "add burst request for base image");
			stillBuilder.set(CaptureRequest.SENSOR_EXPOSURE_TIME, base_exposure_time);
			requests.add( stillBuilder.build() );

			// lighter images
			for(int i=0;i<n_half_images;i++) {
				long exposure_time = base_exposure_time;
				if( exposure_time_range != null ) {
					double this_scale = scale;
					for(int j=0;j<i;j++)
						this_scale *= scale;
					exposure_time *= this_scale;
					if( exposure_time > max_exposure_time )
						exposure_time = max_exposure_time;
					if( MyDebug.LOG ) {
						Log.d(TAG, "add burst request for " + i + "th light image:");
						Log.d(TAG, "    this_scale: " + this_scale);
						Log.d(TAG, "    exposure_time: " + exposure_time);
					}
					stillBuilder.set(CaptureRequest.SENSOR_EXPOSURE_TIME, exposure_time);
					if( i == n_half_images - 1 ) {
						// RequestTag.CAPTURE should only be set for the last request, otherwise we'll may do things like turning
						// off torch (for fake flash) before all images are received
						// More generally, doesn't seem a good idea to be doing the post-capture commands (resetting ae state etc)
						// multiple times, and before all captures are complete!
						if( MyDebug.LOG )
							Log.d(TAG, "set RequestTag.CAPTURE for last burst request");
						stillBuilder.setTag(RequestTag.CAPTURE);
					}
					requests.add( stillBuilder.build() );
				}
			}

			/*
			// testing:
			requests.add( stillBuilder.build() );
			requests.add( stillBuilder.build() );
			requests.add( stillBuilder.build() );
			requests.add( stillBuilder.build() );
			if( MyDebug.LOG )
				Log.d(TAG, "set RequestTag.CAPTURE for last burst request");
			stillBuilder.setTag(RequestTag.CAPTURE);
			requests.add( stillBuilder.build() );
			*/

			n_burst = requests.size();
			if( MyDebug.LOG )
				Log.d(TAG, "n_burst: " + n_burst);

			captureSession.stopRepeating(); // see note under takePictureAfterPrecapture()

			if( jpeg_cb != null ) {
				if( MyDebug.LOG )
					Log.d(TAG, "call onStarted() in callback");
				jpeg_cb.onStarted();
			}

			if( use_expo_fast_burst ) {
				if( MyDebug.LOG )
					Log.d(TAG, "using fast burst");
				int sequenceId = captureSession.captureBurst(requests, previewCaptureCallback, handler);
				if( MyDebug.LOG )
					Log.d(TAG, "sequenceId: " + sequenceId);
			}
			else {
				if( MyDebug.LOG )
					Log.d(TAG, "using slow burst");
				burst_capture_requests = requests;
				burst_start_ms = System.currentTimeMillis();
				captureSession.capture(requests.get(0), previewCaptureCallback, handler);
			}

			if( sounds_enabled ) // play shutter sound asap, otherwise user has the illusion of being slow to take photos
				media_action_sound.play(MediaActionSound.SHUTTER_CLICK);
		}
		catch(CameraAccessException e) {
			if( MyDebug.LOG ) {
				Log.e(TAG, "failed to take picture burst");
				Log.e(TAG, "reason: " + e.getReason());
				Log.e(TAG, "message: " + e.getMessage());
			}
			e.printStackTrace();
			jpeg_cb = null;
			if( take_picture_error_cb != null ) {
				take_picture_error_cb.onError();
				take_picture_error_cb = null;
			}
		}
	}

	private void runPrecapture() {
		if( MyDebug.LOG )
			Log.d(TAG, "runPrecapture");
		// first run precapture sequence
		if( MyDebug.LOG ) {
			if( use_fake_precapture_mode )
				Log.e(TAG, "shouldn't be doing standard precapture when use_fake_precapture_mode is true!");
			else if( want_expo_bracketing )
				Log.e(TAG, "shouldn't be doing precapture for want_expo_bracketing - should be using fake precapture!");
		}
		try {
			// use a separate builder for precapture - otherwise have problem that if we take photo with flash auto/on of dark scene, then point to a bright scene, the autoexposure isn't running until we autofocus again
			final CaptureRequest.Builder precaptureBuilder = camera.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
			precaptureBuilder.set(CaptureRequest.CONTROL_CAPTURE_INTENT, CaptureRequest.CONTROL_CAPTURE_INTENT_STILL_CAPTURE);

			camera_settings.setupBuilder(precaptureBuilder, false);
			precaptureBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER, CameraMetadata.CONTROL_AF_TRIGGER_IDLE);
			precaptureBuilder.set(CaptureRequest.CONTROL_AE_PRECAPTURE_TRIGGER, CameraMetadata.CONTROL_AE_PRECAPTURE_TRIGGER_IDLE);

			precaptureBuilder.addTarget(getPreviewSurface());

	    	state = STATE_WAITING_PRECAPTURE_START;
	    	precapture_state_change_time_ms = System.currentTimeMillis();

	    	// first set precapture to idle - this is needed, otherwise we hang in state STATE_WAITING_PRECAPTURE_START, because precapture already occurred whilst autofocusing, and it doesn't occur again unless we first set the precapture trigger to idle
			if( MyDebug.LOG )
				Log.d(TAG, "capture with precaptureBuilder");
			captureSession.capture(precaptureBuilder.build(), previewCaptureCallback, handler);
			captureSession.setRepeatingRequest(precaptureBuilder.build(), previewCaptureCallback, handler);

			// now set precapture
			precaptureBuilder.set(CaptureRequest.CONTROL_AE_PRECAPTURE_TRIGGER, CameraMetadata.CONTROL_AE_PRECAPTURE_TRIGGER_START);
			captureSession.capture(precaptureBuilder.build(), previewCaptureCallback, handler);
		}
		catch(CameraAccessException e) {
			if( MyDebug.LOG ) {
				Log.e(TAG, "failed to precapture");
				Log.e(TAG, "reason: " + e.getReason());
				Log.e(TAG, "message: " + e.getMessage());
			}
			e.printStackTrace();
			jpeg_cb = null;
			if( take_picture_error_cb != null ) {
				take_picture_error_cb.onError();
				take_picture_error_cb = null;
			}
		}
	}
	
	private void runFakePrecapture() {
		if( MyDebug.LOG )
			Log.d(TAG, "runFakePrecapture");
		switch(camera_settings.flash_value) {
			case "flash_auto":
			case "flash_on":
				if(MyDebug.LOG)
					Log.d(TAG, "turn on torch");
				previewBuilder.set(CaptureRequest.CONTROL_AE_MODE, CameraMetadata.CONTROL_AE_MODE_ON);
				previewBuilder.set(CaptureRequest.FLASH_MODE, CameraMetadata.FLASH_MODE_TORCH);
				test_fake_flash_precapture++;
				fake_precapture_torch_performed = true;
				break;
			case "flash_frontscreen_auto":
			case "flash_frontscreen_on":
				if(jpeg_cb != null) {
					if(MyDebug.LOG)
						Log.d(TAG, "request screen turn on for frontscreen flash");
					jpeg_cb.onFrontScreenTurnOn();
				}
				else {
					if (MyDebug.LOG)
						Log.e(TAG, "can't request screen turn on for frontscreen flash, as no jpeg_cb");
				}
				break;
			default:
				if(MyDebug.LOG)
					Log.e(TAG, "runFakePrecapture called with unexpected flash value: " + camera_settings.flash_value);
				break;
		}
    	state = STATE_WAITING_FAKE_PRECAPTURE_START;
    	precapture_state_change_time_ms = System.currentTimeMillis();
		fake_precapture_turn_on_torch_id = null;
		try {
			CaptureRequest request = previewBuilder.build();
			if( fake_precapture_torch_performed ) {
				fake_precapture_turn_on_torch_id = request;
				if( MyDebug.LOG )
					Log.d(TAG, "fake_precapture_turn_on_torch_id: " + request);
			}
			setRepeatingRequest(request);
		}
		catch(CameraAccessException e) {
			if( MyDebug.LOG ) {
				Log.e(TAG, "failed to start fake precapture");
				Log.e(TAG, "reason: " + e.getReason());
				Log.e(TAG, "message: " + e.getMessage());
			}
			e.printStackTrace();
			jpeg_cb = null;
			if( take_picture_error_cb != null ) {
				take_picture_error_cb.onError();
				take_picture_error_cb = null;
			}
		} 
	}
	
	/** Used in use_fake_precapture mode when flash is auto, this returns whether we fire the flash.
	 *  If the decision was recently calculated, we return that same decision - used to fix problem that if
	 *  we fire flash during autofocus (for autofocus mode), we don't then want to decide the scene is too
	 *  bright to not need flash for taking photo!
	 */
	private boolean fireAutoFlash() {
		if( MyDebug.LOG )
			Log.d(TAG, "fireAutoFlash");
		long time_now = System.currentTimeMillis();
		if( MyDebug.LOG && fake_precapture_use_flash_time_ms != -1 ) {
			Log.d(TAG, "fake_precapture_use_flash_time_ms: " + fake_precapture_use_flash_time_ms);
			Log.d(TAG, "time_now: " + time_now);
			Log.d(TAG, "time since last flash auto decision: " + (time_now - fake_precapture_use_flash_time_ms));
		}
		final long cache_time_ms = 3000; // needs to be at least the time of a typical autoflash, see comment for this function above
		if( fake_precapture_use_flash_time_ms != -1 && time_now - fake_precapture_use_flash_time_ms < cache_time_ms ) {
			if( MyDebug.LOG )
				Log.d(TAG, "use recent decision: " + fake_precapture_use_flash);
			fake_precapture_use_flash_time_ms = time_now;
			return fake_precapture_use_flash;
		}
		switch(camera_settings.flash_value) {
			case "flash_auto":
				fake_precapture_use_flash = is_flash_required;
				break;
			case "flash_frontscreen_auto":
				// iso_threshold fine-tuned for Nexus 6 - front camera ISO never goes above 805, but a threshold of 700 is too low
				int iso_threshold = camera_settings.flash_value.equals("flash_frontscreen_auto") ? 750 : 1000;
				fake_precapture_use_flash = capture_result_has_iso && capture_result_iso >= iso_threshold;
				if(MyDebug.LOG)
					Log.d(TAG, "    ISO was: " + capture_result_iso);
				break;
			default:
				// shouldn't really be calling this function if not flash auto...
				fake_precapture_use_flash = false;
				break;
		}
		if( MyDebug.LOG )
			Log.d(TAG, "fake_precapture_use_flash: " + fake_precapture_use_flash);
		// We only cache the result if we decide to turn on torch, as that mucks up our ability to tell if we need the flash (since once the torch
		// is on, the ae_state thinks it's bright enough to not need flash!)
		// But if we don't turn on torch, this problem doesn't occur, so no need to cache - and good that the next time we should make an up-to-date
		// decision.
		if( fake_precapture_use_flash ) {
			fake_precapture_use_flash_time_ms = time_now;
		}
		else {
			fake_precapture_use_flash_time_ms = -1;
		}
		return fake_precapture_use_flash;
	}
	
	@Override
	public void takePicture(final PictureCallback picture, final ErrorCallback error) {
		if( MyDebug.LOG )
			Log.d(TAG, "takePicture");
		if( camera == null || captureSession == null ) {
			if( MyDebug.LOG )
				Log.d(TAG, "no camera or capture session");
			error.onError();
			return;
		}
		// we store as two identical callbacks, so we can independently set each to null as the two callbacks occur
		this.jpeg_cb = picture;
		if( imageReaderRaw != null )
			this.raw_cb = picture;
		else
			this.raw_cb = null;
		this.take_picture_error_cb = error;
		this.fake_precapture_torch_performed = false; // just in case still on?
		if( !ready_for_capture ) {
			if( MyDebug.LOG )
				Log.e(TAG, "takePicture: not ready for capture!");
			//throw new RuntimeException(); // debugging
		}

		{
			if( MyDebug.LOG ) {
				Log.d(TAG, "current flash value: " + camera_settings.flash_value);
				Log.d(TAG, "use_fake_precapture_mode: " + use_fake_precapture_mode);
			}
			// Don't need precapture if flash off or torch
			// And currently has_iso manual mode doesn't support flash - but just in case that's changed later, we still probably don't want to be doing a precapture...
			if( camera_settings.has_iso || camera_settings.flash_value.equals("flash_off") || camera_settings.flash_value.equals("flash_torch") ) {
				takePictureAfterPrecapture();
			}
			else if( use_fake_precapture_mode ) {
				// fake flash auto/on mode
				// fake precapture works by turning on torch (or using a "front screen flash"), so we can't use the camera's own decision for flash auto
				// instead we check the current ISO value
				boolean auto_flash = camera_settings.flash_value.equals("flash_auto") || camera_settings.flash_value.equals("flash_frontscreen_auto");
				Integer flash_mode = previewBuilder.get(CaptureRequest.FLASH_MODE);
				if( MyDebug.LOG )
					Log.d(TAG, "flash_mode: " + flash_mode);
				if( auto_flash && !fireAutoFlash() ) {
					if( MyDebug.LOG )
						Log.d(TAG, "fake precapture flash auto: seems bright enough to not need flash");
					takePictureAfterPrecapture();
				}
				else if( flash_mode != null && flash_mode == CameraMetadata.FLASH_MODE_TORCH ) {
					if( MyDebug.LOG )
						Log.d(TAG, "fake precapture flash: torch already on (presumably from autofocus)");
					// On some devices (e.g., OnePlus 3T), if we've already turned on torch for an autofocus immediately before
					// taking the photo, ae convergence may have already occurred - so if we called runFakePrecapture(), we'd just get
					// stuck waiting for CONTROL_AE_STATE_SEARCHING which will never happen, until we hit the timeout - it works,
					// but it means taking photos is slower as we have to wait until the timeout
					// Instead we assume that ae scanning has already started, so go straight to STATE_WAITING_FAKE_PRECAPTURE_DONE,
					// which means wait until we're no longer CONTROL_AE_STATE_SEARCHING.
					// (Note, we don't want to go straight to takePictureAfterPrecapture(), as it might be that ae scanning is still
					// taking place.)
					// An alternative solution would be to switch torch off and back on again to cause ae scanning to start - but
					// at worst this is tricky to get working, and at best, taking photos would be slower.
					fake_precapture_torch_performed = true; // so we know to fire the torch when capturing
					test_fake_flash_precapture++; // for testing, should treat this same as if we did do the precapture
					state = STATE_WAITING_FAKE_PRECAPTURE_DONE;
					precapture_state_change_time_ms = System.currentTimeMillis();
				}
				else {
					runFakePrecapture();
				}
			}
			else {
				// standard flash, flash auto or on
				// note that we don't call needsFlash() (or use is_flash_required) - as if ae state is neither CONVERGED nor FLASH_REQUIRED, we err on the side
				// of caution and don't skip the precapture
				//boolean needs_flash = capture_result_ae != null && capture_result_ae == CaptureResult.CONTROL_AE_STATE_FLASH_REQUIRED;
				boolean needs_flash = capture_result_ae != null && capture_result_ae != CaptureResult.CONTROL_AE_STATE_CONVERGED;
				if( camera_settings.flash_value.equals("flash_auto") && !needs_flash ) {
					// if we call precapture anyway, flash wouldn't fire - but we tend to have a pause
					// so skipping the precapture if flash isn't going to fire makes this faster
					if( MyDebug.LOG )
						Log.d(TAG, "flash auto, but we don't need flash");
					takePictureAfterPrecapture();
				}
				else {
					runPrecapture();
				}
			}
		}

		/*camera_settings.setupBuilder(previewBuilder, false);
    	previewBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER, CameraMetadata.CONTROL_AF_TRIGGER_START);
		state = STATE_WAITING_AUTOFOCUS;
		precapture_started = -1;
    	//capture();
    	setRepeatingRequest();*/
	}

	@Override
	public void setDisplayOrientation(int degrees) {
		// for CameraController2, the preview display orientation is handled via the TextureView's transform
		if( MyDebug.LOG )
			Log.d(TAG, "setDisplayOrientation not supported by this API");
		throw new RuntimeException(); // throw as RuntimeException, as this is a programming error
	}

	@Override
	public int getDisplayOrientation() {
		if( MyDebug.LOG )
			Log.d(TAG, "getDisplayOrientation not supported by this API");
		throw new RuntimeException(); // throw as RuntimeException, as this is a programming error
	}

	@Override
	public int getCameraOrientation() {
		return characteristics.get(CameraCharacteristics.SENSOR_ORIENTATION);
	}

	@Override
	public boolean isFrontFacing() {
		return characteristics.get(CameraCharacteristics.LENS_FACING) == CameraCharacteristics.LENS_FACING_FRONT;
	}

	@Override
	public void unlock() {
		// do nothing at this stage
	}

	@Override
	public void reconnect() throws CameraControllerException {
		if( MyDebug.LOG )
			Log.d(TAG, "reconnect");
		// if we change where we play the STOP_VIDEO_RECORDING sound, make sure it can't be heard in resultant video
		if( sounds_enabled )
			media_action_sound.play(MediaActionSound.STOP_VIDEO_RECORDING);
		createPreviewRequest();
		createCaptureSession(null);
		/*if( MyDebug.LOG )
			Log.d(TAG, "add preview surface to previewBuilder");
    	Surface surface = getPreviewSurface();
		previewBuilder.addTarget(surface);*/
		//setRepeatingRequest();
	}

	@Override
	public String getParametersString() {
		return null;
	}

	@Override
	public boolean captureResultIsAEScanning() {
		return capture_result_is_ae_scanning;
	}

	@Override
	public boolean needsFlash() {
		//boolean needs_flash = capture_result_ae != null && capture_result_ae == CaptureResult.CONTROL_AE_STATE_FLASH_REQUIRED;
		//return needs_flash;
		return is_flash_required;
	}

	@Override
	public boolean captureResultHasWhiteBalanceTemperature() {
		return capture_result_has_white_balance_rggb;
	}

	@Override
	public int captureResultWhiteBalanceTemperature() {
		// for performance reasons, we don't convert from rggb to temperature in every frame, rather only when requested
		int temperature = convertRggbToTemperature(capture_result_white_balance_rggb);
		return temperature;
	}

	@Override
	public boolean captureResultHasIso() {
		return capture_result_has_iso;
	}

	@Override
	public int captureResultIso() {
		return capture_result_iso;
	}
	
	@Override
	public boolean captureResultHasExposureTime() {
		return capture_result_has_exposure_time;
	}

	@Override
	public long captureResultExposureTime() {
		return capture_result_exposure_time;
	}

	/*
	@Override
	public boolean captureResultHasFrameDuration() {
		return capture_result_has_frame_duration;
	}

	@Override
	public long captureResultFrameDuration() {
		return capture_result_frame_duration;
	}
	
	@Override
	public boolean captureResultHasFocusDistance() {
		return capture_result_has_focus_distance;
	}

	@Override
	public float captureResultFocusDistanceMin() {
		return capture_result_focus_distance_min;
	}

	@Override
	public float captureResultFocusDistanceMax() {
		return capture_result_focus_distance_max;
	}
	*/

	private final CameraCaptureSession.CaptureCallback previewCaptureCallback = new CameraCaptureSession.CaptureCallback() {
		private long last_process_frame_number = 0;
		private int last_af_state = -1;

		@Override
		public void onCaptureBufferLost(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull Surface target, long frameNumber) {
			if( MyDebug.LOG )
				Log.d(TAG, "onCaptureBufferLost: " + frameNumber);
			super.onCaptureBufferLost(session, request, target, frameNumber);
		}

		@Override
		public void onCaptureFailed(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull CaptureFailure failure) {
			if( MyDebug.LOG )
				Log.d(TAG, "onCaptureFailed: " + failure);
			super.onCaptureFailed(session, request, failure); // API docs say this does nothing, but call it just to be safe
		}

		@Override
		public void onCaptureSequenceAborted(@NonNull CameraCaptureSession session, int sequenceId) {
			if( MyDebug.LOG ) {
				Log.d(TAG, "onCaptureSequenceAborted");
				Log.d(TAG, "sequenceId: " + sequenceId);
			}
			super.onCaptureSequenceAborted(session, sequenceId); // API docs say this does nothing, but call it just to be safe
		}

		@Override
		public void onCaptureSequenceCompleted(@NonNull CameraCaptureSession session, int sequenceId, long frameNumber) {
			if( MyDebug.LOG ) {
				Log.d(TAG, "onCaptureSequenceCompleted");
				Log.d(TAG, "sequenceId: " + sequenceId);
				Log.d(TAG, "frameNumber: " + frameNumber);
			}
			super.onCaptureSequenceCompleted(session, sequenceId, frameNumber); // API docs say this does nothing, but call it just to be safe
		}

		@Override
		public void onCaptureStarted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, long timestamp, long frameNumber) {
			if( request.getTag() == RequestTag.CAPTURE ) {
				if( MyDebug.LOG ) {
					Log.d(TAG, "onCaptureStarted: capture");
					Log.d(TAG, "frameNumber: " + frameNumber);
					Log.d(TAG, "exposure time: " + request.get(CaptureRequest.SENSOR_EXPOSURE_TIME));
				}
				// n.b., we don't play the shutter sound here, as it typically sounds "too late"
				// (if ever we changed this, would also need to fix for burst, where we only set the RequestTag.CAPTURE for the last image)
			}
			super.onCaptureStarted(session, request, timestamp, frameNumber);
		}

		@Override
		public void onCaptureProgressed(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull CaptureResult partialResult) {
			/*if( MyDebug.LOG )
				Log.d(TAG, "onCaptureProgressed");*/
			//process(request, partialResult);
			// Note that we shouldn't try to process partial results - or if in future we decide to, remember that it's documented that
			// not all results may be available. E.g., OnePlus 3T on Android 7 (OxygenOS 4.0.2) reports null for AF_STATE from this method.
			// We'd also need to fix up the discarding of old frames in process(), as we probably don't want to be discarding the
			// complete results from onCaptureCompleted()!
			super.onCaptureProgressed(session, request, partialResult); // API docs say this does nothing, but call it just to be safe (as with Google Camera)
		}

		@Override
		public void onCaptureCompleted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull TotalCaptureResult result) {
			/*if( MyDebug.LOG )
				Log.d(TAG, "onCaptureCompleted");*/
			if( request.getTag() == RequestTag.CAPTURE ) {
				if( MyDebug.LOG ) {
					Log.d(TAG, "onCaptureCompleted: capture");
					Log.d(TAG, "sequenceId: " + result.getSequenceId());
					Log.d(TAG, "frameNumber: " + result.getFrameNumber());
					Log.d(TAG, "exposure time: " + request.get(CaptureRequest.SENSOR_EXPOSURE_TIME));
				}
			}
			process(request, result);
			processCompleted(request, result);
			super.onCaptureCompleted(session, request, result); // API docs say this does nothing, but call it just to be safe (as with Google Camera)
		}

		/** Processes either a partial or total result.
		 */
		private void process(CaptureRequest request, CaptureResult result) {
			/*if( MyDebug.LOG )
			Log.d(TAG, "process, state: " + state);*/
			if( result.getFrameNumber() < last_process_frame_number ) {
				/*if( MyDebug.LOG )
					Log.d(TAG, "processAF discarded outdated frame " + result.getFrameNumber() + " vs " + last_process_frame_number);*/
				return;
			}
			last_process_frame_number = result.getFrameNumber();

			/*Integer flash_mode = result.get(CaptureResult.FLASH_MODE);
			if( MyDebug.LOG ) {
				if( flash_mode == null )
					Log.d(TAG, "FLASH_MODE is null");
				else if( flash_mode == CaptureResult.FLASH_MODE_OFF )
					Log.d(TAG, "FLASH_MODE = FLASH_MODE_OFF");
				else if( flash_mode == CaptureResult.FLASH_MODE_SINGLE )
					Log.d(TAG, "FLASH_MODE = FLASH_MODE_SINGLE");
				else if( flash_mode == CaptureResult.FLASH_MODE_TORCH )
					Log.d(TAG, "FLASH_MODE = FLASH_MODE_TORCH");
				else
					Log.d(TAG, "FLASH_MODE = " + flash_mode);
			}*/

			// use Integer instead of int, so can compare to null: Google Play crashes confirmed that this can happen; Google Camera also ignores cases with null af state
			Integer af_state = result.get(CaptureResult.CONTROL_AF_STATE);
			/*if( MyDebug.LOG ) {
				if( af_state == null )
					Log.d(TAG, "CONTROL_AF_STATE is null");
				else if( af_state == CaptureResult.CONTROL_AF_STATE_INACTIVE )
					Log.d(TAG, "CONTROL_AF_STATE = CONTROL_AF_STATE_INACTIVE");
				else if( af_state == CaptureResult.CONTROL_AF_STATE_PASSIVE_SCAN )
					Log.d(TAG, "CONTROL_AF_STATE = CONTROL_AF_STATE_PASSIVE_SCAN");
				else if( af_state == CaptureResult.CONTROL_AF_STATE_PASSIVE_FOCUSED )
					Log.d(TAG, "CONTROL_AF_STATE = CONTROL_AF_STATE_PASSIVE_FOCUSED");
				else if( af_state == CaptureResult.CONTROL_AF_STATE_ACTIVE_SCAN )
					Log.d(TAG, "CONTROL_AF_STATE = CONTROL_AF_STATE_ACTIVE_SCAN");
				else if( af_state == CaptureResult.CONTROL_AF_STATE_FOCUSED_LOCKED )
					Log.d(TAG, "CONTROL_AF_STATE = CONTROL_AF_STATE_FOCUSED_LOCKED");
				else if( af_state == CaptureResult.CONTROL_AF_STATE_NOT_FOCUSED_LOCKED )
					Log.d(TAG, "CONTROL_AF_STATE = CONTROL_AF_STATE_NOT_FOCUSED_LOCKED");
				else if( af_state == CaptureResult.CONTROL_AF_STATE_PASSIVE_UNFOCUSED )
					Log.d(TAG, "CONTROL_AF_STATE = CONTROL_AF_STATE_PASSIVE_UNFOCUSED");
				else
					Log.d(TAG, "CONTROL_AF_STATE = " + af_state);
			}*/

			// CONTROL_AE_STATE can be null on some devices, so as with af_state, use Integer
			Integer ae_state = result.get(CaptureResult.CONTROL_AE_STATE);
			/*if( MyDebug.LOG ) {
				if( ae_state == null )
					Log.d(TAG, "CONTROL_AE_STATE is null");
				else if( ae_state == CaptureResult.CONTROL_AE_STATE_INACTIVE )
					Log.d(TAG, "CONTROL_AE_STATE = CONTROL_AE_STATE_INACTIVE");
				else if( ae_state == CaptureResult.CONTROL_AE_STATE_SEARCHING )
					Log.d(TAG, "CONTROL_AE_STATE = CONTROL_AE_STATE_SEARCHING");
				else if( ae_state == CaptureResult.CONTROL_AE_STATE_CONVERGED )
					Log.d(TAG, "CONTROL_AE_STATE = CONTROL_AE_STATE_CONVERGED");
				else if( ae_state == CaptureResult.CONTROL_AE_STATE_LOCKED )
					Log.d(TAG, "CONTROL_AE_STATE = CONTROL_AE_STATE_LOCKED");
				else if( ae_state == CaptureResult.CONTROL_AE_STATE_FLASH_REQUIRED )
					Log.d(TAG, "CONTROL_AE_STATE = CONTROL_AE_STATE_FLASH_REQUIRED");
				else if( ae_state == CaptureResult.CONTROL_AE_STATE_PRECAPTURE )
					Log.d(TAG, "CONTROL_AE_STATE = CONTROL_AE_STATE_PRECAPTURE");
				else
					Log.d(TAG, "CONTROL_AE_STATE = " + ae_state);
			}*/
			Integer flash_mode = result.get(CaptureResult.FLASH_MODE);
			if( use_fake_precapture_mode && ( fake_precapture_torch_focus_performed || fake_precapture_torch_performed ) && flash_mode != null && flash_mode == CameraMetadata.FLASH_MODE_TORCH ) {
				// don't change ae state while torch is on for fake flash
			}
			else if( ae_state == null ) {
				capture_result_ae = null;
				is_flash_required = false;
			}
			else if( !ae_state.equals(capture_result_ae) ) {
				// need to store this before calling the autofocus callbacks below
				if( MyDebug.LOG )
					Log.d(TAG, "CONTROL_AE_STATE changed from " + capture_result_ae + " to " + ae_state);
				capture_result_ae = ae_state;
				// capture_result_ae should always be non-null here, as we've already handled ae_state separately
				if( capture_result_ae == CaptureResult.CONTROL_AE_STATE_FLASH_REQUIRED && !is_flash_required ) {
					is_flash_required = true;
					if( MyDebug.LOG )
						Log.d(TAG, "flash now required");
				}
				else if( capture_result_ae == CaptureResult.CONTROL_AE_STATE_CONVERGED && is_flash_required ) {
					is_flash_required = false;
					if( MyDebug.LOG )
						Log.d(TAG, "flash no longer required");
				}
			}

			if( af_state != null && af_state == CaptureResult.CONTROL_AF_STATE_PASSIVE_SCAN ) {
				/*if( MyDebug.LOG )
					Log.d(TAG, "not ready for capture: " + af_state);*/
				ready_for_capture = false;
			}
			else {
				/*if( MyDebug.LOG )
					Log.d(TAG, "ready for capture: " + af_state);*/
				ready_for_capture = true;
				if( autofocus_cb != null && (!do_af_trigger_for_continuous || use_fake_precapture_mode) && focusIsContinuous() ) {
					Integer focus_mode = previewBuilder.get(CaptureRequest.CONTROL_AF_MODE);
					if( focus_mode != null && focus_mode == CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE ) {
						if( MyDebug.LOG )
							Log.d(TAG, "call autofocus callback, as continuous mode and not focusing: " + af_state);
						// need to check af_state != null, I received Google Play crash in 1.33 where it was null
						boolean focus_success = af_state != null && ( af_state == CaptureResult.CONTROL_AF_STATE_FOCUSED_LOCKED || af_state == CaptureResult.CONTROL_AF_STATE_PASSIVE_FOCUSED );
						if( MyDebug.LOG ) {
							if( focus_success )
								Log.d(TAG, "autofocus success");
							else
								Log.d(TAG, "autofocus failed");
							if( af_state == null )
								Log.e(TAG, "continuous focus mode but af_state is null");
							else
								Log.d(TAG, "af_state: " + af_state);
						}
						if( af_state == null ) {
							test_af_state_null_focus++;
						}
						autofocus_cb.onAutoFocus(focus_success);
						autofocus_cb = null;
						capture_follows_autofocus_hint = false;
					}
				}
			}

			/*if( MyDebug.LOG ) {
				if( autofocus_cb == null ) {
					if( af_state == CaptureResult.CONTROL_AF_STATE_FOCUSED_LOCKED )
						Log.d(TAG, "processAF: autofocus success but no callback set");
					else if( af_state == CaptureResult.CONTROL_AF_STATE_NOT_FOCUSED_LOCKED )
						Log.d(TAG, "processAF: autofocus failed but no callback set");
				}
			}*/

			if( ae_state != null && ae_state == CaptureResult.CONTROL_AE_STATE_SEARCHING ) {
				/*if( MyDebug.LOG && !capture_result_is_ae_scanning )
					Log.d(TAG, "ae_state now searching");*/
				capture_result_is_ae_scanning = true;
			}
			else {
				/*if( MyDebug.LOG && capture_result_is_ae_scanning )
					Log.d(TAG, "ae_state stopped searching");*/
				capture_result_is_ae_scanning = false;
			}

			/*Integer awb_state = result.get(CaptureResult.CONTROL_AWB_STATE);
			if( MyDebug.LOG ) {
				if( awb_state == null )
					Log.d(TAG, "CONTROL_AWB_STATE is null");
				else if( awb_state == CaptureResult.CONTROL_AWB_STATE_INACTIVE )
					Log.d(TAG, "CONTROL_AWB_STATE = CONTROL_AWB_STATE_INACTIVE");
				else if( awb_state == CaptureResult.CONTROL_AWB_STATE_SEARCHING )
					Log.d(TAG, "CONTROL_AWB_STATE = CONTROL_AWB_STATE_SEARCHING");
				else if( awb_state == CaptureResult.CONTROL_AWB_STATE_CONVERGED )
					Log.d(TAG, "CONTROL_AWB_STATE = CONTROL_AWB_STATE_CONVERGED");
				else if( awb_state == CaptureResult.CONTROL_AWB_STATE_LOCKED )
					Log.d(TAG, "CONTROL_AWB_STATE = CONTROL_AWB_STATE_LOCKED");
				else
					Log.d(TAG, "CONTROL_AWB_STATE = " + awb_state);
			}*/

			if( fake_precapture_turn_on_torch_id != null && fake_precapture_turn_on_torch_id == request ) {
				if( MyDebug.LOG )
					Log.d(TAG, "torch turned on for fake precapture");
				fake_precapture_turn_on_torch_id = null;
			}

			if( state == STATE_NORMAL ) {
				// do nothing
			}
			else if( state == STATE_WAITING_AUTOFOCUS ) {
				if( af_state == null ) {
					// autofocus shouldn't really be requested if af not available, but still allow this rather than getting stuck waiting for autofocus to complete
					if( MyDebug.LOG )
						Log.e(TAG, "waiting for autofocus but af_state is null");
					test_af_state_null_focus++;
					state = STATE_NORMAL;
			    	precapture_state_change_time_ms = -1;
					if( autofocus_cb != null ) {
						autofocus_cb.onAutoFocus(false);
						autofocus_cb = null;
					}
					capture_follows_autofocus_hint = false;
				}
				else if( af_state != last_af_state ) {
					// check for autofocus completing
					// need to check that af_state != last_af_state, except for continuous focus mode where if we're already focused, should return immediately
					if( af_state == CaptureResult.CONTROL_AF_STATE_FOCUSED_LOCKED || af_state == CaptureResult.CONTROL_AF_STATE_NOT_FOCUSED_LOCKED /*||
							af_state == CaptureResult.CONTROL_AF_STATE_PASSIVE_FOCUSED || af_state == CaptureResult.CONTROL_AF_STATE_PASSIVE_UNFOCUSED*/
							) {
						boolean focus_success = af_state == CaptureResult.CONTROL_AF_STATE_FOCUSED_LOCKED || af_state == CaptureResult.CONTROL_AF_STATE_PASSIVE_FOCUSED;
						if( MyDebug.LOG ) {
							if( focus_success )
								Log.d(TAG, "onCaptureCompleted: autofocus success");
							else
								Log.d(TAG, "onCaptureCompleted: autofocus failed");
							Log.d(TAG, "af_state: " + af_state);
						}
						state = STATE_NORMAL;
				    	precapture_state_change_time_ms = -1;
						if( use_fake_precapture_mode && fake_precapture_torch_focus_performed ) {
							fake_precapture_torch_focus_performed = false;
							if( !capture_follows_autofocus_hint ) {
								// If we're going to be taking a photo immediately after the autofocus, it's better for the fake flash
								// mode to leave the torch on. If we don't do this, one of the following issues can happen:
								// - On OnePlus 3T, the torch doesn't get turned off, but because we've switched off the torch flag
								//   in previewBuilder, we go ahead with the precapture routine instead of
								if( MyDebug.LOG )
									Log.d(TAG, "turn off torch after focus (fake precapture code)");

								// same hack as in setFlashValue() - for fake precapture we need to turn off the torch mode that was set, but
								// at least on Nexus 6, we need to turn to flash_off to turn off the torch!
								String saved_flash_value = camera_settings.flash_value;
								camera_settings.flash_value = "flash_off";
								camera_settings.setAEMode(previewBuilder, false);
								try {
									capture();
								}
								catch(CameraAccessException e) {
									if( MyDebug.LOG ) {
										Log.e(TAG, "failed to do capture to turn off torch after autofocus");
										Log.e(TAG, "reason: " + e.getReason());
										Log.e(TAG, "message: " + e.getMessage());
									}
									e.printStackTrace();
								}

								// now set the actual (should be flash auto or flash on) mode
								camera_settings.flash_value = saved_flash_value;
								camera_settings.setAEMode(previewBuilder, false);
								try {
									setRepeatingRequest();
								}
								catch(CameraAccessException e) {
									if( MyDebug.LOG ) {
										Log.e(TAG, "failed to set repeating request to turn off torch after autofocus");
										Log.e(TAG, "reason: " + e.getReason());
										Log.e(TAG, "message: " + e.getMessage());
									}
									e.printStackTrace();
								}
							}
							else {
								if( MyDebug.LOG )
									Log.d(TAG, "torch was enabled for autofocus, leave it on for capture (fake precapture code)");
							}
						}
						if( autofocus_cb != null ) {
							autofocus_cb.onAutoFocus(focus_success);
							autofocus_cb = null;
						}
						capture_follows_autofocus_hint = false;
					}
				}
			}
			else if( state == STATE_WAITING_PRECAPTURE_START ) {
				if( MyDebug.LOG )
					Log.d(TAG, "waiting for precapture start...");
				if( MyDebug.LOG ) {
					if( ae_state != null )
						Log.d(TAG, "CONTROL_AE_STATE = " + ae_state);
					else
						Log.d(TAG, "CONTROL_AE_STATE is null");
				}
				if( ae_state == null || ae_state == CaptureResult.CONTROL_AE_STATE_PRECAPTURE /*|| ae_state == CaptureResult.CONTROL_AE_STATE_FLASH_REQUIRED*/ ) {
					// we have to wait for CONTROL_AE_STATE_PRECAPTURE; if we allow CONTROL_AE_STATE_FLASH_REQUIRED, then on Nexus 6 at least we get poor quality results with flash:
					// varying levels of brightness, sometimes too bright or too dark, sometimes with blue tinge, sometimes even with green corruption
					// similarly photos with flash come out too dark on OnePlus 3T
					if( MyDebug.LOG ) {
						Log.d(TAG, "precapture started after: " + (System.currentTimeMillis() - precapture_state_change_time_ms));
					}
					state = STATE_WAITING_PRECAPTURE_DONE;
					precapture_state_change_time_ms = System.currentTimeMillis();
				}
				else if( precapture_state_change_time_ms != -1 && System.currentTimeMillis() - precapture_state_change_time_ms > precapture_start_timeout_c ) {
					// hack - give up waiting - sometimes we never get a CONTROL_AE_STATE_PRECAPTURE so would end up stuck
					// always log error, so we can look for it when manually testing with logging disabled
					Log.e(TAG, "precapture start timeout");
					count_precapture_timeout++;
					state = STATE_WAITING_PRECAPTURE_DONE;
					precapture_state_change_time_ms = System.currentTimeMillis();
				}
			}
			else if( state == STATE_WAITING_PRECAPTURE_DONE ) {
				if( MyDebug.LOG )
					Log.d(TAG, "waiting for precapture done...");
				if( MyDebug.LOG ) {
					if( ae_state != null )
						Log.d(TAG, "CONTROL_AE_STATE = " + ae_state);
					else
						Log.d(TAG, "CONTROL_AE_STATE is null");
				}
				if( ae_state == null || ae_state != CaptureResult.CONTROL_AE_STATE_PRECAPTURE ) {
					if( MyDebug.LOG ) {
						Log.d(TAG, "precapture completed after: " + (System.currentTimeMillis() - precapture_state_change_time_ms));
					}
					state = STATE_NORMAL;
					precapture_state_change_time_ms = -1;
					takePictureAfterPrecapture();
				}
				else if( precapture_state_change_time_ms != -1 && System.currentTimeMillis() - precapture_state_change_time_ms > precapture_done_timeout_c ) {
					// just in case
					// always log error, so we can look for it when manually testing with logging disabled
					Log.e(TAG, "precapture done timeout");
					count_precapture_timeout++;
					state = STATE_NORMAL;
					precapture_state_change_time_ms = -1;
					takePictureAfterPrecapture();
				}
			}
			else if( state == STATE_WAITING_FAKE_PRECAPTURE_START ) {
				if( MyDebug.LOG )
					Log.d(TAG, "waiting for fake precapture start...");
				if( MyDebug.LOG ) {
					if( ae_state != null )
						Log.d(TAG, "CONTROL_AE_STATE = " + ae_state);
					else
						Log.d(TAG, "CONTROL_AE_STATE is null");
				}
				if( fake_precapture_turn_on_torch_id != null ) {
					if( MyDebug.LOG )
						Log.d(TAG, "still waiting for torch to come on for fake precapture");
				}

				if( fake_precapture_turn_on_torch_id == null && (ae_state == null || ae_state == CaptureResult.CONTROL_AE_STATE_SEARCHING) ) {
					if( MyDebug.LOG ) {
						Log.d(TAG, "fake precapture started after: " + (System.currentTimeMillis() - precapture_state_change_time_ms));
					}
					state = STATE_WAITING_FAKE_PRECAPTURE_DONE;
					precapture_state_change_time_ms = System.currentTimeMillis();
				}
				else if( precapture_state_change_time_ms != -1 && System.currentTimeMillis() - precapture_state_change_time_ms > precapture_start_timeout_c ) {
					// just in case
					// always log error, so we can look for it when manually testing with logging disabled
					Log.e(TAG, "fake precapture start timeout");
					count_precapture_timeout++;
					state = STATE_WAITING_FAKE_PRECAPTURE_DONE;
					precapture_state_change_time_ms = System.currentTimeMillis();
					fake_precapture_turn_on_torch_id = null;
				}
			}
			else if( state == STATE_WAITING_FAKE_PRECAPTURE_DONE ) {
				if( MyDebug.LOG )
					Log.d(TAG, "waiting for fake precapture done...");
				if( MyDebug.LOG ) {
					if( ae_state != null )
						Log.d(TAG, "CONTROL_AE_STATE = " + ae_state);
					else
						Log.d(TAG, "CONTROL_AE_STATE is null");
					Log.d(TAG, "ready_for_capture? " + ready_for_capture);
				}
				// wait for af and ae scanning to end (need to check af too, as in continuous focus mode, a focus may start again after switching torch on for the fake precapture)
				if( ready_for_capture && ( ae_state == null || ae_state != CaptureResult.CONTROL_AE_STATE_SEARCHING)  ) {
					if( MyDebug.LOG ) {
						Log.d(TAG, "fake precapture completed after: " + (System.currentTimeMillis() - precapture_state_change_time_ms));
					}
					state = STATE_NORMAL;
					precapture_state_change_time_ms = -1;
					takePictureAfterPrecapture();
				}
				else if( precapture_state_change_time_ms != -1 && System.currentTimeMillis() - precapture_state_change_time_ms > precapture_done_timeout_c ) {
					// sometimes camera can take a while to stop ae/af scanning, better to just go ahead and take photo
					// always log error, so we can look for it when manually testing with logging disabled
					Log.e(TAG, "fake precapture done timeout");
					count_precapture_timeout++;
					state = STATE_NORMAL;
					precapture_state_change_time_ms = -1;
					takePictureAfterPrecapture();
				}
			}

			if( af_state != null && af_state == CaptureResult.CONTROL_AF_STATE_PASSIVE_SCAN && af_state != last_af_state ) {
				if( MyDebug.LOG )
					Log.d(TAG, "continuous focusing started");
				if( continuous_focus_move_callback != null ) {
					continuous_focus_move_callback.onContinuousFocusMove(true);
				}
			}
			else if( af_state != null && last_af_state == CaptureResult.CONTROL_AF_STATE_PASSIVE_SCAN && af_state != last_af_state ) {
				if( MyDebug.LOG )
					Log.d(TAG, "continuous focusing stopped");
				if( continuous_focus_move_callback != null ) {
					continuous_focus_move_callback.onContinuousFocusMove(false);
				}
			}

			if( af_state != null && af_state != last_af_state ) {
				if( MyDebug.LOG )
					Log.d(TAG, "CONTROL_AF_STATE changed from " + last_af_state + " to " + af_state);
				last_af_state = af_state;
			}
		}
		
		/** Processes a total result.
		 */
		private void processCompleted(CaptureRequest request, CaptureResult result) {
			/*if( MyDebug.LOG )
				Log.d(TAG, "processCompleted");*/

			if( result.get(CaptureResult.SENSOR_SENSITIVITY) != null ) {
				capture_result_has_iso = true;
				capture_result_iso = result.get(CaptureResult.SENSOR_SENSITIVITY);
				/*if( MyDebug.LOG )
					Log.d(TAG, "capture_result_iso: " + capture_result_iso);*/
				if( camera_settings.has_iso && Math.abs(camera_settings.iso - capture_result_iso) > 10  ) {
					// ugly hack: problem (on Nexus 6 at least) that when we start recording video (video_recorder.start() call), this often causes the ISO setting to reset to the wrong value!
					// seems to happen more often with shorter exposure time
					// seems to happen on other camera apps with Camera2 API too
					// update: allow some tolerance, as on OnePlus 3T it's normal to have some slight difference between requested and actual
					// this workaround still means a brief flash with incorrect ISO, but is best we can do for now!
					if( MyDebug.LOG ) {
						Log.d(TAG, "ISO " + capture_result_iso + " different to requested ISO " + camera_settings.iso);
						Log.d(TAG, "    requested ISO was: " + request.get(CaptureRequest.SENSOR_SENSITIVITY));
						Log.d(TAG, "    requested AE mode was: " + request.get(CaptureRequest.CONTROL_AE_MODE));
					}
					try {
						setRepeatingRequest();
					}
					catch(CameraAccessException e) {
						if( MyDebug.LOG ) {
							Log.e(TAG, "failed to set repeating request after ISO hack");
							Log.e(TAG, "reason: " + e.getReason());
							Log.e(TAG, "message: " + e.getMessage());
						}
						e.printStackTrace();
					} 
				}
			}
			else {
				capture_result_has_iso = false;
			}
			if( result.get(CaptureResult.SENSOR_EXPOSURE_TIME) != null ) {
				capture_result_has_exposure_time = true;
				capture_result_exposure_time = result.get(CaptureResult.SENSOR_EXPOSURE_TIME);
			}
			else {
				capture_result_has_exposure_time = false;
			}
			if( result.get(CaptureResult.SENSOR_FRAME_DURATION) != null ) {
				capture_result_has_frame_duration = true;
				capture_result_frame_duration = result.get(CaptureResult.SENSOR_FRAME_DURATION);
			}
			else {
				capture_result_has_frame_duration = false;
			}
			/*if( MyDebug.LOG ) {
				if( result.get(CaptureResult.SENSOR_EXPOSURE_TIME) != null ) {
					long capture_result_exposure_time = result.get(CaptureResult.SENSOR_EXPOSURE_TIME);
					Log.d(TAG, "capture_result_exposure_time: " + capture_result_exposure_time);
				}
				if( result.get(CaptureResult.SENSOR_FRAME_DURATION) != null ) {
					long capture_result_frame_duration = result.get(CaptureResult.SENSOR_FRAME_DURATION);
					Log.d(TAG, "capture_result_frame_duration: " + capture_result_frame_duration);
				}
			}*/
			/*if( result.get(CaptureResult.LENS_FOCUS_RANGE) != null ) {
				Pair<Float, Float> focus_range = result.get(CaptureResult.LENS_FOCUS_RANGE);
				capture_result_has_focus_distance = true;
				capture_result_focus_distance_min = focus_range.first;
				capture_result_focus_distance_max = focus_range.second;
			}
			else {
				capture_result_has_focus_distance = false;
			}*/
			{
				RggbChannelVector vector = result.get(CaptureResult.COLOR_CORRECTION_GAINS);
				if( vector != null ) {
					capture_result_has_white_balance_rggb = true;
					capture_result_white_balance_rggb = vector;
				}
			}

			/*if( MyDebug.LOG ) {
				RggbChannelVector vector = result.get(CaptureResult.COLOR_CORRECTION_GAINS);
				if( vector != null ) {
					convertRggbToTemperature(vector); // logging will occur in this function
				}
			}*/

			if( face_detection_listener != null && previewBuilder != null && previewBuilder.get(CaptureRequest.STATISTICS_FACE_DETECT_MODE) != null && previewBuilder.get(CaptureRequest.STATISTICS_FACE_DETECT_MODE) != CaptureRequest.STATISTICS_FACE_DETECT_MODE_OFF ) {
				Rect sensor_rect = getViewableRect();
				android.hardware.camera2.params.Face [] camera_faces = result.get(CaptureResult.STATISTICS_FACES);
				if( camera_faces != null ) {
					CameraController.Face [] faces = new CameraController.Face[camera_faces.length];
					for(int i=0;i<camera_faces.length;i++) {
						faces[i] = convertFromCameraFace(sensor_rect, camera_faces[i]);
					}
					face_detection_listener.onFaceDetection(faces);
				}
			}

			if( push_repeating_request_when_torch_off && push_repeating_request_when_torch_off_id == request ) {
				if( MyDebug.LOG )
					Log.d(TAG, "received push_repeating_request_when_torch_off");
				Integer flash_state = result.get(CaptureResult.FLASH_STATE);
				if( MyDebug.LOG ) {
					if( flash_state != null )
						Log.d(TAG, "flash_state: " + flash_state);
					else
						Log.d(TAG, "flash_state is null");
				}
				if( flash_state != null && flash_state == CaptureResult.FLASH_STATE_READY ) {
					push_repeating_request_when_torch_off = false;
					push_repeating_request_when_torch_off_id = null;
					try {
						setRepeatingRequest();
					}
					catch(CameraAccessException e) {
						if( MyDebug.LOG ) {
							Log.e(TAG, "failed to set flash [from torch/flash off hack]");
							Log.e(TAG, "reason: " + e.getReason());
							Log.e(TAG, "message: " + e.getMessage());
						}
						e.printStackTrace();
					} 
				}
			}
			/*if( push_set_ae_lock && push_set_ae_lock_id == request ) {
				if( MyDebug.LOG )
					Log.d(TAG, "received push_set_ae_lock");
				// hack - needed to fix bug on Nexus 6 where auto-exposure sometimes locks when taking a photo of bright scene with flash on!
            	// this doesn't completely resolve the issue, but seems to make it far less common; also when it does happen, taking another photo usually fixes it
				push_set_ae_lock = false;
				push_set_ae_lock_id = null;
				camera_settings.setAutoExposureLock(previewBuilder);
				try {
					setRepeatingRequest();
				}
				catch(CameraAccessException e) {
					if( MyDebug.LOG ) {
						Log.e(TAG, "failed to set ae lock [from ae lock hack]");
						Log.e(TAG, "reason: " + e.getReason());
						Log.e(TAG, "message: " + e.getMessage());
					}
					e.printStackTrace();
				} 
			}*/
			
			if( request.getTag() == RequestTag.CAPTURE ) {
				if( MyDebug.LOG )
					Log.d(TAG, "capture request completed");
				test_capture_results++;
				if( onRawImageAvailableListener != null ) {
					if( test_wait_capture_result ) {
						// for RAW capture, we require the capture result before creating DngCreator
						// but for testing purposes, we need to test the possibility where onImageAvailable() for
						// the RAW image is called before we receive the capture result here
						try {
							if( MyDebug.LOG )
								Log.d(TAG, "test_wait_capture_result: waiting...");
							Thread.sleep(500); // 200ms is enough to test the problem on Nexus 6, but use 500ms to be sure
						}
						catch(InterruptedException e) {
							e.printStackTrace();
						}
					}
					onRawImageAvailableListener.setCaptureResult(result);
				}
				// actual parsing of image data is done in the imageReader's OnImageAvailableListener()
				// need to cancel the autofocus, and restart the preview after taking the photo
				// Camera2Basic does a capture then sets a repeating request - do the same here just to be safe
				previewBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER, CameraMetadata.CONTROL_AF_TRIGGER_CANCEL);
				if( MyDebug.LOG )
					Log.d(TAG, "### reset ae mode");
				String saved_flash_value = camera_settings.flash_value;
				if( use_fake_precapture_mode && fake_precapture_torch_performed ) {
					// same hack as in setFlashValue() - for fake precapture we need to turn off the torch mode that was set, but
					// at least on Nexus 6, we need to turn to flash_off to turn off the torch!
					camera_settings.flash_value = "flash_off";
				}
				// if not using fake precapture, not sure if we need to set the ae mode, but the AE mode is set again in Camera2Basic
				camera_settings.setAEMode(previewBuilder, false);
				// n.b., if capture/setRepeatingRequest throw exception, we don't call the take_picture_error_cb.onError() callback, as the photo should have been taken by this point
				try {
	            	capture();
				}
				catch(CameraAccessException e) {
					if( MyDebug.LOG ) {
						Log.e(TAG, "failed to cancel autofocus after taking photo");
						Log.e(TAG, "reason: " + e.getReason());
						Log.e(TAG, "message: " + e.getMessage());
					}
					e.printStackTrace();
				}
				if( use_fake_precapture_mode && fake_precapture_torch_performed ) {
					// now set up the request to switch to the correct flash value
			    	camera_settings.flash_value = saved_flash_value;
					camera_settings.setAEMode(previewBuilder, false);
				}
				previewBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER, CameraMetadata.CONTROL_AF_TRIGGER_IDLE); // ensure set back to idle
				try {
					setRepeatingRequest();
				}
				catch(CameraAccessException e) {
					if( MyDebug.LOG ) {
						Log.e(TAG, "failed to start preview after taking photo");
						Log.e(TAG, "reason: " + e.getReason());
						Log.e(TAG, "message: " + e.getMessage());
					}
					e.printStackTrace();
					preview_error_cb.onError();
				}
				fake_precapture_torch_performed = false;
			}
		}
	};
}
