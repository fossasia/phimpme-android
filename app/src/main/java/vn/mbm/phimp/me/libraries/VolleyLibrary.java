package vn.mbm.phimp.me.libraries;

import android.content.Context;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

public class VolleyLibrary {
	private static VolleyLibrary mInstance;
    private RequestQueue mRequestQueue;
    private static Context mCtx;
    private ImageLoader mImageLoader;

    private VolleyLibrary(Context context) {
        mCtx = context;
        mRequestQueue = getRequestQueue();
    }

    public static synchronized VolleyLibrary getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new VolleyLibrary(context);
        }
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            mRequestQueue = Volley.newRequestQueue(mCtx.getApplicationContext());
        }
        return mRequestQueue;
    }  

    public <T> void addToRequestQueue(Request<T> req, String tag, boolean retry) {
	    	req.setTag(tag);
	    	if(retry){
	    		req.setRetryPolicy(new DefaultRetryPolicy(10 * 1000, 2, 1.0f));
	    	}
	    	else{
	    		req.setRetryPolicy(new DefaultRetryPolicy(10 * 1000, 1, 1.0f));
	    	}
            req.setShouldCache(false);
	        getRequestQueue().add(req);
    }
    
    public void cancelPendingRequests(String tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }

    public ImageLoader getImageLoader() {
        getRequestQueue();
        if (mImageLoader == null) {
            mImageLoader = new ImageLoader(this.mRequestQueue,
                    new LruBitmapCache());
        }
        return this.mImageLoader;
    }

	
}
