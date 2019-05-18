package org.fossasia.phimpme.opencamera.Camera;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceFragment;
import android.preference.PreferenceGroup;
import android.preference.PreferenceManager;
import android.preference.TwoStatePreference;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import org.fossasia.phimpme.gallery.activities.SettingsActivity;
import org.fossasia.phimpme.gallery.util.ThemeHelper;
import org.fossasia.phimpme.opencamera.Preview.Preview;
import org.fossasia.phimpme.opencamera.UI.FolderChooserDialog;

import java.util.ArrayList;

import org.fossasia.phimpme.R;
import org.fossasia.phimpme.utilities.SnackBarHandler;

/** Fragment to handle the Settings UI. Note that originally this was a
 *  PreferenceActivity rather than a PreferenceFragment which required all
 *  communication to be via the bundle (since this replaced the CameraActivity,
 *  meaning we couldn't access data from that class. This no longer applies due
 *  to now using a PreferenceFragment, but I've still kept with transferring
 *  information via the bundle (for the most part, at least).
 */
public class MyPreferenceFragment extends PreferenceFragment implements OnSharedPreferenceChangeListener {
	private static final String TAG = "MyPreferenceFragment";
	ThemeHelper themeHelper;
	TinyDB bundle;
	public static String new_save_location;
	View parent;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		if( MyDebug.LOG )
			Log.d(TAG, "onCreate");
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
		parent = getActivity().getWindow().getDecorView();

		themeHelper = new ThemeHelper(getActivity());

//		final Bundle bundle = getArguments();
		bundle = new TinyDB(getActivity());
		final int cameraId = bundle.getInt("cameraId");
		if( MyDebug.LOG )
			Log.d(TAG, "cameraId: " + cameraId);
		final int nCameras = bundle.getInt("nCameras");
		if( MyDebug.LOG )
			Log.d(TAG, "nCameras: " + nCameras);

		final String camera_api = bundle.getString("camera_api");
		
