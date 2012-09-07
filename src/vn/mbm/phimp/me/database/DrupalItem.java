package vn.mbm.phimp.me.database;

import android.content.Context;
import android.database.Cursor;


public class DrupalItem 
{
	public String account_id;
	public String user_id;
	public String username;
	public String password;
	public String service_url;
	public String email;
	
	public String getAccountID() { return account_id;	}
	public void setAccountID(String account_id) { this.account_id = account_id; }
	
	public String getUserID() { return user_id;	}
	public void setUserID(String user_id) { this.user_id = user_id; }
	
	public String getUsername() { return username;	}
	public void setUsername(String username) { this.username = username; }
	
	public String getPassword() { return password;	}
	public void setPassword(String password) { this.password = password; }
	
	public String getSerivceURL() { return service_url;	}
	public void setSerivceURL(String service_url) { this.service_url = service_url; }
	
	public String getEmail() { return email;	}
	public void setEmail(String email) { this.email = email; }
	
	public static boolean insertAccount(Context ctx, String account_id, String user_id, String user_name, String password, String service_url, String email)
	{
		boolean result;
		
		DrupalDBAdapter db = new DrupalDBAdapter(ctx);
		db.open();
		result = db.insert(account_id, user_id, user_name, password, service_url, email);
		db.close();
		
		return result;
	}
	
	public static int removeAccount(Context ctx, String id)
	{
		int result;
		
		DrupalDBAdapter db = new DrupalDBAdapter(ctx);
		db.open();
		result = db.removeAccount(id);
		db.close();
		
		return result;
	}
	
	public static DrupalItem getItem(Context ctx, String id)
	{
		DrupalItem item = new DrupalItem();
		
		DrupalDBAdapter db = new DrupalDBAdapter(ctx);
		db.open();
		Cursor c = db.getItem(id);
		if (c.moveToFirst())
		{
			String account_id = c.getString(0);
			String user_id = c.getString(1);
			String user_name = c.getString(2);
			String password = c.getString(3);
			String service_url = c.getString(4);
			String email = c.getString(5);
			
			item.setAccountID(account_id);
			item.setUserID(user_id);
			item.setUsername(user_name);
			item.setPassword(password);
			item.setSerivceURL(service_url);
			item.setEmail(email);
		}
		c.close();
		db.close();
		
		return item;
	}
}
