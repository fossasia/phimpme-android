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

/** Created by codedsun on 26/9/2019. */
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
    scr = findViewById(R.id.svAbout);
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
    toolbar.setNavigationOnClickListener(
            v -> onBackPressed());
     toolbar.setTitle(getString(R.string.about));

    /*Status Bar */
    setStatusBarColor();

    /* Nav Bar */
    setNavBarColor();

    /* Recent App */
    setRecentApp(getString(R.string.about));

    /* Title Cards */
    int color = getAccentColor();
    ((TextView) findViewById(R.id.tvAboutFossasia)).setTextColor(color);
    ((TextView) findViewById(R.id.tvSpecialThanksLabel)).setTextColor(color);
    ((TextView) findViewById(R.id.tvSupportLabel)).setTextColor(color);
    ((TextView) findViewById(R.id.tvLicenseHeading)).setTextColor(color);
    ((TextView) findViewById(R.id.tvSpecialThanksLabel)).setTextColor(color);
    ((TextView) findViewById(R.id.tvConnectToUsLabel)).setTextColor(color);

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
    switch (v.getId()){
      case R.id.tvAboutFossasia :
        cts.launchUrl(getApplicationContext().getString(R.string.contributors_link));
        break;

      case R.id.ivGithub:
      case R.id.tvGithubDesc:
      case R.id.tvGithubLabel:
        cts.launchUrl(getApplicationContext().getString(R.string.phimpme_github));
        break;

      case R.id.tvReportBugDesc:
      case R.id.tvReportBugLabel:
      case R.id.ivBug:
        cts.launchUrl(getApplicationContext().getString(R.string.phimpme_github_issues));
        break;

      case R.id.tvOpenCamera :
        cts.launchUrl(getApplicationContext().getString(R.string.opencamera_sourceforge));
        break;

      case R.id.tvLeafPic :
        cts.launchUrl(getApplicationContext().getString(R.string.leafpic_github));
        break;

      case R.id.ivLicense:
      case R.id.tvLicenseDesc:
      case R.id.tvLicenseLabel:
        cts.launchUrl(getApplicationContext().getString(R.string.phimpme_license));
        break;

      case R.id.ivLibrary:
      case R.id.tvLibraryLicenseDesc:
      case R.id.tvLibraryLicenseLabel:
        licenseDialog();
        break;

      case R.id.ivWebsite:
      case R.id.tvPhimpmeWebsite:
      case R.id.tvPhimpmeWebsiteDesc:
        cts.launchUrl(getApplicationContext().getString(R.string.phimpme_website));
        break;

      case R.id.ivFacebook:
      case R.id.tvFacebook:
      case R.id.tvFacebookDesc:
          try {
           Intent facebookIntent = new Intent(Intent.ACTION_VIEW);
           String facebookUrl = getFacebookPageURL(getApplicationContext());
           facebookIntent.setData(Uri.parse(facebookUrl));
           startActivity(facebookIntent);
          } catch (ActivityNotFoundException e) {
            cts.launchUrl("https://www.facebook.com/phimpmeapp");
          }
          break;
      case R.id.ivTwitter:
        case R.id.tvTwitter:
        case R.id.tvTwitterDesc:
            try {
                Intent intent =
                        new Intent(
                                Intent.ACTION_VIEW, Uri.parse("twitter://user?screen_name=phimpme"));
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
    findViewById(R.id.tvAboutFossasia)
        .setOnClickListener(this);

    // GitHub
    findViewById(R.id.ivGithub).setOnClickListener(this);
    findViewById(R.id.tvGithubDesc).setOnClickListener(this);
    findViewById(R.id.tvGithubLabel).setOnClickListener(this);

    /// Report bug
    findViewById(R.id.tvReportBugDesc).setOnClickListener(this);
    findViewById(R.id.ivBug).setOnClickListener(this);
    findViewById(R.id.tvReportBugLabel).setOnClickListener(this);

    // openCamera
    findViewById(R.id.tvOpenCamera)
        .setOnClickListener(this);

    // LeafPic
    findViewById(R.id.tvLeafPic).setOnClickListener(this);

    // License
    findViewById(R.id.ivLicense).setOnClickListener(this);
    findViewById(R.id.tvLicenseDesc).setOnClickListener(this);
    findViewById(R.id.tvLicenseLabel).setOnClickListener(this);

    // Libs
    findViewById(R.id.ivLibrary).setOnClickListener(this);
    findViewById(R.id.tvLibraryLicenseLabel).setOnClickListener(this);
    findViewById(R.id.tvLibraryLicenseDesc).setOnClickListener(this);

    // Website
    findViewById(R.id.ivWebsite).setOnClickListener(this);
    findViewById(R.id.tvPhimpmeWebsite).setOnClickListener(this);
    findViewById(R.id.tvPhimpmeWebsiteDesc).setOnClickListener(this);

    // Facebook page
    findViewById(R.id.ivFacebook).setOnClickListener(this);
    findViewById(R.id.tvFacebook).setOnClickListener(this);
    findViewById(R.id.tvFacebookDesc).setOnClickListener(this);

    // Twitter page
    findViewById(R.id.ivTwitter).setOnClickListener(this);
    findViewById(R.id.tvTwitterDesc).setOnClickListener(this);
    findViewById(R.id.tvTwitter).setOnClickListener(this);
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
    ((CardView) findViewById(R.id.cardAboutApp)).setCardBackgroundColor(color);
    ((CardView) findViewById(R.id.cardSpecialThanks)).setCardBackgroundColor(color);
    ((CardView) findViewById(R.id.cardSupportDev)).setCardBackgroundColor(color);
    ((CardView) findViewById(R.id.cardLicense)).setCardBackgroundColor(color);
    ((CardView) findViewById(R.id.cardConnectToUs)).setCardBackgroundColor(color);

    // cvSpecialThanks.setBackgroundColor(color);

    /* Icons  */
    // ABOUT APP
    color = getIconColor();
    ((IconicsImageView) findViewById(R.id.ivLibrary)).setColor(color);
    ((IconicsImageView) findViewById(R.id.ivLicense)).setColor(color);

    // ABOUT SUPPORT
    ((IconicsImageView) findViewById(R.id.ivGithub)).setColor(color);
    ((IconicsImageView) findViewById(R.id.ivBug)).setColor(color);

    // CONNECT ICONS
    ((IconicsImageView) findViewById(R.id.ivWebsite)).setColor(color);
    ((IconicsImageView) findViewById(R.id.ivFacebook)).setColor(color);
    ((IconicsImageView) findViewById(R.id.ivTwitter)).setColor(color);

    /** TextViews * */
    color = getTextColor();
    ((TextView) findViewById(R.id.tvLibraryLicenseLabel)).setTextColor(color);
    ((TextView) findViewById(R.id.tvAppDescription)).setTextColor(color);
    ((TextView) findViewById(R.id.tvGithubLabel)).setTextColor(color);
    ((TextView) findViewById(R.id.tvLicenseLabel)).setTextColor(color);
    ((TextView) findViewById(R.id.tvReportBugLabel)).setTextColor(color);
    ((TextView) findViewById(R.id.tvPhimpmeWebsite)).setTextColor(color);
    ((TextView) findViewById(R.id.tvFacebook)).setTextColor(color);
    ((TextView) findViewById(R.id.tvTwitter)).setTextColor(color);

    /** Sub Text Views* */
    color = getSubTextColor();
    ((TextView) findViewById(R.id.tvVersion)).setTextColor(color);
    ((TextView) findViewById(R.id.tvVersion))
        .setText(getString(R.string.version_title)
                + " "
                + BuildConfig.VERSION_NAME);
    ((TextView) findViewById(R.id.tvLicenseDesc)).setTextColor(color);
    ((TextView) findViewById(R.id.tvOpenCamera)).setTextColor(color);
    ((TextView) findViewById(R.id.tvLeafPic)).setTextColor(color);
    ((TextView) findViewById(R.id.tvGithubDesc)).setTextColor(color);
    ((TextView) findViewById(R.id.tvLicenseDesc)).setTextColor(color);
    ((TextView) findViewById(R.id.tvReportBugDesc)).setTextColor(color);
    ((TextView) findViewById(R.id.tvPhimpmeWebsiteDesc)).setTextColor(color);
    ((TextView) findViewById(R.id.tvFacebookDesc)).setTextColor(color);
    ((TextView) findViewById(R.id.tvTwitterDesc)).setTextColor(color);
  }

  private void licenseDialog() {
     //TODO: 10/07/16 ~Jibe rifai sta roba please!
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
