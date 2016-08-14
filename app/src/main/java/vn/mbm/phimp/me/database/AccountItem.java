package vn.mbm.phimp.me.database;

import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;

public class AccountItem 
{
	public String id;
	public String name;
	public String service;
	public String active;
	
	public String getID() { return id;	}
	public void setID(String id) { this.id = id; }
	
	public String getName() { return name;	}
	public void setName(String name) { this.name = name; }
	
	public String getService() { return service;	}
	public void setService(String service) { this.service = service; }
	
	public String getActive() { return active;	}
	public void setActive(String active) { this.active = active; }
	
	public static long insertAccount(Context ctx, String id, String name, String service, String active)
	{
		long result;
		
		AccountDBAdapter db = new AccountDBAdapter(ctx);
		db.open();
		result = db.insert(id, name, service, active);
		db.close();
		
		return result;
	}
	
	public static int removeAccount(Context ctx, String id)
	{
		int result;
		
		AccountDBAdapter db = new AccountDBAdapter(ctx);
		db.open();
		result = db.removeAccount(id);
		db.close();
		
		return result;
	}
	
	public static ArrayList<AccountItem> getAllAccounts(Context ctx)
	{
		ArrayList<AccountItem> list = new ArrayList<AccountItem>();
		
		AccountDBAdapter db = new AccountDBAdapter(ctx);
		db.open();
		
		Cursor c = db.getAllAccounts();
		
		if (c.moveToFirst())
		{
			do
			{
				AccountItem item = new AccountItem();
				
				String id = c.getString(0);
				String name = c.getString(1);
				String service = c.getString(2);
				String active = c.getString(3);
				
				item.setID(id);
				item.setName(name);
				item.setService(service);
				item.setActive(active);
				
				list.add(item);
			}
			while (c.moveToNext());
		}
		
		c.deactivate();
		
		db.close();
		
		return list;
	}
}
