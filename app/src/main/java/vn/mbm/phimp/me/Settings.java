package vn.mbm.phimp.me;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.math.BigDecimal;
import java.util.ArrayList;

import org.json.JSONObject;
import org.wordpress.android.NewAccount;

import vn.mbm.phimp.me.database.AccountItem;
import vn.mbm.phimp.me.database.DeviantArtItem;
import vn.mbm.phimp.me.database.DownloadedPersonalPhotoDBItem;
import vn.mbm.phimp.me.database.DownloadedPhotoDBItem;
import vn.mbm.phimp.me.database.DrupalItem;
import vn.mbm.phimp.me.database.FacebookItem;
import vn.mbm.phimp.me.database.FlickrItem;
import vn.mbm.phimp.me.database.ImageshackItem;
import vn.mbm.phimp.me.database.JoomlaItem;
import vn.mbm.phimp.me.database.KaixinDBItem;
import vn.mbm.phimp.me.database.PicasaItem;
import vn.mbm.phimp.me.database.QQItem;
import vn.mbm.phimp.me.database.S500pxItem;
import vn.mbm.phimp.me.database.SohuItem;
import vn.mbm.phimp.me.database.TumblrItem;
import vn.mbm.phimp.me.database.TwitterItem;
import vn.mbm.phimp.me.database.VkItem;
import vn.mbm.phimp.me.database.WordpressItem;
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
import vn.mbm.phimp.me.services.Wordpress;
import vn.mbm.phimp.me.utils.Commons;
import vn.mbm.phimp.me.utils.RSSUtil;
import vn.mbm.phimp.me.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
import android.widget.Button;
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

