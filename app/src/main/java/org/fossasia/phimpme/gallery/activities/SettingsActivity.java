package org.fossasia.phimpme.gallery.activities;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

import android.annotation.TargetApi;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.mikepenz.community_material_typeface_library.CommunityMaterial;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.iconics.view.IconicsImageView;
import org.fossasia.phimpme.R;
import org.fossasia.phimpme.base.ThemedActivity;
import org.fossasia.phimpme.gallery.util.AlertDialogsHelper;
import org.fossasia.phimpme.gallery.util.ColorPalette;
import org.fossasia.phimpme.gallery.util.PreferenceUtil;
import org.fossasia.phimpme.gallery.util.SecurityHelper;
import org.fossasia.phimpme.gallery.util.StaticMapProvider;
import org.fossasia.phimpme.gallery.util.ThemeHelper;
import org.fossasia.phimpme.opencamera.Camera.CameraActivity;
import org.fossasia.phimpme.opencamera.Camera.MyPreferenceFragment;
import org.fossasia.phimpme.opencamera.Camera.PreferenceKeys;
import org.fossasia.phimpme.opencamera.Camera.TinyDB;
import org.fossasia.phimpme.utilities.ActivitySwitchHelper;
import org.fossasia.phimpme.utilities.SnackBarHandler;
import uz.shift.colorpicker.LineColorPicker;
import uz.shift.colorpicker.OnColorChangedListener;

/** Created by Jibo on 02/03/2016. */
@SuppressWarnings("ResourceAsColor")
public class SettingsActivity extends ThemedActivity {

  private PreferenceUtil SP;
  private SecurityHelper securityObj;

  @BindView(R.id.toolbar)
  Toolbar toolbar;

  @BindView(R.id.settingAct_scrollView)
  ScrollView scr;

  @BindView(R.id.general_setting_title)
  TextView txtGT;

  @BindView(R.id.theme_setting_title)
  TextView txtTT;

  @BindView(R.id.picture_setting_title)
  TextView txtPT;

  @BindView(R.id.advanced_setting_title)
  TextView txtAT;

  @BindView(R.id.setting_background)
  View parent;

  private SwitchCompat swNavBar;
  private SwitchCompat swStatusBar;
  private SwitchCompat swMaxLuminosity;
  private SwitchCompat swPictureOrientation;
  private SwitchCompat swDelayFullImage;
  private SwitchCompat swAutoUpdate;
  private SwitchCompat swSwipeDirection;

  private boolean saf_dialog_from_preferences;

