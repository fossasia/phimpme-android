package vn.mbm.phimp.me.database;

import android.content.Context;
import android.database.Cursor;

public class KaixinDBItem 
{
	public String account_id;
	public String token;	
	public String refresh_token;
	public String user_id;
	public String user_name;
	public String album_id;
	
	public String getAccountID() { return account_id;	}
	public void setAccountID(String account_id) { this.account_id = account_id; }
	
	public String getToken() { return token;	}
	public void setToken(String token) { this.token = token; }
	
	public String getRefreshToken() { return refresh_token;	}
	public void setRefreshToken(String refresh_token) { this.refresh_token = refresh_token; }
	
	public String getUserID() { return user_id;	}
	public void setUserID(String user_id) { this.user_id = user_id; }
	
	public String getUserName() { return user_name;	}
	public void setUserName(String user_name) { this.user_name = user_name; }
	
	public String getAlbumId() { return album_id;	}
	public void setAlbumId(String album_id) { this.album_id = album_id; }
	
	public static boolean insertKaixinAccount(Context ctx, String account_id, String token, String refresh_token, String user_id, String user_name, String album_id)
	{
		boolean result;
		
		KaixinDBAdapter db = new KaixinDBAdapter(ctx);
		db.open();
		result = db.insert(account_id, token, refresh_token, user_id, user_name, album_id);
		db.close();
		return result;
	}
	
	public static int removeAccount(Context ctx, String id)
	{
		int result;
		
		KaixinDBAdapter db = new KaixinDBAdapter(ctx);
		db.open();
		result = db.removeAccount(id);
		db.close();
		
		return result;
	}
	
	public static KaixinDBItem getItem(Context ctx, String id)
	{
		KaixinDBItem item = new KaixinDBItem();
		
		KaixinDBAdapter db = new KaixinDBAdapter(ctx);
		db.open();
		Cursor c = db.getItem(id);
		if (c.moveToFirst())
		{
			String account_id = c.getString(0);
			String token = c.getString(1);	
			String refresh_token = c.getString(2);
			String user_id = c.getString(3);
			String user_name = c.getString(4);
			String album_id = c.getString(5);
			
			item.setAccountID(account_id);
			item.setToken(token);
			item.setRefreshToken(refresh_token);
			item.setUserID(user_id);
			item.setUserName(user_name);
			item.setAlbumId(album_id);
		}
		c.close();
		db.close();
		
		return item;
	}
}
