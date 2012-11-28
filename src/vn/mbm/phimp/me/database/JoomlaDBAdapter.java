package vn.mbm.phimp.me.database;

import vn.mbm.phimp.me.PhimpMe;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class JoomlaDBAdapter {

	private DatabaseHelperJoomla DBHelper;
	private SQLiteDatabase db;
	
	public static final String ACCOUNT_ID = "account_id";
	public static final String URL="url";
	public static final String USER_NAME = "user_name";
	public static final String PASSWORD = "password";
	public static final String SERVICES = "services";
	public static final String CAT_ID = "cat_id";
	private static final String DATABASE_TABLE = "joomla";
	private static final int DATABASE_VERSION = 1;
	
	private static final String DATABASE_CREATE = 
			"create table if not exists " + DATABASE_TABLE + " ("
			+ ACCOUNT_ID + " INTEGER PRIMARY KEY,"
			+ URL +" text null,"
			+ USER_NAME +" text null,"
			+ PASSWORD +" text null,"
			+ CAT_ID + " text null,"
			+ SERVICES +" text null) ;";	
			
		private Context context;
			
		public JoomlaDBAdapter(Context ctx)
		{
			this.context = ctx;
			DBHelper = new DatabaseHelperJoomla(context);
		}
		
		private static class DatabaseHelperJoomla extends SQLiteOpenHelper
		{

			DatabaseHelperJoomla(Context context) 
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
		
		public JoomlaDBAdapter open() throws SQLException
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
		
		public boolean insert(String account_id, String url, String username, String password, String services, String cat_id ) 
		{
			ContentValues initialValues = new ContentValues();
			initialValues.put(ACCOUNT_ID, account_id);
			initialValues.put(URL, url);
			initialValues.put(USER_NAME, username);			
			initialValues.put(PASSWORD, password);
			initialValues.put(SERVICES, services);
			initialValues.put(CAT_ID, cat_id);
		
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
					new String[] { ACCOUNT_ID, URL, USER_NAME, PASSWORD,SERVICES, CAT_ID }, 
					selection, 
					agruments, 
					null, 
					null, 
					null);
		}
	
}
