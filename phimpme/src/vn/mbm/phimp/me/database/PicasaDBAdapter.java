package vn.mbm.phimp.me.database;

import vn.mbm.phimp.me.PhimpMe;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class PicasaDBAdapter 
{
	private DatabaseHelperPicasa DBHelper;
	private SQLiteDatabase db;
	
	public static final String ACCOUNT_ID = "account_id";
	public static final String USER_ID = "user_id";
	public static final String USER_NAME = "user_name";
	public static final String EMAIL = "email";
	public static final String PROFILE_URL = "profile_url";
	public static final String ACCESS_TOKEN = "access_token";
	public static final String TOKEN_TYPE = "token_type";
	public static final String ID_TOKEN = "id_token";
	public static final String REFRESH_TOKEN = "refresh_token";
	
	private static final String DATABASE_TABLE = "account_picasa";
	private static final int DATABASE_VERSION = 1;
	
	private static final String DATABASE_CREATE = 
		"create table if not exists " + DATABASE_TABLE + " ("
		+ ACCOUNT_ID + " INTEGER PRIMARY KEY,"
		+ USER_ID +" text null,"
		+ USER_NAME +" text null,"
		+ EMAIL +" text null,"
		+ PROFILE_URL +" text null,"
		+ ACCESS_TOKEN +" text null,"
		+ TOKEN_TYPE +" text null,"
		+ ID_TOKEN +" text null,"
		+ REFRESH_TOKEN +" text null) ;";	
		
	private Context context;
		
	public PicasaDBAdapter(Context ctx)
	{
		this.context = ctx;
		DBHelper = new DatabaseHelperPicasa(context);
	}
	
	private static class DatabaseHelperPicasa extends SQLiteOpenHelper
	{

		DatabaseHelperPicasa(Context context) 
		{
			super(context, PhimpMe.DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) 
		{
			try
			{
				db.execSQL(DATABASE_CREATE);
			}
			catch (SQLException e)
			{
			}
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) 
		{
			db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
			onCreate(db);
		}
	}
	
	public PicasaDBAdapter open() throws SQLException
	{
		db = DBHelper.getWritableDatabase();
		DBHelper.onCreate(db);
		return this;
	}
	
	public void close()
	{
		DBHelper.close();
		db.close();
	}
	
	public boolean insert(String account_id, String user_id, String user_name, String email, String profile_url, String access_token, String token_type, String id_token, String refresh_token) 
	{
		ContentValues initialValues = new ContentValues();
		initialValues.put(ACCOUNT_ID, account_id);
		initialValues.put(USER_ID, user_id);
		initialValues.put(USER_NAME, user_name);
		initialValues.put(EMAIL, email);
		initialValues.put(PROFILE_URL, profile_url);
		initialValues.put(ACCESS_TOKEN, access_token);
		initialValues.put(TOKEN_TYPE, token_type);
		initialValues.put(ID_TOKEN, id_token);
		initialValues.put(REFRESH_TOKEN, refresh_token);
		
		long result = db.insert(DATABASE_TABLE, null, initialValues);
		
		return (result > 0);
	}
	
	public boolean update(String account_id, String user_id, String user_name, String access_token, String token_type, String id_token, String refresh_token)
	{
		ContentValues initialValues = new ContentValues();
		initialValues.put(ACCESS_TOKEN, access_token);
		initialValues.put(TOKEN_TYPE, token_type);
		initialValues.put(ID_TOKEN, id_token);
		initialValues.put(REFRESH_TOKEN, refresh_token);
		
		String whereClause = ACCOUNT_ID + "=? AND " + USER_ID + "=? AND " + USER_NAME + "=?";
		String[] whereArgs = new String[] {account_id, user_id, user_name};
		
		long result = db.update(DATABASE_TABLE, initialValues, whereClause, whereArgs);
		
		return (result > 0);
	}
	
	public int removeAccount(String id)
	{
		return db.delete(DATABASE_TABLE, ACCOUNT_ID + "=?", new String[] {id});
	}
	
	public void clearDB() 
	{
		db.delete(DATABASE_TABLE, null, null);
	}
	
	public Cursor getItem(String id)
	{
		String selection = ACCOUNT_ID + " = ? ";
		String[] agruments = new String[] {id};
		
		return db.query(
				DATABASE_TABLE, 
				new String[] { ACCOUNT_ID, USER_ID, USER_NAME, EMAIL, PROFILE_URL, ACCESS_TOKEN, TOKEN_TYPE, ID_TOKEN, REFRESH_TOKEN }, 
				selection, 
				agruments, 
				null, 
				null, 
				null);
	}
}
