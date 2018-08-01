package org.fossasia.phimpme.trashbin;

import java.util.ArrayList;

import org.fossasia.phimpme.R;
import org.fossasia.phimpme.base.ThemedActivity;
import org.fossasia.phimpme.data.local.TrashBinRealmModel;
import org.fossasia.phimpme.gallery.util.ThemeHelper;

import com.mikepenz.community_material_typeface_library.CommunityMaterial;
import com.mikepenz.iconics.IconicsDrawable;

import android.content.res.Configuration;
import android.graphics.Color;
import android.support.v4.widget.SwipeRefreshLayout;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmQuery;

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

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trash_bin2);
        ButterKnife.bind(this);
        Realm realm = Realm.getDefaultInstance();
        trashBinRealmModelRealmQuery = realm.where(TrashBinRealmModel.class);
        ArrayList<TrashBinRealmModel> trashlist = getTrashObjects();
        if (trashlist.size() == 0) {
            emptyView.setVisibility(View.VISIBLE);
            trashEmptyViewSetup();
        } else {
            trashBinAdapter = new TrashBinAdapter(trashlist);
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

    private int columnsCount() {
        return getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT
                ? 2
                : 3;
    }

    private void setUpUi(){
        parentView.setBackgroundColor(getBackgroundColor());
        setupToolbar();
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override public void onRefresh() {
                trashBinAdapter.updateTrashListItems(getTrashObjectsLast());
                if(swipeRefreshLayout.isRefreshing()){
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        });
    }

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