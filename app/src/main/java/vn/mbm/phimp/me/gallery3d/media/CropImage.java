/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package vn.mbm.phimp.me.gallery3d.media;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.CountDownLatch;

import vn.mbm.phimp.me.ImagesFilter;
import vn.mbm.phimp.me.PhimpMe;
import vn.mbm.phimp.me.R;
//import vn.mbm.phimp.me.UploadMap;
import vn.mbm.phimp.me.gallery3d.app.App;
import vn.mbm.phimp.me.gallery3d.app.Res;
import vn.mbm.phimp.me.utils.geoDegrees;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.media.FaceDetector;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.RotateAnimation;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

/**
 * The activity can crop specific region of interest from an image.
 */
public class CropImage extends MonitoredActivity {
    private static final String TAG = "CropImage";

    public static final int CROP_MSG = 10;
    public static final int CROP_MSG_INTERNAL = 100;

    private App mApp = null;
    //private static float toDegree = 90;
    //private static float fromDegree = 0;
    // These are various options can be specified in the intent.
    private Bitmap.CompressFormat mOutputFormat = Bitmap.CompressFormat.JPEG; // only
    // used with mSaveUri
    private Uri mSaveUri = null;
    private int mAspectX, mAspectY; // CR: two definitions per line == sad
    // panda.
    private boolean mDoFaceDetection = true;
    private boolean mCircleCrop = false;
    private final Handler mHandler = new Handler();

    // These options specifiy the output image size and whether we should
    // scale the output to fit it (or just crop it).
    private int mOutputX, mOutputY;
    private boolean mScale;
    private boolean mScaleUp = true;

    boolean mWaitingToPick; // Whether we are wait the user to pick a face.
    boolean mSaving; // Whether the "save" button is already clicked.

    private CropImageView mImageView;
    private ContentResolver mContentResolver;

    private Bitmap mBitmap;
    //private static Bitmap mBitmapSave;
    private static Bitmap mBitmapResize;
    private static Bitmap modifiedBitmap;
    private static Bitmap flippedImaged;
    private static int brightnessValue = 0;
    private final int GET_POSITION_ON_MAP = 5;
    private MediaItem mItem;
    private final BitmapManager.ThreadSet mDecodingThreads = new BitmapManager.ThreadSet();
    HighlightView mCrop;
    private String mImagePath;
    private String newpath;
    static String p[] = null;
    static String activitynam;
    int screen_w;
    ProgressDialog gpsloading;
    ImageButton btnUseMap;
    EditText txtPhotoTitle;
    EditText txtLongtitude;
    EditText txtLatitude;
    EditText txtTags;
    Context ctx;

    // Variables to fix: Duplicate images
    private boolean imageEdited = false;
    private int turns = 0;
    private boolean rotated = false;
    private boolean flipped = false;
    private int initialWidth = 0;
    private int initialHeight = 0;

    public static int latitude,longitude;
    public static String from="";
    static private final HashMap<Context, MediaScannerConnection> mConnectionMap = new HashMap<Context, MediaScannerConnection>();

    @SuppressWarnings("static-access")
    static public void launchCropperOrFinish(final Context context, final MediaItem item) {
        final Bundle myExtras = ((Activity) context).getIntent().getExtras();
        String cropValue = myExtras != null ? myExtras.getString("crop") : null;
        final String contentUri = item.mContentUri;
        if (contentUri == null)
            return;
        if (cropValue != null) {
            Bundle newExtras = new Bundle();
            if (cropValue.equals("circle")) {
                newExtras.putString("circleCrop", "true");
            }
            Intent cropIntent = new Intent();
            cropIntent.setData(Uri.parse(contentUri));
            cropIntent.setClass(context, CropImage.class);
            cropIntent.putExtras(newExtras);
            // Pass through any extras that were passed in.
            cropIntent.putExtras(myExtras);
            ((Activity) context).startActivityForResult(cropIntent, CropImage.CROP_MSG);
        } else {
            if (contentUri.startsWith("http://")) {
                // This is a http uri, we must save it locally first and
                // generate a content uri from it.
                final ProgressDialog dialog = ProgressDialog.show(context, context.getResources().getString(Res.string.initializing),
                        context.getResources().getString(Res.string.running_face_detection), true, false);
                if (contentUri != null) {
                    MediaScannerConnection.MediaScannerConnectionClient client = new MediaScannerConnection.MediaScannerConnectionClient() {
                        public void onMediaScannerConnected() {
                            MediaScannerConnection connection = mConnectionMap.get(context);
                            if (connection != null) {
                                try {
                                    final String path = UriTexture.writeHttpDataInDirectory(context, contentUri,
                                            LocalDataSource.DOWNLOAD_BUCKET_NAME);
                                    if (path != null) {
                                        connection.scanFile(path, item.mMimeType);
                                    } else {
                                        shutdown("");
                                    }
                                } catch (Exception e) {
                                    shutdown("");
                                }
                            }
                        }

                        public void onScanCompleted(String path, Uri uri) {
                            shutdown(uri.toString());
                        }

                        public void shutdown(String uri) {
                            dialog.dismiss();
                            performReturn(context, myExtras, uri.toString());
                            MediaScannerConnection connection = mConnectionMap.get(context);
                            if (connection != null) {
                                connection.disconnect();
                                mConnectionMap.put(context, null);
                            }
                        }
                    };
                    MediaScannerConnection connection = new MediaScannerConnection(context, client);
                    mConnectionMap.put(context, connection);
                    connection.connect();
                }
            } else {
                performReturn(context, myExtras, contentUri);
            }
        }
    }

    static private void performReturn(Context context, Bundle myExtras, String contentUri) {
        Intent result = new Intent(null, Uri.parse(contentUri));
        boolean resultSet = false;
        if (myExtras != null) {
            final Uri outputUri = (Uri)myExtras.getParcelable(MediaStore.EXTRA_OUTPUT);
            if (outputUri != null) {
                Bundle extras = new Bundle();
                OutputStream outputStream = null;
                try {
                    outputStream = context.getContentResolver().openOutputStream(outputUri);
                    if (outputStream != null) {
                        InputStream inputStream = context.getContentResolver().openInputStream(Uri.parse(contentUri));
                        Utils.copyStream(inputStream, outputStream);
                        Util.closeSilently(inputStream);
                    }
                    ((Activity) context).setResult(Activity.RESULT_OK, new Intent(outputUri.toString())
                            .putExtras(extras));
                    resultSet = true;
                } catch (Exception ex) {
                    Log.e(TAG, "Cannot save to uri " + outputUri.toString());
                } finally {
                    Util.closeSilently(outputStream);
                }
            }
        }
        if (!resultSet && myExtras != null && myExtras.getBoolean("return-data")) {
            // The size of a transaction should be below 100K.
            Bitmap bitmap = null;
            try {
                bitmap = UriTexture.createFromUri(context, contentUri, 1024, 1024, 0, null);
            } catch (IOException e) {
                ;
            } catch (URISyntaxException e) {
                ;
            }
            if (bitmap != null) {
                result.putExtra("data", bitmap);
            }
        }
        if (!resultSet)
            ((Activity) context).setResult(Activity.RESULT_OK, result);
        ((Activity) context).finish();
    }

