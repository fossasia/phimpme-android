package vn.mbm.phimp.me;

import java.util.ArrayList;
import java.util.HashMap;

import vn.mbm.phimp.me.utils.RSSPhotoItem;
import vn.mbm.phimp.me.utils.RSSPhotoItem_Personal;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.RotateAnimation;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

public class GridviewAdapter {

}

@SuppressLint("ParserError")
class LocalPhotosAdapter extends BaseAdapter {
	private Context context;
	private ArrayList<String> filepath;
	LayoutInflater li;
	HashMap<Integer, Matrix> mImageTransforms = new HashMap<Integer,Matrix>();
	Matrix mIdentityMatrix = new Matrix();
	public LocalPhotosAdapter(Context localContext, ArrayList<String> filepath) {
		this.context = localContext;
		this.filepath = filepath;
	}

	public int getCount() {
		return filepath.size();

	}

	public void removeItem() {

		filepath.clear();
		notifyDataSetChanged();
	}

	public Object getItem(int position) {
		return position;
	}

	public long getItemId(int position) {
		return position;
	}
	
	public View getView(int position, View convertView, ViewGroup parent) {
        ImageView picturesView;
        
        if (convertView == null) {			
            picturesView = new ImageView(context);
            String url = filepath.get(position);	
            picturesView.setImageURI(Uri.withAppendedPath(
                    MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI, "" + url));
            
            
            
            try {
				int orient=getOrientation(context, position);
				Log.i("GV_Adapter","orientation : "+orient);
				
	            picturesView.setPadding(2, 2, 2, 2);
	            picturesView.setScaleType(ScaleType.FIT_XY);	            
	            picturesView.setLayoutParams(new GridView.LayoutParams(PhimpMe.width, PhimpMe.height));
	            
	            try{
	            	picturesView.setRotation(orient);
	            }catch(NoSuchMethodError n){
	            	
	            }
	            
			} catch (Exception e) {

			}                   
        }
        else {
            picturesView = (ImageView)convertView;
        }        
        return picturesView;

    }
	public static int getOrientation(Context context, int position) throws Exception {
		
		String[] projection = {MediaStore.Images.Media.DATA};
        

	    Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
	    		projection, null, null, MediaStore.Images.Media._ID+ " DESC");
	    int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
	    cursor.moveToPosition(position);
        

        String imagePath = cursor.getString(columnIndex);
        cursor.close();
        ExifInterface exif=new ExifInterface(imagePath);
		int orientation=exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);		
		if(orientation==3) return 180;
		else if(orientation==6) return 90;
		else if(orientation==8) return 270;
		else  return 0;
		
       
	}
}

class GridFlickrAdaper extends BaseAdapter {
	ArrayList<RSSPhotoItem> list_flickr = new ArrayList<RSSPhotoItem>();
	private LayoutInflater li;
	private ArrayList<Bitmap> bitmap_p_flickr;
	Context ctx;

	public GridFlickrAdaper(ArrayList<RSSPhotoItem> list_thumb,
			ArrayList<Bitmap> bitmap_p_flickr, Context c) {
		list_flickr.clear();
		this.bitmap_p_flickr = bitmap_p_flickr;
		ctx = c;
		for (int i = 0; i < list_thumb.size(); i++)
			if (list_thumb.get(i).getService().equals("public_flickr")) {
				list_flickr.add(list_thumb.get(i));
			}
	}

	public void addItem(RSSPhotoItem item) {

		list_flickr.add(0, item);
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
		if (convertView == null) {
			li = (LayoutInflater) ctx
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			view = li.inflate(R.layout.gallery_grid_photos_item, null);
		} else {
			view = convertView;
		}
		String url = list_flickr.get(position).getURL();
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

		final String tmp_url = url;

		iv.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				newGallery.pro_gress=ProgressDialog.show(ctx, "", "Please wait...", true, false);
				newGallery n=new newGallery();
				n.Dialog(4000, newGallery.pro_gress);
				Intent _intent = new Intent();
				_intent.setClass(ctx, PhimpMeGallery.class);
				_intent.putExtra("image-path", tmp_url);						
				_intent.putExtra("activityName", "GridviewAdapter");
				((Activity) ctx).startActivityForResult(_intent, 3);
			}
		});
		return view;
	}

}

class GridRecentFlickrAdaper extends BaseAdapter {
	ArrayList<RSSPhotoItem> list_recent_flickr = new ArrayList<RSSPhotoItem>();
	private LayoutInflater li;
	private ArrayList<Bitmap> bitmap_recent_flickr;
	Context ctx;

