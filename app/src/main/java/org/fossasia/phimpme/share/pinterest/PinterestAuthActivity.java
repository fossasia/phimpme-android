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
import butterknife.BindView;
import butterknife.ButterKnife;
import org.fossasia.phimpme.R;
import org.fossasia.phimpme.accounts.AccountViewModel;
import org.fossasia.phimpme.base.ThemedActivity;
import org.fossasia.phimpme.utilities.Constants;
import org.fossasia.phimpme.utilities.PinterestApi;
import org.fossasia.phimpme.utilities.RetrofitClient;
import org.fossasia.phimpme.utilities.Utils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/** Created by codedsun on 13/Oct/2019 */
public class PinterestAuthActivity extends ThemedActivity {

  public static final String TAG = PinterestAuthActivity.class.getName();
  public static final String PINTEREST_GRANT_TYPE = "authorization_code";
  public static final String PINTEREST_AUTH_STATE =
      "2019phimpme"; // FIXME: to be exact in developer console
  public static final String PINTEREST_REDIRECT_URI =
      "https://phimp.me"; // to be exact as in developer console
  public static final String PINTEREST_AUTH_URL =
      "https://api.pinterest.com/oauth/?"
          + "response_type=code&"
          + "redirect_uri="
          + PINTEREST_REDIRECT_URI
          + "&"
          + "client_id="
          + Constants.PINTEREST_APP_ID
          + "&"
          + "scope=read_public,write_public&"
          + "state="
          + PINTEREST_AUTH_STATE;

  @BindView(R.id.web_view)
  WebView webView;

  @BindView(R.id.progress_bar)
  ProgressBar progressBar;

  private PinterestApi pinterestApi;
  private AccountViewModel accountViewModel;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_pinterest_auth);
    ButterKnife.bind(this);
    accountViewModel = ViewModelProviders.of(this).get(AccountViewModel.class);
    webView.setBackgroundColor(Color.TRANSPARENT);
    Log.d(TAG, PINTEREST_AUTH_URL);
    pinterestApi =
        RetrofitClient.getRetrofitClient(Constants.PINTEREST_BASE_URL).create(PinterestApi.class);
    webView.getSettings().setJavaScriptEnabled(true);
    webView.setWebViewClient(
        new WebViewClient() {
          @Override
          public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            progressBar.setVisibility(View.GONE);
          }

          @Override
          public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            // https://phimp.me/?state=2019phimpme&code=87ee9a33518a418c - when yes pressed
            // https://phimp.me/?state=2019phimpme - When no pressed
            if (request.getUrl().toString().contains(PINTEREST_REDIRECT_URI)
                && (request.getUrl().getQueryParameter("state") != null
                    && request.getUrl().getQueryParameter("state").equals(PINTEREST_AUTH_STATE))) {
              if (request.getUrl().getQueryParameter("code") != null) {
                processAuthCode(request.getUrl().getQueryParameter("code"));
                progressBar.setVisibility(View.VISIBLE);
              } else {
                // User has pressed no
                finish();
              }
            }
            return false;
          }
        });
    webView.loadUrl(PINTEREST_AUTH_URL);
  }

  private void processAuthCode(String code) {
    progressBar.setVisibility(View.VISIBLE);
    pinterestApi
        .getUserToken(
            PINTEREST_GRANT_TYPE, Constants.PINTEREST_APP_ID, Constants.PINTEREST_APP_SECRET, code)
        .enqueue(
            new Callback<PinterestUserTokenResp>() {
              @Override
              public void onResponse(
                  Call<PinterestUserTokenResp> call, Response<PinterestUserTokenResp> response) {
                if (response.body() != null && response.isSuccessful()) {
                  PinterestUserTokenResp resp = response.body();
                  fetchUserDetails(resp.getAccessToken());
                } else {
                  progressBar.setVisibility(View.GONE);
                  Utils.showToastShort(
                      PinterestAuthActivity.this, getString(R.string.something_went_wrong));
                  finish();
                }
              }

              @Override
              public void onFailure(Call<PinterestUserTokenResp> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Utils.showToastShort(
                    PinterestAuthActivity.this, getString(R.string.something_went_wrong));
                finish();
              }
            });
  }

  private void fetchUserDetails(String accessToken) {
    pinterestApi
        .getUserDetails(accessToken)
        .enqueue(
            new Callback<PinterestUserResp>() {
              @Override
              public void onResponse(
                  Call<PinterestUserResp> call, Response<PinterestUserResp> response) {
                if (response.body() != null && response.isSuccessful()) {
                  PinterestUserResp resp = response.body();
                  accountViewModel.savePinterestAccount(
                      resp.getData().getUrl().substring(26, resp.getData().getUrl().length() - 1),
                      accessToken);
                  finish();
                } else {
                  Utils.showToastShort(
                      PinterestAuthActivity.this, getString(R.string.something_went_wrong));
                  finish();
                }
              }

              @Override
              public void onFailure(Call<PinterestUserResp> call, Throwable t) {
                Utils.showToastShort(
                    PinterestAuthActivity.this, getString(R.string.something_went_wrong));
                finish();
              }
            });
  }
}
