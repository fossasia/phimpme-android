/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package vn.mbm.phimp.me.gallery3d.media;

import java.util.HashMap;

import vn.mbm.phimp.me.gallery3d.app.App;
import vn.mbm.phimp.me.gallery3d.app.Res;
import vn.mbm.phimp.me.gallery3d.cache.CacheService;
import vn.mbm.phimp.me.gallery3d.wallpaper.RandomDataSource;
import vn.mbm.phimp.me.gallery3d.wallpaper.Slideshow;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.provider.MediaStore.Images;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

public final class Gallery extends Activity {
    public static final String REVIEW_ACTION = "vn.mbm.phimp.me.gallery3d.media.action.REVIEW";
    private static final String TAG = "Gallery";

    private App mApp = null;
    private RenderView mRenderView = null;
    private GridLayer mGridLayer;
    private WakeLock mWakeLock;
    private HashMap<String, Boolean> mAccountsEnabled = new HashMap<String, Boolean>();
    private boolean mDockSlideshow = false;
    private int mNumRetries;
    private boolean mImageManagerHasStorageAfterDelay = false;
    private HandlerThread mPicasaAccountThread = new HandlerThread("PicasaAccountMonitor");
    private Handler mPicasaHandler = null;

    private static final int GET_PICASA_ACCOUNT_STATUS = 1;
    private static final int UPDATE_PICASA_ACCOUNT_STATUS = 2;

    private static final int CHECK_STORAGE = 0;
    private static final int HANDLE_INTENT = 1;
    private static final int NUM_STORAGE_CHECKS = 25;

