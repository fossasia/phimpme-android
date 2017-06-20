package vn.mbm.phimp.me.database;

import android.content.Context;
import android.database.Cursor;

public class QQItem {

	public String account_id;
	public String access_token;
	public String open_id;

	public String user_name;
	public String open_key;
	
	public String getAccountID() { return account_id;	}
	public void setAccountID(String account_id) { this.account_id = account_id; }
	
	public String getAcessToken() { return access_token;	}
	public void setAcessToken(String access_token) { this.access_token = access_token; }
	
	public String getOpenId() { return open_id;	}
	public void setopenId(String open_id) { this.open_id = open_id; }
	
	
	public String getUserName() { return user_name;	}
	public void setUserName(String user_name) { this.user_name = user_name; }
	
	public String getOpenKey() { return open_key;	}
	public void setOpenKey(String open_key) { this.open_key = open_key; }
	
	public static boolean insertQQAccount(Context ctx, String account_id, String access_token,  String user_name, String openid, String openkey)
	{
		boolean result;
		
		QQDBAdapter db = new QQDBAdapter(ctx);
		db.open();
		result = db.insert(account_id, access_token,user_name,openid,openkey);
		db.close();
		
		return result;
	}
	
	public static int removeAccount(Context ctx, String id)
	{
		int result;
		
		QQDBAdapter db = new QQDBAdapter(ctx);
		db.open();
		result = db.removeAccount(id);
		db.close();
		
		return result;
	}
	
	public static QQItem getItem(Context ctx, String id)
	{
		QQItem item = new QQItem();
		
		QQDBAdapter db = new QQDBAdapter(ctx);
		db.open();
		Cursor c = db.getItem(id);
		if (c.moveToFirst())
		{
			String account_id = c.getString(0);
			String access_token = c.getString(1);
			String open_id = c.getString(2);
			String user_name = c.getString(3);
			String open_key = c.getString(4);
			
			item.setAccountID(account_id);
			item.setAcessToken(access_token);
			item.setopenId(open_id);		
			item.setUserName(user_name);
			item.setOpenKey(open_key);
		}
		c.close();
		db.close();		
		return item;
	}
	
}
