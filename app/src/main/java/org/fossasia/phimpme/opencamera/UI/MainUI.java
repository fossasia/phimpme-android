package org.fossasia.phimpme.opencamera.UI;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.media.AudioManager;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.OrientationEventListener;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;

import com.mikepenz.iconics.view.IconicsImageView;

import org.fossasia.phimpme.R;
import org.fossasia.phimpme.opencamera.Camera.CameraActivity;
import org.fossasia.phimpme.opencamera.Camera.MyDebug;
import org.fossasia.phimpme.opencamera.Camera.PreferenceKeys;

/**
 * This contains functionality related to the main UI.
 */
public class MainUI {
	private static final String TAG = "MainUI";

	private final CameraActivity main_activity;

	private volatile boolean popup_view_is_open; // must be volatile for test project reading the state
	private PopupView popup_view;

	private int current_orientation;
	private boolean ui_placement_right = true;

	private boolean immersive_mode;
	private boolean show_gui = true; // result of call to showGUI() - false means a "reduced" GUI is displayed, whilst taking photo or video

	private boolean keydown_volume_up;
	private boolean keydown_volume_down;

	public MainUI(CameraActivity main_activity) {
		if (MyDebug.LOG)
			Log.d(TAG, "MainUI");
		this.main_activity = main_activity;

		this.setSeekbarColors();

		this.setIcon(R.id.popup);
		this.setIcon(R.id.exposure);
		this.setIcon(R.id.switch_camera);
		this.setIcon(R.id.audio_control);
	}

	private void setIcon(int id) {
		if (MyDebug.LOG)
			Log.d(TAG, "setIcon: " + id);
		ImageButton button = (ImageButton) main_activity.findViewById(id);
		button.setBackgroundColor(Color.argb(63, 63, 63, 63)); // n.b., rgb color seems to be ignored for Android 6 onwards, but still relevant for older versions
	}

