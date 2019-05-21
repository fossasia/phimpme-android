package org.fossasia.phimpme.opencamera.Camera;

/** Stores all of the string keys used for SharedPreferences. */
public class PreferenceKeys {
  // must be static, to safely call from other Activities

  // arguably the static methods here that don't receive an argument could just be static final
  // strings? Though we may want to change some of them to be cameraId-specific in future

  /** If this preference is set, no longer show the intro dialog. */
  public static String getFirstTimePreferenceKey() {
    return "done_first_time";
  }

  /** If this preference is set, no longer show the auto-stabilise info dialog. */
  public static String getAutoStabiliseInfoPreferenceKey() {
    return "done_auto_stabilise_info";
  }

  /** If this preference is set, no longer show the HDR info dialog. */
  public static String getHDRInfoPreferenceKey() {
    return "done_hdr_info";
  }

  /** If this preference is set, no longer show the raw info dialog. */
  public static String getRawInfoPreferenceKey() {
    return "done_raw_info";
  }

  public static String getUseCamera2PreferenceKey() {
    return "preference_use_camera2";
  }

  public static String getFlashPreferenceKey(int cameraId) {
    return "flash_value_" + cameraId;
  }

  public static String getFocusPreferenceKey(int cameraId, boolean is_video) {
    return "focus_value_" + cameraId + "_" + is_video;
  }

  public static String getResolutionPreferenceKey(int cameraId) {
    return "camera_resolution_" + cameraId;
  }

  public static String getVideoQualityPreferenceKey(int cameraId) {
    return "video_quality_" + cameraId;
  }

  public static String getIsVideoPreferenceKey() {
    return "is_video";
  }

  public static String getExposurePreferenceKey() {
    return "preference_exposure";
  }

  public static String getColorEffectPreferenceKey() {
    return "preference_color_effect";
  }

  public static String getSceneModePreferenceKey() {
    return "preference_scene_mode";
  }

  public static String getWhiteBalancePreferenceKey() {
    return "preference_white_balance";
  }

  public static String getWhiteBalanceTemperaturePreferenceKey() {
    return "preference_white_balance_temperature";
  }

  public static String getISOPreferenceKey() {
    return "preference_iso";
  }

  public static String getExposureTimePreferenceKey() {
    return "preference_exposure_time";
  }

  public static String getRawPreferenceKey() {
    return "preference_raw";
  }

  public static String getExpoBracketingNImagesPreferenceKey() {
    return "preference_expo_bracketing_n_images";
  }

  public static String getExpoBracketingStopsPreferenceKey() {
    return "preference_expo_bracketing_stops";
  }

  public static String getVolumeKeysPreferenceKey() {
    return "preference_volume_keys";
  }

  public static String getAudioControlPreferenceKey() {
    return "preference_audio_control";
  }

  public static String getAudioNoiseControlSensitivityPreferenceKey() {
    return "preference_audio_noise_control_sensitivity";
  }

  public static String getQualityPreferenceKey() {
    return "preference_quality";
  }

  public static String getAutoStabilisePreferenceKey() {
    return "preference_auto_stabilise";
  }

  public static String getPhotoModePreferenceKey() {
    return "preference_photo_mode";
  }

  public static String getHDRSaveExpoPreferenceKey() {
    return "preference_hdr_save_expo";
  }

  public static String getLocationPreferenceKey() {
    return "preference_location";
  }

  public static String getGPSDirectionPreferenceKey() {
    return "preference_gps_direction";
  }

  public static String getRequireLocationPreferenceKey() {
    return "preference_require_location";
  }

  public static String getStampPreferenceKey() {
    return "preference_stamp";
  }

  public static String getStampDateFormatPreferenceKey() {
    return "preference_stamp_dateformat";
  }

  public static String getStampTimeFormatPreferenceKey() {
    return "preference_stamp_timeformat";
  }

  public static String getStampGPSFormatPreferenceKey() {
    return "preference_stamp_gpsformat";
  }

  public static String getTextStampPreferenceKey() {
    return "preference_textstamp";
  }

  public static String getStampFontSizePreferenceKey() {
    return "preference_stamp_fontsize";
  }

  public static String getStampFontColorPreferenceKey() {
    return "preference_stamp_font_color";
  }

  public static String getStampStyleKey() {
    return "preference_stamp_style";
  }

  public static String getVideoSubtitlePref() {
    return "preference_video_subtitle";
  }

  public static String getFrontCameraMirrorKey() {
    return "preference_front_camera_mirror";
  }

  public static String getBackgroundPhotoSavingPreferenceKey() {
    return "preference_background_photo_saving";
  }

  public static String getCamera2FakeFlashPreferenceKey() {
    return "preference_camera2_fake_flash";
  }

  public static String getCamera2FastBurstPreferenceKey() {
    return "preference_camera2_fast_burst";
  }

  public static String getUIPlacementPreferenceKey() {
    return "preference_ui_placement";
  }

  public static String getTouchCapturePreferenceKey() {
    return "preference_touch_capture";
  }

  public static String getPausePreviewPreferenceKey() {
    return "preference_pause_preview";
  }

  public static String getShowToastsPreferenceKey() {
    return "preference_show_toasts";
  }

  public static String getThumbnailAnimationPreferenceKey() {
    return "preference_thumbnail_animation";
  }

