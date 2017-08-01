package org.fossasia.phimpme.share;

import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.box.androidsdk.content.BoxApiFile;
import com.box.androidsdk.content.BoxConfig;
import com.box.androidsdk.content.BoxException;
import com.box.androidsdk.content.listeners.ProgressListener;
import com.box.androidsdk.content.models.BoxFile;
import com.box.androidsdk.content.models.BoxSession;
import com.box.androidsdk.content.requests.BoxRequestsFile;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.exception.DropboxException;
import com.dropbox.client2.session.AppKeyPair;
import com.facebook.CallbackManager;
import com.facebook.share.ShareApi;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.google.android.gms.plus.PlusShare;
import com.mikepenz.community_material_typeface_library.CommunityMaterial;
import com.mikepenz.iconics.view.IconicsImageView;
import com.owncloud.android.lib.common.OwnCloudClient;
import com.owncloud.android.lib.common.OwnCloudClientFactory;
import com.owncloud.android.lib.common.OwnCloudCredentialsFactory;
import com.owncloud.android.lib.common.operations.OnRemoteOperationListener;
import com.owncloud.android.lib.common.operations.RemoteOperation;
import com.owncloud.android.lib.common.operations.RemoteOperationResult;
import com.owncloud.android.lib.resources.files.FileUtils;
import com.owncloud.android.lib.resources.files.ReadRemoteFolderOperation;
import com.owncloud.android.lib.resources.files.UploadRemoteFileOperation;
import com.pinterest.android.pdk.PDKCallback;
import com.pinterest.android.pdk.PDKClient;
import com.pinterest.android.pdk.PDKException;
import com.pinterest.android.pdk.PDKResponse;
import com.tumblr.jumblr.JumblrClient;
import com.tumblr.jumblr.types.PhotoPost;
import com.tumblr.jumblr.types.User;

import org.fossasia.phimpme.R;
import org.fossasia.phimpme.accounts.AccountActivity;
import org.fossasia.phimpme.base.PhimpmeProgressBarHandler;
import org.fossasia.phimpme.base.RecyclerItemClickListner;
import org.fossasia.phimpme.base.ThemedActivity;
import org.fossasia.phimpme.data.local.AccountDatabase;
import org.fossasia.phimpme.editor.view.imagezoom.ImageViewTouch;
import org.fossasia.phimpme.leafpic.activities.LFMainActivity;
import org.fossasia.phimpme.leafpic.util.AlertDialogsHelper;
import org.fossasia.phimpme.leafpic.util.ThemeHelper;
import org.fossasia.phimpme.share.flickr.FlickrActivity;
import org.fossasia.phimpme.share.flickr.FlickrHelper;
import org.fossasia.phimpme.share.tumblr.TumblrClient;
import org.fossasia.phimpme.share.twitter.HelperMethods;
import org.fossasia.phimpme.utilities.ActivitySwitchHelper;
import org.fossasia.phimpme.utilities.Constants;
import org.fossasia.phimpme.utilities.NotificationHandler;
import org.fossasia.phimpme.utilities.SnackBarHandler;
import org.fossasia.phimpme.utilities.Utils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

import static org.fossasia.phimpme.data.local.AccountDatabase.AccountName.BOX;
import static org.fossasia.phimpme.data.local.AccountDatabase.AccountName.DROPBOX;
import static org.fossasia.phimpme.data.local.AccountDatabase.AccountName.FACEBOOK;
import static org.fossasia.phimpme.data.local.AccountDatabase.AccountName.FLICKR;
import static org.fossasia.phimpme.data.local.AccountDatabase.AccountName.PINTEREST;
import static org.fossasia.phimpme.data.local.AccountDatabase.AccountName.TWITTER;
import static org.fossasia.phimpme.utilities.Constants.BOX_CLIENT_ID;
import static org.fossasia.phimpme.utilities.Constants.BOX_CLIENT_SECRET;
import static org.fossasia.phimpme.utilities.Utils.copyToClipBoard;
import static org.fossasia.phimpme.utilities.Utils.getBitmapFromPath;
import static org.fossasia.phimpme.utilities.Utils.getStringImage;
import static org.fossasia.phimpme.utilities.Utils.isAppInstalled;
import static org.fossasia.phimpme.utilities.Utils.isInternetOn;
import static org.fossasia.phimpme.utilities.Utils.shareMsgOnIntent;

