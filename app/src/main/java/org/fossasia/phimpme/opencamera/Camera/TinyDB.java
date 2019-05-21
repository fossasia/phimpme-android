package org.fossasia.phimpme.opencamera.Camera; /*
                                                 * Copyright 2014 KC Ochibili
                                                 *
                                                 * Licensed under the Apache License, Version 2.0 (the "License");
                                                 * you may not use this file except in compliance with the License.
                                                 * You may obtain a copy of the License at
                                                 *
                                                 * http://www.apache.org/licenses/LICENSE-2.0
                                                 *
                                                 * Unless required by applicable law or agreed to in writing, software
                                                 * distributed under the License is distributed on an "AS IS" BASIS,
                                                 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
                                                 * See the License for the specific language governing permissions and
                                                 * limitations under the License.
                                                 */

/*
 *  The "‚‗‚" character is not a comma, it is the SINGLE LOW-9 QUOTATION MARK unicode 201A
 *  and unicode 2017 that are used for separating the items in a list.
 */

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

public class TinyDB {

  private SharedPreferences preferences;
  private String DEFAULT_APP_IMAGEDATA_DIRECTORY;
  private String lastImagePath = "";

  public TinyDB(Context appContext) {
    preferences = PreferenceManager.getDefaultSharedPreferences(appContext);
  }

  /**
   * Decodes the Bitmap from 'path' and returns it
   *
   * @param path image path
   * @return the Bitmap from 'path'
   */
  public Bitmap getImage(String path) {
    Bitmap bitmapFromPath = null;
    try {
      bitmapFromPath = BitmapFactory.decodeFile(path);

    } catch (Exception e) {
      // TODO: handle exception
      e.printStackTrace();
    }

    return bitmapFromPath;
  }

  /**
   * Returns the String path of the last saved image
   *
   * @return string path of the last saved image
   */
  public String getSavedImagePath() {
    return lastImagePath;
  }

  /**
   * Saves 'theBitmap' into folder 'theFolder' with the name 'theImageName'
   *
   * @param theFolder the folder path dir you want to save it to e.g "DropBox/WorkImages"
   * @param theImageName the name you want to assign to the image file e.g "MeAtLunch.png"
   * @param theBitmap the image you want to save as a Bitmap
   * @return returns the full path(file system address) of the saved image
   */
  public String putImage(String theFolder, String theImageName, Bitmap theBitmap) {
    if (theFolder == null || theImageName == null || theBitmap == null) return null;

    this.DEFAULT_APP_IMAGEDATA_DIRECTORY = theFolder;
    String mFullPath = setupFullPath(theImageName);

    if (!mFullPath.equals("")) {
      lastImagePath = mFullPath;
      saveBitmap(mFullPath, theBitmap);
    }

    return mFullPath;
  }

  /**
   * Saves 'theBitmap' into 'fullPath'
   *
   * @param fullPath full path of the image file e.g. "Images/MeAtLunch.png"
   * @param theBitmap the image you want to save as a Bitmap
   * @return true if image was saved, false otherwise
   */
  public boolean putImageWithFullPath(String fullPath, Bitmap theBitmap) {
    return !(fullPath == null || theBitmap == null) && saveBitmap(fullPath, theBitmap);
  }

  /**
   * Creates the path for the image with name 'imageName' in DEFAULT_APP.. directory
   *
   * @param imageName name of the image
   * @return the full path of the image. If it failed to create directory, return empty string
   */
  private String setupFullPath(String imageName) {
    File mFolder =
        new File(Environment.getExternalStorageDirectory(), DEFAULT_APP_IMAGEDATA_DIRECTORY);

    if (isExternalStorageReadable() && isExternalStorageWritable() && !mFolder.exists()) {
      if (!mFolder.mkdirs()) {
        Log.e("ERROR", "Failed to setup folder");
        return "";
      }
    }

    return mFolder.getPath() + '/' + imageName;
  }

