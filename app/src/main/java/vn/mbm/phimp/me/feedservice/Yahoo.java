package vn.mbm.phimp.me.feedservice;

import java.util.ArrayList;
import java.util.List;

import org.mcsoxford.rss.MediaThumbnail;
import org.mcsoxford.rss.RSSFeed;
import org.mcsoxford.rss.RSSItem;
import org.mcsoxford.rss.RSSReader;

import vn.mbm.phimp.me.utils.RSSPhotoItem;
import vn.mbm.phimp.me.R;
import android.content.Context;
import android.net.Uri;
import android.util.Log;

public class Yahoo 
{
	static final String TAG = "yahoo";
	
	static final String NEWS_FEED = "http://news.yahoo.com/rss/";
	
	public static final String NEWS_TAG = "Yahoo! News";
	
	public static final int ICON = R.drawable.yahoo;
	
	@SuppressWarnings("resource")
	public static ArrayList<RSSPhotoItem> getYahooNews(Context ctx)
	{
		ArrayList<RSSPhotoItem> list = new ArrayList<RSSPhotoItem>();
		
		Log.d(TAG, "Run getFeedsFromYahooNews()");
		
		try
		{
			RSSReader reader = new RSSReader();
			
			RSSFeed feeds = reader.load(NEWS_FEED);
			
			List<RSSItem> items = feeds.getItems();
			
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
						String[] path = thumbnail_url.getPath().split("http://");
						String url_img = path[1];						
						Uri _uri = Uri.parse(url_img);
						if (_uri.getScheme() == null)
						{
							url_img = "http://" + url_img;
						}
						
						RSSPhotoItem rssitem = new RSSPhotoItem();
						rssitem.setDescription(description);
						rssitem.setTitle(title);
						rssitem.setLink(link.toString());
						rssitem.setURL(url_img);
						rssitem.setService("public_yahoo");
						list.add(rssitem);
					}
				}
			}
		}
		catch (Exception e) 
		{
			e.printStackTrace();
			
			Log.e(TAG, "getFeedsFromYahooNews() " + e.toString());
		}
		
		return list;
	}
}
