package vn.mbm.phimp.me;

import android.content.Intent;
import android.graphics.Camera;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import vn.mbm.phimp.me.Utilities.BasicCallBack;
import vn.mbm.phimp.me.opencamera.CameraActivity;


import com.xinlan.imageeditlibrary.editimage.EditImageActivity;
import com.xinlan.imageeditlibrary.editimage.utils.BitmapUtils;
import com.xinlan.imageeditlibrary.picchooser.SelectPictureActivity;


import static vn.mbm.phimp.me.Utilities.Constants.CAMERA_BACK_PRESSED;

/**
 * Created by pa1pal on 11/5/17.
 */

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    @BindView(R.id.tabs)
    TextView tabs;

    @BindView(R.id.navigation)
    BottomNavigationView bottomNavigationView;
    BasicCallBack basicCallBack;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        basicCallBack = new BasicCallBack() {
            @Override
            public void callBack(int status, Object data) {
                if (status == CAMERA_BACK_PRESSED)
               bottomNavigationView.setSelectedItemId(R.id.navigation_home);
            }
        };

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.navigation_home:
                tabs.setText(R.string.title_home);
                return true;
            case R.id.navigation_camera:
                Intent i = new Intent(MainActivity.this, CameraActivity.class);
                CameraActivity.setBasicCallBack(basicCallBack);
                startActivity(i);
                return true;
            case R.id.navigation_accounts:
                tabs.setText(R.string.title_account);
                return true;
        }
        return false;
    }


}
