package vn.mbm.phimp.me.database;

import vn.mbm.phimp.me.PhimpMe;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DownloadedPersonalPhotoDBAdapter {
	private DatabaseHelper DBHelper;
	private SQLiteDatabase db;
	
	public static final String ID = "id";
	public static final String FILEPATH = "filepath";
	public static final String THUMBPATH = "thumbpath";
	public static final String TITLE = "title";
	public static final String LATITUDE = "latitude";
	public static final String LONGITUDE = "longitude";
	public static final String LINK = "link";
	public static final String SERVICE = "service";
	public static final String DESCRIPTION = "description";
	
	private static final String DATABASE_TABLE = "rssphotoitem_personal";
	private static final int DATABASE_VERSION = 1;
	
	private static final String DATABASE_CREATE = 
		"create table if not exists " + DATABASE_TABLE + " ("
		+ ID + " INTEGER PRIMARY KEY,"
		+ FILEPATH +" text null,"
		+ THUMBPATH +" text null,"
		+ TITLE +" text null,"
		+ LATITUDE +" text null,"
		+ LONGITUDE +" text null,"
		+ LINK +" text null,"
		+ SERVICE + " text null,"
		+ DESCRIPTION +" text null) ;";	
		
	private Context context;
		
	public DownloadedPersonalPhotoDBAdapter(Context ctx)
	{
		this.context = ctx;
		DBHelper = new DatabaseHelper(context);
	}
	
	private static class DatabaseHelper extends SQLiteOpenHelper
	{

		DatabaseHelper(Context context) 
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
	
	public synchronized DownloadedPersonalPhotoDBAdapter open() throws RuntimeException, SQLException
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
	
	public synchronized boolean insert(String id, String filepath, String thumbpath, String title, String latitude, String longitude, String link, String service, String description) 
	{
		Log.d("database", "Insert RSSPhoto: " + id + " / " + filepath + " / " + thumbpath + " / " + title);
		
		ContentValues initialValues = new ContentValues();
		initialValues.put(ID, id);
		initialValues.put(FILEPATH, filepath);
		initialValues.put(THUMBPATH, thumbpath);
		initialValues.put(TITLE, title);
		initialValues.put(LATITUDE, latitude);
		initialValues.put(LONGITUDE, longitude);
		initialValues.put(LINK, link);
		initialValues.put(SERVICE, service);
		initialValues.put(DESCRIPTION, description);
		
		long result = db.insert(DATABASE_TABLE, null, initialValues);
		
		return (result > 0);
	}
	
	public Cursor getAll()
	{
		return db.query(
				DATABASE_TABLE, 
				new String[] { ID, FILEPATH, THUMBPATH, TITLE, LATITUDE, LONGITUDE, LINK, SERVICE, DESCRIPTION }, 
				null, 
				null, 
				null, 
				null, 
				ID + " desc");
	}
	
	public synchronized int removeAccount(String id)
	{
		return db.delete(DATABASE_TABLE, ID + "=?", new String[] {id});
	}
	
	public void clearDB() 
	{
		db.delete(DATABASE_TABLE, null, null);
	}
}
