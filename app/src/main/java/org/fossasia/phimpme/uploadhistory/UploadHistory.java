package org.fossasia.phimpme.uploadhistory;

import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import org.fossasia.phimpme.R;
import org.fossasia.phimpme.base.ThemedActivity;
import org.fossasia.phimpme.data.local.UploadHistoryRealmModel;
import org.jetbrains.annotations.NotNull;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmQuery;

/**
 * Created by pa1pal on 17/08/17.
 */

public class UploadHistory extends ThemedActivity {

    @BindView(R.id.upload_history_recycler_view)
    RecyclerView uploadHistoryRecyclerView;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.accounts)
    CoordinatorLayout coordinatorLayout;

    Realm realm;

    private RealmQuery<UploadHistoryRealmModel> uploadResults;

    private UploadHistoryAdapter uploadHistoryAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.upload_history_activity);
        ButterKnife.bind(this);
        uploadHistoryAdapter = new UploadHistoryAdapter();
        realm = Realm.getDefaultInstance();
        uploadResults = realm.where(UploadHistoryRealmModel.class);
        setSupportActionBar(toolbar);
        toolbar.setPopupTheme(getPopupToolbarStyle());
        toolbar.setBackgroundColor(getPrimaryColor());
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        uploadHistoryRecyclerView.setLayoutManager(layoutManager);
        uploadHistoryRecyclerView.setAdapter(uploadHistoryAdapter);
        //uploadHistoryRecyclerView.addOnItemTouchListener(new RecyclerItemClickListner(this, this));

        uploadHistoryAdapter.setResults(uploadResults);


    }

    public void setUpAdapter(@NotNull RealmQuery<UploadHistoryRealmModel> accountDetails) {
        this.uploadResults= accountDetails;
        uploadHistoryAdapter.setResults(uploadResults);
    }
}
