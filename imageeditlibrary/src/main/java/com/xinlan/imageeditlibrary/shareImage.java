package com.xinlan.imageeditlibrary;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import java.io.File;
import java.io.FileNotFoundException;

import static com.xinlan.imageeditlibrary.editimage.EditImageActivity.EXTRA_OUTPUT;
import static com.xinlan.imageeditlibrary.editimage.EditImageActivity.FILE_PATH;

public class shareImage extends AppCompatActivity {

    ImageView mImageView;
    public String filePath;
    public String saveFilePath;
    ImageView mshareButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_image);
        try {
            initView();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        buttonClick();
    }

    private void initView() throws FileNotFoundException {

        mImageView = (ImageView) findViewById(R.id.imageView);
        filePath = getIntent().getStringExtra(FILE_PATH);
        saveFilePath = getIntent().getStringExtra(EXTRA_OUTPUT);
        Bitmap myBitmap = BitmapFactory.decodeFile(filePath);
        mImageView.setImageBitmap(myBitmap);
    }


    private void buttonClick() {

        mshareButton = (ImageView) findViewById(R.id.shareButton);
        mshareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareButton();
            }
        });

    }

    private void shareButton(){

        Uri uri = Uri.fromFile(new File(filePath));
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
        shareIntent.setType("image/jpeg");
        startActivity(Intent.createChooser(shareIntent, getResources().getText(R.string.send_image)));
    }
}