import com.joooid.android.model.User;
import com.joooid.android.xmlrpc.Constants;
import com.joooid.android.xmlrpc.JoooidRpc;
import com.paypal.android.MEP.CheckoutButton;
import com.paypal.android.MEP.PayPal;
import com.paypal.android.MEP.PayPalActivity;
import com.paypal.android.MEP.PayPalPayment;
import com.tani.app.ui.IconContextMenu;
public class Settings extends Fragment
{
	private final int CONTEXT_MENU_ID = 1;
	private final int DIALOG_FILE_SIZE_SETTINGS = 2;
	private final int DIALOG_ADD_ACCOUNT_DRUPAL = 3;
	private final int DIALOG_DISPLAY_PHOTOS_SETTINGS = 4;
	private final int DIALOG_ADD_ACCOUNT_IMAGESHACK = 5;
	private final int DIALOG_ADD_ACCOUNT_WORDPRESS = 6;
	private final int DIALOG_ADD_ACCOUNT_JOOMLA = 7;
	
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
	private final int SERVICES_WORDPRESS_ACTION =16;
	private final int SERVICES_WORDPRESSDOTCOM_ACTION =17;
	private final int SERVICES_JOOMLA_ACTION =18;
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
	Button donatePaypal;
	EditText donateAmount;
	
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

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.settings, container, false);
	}

	@SuppressWarnings("deprecation")
	@Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
		super.onCreate(savedInstanceState);
		getActivity().setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		Resources res = getResources();
		
		ctx = getContext();
		
		PayPal pp = PayPal.getInstance();
		
		//create donate button
		 final CheckoutButton donateButton = pp.getCheckoutButton(ctx, PayPal.BUTTON_278x43, CheckoutButton.TEXT_DONATE);
		
		//Add donate button to the screen
		//((LinearLayout)getView().findViewById(R.id.linearSettingsDonate)).addView(donateButton);

		//initial amount field
		//donateAmount = (EditText) getView().findViewById(R.id.donateAmount);
		
				
		lytAccounts = (LinearLayout) getView().findViewById(R.id.linearSettingsAccounts);
		

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
			btnDelete = (ImageButton)getView().findViewById(R.id.deletebtn);
			btnDelete.setOnClickListener(new OnClickListener() {
				
				@Override
					public void onClick(View v) {
					
							AlertDialog.Builder alertbox = new AlertDialog.Builder(ctx);
							alertbox.setMessage(getString(R.string.ask_delete_photo));
							alertbox.setTitle(R.string.carefully);
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
										
										Commons.AlertLog(ctx, getString(R.string.no_photo_delete), "OK").show();	
										Log.i("Danh","Don't have folder!");
									}else{
									//Delete in database
										pro_gress=ProgressDialog.show(ctx, "", getString(R.string.wait), true, false);						            	
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
									getActivity().sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://"
								            + Environment.getExternalStorageDirectory()))); 
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
		txtMaxPhotoSize = (TextView) getView().findViewById(R.id.txtMaxFilesizeDownload);
		txtMaxPhotoSize.setText(PhimpMe.MAX_FILESIZE_DOWNLOAD + "");
		btnSettingsMaxFilesize = (ImageButton) getView().findViewById(R.id.imgbtnSettingsMaxFilesize);
		btnSettingsMaxFilesize.setOnTouchListener(new OnTouchListener() 
		{			
			@Override
			public boolean onTouch(View v, MotionEvent event) 
			{
				getActivity().showDialog(DIALOG_FILE_SIZE_SETTINGS);
				return false;
			}
		});
		/*
		 * Danh - Add Active google admod
		 */
		//lytGoogleAdmod = (LinearLayout) getView().findViewById(R.id.linearSettingsGoogleAdmod);
		
		LinearLayout.LayoutParams lpMargin_g = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		lpMargin_g.setMargins(10, 0, 10, 0);
		
		LinearLayout.LayoutParams lpLayoutMargin_g = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		lpLayoutMargin_g.setMargins(0, 0, 0, 10);
		
		/*
		 * Active google admod
		 */
		/*
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
		tvGoogleAdmob.setText("Activate Advertising");
		tvGoogleAdmob.setGravity(Gravity.CENTER_VERTICAL);
		tvGoogleAdmob.setTypeface(null, 1);
		lGoogleAdmob.addView(tvGoogleAdmob);



		lytGoogleAdmod.addView(lGoogleAdmob);
		*/
		/*
		 * Danh - Add Local gallery
		 */
		lytLocalGallery = (LinearLayout) getView().findViewById(R.id.linearSettingsLocalGallery);
		
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
						if (s.equals("wordpressdotcom"))
						{
							WordpressItem.removeAccount(ctx, _id);
						}
						if (s.equals("wordpress"))
						{
							WordpressItem.removeAccount(ctx, _id);
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

	// TODO: this may not be safe; was protected
	@Override
	public void onResume()
	{
		super.onResume();
		PhimpMe.showTabs();

		if (PhimpMe.add_account_setting)
		{
			reloadAccountsList();
			PhimpMe.add_account_setting = false;
		}
		if (PhimpMe.IdList.size() == 5) {PhimpMe.IdList.clear();PhimpMe.IdList.add(0);}
		PhimpMe.IdList.add(5);
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
				else if (_service.equals("wordpressdotcom"))
				{
					holder.imgIcon.setImageResource(R.drawable.wordpressdotcom_icon);
				}
				else if (_service.equals("wordpress"))
				{
					holder.imgIcon.setImageResource(R.drawable.icon_wordpress);
				}
				else if (_service.equals("joomla"))
				{
					holder.imgIcon.setImageResource(R.drawable.joomla);
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
	

	/*
	 * Delete photo in database function
	 * */
	public void timerDelayRemoveDialog(long time, final Dialog d){
	    new Handler().postDelayed(new Runnable() {
	        public void run() {                	            
	            boolean del = deletePhotoInDatabase();
				if(del==true){
					newGallery.clearAllPhoto();	
					getActivity().sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://"
				            + Environment.getExternalStorageDirectory()))); 
					//remove deleted photo in upload list
					Upload.imagelist="";	
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
			Log.e("Setting-Delete","Number photo need delete : "+list_photo_delete.size());
			if(list_photo_delete.size()==0){
				count++;
			}else{
				for(int i=0; i<list_photo_delete.size(); i++){
					itm.removeAccount(ctx, list_photo_delete.get(i).getID());
					File f1 = new File(list_photo_delete.get(i).getFilePath());
					File f2 = new File(list_photo_delete.get(i).getThumbPath());
					Log.i("Setting-Delete", "FilePath :"+list_photo_delete.get(i).getFilePath());
					Log.i("Setting-Delete", "ThumbPath :"+list_photo_delete.get(i).getThumbPath());
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

//	@Override
//	public void onBackPressed(){
//		PhimpMe.IdList.remove(PhimpMe.IdList.size()-1);
////		PhimpMe.mTabHost.setCurrentTab(PhimpMe.IdList.get(PhimpMe.IdList.size()-1));
//		PhimpMe.showTabs();
//	}
	
	
}
