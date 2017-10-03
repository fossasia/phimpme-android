package org.fossasia.phimpme.base;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import org.fossasia.phimpme.R;
import org.fossasia.phimpme.accounts.AccountActivity;
import org.fossasia.phimpme.gallery.activities.LFMainActivity;
import org.fossasia.phimpme.opencamera.Camera.CameraActivity;

import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig;

public abstract class BaseActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    protected BottomNavigationView navigationView;
    private static final String SHOWCASE_ID = "1";
    BottomNavigationItemView nav_home;
    BottomNavigationItemView nav_cam;
    BottomNavigationItemView nav_acc;

    private int[][] states = new int[][] {
            new int[] {android.R.attr.state_checked}, // checked
            new int[] {-android.R.attr.state_checked}, // unchecked
    };

    private int[] colors = new int[] {
            Color.WHITE, // checked
            0 // unchecked set default in onCreate
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getContentViewId());
        colors[1]  = ContextCompat.getColor(this, R.color.bottom_navigation_tabs);
        ColorStateList myList = new ColorStateList(states, colors);
        navigationView = (BottomNavigationView) findViewById(R.id.bottombar);
        navigationView.setItemIconTintList(myList);
        navigationView.setItemTextColor(myList);
        navigationView.setOnNavigationItemSelectedListener(this);

         nav_home = (BottomNavigationItemView) findViewById(R.id.navigation_home);
         nav_cam = (BottomNavigationItemView) findViewById(R.id.navigation_camera);
         nav_acc = (BottomNavigationItemView) findViewById(R.id.navigation_accounts);

        int checkStoragePermission = ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if(checkStoragePermission  == PackageManager.PERMISSION_GRANTED)
            presentShowcaseSequence(); // one second delay
    }

    private void presentShowcaseSequence() {

        ShowcaseConfig config = new ShowcaseConfig();
        config.setDelay(4000); // half second between each showcase view

        MaterialShowcaseSequence sequence = new MaterialShowcaseSequence(this, SHOWCASE_ID);

        sequence.setOnItemShownListener(new MaterialShowcaseSequence.OnSequenceItemShownListener() {
            @Override
            public void onShow(MaterialShowcaseView itemView, int position) {
                //Toast.makeText(itemView.getContext(), "Item #" + position, Toast.LENGTH_SHORT).show();
            }
        });

        sequence.setConfig(config);

        sequence.addSequenceItem(nav_home, getResources().getString(R.string.home_button), getResources().getString(R.string.ok_button));

        sequence.addSequenceItem(
                new MaterialShowcaseView.Builder(this)
                        .setTarget(nav_cam)
                        .setDismissText(getResources().getString(R.string.ok_button))
                        .setContentText(getResources().getString(R.string.camera_button))
                        .build()
        );

        sequence.addSequenceItem(
                new MaterialShowcaseView.Builder(this)
                        .setTarget(nav_acc)
                        .setDismissText(getResources().getString(R.string.ok_button))
                        .setContentText(getResources().getString(R.string.accounts_button))
                        .build()
        );

        sequence.start();
    }


    @Override
    protected void onStart() {
        super.onStart();
        updateNavigationBarState();
    }

    // Remove inter-activity transition to avoid screen tossing on tapping bottom navigation items
    @Override
    public void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull final MenuItem item) {
        if (item.getItemId() != getNavigationMenuItemId()) {
            switch (item.getItemId()) {
                case R.id.navigation_camera:
                    startActivity(new Intent(this, CameraActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                    break;
                case R.id.navigation_home:
                    startActivity(new Intent(this, LFMainActivity.class));
                    break;
                case R.id.navigation_accounts:
                    startActivity(new Intent(this, AccountActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                    break;
            }
        }
        return true;
    }

    private void updateNavigationBarState() {
        int actionId = getNavigationMenuItemId();
        selectBottomNavigationBarItem(actionId);
    }

    void selectBottomNavigationBarItem(int itemId) {
        Menu menu = navigationView.getMenu();
        for (int i = 0, size = menu.size(); i < size; i++) {
            MenuItem item = menu.getItem(i);
            boolean shouldBeChecked = item.getItemId() == itemId;
            if (shouldBeChecked) {
                item.setChecked(true);
                break;
            }
        }
    }

    void setIconColor(int color){
        if(Color.red(color) + Color.green(color)+ Color.blue(color) < 300)
            colors[0] = Color.WHITE;
        else
            colors[0] = Color.BLACK;
    }

    public abstract int getContentViewId();

    public abstract int getNavigationMenuItemId();

    public void setNavigationBarColor(int color) {
        navigationView.setBackgroundColor(color);
        setIconColor(color);
    }
    public void hideNavigationBar() {
        navigationView.setVisibility(View.GONE);
    }

}
