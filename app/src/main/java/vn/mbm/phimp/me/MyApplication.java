package vn.mbm.phimp.me;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.twitter.sdk.android.core.DefaultLogger;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterConfig;

import vn.mbm.phimp.me.leafpic.data.Album;
import vn.mbm.phimp.me.leafpic.data.HandlingAlbums;

/**
 * Created by dnld on 28/04/16.
 */
public class MyApplication extends Application {

    private HandlingAlbums albums = null;
    public static Context applicationContext;

    public Album getAlbum() {
        return albums.dispAlbums.size() > 0 ? albums.getCurrentAlbum() : Album.getEmptyAlbum();
    }

    @Override
    public void onCreate() {
        albums = new HandlingAlbums(getApplicationContext());
        applicationContext = getApplicationContext();
        TwitterConfig config = new TwitterConfig.Builder(this)
                .logger(new DefaultLogger(Log.DEBUG))
                .twitterAuthConfig(new TwitterAuthConfig(getString(R.string.twitter_CONSUMER_KEY), getString(R.string.twitter_CONSUMER_SECRET)))
                .debug(true)
                .build();
        Twitter.initialize(config);
        super.onCreate();
    }

    public HandlingAlbums getAlbums() {
        return albums;
    }

    public void setAlbums(HandlingAlbums albums) {
        this.albums = albums;
    }

    public void updateAlbums() {
        albums.loadAlbums(getApplicationContext());
    }
}