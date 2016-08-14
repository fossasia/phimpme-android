package vn.mbm.phimp.me;

import java.util.ArrayList;

import vn.mbm.phimp.me.gallery.PhimpMeGallery;
import vn.mbm.phimp.me.utils.RSSPhotoItem;
import vn.mbm.phimp.me.utils.RSSPhotoItem_Personal;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class GridviewAdapter {
	

	
}

class GridFlickrAdaper extends BaseAdapter {
	ArrayList<RSSPhotoItem> list_flickr = new ArrayList<RSSPhotoItem>();
	ArrayList<String> file_path = new ArrayList<String>();
	
	private LayoutInflater li;
	private ArrayList<Bitmap> bitmap_p_flickr;
	Context ctx;

	public GridFlickrAdaper(ArrayList<RSSPhotoItem> list_thumb,
		ArrayList<Bitmap> bitmap_p_flickr, Context c) {
		list_flickr.clear();
		file_path.clear();	
		this.bitmap_p_flickr = bitmap_p_flickr;		
		ctx = c;
		for (int i = 0; i < list_thumb.size(); i++)
			if (list_thumb.get(i).getService().equals("public_flickr")) {
				list_flickr.add(list_thumb.get(i));				
				file_path.add(list_thumb.get(i).getURL());				
				
			}
	}

	public void addItem(RSSPhotoItem item) {

		list_flickr.add(0, item);
		file_path.add(0,list_flickr.get(0).getURL());
		notifyDataSetChanged();
	}

	public void removeItem() {

		list_flickr.clear();		
		notifyDataSetChanged();
	}

	public void setItem(RSSPhotoItem item) {
		list_flickr.set(0, item);
		this.notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return list_flickr.size();
	}

	@Override
	public Object getItem(int arg0) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view;
		final int pos = position;
		if (convertView == null) {
			li = (LayoutInflater) ctx
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			view = li.inflate(R.layout.gallery_grid_photos_item, null);
		} else {
			view = convertView;
		}
		
		ImageView load_iv = (ImageView) view
				.findViewById(R.id.imgGalleryGridItemLoading);
		ImageView iv = (ImageView) view
				.findViewById(R.id.imgGalleryGridItemThumbnail);
		
		load_iv.setScaleType(ImageView.ScaleType.FIT_XY);
		load_iv.setVisibility(View.INVISIBLE);
		try {
			iv.setImageBitmap(bitmap_p_flickr.get(position));			
		} catch (Exception e) {
		}
		iv.setVisibility(View.VISIBLE);

		//final String tmp_url = url;

		iv.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//newGallery.pro_gress=ProgressDialog.show(ctx, "", "Please wait...", true, false);
				//newGallery n=new newGallery();
				//n.Dialog(4000, newGallery.pro_gress);
				Intent _intent = new Intent();
				_intent.setClass(ctx, PhimpMeGallery.class);
				PhimpMeGallery.setFileList(file_path);	
				_intent.putExtra("activityName", "GridviewAdapter");
				_intent.putExtra("index", pos);
				
				((Activity) ctx).startActivity(_intent);
			}
		});
		return view;
	}

}

class GridRecentFlickrAdaper extends BaseAdapter {
	ArrayList<RSSPhotoItem> list_recent_flickr = new ArrayList<RSSPhotoItem>();
	private LayoutInflater li;
	ArrayList<String> file_path = new ArrayList<String>();
	private ArrayList<Bitmap> bitmap_recent_flickr;
	Context ctx;

	public GridRecentFlickrAdaper(ArrayList<RSSPhotoItem> list_thumb,
			ArrayList<Bitmap> bitmap_recent_flickr, Context c) {
		list_recent_flickr.clear();
		this.bitmap_recent_flickr = bitmap_recent_flickr;
		ctx = c;
		file_path.clear();	
		for (int i = 0; i < list_thumb.size(); i++)
			if (list_thumb.get(i).getService().equals("recent_flickr")) {
				list_recent_flickr.add(list_thumb.get(i));
				file_path.add(list_thumb.get(i).getURL());
			}
	}

	public void addItem(RSSPhotoItem item) {

		list_recent_flickr.add(0, item);
		file_path.add(0,list_recent_flickr.get(0).getURL());
		notifyDataSetChanged();
	}

	public void removeItem() {

		list_recent_flickr.clear();
		notifyDataSetChanged();
	}

	public void setItem(RSSPhotoItem item) {
		list_recent_flickr.set(0, item);
		this.notifyDataSetChanged();
	}
	
	@Override
	public int getCount() {
		return list_recent_flickr.size();
	}

	@Override
	public Object getItem(int arg0) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view;
		final int pos = position;
		if (convertView == null) {
			li = (LayoutInflater) ctx
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			view = li.inflate(R.layout.gallery_grid_photos_item, null);
		} else {
			view = convertView;
		}
		String url = list_recent_flickr.get(position).getURL();
		Log.i("GV_Adapter","url flickr public : "+url);
		ImageView load_iv = (ImageView) view
				.findViewById(R.id.imgGalleryGridItemLoading);
		ImageView iv = (ImageView) view
				.findViewById(R.id.imgGalleryGridItemThumbnail);
		load_iv.setScaleType(ImageView.ScaleType.FIT_XY);
		load_iv.setVisibility(View.INVISIBLE);
		try {
			iv.setImageBitmap(bitmap_recent_flickr.get(position));
		} catch (Exception e) {
		}
		iv.setVisibility(View.VISIBLE);

		//final String tmp_url = url;

		iv.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				/*newGallery.pro_gress=ProgressDialog.show(ctx, "", "Please wait...", true, false);
				newGallery n=new newGallery();
				n.Dialog(4000, newGallery.pro_gress);*/
				Intent _intent = new Intent();
				_intent.setClass(ctx, PhimpMeGallery.class);
				PhimpMeGallery.setFileList(file_path);	
				_intent.putExtra("index", pos);
				_intent.putExtra("activityName", "GridviewAdapter");
				((Activity) ctx).startActivityForResult(_intent, 3);
			}
		});
		return view;
	}

}

class GridPublicPicasaAdaper extends BaseAdapter {
	ArrayList<RSSPhotoItem> list_public_picasa = new ArrayList<RSSPhotoItem>();
	private LayoutInflater li;
	ArrayList<String> file_path = new ArrayList<String>();
	private ArrayList<Bitmap> bitmap_public_picasa;
	Context ctx;

	public GridPublicPicasaAdaper(ArrayList<RSSPhotoItem> list_thumb,
			ArrayList<Bitmap> bitmap_public_picasa, Context c) {
		list_public_picasa.clear();
		file_path.clear();
		this.bitmap_public_picasa = bitmap_public_picasa;
		ctx = c;
		for (int i = 0; i < list_thumb.size(); i++)
			if (list_thumb.get(i).getService().equals("public_picasa")) {
				list_public_picasa.add(list_thumb.get(i));
				file_path.add(list_thumb.get(i).getURL());
			}
	}

	public void addItem(RSSPhotoItem item) {

		list_public_picasa.add(0, item);
		file_path.add(0,list_public_picasa.get(0).getURL());
		notifyDataSetChanged();
	}

	public void removeItem() {

		list_public_picasa.clear();
		notifyDataSetChanged();
	}	
	
	public void setItem(RSSPhotoItem item) {
		list_public_picasa.set(0, item);
		this.notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return list_public_picasa.size();
	}

	@Override
	public Object getItem(int arg0) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view;
		final int pos = position;
		if (convertView == null) {
			li = (LayoutInflater) ctx
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			view = li.inflate(R.layout.gallery_grid_photos_item, null);
		} else {
			view = convertView;
		}
		//String url = list_public_picasa.get(position).getURL();

		ImageView load_iv = (ImageView) view
				.findViewById(R.id.imgGalleryGridItemLoading);
		ImageView iv = (ImageView) view
				.findViewById(R.id.imgGalleryGridItemThumbnail);

		load_iv.setScaleType(ImageView.ScaleType.FIT_XY);
		load_iv.setVisibility(View.INVISIBLE);

		try {
			iv.setImageBitmap(bitmap_public_picasa.get(position));
		} catch (Exception e) {
		}
		iv.setVisibility(View.VISIBLE);

		//final String tmp_url = url;

		iv.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				/*newGallery.pro_gress=ProgressDialog.show(ctx, "", "Please wait...", true, false);
				newGallery n=new newGallery();
				n.Dialog(4000, newGallery.pro_gress);*/
				Intent _intent = new Intent();
				_intent.setClass(ctx, PhimpMeGallery.class);
				PhimpMeGallery.setFileList(file_path);_intent.putExtra("index", pos);
				//_intent.putExtra("image-path", tmp_url);						
				//_intent.putExtra("activityName", "GridviewAdapter");
				((Activity) ctx).startActivityForResult(_intent, 3);
			}
		});
		return view;
	}

}

