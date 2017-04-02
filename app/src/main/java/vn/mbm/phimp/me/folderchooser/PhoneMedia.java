package vn.mbm.phimp.me.folderchooser;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import android.media.ExifInterface;
import android.os.Environment;
import android.util.Log;

import vn.mbm.phimp.me.BuildConfig;
import vn.mbm.phimp.me.utils.Constants;
import vn.mbm.phimp.me.utils.FolderChooserPrefSettings;
import vn.mbm.phimp.me.utils.Utils;

/**
 * Created by rohanagarwal94 on 1/4/17.
 */

public class PhoneMedia {
    private static final Set<String> whitelistedPaths = FolderChooserPrefSettings.getInstance().getWhitelistedPaths();

    private PhoneMedia() {
    }

    public static List<File> getUnanalysedFiles() {
        List<File> files = Collections.synchronizedList(new ArrayList<File>());
        long currentTimeMillis = System.currentTimeMillis();
        List<FileTree> fileTreeList = new ArrayList<>();
        for (StorageInfo storageInfo : AppState.getStorageList()) {
            FileTree fileTree = new FileTree(storageInfo.file, storageInfo.displayName, null);
            fileTreeList.add(fileTree);
            walkDir(fileTree, files);
        }
        currentTimeMillis = (System.currentTimeMillis() - currentTimeMillis) / 1000;
        Log.i("Files loading time = ", String.valueOf(currentTimeMillis));
        sortFiles(files);
        if (BuildConfig.DEBUG) {
            saveFile(files);
        }
        AppState.setScannedFileTree(fileTreeList);
        return files;
    }

    private static void saveFile(List<File> files) {
        try {
            File myFile = new File(Environment.getExternalStorageDirectory(), "mysdfile.txt");
            myFile.createNewFile();
            FileOutputStream fOut = new FileOutputStream(myFile);
            OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
            for (File file : files) {
                myOutWriter.append(file.getPath());
                myOutWriter.append("\n");
            }
            myOutWriter.close();
            fOut.close();
        }
        catch (Exception e) {
            Log.e("Exception", String.valueOf(e));
        }
    }

    public static void walkDir(FileTree fileTree, List<File> files) {
        File listFile[] = fileTree.file.listFiles();
        int max_filesize_download = FolderChooserPrefSettings.getInstance().getMaxFileSize();


        if (listFile != null) {
            for (File file : listFile) {
                if (file.isDirectory()) {
                    FileTree childFileTree = new FileTree(file, file.getName(), fileTree);
                    fileTree.childFileTreeList.add(childFileTree);
                    if (!isFolderWhiteListed(file)) {
                        walkDir(childFileTree, files);
                    }
                }
                else {
                    FileMediaType fileMediaType = getFileMediaType(file.getName());

                    if((fileMediaType == FileMediaType.IMAGE
                            && file.length() <= max_filesize_download*1000*1000
                            && file.length() > Constants.MIN_FILE_SIZE_ALLOWED))
                        fileTree.hasMedia();
                        files.add(file);

                }
            }
        }
    }

    private static boolean isFolderWhiteListed(File file) {
        if (whitelistedPaths.contains(file.toString())) {
            return true;
        }
        return false;
    }

    private static void sortFiles(List<File> files) {
        Collections.sort(files, new Comparator<File>() {
            public int compare(File f1, File f2) {
                return Utils.compare(f1.lastModified(), f2.lastModified());
            }
        });
    }

    private static FileMediaType getFileMediaType(String name) {
        for (MediaExtension mediaExtension : MediaExtension.getList()) {
            if (name.endsWith(mediaExtension.extension)) {
                return FileMediaType.IMAGE;
            }
        }
        return FileMediaType.OTHER;
    }

    public static boolean isImageCapturedFromCamera(File file) {
        try {
            ExifInterface exif = new ExifInterface(file.toString());
            String cameraMake = exif.getAttribute(ExifInterface.TAG_MAKE);
            String cameraModel = exif.getAttribute(ExifInterface.TAG_MAKE);
            if ((cameraMake != null && !cameraMake.isEmpty()) ||
                    (cameraModel != null && !cameraModel.isEmpty())) {
                return true;
            }
        }
        catch (Exception e) {
            Log.d("Exception", String.valueOf(e));
        }
        return false;
    }

    private enum FileMediaType {
        IMAGE,
        OTHER
    }
}