  /**
   * Saves the Bitmap as a PNG file at path 'fullPath'
   *
   * @param fullPath path of the image file
   * @param bitmap the image as a Bitmap
   * @return true if it successfully saved, false otherwise
   */
  private boolean saveBitmap(String fullPath, Bitmap bitmap) {
    if (fullPath == null || bitmap == null) return false;

    boolean fileCreated = false;
    boolean bitmapCompressed = false;
    boolean streamClosed = false;

    File imageFile = new File(fullPath);

    if (imageFile.exists()) if (!imageFile.delete()) return false;

    try {
      fileCreated = imageFile.createNewFile();

    } catch (IOException e) {
      e.printStackTrace();
    }

    FileOutputStream out = null;
    try {
      out = new FileOutputStream(imageFile);
      bitmapCompressed = bitmap.compress(CompressFormat.PNG, 100, out);

    } catch (Exception e) {
      e.printStackTrace();
      bitmapCompressed = false;

    } finally {
      if (out != null) {
        try {
          out.flush();
          out.close();
          streamClosed = true;

        } catch (IOException e) {
          e.printStackTrace();
          streamClosed = false;
        }
      }
    }

    return (fileCreated && bitmapCompressed && streamClosed);
  }

  // Getters

  /**
   * Get int value from SharedPreferences at 'key'. If key not found, return 'defaultValue'
   *
   * @param key SharedPreferences key
   * @return int value at 'key' or 'defaultValue' if key not found
   */
  public int getInt(String key) {
    return preferences.getInt(key, 0);
  }

  /**
   * Get parsed ArrayList of Integers from SharedPreferences at 'key'
   *
   * @param key SharedPreferences key
   * @return ArrayList of Integers
   */
  public ArrayList<Integer> getListInt(String key) {
    String[] myList = TextUtils.split(preferences.getString(key, ""), "‚‗‚");
    ArrayList<String> arrayToList = new ArrayList<String>(Arrays.asList(myList));
    ArrayList<Integer> newList = new ArrayList<Integer>();

    for (String item : arrayToList) newList.add(Integer.parseInt(item));

    return newList;
  }

  /**
   * Get long value from SharedPreferences at 'key'. If key not found, return 'defaultValue'
   *
   * @param key SharedPreferences key
   * @param defaultValue long value returned if key was not found
   * @return long value at 'key' or 'defaultValue' if key not found
   */
  public long getLong(String key, long defaultValue) {
    return preferences.getLong(key, defaultValue);
  }

  /**
   * Get float value from SharedPreferences at 'key'. If key not found, return 'defaultValue'
   *
   * @param key SharedPreferences key
   * @param defaultValue float value returned if key was not found
   * @return float value at 'key' or 'defaultValue' if key not found
   */
  public float getFloat(String key) {
    return preferences.getFloat(key, 0);
  }

  /**
   * Get double value from SharedPreferences at 'key'. If exception thrown, return 'defaultValue'
   *
   * @param key SharedPreferences key
   * @param defaultValue double value returned if exception is thrown
   * @return double value at 'key' or 'defaultValue' if exception is thrown
   */
  public double getDouble(String key, double defaultValue) {
    String number = getString(key);

    try {
      return Double.parseDouble(number);

    } catch (NumberFormatException e) {
      return defaultValue;
    }
  }

  /**
   * Get parsed ArrayList of Double from SharedPreferences at 'key'
   *
   * @param key SharedPreferences key
   * @return ArrayList of Double
   */
  public ArrayList<Double> getListDouble(String key) {
    String[] myList = TextUtils.split(preferences.getString(key, ""), "‚‗‚");
    ArrayList<String> arrayToList = new ArrayList<String>(Arrays.asList(myList));
    ArrayList<Double> newList = new ArrayList<Double>();

    for (String item : arrayToList) newList.add(Double.parseDouble(item));

    return newList;
  }

  /**
   * Get String value from SharedPreferences at 'key'. If key not found, return ""
   *
   * @param key SharedPreferences key
   * @return String value at 'key' or "" (empty String) if key not found
   */
  public String getString(String key) {
    return preferences.getString(key, "");
  }

  /**
   * Get parsed ArrayList of String from SharedPreferences at 'key'
   *
   * @param key SharedPreferences key
   * @return ArrayList of String
   */
  public ArrayList<String> getListString(String key) {
    return new ArrayList<String>(
        Arrays.asList(TextUtils.split(preferences.getString(key, ""), "‚‗‚")));
  }

