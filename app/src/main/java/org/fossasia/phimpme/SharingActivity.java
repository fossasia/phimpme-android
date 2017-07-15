package org.fossasia.phimpme;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.share.ShareApi;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.mikepenz.community_material_typeface_library.CommunityMaterial;
import com.mikepenz.iconics.view.IconicsImageView;
import com.pinterest.android.pdk.PDKCallback;
import com.pinterest.android.pdk.PDKClient;
import com.pinterest.android.pdk.PDKException;
import com.pinterest.android.pdk.PDKResponse;

import org.fossasia.phimpme.base.ThemedActivity;
import org.fossasia.phimpme.editor.editimage.view.imagezoom.ImageViewTouch;
import org.fossasia.phimpme.leafpic.activities.LFMainActivity;
import org.fossasia.phimpme.leafpic.util.AlertDialogsHelper;
import org.fossasia.phimpme.leafpic.util.ThemeHelper;
import org.fossasia.phimpme.sharetwitter.HelperMethods;
import org.fossasia.phimpme.sharetwitter.LoginActivity;
import org.fossasia.phimpme.utilities.ActivitySwitchHelper;
import org.fossasia.phimpme.utilities.Constants;
import org.fossasia.phimpme.utilities.Utils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;

import static org.fossasia.phimpme.utilities.Utils.copyToClipBoard;
import static org.fossasia.phimpme.utilities.Utils.getBitmapFromPath;
import static org.fossasia.phimpme.utilities.Utils.getStringImage;


public class SharingActivity extends ThemedActivity implements View.OnClickListener {

