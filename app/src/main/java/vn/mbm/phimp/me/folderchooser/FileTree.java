package vn.mbm.phimp.me.folderchooser;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by rohanagarwal94 on 31/3/17.
 */

public class FileTree {
    public final File file;
    public final String displayName;
    public final List<FileTree> childFileTreeList = new ArrayList<>();
    public final FileTree parent;
    public boolean hasMedia = false;

    public FileTree(File file, String displayName, FileTree parent) {
        this.file = file;
        this.displayName = displayName;
        this.parent = parent;
    }

    public void hasMedia() {
        hasMedia = true;
        if (parent != null) {
            parent.hasMedia();
        }
    }
}