  /**
   * Get boolean value from SharedPreferences at 'key'. If key not found, return 'defaultValue'
   *
   * @param key SharedPreferences key
   * @param defaultValue boolean value returned if key was not found
   * @return boolean value at 'key' or 'defaultValue' if key not found
   */
  public boolean getBoolean(String key) {
    return preferences.getBoolean(key, false);
  }

  /**
   * Get parsed ArrayList of Boolean from SharedPreferences at 'key'
   *
   * @param key SharedPreferences key
   * @return ArrayList of Boolean
   */
  public ArrayList<Boolean> getListBoolean(String key) {
    ArrayList<String> myList = getListString(key);
    ArrayList<Boolean> newList = new ArrayList<Boolean>();

    for (String item : myList) {
      if (item.equals("true")) {
        newList.add(true);
      } else {
        newList.add(false);
      }
    }

    return newList;
  }

  //    public ArrayList<Object> getListObject(String key, Class<?> mClass){
  //    	Gson gson = new Gson();
  //
  //    	ArrayList<String> objStrings = getListString(key);
  //    	ArrayList<Object> objects =  new ArrayList<Object>();
  //
  //    	for(String jObjString : objStrings){
  //    		Object value  = gson.fromJson(jObjString,  mClass);
  //    		objects.add(value);
  //    	}
  //    	return objects;
  //    }

  //    public <T> T getObject(String key, Class<T> classOfT){
  //
  //        String json = getString(key);
  //        Object value = new Gson().fromJson(json, classOfT);
  //        if (value == null)
  //            throw new NullPointerException();
  //        return (T)value;
  //    }

  // Put methods

  /**
   * Put int value into SharedPreferences with 'key' and save
   *
   * @param key SharedPreferences key
   * @param value int value to be added
   */
  public void putInt(String key, int value) {
    if (key == null) return;
    preferences.edit().putInt(key, value).apply();
  }

  /**
   * Put ArrayList of Integer into SharedPreferences with 'key' and save
   *
   * @param key SharedPreferences key
   * @param intList ArrayList of Integer to be added
   */
  public void putListInt(String key, ArrayList<Integer> intList) {
    if (key == null) return;
    if (intList == null) return;
    Integer[] myIntList = intList.toArray(new Integer[intList.size()]);
    preferences.edit().putString(key, TextUtils.join("‚‗‚", myIntList)).apply();
  }

  /**
   * Put long value into SharedPreferences with 'key' and save
   *
   * @param key SharedPreferences key
   * @param value long value to be added
   */
  public void putLong(String key, long value) {
    if (key == null) return;
    preferences.edit().putLong(key, value).apply();
  }

  /**
   * Put float value into SharedPreferences with 'key' and save
   *
   * @param key SharedPreferences key
   * @param value float value to be added
   */
  public void putFloat(String key, float value) {
    if (key == null) return;
    preferences.edit().putFloat(key, value).apply();
  }

  /**
   * Put double value into SharedPreferences with 'key' and save
   *
   * @param key SharedPreferences key
   * @param value double value to be added
   */
  public void putDouble(String key, double value) {
    if (key == null) return;
    putString(key, String.valueOf(value));
  }

  /**
   * Put ArrayList of Double into SharedPreferences with 'key' and save
   *
   * @param key SharedPreferences key
   * @param doubleList ArrayList of Double to be added
   */
  public void putListDouble(String key, ArrayList<Double> doubleList) {
    if (key == null) return;
    Double[] myDoubleList = doubleList.toArray(new Double[doubleList.size()]);
    preferences.edit().putString(key, TextUtils.join("‚‗‚", myDoubleList)).apply();
  }

  /**
   * Put String value into SharedPreferences with 'key' and save
   *
   * @param key SharedPreferences key
   * @param value String value to be added
   */
  public void putString(String key, String value) {
    if (key == null) return;
    if (value == null) return;
    checkForNullValue(value);
    preferences.edit().putString(key, value).apply();
  }

  /**
   * Put ArrayList of String into SharedPreferences with 'key' and save
   *
   * @param key SharedPreferences key
   * @param stringList ArrayList of String to be added
   */
  public void putListString(String key, ArrayList<String> stringList) {
    if (key == null) return;
    if (stringList == null) return;
    String[] myStringList = stringList.toArray(new String[stringList.size()]);
    preferences.edit().putString(key, TextUtils.join("‚‗‚", myStringList)).apply();
  }

