package vn.mbm.phimp.me;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

public class MapImage extends AppCompatActivity {

    private ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_image);

        Bundle bundle = getIntent().getExtras();
        String path = bundle.getString("path");

        imageView = (ImageView)findViewById(R.id.view_image);
        displayImage(path);
    }

    public void displayImage(String path)
    {
        imageView.setImageURI(Uri.parse(path));
    }
}
