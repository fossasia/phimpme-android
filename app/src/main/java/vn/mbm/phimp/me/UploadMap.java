package vn.mbm.phimp.me;

import java.util.ArrayList;
import java.util.List;

import vn.mbm.phimp.me.gallery3d.media.CropImage;
import vn.mbm.phimp.me.utils.Commons;
import vn.mbm.phimp.me.R;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

public class UploadMap extends MapActivity
{
	Button btnSave;
	Button btnCancel;
	ImageButton btnLocation;
	Button btnClear;
	LocationListener ll;
	private static final int DIALOG_ADD_CURRENT = 1;
	private static final int TURN_ON_GPS = 2;
	
	MapView mv;
	
	MapController mc;
	
	mapOverlay mo;
	
	Intent data;
	
	Drawable marker;
	
	Context ctx;
	
	List<Overlay> overlays;
	
	SitesOverlay pinoverlays;
	
	OverlayItem pinicon;
	
	Boolean addPinIcon;
	
	Boolean addPinIconCurrentPosition;
	
	int latitude;
	int longitude;
	LocationManager lm ;
	ProgressDialog gpsloading;
	ImageButton btnSwitch;
	@SuppressWarnings("deprecation")
	@Override
    public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
        setContentView(R.layout.upload_map);
        
        addPinIcon = false;
        addPinIconCurrentPosition = false;
        btnSave = (Button) findViewById(R.id.btnUploadMapSave);
        btnCancel = (Button) findViewById(R.id.btnUploadMapCancel);
        btnLocation = (ImageButton) findViewById(R.id.btnUploadMapLocation);
        btnClear = (Button) findViewById(R.id.btnUploadMapClear);
        mv = (MapView) findViewById(R.id.mvUploadMap);
        mc = mv.getController();
        overlays = mv.getOverlays();
        mo = new mapOverlay();
        data = getIntent();
        marker = getResources().getDrawable(R.drawable.upload_marker);
        pinoverlays = new SitesOverlay(marker);
        ctx = this;
        lm = (LocationManager) ctx.getSystemService(LOCATION_SERVICE);        
        //Turn on GPS    
        
