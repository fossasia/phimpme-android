package org.fossasia.phimpme.share.flickr.tasks;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;
import org.fossasia.phimpme.share.flickr.FlickrHelper;
import org.fossasia.phimpme.share.flickr.FlickrActivity;
import com.googlecode.flickrjandroid.Flickr;
import com.googlecode.flickrjandroid.oauth.OAuth;
import com.googlecode.flickrjandroid.oauth.OAuthToken;
import com.googlecode.flickrjandroid.uploader.UploadMetaData;

public class UploadPhotoTask extends AsyncTask<OAuth, Void, String> {

	private final FlickrActivity flickrjAndroidSampleActivity;
	private onUploadDone monUploadDone;
	private ProgressDialog mProgressDialog;

	public UploadPhotoTask(FlickrActivity flickrjAndroidSampleActivity) {
		this.flickrjAndroidSampleActivity = flickrjAndroidSampleActivity;
	}


	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		mProgressDialog = ProgressDialog.show(flickrjAndroidSampleActivity,
				"", "Uploading...");
		mProgressDialog.setCanceledOnTouchOutside(true);
		mProgressDialog.setCancelable(true);
		mProgressDialog.setOnCancelListener(new OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dlg) {
				UploadPhotoTask.this.cancel(true);
			}
		});
	}

	@Override
	protected String doInBackground(OAuth... params) {
		OAuth oauth = params[0];
		OAuthToken token = oauth.getToken();

		try {
			FlickrHelper fh = FlickrHelper.getInstance();
			Flickr f = fh.getFlickrAuthed(token.getOauthToken(), token.getOauthTokenSecret());
			UploadMetaData uploadMetaData = new UploadMetaData();
			uploadMetaData.setTitle(fh.getFileName());
			uploadMetaData.setDescription(fh.getDescription());
			return f.getUploader().upload(fh.getFileName(),
					FlickrHelper.getInstance().getInputStream(), uploadMetaData);
		} catch (Exception e) {
			Log.e("boom!!", "" + e.toString());
			e.printStackTrace();
		}
		return null;
	}

	@Override
	protected void onPostExecute(String response) {
		if (mProgressDialog != null) {
			mProgressDialog.dismiss();
		}

		if (response != null) {
			Log.e("Flickr response", "" + response);
		} else {
			Log.e("Flickr response", "Error");
		}
		if (monUploadDone != null) {
			monUploadDone.onComplete();
		}
		Toast.makeText(flickrjAndroidSampleActivity.getApplicationContext(),
				response, Toast.LENGTH_SHORT).show();
	}

	public void setOnUploadDone(onUploadDone monUploadDone) {
		this.monUploadDone = monUploadDone;
	}

	public interface onUploadDone {
		void onComplete();
	}

}