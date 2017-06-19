package vn.mbm.phimp.me.feedservice;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;
import org.mcsoxford.rss.MediaThumbnail;
import org.mcsoxford.rss.RSSFeed;
import org.mcsoxford.rss.RSSItem;
import org.mcsoxford.rss.RSSReader;

import vn.mbm.phimp.me.PhimpMe;
import vn.mbm.phimp.me.database.AccountItem;
import vn.mbm.phimp.me.database.FlickrItem;
import vn.mbm.phimp.me.services.FlickrServices;
import vn.mbm.phimp.me.utils.RSSPhotoItem;
import vn.mbm.phimp.me.utils.RSSPhotoItem_Personal;
import vn.mbm.phimp.me.R;
import android.content.Context;
import android.net.Uri;
import android.util.Log;

public class Flickr 
{
	static final String TAG = "flickr";
	
	static final String PUBLIC_FEEDS = "http://api.flickr.com/services/feeds/photos_public.gne?format=rss2";
	
	public static final String PUBLIC_TAG = "Flickr Public Photos";
	
	static final String RECENT_FEEDS = "http://api.flickr.com/services/rest/?method=flickr.photos.getRecent";
	
	public static final String RECENT_TAG = "Flickr Recent Photos";
	
	public static final String PRIVATE_TAG = "Flickr Private Photos";
	
	public static final int ICON = R.drawable.flickr;
	
	public static ArrayList<RSSPhotoItem> getPublic(Context ctx, String tag)
	{
		ArrayList<RSSPhotoItem> list = new ArrayList<RSSPhotoItem>();
		
		try
		{
			RSSReader reader = new RSSReader();
			
			String url = PUBLIC_FEEDS;
			
			if (!tag.equals(""))
			{
				url += "?tag=" + URLEncoder.encode(tag);
			}
			
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
						rssitem.setService("public_flickr");
						rssitem.setURL(thumbnail_url.toString());
						
						list.add(rssitem);
					}
				}
			}
		}
		catch (Exception e) 
		{
			Log.e(TAG, "Flickr.getPublic() Error: " + e.toString());
			e.printStackTrace();
		}
		
		return list;
	}
	
	public static ArrayList<RSSPhotoItem> getRecent(Context ctx)
	{
		ArrayList<RSSPhotoItem> list = new ArrayList<RSSPhotoItem>();
		try
		{
			/*
			 * Reference:
			 * http://www.flickr.com/services/api/explore/flickr.photos.getRecent
			 * http://www.flickr.com/services/api/misc.urls.html
			 */
			
			int per_page = 20;
			
			String url = "http://api.flickr.com/services/rest/?method=flickr.photos.getRecent";
			url += "&api_key=" + FlickrServices.CONSUMER_KEY;
			url += "&format=json&nojsoncallback=1&per_page=" + per_page;
			url += "&extras=url_o,url_b";
			
			HttpClient client = new DefaultHttpClient();
			
			HttpGet get = new HttpGet(url);
			
			ResponseHandler<String> handler = new BasicResponseHandler();
			
			String response = client.execute(get, handler);
			
			JSONObject json = new JSONObject(response);
			
			
			if (json.getString("stat").equals("ok"))
			{
				JSONArray photos = json.getJSONObject("photos").getJSONArray("photo");
				
				for (int i = 0; i < (per_page - 1); i++)
				{
					RSSPhotoItem item = new RSSPhotoItem();
					
					JSONObject photo = (JSONObject) photos.get(i);
					
					try
					{
						String _title = photo.getString("title");
						
						String p_url = photo.getString("url_o");
					
						String p_width = photo.getString("width_o");
					
						String p_height = photo.getString("height_o");
						 
						item.setTitle(_title);
						item.setWidth(p_width);
						item.setHeight(p_height);
						item.setURL(p_url);
						item.setService("recent_flickr");	
						list.add(item);
					}
					catch (Exception e) 
					{
						
						try
						{
							String _title = photo.getString("title");
							
							String p_url = photo.getString("url_b");
						
							String p_width = photo.getString("width_b");
						
							String p_height = photo.getString("height_b");
							 
							item.setTitle(_title);
							item.setWidth(p_width);
							item.setHeight(p_height);
							item.setService("recent_flickr");
							item.setURL(p_url);
								
							list.add(item);
						}
						catch (Exception e2) 
						{
							
						}
					}
				}
			}
		}
		catch (Exception e) 
		{
			Log.e(TAG, e.toString());
			
			e.printStackTrace();
		}
		
		return list;
	}
	public static ArrayList<RSSPhotoItem_Personal> getPrivatePhotos(Context ctx, String tag)
	{		
		ArrayList<RSSPhotoItem_Personal> list = new ArrayList<RSSPhotoItem_Personal>();
		String[] id;
		String[] name;
		String[] service;
		String account_flickr="";
		
		
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
        			if(service[i].toString().equals("flickr"))
        			{
        				account_flickr +=name[i]+";";
        				Log.e(TAG,"account flickr :"+ account_flickr);
        				try
        				{
        					FlickrItem acc = FlickrItem.getItem(ctx, id[i]);
        					String uid = acc.getUserID();
        					String access_token = acc.getToken();
        					Log.d("access_token", access_token);
        					String token_secret = acc.getTokenSecret();
        					Log.d("access_token", token_secret);
        					String response = FlickrServices.getPhotos(access_token, token_secret, uid);
        					//get photo info
        					JSONObject json = new JSONObject(response);
        					String stat = json.getString("stat");
        					if(stat.equals("ok")){
        						JSONObject _json = json.getJSONObject("photos");
        						Log.d("Flickr", "json: " + _json);
        						JSONArray arr_json = _json.getJSONArray("photo");
        						Log.d("Flickr", "number of photos: " + arr_json.length());
        						for(int j=0; j<arr_json.length(); j++){
        							JSONObject json1 = new JSONObject(arr_json.getString(j));
        							String farm = json1.getString("farm");
        							String server = json1.getString("server");
        							String title = json1.getString("title");
        							String pid = json1.getString("id");
        							String secret = json1.getString("secret");
        							
		        					String url = "http://farm" + farm + ".staticflickr.com/" + server + "/" + pid + "_" + secret + ".jpg";
		        					Log.d("Flickr", url);	        					
		        					String link = url;
		        					String thumbnails = url;
		        					RSSPhotoItem_Personal rssitem = new RSSPhotoItem_Personal();
									rssitem.setDescription("");
									rssitem.setTitle(title);
									rssitem.setLink(link);
									rssitem.setURL(thumbnails);
									rssitem.setService("personal_flickr");
										
									list.add(rssitem);
		        					
	        				}
        				}
        				}
        				catch (Exception e) 
        				{
        					Log.e(TAG, "Personal Flickr Error: " + e.toString());
        					e.printStackTrace();
        				}
        				 
        			}
        		}
        	}

			return list;
	}
}
