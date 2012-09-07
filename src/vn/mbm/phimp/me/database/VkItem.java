package vn.mbm.phimp.me.database;

import android.content.Context;
import android.database.Cursor;

public class VkItem 
{
	public String account_id;
	public String token;	
	public String user_id;
	public String user_name;
	public String user_uploads;
	
	public String getAccountID() { return account_id;	}
	public void setAccountID(String account_id) { this.account_id = account_id; }
	
	public String getToken() { return token;	}
	public void setToken(String token) { this.token = token; }
			
	public String getUserID() { return user_id;	}
	public void setUserID(String user_id) { this.user_id = user_id; }
	
	public String getUserName() { return user_name;	}
	public void setUserName(String user_name) { this.user_name = user_name; }
	
	public String getUserUpload() { return user_uploads;	}
	public void setUserUpload(String upload_url) { this.user_uploads = upload_url; }
	
	public static boolean insertVkAccount(Context ctx, String account_id, String token, String user_id, String user_name, String profile_url)
	{
		boolean result;
		
		VkDBAdapter db = new VkDBAdapter(ctx);
		db.open();
		result = db.insert(account_id, token, user_id, user_name, profile_url);
		db.close();
		return result;
	}
	
	public static int removeAccount(Context ctx, String id)
	{
		int result;
		
		VkDBAdapter db = new VkDBAdapter(ctx);
		db.open();
		result = db.removeAccount(id);
		db.close();
		
		return result;
	}
	
	public static VkItem getItem(Context ctx, String id)
	{
		VkItem item = new VkItem();
		
		VkDBAdapter db = new VkDBAdapter(ctx);
		db.open();
		Cursor c = db.getItem(id);
		if (c.moveToFirst())
		{
			String account_id = c.getString(0);
			String token = c.getString(1);			
			String user_id = c.getString(2);
			String user_name = c.getString(4);
			String profile_url = c.getString(3);
			
			item.setAccountID(account_id);
			item.setToken(token);			
			item.setUserID(user_id);
			item.setUserName(user_name);
			item.setUserUpload(profile_url);
		}
		c.close();
		db.close();
		
		return item;
	}
}
