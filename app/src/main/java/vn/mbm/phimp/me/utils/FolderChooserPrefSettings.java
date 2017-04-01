package vn.mbm.phimp.me.utils;

import java.util.HashSet;
import java.util.Set;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by rohanagarwal94 on 1/4/17.
 */

public class FolderChooserPrefSettings {
    private static final String FOLDER_CHOOSER_PREF = "Pref";
    private static FolderChooserPrefSettings ourInstance = new FolderChooserPrefSettings();
    private SharedPreferences defaultPref;
    private SharedPreferences sharedpreferences;
    private final Object object = new Object();

    private FolderChooserPrefSettings() {
    }

    public static FolderChooserPrefSettings getInstance() {
        return ourInstance;
    }

    public static void init(Context context) {
        ourInstance.sharedpreferences = context.getSharedPreferences(FOLDER_CHOOSER_PREF, Context.MODE_PRIVATE);
        ourInstance.defaultPref = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public boolean get(String prefString) {
        boolean firstTime;
        synchronized (object) {
            firstTime = !sharedpreferences.contains(prefString);
        }

        synchronized (object) {
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putBoolean(prefString, true);
            editor.apply();
        }
        return firstTime;
    }

    public void setMaxFileSize(int maxFileSize) {
        synchronized (object) {
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putInt("MaxFileSize", maxFileSize);
            editor.apply();
        }
    }

    public int getMaxFileSize() {
        synchronized (object) {
            return sharedpreferences.getInt("MaxFileSize", 5);
        }
    }

    public void setMaxFileRadioButton(int radioButtonSelected) {
        synchronized (object) {
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putInt("RadioButtonSelected", radioButtonSelected);
            editor.apply();
        }
    }

    public int getMaxFileRadioButton() {
        synchronized (object) {
            return sharedpreferences.getInt("RadioButtonSelected", 2);
        }
    }

    public void setWhitelistedPaths(Set<String> whitelistedPaths) {
        synchronized (object) {
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.remove("WhitelistedPaths");
            editor.apply();
            editor.putStringSet("WhitelistedPaths", whitelistedPaths);
            editor.apply();
        }
    }

    public Set<String> getWhitelistedPaths() {
        synchronized (object) {
            Set<String> whitelistedPathsGson = sharedpreferences.getStringSet("WhitelistedPaths", null);
            return whitelistedPathsGson == null ? new HashSet<String>() : whitelistedPathsGson;
        }
    }

}