    private final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case CHECK_STORAGE:
                    checkStorage();
                    break;
                case HANDLE_INTENT:
                    initializeDataSource();
                    break;
            }
        }
    };

    @SuppressWarnings("static-access")
	private void checkStorage() {
        mNumRetries++;
        mImageManagerHasStorageAfterDelay = ImageManager.hasStorage();
        if (!mImageManagerHasStorageAfterDelay && mNumRetries < NUM_STORAGE_CHECKS) {
            if (mNumRetries == 1) {
                mApp.showToast(getResources().getString(Res.string.no_sd_card), Toast.LENGTH_LONG);
            }
            handler.sendEmptyMessageDelayed(CHECK_STORAGE, 200);
        } else {
            handler.sendEmptyMessage(HANDLE_INTENT);
        }
    }

    @SuppressWarnings("static-access")
	@Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        
        Log.i(TAG, "onCreate");
        
        mApp = new App(Gallery.this);
        final boolean imageManagerHasStorage = ImageManager.hasStorage();
        boolean slideshowIntent = false;
        if (isViewIntent()) 
        {
        	Log.i(TAG, "onCreate - isViewIntent: true");
            Bundle extras = getIntent().getExtras();
            if (extras != null) 
            {
            	Log.i(TAG, "onCreate - extras is not null");
                slideshowIntent = extras.getBoolean("slideshow", false);
            }
        }
        Log.d("thong", "EXTERNAL: " + Images.Media.EXTERNAL_CONTENT_URI);
        boolean e = false;
        try
        {
        	e = getIntent().getData().equals(Images.Media.EXTERNAL_CONTENT_URI);
        }
        catch (Exception exception) 
        {
			// TODO: handle exception
		}
        if (isViewIntent() && e && slideshowIntent) 
        {
        	Log.i(TAG, "onCreate - isViewIntent: true + EXTERNAL_CONTENT_URI");
            if (!imageManagerHasStorage) 
            {
                Toast.makeText(this, getResources().getString(Res.string.no_sd_card), Toast.LENGTH_LONG).show();
                finish();
            } 
            else 
            {
            	Log.i(TAG, "onCreate - Slideshow");
                Slideshow slideshow = new Slideshow(this);
                slideshow.setDataSource(new RandomDataSource());
                setContentView(slideshow);
                mDockSlideshow = true;
            }
            return;
        }
        
        mRenderView = new RenderView(this);
        mGridLayer = new GridLayer(this, (int) (96.0f * App.PIXEL_DENSITY), (int) (72.0f * App.PIXEL_DENSITY), new GridLayoutInterface(4), mRenderView);
        mRenderView.setRootLayer(mGridLayer);
        
        setContentView(mRenderView);

        mPicasaAccountThread.start();
        mPicasaHandler = new Handler(mPicasaAccountThread.getLooper()) {

            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case GET_PICASA_ACCOUNT_STATUS:
                        mAccountsEnabled = PicasaDataSource.getAccountStatus(Gallery.this);
                        break;
                    case UPDATE_PICASA_ACCOUNT_STATUS:
                        updatePicasaAccountStatus();
                        break;
                }
            }
        };
        

        sendInitialMessage();
    }

    private void sendInitialMessage() {
        mNumRetries = 0;
        Message checkStorage = new Message();
        checkStorage.what = CHECK_STORAGE;
        handler.sendMessage(checkStorage);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        handler.removeMessages(CHECK_STORAGE);
        handler.removeMessages(HANDLE_INTENT);

        sendInitialMessage();
    }

    @Override
    public void onRestart() {
        super.onRestart();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mDockSlideshow) {
            if (mWakeLock != null) {
                if (mWakeLock.isHeld()) {
                    mWakeLock.release();
                }
            }
            PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
            mWakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "GridView.Slideshow.All");
            mWakeLock.acquire();
            return;
        }
        if (mRenderView != null) {
            mRenderView.onResume();
        }
        if (mApp.isPaused()) {
            if (mPicasaHandler != null) {
                mPicasaHandler.removeMessages(GET_PICASA_ACCOUNT_STATUS);
                mPicasaHandler.sendEmptyMessage(UPDATE_PICASA_ACCOUNT_STATUS);
            }
        	mApp.onResume();
        }
    }

    void updatePicasaAccountStatus() {
        // We check to see if the authenticated accounts have
        // changed, if so, reload the datasource.

        // TODO: This should be done in PicasaDataFeed
        if (mGridLayer != null) {
            HashMap<String, Boolean> accountsEnabled = PicasaDataSource.getAccountStatus(this);
            if (!accountsEnabled.equals(mAccountsEnabled)) {
                mGridLayer.setDataSource(mGridLayer.getDataSource());
                mAccountsEnabled = accountsEnabled;
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mRenderView != null)
            mRenderView.onPause();
        if (mWakeLock != null) {
            if (mWakeLock.isHeld()) {
                mWakeLock.release();
            }
            mWakeLock = null;
        }

        LocalDataSource.sThumbnailCache.flush();
        LocalDataSource.sThumbnailCacheVideo.flush();
        PicasaDataSource.sThumbnailCache.flush();

        if (mPicasaHandler != null) {
            mPicasaHandler.removeMessages(GET_PICASA_ACCOUNT_STATUS);
            mPicasaHandler.removeMessages(UPDATE_PICASA_ACCOUNT_STATUS);
        }
    	mApp.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mGridLayer != null)
            mGridLayer.stop();

        // Start the thumbnailer.
        CacheService.startCache(this, true);
    }

    @SuppressWarnings("static-access")
	@Override
    public void onDestroy() {
        // Force GLThread to exit.
        setContentView(Res.layout.main);

        // Remove any post messages.
        handler.removeMessages(CHECK_STORAGE);
        handler.removeMessages(HANDLE_INTENT);

        mPicasaAccountThread.quit();
        mPicasaAccountThread = null;
        mPicasaHandler = null;

        if (mGridLayer != null) {
            DataSource dataSource = mGridLayer.getDataSource();
            if (dataSource != null) {
                dataSource.shutdown();
            }
            mGridLayer.shutdown();
        }
        if (mRenderView != null) {
            mRenderView.shutdown();
            mRenderView = null;
        }
        mGridLayer = null;
        mApp.shutdown();
        super.onDestroy();
        Log.i(TAG, "onDestroy");
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (mGridLayer != null) {
            mGridLayer.markDirty(30);
        }
        if (mRenderView != null)
            mRenderView.requestRender();
        Log.i(TAG, "onConfigurationChanged");
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (mRenderView != null) {
            return mRenderView.onKeyDown(keyCode, event) || super.onKeyDown(keyCode, event);
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    private boolean isViewIntent() 
    {
        //String action = getIntent().getAction();
        //return Intent.ACTION_VIEW.equals(action);
    	return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
        case CropImage.CROP_MSG: {
            if (resultCode == RESULT_OK) {
                setResult(resultCode, data);
                finish();
            }
            break;
        }
        case CropImage.CROP_MSG_INTERNAL: {
            // We cropped an image, we must try to set the focus of the camera
            // to that image.
            if (resultCode == RESULT_OK) {
                String contentUri = data.getAction();
                if (mGridLayer != null && contentUri != null) {
                    mGridLayer.focusItem(contentUri);
                }
            }
            break;
        }
        }
    }

    @Override
    public void onLowMemory() {
        if (mRenderView != null) {
            mRenderView.handleLowMemory();
        }
    }

    @SuppressWarnings("unused")
	private void initializeDataSource() 
    {
    	final boolean hasStorage = mImageManagerHasStorageAfterDelay;
        // Creating the DataSource objects.
    	final PicasaDataSource picasaDataSource = new PicasaDataSource(Gallery.this);
        final LocalDataSource localDataSource = new LocalDataSource(Gallery.this, LocalDataSource.URI_ALL_MEDIA, false);
        final ConcatenatedDataSource combinedDataSource = new ConcatenatedDataSource(localDataSource, picasaDataSource);

        // Depending upon the intent, we assign the right dataSource.
        /*if (!isPickIntent() && !isViewIntent() && !isReviewIntent()) {
            localDataSource.setMimeFilter(true, true);
            if (hasStorage) {
                mGridLayer.setDataSource(combinedDataSource);
            } else {
                mGridLayer.setDataSource(picasaDataSource);
            }
        } else if (isPickIntent()) {
            final Intent intent = getIntent();
            if (intent != null) {
                String type = intent.resolveType(Gallery.this);
                if (type == null) {
                    // By default, we include images
                    type = "image/*";
                }
                boolean includeImages = isImageType(type);
                boolean includeVideos = isVideoType(type);
                localDataSource.setMimeFilter(includeImages, includeVideos);
                if (includeImages) {
                    if (hasStorage) {
                        mGridLayer.setDataSource(combinedDataSource);
                    } else {
                        mGridLayer.setDataSource(picasaDataSource);
                    }
                } else {
                    mGridLayer.setDataSource(localDataSource);
                }
                mGridLayer.setPickIntent(true);
                if (hasStorage) {
                    mApp.showToast(getResources().getString(Res.string.pick_prompt), Toast.LENGTH_LONG);
                }
            }
        } 
        else 
        { */
        	// view intent for images and review intent for images and videos
        	// Thong  - Them data o day
            //final Intent intent = getIntent();
            //Uri uri = intent.getData();
            //boolean slideshow = intent.getBooleanExtra("slideshow", false);
        final Intent intent = getIntent();
        Uri uri = intent.getData();
        boolean slideshow = false;
        final LocalDataSource singleDataSource = new LocalDataSource(Gallery.this, uri.toString(), false);
            
        // Display both image and video.
        singleDataSource.setMimeFilter(true, false);

        if (hasStorage) 
        {
        	ConcatenatedDataSource singleCombinedDataSource = new ConcatenatedDataSource(singleDataSource, picasaDataSource);
        	mGridLayer.setDataSource(singleCombinedDataSource);
        } else 
        {
        	mGridLayer.setDataSource(picasaDataSource);
        }
        mGridLayer.setDataSource(singleDataSource);
        mGridLayer.setViewIntent(true, Utils.getBucketNameFromUri(getContentResolver(), uri));

        if (singleDataSource.isSingleImage()) 
        {
        	mGridLayer.setSingleImage(false);
        } 
        else if (slideshow) 
        {
                mGridLayer.setSingleImage(true);
                mGridLayer.startSlideshow();
        }
        /*}*/
        // We record the set of enabled accounts for picasa.
        //mPicasaHandler.sendEmptyMessage(GET_PICASA_ACCOUNT_STATUS);
    }
}
