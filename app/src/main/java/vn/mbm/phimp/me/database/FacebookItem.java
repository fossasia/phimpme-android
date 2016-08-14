package vn.mbm.phimp.me.database;

import android.content.Context;
import android.database.Cursor;

public class FacebookItem 
{
	public String account_id;
	public String access_token;
	public String user_id;
	public String user_name;
	public String user_fullname;
	public String email;
	public String profile_url;
	
	public String getAccountID() { return account_id; }
	public void setAccountID(String account_id) { this.account_id = account_id; }
	
	public String getAccessToken() { return access_token; }
	public void setAccessToken(String access_token) { this.access_token = access_token; }
	
	public String getUserID() { return user_id;	}
	public void setUserID(String user_id) { this.user_id = user_id; }
	
	public String getUserName() { return user_name;	}
	public void setUserName(String user_name) { this.user_name = user_name; }
	
	public String getUserFullName() { return user_fullname;	}
	public void setUserFullName(String user_fullname) { this.user_fullname = user_fullname; }
	
	public String getEmail() { return email;	}
	public void setEmail(String email) { this.email = email; }
	
	public String getProfileURL() { return profile_url;	}
	public void setProfileURL(String profile_url) { this.profile_url = profile_url; }
	
	public static boolean insertFacebookAccount(Context ctx, String account_id, String access_token, String user_id, String user_name, String user_fullname, String email, String profile_url)
	{
		boolean result;
		
		FacebookDBAdapter db = new FacebookDBAdapter(ctx);
		db.open();
		result = db.insert(account_id, access_token, user_id, user_name, user_fullname, email, profile_url);
		db.close();
		
		return result;
	}
	
	public static int removeAccount(Context ctx, String id)
	{
		int result;
		
		FacebookDBAdapter db = new FacebookDBAdapter(ctx);
		db.open();
		result = db.removeAccount(id);
		db.close();
		
		return result;
	}
	
	public static FacebookItem getItem(Context ctx, String id)
	{
		FacebookItem item = new FacebookItem();
		
		FacebookDBAdapter db = new FacebookDBAdapter(ctx);
		db.open();
		Cursor c = db.getItem(id);
		if (c.moveToFirst())
		{
			String account_id = c.getString(0);
			String access_token = c.getString(1);
			String user_id = c.getString(2);
			String user_name = c.getString(3);
			String user_fullname = c.getString(4);
			String profile_url = c.getString(5);
			
			item.setAccountID(account_id);
			item.setAccessToken(access_token);
			item.setUserID(user_id);
			item.setUserName(user_name);
			item.setUserFullName(user_fullname);
			item.setProfileURL(profile_url);
		}
		c.close();
		db.close();
		
		return item;
	}
}
