package vn.mbm.phimp.me;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import vn.mbm.phimp.me.database.DownloadedPersonalPhotoDBItem;
import vn.mbm.phimp.me.database.DownloadedPhotoDBItem;
import vn.mbm.phimp.me.feedservice.DeviantArt;
import vn.mbm.phimp.me.feedservice.Facebook;
import vn.mbm.phimp.me.feedservice.Flickr;
import vn.mbm.phimp.me.feedservice.Google;
import vn.mbm.phimp.me.feedservice.Imgur;
import vn.mbm.phimp.me.feedservice.Kaixin;
import vn.mbm.phimp.me.feedservice.MyFeedServices;
import vn.mbm.phimp.me.feedservice.Sohu;
import vn.mbm.phimp.me.feedservice.Tumblr;
import vn.mbm.phimp.me.feedservice.Vkontakte;
import vn.mbm.phimp.me.feedservice.Yahoo;
import vn.mbm.phimp.me.feedservice.s500px;
import vn.mbm.phimp.me.gridview.adapter.LocalPhotosAdapter;
import vn.mbm.phimp.me.utils.Commons;
import vn.mbm.phimp.me.utils.ImageUtil;
import vn.mbm.phimp.me.utils.RSSPhotoItem;
import vn.mbm.phimp.me.utils.RSSPhotoItem_Personal;
import vn.mbm.phimp.me.utils.RSSUtil;
import vn.mbm.phimp.me.utils.geoDegrees;
import vn.mbm.phimp.me.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

public class newGallery extends Fragment {
	private static Context ctx;
	static ArrayList<RSSPhotoItem> list_photos = new ArrayList<RSSPhotoItem>();
	static ArrayList<ArrayList<RSSPhotoItem>> array_list = new ArrayList<ArrayList<RSSPhotoItem>>();

	static ArrayList<ArrayList<RSSPhotoItem_Personal>> personal_array_list = new ArrayList<ArrayList<RSSPhotoItem_Personal>>();
	static ArrayList<RSSPhotoItem_Personal> list_photos_personal = new ArrayList<RSSPhotoItem_Personal>();

	public static ArrayList<RSSPhotoItem> list_thumb = new ArrayList<RSSPhotoItem>();
	public static ArrayList<RSSPhotoItem_Personal> list_thumb_personal = new ArrayList<RSSPhotoItem_Personal>();

	public static ArrayList<RSSPhotoItem> list_thumb_moment = new ArrayList<RSSPhotoItem>();

	public static final int NUMBER_PHOTO_NEED_DOWNLOAD = 6;
	public static int number_resume_download = 6;
	public static int count_photo = 1;
	public static int HIGHT_DISPLAY_PHOTOS = 0;
	public int hight_display;
	public static CacheStore cache;
	static ArrayList<Bitmap> bitmap_list = new ArrayList<Bitmap>();
	static ArrayList<Bitmap> bitmap_p_flickr = new ArrayList<Bitmap>();
	static ArrayList<Bitmap> bitmap_recent_flickr = new ArrayList<Bitmap>();
	static ArrayList<Bitmap> bitmap_public_picasa = new ArrayList<Bitmap>();
	static ArrayList<Bitmap> bitmap_google_news = new ArrayList<Bitmap>();
	static ArrayList<Bitmap> bitmap_p_yahoo = new ArrayList<Bitmap>();
	static ArrayList<Bitmap> bitmap_p_deviant = new ArrayList<Bitmap>();
	static ArrayList<Bitmap> bitmap_personal_facebook = new ArrayList<Bitmap>();
	static ArrayList<Bitmap> bitmap_personal_tumblr = new ArrayList<Bitmap>();
	static ArrayList<Bitmap> bitmap_personal_vkontakte = new ArrayList<Bitmap>();
	static ArrayList<Bitmap> bitmap_personal_flickr = new ArrayList<Bitmap>();
	static ArrayList<Bitmap> bitmap_personal_picasa = new ArrayList<Bitmap>();
	static ArrayList<Bitmap> bitmap_personal_deviantart = new ArrayList<Bitmap>();
	static ArrayList<Bitmap> bitmap_personal_imageshack = new ArrayList<Bitmap>();
	static ArrayList<Bitmap> bitmap_personal_imgur = new ArrayList<Bitmap>();
	static ArrayList<Bitmap> bitmap_public_imgur = new ArrayList<Bitmap>();
	static ArrayList<Bitmap> bitmap_my_feed_services = new ArrayList<Bitmap>();
	static ArrayList<Bitmap> bitmap_my_feed_services1 = new ArrayList<Bitmap>();
	static ArrayList<Bitmap> bitmap_my_feed_services2 = new ArrayList<Bitmap>();
	static ArrayList<Bitmap> bitmap_my_feed_services3 = new ArrayList<Bitmap>();
	static ArrayList<Bitmap> bitmap_my_feed_services4 = new ArrayList<Bitmap>();
	static ArrayList<Bitmap> bitmap_my_feed_services5 = new ArrayList<Bitmap>();
	static ArrayList<Bitmap> bitmap_personal_500px = new ArrayList<Bitmap>();
	static ArrayList<Bitmap> bitmap_public_500px = new ArrayList<Bitmap>();

	static ArrayList<Bitmap> bitmap_personal_kaixin = new ArrayList<Bitmap>();
	static ArrayList<Bitmap> bitmap_personal_sohu = new ArrayList<Bitmap>();
	static ArrayList<RSSPhotoItem> tmp_list = new ArrayList<RSSPhotoItem>();
	static ArrayList<RSSPhotoItem_Personal> tmp_list_personal = new ArrayList<RSSPhotoItem_Personal>();
	
	static ArrayList<String> listService = new ArrayList<String>();
	//Button btnPublic;
	Button delete;
	//public static TextView txtStatus;
	private static GridFlickrAdaper flickradapter;
	private static GridRecentFlickrAdaper recentflickradapter;
	private static GridPublicPicasaAdaper publicpicasaadapter;
	private static GridGoogleNewsAdaper googlenewsadapter;
	private static GridYahooAdapter yahooadapter;
	private static GridDeviantAdapter deviantadapter;
	private static GridFacebookAdapter facebookadapter;
	private static GridTumblrAdapter tumblradapter;
	private static GridVKontakteAdapter vkontakteadapter;
	private static GridPersonalFlickrAdapter personal_flickradapter;
	private static GridPersonalPicasaAdapter personal_picasaadapter;
	private static GridPersonalDeviantArtAdapter personal_deviantartadapter;
	private static GridPersonalImgurAdapter personal_imguradapter;
	private static GridImgurPublicAdaper public_imguradapter;
	private static GridMyFeedServicesAdaper my_feed_services_adapter;
	private static GridMyFeedServicesAdaper1 my_feed_services_adapter1;
	private static GridMyFeedServicesAdaper2 my_feed_services_adapter2;
	private static GridMyFeedServicesAdaper3 my_feed_services_adapter3;
	private static GridMyFeedServicesAdaper4 my_feed_services_adapter4;
	private static GridMyFeedServicesAdaper5 my_feed_services_adapter5;
	private static GridPersonalKaixinAdapter personal_kaixinadapter;
	private static GridPersonal500pxAdapter personal_500pxadapter;
	private static GridPublic500pxAdaper public500pxadapter;
	
	private static GridPersonalSohuAdapter personal_sohuadapter;	
	private static LocalPhotosAdapter local_adapter;
	static int current_process;
	static int current_page;
	private static Cursor cursor;
	private static int columnIndex;
	static int check_local = 0;
	public static ProgressDialog pro_gress;
	//ImageButton btnMap,btnBluetoothShare;
	public static int update_number = 0;
	static LinearLayout linear_main;
	ViewFlipper vf;
	File rss_folder;
	static int text_size = 20;
	static int color_line;

	// Local gallery
	static LinearLayout ln_local_gallery;
	static TextView txtlocal_gallery;
	static GridView gv_local_gallery;
	static ImageButton btn_local_more;
	static int local_rows_display = 0;
	// Flickr Public
	static LinearLayout ln_flickr;
	TextView txtPFlickr;
	GridView p_flickr;
	ArrayList<RSSPhotoItem> flickr_list_photos = new ArrayList<RSSPhotoItem>();
	ArrayList<RSSPhotoItem> filckr_list_not_save = new ArrayList<RSSPhotoItem>();
	int flickr_rows_display = 0;
	int flickr_count = 1;
	public static ImageButton btn_flickr_more;
	public static boolean flickr_public_download;
	// Flickr Recent
	static LinearLayout ln_recent_flickr;
	TextView txtRecentFlickr;
	GridView recent_flickr;
	ArrayList<RSSPhotoItem> recent_flickr_list_photos = new ArrayList<RSSPhotoItem>();
	ArrayList<RSSPhotoItem> recent_filckr_list_not_save = new ArrayList<RSSPhotoItem>();
	int recent_flickr_rows_display = 0;
	int recent_flickr_count = 1;
	ImageButton btn_recent_flickr_more;
	public static boolean flick_recent_download;
	// Google Public
	static LinearLayout ln_public_picasa;
	TextView txtPublicPicasa;
	GridView p_picasa;
	ArrayList<RSSPhotoItem> public_picasa_list_photos = new ArrayList<RSSPhotoItem>();
	ArrayList<RSSPhotoItem> pub_picasa_list_not_save = new ArrayList<RSSPhotoItem>();
	int public_picasa_rows_display = 0;
	int public_picasa_count = 1;
	ImageButton btn_public_picasa_more;
	public static boolean picasa_public_download=false;
	// Google New
	static LinearLayout ln_googlenews;
	TextView txtGooglenews;
	GridView p_googlenews;
	ArrayList<RSSPhotoItem> googlenews_list_photos = new ArrayList<RSSPhotoItem>();
	ArrayList<RSSPhotoItem> google_news_list_not_save = new ArrayList<RSSPhotoItem>();
	int googlenews_rows_display = 0;
	int googlenews_count = 1;
	ImageButton btn_googlenews_more;
	public static boolean google_new_download=false;
	// Yahoo news
	static LinearLayout ln_yahoo;
	TextView txtyahoo;
	GridView p_yahoo;
	ArrayList<RSSPhotoItem> yahoo_list_photos = new ArrayList<RSSPhotoItem>();
	ArrayList<RSSPhotoItem> yahoo_list_not_save = new ArrayList<RSSPhotoItem>();
	int yahoo_rows_display = 0;
	int yahoo_count = 1;
	ImageButton btn_yahoo_more;
	public static boolean yahoo_download=false;
	// DeviantArt
	static LinearLayout ln_deviant;
	TextView txtdeviant;
	GridView p_deviant;
	ArrayList<RSSPhotoItem> deviant_list_photos = new ArrayList<RSSPhotoItem>();
	ArrayList<RSSPhotoItem> pub_deviant_list_not_save = new ArrayList<RSSPhotoItem>();
	int deviant_rows_display = 0;
	int deviant_count = 1;
	ImageButton btn_public_deviant_more;
	public static boolean public_deviant_download=false;
	// Personal Facebook
	static LinearLayout ln_facebook;
	TextView txtfacebook;
	GridView gv_personal_facebook;
	ArrayList<RSSPhotoItem_Personal> personal_facebook_list_photos = new ArrayList<RSSPhotoItem_Personal>();
	ArrayList<RSSPhotoItem> per_facebook_list_not_save = new ArrayList<RSSPhotoItem>();
	int personal_facebook_rows_display = 0;
	int personal_facebook_count = 1;
	ImageButton btn_facebook_more;
	public static boolean facebook_download=false;
	// Personal Tumblr
	static LinearLayout ln_tumblr;
	TextView txttumblr;
	GridView gv_personal_tumblr;
	ArrayList<RSSPhotoItem_Personal> personal_tumblr_list_photos = new ArrayList<RSSPhotoItem_Personal>();
	ArrayList<RSSPhotoItem> per_tumblr_list_not_save = new ArrayList<RSSPhotoItem>();
	int personal_tumblr_rows_display = 0;
	int personal_tumblr_count = 1;
	ImageButton btn_tumblr_more;
	public static boolean tumblr_download=false;
	// Personal Vkontakte
	static LinearLayout ln_vkontakte;
	TextView txtvkontakte;
	GridView gv_personal_vkontakte;
	ArrayList<RSSPhotoItem_Personal> personal_vkontakte_list_photos = new ArrayList<RSSPhotoItem_Personal>();
	ArrayList<RSSPhotoItem> per_vk_list_not_save = new ArrayList<RSSPhotoItem>();
	int personal_vkontakte_rows_display = 0;
	int personal_vkontakte_count = 1;
	ImageButton btn_vkontakte_more;
	public static boolean vk_download=false;
	// Personal Flickr
	static LinearLayout ln_personal_flickr;
	TextView txtpersonal_flickr;
	GridView gv_personal_flickr;
	ArrayList<RSSPhotoItem_Personal> personal_flickr_list_photos = new ArrayList<RSSPhotoItem_Personal>();
	ArrayList<RSSPhotoItem> per_flickr_list_not_save = new ArrayList<RSSPhotoItem>();
	int personal_flickr_rows_display = 0;
	int personal_flickr_count = 1;
	ImageButton btn_personal_flickr_more;
	public static boolean personal_flickr_download=false;
	// Personal Picasa
	static LinearLayout ln_personal_picasa;
	TextView txtpersonal_picasa;
	GridView gv_personal_picasa;
	ArrayList<RSSPhotoItem_Personal> personal_picasa_list_photos = new ArrayList<RSSPhotoItem_Personal>();
	ArrayList<RSSPhotoItem> per_picasa_list_not_save = new ArrayList<RSSPhotoItem>();
	int personal_picasa_rows_display = 0;
	int personal_picasa_count = 1;
	ImageButton btn_personal_picasa_more;
	public static boolean personal_picasa_download=false;
	// Personal DeviantArt
	static LinearLayout ln_personal_deviantart;
	TextView txtpersonal_deviantart;
	GridView gv_personal_deviantart;
	ArrayList<RSSPhotoItem_Personal> personal_deviantart_list_photos = new ArrayList<RSSPhotoItem_Personal>();
	ArrayList<RSSPhotoItem> per_deviant_list_not_save = new ArrayList<RSSPhotoItem>();
	int personal_deviantart_rows_display = 0;
	int personal_deviantart_count = 1;
	ImageButton btn_personal_deviant_more;
	public static boolean personal_deviant_download=false;

	// Personal Imgur
	static LinearLayout ln_personal_imgur;
	TextView txtpersonal_imgur;
	GridView gv_personal_imgur;
	ArrayList<RSSPhotoItem_Personal> personal_imgur_list_photos = new ArrayList<RSSPhotoItem_Personal>();
	ArrayList<RSSPhotoItem> per_imgur_list_not_save = new ArrayList<RSSPhotoItem>();
	int personal_imgur_rows_display = 0;
	int personal_imgur_count = 1;
	ImageButton btn_personal_imgur_more;
	public static boolean personal_imgur_download=false;

	// My Rss Feed
	static LinearLayout ln_my_feed_services;
	TextView txtMyFeedServices;
	GridView gv_my_feed_services;
	ArrayList<RSSPhotoItem> my_feed_services_list_photos = new ArrayList<RSSPhotoItem>();
	ArrayList<RSSPhotoItem> my_feed_list_not_save = new ArrayList<RSSPhotoItem>();
	int my_feed_services_rows_display = 0;
	int my_feed_services_count = 1;
	ImageButton btn_my_feed_services_more;
	public static boolean myfeed_download=false;
	// My Rss Feed 1
	static LinearLayout ln_my_feed_services1;
	TextView txtMyFeedServices1;
	GridView gv_my_feed_services1;
	ArrayList<RSSPhotoItem> my_feed_services_list_photos1 = new ArrayList<RSSPhotoItem>();
	ArrayList<RSSPhotoItem> my_feed1_list_not_save = new ArrayList<RSSPhotoItem>();
	int my_feed_services_rows_display1 = 2;
	int my_feed_services_count1 = 1;
	ImageButton btn_my_feed_services_more1;
	public static boolean myfeed_download1=false;
	// My Rss Feed 2
	static LinearLayout ln_my_feed_services2;
	TextView txtMyFeedServices2;
	GridView gv_my_feed_services2;
	ArrayList<RSSPhotoItem> my_feed_services_list_photos2 = new ArrayList<RSSPhotoItem>();
	ArrayList<RSSPhotoItem> my_feed2_list_not_save = new ArrayList<RSSPhotoItem>();
	int my_feed_services_rows_display2 = 2;
	int my_feed_services_count2 = 1;
	ImageButton btn_my_feed_services_more2;
	public static boolean myfeed_download2=false;
	// My Rss Feed 3
	static LinearLayout ln_my_feed_services3;
	TextView txtMyFeedServices3;
	GridView gv_my_feed_services3;
	ArrayList<RSSPhotoItem> my_feed_services_list_photos3 = new ArrayList<RSSPhotoItem>();
	ArrayList<RSSPhotoItem> my_feed3_list_not_save = new ArrayList<RSSPhotoItem>();
	int my_feed_services_rows_display3 = 2;
	int my_feed_services_count3 = 1;
	ImageButton btn_my_feed_services_more3;
	public static boolean myfeed_download3=false;
	// My Rss Feed 4
	static LinearLayout ln_my_feed_services4;
	TextView txtMyFeedServices4;
	GridView gv_my_feed_services4;
	ArrayList<RSSPhotoItem> my_feed_services_list_photos4 = new ArrayList<RSSPhotoItem>();
	ArrayList<RSSPhotoItem> my_feed4_list_not_save = new ArrayList<RSSPhotoItem>();
	int my_feed_services_rows_display4 = 2;
	int my_feed_services_count4 = 1;
	ImageButton btn_my_feed_services_more4;
	public static boolean myfeed_download4=false;
	// My Rss Feed 5
	static LinearLayout ln_my_feed_services5;
	TextView txtMyFeedServices5;
	GridView gv_my_feed_services5;
	ArrayList<RSSPhotoItem> my_feed_services_list_photos5 = new ArrayList<RSSPhotoItem>();
	ArrayList<RSSPhotoItem> my_feed5_list_not_save = new ArrayList<RSSPhotoItem>();
	int my_feed_services_rows_display5 = 2;
	int my_feed_services_count5 = 1;
	ImageButton btn_my_feed_services_more5;
	public static boolean myfeed_download5=false;

	// Personal Kaixin
	static LinearLayout ln_personal_kaixin;
	TextView txtpersonal_kaixin;
	GridView gv_personal_kaixin;
	ArrayList<RSSPhotoItem_Personal> personal_kaixin_list_photos = new ArrayList<RSSPhotoItem_Personal>();
	ArrayList<RSSPhotoItem> per_kaixin_list_not_save = new ArrayList<RSSPhotoItem>();
	int personal_kaixin_rows_display = 0;
	int personal_kaixin_count = 1;
	ImageButton btn_personal_kaixin_more;
	boolean personal_kaixin=false;

	// Public Imgur
	static LinearLayout ln_public_imgur;
	TextView txtpublic_imgur;
	GridView gv_public_imgur;
	ArrayList<RSSPhotoItem> public_imgur_list_photos = new ArrayList<RSSPhotoItem>();
	ArrayList<RSSPhotoItem> pub_imgur_list_not_save = new ArrayList<RSSPhotoItem>();
	int public_imgur_rows_display = 0;
	int public_imgur_count = 1;
	ImageButton btn_public_imgur_more;

	// 500px Public
	static LinearLayout ln_public_500px;
	TextView txtPublic500px;
	GridView p_500px;
	ArrayList<RSSPhotoItem> public_500px_list_photos = new ArrayList<RSSPhotoItem>();
	ArrayList<RSSPhotoItem> pub_500px_list_not_save = new ArrayList<RSSPhotoItem>();
	int public_500px_rows_display = 0;
	int public_500px_count = 1;
	ImageButton btn_public_500px_more;
	// Personal 500px
	static LinearLayout ln_personal_500px;
	TextView txtpersonal_500px;
	GridView gv_personal_500px;
	ArrayList<RSSPhotoItem_Personal> personal_500px_list_photos = new ArrayList<RSSPhotoItem_Personal>();
	ArrayList<RSSPhotoItem> per_500px_list_not_save = new ArrayList<RSSPhotoItem>();
	int personal_500px_rows_display = 0;
	int personal_500px_count = 1;
	ImageButton btn_personal_500px_more;

	// Personal Sohu
	static LinearLayout ln_personal_sohu;
	TextView txtpersonal_sohu;
	GridView gv_personal_sohu;
	ArrayList<RSSPhotoItem_Personal> personal_sohu_list_photos = new ArrayList<RSSPhotoItem_Personal>();
	ArrayList<RSSPhotoItem> per_sohu_list_not_save = new ArrayList<RSSPhotoItem>();
	int personal_sohu_rows_display = 2;
	int personal_sohu_count = 1;
	ImageButton btn_personal_sohu_more;
	public static boolean personal_sohu_download=false;
	
