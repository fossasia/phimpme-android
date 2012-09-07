package vn.mbm.phimp.me.feedservice;

import java.util.ArrayList;

import vn.mbm.phimp.me.PhimpMe;
import vn.mbm.phimp.me.database.AccountItem;
import vn.mbm.phimp.me.database.ImageshackPhotoList;
import vn.mbm.phimp.me.utils.RSSPhotoItem_Personal;
import android.content.Context;
import android.util.Log;

public class Imageshack {
	public static final String PRIVATE_TAG = "Imageshack photo";
	
	
	public static ArrayList<RSSPhotoItem_Personal> getPhoto(Context ctx)
	{
		Log.d("imageshack", "start");
		ArrayList<RSSPhotoItem_Personal> list = new ArrayList<RSSPhotoItem_Personal>();
		String[] id;
		String[] name;
		String[] service;		
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
    			Log.d("imageshack",service[i].toString());
    			if (service[i].toString().equals("imageshack"))
    			{
    				Log.d("imageshack",name[i].toString());
    				String path = ImageshackPhotoList.getItem(ctx, id[i]);
    				Log.d("link", path);
    				try
    				{
    																								
    					String[] items =path.split(";");
    					
    					if (items.length > 0)
    					{	
    						for (int j = 0; j < items.length; j++)
    						{
    								RSSPhotoItem_Personal rssitem = new RSSPhotoItem_Personal();
    									rssitem.setDescription("");
    								rssitem.setTitle("");
    								rssitem.setLink(items[j].toString());
    								rssitem.setURL(items[j].toString()); 
    								rssitem.setService("personal_imageshack");
    							list.add(rssitem);
    							}
    						}
    					}    				
    				catch (Exception e) 
    				{
    					Log.e("Imageshack " ,"Imageshack.getprivate() Error: " + e.toString());
    					e.printStackTrace();
    				}
    			}
    		}
    		}
	
		
		return list;
	}
	
}
