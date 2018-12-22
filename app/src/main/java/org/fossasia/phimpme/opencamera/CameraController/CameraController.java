package org.fossasia.phimpme.opencamera.CameraController;

import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.DngCreator;
import android.location.Location;
import android.media.Image;
import android.media.MediaRecorder;
import android.util.Log;
import android.view.SurfaceHolder;

import org.fossasia.phimpme.opencamera.Camera.MyDebug;

import java.util.List;

/** CameraController is an abstract class that wraps up the access/control to
 *  the Android camera, so that the rest of the application doesn't have to
 *  deal directly with the Android camera API. It also allows us to support
 *  more than one camera API through the same API (this is used to support both
 *  the original camera API, and Android 5's Camera2 API).
 *  The class is fairly low level wrapper about the APIs - there is some
 *  additional logical/workarounds where such things are API-specific, but
 *  otherwise the calling application still controls the behaviour of the
 *  camera.
 */
public abstract class CameraController {
	private static final String TAG = "CameraController";
	private final int cameraId;

	public static final long EXPOSURE_TIME_DEFAULT = 1000000000L/30;

	// for testing:
	public int count_camera_parameters_exception;
	public int count_precapture_timeout;
	public boolean test_wait_capture_result; // whether to test delayed capture result in Camera2 API
	public volatile int test_capture_results; // for Camera2 API, how many capture requests completed with RequestTag.CAPTURE
	public volatile int test_fake_flash_focus; // for Camera2 API, records torch turning on for fake flash during autofocus
	public volatile int test_fake_flash_precapture; // for Camera2 API, records torch turning on for fake flash during precapture
	public volatile int test_fake_flash_photo; // for Camera2 API, records torch turning on for fake flash for photo capture
	public volatile int test_af_state_null_focus; // for Camera2 API, records af_state being null even when we've requested autofocus

	public static class CameraFeatures {
		public boolean is_zoom_supported;
		public int max_zoom;
		public List<Integer> zoom_ratios;
		public boolean supports_face_detection;
		public List<Size> picture_sizes;
		public List<Size> video_sizes;
		public List<Size> video_sizes_high_speed;
		public List<Size> preview_sizes;
		public List<String> supported_flash_values;
		public List<String> supported_focus_values;
		public int max_num_focus_areas;
		public float minimum_focus_distance;
		public boolean is_exposure_lock_supported;
		public boolean is_video_stabilization_supported;
		public boolean supports_white_balance_temperature;
		public int min_temperature;
		public int max_temperature;
		public boolean supports_iso_range;
		public int min_iso;
		public int max_iso;
		public boolean supports_exposure_time;
		public long min_exposure_time;
		public long max_exposure_time;
		public int min_exposure;
		public int max_exposure;
		public float exposure_step;
		public boolean can_disable_shutter_sound;
		public boolean supports_expo_bracketing;
		public int max_expo_bracketing_n_images;
		public boolean supports_raw;
		public float view_angle_x; // horizontal angle of view in degrees (when unzoomed)
		public float view_angle_y; // vertical angle of view in degrees (when unzoomed)
	}

	public static class Size {
		public final int width;
		public final int height;

		public Size(int width, int height) {
			this.width = width;
			this.height = height;
		}

		@Override
		public boolean equals(Object o) {
			if( !(o instanceof Size) )
				return false;
			Size that = (Size)o;
			return this.width == that.width && this.height == that.height;
		}

		@Override
		public int hashCode() {
			// must override this, as we override equals()
			// can't use:
			//return Objects.hash(width, height);
			// as this requires API level 19
			// so use this from http://stackoverflow.com/questions/11742593/what-is-the-hashcode-for-a-custom-class-having-just-two-int-properties
			return width*31 + height;
		}
	}

	/** An area has values from [-1000,-1000] (for top-left) to [1000,1000] (for bottom-right) for whatever is
	 * the current field of view (i.e., taking zoom into account).
	 */
	public static class Area {
		final Rect rect;
		final int weight;

		public Area(Rect rect, int weight) {
			this.rect = rect;
			this.weight = weight;
		}
	}

	public interface FaceDetectionListener {
		void onFaceDetection(Face[] faces);
	}

	public interface PictureCallback {
		void onStarted(); // called immediately before we start capturing the picture
		void onCompleted(); // called after all relevant on*PictureTaken() callbacks have been called and returned
		void onPictureTaken(byte[] data);
		/** Only called if RAW is requested.
		 *  Caller should call image.close() and dngCreator.close() when done with the image.
		 */
		void onRawPictureTaken(DngCreator dngCreator, Image image);
		/** Only called if burst is requested.
		 */
		void onBurstPictureTaken(List<byte[]> images);
		/* This is called for flash_frontscreen_auto or flash_frontscreen_on mode to indicate the caller should light up the screen
		 * (for flash_frontscreen_auto it will only be called if the scene is considered dark enough to require the screen flash).
		 * The screen flash can be removed when or after onCompleted() is called.
		 */
		void onFrontScreenTurnOn();
	}

	public interface AutoFocusCallback {
		void onAutoFocus(boolean success);
	}

