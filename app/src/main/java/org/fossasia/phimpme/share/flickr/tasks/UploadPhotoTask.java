package org.fossasia.phimpme.share.flickr.tasks;

import android.os.AsyncTask;
import android.util.Log;
import com.googlecode.flickrjandroid.Flickr;
import com.googlecode.flickrjandroid.oauth.OAuth;
import com.googlecode.flickrjandroid.oauth.OAuthToken;
import com.googlecode.flickrjandroid.uploader.UploadMetaData;
import org.fossasia.phimpme.R;
import org.fossasia.phimpme.share.flickr.FlickrHelper;
import org.fossasia.phimpme.utilities.NotificationHandler;

public class UploadPhotoTask extends AsyncTask<OAuth, Void, String> {
  private onUploadDone monUploadDone;

  @Override
  protected void onPreExecute() {
    super.onPreExecute();
    NotificationHandler.make(
        R.string.flickr, R.string.upload_progress, R.drawable.ic_cloud_upload_black_24dp);
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
      uploadMetaData.setHidden(true);
      return f.getUploader()
          .upload(fh.getFileName(), FlickrHelper.getInstance().getInputStream(), uploadMetaData);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  @Override
  protected void onPostExecute(String response) {

    if (response != null) {
      Log.e("Flickr response", "" + response);
    } else {
      NotificationHandler.actionFailed();
      Log.e("Flickr response", "Error");
    }
    if (monUploadDone != null) {
      NotificationHandler.actionPassed(R.string.upload_complete);
      monUploadDone.onComplete();
    }
  }

  public void setOnUploadDone(onUploadDone monUploadDone) {
    this.monUploadDone = monUploadDone;
  }

  public interface onUploadDone {
    void onComplete();
  }
}
