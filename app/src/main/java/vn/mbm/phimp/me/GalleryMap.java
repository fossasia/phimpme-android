package vn.mbm.phimp.me;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import vn.mbm.phimp.me.utils.RSSPhotoItem;
import vn.mbm.phimp.me.utils.RSSPhotoItem_Personal;
import vn.mbm.phimp.me.utils.geoDegrees;
import vn.mbm.phimp.me.utils.map.CustomItemizedOverlay;
import vn.mbm.phimp.me.utils.map.CustomOverlayItem;
import vn.mbm.phimp.me.utils.map.Road;
import vn.mbm.phimp.me.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

public class GalleryMap extends MapActivity implements LocationListener 
{
	LocationManager lm;
	LocationListener ll;
	MapView map;
	MapController mc;
	int latitude;
	int longitude;
	private static final int TURN_ON_GPS = 1;
	private static Drawable marker;
	private static Drawable currentMarker;
	static ProgressDialog progLoading;
	
	CustomItemizedOverlay<CustomOverlayItem> currentPositionOverlay;
	CustomItemizedOverlay<CustomOverlayItem> photosOverlay;
	static ArrayList<RSSPhotoItem> list_photos = new ArrayList<RSSPhotoItem>();
	static ArrayList<RSSPhotoItem_Personal> list_photos_personal = new ArrayList<RSSPhotoItem_Personal>();
	