/**
 * Class which deals with Sharing images to multiple Account logged in by the user in the app.
 * If the account is not logged in from Account Manager, it shows a snackbar to login in Account
 * Manager first.
 *
 * Click on the share account from bottom grid layout.
 *
 * To Add new Account:
 * 1. First add the entry in AccountDatabase AccountName enum
 *
 * 2. Add a icon in the format ic_<account_name>_black and,
 *    color in <account_name>_color in this format.
 *    Because the color and icon assigning will be done in a loop to avoid the separate line for
 *    each account.
 *
 * 3. Add the entry in Switch block for the click action on account. Create separate folder for
 *    your share action, Don't code direcly inside the switch case.
 *
 * 4. Do add a documentation on the function.
 */

public class SharingActivity extends ThemedActivity implements View.OnClickListener
        , OnRemoteOperationListener, RecyclerItemClickListner.OnItemClickListener {

    public static final String EXTRA_OUTPUT = "extra_output";
    private static String LOG_TAG = SharingActivity.class.getCanonicalName();
    public String saveFilePath;
    ThemeHelper themeHelper;
    private OwnCloudClient mClient;
    private Handler mHandler;
    private ShareAdapter shareAdapter;

    @BindView(R.id.share_layout)
    View parent;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.share_image)
    ImageViewTouch shareImage;

    @BindView(R.id.edittext_share_caption)
    TextView text_caption;

    @BindView(R.id.share_account)
    RecyclerView shareAccountRecyclerView;

    @BindView(R.id.share_done)
    Button done;

    @BindView(R.id.button_text_focus)
    IconicsImageView editFocus;

    @BindView(R.id.edit_text_caption_container)
    RelativeLayout captionLayout;

    private CallbackManager callbackManager;
    private Realm realm = Realm.getDefaultInstance();
    private String caption;
    private boolean atleastOneShare = false;
    private PhimpmeProgressBarHandler phimpmeProgressBarHandler;
    private Context context;
    private DropboxAPI<AndroidAuthSession> mDBApi;
    private BoxSession sessionBox;
    private ArrayList<AccountDatabase.AccountName> sharableAccountsList = new ArrayList<>();
    Bitmap finalBmp;
    Boolean isPostedOnTwitter = false, isPersonal = false;
    String boardID, imgurAuth = null, imgurString = null;

    private static final int REQ_SELECT_PHOTO = 1;

    public boolean uploadFailedBox = false;
    public String uploadName;

    public static String getClientAuth() {
        return Constants.IMGUR_HEADER_CLIENt + " " + Constants.MY_IMGUR_CLIENT_ID;
    }

    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        setContentView(R.layout.activity_share);
        shareAdapter = new ShareAdapter();
        context = this;
        themeHelper = new ThemeHelper(this);
        mHandler = new Handler();
        sharableAccountsList = Utils.getSharableAccountsList();
        phimpmeProgressBarHandler = new PhimpmeProgressBarHandler(this);
        ActivitySwitchHelper.setContext(this);
        ButterKnife.bind(this);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setupUI();
        initView();
        setUpRecyclerView();
        setStatusBarColor();
        checknetwork();
        configureBoxClient();
    }

    private void configureBoxClient() {
        BoxConfig.CLIENT_ID = BOX_CLIENT_ID;
        BoxConfig.CLIENT_SECRET = BOX_CLIENT_SECRET;
    }

    private boolean checknetwork() {
        if (isInternetOn(SharingActivity.this)) {
            return true;
        } else
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
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .animate(R.anim.fade_in)
                .into(shareImage);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

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

    @Override
    public void onItemClick(View childView, int position) {
        switch (sharableAccountsList.get(position)) {
            case FACEBOOK:
                shareToFacebook();
                break;

            case TWITTER:
                shareToTwitter();
                break;

            case INSTAGRAM:
                shareToInstagram();
                break;

            case NEXTCLOUD:
                shareToNextCloudAndOwnCloud(getString(R.string.nextcloud));
                break;

            case PINTEREST:
                if (Utils.checkAlreadyExist(PINTEREST)) {
                    openPinterestDialogBox();
                } else {
                    Snackbar.make(parent, getResources().getString(R.string.pinterest_signIn_fail), Snackbar.LENGTH_LONG)
                            .setAction(R.string.sign_In, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent accounts = new Intent(SharingActivity.this, AccountActivity.class);
                                    startActivity(accounts);
                                }
                            }).show();
                }
                break;

            case FLICKR:
                shareToFlickr();
                break;

            case IMGUR:
                shareToImgur();
                break;

            case DROPBOX:
                shareToDropBox();
                break;

            case OWNCLOUD:
                shareToNextCloudAndOwnCloud(getString(R.string.owncloud));
                break;


            case BOX:
                shareToBox();
                break;

            case TUMBLR:
                shareToTumblr();
                break;

            case OTHERS:
                shareToOthers();
                break;
            case WHATSAPP:
                shareToWhatsapp();
                break;

            default:
                SnackBarHandler.show(parent, R.string.feature_not_present);
        }
    }

    private void shareToTumblr() {
        new PostToTumblrAsync().execute();
    }

    @Override
    public void onItemLongPress(View childView, int position) {

    }

    private void shareToBox() {
        if (Utils.checkAlreadyExist(BOX)) {
            sessionBox = new BoxSession(this);
            new UploadToBox().execute();
        } else {
            SnackBarHandler.show(parent, R.string.login_box);
        }
    }

    private void shareToGoogle() {
        Uri uri = getImageUri(context);
        PlusShare.Builder share = new PlusShare.Builder(SharingActivity.this);
        share.setText(caption);
        share.addStream(uri);
        share.setType(getResources().getString(R.string.image_type));
        startActivityForResult(share.getIntent(), REQ_SELECT_PHOTO);

    }

    public Uri getImageUri(Context inContext) {
        Bitmap inImage = getBitmapFromPath(saveFilePath);
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    private class UploadToBox extends AsyncTask<Void, Integer, Void> {
        private FileInputStream inputStream;
        private File file;
        private BoxApiFile mFileApi;
        private Boolean success;
        private int fileLength;

        @Override
        protected void onPreExecute() {
            sessionBox.authenticate();
            NotificationHandler.make();
            mFileApi = new BoxApiFile(sessionBox);
            file = new File(saveFilePath);
            fileLength = (int)file.length();
            NotificationHandler.updateProgress(0,fileLength, 0);
            try {
                inputStream = new FileInputStream(file);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            try {
                String destinationFolderId = "0";
                if (!uploadFailedBox)
                    uploadName = file.getName();
                BoxRequestsFile.UploadFile request = mFileApi.getUploadRequest(inputStream, uploadName, destinationFolderId);
                final BoxFile uploadFileInfo = request.setProgressListener(new ProgressListener() {
                    @Override
                    public void onProgressChanged(long l, long l1) {
                        int percent = ((int)l*100)/fileLength;
                        NotificationHandler.updateProgress((int)l,fileLength, percent);
                    }
                }).send();
                Log.d(LOG_TAG, uploadFileInfo.toString());
                success = true;
            } catch (BoxException e) {
                success = false;
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (success) {
                NotificationHandler.uploadPassed();
                SnackBarHandler.show(parent, R.string.uploaded_box);
            }
            else {
                NotificationHandler.uploadFailed();
                Snackbar.make(parent, getString(R.string.upload_failed_retry_box), Snackbar.LENGTH_LONG)
                        .setAction(getString(R.string.retry_upload), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                uploadFailedBox = true;
                                renameUploadName(file.getName());
                            }
                        }).show();
            }
        }
    }

    private void renameUploadName(String fileName) {
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(SharingActivity.this, getDialogStyle());
        final EditText editTextNewName = new EditText(getApplicationContext());
        editTextNewName.setText(fileName);
        editTextNewName.setSelection(fileName.length());
        AlertDialogsHelper.getInsertTextDialog(SharingActivity.this, dialogBuilder, editTextNewName, R.string.Rename, null);

        dialogBuilder.setPositiveButton(getString(R.string.retry_upload).toUpperCase(), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                uploadName = editTextNewName.getText().toString();
                new UploadToBox().execute();
            }
        });
        dialogBuilder.show();
    }
    private void shareToFlickr() {
        if (Utils.checkAlreadyExist(FLICKR)){
            SnackBarHandler.show(parent,getString(R.string.uploading));
            InputStream is = null;
            File file = new File(saveFilePath);
            try {
                is = getContentResolver().openInputStream(Uri.fromFile(file));
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (is != null) {
                FlickrHelper f = FlickrHelper.getInstance();
                f.setInputStream(is);
                f.setFilename(file.getName());

                if (caption!=null && !caption.isEmpty())
                    f.setDescription(caption);
                f.uploadImage();
            }
        }
    }

    private void shareToDropBox() {
        AppKeyPair appKeys = new AppKeyPair(Constants.APP_KEY, Constants.APP_SECRET);
        AndroidAuthSession session = new AndroidAuthSession(appKeys);
        RealmQuery<AccountDatabase> query = realm.where(AccountDatabase.class);
        // Checking if string equals to is exist or not
        query.equalTo("name", DROPBOX.toString());
        RealmResults<AccountDatabase> result = query.findAll();
        try {
            session.setOAuth2AccessToken(result.get(0).getToken());
            mDBApi = new DropboxAPI<AndroidAuthSession>(session);
            if (checknetwork()) {
                new UploadToDropbox().execute();
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            SnackBarHandler.show(parent, R.string.login_dropbox_account);
        }
    }


    private class UploadToDropbox extends AsyncTask<Void, Integer, Void> {
        Boolean success;

        @Override
        protected void onPreExecute() {
            NotificationHandler.make();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            File file = new File(saveFilePath);
            FileInputStream inputStream = null;
            try {
                inputStream = new FileInputStream(file);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            DropboxAPI.Entry response = null;
            try {
                File file2 = new File(saveFilePath);
                response = mDBApi.putFile(file2.getName(), inputStream,
                        file.length(), null, null);
                success = true;
            } catch (DropboxException e) {
                success = false;
                e.printStackTrace();
            }
            if (response != null)
                Log.i("Db", "The uploaded file's rev is: " + response.rev);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if(success) {
                NotificationHandler.uploadPassed();
                SnackBarHandler.show(parent, R.string.uploaded_dropbox);
            }
            else {
                NotificationHandler.uploadFailed();
                SnackBarHandler.show(parent, R.string.upload_failed);
            }
        }
    }


    private void openCaptionDialogBox() {
        AlertDialog.Builder captionDialogBuilder = new AlertDialog.Builder(SharingActivity.this, getDialogStyle());
        final EditText captionEditText = getCaptionDialog(this, captionDialogBuilder);
        if (caption != null) {
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
                if (!captionText.isEmpty()) {
                    caption = captionText;
                    text_caption.setText(caption);
                }
                passwordDialog.dismiss();
            }
        });
    }

    private void openPinterestDialogBox() {
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(SharingActivity.this, getDialogStyle());
        final EditText captionEditText = new EditText(getApplicationContext());

        String link = "<a href=https://www.nutt.net/how-do-i-get-pinterest-board-id/> Get Board ID from the LINK";
        AlertDialogsHelper.getInsertTextDialog(SharingActivity.this, dialogBuilder, captionEditText, R.string.Pinterest_link ,link);
        dialogBuilder.setNegativeButton(getString(R.string.cancel).toUpperCase(), null);
        dialogBuilder.setPositiveButton(getString(R.string.post_action).toUpperCase(), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //This should br empty it will be overwrite later
                //to avoid dismiss of the dialog on wrong password
            }
        });

        final AlertDialog passwordDialog = dialogBuilder.create();
        passwordDialog.show();

        passwordDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String captionText = captionEditText.getText().toString();
                boardID = captionText;
                shareToPinterest(boardID);
                passwordDialog.dismiss();
            }
        });
    }

    private void shareToPinterest(final String boardID) {
        NotificationHandler.make();
        Bitmap image = getBitmapFromPath(saveFilePath);
        PDKClient
                .getInstance().createPin(caption, boardID, image, null, new PDKCallback() {
            @Override
            public void onSuccess(PDKResponse response) {
                NotificationHandler.uploadPassed();
                Log.d(getClass().getName(), response.getData().toString());
                SnackBarHandler.show(parent,R.string.pinterest_post);

            }

            @Override
            public void onFailure(PDKException exception) {
                NotificationHandler.uploadFailed();
                Log.e(getClass().getName(), exception.getDetailMessage());
                SnackBarHandler.show(parent,R.string.Pinterest_fail);
            }
        });
    }

    private void shareToTwitter() {
        if (checknetwork()) {
            if (Utils.checkAlreadyExist(TWITTER)) {
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
            } else {
                SnackBarHandler.show(parent, getString(R.string.sign_from_account));
            }
        }else{
            SnackBarHandler.show(parent, getString(R.string.not_connected));
        }
    }

    private void uploadOnTwitter(String token, String secret) {
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

                HelperMethods.postToTwitterWithImage(context, finalFile, caption,token,secret, new HelperMethods.TwitterCallback() {
                    @Override
                    public void onFinsihed(Boolean response) {
                        isPostedOnTwitter = response;
                        file.delete();
                    }
                });

    }

    private void shareToFacebook() {
        if (Utils.checkAlreadyExist(FACEBOOK)) {
        Bitmap image = getBitmapFromPath(saveFilePath);
        SharePhoto photo = new SharePhoto.Builder()
                .setBitmap(image)
                .setCaption(caption)
                .build();

        SharePhotoContent content = new SharePhotoContent.Builder()
                .addPhoto(photo)
                .build();

        ShareApi.share(content, null);
            SnackBarHandler.show(parent, R.string.facebook_image_posted);
        atleastOneShare = true;
    }else{
            SnackBarHandler.show(parent, R.string.facebook_signIn_fail);
        }
    }

    private void shareToOthers() {
        Uri uri = Uri.fromFile(new File(saveFilePath));
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TEXT, caption);
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
        shareIntent.setType("image/png");
        startActivity(Intent.createChooser(shareIntent, getResources().getText(R.string.send_image)));
    }

    private void shareToInstagram() {
        Uri uri = Uri.fromFile(new File(saveFilePath));
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setPackage("com.instagram.android");
        share.putExtra(Intent.EXTRA_STREAM, uri);
        share.setType("image/*");
        share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(Intent.createChooser(share, caption));
        atleastOneShare = true;
    }

    private void shareToWhatsapp() {
        Uri uri = Uri.fromFile(new File(saveFilePath));
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setPackage("com.whatsapp");
        share.putExtra(Intent.EXTRA_STREAM, uri);
        share.setType("image/*");
        share.putExtra(Intent.EXTRA_TEXT, caption);
        startActivity(share);
        atleastOneShare = true;
    }

    private void shareToImgur() {
        if (checknetwork()) {
            final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(SharingActivity.this, getDialogStyle());

            RealmQuery<AccountDatabase> query = realm.where(AccountDatabase.class);
            query.equalTo("name", getString(R.string.imgur));
            final RealmResults<AccountDatabase> result = query.findAll();
            if (result.size() != 0) {
                isPersonal = true;
                imgurAuth = Constants.IMGUR_HEADER_USER + " " + result.get(0).getToken();
            }
            AlertDialogsHelper.getTextDialog(SharingActivity.this, dialogBuilder,
                    R.string.choose, R.string.imgur_select_mode, null);
            dialogBuilder.setPositiveButton(getString(R.string.personal).toUpperCase(), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    if (!isPersonal) {
                        SnackBarHandler.show(parent, R.string.sign_from_account);
                        return;
                    } else {
                        isPersonal = true;
                        uploadImgur();
                    }
                }
            });

            dialogBuilder.setNeutralButton(getString(R.string.anonymous).toUpperCase(), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    isPersonal = false;
                    uploadImgur();
                }
            });
            dialogBuilder.setNegativeButton(getString(R.string.exit).toUpperCase(), null);
            dialogBuilder.show();

        } else {
            SnackBarHandler.show(parent, R.string.not_connected);
        }
    }

    void uploadImgur() {
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(SharingActivity.this, getDialogStyle());
        final AlertDialog dialog;
        final AlertDialog.Builder progressDialog = new AlertDialog.Builder(SharingActivity.this, getDialogStyle());
        dialog = AlertDialogsHelper.getProgressDialog(SharingActivity.this, progressDialog,
                getString(R.string.posting_on_imgur), getString(R.string.please_wait));
        dialog.show();
        Bitmap bitmap = getBitmapFromPath(saveFilePath);
        final String imageString = getStringImage(bitmap);
        //sending image to server
        StringRequest request = new StringRequest(Request.Method.POST, Constants.IMGUR_IMAGE_UPLOAD_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                dialog.dismiss();
                JSONObject jsonObject = null;

                try {
                    jsonObject = new JSONObject(s);
                    Boolean success = jsonObject.getBoolean("success");
                    if (success) {
                        final String url = jsonObject.getJSONObject("data").getString("link");

                        if (isPersonal) {
                            imgurString = getString(R.string.upload_personal) + "\n" + url;
                        } else {
                            imgurString = getString(R.string.upload_anonymous) + "\n" + url;

                        }

                        AlertDialogsHelper.getTextDialog(SharingActivity.this, dialogBuilder,
                                R.string.imgur_uplaoded_dialog_title, 0, imgurString);
                        dialogBuilder.setPositiveButton(getString(R.string.share).toUpperCase(), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                shareMsgOnIntent(SharingActivity.this, url);
                            }
                        });

                        dialogBuilder.setNeutralButton(getString(R.string.copy_action).toUpperCase(), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                copyToClipBoard(SharingActivity.this, url);

                            }
                        });
                        dialogBuilder.setNegativeButton(getString(R.string.exit).toUpperCase(), null);
                        dialogBuilder.show();
                    } else {
                        SnackBarHandler.show(parent, R.string.error_on_imgur);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                dialog.dismiss();
                SnackBarHandler.show(parent, R.string.error_volly);// add volleyError to check error
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parameters = new HashMap<String, String>();
                parameters.put("image", imageString);
                if (caption != null && !caption.isEmpty())
                    parameters.put("title", caption);
                return parameters;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();
                if (isPersonal) {
                    if (imgurAuth != null) {
                        headers.put(getString(R.string.header_auth), imgurAuth);
                    }
                } else {
                    headers.put(getString(R.string.header_auth), getClientAuth());
                }

                return headers;
            }
        };
        request.setRetryPolicy(new DefaultRetryPolicy(50000, 5, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue rQueue = Volley.newRequestQueue(SharingActivity.this);
        rQueue.add(request);
    }

    /**
     * Function to share on NextCloud and OwnCloud because they share the common android library
     * @param str the name of the account to upload
     */
    void shareToNextCloudAndOwnCloud(String str){
        RealmQuery<AccountDatabase> query = realm.where(AccountDatabase.class);
        RealmResults<AccountDatabase> result = query.equalTo("name", str.toUpperCase()).findAll();

        if (result.size() != 0) {
            Uri serverUri = Uri.parse(result.get(0).getServerUrl());
            String username = result.get(0).getUsername();
            String password = result.get(0).getPassword();

            mClient = OwnCloudClientFactory.createOwnCloudClient(serverUri, this, true);
            mClient.setCredentials(
                    OwnCloudCredentialsFactory.newBasicCredentials(
                            username,
                            password
                    )
            );

            AssetManager assets = getAssets();
            try {
                String sampleFileName = getString(R.string.sample_file_name);
                File upFolder = new File(getCacheDir(), getString(R.string.upload_folder_path));
                upFolder.mkdir();
                File upFile = new File(upFolder, sampleFileName);
                FileOutputStream fos = new FileOutputStream(upFile);
                InputStream is = assets.open(sampleFileName);
                int count = 0;
                byte[] buffer = new byte[1024];
                while ((count = is.read(buffer, 0, buffer.length)) >= 0) {
                    fos.write(buffer, 0, count);
                }
                is.close();
                fos.close();
            } catch (IOException e) {
                SnackBarHandler.show(parent,R.string.error_copying_sample_file);
                Log.e(LOG_TAG, getString(R.string.error_copying_sample_file), e);
            }

            File fileToUpload = new File(saveFilePath);
            String remotePath = FileUtils.PATH_SEPARATOR + fileToUpload.getName();
            ContentResolver cR = context.getContentResolver();
            MimeTypeMap mime = MimeTypeMap.getSingleton();
            Uri uri = Uri.fromFile(new File(saveFilePath));
            String type = mime.getExtensionFromMimeType(cR.getType(uri));
            String mimeType = type;

            // Get the last modification date of the file from the file system
            Long timeStampLong = fileToUpload.lastModified() / 1000;
            String timeStamp = timeStampLong.toString();

            UploadRemoteFileOperation uploadOperation =
                    new UploadRemoteFileOperation(fileToUpload.getAbsolutePath(), remotePath, mimeType, timeStamp);
            uploadOperation.execute(mClient, this, mHandler);
            phimpmeProgressBarHandler.show();

        } else {
            SnackBarHandler.show(parent, "Please sign in to " + str + " from account manager");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int responseCode, Intent data) {
        super.onActivityResult(requestCode, responseCode, data);
        if (requestCode == REQ_SELECT_PHOTO) {
            if (responseCode == RESULT_OK) {
                Snackbar.make(parent, R.string.success_google, Snackbar.LENGTH_LONG).show();
                return;
            } else {
                Snackbar.make(parent, R.string.error_google, Snackbar.LENGTH_LONG).show();
                return;
            }
        }
        callbackManager.onActivityResult(requestCode, responseCode, data);
        PDKClient.getInstance().onOauthResponse(requestCode, responseCode, data);
        atleastOneShare = true;

    }


    private void goToHome() {
        Intent home = new Intent(SharingActivity.this, LFMainActivity.class);
        startActivity(home);
        finish();
    }

    @Override
    public void onResume() {
        ActivitySwitchHelper.setContext(this);
        super.onResume();
    }

    public EditText getCaptionDialog(final ThemedActivity activity, android.support.v7.app.AlertDialog.Builder passwordDialog) {

        final View captionDialogLayout = activity.getLayoutInflater().inflate(R.layout.dialog_caption, null);
        final CardView captionDialogCard = (CardView) captionDialogLayout.findViewById(R.id.caption_dialog_card);
        final EditText captionEditext = (EditText) captionDialogLayout.findViewById(R.id.caption_edittxt);

        captionDialogCard.setBackgroundColor(activity.getCardBackgroundColor());
        ThemeHelper.setCursorDrawableColor(captionEditext, activity.getTextColor());
        captionEditext.setTextColor(activity.getTextColor());
        passwordDialog.setView(captionDialogLayout);
        return captionEditext;
    }

    private void startRefresh() {
        ReadRemoteFolderOperation refreshOperation = new ReadRemoteFolderOperation(FileUtils.PATH_SEPARATOR);
        refreshOperation.execute(mClient, this, mHandler);
    }

    /**
     * Callback for Nextcloud operation
     *
     * @param operation
     * @param result    result of success or failure
     */
    @Override
    public void onRemoteOperationFinish(RemoteOperation operation, RemoteOperationResult result) {
        phimpmeProgressBarHandler.hide();
        if (!result.isSuccess()) {
            Snackbar.make(parent, R.string.login_again, Snackbar.LENGTH_LONG)
                    .setAction(R.string.exit, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            goToHome();
                        }
                    }).show();
        } else if (result.isSuccess()) {
            Snackbar.make(parent, R.string.todo_operation_finished_in_success, Snackbar.LENGTH_LONG).show();
        } else if (operation instanceof UploadRemoteFileOperation) {
            onSuccessfulUpload();
        }
    }

    private void onSuccessfulUpload() {
        startRefresh();
    }

    public void setUpRecyclerView() {
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 2);
        shareAccountRecyclerView.setLayoutManager(layoutManager);
        shareAccountRecyclerView.setAdapter(shareAdapter);
        shareAccountRecyclerView.addOnItemTouchListener(new RecyclerItemClickListner(this, this));
    }

    private class PostToTwitterAsync extends AsyncTask<Void, Void, Void> {
        String token, secret;

        @Override
        protected void onPreExecute() {
            NotificationHandler.make();
            RealmQuery<AccountDatabase> query = realm.where(AccountDatabase.class);
            query.equalTo("name", TWITTER.toString());
            final RealmResults<AccountDatabase> result = query.findAll();
            if (result.size()!=0) {
                token = result.get(0).getToken();
                secret = result.get(0).getSecret();
            }
            super.onPreExecute();
        }
        @Override
        protected Void doInBackground(Void... arg0) {
            uploadOnTwitter(token,secret);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (isPostedOnTwitter) {
                NotificationHandler.uploadPassed();
                SnackBarHandler.show(parent, R.string.tweet_posted_on_twitter);
            }
            else {
                NotificationHandler.uploadFailed();
                SnackBarHandler.show(parent, R.string.error_on_posting_twitter);
            }
        }
    }
    private class PostToTumblrAsync extends AsyncTask<Void, Void, Void> {
        AlertDialog dialog;
        TumblrClient tumblrClient;
        JumblrClient client;
        Boolean success = true;


        @Override
        protected void onPreExecute() {
            tumblrClient = new TumblrClient();
            AlertDialog.Builder progressDialog = new AlertDialog.Builder(SharingActivity.this, getDialogStyle());
            dialog = AlertDialogsHelper.getProgressDialog(SharingActivity.this, progressDialog,
                    getString(R.string.posting_tumblr), getString(R.string.please_wait));
            dialog.show();
            client = tumblrClient.getClient();
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            User user = client.user();
            PhotoPost post = null;
            try {
                post = client.newPost(user.getBlogs().get(0).getName(), PhotoPost.class);
                if (caption!=null && !caption.isEmpty())
                post.setCaption(caption);
                post.setData(new File(saveFilePath));
                post.save();
            } catch (IllegalAccessException | InstantiationException e) {
                success = false;
                e.printStackTrace();
            } catch (IOException e) {
                success = false;
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            dialog.dismiss();
            if (success)
                SnackBarHandler.show(parent, getString(R.string.posted_on_tumblr));
            else
                SnackBarHandler.show(parent, getString(R.string.error_on_tumblr));
        }
    }

}
