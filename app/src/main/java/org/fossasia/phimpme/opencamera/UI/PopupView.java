package org.fossasia.phimpme.opencamera.UI;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import org.fossasia.phimpme.R;
import org.fossasia.phimpme.opencamera.Camera.CameraActivity;
import org.fossasia.phimpme.opencamera.Camera.MyApplicationInterface;
import org.fossasia.phimpme.opencamera.Camera.MyDebug;
import org.fossasia.phimpme.opencamera.Camera.PreferenceKeys;
import org.fossasia.phimpme.opencamera.CameraController.CameraController;
import org.fossasia.phimpme.opencamera.Preview.Preview;

/** This defines the UI for the "popup" button, that provides quick access to a range of options. */
public class PopupView extends LinearLayout {
  private static final String TAG = "PopupView";
  public static final float ALPHA_BUTTON_SELECTED = 1.0f;
  public static final float ALPHA_BUTTON = 0.6f;

  private int picture_size_index = -1;
  private int video_size_index = -1;
  private int timer_index = -1;
  private int burst_mode_index = -1;
  private int grid_index = -1;
  public static int sound_index;

  private final Map<String, View> popup_buttons = new Hashtable<>();

  public PopupView(Context context) {
    super(context);
    if (MyDebug.LOG) Log.d(TAG, "new PopupView: " + this);

    this.setOrientation(LinearLayout.VERTICAL);

    final CameraActivity main_activity = (CameraActivity) this.getContext();
    final Preview preview = main_activity.getPreview();
    {
      List<String> supported_flash_values = preview.getSupportedFlashValues();
      addButtonOptionsToPopup(
          supported_flash_values,
          R.array.flash_icons,
          R.array.flash_values,
          getResources().getString(R.string.flash_mode),
          preview.getCurrentFlashValue(),
          "TEST_FLASH",
          new ButtonOptionsPopupListener() {
            @Override
            public void onClick(String option) {
              if (MyDebug.LOG) Log.d(TAG, "clicked flash: " + option);
              preview.updateFlash(option);
              main_activity.getMainUI().setPopupIcon();
              main_activity.closePopup();
            }
          });
    }

    if (preview.isVideo() && preview.isTakingPhoto()) {
      // don't add any more options
    } else {
      // make a copy of getSupportedFocusValues() so we can modify it
      List<String> supported_focus_values = preview.getSupportedFocusValues();
      if (supported_focus_values != null) {
        supported_focus_values = new ArrayList<>(supported_focus_values);
        // only show appropriate continuous focus mode
        if (preview.isVideo()) {
          supported_focus_values.remove("focus_mode_continuous_picture");
        } else {
          supported_focus_values.remove("focus_mode_continuous_video");
        }
      }
      addButtonOptionsToPopup(
          supported_focus_values,
          R.array.focus_mode_icons,
          R.array.focus_mode_values,
          getResources().getString(R.string.focus_mode),
          preview.getCurrentFocusValue(),
          "TEST_FOCUS",
          new ButtonOptionsPopupListener() {
            @Override
            public void onClick(String option) {
              if (MyDebug.LOG) Log.d(TAG, "clicked focus: " + option);
              preview.updateFocus(option, false, true);
              main_activity.closePopup();
            }
          });

      List<String> supported_isos;
      final String manual_value = "m";
      if (preview.supportsISORange()) {
        if (MyDebug.LOG) Log.d(TAG, "supports ISO range");
        int min_iso = preview.getMinimumISO();
        int max_iso = preview.getMaximumISO();
        List<String> values = new ArrayList<>();
        values.add("auto");
        values.add(manual_value);
        int[] iso_values = {50, 100, 200, 400, 800, 1600, 3200, 6400};
        values.add("" + min_iso);
        for (int iso_value : iso_values) {
          if (iso_value > min_iso && iso_value < max_iso) {
            values.add("" + iso_value);
          }
        }
        values.add("" + max_iso);
        supported_isos = values;
      } else {
        supported_isos = preview.getSupportedISOs();
      }
      SharedPreferences sharedPreferences =
          PreferenceManager.getDefaultSharedPreferences(main_activity);
      String current_iso =
          sharedPreferences.getString(PreferenceKeys.getISOPreferenceKey(), "auto");
      // if the manual ISO value isn't one of the "preset" values, then instead highlight the manual
      // ISO icon
      if (!current_iso.equals("auto")
          && supported_isos != null
          && supported_isos.contains(manual_value)
          && !supported_isos.contains(current_iso)) current_iso = manual_value;
      // n.b., we hardcode the string "ISO" as we don't want it translated - firstly more consistent
      // with the ISO values returned by the driver, secondly need to worry about the size of the
      // buttons, so don't want risk of a translated string being too long
      addButtonOptionsToPopup(
          supported_isos,
          -1,
          -1,
          "ISO",
          current_iso,
          "TEST_ISO",
          new ButtonOptionsPopupListener() {
            @Override
            public void onClick(String option) {
              if (MyDebug.LOG) Log.d(TAG, "clicked iso: " + option);
              SharedPreferences sharedPreferences =
                  PreferenceManager.getDefaultSharedPreferences(main_activity);
              SharedPreferences.Editor editor = sharedPreferences.edit();
              editor.putString(PreferenceKeys.getISOPreferenceKey(), option);
              String toast_option = option;
              if (option.equals("auto")) {
                if (MyDebug.LOG) Log.d(TAG, "switched from manual to auto iso");
                // also reset exposure time when changing from manual to auto from the popup menu:
                editor.putLong(
                    PreferenceKeys.getExposureTimePreferenceKey(),
                    CameraController.EXPOSURE_TIME_DEFAULT);
              } else {
                if (MyDebug.LOG) Log.d(TAG, "switched from auto to manual iso");
                if (option.equals("m")) {
                  // if we used the generic "manual", then instead try to preserve the current iso
                  // if it exists
                  if (preview.getCameraController() != null
                      && preview.getCameraController().captureResultHasIso()) {
                    int iso = preview.getCameraController().captureResultIso();
                    if (MyDebug.LOG) Log.d(TAG, "apply existing iso of " + iso);
                    editor.putString(PreferenceKeys.getISOPreferenceKey(), "" + iso);
                    toast_option = "" + iso;
                  } else {
                    if (MyDebug.LOG) Log.d(TAG, "no existing iso available");
                    // use a default
                    final int iso = 800;
                    editor.putString(PreferenceKeys.getISOPreferenceKey(), "" + iso);
                    toast_option = "" + iso;
                  }
                }
                if (preview.usingCamera2API()) {
                  // if changing from auto to manual, preserve the current exposure time if it
                  // exists
                  if (preview.getCameraController() != null
                      && preview.getCameraController().captureResultHasExposureTime()) {
                    long exposure_time = preview.getCameraController().captureResultExposureTime();
                    if (MyDebug.LOG) Log.d(TAG, "apply existing exposure time of " + exposure_time);
                    editor.putLong(PreferenceKeys.getExposureTimePreferenceKey(), exposure_time);
                  } else {
                    if (MyDebug.LOG) Log.d(TAG, "no existing exposure time available");
                  }
                }
              }
              editor.apply();

              main_activity.updateForSettings("ISO: " + toast_option);
              main_activity.closePopup();
            }
          });

      final List<String> photo_modes = new ArrayList<>();
      final List<MyApplicationInterface.PhotoMode> photo_mode_values = new ArrayList<>();
      photo_modes.add(getResources().getString(R.string.photo_mode_standard));
      photo_mode_values.add(MyApplicationInterface.PhotoMode.Standard);
      if (main_activity.supportsDRO()) {
        photo_modes.add(getResources().getString(R.string.photo_mode_dro));
        photo_mode_values.add(MyApplicationInterface.PhotoMode.DRO);
      }
      if (main_activity.supportsHDR()) {
        photo_modes.add(getResources().getString(R.string.photo_mode_hdr));
        photo_mode_values.add(MyApplicationInterface.PhotoMode.HDR);
      }
      if (main_activity.supportsExpoBracketing()) {
        photo_modes.add(getResources().getString(R.string.photo_mode_expo_bracketing));
        photo_mode_values.add(MyApplicationInterface.PhotoMode.ExpoBracketing);
      }
      if (photo_modes.size() > 1) {
        MyApplicationInterface.PhotoMode photo_mode =
            main_activity.getApplicationInterface().getPhotoMode();
        String current_mode = null;
        for (int i = 0; i < photo_modes.size() && current_mode == null; i++) {
          if (photo_mode_values.get(i) == photo_mode) {
            current_mode = photo_modes.get(i);
          }
        }
        if (current_mode == null) {
          // applicationinterface should only report we're in a mode if it's supported, but just in
          // case...
          if (MyDebug.LOG) Log.e(TAG, "can't find current mode for mode: " + photo_mode);
          current_mode = ""; // this will mean no photo mode is highlighted in the UI
        }

        addTitleToPopup(getResources().getString(R.string.photo_mode));

        addButtonOptionsToPopup(
            photo_modes,
            -1,
            -1,
            "",
            current_mode,
            "TEST_PHOTO_MODE",
            new ButtonOptionsPopupListener() {
              @Override
              public void onClick(String option) {
                if (MyDebug.LOG) Log.d(TAG, "clicked photo mode: " + option);

                int option_id = -1;
                for (int i = 0; i < photo_modes.size() && option_id == -1; i++) {
                  if (option.equals(photo_modes.get(i))) option_id = i;
                }
                if (MyDebug.LOG) Log.d(TAG, "mode id: " + option_id);
                if (option_id == -1) {
                  if (MyDebug.LOG) Log.e(TAG, "unknown mode id: " + option_id);
                } else {
                  MyApplicationInterface.PhotoMode new_photo_mode =
                      photo_mode_values.get(option_id);
                  String toast_message = option;
                  if (new_photo_mode == MyApplicationInterface.PhotoMode.ExpoBracketing)
                    toast_message =
                        getResources().getString(R.string.photo_mode_expo_bracketing_full);
                  final SharedPreferences sharedPreferences =
                      PreferenceManager.getDefaultSharedPreferences(main_activity);
                  SharedPreferences.Editor editor = sharedPreferences.edit();
                  final String HDRMessage = "preference_photo_mode_hdr";
                  boolean done_dialog = false;
                  if (new_photo_mode == MyApplicationInterface.PhotoMode.HDR
                      && !HDRMessage.equals(
                          sharedPreferences.getString(
                              PreferenceKeys.getPhotoModePreferenceKey(), " "))) {
                    boolean done_hdr_info =
                        sharedPreferences.contains(PreferenceKeys.getHDRInfoPreferenceKey());
                    if (!done_hdr_info) {
                      showInfoDialog(
                          R.string.photo_mode_hdr,
                          R.string.hdr_info,
                          PreferenceKeys.getHDRInfoPreferenceKey());
                      done_dialog = true;
                    }
                  } else done_dialog = true;
                  if (new_photo_mode == MyApplicationInterface.PhotoMode.Standard) {
                    editor.putString(
                        PreferenceKeys.getPhotoModePreferenceKey(), "preference_photo_mode_std");
                  } else if (new_photo_mode == MyApplicationInterface.PhotoMode.DRO) {
                    editor.putString(
                        PreferenceKeys.getPhotoModePreferenceKey(), "preference_photo_mode_dro");
                  } else if (new_photo_mode == MyApplicationInterface.PhotoMode.HDR) {
                    editor.putString(PreferenceKeys.getPhotoModePreferenceKey(), HDRMessage);
                  } else if (new_photo_mode == MyApplicationInterface.PhotoMode.ExpoBracketing) {
                    editor.putString(
                        PreferenceKeys.getPhotoModePreferenceKey(),
                        "preference_photo_mode_expo_bracketing");
                  } else {
                    if (MyDebug.LOG) Log.e(TAG, "unknown new_photo_mode: " + new_photo_mode);
                  }
                  editor.apply();
                  if (done_dialog) {
                    // no need to show toast
                    toast_message = null;
                  }

                  main_activity.updateForSettings(toast_message);
                  main_activity.closePopup();
                }
              }
            });
      }

      if (main_activity.supportsAutoStabilise()) {
        CheckBox checkBox = new CheckBox(main_activity);
        checkBox.setText(getResources().getString(R.string.preference_auto_stabilise));
        checkBox.setTextColor(Color.WHITE);

        boolean auto_stabilise =
            sharedPreferences.getBoolean(PreferenceKeys.getAutoStabilisePreferenceKey(), false);
        checkBox.setChecked(auto_stabilise);
        checkBox.setOnCheckedChangeListener(
            new CompoundButton.OnCheckedChangeListener() {
              public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                final SharedPreferences sharedPreferences =
                    PreferenceManager.getDefaultSharedPreferences(main_activity);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(PreferenceKeys.getAutoStabilisePreferenceKey(), isChecked);
                editor.apply();

                boolean done_dialog = false;
                if (isChecked) {
                  boolean done_auto_stabilise_info =
                      sharedPreferences.contains(
                          PreferenceKeys.getAutoStabiliseInfoPreferenceKey());
                  if (!done_auto_stabilise_info) {
                    showInfoDialog(
                        R.string.preference_auto_stabilise,
                        R.string.auto_stabilise_info,
                        PreferenceKeys.getAutoStabiliseInfoPreferenceKey());
                    done_dialog = true;
                  }
                }

                if (!done_dialog) {
                  String message =
                      getResources().getString(R.string.preference_auto_stabilise)
                          + ": "
                          + getResources().getString(isChecked ? R.string.on : R.string.off);
                  preview.showToast(main_activity.getChangedAutoStabiliseToastBoxer(), message);
                }
                main_activity.closePopup();
              }
            });

        this.addView(checkBox);
      }

      final List<CameraController.Size> picture_sizes = preview.getSupportedPictureSizes();
      picture_size_index = preview.getCurrentPictureSizeIndex();
      final List<String> picture_size_strings = new ArrayList<>();
      for (CameraController.Size picture_size : picture_sizes) {
        String size_string =
            picture_size.width
                + " x "
                + picture_size.height
                + " "
                + Preview.getMPString(picture_size.width, picture_size.height);
        picture_size_strings.add(size_string);
      }
      addArrayOptionsToPopup(
          picture_size_strings,
          getResources().getString(R.string.preference_resolution),
          false,
          picture_size_index,
          false,
          "PHOTO_RESOLUTIONS",
          new ArrayOptionsPopupListener() {
            final Handler handler = new Handler();
            final Runnable update_runnable =
                new Runnable() {
                  @Override
                  public void run() {
                    if (MyDebug.LOG) Log.d(TAG, "update settings due to resolution change");
                    main_activity.updateForSettings("");
                  }
                };

            private void update() {
              if (picture_size_index == -1) return;
              CameraController.Size new_size = picture_sizes.get(picture_size_index);
              String resolution_string = new_size.width + " " + new_size.height;
              SharedPreferences sharedPreferences =
                  PreferenceManager.getDefaultSharedPreferences(main_activity);
              SharedPreferences.Editor editor = sharedPreferences.edit();
              editor.putString(
                  PreferenceKeys.getResolutionPreferenceKey(preview.getCameraId()),
                  resolution_string);
              editor.apply();

              // make it easier to scroll through the list of resolutions without a pause each time
              handler.removeCallbacks(update_runnable);
              handler.postDelayed(update_runnable, 400);
            }

            @Override
            public int onClickPrev() {
              if (picture_size_index != -1 && picture_size_index > 0) {
                picture_size_index--;
                update();
                return picture_size_index;
              }
              return -1;
            }

            @Override
            public int onClickNext() {
              if (picture_size_index != -1 && picture_size_index < picture_sizes.size() - 1) {
                picture_size_index++;
                update();
                return picture_size_index;
              }
              return -1;
            }
          });

      final String[] timer_values = getResources().getStringArray(R.array.preference_timer_values);
      String[] timer_entries = getResources().getStringArray(R.array.preference_timer_entries);
      String timer_value = sharedPreferences.getString(PreferenceKeys.getTimerPreferenceKey(), "0");
      timer_index = Arrays.asList(timer_values).indexOf(timer_value);
      if (timer_index == -1) {
        if (MyDebug.LOG) Log.d(TAG, "can't find timer_value " + timer_value + " in timer_values!");
        timer_index = 0;
      }
      addArrayOptionsToPopup(
          Arrays.asList(timer_entries),
          getResources().getString(R.string.preference_timer),
          true,
          timer_index,
          false,
          "TIMER",
          new ArrayOptionsPopupListener() {
            private void update() {
              if (timer_index == -1) return;
              String new_timer_value = timer_values[timer_index];
              SharedPreferences sharedPreferences =
                  PreferenceManager.getDefaultSharedPreferences(main_activity);
              SharedPreferences.Editor editor = sharedPreferences.edit();
              editor.putString(PreferenceKeys.getTimerPreferenceKey(), new_timer_value);
              editor.apply();
            }

            @Override
            public int onClickPrev() {
              if (timer_index != -1 && timer_index > 0) {
                timer_index--;
                update();
                return timer_index;
              }
              return -1;
            }

            @Override
            public int onClickNext() {
              if (timer_index != -1 && timer_index < timer_values.length - 1) {
                timer_index++;
                update();
                return timer_index;
              }
              return -1;
            }
          });

      final String[] burst_mode_values =
          getResources().getStringArray(R.array.preference_burst_mode_values);
      String[] burst_mode_entries =
          getResources().getStringArray(R.array.preference_burst_mode_entries);
      String burst_mode_value =
          sharedPreferences.getString(PreferenceKeys.getBurstModePreferenceKey(), "1");
      burst_mode_index = Arrays.asList(burst_mode_values).indexOf(burst_mode_value);
      if (burst_mode_index == -1) {
        if (MyDebug.LOG)
          Log.d(TAG, "can't find burst_mode_value " + burst_mode_value + " in burst_mode_values!");
        burst_mode_index = 0;
      }
      addArrayOptionsToPopup(
          Arrays.asList(burst_mode_entries),
          getResources().getString(R.string.preference_burst_mode),
          true,
          burst_mode_index,
          false,
          "BURST_MODE",
          new ArrayOptionsPopupListener() {
            private void update() {
              if (burst_mode_index == -1) return;
              String new_burst_mode_value = burst_mode_values[burst_mode_index];
              SharedPreferences sharedPreferences =
                  PreferenceManager.getDefaultSharedPreferences(main_activity);
              SharedPreferences.Editor editor = sharedPreferences.edit();
              editor.putString(PreferenceKeys.getBurstModePreferenceKey(), new_burst_mode_value);
              editor.apply();
            }

            @Override
            public int onClickPrev() {
              if (burst_mode_index != -1 && burst_mode_index > 0) {
                burst_mode_index--;
                update();
                return burst_mode_index;
              }
              return -1;
            }

            @Override
            public int onClickNext() {
              if (burst_mode_index != -1 && burst_mode_index < burst_mode_values.length - 1) {
                burst_mode_index++;
                update();
                return burst_mode_index;
              }
              return -1;
            }
          });

      sound_index =
          sharedPreferences.getInt(PreferenceKeys.getSoundModePreferenceKey(), sound_index);
      String[] sound_states_entries =
          getResources().getStringArray(R.array.preference_sound_states);
      addArrayOptionsToPopup(
          Arrays.asList(sound_states_entries),
          getResources().getString(R.string.preference_shutter_sound),
          true,
          sound_index,
          false,
          getResources().getString(R.string.preference_shutter_sound),
          new ArrayOptionsPopupListener() {
            private void update() {
              SharedPreferences sharedPreferences =
                  PreferenceManager.getDefaultSharedPreferences(main_activity);
              SharedPreferences.Editor editor = sharedPreferences.edit();
              editor.putInt(PreferenceKeys.getSoundModePreferenceKey(), sound_index);
              editor.apply();
            }

            @Override
            public int onClickPrev() {
              if (sound_index != -1 && sound_index > 0) {
                sound_index--;
                update();
                return sound_index;
              }
              return -1;
            }

            @Override
            public int onClickNext() {
              if (sound_index != -1) {
                sound_index++;
                update();
                return sound_index;
              }
              return -1;
            }
          });

      final String[] grid_values = getResources().getStringArray(R.array.preference_grid_values);
      String[] grid_entries = getResources().getStringArray(R.array.preference_grid_entries);
      String grid_value =
          sharedPreferences.getString(
              PreferenceKeys.getShowGridPreferenceKey(), "preference_grid_none");
      grid_index = Arrays.asList(grid_values).indexOf(grid_value);
      if (grid_index == -1) {
        if (MyDebug.LOG) Log.d(TAG, "can't find grid_value " + grid_value + " in grid_values!");
        grid_index = 0;
      }
      addArrayOptionsToPopup(
          Arrays.asList(grid_entries),
          getResources().getString(R.string.grid),
          false,
          grid_index,
          true,
          "GRID",
          new ArrayOptionsPopupListener() {
            private void update() {
              if (grid_index == -1) return;
              String new_grid_value = grid_values[grid_index];
              SharedPreferences sharedPreferences =
                  PreferenceManager.getDefaultSharedPreferences(main_activity);
              SharedPreferences.Editor editor = sharedPreferences.edit();
              editor.putString(PreferenceKeys.getShowGridPreferenceKey(), new_grid_value);
              editor.apply();
            }

            @Override
            public int onClickPrev() {
              if (grid_index != -1) {
                grid_index--;
                if (grid_index < 0) grid_index += grid_values.length;
                update();
                return grid_index;
              }
              return -1;
            }

            @Override
            public int onClickNext() {
              if (grid_index != -1) {
                grid_index++;
                if (grid_index >= grid_values.length) grid_index -= grid_values.length;
                update();
                return grid_index;
              }
              return -1;
            }
          });

      // popup should only be opened if we have a camera controller, but check just to be safe
      if (preview.getCameraController() != null) {
        List<String> supported_white_balances = preview.getSupportedWhiteBalances();
        addRadioOptionsToPopup(
            supported_white_balances,
            getResources().getString(R.string.white_balance),
            PreferenceKeys.getWhiteBalancePreferenceKey(),
            preview.getCameraController().getDefaultWhiteBalance(),
            "TEST_WHITE_BALANCE",
            new RadioOptionsListener() {
              @Override
              public void onClick(String selected_option) {
                if (selected_option.equals("manual")) {
                  // if we used the generic "manual", then instead try to preserve the current iso
                  // if it exists
                  if (preview.getCameraController() != null) {
                    String current_white_balance = preview.getCameraController().getWhiteBalance();
                    if (current_white_balance == null || !current_white_balance.equals("manual")) {
                      // try to choose a default manual white balance temperature as close as
                      // possible to the current auto
                      if (MyDebug.LOG) Log.d(TAG, "changed to manual white balance");
                      if (preview.getCameraController().captureResultHasWhiteBalanceTemperature()) {
                        int temperature =
                            preview.getCameraController().captureResultWhiteBalanceTemperature();
                        if (MyDebug.LOG)
                          Log.d(TAG, "default to manual white balance temperature: " + temperature);
                        SharedPreferences sharedPreferences =
                            PreferenceManager.getDefaultSharedPreferences(main_activity);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putInt(
                            PreferenceKeys.getWhiteBalanceTemperaturePreferenceKey(), temperature);
                        editor.apply();
                      }
                      // otherwise default to the saved value
                    }
                  }
                }
              }
            });

        List<String> supported_scene_modes = preview.getSupportedSceneModes();
        addRadioOptionsToPopup(
            supported_scene_modes,
            getResources().getString(R.string.scene_mode),
            PreferenceKeys.getSceneModePreferenceKey(),
            preview.getCameraController().getDefaultSceneMode(),
            "TEST_SCENE_MODE",
            null);
      }
    }
  }

  private abstract class ButtonOptionsPopupListener {
    public abstract void onClick(String option);
  }

  private void addButtonOptionsToPopup(
      List<String> supported_options,
      int icons_id,
      int values_id,
      String prefix_string,
      String current_value,
      String test_key,
      final ButtonOptionsPopupListener listener) {
    if (MyDebug.LOG) Log.d(TAG, "addButtonOptionsToPopup");
    if (supported_options != null) {
      final long debug_time = System.currentTimeMillis();
      LinearLayout ll2 = new LinearLayout(this.getContext());
      ll2.setOrientation(LinearLayout.HORIZONTAL);
      if (MyDebug.LOG)
        Log.d(TAG, "addButtonOptionsToPopup time 1: " + (System.currentTimeMillis() - debug_time));
      String[] icons = icons_id != -1 ? getResources().getStringArray(icons_id) : null;
      String[] values = values_id != -1 ? getResources().getStringArray(values_id) : null;
      if (MyDebug.LOG)
        Log.d(TAG, "addButtonOptionsToPopup time 2: " + (System.currentTimeMillis() - debug_time));

      final float scale = getResources().getDisplayMetrics().density;
      int total_width_dp = 280;
      {
        Activity activity = (Activity) this.getContext();
        Display display = activity.getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);

        // the height should limit the width, due to when held in portrait
        int dpHeight = (int) (outMetrics.heightPixels / scale);
        if (MyDebug.LOG) Log.d(TAG, "dpHeight: " + dpHeight);
        dpHeight -= 50; // allow space for the icons at top/right of screen
        if (total_width_dp > dpHeight) total_width_dp = dpHeight;
      }
      if (MyDebug.LOG) Log.d(TAG, "total_width_dp: " + total_width_dp);
      final int total_width = (int) (total_width_dp * scale + 0.5f); // convert dps to pixels;
      int button_width_dp = total_width_dp / supported_options.size();
      boolean use_scrollview = false;
      if (button_width_dp < 40) {
        button_width_dp = 40;
        use_scrollview = true;
      }
      final int button_width = (int) (button_width_dp * scale + 0.5f); // convert dps to pixels
      View current_view = null;

      for (final String supported_option : supported_options) {
        if (MyDebug.LOG) Log.d(TAG, "supported_option: " + supported_option);
        int resource = -1;
        if (icons != null && values != null) {
          int index = -1;
          for (int i = 0; i < values.length && index == -1; i++) {
            if (values[i].equals(supported_option)) index = i;
          }
          if (MyDebug.LOG) Log.d(TAG, "index: " + index);
          if (index != -1) {
            resource =
                getResources()
                    .getIdentifier(
                        icons[index],
                        null,
                        this.getContext().getApplicationContext().getPackageName());
          }
        }
        if (MyDebug.LOG)
          Log.d(
              TAG,
              "addButtonOptionsToPopup time 2.1: " + (System.currentTimeMillis() - debug_time));

        String button_string;
        // hacks for ISO mode ISO_HJR (e.g., on Samsung S5)
        // also some devices report e.g. "ISO100" etc
        if (prefix_string.length() == 0) {
          button_string = supported_option;
        } else if (prefix_string.equalsIgnoreCase("ISO")
            && supported_option.length() >= 4
            && supported_option.substring(0, 4).equalsIgnoreCase("ISO_")) {
          button_string = prefix_string + "\n" + supported_option.substring(4);
        } else if (prefix_string.equalsIgnoreCase("ISO")
            && supported_option.length() >= 3
            && supported_option.substring(0, 3).equalsIgnoreCase("ISO")) {
          button_string = prefix_string + "\n" + supported_option.substring(3);
        } else {
          button_string = prefix_string + "\n" + supported_option;
        }
        if (MyDebug.LOG) Log.d(TAG, "button_string: " + button_string);
        View view;
        if (resource != -1) {
          ImageButton image_button = new ImageButton(this.getContext());
          if (MyDebug.LOG)
            Log.d(
                TAG,
                "addButtonOptionsToPopup time 2.11: " + (System.currentTimeMillis() - debug_time));
          view = image_button;
          ll2.addView(view);
          if (MyDebug.LOG)
            Log.d(
                TAG,
                "addButtonOptionsToPopup time 2.12: " + (System.currentTimeMillis() - debug_time));

          // image_button.setImageResource(resource);
          final CameraActivity main_activity = (CameraActivity) this.getContext();
          Bitmap bm = main_activity.getPreloadedBitmap(resource);
          if (bm != null) image_button.setImageBitmap(bm);
          else {
            if (MyDebug.LOG) Log.d(TAG, "failed to find bitmap for resource " + resource + "!");
          }
          if (MyDebug.LOG)
            Log.d(
                TAG,
                "addButtonOptionsToPopup time 2.13: " + (System.currentTimeMillis() - debug_time));
          image_button.setScaleType(ScaleType.FIT_CENTER);
          final int padding = (int) (10 * scale + 0.5f); // convert dps to pixels
          view.setPadding(padding, padding, padding, padding);
        } else {
          Button button = new Button(this.getContext());
          button.setBackgroundColor(Color.TRANSPARENT); // workaround for Android 6 crash!
          view = button;
          ll2.addView(view);

          button.setText(button_string);
          button.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12.0f);
          button.setTextColor(Color.WHITE);
          // need 0 padding so we have enough room to display text for ISO buttons, when there are 6
          // ISO settings
          final int padding = (int) (0 * scale + 0.5f); // convert dps to pixels
          view.setPadding(padding, padding, padding, padding);
        }
        if (MyDebug.LOG)
          Log.d(
              TAG,
              "addButtonOptionsToPopup time 2.2: " + (System.currentTimeMillis() - debug_time));

        ViewGroup.LayoutParams params = view.getLayoutParams();
        params.width = button_width;
        params.height = (int) (50 * scale + 0.5f); // convert dps to pixels
        view.setLayoutParams(params);

        view.setContentDescription(button_string);
        if (supported_option.equals(current_value)) {
          view.setAlpha(ALPHA_BUTTON_SELECTED);
          current_view = view;
        } else {
          view.setAlpha(ALPHA_BUTTON);
        }
        if (MyDebug.LOG)
          Log.d(
              TAG,
              "addButtonOptionsToPopup time 2.3: " + (System.currentTimeMillis() - debug_time));
        view.setOnClickListener(
            new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                if (MyDebug.LOG) Log.d(TAG, "clicked: " + supported_option);
                listener.onClick(supported_option);
              }
            });
        this.popup_buttons.put(test_key + "_" + supported_option, view);
        if (MyDebug.LOG) {
          Log.d(
              TAG,
              "addButtonOptionsToPopup time 2.4: " + (System.currentTimeMillis() - debug_time));
          Log.d(
              TAG,
              "added to popup_buttons: " + test_key + "_" + supported_option + " view: " + view);
          Log.d(TAG, "popup_buttons is now: " + popup_buttons);
        }
      }
      if (MyDebug.LOG)
        Log.d(TAG, "addButtonOptionsToPopup time 3: " + (System.currentTimeMillis() - debug_time));
      if (use_scrollview) {
        if (MyDebug.LOG) Log.d(TAG, "using scrollview");
        final HorizontalScrollView scroll = new HorizontalScrollView(this.getContext());
        scroll.addView(ll2);
        {
          ViewGroup.LayoutParams params = new LayoutParams(total_width, LayoutParams.WRAP_CONTENT);
          scroll.setLayoutParams(params);
        }
        this.addView(scroll);
        if (current_view != null) {
          // scroll to the selected button
          final View final_current_view = current_view;
          this.getViewTreeObserver()
              .addOnGlobalLayoutListener(
                  new OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                      // scroll so selected button is centred
                      int jump_x = final_current_view.getLeft() - (total_width - button_width) / 2;
                      // scrollTo should automatically clamp to the bounds of the view, but just in
                      // case
                      jump_x = Math.min(jump_x, total_width - 1);
                      if (jump_x > 0) {
                        /*if( MyDebug.LOG )
                        Log.d(TAG, "jump to " + jump_X);*/
                        scroll.scrollTo(jump_x, 0);
                      }
                    }
                  });
        }
      } else {
        if (MyDebug.LOG) Log.d(TAG, "not using scrollview");
        this.addView(ll2);
      }
      if (MyDebug.LOG)
        Log.d(TAG, "addButtonOptionsToPopup time 4: " + (System.currentTimeMillis() - debug_time));
    }
  }

  private void addTitleToPopup(final String title) {
    TextView text_view = new TextView(this.getContext());
    text_view.setText(title + ":");
    text_view.setTextColor(Color.WHITE);
    text_view.setGravity(Gravity.CENTER);
    text_view.setTypeface(null, Typeface.BOLD);
    // text_view.setBackgroundColor(Color.GRAY); // debug
    this.addView(text_view);
  }

  private abstract class RadioOptionsListener {
    /**
     * Called when a radio option is selected.
     *
     * @param selected_option The entry in the supplied supported_options list (received by
     *     addRadioOptionsToPopup) that corresponds to the selected radio option.
     */
    public abstract void onClick(String selected_option);
  }

  private void addRadioOptionsToPopup(
      List<String> supported_options,
      final String title,
      final String preference_key,
      final String default_option,
      final String test_key,
      final RadioOptionsListener listener) {
    if (MyDebug.LOG) Log.d(TAG, "addOptionsToPopup: " + title);
    if (supported_options != null) {
      final CameraActivity main_activity = (CameraActivity) this.getContext();

      addTitleToPopup(title);

      RadioGroup rg = new RadioGroup(this.getContext());
      rg.setOrientation(RadioGroup.VERTICAL);
      this.popup_buttons.put(test_key, rg);

      SharedPreferences sharedPreferences =
          PreferenceManager.getDefaultSharedPreferences(main_activity);
      String current_option = sharedPreferences.getString(preference_key, default_option);
      for (final String supported_option : supported_options) {
        if (MyDebug.LOG) Log.d(TAG, "supported_option: " + supported_option);
        // Button button = new Button(this);
        RadioButton button = new RadioButton(this.getContext());
        button.setText(supported_option);
        button.setTextColor(Color.WHITE);
        if (supported_option.equals(current_option)) {
          button.setChecked(true);
        } else {
          button.setChecked(false);
        }
        // ll.addView(button);
        rg.addView(button);
        button.setContentDescription(supported_option);
        button.setOnClickListener(
            new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                if (MyDebug.LOG) Log.d(TAG, "clicked current_option: " + supported_option);
                if (listener != null) {
                  listener.onClick(supported_option);
                }
                SharedPreferences sharedPreferences =
                    PreferenceManager.getDefaultSharedPreferences(main_activity);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(preference_key, supported_option);
                editor.apply();

                main_activity.updateForSettings(title + ": " + supported_option);
                main_activity.closePopup();
              }
            });
        this.popup_buttons.put(test_key + "_" + supported_option, button);
      }
      this.addView(rg);
    }
  }

  private abstract class ArrayOptionsPopupListener {
    public abstract int onClickPrev();

    public abstract int onClickNext();
  }

  private void addArrayOptionsToPopup(
      final List<String> supported_options,
      final String title,
      final boolean title_in_options,
      final int current_index,
      final boolean cyclic,
      final String test_key,
      final ArrayOptionsPopupListener listener) {
    if (supported_options != null && current_index != -1) {
      if (!title_in_options) {
        addTitleToPopup(title);
      }

      /*final Button prev_button = new Button(this.getContext());
      //prev_button.setBackgroundResource(R.drawable.exposure);
      prev_button.setBackgroundColor(Color.TRANSPARENT); // workaround for Android 6 crash!
      prev_button.setText("<");
      this.addView(prev_button);*/

      LinearLayout ll2 = new LinearLayout(this.getContext());
      ll2.setOrientation(LinearLayout.HORIZONTAL);

      final TextView resolution_text_view = new TextView(this.getContext());
      if (title_in_options)
        resolution_text_view.setText(title + ": " + supported_options.get(current_index));
      else resolution_text_view.setText(supported_options.get(current_index));
      resolution_text_view.setTextColor(Color.WHITE);
      resolution_text_view.setGravity(Gravity.CENTER);
      LinearLayout.LayoutParams params =
          new LinearLayout.LayoutParams(
              LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
      resolution_text_view.setLayoutParams(params);

      final float scale = getResources().getDisplayMetrics().density;
      final int padding = (int) (0 * scale + 0.5f); // convert dps to pixels
      final int button_w = (int) (60 * scale + 0.5f); // convert dps to pixels
      final int button_h = (int) (30 * scale + 0.5f); // convert dps to pixels
      final Button prev_button = new Button(this.getContext());
      prev_button.setBackgroundColor(Color.TRANSPARENT); // workaround for Android 6 crash!
      ll2.addView(prev_button);
      prev_button.setText("<");
      prev_button.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12.0f);
      prev_button.setPadding(padding, padding, padding, padding);
      ViewGroup.LayoutParams vg_params = prev_button.getLayoutParams();
      vg_params.width = button_w;
      vg_params.height = button_h;
      prev_button.setLayoutParams(vg_params);
      prev_button.setVisibility((cyclic || current_index > 0) ? View.VISIBLE : View.INVISIBLE);
      this.popup_buttons.put(test_key + "_PREV", prev_button);

      ll2.addView(resolution_text_view);
      this.popup_buttons.put(test_key, resolution_text_view);

      final Button next_button = new Button(this.getContext());
      next_button.setBackgroundColor(Color.TRANSPARENT); // workaround for Android 6 crash!
      ll2.addView(next_button);
      next_button.setText(">");
      next_button.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12.0f);
      next_button.setPadding(padding, padding, padding, padding);
      vg_params = next_button.getLayoutParams();
      vg_params.width = button_w;
      vg_params.height = button_h;
      next_button.setLayoutParams(vg_params);
      next_button.setVisibility(
          (cyclic || current_index < supported_options.size() - 1) ? View.VISIBLE : View.INVISIBLE);
      this.popup_buttons.put(test_key + "_NEXT", next_button);

      prev_button.setOnClickListener(
          new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              int new_index = listener.onClickPrev();
              if (new_index != -1) {
                if (title_in_options)
                  resolution_text_view.setText(title + ": " + supported_options.get(new_index));
                else resolution_text_view.setText(supported_options.get(new_index));
                prev_button.setVisibility(
                    (cyclic || new_index > 0) ? View.VISIBLE : View.INVISIBLE);
                next_button.setVisibility(
                    (cyclic || new_index < supported_options.size() - 1)
                        ? View.VISIBLE
                        : View.INVISIBLE);
              }
            }
          });
      next_button.setOnClickListener(
          new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              int new_index = listener.onClickNext();
              if (new_index != -1) {
                if (title_in_options)
                  resolution_text_view.setText(title + ": " + supported_options.get(new_index));
                else resolution_text_view.setText(supported_options.get(new_index));
                prev_button.setVisibility(
                    (cyclic || new_index > 0) ? View.VISIBLE : View.INVISIBLE);
                next_button.setVisibility(
                    (cyclic || new_index < supported_options.size() - 1)
                        ? View.VISIBLE
                        : View.INVISIBLE);
              }
            }
          });

      this.addView(ll2);
    }
  }

  private void showInfoDialog(int title_id, int info_id, final String info_preference_key) {
    final CameraActivity main_activity = (CameraActivity) this.getContext();
    AlertDialog.Builder alertDialog = new AlertDialog.Builder(PopupView.this.getContext());
    alertDialog.setTitle(title_id);
    alertDialog.setMessage(info_id);
    alertDialog.setPositiveButton(android.R.string.ok, null);
    alertDialog.setNegativeButton(
        R.string.dont_show_again,
        new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            if (MyDebug.LOG) Log.d(TAG, "user clicked dont_show_again for info dialog");
            final SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(main_activity);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(info_preference_key, true);
            editor.apply();
          }
        });

    main_activity.showPreview(false);
    main_activity.setWindowFlagsForSettings();

    AlertDialog alert = alertDialog.create();
    // AlertDialog.Builder.setOnDismissListener() requires API level 17, so do it this way instead
    alert.setOnDismissListener(
        new DialogInterface.OnDismissListener() {
          @Override
          public void onDismiss(DialogInterface arg0) {
            if (MyDebug.LOG) Log.d(TAG, "info dialog dismissed");
            main_activity.setWindowFlagsForCamera();
            main_activity.showPreview(true);
          }
        });
    alert.show();
  }

  // for testing
  public View getPopupButton(String key) {
    if (MyDebug.LOG) {
      Log.d(TAG, "getPopupButton(" + key + "): " + popup_buttons.get(key));
      Log.d(TAG, "this: " + this);
      Log.d(TAG, "popup_buttons: " + popup_buttons);
    }
    return popup_buttons.get(key);
  }
}
