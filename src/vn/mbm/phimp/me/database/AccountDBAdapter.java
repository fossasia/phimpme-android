package vn.mbm.phimp.me.database;

import vn.mbm.phimp.me.PhimpMe;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class AccountDBAdapter 
{
	private DatabaseHelperAccounts DBHelper;
	private SQLiteDatabase db;
	
	public static final String ID = "id";
	public static final String NAME = "name";
	public static final String SERVICE = "service";
	public static final String ACTIVE = "active";
	
	private static final String DATABASE_TABLE = "accounts";
	private static final int DATABASE_VERSION = 1;
	
	private static final String DATABASE_CREATE = 
		"create table if not exists " + DATABASE_TABLE + " ("
		+ ID + " INTEGER PRIMARY KEY,"
		+ NAME +" text null,"
		+ SERVICE +" text null,"
		+ ACTIVE +" text null) ;";	
		
	private Context context;
		
	public AccountDBAdapter(Context ctx)
	{
		this.context = ctx;
		DBHelper = new DatabaseHelperAccounts(context);
	}
	
	private static class DatabaseHelperAccounts extends SQLiteOpenHelper
	{

		DatabaseHelperAccounts(Context context) 
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
	
	public AccountDBAdapter open() throws SQLException
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
	
	public long insert(String id, String name, String service, String active) 
	{
		ContentValues initialValues = new ContentValues();
		initialValues.put(ID, id);
		initialValues.put(NAME, name);
		initialValues.put(SERVICE, service);
		initialValues.put(ACTIVE, active);
		
		long result = db.insert(DATABASE_TABLE, null, initialValues);
		
		return result;
	}
	
	public Cursor getAllAccounts()
	{
		return db.query(
				DATABASE_TABLE, 
				new String[] { ID, NAME, SERVICE, ACTIVE }, 
				null, 
				null, 
				null, 
				null, 
				SERVICE + ", " + NAME);
	}
	
	public int removeAccount(String id)
	{
		return db.delete(DATABASE_TABLE, ID + "=?", new String[] {id});
	}
	
	public void clearDB() 
	{
		db.delete(DATABASE_TABLE, null, null);
	}
}
