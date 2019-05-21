package org.fossasia.phimpme.opencamera.Camera;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.provider.MediaStore.Images.ImageColumns;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/** Provides access to the filesystem. Supports both standard and Storage Access Framework. */
public class StorageUtils {
  private static final String TAG = "StorageUtils";

  static final int MEDIA_TYPE_IMAGE = 1;

  private final Context context;
  private Uri last_media_scanned;

  // for testing:
  public volatile boolean failed_to_scan;

  StorageUtils(Context context) {
    this.context = context;
  }

  Uri getLastMediaScanned() {
    return last_media_scanned;
  }

  void clearLastMediaScanned() {
    last_media_scanned = null;
  }

  /**
   * Sends the intents to announce the new file to other Android applications. E.g., cloud storage
   * applications like OwnCloud use this to listen for new photos/videos to automatically upload.
   * Note that on Android 7 onwards, these broadcasts are deprecated and won't have any effect -
   * see: https://developer.android.com/reference/android/hardware/Camera.html#ACTION_NEW_PICTURE
   * Listeners like OwnCloud should instead be using
   * https://developer.android.com/reference/android/app/job/JobInfo.Builder.html#addTriggerContentUri(android.app.job.JobInfo.TriggerContentUri)
   * See https://github.com/owncloud/android/issues/1675 for OwnCloud's discussion on this.
   */
  void announceUri(Uri uri, boolean is_new_picture) {
    if (MyDebug.LOG) Log.d(TAG, "announceUri: " + uri);
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
      if (MyDebug.LOG) Log.d(TAG, "broadcasts deprecated on Android 7 onwards, so don't send them");
      // see note above; the intents won't be delivered, so might as well save the trouble of trying
      // to send them
    } else if (is_new_picture) {
      // note, we reference the string directly rather than via Camera.ACTION_NEW_PICTURE, as the
      // latter class is now deprecated - but we still need to broadcast the string for other apps
      context.sendBroadcast(new Intent("android.hardware.action.NEW_PICTURE", uri));
      // for compatibility with some apps - apparently this is what used to be broadcast on Android?
      context.sendBroadcast(new Intent("com.android.camera.NEW_PICTURE", uri));

      if (MyDebug.LOG) // this code only used for debugging/logging
      {
        String[] CONTENT_PROJECTION = {
          Images.Media.DATA,
          Images.Media.DISPLAY_NAME,
          Images.Media.MIME_TYPE,
          Images.Media.SIZE,
          Images.Media.DATE_TAKEN,
          Images.Media.DATE_ADDED
        };
        Cursor c = context.getContentResolver().query(uri, CONTENT_PROJECTION, null, null, null);
        if (c == null) {
          if (MyDebug.LOG) Log.e(TAG, "Couldn't resolve given uri [1]: " + uri);
        } else if (!c.moveToFirst()) {
          if (MyDebug.LOG) Log.e(TAG, "Couldn't resolve given uri [2]: " + uri);
        } else {
          String file_path = c.getString(c.getColumnIndex(Images.Media.DATA));
          String file_name = c.getString(c.getColumnIndex(Images.Media.DISPLAY_NAME));
          String mime_type = c.getString(c.getColumnIndex(Images.Media.MIME_TYPE));
          long date_taken = c.getLong(c.getColumnIndex(Images.Media.DATE_TAKEN));
          long date_added = c.getLong(c.getColumnIndex(Images.Media.DATE_ADDED));
          Log.d(TAG, "file_path: " + file_path);
          Log.d(TAG, "file_name: " + file_name);
          Log.d(TAG, "mime_type: " + mime_type);
          Log.d(TAG, "date_taken: " + date_taken);
          Log.d(TAG, "date_added: " + date_added);
          c.close();
        }
      }
      /*{
      	// hack: problem on Camera2 API (at least on Nexus 6) that if geotagging is enabled, then the resultant image has incorrect Exif TAG_GPS_DATESTAMP (GPSDateStamp) set (tends to be around 2038 - possibly a driver bug of casting long to int?)
      	// whilst we don't yet correct for that bug, the more immediate problem is that it also messes up the DATE_TAKEN field in the media store, which messes up Gallery apps
      	// so for now, we correct it based on the DATE_ADDED value.
      	        String[] CONTENT_PROJECTION = { Images.Media.DATE_ADDED };
      	        Cursor c = context.getContentResolver().query(uri, CONTENT_PROJECTION, null, null, null);
      	        if( c == null ) {
      		if( MyDebug.LOG )
      			Log.e(TAG, "Couldn't resolve given uri [1]: " + uri);
      	        }
      	        else if( !c.moveToFirst() ) {
      		if( MyDebug.LOG )
      			Log.e(TAG, "Couldn't resolve given uri [2]: " + uri);
      	        }
      	        else {
          	        long date_added = c.getLong(c.getColumnIndex(Images.Media.DATE_ADDED));
      		if( MyDebug.LOG )
      			Log.e(TAG, "replace date_taken with date_added: " + date_added);
      	ContentValues values = new ContentValues();
      	values.put(Images.Media.DATE_TAKEN, date_added*1000);
      	context.getContentResolver().update(uri, values, null, null);
          	        c.close();
      	        }
      }*/
    }
  }

  /*public Uri broadcastFileRaw(File file, Date current_date, Location location) {
  	if( MyDebug.LOG )
  		Log.d(TAG, "broadcastFileRaw: " + file.getAbsolutePath());
         ContentValues values = new ContentValues();
         values.put(ImageColumns.TITLE, file.getName().substring(0, file.getName().lastIndexOf(".")));
         values.put(ImageColumns.DISPLAY_NAME, file.getName());
         values.put(ImageColumns.DATE_TAKEN, current_date.getTime());
         values.put(ImageColumns.MIME_TYPE, "image/dng");
         //values.put(ImageColumns.MIME_TYPE, "image/jpeg");
         if( location != null ) {
             values.put(ImageColumns.LATITUDE, location.getLatitude());
             values.put(ImageColumns.LONGITUDE, location.getLongitude());
         }
         // leave ORIENTATION for now - this doesn't seem to get inserted for JPEGs anyway (via MediaScannerConnection.scanFile())
         values.put(ImageColumns.DATA, file.getAbsolutePath());
         //values.put(ImageColumns.DATA, "/storage/emulated/0/DCIM/OpenCamera/blah.dng");
         Uri uri = null;
         try {
     		uri = context.getContentResolver().insert(Images.Media.EXTERNAL_CONTENT_URI, values);
  			if( MyDebug.LOG )
  				Log.d(TAG, "inserted media uri: " + uri);
     		context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));
         }
         catch (Throwable th) {
          // This can happen when the external volume is already mounted, but
          // MediaScanner has not notify MediaProvider to add that volume.
          // The picture is still safe and MediaScanner will find it and
          // insert it into MediaProvider. The only problem is that the user
          // cannot click the thumbnail to review the picture.
          Log.e(TAG, "Failed to write MediaStore" + th);
      }
         return uri;
  }*/

  /**
   * Sends a "broadcast" for the new file. This is necessary so that Android recognises the new file
   * without needing a reboot: - So that they show up when connected to a PC using MTP. - For JPEGs,
   * so that they show up in gallery applications. - This also calls announceUri() on the resultant
   * Uri for the new file. - Note this should also be called after deleting a file. - Note that for
   * DNG files, MediaScannerConnection.scanFile() doesn't result in the files being shown in gallery
   * applications. This may well be intentional, since most gallery applications won't read DNG
   * files anyway. But it's still important to call this function for DNGs, so that they show up on
   * MTP.
   */
  public void broadcastFile(
      final File file, final boolean is_new_picture, final boolean set_last_scanned) {
    if (MyDebug.LOG) Log.d(TAG, "broadcastFile: " + file.getAbsolutePath());
    // note that the new method means that the new folder shows up as a file when connected to a PC
    // via MTP (at least tested on Windows 8)
    if (file.isDirectory()) {
      // this.sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.fromFile(file)));
      // ACTION_MEDIA_MOUNTED no longer allowed on Android 4.4! Gives: SecurityException: Permission
      // Denial: not allowed to send broadcast android.intent.action.MEDIA_MOUNTED
      // note that we don't actually need to broadcast anything, the folder and contents appear
      // straight away (both in Gallery on device, and on a PC when connecting via MTP)
      // also note that we definitely don't want to broadcast ACTION_MEDIA_SCANNER_SCAN_FILE or use
      // scanFile() for folders, as this means the folder shows up as a file on a PC via MTP (and
      // isn't fixed by rebooting!)
    } else {
      // both of these work fine, but using MediaScannerConnection.scanFile() seems to be preferred
      // over sending an intent
      // context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
      // Uri.fromFile(file)));
      failed_to_scan = true; // set to true until scanned okay
      if (MyDebug.LOG) Log.d(TAG, "failed_to_scan set to true");
      MediaScannerConnection.scanFile(
          context,
          new String[] {file.getAbsolutePath()},
          null,
          new MediaScannerConnection.OnScanCompletedListener() {
            public void onScanCompleted(String path, Uri uri) {
              failed_to_scan = false;
              if (MyDebug.LOG) {
                Log.d(TAG, "Scanned " + path + ":");
                Log.d(TAG, "-> uri=" + uri);
              }
              if (set_last_scanned) {
                last_media_scanned = uri;
                if (MyDebug.LOG) Log.d(TAG, "set last_media_scanned to " + last_media_scanned);
              }
              announceUri(uri, is_new_picture);
            }
          });
    }
  }

  boolean isUsingSAF() {
    // check Android version just to be safe
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
      if (sharedPreferences.getBoolean(PreferenceKeys.getUsingSAFPreferenceKey(), false)) {
        return true;
      }
    }
    return false;
  }

  // only valid if !isUsingSAF()
  String getSaveLocation() {
    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    return sharedPreferences.getString(
        PreferenceKeys.getSaveLocationPreferenceKey(), "Phimpme Camera");
  }

  // only valid if isUsingSAF()
  String getSaveLocationSAF() {
    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    return sharedPreferences.getString(PreferenceKeys.getSaveLocationSAFPreferenceKey(), "");
  }

  // only valid if isUsingSAF()
  private Uri getTreeUriSAF() {
    String folder_name = getSaveLocationSAF();
    return Uri.parse(folder_name);
  }

  // valid if whether or not isUsingSAF()
  // but note that if isUsingSAF(), this may return null - it can't be assumed that there is a File
  // corresponding to the SAF Uri
  @TargetApi(Build.VERSION_CODES.LOLLIPOP)
  File getImageFolder() {
    File file;
    if (isUsingSAF()) {
      Uri uri = getTreeUriSAF();
      /*if( MyDebug.LOG )
      Log.d(TAG, "uri: " + uri);*/
      file = getFileFromDocumentUriSAF(uri, true);
    } else {
      String folder_name = getSaveLocation();
      file = getImageFolder(folder_name);
    }
    return file;
  }

  public static File getBaseFolder() {
    return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
  }

  // only valid if !isUsingSAF()
  public static File getImageFolder(String folder_name) {
    File file;
    if (folder_name.length() > 0 && folder_name.lastIndexOf('/') == folder_name.length() - 1) {
      // ignore final '/' character
      folder_name = folder_name.substring(0, folder_name.length() - 1);
    }
    // if( folder_name.contains("/") ) {
    if (folder_name.startsWith("/")) {
      file = new File(folder_name);
    } else {
      file = new File(getBaseFolder(), folder_name);
    }
    return file;
  }

  // only valid if isUsingSAF()
  // This function should only be used as a last resort - we shouldn't generally assume that a Uri
  // represents an actual File, and instead.
  // However this is needed for a workaround to the fact that deleting a document file doesn't
  // remove it from MediaStore.
  // See:
  // http://stackoverflow.com/questions/21605493/storage-access-framework-does-not-update-mediascanner-mtp
  // http://stackoverflow.com/questions/20067508/get-real-path-from-uri-android-kitkat-new-storage-access-framework/
  // only valid if isUsingSAF()
  @TargetApi(Build.VERSION_CODES.LOLLIPOP)
  File getFileFromDocumentUriSAF(Uri uri, boolean is_folder) {
    if (MyDebug.LOG) {
      Log.d(TAG, "getFileFromDocumentUriSAF: " + uri);
      Log.d(TAG, "is_folder?: " + is_folder);
    }
    File file = null;
    if ("com.android.externalstorage.documents".equals(uri.getAuthority())) {
      final String id =
          is_folder
              ? DocumentsContract.getTreeDocumentId(uri)
              : DocumentsContract.getDocumentId(uri);
      if (MyDebug.LOG) Log.d(TAG, "id: " + id);
      String[] split = id.split(":");
      if (split.length >= 2) {
        String type = split[0];
        String path = split[1];
        /*if( MyDebug.LOG ) {
        	Log.d(TAG, "type: " + type);
        	Log.d(TAG, "path: " + path);
        }*/
        File[] storagePoints = new File("/storage").listFiles();

        if ("primary".equalsIgnoreCase(type)) {
          final File externalStorage = Environment.getExternalStorageDirectory();
          file = new File(externalStorage, path);
        }
        for (int i = 0; storagePoints != null && i < storagePoints.length && file == null; i++) {
          File externalFile = new File(storagePoints[i], path);
          if (externalFile.exists()) {
            file = externalFile;
          }
        }
        if (file == null) {
          // just in case?
          file = new File(path);
        }
      }
    } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
      final String id = DocumentsContract.getDocumentId(uri);
      final Uri contentUri =
          ContentUris.withAppendedId(
              Uri.parse("content://downloads/public_downloads"), Long.parseLong(id));

      String filename = getDataColumn(contentUri, null, null);
      if (filename != null) file = new File(filename);
    } else if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
      final String docId = DocumentsContract.getDocumentId(uri);
      final String[] split = docId.split(":");
      final String type = split[0];

      Uri contentUri = null;
      if ("image".equals(type)) {
        contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
      }

      final String selection = "_id=?";
      final String[] selectionArgs = new String[] {split[1]};

      String filename = getDataColumn(contentUri, selection, selectionArgs);
      if (filename != null) file = new File(filename);
    }

    if (MyDebug.LOG) {
      if (file != null) Log.d(TAG, "file: " + file.getAbsolutePath());
      else Log.d(TAG, "failed to find file");
    }
    return file;
  }

  private String getDataColumn(Uri uri, String selection, String[] selectionArgs) {
    final String column = "_data";
    final String[] projection = {column};

    Cursor cursor = null;
    try {
      cursor =
          this.context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
      if (cursor != null && cursor.moveToFirst()) {
        final int column_index = cursor.getColumnIndexOrThrow(column);
        return cursor.getString(column_index);
      }
    } finally {
      if (cursor != null) cursor.close();
    }
    return null;
  }

  private String createMediaFilename(
      int type, String suffix, int count, String extension, Date current_date) {
    String index = "";
    if (count > 0) {
      index = "_" + count; // try to find a unique filename
    }
    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    boolean useZuluTime =
        sharedPreferences
            .getString(PreferenceKeys.getSaveZuluTimePreferenceKey(), "local")
            .equals("zulu");
    String timeStamp;
    if (useZuluTime) {
      SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd_HHmmss'Z'", Locale.US);
      fmt.setTimeZone(TimeZone.getTimeZone("UTC"));
      timeStamp = fmt.format(current_date);
    } else {
      timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(current_date);
    }
    String mediaFilename;
    if (type == MEDIA_TYPE_IMAGE) {
      String prefix =
          sharedPreferences.getString(PreferenceKeys.getSavePhotoPrefixPreferenceKey(), "IMG_");
      mediaFilename = prefix + timeStamp + suffix + index + "." + extension;
    } else {
      // throw exception as this is a programming error
      if (MyDebug.LOG) Log.e(TAG, "unknown type: " + type);
      throw new RuntimeException();
    }
    return mediaFilename;
  }

  // only valid if !isUsingSAF()
  @SuppressLint("SimpleDateFormat")
  File createOutputMediaFile(int type, String suffix, String extension, Date current_date)
      throws IOException {
    File mediaStorageDir = getImageFolder();

    // Create the storage directory if it does not exist
    if (!mediaStorageDir.exists()) {
      if (!mediaStorageDir.mkdirs()) {
        if (MyDebug.LOG) Log.e(TAG, "failed to create directory");
        throw new IOException();
      }
      broadcastFile(mediaStorageDir, false, false);
    }

    // Create a media file name
    File mediaFile = null;
    for (int count = 0; count < 100; count++) {
      String mediaFilename = createMediaFilename(type, suffix, count, extension, current_date);
      mediaFile = new File(mediaStorageDir.getPath() + File.separator + mediaFilename);
      if (!mediaFile.exists()) {
        break;
      }
    }

    if (MyDebug.LOG) {
      Log.d(TAG, "getOutputMediaFile returns: " + mediaFile);
    }
    if (mediaFile == null) throw new IOException();
    return mediaFile;
  }

  // only valid if isUsingSAF()
  @TargetApi(Build.VERSION_CODES.LOLLIPOP)
  Uri createOutputFileSAF(String filename, String mimeType) throws IOException {
    try {
      Uri treeUri = getTreeUriSAF();
      if (MyDebug.LOG) Log.d(TAG, "treeUri: " + treeUri);
      Uri docUri =
          DocumentsContract.buildDocumentUriUsingTree(
              treeUri, DocumentsContract.getTreeDocumentId(treeUri));
      if (MyDebug.LOG) Log.d(TAG, "docUri: " + docUri);
      // note that DocumentsContract.createDocument will automatically append to the filename if it
      // already exists
      Uri fileUri =
          DocumentsContract.createDocument(
              context.getContentResolver(), docUri, mimeType, filename);
      if (MyDebug.LOG) Log.d(TAG, "returned fileUri: " + fileUri);
      if (fileUri == null) throw new IOException();
      return fileUri;
    } catch (IllegalArgumentException e) {
      // DocumentsContract.getTreeDocumentId throws this if URI is invalid
      if (MyDebug.LOG) Log.e(TAG, "createOutputMediaFileSAF failed");
      e.printStackTrace();
      throw new IOException();
    }
  }

  // only valid if isUsingSAF()
  @TargetApi(Build.VERSION_CODES.LOLLIPOP)
  Uri createOutputMediaFileSAF(int type, String suffix, String extension, Date current_date)
      throws IOException {
    String mimeType;
    if (type == MEDIA_TYPE_IMAGE) {
      if (extension.equals("dng")) {
        mimeType = "image/dng";
        // mimeType = "image/x-adobe-dng";
      } else mimeType = "image/jpeg";
    } else {
      // throw exception as this is a programming error
      if (MyDebug.LOG) Log.e(TAG, "unknown type: " + type);
      throw new RuntimeException();
    }
    // note that DocumentsContract.createDocument will automatically append to the filename if it
    // already exists
    String mediaFilename = createMediaFilename(type, suffix, 0, extension, current_date);
    return createOutputFileSAF(mediaFilename, mimeType);
  }

  static class Media {
    final long id;
    final Uri uri;
    final long date;
    final int orientation;

    Media(long id, Uri uri, long date, int orientation) {
      this.id = id;
      this.uri = uri;
      this.date = date;
      this.orientation = orientation;
    }
  }

  public Media getLatestMedia() {
    if (MyDebug.LOG) Log.d(TAG, "getLatestMedia: " + ("images"));
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
        && ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) {
      // needed for Android 6, in case users deny storage permission, otherwise we get
      // java.lang.SecurityException from ContentResolver.query()
      // see https://developer.android.com/training/permissions/requesting.html
      // we now request storage permission before opening the camera, but keep this here just in
      // case
      // we restrict check to Android 6 or later just in case, see note in
      // LocationSupplier.setupLocationListener()
      if (MyDebug.LOG) Log.e(TAG, "don't have READ_EXTERNAL_STORAGE permission");
      return null;
    }
    Media media = null;
    Uri baseUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
    // Uri query = baseUri.buildUpon().appendQueryParameter("limit", "1").build();
    Uri query = baseUri;
    final int column_id_c = 0;
    final int column_date_taken_c = 1;
    final int column_data_c = 2;
    final int column_orientation_c = 3;
    String[] projection =
        new String[] {
          ImageColumns._ID, ImageColumns.DATE_TAKEN, ImageColumns.DATA, ImageColumns.ORIENTATION
        };
    String selection = ImageColumns.MIME_TYPE + "='image/jpeg'";
    String order = ImageColumns.DATE_TAKEN + " DESC," + ImageColumns._ID + " DESC";
    Cursor cursor = null;
    try {
      cursor = context.getContentResolver().query(query, projection, selection, null, order);
      if (cursor != null && cursor.moveToFirst()) {
        if (MyDebug.LOG) Log.d(TAG, "found: " + cursor.getCount());
        // now sorted in order of date - scan to most recent one in the Open Camera save folder
        boolean found = false;
        File save_folder = getImageFolder(); // may be null if using SAF
        String save_folder_string =
            save_folder == null ? null : save_folder.getAbsolutePath() + File.separator;
        if (MyDebug.LOG) Log.d(TAG, "save_folder_string: " + save_folder_string);
        do {
          String path = cursor.getString(column_data_c);
          if (MyDebug.LOG) Log.d(TAG, "path: " + path);
          // path may be null on Android 4.4!:
          // http://stackoverflow.com/questions/3401579/get-filename-and-path-from-uri-from-mediastore
          if (save_folder_string == null || (path != null && path.contains(save_folder_string))) {
            if (MyDebug.LOG) Log.d(TAG, "found most recent in Open Camera folder");
            // we filter files with dates in future, in case there exists an image in the folder
            // with incorrect datestamp set to the future
            // we allow up to 2 days in future, to avoid risk of issues to do with timezone etc
            long date = cursor.getLong(column_date_taken_c);
            long current_time = System.currentTimeMillis();
            if (date > current_time + 172800000) {
              if (MyDebug.LOG) Log.d(TAG, "skip date in the future!");
            } else {
              found = true;
              break;
            }
          }
        } while (cursor.moveToNext());
        if (!found) {
          if (MyDebug.LOG)
            Log.d(TAG, "can't find suitable in Open Camera folder, so just go with most recent");
          cursor.moveToFirst();
        }
        long id = cursor.getLong(column_id_c);
        long date = cursor.getLong(column_date_taken_c);
        int orientation = cursor.getInt(column_orientation_c);
        Uri uri = ContentUris.withAppendedId(baseUri, id);
        if (MyDebug.LOG) Log.d(TAG, "found most recent uri for " + ("images") + ": " + uri);
        media = new Media(id, uri, date, orientation);
      }
    } catch (SQLiteException e) {
      // had this reported on Google Play from getContentResolver().query() call
      if (MyDebug.LOG) Log.e(TAG, "SQLiteException trying to find latest media");
      e.printStackTrace();
    } finally {
      if (cursor != null) {
        cursor.close();
      }
    }
    return media;
  }
  /*
    Media getLatestMedia() {
  Media image_media = getLatestMedia(false);
  Media media = null;
  if( image_media != null) {
  	if( MyDebug.LOG )
  		Log.d(TAG, "only found images");
  	media = image_media;
  }
  if( MyDebug.LOG )
  	Log.d(TAG, "return latest media: " + media);
  return media;
    }*/
}
