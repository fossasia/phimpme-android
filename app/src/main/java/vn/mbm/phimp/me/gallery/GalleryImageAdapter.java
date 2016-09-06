package vn.mbm.phimp.me.gallery;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import vn.mbm.phimp.me.R;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;

public class GalleryImageAdapter extends BaseAdapter {

	private Activity context;

	private static ImageView imageView;

	
	private int screen_w;	
	//private int screen_h;
	private List<String> mFilePath;

	private static ViewHolder holder;
	//private Bitmap bm = null;
	@SuppressWarnings("deprecation")
	public GalleryImageAdapter(Activity context, List<String> filepath) {

		this.context = context;
		//this.plotsImages = plotsImages;
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		screen_w = display.getWidth();		
		mFilePath = filepath;		

	}

	
	public int getCount() {
		return mFilePath.size();
	}
	public void onChange(){
		mFilePath.remove(PhimpMeGallery.position);
		notifyDataSetChanged();
	}

	public Object getItem(int position) {
		return null;
	}


	public long getItemId(int position) {
		return 0;
	}


	public View getView(int position, View convertView, ViewGroup parent) {
		PhimpMeGallery.position = position;
		if (convertView == null) {

			holder = new ViewHolder();

			imageView = new ImageView(this.context);

			imageView.setPadding(3, 3, 3, 3);

			convertView = imageView;

			holder.imageView = imageView;

			convertView.setTag(holder);

		} else {

			holder = (ViewHolder) convertView.getTag();
		}
		
				
		try{
			holder.imageView.setImageBitmap(decodeSampledBitmapFromFile(context, mFilePath.get(position),  screen_w));
		}catch(IndexOutOfBoundsException e){
			holder.imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.image_not_found));
		}
		//holder.imageView.setImageBitmap(BitmapFactory.decodeResource(context.getResources(),R.drawable.image_not_found, null));
		//bm = null;
	    
		holder.imageView.setLayoutParams(new Gallery.LayoutParams(Gallery.LayoutParams.MATCH_PARENT, Gallery.LayoutParams.MATCH_PARENT));
		
		holder.imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
		
		return imageView;
		
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
	    		
	    Log.e("Width",String.valueOf(width));
	    System.gc();
	    int inSampleSize = 1;
	    
	    if (width > reqWidth) {	        
	            inSampleSize = Math.round((float)width / (float)reqWidth);	        
    }
    return inSampleSize;
	}
	public static Bitmap decodeSampledBitmapFromFile(Context ctx, String mfile, 
	        int reqWidth) {
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
	        	Log.e("dsfsdfds","sdffsdfds");
	        	if (rotate == 0) return BitmapFactory.decodeFileDescriptor(fs.getFD(), null, options);
	        	else {
	        	 matrix.postRotate(rotate);
	        	 
	        	 bm = BitmapFactory.decodeFileDescriptor(fs.getFD(), null, options);	        	
	        		 
	        	 return Bitmap.createBitmap(bm,0,0,bm.getWidth(),bm.getHeight(),matrix,true);	        	
	        	}
	        	}	       
	        else return BitmapFactory.decodeResource(ctx.getResources(),R.drawable.image_not_found, options);
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
	private static class ViewHolder {
		ImageView imageView;
	}

}
