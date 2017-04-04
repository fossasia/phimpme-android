package vn.mbm.phimp.me.folderchooser;

import java.util.Set;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import vn.mbm.phimp.me.R;

/**
 * Created by rohanagarwal94 on 31/3/17.
 */

public class FolderChooser extends ListView implements AdapterView.OnItemClickListener {
    private FolderAdapter adapter;

    public FolderChooser(Context context) {
        super(context);
        setup();
    }

    public FolderChooser(Context context, AttributeSet attrs) {
        super(context, attrs);
        setup();
    }

    private void setup() {
        View headerView = View.inflate(getContext(), R.layout.folder_header_view, null);
        headerView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                folderUp();
            }
        });
        addHeaderView(headerView);
        setOnItemClickListener(this);
    }


    public void showChooser(Set<String> whiteListedPath, IFolderChoosen iFolderChoosen) {
        adapter = new FolderAdapter(getContext(), iFolderChoosen, whiteListedPath);
        adapter.updateAdapterData(AppState.getScannedFileTree());
        setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    public void folderUp() {
        if (adapter.getCount() == 0) {
            adapter.updateAdapterData(AppState.getScannedFileTree());
            return;
        }
        FileTree parentFile = ((FileTree) adapter.getItem(0)).parent;
        if (parentFile != null) {
            if (parentFile.parent != null) {
                adapter.updateAdapterData(parentFile.parent.childFileTreeList);
            }
            else {
                adapter.updateAdapterData(AppState.getScannedFileTree());
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        FolderViewHolder folderViewHolder = (FolderViewHolder) view.getTag();
        if (folderViewHolder.hasSubDirectories()) {
            adapter.updateAdapterData(((FileTree) adapter.getItem(position - 1)).childFileTreeList);
        }
    }
}
