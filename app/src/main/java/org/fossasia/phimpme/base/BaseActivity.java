package org.fossasia.phimpme.base;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetSequence;
import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import org.fossasia.phimpme.R;
import org.fossasia.phimpme.accounts.AccountActivity;
import org.fossasia.phimpme.gallery.activities.LFMainActivity;
import org.fossasia.phimpme.gallery.util.PermissionUtils;
import org.fossasia.phimpme.gallery.util.PreferenceUtil;
import org.fossasia.phimpme.opencamera.Camera.CameraActivity;

public abstract class BaseActivity extends AppCompatActivity
    implements BottomNavigationView.OnNavigationItemSelectedListener {

  protected BottomNavigationView navigationView;
  private static final String SHOWCASE_ID = "1";
  BottomNavigationItemView nav_home;
  BottomNavigationItemView nav_cam;
  BottomNavigationItemView nav_acc;
  private PreferenceUtil SP;
  private boolean isSWNavBarChecked;

  private int[][] states =
      new int[][] {
        new int[] {android.R.attr.state_checked}, // checked
        new int[] {-android.R.attr.state_checked}, // unchecked
      };

  private int[] colors =
      new int[] {
        Color.WHITE, // checked
        0 // unchecked set default in onCreate
      };

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(getContentViewId());
    colors[1] = ContextCompat.getColor(this, R.color.bottom_navigation_tabs);
    ColorStateList myList = new ColorStateList(states, colors);
    navigationView = findViewById(R.id.bottombar);
    navigationView.setItemIconTintList(myList);
    navigationView.setItemTextColor(myList);
    navigationView.setOnNavigationItemSelectedListener(this);

    nav_home = findViewById(R.id.navigation_home);
    nav_cam = findViewById(R.id.navigation_camera);
    nav_acc = findViewById(R.id.navigation_accounts);

    SP = PreferenceUtil.getInstance(getApplicationContext());

    if (PermissionUtils.checkPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
        && SP.getBoolean(getResources().getString(R.string.first_time_showcase), true))
      presentShowcaseSequence();
  }

  @Override
  protected void onResume() {
    super.onResume();
    isSWNavBarChecked = SP.getBoolean(getString(R.string.preference_colored_nav_bar), true);
  }

  @Override
  protected void onStart() {
    super.onStart();
    updateNavigationBarState();
  }

  private void presentShowcaseSequence() {
    new TapTargetSequence(this)
        .targets(
            TapTarget.forView(nav_home, getResources().getString(R.string.home_button))
                .cancelable(true)
                .outerCircleAlpha(0.7f)
                .textColor(R.color.white)
                .transparentTarget(true),
            TapTarget.forView(nav_cam, getResources().getString(R.string.camera_button))
                .cancelable(true)
                .outerCircleAlpha(0.7f)
                .textColor(R.color.white)
                .transparentTarget(true),
            TapTarget.forView(nav_acc, getResources().getString(R.string.accounts_button))
                .outerCircleAlpha(0.7f)
                .textColor(R.color.white)
                .transparentTarget(true))
        .listener(
            new TapTargetSequence.Listener() {
              @Override
              public void onSequenceFinish() {
                SP.putBoolean(getResources().getString(R.string.first_time_showcase), false);
              }

              @Override
              public void onSequenceStep(TapTarget lastTarget, boolean targetClicked) {}

              @Override
              public void onSequenceCanceled(TapTarget lastTarget) {
                SP.putBoolean(getResources().getString(R.string.first_time_showcase), false);
              }
            })
        .start();
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
          Intent cameraIntent = new Intent(this, CameraActivity.class);
          startActivity(cameraIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
          break;
        case R.id.navigation_home:
          Intent homeIntent = new Intent(this, LFMainActivity.class);
          startActivity(homeIntent);
          break;
        case R.id.navigation_accounts:
          Intent accountIntent = new Intent(this, AccountActivity.class);
          startActivity(accountIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
          break;
      }
      finish();
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

  void setIconColor(int color) {
    if (Color.red(color) + Color.green(color) + Color.blue(color) < 300) colors[0] = Color.WHITE;
    else colors[0] = Color.BLACK;
  }

  public abstract int getContentViewId();

  public abstract int getNavigationMenuItemId();

  public void setNavigationBarColor(int color) {
    if (isSWNavBarChecked) {
      navigationView.setBackgroundColor(color);
      SP.putInt(getString(R.string.preference_BottomNavColor), color);
    } else {
      navigationView.setBackgroundColor(
          SP.getInt(getString(R.string.preference_BottomNavColor), color));
    }
    setIconColor(color);
  }

  /** Animate bottom navigation bar from GONE to VISIBLE */
  public void showNavigationBar() {
    navigationView
        .animate()
        .translationY(0)
        .alpha(1.0f)
        .setDuration(400)
        .setListener(
            new AnimatorListenerAdapter() {
              @Override
              public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                navigationView.setVisibility(View.VISIBLE);
              }
            });
  }

  /** Animate bottom navigation bar from VISIBLE to GONE */
  public void hideNavigationBar() {
    navigationView
        .animate()
        .alpha(0.0f)
        .translationYBy(navigationView.getHeight())
        .setDuration(400)
        .setListener(
            new AnimatorListenerAdapter() {
              @Override
              public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                navigationView.setVisibility(View.GONE);
              }
            });
  }
}