  /**
   * Put boolean value into SharedPreferences with 'key' and save
   *
   * @param key SharedPreferences key
   * @param value boolean value to be added
   */
  public void putBoolean(String key, boolean value) {
    if (key == null) return;
    preferences.edit().putBoolean(key, value).apply();
  }

  /**
   * Put ArrayList of Boolean into SharedPreferences with 'key' and save
   *
   * @param key SharedPreferences key
   * @param boolList ArrayList of Boolean to be added
   */
  public void putListBoolean(String key, ArrayList<Boolean> boolList) {
    if (key == null) return;
    ArrayList<String> newList = new ArrayList<String>();

    for (Boolean item : boolList) {
      if (item) {
        newList.add("true");
      } else {
        newList.add("false");
      }
    }

    putListString(key, newList);
  }

  /**
   * Put ObJect any type into SharedPrefrences with 'key' and save
   *
   * @param key SharedPreferences key
   * @param obj is the Object you want to put
   */
  //    public void putObject(String key, Object obj){
  //    	checkForNullKey(key);
  //    	Gson gson = new Gson();
  //    	putString(key, gson.toJson(obj));
  //    }
  //
  //    public void putListObject(String key, ArrayList<Object> objArray){
  //    	checkForNullKey(key);
  //    	Gson gson = new Gson();
  //    	ArrayList<String> objStrings = new ArrayList<String>();
  //    	for(Object obj : objArray){
  //    		objStrings.add(gson.toJson(obj));
  //    	}
  //    	putListString(key, objStrings);
  //    }

  /**
   * Remove SharedPreferences item with 'key'
   *
   * @param key SharedPreferences key
   */
  public void remove(String key) {
    preferences.edit().remove(key).apply();
  }

  /**
   * Delete image file at 'path'
   *
   * @param path path of image file
   * @return true if it successfully deleted, false otherwise
   */
  public boolean deleteImage(String path) {
    return new File(path).delete();
  }

  /** Clear SharedPreferences (remove everything) */
  public void clear() {
    preferences.edit().clear().apply();
  }

  /**
   * Retrieve all values from SharedPreferences. Do not modify collection return by method
   *
   * @return a Map representing a list of key/value pairs from SharedPreferences
   */
  public Map<String, ?> getAll() {
    return preferences.getAll();
  }

  /**
   * Register SharedPreferences change listener
   *
   * @param listener listener object of OnSharedPreferenceChangeListener
   */
  public void registerOnSharedPreferenceChangeListener(
      SharedPreferences.OnSharedPreferenceChangeListener listener) {

    preferences.registerOnSharedPreferenceChangeListener(listener);
  }

  /**
   * Unregister SharedPreferences change listener
   *
   * @param listener listener object of OnSharedPreferenceChangeListener to be unregistered
   */
  public void unregisterOnSharedPreferenceChangeListener(
      SharedPreferences.OnSharedPreferenceChangeListener listener) {

    preferences.unregisterOnSharedPreferenceChangeListener(listener);
  }

  /**
   * Check if external storage is writable or not
   *
   * @return true if writable, false otherwise
   */
  public static boolean isExternalStorageWritable() {
    return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
  }

  /**
   * Check if external storage is readable or not
   *
   * @return true if readable, false otherwise
   */
  public static boolean isExternalStorageReadable() {
    String state = Environment.getExternalStorageState();

    return Environment.MEDIA_MOUNTED.equals(state)
        || Environment.MEDIA_MOUNTED_READ_ONLY.equals(state);
  }

  /**
   * null keys would corrupt the shared pref file and make them unreadable this is a preventive
   * measure
   *
   * @param the pref key
   */
  public void checkForNullKey(String key) {
    if (key == null) {
      throw new NullPointerException();
    }
  }

  /**
   * null keys would corrupt the shared pref file and make them unreadable this is a preventive
   * measure
   *
   * @param the pref key
   */
  public void checkForNullValue(String value) {
    if (value == null) {
      throw new NullPointerException();
    }
  }
}
