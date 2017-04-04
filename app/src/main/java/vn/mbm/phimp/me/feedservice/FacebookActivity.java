package vn.mbm.phimp.me.feedservice;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInstaller;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.service.textservice.SpellCheckerService;
import android.util.Log;
import android.widget.Toast;

import com.facebook.*;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

import vn.mbm.phimp.me.R;
import vn.mbm.phimp.me.database.AccountItem;
import vn.mbm.phimp.me.database.FacebookItem;
import vn.mbm.phimp.me.utils.PrefManager;

/**
 * User: pa1pal
 * Date: 8/26/16
 */
public class FacebookActivity extends Activity {

    Context ctx;

    static Activity activity = new Activity();

    private CallbackManager callbackManager;

    private LoginButton loginButton;

    private static final String PERMISSION = "email";

    SharedPreferences sharedPref;

    SharedPreferences.Editor editor;

    Boolean loginStatus = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(this);
        ctx = this;
        activity = (Activity) this;
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        setContentView(R.layout.facebook_layout);

        callbackManager = CallbackManager.Factory.create();

        loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions(Arrays.asList(
                "public_profile", "email", "user_birthday", "user_friends"));
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                final AccessToken accessToken = loginResult.getAccessToken();
                final String at = loginResult.getAccessToken().getToken();
                final Profile profile = Profile.getCurrentProfile();
                GraphRequest request = GraphRequest.newMeRequest(
                        AccessToken.getCurrentAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                Log.v("LoginActivity", response.toString());

                                try {
                                    String email = object.getString("email");
                                    //String profile_url = object.getString("picture");
                                    String user_id = accessToken.getUserId();
                                    String user_name = accessToken.getUserId(); //The username is no longer available in v2.
                                    String user_fullname = profile.getName();
                                    String profile_url = String.valueOf(profile.getLinkUri());
                                    try {
                                        long account_id = AccountItem.insertAccount(ctx, null, user_fullname, "facebook", "1");
                                        Log.d("ID", String.valueOf(account_id));
                                        if (account_id > 0) {
                                            if (FacebookItem.insertFacebookAccount(ctx, String.valueOf(account_id), at, user_id, user_name, user_fullname, email, profile_url)) {
                                                Toast.makeText(ctx, "Insert account '" + user_fullname + "' (Facebook) SUCCESS!", Toast.LENGTH_LONG).show();
                                            } else {
                                                Toast.makeText(ctx, "Insert account '" + user_fullname + "' (Facebook) FAIL!", Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        Log.e("webkit", "Facebook Service - " + e.toString());
                                    }


                                    PrefManager.putBoolean(PrefManager.LOGIN_STATUS, true);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,first_name,last_name,picture,name,email,gender,birthday");
                request.setParameters(parameters);
                request.executeAsync();
                //check=true;
                activity.finish();
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onResume() {
        super.onResume();
        AppEventsLogger.activateApp(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        AppEventsLogger.deactivateApp(this);
    }
}
