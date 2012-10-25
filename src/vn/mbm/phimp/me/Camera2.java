package vn.mbm.phimp.me;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import vn.mbm.phimp.me.gallery3d.media.CropImage;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.OrientationEventListener;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class Camera2 extends Activity{
	private static final String TAG = "Camera";
	static Context ctx;
	public static Camera mCamera;
	public static Preview preview;
	OrientationEventListener mOrientation;
	//public static Preview preview1;
	public static ImageButton buttonClick;	
	ImageButton flash;
	ImageButton camera_switch;
	FrameLayout frame;	
	public int degrees;
	//** Called when the activity is first created. *//*
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.camera);
		
		//setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		//setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_BEHIND);
		frame = ((FrameLayout) findViewById(R.id.preview));	   
		preview = new Preview(this);				
	    
		try{
		mCamera = Camera.open();}catch(Exception e){
			mCamera.release();
			mCamera = Camera.open();
		}
		//mCamera.setDisplayOrientation(90);
		setCameraDisplayOrientation(this, 0, mCamera);
		preview.setCamera(mCamera);
		RelativeLayout.LayoutParams layoutparams = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);		
		layoutparams.addRule(RelativeLayout.ALIGN_PARENT_LEFT,RelativeLayout.TRUE);
		layoutparams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT,RelativeLayout.TRUE);
		layoutparams.addRule(RelativeLayout.ALIGN_PARENT_TOP,RelativeLayout.TRUE);
		layoutparams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM,RelativeLayout.TRUE);
		preview.setLayoutParams(layoutparams);
		ctx = this;
		frame.addView(preview);			
		buttonClick = (ImageButton) findViewById(R.id.takephoto);
		buttonClick.bringToFront();
		buttonClick.setOnClickListener( new OnClickListener() {
			public void onClick(View v) {
				preview.camera.takePicture(shutterCallback, rawCallback, jpegCallback);				
			}
		});	
		Log.e("Orirention", String.valueOf(ctx.getResources().getConfiguration().orientation));
		mOrientation = new OrientationEventListener(this) {
			
			@Override
			public void onOrientationChanged(int orientation) {
				// TODO Auto-generated method stub
			Log.e("Orientation","Change"+ String.valueOf(orientation));
			degrees = orientation;
				
			}
		};
		mOrientation.enable();
		camera_switch = (ImageButton)findViewById(R.id.switch_camera);
		if (Camera.getNumberOfCameras() <=1 ) camera_switch.setVisibility(View.GONE);
		camera_switch.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				/*if (Camera.getNumberOfCameras() <= 1){
					Toast.makeText(ctx, "Sorry ! Your device has one camera !",Toast.LENGTH_SHORT).show();	
				}else{*/
					if (PhimpMe.camera_use == 0) {
						Log.e("Camera", "0");						
						PhimpMe.camera_use = 1;
						if (mCamera != null) {
			                mCamera.stopPreview();
			                preview.setCamera(null);
			                mCamera.release();
			                mCamera = null;
			            }
						mCamera = Camera.open(1);																		  			            
					   // mCamera.setDisplayOrientation(90);
						setCameraDisplayOrientation((Activity) ctx, 1, mCamera);
					    preview.switchCamera(mCamera);
						mCamera.startPreview();
					}else
					{						
						Log.e("Camera", "0");						
						PhimpMe.camera_use = 0;
						if (mCamera != null) {
			                mCamera.stopPreview();
			                preview.setCamera(null);
			                mCamera.release();
			                mCamera = null;
			            }
						mCamera = Camera.open(0);

						//mCamera.setDisplayOrientation(90);
						setCameraDisplayOrientation((Activity) ctx, 0, mCamera);
						preview.switchCamera(mCamera);
						mCamera.startPreview();						
					}
				}
				
			//}
		});
		Camera.Parameters parameters = preview.camera.getParameters();
		parameters.setFlashMode(Camera.Parameters.FLASH_MODE_AUTO);
		preview.camera.setParameters(parameters);
		LinearLayout linear = (LinearLayout)findViewById(R.id.lnCam);
		linear.bringToFront();
		flash = (ImageButton)findViewById(R.id.flash);		
		
		flash.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//Log.e("Flash",preview.camera.getParameters().getFlashMode());
				try{
				Camera.Parameters parameters = preview.camera.getParameters();
				if (preview.camera.getParameters().getFlashMode().equals(Camera.Parameters.FLASH_MODE_OFF))
					PhimpMe.flashStatus = 0;
				else
				if (preview.camera.getParameters().getFlashMode().equals(Camera.Parameters.FLASH_MODE_ON))
					PhimpMe.flashStatus = 1;
				else PhimpMe.flashStatus = 2;
				if (PhimpMe.flashStatus == 0){
					flash.setImageDrawable(getResources().getDrawable(R.drawable.thunder));
					parameters.setFlashMode(Camera.Parameters.FLASH_MODE_ON);
					preview.camera.setParameters(parameters);
					PhimpMe.flashStatus = 1;
				}else if (PhimpMe.flashStatus == 1)
					{
					flash.setImageDrawable(getResources().getDrawable(R.drawable.thunder_a));
					parameters.setFlashMode(Camera.Parameters.FLASH_MODE_AUTO);
					preview.camera.setParameters(parameters);
					PhimpMe.flashStatus = 2;
				}else
				{
					flash.setImageDrawable(getResources().getDrawable(R.drawable.thunder_gray));
					parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
					preview.camera.setParameters(parameters);
					PhimpMe.flashStatus = 0;
				}
				Log.e("Flash",preview.camera.getParameters().getFlashMode());
				}catch(Exception e){}
				//Log.e("Flash",preview.camera.getParameters().getSupportedFlashModes().get(0));
				
			}
		});		
		
		Log.d(TAG, "onCreate'd");
		PhimpMe.hideTabs();
	}


	ShutterCallback shutterCallback = new ShutterCallback() {
		public void onShutter() {
			Log.d(TAG, "onShutter'd");
		}
	};

	//** Handles data for raw picture *//*
	PictureCallback rawCallback = new PictureCallback() {
		public void onPictureTaken(byte[] data, Camera camera) {
			Log.d(TAG, "onPictureTaken - raw");
		}
	};

	//** Handles data for jpeg picture *//*
	PictureCallback jpegCallback = new PictureCallback() {
		
		public void onPictureTaken(byte[] data, Camera camera) {
			
			FileOutputStream outStream = null;
			Bitmap rotatedBMP = null;
			String picture = "";
			Bitmap bmp =null;
			//ExifInterface exif;
			//if (mOrientation.canDetectOrientation()) mOrientation.enable();
			//mOrientation.disable();
			Log.e("Size",String.valueOf(data.length)) ;
			try {
				
				picture = String.format("/sdcard/phimp.me/take_photo/%d.jpg", System.currentTimeMillis());
				outStream = new FileOutputStream(picture);				
	            bmp = BitmapFactory.decodeByteArray(data, 0, data.length);	            	            
	            int w = bmp.getWidth();
	            int h = bmp.getHeight();
	            Matrix mtx = new Matrix();
	            if (PhimpMe.camera_use == 0)
	            	{
	            	Log.e("Degrees",String.valueOf(degrees));
	            	if (degrees < 180)
	            		mtx.postRotate(90);else mtx.postRotate(90+degrees);
	            	}
	            else {
	            	if (degrees > 180) mtx.postRotate(-90);else mtx.postRotate(-90-degrees);
	            } 
	            // Rotating Bitmap	      	            
	            rotatedBMP = Bitmap.createBitmap(bmp, 0, 0, w, h, mtx, true);
	            rotatedBMP.compress(CompressFormat.JPEG, 80, outStream);
	            rotatedBMP.recycle();
	            Log.e("Width + Height","Width => "+ w+ "Height =>"+ h);
	            bmp.recycle();
				//outStream.write(data);
	            outStream.flush();	            
				outStream.close();
				outStream = null;
				System.gc();				
				Log.d(TAG, "onPictureTaken - wrote bytes: " + data.length);
				//int orientation = getOrientation(degress);
				//Log.d(TAG, "Orientation: " + String.valueOf(orientation));					
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
			}
			
			sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://"
		            + Environment.getExternalStorageDirectory()))); 
			ProgressDialog progress=ProgressDialog.show(ctx, "", getString(R.string.wait));				
			Dialog(2500,progress);
			Log.e("Camera2", "picture : "+picture);						
			Intent _intent = new Intent();			
			_intent.setClass(ctx, CropImage.class);
			_intent.putExtra("image-path", picture);			
			_intent.putExtra("aspectX", 0);
			_intent.putExtra("aspectY", 0);
			_intent.putExtra("scale", true);
			_intent.putExtra("activityName", "Camera2");			
			startActivityForResult(_intent, 1);	
					
		}
	};	
	public static void setCameraDisplayOrientation(Activity activity,
	         int cameraId, android.hardware.Camera camera) {
	     android.hardware.Camera.CameraInfo info =
	             new android.hardware.Camera.CameraInfo();
	     android.hardware.Camera.getCameraInfo(cameraId, info);
	     int rotation = activity.getWindowManager().getDefaultDisplay()
	             .getRotation();
	     int degrees = 0;
	     switch (rotation) {
	         case Surface.ROTATION_0: degrees = 0; break;
	         case Surface.ROTATION_90: degrees = 90; break;
	         case Surface.ROTATION_180: degrees = 180; break;
	         case Surface.ROTATION_270: degrees = 270; break;
	     }

	     int result;
	     if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
	         result = (info.orientation + degrees) % 360;
	         result = (360 - result) % 360;  // compensate the mirror
	     } else {  // back-facing
	         result = (info.orientation - degrees + 360) % 360;
	     }
	     camera.setDisplayOrientation(result);
	 }
	public void Dialog(long time, final Dialog d){
	    new Handler().postDelayed(new Runnable() {
	        public void run() { 
	            d.dismiss();  
	            
	        }
	    }, time); 
	}
	/*public void onConfigurationChanged(Configuration newConfig) {
	    super.onConfigurationChanged(newConfig);
	    // Checks the orientation of the screen
	    setCameraDisplayOrientation((Activity)ctx, PhimpMe.camera_use, mCamera);
	    Display display = ((WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
	    //int rotation = display.getRotation();
	    //setRequestedOrientation(getRotation(rotation));
	    }*/
	public static int getRotation(int rotation){
		int result = 0;
		switch (rotation) {
		case Surface.ROTATION_0:
			result = ActivityInfo.SCREEN_ORIENTATION_BEHIND;
			break;
		case Surface.ROTATION_90:
			result = ActivityInfo.SCREEN_ORIENTATION_BEHIND;
			break;
		case Surface.ROTATION_180:
			result = ActivityInfo.SCREEN_ORIENTATION_BEHIND;
			break;
		default:
			result = ActivityInfo.SCREEN_ORIENTATION_BEHIND;
			break;
		}
		return result;
		
	}
	@Override
	protected void onResume(){
		super.onResume();		
		PhimpMe.hideTabs();
		PhimpMe.hideAd();			
		mOrientation.enable();
		if (mCamera == null){
			mCamera = Camera.open();
		}
		preview.setCamera(mCamera);
		if (PhimpMe.IdList.size() == 5) {PhimpMe.IdList.clear();PhimpMe.IdList.add(0);}
		PhimpMe.IdList.add(3);
	}
	@Override
	protected void onPause(){
		super.onPause();		
		mOrientation.disable();
	}
	
	/*@Override
	public boolean onKeyDown(int keycode, KeyEvent event)
    {
    	if (keycode == KeyEvent.KEYCODE_BACK){
    	
    		PhimpMe.IdList.remove(PhimpMe.IdList.size()-1);
    		PhimpMe.mTabHost.setCurrentTab(PhimpMe.IdList.get(PhimpMe.IdList.size()-1));
    		PhimpMe.showTabs();
    	}  	
        //return super.onKeyDown(keycode, event);
    	return true;
    }*/
	@Override
	public void onBackPressed(){		
		//PhimpMe.showTabs();		
		PhimpMe.IdList.remove(PhimpMe.IdList.size()-1);
		PhimpMe.mTabHost.setCurrentTab(PhimpMe.IdList.get(PhimpMe.IdList.size()-1));		
	}
		
}