	public GridRecentFlickrAdaper(ArrayList<RSSPhotoItem> list_thumb,
			ArrayList<Bitmap> bitmap_recent_flickr, Context c) {
		list_recent_flickr.clear();
		this.bitmap_recent_flickr = bitmap_recent_flickr;
		ctx = c;
		for (int i = 0; i < list_thumb.size(); i++)
			if (list_thumb.get(i).getService().equals("recent_flickr")) {
				list_recent_flickr.add(list_thumb.get(i));
			}
	}

	public void addItem(RSSPhotoItem item) {

		list_recent_flickr.add(0, item);
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

		final String tmp_url = url;

		iv.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				newGallery.pro_gress=ProgressDialog.show(ctx, "", "Please wait...", true, false);
				newGallery n=new newGallery();
				n.Dialog(4000, newGallery.pro_gress);
				Intent _intent = new Intent();
				_intent.setClass(ctx, PhimpMeGallery.class);
				_intent.putExtra("image-path", tmp_url);						
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
	private ArrayList<Bitmap> bitmap_public_picasa;
	Context ctx;

	public GridPublicPicasaAdaper(ArrayList<RSSPhotoItem> list_thumb,
			ArrayList<Bitmap> bitmap_public_picasa, Context c) {
		list_public_picasa.clear();
		this.bitmap_public_picasa = bitmap_public_picasa;
		ctx = c;
		for (int i = 0; i < list_thumb.size(); i++)
			if (list_thumb.get(i).getService().equals("public_picasa")) {
				list_public_picasa.add(list_thumb.get(i));
			}
	}

	public void addItem(RSSPhotoItem item) {

		list_public_picasa.add(0, item);
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
		if (convertView == null) {
			li = (LayoutInflater) ctx
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			view = li.inflate(R.layout.gallery_grid_photos_item, null);
		} else {
			view = convertView;
		}
		String url = list_public_picasa.get(position).getURL();

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

		final String tmp_url = url;

		iv.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				newGallery.pro_gress=ProgressDialog.show(ctx, "", "Please wait...", true, false);
				newGallery n=new newGallery();
				n.Dialog(4000, newGallery.pro_gress);
				Intent _intent = new Intent();
				_intent.setClass(ctx, PhimpMeGallery.class);
				_intent.putExtra("image-path", tmp_url);						
				_intent.putExtra("activityName", "GridviewAdapter");
				((Activity) ctx).startActivityForResult(_intent, 3);
			}
		});
		return view;
	}

}

class GridGoogleNewsAdaper extends BaseAdapter {
	ArrayList<RSSPhotoItem> list_google_news = new ArrayList<RSSPhotoItem>();
	private LayoutInflater li;
	private ArrayList<Bitmap> bitmap_google_news;
	Context ctx;

	public GridGoogleNewsAdaper(ArrayList<RSSPhotoItem> list_thumb,
			ArrayList<Bitmap> bitmap_google_news, Context c) {
		list_google_news.clear();
		this.bitmap_google_news = bitmap_google_news;
		ctx = c;
		for (int i = 0; i < list_thumb.size(); i++)
			if (list_thumb.get(i).getService().equals("google_news")) {
				list_google_news.add(list_thumb.get(i));
			}
	}