class GridGoogleNewsAdaper extends BaseAdapter {
	ArrayList<RSSPhotoItem> list_google_news = new ArrayList<RSSPhotoItem>();
	private LayoutInflater li;
	ArrayList<String> file_path = new ArrayList<String>();
	private ArrayList<Bitmap> bitmap_google_news;
	Context ctx;

	public GridGoogleNewsAdaper(ArrayList<RSSPhotoItem> list_thumb,
			ArrayList<Bitmap> bitmap_google_news, Context c) {
		list_google_news.clear();
		file_path.clear();
		this.bitmap_google_news = bitmap_google_news;
		ctx = c;
		for (int i = 0; i < list_thumb.size(); i++)
			if (list_thumb.get(i).getService().equals("google_news")) {
				list_google_news.add(list_thumb.get(i));
				file_path.add(list_thumb.get(0).getURL());
			}
	}

	public void addItem(RSSPhotoItem item) {

		list_google_news.add(0, item);
		file_path.add(0,list_google_news.get(0).getURL());
		notifyDataSetChanged();
	}

	public void removeItem() {

		list_google_news.clear();
		notifyDataSetChanged();
	}

	public void setItem(RSSPhotoItem item) {
		list_google_news.set(0, item);
		this.notifyDataSetChanged();
	}
	
	@Override
	public int getCount() {
		return list_google_news.size();
	}

	@Override
	public Object getItem(int arg0) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view;
		final int pos = position;
		if (convertView == null) {
			li = (LayoutInflater) ctx
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			view = li.inflate(R.layout.gallery_grid_photos_item, null);
		} else {
			view = convertView;
		}
		//String url = list_google_news.get(position).getURL();

		ImageView load_iv = (ImageView) view
				.findViewById(R.id.imgGalleryGridItemLoading);
		ImageView iv = (ImageView) view
				.findViewById(R.id.imgGalleryGridItemThumbnail);

		load_iv.setScaleType(ImageView.ScaleType.FIT_XY);
		load_iv.setVisibility(View.INVISIBLE);

		try {
			iv.setImageBitmap(bitmap_google_news.get(position));
		} catch (Exception e) {
		}
		iv.setVisibility(View.VISIBLE);

		//final String tmp_url = url;

		iv.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				/*newGallery.pro_gress=ProgressDialog.show(ctx, "", "Please wait...", true, false);
				newGallery n=new newGallery();
				n.Dialog(4000, newGallery.pro_gress);*/
				Intent _intent = new Intent();
				_intent.setClass(ctx, PhimpMeGallery.class);
				PhimpMeGallery.setFileList(file_path);_intent.putExtra("index", pos);
				//_intent.putExtra("image-path", tmp_url);						
				_intent.putExtra("activityName", "GridviewAdapter");
				((Activity) ctx).startActivityForResult(_intent, 3);
			}
		});
		return view;
	}

}

class GridYahooAdapter extends BaseAdapter {
	ArrayList<RSSPhotoItem> list_yahoo = new ArrayList<RSSPhotoItem>();
	private LayoutInflater li;
	ArrayList<String> file_path = new ArrayList<String>();
	private ArrayList<Bitmap> bitmap_p_yahoo;
	Context ctx;

	public GridYahooAdapter(ArrayList<RSSPhotoItem> list_thumb,
			ArrayList<Bitmap> bitmap_p_yahoo, Context c) {
		list_yahoo.clear();
		file_path.clear();
		this.bitmap_p_yahoo = bitmap_p_yahoo;
		ctx = c;
		for (int i = 0; i < list_thumb.size(); i++)
			if (list_thumb.get(i).getService().equals("public_flick")) {
				list_yahoo.add(list_thumb.get(i));
				file_path.add(list_thumb.get(i).getURL());
			}
	}

	public void addItem(RSSPhotoItem item) {

		list_yahoo.add(0, item);
		file_path.add(0,list_yahoo.get(0).getURL());
		notifyDataSetChanged();
	}

	public void removeItem() {

		list_yahoo.clear();
		notifyDataSetChanged();
	}

	public void setItem(RSSPhotoItem item) {
		list_yahoo.set(0, item);
		this.notifyDataSetChanged();
	}
	
	@Override
	public int getCount() {
		return list_yahoo.size();
	}

	@Override
	public Object getItem(int arg0) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view;
		final int pos = position;
		if (convertView == null) {
			li = (LayoutInflater) ctx
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			view = li.inflate(R.layout.gallery_grid_photos_item, null);
		} else {
			view = convertView;
		}
		//String url = list_yahoo.get(position).getURL();

		ImageView load_iv = (ImageView) view
				.findViewById(R.id.imgGalleryGridItemLoading);
		ImageView iv = (ImageView) view
				.findViewById(R.id.imgGalleryGridItemThumbnail);

		load_iv.setVisibility(View.INVISIBLE);

		try {
			iv.setImageBitmap(bitmap_p_yahoo.get(position));
		} catch (Exception e) {
		}
		iv.setVisibility(View.VISIBLE);

		//final String tmp_url = url;

		iv.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				/*newGallery.pro_gress=ProgressDialog.show(ctx, "", "Please wait...", true, false);
				newGallery n=new newGallery();
				n.Dialog(4000, newGallery.pro_gress);*/
				Intent _intent = new Intent();
				_intent.setClass(ctx, PhimpMeGallery.class);
				//_intent.putExtra("image-path", tmp_url);
				PhimpMeGallery.setFileList(file_path);_intent.putExtra("index", pos);
				_intent.putExtra("activityName", "GridviewAdapter");
				((Activity) ctx).startActivityForResult(_intent, 3);
			}
		});
		return view;
	}

}

class GridDeviantAdapter extends BaseAdapter {
	ArrayList<RSSPhotoItem> list_deviant = new ArrayList<RSSPhotoItem>();
	private LayoutInflater li;
	private ArrayList<Bitmap> bitmap_p_deviant;
	ArrayList<String> file_path = new ArrayList<String>();
	Context ctx;

	public GridDeviantAdapter(ArrayList<RSSPhotoItem> list_thumb,
			ArrayList<Bitmap> bitmap_p_deviant, Context c) {
		list_deviant.clear();
		file_path.clear();	
		this.bitmap_p_deviant = bitmap_p_deviant;
		ctx = c;
		for (int i = 0; i < list_thumb.size(); i++)
			if (list_thumb.get(i).getService().equals("public_deviant")) {
				list_deviant.add(list_thumb.get(i));
				file_path.add(list_thumb.get(i).getURL());
			}
	}

	public void addItem(RSSPhotoItem item) {

		list_deviant.add(0, item);
		file_path.add(0,list_deviant.get(0).getURL());
		notifyDataSetChanged();
	}

	public void removeItem() {

		list_deviant.clear();
		notifyDataSetChanged();
	}

	public void setItem(RSSPhotoItem item) {
		list_deviant.set(0, item);
		this.notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return list_deviant.size();
	}

	@Override
	public Object getItem(int arg0) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view;
		final int pos = position;				
		if (convertView == null) {
			li = (LayoutInflater) ctx
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			view = li.inflate(R.layout.gallery_grid_photos_item, null);
		} else {
			view = convertView;
		}
		//String url = list_deviant.get(position).getURL();

		ImageView load_iv = (ImageView) view
				.findViewById(R.id.imgGalleryGridItemLoading);
		ImageView iv = (ImageView) view
				.findViewById(R.id.imgGalleryGridItemThumbnail);

		load_iv.setVisibility(View.INVISIBLE);

		try {
			iv.setImageBitmap(bitmap_p_deviant.get(position));
		} catch (Exception e) {
		}
		iv.setVisibility(View.VISIBLE);

		//final String tmp_url = url;

		iv.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				/*newGallery.pro_gress=ProgressDialog.show(ctx, "", "Please wait...", true, false);
				newGallery n=new newGallery();
				n.Dialog(4000, newGallery.pro_gress);*/
				Intent _intent = new Intent();
				_intent.setClass(ctx, PhimpMeGallery.class);
				//_intent.putExtra("image-path", tmp_url);
				PhimpMeGallery.setFileList(file_path);_intent.putExtra("index", pos);
				_intent.putExtra("activityName", "GridviewAdapter");
				((Activity) ctx).startActivityForResult(_intent, 3);
			}
		});
		return view;
	}
}

class GridFacebookAdapter extends BaseAdapter {
	ArrayList<RSSPhotoItem_Personal> list_facebook = new ArrayList<RSSPhotoItem_Personal>();
	private LayoutInflater li;
	ArrayList<String> file_path = new ArrayList<String>();
	private ArrayList<Bitmap> bitmap_personal_facebook;
	Context ctx;

	public GridFacebookAdapter(
			ArrayList<RSSPhotoItem_Personal> list_thumb_personal,
			ArrayList<Bitmap> bitmap_personal_facebook, Context c) {
		list_facebook.clear();
		file_path.clear();		
		this.bitmap_personal_facebook = bitmap_personal_facebook;
		ctx = c;
		for (int i = 0; i < list_thumb_personal.size(); i++)
			if (list_thumb_personal.get(i).getService()
					.equals("personal_facebook")) {
				list_facebook.add(list_thumb_personal.get(i));
				file_path.add(list_thumb_personal.get(i).getURL());
			}
	}

