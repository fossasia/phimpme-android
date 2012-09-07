package vn.mbm.phimp.me.database;

import android.content.Context;
import android.database.Cursor;

public class ImageshackItem {

	public String account_id;
	public String registrator_code;	
	public String user_name;	
	
	public String getAccountID() { return account_id;	}
	public void setAccountID(String account_id) { this.account_id = account_id; }
	
	public String getRegistratorCode() { return registrator_code;	}
	public void setRegistratorCode(String registrator_code) { this.registrator_code = registrator_code; }
				
	public String getUserName() { return user_name;	}
	public void setUserName(String user_name) { this.user_name = user_name; }
		
	
	public static boolean insertAccount(Context ctx, String account_id, String registratorcode,  String user_name)
	{
		boolean result;
		
		ImageshackAdapter db = new ImageshackAdapter(ctx);
		db.open();
		result = db.insert(account_id, registratorcode, user_name);
		db.close();
		
		return result;
	}
	
	public static int removeAccount(Context ctx, String id)
	{
		int result;
		
		ImageshackAdapter db = new ImageshackAdapter(ctx);
		db.open();
		result = db.removeAccount(id);
		db.close();
		
		return result;
	}
	
	public static ImageshackItem getItem(Context ctx, String id)
	{
		ImageshackItem item = new ImageshackItem();
		
		ImageshackAdapter db = new ImageshackAdapter(ctx);
		db.open();
		Cursor c = db.getItem(id);
		if (c.moveToFirst())
		{
			String account_id = c.getString(0);
			String registrator_code = c.getString(1);						
			String user_name = c.getString(2);						
			item.setAccountID(account_id);
			item.setRegistratorCode(registrator_code);					
			item.setUserName(user_name);
			
		}
		c.close();
		db.close();
		
		return item;
	}
	
}
