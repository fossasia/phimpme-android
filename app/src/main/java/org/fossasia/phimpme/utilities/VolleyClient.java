package org.fossasia.phimpme.utilities;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by @codedsun on 13/Oct/2019
 */

//Singleton pattern class to get volley request queue
public class VolleyClient {

    private static VolleyClient instance;
    private RequestQueue requestQueue;
    private Context context;

    private VolleyClient(Context context) {
        this.context = context;
        requestQueue = getRequestQueue();

    }

    public static synchronized VolleyClient getInstance(Context context) {
        if(instance == null) {
            instance = new VolleyClient(context);
        }
        return instance;
    }

    public RequestQueue getRequestQueue(){
        if(requestQueue == null) {
            requestQueue = Volley.newRequestQueue(context);
        }
        return requestQueue;
    }


}