class Preview extends SurfaceView implements SurfaceHolder.Callback {
	private static final String TAG = "Preview";

    SurfaceHolder mHolder;
    public Camera camera;
    public List<String> Support_Flash;    
	@SuppressWarnings("deprecation")
	Preview(Context context) {
        super(context);
        
        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.          
      
        mHolder = getHolder();
        mHolder.addCallback(this);
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }
	public void setCamera(Camera camera) {
        this.camera = camera;
        if (camera != null) {
            //mSupportedPreviewSizes = this.camera.getParameters().getSupportedPreviewSizes();        	        	
            requestLayout();
        }
    }
	public void switchCamera(Camera camera) {
	       setCamera(camera);
	       try {
	           camera.setPreviewDisplay(mHolder);
	       } catch (IOException exception) {
	           Log.e(TAG, "IOException caused by setPreviewDisplay()", exception);
	       }
	       
	       requestLayout();
	       
	    }
    public void surfaceCreated(SurfaceHolder holder) {
        // The Surface has been created, acquire the camera and tell it where
        // to draw.
    	//camera = Camera.open(cam_use);
    	
        try {
			camera.setPreviewDisplay(holder);									
		} catch (Exception e) {
			Camera2.mCamera.release();
			Camera2.mCamera = null;
			Camera2.mCamera = Camera.open(PhimpMe.camera_use);
			Camera2.mCamera.setDisplayOrientation(90);
			camera = null;
			camera = Camera2.mCamera;
			try {
				camera.setPreviewDisplay(holder);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			requestLayout();
			
		}
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        // Surface will be destroyed when we return, so stop the preview.
        // Because the CameraDevice object is not a shared resource, it's very
        // important to release it when the activity is paused.
    	try{
        camera.stopPreview();
    	}catch(Exception e){}
        camera = null;
    }
   /* @Override
    public boolean onTouchEvent(MotionEvent event) {
    	if (PhimpMe.popupTabs.getVisibility() == ViewGroup.VISIBLE)
    		PhimpMe.hideTabs();
    	//else Camera2.buttonClick.performClick(); 
    	return true;
    	
    }*/
    private Camera.Size getBestPreviewSize(int width, int height,
            Camera.Parameters parameters) {
			Camera.Size result = null;
			
			for (Camera.Size size : parameters.getSupportedPreviewSizes()) {
			if (size.width <= width && size.height <= height) {
			if (result == null) {
			result = size;
			}
			else {
			int resultArea=result.width * result.height;
			int newArea=size.width * size.height;
			
			if (newArea > resultArea) {
			result=size;
				}
			}
			}
				}

				return(result);
    }
   
    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        // Now that the size is known, set up the camera parameters and begin
        // the preview.    	
    	try{
    	camera.stopPreview();
    	Camera.Parameters parameters = camera.getParameters();        
        Camera.Size previewSize = getBestPreviewSize(w, h, parameters);   
        requestLayout();
        parameters.setPreviewSize(previewSize.width,previewSize.height);
        camera.startPreview();
    	}catch(NullPointerException e){}
    }

    @Override
    public void draw(Canvas canvas) {
    		super.draw(canvas);
    		Paint p= new Paint(Color.RED);
    		Log.d(TAG,"draw");
    		canvas.drawText("PREVIEW", canvas.getWidth()/2, canvas.getHeight()/2, p );
    }
}