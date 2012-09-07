package vn.mbm.phimp.me;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

import vn.mbm.phimp.me.database.AccountDBAdapter;
import vn.mbm.phimp.me.database.TumblrDBAdapter;
import vn.mbm.phimp.me.utils.Commons;
import vn.mbm.phimp.me.utils.RSSPhotoItem;
import vn.mbm.phimp.me.utils.RSSPhotoItem_Personal;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.TabActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.Toast;

import com.google.ads.AdRequest;
import com.google.ads.AdView;
import com.google.android.maps.GeoPoint;
@SuppressWarnings("deprecation")
public class PhimpMe extends TabActivity 
{
	public static Context ctx;
	public static File DataDirectory;
	public static final String PREFS_NAME = "PhimpMePrefs";
	public static final String DATABASE_NAME = "PhimpMe";
	private static final String DATA_DIRECTORY_NAME = "phimp.me";
	public static int MAX_DISPLAY_PHOTOS;
	public static int MAX_FILESIZE_DOWNLOAD;
	/* Hon Nguyen */
	public static String phimp_me_tmp;
	public static Uri phimp_me_img_uri_temporary;
	
	public static boolean FEEDS_LIST_YAHOO_NEWS;
	public static final String FEEDS_LIST_YAHOO_NEWS_TAG = "feeds_list_yahoo_news";
	public static boolean FEEDS_LOCAL_GALLERY;
	public static final String FEEDS_LOCAL_GALLERY_TAG = "feeds_local_gallery";
	public static int check=0;
	public static boolean FEEDS_LIST_FLICKR_PUBLIC;
	public static final String FEEDS_LIST_FLICKR_PUBLIC_TAG = "feeds_list_flickr_public";
	public static boolean FEEDS_LIST_FLICKR_RECENT;
	public static final String FEEDS_LIST_FLICKR_RECENT_TAG = "feeds_list_flickr_recent";
	public static boolean FEEDS_LIST_FLICKR_PRIVATE;
	public static final String FEEDS_LIST_FLICKR_PRIVATE_TAG = "feeds_list_flickr_private";
	
	public static boolean FEEDS_LIST_GOOGLE_PICASA_PUBLIC;
	public static final String FEEDS_LIST_GOOGLE_PICASA_PUBLIC_TAG = "feeds_list_google_picasa_public";
	public static boolean FEEDS_LIST_GOOGLE_NEWS;
	public static final String FEEDS_LIST_GOOGLE_NEWS_TAG = "feeds_list_google_news";
	public static boolean FEEDS_LIST_GOOGLE_PICASA_PRIVATE;
	public static final String FEEDS_LIST_GOOGLE_PICASA_PRIVATE_TAG = "feeds_list_google_picasa_private";
	
	public static boolean FEEDS_LIST_DEVIANTART_PUBLIC;
	public static final String FEEDS_LIST_DEVIANTART_PUBLIC_TAG = "feeds_list_deviantart_public";
	public static boolean FEEDS_LIST_DEVIANTART_PRIVITE;
	public static final String FEEDS_LIST_DEVIANTART_PRIVITE_TAG = "feeds_list_deviantart_privite";
	
	public static boolean FEEDS_LIST_IMAGESHACK_PRIVITE;
	public static final String FEEDS_LIST_IMAGESHACK_PRIVITE_TAG = "feeds_list_imageshack_privite";
	
	public static boolean FEEDS_LIST_VK;
	public static final String FEEDS_LIST_VK_TAG = "feeds_list_vk";
	
	public static boolean FEEDS_LIST_FACEBOOK_PRIVATE;
	public static final String FEEDS_LIST_FACEBOOK_PRIVATE_TAG = "feeds_list_facebook_private";
	
	public static boolean FEEDS_LIST_TUMBLR_PRIVATE;
	public static final String FEEDS_LIST_TUMBLR_PRIVATE_TAG = "feeds_list_tumblr_private";
	
	public static boolean FEEDS_LIST_TWITTER_PRIVATE;
	public static final String FEEDS_LIST_TWITTER_PRIVATE_TAG = "feeds_list_twitter_private";
	
	public static boolean FEEDS_LIST_KAIXIN_PRIVATE;
	public static final String FEEDS_LIST_KAIXIN_PRIVATE_TAG = "feeds_list_twitter_private";
	
	public static boolean FEEDS_LIST_IMGUR_PERSONAL;
	public static final String FEEDS_LIST_IMGUR_PERSONAL_TAG = "feeds_list_imgur_personal";
	public static boolean FEEDS_LIST_IMGUR_PUBLIC;
	public static final String FEEDS_LIST_IMGUR_PUBLIC_TAG = "feeds_list_imgur_public";
	