	public void addItem(RSSPhotoItem_Personal item) {

		list_facebook.add(0, item);
		file_path.add(0,list_facebook.get(0).getURL());
		notifyDataSetChanged();
	}

	public void removeItem() {

		list_facebook.clear();
		notifyDataSetChanged();
	}

	public void setItem(RSSPhotoItem_Personal item) {
		list_facebook.set(0, item);
		this.notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return list_facebook.size();
	}

	@Override
	public Object getItem(int arg0) {
		return null;
	}
	
	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view;
		final int pos = position;
		if (convertView == null) {
			li = (LayoutInflater) ctx
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			view = li.inflate(R.layout.gallery_grid_photos_item, null);
		} else {
			view = convertView;
		}
		//String url = list_facebook.get(position).getURL();

		ImageView load_iv = (ImageView) view
				.findViewById(R.id.imgGalleryGridItemLoading);
		ImageView iv = (ImageView) view
				.findViewById(R.id.imgGalleryGridItemThumbnail);

		load_iv.setVisibility(View.INVISIBLE);

		try {
			iv.setImageBitmap(bitmap_personal_facebook.get(position));
		} catch (Exception e) {
		}
		iv.setVisibility(View.VISIBLE);

		//final String tmp_url = url;

		iv.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				/*newGallery.pro_gress=ProgressDialog.show(ctx, "", "Please wait...", true, false);
				newGallery n=new newGallery();
				n.Dialog(4000, newGallery.pro_gress);*/
				Intent _intent = new Intent();
				_intent.setClass(ctx, PhimpMeGallery.class);
				//_intent.putExtra("image-path", tmp_url);
				PhimpMeGallery.setFileList(file_path);_intent.putExtra("index", pos);
				_intent.putExtra("activityName", "GridviewAdapter");
				((Activity) ctx).startActivityForResult(_intent, 3);
			}
		});
		return view;
	}
}

class GridTumblrAdapter extends BaseAdapter {
	ArrayList<RSSPhotoItem_Personal> list_tumblr = new ArrayList<RSSPhotoItem_Personal>();
	private LayoutInflater li;
	ArrayList<String> file_path = new ArrayList<String>();
	private ArrayList<Bitmap> bitmap_personal_tumblr;
	Context ctx;

	public GridTumblrAdapter(
			ArrayList<RSSPhotoItem_Personal> list_thumb_personal,
			ArrayList<Bitmap> bitmap_personal_facebook, Context c) {
		list_tumblr.clear();
		file_path.clear();
		this.bitmap_personal_tumblr = bitmap_personal_facebook;
		ctx = c;
		for (int i = 0; i < list_thumb_personal.size(); i++)
			if (list_thumb_personal.get(i).getService()
					.equals("personal_tumblr")) {
				list_tumblr.add(list_thumb_personal.get(i));
				file_path.add(list_thumb_personal.get(i).getURL());
			}
	}

	public void addItem(RSSPhotoItem_Personal item) {

		list_tumblr.add(0, item);
		file_path.add(0,list_tumblr.get(0).getURL());
		notifyDataSetChanged();
	}

	public void removeItem() {

		list_tumblr.clear();
		notifyDataSetChanged();
	}
	
	public void setItem(RSSPhotoItem_Personal item) {
		list_tumblr.set(0, item);
		this.notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return list_tumblr.size();
	}

	@Override
	public Object getItem(int arg0) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view;
		final int pos =position;
		if (convertView == null) {
			li = (LayoutInflater) ctx
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			view = li.inflate(R.layout.gallery_grid_photos_item, null);
		} else {
			view = convertView;
		}
		//String url = list_tumblr.get(position).getURL();

		ImageView load_iv = (ImageView) view
				.findViewById(R.id.imgGalleryGridItemLoading);
		ImageView iv = (ImageView) view
				.findViewById(R.id.imgGalleryGridItemThumbnail);

		load_iv.setVisibility(View.INVISIBLE);

		try {
			iv.setImageBitmap(bitmap_personal_tumblr.get(position));
		} catch (Exception e) {
		}
		iv.setVisibility(View.VISIBLE);

		//final String tmp_url = url;

		iv.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				/*newGallery.pro_gress=ProgressDialog.show(ctx, "", "Please wait...", true, false);
				newGallery n=new newGallery();
				n.Dialog(4000, newGallery.pro_gress);*/
				Intent _intent = new Intent();
				_intent.setClass(ctx, PhimpMeGallery.class);
				//_intent.putExtra("image-path", tmp_url);
				PhimpMeGallery.setFileList(file_path);_intent.putExtra("index", pos);
				_intent.putExtra("activityName", "GridviewAdapter");
				((Activity) ctx).startActivityForResult(_intent, 3);
			}
		});
		return view;
	}
}

class GridVKontakteAdapter extends BaseAdapter {
	ArrayList<RSSPhotoItem_Personal> list_vkontakte = new ArrayList<RSSPhotoItem_Personal>();
	private LayoutInflater li; 
	ArrayList<String> file_path = new ArrayList<String>(); 
	private ArrayList<Bitmap> bitmap_personal_vkontakte;
	Context ctx;

	public GridVKontakteAdapter(
			ArrayList<RSSPhotoItem_Personal> list_vkontakte_personal,
			ArrayList<Bitmap> bitmap_personal_vkontakte, Context c) {
		list_vkontakte.clear();
		file_path.clear();
		this.bitmap_personal_vkontakte = bitmap_personal_vkontakte;
		ctx = c;
		for (int i = 0; i < list_vkontakte_personal.size(); i++)
			if (list_vkontakte_personal.get(i).getService()
					.equals("personal_vkontakte")) {
				list_vkontakte.add(list_vkontakte_personal.get(i));
				file_path.add(list_vkontakte_personal.get(i).getURL());
			}
	}

	public void addItem(RSSPhotoItem_Personal item) {

		list_vkontakte.add(0, item);
		file_path.add(0,list_vkontakte.get(0).getURL());
		notifyDataSetChanged();
	}

	public void removeItem() {

		list_vkontakte.clear();
		notifyDataSetChanged();
	}

	public void setItem(RSSPhotoItem_Personal item) {
		list_vkontakte.set(0, item);
		this.notifyDataSetChanged();
	}
	
	@Override
	public int getCount() {
		return list_vkontakte.size();
	}

	@Override
	public Object getItem(int arg0) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view;
		final int pos = position;
		if (convertView == null) {
			li = (LayoutInflater) ctx
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			view = li.inflate(R.layout.gallery_grid_photos_item, null);
		} else {
			view = convertView;
		}

		// String title = list_thumb.get(position).getTitle();
		//String url = list_vkontakte.get(position).getURL();

		ImageView load_iv = (ImageView) view
				.findViewById(R.id.imgGalleryGridItemLoading);
		ImageView iv = (ImageView) view
				.findViewById(R.id.imgGalleryGridItemThumbnail);

		load_iv.setVisibility(View.INVISIBLE);

		try {
			iv.setImageBitmap(bitmap_personal_vkontakte.get(position));
		} catch (Exception e) {
		}
		iv.setVisibility(View.VISIBLE);

		//final String tmp_url = url;

		iv.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				/*newGallery.pro_gress=ProgressDialog.show(ctx, "", "Please wait...", true, false);
				newGallery n=new newGallery();
				n.Dialog(4000, newGallery.pro_gress);*/
				Intent _intent = new Intent();
				_intent.setClass(ctx, PhimpMeGallery.class);
				PhimpMeGallery.setFileList(file_path);_intent.putExtra("index", pos);
				//_intent.putExtra("image-path", tmp_url);						
				_intent.putExtra("activityName", "GridviewAdapter");
				((Activity) ctx).startActivityForResult(_intent, 3);
			}
		});
		return view;
	}
}

class GridPersonalFlickrAdapter extends BaseAdapter {
	ArrayList<RSSPhotoItem_Personal> list_personal_flickr = new ArrayList<RSSPhotoItem_Personal>();
	private LayoutInflater li;
	ArrayList<String> file_path = new ArrayList<String>();
	private ArrayList<Bitmap> bitmap_personal_flickr;
	Context ctx;

	public GridPersonalFlickrAdapter(
			ArrayList<RSSPhotoItem_Personal> list_flickr_personal,
			ArrayList<Bitmap> bitmap_personal_flickr, Context c) {
		list_personal_flickr.clear();
		file_path.clear();
		this.bitmap_personal_flickr = bitmap_personal_flickr;
		ctx = c;
		for (int i = 0; i < list_flickr_personal.size(); i++)
			if (list_flickr_personal.get(i).getService()
					.equals("personal_flickr")) {
				list_personal_flickr.add(list_flickr_personal.get(i));
				file_path.add(list_flickr_personal.get(i).getURL());
			}
	}

