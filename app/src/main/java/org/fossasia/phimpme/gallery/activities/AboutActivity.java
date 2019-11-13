package org.fossasia.phimpme.gallery.activities;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.iconics.view.IconicsImageView;
import de.psdev.licensesdialog.LicensesDialog;
import de.psdev.licensesdialog.licenses.ApacheSoftwareLicense20;
import de.psdev.licensesdialog.licenses.MITLicense;
import de.psdev.licensesdialog.model.Notice;
import de.psdev.licensesdialog.model.Notices;
import org.fossasia.phimpme.BuildConfig;
import org.fossasia.phimpme.R;
import org.fossasia.phimpme.base.ThemedActivity;
import org.fossasia.phimpme.gallery.util.CustomTabService;
import org.fossasia.phimpme.utilities.ActivitySwitchHelper;

/** Created by Jibo on 02/03/2016. */
public class AboutActivity extends ThemedActivity implements View.OnClickListener {

  private Toolbar toolbar;

  /** ** CustomTabService */
  private CustomTabService cts;

  /** ** Scroll View */
  private ScrollView scr;

  @Override
  protected void onPostCreate(Bundle savedInstanceState) {
    super.onPostCreate(savedInstanceState);
    setContentView(R.layout.activity_about);
    toolbar = findViewById(R.id.toolbar);
    setNavBarColor();
    cts = new CustomTabService(AboutActivity.this, getPrimaryColor());
    scr = findViewById(R.id.sv_about);
  }

  @Override
  public void onPostResume() {
    super.onPostResume();
    ActivitySwitchHelper.setContext(this);
    setTheme();
  }

