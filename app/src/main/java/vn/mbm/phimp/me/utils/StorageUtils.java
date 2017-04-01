package vn.mbm.phimp.me.utils;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import vn.mbm.phimp.me.folderchooser.StorageInfo;

/**
 * Created by rohanagarwal94 on 31/3/17.
 */

public class StorageUtils {
    private static int curRemovableNumber = 1;

    public static List<StorageInfo> getStorageList(Context context) {
        List<StorageInfo> storageList = new ArrayList<>();

//		final File[] appsDir = ContextCompat.getExternalFilesDirs(context, null);
//		for (final File file : appsDir) {
//			File rootFile = file.getParentFile().getParentFile().getParentFile().getParentFile();
//			storageList.add(new StorageInfo(rootFile, getDisplayName(rootFile)));
//		}
        File rootFile = Environment.getExternalStorageDirectory();
        storageList.add(new StorageInfo(rootFile, getDisplayName(rootFile)));

        return storageList;
    }

    private static String getDisplayName(File file) {
        if (file.equals(Environment.getExternalStorageDirectory())) {
            return "Internal Memory";
        }
        else {
            return "SD card " + curRemovableNumber++;
        }
    }
}