    @SuppressWarnings({ "static-access", "deprecation" })
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        mApp = new App(CropImage.this);
        mContentResolver = getContentResolver();
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(Res.layout.gallery3d_cropimage);
        ctx = this;
        mImageView = (CropImageView) findViewById(Res.id.image);
        txtPhotoTitle = (EditText) findViewById(R.id.txtUploadPhotoTitle);

        txtLongtitude = (EditText) findViewById(R.id.txtUploadPhotoLongtitude);

        txtLatitude = (EditText) findViewById(R.id.txtUploadPhotoLatitude);

        txtTags = (EditText) findViewById(R.id.txtUploadPhotoTags);
        btnUseMap = (ImageButton) findViewById(R.id.btnUploadPhotoPutPos);
		btnUseMap.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v)
			{

                LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                boolean enabled = locationManager
                        .isProviderEnabled(LocationManager.GPS_PROVIDER);
                //Checks if GPS is active or not.
                if (!enabled) {
                    //Added dialog box.
                    //Added material design.
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                            CropImage.this, R.style.AppCompatAlertDialogStyle);
                    alertDialogBuilder
                            .setMessage("GPS is disabled in your device. Enable it?")
                            .setCancelable(false)
                            .setPositiveButton("Enable GPS",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog,
                                                            int id) {
                                            Intent callGPSSettingIntent = new Intent(
                                                    android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                            CropImage.this.startActivity(callGPSSettingIntent);
                                        }
                                    });
                    alertDialogBuilder.setNegativeButton("Cancel",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
                    AlertDialog alert = alertDialogBuilder.create();
                    alert.show();

                }else{
                    Toast.makeText(CropImage.this,"GPS is enabled",Toast.LENGTH_SHORT).show();
                }
            }

				/*Intent _itent = new Intent(ctx, UploadMap.class);
				_itent.putExtra("latitude", txtLatitude.getText().toString());
				_itent.putExtra("longitude", txtLongtitude.getText().toString());
				startActivityForResult(_itent, GET_POSITION_ON_MAP);*/

		});
        // CR: remove TODO's.
        // TODO: we may need to show this indicator for the main gallery
        // application
        // MenuHelper.showStorageToast(this);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            if (extras.getString("circleCrop") != null) {
                mCircleCrop = true;
                mAspectX = 1;
                mAspectY = 1;
            }
            Date date = new Date();
            Long longTime = new Long(date.getTime()/1000);
            newpath = Environment.getExternalStorageDirectory()+"/phimp.me/PhimpMe_Photo_Effect"+"/tmp_"+longTime+".jpg";
            mSaveUri = Uri.fromFile(new File(newpath));
            //mSaveUri = (Uri) extras.getParcelable(MediaStore.EXTRA_OUTPUT);
            if (mSaveUri != null) {
                String outputFormatString = extras.getString("outputFormat");
                if (outputFormatString != null) {
                    mOutputFormat = Bitmap.CompressFormat.valueOf(outputFormatString);
                }
            }
            //mBitmap = (Bitmap) extras.getParcelable("data");
            mImagePath = extras.getString("image-path");
            activitynam = extras.getString("activityName");
            p = mImagePath.split(";");
            try{
                File f =  new File(p[0]);
                ExifInterface exif_data = null;
                geoDegrees _g = null;
                String la = "";
                String lo = "";
                try
                {
                    exif_data = new ExifInterface(f.getAbsolutePath());
                    _g = new geoDegrees(exif_data);
                    if (_g.isValid())
                    {
                        la = _g.getLatitude() + "";
                        lo = _g.getLongitude() + "";
                    }
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
                finally
                {
                    exif_data = null;
                    _g = null;
                }
                txtPhotoTitle.setText(extras.getString("title"));
                txtLatitude.setText(la);
                txtLongtitude.setText(lo);
			/*if (p.length == 2){
				JSONObject js = new JSONObject(p[1]);
				txtLatitude.setText(js.getString("lati"));
				txtLongtitude.setText(js.getString("logi"));
				txtPhotoTitle.setText(js.getString("name"));
				txtTags.setText(js.getString("tags"));
			}*/
            }catch(Exception e){}
            gpsloading = new ProgressDialog(ctx);
            gpsloading.setCancelable(true);
            gpsloading.setCanceledOnTouchOutside(false);
            gpsloading.setTitle(getString(R.string.loading));
            gpsloading.setMessage(getString(R.string.infor_upload_loading_current_position));
            gpsloading.setIndeterminate(true);
            WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
            Display display = wm.getDefaultDisplay();
            screen_w = display.getWidth();
            mBitmap = decodeSampledBitmapFromFile(this, p[0], screen_w);
            modifiedBitmap= flippedImaged = mBitmap;
            mAspectX = extras.getInt("aspectX");
            mAspectY = extras.getInt("aspectY");
            mOutputX = extras.getInt("outputX");
            mOutputY = extras.getInt("outputY");
            mScale = extras.getBoolean("scale", true);
            mScaleUp = extras.getBoolean("scaleUpIfNeeded", true);
            mDoFaceDetection = extras.containsKey("noFaceDetection") ? !extras.getBoolean("noFaceDetection") : true;
        }

        if (mBitmap == null) {
            Toast.makeText(ctx, "Sorry,can't load this photo !",Toast.LENGTH_SHORT).show();
            /*// Create a MediaItem representing the URI.
            Uri target = intent.getData();
            String targetScheme = target.getScheme();
            int rotation = 0;

            if (targetScheme.equals("content")) {
                mItem = LocalDataSource.createMediaItemFromUri(this, target, MediaItem.MEDIA_TYPE_IMAGE);
            }
            try {
                if (mItem != null) {
                    mBitmap = UriTexture.createFromUri(this, mItem.mContentUri, 1024, 1024, 0, null);
                    rotation = (int) mItem.mRotation;
                } else {
                    mBitmap = UriTexture.createFromUri(this, target.toString(), 1024, 1024, 0, null);
                    if (targetScheme.equals("file")) {
                        ExifInterface exif = new ExifInterface(target.getPath());
                        rotation = (int) Shared.exifOrientationToDegrees(exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                                ExifInterface.ORIENTATION_NORMAL));
                    }
                }
            } catch (IOException e) {
            } catch (URISyntaxException e) {
            }

            if (mBitmap != null && rotation != 0f) {
                mBitmap = Util.rotate(mBitmap, rotation);
            }*/
        }

        if (mBitmap == null) {
            Log.e(TAG, "Cannot load bitmap, exiting.");
            finish();
            return;
        }

        // Make UI fullscreen.
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        findViewById(Res.id.discard).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });

        // Keep track of initial width and height of the image
        initialWidth = mBitmap.getWidth();
        initialHeight = mBitmap.getHeight();

        findViewById(Res.id.save).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(txtLongtitude.getText().toString().equals("")&& !txtLatitude.getText().toString().equals("")){
                    AlertDialog.Builder builder=new AlertDialog.Builder(ctx);
                    builder.setMessage("Please enter Longtitude !");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {}
                    });
                    builder.create().show();
                }else if(!txtLongtitude.getText().toString().equals("")&& txtLatitude.getText().toString().equals("")){
                    AlertDialog.Builder builder=new AlertDialog.Builder(ctx);
                    builder.setMessage("Please enter Latitude !");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {}
                    });
                    builder.create().show();
                }
                else{
                    //========================save ==============================
                    //don't have geolocation
                    if(txtLongtitude.getText().toString().equals("") && txtLatitude.getText().toString().equals("")){
                        onSaveClicked();
                        if (!activitynam.equals("Upload")){
                            Intent intent=new Intent(ctx, PhimpMe.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        }
                    }else{
                        //have geolocation
                        double lon=Double.parseDouble((txtLongtitude.getText().toString()));
                        double la=Double.parseDouble((txtLatitude.getText().toString()));
                        //Log.e("CropImage", "Longtitude : "+lon+", Latitude : "+la);
                        if((lon>180 ||lon+180 <0)){
                            Log.e("CropImage","Longtitude is limit");
                            AlertDialog.Builder builder=new AlertDialog.Builder(ctx);
                            builder.setMessage("Longtitude has minimum value is -180 and maximum value is 180. Please try again !");
                            builder.setPositiveButton("OK", new DialogInterface.OnClickListener()
                            {
                                @Override
                                public void onClick(DialogInterface dialog, int which)
                                {
                                    txtLongtitude.requestFocus();
                                }
                            });
                            builder.create().show();
                        }else if((la > 90 ||la + 90 <0)){
                            Log.e("CropImage","Latitude is limit");
                            AlertDialog.Builder builder=new AlertDialog.Builder(ctx);
                            builder.setMessage("Latitude has minimum value is -90 and maximum value is 90. Please try again !");
                            builder.setPositiveButton("OK", new DialogInterface.OnClickListener()
                            {
                                @Override
                                public void onClick(DialogInterface dialog, int which)
                                {
                                    txtLatitude.requestFocus();
                                }
                            });
                            builder.create().show();
                        }
                        else{
                            onSaveClicked();
                            if (!activitynam.equals("Upload")){
                                Intent intent=new Intent(ctx, PhimpMe.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                            }
                        }
                    }

                }


            }
        });

        findViewById(R.id.btnRotateLeftRight).setOnClickListener (
                new View.OnClickListener() {
                    public void onClick (View v) {
                        try {
                            /*doRotate(mImageView, fromDegree, toDegree);
                            fromDegree = (toDegree == 360) ? 0 : toDegree;
                            toDegree += 90;
                            if (toDegree > 360) {
                                toDegree = 90;
                            }*/
                            // Flags an edit
                            if (!imageEdited) {
                                if (turns % 3 == 0 && turns != 0) {
                                    imageEdited = false;
                                    turns = 0;
                                } else {
                                    imageEdited = true;
                                }
                                turns++;
                            }

                            doRotate90(mImageView);
                            mImageView.mHighlightViews.clear();
                            if (mBitmap != null && !mBitmap.isRecycled()) {
                                mBitmap.recycle();
                                mBitmap = null;
                            }
                            mBitmap = modifiedBitmap;
                            startFaceDetection();
                        } catch (OutOfMemoryError o) {
                            o.printStackTrace();
                        }
                    }
                });

        findViewById(R.id.btnRotateTopDown).setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        try {
                            // Flags an edit
                            flipped = !flipped;
                            modifiedBitmap = doVerticalFlip(modifiedBitmap);
                            flippedImaged = doVerticalFlip(flippedImaged);
                            mImageView.setImageBitmap(changeBrightness(modifiedBitmap, brightnessValue));
                            mBitmap = modifiedBitmap;
                        } catch (OutOfMemoryError o) {
                            mBitmapResize = getResizedBitmap(mBitmap, (mBitmap.getHeight()/4), (mBitmap.getWidth()/4));
                            //mBitmapResize=getResizedBitmap(p[0], (mBitmap.getHeight()/2), (mBitmap.getWidth()/2));
                            modifiedBitmap=flippedImaged=mBitmapResize;
                        }
                    }
                });

        findViewById(R.id.btnBlackAndWhite).setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        try {
                            // Flags an edit
                            imageEdited = true;
                            modifiedBitmap = null;
                            modifiedBitmap = ImagesFilter.convertToBW(flippedImaged);
                            mImageView.setImageBitmap(changeBrightness(modifiedBitmap, brightnessValue));
                            mBitmap = modifiedBitmap;
                        } catch (OutOfMemoryError o) {
                            mBitmapResize=getResizedBitmap(mBitmap, (mBitmap.getHeight()/4), (mBitmap.getWidth()/4));
                            //mBitmapResize=getResizedBitmap(p[0], (mBitmap.getHeight()/2), (mBitmap.getWidth()/2));
                            modifiedBitmap=flippedImaged=mBitmapResize;
                        }
                    }
                });

        findViewById(R.id.btnSaphia).setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        try {
                            // Flags an edit
                            imageEdited = true;
                            modifiedBitmap = null;
                            modifiedBitmap = ImagesFilter.convertToSepia(flippedImaged);
                            mImageView.setImageBitmap(changeBrightness(modifiedBitmap, brightnessValue));
                            mBitmap = modifiedBitmap;
                        } catch (OutOfMemoryError o) {
                            mBitmapResize=getResizedBitmap(mBitmap, (mBitmap.getHeight()/4), (mBitmap.getWidth()/4));
                            modifiedBitmap=flippedImaged=mBitmapResize;
                        }
                    }
                });
        findViewById(R.id.btnalpha).setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        try {
                            // Flags an edit
                            imageEdited = true;
                            modifiedBitmap = null;
                            modifiedBitmap = ImagesFilter.convertToAlpha(flippedImaged);
                            mImageView.setImageBitmap(changeBrightness(modifiedBitmap, brightnessValue));
                            mBitmap = modifiedBitmap;
                        } catch (OutOfMemoryError o) {
                            mBitmapResize=getResizedBitmap(mBitmap, (mBitmap.getHeight()/4), (mBitmap.getWidth()/4));
                            modifiedBitmap=flippedImaged=mBitmapResize;
                        }
                    }
                });
        findViewById(R.id.btnpink).setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        try {
                            // Flags an edit
                            imageEdited = true;
                            modifiedBitmap = null;
                            modifiedBitmap = ImagesFilter.convertToPink(flippedImaged);
                            mImageView.setImageBitmap(changeBrightness(modifiedBitmap, brightnessValue));
                            mBitmap = modifiedBitmap;
                        } catch (OutOfMemoryError o) {
                            mBitmapResize=getResizedBitmap(mBitmap, (mBitmap.getHeight()/4), (mBitmap.getWidth()/4));
                            modifiedBitmap=flippedImaged=mBitmapResize;
                        }
                    }
                });
        findViewById(R.id.btnpolaroid).setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        try {
                            // Flags an edit
                            imageEdited = true;
                            modifiedBitmap = null;
                            modifiedBitmap = ImagesFilter.convertToPolaroid(flippedImaged);
                            mImageView.setImageBitmap(changeBrightness(modifiedBitmap, brightnessValue));
                            mBitmap = modifiedBitmap;
                        } catch (OutOfMemoryError o) {
                            mBitmapResize=getResizedBitmap(mBitmap, (mBitmap.getHeight()/4), (mBitmap.getWidth()/4));
                            modifiedBitmap=flippedImaged=mBitmapResize;
                        }
                    }
                });
        findViewById(R.id.btnblur).setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        try {
                            // Flags an edit
                            imageEdited = true;
                            modifiedBitmap = null;
                            modifiedBitmap = ImagesFilter.converttoBlur(flippedImaged,9,getApplicationContext());
                            mImageView.setImageBitmap(changeBrightness(modifiedBitmap, brightnessValue));
                            mBitmap = modifiedBitmap;
                        } catch (OutOfMemoryError o) {
                            mBitmapResize=getResizedBitmap(mBitmap, (mBitmap.getHeight()/4), (mBitmap.getWidth()/4));
                            modifiedBitmap=flippedImaged=mBitmapResize;
                        }
                    }
                });
        findViewById(R.id.btnsharp).setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        try {
                            // Flags an edit
                            imageEdited = true;
                            modifiedBitmap = null;
                            modifiedBitmap = ImagesFilter.convertToSharp(flippedImaged,getApplicationContext());
                            mImageView.setImageBitmap(changeBrightness(modifiedBitmap, brightnessValue));
                            mBitmap = modifiedBitmap;
                        } catch (OutOfMemoryError o) {
                            mBitmapResize=getResizedBitmap(mBitmap, (mBitmap.getHeight()/4), (mBitmap.getWidth()/4));
                            modifiedBitmap=flippedImaged=mBitmapResize;
                        }
                    }
                });
        findViewById(R.id.btnedge).setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        try {
                            // Flags an edit
                            imageEdited = true;
                            modifiedBitmap = null;
                            modifiedBitmap = ImagesFilter.convertToEdge(flippedImaged,getApplicationContext());
                            mImageView.setImageBitmap(changeBrightness(modifiedBitmap, brightnessValue));
                            mBitmap = modifiedBitmap;
                        } catch (OutOfMemoryError o) {
                            mBitmapResize=getResizedBitmap(mBitmap, (mBitmap.getHeight()/4), (mBitmap.getWidth()/4));
                            modifiedBitmap=flippedImaged=mBitmapResize;
                        }
                    }
                });
        findViewById(R.id.btnfuzz).setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        try {
                            // Flags an edit
                            imageEdited = true;
                            modifiedBitmap = null;
                            modifiedBitmap = ImagesFilter.convertToFuzz(flippedImaged,getApplicationContext());
                            mImageView.setImageBitmap(changeBrightness(modifiedBitmap, brightnessValue));
                            mBitmap = modifiedBitmap;
                        } catch (OutOfMemoryError o) {
                            mBitmapResize=getResizedBitmap(mBitmap, (mBitmap.getHeight()/4), (mBitmap.getWidth()/4));
                            modifiedBitmap=flippedImaged=mBitmapResize;
                        }
                    }
                });
        findViewById(R.id.btnNegative).setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        try {
                            // Flags an edit
                            imageEdited = true;
                            modifiedBitmap = null;
                            modifiedBitmap = ImagesFilter.convertToNegative(flippedImaged);
                            mImageView.setImageBitmap(changeBrightness(modifiedBitmap, brightnessValue));
                            mBitmap = modifiedBitmap;
                        } catch (OutOfMemoryError o) {
                            mBitmapResize=getResizedBitmap(mBitmap, (mBitmap.getHeight()/4), (mBitmap.getWidth()/4));
                            //mBitmapResize=getResizedBitmap(p[0], (mBitmap.getHeight()/2), (mBitmap.getWidth()/2));
                            modifiedBitmap=flippedImaged=mBitmapResize;
                        }
                    }
                });

        ImageButton btnReflection=(ImageButton)findViewById(R.id.btnSnow);
        btnReflection.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        try {
                            // Flags an edit
                            imageEdited = true;
                            modifiedBitmap = null;
                            modifiedBitmap = ImagesFilter.applySnowEffect(flippedImaged);
                            mImageView.setImageBitmap(changeBrightness(modifiedBitmap, brightnessValue));
                            mBitmap = modifiedBitmap;
                        } catch (OutOfMemoryError o) {
                            mBitmapResize=getResizedBitmap(mBitmap, (mBitmap.getHeight()/4), (mBitmap.getWidth()/4));
                            //mBitmapResize=getResizedBitmap(p[0], (mBitmap.getHeight()/2), (mBitmap.getWidth()/2));
                            modifiedBitmap=flippedImaged=mBitmapResize;
                        }
                    }
                });

        ImageButton btnRoundCorner=(ImageButton)findViewById(R.id.btnRoundCorner);
        btnRoundCorner.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        try {
                            // Flags an edit
                            imageEdited = true;
                            modifiedBitmap = null;
                            modifiedBitmap = ImagesFilter.roundCorner(flippedImaged,45f);
                            mImageView.setImageBitmap(changeBrightness(modifiedBitmap, brightnessValue));
                            mBitmap = modifiedBitmap;
                        } catch (OutOfMemoryError o) {
                            mBitmapResize=getResizedBitmap(mBitmap, (mBitmap.getHeight()/4), (mBitmap.getWidth()/4));
                            //mBitmapResize=getResizedBitmap(p[0], (mBitmap.getHeight()/2), (mBitmap.getWidth()/2));
                            modifiedBitmap=flippedImaged=mBitmapResize;
                        }
                    }
                });

        ImageButton btnOriginal=(ImageButton)findViewById(R.id.btnOriginal);
        btnOriginal.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        try {
                            // Flags an edit
                            imageEdited = false;
                            // doRotate(mImageView, 0, 0);
                            // fromDegree = 0;
                            // toDegree = 90;
                            mBitmap.recycle();
                            mBitmap = decodeSampledBitmapFromFile(ctx, p[0], screen_w);
                            mImageView.setImageBitmap(mBitmap);
                            modifiedBitmap = flippedImaged = mBitmap;
                        } catch(OutOfMemoryError o) {
                            // doRotate(mImageView, 0, 0);
                            // fromDegree = 0;
                            // toDegree = 90;
                            mImageView.setImageBitmap(mBitmapResize);
                            modifiedBitmap = flippedImaged = mBitmapResize;
                        }
                    }
                });

        startFaceDetection();
    }

    @SuppressWarnings("static-access")
    private void startFaceDetection() {
        if (isFinishing()) {
            return;
        }

        mImageView.setImageBitmapResetBase(mBitmap, true);

        Util.startBackgroundJob(this, null, getResources().getString(Res.string.running_face_detection), new Runnable() {
            public void run() {
                final CountDownLatch latch = new CountDownLatch(1);
                final Bitmap b = mBitmap;
                mHandler.post(new Runnable() {
                    public void run() {
                        if (b != mBitmap && b != null) {
                            mImageView.setImageBitmapResetBase(b, true);
                            mBitmap.recycle();
                            mBitmap = b;
                        }
                        if (mImageView.getScale() == 1.0f) {
                            mImageView.center(true, true);
                        }
                        latch.countDown();
                    }
                });
                try {
                    latch.await();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                mRunFaceDetection.run();
            }
        }, mHandler);
    }

    @SuppressWarnings("static-access")
    private void onSaveClicked() {
        // CR: TODO!
        // TODO this code needs to change to use the decode/crop/encode single
        // step api so that we don't require that the whole (possibly large)
        // bitmap doesn't have to be read into memory
        if (mSaving)
            return;

        if (mCrop == null) {
            return;
        }

        mSaving = true;

        Rect r = mCrop.getCropRect();

        int width = r.width(); // CR: final == happy panda!
        int height = r.height();

        // Check if the image has been cropped at all
        if (initialHeight != height | initialWidth != width) {
            imageEdited = true;
        }

        // If we are circle cropping, we want alpha channel, which is the
        // third param here.
        Bitmap croppedImage = Bitmap.createBitmap(width, height, mCircleCrop ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565);
        {
            Canvas canvas = new Canvas(croppedImage);
            Rect dstRect = new Rect(0, 0, width, height);
            canvas.drawBitmap(mBitmap, r, dstRect, null);
        }

        if (mCircleCrop) {
            // OK, so what's all this about?
            // Bitmaps are inherently rectangular but we want to return
            // something that's basically a circle. So we fill in the
            // area around the circle with alpha. Note the all important
            // PortDuff.Mode.CLEARes.
            Canvas c = new Canvas(croppedImage);
            Path p = new Path();
            p.addCircle(width / 2F, height / 2F, width / 2F, Path.Direction.CW);
            c.clipPath(p, Region.Op.DIFFERENCE);
            c.drawColor(0x00000000, PorterDuff.Mode.CLEAR);
        }

        // If the output is required to a specific size then scale or fill.
        if (mOutputX != 0 && mOutputY != 0) {
            if (mScale) {
                // Scale the image to the required dimensions.
                Bitmap old = croppedImage;
                croppedImage = Util.transform(new Matrix(), croppedImage, mOutputX, mOutputY, mScaleUp);
                if (old != croppedImage) {
                    old.recycle();
                }
            } else {

                /*
                 * Don't scale the image crop it to the size requested. Create
                 * an new image with the cropped image in the center and the
                 * extra space filled.
                 */

                // Don't scale the image but instead fill it so it's the
                // required dimension
                Bitmap b = Bitmap.createBitmap(mOutputX, mOutputY, Bitmap.Config.RGB_565);
                Canvas canvas = new Canvas(b);

                Rect srcRect = mCrop.getCropRect();
                Rect dstRect = new Rect(0, 0, mOutputX, mOutputY);

                int dx = (srcRect.width() - dstRect.width()) / 2;
                int dy = (srcRect.height() - dstRect.height()) / 2;

                // If the srcRect is too big, use the center part of it.
                srcRect.inset(Math.max(0, dx), Math.max(0, dy));

                // If the dstRect is too big, use the center part of it.
                dstRect.inset(Math.max(0, -dx), Math.max(0, -dy));

                // Draw the cropped bitmap in the center.
                canvas.drawBitmap(mBitmap, srcRect, dstRect, null);

                // Set the cropped bitmap as the new bitmap.
                croppedImage.recycle();
                croppedImage = b;

            }
        }
        // Return the cropped image directly or save it to the specified URI.
        Bundle myExtras = getIntent().getExtras();
        if (myExtras != null && (myExtras.getParcelable("data") != null || myExtras.getBoolean("return-data"))) {
            Bundle extras = new Bundle();
            extras.putParcelable("data", croppedImage);
            setResult(RESULT_OK, (new Intent()).setAction("inline-data").putExtras(extras));
            finish();
        } else {
            final Bitmap b = croppedImage;
            final Runnable save = new Runnable() {
                public void run() {
                    saveOutput(b);
                }
            };
            Util.startBackgroundJob(this, null, getResources().getString(Res.string.saving_image), save, mHandler);
        }
    }

    public Bitmap getResizedBitmap(Bitmap bm, int newHeight, int newWidth) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);


        // RECREATE THE NEW BITMAP
        try{
            Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);
            return resizedBitmap;
        }catch(OutOfMemoryError o){
            Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width/2, height/2, matrix, false);
            return resizedBitmap;
        }
    }
    public static void doRotate(ImageView im, float fromDegree, float toDegree) {
        RotateAnimation ra1 = new RotateAnimation(fromDegree, toDegree,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        ra1.setStartOffset(0);
        ra1.setDuration(0);
        ra1.setFillAfter(true);
        ra1.setFillEnabled(true);
        im.startAnimation(ra1);
    }
    public static void doRotate90(ImageView image){
        image.setDrawingCacheEnabled(true);
        Bitmap bm = modifiedBitmap;
        Matrix mx = new Matrix();
        //float scaleW = (float)bm.getHeight()/bm.getWidth();
        //float scaleH = (float)bm.getWidth()/bm.getHeight();
        mx.setRotate(90);
        //modifiedBitmap.recycle();
        modifiedBitmap = Bitmap.createBitmap(bm,0,0,bm.getWidth(),bm.getHeight(),mx,true);
        image.setImageBitmap(modifiedBitmap);
    }
    public static Bitmap doHorizontalFlip(Bitmap sampleBitmap) {
        Matrix matrixHorizontalFlip = new Matrix();
        matrixHorizontalFlip.preScale(-1.0f, 1.0f);
        Bitmap des = sampleBitmap.copy(Bitmap.Config.ARGB_8888, true);
        des = Bitmap.createBitmap(sampleBitmap, 0, 0, sampleBitmap.getWidth(), sampleBitmap.getHeight(),
                matrixHorizontalFlip, true);
        return des;
    }

    public static Bitmap doVerticalFlip(Bitmap sampleBitmap) {
        Matrix matrixVerticalFlip = new Matrix();
        matrixVerticalFlip.preScale(1.0f, -1.0f);
        try {
            return Bitmap.createBitmap(sampleBitmap, 0, 0, sampleBitmap.getWidth(), sampleBitmap.getHeight(),
                    matrixVerticalFlip, true);
        } catch (Exception e) {
            return sampleBitmap;
        }

    }
    public static Bitmap changeBrightness(Bitmap sampleBitmap,
                                          int brightnessValue) {
        ColorMatrix sepiaMatrix = new ColorMatrix();
        float[] sepMat = { 1, 0, 0, 0, brightnessValue, 0, 1, 0, 0,
                brightnessValue, 0, 0, 1, 0, brightnessValue, 0, 0, 0, 1,
                brightnessValue };
        sepiaMatrix.set(sepMat);
        final ColorMatrixColorFilter colorFilter = new ColorMatrixColorFilter(
                sepiaMatrix);
        Bitmap rBitmap = sampleBitmap.copy(Bitmap.Config.ARGB_8888, true);
        Paint paint = new Paint();
        paint.setColorFilter(colorFilter);
        Canvas myCanvas = new Canvas(rBitmap);
        myCanvas.drawBitmap(rBitmap, 0, 0, paint);
        return rBitmap;
    }
    public static int calculateInSampleSize(

            String mfile, int reqWidth) {
        // Raw height and width of image
        //Bitmap bm = null;
        BitmapFactory.Options bounds = new BitmapFactory.Options();
        bounds.inTempStorage = new byte[32 * 1024];
        bounds.inDither=false;
        bounds.inPurgeable=true;
        bounds.inInputShareable=true;
        bounds.inJustDecodeBounds = true;
        File file=new File(mfile);
        float rotate = getOrientation(mfile);
        FileInputStream fs=null;
        try {
            fs = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            //TODO do something intelligent
            e.printStackTrace();
        }

        try {
            if(fs!=null) BitmapFactory.decodeFileDescriptor(fs.getFD(), null, bounds);
        } catch (IOException e) {
            //TODO do something intelligent
            e.printStackTrace();
        } finally{
            if(fs!=null) {
                try {
                    fs.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        //final int height = bounds.outHeight;
        final int width;
        if (rotate == 0)
            width = bounds.outWidth; else
            width = bounds.outHeight;

        Log.e("Width",String.valueOf(bounds.outWidth));
        Log.e("Height",String.valueOf(bounds.outHeight));
        System.gc();
        int inSampleSize = 1;

        if (width > reqWidth) {
            inSampleSize = Math.round((float)width / (float)reqWidth);
        }
        return inSampleSize;
    }
    public static Bitmap decodeSampledBitmapFromFile(Context ctx, String mfile, int reqWidth) {
        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        Bitmap bm = null;
        System.gc();
        // BitmapFactory.decodeResource(res, resId, options);
        options.inTempStorage = new byte[32* 1024];
        options.inDither = false;
        options.inPurgeable = true;
        options.inInputShareable = true;
        // Calculate inSampleSize
        int inSampleSize = calculateInSampleSize(mfile, reqWidth);
        options.inSampleSize = inSampleSize;
        Log.e("Sample Size", String.valueOf(inSampleSize));
        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        File file=new File(mfile);
        FileInputStream fs=null;
        try {
            fs = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            //TODO do something intelligent
            e.printStackTrace();
        }
        Matrix matrix = new Matrix();
        float rotate = getOrientation(mfile);
        try {
            if(fs!=null) {

                if (rotate == 0) return BitmapFactory.decodeFileDescriptor(fs.getFD(), null, options);
                else {
                    matrix.postRotate(rotate);

                    bm = BitmapFactory.decodeFileDescriptor(fs.getFD(), null, options);
                    // if ((bm.getWidth() < reqWidth) && (inSampleSize > 1)){
                    Log.e("Width image",String.valueOf(bm.getWidth()));
                    return Bitmap.createBitmap(bm,0,0,bm.getWidth(),bm.getHeight(),matrix,true);

	        		 /*}else{
	        			 return Bitmap.createBitmap(bm,0,0,bm.getWidth(),bm.getHeight(),matrix,true);}*/
                }
            }

        } catch (IOException e) {
            //TODO do something intelligent
            e.printStackTrace();
        } finally{
            if(fs!=null) {
                try {
                    fs.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
    public static float getOrientation(String f)
    {
        float degress = 0;
        try {

            ExifInterface exif = new ExifInterface(f);
            int orientation=exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
            Log.e("Orientation Crop Image",String.valueOf(orientation));
            if(orientation == 3){
                degress = 180;
            }
            if(orientation == 6){
                degress = 90;
            }
            if(orientation == 8){
                degress = 270;
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return degress;
    }
    private void saveOutput(Bitmap croppedImage) {
        if (mSaveUri != null) {
            OutputStream outputStream = null;
            try {
                outputStream = mContentResolver.openOutputStream(mSaveUri);
                if (outputStream != null) {
                    croppedImage.compress(mOutputFormat, 80, outputStream);
                }
                // TODO ExifInterface write
            } catch (IOException ex) {
                Log.e(TAG, "Cannot open file: " + mSaveUri, ex);
            } finally {
                Util.closeSilently(outputStream);
            }
            Bundle extras = new Bundle();
            setResult(RESULT_OK, new Intent(mSaveUri.toString()).putExtras(extras));
        } else {
            Bundle extras = new Bundle();
            extras.putString("rect", mCrop.getCropRect().toString());
            if (mItem == null) {
                // CR: Comments should be full sentences.
                // this image doesn't belong to the local data source
                // we can add it locally if necessary
            } else {
                File oldPath = new File(mItem.mFilePath);
                File directory = new File(oldPath.getParent());

                int x = 0;
                String fileName = oldPath.getName();
                fileName = fileName.substring(0, fileName.lastIndexOf("."));

                // Try file-1.jpg, file-2.jpg, ... until we find a filename
                // which
                // does not exist yet.
                while (true) {
                    x += 1;
                    String candidate = directory.toString() + "/" + fileName + "-" + x + ".jpg";
                    boolean exists = (new File(candidate)).exists();
                    if (!exists) { // CR: inline the expression for exists
                        // here--it's clear enough.
                        break;
                    }
                }

                MediaItem item = mItem;
                String title = fileName + "-" + x;
                String finalFileName = title + ".jpg";
                int[] degree = new int[1];
                Double latitude = null;
                Double longitude = null;
                if (item.isLatLongValid()) {
                    latitude = new Double(item.mLatitude);
                    longitude = new Double(item.mLongitude);
                }
                Uri newUri = ImageManager.addImage(mContentResolver, title,
                        item.mDateAddedInSec, item.mDateTakenInMs, latitude,
                        longitude, directory.toString(), finalFileName,
                        croppedImage, null, degree);
                if (newUri != null) {
                    setResult(RESULT_OK, new Intent().setAction(newUri.toString()).putExtras(extras));
                } else {
                    setResult(RESULT_OK, new Intent().setAction(null));
                }
            }
        }
        if (!txtLatitude.getText().toString().equals("")){
            ExifInterface exif;
            try {
                Log.e("Exif data","Run");
                // If the image has been edited, it will be saved along with the original image
                if (imageEdited | flipped) {
                    exif = new ExifInterface(newpath);
                    createExifData(exif, Double.parseDouble(txtLatitude.getText().toString()), Double.parseDouble(txtLongtitude.getText().toString()));
                    exif.saveAttributes();
                } else {
                    // Else, the temporary image will be deleted!
                    File file = new File(newpath);
                    if(file.exists()) {
                        file.delete();
                    }
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, mSaveUri));
        /*sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED,
                Uri.parse("file://" + Environment.getExternalStorageDirectory())));   */
        croppedImage.recycle();
        Intent intent = this.getIntent();
        intent.putExtra("Impath", mImagePath);
        intent.putExtra("saveUri",newpath);
        intent.putExtra("lati", txtLatitude.getText().toString());
        intent.putExtra("logi",txtLongtitude.getText().toString());
        intent.putExtra("tags",txtTags.getText().toString());
        intent.putExtra("name", txtPhotoTitle.getText().toString());
        //Log.i("CropImage","Image path output save : "+newpath);
        setResult(RESULT_OK,intent);
        finish();
    }
    public void createExifData(ExifInterface exif, double lattude, double longitude){

        if (lattude < 0) {
            exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE_REF, "S");
            lattude = -lattude;
        } else {
            exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE_REF, "N");
        }

        exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE,
                formatLatLongString(lattude));

        if (longitude < 0) {
            exif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF, "W");
            longitude = -longitude;
        } else {
            exif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF, "E");
        }
        exif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE,
                formatLatLongString(longitude));

        try {
            exif.saveAttributes();
        } catch (IOException e) {

            e.printStackTrace();
        }
        exif.setAttribute(ExifInterface.TAG_DATETIME, (new Date(System.currentTimeMillis())).toString()); // set the date & time

    }

    private static String formatLatLongString(double d) {
        StringBuilder b = new StringBuilder();
        b.append((int) d);
        b.append("/1,");
        d = (d - (int) d) * 60;
        b.append((int) d);
        b.append("/1,");
        d = (d - (int) d) * 60000;
        b.append((int) d);
        b.append("/1000");
        return b.toString();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        switch (requestCode)
        {
            case GET_POSITION_ON_MAP:
            {
                if (resultCode == Activity.RESULT_OK)
                {/*
					txtLatitude.setText("");
					txtLongtitude.setText("");
					int lat = data.getIntExtra("latitude", 0);
					int log = data.getIntExtra("longitude", 0);
					float _lat = (float) (lat / 1E6);
					float _log = (float) (log / 1E6);

					Log.e("thong", "CropImage: Result OK, latitude : "+_lat+",longitude : "+_log);
					txtLatitude.setText(String.valueOf(_lat));

					txtLongtitude.setText(String.valueOf(_log));*/
                }
                break;
            }
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        mApp.onResume();

        //get Geolocation from Map

        if(from.equals("Map")){
            float _latitude=(float)(latitude/1E6);
            float _longitude=(float)(longitude/1E6);
            Log.e("CropImage-onResume","Latitude : "+_latitude+",longitude : "+_longitude);
            txtLatitude.setText(String.valueOf(_latitude));
            txtLongtitude.setText(String.valueOf(_longitude));
            from="";
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        BitmapManager.instance().cancelThreadDecoding(mDecodingThreads);
        mApp.onPause();
    }

    @Override
    protected void onDestroy() {
        mApp.shutdown();
        super.onDestroy();
    }

    Runnable mRunFaceDetection = new Runnable() {
        float mScale = 1F;
        Matrix mImageMatrix;
        FaceDetector.Face[] mFaces = new FaceDetector.Face[3];
        int mNumFaces;

        // For each face, we create a HightlightView for it.
        private void handleFace(FaceDetector.Face f) {
            PointF midPoint = new PointF();

            int r = ((int) (f.eyesDistance() * mScale)) * 2;
            f.getMidPoint(midPoint);
            midPoint.x *= mScale;
            midPoint.y *= mScale;

            int midX = (int) midPoint.x;
            int midY = (int) midPoint.y;

            HighlightView hv = new HighlightView(mImageView);

            int width = mBitmap.getWidth();
            int height = mBitmap.getHeight();

            Rect imageRect = new Rect(0, 0, width, height);

            RectF faceRect = new RectF(midX, midY, midX, midY);
            faceRect.inset(-r, -r);
            if (faceRect.left < 0) {
                faceRect.inset(-faceRect.left, -faceRect.left);
            }

            if (faceRect.top < 0) {
                faceRect.inset(-faceRect.top, -faceRect.top);
            }

            if (faceRect.right > imageRect.right) {
                faceRect.inset(faceRect.right - imageRect.right, faceRect.right - imageRect.right);
            }

            if (faceRect.bottom > imageRect.bottom) {
                faceRect.inset(faceRect.bottom - imageRect.bottom, faceRect.bottom - imageRect.bottom);
            }

            hv.setup(mImageMatrix, imageRect, faceRect, mCircleCrop, mAspectX != 0 && mAspectY != 0);

            mImageView.add(hv);
        }

        // Create a default HightlightView if we found no face in the picture.
        private void makeDefault() {
            HighlightView hv = new HighlightView(mImageView);

            int width = mBitmap.getWidth();
            int height = mBitmap.getHeight();

            Rect imageRect = new Rect(0, 0, width, height);

            // CR: sentences!
            // make the default size about 4/5 of the width or height
            int cropWidth = width;//Math.min(width, height) * 1;
            int cropHeight = height;

            if (mAspectX != 0 && mAspectY != 0) {
                if (mAspectX > mAspectY) {
                    cropHeight = cropWidth * mAspectY / mAspectX;
                } else {
                    cropWidth = cropHeight * mAspectX / mAspectY;
                }
            }

            int x = (width - cropWidth) / 2;
            int y = (height - cropHeight) / 2;

            RectF cropRect = new RectF(x, y, x + cropWidth, y + cropHeight);
            hv.setup(mImageMatrix, imageRect, cropRect, mCircleCrop, mAspectX != 0 && mAspectY != 0);
            mImageView.add(hv);
        }

        // Scale the image down for faster face detection.
        private Bitmap prepareBitmap() {
            if (mBitmap == null) {
                return null;
            }

            // 256 pixels wide is enough.
            if (mBitmap.getWidth() > 256) {
                mScale = 256.0F / mBitmap.getWidth(); // CR: F => f (or change
                // all f to F).
            }
            Matrix matrix = new Matrix();
            matrix.setScale(mScale, mScale);
            Bitmap faceBitmap = Bitmap.createBitmap(mBitmap, 0, 0, mBitmap.getWidth(), mBitmap.getHeight(), matrix, true);
            return faceBitmap;
        }

        public void run() {
            mImageMatrix = mImageView.getImageMatrix();
            Bitmap faceBitmap = prepareBitmap();

            mScale = 1.0F / mScale;
            if (faceBitmap != null && mDoFaceDetection) {
                FaceDetector detector = new FaceDetector(faceBitmap.getWidth(), faceBitmap.getHeight(), mFaces.length);
                mNumFaces = detector.findFaces(faceBitmap, mFaces);
            }

            if (faceBitmap != null && faceBitmap != mBitmap) {
                faceBitmap.recycle();
            }

            mHandler.post(new Runnable() {
                @SuppressWarnings("static-access")
                public void run() {
                    mWaitingToPick = mNumFaces > 1;
                    if (mNumFaces > 0) {
                        for (int i = 0; i < mNumFaces; i++) {
                            handleFace(mFaces[i]);
                        }
                    } else {
                        makeDefault();
                    }
                    mImageView.invalidate();
                    if (mImageView.mHighlightViews.size() == 1) {
                        mCrop = mImageView.mHighlightViews.get(0);
                        mCrop.setFocus(true);
                    }

                    if (mNumFaces > 1) {
                        // CR: no need for the variable t. just do
                        // Toast.makeText(...).show().
                        Toast t = Toast.makeText(CropImage.this, Res.string.multiface_crop_help, Toast.LENGTH_SHORT);
                        t.show();
                    }
                }
            });
        }
    };
}

class CropImageView extends ImageViewTouchBase {
    ArrayList<HighlightView> mHighlightViews = new ArrayList<HighlightView>();
    HighlightView mMotionHighlightView = null;
    float mLastX, mLastY;
    int mMotionEdge;

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (mBitmapDisplayed.getBitmap() != null) {
            for (HighlightView hv : mHighlightViews) {
                hv.mMatrix.set(getImageMatrix());
                hv.invalidate();
                if (hv.mIsFocused) {
                    centerBasedOnHighlightView(hv);
                }
            }
        }
        center(true, true);
        Log.e("Crop Image","Create");
    }

    public CropImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void zoomTo(float scale, float centerX, float centerY) {
        super.zoomTo(scale, centerX, centerY);
        for (HighlightView hv : mHighlightViews) {
            hv.mMatrix.set(getImageMatrix());
            hv.invalidate();
        }
    }

    @Override
    protected void zoomIn() {
        super.zoomIn();
        for (HighlightView hv : mHighlightViews) {
            hv.mMatrix.set(getImageMatrix());
            hv.invalidate();
        }
    }

    @Override
    protected void zoomOut() {
        super.zoomOut();
        for (HighlightView hv : mHighlightViews) {
            hv.mMatrix.set(getImageMatrix());
            hv.invalidate();
        }
    }

    @Override
    protected void postTranslate(float deltaX, float deltaY) {
        super.postTranslate(deltaX, deltaY);
        for (int i = 0; i < mHighlightViews.size(); i++) {
            HighlightView hv = mHighlightViews.get(i);
            hv.mMatrix.postTranslate(deltaX, deltaY);
            hv.invalidate();
        }
    }

    // According to the event's position, change the focus to the first
    // hitting cropping rectangle.
    private void recomputeFocus(MotionEvent event) {
        for (int i = 0; i < mHighlightViews.size(); i++) {
            HighlightView hv = mHighlightViews.get(i);
            hv.setFocus(false);
            hv.invalidate();
        }

        for (int i = 0; i < mHighlightViews.size(); i++) {
            HighlightView hv = mHighlightViews.get(i);
            int edge = hv.getHit(event.getX(), event.getY());
            if (edge != HighlightView.GROW_NONE) {
                if (!hv.hasFocus()) {
                    hv.setFocus(true);
                    hv.invalidate();
                }
                break;
            }
        }
        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        CropImage cropImage = (CropImage) getContext();
        if (cropImage.mSaving) {
            return false;
        }

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: // CR: inline case blocks.
                if (cropImage.mWaitingToPick) {
                    recomputeFocus(event);
                } else {
                    for (int i = 0; i < mHighlightViews.size(); i++) { // CR:
                        // iterator
                        // for; if
                        // not, then
                        // i++ =>
                        // ++i.
                        HighlightView hv = mHighlightViews.get(i);
                        int edge = hv.getHit(event.getX(), event.getY());
                        if (edge != HighlightView.GROW_NONE) {
                            mMotionEdge = edge;
                            mMotionHighlightView = hv;
                            mLastX = event.getX();
                            mLastY = event.getY();
                            // CR: get rid of the extraneous parens below.
                            mMotionHighlightView.setMode((edge == HighlightView.MOVE) ? HighlightView.ModifyMode.Move
                                    : HighlightView.ModifyMode.Grow);
                            break;
                        }
                    }
                }
                break;
            // CR: vertical space before case blocks.
            case MotionEvent.ACTION_UP:
                if (cropImage.mWaitingToPick) {
                    for (int i = 0; i < mHighlightViews.size(); i++) {
                        HighlightView hv = mHighlightViews.get(i);
                        if (hv.hasFocus()) {
                            cropImage.mCrop = hv;
                            for (int j = 0; j < mHighlightViews.size(); j++) {
                                if (j == i) { // CR: if j != i do your shit; no need
                                    // for continue.
                                    continue;
                                }
                                mHighlightViews.get(j).setHidden(true);
                            }
                            centerBasedOnHighlightView(hv);
                            ((CropImage) getContext()).mWaitingToPick = false;
                            return true;
                        }
                    }
                } else if (mMotionHighlightView != null) {
                    centerBasedOnHighlightView(mMotionHighlightView);
                    mMotionHighlightView.setMode(HighlightView.ModifyMode.None);
                }
                mMotionHighlightView = null;
                break;
            case MotionEvent.ACTION_MOVE:
                if (cropImage.mWaitingToPick) {
                    recomputeFocus(event);
                } else if (mMotionHighlightView != null) {
                    mMotionHighlightView.handleMotion(mMotionEdge, event.getX() - mLastX, event.getY() - mLastY);
                    mLastX = event.getX();
                    mLastY = event.getY();

                    if (true) {
                        // This section of code is optional. It has some user
                        // benefit in that moving the crop rectangle against
                        // the edge of the screen causes scrolling but it means
                        // that the crop rectangle is no longer fixed under
                        // the user's finger.
                        ensureVisible(mMotionHighlightView);
                    }
                }
                break;
        }

        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                center(true, true);
                break;
            case MotionEvent.ACTION_MOVE:
                // if we're not zoomed then there's no point in even allowing
                // the user to move the image around. This call to center puts
                // it back to the normalized location (with false meaning don't
                // animate).
                if (getScale() == 1F) {
                    center(true, true);
                }
                break;
        }

        return true;
    }

    // Pan the displayed image to make sure the cropping rectangle is visible.
    private void ensureVisible(HighlightView hv) {
        Rect r = hv.mDrawRect;

        int panDeltaX1 = Math.max(0, getLeft() - r.left);
        int panDeltaX2 = Math.min(0, getRight() - r.right);

        int panDeltaY1 = Math.max(0, getTop() - r.top);
        int panDeltaY2 = Math.min(0, getBottom() - r.bottom);

        int panDeltaX = panDeltaX1 != 0 ? panDeltaX1 : panDeltaX2;
        int panDeltaY = panDeltaY1 != 0 ? panDeltaY1 : panDeltaY2;

        if (panDeltaX != 0 || panDeltaY != 0) {
            panBy(panDeltaX, panDeltaY);
        }
    }

    // If the cropping rectangle's size changed significantly, change the
    // view's center and scale according to the cropping rectangle.
    private void centerBasedOnHighlightView(HighlightView hv) {
        Rect drawRect = hv.mDrawRect;

        float width = drawRect.width();
        float height = drawRect.height();

        float thisWidth = getWidth();
        float thisHeight = getHeight();

        float z1 = thisWidth / width * .6F;
        float z2 = thisHeight / height * .6F;

        float zoom = Math.min(z1, z2);
        zoom = zoom * this.getScale();
        zoom = Math.max(1F, zoom);

        if ((Math.abs(zoom - getScale()) / zoom) > .1) {
            float[] coordinates = new float[] { hv.mCropRect.centerX(), hv.mCropRect.centerY() };
            getImageMatrix().mapPoints(coordinates);
            zoomTo(zoom, coordinates[0], coordinates[1], 300F); // CR: 300.0f.
        }

        ensureVisible(hv);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (int i = 0; i < mHighlightViews.size(); i++) {
            mHighlightViews.get(i).draw(canvas);
        }
    }

    public void add(HighlightView hv) {
        mHighlightViews.add(hv);
        invalidate();
    }
}
