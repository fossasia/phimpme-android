package vn.mbm.phimp.me.database;

import android.content.Context;
import android.database.Cursor;

public class JoomlaItem {

	public String account_id;
	public String url;
	public String username;
	public String password;
	public String services;
	public String cat_id;
	
	public String getAccountID() { return account_id;	}
	public void setAccountID(String account_id) { this.account_id = account_id; }
	
	public String getUrl() { return url;	}
	public void setUrl(String url) { this.url = url; }
	
	public String getUsername() { return username;	}
	public void setUsername(String username) { this.username = username; }
	
	public String getPassword() { return password;	}
	public void setPassword(String password) { this.password = password; }
	
	
	public String getServices() { return services;	}
	public void setServices(String services) { this.services = services; }
	
	public String getCatId(){return cat_id;};
	public void setCateId(String cat){cat_id = cat;}
	
	public static boolean insertJoomlaAccount(Context ctx, String account_id, String url, String username, String password,String services, String cat)
	{
		boolean result;
		
		JoomlaDBAdapter db = new JoomlaDBAdapter(ctx);
		db.open();
		result = db.insert(account_id, url,username,password,services,cat);
		db.close();
		
		return result;
	}
	public static int removeAccount(Context ctx, String id)
	{
		int result;
		
		JoomlaDBAdapter db = new JoomlaDBAdapter(ctx);
		db.open();
		result = db.removeAccount(id);
		db.close();
		
		return result;
	}
	
	public static JoomlaItem getItem(Context ctx, String id)
	{
		
		JoomlaItem item = new JoomlaItem();
		
		JoomlaDBAdapter db = new JoomlaDBAdapter(ctx);
		db.open();
		Cursor c = db.getItem(id);
		if (c.moveToFirst())
		{
			String account_id = c.getString(0);
			String url = c.getString(1);
			String username = c.getString(2);
			String password = c.getString(3);		
			String services=c.getString(4);
			String cat = c.getString(5);
			
			item.setAccountID(account_id);
			item.setUrl(url);
			item.setUsername(username);
			item.setPassword(password);
			item.setServices(services);
			item.setCateId(cat);
			
			
		}
		c.close();
		db.close();
		
		return item;
	}
	
}
