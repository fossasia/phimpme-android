package org.fossasia.phimpme.share.flickr.tasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.widget.Toast;

import org.fossasia.phimpme.share.flickr.FlickrHelper;
import org.fossasia.phimpme.share.flickr.FlickrActivity;
import com.googlecode.flickrjandroid.Flickr;
import com.googlecode.flickrjandroid.auth.Permission;
import com.googlecode.flickrjandroid.oauth.OAuthToken;

import java.net.URL;

public class OAuthTask extends AsyncTask<Void, Integer, String> {

	private static final Uri OAUTH_CALLBACK_URI = Uri
			.parse(FlickrActivity.CALLBACK_SCHEME + "://oauth"); //$NON-NLS-1$

	private Context mContext;

	private ProgressDialog mProgressDialog;

	public OAuthTask(Context context) {
		super();
		this.mContext = context;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		mProgressDialog = ProgressDialog.show(mContext,
				"", "Generating the authorization request..."); //$NON-NLS-1$ //$NON-NLS-2$
		mProgressDialog.setCanceledOnTouchOutside(true);
		mProgressDialog.setCancelable(true);
		mProgressDialog.setOnCancelListener(new OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dlg) {
				OAuthTask.this.cancel(true);
			}
		});
	}

	@Override
	protected String doInBackground(Void... params) {
		try {
			Flickr f = FlickrHelper.getInstance().getFlickr();
			OAuthToken oauthToken = f.getOAuthInterface().getRequestToken(
					OAUTH_CALLBACK_URI.toString());
			saveTokenSecrent(oauthToken.getOauthTokenSecret());
			URL oauthUrl = f.getOAuthInterface().buildAuthenticationUrl(
					Permission.WRITE, oauthToken);
			return oauthUrl.toString();
		} catch (Exception e) {
			//			logger.error("Error to oauth", e); //$NON-NLS-1$
			return "error:" + e.getMessage(); //$NON-NLS-1$
		}
	}

	private void saveTokenSecrent(String tokenSecret) {
		FlickrActivity act = (FlickrActivity) mContext;
		act.saveOAuthToken(null, null, null, tokenSecret);
	}

	@Override
	protected void onPostExecute(String result) {
		if (mProgressDialog != null) {
			mProgressDialog.dismiss();
		}
		if (result != null && !result.startsWith("error")) {
			mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri
					.parse(result)));
		} else {
			Toast.makeText(mContext, result, Toast.LENGTH_LONG).show();
		}
	}

}
