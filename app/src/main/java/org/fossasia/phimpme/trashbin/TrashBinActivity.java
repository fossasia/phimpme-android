package org.fossasia.phimpme.trashbin;

import java.io.File;
import java.util.ArrayList;

import org.fossasia.phimpme.R;
import org.fossasia.phimpme.base.ThemedActivity;
import org.fossasia.phimpme.data.local.TrashBinRealmModel;
import org.fossasia.phimpme.gallery.activities.SingleMediaActivity;
import org.fossasia.phimpme.gallery.data.Media;
import org.fossasia.phimpme.gallery.util.ThemeHelper;
import org.fossasia.phimpme.gallery.util.AlertDialogsHelper;
import org.fossasia.phimpme.gallery.util.SecurityHelper;
import org.fossasia.phimpme.gallery.util.ContentHelper;
import org.fossasia.phimpme.gallery.util.StringUtils;
import org.fossasia.phimpme.utilities.BasicCallBack;
import org.fossasia.phimpme.utilities.SnackBarHandler;

import com.mikepenz.community_material_typeface_library.CommunityMaterial;
import com.mikepenz.iconics.IconicsDrawable;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.Uri;
import android.content.DialogInterface;
import android.os.Environment;
import android.content.Context;
import android.media.MediaScannerConnection;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

import static org.fossasia.phimpme.utilities.ActivitySwitchHelper.context;

public class TrashBinActivity extends ThemedActivity {

    @BindView(R.id.trashbin_recycler_view)
    public RecyclerView trashBinRecyclerView;

    @BindView(R.id.toolbar)
    public Toolbar toolbar;

    @BindView(R.id.empty_trash)
    public RelativeLayout emptyView;

    @BindView(R.id.image_trash)
    public ImageView emptyIcon;

    @BindView(R.id.trash_text)
    public TextView trashText;

    @BindView(R.id.trash_message)
    public TextView trashMessage;

    @BindView(R.id.swipeRefreshLayout_trashbin)
    protected SwipeRefreshLayout swipeRefreshLayout;

    @BindView(R.id.view_parent)
    public RelativeLayout parentView;

    private RealmQuery<TrashBinRealmModel> trashBinRealmModelRealmQuery;
    private TrashBinAdapter trashBinAdapter;
    private SecurityHelper securityObj;
    private ArrayList<TrashBinRealmModel> deletedTrash;

