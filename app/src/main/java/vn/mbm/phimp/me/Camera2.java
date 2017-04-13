package vn.mbm.phimp.me;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.hardware.Camera.Size;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.OrientationEventListener;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static android.hardware.Camera.Parameters.FLASH_MODE_AUTO;
import static android.hardware.Camera.Parameters.FLASH_MODE_ON;
import static vn.mbm.phimp.me.Camera2.FLASH_OFF;
import static vn.mbm.phimp.me.Camera2.FLASH_ON;
import static vn.mbm.phimp.me.Camera2.state;

import vn.mbm.phimp.me.gallery3d.media.CropImage;
import vn.mbm.phimp.me.utils.Utils;

import static vn.mbm.phimp.me.Preview.GRID_ENABLED;

public class Camera2 extends android.support.v4.app.Fragment {
	private static final String TAG = "Camera";
	static Context ctx;
	public static Camera mCamera;
	public static Preview preview;
	//ProgressBar progress;
	OrientationEventListener mOrientation;
	//public static Preview preview1;
	public static ImageButton buttonClick;
	ImageButton flash,grid_overlay_button;
	ImageButton camera_switch;
	FrameLayout frame;
	public int degrees;
	private String make;
	private String model;
	private String imei;
	LocationManager locationManager;
	private LocationListener locationListener;
	boolean portrait = true;
	double lat;
	double lon;
	int statusScreen = 0;
	View view;

	// Directions for Camera Button Orientations
	private final int NE = 45;
	private final int SE = 135;
	private final int SW = 225;
	private final int NW = 315;
	// Camera Icon IDs
	private final int FLASH_ON_ICON = R.drawable.ic_flash_on_white_24dp;
	private final int FLASH_AUTO_ICON = R.drawable.ic_flash_auto_white_24dp;
	private final int FLASH_OFF_ICON = R.drawable.ic_flash_off_white_24dp;
	private final int GRID_ON_ICON = R.drawable.ic_grid_on;
	private final int GRID_OFF_ICON = R.drawable.ic_grid_off;
	// Flag for flasher
	public static int state = 0;
	int camOrientation = 0;
	// States for Flash
	public static final int FLASH_ON = 0;
	public static final int FLASH_OFF = 1;
	public static final int FLASH_AUTO = 2;

	private boolean FLAG_CAPTURE_IN_PROGRESS = false;

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View decorView = getActivity().getWindow().getDecorView();
		int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
		decorView.setSystemUiVisibility(uiOptions);

