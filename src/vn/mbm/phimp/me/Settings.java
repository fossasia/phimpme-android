package vn.mbm.phimp.me;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import org.json.JSONObject;

import vn.mbm.phimp.me.database.AccountItem;
import vn.mbm.phimp.me.database.DeviantArtItem;
import vn.mbm.phimp.me.database.DownloadedPersonalPhotoDBItem;
import vn.mbm.phimp.me.database.DownloadedPhotoDBItem;
import vn.mbm.phimp.me.database.DrupalItem;
import vn.mbm.phimp.me.database.FacebookItem;
import vn.mbm.phimp.me.database.FlickrItem;
import vn.mbm.phimp.me.database.ImageshackItem;
import vn.mbm.phimp.me.database.KaixinDBItem;
import vn.mbm.phimp.me.database.PicasaItem;
import vn.mbm.phimp.me.database.QQItem;
import vn.mbm.phimp.me.database.S500pxItem;
import vn.mbm.phimp.me.database.SohuItem;
import vn.mbm.phimp.me.database.TumblrItem;
import vn.mbm.phimp.me.database.TwitterItem;
import vn.mbm.phimp.me.database.VkItem;
import vn.mbm.phimp.me.feedservice.DeviantArt;
import vn.mbm.phimp.me.feedservice.Facebook;
import vn.mbm.phimp.me.feedservice.Flickr;
import vn.mbm.phimp.me.feedservice.Google;
import vn.mbm.phimp.me.feedservice.Imgur;
import vn.mbm.phimp.me.feedservice.Sohu;
import vn.mbm.phimp.me.feedservice.Tumblr;
import vn.mbm.phimp.me.feedservice.Vkontakte;
import vn.mbm.phimp.me.feedservice.Yahoo;
import vn.mbm.phimp.me.services.DeviantArtService;
import vn.mbm.phimp.me.services.DrupalServices;
import vn.mbm.phimp.me.services.FacebookServices;
import vn.mbm.phimp.me.services.FlickrServices;
import vn.mbm.phimp.me.services.ImageshackServices;
import vn.mbm.phimp.me.services.ImgurServices;
import vn.mbm.phimp.me.services.KaixinServices;
import vn.mbm.phimp.me.services.PicasaServices;
import vn.mbm.phimp.me.services.QQServices;
import vn.mbm.phimp.me.services.S500pxService;
import vn.mbm.phimp.me.services.SohuServices;
import vn.mbm.phimp.me.services.TumblrServices;
import vn.mbm.phimp.me.services.TwitterServices;
import vn.mbm.phimp.me.services.VKServices;
import vn.mbm.phimp.me.utils.Commons;
import vn.mbm.phimp.me.utils.RSSUtil;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.tani.app.ui.IconContextMenu;
public class Settings extends Activity 
{
	private final int CONTEXT_MENU_ID = 1;
	private final int DIALOG_FILE_SIZE_SETTINGS = 2;
	private final int DIALOG_ADD_ACCOUNT_DRUPAL = 3;
	private final int DIALOG_DISPLAY_PHOTOS_SETTINGS = 4;
	private final int DIALOG_ADD_ACCOUNT_IMAGESHACK = 5;
	
	private IconContextMenu iconContextMenu = null;
	
	private final int SERVICES_FACEBOOK_ACTION = 1;
	private final int SERVICES_FLICKR_ACTION = 2;
	private final int SERVICES_PICASA_ACTION = 3;
	private final int SERVICES_TUMBLR_ACTION = 4;
	private final int SERVICES_TWITTER_ACTION = 5;
	private final int SERVICES_DRUPAL_ACTION = 6;
	private final int SERVICES_DEVIANTART_ACTION = 7;
	private final int SERVICES_IMAGESHACK_ACTION = 8;
	//private final int SERVICES_QQ_ACTION = 9;
	private final int SERVICES_VK_ACTION = 10;
	private final int SERVICES_KAIXIN_ACTION = 12;
	private final int SERVICES_IMGUR_ACTION=13;
	private final int SERVICES_500PX_ACTION = 11;
	private final int SERVICES_SOHU_ACTION =15;
	public static EditText etMyFeedServicesTextbox;
	public static EditText etMyFeedServicesTextbox1;
	public static EditText etMyFeedServicesTextbox2;
	public static EditText etMyFeedServicesTextbox3;
	public static EditText etMyFeedServicesTextbox4;
	public static EditText etMyFeedServicesTextbox5;

	static Context ctx;
	int i=1;
	ImageButton btnAdd;
	ImageButton btnLangUS;
	ImageButton btnLangDE;
	ImageButton btnLangVI;
	ImageButton btnSettingsMaxFilesize;
	ImageButton btnSettingsMaxDisplayPhotos;
	
	TextView txtMaxPhotoSize;
	TextView txtMaxDisplay;
	TextView txtMB;
	LinearLayout liFileSize;
	TextView tvLangEN;
	TextView tvLangDE;
	TextView tvLangVI;
	
	LinearLayout lytGoogleAdmod;
	LinearLayout lytLocalGallery;
	LinearLayout lytMyFeedGallery;
	LinearLayout lytMyFeedServices;
	LinearLayout lytMyFeedMore;
	LinearLayout lytPublicFeedList;
	LinearLayout lytPrivateFeedList;
	LinearLayout lytAccounts;
	LinearLayout lyMore;
	
	RadioGroup rdgMaxPhotoSizeType;
	ImageButton btnMore;
	ImageButton btnDelete;
	//ImageButton btnHelp;
	File rss_folder;
	File rss_thums;
	File tmp_folder;
	int error_count = 0;
	ProgressDialog pro_gress;
	
