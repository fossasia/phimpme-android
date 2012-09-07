package vn.mbm.phimp.me;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.ItemizedIconOverlay.OnItemGestureListener;
import org.osmdroid.views.overlay.MyLocationOverlay;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.ScaleBarOverlay;

import vn.mbm.phimp.me.utils.geoDegrees;
import vn.mbm.phimp.me.ultils.osmap.CustomOverlayItem;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.Toast;


public class OpenStreetMap extends Activity{
	protected static final int GONE = 8;
	private MapView myOpenMapView;
	private MapController myMapController;
	static ProgressDialog progLoading;
	//private ImageView image;
	MapView map;
	MapController mc;
	Context ctx;
	Activity acti;
	ArrayList<OverlayItem> anotherOverlayItemArray;
	MyLocationOverlay myLocationOverlay = null;
	static String path;

	ImageButton btnS;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.open_street_map);
        try{
        acti = this;
        myOpenMapView = (MapView)findViewById(R.id.openmapview);
        myOpenMapView.setBuiltInZoomControls(true);
        myOpenMapView.setMultiTouchControls(true);
        myMapController = myOpenMapView.getController();        
        myMapController.setZoom(3);
        
        btnS = (ImageButton)findViewById(R.id.btnsw);
        btnS.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				acti.finish();
				Intent i = new Intent(acti, GalleryMap.class);
				startActivity(i);
			}
		});
       

            /*
             * Pin photo
             */
            		String[] projection = {MediaStore.Images.Media.DATA};
            		Cursor cursor = managedQuery( MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                            projection,
                            null,null,
                            null);
            		int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            		for(int i=0; i<cursor.getCount(); i++){
            			if(cursor.moveToNext())
            				PhimpMe.filepath.add(cursor.getString(columnIndex));
            		}						
            		try{
            			for(int k=0; k<PhimpMe.filepath.size(); k++){
                			String tmp[] = PhimpMe.filepath.get(k).split("/");
                			for(int t=0; t<tmp.length;t++){
                				if(tmp[t].equals("phimp.me")){					
                					PhimpMe.filepath.remove(k);
                					k--;
                					break;
                				}
                			}
                		}
            		}
            		catch(NullPointerException e){
            			
            		}
            		int count=PhimpMe.filepath.size();
            		Log.d("OpenStreetMap", "number local image :"+count);
            		int num_photos_added = 0;
    				if(count>0){
            	        
            	        for(int i=0; i<PhimpMe.filepath.size(); i++){
            	        String imagePath=PhimpMe.filepath.get(i);

    				
        	                Log.d("OpenStreetMap", "gallery map path photos index :"+i+imagePath);
        	                File f =  new File(imagePath);
        	    	        ExifInterface exif_data = null;
        	    			 geoDegrees _g = null;
        	    			 try 
        	    			 {
        	    				 exif_data = new ExifInterface(f.getAbsolutePath());
        	    				 _g = new geoDegrees(exif_data);
        	    				 if (_g.isValid())
        	    				 {
        	    					 
        	    					 try
        	         				{    	
        	    						 
        	    						 String la = _g.getLatitude() + "";
    	    	    					 String lo = _g.getLongitude() + "";
    	    	    					 int _latitude = (int) (Float.parseFloat(la) * 1000000);
    	    	        				 int _longitude = (int) (Float.parseFloat(lo) * 1000000);
    	    	    					 Log.d("OpenStreetMap ", "Longtitude :" +_longitude +" Latitude :"+_latitude);
        	         					 if ((_latitude != 0) && (_longitude != 0))
        	         					 {
        	         						GeoPoint _gp = new GeoPoint(_latitude, _longitude);
        	         						CustomOverlayItem _item = new CustomOverlayItem(_gp, imagePath,imagePath );
        	         						anotherOverlayItemArray = new ArrayList<OverlayItem>();
        	         				        anotherOverlayItemArray.add(_item);
        	         				       ItemizedIconOverlay<OverlayItem> anotherItemizedIconOverlay 
        	         			        	= new ItemizedIconOverlay<OverlayItem>(
        	         			        			this, anotherOverlayItemArray, myOnItemGestureListener);     			        
        	         	
        	         						myOpenMapView.getOverlays().add(anotherItemizedIconOverlay);
        	         						
        	         						PhimpMe.filepath.remove(i);
        	         						num_photos_added++;
        	         					 }
        	         				}
        	         				catch (Exception e) 
        	         				{
        	 							e.printStackTrace();
        	 						}
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
        				}
            	        
    			}
    				Toast.makeText(OpenStreetMap.this, 
    						num_photos_added +" photos has been displayed on map", 
    						Toast.LENGTH_LONG).show();	
         	
            
                
            //Add Scale Bar
            ScaleBarOverlay myScaleBarOverlay = new ScaleBarOverlay(this);
            myOpenMapView.getOverlays().add(myScaleBarOverlay);
            
            //Add MyLocationOverlay
            myLocationOverlay = new MyLocationOverlay(this, myOpenMapView);
            myOpenMapView.getOverlays().add(myLocationOverlay);
            myOpenMapView.postInvalidate();
                  
        }catch(UnsupportedOperationException u){
        	AlertDialog.Builder alert_bug = new AlertDialog.Builder(ctx);
        	alert_bug.setTitle("Error!");
        	alert_bug.setMessage("Sorry! Your device not support this service!");
        	alert_bug.show();
        }
        
        
    }
    public Location getLastLocation(Context ctx) {
        LocationManager locmgr = (LocationManager) ctx.getSystemService(Context.LOCATION_SERVICE);
        Location l = locmgr.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (l != null)
            return l;
        return locmgr.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
    }
    
    OnItemGestureListener<OverlayItem> myOnItemGestureListener
    = new OnItemGestureListener<OverlayItem>(){

		public boolean onItemLongPress(int arg0, OverlayItem arg1) {
			// TODO Auto-generated method stub
			return false;
		}

		public boolean onItemSingleTapUp( final int index, final OverlayItem item) {
			//setupView(ctx,myOpenMapView);	
			Toast.makeText(OpenStreetMap.this, 
					
					 "Latitude :"+ item.mGeoPoint.getLatitudeE6() + "\n"
					 + "Longtitude: " + item.mGeoPoint.getLongitudeE6(), 
					Toast.LENGTH_LONG).show();

	/*	image.setImageResource(R.drawable.icon);
			new FetchImageTask() { 
		        protected void onPostExecute(Bitmap result) {
		            if (result != null) {
		            	image.setImageBitmap(result);
		            }
		        }
		    }.execute(item.mDescription);*/
 
			return true;
			
		}
    	
    };
/*
    protected void setupView(Context context, final ViewGroup parent) {

    	LayoutInflater inflater = getLayoutInflater();    	
		View v = inflater.inflate(R.layout.custom_balloon_overlay, parent);
		image = (ImageView) v.findViewById(R.id.balloon_item_image);
		
		ImageView close = (ImageView) v.findViewById(R.id.balloon_close);
		close.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				onResume();
				v.setVisibility(GONE);				
				image.setVisibility(GONE);
				
			}
		});
		
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
	}*/
    @Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		myLocationOverlay.enableMyLocation();
		myLocationOverlay.enableCompass();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		finish();
		myLocationOverlay.disableMyLocation();
		myLocationOverlay.disableCompass();
	}
	


	

}
