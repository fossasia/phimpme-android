package org.fossasia.phimpme.gallery.util;

import android.app.Activity;
import android.content.ComponentName;
import android.net.Uri;
import android.widget.Toast;
import androidx.browser.customtabs.CustomTabsClient;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.browser.customtabs.CustomTabsServiceConnection;
import androidx.browser.customtabs.CustomTabsSession;
import org.fossasia.phimpme.R;

public class CustomTabService {
  private CustomTabsClient mCustomTabsClient;
  private CustomTabsSession mCustomTabsSession;
  private CustomTabsServiceConnection mCustomTabsServiceConnection;
  private CustomTabsIntent mCustomTabsIntent;
  private Activity activity;
  private int color;

  public CustomTabService(Activity act, int c) {
    this.activity = act;
    this.color = c;
    init();
  }

  private void init() {
    mCustomTabsServiceConnection =
        new CustomTabsServiceConnection() {
          @Override
          public void onCustomTabsServiceConnected(
              ComponentName componentName, CustomTabsClient customTabsClient) {
            mCustomTabsClient = customTabsClient;
            mCustomTabsClient.warmup(0L);
            mCustomTabsSession = mCustomTabsClient.newSession(null);
          }

          @Override
          public void onServiceDisconnected(ComponentName name) {
            mCustomTabsClient = null;
          }
        };
    CustomTabsClient.bindCustomTabsService(
        activity, activity.getPackageName(), mCustomTabsServiceConnection);
    mCustomTabsIntent =
        new CustomTabsIntent.Builder(mCustomTabsSession)
            .setShowTitle(true)
            .setToolbarColor(color)
            .build();
  }

  public void launchUrl(String Url) {
    try {
      mCustomTabsIntent.launchUrl(activity, Uri.parse(Url));
    } catch (Exception e) {
      Toast.makeText(
              activity.getApplication(), R.string.error_title + e.toString(), Toast.LENGTH_SHORT)
          .show();
    }
  }
}
