package org.fossasia.phimpme.opencamera.CameraController;

import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusMoveCallback;
import android.location.Location;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.view.SurfaceHolder;

import org.fossasia.phimpme.opencamera.Camera.MyDebug;
import org.fossasia.phimpme.opencamera.UI.PopupView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/** Provides support using Android's original camera API
 *  android.hardware.Camera.
 */
@SuppressWarnings("deprecation")
public class CameraController1 extends CameraController {
	private static final String TAG = "CameraController1";

	private Camera camera;
    private int display_orientation;
    private final Camera.CameraInfo camera_info = new Camera.CameraInfo();
	private String iso_key;
	private boolean frontscreen_flash;
	private final ErrorCallback camera_error_cb;

	private int n_burst; // number of expected burst images in this capture
	private final List<byte []> pending_burst_images = new ArrayList<>(); // burst images that have been captured so far, but not yet sent to the application
	private List<Integer> burst_exposures;
	private boolean want_expo_bracketing;
	private final static int max_expo_bracketing_n_images = 3; // seem to have problems with 5 images in some cases, e.g., images coming out same brightness on OnePlus 3T
	private int expo_bracketing_n_images = 3;
	private double expo_bracketing_stops = 2.0;

	/** Opens the camera device.
	 * @param cameraId Which camera to open (must be between 0 and CameraControllerManager1.getNumberOfCameras()-1).
	 * @param camera_error_cb onError() will be called if the camera closes due to serious error. No more calls to the CameraController1 object should be made (though a new one can be created, to try reopening the camera).
	 * @throws CameraControllerException if the camera device fails to open.
     */
	public CameraController1(int cameraId, final ErrorCallback camera_error_cb) throws CameraControllerException {
		super(cameraId);
		if( MyDebug.LOG )
			Log.d(TAG, "create new CameraController1: " + cameraId);
		this.camera_error_cb = camera_error_cb;
		try {
			camera = Camera.open(cameraId);
		}
		catch(RuntimeException e) {
			if( MyDebug.LOG )
				Log.e(TAG, "failed to open camera");
			e.printStackTrace();
			throw new CameraControllerException();
		}
		if( camera == null ) {
			// Although the documentation says Camera.open() should throw a RuntimeException, it seems that it some cases it can return null
			// I've seen this in some crashes reported in Google Play; also see:
			// http://stackoverflow.com/questions/12054022/camera-open-returns-null			
			if( MyDebug.LOG )
				Log.e(TAG, "camera.open returned null");
			throw new CameraControllerException();
		}
		try {
			Camera.getCameraInfo(cameraId, camera_info);
		}
		catch(RuntimeException e) {
			// Had reported RuntimeExceptions from Google Play
			// also see http://stackoverflow.com/questions/22383708/java-lang-runtimeexception-fail-to-get-camera-info
			if( MyDebug.LOG )
				Log.e(TAG, "failed to get camera info");
			e.printStackTrace();
			this.release();
			throw new CameraControllerException();
		}
		/*{
			// TEST cam_mode workaround from http://stackoverflow.com/questions/7225571/camcorderprofile-quality-high-resolution-produces-green-flickering-video
			if( MyDebug.LOG )
				Log.d(TAG, "setting cam_mode workaround");
	    	Camera.Parameters parameters = this.getParameters();
	    	parameters.set("cam_mode", 1);
	    	setCameraParameters(parameters);
		}*/

		final CameraErrorCallback camera_error_callback = new CameraErrorCallback();
		camera.setErrorCallback(camera_error_callback);

		/*{
			// test error handling
			final Handler handler = new Handler();
			handler.postDelayed(new Runnable() {
				@Override
				public void run() {
					if( MyDebug.LOG )
						Log.d(TAG, "test camera error");
					camera_error_callback.onError(Camera.CAMERA_ERROR_SERVER_DIED, camera);
				}
			}, 5000);
		}*/
	}

	@Override
	public void onError() {
		Log.e(TAG, "onError");
		if( this.camera != null ) { // I got Google Play crash reports due to camera being null in v1.36
			this.camera.release();
			this.camera = null;
		}
		if( this.camera_error_cb != null ) {
			// need to communicate the problem to the application
			this.camera_error_cb.onError();
		}
	}
	
	private class CameraErrorCallback implements Camera.ErrorCallback {
		@Override
		public void onError(int error, Camera cam) {
			// n.b., as this is potentially serious error, we always log even if MyDebug.LOG is false
			Log.e(TAG, "camera onError: " + error);
			if( error == Camera.CAMERA_ERROR_SERVER_DIED ) {
				Log.e(TAG, "    CAMERA_ERROR_SERVER_DIED");
				CameraController1.this.onError();
			}
			else if( error == Camera.CAMERA_ERROR_UNKNOWN  ) {
				Log.e(TAG, "    CAMERA_ERROR_UNKNOWN ");
			}
		}
	}
	
	public void release() {
		camera.release();
		camera = null;
	}

	private Camera.Parameters getParameters() {
		return camera.getParameters();
	}
	
	private void setCameraParameters(Camera.Parameters parameters) {
		if( MyDebug.LOG )
			Log.d(TAG, "setCameraParameters");
	    try {
			camera.setParameters(parameters);
    		if( MyDebug.LOG )
    			Log.d(TAG, "done");
	    }
	    catch(RuntimeException e) {
	    	// just in case something has gone wrong
    		if( MyDebug.LOG )
    			Log.d(TAG, "failed to set parameters");
    		e.printStackTrace();
    		count_camera_parameters_exception++;
	    }
	}
	
	private List<String> convertFlashModesToValues(List<String> supported_flash_modes) {
		if( MyDebug.LOG ) {
			Log.d(TAG, "convertFlashModesToValues()");
			Log.d(TAG, "supported_flash_modes: " + supported_flash_modes);
		}
		List<String> output_modes = new ArrayList<>();
		if( supported_flash_modes != null ) {
			// also resort as well as converting
			if( supported_flash_modes.contains(Camera.Parameters.FLASH_MODE_OFF) ) {
				output_modes.add("flash_off");
				if( MyDebug.LOG )
					Log.d(TAG, " supports flash_off");
			}
			if( supported_flash_modes.contains(Camera.Parameters.FLASH_MODE_AUTO) ) {
				output_modes.add("flash_auto");
				if( MyDebug.LOG )
					Log.d(TAG, " supports flash_auto");
			}
			if( supported_flash_modes.contains(Camera.Parameters.FLASH_MODE_ON) ) {
				output_modes.add("flash_on");
				if( MyDebug.LOG )
					Log.d(TAG, " supports flash_on");
			}
			if( supported_flash_modes.contains(Camera.Parameters.FLASH_MODE_TORCH) ) {
				output_modes.add("flash_torch");
				if( MyDebug.LOG )
					Log.d(TAG, " supports flash_torch");
			}
			if( supported_flash_modes.contains(Camera.Parameters.FLASH_MODE_RED_EYE) ) {
				output_modes.add("flash_red_eye");
				if( MyDebug.LOG )
					Log.d(TAG, " supports flash_red_eye");
			}
		}
		
		// Samsung Galaxy S7 at least for front camera has supported_flash_modes: auto, beach, portrait?!
		// so rather than checking supported_flash_modes, we should check output_modes here
		// this is always why we check whether the size is greater than 1, rather than 0 (this also matches
		// the check we do in Preview.setupCameraParameters()).
		if( output_modes.size() > 1 ) {
			if( MyDebug.LOG )
				Log.d(TAG, "flash supported");
		}
		else {
			if( isFrontFacing() ) {
				if( MyDebug.LOG )
					Log.d(TAG, "front-screen with no flash");
				output_modes.clear(); // clear any pre-existing mode (see note above about Samsung Galaxy S7)
				output_modes.add("flash_off");
				output_modes.add("flash_frontscreen_on");
			}
			else {
				if( MyDebug.LOG )
					Log.d(TAG, "no flash");
				// probably best to not return any modes, rather than one mode (see note about about Samsung Galaxy S7)
				output_modes.clear();
			}
		}

		return output_modes;
	}

