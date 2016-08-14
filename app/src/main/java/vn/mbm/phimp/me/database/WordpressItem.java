package vn.mbm.phimp.me.database;

import android.content.Context;
import android.database.Cursor;

public class WordpressItem {

	public String account_id;
	public String url;
	public String username;
	public String password;
	public String http_username;
	public String http_password;
	public String services;
	
	public String getAccountID() { return account_id;	}
	public void setAccountID(String account_id) { this.account_id = account_id; }
	
	public String getUrl() { return url;	}
	public void setUrl(String url) { this.url = url; }
	
	public String getUsername() { return username;	}
	public void setUsername(String username) { this.username = username; }
	
	public String getPassword() { return password;	}
	public void setPassword(String password) { this.password = password; }
	
	public String getHttpUsername() { return http_username;	}
	public void setHttpUsername(String http_username) { this.http_username = http_username; }
	
	public String getHttp_password() { return http_password;	}
	public void setHttp_password(String http_password) { this.http_password = http_password; }
	
	public String getServices() { return services;	}
	public void setServices(String services) { this.services = services; }
	
	public static boolean insertWordpressAccount(Context ctx, String account_id, String url, String username,  String password, String http_username,String http_password,String services)
	{
		boolean result;
		
		WordpressDBAdapter db = new WordpressDBAdapter(ctx);
		db.open();
		result = db.insert(account_id, url,username,password,http_username,http_password,services);
		db.close();
		
		return result;
	}
	public static int removeAccount(Context ctx, String id)
	{
		int result;
		
		WordpressDBAdapter db = new WordpressDBAdapter(ctx);
		db.open();
		result = db.removeAccount(id);
		db.close();
		
		return result;
	}
	
	public static WordpressItem getItem(Context ctx, String id)
	{
		
		WordpressItem item = new WordpressItem();
		
		WordpressDBAdapter db = new WordpressDBAdapter(ctx);
		db.open();
		Cursor c = db.getItem(id);
		if (c.moveToFirst())
		{
			String account_id = c.getString(0);
			String url = c.getString(1);
			String username = c.getString(2);
			String password = c.getString(3);		
			String http_username = c.getString(4);
			String http_password = c.getString(5);
			String services=c.getString(6);
			
			item.setAccountID(account_id);
			item.setUrl(url);
			item.setUsername(username);
			item.setPassword(password);
			item.setHttpUsername(http_username);
			item.setHttp_password(http_password);
			item.setServices(services);
			
			
		}
		c.close();
		db.close();
		
		return item;
	}
	
}
