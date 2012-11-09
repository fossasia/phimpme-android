package vn.mbm.phimp.me.utils.map;

import vn.mbm.phimp.me.utils.ImageUtil;
import vn.mbm.phimp.me.R;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.android.maps.OverlayItem;
import com.readystatesoftware.mapviewballoons.BalloonOverlayView;

public class CustomBalloonOverlayView<Item extends OverlayItem> extends BalloonOverlayView<CustomOverlayItem>{

	//private TextView title;
	//private TextView snippet;
	private ImageView image;
	
	public CustomBalloonOverlayView(Context context, int balloonBottomOffset) {
		super(context, balloonBottomOffset);
	}
	
	@Override
	protected void setupView(Context context, final ViewGroup parent) {
		Log.d("Danh","setupView function called");
		// inflate our custom layout into parent
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View v = inflater.inflate(R.layout.custom_balloon_overlay, parent);
		
		// setup our fields
		//title = (TextView) v.findViewById(R.id.balloon_item_title);
		//snippet = (TextView) v.findViewById(R.id.balloon_item_snippet);
		image = (ImageView) v.findViewById(R.id.balloon_item_image);

		// implement balloon close
		ImageView close = (ImageView) v.findViewById(R.id.balloon_close);
		close.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				parent.setVisibility(GONE);
			}
		});
		
	}

	@Override
	protected void setBalloonData(CustomOverlayItem item, ViewGroup parent) 
	{
		Log.d("Danh","setBalloonData function called");
		// map our custom item data to fields
		//title.setText(item.getTitle());
		//snippet.setText(item.getSnippet());
		
		// get remote image from network.
		// bitmap results would normally be cached, but this is good enough for demo purpose.
		image.setImageResource(R.drawable.icon);
		new FetchImageTask() { 
	        protected void onPostExecute(Bitmap result) {
	            if (result != null) {
	            	image.setImageBitmap(result);
	            }
	        }
	    }.execute(item.getImageURL());
		
	}

	private class FetchImageTask extends AsyncTask<String, Integer, Bitmap> {
	    @Override
	    protected Bitmap doInBackground(String... arg0) {
	    	Log.d("Danh","FetchImageTask function called");
	    	Bitmap b = null;	    	
	    	try{
	    		  	
		    	BitmapFactory.Options bfOpt = new BitmapFactory.Options();          
	        	bfOpt.inScaled = true;
	        	bfOpt.inSampleSize = 2;
	        	bfOpt.inPurgeable = true;				
	        	b = BitmapFactory.decodeFile(arg0[0], bfOpt);				
	        	b = ImageUtil.scaleCenterCrop(b, 100, 80);
	        	
	    	}
	    	catch(NullPointerException e){
	    		
	    	}
	    	return b;
	    	
	    }

	}

	
}
