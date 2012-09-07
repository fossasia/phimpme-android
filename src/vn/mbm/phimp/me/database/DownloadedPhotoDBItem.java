package vn.mbm.phimp.me.database;

import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;

public class DownloadedPhotoDBItem 
{
	public String id;
	public String filepath;
	public String thumbpath;
	public String title;
	public String latitude;
	public String longitude;
	public String link;
	public String serivce;
	public String description;
	
	public String getID() { return id;	}
	public void setID(String id) { this.id = id; }
	
	public String getFilePath() { return filepath;	}
	public void setFilePath(String filepath) { this.filepath = filepath; }
	
	public String getThumbPath() { return thumbpath;	}
	public void setThumbPath(String thumbpath) { this.thumbpath = thumbpath; }
	
	public String getTitle() { return title; }
	public void setTitle(String title) { this.title = title; }
	
	public String getLatitude() { return latitude; }
	public void setLatitude(String latitude) { this.latitude = latitude; }
	
	public String getLongitude() { return longitude; }
	public void setLongitude(String longitude) { this.longitude = longitude; }
	
	public String getLink() { return this.link; }
	public void setLink(String link) { this.link = link; }
	
	public String getService() { return this.serivce; }
	public void setService(String service) { this.serivce = service; }
	
	public String getDescription() { return this.description; }
	public void setDescription(String description) { this.description = description; }
	
	public synchronized static boolean insert(Context ctx, String id, String filepath, String thumbpath, String title, String latitude, String longitude, String link, String service, String description)
	{
		boolean result;
		
		DownloadedPhotoDBAdapter db = new DownloadedPhotoDBAdapter(ctx);
		db.open();
		result = db.insert(id, filepath, thumbpath, title, latitude, longitude, link, service, description);
		db.close();
		
		return result;
	}
	
	public static int removeAccount(Context ctx, String id)
	{
		int result;
		
		DownloadedPhotoDBAdapter db = new DownloadedPhotoDBAdapter(ctx);
		db.open();
		result = db.removeAccount(id);
		db.close();
		
		return result;
	}
	
	public static ArrayList<DownloadedPhotoDBItem> getAll(Context ctx)
	{
		ArrayList<DownloadedPhotoDBItem> items = new ArrayList<DownloadedPhotoDBItem>();
		
		DownloadedPhotoDBAdapter db = new DownloadedPhotoDBAdapter(ctx);
		db.open();
		
		Cursor c = db.getAll();
		
		if (c.moveToFirst())
		{
			do
			{
				DownloadedPhotoDBItem item = new DownloadedPhotoDBItem();
				
				String id = c.getString(0);
				String filepath = c.getString(1);
				String thumbpath = c.getString(2);
				String title = c.getString(3);
				String latitude = c.getString(4);
				String longitude = c.getString(5);
				String service = c.getString(7);
				
				item.setID(id);
				item.setFilePath(filepath);
				item.setThumbPath(thumbpath);
				item.setTitle(title);
				item.setLatitude(latitude);
				item.setLongitude(longitude);
				item.setService(service);
				
				items.add(item);
			}
			while (c.moveToNext());
		}
		
		c.deactivate();
		
		db.close();
		
		return items;
	}
}
