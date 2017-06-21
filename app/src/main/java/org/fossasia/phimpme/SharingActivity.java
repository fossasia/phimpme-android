package org.fossasia.phimpme;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.share.ShareApi;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;

import org.fossasia.phimpme.base.ThemedActivity;
import org.fossasia.phimpme.leafpic.activities.LFMainActivity;
import org.fossasia.phimpme.leafpic.util.ColorPalette;


import java.io.File;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;

public class SharingActivity extends ThemedActivity implements View.OnClickListener {

    public String saveFilePath;
    public static final String EXTRA_OUTPUT = "extra_output";
    private CallbackManager callbackManager;
    private String caption;
    private boolean atleastOneShare = false;

    private int[] cellcolors = {R.color.facebook_color, R.color.twitter_color, R.color.instagram_color, R.color.other_share_color};
    private int[] icons_drawables = {R.drawable.ic_facebook_black, R.drawable.ic_twitter_black,
                                    R.drawable.ic_instagram_black,R.drawable.ic_share_minimal};
    private int[] titles_text = {R.string.facebook, R.string.twitter, R.string.instagram, R.string.other};

    @BindView(R.id.share_layout) View parent;
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.share_image) ImageView shareImage;
    @BindView(R.id.edittext_share_caption) EditText text_caption;

    @BindViews({R.id.icon_00, R.id.icon_01, R.id.icon_10, R.id.icon_11}) List<ImageView> icons;
    @BindViews({R.id.title_00, R.id.title_01, R.id.title_10, R.id.title_11}) List<TextView> titles;
    @BindViews({R.id.cell_00, R.id.cell_01, R.id.cell_10, R.id.cell_11}) List<View> cells;

    @BindView(R.id.share_done) Button done;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);
        ButterKnife.bind(this);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setupUI();
        initView();
    }

    private void setupUI() {

        toolbar.setTitleTextColor(Color.BLACK);
        toolbar.setTitle(R.string.shareto);
        toolbar.setBackgroundColor(Color.WHITE);
        Drawable backIcon = getToolbarIcon(GoogleMaterial.Icon.gmd_arrow_back);
        backIcon.setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_ATOP);
        toolbar.setNavigationIcon(backIcon);
        setSupportActionBar(toolbar);

        setStatusBarColor(getResources().getColor(R.color.md_grey_400));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            done.getBackground().setColorFilter(
                    getResources().getColor(R.color.share_done_button_color), PorterDuff.Mode.MULTIPLY);
        else
            done.setBackground(new ColorDrawable(getResources().getColor(R.color.share_done_button_color)));

        done.setOnClickListener(this);

        for (int i = 0; i <= 3; i++ ){
            cells.get(i).setOnClickListener(this);
            icons.get(i).setImageResource(icons_drawables[i]);
            titles.get(i).setText(titles_text[i]);
            icons.get(i).setColorFilter(getResources().getColor(cellcolors[i]));
            titles.get(i).setTextColor(getResources().getColor(cellcolors[i]));
        }
    }

    private void initView() {
        saveFilePath = getIntent().getStringExtra(EXTRA_OUTPUT);
        try {
            shareImage.setImageBitmap(BitmapFactory.decodeFile(saveFilePath));
        }catch (OutOfMemoryError error){
            Snackbar.make(parent,"Unable to load Image",Snackbar.LENGTH_INDEFINITE)
                    .setAction("RETRY", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent share = new Intent(getBaseContext(),SharingActivity.class);
                            share.putExtra(EXTRA_OUTPUT,saveFilePath);
                            startActivity(share);
                            finish();
                        }
                    })
                    .show();
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    protected void setStatusBarColor(int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (isTranslucentStatusBar())
                getWindow().setStatusBarColor(ColorPalette.getObscuredColor(color));
            else
                getWindow().setStatusBarColor(color);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.cell_00: //facebook
                setupFacebookAndShare();
                break;
            case R.id.cell_01: //twitter
                Snackbar.make(parent,R.string.coming_soon,Snackbar.LENGTH_LONG).show();
                break;
            case R.id.cell_10: //instagram
                shareToInstagram();
                break;
            case R.id.cell_11: //other
                otherShare();
                break;
            case R.id.share_done:
                if (atleastOneShare)goToHome();
                else
                    Snackbar.make(parent, getResources().getString(R.string.share_not_completed),Snackbar.LENGTH_LONG)
                    .setAction(R.string.exit, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            goToHome();
                        }
                    }).show();

                break;
        }
    }

    private void setupFacebookAndShare() {
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        List<String> permissionNeeds = Arrays.asList("publish_actions");

        //this loginManager helps you eliminate adding a LoginButton to your UI
        LoginManager manager = LoginManager.getInstance();
        manager.logInWithPublishPermissions(this, permissionNeeds);
        manager.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                sharePhotoToFacebook();
            }

            @Override
            public void onCancel() {
                System.out.println("onCancel");
            }

            @Override
            public void onError(FacebookException error) {
                System.out.println("onError");
            }
        });
    }

    private void sharePhotoToFacebook() {
        caption = text_caption.getText().toString();
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        Bitmap image = BitmapFactory.decodeFile(saveFilePath, bmOptions);
        SharePhoto photo = new SharePhoto.Builder()
                .setBitmap(image)
                .setCaption(caption)
                .build();

        SharePhotoContent content = new SharePhotoContent.Builder()
                .addPhoto(photo)
                .build();

        ShareApi.share(content, null);
        atleastOneShare = true;
    }

    private void otherShare() {
        caption = text_caption.getText().toString();
        Uri uri = Uri.fromFile(new File(saveFilePath));
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TEXT, caption);
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
        shareIntent.setType("image/jpeg");
        startActivity(Intent.createChooser(shareIntent, getResources().getText(R.string.send_image)));
    }

    private void shareToInstagram() {
        caption = text_caption.getText().toString();
        Uri uri = Uri.fromFile(new File(saveFilePath));
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setPackage("com.instagram.android");
        share.putExtra(Intent.EXTRA_STREAM, uri);
        share.setType("image/*");
        share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(Intent.createChooser(share, caption));
        atleastOneShare = true;
    }


    @Override
    protected void onActivityResult(int requestCode, int responseCode, Intent data) {
        super.onActivityResult(requestCode, responseCode, data);
        callbackManager.onActivityResult(requestCode, responseCode, data);
        atleastOneShare = true;
    }

    private void goToHome() {
        Intent home = new Intent(SharingActivity.this, LFMainActivity.class);
        startActivity(home);
        finish();
    }
}