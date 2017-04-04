package vn.mbm.phimp.me.folderchooser;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import vn.mbm.phimp.me.R;

/**
 * Created by rohanagarwal94 on 31/3/17.
 */

public class WhitelistedFolderListAdapter extends BaseAdapter {
    private final List<String> whitelistedPaths;
    private final LayoutInflater inflater;
    private final IFolderChoosen iFolderChoosen;

    public WhitelistedFolderListAdapter(Context context, Set<String> stringSet, IFolderChoosen iFolderChoosen) {
        this.iFolderChoosen = iFolderChoosen;
        inflater = LayoutInflater.from(context);
        whitelistedPaths = new ArrayList<>(stringSet);
    }

    public void addOrRemoveFile(File file, boolean isFileAdded) {
        if (isFileAdded) {
            whitelistedPaths.add(file.toString());
        }
        else {
            whitelistedPaths.remove(file.toString());
        }
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return whitelistedPaths.size();
    }

    @Override
    public Object getItem(int position) {
        return whitelistedPaths.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.snippet_row, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.setFile((String) getItem(position));
        return convertView;
    }

    private class ViewHolder implements View.OnClickListener {
        private android.widget.TextView folderName;
        private ImageView folderIcon;
        private File file;

        public ViewHolder(View convertView) {
            folderName = (android.widget.TextView) convertView.findViewById(R.id.folder_name);
            folderIcon = (ImageView) convertView.findViewById(R.id.add_folder);
            folderIcon.setOnClickListener(ViewHolder.this);

            folderIcon.setImageResource(R.drawable.ic_remove_circle_black_24dp);
        }

        public void setFile(String item) {
            file = new File(item);
            folderName.setText(file.getName());
        }

        @Override
        public void onClick(View v) {
            iFolderChoosen.folderChoosen(file, false);
        }
    }
}
