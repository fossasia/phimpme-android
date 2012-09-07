package vn.mbm.phimp.me.ultils.osmap;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.OverlayItem;

public class CustomOverlayItem extends OverlayItem {

	protected String mImageURL;
	protected String mFullpath;
	
	public CustomOverlayItem(GeoPoint point, String title,  String fullpath) {
		super(fullpath, title, point);
		mFullpath = fullpath;
	}
	
	public String getFullPath() {
		return mFullpath;
	}

	public void setFullPath(String fullpath) {
		this.mFullpath = fullpath;
	}
}