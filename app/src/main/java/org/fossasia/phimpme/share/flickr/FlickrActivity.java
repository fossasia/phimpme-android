package org.fossasia.phimpme.share.flickr;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;

import com.googlecode.flickrjandroid.Flickr;
import com.googlecode.flickrjandroid.auth.Permission;
import com.googlecode.flickrjandroid.oauth.OAuth;
import com.googlecode.flickrjandroid.oauth.OAuthToken;
import com.googlecode.flickrjandroid.people.User;

import org.fossasia.phimpme.R;
import org.fossasia.phimpme.base.ThemedActivity;
import org.fossasia.phimpme.data.local.AccountDatabase;
import org.fossasia.phimpme.gallery.util.AlertDialogsHelper;
import org.fossasia.phimpme.share.flickr.tasks.GetOAuthTokenTask;
import org.fossasia.phimpme.utilities.BasicCallBack;
import org.fossasia.phimpme.utilities.Constants;
import org.fossasia.phimpme.utilities.SnackBarHandler;

import java.net.URL;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

import static org.fossasia.phimpme.data.local.AccountDatabase.AccountName.FLICKR;
import static org.fossasia.phimpme.share.flickr.FlickrHelper.getOAuthToken;

public class FlickrActivity extends ThemedActivity {
    public static final String CALLBACK_SCHEME = "flickrj-android-sample-oauth";
    public static BasicCallBack basicCallBack;

    @BindView(R.id.login_parent)
    View parent;

    public static void setBasicCallBack(BasicCallBack basicCallBack) {
        FlickrActivity.basicCallBack = basicCallBack;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account_login_activity);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        OAuth oauth = getOAuthToken();
        if (oauth == null || oauth.getUser() == null) {
            OAuthTask task = new OAuthTask(getContext());
            task.execute();
        }
        ButterKnife.bind(this);


    }


    @Override
    protected void onNewIntent(Intent intent) {
        // this is very important, otherwise you would get a null Scheme in the
        // onResume later on.
        setIntent(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        Intent intent = getIntent();
        String scheme = intent.getScheme();
        OAuth savedToken = getOAuthToken();

        if (CALLBACK_SCHEME.equals(scheme)
                && (savedToken == null || savedToken.getUser() == null)) {
            Uri uri = intent.getData();
            String query = uri.getQuery();
            String[] data = query.split("&");
            if (data != null && data.length == 2) {
                String oauthToken = data[0].substring(data[0].indexOf("=") + 1);
                String oauthVerifier = data[1].substring(data[1].indexOf("=") + 1);
                OAuth oauth = getOAuthToken();
                if (oauth != null && oauth.getToken() != null
                        && oauth.getToken().getOauthTokenSecret() != null) {
                    GetOAuthTokenTask task = new GetOAuthTokenTask(this);
                    task.execute(oauthToken, oauth.getToken()
                            .getOauthTokenSecret(), oauthVerifier);
                }
            }
        }
    }

    public void onOAuthDone(OAuth result) {
        if (result == null) {
           SnackBarHandler.show(parent, getString(R.string.auth_failed));
        } else {
            User user = result.getUser();
            OAuthToken token = result.getToken();
            if (user == null || user.getId() == null || token == null
                    || token.getOauthToken() == null
                    || token.getOauthTokenSecret() == null) {
                SnackBarHandler.show(parent, getString(R.string.auth_failed));
                return;
            }
            basicCallBack.callBack(Constants.SUCCESS, null);
            saveOAuthToken(user.getUsername(), user.getId(),
                    token.getOauthToken(), token.getOauthTokenSecret());
            finish();
        }
    }

    /**
     * This method is called two times during the authentication. In the first time, only tokenSecret will
     * have some value and others will be null and in the second time this method will called with all the
     * details of the account. So after the two calls of this function the account abject will be
     * fully populated in realm database.
     */
    public void saveOAuthToken(String userName, String userId, String token, String tokenSecret) {
        Realm realm = Realm.getDefaultInstance();
        RealmQuery<AccountDatabase> query = realm.where(AccountDatabase.class);
        query.equalTo("name", FLICKR.toString());
        RealmResults<AccountDatabase> result = query.findAll();
        AccountDatabase account;
        realm.beginTransaction();
        if (result.size() == 0)
            account = realm.createObject(AccountDatabase.class, FLICKR.toString());
        else
            account = result.first();

        account.setToken(token);
        account.setTokenSecret(tokenSecret);
        account.setUsername(userName);
        account.setUserId(userId);
        realm.commitTransaction();
    }

    private Context getContext() {
        return this;

    }

    public class OAuthTask extends AsyncTask<Void, Integer, String> {

        private final Uri OAUTH_CALLBACK_URI = Uri
                .parse(FlickrActivity.CALLBACK_SCHEME + "://oauth"); //$NON-NLS-1$
        private Context mContext;
        private AlertDialog dialog;

        public OAuthTask(Context context) {
            super();
            this.mContext = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            AlertDialog.Builder progressDialog = new AlertDialog.Builder(getContext(), getDialogStyle());
            dialog = AlertDialogsHelper.getProgressDialog(FlickrActivity.this, progressDialog, getContext().getString(R.string.authenticating_your_app_message), getContext().getString(R.string.please_wait));
            dialog.show();
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                Flickr f = FlickrHelper.getInstance().getFlickr();
                OAuthToken oauthToken = f.getOAuthInterface().getRequestToken(
                        OAUTH_CALLBACK_URI.toString());
                Log.i("kakakakakaka", oauthToken.getOauthToken().toString());
                //saveTokenSecrent(oauthToken.getOauthToken(), oauthToken.getOauthTokenSecret());
                URL oauthUrl = f.getOAuthInterface().buildAuthenticationUrl(
                        Permission.WRITE, oauthToken);
                return oauthUrl.toString();
            } catch (Exception e) {
                Log.e("lllllllllllllll", "moo");
                return "error:" + e.getMessage(); //$NON-NLS-1$


            }
        }

        private void saveTokenSecrent(String token, String tokenSecret) {
            FlickrActivity act = (FlickrActivity) mContext;

            act.saveOAuthToken(null, null, token, tokenSecret);
        }

        @Override
        protected void onPostExecute(String result) {
            if (dialog != null) {
                dialog.dismiss();
            }
            if (result != null && !result.startsWith("error")) {
                mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri
                        .parse(result)));
            } else {
               SnackBarHandler.show(parent, result);
            }
        }
    }
}