	public void addItem(RSSPhotoItem_Personal item) {

		list_personal_flickr.add(0, item);
		file_path.add(0,list_personal_flickr.get(0).getURL());
		notifyDataSetChanged();
	}

	public void removeItem() {

		list_personal_flickr.clear();
		notifyDataSetChanged();
	}

	public void setItem(RSSPhotoItem_Personal item) {
		list_personal_flickr.set(0, item);
		this.notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return list_personal_flickr.size();
	}

	@Override
	public Object getItem(int arg0) {
		return null;
	}
	
	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view;
		final int pos = position;
		if (convertView == null) {
			li = (LayoutInflater) ctx
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			view = li.inflate(R.layout.gallery_grid_photos_item, null);
		} else {
			view = convertView;
		}
		//String url = list_personal_flickr.get(position).getURL();

		ImageView load_iv = (ImageView) view
				.findViewById(R.id.imgGalleryGridItemLoading);
		ImageView iv = (ImageView) view
				.findViewById(R.id.imgGalleryGridItemThumbnail);

		load_iv.setVisibility(View.INVISIBLE);

		try {
			iv.setImageBitmap(bitmap_personal_flickr.get(position));
		} catch (Exception e) {
		}
		iv.setVisibility(View.VISIBLE);

		//final String tmp_url = url;

		iv.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				/*newGallery.pro_gress=ProgressDialog.show(ctx, "", "Please wait...", true, false);
				newGallery n=new newGallery();
				n.Dialog(4000, newGallery.pro_gress);*/
				Intent _intent = new Intent();
				_intent.setClass(ctx, PhimpMeGallery.class);
				PhimpMeGallery.setFileList(file_path);_intent.putExtra("index", pos);
				//_intent.putExtra("image-path", tmp_url);						
				_intent.putExtra("activityName", "GridviewAdapter");
				((Activity) ctx).startActivityForResult(_intent, 3);
			}
		});
		return view;
	}
}

class GridPersonalPicasaAdapter extends BaseAdapter {
	ArrayList<RSSPhotoItem_Personal> list_picasa = new ArrayList<RSSPhotoItem_Personal>();
	private LayoutInflater li;
	private ArrayList<Bitmap> bitmap_personal_picasa;
	ArrayList<String> file_path = new ArrayList<String>();
	Context ctx;

	public GridPersonalPicasaAdapter(
			ArrayList<RSSPhotoItem_Personal> list_picasa_personal,
			ArrayList<Bitmap> bitmap_personal_picasa, Context c) {
		list_picasa.clear();
		file_path.clear();
		this.bitmap_personal_picasa = bitmap_personal_picasa;
		ctx = c;
		for (int i = 0; i < list_picasa_personal.size(); i++)
			if (list_picasa_personal.get(i).getService()
					.equals("personal_picasa")) {
				list_picasa.add(list_picasa_personal.get(i));
				file_path.add(list_picasa_personal.get(i).getURL());
			}
	}

	public void addItem(RSSPhotoItem_Personal item) {

		list_picasa.add(0, item);
		file_path.add(0,list_picasa.get(0).getURL());		
		notifyDataSetChanged();
	}

	public void removeItem() {

		list_picasa.clear();
		notifyDataSetChanged();
	}

	public void setItem(RSSPhotoItem_Personal item) {
		list_picasa.set(0, item);
		this.notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return list_picasa.size();
	}	
	@Override
	public Object getItem(int arg0) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view;
		final int pos = position;
		if (convertView == null) {
			li = (LayoutInflater) ctx
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			view = li.inflate(R.layout.gallery_grid_photos_item, null);
		} else {
			view = convertView;
		}
		//String url = list_picasa.get(position).getURL();

		ImageView load_iv = (ImageView) view
				.findViewById(R.id.imgGalleryGridItemLoading);
		ImageView iv = (ImageView) view
				.findViewById(R.id.imgGalleryGridItemThumbnail);

		load_iv.setVisibility(View.INVISIBLE);

		try {
			iv.setImageBitmap(bitmap_personal_picasa.get(position));
		} catch (Exception e) {
		}
		iv.setVisibility(View.VISIBLE);

		//final String tmp_url = url;

		iv.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				/*newGallery.pro_gress=ProgressDialog.show(ctx, "", "Please wait...", true, false);
				newGallery n=new newGallery();
				n.Dialog(4000, newGallery.pro_gress);*/
				Intent _intent = new Intent();
				_intent.setClass(ctx, PhimpMeGallery.class);
				PhimpMeGallery.setFileList(file_path);_intent.putExtra("index", pos);
				//_intent.putExtra("image-path", tmp_url);						
				_intent.putExtra("activityName", "GridviewAdapter");
				((Activity) ctx).startActivityForResult(_intent, 3);
			}
		});
		return view;
	}
}

class GridPersonalDeviantArtAdapter extends BaseAdapter {
	ArrayList<RSSPhotoItem_Personal> list_deviantart = new ArrayList<RSSPhotoItem_Personal>();
	private LayoutInflater li;
	ArrayList<String> file_path = new ArrayList<String>();
	private ArrayList<Bitmap> bitmap_personal_deviantart;
	Context ctx;

	public GridPersonalDeviantArtAdapter(
			ArrayList<RSSPhotoItem_Personal> list_deviantart_personal,
			ArrayList<Bitmap> bitmap_personal_deviantart, Context c) {
		list_deviantart.clear();
		file_path.clear();
		this.bitmap_personal_deviantart = bitmap_personal_deviantart;
		ctx = c;
		for (int i = 0; i < list_deviantart_personal.size(); i++)
			if (list_deviantart_personal.get(i).getService()
					.equals("personal_deviantart")) {
				list_deviantart.add(list_deviantart_personal.get(i));
				file_path.add(list_deviantart_personal.get(i).getURL());
			}
	}

	public void addItem(RSSPhotoItem_Personal item) {

		list_deviantart.add(0, item);
		file_path.add(0,list_deviantart.get(0).getURL());		
		notifyDataSetChanged();
	}

	public void setItem(RSSPhotoItem_Personal item) {
		list_deviantart.set(0, item);
		this.notifyDataSetChanged();
	}

	public void removeItem() {

		list_deviantart.clear();
		notifyDataSetChanged();
	}
	
	@Override
	public int getCount() {
		return list_deviantart.size();
	}

	@Override
	public Object getItem(int arg0) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view;
		final int pos = position;
		if (convertView == null) {
			li = (LayoutInflater) ctx
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			view = li.inflate(R.layout.gallery_grid_photos_item, null);
		} else {
			view = convertView;
		}
		//String url = list_deviantart.get(position).getURL();

		ImageView load_iv = (ImageView) view
				.findViewById(R.id.imgGalleryGridItemLoading);
		ImageView iv = (ImageView) view
				.findViewById(R.id.imgGalleryGridItemThumbnail);

		load_iv.setVisibility(View.INVISIBLE);

		try {
			iv.setImageBitmap(bitmap_personal_deviantart.get(position));
		} catch (Exception e) {
		}
		iv.setVisibility(View.VISIBLE);

		//final String tmp_url = url;

		iv.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				/*newGallery.pro_gress=ProgressDialog.show(ctx, "", "Please wait...", true, false);
				newGallery n=new newGallery();
				n.Dialog(4000, newGallery.pro_gress);*/
				Intent _intent = new Intent();
				_intent.setClass(ctx, PhimpMeGallery.class);
				PhimpMeGallery.setFileList(file_path);_intent.putExtra("index", pos);
				//_intent.putExtra("image-path", tmp_url);						
				_intent.putExtra("activityName", "GridviewAdapter");
				((Activity) ctx).startActivityForResult(_intent, 3);
			}
		});
		return view;
	}
}

class GridPersonalImageShackAdapter extends BaseAdapter {
	ArrayList<RSSPhotoItem_Personal> list_imageshack = new ArrayList<RSSPhotoItem_Personal>();
	private LayoutInflater li;
	ArrayList<String> file_path = new ArrayList<String>();
	private ArrayList<Bitmap> bitmap_personal_imageshack;
	Context ctx;

	public GridPersonalImageShackAdapter(
			ArrayList<RSSPhotoItem_Personal> list_imageshack_personal,
			ArrayList<Bitmap> bitmap_personal_imageshack, Context c) {
		list_imageshack.clear();
		file_path.clear();
		this.bitmap_personal_imageshack = bitmap_personal_imageshack;
		ctx = c;
		for (int i = 0; i < list_imageshack_personal.size(); i++)
			if (list_imageshack_personal.get(i).getService()
					.equals("personal_imageshack")) {
				list_imageshack.add(list_imageshack_personal.get(i));
				file_path.add(list_imageshack_personal.get(i).getURL());
			}
	}

	public void addItem(RSSPhotoItem_Personal item) {

		list_imageshack.add(0, item);
		file_path.add(list_imageshack.get(0).getURL());		
		notifyDataSetChanged();
	}

	public void setItem(RSSPhotoItem_Personal item) {
		list_imageshack.set(0, item);
		this.notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return list_imageshack.size();
	}

