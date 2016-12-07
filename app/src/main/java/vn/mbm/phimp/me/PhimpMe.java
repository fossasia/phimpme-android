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
import vn.mbm.phimp.me.gallery.PhimpMeGallery;
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
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.gesture.GestureOverlayView;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;
import android.widget.Toast;

import android.support.design.widget.BottomNavigationView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.maps.GeoPoint;
import com.paypal.android.MEP.PayPal;
//
//@ReportsCrashes(formKey = "dFRsUzBJSWFKUFc3WmFjaXZab2V0dHc6MQ",
//        mode = ReportingInteractionMode.TOAST,
//        forceCloseDialogAfterToast = false,
//        resToastText = R.string.crash_report_text)
@SuppressWarnings("deprecation")
public class PhimpMe extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener //, android.view.GestureDetector.OnGestureListener
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

    public static boolean FEEDS_GOOGLE_ADMOB;

    public static boolean FEEDS_LIST_YAHOO_NEWS;
    public static final String FEEDS_LIST_YAHOO_NEWS_TAG = "feeds_list_yahoo_news";
    public static boolean FEEDS_LOCAL_GALLERY;
    public static final String FEEDS_LOCAL_GALLERY_TAG = "feeds_local_gallery";
    public static int check = 0;
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
    public static final String FEDDS_LIST_MYSERVICES_TAG = "feeds_list_myservices";
    public static final String FEDDS_LIST_MYSERVICES_TAG1 = "feeds_list_myservices";
    public static final String FEDDS_LIST_MYSERVICES_TAG2 = "feeds_list_myservices";
    public static final String FEDDS_LIST_MYSERVICES_TAG3 = "feeds_list_myservices";
    public static final String FEDDS_LIST_MYSERVICES_TAG4 = "feeds_list_myservices";
    public static final String FEDDS_LIST_MYSERVICES_TAG5 = "feeds_list_myservices";
    public static String MY_FEED_URL = "";

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
    public static LinearLayout popupTabs; // !?!
    public static int camera_use;
    //LOCAL
    public static ArrayList<String> filepath = new ArrayList<String>();
    public static ArrayList<Integer> IdList;
    public static int local_count = 1;
    ProgressDialog progConfig;
    //new Gallery
    static ArrayList<ArrayList<RSSPhotoItem>> phimpme_array_list = new ArrayList<ArrayList<RSSPhotoItem>>();
    static ArrayList<ArrayList<RSSPhotoItem_Personal>> phimpme_personal_array_list = new ArrayList<ArrayList<RSSPhotoItem_Personal>>();
    //Cache
    public static CacheStore cache;
    public static CacheTask cachetask;
    //Crash Report
    public static String CRITTERCISM_APP_ID = "4fffa20fbe790e4bc7000002";
    boolean serviceDisabled = false;
    public static boolean check_cache;
    public static boolean check_export = true;
    public static BottomNavigationView mBottomNav;
    public static boolean check_donwload = false;
    public static boolean check_donwload_local_gallery = false;
    public static AdView ad;
    public static int flashStatus = 2;

    //Gallery
    public static boolean gallery_delete = false;
    //private GestureDetector gestureScanner;
    //View.OnTouchListener gestureListener;
    public static int width, height;

    HomeScreenState currentScreen = HomeScreenState.GALLERY;

    @SuppressWarnings("unused")
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        ctx = this;
        Log.d("thong", "PhimpMe - onCreate()");
        // The following line triggers the initialization of ACRA
        //ACRA.init((Application) ctx.getApplicationContext());
        //Init PayPal library
        new Thread(new Runnable() {
            @Override
            public void run() {
                initLibrary(ctx);

            }
        }).start();

        camera_use = 0;
        if (IdList == null) IdList = new ArrayList<Integer>();

        setContentView(R.layout.main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        //gestureScanner = new GestureDetector(this);
        //Crash report
        // Crittercism.init(getApplicationContext(), CRITTERCISM_APP_ID, serviceDisabled);
        add_account_upload = false;
        add_account_setting = false;

        cache = CacheStore.getInstance();
        cachetask = new CacheTask();
        String[] str = null;
        cachetask.execute(str);

        /*
         * get window width, height
         */
        Display display = getWindowManager().getDefaultDisplay();
        width = display.getWidth() / 3;
        height = width;
        /*
         * Google admod
         */
        //ad = (AdView) findViewById(R.id.adView);
        SharedPreferences setting = getSharedPreferences(PREFS_NAME, 0);
        FEEDS_GOOGLE_ADMOB = setting.getBoolean("Google Admob", true);
        File file = getBaseContext().getFileStreamPath("google_admob.txt");
        if (file.exists()) {
            try {
                FileInputStream Rfile = openFileInput("google_admob.txt");

                InputStreamReader einputreader = new InputStreamReader(Rfile);
                BufferedReader ebuffreader = new BufferedReader(einputreader);
                Boolean tmp = Boolean.valueOf(ebuffreader.readLine());
                PhimpMe.FEEDS_GOOGLE_ADMOB = tmp;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Log.i("PhimpMe", "feed_google_admob : " + FEEDS_GOOGLE_ADMOB);
//        AdView adView = (AdView) this.findViewById(R.id.adView);
//
//        AdRequest request = new AdRequest.Builder()       // All emulators
//                .addTestDevice("AC98C820A50B4AD8A2106EDE96FB87D4")  // An example device ID
//                .build();
//        adView.loadAd(request);
//        if (FEEDS_GOOGLE_ADMOB == false) {
//            adView.setVisibility(ViewGroup.GONE);
//            //adView.destroy();
//        }
    	        
    	        /*
    	         * user config
    	         */

        File file0 = getBaseContext().getFileStreamPath("local_gallery.txt");
        if (file0.exists()) {
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
        if (file1.exists()) {
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
        if (file2.exists()) {
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
        if (file3.exists()) {
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
        if (file4.exists()) {
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
        if (file5.exists()) {
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
        if (file6.exists()) {
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
        if (file7.exists()) {
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
        if (file8.exists()) {
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
        if (file9.exists()) {
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
        if (file10.exists()) {
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
        if (file11.exists()) {
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
        if (file12.exists()) {
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
        if (file13.exists()) {
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
        if (file14.exists()) {
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
        
        /*
         * Export data
         */
        if (check_export = true) {
            //exportDevicesInfomation();
            //exportInstalledPakage();
            //Intent intent = new Intent(this,CollectUserData.class);
            //startService(intent);
            //check_export = false;
        }
        TabSpec ts; // !?
        View tbview;
        Intent intent;
     
        /*
         * Thong - Load preferences
         */
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        MAX_DISPLAY_PHOTOS = settings.getInt("gallery_max_display_photos", getResources().getInteger(R.integer.gallery_max_display_photos));
        MAX_FILESIZE_DOWNLOAD = settings.getInt("max_filesize_download", getResources().getInteger(R.integer.max_filesize_download));
        FEEDS_LOCAL_GALLERY = settings.getBoolean(FEEDS_LOCAL_GALLERY_TAG, true);
        /*FEEDS_LIST_FLICKR_PUBLIC = settings.getBoolean(FEEDS_LIST_FLICKR_PUBLIC_TAG, false);
        FEEDS_LIST_FLICKR_RECENT = settings.getBoolean(FEEDS_LIST_FLICKR_RECENT_TAG, false);       
        FEEDS_LIST_YAHOO_NEWS = settings.getBoolean(FEEDS_LIST_YAHOO_NEWS_TAG, false);       
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
        try {
            DataDirectory = new File(Commons.getDataDirectory(ctx).getAbsolutePath() + "/" + DATA_DIRECTORY_NAME);

            if (!DataDirectory.exists()) {
                if (!DataDirectory.mkdirs()) {
                    Commons.AlertLog(ctx, "Cannot create Data Directory " + DataDirectory.getAbsolutePath(), "OK").show();
                } else {
                }
            } else {
            }
        } catch (Exception e) {
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
        if (!database_file.exists()) {
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

        try {
            mBottomNav = (BottomNavigationView) findViewById(R.id.navigation_view);
        } catch (Exception e) {
        }
        mBottomNav.setOnNavigationItemSelectedListener(this);

        mBottomNav.getMenu().getItem(0).setChecked(true);

        // Initialising fragment container
        if (findViewById(R.id.fragment_container) != null) {
            newGallery frag = new newGallery();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, frag)
                    .commit();
        }

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.tab_gallery:
                if (currentScreen != HomeScreenState.GALLERY) {
                    newGallery frag = new newGallery();
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, frag)
                            .commit();
                    currentScreen = HomeScreenState.GALLERY;
                }
                break;
            case R.id.tab_camera:
                Intent intent = new Intent(this, Camera2.class);
                startActivity(intent);
                break;
            case R.id.tab_upload:
                if (currentScreen != HomeScreenState.UPLOAD) {
                    Upload frag = new Upload();
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, frag)
                            .commit();
                    currentScreen = HomeScreenState.UPLOAD;
                }
                break;
            case R.id.tab_settings:
                if (currentScreen != HomeScreenState.SETTINGS) {
                    Settings frag = new Settings();
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, frag)
                            .commit();
                    currentScreen = HomeScreenState.SETTINGS;
                }
                break;

        }

        return true;
    }

  /*  public Animation inFromRightAnimation() {
        Animation inFromRight = new TranslateAnimation(
        Animation.RELATIVE_TO_PARENT, +1.0f,
        Animation.RELATIVE_TO_PARENT, 0.0f,
        Animation.RELATIVE_TO_PARENT, 0.0f,
        Animation.RELATIVE_TO_PARENT, 0.0f);
        inFromRight.setDuration(500);
        inFromRight.setInterpolator(new AccelerateInterpolator());
        return inFromRight;
    }*/

    // navigation bar tabs
//    private void setTabs() {
//        addTab("", R.drawable.tab_icon_gallery_selector, newGallery.class);
//        addTab("", R.drawable.tab_icon_map_selector, OpenStreetMap.class);
//        addTab("", R.drawable.tab_icon_map_selector, GalleryMap.class);
//        addTab("", R.drawable.tab_icon_camera_selector, Blank.class);
//        addTab("", R.drawable.tab_icon_upload_selector, Upload.class);
//        addTab("", R.drawable.tab_icon_settings_selector, Settings.class);
//    }
//
//    private void addTab(String labelId, int drawableId, Class<?> c) {
//        TabHost.TabSpec spec = mTabHost.newTabSpec("tab" + labelId);
//        View tabIndicator = LayoutInflater.from(this).inflate(R.layout.tab_indicator, getTabWidget(), false);
//        TextView title = (TextView) tabIndicator.findViewById(R.id.title);
//        title.setText(labelId);
//        ImageView icon = (ImageView) tabIndicator.findViewById(R.id.icon);
//        icon.setImageResource(drawableId);
//        spec.setIndicator(tabIndicator);
//        Intent intent = new Intent(this, c);
//        spec.setContent(intent);
//        mTabHost.addTab(spec);
//
//
//    }


    enum HomeScreenState {
        // todo: add as needed
        GALLERY,
        UPLOAD,
        SETTINGS
    }

    public Animation outToLeftAnimation() {
        Animation outtoLeft = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, -1.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f);
        outtoLeft.setDuration(500);
        outtoLeft.setInterpolator(new AccelerateInterpolator());
        return outtoLeft;
    }

    // Show Tabs method
    public static void showTabs() {
        mBottomNav.setVisibility(ViewGroup.VISIBLE);
    }

    // Hide Tabs method
    public static void hideTabs() {
//        mBottomNav.setVisibility(ViewGroup.GONE);
    }

//    public static void ShowAd() {
//        ad.setVisibility(ViewGroup.VISIBLE);
//    }

    public static void hideAd() {
        ad.setVisibility(ViewGroup.GONE);
    }

    @Override
    protected void onPause() {
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
        if (editor.commit()) {
            Log.d("thong", "Commit success");
        } else {
            Log.d("thong", "Commit fail");
        }
    }

    @Override
    public void onResume() {
        //showTabs();
        Log.e("PhimpMe", "Resume");
        try {
            super.onResume();

        } catch (Exception e) {
            e.printStackTrace();
        }
        if (gallery_delete) {
            newGallery.update(PhimpMeGallery.num);
        }

    }

    @Override
    public boolean onKeyDown(int keycode, KeyEvent event) {
        if (keycode == KeyEvent.KEYCODE_BACK) {
            if (currentScreen != HomeScreenState.GALLERY) {
                newGallery frag = new newGallery();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, frag)
                        .commit();
                currentScreen = HomeScreenState.GALLERY;
            }
            else {
                AlertDialog.Builder alertbox = new AlertDialog.Builder(ctx);
                alertbox.setMessage(getString(R.string.exit_message));
                alertbox.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                        System.exit(0);
                    }
                });
                alertbox.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Resume to current process
                    }
                });
                alertbox.create().show();

            }

        }
        return super.onKeyDown(keycode, event);
    }

    /*public boolean onTouchEvent(MotionEvent me) {
        return gestureScanner.onTouchEvent(me);
    }*/
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        float dispX = e2.getX() - e1.getX();
        float dispY = e2.getY() - e1.getY();
        if (Math.abs(dispX) >= 200 && Math.abs(dispY) <= 100) {
            // swipe ok
            if (dispX > 0) {
                // L-R swipe
                //changeRtoL();
            } else {
                // R-L swipe            	
                //changeLtoR();

            }
        }
        return true;
    }

    // fixme: properly implement changeLtoR and changeRtoL
    // Currently the above function is the only use of this
    /*
    private void changeLtoR() {
        int curTab = mTabHost.getCurrentTab();
        int nextTab = ((curTab + 1) % 4); // !?! (why mod 4)
        mTabHost.setCurrentTab(nextTab);
    }

    private void changeRtoL() {
        int curTab = mTabHost.getCurrentTab();
        if (curTab != 0) {
            int lastTab = ((curTab - 1) % 4);
            mTabHost.setCurrentTab(lastTab);
        }

    }
    */
   /* public boolean dispatchTouchEvent(MotionEvent ev) {
        if (gestureScanner != null) {
            if (gestureScanner.onTouchEvent(ev))
                return true;
        }
        return super.dispatchTouchEvent(ev);
    }*/

    public void initialize() {
        int id;
        final String[] columns = {MediaStore.Images.Thumbnails._ID};
        final String[] data = {MediaStore.Images.Media.DATA};
        final String orderBy = MediaStore.Images.Media._ID;
        Cursor pathcursor = this.managedQuery(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, data,
                null, null, orderBy);
        if (pathcursor != null) {
            int path_column_index = pathcursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            int count = pathcursor.getCount();
            int c = 0;
            for (int i = 0; i < count; i++) {

                pathcursor.moveToPosition(i);
                String path = pathcursor.getString(path_column_index);
                boolean check = cache.check(path);
                if (check) {
                    @SuppressWarnings("unused")
                    int index = Integer.valueOf(PhimpMe.cache.getCacheId(path));
                    @SuppressWarnings("unused")
                    Bitmap bmp = PhimpMe.cache.getCachePath(path);

                } else if (c <= 20) {
                    Cursor cursor = this.managedQuery(
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns,
                            MediaStore.Images.Media.DATA + " = " + "\"" + path + "\"", null, MediaStore.Images.Media._ID);
                    if (cursor != null && cursor.getCount() > 0) {
                        cursor.moveToPosition(0);
                        id = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID));
                        Bitmap bmp = MediaStore.Images.Thumbnails.getThumbnail(
                                getApplicationContext().getContentResolver(), id,
                                MediaStore.Images.Thumbnails.MICRO_KIND, null);
                        PhimpMe.cache.saveCacheFile(path, bmp, id);
                    } else id = -1;

                    c++;

                }

            }
            newGallery.update_number++;
        }
        //pathcursor.close();
    }

    public static void stopThread() {
        cachetask.onCancelled();
        Log.d("PhimpMe", "Stop Cache Task");
    }

    public class CacheTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            try {
                Log.d("luong", "Run Cache Task");
                initialize();
            } catch (RuntimeException runex) {

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

    public void onTabChanged(String tabId) {
        // TODO Auto-generated method stub

    }

    public boolean onTouch(View v, MotionEvent event) {
        // TODO Auto-generated method stub
        return false;
    }

    public void onGesture(GestureOverlayView overlay, MotionEvent event) {
        // TODO Auto-generated method stub

    }

    public void onGestureCancelled(GestureOverlayView overlay, MotionEvent event) {
        // TODO Auto-generated method stub

    }

    public static void initLibrary(Context context) {
        try {
            PayPal pp = PayPal.getInstance();
            // If the library is already initialized, then we don't need to
            // initialize it again.
            if ((pp == null) || (!pp.isLibraryInitialized())) {
                pp = null;

                pp = PayPal.initWithAppID(context, "APP-80W284485P519543T", PayPal.ENV_SANDBOX);

                // -- These are required settings.
                //pp.setLanguage("de_DE");
                pp.setLanguage("en_US");

                pp.setFeesPayer(PayPal.FEEPAYER_EACHRECEIVER);
                // Set to true if the transaction will require shipping.
                pp.setShippingEnabled(true);

                pp.setDynamicAmountCalculationEnabled(false);

            }
        } catch (IllegalStateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}