    private BasicCallBack basicCallBack = new BasicCallBack() {
        @Override
        public void callBack(int status, Object data) {
            if(status == 2){
                emptyView.setVisibility(View.VISIBLE);
                trashEmptyViewSetup();
                invalidateOptionsMenu();
            }
        }
    };

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
        securityObj = new SecurityHelper(TrashBinActivity.this);
        trashBinRealmModelRealmQuery = realm.where(TrashBinRealmModel.class);
        emptyIcon.setColorFilter(getPrimaryColor());
        ArrayList<TrashBinRealmModel> trashlist = getTrashObjects();
        trashBinAdapter = new TrashBinAdapter(trashlist, basicCallBack);
        if (trashlist.size() == 0) {
            emptyView.setVisibility(View.VISIBLE);
            trashEmptyViewSetup();
        } else {
            trashBinAdapter.setOnClickListener(onClickListener);
            GridLayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), columnsCount());
            layoutManager.setReverseLayout(false);
            trashBinRecyclerView.setLayoutManager(layoutManager);
            trashBinRecyclerView.setAdapter(trashBinAdapter);
        }
        setUpUi();
    }

    private void trashEmptyViewSetup(){
        if(ThemeHelper.getBaseTheme(getApplicationContext()) == ThemeHelper.DARK_THEME ||
                ThemeHelper.getBaseTheme(getApplicationContext()) == ThemeHelper.AMOLED_THEME){
            emptyIcon.setImageResource(R.drawable.ic_delete_sweep_white_24dp);
        } else {
            emptyIcon.setImageResource(R.drawable.ic_delete_sweep_black_24dp);
        }
        trashText.setTextColor(getTextColor());
        trashMessage.setTextColor(getTextColor());
    }

    private ArrayList<TrashBinRealmModel> getTrashObjects(){
        ArrayList<TrashBinRealmModel> list = new ArrayList<>();
        final ArrayList<TrashBinRealmModel> toDelete = new ArrayList<>();
        for(int i = 0; i < trashBinRealmModelRealmQuery.count(); i++){
            if(new File(trashBinRealmModelRealmQuery.findAll().get(i).getTrashbinpath()).exists()){
                list.add(trashBinRealmModelRealmQuery.findAll().get(i));

            }else{
                toDelete.add(trashBinRealmModelRealmQuery.findAll().get(i));
            }
        }
        for(int i = 0; i < toDelete.size(); i++){
            final String path = toDelete.get(i).getTrashbinpath();
            Realm realm = Realm.getDefaultInstance();
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    RealmResults<TrashBinRealmModel> realmResults = realm.where(TrashBinRealmModel.class).equalTo("trashbinpath",
                            path).findAll();
                    realmResults.deleteAllFromRealm();
                }
            });
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
        Realm realm = Realm.getDefaultInstance();
        trashBinRealmModelRealmQuery = realm.where(TrashBinRealmModel.class);
        if(getTrashObjects().size() == 0)
        {
            swipeRefreshLayout.setEnabled(false);
        }
        else
            {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                trashBinAdapter.setResults(getTrashObjects());
                if (swipeRefreshLayout.isRefreshing()) {
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        });
        }
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

        if(trashBinRealmModelRealmQuery.count() == 0){
            menu.findItem(R.id.trashbin_restore).setVisible(false);
            menu.findItem(R.id.delete_action).setVisible(false);
        }

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){

            case R.id.trashbin_restore:
                new RestoreAll().execute();
                return true;

            case R.id.delete_action:
                deleteAllMedia();
                return true;

                default:
                    return super.onOptionsItemSelected(item);
        }
    }
  
   class DeleteAll extends AsyncTask<Void, Void, Void>{
        private final Boolean[] deleted = {false};

        @Override
        protected void onPreExecute() {
            swipeRefreshLayout.setRefreshing(true);
            super.onPreExecute();
        }
          
           @Override
        protected Void doInBackground(Void... voids) {
           Realm realm = Realm.getDefaultInstance();
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    RealmResults<TrashBinRealmModel> trashBinRealmModels = realm.where(TrashBinRealmModel.class).findAll();
                    deleted[0] = trashBinRealmModels.deleteAllFromRealm();
                }
            });
            File binfolder = new File(Environment.getExternalStorageDirectory() + "/" + ".nomedia");
            if(binfolder.exists()){
                binfolder.delete();
               }
            return null;
        }
          
           @Override
        protected void onPostExecute(Void aVoid) {
            swipeRefreshLayout.setRefreshing(false);
            super.onPostExecute(aVoid);
            if(deleted[0] && trashBinRealmModelRealmQuery.count() == 0){
                swipeRefreshLayout.setEnabled(false);
                emptyView.setVisibility(View.VISIBLE);
                trashBinAdapter.setResults(getTrashObjects());
                SnackBarHandler.showWithBottomMargin(parentView, getResources().getString(R.string.clear_all_success_mssg), 0, Snackbar.LENGTH_SHORT);
            }
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
            trashBinAdapter.setResults(getTrashObjects());
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

    private boolean[] deleteAllMedia(){
        final boolean[] deleted = {false};
        AlertDialog.Builder deleteDialog = new AlertDialog.Builder(TrashBinActivity.this, getDialogStyle());
        AlertDialogsHelper.getTextDialog(this, deleteDialog, R.string.clear_trash_title, R.string.delete_all_trash, null);
        deleteDialog.setNegativeButton(getString(R.string.cancel).toUpperCase(), null);
        deleteDialog.setPositiveButton(getString(R.string.clear).toUpperCase(), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (securityObj.isActiveSecurity() && securityObj.isPasswordOnDelete()) {
                    final boolean passco[] = {false};
                    AlertDialog.Builder passwordDialogBuilder = new AlertDialog.Builder(TrashBinActivity.this, getDialogStyle());
                    final EditText editTextPassword = securityObj.getInsertPasswordDialog(TrashBinActivity.this,
                            passwordDialogBuilder);
                    editTextPassword.setHintTextColor(getResources().getColor(R.color.grey, null));
                    passwordDialogBuilder.setNegativeButton(getString(R.string.cancel).toUpperCase(), null);
                    passwordDialogBuilder.setPositiveButton(getString(R.string.ok_action).toUpperCase(), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //This should be empty. It will be overwritten later
                            //to avoid dismiss of the dialog on wrong password
                        }
                    });
                    editTextPassword.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                            //empty method body
                        }

                        @Override public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                            //empty method body
                        }

                        @Override public void afterTextChanged(Editable editable) {
                            if(securityObj.getTextInputLayout().getVisibility() == View.VISIBLE && !passco[0]){
                                securityObj.getTextInputLayout().setVisibility(View.INVISIBLE);
                            }
                            else{
                                passco[0]=false;
                            }
                        }
                    });

                    final AlertDialog passwordDialog = passwordDialogBuilder.create();
                    passwordDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
                    passwordDialog.show();
                    AlertDialogsHelper.setButtonTextColor(new int[]{DialogInterface.BUTTON_POSITIVE, DialogInterface.BUTTON_NEGATIVE}, getAccentColor(), passwordDialog);
                    passwordDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // if password is correct, call DeletePhotos and perform deletion
                            if (securityObj.checkPassword(editTextPassword.getText().toString())) {
                                new DeleteAll().execute();
                            }
                            // if password is incorrect, don't delete and notify user of incorrect password
                            else {
                                passco[0] = true;
                                securityObj.getTextInputLayout().setVisibility(View.VISIBLE);
//                                SnackBarHandler.showWithBottomMargin(parentView, getString(R.string.wrong_password),
//                                        navigationView.getHeight());
                                editTextPassword.getText().clear();
                                editTextPassword.requestFocus();
                            }
                        }
                    });
                } else {
                    new DeleteAll().execute();
                }
            }
        });
        AlertDialog alertDialogDelete = deleteDialog.create();
        alertDialogDelete.show();
        AlertDialogsHelper.setButtonTextColor(new int[]{DialogInterface.BUTTON_POSITIVE, DialogInterface.BUTTON_NEGATIVE}, getAccentColor(), alertDialogDelete);
        return deleted;
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
        getSupportActionBar().setTitle(getString(R.string.trash_bin));
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

    @Override
    public void onResume() {
        super.onResume();
        setUpUi();
        Realm realm = Realm.getDefaultInstance();
        trashBinRealmModelRealmQuery = realm.where(TrashBinRealmModel.class);
        if(getTrashObjects().size() == 0){
            //trashBinAdapter.updateTrashListItems(getTrashObjects());
            trashBinRecyclerView.setVisibility(View.INVISIBLE);
            emptyView.setVisibility(View.VISIBLE);
        } else {
            trashBinAdapter.setResults(getTrashObjects());
        }
    }
}