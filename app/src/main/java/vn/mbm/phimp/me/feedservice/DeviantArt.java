package vn.mbm.phimp.me.feedservice;


import java.util.ArrayList;
import java.util.List;

import org.mcsoxford.rss.MediaThumbnail;
import org.mcsoxford.rss.RSSFeed;
import org.mcsoxford.rss.RSSItem;
import org.mcsoxford.rss.RSSReader;

import vn.mbm.phimp.me.PhimpMe;
import vn.mbm.phimp.me.database.AccountItem;
import vn.mbm.phimp.me.utils.RSSPhotoItem;
import vn.mbm.phimp.me.utils.RSSPhotoItem_Personal;
import vn.mbm.phimp.me.R;
import android.content.Context;
import android.net.Uri;
import android.util.Log;

public class DeviantArt {
	
static final String TAG = "DeviantArt";
	
	static final String PUBLIC_FEEDS = "http://backend.deviantart.com/rss.xml?type=deviation";
	
	public static final String PUBLIC_TAG = "DeviantArt Newest Photos";
	public static final String PRIVITE_TAG = "DeviantArt Private";
	public static final int ICON = R.drawable.devart;
	
	public static ArrayList<RSSPhotoItem> getPublic(Context ctx, String tag)
	{
		ArrayList<RSSPhotoItem> list = new ArrayList<RSSPhotoItem>();
		
		try
		{
			RSSReader reader = new RSSReader();
			
			String url = PUBLIC_FEEDS;
			
			
			RSSFeed feeds = reader.load(url);
			
			List<RSSItem> items = feeds.getItems();
			
			if (items.size() > 0)
			{
				Log.d(TAG, "Items size is greater than 0");
				
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
						rssitem.setURL(thumbnail_url.toString());
						rssitem.setService("public_deviant");
						list.add(rssitem);
					}
				}

			}
		}
		catch (Exception e) 
		{
			Log.e(TAG, "DeviantArt.getPublic() Error: " + e.toString());
			e.printStackTrace();
		}
		
		return list;
	}
	
	public static ArrayList<RSSPhotoItem_Personal> getPrivite(Context ctx, String tag)
	{
		ArrayList<RSSPhotoItem_Personal> list = new ArrayList<RSSPhotoItem_Personal>();
		String[] id;
		String[] name;
		String[] service;
		String account_deviant="";
		
		
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
        	

        			
        			if(service[i].toString().equals("deviantart"))
        			{
        				account_deviant +=name[i]+";";
        				Log.e(TAG,"account dv :"+ account_deviant);
        				try
        				{
        					RSSReader reader = new RSSReader();
        					
        					String url ="http://backend.deviantart.com/rss.xml?type=deviation&q=by:"+name[i]+"+meta:all";
        					
        					Log.e(TAG,"user name :"+ name[i]+url);
        					
        					RSSFeed feeds = reader.load(url);
        					
        					List<RSSItem> items = feeds.getItems();
        					
        					if (items.size() > 0)
        					{
        						Log.d(TAG, "Items size is greater than 0");
        						
        						for (int j = 0; j < items.size(); j++)
        						{
        							RSSItem item = items.get(j);
        							String description = item.getDescription();
        							Uri link = item.getLink();
        							String title = item.getTitle();
        							List<MediaThumbnail> thumbnails = item.getThumbnails();
        							
        							if (thumbnails.size() > 0)
        							{
        								MediaThumbnail thumbnail = thumbnails.get(0);
        								Uri thumbnail_url = thumbnail.getUrl();      								
        								RSSPhotoItem_Personal rssitem = new RSSPhotoItem_Personal();
        								rssitem.setDescription(description);
        								rssitem.setTitle(title);
        								rssitem.setLink(link.toString());
        								rssitem.setURL(thumbnail_url.toString());
        								rssitem.setService("personal_deviantart");
        								
        								list.add(rssitem);
        							}
        						}

        					}
        				}
        				catch (Exception e) 
        				{
        					Log.e(TAG, "DeviantArt.getPublic() Error: " + e.toString());
        					e.printStackTrace();
        				}
        				
        			}
        		}
        	}

			return list;
	}
}
