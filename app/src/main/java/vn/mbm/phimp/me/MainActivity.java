package vn.mbm.phimp.me;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import vn.mbm.phimp.me.opencamera.CameraActivity;

/**
 * Created by pa1pal on 11/5/17.
 */

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    @BindView(R.id.tabs)
    TextView tabs;

    @BindView(R.id.navigation)
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.navigation_home:
                tabs.setText(R.string.title_home);
                return true;
            case R.id.navigation_camera:
                Intent i = new Intent(MainActivity.this, CameraActivity.class);
                startActivity(i);
                return true;
            case R.id.navigation_accounts:
                tabs.setText(R.string.title_account);
                return true;
        }
        return false;
    }
}