  private void setTheme() {
    /* ToolBar*/
    toolbar.setBackgroundColor(getPrimaryColor());
    setSupportActionBar(toolbar);
    toolbar.setNavigationIcon(
        new IconicsDrawable(this)
            .icon(GoogleMaterial.Icon.gmd_arrow_back)
            .color(Color.WHITE)
            .sizeDp(19));
    toolbar.setNavigationOnClickListener(v -> onBackPressed());
    toolbar.setTitle(getString(R.string.about));

    /*Status Bar */
    setStatusBarColor();

    /* Nav Bar */
    setNavBarColor();

    /* Recent App */
    setRecentApp(getString(R.string.about));

    /* Title Cards */
    int color = getAccentColor();
    ((TextView) findViewById(R.id.tv_about_fossasia)).setTextColor(color);
    ((TextView) findViewById(R.id.tv_special_thanks)).setTextColor(color);
    ((TextView) findViewById(R.id.tv_support_label)).setTextColor(color);
    ((TextView) findViewById(R.id.tv_license_heading)).setTextColor(color);
    ((TextView) findViewById(R.id.tv_connect_to_us)).setTextColor(color);

    /* ScrolView */
    setScrollViewColor(scr);

    setThemeOnChangeListener();
    setUpActions();
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.menu_about, menu);
    return super.onCreateOptionsMenu(menu);
  }

  @Override
  public boolean onPrepareOptionsMenu(Menu menu) {
    return super.onPrepareOptionsMenu(menu);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {

    switch (item.getItemId()) {
      case R.id.up_settings:
        startActivity(new Intent(AboutActivity.this, SettingsActivity.class));
        return true;

      default:
        return super.onOptionsItemSelected(item);
    }
  }

  @Override
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.tv_about_fossasia:
        cts.launchUrl(getApplicationContext().getString(R.string.contributors_link));
        break;

      case R.id.iv_github:
      case R.id.tv_github_desc:
      case R.id.tv_github_label:
        cts.launchUrl(getApplicationContext().getString(R.string.phimpme_github));
        break;

      case R.id.tv_report_bug_desc:
      case R.id.tv_report_bug_label:
      case R.id.iv_bug:
        cts.launchUrl(getApplicationContext().getString(R.string.phimpme_github_issues));
        break;

      case R.id.tv_open_camera:
        cts.launchUrl(getApplicationContext().getString(R.string.opencamera_sourceforge));
        break;

      case R.id.tv_leaf_pic:
        cts.launchUrl(getApplicationContext().getString(R.string.leafpic_github));
        break;

      case R.id.iv_license:
      case R.id.tv_license_desc:
      case R.id.tv_license_label:
        cts.launchUrl(getApplicationContext().getString(R.string.phimpme_license));
        break;

      case R.id.iv_library:
      case R.id.tv_library_license_desc:
      case R.id.tv_library_license_label:
        licenseDialog();
        break;

      case R.id.iv_website:
      case R.id.tv_phimpme_website:
      case R.id.tv_phimpme_website_desc:
        cts.launchUrl(getApplicationContext().getString(R.string.phimpme_website));
        break;

      case R.id.iv_facebook:
      case R.id.tv_facebook:
      case R.id.tv_facebook_desc:
        try {
          Intent facebookIntent = new Intent(Intent.ACTION_VIEW);
          String facebookUrl = getFacebookPageURL(getApplicationContext());
          facebookIntent.setData(Uri.parse(facebookUrl));
          startActivity(facebookIntent);
        } catch (ActivityNotFoundException e) {
          cts.launchUrl("https://www.facebook.com/phimpmeapp");
        }
        break;
      case R.id.iv_twitter:
      case R.id.tv_twitter:
      case R.id.tv_twitter_desc:
        try {
          Intent intent =
              new Intent(Intent.ACTION_VIEW, Uri.parse("twitter://user?screen_name=phimpme"));
          startActivity(intent);
        } catch (Exception e) {
          startActivity(
              new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/#!/phimpme")));
        }
        break;
    }
  }

  private void setUpActions() {

    // Fossasia contributors
    findViewById(R.id.tv_about_fossasia).setOnClickListener(this);

    // GitHub
    findViewById(R.id.iv_github).setOnClickListener(this);
    findViewById(R.id.tv_github_desc).setOnClickListener(this);
    findViewById(R.id.tv_github_label).setOnClickListener(this);

    /// Report bug
    findViewById(R.id.tv_report_bug_desc).setOnClickListener(this);
    findViewById(R.id.iv_bug).setOnClickListener(this);
    findViewById(R.id.tv_report_bug_label).setOnClickListener(this);

    // openCamera
    findViewById(R.id.tv_open_camera).setOnClickListener(this);

    // LeafPic
    findViewById(R.id.tv_leaf_pic).setOnClickListener(this);

    // License
    findViewById(R.id.iv_license).setOnClickListener(this);
    findViewById(R.id.tv_license_desc).setOnClickListener(this);
    findViewById(R.id.tv_license_label).setOnClickListener(this);

    // Libs
    findViewById(R.id.iv_library).setOnClickListener(this);
    findViewById(R.id.tv_library_license_label).setOnClickListener(this);
    findViewById(R.id.tv_library_license_desc).setOnClickListener(this);

    // Website
    findViewById(R.id.iv_website).setOnClickListener(this);
    findViewById(R.id.tv_phimpme_website).setOnClickListener(this);
    findViewById(R.id.tv_phimpme_website_desc).setOnClickListener(this);

    // Facebook page
    findViewById(R.id.iv_facebook).setOnClickListener(this);
    findViewById(R.id.tv_facebook).setOnClickListener(this);
    findViewById(R.id.tv_facebook_desc).setOnClickListener(this);

    // Twitter page
    findViewById(R.id.iv_twitter).setOnClickListener(this);
    findViewById(R.id.tv_twitter_desc).setOnClickListener(this);
    findViewById(R.id.tv_twitter).setOnClickListener(this);
  }

  public String getFacebookPageURL(Context context) {

    PackageManager packageManager = context.getPackageManager();
    try {
      int versionCode = packageManager.getPackageInfo("com.facebook.katana", 0).versionCode;
      if (versionCode >= 3002850) { // newer versions of fb app
        return "fb://facewebmodal/f?href=" + "https://www.facebook.com/phimpmeapp";
      } else { // older versions of fb app
        return "fb://page/" + "phimpmeapp";
      }
    } catch (PackageManager.NameNotFoundException e) {
      return "https://www.facebook.com/phimpmeapp"; // normal web url
    }
  }

  private void setThemeOnChangeListener() {

    /* Background */
    findViewById(R.id.clAboutParent).setBackgroundColor(getBackgroundColor());

    /* Cards */
    int color = getCardBackgroundColor();
    ((CardView) findViewById(R.id.card_about_app)).setCardBackgroundColor(color);
    ((CardView) findViewById(R.id.card_special_thanks)).setCardBackgroundColor(color);
    ((CardView) findViewById(R.id.card_support_dev)).setCardBackgroundColor(color);
    ((CardView) findViewById(R.id.card_license)).setCardBackgroundColor(color);
    ((CardView) findViewById(R.id.card_connect_to_us)).setCardBackgroundColor(color);

    // cvSpecialThanks.setBackgroundColor(color);

    /* Icons  */
    // ABOUT APP
    color = getIconColor();
    ((IconicsImageView) findViewById(R.id.iv_library)).setColor(color);
    ((IconicsImageView) findViewById(R.id.iv_license)).setColor(color);

    // ABOUT SUPPORT
    ((IconicsImageView) findViewById(R.id.iv_github)).setColor(color);
    ((IconicsImageView) findViewById(R.id.iv_bug)).setColor(color);

    // CONNECT ICONS
    ((IconicsImageView) findViewById(R.id.iv_website)).setColor(color);
    ((IconicsImageView) findViewById(R.id.iv_facebook)).setColor(color);
    ((IconicsImageView) findViewById(R.id.iv_twitter)).setColor(color);

    /** TextViews * */
    color = getTextColor();
    ((TextView) findViewById(R.id.tv_library_license_desc)).setTextColor(color);
    ((TextView) findViewById(R.id.tv_app_description)).setTextColor(color);
    ((TextView) findViewById(R.id.tv_github_label)).setTextColor(color);
    ((TextView) findViewById(R.id.tv_license_label)).setTextColor(color);
    ((TextView) findViewById(R.id.tv_report_bug_label)).setTextColor(color);
    ((TextView) findViewById(R.id.tv_phimpme_website)).setTextColor(color);
    ((TextView) findViewById(R.id.tv_facebook)).setTextColor(color);
    ((TextView) findViewById(R.id.tv_twitter)).setTextColor(color);

    /** Sub Text Views* */
    color = getSubTextColor();
    ((TextView) findViewById(R.id.tv_version)).setTextColor(color);
    ((TextView) findViewById(R.id.tv_version))
        .setText(getString(R.string.version_title) + " " + BuildConfig.VERSION_NAME);
    ((TextView) findViewById(R.id.tv_license_desc)).setTextColor(color);
    ((TextView) findViewById(R.id.tv_open_camera)).setTextColor(color);
    ((TextView) findViewById(R.id.tv_leaf_pic)).setTextColor(color);
    ((TextView) findViewById(R.id.tv_github_desc)).setTextColor(color);
    ((TextView) findViewById(R.id.tv_library_license_desc)).setTextColor(color);
    ((TextView) findViewById(R.id.tv_report_bug_desc)).setTextColor(color);
    ((TextView) findViewById(R.id.tv_phimpme_website_desc)).setTextColor(color);
    ((TextView) findViewById(R.id.tv_facebook_desc)).setTextColor(color);
    ((TextView) findViewById(R.id.tv_twitter_desc)).setTextColor(color);
  }

  private void licenseDialog() {
    // TODO: 10/07/16 ~Jibe rifai sta roba please!
    final Notices notices = new Notices();
    notices.addNotice(
        new Notice(
            getApplicationContext().getString(R.string.glide),
            getApplicationContext().getString(R.string.glide_github),
            getApplicationContext().getString(R.string.copyright_2014_google),
            new ApacheSoftwareLicense20()));
    notices.addNotice(
        new Notice(
            getApplicationContext().getString(R.string.ion),
            getApplicationContext().getString(R.string.ion_github),
            getApplicationContext().getString(R.string.copyright_2013_koushik_dutta),
            new ApacheSoftwareLicense20()));
    notices.addNotice(
        new Notice(
            getApplicationContext().getString(R.string.android_iconics),
            getApplicationContext().getString(R.string.android_iconics_github),
            getApplicationContext().getString(R.string.copyright_2016_mike_penz),
            new ApacheSoftwareLicense20()));
    notices.addNotice(
        new Notice(
            getApplicationContext().getString(R.string.appintro),
            getApplicationContext().getString(R.string.appintro_github),
            getApplicationContext().getString(R.string.copyright_2015_paolo_rotolo)
                + getApplicationContext().getString(R.string.copyright_2016_maximilian_narr),
            new ApacheSoftwareLicense20()));
    notices.addNotice(
        new Notice(
            getApplicationContext().getString(R.string.ucrop),
            getApplicationContext().getString(R.string.ucrop_github),
            getApplicationContext().getString(R.string.copyright_2016_yolantis),
            new ApacheSoftwareLicense20()));
    notices.addNotice(
        new Notice(
            getApplicationContext().getString(R.string.shiftcolorpicker),
            getApplicationContext().getString(R.string.shiftcolorpicker),
            getApplicationContext().getString(R.string.copyright_2015_bogdasarov_bogdan),
            new MITLicense()));
    notices.addNotice(
        new Notice(
            getApplicationContext().getString(R.string.photoview),
            getApplicationContext().getString(R.string.photoview_github),
            getApplicationContext().getString(R.string.copyright_2011_2012_chris_banes),
            new ApacheSoftwareLicense20()));
    notices.addNotice(
        new Notice(
            getApplicationContext().getString(R.string.circleimageview),
            getApplicationContext().getString(R.string.circleimageview_github),
            getApplicationContext().getString(R.string.copyright_2014_2015_henning_dodenhof),
            new ApacheSoftwareLicense20()));
    notices.addNotice(
        new Notice(
            getApplicationContext().getString(R.string.horizontalwheelview),
            getApplicationContext().getString(R.string.horizontalwheelview_github),
            getApplicationContext().getString(R.string.copyright_2016_mykhailo_schurov),
            new ApacheSoftwareLicense20()));

    new LicensesDialog.Builder(this)
        .setNotices(notices)
        .setIncludeOwnLicense(true)
        .setThemeResourceId(getDialogStyle())
        .build()
        .show();
  }
}
