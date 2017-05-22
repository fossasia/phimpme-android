package vn.mbm.phimp.me;

import android.app.Application;

import vn.mbm.phimp.me.leafpic.data.Album;
import vn.mbm.phimp.me.leafpic.data.HandlingAlbums;

/**
 * Created by dnld on 28/04/16.
 */
public class MyApplication extends Application {

    private HandlingAlbums albums = null;

    public Album getAlbum() {
        return albums.dispAlbums.size() > 0 ? albums.getCurrentAlbum() : Album.getEmptyAlbum();
    }

    @Override
    public void onCreate() {
        albums = new HandlingAlbums(getApplicationContext());
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