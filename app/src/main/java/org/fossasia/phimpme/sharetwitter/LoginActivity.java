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
package org.fossasia.phimpme.sharetwitter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import org.fossasia.phimpme.R;
import org.fossasia.phimpme.base.ThemedActivity;
import org.fossasia.phimpme.leafpic.util.AlertDialogsHelper;

import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

public class LoginActivity extends ThemedActivity {

	public static final int TWITTER_LOGIN_RESULT_CODE_SUCCESS = 1111;
	public static final int TWITTER_LOGIN_RESULT_CODE_FAILURE = 2222;

	private static final String TAG = "LoginActivity";

	private WebView twitterLoginWebView;
	private static String twitterConsumerKey;
	private static String twitterConsumerSecret;

	private static Twitter twitter;
	private static RequestToken requestToken;
	private AlertDialog dialog;
	private AlertDialog.Builder progressDialog;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_twitter_login);
		twitterConsumerKey = getResources().getString(R.string.twitter_consumer_key);
		twitterConsumerSecret = getResources().getString(R.string.twitter_consumer_secret);

		if(twitterConsumerKey == null || twitterConsumerSecret == null){
			Log.e(TAG, "ERROR: Consumer Key and Consumer Secret required!");
			LoginActivity.this.setResult(TWITTER_LOGIN_RESULT_CODE_FAILURE);
			LoginActivity.this.finish();
		}


		progressDialog = new AlertDialog.Builder(LoginActivity.this, getDialogStyle());
		dialog = AlertDialogsHelper.getProgressDialog(LoginActivity.this, progressDialog,
				getString(R.string.authenticating_your_app_message), getString(R.string.please_wait));
		dialog.show();

		twitterLoginWebView = (WebView)findViewById(R.id.twitterLoginWebView);
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
		new Thread(new Runnable() {
			@Override
			public void run() {
				String verifier = uri.getQueryParameter(AppConstant.IEXTRA_OAUTH_VERIFIER);
				try { 
					SharedPreferences sharedPrefs = getSharedPreferences(AppConstant.SHARED_PREF_NAME, Context.MODE_PRIVATE);
					AccessToken accessToken = twitter.getOAuthAccessToken(requestToken, verifier);
					Editor e = sharedPrefs.edit();
					e.putString(AppConstant.SHARED_PREF_KEY_TOKEN, accessToken.getToken()); 
					e.putString(AppConstant.SHARED_PREF_KEY_SECRET, accessToken.getTokenSecret()); 
					e.commit();

					Log.d(TAG, "TWITTER LOGIN SUCCESS ----TOKEN " + accessToken.getToken());
					Log.d(TAG, "TWITTER LOGIN SUCCESS ----TOKEN SECRET " + accessToken.getTokenSecret());
					LoginActivity.this.setResult(TWITTER_LOGIN_RESULT_CODE_SUCCESS);
				} catch (Exception e) {
					e.printStackTrace();
					if(e.getMessage() != null){
						Log.e(TAG, e.getMessage());

					}else{
						Log.e(TAG, "ERROR: Twitter callback failed");
					}
					LoginActivity.this.setResult(TWITTER_LOGIN_RESULT_CODE_FAILURE);
				}
				LoginActivity.this.finish();
			}
		}).start();
	}


	public static boolean isActive(Context ctx) {
		SharedPreferences sharedPrefs = ctx.getSharedPreferences(AppConstant.SHARED_PREF_NAME, Context.MODE_PRIVATE);
		return sharedPrefs.getString(AppConstant.SHARED_PREF_KEY_TOKEN, null) != null;
	}

	public static void logOutOfTwitter(Context ctx){
		SharedPreferences sharedPrefs = ctx.getSharedPreferences(AppConstant.SHARED_PREF_NAME, Context.MODE_PRIVATE);
		Editor e = sharedPrefs.edit();
		e.putString(AppConstant.SHARED_PREF_KEY_TOKEN, null); 
		e.putString(AppConstant.SHARED_PREF_KEY_SECRET, null); 
		e.commit();
	}

	public static String getAccessToken(Context ctx){
		SharedPreferences sharedPrefs = ctx.getSharedPreferences(AppConstant.SHARED_PREF_NAME, Context.MODE_PRIVATE);
		return sharedPrefs.getString(AppConstant.SHARED_PREF_KEY_TOKEN, null);
	}

	public static String getAccessTokenSecret(Context ctx){
		SharedPreferences sharedPrefs = ctx.getSharedPreferences(AppConstant.SHARED_PREF_NAME, Context.MODE_PRIVATE);
		return sharedPrefs.getString(AppConstant.SHARED_PREF_KEY_SECRET, null);
	}

	private void askOAuth() {
		ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();
		configurationBuilder.setOAuthConsumerKey(twitterConsumerKey);
		configurationBuilder.setOAuthConsumerSecret(twitterConsumerSecret);
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
							Toast.makeText(LoginActivity.this, errorString, Toast.LENGTH_SHORT).show();
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

}