	@Override
    public void onCreate(Bundle savedInstanceState)
    {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings);
		setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);		
		Resources res = getResources();
		if (PhimpMe.IdList.size() == 5) {PhimpMe.IdList.clear();PhimpMe.IdList.add(0);}
		PhimpMe.IdList.add(3);
		ctx = this;
		lytAccounts = (LinearLayout) findViewById(R.id.linearSettingsAccounts);
		
		btnAdd = (ImageButton) findViewById(R.id.btnSettingsAccountAdd);
		btnAdd.setOnTouchListener(new OnTouchListener() 
		{
			@SuppressWarnings("deprecation")
			@Override
			public boolean onTouch(View v, MotionEvent event) 
			{
				showDialog(CONTEXT_MENU_ID);
				return false;
			}
		});
		try{
				try{
					rss_folder = new File(PhimpMe.DataDirectory.getAbsolutePath() + "/" + RSSUtil.RSS_ITEM_FOLDER);
				}catch(Exception e){
				}
				try{
					rss_thums = new File(PhimpMe.DataDirectory.getAbsolutePath() + "/" + RSSUtil.RSS_THUMB_FOLDER);
				}catch(Exception e){
				}	
				try{
					tmp_folder = new File(PhimpMe.DataDirectory.getAbsolutePath() + "/" + RSSUtil.TMP_FOLDER);
				}catch(Exception e){
				}	
			btnDelete = (ImageButton)findViewById(R.id.deletebtn);
			btnDelete.setOnClickListener(new OnClickListener() {
				
				@Override
					public void onClick(View v) {
					
							AlertDialog.Builder alertbox = new AlertDialog.Builder(ctx);
							alertbox.setMessage(getString(R.string.ask_delete_photo));
							alertbox.setTitle("Carefully!");
							alertbox.setPositiveButton(getString(R.string.accept), new DialogInterface.OnClickListener()
					        {
					            @Override
					            public void onClick(DialogInterface dialog, int which)
					            {
					            	
					            	if (!rss_folder.exists())
									{
					            		error_count++;
									}
									if (!rss_thums.exists())
									{
										error_count++;
									}
									if (!tmp_folder.exists())
									{
										error_count++;
									}
									if(error_count==3){
										
										Commons.AlertLog(ctx, "Don't have photos to delete!", "OK").show();	
										Log.i("Danh","Don't have folder!");
									}else{
									//Delete in database
										pro_gress=ProgressDialog.show(ctx, "", "Please wait...", true, false);						            	
						            	timerDelayRemoveDialog(2000,pro_gress);
										/*boolean del = deletePhotoInDatabase();
										if(del==true){
											newGallery.clearAllPhoto();
											
											Commons.AlertLog(ctx, "Successfully", "OK").show();	
										}else{
											
											Commons.AlertLog(ctx, "Don't have photos to delete!", "OK").show();	
											Log.i("Danh","Don't have photo!");
										}*/
										
									}
					            }				            
					        });
							alertbox.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() 
							{
								@Override
								public void onClick(DialogInterface dialog, int which) 
								{
									
								}
							});
							
							alertbox.show();
					}
				
			});			
		}catch (Exception e){}
		txtMaxPhotoSize = (TextView) findViewById(R.id.txtMaxFilesizeDownload);
		txtMaxPhotoSize.setText(PhimpMe.MAX_FILESIZE_DOWNLOAD + "");
		btnSettingsMaxFilesize = (ImageButton) findViewById(R.id.imgbtnSettingsMaxFilesize);
		btnSettingsMaxFilesize.setOnTouchListener(new OnTouchListener() 
		{
			@Override
			public boolean onTouch(View v, MotionEvent event) 
			{
				showDialog(DIALOG_FILE_SIZE_SETTINGS);
				return false;
			}
		});
		/*
		 * Danh - Add Active google admod
		 */
		lytGoogleAdmod = (LinearLayout) findViewById(R.id.linearSettingsGoogleAdmod);
		
		LinearLayout.LayoutParams lpMargin_g = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		lpMargin_g.setMargins(10, 0, 10, 0);
		
		LinearLayout.LayoutParams lpLayoutMargin_g = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		lpLayoutMargin_g.setMargins(0, 0, 0, 10);
		
		/*
		 * Active google admod
		 */
		LinearLayout lGoogleAdmob = new LinearLayout(ctx);
		lGoogleAdmob.setLayoutParams(lpLayoutMargin_g);
		lGoogleAdmob.setGravity(Gravity.CENTER_VERTICAL);
		lGoogleAdmob.setOrientation(LinearLayout.HORIZONTAL);
		
		CheckBox chkGoogleAdmob = new CheckBox(ctx);
		chkGoogleAdmob.setChecked(PhimpMe.FEEDS_GOOGLE_ADMOB);
		chkGoogleAdmob.setOnCheckedChangeListener(new OnCheckedChangeListener() 
		{
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) 
			{
				PhimpMe.FEEDS_GOOGLE_ADMOB = isChecked;
				FileOutputStream fOut;
				try {
					fOut = openFileOutput("google_admob.txt",MODE_WORLD_READABLE);
					OutputStreamWriter osw = new OutputStreamWriter(fOut); 

					osw.write(""+PhimpMe.FEEDS_GOOGLE_ADMOB);

					osw.flush();
					osw.close();
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if(isChecked==false){
					Toast.makeText(ctx, "No Ads would be shown after you restart the application ", Toast.LENGTH_LONG).show();
				}
				
			}
		});
		lGoogleAdmob.addView(chkGoogleAdmob);
		
		
				
		TextView tvGoogleAdmob = new TextView(ctx);
		tvGoogleAdmob.setText("Activate Ads");
		tvGoogleAdmob.setGravity(Gravity.CENTER_VERTICAL);
		tvGoogleAdmob.setTypeface(null, 1);
		lGoogleAdmob.addView(tvGoogleAdmob);
		
		
				
		lytGoogleAdmod.addView(lGoogleAdmob);
		
		/*
		 * Danh - Add Local gallery
		 */
		lytLocalGallery = (LinearLayout) findViewById(R.id.linearSettingsLocalGallery);
		
		LinearLayout.LayoutParams lpMargin_ = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		lpMargin_.setMargins(10, 0, 10, 0);
		
		LinearLayout.LayoutParams lpLayoutMargin_ = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		lpLayoutMargin_.setMargins(0, 0, 0, 10);
		
		/*
		 * Local Gallery
		 */
		LinearLayout lLocalGallery = new LinearLayout(ctx);
		lLocalGallery.setLayoutParams(lpLayoutMargin_);
		lLocalGallery.setGravity(Gravity.CENTER_VERTICAL);
		lLocalGallery.setOrientation(LinearLayout.HORIZONTAL);
			
		CheckBox chkLocalGallery = new CheckBox(ctx);
		chkLocalGallery.setChecked(PhimpMe.FEEDS_LOCAL_GALLERY);		
		chkLocalGallery.setOnCheckedChangeListener(new OnCheckedChangeListener() 
		{
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) 
			{
				PhimpMe.FEEDS_LOCAL_GALLERY = isChecked;
				if(isChecked==true){
					PhimpMe.check_donwload_local_gallery=true;
				}
			}
		});
		lLocalGallery.addView(chkLocalGallery);
		
		ImageView imgLocalGallery = new ImageView(ctx);
		imgLocalGallery.setImageResource(R.drawable.icon_folder);
		imgLocalGallery.setLayoutParams(lpMargin_);
		lLocalGallery.addView(imgLocalGallery);
				
		TextView tvLocalGallery = new TextView(ctx);
		tvLocalGallery.setText("My Gallery");
		tvLocalGallery.setGravity(Gravity.CENTER_VERTICAL);
		tvLocalGallery.setTypeface(null, 1);
		lLocalGallery.addView(tvLocalGallery);
				
		lytLocalGallery.addView(lLocalGallery);
		
		/*
		 * My feeds services
		 */
		lytMyFeedGallery = (LinearLayout) findViewById(R.id.linearSettingsMyFeedGallery);
		lytMyFeedServices = (LinearLayout) findViewById(R.id.linearFeedService);
		lytMyFeedMore = (LinearLayout) findViewById(R.id.linearMore);
		
		LinearLayout.LayoutParams lpMargin_1 = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		lpMargin_1.setMargins(10, 0, 10, 0);
		
		LinearLayout.LayoutParams lpLayoutMargin_1 = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		lpLayoutMargin_1.setMargins(0, 0, 0, 10);
		LinearLayout.LayoutParams lpLayoutMargin_2 = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		lpLayoutMargin_2.setMargins(0, 0, 0, 0);
		/*
		 * layout
		 */		
		LinearLayout lMyFeedServicesChild=new LinearLayout(ctx);
		lMyFeedServicesChild.setPadding(0, 0, 0, 0);
		lMyFeedServicesChild.setLayoutParams(lpLayoutMargin_2);
		lMyFeedServicesChild.setGravity(Gravity.CENTER_VERTICAL);
		lMyFeedServicesChild.setOrientation(LinearLayout.HORIZONTAL);
		
		CheckBox chkMyFeedServices = new CheckBox(ctx);
		chkMyFeedServices.setChecked(PhimpMe.FEEDS_LIST_MYSERVICES);
		chkMyFeedServices.setOnCheckedChangeListener(new OnCheckedChangeListener() 
		{
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) 
			{
				PhimpMe.FEEDS_LIST_MYSERVICES = isChecked;				
			}
			
		});
		lMyFeedServicesChild.addView(chkMyFeedServices);
		
		ImageView imgMyFeedServices = new ImageView(ctx);
		imgMyFeedServices.setImageResource(R.drawable.icon_folder);
		imgMyFeedServices.setLayoutParams(lpMargin_1);
		lMyFeedServicesChild.addView(imgMyFeedServices);
				
		etMyFeedServicesTextbox = new EditText(ctx);
		etMyFeedServicesTextbox.setHint("Please enter Rss Url");
		etMyFeedServicesTextbox.setGravity(Gravity.CENTER_VERTICAL);	
		etMyFeedServicesTextbox.setTypeface(null, 1);
		etMyFeedServicesTextbox.setLayoutParams(lpLayoutMargin_2);
		lMyFeedServicesChild.addView(etMyFeedServicesTextbox);
		
		lytMyFeedServices.addView(lMyFeedServicesChild);
		
		
		/*
		 * My feeds services More button
		 */
		lyMore=new LinearLayout(ctx);
		lyMore.setLayoutParams(lpLayoutMargin_1);
		lyMore.setGravity(Gravity.RIGHT);
		lyMore.setOrientation(LinearLayout.HORIZONTAL);
		
		btnMore = new ImageButton(ctx);
		btnMore.setImageResource(R.drawable.more);
		lyMore.addView(btnMore);
		lytMyFeedMore.addView(lyMore);

		btnMore.setOnClickListener(new OnClickListener() {
		
			@Override
			public void onClick(View v) {

				LinearLayout.LayoutParams lpMargin_1 = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				lpMargin_1.setMargins(10, 0, 10, 0);
				LinearLayout.LayoutParams lpLayoutMargin_2 = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
				lpLayoutMargin_2.setMargins(0, 0, 0, 0);
				if(i<7){
					Log.d("Setting", "i="+i);
					/*
					 * create linear layout for each case of i , i<4
					 */
					if(i==1){
						LinearLayout lyMyFeedServicesMoreChild = new LinearLayout(ctx);
						CheckBox chkMore = new CheckBox(ctx);
						chkMore.setChecked(PhimpMe.FEEDS_LIST_MYSERVICES1);
						chkMore.setOnCheckedChangeListener(new OnCheckedChangeListener() 
						{
							@Override
							public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) 
							{
								PhimpMe.FEEDS_LIST_MYSERVICES1 = isChecked;				
							}
							
						});
						lyMyFeedServicesMoreChild.addView(chkMore);
						
						ImageView imgMyFeedServicesMore = new ImageView(ctx);
						imgMyFeedServicesMore.setImageResource(R.drawable.icon_folder);
						imgMyFeedServicesMore.setLayoutParams(lpMargin_1);
						lyMyFeedServicesMoreChild.addView(imgMyFeedServicesMore);
								
						etMyFeedServicesTextbox1 = new EditText(ctx);
						etMyFeedServicesTextbox1.setHint("Please enter Rss Url");
						etMyFeedServicesTextbox1.setGravity(Gravity.CENTER_VERTICAL);	
						etMyFeedServicesTextbox1.setTypeface(null, 1);
						etMyFeedServicesTextbox1.setLayoutParams(lpLayoutMargin_2);
						
						lyMyFeedServicesMoreChild.addView(etMyFeedServicesTextbox1);
						lytMyFeedServices.addView(lyMyFeedServicesMoreChild);
						Log.d("Setting","Linear layout 1 is created");
						i++;
					}else if(i==2){
						LinearLayout lyMyFeedServicesMoreChild = new LinearLayout(ctx);
						CheckBox chkMore = new CheckBox(ctx);
						chkMore.setChecked(PhimpMe.FEEDS_LIST_MYSERVICES2);
						chkMore.setOnCheckedChangeListener(new OnCheckedChangeListener() 
						{
							@Override
							public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) 
							{
								PhimpMe.FEEDS_LIST_MYSERVICES2 = isChecked;				
							}
							
						});
						lyMyFeedServicesMoreChild.addView(chkMore);
						
						ImageView imgMyFeedServicesMore = new ImageView(ctx);
						imgMyFeedServicesMore.setImageResource(R.drawable.icon_folder);
						imgMyFeedServicesMore.setLayoutParams(lpMargin_1);
						lyMyFeedServicesMoreChild.addView(imgMyFeedServicesMore);
								
						etMyFeedServicesTextbox2 = new EditText(ctx);
						etMyFeedServicesTextbox2.setHint("Please enter Rss Url");
						etMyFeedServicesTextbox2.setGravity(Gravity.CENTER_VERTICAL);	
						etMyFeedServicesTextbox2.setTypeface(null, 1);
						etMyFeedServicesTextbox2.setLayoutParams(lpLayoutMargin_2);
						
						lyMyFeedServicesMoreChild.addView(etMyFeedServicesTextbox2);
						lytMyFeedServices.addView(lyMyFeedServicesMoreChild);
						Log.d("Setting","Linear layout 2 is created");
						i++;
					}else if(i==3){
						AlertDialog.Builder build=new AlertDialog.Builder(ctx);
						build.setTitle("Warning !!!");
						build.setMessage("Activating more services can slow down the application and cause it to crash !");
						build.setPositiveButton(getString(R.string.accept), new DialogInterface.OnClickListener() 
						{
							@Override
							public void onClick(DialogInterface dialog, int which) 
							{
								LinearLayout.LayoutParams lpMargin_1 = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
								lpMargin_1.setMargins(10, 0, 10, 0);
								LinearLayout.LayoutParams lpLayoutMargin_2 = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
								lpLayoutMargin_2.setMargins(0, 0, 0, 0);
								LinearLayout lyMyFeedServicesMoreChild = new LinearLayout(ctx);
								CheckBox chkMore = new CheckBox(ctx);
								chkMore.setChecked(PhimpMe.FEEDS_LIST_MYSERVICES3);
								chkMore.setOnCheckedChangeListener(new OnCheckedChangeListener() 
								{
									@Override
									public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) 
									{
										PhimpMe.FEEDS_LIST_MYSERVICES3 = isChecked;				
									}
									
								});
								lyMyFeedServicesMoreChild.addView(chkMore);
								
								ImageView imgMyFeedServicesMore = new ImageView(ctx);
								imgMyFeedServicesMore.setImageResource(R.drawable.icon_folder);
								imgMyFeedServicesMore.setLayoutParams(lpMargin_1);
								lyMyFeedServicesMoreChild.addView(imgMyFeedServicesMore);
										
								etMyFeedServicesTextbox3 = new EditText(ctx);
								etMyFeedServicesTextbox3.setHint("Please enter Rss Url");
								etMyFeedServicesTextbox3.setGravity(Gravity.CENTER_VERTICAL);	
								etMyFeedServicesTextbox3.setTypeface(null, 1);
								etMyFeedServicesTextbox3.setLayoutParams(lpLayoutMargin_2);
								
								lyMyFeedServicesMoreChild.addView(etMyFeedServicesTextbox3);
								lytMyFeedServices.addView(lyMyFeedServicesMoreChild);
								Log.d("Setting","Linear layout 3 is created");
								i++;
							}
						});
						build.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() 
						{
							@Override
							public void onClick(DialogInterface dialog, int which) 
							{
								
							}
						});
						build.show();
					}	
					else if(i==4){
						AlertDialog.Builder build=new AlertDialog.Builder(ctx);
						build.setTitle("Warning !!!");
						build.setMessage("Activating more services can slow down the application and cause it to crash !");
						build.setPositiveButton(getString(R.string.accept), new DialogInterface.OnClickListener() 
						{
							@Override
							public void onClick(DialogInterface dialog, int which) 
							{
								LinearLayout.LayoutParams lpMargin_1 = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
								lpMargin_1.setMargins(10, 0, 10, 0);
								LinearLayout.LayoutParams lpLayoutMargin_2 = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
								lpLayoutMargin_2.setMargins(0, 0, 0, 0);
								LinearLayout lyMyFeedServicesMoreChild = new LinearLayout(ctx);
								CheckBox chkMore = new CheckBox(ctx);
								chkMore.setChecked(PhimpMe.FEEDS_LIST_MYSERVICES4);
								chkMore.setOnCheckedChangeListener(new OnCheckedChangeListener() 
								{
									@Override
									public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) 
									{
										PhimpMe.FEEDS_LIST_MYSERVICES4 = isChecked;				
									}
									
								});
								lyMyFeedServicesMoreChild.addView(chkMore);
								
								ImageView imgMyFeedServicesMore = new ImageView(ctx);
								imgMyFeedServicesMore.setImageResource(R.drawable.icon_folder);
								imgMyFeedServicesMore.setLayoutParams(lpMargin_1);
								lyMyFeedServicesMoreChild.addView(imgMyFeedServicesMore);
										
								etMyFeedServicesTextbox4 = new EditText(ctx);
								etMyFeedServicesTextbox4.setHint("Please enter Rss Url");
								etMyFeedServicesTextbox4.setGravity(Gravity.CENTER_VERTICAL);	
								etMyFeedServicesTextbox4.setTypeface(null, 1);
								etMyFeedServicesTextbox4.setLayoutParams(lpLayoutMargin_2);
								
								lyMyFeedServicesMoreChild.addView(etMyFeedServicesTextbox4);
								lytMyFeedServices.addView(lyMyFeedServicesMoreChild);
								Log.d("Setting","Linear layout 4 is created");
								i++;
							}
						});
						build.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() 
						{
							@Override
							public void onClick(DialogInterface dialog, int which) 
							{
								
							}
						});
						build.show();
					}	
					else if(i==5){
						AlertDialog.Builder build=new AlertDialog.Builder(ctx);
						build.setTitle("Warning !!!");
						build.setMessage("Activating more services can slow down the application and cause it to crash !");
						build.setPositiveButton(getString(R.string.accept), new DialogInterface.OnClickListener() 
						{
							@Override
							public void onClick(DialogInterface dialog, int which) 
							{
								LinearLayout.LayoutParams lpMargin_1 = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
								lpMargin_1.setMargins(10, 0, 10, 0);
								LinearLayout.LayoutParams lpLayoutMargin_2 = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
								lpLayoutMargin_2.setMargins(0, 0, 0, 0);
								LinearLayout lyMyFeedServicesMoreChild = new LinearLayout(ctx);
								CheckBox chkMore = new CheckBox(ctx);
								chkMore.setChecked(PhimpMe.FEEDS_LIST_MYSERVICES5);
								chkMore.setOnCheckedChangeListener(new OnCheckedChangeListener() 
								{
									@Override
									public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) 
									{
										PhimpMe.FEEDS_LIST_MYSERVICES5= isChecked;				
									}
									
								});
								lyMyFeedServicesMoreChild.addView(chkMore);
								
								ImageView imgMyFeedServicesMore = new ImageView(ctx);
								imgMyFeedServicesMore.setImageResource(R.drawable.icon_folder);
								imgMyFeedServicesMore.setLayoutParams(lpMargin_1);
								lyMyFeedServicesMoreChild.addView(imgMyFeedServicesMore);
										
								etMyFeedServicesTextbox5 = new EditText(ctx);
								etMyFeedServicesTextbox5.setHint("Please enter Rss Url");
								etMyFeedServicesTextbox5.setGravity(Gravity.CENTER_VERTICAL);	
								etMyFeedServicesTextbox5.setTypeface(null, 1);
								etMyFeedServicesTextbox5.setLayoutParams(lpLayoutMargin_2);
								
								lyMyFeedServicesMoreChild.addView(etMyFeedServicesTextbox5);
								lytMyFeedServices.addView(lyMyFeedServicesMoreChild);
								Log.d("Setting","Linear layout 5 is created");
								i++;
							}
						});
						build.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() 
						{
							@Override
							public void onClick(DialogInterface dialog, int which) 
							{
								
							}
						});
						build.show();
					}
					else if(i==6){
						AlertDialog.Builder build=new AlertDialog.Builder(ctx);
						build.setTitle("Warning !!!");
						build.setMessage("Sorry ! can not add more activating services !");
						build.setPositiveButton("OK", new DialogInterface.OnClickListener() 
						{
							@Override
							public void onClick(DialogInterface dialog, int which) 
							{
								lyMore.removeView(btnMore);
							}
						});						
						build.show();
					}
				}				
			}
			
		});
		
		
		/*
		 * Luong - Add public feeds list
		 */
		lytPublicFeedList = (LinearLayout) findViewById(R.id.linearSettingsPublicFeedsList);
		
		LinearLayout.LayoutParams lpMargin = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		lpMargin.setMargins(10, 0, 10, 0);
		
		LinearLayout.LayoutParams lpLayoutMargin = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		lpLayoutMargin.setMargins(0, 0, 0, 10);
		
		
		
		/*
		 * Flickr Public Photos
		 */
		LinearLayout lFlickrPublic = new LinearLayout(ctx);
		lFlickrPublic.setLayoutParams(lpLayoutMargin);
		lFlickrPublic.setGravity(Gravity.CENTER_VERTICAL);
		lFlickrPublic.setOrientation(LinearLayout.HORIZONTAL);
			
		CheckBox chkFlickrPublic = new CheckBox(ctx);
		chkFlickrPublic.setChecked(PhimpMe.FEEDS_LIST_FLICKR_PUBLIC);
		chkFlickrPublic.setOnCheckedChangeListener(new OnCheckedChangeListener() 
		{
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) 
			{
				PhimpMe.FEEDS_LIST_FLICKR_PUBLIC = isChecked;
				if(isChecked==true){
					PhimpMe.check_donwload=true;
				}
			}
		});
		lFlickrPublic.addView(chkFlickrPublic);
		
		ImageView imgFlickrPublic = new ImageView(ctx);
		imgFlickrPublic.setImageResource(Flickr.ICON);
		imgFlickrPublic.setLayoutParams(lpMargin);
		lFlickrPublic.addView(imgFlickrPublic);
				
		TextView tvFlickrPublic = new TextView(ctx);
		tvFlickrPublic.setText(Flickr.PUBLIC_TAG);
		tvFlickrPublic.setGravity(Gravity.CENTER_VERTICAL);
		tvFlickrPublic.setTypeface(null, 1);
		lFlickrPublic.addView(tvFlickrPublic);
				
		lytPublicFeedList.addView(lFlickrPublic);
		
		/*
		 * Flickr Recent Photos
		 */
		LinearLayout lFlickrRecent = new LinearLayout(ctx);
		lFlickrRecent.setLayoutParams(lpLayoutMargin);
		lFlickrRecent.setGravity(Gravity.CENTER_VERTICAL);
		lFlickrRecent.setOrientation(LinearLayout.HORIZONTAL);
					
		CheckBox chkFlickrRecent = new CheckBox(ctx);
		chkFlickrRecent.setChecked(PhimpMe.FEEDS_LIST_FLICKR_RECENT);
		chkFlickrRecent.setOnCheckedChangeListener(new OnCheckedChangeListener() 
		{
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) 
			{
				PhimpMe.FEEDS_LIST_FLICKR_RECENT = isChecked;
				if(isChecked==true){
					PhimpMe.check_donwload=true;
				}
			}
		});
		lFlickrRecent.addView(chkFlickrRecent);
		
		ImageView imgFlickrRecent = new ImageView(ctx);
		imgFlickrRecent.setImageResource(Flickr.ICON);
		imgFlickrRecent.setLayoutParams(lpMargin);
		lFlickrRecent.addView(imgFlickrRecent);
						
		TextView tvFlickrRecent = new TextView(ctx);
		tvFlickrRecent.setText(Flickr.RECENT_TAG);
		tvFlickrRecent.setGravity(Gravity.CENTER_VERTICAL);
		tvFlickrRecent.setTypeface(null, 1);
		lFlickrRecent.addView(tvFlickrRecent);
						
		lytPublicFeedList.addView(lFlickrRecent);

		/*
		 * Google News
		 */
		LinearLayout lGoogleNews = new LinearLayout(ctx);
		lGoogleNews.setLayoutParams(lpLayoutMargin);
		lGoogleNews.setGravity(Gravity.CENTER_VERTICAL);
		lGoogleNews.setOrientation(LinearLayout.HORIZONTAL);
							
		CheckBox chkGoogleNews = new CheckBox(ctx);
		chkGoogleNews.setChecked(PhimpMe.FEEDS_LIST_GOOGLE_NEWS);
		chkGoogleNews.setOnCheckedChangeListener(new OnCheckedChangeListener() 
		{
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) 
			{
				PhimpMe.FEEDS_LIST_GOOGLE_NEWS = isChecked;
				if(isChecked==true){
					PhimpMe.check_donwload=true;
				}
			}
		});
		lGoogleNews.addView(chkGoogleNews);
			
		ImageView imgGoogleNews = new ImageView(ctx);
		imgGoogleNews.setImageResource(Google.ICON);
		imgGoogleNews.setLayoutParams(lpMargin);
		lGoogleNews.addView(imgGoogleNews);
								
		TextView tvGoogleNews = new TextView(ctx);
		tvGoogleNews.setText(Google.NEWS_TAG);
		tvGoogleNews.setGravity(Gravity.CENTER_VERTICAL);
		tvGoogleNews.setTypeface(null, 1);
		lGoogleNews.addView(tvGoogleNews);
						
		lytPublicFeedList.addView(lGoogleNews);
		
		/*
		 * Google Picasa Public Photos
		 */
		LinearLayout lGooglePicasaPublic = new LinearLayout(ctx);
		lGooglePicasaPublic.setLayoutParams(lpLayoutMargin);
		lGooglePicasaPublic.setGravity(Gravity.CENTER_VERTICAL);
		lGooglePicasaPublic.setOrientation(LinearLayout.HORIZONTAL);
							
		CheckBox chkGooglePicasaPublic = new CheckBox(ctx);
		chkGooglePicasaPublic.setChecked(PhimpMe.FEEDS_LIST_GOOGLE_PICASA_PUBLIC);
		chkGooglePicasaPublic.setOnCheckedChangeListener(new OnCheckedChangeListener() 
		{
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) 
			{
				PhimpMe.FEEDS_LIST_GOOGLE_PICASA_PUBLIC = isChecked;
				if(isChecked==true){
					PhimpMe.check_donwload=true;
				}
			}
		});
		lGooglePicasaPublic.addView(chkGooglePicasaPublic);
			
		ImageView imgGooglePicasaPublic = new ImageView(ctx);
		imgGooglePicasaPublic.setImageResource(Google.PICASA_ICON);
		imgGooglePicasaPublic.setLayoutParams(lpMargin);
		lGooglePicasaPublic.addView(imgGooglePicasaPublic);
								
		TextView tvGooglePicasaPublic = new TextView(ctx);
		tvGooglePicasaPublic.setText(Google.PICASA_PUBLIC_TAG);
		tvGooglePicasaPublic.setGravity(Gravity.CENTER_VERTICAL);
		tvGooglePicasaPublic.setTypeface(null, 1);
		lGooglePicasaPublic.addView(tvGooglePicasaPublic);
						
		lytPublicFeedList.addView(lGooglePicasaPublic);	
		/*
		 * Yahoo! News
		 */
		LinearLayout lYahooNews = new LinearLayout(ctx);
		lYahooNews.setLayoutParams(lpLayoutMargin);
		lYahooNews.setGravity(Gravity.CENTER_VERTICAL);
		lYahooNews.setOrientation(LinearLayout.HORIZONTAL);
				
		CheckBox chkYahooNews = new CheckBox(ctx);
		chkYahooNews.setChecked(PhimpMe.FEEDS_LIST_YAHOO_NEWS);
		chkYahooNews.setOnCheckedChangeListener(new OnCheckedChangeListener() 
		{
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) 
			{
				PhimpMe.FEEDS_LIST_YAHOO_NEWS = isChecked;
				if(isChecked==true){
					PhimpMe.check_donwload=true;
				}
			}
		});
		lYahooNews.addView(chkYahooNews);
		
		ImageView imgYahooNews = new ImageView(ctx);
		imgYahooNews.setImageResource(Yahoo.ICON);
		imgYahooNews.setLayoutParams(lpMargin);
		lYahooNews.addView(imgYahooNews);
				
		TextView tvYahooNews = new TextView(ctx);
		tvYahooNews.setText(Yahoo.NEWS_TAG);
		tvYahooNews.setGravity(Gravity.CENTER_VERTICAL);
		tvYahooNews.setTypeface(null, 1);
		lYahooNews.addView(tvYahooNews);
				
		lytPublicFeedList.addView(lYahooNews);
		
		/*
		 * DeviantArt
		 */
		LinearLayout lDeviantArtPublic = new LinearLayout(ctx);
		lDeviantArtPublic.setLayoutParams(lpLayoutMargin);
		lDeviantArtPublic.setGravity(Gravity.CENTER_VERTICAL);
		lDeviantArtPublic.setOrientation(LinearLayout.HORIZONTAL);
			
		CheckBox chkDeviantArtPublic = new CheckBox(ctx);
		chkDeviantArtPublic.setChecked(PhimpMe.FEEDS_LIST_DEVIANTART_PUBLIC);
		chkDeviantArtPublic.setOnCheckedChangeListener(new OnCheckedChangeListener() 
		{
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) 
			{
				PhimpMe.FEEDS_LIST_DEVIANTART_PUBLIC = isChecked;
				if(isChecked==true){
					PhimpMe.check_donwload=true;
				}
			}
		});
		lDeviantArtPublic.addView(chkDeviantArtPublic);
		
		ImageView imgDeviantArtrPublic = new ImageView(ctx);
		imgDeviantArtrPublic.setImageResource(DeviantArt.ICON);
		imgDeviantArtrPublic.setLayoutParams(lpMargin);
		lDeviantArtPublic.addView(imgDeviantArtrPublic);
		
		TextView tvDeviantArtPublic = new TextView(ctx);
		tvDeviantArtPublic.setText(DeviantArt.PUBLIC_TAG);
		tvDeviantArtPublic.setGravity(Gravity.CENTER_VERTICAL);
		tvDeviantArtPublic.setTypeface(null, 1);
		lDeviantArtPublic.addView(tvDeviantArtPublic);
				
		lytPublicFeedList.addView(lDeviantArtPublic);
		
		/*
		 * Imgur Public Photos
		 */
		/*LinearLayout lImgurrPublic = new LinearLayout(ctx);
		lImgurrPublic.setLayoutParams(lpLayoutMargin);
		lImgurrPublic.setGravity(Gravity.CENTER_VERTICAL);
		lImgurrPublic.setOrientation(LinearLayout.HORIZONTAL);
			
		CheckBox chkImgurPublic = new CheckBox(ctx);
		chkImgurPublic.setChecked(PhimpMe.FEEDS_LIST_IMGUR_PUBLIC);
		chkImgurPublic.setOnCheckedChangeListener(new OnCheckedChangeListener() 
		{
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) 
			{
				PhimpMe.FEEDS_LIST_IMGUR_PUBLIC = isChecked;
			}
		});
		lImgurrPublic.addView(chkImgurPublic);
		
		ImageView imgImgurPublic = new ImageView(ctx);
		imgImgurPublic.setImageResource(Imgur.ICON);
		imgImgurPublic.setLayoutParams(lpMargin);
		lImgurrPublic.addView(imgImgurPublic);
				
		TextView tvImgurPublic = new TextView(ctx);
		tvImgurPublic.setText(Imgur.PUBLIC_TAG);
		tvImgurPublic.setGravity(Gravity.CENTER_VERTICAL);
		tvImgurPublic.setTypeface(null, 1);
		lImgurrPublic.addView(tvImgurPublic);
				
		lytPublicFeedList.addView(lImgurrPublic);*/
		/*
		 * 500PX Public Photos
		 */
		/*LinearLayout l500pxPublic = new LinearLayout(ctx);
		l500pxPublic.setLayoutParams(lpLayoutMargin);
		l500pxPublic.setGravity(Gravity.CENTER_VERTICAL);
		l500pxPublic.setOrientation(LinearLayout.HORIZONTAL);
			
		CheckBox chk500pxPublic = new CheckBox(ctx);
		chk500pxPublic.setChecked(PhimpMe.FEEDS_LIST_500PX_PUBLIC);
		chk500pxPublic.setOnCheckedChangeListener(new OnCheckedChangeListener() 
		{
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) 
			{
				PhimpMe.FEEDS_LIST_500PX_PUBLIC = isChecked;
			}
		});
		l500pxPublic.addView(chk500pxPublic);
		
		ImageView img500pxPublic = new ImageView(ctx);
		img500pxPublic.setImageResource(S500pxService.icon);
		img500pxPublic.setLayoutParams(lpMargin);
		l500pxPublic.addView(img500pxPublic);
				
		TextView tv500pxPublic = new TextView(ctx);
		tv500pxPublic.setText(s500px.PUBLIC_TAG);
		tv500pxPublic.setGravity(Gravity.CENTER_VERTICAL);
		tv500pxPublic.setTypeface(null, 1);
		l500pxPublic.addView(tv500pxPublic);
				
		lytPublicFeedList.addView(l500pxPublic);*/
		
		/*
		 * Luong - Add private feeds list
		 */
        lytPrivateFeedList = (LinearLayout) findViewById(R.id.linearSettingsPrivateFeedsList);
		
		LinearLayout.LayoutParams lpMarginPrivate = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		lpMarginPrivate.setMargins(10, 0, 10, 0);
		
		LinearLayout.LayoutParams lpLayoutMarginPrivate = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		lpLayoutMarginPrivate.setMargins(0, 0, 0, 10);
		
		/*
		 * Flickr Private Photos
		 */
		LinearLayout lFlickrPrivate = new LinearLayout(ctx);
		lFlickrPrivate.setLayoutParams(lpLayoutMarginPrivate);
		lFlickrPrivate.setGravity(Gravity.CENTER_VERTICAL);
		lFlickrPrivate.setOrientation(LinearLayout.HORIZONTAL);
			
		CheckBox chkFlickrPrivate = new CheckBox(ctx);
		chkFlickrPrivate.setChecked(PhimpMe.FEEDS_LIST_FLICKR_PRIVATE);
		chkFlickrPrivate.setOnCheckedChangeListener(new OnCheckedChangeListener() 
		{
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) 
			{
				PhimpMe.FEEDS_LIST_FLICKR_PRIVATE = isChecked;
				if(isChecked==true){
					PhimpMe.check_donwload=true;
				}
			}
		});
		lFlickrPrivate.addView(chkFlickrPrivate);
		
		ImageView imgFlickrPrivate = new ImageView(ctx);
		imgFlickrPrivate.setImageResource(Flickr.ICON);
		imgFlickrPrivate.setLayoutParams(lpMarginPrivate);
		lFlickrPrivate.addView(imgFlickrPrivate);
				
		TextView tvFlickrPrivate = new TextView(ctx);
		tvFlickrPrivate.setText(Flickr.PRIVATE_TAG);
		tvFlickrPrivate.setGravity(Gravity.CENTER_VERTICAL);
		tvFlickrPrivate.setTypeface(null, 1);
		lFlickrPrivate.addView(tvFlickrPrivate);
				
		lytPrivateFeedList.addView(lFlickrPrivate);
		
		/*
		 * Google Picasa Private Photos
		 */
		LinearLayout lGooglePicasaPrivate = new LinearLayout(ctx);
		lGooglePicasaPrivate.setLayoutParams(lpLayoutMarginPrivate);
		lGooglePicasaPrivate.setGravity(Gravity.CENTER_VERTICAL);
		lGooglePicasaPrivate.setOrientation(LinearLayout.HORIZONTAL);
							
		CheckBox chkGooglePicasaPrivate = new CheckBox(ctx);
		chkGooglePicasaPrivate.setChecked(PhimpMe.FEEDS_LIST_GOOGLE_PICASA_PRIVATE);
		chkGooglePicasaPrivate.setOnCheckedChangeListener(new OnCheckedChangeListener() 
		{
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) 
			{
				PhimpMe.FEEDS_LIST_GOOGLE_PICASA_PRIVATE = isChecked;
				if(isChecked==true){
					PhimpMe.check_donwload=true;
				}
			}
		});
		lGooglePicasaPrivate.addView(chkGooglePicasaPrivate);
			
		ImageView imgGooglePicasaPrivate = new ImageView(ctx);
		imgGooglePicasaPrivate.setImageResource(Google.PICASA_ICON);
		imgGooglePicasaPrivate.setLayoutParams(lpMarginPrivate);
		lGooglePicasaPrivate.addView(imgGooglePicasaPrivate);
								
		TextView tvGooglePicasaPrivate = new TextView(ctx);
		tvGooglePicasaPrivate.setText(Google.PICASA_PRIVATE_TAG);
		tvGooglePicasaPrivate.setGravity(Gravity.CENTER_VERTICAL);
		tvGooglePicasaPrivate.setTypeface(null, 1);
		lGooglePicasaPrivate.addView(tvGooglePicasaPrivate);
						
		lytPrivateFeedList.addView(lGooglePicasaPrivate);
		
		/*
		 * DeviantArt Privite
		 */
		LinearLayout lDeviantArtPrivite = new LinearLayout(ctx);
		lDeviantArtPrivite.setLayoutParams(lpLayoutMarginPrivate);
		lDeviantArtPrivite.setGravity(Gravity.CENTER_VERTICAL);
		lDeviantArtPrivite.setOrientation(LinearLayout.HORIZONTAL);
			
		CheckBox chkDeviantArtPrivite = new CheckBox(ctx);
		chkDeviantArtPrivite.setChecked(PhimpMe.FEEDS_LIST_DEVIANTART_PRIVITE);
		chkDeviantArtPrivite.setOnCheckedChangeListener(new OnCheckedChangeListener() 
		{
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) 
			{
				PhimpMe.FEEDS_LIST_DEVIANTART_PRIVITE = isChecked;
				if(isChecked==true){
					PhimpMe.check_donwload=true;
				}
			}
		});
		lDeviantArtPrivite.addView(chkDeviantArtPrivite);
		
		ImageView imgDeviantArtrPrivite = new ImageView(ctx);
		imgDeviantArtrPrivite.setImageResource(DeviantArt.ICON);
		imgDeviantArtrPrivite.setLayoutParams(lpMarginPrivate);
		lDeviantArtPrivite.addView(imgDeviantArtrPrivite);
		
		TextView tvDeviantArtPrivite = new TextView(ctx);
		tvDeviantArtPrivite.setText(DeviantArt.PRIVITE_TAG);
		tvDeviantArtPrivite.setGravity(Gravity.CENTER_VERTICAL);
		tvDeviantArtPrivite.setTypeface(null, 1);
		lDeviantArtPrivite.addView(tvDeviantArtPrivite);
				
		lytPrivateFeedList.addView(lDeviantArtPrivite);
		/*
		 * Imageshack 
		 * 
		 
		/*
		 * VK Services
		 */
		LinearLayout lVK = new LinearLayout(ctx);
		lVK.setLayoutParams(lpLayoutMarginPrivate);
		lVK.setGravity(Gravity.CENTER_VERTICAL);
		lVK.setOrientation(LinearLayout.HORIZONTAL);
			
		CheckBox chkVK= new CheckBox(ctx);
		chkVK.setChecked(PhimpMe.FEEDS_LIST_VK);
		chkVK.setOnCheckedChangeListener(new OnCheckedChangeListener() 
		{
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) 
			{
				PhimpMe.FEEDS_LIST_VK = isChecked;
				if(isChecked==true){
					PhimpMe.check_donwload=true;
				}	
			}
		});
		lVK.addView(chkVK);
		
		ImageView imgVK = new ImageView(ctx);
		imgVK.setImageResource(Vkontakte.ICON);
		imgVK.setLayoutParams(lpMarginPrivate);
		lVK.addView(imgVK);
		
		TextView tvVK = new TextView(ctx);
		tvVK.setText(Vkontakte.PRIVATE_TAG);
		tvVK.setGravity(Gravity.CENTER_VERTICAL);
		tvVK.setTypeface(null, 1);
		lVK.addView(tvVK);
				
		lytPrivateFeedList.addView(lVK);
		
		/*
		 * FACEBOOK Services
		 */
		LinearLayout lFacebook= new LinearLayout(ctx);
		lFacebook.setLayoutParams(lpLayoutMarginPrivate);
		lFacebook.setGravity(Gravity.CENTER_VERTICAL);
		lFacebook.setOrientation(LinearLayout.HORIZONTAL);
			
		CheckBox chkFacebook= new CheckBox(ctx);
		chkFacebook.setChecked(PhimpMe.FEEDS_LIST_FACEBOOK_PRIVATE);
		chkFacebook.setOnCheckedChangeListener(new OnCheckedChangeListener() 
		{
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) 
			{
				PhimpMe.FEEDS_LIST_FACEBOOK_PRIVATE = isChecked;
				if(isChecked==true){
					PhimpMe.check_donwload=true;
				}
			}
		});
		lFacebook.addView(chkFacebook);
		
		ImageView imgFacebook = new ImageView(ctx);
		imgFacebook.setImageResource(Facebook.ICON);
		imgFacebook.setLayoutParams(lpMarginPrivate);
		lFacebook.addView(imgFacebook);
		
		TextView tvFacebook = new TextView(ctx);
		tvFacebook.setText(Facebook.PRIVATE_TAG);
		tvFacebook.setGravity(Gravity.CENTER_VERTICAL);
		tvFacebook.setTypeface(null, 1);
		lFacebook.addView(tvFacebook);
				
		lytPrivateFeedList.addView(lFacebook);
		
		/*
		 * Tumblr Services
		 */
		LinearLayout lTumblr= new LinearLayout(ctx);
		lTumblr.setLayoutParams(lpLayoutMarginPrivate);
		lTumblr.setGravity(Gravity.CENTER_VERTICAL);
		lTumblr.setOrientation(LinearLayout.HORIZONTAL);
			
		CheckBox chkTumblr= new CheckBox(ctx);
		chkTumblr.setChecked(PhimpMe.FEEDS_LIST_TUMBLR_PRIVATE);
		chkTumblr.setOnCheckedChangeListener(new OnCheckedChangeListener() 
		{
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) 
			{
				PhimpMe.FEEDS_LIST_TUMBLR_PRIVATE = isChecked;
				if(isChecked==true){
					PhimpMe.check_donwload=true;
				}
			}
		});
		lTumblr.addView(chkTumblr);
		
		ImageView imgTumblr = new ImageView(ctx);
		imgTumblr.setImageResource(Tumblr.ICON);
		imgTumblr.setLayoutParams(lpMarginPrivate);
		lTumblr.addView(imgTumblr);
		
		TextView tvTumblr = new TextView(ctx);
		tvTumblr.setText(Tumblr.PRIVATE_TAG);
		tvTumblr.setGravity(Gravity.CENTER_VERTICAL);
		tvTumblr.setTypeface(null, 1);
		lTumblr.addView(tvTumblr);
				
		lytPrivateFeedList.addView(lTumblr);
		/*
		 * Kaixin Services
		 */
		/*LinearLayout lKaixin= new LinearLayout(ctx);
		lKaixin.setLayoutParams(lpLayoutMarginPrivate);
		lKaixin.setGravity(Gravity.CENTER_VERTICAL);
		lKaixin.setOrientation(LinearLayout.HORIZONTAL);
			
		CheckBox chkKaixin= new CheckBox(ctx);
		chkKaixin.setChecked(PhimpMe.FEEDS_LIST_KAIXIN_PRIVATE);
		chkKaixin.setOnCheckedChangeListener(new OnCheckedChangeListener() 
		{
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) 
			{
				PhimpMe.FEEDS_LIST_KAIXIN_PRIVATE = isChecked;
			}
		});
		lKaixin.addView(chkKaixin);
		
		ImageView imgKaixin = new ImageView(ctx);
		imgKaixin.setImageResource(Kaixin.ICON);
		imgKaixin.setLayoutParams(lpMarginPrivate);
		lKaixin.addView(imgKaixin);
		
		TextView tvKaixin = new TextView(ctx);
		tvKaixin.setText(Kaixin.PRIVATE_TAG);
		tvKaixin.setGravity(Gravity.CENTER_VERTICAL);
		tvKaixin.setTypeface(null, 1);
		lKaixin.addView(tvKaixin);
				
		lytPrivateFeedList.addView(lKaixin);*/
		/*
		 * Imgur Personal Photos
		 */
		LinearLayout lImgurPersonal = new LinearLayout(ctx);
		lImgurPersonal.setLayoutParams(lpLayoutMarginPrivate);
		lImgurPersonal.setGravity(Gravity.CENTER_VERTICAL);
		lImgurPersonal.setOrientation(LinearLayout.HORIZONTAL);
			
		CheckBox chkImgurPersonal = new CheckBox(ctx);
		chkImgurPersonal.setChecked(PhimpMe.FEEDS_LIST_IMGUR_PERSONAL);
		chkImgurPersonal.setOnCheckedChangeListener(new OnCheckedChangeListener() 
		{
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) 
			{
				PhimpMe.FEEDS_LIST_IMGUR_PERSONAL = isChecked;
				if(isChecked==true){
					PhimpMe.check_donwload=true;
				}
			}
		});
		lImgurPersonal.addView(chkImgurPersonal);
		
		ImageView imgImgurPersonal = new ImageView(ctx);
		imgImgurPersonal.setImageResource(Imgur.ICON);
		imgImgurPersonal.setLayoutParams(lpMarginPrivate);
		lImgurPersonal.addView(imgImgurPersonal);
				
		TextView tvImgurPersonal = new TextView(ctx);
		tvImgurPersonal.setText(Imgur.IMGUR_PERSONAL_TAG);
		tvImgurPersonal.setGravity(Gravity.CENTER_VERTICAL);
		tvImgurPersonal.setTypeface(null, 1);
		lImgurPersonal.addView(tvImgurPersonal);
				
		lytPrivateFeedList.addView(lImgurPersonal);
		/*
		 * 500PX Services
		 */
		/*LinearLayout l500px= new LinearLayout(ctx);
		l500px.setLayoutParams(lpLayoutMarginPrivate);
		l500px.setGravity(Gravity.CENTER_VERTICAL);
		l500px.setOrientation(LinearLayout.HORIZONTAL);
			
		CheckBox chk500px= new CheckBox(ctx);
		chk500px.setChecked(PhimpMe.FEEDS_LIST_500PX_PRIVATE);
		chk500px.setOnCheckedChangeListener(new OnCheckedChangeListener() 
		{
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) 
			{
				PhimpMe.FEEDS_LIST_500PX_PRIVATE = isChecked;
			}
		});
		l500px.addView(chk500px);
		
		ImageView img500px = new ImageView(ctx);
		img500px.setImageResource(s500px.ICON);
		img500px.setLayoutParams(lpMarginPrivate);
		l500px.addView(img500px);
		
		TextView tv500px = new TextView(ctx);
		tv500px.setText(s500px.PRIVATE_TAG);
		tv500px.setGravity(Gravity.CENTER_VERTICAL);
		tv500px.setTypeface(null, 1);
		l500px.addView(tv500px);
				
		lytPrivateFeedList.addView(l500px);*/
		/*
		 * Sohu Personal
		 */
		LinearLayout lSohu= new LinearLayout(ctx);
		lSohu.setLayoutParams(lpLayoutMarginPrivate);
		lSohu.setGravity(Gravity.CENTER_VERTICAL);
		lSohu.setOrientation(LinearLayout.HORIZONTAL);
			
		CheckBox chkSohu= new CheckBox(ctx);
		chkSohu.setChecked(PhimpMe.FEEDS_LIST_SOHU_PERSONAL);
		chkSohu.setOnCheckedChangeListener(new OnCheckedChangeListener() 
		{
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) 
			{
				PhimpMe.FEEDS_LIST_SOHU_PERSONAL = isChecked;
				if(isChecked==true){
					PhimpMe.check_donwload=true;
				}
			}
		});
		lSohu.addView(chkSohu);
		
		ImageView imgSohu = new ImageView(ctx);
		imgSohu.setImageResource(Sohu.ICON);
		imgSohu.setLayoutParams(lpMarginPrivate);
		lSohu.addView(imgSohu);
		
		TextView tvSohu = new TextView(ctx);
		tvSohu.setText(Sohu.SOHU_PERSONAL_TAG);
		tvSohu.setGravity(Gravity.CENTER_VERTICAL);
		tvSohu.setTypeface(null, 1);
		lSohu.addView(tvSohu);
				
		lytPrivateFeedList.addView(lSohu);
		/*
		 * Thong - Init services
		 */
		TumblrServices.init();
		FlickrServices.init();
		TwitterServices.init();
		S500pxService.init();
		/*
		 * Thong - Init accounts
		 */
		reloadAccountsList();
		
		iconContextMenu = new IconContextMenu(this, CONTEXT_MENU_ID);
		//iconContextMenu.addItem(res, DrupalServices.title, DrupalServices.icon, SERVICES_DRUPAL_ACTION);
        iconContextMenu.addItem(res, FacebookServices.title, FacebookServices.icon, SERVICES_FACEBOOK_ACTION);
        iconContextMenu.addItem(res, FlickrServices.title, FlickrServices.icon, SERVICES_FLICKR_ACTION);
        iconContextMenu.addItem(res, PicasaServices.title, PicasaServices.icon, SERVICES_PICASA_ACTION);
        iconContextMenu.addItem(res, TumblrServices.title, TumblrServices.icon, SERVICES_TUMBLR_ACTION);
        iconContextMenu.addItem(res, TwitterServices.title, TwitterServices.icon, SERVICES_TWITTER_ACTION);
        iconContextMenu.addItem(res, DeviantArtService.title, DeviantArtService.icon, SERVICES_DEVIANTART_ACTION);
        iconContextMenu.addItem(res, ImageshackServices.title, ImageshackServices.icon, SERVICES_IMAGESHACK_ACTION);
      //  iconContextMenu.addItem(res, QQServices.title, QQServices.icon, SERVICES_QQ_ACTION);
        iconContextMenu.addItem(res, VKServices.title, VKServices.icon, SERVICES_VK_ACTION);
        //iconContextMenu.addItem(res, KaixinServices.title, KaixinServices.icon, SERVICES_KAIXIN_ACTION);
        iconContextMenu.addItem(res, ImgurServices.title, ImgurServices.icon, SERVICES_IMGUR_ACTION);
        //iconContextMenu.addItem(res, S500pxService.title, S500pxService.icon, SERVICES_500PX_ACTION)
        iconContextMenu.addItem(res, SohuServices.title, SohuServices.icon, SERVICES_SOHU_ACTION);;
        iconContextMenu.setOnClickListener(new IconContextMenu.IconContextMenuOnClickListener() {
			@Override
			public void onClick(int menuId) 
			{
				switch(menuId) 
				{
				case SERVICES_FACEBOOK_ACTION:
					String fauthURL = FacebookServices.getAuthenticateLink();
					Intent fauthApp = new Intent(ctx, Webkit.class);
					fauthApp.putExtra("URL", fauthURL);
					ctx.startActivity(fauthApp);
					PhimpMe.add_account_upload = true;
					PhimpMe.add_account_setting = true;
					break;
				case SERVICES_FLICKR_ACTION:
					String flickr_oauth_request_token_url = FlickrServices.OAuthRequestToken();
					Intent flickr_authApp = new Intent(ctx, Webkit.class);
					flickr_authApp.putExtra("URL", flickr_oauth_request_token_url);
					ctx.startActivity(flickr_authApp);
					PhimpMe.add_account_upload = true;
					PhimpMe.add_account_setting = true;
					break;
				case SERVICES_PICASA_ACTION:
					String picasa_oauth_request_token_url = PicasaServices.OAuthGetAuthenticateLink();
					Intent picasa_authApp = new Intent(ctx, Webkit.class);
					picasa_authApp.putExtra("URL", picasa_oauth_request_token_url);
					ctx.startActivity(picasa_authApp);
					PhimpMe.add_account_upload = true;
					PhimpMe.add_account_setting = true;
					break;
				case SERVICES_TUMBLR_ACTION:
					String tumblr_oauth_request_token_url = TumblrServices.oauthRequestToken();
					Intent tauthApp = new Intent(ctx, Webkit.class);
					tauthApp.putExtra("URL", tumblr_oauth_request_token_url);
					ctx.startActivity(tauthApp);
					PhimpMe.add_account_upload = true;
					PhimpMe.add_account_setting = true;
					break;
				case SERVICES_TWITTER_ACTION:
					String twitter_oauth_request_token_url = TwitterServices.OAuthRequestToken();
					Intent twitter_authApp = new Intent(ctx, Webkit.class);
					twitter_authApp.putExtra("URL", twitter_oauth_request_token_url);
					ctx.startActivity(twitter_authApp);
					PhimpMe.add_account_upload = true;
					PhimpMe.add_account_setting = true;
					break;
				case SERVICES_DRUPAL_ACTION:
					showDialog(DIALOG_ADD_ACCOUNT_DRUPAL);
					break;
				case SERVICES_DEVIANTART_ACTION:
					String deviantart_oauth_url = DeviantArtService.getAuthenticateCode();
					Intent deviantart = new Intent(ctx,Webkit.class);
					deviantart.putExtra("URL", deviantart_oauth_url);
					ctx.startActivity(deviantart);
					PhimpMe.add_account_upload = true;
					PhimpMe.add_account_setting = true;
					break;
				case SERVICES_IMAGESHACK_ACTION:
					showDialog(DIALOG_ADD_ACCOUNT_IMAGESHACK);
					break;		
				case SERVICES_VK_ACTION:										
					Intent vk_authApp = new Intent(ctx, Webkit.class);
					vk_authApp.putExtra("URL", VKServices.getAuthorzingUrl());
					ctx.startActivity(vk_authApp);
					PhimpMe.add_account_upload = true;
					PhimpMe.add_account_setting = true;
					break;	
				case SERVICES_KAIXIN_ACTION:
					String kaixin_oauth_url = KaixinServices.getAuthenticateCode();
					Intent kaixin = new Intent(ctx,Webkit.class);
					kaixin.putExtra("URL", kaixin_oauth_url);
					ctx.startActivity(kaixin);
					PhimpMe.add_account_upload = true;
					PhimpMe.add_account_setting = true;
					break;	
				case SERVICES_IMGUR_ACTION:
					String Imgur_oauth_request_token_url = ImgurServices.OAuthRequestToken();
					Intent Imgur_authApp = new Intent(ctx, Webkit.class);
					Imgur_authApp.putExtra("URL", Imgur_oauth_request_token_url);
					ctx.startActivity(Imgur_authApp);
					PhimpMe.add_account_upload = true;
					PhimpMe.add_account_setting = true;
					break;
				case SERVICES_500PX_ACTION:	
					String s500px_oauth_request_token_url = S500pxService.OAuthRequestToken();
					Intent s500px_authApp = new Intent(ctx, Webkit.class);
					s500px_authApp.putExtra("URL", s500px_oauth_request_token_url);
					ctx.startActivity(s500px_authApp);
					PhimpMe.add_account_upload = true;
					PhimpMe.add_account_setting = true;
					break;
				case SERVICES_SOHU_ACTION:
					String sohu_oauth_request_token_url = SohuServices.OAuthRequestToken();
					Intent sohu_authApp = new Intent(ctx, Webkit.class);
					sohu_authApp.putExtra("URL", sohu_oauth_request_token_url);
					ctx.startActivity(sohu_authApp);
					PhimpMe.add_account_upload = true;
					PhimpMe.add_account_setting = true;
					break;
				}
			}
		});
    }
	private class btnDeleteListener implements OnClickListener
	{
		private String id;
		private String name;
		private String service;
		
		public btnDeleteListener(String id, String name, String service)
		{
			this.id = id;
			this.name = name;
			this.service = service;
		}
		
		@Override
		public void onClick(View v) 
		{
			try
			{
				final String s = service;
				final String _id = id;
				
				AlertDialog.Builder cofirmbox = new AlertDialog.Builder(ctx);
				cofirmbox.setMessage(getString(R.string.ask_delete_account) + "\n" + name + " (" + service + ")");
				cofirmbox.setPositiveButton(getString(R.string.accept), new DialogInterface.OnClickListener()
		        {
		            @Override
		            public void onClick(DialogInterface dialog, int which)
		            {
		            	AccountItem.removeAccount(ctx, _id);
						
		            	PhimpMe.checked_accounts.remove(_id);
		            	
						if (s.equals("facebook"))
						{
							FacebookItem.removeAccount(ctx, _id);
						}
						if (s.equals("flickr"))
						{
							FlickrItem.removeAccount(ctx, _id);
						}
						if (s.equals("picasa"))
						{
							PicasaItem.removeAccount(ctx, _id);
						}
						if (s.equals("tumblr"))
						{
							TumblrItem.removeAccount(ctx, _id);
						}
						if (s.equals("twitter"))
						{
							TwitterItem.removeAccount(ctx, _id);
						}
						if (s.equals("drupal"))
						{
							DrupalItem.removeAccount(ctx, _id);
						}
						if (s.equals("deviantart"))
						{
							DeviantArtItem.removeAccount(ctx, _id);
						}
						if (s.equals("imageshack"))
						{
							ImageshackItem.removeAccount(ctx, _id);
						}
						if (s.equals("qq"))
						{
							QQItem.removeAccount(ctx, _id);
						}
						if (s.equals("vkontakte"))
						{
							VkItem.removeAccount(ctx, _id);
						}
						if (s.equals("kaixin"))
						{
							KaixinDBItem.removeAccount(ctx, _id);
						}
						if (s.equals("imgur"))
						{
							VkItem.removeAccount(ctx, _id);
						}
						if (s.equals("500px"))
						{
							S500pxItem.removeAccount(ctx, _id);
						}
						if (s.equals("sohu"))
						{
							SohuItem.removeAccount(ctx, _id);
						}
						reloadAccountsList();						
						PhimpMe.add_account_upload = true;
		            }
		        });
				cofirmbox.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() 
				{
					@Override
					public void onClick(DialogInterface dialog, int which) 
					{
						
					}
				});
				
				cofirmbox.show();
			}
			catch (Exception e) 
			{
				
			}
		}
	}
	
	@Override
	protected void onResume()
	{
		super.onResume();
		PhimpMe.showTabs();
		if (PhimpMe.add_account_setting)
		{
			reloadAccountsList();
			PhimpMe.add_account_setting = false;
		}
		if (PhimpMe.IdList.size() == 5) {PhimpMe.IdList.clear();PhimpMe.IdList.add(0);}
		PhimpMe.IdList.add(3);
	}
	
	class ViewHolder
	{
		public ImageView imgIcon;
		public TextView txtName;
		/*public ImageButton btnConfigure;*/
		public ImageButton btnDelete;
	}
	
	private void reloadAccountsList()
	{
		ArrayList<AccountItem> accounts = AccountItem.getAllAccounts(ctx);
		
		if (accounts.size() > 0)
		{
			lytAccounts.removeAllViews();
			
			for (int i = 0; i < accounts.size(); i++)
			{
				AccountItem item = accounts.get(i);
				String _id = item.getID();
				String _name = item.getName();
				String _service = item.getService();
				
				LayoutInflater inflater = ((Activity) ctx).getLayoutInflater();
				View view = inflater.inflate(R.layout.settings_account_item, null, true);
				
				ViewHolder holder = new ViewHolder();
				
				holder.imgIcon = (ImageView) view.findViewById(R.id.imgServiceIcon);
				holder.txtName = (TextView) view.findViewById(R.id.txtAccountName);
				holder.btnDelete = (ImageButton) view.findViewById(R.id.imgbtnDelete);
				
				view.setTag(holder);
				
				if (_service.equals("tumblr"))
				{
					holder.imgIcon.setImageResource(TumblrServices.icon);
				}
				else if (_service.equals("facebook"))
				{
					holder.imgIcon.setImageResource(FacebookServices.icon);
				}
				else if (_service.equals("flickr"))
				{
					holder.imgIcon.setImageResource(FlickrServices.icon);
				}
				else if (_service.equals("picasa"))
				{
					holder.imgIcon.setImageResource(PicasaServices.icon);
				}
				else if (_service.equals("twitter"))
				{
					holder.imgIcon.setImageResource(TwitterServices.icon);
				}
				else if (_service.equals("drupal"))
				{
					holder.imgIcon.setImageResource(DrupalServices.icon);
				}
				
				else if (_service.equals("deviantart"))
				{
					holder.imgIcon.setImageResource(DeviantArtService.icon);
				}
				else if (_service.equals("imageshack"))
				{
					holder.imgIcon.setImageResource(ImageshackServices.icon);
				}
				else if (_service.equals("qq"))
				{
					holder.imgIcon.setImageResource(QQServices.icon);
				}
				else if (_service.equals("vkontakte"))
				{
					holder.imgIcon.setImageResource(VKServices.icon);
				}
				else if (_service.equals("kaixin"))
				{
					holder.imgIcon.setImageResource(KaixinServices.icon);
				}
				else if (_service.equals("imgur"))
				{
					holder.imgIcon.setImageResource(ImgurServices.icon);
				}
				else if (_service.equals("500px"))
				{
					holder.imgIcon.setImageResource(S500pxService.icon);
				}
				else if (_service.equals("sohu"))
				{
					holder.imgIcon.setImageResource(SohuServices.icon);
				}
				String acc_name = _name;
				
				holder.txtName.setText(acc_name);
				holder.btnDelete.setOnClickListener(new btnDeleteListener(_id, _name, _service));
				
				lytAccounts.addView(view);
			}
		}else
			{
			lytAccounts.removeAllViews();
			}
		accounts = null;
	}
	
	/**
	 * create context menu
	 */
	@Override
	protected Dialog onCreateDialog(int id) 
	{
		switch (id) 
		{
			case CONTEXT_MENU_ID:
				return iconContextMenu.createMenu(getString(R.string.services));
			case DIALOG_FILE_SIZE_SETTINGS:
				final EditText in = new EditText(Settings.this);
				in.setInputType(InputType.TYPE_CLASS_NUMBER);
				in.setMaxEms(5);
				return new AlertDialog.Builder(Settings.this)
					.setTitle(getString(R.string.max_file_size_download))
					.setMessage("")
					.setView(in)
					.setPositiveButton(getString(R.string.accept), new DialogInterface.OnClickListener() 
					{
						@Override
						public void onClick(DialogInterface dialog, int which) 
						{
							String tmp = in.getText().toString();
							try
							{
								int _int = Integer.parseInt(tmp);
								PhimpMe.MAX_FILESIZE_DOWNLOAD = _int;
								txtMaxPhotoSize.setText(PhimpMe.MAX_FILESIZE_DOWNLOAD + "");
							}
							catch (Exception e) 
							{
								Toast.makeText(Settings.this, "Error: " + e.toString(), Toast.LENGTH_LONG).show();
								
								e.printStackTrace();
							}
						}
					})
					.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() 
					{
						@Override
						public void onClick(DialogInterface dialog, int which) 
						{
							
						}
					})
					.create();
			case DIALOG_DISPLAY_PHOTOS_SETTINGS:
				final EditText input = new EditText(Settings.this);
				input.setInputType(InputType.TYPE_CLASS_NUMBER);
				input.setMaxEms(5);
				return new AlertDialog.Builder(Settings.this)
					.setTitle(getString(R.string.gallery_max_display_photos))
					.setMessage("")
					.setView(input)
					.setPositiveButton(getString(R.string.accept), new DialogInterface.OnClickListener() 
					{
						@Override
						public void onClick(DialogInterface dialog, int which) 
						{
							String tmp = input.getText().toString();
							try
							{
								int _int = Integer.parseInt(tmp);
								PhimpMe.MAX_DISPLAY_PHOTOS = _int;
								txtMaxDisplay.setText(PhimpMe.MAX_DISPLAY_PHOTOS + "");
							}
							catch (Exception e) 
							{
								Toast.makeText(Settings.this, "Error: " + e.toString(), Toast.LENGTH_LONG).show();
								
								e.printStackTrace();
							}
						}
					})
					.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() 
					{
						@Override
						public void onClick(DialogInterface dialog, int which) 
						{
							
						}
					})
					.create();
			case DIALOG_ADD_ACCOUNT_DRUPAL:
				LayoutInflater inflater = (LayoutInflater) ctx.getSystemService(LAYOUT_INFLATER_SERVICE);
				final View layout = inflater.inflate(R.layout.dialog_add_account_drupal, (ViewGroup) findViewById(R.id.lytDialogAddAccountDrupal));
				
				return new AlertDialog.Builder(Settings.this)
					.setTitle(DrupalServices.title)
					.setMessage("")
					.setView(layout)
					.setPositiveButton(getString(R.string.accept), new DialogInterface.OnClickListener() 
					{
						@Override
						public void onClick(DialogInterface dialog, int which) 
						{
							try
							{
								String username = ((EditText) layout.findViewById(R.id.txtDialogAddAccountDrupalUsername)).getText().toString();
								String password = ((EditText) layout.findViewById(R.id.txtDialogAddAccountDrupalPassword)).getText().toString();
								String siteurl = ((EditText) layout.findViewById(R.id.txtDialogAddAccountDrupalSiteurl)).getText().toString();
								
								String result = DrupalServices.login(username, password, siteurl);
								
								JSONObject json = new JSONObject(result);
								JSONObject user = json.getJSONObject("user");
								String user_id = user.getString("uid");
								String email = user.getString("mail");
								
								long account_id = AccountItem.insertAccount(ctx, null, username, "drupal", "1");
								
								if (account_id > 0)
								{
									if (DrupalItem.insertAccount(ctx, String.valueOf(account_id), user_id, username, password, siteurl, email))
									{
										Toast.makeText(ctx, "Insert account '" + username + "' (PhimpMe) SUCCESS!", Toast.LENGTH_LONG).show();
									}
									else
									{
										Toast.makeText(ctx, "Insert account '" + username + "' (PhimpMe) FAIL!", Toast.LENGTH_LONG).show();
									}
								}
								
								PhimpMe.add_account_upload = true;
								
								reloadAccountsList();
							}
							catch (Exception e) 
							{
								Toast.makeText(Settings.this, "Error: " + e.toString(), Toast.LENGTH_LONG).show();
								
								e.printStackTrace();
							}
						}
					})
					.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() 
					{
						@Override
						public void onClick(DialogInterface dialog, int which) 
						{
							
						}
					})
					.create();
			case DIALOG_ADD_ACCOUNT_IMAGESHACK:
				LayoutInflater inflater1 = (LayoutInflater) ctx.getSystemService(LAYOUT_INFLATER_SERVICE);
				final View layout1 = inflater1.inflate(R.layout.dialog_add_account_imageshack, (ViewGroup) findViewById(R.id.lytDialogAddAccountImageshack));
				
				return new AlertDialog.Builder(Settings.this)
					.setTitle(ImageshackServices.title)
					.setMessage("")
					.setView(layout1)
					.setPositiveButton(getString(R.string.accept), new DialogInterface.OnClickListener() 
					{
						@Override
						public void onClick(DialogInterface dialog, int which) 
						{
							try
							{
								String username = ((EditText) layout1.findViewById(R.id.txtDialogAddAccountImageshackUsername)).getText().toString();
								String password = ((EditText) layout1.findViewById(R.id.txtDialogAddAccountImageshackPassword)).getText().toString();								
								
								String result = ImageshackServices.login(username, password);
								
								
								JSONObject json = new JSONObject(result);	
								String status = json.getString("status");
								String registratorcode = "";
								if (status.equals("true")){
								registratorcode = json.getString("myimages");
								long account_id = AccountItem.insertAccount(ctx, null, username, "imageshack", "1");								
								if (account_id > 0)
								{								
									if (ImageshackItem.insertAccount(ctx, String.valueOf(account_id), registratorcode, username))
									{
										Toast.makeText(ctx, "Insert account '" + username + "' (Imageshack) SUCCESS!", Toast.LENGTH_LONG).show();
									}
									else
									{
										Toast.makeText(ctx, "Insert account '" + username + "' (Imageshack) FAIL!", Toast.LENGTH_LONG).show();
									}
								}
								}else
								{
								Toast.makeText(ctx, "Login Imageshack Fail !", Toast.LENGTH_LONG).show();	
								}
								
								PhimpMe.add_account_setting = true;
								
								reloadAccountsList();
							}
							catch (Exception e) 
							{
								Toast.makeText(Settings.this, "Error: " + e.toString(), Toast.LENGTH_LONG).show();
								
								e.printStackTrace();
							}
						}
					})
					.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() 
					{
						@Override
						public void onClick(DialogInterface dialog, int which) 
						{
							
						}
					})
					.create();
			default:
				return super.onCreateDialog(id);
		}
	}
	/*
	 * Delete photo in database function
	 * */
	public void timerDelayRemoveDialog(long time, final Dialog d){
	    new Handler().postDelayed(new Runnable() {
	        public void run() {                	            
	            boolean del = deletePhotoInDatabase();
				if(del==true){
					newGallery.clearAllPhoto();		
					d.dismiss();
					Commons.AlertLog(ctx, "Successfully", "OK").show();	
				}else{	
					d.dismiss();
					Commons.AlertLog(ctx, "Don't have photos to delete!", "OK").show();	
					Log.i("Danh","Don't have photo!");
				}
	        }
	    }, time); 
	}
	@SuppressWarnings("static-access")
	private boolean deletePhotoInDatabase(){
		Log.d("Danh","Start Delete !");
		//pro_gress = ProgressDialog.show(ctx, "Deleting...", "Delete PhimpMe's photos, please wait!");
		int count=0;
		try{
			
			PhimpMe.cache.clearCache();			
			DownloadedPhotoDBItem itm = new DownloadedPhotoDBItem();
			ArrayList<DownloadedPhotoDBItem> list_photo_delete = new ArrayList<DownloadedPhotoDBItem>();
			list_photo_delete = itm.getAll(ctx);
			Log.d("Danh","Number photo need delete : "+list_photo_delete.size());
			if(list_photo_delete.size()==0){
				count++;
			}else{
				for(int i=0; i<list_photo_delete.size(); i++){
					itm.removeAccount(ctx, list_photo_delete.get(i).getID());
					File f1 = new File(list_photo_delete.get(i).getFilePath());
					File f2 = new File(list_photo_delete.get(i).getThumbPath());
					Log.i("Danh", "FilePath :"+list_photo_delete.get(i).getFilePath());
					Log.i("Danh", "ThumbPath :"+list_photo_delete.get(i).getThumbPath());
					f1.delete();
					f2.delete();
				}
			}
			DownloadedPersonalPhotoDBItem personal_itm = new DownloadedPersonalPhotoDBItem();
			ArrayList<DownloadedPersonalPhotoDBItem> personal_list_photo_delete = new ArrayList<DownloadedPersonalPhotoDBItem>();
			personal_list_photo_delete = personal_itm.getAll(ctx);
			if(personal_list_photo_delete.size()==0){
				count++;
			}else{
				for(int i=0; i<personal_list_photo_delete.size(); i++){
					itm.removeAccount(ctx, personal_list_photo_delete.get(i).getID());
					File f1 = new File(personal_list_photo_delete.get(i).getFilePath());
					File f2 = new File(personal_list_photo_delete.get(i).getThumbPath());
					f1.delete();
					f2.delete();
				}
			}
			//Delete in /tmp
			try{
				String str[] = tmp_folder.list();
				if(str.length==0){
					count++;
				}else{
					for(int i=0; i<str.length; i++){
						File f3 = new File(tmp_folder + "/" + str[i]);
						f3.delete();
					}
				}
			}catch(Exception e){
			}
			
			if(count==3){
				return false;
			}
			
		}catch(Exception e){
			return false;
		}
		return true;
	}
	/*@Override
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
	                	Activity current = getParent();
	                	current.finish();
	                	//System.exit(0);
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
	    		PhimpMe.IdList.remove(PhimpMe.IdList.size()-1);
	    		PhimpMe.mTabHost.setCurrentTab(PhimpMe.IdList.get(PhimpMe.IdList.size()-1));
	    	}  	
	        //return super.onKeyDown(keycode, event);
	    	return true;
    }*/
	@Override
	public void onBackPressed(){
		PhimpMe.IdList.remove(PhimpMe.IdList.size()-1);
		PhimpMe.mTabHost.setCurrentTab(PhimpMe.IdList.get(PhimpMe.IdList.size()-1));
		PhimpMe.showTabs();
	}
	
	
}
