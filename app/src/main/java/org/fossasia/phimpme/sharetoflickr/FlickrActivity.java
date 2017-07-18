package org.fossasia.phimpme.sharetoflickr;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import org.fossasia.phimpme.base.ThemedActivity;
import org.fossasia.phimpme.sharetoflickr.tasks.GetOAuthTokenTask;
import org.fossasia.phimpme.sharetoflickr.tasks.OAuthTask;
import org.fossasia.phimpme.sharetoflickr.tasks.UploadPhotoTask;
import com.googlecode.flickrjandroid.oauth.OAuth;
import com.googlecode.flickrjandroid.oauth.OAuthToken;
import com.googlecode.flickrjandroid.people.User;

import java.io.InputStream;

public class FlickrActivity extends ThemedActivity {
	public static final String CALLBACK_SCHEME = "flickrj-android-sample-oauth";
	public static final String PREFS_NAME = "flickrj-android-sample-pref";
	public static final String KEY_OAUTH_TOKEN = "flickrj-android-oauthToken";
	public static final String KEY_TOKEN_SECRET = "flickrj-android-tokenSecret";
	public static final String KEY_USER_NAME = "flickrj-android-userName";
	public static final String KEY_USER_ID = "flickrj-android-userId";
	private static InputStream photoStream;
	static String filename = null;
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
				if (filename != null)
					load(oauth);
			}
		}
	};

	private void load(OAuth oauth) {
		if (oauth != null) {

			UploadPhotoTask taskUpload = new UploadPhotoTask(this,filename);
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
		// Restore preferences
		SharedPreferences settings = getSharedPreferences(PREFS_NAME,
				Context.MODE_PRIVATE);
		String oauthTokenString = settings.getString(KEY_OAUTH_TOKEN, null);
		String tokenSecret = settings.getString(KEY_TOKEN_SECRET, null);
		if (oauthTokenString == null && tokenSecret == null) {
			return null;
		}
		OAuth oauth = new OAuth();
		String userName = settings.getString(KEY_USER_NAME, null);
		String userId = settings.getString(KEY_USER_ID, null);
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
	}

	public void saveOAuthToken(String userName, String userId, String token, String tokenSecret) {

		SharedPreferences sp = getSharedPreferences(PREFS_NAME,
				Context.MODE_PRIVATE);
		Editor editor = sp.edit();
		editor.putString(KEY_OAUTH_TOKEN, token);
		editor.putString(KEY_TOKEN_SECRET, tokenSecret);
		editor.putString(KEY_USER_NAME, userName);
		editor.putString(KEY_USER_ID, userId);
		editor.apply();
	}

	public static void setInputStream(InputStream inputStream){
		photoStream = inputStream;
	}

	public static InputStream getInputStream(){
		return photoStream;
	}

	public static void setFilename(String file){
		filename = file;
	}

	private Context getContext() {
		return this;

	}
}