    	String provider1 = android.provider.Settings.Secure.getString(getContentResolver(), android.provider.Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
    	  if(!provider1.contains("gps")){
    	    showDialog(TURN_ON_GPS);
    	   
    	  }
        btnSave.setOnClickListener(new OnClickListener() 
        {
			@Override
			public void onClick(View v) 
			{
				if (addPinIcon)
				{
					Log.e("UploadMap","latitude : "+latitude+", longitude : "+longitude);
					/*data.putExtra("latitude", latitude);
					data.putExtra("longitude", longitude);
					setResult(RESULT_OK, data);*/
					CropImage.from="Map";
					CropImage.latitude=latitude;
					CropImage.longitude=longitude;
					finish();
				}
				else
				{
					Commons.AlertLog(ctx, getString(R.string.error_upload_map_no_position), getString(R.string.accept)).show();
				}
			}
		});
        
        btnCancel.setOnClickListener(new OnClickListener() 
        {
			@Override
			public void onClick(View v) 
			{
				setResult(RESULT_CANCELED);
				finish();
			}
		});
        
        btnLocation.setOnClickListener(new OnClickListener() 
        {			
			@Override
			public void onClick(View v) 
			{
				showDialog(DIALOG_ADD_CURRENT);
			}
		});
        
        btnClear.setOnClickListener(new OnClickListener() 
        {
			@Override
			public void onClick(View v) 
			{
				overlays.clear();
				
				mo = new mapOverlay();
				pinoverlays = new SitesOverlay(marker);
				
				PhimpMe.UploadLatitude = null;
				PhimpMe.UploadLongitude = null;
				
		       overlays.add(mo);
		       overlays.add(pinoverlays);
		        
		        mv.invalidate();
		        
		        addPinIcon = false;
			}
		});
        btnSwitch=(ImageButton)findViewById(R.id.btnswitchOSM_UploadMap);
        btnSwitch.bringToFront();
        btnSwitch.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
				//Intent intent=new Intent(UploadMap.this, UploadOSMap.class);
				Intent intent=new Intent(UploadMap.this, UploadMap.class);
				startActivity(intent);
			}
		});
        marker.setBounds(0, 0, marker.getIntrinsicWidth(),marker.getIntrinsicHeight());
        gpsloading = new ProgressDialog(ctx);
		gpsloading.setCancelable(true);
		gpsloading.setCanceledOnTouchOutside(false);
		gpsloading.setTitle(getString(R.string.loading));
		gpsloading.setMessage(getString(R.string.infor_upload_loading_current_position));
		gpsloading.setIndeterminate(true);

        mv.getController().setZoom(12);
        mv.setBuiltInZoomControls(false);
        overlays.clear();
        overlays.add(mo);
        overlays.add(pinoverlays);
        
        
	}
	
	@Override
	protected boolean isRouteDisplayed() 
	{
		return false;
	}
	
	/*
	 * https://github.com/commonsguy/cw-advandroid/tree/master/Maps/NooYawkTouch
	 */
	private class SitesOverlay extends ItemizedOverlay<OverlayItem> 
	{
	    private List<OverlayItem> items=new ArrayList<OverlayItem>();
	    private Drawable marker=null;
	    private OverlayItem inDrag=null;
	    private ImageView dragImage=null;
	    private int xDragImageOffset=0;
	    private int yDragImageOffset=0;
	    private int xDragTouchOffset=0;
	    private int yDragTouchOffset=0;
	    
	    public SitesOverlay(Drawable marker) 
	    {
	    	super(marker);
	    	this.marker=marker;
	      
	    	dragImage = (ImageView)findViewById(R.id.drag);
	    	xDragImageOffset = dragImage.getDrawable().getIntrinsicWidth()/2;
	    	yDragImageOffset = dragImage.getDrawable().getIntrinsicHeight();
	    	
	    	if ((PhimpMe.UploadLatitude != null) && (PhimpMe.UploadLongitude != null) && (!addPinIcon))
	    	{
	    		Log.d("thong", "UploadMap > SitesOverlay > addPinIcon: " + addPinIcon);
	    		
	    		latitude = (int) (PhimpMe.UploadLatitude * 1E6);
	    		longitude = (int) (PhimpMe.UploadLongitude * 1E6);
	    		
	    		items.add(new OverlayItem(getPoint(PhimpMe.UploadLatitude, PhimpMe.UploadLongitude), "", ""));
	    		Log.d("thong", "UploadMap > SitesOverlay > Add items Upload");
	    		mc.animateTo(getPoint(PhimpMe.UploadLatitude, PhimpMe.UploadLongitude));
	    		addPinIcon = true;
	    	}
	    	
	      	populate();
	      	
	      	if (addPinIconCurrentPosition)
	      	{
	      		try
	      		{
	      			gpsloading.dismiss();
	      		}
	      		catch (Exception e) 
	      		{
					
				}
	      		addPinIconCurrentPosition = false;
	      	}
	    }
	    
	    @Override
	    protected OverlayItem createItem(int i) 
	    {
	    	return(items.get(i));
	    }
	    
	    public void addItem(OverlayItem item)
	    {
	    	items.add(item);
	    	populate();
	    }
	    
	    @Override
	    public void draw(Canvas canvas, MapView mapView, boolean shadow) 
	    {
	    	super.draw(canvas, mapView, shadow);
	    	boundCenterBottom(marker);
	    }
	    
	    @Override
	    public int size() 
	    {
	    	return(items.size());
	    }
	    
	    @Override
	    public boolean onTouchEvent(MotionEvent event, MapView mapView) 
	    {
	    	final int action=event.getAction();
	    	final int x=(int)event.getX();
	    	final int y=(int)event.getY();
	    	boolean result=false;
	      
	    	if (action == MotionEvent.ACTION_DOWN) 
	    	{
	    		for (OverlayItem item : items) 
	    		{
	    			Point p=new Point(0,0);
	          
	    			mv.getProjection().toPixels(item.getPoint(), p);
	          
	    			if (hitTest(item, marker, x-p.x, y-p.y)) 
	    			{
	    				result=true;
	    				inDrag=item;
	    				items.remove(inDrag);
	    				populate();

	    				xDragTouchOffset=0;
	    				yDragTouchOffset=0;
	            
	    				setDragImagePosition(p.x, p.y);
	    				dragImage.setVisibility(View.VISIBLE);

	    				xDragTouchOffset=x-p.x;
	    				yDragTouchOffset=y-p.y;
	            
	    				break;
	    			}
	    		}
	    	}
	    	else if ((action == MotionEvent.ACTION_MOVE) && (inDrag != null)) 
	    	{
	    		setDragImagePosition(x, y);
	    		result=true;
	    	}
	    	else if ((action==MotionEvent.ACTION_UP) && (inDrag!=null)) 
	    	{
	    		dragImage.setVisibility(View.GONE);
	        
	    		GeoPoint pt = mv.getProjection().fromPixels(x-xDragTouchOffset, y-yDragTouchOffset);
	    		OverlayItem toDrop=new OverlayItem(pt, inDrag.getTitle(), inDrag.getSnippet());
	    		
	    		latitude = pt.getLatitudeE6();
	    		longitude = pt.getLongitudeE6();
	    		
	    		items.add(toDrop);
	    		populate();
	        
	    		inDrag=null;
	    		result=true;
	    	}
	      
	    	return(result || super.onTouchEvent(event, mapView));
	    }
	    
	    private void setDragImagePosition(int x, int y) 
	    {
	    	RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams)dragImage.getLayoutParams();
	            
	    	lp.setMargins(x-xDragImageOffset-xDragTouchOffset, y-yDragImageOffset-yDragTouchOffset, 0, 0);
	    	dragImage.setLayoutParams(lp);
	    }
	}
	
	private GeoPoint getPoint(double lat, double lon) 
	{
		return(new GeoPoint((int)(lat*1000000.0), (int)(lon*1000000.0)));
	}
	
	/*
	 * Reference:
	 * 	http://stackoverflow.com/questions/4806061/how-do-i-respond-to-a-tap-on-an-android-mapview-but-ignore-pinch-zoom
	 */
	class mapOverlay extends com.google.android.maps.Overlay
	{
		private boolean isPinch = false;
		
		@Override
		public boolean onTap(GeoPoint p, MapView map)
		{
		    if (isPinch)
		    {
		        return false;
		    }
		    else
		    {
		        if ( p!=null )
		        {
		        	if (!addPinIcon)
		        	{
		        		latitude = p.getLatitudeE6();
		        		longitude = p.getLongitudeE6();
		        		GeoPoint _gp = new GeoPoint(p.getLatitudeE6(), p.getLongitudeE6());
		        		pinicon = new OverlayItem(_gp, "", "");
		        		pinoverlays.addItem(pinicon);
		        		mc.animateTo(_gp);
		        		addPinIcon = true;
		        	}
		            return true;            // We handled the tap
		        }
		        else
		        {
		            return false;           // Null GeoPoint
		        }
		    }
		}
		
	    @Override
	    public boolean onTouchEvent(MotionEvent e, MapView mapview)
	    {
	    	int fingers = e.getPointerCount();
	        if (e.getAction() == MotionEvent.ACTION_DOWN )
	        {
	            isPinch=false;  // Touch DOWN, don't know if it's a pinch yet
	        }
	        if ((e.getAction() == MotionEvent.ACTION_MOVE) && (fingers==2))
	        {
	            isPinch=true;   // Two fingers, def a pinch
	        }
	        return super.onTouchEvent(e,mapview);
	    }
	}
	
	@Override
	protected Dialog onCreateDialog(int id) 
	{
		switch (id)
		{
			case DIALOG_ADD_CURRENT:
			{
				return new AlertDialog.Builder(ctx)
					.setTitle("")
					.setMessage("Do you want to add pin on your current position?")
					.setPositiveButton("Yes, drop it", new DialogInterface.OnClickListener() 
					{
						@Override
						public void onClick(DialogInterface dialog, int which) 
						{
							gpsloading.show();
							
							try
				        	{
								runOnUiThread(new Runnable() 
								{
									@Override
									public void run() 
									{
										try
								        {
								        	
								        	if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER))
											{
												gpsloading.dismiss();												
												Commons.AlertLog(ctx, getString(R.string.error_gps_fail) + "\n" + getString(R.string.infor_turn_on_gps), getString(R.string.accept)).show();
											}
								        	else
								        	{
								        		Criteria criteria = new Criteria();
								    			String provider = lm.getBestProvider(criteria, true);
								    			LocationListener ll = new MyLocationListener();
								    			lm.requestLocationUpdates(provider, 0, 0, ll);
								    			addPinIconCurrentPosition = true;
								        	}
								        }
								        catch (Exception e) 
								        {
								        	gpsloading.dismiss();
								        	Commons.AlertLog(ctx, getString(R.string.error_gps_fail) + "\n" + getString(R.string.infor_turn_on_gps), getString(R.string.accept));
								        	e.printStackTrace();
										}
									}
								});
				        	}
				        	catch (Exception e) 
				        	{
								e.printStackTrace();
							}
						}
					})
					.setNegativeButton("No, just move to my position", new DialogInterface.OnClickListener() 
					{
						@Override
						public void onClick(DialogInterface dialog, int which) 
						{
							if (PhimpMe.currentGeoPoint != null)
							{
								mc.animateTo(PhimpMe.currentGeoPoint);
							}
							else
							{
								try
					        	{
					        		LocationManager lm = (LocationManager) ctx.getSystemService(LOCATION_SERVICE);
					        		Criteria criteria = new Criteria();
					    			String provider = lm.getBestProvider(criteria, true);
					    			LocationListener ll = new MyLocationListener();
					    			lm.requestLocationUpdates(provider, 0, 0, ll);
					        	}
					        	catch (Exception e) 
					        	{
									e.printStackTrace();
								}
							}
						}
					})
					.create();
			}
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
	
	public class MyLocationListener implements LocationListener
	{
		@Override
		public void onLocationChanged(Location location) 
		{
			Log.d("thong", "UploadMap > MyLocationListener > onLocationChanged");
			if (location != null)
			{
				Log.d("thong", "UploadMap > MyLocationListener > onLocationChanged > location not null");
				
				PhimpMe.curLatitude = location.getLatitude();
				PhimpMe.curLongtitude = location.getLongitude();
				
				PhimpMe.currentGeoPoint = new GeoPoint((int) (PhimpMe.curLatitude * 1000000), (int) (PhimpMe.curLongtitude * 1000000));
				
				Log.d("thong", "UploadMap > MyLocationListener > onLocationChanged > latitude: " + PhimpMe.curLatitude);
				Log.d("thong", "UploadMap > MyLocationListener > onLocationChanged > longitude: " + PhimpMe.curLongtitude);
				
				mc.animateTo(PhimpMe.currentGeoPoint);
				
				if (addPinIconCurrentPosition)
				{
					Log.d("thong", "UploadMap > MyLocationListener > onLocationChanged > addPinIconCurrentPosition: " + addPinIconCurrentPosition);
					
					overlays.clear();
					
					PhimpMe.UploadLatitude = PhimpMe.curLatitude;
					PhimpMe.UploadLongitude = PhimpMe.curLongtitude;
					addPinIcon = false;
					
					mo = new mapOverlay();
					pinoverlays = new SitesOverlay(marker);
					
			        overlays.add(mo);
			        overlays.add(pinoverlays);
			        
			        mv.invalidate();
				}
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