	@Override
	public Object getItem(int arg0) {
		return null;
	}
	
	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view;
		final int pos = position;
		if (convertView == null) {
			li = (LayoutInflater) ctx
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			view = li.inflate(R.layout.gallery_grid_photos_item, null);
		} else {
			view = convertView;
		}
		//String url = list_imageshack.get(position).getURL();

		ImageView load_iv = (ImageView) view
				.findViewById(R.id.imgGalleryGridItemLoading);
		ImageView iv = (ImageView) view
				.findViewById(R.id.imgGalleryGridItemThumbnail);

		load_iv.setVisibility(View.INVISIBLE);

		try {
			iv.setImageBitmap(bitmap_personal_imageshack.get(position));
		} catch (Exception e) {
		}
		iv.setVisibility(View.VISIBLE);

		//final String tmp_url = url;

		iv.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				/*newGallery.pro_gress=ProgressDialog.show(ctx, "", "Please wait...", true, false);
				newGallery n=new newGallery();
				n.Dialog(4000, newGallery.pro_gress);*/
				Intent _intent = new Intent();
				_intent.setClass(ctx, PhimpMeGallery.class);
				PhimpMeGallery.setFileList(file_path);_intent.putExtra("index", pos);
				//_intent.putExtra("image-path", tmp_url);						
				_intent.putExtra("activityName", "GridviewAdapter");
				((Activity) ctx).startActivityForResult(_intent, 3);
			}
		});
		return view;
	}
}

class GridPersonalImgurAdapter extends BaseAdapter {
	ArrayList<RSSPhotoItem_Personal> list_imgur = new ArrayList<RSSPhotoItem_Personal>();
	private LayoutInflater li;
	ArrayList<String> file_path = new ArrayList<String>();
	private ArrayList<Bitmap> bitmap_personal_imgur;
	Context ctx;

	public GridPersonalImgurAdapter(
			ArrayList<RSSPhotoItem_Personal> list_imgur_personal,
			ArrayList<Bitmap> bitmap_personal_imgur, Context c) {
		list_imgur.clear();
		file_path.clear();
		this.bitmap_personal_imgur = bitmap_personal_imgur;
		ctx = c;
		for (int i = 0; i < list_imgur_personal.size(); i++)
			if (list_imgur_personal.get(i).getService()
					.equals("personal_imgur")) {
				list_imgur.add(list_imgur_personal.get(i));
				file_path.add(list_imgur_personal.get(i).getURL());
			}
	}

	public void addItem(RSSPhotoItem_Personal item) {

		list_imgur.add(0, item);
		file_path.add(0,list_imgur.get(0).getURL());
		notifyDataSetChanged();
	}

	public void removeItem() {

		list_imgur.clear();
		notifyDataSetChanged();
	}

	public void setItem(RSSPhotoItem_Personal item) {
		list_imgur.set(0, item);
		this.notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return list_imgur.size();
	}
	
	@Override
	public Object getItem(int arg0) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view;
		final int pos = position;
		if (convertView == null) {
			li = (LayoutInflater) ctx
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			view = li.inflate(R.layout.gallery_grid_photos_item, null);
		} else {
			view = convertView;
		}

		//String url = list_imgur.get(position).getURL();

		ImageView load_iv = (ImageView) view
				.findViewById(R.id.imgGalleryGridItemLoading);
		ImageView iv = (ImageView) view
				.findViewById(R.id.imgGalleryGridItemThumbnail);

		load_iv.setVisibility(View.INVISIBLE);

		try {
			iv.setImageBitmap(bitmap_personal_imgur.get(position));
		} catch (Exception e) {
		}
		iv.setVisibility(View.VISIBLE);

		//final String tmp_url = url;

		iv.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				/*newGallery.pro_gress=ProgressDialog.show(ctx, "", "Please wait...", true, false);
				newGallery n=new newGallery();
				n.Dialog(4000, newGallery.pro_gress);*/
				Intent _intent = new Intent();
				_intent.setClass(ctx, PhimpMeGallery.class);
				PhimpMeGallery.setFileList(file_path);_intent.putExtra("index", pos);
				//_intent.putExtra("image-path", tmp_url);						
				_intent.putExtra("activityName", "GridviewAdapter");
				((Activity) ctx).startActivityForResult(_intent, 3);
			}
		});
		return view;
	}
}

class GridMyFeedServicesAdaper extends BaseAdapter {
	ArrayList<RSSPhotoItem> list_my_feed_services = new ArrayList<RSSPhotoItem>();
	private LayoutInflater li;
	ArrayList<String> file_path = new ArrayList<String>();
	private ArrayList<Bitmap> bitmap_my_feed_services;
	Context ctx;

	public GridMyFeedServicesAdaper(ArrayList<RSSPhotoItem> list_thumb,
			ArrayList<Bitmap> bitmap_my_feed_services, Context c) {
		list_my_feed_services.clear();
		file_path.clear();
		this.bitmap_my_feed_services = bitmap_my_feed_services;
		ctx = c;
		for (int i = 0; i < list_thumb.size(); i++)
			if (list_thumb.get(i).getService().equals("my_feed_services")) {
				list_my_feed_services.add(list_thumb.get(i));
				file_path.add(0,list_thumb.get(i).getURL());
			}
	}

	public void addItem(RSSPhotoItem item) {

		list_my_feed_services.add(0, item);
		file_path.add(0,list_my_feed_services.get(0).getURL());
		notifyDataSetChanged();
	}

	public void removeItem() {

		list_my_feed_services.clear();
		notifyDataSetChanged();
	}

	public void setItem(RSSPhotoItem item) {
		list_my_feed_services.set(0, item);
		this.notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return list_my_feed_services.size();
	}
	
	@Override
	public Object getItem(int arg0) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view;
		final int pos =position;
		if (convertView == null) {
			li = (LayoutInflater) ctx
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			view = li.inflate(R.layout.gallery_grid_photos_item, null);
		} else {
			view = convertView;
		}

		//String url = list_my_feed_services.get(position).getURL();

		ImageView load_iv = (ImageView) view
				.findViewById(R.id.imgGalleryGridItemLoading);
		ImageView iv = (ImageView) view
				.findViewById(R.id.imgGalleryGridItemThumbnail);
		load_iv.setScaleType(ImageView.ScaleType.FIT_XY);
		load_iv.setVisibility(View.INVISIBLE);

		try {
			iv.setImageBitmap(bitmap_my_feed_services.get(position));
		} catch (Exception e) {
		}
		iv.setVisibility(View.VISIBLE);

		//final String tmp_url = url;

		iv.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				/*newGallery.pro_gress=ProgressDialog.show(ctx, "", "Please wait...", true, false);
				newGallery n=new newGallery();
				n.Dialog(4000, newGallery.pro_gress);*/
				Intent _intent = new Intent();
				_intent.setClass(ctx, PhimpMeGallery.class);
				//_intent.putExtra("image-path", tmp_url);		
				PhimpMeGallery.setFileList(file_path);_intent.putExtra("index", pos);
				_intent.putExtra("activityName", "GridviewAdapter");
				((Activity) ctx).startActivityForResult(_intent, 3);
			}
		});
		return view;
	}
}

class GridMyFeedServicesAdaper1 extends BaseAdapter {
	ArrayList<RSSPhotoItem> list_my_feed_services1 = new ArrayList<RSSPhotoItem>();
	private LayoutInflater li;
	ArrayList<String>file_path = new ArrayList<String>();
	private ArrayList<Bitmap> bitmap_my_feed_services1;
	Context ctx;

	public GridMyFeedServicesAdaper1(ArrayList<RSSPhotoItem> list_thumb,
			ArrayList<Bitmap> bitmap_my_feed_services1, Context c) {
		list_my_feed_services1.clear();
		file_path.clear();		
		this.bitmap_my_feed_services1 = bitmap_my_feed_services1;
		ctx = c;
		for (int i = 0; i < list_thumb.size(); i++)
			if (list_thumb.get(i).getService().equals("my_feed_services1")) {
				list_my_feed_services1.add(list_thumb.get(i));
				file_path.add(list_thumb.get(i).getURL());
			}
	}

	public void addItem(RSSPhotoItem item) {

		list_my_feed_services1.add(0, item);
		file_path.add(0,list_my_feed_services1.get(0).getURL());
		notifyDataSetChanged();
	}

	public void removeItem() {

		list_my_feed_services1.clear();
		notifyDataSetChanged();
	}

	public void setItem(RSSPhotoItem item) {
		list_my_feed_services1.set(0, item);
		this.notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return list_my_feed_services1.size();
	}

	@Override
	public Object getItem(int arg0) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view;
		final int pos = position;
		if (convertView == null) {
			li = (LayoutInflater) ctx
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			view = li.inflate(R.layout.gallery_grid_photos_item, null);
		} else {
			view = convertView;
		}

		//String url = list_my_feed_services1.get(position).getURL();

