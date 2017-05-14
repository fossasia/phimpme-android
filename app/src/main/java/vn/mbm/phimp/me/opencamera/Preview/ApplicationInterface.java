package vn.mbm.phimp.me.opencamera.Preview;

import android.content.Context;
import android.graphics.Canvas;
import android.hardware.camera2.DngCreator;
import android.location.Location;
import android.media.CamcorderProfile;
import android.media.Image;
import android.net.Uri;
import android.util.Pair;
import android.view.MotionEvent;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

/** Provides communication between the Preview and the rest of the application
 *  - so in theory one can drop the Preview/ (and CameraController/) classes
 *  into a new application, by providing an appropriate implementation of this
 *  ApplicationInterface.
 */
public interface ApplicationInterface {
	class NoFreeStorageException extends Exception {
		private static final long serialVersionUID = -2021932609486148748L;
	}
	class VideoMaxFileSize {
		public long max_filesize; // maximum file size in bytes for video (return 0 for device default - typically this is ~2GB)
		public boolean auto_restart; // whether to automatically restart on hitting max filesize (this setting is still relevant for max_filesize==0, as typically there will still be a device max filesize)
	}

	int VIDEOMETHOD_FILE = 0; // video will be saved to a file
	int VIDEOMETHOD_SAF = 1; // video will be saved using Android 5's Storage Access Framework
	int VIDEOMETHOD_URI = 2; // video will be written to the supplied Uri
	
	// methods that request information
	Context getContext(); // get the application context
	boolean useCamera2(); // should Android 5's Camera 2 API be used?
	Location getLocation(); // get current location - null if not available (or you don't care about geotagging)
	int createOutputVideoMethod(); // return a VIDEOMETHOD_* value to specify how to create a video file
	File createOutputVideoFile() throws IOException; // will be called if createOutputVideoUsingSAF() returns VIDEOMETHOD_FILE
	Uri createOutputVideoSAF() throws IOException; // will be called if createOutputVideoUsingSAF() returns VIDEOMETHOD_SAF
	Uri createOutputVideoUri(); // will be called if createOutputVideoUsingSAF() returns VIDEOMETHOD_URI
	// for all of the get*Pref() methods, you can use Preview methods to get the supported values (e.g., getSupportedSceneModes())
	// if you just want a default or don't really care, see the comments for each method for a default or possible options
	// if Preview doesn't support the requested setting, it will check this, and choose its own
	int getCameraIdPref(); // camera to use, from 0 to getCameraControllerManager().getNumberOfCameras()
	String getFlashPref(); // flash_off, flash_auto, flash_on, flash_torch, flash_red_eye
	String getFocusPref(boolean is_video); // focus_mode_auto, focus_mode_infinity, focus_mode_macro, focus_mode_locked, focus_mode_fixed, focus_mode_manual2, focus_mode_edof, focus_mode_continuous_video
	boolean isVideoPref(); // start up in video mode?
	String getSceneModePref(); // "auto" for default (strings correspond to Android's scene mode constants in android.hardware.Camera.Parameters)
	String getColorEffectPref(); // "node" for default (strings correspond to Android's color effect constants in android.hardware.Camera.Parameters)
	String getWhiteBalancePref(); // "auto" for default (strings correspond to Android's white balance constants in android.hardware.Camera.Parameters)
	int getWhiteBalanceTemperaturePref();
	String getISOPref(); // "auto" for auto-ISO, otherwise a numerical value; see documentation for Preview.supportsISORange().
	int getExposureCompensationPref(); // 0 for default
	Pair<Integer, Integer> getCameraResolutionPref(); // return null to let Preview choose size
	int getImageQualityPref(); // jpeg quality for taking photos; "90" is a recommended default
	boolean getFaceDetectionPref(); // whether to use face detection mode
	String getVideoQualityPref(); // should be one of Preview.getSupportedVideoQuality() (use Preview.getCamcorderProfile() or Preview.getCamcorderProfileDescription() for details); or return "" to let Preview choose quality
	boolean getVideoStabilizationPref(); // whether to use video stabilization for video
	boolean getForce4KPref(); // whether to force 4K mode - experimental, only really available for some devices that allow 4K recording but don't return it as an available resolution - not recommended for most uses
	String getVideoBitratePref(); // return "default" to let Preview choose
	String getVideoFPSPref(); // return "default" to let Preview choose
	long getVideoMaxDurationPref(); // time in ms after which to automatically stop video recording (return 0 for off)
	int getVideoRestartTimesPref(); // number of times to restart video recording after hitting max duration (return 0 for never auto-restarting)
	VideoMaxFileSize getVideoMaxFileSizePref() throws NoFreeStorageException; // see VideoMaxFileSize class for details
	boolean getVideoFlashPref(); // option to switch flash on/off while recording video (should be false in most cases!)
	boolean getVideoLowPowerCheckPref(); // whether to stop video automatically on critically low battery
	String getPreviewSizePref(); // "preference_preview_size_wysiwyg" is recommended (preview matches aspect ratio of photo resolution as close as possible), but can also be "preference_preview_size_display" to maximise the preview size
	String getPreviewRotationPref(); // return "0" for default; use "180" to rotate the preview 180 degrees
	String getLockOrientationPref(); // return "none" for default; use "portrait" or "landscape" to lock photos/videos to that orientation
    boolean getTouchCapturePref(); // whether to enable touch to capture
    boolean getDoubleTapCapturePref(); // whether to enable double-tap to capture
	boolean getPausePreviewPref(); // whether to pause the preview after taking a photo
	boolean getShowToastsPref();
	boolean getShutterSoundPref(); // whether to play sound when taking photo
	boolean getStartupFocusPref(); // whether to do autofocus on startup
	long getTimerPref(); // time in ms for timer (so 0 for off)
	String getRepeatPref(); // return number of times to repeat photo in a row (as a string), so "1" for default; return "unlimited" for unlimited
	long getRepeatIntervalPref(); // time in ms between repeat
	boolean getGeotaggingPref(); // whether to geotag photos
	boolean getRequireLocationPref(); // if getGeotaggingPref() returns true, and this method returns true, then phot/video will only be taken if location data is available
	boolean getRecordAudioPref(); // whether to record audio when recording video
	String getRecordAudioChannelsPref(); // either "audio_default", "audio_mono" or "audio_stereo"
	String getRecordAudioSourcePref(); // "audio_src_camcorder" is recommended, but other options are: "audio_src_mic", "audio_src_default", "audio_src_voice_communication"; see corresponding values in android.media.MediaRecorder.AudioSource
	int getZoomPref(); // index into Preview.getSupportedZoomRatios() array (each entry is the zoom factor, scaled by 100; array is sorted from min to max zoom)
	double getCalibratedLevelAngle(); // set to non-zero to calibrate the accelerometer used for the level angles
	// Camera2 only modes:
	long getExposureTimePref(); // only called if getISOPref() is not "default"
	float getFocusDistancePref();
	boolean isExpoBracketingPref(); // whether to enable burst photos with expo bracketing
    int getExpoBracketingNImagesPref(); // how many images to take for exposure bracketing
    double getExpoBracketingStopsPref(); // stops per image for exposure bracketing
	boolean getOptimiseAEForDROPref(); // see CameraController doc for setOptimiseAEForDRO().
	boolean isRawPref(); // whether to enable RAW photos
	boolean useCamera2FakeFlash(); // whether to enable CameraController.setUseCamera2FakeFlash() for Camera2 API
	boolean useCamera2FastBurst(); // whether to enable Camera2's captureBurst() for faster taking of expo-bracketing photos (generally should be true, but some devices have problems with captureBurst())

