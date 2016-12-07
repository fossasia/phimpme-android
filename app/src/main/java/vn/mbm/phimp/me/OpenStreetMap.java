package vn.mbm.phimp.me;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay.OnItemGestureListener;
import org.osmdroid.views.overlay.MyLocationOverlay;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.ScaleBarOverlay;

import vn.mbm.phimp.me.ultils.osmap.ExtendedOverlayItem;
import vn.mbm.phimp.me.ultils.osmap.ItemizedOverlayWithBubble;
import vn.mbm.phimp.me.utils.geoDegrees;
import vn.mbm.phimp.me.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;


public class OpenStreetMap extends Fragment{
	protected static final int GONE = 8;
	private MapView myOpenMapView;
	private MapController myMapController;
	static ProgressDialog progLoading;	
	private static final int TURN_ON_GPS = 1;
	//private ImageView image;
	MapView map;
	MapController mc;
	Context ctx;
	Activity acti;
	ArrayList<ExtendedOverlayItem> anotherOverlayItemArray;
	MyLocationOverlay myLocationOverlay = null;
	LocationManager lm;
	LocationListener ll;
	static String path = "";	
	static boolean check=false;
	protected ItemizedOverlayWithBubble<ExtendedOverlayItem> itineraryMarkers; 
	private GeoPoint currentLocation;

