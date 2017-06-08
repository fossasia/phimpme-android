package vn.mbm.phimp.me;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.share.ShareApi;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.List;

public class shareActivity extends AppCompatActivity {

    ImageView mImageView;
    ImageView mshareButton;
    ImageView mfacebookButton;
    ImageView mInstagrambutton;
    public EditText mEditText;
    public String filePath;
    public String saveFilePath;
    public String sendMessage;
    public static final String EXTRA_OUTPUT = "extra_output";
    public static final String FILE_PATH = "file_path";
    private CallbackManager callbackManager;
    private LoginManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);
        mEditText = (EditText) findViewById(R.id.edit_text);
        sendMessage = mEditText.getText().toString();
        initView();
        buttonClick();
    }

    public void buttonClick() {
        mshareButton = (ImageView) findViewById(R.id.share_button);
        mshareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareButton();
            }
        });
        mfacebookButton = (ImageView) findViewById(R.id.facebook_button);
        mfacebookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareFacebook();
            }
        });
        mInstagrambutton = (ImageView) findViewById(R.id.instagram_button);
        mInstagrambutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareInstagram();
            }
        });
    }

    private void shareButton() {
        sendMessage = mEditText.getText().toString();
        Uri uri = Uri.fromFile(new File(saveFilePath));
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TEXT, sendMessage);
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
        shareIntent.setType("image/jpeg");
        startActivity(Intent.createChooser(shareIntent, getResources().getText(R.string.send_image)));
    }

    public void initView() {
        mImageView = (ImageView) findViewById(R.id.image_view);
        //filePath = getIntent().getStringExtra(FILE_PATH);
        saveFilePath = getIntent().getStringExtra(EXTRA_OUTPUT);
        Bitmap myBitmap = BitmapFactory.decodeFile(saveFilePath);
        mImageView.setImageBitmap(myBitmap);
        mEditText = (EditText) findViewById(R.id.edit_text);

    }

    private void shareInstagram() {
        Uri uri = Uri.fromFile(new File(saveFilePath));
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setPackage("com.instagram.android");
        share.putExtra(Intent.EXTRA_STREAM, uri);
        share.setType("image/*");
        share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(Intent.createChooser(share, sendMessage));
    }

    private void shareFacebook() {
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        List<String> permissionNeeds = Arrays.asList("publish_actions");

        //this loginManager helps you eliminate adding a LoginButton to your UI
        manager = LoginManager.getInstance();
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
        sendMessage = mEditText.getText().toString();
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        Bitmap image = BitmapFactory.decodeFile(saveFilePath, bmOptions);
        SharePhoto photo = new SharePhoto.Builder()
                .setBitmap(image)
                .setCaption(sendMessage)
                .build();

        SharePhotoContent content = new SharePhotoContent.Builder()
                .addPhoto(photo)
                .build();

        ShareApi.share(content, null);
    }

    @Override
    protected void onActivityResult(int requestCode, int responseCode, Intent data) {
        super.onActivityResult(requestCode, responseCode, data);
        callbackManager.onActivityResult(requestCode, responseCode, data);
    }
}