package vn.mbm.phimp.me.image;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
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
import android.media.FaceDetector;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StatFs;
import android.provider.MediaStore;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.CountDownLatch;

import vn.mbm.phimp.me.ImagesFilter;
import vn.mbm.phimp.me.PhimpMe;
import vn.mbm.phimp.me.R;

//import vn.mbm.phimp.me.UploadMap;
//import android.media.MediaScannerConnection;
//import android.view.View.OnClickListener;

/**
 * The activity can crop specific region of interest from an image.
 */
public class CropImage extends MonitoredActivity 
{
	
	private static int brightnessValue = 0;
	private static float toDegree = 90;
	private static float fromDegree = 0;
    // These are various options can be specified in the intent.
    private Bitmap.CompressFormat mOutputFormat =
	Bitmap.CompressFormat.JPEG; // only used with mSaveUri
    private Uri mSaveUri = null;
    private int mAspectX, mAspectY;
    private boolean mCircleCrop = false;
    private final Handler mHandler = new Handler();

    // These options specifiy the output image size and whether we should
    // scale the output to fit it (or just crop it).
    private int mOutputX, mOutputY;
    private boolean mScale;
    private boolean mScaleUp = true;

    private boolean mDoFaceDetection = true;

    boolean mWaitingToPick; // Whether we are wait the user to pick a face.
    boolean mSaving;  // Whether the "save" button is already clicked.

    private static CropImageView mImageView;
    private ContentResolver mContentResolver;
    private Button btnSave;
    private static Bitmap mBitmap;
    private static Bitmap mBitmapSave;
    private static Bitmap mBitmapResize;
    private static Bitmap modifiedBitmap;
    private static Bitmap flippedImaged;
    HighlightView mCrop;

    private IImage mImage;
    
