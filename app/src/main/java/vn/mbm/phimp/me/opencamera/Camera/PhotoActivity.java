package vn.mbm.phimp.me.opencamera.Camera;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;

import vn.mbm.phimp.me.R;
import vn.mbm.phimp.me.editor.FileUtils;
import vn.mbm.phimp.me.editor.editimage.EditImageActivity;
import vn.mbm.phimp.me.leafpic.activities.SingleMediaActivity;
import vn.mbm.phimp.me.utilities.ActivitySwitchHelper;


public class PhotoActivity extends AppCompatActivity {

    public static String FILE_PATH = "file_path";
    final String REVIEW_ACTION = "com.android.camera.action.REVIEW";

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);
        ImageView imageView = (ImageView)findViewById(R.id.imageView);
        imageView.setImageBitmap(BitmapFactory.decodeFile(FILE_PATH));
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
        Intent intent = new Intent(REVIEW_ACTION, Uri.fromFile(new File(FILE_PATH)));
        intent.setClass(getApplicationContext(), SingleMediaActivity.class);
        this.startActivity(intent);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        ActivitySwitchHelper.setContext(this);
    }
}
