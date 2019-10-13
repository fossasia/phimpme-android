package org.fossasia.phimpme.share.pinterest;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.fossasia.phimpme.R;
import org.fossasia.phimpme.base.ThemedActivity;
import org.fossasia.phimpme.utilities.Constants;
import org.fossasia.phimpme.utilities.Utils;
import org.fossasia.phimpme.utilities.VolleyClient;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by codedsun on 13/Oct/2019
 */

public class PinterestAuthActivity extends ThemedActivity {

    public static final String TAG = PinterestAuthActivity.class.getName();

    @BindView(R.id.web_view)
    WebView webView;

    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pinterest_auth);
        ButterKnife.bind(this);
        webView.setBackgroundColor(Color.TRANSPARENT);
        Log.d(TAG, Constants.PINTEREST_AUTH_URL);
        webView.getSettings().setJavaScriptEnabled(true);
        VolleyLog.DEBUG = true;
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                //https://phimp.me/?state=2019phimpme&code=87ee9a33518a418c - when yes pressed
                //https://phimp.me/?state=2019phimpme - When no pressed
                if (request.getUrl().toString().contains(Constants.PINTEREST_REDIRECT_URI) && request.getUrl().getQueryParameter("state").equals(Constants.PINTEREST_AUTH_STATE)) {
                    if (request.getUrl().getQueryParameter("code") != null) {
                        processAuthCode(request.getUrl().getQueryParameter("code"));
                        progressBar.setVisibility(View.VISIBLE);
                    } else {
                        //User has pressed no
                        finish();
                    }

                } else {
                    //some error in url callback, check developer console or check constant of app
                    Utils.showToastShort(getBaseContext(), getString(R.string.something_went_wrong));
                }
                return false;
            }

        });
        webView.loadUrl(Constants.PINTEREST_AUTH_URL);
    }

    private void processAuthCode(String code) {
      //  webView.destroy();
        String url = "https://api.pinterest.com/v1/oauth/token" ;
//                "?grant_type=authorization_code&" +
//                "client_id=" + Constants.PINTEREST_APP_ID + "&" +
//                "client_secret=" + Constants.PINTEREST_APP_SECRET + "&" +
//                "code=" + code;
        JSONObject params = new JSONObject();
        try {
            params.put("grant_type", "authorization_code");
            params.put("client_id", Constants.PINTEREST_APP_ID);
            params.put("client_secret", Constants.PINTEREST_APP_SECRET);
            params.put("code", code);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest requestToken = new JsonObjectRequest(Request.Method.POST, url, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.e(TAG, "response -- " + response.toString());

            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "error -- " + error.toString());

                    }
                });
        requestToken.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(requestToken);
    }

}
