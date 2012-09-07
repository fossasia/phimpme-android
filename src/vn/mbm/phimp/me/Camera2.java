package vn.mbm.phimp.me;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import vn.mbm.phimp.me.image.CropImage;
import android.app.Activity;
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
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.Toast;

public class Camera2 extends Activity {
	private static final String TAG = "Camera";
	static Context ctx;
	public static Camera mCamera;
	public static Preview preview;
	public static Preview preview1;
	public static ImageButton buttonClick;
	ImageButton flash;
	ImageButton camera_switch;
	FrameLayout frame;	
	//** Called when the activity is first created. *//*
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.camera);
		if (PhimpMe.IdList.size() == 5) {PhimpMe.IdList.clear();PhimpMe.IdList.add(0);}
		PhimpMe.IdList.add(1);
		
		setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);		
		frame = ((FrameLayout) findViewById(R.id.preview));
		preview = new Preview(this);
		mCamera = Camera.open(0);
		
		preview.setCamera(mCamera);
		ctx = this;
		frame.addView(preview);						
		buttonClick = (ImageButton) findViewById(R.id.takephoto);
		buttonClick.setOnClickListener( new OnClickListener() {
			public void onClick(View v) {
				preview.camera.takePicture(shutterCallback, rawCallback, jpegCallback);
			}
		});
		
		camera_switch = (ImageButton)findViewById(R.id.switch_camera);
		camera_switch.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (Camera.getNumberOfCameras() <= 1){
					Toast.makeText(ctx, "Sorry ! Your device has one camera !",Toast.LENGTH_SHORT).show();	
				}else{
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
					    mCamera.setDisplayOrientation(90);            					     
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
						preview.switchCamera(mCamera);
						mCamera.startPreview();
					}
				}
				
			}
		});
		
		flash = (ImageButton)findViewById(R.id.flash);		
		flash.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//Log.e("Flash",preview.camera.getParameters().getFlashMode());
				try{
				Camera.Parameters parameters = preview.camera.getParameters();
				if (preview.camera.getParameters().getFlashMode().equals(Camera.Parameters.FLASH_MODE_OFF)){
					flash.setImageDrawable(getResources().getDrawable(R.drawable.thunder));
					parameters.setFlashMode(Camera.Parameters.FLASH_MODE_ON);
					preview.camera.setParameters(parameters);
				}else{
					flash.setImageDrawable(getResources().getDrawable(R.drawable.thunder_gray));
					parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
					preview.camera.setParameters(parameters);
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
			try {
				picture = String.format("/sdcard/phimp.me/take_photo/%d.jpg", System.currentTimeMillis());
				outStream = new FileOutputStream(picture);										
	            bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
	            int w = bmp.getWidth();
	            int h = bmp.getHeight();
	            Matrix mtx = new Matrix();
	            if (PhimpMe.camera_use == 0)
	            	mtx.postRotate(90);else mtx.postRotate(-90); 
	            // Rotating Bitmap	            
	            rotatedBMP = Bitmap.createBitmap(bmp, 0, 0, w, h, mtx, true);
	            rotatedBMP.compress(CompressFormat.JPEG, 100, outStream);
	            rotatedBMP.recycle();
	            bmp.recycle();
	            outStream.flush();
				outStream.close();	
				System.gc();
				Log.d(TAG, "onPictureTaken - wrote bytes: " + data.length);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
			}
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
	@Override
	protected void onResume(){
		super.onResume();
		PhimpMe.hideTabs();
		if (mCamera == null){
			mCamera = Camera.open();
		}
		preview.setCamera(mCamera);
		if (PhimpMe.IdList.size() == 5) {PhimpMe.IdList.clear();PhimpMe.IdList.add(0);}
		PhimpMe.IdList.add(1);
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
		PhimpMe.showTabs();
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
        	Camera.Parameters parameters = camera.getParameters();
        	parameters.setFocusMode(Camera.Parameters.FLASH_MODE_AUTO);
            camera.setParameters(parameters)        ;              
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
		} catch (IOException e) {
			e.printStackTrace();
		}
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        // Surface will be destroyed when we return, so stop the preview.
        // Because the CameraDevice object is not a shared resource, it's very
        // important to release it when the activity is paused.
        camera.stopPreview();
        camera = null;
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
    	if (PhimpMe.popupTabs.getVisibility() == ViewGroup.VISIBLE)
    		PhimpMe.hideTabs();
    	//else Camera2.buttonClick.performClick(); 
    	return true;
    	
    }
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
    	//camera.stopPreview();
    	Camera.Parameters parameters = camera.getParameters();        
        Camera.Size previewSize = getBestPreviewSize(w, h, parameters);        
        camera.setDisplayOrientation(90);            
        parameters.setPreviewSize(previewSize.width,previewSize.height);
        camera.startPreview();
    }

    @Override
    public void draw(Canvas canvas) {
    		super.draw(canvas);
    		Paint p= new Paint(Color.RED);
    		Log.d(TAG,"draw");
    		canvas.drawText("PREVIEW", canvas.getWidth()/2, canvas.getHeight()/2, p );
    }
}