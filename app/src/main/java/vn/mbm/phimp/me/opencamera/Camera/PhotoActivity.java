package vn.mbm.phimp.me.opencamera.Camera;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;


import com.mikepenz.google_material_typeface_library.GoogleMaterial;


import uk.co.senab.photoview.PhotoView;
import vn.mbm.phimp.me.R;
import vn.mbm.phimp.me.base.ThemedActivity;
import vn.mbm.phimp.me.editor.FileUtils;
import vn.mbm.phimp.me.editor.editimage.EditImageActivity;
import vn.mbm.phimp.me.shareActivity;
import vn.mbm.phimp.me.utilities.ActivitySwitchHelper;

import static vn.mbm.phimp.me.shareActivity.EXTRA_OUTPUT;


public class PhotoActivity extends ThemedActivity {

    public static String FILE_PATH = "file_path";
    final String REVIEW_ACTION = "com.android.camera.action.REVIEW";
    Toolbar toolbar;

    public static void start(Activity context, final String editImagePath, final int requestCode) {
        if (TextUtils.isEmpty(editImagePath)) {
            Toast.makeText(context, R.string.no_choose, Toast.LENGTH_SHORT).show();
            return;
        }

        Intent it = new Intent(context, PhotoActivity.class);
        context.startActivityForResult(it, requestCode);
        FILE_PATH = editImagePath;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setBackgroundColor(getPrimaryColor());
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(getToolbarIcon(GoogleMaterial.Icon.gmd_arrow_back));
        setStatusBarColor();
        PhotoView imageView = (PhotoView) findViewById(R.id.imageView);
        imageView.setImageBitmap(BitmapFactory.decodeFile(FILE_PATH));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               deleteFile(v);
            }
        });
    }

    public void deleteFile(View v){
            String[] projection = {MediaStore.Images.Media._ID};

            // Match on the file path
            String selection = MediaStore.Images.Media.DATA + " = ?";
            String[] selectionArgs = new String[]{FILE_PATH};

            // Query for the ID of the media matching the file path
            Uri queryUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            ContentResolver contentResolver = getContentResolver();
            Cursor c = contentResolver.query(queryUri, projection, selection, selectionArgs, null);
            if (c.moveToFirst()) {
                // We found the ID. Deleting the item via the content provider will also remove the file
                long id = c.getLong(c.getColumnIndexOrThrow(MediaStore.Images.Media._ID));
                Uri deleteUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);
                contentResolver.delete(deleteUri, null, null);
            } else {
                // File not found in media store DB
            }
            c.close();
            finish();
        }

    public void editImage(View v){
        EditImageActivity.start(this, FILE_PATH, FileUtils.genEditFile().getAbsolutePath(),1);
        finish();
    }

    public void saveOriginal(View v){
       finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_photoactivity, menu);
        Drawable shareIcon = getResources().getDrawable(R.drawable.ic_share_minimal, getTheme());
        shareIcon.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
        menu.findItem(R.id.menu_share).setIcon(shareIcon);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (R.id.menu_share == item.getItemId()){
            Intent share = new Intent(PhotoActivity.this,shareActivity.class);
            share.putExtra(EXTRA_OUTPUT,FILE_PATH);
            startActivity(share);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        ActivitySwitchHelper.setContext(this);
    }
}
