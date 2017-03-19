package vn.mbm.phimp.me;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.io.File;

/**
 * @author heysadboy 19/03/2017
 */


public class MapImageActivity extends AppCompatActivity {

    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_image);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_map_image);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Bundle bundle = getIntent().getExtras();
        String path = bundle.getString("path");

        imageView = (ImageView) findViewById(R.id.view_image);
        displayImage(path);
    }

    public void displayImage(String path) {
        File file = new File(path);
        Uri imageUri = Uri.fromFile(file);
        Glide.with(this).load(imageUri).into(imageView);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