		view=  inflater.inflate(R.layout.camera, container, false);
		setLayout();
		return view;
	}

	public Camera2() {
		super();

	}

	private boolean inRange(int SubjectValue, int High, int Low) {
		// In case of a 360
		SubjectValue =  (360 + (SubjectValue % 360)) % 360;
		if (High < Low) {
			return High <= SubjectValue && SubjectValue <= Low;
		} else {
			return High <= SubjectValue || SubjectValue <= Low;
		}
	}

	private void setFlashIcon() {
		switch (state) {
			case FLASH_OFF:
				flash.setImageResource(FLASH_OFF_ICON);
				break;
			case FLASH_AUTO:
				flash.setImageResource(FLASH_AUTO_ICON);
				break;
			default:
				flash.setImageResource(FLASH_ON_ICON);
				break;
		}
	}

	private void rotateIcons(int degrees) {
		flash.setRotation(degrees);
		camera_switch.setRotation(degrees);
		buttonClick.setRotation(degrees);
	}

	private void adjustIconPositions() {
		setFlashIcon();
		if (inRange(camOrientation, NW, NE)) {
			rotateIcons(0);
		} else if (inRange(camOrientation, NE, SE)) {
			rotateIcons(270);
		} else if (inRange(camOrientation, SE, SW)) {
			rotateIcons(180);
		} else if (inRange(camOrientation, SW, NW)) {
			rotateIcons(90);
		}
	}

	//** Called when the activity is first created. *//*
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (PhimpMe.IdList.size() == 5) {
			PhimpMe.IdList.clear();
			PhimpMe.IdList.add(0);
		}
		PhimpMe.IdList.add(3);
		//getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

		//Log.e("Orirention", String.valueOf(ctx.getResources().getConfiguration().orientation));
		mOrientation = new OrientationEventListener(getContext()) {
			@Override
			public void onOrientationChanged(int orientation) {
				camOrientation = orientation;
				adjustIconPositions();

				if (orientation >= 350) {
					degrees = 0;
				}
				else {
					degrees = orientation;
				}

				/*if (orientation >=90 && orientation < 180 && statusScreen == 0){
					statusScreen = 1;
					//setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
					preview.mCamera.stopPreview();
					setCameraDisplayOrientation((Activity) ctx, PhimpMe.camera_use, preview.mCamera);
					preview.requestLayout();
					preview.mSurfaceView.requestLayout();
					preview.mCamera.startPreview();
					//setLayout(R.layout.camera);
					if (PhimpMe.flashStatus == 0) flash.setImageDrawable(getResources().getDrawable(R.drawable.thunder_gray_r1));
					else if (PhimpMe.flashStatus == 1) flash.setImageDrawable(getResources().getDrawable(R.drawable.thunder_r1));
					else flash.setImageDrawable(getResources().getDrawable(R.drawable.thunder_a_r1));
					buttonClick.setImageDrawable(getResources().getDrawable(R.drawable.icon_camera_r1));
					camera_switch.setImageDrawable(getResources().getDrawable(R.drawable.camera_32_r1));

				}else
					if (orientation >=260 && orientation <= 340 && statusScreen == 0){
						statusScreen = 1;
						if (PhimpMe.flashStatus == 0) flash.setImageDrawable(getResources().getDrawable(R.drawable.thunder_gray));
						else if (PhimpMe.flashStatus == 1) flash.setImageDrawable(getResources().getDrawable(R.drawable.thunder));
						else flash.setImageDrawable(getResources().getDrawable(R.drawable.thunder_a));
						buttonClick.setImageDrawable(getResources().getDrawable(R.drawable.icon_camera));
						camera_switch.setImageDrawable(getResources().getDrawable(R.drawable.camera_32));
					}else
						if (statusScreen == 1){
						statusScreen = 0;
						if (PhimpMe.flashStatus == 0) flash.setImageDrawable(getResources().getDrawable(R.drawable.thunder_gray_r));
						else if (PhimpMe.flashStatus == 1) flash.setImageDrawable(getResources().getDrawable(R.drawable.thunder_r));
						else flash.setImageDrawable(getResources().getDrawable(R.drawable.thunder_a_r));
						buttonClick.setImageDrawable(getResources().getDrawable(R.drawable.icon_camera_r));
						camera_switch.setImageDrawable(getResources().getDrawable(R.drawable.camera_32_r));
				}*/
			}
		};
		mOrientation.enable();
		Log.d(TAG, "onCreate'd");
		//PhimpMe.hideTabs();
	}

	public void setLayout() {
//		setContentView(layout);
		frame = ((FrameLayout) view.findViewById(R.id.preview));
		preview = new Preview(getActivity());
		//setContentView(preview);
		locationListener = new LocationListener() {

			public void onStatusChanged(String provider, int status, Bundle extras) {

			}

			public void onProviderEnabled(String provider) {

			}

			public void onProviderDisabled(String provider) {

			}

			public void onLocationChanged(Location location) {

				// Camera2.this.gpsLocationReceived(location);
				try {
					if (ActivityCompat.checkSelfPermission(getActivity(),
							Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
							ActivityCompat.checkSelfPermission(getContext(),
									Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
							) {
						// TODO: Consider calling
						//    ActivityCompat#requestPermissions
						// here to request the missing permissions, and then overriding
						//   public void onRequestPermissionsResult(int requestCode, String[] permissions,
						//                                          int[] grantResults)
						// to handle the case where the user grants the permission. See the documentation
						// for ActivityCompat#requestPermissions for more details.
						return;
					}
				} catch (NullPointerException e) {
					return;
				}
				lat = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER).getLatitude();
				lon = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER).getLongitude();
			}

		};
		locationManager = (LocationManager)getActivity().getSystemService(Context.LOCATION_SERVICE);
		Criteria locationCritera = new Criteria();
		locationCritera.setAccuracy(Criteria.ACCURACY_COARSE);
		locationCritera.setAltitudeRequired(false);
		locationCritera.setBearingRequired(false);
		locationCritera.setCostAllowed(true);
		locationCritera.setPowerRequirement(Criteria.NO_REQUIREMENT);
		String providerName = locationManager.getBestProvider(locationCritera, true);

		if (providerName != null && locationManager.isProviderEnabled(providerName)) {
			locationManager.requestLocationUpdates(providerName, 20000, 100,Camera2.this.locationListener);
		} else {
			// Provider not enabled, prompt user to enable it
		}
		if(locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)!=null){

			lat = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER).getLatitude();
			lon = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER).getLongitude();
		}
		else if (locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)!=null){
			Log.d("TAG", "Inside NETWORK");
			lat = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER).getLatitude();
			lon = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER).getLongitude();
		}
		else{
			Log.d("TAG", "else +++++++ ");
			lat = -1;
			lon = -1;
		}
		try{
			mCamera = Camera.open();}catch(Exception e){
		}
		//mCamera.setDisplayOrientation(90);
		setCameraDisplayOrientation(getActivity(), 0, mCamera);
		preview.setCamera(mCamera);
		ctx = getActivity();
		//frame.setLayoutParams(layparam);
		//preview.setLayoutParams(layparam);
		frame.addView(preview);
		buttonClick = (ImageButton) view.findViewById(R.id.takephoto);
		buttonClick.bringToFront();
		buttonClick.setOnClickListener( new OnClickListener() {
			public void onClick(View v) {
				//progress = ProgressDialog.show(ctx, "", "");
				if (!FLAG_CAPTURE_IN_PROGRESS) {
					FLAG_CAPTURE_IN_PROGRESS = true;
					preview.mCamera.takePicture(shutterCallback, null, jpegCallback);
				}
			}
		});
		camera_switch = (ImageButton)view.findViewById(R.id.switch_camera);
		camera_switch.bringToFront();
		buttonClick.setImageResource(R.drawable.takepic);
		if (Camera.getNumberOfCameras() <=1 ) camera_switch.setVisibility(View.GONE);
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

			}
		});
		Camera.Parameters parameters = preview.mCamera.getParameters();
		preview.mCamera.setParameters(parameters);
		flash = (ImageButton)view.findViewById(R.id.flash);
		flash.bringToFront();
		flash.setImageResource(FLASH_ON_ICON);
		flash.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// Log.e("Flash",preview.camera.getParameters().getFlashMode());
				try {
					adjustIconPositions();
					// Get camera parameters
					Camera.Parameters parameters = preview.mCamera.getParameters();
					// Switch flash icon
					switch (state) {
						case FLASH_ON:
							state = FLASH_OFF;
							parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
							break;
						case FLASH_OFF:
							state = FLASH_AUTO;
							parameters.setFlashMode(Camera.Parameters.FLASH_MODE_AUTO);
							break;
						default:
							state = FLASH_ON;
							parameters.setFlashMode(Camera.Parameters.FLASH_MODE_ON);
							break;
					}
					preview.mCamera.setParameters(parameters);
				} catch (Exception e) {
					Log.e("Flash", e.getMessage());
				}
				//Log.e("Flash",preview.camera.getParameters().getSupportedFlashModes().get(0));
			}
		});