  @Override
  protected void onPostCreate(Bundle savedInstanceState) {
    super.onPostCreate(savedInstanceState);
    setContentView(R.layout.activity_settings);
    ButterKnife.bind(this);
    toolbar = findViewById(R.id.toolbar);
    SP = PreferenceUtil.getInstance(getApplicationContext());

    securityObj = new SecurityHelper(SettingsActivity.this);

    scr = findViewById(R.id.settingAct_scrollView);

    /** * BASIC THEME ** */
    findViewById(R.id.ll_basic_theme)
        .setOnClickListener(
            new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                baseThemeDialog();
              }
            });

    /** * SECURITY ** */
    findViewById(R.id.ll_security)
        .setOnClickListener(
            new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                if (!securityObj.isActiveSecurity())
                  startActivity(new Intent(getApplicationContext(), SecurityActivity.class));
                else askPasswordDialog();
              }
            });
    /** * CAMERA ** */
    findViewById(R.id.ll_camera)
        .setOnClickListener(
            new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                openCameraSetting(SettingsActivity.this);
              }
            });

    /** * PRIMARY COLOR PIKER ** */
    findViewById(R.id.ll_primaryColor)
        .setOnClickListener(
            new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                primaryColorPiker();
              }
            });

    /** * ACCENT COLOR PIKER ** */
    findViewById(R.id.ll_accentColor)
        .setOnClickListener(
            new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                accentColorPiker();
              }
            });

    /** * EXCLUDED ALBUMS INTENT ** */
    findViewById(R.id.ll_excluded_album)
        .setOnClickListener(
            new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                startActivity(new Intent(SettingsActivity.this, ExcludedAlbumsActivity.class));
              }
            });

    /** * CUSTOMIZE PICTURE VIEWER DIALOG ** */
    findViewById(R.id.ll_custom_thirdAct)
        .setOnClickListener(
            new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                customizePictureViewer();
              }
            });

    /** * MAP PROVIDER DIALOG ** */
    findViewById(R.id.ll_map_provider)
        .setOnClickListener(
            new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                mapProviderDialog();
              }
            });

    /** * MULTI COLUMN DIALOG ** */
    findViewById(R.id.ll_n_columns)
        .setOnClickListener(
            new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                multiColumnsDialog();
              }
            });

    /** * RESET SETTINGS ** */
    findViewById(R.id.ll_reset_settings)
        .setOnClickListener(
            new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                resetSettingsDialog();
              }
            });

    /** * SW SWIPE DIRECTION ** */
    swSwipeDirection = findViewById(R.id.Set_media_viewer_swipe_direction);
    swSwipeDirection.setChecked(
        SP.getBoolean(getString(R.string.preference_swipe_direction_inverted), false));
    swSwipeDirection.setOnCheckedChangeListener(
        new CompoundButton.OnCheckedChangeListener() {
          @Override
          public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            SP.putBoolean(getString(R.string.preference_swipe_direction_inverted), isChecked);
            updateSwitchColor(swSwipeDirection, getAccentColor());
          }
        });

    /** * SW AUTO UPDATE MEDIA ** */
    swAutoUpdate = findViewById(R.id.SetAutoUpdateMedia);
    swAutoUpdate.setChecked(SP.getBoolean(getString(R.string.preference_auto_update_media), false));
    swAutoUpdate.setOnCheckedChangeListener(
        new CompoundButton.OnCheckedChangeListener() {
          @Override
          public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            SP.putBoolean(getString(R.string.preference_auto_update_media), isChecked);
            updateSwitchColor(swAutoUpdate, getAccentColor());
          }
        });

    /** * SW DELAY FULL-SIZE IMAGE ** */
    swDelayFullImage = findViewById(R.id.set_full_resolution);
    swDelayFullImage.setChecked(
        SP.getBoolean(getString(R.string.preference_delay_full_image), true));
    swDelayFullImage.setOnCheckedChangeListener(
        new CompoundButton.OnCheckedChangeListener() {
          @Override
          public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            SP.putBoolean(getString(R.string.preference_delay_full_image), isChecked);
            updateSwitchColor(swDelayFullImage, getAccentColor());
          }
        });

    /** * SW PICTURE ORIENTATION ** */
    swPictureOrientation = findViewById(R.id.set_picture_orientation);
    swPictureOrientation.setChecked(
        SP.getBoolean(getString(R.string.preference_auto_rotate), false));
    swPictureOrientation.setOnCheckedChangeListener(
        new CompoundButton.OnCheckedChangeListener() {
          @Override
          public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            SP.putBoolean(getString(R.string.preference_auto_rotate), isChecked);
            updateSwitchColor(swPictureOrientation, getAccentColor());
          }
        });

    /** * SW MAX LUMINOSITY ** */
    swMaxLuminosity = findViewById(R.id.set_max_luminosity);
    swMaxLuminosity.setChecked(SP.getBoolean(getString(R.string.preference_max_brightness), false));
    swMaxLuminosity.setOnCheckedChangeListener(
        new CompoundButton.OnCheckedChangeListener() {
          @Override
          public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            SP.putBoolean(getString(R.string.preference_max_brightness), isChecked);
            updateSwitchColor(swMaxLuminosity, getAccentColor());
          }
        });

    /** * SW TRANSLUCENT STATUS BAR ** */
    swStatusBar = findViewById(R.id.SetTraslucentStatusBar);
    swStatusBar.setChecked(
        SP.getBoolean(getString(R.string.preference_translucent_status_bar), true));
    swStatusBar.setOnCheckedChangeListener(
        new CompoundButton.OnCheckedChangeListener() {
          @Override
          public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            SP.putBoolean(getString(R.string.preference_translucent_status_bar), isChecked);
            updateTheme();
            setStatusBarColor();
            Snackbar.make(
                    findViewById(android.R.id.content),
                    getString(R.string.restart_app),
                    Snackbar.LENGTH_SHORT)
                .show();
            updateSwitchColor(swStatusBar, getAccentColor());
          }
        });

    /** * SW COLORED NAV BAR ** */
    swNavBar = findViewById(R.id.SetColoredNavBar);
    swNavBar.setChecked(SP.getBoolean(getString(R.string.preference_colored_nav_bar), true));
    swNavBar.setOnCheckedChangeListener(
        new CompoundButton.OnCheckedChangeListener() {
          @Override
          public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            SP.putBoolean(getString(R.string.preference_colored_nav_bar), isChecked);
            updateTheme();
            updateSwitchColor(swNavBar, getAccentColor());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
              getWindow()
                  .setNavigationBarColor(
                      isNavigationBarColored()
                          ? getPrimaryColor()
                          : ContextCompat.getColor(getApplicationContext(), R.color.md_black_1000));
          }
        });
  }

  private void multiColumnsDialog() {
    AlertDialog.Builder multiColumnDialogBuilder =
        new AlertDialog.Builder(SettingsActivity.this, getDialogStyle());
    View dialogLayout = getLayoutInflater().inflate(R.layout.dialog_multi_column, null);

    ((TextView) dialogLayout.findViewById(R.id.text_view_portrait)).setTextColor(getTextColor());
    ((TextView) dialogLayout.findViewById(R.id.text_view_landscape)).setTextColor(getTextColor());
    ((TextView) dialogLayout.findViewById(R.id.folders_title)).setTextColor(getTextColor());
    ((TextView) dialogLayout.findViewById(R.id.media_title)).setTextColor(getTextColor());
    ((TextView) dialogLayout.findViewById(R.id.folders_title_landscape))
        .setTextColor(getTextColor());
    ((TextView) dialogLayout.findViewById(R.id.media_title_landscape)).setTextColor(getTextColor());
    ((CardView) dialogLayout.findViewById(R.id.multi_column_card))
        .setCardBackgroundColor(getCardBackgroundColor());

    dialogLayout.findViewById(R.id.multi_column_title).setBackgroundColor(getPrimaryColor());
    final TextView nColFolders = dialogLayout.findViewById(R.id.n_columns_folders);
    final TextView nColMedia = dialogLayout.findViewById(R.id.n_columns_media);
    final TextView nColFoldersL = dialogLayout.findViewById(R.id.n_columns_folders_landscape);
    final TextView nColMediaL = dialogLayout.findViewById(R.id.n_columns_media_landscape);

    nColFolders.setTextColor(getSubTextColor());
    nColMedia.setTextColor(getSubTextColor());
    nColFoldersL.setTextColor(getSubTextColor());
    nColMediaL.setTextColor(getSubTextColor());

    SeekBar barFolders = dialogLayout.findViewById(R.id.seek_bar_n_columns_folders);
    SeekBar barMedia = dialogLayout.findViewById(R.id.seek_bar_n_columns_media);
    SeekBar barFoldersL = dialogLayout.findViewById(R.id.seek_bar_n_columns_folders_landscape);
    SeekBar barMediaL = dialogLayout.findViewById(R.id.seek_bar_n_columns_media_landscape);

    themeSeekBar(barFolders);
    themeSeekBar(barMedia);
    themeSeekBar(barFoldersL);
    themeSeekBar(barMediaL);

    nColFolders.setText(String.valueOf(SP.getInt("n_columns_folders", 2)));
    nColMedia.setText(String.valueOf(SP.getInt("n_columns_media", 3)));
    barFolders.setProgress(SP.getInt("n_columns_folders", 2) - 1);
    barMedia.setProgress(SP.getInt("n_columns_media", 3) - 1);
    barFolders.setOnSeekBarChangeListener(
        new SeekBar.OnSeekBarChangeListener() {
          @Override
          public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            nColFolders.setText(String.valueOf(i + 1));
          }

          @Override
          public void onStartTrackingTouch(SeekBar seekBar) {}

          @Override
          public void onStopTrackingTouch(SeekBar seekBar) {}
        });

    barMedia.setOnSeekBarChangeListener(
        new SeekBar.OnSeekBarChangeListener() {
          @Override
          public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            nColMedia.setText(String.valueOf(i + 1));
          }

          @Override
          public void onStartTrackingTouch(SeekBar seekBar) {}

          @Override
          public void onStopTrackingTouch(SeekBar seekBar) {}
        });

    /// LANDSCAPE
    nColFoldersL.setText(String.valueOf(SP.getInt("n_columns_folders_landscape", 3)));
    nColMediaL.setText(String.valueOf(SP.getInt("n_columns_media_landscape", 4)));
    barFoldersL.setProgress(SP.getInt("n_columns_folders_landscape", 3) - 2);
    barMediaL.setProgress(SP.getInt("n_columns_media_landscape", 4) - 3);
    barFoldersL.setOnSeekBarChangeListener(
        new SeekBar.OnSeekBarChangeListener() {
          @Override
          public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            nColFoldersL.setText(String.valueOf(i + 2));
          }

          @Override
          public void onStartTrackingTouch(SeekBar seekBar) {}

          @Override
          public void onStopTrackingTouch(SeekBar seekBar) {}
        });

    barMediaL.setOnSeekBarChangeListener(
        new SeekBar.OnSeekBarChangeListener() {
          @Override
          public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            nColMediaL.setText(String.valueOf(i + 3));
          }

          @Override
          public void onStartTrackingTouch(SeekBar seekBar) {}

          @Override
          public void onStopTrackingTouch(SeekBar seekBar) {}
        });

    multiColumnDialogBuilder.setPositiveButton(
        getString(R.string.ok_action).toUpperCase(),
        new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialogInterface, int i) {
            int nFolders = Integer.parseInt(nColFolders.getText().toString());
            int nMedia = Integer.parseInt(nColMedia.getText().toString());
            int nFoldersL = Integer.parseInt(nColFoldersL.getText().toString());
            int nMediaL = Integer.parseInt(nColMediaL.getText().toString());

            SP.putInt("n_columns_folders", nFolders);
            SP.putInt("n_columns_media", nMedia);
            SP.putInt("n_columns_folders_landscape", nFoldersL);
            SP.putInt("n_columns_media_landscape", nMediaL);
          }
        });
    multiColumnDialogBuilder.setNegativeButton(getString(R.string.cancel).toUpperCase(), null);
    multiColumnDialogBuilder.setView(dialogLayout);
    AlertDialog alertDialog = multiColumnDialogBuilder.create();
    alertDialog.show();
    AlertDialogsHelper.setButtonTextColor(
        new int[] {DialogInterface.BUTTON_POSITIVE, DialogInterface.BUTTON_NEGATIVE},
        getAccentColor(),
        alertDialog);
  }

  private void askPasswordDialog() {
    final short max_password_length = 128;
    final boolean[] passco = {false};
    AlertDialog.Builder passwordDialogBuilder =
        new AlertDialog.Builder(SettingsActivity.this, getDialogStyle());
    final EditText editTextPassword =
        securityObj.getInsertPasswordDialog(SettingsActivity.this, passwordDialogBuilder);
    passwordDialogBuilder.setNegativeButton(getString(R.string.cancel).toUpperCase(), null);
    editTextPassword.setInputType(
        InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
    editTextPassword.setHint(getResources().getString(R.string.enter_password));
    editTextPassword.setHintTextColor(getSubTextColor());
    editTextPassword.addTextChangedListener(
        new TextWatcher() {
          @Override
          public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            // empty method
          }

          @Override
          public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            editTextPassword.setSelection(editTextPassword.getText().toString().length());
          }

          @Override
          public void afterTextChanged(Editable editable) {
            if (securityObj.getTextInputLayout().getVisibility() == View.VISIBLE && !passco[0]) {
              securityObj.getTextInputLayout().setVisibility(View.INVISIBLE);
            } else {
              passco[0] = false;
            }
            if (editable.length() == max_password_length) {
              editTextPassword.setText(editable.toString().substring(0, max_password_length - 1));
              editTextPassword.setSelection(max_password_length - 1);
              Toast.makeText(
                      getApplicationContext(),
                      getResources().getString(R.string.max_password_length),
                      Toast.LENGTH_SHORT)
                  .show();
            }
          }
        });
    passwordDialogBuilder.setPositiveButton(
        getString(R.string.ok_action).toUpperCase(),
        new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            // This should br empty it will be overwrite later
            // to avoid dismiss of the dialog on wrong password
          }
        });

    final AlertDialog passwordDialog = passwordDialogBuilder.create();
    passwordDialog
        .getWindow()
        .setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    passwordDialog.show();
    AlertDialogsHelper.setButtonTextColor(
        new int[] {DialogInterface.BUTTON_POSITIVE, DialogInterface.BUTTON_NEGATIVE},
        getAccentColor(),
        passwordDialog);
    passwordDialog
        .getButton(AlertDialog.BUTTON_POSITIVE)
        .setOnClickListener(
            new View.OnClickListener() {

              @Override
              public void onClick(View v) {

                if (securityObj.checkPassword(editTextPassword.getText().toString())) {
                  passwordDialog.dismiss();
                  startActivity(new Intent(getApplicationContext(), SecurityActivity.class));
                } else {
                  passco[0] = true;
                  securityObj.getTextInputLayout().setVisibility(View.VISIBLE);
                  SnackBarHandler.show(parent, R.string.wrong_password);
                  editTextPassword.getText().clear();
                  editTextPassword.requestFocus();
                }
              }
            });
  }

  private void mapProviderDialog() {

    final AlertDialog.Builder dialogBuilder =
        new AlertDialog.Builder(SettingsActivity.this, getDialogStyle());
    View dialogLayout = getLayoutInflater().inflate(R.layout.dialog_map_provider, null);
    TextView dialogTitle = dialogLayout.findViewById(R.id.title);
    ((CardView) dialogLayout.findViewById(R.id.dialog_chose_provider_title))
        .setCardBackgroundColor(getCardBackgroundColor());
    dialogTitle.setBackgroundColor(getPrimaryColor());

    final RadioGroup mapProvider = dialogLayout.findViewById(R.id.radio_group_maps_provider);
    RadioButton radioGoogleMaps = dialogLayout.findViewById(R.id.radio_google_maps);
    RadioButton radioMapBoxStreets = dialogLayout.findViewById(R.id.radio_mapb_streets);
    RadioButton radioMapBoxDark = dialogLayout.findViewById(R.id.radio_mapb_dark);
    RadioButton radioMapBoxLight = dialogLayout.findViewById(R.id.radio_mapb_light);
    RadioButton radioTyler = dialogLayout.findViewById(R.id.radio_osm_tyler);
    setRadioTextButtonColor(radioGoogleMaps, getSubTextColor());
    setRadioTextButtonColor(radioMapBoxStreets, getSubTextColor());
    setRadioTextButtonColor(radioMapBoxDark, getSubTextColor());
    setRadioTextButtonColor(radioMapBoxLight, getSubTextColor());
    setRadioTextButtonColor(radioTyler, getSubTextColor());

    ((TextView) dialogLayout.findViewById(R.id.header_proprietary_maps))
        .setTextColor(getTextColor());
    ((TextView) dialogLayout.findViewById(R.id.header_free_maps)).setTextColor(getTextColor());
    switch (StaticMapProvider.fromValue(
        SP.getInt(
            getString(R.string.preference_map_provider),
            StaticMapProvider.GOOGLE_MAPS.getValue()))) {
      case GOOGLE_MAPS:
      default:
        radioGoogleMaps.setChecked(true);
        break;
      case MAP_BOX:
        radioMapBoxStreets.setChecked(true);
        break;
      case MAP_BOX_DARK:
        radioMapBoxDark.setChecked(true);
        break;
      case MAP_BOX_LIGHT:
        radioMapBoxLight.setChecked(true);
        break;
      case TYLER:
        radioTyler.setChecked(true);
        break;
    }

    dialogBuilder.setNegativeButton(getString(R.string.cancel).toUpperCase(), null);
    dialogBuilder.setPositiveButton(
        getString(R.string.ok_action).toUpperCase(),
        new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            switch (mapProvider.getCheckedRadioButtonId()) {
              case R.id.radio_google_maps:
              default:
                SP.putInt(
                    getString(R.string.preference_map_provider),
                    StaticMapProvider.GOOGLE_MAPS.getValue());
                break;
              case R.id.radio_mapb_streets:
                SP.putInt(
                    getString(R.string.preference_map_provider),
                    StaticMapProvider.MAP_BOX.getValue());
                break;
              case R.id.radio_osm_tyler:
                SP.putInt(
                    getString(R.string.preference_map_provider),
                    StaticMapProvider.TYLER.getValue());
                break;
              case R.id.radio_mapb_dark:
                SP.putInt(
                    getString(R.string.preference_map_provider),
                    StaticMapProvider.MAP_BOX_DARK.getValue());
                break;
              case R.id.radio_mapb_light:
                SP.putInt(
                    getString(R.string.preference_map_provider),
                    StaticMapProvider.MAP_BOX_LIGHT.getValue());
                break;
            }
          }
        });
    dialogBuilder.setView(dialogLayout);
    AlertDialog alertDialog = dialogBuilder.create();
    alertDialog.show();
    AlertDialogsHelper.setButtonTextColor(
        new int[] {DialogInterface.BUTTON_POSITIVE, DialogInterface.BUTTON_NEGATIVE},
        getAccentColor(),
        alertDialog);
  }

  private void baseThemeDialog() {
    final AlertDialog.Builder dialogBuilder =
        new AlertDialog.Builder(SettingsActivity.this, getDialogStyle());

    final View dialogLayout = getLayoutInflater().inflate(R.layout.dialog_basic_theme, null);
    final TextView dialogTitle = dialogLayout.findViewById(R.id.basic_theme_title);
    final CardView dialogCardView = dialogLayout.findViewById(R.id.basic_theme_card);

    final IconicsImageView themeIconWhite = dialogLayout.findViewById(R.id.white_basic_theme_icon);
    final IconicsImageView themeIconDark = dialogLayout.findViewById(R.id.dark_basic_theme_icon);
    final IconicsImageView themeIconDarkAmoled =
        dialogLayout.findViewById(R.id.dark_amoled_basic_theme_icon);
    final IconicsImageView whiteSelect = dialogLayout.findViewById(R.id.white_basic_theme_select);
    final IconicsImageView darkSelect = dialogLayout.findViewById(R.id.dark_basic_theme_select);
    final IconicsImageView darkAmoledSelect =
        dialogLayout.findViewById(R.id.dark_amoled_basic_theme_select);

    themeIconWhite.setIcon("gmd-invert-colors");
    themeIconDark.setIcon("gmd-invert-colors");
    themeIconDarkAmoled.setIcon("gmd-invert-colors");
    whiteSelect.setIcon("gmd-done");
    darkSelect.setIcon("gmd-done");
    darkAmoledSelect.setIcon("gmd-done");

    switch (getBaseTheme()) {
      case ThemeHelper.LIGHT_THEME:
        whiteSelect.setVisibility(View.VISIBLE);
        darkSelect.setVisibility(View.GONE);
        darkAmoledSelect.setVisibility(View.GONE);
        break;
      case ThemeHelper.DARK_THEME:
        whiteSelect.setVisibility(View.GONE);
        darkSelect.setVisibility(View.VISIBLE);
        darkAmoledSelect.setVisibility(View.GONE);
        break;
      case ThemeHelper.AMOLED_THEME:
        whiteSelect.setVisibility(View.GONE);
        darkSelect.setVisibility(View.GONE);
        darkAmoledSelect.setVisibility(View.VISIBLE);
        break;
    }

    /** SET OBJ THEME * */
    dialogTitle.setBackgroundColor(getPrimaryColor());
    dialogCardView.setCardBackgroundColor(getCardBackgroundColor());

    dialogLayout
        .findViewById(R.id.ll_white_basic_theme)
        .setOnClickListener(
            new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                whiteSelect.setVisibility(View.VISIBLE);
                darkSelect.setVisibility(View.GONE);
                darkAmoledSelect.setVisibility(View.GONE);
                setBaseTheme(ThemeHelper.LIGHT_THEME, false);
                // dialogCardView.setCardBackgroundColor(getCardBackgroundColor());
                // setTheme();

              }
            });
    dialogLayout
        .findViewById(R.id.ll_dark_basic_theme)
        .setOnClickListener(
            new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                whiteSelect.setVisibility(View.GONE);
                darkSelect.setVisibility(View.VISIBLE);
                darkAmoledSelect.setVisibility(View.GONE);
                setBaseTheme(ThemeHelper.DARK_THEME, false);
                // dialogCardView.setCardBackgroundColor(getCardBackgroundColor());
                // setTheme();
              }
            });
    dialogLayout
        .findViewById(R.id.ll_dark_amoled_basic_theme)
        .setOnClickListener(
            new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                whiteSelect.setVisibility(View.GONE);
                darkSelect.setVisibility(View.GONE);
                darkAmoledSelect.setVisibility(View.VISIBLE);
                setBaseTheme(ThemeHelper.AMOLED_THEME, false);
                // dialogCardView.setCardBackgroundColor(getCardBackgroundColor());
                // setTheme();

              }
            });
    dialogBuilder.setView(dialogLayout);
    dialogBuilder.setPositiveButton(
        getString(R.string.ok_action).toUpperCase(),
        new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialog, int which) {
            SP.putInt(getString(R.string.preference_base_theme), getBaseTheme());
            setTheme();
          }
        });
    dialogBuilder.setNegativeButton(
        getString(R.string.cancel).toUpperCase(),
        new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            setBaseTheme(ThemeHelper.getBaseTheme(getApplicationContext()), false);
            setTheme();
          }
        });
    dialogBuilder.setView(dialogLayout);
    AlertDialog alertDialog = dialogBuilder.create();
    alertDialog.show();
    AlertDialogsHelper.setButtonTextColor(
        new int[] {DialogInterface.BUTTON_POSITIVE, DialogInterface.BUTTON_NEGATIVE},
        getAccentColor(),
        alertDialog);
  }

  private void primaryColorPiker() {
    final AlertDialog.Builder dialogBuilder =
        new AlertDialog.Builder(SettingsActivity.this, getDialogStyle());

    final View dialogLayout = getLayoutInflater().inflate(R.layout.color_piker_primary, null);
    final LineColorPicker colorPicker = dialogLayout.findViewById(R.id.color_picker_primary);
    final LineColorPicker colorPicker2 = dialogLayout.findViewById(R.id.color_picker_primary_2);
    final TextView dialogTitle = dialogLayout.findViewById(R.id.cp_primary_title);
    CardView dialogCardView = dialogLayout.findViewById(R.id.cp_primary_card);
    dialogCardView.setCardBackgroundColor(getCardBackgroundColor());

    setColor(colorPicker, colorPicker2, dialogTitle);

    dialogBuilder.setView(dialogLayout);

    dialogBuilder.setNeutralButton(
        getString(R.string.cancel).toUpperCase(),
        new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
              if (isTranslucentStatusBar()) {
                getWindow().setStatusBarColor(ColorPalette.getObscuredColor(getPrimaryColor()));
              } else getWindow().setStatusBarColor(getPrimaryColor());
            }
            toolbar.setBackgroundColor(getPrimaryColor());
            dialog.cancel();
          }
        });

    dialogBuilder.setPositiveButton(
        getString(R.string.ok_action).toUpperCase(),
        new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialog, int which) {
            SP.putInt(getString(R.string.preference_primary_color), colorPicker2.getColor());
            updateTheme();
            accentcolourchange(colorPicker2.getColor());
            if (swNavBar.isChecked()) setNavBarColor();

            setScrollViewColor(scr);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
              if (isTranslucentStatusBar()) {
                getWindow().setStatusBarColor(ColorPalette.getObscuredColor(getPrimaryColor()));
              } else {
                getWindow().setStatusBarColor(getPrimaryColor());
              }
            }
          }
        });

    dialogBuilder.setOnDismissListener(
        new DialogInterface.OnDismissListener() {
          @Override
          public void onDismiss(DialogInterface dialog) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
              if (isTranslucentStatusBar()) {
                getWindow().setStatusBarColor(ColorPalette.getObscuredColor(getPrimaryColor()));
              } else getWindow().setStatusBarColor(getPrimaryColor());
              if (isNavigationBarColored() && swNavBar.isChecked())
                getWindow().setNavigationBarColor(getPrimaryColor());
              else
                getWindow()
                    .setNavigationBarColor(
                        ContextCompat.getColor(getApplicationContext(), R.color.md_black_1000));
            }
            toolbar.setBackgroundColor(getPrimaryColor());
          }
        });
    AlertDialog alertDialog = dialogBuilder.create();
    alertDialog.show();
    AlertDialogsHelper.setButtonTextColor(
        new int[] {
          DialogInterface.BUTTON_POSITIVE,
          DialogInterface.BUTTON_NEGATIVE,
          DialogInterface.BUTTON_NEUTRAL
        },
        getAccentColor(),
        alertDialog);
  }

  private void accentcolourchange(final int color) {
    AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this, getDialogStyle());
    AlertDialogsHelper.getTextDialog(
        SettingsActivity.this,
        builder,
        R.string.accent_color,
        R.string.accent_primary_same_mssg,
        null);
    builder.setNegativeButton(this.getString(R.string.cancel).toUpperCase(), null);
    builder.setPositiveButton(
        this.getString(R.string.ok).toUpperCase(),
        new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialogInterface, int i) {
            SP.putInt(getString(R.string.preference_accent_color), color);
            updateTheme();
            updateViewswithAccentColor(getAccentColor());
          }
        });
    AlertDialog alertDialog = builder.create();
    alertDialog.show();
    AlertDialogsHelper.setButtonTextColor(
        new int[] {
          DialogInterface.BUTTON_POSITIVE,
          DialogInterface.BUTTON_NEGATIVE,
          DialogInterface.BUTTON_NEUTRAL
        },
        getAccentColor(),
        alertDialog);
  }

  private void setColor(
      final LineColorPicker colorPicker,
      final LineColorPicker colorPicker2,
      final TextView dialogTitle) {
    colorPicker.setColors(ColorPalette.getBaseColors(getApplicationContext()));
    for (int i : colorPicker.getColors())
      for (int i2 : ColorPalette.getColors(getBaseContext(), i))
        if (i2 == getPrimaryColor()) {
          colorPicker.setSelectedColor(i);
          colorPicker2.setColors(ColorPalette.getColors(getBaseContext(), i));
          colorPicker2.setSelectedColor(i2);
          break;
        }

    dialogTitle.setBackgroundColor(getPrimaryColor());

    colorPicker.setOnColorChangedListener(
        new OnColorChangedListener() {
          @Override
          public void onColorChanged(int c) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
              if (isTranslucentStatusBar()) {
                getWindow().setStatusBarColor(ColorPalette.getObscuredColor(getPrimaryColor()));
              } else getWindow().setStatusBarColor(c);
            }

            toolbar.setBackgroundColor(c);
            dialogTitle.setBackgroundColor(c);
            colorPicker2.setColors(
                ColorPalette.getColors(getApplicationContext(), colorPicker.getColor()));
            colorPicker2.setSelectedColor(colorPicker.getColor());
          }
        });
    colorPicker2.setOnColorChangedListener(
        new OnColorChangedListener() {
          @Override
          public void onColorChanged(int c) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
              if (isTranslucentStatusBar()) {
                getWindow().setStatusBarColor(ColorPalette.getObscuredColor(c));
              } else getWindow().setStatusBarColor(c);
              if (isNavigationBarColored()) getWindow().setNavigationBarColor(c);
              else
                getWindow()
                    .setNavigationBarColor(
                        ContextCompat.getColor(getApplicationContext(), R.color.md_black_1000));
            }
            toolbar.setBackgroundColor(c);
            dialogTitle.setBackgroundColor(c);
          }
        });
  }

  private void accentColorPiker() {
    final AlertDialog.Builder dialogBuilder =
        new AlertDialog.Builder(SettingsActivity.this, getDialogStyle());

    final View dialogLayout = getLayoutInflater().inflate(R.layout.color_piker_accent, null);
    final LineColorPicker colorPicker = dialogLayout.findViewById(R.id.color_picker_accent);
    final LineColorPicker colorPicker2 = dialogLayout.findViewById(R.id.color_picker_accent_2);
    final TextView dialogTitle = dialogLayout.findViewById(R.id.cp_accent_title);
    CardView cv = dialogLayout.findViewById(R.id.cp_accent_card);
    cv.setCardBackgroundColor(getCardBackgroundColor());

    setColor2(colorPicker, colorPicker2, dialogTitle);
    dialogTitle.setBackgroundColor(getAccentColor());

    dialogBuilder.setView(dialogLayout);

    dialogBuilder.setNeutralButton(
        getString(R.string.cancel).toUpperCase(),
        new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            dialog.cancel();
            updateViewswithAccentColor(getAccentColor());
          }
        });
    dialogBuilder.setPositiveButton(
        getString(R.string.ok_action).toUpperCase(),
        new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialog, int which) {
            SP.putInt(getString(R.string.preference_accent_color), colorPicker2.getColor());
            updateTheme();
            updateViewswithAccentColor(getAccentColor());
          }
        });
    dialogBuilder.setOnDismissListener(
        new DialogInterface.OnDismissListener() {
          @Override
          public void onDismiss(DialogInterface dialog) {
            updateViewswithAccentColor(getAccentColor());
          }
        });
    AlertDialog alertDialog = dialogBuilder.create();
    alertDialog.show();
    AlertDialogsHelper.setButtonTextColor(
        new int[] {
          DialogInterface.BUTTON_POSITIVE,
          DialogInterface.BUTTON_NEGATIVE,
          DialogInterface.BUTTON_NEUTRAL
        },
        getAccentColor(),
        alertDialog);
  }

  private void setColor2(
      final LineColorPicker colorPicker,
      final LineColorPicker colorPicker2,
      final TextView dialogTitle) {
    colorPicker.setColors(ColorPalette.getBaseColors(getApplicationContext()));
    for (int i : colorPicker.getColors())
      for (int i2 : ColorPalette.getColors(getBaseContext(), i))
        if (i2 == getAccentColor()) {
          colorPicker.setSelectedColor(i);
          colorPicker2.setColors(ColorPalette.getColors(getBaseContext(), i));
          colorPicker2.setSelectedColor(i2);
          break;
        }

    dialogTitle.setBackgroundColor(getPrimaryColor());

    colorPicker.setOnColorChangedListener(
        new OnColorChangedListener() {
          @Override
          public void onColorChanged(int c) {
            dialogTitle.setBackgroundColor(c);
            updateViewswithAccentColor(c);
            colorPicker2.setColors(
                ColorPalette.getColors(getApplicationContext(), colorPicker.getColor()));
            colorPicker2.setSelectedColor(colorPicker.getColor());
          }
        });
    colorPicker2.setOnColorChangedListener(
        new OnColorChangedListener() {
          @Override
          public void onColorChanged(int c) {
            dialogTitle.setBackgroundColor(c);
            updateViewswithAccentColor(c);
          }
        });
  }

  private void customizePictureViewer() {

    final AlertDialog.Builder dialogBuilder =
        new AlertDialog.Builder(SettingsActivity.this, getDialogStyle());

    View dialogLayout = getLayoutInflater().inflate(R.layout.dialog_media_viewer_theme, null);
    final SwitchCompat swApplyTheme_Viewer =
        dialogLayout.findViewById(R.id.apply_theme_3th_act_enabled);

    ((CardView) dialogLayout.findViewById(R.id.third_act_theme_card))
        .setCardBackgroundColor(getCardBackgroundColor());
    dialogLayout
        .findViewById(R.id.third_act_theme_title)
        .setBackgroundColor(getPrimaryColor()); // or GetPrimary
    ((TextView) dialogLayout.findViewById(R.id.apply_theme_3thAct_title))
        .setTextColor(getTextColor());
    ((TextView) dialogLayout.findViewById(R.id.apply_theme_3thAct_title_Sub))
        .setTextColor(getSubTextColor());
    ((IconicsImageView) dialogLayout.findViewById(R.id.ll_apply_theme_3thAct_icon))
        .setColor(getIconColor());

    swApplyTheme_Viewer.setChecked(isApplyThemeOnImgAct());
    swApplyTheme_Viewer.setOnCheckedChangeListener(
        new CompoundButton.OnCheckedChangeListener() {
          @Override
          public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            updateSwitchColor(swApplyTheme_Viewer, getAccentColor());
          }
        });
    updateSwitchColor(swApplyTheme_Viewer, getAccentColor());

    final LineColorPicker transparencyColorPicker =
        dialogLayout.findViewById(R.id.pickerTransparent);
    transparencyColorPicker.setColors(ColorPalette.getTransparencyShadows(getPrimaryColor()));
    transparencyColorPicker.setSelectedColor(
        ColorPalette.getTransparentColor(getPrimaryColor(), getTransparency()));

    /** TEXT VIEWS* */
    ((TextView) dialogLayout.findViewById(R.id.seek_bar_alpha_title)).setTextColor(getTextColor());
    ((TextView) dialogLayout.findViewById(R.id.seek_bar_alpha_title_Sub))
        .setTextColor(getSubTextColor());

    dialogBuilder.setView(dialogLayout);
    dialogBuilder.setNeutralButton(getString(R.string.cancel).toUpperCase(), null);
    dialogBuilder.setPositiveButton(
        getString(R.string.ok_action).toUpperCase(),
        new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialog, int which) {
            SharedPreferences.Editor editor = SP.getEditor();
            editor.putBoolean(
                getString(R.string.preference_apply_theme_pager), swApplyTheme_Viewer.isChecked());
            int c = Color.alpha(transparencyColorPicker.getColor());
            editor.putInt(getString(R.string.preference_transparency), 255 - c);
            editor.commit();
            updateTheme();
          }
        });

    AlertDialog alertDialog = dialogBuilder.create();
    alertDialog.show();
    AlertDialogsHelper.setButtonTextColor(
        new int[] {
          DialogInterface.BUTTON_POSITIVE,
          DialogInterface.BUTTON_NEGATIVE,
          DialogInterface.BUTTON_NEUTRAL
        },
        getAccentColor(),
        alertDialog);
  }

  private void updateViewswithAccentColor(int color) {
    txtGT.setTextColor(color);
    txtTT.setTextColor(color);
    txtPT.setTextColor(color);
    txtAT.setTextColor(color);

    updateSwitchColor(swDelayFullImage, color);
    updateSwitchColor(swNavBar, color);
    updateSwitchColor(swStatusBar, color);
    updateSwitchColor(swMaxLuminosity, color);
    updateSwitchColor(swPictureOrientation, color);
    updateSwitchColor(swAutoUpdate, color);
    updateSwitchColor(swSwipeDirection, color);
  }

  @Override
  public void onPostResume() {
    super.onPostResume();
    ActivitySwitchHelper.setContext(this);
    setTheme();
    securityObj.updateSecuritySetting();
  }

  private void setTheme() {

    /** BackGround * */
    findViewById(R.id.setting_background).setBackgroundColor(getBackgroundColor());

    /** Cards * */
    int color = getCardBackgroundColor();
    ((CardView) findViewById(R.id.general_setting_card)).setCardBackgroundColor(color);
    ((CardView) findViewById(R.id.theme_setting_card)).setCardBackgroundColor(color);
    ((CardView) findViewById(R.id.preview_picture_setting_card)).setCardBackgroundColor(color);
    ((CardView) findViewById(R.id.advanced_setting_card)).setCardBackgroundColor(color);

    toolbar.setBackgroundColor(getPrimaryColor());
    setSupportActionBar(toolbar);
    toolbar.setNavigationIcon(
        new IconicsDrawable(this)
            .icon(CommunityMaterial.Icon.cmd_arrow_left)
            .color(Color.WHITE)
            .sizeDp(19));
    toolbar.setNavigationOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            onBackPressed();
          }
        });

    setStatusBarColor();
    setNavBarColor();
    setRecentApp(getString(R.string.settings));
    setScrollViewColor(scr);
    updateViewswithAccentColor(getAccentColor());

    /** Icons * */
    color = getIconColor();
    ((IconicsImageView) findViewById(R.id.ll_switch_picture_orientation_icon)).setColor(color);
    ((IconicsImageView) findViewById(R.id.ll_switch_max_luminosity_icon)).setColor(color);
    ((IconicsImageView) findViewById(R.id.ll_switch_full_resolution_icon)).setColor(color);
    ((IconicsImageView) findViewById(R.id.traslucent_statusbar_icon)).setColor(color);
    ((IconicsImageView) findViewById(R.id.custom_3thact_icon)).setColor(color);
    ((IconicsImageView) findViewById(R.id.primary_color_icon)).setColor(color);
    ((IconicsImageView) findViewById(R.id.accent_color_icon)).setColor(color);
    ((IconicsImageView) findViewById(R.id.basic_theme_icon)).setColor(color);
    ((IconicsImageView) findViewById(R.id.n_columns_icon)).setColor(color);
    ((IconicsImageView) findViewById(R.id.nav_bar_icon)).setColor(color);
    ((IconicsImageView) findViewById(R.id.excluded_album_icon)).setColor(color);
    ((IconicsImageView) findViewById(R.id.auto_update_media_Icon)).setColor(color);
    ((IconicsImageView) findViewById(R.id.security_icon)).setColor(color);
    ((IconicsImageView) findViewById(R.id.camera_icon)).setColor(color);
    ((IconicsImageView) findViewById(R.id.map_provider_icon)).setColor(color);
    ((IconicsImageView) findViewById(R.id.media_viewer_swipe_direction_Icon)).setColor(color);
    ((IconicsImageView) findViewById(R.id.reset_settings_Icon)).setColor(color);

    /** TextViews * */
    color = getTextColor();
    ((TextView) findViewById(R.id.max_luminosity_Item)).setTextColor(color);
    ((TextView) findViewById(R.id.full_resolution_Item)).setTextColor(color);
    ((TextView) findViewById(R.id.picture_orientation_Item)).setTextColor(color);
    ((TextView) findViewById(R.id.custom_3thAct_title)).setTextColor(color);
    ((TextView) findViewById(R.id.Traslucent_StatusBar_Item)).setTextColor(color);
    ((TextView) findViewById(R.id.PrimaryColor_Item)).setTextColor(color);
    ((TextView) findViewById(R.id.accentColor_Item)).setTextColor(color);
    ((TextView) findViewById(R.id.basic_theme_item)).setTextColor(color);
    ((TextView) findViewById(R.id.n_columns_Item_Title)).setTextColor(color);
    ((TextView) findViewById(R.id.NavBar_Item)).setTextColor(color);
    ((TextView) findViewById(R.id.Excluded_Album_Item_Title)).setTextColor(color);
    ((TextView) findViewById(R.id.auto_update_media_Item)).setTextColor(color);
    ((TextView) findViewById(R.id.security_item_title)).setTextColor(color);
    ((TextView) findViewById(R.id.camera_item_title)).setTextColor(color);
    ((TextView) findViewById(R.id.map_provider_item_title)).setTextColor(color);
    ((TextView) findViewById(R.id.media_viewer_swipe_direction_Item)).setTextColor(color);
    ((TextView) findViewById(R.id.reset_settings_Item)).setTextColor(color);

    /** Sub Text Views* */
    color = getSubTextColor();
    ((TextView) findViewById(R.id.max_luminosity_Item_Sub)).setTextColor(color);
    ((TextView) findViewById(R.id.full_resolution_Item_Sub)).setTextColor(color);
    ((TextView) findViewById(R.id.custom_3thAct_Sub)).setTextColor(color);
    ((TextView) findViewById(R.id.picture_orientation_Item_Sub)).setTextColor(color);
    ((TextView) findViewById(R.id.Traslucent_StatusBar_Item_Sub)).setTextColor(color);
    ((TextView) findViewById(R.id.PrimaryColor_Item_Sub)).setTextColor(color);
    ((TextView) findViewById(R.id.accentColor_Item_Sub)).setTextColor(color);
    ((TextView) findViewById(R.id.basic_theme_item_sub)).setTextColor(color);
    ((TextView) findViewById(R.id.n_columns_Item_Title_Sub)).setTextColor(color);
    ((TextView) findViewById(R.id.NavBar_Item_Sub)).setTextColor(color);
    ((TextView) findViewById(R.id.Excluded_Album_Item_Title_Sub)).setTextColor(color);
    ((TextView) findViewById(R.id.auto_update_media_Item_sub)).setTextColor(color);
    ((TextView) findViewById(R.id.security_item_sub)).setTextColor(color);
    ((TextView) findViewById(R.id.map_provider_item_sub)).setTextColor(color);
    ((TextView) findViewById(R.id.media_viewer_swipe_direction_sub)).setTextColor(color);
    ((TextView) findViewById(R.id.camera_item_sub)).setTextColor(color);
    ((TextView) findViewById(R.id.reset_settings_Item_sub)).setTextColor(color);
  }

  @Override
  public void onBackPressed() {
    FragmentManager fm = getFragmentManager();
    if (fm.getBackStackEntryCount() > 0) {
      fm.popBackStack();
      if (fm.getBackStackEntryCount() == 0) findViewById(R.id.ll_camera).setVisibility(View.GONE);
      findViewById(R.id.settingAct_scrollView).setVisibility(View.VISIBLE);
      setToolbarCamera(false);
    } else {
      super.onBackPressed();
    }
  }

  private void setToolbarCamera(Boolean isCamera) {
    getSupportActionBar();
    if (isCamera) toolbar.setTitle(getString(R.string.camera_setting_title));
    else toolbar.setTitle(getString(R.string.settings));
  }

  private void resetSettingsDialog() {

    final short max_password_length = 128;

    final AlertDialog.Builder resetDialog =
        new AlertDialog.Builder(SettingsActivity.this, getDialogStyle());

    AlertDialogsHelper.getTextDialog(
        SettingsActivity.this, resetDialog, R.string.reset, R.string.reset_settings, null);

    resetDialog.setNegativeButton(this.getString(R.string.no_action).toUpperCase(), null);
    resetDialog.setPositiveButton(
        this.getString(R.string.yes_action).toUpperCase(),
        new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialog, int id) {
            if (securityObj.isActiveSecurity()) {
              final boolean[] passco = {false};
              AlertDialog.Builder passwordDialogBuilder =
                  new AlertDialog.Builder(SettingsActivity.this, getDialogStyle());
              final EditText editTextPassword =
                  securityObj.getInsertPasswordDialog(SettingsActivity.this, passwordDialogBuilder);
              passwordDialogBuilder.setNegativeButton(
                  getString(R.string.cancel).toUpperCase(), null);
              editTextPassword.setInputType(
                  InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
              editTextPassword.setHint(getResources().getString(R.string.enter_password));
              editTextPassword.setHintTextColor(getSubTextColor());
              editTextPassword.addTextChangedListener(
                  new TextWatcher() {
                    @Override
                    public void beforeTextChanged(
                        CharSequence charSequence, int i, int i1, int i2) {
                      // empty method
                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                      editTextPassword.setSelection(editTextPassword.getText().toString().length());
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                      if (securityObj.getTextInputLayout().getVisibility() == View.VISIBLE
                          && !passco[0]) {
                        securityObj.getTextInputLayout().setVisibility(View.INVISIBLE);
                      } else {
                        passco[0] = false;
                      }
                      if (editable.length() == max_password_length) {
                        editTextPassword.setText(
                            editable.toString().substring(0, max_password_length - 1));
                        editTextPassword.setSelection(max_password_length - 1);
                        Toast.makeText(
                                getApplicationContext(),
                                getResources().getString(R.string.max_password_length),
                                Toast.LENGTH_SHORT)
                            .show();
                      }
                    }
                  });
              passwordDialogBuilder.setPositiveButton(
                  getString(R.string.ok_action).toUpperCase(),
                  new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                      // This should br empty it will be overwrite later
                      // to avoid dismiss of the dialog on wrong password
                    }
                  });

              final AlertDialog passwordDialog = passwordDialogBuilder.create();
              passwordDialog
                  .getWindow()
                  .setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
              passwordDialog.show();
              AlertDialogsHelper.setButtonTextColor(
                  new int[] {DialogInterface.BUTTON_POSITIVE, DialogInterface.BUTTON_NEGATIVE},
                  getAccentColor(),
                  passwordDialog);
              passwordDialog
                  .getButton(AlertDialog.BUTTON_POSITIVE)
                  .setOnClickListener(
                      new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {

                          if (securityObj.checkPassword(editTextPassword.getText().toString())) {
                            passwordDialog.dismiss();
                            SP.clearPreferences();
                            recreate();
                            Snackbar.make(
                                    findViewById(android.R.id.content),
                                    R.string.settings_reset,
                                    Snackbar.LENGTH_SHORT)
                                .show();
                          } else {
                            passco[0] = true;
                            securityObj.getTextInputLayout().setVisibility(View.VISIBLE);
                            SnackBarHandler.show(parent, R.string.wrong_password);
                            editTextPassword.getText().clear();
                            editTextPassword.requestFocus();
                          }
                        }
                      });

            } else {
              String password = SP.getString(getString(R.string.preference_password_value), "");
              String securedLocalFolders =
                  SP.getString(
                      getString(R.string.preference_use_password_secured_local_folders), "");
              boolean activeSecurity =
                  SP.getBoolean(getString(R.string.preference_use_password), false);
              boolean hiddenFolders =
                  SP.getBoolean(getString(R.string.preference_use_password_on_hidden), false);
              boolean localFolders =
                  SP.getBoolean(getString(R.string.preference_use_password_on_folder), false);
              boolean deleteAction =
                  SP.getBoolean(getString(R.string.preference_use_password_on_delete), false);

              SP.clearPreferences();
              recreate();
              Snackbar.make(
                      findViewById(android.R.id.content),
                      R.string.settings_reset,
                      Snackbar.LENGTH_SHORT)
                  .show();

              SP.putString(getString(R.string.preference_password_value), password);
              SP.putString(
                  getString(R.string.preference_use_password_secured_local_folders),
                  securedLocalFolders);
              SP.putBoolean(getString(R.string.preference_use_password), activeSecurity);
              SP.putBoolean(getString(R.string.preference_use_password_on_hidden), hiddenFolders);
              SP.putBoolean(getString(R.string.preference_use_password_on_folder), localFolders);
              SP.putBoolean(getString(R.string.preference_use_password_on_delete), deleteAction);
              securityObj.updateSecuritySetting();
            }
          }
        });
    AlertDialog alertDialog = resetDialog.create();
    alertDialog.show();
    AlertDialogsHelper.setButtonTextColor(
        new int[] {DialogInterface.BUTTON_POSITIVE, DialogInterface.BUTTON_NEGATIVE},
        getAccentColor(),
        alertDialog);
  }

  private void openDialog(final Context context) {
    AlertDialog.Builder passwordDialogBuilder =
        new AlertDialog.Builder(SettingsActivity.this, getDialogStyle());
    passwordDialogBuilder.setNegativeButton(getString(R.string.cancel).toUpperCase(), null);

    passwordDialogBuilder.setPositiveButton(
        getString(R.string.ok_action).toUpperCase(),
        new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            // This should br empty it will be overwrite later
            // to avoid dismiss of the dialog on wrong password
          }
        });
    final AlertDialog passwordDialog = passwordDialogBuilder.create();
    passwordDialog.setTitle(R.string.camera_setting_title);
    passwordDialog.setMessage(getString(R.string.camera_support_options));
    passwordDialog.show();
    AlertDialogsHelper.setButtonTextColor(
        new int[] {DialogInterface.BUTTON_POSITIVE, DialogInterface.BUTTON_NEGATIVE},
        getAccentColor(),
        passwordDialog);

    passwordDialog
        .getButton(AlertDialog.BUTTON_POSITIVE)
        .setOnClickListener(
            new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                startActivity(new Intent(context, CameraActivity.class));
                finish();
              }
            });
  }

  private void openCameraSetting(final Context context) {
    TinyDB tinyDB = new TinyDB(context);
    if (tinyDB.getListInt("resolution_widths").size() != 0) {
      setToolbarCamera(true);
      MyPreferenceFragment fragment = new MyPreferenceFragment();
      getFragmentManager()
          .beginTransaction()
          .add(R.id.pref_container, fragment, "PREFERENCE_FRAGMENT")
          .addToBackStack(null)
          .commitAllowingStateLoss();
      findViewById(R.id.settingAct_scrollView).setVisibility(View.GONE);
    } else {
      openDialog(context);
    }
  }

  public boolean isUsingSAF() {
    // check Android version just to be safe
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      SharedPreferences sharedPreferences = getDefaultSharedPreferences(getApplicationContext());
      if (sharedPreferences.getBoolean(PreferenceKeys.getUsingSAFPreferenceKey(), false)) {
        return true;
      }
    }
    return false;
  }

  @TargetApi(Build.VERSION_CODES.LOLLIPOP)
  public void openFolderChooserDialogSAF(boolean from_preferences) {
    this.saf_dialog_from_preferences = from_preferences;
    Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
    // Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
    // intent.addCategory(Intent.CATEGORY_OPENABLE);
    startActivityForResult(intent, 42);
  }

  @TargetApi(Build.VERSION_CODES.LOLLIPOP)
  public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
    if (requestCode == 42) {
      if (resultCode == RESULT_OK && resultData != null) {
        Uri treeUri = resultData.getData();
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

      } else {
        // cancelled - if the user had yet to set a save location, make sure we switch SAF back off
        SharedPreferences sharedPreferences = getDefaultSharedPreferences(this);
        String uri =
            sharedPreferences.getString(PreferenceKeys.getSaveLocationSAFPreferenceKey(), "");
        if (uri.length() == 0) {
          SharedPreferences.Editor editor = sharedPreferences.edit();
          editor.putBoolean(PreferenceKeys.getUsingSAFPreferenceKey(), false);
          editor.apply();
        }
      }
    }
  }
}