	public static int DEFAULT_THUMBNAIL_SIZE = 0;
	static int cols = 3;
	int rows = 2;
	int position = 0;
	static ArrayList<String> array_ID = new ArrayList<String>();

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.newgallery, container, false);
	}


	@SuppressWarnings("deprecation")
	@Override
	public void onViewCreated (View view, @Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i("newGallery","onCreate");
		getActivity().setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		ctx = getContext();
		cache=CacheStore.getInstance();
		color_line = R.color.blue_dark;
		// listService.clear();
		list_thumb.clear();
		list_thumb = RSSUtil.getLocalPhotos(ctx);
		list_thumb_personal.clear();
		list_thumb_personal = RSSUtil.getLocalPhotosPersonal(ctx);

		// Clear bitmap
		bitmap_list.clear();
		bitmap_p_flickr.clear();
		bitmap_recent_flickr.clear();
		bitmap_public_picasa.clear();
		bitmap_p_yahoo.clear();
		bitmap_p_deviant.clear();
		bitmap_personal_facebook.clear();
		bitmap_personal_tumblr.clear();
		bitmap_personal_vkontakte.clear();
		bitmap_personal_flickr.clear();
		bitmap_personal_picasa.clear();
		bitmap_personal_deviantart.clear();
		bitmap_personal_imageshack.clear();
		bitmap_personal_imgur.clear();
		bitmap_public_imgur.clear();
		bitmap_my_feed_services.clear();
		bitmap_my_feed_services1.clear();
		bitmap_my_feed_services2.clear();
		bitmap_my_feed_services3.clear();
		bitmap_my_feed_services4.clear();
		bitmap_my_feed_services5.clear();
		bitmap_personal_500px.clear();
		bitmap_public_500px.clear();
		bitmap_personal_kaixin.clear();
		bitmap_personal_sohu.clear();

		Display display = ((Activity) ctx).getWindowManager()
				.getDefaultDisplay();
		int w = display.getWidth();

		DEFAULT_THUMBNAIL_SIZE = (int) w / cols;
		HIGHT_DISPLAY_PHOTOS = DEFAULT_THUMBNAIL_SIZE * rows + 40;
		Log.d("Listhumb", String.valueOf(list_thumb.size()));
		Log.d("Listhumb personal", String.valueOf(list_thumb_personal.size()));
		rss_folder = new File(PhimpMe.DataDirectory.getAbsolutePath() + "/"
				+ RSSUtil.RSS_ITEM_FOLDER);

		if (!rss_folder.exists()) {
			rss_folder.mkdirs();
		}

		linear_main = (LinearLayout) getView().findViewById(R.id.newgalleryContent);
		RelativeLayout.LayoutParams lp_more = new RelativeLayout.LayoutParams(
				40, 40);
		lp_more.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		
				for (int i = 0; i < PhimpMe.phimpme_array_list.size(); i++) {
					if (PhimpMe.phimpme_array_list.get(i).size() == 0)
						continue;
					else {
						if (PhimpMe.phimpme_array_list.get(i).get(0).getService()
								.equals("public_flickr")) {
							Button btn_line = new Button(ctx);
							btn_line.setHeight(2);
							btn_line.setWidth(LayoutParams.MATCH_PARENT);
							btn_line.setBackgroundResource(color_line);

							txtPFlickr = new TextView(getContext());
							txtPFlickr.setText("Public Flickr");
							txtPFlickr.setTextSize(text_size);
							p_flickr = new GridView(getContext());
							p_flickr.setPadding(0, 10, 0, 0);
							ln_flickr = new LinearLayout(getContext());
							btn_flickr_more = new ImageButton(getContext());
							btn_flickr_more.setImageResource(R.drawable.more_disable);
							btn_flickr_more.setEnabled(false);
							RelativeLayout more_li = new RelativeLayout(getContext());
							btn_flickr_more.setLayoutParams(lp_more);
							more_li.addView(btn_flickr_more);
							more_li.addView(txtPFlickr);
							ln_flickr.setOrientation(LinearLayout.VERTICAL);
							ln_flickr.addView(more_li);
							ln_flickr.addView(btn_line);
							ln_flickr.addView(p_flickr);

							ln_flickr.setEnabled(false);
							linear_main.addView(ln_flickr);
							p_flickr.setNumColumns(cols);
							p_flickr.setDrawingCacheEnabled(true);
							flickr_count= 0;
							final ArrayList<RSSPhotoItem> list = PhimpMe.phimpme_array_list
									.get(i);
							int count = 0;
							for (int j = 0; j < list.size(); j++) {
								String url = list.get(j).getURL();
								String filepath = rss_folder.getAbsolutePath()
										+ "/"
										+ url.toLowerCase().replace("://", "")
												.replace("/", "_");
								File f = new File(filepath);
								if (f.exists()) {
									try {

										InputStream is = new FileInputStream(f);

										BitmapFactory.Options bfOpt = new BitmapFactory.Options();

										bfOpt.inScaled = true;
										bfOpt.inSampleSize = 2;
										bfOpt.inPurgeable = true;

										Bitmap bmp = BitmapFactory.decodeStream(is,
												null, bfOpt);
										bmp = ImageUtil.scaleCenterCrop(bmp,
												newGallery.DEFAULT_THUMBNAIL_SIZE,
												newGallery.DEFAULT_THUMBNAIL_SIZE);
										bitmap_p_flickr.add(0,bmp);
										count++;
									} catch (Exception e) {
									}
								} else {
									filckr_list_not_save.add(list.get(j));
								}

							}
							if (filckr_list_not_save.size() > 0) {
								btn_flickr_more.setImageResource(R.drawable.more);
								btn_flickr_more.setEnabled(true);
							}
							if (count < 3) {
								hight_display = DEFAULT_THUMBNAIL_SIZE + 100;
							} else if (count % 3 != 0) {
								hight_display = DEFAULT_THUMBNAIL_SIZE
										* (count / 3 + 1) + 100;
							} else {
								hight_display = DEFAULT_THUMBNAIL_SIZE * (count / 3)
										+ 100;
							}

							LinearLayout.LayoutParams rep = new LinearLayout.LayoutParams(
									ViewGroup.LayoutParams.WRAP_CONTENT, hight_display);
							ln_flickr.setLayoutParams(rep);

							p_flickr.setAdapter(flickradapter);
							btn_flickr_more.setOnClickListener(new OnClickListener() {
								@Override
								public void onClick(View v) {
									btn_flickr_more
											.setImageResource(R.drawable.more_disable);
									btn_flickr_more.setEnabled(false);
									if ((filckr_list_not_save.size() - flickr_count
											* NUMBER_PHOTO_NEED_DOWNLOAD) > 3) {
										hight_display += DEFAULT_THUMBNAIL_SIZE * 2;
									} else if ((filckr_list_not_save.size() - flickr_count
											* NUMBER_PHOTO_NEED_DOWNLOAD) > 0)
										hight_display += DEFAULT_THUMBNAIL_SIZE;

									LinearLayout.LayoutParams rep = new LinearLayout.LayoutParams(
											ViewGroup.LayoutParams.WRAP_CONTENT,
											hight_display);
									ln_flickr.setLayoutParams(rep);
									ln_flickr.setEnabled(true);
									moreClick(filckr_list_not_save, flickr_count);
									flickr_count++;
								}
							});
						}
						// Flickr Recent
						if (PhimpMe.phimpme_array_list.get(i).get(0).getService()
								.equals("recent_flickr")) {
							Button btn_line = new Button(ctx);
							btn_line.setHeight(2);
							btn_line.setWidth(LayoutParams.MATCH_PARENT);
							btn_line.setBackgroundResource(color_line);

							txtRecentFlickr = new TextView(getContext());
							txtRecentFlickr.setText("Recent Flickr");
							txtRecentFlickr.setTextSize(text_size);
							recent_flickr = new GridView(getContext());
							recent_flickr.setPadding(0, 10, 0, 0);
							ln_recent_flickr = new LinearLayout(getContext());
							btn_recent_flickr_more = new ImageButton(getContext());
							btn_recent_flickr_more
									.setImageResource(R.drawable.more_disable);
							btn_recent_flickr_more.setEnabled(false);

							RelativeLayout more_li = new RelativeLayout(getContext());
							btn_recent_flickr_more.setLayoutParams(lp_more);
							more_li.addView(btn_recent_flickr_more);
							more_li.addView(txtRecentFlickr);

							ln_recent_flickr.setOrientation(LinearLayout.VERTICAL);

							ln_recent_flickr.addView(more_li);
							ln_recent_flickr.addView(btn_line);
							ln_recent_flickr.addView(recent_flickr);

							ln_recent_flickr.setEnabled(false);
							linear_main.addView(ln_recent_flickr);
							recent_flickr.setNumColumns(cols);
							recent_flickr.setDrawingCacheEnabled(true);
							recent_flickr_count= 0;
							final ArrayList<RSSPhotoItem> list = PhimpMe.phimpme_array_list
									.get(i);
							final ArrayList<RSSPhotoItem> list_not_save = new ArrayList<RSSPhotoItem>();
							int count = 0;
							for (int j = 0; j < list.size(); j++) {
								String url = list.get(j).getURL();
								String filepath = rss_folder.getAbsolutePath()
										+ "/"
										+ url.toLowerCase().replace("://", "")
												.replace("/", "_");
								File f = new File(filepath);
								if (f.exists()) {
									try {
										InputStream is = new FileInputStream(f);

										BitmapFactory.Options bfOpt = new BitmapFactory.Options();

										bfOpt.inScaled = true;
										bfOpt.inSampleSize = 2;
										bfOpt.inPurgeable = true;

										Bitmap bmp = BitmapFactory.decodeStream(is,
												null, bfOpt);
										bmp = ImageUtil.scaleCenterCrop(bmp,
												newGallery.DEFAULT_THUMBNAIL_SIZE,
												newGallery.DEFAULT_THUMBNAIL_SIZE);
										bitmap_recent_flickr.add(0,bmp);
										count++;
									} catch (Exception e) {
									}
								} else {
									list_not_save.add(list.get(j));
								}

							}
							if (list_not_save.size() > 0) {
								btn_recent_flickr_more
										.setImageResource(R.drawable.more);
								btn_recent_flickr_more.setEnabled(true);
							}
							if (count < 3) {
								hight_display = DEFAULT_THUMBNAIL_SIZE + 100;
							} else if (count % 3 != 0) {
								hight_display = DEFAULT_THUMBNAIL_SIZE
										* (count / 3 + 1) + 100;
							} else {
								hight_display = DEFAULT_THUMBNAIL_SIZE * (count / 3)
										+ 100;
							}

							LinearLayout.LayoutParams rep = new LinearLayout.LayoutParams(
									ViewGroup.LayoutParams.WRAP_CONTENT, hight_display);
							ln_recent_flickr.setLayoutParams(rep);

							recent_flickr.setAdapter(recentflickradapter);
							btn_recent_flickr_more
									.setOnClickListener(new OnClickListener() {
										@Override
										public void onClick(View v) {
											btn_recent_flickr_more
													.setImageResource(R.drawable.more_disable);
											btn_recent_flickr_more.setEnabled(false);
											if ((list_not_save.size() - recent_flickr_count
													* NUMBER_PHOTO_NEED_DOWNLOAD) > 3) {
												hight_display += DEFAULT_THUMBNAIL_SIZE * 2;
											} else if ((list_not_save.size() - recent_flickr_count
													* NUMBER_PHOTO_NEED_DOWNLOAD) > 0)
												hight_display += DEFAULT_THUMBNAIL_SIZE;

											LinearLayout.LayoutParams rep = new LinearLayout.LayoutParams(
													ViewGroup.LayoutParams.WRAP_CONTENT,
													hight_display);
											ln_recent_flickr.setLayoutParams(rep);
											ln_recent_flickr.setEnabled(true);
											moreClick(list_not_save,
													recent_flickr_count);
											recent_flickr_count++;
										}
									});
						}
						// Yahoo new
						if (PhimpMe.phimpme_array_list.get(i).get(0).getService()
								.equals("public_yahoo")) {
							Button btn_line = new Button(ctx);
							btn_line.setHeight(2);
							btn_line.setWidth(LayoutParams.MATCH_PARENT);
							btn_line.setBackgroundResource(color_line);

							txtyahoo = new TextView(getContext());
							txtyahoo.setText("Public Yahoo");
							txtyahoo.setTextSize(text_size);
							p_yahoo = new GridView(getContext());
							p_yahoo.setPadding(0, 10, 0, 0);
							ln_yahoo = new LinearLayout(getContext());
							btn_yahoo_more = new ImageButton(getContext());
							btn_yahoo_more.setImageResource(R.drawable.more_disable);
							btn_yahoo_more.setEnabled(false);

							RelativeLayout more_li = new RelativeLayout(getContext());
							btn_yahoo_more.setLayoutParams(lp_more);
							more_li.addView(btn_yahoo_more);
							more_li.addView(txtyahoo);

							ln_yahoo.setOrientation(LinearLayout.VERTICAL);
							ln_yahoo.addView(more_li);
							ln_yahoo.addView(btn_line);
							ln_yahoo.addView(p_yahoo);

							ln_yahoo.setEnabled(false);
							linear_main.addView(ln_yahoo);
							p_yahoo.setNumColumns(cols);
							p_yahoo.setDrawingCacheEnabled(true);
							yahoo_count= 0;
							final ArrayList<RSSPhotoItem> list = PhimpMe.phimpme_array_list
									.get(i);
							final ArrayList<RSSPhotoItem> list_not_save = new ArrayList<RSSPhotoItem>();
							int count = 0;
							for (int j = 0; j < list.size(); j++) {
								String url = list.get(j).getURL();
								String filepath = rss_folder.getAbsolutePath()
										+ "/"
										+ url.toLowerCase().replace("://", "")
												.replace("/", "_");
								File f = new File(filepath);
								if (f.exists()) {
									try {
										InputStream is = new FileInputStream(f);

										BitmapFactory.Options bfOpt = new BitmapFactory.Options();

										bfOpt.inScaled = true;
										bfOpt.inSampleSize = 2;
										bfOpt.inPurgeable = true;

										Bitmap bmp = BitmapFactory.decodeStream(is,
												null, bfOpt);
										bmp = ImageUtil.scaleCenterCrop(bmp,
												newGallery.DEFAULT_THUMBNAIL_SIZE,
												newGallery.DEFAULT_THUMBNAIL_SIZE);
										bitmap_p_yahoo.add(0,bmp);
										count++;
									} catch (Exception e) {
									}
								} else {
									list_not_save.add(list.get(j));
								}

							}
							if (list_not_save.size() > 0) {
								btn_yahoo_more.setImageResource(R.drawable.more);
								btn_yahoo_more.setEnabled(true);
							}
							if (count < 3) {
								hight_display = DEFAULT_THUMBNAIL_SIZE + 100;
							} else if (count % 3 != 0) {
								hight_display = DEFAULT_THUMBNAIL_SIZE
										* (count / 3 + 1) + 100;
							} else {
								hight_display = DEFAULT_THUMBNAIL_SIZE * (count / 3)
										+ 100;
							}

							LinearLayout.LayoutParams rep = new LinearLayout.LayoutParams(
									ViewGroup.LayoutParams.WRAP_CONTENT, hight_display);
							ln_yahoo.setLayoutParams(rep);

							p_yahoo.setAdapter(yahooadapter);
							btn_yahoo_more.setOnClickListener(new OnClickListener() {
								@Override
								public void onClick(View v) {
									btn_yahoo_more
											.setImageResource(R.drawable.more_disable);
									btn_yahoo_more.setEnabled(false);
									if ((list_not_save.size() - yahoo_count
											* NUMBER_PHOTO_NEED_DOWNLOAD) > 3) {
										hight_display += DEFAULT_THUMBNAIL_SIZE * 2;
									} else if ((list_not_save.size() - yahoo_count
											* NUMBER_PHOTO_NEED_DOWNLOAD) > 0)
										hight_display += DEFAULT_THUMBNAIL_SIZE;

									LinearLayout.LayoutParams rep = new LinearLayout.LayoutParams(
											ViewGroup.LayoutParams.WRAP_CONTENT,
											hight_display);
									ln_yahoo.setLayoutParams(rep);
									ln_yahoo.setEnabled(true);
									moreClick(list_not_save, yahoo_count);
									yahoo_count++;
								}
							});
						}
						// Public Picasa
						if (PhimpMe.phimpme_array_list.get(i).get(0).getService()
								.equals("public_picasa")) {
							Button btn_line = new Button(ctx);
							btn_line.setHeight(2);
							btn_line.setWidth(LayoutParams.MATCH_PARENT);
							btn_line.setBackgroundResource(color_line);

							txtPublicPicasa = new TextView(getContext());
							txtPublicPicasa.setText("Public Picasa");
							txtPublicPicasa.setTextSize(text_size);
							p_picasa = new GridView(getContext());
							p_picasa.setPadding(0, 10, 0, 0);
							ln_public_picasa = new LinearLayout(getContext());
							btn_public_picasa_more = new ImageButton(getContext());
							btn_public_picasa_more
									.setImageResource(R.drawable.more_disable);
							btn_public_picasa_more.setEnabled(false);

							RelativeLayout more_li = new RelativeLayout(getContext());
							btn_public_picasa_more.setLayoutParams(lp_more);
							more_li.addView(btn_public_picasa_more);
							more_li.addView(txtPublicPicasa);
							ln_public_picasa.setOrientation(LinearLayout.VERTICAL);
							ln_public_picasa.addView(more_li);
							ln_public_picasa.addView(btn_line);
							ln_public_picasa.addView(p_picasa);

							ln_public_picasa.setEnabled(false);
							linear_main.addView(ln_public_picasa);
							p_picasa.setNumColumns(cols);
							p_picasa.setDrawingCacheEnabled(true);
							public_picasa_count= 0;
							final ArrayList<RSSPhotoItem> list = PhimpMe.phimpme_array_list
									.get(i);
							final ArrayList<RSSPhotoItem> list_not_save = new ArrayList<RSSPhotoItem>();
							int count = 0;
							for (int j = 0; j < list.size(); j++) {
								String url = list.get(j).getURL();
								String filepath = rss_folder.getAbsolutePath()
										+ "/"
										+ url.toLowerCase().replace("://", "")
												.replace("/", "_");
								File f = new File(filepath);
								if (f.exists()) {
									try {
										InputStream is = new FileInputStream(f);

										BitmapFactory.Options bfOpt = new BitmapFactory.Options();

										bfOpt.inScaled = true;
										bfOpt.inSampleSize = 2;
										bfOpt.inPurgeable = true;

										Bitmap bmp = BitmapFactory.decodeStream(is,
												null, bfOpt);
										bmp = ImageUtil.scaleCenterCrop(bmp,
												newGallery.DEFAULT_THUMBNAIL_SIZE,
												newGallery.DEFAULT_THUMBNAIL_SIZE);
										bitmap_public_picasa.add(0,bmp);
										count++;
									} catch (Exception e) {
									}
								} else {
									list_not_save.add(list.get(j));
								}

							}
							if (list_not_save.size() > 0) {
								btn_public_picasa_more
										.setImageResource(R.drawable.more);
								btn_public_picasa_more.setEnabled(true);
							}
							if (count < 3) {
								hight_display = DEFAULT_THUMBNAIL_SIZE + 100;
							} else if (count % 3 != 0) {
								hight_display = DEFAULT_THUMBNAIL_SIZE
										* (count / 3 + 1) + 100;
							} else {
								hight_display = DEFAULT_THUMBNAIL_SIZE * (count / 3)
										+ 100;
							}

							LinearLayout.LayoutParams rep = new LinearLayout.LayoutParams(
									ViewGroup.LayoutParams.WRAP_CONTENT, hight_display);
							ln_public_picasa.setLayoutParams(rep);

							p_picasa.setAdapter(publicpicasaadapter);
							btn_public_picasa_more
									.setOnClickListener(new OnClickListener() {
										@Override
										public void onClick(View v) {
											btn_public_picasa_more
													.setImageResource(R.drawable.more_disable);
											btn_public_picasa_more.setEnabled(false);
											if ((list_not_save.size() - public_picasa_count
													* NUMBER_PHOTO_NEED_DOWNLOAD) > 3) {
												hight_display += DEFAULT_THUMBNAIL_SIZE * 2;
											} else if ((list_not_save.size() - public_picasa_count
													* NUMBER_PHOTO_NEED_DOWNLOAD) > 0)
												hight_display += DEFAULT_THUMBNAIL_SIZE;

											LinearLayout.LayoutParams rep = new LinearLayout.LayoutParams(
													ViewGroup.LayoutParams.WRAP_CONTENT,
													hight_display);
											ln_public_picasa.setLayoutParams(rep);
											ln_public_picasa.setEnabled(true);
											moreClick(list_not_save,
													public_picasa_count);
											public_picasa_count++;
										}
									});
						}
						// Google news
						if (PhimpMe.phimpme_array_list.get(i).get(0).getService()
								.equals("google_news")) {
							Button btn_line = new Button(ctx);
							btn_line.setHeight(2);
							btn_line.setWidth(LayoutParams.MATCH_PARENT);
							btn_line.setBackgroundResource(color_line);

							txtGooglenews = new TextView(getContext());
							txtGooglenews.setText("Google News");
							txtGooglenews.setTextSize(text_size);
							p_googlenews = new GridView(getContext());
							p_googlenews.setPadding(0, 10, 0, 0);
							ln_googlenews = new LinearLayout(getContext());
							btn_googlenews_more = new ImageButton(getContext());
							btn_googlenews_more
									.setImageResource(R.drawable.more_disable);
							btn_googlenews_more.setEnabled(false);

							RelativeLayout more_li = new RelativeLayout(getContext());
							btn_googlenews_more.setLayoutParams(lp_more);
							more_li.addView(btn_googlenews_more);
							more_li.addView(txtGooglenews);
							ln_googlenews.setOrientation(LinearLayout.VERTICAL);
							ln_googlenews.addView(more_li);
							ln_googlenews.addView(btn_line);
							ln_googlenews.addView(p_googlenews);

							ln_googlenews.setEnabled(false);
							linear_main.addView(ln_googlenews);
							p_googlenews.setNumColumns(cols);
							p_googlenews.setDrawingCacheEnabled(true);
							googlenews_count= 0;
							final ArrayList<RSSPhotoItem> list = PhimpMe.phimpme_array_list
									.get(i);
							final ArrayList<RSSPhotoItem> list_not_save = new ArrayList<RSSPhotoItem>();
							int count = 0;
							for (int j = 0; j < list.size(); j++) {
								String url = list.get(j).getURL();
								String filepath = rss_folder.getAbsolutePath()
										+ "/"
										+ url.toLowerCase().replace("://", "")
												.replace("/", "_");
								File f = new File(filepath);
								if (f.exists()) {
									try {
										InputStream is = new FileInputStream(f);

										BitmapFactory.Options bfOpt = new BitmapFactory.Options();

										bfOpt.inScaled = true;
										bfOpt.inSampleSize = 2;
										bfOpt.inPurgeable = true;

										Bitmap bmp = BitmapFactory.decodeStream(is,
												null, bfOpt);
										bmp = ImageUtil.scaleCenterCrop(bmp,
												newGallery.DEFAULT_THUMBNAIL_SIZE,
												newGallery.DEFAULT_THUMBNAIL_SIZE);
										bitmap_google_news.add(0,bmp);					
										count++;
									} catch (Exception e) {
									}
								} else {
									list_not_save.add(list.get(j));
								}

							}
							if (list_not_save.size() > 0) {
								btn_googlenews_more.setImageResource(R.drawable.more);
								btn_googlenews_more.setEnabled(true);
							}
							if (count < 3) {
								hight_display = DEFAULT_THUMBNAIL_SIZE + 100;
							} else if (count % 3 != 0) {
								hight_display = DEFAULT_THUMBNAIL_SIZE
										* (count / 3 + 1) + 100;
							} else {
								hight_display = DEFAULT_THUMBNAIL_SIZE * (count / 3)
										+ 100;
							}

							LinearLayout.LayoutParams rep = new LinearLayout.LayoutParams(
									ViewGroup.LayoutParams.WRAP_CONTENT, hight_display);
							ln_googlenews.setLayoutParams(rep);

							p_googlenews.setAdapter(googlenewsadapter);
							btn_googlenews_more
									.setOnClickListener(new OnClickListener() {
										@Override
										public void onClick(View v) {
											btn_googlenews_more
													.setImageResource(R.drawable.more_disable);
											btn_googlenews_more.setEnabled(false);
											if ((list_not_save.size() - googlenews_count
													* NUMBER_PHOTO_NEED_DOWNLOAD) > 3) {
												hight_display += DEFAULT_THUMBNAIL_SIZE * 2;
											} else if ((list_not_save.size() - googlenews_count
													* NUMBER_PHOTO_NEED_DOWNLOAD) > 0)
												hight_display += DEFAULT_THUMBNAIL_SIZE;

											LinearLayout.LayoutParams rep = new LinearLayout.LayoutParams(
													ViewGroup.LayoutParams.WRAP_CONTENT,
													hight_display);
											ln_googlenews.setLayoutParams(rep);
											ln_googlenews.setEnabled(true);
											moreClick(list_not_save, googlenews_count);
											googlenews_count++;
										}
									});
						}
						// DeviantArt Public
						if (PhimpMe.phimpme_array_list.get(i).get(0).getService()
								.equals("public_deviant")) {
							Button btn_line = new Button(ctx);
							btn_line.setHeight(2);
							btn_line.setWidth(LayoutParams.MATCH_PARENT);
							btn_line.setBackgroundResource(color_line);

							txtdeviant = new TextView(getContext());
							txtdeviant.setText("Public DeviantArt");
							txtdeviant.setTextSize(text_size);
							p_deviant = new GridView(getContext());
							p_deviant.setPadding(0, 10, 0, 0);
							ln_deviant = new LinearLayout(getContext());
							btn_public_deviant_more = new ImageButton(getContext());
							btn_public_deviant_more
									.setImageResource(R.drawable.more_disable);
							btn_public_deviant_more.setEnabled(false);

							RelativeLayout more_li = new RelativeLayout(getContext());
							btn_public_deviant_more.setLayoutParams(lp_more);
							more_li.addView(btn_public_deviant_more);
							more_li.addView(txtdeviant);
							ln_deviant.setOrientation(LinearLayout.VERTICAL);
							ln_deviant.addView(more_li);
							ln_deviant.addView(btn_line);
							ln_deviant.addView(p_deviant);

							ln_deviant.setEnabled(false);
							linear_main.addView(ln_deviant);
							p_deviant.setNumColumns(cols);
							p_deviant.setDrawingCacheEnabled(true);
							deviant_count= 0;
							final ArrayList<RSSPhotoItem> list = PhimpMe.phimpme_array_list
									.get(i);
							final ArrayList<RSSPhotoItem> list_not_save = new ArrayList<RSSPhotoItem>();
							int count = 0;
							for (int j = 0; j < list.size(); j++) {
								String url = list.get(j).getURL();
								String filepath = rss_folder.getAbsolutePath()
										+ "/"
										+ url.toLowerCase().replace("://", "")
												.replace("/", "_");
								File f = new File(filepath);
								if (f.exists()) {
									try {
										InputStream is = new FileInputStream(f);

										BitmapFactory.Options bfOpt = new BitmapFactory.Options();

										bfOpt.inScaled = true;
										bfOpt.inSampleSize = 2;
										bfOpt.inPurgeable = true;

										Bitmap bmp = BitmapFactory.decodeStream(is,
												null, bfOpt);
										bmp = ImageUtil.scaleCenterCrop(bmp,
												newGallery.DEFAULT_THUMBNAIL_SIZE,
												newGallery.DEFAULT_THUMBNAIL_SIZE);
										bitmap_p_deviant.add(0,bmp);
										count++;
									} catch (Exception e) {
									}
								} else {
									list_not_save.add(list.get(j));
								}

							}
							if (list_not_save.size() > 0) {
								btn_public_deviant_more
										.setImageResource(R.drawable.more);
								btn_public_deviant_more.setEnabled(true);
							}
							if (count < 3) {
								hight_display = DEFAULT_THUMBNAIL_SIZE + 100;
							} else if (count % 3 != 0) {
								hight_display = DEFAULT_THUMBNAIL_SIZE
										* (count / 3 + 1) + 100;
							} else {
								hight_display = DEFAULT_THUMBNAIL_SIZE * (count / 3)
										+ 100;
							}

							LinearLayout.LayoutParams rep = new LinearLayout.LayoutParams(
									ViewGroup.LayoutParams.WRAP_CONTENT, hight_display);
							ln_deviant.setLayoutParams(rep);

							p_deviant.setAdapter(deviantadapter);
							btn_public_deviant_more
									.setOnClickListener(new OnClickListener() {
										@Override
										public void onClick(View v) {
											btn_public_deviant_more
													.setImageResource(R.drawable.more_disable);
											btn_public_deviant_more.setEnabled(false);
											if ((list_not_save.size() - deviant_count
													* NUMBER_PHOTO_NEED_DOWNLOAD) > 3) {
												hight_display += DEFAULT_THUMBNAIL_SIZE * 2;
											} else if ((list_not_save.size() - deviant_count
													* NUMBER_PHOTO_NEED_DOWNLOAD) > 0)
												hight_display += DEFAULT_THUMBNAIL_SIZE;

											LinearLayout.LayoutParams rep = new LinearLayout.LayoutParams(
													ViewGroup.LayoutParams.WRAP_CONTENT,
													hight_display);
											ln_deviant.setLayoutParams(rep);
											ln_deviant.setEnabled(true);
											moreClick(list_not_save, deviant_count);
											deviant_count++;
										}
									});
						}
						/*
						 * public imgur
						 */
						if (PhimpMe.phimpme_array_list.get(i).get(0).getService()
								.equals("public_imgur")) {
							Button btn_line = new Button(ctx);
							btn_line.setHeight(2);
							btn_line.setWidth(LayoutParams.MATCH_PARENT);
							btn_line.setBackgroundResource(color_line);

							txtpublic_imgur = new TextView(getContext());
							txtpublic_imgur.setText("Public Imgur");
							txtpublic_imgur.setTextSize(text_size);
							gv_public_imgur = new GridView(getContext());
							gv_public_imgur.setPadding(0, 10, 0, 0);
							ln_public_imgur = new LinearLayout(getContext());
							btn_public_imgur_more = new ImageButton(getContext());
							btn_public_imgur_more
									.setImageResource(R.drawable.more_disable);
							btn_public_imgur_more.setEnabled(false);

							RelativeLayout more_li = new RelativeLayout(getContext());
							btn_public_imgur_more.setLayoutParams(lp_more);
							more_li.addView(btn_public_imgur_more);
							more_li.addView(txtpublic_imgur);
							ln_public_imgur.setOrientation(LinearLayout.VERTICAL);
							ln_public_imgur.addView(more_li);
							ln_public_imgur.addView(btn_line);
							ln_public_imgur.addView(gv_public_imgur);

							ln_public_imgur.setEnabled(false);
							linear_main.addView(ln_public_imgur);
							gv_public_imgur.setNumColumns(cols);
							gv_public_imgur.setDrawingCacheEnabled(true);
							public_imgur_count= 0;
							final ArrayList<RSSPhotoItem> list = PhimpMe.phimpme_array_list
									.get(i);
							final ArrayList<RSSPhotoItem> list_not_save = new ArrayList<RSSPhotoItem>();
							int count = 0;
							for (int j = 0; j < list.size(); j++) {
								String url = list.get(j).getURL();
								String filepath = rss_folder.getAbsolutePath()
										+ "/"
										+ url.toLowerCase().replace("://", "")
												.replace("/", "_");
								File f = new File(filepath);
								if (f.exists()) {
									try {
										InputStream is = new FileInputStream(f);

										BitmapFactory.Options bfOpt = new BitmapFactory.Options();

										bfOpt.inScaled = true;
										bfOpt.inSampleSize = 2;
										bfOpt.inPurgeable = true;

										Bitmap bmp = BitmapFactory.decodeStream(is,
												null, bfOpt);
										bmp = ImageUtil.scaleCenterCrop(bmp,
												newGallery.DEFAULT_THUMBNAIL_SIZE,
												newGallery.DEFAULT_THUMBNAIL_SIZE);
										bitmap_public_imgur.add(0,bmp);
										count++;
									} catch (Exception e) {
									}
								} else {
									list_not_save.add(list.get(j));
								}

							}
							if (list_not_save.size() > 0) {
								btn_public_imgur_more.setImageResource(R.drawable.more);
								btn_public_imgur_more.setEnabled(true);
							}
							if (count < 3) {
								hight_display = DEFAULT_THUMBNAIL_SIZE + 100;
							} else if (count % 3 != 0) {
								hight_display = DEFAULT_THUMBNAIL_SIZE
										* (count / 3 + 1) + 100;
							} else {
								hight_display = DEFAULT_THUMBNAIL_SIZE * (count / 3)
										+ 100;
							}

							LinearLayout.LayoutParams rep = new LinearLayout.LayoutParams(
									ViewGroup.LayoutParams.WRAP_CONTENT, hight_display);
							ln_public_imgur.setLayoutParams(rep);

							gv_public_imgur.setAdapter(public_imguradapter);
							btn_public_imgur_more
									.setOnClickListener(new OnClickListener() {
										@Override
										public void onClick(View v) {
											btn_public_imgur_more
													.setImageResource(R.drawable.more_disable);
											btn_public_imgur_more.setEnabled(false);
											if ((list_not_save.size() - public_imgur_count
													* NUMBER_PHOTO_NEED_DOWNLOAD) > 3) {
												hight_display += DEFAULT_THUMBNAIL_SIZE * 2;
											} else if ((list_not_save.size() - public_imgur_count
													* NUMBER_PHOTO_NEED_DOWNLOAD) > 0)
												hight_display += DEFAULT_THUMBNAIL_SIZE;

											LinearLayout.LayoutParams rep = new LinearLayout.LayoutParams(
													ViewGroup.LayoutParams.WRAP_CONTENT,
													hight_display);
											ln_public_imgur.setLayoutParams(rep);
											ln_public_imgur.setEnabled(true);
											moreClick(list_not_save, public_imgur_count);
											public_imgur_count++;
										}
									});
						}
						/*
						 * public 500px
						 */
						if (PhimpMe.phimpme_array_list.get(i).get(0).getService()
								.equals("public_500px")) {
							txtPublic500px = new TextView(getContext());
							txtPublic500px.setText("Public 500px");
							p_500px = new GridView(getContext());
							ln_public_500px = new LinearLayout(getContext());
							btn_public_500px_more = new ImageButton(getContext());
							btn_public_500px_more
									.setImageResource(R.drawable.more_disable);
							btn_public_500px_more.setEnabled(false);

							RelativeLayout more_li = new RelativeLayout(getContext());
							btn_public_500px_more.setLayoutParams(lp_more);
							more_li.addView(btn_public_500px_more);

							ln_public_500px.setOrientation(LinearLayout.VERTICAL);
							ln_public_500px.addView(txtPublic500px);
							ln_public_500px.addView(p_500px);
							ln_public_500px.addView(more_li);
							ln_public_500px.setEnabled(false);
							linear_main.addView(ln_public_500px);
							p_500px.setNumColumns(cols);
							p_500px.setDrawingCacheEnabled(true);
							public_500px_count= 0;
							final ArrayList<RSSPhotoItem> list = PhimpMe.phimpme_array_list
									.get(i);
							final ArrayList<RSSPhotoItem> list_not_save = new ArrayList<RSSPhotoItem>();
							int count = 0;
							for (int j = 0; j < list.size(); j++) {
								String url = list.get(j).getURL();
								String filepath = rss_folder.getAbsolutePath()
										+ "/"
										+ url.toLowerCase().replace("://", "")
												.replace("/", "_");
								File f = new File(filepath);
								if (f.exists()) {
									try {
										InputStream is = new FileInputStream(f);

										BitmapFactory.Options bfOpt = new BitmapFactory.Options();

										bfOpt.inScaled = true;
										bfOpt.inSampleSize = 2;
										bfOpt.inPurgeable = true;

										Bitmap bmp = BitmapFactory.decodeStream(is,
												null, bfOpt);
										bmp = ImageUtil.scaleCenterCrop(bmp,
												newGallery.DEFAULT_THUMBNAIL_SIZE,
												newGallery.DEFAULT_THUMBNAIL_SIZE);
										bitmap_public_500px.add(0,bmp);
										count++;
									} catch (Exception e) {
									}
								} else {
									list_not_save.add(list.get(j));
								}

							}
							if (list_not_save.size() > 0) {
								btn_public_500px_more.setImageResource(R.drawable.more);
								btn_public_500px_more.setEnabled(true);
							}
							if (count < 3) {
								hight_display = DEFAULT_THUMBNAIL_SIZE + 100;
							} else if (count % 3 != 0) {
								hight_display = DEFAULT_THUMBNAIL_SIZE
										* (count / 3 + 1) + 100;
							} else {
								hight_display = DEFAULT_THUMBNAIL_SIZE * (count / 3)
										+ 100;
							}

							LinearLayout.LayoutParams rep = new LinearLayout.LayoutParams(
									ViewGroup.LayoutParams.WRAP_CONTENT, hight_display);
							ln_public_500px.setLayoutParams(rep);

							p_500px.setAdapter(public500pxadapter);
							btn_public_500px_more
									.setOnClickListener(new OnClickListener() {
										@Override
										public void onClick(View v) {
											btn_public_500px_more
													.setImageResource(R.drawable.more_disable);
											btn_public_500px_more.setEnabled(false);
											if ((list_not_save.size() - public_500px_count
													* NUMBER_PHOTO_NEED_DOWNLOAD) > 3) {
												hight_display += DEFAULT_THUMBNAIL_SIZE * 2;
											} else if ((list_not_save.size() - public_500px_count
													* NUMBER_PHOTO_NEED_DOWNLOAD) > 0)
												hight_display += DEFAULT_THUMBNAIL_SIZE;

											LinearLayout.LayoutParams rep = new LinearLayout.LayoutParams(
													ViewGroup.LayoutParams.WRAP_CONTENT,
													hight_display);
											ln_public_500px.setLayoutParams(rep);
											ln_public_500px.setEnabled(true);
											moreClick(list_not_save, public_500px_count);
											public_500px_count++;
										}
									});
						}
						/*
						 * My feed services
						 */
						if (PhimpMe.phimpme_array_list.get(i).get(0).getService()
								.equals("my_feed_services")) {
							Button btn_line = new Button(ctx);
							btn_line.setHeight(2);
							btn_line.setWidth(LayoutParams.MATCH_PARENT);
							btn_line.setBackgroundResource(color_line);

							txtMyFeedServices = new TextView(getContext());
							txtMyFeedServices.setText("My Feed Photo");
							txtMyFeedServices.setTextSize(text_size);
							gv_my_feed_services = new GridView(getContext());
							gv_my_feed_services.setPadding(0, 10, 0, 0);
							ln_my_feed_services = new LinearLayout(getContext());
							btn_my_feed_services_more = new ImageButton(getContext());
							btn_my_feed_services_more
									.setImageResource(R.drawable.more_disable);
							btn_my_feed_services_more.setEnabled(false);

							RelativeLayout more_li = new RelativeLayout(getContext());
							btn_my_feed_services_more.setLayoutParams(lp_more);
							more_li.addView(btn_my_feed_services_more);
							more_li.addView(txtMyFeedServices);
							ln_my_feed_services.setOrientation(LinearLayout.VERTICAL);
							ln_my_feed_services.addView(more_li);
							ln_my_feed_services.addView(btn_line);
							ln_my_feed_services.addView(gv_my_feed_services);

							ln_my_feed_services.setEnabled(false);
							linear_main.addView(ln_my_feed_services);
							gv_my_feed_services.setNumColumns(cols);
							gv_my_feed_services.setDrawingCacheEnabled(true);
							my_feed_services_count= 0;
							final ArrayList<RSSPhotoItem> list = PhimpMe.phimpme_array_list
									.get(i);
							final ArrayList<RSSPhotoItem> list_not_save = new ArrayList<RSSPhotoItem>();
							int count = 0;
							for (int j = 0; j < list.size(); j++) {
								String url = list.get(j).getURL();
								String filepath = rss_folder.getAbsolutePath()
										+ "/"
										+ url.toLowerCase().replace("://", "")
												.replace("/", "_");
								File f = new File(filepath);
								if (f.exists()) {
									try {
										InputStream is = new FileInputStream(f);

										BitmapFactory.Options bfOpt = new BitmapFactory.Options();

										bfOpt.inScaled = true;
										bfOpt.inSampleSize = 2;
										bfOpt.inPurgeable = true;

										Bitmap bmp = BitmapFactory.decodeStream(is,
												null, bfOpt);
										bmp = ImageUtil.scaleCenterCrop(bmp,
												newGallery.DEFAULT_THUMBNAIL_SIZE,
												newGallery.DEFAULT_THUMBNAIL_SIZE);
										bitmap_my_feed_services.add(0,bmp);
										count++;

									} catch (Exception e) {
									}
								} else {
									list_not_save.add(list.get(j));
								}

							}
							if (list_not_save.size() > 0) {
								btn_my_feed_services_more
										.setImageResource(R.drawable.more);
								btn_my_feed_services_more.setEnabled(true);
							}
							if (count < 3) {
								hight_display = DEFAULT_THUMBNAIL_SIZE + 100;
							} else if (count % 3 != 0) {
								hight_display = DEFAULT_THUMBNAIL_SIZE
										* (count / 3 + 1) + 100;
							} else {
								hight_display = DEFAULT_THUMBNAIL_SIZE * (count / 3)
										+ 100;
							}

							LinearLayout.LayoutParams rep = new LinearLayout.LayoutParams(
									ViewGroup.LayoutParams.WRAP_CONTENT, hight_display);
							ln_my_feed_services.setLayoutParams(rep);

							gv_my_feed_services.setAdapter(my_feed_services_adapter);
							btn_my_feed_services_more
									.setOnClickListener(new OnClickListener() {
										@Override
										public void onClick(View v) {
											btn_my_feed_services_more
													.setImageResource(R.drawable.more_disable);
											btn_my_feed_services_more.setEnabled(false);
											if ((list_not_save.size() - my_feed_services_count
													* NUMBER_PHOTO_NEED_DOWNLOAD) > 3) {
												hight_display += DEFAULT_THUMBNAIL_SIZE * 2;
											} else if ((list_not_save.size() - my_feed_services_count
													* NUMBER_PHOTO_NEED_DOWNLOAD) > 0)
												hight_display += DEFAULT_THUMBNAIL_SIZE;

											LinearLayout.LayoutParams rep = new LinearLayout.LayoutParams(
													ViewGroup.LayoutParams.WRAP_CONTENT,
													hight_display);
											ln_my_feed_services.setLayoutParams(rep);
											ln_my_feed_services.setEnabled(true);
											moreClick(list_not_save,
													my_feed_services_count);
											my_feed_services_count++;
										}
									});
						}
						/*
						 * My feed services 1
						 */
						if (PhimpMe.phimpme_array_list.get(i).get(0).getService()
								.equals("my_feed_services1")) {
							Button btn_line = new Button(ctx);
							btn_line.setHeight(2);
							btn_line.setWidth(LayoutParams.MATCH_PARENT);
							btn_line.setBackgroundResource(color_line);

							txtMyFeedServices1 = new TextView(getContext());
							txtMyFeedServices1.setText("My Feed Photo");
							txtMyFeedServices1.setTextSize(text_size);
							gv_my_feed_services1 = new GridView(getContext());
							gv_my_feed_services1.setPadding(0, 10, 0, 0);
							ln_my_feed_services1 = new LinearLayout(getContext());
							btn_my_feed_services_more1 = new ImageButton(getContext());
							btn_my_feed_services_more1
									.setImageResource(R.drawable.more_disable);
							btn_my_feed_services_more1.setEnabled(false);

							RelativeLayout more_li = new RelativeLayout(getContext());
							btn_my_feed_services_more1.setLayoutParams(lp_more);
							more_li.addView(btn_my_feed_services_more1);
							more_li.addView(txtMyFeedServices1);
							ln_my_feed_services1.setOrientation(LinearLayout.VERTICAL);
							ln_my_feed_services1.addView(more_li);
							ln_my_feed_services1.addView(btn_line);
							ln_my_feed_services1.addView(gv_my_feed_services1);

							ln_my_feed_services1.setEnabled(false);
							linear_main.addView(ln_my_feed_services1);
							gv_my_feed_services1.setNumColumns(cols);
							gv_my_feed_services1.setDrawingCacheEnabled(true);
							my_feed_services_count1= 0;
							final ArrayList<RSSPhotoItem> list = PhimpMe.phimpme_array_list
									.get(i);
							final ArrayList<RSSPhotoItem> list_not_save = new ArrayList<RSSPhotoItem>();
							int count = 0;
							for (int j = 0; j < list.size(); j++) {
								String url = list.get(j).getURL();
								String filepath = rss_folder.getAbsolutePath()
										+ "/"
										+ url.toLowerCase().replace("://", "")
												.replace("/", "_");
								File f = new File(filepath);
								if (f.exists()) {
									try {
										InputStream is = new FileInputStream(f);

										BitmapFactory.Options bfOpt = new BitmapFactory.Options();

										bfOpt.inScaled = true;
										bfOpt.inSampleSize = 2;
										bfOpt.inPurgeable = true;

										Bitmap bmp = BitmapFactory.decodeStream(is,
												null, bfOpt);
										bmp = ImageUtil.scaleCenterCrop(bmp,
												newGallery.DEFAULT_THUMBNAIL_SIZE,
												newGallery.DEFAULT_THUMBNAIL_SIZE);
										bitmap_my_feed_services1.add(0,bmp);
										count++;
									} catch (Exception e) {
									}
								} else {
									list_not_save.add(list.get(j));
								}

							}
							if (list_not_save.size() > 0) {
								btn_my_feed_services_more1
										.setImageResource(R.drawable.more);
								btn_my_feed_services_more1.setEnabled(true);
							}
							if (count < 3) {
								hight_display = DEFAULT_THUMBNAIL_SIZE + 100;
							} else if (count % 3 != 0) {
								hight_display = DEFAULT_THUMBNAIL_SIZE
										* (count / 3 + 1) + 100;
							} else {
								hight_display = DEFAULT_THUMBNAIL_SIZE * (count / 3)
										+ 100;
							}

							LinearLayout.LayoutParams rep = new LinearLayout.LayoutParams(
									ViewGroup.LayoutParams.WRAP_CONTENT, hight_display);
							ln_my_feed_services1.setLayoutParams(rep);

							gv_my_feed_services1.setAdapter(my_feed_services_adapter1);
							btn_my_feed_services_more1
									.setOnClickListener(new OnClickListener() {
										@Override
										public void onClick(View v) {
											btn_my_feed_services_more1
													.setImageResource(R.drawable.more_disable);
											btn_my_feed_services_more1
													.setEnabled(false);
											if ((list_not_save.size() - my_feed_services_count1
													* NUMBER_PHOTO_NEED_DOWNLOAD) > 3) {
												hight_display += DEFAULT_THUMBNAIL_SIZE * 2;
											} else if ((list_not_save.size() - my_feed_services_count1
													* NUMBER_PHOTO_NEED_DOWNLOAD) > 0)
												hight_display += DEFAULT_THUMBNAIL_SIZE;

											LinearLayout.LayoutParams rep = new LinearLayout.LayoutParams(
													ViewGroup.LayoutParams.WRAP_CONTENT,
													hight_display);
											ln_my_feed_services1.setLayoutParams(rep);
											ln_my_feed_services1.setEnabled(true);
											moreClick(list_not_save,
													my_feed_services_count1);
											my_feed_services_count1++;
										}
									});
						}
						/*
						 * My feed services 2
						 */
						if (PhimpMe.phimpme_array_list.get(i).get(0).getService()
								.equals("my_feed_services2")) {
							Button btn_line = new Button(ctx);
							btn_line.setHeight(2);
							btn_line.setWidth(LayoutParams.MATCH_PARENT);
							btn_line.setBackgroundResource(color_line);

							txtMyFeedServices2 = new TextView(getContext());
							txtMyFeedServices2.setText("My Feed Photo");
							txtMyFeedServices2.setTextSize(text_size);
							gv_my_feed_services2 = new GridView(getContext());
							gv_my_feed_services2.setPadding(0, 10, 0, 0);
							ln_my_feed_services2 = new LinearLayout(getContext());
							btn_my_feed_services_more2 = new ImageButton(getContext());
							btn_my_feed_services_more2
									.setImageResource(R.drawable.more_disable);
							btn_my_feed_services_more2.setEnabled(false);

							RelativeLayout more_li = new RelativeLayout(getContext());
							btn_my_feed_services_more2.setLayoutParams(lp_more);
							more_li.addView(btn_my_feed_services_more2);
							more_li.addView(txtMyFeedServices2);
							ln_my_feed_services2.setOrientation(LinearLayout.VERTICAL);
							ln_my_feed_services2.addView(more_li);
							ln_my_feed_services2.addView(btn_line);
							ln_my_feed_services2.addView(gv_my_feed_services2);

							ln_my_feed_services2.setEnabled(false);
							linear_main.addView(ln_my_feed_services2);
							gv_my_feed_services2.setNumColumns(cols);
							gv_my_feed_services2.setDrawingCacheEnabled(true);
							my_feed_services_count2= 0;
							final ArrayList<RSSPhotoItem> list = PhimpMe.phimpme_array_list
									.get(i);
							final ArrayList<RSSPhotoItem> list_not_save = new ArrayList<RSSPhotoItem>();
							int count = 0;
							for (int j = 0; j < list.size(); j++) {
								String url = list.get(j).getURL();
								String filepath = rss_folder.getAbsolutePath()
										+ "/"
										+ url.toLowerCase().replace("://", "")
												.replace("/", "_");
								File f = new File(filepath);
								if (f.exists()) {
									try {
										InputStream is = new FileInputStream(f);

										BitmapFactory.Options bfOpt = new BitmapFactory.Options();

										bfOpt.inScaled = true;
										bfOpt.inSampleSize = 2;
										bfOpt.inPurgeable = true;

										Bitmap bmp = BitmapFactory.decodeStream(is,
												null, bfOpt);
										bmp = ImageUtil.scaleCenterCrop(bmp,
												newGallery.DEFAULT_THUMBNAIL_SIZE,
												newGallery.DEFAULT_THUMBNAIL_SIZE);
										bitmap_my_feed_services2.add(0,bmp);
										count++;
									} catch (Exception e) {
									}
								} else {
									list_not_save.add(list.get(j));
								}

							}
							if (list_not_save.size() > 0) {
								btn_my_feed_services_more2
										.setImageResource(R.drawable.more);
								btn_my_feed_services_more2.setEnabled(true);
							}
							if (count < 3) {
								hight_display = DEFAULT_THUMBNAIL_SIZE + 100;
							} else if (count % 3 != 0) {
								hight_display = DEFAULT_THUMBNAIL_SIZE
										* (count / 3 + 1) + 100;
							} else {
								hight_display = DEFAULT_THUMBNAIL_SIZE * (count / 3)
										+ 100;
							}

							LinearLayout.LayoutParams rep = new LinearLayout.LayoutParams(
									ViewGroup.LayoutParams.WRAP_CONTENT, hight_display);
							ln_my_feed_services2.setLayoutParams(rep);

							gv_my_feed_services2.setAdapter(my_feed_services_adapter2);
							btn_my_feed_services_more2
									.setOnClickListener(new OnClickListener() {
										@Override
										public void onClick(View v) {
											btn_my_feed_services_more2
													.setImageResource(R.drawable.more_disable);
											btn_my_feed_services_more2
													.setEnabled(false);
											if ((list_not_save.size() - my_feed_services_count2
													* NUMBER_PHOTO_NEED_DOWNLOAD) > 3) {
												hight_display += DEFAULT_THUMBNAIL_SIZE * 2;
											} else if ((list_not_save.size() - my_feed_services_count2
													* NUMBER_PHOTO_NEED_DOWNLOAD) > 0)
												hight_display += DEFAULT_THUMBNAIL_SIZE;

											LinearLayout.LayoutParams rep = new LinearLayout.LayoutParams(
													ViewGroup.LayoutParams.WRAP_CONTENT,
													hight_display);
											ln_my_feed_services2.setLayoutParams(rep);
											ln_my_feed_services2.setEnabled(true);
											moreClick(list_not_save,
													my_feed_services_count2);
											my_feed_services_count2++;
										}
									});
						}
						/*
						 * My feed services 3
						 */
						if (PhimpMe.phimpme_array_list.get(i).get(0).getService()
								.equals("my_feed_services3")) {
							Button btn_line = new Button(ctx);
							btn_line.setHeight(2);
							btn_line.setWidth(LayoutParams.MATCH_PARENT);
							btn_line.setBackgroundResource(color_line);

							txtMyFeedServices3 = new TextView(getContext());
							txtMyFeedServices3.setText("My Feed Photo");
							txtMyFeedServices3.setTextSize(text_size);
							gv_my_feed_services3 = new GridView(getContext());
							gv_my_feed_services3.setPadding(0, 10, 0, 0);
							ln_my_feed_services3 = new LinearLayout(getContext());
							btn_my_feed_services_more3 = new ImageButton(getContext());
							btn_my_feed_services_more3
									.setImageResource(R.drawable.more_disable);
							btn_my_feed_services_more3.setEnabled(false);

							RelativeLayout more_li = new RelativeLayout(getContext());
							btn_my_feed_services_more3.setLayoutParams(lp_more);
							more_li.addView(btn_my_feed_services_more3);
							more_li.addView(txtMyFeedServices3);
							ln_my_feed_services3.setOrientation(LinearLayout.VERTICAL);
							ln_my_feed_services3.addView(more_li);
							ln_my_feed_services3.addView(btn_line);
							ln_my_feed_services3.addView(gv_my_feed_services3);

							ln_my_feed_services3.setEnabled(false);
							linear_main.addView(ln_my_feed_services3);
							gv_my_feed_services3.setNumColumns(cols);
							gv_my_feed_services3.setDrawingCacheEnabled(true);
							my_feed_services_count3= 0;
							final ArrayList<RSSPhotoItem> list = PhimpMe.phimpme_array_list
									.get(i);
							final ArrayList<RSSPhotoItem> list_not_save = new ArrayList<RSSPhotoItem>();
							int count = 0;
							for (int j = 0; j < list.size(); j++) {
								String url = list.get(j).getURL();
								String filepath = rss_folder.getAbsolutePath()
										+ "/"
										+ url.toLowerCase().replace("://", "")
												.replace("/", "_");
								File f = new File(filepath);
								if (f.exists()) {
									try {
										InputStream is = new FileInputStream(f);

										BitmapFactory.Options bfOpt = new BitmapFactory.Options();

										bfOpt.inScaled = true;
										bfOpt.inSampleSize = 2;
										bfOpt.inPurgeable = true;

										Bitmap bmp = BitmapFactory.decodeStream(is,
												null, bfOpt);
										bmp = ImageUtil.scaleCenterCrop(bmp,
												newGallery.DEFAULT_THUMBNAIL_SIZE,
												newGallery.DEFAULT_THUMBNAIL_SIZE);
										bitmap_my_feed_services3.add(0,bmp);
										count++;
									} catch (Exception e) {
									}
								} else {
									list_not_save.add(list.get(j));
								}

							}
							if (list_not_save.size() > 0) {
								btn_my_feed_services_more3
										.setImageResource(R.drawable.more);
								btn_my_feed_services_more3.setEnabled(true);
							}
							if (count < 3) {
								hight_display = DEFAULT_THUMBNAIL_SIZE + 100;
							} else if (count % 3 != 0) {
								hight_display = DEFAULT_THUMBNAIL_SIZE
										* (count / 3 + 1) + 100;
							} else {
								hight_display = DEFAULT_THUMBNAIL_SIZE * (count / 3)
										+ 100;
							}

							LinearLayout.LayoutParams rep = new LinearLayout.LayoutParams(
									ViewGroup.LayoutParams.WRAP_CONTENT, hight_display);
							ln_my_feed_services3.setLayoutParams(rep);

							gv_my_feed_services3.setAdapter(my_feed_services_adapter3);
							btn_my_feed_services_more3
									.setOnClickListener(new OnClickListener() {
										@Override
										public void onClick(View v) {
											btn_my_feed_services_more3
													.setImageResource(R.drawable.more_disable);
											btn_my_feed_services_more3
													.setEnabled(false);
											if ((list_not_save.size() - my_feed_services_count3
													* NUMBER_PHOTO_NEED_DOWNLOAD) > 3) {
												hight_display += DEFAULT_THUMBNAIL_SIZE * 2;
											} else if ((list_not_save.size() - my_feed_services_count3
													* NUMBER_PHOTO_NEED_DOWNLOAD) > 0)
												hight_display += DEFAULT_THUMBNAIL_SIZE;

											LinearLayout.LayoutParams rep = new LinearLayout.LayoutParams(
													ViewGroup.LayoutParams.WRAP_CONTENT,
													hight_display);
											ln_my_feed_services3.setLayoutParams(rep);
											ln_my_feed_services3.setEnabled(true);
											moreClick(list_not_save,
													my_feed_services_count3);
											my_feed_services_count3++;
										}
									});
						}
						/*
						 * My feed services 4
						 */
						if (PhimpMe.phimpme_array_list.get(i).get(0).getService()
								.equals("my_feed_services4")) {
							Button btn_line = new Button(ctx);
							btn_line.setHeight(2);
							btn_line.setWidth(LayoutParams.MATCH_PARENT);
							btn_line.setBackgroundResource(color_line);

							txtMyFeedServices4 = new TextView(getContext());
							txtMyFeedServices4.setText("My Feed Photo");
							txtMyFeedServices4.setTextSize(text_size);
							gv_my_feed_services4 = new GridView(getContext());
							gv_my_feed_services4.setPadding(0, 10, 0, 0);
							ln_my_feed_services4 = new LinearLayout(getContext());
							btn_my_feed_services_more4 = new ImageButton(getContext());
							btn_my_feed_services_more4
									.setImageResource(R.drawable.more_disable);
							btn_my_feed_services_more4.setEnabled(false);

							RelativeLayout more_li = new RelativeLayout(getContext());
							btn_my_feed_services_more4.setLayoutParams(lp_more);
							more_li.addView(btn_my_feed_services_more4);
							more_li.addView(txtMyFeedServices4);
							ln_my_feed_services4.setOrientation(LinearLayout.VERTICAL);
							ln_my_feed_services4.addView(more_li);
							ln_my_feed_services4.addView(btn_line);
							ln_my_feed_services4.addView(gv_my_feed_services4);

							ln_my_feed_services4.setEnabled(false);

							linear_main.addView(ln_my_feed_services4);
							gv_my_feed_services4.setNumColumns(cols);
							gv_my_feed_services4.setDrawingCacheEnabled(true);
							my_feed_services_count4= 0;
							final ArrayList<RSSPhotoItem> list = PhimpMe.phimpme_array_list
									.get(i);
							final ArrayList<RSSPhotoItem> list_not_save = new ArrayList<RSSPhotoItem>();
							int count = 0;
							for (int j = 0; j < list.size(); j++) {
								String url = list.get(j).getURL();
								String filepath = rss_folder.getAbsolutePath()
										+ "/"
										+ url.toLowerCase().replace("://", "")
												.replace("/", "_");
								File f = new File(filepath);
								if (f.exists()) {
									try {
										InputStream is = new FileInputStream(f);

										BitmapFactory.Options bfOpt = new BitmapFactory.Options();

										bfOpt.inScaled = true;
										bfOpt.inSampleSize = 2;
										bfOpt.inPurgeable = true;

										Bitmap bmp = BitmapFactory.decodeStream(is,
												null, bfOpt);
										bmp = ImageUtil.scaleCenterCrop(bmp,
												newGallery.DEFAULT_THUMBNAIL_SIZE,
												newGallery.DEFAULT_THUMBNAIL_SIZE);
										bitmap_my_feed_services4.add(0,bmp);
										count++;
									} catch (Exception e) {
									}
								} else {
									list_not_save.add(list.get(j));
								}

							}
							if (list_not_save.size() > 0) {
								btn_my_feed_services_more4
										.setImageResource(R.drawable.more);
								btn_my_feed_services_more4.setEnabled(true);
							}
							if (count < 3) {
								hight_display = DEFAULT_THUMBNAIL_SIZE + 100;
							} else if (count % 3 != 0) {
								hight_display = DEFAULT_THUMBNAIL_SIZE
										* (count / 3 + 1) + 100;
							} else {
								hight_display = DEFAULT_THUMBNAIL_SIZE * (count / 3)
										+ 100;
							}

							LinearLayout.LayoutParams rep = new LinearLayout.LayoutParams(
									ViewGroup.LayoutParams.WRAP_CONTENT, hight_display);
							ln_my_feed_services4.setLayoutParams(rep);

							gv_my_feed_services4.setAdapter(my_feed_services_adapter4);
							btn_my_feed_services_more4
									.setOnClickListener(new OnClickListener() {
										@Override
										public void onClick(View v) {
											btn_my_feed_services_more4
													.setImageResource(R.drawable.more_disable);
											btn_my_feed_services_more4
													.setEnabled(false);
											if ((list_not_save.size() - my_feed_services_count4
													* NUMBER_PHOTO_NEED_DOWNLOAD) > 3) {
												hight_display += DEFAULT_THUMBNAIL_SIZE * 2;
											} else if ((list_not_save.size() - my_feed_services_count4
													* NUMBER_PHOTO_NEED_DOWNLOAD) > 0)
												hight_display += DEFAULT_THUMBNAIL_SIZE;

											LinearLayout.LayoutParams rep = new LinearLayout.LayoutParams(
													ViewGroup.LayoutParams.WRAP_CONTENT,
													hight_display);
											ln_my_feed_services4.setLayoutParams(rep);
											ln_my_feed_services4.setEnabled(true);
											moreClick(list_not_save,
													my_feed_services_count4);
											my_feed_services_count4++;
										}
									});
						}
						/*
						 * My feed services 5
						 */
						if (PhimpMe.phimpme_array_list.get(i).get(0).getService()
								.equals("my_feed_services5")) {
							Button btn_line = new Button(ctx);
							btn_line.setHeight(2);
							btn_line.setWidth(LayoutParams.MATCH_PARENT);
							btn_line.setBackgroundResource(color_line);

							txtMyFeedServices5 = new TextView(getContext());
							txtMyFeedServices5.setText("My Feed Photo");
							txtMyFeedServices5.setTextSize(text_size);
							gv_my_feed_services5 = new GridView(getContext());
							gv_my_feed_services5.setPadding(0, 10, 0, 0);
							ln_my_feed_services5 = new LinearLayout(getContext());
							btn_my_feed_services_more5 = new ImageButton(getContext());
							btn_my_feed_services_more5
									.setImageResource(R.drawable.more_disable);
							btn_my_feed_services_more5.setEnabled(false);

							RelativeLayout more_li = new RelativeLayout(getContext());
							btn_my_feed_services_more5.setLayoutParams(lp_more);
							more_li.addView(btn_my_feed_services_more5);
							more_li.addView(txtMyFeedServices5);
							ln_my_feed_services5.setOrientation(LinearLayout.VERTICAL);
							ln_my_feed_services5.addView(more_li);
							ln_my_feed_services5.addView(btn_line);
							ln_my_feed_services5.addView(gv_my_feed_services5);

							ln_my_feed_services5.setEnabled(false);
							linear_main.addView(ln_my_feed_services5);
							gv_my_feed_services5.setNumColumns(cols);
							gv_my_feed_services5.setDrawingCacheEnabled(true);
							my_feed_services_count5= 0;
							final ArrayList<RSSPhotoItem> list = PhimpMe.phimpme_array_list
									.get(i);
							final ArrayList<RSSPhotoItem> list_not_save = new ArrayList<RSSPhotoItem>();
							int count = 0;
							for (int j = 0; j < list.size(); j++) {
								String url = list.get(j).getURL();
								String filepath = rss_folder.getAbsolutePath()
										+ "/"
										+ url.toLowerCase().replace("://", "")
												.replace("/", "_");
								File f = new File(filepath);
								if (f.exists()) {
									try {
										InputStream is = new FileInputStream(f);

										BitmapFactory.Options bfOpt = new BitmapFactory.Options();

										bfOpt.inScaled = true;
										bfOpt.inSampleSize = 2;
										bfOpt.inPurgeable = true;

										Bitmap bmp = BitmapFactory.decodeStream(is,
												null, bfOpt);
										bmp = ImageUtil.scaleCenterCrop(bmp,
												newGallery.DEFAULT_THUMBNAIL_SIZE,
												newGallery.DEFAULT_THUMBNAIL_SIZE);
										bitmap_my_feed_services5.add(0,bmp);
										count++;
									} catch (Exception e) {
									}
								} else {
									list_not_save.add(list.get(j));
								}

							}
							if (list_not_save.size() > 0) {
								btn_my_feed_services_more5
										.setImageResource(R.drawable.more);
								btn_my_feed_services_more5.setEnabled(true);
							}
							if (count < 3) {
								hight_display = DEFAULT_THUMBNAIL_SIZE + 100;
							} else if (count % 3 != 0) {
								hight_display = DEFAULT_THUMBNAIL_SIZE
										* (count / 3 + 1) + 100;
							} else {
								hight_display = DEFAULT_THUMBNAIL_SIZE * (count / 3)
										+ 100;
							}

							LinearLayout.LayoutParams rep = new LinearLayout.LayoutParams(
									ViewGroup.LayoutParams.WRAP_CONTENT, hight_display);
							ln_my_feed_services5.setLayoutParams(rep);

							gv_my_feed_services5.setAdapter(my_feed_services_adapter5);
							btn_my_feed_services_more5
									.setOnClickListener(new OnClickListener() {
										@Override
										public void onClick(View v) {
											btn_my_feed_services_more5
													.setImageResource(R.drawable.more_disable);
											btn_my_feed_services_more5
													.setEnabled(false);
											if ((list_not_save.size() - my_feed_services_count5
													* NUMBER_PHOTO_NEED_DOWNLOAD) > 3) {
												hight_display += DEFAULT_THUMBNAIL_SIZE * 2;
											} else if ((list_not_save.size() - my_feed_services_count5
													* NUMBER_PHOTO_NEED_DOWNLOAD) > 0)
												hight_display += DEFAULT_THUMBNAIL_SIZE;

											LinearLayout.LayoutParams rep = new LinearLayout.LayoutParams(
													ViewGroup.LayoutParams.WRAP_CONTENT,
													hight_display);
											ln_my_feed_services5.setLayoutParams(rep);
											ln_my_feed_services5.setEnabled(true);
											moreClick(list_not_save,
													my_feed_services_count5);
											my_feed_services_count5++;
										}
									});
						}
					}
				}
				/*
				 * Personal
				 */

				for (int i = 0; i < PhimpMe.phimpme_personal_array_list.size(); i++) {
					if (PhimpMe.phimpme_personal_array_list.get(i).size() == 0)
						continue;
					else {
						// Facebook personal
						if (PhimpMe.phimpme_personal_array_list.get(i).get(0)
								.getService().equals("personal_facebook")) {
							Button btn_line = new Button(ctx);
							btn_line.setHeight(2);
							btn_line.setWidth(LayoutParams.MATCH_PARENT);
							btn_line.setBackgroundResource(color_line);

							txtfacebook = new TextView(getContext());
							txtfacebook.setText("Personal Facebook");
							txtfacebook.setTextSize(text_size);
							gv_personal_facebook = new GridView(getContext());
							gv_personal_facebook.setPadding(0, 10, 0, 0);
							ln_facebook = new LinearLayout(getContext());
							btn_facebook_more = new ImageButton(getContext());
							btn_facebook_more.setImageResource(R.drawable.more_disable);
							btn_facebook_more.setEnabled(false);

							RelativeLayout more_li = new RelativeLayout(getContext());
							btn_facebook_more.setLayoutParams(lp_more);
							more_li.addView(btn_facebook_more);
							more_li.addView(txtfacebook);
							ln_facebook.setOrientation(LinearLayout.VERTICAL);
							ln_facebook.addView(more_li);
							ln_facebook.addView(btn_line);
							ln_facebook.addView(gv_personal_facebook);

							ln_facebook.setEnabled(false);
							linear_main.addView(ln_facebook);
							gv_personal_facebook.setNumColumns(cols);
							gv_personal_facebook.setDrawingCacheEnabled(true);
							personal_facebook_count = 0;
							final ArrayList<RSSPhotoItem_Personal> list = PhimpMe.phimpme_personal_array_list
									.get(i);
							final ArrayList<RSSPhotoItem_Personal> list_not_save = new ArrayList<RSSPhotoItem_Personal>();
							int count = 0;
							for (int j = 0; j < list.size(); j++) {
								String url = list.get(j).getURL();
								String filepath = rss_folder.getAbsolutePath()
										+ "/"
										+ url.toLowerCase().replace("://", "")
												.replace("/", "_");
								File f = new File(filepath);
								if (f.exists()) {
									try {
										InputStream is = new FileInputStream(f);

										BitmapFactory.Options bfOpt = new BitmapFactory.Options();

										bfOpt.inScaled = true;
										bfOpt.inSampleSize = 2;
										bfOpt.inPurgeable = true;

										Bitmap bmp = BitmapFactory.decodeStream(is,
												null, bfOpt);
										bmp = ImageUtil.scaleCenterCrop(bmp,
												newGallery.DEFAULT_THUMBNAIL_SIZE,
												newGallery.DEFAULT_THUMBNAIL_SIZE);
										bitmap_personal_facebook.add(0,bmp);
										count++;
									} catch (Exception e) {
									}
								} else {
									list_not_save.add(list.get(j));
								}

							}
							if (list_not_save.size() > 0) {
								btn_facebook_more.setImageResource(R.drawable.more);
								btn_facebook_more.setEnabled(true);
							}
							if (count < 3) {
								hight_display = DEFAULT_THUMBNAIL_SIZE + 100;
							} else if (count % 3 != 0) {
								hight_display = DEFAULT_THUMBNAIL_SIZE
										* (count / 3 + 1) + 100;
							} else {
								hight_display = DEFAULT_THUMBNAIL_SIZE * (count / 3)
										+ 100;
							}

							LinearLayout.LayoutParams rep = new LinearLayout.LayoutParams(
									ViewGroup.LayoutParams.WRAP_CONTENT, hight_display);
							ln_facebook.setLayoutParams(rep);

							gv_personal_facebook.setAdapter(facebookadapter);
							btn_facebook_more.setOnClickListener(new OnClickListener() {
								@Override
								public void onClick(View v) {
									btn_facebook_more
											.setImageResource(R.drawable.more_disable);
									btn_facebook_more.setEnabled(false);
									if ((list_not_save.size() - personal_facebook_count
											* NUMBER_PHOTO_NEED_DOWNLOAD) > 3) {
										hight_display += DEFAULT_THUMBNAIL_SIZE * 2;
									} else if ((list_not_save.size() - personal_facebook_count
											* NUMBER_PHOTO_NEED_DOWNLOAD) > 0)
										hight_display += DEFAULT_THUMBNAIL_SIZE;

									LinearLayout.LayoutParams rep = new LinearLayout.LayoutParams(
											ViewGroup.LayoutParams.WRAP_CONTENT,
											hight_display);
									ln_facebook.setLayoutParams(rep);
									ln_facebook.setEnabled(true);
									personal_moreClick(list_not_save,
											personal_facebook_count);
									personal_facebook_count++;
								}
							});
						}
						// Personal Tumblr
						if (PhimpMe.phimpme_personal_array_list.get(i).get(0)
								.getService().equals("personal_tumblr")) {
							Button btn_line = new Button(ctx);
							btn_line.setHeight(2);
							btn_line.setWidth(LayoutParams.MATCH_PARENT);
							btn_line.setBackgroundResource(color_line);

							txttumblr = new TextView(getContext());
							txttumblr.setText("Tumblr Personal");
							txttumblr.setTextSize(text_size);
							gv_personal_tumblr = new GridView(getContext());
							gv_personal_tumblr.setPadding(0, 10, 0, 0);
							ln_tumblr = new LinearLayout(getContext());
							btn_tumblr_more = new ImageButton(getContext());
							btn_tumblr_more.setImageResource(R.drawable.more_disable);
							btn_tumblr_more.setEnabled(false);

							RelativeLayout more_li = new RelativeLayout(getContext());
							btn_tumblr_more.setLayoutParams(lp_more);
							more_li.addView(btn_tumblr_more);
							more_li.addView(txttumblr);
							ln_tumblr.setOrientation(LinearLayout.VERTICAL);
							ln_tumblr.addView(more_li);
							ln_tumblr.addView(btn_line);
							ln_tumblr.addView(gv_personal_tumblr);

							ln_tumblr.setEnabled(false);
							linear_main.addView(ln_tumblr);
							gv_personal_tumblr.setNumColumns(cols);
							gv_personal_tumblr.setDrawingCacheEnabled(true);
							personal_tumblr_count = 0;
							final ArrayList<RSSPhotoItem_Personal> list = PhimpMe.phimpme_personal_array_list
									.get(i);
							final ArrayList<RSSPhotoItem_Personal> list_not_save = new ArrayList<RSSPhotoItem_Personal>();
							int count = 0;
							for (int j = 0; j < list.size(); j++) {
								String url = list.get(j).getURL();
								String filepath = rss_folder.getAbsolutePath()
										+ "/"
										+ url.toLowerCase().replace("://", "")
												.replace("/", "_");
								File f = new File(filepath);
								if (f.exists()) {
									try {
										InputStream is = new FileInputStream(f);

										BitmapFactory.Options bfOpt = new BitmapFactory.Options();

										bfOpt.inScaled = true;
										bfOpt.inSampleSize = 2;
										bfOpt.inPurgeable = true;

										Bitmap bmp = BitmapFactory.decodeStream(is,
												null, bfOpt);
										bmp = ImageUtil.scaleCenterCrop(bmp,
												newGallery.DEFAULT_THUMBNAIL_SIZE,
												newGallery.DEFAULT_THUMBNAIL_SIZE);
										bitmap_personal_tumblr.add(0,bmp);
										count++;
									} catch (Exception e) {
									}
								} else {
									list_not_save.add(list.get(j));
								}

							}
							if (list_not_save.size() > 0) {
								btn_tumblr_more.setImageResource(R.drawable.more);
								btn_tumblr_more.setEnabled(true);
							}
							if (count < 3) {
								hight_display = DEFAULT_THUMBNAIL_SIZE + 100;
							} else if (count % 3 != 0) {
								hight_display = DEFAULT_THUMBNAIL_SIZE
										* (count / 3 + 1) + 100;
							} else {
								hight_display = DEFAULT_THUMBNAIL_SIZE * (count / 3)
										+ 100;
							}

							LinearLayout.LayoutParams rep = new LinearLayout.LayoutParams(
									ViewGroup.LayoutParams.WRAP_CONTENT, hight_display);
							ln_tumblr.setLayoutParams(rep);

							gv_personal_tumblr.setAdapter(tumblradapter);
							btn_tumblr_more.setOnClickListener(new OnClickListener() {
								@Override
								public void onClick(View v) {
									btn_tumblr_more
											.setImageResource(R.drawable.more_disable);
									btn_tumblr_more.setEnabled(false);
									if ((list_not_save.size() - personal_tumblr_count
											* NUMBER_PHOTO_NEED_DOWNLOAD) > 3) {
										hight_display += DEFAULT_THUMBNAIL_SIZE * 2;
									} else if ((list_not_save.size() - personal_tumblr_count
											* NUMBER_PHOTO_NEED_DOWNLOAD) > 0)
										hight_display += DEFAULT_THUMBNAIL_SIZE;

									LinearLayout.LayoutParams rep = new LinearLayout.LayoutParams(
											ViewGroup.LayoutParams.WRAP_CONTENT,
											hight_display);
									ln_tumblr.setLayoutParams(rep);
									ln_tumblr.setEnabled(true);
									personal_moreClick(list_not_save,
											personal_tumblr_count);
									personal_tumblr_count++;
								}
							});
						}
						// VK personal
						if (PhimpMe.phimpme_personal_array_list.get(i).get(0)
								.getService().equals("personal_vkontakte")) {
							Button btn_line = new Button(ctx);
							btn_line.setHeight(2);
							btn_line.setWidth(LayoutParams.MATCH_PARENT);
							btn_line.setBackgroundResource(color_line);

							txtvkontakte = new TextView(getContext());
							txtvkontakte.setText("Vkontakte Personal");
							txtvkontakte.setTextSize(text_size);
							gv_personal_vkontakte = new GridView(getContext());
							gv_personal_vkontakte.setPadding(0, 10, 0, 0);
							ln_vkontakte = new LinearLayout(getContext());
							btn_vkontakte_more = new ImageButton(getContext());
							btn_vkontakte_more
									.setImageResource(R.drawable.more_disable);
							btn_vkontakte_more.setEnabled(false);

							RelativeLayout more_li = new RelativeLayout(getContext());
							btn_vkontakte_more.setLayoutParams(lp_more);
							more_li.addView(btn_vkontakte_more);
							more_li.addView(txtvkontakte);
							ln_vkontakte.setOrientation(LinearLayout.VERTICAL);
							ln_vkontakte.addView(more_li);
							ln_vkontakte.addView(btn_line);
							ln_vkontakte.addView(gv_personal_vkontakte);

							ln_vkontakte.setEnabled(false);
							linear_main.addView(ln_vkontakte);
							gv_personal_vkontakte.setNumColumns(cols);
							gv_personal_vkontakte.setDrawingCacheEnabled(true);
							personal_vkontakte_count = 0;
							final ArrayList<RSSPhotoItem_Personal> list = PhimpMe.phimpme_personal_array_list
									.get(i);
							final ArrayList<RSSPhotoItem_Personal> list_not_save = new ArrayList<RSSPhotoItem_Personal>();
							int count = 0;
							for (int j = 0; j < list.size(); j++) {
								String url = list.get(j).getURL();
								String filepath = rss_folder.getAbsolutePath()
										+ "/"
										+ url.toLowerCase().replace("://", "")
												.replace("/", "_");
								File f = new File(filepath);
								if (f.exists()) {
									try {
										InputStream is = new FileInputStream(f);

										BitmapFactory.Options bfOpt = new BitmapFactory.Options();

										bfOpt.inScaled = true;
										bfOpt.inSampleSize = 2;
										bfOpt.inPurgeable = true;

										Bitmap bmp = BitmapFactory.decodeStream(is,
												null, bfOpt);
										bmp = ImageUtil.scaleCenterCrop(bmp,
												newGallery.DEFAULT_THUMBNAIL_SIZE,
												newGallery.DEFAULT_THUMBNAIL_SIZE);
										bitmap_personal_vkontakte.add(0,bmp);
										count++;
									} catch (Exception e) {
									}
								} else {
									list_not_save.add(list.get(j));
								}

							}
							if (list_not_save.size() > 0) {
								btn_vkontakte_more.setImageResource(R.drawable.more);
								btn_vkontakte_more.setEnabled(true);
							}
							if (count < 3) {
								hight_display = DEFAULT_THUMBNAIL_SIZE + 100;
							} else if (count % 3 != 0) {
								hight_display = DEFAULT_THUMBNAIL_SIZE
										* (count / 3 + 1) + 100;
							} else {
								hight_display = DEFAULT_THUMBNAIL_SIZE * (count / 3)
										+ 100;
							}

							LinearLayout.LayoutParams rep = new LinearLayout.LayoutParams(
									ViewGroup.LayoutParams.WRAP_CONTENT, hight_display);
							ln_vkontakte.setLayoutParams(rep);

							gv_personal_vkontakte.setAdapter(vkontakteadapter);
							btn_vkontakte_more
									.setOnClickListener(new OnClickListener() {
										@Override
										public void onClick(View v) {
											btn_vkontakte_more
													.setImageResource(R.drawable.more_disable);
											btn_vkontakte_more.setEnabled(false);
											if ((list_not_save.size() - personal_vkontakte_count
													* NUMBER_PHOTO_NEED_DOWNLOAD) > 3) {
												hight_display += DEFAULT_THUMBNAIL_SIZE * 2;
											} else if ((list_not_save.size() - personal_vkontakte_count
													* NUMBER_PHOTO_NEED_DOWNLOAD) > 0)
												hight_display += DEFAULT_THUMBNAIL_SIZE;

											LinearLayout.LayoutParams rep = new LinearLayout.LayoutParams(
													ViewGroup.LayoutParams.WRAP_CONTENT,
													hight_display);
											ln_vkontakte.setLayoutParams(rep);
											ln_vkontakte.setEnabled(true);
											personal_moreClick(list_not_save,
													personal_vkontakte_count);
											personal_vkontakte_count++;
										}
									});
						}
						// Flickr personal
						if (PhimpMe.phimpme_personal_array_list.get(i).get(0)
								.getService().equals("personal_flickr")) {
							Button btn_line = new Button(ctx);
							btn_line.setHeight(2);
							btn_line.setWidth(LayoutParams.MATCH_PARENT);
							btn_line.setBackgroundResource(color_line);

							txtpersonal_flickr = new TextView(getContext());
							txtpersonal_flickr.setText("Personal Flickr");
							txtpersonal_flickr.setTextSize(text_size);
							gv_personal_flickr = new GridView(getContext());
							gv_personal_flickr.setPadding(0, 10, 0, 0);
							ln_personal_flickr = new LinearLayout(getContext());
							btn_personal_flickr_more = new ImageButton(getContext());
							btn_personal_flickr_more
									.setImageResource(R.drawable.more_disable);
							btn_personal_flickr_more.setEnabled(false);

							RelativeLayout more_li = new RelativeLayout(getContext());
							btn_personal_flickr_more.setLayoutParams(lp_more);
							more_li.addView(btn_personal_flickr_more);
							more_li.addView(txtpersonal_flickr);
							ln_personal_flickr.setOrientation(LinearLayout.VERTICAL);
							ln_personal_flickr.addView(more_li);
							ln_personal_flickr.addView(btn_line);
							ln_personal_flickr.addView(gv_personal_flickr);

							ln_personal_flickr.setEnabled(false);
							linear_main.addView(ln_personal_flickr);
							gv_personal_flickr.setNumColumns(cols);
							gv_personal_flickr.setDrawingCacheEnabled(true);
							personal_flickr_count = 0;
							final ArrayList<RSSPhotoItem_Personal> list = PhimpMe.phimpme_personal_array_list
									.get(i);
							final ArrayList<RSSPhotoItem_Personal> list_not_save = new ArrayList<RSSPhotoItem_Personal>();
							int count = 0;
							for (int j = 0; j < list.size(); j++) {
								String url = list.get(j).getURL();
								String filepath = rss_folder.getAbsolutePath()
										+ "/"
										+ url.toLowerCase().replace("://", "")
												.replace("/", "_");
								File f = new File(filepath);
								if (f.exists()) {
									try {
										InputStream is = new FileInputStream(f);

										BitmapFactory.Options bfOpt = new BitmapFactory.Options();

										bfOpt.inScaled = true;
										bfOpt.inSampleSize = 2;
										bfOpt.inPurgeable = true;

										Bitmap bmp = BitmapFactory.decodeStream(is,
												null, bfOpt);
										bmp = ImageUtil.scaleCenterCrop(bmp,
												newGallery.DEFAULT_THUMBNAIL_SIZE,
												newGallery.DEFAULT_THUMBNAIL_SIZE);
										bitmap_personal_flickr.add(0,bmp);
										count++;
									} catch (Exception e) {
									}
								} else {
									list_not_save.add(list.get(j));
								}

							}
							if (list_not_save.size() > 0) {
								btn_personal_flickr_more
										.setImageResource(R.drawable.more);
								btn_personal_flickr_more.setEnabled(true);
							}
							if (count < 3) {
								hight_display = DEFAULT_THUMBNAIL_SIZE + 100;
							} else if (count % 3 != 0) {
								hight_display = DEFAULT_THUMBNAIL_SIZE
										* (count / 3 + 1) + 100;
							} else {
								hight_display = DEFAULT_THUMBNAIL_SIZE * (count / 3)
										+ 100;
							}

							LinearLayout.LayoutParams rep = new LinearLayout.LayoutParams(
									ViewGroup.LayoutParams.WRAP_CONTENT, hight_display);
							ln_personal_flickr.setLayoutParams(rep);

							gv_personal_flickr.setAdapter(personal_flickradapter);
							btn_personal_flickr_more
									.setOnClickListener(new OnClickListener() {
										@Override
										public void onClick(View v) {
											btn_personal_flickr_more
													.setImageResource(R.drawable.more_disable);
											btn_personal_flickr_more.setEnabled(false);
											if ((list_not_save.size() - personal_flickr_count
													* NUMBER_PHOTO_NEED_DOWNLOAD) > 3) {
												hight_display += DEFAULT_THUMBNAIL_SIZE * 2;
											} else if ((list_not_save.size() - personal_flickr_count
													* NUMBER_PHOTO_NEED_DOWNLOAD) > 0)
												hight_display += DEFAULT_THUMBNAIL_SIZE;

											LinearLayout.LayoutParams rep = new LinearLayout.LayoutParams(
													ViewGroup.LayoutParams.WRAP_CONTENT,
													hight_display);
											ln_personal_flickr.setLayoutParams(rep);
											ln_personal_flickr.setEnabled(true);
											personal_moreClick(list_not_save,
													personal_flickr_count);
											personal_flickr_count++;
										}
									});
						}
						// Picasa personal
						if (PhimpMe.phimpme_personal_array_list.get(i).get(0)
								.getService().equals("personal_picasa")) {
							Button btn_line = new Button(ctx);
							btn_line.setHeight(2);
							btn_line.setWidth(LayoutParams.MATCH_PARENT);
							btn_line.setBackgroundResource(color_line);

							txtpersonal_picasa = new TextView(getContext());
							txtpersonal_picasa.setText("Personal Picasa");
							txtpersonal_picasa.setTextSize(text_size);
							gv_personal_picasa = new GridView(getContext());
							gv_personal_picasa.setPadding(0, 10, 0, 0);
							ln_personal_picasa = new LinearLayout(getContext());
							btn_personal_picasa_more = new ImageButton(getContext());
							btn_personal_picasa_more
									.setImageResource(R.drawable.more_disable);
							btn_personal_picasa_more.setEnabled(false);

							RelativeLayout more_li = new RelativeLayout(getContext());
							btn_personal_picasa_more.setLayoutParams(lp_more);
							more_li.addView(btn_personal_picasa_more);
							more_li.addView(txtpersonal_picasa);
							ln_personal_picasa.setOrientation(LinearLayout.VERTICAL);
							ln_personal_picasa.addView(more_li);
							ln_personal_picasa.addView(btn_line);
							ln_personal_picasa.addView(gv_personal_picasa);

							ln_personal_picasa.setEnabled(false);
							linear_main.addView(ln_personal_picasa);
							gv_personal_picasa.setNumColumns(cols);
							gv_personal_picasa.setDrawingCacheEnabled(true);
							personal_picasa_count = 0;
							final ArrayList<RSSPhotoItem_Personal> list = PhimpMe.phimpme_personal_array_list
									.get(i);
							final ArrayList<RSSPhotoItem_Personal> list_not_save = new ArrayList<RSSPhotoItem_Personal>();
							int count = 0;
							for (int j = 0; j < list.size(); j++) {
								String url = list.get(j).getURL();
								String filepath = rss_folder.getAbsolutePath()
										+ "/"
										+ url.toLowerCase().replace("://", "")
												.replace("/", "_");
								File f = new File(filepath);
								if (f.exists()) {
									try {
										InputStream is = new FileInputStream(f);

										BitmapFactory.Options bfOpt = new BitmapFactory.Options();

										bfOpt.inScaled = true;
										bfOpt.inSampleSize = 2;
										bfOpt.inPurgeable = true;

										Bitmap bmp = BitmapFactory.decodeStream(is,
												null, bfOpt);
										bmp = ImageUtil.scaleCenterCrop(bmp,
												newGallery.DEFAULT_THUMBNAIL_SIZE,
												newGallery.DEFAULT_THUMBNAIL_SIZE);
										bitmap_personal_picasa.add(0,bmp);
										count++;
									} catch (Exception e) {
									}
								} else {
									list_not_save.add(list.get(j));
								}

							}
							if (list_not_save.size() > 0) {
								btn_personal_picasa_more
										.setImageResource(R.drawable.more);
								btn_personal_picasa_more.setEnabled(true);
							}
							if (count < 3) {
								hight_display = DEFAULT_THUMBNAIL_SIZE + 100;
							} else if (count % 3 != 0) {
								hight_display = DEFAULT_THUMBNAIL_SIZE
										* (count / 3 + 1) + 100;
							} else {
								hight_display = DEFAULT_THUMBNAIL_SIZE * (count / 3)
										+ 100;
							}

							LinearLayout.LayoutParams rep = new LinearLayout.LayoutParams(
									ViewGroup.LayoutParams.WRAP_CONTENT, hight_display);
							ln_personal_picasa.setLayoutParams(rep);

							gv_personal_picasa.setAdapter(personal_picasaadapter);
							btn_personal_picasa_more
									.setOnClickListener(new OnClickListener() {
										@Override
										public void onClick(View v) {
											btn_personal_picasa_more
													.setImageResource(R.drawable.more_disable);
											btn_personal_picasa_more.setEnabled(false);
											if ((list_not_save.size() - personal_picasa_count
													* NUMBER_PHOTO_NEED_DOWNLOAD) > 3) {
												hight_display += DEFAULT_THUMBNAIL_SIZE * 2;
											} else if ((list_not_save.size() - personal_picasa_count
													* NUMBER_PHOTO_NEED_DOWNLOAD) > 0)
												hight_display += DEFAULT_THUMBNAIL_SIZE;

											LinearLayout.LayoutParams rep = new LinearLayout.LayoutParams(
													ViewGroup.LayoutParams.WRAP_CONTENT,
													hight_display);
											ln_personal_picasa.setLayoutParams(rep);
											ln_personal_picasa.setEnabled(true);
											personal_moreClick(list_not_save,
													personal_picasa_count);
											personal_picasa_count++;
										}
									});
						}
						// DeviantArt personal
						if (PhimpMe.phimpme_personal_array_list.get(i).get(0)
								.getService().equals("personal_deviantart")) {
							Button btn_line = new Button(ctx);
							btn_line.setHeight(2);
							btn_line.setWidth(LayoutParams.MATCH_PARENT);
							btn_line.setBackgroundResource(color_line);

							txtpersonal_deviantart = new TextView(getContext());
							txtpersonal_deviantart.setText("Personal DeviantArt");
							txtpersonal_deviantart.setTextSize(text_size);
							gv_personal_deviantart = new GridView(getContext());
							gv_personal_deviantart.setPadding(0, 10, 0, 0);
							ln_personal_deviantart = new LinearLayout(getContext());
							btn_personal_deviant_more = new ImageButton(getContext());
							btn_personal_deviant_more
									.setImageResource(R.drawable.more_disable);
							btn_personal_deviant_more.setEnabled(false);

							RelativeLayout more_li = new RelativeLayout(getContext());
							btn_personal_deviant_more.setLayoutParams(lp_more);
							more_li.addView(btn_personal_deviant_more);
							more_li.addView(txtpersonal_deviantart);
							ln_personal_deviantart
									.setOrientation(LinearLayout.VERTICAL);
							ln_personal_deviantart.addView(more_li);
							ln_personal_deviantart.addView(btn_line);
							ln_personal_deviantart.addView(gv_personal_deviantart);

							ln_personal_deviantart.setEnabled(false);
							linear_main.addView(ln_personal_deviantart);
							gv_personal_deviantart.setNumColumns(cols);
							gv_personal_deviantart.setDrawingCacheEnabled(true);
							personal_deviantart_count = 0;
							final ArrayList<RSSPhotoItem_Personal> list = PhimpMe.phimpme_personal_array_list
									.get(i);
							final ArrayList<RSSPhotoItem_Personal> list_not_save = new ArrayList<RSSPhotoItem_Personal>();
							int count = 0;
							for (int j = 0; j < list.size(); j++) {
								String url = list.get(j).getURL();
								String filepath = rss_folder.getAbsolutePath()
										+ "/"
										+ url.toLowerCase().replace("://", "")
												.replace("/", "_");
								File f = new File(filepath);
								if (f.exists()) {
									try {
										InputStream is = new FileInputStream(f);

										BitmapFactory.Options bfOpt = new BitmapFactory.Options();

										bfOpt.inScaled = true;
										bfOpt.inSampleSize = 2;
										bfOpt.inPurgeable = true;

										Bitmap bmp = BitmapFactory.decodeStream(is,
												null, bfOpt);
										bmp = ImageUtil.scaleCenterCrop(bmp,
												newGallery.DEFAULT_THUMBNAIL_SIZE,
												newGallery.DEFAULT_THUMBNAIL_SIZE);
										bitmap_personal_deviantart.add(0,bmp);
										count++;
									} catch (Exception e) {
									}
								} else {
									list_not_save.add(list.get(j));
								}

							}
							if (list_not_save.size() > 0) {
								btn_personal_deviant_more
										.setImageResource(R.drawable.more);
								btn_personal_deviant_more.setEnabled(true);
							}
							if (count < 3) {
								hight_display = DEFAULT_THUMBNAIL_SIZE + 100;
							} else if (count % 3 != 0) {
								hight_display = DEFAULT_THUMBNAIL_SIZE
										* (count / 3 + 1) + 100;
							} else {
								hight_display = DEFAULT_THUMBNAIL_SIZE * (count / 3)
										+ 100;
							}

							LinearLayout.LayoutParams rep = new LinearLayout.LayoutParams(
									ViewGroup.LayoutParams.WRAP_CONTENT, hight_display);
							ln_personal_deviantart.setLayoutParams(rep);

							gv_personal_deviantart
									.setAdapter(personal_deviantartadapter);
							btn_personal_deviant_more
									.setOnClickListener(new OnClickListener() {
										@Override
										public void onClick(View v) {
											btn_personal_deviant_more
													.setImageResource(R.drawable.more_disable);
											btn_personal_deviant_more.setEnabled(false);
											if ((list_not_save.size() - personal_deviantart_count
													* NUMBER_PHOTO_NEED_DOWNLOAD) > 3) {
												hight_display += DEFAULT_THUMBNAIL_SIZE * 2;
											} else if ((list_not_save.size() - personal_deviantart_count
													* NUMBER_PHOTO_NEED_DOWNLOAD) > 0)
												hight_display += DEFAULT_THUMBNAIL_SIZE;

											LinearLayout.LayoutParams rep = new LinearLayout.LayoutParams(
													ViewGroup.LayoutParams.WRAP_CONTENT,
													hight_display);
											ln_personal_deviantart.setLayoutParams(rep);
											ln_personal_deviantart.setEnabled(true);
											personal_moreClick(list_not_save,
													personal_deviantart_count);
											personal_deviantart_count++;
										}
									});
						}
						/*
						 * Imgur Personal
						 */
						if (PhimpMe.phimpme_personal_array_list.get(i).get(0)
								.getService().equals("personal_imgur")) {
							Button btn_line = new Button(ctx);
							btn_line.setHeight(2);
							btn_line.setWidth(LayoutParams.MATCH_PARENT);
							btn_line.setBackgroundResource(color_line);

							txtpersonal_imgur = new TextView(getContext());
							txtpersonal_imgur.setText("Personal Imgur");
							txtpersonal_imgur.setTextSize(text_size);
							gv_personal_imgur = new GridView(getContext());
							gv_personal_imgur.setPadding(0, 10, 0, 0);
							ln_personal_imgur = new LinearLayout(getContext());
							btn_personal_imgur_more = new ImageButton(getContext());
							btn_personal_imgur_more
									.setImageResource(R.drawable.more_disable);
							btn_personal_imgur_more.setEnabled(false);

							RelativeLayout more_li = new RelativeLayout(getContext());
							btn_personal_imgur_more.setLayoutParams(lp_more);
							more_li.addView(btn_personal_imgur_more);
							more_li.addView(txtpersonal_imgur);
							ln_personal_imgur.setOrientation(LinearLayout.VERTICAL);
							ln_personal_imgur.addView(more_li);
							ln_personal_imgur.addView(btn_line);
							ln_personal_imgur.addView(gv_personal_imgur);

							ln_personal_imgur.setEnabled(false);
							linear_main.addView(ln_personal_imgur);
							gv_personal_imgur.setNumColumns(cols);
							gv_personal_imgur.setDrawingCacheEnabled(true);
							personal_imgur_count = 0;
							final ArrayList<RSSPhotoItem_Personal> list = PhimpMe.phimpme_personal_array_list
									.get(i);
							final ArrayList<RSSPhotoItem_Personal> list_not_save = new ArrayList<RSSPhotoItem_Personal>();
							int count = 0;
							for (int j = 0; j < list.size(); j++) {
								String url = list.get(j).getURL();
								String filepath = rss_folder.getAbsolutePath()
										+ "/"
										+ url.toLowerCase().replace("://", "")
												.replace("/", "_");
								File f = new File(filepath);
								if (f.exists()) {
									try {
										InputStream is = new FileInputStream(f);

										BitmapFactory.Options bfOpt = new BitmapFactory.Options();

										bfOpt.inScaled = true;
										bfOpt.inSampleSize = 2;
										bfOpt.inPurgeable = true;

										Bitmap bmp = BitmapFactory.decodeStream(is,
												null, bfOpt);
										bmp = ImageUtil.scaleCenterCrop(bmp,
												newGallery.DEFAULT_THUMBNAIL_SIZE,
												newGallery.DEFAULT_THUMBNAIL_SIZE);
										bitmap_personal_imgur.add(0,bmp);
										count++;
									} catch (Exception e) {
									}
								} else {
									list_not_save.add(list.get(j));
								}

							}
							if (list_not_save.size() > 0) {
								btn_personal_imgur_more
										.setImageResource(R.drawable.more);
								btn_personal_imgur_more.setEnabled(true);
							}
							if (count < 3) {
								hight_display = DEFAULT_THUMBNAIL_SIZE + 100;
							} else if (count % 3 != 0) {
								hight_display = DEFAULT_THUMBNAIL_SIZE
										* (count / 3 + 1) + 100;
							} else {
								hight_display = DEFAULT_THUMBNAIL_SIZE * (count / 3)
										+ 100;
							}

							LinearLayout.LayoutParams rep = new LinearLayout.LayoutParams(
									ViewGroup.LayoutParams.WRAP_CONTENT, hight_display);
							ln_personal_imgur.setLayoutParams(rep);

							gv_personal_imgur.setAdapter(personal_imguradapter);
							btn_personal_imgur_more
									.setOnClickListener(new OnClickListener() {
										@Override
										public void onClick(View v) {
											btn_personal_imgur_more
													.setImageResource(R.drawable.more_disable);
											btn_personal_imgur_more.setEnabled(false);
											if ((list_not_save.size() - personal_imgur_count
													* NUMBER_PHOTO_NEED_DOWNLOAD) > 3) {
												hight_display += DEFAULT_THUMBNAIL_SIZE * 2;
											} else if ((list_not_save.size() - personal_imgur_count
													* NUMBER_PHOTO_NEED_DOWNLOAD) > 0)
												hight_display += DEFAULT_THUMBNAIL_SIZE;

											LinearLayout.LayoutParams rep = new LinearLayout.LayoutParams(
													ViewGroup.LayoutParams.WRAP_CONTENT,
													hight_display);
											ln_personal_imgur.setLayoutParams(rep);
											ln_personal_imgur.setEnabled(true);
											personal_moreClick(list_not_save,
													personal_imgur_count);
											personal_imgur_count++;
										}
									});
						}
						// Picasa personal
						if (PhimpMe.phimpme_personal_array_list.get(i).get(0)
								.getService().equals("personal_kaixin")) {
							Button btn_line = new Button(ctx);
							btn_line.setHeight(2);
							btn_line.setWidth(LayoutParams.MATCH_PARENT);
							btn_line.setBackgroundResource(color_line);

							txtpersonal_kaixin = new TextView(getContext());
							txtpersonal_kaixin.setText("Personal Kaixin");
							txtpersonal_kaixin.setTextSize(text_size);
							gv_personal_kaixin = new GridView(getContext());
							gv_personal_kaixin.setPadding(0, 10, 0, 0);
							ln_personal_kaixin = new LinearLayout(getContext());

							btn_personal_kaixin_more = new ImageButton(getContext());
							btn_personal_kaixin_more
									.setImageResource(R.drawable.more_disable);
							btn_personal_kaixin_more.setEnabled(false);

							RelativeLayout more_li = new RelativeLayout(getContext());
							btn_personal_kaixin_more.setLayoutParams(lp_more);
							more_li.addView(btn_personal_kaixin_more);
							more_li.addView(txtpersonal_kaixin);
							ln_personal_kaixin.setOrientation(LinearLayout.VERTICAL);
							ln_personal_kaixin.addView(more_li);
							ln_personal_kaixin.addView(btn_line);
							ln_personal_kaixin.addView(gv_personal_kaixin);

							ln_personal_kaixin.setEnabled(false);
							linear_main.addView(ln_personal_kaixin);
							gv_personal_kaixin.setNumColumns(cols);
							gv_personal_kaixin.setDrawingCacheEnabled(true);
							personal_kaixin_count = 0;
							final ArrayList<RSSPhotoItem_Personal> list = PhimpMe.phimpme_personal_array_list
									.get(i);
							final ArrayList<RSSPhotoItem_Personal> list_not_save = new ArrayList<RSSPhotoItem_Personal>();
							int count = 0;
							for (int j = 0; j < list.size(); j++) {
								String url = list.get(j).getURL();
								String filepath = rss_folder.getAbsolutePath()
										+ "/"
										+ url.toLowerCase().replace("://", "")
												.replace("/", "_");
								File f = new File(filepath);
								if (f.exists()) {
									try {
										InputStream is = new FileInputStream(f);

										BitmapFactory.Options bfOpt = new BitmapFactory.Options();

										bfOpt.inScaled = true;
										bfOpt.inSampleSize = 2;
										bfOpt.inPurgeable = true;

										Bitmap bmp = BitmapFactory.decodeStream(is,
												null, bfOpt);
										bmp = ImageUtil.scaleCenterCrop(bmp,
												newGallery.DEFAULT_THUMBNAIL_SIZE,
												newGallery.DEFAULT_THUMBNAIL_SIZE);
										bitmap_personal_kaixin.add(0,bmp);
										count++;
									} catch (Exception e) {
									}
								} else {
									list_not_save.add(list.get(j));
								}

							}
							if (list_not_save.size() > 0) {
								btn_personal_kaixin_more
										.setImageResource(R.drawable.more);
								btn_personal_kaixin_more.setEnabled(true);
							}
							if (count < 3) {
								hight_display = DEFAULT_THUMBNAIL_SIZE + 100;
							} else if (count % 3 != 0) {
								hight_display = DEFAULT_THUMBNAIL_SIZE
										* (count / 3 + 1) + 100;
							} else {
								hight_display = DEFAULT_THUMBNAIL_SIZE * (count / 3)
										+ 100;
							}

							LinearLayout.LayoutParams rep = new LinearLayout.LayoutParams(
									ViewGroup.LayoutParams.WRAP_CONTENT, hight_display);
							ln_personal_kaixin.setLayoutParams(rep);

							gv_personal_kaixin.setAdapter(personal_kaixinadapter);
							btn_personal_kaixin_more
									.setOnClickListener(new OnClickListener() {
										@Override
										public void onClick(View v) {
											btn_personal_kaixin_more
													.setImageResource(R.drawable.more_disable);
											btn_personal_kaixin_more.setEnabled(false);
											if ((list_not_save.size() - personal_kaixin_count
													* NUMBER_PHOTO_NEED_DOWNLOAD) > 3) {
												hight_display += DEFAULT_THUMBNAIL_SIZE * 2;
											} else if ((list_not_save.size() - personal_kaixin_count
													* NUMBER_PHOTO_NEED_DOWNLOAD) > 0)
												hight_display += DEFAULT_THUMBNAIL_SIZE;

											LinearLayout.LayoutParams rep = new LinearLayout.LayoutParams(
													ViewGroup.LayoutParams.WRAP_CONTENT,
													hight_display);
											ln_personal_kaixin.setLayoutParams(rep);
											ln_personal_kaixin.setEnabled(true);
											personal_moreClick(list_not_save,
													personal_kaixin_count);
											personal_kaixin_count++;
										}
									});
						}
						/*
						 * 500px Personal
						 */
						if (PhimpMe.phimpme_personal_array_list.get(i).get(0)
								.getService().equals("personal_500px")) {
							Button btn_line = new Button(ctx);
							btn_line.setHeight(2);
							btn_line.setWidth(LayoutParams.MATCH_PARENT);
							btn_line.setBackgroundResource(color_line);

							txtpersonal_500px = new TextView(getContext());
							txtpersonal_500px.setText("Personal 500px");
							txtpersonal_500px.setTextSize(text_size);
							gv_personal_500px = new GridView(getContext());
							gv_personal_500px.setPadding(0, 10, 0, 0);
							ln_personal_500px = new LinearLayout(getContext());
							btn_personal_500px_more = new ImageButton(getContext());
							btn_personal_500px_more
									.setImageResource(R.drawable.more_disable);
							btn_personal_500px_more.setEnabled(false);

							RelativeLayout more_li = new RelativeLayout(getContext());
							btn_personal_500px_more.setLayoutParams(lp_more);
							more_li.addView(btn_personal_500px_more);
							more_li.addView(txtpersonal_500px);
							ln_personal_500px.setOrientation(LinearLayout.VERTICAL);
							ln_personal_500px.addView(more_li);
							ln_personal_500px.addView(btn_line);
							ln_personal_500px.addView(gv_personal_500px);

							ln_personal_500px.setEnabled(false);
							linear_main.addView(ln_personal_500px);
							gv_personal_500px.setNumColumns(cols);
							gv_personal_500px.setDrawingCacheEnabled(true);
							personal_500px_count = 0;
							final ArrayList<RSSPhotoItem_Personal> list = PhimpMe.phimpme_personal_array_list
									.get(i);
							final ArrayList<RSSPhotoItem_Personal> list_not_save = new ArrayList<RSSPhotoItem_Personal>();
							int count = 0;
							for (int j = 0; j < list.size(); j++) {
								String url = list.get(j).getURL();
								String filepath = rss_folder.getAbsolutePath()
										+ "/"
										+ url.toLowerCase().replace("://", "")
												.replace("/", "_");
								File f = new File(filepath);
								if (f.exists()) {
									try {
										Bitmap bmp = BitmapFactory.decodeFile(filepath);
										bmp = ImageUtil.scaleCenterCrop(bmp,
												newGallery.DEFAULT_THUMBNAIL_SIZE,
												newGallery.DEFAULT_THUMBNAIL_SIZE);
										bitmap_personal_500px.add(0,bmp);
										count++;
									} catch (Exception e) {
									}
								} else {
									list_not_save.add(list.get(j));
								}

							}
							if (list_not_save.size() > 0) {
								btn_personal_500px_more
										.setImageResource(R.drawable.more);
								btn_personal_500px_more.setEnabled(true);
							}
							if (count < 3) {
								hight_display = DEFAULT_THUMBNAIL_SIZE + 100;
							} else if (count % 3 != 0) {
								hight_display = DEFAULT_THUMBNAIL_SIZE
										* (count / 3 + 1) + 100;
							} else {
								hight_display = DEFAULT_THUMBNAIL_SIZE * (count / 3)
										+ 100;
							}

							LinearLayout.LayoutParams rep = new LinearLayout.LayoutParams(
									ViewGroup.LayoutParams.WRAP_CONTENT, hight_display);
							ln_personal_500px.setLayoutParams(rep);

							gv_personal_500px.setAdapter(personal_500pxadapter);
							btn_personal_500px_more
									.setOnClickListener(new OnClickListener() {
										@Override
										public void onClick(View v) {
											btn_personal_500px_more
													.setImageResource(R.drawable.more_disable);
											btn_personal_500px_more.setEnabled(false);
											if ((list_not_save.size() - personal_500px_count
													* NUMBER_PHOTO_NEED_DOWNLOAD) > 3) {
												hight_display += DEFAULT_THUMBNAIL_SIZE * 2;
											} else if ((list_not_save.size() - personal_500px_count
													* NUMBER_PHOTO_NEED_DOWNLOAD) > 0)
												hight_display += DEFAULT_THUMBNAIL_SIZE;

											LinearLayout.LayoutParams rep = new LinearLayout.LayoutParams(
													ViewGroup.LayoutParams.WRAP_CONTENT,
													hight_display);
											ln_personal_500px.setLayoutParams(rep);
											ln_personal_500px.setEnabled(true);
											personal_moreClick(list_not_save,
													personal_500px_count);
											personal_500px_count++;
										}
									});
						}
						/*
						 * Sohu Personal
						 */
						if (PhimpMe.phimpme_personal_array_list.get(i).get(0)
								.getService().equals("personal_sohu")) {
							Button btn_line = new Button(ctx);
							btn_line.setHeight(2);
							btn_line.setWidth(LayoutParams.MATCH_PARENT);
							btn_line.setBackgroundResource(color_line);

							txtpersonal_sohu = new TextView(getContext());
							txtpersonal_sohu.setText("Personal Sohu");
							txtpersonal_sohu.setTextSize(text_size);
							gv_personal_sohu = new GridView(getContext());
							gv_personal_sohu.setPadding(0, 10, 0, 0);
							ln_personal_sohu = new LinearLayout(getContext());
							btn_personal_sohu_more = new ImageButton(getContext());
							btn_personal_sohu_more
									.setImageResource(R.drawable.more_disable);
							btn_personal_sohu_more.setEnabled(false);

							RelativeLayout more_li = new RelativeLayout(getContext());
							btn_personal_sohu_more.setLayoutParams(lp_more);
							more_li.addView(btn_personal_sohu_more);
							more_li.addView(txtpersonal_sohu);
							ln_personal_sohu.setOrientation(LinearLayout.VERTICAL);

							ln_personal_sohu.addView(more_li);
							ln_personal_sohu.addView(btn_line);
							ln_personal_sohu.addView(gv_personal_sohu);

							ln_personal_sohu.setEnabled(false);
							linear_main.addView(ln_personal_sohu);
							gv_personal_sohu.setNumColumns(cols);
							gv_personal_sohu.setDrawingCacheEnabled(true);
							personal_sohu_count = 0;
							final ArrayList<RSSPhotoItem_Personal> list = PhimpMe.phimpme_personal_array_list
									.get(i);
							final ArrayList<RSSPhotoItem_Personal> list_not_save = new ArrayList<RSSPhotoItem_Personal>();
							int count = 0;
							for (int j = 0; j < list.size(); j++) {
								String url = list.get(j).getURL();
								String filepath = rss_folder.getAbsolutePath()
										+ "/"
										+ url.toLowerCase().replace("://", "")
												.replace("/", "_");
								File f = new File(filepath);
								if (f.exists()) {
									try {

										InputStream is = new FileInputStream(f);

										BitmapFactory.Options bfOpt = new BitmapFactory.Options();

										bfOpt.inScaled = true;
										bfOpt.inSampleSize = 2;
										bfOpt.inPurgeable = true;

										Bitmap bmp = BitmapFactory.decodeStream(is,
												null, bfOpt);
										bmp = ImageUtil.scaleCenterCrop(bmp,
												newGallery.DEFAULT_THUMBNAIL_SIZE,
												newGallery.DEFAULT_THUMBNAIL_SIZE);
										bitmap_personal_500px.add(0,bmp);
										count++;
									} catch (Exception e) {
									}
								} else {
									list_not_save.add(list.get(j));
								}

							}
							if (list_not_save.size() > 0) {
								btn_personal_sohu_more
										.setImageResource(R.drawable.more);
								btn_personal_sohu_more.setEnabled(true);
							}
							if (count < 3) {
								hight_display = DEFAULT_THUMBNAIL_SIZE + 100;
							} else if (count % 3 != 0) {
								hight_display = DEFAULT_THUMBNAIL_SIZE
										* (count / 3 + 1) + 100;
							} else {
								hight_display = DEFAULT_THUMBNAIL_SIZE * (count / 3)
										+ 100;
							}

							LinearLayout.LayoutParams rep = new LinearLayout.LayoutParams(
									ViewGroup.LayoutParams.WRAP_CONTENT, hight_display);
							ln_personal_sohu.setLayoutParams(rep);

							gv_personal_sohu.setAdapter(personal_sohuadapter);
							btn_personal_sohu_more
									.setOnClickListener(new OnClickListener() {
										@Override
										public void onClick(View v) {
											btn_personal_sohu_more
													.setImageResource(R.drawable.more_disable);
											btn_personal_sohu_more.setEnabled(false);
											if ((list_not_save.size() - personal_sohu_count
													* NUMBER_PHOTO_NEED_DOWNLOAD) > 3) {
												hight_display += DEFAULT_THUMBNAIL_SIZE * 2;
											} else if ((list_not_save.size() - personal_sohu_count
													* NUMBER_PHOTO_NEED_DOWNLOAD) > 0)
												hight_display += DEFAULT_THUMBNAIL_SIZE;

											LinearLayout.LayoutParams rep = new LinearLayout.LayoutParams(
													ViewGroup.LayoutParams.WRAP_CONTENT,
													hight_display);
											ln_personal_sohu.setLayoutParams(rep);
											ln_personal_sohu.setEnabled(true);
											personal_moreClick(list_not_save,
													personal_sohu_count);
											personal_sohu_count++;
										}
									});
								}
						
						
					}
				}
	}

	// TODO: this might not be safe; was protected.
	@Override
	public void onResume()
	{
		super.onResume();
		
		Log.e("newGallery","onResume");
		
		PhimpMe.showTabs();	
		
		if (PhimpMe.FEEDS_GOOGLE_ADMOB == true){
			//PhimpMe.ShowAd();
		}
		
		if (PhimpMe.IdList.size() == 5) {
			PhimpMe.IdList.clear();
			PhimpMe.IdList.add(0);
			}else
				PhimpMe.IdList.add(0);
		
			CacheTask cachetask = new CacheTask();
			String[] str = null;
        	cachetask.execute(str);
        	if(PhimpMe.FEEDS_LOCAL_GALLERY==true){
        		Log.d("newGallery","resume load local gallery, number photo : "+number_resume_download);
    			linear_main.removeView(ln_local_gallery);    			
    			check_local = 0;
    			PhimpMe.filepath.clear();
    			array_ID.clear(); 		
    			resumeLocalPhoto(number_resume_download);
    		}  
        	//download photo
        	try{
    			//previous tab is setting
    			if(PhimpMe.IdList.get(PhimpMe.IdList.size()-2)==5){    					
    				pro_gress=ProgressDialog.show(ctx, "",getString(R.string.wait), true, false);					
    				Dialog_download(0,pro_gress);		
    			}
    		}catch(Exception e){
    			
    		}
	}
	public static void update(int num){
		Log.e("Gallery","Update");
		linear_main.removeView(ln_local_gallery);    			
		check_local = 0;
		PhimpMe.filepath.clear();
		array_ID.clear(); 		
		resumeLocalPhoto(num);
	}
	public class CacheTask extends AsyncTask<String, Void, String> {
	    @Override
	    protected String doInBackground(String... urls) {
	    	try{
	    		Log.d("newGallery", "Run Cache Task");
	    		updatePhoto();
	    		
	    	}catch(RuntimeException runex){
	    		//this.onCancelled();
				cancel(false);
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
	@SuppressWarnings("deprecation")
	public void updatePhoto(){
			Log.e("newGallery","load update photo");
			int id;
			final String[] columns = { MediaStore.Images.Thumbnails._ID};
			final String[] data = { MediaStore.Images.Media.DATA };
			final String orderBy = MediaStore.Images.Media._ID;
			Cursor pathcursor = getActivity().getContentResolver().query(
					MediaStore.Images.Media.EXTERNAL_CONTENT_URI, data,
					null, null, orderBy);
			if(pathcursor != null){
				int path_column_index = pathcursor
						.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
				int count = pathcursor.getCount();
				int c = 0;
				for (int i = 0; i< count; i++) {
					
						try{
							pathcursor.moveToPosition(i);
							String path = pathcursor.getString(path_column_index);
							
							boolean check = cache.check(path);
							if(check){
								@SuppressWarnings("unused")
								int index = Integer.valueOf(cache.getCacheId(path));
								@SuppressWarnings("unused")
								Bitmap bmp = cache.getCachePath(path);
								
							}
							else if(c<=20){				
								Cursor cursor = getActivity().getContentResolver().query(
										MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns,
										MediaStore.Images.Media.DATA+ " = " + "\""+path+"\"", null, MediaStore.Images.Media._ID);
								if (cursor != null && cursor.getCount() > 0){
									cursor.moveToPosition(0);
									id = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID));	
									Bitmap bmp = MediaStore.Images.Thumbnails.getThumbnail(
											getActivity().getApplicationContext().getContentResolver(), id,
											MediaStore.Images.Thumbnails.MICRO_KIND, null);		
									cache.saveCacheFile(path, bmp, id);
									cursor.close();
								}else id = -1;
								
								c++;
								
							}
						}catch(NullPointerException e){}
						
						
				}	
				pathcursor.close();
				
				
			}
			
			
	}

	@SuppressWarnings("deprecation")
	public static void resumeLocalPhoto(int resum_number){
		if (check_local == 0){
			
			int row;		
			row=(int)Math.ceil(resum_number/3);
			LinearLayout.LayoutParams p_two_row = new LinearLayout.LayoutParams(
					ViewGroup.LayoutParams.MATCH_PARENT,
					DEFAULT_THUMBNAIL_SIZE * row + 120);

			LinearLayout.LayoutParams p_zero = new LinearLayout.LayoutParams(
					0, 0);

			LinearLayout.LayoutParams p_one_row = new LinearLayout.LayoutParams(
					ViewGroup.LayoutParams.MATCH_PARENT,
					DEFAULT_THUMBNAIL_SIZE + 100);
			RelativeLayout.LayoutParams lp_more = new RelativeLayout.LayoutParams(
					40, 40);
			lp_more.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
			if (check_local == 0) {
				Log.d("Danh", "check value 1 =" + check_local);
				Button btn_line = new Button(ctx);
				btn_line.setHeight(2);
				btn_line.setWidth(LayoutParams.MATCH_PARENT);
				btn_line.setBackgroundResource(color_line);
				
				Button btn_line_black = new Button(ctx);
				btn_line_black.setHeight(10);
				btn_line_black.setWidth(LayoutParams.MATCH_PARENT);
				btn_line_black.setBackgroundResource(R.color.black);
				
				txtlocal_gallery = new TextView(ctx);
				txtlocal_gallery.setText(ctx.getString(R.string.localphotos));
				txtlocal_gallery.setTextSize(text_size);
				
				gv_local_gallery = new GridView(ctx);
				gv_local_gallery.setBackgroundResource(R.color.white);
				
				ln_local_gallery = new LinearLayout(ctx);
				ln_local_gallery.setOrientation(LinearLayout.VERTICAL);
				
				btn_local_more = new ImageButton(ctx);
				btn_local_more.setImageResource(R.drawable.more_disable);
				btn_local_more.setEnabled(false);

				RelativeLayout more_li = new RelativeLayout(ctx);
				btn_local_more.setLayoutParams(lp_more);
				more_li.addView(btn_local_more);
				more_li.addView(txtlocal_gallery);
				ln_local_gallery.addView(more_li);
				ln_local_gallery.addView(btn_line);
				ln_local_gallery.addView(btn_line_black);
				ln_local_gallery.addView(gv_local_gallery);
			
				PhimpMe.local_count = 1;

				linear_main.addView(ln_local_gallery,0);

				// get photo
				String[] projection={MediaStore.Images.Thumbnails._ID, MediaStore.Images.Thumbnails.IMAGE_ID};
				cursor = ((Activity) ctx).managedQuery(MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI, projection, null, null, MediaStore.Images.Thumbnails.IMAGE_ID+ " DESC");
				
				columnIndex=cursor.getColumnIndexOrThrow(MediaStore.Images.Thumbnails._ID);
				
				for (int i = 0; i < cursor.getCount(); i++) {
					if (cursor.moveToNext()){
						PhimpMe.filepath.add(cursor
								.getString(columnIndex));
						array_ID.add(cursor.getString(1));
					}
						
				}
				Log.d("newGallery","number image :" + PhimpMe.filepath.size());
				if (PhimpMe.filepath.size() <= 3) {
					ln_local_gallery.setLayoutParams(p_one_row);

				} else {
					ln_local_gallery.setLayoutParams(p_two_row);
				}

				if (PhimpMe.filepath.size() > 0) {
					final ArrayList<String> array_file;
					final ArrayList<String> ID;
					if (PhimpMe.filepath.size() <= resum_number) {
						array_file = new ArrayList<String>(PhimpMe.filepath.size());
						ID = new ArrayList<String>(array_ID.size());
						for (int i = 0; i < PhimpMe.filepath.size(); i++) {
							array_file.add(PhimpMe.filepath.get(i))	;
							ID.add(array_ID.get(i));
						}

					} else {
						array_file = new ArrayList<String>(resum_number);
						ID = new ArrayList<String>(resum_number);
						for (int i = 0; i < resum_number; i++) {
							array_file.add(PhimpMe.filepath.get(i));
							ID.add(array_ID.get(i));
						}

						btn_local_more
								.setImageResource(R.drawable.more);
						btn_local_more.setEnabled(true);
					}
					
					gv_local_gallery.setNumColumns(cols);
					local_adapter = new LocalPhotosAdapter(ctx,
							array_file,ID);
					gv_local_gallery.setAdapter(local_adapter);
					gv_local_gallery.setDrawingCacheEnabled(true);

					check_local = 1;
					btn_local_more
							.setOnClickListener(new OnClickListener() {
								@Override
								public void onClick(View v) {
									
									pro_gress=ProgressDialog.show(ctx, "",ctx.getString(R.string.wait), true, false);		
									timerDelayRemoveDialog(1000,pro_gress);										
								}
							});							
				} else {
					ln_local_gallery.setLayoutParams(p_zero);
					linear_main.removeView(more_li);
					check_local = 1;
				}
				Log.d("Danh", "check = " + check_local);
			} else {
			}
			
		}
	}
	public static void timerDelayRemoveDialog(long time, final Dialog d){
	    new Handler().postDelayed(new Runnable() {
	        public void run() { 
	        	
	        	number_resume_download=count_photo* NUMBER_PHOTO_NEED_DOWNLOAD+ NUMBER_PHOTO_NEED_DOWNLOAD;			
	        	if (PhimpMe.filepath.size()
						- count_photo
						* NUMBER_PHOTO_NEED_DOWNLOAD > 3) {
					local_rows_display = 2;
				} else if ((PhimpMe.filepath.size() - count_photo
						* NUMBER_PHOTO_NEED_DOWNLOAD) > 0) {
					local_rows_display = 1;
				}
				int h = ln_local_gallery
						.getHeight();
				int hight_display = h
						+ DEFAULT_THUMBNAIL_SIZE
						* local_rows_display + 100;
				LinearLayout.LayoutParams rep = new LinearLayout.LayoutParams(
						ViewGroup.LayoutParams.WRAP_CONTENT,
						hight_display);
				ln_local_gallery
						.setLayoutParams(rep);
				ln_local_gallery.setEnabled(true);
				final ArrayList<String> array_file;
				final ArrayList<String> ID;
				if ((PhimpMe.filepath.size() - count_photo
						* NUMBER_PHOTO_NEED_DOWNLOAD) <= NUMBER_PHOTO_NEED_DOWNLOAD) {
					array_file = new ArrayList<String>(PhimpMe.filepath.size());
					ID = new ArrayList<String>(array_ID.size());
					for (int i = 0; i < PhimpMe.filepath.size(); i++) {
						array_file.add(PhimpMe.filepath.get(i));
						ID.add(array_ID.get(i));
					}
					
				} else {
					array_file = new ArrayList<String>(count_photo* NUMBER_PHOTO_NEED_DOWNLOAD+ NUMBER_PHOTO_NEED_DOWNLOAD);
					ID = new ArrayList<String>(count_photo* NUMBER_PHOTO_NEED_DOWNLOAD+ NUMBER_PHOTO_NEED_DOWNLOAD);
					for (int i = 0; i <count_photo* NUMBER_PHOTO_NEED_DOWNLOAD+ NUMBER_PHOTO_NEED_DOWNLOAD; i++) {
						array_file.add(PhimpMe.filepath.get(i));
						ID.add(array_ID.get(i));
						
					}
					Log.d("newGallery","count_photo : "+count_photo);
				}
				
				local_adapter = new LocalPhotosAdapter(
						ctx, array_file,ID);
				gv_local_gallery
						.setAdapter(local_adapter);

				count_photo++;
				if (PhimpMe.filepath.size()
						- count_photo
						* NUMBER_PHOTO_NEED_DOWNLOAD <= 0) {
					
					btn_local_more
							.setImageResource(R.drawable.more_disable);
					btn_local_more
							.setEnabled(false);
					
				}			
				check_local = 1;
	            d.dismiss();         
	        }
	    }, time); 
	}
	public void Dialog_download(long time, final Dialog d){
	    new Handler().postDelayed(new Runnable() {
	        public void run() {  
	        	d.dismiss();	        	
	        	clearAllPhoto();
				if(PhimpMe.FEEDS_LOCAL_GALLERY==true){						
					linear_main.removeView(ln_local_gallery);    			
        			check_local = 0;
        			PhimpMe.filepath.clear();
        			array_ID.clear();
    				number_resume_download=6;
    				count_photo = 1;
    				resumeLocalPhoto(number_resume_download);
				}
				
	        	refreshNewPhotos();           	        	
	               
	        }
	    }, time); 
	}

	public void Dialog(long time,final Dialog d){
		new Handler().postDelayed(new Runnable() {
			
			@Override
			public void run() {
				d.dismiss();
			}
		}, time);
	}
	public void refreshNewPhotos() {
		
		if (Commons.checkConnection(getActivity())) {
			
			if ( 
					(!PhimpMe.FEEDS_LIST_FLICKR_PUBLIC)
					&& (!PhimpMe.FEEDS_LIST_FLICKR_RECENT)
					&& (!PhimpMe.FEEDS_LIST_YAHOO_NEWS)
					&& (!PhimpMe.FEEDS_LIST_GOOGLE_PICASA_PUBLIC)
					&& (!PhimpMe.FEEDS_LIST_GOOGLE_NEWS)
					&& (!PhimpMe.FEEDS_LIST_DEVIANTART_PUBLIC)
					&& (!PhimpMe.FEEDS_LIST_500PX_PUBLIC) &&

					(!PhimpMe.FEEDS_LIST_FACEBOOK_PRIVATE)
					&& (!PhimpMe.FEEDS_LIST_TUMBLR_PRIVATE)
					&& (!PhimpMe.FEEDS_LIST_VK)
					&& (!PhimpMe.FEEDS_LIST_FLICKR_PRIVATE)
					&& (!PhimpMe.FEEDS_LIST_GOOGLE_PICASA_PRIVATE)
					&& (!PhimpMe.FEEDS_LIST_DEVIANTART_PRIVITE)
					&& (!PhimpMe.FEEDS_LIST_IMGUR_PERSONAL)
					&& (!PhimpMe.FEEDS_LIST_500PX_PRIVATE)
					&& (!PhimpMe.FEEDS_LIST_MYSERVICES)
					&& (!PhimpMe.FEEDS_LIST_MYSERVICES1)
					&& (!PhimpMe.FEEDS_LIST_MYSERVICES2)
					&& (!PhimpMe.FEEDS_LIST_MYSERVICES3)
					&& (!PhimpMe.FEEDS_LIST_MYSERVICES4)
					&& (!PhimpMe.FEEDS_LIST_MYSERVICES5)
					&& (!PhimpMe.FEEDS_LIST_SOHU_PERSONAL)
					&& (!PhimpMe.FEEDS_LIST_KAIXIN_PRIVATE)

			) {
			} 			
			else if (PhimpMe.FEEDS_LIST_MYSERVICES
					&& (Settings.etMyFeedServicesTextbox.getText().toString()
							.equals("") || Settings.etMyFeedServicesTextbox
							.getText().toString().equals("\n"))) {
			} else if (PhimpMe.FEEDS_LIST_MYSERVICES1
					&& (Settings.etMyFeedServicesTextbox1.getText().toString()
							.equals("") || Settings.etMyFeedServicesTextbox1
							.getText().toString().equals("\n"))) {
			} else if (PhimpMe.FEEDS_LIST_MYSERVICES2
					&& (Settings.etMyFeedServicesTextbox2.getText().toString()
							.equals("") || Settings.etMyFeedServicesTextbox2
							.getText().toString().equals("\n"))) {
			} else if (PhimpMe.FEEDS_LIST_MYSERVICES3
					&& (Settings.etMyFeedServicesTextbox3.getText().toString()
							.equals("") || Settings.etMyFeedServicesTextbox3
							.getText().toString().equals("\n"))) {
			} else if (PhimpMe.FEEDS_LIST_MYSERVICES4
					&& (Settings.etMyFeedServicesTextbox4.getText().toString()
							.equals("") || Settings.etMyFeedServicesTextbox4
							.getText().toString().equals("\n"))) {
			} else if (PhimpMe.FEEDS_LIST_MYSERVICES5
					&& (Settings.etMyFeedServicesTextbox5.getText().toString()
							.equals("") || Settings.etMyFeedServicesTextbox5
							.getText().toString().equals("\n"))) {
			} else {
				tmp_list.clear();
				tmp_list_personal.clear();
				list_photos.clear();
				list_photos_personal.clear();
				final LinearLayout.LayoutParams p_two_row = new LinearLayout.LayoutParams(
						ViewGroup.LayoutParams.WRAP_CONTENT,
						DEFAULT_THUMBNAIL_SIZE * 2 + 80);

				final LinearLayout.LayoutParams p_zero = new LinearLayout.LayoutParams(
						0, 0);

				final LinearLayout.LayoutParams p_one_row = new LinearLayout.LayoutParams(
						ViewGroup.LayoutParams.WRAP_CONTENT,
						DEFAULT_THUMBNAIL_SIZE + 80);
				final RelativeLayout.LayoutParams lp_more = new RelativeLayout.LayoutParams(
						40, 40);
				lp_more.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
				if (PhimpMe.FEEDS_LIST_FLICKR_PUBLIC) {
					new Handler().post(new Runnable() {
						
						@Override
						public void run() {			
							if (listService.indexOf("public_flickr") == -1) {						
								flickr_public_download=true;
								Button btn_line = new Button(ctx);
								btn_line.setHeight(2);
								btn_line.setWidth(LayoutParams.MATCH_PARENT);
								btn_line.setBackgroundResource(color_line);
		
								txtPFlickr = new TextView(ctx);
								txtPFlickr.setText("Public Flickr");
								txtPFlickr.setTextSize(text_size);
								p_flickr = new GridView(ctx);
								p_flickr.setPadding(0, 10, 0, 0);
								ln_flickr = new LinearLayout(ctx);
								btn_flickr_more = new ImageButton(ctx);
								btn_flickr_more
										.setImageResource(R.drawable.more_disable);
								btn_flickr_more.setEnabled(false);
								RelativeLayout more_li = new RelativeLayout(ctx);
								btn_flickr_more.setLayoutParams(lp_more);
								more_li.addView(btn_flickr_more);
								more_li.addView(txtPFlickr);
								ln_flickr.setOrientation(LinearLayout.VERTICAL);
								ln_flickr.addView(more_li);
								ln_flickr.addView(btn_line);
								ln_flickr.addView(p_flickr);
		
								ln_flickr.setEnabled(false);
		
								linear_main.addView(ln_flickr);
								Log.d("luong test", bitmap_p_flickr.size() + "");
								flickradapter = new GridFlickrAdaper(list_thumb,
										bitmap_p_flickr, ctx);
								p_flickr.setNumColumns(cols);
								p_flickr.setAdapter(flickradapter);
								p_flickr.setDrawingCacheEnabled(true);
								listService.add("public_flickr");
							}
							btn_flickr_more.setImageResource(R.drawable.more_disable);
							btn_flickr_more.setEnabled(false);
							flickr_count = 1;		
							flickradapter.removeItem();							
							tmp_list = Flickr.getPublic(ctx, "");
							Log.d("thong", "Flickr Public: " + tmp_list.size());
							list_photos.addAll(tmp_list);
							// Don't save exist photos
							ArrayList<RSSPhotoItem> list_save = new ArrayList<RSSPhotoItem>();
							for (int t = 0; t < tmp_list.size(); t++) {
								String url = tmp_list.get(t).getURL();
								String filepath = rss_folder.getAbsolutePath()
										+ "/"
										+ url.toLowerCase().replace("://", "")
												.replace("/", "_");
								File f = new File(filepath);
								if (!f.exists()) {
									list_save.add(tmp_list.get(t));
								}
							}
		
							if (list_save.size() == 0) {
								ln_flickr.setLayoutParams(p_zero);
							} else if (list_save.size() <= 3) {
								ln_flickr.setLayoutParams(p_one_row);
							} else
							ln_flickr.setLayoutParams(p_two_row);
							flickr_list_photos.clear();
							flickr_list_photos.addAll(list_save);
							array_list.add(flickr_list_photos);
							if (!checkArray(PhimpMe.phimpme_array_list,flickr_list_photos)) {
								PhimpMe.phimpme_array_list.add(flickr_list_photos);
							} else {
								deleteItem(PhimpMe.phimpme_array_list,flickr_list_photos);
								PhimpMe.phimpme_array_list.add(flickr_list_photos);
							}							
							/*for (int i = 0; i < array_list.size(); i++) {
								if (array_list.get(i).size() > 0) {
									Log.e("thong", "RunOnUiThread , ArrayList size :"+array_list.size());
									final RSSPhotoItem[] tmp;
									if (array_list.get(i).size() < NUMBER_PHOTO_NEED_DOWNLOAD) {
										tmp = new RSSPhotoItem[array_list.get(i).size()];
									} else {
										tmp = new RSSPhotoItem[NUMBER_PHOTO_NEED_DOWNLOAD];
									}
									for (int j = 0; j < tmp.length; j++) {
										tmp[j] = array_list.get(i).get(j);
									}
									getActivity().runOnUiThread(new Runnable() {
										@Override
										public void run() {
											new DownloadImageAsyncTask().execute(tmp);
										}
									});

								}
							}*/
							Log.d("thong", "RunOnUiThread");
							final RSSPhotoItem[] tmp;									
							if (flickr_list_photos.size() < NUMBER_PHOTO_NEED_DOWNLOAD) {
								tmp = new RSSPhotoItem[flickr_list_photos.size()];
							} else {
								tmp = new RSSPhotoItem[NUMBER_PHOTO_NEED_DOWNLOAD];
							}
							for (int j = 0; j < tmp.length; j++) {
								tmp[j] = flickr_list_photos.get(j);
							}
							getActivity().runOnUiThread(new Runnable() {
								@Override
								public void run() {
									new DownloadImageAsyncTask().execute(tmp);
								}
							});
							btn_flickr_more.setOnClickListener(new OnClickListener() {
								@Override
								public void onClick(View v) {
									btn_flickr_more
											.setImageResource(R.drawable.more_disable);
									btn_flickr_more.setEnabled(false);
									if ((flickr_list_photos.size() - flickr_count
											* NUMBER_PHOTO_NEED_DOWNLOAD) > 3) {
										flickr_rows_display = 2;
									} else if ((flickr_list_photos.size() - flickr_count
											* NUMBER_PHOTO_NEED_DOWNLOAD) > 0)
										flickr_rows_display = 1;
									int h = ln_flickr.getHeight();
									int hight_display = h + DEFAULT_THUMBNAIL_SIZE
											* flickr_rows_display;
									LinearLayout.LayoutParams rep = new LinearLayout.LayoutParams(
											ViewGroup.LayoutParams.WRAP_CONTENT,
											hight_display);
									ln_flickr.setLayoutParams(rep);
									ln_flickr.setEnabled(true);
									moreClick(flickr_list_photos, flickr_count);
									flickr_count++;
								}
							});
						}});
				}

				if (PhimpMe.FEEDS_LIST_FLICKR_RECENT) {
					new Handler().post(new Runnable() {
						
						@Override
						public void run() {										
						if (listService.indexOf("recent_flickr") == -1) {
							flick_recent_download=true;
							Button btn_line = new Button(ctx);
							btn_line.setHeight(2);
							btn_line.setWidth(LayoutParams.MATCH_PARENT);
							btn_line.setBackgroundResource(color_line);
	
							txtRecentFlickr = new TextView(ctx);
							txtRecentFlickr.setText("Recent Flickr");
							txtRecentFlickr.setTextSize(text_size);
							recent_flickr = new GridView(ctx);
							recent_flickr.setPadding(0, 10, 0, 0);
							ln_recent_flickr = new LinearLayout(ctx);
							btn_recent_flickr_more = new ImageButton(ctx);
							btn_recent_flickr_more
									.setImageResource(R.drawable.more_disable);
							btn_recent_flickr_more.setEnabled(false);
	
							RelativeLayout more_li = new RelativeLayout(ctx);
							btn_recent_flickr_more.setLayoutParams(lp_more);
							more_li.addView(btn_recent_flickr_more);
							more_li.addView(txtRecentFlickr);
	
							ln_recent_flickr.setOrientation(LinearLayout.VERTICAL);
	
							ln_recent_flickr.addView(more_li);
							ln_recent_flickr.addView(btn_line);
							ln_recent_flickr.addView(recent_flickr);
	
							ln_recent_flickr.setEnabled(false);
	
							linear_main.addView(ln_recent_flickr);
							recentflickradapter = new GridRecentFlickrAdaper(
									list_thumb, bitmap_recent_flickr, ctx);
							recent_flickr.setNumColumns(cols);
							recent_flickr.setAdapter(recentflickradapter);
							recent_flickr.setDrawingCacheEnabled(true);
							listService.add("recent_flickr");
	
						}
						btn_recent_flickr_more
								.setImageResource(R.drawable.more_disable);
						btn_recent_flickr_more.setEnabled(false);
						recent_flickr_count = 1;
	
						recentflickradapter.removeItem();
						tmp_list = Flickr.getRecent(ctx);
						Log.d("thong", "Flickr Recent: " + tmp_list.size());
						// Don't save exist photos
						ArrayList<RSSPhotoItem> list_save = new ArrayList<RSSPhotoItem>();
						for (int t = 0; t < tmp_list.size(); t++) {
							String url = tmp_list.get(t).getURL();
							String filepath = rss_folder.getAbsolutePath()
									+ "/"
									+ url.toLowerCase().replace("://", "")
											.replace("/", "_");
							File f = new File(filepath);
							if (!f.exists()) {
								list_save.add(tmp_list.get(t));
							}
						}
	
						if (list_save.size() == 0) {
							ln_recent_flickr.setLayoutParams(p_zero);
						} else if (list_save.size() <= 3) {
							ln_recent_flickr.setLayoutParams(p_one_row);
						} else
							ln_recent_flickr.setLayoutParams(p_two_row);
						list_photos.addAll(tmp_list);
						recent_flickr_list_photos.clear();
						recent_flickr_list_photos.addAll(list_save);
						array_list.add(recent_flickr_list_photos);
						if (!checkArray(PhimpMe.phimpme_array_list,recent_flickr_list_photos)) {
							PhimpMe.phimpme_array_list
									.add(recent_flickr_list_photos);
						} else {
							deleteItem(PhimpMe.phimpme_array_list,recent_flickr_list_photos);
							PhimpMe.phimpme_array_list
									.add(recent_flickr_list_photos);
						}
						/*for (int i = 0; i < array_list.size(); i++) {
							if (array_list.get(i).size() > 0) {
								Log.d("thong", "RunOnUiThread");
								final RSSPhotoItem[] tmp;
								if (array_list.get(i).size() < NUMBER_PHOTO_NEED_DOWNLOAD) {
									tmp = new RSSPhotoItem[array_list.get(i).size()];
								} else {
									tmp = new RSSPhotoItem[NUMBER_PHOTO_NEED_DOWNLOAD];
								}
								for (int j = 0; j < tmp.length; j++) {
									tmp[j] = array_list.get(i).get(j);
								}
								getActivity().runOnUiThread(new Runnable() {
									@Override
									public void run() {

										new DownloadImageAsyncTask().execute(tmp);
									}
								});

							}
						}	*/
						Log.d("thong", "RunOnUiThread");
						final RSSPhotoItem[] tmp;
						if (recent_flickr_list_photos.size() < NUMBER_PHOTO_NEED_DOWNLOAD) {
							tmp = new RSSPhotoItem[recent_flickr_list_photos.size()];
						} else {
							tmp = new RSSPhotoItem[NUMBER_PHOTO_NEED_DOWNLOAD];
						}
						for (int j = 0; j < tmp.length; j++) {
							tmp[j] = recent_flickr_list_photos.get(j);
						}
						getActivity().runOnUiThread(new Runnable() {
							@Override
							public void run() {

								new DownloadImageAsyncTask().execute(tmp);
							}
						});
						btn_recent_flickr_more
								.setOnClickListener(new OnClickListener() {
									@Override
									public void onClick(View v) {
										btn_recent_flickr_more
												.setImageResource(R.drawable.more_disable);
										btn_recent_flickr_more.setEnabled(false);
										if (recent_flickr_list_photos.size()
												- recent_flickr_count
												* NUMBER_PHOTO_NEED_DOWNLOAD > 3) {
											recent_flickr_rows_display = 2;
										} else if ((recent_flickr_list_photos
												.size() - recent_flickr_count
												* NUMBER_PHOTO_NEED_DOWNLOAD) > 0)
											recent_flickr_rows_display = 1;
										int h = ln_recent_flickr.getHeight();
										int hight_display = h
												+ DEFAULT_THUMBNAIL_SIZE
												* recent_flickr_rows_display;
										LinearLayout.LayoutParams rep = new LinearLayout.LayoutParams(
												ViewGroup.LayoutParams.WRAP_CONTENT,
												hight_display);
										ln_recent_flickr.setLayoutParams(rep);
										ln_recent_flickr.setEnabled(true);
										moreClick(recent_flickr_list_photos,
												recent_flickr_count);
										recent_flickr_count++;
									}
								});
						}});

				}

				if (PhimpMe.FEEDS_LIST_YAHOO_NEWS) {
					new Handler().post(new Runnable() {						
						@Override
						public void run() {							
							if (listService.indexOf("public_yahoo") == -1) {
								yahoo_download=true;
								Button btn_line = new Button(ctx);
								btn_line.setHeight(2);
								btn_line.setWidth(LayoutParams.MATCH_PARENT);
								btn_line.setBackgroundResource(color_line);
		
								txtyahoo = new TextView(ctx);
								txtyahoo.setText("Public Yahoo");
								txtyahoo.setTextSize(text_size);
								p_yahoo = new GridView(ctx);
								p_yahoo.setPadding(0, 10, 0, 0);
								ln_yahoo = new LinearLayout(ctx);
								btn_yahoo_more = new ImageButton(ctx);
								btn_yahoo_more
										.setImageResource(R.drawable.more_disable);
								btn_yahoo_more.setEnabled(false);
		
								RelativeLayout more_li = new RelativeLayout(ctx);
								btn_yahoo_more.setLayoutParams(lp_more);
								more_li.addView(btn_yahoo_more);
								more_li.addView(txtyahoo);
		
								ln_yahoo.setOrientation(LinearLayout.VERTICAL);
								ln_yahoo.addView(more_li);
								ln_yahoo.addView(btn_line);
								ln_yahoo.addView(p_yahoo);
		
								ln_yahoo.setEnabled(false);
		
								linear_main.addView(ln_yahoo);
								yahooadapter = new GridYahooAdapter(list_thumb,
										bitmap_p_yahoo, ctx);
								p_yahoo.setNumColumns(cols);
								p_yahoo.setAdapter(yahooadapter);
								p_yahoo.setDrawingCacheEnabled(true);
								listService.add("public_yahoo");
		
							}
							btn_yahoo_more.setImageResource(R.drawable.more_disable);
							btn_yahoo_more.setEnabled(false);
							yahoo_count = 1;
							yahooadapter.removeItem();
							tmp_list = Yahoo.getYahooNews(ctx);
							Log.d("thong", "Yahoo Public: " + tmp_list.size());
							// Don't save exist photos
							ArrayList<RSSPhotoItem> list_save = new ArrayList<RSSPhotoItem>();
							for (int t = 0; t < tmp_list.size(); t++) {
								String url = tmp_list.get(t).getURL();
								String filepath = rss_folder.getAbsolutePath()
										+ "/"
										+ url.toLowerCase().replace("://", "")
												.replace("/", "_");
								File f = new File(filepath);
								if (!f.exists()) {
									list_save.add(tmp_list.get(t));
								}
							}
		
							if (list_save.size() == 0) {
								ln_yahoo.setLayoutParams(p_zero);
							} else if (list_save.size() <= 3) {
								ln_yahoo.setLayoutParams(p_one_row);
							} else
								ln_yahoo.setLayoutParams(p_two_row);
							list_photos.addAll(tmp_list);
							yahoo_list_photos.clear();
							yahoo_list_photos.addAll(list_save);
		
							array_list.add(yahoo_list_photos);
							if (!checkArray(PhimpMe.phimpme_array_list,yahoo_list_photos)) {
								PhimpMe.phimpme_array_list.add(yahoo_list_photos);
							} else {
								deleteItem(PhimpMe.phimpme_array_list,yahoo_list_photos);
								PhimpMe.phimpme_array_list.add(yahoo_list_photos);
							}
							/*for (int i = 0; i < array_list.size(); i++) {
								if (array_list.get(i).size() > 0) {
									Log.d("thong", "RunOnUiThread");
									final RSSPhotoItem[] tmp;
									if (array_list.get(i).size() < NUMBER_PHOTO_NEED_DOWNLOAD) {
										tmp = new RSSPhotoItem[array_list.get(i).size()];
									} else {
										tmp = new RSSPhotoItem[NUMBER_PHOTO_NEED_DOWNLOAD];
									}
									for (int j = 0; j < tmp.length; j++) {
										tmp[j] = array_list.get(i).get(j);
									}
									getActivity().runOnUiThread(new Runnable() {
										@Override
										public void run() {
											new DownloadImageAsyncTask().execute(tmp);
										}
									});

								}
							}*/
							Log.d("thong", "RunOnUiThread");
							final RSSPhotoItem[] tmp;
							if (yahoo_list_photos.size() < NUMBER_PHOTO_NEED_DOWNLOAD) {
								tmp = new RSSPhotoItem[yahoo_list_photos.size()];
							} else {
								tmp = new RSSPhotoItem[NUMBER_PHOTO_NEED_DOWNLOAD];
							}
							for (int j = 0; j < tmp.length; j++) {
								tmp[j] = yahoo_list_photos.get(j);
							}
							getActivity().runOnUiThread(new Runnable() {
								@Override
								public void run() {
									new DownloadImageAsyncTask().execute(tmp);
								}
							});
				
							btn_yahoo_more.setOnClickListener(new OnClickListener() {
								@Override
								public void onClick(View v) {
									btn_yahoo_more
											.setImageResource(R.drawable.more_disable);
									btn_yahoo_more.setEnabled(false);
									if (yahoo_list_photos.size() - yahoo_count
											* NUMBER_PHOTO_NEED_DOWNLOAD > 3) {
										yahoo_rows_display = 2;
									} else if ((yahoo_list_photos.size() - yahoo_count
											* NUMBER_PHOTO_NEED_DOWNLOAD) > 0)
										yahoo_rows_display = 1;
		
									int hight_display = ln_yahoo.getHeight()
											+ DEFAULT_THUMBNAIL_SIZE
											* yahoo_rows_display;
									LinearLayout.LayoutParams rep = new LinearLayout.LayoutParams(
											ViewGroup.LayoutParams.WRAP_CONTENT,
											hight_display);
									ln_yahoo.setLayoutParams(rep);
									ln_yahoo.setEnabled(true);
									moreClick(yahoo_list_photos, yahoo_count);
									yahoo_count++;
								}
							});
						}});
				}

				if (PhimpMe.FEEDS_LIST_GOOGLE_PICASA_PUBLIC) {
					new Handler().post(new Runnable() {						
						@Override
						public void run() {			
					if (listService.indexOf("public_picasa") == -1) {
						picasa_public_download=true;
						Button btn_line = new Button(ctx);
						btn_line.setHeight(2);
						btn_line.setWidth(LayoutParams.MATCH_PARENT);
						btn_line.setBackgroundResource(color_line);

						txtPublicPicasa = new TextView(ctx);
						txtPublicPicasa.setText("Public Picasa");
						txtPublicPicasa.setTextSize(text_size);
						p_picasa = new GridView(ctx);
						p_picasa.setPadding(0, 10, 0, 0);
						ln_public_picasa = new LinearLayout(ctx);
						btn_public_picasa_more = new ImageButton(ctx);
						btn_public_picasa_more
								.setImageResource(R.drawable.more_disable);
						btn_public_picasa_more.setEnabled(false);

						RelativeLayout more_li = new RelativeLayout(ctx);
						btn_public_picasa_more.setLayoutParams(lp_more);
						more_li.addView(btn_public_picasa_more);
						more_li.addView(txtPublicPicasa);
						ln_public_picasa.setOrientation(LinearLayout.VERTICAL);
						ln_public_picasa.addView(more_li);
						ln_public_picasa.addView(btn_line);
						ln_public_picasa.addView(p_picasa);

						ln_public_picasa.setEnabled(false);

						linear_main.addView(ln_public_picasa);
						publicpicasaadapter = new GridPublicPicasaAdaper(
								list_thumb, bitmap_public_picasa, ctx);
						p_picasa.setNumColumns(cols);
						p_picasa.setAdapter(publicpicasaadapter);
						p_picasa.setDrawingCacheEnabled(true);
						listService.add("public_picasa");

					}
					btn_public_picasa_more
							.setImageResource(R.drawable.more_disable);
					btn_public_picasa_more.setEnabled(false);
					public_picasa_count = 1;

					publicpicasaadapter.removeItem();
					tmp_list = Google.getPicasaPublic(ctx);
					Log.d("thong", "Google Public Picasa: " + tmp_list.size());
					// Don't save exist photos
					ArrayList<RSSPhotoItem> list_save = new ArrayList<RSSPhotoItem>();
					for (int t = 0; t < tmp_list.size(); t++) {
						String url = tmp_list.get(t).getURL();
						String filepath = rss_folder.getAbsolutePath()
								+ "/"
								+ url.toLowerCase().replace("://", "")
										.replace("/", "_");
						File f = new File(filepath);
						if (!f.exists()) {
							list_save.add(tmp_list.get(t));
						}
					}

					if (list_save.size() == 0) {
						ln_public_picasa.setLayoutParams(p_zero);
					} else if (list_save.size() <= 3) {
						ln_public_picasa.setLayoutParams(p_one_row);
					} else
						ln_public_picasa.setLayoutParams(p_two_row);
					list_photos.addAll(tmp_list);
					public_picasa_list_photos.clear();
					public_picasa_list_photos.addAll(list_save);
					array_list.add(public_picasa_list_photos);
					if (!checkArray(PhimpMe.phimpme_array_list,public_picasa_list_photos)) {
						PhimpMe.phimpme_array_list
								.add(public_picasa_list_photos);
					} else {
						deleteItem(PhimpMe.phimpme_array_list,public_picasa_list_photos);
						PhimpMe.phimpme_array_list
								.add(public_picasa_list_photos);
					}
					/*for (int i = 0; i < array_list.size(); i++) {
						if (array_list.get(i).size() > 0) {
							Log.d("thong", "RunOnUiThread");
							final RSSPhotoItem[] tmp;
							if (array_list.get(i).size() < NUMBER_PHOTO_NEED_DOWNLOAD) {
								tmp = new RSSPhotoItem[array_list.get(i).size()];
							} else {
								tmp = new RSSPhotoItem[NUMBER_PHOTO_NEED_DOWNLOAD];
							}
							for (int j = 0; j < tmp.length; j++) {
								tmp[j] = array_list.get(i).get(j);
							}
							getActivity().runOnUiThread(new Runnable() {
								@Override
								public void run() {

									new DownloadImageAsyncTask().execute(tmp);
								}
							});

						}
					}*/
					Log.d("thong", "RunOnUiThread");
					final RSSPhotoItem[] tmp;
					if (public_picasa_list_photos.size() < NUMBER_PHOTO_NEED_DOWNLOAD) {
						tmp = new RSSPhotoItem[public_picasa_list_photos.size()];
					} else {
						tmp = new RSSPhotoItem[NUMBER_PHOTO_NEED_DOWNLOAD];
					}
					for (int j = 0; j < tmp.length; j++) {
						tmp[j] = public_picasa_list_photos.get(j);
					}
					getActivity().runOnUiThread(new Runnable() {
						@Override
						public void run() {

							new DownloadImageAsyncTask().execute(tmp);
						}
					});
					btn_public_picasa_more
							.setOnClickListener(new OnClickListener() {
								@Override
								public void onClick(View v) {
									btn_public_picasa_more
											.setImageResource(R.drawable.more_disable);
									btn_public_picasa_more.setEnabled(false);
									if (public_picasa_list_photos.size()
											- public_picasa_count
											* NUMBER_PHOTO_NEED_DOWNLOAD > 3) {
										public_picasa_rows_display = 2;
									} else if ((public_picasa_list_photos
											.size() - public_picasa_count
											* NUMBER_PHOTO_NEED_DOWNLOAD) > 0)
										public_picasa_rows_display = 1;

									int hight_display = ln_public_picasa
											.getHeight()
											+ DEFAULT_THUMBNAIL_SIZE
											* public_picasa_rows_display;
									LinearLayout.LayoutParams rep = new LinearLayout.LayoutParams(
											ViewGroup.LayoutParams.WRAP_CONTENT,
											hight_display);
									ln_public_picasa.setLayoutParams(rep);
									ln_public_picasa.setEnabled(true);
									moreClick(public_picasa_list_photos,
											public_picasa_count);
									public_picasa_count++;
								}
							});
						}});
				}

				if (PhimpMe.FEEDS_LIST_GOOGLE_NEWS) {										
							if (listService.indexOf("google_news") == -1) {
								google_new_download=true;
								Button btn_line = new Button(ctx);
								btn_line.setHeight(2);
								btn_line.setWidth(LayoutParams.MATCH_PARENT);
								btn_line.setBackgroundResource(color_line);
		
								txtGooglenews = new TextView(ctx);
								txtGooglenews.setText("Google News");
								txtGooglenews.setTextSize(text_size);
								p_googlenews = new GridView(ctx);
								p_googlenews.setPadding(0, 10, 0, 0);
								ln_googlenews = new LinearLayout(ctx);
								btn_googlenews_more = new ImageButton(ctx);
								btn_googlenews_more
										.setImageResource(R.drawable.more_disable);
								btn_googlenews_more.setEnabled(false);
		
								RelativeLayout more_li = new RelativeLayout(ctx);
								btn_googlenews_more.setLayoutParams(lp_more);
								more_li.addView(btn_googlenews_more);
								more_li.addView(txtGooglenews);
								ln_googlenews.setOrientation(LinearLayout.VERTICAL);
								ln_googlenews.addView(more_li);
								ln_googlenews.addView(btn_line);
								ln_googlenews.addView(p_googlenews);
		
								ln_googlenews.setEnabled(false);
		
								linear_main.addView(ln_googlenews);
								googlenewsadapter = new GridGoogleNewsAdaper(
										list_thumb, bitmap_google_news, ctx);
								p_googlenews.setNumColumns(cols);
								p_googlenews.setAdapter(googlenewsadapter);
								p_googlenews.setDrawingCacheEnabled(true);
								listService.add("google_news");
		
							}
							btn_googlenews_more
									.setImageResource(R.drawable.more_disable);
							btn_googlenews_more.setEnabled(false);
							googlenews_count = 1;
		
							googlenewsadapter.removeItem();
							tmp_list = Google.getNews(ctx);
							Log.d("thong", "Google News: " + tmp_list.size());
							// Don't save exist photos
							ArrayList<RSSPhotoItem> list_save = new ArrayList<RSSPhotoItem>();
							for (int t = 0; t < tmp_list.size(); t++) {
								String url = tmp_list.get(t).getURL();
								String filepath = rss_folder.getAbsolutePath()
										+ "/"
										+ url.toLowerCase().replace("://", "")
												.replace("/", "_");
								File f = new File(filepath);
								if (!f.exists()) {
									list_save.add(tmp_list.get(t));
								}
							}
		
							if (list_save.size() == 0) {
								ln_googlenews.setLayoutParams(p_zero);
							} else if (list_save.size() <= 3) {
								ln_googlenews.setLayoutParams(p_one_row);
							} else
								ln_googlenews.setLayoutParams(p_two_row);
							list_photos.addAll(tmp_list);
							googlenews_list_photos.clear();
							googlenews_list_photos.addAll(list_save);
		
							array_list.add(googlenews_list_photos);
							if (!checkArray(PhimpMe.phimpme_array_list,googlenews_list_photos)) {
								PhimpMe.phimpme_array_list.add(googlenews_list_photos);
							} else {
								deleteItem(PhimpMe.phimpme_array_list,googlenews_list_photos);
								PhimpMe.phimpme_array_list.add(googlenews_list_photos);
							}						
							btn_googlenews_more
									.setOnClickListener(new OnClickListener() {
										@Override
										public void onClick(View v) {
											btn_googlenews_more
													.setImageResource(R.drawable.more_disable);
											btn_googlenews_more.setEnabled(false);
											if (googlenews_list_photos.size()
													- googlenews_count
													* NUMBER_PHOTO_NEED_DOWNLOAD > 3) {
												googlenews_rows_display = 2;
											} else if ((googlenews_list_photos.size() - googlenews_count
													* NUMBER_PHOTO_NEED_DOWNLOAD) > 0)
												googlenews_rows_display = 1;
		
											int hight_display = ln_googlenews
													.getHeight()
													+ DEFAULT_THUMBNAIL_SIZE
													* googlenews_rows_display;
											LinearLayout.LayoutParams rep = new LinearLayout.LayoutParams(
													ViewGroup.LayoutParams.WRAP_CONTENT,
													hight_display);
											ln_googlenews.setLayoutParams(rep);
											ln_googlenews.setEnabled(true);
											moreClick(googlenews_list_photos,
													googlenews_count);
											googlenews_count++;
										}
									});					
				}
				if (PhimpMe.FEEDS_LIST_DEVIANTART_PUBLIC) {
					new Handler().post(new Runnable() {						
						@Override
						public void run() {			
							if (listService.indexOf("public_deviant") == -1) {
								public_deviant_download=true;
								Button btn_line = new Button(ctx);
								btn_line.setHeight(2);
								btn_line.setWidth(LayoutParams.MATCH_PARENT);
								btn_line.setBackgroundResource(color_line);
		
								txtdeviant = new TextView(ctx);
								txtdeviant.setText("Public DeviantArt");
								txtdeviant.setTextSize(text_size);
								p_deviant = new GridView(ctx);
								p_deviant.setPadding(0, 10, 0, 0);
								ln_deviant = new LinearLayout(ctx);
								btn_public_deviant_more = new ImageButton(ctx);
								btn_public_deviant_more
										.setImageResource(R.drawable.more_disable);
								btn_public_deviant_more.setEnabled(false);
		
								RelativeLayout more_li = new RelativeLayout(ctx);
								btn_public_deviant_more.setLayoutParams(lp_more);
								more_li.addView(btn_public_deviant_more);
								more_li.addView(txtdeviant);
								ln_deviant.setOrientation(LinearLayout.VERTICAL);
								ln_deviant.addView(more_li);
								ln_deviant.addView(btn_line);
								ln_deviant.addView(p_deviant);
		
								ln_deviant.setEnabled(false);
		
								linear_main.addView(ln_deviant);
								deviantadapter = new GridDeviantAdapter(list_thumb,
										bitmap_p_deviant, ctx);
								p_deviant.setNumColumns(cols);
								p_deviant.setAdapter(deviantadapter);
								p_deviant.setDrawingCacheEnabled(true);
								listService.add("public_deviant");
		
							}
							btn_public_deviant_more
									.setImageResource(R.drawable.more_disable);
							btn_public_deviant_more.setEnabled(false);
							deviant_count = 1;
		
							deviantadapter.removeItem();
							tmp_list = DeviantArt.getPublic(ctx, "DeviantArt");
							Log.d("thong", "DeviantArt Public: " + tmp_list.size());
							// Don't save exist photos
							ArrayList<RSSPhotoItem> list_save = new ArrayList<RSSPhotoItem>();
							for (int t = 0; t < tmp_list.size(); t++) {
								String url = tmp_list.get(t).getURL();
								String filepath = rss_folder.getAbsolutePath()
										+ "/"
										+ url.toLowerCase().replace("://", "")
												.replace("/", "_");
								File f = new File(filepath);
								if (!f.exists()) {
									list_save.add(tmp_list.get(t));
								}
							}
							if (list_save.size() == 0) {
								ln_deviant.setLayoutParams(p_zero);
							} else if (list_save.size() <= 3) {
								ln_deviant.setLayoutParams(p_one_row);
							} else
								ln_deviant.setLayoutParams(p_two_row);
							list_photos.addAll(tmp_list);
							deviant_list_photos.clear();
							deviant_list_photos.addAll(list_save);
		
							array_list.add(deviant_list_photos);
							if (!checkArray(PhimpMe.phimpme_array_list,deviant_list_photos)) {
								PhimpMe.phimpme_array_list.add(deviant_list_photos);
							} else {
								deleteItem(PhimpMe.phimpme_array_list,deviant_list_photos);
								PhimpMe.phimpme_array_list.add(deviant_list_photos);
							}
							/*for (int i = 0; i < array_list.size(); i++) {
								if (array_list.get(i).size() > 0) {
									Log.d("thong", "RunOnUiThread");
									final RSSPhotoItem[] tmp;
									if (array_list.get(i).size() < NUMBER_PHOTO_NEED_DOWNLOAD) {
										tmp = new RSSPhotoItem[array_list.get(i).size()];
									} else {
										tmp = new RSSPhotoItem[NUMBER_PHOTO_NEED_DOWNLOAD];
									}
									for (int j = 0; j < tmp.length; j++) {
										tmp[j] = array_list.get(i).get(j);
									}
									getActivity().runOnUiThread(new Runnable() {
										@Override
										public void run() {

											new DownloadImageAsyncTask().execute(tmp);
										}
									});

								}
							}*/
							Log.d("thong", "RunOnUiThread");
							final RSSPhotoItem[] tmp;
							if (deviant_list_photos.size() < NUMBER_PHOTO_NEED_DOWNLOAD) {
								tmp = new RSSPhotoItem[deviant_list_photos.size()];
							} else {
								tmp = new RSSPhotoItem[NUMBER_PHOTO_NEED_DOWNLOAD];
							}
							for (int j = 0; j < tmp.length; j++) {
								tmp[j] = deviant_list_photos.get(j);
							}
							getActivity().runOnUiThread(new Runnable() {
								@Override
								public void run() {

									new DownloadImageAsyncTask().execute(tmp);
								}
							});
							btn_public_deviant_more
									.setOnClickListener(new OnClickListener() {
										@Override
										public void onClick(View v) {
											btn_public_deviant_more
													.setImageResource(R.drawable.more_disable);
											btn_public_deviant_more.setEnabled(false);
											if (deviant_list_photos.size()
													- deviant_count
													* NUMBER_PHOTO_NEED_DOWNLOAD > 3) {
												deviant_rows_display = 2;
											} else if ((deviant_list_photos.size() - deviant_count
													* NUMBER_PHOTO_NEED_DOWNLOAD) > 0)
												deviant_rows_display = 1;
		
											int hight_display = ln_deviant.getHeight()
													+ DEFAULT_THUMBNAIL_SIZE
													* deviant_rows_display;
											LinearLayout.LayoutParams rep = new LinearLayout.LayoutParams(
													ViewGroup.LayoutParams.WRAP_CONTENT,
													hight_display);
											ln_deviant.setLayoutParams(rep);
											ln_deviant.setEnabled(true);
											moreClick(deviant_list_photos,
													deviant_count);
											deviant_count++;
										}
									});
						}});

				}
				if (PhimpMe.FEEDS_LIST_IMGUR_PUBLIC) {
					if (listService.indexOf("public_imgur") == -1) {
						Button btn_line = new Button(ctx);
						btn_line.setHeight(2);
						btn_line.setWidth(LayoutParams.MATCH_PARENT);
						btn_line.setBackgroundResource(color_line);

						txtpublic_imgur = new TextView(getContext());
						txtpublic_imgur.setText("Public Imgur");
						txtpublic_imgur.setTextSize(text_size);
						gv_public_imgur = new GridView(getContext());
						gv_public_imgur.setPadding(0, 10, 0, 0);
						ln_public_imgur = new LinearLayout(getContext());
						btn_public_imgur_more = new ImageButton(getContext());
						btn_public_imgur_more
								.setImageResource(R.drawable.more_disable);
						btn_public_imgur_more.setEnabled(false);

						RelativeLayout more_li = new RelativeLayout(getContext());
						btn_public_imgur_more.setLayoutParams(lp_more);
						more_li.addView(btn_public_imgur_more);
						more_li.addView(txtpublic_imgur);
						ln_public_imgur.setOrientation(LinearLayout.VERTICAL);
						ln_public_imgur.addView(more_li);
						ln_public_imgur.addView(btn_line);
						ln_public_imgur.addView(gv_public_imgur);

						ln_public_imgur.setEnabled(false);
						linear_main.addView(ln_public_imgur);

						Log.d("luong test", bitmap_public_imgur.size() + "");
						public_imguradapter = new GridImgurPublicAdaper(
								list_thumb, bitmap_public_imgur, ctx);
						gv_public_imgur.setNumColumns(cols);
						gv_public_imgur.setAdapter(public_imguradapter);
						gv_public_imgur.setDrawingCacheEnabled(true);
						listService.add("public_imgur");
					}
					btn_public_imgur_more
							.setImageResource(R.drawable.more_disable);
					btn_public_imgur_more.setEnabled(false);
					public_imgur_count = 1;
					public_imguradapter.removeItem();
					tmp_list = Imgur.getPublic(ctx, "");
					Log.d("Danh", "Imgur Public: " + tmp_list.size());
					list_photos.addAll(tmp_list);
					// Don't save exist photos
					ArrayList<RSSPhotoItem> list_save = new ArrayList<RSSPhotoItem>();
					for (int t = 0; t < tmp_list.size(); t++) {
						String url = tmp_list.get(t).getURL();
						String filepath = rss_folder.getAbsolutePath()
								+ "/"
								+ url.toLowerCase().replace("://", "")
										.replace("/", "_");
						File f = new File(filepath);
						if (!f.exists()) {
							list_save.add(tmp_list.get(t));
						}
					}
					if (list_save.size() == 0) {
						ln_public_imgur.setLayoutParams(p_zero);
					} else if (list_save.size() <= 3) {
						ln_public_imgur.setLayoutParams(p_one_row);
					} else
						ln_public_imgur.setLayoutParams(p_two_row);
					public_imgur_list_photos.clear();
					public_imgur_list_photos.addAll(list_save);
					array_list.add(public_imgur_list_photos);
					if (!checkArray(PhimpMe.phimpme_array_list,public_imgur_list_photos)) {
						PhimpMe.phimpme_array_list.add(public_imgur_list_photos);
					} else {
						deleteItem(PhimpMe.phimpme_array_list,public_imgur_list_photos);
						PhimpMe.phimpme_array_list
								.add(public_imgur_list_photos);
					}
					btn_public_imgur_more
							.setOnClickListener(new OnClickListener() {
								@Override
								public void onClick(View v) {
									btn_public_imgur_more
											.setImageResource(R.drawable.more_disable);
									btn_public_imgur_more.setEnabled(false);
									Log.d("luong test hight1",
											flickr_rows_display + "");
									if ((public_imgur_list_photos.size() - public_imgur_count
											* NUMBER_PHOTO_NEED_DOWNLOAD) > 3) {
										public_imgur_rows_display = 2;
									} else if ((public_imgur_list_photos.size() - public_imgur_count
											* NUMBER_PHOTO_NEED_DOWNLOAD) > 0)
										public_imgur_rows_display = 1;

									int hight_display = ln_public_imgur
											.getHeight()
											+ DEFAULT_THUMBNAIL_SIZE
											* public_imgur_rows_display;
									LinearLayout.LayoutParams rep = new LinearLayout.LayoutParams(
											ViewGroup.LayoutParams.WRAP_CONTENT,
											hight_display);
									ln_public_imgur.setLayoutParams(rep);
									ln_public_imgur.setEnabled(true);
									moreClick(public_imgur_list_photos,
											public_imgur_count);
									public_imgur_count++;
									Log.d("luong test hight2",
											public_imgur_rows_display + "");
								}
							});

				}
				if (PhimpMe.FEEDS_LIST_500PX_PUBLIC) {
					if (listService.indexOf("public_500px") == -1) {
						Button btn_line = new Button(ctx);
						btn_line.setHeight(2);
						btn_line.setWidth(LayoutParams.MATCH_PARENT);
						btn_line.setBackgroundResource(color_line);

						txtPublic500px = new TextView(getContext());
						txtPublic500px.setText("Public 500px");
						txtPublic500px.setTextSize(text_size);
						p_500px = new GridView(getContext());
						p_500px.addView(btn_line);
						ln_public_500px = new LinearLayout(getContext());
						btn_public_500px_more = new ImageButton(getContext());
						btn_public_500px_more
								.setImageResource(R.drawable.more_disable);
						btn_public_500px_more.setEnabled(false);

						RelativeLayout more_li = new RelativeLayout(getContext());
						btn_public_500px_more.setLayoutParams(lp_more);
						more_li.addView(btn_public_500px_more);
						more_li.addView(txtPublic500px);
						ln_public_500px.setOrientation(LinearLayout.VERTICAL);
						ln_public_500px.addView(more_li);
						ln_public_500px.addView(btn_line);
						ln_public_500px.addView(p_500px);

						ln_public_500px.setEnabled(false);

						linear_main.addView(ln_public_500px);
						public500pxadapter = new GridPublic500pxAdaper(
								list_thumb, bitmap_public_500px, ctx);

						p_500px.setNumColumns(cols);
						p_500px.setAdapter(public500pxadapter);
						p_500px.setDrawingCacheEnabled(true);
						listService.add("public_500px");

					}
					btn_public_500px_more
							.setImageResource(R.drawable.more_disable);
					btn_public_500px_more.setEnabled(false);
					public_500px_count = 1;
					public500pxadapter.removeItem();
					tmp_list = s500px.get500pxPublic(ctx);
					// Don't save exist photos
					ArrayList<RSSPhotoItem> list_save = new ArrayList<RSSPhotoItem>();
					for (int t = 0; t < tmp_list.size(); t++) {
						String url = tmp_list.get(t).getURL();
						String filepath = rss_folder.getAbsolutePath()
								+ "/"
								+ url.toLowerCase().replace("://", "")
										.replace("/", "_");
						File f = new File(filepath);
						if (!f.exists()) {
							list_save.add(tmp_list.get(t));
						}
					}
					//
					if (list_save.size() == 0) {
						ln_public_500px.setLayoutParams(p_zero);
					} else if (list_save.size() <= 3) {
						ln_public_500px.setLayoutParams(p_one_row);
					} else
						ln_public_500px.setLayoutParams(p_two_row);
					public_500px_list_photos.clear();
					public_500px_list_photos.addAll(list_save);
					array_list.add(public_500px_list_photos);
					if (!checkArray(PhimpMe.phimpme_array_list,public_500px_list_photos)) {
						PhimpMe.phimpme_array_list
								.add(public_500px_list_photos);
					} else {
						deleteItem(PhimpMe.phimpme_array_list,public_500px_list_photos);
						PhimpMe.phimpme_array_list
								.add(public_500px_list_photos);
					}
					btn_public_500px_more
							.setOnClickListener(new OnClickListener() {
								@Override
								public void onClick(View v) {
									btn_public_500px_more
											.setImageResource(R.drawable.more_disable);
									btn_public_500px_more.setEnabled(false);
									if (public_500px_list_photos.size()
											- public_500px_count
											* NUMBER_PHOTO_NEED_DOWNLOAD > 3) {
										public_500px_rows_display = 2;
									} else if ((public_500px_list_photos.size() - public_500px_count
											* NUMBER_PHOTO_NEED_DOWNLOAD) > 0)
										public_500px_rows_display = 1;

									int hight_display = ln_public_500px
											.getHeight()
											+ DEFAULT_THUMBNAIL_SIZE
											* public_500px_rows_display;
									LinearLayout.LayoutParams rep = new LinearLayout.LayoutParams(
											ViewGroup.LayoutParams.WRAP_CONTENT,
											hight_display);
									ln_public_500px.setLayoutParams(rep);
									ln_public_500px.setEnabled(true);
									moreClick(public_500px_list_photos,
											public_500px_count);
									public_500px_count++;
								}
							});
				}
				if (PhimpMe.FEEDS_LIST_MYSERVICES) {					
					if (listService.indexOf("my_feed_services") == -1) {
						myfeed_download=true;
						Button btn_line = new Button(ctx);
						btn_line.setHeight(2);
						btn_line.setWidth(LayoutParams.MATCH_PARENT);
						btn_line.setBackgroundResource(color_line);

						txtMyFeedServices = new TextView(getContext());
						txtMyFeedServices.setText("My Feed Photo");
						txtMyFeedServices.setTextSize(text_size);
						gv_my_feed_services = new GridView(getContext());
						gv_my_feed_services.setPadding(0, 10, 0, 0);
						ln_my_feed_services = new LinearLayout(getContext());
						btn_my_feed_services_more = new ImageButton(getContext());
						btn_my_feed_services_more
								.setImageResource(R.drawable.more_disable);
						btn_my_feed_services_more.setEnabled(false);

						RelativeLayout more_li = new RelativeLayout(getContext());
						btn_my_feed_services_more.setLayoutParams(lp_more);
						more_li.addView(btn_my_feed_services_more);
						more_li.addView(txtMyFeedServices);
						ln_my_feed_services
								.setOrientation(LinearLayout.VERTICAL);
						ln_my_feed_services.addView(more_li);
						ln_my_feed_services.addView(btn_line);
						ln_my_feed_services.addView(gv_my_feed_services);

						ln_my_feed_services.setEnabled(false);

						linear_main.addView(ln_my_feed_services);
						Log.d("luong test", bitmap_my_feed_services.size() + "");
						my_feed_services_adapter = new GridMyFeedServicesAdaper(
								list_thumb, bitmap_my_feed_services, ctx);
						gv_my_feed_services.setNumColumns(cols);
						gv_my_feed_services
								.setAdapter(my_feed_services_adapter);
						gv_my_feed_services.setDrawingCacheEnabled(true);
						listService.add("my_feed_services");
					}
					btn_my_feed_services_more
							.setImageResource(R.drawable.more_disable);
					btn_my_feed_services_more.setEnabled(false);
					my_feed_services_count = 1;
					my_feed_services_adapter.removeItem();
					tmp_list = MyFeedServices.getPublic(ctx, "");
					Log.d("thong", "My Feed : " + tmp_list.size());
					list_photos.addAll(tmp_list);
					// Don't save exist photos
					ArrayList<RSSPhotoItem> list_save = new ArrayList<RSSPhotoItem>();
					for (int t = 0; t < tmp_list.size(); t++) {
						String url = tmp_list.get(t).getURL();
						String filepath = rss_folder.getAbsolutePath()
								+ "/"
								+ url.toLowerCase().replace("://", "")
										.replace("/", "_");
						File f = new File(filepath);
						if (!f.exists()) {
							list_save.add(tmp_list.get(t));
						}
					}
					//
					if (list_save.size() == 0) {
						ln_my_feed_services.setLayoutParams(p_zero);
					} else if (list_save.size() <= 3) {
						ln_my_feed_services.setLayoutParams(p_one_row);
					} else
						ln_my_feed_services.setLayoutParams(p_two_row);
					my_feed_services_list_photos.clear();
					my_feed_services_list_photos.addAll(list_save);
					array_list.add(my_feed_services_list_photos);
					if (!checkArray(PhimpMe.phimpme_array_list,my_feed_services_list_photos)) {
						PhimpMe.phimpme_array_list
								.add(my_feed_services_list_photos);
					} else {
						deleteItem(PhimpMe.phimpme_array_list,my_feed_services_list_photos);
						PhimpMe.phimpme_array_list
								.add(my_feed_services_list_photos);
					}
					btn_my_feed_services_more
							.setOnClickListener(new OnClickListener() {
								@Override
								public void onClick(View v) {
									btn_my_feed_services_more
											.setImageResource(R.drawable.more_disable);
									btn_my_feed_services_more.setEnabled(false);
									Log.d("luong test hight1",
											my_feed_services_rows_display + "");
									if ((my_feed_services_list_photos.size() - my_feed_services_count
											* NUMBER_PHOTO_NEED_DOWNLOAD) > 3) {
										my_feed_services_rows_display = 2;
									} else if ((my_feed_services_list_photos
											.size() - my_feed_services_count
											* NUMBER_PHOTO_NEED_DOWNLOAD) > 0)
										my_feed_services_rows_display = 1;

									int hight_display = ln_my_feed_services
											.getHeight()
											+ DEFAULT_THUMBNAIL_SIZE
											* my_feed_services_rows_display;
									LinearLayout.LayoutParams rep = new LinearLayout.LayoutParams(
											ViewGroup.LayoutParams.WRAP_CONTENT,
											hight_display);
									ln_my_feed_services.setLayoutParams(rep);
									ln_my_feed_services.setEnabled(true);
									moreClick(my_feed_services_list_photos,
											my_feed_services_count);
									my_feed_services_count++;
									Log.d("luong test hight2",
											my_feed_services_rows_display + "");
								}
							});

				}
				/*
				 * My feed services 1
				 */
				if (PhimpMe.FEEDS_LIST_MYSERVICES1) {
					if (listService.indexOf("my_feed_services1") == -1) {
						myfeed_download1=true;
						Button btn_line = new Button(ctx);
						btn_line.setHeight(2);
						btn_line.setWidth(LayoutParams.MATCH_PARENT);
						btn_line.setBackgroundResource(color_line);

						txtMyFeedServices1 = new TextView(getContext());
						txtMyFeedServices1.setText("My Feed Photo");
						txtMyFeedServices1.setTextSize(text_size);
						gv_my_feed_services1 = new GridView(getContext());
						gv_my_feed_services1.setPadding(0, 10, 0, 0);
						ln_my_feed_services1 = new LinearLayout(getContext());
						btn_my_feed_services_more1 = new ImageButton(getContext());
						btn_my_feed_services_more1
								.setImageResource(R.drawable.more_disable);
						btn_my_feed_services_more1.setEnabled(false);

						RelativeLayout more_li = new RelativeLayout(getContext());
						btn_my_feed_services_more1.setLayoutParams(lp_more);
						more_li.addView(btn_my_feed_services_more1);
						more_li.addView(txtMyFeedServices1);
						ln_my_feed_services1
								.setOrientation(LinearLayout.VERTICAL);
						ln_my_feed_services1.addView(more_li);
						ln_my_feed_services1.addView(btn_line);
						ln_my_feed_services1.addView(gv_my_feed_services1);

						ln_my_feed_services1.setEnabled(false);

						linear_main.addView(ln_my_feed_services1);
						Log.d("luong test", bitmap_my_feed_services1.size()
								+ "");
						my_feed_services_adapter1 = new GridMyFeedServicesAdaper1(
								list_thumb, bitmap_my_feed_services1, ctx);
						gv_my_feed_services1.setNumColumns(cols);
						gv_my_feed_services1
								.setAdapter(my_feed_services_adapter1);
						gv_my_feed_services1.setDrawingCacheEnabled(true);
						listService.add("my_feed_services1");
					}
					btn_my_feed_services_more1
							.setImageResource(R.drawable.more_disable);
					btn_my_feed_services_more1.setEnabled(false);
					my_feed_services_count1 = 1;
					my_feed_services_adapter1.removeItem();
					tmp_list = MyFeedServices.getPublic1(ctx, "");
					Log.d("thong", "My Feed 1: " + tmp_list.size());
					list_photos.addAll(tmp_list);
					// Don't save exist photos
					ArrayList<RSSPhotoItem> list_save = new ArrayList<RSSPhotoItem>();
					for (int t = 0; t < tmp_list.size(); t++) {
						String url = tmp_list.get(t).getURL();
						String filepath = rss_folder.getAbsolutePath()
								+ "/"
								+ url.toLowerCase().replace("://", "")
										.replace("/", "_");
						File f = new File(filepath);
						if (!f.exists()) {
							list_save.add(tmp_list.get(t));
						}
					}

					if (list_save.size() == 0) {
						ln_my_feed_services1.setLayoutParams(p_zero);
					} else if (list_save.size() <= 3) {
						ln_my_feed_services1.setLayoutParams(p_one_row);
					} else
						ln_my_feed_services1.setLayoutParams(p_two_row);
					my_feed_services_list_photos1.clear();
					my_feed_services_list_photos1.addAll(list_save);
					array_list.add(my_feed_services_list_photos1);
					if (!checkArray(PhimpMe.phimpme_array_list,my_feed_services_list_photos1)) {
						PhimpMe.phimpme_array_list
								.add(my_feed_services_list_photos1);
					} else {
						deleteItem(PhimpMe.phimpme_array_list,my_feed_services_list_photos1);
						PhimpMe.phimpme_array_list
								.add(my_feed_services_list_photos1);
					}
					btn_my_feed_services_more1
							.setOnClickListener(new OnClickListener() {
								@Override
								public void onClick(View v) {
									btn_my_feed_services_more1
											.setImageResource(R.drawable.more_disable);
									btn_my_feed_services_more1
											.setEnabled(false);
									Log.d("luong test hight1",
											my_feed_services_rows_display1 + "");
									if ((my_feed_services_list_photos1.size() - my_feed_services_count1
											* NUMBER_PHOTO_NEED_DOWNLOAD) > 3) {
										my_feed_services_rows_display1 += 2;
									} else if ((my_feed_services_list_photos1
											.size() - my_feed_services_count1
											* NUMBER_PHOTO_NEED_DOWNLOAD) > 0)
										my_feed_services_rows_display1 += 1;

									int hight_display = DEFAULT_THUMBNAIL_SIZE
											* my_feed_services_rows_display1
											+ 100;
									LinearLayout.LayoutParams rep = new LinearLayout.LayoutParams(
											ViewGroup.LayoutParams.WRAP_CONTENT,
											hight_display);
									ln_my_feed_services1.setLayoutParams(rep);
									ln_my_feed_services1.setEnabled(true);
									moreClick(my_feed_services_list_photos1,
											my_feed_services_count1);
									my_feed_services_count1++;
									Log.d("luong test hight2",
											my_feed_services_rows_display1 + "");
								}
							});
				}
				/*
				 * My feed services 2
				 */
				if (PhimpMe.FEEDS_LIST_MYSERVICES2) {
					if (listService.indexOf("my_feed_services2") == -1) {
						myfeed_download2=true;
						Button btn_line = new Button(ctx);
						btn_line.setHeight(2);
						btn_line.setWidth(LayoutParams.MATCH_PARENT);
						btn_line.setBackgroundResource(color_line);

						txtMyFeedServices2 = new TextView(getContext());
						txtMyFeedServices2.setText("My Feed Photo");
						txtMyFeedServices2.setTextSize(text_size);
						gv_my_feed_services2 = new GridView(getContext());
						gv_my_feed_services2.setPadding(0, 10, 0, 0);
						ln_my_feed_services2 = new LinearLayout(getContext());
						btn_my_feed_services_more2 = new ImageButton(getContext());
						btn_my_feed_services_more2
								.setImageResource(R.drawable.more_disable);
						btn_my_feed_services_more2.setEnabled(false);

						RelativeLayout more_li = new RelativeLayout(getContext());
						btn_my_feed_services_more2.setLayoutParams(lp_more);
						more_li.addView(btn_my_feed_services_more2);
						more_li.addView(txtMyFeedServices2);
						ln_my_feed_services2
								.setOrientation(LinearLayout.VERTICAL);
						ln_my_feed_services2.addView(more_li);
						ln_my_feed_services2.addView(btn_line);
						ln_my_feed_services2.addView(gv_my_feed_services2);

						ln_my_feed_services2.setEnabled(false);

						linear_main.addView(ln_my_feed_services2);
						Log.d("luong test", bitmap_my_feed_services2.size()
								+ "");
						my_feed_services_adapter2 = new GridMyFeedServicesAdaper2(
								list_thumb, bitmap_my_feed_services2, ctx);
						gv_my_feed_services2.setNumColumns(cols);
						gv_my_feed_services2
								.setAdapter(my_feed_services_adapter2);
						gv_my_feed_services2.setDrawingCacheEnabled(true);
						listService.add("my_feed_services2");
					}
					btn_my_feed_services_more2
							.setImageResource(R.drawable.more_disable);
					btn_my_feed_services_more2.setEnabled(false);
					my_feed_services_count2 = 1;
					my_feed_services_adapter2.removeItem();
					tmp_list = MyFeedServices.getPublic2(ctx, "");
					Log.d("thong", "My Feed 2: " + tmp_list.size());
					list_photos.addAll(tmp_list);
					// Don't save exist photos
					ArrayList<RSSPhotoItem> list_save = new ArrayList<RSSPhotoItem>();
					for (int t = 0; t < tmp_list.size(); t++) {
						String url = tmp_list.get(t).getURL();
						String filepath = rss_folder.getAbsolutePath()
								+ "/"
								+ url.toLowerCase().replace("://", "")
										.replace("/", "_");
						File f = new File(filepath);
						if (!f.exists()) {
							list_save.add(tmp_list.get(t));
						}
					}

					if (list_save.size() == 0) {
						ln_my_feed_services2.setLayoutParams(p_zero);
					} else if (list_save.size() <= 3) {
						ln_my_feed_services2.setLayoutParams(p_one_row);
					} else
						ln_my_feed_services2.setLayoutParams(p_two_row);
					my_feed_services_list_photos2.clear();
					my_feed_services_list_photos2.addAll(list_save);
					array_list.add(my_feed_services_list_photos2);
					if (!checkArray(PhimpMe.phimpme_array_list,my_feed_services_list_photos2)) {
						PhimpMe.phimpme_array_list
								.add(my_feed_services_list_photos2);
					} else {
						deleteItem(PhimpMe.phimpme_array_list,my_feed_services_list_photos2);
						PhimpMe.phimpme_array_list
								.add(my_feed_services_list_photos2);
					}
					btn_my_feed_services_more2
							.setOnClickListener(new OnClickListener() {
								@Override
								public void onClick(View v) {
									btn_my_feed_services_more2
											.setImageResource(R.drawable.more_disable);
									btn_my_feed_services_more2
											.setEnabled(false);
									Log.d("luong test hight2",
											my_feed_services_rows_display2 + "");
									if ((my_feed_services_list_photos2.size() - my_feed_services_count2
											* NUMBER_PHOTO_NEED_DOWNLOAD) > 3) {
										my_feed_services_rows_display2 += 2;
									} else if ((my_feed_services_list_photos2
											.size() - my_feed_services_count2
											* NUMBER_PHOTO_NEED_DOWNLOAD) > 0)
										my_feed_services_rows_display2 += 1;

									int hight_display = DEFAULT_THUMBNAIL_SIZE
											* my_feed_services_rows_display2
											+ 100;
									LinearLayout.LayoutParams rep = new LinearLayout.LayoutParams(
											ViewGroup.LayoutParams.WRAP_CONTENT,
											hight_display);
									ln_my_feed_services2.setLayoutParams(rep);
									ln_my_feed_services2.setEnabled(true);
									moreClick(my_feed_services_list_photos2,
											my_feed_services_count2);
									my_feed_services_count2++;
									Log.d("luong test hight2",
											my_feed_services_rows_display2 + "");
								}
							});
				}
				/*
				 * My feed services 3
				 */
				if (PhimpMe.FEEDS_LIST_MYSERVICES3) {
					if (listService.indexOf("my_feed_services3") == -1) {
						myfeed_download3=true;
						Button btn_line = new Button(ctx);
						btn_line.setHeight(2);
						btn_line.setWidth(LayoutParams.MATCH_PARENT);
						btn_line.setBackgroundResource(color_line);

						txtMyFeedServices3 = new TextView(getContext());
						txtMyFeedServices3.setText("My Feed Photo");
						txtMyFeedServices3.setTextSize(text_size);
						gv_my_feed_services3 = new GridView(getContext());
						gv_my_feed_services3.setPadding(0, 10, 0, 0);
						ln_my_feed_services3 = new LinearLayout(getContext());
						btn_my_feed_services_more3 = new ImageButton(getContext());
						btn_my_feed_services_more3
								.setImageResource(R.drawable.more_disable);
						btn_my_feed_services_more3.setEnabled(false);

						RelativeLayout more_li = new RelativeLayout(getContext());
						btn_my_feed_services_more3.setLayoutParams(lp_more);
						more_li.addView(btn_my_feed_services_more3);
						more_li.addView(txtMyFeedServices3);
						ln_my_feed_services3
								.setOrientation(LinearLayout.VERTICAL);
						ln_my_feed_services3.addView(more_li);
						ln_my_feed_services3.addView(btn_line);
						ln_my_feed_services3.addView(gv_my_feed_services3);

						ln_my_feed_services3.setEnabled(false);

						linear_main.addView(ln_my_feed_services3);
						Log.d("luong test", bitmap_my_feed_services3.size()
								+ "");
						my_feed_services_adapter3 = new GridMyFeedServicesAdaper3(
								list_thumb, bitmap_my_feed_services3, ctx);
						gv_my_feed_services3.setNumColumns(cols);
						gv_my_feed_services3
								.setAdapter(my_feed_services_adapter3);
						gv_my_feed_services3.setDrawingCacheEnabled(true);
						listService.add("my_feed_services3");
					}
					btn_my_feed_services_more3
							.setImageResource(R.drawable.more_disable);
					btn_my_feed_services_more3.setEnabled(false);
					my_feed_services_count3 = 1;
					my_feed_services_adapter3.removeItem();
					tmp_list = MyFeedServices.getPublic3(ctx, "");
					Log.d("thong", "My Feed 3: " + tmp_list.size());
					list_photos.addAll(tmp_list);
					// Don't save exist photos
					ArrayList<RSSPhotoItem> list_save = new ArrayList<RSSPhotoItem>();
					for (int t = 0; t < tmp_list.size(); t++) {
						String url = tmp_list.get(t).getURL();
						String filepath = rss_folder.getAbsolutePath()
								+ "/"
								+ url.toLowerCase().replace("://", "")
										.replace("/", "_");
						File f = new File(filepath);
						if (!f.exists()) {
							list_save.add(tmp_list.get(t));
						}
					}

					if (list_save.size() == 0) {
						ln_my_feed_services3.setLayoutParams(p_zero);
					} else if (list_save.size() <= 3) {
						ln_my_feed_services3.setLayoutParams(p_one_row);
					} else
						ln_my_feed_services3.setLayoutParams(p_two_row);
					my_feed_services_list_photos3.clear();
					my_feed_services_list_photos3.addAll(list_save);
					array_list.add(my_feed_services_list_photos3);
					if (!checkArray(PhimpMe.phimpme_array_list,my_feed_services_list_photos3)) {
						PhimpMe.phimpme_array_list
								.add(my_feed_services_list_photos3);
					} else {
						deleteItem(PhimpMe.phimpme_array_list,my_feed_services_list_photos3);
						PhimpMe.phimpme_array_list
								.add(my_feed_services_list_photos3);
					}
					btn_my_feed_services_more3
							.setOnClickListener(new OnClickListener() {
								@Override
								public void onClick(View v) {
									btn_my_feed_services_more3
											.setImageResource(R.drawable.more_disable);
									btn_my_feed_services_more3
											.setEnabled(false);
									Log.d("luong test hight3",
											my_feed_services_rows_display3 + "");
									if ((my_feed_services_list_photos3.size() - my_feed_services_count3
											* NUMBER_PHOTO_NEED_DOWNLOAD) > 3) {
										my_feed_services_rows_display3 += 2;
									} else if ((my_feed_services_list_photos3
											.size() - my_feed_services_count3
											* NUMBER_PHOTO_NEED_DOWNLOAD) > 0)
										my_feed_services_rows_display3 += 1;

									int hight_display = DEFAULT_THUMBNAIL_SIZE
											* my_feed_services_rows_display3
											+ 100;
									LinearLayout.LayoutParams rep = new LinearLayout.LayoutParams(
											ViewGroup.LayoutParams.WRAP_CONTENT,
											hight_display);
									ln_my_feed_services3.setLayoutParams(rep);
									ln_my_feed_services3.setEnabled(true);
									moreClick(my_feed_services_list_photos3,
											my_feed_services_count3);
									my_feed_services_count3++;
									Log.d("luong test hight3",
											my_feed_services_rows_display3 + "");
								}
							});
				}
				/*
				 * My feed services 4
				 */
				if (PhimpMe.FEEDS_LIST_MYSERVICES4) {
					if (listService.indexOf("my_feed_services4") == -1) {
						myfeed_download4=true;
						Button btn_line = new Button(ctx);
						btn_line.setHeight(2);
						btn_line.setWidth(LayoutParams.MATCH_PARENT);
						btn_line.setBackgroundResource(color_line);

						txtMyFeedServices4 = new TextView(getContext());
						txtMyFeedServices4.setText("My Feed Photo");
						txtMyFeedServices4.setTextSize(text_size);
						gv_my_feed_services4 = new GridView(getContext());
						gv_my_feed_services4.setPadding(0, 10, 0, 0);
						ln_my_feed_services4 = new LinearLayout(getContext());
						btn_my_feed_services_more4 = new ImageButton(getContext());
						btn_my_feed_services_more4
								.setImageResource(R.drawable.more_disable);
						btn_my_feed_services_more4.setEnabled(false);

						RelativeLayout more_li = new RelativeLayout(getContext());
						btn_my_feed_services_more4.setLayoutParams(lp_more);
						more_li.addView(btn_my_feed_services_more4);
						more_li.addView(txtMyFeedServices4);
						ln_my_feed_services4
								.setOrientation(LinearLayout.VERTICAL);
						ln_my_feed_services4.addView(more_li);
						ln_my_feed_services4.addView(btn_line);
						ln_my_feed_services4.addView(gv_my_feed_services4);

						ln_my_feed_services4.setEnabled(false);

						linear_main.addView(ln_my_feed_services4);
						Log.d("luong test", bitmap_my_feed_services4.size()
								+ "");
						my_feed_services_adapter4 = new GridMyFeedServicesAdaper4(
								list_thumb, bitmap_my_feed_services4, ctx);
						gv_my_feed_services4.setNumColumns(cols);
						gv_my_feed_services4
								.setAdapter(my_feed_services_adapter4);
						gv_my_feed_services4.setDrawingCacheEnabled(true);
						listService.add("my_feed_services4");
					}
					btn_my_feed_services_more4
							.setImageResource(R.drawable.more_disable);
					btn_my_feed_services_more4.setEnabled(false);
					my_feed_services_count4 = 1;
					my_feed_services_adapter4.removeItem();
					tmp_list = MyFeedServices.getPublic4(ctx, "");
					Log.d("thong", "My Feed 4: " + tmp_list.size());
					list_photos.addAll(tmp_list);
					// Don't save exist photos
					ArrayList<RSSPhotoItem> list_save = new ArrayList<RSSPhotoItem>();
					for (int t = 0; t < tmp_list.size(); t++) {
						String url = tmp_list.get(t).getURL();
						String filepath = rss_folder.getAbsolutePath()
								+ "/"
								+ url.toLowerCase().replace("://", "")
										.replace("/", "_");
						File f = new File(filepath);
						if (!f.exists()) {
							list_save.add(tmp_list.get(t));
						}
					}

					if (list_save.size() == 0) {
						ln_my_feed_services4.setLayoutParams(p_zero);
					} else if (list_save.size() <= 3) {
						ln_my_feed_services4.setLayoutParams(p_one_row);
					} else
						ln_my_feed_services4.setLayoutParams(p_two_row);
					my_feed_services_list_photos4.clear();
					my_feed_services_list_photos4.addAll(list_save);
					array_list.add(my_feed_services_list_photos4);
					if (!checkArray(PhimpMe.phimpme_array_list,my_feed_services_list_photos4)) {
						PhimpMe.phimpme_array_list
								.add(my_feed_services_list_photos4);
					} else {
						deleteItem(PhimpMe.phimpme_array_list,my_feed_services_list_photos4);
						PhimpMe.phimpme_array_list
								.add(my_feed_services_list_photos4);
					}
					btn_my_feed_services_more4
							.setOnClickListener(new OnClickListener() {
								@Override
								public void onClick(View v) {
									btn_my_feed_services_more4
											.setImageResource(R.drawable.more_disable);
									btn_my_feed_services_more4
											.setEnabled(false);
									Log.d("luong test hight4",
											my_feed_services_rows_display4 + "");
									if ((my_feed_services_list_photos4.size() - my_feed_services_count4
											* NUMBER_PHOTO_NEED_DOWNLOAD) > 3) {
										my_feed_services_rows_display4 += 2;
									} else if ((my_feed_services_list_photos4
											.size() - my_feed_services_count4
											* NUMBER_PHOTO_NEED_DOWNLOAD) > 0)
										my_feed_services_rows_display4 += 1;

									int hight_display = DEFAULT_THUMBNAIL_SIZE
											* my_feed_services_rows_display4
											+ 100;
									LinearLayout.LayoutParams rep = new LinearLayout.LayoutParams(
											ViewGroup.LayoutParams.WRAP_CONTENT,
											hight_display);
									ln_my_feed_services4.setLayoutParams(rep);
									ln_my_feed_services4.setEnabled(true);
									moreClick(my_feed_services_list_photos4,
											my_feed_services_count4);
									my_feed_services_count4++;
									Log.d("luong test hight4",
											my_feed_services_rows_display4 + "");
								}
							});
				}
				/*
				 * My feed services 5
				 */
				if (PhimpMe.FEEDS_LIST_MYSERVICES5) {
					if (listService.indexOf("my_feed_services5") == -1) {
						myfeed_download5=true;
						Button btn_line = new Button(ctx);
						btn_line.setHeight(2);
						btn_line.setWidth(LayoutParams.MATCH_PARENT);
						btn_line.setBackgroundResource(color_line);

						txtMyFeedServices5 = new TextView(getContext());
						txtMyFeedServices5.setText("My Feed Photo");
						txtMyFeedServices5.setTextSize(text_size);
						gv_my_feed_services5 = new GridView(getContext());
						gv_my_feed_services5.setPadding(0, 10, 0, 0);
						ln_my_feed_services5 = new LinearLayout(getContext());
						btn_my_feed_services_more5 = new ImageButton(getContext());
						btn_my_feed_services_more5
								.setImageResource(R.drawable.more_disable);
						btn_my_feed_services_more5.setEnabled(false);

						RelativeLayout more_li = new RelativeLayout(getContext());
						btn_my_feed_services_more5.setLayoutParams(lp_more);
						more_li.addView(btn_my_feed_services_more5);
						more_li.addView(txtMyFeedServices5);
						ln_my_feed_services5
								.setOrientation(LinearLayout.VERTICAL);
						ln_my_feed_services5.addView(more_li);
						ln_my_feed_services5.addView(btn_line);
						ln_my_feed_services5.addView(gv_my_feed_services5);

						ln_my_feed_services5.setEnabled(false);

						linear_main.addView(ln_my_feed_services5);
						Log.d("luong test", bitmap_my_feed_services5.size()
								+ "");
						my_feed_services_adapter5 = new GridMyFeedServicesAdaper5(
								list_thumb, bitmap_my_feed_services5, ctx);
						gv_my_feed_services5.setNumColumns(cols);
						gv_my_feed_services5
								.setAdapter(my_feed_services_adapter5);
						gv_my_feed_services5.setDrawingCacheEnabled(true);
						listService.add("my_feed_services5");
					}
					btn_my_feed_services_more5
							.setImageResource(R.drawable.more_disable);
					btn_my_feed_services_more5.setEnabled(false);
					my_feed_services_count5 = 1;
					my_feed_services_adapter5.removeItem();
					tmp_list = MyFeedServices.getPublic5(ctx, "");
					Log.d("thong", "My Feed 5: " + tmp_list.size());
					list_photos.addAll(tmp_list);
					// Don't save exist photos
					ArrayList<RSSPhotoItem> list_save = new ArrayList<RSSPhotoItem>();
					for (int t = 0; t < tmp_list.size(); t++) {
						String url = tmp_list.get(t).getURL();
						String filepath = rss_folder.getAbsolutePath()
								+ "/"
								+ url.toLowerCase().replace("://", "")
										.replace("/", "_");
						File f = new File(filepath);
						if (!f.exists()) {
							list_save.add(tmp_list.get(t));
						}
					}

					if (list_save.size() == 0) {
						ln_my_feed_services5.setLayoutParams(p_zero);
					} else if (list_save.size() <= 3) {
						ln_my_feed_services5.setLayoutParams(p_one_row);
					} else
						ln_my_feed_services5.setLayoutParams(p_two_row);
					my_feed_services_list_photos5.clear();
					my_feed_services_list_photos5.addAll(list_save);
					array_list.add(my_feed_services_list_photos5);
					if (!checkArray(PhimpMe.phimpme_array_list,my_feed_services_list_photos5)) {
						PhimpMe.phimpme_array_list
								.add(my_feed_services_list_photos5);
					} else {
						deleteItem(PhimpMe.phimpme_array_list,my_feed_services_list_photos5);
						PhimpMe.phimpme_array_list
								.add(my_feed_services_list_photos5);
					}
					btn_my_feed_services_more5
							.setOnClickListener(new OnClickListener() {
								@Override
								public void onClick(View v) {
									btn_my_feed_services_more5
											.setImageResource(R.drawable.more_disable);
									btn_my_feed_services_more5
											.setEnabled(false);
									Log.d("luong test hight5",
											my_feed_services_rows_display5 + "");
									if ((my_feed_services_list_photos5.size() - my_feed_services_count5
											* NUMBER_PHOTO_NEED_DOWNLOAD) > 3) {
										my_feed_services_rows_display5 += 2;
									} else if ((my_feed_services_list_photos5
											.size() - my_feed_services_count5
											* NUMBER_PHOTO_NEED_DOWNLOAD) > 0)
										my_feed_services_rows_display5 += 1;

									int hight_display = DEFAULT_THUMBNAIL_SIZE
											* my_feed_services_rows_display5
											+ 100;
									LinearLayout.LayoutParams rep = new LinearLayout.LayoutParams(
											ViewGroup.LayoutParams.WRAP_CONTENT,
											hight_display);
									ln_my_feed_services5.setLayoutParams(rep);
									ln_my_feed_services5.setEnabled(true);
									moreClick(my_feed_services_list_photos5,
											my_feed_services_count5);
									my_feed_services_count5++;
									Log.d("luong test hight5",
											my_feed_services_rows_display5 + "");
								}
							});
				}
				if (PhimpMe.FEEDS_LIST_FACEBOOK_PRIVATE) {
					if (listService.indexOf("personal_facebook") == -1) {
						facebook_download=true;
						Button btn_line = new Button(ctx);
						btn_line.setHeight(2);
						btn_line.setWidth(LayoutParams.MATCH_PARENT);
						btn_line.setBackgroundResource(color_line);

						txtfacebook = new TextView(getContext());
						txtfacebook.setText("Personal Facebook");
						txtfacebook.setTextSize(text_size);
						gv_personal_facebook = new GridView(getContext());
						gv_personal_facebook.setPadding(0, 10, 0, 0);
						ln_facebook = new LinearLayout(getContext());
						btn_facebook_more = new ImageButton(getContext());
						btn_facebook_more
								.setImageResource(R.drawable.more_disable);
						btn_facebook_more.setEnabled(false);

						RelativeLayout more_li = new RelativeLayout(getContext());
						btn_facebook_more.setLayoutParams(lp_more);
						more_li.addView(btn_facebook_more);
						more_li.addView(txtfacebook);
						ln_facebook.setOrientation(LinearLayout.VERTICAL);
						ln_facebook.addView(more_li);
						ln_facebook.addView(btn_line);
						ln_facebook.addView(gv_personal_facebook);

						ln_facebook.setEnabled(false);

						linear_main.addView(ln_facebook);
						facebookadapter = new GridFacebookAdapter(
								list_thumb_personal, bitmap_personal_facebook,
								ctx);
						gv_personal_facebook.setNumColumns(cols);
						gv_personal_facebook.setAdapter(facebookadapter);
						gv_personal_facebook.setDrawingCacheEnabled(true);
						listService.add("personal_facebook");
					}
					btn_facebook_more.setImageResource(R.drawable.more_disable);
					btn_facebook_more.setEnabled(false);
					personal_facebook_count = 1;
					facebookadapter.removeItem();
					tmp_list_personal = Facebook.getPrivatePhotos(ctx,
							"Personal Facebook");
					Log.d("thong",
							"Facebook Personal: " + tmp_list_personal.size());
					// Don't save exist photos
					ArrayList<RSSPhotoItem_Personal> list_save = new ArrayList<RSSPhotoItem_Personal>();
					for (int t = 0; t < tmp_list_personal.size(); t++) {
						String url = tmp_list_personal.get(t).getURL();
						String filepath = rss_folder.getAbsolutePath()
								+ "/"
								+ url.toLowerCase().replace("://", "")
										.replace("/", "_");
						File f = new File(filepath);
						if (!f.exists()) {
							list_save.add(tmp_list_personal.get(t));
						}
					}
					//
					if (list_save.size() == 0) {
						ln_facebook.setLayoutParams(p_zero);
					} else if (list_save.size() <= 3) {
						ln_facebook.setLayoutParams(p_one_row);
					} else
						ln_facebook.setLayoutParams(p_two_row);
					list_photos_personal.addAll(tmp_list_personal);
					personal_facebook_list_photos.clear();
					personal_facebook_list_photos.addAll(list_save);

					personal_array_list.add(personal_facebook_list_photos);
					if (!checkArray_Persional(PhimpMe.phimpme_personal_array_list,personal_facebook_list_photos)) {
						PhimpMe.phimpme_personal_array_list
								.add(personal_facebook_list_photos);
					} else {
						deleteItem_Persional(PhimpMe.phimpme_personal_array_list,personal_facebook_list_photos);
						PhimpMe.phimpme_personal_array_list
								.add(personal_facebook_list_photos);
					}

					btn_facebook_more.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							btn_facebook_more
									.setImageResource(R.drawable.more_disable);
							btn_facebook_more.setEnabled(false);
							if (personal_facebook_list_photos.size()
									- personal_facebook_count
									* NUMBER_PHOTO_NEED_DOWNLOAD > 3) {
								personal_facebook_rows_display = 2;
							} else if ((personal_facebook_list_photos.size() - personal_facebook_count
									* NUMBER_PHOTO_NEED_DOWNLOAD) > 0)
								personal_facebook_rows_display = 1;

							int hight_display = ln_facebook.getHeight()
									+ DEFAULT_THUMBNAIL_SIZE
									* personal_facebook_rows_display;
							LinearLayout.LayoutParams rep = new LinearLayout.LayoutParams(
									ViewGroup.LayoutParams.WRAP_CONTENT,
									hight_display);
							ln_facebook.setLayoutParams(rep);
							ln_facebook.setEnabled(true);
							personal_moreClick(personal_facebook_list_photos,
									personal_facebook_count);
							personal_facebook_count++;
						}
					});
				}
				if (PhimpMe.FEEDS_LIST_TUMBLR_PRIVATE) {
					if (listService.indexOf("personal_tumblr") == -1) {
						tumblr_download=true;
						Button btn_line = new Button(ctx);
						btn_line.setHeight(2);
						btn_line.setWidth(LayoutParams.MATCH_PARENT);
						btn_line.setBackgroundResource(color_line);

						txttumblr = new TextView(getContext());
						txttumblr.setText("Tumblr Personal");
						txttumblr.setTextSize(text_size);
						gv_personal_tumblr = new GridView(getContext());
						gv_personal_tumblr.setPadding(0, 10, 0, 0);
						ln_tumblr = new LinearLayout(getContext());
						btn_tumblr_more = new ImageButton(getContext());
						btn_tumblr_more
								.setImageResource(R.drawable.more_disable);
						btn_tumblr_more.setEnabled(false);

						RelativeLayout more_li = new RelativeLayout(getContext());
						btn_tumblr_more.setLayoutParams(lp_more);
						more_li.addView(btn_tumblr_more);
						more_li.addView(txttumblr);
						ln_tumblr.setOrientation(LinearLayout.VERTICAL);
						ln_tumblr.addView(more_li);
						ln_tumblr.addView(btn_line);
						ln_tumblr.addView(gv_personal_tumblr);

						ln_tumblr.setEnabled(false);
						linear_main.addView(ln_tumblr);
						tumblradapter = new GridTumblrAdapter(
								list_thumb_personal, bitmap_personal_tumblr,
								ctx);
						gv_personal_tumblr.setNumColumns(cols);
						gv_personal_tumblr.setAdapter(tumblradapter);
						gv_personal_tumblr.setDrawingCacheEnabled(true);
						listService.add("personal_tumblr");
					}
					btn_tumblr_more.setImageResource(R.drawable.more_disable);
					btn_tumblr_more.setEnabled(false);
					personal_tumblr_count = 1;
					tumblradapter.removeItem();
					tmp_list_personal = Tumblr.getOwn(ctx, "Personal Tumblr");
					Log.d("thong",
							"Tumblr Personal: " + tmp_list_personal.size());
					// Don't save exist photos
					ArrayList<RSSPhotoItem_Personal> list_save = new ArrayList<RSSPhotoItem_Personal>();
					for (int t = 0; t < tmp_list_personal.size(); t++) {
						String url = tmp_list_personal.get(t).getURL();
						String filepath = rss_folder.getAbsolutePath()
								+ "/"
								+ url.toLowerCase().replace("://", "")
										.replace("/", "_");
						File f = new File(filepath);
						if (!f.exists()) {
							list_save.add(tmp_list_personal.get(t));
						}
					}
					//
					if (list_save.size() == 0) {
						ln_tumblr.setLayoutParams(p_zero);
					} else if (list_save.size() <= 3) {
						ln_tumblr.setLayoutParams(p_one_row);
					} else
						ln_tumblr.setLayoutParams(p_two_row);
					list_photos_personal.addAll(tmp_list_personal);
					personal_tumblr_list_photos.clear();
					personal_tumblr_list_photos.addAll(list_save);

					personal_array_list.add(personal_tumblr_list_photos);
					if (!checkArray_Persional(PhimpMe.phimpme_personal_array_list,personal_tumblr_list_photos)) {
						PhimpMe.phimpme_personal_array_list
								.add(personal_tumblr_list_photos);
					} else {
						deleteItem_Persional(PhimpMe.phimpme_personal_array_list,personal_tumblr_list_photos);
						PhimpMe.phimpme_personal_array_list
								.add(personal_tumblr_list_photos);
					}

					btn_tumblr_more.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							btn_tumblr_more
									.setImageResource(R.drawable.more_disable);
							btn_tumblr_more.setEnabled(false);
							if (personal_tumblr_list_photos.size()
									- personal_tumblr_count
									* NUMBER_PHOTO_NEED_DOWNLOAD > 3) {
								personal_tumblr_rows_display = 2;
							} else if ((personal_tumblr_list_photos.size() - personal_tumblr_count
									* NUMBER_PHOTO_NEED_DOWNLOAD) > 0)
								personal_tumblr_rows_display = 1;

							int hight_display = ln_tumblr.getHeight()
									+ DEFAULT_THUMBNAIL_SIZE
									* personal_tumblr_rows_display;
							LinearLayout.LayoutParams rep = new LinearLayout.LayoutParams(
									ViewGroup.LayoutParams.WRAP_CONTENT,
									hight_display);
							ln_tumblr.setLayoutParams(rep);
							ln_tumblr.setEnabled(true);
							personal_moreClick(personal_tumblr_list_photos,
									personal_tumblr_count);
							personal_tumblr_count++;
						}
					});
				}
				if (PhimpMe.FEEDS_LIST_VK) {
					if (listService.indexOf("personal_vkontakte") == -1) {
						vk_download=true;
						Button btn_line = new Button(ctx);
						btn_line.setHeight(2);
						btn_line.setWidth(LayoutParams.MATCH_PARENT);
						btn_line.setBackgroundResource(color_line);

						txtvkontakte = new TextView(getContext());
						txtvkontakte.setText("Vkontakte Personal");
						txtvkontakte.setTextSize(text_size);
						gv_personal_vkontakte = new GridView(getContext());
						gv_personal_vkontakte.setPadding(0, 10, 0, 0);
						ln_vkontakte = new LinearLayout(getContext());
						btn_vkontakte_more = new ImageButton(getContext());
						btn_vkontakte_more
								.setImageResource(R.drawable.more_disable);
						btn_vkontakte_more.setEnabled(false);

						RelativeLayout more_li = new RelativeLayout(getContext());
						btn_vkontakte_more.setLayoutParams(lp_more);
						more_li.addView(btn_vkontakte_more);
						more_li.addView(txtvkontakte);
						ln_vkontakte.setOrientation(LinearLayout.VERTICAL);
						ln_vkontakte.addView(more_li);
						ln_vkontakte.addView(btn_line);
						ln_vkontakte.addView(gv_personal_vkontakte);

						ln_vkontakte.setEnabled(false);
						linear_main.addView(ln_vkontakte);
						vkontakteadapter = new GridVKontakteAdapter(
								list_thumb_personal, bitmap_personal_vkontakte,
								ctx);
						gv_personal_vkontakte.setNumColumns(cols);
						gv_personal_vkontakte.setAdapter(vkontakteadapter);
						gv_personal_vkontakte.setDrawingCacheEnabled(true);
						listService.add("personal_vkontakte");
					}
					btn_vkontakte_more
							.setImageResource(R.drawable.more_disable);
					btn_vkontakte_more.setEnabled(false);
					personal_vkontakte_count = 1;

					vkontakteadapter.removeItem();
					tmp_list_personal = Vkontakte.getOwn(ctx,
							"Personal VKontakte");
					Log.d("thong",
							"VKontakte Personal: " + tmp_list_personal.size());

					// Don't save exist photos
					ArrayList<RSSPhotoItem_Personal> list_save = new ArrayList<RSSPhotoItem_Personal>();
					for (int t = 0; t < tmp_list_personal.size(); t++) {
						String url = tmp_list_personal.get(t).getURL();
						String filepath = rss_folder.getAbsolutePath()
								+ "/"
								+ url.toLowerCase().replace("://", "")
										.replace("/", "_");
						File f = new File(filepath);
						if (!f.exists()) {
							list_save.add(tmp_list_personal.get(t));
						}
					}

					if (list_save.size() == 0) {
						ln_vkontakte.setLayoutParams(p_zero);
					} else if (list_save.size() <= 3) {
						ln_vkontakte.setLayoutParams(p_one_row);
					} else
						ln_vkontakte.setLayoutParams(p_two_row);
					list_photos_personal.addAll(tmp_list_personal);
					personal_vkontakte_list_photos.clear();
					personal_vkontakte_list_photos.addAll(list_save);

					personal_array_list.add(personal_vkontakte_list_photos);
					if (!checkArray_Persional(PhimpMe.phimpme_personal_array_list,personal_vkontakte_list_photos)) {
						PhimpMe.phimpme_personal_array_list
								.add(personal_vkontakte_list_photos);
					} else {
						deleteItem_Persional(PhimpMe.phimpme_personal_array_list,personal_vkontakte_list_photos);
						PhimpMe.phimpme_personal_array_list
								.add(personal_vkontakte_list_photos);
					}
					btn_vkontakte_more
							.setOnClickListener(new OnClickListener() {
								@Override
								public void onClick(View v) {
									btn_vkontakte_more
											.setImageResource(R.drawable.more_disable);
									btn_vkontakte_more.setEnabled(false);
									if (personal_vkontakte_list_photos.size()
											- personal_vkontakte_count
											* NUMBER_PHOTO_NEED_DOWNLOAD > 3) {
										personal_vkontakte_rows_display = 2;
									} else if ((personal_vkontakte_list_photos
											.size() - personal_vkontakte_count
											* NUMBER_PHOTO_NEED_DOWNLOAD) > 0)
										personal_vkontakte_rows_display = 1;

									int hight_display = ln_vkontakte
											.getHeight()
											+ DEFAULT_THUMBNAIL_SIZE
											* personal_vkontakte_rows_display;
									LinearLayout.LayoutParams rep = new LinearLayout.LayoutParams(
											ViewGroup.LayoutParams.WRAP_CONTENT,
											hight_display);
									ln_vkontakte.setLayoutParams(rep);
									ln_vkontakte.setEnabled(true);
									personal_moreClick(
											personal_vkontakte_list_photos,
											personal_vkontakte_count);
									personal_vkontakte_count++;
								}
							});
				}
				if (PhimpMe.FEEDS_LIST_FLICKR_PRIVATE) {
					if (listService.indexOf("personal_flickr") == -1) {
						personal_flickr_download=true;
						Button btn_line = new Button(ctx);
						btn_line.setHeight(2);
						btn_line.setWidth(LayoutParams.MATCH_PARENT);
						btn_line.setBackgroundResource(color_line);

						txtpersonal_flickr = new TextView(getContext());
						txtpersonal_flickr.setText("Personal Flickr");
						txtpersonal_flickr.setTextSize(text_size);
						gv_personal_flickr = new GridView(getContext());
						gv_personal_flickr.setPadding(0, 10, 0, 0);
						ln_personal_flickr = new LinearLayout(getContext());
						btn_personal_flickr_more = new ImageButton(getContext());
						btn_personal_flickr_more
								.setImageResource(R.drawable.more_disable);
						btn_personal_flickr_more.setEnabled(false);

						RelativeLayout more_li = new RelativeLayout(getContext());
						btn_personal_flickr_more.setLayoutParams(lp_more);
						more_li.addView(btn_personal_flickr_more);
						more_li.addView(txtpersonal_flickr);
						ln_personal_flickr
								.setOrientation(LinearLayout.VERTICAL);
						ln_personal_flickr.addView(more_li);
						ln_personal_flickr.addView(btn_line);
						ln_personal_flickr.addView(gv_personal_flickr);

						ln_personal_flickr.setEnabled(false);

						linear_main.addView(ln_personal_flickr);
						personal_flickradapter = new GridPersonalFlickrAdapter(
								list_thumb_personal, bitmap_personal_flickr,
								ctx);
						gv_personal_flickr.setNumColumns(cols);
						gv_personal_flickr.setAdapter(personal_flickradapter);
						gv_personal_flickr.setDrawingCacheEnabled(true);
						listService.add("personal_flickr");
					}
					btn_personal_flickr_more
							.setImageResource(R.drawable.more_disable);
					btn_personal_flickr_more.setEnabled(false);
					personal_flickr_count = 1;

					personal_flickradapter.removeItem();
					tmp_list_personal = Flickr.getPrivatePhotos(ctx,
							"Personal Flickr");
					Log.d("thong",
							"Flickr Personal: " + tmp_list_personal.size());

					// Don't save exist photos
					ArrayList<RSSPhotoItem_Personal> list_save = new ArrayList<RSSPhotoItem_Personal>();
					for (int t = 0; t < tmp_list_personal.size(); t++) {
						String url = tmp_list_personal.get(t).getURL();
						String filepath = rss_folder.getAbsolutePath()
								+ "/"
								+ url.toLowerCase().replace("://", "")
										.replace("/", "_");
						File f = new File(filepath);
						if (!f.exists()) {
							list_save.add(tmp_list_personal.get(t));
						}
					}

					if (list_save.size() == 0) {
						ln_personal_flickr.setLayoutParams(p_zero);
					} else if (list_save.size() <= 3) {
						ln_personal_flickr.setLayoutParams(p_one_row);
					} else
						ln_personal_flickr.setLayoutParams(p_two_row);
					list_photos_personal.addAll(tmp_list_personal);
					personal_flickr_list_photos.clear();
					personal_flickr_list_photos.addAll(list_save);

					personal_array_list.add(personal_flickr_list_photos);
					if (!checkArray_Persional(PhimpMe.phimpme_personal_array_list,personal_flickr_list_photos)) {
						PhimpMe.phimpme_personal_array_list
								.add(personal_flickr_list_photos);
					} else {
						deleteItem_Persional(PhimpMe.phimpme_personal_array_list,personal_flickr_list_photos);
						PhimpMe.phimpme_personal_array_list
								.add(personal_flickr_list_photos);
					}
					btn_personal_flickr_more
							.setOnClickListener(new OnClickListener() {
								@Override
								public void onClick(View v) {
									btn_personal_flickr_more
											.setImageResource(R.drawable.more_disable);
									btn_personal_flickr_more.setEnabled(false);
									if (personal_flickr_list_photos.size()
											- personal_flickr_count
											* NUMBER_PHOTO_NEED_DOWNLOAD > 3) {
										personal_flickr_rows_display = 2;
									} else if ((personal_flickr_list_photos
											.size() - personal_flickr_count
											* NUMBER_PHOTO_NEED_DOWNLOAD) > 0)
										personal_flickr_rows_display = 1;

									int hight_display = ln_personal_flickr
											.getHeight()
											+ DEFAULT_THUMBNAIL_SIZE
											* personal_flickr_rows_display;
									LinearLayout.LayoutParams rep = new LinearLayout.LayoutParams(
											ViewGroup.LayoutParams.WRAP_CONTENT,
											hight_display);
									ln_personal_flickr.setLayoutParams(rep);
									ln_personal_flickr.setEnabled(true);
									personal_moreClick(
											personal_flickr_list_photos,
											personal_flickr_count);
									personal_flickr_count++;
								}
							});
				}
				if (PhimpMe.FEEDS_LIST_GOOGLE_PICASA_PRIVATE) {					
					if (listService.indexOf("personal_picasa") == -1) {
						personal_picasa_download=true;
						Button btn_line = new Button(ctx);
						btn_line.setHeight(2);
						btn_line.setWidth(LayoutParams.MATCH_PARENT);
						btn_line.setBackgroundResource(color_line);

						txtpersonal_picasa = new TextView(getContext());
						txtpersonal_picasa.setText("Personal Picasa");
						txtpersonal_picasa.setTextSize(text_size);
						gv_personal_picasa = new GridView(getContext());
						gv_personal_picasa.setPadding(0, 10, 0, 0);
						ln_personal_picasa = new LinearLayout(getContext());
						btn_personal_picasa_more = new ImageButton(getContext());
						btn_personal_picasa_more
								.setImageResource(R.drawable.more_disable);
						btn_personal_picasa_more.setEnabled(false);

						RelativeLayout more_li = new RelativeLayout(getContext());
						btn_personal_picasa_more.setLayoutParams(lp_more);
						more_li.addView(btn_personal_picasa_more);
						more_li.addView(txtpersonal_picasa);
						ln_personal_picasa
								.setOrientation(LinearLayout.VERTICAL);
						ln_personal_picasa.addView(more_li);
						ln_personal_picasa.addView(btn_line);
						ln_personal_picasa.addView(gv_personal_picasa);

						ln_personal_picasa.setEnabled(false);

						linear_main.addView(ln_personal_picasa);
						personal_picasaadapter = new GridPersonalPicasaAdapter(
								list_thumb_personal, bitmap_personal_picasa,
								ctx);
						gv_personal_picasa.setNumColumns(cols);
						gv_personal_picasa.setAdapter(personal_picasaadapter);
						gv_personal_picasa.setDrawingCacheEnabled(true);
						listService.add("personal_picasa");
					}
					btn_personal_picasa_more
							.setImageResource(R.drawable.more_disable);
					btn_personal_picasa_more.setEnabled(false);
					personal_picasa_count = 1;

					personal_picasaadapter.removeItem();
					tmp_list_personal = Google.getOwn(ctx, "Personal Picasa");
					Log.d("thong",
							"Picasa Personal: " + tmp_list_personal.size());
					// Don't save exist photos
					ArrayList<RSSPhotoItem_Personal> list_save = new ArrayList<RSSPhotoItem_Personal>();
					for (int t = 0; t < tmp_list_personal.size(); t++) {
						String url = tmp_list_personal.get(t).getURL();
						String filepath = rss_folder.getAbsolutePath()
								+ "/"
								+ url.toLowerCase().replace("://", "")
										.replace("/", "_");
						File f = new File(filepath);
						if (!f.exists()) {
							list_save.add(tmp_list_personal.get(t));
						}
					}

					if (list_save.size() == 0) {
						ln_personal_picasa.setLayoutParams(p_zero);
					} else if (list_save.size() <= 3) {
						ln_personal_picasa.setLayoutParams(p_one_row);
					} else
						ln_personal_picasa.setLayoutParams(p_two_row);
					list_photos_personal.addAll(tmp_list_personal);
					personal_picasa_list_photos.clear();
					personal_picasa_list_photos.addAll(list_save);
					personal_array_list.add(personal_picasa_list_photos);
					if (!checkArray_Persional(PhimpMe.phimpme_personal_array_list,personal_picasa_list_photos)) {
						PhimpMe.phimpme_personal_array_list
								.add(personal_picasa_list_photos);
					} else {
						deleteItem_Persional(PhimpMe.phimpme_personal_array_list,personal_picasa_list_photos);
						PhimpMe.phimpme_personal_array_list
								.add(personal_picasa_list_photos);
					}
					btn_personal_picasa_more
							.setOnClickListener(new OnClickListener() {
								@Override
								public void onClick(View v) {
									btn_personal_picasa_more
											.setImageResource(R.drawable.more_disable);
									btn_personal_picasa_more.setEnabled(false);
									if (personal_picasa_list_photos.size()
											- personal_picasa_count
											* NUMBER_PHOTO_NEED_DOWNLOAD > 3) {
										personal_picasa_rows_display = 2;
									} else if ((personal_picasa_list_photos
											.size() - personal_picasa_count
											* NUMBER_PHOTO_NEED_DOWNLOAD) > 0)
										personal_picasa_rows_display = 1;

									int hight_display = ln_personal_picasa
											.getHeight()
											+ DEFAULT_THUMBNAIL_SIZE
											* personal_picasa_rows_display;
									LinearLayout.LayoutParams rep = new LinearLayout.LayoutParams(
											ViewGroup.LayoutParams.WRAP_CONTENT,
											hight_display);
									ln_personal_picasa.setLayoutParams(rep);
									ln_personal_picasa.setEnabled(true);
									personal_moreClick(
											personal_picasa_list_photos,
											personal_picasa_count);
									personal_picasa_count++;
								}
							});
				}
				if (PhimpMe.FEEDS_LIST_DEVIANTART_PRIVITE) {
					if (listService.indexOf("personal_deviantart") == -1) {
						personal_deviant_download=true;
						Button btn_line = new Button(ctx);
						btn_line.setHeight(2);
						btn_line.setWidth(LayoutParams.MATCH_PARENT);
						btn_line.setBackgroundResource(color_line);

						txtpersonal_deviantart = new TextView(getContext());
						txtpersonal_deviantart.setText("Personal DeviantArt");
						txtpersonal_deviantart.setTextSize(text_size);
						gv_personal_deviantart = new GridView(getContext());
						gv_personal_deviantart.setPadding(0, 10, 0, 0);
						ln_personal_deviantart = new LinearLayout(getContext());
						btn_personal_deviant_more = new ImageButton(getContext());
						btn_personal_deviant_more
								.setImageResource(R.drawable.more_disable);
						btn_personal_deviant_more.setEnabled(false);

						RelativeLayout more_li = new RelativeLayout(getContext());
						btn_personal_deviant_more.setLayoutParams(lp_more);
						more_li.addView(btn_personal_deviant_more);
						more_li.addView(txtpersonal_deviantart);
						ln_personal_deviantart
								.setOrientation(LinearLayout.VERTICAL);
						ln_personal_deviantart.addView(more_li);
						ln_personal_deviantart.addView(btn_line);
						ln_personal_deviantart.addView(gv_personal_deviantart);

						ln_personal_deviantart.setEnabled(false);
						linear_main.addView(ln_personal_deviantart);
						personal_deviantartadapter = new GridPersonalDeviantArtAdapter(
								list_thumb_personal,
								bitmap_personal_deviantart, ctx);
						gv_personal_deviantart.setNumColumns(cols);
						gv_personal_deviantart
								.setAdapter(personal_deviantartadapter);
						gv_personal_deviantart.setDrawingCacheEnabled(true);
						listService.add("personal_deviantart");
					}
					btn_personal_deviant_more
							.setImageResource(R.drawable.more_disable);
					btn_personal_deviant_more.setEnabled(false);
					personal_deviantart_count = 1;
					personal_deviantartadapter.removeItem();
					tmp_list_personal = DeviantArt.getPrivite(ctx,
							"Personal DeviantArt");
					Log.d("thong",
							"DeviantArt Personal: " + tmp_list_personal.size());
					// Don't save exist photos
					ArrayList<RSSPhotoItem_Personal> list_save = new ArrayList<RSSPhotoItem_Personal>();
					for (int t = 0; t < tmp_list_personal.size(); t++) {
						String url = tmp_list_personal.get(t).getURL();
						String filepath = rss_folder.getAbsolutePath()
								+ "/"
								+ url.toLowerCase().replace("://", "")
										.replace("/", "_");
						File f = new File(filepath);
						if (!f.exists()) {
							list_save.add(tmp_list_personal.get(t));
						}
					}

					if (list_save.size() == 0) {
						ln_personal_deviantart.setLayoutParams(p_zero);
					} else if (list_save.size() <= 3) {
						ln_personal_deviantart.setLayoutParams(p_one_row);
					} else
						ln_personal_deviantart.setLayoutParams(p_two_row);
					list_photos_personal.addAll(tmp_list_personal);
					personal_deviantart_list_photos.clear();
					personal_deviantart_list_photos.addAll(list_save);

					personal_array_list.add(personal_deviantart_list_photos);
					if (!checkArray_Persional(PhimpMe.phimpme_personal_array_list,personal_deviantart_list_photos)) {
						PhimpMe.phimpme_personal_array_list
								.add(personal_deviantart_list_photos);
					} else {
						deleteItem_Persional(PhimpMe.phimpme_personal_array_list,personal_deviantart_list_photos);
						PhimpMe.phimpme_personal_array_list
								.add(personal_deviantart_list_photos);
					}
					btn_personal_deviant_more
							.setOnClickListener(new OnClickListener() {
								@Override
								public void onClick(View v) {
									btn_personal_deviant_more
											.setImageResource(R.drawable.more_disable);
									btn_personal_deviant_more.setEnabled(false);
									if (personal_deviantart_list_photos.size()
											- personal_deviantart_count
											* NUMBER_PHOTO_NEED_DOWNLOAD > 3) {
										personal_deviantart_rows_display = 2;
									} else if ((personal_deviantart_list_photos
											.size() - personal_deviantart_count
											* NUMBER_PHOTO_NEED_DOWNLOAD) > 0)
										personal_deviantart_rows_display = 1;

									int hight_display = ln_personal_deviantart
											.getHeight()
											+ DEFAULT_THUMBNAIL_SIZE
											* personal_deviantart_rows_display;
									LinearLayout.LayoutParams rep = new LinearLayout.LayoutParams(
											ViewGroup.LayoutParams.WRAP_CONTENT,
											hight_display);
									ln_personal_deviantart.setLayoutParams(rep);
									ln_personal_deviantart.setEnabled(true);
									personal_moreClick(
											personal_deviantart_list_photos,
											personal_deviantart_count);
									personal_deviantart_count++;
								}
							});
				}
				if (PhimpMe.FEEDS_LIST_IMGUR_PERSONAL) {
					if (listService.indexOf("personal_imgur") == -1) {
						personal_imgur_download=true;
						Button btn_line = new Button(ctx);
						btn_line.setHeight(2);
						btn_line.setWidth(LayoutParams.MATCH_PARENT);
						btn_line.setBackgroundResource(color_line);

						txtpersonal_imgur = new TextView(getContext());
						txtpersonal_imgur.setText("Personal Imgur");
						txtpersonal_imgur.setTextSize(text_size);
						gv_personal_imgur = new GridView(getContext());
						gv_personal_imgur.setPadding(0, 10, 0, 0);
						ln_personal_imgur = new LinearLayout(getContext());
						btn_personal_imgur_more = new ImageButton(getContext());
						btn_personal_imgur_more
								.setImageResource(R.drawable.more_disable);
						btn_personal_imgur_more.setEnabled(false);

						RelativeLayout more_li = new RelativeLayout(getContext());
						btn_personal_imgur_more.setLayoutParams(lp_more);
						more_li.addView(btn_personal_imgur_more);
						more_li.addView(txtpersonal_imgur);
						ln_personal_imgur.setOrientation(LinearLayout.VERTICAL);
						ln_personal_imgur.addView(more_li);
						ln_personal_imgur.addView(btn_line);
						ln_personal_imgur.addView(gv_personal_imgur);

						ln_personal_imgur.setEnabled(false);
						linear_main.addView(ln_personal_imgur);
						personal_imguradapter = new GridPersonalImgurAdapter(
								list_thumb_personal, bitmap_personal_imgur, ctx);
						gv_personal_imgur.setNumColumns(cols);
						gv_personal_imgur.setAdapter(personal_imguradapter);
						gv_personal_imgur.setDrawingCacheEnabled(true);
						listService.add("personal_imgur");
					}
					btn_personal_imgur_more
							.setImageResource(R.drawable.more_disable);
					btn_personal_imgur_more.setEnabled(false);
					personal_imgur_count = 1;

					personal_imguradapter.removeItem();
					tmp_list_personal = Imgur.getPersonalPhotos(ctx,
							"Personal Imgur");
					Log.d("Danh", "Imgur Personal: " + tmp_list_personal.size());
					// Don't save exist photos
					ArrayList<RSSPhotoItem_Personal> list_save = new ArrayList<RSSPhotoItem_Personal>();
					for (int t = 0; t < tmp_list_personal.size(); t++) {
						String url = tmp_list_personal.get(t).getURL();
						String filepath = rss_folder.getAbsolutePath()
								+ "/"
								+ url.toLowerCase().replace("://", "")
										.replace("/", "_");
						File f = new File(filepath);
						if (!f.exists()) {
							list_save.add(tmp_list_personal.get(t));
						}
					}

					if (list_save.size() == 0) {
						ln_personal_imgur.setLayoutParams(p_zero);
					} else if (list_save.size() <= 3) {
						ln_personal_imgur.setLayoutParams(p_one_row);
					} else
						ln_personal_imgur.setLayoutParams(p_two_row);
					list_photos_personal.addAll(tmp_list_personal);
					personal_imgur_list_photos.clear();
					personal_imgur_list_photos.addAll(list_save);

					personal_array_list.add(personal_imgur_list_photos);
					if (!checkArray_Persional(PhimpMe.phimpme_personal_array_list,personal_imgur_list_photos)) {
						PhimpMe.phimpme_personal_array_list
								.add(personal_imgur_list_photos);
					} else {
						deleteItem_Persional(PhimpMe.phimpme_personal_array_list,personal_imgur_list_photos);
						PhimpMe.phimpme_personal_array_list
								.add(personal_imgur_list_photos);
					}
					btn_personal_imgur_more
							.setOnClickListener(new OnClickListener() {
								@Override
								public void onClick(View v) {
									btn_personal_imgur_more
											.setImageResource(R.drawable.more_disable);
									btn_personal_imgur_more.setEnabled(false);
									if (personal_imgur_list_photos.size()
											- personal_imgur_count
											* NUMBER_PHOTO_NEED_DOWNLOAD > 3) {
										personal_imgur_rows_display = 2;
									} else if ((personal_imgur_list_photos
											.size() - personal_imgur_count
											* NUMBER_PHOTO_NEED_DOWNLOAD) > 0)
										personal_imgur_rows_display = 1;

									int hight_display = ln_personal_imgur
											.getHeight()
											+ DEFAULT_THUMBNAIL_SIZE
											* personal_imgur_rows_display;
									LinearLayout.LayoutParams rep = new LinearLayout.LayoutParams(
											ViewGroup.LayoutParams.WRAP_CONTENT,
											hight_display);
									ln_personal_imgur.setLayoutParams(rep);
									ln_personal_imgur.setEnabled(true);
									personal_moreClick(
											personal_imgur_list_photos,
											personal_imgur_count);
									personal_imgur_count++;
								}
							});
				}
				if (PhimpMe.FEEDS_LIST_KAIXIN_PRIVATE) {
					if (listService.indexOf("personal_kaixin") == -1) {
						Button btn_line = new Button(ctx);
						btn_line.setHeight(2);
						btn_line.setWidth(LayoutParams.MATCH_PARENT);
						btn_line.setBackgroundResource(color_line);

						txtpersonal_kaixin = new TextView(getContext());
						txtpersonal_kaixin.setText("Personal Kaixin");
						txtpersonal_kaixin.setTextSize(text_size);
						gv_personal_kaixin = new GridView(getContext());
						gv_personal_kaixin.setPadding(0, 10, 0, 0);
						ln_personal_kaixin = new LinearLayout(getContext());

						btn_personal_kaixin_more = new ImageButton(getContext());
						btn_personal_kaixin_more
								.setImageResource(R.drawable.more_disable);
						btn_personal_kaixin_more.setEnabled(false);

						RelativeLayout more_li = new RelativeLayout(getContext());
						btn_personal_kaixin_more.setLayoutParams(lp_more);
						more_li.addView(btn_personal_kaixin_more);
						more_li.addView(txtpersonal_kaixin);
						ln_personal_kaixin
								.setOrientation(LinearLayout.VERTICAL);
						ln_personal_kaixin.addView(more_li);
						ln_personal_kaixin.addView(btn_line);
						ln_personal_kaixin.addView(gv_personal_kaixin);

						ln_personal_kaixin.setEnabled(false);
						linear_main.addView(ln_personal_kaixin);
						personal_kaixinadapter = new GridPersonalKaixinAdapter(
								list_thumb_personal, bitmap_personal_kaixin,
								ctx);
						gv_personal_kaixin.setNumColumns(cols);
						gv_personal_kaixin.setAdapter(personal_kaixinadapter);
						gv_personal_kaixin.setDrawingCacheEnabled(true);
						listService.add("personal_kaixin");
					}
					btn_personal_kaixin_more
							.setImageResource(R.drawable.more_disable);
					btn_personal_kaixin_more.setEnabled(false);
					personal_kaixin_count = 1;
					personal_kaixinadapter.removeItem();
					tmp_list_personal = Kaixin.getPersonal(ctx,
							"Personal Kaixin");
					Log.d("thong",
							"Kaixin Personal: " + tmp_list_personal.size());

					// Don't save exist photos
					ArrayList<RSSPhotoItem_Personal> list_save = new ArrayList<RSSPhotoItem_Personal>();
					for (int t = 0; t < tmp_list_personal.size(); t++) {
						String url = tmp_list_personal.get(t).getURL();
						String filepath = rss_folder.getAbsolutePath()
								+ "/"
								+ url.toLowerCase().replace("://", "")
										.replace("/", "_");
						File f = new File(filepath);
						if (!f.exists()) {
							list_save.add(tmp_list_personal.get(t));
						}
					}

					if (list_save.size() == 0) {
						ln_personal_kaixin.setLayoutParams(p_zero);
					} else if (list_save.size() <= 3) {
						ln_personal_kaixin.setLayoutParams(p_one_row);
					} else
						ln_personal_kaixin.setLayoutParams(p_two_row);
					list_photos_personal.addAll(tmp_list_personal);
					personal_kaixin_list_photos.clear();
					personal_kaixin_list_photos.addAll(list_save);

					personal_array_list.add(personal_kaixin_list_photos);
					if (!checkArray_Persional(PhimpMe.phimpme_personal_array_list,personal_kaixin_list_photos)) {
						PhimpMe.phimpme_personal_array_list
								.add(personal_kaixin_list_photos);
					} else {
						deleteItem_Persional(PhimpMe.phimpme_personal_array_list,personal_kaixin_list_photos);
						PhimpMe.phimpme_personal_array_list
								.add(personal_kaixin_list_photos);
					}
					btn_personal_kaixin_more
							.setOnClickListener(new OnClickListener() {
								@Override
								public void onClick(View v) {
									btn_personal_kaixin_more
											.setImageResource(R.drawable.more_disable);
									btn_personal_kaixin_more.setEnabled(false);
									if (personal_kaixin_list_photos.size()
											- personal_kaixin_count
											* NUMBER_PHOTO_NEED_DOWNLOAD > 3) {
										personal_kaixin_rows_display = 2;
									} else if ((personal_kaixin_list_photos
											.size() - personal_kaixin_count
											* NUMBER_PHOTO_NEED_DOWNLOAD) > 0)
										personal_kaixin_rows_display = 1;

									int hight_display = ln_personal_kaixin
											.getHeight()
											+ DEFAULT_THUMBNAIL_SIZE
											* personal_kaixin_rows_display;
									LinearLayout.LayoutParams rep = new LinearLayout.LayoutParams(
											ViewGroup.LayoutParams.WRAP_CONTENT,
											hight_display);
									ln_personal_kaixin.setLayoutParams(rep);
									ln_personal_kaixin.setEnabled(true);
									personal_moreClick(
											personal_kaixin_list_photos,
											personal_kaixin_count);
									personal_kaixin_count++;
								}
							});
				}
				if (PhimpMe.FEEDS_LIST_500PX_PRIVATE) {
					if (listService.indexOf("personal_500px") == -1) {
						Button btn_line = new Button(ctx);
						btn_line.setHeight(2);
						btn_line.setWidth(LayoutParams.MATCH_PARENT);
						btn_line.setBackgroundResource(color_line);

						txtpersonal_500px = new TextView(getContext());
						txtpersonal_500px.setText("Personal 500px");
						txtpersonal_500px.setTextSize(text_size);
						gv_personal_500px = new GridView(getContext());
						gv_personal_500px.setPadding(0, 10, 0, 0);
						ln_personal_500px = new LinearLayout(getContext());
						btn_personal_500px_more = new ImageButton(getContext());
						btn_personal_500px_more
								.setImageResource(R.drawable.more_disable);
						btn_personal_500px_more.setEnabled(false);

						RelativeLayout more_li = new RelativeLayout(getContext());
						btn_personal_500px_more.setLayoutParams(lp_more);
						more_li.addView(btn_personal_500px_more);
						more_li.addView(txtpersonal_500px);
						ln_personal_500px.setOrientation(LinearLayout.VERTICAL);
						ln_personal_500px.addView(more_li);
						ln_personal_500px.addView(btn_line);
						ln_personal_500px.addView(gv_personal_500px);

						ln_personal_500px.setEnabled(false);
						linear_main.addView(ln_personal_500px);
						personal_500pxadapter = new GridPersonal500pxAdapter(
								list_thumb_personal, bitmap_personal_500px, ctx);
						gv_personal_500px.setNumColumns(cols);
						gv_personal_500px.setAdapter(personal_500pxadapter);
						gv_personal_500px.setDrawingCacheEnabled(true);
						listService.add("personal_500px");
					}
					btn_personal_500px_more
							.setImageResource(R.drawable.more_disable);
					btn_personal_500px_more.setEnabled(false);
					personal_500px_count = 1;
					personal_500pxadapter.removeItem();
					tmp_list_personal = s500px.getPrivatePhotos(ctx,
							"Personal 500px");
					Log.d("thong",
							"500px Personal: " + tmp_list_personal.size());

					// Don't save exist photos
					ArrayList<RSSPhotoItem_Personal> list_save = new ArrayList<RSSPhotoItem_Personal>();
					for (int t = 0; t < tmp_list_personal.size(); t++) {
						String url = tmp_list_personal.get(t).getURL();
						String filepath = rss_folder.getAbsolutePath()
								+ "/"
								+ url.toLowerCase().replace("://", "")
										.replace("/", "_");
						File f = new File(filepath);
						if (!f.exists()) {
							list_save.add(tmp_list_personal.get(t));
						}
					}

					if (list_save.size() == 0) {
						ln_personal_500px.setLayoutParams(p_zero);
					} else if (list_save.size() <= 3) {
						ln_personal_500px.setLayoutParams(p_one_row);
					} else
						ln_personal_500px.setLayoutParams(p_two_row);
					list_photos_personal.addAll(tmp_list_personal);
					personal_500px_list_photos.clear();
					personal_500px_list_photos.addAll(list_save);

					personal_array_list.add(personal_500px_list_photos);
					if (!checkArray_Persional(PhimpMe.phimpme_personal_array_list,personal_500px_list_photos)) {
						PhimpMe.phimpme_personal_array_list
								.add(personal_500px_list_photos);
					} else {
						deleteItem_Persional(PhimpMe.phimpme_personal_array_list,personal_500px_list_photos);
						PhimpMe.phimpme_personal_array_list
								.add(personal_500px_list_photos);
					}
					btn_personal_500px_more
							.setOnClickListener(new OnClickListener() {
								@Override
								public void onClick(View v) {
									btn_personal_500px_more
											.setImageResource(R.drawable.more_disable);
									btn_personal_500px_more.setEnabled(false);
									if (personal_500px_list_photos.size()
											- personal_500px_count
											* NUMBER_PHOTO_NEED_DOWNLOAD > 3) {
										personal_500px_rows_display = 2;
									} else if ((personal_500px_list_photos
											.size() - personal_500px_count
											* NUMBER_PHOTO_NEED_DOWNLOAD) > 0)
										personal_500px_rows_display = 1;

									int hight_display = ln_personal_500px
											.getHeight()
											+ DEFAULT_THUMBNAIL_SIZE
											* personal_500px_rows_display;
									LinearLayout.LayoutParams rep = new LinearLayout.LayoutParams(
											ViewGroup.LayoutParams.WRAP_CONTENT,
											hight_display);
									ln_personal_500px.setLayoutParams(rep);
									ln_personal_500px.setEnabled(true);
									personal_moreClick(
											personal_500px_list_photos,
											personal_500px_count);
									personal_500px_count++;
								}
							});
				}
				
				if (PhimpMe.FEEDS_LIST_SOHU_PERSONAL) {
					if (listService.indexOf("personal_sohu") == -1) {
						personal_sohu_download=true;
						Button btn_line = new Button(ctx);
						btn_line.setHeight(2);
						btn_line.setWidth(LayoutParams.MATCH_PARENT);
						btn_line.setBackgroundResource(color_line);

						txtpersonal_sohu = new TextView(getContext());
						txtpersonal_sohu.setText("Personal Sohu");
						txtpersonal_sohu.setTextSize(text_size);
						gv_personal_sohu = new GridView(getContext());
						gv_personal_sohu.setPadding(0, 10, 0, 0);
						ln_personal_sohu = new LinearLayout(getContext());
						btn_personal_sohu_more = new ImageButton(getContext());
						btn_personal_sohu_more
								.setImageResource(R.drawable.more_disable);
						btn_personal_sohu_more.setEnabled(false);

						RelativeLayout more_li = new RelativeLayout(getContext());
						btn_personal_sohu_more.setLayoutParams(lp_more);
						more_li.addView(btn_personal_sohu_more);
						more_li.addView(txtpersonal_sohu);
						ln_personal_sohu.setOrientation(LinearLayout.VERTICAL);

						ln_personal_sohu.addView(more_li);
						ln_personal_sohu.addView(btn_line);
						ln_personal_sohu.addView(gv_personal_sohu);

						ln_personal_sohu.setEnabled(false);
						linear_main.addView(ln_personal_sohu);
						personal_sohuadapter = new GridPersonalSohuAdapter(
								list_thumb_personal, bitmap_personal_sohu, ctx);
						gv_personal_sohu.setNumColumns(cols);
						gv_personal_sohu.setAdapter(personal_sohuadapter);
						gv_personal_sohu.setDrawingCacheEnabled(true);
						listService.add("personal_sohu");
					}
					btn_personal_sohu_more
							.setImageResource(R.drawable.more_disable);
					btn_personal_sohu_more.setEnabled(false);
					personal_sohu_count = 1;
					personal_sohuadapter.removeItem();
					tmp_list_personal = Sohu.getPersonalPhotos(ctx,
							"Sohu Personal");
					Log.d("thong", "sohu Personal: " + tmp_list_personal.size());

					// Don't save exist photos
					ArrayList<RSSPhotoItem_Personal> list_save = new ArrayList<RSSPhotoItem_Personal>();
					for (int t = 0; t < tmp_list_personal.size(); t++) {
						String url = tmp_list_personal.get(t).getURL();
						String filepath = rss_folder.getAbsolutePath()
								+ "/"
								+ url.toLowerCase().replace("://", "")
										.replace("/", "_");
						File f = new File(filepath);
						if (!f.exists()) {
							list_save.add(tmp_list_personal.get(t));
						}
					}

					if (list_save.size() == 0) {
						ln_personal_sohu.setLayoutParams(p_zero);
					} else if (list_save.size() <= 3) {
						ln_personal_sohu.setLayoutParams(p_one_row);
					} else
						ln_personal_sohu.setLayoutParams(p_two_row);
					list_photos_personal.addAll(tmp_list_personal);
					personal_sohu_list_photos.clear();
					personal_sohu_list_photos.addAll(list_save);

					personal_array_list.add(personal_sohu_list_photos);
					if (!checkArray_Persional(PhimpMe.phimpme_personal_array_list,personal_sohu_list_photos)) {
						PhimpMe.phimpme_personal_array_list
								.add(personal_sohu_list_photos);
					} else {
						deleteItem_Persional(PhimpMe.phimpme_personal_array_list,personal_sohu_list_photos);
						PhimpMe.phimpme_personal_array_list
								.add(personal_sohu_list_photos);
					}
					btn_personal_sohu_more
							.setOnClickListener(new OnClickListener() {
								@Override
								public void onClick(View v) {
									btn_personal_sohu_more
											.setImageResource(R.drawable.more_disable);
									btn_personal_sohu_more.setEnabled(false);
									if (personal_sohu_list_photos.size()
											- personal_sohu_count
											* NUMBER_PHOTO_NEED_DOWNLOAD > 3) {
										personal_sohu_rows_display += 2;
									} else if ((personal_sohu_list_photos
											.size() - personal_sohu_count
											* NUMBER_PHOTO_NEED_DOWNLOAD) > 0)
										personal_sohu_rows_display += 1;

									int hight_display = DEFAULT_THUMBNAIL_SIZE
											* personal_sohu_rows_display + 100;
									LinearLayout.LayoutParams rep = new LinearLayout.LayoutParams(
											ViewGroup.LayoutParams.WRAP_CONTENT,
											hight_display);
									ln_personal_sohu.setLayoutParams(rep);
									ln_personal_sohu.setEnabled(true);
									personal_moreClick(
											personal_sohu_list_photos,
											personal_sohu_count);
									personal_sohu_count++;
								}
							});
				}
				Log.d("thong", "List photos: " + list_photos.size());
				Log.d("luong",
						"List photos personal: " + list_photos_personal.size());
				Log.d("luong", "list test: " + array_list.size());
				Log.d("luong",
						"list test personal: " + personal_array_list.size());				
				for (int i = 0; i < array_list.size(); i++) {
					if (array_list.get(i).size() > 0) {
						Log.d("thong", "RunOnUiThread");
						final RSSPhotoItem[] tmp;
						if (array_list.get(i).size() < NUMBER_PHOTO_NEED_DOWNLOAD) {
							tmp = new RSSPhotoItem[array_list.get(i).size()];
						} else {
							tmp = new RSSPhotoItem[NUMBER_PHOTO_NEED_DOWNLOAD];
						}
						for (int j = 0; j < tmp.length; j++) {
							tmp[j] = array_list.get(i).get(j);
						}
						getActivity().runOnUiThread(new Runnable() {
							@Override
							public void run() {

								new DownloadImageAsyncTask().execute(tmp);
							}
						});

					}
				}
				for (int i = 0; i < personal_array_list.size(); i++) {
					if (personal_array_list.get(i).size() > 0) {
						Log.d("thong", "RunOnUiThread");
						final RSSPhotoItem_Personal[] tmp;
						if (personal_array_list.get(i).size() < NUMBER_PHOTO_NEED_DOWNLOAD) {
							tmp = new RSSPhotoItem_Personal[personal_array_list
									.get(i).size()];
						} else {
							tmp = new RSSPhotoItem_Personal[NUMBER_PHOTO_NEED_DOWNLOAD];
						}
						for (int j = 0; j < tmp.length; j++) {
							tmp[j] = personal_array_list.get(i).get(j);
						}
						getActivity().runOnUiThread(new Runnable() {
							@Override
							public void run() {

								new DownloadImageAsyncTask_Personal()
										.execute(tmp);
							}
						});

					}
				}
			}
		} else {
			Log.e("newGallery", "Don't connect internet");
			RelativeLayout.LayoutParams lp_more = new RelativeLayout.LayoutParams(
					40, 40);
			
			lp_more.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
			
			if (PhimpMe.FEEDS_LIST_FLICKR_PUBLIC) {									
					setOffline(ln_flickr, p_flickr, btn_flickr_more, txtPFlickr, lp_more, "Public Flickr");
				
				}
				if (PhimpMe.FEEDS_LIST_FLICKR_RECENT) {									
					setOffline(ln_recent_flickr, recent_flickr, btn_recent_flickr_more, txtRecentFlickr, lp_more,"Recent Flickr");
				}
				if (PhimpMe.FEEDS_LIST_YAHOO_NEWS) {					
					setOffline(ln_yahoo, p_yahoo, btn_yahoo_more, txtyahoo, lp_more, "Public Yahoo");
				}
				
				if (PhimpMe.FEEDS_LIST_GOOGLE_PICASA_PUBLIC) {					
					setOffline(ln_public_picasa, p_picasa, btn_public_picasa_more, txtPublicPicasa, lp_more, "Public Picasa");
				}
				
				if (PhimpMe.FEEDS_LIST_GOOGLE_NEWS) {					
					setOffline(ln_googlenews, p_googlenews, btn_googlenews_more, txtGooglenews, lp_more, "Google News");
				}
				
				if (PhimpMe.FEEDS_LIST_DEVIANTART_PUBLIC) {					
					setOffline(ln_deviant, p_deviant, btn_public_deviant_more, txtdeviant, lp_more, "Public DeviantArt");
				}
				
				if (PhimpMe.FEEDS_LIST_MYSERVICES) {					
					setOffline(ln_my_feed_services, gv_my_feed_services, btn_my_feed_services_more, txtMyFeedServices, lp_more, "My Feed Photo");
				}
				
				if (PhimpMe.FEEDS_LIST_FACEBOOK_PRIVATE) {					
					setOffline(ln_facebook, gv_personal_facebook, btn_facebook_more, txtfacebook, lp_more, "Personal Facebook");
				}
				
				if (PhimpMe.FEEDS_LIST_TUMBLR_PRIVATE) {					
					setOffline(ln_tumblr, gv_personal_tumblr, btn_tumblr_more, txttumblr, lp_more, "Tumblr Personal");
				}
				
				if (PhimpMe.FEEDS_LIST_VK) {					
					setOffline(ln_vkontakte, gv_personal_vkontakte, btn_vkontakte_more, txtvkontakte, lp_more, "Personal Vkontakte");

				}
				
				if (PhimpMe.FEEDS_LIST_FLICKR_PRIVATE) {					
					setOffline(ln_personal_flickr, gv_personal_flickr,btn_personal_flickr_more, txtpersonal_flickr, lp_more, "Personal Flickr");
				}
				
				if (PhimpMe.FEEDS_LIST_GOOGLE_PICASA_PRIVATE) {					
					setOffline(ln_personal_picasa, gv_personal_picasa, btn_personal_picasa_more, txtpersonal_picasa, lp_more, "Personal Picasa");
				}
				
				if (PhimpMe.FEEDS_LIST_DEVIANTART_PRIVITE) {					
					setOffline(ln_personal_deviantart, gv_personal_deviantart, btn_personal_deviant_more, txtpersonal_deviantart, lp_more, "Personal DevianArt");
				}
				
				if (PhimpMe.FEEDS_LIST_IMGUR_PERSONAL) {					
					setOffline(ln_personal_imgur, gv_personal_imgur, btn_personal_imgur_more, txtpersonal_imgur, lp_more, "Personal Imgur");
				}
				
				if (PhimpMe.FEEDS_LIST_SOHU_PERSONAL) {					
					setOffline(ln_personal_sohu, gv_personal_sohu, btn_personal_sohu_more, txtpersonal_sohu, lp_more, "Personal Sohu");
				}
			
		}
	}
	public void setOffline(LinearLayout linear, GridView grid, ImageButton btn, TextView txt, RelativeLayout.LayoutParams lp_more, String text){
		LinearLayout.LayoutParams p_two_row = new LinearLayout.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT,
				DEFAULT_THUMBNAIL_SIZE * 2 + 120);
		
		Button btn_line = new Button(ctx);
		btn_line.setHeight(2);
		btn_line.setWidth(LayoutParams.MATCH_PARENT);
		btn_line.setBackgroundResource(color_line);
		
		Button btn_line_black = new Button(ctx);
		btn_line_black.setHeight(10);
		btn_line_black.setWidth(LayoutParams.MATCH_PARENT);
		btn_line_black.setBackgroundResource(R.color.black);
		
		txt = new TextView(getContext());
		txt.setText(text);
		txt.setTextSize(text_size);
		grid = new GridView(getContext());
		grid.setBackgroundResource(R.color.white);
		linear = new LinearLayout(getContext());
		btn = new ImageButton(getContext());
		btn.setImageResource(R.drawable.more_disable);
		btn.setEnabled(false);

		RelativeLayout more_li = new RelativeLayout(getContext());
		btn.setLayoutParams(lp_more);
		more_li.addView(btn);
		more_li.addView(txt);
		linear.setOrientation(LinearLayout.VERTICAL);

		linear.addView(more_li);
		linear.addView(btn_line);
		linear.addView(btn_line_black);
		linear.addView(grid);
		linear.setLayoutParams(p_two_row);
		linear.setEnabled(false);
		linear_main.addView(linear);
		GridviewNotWifiAddapter grid_offline=new GridviewNotWifiAddapter(ctx);
		grid.setNumColumns(cols);
		grid.setAdapter(grid_offline);
		grid.setDrawingCacheEnabled(true);
		
	}
	// download Public photos
	private class DownloadImageAsyncTask extends
			AsyncTask<RSSPhotoItem[], String, Long> {
		int complete_file = 0;

		protected Long doInBackground(RSSPhotoItem[]... items) {
			
			long size = 0;

			final RSSPhotoItem[] _items = items[0];

			for (int i = 0; i < _items.length; i++) {

				RSSPhotoItem item = _items[i];

				Log.d("thong", "	URL download img: " + item.getURL());

				String url = item.getURL();
				String filepath = rss_folder.getAbsolutePath()
						+ "/"
						+ url.toLowerCase().replace("://", "")
								.replace("/", "_");
				File f = new File(filepath);
				
				getActivity().runOnUiThread(new Runnable() {
					@Override
					public void run() {
					}
				});

				if (!f.exists()) {
					long max_size = PhimpMe.MAX_FILESIZE_DOWNLOAD * 1024 * 1024;

					if (RSSUtil.downloadFile(ctx, url, f.getAbsolutePath(),
							max_size)) {
						final String title = item.getTitle();
						final String urlstr = f.getAbsolutePath();
						String la = "";
						String lo = "";
						ExifInterface exif_data = null;
						geoDegrees _g = null;
						try {
							exif_data = new ExifInterface(urlstr);
							_g = new geoDegrees(exif_data);
							if (_g.isValid()) {
								la = _g.getLatitude() + "";
								lo = _g.getLongitude() + "";
							}
						} catch (IOException e) {
							e.printStackTrace();
						} finally {
							exif_data = null;
							_g = null;
						}
						final String latitude = la;
						final String longitude = lo;
						final String link = item.getLink();
						final String service = item.getService();
						final String description = item.getDescription();

						final String thumb_path = RSSUtil
								.getThumbPhotos(ctx, f);

						DownloadedPhotoDBItem.insert(ctx, null, urlstr,
								thumb_path, title, latitude, longitude, link,
								service, description);
						
						complete_file++;
						getActivity().runOnUiThread(new Runnable() {
							@Override
							public void run() {
								bitmap_list.add(BitmapFactory.decodeFile(thumb_path));
								RSSPhotoItem newitem = new RSSPhotoItem();
								newitem.setTitle(title);
								newitem.setURL(urlstr);
								newitem.setThumb(thumb_path);
								
								if (service.equals("public_flickr")) {
									bitmap_p_flickr.add(0, BitmapFactory.decodeFile(thumb_path));
									flickradapter.addItem(newitem);
									//Log.e("newGallery","complete_file public flick : "+complete_file);
									if(complete_file % 6 ==0){
										btn_flickr_more.setImageResource(R.drawable.more);
										btn_flickr_more.setEnabled(true);
									}
								}
								if (service.equals("recent_flickr")) {
									bitmap_recent_flickr.add(0, BitmapFactory.decodeFile(thumb_path));
									recentflickradapter.addItem(newitem);
									//Log.e("newGallery","complete_file recent flick : "+complete_file);
									if(complete_file % 6 ==0){
										btn_recent_flickr_more.setImageResource(R.drawable.more);
										btn_recent_flickr_more.setEnabled(true);
									}
									

								}
								if (service.equals("public_picasa")) {
									bitmap_public_picasa.add(0, BitmapFactory.decodeFile(thumb_path));
									publicpicasaadapter.addItem(newitem);
									if(complete_file % 6 ==0){
										btn_public_picasa_more.setImageResource(R.drawable.more);
										btn_public_picasa_more.setEnabled(true);
									}

								}
								if (service.equals("google_news")) {
									bitmap_google_news.add(0, BitmapFactory.decodeFile(thumb_path));
									googlenewsadapter.addItem(newitem);
									if(complete_file % 6 ==0){
										btn_googlenews_more.setImageResource(R.drawable.more);
										btn_googlenews_more.setEnabled(true);
									}

								}
								if (service.equals("public_deviant")) {
									bitmap_p_deviant.add(0, BitmapFactory.decodeFile(thumb_path));
									deviantadapter.addItem(newitem);
									if(complete_file % 6 ==0){
										btn_public_deviant_more.setImageResource(R.drawable.more);
										btn_public_deviant_more.setEnabled(true);
									}

								}
								if (service.equals("public_yahoo")) {
									bitmap_p_yahoo.add(0, BitmapFactory.decodeFile(thumb_path));
									yahooadapter.addItem(newitem);
									if(complete_file % 6 ==0){
										btn_yahoo_more.setImageResource(R.drawable.more);
										btn_yahoo_more.setEnabled(true);
									}

								}
								if (service.equals("my_feed_services")) {
									bitmap_my_feed_services.add(0,BitmapFactory.decodeFile(thumb_path));
									my_feed_services_adapter.addItem(newitem);
									if(complete_file % 6 ==0){
										btn_my_feed_services_more.setImageResource(R.drawable.more);
										btn_my_feed_services_more.setEnabled(true);
									}
								}																
								if (service.equals("my_feed_services1")) {
									bitmap_my_feed_services1.add(0,BitmapFactory.decodeFile(thumb_path));
									my_feed_services_adapter1.addItem(newitem);
									if(complete_file % 6 ==0){
										btn_my_feed_services_more1.setImageResource(R.drawable.more);
										btn_my_feed_services_more1.setEnabled(true);
									}
								}
								if (service.equals("my_feed_services2")) {
									bitmap_my_feed_services2.add(0,BitmapFactory.decodeFile(thumb_path));
									my_feed_services_adapter2.addItem(newitem);
									if(complete_file % 6 ==0){
										btn_my_feed_services_more2.setImageResource(R.drawable.more);
										btn_my_feed_services_more2.setEnabled(true);
									}
								}
								if (service.equals("my_feed_services3")) {
									bitmap_my_feed_services3.add(0,BitmapFactory.decodeFile(thumb_path));
									my_feed_services_adapter3.addItem(newitem);
									if(complete_file % 6 ==0){
										btn_my_feed_services_more3.setImageResource(R.drawable.more);
										btn_my_feed_services_more3.setEnabled(true);
									}
								}
								if (service.equals("my_feed_services4")) {
									bitmap_my_feed_services4.add(0,BitmapFactory.decodeFile(thumb_path));
									my_feed_services_adapter4.addItem(newitem);
									if(complete_file % 6 ==0){
										btn_my_feed_services_more4.setImageResource(R.drawable.more);
										btn_my_feed_services_more4.setEnabled(true);
									}
								}
								if (service.equals("my_feed_services5")) {
									bitmap_my_feed_services5.add(0,BitmapFactory.decodeFile(thumb_path));
									my_feed_services_adapter5.addItem(newitem);
									if(complete_file % 6 ==0){
										btn_my_feed_services_more5.setImageResource(R.drawable.more);
										btn_my_feed_services_more5.setEnabled(true);
									}
								}
								newitem = null;
							}
						});
					} else {
						Log.d("thread", "Save image fail");
					}
				}
			}
			return size;
		}

		@Override
		public void onProgressUpdate(String... value) {

		}

		@Override
		protected void onPostExecute(Long result) {				
			 for(int i=0; i<PhimpMe.phimpme_array_list.size(); i++){
			  if((filckr_list_not_save
			  .size()-flickr_count*NUMBER_PHOTO_NEED_DOWNLOAD)>0){
			  btn_flickr_more.setImageResource(R.drawable.more);
			  btn_flickr_more.setEnabled(true); }
			  if((recent_filckr_list_not_save
			  .size()-recent_flickr_count*NUMBER_PHOTO_NEED_DOWNLOAD)>0){
			  btn_recent_flickr_more.setImageResource(R.drawable.more);
			  btn_recent_flickr_more.setEnabled(true); }
			  if((pub_deviant_list_not_save
			  .size()-deviant_count*NUMBER_PHOTO_NEED_DOWNLOAD)>0){
			  btn_public_deviant_more.setImageResource(R.drawable.more);
			  btn_public_deviant_more.setEnabled(true); }
			  if((google_news_list_not_save
			  .size()-googlenews_count*NUMBER_PHOTO_NEED_DOWNLOAD)>0){
			  btn_googlenews_more.setImageResource(R.drawable.more);
			  btn_googlenews_more.setEnabled(true); }
			  if((pub_picasa_list_not_save
			  .size()-public_picasa_count*NUMBER_PHOTO_NEED_DOWNLOAD)>0 ){
			  btn_public_picasa_more.setImageResource(R.drawable.more);
			  btn_public_picasa_more.setEnabled(true); }
			  if((yahoo_list_not_save
			  .size()-yahoo_count*NUMBER_PHOTO_NEED_DOWNLOAD)>0){
			  btn_yahoo_more.setImageResource(R.drawable.more);
			  btn_yahoo_more.setEnabled(true); }
			  if((my_feed_list_not_save.size(
			  )-my_feed_services_count*NUMBER_PHOTO_NEED_DOWNLOAD)>0){
			  btn_my_feed_services_more.setImageResource(R.drawable.more);
			  btn_my_feed_services_more.setEnabled(true); }
			  if((my_feed1_list_not_save
			  .size()-my_feed_services_count1*NUMBER_PHOTO_NEED_DOWNLOAD)>0){
			  btn_my_feed_services_more1.setImageResource(R.drawable.more);
			  btn_my_feed_services_more1.setEnabled(true);
			  }if((my_feed2_list_not_save
			  .size()-my_feed_services_count2*NUMBER_PHOTO_NEED_DOWNLOAD)>0){
			  btn_my_feed_services_more2.setImageResource(R.drawable.more);
			  btn_my_feed_services_more2.setEnabled(true);
			  }if((my_feed3_list_not_save
			  .size()-my_feed_services_count3*NUMBER_PHOTO_NEED_DOWNLOAD)>0){
			  btn_my_feed_services_more3.setImageResource(R.drawable.more);
			  btn_my_feed_services_more3.setEnabled(true);
			  }if((my_feed4_list_not_save
			  .size()-my_feed_services_count4*NUMBER_PHOTO_NEED_DOWNLOAD)>0){
			  btn_my_feed_services_more4.setImageResource(R.drawable.more);
			  btn_my_feed_services_more4.setEnabled(true);
			  }if((my_feed5_list_not_save
			  .size()-my_feed_services_count5*NUMBER_PHOTO_NEED_DOWNLOAD)>0){
			  btn_my_feed_services_more5.setImageResource(R.drawable.more);
			  btn_my_feed_services_more5.setEnabled(true); }}
			 getActivity().sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://"
			            + Environment.getExternalStorageDirectory()))); 
			 
		}

	}

	// download personal photos
	private class DownloadImageAsyncTask_Personal extends
			AsyncTask<RSSPhotoItem_Personal[], String, Long> {
		int complete_file = 0;
		protected Long doInBackground(RSSPhotoItem_Personal[]... items) {
			long size = 0;
			
			final RSSPhotoItem_Personal[] _items = items[0];
			for (int i = 0; i < _items.length; i++) {
				RSSPhotoItem_Personal item = _items[i];

				Log.d("thong", "	URL download img: " + item.getURL());

				String url = item.getURL();
				String filepath = rss_folder.getAbsolutePath()
						+ "/"
						+ url.toLowerCase().replace("://", "")
								.replace("/", "_");
				File f = new File(filepath);
				
				getActivity().runOnUiThread(new Runnable() {
					@Override
					public void run() {

					}
				});

				if (!f.exists()) {
					long max_size = PhimpMe.MAX_FILESIZE_DOWNLOAD * 1024 * 1024;

					if (RSSUtil.downloadFile(ctx, url, f.getAbsolutePath(),
							max_size)) {

						final String title = item.getTitle();
						final String urlstr = f.getAbsolutePath();
						String la = "";
						String lo = "";
						ExifInterface exif_data = null;
						geoDegrees _g = null;
						try {
							exif_data = new ExifInterface(urlstr);
							_g = new geoDegrees(exif_data);
							if (_g.isValid()) {
								la = _g.getLatitude() + "";
								lo = _g.getLongitude() + "";
							}
						} catch (IOException e) {
							e.printStackTrace();
						} finally {
							exif_data = null;
							_g = null;
						}
						final String latitude = la;
						final String longitude = lo;
						final String link = item.getLink();
						final String service = item.getService();
						final String description = item.getDescription();

						final String thumb_path = RSSUtil
								.getThumbPhotos(ctx, f);
						DownloadedPersonalPhotoDBItem.insert(ctx, null, urlstr,
								thumb_path, title, latitude, longitude, link,
								service, description);

						complete_file++;
						
						getActivity().runOnUiThread(new Runnable() {
							@Override
							public void run() {
								bitmap_list.add(0,
										BitmapFactory.decodeFile(thumb_path));
								RSSPhotoItem_Personal newitem = new RSSPhotoItem_Personal();
								newitem.setTitle(title);
								newitem.setURL(urlstr);
								if (service.equals("personal_facebook")) {
									bitmap_personal_facebook.add(0,BitmapFactory.decodeFile(thumb_path));
									facebookadapter.addItem(newitem);
									if(complete_file % 6 ==0){
										btn_facebook_more.setImageResource(R.drawable.more);
										btn_facebook_more.setEnabled(true);
									}
								}
								if (service.equals("personal_tumblr")) {
									bitmap_personal_tumblr.add(0, BitmapFactory.decodeFile(thumb_path));
									tumblradapter.addItem(newitem);
									if(complete_file % 6 ==0){
										btn_tumblr_more.setImageResource(R.drawable.more);
										btn_tumblr_more.setEnabled(true);
									}
								}
								if (service.equals("personal_vkontakte")) {
									bitmap_personal_vkontakte.add(0,BitmapFactory.decodeFile(thumb_path));
									vkontakteadapter.addItem(newitem);
									if(complete_file % 6 ==0){
										btn_vkontakte_more.setImageResource(R.drawable.more);
										btn_vkontakte_more.setEnabled(true);
									}
								}
								if (service.equals("personal_flickr")) {
									bitmap_personal_flickr.add(0, BitmapFactory.decodeFile(thumb_path));
									personal_flickradapter.addItem(newitem);
									if(complete_file % 6 ==0){
										btn_personal_flickr_more.setImageResource(R.drawable.more);
										btn_personal_flickr_more.setEnabled(true);
									}
								}
								if (service.equals("personal_picasa")) {
									bitmap_personal_picasa.add(0, BitmapFactory.decodeFile(thumb_path));
									personal_picasaadapter.addItem(newitem);
									if(complete_file % 6 ==0){
										btn_personal_picasa_more.setImageResource(R.drawable.more);
										btn_personal_picasa_more.setEnabled(true);
									}
								}
								if (service.equals("personal_deviantart")) {
									bitmap_personal_deviantart.add(0,BitmapFactory.decodeFile(thumb_path));
									personal_deviantartadapter.addItem(newitem);
									if(complete_file % 6 ==0){
										btn_personal_deviant_more.setImageResource(R.drawable.more);
										btn_personal_deviant_more.setEnabled(true);
									}
								}
								if (service.equals("personal_imgur")) {
									bitmap_personal_imgur.add(0, BitmapFactory.decodeFile(thumb_path));
									personal_imguradapter.addItem(newitem);
									if(complete_file % 6 ==0){
										btn_personal_imgur_more.setImageResource(R.drawable.more);
										btn_personal_imgur_more.setEnabled(true);
									}
								}
								if (service.equals("personal_kaixin")) {
									bitmap_personal_kaixin.add(0, BitmapFactory.decodeFile(thumb_path));
									personal_kaixinadapter.addItem(newitem);
									if(complete_file % 6 ==0){
										btn_personal_kaixin_more.setImageResource(R.drawable.more);
										btn_personal_kaixin_more.setEnabled(true);
									}
								}								
								if (service.equals("personal_sohu")) {
									bitmap_personal_sohu.add(0, BitmapFactory.decodeFile(thumb_path));
									personal_sohuadapter.addItem(newitem);
									if(complete_file % 6 ==0){
										btn_personal_sohu_more.setImageResource(R.drawable.more);
										btn_personal_sohu_more.setEnabled(true);
									}
								}

								newitem = null;
							}
						});
					} else {
						Log.d("thread", "Save image fail");
					}
				}
			}
			return size;
		}

		@Override
		public void onProgressUpdate(String... value) {
		}

		@Override
		protected void onPostExecute(Long result) {
			for (int i = 0; i < PhimpMe.phimpme_personal_array_list.size(); i++) {
				if ((per_deviant_list_not_save.size() - personal_deviantart_count
						* NUMBER_PHOTO_NEED_DOWNLOAD) > 0) {
					btn_flickr_more.setImageResource(R.drawable.more);
					btn_flickr_more.setEnabled(true);
				}
				if ((per_facebook_list_not_save.size() - personal_facebook_count
						* NUMBER_PHOTO_NEED_DOWNLOAD) > 0) {
					btn_recent_flickr_more.setImageResource(R.drawable.more);
					btn_recent_flickr_more.setEnabled(true);
				}
				if ((per_flickr_list_not_save.size() - personal_flickr_count
						* NUMBER_PHOTO_NEED_DOWNLOAD) > 0) {
					btn_public_deviant_more.setImageResource(R.drawable.more);
					btn_public_deviant_more.setEnabled(true);
				}
				if ((per_picasa_list_not_save.size() - personal_picasa_count
						* NUMBER_PHOTO_NEED_DOWNLOAD) > 0) {
					btn_googlenews_more.setImageResource(R.drawable.more);
					btn_googlenews_more.setEnabled(true);
				}
				if ((per_tumblr_list_not_save.size() - personal_tumblr_count
						* NUMBER_PHOTO_NEED_DOWNLOAD) > 0) {
					btn_public_picasa_more.setImageResource(R.drawable.more);
					btn_public_picasa_more.setEnabled(true);
				}
				if ((per_vk_list_not_save.size() - personal_vkontakte_count
						* NUMBER_PHOTO_NEED_DOWNLOAD) > 0) {
					btn_yahoo_more.setImageResource(R.drawable.more);
					btn_yahoo_more.setEnabled(true);
				}
				if ((per_imgur_list_not_save.size() - personal_imgur_count
						* NUMBER_PHOTO_NEED_DOWNLOAD) > 0) {
					btn_my_feed_services_more.setImageResource(R.drawable.more);
					btn_my_feed_services_more.setEnabled(true);
				}
				if ((per_kaixin_list_not_save.size() - personal_kaixin_count
						* NUMBER_PHOTO_NEED_DOWNLOAD) > 0) {
					btn_my_feed_services_more1
							.setImageResource(R.drawable.more);
					btn_my_feed_services_more1.setEnabled(true);
				}
				if ((per_500px_list_not_save.size() - personal_500px_count
						* NUMBER_PHOTO_NEED_DOWNLOAD) > 0) {
					btn_my_feed_services_more2
							.setImageResource(R.drawable.more);
					btn_my_feed_services_more2.setEnabled(true);
				}
				if ((per_sohu_list_not_save.size() - personal_sohu_count
						* NUMBER_PHOTO_NEED_DOWNLOAD) > 0) {
					btn_my_feed_services_more3
							.setImageResource(R.drawable.more);
					btn_my_feed_services_more3.setEnabled(true);
				}
			}
			getActivity().sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://"
		            + Environment.getExternalStorageDirectory()))); 
		}

	}

	// Function button more touch
	public void moreClick(ArrayList<RSSPhotoItem> list, int count) {
		if (list.size() > 0) {
			Log.d("thong", "RunOnUiThread");
			final RSSPhotoItem[] tmp;
			if ((list.size() - count * NUMBER_PHOTO_NEED_DOWNLOAD) >= NUMBER_PHOTO_NEED_DOWNLOAD) {
				tmp = new RSSPhotoItem[NUMBER_PHOTO_NEED_DOWNLOAD];
			} else if((list.size() - count* NUMBER_PHOTO_NEED_DOWNLOAD)>0){
				tmp = new RSSPhotoItem[list.size() - count
						* NUMBER_PHOTO_NEED_DOWNLOAD];
			}else{
				tmp = null;
			}
			if (tmp.length != 0) {
				for (int j = 0; j < tmp.length; j++) {
					tmp[j] = list.get(count * NUMBER_PHOTO_NEED_DOWNLOAD + j);
				}
				getActivity().runOnUiThread(new Runnable() {
					@Override
					public void run() {

						new DownloadImageAsyncTask().execute(tmp);
					}
				});
			}
		}
	}

	public void personal_moreClick(ArrayList<RSSPhotoItem_Personal> list,
			int count) {
		if (list.size() > 0) {
			Log.d("thong", "RunOnUiThread");
			final RSSPhotoItem_Personal[] tmp;
			if ((list.size() - count * NUMBER_PHOTO_NEED_DOWNLOAD) >= NUMBER_PHOTO_NEED_DOWNLOAD) {
				tmp = new RSSPhotoItem_Personal[NUMBER_PHOTO_NEED_DOWNLOAD];
			} else if((list.size() - count* NUMBER_PHOTO_NEED_DOWNLOAD)>0){
				tmp = new RSSPhotoItem_Personal[list.size() - count
						* NUMBER_PHOTO_NEED_DOWNLOAD];
			}else tmp =null;
			if (tmp.length != 0) {
				for (int j = 0; j < tmp.length; j++) {
					tmp[j] = list.get(count * NUMBER_PHOTO_NEED_DOWNLOAD + j);
				}
				getActivity().runOnUiThread(new Runnable() {
					@Override
					public void run() {

						new DownloadImageAsyncTask_Personal().execute(tmp);
					}
				});
			}
		}
	}

	public static void clearAllPhoto() {
		list_photos.clear();
		list_photos_personal.clear();
		array_list.clear();
		personal_array_list.clear();
		PhimpMe.phimpme_array_list.clear();
		PhimpMe.phimpme_personal_array_list.clear();
		// Clear bitmap
		bitmap_list.clear();
		bitmap_p_flickr.clear();
		bitmap_recent_flickr.clear();
		bitmap_public_picasa.clear();
		bitmap_p_yahoo.clear();
		bitmap_p_deviant.clear();
		bitmap_personal_facebook.clear();
		bitmap_personal_tumblr.clear();
		bitmap_personal_vkontakte.clear();
		bitmap_personal_flickr.clear();
		bitmap_personal_picasa.clear();
		bitmap_personal_deviantart.clear();
		bitmap_personal_imageshack.clear();
		bitmap_personal_imgur.clear();
		bitmap_public_imgur.clear();
		bitmap_my_feed_services.clear();
		bitmap_public_500px.clear();
		bitmap_personal_kaixin.clear();
		bitmap_personal_500px.clear();
		bitmap_personal_sohu.clear();
		bitmap_my_feed_services1.clear();
		bitmap_my_feed_services2.clear();
		bitmap_my_feed_services3.clear();
		bitmap_my_feed_services4.clear();
		bitmap_my_feed_services5.clear();
		if (linear_main.getChildCount() != 0) {
			linear_main.removeAllViews();			
		}
		//txtStatus.setText("");
		listService.clear();
		PhimpMe.check = 0;
	}
	public boolean checkArray(ArrayList<ArrayList<RSSPhotoItem>> _object, ArrayList<RSSPhotoItem> _obj){
		try{
			boolean c = false;
			for(int i=0; i<_object.size(); i++){
				if(_object.get(i).get(0).getService().equals(_obj.get(0).getService())){
					c=true;	
					break;
				}
			}
			return c;
		}catch(Exception e){
			return false;
		}
	}
	public boolean checkArray_Persional(ArrayList<ArrayList<RSSPhotoItem_Personal>> _object, ArrayList<RSSPhotoItem_Personal> _obj){
		try{
			boolean c = false;
			for(int i=0; i<_object.size(); i++){
				if(_object.get(i).get(0).getService().equals(_obj.get(0).getService())){
					c =  true;
					break;
				}
			}
			return c;
		}catch(Exception e){
			return false;
		}
	}
	public boolean deleteItem(ArrayList<ArrayList<RSSPhotoItem>> _object, ArrayList<RSSPhotoItem> _obj){
		try{
			int index=-1;
			for(int i=0; i<_object.size(); i++){
				if(_object.get(i).get(0).getService().equals(_obj.get(0).getService())){
					index = i;	
					break;
				}
			}
			if(index!=-1){
				_object.remove(index);
				return true;
			}else return false;
		}catch(Exception e){
			return false;
		}
	}
	public boolean deleteItem_Persional(ArrayList<ArrayList<RSSPhotoItem_Personal>> _object, ArrayList<RSSPhotoItem_Personal> _obj){
		try{
			int index=-1;
			for(int i=0; i<_object.size(); i++){
				if(_object.get(i).get(0).getService().equals(_obj.get(0).getService())){
					index = i;	
					break;
				}
			}
			if(index!=-1){
				_object.remove(index);
				return true;
			}else return false;
		}catch(Exception e){
			return false;
		}
	}

	
}

