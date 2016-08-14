package vn.mbm.phimp.me.feedservice;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.mcsoxford.rss.MediaThumbnail;
import org.mcsoxford.rss.RSSFeed;
import org.mcsoxford.rss.RSSItem;
import org.mcsoxford.rss.RSSReader;

import vn.mbm.phimp.me.Settings;
import vn.mbm.phimp.me.utils.RSSPhotoItem;
import android.content.Context;
import android.net.Uri;
import android.util.Log;

public class MyFeedServices {
	static final String TAG = "my feed services";
	public static String title = "MyRssFeed";
	public static final String PUBLIC_TAG = "My Services Photos";
	
	public static ArrayList<RSSPhotoItem> getPublic(Context ctx, String tag)
	{
		ArrayList<RSSPhotoItem> list = new ArrayList<RSSPhotoItem>();
		
		try
		{
			RSSReader reader = new RSSReader();
						
			String url="http://"+Settings.etMyFeedServicesTextbox.getText().toString();
			Log.d("My Feed Services.java", "url : "+url);
			if (!tag.equals(""))
			{
				url += "?tag=" + URLEncoder.encode(tag);
			}
				RSSFeed feeds = reader.load(url);
				
				List<RSSItem> items = feeds.getItems();
				Log.d(TAG, "Items size is greater than 0"+ items.size());
				if (items.size() > 0)
				{
					Log.d(TAG, "Items size is greater than 0"+ url);
					
					for (int i = 0; i < items.size(); i++)
					{
						
						RSSItem item = items.get(i);
						String description = item.getDescription();
						Uri link = item.getLink();
						String title = item.getTitle();
						List<MediaThumbnail> thumbnails = item.getThumbnails();
						
						if (thumbnails.size() > 0)
						{
							MediaThumbnail thumbnail = thumbnails.get(0);
							Uri thumbnail_url = thumbnail.getUrl();
							RSSPhotoItem rssitem = new RSSPhotoItem();
							rssitem.setDescription(description);
							rssitem.setTitle(title);
							rssitem.setLink(link.toString());
							rssitem.setService("my_feed_services");
							rssitem.setURL(thumbnail_url.toString());
							
							list.add(rssitem);
						}
					}
				}				
		}
		catch (Exception e) 
		{
			Log.e(TAG, "My Feed Services Error: " + e.toString());
			e.printStackTrace();
		}
		Log.d("My Feed Services","My Feed Services photos : "+list.size() );
		return list;
	}
	
	public static ArrayList<RSSPhotoItem> getPublic1(Context ctx, String tag)
	{
		ArrayList<RSSPhotoItem> list = new ArrayList<RSSPhotoItem>();
		
		try
		{
			RSSReader reader = new RSSReader();
						
			String url="http://"+Settings.etMyFeedServicesTextbox1.getText().toString();
			Log.d("My Feed Services.java", "url : "+url);

				RSSFeed feeds = reader.load(url);
				
				List<RSSItem> items = feeds.getItems();
				Log.d(TAG, "Items size is greater than 0"+ items.size());
				if (items.size() > 0)
				{
					Log.d(TAG, "Items size is greater than 0"+ url);
					
					for (int i = 0; i < items.size(); i++)
					{
						
						RSSItem item = items.get(i);
						String description = item.getDescription();
						Uri link = item.getLink();
						String title = item.getTitle();
						List<MediaThumbnail> thumbnails = item.getThumbnails();
						
						if (thumbnails.size() > 0)
						{
							MediaThumbnail thumbnail = thumbnails.get(0);
							Uri thumbnail_url = thumbnail.getUrl();
							RSSPhotoItem rssitem = new RSSPhotoItem();
							rssitem.setDescription(description);
							rssitem.setTitle(title);
							rssitem.setLink(link.toString());
							rssitem.setService("my_feed_services1");
							rssitem.setURL(thumbnail_url.toString());
							
							list.add(rssitem);
						}
					}
				}				
		}
		catch (Exception e) 
		{
			Log.e(TAG, "My Feed Services 1 Error: " + e.toString());
			e.printStackTrace();
		}
		Log.d("My Feed Services","My Feed Services 1 photos : "+list.size() );
		return list;
	}
	
	public static ArrayList<RSSPhotoItem> getPublic2(Context ctx, String tag)
	{
		ArrayList<RSSPhotoItem> list = new ArrayList<RSSPhotoItem>();
		
		try
		{
			RSSReader reader = new RSSReader();
						
			String url="http://"+Settings.etMyFeedServicesTextbox2.getText().toString();
			Log.d("My Feed Services.java", "url : "+url);
			
				RSSFeed feeds = reader.load(url);
				
				List<RSSItem> items = feeds.getItems();
				Log.d(TAG, "Items size is greater than 0"+ items.size());
				if (items.size() > 0)
				{
					Log.d(TAG, "Items size is greater than 0"+ url);
					
					for (int i = 0; i < items.size(); i++)
					{
						
						RSSItem item = items.get(i);
						String description = item.getDescription();
						Uri link = item.getLink();
						String title = item.getTitle();
						List<MediaThumbnail> thumbnails = item.getThumbnails();
						
						if (thumbnails.size() > 0)
						{
							MediaThumbnail thumbnail = thumbnails.get(0);
							Uri thumbnail_url = thumbnail.getUrl();
							RSSPhotoItem rssitem = new RSSPhotoItem();
							rssitem.setDescription(description);
							rssitem.setTitle(title);
							rssitem.setLink(link.toString());
							rssitem.setService("my_feed_services2");
							rssitem.setURL(thumbnail_url.toString());
							
							list.add(rssitem);
						}
					}
				}				
		}
		catch (Exception e) 
		{
			Log.e(TAG, "My Feed Services 2 Error: " + e.toString());
			e.printStackTrace();
		}
		Log.d("My Feed Services","My Feed Services 2 photos : "+list.size() );
		return list;
	}
	
