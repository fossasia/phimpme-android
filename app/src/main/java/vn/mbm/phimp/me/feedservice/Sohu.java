package vn.mbm.phimp.me.feedservice;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import vn.mbm.phimp.me.PhimpMe;
import vn.mbm.phimp.me.database.AccountItem;
import vn.mbm.phimp.me.database.SohuItem;
import vn.mbm.phimp.me.services.SohuServices;
import vn.mbm.phimp.me.utils.RSSPhotoItem_Personal;
import vn.mbm.phimp.me.R;
import android.content.Context;
import android.util.Log;

public class Sohu {
	public static final String TAG = "Sohu";
	public static final String SOHU_PERSONAL_TAG = "Sohu Personal";
	public static final int ICON = R.drawable.sohu;
	
	public static ArrayList<RSSPhotoItem_Personal> getPersonalPhotos(Context ctx, String tag)
	{
		ArrayList<RSSPhotoItem_Personal> list = new ArrayList<RSSPhotoItem_Personal>();
		String[] id;
		String[] name;
		String[] service;
		String account_sohu="";
		
		
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
        			if(service[i].toString().equals("sohu"))
        			{
        				account_sohu +=name[i]+";";
        				Log.e(TAG,"account sohu :"+ account_sohu);
        				try
        				{      
     					
        					SohuItem acc = SohuItem.getItem(ctx, id[i]);
        					String user_id=acc.getUserID();
        					String token=acc.getToken();
        					String token_secret=acc.getTokenSecret();
        					Log.e(TAG,"user_id sohu :"+ user_id);
        					String respone=SohuServices.getPersonalPhotos(token, token_secret, user_id);
        					Log.d(tag, "response : "+respone);
        					
        					JSONArray js=new JSONArray(respone);
        					Log.d(tag, "photo : "+ js.length());
        					for(int j=0;j<js.length();j++){
        						JSONObject json=js.getJSONObject(j);
        						String thumb=json.getString("small_pic");
        						String link=json.getString("original_pic");
        						RSSPhotoItem_Personal rssitem = new RSSPhotoItem_Personal();
								rssitem.setDescription("");
								rssitem.setTitle("");
								rssitem.setLink(link);
								rssitem.setURL(thumb);
								rssitem.setService("personal_sohu");
								
								list.add(rssitem);
        					}
    					
        				}
        				catch (Exception e) 
        				{
        					Log.e(TAG, "Sohu Error: " + e.toString());
        					e.printStackTrace();
        				}           
        			}
        		}
        	}
			Log.d("list", list.size() + "");
			return list;
	}
}
