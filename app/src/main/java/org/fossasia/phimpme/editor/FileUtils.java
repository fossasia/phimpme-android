package org.fossasia.phimpme.editor;

import android.os.Environment;

import java.io.File;

/**
 * Documents related auxiliary class
 * 
 * @author panyi
 * 
 */
public class FileUtils {
	public static final String FOLDER_NAME = "phimpme-edit";

	/**
	 *Get storage file folder path
	 * @return
	 */
	public static File createFolders() {
		File baseDir;
		if (android.os.Build.VERSION.SDK_INT < 8) {
			baseDir = Environment.getExternalStorageDirectory();
		} else {
			baseDir = Environment
					.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
		}
		if (baseDir == null)
			return Environment.getExternalStorageDirectory();
		File aviaryFolder = new File(baseDir, FOLDER_NAME);
		if (aviaryFolder.exists())
			return aviaryFolder;
		if (aviaryFolder.isFile())
			aviaryFolder.delete();
		if (aviaryFolder.mkdirs())
			return aviaryFolder;
		return Environment.getExternalStorageDirectory();
	}

	public static File genEditFile(String extension){
		return FileUtils.getEmptyFile("phimpme"
				+ System.currentTimeMillis() + extension);
	}

	public static String getExtension(String path){
		String supportExt[] = {".jpg",".png",".jpeg",".bmp",".tiff"};
		String ext = path.substring(path.lastIndexOf(".")).toLowerCase();

		for (String itr : supportExt)
			if (itr.equals(ext))
				return (itr);

		return null;
	}

	private static File getEmptyFile(String name) {
		File folder = FileUtils.createFolders();
		if (folder != null) {
			if (folder.exists()) {
				File file = new File(folder, name);
				return file;
			}
		}
		return null;
	}
}
