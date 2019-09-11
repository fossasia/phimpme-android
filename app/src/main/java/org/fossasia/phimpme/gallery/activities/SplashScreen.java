package org.fossasia.phimpme.gallery.activities;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;

import butterknife.BindView;
import butterknife.ButterKnife;
import java.io.IOException;
import org.fossasia.phimpme.R;
import org.fossasia.phimpme.base.SharedMediaActivity;
import org.fossasia.phimpme.gallery.data.Album;
import org.fossasia.phimpme.gallery.util.ColorPalette;
import org.fossasia.phimpme.gallery.util.PermissionUtils;
import org.fossasia.phimpme.gallery.util.PreferenceUtil;
import org.fossasia.phimpme.utilities.ActivitySwitchHelper;
import org.fossasia.phimpme.utilities.SnackBarHandler;
import pl.droidsonroids.gif.AnimationListener;
import pl.droidsonroids.gif.GifDrawable;

public class SplashScreen extends SharedMediaActivity {

  private final int READ_EXTERNAL_STORAGE_ID = 12;
  private static final int PICK_MEDIA_REQUEST = 44;

  public static final String CONTENT = "content";
  public static final String PICK_MODE = "pick_mode";

  public static final int ALBUMS_PREFETCHED = 23;
  public static final int PHOTOS_PREFETCHED = 2;
  public static final int ALBUMS_BACKUP = 60;
  private boolean PICK_INTENT = false;
  public static final String ACTION_OPEN_ALBUM = "vn.mbm.phimp.leafpic.OPEN_ALBUM";

  // private HandlingAlbums albums;
  private Album album;
  private boolean can_be_finished = false;
  private Intent nextIntent = null;
  private PreferenceUtil SP;

  @BindView(R.id.splash_bg)
  RelativeLayout parentView;

  @BindView(R.id.imgLogo)
  ImageView logoView;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_splash);
    ActivitySwitchHelper.setContext(this);
    ButterKnife.bind(this);
    SP = PreferenceUtil.getInstance(getApplicationContext());

    parentView.setBackgroundColor(ColorPalette.getObscuredColor(getPrimaryColor()));

    getWindow()
        .getDecorView()
        .setSystemUiVisibility(
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

    setNavBarColor();
    setStatusBarColor();

    GifDrawable gifDrawable = null;
    try {
      gifDrawable = new GifDrawable(getAssets(), "splash_logo_anim.gif");
    } catch (IOException e) {
      e.printStackTrace();
    }
    if (gifDrawable != null) {
      gifDrawable.addAnimationListener(
          new AnimationListener() {
            @Override
            public void onAnimationCompleted(int loopNumber) {
              Log.d("splashscreen", "Gif animation completed");
              if (can_be_finished && nextIntent != null) {
                startActivity(nextIntent);
                finish();
              } else {
                can_be_finished = true;
              }
            }
          });
    }
    logoView.setImageDrawable(gifDrawable);

    if (PermissionUtils.isDeviceInfoGranted(this)) {
      PICK_INTENT =
          getIntent().getAction().equals(Intent.ACTION_GET_CONTENT)
              || getIntent().getAction().equals(Intent.ACTION_PICK);
      if (getIntent().getAction().equals(ACTION_OPEN_ALBUM)) {
        Bundle data = getIntent().getExtras();
        if (data != null) {
          String ab = data.getString("albumPath");
          if (ab != null) {
            new PrefetchPhotosData().execute();
          }
        } else SnackBarHandler.create(parentView, R.string.album_not_found).show();
      } else // default intent
      new PrefetchAlbumsData().execute();
    } else {
      askForPermission();
    }
  }

  private void askForPermission() {
    String[] permissions = new String[] {Manifest.permission.READ_EXTERNAL_STORAGE};
    PermissionUtils.requestPermissions(this, READ_EXTERNAL_STORAGE_ID, permissions);
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (requestCode == PICK_MEDIA_REQUEST) {
      if (resultCode == RESULT_OK) {
        setResult(RESULT_OK, data);
        finish();
      }
    }
  }

  @Override
  public void setNavBarColor() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      getWindow()
          .setStatusBarColor(
              ColorPalette.getTransparentColor(
                  ContextCompat.getColor(getApplicationContext(), R.color.md_black_1000), 70));
    }
  }

  @Override
  protected void setStatusBarColor() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      getWindow()
          .setNavigationBarColor(
              ColorPalette.getTransparentColor(
                  ContextCompat.getColor(getApplicationContext(), R.color.md_black_1000), 70));
    }
  }

  @Override
  public void onRequestPermissionsResult(
          int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    switch (requestCode) {
      case READ_EXTERNAL_STORAGE_ID:
        boolean granted =
            grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED;
        if (granted)
          new PrefetchAlbumsData()
              .execute(SP.getBoolean(getString(R.string.preference_auto_update_media), false));
        else {
          SnackBarHandler.create(parentView, R.string.storage_permission_denied).show();
          showPermissionAlertDialog();
        }
        break;
      default:
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
  }

  private void showPermissionAlertDialog() {
    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setIcon(R.drawable.ic_caution);
    builder.setTitle(R.string.permission_rationale_title);
    builder.setMessage(R.string.permission_storage_alert);
    builder.setCancelable(false);
    builder.setPositiveButton(
        R.string.exit,
        new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            finish();
          }
        });

    builder.setNegativeButton(
        R.string.grant_permission,
        new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            Toast.makeText(getApplicationContext(), R.string.permissions_restart, Toast.LENGTH_LONG)
                .show();
            Intent intent = new Intent();
            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts("package", getPackageName(), null);
            intent.setData(uri);
            startActivity(intent);
          }
        });

    AlertDialog alertDialog = builder.create();
    alertDialog.show();
  }

  private class PrefetchAlbumsData extends AsyncTask<Boolean, Boolean, Boolean> {

    @Override
    protected Boolean doInBackground(Boolean... arg0) {
      getAlbums().restoreBackup(getApplicationContext());
      if (getAlbums().dispAlbums.size() == 0) {
        getAlbums().loadAlbums(getApplicationContext(), false);
        return true;
      }
      return false;
    }

    @Override
    protected void onPostExecute(Boolean result) {
      super.onPostExecute(result);

      nextIntent = new Intent(SplashScreen.this, LFMainActivity.class);
      Bundle b = new Bundle();
      b.putInt(CONTENT, result ? ALBUMS_PREFETCHED : ALBUMS_BACKUP);
      b.putBoolean(PICK_MODE, PICK_INTENT);
      nextIntent.putExtras(b);
      if (PICK_INTENT) startActivityForResult(nextIntent, PICK_MEDIA_REQUEST);
      else {
        if (can_be_finished) {
          startActivity(nextIntent);
          finish();
        } else {
          can_be_finished = true;
        }
      }
      if (result) getAlbums().saveBackup(getApplicationContext());
    }
  }

  private class PrefetchPhotosData extends AsyncTask<Void, Void, Void> {
    @Override
    protected Void doInBackground(Void... arg0) {
      album.updatePhotos(getApplicationContext());
      return null;
    }

    @Override
    protected void onPostExecute(Void result) {
      super.onPostExecute(result);
      nextIntent = new Intent(SplashScreen.this, LFMainActivity.class);
      Bundle b = new Bundle();
      getAlbums().addAlbum(0, album);
      b.putInt(CONTENT, PHOTOS_PREFETCHED);
      nextIntent.putExtras(b);
      if (can_be_finished) {
        startActivity(nextIntent);
        finish();
      } else {
        can_be_finished = true;
      }
    }
  }
}