		final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this.getActivity());
		sharedPreferences.registerOnSharedPreferenceChangeListener(this);

		final boolean supports_auto_stabilise = bundle.getBoolean("supports_auto_stabilise");
		if( MyDebug.LOG )
			Log.d(TAG, "supports_auto_stabilise: " + supports_auto_stabilise);

		/*if( !supports_auto_stabilise ) {
			Preference pref = findPreference("preference_auto_stabilise");
			PreferenceGroup pg = (PreferenceGroup)this.findPreference("preference_category_camera_effects");
        	pg.removePreference(pref);
		}*/

		//readFromBundle(bundle, "color_effects", Preview.getColorEffectPreferenceKey(), Camera.Parameters.EFFECT_NONE, "preference_category_camera_effects");
		//readFromBundle(bundle, "scene_modes", Preview.getSceneModePreferenceKey(), Camera.Parameters.SCENE_MODE_AUTO, "preference_category_camera_effects");
		//readFromBundle(bundle, "white_balances", Preview.getWhiteBalancePreferenceKey(), Camera.Parameters.WHITE_BALANCE_AUTO, "preference_category_camera_effects");
		//readFromBundle(bundle, "isos", Preview.getISOPreferenceKey(), "auto", "preference_category_camera_effects");
		//readFromBundle(bundle, "exposures", "preference_exposure", "0", "preference_category_camera_effects");

		final boolean supports_face_detection = bundle.getBoolean("supports_face_detection");
		if( MyDebug.LOG )
			Log.d(TAG, "supports_face_detection: " + supports_face_detection);

		if( !supports_face_detection ) {
			Preference pref = findPreference("preference_face_detection");
			PreferenceGroup pg = (PreferenceGroup)this.findPreference("preference_category_camera_controls");
        	pg.removePreference(pref);
		}

		final boolean preference_pause_preview = bundle.getBoolean("preference_pause_preview");
		Log.e(TAG, "onCreate: FRAGMENT" +preference_pause_preview);

		final ArrayList<Integer> widths = bundle.getListInt("resolution_widths");
		final ArrayList<Integer> heights = bundle.getListInt("resolution_heights");
		if( widths != null && heights != null ) {
			CharSequence[] entries = new CharSequence[widths.size()];
			CharSequence[] values = new CharSequence[widths.size()];
			for(int i = 0; i< widths.size(); i++) {
				entries[i] = widths.get(i) + " x " + heights.get(i) + " " + Preview.getAspectRatioMPString(widths.get(i), heights.get(i));
				values[i] = widths.get(i) + " " + heights.get(i);
			}
			ListPreference lp = (ListPreference)findPreference("preference_resolution");
			lp.setEntries(entries);
			lp.setEntryValues(values);
			String resolution_preference_key = PreferenceKeys.getResolutionPreferenceKey(cameraId);
			String resolution_value = sharedPreferences.getString(resolution_preference_key, "");
			if( MyDebug.LOG )
				Log.d(TAG, "resolution_value: " + resolution_value);
			lp.setValue(resolution_value);
			// now set the key, so we save for the correct cameraId
			lp.setKey(resolution_preference_key);
		}
		else {
			Preference pref = findPreference("preference_resolution");
			PreferenceGroup pg = (PreferenceGroup)this.findPreference("preference_screen_photo_settings");
        	pg.removePreference(pref);
		}

		{
			final int n_quality = 100;
			CharSequence[] entries = new CharSequence[n_quality];
			CharSequence[] values = new CharSequence[n_quality];
			for(int i=0;i<n_quality;i++) {
				entries[i] = "" + (i+1) + "%";
				values[i] = "" + (i+1);
			}
			ListPreference lp = (ListPreference)findPreference("preference_quality");
			lp.setEntries(entries);
			lp.setEntryValues(values);
		}
		
		final boolean supports_raw = bundle.getBoolean("supports_raw");
		if( MyDebug.LOG )
			Log.d(TAG, "supports_raw: " + supports_raw);

		if( !supports_raw ) {
			Preference pref = findPreference("preference_raw");
			PreferenceGroup pg = (PreferenceGroup)this.findPreference("preference_screen_photo_settings");
        	pg.removePreference(pref);
		}
		else {
        	Preference pref = findPreference("preference_raw");
        	pref.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
        		@Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
            		if( MyDebug.LOG )
            			Log.d(TAG, "clicked raw: " + newValue);
            		if( newValue.equals("preference_raw_yes") ) {
            			// we check done_raw_info every time, so that this works if the user selects RAW again without leaving and returning to Settings
            			boolean done_raw_info = sharedPreferences.contains(PreferenceKeys.getRawInfoPreferenceKey());
            			if( !done_raw_info ) {
	        		        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MyPreferenceFragment.this.getActivity());
	        	            alertDialog.setTitle(R.string.preference_raw);
	        	            alertDialog.setMessage(R.string.raw_info);
	        	            alertDialog.setPositiveButton(android.R.string.ok, null);
	        	            alertDialog.setNegativeButton(R.string.dont_show_again, new OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
				            		if( MyDebug.LOG )
				            			Log.d(TAG, "user clicked dont_show_again for raw info dialog");
				            		SharedPreferences.Editor editor = sharedPreferences.edit();
				            		editor.putBoolean(PreferenceKeys.getRawInfoPreferenceKey(), true);
				            		editor.apply();
								}
	        	            });
	        	            alertDialog.show();
            			}
                    }
                	return true;
                }
            });        	
		}

		final boolean supports_hdr = bundle.getBoolean("supports_hdr");
		if( MyDebug.LOG )
			Log.d(TAG, "supports_hdr: " + supports_hdr);

		if( !supports_hdr ) {
			Preference pref = findPreference("preference_hdr_save_expo");
			PreferenceGroup pg = (PreferenceGroup)this.findPreference("preference_screen_photo_settings");
        	pg.removePreference(pref);
		}

		final boolean supports_expo_bracketing = bundle.getBoolean("supports_expo_bracketing");
		if( MyDebug.LOG )
			Log.d(TAG, "supports_expo_bracketing: " + supports_expo_bracketing);

		final int max_expo_bracketing_n_images = bundle.getInt("max_expo_bracketing_n_images");
		if( MyDebug.LOG )
			Log.d(TAG, "max_expo_bracketing_n_images: " + max_expo_bracketing_n_images);

		final boolean supports_exposure_compensation = bundle.getBoolean("supports_exposure_compensation");
		if( MyDebug.LOG )
			Log.d(TAG, "supports_exposure_compensation: " + supports_exposure_compensation);

		final boolean supports_iso_range = bundle.getBoolean("supports_iso_range");
		if( MyDebug.LOG )
			Log.d(TAG, "supports_iso_range: " + supports_iso_range);

		final boolean supports_exposure_time = bundle.getBoolean("supports_exposure_time");
		if( MyDebug.LOG )
			Log.d(TAG, "supports_exposure_time: " + supports_exposure_time);

		final boolean supports_white_balance_temperature = bundle.getBoolean("supports_white_balance_temperature");
		if( MyDebug.LOG )
			Log.d(TAG, "supports_white_balance_temperature: " + supports_white_balance_temperature);

		if( !supports_expo_bracketing || max_expo_bracketing_n_images <= 3 ) {
			Preference pref = findPreference("preference_expo_bracketing_n_images");
			PreferenceGroup pg = (PreferenceGroup) this.findPreference("preference_screen_photo_settings");
			pg.removePreference(pref);
		}
		if( !supports_expo_bracketing ) {
			Preference pref = findPreference("preference_expo_bracketing_stops");
			PreferenceGroup pg = (PreferenceGroup)this.findPreference("preference_screen_photo_settings");
        	pg.removePreference(pref);
		}

		final boolean can_disable_shutter_sound = bundle.getBoolean("can_disable_shutter_sound");
		if( MyDebug.LOG )
			Log.d(TAG, "can_disable_shutter_sound: " + can_disable_shutter_sound);
        if( Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1 || !can_disable_shutter_sound ) {
        	// Camera.enableShutterSound requires JELLY_BEAN_MR1 or greater
        	Preference pref = findPreference("preference_shutter_sound");
        	PreferenceGroup pg = (PreferenceGroup)this.findPreference("preference_screen_camera_controls_more");
        	pg.removePreference(pref);
        }

        if( Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT ) {
        	// Some immersive modes require KITKAT - simpler to require Kitkat for any of the menu options
        	Preference pref = findPreference("preference_immersive_mode");
        	PreferenceGroup pg = (PreferenceGroup)this.findPreference("preference_screen_gui");
        	pg.removePreference(pref);
        }
        
		final boolean using_android_l = bundle.getBoolean("using_android_l");
        if( !using_android_l ) {
        	Preference pref = findPreference("preference_show_iso");
        	PreferenceGroup pg = (PreferenceGroup)this.findPreference("preference_screen_gui");
        	pg.removePreference(pref);
        }
        if( !using_android_l ) {
        	Preference pref = findPreference("preference_camera2_fake_flash");
        	PreferenceGroup pg = (PreferenceGroup)this.findPreference("preference_category_photo_debugging");
        	pg.removePreference(pref);

			pref = findPreference("preference_camera2_fast_burst");
			pg = (PreferenceGroup)this.findPreference("preference_category_photo_debugging");
			pg.removePreference(pref);
        }

		final boolean supports_camera2 = bundle.getBoolean("supports_camera2");
		if( MyDebug.LOG )
			Log.d(TAG, "supports_camera2: " + supports_camera2);
        if( supports_camera2 ) {
        	final Preference pref = findPreference("preference_use_camera2");
            pref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference arg0) {
					AlertDialog.Builder builder;
					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
						builder = new AlertDialog.Builder(getContext(), android.R.style.Theme_Material_Dialog_Alert);
					} else {
						builder = new AlertDialog.Builder(getContext());
					}
					builder.setTitle("Alert")
							.setMessage("Changes will take place after the app is restarted. Restart now?")
							.setPositiveButton("Restart now", new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int which) {
									if( pref.getKey().equals("preference_use_camera2") ) {
										if( MyDebug.LOG )
											Log.d(TAG, "user clicked camera2 API - need to restart");
										// see http://stackoverflow.com/questions/2470870/force-application-to-restart-on-first-activity
										Intent i = getActivity().getBaseContext().getPackageManager().getLaunchIntentForPackage( getActivity().getBaseContext().getPackageName() );
										i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
										startActivity(i);
									}
								}
							})
							.setNegativeButton("Restart Later", new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int which) {
									// do nothing
								}
							})
							.setIcon(android.R.drawable.ic_dialog_alert)
							.show();
                	return false;
                }
            });
        }

        {
        	Preference pref = findPreference("preference_save_location");
        	pref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
        		@Override
                public boolean onPreferenceClick(Preference arg0) {
            		if( MyDebug.LOG )
            			Log.d(TAG, "clicked save location");
            		SettingsActivity main_activity = (SettingsActivity) MyPreferenceFragment.this.getActivity();
            		if( main_activity.isUsingSAF() ) {
                		main_activity.openFolderChooserDialogSAF(true);
            			return true;
                    }
            		else {
						FolderChooserDialog fragment = new SaveFolderChooserDialog();
                		fragment.show(getFragmentManager(), "FOLDER_FRAGMENT");
                    	return true;
            		}
                }
            });        	
        }

        if( Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP ) {
        	Preference pref = findPreference("preference_using_saf");
        	PreferenceGroup pg = (PreferenceGroup)this.findPreference("preference_screen_camera_controls_more");
        	pg.removePreference(pref);
        }
        else {
            final Preference pref = findPreference("preference_using_saf");
            pref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference arg0) {
                	if( pref.getKey().equals("preference_using_saf") ) {
                		if( MyDebug.LOG )
                			Log.d(TAG, "user clicked saf");
            			if( sharedPreferences.getBoolean(PreferenceKeys.getUsingSAFPreferenceKey(), false) ) {
                    		if( MyDebug.LOG )
                    			Log.d(TAG, "saf is now enabled");
                    		// seems better to alway re-show the dialog when the user selects, to make it clear where files will be saved (as the SAF location in general will be different to the non-SAF one)
                    		//String uri = sharedPreferences.getString(PreferenceKeys.getSaveLocationSAFPreferenceKey(), "");
                    		//if( uri.length() == 0 )
                    		{
                        		SettingsActivity main_activity = (SettingsActivity) MyPreferenceFragment.this.getActivity();
								SnackBarHandler.show(parent,R.string.saf_select_save_location);
                        		main_activity.openFolderChooserDialogSAF(true);
                    		}
            			}
            			else {
                    		if( MyDebug.LOG )
                    			Log.d(TAG, "saf is now disabled");
            			}
                	}
                	return false;
                }
            });
        }

	}

	public static class SaveFolderChooserDialog extends FolderChooserDialog {
		@Override
		public void onDismiss(DialogInterface dialog) {
			if( MyDebug.LOG )
				Log.d(TAG, "FolderChooserDialog dismissed");
			// n.b., fragments have to be static (as they might be inserted into a new Activity - see http://stackoverflow.com/questions/15571010/fragment-inner-class-should-be-static),
			// so we access the CameraActivity via the fragment's getActivity().
			new_save_location = this.getChosenFolder();
			SettingsActivity main_activity = (SettingsActivity) this.getActivity();
			if(new_save_location!=null){
				Toast.makeText(main_activity,"Changed save location to: \n"+new_save_location,Toast.LENGTH_SHORT).show();
			}
			super.onDismiss(dialog);
		}
	}

	public void onResume() {
		super.onResume();
		// prevent fragment being transparent
		// note, setting color here only seems to affect the "main" preference fragment screen, and not sub-screens
		// note, on Galaxy Nexus Android 4.3 this sets to black rather than the dark grey that the background theme should be (and what the sub-screens use); works okay on Nexus 7 Android 5
		// we used to use a light theme for the PreferenceFragment, but mixing themes in same activity seems to cause problems (e.g., for EditTextPreference colors)
		TypedArray array = getActivity().getTheme().obtainStyledAttributes(new int[] {
			    android.R.attr.colorBackground
		});
		int backgroundColor = array.getColor(0, Color.BLACK);
		/*if( MyDebug.LOG ) {
			int r = (backgroundColor >> 16) & 0xFF;
			int g = (backgroundColor >> 8) & 0xFF;
			int b = (backgroundColor >> 0) & 0xFF;
			Log.d(TAG, "backgroundColor: " + r + " , " + g + " , " + b);
		}*/
		if (getView()!=null)
		getView().setBackgroundColor(backgroundColor);
		array.recycle();

		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this.getActivity());
		sharedPreferences.registerOnSharedPreferenceChangeListener(this);
	}

	public void onPause() {
		super.onPause();
	}

	/* So that manual changes to the checkbox/switch preferences, while the preferences are showing, show up;
	 * in particular, needed for preference_using_saf, when the user cancels the SAF dialog (see
	 * CameraActivity.onActivityResult).
	 */
	public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
		if( MyDebug.LOG )
			Log.d(TAG, "onSharedPreferenceChanged");
        Preference pref = findPreference(key);
	    if( pref instanceof TwoStatePreference){
	    	TwoStatePreference twoStatePref = (TwoStatePreference)pref;
	    	twoStatePref.setChecked(prefs.getBoolean(key, true));
	    }
	}

}
