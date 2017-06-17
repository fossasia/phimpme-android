package org.fossasia.phimpme.base;

import android.os.Bundle;

import org.fossasia.phimpme.MyApplication;
import org.fossasia.phimpme.leafpic.data.Album;
import org.fossasia.phimpme.leafpic.data.HandlingAlbums;

/**
 * Created by dnld on 03/08/16.
 */

public class SharedMediaActivity extends ThemedActivity {
  @Override
  public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
  }

  public static HandlingAlbums getAlbums() {
	return ((MyApplication)MyApplication.applicationContext).getAlbums();
  }

  public Album getAlbum() {
	return ((MyApplication) getApplicationContext()).getAlbum();
  }
}