	ImageButton btnS;

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.open_street_map, container, false);
	}

	/** Called when the activity is first created. */
    @SuppressWarnings("deprecation")
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);        
        try{
        ctx = getContext();
        acti = getActivity();
//        setContentView(R.layout.open_street_map);
        /* location manager */
        currentLocation = null;        		
        try{
        	//Log.e("find location","dsfdsfdsf");
        	lm = (LocationManager) ctx.getSystemService(getActivity().LOCATION_SERVICE);
        	Criteria criteria = new Criteria();
			String provider = lm.getBestProvider(criteria, true);
			ll = new MyLocationListener();
			lm.requestLocationUpdates(provider, 0, 0, ll);
        }catch(Exception e){}
        if (!android.provider.Settings.Secure.getString(getActivity().getContentResolver(), android.provider.Settings.Secure.LOCATION_PROVIDERS_ALLOWED).contains("gps")){
        	getActivity().showDialog(TURN_ON_GPS);
        }
        check=false;
        //show only one photo into map
        Intent intent=getActivity().getIntent();
        Bundle extract=intent.getExtras();
        if(extract !=null){
        	Log.e("OpenStreetMap","check : "+check);
        	check=true;
        	path=extract.getString("image-path");
        }
        myOpenMapView = (MapView)getView().findViewById(R.id.openmapview);
        myOpenMapView.setBuiltInZoomControls(true);
        myOpenMapView.setMultiTouchControls(true);
        myMapController = myOpenMapView.getController();        
        myMapController.setZoom(16);        
        //if (currentLocation != null) myMapController.animateTo(currentLocation);
        btnS = (ImageButton)getView().findViewById(R.id.btnsw);
        btnS.bringToFront();
        btnS.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				Log.e("OpenStreetMap","check : "+check);
				if(check==true){
					acti.finish();
					Intent i = new Intent(acti, GalleryMap.class);
					i.putExtra("image-path", path);
					startActivity(i);
					
				}
				else{
//					PhimpMe.mTabHost.getTabWidget().getChildAt(1).setVisibility(View.GONE);
//					PhimpMe.mTabHost.getTabWidget().getChildAt(2).setVisibility(View.VISIBLE);
//					PhimpMe.mTabHost.setCurrentTab(2);
				}
				
			}
		});
     

            /*
             * Pin photo
             */
        new Thread(){
        	public void run(){
        		
        			//show only one photo into map

        		if(!path.equals("")){



        		Intent intent=getActivity().getIntent();
                Bundle extract=intent.getExtras();
        		if(extract !=null && extract.getString("image-path")!=null){
        			String path=extract.getString("image-path");




        			File f =  new File(path);
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
	         						ExtendedOverlayItem _item = new ExtendedOverlayItem(f.getName(),"",path,_gp,ctx);
	         						
	         						
	         						anotherOverlayItemArray = new ArrayList<ExtendedOverlayItem>();
	         				        anotherOverlayItemArray.add(_item);	      				       
	         				       ItemizedOverlayWithBubble<ExtendedOverlayItem> anotherItemizedIconOverlay  = 
	         				    		   new ItemizedOverlayWithBubble<ExtendedOverlayItem>(ctx,anotherOverlayItemArray,myOpenMapView);        	         	
	         						myOpenMapView.getOverlays().add(anotherItemizedIconOverlay);	         						
	         						myMapController.animateTo(_gp);
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
        		//show all photo in gallery
        		else{
        			Log.e("Show all","Openstreetmap" );
        			String[] projection = {MediaStore.Images.Media.DATA};
            		Cursor cursor = getActivity().managedQuery( MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                            projection,
                            null,null,
                            null);
            		int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            		for(int i=0; i<cursor.getCount(); i++){
            			if(cursor.moveToNext())
            				PhimpMe.filepath.add(cursor.getString(columnIndex));
            		}						
            		
            		int count=PhimpMe.filepath.size();
            		Log.d("OpenStreetMap", "number local image :"+count);
            		//int num_photos_added = 0;
    				if(count>0){
            	        int i ;
            	        for( i=0; i<PhimpMe.filepath.size(); i++){
            	        String imagePath =PhimpMe.filepath.get(i);

    				
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
        	         						ExtendedOverlayItem _item = new ExtendedOverlayItem(f.getName(),"",imagePath,_gp,ctx);
        	         						
        	         						
        	         						anotherOverlayItemArray = new ArrayList<ExtendedOverlayItem>();
        	         				        anotherOverlayItemArray.add(_item);
        	         				       /*ItemizedIconOverlay<OverlayItem> anotherItemizedIconOverlay 
        	         			        	= new ItemizedIconOverlay<OverlayItem>(
        	         			        			ctx, anotherOverlayItemArray, myOnItemGestureListener);     */
        	         				       ItemizedOverlayWithBubble<ExtendedOverlayItem> anotherItemizedIconOverlay  = 
        	         				    		   new ItemizedOverlayWithBubble<ExtendedOverlayItem>(ctx,anotherOverlayItemArray,myOpenMapView);;        	         	
        	         						myOpenMapView.getOverlays().add(anotherItemizedIconOverlay);
        	         						
        	         						PhimpMe.filepath.remove(i);
        	         						//num_photos_added++;
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
        		}
            		
        	}
        }.start();
    				/*Toast.makeText(OpenStreetMap.this, 
    						num_photos_added +" photos has been displayed on map", 
    						Toast.LENGTH_LONG).show();	*/
         	
            
                
            //Add Scale Bar
            ScaleBarOverlay myScaleBarOverlay = new ScaleBarOverlay(getContext());
            myOpenMapView.getOverlays().add(myScaleBarOverlay);
            
            //Add MyLocationOverlay
            myLocationOverlay = new MyLocationOverlay(getContext(), myOpenMapView);
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
			Toast.makeText(getContext(),
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
    
    protected Dialog onCreateDialog(int id)
	{
		switch (id)
		{			
			case TURN_ON_GPS:
			{
				return new AlertDialog.Builder(ctx)
					.setTitle("")
					.setMessage("Do you want turn on GPS ?")
					.setPositiveButton("Yes", new DialogInterface.OnClickListener() 
					{
						@Override
						public void onClick(DialogInterface dialog, int which) 
						{							
							
							startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
						}
					})
					.setNegativeButton("No", new DialogInterface.OnClickListener() 
					{
						@Override
						public void onClick(DialogInterface dialog, int which) 
						{
							
						}
					})
					.create();
			}
		}
		return null;
	}
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

	// TODO: may not be safe; was protected
	// ditto for onPause()
    @Override
	public void onResume() {
		// TODO Auto-generated method stub
    	try{
		super.onResume();
		PhimpMe.showTabs();
		if (PhimpMe.IdList.size() == 5) {PhimpMe.IdList.clear();PhimpMe.IdList.add(0);}
		PhimpMe.IdList.add(1);		
		myLocationOverlay.enableMyLocation();
		myLocationOverlay.enableCompass();
    	}catch(UnsupportedOperationException e){}
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		
		myLocationOverlay.disableMyLocation();
		myLocationOverlay.disableCompass();
	}

	public class MyLocationListener implements LocationListener
	{
		@Override
		public void onLocationChanged(Location location) 
		{
			Log.d("thong", "UploadMap > MyLocationListener > onLocationChanged");
			if (location != null)
			{
				Log.d("thong", "UploadMap > MyLocationListener > onLocationChanged > location not null");
				
				/*PhimpMe.curLatitude = location.getLatitude();
				PhimpMe.curLongtitude = location.getLongitude();*/
				
				currentLocation = new GeoPoint((int) (location.getLatitude() * 1000000), (int) (location.getLongitude() * 1000000));
				
				Log.e("latituade", "UploadMap > MyLocationListener > onLocationChanged > latitude: " + PhimpMe.curLatitude);
				Log.e("latituade", "UploadMap > MyLocationListener > onLocationChanged > longitude: " + PhimpMe.curLongtitude);				
				myMapController.animateTo(currentLocation);
				myMapController.setCenter(currentLocation);
				myOpenMapView.invalidate();
			}else {
				mc.setZoom(1);
			}
		}

		
		@Override
		public void onProviderDisabled(String provider) 
		{
			
		}
		
		@Override
		public void onProviderEnabled(String provider) 
		{
			
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) 
		{
			
		}
	}
}

