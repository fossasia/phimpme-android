package vn.mbm.phimp.me.database;

import vn.mbm.phimp.me.PhimpMe;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ImageshackAdapter {

	private DatabaseHelperImageshack DBHelper;
	private SQLiteDatabase db;
	
	public static final String ACCOUNT_ID = "account_id";
	public static final String REGISTRATOR_CODE = "registrator_code";	
	
	public static final String USER_NAME = "user_name";	
	
	private static final String DATABASE_TABLE = "account_imageshack";
	private static final int DATABASE_VERSION = 1;
	
	private static final String DATABASE_CREATE = 
			"create table if not exists " + DATABASE_TABLE + " ("
			+ ACCOUNT_ID + " INTEGER PRIMARY KEY,"
			+ REGISTRATOR_CODE +" text null,"						
			+ USER_NAME +" text null) ;";	
			
		private Context context;
			
		public ImageshackAdapter(Context ctx)
		{
			this.context = ctx;
			DBHelper = new DatabaseHelperImageshack(context);
		}
		
		private static class DatabaseHelperImageshack extends SQLiteOpenHelper
		{

			DatabaseHelperImageshack(Context context) 
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
		
		public ImageshackAdapter open() throws SQLException
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
		
		public boolean insert(String account_id, String registrator_code, String user_name) 
		{
			ContentValues initialValues = new ContentValues();
			initialValues.put(ACCOUNT_ID, account_id);
			initialValues.put(REGISTRATOR_CODE, registrator_code);					
			initialValues.put(USER_NAME, user_name);			
			
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
					new String[] { ACCOUNT_ID, REGISTRATOR_CODE,  USER_NAME}, 
					selection, 
					agruments, 
					null, 
					null, 
					null);
		}
	
}