	public static boolean FEEDS_LIST_MYSERVICES;
	public static boolean FEEDS_LIST_MYSERVICES1;
	public static boolean FEEDS_LIST_MYSERVICES2;
	public static boolean FEEDS_LIST_MYSERVICES3;
	public static boolean FEEDS_LIST_MYSERVICES4;
	public static boolean FEEDS_LIST_MYSERVICES5;
	public static final String FEDDS_LIST_MYSERVICES_TAG="feeds_list_myservices";
	public static final String FEDDS_LIST_MYSERVICES_TAG1="feeds_list_myservices";
	public static final String FEDDS_LIST_MYSERVICES_TAG2="feeds_list_myservices";
	public static final String FEDDS_LIST_MYSERVICES_TAG3="feeds_list_myservices";
	public static final String FEDDS_LIST_MYSERVICES_TAG4="feeds_list_myservices";
	public static final String FEDDS_LIST_MYSERVICES_TAG5="feeds_list_myservices";
	public static String MY_FEED_URL="";
	
	public static boolean FEEDS_LIST_500PX_PRIVATE;
	public static final String FEEDS_LIST_500PX_PRIVATE_TAG = "feeds_list_500px_private";
	public static boolean FEEDS_LIST_500PX_PUBLIC;
	public static final String FEEDS_LIST_500PX_PUBLIC_TAG = "feeds_list_500px_public";
	
	public static boolean FEEDS_LIST_SOHU_PERSONAL;
	public static final String FEEDS_LIST_SOHU_PERSONAL_TAG = "feeds_list_sohu_personal";
	
	public static boolean add_account_upload, add_account_setting;
	public static HashMap<String, Boolean> checked_accounts = new HashMap<String, Boolean>();
	public static Uri UploadPhotoPreview;
	
	public static boolean addCurrentPin = false;
	
	public static GeoPoint currentGeoPoint;
	
	public static Double curLatitude, curLongtitude;
	
	public static Double UploadLatitude, UploadLongitude;
	public static LinearLayout popupTabs ;
	public static int camera_use;
	//LOCAL
	public static ArrayList<String> filepath = new ArrayList<String>();
	public static ArrayList<Integer> IdList;
	public static int local_count = 1;
	ProgressDialog progConfig;
	//new Gallery
	static ArrayList<ArrayList<RSSPhotoItem>> phimpme_array_list = new ArrayList<ArrayList<RSSPhotoItem>>() ;
	static ArrayList<ArrayList<RSSPhotoItem_Personal>> phimpme_personal_array_list = new ArrayList<ArrayList<RSSPhotoItem_Personal>>() ;
	//Cache
	public static CacheStore cache;
	public static CacheTask cachetask;
	//Crash Report
	public static String CRITTERCISM_APP_ID = "4fffa20fbe790e4bc7000002";
	boolean serviceDisabled = false;
	
	public static boolean check_export = true;
	public static TabHost mTabHost;
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
    	super.onCreate(savedInstanceState);
    	ctx = this;
    	Log.d("thong", "PhimpMe - onCreate()");
    	camera_use = 0;
    	if (IdList == null) IdList = new ArrayList<Integer>();
    	    	
        setContentView(R.layout.main);
        //Crash report
        //Crittercism.init(getApplicationContext(), CRITTERCISM_APP_ID, serviceDisabled);
        add_account_upload = false;
        add_account_setting = false;
        
        cache=CacheStore.getInstance();
        cachetask = new CacheTask();
        String[] str = null;
        	cachetask.execute(str);
        	
        /*
         * Google admod
         */
    	AdView adView = (AdView)this.findViewById(R.id.adView);
        adView.loadAd(new AdRequest());
        /*
         * Export data
         */
        if(check_export = true){
        	//exportDevicesInfomation();
        	//exportInstalledPakage();        	
        	Intent intent = new Intent(this,CollectUserData.class);
        	//startService(intent);
        	check_export = false;
        }        
        /*
         * user config
         */
        
