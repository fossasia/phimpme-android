package vn.mbm.phimp.me.database;

import vn.mbm.phimp.me.PhimpMe;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ImageshackPhotoListAdapter {
	private DatabaseHelperImageshackPhotoListAdapter DBHelper;
	private SQLiteDatabase db;
	public static final int DATABASE_VERSION = 1;
	public static final String ID = "id_photo";
	public static final String USER_ID = "user_id";
	public static final String LINK = "link";
	public static final String SERVICE = "service";
	public static final String DATABASE_TABLE = "photo_imageshack";
	public static final String DATABASE_CREATE = "create table if not exists " + DATABASE_TABLE + " ("
			+ ID + " INTEGER PRIMARY KEY,"
			+ USER_ID +" INTERGER,"						
			+ LINK +" text null,"
			+ SERVICE +" text null) ;";
	private Context context;
	public ImageshackPhotoListAdapter(Context ctx)
	{
		this.context = ctx;
		DBHelper = new DatabaseHelperImageshackPhotoListAdapter(context);
	}
	private static class  DatabaseHelperImageshackPhotoListAdapter extends SQLiteOpenHelper
	{

		 DatabaseHelperImageshackPhotoListAdapter(Context context) 
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
	public ImageshackPhotoListAdapter open() throws SQLException
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
	
	public boolean insert(String id, String user_id, String link, String service) 
	{
		ContentValues initialValues = new ContentValues();
		initialValues.put(ID, id);
		initialValues.put(USER_ID, user_id);					
		initialValues.put(LINK, link);			
		initialValues.put(SERVICE, service);	
		
		long result = db.insert(DATABASE_TABLE, null, initialValues);
		
		return (result > 0);
	}
	
	public int removeAccount(String id)
	{
		return db.delete(DATABASE_TABLE, ID + "=?", new String[] {id});
	}
	
	public void clearDB() 
	{
		db.delete(DATABASE_TABLE, null, null);
	}
	
	public Cursor getItem(String id)
	{
		String selection = USER_ID + " = ? ";
		String[] agruments = new String[] {id};
		
		return db.query(
				DATABASE_TABLE, 
				new String[] { ID, USER_ID,  LINK, SERVICE}, 
				selection, 
				agruments, 
				null, 
				null, 
				null);
	}
}

