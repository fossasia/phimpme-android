package org.fossasia.phimpme.trashbin;

import java.io.File;
import java.util.ArrayList;

import org.fossasia.phimpme.R;
import org.fossasia.phimpme.base.ThemedActivity;
import org.fossasia.phimpme.data.local.TrashBinRealmModel;
import org.fossasia.phimpme.gallery.activities.SingleMediaActivity;
import org.fossasia.phimpme.gallery.data.Media;
import org.fossasia.phimpme.gallery.util.ContentHelper;
import org.fossasia.phimpme.gallery.util.StringUtils;
import org.fossasia.phimpme.utilities.SnackBarHandler;

import com.mikepenz.community_material_typeface_library.CommunityMaterial;
import com.mikepenz.iconics.IconicsDrawable;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.Uri;
import android.content.Context;
import android.media.MediaScannerConnection;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

import static org.fossasia.phimpme.utilities.ActivitySwitchHelper.context;

import static org.fossasia.phimpme.utilities.ActivitySwitchHelper.context;

public class TrashBinActivity extends ThemedActivity {

    @BindView(R.id.trashbin_recycler_view)
    public RecyclerView trashBinRecyclerView;

    @BindView(R.id.toolbar)
    public Toolbar toolbar;

    @BindView(R.id.empty_trash)
    public RelativeLayout emptyView;

    @BindView(R.id.swipeRefreshLayout_trashbin)
    protected SwipeRefreshLayout swipeRefreshLayout;

    @BindView(R.id.view_parent)
    public RelativeLayout parentView;

