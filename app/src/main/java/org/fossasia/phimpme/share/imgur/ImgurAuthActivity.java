package org.fossasia.phimpme.share.imgur;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import org.fossasia.phimpme.R;
import org.fossasia.phimpme.base.ThemedActivity;
import org.fossasia.phimpme.gallery.util.AlertDialogsHelper;
import org.fossasia.phimpme.utilities.BasicCallBack;
import org.fossasia.phimpme.utilities.Constants;

import butterknife.BindView;
import butterknife.ButterKnife;

import static org.fossasia.phimpme.utilities.Constants.IMGUR_LOGIN_URL;

/**
 * Created by manuja on 16/7/17.
 */

public class ImgurAuthActivity extends ThemedActivity {
    private static final String REDIRECT_URL = "https://org.fossasia.phimpme";
    static BasicCallBack imgurCallBack;
    @BindView(R.id.login_parent)
    View parent;
    AlertDialog dialog;
    AlertDialog.Builder progressDialog;


    public static void setBasicCallBack(BasicCallBack basicCallBack) {
        imgurCallBack = basicCallBack;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account_login_activity);
        ButterKnife.bind(this);
        progressDialog = new AlertDialog.Builder(ImgurAuthActivity.this, getDialogStyle());
        dialog = AlertDialogsHelper.getProgressDialog(ImgurAuthActivity.this, progressDialog,
                getString(R.string.authenticating_your_app_message), getString(R.string.please_wait));
        dialog.show();

        WebView imgurWebView = findViewById(R.id.twitterLoginWebView);
        imgurWebView.setBackgroundColor(Color.TRANSPARENT);
        imgurWebView.loadUrl(IMGUR_LOGIN_URL);
        imgurWebView.getSettings().setJavaScriptEnabled(true);

        imgurWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {

                if (url.contains(REDIRECT_URL)) {

                    if (url.contains("/?error=")) {
                        view.loadUrl(IMGUR_LOGIN_URL);
                        return true;
                    }
                    Log.d("user", url);

                    // We will extract the info from the callback url
                    splitUrl(url, view);
                } else {
                    view.loadUrl(url);
                }

                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

                if (dialog != null) {
                    dialog.dismiss();
                }
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);

                if (dialog != null) {
                    dialog.show();
                }
            }
        });
    }
    private void splitUrl(String url, WebView view) {
        String[] outerSplit = url.split("\\#")[1].split("\\&");
        String username = null;
        String accessToken = null;
        String refreshToken = null;
        long accessTokenExpiration = 0;
        int index = 0;

        for (String s : outerSplit) {
            String[] innerSplit = s.split("\\=");

            switch (index) {
                // Access Token
                case 0:
                    accessToken = innerSplit[1];
                    break;

                // Access Token Expiration
                case 1:
                    long expiresIn = Long.parseLong(innerSplit[1]);
                    accessTokenExpiration = System.currentTimeMillis() + (expiresIn * DateUtils.SECOND_IN_MILLIS);
                    break;

                // Token Type, not using
                case 2:
                    //NO OP
                    break;

                // Refresh Token
                case 3:
                    refreshToken = innerSplit[1];
                    break;

                // Username
                case 4:
                    username = innerSplit[1];
                    break;
                default:

            }

            index++;
        }

        // Make sure that everything was set
        if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(accessToken) &&
                !TextUtils.isEmpty(refreshToken) && accessTokenExpiration > 0) {
            view.clearHistory();
            view.clearCache(true);
            view.clearFormData();
            Bundle bundle= new Bundle();
            bundle.putString(getString(R.string.auth_token), accessToken);
            bundle.putString(getString(R.string.auth_username),username);
            imgurCallBack.callBack(Constants.SUCCESS, bundle);
//                        setResult(Activity.RESULT_OK, new Intent().putExtra(IMGUR_KEY_LOGGED_IN, true));
            finish();
        }
    }
}
