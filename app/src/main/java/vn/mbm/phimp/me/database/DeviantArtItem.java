package vn.mbm.phimp.me.database;

import android.content.Context;
import android.database.Cursor;

public class DeviantArtItem {

	public String account_id;
	public String access_token;
	public String refresh_token;

	public String user_name;
	public String user_iconurl;
	
	public String getAccountID() { return account_id;	}
	public void setAccountID(String account_id) { this.account_id = account_id; }
	
	public String getAcessToken() { return access_token;	}
	public void setAcessToken(String access_token) { this.access_token = access_token; }
	
	public String getRefreshToken() { return refresh_token;	}
	public void setRefreshToken(String refresh_token) { this.refresh_token = refresh_token; }
	
	
	
	public String getUserName() { return user_name;	}
	public void setUserName(String user_name) { this.user_name = user_name; }
	
	public String getUserIconURL() { return user_iconurl;	}
	public void setUserIconURL(String user_iconurl) { this.user_iconurl = user_iconurl; }
	
	public static boolean insertDeviantArtAccount(Context ctx, String account_id, String access_token, String refresh_token,  String user_name, String user_iconurl)
	{
		boolean result;
		
		DeviantArtDBAdapter db = new DeviantArtDBAdapter(ctx);
		db.open();
		result = db.insert(account_id, access_token, refresh_token,user_name, user_iconurl);
		db.close();
		
		return result;
	}
	public static boolean updateDeviantArtAccount(Context ctx,String account_id,String access_token,String refresh_token){
		boolean result;
		DeviantArtDBAdapter db=new DeviantArtDBAdapter(ctx);
		db.open();
		result=db.update(account_id, access_token, refresh_token);
		db.close();
		return result;
	}
	public static int removeAccount(Context ctx, String id)
	{
		int result;
		
		DeviantArtDBAdapter db = new DeviantArtDBAdapter(ctx);
		db.open();
		result = db.removeAccount(id);
		db.close();
		
		return result;
	}
	
	public static DeviantArtItem getItem(Context ctx, String id)
	{
		
		DeviantArtItem item = new DeviantArtItem();
		
		DeviantArtDBAdapter db = new DeviantArtDBAdapter(ctx);
		db.open();
		Cursor c = db.getItem(id);
		if (c.moveToFirst())
		{
			String account_id = c.getString(0);
			String access_token = c.getString(1);
			String refresh_token = c.getString(2);
			
			String user_name = c.getString(3);
			String user_iconurl = c.getString(4);
			item.setAccountID(account_id);
			item.setAcessToken(access_token);
			item.setRefreshToken(refresh_token);
		
			item.setUserName(user_name);
			item.setUserIconURL(user_iconurl);
			
			
		}
		c.close();
		db.close();
		
		return item;
	}
	
}
