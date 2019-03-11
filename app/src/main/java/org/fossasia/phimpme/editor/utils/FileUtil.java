package org.fossasia.phimpme.editor.utils;

import android.content.ContentValues;
import android.content.Context;
import android.provider.MediaStore;
import android.text.TextUtils;

import org.fossasia.phimpme.R;

import java.io.File;

/**
 * Created by panyi on 16/10/23.
 */
public class FileUtil {
    public static boolean checkFileExist(final String path) {
        if (TextUtils.isEmpty(path))
            return false;

        File file = new File(path);
        return file.exists();
    }

    // Get file extension
    public static String getExtensionName(String filename) {
        if ((filename != null) && (filename.length() > 0)) {
            int dot = filename.lastIndexOf('.');
            if ((dot > -1) && (dot < (filename.length() - 1))) {
                return filename.substring(dot + 1);
            }
        }
        return "";
    }

    /**
     * The image file is added to an album
     * @param context
     * @param dstPath
     */
    public static void albumUpdate(final Context context, final String dstPath) {
        if (TextUtils.isEmpty(dstPath) || context == null)
            return;

        ContentValues values = new ContentValues(2);
        String extensionName = getExtensionName(dstPath);
        values.put(MediaStore.Images.Media.MIME_TYPE, R.string.image + (TextUtils.isEmpty(extensionName) ? "jpeg" : extensionName));
        values.put(MediaStore.Images.Media.DATA, dstPath);
        context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
    }
}