	private void setSeekbarColors() {
		if (MyDebug.LOG)
			Log.d(TAG, "setSeekbarColors");
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			ColorStateList progress_color = ColorStateList.valueOf(Color.argb(255, 240, 240, 240));
			ColorStateList thumb_color = ColorStateList.valueOf(Color.argb(255, 255, 255, 255));

			SeekBar seekBar = (SeekBar) main_activity.findViewById(R.id.focus_seekbar);
			seekBar.setProgressTintList(progress_color);
			seekBar.setThumbTintList(thumb_color);

			seekBar = (SeekBar) main_activity.findViewById(R.id.exposure_seekbar);
			seekBar.setProgressTintList(progress_color);
			seekBar.setThumbTintList(thumb_color);

			seekBar = (SeekBar) main_activity.findViewById(R.id.iso_seekbar);
			seekBar.setProgressTintList(progress_color);
			seekBar.setThumbTintList(thumb_color);

			seekBar = (SeekBar) main_activity.findViewById(R.id.exposure_time_seekbar);
			seekBar.setProgressTintList(progress_color);
			seekBar.setThumbTintList(thumb_color);

			seekBar = (SeekBar) main_activity.findViewById(R.id.white_balance_seekbar);
			seekBar.setProgressTintList(progress_color);
			seekBar.setThumbTintList(thumb_color);
		}
	}

	/**
	 * Similar view.setRotation(ui_rotation), but achieves this via an animation.
	 */
	private void setViewRotation(View view, float ui_rotation) {
		//view.setRotation(ui_rotation);
		float rotate_by = ui_rotation - view.getRotation();
		if (rotate_by > 181.0f)
			rotate_by -= 360.0f;
		else if (rotate_by < -181.0f)
			rotate_by += 360.0f;
		// view.animate() modifies the view's rotation attribute, so it ends up equivalent to view.setRotation()
		// we use rotationBy() instead of rotation(), so we get the minimal rotation for clockwise vs anti-clockwise
		view.animate().rotationBy(rotate_by).setDuration(100).setInterpolator(new AccelerateDecelerateInterpolator()).start();
	}

	public void layoutUI() {
		long debug_time = 0;
		if (MyDebug.LOG) {
			Log.d(TAG, "layoutUI");
			debug_time = System.currentTimeMillis();
		}
		//main_activity.getPreview().updateUIPlacement();
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(main_activity);
		String ui_placement = sharedPreferences.getString(PreferenceKeys.getUIPlacementPreferenceKey(), "ui_right");
		// we cache the preference_ui_placement to save having to check it in the draw() method
		this.ui_placement_right = ui_placement.equals("ui_right");
		if (MyDebug.LOG)
			Log.d(TAG, "ui_placement: " + ui_placement);
		// new code for orientation fixed to landscape
		// the display orientation should be locked to landscape, but how many degrees is that?
		int rotation = main_activity.getWindowManager().getDefaultDisplay().getRotation();
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
		// getRotation is anti-clockwise, but current_orientation is clockwise, so we add rather than subtract
		// relative_orientation is clockwise from landscape-left
		//int relative_orientation = (current_orientation + 360 - degrees) % 360;
		int relative_orientation = (current_orientation + degrees) % 360;
		if (MyDebug.LOG) {
			Log.d(TAG, "    current_orientation = " + current_orientation);
			Log.d(TAG, "    degrees = " + degrees);
			Log.d(TAG, "    relative_orientation = " + relative_orientation);
		}
		int ui_rotation = (360 - relative_orientation) % 360;
		main_activity.getPreview().setUIRotation(ui_rotation);
		int align_left = RelativeLayout.ALIGN_LEFT;
		int align_right = RelativeLayout.ALIGN_RIGHT;
		int align_top = RelativeLayout.ALIGN_TOP;
		int align_bottom = RelativeLayout.ALIGN_BOTTOM;
		int left_of = RelativeLayout.LEFT_OF;
		int right_of = RelativeLayout.RIGHT_OF;
		int above = RelativeLayout.ABOVE;
		int below = RelativeLayout.BELOW;
		int align_parent_left = RelativeLayout.ALIGN_PARENT_LEFT;
		int align_parent_right = RelativeLayout.ALIGN_PARENT_RIGHT;
		int align_parent_top = RelativeLayout.ALIGN_PARENT_TOP;
		int align_parent_bottom = RelativeLayout.ALIGN_PARENT_BOTTOM;
		int icon_margin = 20;
		if (!ui_placement_right) {
			//align_top = RelativeLayout.ALIGN_BOTTOM;
			//align_bottom = RelativeLayout.ALIGN_TOP;
			above = RelativeLayout.BELOW;
			below = RelativeLayout.ABOVE;
			align_parent_top = RelativeLayout.ALIGN_PARENT_BOTTOM;
			align_parent_bottom = RelativeLayout.ALIGN_PARENT_TOP;
		}

		{
			TypedValue outValue = new TypedValue();
			main_activity.getTheme().resolveAttribute(android.R.attr.selectableItemBackground, outValue, true);
			int bgresourceId = outValue.resourceId;

			// we use a dummy button, so that the GUI buttons keep their positioning even if the Settings button is hidden (visibility set to View.GONE)
			View view = main_activity.findViewById(R.id.gui_anchor);
			RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
			layoutParams.addRule(align_parent_left, 0);
			layoutParams.addRule(align_right, R.id.preview);
			layoutParams.addRule(align_top,R.id.preview);
			layoutParams.addRule(align_parent_bottom, 0);
			layoutParams.addRule(left_of, 0);
			layoutParams.addRule(right_of, 0);
			view.setLayoutParams(layoutParams);
			setViewRotation(view, ui_rotation);


			view = main_activity.findViewById(R.id.popup);
			layoutParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
			layoutParams.addRule(align_top, R.id.preview);
			layoutParams.addRule(align_parent_bottom, 0);
			layoutParams.addRule(left_of, R.id.gui_anchor);
			layoutParams.addRule(right_of, 0);
			view.setLayoutParams(layoutParams);
			layoutParams.setMargins(icon_margin, icon_margin, icon_margin, 0);
			view.setBackgroundColor(Color.TRANSPARENT);
			view.setBackgroundResource(bgresourceId);
			setViewRotation(view, ui_rotation);


			view = main_activity.findViewById(R.id.exposure);
			layoutParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
			layoutParams.addRule(align_parent_top, 0);
			layoutParams.addRule(align_bottom, R.id.preview);
			layoutParams.addRule(left_of, R.id.popup);
			layoutParams.addRule(align_right, R.id.preview);
			layoutParams.addRule(right_of, 0);
			view.setLayoutParams(layoutParams);
			layoutParams.setMargins(icon_margin, icon_margin, icon_margin, icon_margin);
			view.setBackgroundColor(Color.TRANSPARENT);
			view.setBackgroundResource(bgresourceId);
			setViewRotation(view, ui_rotation);

			view = main_activity.findViewById(R.id.switch_camera);
			layoutParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
			layoutParams.addRule(align_left, R.id.preview);
			layoutParams.addRule(align_parent_right, 0);
			layoutParams.addRule(align_parent_top, 0);
			layoutParams.addRule(align_bottom, R.id.preview);
			layoutParams.addRule(left_of, 0);
			layoutParams.addRule(right_of, 0);
			view.setLayoutParams(layoutParams);
			layoutParams.setMargins(icon_margin, icon_margin, icon_margin, icon_margin);
			view.setBackgroundColor(Color.TRANSPARENT);
			view.setBackgroundResource(bgresourceId);
			setViewRotation(view, ui_rotation);

			view = main_activity.findViewById(R.id.audio_control);
			layoutParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
			layoutParams.addRule(align_parent_left, 0);
			layoutParams.addRule(align_parent_right, 0);
			layoutParams.addRule(align_top, R.id.preview);
			layoutParams.addRule(align_parent_bottom, 0);
			layoutParams.addRule(left_of, R.id.switch_camera);
			layoutParams.addRule(right_of, 0);
			view.setLayoutParams(layoutParams);
			view.setBackgroundColor(Color.TRANSPARENT);
			setViewRotation(view, ui_rotation);

			view = main_activity.findViewById(R.id.take_photo);
			((ImageButton) view).setImageResource(R.drawable.ic_capture);
			((ImageButton) view).setColorFilter(Color.WHITE);
			layoutParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
			layoutParams.addRule(align_bottom, R.id.preview);
			view.setBackgroundResource(bgresourceId);
			setViewRotation(view, ui_rotation);

			view = main_activity.findViewById(R.id.toggle_button);
			((IconicsImageView) view).setColorFilter(Color.WHITE);
			view.setBackgroundResource(bgresourceId);
			setViewRotation(view, ui_rotation);

			view = main_activity.findViewById(R.id.focus_seekbar);
			layoutParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
			layoutParams.addRule(align_left, R.id.preview);
			layoutParams.addRule(align_right, 0);
			layoutParams.addRule(right_of, 0);
			layoutParams.addRule(align_parent_top, 0);
			layoutParams.addRule(align_bottom,R.id.preview);
			view.setLayoutParams(layoutParams);
		}

		{
			// set seekbar info
			int width_dp;
			if (ui_rotation == 0 || ui_rotation == 180) {
				width_dp = 300;
			} else {
				width_dp = 200;
			}
			int height_dp = 50;
			final float scale = main_activity.getResources().getDisplayMetrics().density;
			int width_pixels = (int) (width_dp * scale + 0.5f); // convert dps to pixels
			int height_pixels = (int) (height_dp * scale + 0.5f); // convert dps to pixels
			int exposure_zoom_gap = (int) (4 * scale + 0.5f); // convert dps to pixels

			View view = main_activity.findViewById(R.id.sliders_container);
			setViewRotation(view, ui_rotation);

			view = main_activity.findViewById(R.id.exposure_seekbar);
			RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) view.getLayoutParams();
			lp.width = width_pixels;
			lp.height = height_pixels;
			view.setLayoutParams(lp);

//			view = main_activity.findViewById(R.id.exposure_seekbar_zoom);
//			view.setAlpha(0.5f);

			view = main_activity.findViewById(R.id.iso_seekbar);
			lp = (RelativeLayout.LayoutParams) view.getLayoutParams();
			lp.width = width_pixels;
			lp.height = height_pixels;
			view.setLayoutParams(lp);

			view = main_activity.findViewById(R.id.exposure_time_seekbar);
			lp = (RelativeLayout.LayoutParams) view.getLayoutParams();
			lp.width = width_pixels;
			lp.height = height_pixels;
			view.setLayoutParams(lp);

			view = main_activity.findViewById(R.id.white_balance_seekbar);
			lp = (RelativeLayout.LayoutParams) view.getLayoutParams();
			lp.width = width_pixels;
			lp.height = height_pixels;
			view.setLayoutParams(lp);
		}

		{
			View view = main_activity.findViewById(R.id.popup_container);
			RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
			//layoutParams.addRule(left_of, R.id.popup);
			layoutParams.addRule(align_right, R.id.popup);
			layoutParams.addRule(below, R.id.popup);
			layoutParams.addRule(align_bottom, R.id.preview);
			layoutParams.addRule(above, 0);
			layoutParams.addRule(align_parent_top, 0);
			view.setLayoutParams(layoutParams);

			setViewRotation(view, ui_rotation);
			// reset:
			view.setTranslationX(0.0f);
			view.setTranslationY(0.0f);
			if (MyDebug.LOG) {
				Log.d(TAG, "popup view width: " + view.getWidth());
				Log.d(TAG, "popup view height: " + view.getHeight());
			}
			if (ui_rotation == 0 || ui_rotation == 180) {
				view.setPivotX(view.getWidth() / 2.0f);
				view.setPivotY(view.getHeight() / 2.0f);
			} else {
				view.setPivotX(view.getWidth());
				view.setPivotY(ui_placement_right ? 0.0f : view.getHeight());
				if (ui_placement_right) {
					if (ui_rotation == 90)
						view.setTranslationY(view.getWidth());
					else if (ui_rotation == 270)
						view.setTranslationX(-view.getHeight());
				} else {
					if (ui_rotation == 90)
						view.setTranslationX(-view.getHeight());
					else if (ui_rotation == 270)
						view.setTranslationY(-view.getWidth());
				}
			}
		}
		// no need to call setSwitchCameraContentDescription()

		if (MyDebug.LOG) {
			Log.d(TAG, "layoutUI: total time: " + (System.currentTimeMillis() - debug_time));
		}
	}

	/**
	 * Set content description for switch camera button.
	 */
	public void setSwitchCameraContentDescription() {
		if (MyDebug.LOG)
			Log.d(TAG, "setSwitchCameraContentDescription()");
		if (main_activity.getPreview() != null && main_activity.getPreview().canSwitchCamera()) {
			ImageButton view = (ImageButton) main_activity.findViewById(R.id.switch_camera);
			int content_description;
			int cameraId = main_activity.getNextCameraId();
			if (main_activity.getPreview().getCameraControllerManager().isFrontFacing(cameraId)) {
				content_description = R.string.switch_to_front_camera;
			} else {
				content_description = R.string.switch_to_back_camera;
			}
			if (MyDebug.LOG)
				Log.d(TAG, "content_description: " + main_activity.getResources().getString(content_description));
			view.setContentDescription(main_activity.getResources().getString(content_description));
		}
	}

	public boolean getUIPlacementRight() {
		return this.ui_placement_right;
	}

	public void onOrientationChanged(int orientation) {
        /*if( MyDebug.LOG ) {
			Log.d(TAG, "onOrientationChanged()");
			Log.d(TAG, "orientation: " + orientation);
			Log.d(TAG, "current_orientation: " + current_orientation);
		}*/
		if (orientation == OrientationEventListener.ORIENTATION_UNKNOWN)
			return;
		int diff = Math.abs(orientation - current_orientation);
		if (diff > 180)
			diff = 360 - diff;
		// only change orientation when sufficiently changed
		if (diff > 60) {
			orientation = (orientation + 45) / 90 * 90;
			orientation = orientation % 360;
			if (orientation != current_orientation) {
				this.current_orientation = orientation;
				if (MyDebug.LOG) {
					Log.d(TAG, "current_orientation is now: " + current_orientation);
				}
				layoutUI();
			}
		}
	}

	public void setImmersiveMode(final boolean immersive_mode) {
		if (MyDebug.LOG)
			Log.d(TAG, "setImmersiveMode: " + immersive_mode);
		this.immersive_mode = immersive_mode;
		main_activity.runOnUiThread(new Runnable() {
			public void run() {
				SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(main_activity);
				// if going into immersive mode, the we should set GONE the ones that are set GONE in showGUI(false)
				//final int visibility_gone = immersive_mode ? View.GONE : View.VISIBLE;
				final int visibility = immersive_mode ? View.GONE : View.VISIBLE;
				if (MyDebug.LOG)
					Log.d(TAG, "setImmersiveMode: set visibility: " + visibility);
				// n.b., don't hide share and trash buttons, as they require immediate user input for us to continue
				View switchCameraButton = main_activity.findViewById(R.id.switch_camera);
				View exposureButton = main_activity.findViewById(R.id.exposure);
				View audioControlButton = main_activity.findViewById(R.id.audio_control);
				View popupButton = main_activity.findViewById(R.id.popup);
				if (main_activity.getPreview().getCameraControllerManager().getNumberOfCameras() > 1)
					switchCameraButton.setVisibility(visibility);
				if (main_activity.supportsExposureButton())
					exposureButton.setVisibility(visibility);
				if (main_activity.hasAudioControl())
					audioControlButton.setVisibility(visibility);
				popupButton.setVisibility(visibility);
				if (MyDebug.LOG) {
					Log.d(TAG, "has_zoom: " + main_activity.getPreview().supportsZoom());
				}

				String pref_immersive_mode = sharedPreferences.getString(PreferenceKeys.getImmersiveModePreferenceKey(), "immersive_mode_low_profile");
				if (pref_immersive_mode.equals("immersive_mode_everything")) {
					if (sharedPreferences.getBoolean(PreferenceKeys.getShowTakePhotoPreferenceKey(), true)) {
						View takePhotoButton = main_activity.findViewById(R.id.take_photo);
						takePhotoButton.setVisibility(visibility);
					}
				}
				if (!immersive_mode) {
					// make sure the GUI is set up as expected
					showGUI(show_gui);
				}
			}
		});
	}

	public boolean inImmersiveMode() {
		return immersive_mode;
	}

	public void showGUI(final boolean show) {
		if (MyDebug.LOG)
			Log.d(TAG, "showGUI: " + show);
		this.show_gui = show;
		if (inImmersiveMode())
			return;
		if (show && main_activity.usingKitKatImmersiveMode()) {
			// call to reset the timer
			main_activity.initImmersiveMode();
		}
		main_activity.runOnUiThread(new Runnable() {
			public void run() {
				final int visibility = show ? View.VISIBLE : View.GONE;
				View switchCameraButton = main_activity.findViewById(R.id.switch_camera);
				View exposureButton = main_activity.findViewById(R.id.exposure);
				View audioControlButton = main_activity.findViewById(R.id.audio_control);
				View popupButton = main_activity.findViewById(R.id.popup);
				View toggleButton = main_activity.findViewById(R.id.toggle_button);
				if (main_activity.getPreview().getCameraControllerManager().getNumberOfCameras() > 1)
					switchCameraButton.setVisibility(visibility);
				if (main_activity.supportsExposureButton() && !main_activity.getPreview().isVideo()) // still allow exposure when recording video
					exposureButton.setVisibility(visibility);
				if (main_activity.hasAudioControl())
					audioControlButton.setVisibility(visibility);
				if (!show) {
					closePopup(); // we still allow the popup when recording video, but need to update the UI (so it only shows flash options), so easiest to just close
				}
				if (!main_activity.getPreview().isVideo() || !main_activity.getPreview().supportsFlash())
					popupButton.setVisibility(visibility); // still allow popup in order to change flash mode when recording video
				toggleButton.setVisibility(visibility);
			}
		});
	}

	public void audioControlStarted() {
		ImageButton view = (ImageButton) main_activity.findViewById(R.id.audio_control);
		view.setImageResource(R.drawable.ic_mic_red_48dp);
		view.setContentDescription(main_activity.getResources().getString(R.string.audio_control_stop));
	}

	public void audioControlStopped() {
		ImageButton view = (ImageButton) main_activity.findViewById(R.id.audio_control);
		view.setImageResource(R.drawable.ic_mic_white_48dp);
		view.setContentDescription(main_activity.getResources().getString(R.string.audio_control_start));
	}

	public void toggleExposureUI() {
		if (MyDebug.LOG)
			Log.d(TAG, "toggleExposureUI");
		closePopup();
		View exposure_seek_bar = main_activity.findViewById(R.id.exposure_container);
		int exposure_visibility = exposure_seek_bar.getVisibility();
		View manual_exposure_seek_bar = main_activity.findViewById(R.id.manual_exposure_container);
		int manual_exposure_visibility = manual_exposure_seek_bar.getVisibility();
		boolean is_open = exposure_visibility == View.VISIBLE || manual_exposure_visibility == View.VISIBLE;
		if (is_open) {
			clearSeekBar();
		} else if (main_activity.getPreview().getCameraController() != null) {
			String iso_value = main_activity.getApplicationInterface().getISOPref();
			if (main_activity.getPreview().usingCamera2API() && !iso_value.equals("auto")) {
				// with Camera2 API, when using manual ISO we instead show sliders for ISO range and exposure time
				if (main_activity.getPreview().supportsISORange()) {
					manual_exposure_seek_bar.setVisibility(View.VISIBLE);
					SeekBar exposure_time_seek_bar = ((SeekBar) main_activity.findViewById(R.id.exposure_time_seekbar));
					if (main_activity.getPreview().supportsExposureTime()) {
						exposure_time_seek_bar.setVisibility(View.VISIBLE);
					} else {
						exposure_time_seek_bar.setVisibility(View.GONE);
					}
				}
			} else {
				if (main_activity.getPreview().supportsExposures()) {
					exposure_seek_bar.setVisibility(View.VISIBLE);
					LinearLayout seek_bar_zoom = (LinearLayout) main_activity.findViewById(R.id.exposure_seekbar_zoom);
					seek_bar_zoom.setVisibility(View.VISIBLE);
				}
			}

			if (main_activity.getPreview().supportsWhiteBalanceTemperature()) {
				// we also show slider for manual white balance, if in that mode
				String white_balance_value = main_activity.getApplicationInterface().getWhiteBalancePref();
				View manual_white_balance_seek_bar = main_activity.findViewById(R.id.manual_white_balance_container);
				if (main_activity.getPreview().usingCamera2API() && white_balance_value.equals("manual")) {
					manual_white_balance_seek_bar.setVisibility(View.VISIBLE);
				} else {
					manual_white_balance_seek_bar.setVisibility(View.GONE);
				}
			}
		}
	}

	public void changeSeekbar(int seekBarId, int change) {
		if (MyDebug.LOG)
			Log.d(TAG, "changeSeekbar: " + change);
		SeekBar seekBar = (SeekBar) main_activity.findViewById(seekBarId);
		int value = seekBar.getProgress();
		int new_value = value + change;
		if (new_value < 0)
			new_value = 0;
		else if (new_value > seekBar.getMax())
			new_value = seekBar.getMax();
		if (MyDebug.LOG) {
			Log.d(TAG, "value: " + value);
			Log.d(TAG, "new_value: " + new_value);
			Log.d(TAG, "max: " + seekBar.getMax());
		}
		if (new_value != value) {
			seekBar.setProgress(new_value);
		}
	}

	public void clearSeekBar() {
		View view = main_activity.findViewById(R.id.exposure_container);
		view.setVisibility(View.GONE);
		view = main_activity.findViewById(R.id.exposure_seekbar_zoom);
		view.setVisibility(View.GONE);
		view = main_activity.findViewById(R.id.manual_exposure_container);
		view.setVisibility(View.GONE);
		view = main_activity.findViewById(R.id.manual_white_balance_container);
		view.setVisibility(View.GONE);
	}

	public void setPopupIcon() {
		if (MyDebug.LOG)
			Log.d(TAG, "setPopupIcon");
		ImageButton popup = (ImageButton) main_activity.findViewById(R.id.popup);
		String flash_value = main_activity.getPreview().getCurrentFlashValue();
		if (MyDebug.LOG)
			Log.d(TAG, "flash_value: " + flash_value);
		if (flash_value != null && flash_value.equals("flash_off")) {
			popup.setImageResource(R.drawable.popup_flash_off);
		} else if (flash_value != null && flash_value.equals("flash_torch")) {
			popup.setImageResource(R.drawable.popup_flash_torch);
		} else if (flash_value != null && (flash_value.equals("flash_auto") || flash_value.equals("flash_frontscreen_auto"))) {
			popup.setImageResource(R.drawable.popup_flash_auto);
		} else if (flash_value != null && (flash_value.equals("flash_on") || flash_value.equals("flash_frontscreen_on"))) {
			popup.setImageResource(R.drawable.popup_flash_on);
		} else if (flash_value != null && flash_value.equals("flash_red_eye")) {
			popup.setImageResource(R.drawable.popup_flash_red_eye);
		} else {
			popup.setImageResource(R.drawable.popup);
		}
	}

	public void closePopup() {
		if (MyDebug.LOG)
			Log.d(TAG, "close popup");
		if (popupIsOpen()) {
			ViewGroup popup_container = (ViewGroup) main_activity.findViewById(R.id.popup_container);
			popup_container.removeAllViews();
			popup_view_is_open = false;
			/* Not destroying the popup doesn't really gain any performance.
			 * Also there are still outstanding bugs to fix if we wanted to do this:
			 *   - Not resetting the popup menu when switching between photo and video mode. See test testVideoPopup().
			 *   - When changing options like flash/focus, the new option isn't selected when reopening the popup menu. See test
			 *     testPopup().
			 *   - Changing settings potentially means we have to recreate the popup, so the natural place to do this is in
			 *     CameraActivity.updateForSettings(), but doing so makes the popup close when checking photo or video resolutions!
			 *     See test testSwitchResolution().
			 */
			destroyPopup();
			main_activity.initImmersiveMode(); // to reset the timer when closing the popup
		}
	}

	public boolean popupIsOpen() {
		return popup_view_is_open;
	}

	public void destroyPopup() {
		if (popupIsOpen()) {
			closePopup();
		}
		popup_view = null;
	}

	public void togglePopupSettings() {
		final ViewGroup popup_container = (ViewGroup) main_activity.findViewById(R.id.popup_container);
		if (popupIsOpen()) {
			closePopup();
			return;
		}
		if (main_activity.getPreview().getCameraController() == null) {
			if (MyDebug.LOG)
				Log.d(TAG, "camera not opened!");
			return;
		}

		if (MyDebug.LOG)
			Log.d(TAG, "open popup");

		clearSeekBar();
		main_activity.getPreview().cancelTimer(); // best to cancel any timer, in case we take a photo while settings window is open, or when changing settings
		main_activity.stopAudioListeners();

		final long time_s = System.currentTimeMillis();

		{
			// prevent popup being transparent
			popup_container.setBackgroundColor(Color.BLACK);
			popup_container.setAlpha(0.9f);
		}

		if (popup_view == null) {
			if (MyDebug.LOG)
				Log.d(TAG, "create new popup_view");
			popup_view = new PopupView(main_activity);
		} else {
			if (MyDebug.LOG)
				Log.d(TAG, "use cached popup_view");
		}
		popup_container.addView(popup_view);
		popup_view_is_open = true;

		// need to call layoutUI to make sure the new popup is oriented correctly
		// but need to do after the layout has been done, so we have a valid width/height to use
		// n.b., even though we only need the portion of layoutUI for the popup container, there
		// doesn't seem to be any performance benefit in only calling that part
		popup_container.getViewTreeObserver().addOnGlobalLayoutListener(
				new OnGlobalLayoutListener() {
					@SuppressWarnings("deprecation")
					@Override
					public void onGlobalLayout() {
						if (MyDebug.LOG)
							Log.d(TAG, "onGlobalLayout()");
						if (MyDebug.LOG)
							Log.d(TAG, "time after global layout: " + (System.currentTimeMillis() - time_s));
						layoutUI();
						if (MyDebug.LOG)
							Log.d(TAG, "time after layoutUI: " + (System.currentTimeMillis() - time_s));
						// stop listening - only want to call this once!
						if (Build.VERSION.SDK_INT > Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
							popup_container.getViewTreeObserver().removeOnGlobalLayoutListener(this);
						} else {
							popup_container.getViewTreeObserver().removeGlobalOnLayoutListener(this);
						}

						SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(main_activity);
						String ui_placement = sharedPreferences.getString(PreferenceKeys.getUIPlacementPreferenceKey(), "ui_right");
						boolean ui_placement_right = ui_placement.equals("ui_right");
						ScaleAnimation animation = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f, Animation.RELATIVE_TO_SELF, 1.0f, Animation.RELATIVE_TO_SELF, ui_placement_right ? 0.0f : 1.0f);
						animation.setDuration(100);
						popup_container.setAnimation(animation);
					}
				}
		);

		if (MyDebug.LOG)
			Log.d(TAG, "time to create popup: " + (System.currentTimeMillis() - time_s));
	}

	@SuppressWarnings("deprecation")
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (MyDebug.LOG)
			Log.d(TAG, "onKeyDown: " + keyCode);
		switch (keyCode) {
			case KeyEvent.KEYCODE_VOLUME_UP:
			case KeyEvent.KEYCODE_VOLUME_DOWN:
			case KeyEvent.KEYCODE_MEDIA_PREVIOUS: // media codes are for "selfie sticks" buttons
			case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
			case KeyEvent.KEYCODE_MEDIA_STOP: {
				if (keyCode == KeyEvent.KEYCODE_VOLUME_UP)
					keydown_volume_up = true;
				else if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN)
					keydown_volume_down = true;

				SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(main_activity);
				String volume_keys = sharedPreferences.getString(PreferenceKeys.getVolumeKeysPreferenceKey(), "volume_take_photo");

				if ((keyCode == KeyEvent.KEYCODE_MEDIA_PREVIOUS
						|| keyCode == KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE
						|| keyCode == KeyEvent.KEYCODE_MEDIA_STOP)
						&& !(volume_keys.equals("volume_take_photo"))) {
					AudioManager audioManager = (AudioManager) main_activity.getSystemService(Context.AUDIO_SERVICE);
					if (audioManager == null) break;
					if (!audioManager.isWiredHeadsetOn())
						break; // isWiredHeadsetOn() is deprecated, but comment says "Use only to check is a headset is connected or not."
				}

				switch (volume_keys) {
					case "volume_take_photo":
						main_activity.takePicture();
						return true;

					case "volume_zoom":
						if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
							main_activity.getPreview().zoomTo(main_activity.getPreview().getCameraController().getZoom() + 1);
						}
						else {
							main_activity.getPreview().zoomTo(main_activity.getPreview().getCameraController().getZoom() - 1);
						}
						return true;

					case "volume_focus":
						if (keydown_volume_up && keydown_volume_down) {
							if (MyDebug.LOG)
								Log.d(TAG, "take photo rather than focus, as both volume keys are down");
							main_activity.takePicture();
						} else if (main_activity.getPreview().getCurrentFocusValue() != null && main_activity.getPreview().getCurrentFocusValue().equals("focus_mode_manual2")) {
							if (keyCode == KeyEvent.KEYCODE_VOLUME_UP)
								main_activity.changeFocusDistance(-1);
							else
								main_activity.changeFocusDistance(1);
						} else {
							// important not to repeatedly request focus, even though main_activity.getPreview().requestAutoFocus() will cancel, as causes problem if key is held down (e.g., flash gets stuck on)
							// also check DownTime vs EventTime to prevent repeated focusing whilst the key is held down
							if (event.getDownTime() == event.getEventTime() && !main_activity.getPreview().isFocusWaiting()) {
								if (MyDebug.LOG)
									Log.d(TAG, "request focus due to volume key");
								main_activity.getPreview().requestAutoFocus();
							}
						}
						return true;
					case "volume_exposure":
						if (main_activity.getPreview().getCameraController() != null) {
							String value = sharedPreferences.getString(PreferenceKeys.getISOPreferenceKey(), main_activity.getPreview().getCameraController().getDefaultISO());
							boolean manual_iso = !value.equals("auto");
							if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
								if (manual_iso) {
									if (main_activity.getPreview().supportsISORange())
										main_activity.changeISO(1);
								} else
									main_activity.changeExposure(1);
							} else {
								if (manual_iso) {
									if (main_activity.getPreview().supportsISORange())
										main_activity.changeISO(-1);
								} else
									main_activity.changeExposure(-1);
							}
						}
						return true;
					case "volume_auto_stabilise":
						if (main_activity.supportsAutoStabilise()) {
							boolean auto_stabilise = sharedPreferences.getBoolean(PreferenceKeys.getAutoStabilisePreferenceKey(), false);
							auto_stabilise = !auto_stabilise;
							SharedPreferences.Editor editor = sharedPreferences.edit();
							editor.putBoolean(PreferenceKeys.getAutoStabilisePreferenceKey(), auto_stabilise);
							editor.apply();
							String message = main_activity.getResources().getString(R.string.preference_auto_stabilise) + ": " + main_activity.getResources().getString(auto_stabilise ? R.string.on : R.string.off);
							main_activity.getPreview().showToast(main_activity.getChangedAutoStabiliseToastBoxer(), message);
						} else {
							main_activity.getPreview().showToast(main_activity.getChangedAutoStabiliseToastBoxer(), R.string.auto_stabilise_not_supported);
						}
						return true;
					case "volume_really_nothing":
						// do nothing, but still return true so we don't change volume either
						return true;
				}
				// else do nothing here, but still allow changing of volume (i.e., the default behaviour)
				break;
			}
			case KeyEvent.KEYCODE_MENU: {
				// needed to support hardware menu button
				// tested successfully on Samsung S3 (via RTL)
				// see http://stackoverflow.com/questions/8264611/how-to-detect-when-user-presses-menu-key-on-their-android-device
				main_activity.openSettings();
				return true;
			}
			case KeyEvent.KEYCODE_CAMERA: {
				if (event.getRepeatCount() == 0) {
					main_activity.takePicture();
					return true;
				}
			}
			case KeyEvent.KEYCODE_FOCUS: {
				// important not to repeatedly request focus, even though main_activity.getPreview().requestAutoFocus() will cancel - causes problem with hardware camera key where a half-press means to focus
				// also check DownTime vs EventTime to prevent repeated focusing whilst the key is held down - see https://sourceforge.net/p/opencamera/tickets/174/ ,
				// or same issue above for volume key focus
				if (event.getDownTime() == event.getEventTime() && !main_activity.getPreview().isFocusWaiting()) {
					if (MyDebug.LOG)
						Log.d(TAG, "request focus due to focus key");
					main_activity.getPreview().requestAutoFocus();
				}
				return true;
			}

		}
		return false;
	}

	public void onKeyUp(int keyCode, KeyEvent event) {
		if (MyDebug.LOG)
			Log.d(TAG, "onKeyUp: " + keyCode);
		if (keyCode == KeyEvent.KEYCODE_VOLUME_UP)
			keydown_volume_up = false;
		else if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN)
			keydown_volume_down = false;
	}

	// for testing
	public View getPopupButton(String key) {
		return popup_view.getPopupButton(key);
	}
}
