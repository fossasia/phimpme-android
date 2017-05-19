package vn.mbm.phimp.me.base;

import android.os.Bundle;

import vn.mbm.phimp.me.MyApplication;
import vn.mbm.phimp.me.leafpic.data.Album;
import vn.mbm.phimp.me.leafpic.data.HandlingAlbums;

/**
 * Created by dnld on 03/08/16.
 */

public class SharedMediaActivity extends ThemedActivity {
  @Override
  public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
  }

  public HandlingAlbums getAlbums() {
	return ((MyApplication) getApplicationContext()).getAlbums();
  }

  public Album getAlbum() {
	return ((MyApplication) getApplicationContext()).getAlbum();
  }
}