		ImageView load_iv = (ImageView) view
				.findViewById(R.id.imgGalleryGridItemLoading);
		ImageView iv = (ImageView) view
				.findViewById(R.id.imgGalleryGridItemThumbnail);
		load_iv.setScaleType(ImageView.ScaleType.FIT_XY);
		load_iv.setVisibility(View.INVISIBLE);

		try {
			iv.setImageBitmap(bitmap_my_feed_services1.get(position));
		} catch (Exception e) {
		}
		iv.setVisibility(View.VISIBLE);

		//final String tmp_url = url;

		iv.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				/*newGallery.pro_gress=ProgressDialog.show(ctx, "", "Please wait...", true, false);
				newGallery n=new newGallery();
				n.Dialog(4000, newGallery.pro_gress);*/
				Intent _intent = new Intent();
				_intent.setClass(ctx, PhimpMeGallery.class);
				PhimpMeGallery.setFileList(file_path);_intent.putExtra("index", pos);
				//_intent.putExtra("image-path", tmp_url);						
				_intent.putExtra("activityName", "GridviewAdapter");
				((Activity) ctx).startActivityForResult(_intent, 3);
			}
		});
		return view;
	}
}

class GridMyFeedServicesAdaper2 extends BaseAdapter {
	ArrayList<RSSPhotoItem> list_my_feed_services2 = new ArrayList<RSSPhotoItem>();
	private LayoutInflater li;
	ArrayList<String> file_path = new ArrayList<String>();
	private ArrayList<Bitmap> bitmap_my_feed_services2;
	Context ctx;

	public GridMyFeedServicesAdaper2(ArrayList<RSSPhotoItem> list_thumb,
			ArrayList<Bitmap> bitmap_my_feed_services2, Context c) {
		list_my_feed_services2.clear();
		file_path.clear();
		this.bitmap_my_feed_services2 = bitmap_my_feed_services2;
		ctx = c;
		for (int i = 0; i < list_thumb.size(); i++)
			if (list_thumb.get(i).getService().equals("my_feed_services2")) {
				list_my_feed_services2.add(list_thumb.get(i));
				file_path.add(list_thumb.get(i).getURL());
			}
	}

	public void addItem(RSSPhotoItem item) {

		list_my_feed_services2.add(0, item);
		file_path.add(0,list_my_feed_services2.get(0).getURL()); 
		notifyDataSetChanged();
	}
	
	public void removeItem() {

		list_my_feed_services2.clear();
		notifyDataSetChanged();
	}

	public void setItem(RSSPhotoItem item) {
		list_my_feed_services2.set(0, item);
		this.notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return list_my_feed_services2.size();
	}

	@Override
	public Object getItem(int arg0) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view;
		final int pos = position;
		if (convertView == null) {
			li = (LayoutInflater) ctx
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			view = li.inflate(R.layout.gallery_grid_photos_item, null);
		} else {
			view = convertView;
		}

		//String url = list_my_feed_services2.get(position).getURL();

		ImageView load_iv = (ImageView) view
				.findViewById(R.id.imgGalleryGridItemLoading);
		ImageView iv = (ImageView) view
				.findViewById(R.id.imgGalleryGridItemThumbnail);
		load_iv.setScaleType(ImageView.ScaleType.FIT_XY);
		load_iv.setVisibility(View.INVISIBLE);

		try {
			iv.setImageBitmap(bitmap_my_feed_services2.get(position));
		} catch (Exception e) {
		}
		iv.setVisibility(View.VISIBLE);

		//final String tmp_url = url;

		iv.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				/*newGallery.pro_gress=ProgressDialog.show(ctx, "", "Please wait...", true, false);
				newGallery n=new newGallery();
				n.Dialog(4000, newGallery.pro_gress);*/
				Intent _intent = new Intent();
				_intent.setClass(ctx, PhimpMeGallery.class);
				//_intent.putExtra("image-path", tmp_url);
				PhimpMeGallery.setFileList(file_path);_intent.putExtra("index", pos);
				_intent.putExtra("activityName", "GridviewAdapter");
				((Activity) ctx).startActivityForResult(_intent, 3);
			}
		});
		return view;
	}
}

class GridMyFeedServicesAdaper3 extends BaseAdapter {
	ArrayList<RSSPhotoItem> list_my_feed_services3 = new ArrayList<RSSPhotoItem>();
	private LayoutInflater li;
	ArrayList<String> file_path = new ArrayList<String>();
	private ArrayList<Bitmap> bitmap_my_feed_services3;
	Context ctx;

	public GridMyFeedServicesAdaper3(ArrayList<RSSPhotoItem> list_thumb,
			ArrayList<Bitmap> bitmap_my_feed_services3, Context c) {
		list_my_feed_services3.clear();
		file_path.clear();
		this.bitmap_my_feed_services3 = bitmap_my_feed_services3;
		ctx = c;
		for (int i = 0; i < list_thumb.size(); i++)
			if (list_thumb.get(i).getService().equals("my_feed_services3")) {
				list_my_feed_services3.add(list_thumb.get(i));
				file_path.add(list_thumb.get(i).getURL());
			}
	}

	public void addItem(RSSPhotoItem item) {

		list_my_feed_services3.add(0, item);
		file_path.add(0,list_my_feed_services3.get(0).getURL());
		notifyDataSetChanged();
	}

	public void removeItem() {

		list_my_feed_services3.clear();
		notifyDataSetChanged();
	}

	public void setItem(RSSPhotoItem item) {
		list_my_feed_services3.set(0, item);
		this.notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return list_my_feed_services3.size();
	}
	
	@Override
	public Object getItem(int arg0) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view;
		final int pos = position;
		if (convertView == null) {
			li = (LayoutInflater) ctx
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			view = li.inflate(R.layout.gallery_grid_photos_item, null);
		} else {
			view = convertView;
		}

		//String url = list_my_feed_services3.get(position).getURL();

		ImageView load_iv = (ImageView) view
				.findViewById(R.id.imgGalleryGridItemLoading);
		ImageView iv = (ImageView) view
				.findViewById(R.id.imgGalleryGridItemThumbnail);
		load_iv.setScaleType(ImageView.ScaleType.FIT_XY);
		load_iv.setVisibility(View.INVISIBLE);

		try {
			iv.setImageBitmap(bitmap_my_feed_services3.get(position));
		} catch (Exception e) {
		}
		iv.setVisibility(View.VISIBLE);

		//final String tmp_url = url;

		iv.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				/*newGallery.pro_gress=ProgressDialog.show(ctx, "", "Please wait...", true, false);
				newGallery n=new newGallery();
				n.Dialog(4000, newGallery.pro_gress);*/
				Intent _intent = new Intent();
				_intent.setClass(ctx, PhimpMeGallery.class);
				//_intent.putExtra("image-path", tmp_url);
				PhimpMeGallery.setFileList(file_path);_intent.putExtra("index", pos);
				_intent.putExtra("activityName", "GridviewAdapter");
				((Activity) ctx).startActivityForResult(_intent, 3);
			}
		});
		return view;
	}
}

class GridMyFeedServicesAdaper4 extends BaseAdapter {
	ArrayList<RSSPhotoItem> list_my_feed_services4 = new ArrayList<RSSPhotoItem>();
	private LayoutInflater li;
	ArrayList<String> file_path = new ArrayList<String>(); 
	private ArrayList<Bitmap> bitmap_my_feed_services4;
	Context ctx;

	public GridMyFeedServicesAdaper4(ArrayList<RSSPhotoItem> list_thumb,
			ArrayList<Bitmap> bitmap_my_feed_services4, Context c) {
		list_my_feed_services4.clear();
		file_path.clear();
		this.bitmap_my_feed_services4 = bitmap_my_feed_services4;
		ctx = c;
		for (int i = 0; i < list_thumb.size(); i++)
			if (list_thumb.get(i).getService().equals("my_feed_services4")) {
				list_my_feed_services4.add(list_thumb.get(i));
				file_path.add(list_thumb.get(i).getURL());
			}
	}

	public void addItem(RSSPhotoItem item) {

		list_my_feed_services4.add(0, item);
		file_path.add(0,list_my_feed_services4.get(0).getURL());
		notifyDataSetChanged();
	}

	public void removeItem() {

		list_my_feed_services4.clear();
		notifyDataSetChanged();
	}

	public void setItem(RSSPhotoItem item) {
		list_my_feed_services4.set(0, item);
		this.notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return list_my_feed_services4.size();
	}

	@Override
	public Object getItem(int arg0) {
		return null;
	}
	
	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view;
		final int pos = position;
		if (convertView == null) {
			li = (LayoutInflater) ctx
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			view = li.inflate(R.layout.gallery_grid_photos_item, null);
		} else {
			view = convertView;
		}

		//String url = list_my_feed_services4.get(position).getURL();

		ImageView load_iv = (ImageView) view
				.findViewById(R.id.imgGalleryGridItemLoading);
		ImageView iv = (ImageView) view
				.findViewById(R.id.imgGalleryGridItemThumbnail);
		load_iv.setScaleType(ImageView.ScaleType.FIT_XY);
		load_iv.setVisibility(View.INVISIBLE);