	public interface ContinuousFocusMoveCallback {
		void onContinuousFocusMove(boolean start);
	}

	public interface ErrorCallback {
		void onError();
	}

	public static class Face {
		public final int score;
		/* The has values from [-1000,-1000] (for top-left) to [1000,1000] (for bottom-right) for whatever is
		 * the current field of view (i.e., taking zoom into account).
		 */
		public final Rect rect;

		Face(int score, Rect rect) {
			this.score = score;
			this.rect = rect;
		}
	}

	public static class SupportedValues {
		public final List<String> values;
		public final String selected_value;
		SupportedValues(List<String> values, String selected_value) {
			this.values = values;
			this.selected_value = selected_value;
		}
	}

	public abstract void release();
	public abstract void onError(); // triggers error mechanism - should only be called externally for testing purposes

	CameraController(int cameraId) {
		this.cameraId = cameraId;
	}
	public abstract String getAPI();
	public abstract CameraFeatures getCameraFeatures();
	public int getCameraId() {
		return cameraId;
	}
	public abstract SupportedValues setSceneMode(String value);
	/**
	 * @return The current scene mode. Will be null if scene mode not supported.
     */
	public abstract String getSceneMode();
	public abstract SupportedValues setColorEffect(String value);
	public abstract String getColorEffect();
	public abstract SupportedValues setWhiteBalance(String value);
	public abstract String getWhiteBalance();
	public abstract boolean setWhiteBalanceTemperature(int temperature);
	public abstract int getWhiteBalanceTemperature();
	/** Set an ISO value. Only supported if supports_iso_range is false.
	 */
	public abstract SupportedValues setISO(String value);
	/** Switch between auto and manual ISO mode. Only supported if supports_iso_range is true.
	 * @param manual_iso Whether to switch to manual mode or back to auto.
	 * @param iso If manual_iso is true, this specifies the desired ISO value. If this is outside
	 *            the min_iso/max_iso, the value will be snapped so it does lie within that range.
	 *            If manual_iso i false, this value is ignored.
	 */
	public abstract void setManualISO(boolean manual_iso, int iso);

	/**
	 * @return Whether in manual ISO mode (as opposed to auto).
     */
	public abstract boolean isManualISO();
	/** Specify a specific ISO value. Only supported if supports_iso_range is true. Callers should
	 *  first switch to manual ISO mode using setManualISO().
	 */
	public abstract boolean setISO(int iso);
    public abstract String getISOKey();
	/** Returns the manual ISO value. Only supported if supports_iso_range is true.
	 */
	public abstract int getISO();
	public abstract long getExposureTime();
	public abstract boolean setExposureTime(long exposure_time);
    public abstract CameraController.Size getPictureSize();
    public abstract void setPictureSize(int width, int height);
    public abstract CameraController.Size getPreviewSize();
    public abstract void setPreviewSize(int width, int height);
	public abstract void setExpoBracketing(boolean want_expo_bracketing);
	/** n_images must be an odd number greater than 1.
	 */
	public abstract void setExpoBracketingNImages(int n_images);
	public abstract void setExpoBracketingStops(double stops);
	public abstract void setUseExpoFastBurst(boolean use_expo_fast_burst);
	/** If optimise_ae_for_dro is true, then this is a hint that if in auto-exposure mode and flash/torch
	 *  is not on, the CameraController should try to optimise for a DRO (dynamic range optimisation) mode.
	 */
	public abstract void setOptimiseAEForDRO(boolean optimise_ae_for_dro);
	public abstract void setRaw(boolean want_raw);
	/**
	 * setUseCamera2FakeFlash() should be called after creating the CameraController, and before calling getCameraFeatures() or
	 * starting the preview (as it changes the available flash modes).
	 * "Fake flash" is an alternative mode for handling flash, for devices that have poor Camera2 support - typical symptoms
	 * include precapture never starting, flash not firing, photos being over or under exposed.
	 * Instead, we fake the precapture and flash simply by turning on the torch. After turning on torch, we wait for ae to stop
	 * scanning (and af too, as it can start scanning in continuous mode) - this is effectively the equivalent of precapture -
	 * before taking the photo.
	 * In auto-focus mode, we make the decision ourselves based on the current ISO.
	 * We also handle the flash firing for autofocus by turning the torch on and off too. Advantages are:
	 *   - The flash tends to be brighter, and the photo can end up overexposed as a result if capture follows the autofocus.
	 *   - Some devices also don't seem to fire flash for autofocus in Camera2 mode (e.g., Samsung S7)
	 *   - When capture follows autofocus, we need to make the same decision for firing flash for both the autofocus and the capture.
	 */
	public void setUseCamera2FakeFlash(boolean use_fake_precapture) {
	}
	public boolean getUseCamera2FakeFlash() {
		return false;
	}
	public abstract void setVideoStabilization(boolean enabled);
	public abstract boolean getVideoStabilization();
	public abstract int getJpegQuality();
	public abstract void setJpegQuality(int quality);
	public abstract int getZoom();
	public abstract void setZoom(int value);
	public abstract int getExposureCompensation();
	public abstract boolean setExposureCompensation(int new_exposure);
	public abstract void setPreviewFpsRange(int min, int max);
	public abstract List<int []> getSupportedPreviewFpsRange();

