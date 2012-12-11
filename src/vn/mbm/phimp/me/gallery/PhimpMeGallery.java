package vn.mbm.phimp.me.gallery;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import vn.mbm.phimp.me.R;
import vn.mbm.phimp.me.SendFileActivity;
import vn.mbm.phimp.me.Upload;
import vn.mbm.phimp.me.gallery3d.media.CropImage;
import vn.mbm.phimp.me.utils.geoDegrees;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Gallery;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

public class PhimpMeGallery extends Activity {
	private Gallery gallery;
	private static ArrayList<String> filePath;


	private GalleryImageAdapter galImageAdapter;
	private ImageButton btnShare;
	private ImageButton btnEdit;
	private ImageButton btnZoom;
	private ImageButton btnUpload;
	public static int position;
	public static View overscrollleft;
	public static View overscrollright;
	public int index = 0;
	private static String longtitude="",latitude="",title="";
	//private Context ctx;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.phimpmegallery);	
		//requestWindowFeature(Window.FEATURE_NO_TITLE);
	//	ctx = this;	
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		Intent intent = getIntent();
		Bundle extract = intent.getExtras();		
		try{
		index = extract.getInt("index");
		}catch(Exception e){}
		setupUI();
		
	}
	
	private void setupUI() {	
		gallery = (Gallery) findViewById(R.id.gallery);			
		galImageAdapter = new GalleryImageAdapter(this, filePath);
		overscrollleft = (View)findViewById(R.id.overscroll_left);
		overscrollright = (View)findViewById(R.id.overscroll_right);
		RelativeLayout layout = (RelativeLayout)findViewById(R.id.btn);
		layout.bringToFront();
		gallery.setAdapter(galImageAdapter);
		gallery.setSelection(index);
		btnShare = (ImageButton)findViewById(R.id.btnShare);
		btnShare.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent=new Intent();
				intent.setClass(PhimpMeGallery.this, SendFileActivity.class);
				intent.putExtra("image-path", filePath.get(position));
				intent.putExtra("aspectX", 0);
				intent.putExtra("aspectY", 0);
				intent.putExtra("scale", true);
				intent.putExtra("activityName", "PhimpMeGallery");
				startActivityForResult(intent, 1);
				
			}
		});
		btnUpload = (ImageButton)findViewById(R.id.btnUpload);
		btnUpload.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				
				
				AlertDialog.Builder builder=new AlertDialog.Builder(PhimpMeGallery.this);
				builder.setMessage("This photo have been add to list upload photo !");
				builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {					
					@Override
					public void onClick(DialogInterface dialog, int which) {		
						Upload.imagelist+=filePath.get(position)+"#";
					}
				});
				builder.show();
			}
		});		
		btnEdit = (ImageButton)findViewById(R.id.btnEdit);
		btnEdit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//get image info
				 File f =  new File(filePath.get(position));				
				 /*ExifInterface exif_data = null;
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
				 longtitude=lo;
				 latitude=la;*/
				 title=f.getName();
				 
				Intent intent = new Intent();
				intent.setClass(PhimpMeGallery.this, CropImage.class);
				intent.putExtra("image-path", filePath.get(position));
				intent.putExtra("longtitude", longtitude);
				intent.putExtra("latitude", latitude);
				intent.putExtra("title", title);
				intent.putExtra("aspectX", 0);
				intent.putExtra("aspectY", 0);
				intent.putExtra("scale", true);
				intent.putExtra("activityName", "PhimpMeGallery");
				startActivity(intent);
			}
		});
		btnZoom=(ImageButton)findViewById(R.id.btnZoom);
		btnZoom.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				TouchImageView imageViewGallery = new TouchImageView(PhimpMeGallery.this);

					BitmapFactory.Options o2 = new BitmapFactory.Options();
					o2.inPurgeable = true;

					try
					{
						WindowManager wm = (WindowManager) PhimpMeGallery.this.getSystemService(Context.WINDOW_SERVICE);
						Display display = wm.getDefaultDisplay();
						@SuppressWarnings("deprecation")
						int screen_w = display.getWidth();		
						imageViewGallery.setImageBitmap(GalleryImageAdapter.decodeSampledBitmapFromFile(PhimpMeGallery.this, filePath.get(position),  screen_w));
						imageViewGallery.setMaxZoom(4f);
						
						//dialog show zoom photo
						Dialog d=new Dialog(PhimpMeGallery.this, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
						d.setContentView(imageViewGallery);
						d.setCanceledOnTouchOutside(true);
						d.show();
					}
					catch (Exception ex)
					{
						Log.e("Exception", ex.getLocalizedMessage());
					}
			}
		});
		
	}	
	public static void setFileList(ArrayList<String> file){
		//filePath.clear();
		filePath = file;
	}
	public void onBackPressed(){
		
		super.onBackPressed();
	}
	
}