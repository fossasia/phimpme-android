package vn.mbm.phimp.me;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.webkit.CookieSyncManager;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import vn.mbm.phimp.me.libraries.CustomRequest;
import vn.mbm.phimp.me.libraries.VolleyLibrary;


public class WordpressWebviewActivity extends AppCompatActivity {
    String client_id = "52266";
    String client_secret = "kY198VU8cNsdV2WOfw0tHasIWYa45vPk4NcCSo6jVdhIfeP57hec5Vak8XEHuUq9";
    String redirect_url = "http://labs.fossasia.org";
    String url = "https://public-api.wordpress.com/oauth2/token";
    String toLoadURL = "https://public-api.wordpress.com/oauth2/authorize?client_id=" + client_id + "&redirect_uri=" + redirect_url + "&response_type=code";
    EditText eusername, epassword;
    Button btnlogin;
    String username, password;
    ProgressDialog pd;
    String access_token, token_type;
    SwipeRefreshLayout mSwipeRefreshLayout;
    private WebView mWebView;

    public static Map<String, String> splitQuery(String url) throws UnsupportedEncodingException {
        final Map<String, String> query_pairs = new LinkedHashMap<String, String>();
        final String[] pairs = url.substring(url.indexOf('?') + 1).split("&");
        for (String pair : pairs) {
            final int idx = pair.indexOf("=");
            final String key = idx > 0 ? URLDecoder.decode(pair.substring(0, idx), "UTF-8") : pair;
            final String value = idx > 0 && pair.length() > idx + 1 ? URLDecoder.decode(pair.substring(idx + 1), "UTF-8") : "";
            query_pairs.put(key, value);
        }
        return query_pairs;
    }

    private void authenticate(String code) {
        pd = new ProgressDialog(WordpressWebviewActivity.this);
        pd.setMessage("Authenticating. Please Wait...");
        pd.show();

        final HashMap<String, String> params = new HashMap<>();
        params.put("client_id", client_id);
        params.put("client_secret", client_secret);
        params.put("redirect_uri", redirect_url);
        params.put("grant_type", "authorization_code");
        params.put("code", code);


        CustomRequest request = new CustomRequest(Request.Method.POST, url, params,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject json) {
                        Log.e("GET_PROFILE", json.toString());

//                        {"access_token":"ACCESS_TOKEN","token_type":"bearer","blog_id":"BLOG_ID","blog_url":"BLOG_URL","scope":""}
                        try {
                            access_token = json.getString("access_token");
                            token_type = json.getString("token_type");
                            String blog_id = json.getString("blog_id");
                            String blog_url = json.getString("blog_url");
                            //todo Authentication successful. Save the token and blog id for further usage.
                            // We can use the following URL to get further information about the site, and store required details
                            //https://developer.wordpress.com/docs/api/1.1/get/me/

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        pd.dismiss();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error != null)
                            Log.e("GET_PROFILE", "" + error.getMessage());
                        Toast.makeText(WordpressWebviewActivity.this, error.getMessage().toString(), Toast.LENGTH_SHORT).show();
                        pd.dismiss();
                    }
                });

        VolleyLibrary.getInstance(WordpressWebviewActivity.this).addToRequestQueue(request, "", false);

    }

    void init() {
       /* eusername = (EditText) findViewById(R.id.input_email);
        epassword = (EditText) findViewById(R.id.input_password);
        btnlogin = (Button) findViewById(R.id.btn_login);
        btnlogin.setOnClickListener(clickListener);*/

        mWebView = (WebView) findViewById(R.id.webview);

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        CookieSyncManager.createInstance(this);
        CookieSyncManager.getInstance().startSync();
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mWebView.loadUrl(mWebView.getUrl());
            }
        });

        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setDomStorageEnabled(true);
        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                return super.onJsAlert(view, url, message, result);
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                setTitle(title);
            }
        });


        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.e("URL requested", url);
                //when User denies access- This URL is requested.
                // http://labs.fossasia.org/?error=access_denied&error_description=You+need+to+log+in+to+WordPress.com&state
                // and When user grants access, the following URL is requested-
                // http://labs.fossasia.org/?code=USER_CODE&state
                if (url.startsWith("http://labs.fossasia.org/")) {
                    //handle URL
                    try {
                        Map<String, String> params = splitQuery(url);
                        if (params.get("code") != null) {
                            authenticate(params.get("code"));
                        } else if (params.get("error") != null) {  //error has occurred
                            Toast.makeText(WordpressWebviewActivity.this, "Error " + params.get("error"), Toast.LENGTH_SHORT).show();
                        } else
                            Toast.makeText(WordpressWebviewActivity.this, "Unknown error occured. Please try again.", Toast.LENGTH_SHORT).show();
                        return true;


                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
                view.loadUrl(url);
                mSwipeRefreshLayout.setRefreshing(true);
                return true;
            }

            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                view.loadUrl(request.getUrl().toString());
                mSwipeRefreshLayout.setRefreshing(true);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                Log.d("URL loaded", url);
                mSwipeRefreshLayout.setRefreshing(false);


            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                Toast.makeText(WordpressWebviewActivity.this, "Your Internet Connection May not be active", Toast.LENGTH_LONG).show();
                mSwipeRefreshLayout.setRefreshing(false);
            }

        });

        mWebView.loadUrl(toLoadURL);
        mSwipeRefreshLayout.setRefreshing(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wordpress_introduction);
        init();


        //todo
        // Upload new Media - https://developer.wordpress.com/docs/api/1.1/post/sites/%24site/media/new/
         //List media - https://developer.wordpress.com/docs/api/1.1/get/sites/%24site/media/ */

/*		mWebView = (WebView) findViewById(R.id.wvWordpressIntroduction);
		mWebView.getSettings().setJavaScriptEnabled(true);

		String customHtml =
		"<html>"+
		    "<body style='background:#cfe'>"+
		        "<center><h2>Wordpress Introduction</h2> " +
		        "------------------------" +
		        "</center>"+
		        "<p>"+
		            "If you want to implement wordpress for Phimpme applicaion, please install some plugin below :"+
		            "<ol>"+
		                "<li>Drupal to WP XML_RPC <a href='http://wordpress.org/extend/plugins/drupal-to-wp-xml-rpc/'>http://wordpress.org/extend/plugins/drupal-to-wp-xml-rpc/</a> </li>"+
		                "<li>Wp-xmlrpc-modernization <a href='http://wordpress.org/extend/plugins/xml-rpc-modernization/'>http://wordpress.org/extend/plugins/xml-rpc-modernization/</a></li>"+
		                "<li>XML-RPC Extended Media Upload <a href='http://wordpress.org/extend/plugins/xml-rpc-extended-media-upload/'>http://wordpress.org/extend/plugins/xml-rpc-extended-media-upload/</a></li>"+
		            "</ol>"+
		            "Then, please access admin page <a href='http://yourdomain/wp-admin/options-writing.php'>http://yourdomain/wp-admin/options-writing.php</a> and check enable XML_RPC"+
		        "</p>"+
		    "</body>"+
		"</html>";
		mWebView.loadData(customHtml, "text/html", "UTF-8");*/


    }
}