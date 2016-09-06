package vn.mbm.phimp.me.feedservice;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.mcsoxford.rss.MediaThumbnail;
import org.mcsoxford.rss.RSSFeed;
import org.mcsoxford.rss.RSSItem;
import org.mcsoxford.rss.RSSReader;

import vn.mbm.phimp.me.PhimpMe;
import vn.mbm.phimp.me.database.AccountItem;
import vn.mbm.phimp.me.database.ImgurItem;
import vn.mbm.phimp.me.services.ImgurServices;
import vn.mbm.phimp.me.utils.RSSPhotoItem;
import vn.mbm.phimp.me.utils.RSSPhotoItem_Personal;
import vn.mbm.phimp.me.R;
import android.content.Context;
import android.net.Uri;
import android.util.Log;

public class Imgur {
	public static final String TAG = "Imgur";
	public static final String IMGUR_PERSONAL_TAG = "Imgur Personal";
	public static final String PUBLIC_TAG="Imgur Public";
	public static final int ICON = R.drawable.imgur;
	static final String PUBLIC_FEEDS = "http://feeds.feedburner.com/ImgurGallery?format=xml";

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
						rssitem.setService("public_imgur");
						rssitem.setURL(thumbnail_url.toString());
						
						list.add(rssitem);
					}
				}
			}
		}
		catch (Exception e) 
		{
			Log.e(TAG, "Imgur.getPublic() Error: " + e.toString());
			e.printStackTrace();
		}
		
		return list;
	}
	
	
	public static ArrayList<RSSPhotoItem_Personal> getPersonalPhotos(Context ctx, String tag)
	{
		ArrayList<RSSPhotoItem_Personal> list = new ArrayList<RSSPhotoItem_Personal>();
		String[] id;
		String[] name;
		String[] service;
		String account_imgur="";
		
		
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
        			if(service[i].toString().equals("imgur"))
        			{
        				account_imgur +=name[i]+";";
        				Log.e(TAG,"account imgur :"+ account_imgur);
        				try
        				{        					
        					ImgurItem acc = ImgurItem.getItem(ctx, id[i]);
        					String token = acc.getToken();
        					Log.e("Imgur feed", "token : "+token);
        					String token_secret = acc.getTokenSecret();
        					Log.e("Imgur feed","token_secret : "+ token_secret);
        					String response = ImgurServices.getPersonalPhotos(token, token_secret);
        					Log.d(TAG, "Imgur get personal response : " + response);
        					
        					//get photo info
        					JSONObject json = new JSONObject(response);        					
        					JSONArray arr_json = json.getJSONArray("images");
        					
        					Log.d("Imgur", "number of photos: " + arr_json.length());
        					
        					for(int j=0; j< arr_json.length(); j++){
        						JSONObject _json = arr_json.getJSONObject(j);
        						String image=_json.getString("image");
        						String links=_json.getString("links");
        						JSONObject js=new JSONObject(image);
        						JSONObject js1=new JSONObject(links);
        						
        						String description = js.getString("name");
        						String title = js.getString("title");
        						String link = js1.getString("original");
        						String thumbnails = js1.getString("small_square");
        						Log.d("Imgur", "description: "+description+" title: "+title+" link:"+link+"thumbnails : "+thumbnails);
        						
        						RSSPhotoItem_Personal rssitem = new RSSPhotoItem_Personal();
    							rssitem.setDescription(description);
    							rssitem.setTitle(title);
    							rssitem.setLink(link);
    							rssitem.setURL(link);
    							rssitem.setService("personal_imgur");
    								
    							list.add(rssitem);
        					}
        					    					
        					
        				}
        				catch (Exception e) 
        				{
        					Log.e(TAG, "Imgur Error: " + e.toString());
        					e.printStackTrace();
        				}

             
        			}
        		}
        	}
			Log.d("list", list.size() + "");
			return list;
	}
	
	
}
