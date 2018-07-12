package org.fossasia.phimpme.TrashBin;

import java.util.ArrayList;

import org.fossasia.phimpme.R;
import org.fossasia.phimpme.base.ThemedActivity;
import org.fossasia.phimpme.data.local.TrashBinRealmModel;

import com.mikepenz.community_material_typeface_library.CommunityMaterial;
import com.mikepenz.iconics.IconicsDrawable;

import android.content.res.Configuration;
import android.graphics.Color;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.RelativeLayout;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmQuery;

public class TrashBinActivity extends ThemedActivity {

    @BindView(R.id.trashbin_recycler_view)
    RecyclerView trashBinRecyclerView;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.empty_trash)
    RelativeLayout emptyView;

    @BindView(R.id.swipeRefreshLayout_trashbin)
    protected SwipeRefreshLayout swipeRefreshLayout;

    @BindView(R.id.view_parent)
    RelativeLayout parentView;

    private Realm realm;
    private RealmQuery<TrashBinRealmModel> trashBinRealmModelRealmQuery;
    private ArrayList<TrashBinRealmModel> trashlist = null;


    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trash_bin);
        ButterKnife.bind(this);
        realm = Realm.getDefaultInstance();
        trashBinRealmModelRealmQuery = realm.where(TrashBinRealmModel.class);
        trashlist = getTrashObjects();
        if(trashlist.size() == 0){
            emptyView.setVisibility(View.VISIBLE);
        }else{
            TrashBinAdapter trashBinAdapter = new TrashBinAdapter(trashlist);
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

    private int columnsCount() {
        return getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT
                ? 2
                : 3;
    }

    private void setUpUi(){
        parentView.setBackgroundColor(getBackgroundColor());
        setupToolbar();
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