	public String getDefaultSceneMode() {
		return "auto"; // chosen to match Camera.Parameters.SCENE_MODE_AUTO, but we also use compatible values for Camera2 API
	}
	public String getDefaultColorEffect() {
		return "none"; // chosen to match Camera.Parameters.EFFECT_NONE, but we also use compatible values for Camera2 API
	}
	public String getDefaultWhiteBalance() {
		return "auto"; // chosen to match Camera.Parameters.WHITE_BALANCE_AUTO, but we also use compatible values for Camera2 API
	}
	public String getDefaultISO() {
		return "auto";
	}

	public abstract void setFocusValue(String focus_value);
	public abstract String getFocusValue();
	public abstract float getFocusDistance();
	public abstract boolean setFocusDistance(float focus_distance);
	public abstract void setFlashValue(String flash_value);
	public abstract String getFlashValue();
	public abstract void setRecordingHint(boolean hint);
	public abstract void setRotation(int rotation);
	public abstract void setLocationInfo(Location location);
	public abstract void removeLocationInfo();
	public abstract void enableShutterSound(boolean enabled);
	public abstract boolean setFocusAndMeteringArea(List<Area> areas);
	public abstract void clearFocusAndMetering();
	public abstract boolean supportsAutoFocus();
	public abstract boolean focusIsContinuous();
	public abstract void reconnect() throws CameraControllerException;
	public abstract void setPreviewDisplay(SurfaceHolder holder) throws CameraControllerException;
	public abstract void setPreviewTexture(SurfaceTexture texture) throws CameraControllerException;
	/** Starts the camera preview.
	 *  @throws CameraControllerException if the camera preview fails to start.
     */
	public abstract void startPreview() throws CameraControllerException;
	public abstract void stopPreview();
	public abstract boolean startFaceDetection();
	public abstract void setFaceDetectionListener(final CameraController.FaceDetectionListener listener);

	/**
	 * @param cb Callback to be called when autofocus completes.
	 * @param capture_follows_autofocus_hint Set to true if you intend to take a photo immediately after autofocus. If the
	 *                                       decision changes after autofocus has started (e.g., user initiates autofocus,
	 *                                       then takes photo before autofocus has completed), use setCaptureFollowAutofocusHint().
     */
	public abstract void autoFocus(final CameraController.AutoFocusCallback cb, boolean capture_follows_autofocus_hint);
	/** See autoFocus() for details - used to update the capture_follows_autofocus_hint setting.
     */
	public abstract void setCaptureFollowAutofocusHint(boolean capture_follows_autofocus_hint);
	public abstract void cancelAutoFocus();
	public abstract void setContinuousFocusMoveCallback(ContinuousFocusMoveCallback cb);
	public abstract void takePicture(final CameraController.PictureCallback picture, final ErrorCallback error);
	public abstract void setDisplayOrientation(int degrees);
	public abstract int getDisplayOrientation();
	public abstract int getCameraOrientation();
	public abstract boolean isFrontFacing();
	public abstract void unlock();
	public abstract String getParametersString();
	public boolean captureResultIsAEScanning() {
		return false;
	}
	/**
	 * @return whether flash will fire; returns false if not known
     */
	public boolean needsFlash() {
		return false;
	}
	public boolean captureResultHasWhiteBalanceTemperature() {
		return false;
	}
	public int captureResultWhiteBalanceTemperature() {
		return 0;
	}
	public boolean captureResultHasIso() {
		return false;
	}
	public int captureResultIso() {
		return 0;
	}
	public boolean captureResultHasExposureTime() {
		return false;
	}
	public long captureResultExposureTime() {
		return 0;
	}
	/*public boolean captureResultHasFrameDuration() {
		return false;
	}*/
	/*public long captureResultFrameDuration() {
		return 0;
	}*/
	/*public boolean captureResultHasFocusDistance() {
		return false;
	}*/
	/*public float captureResultFocusDistanceMin() {
		return 0.0f;
	}*/
	/*public float captureResultFocusDistanceMax() {
		return 0.0f;
	}*/

	// gets the available values of a generic mode, e.g., scene, color etc, and makes sure the requested mode is available
	SupportedValues checkModeIsSupported(List<String> values, String value, String default_value) {
		if( values != null && values.size() > 1 ) { // n.b., if there is only 1 supported value, we also return null, as no point offering the choice to the user (there are some devices, e.g., Samsung, that only have a scene mode of "auto")
			if( MyDebug.LOG ) {
				for(int i=0;i<values.size();i++) {
		        	Log.d(TAG, "supported value: " + values.get(i));
				}
			}
			// make sure result is valid
			if( !values.contains(value) ) {
				if( MyDebug.LOG )
					Log.d(TAG, "value not valid!");
				if( values.contains(default_value) )
					value = default_value;
				else
					value = values.get(0);
				if( MyDebug.LOG )
					Log.d(TAG, "value is now: " + value);
			}
			return new SupportedValues(values, value);
		}
		return null;
	}
}
