package vn.mbm.phimp.me;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import vn.mbm.phimp.me.Views.CustomProgressDialog;


/**
 * Created by dynamitechetan on 14/03/2017.
 */
public class ImagePickerBase extends AppCompatActivity {

    public CustomProgressDialog progressDialog;
    public static final int SHOW_PROGRESS = 1;
    public static final int DISMISS_PROGRESS = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    public void showProgressDialog(String message){
        if(progressDialog == null)
            progressDialog = new CustomProgressDialog(this);
        progressDialog.setMessage(message);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
    }

    public void dismissProgressDialog(){
        if(progressDialog != null)
            if(progressDialog.isShowing())
                progressDialog.dismiss();
    }

    public boolean hasStoragePermission(Context context) {
        int writePermissionCheck = ContextCompat.checkSelfPermission(context,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int readPermissionCheck = ContextCompat.checkSelfPermission(context,
                Manifest.permission.READ_EXTERNAL_STORAGE);
        return !(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && (writePermissionCheck == PackageManager.PERMISSION_DENIED
                || readPermissionCheck == PackageManager.PERMISSION_DENIED));
    }

    public boolean hasCameraPermission(Context context){
        int cameraPermissionCheck = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA);
        return !(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && cameraPermissionCheck == PackageManager.PERMISSION_DENIED);
    }

    public boolean hasCallPermission(Context context){
        int callPermissionCheck = ContextCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE);
        return !(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && callPermissionCheck == PackageManager.PERMISSION_DENIED);
    }

    public void requestCameraPermissions(Activity activity, int requestCode){
        ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CAMERA},
                requestCode);
    }

    public void requestStoragePermissions(Activity activity, int requestCode) {
        int hasReadPermission = ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE);
        int hasWritePermission = ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        List<String> permissions = new ArrayList<>();
        if( hasReadPermission != PackageManager.PERMISSION_GRANTED ) {
            permissions.add( Manifest.permission.READ_EXTERNAL_STORAGE );
        }

        if( hasWritePermission != PackageManager.PERMISSION_GRANTED ) {
            permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

        if( !permissions.isEmpty() ) {
            ActivityCompat.requestPermissions(activity, permissions.toArray(new String[permissions.size()]), requestCode);
        }
    }

    public void requestCallPermissions(Activity activity, int requestCode) {
        int hasCallPermission = ContextCompat.checkSelfPermission(activity, Manifest.permission.CALL_PHONE);
        List<String> permissions = new ArrayList<>();
        if( hasCallPermission != PackageManager.PERMISSION_GRANTED ) {
            permissions.add( Manifest.permission.CALL_PHONE );
        }

        if( !permissions.isEmpty() ) {
            ActivityCompat.requestPermissions(activity, permissions.toArray(new String[permissions.size()]), requestCode);
        }
    }

    public boolean validateGrantedPermissions(int[] grantResults) {
        boolean isGranted = true;
        if (grantResults != null && grantResults.length > 0) {
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                    isGranted = false;
                    break;
                }
            }
            return isGranted;
        } else {
            isGranted = false;
            return isGranted;
        }
    }

}