	// for testing purposes:
	boolean isTestAlwaysFocus(); // if true, pretend autofocus always successful

	// methods that transmit information/events (up to the Application whether to do anything or not)
    void cameraSetup(); // called when the camera is (re-)set up - should update UI elements/parameters that depend on camera settings
	void touchEvent(MotionEvent event);
	void startingVideo(); // called just before video recording starts
	void startedVideo(); // called just after video recording starts
	void stoppingVideo(); // called just before video recording stops; note that if startingVideo() is called but then video recording fails to start, this method will still be called, but startedVideo() and stoppedVideo() won't be called
	void stoppedVideo(final int video_method, final Uri uri, final String filename); // called after video recording stopped (uri/filename will be null if video is corrupt or not created); will be called iff startedVideo() was called
	void onFailedStartPreview(); // called if failed to start camera preview
	void onCameraError(); // called if the camera closes due to serious error.
	void onPhotoError(); // callback for failing to take a photo
	void onVideoInfo(int what, int extra); // callback for info when recording video (see MediaRecorder.OnInfoListener)
	void onVideoError(int what, int extra); // callback for errors when recording video (see MediaRecorder.OnErrorListener)
	void onVideoRecordStartError(CamcorderProfile profile); // callback for video recording failing to start
	void onVideoRecordStopError(CamcorderProfile profile); // callback for video recording being corrupted
	void onFailedReconnectError(); // failed to reconnect camera after stopping video recording
	void onFailedCreateVideoFileError(); // callback if unable to create file for recording video
	void hasPausedPreview(boolean paused); // called when the preview is paused or unpaused (due to getPausePreviewPref())
	void cameraInOperation(boolean in_operation); // called when the camera starts/stops being operation (taking photos or recording video, including if preview is paused after taking a photo), use to disable GUI elements during camera operation
	void turnFrontScreenFlashOn(); // called when front-screen "flash" required (for modes flash_frontscreen_auto, flash_frontscreen_on); the application should light up the screen, until cameraInOperation(false) is called
	void cameraClosed();
	void timerBeep(long remaining_time); // n.b., called once per second on timer countdown - so application can beep, or do whatever it likes

	// methods that request actions
	void layoutUI(); // application should layout UI that's on top of the preview
	void multitouchZoom(int new_zoom); // zoom has changed due to multitouch gesture on preview
	// the set/clear*Pref() methods are called if Preview decides to override the requested pref (because Camera device doesn't support requested pref) (clear*Pref() is called if the feature isn't supported at all)
	// the application can use this information to update its preferences
	void setCameraIdPref(int cameraId);
	void setFlashPref(String flash_value);
	void setFocusPref(String focus_value, boolean is_video);
	void setVideoPref(boolean is_video);
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
	void setVideoQualityPref(String video_quality);
	void setZoomPref(int zoom);
	void requestCameraPermission(); // for Android 6+: called when trying to open camera, but CAMERA permission not available
	void requestStoragePermission(); // for Android 6+: called when trying to open camera, but WRITE_EXTERNAL_STORAGE permission not available
	void requestRecordAudioPermission(); // for Android 6+: called when switching to (or starting up in) video mode, but RECORD_AUDIO permission not available
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
	void onContinuousFocusMove(boolean start); // called when focusing starts/stop in continuous picture mode (in photo mode only)
}