		try {
			iv.setImageBitmap(bitmap_my_feed_services4.get(position));
		} catch (Exception e) {
		}
		iv.setVisibility(View.VISIBLE);

		//final String tmp_url = url;

		iv.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				/*newGallery.pro_gress=ProgressDialog.show(ctx, "", "Please wait...", true, false);
				newGallery n=new newGallery();
				n.Dialog(4000, newGallery.pro_gress);*/
				Intent _intent = new Intent();
				_intent.setClass(ctx, PhimpMeGallery.class);
				//_intent.putExtra("image-path", tmp_url);
				PhimpMeGallery.setFileList(file_path);_intent.putExtra("index", pos);
				_intent.putExtra("activityName", "GridviewAdapter");
				((Activity) ctx).startActivityForResult(_intent, 3);
			}
		});
		return view;
	}
}

class GridMyFeedServicesAdaper5 extends BaseAdapter {
	ArrayList<RSSPhotoItem> list_my_feed_services5 = new ArrayList<RSSPhotoItem>();
	private LayoutInflater li;
	ArrayList<String> file_path = new ArrayList<String>();
	private ArrayList<Bitmap> bitmap_my_feed_services5;
	Context ctx;

	public GridMyFeedServicesAdaper5(ArrayList<RSSPhotoItem> list_thumb,
			ArrayList<Bitmap> bitmap_my_feed_services5, Context c) {
		list_my_feed_services5.clear();
		file_path.clear();
		this.bitmap_my_feed_services5 = bitmap_my_feed_services5;
		ctx = c;
		for (int i = 0; i < list_thumb.size(); i++)
			if (list_thumb.get(i).getService().equals("my_feed_services5")) {
				list_my_feed_services5.add(list_thumb.get(i));
				file_path.add(list_thumb.get(i).getURL());
			}
	}

	public void addItem(RSSPhotoItem item) {

		list_my_feed_services5.add(0, item);
		file_path.add(0,list_my_feed_services5.get(0).getURL());
		notifyDataSetChanged();
	}

	public void removeItem() {

		list_my_feed_services5.clear();
		notifyDataSetChanged();
	}

	public void setItem(RSSPhotoItem item) {
		list_my_feed_services5.set(0, item);
		this.notifyDataSetChanged();
	}
	
	@Override
	public int getCount() {
		return list_my_feed_services5.size();
	}

	@Override
	public Object getItem(int arg0) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view;
		final int pos = position;
		if (convertView == null) {
			li = (LayoutInflater) ctx
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			view = li.inflate(R.layout.gallery_grid_photos_item, null);
		} else {
			view = convertView;
		}

		//String url = list_my_feed_services5.get(position).getURL();

		ImageView load_iv = (ImageView) view
				.findViewById(R.id.imgGalleryGridItemLoading);
		ImageView iv = (ImageView) view
				.findViewById(R.id.imgGalleryGridItemThumbnail);
		load_iv.setScaleType(ImageView.ScaleType.FIT_XY);
		load_iv.setVisibility(View.INVISIBLE);

		try {
			iv.setImageBitmap(bitmap_my_feed_services5.get(position));
		} catch (Exception e) {
		}
		iv.setVisibility(View.VISIBLE);

		//final String tmp_url = url;

		iv.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				/*newGallery.pro_gress=ProgressDialog.show(ctx, "", "Please wait...", true, false);
				newGallery n=new newGallery();
				n.Dialog(4000, newGallery.pro_gress);*/
				Intent _intent = new Intent();
				_intent.setClass(ctx, PhimpMeGallery.class);
				PhimpMeGallery.setFileList(file_path);_intent.putExtra("index", pos);
				//_intent.putExtra("image-path", tmp_url);						
				_intent.putExtra("activityName", "GridviewAdapter");
				((Activity) ctx).startActivityForResult(_intent, 3);
			}
		});
		return view;
	}
}

class GridPersonalKaixinAdapter extends BaseAdapter {
	ArrayList<RSSPhotoItem_Personal> list_kaixin = new ArrayList<RSSPhotoItem_Personal>();
	private LayoutInflater li;
	ArrayList<String> file_path = new ArrayList<String>();
	private ArrayList<Bitmap> bitmap_personal_kaixin;
	Context ctx;

	public GridPersonalKaixinAdapter(
			ArrayList<RSSPhotoItem_Personal> list_kaixin_personal,
			ArrayList<Bitmap> bitmap_personal_kaixin, Context c) {
		list_kaixin.clear();
		file_path.clear();
		this.bitmap_personal_kaixin = bitmap_personal_kaixin;
		ctx = c;
		for (int i = 0; i < list_kaixin_personal.size(); i++)
			if (list_kaixin_personal.get(i).getService()
					.equals("personal_kaixin")) {
				list_kaixin.add(list_kaixin_personal.get(i));
				file_path.add(list_kaixin_personal.get(0).getURL());				
			}
	}

	public void addItem(RSSPhotoItem_Personal item) {

		list_kaixin.add(0, item);
		file_path.add(0,list_kaixin.get(0).getURL());
		notifyDataSetChanged();
	}

	public void removeItem() {

		list_kaixin.clear();
		notifyDataSetChanged();
	}

	public void setItem(RSSPhotoItem_Personal item) {
		list_kaixin.set(0, item);
		this.notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return list_kaixin.size();
	}

	@Override
	public Object getItem(int arg0) {
		return null;
	}
	
	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view;
		final int pos = position;
		if (convertView == null) {
			li = (LayoutInflater) ctx
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			view = li.inflate(R.layout.gallery_grid_photos_item, null);
		} else {
			view = convertView;
		}
		//String url = list_kaixin.get(position).getURL();

		ImageView load_iv = (ImageView) view
				.findViewById(R.id.imgGalleryGridItemLoading);
		ImageView iv = (ImageView) view
				.findViewById(R.id.imgGalleryGridItemThumbnail);

		load_iv.setVisibility(View.INVISIBLE);

		try {
			iv.setImageBitmap(bitmap_personal_kaixin.get(position));
		} catch (Exception e) {
		}
		iv.setVisibility(View.VISIBLE);

		//final String tmp_url = url;
		iv.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				/*newGallery.pro_gress=ProgressDialog.show(ctx, "", "Please wait...", true, false);
				newGallery n=new newGallery();
				n.Dialog(4000, newGallery.pro_gress);*/
				Intent _intent = new Intent();
				_intent.setClass(ctx, PhimpMeGallery.class);
				//_intent.putExtra("image-path", tmp_url);
				PhimpMeGallery.setFileList(file_path);_intent.putExtra("index", pos);
				_intent.putExtra("activityName", "GridviewAdapter");
				((Activity) ctx).startActivityForResult(_intent, 3);
			}
		});
		return view;
	}
}

class GridImgurPublicAdaper extends BaseAdapter {
	ArrayList<RSSPhotoItem> list_imgur = new ArrayList<RSSPhotoItem>();
	private LayoutInflater li;
	ArrayList<String>file_path = new ArrayList<String>();
	private ArrayList<Bitmap> bitmap_p_imgur;
	Context ctx;

	public GridImgurPublicAdaper(ArrayList<RSSPhotoItem> list_thumb,
			ArrayList<Bitmap> bitmap_p_imgur, Context c) {
		list_imgur.clear();
		file_path.clear();		
		this.bitmap_p_imgur = bitmap_p_imgur;
		ctx = c;
		for (int i = 0; i < list_thumb.size(); i++)
			if (list_thumb.get(i).getService().equals("public_imgur")) {
				list_imgur.add(list_thumb.get(i));
				file_path.add(list_thumb.get(0).getURL());
			}
	}

	public void addItem(RSSPhotoItem item) {

		list_imgur.add(0, item);
		file_path.add(0,list_imgur.get(0).getURL());
		notifyDataSetChanged();
	}

	public void removeItem() {

		list_imgur.clear();
		notifyDataSetChanged();
	}

	public void setItem(RSSPhotoItem item) {
		list_imgur.set(0, item);
		this.notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return list_imgur.size();
	}

	@Override
	public Object getItem(int arg0) {
		return null;
	}
	
	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view;
		final int pos  = position;
		if (convertView == null) {
			li = (LayoutInflater) ctx
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			view = li.inflate(R.layout.gallery_grid_photos_item, null);
		} else {
			view = convertView;
		}

		//String url = list_imgur.get(position).getURL();

		ImageView load_iv = (ImageView) view
				.findViewById(R.id.imgGalleryGridItemLoading);
		ImageView iv = (ImageView) view
				.findViewById(R.id.imgGalleryGridItemThumbnail);
		load_iv.setScaleType(ImageView.ScaleType.FIT_XY);
		load_iv.setVisibility(View.INVISIBLE);

		try {
			iv.setImageBitmap(bitmap_p_imgur.get(position));
		} catch (Exception e) {
		}
		iv.setVisibility(View.VISIBLE);

		//final String tmp_url = url;

