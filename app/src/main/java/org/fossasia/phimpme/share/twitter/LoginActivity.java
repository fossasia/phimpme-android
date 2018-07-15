/*
 * Copyright 2013 - learnNcode (learnncode@gmail.com)
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package org.fossasia.phimpme.share.twitter;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import org.fossasia.phimpme.R;
import org.fossasia.phimpme.base.ThemedActivity;
import org.fossasia.phimpme.data.local.AccountDatabase;
import org.fossasia.phimpme.gallery.util.AlertDialogsHelper;
import org.fossasia.phimpme.utilities.SnackBarHandler;
import org.fossasia.phimpme.utilities.BasicCallBack;
import org.fossasia.phimpme.utilities.Constants;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

import static org.fossasia.phimpme.data.local.AccountDatabase.AccountName.TWITTER;
import static org.fossasia.phimpme.utilities.Constants.TWITTER_CONSUMER_KEY;
import static org.fossasia.phimpme.utilities.Constants.TWITTER_CONSUMER_SECRET;

public class LoginActivity extends ThemedActivity {

	public static final int TWITTER_LOGIN_RESULT_CODE_FAILURE = 2222;

	private static final String TAG = "LoginActivity";

	@BindView(R.id.twitterLoginWebView)
	WebView twitterLoginWebView;

	@BindView(R.id.login_parent)
	View parentView;


	private static Twitter twitter;
	private static RequestToken requestToken;
	private AlertDialog dialog;
	private AlertDialog.Builder progressDialog;
	private static BasicCallBack twitterCallBack;
    private Uri uri;
    private boolean mTwitterAuthDone = false;

	public static void setBasicCallBack(BasicCallBack basicCallBack){
		twitterCallBack = basicCallBack;
	}


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.account_login_activity);
		ButterKnife.bind(this);


		if(TWITTER_CONSUMER_KEY == null || TWITTER_CONSUMER_SECRET == null){
			Log.e(TAG, "ERROR: Consumer Key and Consumer Secret required!");
			LoginActivity.this.setResult(TWITTER_LOGIN_RESULT_CODE_FAILURE);
			LoginActivity.this.finish();
		}
		progressDialog = new AlertDialog.Builder(LoginActivity.this, getDialogStyle());
		dialog = AlertDialogsHelper.getProgressDialog(LoginActivity.this, progressDialog,
				getString(R.string.authenticating_your_app_message), getString(R.string.please_wait));
		dialog.show();

		twitterLoginWebView.setBackgroundColor(Color.TRANSPARENT);
		twitterLoginWebView.setWebViewClient( new WebViewClient(){
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url){

				if( url.contains(AppConstant.TWITTER_CALLBACK_URL)){
					Uri uri = Uri.parse(url);
					LoginActivity.this.saveAccessTokenAndFinish(uri);
					return true;
				}
				return false;
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);

				if(dialog != null){
                    dialog.dismiss();
				}
			}

			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				super.onPageStarted(view, url, favicon);

				if(dialog != null){
                    dialog.show();
				}
			}
		});

		Log.d(TAG, "Authorize....");
		askOAuth();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		if(dialog != null) {
            dialog.dismiss();
		}
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	private void saveAccessTokenAndFinish(final Uri uri){
        this.uri = uri;
        new SaveTokenAsync().execute();
	}

    private class SaveTokenAsync extends AsyncTask<Void, Void, Void> {
        Bundle bundle;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            String verifier = uri.getQueryParameter(AppConstant.IEXTRA_OAUTH_VERIFIER);
            try {
				if (verifier != null) {
					AccessToken accessToken = twitter.getOAuthAccessToken(requestToken, verifier);
					bundle= new Bundle();
					bundle.putString(getString(R.string.auth_token), accessToken.getToken());
					bundle.putString(getString(R.string.auth_username),accessToken.getScreenName());
					bundle.putString(getString(R.string.auth_secret),accessToken.getTokenSecret());
				}
				mTwitterAuthDone = true;
            } catch (TwitterException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            //twitterCallBack.callBack(Constants.SUCCESS, bundle);
            if(mTwitterAuthDone){
            	addToAccountsRealmDatabase(bundle);
			}
            dialog.dismiss();
            finish();
        }
    }

    private void addToAccountsRealmDatabase(Bundle bundle){
		Realm realm = Realm.getDefaultInstance();
		SnackBarHandler.show(parentView, getString(R.string.account_logged_twitter));
		if (bundle instanceof Bundle) {
			Bundle bundle2 = bundle;
			realm.beginTransaction();
			AccountDatabase account = realm.createObject(AccountDatabase.class, TWITTER.toString());
			account.setAccountname(TWITTER);
			account.setUsername(bundle2.getString(getString(R.string.auth_username)));
			account.setToken(bundle2.getString(getString(R.string.auth_token)));
			account.setSecret(bundle2.getString(getString(R.string.auth_secret)));
			realm.commitTransaction();
		}
	}


	private void askOAuth() {
		ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();
		configurationBuilder.setOAuthConsumerKey(TWITTER_CONSUMER_KEY);
		configurationBuilder.setOAuthConsumerSecret(TWITTER_CONSUMER_SECRET);
		Configuration configuration = configurationBuilder.build();
		twitter = new TwitterFactory(configuration).getInstance();

		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					requestToken = twitter.getOAuthRequestToken(AppConstant.TWITTER_CALLBACK_URL);
				} catch (Exception e) {
					final String errorString = e.toString();
					LoginActivity.this.runOnUiThread(new Runnable() {
						@Override
						public void run() {
                            dialog.dismiss();
							SnackBarHandler.show(parentView,errorString);
							finish();
						}
					});
					return;
				}

				LoginActivity.this.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						twitterLoginWebView.loadUrl(requestToken.getAuthenticationURL());
					}
				});
			}
		}).start();
	}

	@Override public void onBackPressed() {
		super.onBackPressed();
		if(dialog.isShowing()){
			dialog.dismiss();
		}
		finish();
	}
}
