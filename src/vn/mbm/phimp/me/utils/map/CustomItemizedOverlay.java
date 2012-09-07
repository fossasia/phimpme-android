package vn.mbm.phimp.me.utils.map;

import java.io.File;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;

import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;
import com.readystatesoftware.mapviewballoons.BalloonItemizedOverlay;
import com.readystatesoftware.mapviewballoons.BalloonOverlayView;

public class CustomItemizedOverlay<Item extends OverlayItem> extends BalloonItemizedOverlay<CustomOverlayItem> {

	private ArrayList<CustomOverlayItem> m_overlays = new ArrayList<CustomOverlayItem>();
	private Context c;
	
	public CustomItemizedOverlay(Drawable defaultMarker, MapView mapView) {
		super(boundCenter(defaultMarker), mapView);
		c = mapView.getContext();
	}
	public void addOverlay(CustomOverlayItem overlay) {
	    m_overlays.add(overlay);
	    populate();
	}

	@Override
	protected CustomOverlayItem createItem(int i) {
		return m_overlays.get(i);
	}

	@Override
	public int size() {
		return m_overlays.size();
	}

	@Override
	protected boolean onBalloonTap(int index, CustomOverlayItem item) 
	{

			Log.d("Danh","OnballonTap function called");
			String path = item.getFullPath();
			File f = new File(path);
			Intent myitent = new Intent();			
			myitent.setAction(Intent.ACTION_VIEW);
			myitent.setDataAndType(Uri.fromFile(f), "image/*");
			((Activity) c).startActivityForResult(myitent, 3);
			
			return true;
		
		

	}

	@Override
	protected BalloonOverlayView<CustomOverlayItem> createBalloonOverlayView() {
		// use our custom balloon view with our custom overlay item type:
		Log.d("Danh","BalloonOverlayView function called");
		return new CustomBalloonOverlayView<CustomOverlayItem>(getMapView().getContext(), getBalloonBottomOffset());
		
	}
	
	/*public boolean onTouchEvent(MotionEvent event, MapView mapView)
	{
		
		return false;

	}
	public boolean onTap(GeoPoint geo, MapView mapView)
	{ 
    	return false;
	}*/
	
}