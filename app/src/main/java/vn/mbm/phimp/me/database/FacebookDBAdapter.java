package vn.mbm.phimp.me.database;

import vn.mbm.phimp.me.PhimpMe;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class FacebookDBAdapter 
{
	private DatabaseHelperFacebook DBHelper;
	private SQLiteDatabase db;
	
	public static final String ACCOUNT_ID = "account_id";
	public static final String ACCESS_TOKEN = "access_token";
	public static final String USER_ID = "user_id";
	public static final String USER_NAME = "user_name";
	public static final String USER_FULLNAME = "user_fullname";
	public static final String EMAIL = "email";
	public static final String PROFILE_URL = "profile_url";
	
	private static final String DATABASE_TABLE = "account_facebook";
	private static final int DATABASE_VERSION = 1;
	
	private static final String DATABASE_CREATE = 
		"create table if not exists " + DATABASE_TABLE + " ("
		+ ACCOUNT_ID + " INTEGER PRIMARY KEY,"
		+ ACCESS_TOKEN +" text null,"
		+ USER_ID +" text null,"
		+ USER_NAME +" text null,"
		+ USER_FULLNAME +" text null,"
		+ EMAIL +" text null,"
		+ PROFILE_URL +" text null) ;";	
		
	private Context context;
		
	public FacebookDBAdapter(Context ctx)
	{
		this.context = ctx;
		DBHelper = new DatabaseHelperFacebook(context);
	}
	
	private static class DatabaseHelperFacebook extends SQLiteOpenHelper
	{

		DatabaseHelperFacebook(Context context) 
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
	
	public FacebookDBAdapter open() throws SQLException
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
	
	public boolean insert(String account_id, String access_token, String user_id, String user_name, String user_fullname, String email, String profile_url) 
	{
		ContentValues initialValues = new ContentValues();
		initialValues.put(ACCOUNT_ID, account_id);
		initialValues.put(ACCESS_TOKEN, access_token);
		initialValues.put(USER_ID, user_id);
		initialValues.put(USER_NAME, user_name);
		initialValues.put(USER_FULLNAME, user_fullname);
		initialValues.put(EMAIL, email);
		initialValues.put(PROFILE_URL, profile_url);
		
		long result = db.insert(DATABASE_TABLE, null, initialValues);
		
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
				new String[] { ACCOUNT_ID, ACCESS_TOKEN, USER_ID, USER_NAME, USER_FULLNAME, EMAIL, PROFILE_URL }, 
				selection, 
				agruments, 
				null, 
				null, 
				null);
	}
}
