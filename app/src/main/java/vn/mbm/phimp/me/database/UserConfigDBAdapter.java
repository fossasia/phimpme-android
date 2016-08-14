package vn.mbm.phimp.me.database;


import vn.mbm.phimp.me.PhimpMe;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class UserConfigDBAdapter {

	private DatabaseHelperUserConfig DBHelper;
	private SQLiteDatabase db;
	
	public static final String services = "serv";
	public static final String status="sta";

	private static final String DATABASE_TABLE = "user_config";
	private static final int DATABASE_VERSION = 1;
	
	private static final String DATABASE_CREATE = 
			"create table if not exists " + DATABASE_TABLE + " ("
			+ services + " STRING PRIMARY KEY,"
			+ status +" text null,";	
			
		private Context context;
			
		public UserConfigDBAdapter(Context ctx)
		{
			this.context = ctx;
			DBHelper = new DatabaseHelperUserConfig(context);
		}
		
		private static class DatabaseHelperUserConfig extends SQLiteOpenHelper
		{

			DatabaseHelperUserConfig(Context context) 
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
		
		public UserConfigDBAdapter open() throws SQLException
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
		
		public boolean insert(String serv, String sta ) 
		{
			ContentValues initialValues = new ContentValues();
			initialValues.put(services, serv);
			initialValues.put(status, sta);			

			
			long result = db.insert(DATABASE_TABLE, null, initialValues);
			
			return (result > 0);
		}
		public boolean update(String serv,String sta){
			ContentValues initialValues = new ContentValues();
			initialValues.put(status, sta);			
			
			
			
			long result=db.update(DATABASE_TABLE, initialValues, serv, null);
			return (result>0);
		}
		
		
		
		public void clearDB() 
		{
			db.delete(DATABASE_TABLE, null, null);
		}
		
		public Cursor getItem(String serv)
		{
			String selection = services + " = ? ";
			String[] agruments = new String[] {serv};
			
			return db.query(
					DATABASE_TABLE, 
					new String[] { services, status }, 
					selection, 
					agruments, 
					null, 
					null, 
					null);
		}

}
