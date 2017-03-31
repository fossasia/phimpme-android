package vn.mbm.phimp.me.folderchooser;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import vn.mbm.phimp.me.R;

/**
 * Created by rohanagarwal94 on 31/3/17.
 */

public class FolderViewHolder {
    private final TextView folderName;
    private final ImageView addFolderImage;
    private final View container;
    private FileTree fileTree;
    private boolean hasSubDirectories;

    public FolderViewHolder(final IFolderChoosen iFolderChoosen, View convertView) {
        container = convertView.findViewById(R.id.container);
        folderName = (TextView) convertView.findViewById(R.id.folder_name);
        addFolderImage = (ImageView) convertView.findViewById(R.id.add_folder);
        addFolderImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iFolderChoosen.folderChoosen(fileTree.file, true);
            }
        });
    }

    public void setFile(FileTree fileTree, String visibleName, boolean hasSubDirectories, boolean isRootLevelDir) {
        this.fileTree = fileTree;
        this.hasSubDirectories = hasSubDirectories;
        folderName.setCompoundDrawablesWithIntrinsicBounds(
                hasSubDirectories ? R.drawable.ic_folder_black_24dp : R.drawable.ic_folder_open_black_24dp, 0, 0, 0);
        folderName.setText(visibleName);
        addFolderImage.setVisibility(isRootLevelDir ? View.GONE : View.VISIBLE);
    }

    public boolean hasSubDirectories() {
        return hasSubDirectories;
    }

    public void setContainerVisibility(boolean visibile) {
        container.setVisibility(visibile ? View.VISIBLE : View.GONE);
    }
}