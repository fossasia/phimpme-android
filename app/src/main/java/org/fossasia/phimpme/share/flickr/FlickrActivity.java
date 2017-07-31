package org.fossasia.phimpme.share.flickr;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import org.fossasia.phimpme.base.ThemedActivity;
import org.fossasia.phimpme.data.local.AccountDatabase;
import org.fossasia.phimpme.share.flickr.tasks.GetOAuthTokenTask;
import org.fossasia.phimpme.share.flickr.tasks.OAuthTask;
import org.fossasia.phimpme.share.flickr.tasks.UploadPhotoTask;
import com.googlecode.flickrjandroid.oauth.OAuth;
import com.googlecode.flickrjandroid.oauth.OAuthToken;
import com.googlecode.flickrjandroid.people.User;

import java.io.InputStream;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

import static org.fossasia.phimpme.data.local.AccountDatabase.AccountName.FLICKR;

public class FlickrActivity extends ThemedActivity {
	public static final String CALLBACK_SCHEME = "flickrj-android-sample-oauth";
	private static InputStream photoStream;
	Handler h = new Handler();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		new Thread() {
			public void run() {
				h.post(init);
			};
		}.start();
	}

	Runnable init = new Runnable() {

		@Override
		public void run() {
			OAuth oauth = getOAuthToken();
			if (oauth == null || oauth.getUser() == null) {
				OAuthTask task = new OAuthTask(getContext());
				task.execute();
			} else {
				if (FlickrHelper.getInstance().getFileName() != null)
					load(oauth);
			}
		}
	};

	private void load(OAuth oauth) {
		if (oauth != null) {

			UploadPhotoTask taskUpload = new UploadPhotoTask(this);
			taskUpload.setOnUploadDone(new UploadPhotoTask.onUploadDone() {
				@Override
				public void onComplete() {
					finish();
				}
			});
			taskUpload.execute(oauth);
		}
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
			Toast.makeText(this, "Authorization failed",
					Toast.LENGTH_LONG).show();
		} else {
			User user = result.getUser();
			OAuthToken token = result.getToken();
			if (user == null || user.getId() == null || token == null
					|| token.getOauthToken() == null
					|| token.getOauthTokenSecret() == null) {
				Toast.makeText(this, "Authorization failed",
						Toast.LENGTH_LONG).show();
				return;
			}
			String message = "Login Success";
			Toast.makeText(this, message, Toast.LENGTH_LONG).show();
			saveOAuthToken(user.getUsername(), user.getId(),
					token.getOauthToken(), token.getOauthTokenSecret());
			load(result);
		}
	}

	public OAuth getOAuthToken() {
		Realm realm = Realm.getDefaultInstance();
		AccountDatabase account;
		RealmQuery<AccountDatabase> query = realm.where(AccountDatabase.class);
		query.equalTo("name", FLICKR.toString());
		RealmResults<AccountDatabase> result = query.findAll();
		if (result.size() != 0) {
			account = result.get(0);
			String oauthTokenString = account.getToken();
			String tokenSecret = account.getTokenSecret();

			if (oauthTokenString == null && tokenSecret == null) {
				return null;
			}
			OAuth oauth = new OAuth();
			String userName = account.getUsername();
			String userId = account.getUserId();
			if (userId != null) {
				User user = new User();
				user.setUsername(userName);
				user.setId(userId);
				oauth.setUser(user);
			}
			OAuthToken oauthToken = new OAuthToken();
			oauth.setToken(oauthToken);
			oauthToken.setOauthToken(oauthTokenString);
			oauthToken.setOauthTokenSecret(tokenSecret);
			return oauth;
		}else
			return null;
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
		if( result.size() == 0 )
			account = realm.createObject(AccountDatabase.class,FLICKR.toString());
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
}