package org.fossasia.phimpme.uploadhistory;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mikepenz.community_material_typeface_library.CommunityMaterial;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.iconics.view.IconicsImageView;

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

    @BindView(R.id.empty_icon)
    IconicsImageView emptyIcon;

    @BindView(R.id.emptyLayout)
    RelativeLayout emptyLayout;

    @BindView(R.id.empty_text)
    TextView emptyText;

    @BindView(R.id.accounts_parent)
    RelativeLayout parentView;

    Realm realm;

    private RealmQuery<UploadHistoryRealmModel> uploadResults;
    private UploadHistoryAdapter uploadHistoryAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.upload_history_activity);
        ButterKnife.bind(this);
        uploadHistoryAdapter = new UploadHistoryAdapter(getPrimaryColor());
        realm = Realm.getDefaultInstance();
        uploadResults = realm.where(UploadHistoryRealmModel.class);
        GridLayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 2);
        layoutManager.setReverseLayout(false);
        uploadHistoryRecyclerView.setLayoutManager(layoutManager);
        uploadHistoryRecyclerView.setAdapter(uploadHistoryAdapter);
        uploadHistoryAdapter.setResults(uploadResults);

        setUpUI();
        //uploadHistoryRecyclerView.addOnItemTouchListener(new RecyclerItemClickListner(this, this));
    }

    private void setUpUI() {
        emptyIcon.setColor(getIconColor());
        emptyText.setTextColor(getAccentColor());
        parentView.setBackgroundColor(getBackgroundColor());
        setupToolbar();
    }

    public void setUpAdapter(@NotNull RealmQuery<UploadHistoryRealmModel> accountDetails) {
        this.uploadResults = accountDetails;
        uploadHistoryAdapter.setResults(uploadResults);
    }

    @Override
    public void onResume() {
        super.onResume();
        setUpUI();
        if (uploadResults.findAll().size() == 0) {
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
}
