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

package vn.mbm.phimp.me.gallery3d.app;

import java.util.HashMap;
import java.util.TimeZone;

import vn.mbm.phimp.me.gallery3d.media.ReverseGeocoder;
import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.DisplayMetrics;
import android.widget.Toast;

/*
 *  TODO: consider adding HashMap<object, object> for globals globals
 *  TODO: hook up other activity classes to App (besides Gallery and Search)
 */
public class App {
	static private final HashMap<Context, App> mMap = new HashMap<Context, App>();
		
	static public App get(Context context) {
		return mMap.get(context);
	}
	
    public static final TimeZone CURRENT_TIME_ZONE = TimeZone.getDefault();
    public static float PIXEL_DENSITY = 0.0f;
    
	private final Context mContext;
    private final HandlerThread mHandlerThread = new HandlerThread("AppHandlerThread");
    private final Handler mHandler;	
    private ReverseGeocoder mReverseGeocoder = null;
    
    private boolean mPaused = false;
    
	public App(Context context) {
		// register
		mMap.put(context, this);
		
		mContext = context;
				
		if(PIXEL_DENSITY == 0.0f) {
			DisplayMetrics metrics = new DisplayMetrics();
			((Activity)mContext).getWindowManager().getDefaultDisplay().getMetrics(metrics);
			PIXEL_DENSITY = metrics.density;
		}

        mHandlerThread.start();
        mHandler = new Handler(mHandlerThread.getLooper());		
		
	    mReverseGeocoder = new ReverseGeocoder(mContext);					
	}
		
	public void shutdown() {
        mReverseGeocoder.shutdown();
        
        // unregister
        mMap.put(mContext, null);
	}
	
    public Context getContext() {
        return mContext;
    }	
	
    public Handler getHandler() {
        while (mHandler == null) {
            // Wait till the handler is created.
            ;
        }
        return mHandler;
    }
    
    public ReverseGeocoder getReverseGeocoder() {
        return mReverseGeocoder;
    }    
    
    public boolean isPaused() {
    	return mPaused;
    }
	
//    public void onCreate(Bundle savedInstanceState) {
//    }
//
//    public void onStart() {
//    }
//    
//    public void onRestart() {
//    }

    public void onResume() {
    	mPaused = false;
    }

    public void onPause() {
    	mReverseGeocoder.flushCache();
    	mPaused = true;
    }

//    public void onStop() {
//    	  
//    }
//
//    public void onDestroy() {
//    }
    
    public void showToast(final String string, final int duration) {
        mHandler.post(new Runnable() {
            public void run() {
                Toast.makeText(mContext, string, duration).show();
            }
        });
    }
}