    private final int GET_POSITION_ON_MAP = 5;
    private String mImagePath;
    static int position ;
    private static boolean check=false;
   // private String activityName;
    ProgressDialog gpsloading;
    ImageButton btnUseMap;
    EditText txtPhotoTitle;	
	EditText txtLongtitude;
	EditText txtLatitude;
	EditText txtTags;
	static Context ctx;
	static String p[] = null;
    private String newpath;
    @Override
    public void onCreate(Bundle icicle) 
    {
    	try
    	{
    		super.onCreate(icicle);
    		mContentResolver = getContentResolver();
    		ctx = this;    		
    		Log.d("crop image","start");
    		requestWindowFeature(Window.FEATURE_NO_TITLE);
    		setContentView(R.layout.cropimage);
    		setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);		
    		btnSave=(Button)findViewById(R.id.save);
    		
    		txtPhotoTitle = (EditText) findViewById(R.id.txtUploadPhotoTitle);
    		
    		txtLongtitude = (EditText) findViewById(R.id.txtUploadPhotoLongtitude);
    		
    		txtLatitude = (EditText) findViewById(R.id.txtUploadPhotoLatitude);
    		
    		txtTags = (EditText) findViewById(R.id.txtUploadPhotoTags);
    		btnUseMap = (ImageButton) findViewById(R.id.btnUploadPhotoPutPos);
    		/*btnUseMap.setOnClickListener(new OnClickListener() {
    			
    			@Override
    			public void onClick(View v) 
    			{
    				Intent _itent = new Intent(ctx, UploadMap.class);
    				
    				_itent.putExtra("latitude", txtLatitude.getText().toString());
    				_itent.putExtra("longitude", txtLongtitude.getText().toString());
    				startActivityForResult(_itent, GET_POSITION_ON_MAP);
    			}
    		});*/
    		mImageView = (CropImageView) findViewById(R.id.image);
    		gpsloading = new ProgressDialog(ctx);
    		gpsloading.setCancelable(true);
    		gpsloading.setCanceledOnTouchOutside(false);
    		gpsloading.setTitle(getString(R.string.loading));
    		gpsloading.setMessage(getString(R.string.infor_upload_loading_current_position));
    		gpsloading.setIndeterminate(true);
    		Log.d("cropimage","cropimage running..");
    		//showStorageToast(this);
			Intent intent = getIntent();
			Bundle extras = intent.getExtras();
			if (extras != null) 
			{
				if (extras.getString("circleCrop") != null) 
				{
					mCircleCrop = true;
					mAspectX = 1;
					mAspectY = 1;
				}
				
				mImagePath = extras.getString("image-path");
				p = mImagePath.split(";");
				
				if (p.length == 2){
					JSONObject js = new JSONObject(p[1]);
					txtLatitude.setText(js.getString("lati"));
					txtLongtitude.setText(js.getString("logi"));
					txtPhotoTitle.setText(js.getString("name"));
					txtTags.setText(js.getString("tags"));
				}
				Log.d("path",mImagePath);
				//mSaveUri = getImageUri(mImagePath);
				Date date = new Date();
				Long longTime = new Long(date.getTime()/1000);
				newpath = PhimpMe.DataDirectory+"/PhimpMe_Photo_Effect"+"/tmp_"+longTime+".jpg";
				mSaveUri = getImageUri(newpath);
				Log.d("mSaveUri",mSaveUri.toString());
				Log.d("p[0]",p[0]);	
				
				BitmapFactory.Options options = new BitmapFactory.Options();
		        options.inSampleSize = 4;		    	
		        mBitmap = BitmapFactory.decodeFile( p[0], options );
		        if(mBitmap.getWidth() %2 != 0||mBitmap.getHeight() %2 != 0){
		        	//bitmap width , height must even
		        	Log.i("CropImage","mBitmap width or height not even");			        	
		        	mBitmap=Bitmap.createScaledBitmap(mBitmap, mBitmap.getWidth()*4, mBitmap.getHeight()*4, false);
	        				        	
		        }
				modifiedBitmap= flippedImaged = mBitmap;
				
				Log.i("CropImage", "mBitmap Width :"+mBitmap.getWidth()+" mBitmap Height : "+mBitmap.getHeight());
				mAspectX = extras.getInt("aspectX");
			    mAspectY = extras.getInt("aspectY");
			    mOutputX = extras.getInt("outputX");
			    mOutputY = extras.getInt("outputY");
			    mScale = extras.getBoolean("scale", true);
			    mScaleUp = extras.getBoolean("scaleUpIfNeeded", true);	
			    position = extras.getInt("position");
			}
			if (mBitmap == null) 
			{
			    Log.d("null", "finish!!!");
				setResult(RESULT_CANCELED);
			    finish();
			    return;
			}
		
			// Make UI fullscreen.
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

			findViewById(R.id.discard).setOnClickListener(
				new View.OnClickListener() 
				{
				    public void onClick(View v) 
				    {
				    	if (btnSave.getVisibility() != View.VISIBLE)
				    		{
				    		//mBitmapSave.recycle();
				    		//mBitmap.recycle();
				    		//mBitmapResize.recycle();
				    		//modifiedBitmap.recycle();
				    		//flippedImaged.recycle();
				    		}
				    		setResult(RESULT_CANCELED);
				    		//System.exit(0);
							finish();				    					    								
				    }
				});
		
			/*
			 * Thong - Add event for button rotate image
			 */
			findViewById(R.id.btnRotateLeftRight).setOnClickListener(
					new View.OnClickListener() {
					    public void onClick(View v) 
					    {
					    	check=true;
					    	try{
					    		doRotate(mImageView, fromDegree, toDegree);
								fromDegree = (toDegree == 360) ? 0 : toDegree;
								toDegree += 90;
								if (toDegree > 360) {
									toDegree = 90;
								}
								
					    	}catch(OutOfMemoryError o){
					    		o.printStackTrace();
					    	}
					    	
					    }
					});
			/*
			 * Thong - Add event for button rotate image - End
			 */
						
			/*
			 * Danh - Add event for button rotate top-down image
			 */
			findViewById(R.id.btnRotateTopDown).setOnClickListener(
					new View.OnClickListener() {
					    public void onClick(View v) 
					    {
					    	check=true;
					    	try{
					    		modifiedBitmap = doVerticalFlip(modifiedBitmap);
								flippedImaged = doVerticalFlip(flippedImaged);
								mImageView.setImageBitmap(changeBrightness(
										modifiedBitmap, brightnessValue));
								mBitmapSave = modifiedBitmap;
								
					    	}catch(OutOfMemoryError o){
					    		mBitmapResize=getResizedBitmap(mBitmap, (mBitmap.getHeight()/4), (mBitmap.getWidth()/4));
					    		//mBitmapResize=getResizedBitmap(p[0], (mBitmap.getHeight()/2), (mBitmap.getWidth()/2));
					    		modifiedBitmap=flippedImaged=mBitmapResize;
					    	}
					    	
					    }
					});
			/*
			 * Danh - Add event for button rotate top-down image - End
			 */
			/*
			 * Danh - Add event for button black white image effect
			 */
			findViewById(R.id.btnBlackAndWhite).setOnClickListener(
					new View.OnClickListener() {
					    public void onClick(View v) 
					    {
					    	check=true;
					    	try{

					    		modifiedBitmap = null;
								modifiedBitmap = ImagesFilter.convertToBW(flippedImaged);
								mImageView.setImageBitmap(changeBrightness(
										modifiedBitmap, brightnessValue));
								mBitmapSave = modifiedBitmap;
								
					    	}catch(OutOfMemoryError o){								

					    		mBitmapResize=getResizedBitmap(mBitmap, (mBitmap.getHeight()/4), (mBitmap.getWidth()/4));
					    		//mBitmapResize=getResizedBitmap(p[0], (mBitmap.getHeight()/2), (mBitmap.getWidth()/2));
					    		modifiedBitmap=flippedImaged=mBitmapResize;
				    		

					    	}
					    	
					    }
					});
			/*
			 * Danh - Add event for button black white image effect - End
			 */
			/*
			 * Danh - Add event for button Sepia image effect
			 */
			findViewById(R.id.btnSaphia).setOnClickListener(
					new View.OnClickListener() {
					    public void onClick(View v) 
					    {
					    	check=true;
					    	try{

					    		modifiedBitmap = null;
								modifiedBitmap = ImagesFilter.convertToSepia(flippedImaged);
								mImageView.setImageBitmap(changeBrightness(
										modifiedBitmap, brightnessValue));
								mBitmapSave = modifiedBitmap;
								
					    	}catch(OutOfMemoryError o){
					    		mBitmapResize=getResizedBitmap(mBitmap, (mBitmap.getHeight()/4), (mBitmap.getWidth()/4));
					    		//mBitmapResize=getResizedBitmap(p[0], (mBitmap.getHeight()/2), (mBitmap.getWidth()/2));
								modifiedBitmap=flippedImaged=mBitmapResize;

					    	}
					    
					    }
					});

			/*
			 * Danh - Add event for button Sepia image effect - End
			 */

            findViewById(R.id.btnalpha).setOnClickListener(
                    new View.OnClickListener() {
                        public void onClick(View v) {
                            check=true;
                            try {
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
                            check=true;
                            try {
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
                            check=true;
                            try {
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
                            check=true;
                            try {
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
                            check=true;
                            try {
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
                            check=true;
                            try {
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
                            check=true;
                            try {
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
			/*
			 * Danh - Add event for button Negative image effect
			 */
			findViewById(R.id.btnNegative).setOnClickListener(
					new View.OnClickListener() {
					    public void onClick(View v) 
					    {
					    	check=true;
					    	try{
					    		
					    		modifiedBitmap = null;
								modifiedBitmap = ImagesFilter.convertToNegative(flippedImaged);
								mImageView.setImageBitmap(changeBrightness(
										modifiedBitmap, brightnessValue));
								mBitmapSave = modifiedBitmap;
								
					    	}catch(OutOfMemoryError o){

					    		mBitmapResize=getResizedBitmap(mBitmap, (mBitmap.getHeight()/4), (mBitmap.getWidth()/4));
					    		//mBitmapResize=getResizedBitmap(p[0], (mBitmap.getHeight()/2), (mBitmap.getWidth()/2));
					    		modifiedBitmap=flippedImaged=mBitmapResize;

					    	}
					    	
					    }
					});
			/*
			 * Danh - Add event for button Negative image effect - End
			 */
			btnSave.setOnClickListener(
					new View.OnClickListener() {
					    public void onClick(View v)
					    {
					    	if(check==true){
					    		onSaveClicked();
					    		//finish();
						    	Intent intent=new Intent(ctx, PhimpMe.class);
						    	intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						    	startActivity(intent);
					    	}else{
					    		
					    		//finish();
					    		Intent intent=new Intent(ctx, PhimpMe.class);
						    	intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						    	startActivity(intent);
					    	}
					    	
					    }
				});			
			startFaceDetection();
	    }
    	catch (Exception e)
    	{
    	}
    }
    public static void ConvertToOriginal() {
    	try{

			doRotate(mImageView, 0, 0);
			fromDegree = 0;
			toDegree = 90;
			mImageView.setImageBitmap(mBitmap);
			modifiedBitmap = flippedImaged = mBitmap;
    	}catch(OutOfMemoryError o){

			doRotate(mImageView, 0, 0);
			fromDegree = 0;
			toDegree = 90;
			mImageView.setImageBitmap(mBitmapResize);
			modifiedBitmap = flippedImaged = mBitmapResize;
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
		Bitmap des = sampleBitmap.copy(Bitmap.Config.ARGB_8888, true);
		des = Bitmap.createBitmap(sampleBitmap, 0, 0, sampleBitmap.getWidth(), sampleBitmap.getHeight(),
				matrixVerticalFlip, true);
		return des;
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

    
    OnSeekBarChangeListener brightnessBarSeekListener = new OnSeekBarChangeListener() {

		public void onStopTrackingTouch(SeekBar seekBar) {
			// TODO Auto-generated method stub

		}

		public void onStartTrackingTouch(SeekBar seekBar) {
			// TODO Auto-generated method stub

		}

		public void onProgressChanged(SeekBar seekBar, int progress,boolean fromUser) {
			try{
				brightnessValue = progress;
				ColorMatrix sepiaMatrix = new ColorMatrix();
				float[] sepMat = { 1, 0, 0, 0, brightnessValue, 0, 1, 0, 0,
						brightnessValue, 0, 0, 1, 0, brightnessValue, 0, 0, 0, 1,
						brightnessValue };
				sepiaMatrix.set(sepMat);
				final ColorMatrixColorFilter colorFilter = new ColorMatrixColorFilter(
						sepiaMatrix);
				Bitmap rBitmap = mBitmap.copy(Bitmap.Config.ARGB_8888, true);
				Paint paint = new Paint();
				paint.setColorFilter(colorFilter);
				Canvas myCanvas = new Canvas(rBitmap);
				myCanvas.drawBitmap(rBitmap, 0, 0, paint);
				
				mImageView.setImageBitmap(rBitmap);
				
			}
			catch(OutOfMemoryError o){
				mBitmapResize=getResizedBitmap(mBitmap, (mBitmap.getHeight()/4), (mBitmap.getWidth()/4));
				brightnessValue = progress;
				ColorMatrix sepiaMatrix = new ColorMatrix();
				float[] sepMat = { 1, 0, 0, 0, brightnessValue, 0, 1, 0, 0,
						brightnessValue, 0, 0, 1, 0, brightnessValue, 0, 0, 0, 1,
						brightnessValue };
				sepiaMatrix.set(sepMat);
				final ColorMatrixColorFilter colorFilter = new ColorMatrixColorFilter(
						sepiaMatrix);
				Bitmap rBitmap = mBitmapResize.copy(Bitmap.Config.ARGB_8888, true);
				Paint paint = new Paint();
				paint.setColorFilter(colorFilter);
				Canvas myCanvas = new Canvas(rBitmap);
				myCanvas.drawBitmap(rBitmap, 0, 0, paint);
				
				mImageView.setImageBitmap(rBitmap);
			}
			btnSave.setVisibility(View.INVISIBLE);
		}
	};
    private Uri getImageUri(String path) 
    {
    	return Uri.fromFile(new File(path));
    }

    public Bitmap getBitmap(String path) {
	Uri uri = getImageUri(path);
	InputStream in = null;
	try {
	    in = mContentResolver.openInputStream(uri);
	    return BitmapFactory.decodeStream(in);
	} catch (FileNotFoundException e) {
	}
	return null;
    }

    @Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) 
	{
		switch (requestCode) 
		{			
			case GET_POSITION_ON_MAP:
			{
				if (resultCode == Activity.RESULT_OK)
				{
					Log.d("thong", "Upload: Result OK");
					
					int lat = data.getIntExtra("latitude", 0);
					int log = data.getIntExtra("longitude", 0);
					
					float _lat = (float) (lat / 1E6);
					float _log = (float) (log / 1E6);
					
					txtLatitude.setText(String.valueOf(_lat));
					
					txtLongtitude.setText(String.valueOf(_log));
				}
				break;
			}
		}
	}
    private void startFaceDetection() 
    {
    	if (isFinishing()) 
    	{
    		return;
    	}
    	if(mBitmap.getWidth()>1800){
    		mBitmapResize=getResizedBitmap(mBitmap, (mBitmap.getHeight()/2), (mBitmap.getWidth()/2));
        	mBitmap = mBitmapResize;
        	mImageView.setImageBitmapResetBase(mBitmap, true);
        	ConvertToOriginal();
    	}else
    	ConvertToOriginal();
    	
    	Util.startBackgroundJob(this, null, "Please wait\u2026", new Runnable() 
    		{
	    		public void run() 
	    		{
	    			final CountDownLatch latch = new CountDownLatch(1);
	    			final Bitmap b = (mImage != null) ? mImage.fullSizeBitmap(IImage.UNCONSTRAINED, 1024 * 1024) : mBitmap;
	    			mHandler.post(new Runnable() 
		    			{
		    				public void run() 
		    				{
		    					//vn.mbm.postmail.Util.WriteLog("d", "startFaceDetection()", "Runnable");
		    					if (b != mBitmap && b != null) 
		    					{
		    						mImageView.setImageBitmapResetBase(b, true);
		    						mBitmap.recycle();
		    						mBitmap = b;
		    					}
		    					if (mImageView.getScale() == 1F) 
		    					{
		    						mImageView.center(true, true);
		    					}
		    					latch.countDown();
		    				}
		    			});
	    			try 
	    			{
	    				latch.await();
	    			} 
	    			catch (InterruptedException e) 
	    			{
	    				throw new RuntimeException(e);
	    			}
	    			mRunFaceDetection.run();
	    		}
	    	}, mHandler);
    }
    private void onSaveClicked() 
    {
		// TODO this code needs to change to use the decode/crop/encode single
		// step api so that we don't require that the whole (possibly large)
		// bitmap doesn't have to be read into memory
    	if (mSaving) return;

    	if (mCrop == null) 
    	{
    		return;
    	}

    	mSaving = true;

    	Rect r = mCrop.getCropRect();

    	int width = r.width();
    	int height = r.height();

    	// If we are circle cropping, we want alpha channel, which is the
    	// third param here.
    	Bitmap croppedImage = null;
    	try
    	{
	    	croppedImage = Bitmap.createBitmap(width, height, mCircleCrop ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565);
	    	{
	    		Canvas canvas = new Canvas(croppedImage);
	    		Rect dstRect = new Rect(0, 0, width, height);
	    		canvas.drawBitmap(mBitmapSave, r, dstRect, null);
	    	}	    	
    	}
    	catch (OutOfMemoryError o) 
    	{
    		mBitmapSave.recycle();
    		croppedImage.recycle();
    		setResult(RESULT_CANCELED,new Intent());
    		finish();
		}

    	if (mCircleCrop) 
    	{
    		// OK, so what's all this about?
    		// Bitmaps are inherently rectangular but we want to return
    		// something that's basically a circle.  So we fill in the
    		// area around the circle with alpha.  Note the all important
    		// PortDuff.Mode.CLEAR.
    		Canvas c = new Canvas(croppedImage);
    		Path p = new Path();
    		p.addCircle(width / 2F, height / 2F, width / 2F, Path.Direction.CW);
    		c.clipPath(p, Region.Op.DIFFERENCE);
	    	c.drawColor(0x00000000, PorterDuff.Mode.CLEAR);
    	}

    	/* If the output is required to a specific size then scale or fill */
    	if (mOutputX != 0 && mOutputY != 0) 
    	{
    		if (mScale) 
    		{
    			/* Scale the image to the required dimensions */
    			Bitmap old = croppedImage;
    			croppedImage = Util.transform(new Matrix(), croppedImage, mOutputX, mOutputY, mScaleUp);
    			if (old != croppedImage) 
    			{
    				old.recycle();
    			}
    		} 
    		else 
    		{
    			try{
    				/* Don't scale the image crop it to the size requested.
    				 * Create an new image with the cropped image in the center and
    				 * the extra space filled.
    				 */

        			// Don't scale the image but instead fill it so it's the
        			// required dimension
        			Bitmap b = Bitmap.createBitmap(mOutputX, mOutputY, Bitmap.Config.RGB_565);
        			Canvas canvas = new Canvas(b);

        			Rect srcRect = mCrop.getCropRect();
        			Rect dstRect = new Rect(0, 0, mOutputX, mOutputY);

        			int dx = (srcRect.width() - dstRect.width()) / 2;
        			int dy = (srcRect.height() - dstRect.height()) / 2;

        			/* If the srcRect is too big, use the center part of it. */
        			srcRect.inset(Math.max(0, dx), Math.max(0, dy));

        			/* If the dstRect is too big, use the center part of it. */
        			dstRect.inset(Math.max(0, -dx), Math.max(0, -dy));

        			/* Draw the cropped bitmap in the center */
        			canvas.drawBitmap(mBitmapSave, srcRect, dstRect, null);

        			/* Set the cropped bitmap as the new bitmap */
        			croppedImage.recycle();
        			croppedImage = b;
        			
        			b.recycle();
    			}catch(OutOfMemoryError o){
    				o.printStackTrace();
    			}
				
    		}
    	}

    	// Return the cropped image directly or save it to the specified URI.
    	Bundle myExtras = getIntent().getExtras();
    	if (myExtras != null && (myExtras.getParcelable("data") != null || myExtras.getBoolean("return-data"))) 
    	{
    		Bundle extras = new Bundle();
    		extras.putParcelable("data", croppedImage);
    		setResult(RESULT_OK,(new Intent()).setAction("inline-data").putExtras(extras));
    		finish();
    		
    	} 
    	else 
    	{
    		final Bitmap b = croppedImage;
    		Util.startBackgroundJob(this, null,"Please wait ...", new Runnable() 
    		{
    			public void run() {
    				saveOutput(b);
    			}
    		}, mHandler);
    	}
    }
    private boolean isMediaScannerRunning() {
    	Cursor query = getContentResolver().query(MediaStore.getMediaScannerUri(),
                                                       new String[]{MediaStore.MEDIA_SCANNER_VOLUME}, null, null, null);
            if(query!=null){
            	if(query.moveToFirst()){
            		int columnIndex = query.getColumnIndex(MediaStore.MEDIA_SCANNER_VOLUME);
            		String volumeName = query.getString(columnIndex);
            		if(volumeName!=null){
            			return true;
            		}
            	}
            	query.close();
            }
            return false;
    }
    private void saveOutput(Bitmap croppedImage) 
    {
		if (mSaveUri != null) 
		{
		    OutputStream outputStream = null;
		    try 
		    {
				outputStream = mContentResolver.openOutputStream(mSaveUri);
				if (outputStream != null) 
				{
				    croppedImage.compress(mOutputFormat, 100, outputStream);
				}
		    } 
		    catch (IOException ex) 
		    {
		    } 
		    finally 
		    {
		    	mBitmapSave.recycle();
		    	croppedImage.recycle();
		    	Util.closeSilently(outputStream);
		    }
		    		
		}		                      
        sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, mSaveUri));
        sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                Uri.parse("file://" + Environment.getExternalStorageDirectory())));

		mBitmapSave.recycle();
		croppedImage.recycle();
		Intent intent = this.getIntent();
		while(isMediaScannerRunning())
		{
				new Thread(){
					public void run(){
						try{
						sleep(1000);
						}catch(Exception e)
						{e.printStackTrace();}
					}
				}.start();
			
		}
		intent.putExtra("Impath", mImagePath);
		intent.putExtra("saveUri",newpath);
		intent.putExtra("lati", txtLatitude.getText().toString());
		intent.putExtra("logi",txtLongtitude.getText().toString());
		intent.putExtra("tags",txtTags.getText().toString());
		intent.putExtra("name", txtPhotoTitle.getText().toString());		
		Log.i("Danh","Image path output save : "+mImagePath);
		setResult(RESULT_OK,intent);		
		finish();
    }

    @Override
    protected void onPause() {
	super.onPause();
    }

    @Override
    protected void onDestroy() 
    {
    	super.onDestroy();
    	
    	System.gc();
    }


    Runnable mRunFaceDetection = new Runnable() 
    {
    	float mScale = 1F;
    	Matrix mImageMatrix;
    	FaceDetector.Face[] mFaces = new FaceDetector.Face[3];
    	int mNumFaces;

    	// For each face, we create a HightlightView for it.
    	private void handleFace(FaceDetector.Face f) 
    	{
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
		    if (faceRect.left < 0) 
		    {
		    	faceRect.inset(-faceRect.left, -faceRect.left);
		    }
	
		    if (faceRect.top < 0)
		    {
		    	faceRect.inset(-faceRect.top, -faceRect.top);
		    }
	
		    if (faceRect.right > imageRect.right) 
		    {
		    	faceRect.inset(faceRect.right - imageRect.right, faceRect.right - imageRect.right);
		    }
	
		    if (faceRect.bottom > imageRect.bottom) 
		    {
		    	faceRect.inset(faceRect.bottom - imageRect.bottom, faceRect.bottom - imageRect.bottom);
		    }
	
		    hv.setup(mImageMatrix, imageRect, faceRect, mCircleCrop, mAspectX != 0 && mAspectY != 0);
		    mImageView.add(hv);
    	}

		private void makeDefault() 
		{
		    HighlightView hv = new HighlightView(mImageView);
	
		    int width = mBitmap.getWidth();
		    int height = mBitmap.getHeight();
			    
			
		    Rect imageRect = new Rect(0, 0, width, height);
	
		    // make the default size about 4/5 of the width or height
		    int cropWidth = Math.min(width, height) * 4 / 5;
		    int cropHeight = cropWidth;
		    
		    if (mAspectX != 0 && mAspectY != 0) 
		    {
		    	if (mAspectX > mAspectY) 
		    	{
		    		cropHeight = cropWidth * mAspectY / mAspectX;
		    	} 
		    	else 
		    	{
		    		cropWidth = cropHeight * mAspectX / mAspectY;
		    	}
		    }		    
		    int x = (width - cropWidth) / 2;
		    int y = (height - cropHeight) / 2;
		    RectF cropRect = new RectF(x, y, x + cropWidth, y + cropHeight);
		    hv.setup(mImageMatrix, imageRect, cropRect, mCircleCrop, mAspectX != 0 && mAspectY != 0);
		    mImageView.mHighlightViews.clear(); // Thong
		    mImageView.add(hv);
		}

		// Scale the image down for faster face detection.
		private Bitmap prepareBitmap() 
		{
		    if (mBitmap == null) 
		    {
		    	return null;
		    }
	
		    // 256 pixels wide is enough.
		    if (mBitmap.getWidth() > 256) 
		    {
		    	mScale = 256.0F / mBitmap.getWidth();
		    }
		    Matrix matrix = new Matrix();
		    matrix.setScale(mScale, mScale);
		    Bitmap faceBitmap = Bitmap.createBitmap(mBitmap, 0, 0, mBitmap.getWidth(), mBitmap.getHeight(), matrix, true);
		    return faceBitmap;
		}

		public void run() 
		{
		    mImageMatrix = mImageView.getImageMatrix();
		    Bitmap faceBitmap = prepareBitmap();
	
		    mScale = 1.0F / mScale;
		    if (faceBitmap != null && mDoFaceDetection) 
		    {
		    	FaceDetector detector = new FaceDetector(faceBitmap.getWidth(), faceBitmap.getHeight(), mFaces.length);
		    	mNumFaces = detector.findFaces(faceBitmap, mFaces);
		    }
	
		    if (faceBitmap != null && faceBitmap != mBitmap) 
		    {
		    	faceBitmap.recycle();
		    }
	
		    mHandler.post(new Runnable() 
		    {
				public void run() 
				{
				    mWaitingToPick = mNumFaces > 1;
				    if (mNumFaces > 0) 
				    {
						for (int i = 0; i < mNumFaces; i++) 
						{
						    handleFace(mFaces[i]);
						}
				    } 
				    else 
				    {
				    	makeDefault();
				    }
				    mImageView.invalidate();
				    if (mImageView.mHighlightViews.size() == 1) 
				    {
				    	mCrop = mImageView.mHighlightViews.get(0);
				    	mCrop.setFocus(true);
				    }
		
				    if (mNumFaces > 1) 
				    {
						Toast t = Toast.makeText(CropImage.this, "Multi face crop help", Toast.LENGTH_SHORT);
						t.show();
				    }
				}
		    });
		}
		
    };

    public static final int NO_STORAGE_ERROR = -1;
    public static final int CANNOT_STAT_ERROR = -2;

    public static void showStorageToast(Activity activity) 
    {
    	try
    	{
    		showStorageToast(activity, calculatePicturesRemaining());
    	}
    	catch (Exception e)
    	{
    	}
    }

    public static void showStorageToast(Activity activity, int remaining) 
    {
		String noStorageText = null;
	
		if (remaining == NO_STORAGE_ERROR) 
		{
		    String state = Environment.getExternalStorageState();
		    if (state == Environment.MEDIA_CHECKING) 
		    {
		    	noStorageText = "Preparing card";
		    } 
		    else 
		    {
		    	noStorageText = "No storage card";
		    }
		} 
		else if (remaining < 1) 
		{
		    noStorageText = "Not enough space";
		}
	
		if (noStorageText != null) 
		{
		}
    }

    public static int calculatePicturesRemaining() {
	try {
	    String storageDirectory =
		Environment.getExternalStorageDirectory().toString();
	    StatFs stat = new StatFs(storageDirectory);
	    float remaining = ((float) stat.getAvailableBlocks()
		    * (float) stat.getBlockSize()) / 400000F;
	    return (int) remaining;
	    //}
	} catch (Exception ex) {
	    // if we can't stat the filesystem then we don't know how many
	    // pictures are remaining.  it might be zero but just leave it
	    // blank since we really don't know.
	    return CANNOT_STAT_ERROR;
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
        
 
}


class CropImageView extends ImageViewTouchBase
{
    ArrayList<HighlightView> mHighlightViews = new ArrayList<HighlightView>();
    HighlightView mMotionHighlightView = null;
    float mLastX, mLastY;
    int mMotionEdge;

    private Context mContext;

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) 
    {
		super.onLayout(changed, left, top, right, bottom);
		if (mBitmapDisplayed.getBitmap() != null) 
		{
		    for (HighlightView hv : mHighlightViews) 
		    {
		    	hv.mMatrix.set(getImageMatrix());
		    	hv.invalidate();
		    	if (hv.mIsFocused) 
		    	{
		    		centerBasedOnHighlightView(hv);
		    	}
		    }
		}
    }

    public CropImageView(Context context, AttributeSet attrs) 
    {
    	super(context, attrs);
    	this.mContext = context;
    }

    @Override
    protected void zoomTo(float scale, float centerX, float centerY) 
    {
    	super.zoomTo(scale, centerX, centerY);
    	for (HighlightView hv : mHighlightViews) 
    	{
    		hv.mMatrix.set(getImageMatrix());
    		hv.invalidate();
    	}
    }

    @Override
    protected void zoomIn() 
    {
		super.zoomIn();
		for (HighlightView hv : mHighlightViews) 
		{
		    hv.mMatrix.set(getImageMatrix());
		    hv.invalidate();
		}
    }

    @Override
    protected void zoomOut() 
    {
		super.zoomOut();
		for (HighlightView hv : mHighlightViews) 
		{
		    hv.mMatrix.set(getImageMatrix());
		    hv.invalidate();
		}
    }

    @Override
    protected void postTranslate(float deltaX, float deltaY) 
    {
		super.postTranslate(deltaX, deltaY);
		for (int i = 0; i < mHighlightViews.size(); i++) 
		{
		    HighlightView hv = mHighlightViews.get(i);
		    hv.mMatrix.postTranslate(deltaX, deltaY);
		    hv.invalidate();
		}
    }

    // According to the event's position, change the focus to the first
    // hitting cropping rectangle.
    private void recomputeFocus(MotionEvent event) 
    {
		for (int i = 0; i < mHighlightViews.size(); i++) 
		{
		    HighlightView hv = mHighlightViews.get(i);
		    hv.setFocus(false);
		    hv.invalidate();
		}

		for (int i = 0; i < mHighlightViews.size(); i++) 
		{
		    HighlightView hv = mHighlightViews.get(i);
		    int edge = hv.getHit(event.getX(), event.getY());
		    if (edge != HighlightView.GROW_NONE) 
		    {
				if (!hv.hasFocus()) 
				{
				    hv.setFocus(true);
				    hv.invalidate();
				}
				break;
		    }
		}
		invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) 
    {
    	CropImage cropImage = (CropImage) mContext;
		if (cropImage.mSaving)
		{
		    return false;
		}

		switch (event.getAction()) 
		{
			case MotionEvent.ACTION_DOWN:
			    if (cropImage.mWaitingToPick) 
			    {
			    	recomputeFocus(event);
			    } 
			    else 
			    {
					for (int i = 0; i < mHighlightViews.size(); i++) 
					{
					    HighlightView hv = mHighlightViews.get(i);
					    int edge = hv.getHit(event.getX(), event.getY());
					    if (edge != HighlightView.GROW_NONE) 
					    {
							mMotionEdge = edge;
							mMotionHighlightView = hv;
							mLastX = event.getX();
							mLastY = event.getY();
							mMotionHighlightView.setMode(
								(edge == HighlightView.MOVE)
								? HighlightView.ModifyMode.Move
									: HighlightView.ModifyMode.Grow);
							break;
					    }
					}
			    }
			    break;
			case MotionEvent.ACTION_UP:
			    if (cropImage.mWaitingToPick) 
			    {
					for (int i = 0; i < mHighlightViews.size(); i++) 
					{
					    HighlightView hv = mHighlightViews.get(i);
					    if (hv.hasFocus()) 
					    {
					    	cropImage.mCrop = hv;
							for (int j = 0; j < mHighlightViews.size(); j++) 
							{
							    if (j == i) 
							    {
							    	continue;
							    }
							    mHighlightViews.get(j).setHidden(true);
							}
							centerBasedOnHighlightView(hv);
							((CropImage) mContext).mWaitingToPick = false;
							return true;
					    }
					}
			    } 
			    else if (mMotionHighlightView != null) 
			    {
			    	centerBasedOnHighlightView(mMotionHighlightView);
			    	mMotionHighlightView.setMode(HighlightView.ModifyMode.None);
			    }
			    mMotionHighlightView = null;
			    break;
			case MotionEvent.ACTION_MOVE:
			    if (cropImage.mWaitingToPick) 
			    {
			    	recomputeFocus(event);
			    } 
			    else if (mMotionHighlightView != null) 
			    {
			    	mMotionHighlightView.handleMotion(mMotionEdge,
			    			event.getX() - mLastX,
			    			event.getY() - mLastY);
			    	mLastX = event.getX();
			    	mLastY = event.getY();

			    	if (true)
			    	{
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
		switch (event.getAction()) 
		{
			case MotionEvent.ACTION_UP:
				center(true, true);
				break;
			case MotionEvent.ACTION_MOVE:
			    // if we're not zoomed then there's no point in even allowing
			    // the user to move the image around.  This call to center puts
			    // it back to the normalized location (with false meaning don't
			    // animate).
				if (getScale() == 1F) 
				{
					center(true, true);
				}
				break;
		}
		return true;
    }

    // Pan the displayed image to make sure the cropping rectangle is visible.
    private void ensureVisible(HighlightView hv) 
    {
		Rect r = hv.mDrawRect;
	
		int panDeltaX1 = Math.max(0, mLeft - r.left);
		int panDeltaX2 = Math.min(0, mRight - r.right);
	
		int panDeltaY1 = Math.max(0, mTop - r.top);
		int panDeltaY2 = Math.min(0, mBottom - r.bottom);
	
		int panDeltaX = panDeltaX1 != 0 ? panDeltaX1 : panDeltaX2;
		int panDeltaY = panDeltaY1 != 0 ? panDeltaY1 : panDeltaY2;
	
		if (panDeltaX != 0 || panDeltaY != 0) 
		{
		    panBy(panDeltaX, panDeltaY);
		}
    }

    // If the cropping rectangle's size changed significantly, change the
    // view's center and scale according to the cropping rectangle.
    private void centerBasedOnHighlightView(HighlightView hv) 
    {
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
		if ((Math.abs(zoom - getScale()) / zoom) > .1) 
		{
		    float [] coordinates = new float[] { hv.mCropRect.centerX(), hv.mCropRect.centerY()};
		    getImageMatrix().mapPoints(coordinates);

			// Disabling zoom as it causes disappearance of image
			//zoomTo(zoom, coordinates[0], coordinates[1], 300F);
		}
		ensureVisible(hv);
    }

    @Override
    protected void onDraw(Canvas canvas) 
    {
		try
		{
	    	super.onDraw(canvas);
	    	for (int i = 0; i < mHighlightViews.size(); i++) 
	    	{
	    		mHighlightViews.get(i).draw(canvas);
	    	}
		}
		catch(Exception ce)
		{
		}
    }

    public void add(HighlightView hv) 
    {
		mHighlightViews.add(hv);
		invalidate();
    }

	@Override
	protected void onMeasure(int widthM, int heightM)
	{
		super.onMeasure(widthM, heightM);
		int width = getMeasuredWidth();
		setMeasuredDimension(width, width);
	}
}
