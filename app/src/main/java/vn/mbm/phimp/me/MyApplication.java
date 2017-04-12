package vn.mbm.phimp.me;

import android.app.Activity;
import android.app.Application;
import android.content.ComponentCallbacks2;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.content.res.Configuration;
import android.net.http.HttpResponseCache;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.yarolegovich.wellsql.WellSql;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.wordpress.android.fluxc.Dispatcher;
import org.wordpress.android.fluxc.module.AppContextModule;
import org.wordpress.android.fluxc.persistence.WellSqlConfig;
import org.wordpress.android.fluxc.store.AccountStore;
import org.wordpress.android.fluxc.store.SiteStore;
import org.wordpress.android.fluxc.tools.FluxCImageLoader;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.inject.Inject;
import javax.inject.Named;

import vn.mbm.phimp.me.utils.BitmapLruCache;
import vn.mbm.phimp.me.wordpress.AccountActionBuilder;
import vn.mbm.phimp.me.wordpress.AppComponent;
import vn.mbm.phimp.me.wordpress.AppPrefs;
import vn.mbm.phimp.me.wordpress.DaggerAppComponent;
import vn.mbm.phimp.me.wordpress.FluxCUtils;
import vn.mbm.phimp.me.wordpress.RateLimitedTask;
import vn.mbm.phimp.me.wordpress.SiteActionBuilder;

import vn.mbm.phimp.me.folderchooser.AppState;
import vn.mbm.phimp.me.utils.FolderChooserPrefSettings;
import vn.mbm.phimp.me.utils.StorageUtils;

/**
 * User: pa1pal
 * Date: 8/26/16
 */
public class MyApplication extends Application {

    private static MyApplication instance;
    private static Context mContext;
    public static final String SITE = "SITE";
    private static BitmapLruCache mBitmapCache;
    private static final int SECONDS_BETWEEN_BLOGLIST_UPDATE = 15 * 60; // 15 minutes

    @Inject
    Dispatcher mDispatcher;
    @Inject
    AccountStore mAccountStore;
    @Inject
    SiteStore mSiteStore;

    private AppComponent mAppComponent;

    public AppComponent component() {
        return mAppComponent;
    }

    @Inject
    @Named("custom-ssl")
    RequestQueue mRequestQueue;
    public static RequestQueue sRequestQueue;
    @Inject FluxCImageLoader mImageLoader;
    public static FluxCImageLoader sImageLoader;

    /**
     * Update site list in a background task. (WPCOM site list, and eventually self hosted multisites)
     */
    public RateLimitedTask mUpdateSiteList = new RateLimitedTask(SECONDS_BETWEEN_BLOGLIST_UPDATE) {
        protected boolean run() {
            if (mAccountStore.hasAccessToken()) {
                mDispatcher.dispatch(SiteActionBuilder.newFetchSitesAction());
            }
            return true;
        }
    };

    public static MyApplication getInstance() {
        return instance;
    }

    public static BitmapLruCache getBitmapCache() {
        if (mBitmapCache == null) {
            // The cache size will be measured in kilobytes rather than
            // number of items. See http://developer.android.com/training/displaying-bitmaps/cache-bitmap.html
            int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
            int cacheSize = maxMemory / 16;  //Use 1/16th of the available memory for this memory cache.
            mBitmapCache = new BitmapLruCache(cacheSize);
        }
        return mBitmapCache;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        mContext = this;

        WellSql.init(new WellSqlConfig(getApplicationContext()));

        // Init Dagger
        mAppComponent = DaggerAppComponent.builder()
                .appContextModule(new AppContextModule(getApplicationContext()))
                .build();

        enableHttpResponseCache(mContext);
        component().inject(this);
        mDispatcher.register(this);

        sRequestQueue = mRequestQueue;
        sImageLoader = mImageLoader;

        ApplicationLifecycleMonitor applicationLifecycleMonitor = new ApplicationLifecycleMonitor();
        registerComponentCallbacks(applicationLifecycleMonitor);
        registerActivityLifecycleCallbacks(applicationLifecycleMonitor);

        // EventBus setup
        EventBus.TAG = "Phimpme-EVENT";
        EventBus.builder()
                .logNoSubscriberMessages(false)
                .sendNoSubscriberEvent(false)
                .throwSubscriberException(true)
                .installDefaultEventBus();

        FolderChooserPrefSettings.init(this);
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);

        AppState.setStorageInfos(StorageUtils.getStorageList(this));

        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "vn.mbm.phimp.me",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }
    }

    public void deferredInit(Activity activity) {

        // Refresh account informations
        if (mAccountStore.hasAccessToken()) {
            mDispatcher.dispatch(AccountActionBuilder.newFetchAccountAction());
            mDispatcher.dispatch(AccountActionBuilder.newFetchSettingsAction());

        }
    }

    public static Context getContext() {
        return mContext;
    }

    /**
     * Sign out from wpcom account.
     * Note: This method must not be called on UI Thread.
     */
    public void wordPressComSignOut() {

        removeWpComUserRelatedData();
    }

    @SuppressWarnings("unused")
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAccountChanged(AccountStore.OnAccountChanged event) {
        if (!FluxCUtils.isSignedInWPComOrHasWPOrgSite(mAccountStore, mSiteStore)) {
            flushHttpCache();
        }
    }

    public void removeWpComUserRelatedData() {

        // reset default account
        mDispatcher.dispatch(AccountActionBuilder.newSignOutAction());
        // delete wpcom and jetpack sites
        mDispatcher.dispatch(SiteActionBuilder.newRemoveWpcomAndJetpackSitesAction());

        // reset all reader-related prefs & data
        AppPrefs.reset();

    }

    private static void flushHttpCache() {
        HttpResponseCache cache = HttpResponseCache.getInstalled();
        if (cache != null) {
            cache.flush();
        }
    }

    /*
  * enable caching for HttpUrlConnection
  * http://developer.android.com/training/efficient-downloads/redundant_redundant.html
  */
    private static void enableHttpResponseCache(Context context) {
        try {
            long httpCacheSize = 5 * 1024 * 1024; // 5MB
            File httpCacheDir = new File(context.getCacheDir(), "http");
            HttpResponseCache.install(httpCacheDir, httpCacheSize);
        } catch (IOException e) {
        }
    }

    public static Object getBuildConfigValue(Application application, String fieldName) {
        try {
            String packageName = application.getClass().getPackage().getName();
            Class<?> clazz = Class.forName(packageName + ".BuildConfig");
            Field field = clazz.getField(fieldName);
            return field.get(null);
        } catch (Exception e) {
            return null;
        }
    }

    private class ApplicationLifecycleMonitor implements Application.ActivityLifecycleCallbacks, ComponentCallbacks2 {


        @Override
        public void onConfigurationChanged(final Configuration newConfig) {

        }

        @Override
        public void onLowMemory() {
        }

        @Override
        public void onTrimMemory(final int level) {
            boolean evictBitmaps = false;
            switch (level) {
                case TRIM_MEMORY_RUNNING_LOW:
                    evictBitmaps = true;
                    break;
                default:
                    break;
            }

            if (evictBitmaps && mBitmapCache != null) {
                mBitmapCache.evictAll();
            }
        }

        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

        }

        @Override
        public void onActivityStarted(Activity activity) {

        }

        @Override
        public void onActivityResumed(Activity activity) {

        }

        @Override
        public void onActivityPaused(Activity activity) {

        }

        @Override
        public void onActivityStopped(Activity activity) {

        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

        }

        @Override
        public void onActivityDestroyed(Activity activity) {

        }
    }

}