	public void addItem(RSSPhotoItem item) {

		list_google_news.add(0, item);
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
		if (convertView == null) {
			li = (LayoutInflater) ctx
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			view = li.inflate(R.layout.gallery_grid_photos_item, null);
		} else {
			view = convertView;
		}
		String url = list_google_news.get(position).getURL();

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

		final String tmp_url = url;

		iv.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				newGallery.pro_gress=ProgressDialog.show(ctx, "", "Please wait...", true, false);
				newGallery n=new newGallery();
				n.Dialog(4000, newGallery.pro_gress);
				Intent _intent = new Intent();
				_intent.setClass(ctx, PhimpMeGallery.class);
				_intent.putExtra("image-path", tmp_url);						
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
	private ArrayList<Bitmap> bitmap_p_yahoo;
	Context ctx;

	public GridYahooAdapter(ArrayList<RSSPhotoItem> list_thumb,
			ArrayList<Bitmap> bitmap_p_yahoo, Context c) {
		list_yahoo.clear();
		this.bitmap_p_yahoo = bitmap_p_yahoo;
		ctx = c;
		for (int i = 0; i < list_thumb.size(); i++)
			if (list_thumb.get(i).getService().equals("public_flick")) {
				list_yahoo.add(list_thumb.get(i));
			}
	}

	public void addItem(RSSPhotoItem item) {

		list_yahoo.add(0, item);
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
		if (convertView == null) {
			li = (LayoutInflater) ctx
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			view = li.inflate(R.layout.gallery_grid_photos_item, null);
		} else {
			view = convertView;
		}
		String url = list_yahoo.get(position).getURL();

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

		final String tmp_url = url;

		iv.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				newGallery.pro_gress=ProgressDialog.show(ctx, "", "Please wait...", true, false);
				newGallery n=new newGallery();
				n.Dialog(4000, newGallery.pro_gress);
				Intent _intent = new Intent();
				_intent.setClass(ctx, PhimpMeGallery.class);
				_intent.putExtra("image-path", tmp_url);						
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
	Context ctx;

	public GridDeviantAdapter(ArrayList<RSSPhotoItem> list_thumb,
			ArrayList<Bitmap> bitmap_p_deviant, Context c) {
		list_deviant.clear();
		this.bitmap_p_deviant = bitmap_p_deviant;
		ctx = c;
		for (int i = 0; i < list_thumb.size(); i++)
			if (list_thumb.get(i).getService().equals("public_deviant")) {
				list_deviant.add(list_thumb.get(i));
			}
	}

	public void addItem(RSSPhotoItem item) {

		list_deviant.add(0, item);
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
		if (convertView == null) {
			li = (LayoutInflater) ctx
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			view = li.inflate(R.layout.gallery_grid_photos_item, null);
		} else {
			view = convertView;
		}
		String url = list_deviant.get(position).getURL();

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

		final String tmp_url = url;

		iv.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				newGallery.pro_gress=ProgressDialog.show(ctx, "", "Please wait...", true, false);
				newGallery n=new newGallery();
				n.Dialog(4000, newGallery.pro_gress);
				Intent _intent = new Intent();
				_intent.setClass(ctx, PhimpMeGallery.class);
				_intent.putExtra("image-path", tmp_url);						
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
	private ArrayList<Bitmap> bitmap_personal_facebook;
	Context ctx;

	public GridFacebookAdapter(
			ArrayList<RSSPhotoItem_Personal> list_thumb_personal,
			ArrayList<Bitmap> bitmap_personal_facebook, Context c) {
		list_facebook.clear();
		this.bitmap_personal_facebook = bitmap_personal_facebook;
		ctx = c;
		for (int i = 0; i < list_thumb_personal.size(); i++)
			if (list_thumb_personal.get(i).getService()
					.equals("personal_facebook")) {
				list_facebook.add(list_thumb_personal.get(i));
			}
	}

	public void addItem(RSSPhotoItem_Personal item) {

		list_facebook.add(0, item);
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
		if (convertView == null) {
			li = (LayoutInflater) ctx
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			view = li.inflate(R.layout.gallery_grid_photos_item, null);
		} else {
			view = convertView;
		}
		String url = list_facebook.get(position).getURL();

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

		final String tmp_url = url;

		iv.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				newGallery.pro_gress=ProgressDialog.show(ctx, "", "Please wait...", true, false);
				newGallery n=new newGallery();
				n.Dialog(4000, newGallery.pro_gress);
				Intent _intent = new Intent();
				_intent.setClass(ctx, PhimpMeGallery.class);
				_intent.putExtra("image-path", tmp_url);						
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
	private ArrayList<Bitmap> bitmap_personal_tumblr;
	Context ctx;

	public GridTumblrAdapter(
			ArrayList<RSSPhotoItem_Personal> list_thumb_personal,
			ArrayList<Bitmap> bitmap_personal_facebook, Context c) {
		list_tumblr.clear();
		this.bitmap_personal_tumblr = bitmap_personal_facebook;
		ctx = c;
		for (int i = 0; i < list_thumb_personal.size(); i++)
			if (list_thumb_personal.get(i).getService()
					.equals("personal_tumblr")) {
				list_tumblr.add(list_thumb_personal.get(i));
			}
	}

	public void addItem(RSSPhotoItem_Personal item) {

		list_tumblr.add(0, item);
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
		if (convertView == null) {
			li = (LayoutInflater) ctx
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			view = li.inflate(R.layout.gallery_grid_photos_item, null);
		} else {
			view = convertView;
		}
		String url = list_tumblr.get(position).getURL();

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

		final String tmp_url = url;

		iv.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				newGallery.pro_gress=ProgressDialog.show(ctx, "", "Please wait...", true, false);
				newGallery n=new newGallery();
				n.Dialog(4000, newGallery.pro_gress);
				Intent _intent = new Intent();
				_intent.setClass(ctx, PhimpMeGallery.class);
				_intent.putExtra("image-path", tmp_url);						
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
	private ArrayList<Bitmap> bitmap_personal_vkontakte;
	Context ctx;

	public GridVKontakteAdapter(
			ArrayList<RSSPhotoItem_Personal> list_vkontakte_personal,
			ArrayList<Bitmap> bitmap_personal_vkontakte, Context c) {
		list_vkontakte.clear();
		this.bitmap_personal_vkontakte = bitmap_personal_vkontakte;
		ctx = c;
		for (int i = 0; i < list_vkontakte_personal.size(); i++)
			if (list_vkontakte_personal.get(i).getService()
					.equals("personal_vkontakte")) {
				list_vkontakte.add(list_vkontakte_personal.get(i));
			}
	}

	public void addItem(RSSPhotoItem_Personal item) {

		list_vkontakte.add(0, item);
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
		if (convertView == null) {
			li = (LayoutInflater) ctx
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			view = li.inflate(R.layout.gallery_grid_photos_item, null);
		} else {
			view = convertView;
		}

		// String title = list_thumb.get(position).getTitle();
		String url = list_vkontakte.get(position).getURL();

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

		final String tmp_url = url;

		iv.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				newGallery.pro_gress=ProgressDialog.show(ctx, "", "Please wait...", true, false);
				newGallery n=new newGallery();
				n.Dialog(4000, newGallery.pro_gress);
				Intent _intent = new Intent();
				_intent.setClass(ctx, PhimpMeGallery.class);
				_intent.putExtra("image-path", tmp_url);						
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
	private ArrayList<Bitmap> bitmap_personal_flickr;
	Context ctx;

	public GridPersonalFlickrAdapter(
			ArrayList<RSSPhotoItem_Personal> list_flickr_personal,
			ArrayList<Bitmap> bitmap_personal_flickr, Context c) {
		list_personal_flickr.clear();
		this.bitmap_personal_flickr = bitmap_personal_flickr;
		ctx = c;
		for (int i = 0; i < list_flickr_personal.size(); i++)
			if (list_flickr_personal.get(i).getService()
					.equals("personal_flickr")) {
				list_personal_flickr.add(list_flickr_personal.get(i));
			}
	}

	public void addItem(RSSPhotoItem_Personal item) {

		list_personal_flickr.add(0, item);
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
		if (convertView == null) {
			li = (LayoutInflater) ctx
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			view = li.inflate(R.layout.gallery_grid_photos_item, null);
		} else {
			view = convertView;
		}
		String url = list_personal_flickr.get(position).getURL();

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

		final String tmp_url = url;

		iv.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				newGallery.pro_gress=ProgressDialog.show(ctx, "", "Please wait...", true, false);
				newGallery n=new newGallery();
				n.Dialog(4000, newGallery.pro_gress);
				Intent _intent = new Intent();
				_intent.setClass(ctx, PhimpMeGallery.class);
				_intent.putExtra("image-path", tmp_url);						
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
	Context ctx;

	public GridPersonalPicasaAdapter(
			ArrayList<RSSPhotoItem_Personal> list_picasa_personal,
			ArrayList<Bitmap> bitmap_personal_picasa, Context c) {
		list_picasa.clear();
		this.bitmap_personal_picasa = bitmap_personal_picasa;
		ctx = c;
		for (int i = 0; i < list_picasa_personal.size(); i++)
			if (list_picasa_personal.get(i).getService()
					.equals("personal_picasa")) {
				list_picasa.add(list_picasa_personal.get(i));
			}
	}

	public void addItem(RSSPhotoItem_Personal item) {

		list_picasa.add(0, item);
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
		if (convertView == null) {
			li = (LayoutInflater) ctx
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			view = li.inflate(R.layout.gallery_grid_photos_item, null);
		} else {
			view = convertView;
		}
		String url = list_picasa.get(position).getURL();

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

		final String tmp_url = url;

		iv.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				newGallery.pro_gress=ProgressDialog.show(ctx, "", "Please wait...", true, false);
				newGallery n=new newGallery();
				n.Dialog(4000, newGallery.pro_gress);
				Intent _intent = new Intent();
				_intent.setClass(ctx, PhimpMeGallery.class);
				_intent.putExtra("image-path", tmp_url);						
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
	private ArrayList<Bitmap> bitmap_personal_deviantart;
	Context ctx;

	public GridPersonalDeviantArtAdapter(
			ArrayList<RSSPhotoItem_Personal> list_deviantart_personal,
			ArrayList<Bitmap> bitmap_personal_deviantart, Context c) {
		list_deviantart.clear();
		this.bitmap_personal_deviantart = bitmap_personal_deviantart;
		ctx = c;
		for (int i = 0; i < list_deviantart_personal.size(); i++)
			if (list_deviantart_personal.get(i).getService()
					.equals("personal_deviantart")) {
				list_deviantart.add(list_deviantart_personal.get(i));
			}
	}

	public void addItem(RSSPhotoItem_Personal item) {

		list_deviantart.add(0, item);
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
		if (convertView == null) {
			li = (LayoutInflater) ctx
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			view = li.inflate(R.layout.gallery_grid_photos_item, null);
		} else {
			view = convertView;
		}
		String url = list_deviantart.get(position).getURL();

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

		final String tmp_url = url;

		iv.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				newGallery.pro_gress=ProgressDialog.show(ctx, "", "Please wait...", true, false);
				newGallery n=new newGallery();
				n.Dialog(4000, newGallery.pro_gress);
				Intent _intent = new Intent();
				_intent.setClass(ctx, PhimpMeGallery.class);
				_intent.putExtra("image-path", tmp_url);						
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
	private ArrayList<Bitmap> bitmap_personal_imageshack;
	Context ctx;

	public GridPersonalImageShackAdapter(
			ArrayList<RSSPhotoItem_Personal> list_imageshack_personal,
			ArrayList<Bitmap> bitmap_personal_imageshack, Context c) {
		list_imageshack.clear();
		this.bitmap_personal_imageshack = bitmap_personal_imageshack;
		ctx = c;
		for (int i = 0; i < list_imageshack_personal.size(); i++)
			if (list_imageshack_personal.get(i).getService()
					.equals("personal_imageshack")) {
				list_imageshack.add(list_imageshack_personal.get(i));
			}
	}

	public void addItem(RSSPhotoItem_Personal item) {

		list_imageshack.add(0, item);
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
		if (convertView == null) {
			li = (LayoutInflater) ctx
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			view = li.inflate(R.layout.gallery_grid_photos_item, null);
		} else {
			view = convertView;
		}
		String url = list_imageshack.get(position).getURL();

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

		final String tmp_url = url;

		iv.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				newGallery.pro_gress=ProgressDialog.show(ctx, "", "Please wait...", true, false);
				newGallery n=new newGallery();
				n.Dialog(4000, newGallery.pro_gress);
				Intent _intent = new Intent();
				_intent.setClass(ctx, PhimpMeGallery.class);
				_intent.putExtra("image-path", tmp_url);						
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
	private ArrayList<Bitmap> bitmap_personal_imgur;
	Context ctx;

	public GridPersonalImgurAdapter(
			ArrayList<RSSPhotoItem_Personal> list_imgur_personal,
			ArrayList<Bitmap> bitmap_personal_imgur, Context c) {
		list_imgur.clear();
		this.bitmap_personal_imgur = bitmap_personal_imgur;
		ctx = c;
		for (int i = 0; i < list_imgur_personal.size(); i++)
			if (list_imgur_personal.get(i).getService()
					.equals("personal_imgur")) {
				list_imgur.add(list_imgur_personal.get(i));
			}
	}

	public void addItem(RSSPhotoItem_Personal item) {

		list_imgur.add(0, item);
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
		if (convertView == null) {
			li = (LayoutInflater) ctx
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			view = li.inflate(R.layout.gallery_grid_photos_item, null);
		} else {
			view = convertView;
		}

		String url = list_imgur.get(position).getURL();

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

		final String tmp_url = url;

		iv.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				newGallery.pro_gress=ProgressDialog.show(ctx, "", "Please wait...", true, false);
				newGallery n=new newGallery();
				n.Dialog(4000, newGallery.pro_gress);
				Intent _intent = new Intent();
				_intent.setClass(ctx, PhimpMeGallery.class);
				_intent.putExtra("image-path", tmp_url);						
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
	private ArrayList<Bitmap> bitmap_my_feed_services;
	Context ctx;

	public GridMyFeedServicesAdaper(ArrayList<RSSPhotoItem> list_thumb,
			ArrayList<Bitmap> bitmap_my_feed_services, Context c) {
		list_my_feed_services.clear();
		this.bitmap_my_feed_services = bitmap_my_feed_services;
		ctx = c;
		for (int i = 0; i < list_thumb.size(); i++)
			if (list_thumb.get(i).getService().equals("my_feed_services")) {
				list_my_feed_services.add(list_thumb.get(i));
			}
	}

	public void addItem(RSSPhotoItem item) {

		list_my_feed_services.add(0, item);
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

		if (convertView == null) {
			li = (LayoutInflater) ctx
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			view = li.inflate(R.layout.gallery_grid_photos_item, null);
		} else {
			view = convertView;
		}

		String url = list_my_feed_services.get(position).getURL();

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

		final String tmp_url = url;

		iv.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				newGallery.pro_gress=ProgressDialog.show(ctx, "", "Please wait...", true, false);
				newGallery n=new newGallery();
				n.Dialog(4000, newGallery.pro_gress);
				Intent _intent = new Intent();
				_intent.setClass(ctx, PhimpMeGallery.class);
				_intent.putExtra("image-path", tmp_url);						
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
	private ArrayList<Bitmap> bitmap_my_feed_services1;
	Context ctx;

	public GridMyFeedServicesAdaper1(ArrayList<RSSPhotoItem> list_thumb,
			ArrayList<Bitmap> bitmap_my_feed_services1, Context c) {
		list_my_feed_services1.clear();
		this.bitmap_my_feed_services1 = bitmap_my_feed_services1;
		ctx = c;
		for (int i = 0; i < list_thumb.size(); i++)
			if (list_thumb.get(i).getService().equals("my_feed_services1")) {
				list_my_feed_services1.add(list_thumb.get(i));
			}
	}

	public void addItem(RSSPhotoItem item) {

		list_my_feed_services1.add(0, item);
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

		if (convertView == null) {
			li = (LayoutInflater) ctx
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			view = li.inflate(R.layout.gallery_grid_photos_item, null);
		} else {
			view = convertView;
		}

		String url = list_my_feed_services1.get(position).getURL();

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

		final String tmp_url = url;

		iv.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				newGallery.pro_gress=ProgressDialog.show(ctx, "", "Please wait...", true, false);
				newGallery n=new newGallery();
				n.Dialog(4000, newGallery.pro_gress);
				Intent _intent = new Intent();
				_intent.setClass(ctx, PhimpMeGallery.class);
				_intent.putExtra("image-path", tmp_url);						
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
	private ArrayList<Bitmap> bitmap_my_feed_services2;
	Context ctx;

	public GridMyFeedServicesAdaper2(ArrayList<RSSPhotoItem> list_thumb,
			ArrayList<Bitmap> bitmap_my_feed_services2, Context c) {
		list_my_feed_services2.clear();
		this.bitmap_my_feed_services2 = bitmap_my_feed_services2;
		ctx = c;
		for (int i = 0; i < list_thumb.size(); i++)
			if (list_thumb.get(i).getService().equals("my_feed_services2")) {
				list_my_feed_services2.add(list_thumb.get(i));
			}
	}

	public void addItem(RSSPhotoItem item) {

		list_my_feed_services2.add(0, item);
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

		if (convertView == null) {
			li = (LayoutInflater) ctx
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			view = li.inflate(R.layout.gallery_grid_photos_item, null);
		} else {
			view = convertView;
		}

		String url = list_my_feed_services2.get(position).getURL();

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

		final String tmp_url = url;

		iv.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				newGallery.pro_gress=ProgressDialog.show(ctx, "", "Please wait...", true, false);
				newGallery n=new newGallery();
				n.Dialog(4000, newGallery.pro_gress);
				Intent _intent = new Intent();
				_intent.setClass(ctx, PhimpMeGallery.class);
				_intent.putExtra("image-path", tmp_url);						
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
	private ArrayList<Bitmap> bitmap_my_feed_services3;
	Context ctx;

	public GridMyFeedServicesAdaper3(ArrayList<RSSPhotoItem> list_thumb,
			ArrayList<Bitmap> bitmap_my_feed_services3, Context c) {
		list_my_feed_services3.clear();
		this.bitmap_my_feed_services3 = bitmap_my_feed_services3;
		ctx = c;
		for (int i = 0; i < list_thumb.size(); i++)
			if (list_thumb.get(i).getService().equals("my_feed_services3")) {
				list_my_feed_services3.add(list_thumb.get(i));
			}
	}

	public void addItem(RSSPhotoItem item) {

		list_my_feed_services3.add(0, item);
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

		if (convertView == null) {
			li = (LayoutInflater) ctx
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			view = li.inflate(R.layout.gallery_grid_photos_item, null);
		} else {
			view = convertView;
		}

		String url = list_my_feed_services3.get(position).getURL();

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

		final String tmp_url = url;

		iv.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				newGallery.pro_gress=ProgressDialog.show(ctx, "", "Please wait...", true, false);
				newGallery n=new newGallery();
				n.Dialog(4000, newGallery.pro_gress);
				Intent _intent = new Intent();
				_intent.setClass(ctx, PhimpMeGallery.class);
				_intent.putExtra("image-path", tmp_url);						
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
	private ArrayList<Bitmap> bitmap_my_feed_services4;
	Context ctx;

	public GridMyFeedServicesAdaper4(ArrayList<RSSPhotoItem> list_thumb,
			ArrayList<Bitmap> bitmap_my_feed_services4, Context c) {
		list_my_feed_services4.clear();
		this.bitmap_my_feed_services4 = bitmap_my_feed_services4;
		ctx = c;
		for (int i = 0; i < list_thumb.size(); i++)
			if (list_thumb.get(i).getService().equals("my_feed_services4")) {
				list_my_feed_services4.add(list_thumb.get(i));
			}
	}

	public void addItem(RSSPhotoItem item) {

		list_my_feed_services4.add(0, item);
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

		if (convertView == null) {
			li = (LayoutInflater) ctx
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			view = li.inflate(R.layout.gallery_grid_photos_item, null);
		} else {
			view = convertView;
		}

		String url = list_my_feed_services4.get(position).getURL();

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

		final String tmp_url = url;

		iv.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				newGallery.pro_gress=ProgressDialog.show(ctx, "", "Please wait...", true, false);
				newGallery n=new newGallery();
				n.Dialog(4000, newGallery.pro_gress);
				Intent _intent = new Intent();
				_intent.setClass(ctx, PhimpMeGallery.class);
				_intent.putExtra("image-path", tmp_url);						
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
	private ArrayList<Bitmap> bitmap_my_feed_services5;
	Context ctx;

	public GridMyFeedServicesAdaper5(ArrayList<RSSPhotoItem> list_thumb,
			ArrayList<Bitmap> bitmap_my_feed_services5, Context c) {
		list_my_feed_services5.clear();
		this.bitmap_my_feed_services5 = bitmap_my_feed_services5;
		ctx = c;
		for (int i = 0; i < list_thumb.size(); i++)
			if (list_thumb.get(i).getService().equals("my_feed_services5")) {
				list_my_feed_services5.add(list_thumb.get(i));
			}
	}

	public void addItem(RSSPhotoItem item) {

		list_my_feed_services5.add(0, item);
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

		if (convertView == null) {
			li = (LayoutInflater) ctx
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			view = li.inflate(R.layout.gallery_grid_photos_item, null);
		} else {
			view = convertView;
		}

		String url = list_my_feed_services5.get(position).getURL();

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

		final String tmp_url = url;

		iv.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				newGallery.pro_gress=ProgressDialog.show(ctx, "", "Please wait...", true, false);
				newGallery n=new newGallery();
				n.Dialog(4000, newGallery.pro_gress);
				Intent _intent = new Intent();
				_intent.setClass(ctx, PhimpMeGallery.class);
				_intent.putExtra("image-path", tmp_url);						
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
	private ArrayList<Bitmap> bitmap_personal_kaixin;
	Context ctx;

	public GridPersonalKaixinAdapter(
			ArrayList<RSSPhotoItem_Personal> list_kaixin_personal,
			ArrayList<Bitmap> bitmap_personal_kaixin, Context c) {
		list_kaixin.clear();
		this.bitmap_personal_kaixin = bitmap_personal_kaixin;
		ctx = c;
		for (int i = 0; i < list_kaixin_personal.size(); i++)
			if (list_kaixin_personal.get(i).getService()
					.equals("personal_kaixin")) {
				list_kaixin.add(list_kaixin_personal.get(i));
			}
	}

	public void addItem(RSSPhotoItem_Personal item) {

		list_kaixin.add(0, item);
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
		if (convertView == null) {
			li = (LayoutInflater) ctx
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			view = li.inflate(R.layout.gallery_grid_photos_item, null);
		} else {
			view = convertView;
		}
		String url = list_kaixin.get(position).getURL();

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

		final String tmp_url = url;
		iv.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				newGallery.pro_gress=ProgressDialog.show(ctx, "", "Please wait...", true, false);
				newGallery n=new newGallery();
				n.Dialog(4000, newGallery.pro_gress);
				Intent _intent = new Intent();
				_intent.setClass(ctx, PhimpMeGallery.class);
				_intent.putExtra("image-path", tmp_url);						
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
	private ArrayList<Bitmap> bitmap_p_imgur;
	Context ctx;

	public GridImgurPublicAdaper(ArrayList<RSSPhotoItem> list_thumb,
			ArrayList<Bitmap> bitmap_p_imgur, Context c) {
		list_imgur.clear();
		this.bitmap_p_imgur = bitmap_p_imgur;
		ctx = c;
		for (int i = 0; i < list_thumb.size(); i++)
			if (list_thumb.get(i).getService().equals("public_imgur")) {
				list_imgur.add(list_thumb.get(i));
			}
	}

	public void addItem(RSSPhotoItem item) {

		list_imgur.add(0, item);
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
		if (convertView == null) {
			li = (LayoutInflater) ctx
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			view = li.inflate(R.layout.gallery_grid_photos_item, null);
		} else {
			view = convertView;
		}

		String url = list_imgur.get(position).getURL();

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

		final String tmp_url = url;

		iv.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				newGallery.pro_gress=ProgressDialog.show(ctx, "", "Please wait...", true, false);
				newGallery n=new newGallery();
				n.Dialog(4000, newGallery.pro_gress);
				Intent _intent = new Intent();
				_intent.setClass(ctx, PhimpMeGallery.class);
				_intent.putExtra("image-path", tmp_url);						
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
	private ArrayList<Bitmap> bitmap_public_500px;
	Context ctx;

	public GridPublic500pxAdaper(ArrayList<RSSPhotoItem> list_thumb,
			ArrayList<Bitmap> bitmap_public_500px, Context c) {
		list_public_500px.clear();
		this.bitmap_public_500px = bitmap_public_500px;
		ctx = c;
		for (int i = 0; i < list_thumb.size(); i++)
			if (list_thumb.get(i).getService().equals("public_500px")) {
				list_public_500px.add(list_thumb.get(i));
			}
	}

	public void addItem(RSSPhotoItem item) {

		list_public_500px.add(0, item);
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
		if (convertView == null) {
			li = (LayoutInflater) ctx
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			view = li.inflate(R.layout.gallery_grid_photos_item, null);
		} else {
			view = convertView;
		}
		String url = list_public_500px.get(position).getURL();

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

		final String tmp_url = url;

		iv.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				newGallery.pro_gress=ProgressDialog.show(ctx, "", "Please wait...", true, false);
				newGallery n=new newGallery();
				n.Dialog(4000, newGallery.pro_gress);
				Intent _intent = new Intent();
				_intent.setClass(ctx, PhimpMeGallery.class);
				_intent.putExtra("image-path", tmp_url);						
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
	private ArrayList<Bitmap> bitmap_personal_500px;
	Context ctx;

	public GridPersonal500pxAdapter(
			ArrayList<RSSPhotoItem_Personal> list_500px_personal,
			ArrayList<Bitmap> bitmap_personal_500px, Context c) {
		list_500px.clear();
		this.bitmap_personal_500px = bitmap_personal_500px;
		ctx = c;
		for (int i = 0; i < list_500px_personal.size(); i++)
			if (list_500px_personal.get(i).getService()
					.equals("personal_500px")) {
				list_500px.add(list_500px_personal.get(i));
			}
	}

	public void addItem(RSSPhotoItem_Personal item) {

		list_500px.add(0, item);
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

		if (convertView == null) {
			li = (LayoutInflater) ctx
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			view = li.inflate(R.layout.gallery_grid_photos_item, null);
		} else {
			view = convertView;
		}

		String url = list_500px.get(position).getURL();

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

		final String tmp_url = url;
		iv.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				newGallery.pro_gress=ProgressDialog.show(ctx, "", "Please wait...", true, false);
				newGallery n=new newGallery();
				n.Dialog(4000, newGallery.pro_gress);
				Intent _intent = new Intent();
				_intent.setClass(ctx, PhimpMeGallery.class);
				_intent.putExtra("image-path", tmp_url);						
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
	private ArrayList<Bitmap> bitmap_personal_sohu;
	Context ctx;

	public GridPersonalSohuAdapter(
			ArrayList<RSSPhotoItem_Personal> list_sohu_personal,
			ArrayList<Bitmap> bitmap_personal_sohu, Context c) {
		list_sohu.clear();
		this.bitmap_personal_sohu = bitmap_personal_sohu;
		ctx = c;
		for (int i = 0; i < list_sohu_personal.size(); i++)
			if (list_sohu_personal.get(i).getService().equals("personal_sohu")) {
				list_sohu.add(list_sohu_personal.get(i));
			}
	}

	public void addItem(RSSPhotoItem_Personal item) {

		list_sohu.add(0, item);
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
		if (convertView == null) {
			li = (LayoutInflater) ctx
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			view = li.inflate(R.layout.gallery_grid_photos_item, null);
		} else {
			view = convertView;
		}
		String url = list_sohu.get(position).getURL();

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

		final String tmp_url = url;
		iv.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				newGallery.pro_gress=ProgressDialog.show(ctx, "", "Please wait...", true, false);
				newGallery n=new newGallery();
				n.Dialog(4000, newGallery.pro_gress);
				Intent _intent = new Intent();
				_intent.setClass(ctx, PhimpMeGallery.class);
				_intent.putExtra("image-path", tmp_url);						
				_intent.putExtra("activityName", "GridviewAdapter");
				((Activity) ctx).startActivityForResult(_intent, 3);
			}
		});
		return view;
	}
}
