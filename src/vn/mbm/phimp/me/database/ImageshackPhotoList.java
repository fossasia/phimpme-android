package vn.mbm.phimp.me.database;

import android.content.Context;
import android.database.Cursor;

public class ImageshackPhotoList {

	public String id;
	public String user_id;	
	public String link;
	public String service;
	
	public String getAccountID() { return user_id;	}
	public void setAccountID(String account_id) { this.user_id = account_id; }
	
	public String getId() { return id;	}
	public void setId(String id) { this.id = id; }
				
	public String getLink() { return link;	}
	public void setLink(String link) { this.link = link; }
	
	public String getService() { return service;	}
	public void setService(String service) { this.service = service; }
		
	
	public static boolean insertPhoto(Context ctx, String id, String user_id,  String link, String service)
	{
		boolean result;
		
		ImageshackPhotoListAdapter db = new ImageshackPhotoListAdapter(ctx);
		db.open();
		result = db.insert(id, user_id, link, service);
		db.close();
		
		return result;
	}
	
	public static int removeAccount(Context ctx, String id)
	{
		int result;
		
		ImageshackAdapter db = new ImageshackAdapter(ctx);
		db.open();
		result = db.removeAccount(id);
		db.close();
		
		return result;
	}
	
	public static String getItem(Context ctx, String uid)
	{
		
		String  tmp = "";
		ImageshackPhotoListAdapter db = new ImageshackPhotoListAdapter(ctx);
		db.open();
		Cursor c = db.getItem(uid);
		while (c.moveToNext())
		{								
			tmp += c.getString(2) + ";";								
			
		}
		c.close();
		db.close();		
		return tmp;
	}
	
}
