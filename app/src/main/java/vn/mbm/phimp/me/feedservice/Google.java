package vn.mbm.phimp.me.feedservice;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.mcsoxford.rss.MediaThumbnail;
import org.mcsoxford.rss.RSSFeed;
import org.mcsoxford.rss.RSSItem;
import org.mcsoxford.rss.RSSReader;

import vn.mbm.phimp.me.PhimpMe;
import vn.mbm.phimp.me.database.AccountItem;
import vn.mbm.phimp.me.database.PicasaItem;
import vn.mbm.phimp.me.services.PicasaServices;
import vn.mbm.phimp.me.utils.RSSPhotoItem;
import vn.mbm.phimp.me.utils.RSSPhotoItem_Personal;
import vn.mbm.phimp.me.R;
import android.content.Context;
import android.net.Uri;
import android.util.Log;

public class Google 
{
	static final String TAG = "google";
	
	static final String PICASA_PUBLIC_FEEDS = "https://picasaweb.google.com/data/feed/base/featured?alt=rss&kind=photo&access=public&slabel=featured&imgmax=1600&hl=en_US";
	
	static final String PICASA_SEARCH_FEEDS = "https://picasaweb.google.com/data/feed/api/all?v=2&alt=rss&strict=true&kind=photo&access=public&imgmax=1600";
	
	static final String NEWS_FEEDS = "http://news.google.com.vn/news?output=rss";
	
	public static final String PICASA_PUBLIC_TAG = "Picasa Public Photos";
	
	public static final String PICASA_PRIVATE_TAG = "Picasa Private Photos";
	
	public static final String NEWS_TAG = "Google News";
	
	public static final int ICON = R.drawable.google;
	
	public static final int PICASA_ICON = R.drawable.picasa;
	
