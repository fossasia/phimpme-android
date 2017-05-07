package vn.mbm.phimp.me.wordpress;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Window;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;

import org.wordpress.android.fluxc.store.SiteStore;

import javax.inject.Inject;

import vn.mbm.phimp.me.MyApplication;
import vn.mbm.phimp.me.R;

/**
 * Created by rohanagarwal94 on 6/4/17.
 */

public class SignInActivity extends AppCompatActivity implements ConnectionCallbacks, OnConnectionFailedListener
        {

    public static final int REQUEST_CODE = 5000;

    public static final String EXTRA_IS_AUTH_ERROR = "EXTRA_IS_AUTH_ERROR";

    private ProgressDialog mProgressDialog;

    @Inject SiteStore mSiteStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((MyApplication) getApplication()).component().inject(this);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.welcome_activity);

        if (savedInstanceState == null) {
            addSignInFragment();
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        cancelProgressDialog();
    }

    private void cancelProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.cancel();
        }
    }

    protected void addSignInFragment() {
        SignInFragment signInFragment = new SignInFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, signInFragment, SignInFragment.TAG);
        fragmentTransaction.commit();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d("Connection Suspended", String.valueOf(connectionResult));
    }

    @Override
    public void onConnected(Bundle bundle) {
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d("Connection Suspended", String.valueOf(i));
    }
}
