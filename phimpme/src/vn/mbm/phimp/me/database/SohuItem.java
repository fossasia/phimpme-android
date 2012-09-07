package vn.mbm.phimp.me.database;

import android.content.Context;
import android.database.Cursor;

public class SohuItem {
	public String account_id;
	public String user_id;
	public String token;
	public String token_secret;
	public String user_name;
	
	public String getAccountID() { return account_id;	}
	public void setAccountID(String account_id) { this.account_id = account_id; }
	
	public String getUserID() { return user_id;	}
	public void setUserID(String user_id) { this.user_id = user_id; }
	
	public String getToken() { return token;	}
	public void setToken(String token) { this.token = token; }
	
	public String getTokenSecret() { return token_secret;	}
	public void setTokenSecret(String token_secret) { this.token_secret = token_secret; }
	
	public String getUserName() { return user_name;	}
	public void setUserName(String user_name) { this.user_name = user_name; }
	
	public static boolean insertSohuAccount(Context ctx, String account_id,String user_id, String token, String token_secret, String user_name)
	{
		boolean result;
		
		SohuDBAdapter db = new SohuDBAdapter(ctx);
		db.open();
		result = db.insert(account_id,user_id, token, token_secret, user_name);
		db.close();
		
		return result;
	}
	
	public static int removeAccount(Context ctx, String id)
	{
		int result;
		
		SohuDBAdapter db = new SohuDBAdapter(ctx);
		db.open();
		result = db.removeAccount(id);
		db.close();
		
		return result;
	}
	
	public static SohuItem getItem(Context ctx, String id)
	{
		SohuItem item = new SohuItem();
		
		SohuDBAdapter db = new SohuDBAdapter(ctx);
		db.open();
		Cursor c = db.getItem(id);
		if (c.moveToFirst())
		{
			String account_id = c.getString(0);
			String user_id = c.getString(1);
			String token = c.getString(2);
			String token_secret = c.getString(3);
			String user_name = c.getString(4);

			
			item.setAccountID(account_id);
			item.setUserID(user_id);
			item.setToken(token);
			item.setTokenSecret(token_secret);
			item.setUserName(user_name);
		}
		c.close();
		db.close();
		
		return item;
	}
}