    private RealmQuery<TrashBinRealmModel> trashBinRealmModelRealmQuery;
    private TrashBinAdapter trashBinAdapter;
    private ArrayList<TrashBinRealmModel> deletedTrash;

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override public void onClick(View view) {
            TrashBinRealmModel trashBinRealmModel = (TrashBinRealmModel)  view.findViewById(R.id.delete_date).getTag();
            view.setTransitionName(getString(R.string.transition_photo));
            Intent intent = new Intent("com.android.camera.action.REVIEW", Uri.fromFile(new File(trashBinRealmModel.getTrashbinpath())));
            intent.putExtra("path", trashBinRealmModel.getTrashbinpath());
            intent.putExtra("position", checkpos(trashBinRealmModel.getTrashbinpath()));
            intent.putExtra("size", getTrashObjects().size());
            intent.putExtra("trashbin", true);
            ArrayList<Media> u = loaduploaddata();
            intent.putParcelableArrayListExtra("trashdatalist", u);
            intent.setClass(getApplicationContext(), SingleMediaActivity.class);
            context.startActivity(intent);
        }
    };

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trash_bin2);
        ButterKnife.bind(this);
        Realm realm = Realm.getDefaultInstance();
        trashBinRealmModelRealmQuery = realm.where(TrashBinRealmModel.class);
        ArrayList<TrashBinRealmModel> trashlist = getTrashObjects();
        if (trashlist.size() == 0) {
            emptyView.setVisibility(View.VISIBLE);
        } else {
            trashBinAdapter = new TrashBinAdapter(trashlist);
            trashBinAdapter.setOnClickListener(onClickListener);
            GridLayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), columnsCount());
            layoutManager.setReverseLayout(false);
            trashBinRecyclerView.setLayoutManager(layoutManager);
            trashBinRecyclerView.setAdapter(trashBinAdapter);
        }
        setUpUi();
    }

    private ArrayList<TrashBinRealmModel> getTrashObjects(){
        ArrayList<TrashBinRealmModel> list = new ArrayList<>();
        for(int i = 0; i < trashBinRealmModelRealmQuery.count(); i++){
            list.add(trashBinRealmModelRealmQuery.findAll().get(i));
        }
        return list;
    }

    private ArrayList<TrashBinRealmModel> getTrashObjectsLast(){
        ArrayList<TrashBinRealmModel> list = new ArrayList<>();
        for(int i = 0; i < trashBinRealmModelRealmQuery.count(); i++){
            list.add(trashBinRealmModelRealmQuery.findAll().get((int) (trashBinRealmModelRealmQuery.count() - i - 1)));
        }
        return list;
    }

    private ArrayList<Media> loaduploaddata() {
        ArrayList<Media> data = new ArrayList<>();
        ArrayList<TrashBinRealmModel> binRealmModelArrayList = getTrashObjects();
        for (int i = 0; i < binRealmModelArrayList.size(); i++) {
            data.add(new Media(new File(binRealmModelArrayList.get(i).getTrashbinpath())));
        }
        return data;
    }

    private int columnsCount() {
        return getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT
                ? 2
                : 3;
    }

    private void setUpUi() {
        parentView.setBackgroundColor(getBackgroundColor());
        setupToolbar();
        swipeRefreshLayout.setColorSchemeColors(getAccentColor());
        swipeRefreshLayout.setProgressBackgroundColorSchemeColor(getBackgroundColor());
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                trashBinAdapter.updateTrashListItems(getTrashObjects());
                if (swipeRefreshLayout.isRefreshing()) {
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        });
    }

        private int checkpos(String path){
            int pos = 0;
            ArrayList<TrashBinRealmModel> trashBinRealmModels = getTrashObjects();
            for(int i = 0; i < trashBinRealmModels.size(); i++){
                if(path.equals(trashBinRealmModels.get(i).getTrashbinpath())){
                    pos = i;
                    break;
                }
            }
            return pos;
        }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_trashbin, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){

            case R.id.trashbin_restore:
                new RestoreAll().execute();
                return true;

            case R.id.delete_action:
                return true;

                default:
                    return super.onOptionsItemSelected(item);
        }
    }

    class RestoreAll extends AsyncTask<Void, Void, Void> {
        private int count = 0, originalCount = 0;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            swipeRefreshLayout.setRefreshing(true);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            deletedTrash = new ArrayList<>();
            Realm realm = Realm.getDefaultInstance();
            RealmQuery<TrashBinRealmModel> trashBinRealmModelRealmQuery = realm.where(TrashBinRealmModel.class);
            originalCount = (int)trashBinRealmModelRealmQuery.count();
            for (int i = 0; i < originalCount; i++) {
                if(restoreImage(trashBinRealmModelRealmQuery.findAll().get(i))){
                    count ++;
                }
            }
            for(int i = 0; i < deletedTrash.size(); i++){
                removeFromRealm(deletedTrash.get(i).getTrashbinpath());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            swipeRefreshLayout.setRefreshing(false);
            trashBinAdapter.updateTrashListItems(getTrashObjects());
            if (trashBinRealmModelRealmQuery.count() == 0) {
                emptyView.setVisibility(View.VISIBLE);
                SnackBarHandler.showWithBottomMargin(parentView, String.valueOf(count) + " " +
                        getResources().getString(R.string.restore_all_success_mssg), 0, Snackbar.LENGTH_SHORT);
            } else {
                SnackBarHandler.showWithBottomMargin(parentView,  String.valueOf(count) + " " + getResources().getString(R.string.restore_all_success_mssg) +" but" +
                        String.valueOf(originalCount - count) + " " + getResources().getString(R.string.restore_all_fail_mssg), 0, Snackbar.LENGTH_SHORT);
            }
        }
    }

    private boolean restoreImage(TrashBinRealmModel trashBinRealmModel){
        boolean result = false;
        String oldpath = trashBinRealmModel.getOldpath();
        String oldFolder = oldpath.substring(0, oldpath.lastIndexOf("/"));
        if(restoreMove(getApplicationContext(), trashBinRealmModel.getTrashbinpath(), oldFolder)){
            result = true;
            scanFile(context, new String[]{ trashBinRealmModel.getTrashbinpath(), StringUtils.getPhotoPathMoved
                    (trashBinRealmModel.getTrashbinpath(),
                            oldFolder) });
            deletedTrash.add(trashBinRealmModel);
        }
        return result;
    }

    private boolean restoreMove(Context context, String source, String targetDir){
        File from = new File(source);
        File to = new File(targetDir);
        return ContentHelper.moveFile(context, from, to);
    }

    private boolean removeFromRealm(final String path){
        final boolean[] delete = {false};
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override public void execute(Realm realm) {
                RealmResults<TrashBinRealmModel> result = realm.where(TrashBinRealmModel.class).equalTo
                        ("trashbinpath", path).findAll();
                delete[0] = result.deleteAllFromRealm();
            }
        });
        return delete[0];
    }

    public void scanFile(Context context, String[] path) { MediaScannerConnection.scanFile(context, path, null, null); }

    private void setupToolbar(){
        setSupportActionBar(toolbar);
        //toolbar.setTitle("Trash Bin");
        getSupportActionBar().setTitle("Trash Bin");
        toolbar.setPopupTheme(getPopupToolbarStyle());
        toolbar.setBackgroundColor(getPrimaryColor());
        toolbar.setNavigationIcon(
                new IconicsDrawable(this)
                        .icon(CommunityMaterial.Icon.cmd_arrow_left)
                        .color(Color.WHITE)
                        .sizeDp(19));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        //toolbar.setTitle("Trash Bin");
    }
}