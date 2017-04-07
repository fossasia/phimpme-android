package vn.mbm.phimp.me.folderchooser;

import java.io.File;

/**
 * Created by rohanagarwal94 on 31/3/17.
 */

public class StorageInfo {
    public final File file;
    public String displayName;

    public StorageInfo(File file, String displayName) {
        this.file = file;
        this.displayName = displayName;
    }
}
