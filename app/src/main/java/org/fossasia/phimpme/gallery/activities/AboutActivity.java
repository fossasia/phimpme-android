package org.fossasia.phimpme.gallery.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;

import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.iconics.view.IconicsImageView;

import org.fossasia.phimpme.BuildConfig;
import org.fossasia.phimpme.R;
import org.fossasia.phimpme.base.ThemedActivity;
import org.fossasia.phimpme.gallery.util.CustomTabService;
import org.fossasia.phimpme.utilities.ActivitySwitchHelper;

import de.psdev.licensesdialog.LicensesDialog;
import de.psdev.licensesdialog.licenses.ApacheSoftwareLicense20;
import de.psdev.licensesdialog.licenses.MITLicense;
import de.psdev.licensesdialog.model.Notice;
import de.psdev.licensesdialog.model.Notices;

/**
 * Created by Jibo on 02/03/2016.
 */
public class AboutActivity extends ThemedActivity {

    private Toolbar toolbar;

    /**** CustomTabService*/
    private CustomTabService cts;

    /**** Scroll View*/
    private ScrollView scr;

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        setNavBarColor();
        cts = new CustomTabService(AboutActivity.this,getPrimaryColor());
        scr = (ScrollView)findViewById(R.id.aboutAct_scrollView);

    }

    @Override
    public void onPostResume() {
        super.onPostResume();
        ActivitySwitchHelper.setContext(this);
        setTheme();
    }

    private void setTheme(){
        /**** ToolBar *****/
        toolbar.setBackgroundColor(getPrimaryColor());
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(
                new IconicsDrawable(this)
                        .icon(GoogleMaterial.Icon.gmd_arrow_back)
                        .color(Color.WHITE)
                        .sizeDp(19));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        //toolbar.setTitle(getString(R.string.about));

        /**** Status Bar ****/
        setStatusBarColor();

        /**** Nav Bar *******/
        setNavBarColor();

        /**** Recent App ****/
        setRecentApp(getString(R.string.about));

        /**** Title Cards ***/
        int color=getAccentColor();
        ((TextView) findViewById(R.id.about_app_title)).setTextColor(color);
        ((TextView) findViewById(R.id.about_special_thanks_title)).setTextColor(color);
        ((TextView) findViewById(R.id.about_support_title)).setTextColor(color);
        ((TextView) findViewById(R.id.about_license_title)).setTextColor(color);
        ((TextView) findViewById(R.id.about_special_thanks_title)).setTextColor(color);

        /***** LeafPic Header *****/
        /*
        Glide.with(this)
                .load("https://lh5.googleusercontent.com/-aX4YeN8Cvdc/VizLlektHNI/AAAAAAAATE0/c8NUmcEs46QTVf-HITTbvo3e5dja6JwoQCL0B/w958-h539-no/flat_landscape_wallpaper_by_othrod-d8crs50.png")
                .priority(Priority.HIGH)
                .animate(R.anim.fade_in)
                .into((ImageView) findViewById(R.id.leafpic_header));
        */
        /***** Donald Card *****/
        /***** Images *****/
/*        Glide.with(this)
                .load("https://lh3.googleusercontent.com/-4lGmk-K4r4U/Vw1Vj8yERrI/AAAAAAAANww/FIsb58PcO-U-9AfD8FXfruK1c75SZ184QCL0B/w958-h539-no/asd.png")
                .placeholder(getPlaceHolder())
                .priority(Priority.HIGH)
                .animate(R.anim.fade_in)
                .into((ImageView) findViewById(R.id.donald_header_img));*/

        /***** ScrolView *****/
        setScrollViewColor(scr);

        setThemeOnChangeListener();
        setUpActions();
    }

    private void setUpActions(){

        //Fossasia contributors
        findViewById(R.id.about_fossasia).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cts.launchUrl("https://github.com/fossasia/phimpme-android/graphs/contributors");
            }
        });
        //GitHub
        findViewById(R.id.ll_about_support_github).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cts.launchUrl("https://github.com/fossasia/phimpme-android");
            }
        });
        ///Report bug
        findViewById(R.id.ll_about_report_bug).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cts.launchUrl("https://github.com/fossasia/phimpme-android/issues");
            }
        });
         //openCamera
        findViewById(R.id.about_patryk_goworowski_item_sub).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cts.launchUrl("http://opencamera.sourceforge.net/");
            }
        });
        //LeafPic
        findViewById(R.id.about_community_members_sub).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cts.launchUrl("https://github.com/HoraApps/LeafPic");
            }
        });
        
        //License
        findViewById(R.id.ll_about_license).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { cts.launchUrl("https://github.com/fossasia/phimpme-android/blob/master/LICENSE.md");}
        });

        //Libs
        findViewById(R.id.ll_about_libs).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {licenseDialog();}
        });

    }

    private void setThemeOnChangeListener(){

        /** BackGround **/
        findViewById(R.id.about_background).setBackgroundColor(getBackgroundColor());

        /** Cards **/
        int color = getCardBackgroundColor();
        ((CardView) findViewById(R.id.about_app_card)).setCardBackgroundColor(color);
        ((CardView) findViewById(R.id.about_special_thanks_card)).setCardBackgroundColor(color);
        ((CardView) findViewById(R.id.about_support_card)).setCardBackgroundColor(color);
        ((CardView) findViewById(R.id.about_license_card)).setCardBackgroundColor(color);
        //cvSpecialThanks.setBackgroundColor(color);

        /** Icons **/
        //ABOUT APP
        color = getIconColor();
        ((IconicsImageView) findViewById(R.id.about_libs_icon)).setColor(color);
        ((IconicsImageView) findViewById(R.id.about_license_icon)).setColor(color);

        //ABOUT SUPPORT
        ((IconicsImageView) findViewById(R.id.about_support_github_icon)).setColor(color);
        ((IconicsImageView) findViewById(R.id.about_support_report_bug_icon)).setColor(color);

        /** TextViews **/
        color = getTextColor();
        ((TextView) findViewById(R.id.about_libs_item)).setTextColor(color);
        ((TextView) findViewById(R.id.about_app_light_description)).setTextColor(color);
        ((TextView) findViewById(R.id.about_support_github_item)).setTextColor(color);
        ((TextView) findViewById(R.id.about_license_item)).setTextColor(color);
        ((TextView) findViewById(R.id.about_support_report_bug_item)).setTextColor(color);

        /** Sub Text Views**/
        color = getSubTextColor();
        ((TextView) findViewById(R.id.about_version_item_sub)).setTextColor(color);
        ((TextView) findViewById(R.id.about_version_item_sub)).setText("Version: "+BuildConfig.VERSION_NAME);
        ((TextView) findViewById(R.id.about_libs_item_sub)).setTextColor(color);
        ((TextView) findViewById(R.id.about_patryk_goworowski_item_sub)).setTextColor(color);
        ((TextView) findViewById(R.id.about_community_members_sub)).setTextColor(color);
        ((TextView) findViewById(R.id.about_community_you_sub)).setTextColor(color);
        ((TextView) findViewById(R.id.about_support_github_item_sub)).setTextColor(color);
        ((TextView) findViewById(R.id.about_license_item_sub)).setTextColor(color);
        ((TextView) findViewById(R.id.about_support_report_bug_sub)).setTextColor(color);
    }

    private void licenseDialog() {
        // TODO: 10/07/16 ~Jibe rifai sta roba please!
        final Notices notices = new Notices();
        notices.addNotice(new Notice("Glide", "http://github.com/bumptech/glide", "Copyright 2014 Google, Inc. All rights reserved.", new ApacheSoftwareLicense20()));
        notices.addNotice(new Notice("Ion", "http://github.com/koush/ion", "Copyright 2013 Koushik Dutta (2013)", new ApacheSoftwareLicense20()));
        notices.addNotice(new Notice("Android-Iconics", "http://github.com/mikepenz/Android-Iconics", "Copyright 2016 Mike Penz", new ApacheSoftwareLicense20()));
        notices.addNotice(new Notice("AppIntro", "http://github.com/PaoloRotolo/AppIntro", "Copyright 2015 Paolo Rotolo\n"
                + "Copyright 2016 Maximilian Narr", new ApacheSoftwareLicense20()));
        notices.addNotice(new Notice("uCrop", "http://github.com/Yalantis/uCrop", "Copyright 2016, Yalantis", new ApacheSoftwareLicense20()));
        notices.addNotice(new Notice("ShiftColorPicker", "http://github.com/DASAR/ShiftColorPicker", "Copyright (c) 2015 Bogdasarov Bogdan", new MITLicense()));
        notices.addNotice(new Notice("PhotoView", "http://github.com/chrisbanes/PhotoView", "Copyright 2011, 2012 Chris Banes.", new ApacheSoftwareLicense20()));
        notices.addNotice(new Notice("CircleImageView", "https://github.com/hdodenhof/CircleImageView", "Copyright 2014 - 2015 Henning Dodenhof", new ApacheSoftwareLicense20()));

        new LicensesDialog.Builder(this)
                .setNotices(notices)
                .setIncludeOwnLicense(true)
                .setThemeResourceId(getDialogStyle())
                .build()
                .show();
    }
}
