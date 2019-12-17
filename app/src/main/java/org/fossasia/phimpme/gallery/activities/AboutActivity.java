package org.fossasia.phimpme.gallery.activities;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import butterknife.BindView;
import butterknife.ButterKnife;
import mehdi.sakout.aboutpage.AboutPage;
import mehdi.sakout.aboutpage.Element;
import org.fossasia.phimpme.BuildConfig;
import org.fossasia.phimpme.R;
import org.fossasia.phimpme.gallery.util.CustomTabService;

/** Created by codedsun on 20/11/2019. */
public class AboutActivity extends AppCompatActivity {

  @BindView(R.id.toolbar)
  Toolbar toolbar;

  @BindView(R.id.llAbout)
  LinearLayout llAbout;

  CustomTabService customTabService;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_about);
    ButterKnife.bind(this);
    customTabService =
        new CustomTabService(this, getResources().getColor(R.color.about_background_color));
    setSupportActionBar(toolbar);
    getSupportActionBar().setTitle(R.string.about);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    simulateDayNight(/* DAY */ 0);

    View aboutPage =
        new AboutPage(this)
            .isRTL(false)
            .setImage(R.drawable.ic_launcher_web)
            .setDescription(getString(R.string.app_light_description))
            .addItem(new Element(BuildConfig.VERSION_NAME + "", R.drawable.ic_release))
            .addGroup(getString(R.string.about_special_thanks))
            .addItem(getOpenCameraElement())
            .addItem(getLeafPicElement())
            .addGroup(getString(R.string.connect_to_us))
            .addEmail("phimpme@googlegroups.com")
            .addWebsite(getString(R.string.phimpme_website))
            .addGitHub(getString(R.string.github_phimpme))
            .addFacebook(getString(R.string.facebook_phimpme))
            .addTwitter(getString(R.string.twitter_phimpme))
            .create();

    llAbout.addView(aboutPage);
  }

  @Override
  public boolean onOptionsItemSelected(@NonNull MenuItem item) {
    switch (item.getItemId()) {
      case android.R.id.home:
        finish();
    }
    return super.onOptionsItemSelected(item);
  }

  private Element getOpenCameraElement() {
    return new Element()
        .setTitle(getString(R.string.about_special_thanks_open_camera))
        .setIconDrawable(R.drawable.ic_special_thanks_gift)
        .setOnClickListener(v -> customTabService.launchUrl("http://opencamera.sourceforge.net/"));
  }

  private Element getLeafPicElement() {
    return new Element()
        .setTitle(getString(R.string.about_special_thanks_leafpic))
        .setIconDrawable(R.drawable.ic_special_thanks_gift)
        .setOnClickListener(v -> customTabService.launchUrl("https://github.com/HoraApps/LeafPic"));
  }

  void simulateDayNight(int currentSetting) {
    final int DAY = 0;
    final int NIGHT = 1;
    final int FOLLOW_SYSTEM = 3;

    int currentNightMode =
        getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
    if (currentSetting == DAY && currentNightMode != Configuration.UI_MODE_NIGHT_NO) {
      AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
    } else if (currentSetting == NIGHT && currentNightMode != Configuration.UI_MODE_NIGHT_YES) {
      AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
    } else if (currentSetting == FOLLOW_SYSTEM) {
      AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
    }
  }
}
