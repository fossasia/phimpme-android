package vn.mbm.phimp.me;

import java.io.File;
import java.util.Date;

import vn.mbm.phimp.me.gallery3d.media.CropImage;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class Camera extends Activity 
{
	public static File DataDirectory;
	protected ImageButton btnTakePhoto;
	protected ImageView imageView;
	protected TextView txtNoImage;
	private static String _path,_path_tmp;
	protected boolean _taken;
	public static String imagelist ="";
	protected static final String PHOTO_TAKEN	= "photo_taken";
	static Context ctx;
	@Override
    public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.take_photo);
		ctx = this;
		imageView = ( ImageView ) findViewById( R.id.image );  
		txtNoImage=(TextView)findViewById(R.id.txtNoImage);
        btnTakePhoto = ( ImageButton ) findViewById( R.id.btnTakePhoto );
        btnTakePhoto.setOnClickListener(new OnClickListener() {		
			@Override
			public void onClick(View v) {				
				//startCameraActivity();	
				
		    }			
		});
       
		
        Date d = new Date();
		String filename = "phimp.me_" + d.getTime() + ".jpg";
        _path = Environment.getExternalStorageDirectory() + "/phimp.me/take_photo/"+filename;
        Log.i("Danh","Path 1 :"+_path);
	}
	protected void startCameraActivity()
    {
    	Log.i("MakeMachine", "startCameraActivity()" );
    	File file = new File( _path );
    	Uri outputFileUri = Uri.fromFile( file );
    	Log.i("Danh","Path 2 :"+_path);
    	Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE );
    	intent.putExtra( MediaStore.EXTRA_OUTPUT, outputFileUri );    	
    	startActivityForResult( intent, 0 );
		
    }
	
	 @Override
	    protected void onActivityResult(int requestCode, int resultCode, Intent data) 
	    {	
	    	Log.i( "MakeMachine", "resultCode: " + resultCode );
	    	switch( requestCode )
	    	{
	    		
	    		case 0:
	    			if (resultCode == Activity.RESULT_OK) {
	    				onPhotoTaken();
		    			break;
	    			}
	    			
	    		case 1:
	    			if (resultCode == Activity.RESULT_OK) {						
						String saveUri = data.getStringExtra("saveUri");
						_path_tmp = _path = saveUri;
						Log.e("Danh","Image path after change effect :"+saveUri);
						_taken = true;
				    	
				    	BitmapFactory.Options options = new BitmapFactory.Options();
				        options.inSampleSize = 4;
				    	
				    	Bitmap bitmap = BitmapFactory.decodeFile( saveUri, options );
				    	
				    	Log.e("Danh", "Image size : "+bitmap.getWidth() + ",height :"+bitmap.getHeight());
				    	txtNoImage.setVisibility( View.GONE );
				    	imageView.setImageBitmap(bitmap);
						
					
					break;
	    			}
	    		
	    	}
	    }
	    
	    protected void onPhotoTaken()
	    {
	    	Log.d( "MakeMachine", "onPhotoTaken" );
	    	
	    	_taken = true;
	    	_path_tmp = _path;
	    	BitmapFactory.Options options = new BitmapFactory.Options();
	        options.inSampleSize = 4;
	    	
	    	Bitmap bitmap = BitmapFactory.decodeFile( _path, options );
	    	Log.e("Danh", "Image Path 3: "+_path);
	    	Log.e("Danh", "Image _path_tmp 4: "+_path_tmp);
	    	Log.e("Danh", "Image size : "+bitmap.getWidth() + ",height :"+bitmap.getHeight());
	    	txtNoImage.setVisibility( View.GONE );
	    	imageView.setImageBitmap(bitmap);
	    	imageView.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Intent _intent = new Intent();
					_intent.setClass(ctx, CropImage.class);
					_intent.putExtra("image-path", _path);			
					_intent.putExtra("aspectX", 0);
					_intent.putExtra("aspectY", 0);
					_intent.putExtra("scale", true);
					_intent.putExtra("activityName", "Camera");
					startActivityForResult(_intent, 1);
					
				}
			});
	    	
	    }
	   
	    @Override 
	    protected void onRestoreInstanceState( Bundle savedInstanceState){
	    	Log.i( "MakeMachine", "onRestoreInstanceState()");
	    	if( savedInstanceState.getBoolean( Camera.PHOTO_TAKEN ) ) {
	    		Log.e("Danh", "Image Path when restore instance: "+_path_tmp);	        	
	        	_taken = true;
	        	
	        	BitmapFactory.Options options = new BitmapFactory.Options();
	            options.inSampleSize = 4;
	        	
	        	Bitmap bitmap = BitmapFactory.decodeFile( _path_tmp, options );
	        	txtNoImage.setVisibility( View.GONE );
	        	imageView.setImageBitmap(bitmap);
	        	imageView.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						Intent _intent = new Intent();
						_intent.setClass(ctx, CropImage.class);
						_intent.putExtra("image-path", _path_tmp);			
						_intent.putExtra("aspectX", 0);
						_intent.putExtra("aspectY", 0);
						_intent.putExtra("scale", true);
						_intent.putExtra("activityName", "Camera");
						startActivityForResult(_intent, 1);
						
					}
				});
	        	
	    	}
	    }
	    
	    @Override
	    protected void onSaveInstanceState( Bundle outState ) {
	    	outState.putBoolean( Camera.PHOTO_TAKEN, _taken );
	    }
	    @Override
		public boolean onKeyDown(int keycode, KeyEvent event)
	    {
	    	if (keycode == KeyEvent.KEYCODE_BACK){
	    		AlertDialog.Builder alertbox = new AlertDialog.Builder(ctx);
	            alertbox.setMessage(getString(R.string.exit_message));
	            alertbox.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener()
	            {
	                @Override
	                public void onClick(DialogInterface dialog, int which)
	                {
	                	finish();
	                	System.exit(0);
	                	
	                }
	            });
	            alertbox.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener()
	            {
	                @Override
	                public void onClick(DialogInterface dialog, int which)
	                {
	                	//Resume to current process
	                }
	            });
	            alertbox.create().show();
	    	}  	
	        return super.onKeyDown(keycode, event);
	    }
}
