package vn.mbm.phimp.me.gallery;

import java.util.ArrayList;

import vn.mbm.phimp.me.R;
import vn.mbm.phimp.me.SendFileActivity;
import vn.mbm.phimp.me.Upload;
import vn.mbm.phimp.me.gallery3d.media.CropImage;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Gallery;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

public class PhimpMeGallery extends Activity {
	private Gallery gallery;
	private static ArrayList<String> filePath;


	private GalleryImageAdapter galImageAdapter;
	private ImageButton btnShare;
	private ImageButton btnEdit;
	
	private ImageButton btnUpload;
	public static int position;
	public static View overscrollleft;
	public static View overscrollright;
	
	//private Context ctx;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.phimpmegallery);	
		//requestWindowFeature(Window.FEATURE_NO_TITLE);
	//	ctx = this;		
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
				Intent intent = new Intent();
				intent.setClass(PhimpMeGallery.this, CropImage.class);
				intent.putExtra("image-path", filePath.get(position));			
				intent.putExtra("aspectX", 0);
				intent.putExtra("aspectY", 0);
				intent.putExtra("scale", true);
				intent.putExtra("activityName", "PhimpMeGallery");
				startActivity(intent);
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