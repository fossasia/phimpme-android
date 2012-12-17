package vn.mbm.phimp.me.gridview.adapter;

import java.util.ArrayList;
import java.util.HashMap;

import vn.mbm.phimp.me.PhimpMe;
import vn.mbm.phimp.me.gallery.PhimpMeGallery;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

public class LocalPhotosAdapter extends BaseAdapter {
	
	private Context context;
	private ArrayList<String> fileID;
	//private ArrayList<String> ID;
	private ArrayList<String> filepath;	
	LayoutInflater li;
	String TAG = "LOCAL GALLERY";
	HashMap<Integer, Matrix> mImageTransforms = new HashMap<Integer,Matrix>();
	Matrix mIdentityMatrix = new Matrix();
	public LocalPhotosAdapter(Context localContext, ArrayList<String> fileID, ArrayList<String> ID) {
		this.context = localContext;
		this.fileID = fileID;
		filepath = new ArrayList<String>();
		//this.ID = ID;
		convertURI2URL(ID);		
	}

	public int getCount() {
		return fileID.size();
	}

	public void removeItem() {
		fileID.clear();
		notifyDataSetChanged();
	}
	
	public Object getItem(int position) {
		return position;
	}

	public long getItemId(int position) {
		return position;
	}
	public void convertURI2URL(ArrayList<String> ID){
		String[] projection = {MediaStore.Images.Media._ID,MediaStore.Images.Media.DATA};			
        Cursor cursor = context.getContentResolver().query( MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection, // Which columns to return
                null,       // Return all rows
                null,
                MediaStore.Images.Media._ID+ " DESC");
        int columnPath = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        int columnID = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)        		;
        boolean stop = false;
        cursor.moveToFirst();
        int k = 0;
        while (!stop){
        	k++;
        	for (int i = 0; i < ID.size();  i++ ){
        		if (cursor.getString(columnID).equals(ID.get(i))) {
        				filepath.add(cursor.getString(columnPath));
        				cursor.moveToNext();break;
        			}
        	}
        	if (k == fileID.size()) stop = true;
        }
        cursor.close();
	}
	@SuppressWarnings("unchecked")
	public ArrayList<String> changePosition(int position){		
		ArrayList<String> tmpFile = (ArrayList<String>) filepath.clone();		
		if (position != 0){
			String tmp = tmpFile.get(0);
			tmpFile.set(0, tmpFile.get(position));
			tmpFile.set(position, tmp);
			}
			return tmpFile;
	}	
	public View getView(int position, View convertView, ViewGroup parent) {
        ImageView picturesView;        
        final int pos = position;
        if (convertView == null) {		
            picturesView = new ImageView(context);
            String url = fileID.get(position);	
            
            picturesView.setImageURI(Uri.withAppendedPath(
                    MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI, "" + url));
           
            try {
	            picturesView.setPadding(3,3,2,2);
	            picturesView.setScaleType(ScaleType.FIT_XY);	            
	            picturesView.setLayoutParams(new GridView.LayoutParams(PhimpMe.width, PhimpMe.height));	            
	            try{
	            }catch(NoSuchMethodError n){
	            	
	            }
	            
			} catch (Exception e) {

			}                   
        }
        else {
            picturesView = (ImageView)convertView;
        }        
        picturesView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(context, PhimpMeGallery.class);		
				Log.e("Size file Path",String.valueOf(filepath.size()));
				PhimpMeGallery.setFileList(filepath);//changePosition(pos));
				//PhimpMeGallery.gallery.setSelection(pos);
				intent.putExtra("index", pos);
				intent.putExtra("from", "local");
				((Activity) context).startActivity(intent);
				
			}
		});
        return picturesView;

    }

@SuppressWarnings("deprecation")
public static int getOrientation(Context context, int position) throws Exception {
	
	String[] projection = {MediaStore.Images.Media.DATA};
    Activity act=new Activity();
    
    Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
    		projection, null, null, MediaStore.Images.Media._ID+ " DESC");
    int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
    cursor.moveToPosition(position);
    act.startManagingCursor(cursor);

    String imagePath = cursor.getString(columnIndex);       
    ExifInterface exif=new ExifInterface(imagePath);
	int orientation=exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);		
	cursor.close();
	if(orientation==3) return 180;
	else if(orientation==6) return 90;
	else if(orientation==8) return 270;
	else  return 0;
	
   
}
}