package vn.mbm.phimp.me.gallery;

import java.io.File;
import java.util.ArrayList;

import vn.mbm.phimp.me.PhimpMe;
import vn.mbm.phimp.me.R;
import vn.mbm.phimp.me.SendFileActivity;
import vn.mbm.phimp.me.Upload;
import vn.mbm.phimp.me.gallery3d.media.CropImage;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.Gallery;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class PhimpMeGallery extends Activity {
	private Gallery gallery;
	private static ArrayList<String> filePath;


	private GalleryImageAdapter galImageAdapter;
	private ImageButton btnShare;
	private ImageButton btnEdit;
	private ImageButton btnZoom;
	private ImageButton btnUpload;
	ImageButton btnDelete;
	public static int position;
	public static View overscrollleft;
	public static View overscrollright;
	public int index = 0;
	public static int num;
	private static String longtitude="",latitude="",title="";
	private Context ctx;
	ExpandableListAdapter mAdapter;
	ExpandableListView expand;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.phimpmegallery);	
		//requestWindowFeature(Window.FEATURE_NO_TITLE);
		ctx = this;	
		num = filePath.size();
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		Intent intent = getIntent();
		Bundle extract = intent.getExtras();		
		try{
		index = extract.getInt("index");
		}catch(Exception e){}
		setupUI();
		
	}
	public class MyExpandableListAdapter extends BaseExpandableListAdapter {
        // Sample data set.  children[i] contains the children (String[]) for groups[i].
        private String[] groups = { getString(R.string.action) };
        private String[][] children = {
                { "Arnold", "Barry", "Chuck", "David" },              
        };
        
        public Object getChild(int groupPosition, int childPosition) {
            return children[groupPosition][childPosition];
        }

        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        public int getChildrenCount(int groupPosition) {
            return children[groupPosition].length;
        }

        public TextView getGenericView() {
            // Layout parameters for the ExpandableListView
            AbsListView.LayoutParams lp = new AbsListView.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, 64);

            TextView textView = new TextView(ctx);
            textView.setLayoutParams(lp);
            // Center the text vertically
            textView.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
            // Set the text starting position
            textView.setPadding(36, 0, 0, 0);
            return textView;
        }
        
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild,
                View convertView, ViewGroup parent) {
            TextView textView = getGenericView();
            textView.setText(getChild(groupPosition, childPosition).toString());
            return textView;
        }

        public Object getGroup(int groupPosition) {
            return groups[groupPosition];
        }

        public int getGroupCount() {
            return groups.length;
        }

        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        public View getGroupView(int groupPosition, boolean isExpanded, View convertView,
                ViewGroup parent) {
            TextView textView = getGenericView();
            textView.setText(getGroup(groupPosition).toString());
            return textView;
        }

        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }

        public boolean hasStableIds() {
            return true;
        }

    }
	private void setupUI() {	
		gallery = (Gallery) findViewById(R.id.gallery);			
		galImageAdapter = new GalleryImageAdapter(this, filePath);
		overscrollleft = (View)findViewById(R.id.overscroll_left);
		overscrollright = (View)findViewById(R.id.overscroll_right);
		RelativeLayout layout = (RelativeLayout)findViewById(R.id.btn);
		layout.bringToFront();
		expand = (ExpandableListView)findViewById(R.id.lstAction);
		mAdapter = new MyExpandableListAdapter();
		expand.setAdapter(mAdapter);
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
		btnDelete  = (ImageButton)findViewById(R.id.btnDelete);
		btnDelete.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				File f = new File(filePath.get(position));
				if (f.exists()){
					try{
						//f.delete();
						Log.e("file path",f.getAbsolutePath());
						//Log.e("Delete",String.valueOf(deleteImageFromMediaStore(f.getAbsolutePath())));
						deleteImageFromMediaStore(f.getAbsolutePath());	
						PhimpMe.gallery_delete = true;
						if (f.exists())f.delete();											
					}catch(Exception e){
						e.printStackTrace();
					}
				}
				filePath.remove(position);
				galImageAdapter.notifyDataSetChanged();
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
	public void deleteImageFromMediaStore(String path) throws Exception{		
		String[] projection = {MediaStore.Images.Media._ID,MediaStore.Images.Media.DATA};	
		String selection = "_data like ?";
        Cursor cursor =ctx.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, selection, new String[] {path}, null);
        cursor.moveToFirst();
        String id = cursor.getString(0);
        ctx.getContentResolver().delete(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection,new String[] {path});
        cursor = null;
        String[] proj = {MediaStore.Images.Thumbnails._ID, MediaStore.Images.Thumbnails.IMAGE_ID,MediaStore.Images.Thumbnails.DATA};
        cursor = ctx.getContentResolver().query(MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI, proj, "image_id = ?", new String[] {id}, null);
        Log.e("Gallery",String.valueOf(cursor.getCount()));
        if (cursor.getCount() >0 ){
        cursor.moveToFirst();        
        String thumb = cursor.getString(2);
        Log.e("Thumb",thumb);
        File f_thumb = new File(thumb);
        if (f_thumb.exists()) f_thumb.delete();         
        ctx.getContentResolver().delete(MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI, "image_id = ?", new String[] {id});
        }
        //result = cursor.getCount();
        cursor.close();
		//result = cursor.getCount();//ctx.getContentResolver().delete(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, MediaStore.Images.Media.DATA + " LIKE "+path +"", null);		
	}
	public static void setFileList(ArrayList<String> file){
		//filePath.clear();
		filePath = file;
	}
	public void onBackPressed(){
		
		 super.onBackPressed();
		
	}
	
}