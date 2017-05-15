package vn.mbm.phimp.me.leafpic.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import vn.mbm.phimp.me.R;
import vn.mbm.phimp.me.base.SharedMediaActivity;
import vn.mbm.phimp.me.leafpic.data.Album;
import vn.mbm.phimp.me.leafpic.util.ColorPalette;
import vn.mbm.phimp.me.leafpic.util.PermissionUtils;
import vn.mbm.phimp.me.leafpic.util.PreferenceUtil;

/**
 * Created by dnld on 01/04/16.
 */
public class SplashScreen extends SharedMediaActivity {

    private final int READ_EXTERNAL_STORAGE_ID = 12;
    private static final int PICK_MEDIA_REQUEST = 44;

    final static String CONTENT = "content";
    final static String PICK_MODE = "pick_mode";

    final static int ALBUMS_PREFETCHED = 23;
    final static int PHOTOS_PREFETCHED = 2;
    final static int ALBUMS_BACKUP = 60;
    private boolean PICK_INTENT = false;
    public final static String ACTION_OPEN_ALBUM = "vn.mbm.phimp.me.leafpic.OPEN_ALBUM";

    //private HandlingAlbums albums;
    private Album album;

    private PreferenceUtil SP;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        SP = PreferenceUtil.getInstance(getApplicationContext());

        ((ProgressBar) findViewById(R.id.progress_splash)).getIndeterminateDrawable().setColorFilter(getPrimaryColor(), PorterDuff.Mode.SRC_ATOP);

        RelativeLayout RelLay = (RelativeLayout) findViewById(R.id.Splah_Bg);
        RelLay.setBackgroundColor(getBackgroundColor());

        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        setNavBarColor();
        setStatusBarColor();

        if (PermissionUtils.isDeviceInfoGranted(this)) {
            new PrefetchAlbumsData().execute(SP.getBoolean(getString(R.string.preference_auto_update_media), false));
        } else {
            String[] permissions = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE};
            PermissionUtils.requestPermissions(this, READ_EXTERNAL_STORAGE_ID, permissions);
        }
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
            getWindow().setStatusBarColor(ColorPalette.getTransparentColor(
                    ContextCompat.getColor(getApplicationContext(), R.color.md_black_1000), 70));
        }
    }

    @Override
    protected void setStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(ColorPalette.getTransparentColor(
                    ContextCompat.getColor(getApplicationContext(), R.color.md_black_1000), 70));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case READ_EXTERNAL_STORAGE_ID:
                boolean granted = grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED;
                if (granted)
                    new PrefetchAlbumsData().execute(SP.getBoolean(getString(R.string.preference_auto_update_media), false));
                else
                    Toast.makeText(SplashScreen.this, getString(R.string.storage_permission_denied), Toast.LENGTH_LONG).show();
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private class PrefetchAlbumsData extends AsyncTask<Boolean, Boolean, Boolean> {

        @Override
        protected Boolean doInBackground(Boolean... arg0) {
            getAlbums().restoreBackup(getApplicationContext());
            if(getAlbums().dispAlbums.size() == 0) {
                getAlbums().loadAlbums(getApplicationContext(), false);
                return true;
            }
            return false;
        }

        
        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);

            Intent i = new Intent(SplashScreen.this, LFMainActivity.class);
            Bundle b = new Bundle();
            b.putInt(CONTENT, result ? ALBUMS_PREFETCHED : ALBUMS_BACKUP);
            b.putBoolean(PICK_MODE, PICK_INTENT);
            i.putExtras(b);
            if (PICK_INTENT)
                startActivityForResult(i, PICK_MEDIA_REQUEST);
            else {
                startActivity(i);
                finish();
            }
            if(result)
                getAlbums().saveBackup(getApplicationContext());
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
            Intent i = new Intent(SplashScreen.this, LFMainActivity.class);
            Bundle b = new Bundle();
            getAlbums().addAlbum(0, album);
            b.putInt(CONTENT, PHOTOS_PREFETCHED);
            i.putExtras(b);
            startActivity(i);
            finish();
        }
    }
}