		iv.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				/*newGallery.pro_gress=ProgressDialog.show(ctx, "", "Please wait...", true, false);
				newGallery n=new newGallery();
				n.Dialog(4000, newGallery.pro_gress);*/
				Intent _intent = new Intent();
				_intent.setClass(ctx, PhimpMeGallery.class);
				//_intent.putExtra("image-path", tmp_url);
				PhimpMeGallery.setFileList(file_path);_intent.putExtra("index", pos);
				_intent.putExtra("activityName", "GridviewAdapter");
				((Activity) ctx).startActivityForResult(_intent, 3);
			}
		});
		return view;
	}

}

class GridPublic500pxAdaper extends BaseAdapter {
	ArrayList<RSSPhotoItem> list_public_500px = new ArrayList<RSSPhotoItem>();
	private LayoutInflater li;
	ArrayList<String>file_path = new ArrayList<String>();
	private ArrayList<Bitmap> bitmap_public_500px;
	Context ctx;

	public GridPublic500pxAdaper(ArrayList<RSSPhotoItem> list_thumb,
			ArrayList<Bitmap> bitmap_public_500px, Context c) {
		list_public_500px.clear();
		file_path.clear();
		this.bitmap_public_500px = bitmap_public_500px;
		ctx = c;
		for (int i = 0; i < list_thumb.size(); i++)
			if (list_thumb.get(i).getService().equals("public_500px")) {
				list_public_500px.add(list_thumb.get(i));
				file_path.add(list_thumb.get(i).getURL());				
			}	
	}

	public void addItem(RSSPhotoItem item) {

		list_public_500px.add(0, item);
		file_path.add(0,list_public_500px.get(0).getURL());
		notifyDataSetChanged();
	}

	public void removeItem() {

		list_public_500px.clear();
		notifyDataSetChanged();
	}

	public void setItem(RSSPhotoItem item) {
		list_public_500px.set(0, item);
		this.notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return list_public_500px.size();
	}

	@Override
	public Object getItem(int arg0) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view;
		final int pos = position;
		if (convertView == null) {
			li = (LayoutInflater) ctx
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			view = li.inflate(R.layout.gallery_grid_photos_item, null);
		} else {
			view = convertView;
		}
		//String url = list_public_500px.get(position).getURL();

		ImageView load_iv = (ImageView) view
				.findViewById(R.id.imgGalleryGridItemLoading);
		ImageView iv = (ImageView) view
				.findViewById(R.id.imgGalleryGridItemThumbnail);

		load_iv.setScaleType(ImageView.ScaleType.FIT_XY);
		load_iv.setVisibility(View.INVISIBLE);

		try {
			iv.setImageBitmap(bitmap_public_500px.get(position));
		} catch (Exception e) {
		}
		iv.setVisibility(View.VISIBLE);

		//final String tmp_url = url;

		iv.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				/*newGallery.pro_gress=ProgressDialog.show(ctx, "", "Please wait...", true, false);
				newGallery n=new newGallery();
				n.Dialog(4000, newGallery.pro_gress);*/
				Intent _intent = new Intent();
				_intent.setClass(ctx, PhimpMeGallery.class);
				//_intent.putExtra("image-path", tmp_url);
				PhimpMeGallery.setFileList(file_path);_intent.putExtra("index", pos);
				_intent.putExtra("activityName", "GridviewAdapter");
				((Activity) ctx).startActivityForResult(_intent, 3);
			}
		});
		return view;
	}

}

class GridPersonal500pxAdapter extends BaseAdapter {
	ArrayList<RSSPhotoItem_Personal> list_500px = new ArrayList<RSSPhotoItem_Personal>();
	private LayoutInflater li;
	ArrayList<String>file_path = new ArrayList<String>();
	private ArrayList<Bitmap> bitmap_personal_500px;
	Context ctx;

	public GridPersonal500pxAdapter(
			ArrayList<RSSPhotoItem_Personal> list_500px_personal,
			ArrayList<Bitmap> bitmap_personal_500px, Context c) {
		list_500px.clear();
		file_path.clear();
		this.bitmap_personal_500px = bitmap_personal_500px;
		ctx = c;
		for (int i = 0; i < list_500px_personal.size(); i++)
			if (list_500px_personal.get(i).getService()
					.equals("personal_500px")) {
				list_500px.add(list_500px_personal.get(i));
				file_path.add(list_500px_personal.get(i).getURL());
			}
	}

	public void addItem(RSSPhotoItem_Personal item) {

		list_500px.add(0, item);
		file_path.add(0,list_500px.get(0).getURL());		
		notifyDataSetChanged();
	}

	public void removeItem() {

		list_500px.clear();
		notifyDataSetChanged();
	}

	public void setItem(RSSPhotoItem_Personal item) {
		list_500px.set(0, item);
		this.notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return list_500px.size();
	}

	@Override
	public Object getItem(int arg0) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view;
		final int pos = position;
		if (convertView == null) {
			li = (LayoutInflater) ctx
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			view = li.inflate(R.layout.gallery_grid_photos_item, null);
		} else {
			view = convertView;
		}

		//String url = list_500px.get(position).getURL();

		ImageView load_iv = (ImageView) view
				.findViewById(R.id.imgGalleryGridItemLoading);
		ImageView iv = (ImageView) view
				.findViewById(R.id.imgGalleryGridItemThumbnail);

		load_iv.setVisibility(View.INVISIBLE);

		try {
			iv.setImageBitmap(bitmap_personal_500px.get(position));
		} catch (Exception e) {
		}
		iv.setVisibility(View.VISIBLE);

		//final String tmp_url = url;
		iv.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				/*newGallery.pro_gress=ProgressDialog.show(ctx, "", "Please wait...", true, false);
				newGallery n=new newGallery();
				n.Dialog(4000, newGallery.pro_gress);*/
				Intent _intent = new Intent();
				_intent.setClass(ctx, PhimpMeGallery.class);
				//_intent.putExtra("image-path", tmp_url);
				PhimpMeGallery.setFileList(file_path);_intent.putExtra("index", pos);
				_intent.putExtra("activityName", "GridviewAdapter");
				((Activity) ctx).startActivityForResult(_intent, 3);
			}
		});

		return view;
	}
}

class GridPersonalSohuAdapter extends BaseAdapter {
	ArrayList<RSSPhotoItem_Personal> list_sohu = new ArrayList<RSSPhotoItem_Personal>();
	private LayoutInflater li;
	ArrayList<String>file_path = new ArrayList<String>();
	private ArrayList<Bitmap> bitmap_personal_sohu;
	Context ctx;

	public GridPersonalSohuAdapter(
			ArrayList<RSSPhotoItem_Personal> list_sohu_personal,
			ArrayList<Bitmap> bitmap_personal_sohu, Context c) {
		list_sohu.clear();
		file_path.clear();
		this.bitmap_personal_sohu = bitmap_personal_sohu;
		ctx = c;
		for (int i = 0; i < list_sohu_personal.size(); i++)
			if (list_sohu_personal.get(i).getService().equals("personal_sohu")) {
				list_sohu.add(list_sohu_personal.get(i));
				file_path.add(list_sohu_personal.get(i).getURL());
			}
	}

	public void addItem(RSSPhotoItem_Personal item) {

		list_sohu.add(0, item);
		file_path.add(0,list_sohu.get(0).getURL());		
		notifyDataSetChanged();
	}

	public void removeItem() {

		list_sohu.clear();
		notifyDataSetChanged();
	}

	public void setItem(RSSPhotoItem_Personal item) {
		list_sohu.set(0, item);
		this.notifyDataSetChanged();
	}
	
	@Override
	public int getCount() {
		return list_sohu.size();
	}

	@Override
	public Object getItem(int arg0) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view;
		final int pos = position;
		if (convertView == null) {
			li = (LayoutInflater) ctx
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			view = li.inflate(R.layout.gallery_grid_photos_item, null);
		} else {
			view = convertView;
		}
		//String url = list_sohu.get(position).getURL();

		ImageView load_iv = (ImageView) view
				.findViewById(R.id.imgGalleryGridItemLoading);
		ImageView iv = (ImageView) view
				.findViewById(R.id.imgGalleryGridItemThumbnail);

		load_iv.setVisibility(View.INVISIBLE);

		try {
			iv.setImageBitmap(bitmap_personal_sohu.get(position));
		} catch (Exception e) {
		}
		iv.setVisibility(View.VISIBLE);

		//final String tmp_url = url;
		iv.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				/*newGallery.pro_gress=ProgressDialog.show(ctx, "", "Please wait...", true, false);
				newGallery n=new newGallery();
				n.Dialog(4000, newGallery.pro_gress);*/
				Intent _intent = new Intent();
				_intent.setClass(ctx, PhimpMeGallery.class);
				PhimpMeGallery.setFileList(file_path);
				_intent.putExtra("index", pos);
				//_intent.putExtra("image-path", tmp_url);						
				_intent.putExtra("activityName", "GridviewAdapter");
				((Activity) ctx).startActivityForResult(_intent, 3);
			}
		});
		return view;
	}
}
