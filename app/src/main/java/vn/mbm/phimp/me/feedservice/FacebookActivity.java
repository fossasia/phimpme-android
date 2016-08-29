package vn.mbm.phimp.me.feedservice;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.facebook.*;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONObject;

import vn.mbm.phimp.me.R;
import vn.mbm.phimp.me.database.AccountItem;
import vn.mbm.phimp.me.database.FacebookItem;
import vn.mbm.phimp.me.services.FacebookServices;

/**
 * User: pa1pal
 * Date: 8/26/16
 */
public class FacebookActivity extends Activity {

    Context ctx;
    static Activity activity = new Activity();
    private CallbackManager callbackManager;
    private LoginButton loginButton;
    private FacebookCallback<LoginResult> callback = new FacebookCallback<LoginResult>() {
        @Override
        public void onSuccess(LoginResult loginResult) {

            AccessToken accessToken = loginResult.getAccessToken();
            Profile profile = Profile.getCurrentProfile();



            try
            {
                //String access_token_change= FacebookServices.change_access_token(accessToken);

                Log.d("webkit", "Access_token_change: " + accessToken);

                //String user_info = FacebookServices.getUserInfo(access_token_change);
                //String user_info = FacebookServices.getUserInfo(access_token);
                Log.d("webkit", "User Info:" + profile.getId());

                String user_id = profile.getId();
                String user_name = "pa1pal";
                String user_fullname = profile.getName();
                String profile_url = String.valueOf(profile.getLinkUri());
                String email = "pawan";

                long account_id = AccountItem.insertAccount(ctx, null, user_fullname, "facebook", "1");
                Log.d("ID",String.valueOf(account_id));
                if (account_id > 0)
                {
                    if (FacebookItem.insertFacebookAccount(ctx, String.valueOf(account_id), String.valueOf(accessToken), user_id, user_name, user_fullname, email, profile_url))
                    //if (FacebookItem.insertFacebookAccount(ctx, String.valueOf(account_id), access_token, user_id, user_name, user_fullname, email, profile_url))
                    {
                        Toast.makeText(ctx, "Insert account '" + user_fullname + "' (Facebook) SUCCESS!", Toast.LENGTH_LONG).show();
                    }
                    else
                    {
                        Toast.makeText(ctx, "Insert account '" + user_fullname + "' (Facebook) FAIL!", Toast.LENGTH_LONG).show();
                    }
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
                Log.e("webkit", "Facebook Service - " + e.toString());
            }
            //check=true;
            //activity.finish();
        }

        @Override
        public void onCancel() {

        }

        @Override
        public void onError(FacebookException error) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ctx = this;
        activity = (Activity) this;
        setContentView(R.layout.facebook_layout);

        callbackManager = CallbackManager.Factory.create();

        loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions("email");
        loginButton.registerCallback(callbackManager, callback);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
}
