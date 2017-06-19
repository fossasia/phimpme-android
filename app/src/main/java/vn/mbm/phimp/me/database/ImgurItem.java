package vn.mbm.phimp.me.database;

import android.content.Context;
import android.database.Cursor;

public class ImgurItem {
	public String account_id;
	public String token;
	public String token_secret;
	public String user_name;
	
	public String getAccountID() { return account_id;	}
	public void setAccountID(String account_id) { this.account_id = account_id; }
	
	public String getToken() { return token;	}
	public void setToken(String token) { this.token = token; }
	
	public String getTokenSecret() { return token_secret;	}
	public void setTokenSecret(String token_secret) { this.token_secret = token_secret; }
	
	public String getUserName() { return user_name;	}
	public void setUserName(String user_name) { this.user_name = user_name; }
	
	public static boolean insertImgurAccount(Context ctx, String account_id, String token, String token_secret, String user_name)
	{
		boolean result;
		
		ImgurDBAdapter db = new ImgurDBAdapter(ctx);
		db.open();
		result = db.insert(account_id, token, token_secret, user_name);
		db.close();
		
		return result;
	}
	
	public static int removeAccount(Context ctx, String id)
	{
		int result;
		
		ImgurDBAdapter db = new ImgurDBAdapter(ctx);
		db.open();
		result = db.removeAccount(id);
		db.close();
		
		return result;
	}
	
	public static ImgurItem getItem(Context ctx, String id)
	{
		ImgurItem item = new ImgurItem();
		
		ImgurDBAdapter db = new ImgurDBAdapter(ctx);
		db.open();
		Cursor c = db.getItem(id);
		if (c.moveToFirst())
		{
			String account_id = c.getString(0);
			String token = c.getString(1);
			String token_secret = c.getString(2);
			String user_name = c.getString(3);

			
			item.setAccountID(account_id);
			item.setToken(token);
			item.setTokenSecret(token_secret);
			item.setUserName(user_name);
		}
		c.close();
		db.close();
		
		return item;
	}
}
