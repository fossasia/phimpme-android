package vn.mbm.phimp.me.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import vn.mbm.phimp.me.PhimpMe;
import vn.mbm.phimp.me.newGallery;
import vn.mbm.phimp.me.database.DownloadedPersonalPhotoDBItem;
import vn.mbm.phimp.me.database.DownloadedPhotoDBItem;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

public class RSSUtil 
{
	final static String TAG = "rss";
	
	public static String RSS_ITEM_FOLDER = ".rss_items";
	
	public static String RSS_THUMB_FOLDER = ".rss_thumbs";
	
	public static String TMP_FOLDER = ".tmp";
	
	public static int max_photos_in_gallery = 15;
	
	
	
	public static ArrayList<RSSPhotoItem> getLocalPhotos(Context ctx)
	{
		ArrayList<RSSPhotoItem> list = new ArrayList<RSSPhotoItem>();
		try
		{
			ArrayList<DownloadedPhotoDBItem> items = DownloadedPhotoDBItem.getAll(ctx);
			
			for (int i = 0; i < items.size(); i++)
			{
				RSSPhotoItem _item = new RSSPhotoItem();
				DownloadedPhotoDBItem _item2 = items.get(i);
				
				_item.setTitle(_item2.getTitle());
				//Log.d("database", "Title: " + _item.getTitle() + " - " + _item2.getTitle());
				_item.setThumb(_item2.getThumbPath());
				_item.setURL(_item2.getFilePath());
				_item.setLatitude(_item2.getLatitude());
				_item.setLongitude(_item2.getLongitude());
				_item.setService(_item2.getService());
				list.add(_item);
			}
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		return list;
	}
	
	public static ArrayList<RSSPhotoItem_Personal> getLocalPhotosPersonal(Context ctx)
	{
		ArrayList<RSSPhotoItem_Personal> list = new ArrayList<RSSPhotoItem_Personal>();
		try
		{		
			ArrayList<DownloadedPersonalPhotoDBItem> items = DownloadedPersonalPhotoDBItem.getAll(ctx);
			
			for (int i = 0; i < items.size(); i++)
			{
				RSSPhotoItem_Personal _item = new RSSPhotoItem_Personal();
				DownloadedPersonalPhotoDBItem _item2 = items.get(i);
				
				_item.setTitle(_item2.getTitle());
				//Log.d("database", "Title: " + _item.getTitle() + " - " + _item2.getTitle());
				_item.setThumb(_item2.getThumbPath());
				_item.setURL(_item2.getFilePath());
				_item.setLatitude(_item2.getLatitude());
				_item.setLongitude(_item2.getLongitude());
				_item.setService(_item2.getService());
				list.add(_item);
			}
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		return list;
	}
	public static String getThumbPhotos(Context ctx, File original_file)
	{
		String filepath = "";
		try
		{
			File thumb_folder = new File(PhimpMe.DataDirectory.getAbsolutePath() + "/" + RSS_THUMB_FOLDER);
			
			if (!thumb_folder.exists())
			{
				thumb_folder.mkdirs();
			}
			
			File thumb_file = new File(thumb_folder.getAbsolutePath() + "/" + original_file.getName());
			
			if (thumb_file.exists())
			{
				filepath = thumb_file.getAbsolutePath();
				Log.d("mbmphotos", "Thumbfile exist!");
			}
			else
			{
				Log.d("mbmphotos", "Thumbfile not exist!");
				InputStream is = new FileInputStream(original_file);
				
				BitmapFactory.Options bfOpt = new BitmapFactory.Options();
	            
	            bfOpt.inScaled = true;
	            bfOpt.inSampleSize = 2;
	            bfOpt.inPurgeable = true;
	            try{
					Bitmap bmp = BitmapFactory.decodeStream(is, null, bfOpt);
					
					bmp = ImageUtil.scaleCenterCrop(bmp, newGallery.DEFAULT_THUMBNAIL_SIZE, newGallery.DEFAULT_THUMBNAIL_SIZE);
		           
					if (ImageUtil.SaveBitmap(ctx, bmp, thumb_file.getAbsolutePath()))
					{
						Log.d("mbmphotos", "Thumbfile create success!");
						filepath = thumb_file.getAbsolutePath();
					}
					else
					{
						Log.e("mbmphotos", "Thumbfile create fail!");
						
					}
					is.close();
					bmp.recycle();
				}catch(OutOfMemoryError oome){
	            	
	            }
			}
		}
		catch (Exception e)
		{
			Log.e("mbmphotos", "getThumbPhotos error: " + e.toString());
			e.printStackTrace();
		}
		return filepath;
	}
	
	public static boolean downloadFile(Context ctx, String url, String output_file)
	{
		boolean result = false;
		
		try
		{
			int count;
			HttpGet httpGet = new HttpGet(url);
			HttpClient httpclient = new DefaultHttpClient();
			// Execute HTTP Get Request
			HttpResponse response = httpclient.execute(httpGet);

            // download the file
            InputStream input = response.getEntity().getContent();
            OutputStream output = new FileOutputStream(output_file);

            byte data[] = new byte[1024];

            while ((count = input.read(data)) != -1) 
            {
                output.write(data, 0, count);
            }

            output.flush();
            output.close();
            input.close();
            
            result = true;
		}
		catch (Exception e) 
		{
			result = false;
		}
		
		return result;
	}
	
	public static boolean downloadFile(Context ctx, String url, String output_file, long max_size)
	{
		boolean result = false;				
		try
		{
			int count;
			HttpGet httpGet = new HttpGet(url);
			HttpClient httpclient = new DefaultHttpClient();
			// Execute HTTP Get Request
			HttpResponse response = httpclient.execute(httpGet);

            // download the file
			long file_size = response.getEntity().getContentLength();
			Log.d("thread", "Filesize: " + (file_size / 1024) + " KB");
			Log.d("thread", "Max size: " + (max_size / 1024) + " KB");
			if (file_size > max_size)
			{
				result = false;
			}
			else
			{
	            InputStream input = response.getEntity().getContent();
	            OutputStream output = new FileOutputStream(output_file);
	
	            byte data[] = new byte[1024];
	
	            while ((count = input.read(data)) != -1) 
	            {
	                output.write(data, 0, count);
	            }
	
	            output.flush();
	            output.close();
	            input.close();
	            
	            result = true;
			}
		}
		catch (IllegalStateException err){
			try {
				int count;
				InputStream input = new FileInputStream(url);
				OutputStream output = new FileOutputStream(output_file);
				 byte data[] = new byte[1024];
					
		            while ((count = input.read(data)) != -1) 
		            {
		                output.write(data, 0, count);
		            }
		
		            output.flush();
		            output.close();
		            input.close();
		            
		            result = true;
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		catch (Exception e) 
		{
			e.printStackTrace();
			result = false;
		}
		
		return result;
	}
}