	private static List<Overlay> mapOverlays;
	Context ctx;
	public Activity acti;
	ImageButton btnSwitch;
	ArrayList<String> filepath = new ArrayList<String>();
	private static boolean check=false;
	private static String image_path="";
	@SuppressWarnings("deprecation")
	@Override
    public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
        setContentView(R.layout.gallery_map);
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);		
        ctx = this;
        acti = this;
        check=false;
        Intent intent=getIntent();
        Bundle extract=intent.getExtras();
        if(extract!=null){
        	check=true;
        	image_path=extract.getString("image-path");
        }
        try
        {
        	lm = (LocationManager) ctx.getSystemService(LOCATION_SERVICE);
        	Criteria criteria = new Criteria();
			String provider = lm.getBestProvider(criteria, true);
			LocationListener ll = new MyLocationListener();
			lm.requestLocationUpdates(provider, 0, 0, ll);
        }
        catch (Exception e) 
        {
		}
        
        if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER))
		{
        	showDialog(TURN_ON_GPS);
		}
        try{
        	//progLoading = ProgressDialog.show(ctx, getString(R.string.loading), getString(R.string.infor_loading_photo_in_map), true, false);
            
            map = (MapView)findViewById(R.id.mapStore);
            map.setBuiltInZoomControls(true);            
            mc = map.getController();            
            mc.setZoom(6);      
            mapOverlays = map.getOverlays();     
            
            btnSwitch = (ImageButton) findViewById(R.id.btnswitch);
            btnSwitch.bringToFront();
            btnSwitch.setOnClickListener(new OnClickListener(){				
				@Override
				public void onClick(View v){
					Log.e("GalleryMap","check : "+check);
					if(check==true){
						acti.finish();
						Intent i = new Intent(acti, OpenStreetMap.class);
						i.putExtra("image-path", image_path);
						startActivity(i);
						
					}
					else{
//						PhimpMe.mTabHost.getTabWidget().getChildAt(2).setVisibility(View.GONE);
//						PhimpMe.mTabHost.getTabWidget().getChildAt(1).setVisibility(View.VISIBLE);
//						PhimpMe.mTabHost.setCurrentTab(1);
					}
					
				}
			});
        }catch(Exception e){
        	
        }
		
        new Thread()
        {
        	
			public void run()
			{
        		if ((PhimpMe.currentGeoPoint != null) && (currentPositionOverlay == null))
        		{
        			currentMarker = ctx.getResources().getDrawable(R.drawable.pin_user);        
        		    currentPositionOverlay = new CustomItemizedOverlay<CustomOverlayItem>(currentMarker, map);
		        	CustomOverlayItem overlayItem = new CustomOverlayItem(PhimpMe.currentGeoPoint, "My Position", "Geo Location : "+PhimpMe.currentGeoPoint.toString(), "", "");
		        	currentPositionOverlay.addOverlay(overlayItem);
		        	mapOverlays.add(currentPositionOverlay);		        	
		        	PhimpMe.addCurrentPin = true;
		        	mc.setZoom(16);
		        	mc.animateTo(PhimpMe.currentGeoPoint);
		        	
        		}
        		marker = ctx.getResources().getDrawable(R.drawable.pin_photo);	        	        
    	        photosOverlay = new CustomItemizedOverlay<CustomOverlayItem>(marker, map);      
        		//show only one photo in map from gallery view  
    	        Intent intent=getIntent();
    	        Bundle extract=intent.getExtras();
                if(extract != null && extract.getString("image-path")!=null){
                	  	 
                	Log.e("GalleryMap","Show only one photo");
                	String image_path=extract.getString("image-path");
                	File f =  new File(image_path);
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
    	    					 Log.e("GalleryMap ", "Longtitude :" +_longitude +" Latitude :"+_latitude);
	         					if ((_latitude != 0) && (_longitude != 0))
	         					{
	         						GeoPoint _gp = new GeoPoint(_latitude, _longitude);
	         						CustomOverlayItem _item = new CustomOverlayItem(_gp, f.getName(),"Local Photos", f.getPath(), image_path);
	         						photosOverlay.addOverlay(_item);
	         						mapOverlays.add(photosOverlay);     			
	         	 					final MapController mc = map.getController();
	         	 					mc.animateTo(_gp);
	         	    				mc.setZoom(16);
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
                
                //show all photo in gallery
                else {              
        		String[] projection = {MediaStore.Images.Media.DATA};
        		Cursor cursor = managedQuery( MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        projection,
                        null,null,
                        null);
        		int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        		for(int i=0; i<cursor.getCount(); i++){
        			if(cursor.moveToNext())
        			filepath.add(cursor.getString(columnIndex));
        		}	
        		
        		int count=filepath.size();
        		Log.d("Danh", "number local image :"+count);
        		int num_photos_added = 0;
				if(count>0){
					/*marker = ctx.getResources().getDrawable(R.drawable.pin_photo);	        	        
        	        photosOverlay = new CustomItemizedOverlay<CustomOverlayItem>(marker, map);      */  	        
        	        int i;
        	        
        	        for(i=0; i<filepath.size(); i++){
        	        String imagePath=filepath.get(i);

				
    	                Log.d("Danh", "gallery map path photos index :"+i+imagePath);
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
	    	    					 Log.d("Danh ", "Longtitude :" +_longitude +" Latitude :"+_latitude);
    	         					if ((_latitude != 0) && (_longitude != 0))
    	         					{
    	         						GeoPoint _gp = new GeoPoint(_latitude, _longitude);
    	         						CustomOverlayItem _item = new CustomOverlayItem(_gp, f.getName(),"Local Photos", f.getPath(), imagePath);
    	         						photosOverlay.addOverlay(_item);
    	         						num_photos_added++;
    	         						filepath.remove(i);
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
        	        if(num_photos_added>0){
        	        	mapOverlays.add(photosOverlay);
            			handler.sendEmptyMessage(num_photos_added);	
    					final MapController mc = map.getController();
       					mc.setZoom(16);
       					//mc.animateTo(PhimpMe.currentGeoPoint);
        	        }else
        	         if(num_photos_added < 1){

        	        	/*GeoPoint _gp = new GeoPoint(10031200, 105775280);
    					CustomOverlayItem _item = new CustomOverlayItem(_gp, null,null, null, null);
    					photosOverlay.addOverlay(_item);
    					handler.sendEmptyMessage(0);	
            	        mapOverlays.add(photosOverlay);
    					final MapController mc1 = map.getController();
    					mc1.animateTo(_gp);
    					mc1.setZoom(16);*/
        	        	
        	        }                   	        				
				}
				
          }
        }
     }.start();   
}
	
	private Handler handler = new Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{
			//GalleryMap.progLoading.dismiss();
			//Toast.makeText(ctx, msg.what + " " + getString(R.string.infor_map_have_num_photos), Toast.LENGTH_LONG).show();
		}
	};
	
	@Override
	protected void onResume()
	{
		super.onResume();	
		
		if (PhimpMe.IdList.size() == 5) {PhimpMe.IdList.clear();PhimpMe.IdList.add(0);}
		PhimpMe.IdList.add(2);		
		PhimpMe.showTabs();
		try
		{
			lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, ll);
		}
		catch (Exception e) 
		{
			try
	        {
	        	lm = (LocationManager) this.getSystemService(LOCATION_SERVICE);
				LocationListener ll = new MyLocationListener();
				lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, ll);
	        }
	        catch (Exception e2) 
	        {
			}
		}
	}
	
	/*@Override
	protected void onPause()
	{
		//super.onPause();
		//finish();
		lm.removeUpdates(this);
	}*/
	
	

	@Override
	public void onLocationChanged(Location location) {
		
	}

	@Override
	public void onProviderDisabled(String provider) {
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		
	}
	class MapOverlay extends com.google.android.maps.Overlay 
	{
		Road mRoad;
		ArrayList<GeoPoint> mPoints;

		public MapOverlay(Road road, MapView mv) 
		{
			mRoad = road;
			if (road.mRoute.length > 0) 
			{
				mPoints = new ArrayList<GeoPoint>();
				for (int i = 0; i < road.mRoute.length; i++) 
				{
					mPoints.add(new GeoPoint(
							(int) (road.mRoute[i][1] * 1000000),
							(int) (road.mRoute[i][0] * 1000000))
					);
				}
				int moveToLat = (mPoints.get(0).getLatitudeE6() + (mPoints.get(
						mPoints.size() - 1).getLatitudeE6() - mPoints.get(0)
						.getLatitudeE6()) / 2);
				int moveToLong = (mPoints.get(0).getLongitudeE6() + (mPoints.get(
						mPoints.size() - 1).getLongitudeE6() - mPoints.get(0)
						.getLongitudeE6()) / 2);
				GeoPoint moveTo = new GeoPoint(moveToLat, moveToLong);

				MapController mapController = mv.getController();
				mapController.animateTo(moveTo);
			}
			
		}

		@Override
		public boolean draw(Canvas canvas, MapView mv, boolean shadow, long when) 
		{
			try
			{
				super.draw(canvas, mv, shadow);
				drawPath(mv, canvas);
			}
			catch (Exception e) 
			{
			}
			return true;
		}

		public void drawPath(MapView mv, Canvas canvas) 
		{
			int x1 = -1, y1 = -1, x2 = -1, y2 = -1;
			Paint paint = new Paint();
			paint.setColor(Color.GREEN);
			paint.setStyle(Paint.Style.STROKE);
			paint.setStrokeWidth(3);
			for (int i = 0; i < mPoints.size(); i++) {
				Point point = new Point();
				mv.getProjection().toPixels(mPoints.get(i), point);
				x2 = point.x;
				y2 = point.y;
				if (i > 0) {
					canvas.drawLine(x1, y1, x2, y2, paint);
				}
				x1 = x2;
				y1 = y2;
			}
		}
	
	    
	}
	public class MyLocationListener implements LocationListener
	{

		@Override
		public void onLocationChanged(Location loc)
		{
			if (loc != null)
			{
				PhimpMe.curLatitude = loc.getLatitude();
				PhimpMe.curLongtitude = loc.getLongitude();
				int _lat = (int) (PhimpMe.curLatitude * 1000000);
				int _long = (int) (PhimpMe.curLongtitude * 1000000);
				PhimpMe.currentGeoPoint = new GeoPoint(_lat, _long);
		        if (!PhimpMe.addCurrentPin)
		        {
		        	currentMarker = ctx.getResources().getDrawable(R.drawable.pin_user);
		        	currentPositionOverlay = new CustomItemizedOverlay<CustomOverlayItem>(currentMarker, map);
		        	CustomOverlayItem overlayItem = new CustomOverlayItem(PhimpMe.currentGeoPoint, "My Position", "skfkjsdfjsdfjsdf", null, null);
		        	currentPositionOverlay.addOverlay(overlayItem);
		        	mapOverlays.add(currentPositionOverlay);
		        	
		        	PhimpMe.addCurrentPin = true;
		        	mc.animateTo(PhimpMe.currentGeoPoint);
		        }
			}
			else
			{
			}
		}


		@Override
		public void onProviderDisabled(String provider)
		{
		}


		@Override
		public void onProviderEnabled(String provider)
		{
			Toast.makeText( getApplicationContext(), "Gps Enabled", Toast.LENGTH_SHORT).show();
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras)
		{
			
		}
	}
	@Override
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
	
	public void onBackPressed()
	{	
		if(check==false){
			PhimpMe.IdList.remove(PhimpMe.IdList.size()-1);
			if (PhimpMe.IdList.get(PhimpMe.IdList.size()-1) == 1) PhimpMe.IdList.remove(PhimpMe.IdList.size()-1);
//			PhimpMe.mTabHost.setCurrentTab(PhimpMe.IdList.get(PhimpMe.IdList.size()-1));
			super.onStop();		
			Log.d("Hon","Backpressed !");
		}else{
			acti.finish();
		}
		
					
			
	}

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}
	
}
