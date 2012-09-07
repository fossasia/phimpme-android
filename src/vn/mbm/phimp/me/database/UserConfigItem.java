package vn.mbm.phimp.me.database;

import android.content.Context;
import android.database.Cursor;

public class UserConfigItem {

	public String services;
	public String status;
	
	
	public String getServices() { return services;	}
	public void setServices(String services) { this.services = services; }
	
	public String getStatus() { return status;	}
	public void setStatus(String status) { this.status = status; }
	
	public static boolean insertUserConfig(Context ctx, String services, String status)
	{
		boolean result;
		
		UserConfigDBAdapter db = new UserConfigDBAdapter(ctx);
		db.open();
		result = db.insert(services, status);
		db.close();		
		return result;
	}
	public static boolean updateUserConfig(Context ctx,String services, String status){
		boolean result;
		UserConfigDBAdapter db=new UserConfigDBAdapter(ctx);
		db.open();
		result=db.update(services, status);
		db.close();
		return result;
	}
	
	
	public static UserConfigItem getItem(Context ctx, String services)
	{
		
		UserConfigItem item = new UserConfigItem();
		
		UserConfigDBAdapter db = new UserConfigDBAdapter(ctx);
		db.open();
		Cursor c = db.getItem(services);
		if (c.moveToFirst())
		{
			String serv = c.getString(0);
			String status = c.getString(1);

			item.setServices(serv);
			item.setStatus(status);
			
			
		}
		c.close();
		db.close();
		
		return item;
	}
	
}
