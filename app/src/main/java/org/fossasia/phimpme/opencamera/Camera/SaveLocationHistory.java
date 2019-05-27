package org.fossasia.phimpme.opencamera.Camera;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import java.util.ArrayList;

/** Handles a history of save locations. */
public class SaveLocationHistory {
  private static final String TAG = "SaveLocationHistory";
  private final CameraActivity main_activity;
  private final String pref_base;
  private final ArrayList<String> save_location_history = new ArrayList<>();

  /**
   * Creates a new SaveLocationHistory class. This manages a history of save folder locations.
   *
   * @param main_activity CameraActivity.
   * @param pref_base String to use for shared preferences.
   * @param folder_name The current save folder.
   */
  SaveLocationHistory(CameraActivity main_activity, String pref_base, String folder_name) {
    if (MyDebug.LOG) Log.d(TAG, "pref_base: " + pref_base);
    this.main_activity = main_activity;
    this.pref_base = pref_base;
    SharedPreferences sharedPreferences =
        PreferenceManager.getDefaultSharedPreferences(main_activity);

    // read save locations
    save_location_history.clear();
    int save_location_history_size = sharedPreferences.getInt(pref_base + "_size", 0);
    if (MyDebug.LOG) Log.d(TAG, "save_location_history_size: " + save_location_history_size);
    for (int i = 0; i < save_location_history_size; i++) {
      String string = sharedPreferences.getString(pref_base + "_" + i, null);
      if (string != null) {
        if (MyDebug.LOG) Log.d(TAG, "save_location_history " + i + ": " + string);
        save_location_history.add(string);
      }
    }
    // also update, just in case a new folder has been set
    updateFolderHistory(
        folder_name); // update_icon can be false, as updateGalleryIcon() is called later in
    // CameraActivity.onResume()
    // updateFolderHistory("/sdcard/Pictures/OpenCameraTest");
  }

  /**
   * Updates the save history with the supplied folder name
   *
   * @param folder_name The folder name to add or update in the history.
   */
  public void updateFolderHistory(String folder_name) {
    if (MyDebug.LOG) {
      Log.d(TAG, "updateFolderHistory: " + folder_name);
      Log.d(TAG, "save_location_history size: " + save_location_history.size());
      for (int i = 0; i < save_location_history.size(); i++) {
        Log.d(TAG, save_location_history.get(i));
      }
    }
    while (save_location_history.remove(folder_name)) {}
    save_location_history.add(folder_name);
    while (save_location_history.size() > 6) {
      save_location_history.remove(0);
    }
    writeSaveLocations();
    if (MyDebug.LOG) {
      Log.d(TAG, "updateFolderHistory exit:");
      Log.d(TAG, "save_location_history size: " + save_location_history.size());
      for (int i = 0; i < save_location_history.size(); i++) {
        Log.d(TAG, save_location_history.get(i));
      }
    }
  }

  /**
   * Clears the folder history, and reinitialise it with the current folder.
   *
   * @param folder_name The current folder name.
   */
  void clearFolderHistory(String folder_name) {
    if (MyDebug.LOG) Log.d(TAG, "clearFolderHistory: " + folder_name);
    save_location_history.clear();
    updateFolderHistory(folder_name); // to re-add the current choice, and save
  }

  /** Writes the history to the SharedPreferences. */
  private void writeSaveLocations() {
    if (MyDebug.LOG) Log.d(TAG, "writeSaveLocations");
    SharedPreferences sharedPreferences =
        PreferenceManager.getDefaultSharedPreferences(main_activity);
    SharedPreferences.Editor editor = sharedPreferences.edit();
    editor.putInt(pref_base + "_size", save_location_history.size());
    if (MyDebug.LOG) Log.d(TAG, "save_location_history_size = " + save_location_history.size());
    for (int i = 0; i < save_location_history.size(); i++) {
      String string = save_location_history.get(i);
      editor.putString(pref_base + "_" + i, string);
    }
    editor.apply();
  }

  /**
   * Return the size of the history.
   *
   * @return The size of the history.
   */
  public int size() {
    return save_location_history.size();
  }

  /**
   * Returns a save location entry.
   *
   * @param index The index to return.
   * @return The save location at this index.
   */
  public String get(int index) {
    return save_location_history.get(index);
  }

  // for testing:
  /**
   * Should be used for testing only.
   *
   * @param value The value to search the location history for.
   * @return Whether the save location history contains the supplied value.
   */
  public boolean contains(String value) {
    return save_location_history.contains(value);
  }
}
