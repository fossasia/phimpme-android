package vn.mbm.phimp.me.folderchooser;

import java.util.List;

/**
 * Created by rohanagarwal94 on 1/4/17.
 */

public class AppState {
    private List<FileTree> scannedFileTree;
    private List<StorageInfo> storageInfos;

    private static AppState ourInstance = new AppState();

    public static List<StorageInfo> getStorageList() {
        return ourInstance.storageInfos;
    }

    public static void setStorageInfos(List<StorageInfo> storageInfos) {
        ourInstance.storageInfos = storageInfos;
    }

    public static List<FileTree> getScannedFileTree() {
        return ourInstance.scannedFileTree;
    }

    public static void setScannedFileTree(List<FileTree> scannedFileTree) {
        ourInstance.scannedFileTree = scannedFileTree;
    }
}