    public static final String EXTRA_OUTPUT = "extra_output";
    public String saveFilePath;
    ThemeHelper themeHelper;
    @BindView(R.id.share_layout)
    View parent;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.share_image)
    ImageViewTouch shareImage;
    @BindView(R.id.edittext_share_caption)
    TextView text_caption;
    @BindViews({R.id.icon_00, R.id.icon_01, R.id.icon_10, R.id.icon_11,R.id.icon_20,R.id.icon_21,R.id.icon_30,R.id.icon_31, R.id.icon_40})
    List<ImageView> icons;
    @BindViews({R.id.title_00, R.id.title_01, R.id.title_10, R.id.title_11,R.id.title_20,R.id.title_21,R.id.title_30,R.id.title_31, R.id.title_40})
    List<TextView> titles;
    @BindViews({R.id.cell_00, R.id.cell_01, R.id.cell_10, R.id.cell_11,R.id.cell_20,R.id.cell_21,R.id.cell_30,R.id.cell_31,R.id.cell_40})
    List<View> cells;
    @BindView(R.id.share_done)
    Button done;
    @BindView(R.id.button_text_focus)
    IconicsImageView editFocus;
    @BindView(R.id.edit_text_caption_container)
    RelativeLayout captionLayout;
    private CallbackManager callbackManager;
    private String caption;
    private boolean atleastOneShare = false;
    private int[] cellcolors = {R.color.facebook_color, R.color.twitter_color, R.color.instagram_color,
            R.color.wordpress_color,R.color.pinterest_color,R.color.flickr_color,R.color.nextcloud_color, R.color.imgur_color ,R.color.other_share_color};
    private int[] icons_drawables = {R.drawable.ic_facebook_black, R.drawable.ic_twitter_black,
            R.drawable.ic_instagram_black, R.drawable.ic_wordpress_black, R.drawable.ic_pinterest_black,
            R.drawable.ic_flickr, R.drawable.ic_nextcloud, R.drawable.ic_imgur,R.drawable.ic_share_minimal};
    private int[] titles_text = {R.string.facebook, R.string.twitter, R.string.instagram,
            R.string.wordpress, R.string.pinterest, R.string.flickr, R.string.nextcloud,R.string.imgur,R.string.other};
    private Context context;
    Utils utils = new Utils();
    Bitmap finalBmp;
    Boolean isPostedOnTwitter =false;
    String boardID;



    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        setContentView(R.layout.activity_share);
        context = this;
        themeHelper = new ThemeHelper(this);
        ActivitySwitchHelper.setContext(this);
        ButterKnife.bind(this);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setupUI();
        initView();
        setStatusBarColor();
        checknetwork();
    }

    private boolean checknetwork() {
        ConnectivityManager connect =
                (ConnectivityManager)getSystemService(getBaseContext().CONNECTIVITY_SERVICE);
        if(utils.isInternetOn(connect)){
            return true;
        }else
            Snackbar.make(parent, R.string.not_connected, Snackbar.LENGTH_LONG).show();
        return false;
    }

    private void setupUI() {

        toolbar.setTitle(R.string.shareto);
        toolbar.setBackgroundColor(themeHelper.getPrimaryColor());
        toolbar.setNavigationIcon(getToolbarIcon(CommunityMaterial.Icon.cmd_arrow_left));
        setSupportActionBar(toolbar);
        parent.setBackgroundColor(themeHelper.getBackgroundColor());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            done.getBackground().setColorFilter(getResources().getColor(R.color.share_done_button_color), PorterDuff.Mode.MULTIPLY);
        else
            done.setBackground(new ColorDrawable(getResources().getColor(R.color.share_done_button_color)));

        done.setOnClickListener(this);
        captionLayout.setOnClickListener(this);

        text_caption.getBackground().mutate().setColorFilter(getTextColor(), PorterDuff.Mode.SRC_ATOP);
        text_caption.setTextColor(getTextColor());
        text_caption.setHintTextColor(getSubTextColor());

        for (int i = 0; i <= 8; i++) {
            cells.get(i).setOnClickListener(this);
            icons.get(i).setImageResource(icons_drawables[i]);
            titles.get(i).setText(titles_text[i]);
            icons.get(i).setColorFilter(getResources().getColor(cellcolors[i]));
            titles.get(i).setTextColor(getResources().getColor(cellcolors[i]));
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        editFocus.setColor(getIconColor());
    }

    private void initView() {
        saveFilePath = getIntent().getStringExtra(EXTRA_OUTPUT);
        Glide.with(this)
                .load(Uri.fromFile(new File(saveFilePath)))
                .thumbnail(0.5f)
                .centerCrop()
                .animate(R.anim.fade_in)
                .into(shareImage);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cell_00: //facebook
                setupFacebookAndShare();
                break;
            case R.id.cell_01: //twitter
                postToTwitter();
                break;
            case R.id.cell_10: //instagram
                shareToInstagram();
                break;
            case R.id.cell_11: //wordpress
                break;
            case R.id.cell_20: //pinterest
                openPinterestDialogBox();
                break;
            case R.id.cell_21: //flickr
                break;
            case R.id.cell_30: //nextcloud
                break;
            case R.id.cell_31: //imgur
                imgurShare();
                break;
            case R.id.cell_40: //othershare
                otherShare();
                break;
            case R.id.share_done:
                if (atleastOneShare) goToHome();
                else
                    Snackbar.make(parent, getResources().getString(R.string.share_not_completed), Snackbar.LENGTH_LONG)
                            .setAction(R.string.exit, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    goToHome();
                                }
                            }).show();
                break;
            case R.id.edit_text_caption_container:
                 openCaptionDialogBox();
                break;
        }
    }

    private void openCaptionDialogBox() {
        AlertDialog.Builder captionDialogBuilder = new AlertDialog.Builder(SharingActivity.this, getDialogStyle());
        final EditText captionEditText = getCaptionDialog(this, captionDialogBuilder);
        if (caption!=null) {
            captionEditText.setText(caption);
            captionEditText.setSelection(caption.length());
        }

        captionDialogBuilder.setNegativeButton(getString(R.string.cancel).toUpperCase(), null);
        captionDialogBuilder.setPositiveButton(getString(R.string.ok_action).toUpperCase(), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //This should br empty it will be overwrite later
                //to avoid dismiss of the dialog on wrong password
            }
        });

        final AlertDialog passwordDialog = captionDialogBuilder.create();
        passwordDialog.show();

        passwordDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String captionText = captionEditText.getText().toString();
                if (!captionText.isEmpty()){
                    caption =captionText;
                    text_caption.setText(caption);
                }
                passwordDialog.dismiss();
            }
        });
    }

    private void openPinterestDialogBox() {
        AlertDialog.Builder captionDialogBuilder = new AlertDialog.Builder(SharingActivity.this, getDialogStyle());
        final EditText captionEditText = getCaptionDialog(this, captionDialogBuilder);

        captionEditText.setHint(R.string.hint_boardID);

        captionDialogBuilder.setNegativeButton(getString(R.string.cancel).toUpperCase(), null);
        captionDialogBuilder.setPositiveButton(getString(R.string.post_action).toUpperCase(), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //This should br empty it will be overwrite later
                //to avoid dismiss of the dialog on wrong password
            }
        });

        final AlertDialog passwordDialog = captionDialogBuilder.create();
        passwordDialog.show();

        passwordDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String captionText = captionEditText.getText().toString();
                boardID =captionText;
                shareToPinterest(boardID);
                passwordDialog.dismiss();
            }
        });
    }

    private void shareToPinterest(final String boardID) {
        Bitmap image = getBitmapFromPath(saveFilePath);
        PDKClient
                .getInstance().createPin(caption, boardID, image, null, new PDKCallback() {
            @Override
            public void onSuccess(PDKResponse response) {
                Log.d(getClass().getName(), response.getData().toString());
                Snackbar.make(parent, R.string.pinterest_post, Snackbar.LENGTH_LONG).show();
                //Toast.makeText(SharingActivity.this,message,Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onFailure(PDKException exception) {
                Log.e(getClass().getName(), exception.getDetailMessage());
                Snackbar.make(parent, R.string.Pinterest_fail, Snackbar.LENGTH_LONG).show();
                //Toast.makeText(SharingActivity.this, boardID + caption, Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void postToTwitter() {
        if(checknetwork()) {
            Glide.with(this)
                    .load(Uri.fromFile(new File(saveFilePath)))
                    .asBitmap()
                    .into(new SimpleTarget<Bitmap>(1024, 512) {
                        @Override
                        public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                            finalBmp = resource;
                            new PostToTwitterAsync().execute();

                        }
                    });
        }else{
            Snackbar.make(parent, R.string.not_connected, Snackbar.LENGTH_LONG).show();
        }
    }

    private void uploadOnTwitter() {
        if (LoginActivity.isActive(context)) {
            final File f3 = new File(Environment.getExternalStorageDirectory() + "/twitter_upload/");
            final File file = new File(Environment.getExternalStorageDirectory() + "/twitter_upload/" + "temp" + ".png");
            if (!f3.exists())
                f3.mkdirs();
            OutputStream outStream;
            try {
                outStream = new FileOutputStream(file);
                finalBmp.compress(Bitmap.CompressFormat.PNG, 100, outStream);
                outStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            String finalFile = file.getAbsolutePath();
            HelperMethods.postToTwitterWithImage(context, finalFile, caption, new HelperMethods.TwitterCallback() {
                @Override
                public void onFinsihed(Boolean response) {
                    isPostedOnTwitter = response;
                    file.delete();
                }
            });
        } else {
            startActivity(new Intent(context, LoginActivity.class));
        }
    }

    private void setupFacebookAndShare() {
        if(checknetwork()) {
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
        }else
            Snackbar.make(parent, R.string.not_connected, Snackbar.LENGTH_LONG).show();
    }

    private void sharePhotoToFacebook() {
        Bitmap image = getBitmapFromPath(saveFilePath);
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
        Uri uri = Uri.fromFile(new File(saveFilePath));
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TEXT, caption);
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
        shareIntent.setType("image/jpeg");
        startActivity(Intent.createChooser(shareIntent, getResources().getText(R.string.send_image)));
    }

    private void shareToInstagram() {
        PackageManager pm = getPackageManager();
        if(utils.isAppInstalled("com.instagram.android",pm)) {
            Uri uri = Uri.fromFile(new File(saveFilePath));
            Intent share = new Intent(Intent.ACTION_SEND);
            share.setPackage("com.instagram.android");
            share.putExtra(Intent.EXTRA_STREAM, uri);
            share.setType("image/*");
            share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(Intent.createChooser(share, caption));
            atleastOneShare = true;
        }else
            Snackbar.make(parent,R.string.instagram_not_installed,Snackbar.LENGTH_LONG).show();
    }

    private void imgurShare() {
        if(checknetwork()) {
            final AlertDialog dialog;
            final AlertDialog.Builder progressDialog = new AlertDialog.Builder(SharingActivity.this, getDialogStyle());
            dialog = AlertDialogsHelper.getProgressDialog(SharingActivity.this, progressDialog,
                    getString(R.string.posting_on_imgur), getString(R.string.please_wait));
            dialog.show();
            Bitmap bitmap = getBitmapFromPath(saveFilePath);
            final String imageString = getStringImage(bitmap);
            //sending image to server
            StringRequest request = new StringRequest(Request.Method.POST, Constants.IMGUR_IMAGE_UPLOAD_URL, new Response.Listener<String>(){
                @Override
                public void onResponse(String s) {
                    dialog.dismiss();
                    JSONObject jsonObject = null;

                    try {
                        jsonObject = new JSONObject(s);
                        Boolean success = jsonObject.getBoolean("success");
                        if(success){
                            String url = jsonObject.getJSONObject("data").getString("link");
                            Snackbar.make(parent, R.string.uploaded_on_imgur, Snackbar.LENGTH_LONG).show();
                            copyToClipBoard(SharingActivity.this,url);
                            //// TODO: 13/7/17 Implement Dialog box
                        }
                        else{
                            Snackbar.make(parent, R.string.error_on_imgur, Snackbar.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            },new Response.ErrorListener(){
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    dialog.dismiss();
                    Snackbar.make(parent, R.string.error_volly, Snackbar.LENGTH_LONG).show(); // add volleyError to check error
                }
            }) {
                //adding parameters to send
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> parameters = new HashMap<String, String>();
                    parameters.put("image", imageString);
                    if(caption!=null && !caption.isEmpty())
                        parameters.put("title",caption);

                    return parameters;
                }
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> headers = new HashMap<String, String>();
                    headers.put("Authorization", getClientAuth());
                    return headers;
                }


            };
            request.setRetryPolicy(new DefaultRetryPolicy( 50000, 5, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            RequestQueue rQueue = Volley.newRequestQueue(SharingActivity.this);
            rQueue.add(request);
        }
        else{
            Snackbar.make(parent, R.string.not_connected, Snackbar.LENGTH_LONG).show();
        }
    }
    private void goToHome() {
        Intent home = new Intent(SharingActivity.this, LFMainActivity.class);
        startActivity(home);
        finish();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public EditText getCaptionDialog(final ThemedActivity activity, android.support.v7.app.AlertDialog.Builder passwordDialog){

        final View captionDialogLayout = activity.getLayoutInflater().inflate(R.layout.dialog_caption, null);
        final CardView captionDialogCard = (CardView) captionDialogLayout.findViewById(R.id.caption_dialog_card);
        final EditText captionEditext = (EditText) captionDialogLayout.findViewById(R.id.caption_edittxt);

        captionDialogCard.setBackgroundColor(activity.getCardBackgroundColor());
        ThemeHelper.setCursorDrawableColor(captionEditext, activity.getTextColor());
        captionEditext.setTextColor(activity.getTextColor());
        passwordDialog.setView(captionDialogLayout);
        return captionEditext;
    }


    public static String getClientAuth() {
        return "Client-ID " + Constants.MY_IMGUR_CLIENT_ID;
    }

    private class PostToTwitterAsync extends AsyncTask<Void, Void, Void> {
        AlertDialog dialog;

        @Override
        protected void onPreExecute() {
            AlertDialog.Builder progressDialog = new AlertDialog.Builder(SharingActivity.this, getDialogStyle());
            dialog = AlertDialogsHelper.getProgressDialog(SharingActivity.this, progressDialog,
                    getString(R.string.posting_twitter), getString(R.string.please_wait));
            dialog.show();
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            uploadOnTwitter();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            dialog.dismiss();
            if (isPostedOnTwitter)
                Snackbar.make(parent, R.string.tweet_posted_on_twitter, Snackbar.LENGTH_LONG).show();
            else
                Snackbar.make(parent, R.string.error_on_posting_twitter, Snackbar.LENGTH_LONG).show();

        }
    }



}