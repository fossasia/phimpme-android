package org.fossasia.phimpme.uploadhistory;


import static org.fossasia.phimpme.utilities.ActivitySwitchHelper.context;

import java.io.File;
import java.util.ArrayList;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mikepenz.community_material_typeface_library.CommunityMaterial;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.iconics.view.IconicsImageView;

import org.fossasia.phimpme.R;
import org.fossasia.phimpme.base.ThemedActivity;
import org.fossasia.phimpme.data.local.UploadHistoryRealmModel;
import org.fossasia.phimpme.gallery.activities.SettingsActivity;
import org.fossasia.phimpme.gallery.activities.SingleMediaActivity;
import org.fossasia.phimpme.gallery.data.Media;
import org.fossasia.phimpme.gallery.util.AlertDialogsHelper;
import org.fossasia.phimpme.gallery.util.PreferenceUtil;
import org.fossasia.phimpme.gallery.util.SecurityHelper;
import org.fossasia.phimpme.utilities.SnackBarHandler;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

import static org.fossasia.phimpme.utilities.ActivitySwitchHelper.getContext;

/**
 * Created by pa1pal on 17/08/17.
 */

public class UploadHistory extends ThemedActivity {

    @BindView(R.id.upload_history_recycler_view)
    RecyclerView uploadHistoryRecyclerView;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.swipeRefreshLayout_uploadhis)
    protected SwipeRefreshLayout swipeRefreshLayout;

    @BindView(R.id.empty_icon)
    IconicsImageView emptyIcon;

    @BindView(R.id.emptyLayout)
    RelativeLayout emptyLayout;

    @BindView(R.id.empty_text)
    TextView emptyText;

    @BindView(R.id.accounts_parent)
    RelativeLayout parentView;

    Realm realm;

    private ArrayList<UploadHistoryRealmModel> uploadResults;
    private RealmQuery<UploadHistoryRealmModel> uploadHistoryRealmModelRealmQuery;
    private UploadHistoryAdapter uploadHistoryAdapter;
    private PreferenceUtil preferenceUtil;
    private SecurityHelper securityObj;

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override public void onClick(View view) {
            UploadHistoryRealmModel uploadHistoryRealmModel = (UploadHistoryRealmModel) view.findViewById(R.id
                    .upload_time).getTag();
            view.setTransitionName(getString(R.string.transition_photo));
            Intent intent = new Intent("com.android.camera.action.REVIEW", Uri.fromFile(new File(uploadHistoryRealmModel.getPathname())));
            intent.putExtra("path", uploadHistoryRealmModel.getPathname());
            intent.putExtra("position", checkpos(uploadHistoryRealmModel.getPathname()));
            intent.putExtra("size", uploadResults.size());
            intent.putExtra("uploadhistory", true);
            ArrayList<Media> u = loaduploaddata();
            intent.putParcelableArrayListExtra("datalist", u);
            intent.setClass(getApplicationContext(), SingleMediaActivity.class);
            context.startActivity(intent);
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.upload_history_activity);
        ButterKnife.bind(this);
        preferenceUtil = PreferenceUtil.getInstance(getContext());
        uploadHistoryAdapter = new UploadHistoryAdapter(getPrimaryColor());
        uploadHistoryAdapter.setOnClickListener(onClickListener);
        realm = Realm.getDefaultInstance();
        securityObj = new SecurityHelper(UploadHistory.this);
        removedeletedphotos();
        uploadHistoryRealmModelRealmQuery = realm.where(UploadHistoryRealmModel.class);
        if(uploadHistoryRealmModelRealmQuery.count() == 0){
            emptyLayout.setVisibility(View.VISIBLE);
            swipeRefreshLayout.setEnabled(false);
        } else {
            String choiceofdisply = preferenceUtil.getString(getString(R.string.upload_view_choice), getString(R.string
                    .last_first));
            if(choiceofdisply.equals(getString(R.string.last_first))){
                uploadHistoryAdapter.setResults(loadData(getString(R.string.last_first)));
            }else if(choiceofdisply.equals(getString(R.string.latest_first))){
                uploadHistoryAdapter.setResults(loadData(getString(R.string.latest_first)));
            }
            GridLayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), columnsCount());
            layoutManager.setReverseLayout(false);
            uploadHistoryRecyclerView.setLayoutManager(layoutManager);
            uploadHistoryRecyclerView.setAdapter(uploadHistoryAdapter);
        }
        setUpUI();
        //uploadHistoryRecyclerView.addOnItemTouchListener(new RecyclerItemClickListner(this, this));
    }


    private void removedeletedphotos(){
        RealmQuery<UploadHistoryRealmModel> uploadHistoryRealmModelRealmQuery = realm.where(UploadHistoryRealmModel.class);
        ArrayList<String> todel = new ArrayList<>();
        for(int i = 0; i < uploadHistoryRealmModelRealmQuery.count(); i++){
            if(!new File(uploadHistoryRealmModelRealmQuery.findAll().get(i).getPathname()).exists()){
                todel.add(uploadHistoryRealmModelRealmQuery.findAll().get(i).getPathname());
            }
        }
        for(int i = 0; i < todel.size(); i++){
            final String path = todel.get(i);
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    RealmResults<UploadHistoryRealmModel> result = realm.where(UploadHistoryRealmModel.class).equalTo
                            ("pathname", path).findAll();
                    result.deleteAllFromRealm();
                }
            });
        }
    }

    private ArrayList<Media> loaduploaddata(){
        ArrayList<Media> data = new ArrayList<>();
        for(int i = 0; i < uploadResults.size(); i++){
            data.add(new Media(new File(uploadResults.get(i).getPathname())));
        }
        return data;
    }

    private ArrayList<UploadHistoryRealmModel> loadData(String displaychoice){
       // ArrayList<UploadHistoryRealmModel> ki = new ArrayList<>();
       // String s = preferenceUtil.getString("upload_view_choice", "Last first");
        uploadResults = new ArrayList<>();
        if(displaychoice.equals(getString(R.string.last_first)) && uploadHistoryRealmModelRealmQuery.count() != 0){
            for(int i = 0; i < uploadHistoryRealmModelRealmQuery.findAll().size(); i++){
                uploadResults.add(uploadHistoryRealmModelRealmQuery.findAll().get(i));
            }
        }else if(displaychoice.equals(getString(R.string.latest_first)) && uploadHistoryRealmModelRealmQuery.count() != 0){
            for(int i = 0; i < uploadHistoryRealmModelRealmQuery.findAll().size(); i++){
                uploadResults.add(uploadHistoryRealmModelRealmQuery.findAll().get(uploadHistoryRealmModelRealmQuery
                        .findAll().size() - i - 1));
            }
        }
        return uploadResults;
    }

    private int checkpos(String path){
        int pos = 0;
        for(int i = 0; i < uploadResults.size(); i++){
            if(path.equals(uploadResults.get(i).getPathname())){
                pos = i;
                break;
            }
        }
        return pos;
    }

    private int columnsCount() {
        return getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT
                ? 2
                : 3;
    }

    private void setUpUI() {
        emptyIcon.setColor(getIconColor());
        emptyText.setTextColor(getAccentColor());
        parentView.setBackgroundColor(getBackgroundColor());
        setupToolbar();
        swipeRefreshLayout.setColorSchemeColors(getAccentColor());
        swipeRefreshLayout.setProgressBackgroundColorSchemeColor(getBackgroundColor());
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override public void onRefresh() {
                String choiceofdisply = preferenceUtil.getString(getString(R.string.upload_view_choice), getString(R.string
                        .last_first));
                if(uploadHistoryRealmModelRealmQuery.count() != 0){
                    if(choiceofdisply.equals(getString(R.string.last_first))){
                        uploadHistoryAdapter.setResults(loadData(getString(R.string.last_first)));
                    }else if(choiceofdisply.equals(getString(R.string.latest_first))){
                        uploadHistoryAdapter.setResults(loadData(getString(R.string.latest_first)));
                    }
                }else {
                    emptyLayout.setVisibility(View.VISIBLE);
                }
                if(swipeRefreshLayout.isRefreshing()){
                    swipeRefreshLayout.setRefreshing(false
                    );
                }
            }
        });
    }

    @Override public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_uploadhistoryactivity, menu);
        return true;
    }

    @Override public boolean onPrepareOptionsMenu(Menu menu) {

        if(uploadHistoryRealmModelRealmQuery.count() == 0){
            menu.findItem(R.id.delete_action).setVisible(false);
            menu.findItem(R.id.upload_history_sort).setVisible(false);
        }
       if(preferenceUtil.getString(getString(R.string.upload_view_choice), getString(R.string.last_first)).equals
               (getString(R.string.last_first))){
           menu.findItem(R.id.upload_history_sort).setTitle(getString(R.string.latest_first));
       }else if(preferenceUtil.getString(getString(R.string.upload_view_choice), getString(R.string.last_first)).equals
               (getString(R.string.latest_first))){
           menu.findItem(R.id.upload_history_sort).setTitle(getString(R.string.last_first));
       }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){

            case R.id.upload_history_sort:
                if(item.getTitle().equals(getString(R.string.latest_first))){
                    item.setTitle(getString(R.string.last_first));
                    new SortTask().execute(getString(R.string.latest_first));
                }else{
                    item.setTitle(getString(R.string.latest_first));
                    new SortTask().execute(getString(R.string.last_first));
                }
                return true;

            case R.id.delete_action:
                deleteAllMedia();
                return true;
            case R.id.up_settings:
                startActivity(new Intent(UploadHistory.this, SettingsActivity.class));
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void deleteAllMedia(){
        AlertDialog.Builder deleteDialog = new AlertDialog.Builder(UploadHistory.this, getDialogStyle());
        AlertDialogsHelper.getTextDialog(this, deleteDialog, R.string.delete_all_title, R.string.delete_all_message, null);
        deleteDialog.setNegativeButton(getString(R.string.cancel).toUpperCase(), null);
        deleteDialog.setPositiveButton(getString(R.string.delete).toUpperCase(), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (securityObj.isActiveSecurity() && securityObj.isPasswordOnDelete()) {
                    final boolean passco[] = {false};
                    AlertDialog.Builder passwordDialogBuilder = new AlertDialog.Builder(UploadHistory.this, getDialogStyle());
                    final EditText editTextPassword = securityObj.getInsertPasswordDialog(UploadHistory.this,
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
                                passwordDialog.dismiss();
                               new DeleteHistory().execute();
                            }
                            // if password is incorrect, don't delete and notify user of incorrect password
                            else {
                                passco[0] = true;
                                securityObj.getTextInputLayout().setVisibility(View.VISIBLE);
                                SnackBarHandler.showWithBottomMargin(parentView, getString(R.string.wrong_password),
                                        navigationView.getHeight());
                                editTextPassword.getText().clear();
                                editTextPassword.requestFocus();
                            }
                        }
                    });
                } else new DeleteHistory().execute();
            }
        });
        AlertDialog alertDialogDelete = deleteDialog.create();
        alertDialogDelete.show();
        AlertDialogsHelper.setButtonTextColor(new int[]{DialogInterface.BUTTON_POSITIVE, DialogInterface.BUTTON_NEGATIVE}, getAccentColor(), alertDialogDelete);
    }

    class DeleteHistory extends AsyncTask<Void, Void, Void>{

        final boolean[] result = {false};

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            swipeRefreshLayout.setRefreshing(true);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            Realm realm = Realm.getDefaultInstance();
            realm.executeTransaction(new Realm.Transaction() {
                @Override public void execute(Realm realm) {
                    RealmResults<UploadHistoryRealmModel> uploadHistoryRealmModels = realm.where(UploadHistoryRealmModel.class).findAll();
                    result[0] = uploadHistoryRealmModels.deleteAllFromRealm();
                }
            });
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            swipeRefreshLayout.setRefreshing(false);
            if(result[0] && uploadHistoryRealmModelRealmQuery.count() == 0){
                emptyLayout.setVisibility(View.VISIBLE);
                uploadHistoryRecyclerView.setVisibility(View.GONE);
                swipeRefreshLayout.setEnabled(false);
            }
            invalidateOptionsMenu();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        setUpUI();
        uploadHistoryRealmModelRealmQuery = realm.where(UploadHistoryRealmModel.class);
        String choiceofdisply = preferenceUtil.getString(getString(R.string.upload_view_choice), getString(R.string
                .last_first));
        if(choiceofdisply.equals(getString(R.string.last_first))){
            uploadHistoryAdapter.setResults(loadData(getString(R.string.last_first)));
        }else if(choiceofdisply.equals(getString(R.string.latest_first))){
            uploadHistoryAdapter.setResults(loadData(getString(R.string.latest_first)));
        }
        if (uploadHistoryRealmModelRealmQuery.count() == 0) {
            emptyLayout.setVisibility(View.VISIBLE);
            uploadHistoryRecyclerView.setVisibility(View.GONE);
        }
    }
    private void setupToolbar(){
        setSupportActionBar(toolbar);
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
    }

    private class SortTask extends AsyncTask<String, Void, Void> {
        Realm realm;

        @Override protected void onPreExecute() {
            swipeRefreshLayout.setRefreshing(true);
            super.onPreExecute();
        }

        @Override protected Void doInBackground(String... strings) {
            realm = Realm.getDefaultInstance();
            if(strings[0].equals(getString(R.string.latest_first))){
                SharedPreferences.Editor s = preferenceUtil.getEditor();
                s.putString(getString(R.string.upload_view_choice), getString(R.string.latest_first));
                s.commit();
                runOnUiThread(new Runnable() {
                    @Override public void run() {
                        uploadHistoryAdapter.setResults(loadData(getString(R.string.latest_first)));
                    }
                });
            }else if(strings[0].equals(getString(R.string.last_first))){
                SharedPreferences.Editor s = preferenceUtil.getEditor();
                s.putString(getString(R.string.upload_view_choice), getString(R.string.last_first));
                s.commit();
                runOnUiThread(new Runnable() {
                    @Override public void run() {
                        uploadHistoryAdapter.setResults(loadData(getString(R.string.last_first)));
                    }
                });
            }
            return null;
        }

        @Override protected void onPostExecute(Void aVoid) {
            swipeRefreshLayout.setRefreshing(false);
        }
    }
}