  public static String getTakePhotoBorderPreferenceKey() {
    return "preference_take_photo_border";
  }

  public static String getShowWhenLockedPreferenceKey() {
    return "preference_show_when_locked";
  }

  public static String getStartupFocusPreferenceKey() {
    return "preference_startup_focus";
  }

  public static String getKeepDisplayOnPreferenceKey() {
    return "preference_keep_display_on";
  }

  public static String getMaxBrightnessPreferenceKey() {
    return "preference_max_brightness";
  }

  public static String getUsingSAFPreferenceKey() {
    return "preference_using_saf";
  }

  public static String getSaveLocationPreferenceKey() {
    return "preference_save_location";
  }

  public static String getSaveLocationSAFPreferenceKey() {
    return "preference_save_location_saf";
  }

  public static String getSavePhotoPrefixPreferenceKey() {
    return "preference_save_photo_prefix";
  }

  public static String getSaveVideoPrefixPreferenceKey() {
    return "preference_save_video_prefix";
  }

  public static String getSaveZuluTimePreferenceKey() {
    return "preference_save_zulu_time";
  }

  public static String getShowZoomControlsPreferenceKey() {
    return "preference_show_zoom_controls";
  }

  public static String getShowZoomSliderControlsPreferenceKey() {
    return "preference_show_zoom_slider_controls";
  }

  public static String getShowTakePhotoPreferenceKey() {
    return "preference_show_take_photo";
  }

  public static String getShowZoomPreferenceKey() {
    return "preference_show_zoom";
  }

  public static String getShowISOPreferenceKey() {
    return "preference_show_iso";
  }

  public static String getShowAnglePreferenceKey() {
    return "preference_show_angle";
  }

  public static String getShowAngleLinePreferenceKey() {
    return "preference_show_angle_line";
  }

  public static String getShowPitchLinesPreferenceKey() {
    return "preference_show_pitch_lines";
  }

  public static String getShowGeoDirectionLinesPreferenceKey() {
    return "preference_show_geo_direction_lines";
  }

  public static String getShowAngleHighlightColorPreferenceKey() {
    return "preference_angle_highlight_color";
  }

  public static String getCalibratedLevelAnglePreferenceKey() {
    return "preference_calibrate_level_angle";
  }

  public static String getShowGeoDirectionPreferenceKey() {
    return "preference_show_geo_direction";
  }

  public static String getShowFreeMemoryPreferenceKey() {
    return "preference_free_memory";
  }

  public static String getShowTimePreferenceKey() {
    return "preference_show_time";
  }

  public static String getShowBatteryPreferenceKey() {
    return "preference_show_battery";
  }

  public static String getShowGridPreferenceKey() {
    return "preference_grid";
  }

  public static String getShowCropGuidePreferenceKey() {
    return "preference_crop_guide";
  }

  public static String getFaceDetectionPreferenceKey() {
    return "preference_face_detection";
  }

  public static String getVideoStabilizationPreferenceKey() {
    return "preference_video_stabilization";
  }

  public static String getForceVideo4KPreferenceKey() {
    return "preference_force_video_4k";
  }

  public static String getVideoBitratePreferenceKey() {
    return "preference_video_bitrate";
  }

  public static String getVideoFPSPreferenceKey() {
    return "preference_video_fps";
  }

  public static String getVideoMaxDurationPreferenceKey() {
    return "preference_video_max_duration";
  }

  public static String getVideoRestartPreferenceKey() {
    return "preference_video_restart";
  }

  public static String getVideoMaxFileSizePreferenceKey() {
    return "preference_video_max_filesize";
  }

  public static String getVideoRestartMaxFileSizePreferenceKey() {
    return "preference_video_restart_max_filesize";
  }

  public static String getVideoFlashPreferenceKey() {
    return "preference_video_flash";
  }

  public static String getVideoLowPowerCheckPreferenceKey() {
    return "preference_video_low_power_check";
  }

  public static String getLockVideoPreferenceKey() {
    return "preference_lock_video";
  }

  public static String getRecordAudioPreferenceKey() {
    return "preference_record_audio";
  }

  public static String getRecordAudioChannelsPreferenceKey() {
    return "preference_record_audio_channels";
  }

  public static String getRecordAudioSourcePreferenceKey() {
    return "preference_record_audio_src";
  }

  public static String getPreviewSizePreferenceKey() {
    return "preference_preview_size";
  }

  public static String getRotatePreviewPreferenceKey() {
    return "preference_rotate_preview";
  }

  public static String getLockOrientationPreferenceKey() {
    return "preference_lock_orientation";
  }

  public static String getTimerPreferenceKey() {
    return "preference_timer";
  }

  public static String getTimerBeepPreferenceKey() {
    return "preference_timer_beep";
  }

  public static String getTimerSpeakPreferenceKey() {
    return "preference_timer_speak";
  }

  public static String getBurstModePreferenceKey() {
    return "preference_burst_mode";
  }

  public static String getBurstIntervalPreferenceKey() {
    return "preference_burst_interval";
  }

  public static String getSoundModePreferenceKey() {
    return "preference_sound_mode";
  }

  public static String getImmersiveModePreferenceKey() {
    return "preference_immersive_mode";
  }
}
