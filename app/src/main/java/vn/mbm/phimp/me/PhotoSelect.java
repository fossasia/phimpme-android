package vn.mbm.phimp.me;

import java.util.ArrayList;

import vn.mbm.phimp.me.gallery.PhimpMeGallery;
import vn.mbm.phimp.me.R;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

public class PhotoSelect extends Activity {

	public ImageAdapter imageAdapter;
	//private final static int VIEW_IMAGE = 3;
	public GridView imagegrid;
	private long lastId;
	Context ctx;
	Activity acti;
	public static CacheTask cachetask;
	static Activity activity = new Activity();
	Cursor pathcursor;
	/** Called when the activity is first created. */
	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.photolist);
		activity = (Activity) this;	
		imageAdapter = new ImageAdapter();		
		imagegrid = (GridView) findViewById(R.id.PhoneImageGrid);
		imagegrid.setAdapter(imageAdapter);	
		cachetask = new CacheTask();
		showFromCache();		
		final String[] data = { MediaStore.Images.Media.DATA };
		final String orderBy = MediaStore.Images.Media._ID;
		pathcursor = managedQuery(
				MediaStore.Images.Media.EXTERNAL_CONTENT_URI, data,
				null, null, orderBy);	
		final Button selectBtn = (Button) findViewById(R.id.selectBtn);
		selectBtn.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				final int len = imageAdapter.images.size();
				int cnt = 0;
				String selectImages = "";
				for (int i = 0; i < len; i++) {
					if (imageAdapter.images.get(i).selection) {
						cnt++;
						selectImages = selectImages
								+ imageAdapter.images.get(i).path + "#";
					}
				}
				if (cnt == 0) {
					Toast.makeText(getApplicationContext(),
							"Please select at least one image",
							Toast.LENGTH_LONG).show();
				} else {
					
					selectImages = selectImages.substring(0,selectImages.lastIndexOf("#"));
					Log.d("SelectImage", selectImages);
					Intent intent = activity.getIntent();					
					intent.putExtra("Ids",selectImages);							
					activity.setResult(RESULT_OK,intent);
					activity.finish();
				}
				
			}
		});
	}

	public void updateUI() {
		
		imageAdapter.checkForNewImages();
	}
	public class ImageAdapter extends BaseAdapter {
		Context ctx;
		private LayoutInflater mInflater;
		public ArrayList<ImageItem> images = new ArrayList<ImageItem>();
		public ImageAdapter() {
			mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			PhimpMe.cache = CacheStore.getInstance();
		}		
		@SuppressWarnings("deprecation")
		public void checkForNewImages(){
			
			//Here we'll only check for newer images
			final String[] columns = { MediaStore.Images.Thumbnails._ID };
			final String orderBy = MediaStore.Images.Media._ID;
			Cursor imagecursor = managedQuery(
					MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns,
					MediaStore.Images.Media._ID + " > " + lastId , null, orderBy);
			int image_column_index = imagecursor
					.getColumnIndex(MediaStore.Images.Media._ID);
			int count = imagecursor.getCount();
			for (int i = 0; i < count; i++) {
				imagecursor.moveToPosition(i);
				int id = imagecursor.getInt(image_column_index);
				ImageItem imageItem = new ImageItem();
				imageItem.id = id;
				lastId = id;				
				imageItem.img = MediaStore.Images.Thumbnails.getThumbnail(
						getApplicationContext().getContentResolver(), id,
						MediaStore.Images.Thumbnails.MICRO_KIND, null);
				
				imageItem.selection = true; //newly added item will be selected by default
				images.add(imageItem);
			}
			imagecursor.close();
			notifyDataSetChanged();
		}

		public int getCount() {
			return images.size();
		}

		public Object getItem(int position) {
			return position;
		}

		public long getItemId(int position) {
			return position;
		}
		public void addItem(ImageItem item) {
			images.add(item);
			notifyDataSetChanged();
		}
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;		
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = mInflater.inflate(R.layout.photoitem, null);
				holder.imageview = (ImageView) convertView
						.findViewById(R.id.thumbImage);
				holder.checkbox = (CheckBox) convertView
						.findViewById(R.id.itemCheckBox);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			ImageItem item = images.get(position);
			holder.checkbox.setId(position);
			holder.imageview.setId(position);
			holder.checkbox.setOnClickListener(new OnClickListener() {

				public void onClick(View v) {
					// TODO Auto-generated method stub
					CheckBox cb = (CheckBox) v;
					int id = cb.getId();
					if (images.get(id).selection) {
						cb.setChecked(false);
						images.get(id).selection = false;
					} else {
						cb.setChecked(true);
						images.get(id).selection = true;
					}
				}
			});
			holder.imageview.setOnClickListener(new OnClickListener() {

				@SuppressWarnings("deprecation")
				public void onClick(View v) {
					// TODO Auto-generated method stub	
					try{	

						int id = v.getId();
						ImageItem item = images.get(id);
						Intent intent = new Intent();
						intent.setAction(Intent.ACTION_VIEW);				
						final String[] columns = { MediaStore.Images.Media.DATA };
						Cursor imagecursor = managedQuery(
								MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns,
								MediaStore.Images.Media._ID + " = " + item.id, null, MediaStore.Images.Media._ID);
						if (imagecursor != null && imagecursor.getCount() > 0){
							imagecursor.moveToPosition(0);
							String path = imagecursor.getString(imagecursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA));
							ArrayList<String> file = new ArrayList<String>();
							file.add(path);
							Intent _intent = new Intent();
							_intent.setClass(PhotoSelect.this, PhimpMeGallery.class);
							//_intent.putExtra("image-path", path);
							PhimpMeGallery.setFileList(file);
							_intent.putExtra("aspectX", 0);
							_intent.putExtra("aspectY", 0);
							_intent.putExtra("scale", true);
							_intent.putExtra("activityName", "PhotoSelect");
							startActivity(_intent);
							Log.d("file path",path);
						}
					}catch(Exception e){
						e.printStackTrace();
					}
			
				}
			});
			holder.imageview.setImageBitmap(item.img);
			holder.checkbox.setChecked(item.selection);
			return convertView;
			
		}	
	}

	class ViewHolder {		
		ImageView imageview;
		CheckBox checkbox;
	}

	class ImageItem {
		boolean selection;
		int id;
		Bitmap img;
		String path;
	}
	public void showFromCache(){
		Log.d("Luong", "Run Show Photo From Cache");
		final String[] data = { MediaStore.Images.Media.DATA };
		final String orderBy = MediaStore.Images.Media._ID;
		@SuppressWarnings("deprecation")
		final Cursor pathcursor = managedQuery(
				MediaStore.Images.Media.EXTERNAL_CONTENT_URI, data,
				null, null, orderBy+ " DESC");
		if(pathcursor != null){
			int path_column_index = pathcursor
					.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			int count = pathcursor.getCount();
			for (int i = 0; i< count; i++) {			
				pathcursor.moveToPosition(i);
				ImageItem imageItem = new ImageItem();
				String path = pathcursor.getString(path_column_index);
				imageItem.path = path;
				boolean check = PhimpMe.cache.check(path);
				if(check){
					imageItem.id = Integer.valueOf(PhimpMe.cache.getCacheId(path));
					imageItem.img = PhimpMe.cache.getCachePath(path);
					
					imageAdapter.images.add(imageItem);				
				}
				
			}
			PhotoSelect.this.runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					cachetask.execute(pathcursor);
				}
			});
		}
	}
	class CacheTask extends AsyncTask<Cursor, Void, String> {
	    @Override
	    protected String doInBackground(Cursor... cursor) {
	    	try{
	    		 Log.d("Luong", "Run View Photo Task");
	    		
	    		 Cursor pathcursor = cursor[0];
	    		 int path_column_index = pathcursor
	 					.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
	    		 int count = pathcursor.getCount();
	    		 final String[] columns = { MediaStore.Images.Thumbnails._ID};
	    		 for (int i = 0; i< count; i++) {
	 				pathcursor.moveToPosition(i);
	 				final ImageItem imageItem = new ImageItem();
	 				String path = pathcursor.getString(path_column_index);
	 				imageItem.path = path;
	 				boolean check = PhimpMe.cache.check(path);
	 				if(!check){	 					
	 					@SuppressWarnings("deprecation")
						Cursor cur = managedQuery(
	 							MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns,
	 							MediaStore.Images.Media.DATA+ " = " + "\""+path+"\"", null, MediaStore.Images.Media._ID);									
	 					if (cur != null && cur.getCount() > 0){
	 						cur.moveToPosition(0);
	 						int id = cur.getInt(cur.getColumnIndexOrThrow(MediaStore.Images.Media._ID));
	 						imageItem.id = id;
	 						imageItem.img = MediaStore.Images.Thumbnails.getThumbnail(
	 								getApplicationContext().getContentResolver(), id,
	 								MediaStore.Images.Thumbnails.MICRO_KIND, null);		
	 						PhimpMe.cache.saveCacheFile(imageItem.path, imageItem.img, imageItem.id);
	 					}else imageItem.id = -1;
	 					PhotoSelect.this.runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							// TODO Auto-generated method stub
							imageAdapter.addItem(imageItem);
						}
					});
	 					
	 				}
	 				
							
	 				
	 		        
	 			}
	    	}catch(RuntimeException runex){
	    		this.onCancelled();
	    	}
	    	
	        return "";
	    }

	    @Override
	    protected void onPostExecute(String result) {
	    	
	    }
	    @Override
	    protected void onCancelled() {
	    	// TODO Auto-generated method stub
	    	super.onCancelled();
	    }
	}
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		cachetask.onCancelled();
	}
}
   