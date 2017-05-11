package vn.mbm.phimp.me.gallery;

import android.app.Activity;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import java.util.List;

public class GalleryImageAdapter extends PagerAdapter {

	private Activity context;
	private List<String> mFilePath;

	public GalleryImageAdapter(Activity context, List<String> filepath) {
		this.context = context;
		mFilePath = filepath;
	}

	public int getCount() {
		if (mFilePath == null) return 0;
		return mFilePath.size();
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view == object;
	}


	@Override
	public void destroyItem(ViewGroup container, int position,Object object) {
		container.removeView((View) object);
	}

	public long getItemId(int position) {
		return 0;
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		GalleryImageView galleryImageView = new GalleryImageView(context);
		galleryImageView.setImage(mFilePath.get(position));
		container.addView(galleryImageView);
		return galleryImageView;
	}

}
