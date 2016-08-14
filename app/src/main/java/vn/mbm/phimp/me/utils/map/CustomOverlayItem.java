package vn.mbm.phimp.me.utils.map;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.OverlayItem;

public class CustomOverlayItem extends OverlayItem {

	protected String mImageURL;
	protected String mFullpath;
	
	public CustomOverlayItem(GeoPoint point, String title, String snippet, String imageURL, String fullpath) {
		super(point, title, snippet);
		mImageURL = imageURL;
		mFullpath = fullpath;
	}

	public String getImageURL() {
		return mImageURL;
	}

	public void setImageURL(String imageURL) {
		this.mImageURL = imageURL;
	}
	
	public String getFullPath() {
		return mFullpath;
	}

	public void setFullPath(String fullpath) {
		this.mFullpath = fullpath;
	}
}