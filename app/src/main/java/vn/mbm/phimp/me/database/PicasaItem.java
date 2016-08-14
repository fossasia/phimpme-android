package vn.mbm.phimp.me.database;

import android.content.Context;
import android.database.Cursor;

public class PicasaItem 
{
	public String account_id;
	public String user_id;
	public String user_name;
	public String email;
	public String profile_url;
	public String access_token;
	public String token_type;
	public String id_token;
	public String refresh_token;
	
	public String getAccountID() { return account_id;	}
	public void setAccountID(String account_id) { this.account_id = account_id; }
	
	public String getUserID() { return user_id;	}
	public void setUserID(String user_id) { this.user_id = user_id; }
	
	public String getUserName() { return user_name;	}
	public void setUserName(String user_name) { this.user_name = user_name; }
	
	public String getEmail() { return email;	}
	public void setEmail(String email) { this.email = email; }
	
	public String getProfileURL() { return profile_url;	}
	public void setProfileURL(String profile_url) { this.profile_url = profile_url; }
	
	public String getAccessToken() { return access_token;	}
	public void setAccessToken(String access_token) { this.access_token = access_token; }
	
	public String getTokenType() { return token_type;	}
	public void setTokenType(String token_type) { this.token_type = token_type; }
	
	public String getIDToken() { return id_token;	}
	public void setIDToken(String id_token) { this.id_token = id_token; }
	
	public String getRefreshToken() { return refresh_token;	}
	public void setRefreshToken(String refresh_token) { this.refresh_token = refresh_token; }
	
	public static boolean insertPicasaAccount(Context ctx, String account_id, String user_id, String user_name, String email, String profile_url, String access_token, String token_type, String id_token, String refresh_token)
	{
		boolean result;
		
		PicasaDBAdapter db = new PicasaDBAdapter(ctx);
		db.open();
		result = db.insert(account_id, user_id, user_name, email, profile_url, access_token, token_type, id_token, refresh_token);
		db.close();
		
		return result;
	}
	
	public static int removeAccount(Context ctx, String id)
	{
		int result;
		
		PicasaDBAdapter db = new PicasaDBAdapter(ctx);
		db.open();
		result = db.removeAccount(id);
		db.close();
		
		return result;
	}
	
	public static boolean updateRefreshToken(Context ctx, String account_id, String user_id, String user_name, String access_token, String token_type, String id_token, String refresh_token)
	{
		boolean result;
		
		PicasaDBAdapter db = new PicasaDBAdapter(ctx);
		db.open();
		result = db.update(account_id, user_id, user_name, access_token, token_type, id_token, refresh_token);
		db.close();
		
		return result;
	}
	
	public static PicasaItem getItem(Context ctx, String id)
	{
		PicasaItem item = new PicasaItem();
		
		PicasaDBAdapter db = new PicasaDBAdapter(ctx);
		db.open();
		Cursor c = db.getItem(id);
		if (c.moveToFirst())
		{
			String account_id = c.getString(0);
			String user_id = c.getString(1);
			String user_name = c.getString(2);
			String email = c.getString(3);
			String profile_url = c.getString(4);
			String access_token = c.getString(5);
			String token_type = c.getString(6);
			String id_token = c.getString(7);
			String refresh_token = c.getString(8);
			
			item.setAccountID(account_id);
			item.setUserID(user_id);
			item.setUserName(user_name);
			item.setEmail(email);
			item.setProfileURL(profile_url);
			item.setAccessToken(access_token);
			item.setTokenType(token_type);
			item.setIDToken(id_token);
			item.setRefreshToken(refresh_token);
		}
		c.close();
		db.close();
		
		return item;
	}
}