	public static ArrayList<RSSPhotoItem> getPicasaPublic(Context ctx)
	{
		ArrayList<RSSPhotoItem> list = new ArrayList<RSSPhotoItem>();
		
		Log.d(TAG, "Run getPicasaPublic()");
		
		try
		{
			RSSReader reader = new RSSReader();
			
			String url = PICASA_PUBLIC_FEEDS + "&max-results=10";
			
			RSSFeed feeds = reader.load(url);
			
			List<RSSItem> items = feeds.getItems();
			
			//Log.d(TAG, "Items: "  + items.size());
			
			if (items.size() > 0)
			{				
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
						String url_img = thumbnail_url.toString();
						
						RSSPhotoItem rssitem = new RSSPhotoItem();
						rssitem.setDescription(description);
						rssitem.setTitle(title);
						rssitem.setLink(link.toString());
						rssitem.setURL(url_img);
						rssitem.setService("public_picasa");
						list.add(rssitem);
					}
				}
			}
		}
		catch (Exception e) 
		{
			e.printStackTrace();
			
			Log.e(TAG, "getPicasaPublic() " + e.toString());
		}
		
		return list;
	}
	
	public static ArrayList<RSSPhotoItem> getPicasaSearch(Context ctx, String tag)
	{
		ArrayList<RSSPhotoItem> list = new ArrayList<RSSPhotoItem>();
		
		Log.d(TAG, "Run getPicasaSearch()");
		
		try
		{
			RSSReader reader = new RSSReader();
			
			String url = PICASA_SEARCH_FEEDS + "&max-results=10&q=" + URLEncoder.encode(tag);
			
			RSSFeed feeds = reader.load(url);
			
			List<RSSItem> items = feeds.getItems();
			
			
			if (items.size() > 0)
			{		
				for (int i = 0; i < items.size(); i++)
				{					
					RSSItem item = items.get(i);
					String description = item.getDescription() ;
					Uri link = item.getLink();
					String title = item.getTitle();
					List<MediaThumbnail> thumbnails = item.getThumbnails();
				
					if (thumbnails.size() > 0)
					{
						MediaThumbnail thumbnail = thumbnails.get(0);
						Uri thumbnail_url = thumbnail.getUrl();
						String url_img = thumbnail_url.toString();
						
						RSSPhotoItem rssitem = new RSSPhotoItem();
						rssitem.setDescription(description);
						rssitem.setTitle(title);
						rssitem.setLink(link.toString());
						rssitem.setURL(url_img);
						
						list.add(rssitem);
					}
				}
			}
		}
		catch (Exception e) 
		{
			e.printStackTrace();
			
			Log.e(TAG, "getPicasaSearch() " + e.toString());
		}
		
		return list;
	}
	
	public static ArrayList<RSSPhotoItem> getNews(Context ctx)
	{
		ArrayList<RSSPhotoItem> list = new ArrayList<RSSPhotoItem>();
		
		Log.d(TAG, "Run getNews()");
		
		try
		{
			RSSReader reader = new RSSReader();
			
			String url = NEWS_FEEDS;
			
			RSSFeed feeds = reader.load(url);
			
			List<RSSItem> items = feeds.getItems();
			
			if (items.size() > 0)
			{
				for (int i = 0; i < items.size(); i++)
				{
					RSSItem item = items.get(i);
					String description = item.getDescription();
					Uri link = item.getLink();
					String title = item.getTitle();
					
					Log.d(TAG, "Item: " + i);
					Log.d(TAG, "	Title: " + title);
					Log.d(TAG, "	Description: " + description);
					Log.d(TAG, "	Link: " + link.toString());
					
					String tmp = description.substring(description.indexOf("<img"));
					tmp = tmp.substring(0, tmp.indexOf("/>"));
					
					String[] tmp2 = tmp.split("\"");
					
					Log.d(TAG, "	Link: " + tmp2[1]);					
					String url_img = "http:" + tmp2[1];
					
					tmp = null;
					tmp2 = null;
					
					RSSPhotoItem rssitem = new RSSPhotoItem();
					rssitem.setDescription(description);
					rssitem.setTitle(title);
					rssitem.setLink(link.toString());
					rssitem.setURL(url_img);
					rssitem.setService("google_news");
					list.add(rssitem);
				}
			}
		}
		catch (Exception e) 
		{
			e.printStackTrace();
			
			Log.e(TAG, "getNews() " + e.toString());
		}
		
		return list;
	}
	public static ArrayList<RSSPhotoItem_Personal> getOwn(Context ctx, String tag)
	{
		ArrayList<RSSPhotoItem_Personal> list = new ArrayList<RSSPhotoItem_Personal>();
		String[] id;
		String[] name;
		String[] service;
		String account_picasa="";
		
		
			ArrayList<AccountItem> accounts = AccountItem.getAllAccounts(ctx);
			id = new String[accounts.size()];
			name = new String[accounts.size()];
			service = new String[accounts.size()];
			for (int i = 0; i < accounts.size(); i++)
			{
				AccountItem item = accounts.get(i);
				id[i] = item.getID();
				name[i] = item.getName();
				service[i] = item.getService();
			}
			accounts = null;
			for (int i = 0; i < PhimpMe.checked_accounts.size(); i++)
        	{
				
        		boolean _b = PhimpMe.checked_accounts.get(id[i]);        		
        		
        		if (_b)
        		{
        			if(service[i].toString().equals("picasa"))
        			{
        				account_picasa +=name[i]+";";
        				Log.e(TAG,"account picasa :"+ account_picasa);
        				try
        				{
        					PicasaItem acc = PicasaItem.getItem(ctx, id[i]);
        					String access_token = acc.getAccessToken();
        					String uid = acc.getUserID();
        					String link_get = PicasaServices.getLink(uid, access_token);	
        					Log.d("luong", link_get);
        					RSSReader reader = new RSSReader(); 					
        					RSSFeed feeds = reader.load(link_get);
        					Log.d(TAG, "feed: "  + feeds);
        					List<RSSItem> items = feeds.getItems();				
        					Log.d(TAG, "Items: "  + items.size());
        					if (items.size() > 0)
        					{
        						for (int j = 0; j < items.size(); j++)
        						{      							
        							RSSItem item = items.get(j);
        							List<MediaThumbnail> link = item.getThumbnails();
        							Log.d(TAG, "link: " + link.toString());
        								String url = link.get(0).getUrl().toString();
        								RSSPhotoItem_Personal rssitem = new RSSPhotoItem_Personal();
        								rssitem.setDescription("");
        								rssitem.setTitle("");
        								rssitem.setLink(url);
        								rssitem.setURL(url); 
        								rssitem.setService("personal_picasa");
        								list.add(rssitem);	      							
        						}
        					}	
        				}
        				catch (Exception e) 
        				{
        					Log.e(TAG, "Picasa Error: " + e.toString());
        					e.printStackTrace();
        				} 
        			}
        		}
        	}

			return list;
	}
}