	public static ArrayList<RSSPhotoItem> getPublic3(Context ctx, String tag)
	{
		ArrayList<RSSPhotoItem> list = new ArrayList<RSSPhotoItem>();
		
		try
		{
			RSSReader reader = new RSSReader();
						
			String url="http://"+Settings.etMyFeedServicesTextbox3.getText().toString();
			
			Log.d("My Feed Services.java", "url : "+url);
			if (!tag.equals(""))
			{
				url += "?tag=" + URLEncoder.encode(tag);
			}
				RSSFeed feeds = reader.load(url);
				
				List<RSSItem> items = feeds.getItems();
				Log.d(TAG, "Items size is greater than 0"+ items.size());
				if (items.size() > 0)
				{
					Log.d(TAG, "Items size is greater than 0"+ url);
					
					for (int i = 0; i < items.size(); i++)
					{
						
						RSSItem item = items.get(i);
						String description = item.getDescription();
						Uri link = item.getLink();
						String title = item.getTitle();
						List<MediaThumbnail> thumbnails = item.getThumbnails();
						
						if (thumbnails.size() > 0)
						{
							MediaThumbnail thumbnail = thumbnails.get(0);
							Uri thumbnail_url = thumbnail.getUrl();
							RSSPhotoItem rssitem = new RSSPhotoItem();
							rssitem.setDescription(description);
							rssitem.setTitle(title);
							rssitem.setLink(link.toString());
							rssitem.setService("my_feed_services3");
							rssitem.setURL(thumbnail_url.toString());
							
							list.add(rssitem);
						}
					}
				}				
		}
		catch (Exception e) 
		{
			Log.e(TAG, "My Feed Services 3 Error: " + e.toString());
			e.printStackTrace();
		}
		Log.d("My Feed Services","My Feed Services 3 photos : "+list.size() );
		return list;
	}
	public static ArrayList<RSSPhotoItem> getPublic4(Context ctx, String tag)
	{
		ArrayList<RSSPhotoItem> list = new ArrayList<RSSPhotoItem>();
		
		try
		{
			RSSReader reader = new RSSReader();
						
			String url="http://"+Settings.etMyFeedServicesTextbox4.getText().toString();
			
			Log.d("My Feed Services.java", "url : "+url);

				RSSFeed feeds = reader.load(url);
				
				List<RSSItem> items = feeds.getItems();
				Log.d(TAG, "Items size is greater than 0"+ items.size());
				if (items.size() > 0)
				{
					Log.d(TAG, "Items size is greater than 0"+ url);
					
					for (int i = 0; i < items.size(); i++)
					{
						
						RSSItem item = items.get(i);
						String description = item.getDescription();
						Uri link = item.getLink();
						String title = item.getTitle();
						List<MediaThumbnail> thumbnails = item.getThumbnails();
						
						if (thumbnails.size() > 0)
						{
							MediaThumbnail thumbnail = thumbnails.get(0);
							Uri thumbnail_url = thumbnail.getUrl();
							RSSPhotoItem rssitem = new RSSPhotoItem();
							rssitem.setDescription(description);
							rssitem.setTitle(title);
							rssitem.setLink(link.toString());
							rssitem.setService("my_feed_services4");
							rssitem.setURL(thumbnail_url.toString());
							
							list.add(rssitem);
						}
					}
				}				
		}
		catch (Exception e) 
		{
			Log.e(TAG, "My Feed Services 4 Error: " + e.toString());
			e.printStackTrace();
		}
		Log.d("My Feed Services","My Feed Services 4 photos : "+list.size() );
		return list;
	}
	public static ArrayList<RSSPhotoItem> getPublic5(Context ctx, String tag)
	{
		ArrayList<RSSPhotoItem> list = new ArrayList<RSSPhotoItem>();
		
		try
		{
			RSSReader reader = new RSSReader();
						
			String url="http://"+Settings.etMyFeedServicesTextbox5.getText().toString();
			
			Log.d("My Feed Services.java", "url : "+url);
			if (!tag.equals(""))
			{
				url += "?tag=" + URLEncoder.encode(tag);
			}
				RSSFeed feeds = reader.load(url);
				
				List<RSSItem> items = feeds.getItems();
				Log.d(TAG, "Items size is greater than 0"+ items.size());
				if (items.size() > 0)
				{
					Log.d(TAG, "Items size is greater than 0"+ url);
					
					for (int i = 0; i < items.size(); i++)
					{
						
						RSSItem item = items.get(i);
						String description = item.getDescription();
						Uri link = item.getLink();
						String title = item.getTitle();
						List<MediaThumbnail> thumbnails = item.getThumbnails();
						
						if (thumbnails.size() > 0)
						{
							MediaThumbnail thumbnail = thumbnails.get(0);
							Uri thumbnail_url = thumbnail.getUrl();
							RSSPhotoItem rssitem = new RSSPhotoItem();
							rssitem.setDescription(description);
							rssitem.setTitle(title);
							rssitem.setLink(link.toString());
							rssitem.setService("my_feed_services5");
							rssitem.setURL(thumbnail_url.toString());
							
							list.add(rssitem);
						}
					}
				}				
		}
		catch (Exception e) 
		{
			Log.e(TAG, "My Feed Services 5 Error: " + e.toString());
			e.printStackTrace();
		}
		Log.d("My Feed Services","My Feed Services 5 photos : "+list.size() );
		return list;
	}
}
