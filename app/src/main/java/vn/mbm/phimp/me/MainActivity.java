package vn.mbm.phimp.me;

import android.Manifest;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.Toast;

import com.vistrav.ask.Ask;

import butterknife.BindView;
import butterknife.ButterKnife;
import vn.mbm.phimp.me.Fragments.Gallery;

/**
 * Created by pa1pal on 11/5/17.
 */

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener{
/*
    @BindView(R.id.tabs)
    TextView tabs;*/

    @BindView(R.id.navigation)
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Ask.on(this)
                .forPermissions(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA
                ).go();

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        if (null != (findViewById(R.id.content))) {
            Gallery gallery = Gallery.getInstance();
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.content, gallery)
                    .commit();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.navigation_home:
                Gallery gallery = Gallery.getInstance();
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.content,gallery)
                        .commit();
                return true;
            case R.id.navigation_camera:
              //  tabs.setText(R.string.title_camera);
                return true;
            case R.id.navigation_accounts:
                //tabs.setText(R.string.title_account);
                return true;
        }
        return false;
    }
}
