package org.fossasia.phimpme;

import android.app.Application;
import android.content.Context;
import android.os.StrictMode;
import android.support.multidex.MultiDex;
import android.util.Log;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;
import com.twitter.sdk.android.core.DefaultLogger;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterConfig;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import org.fossasia.phimpme.gallery.data.Album;
import org.fossasia.phimpme.gallery.data.HandlingAlbums;
import org.fossasia.phimpme.utilities.Constants;

/** Created by dnld on 28/04/16. */
public class MyApplication extends Application {

  private HandlingAlbums albums = null;
  public static Context applicationContext;
  private RefWatcher refWatcher;

  public Album getAlbum() {
    return albums.dispAlbums.size() > 0 ? albums.getCurrentAlbum() : Album.getEmptyAlbum();
  }

  @Override
  public void onCreate() {

    StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
    StrictMode.setVmPolicy(builder.build());

    if (LeakCanary.isInAnalyzerProcess(this)) {
      // This process is dedicated to LeakCanary for heap analysis.
      // You should not init your app in this process.
      return;
    }
    refWatcher = LeakCanary.install(this);
    albums = new HandlingAlbums(getApplicationContext());
    applicationContext = getApplicationContext();

    MultiDex.install(this);

    TwitterConfig config =
        new TwitterConfig.Builder(this)
            .logger(new DefaultLogger(Log.DEBUG))
            .twitterAuthConfig(
                new TwitterAuthConfig(
                    Constants.TWITTER_CONSUMER_KEY, Constants.TWITTER_CONSUMER_SECRET))
            .debug(true)
            .build();
    Twitter.initialize(config);

    /** Realm initialization */
    Realm.init(this);
    RealmConfiguration realmConfiguration =
        new RealmConfiguration.Builder()
            .name("phimpme.realm")
            .schemaVersion(1)
            .deleteRealmIfMigrationNeeded()
            .build();
    Realm.setDefaultConfiguration(realmConfiguration);
    super.onCreate();
    // if (isPublished)
    //   Fabric.with(this, new Crashlytics());

    /** Stetho initialization */
    /*Stetho.initialize(
    Stetho.newInitializerBuilder(this)
            .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
            .enableWebKitInspector(RealmInspectorModulesProvider.builder(this).build())
            .build());*/
  }

  public static RefWatcher getRefWatcher(Context context) {
    MyApplication myApplication = (MyApplication) context.getApplicationContext();
    return myApplication.refWatcher;
  }

  @Override
  protected void attachBaseContext(Context base) {
    super.attachBaseContext(base);
  }

  public HandlingAlbums getAlbums() {
    return albums;
  }
}