        File file = getBaseContext().getFileStreamPath("local_gallery.txt");
		if(file.exists()){
			try {
				FileInputStream Rfile = openFileInput("local_gallery.txt");
				
				InputStreamReader einputreader = new InputStreamReader(Rfile);
				BufferedReader ebuffreader = new BufferedReader(einputreader);
				Boolean tmp = Boolean.valueOf(ebuffreader.readLine());
				PhimpMe.FEEDS_LOCAL_GALLERY = tmp;
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		
		File file1 = getBaseContext().getFileStreamPath("flickr_public.txt");
		if(file1.exists()){
			try {
				FileInputStream Rfile = openFileInput("flickr_public.txt");
				
				InputStreamReader einputreader = new InputStreamReader(Rfile);
				BufferedReader ebuffreader = new BufferedReader(einputreader);
				Boolean tmp = Boolean.valueOf(ebuffreader.readLine());
				PhimpMe.FEEDS_LIST_FLICKR_PUBLIC = tmp;
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		File file2 = getBaseContext().getFileStreamPath("flickr_recent.txt");
		if(file2.exists()){
			try {
				FileInputStream Rfile = openFileInput("flickr_recent.txt");
				
				InputStreamReader einputreader = new InputStreamReader(Rfile);
				BufferedReader ebuffreader = new BufferedReader(einputreader);
				Boolean tmp = Boolean.valueOf(ebuffreader.readLine());
				PhimpMe.FEEDS_LIST_FLICKR_RECENT = tmp;
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
    	
		File file3 = getBaseContext().getFileStreamPath("google_news.txt");
		if(file3.exists()){
			try {
				FileInputStream Rfile = openFileInput("google_news.txt");
				
				InputStreamReader einputreader = new InputStreamReader(Rfile);
				BufferedReader ebuffreader = new BufferedReader(einputreader);
				Boolean tmp = Boolean.valueOf(ebuffreader.readLine());
				PhimpMe.FEEDS_LIST_GOOGLE_NEWS = tmp;
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
    	
		File file4 = getBaseContext().getFileStreamPath("public_picasa.txt");
		if(file4.exists()){
			try {
				FileInputStream Rfile = openFileInput("public_picasa.txt");
				
				InputStreamReader einputreader = new InputStreamReader(Rfile);
				BufferedReader ebuffreader = new BufferedReader(einputreader);
				Boolean tmp = Boolean.valueOf(ebuffreader.readLine());
				PhimpMe.FEEDS_LIST_GOOGLE_PICASA_PUBLIC = tmp;
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		File file5 = getBaseContext().getFileStreamPath("yahoo_news.txt");
		if(file5.exists()){
			try {
				FileInputStream Rfile = openFileInput("yahoo_news.txt");
				
				InputStreamReader einputreader = new InputStreamReader(Rfile);
				BufferedReader ebuffreader = new BufferedReader(einputreader);
				Boolean tmp = Boolean.valueOf(ebuffreader.readLine());
				PhimpMe.FEEDS_LIST_YAHOO_NEWS = tmp;
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
    	
		File file6 = getBaseContext().getFileStreamPath("deviant_public.txt");
		if(file6.exists()){
			try {
				FileInputStream Rfile = openFileInput("deviant_public.txt");
				
				InputStreamReader einputreader = new InputStreamReader(Rfile);
				BufferedReader ebuffreader = new BufferedReader(einputreader);
				Boolean tmp = Boolean.valueOf(ebuffreader.readLine());
				PhimpMe.FEEDS_LIST_DEVIANTART_PUBLIC = tmp;
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
    	
		File file7 = getBaseContext().getFileStreamPath("flick_private.txt");
		if(file7.exists()){
			try {
				FileInputStream Rfile = openFileInput("flick_private.txt");
				
				InputStreamReader einputreader = new InputStreamReader(Rfile);
				BufferedReader ebuffreader = new BufferedReader(einputreader);
				Boolean tmp = Boolean.valueOf(ebuffreader.readLine());
				PhimpMe.FEEDS_LIST_FLICKR_PRIVATE = tmp;
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		File file8 = getBaseContext().getFileStreamPath("picasa_private.txt");
		if(file8.exists()){
			try {
				FileInputStream Rfile = openFileInput("picasa_private.txt");
				
				InputStreamReader einputreader = new InputStreamReader(Rfile);
				BufferedReader ebuffreader = new BufferedReader(einputreader);
				Boolean tmp = Boolean.valueOf(ebuffreader.readLine());
				PhimpMe.FEEDS_LIST_GOOGLE_PICASA_PRIVATE = tmp;
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
    	
		File file9 = getBaseContext().getFileStreamPath("deviant_private.txt");
		if(file9.exists()){
			try {
				FileInputStream Rfile = openFileInput("deviant_private.txt");
				
				InputStreamReader einputreader = new InputStreamReader(Rfile);
				BufferedReader ebuffreader = new BufferedReader(einputreader);
				Boolean tmp = Boolean.valueOf(ebuffreader.readLine());
				PhimpMe.FEEDS_LIST_DEVIANTART_PRIVITE = tmp;
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
    	
		File file10 = getBaseContext().getFileStreamPath("vk.txt");
		if(file10.exists()){
			try {
				FileInputStream Rfile = openFileInput("vk.txt");
				
				InputStreamReader einputreader = new InputStreamReader(Rfile);
				BufferedReader ebuffreader = new BufferedReader(einputreader);
				Boolean tmp = Boolean.valueOf(ebuffreader.readLine());
				PhimpMe.FEEDS_LIST_VK = tmp;
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
    	
		File file11 = getBaseContext().getFileStreamPath("facebook.txt");
		if(file11.exists()){
			try {
				FileInputStream Rfile = openFileInput("facebook.txt");
				
				InputStreamReader einputreader = new InputStreamReader(Rfile);
				BufferedReader ebuffreader = new BufferedReader(einputreader);
				Boolean tmp = Boolean.valueOf(ebuffreader.readLine());
				PhimpMe.FEEDS_LIST_FACEBOOK_PRIVATE = tmp;
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
    	
		File file12 = getBaseContext().getFileStreamPath("tumblr_private.txt");
		if(file12.exists()){
			try {
				FileInputStream Rfile = openFileInput("tumblr_private.txt");
				
				InputStreamReader einputreader = new InputStreamReader(Rfile);
				BufferedReader ebuffreader = new BufferedReader(einputreader);
				Boolean tmp = Boolean.valueOf(ebuffreader.readLine());
				PhimpMe.FEEDS_LIST_TUMBLR_PRIVATE = tmp;
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
    	
		File file13 = getBaseContext().getFileStreamPath("imgur_personal.txt");
		if(file13.exists()){
			try {
				FileInputStream Rfile = openFileInput("imgur_personal.txt");
				
				InputStreamReader einputreader = new InputStreamReader(Rfile);
				BufferedReader ebuffreader = new BufferedReader(einputreader);
				Boolean tmp = Boolean.valueOf(ebuffreader.readLine());
				PhimpMe.FEEDS_LIST_IMGUR_PERSONAL = tmp;
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		File file14 = getBaseContext().getFileStreamPath("sohu_personal.txt");
		if(file14.exists()){
			try {
				FileInputStream Rfile = openFileInput("sohu_personal.txt");
				
				InputStreamReader einputreader = new InputStreamReader(Rfile);
				BufferedReader ebuffreader = new BufferedReader(einputreader);
				Boolean tmp = Boolean.valueOf(ebuffreader.readLine());
				PhimpMe.FEEDS_LIST_SOHU_PERSONAL = tmp;
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
        TabSpec ts;
        View tbview;
        Intent intent;
     
        /*
         * Thong - Load preferences
         */
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        MAX_DISPLAY_PHOTOS = settings.getInt("gallery_max_display_photos", getResources().getInteger(R.integer.gallery_max_display_photos));
        MAX_FILESIZE_DOWNLOAD = settings.getInt("max_filesize_download", getResources().getInteger(R.integer.max_filesize_download));
        FEEDS_LIST_FLICKR_PUBLIC = settings.getBoolean(FEEDS_LIST_FLICKR_PUBLIC_TAG, true);
        FEEDS_LIST_FLICKR_RECENT = settings.getBoolean(FEEDS_LIST_FLICKR_RECENT_TAG, true);
        FEEDS_LOCAL_GALLERY=settings.getBoolean(FEEDS_LOCAL_GALLERY_TAG, true);
        /*FEEDS_LIST_YAHOO_NEWS = settings.getBoolean(FEEDS_LIST_YAHOO_NEWS_TAG, false);       
        FEEDS_LIST_GOOGLE_PICASA_PUBLIC = settings.getBoolean(FEEDS_LIST_GOOGLE_PICASA_PUBLIC_TAG, false);
        FEEDS_LIST_GOOGLE_NEWS = settings.getBoolean(FEEDS_LIST_GOOGLE_NEWS_TAG, false);
        FEEDS_LIST_VK = settings.getBoolean(FEEDS_LIST_VK_TAG, false);
        FEEDS_LIST_FACEBOOK_PRIVATE = settings.getBoolean(FEEDS_LIST_FACEBOOK_PRIVATE_TAG, false);
        FEEDS_LIST_FLICKR_PRIVATE = settings.getBoolean(FEEDS_LIST_FLICKR_PRIVATE_TAG, false);
        FEEDS_LIST_GOOGLE_PICASA_PRIVATE= settings.getBoolean(FEEDS_LIST_GOOGLE_PICASA_PRIVATE_TAG, false);
        FEEDS_LIST_TUMBLR_PRIVATE= settings.getBoolean(FEEDS_LIST_TUMBLR_PRIVATE_TAG, false);
        FEEDS_LIST_DEVIANTART_PRIVITE= settings.getBoolean(FEEDS_LIST_DEVIANTART_PRIVITE_TAG, false);
        FEEDS_LIST_DEVIANTART_PUBLIC= settings.getBoolean(FEEDS_LIST_DEVIANTART_PUBLIC_TAG, false);
        FEEDS_LIST_GOOGLE_PICASA_PRIVATE= settings.getBoolean(FEEDS_LIST_GOOGLE_PICASA_PRIVATE_TAG, false);
        FEEDS_LIST_TWITTER_PRIVATE= settings.getBoolean(FEEDS_LIST_TWITTER_PRIVATE_TAG, false);
        FEEDS_LIST_KAIXIN_PRIVATE= settings.getBoolean(FEEDS_LIST_KAIXIN_PRIVATE_TAG, false);
        FEEDS_LIST_IMGUR_PERSONAL= settings.getBoolean(FEEDS_LIST_IMGUR_PERSONAL_TAG, false);
        FEEDS_LIST_MYSERVICES= settings.getBoolean(FEDDS_LIST_MYSERVICES_TAG, false);
        FEEDS_LIST_IMGUR_PUBLIC= settings.getBoolean(FEEDS_LIST_IMGUR_PUBLIC_TAG, false);
        FEEDS_LIST_500PX_PRIVATE=settings.getBoolean(FEEDS_LIST_500PX_PRIVATE_TAG, false);
        FEEDS_LIST_500PX_PUBLIC= settings.getBoolean(FEEDS_LIST_500PX_PUBLIC_TAG, false);
        FEEDS_LIST_SOHU_PERSONAL= settings.getBoolean(FEEDS_LIST_SOHU_PERSONAL_TAG, false);
        FEEDS_LIST_MYSERVICES1= settings.getBoolean(FEDDS_LIST_MYSERVICES_TAG1, false);
        FEEDS_LIST_MYSERVICES2= settings.getBoolean(FEDDS_LIST_MYSERVICES_TAG2, false);
        FEEDS_LIST_MYSERVICES3= settings.getBoolean(FEDDS_LIST_MYSERVICES_TAG3, false);
        FEEDS_LIST_MYSERVICES4= settings.getBoolean(FEDDS_LIST_MYSERVICES_TAG4, false);
        FEEDS_LIST_MYSERVICES5= settings.getBoolean(FEDDS_LIST_MYSERVICES_TAG5, false);*/
        /*
         * Thong - Get data directory
         */
        try
        {
        	DataDirectory = new File(Commons.getDataDirectory(ctx).getAbsolutePath() + "/" + DATA_DIRECTORY_NAME);
        	
        	if (!DataDirectory.exists())
        	{
        		if (!DataDirectory.mkdirs())
        		{
        			Commons.AlertLog(ctx, "Cannot create Data Directory " + DataDirectory.getAbsolutePath(), "OK").show();
        		}
        		else
        		{
        		}
        	}
        	else
        	{
        	}
        }
        catch (Exception e) 
        {
        	e.printStackTrace();
        	Toast.makeText(getApplicationContext(), "Error: " + e.toString(), Toast.LENGTH_LONG).show();
		}
        
        /*
         * Thong - Database file init
         */
        File folder = new File(DataDirectory + "/PhimpMe_Photo_Effect");
		folder.mkdirs();
		
		File folder_take_photo = new File(DataDirectory + "/take_photo");
		folder_take_photo.mkdirs();
		
		phimp_me_tmp = folder + "/tmp.jpg";
		phimp_me_img_uri_temporary = Uri.fromFile(new File(phimp_me_tmp));
        File database_file = getDatabasePath(DATABASE_NAME);
        if (!database_file.exists()) 
		{
        	AccountDBAdapter db = new AccountDBAdapter(ctx);
        	db.open();
        	db.close();
        	
        	TumblrDBAdapter db2 = new TumblrDBAdapter(ctx);
        	db2.open();
        	db2.close();
        	
        	/* Clear memory */
        	db = null;
        	db2 = null;
		}
        
        /*
         * Thong - Initial Tab control
         */
        popupTabs = (LinearLayout) findViewById(R.id.popupTabs);
        showTabs();
        try
        {
	        mTabHost = getTabHost();
		
	        intent = new Intent().setClass(ctx, newGallery.class);
	        tbview = LayoutInflater.from(mTabHost.getContext()).inflate(R.layout.ic_tab_gallery, null);
	        ts = mTabHost.newTabSpec("gallery");
	        ts.setIndicator(tbview);
	        ts.setContent(intent);
	        mTabHost.addTab(ts);
	        
	        intent = new Intent().setClass(ctx, Camera2.class);
	        tbview = LayoutInflater.from(mTabHost.getContext()).inflate(R.layout.ic_tab_camera, null);	        
	        ts = mTabHost.newTabSpec("camera");
	        ts.setIndicator(tbview);
	        ts.setContent(intent);
	        mTabHost.addTab(ts);
	        
	        intent = new Intent().setClass(ctx, Upload.class);
	        tbview = LayoutInflater.from(mTabHost.getContext()).inflate(R.layout.ic_tab_upload, null);
	        ts = mTabHost.newTabSpec("upload");
	        ts.setIndicator(tbview);
	        ts.setContent(intent);
	        mTabHost.addTab(ts);	       	        
	        	        
	        
	        intent = new Intent().setClass(ctx, Settings.class);
	        tbview = LayoutInflater.from(mTabHost.getContext()).inflate(R.layout.ic_tab_settings, null);
	        ts = mTabHost.newTabSpec("settings");
	        ts.setIndicator(tbview);
	        ts.setContent(intent);
	        mTabHost.addTab(ts);
	        
	        mTabHost.setCurrentTab(0);
	        
        }
        catch (Exception e) 
        {
			e.printStackTrace();
			Toast.makeText(getApplicationContext(), "Error: " + e.toString(), Toast.LENGTH_LONG).show();
		}
}
    
 // Show Tabs method
    public static void showTabs(){
        popupTabs.setVisibility(ViewGroup.VISIBLE);
    }
 
    // Hide Tabs method
    public static void hideTabs(){
        popupTabs.setVisibility(ViewGroup.GONE);
    }
    @Override
    protected void onPause()
    {
    	Log.d("thong", "Run PhimpMe.onPause()");
    	
    	super.onPause();
    	showTabs();
    	SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
    	SharedPreferences.Editor editor = settings.edit();
    	editor.putInt("gallery_max_display_photos", MAX_DISPLAY_PHOTOS);
    	editor.putInt("max_filesize_download", MAX_FILESIZE_DOWNLOAD);
    	editor.putBoolean(FEEDS_LIST_YAHOO_NEWS_TAG, FEEDS_LIST_YAHOO_NEWS);
    	editor.putBoolean(FEEDS_LIST_FLICKR_PUBLIC_TAG, FEEDS_LIST_FLICKR_PUBLIC);
    	editor.putBoolean(FEEDS_LIST_FLICKR_RECENT_TAG, FEEDS_LIST_FLICKR_RECENT);
    	editor.putBoolean(FEEDS_LIST_GOOGLE_PICASA_PUBLIC_TAG, FEEDS_LIST_GOOGLE_PICASA_PUBLIC);
    	editor.putBoolean(FEEDS_LIST_GOOGLE_NEWS_TAG, FEEDS_LIST_GOOGLE_NEWS);
    	editor.putBoolean(FEEDS_LIST_VK_TAG, FEEDS_LIST_VK);
    	editor.putBoolean(FEEDS_LIST_FACEBOOK_PRIVATE_TAG, FEEDS_LIST_FACEBOOK_PRIVATE);
    	editor.putBoolean(FEEDS_LIST_FLICKR_PRIVATE_TAG, FEEDS_LIST_FLICKR_PRIVATE);
    	editor.putBoolean(FEEDS_LIST_GOOGLE_PICASA_PRIVATE_TAG, FEEDS_LIST_GOOGLE_PICASA_PRIVATE);
    	editor.putBoolean(FEEDS_LIST_DEVIANTART_PRIVITE_TAG, FEEDS_LIST_DEVIANTART_PRIVITE);
    	editor.putBoolean(FEEDS_LIST_TUMBLR_PRIVATE_TAG, FEEDS_LIST_TUMBLR_PRIVATE);
    	editor.putBoolean(FEEDS_LIST_TWITTER_PRIVATE_TAG, FEEDS_LIST_TWITTER_PRIVATE);
    	editor.putBoolean(FEEDS_LIST_DEVIANTART_PUBLIC_TAG, FEEDS_LIST_DEVIANTART_PUBLIC);
    	editor.putBoolean(FEEDS_LIST_IMAGESHACK_PRIVITE_TAG, FEEDS_LIST_IMAGESHACK_PRIVITE);
    	editor.putBoolean(FEEDS_LIST_KAIXIN_PRIVATE_TAG, FEEDS_LIST_KAIXIN_PRIVATE);
    	editor.putBoolean(FEEDS_LIST_IMGUR_PERSONAL_TAG, FEEDS_LIST_IMGUR_PERSONAL);
    	editor.putBoolean(FEDDS_LIST_MYSERVICES_TAG, FEEDS_LIST_MYSERVICES);
    	editor.putBoolean(FEEDS_LIST_IMGUR_PUBLIC_TAG, FEEDS_LIST_IMGUR_PUBLIC);
    	editor.putBoolean(FEEDS_LIST_500PX_PRIVATE_TAG, FEEDS_LIST_500PX_PRIVATE);
    	editor.putBoolean(FEEDS_LIST_500PX_PUBLIC_TAG, FEEDS_LIST_500PX_PUBLIC);
    	editor.putBoolean(FEEDS_LIST_SOHU_PERSONAL_TAG, FEEDS_LIST_SOHU_PERSONAL);
    	editor.putBoolean(FEDDS_LIST_MYSERVICES_TAG1, FEEDS_LIST_MYSERVICES1);
    	editor.putBoolean(FEDDS_LIST_MYSERVICES_TAG2, FEEDS_LIST_MYSERVICES2);
    	editor.putBoolean(FEDDS_LIST_MYSERVICES_TAG3, FEEDS_LIST_MYSERVICES3);
    	editor.putBoolean(FEDDS_LIST_MYSERVICES_TAG4, FEEDS_LIST_MYSERVICES4);
    	editor.putBoolean(FEDDS_LIST_MYSERVICES_TAG5, FEEDS_LIST_MYSERVICES5);
        
    	// Commit the edits!
    	if (editor.commit())
    	{
    		Log.d("thong", "Commit success");
    	}
    	else
    	{
    		Log.d("thong", "Commit fail");
    	}
    }
    @Override
    protected void onResume()
    {
    	showTabs();
    	try
    	{
    		super.onResume();
    		
    	}
    	catch (Exception e) 
    	{
			e.printStackTrace();
		}
    }
   
    @Override
	public boolean onKeyDown(int keycode, KeyEvent event)
    {
    	if (keycode == KeyEvent.KEYCODE_BACK){
    		AlertDialog.Builder alertbox = new AlertDialog.Builder(ctx);
            alertbox.setMessage(getString(R.string.exit_message));
            alertbox.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                	finish();
                	System.exit(0);
                }
            });
            alertbox.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                	//Resume to current process
                }
            });
            alertbox.create().show();
    	}  	
        return super.onKeyDown(keycode, event);
    }

public void initialize() {	
	int id;
	final String[] columns = { MediaStore.Images.Thumbnails._ID};
	final String[] data = { MediaStore.Images.Media.DATA };
	final String orderBy = MediaStore.Images.Media._ID;
	Cursor pathcursor = managedQuery(
			MediaStore.Images.Media.EXTERNAL_CONTENT_URI, data,
			null, null, orderBy);	
	if(pathcursor != null){
		int path_column_index = pathcursor
				.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		int count = pathcursor.getCount();
		int c = 0;
		for (int i = 0; i< count; i++) {
			
				pathcursor.moveToPosition(i);
				String path = pathcursor.getString(path_column_index);
				boolean check = cache.check(path);
				if(check){
					@SuppressWarnings("unused")
					int index = Integer.valueOf(PhimpMe.cache.getCacheId(path));
					@SuppressWarnings("unused")
					Bitmap bmp = PhimpMe.cache.getCachePath(path);
					
				}
				else if(c<=20){
					Cursor cursor = managedQuery(
							MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns,
							MediaStore.Images.Media.DATA+ " = " + "\""+path+"\"", null, MediaStore.Images.Media._ID);									
					if (cursor != null && cursor.getCount() > 0){
						cursor.moveToPosition(0);
						id = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID));	
						Bitmap bmp = MediaStore.Images.Thumbnails.getThumbnail(
								getApplicationContext().getContentResolver(), id,
								MediaStore.Images.Thumbnails.MICRO_KIND, null);		
						PhimpMe.cache.saveCacheFile(path, bmp, id);
					}else id = -1;
					c++;
				}
			
		}		
	}
	//pathcursor.close();
}
public static void stopThread(){
	cachetask.onCancelled();
	Log.d("PhimpMe", "Stop Cache Task");
}
class CacheTask extends AsyncTask<String, Void, String> {
    @Override
    protected String doInBackground(String... urls) {
    	try{
    		Log.d("luong", "Run Cache Task");
    			initialize();
    	}catch(RuntimeException runex){
    		this.onCancelled();
    	}
    	
        return "";
    }

    @Override
    protected void onPostExecute(String result) {
    }
    @Override
    protected void onCancelled() {
    	// TODO Auto-generated method stub
    	super.onCancelled();
    	
    }
}

/*public void exportDevicesInfomation(){
	JSONObject js=new JSONObject();
	
		// the getLaunchIntentForPackage returns an intent that you can use
	 	String  ANDROID         =   android.os.Build.VERSION.RELEASE;       //The current development codename, or the string "REL" if this is a release build.
	    String  BOARD           =   android.os.Build.BOARD;                 //The name of the underlying board, like "goldfish".    
	    String  BOOTLOADER      =   android.os.Build.BOOTLOADER;            //  The system bootloader version number.
	    String  BRAND           =   android.os.Build.BRAND;                 //The brand (e.g., carrier) the software is customized for, if any.
	    String  CPU_ABI         =   android.os.Build.CPU_ABI;               //The name of the instruction set (CPU type + ABI convention) of native code.
	    String  CPU_ABI2        =   android.os.Build.CPU_ABI2;              //  The name of the second instruction set (CPU type + ABI convention) of native code.
	    String  DEVICE          =   android.os.Build.DEVICE;                //  The name of the industrial design.
	    String  DISPLAY         =   android.os.Build.DISPLAY;               //A build ID string meant for displaying to the user
	    String  FINGERPRINT     =   android.os.Build.FINGERPRINT;           //A string that uniquely identifies this build.
	    String  HARDWARE        =   android.os.Build.HARDWARE;              //The name of the hardware (from the kernel command line or /proc).
	    String  HOST            =   android.os.Build.HOST;  
	    String  ID              =   android.os.Build.ID;                    //Either a changelist number, or a label like "M4-rc20".
	    String  MANUFACTURER    =   android.os.Build.MANUFACTURER;          //The manufacturer of the product/hardware.
	    String  MODEL           =   android.os.Build.MODEL;                 //The end-user-visible name for the end product.
	    String  PRODUCT         =   android.os.Build.PRODUCT;               //The name of the overall product.
	    String  RADIO           =   android.os.Build.PRODUCT;               //The radio firmware version number.
	    String  TAGS            =   android.os.Build.TAGS;                  //Comma-separated tags describing the build, like "unsigned,debug".
	    String  TYPE            =   android.os.Build.TYPE;                  //The type of build, like "user" or "eng".
	    String  USER            =   android.os.Build.USER;                  //
	   	    
	    try {
			js.put("android version", ANDROID);
			js.put("board", BOARD);
		    js.put("bootloader", BOOTLOADER);
		    js.put("brand", BRAND);
		    js.put("cpu_abi", CPU_ABI);
		    js.put("cpu_abi2", CPU_ABI2);
		    js.put("device", DEVICE);
		    js.put("display", DISPLAY);
		    js.put("fingerprint", FINGERPRINT);
		    js.put("hardware", HARDWARE);
		    js.put("host", HOST);
		    js.put("id", ID);
		    js.put("manufacturer", MANUFACTURER);
		    js.put("model", MODEL);
		    js.put("product", PRODUCT);
		    js.put("radio", RADIO);
		    js.put("tags", TAGS);
		    js.put("type", TYPE);
		    js.put("user", USER);
		    Log.e("Danh", "json value : "+js.toString());
		    String url = "http://192.168.1.200:8080/pentaho/ViewAction?&solution=bi-developers&path=demo&action=convert.xaction&userid=joe&password=password";
		    HttpClient client = new DefaultHttpClient();
		    URI uri = new URI(url);
		    Log.e("url",url);
		    HttpPost post = new HttpPost(uri);
		    List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);    
		    nameValuePairs.add(new BasicNameValuePair("t", TAGS));
		    post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
		    ResponseHandler<String> res = new BasicResponseHandler();
		    String httpResponse = client.execute(post, res);
		    Log.e("User data",httpResponse);
		} catch (Exception e) {
			e.printStackTrace();
		}	
	    //export devices infomation
	    String filename = "device_information.txt";
	    File folder = Environment.getExternalStorageDirectory();
	    String contents = js.toString();
	    saveToFile(filename, folder, contents);
}

public void exportInstalledPakage(){
	JSONArray json_array = new JSONArray();
	final PackageManager pm = getPackageManager();
	String contents = null;
	// get a list of installed apps.
	List<ApplicationInfo> packages = pm
			.getInstalledApplications(PackageManager.GET_META_DATA);

	for (ApplicationInfo packageInfo : packages) {

		Log.i("Danh", "Installed package :" + packageInfo.packageName +" version :"+ packageInfo.targetSdkVersion);
		contents +="\n Name :" + packageInfo.packageName +", targetSdkVersion :"+ packageInfo.targetSdkVersion +". \n";
		try {
			JSONObject js1=new JSONObject();
			js1.put("installed_package", packageInfo.packageName);
			js1.put("version", packageInfo.targetSdkVersion);
			json_array.put(js1);
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
	}
	try {
		Log.i("Danh", json_array.getJSONObject(0).getString("installed_package"));
		Log.i("Danh", json_array.getJSONObject(0).getString("version"));
	} catch (JSONException e) {
		e.printStackTrace();
	}
	
	//export installed pakage
    String filename = "installed_pakage.txt";
    File folder = Environment.getExternalStorageDirectory();
    saveToFile(filename, folder, contents);
	
}

public void saveToFile(String fileName, File directory, String contents){
	Log.i("Danh", "Saving file.");

	if (android.os.Environment.getExternalStorageState().equals(
			android.os.Environment.MEDIA_MOUNTED)){
		try {

			if (directory.canWrite()){
				String path=Environment.getExternalStorageDirectory() + "/phimp.me/";
				File gpxfile = new File(path, fileName);
				FileWriter gpxwriter = new FileWriter(gpxfile);
				BufferedWriter out = new BufferedWriter(gpxwriter);
				out.write(contents);
				out.close();
				Log.i("Danh", "Saved to SD as '" + path + fileName + "'");					
			}

		} catch (Exception e) {
			
			Log.i("Danh", "Could not write file " + e.getMessage());
		}

	}else{			
		Log.e("Danh", "No SD card is mounted.");		
	}
}*/
}