package vn.mbm.phimp.me.folderchooser;

import java.util.List;
import java.util.Set;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import vn.mbm.phimp.me.R;

/**
 * Created by rohanagarwal94 on 31/3/17.
 */

public class FolderAdapter extends BaseAdapter {
    private final LayoutInflater inflater;
    private final IFolderChoosen iFolderChoosen;
    private final Set<String> whiteListedPath;
    private List<FileTree> fileTreeList;

    public FolderAdapter(Context context, IFolderChoosen iFolderChoosen, Set<String> whiteListedPath) {
        this.iFolderChoosen = iFolderChoosen;
        this.whiteListedPath = whiteListedPath;
        inflater = LayoutInflater.from(context);
    }

    public void updateAdapterData(List<FileTree> fileTreeList) {
        this.fileTreeList = fileTreeList;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return fileTreeList.size();
    }

    @Override
    public Object getItem(int position) {
        return fileTreeList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        FolderViewHolder folderViewHolder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.snippet_row, parent, false);
            folderViewHolder = new FolderViewHolder(iFolderChoosen, convertView);
            convertView.setTag(folderViewHolder);
        }
        else {
            folderViewHolder = (FolderViewHolder) convertView.getTag();
        }
        FileTree fileTree = (FileTree) getItem(position);
        boolean doNotShowFolder = whiteListedPath.contains(fileTree.file.toString()) || !fileTree.hasMedia;
        folderViewHolder.setContainerVisibility(!doNotShowFolder);
        if (!doNotShowFolder) {
            folderViewHolder
                    .setFile(fileTree, fileTree.displayName, fileTree.childFileTreeList.size() > 0,
                            fileTree.parent == null);
        }
        return convertView;
    }
}