	private List<String> convertFocusModesToValues(List<String> supported_focus_modes) {
		if( MyDebug.LOG )
			Log.d(TAG, "convertFocusModesToValues()");
		List<String> output_modes = new ArrayList<>();
		if( supported_focus_modes != null ) {
			// also resort as well as converting
			if( supported_focus_modes.contains(Camera.Parameters.FOCUS_MODE_AUTO) ) {
				output_modes.add("focus_mode_auto");
				if( MyDebug.LOG ) {
					Log.d(TAG, " supports focus_mode_auto");
				}
			}
			if( supported_focus_modes.contains(Camera.Parameters.FOCUS_MODE_INFINITY) ) {
				output_modes.add("focus_mode_infinity");
				if( MyDebug.LOG )
					Log.d(TAG, " supports focus_mode_infinity");
			}
			if( supported_focus_modes.contains(Camera.Parameters.FOCUS_MODE_MACRO) ) {
				output_modes.add("focus_mode_macro");
				if( MyDebug.LOG )
					Log.d(TAG, " supports focus_mode_macro");
			}
			if( supported_focus_modes.contains(Camera.Parameters.FOCUS_MODE_AUTO) ) {
				output_modes.add("focus_mode_locked");
				if( MyDebug.LOG ) {
					Log.d(TAG, " supports focus_mode_locked");
				}
			}
			if( supported_focus_modes.contains(Camera.Parameters.FOCUS_MODE_FIXED) ) {
				output_modes.add("focus_mode_fixed");
				if( MyDebug.LOG )
					Log.d(TAG, " supports focus_mode_fixed");
			}
			if( supported_focus_modes.contains(Camera.Parameters.FOCUS_MODE_EDOF) ) {
				output_modes.add("focus_mode_edof");
				if( MyDebug.LOG )
					Log.d(TAG, " supports focus_mode_edof");
			}
			if( supported_focus_modes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE) ) {
				output_modes.add("focus_mode_continuous_picture");
				if( MyDebug.LOG )
					Log.d(TAG, " supports focus_mode_continuous_picture");
			}
			if( supported_focus_modes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO) ) {
				output_modes.add("focus_mode_continuous_video");
				if( MyDebug.LOG )
					Log.d(TAG, " supports focus_mode_continuous_video");
			}
		}
		return output_modes;
	}
	
	public String getAPI() {
		return "Camera";
	}
	
	public CameraFeatures getCameraFeatures() {
		if( MyDebug.LOG )
			Log.d(TAG, "getCameraFeatures()");
	    Camera.Parameters parameters = this.getParameters();
	    CameraFeatures camera_features = new CameraFeatures();
		camera_features.is_zoom_supported = parameters.isZoomSupported();
		if( camera_features.is_zoom_supported ) {
			camera_features.max_zoom = parameters.getMaxZoom();
			try {
				camera_features.zoom_ratios = parameters.getZoomRatios();
			}
			catch(NumberFormatException e) {
        		// crash java.lang.NumberFormatException: Invalid int: " 500" reported in v1.4 on device "es209ra", Android 4.1, 3 Jan 2014
				// this is from java.lang.Integer.invalidInt(Integer.java:138) - unclear if this is a bug in Open Camera, all we can do for now is catch it
	    		if( MyDebug.LOG )
	    			Log.e(TAG, "NumberFormatException in getZoomRatios()");
				e.printStackTrace();
				camera_features.is_zoom_supported = false;
				camera_features.max_zoom = 0;
				camera_features.zoom_ratios = null;
			}
		}

		camera_features.supports_face_detection = parameters.getMaxNumDetectedFaces() > 0;

		// get available sizes
		List<Camera.Size> camera_picture_sizes = parameters.getSupportedPictureSizes();
		camera_features.picture_sizes = new ArrayList<>();
		//camera_features.picture_sizes.add(new CameraController.Size(1920, 1080)); // test
		for(Camera.Size camera_size : camera_picture_sizes) {
			camera_features.picture_sizes.add(new CameraController.Size(camera_size.width, camera_size.height));
		}

        //camera_features.supported_flash_modes = parameters.getSupportedFlashModes(); // Android format
        List<String> supported_flash_modes = parameters.getSupportedFlashModes(); // Android format
		camera_features.supported_flash_values = convertFlashModesToValues(supported_flash_modes); // convert to our format (also resorts)

        List<String> supported_focus_modes = parameters.getSupportedFocusModes(); // Android format
		camera_features.supported_focus_values = convertFocusModesToValues(supported_focus_modes); // convert to our format (also resorts)
		camera_features.max_num_focus_areas = parameters.getMaxNumFocusAreas();

        camera_features.is_exposure_lock_supported = parameters.isAutoExposureLockSupported();

        camera_features.is_video_stabilization_supported = parameters.isVideoStabilizationSupported();
        
		camera_features.supports_expo_bracketing = true;
		camera_features.max_expo_bracketing_n_images = max_expo_bracketing_n_images;
        camera_features.min_exposure = parameters.getMinExposureCompensation();
        camera_features.max_exposure = parameters.getMaxExposureCompensation();
		camera_features.exposure_step = getExposureCompensationStep();

		List<Camera.Size> camera_video_sizes = parameters.getSupportedVideoSizes();
    	if( camera_video_sizes == null ) {
    		// if null, we should use the preview sizes - see http://stackoverflow.com/questions/14263521/android-getsupportedvideosizes-allways-returns-null
    		if( MyDebug.LOG )
    			Log.d(TAG, "take video_sizes from preview sizes");
    		camera_video_sizes = parameters.getSupportedPreviewSizes();
    	}
		camera_features.video_sizes = new ArrayList<>();
		//camera_features.video_sizes.add(new CameraController.Size(1920, 1080)); // test
		for(Camera.Size camera_size : camera_video_sizes) {
			camera_features.video_sizes.add(new CameraController.Size(camera_size.width, camera_size.height));
		}

		List<Camera.Size> camera_preview_sizes = parameters.getSupportedPreviewSizes();
		camera_features.preview_sizes = new ArrayList<>();
		for(Camera.Size camera_size : camera_preview_sizes) {
			camera_features.preview_sizes.add(new CameraController.Size(camera_size.width, camera_size.height));
		}

		if( MyDebug.LOG )
			Log.d(TAG, "camera parameters: " + parameters.flatten());

		if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 ) {
        	// Camera.canDisableShutterSound requires JELLY_BEAN_MR1 or greater
        	camera_features.can_disable_shutter_sound = camera_info.canDisableShutterSound;
        }
        else {
        	camera_features.can_disable_shutter_sound = false;
        }

		// Determine view angles. Note that these can vary based on the resolution - and since we read these before the caller has
		// set the desired resolution, this isn't strictly correct. However these are presumably view angles for the photo anyway,
		// when some callers (e.g., DrawPreview) want view angles for the preview anyway - so these will only be an approximation for
		// what we want anyway.
		final float default_view_angle_x = 55.0f;
		final float default_view_angle_y = 43.0f;
		try {
			camera_features.view_angle_x = parameters.getHorizontalViewAngle();
			camera_features.view_angle_y = parameters.getVerticalViewAngle();
		}
		catch(Exception e) {
			// apparently some devices throw exceptions...
			e.printStackTrace();
			Log.e(TAG, "exception reading horizontal or vertical view angles");
			camera_features.view_angle_x = default_view_angle_x;
			camera_features.view_angle_y = default_view_angle_y;
		}
		if( MyDebug.LOG ) {
			Log.d(TAG, "view_angle_x: " + camera_features.view_angle_x);
			Log.d(TAG, "view_angle_y: " + camera_features.view_angle_y);
		}
		// need to handle some devices reporting rubbish
		if( camera_features.view_angle_x > 150.0f || camera_features.view_angle_y > 150.0f ) {
			Log.e(TAG, "camera API reporting stupid view angles, set to sensible defaults");
			camera_features.view_angle_x = default_view_angle_x;
			camera_features.view_angle_y = default_view_angle_y;
		}

		return camera_features;
	}
	
	public long getDefaultExposureTime() {
		// not supported for CameraController1
		return 0L;
	}

	// important, from docs:
	// "Changing scene mode may override other parameters (such as flash mode, focus mode, white balance).
	// For example, suppose originally flash mode is on and supported flash modes are on/off. In night
	// scene mode, both flash mode and supported flash mode may be changed to off. After setting scene
	// mode, applications should call getParameters to know if some parameters are changed."
	public SupportedValues setSceneMode(String value) {
		String default_value = getDefaultSceneMode();
    	Camera.Parameters parameters = this.getParameters();
		List<String> values = parameters.getSupportedSceneModes();
		/*{
			// test
			values = new ArrayList<>();
			values.add("auto");
		}*/
		SupportedValues supported_values = checkModeIsSupported(values, value, default_value);
		if( supported_values != null ) {
			String scene_mode = parameters.getSceneMode();
			// if scene mode is null, it should mean scene modes aren't supported anyway
			if( scene_mode != null && !scene_mode.equals(supported_values.selected_value) ) {
	        	parameters.setSceneMode(supported_values.selected_value);
	        	setCameraParameters(parameters);
			}
		}
		return supported_values;
	}
	
	public String getSceneMode() {
    	Camera.Parameters parameters = this.getParameters();
    	return parameters.getSceneMode();
	}

	public SupportedValues setColorEffect(String value) {
		String default_value = getDefaultColorEffect();
    	Camera.Parameters parameters = this.getParameters();
		List<String> values = parameters.getSupportedColorEffects();
		SupportedValues supported_values = checkModeIsSupported(values, value, default_value);
		if( supported_values != null ) {
			String color_effect = parameters.getColorEffect();
			// have got nullpointerexception from Google Play, so now check for null
			if( color_effect == null || !color_effect.equals(supported_values.selected_value) ) {
	        	parameters.setColorEffect(supported_values.selected_value);
	        	setCameraParameters(parameters);
			}
		}
		return supported_values;
	}

	public String getColorEffect() {
    	Camera.Parameters parameters = this.getParameters();
    	return parameters.getColorEffect();
	}

	public SupportedValues setWhiteBalance(String value) {
		String default_value = getDefaultWhiteBalance();
    	Camera.Parameters parameters = this.getParameters();
		List<String> values = parameters.getSupportedWhiteBalance();
		if( values != null ) {
			// Some devices (e.g., OnePlus 3T) claim to support a "manual" mode, even though this
			// isn't one of the possible white balances defined in Camera.Parameters.
			// Since the old API doesn't support white balance temperatures, and this mode seems to
			// have no useful effect, we remove it to avoid confusion.
			while( values.contains("manual") ) {
				values.remove("manual");
			}
		}
		SupportedValues supported_values = checkModeIsSupported(values, value, default_value);
		if( supported_values != null ) {
			if( !parameters.getWhiteBalance().equals(supported_values.selected_value) ) {
	        	parameters.setWhiteBalance(supported_values.selected_value);
	        	setCameraParameters(parameters);
			}
		}
		return supported_values;
	}

	public String getWhiteBalance() {
    	Camera.Parameters parameters = this.getParameters();
    	return parameters.getWhiteBalance();
	}

	@Override
	public boolean setWhiteBalanceTemperature(int temperature) {
		// not supported for CameraController1
		return false;
	}

	@Override
	public int getWhiteBalanceTemperature() {
		// not supported for CameraController1
		return 0;
	}

	@Override
	public SupportedValues setISO(String value) {
    	Camera.Parameters parameters = this.getParameters();
		// get available isos - no standard value for this, see http://stackoverflow.com/questions/2978095/android-camera-api-iso-setting
		String iso_values = parameters.get("iso-values");
		if( iso_values == null ) {
			iso_values = parameters.get("iso-mode-values"); // Galaxy Nexus
			if( iso_values == null ) {
				iso_values = parameters.get("iso-speed-values"); // Micromax A101
				if( iso_values == null )
					iso_values = parameters.get("nv-picture-iso-values"); // LG dual P990
			}
		}
		List<String> values = null;
		if( iso_values != null && iso_values.length() > 0 ) {
			if( MyDebug.LOG )
				Log.d(TAG, "iso_values: " + iso_values);
			String[] isos_array = iso_values.split(",");
			// split shouldn't return null
			if( isos_array.length > 0 ) {
				// remove duplicates (OnePlus 3T has several duplicate "auto" entries)
				HashSet<String> hashSet = new HashSet<>();
				values = new ArrayList<>();
				// use hashset for efficiency
				// make sure we alo preserve the order
				for(String iso : isos_array) {
					if( !hashSet.contains(iso) ) {
						values.add(iso);
						hashSet.add(iso);
					}
				}
			}
		}

		iso_key = "iso";
		if( parameters.get(iso_key) == null ) {
			iso_key = "iso-speed"; // Micromax A101
			if( parameters.get(iso_key) == null ) {
				iso_key = "nv-picture-iso"; // LG dual P990
				if( parameters.get(iso_key) == null ) {
					if ( Build.MODEL.contains("Z00") )
						iso_key = "iso"; // Asus Zenfone 2 Z00A and Z008: see https://sourceforge.net/p/opencamera/tickets/183/
					else
						iso_key = null; // not supported
				}
			}
		}
		/*values = new ArrayList<>();
		//values.add("auto");
		//values.add("ISO_HJR");
		values.add("ISO50");
		values.add("ISO64");
		values.add("ISO80");
		values.add("ISO100");
		values.add("ISO125");
		values.add("ISO160");
		values.add("ISO200");
		values.add("ISO250");
		values.add("ISO320");
		values.add("ISO400");
		values.add("ISO500");
		values.add("ISO640");
		values.add("ISO800");
		values.add("ISO1000");
		values.add("ISO1250");
		values.add("ISO1600");
		values.add("ISO2000");
		values.add("ISO2500");
		values.add("ISO3200");
		values.add("auto");
		//values.add("400");
		//values.add("800");
		//values.add("1600");
		iso_key = "iso";*/
		if( iso_key != null ){
			if( values == null ) {
				// set a default for some devices which have an iso_key, but don't give a list of supported ISOs
				values = new ArrayList<>();
				values.add("auto");
				values.add("50");
				values.add("100");
				values.add("200");
				values.add("400");
				values.add("800");
				values.add("1600");
			}
			SupportedValues supported_values = checkModeIsSupported(values, value, getDefaultISO());
			if( supported_values != null ) {
				if( MyDebug.LOG )
					Log.d(TAG, "set: " + iso_key + " to: " + supported_values.selected_value);
	        	parameters.set(iso_key, supported_values.selected_value);
	        	setCameraParameters(parameters);
			}
			return supported_values;
		}
		return null;
	}

	@Override
	public String getISOKey() {
		if( MyDebug.LOG )
			Log.d(TAG, "getISOKey");
    	return this.iso_key;
    }

	@Override
	public void setManualISO(boolean manual_iso, int iso) {
		// not supported for CameraController1
	}

	@Override
	public boolean isManualISO() {
		// not supported for CameraController1
		return false;
	}

	@Override
	public boolean setISO(int iso) {
		// not supported for CameraController1
		return false;
	}

	@Override
	public int getISO() {
		// not supported for CameraController1
		return 0;
	}

	@Override
	public long getExposureTime() {
		// not supported for CameraController1
		return 0L;
	}

	@Override
	public boolean setExposureTime(long exposure_time) {
		// not supported for CameraController1
		return false;
	}
	
	@Override
    public CameraController.Size getPictureSize() {
    	Camera.Parameters parameters = this.getParameters();
    	Camera.Size camera_size = parameters.getPictureSize();
    	return new CameraController.Size(camera_size.width, camera_size.height);
    }

	@Override
	public void setPictureSize(int width, int height) {
    	Camera.Parameters parameters = this.getParameters();
		parameters.setPictureSize(width, height);
		if( MyDebug.LOG )
			Log.d(TAG, "set picture size: " + parameters.getPictureSize().width + ", " + parameters.getPictureSize().height);
    	setCameraParameters(parameters);
	}
    
	@Override
    public CameraController.Size getPreviewSize() {
    	Camera.Parameters parameters = this.getParameters();
    	Camera.Size camera_size = parameters.getPreviewSize();
    	return new CameraController.Size(camera_size.width, camera_size.height);
    }

	@Override
	public void setPreviewSize(int width, int height) {
    	Camera.Parameters parameters = this.getParameters();
		if( MyDebug.LOG )
			Log.d(TAG, "current preview size: " + parameters.getPreviewSize().width + ", " + parameters.getPreviewSize().height);
        parameters.setPreviewSize(width, height);
		if( MyDebug.LOG )
			Log.d(TAG, "new preview size: " + parameters.getPreviewSize().width + ", " + parameters.getPreviewSize().height);
    	setCameraParameters(parameters);
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
		this.want_expo_bracketing = want_expo_bracketing;
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
		// not supported for CameraController1
	}

	@Override
	public void setOptimiseAEForDRO(boolean optimise_ae_for_dro) {
		// not supported for CameraController1
	}

	@Override
	public void setRaw(boolean want_raw) {
		// not supported for CameraController1
	}

	@Override
	public void setVideoStabilization(boolean enabled) {
	    Camera.Parameters parameters = this.getParameters();
        parameters.setVideoStabilization(enabled);
    	setCameraParameters(parameters);
	}
	
	public boolean getVideoStabilization() {
	    Camera.Parameters parameters = this.getParameters();
        return parameters.getVideoStabilization();
	}

	public int getJpegQuality() {
	    Camera.Parameters parameters = this.getParameters();
	    return parameters.getJpegQuality();
	}
	
	public void setJpegQuality(int quality) {
	    Camera.Parameters parameters = this.getParameters();
		parameters.setJpegQuality(quality);
    	setCameraParameters(parameters);
	}
	
	public int getZoom() {
		Camera.Parameters parameters = this.getParameters();
		return parameters.getZoom();
	}
	
	public void setZoom(int value) {
		Camera.Parameters parameters = this.getParameters();
		if( MyDebug.LOG )
			Log.d(TAG, "zoom was: " + parameters.getZoom());
		parameters.setZoom(value);
    	setCameraParameters(parameters);
	}

	public int getExposureCompensation() {
		Camera.Parameters parameters = this.getParameters();
		return parameters.getExposureCompensation();
	}

	private float getExposureCompensationStep() {
		float exposure_step;
		Camera.Parameters parameters = this.getParameters();
        try {
        	exposure_step = parameters.getExposureCompensationStep();
        }
        catch(Exception e) {
        	// received a NullPointerException from StringToReal.parseFloat() beneath getExposureCompensationStep() on Google Play!
    		if( MyDebug.LOG )
    			Log.e(TAG, "exception from getExposureCompensationStep()");
        	e.printStackTrace();
        	exposure_step = 1.0f/3.0f; // make up a typical example
        }
        return exposure_step;
	}
	
	// Returns whether exposure was modified
	public boolean setExposureCompensation(int new_exposure) {
		Camera.Parameters parameters = this.getParameters();
		int current_exposure = parameters.getExposureCompensation();
		if( new_exposure != current_exposure ) {
			if( MyDebug.LOG )
				Log.d(TAG, "change exposure from " + current_exposure + " to " + new_exposure);
			parameters.setExposureCompensation(new_exposure);
        	setCameraParameters(parameters);
        	return true;
		}
		return false;
	}
	
	public void setPreviewFpsRange(int min, int max) {
    	if( MyDebug.LOG )
    		Log.d(TAG, "setPreviewFpsRange: " + min + " to " + max);
		Camera.Parameters parameters = this.getParameters();
        parameters.setPreviewFpsRange(min, max);
    	setCameraParameters(parameters);
	}
	
	public List<int []> getSupportedPreviewFpsRange() {
		Camera.Parameters parameters = this.getParameters();
		try {
			return parameters.getSupportedPreviewFpsRange();
		}
		catch(StringIndexOutOfBoundsException e) {
			/* Have had reports of StringIndexOutOfBoundsException on Google Play on Sony Xperia M devices
				at android.hardware.Camera$Parameters.splitRange(Camera.java:4098)
				at android.hardware.Camera$Parameters.getSupportedPreviewFpsRange(Camera.java:2799)
				*/
			e.printStackTrace();
	    	if( MyDebug.LOG ) {
	    		Log.e(TAG, "getSupportedPreviewFpsRange() gave StringIndexOutOfBoundsException");
	    	}
		}
		return null;
	}
	
	@Override
	public void setFocusValue(String focus_value) {
		Camera.Parameters parameters = this.getParameters();
		switch(focus_value) {
			case "focus_mode_auto":
			case "focus_mode_locked":
				parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
				break;
			case "focus_mode_infinity":
				parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_INFINITY);
				break;
			case "focus_mode_macro":
				parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_MACRO);
				break;
			case "focus_mode_fixed":
				parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_FIXED);
				break;
			case "focus_mode_edof":
				parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_EDOF);
				break;
			case "focus_mode_continuous_picture":
				parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
				break;
			case "focus_mode_continuous_video":
				parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
				break;
			default:
				if (MyDebug.LOG)
					Log.d(TAG, "setFocusValue() received unknown focus value " + focus_value);
				break;
		}
    	setCameraParameters(parameters);
	}
	
	private String convertFocusModeToValue(String focus_mode) {
		// focus_mode may be null on some devices; we return ""
		if( MyDebug.LOG )
			Log.d(TAG, "convertFocusModeToValue: " + focus_mode);
		String focus_value = "";
		if( focus_mode == null ) {
			// ignore, leave focus_value at ""
		}
		else if( focus_mode.equals(Camera.Parameters.FOCUS_MODE_AUTO) ) {
    		focus_value = "focus_mode_auto";
    	}
		else if( focus_mode.equals(Camera.Parameters.FOCUS_MODE_INFINITY) ) {
    		focus_value = "focus_mode_infinity";
    	}
		else if( focus_mode.equals(Camera.Parameters.FOCUS_MODE_MACRO) ) {
    		focus_value = "focus_mode_macro";
    	}
		else if( focus_mode.equals(Camera.Parameters.FOCUS_MODE_FIXED) ) {
    		focus_value = "focus_mode_fixed";
    	}
		else if( focus_mode.equals(Camera.Parameters.FOCUS_MODE_EDOF) ) {
    		focus_value = "focus_mode_edof";
    	}
		else if( focus_mode.equals(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE) ) {
    		focus_value = "focus_mode_continuous_picture";
    	}
		else if( focus_mode.equals(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO) ) {
    		focus_value = "focus_mode_continuous_video";
    	}
    	return focus_value;
	}
	
	@Override
	public String getFocusValue() {
		// returns "" if Parameters.getFocusMode() returns null
		Camera.Parameters parameters = this.getParameters();
		String focus_mode = parameters.getFocusMode();
		// getFocusMode() is documented as never returning null, however I've had null pointer exceptions reported in Google Play
		return convertFocusModeToValue(focus_mode);
	}

	@Override
	public float getFocusDistance() {
		// not supported for CameraController1!
		return 0.0f;
	}

	@Override
	public boolean setFocusDistance(float focus_distance) {
		// not supported for CameraController1!
		return false;
	}

	private String convertFlashValueToMode(String flash_value) {
		String flash_mode = "";
		switch(flash_value) {
			case "flash_off":
				flash_mode = Camera.Parameters.FLASH_MODE_OFF;
				break;
			case "flash_auto":
				flash_mode = Camera.Parameters.FLASH_MODE_AUTO;
				break;
			case "flash_on":
				flash_mode = Camera.Parameters.FLASH_MODE_ON;
				break;
			case "flash_torch":
				flash_mode = Camera.Parameters.FLASH_MODE_TORCH;
				break;
			case "flash_red_eye":
				flash_mode = Camera.Parameters.FLASH_MODE_RED_EYE;
				break;
			case "flash_frontscreen_on":
				flash_mode = Camera.Parameters.FLASH_MODE_OFF;
				break;
		}
    	return flash_mode;
	}
	
	public void setFlashValue(String flash_value) {
		Camera.Parameters parameters = this.getParameters();
		if( MyDebug.LOG )
			Log.d(TAG, "setFlashValue: " + flash_value);

		this.frontscreen_flash = false;
    	if( flash_value.equals("flash_frontscreen_on") ) {
    		// we do this check first due to weird behaviour on Samsung Galaxy S7 front camera where parameters.getFlashMode() returns values (auto, beach, portrait)
    		this.frontscreen_flash = true;
    		return;
    	}
		
    	if( parameters.getFlashMode() == null ) {
    		if( MyDebug.LOG )
    			Log.d(TAG, "flash mode not supported");
			return;
    	}

		final String flash_mode = convertFlashValueToMode(flash_value);
    	if( flash_mode.length() > 0 && !flash_mode.equals(parameters.getFlashMode()) ) {
    		if( parameters.getFlashMode().equals(Camera.Parameters.FLASH_MODE_TORCH) && !flash_mode.equals(Camera.Parameters.FLASH_MODE_OFF) ) {
    			// workaround for bug on Nexus 5 and Nexus 6 where torch doesn't switch off until we set FLASH_MODE_OFF
    			if( MyDebug.LOG )
    				Log.d(TAG, "first turn torch off");
        		parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
            	setCameraParameters(parameters);
            	// need to set the correct flash mode after a delay
            	Handler handler = new Handler();
            	handler.postDelayed(new Runnable(){
            		@Override
            	    public void run(){
            			if( MyDebug.LOG )
            				Log.d(TAG, "now set actual flash mode after turning torch off");
            			if( camera != null ) { // make sure camera wasn't released in the meantime (has a Google Play crash as a result of this)
	            			Camera.Parameters parameters = getParameters();
	                		parameters.setFlashMode(flash_mode);
	                    	setCameraParameters(parameters);
            			}
            	   }
            	}, 100);
    		}
    		else {
        		parameters.setFlashMode(flash_mode);
            	setCameraParameters(parameters);
    		}
    	}
	}
	
	private String convertFlashModeToValue(String flash_mode) {
		// flash_mode may be null, meaning flash isn't supported; we return ""
		if( MyDebug.LOG )
			Log.d(TAG, "convertFlashModeToValue: " + flash_mode);
		String flash_value = "";
		if( flash_mode == null ) {
			// ignore, leave focus_value at ""
		}
		else if( flash_mode.equals(Camera.Parameters.FLASH_MODE_OFF) ) {
    		flash_value = "flash_off";
    	}
    	else if( flash_mode.equals(Camera.Parameters.FLASH_MODE_AUTO) ) {
    		flash_value = "flash_auto";
    	}
    	else if( flash_mode.equals(Camera.Parameters.FLASH_MODE_ON) ) {
    		flash_value = "flash_on";
    	}
    	else if( flash_mode.equals(Camera.Parameters.FLASH_MODE_TORCH) ) {
    		flash_value = "flash_torch";
    	}
    	else if( flash_mode.equals(Camera.Parameters.FLASH_MODE_RED_EYE) ) {
    		flash_value = "flash_red_eye";
    	}
    	return flash_value;
	}
	
	public String getFlashValue() {
		// returns "" if flash isn't supported
		Camera.Parameters parameters = this.getParameters();
		String flash_mode = parameters.getFlashMode(); // will be null if flash mode not supported
		return convertFlashModeToValue(flash_mode);
	}
	
	public void setRecordingHint(boolean hint) {
		if( MyDebug.LOG )
			Log.d(TAG, "setRecordingHint: " + hint);
		Camera.Parameters parameters = this.getParameters();
		// Calling setParameters here with continuous video focus mode causes preview to not restart after taking a photo on Galaxy Nexus?! (fine on my Nexus 7).
		// The issue seems to specifically be with setParameters (i.e., the problem occurs even if we don't setRecordingHint).
		// In addition, I had a report of a bug on HTC Desire X, Android 4.0.4 where the saved video was corrupted.
		// This worked fine in 1.7, then not in 1.8 and 1.9, then was fixed again in 1.10
		// The only thing in common to 1.7->1.8 and 1.9-1.10, that seems relevant, was adding this code to setRecordingHint() and setParameters() (unclear which would have been the problem),
		// so we should be very careful about enabling this code again!
		// Update for v1.23: the bug with Galaxy Nexus has come back (see comments in Preview.setPreviewFps()) and is now unavoidable,
		// but I've still kept this check here - if nothing else, because it apparently caused video recording problems on other devices too.
		// Update for v1.29: this doesn't seem to happen on Galaxy Nexus with continuous picture focus mode, which is what we now use; but again, still keepin the check here due to possible problems on other devices
		String focus_mode = parameters.getFocusMode();
		// getFocusMode() is documented as never returning null, however I've had null pointer exceptions reported in Google Play
        if( focus_mode != null && !focus_mode.equals(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO) ) {
			parameters.setRecordingHint(hint);
        	setCameraParameters(parameters);
        }
	}

	public void setRotation(int rotation) {
		Camera.Parameters parameters = this.getParameters();
		parameters.setRotation(rotation);
    	setCameraParameters(parameters);
	}
	
	public void setLocationInfo(Location location) {
        Camera.Parameters parameters = this.getParameters();
        parameters.removeGpsData();
        parameters.setGpsTimestamp(System.currentTimeMillis() / 1000); // initialise to a value (from Android camera source)
        parameters.setGpsLatitude(location.getLatitude());
        parameters.setGpsLongitude(location.getLongitude());
        parameters.setGpsProcessingMethod(location.getProvider()); // from http://boundarydevices.com/how-to-write-an-android-camera-app/
        if( location.hasAltitude() ) {
            parameters.setGpsAltitude(location.getAltitude());
        }
        else {
        	// Android camera source claims we need to fake one if not present
        	// and indeed, this is needed to fix crash on Nexus 7
            parameters.setGpsAltitude(0);
        }
        if( location.getTime() != 0 ) { // from Android camera source
        	parameters.setGpsTimestamp(location.getTime() / 1000);
        }
    	setCameraParameters(parameters);
	}
	
	public void removeLocationInfo() {
        Camera.Parameters parameters = this.getParameters();
        parameters.removeGpsData();
    	setCameraParameters(parameters);
	}

	public void enableShutterSound(boolean enabled) {
        if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 ) {
        	camera.enableShutterSound(enabled);
        }
	}
	
	public boolean setFocusAndMeteringArea(List<Area> areas) {
		List<Camera.Area> camera_areas = new ArrayList<>();
		for(CameraController.Area area : areas) {
			camera_areas.add(new Camera.Area(area.rect, area.weight));
		}
        Camera.Parameters parameters = this.getParameters();
		String focus_mode = parameters.getFocusMode();
		// getFocusMode() is documented as never returning null, however I've had null pointer exceptions reported in Google Play
        if( parameters.getMaxNumFocusAreas() != 0 && focus_mode != null && ( focus_mode.equals(Camera.Parameters.FOCUS_MODE_AUTO) || focus_mode.equals(Camera.Parameters.FOCUS_MODE_MACRO) || focus_mode.equals(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE) || focus_mode.equals(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO) ) ) {
		    parameters.setFocusAreas(camera_areas);

		    // also set metering areas
		    if( parameters.getMaxNumMeteringAreas() == 0 ) {
        		if( MyDebug.LOG )
        			Log.d(TAG, "metering areas not supported");
		    }
		    else {
		    	parameters.setMeteringAreas(camera_areas);
		    }

		    setCameraParameters(parameters);

		    return true;
        }
        else if( parameters.getMaxNumMeteringAreas() != 0 ) {
	    	parameters.setMeteringAreas(camera_areas);

		    setCameraParameters(parameters);
        }
        return false;
	}

	public void clearFocusAndMetering() {
        Camera.Parameters parameters = this.getParameters();
        boolean update_parameters = false;
        if( parameters.getMaxNumFocusAreas() > 0 ) {
        	parameters.setFocusAreas(null);
        	update_parameters = true;
        }
        if( parameters.getMaxNumMeteringAreas() > 0 ) {
        	parameters.setMeteringAreas(null);
        	update_parameters = true;
        }
        if( update_parameters ) {
		    setCameraParameters(parameters);
        }
	}

	@Override
	public boolean supportsAutoFocus() {
        Camera.Parameters parameters = this.getParameters();
		String focus_mode = parameters.getFocusMode();
		// getFocusMode() is documented as never returning null, however I've had null pointer exceptions reported in Google Play from the below line (v1.7),
		// on Galaxy Tab 10.1 (GT-P7500), Android 4.0.3 - 4.0.4; HTC EVO 3D X515m (shooteru), Android 4.0.3 - 4.0.4
        if( focus_mode != null && ( focus_mode.equals(Camera.Parameters.FOCUS_MODE_AUTO) || focus_mode.equals(Camera.Parameters.FOCUS_MODE_MACRO) ) ) {
        	return true;
        }
        return false;
	}
	
	@Override
	public boolean focusIsContinuous() {
        Camera.Parameters parameters = this.getParameters();
		String focus_mode = parameters.getFocusMode();
		// getFocusMode() is documented as never returning null, however I've had null pointer exceptions reported in Google Play from the below line (v1.7),
		// on Galaxy Tab 10.1 (GT-P7500), Android 4.0.3 - 4.0.4; HTC EVO 3D X515m (shooteru), Android 4.0.3 - 4.0.4
        if( focus_mode != null && ( focus_mode.equals(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE) || focus_mode.equals(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO) ) ) {
        	return true;
        }
        return false;
	}

	@Override
	public 
	void reconnect() throws CameraControllerException {
		if( MyDebug.LOG )
			Log.d(TAG, "reconnect");
		try {
			camera.reconnect();
		}
		catch(IOException e) {
			if( MyDebug.LOG )
				Log.e(TAG, "reconnect threw IOException");
			e.printStackTrace();
			throw new CameraControllerException();
		}
	}
	
	@Override
	public void setPreviewDisplay(SurfaceHolder holder) throws CameraControllerException {
		if( MyDebug.LOG )
			Log.d(TAG, "setPreviewDisplay");
		try {
			camera.setPreviewDisplay(holder);
		}
		catch(IOException e) {
			e.printStackTrace();
			throw new CameraControllerException();
		}
	}

	@Override
	public void setPreviewTexture(SurfaceTexture texture) throws CameraControllerException {
		if( MyDebug.LOG )
			Log.d(TAG, "setPreviewTexture");
		try {
			camera.setPreviewTexture(texture);
		}
		catch(IOException e) {
			e.printStackTrace();
			throw new CameraControllerException();
		}
	}

	@Override
	public void startPreview() throws CameraControllerException {
		if( MyDebug.LOG )
			Log.d(TAG, "startPreview");
		try {
			camera.startPreview();
		}
		catch(RuntimeException e) {
			if( MyDebug.LOG )
				Log.e(TAG, "failed to start preview");
			e.printStackTrace();
			throw new CameraControllerException();
		}
	}
	
	@Override
	public void stopPreview() {
		camera.stopPreview();
	}
	
	// returns false if RuntimeException thrown (may include if face-detection already started)
	public boolean startFaceDetection() {
	    try {
			camera.startFaceDetection();
	    }
	    catch(RuntimeException e) {
			if( MyDebug.LOG )
				Log.d(TAG, "face detection failed or already started");
	    	return false;
	    }
	    return true;
	}
	
	public void setFaceDetectionListener(final CameraController.FaceDetectionListener listener) {
		class CameraFaceDetectionListener implements Camera.FaceDetectionListener {
		    @Override
		    public void onFaceDetection(Camera.Face[] camera_faces, Camera camera) {
		    	Face [] faces = new Face[camera_faces.length];
		    	for(int i=0;i<camera_faces.length;i++) {
		    		faces[i] = new Face(camera_faces[i].score, camera_faces[i].rect);
		    	}
		    	listener.onFaceDetection(faces);
		    }
		}
		camera.setFaceDetectionListener(new CameraFaceDetectionListener());
	}

	@Override
	public void autoFocus(final CameraController.AutoFocusCallback cb, boolean capture_follows_autofocus_hint) {
		if( MyDebug.LOG )
			Log.d(TAG, "autoFocus");
        Camera.AutoFocusCallback camera_cb = new Camera.AutoFocusCallback() {
    		boolean done_autofocus = false;

    		@Override
			public void onAutoFocus(boolean success, Camera camera) {
				if( MyDebug.LOG )
					Log.d(TAG, "autoFocus.onAutoFocus");
				// in theory we should only ever get one call to onAutoFocus(), but some Samsung phones at least can call the callback multiple times
				// see http://stackoverflow.com/questions/36316195/take-picture-fails-on-samsung-phones
				// needed to fix problem on Samsung S7 with flash auto/on and continuous picture focus where it would claim failed to take picture even though it'd succeeded,
				// because we repeatedly call takePicture(), and the subsequent ones cause a runtime exception
				if( !done_autofocus ) {
					done_autofocus = true;
					cb.onAutoFocus(success);
				}
				else {
					if( MyDebug.LOG )
						Log.e(TAG, "ignore repeated autofocus");
				}
			}
        };
        try {
        	camera.autoFocus(camera_cb);
        }
		catch(RuntimeException e) {
			// just in case? We got a RuntimeException report here from 1 user on Google Play:
			// 21 Dec 2013, Xperia Go, Android 4.1
			if( MyDebug.LOG )
				Log.e(TAG, "runtime exception from autoFocus");
			e.printStackTrace();
			// should call the callback, so the application isn't left waiting (e.g., when we autofocus before trying to take a photo)
			cb.onAutoFocus(false);
		}
	}

	@Override
	public void setCaptureFollowAutofocusHint(boolean capture_follows_autofocus_hint) {
		// unused by this API
	}

	@Override
	public void cancelAutoFocus() {
		try {
			camera.cancelAutoFocus();
		}
		catch(RuntimeException e) {
			// had a report of crash on some devices, see comment at https://sourceforge.net/p/opencamera/tickets/4/ made on 20140520
			if( MyDebug.LOG )
				Log.d(TAG, "cancelAutoFocus() failed");
    		e.printStackTrace();
		}
	}
	
	@Override
	public void setContinuousFocusMoveCallback(final ContinuousFocusMoveCallback cb) {
		if( MyDebug.LOG )
			Log.d(TAG, "setContinuousFocusMoveCallback");
		if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN ) {
			// setAutoFocusMoveCallback() requires JELLY_BEAN
			try {
				if( cb != null ) {
					camera.setAutoFocusMoveCallback(new AutoFocusMoveCallback() {
						@Override
						public void onAutoFocusMoving(boolean start, Camera camera) {
							if( MyDebug.LOG )
								Log.d(TAG, "onAutoFocusMoving: " + start);
							cb.onContinuousFocusMove(start);
						}
					});
				}
				else {
					camera.setAutoFocusMoveCallback(null);
				}
			}
			catch(RuntimeException e) {
				// received RuntimeException reports from some users on Google Play - seems to be older devices, but still important to catch!
				if( MyDebug.LOG )
					Log.e(TAG, "runtime exception from setAutoFocusMoveCallback");
				e.printStackTrace();
			}
		}
		else {
			if( MyDebug.LOG )
				Log.d(TAG, "setContinuousFocusMoveCallback requires Android JELLY_BEAN or higher");
		}
	}

	private static class TakePictureShutterCallback implements Camera.ShutterCallback {
		// don't do anything here, but we need to implement the callback to get the shutter sound (at least on Galaxy Nexus and Nexus 7)
		@Override
        public void onShutter() {
			if( MyDebug.LOG )
				Log.d(TAG, "shutterCallback.onShutter()");
        }
	}
	
	private void clearPending() {
		if( MyDebug.LOG )
			Log.d(TAG, "clearPending");
		pending_burst_images.clear();
		burst_exposures = null;
		n_burst = 0;
	}

	private void takePictureNow(final CameraController.PictureCallback picture, final ErrorCallback error) {
		if( MyDebug.LOG )
			Log.d(TAG, "takePictureNow");

    	final Camera.ShutterCallback shutter = new TakePictureShutterCallback();
        final Camera.PictureCallback camera_jpeg = picture == null ? null : new Camera.PictureCallback() {
    	    public void onPictureTaken(byte[] data, Camera cam) {
				if( MyDebug.LOG )
					Log.d(TAG, "onPictureTaken");
    	    	// n.b., this is automatically run in a different thread

				if( want_expo_bracketing && n_burst > 1 ) {
					pending_burst_images.add(data);
					if( pending_burst_images.size() >= n_burst ) { // shouldn't ever be greater, but just in case
						if( MyDebug.LOG )
							Log.d(TAG, "all burst images available");
						if( pending_burst_images.size() > n_burst ) {
							Log.e(TAG, "pending_burst_images size " + pending_burst_images.size() + " is greater than n_burst " + n_burst);
						}

						// set exposure compensation back to original
						setExposureCompensation(burst_exposures.get(0));

						// take a copy, so that we can clear pending_burst_images
						// also allows us to reorder from dark to light
						// since we took the images with the base exposure being first
						int n_half_images = pending_burst_images.size()/2;
						List<byte []> images = new ArrayList<>();
						// darker images
						for(int i=0;i<n_half_images;i++) {
							images.add(pending_burst_images.get(i+1));
						}
						// base image
						images.add(pending_burst_images.get(0));
						// lighter images
						for(int i=0;i<n_half_images;i++) {
							images.add(pending_burst_images.get(n_half_images+1));
						}

						picture.onBurstPictureTaken(images);
						pending_burst_images.clear();
						picture.onCompleted();
					}
					else {
						if( MyDebug.LOG )
							Log.d(TAG, "number of burst images is now: " + pending_burst_images.size());
						// set exposure compensation for next image
						setExposureCompensation(burst_exposures.get(pending_burst_images.size()));

						// need to start preview again: otherwise fail to take subsequent photos on Nexus 6
						// and Nexus 7; on Galaxy Nexus we succeed, but exposure compensation has no effect
						try {
							startPreview();
						}
						catch(CameraControllerException e) {
							if( MyDebug.LOG )
								Log.d(TAG, "CameraControllerException trying to startPreview");
							e.printStackTrace();
						}

						Handler handler = new Handler();
						handler.postDelayed(new Runnable(){
							@Override
							public void run(){
								if( MyDebug.LOG )
									Log.d(TAG, "take picture after delay for next expo");
								if( camera != null ) { // make sure camera wasn't released in the meantime
									takePictureNow(picture, error);
								}
						   }
						}, 1000);
					}
				}
				else {
					picture.onPictureTaken(data);
					picture.onCompleted();
				}
    	    }
        };

		if( picture != null ) {
			if( MyDebug.LOG )
				Log.d(TAG, "call onStarted() in callback");
			picture.onStarted();
		}
        try {
			if (PopupView.sound_index == 0) {
				camera.takePicture(shutter, null, camera_jpeg);
			} else {
				camera.takePicture(null, null, camera_jpeg);
			}
		}
		catch(RuntimeException e) {
			// just in case? We got a RuntimeException report here from 1 user on Google Play; I also encountered it myself once of Galaxy Nexus when starting up
			if( MyDebug.LOG )
				Log.e(TAG, "runtime exception from takePicture");
			e.printStackTrace();
			error.onError();
		}
	}

	public void takePicture(final CameraController.PictureCallback picture, final ErrorCallback error) {
		if( MyDebug.LOG )
			Log.d(TAG, "takePicture");

		clearPending();
        if( want_expo_bracketing ) {
			if( MyDebug.LOG )
				Log.d(TAG, "set up expo bracketing");
			Camera.Parameters parameters = this.getParameters();
			int n_half_images = expo_bracketing_n_images/2;
			int min_exposure = parameters.getMinExposureCompensation();
			int max_exposure = parameters.getMaxExposureCompensation();
			float exposure_step = getExposureCompensationStep();
			if( exposure_step == 0.0f ) // just in case?
	        	exposure_step = 1.0f/3.0f; // make up a typical example
			int exposure_current = getExposureCompensation();
			double stops_per_image = expo_bracketing_stops / (double)n_half_images;
			int steps = (int)((stops_per_image+1.0e-5) / exposure_step); // need to add a small amount, otherwise we can round down
			steps = Math.max(steps, 1);
			if( MyDebug.LOG ) {
				Log.d(TAG, "steps: " + steps);
				Log.d(TAG, "exposure_current: " + exposure_current);
			}

			List<Integer> requests = new ArrayList<>();

			// do the current exposure first, so we can take the first shot immediately
			// if we change the order, remember to update the code that re-orders for passing resultant images back to picture.onBurstPictureTaken()
			requests.add(exposure_current);

			// darker images
			for(int i=0;i<n_half_images;i++) {
				int exposure = exposure_current - (n_half_images-i)*steps;
				exposure = Math.max(exposure, min_exposure);
				requests.add(exposure);
				if( MyDebug.LOG ) {
					Log.d(TAG, "add burst request for " + i + "th dark image:");
					Log.d(TAG, "exposure: " + exposure);
				}
			}

			// lighter images
			for(int i=0;i<n_half_images;i++) {
				int exposure = exposure_current + (i+1)*steps;
				exposure = Math.min(exposure, max_exposure);
				requests.add(exposure);
				if( MyDebug.LOG ) {
					Log.d(TAG, "add burst request for " + i + "th light image:");
					Log.d(TAG, "exposure: " + exposure);
				}
			}

			burst_exposures = requests;
			n_burst = requests.size();
		}

		if( frontscreen_flash ) {
			if( MyDebug.LOG )
				Log.d(TAG, "front screen flash");
			picture.onFrontScreenTurnOn();
			// take picture after a delay, to allow autoexposure and autofocus to update (unlike CameraController2, we can't tell when this happens, so we just wait for a fixed delay)
        	Handler handler = new Handler();
        	handler.postDelayed(new Runnable(){
        		@Override
        	    public void run(){
        			if( MyDebug.LOG )
        				Log.d(TAG, "take picture after delay for front screen flash");
        			if( camera != null ) { // make sure camera wasn't released in the meantime
        				takePictureNow(picture, error);
        			}
        	   }
        	}, 1000);
			return;
		}
		takePictureNow(picture, error);
	}
	
	public void setDisplayOrientation(int degrees) {
		// rest of code from http://developer.android.com/reference/android/hardware/Camera.html#setDisplayOrientation(int)
	    int result;
	    if( camera_info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT ) {
	        result = (camera_info.orientation + degrees) % 360;
	        result = (360 - result) % 360;  // compensate the mirror
	    }
	    else {
	        result = (camera_info.orientation - degrees + 360) % 360;
	    }
		if( MyDebug.LOG ) {
			Log.d(TAG, "    info orientation is " + camera_info.orientation);
			Log.d(TAG, "    setDisplayOrientation to " + result);
		}

		camera.setDisplayOrientation(result);
	    this.display_orientation = result;
	}
	
	public int getDisplayOrientation() {
		return this.display_orientation;
	}
	
	public int getCameraOrientation() {
		return camera_info.orientation;
	}
	
	public boolean isFrontFacing() {
		return (camera_info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT);
	}
	
	public void unlock() {
		this.stopPreview(); // although not documented, we need to stop preview to prevent device freeze or video errors shortly after video recording starts on some devices (e.g., device freeze on Samsung Galaxy S2 - I could reproduce this on Samsung RTL; also video recording fails and preview becomes corrupted on Galaxy S3 variant "SGH-I747-US2"); also see http://stackoverflow.com/questions/4244999/problem-with-video-recording-after-auto-focus-in-android
		camera.unlock();
	}

	@Override
	public String getParametersString() {
		String string = "";
		try {
			string = this.getParameters().flatten();
		}
        catch(Exception e) {
        	// received a StringIndexOutOfBoundsException from beneath getParameters().flatten() on Google Play!
    		if( MyDebug.LOG )
    			Log.e(TAG, "exception from getParameters().flatten()");
        	e.printStackTrace();
        }
		return string;
	}
}
