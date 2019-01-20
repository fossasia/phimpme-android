package org.fossasia.phimpme.editor;

import android.os.Environment;
import java.io.File;

/** Created by JASPREET SINGH on 31-12-2017. */
public class FileUtilsCompress {

  public static final String FOLDER_NAME = "phimpme-compress";

  /**
   * Get storage file folder path
   *
   * @return
   */
  public static File createFolders() {
    File baseDir;
    if (android.os.Build.VERSION.SDK_INT < 8) {
      baseDir = Environment.getExternalStorageDirectory();
    } else {
      baseDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
    }
    if (baseDir == null) return Environment.getExternalStorageDirectory();
    File aviaryFolder = new File(baseDir, FOLDER_NAME);
    if (aviaryFolder.exists()) return aviaryFolder;
    if (aviaryFolder.isFile()) aviaryFolder.delete();
    if (aviaryFolder.mkdirs()) return aviaryFolder;
    return Environment.getExternalStorageDirectory();
  }
}