/*
		if (preview.getcamPreviewSize()!=null)
			frame.setLayoutParams(new FrameLayout.LayoutParams(preview.getcamPreviewSize().width,
					preview.getcamPreviewSize().height));*/
frame.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
		ViewGroup.LayoutParams.MATCH_PARENT));
/*
		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(1080,(1080*4)/3);
		lp.addRule(RelativeLayout.CENTER_IN_PARENT);
		frame.setLayoutParams(lp);
*/
		grid_overlay_button = (ImageButton)view.findViewById(R.id.grid_overlay);
		grid_overlay_button.bringToFront();
		if (GRID_ENABLED){
            grid_overlay_button.setImageResource(GRID_OFF_ICON);
        }else {
            grid_overlay_button.setImageResource(GRID_ON_ICON);
        }
		grid_overlay_button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!GRID_ENABLED){
                    grid_overlay_button.setImageResource(GRID_OFF_ICON);
                    GRID_ENABLED = true;
                    preview.invalidate();						///onDraw gets called when view refreshes
                }else {
                    grid_overlay_button.setImageResource(GRID_ON_ICON);
                    GRID_ENABLED = false;
                    preview.invalidate();
                }
            }
	    });
	}

	ShutterCallback shutterCallback = new ShutterCallback() {
		public void onShutter() {
			//Dialog(1000, progress);
			//preview.mCamera.stopPreview();
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
			//camera.startPreview();
			Log.e("Size",String.valueOf(data.length)) ;
			try {
				picture = Environment.getExternalStorageDirectory()+ String.format("/phimp.me/take_photo/%d.jpg", System.currentTimeMillis());
				outStream = new FileOutputStream(picture);
				bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
				int w = bmp.getWidth();
				int h = bmp.getHeight();
				Matrix mtx = new Matrix();
				if (PhimpMe.camera_use == 0)
				{
					Log.e("Degrees",String.valueOf(degrees));
					if (degrees >= 0 && degrees <=90)
					{
						Log.e("Degree",String.valueOf(degrees));
						mtx.postRotate(90);//else mtx.postRotate(-degrees);
					}
					else if (degrees > 90 && degrees <=180 ){
						mtx.postRotate(180);
					}
				}
				else {
					if (degrees <= 0 && degrees >= -90)
					{
						Log.e("Degree",String.valueOf(degrees));
						mtx.postRotate(-90);//else mtx.postRotate(-degrees);
					}
					else if (degrees < -90 && degrees >= -180 ){
						mtx.postRotate(-180);
					}
				}
				// Rotating Bitmap
				rotatedBMP = Bitmap.createBitmap(bmp, 0, 0, w, h, mtx, true);
				rotatedBMP.compress(CompressFormat.JPEG, 90, outStream);
				rotatedBMP.recycle();
				// Log.e("Width + Height","Width => "+ w+ "Height =>"+ h);
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
			Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
			File f = new File(picture);
			Uri contentUri = Uri.fromFile(f);
			mediaScanIntent.setData(contentUri);
			Camera2.ctx.sendBroadcast(mediaScanIntent);
			ExifInterface exif;
			try {
				Log.e("Exif data","Run");
				exif = new ExifInterface(picture);
				createExifData(exif,lat , lon);
				exif.saveAttributes();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://"
			//      + Environment.getExternalStorageDirectory())));
			Log.e("Camera2", "picture : "+picture);
			Intent _intent = new Intent();
			_intent.setClass(ctx, CropImage.class);
			_intent.putExtra("image-path", picture);
			_intent.putExtra("aspectX", 0);
			_intent.putExtra("aspectY", 0);
			/*_intent.putExtra("latitude",lat);
			_intent.putExtra("longtitude",lon);*/
			_intent.putExtra("scale", true);
			_intent.putExtra("activityName", "Camera2");

			//resetting capture progress flag
			FLAG_CAPTURE_IN_PROGRESS = false;

			startActivityForResult(_intent, 1);
			//progress.dismiss();

		}
	};
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
		make = android.os.Build.MANUFACTURER; // get the make of the device
		model = android.os.Build.MODEL; // get the model of the divice

		exif.setAttribute(ExifInterface.TAG_MAKE, make);
		TelephonyManager telephonyManager = (TelephonyManager)getActivity().getSystemService(Context.TELEPHONY_SERVICE);
		imei = telephonyManager.getDeviceId();
		exif.setAttribute(ExifInterface.TAG_MODEL, model+" - "+imei);

		exif.setAttribute(ExifInterface.TAG_DATETIME, (new Date(System.currentTimeMillis())).toString()); // set the date & time

		Log.d("TAG", "Information : lat ="+ lattude+"  lon ="+ longitude+"  make = "+make+"  model ="+ model+"  imei="+imei+" time ="+(new Date(System.currentTimeMillis())).toString());
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
	public void onResume(){
		super.onResume();
		//PhimpMe.hideTabs();
		//PhimpMe.hideAd();
		mOrientation.enable();
		try{
			mCamera = Camera.open(PhimpMe.camera_use);
		}catch(Exception e){}
		try{
			setCameraDisplayOrientation((Activity) ctx, PhimpMe.camera_use, mCamera);
		}catch(RuntimeException e){}
		preview.setCamera(mCamera);
	}
	@Override
	public void onPause(){
		super.onPause();
		mOrientation.disable();
	}

//	@Override
//	public boolean onKeyDown(int keycode, KeyEvent event)
//    {
//    	if (keycode == KeyEvent.KEYCODE_BACK){
////    		finish();
//    		Intent i=new Intent(getActivity(), PhimpMe.class);
//    		startActivity(i);
//
//    	}
//        return super.onKeyDown(keycode, event);
//    	//return true;
//    }


// ----------------------------------------------------------------------

/**
 * A simple wrapper around a Camera and a SurfaceView that renders a centered preview of the Camera
 * to the surface. We need to center the SurfaceView because not all devices have cameras that
 * support preview sizes at the same aspect ratio as the device's display.
 */
}
class Preview extends SurfaceView implements SurfaceHolder.Callback {
	private final String TAG = "Preview";

	SurfaceView mSurfaceView;
	SurfaceHolder mHolder;
	Size mPreviewSize;
	List<Size> mSupportedPreviewSizes;
	List<String> mSupportFocus;
	Camera mCamera;
	private static  final int FOCUS_AREA_SIZE= 300;
	Paint paint;
	public static boolean GRID_ENABLED = false;

	@SuppressWarnings("deprecation")
	Preview(Context context) {
		super(context);

		mSurfaceView = this;//new SurfaceView(context);
		//addView(mSurfaceView);
        //Set Touch Listener
		this.setWillNotDraw(false);
		mSurfaceView.setOnTouchListener(new OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        if (event.getAction() == MotionEvent.ACTION_DOWN) {
                            focusOnTouch(event);
                        }
                        return true;
                    }
                 });


		//setting paint values for drawing grid
		paint = new Paint();
		paint.setAntiAlias(true);
		paint.setStrokeWidth(3);
		paint.setStyle(Paint.Style.STROKE);
		paint.setColor(Color.argb(255, 255, 255, 255));

		// Install a SurfaceHolder.Callback so we get notified when the
		// underlying surface is created and destroyed.
		mHolder = mSurfaceView.getHolder();
		mHolder.addCallback(this);
		mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	}

	public void setCamera(Camera camera) {
		mCamera = camera;
		if (mCamera != null) {
			mSupportedPreviewSizes = mCamera.getParameters().getSupportedPreviewSizes();
			mSupportFocus = mCamera.getParameters().getSupportedFocusModes();
			// mCamera.setDisplayOrientation(90);
			requestLayout();
		}
	}


	@Override
	protected void onDraw(Canvas canvas)
	{
		if (GRID_ENABLED) {
			int screenWidth = Utils.getScreenWidth(getContext());
			int screenHeight = Utils.getScreenHeight(getContext());

			canvas.drawLine(2 * (screenWidth / 3), 0, 2 * (screenWidth / 3), screenHeight, paint);
			canvas.drawLine((screenWidth / 3), 0, (screenWidth / 3), screenHeight, paint);
			canvas.drawLine(0, 2 * (screenHeight / 3), screenWidth, 2 * (screenHeight / 3), paint);
			canvas.drawLine(0, (screenHeight / 3), screenWidth, (screenHeight / 3), paint);
		}
	}


	//Check if tap to focus supported and Set the focus on the Tapped Area
	private void focusOnTouch(MotionEvent event) {
            if (mCamera != null ) {

                Camera.Parameters parameters = mCamera.getParameters();
                if (parameters.getMaxNumMeteringAreas() > 0 && parameters.getSupportedFocusModes().contains("manual")){
                    Log.i(TAG,"meteringAreas Supported");
                    Rect rect = calculateFocusArea(event.getX(), event.getY());
                    parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
                    List<Camera.Area> meteringAreas = new ArrayList<Camera.Area>();
                    meteringAreas.add(new Camera.Area(rect, 800));
                    parameters.setFocusAreas(meteringAreas);

                    mCamera.setParameters(parameters);
                    mCamera.autoFocus(mAutoFocusTakePictureCallback);
                }else {
                    mCamera.autoFocus(mAutoFocusTakePictureCallback);
                }
            }
        }

        //Calculate the Focus Area
        private Rect calculateFocusArea(float x, float y) {
            int left = clamp(Float.valueOf((x / mSurfaceView.getWidth()) * 2000 - 1000).intValue(), FOCUS_AREA_SIZE);
            int top = clamp(Float.valueOf((y / mSurfaceView.getHeight()) * 2000 - 1000).intValue(), FOCUS_AREA_SIZE);

            return new Rect(left, top, left + FOCUS_AREA_SIZE, top + FOCUS_AREA_SIZE);
        }

        private int clamp(int touchCoordinateInCameraReper, int focusAreaSize) {
            int result;
            if (Math.abs(touchCoordinateInCameraReper)+focusAreaSize/2>1000){
                if (touchCoordinateInCameraReper>0){
                    result = 1000 - focusAreaSize/2;
                } else {
                    result = -1000 + focusAreaSize/2;
                }
            } else{
                result = touchCoordinateInCameraReper - focusAreaSize/2;
            }
            return result;
        }

        private Camera.AutoFocusCallback mAutoFocusTakePictureCallback = new Camera.AutoFocusCallback() {
            @Override
            public void onAutoFocus(boolean success, Camera camera) {
                if (success) {
                    // do something...
                    Log.i("tap_to_focus","success!");
                } else {
                    // do something...
                    Log.i("tap_to_focus","fail!");
                }
            }
        };

	public void switchCamera(Camera camera) {
		setCamera(camera);
		try {
			camera.setPreviewDisplay(mHolder);
		} catch (IOException exception) {
			Log.e(TAG, "IOException caused by setPreviewDisplay()", exception);
		}
		Camera.Parameters parameters = camera.getParameters();
		//parameters.setPreviewSize(mPreviewSize.width, mPreviewSize.height);
		List<Camera.Size> sizes = parameters.getSupportedPreviewSizes();
		Camera.Size cs = sizes.get(0);
		parameters.setPreviewSize(cs.width, cs.height);
		requestLayout();

		camera.setParameters(parameters);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// We purposely disregard child measurements because act as a
		// wrapper to a SurfaceView that centers the camera preview instead
		// of stretching it.
		final int width = resolveSize(getSuggestedMinimumWidth(), widthMeasureSpec);
		final int height = resolveSize(getSuggestedMinimumHeight(), heightMeasureSpec);
		setMeasuredDimension(width, height);

	}

	public Size getcamPreviewSize(){
		return mPreviewSize;
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		if (changed && this.mSurfaceView != null) {
			final View child = this.mSurfaceView;

			final int width = r - l;
			final int height = b - t;
			Log.e("Height",String.valueOf(height));
			int previewWidth = width;
			int previewHeight = height;
			if (mPreviewSize != null) {
				previewWidth = mPreviewSize.width;
				previewHeight = mPreviewSize.height;
			}

			// Center the child SurfaceView within the parent.
			if (width * previewHeight > height * previewWidth) {
				final int scaledChildWidth = previewWidth * height / previewHeight;
				Log.e("width",String.valueOf(previewWidth));
				Log.e("Height",String.valueOf(previewHeight));
				Log.e("scaled",String.valueOf(scaledChildWidth));/*
				layout((width - scaledChildWidth) / 2, 0,
						(width + scaledChildWidth) / 2, height);*/
				Log.e("Height",String.valueOf(height));
			} else {
				final int scaledChildHeight = previewHeight * width / previewWidth;
				Log.e("width",String.valueOf(previewWidth));
				Log.e("Height",String.valueOf(previewHeight));
				Log.e("scaled",String.valueOf(scaledChildHeight));
               /* child.layout(0, 0,
                        width, height);*//*
				layout(0, 0,
						width, height);*/
                layout(0, 0 ,previewWidth, previewHeight);
				Log.e("Height",String.valueOf(height));
			}
			//layout(0,0,1080,1440);
		}
	}

	public void surfaceCreated(SurfaceHolder holder) {
		// The Surface has been created, acquire the camera and tell it where
		// to draw.
		try {
			if (mCamera != null) {
				mCamera.setPreviewDisplay(holder);
			}
		} catch (IOException exception) {
			Log.e(TAG, "IOException caused by setPreviewDisplay()", exception);
		}
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		// Surface will be destroyed when we return, so stop the preview.
		if (mCamera != null) {
			mCamera.stopPreview();
			mCamera.release();
		}
	}


	private Size getOptimalPreviewSize(List<Size> sizes, int w, int h) {
		final double ASPECT_TOLERANCE = 0.1;
		double targetRatio = (double) w / h;
		if (sizes == null) return null;

		Size optimalSize = null;
		double minDiff = Double.MAX_VALUE;

		int targetHeight = h;

		// Try to find an size match aspect ratio and size
		for (Size size : sizes) {
			double ratio = (double) size.width / size.height;
			if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
			if (Math.abs(size.height - targetHeight) < minDiff) {
				optimalSize = size;
				minDiff = Math.abs(size.height - targetHeight);
			}
		}

		// Cannot find the one match the aspect ratio, ignore the requirement
		if (optimalSize == null) {
			minDiff = Double.MAX_VALUE;
			for (Size size : sizes) {
				if (Math.abs(size.height - targetHeight) < minDiff) {
					optimalSize = size;
					minDiff = Math.abs(size.height - targetHeight);
				}
			}
		}
		return optimalSize;
	}
boolean c = true;
	public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
		// Now that the size is known, set up the camera parameters and begin
		// the preview.
		Log.e("Surface","Change");
		Camera.Parameters parameters = mCamera.getParameters();
		switch (state) {
			case FLASH_ON:
				parameters.setFlashMode(FLASH_MODE_ON);
				break;
			case FLASH_OFF:
				parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
				break;
			default:
				parameters.setFlashMode(FLASH_MODE_AUTO);
				break;
		}

		try{
			mPreviewSize = getOptimalPreviewSize(mSupportedPreviewSizes, w, h);
			if (mSupportFocus.contains(Camera.Parameters.FOCUS_MODE_AUTO))
			{
				parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
			}
			parameters.setSceneMode(Camera.Parameters.SCENE_MODE_AUTO);
			parameters.setPreviewSize(mPreviewSize.width, mPreviewSize.height);

		}catch(Exception e){}
		requestLayout();
		mCamera.setParameters(parameters);
		mCamera.startPreview();
		if (c) {
			c = false;
			layout(0, 0, 1080, (mPreviewSize.height * 1080) / mPreviewSize.width);
		}

	}
}
