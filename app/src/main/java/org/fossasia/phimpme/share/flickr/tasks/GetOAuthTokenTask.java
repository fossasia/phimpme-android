package org.fossasia.phimpme.share.flickr.tasks;

import android.os.AsyncTask;
import com.googlecode.flickrjandroid.Flickr;
import com.googlecode.flickrjandroid.oauth.OAuth;
import com.googlecode.flickrjandroid.oauth.OAuthInterface;
import org.fossasia.phimpme.share.flickr.FlickrActivity;
import org.fossasia.phimpme.share.flickr.FlickrHelper;

public class GetOAuthTokenTask extends AsyncTask<String, Integer, OAuth> {

  private FlickrActivity activity;

  public GetOAuthTokenTask(FlickrActivity context) {
    this.activity = context;
  }

  @Override
  protected OAuth doInBackground(String... params) {
    String oauthToken = params[0];
    String oauthTokenSecret = params[1];
    String verifier = params[2];

    Flickr f = FlickrHelper.getInstance().getFlickr();
    OAuthInterface oauthApi = null;
    if (f != null) {
      oauthApi = f.getOAuthInterface();
      try {
        return oauthApi.getAccessToken(oauthToken, oauthTokenSecret, verifier);
      } catch (Exception e) {
        return null;
      }
    }
    return null;
  }

  @Override
  protected void onPostExecute(OAuth result) {
    if (activity != null) {
      activity.onOAuthDone(result);
    }
  }
}
