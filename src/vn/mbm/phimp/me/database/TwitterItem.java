package vn.mbm.phimp.me.database;

import android.content.Context;
import android.database.Cursor;

public class TwitterItem 
{
	public String account_id;
	public String token;
	public String token_secret;
	public String user_id;
	public String user_name;
	public String profile_url;
	
	public String getAccountID() { return account_id;	}
	public void setAccountID(String account_id) { this.account_id = account_id; }
	
	public String getToken() { return token;	}
	public void setToken(String token) { this.token = token; }
	
	public String getTokenSecret() { return token_secret;	}
	public void setTokenSecret(String token_secret) { this.token_secret = token_secret; }
	
	public String getUserID() { return user_id;	}
	public void setUserID(String user_id) { this.user_id = user_id; }
	
	public String getUserName() { return user_name;	}
	public void setUserName(String user_name) { this.user_name = user_name; }
	
	public String getProfileURL() { return profile_url;	}
	public void setProfileURL(String profile_url) { this.profile_url = profile_url; }
	
	public static boolean insertTwitterAccount(Context ctx, String account_id, String token, String token_secret, String user_id, String user_name, String profile_url)
	{
		boolean result;
		
		TwitterDBAdapter db = new TwitterDBAdapter(ctx);
		db.open();
		result = db.insert(account_id, token, token_secret, user_id, user_name, profile_url);
		db.close();
		
		return result;
	}
	
	public static int removeAccount(Context ctx, String id)
	{
		int result;
		
		TwitterDBAdapter db = new TwitterDBAdapter(ctx);
		db.open();
		result = db.removeAccount(id);
		db.close();
		
		return result;
	}
	
	public static TwitterItem getItem(Context ctx, String id)
	{
		TwitterItem item = new TwitterItem();
		
		TwitterDBAdapter db = new TwitterDBAdapter(ctx);
		db.open();
		Cursor c = db.getItem(id);
		if (c.moveToFirst())
		{
			String account_id = c.getString(0);
			String token = c.getString(1);
			String token_secret = c.getString(2);
			String user_id = c.getString(3);
			String user_name = c.getString(4);
			String profile_url = c.getString(5);
			
			item.setAccountID(account_id);
			item.setToken(token);
			item.setTokenSecret(token_secret);
			item.setUserID(user_id);
			item.setUserName(user_name);
			item.setProfileURL(profile_url);
		}
		c.close();
		db.close();
		
		return item;
	}
}
