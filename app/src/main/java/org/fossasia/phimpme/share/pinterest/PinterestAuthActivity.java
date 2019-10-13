package org.fossasia.phimpme.share.pinterest;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import androidx.lifecycle.ViewModelProviders;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;

import org.fossasia.phimpme.R;
import org.fossasia.phimpme.accounts.AccountViewModel;
import org.fossasia.phimpme.base.ThemedActivity;
import org.fossasia.phimpme.data.local.AccountDatabase;
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

    private AccountViewModel accountViewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pinterest_auth);
        ButterKnife.bind(this);
        accountViewModel = ViewModelProviders.of(this).get(AccountViewModel.class);
        webView.setBackgroundColor(Color.TRANSPARENT);
        Log.d(TAG, Constants.PINTEREST_AUTH_URL);
        webView.getSettings().setJavaScriptEnabled(true);
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
                if (request.getUrl().toString().contains(Constants.PINTEREST_REDIRECT_URI) &&
                        (request.getUrl().getQueryParameter("state") != null && request.getUrl().getQueryParameter("state").equals(Constants.PINTEREST_AUTH_STATE))) {
                    if (request.getUrl().getQueryParameter("code") != null) {
                        processAuthCode(request.getUrl().getQueryParameter("code"));
                        progressBar.setVisibility(View.VISIBLE);
                    } else {
                        //User has pressed no
                        finish();
                    }

                }
                return false;
            }

        });
        webView.loadUrl(Constants.PINTEREST_AUTH_URL);
    }

    private void processAuthCode(String code) {
        progressBar.setVisibility(View.VISIBLE);
        JSONObject params = new JSONObject();
        try {
            params.put("grant_type", "authorization_code");
            params.put("client_id", Constants.PINTEREST_APP_ID);
            params.put("client_secret", Constants.PINTEREST_APP_SECRET);
            params.put("code", code);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest requestToken = new JsonObjectRequest(Request.Method.POST, Constants.PINTEREST_GET_USER_TOKEN, params, response -> {
            try {
                fetchUserDetails(response.getString("access_token"));
            } catch (JSONException e) {
                e.printStackTrace();
                Utils.showToastShort(this, getString(R.string.something_went_wrong));
                finish();
            }
        }, error -> {
            Log.e(TAG, "error -- " + error.toString());
            Utils.showToastShort(PinterestAuthActivity.this, getString(R.string.something_went_wrong));
            finish();
        });
        requestToken.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = VolleyClient.getInstance(this).getRequestQueue();
        requestQueue.add(requestToken);
    }

    private void fetchUserDetails(String accessToken) {
        JsonObjectRequest requestFetchUser = new JsonObjectRequest(Request.Method.GET, Constants.PINTEREST_GET_USER_DETAILS + accessToken, response -> {
            try {
                String url = response.getJSONObject("data").getString("url"); //"https://www.pinterest.com/username/"
                //Extracting username from url
                String username = url.substring(26, url.length()-1);
                accountViewModel.savePinterestAccount(username, accessToken);
                progressBar.setVisibility(View.GONE);
                finish();
            } catch (JSONException e) {
                e.printStackTrace();
                Utils.showToastShort(this, getString(R.string.something_went_wrong));
                finish();
            }
        }, error -> {
            Log.e(TAG, "error -- " + error.toString());
            accountViewModel.savePinterestAccount(AccountDatabase.AccountName.PINTEREST.toString(), accessToken);
            finish();
        });

        requestFetchUser.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = VolleyClient.getInstance(this).getRequestQueue();
        requestQueue.add(requestFetchUser);
    }

}
