package vn.mbm.phimp.me;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.share.widget.ShareDialog;
import com.joooid.android.model.User;
import com.joooid.android.xmlrpc.Constants;
import com.joooid.android.xmlrpc.JoooidRpc;
import com.tani.app.ui.IconContextMenu;

import org.json.JSONObject;
import org.wordpress.android.NewAccount;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import vn.mbm.phimp.me.database.AccountItem;
import vn.mbm.phimp.me.database.DrupalItem;
import vn.mbm.phimp.me.database.ImageshackItem;
import vn.mbm.phimp.me.database.JoomlaItem;
import vn.mbm.phimp.me.feedservice.FacebookActivity;
import vn.mbm.phimp.me.gallery3d.media.CropImage;
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
import vn.mbm.phimp.me.utils.Image;
import vn.mbm.phimp.me.utils.Params;
import vn.mbm.phimp.me.utils.PrefManager;
import vn.mbm.phimp.me.utils.geoDegrees;

import static vn.mbm.phimp.me.utils.Constants.TYPE_MULTI_PICKER;

//import com.google.android.maps.GeoPoint;

public class Upload extends android.support.v4.app.Fragment {
    private final int CONTEXT_MENU_ID = 1;

    private final int DIALOG_ADD_PHOTO = 2;

    private final int DIALOG_ADD_ACCOUNT_DRUPAL = 3;

    private final int DIALOG_ADD_ACCOUNT_IMAGESHACK = 4;

    private final int DIALOG_ADD_ACCOUNT_WORDPRESS = 5;

    private final int DIALOG_ADD_ACCOUNT_JOOMLA = 6;

    private final int SELECT_IMAGE_FROM_GALLERY = 3;

    private final int TAKE_PICTURE = 4;

    private final int GET_POSITION_ON_MAP = 5;

    private IconContextMenu iconContextMenu = null;

    private final int CROP_IMAGE = 0;

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

    private final int SERVICES_500PX_ACTION = 11;

    private final int SERVICES_IMGUR_ACTION = 13;

    private final int SERVICES_KAIXIN_ACTION = 12;

    private final int SERVICES_SOHU_ACTION = 15;

    private final int SERVICES_WORDPRESSDOTCOM_ACTION = 16;

    private final int SERVICES_WORDPRESS_ACTION = 17;

    private final int SERVICES_JOOMLA_ACTION = 18;

    private static Uri camera_img_uri;

    public static ProgressDialog progLoading;

    static Context ctx;

    CallbackManager callbackManager;
    //private WebView webView;
    ListView listAccounts;

    GridView listPhotoUpload;
    TextView noPhotos;

    ImageView btnAdd;

    ImageView btnPhotoAdd;

    //bluetooth share
    public static ImageView btnBluetoothShare;

    static String[] path;

    Uri uri;

    /*ImageButton btnUseCurrentPosition;*/
    ImageButton btnUseMap;

    ImageView imgPreview;

    ImageView btnUpload;

    EditText txtPhotoTitle;

    EditText txtPhotoDescription;

    EditText txtLongtitude;

    EditText txtLatitude;

    EditText txtTags;

    String[] id;

    String[] name;

    String[] service;

    int color;

    // Views to toggle
    LinearLayout clearPanel, uploadPanel;
    // Upload panel lable
    TextView panelLable;
    // Image buttons to clear panel
    ImageView removeSelectedBtn, selectAllBtn, clearSelectionBtn;
    // Upload image list and temporary list to store removable items
    public static ArrayList<String> uploadGridList = new ArrayList<>();
    private ArrayList<String> removableList;

    private static String longtitude = "", latitude = "", title = "";

    Bitmap bmp_scale = null;

    static boolean upload_photo_process = false;

    public boolean checkListAccount() {
        boolean check = false;
        for (String s : PhimpMe.checked_accounts.keySet()) {
            if (PhimpMe.checked_accounts.get(s) == true) check = true;

        }
        return check;
    }

    public static ProgressDialog uploadDialog;

    ProgressDialog pd;

    ProgressDialog gpsloading;

    long totalSize;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.upload, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        color = getActivity().getResources().getColor(R.color.icongrey);
        FacebookSdk.sdkInitialize(getActivity().getApplicationContext());
        callbackManager = CallbackManager.Factory.create();

        final ShareDialog shareDialog = new ShareDialog(getActivity());
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        Resources res = getResources();
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        ctx = getContext();
        Log.d("Upload", "Upload Start");
        noPhotos = (TextView) view.findViewById(R.id.nophotos);
        // Initiate toggle panels
        clearPanel = (LinearLayout) view.findViewById(R.id.clearButtonPanel);
        uploadPanel = (LinearLayout) view.findViewById(R.id.uploadButtonPanel);
        clearPanel.setVisibility(View.GONE);
        uploadPanel.setVisibility(View.VISIBLE);
        // Set panel title as "Upload"
        panelLable = (TextView) view.findViewById(R.id.uploadPanelLable);
        panelLable.setText(R.string.upload);
        // Initiate removable upload item list
        removableList = new ArrayList<>();
        // Initiate clear panel buttons
        selectAllBtn = (ImageView) view.findViewById(R.id.btnSelectAll);
        selectAllBtn.setColorFilter(color);
        selectAllBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                removableList.clear();
                for (String path : uploadGridList) {
                    removableList.add(path);
                }
                ((ImageAdapter) listPhotoUpload.getAdapter()).notifyDataSetChanged();
                int delCount = removableList.size();
                String newTitle;
                if (delCount > 0) {
                    newTitle = delCount + (delCount == 1 ? " image" : " images") + " selected";
                    toggleButtonPanel(true);
                } else {
                    newTitle =  getResources().getString(R.string.upload);
                    toggleButtonPanel(false);
                }
                panelLable.setText(newTitle);
            }
        });

        clearSelectionBtn = (ImageView) view.findViewById(R.id.btnDeselectAll);
        clearSelectionBtn.setColorFilter(color);
        clearSelectionBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                removableList.clear();
                ((ImageAdapter) listPhotoUpload.getAdapter()).notifyDataSetChanged();
                panelLable.setText(getResources().getString(R.string.upload));
                toggleButtonPanel(false);
            }
        });

        removeSelectedBtn = (ImageView) view.findViewById(R.id.btnClearSelected);
        removeSelectedBtn.setColorFilter(color);
        removeSelectedBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder d = new AlertDialog.Builder(getContext());
                d.setMessage(R.string.AlertMessage).setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (!removableList.isEmpty()) {
                                    uploadGridList.removeAll(removableList);
                                    removableList.clear();
                                    ((ImageAdapter) listPhotoUpload.getAdapter()).notifyDataSetChanged();
                                    panelLable.setText(getResources().getString(R.string.upload));
                                    toggleButtonPanel(false);
                                    if (uploadGridList.isEmpty()) {
                                        noPhotos.setVisibility(View.VISIBLE);
                                    }
                                }
                            }
                        }).
                        setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });

                AlertDialog alert = d.create();
                alert.setTitle(R.string.AlertTitle);
                alert.show();
                Button nbutton = alert.getButton(DialogInterface.BUTTON_NEGATIVE);
                nbutton.setTextColor(Color.BLACK);
                Button pbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE);
                pbutton.setTextColor(Color.BLACK);
            }
        });

        listAccounts = (ListView) getView().findViewById(R.id.listUploadAccounts);
        listPhotoUpload = (GridView) getView().findViewById(R.id.photolistview);
        if (!uploadGridList.isEmpty()) {
            listPhotoUpload.setAdapter(new ImageAdapter(ctx));
        }

		/*
         * bluetooth share
		 */
        btnBluetoothShare = (ImageView) getView().findViewById(R.id.upload_sendDirectly);
        btnBluetoothShare.setColorFilter(color);
        btnBluetoothShare.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!uploadGridList.isEmpty()) {
                    Intent intent = new Intent();
                    intent.setClass(getContext(), BluetoothShareMultipleFile.class);
                    intent.putExtra("imagelist", TextUtils.join("#", uploadGridList));
                    intent.putExtra("activityName", "Upload");
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);

                } else {
                    Commons.AlertLog(ctx, "Do not have photo to share", getString(R.string.accept)).show();
                }

            }
        });

        btnUpload = (ImageView) getView().findViewById(R.id.btnUploadPhoto);
        btnUpload.setColorFilter(color);
        if (savedInstanceState != null) {
        }

        btnUpload.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!upload_photo_process) {
                    if (PhimpMe.checked_accounts.size() > 0) {
                        Log.d("Hon", String.valueOf(PhimpMe.checked_accounts.size()));
                        if (checkListAccount()) {
                            if (!uploadGridList.isEmpty()) {
                                Log.d("Upload", "start");
                                Bundle data = new Bundle();
                                data.putStringArray("id", id);
                                data.putStringArray("service", service);
                                data.putStringArray("name", name);
                                data.putString("imagelist", TextUtils.join("#", uploadGridList));
                                Intent uitent = new Intent(ctx, UploadProgress.class);
                                uitent.putExtras(data);
                                Log.d("UploadProgress", "start : " + name);
                                startActivity(uitent);
                            } else {
                                Commons.AlertLog(ctx, getString(R.string.error_upload_no_photo), getString(R.string.accept)).show();
                            }
                        } else {
                            Commons.AlertLog(ctx, getString(R.string.upload_infor_choose_account), getString(R.string.accept)).show();
                        }
                    } else {
                        Commons.AlertLog(ctx, getString(R.string.error_upload_no_accounts), getString(R.string.accept)).show();

                    }
                } else {
                    Commons.AlertLog(ctx, getString(R.string.error_upload_process_still_running), getString(R.string.accept)).show();
                }
            }
        });

        btnAdd = (ImageView) getView().findViewById(R.id.btnUploadAccountAdd);
        btnAdd.setColorFilter(color);
        btnAdd.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new AddAccountDialogFragment();
                newFragment.show(getFragmentManager(), "dialog");
            }
        });

        btnPhotoAdd = (ImageView) getView().findViewById(R.id.btnUploadPhotoAdd);
        btnPhotoAdd.setColorFilter(color);
        btnPhotoAdd.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ctx, ImagePickerActivity.class);
                Params params = new Params();
                params.setPickerLimit(20);
                params.setActionButtonColor(fetchAccentColor());
                params.setToolbarColor(fetchAccentColor());
                params.setColumnCount(3);
                params.setButtonTextColor(fetchAccentColor());
                intent.putExtra(vn.mbm.phimp.me.utils.Constants.KEY_PARAMS, params);
                startActivityForResult(intent, TYPE_MULTI_PICKER);
            }
        });
        /*btnPhotoAdd.setOnTouchListener(new OnTouchListener()
        {
			@SuppressWarnings("deprecation")
			@Override
			public boolean onTouch(View v, MotionEvent event)
			{
				showDialog(DIALOG_ADD_PHOTO);
				return false;
			}
		});*/
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
        if ((PhimpMe.add_account_upload) || (name == null)) {
            reloadAccountsList();
        }

        iconContextMenu = new IconContextMenu(getActivity(), CONTEXT_MENU_ID);
        //iconContextMenu.addItem(res, DrupalServices.title, DrupalServices.icon, SERVICES_DRUPAL_ACTION);
        iconContextMenu.addItem(res, "Wordpress", R.drawable.icon_wordpress, SERVICES_WORDPRESS_ACTION);
        //iconContextMenu.addItem(res, "Wordpress.com", R.drawable.wordpressdotcom_icon, SERVICES_WORDPRESSDOTCOM_ACTION);
//        iconContextMenu.addItem(res, "Joomla", R.drawable.joomla, SERVICES_JOOMLA_ACTION);
        iconContextMenu.addItem(res, FacebookServices.title, FacebookServices.icon, SERVICES_FACEBOOK_ACTION);
//        iconContextMenu.addItem(res, FlickrServices.title, FlickrServices.icon, SERVICES_FLICKR_ACTION);
//        iconContextMenu.addItem(res, PicasaServices.title, PicasaServices.icon, SERVICES_PICASA_ACTION);
//        iconContextMenu.addItem(res, TumblrServices.title, TumblrServices.icon, SERVICES_TUMBLR_ACTION);
//        iconContextMenu.addItem(res, TwitterServices.title, TwitterServices.icon, SERVICES_TWITTER_ACTION);
//        iconContextMenu.addItem(res, DeviantArtService.title, DeviantArtService.icon, SERVICES_DEVIANTART_ACTION);
//        iconContextMenu.addItem(res, ImageshackServices.title, ImageshackServices.icon, SERVICES_IMAGESHACK_ACTION);

        //iconContextMenu.addItem(res, QQServices.title, QQServices.icon, SERVICES_QQ_ACTION);
        //iconContextMenu.addItem(res, VKServices.title, VKServices.icon, SERVICES_VK_ACTION);
        //iconContextMenu.addItem(res, S500pxService.title, S500pxService.icon, SERVICES_500PX_ACTION);
        //iconContextMenu.addItem(res, ImgurServices.title, ImgurServices.icon, SERVICES_IMGUR_ACTION);

        //iconContextMenu.addItem(res, KaixinServices.title, KaixinServices.icon, SERVICES_KAIXIN_ACTION);
        //iconContextMenu.addItem(res, SohuServices.title, SohuServices.icon, SERVICES_SOHU_ACTION);
        iconContextMenu.setOnClickListener(new IconContextMenu.IconContextMenuOnClickListener() {
            @SuppressWarnings("deprecation")
            @Override
            public void onClick(int menuId) {
                switch (menuId) {
                    case SERVICES_FACEBOOK_ACTION:
                        Intent fbauth = new Intent(ctx, FacebookActivity.class);
                        ctx.startActivity(fbauth);

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
                        getActivity().showDialog(DIALOG_ADD_ACCOUNT_DRUPAL);
                        break;
                    case SERVICES_DEVIANTART_ACTION:
                        String deviantart_oauth_url = DeviantArtService.getAuthenticateCode();
                        Intent deviantart = new Intent(ctx, Webkit.class);
                        deviantart.putExtra("URL", deviantart_oauth_url);
                        ctx.startActivity(deviantart);
                        PhimpMe.add_account_upload = true;
                        PhimpMe.add_account_setting = true;
                        break;
                    case SERVICES_IMAGESHACK_ACTION:
                        getActivity().showDialog(DIALOG_ADD_ACCOUNT_IMAGESHACK);
                        break;
                    case SERVICES_VK_ACTION:
                        Intent vk_authApp = new Intent(ctx, Webkit.class);
                        vk_authApp.putExtra("URL", VKServices.getAuthorzingUrl());
                        ctx.startActivity(vk_authApp);
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

                    case SERVICES_KAIXIN_ACTION:
                        String kaixin_oauth_url = KaixinServices.getAuthenticateCode();
                        Intent kaixin = new Intent(ctx, Webkit.class);
                        kaixin.putExtra("URL", kaixin_oauth_url);
                        ctx.startActivity(kaixin);
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
                    case SERVICES_WORDPRESSDOTCOM_ACTION:
                        Intent wordpress = new Intent(ctx, NewAccount.class);
                        ctx.startActivity(wordpress);
                        PhimpMe.add_account_upload = true;
                        PhimpMe.add_account_setting = true;
                        break;
                    case SERVICES_WORDPRESS_ACTION:
                        getActivity().showDialog(DIALOG_ADD_ACCOUNT_WORDPRESS);
                        break;
                    case SERVICES_JOOMLA_ACTION:
                        getActivity().showDialog(DIALOG_ADD_ACCOUNT_JOOMLA);
                        break;
                }

            }
        });
    }

    private int fetchAccentColor() {
        TypedValue typedValue = new TypedValue();
        TypedArray a = getActivity().obtainStyledAttributes(typedValue.data, new int[]{R.attr.colorAccent});
        int color = a.getColor(0, 0);
        a.recycle();
        return color;
    }

    class ImageAdapter extends BaseAdapter {

        private ArrayList<String> uploadImageList = new ArrayList<>();
        private LayoutInflater mInflater;

        public ImageAdapter(Context c) {

            mInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            uploadImageList = uploadGridList;
            if (!uploadGridList.isEmpty()) {
                noPhotos.setVisibility(View.GONE);
            }
        }

        public int getCount() {
            return uploadImageList.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        public long getItemId(int position) {
            return 0;
        }

        public View getView(final int position, View convertView, final ViewGroup parent) {
            // Holder for the view
            final GridItem holder;
            // Initiate holder
            if (convertView == null) {
                holder = new GridItem();
                convertView = mInflater.inflate(R.layout.gridviewitem, null);
                holder.imageview = (ImageView) convertView.findViewById(R.id.ImageGrid);
                holder.imageview.setScaleType(ImageView.ScaleType.CENTER_CROP);
                holder.checkView = (ImageView) convertView.findViewById(R.id.ImCheck);
                holder.title = (TextView) convertView.findViewById(R.id.ImTitle);
                holder.tags = (TextView) convertView.findViewById(R.id.ImTags);
                holder.lati = (TextView) convertView.findViewById(R.id.ImLati);
                holder.longi = (TextView) convertView.findViewById(R.id.ImLongi);
                convertView.setTag(holder);
            } else {
                holder = (GridItem) convertView.getTag();
            }
            final String tmp[] = String.valueOf(uploadImageList.get(position)).split(";");
            Uri imageUri = Uri.parse(tmp[0]);
            ContentResolver cr = getActivity().getContentResolver();
            String[] proj = {MediaStore.Images.Media._ID};
            Cursor cursor = getActivity().getContentResolver().query(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    proj,
                    MediaStore.Images.Media.DATA + " = '" + imageUri + "'",
                    null,
                    MediaStore.Images.Media._ID
            );
            if (cursor != null) {
                if (cursor.getCount() == 0) {
                    // display download photo in list upload photo
                    String thumb_path = tmp[0].replace(".rss_items", ".rss_thumbs");
                    Log.e("Upload", "thumb_path : " + thumb_path);
                    holder.imageview.setImageBitmap(BitmapFactory.decodeFile(thumb_path));
                } else {
                    cursor.moveToFirst();
                    holder.imageview.setScaleType(ImageView.ScaleType.FIT_XY);
                    try {
                        holder.imageview.setImageBitmap(
                                MediaStore.Images.Thumbnails.getThumbnail(
                                        cr,
                                        Integer.valueOf(cursor.getString(0)),
                                        MediaStore.Images.Thumbnails.MICRO_KIND, null));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (tmp.length < 2) {
                    File f = new File(String.valueOf(uploadImageList.get(position)).split(";")[0]);
                    holder.title.setText(f.getName());
                    ExifInterface exif_data = null;
                    geoDegrees _g = null;
                    String la = "";
                    String lo = "";
                    try {
                        exif_data = new ExifInterface(f.getAbsolutePath());
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
                    longtitude = lo;
                    latitude = la;
                    title = f.getName();
                    String latTxt = "Latitude: " + la;
                    holder.lati.setText(latTxt);
                    String lonTxt = "Longitude: " + lo;
                    holder.longi.setText(lonTxt);
                    String tagTxt = "Tags: ";
                    holder.tags.setText(tagTxt);
                } else {
                    try {
                        JSONObject js = new JSONObject(tmp[1]);
                        if (js.getString("name").equals("")) {
                            File f = new File(String.valueOf(uploadImageList.get(position)).split(";")[0]);
                            holder.title.setText(f.getName());
                        } else {
                            holder.title.setText(js.getString("name"));
                        }
                        String latTxt = "Latitude: " + js.getString("lati");
                        holder.lati.setText(latTxt);
                        String lonTxt = "Longitude: " + js.getString("logi");
                        holder.longi.setText(lonTxt);
                        String tagTxt = "Tags: " + js.getString("tags");
                        holder.tags.setText(tagTxt);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                cursor.close();
            }

            convertView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    // If holder is not selected and no other holders are selected, display Image
                    // If the holder is selected, deselect it
                    // If any other holder is selected, select this holder as well
                    if (holder.isSelected) {
                        holder.isSelected = false;
                        holder.imageview.setAlpha(1.0f);
                        holder.checkView.setVisibility(View.GONE);
                        removableList.remove(holder.imagePath);
                        int delCount = removableList.size();
                        String newTitle;
                        if (delCount > 0) {
                            newTitle = delCount + (delCount == 1 ? " image" : " images") + " selected";
                            toggleButtonPanel(true);
                        } else {
                            newTitle =  getResources().getString(R.string.upload);
                            toggleButtonPanel(false);
                        }
                        panelLable.setText(newTitle);
                    } else {
                        if (removableList.isEmpty()) {
                            Intent viewImageIntent = new Intent();
                            viewImageIntent.setClass(ctx, CropImage.class);
                            viewImageIntent.putExtra("image-path", holder.imagePath);
                            viewImageIntent.putExtra("longtitude", longtitude);
                            viewImageIntent.putExtra("latitude", latitude);
                            viewImageIntent.putExtra("title", title);
                            viewImageIntent.putExtra("position", position);
                            viewImageIntent.putExtra("aspectX", 0);
                            viewImageIntent.putExtra("aspectY", 0);
                            viewImageIntent.putExtra("scale", true);
                            viewImageIntent.putExtra("activityName", "Upload");
                            startActivityForResult(viewImageIntent, CROP_IMAGE);
                        } else {
                            holder.isSelected = true;
                            holder.imageview.setAlpha(0.5f);
                            holder.checkView.setVisibility(View.VISIBLE);
                            removableList.add(holder.imagePath);
                            int delCount = removableList.size();
                            String newTitle;
                            if (delCount > 0) {
                                newTitle = delCount + (delCount == 1 ? " image" : " images") + " selected";
                                toggleButtonPanel(true);
                            } else {
                                newTitle =  getResources().getString(R.string.upload);
                                toggleButtonPanel(false);
                            }
                            panelLable.setText(newTitle);
                        }
                    }
                }
            });

            convertView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (holder.isSelected) {
                        holder.imageview.setAlpha(1.0f);
                        holder.checkView.setVisibility(View.GONE);
                        holder.isSelected = false;
                        removableList.remove(holder.imagePath);
                    } else {
                        holder.imageview.setAlpha(0.5f);
                        holder.checkView.setVisibility(View.VISIBLE);
                        holder.isSelected = true;
                        removableList.add(holder.imagePath);
                    }
                    int delCount = removableList.size();
                    String newTitle;
                    if (delCount > 0) {
                        newTitle = delCount + (delCount == 1 ? " image" : " images") + " selected";
                        toggleButtonPanel(true);
                    } else {
                        newTitle =  getResources().getString(R.string.upload);
                        toggleButtonPanel(false);
                    }
                    panelLable.setText(newTitle);
                    return true;
                }
            });

            holder.isSelected = removableList.contains(uploadImageList.get(position));
            holder.imagePath = uploadImageList.get(position);
            holder.imageview.setAlpha(holder.isSelected ? 0.5f : 1.0f);
            holder.checkView.setVisibility(holder.isSelected? View.VISIBLE : View.GONE);

            return convertView;
        }
    }

    /**
     * Upload item
     */
    class GridItem {
        ImageView imageview;
        ImageView checkView;
        TextView title;
        TextView lati;
        TextView longi;
        TextView tags;
        boolean isSelected;
        String imagePath;
    }

    private void toggleButtonPanel(boolean visibility) {
        uploadPanel.setVisibility(!visibility ? View.VISIBLE : View.GONE);
        clearPanel.setVisibility(visibility ? View.VISIBLE : View.GONE);
    }

    private class AccountsAdapter extends ArrayAdapter<String> {
        private final Activity context;

        private String[] id;

        private String[] name;

        private String[] service;

        public AccountsAdapter(Activity context, String[] id, String[] name, String[] service) {
            super(context, R.layout.uploads_account_item, id);
            this.context = context;
            this.id = id;
            this.name = name;
            this.service = service;
        }

        class ViewHolder {
            public ImageView imgIcon;

            public TextView txtName;

            public CheckBox chkCheck;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;

            View view = convertView;

            if (view == null) {
                LayoutInflater inflater = context.getLayoutInflater();
                view = inflater.inflate(R.layout.uploads_account_item, null, true);
                holder = new ViewHolder();

                holder.imgIcon = (ImageView) view.findViewById(R.id.imgServiceIcon);
                holder.txtName = (TextView) view.findViewById(R.id.txtAccountName);
                holder.chkCheck = (CheckBox) view.findViewById(R.id.chkboxCheck);

                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }

            if (service[position].equals("tumblr")) {
                holder.imgIcon.setImageResource(TumblrServices.icon);
            } else if (service[position].equals("facebook")) {
                holder.imgIcon.setImageResource(FacebookServices.icon);
            } else if (service[position].equals("flickr")) {
                holder.imgIcon.setImageResource(FlickrServices.icon);
            } else if (service[position].equals("picasa")) {
                holder.imgIcon.setImageResource(PicasaServices.icon);
            } else if (service[position].equals("twitter")) {
                holder.imgIcon.setImageResource(TwitterServices.icon);
            } else if (service[position].equals("drupal")) {
                holder.imgIcon.setImageResource(DrupalServices.icon);
            } else if (service[position].equals("deviantart")) {
                holder.imgIcon.setImageResource(DeviantArtService.icon);
            } else if (service[position].equals("imageshack")) {
                holder.imgIcon.setImageResource(ImageshackServices.icon);
            } else if (service[position].equals("qq")) {
                holder.imgIcon.setImageResource(QQServices.icon);
            } else if (service[position].equals("vkontakte")) {
                holder.imgIcon.setImageResource(VKServices.icon);
            } else if (service[position].equals("imgur")) {
                holder.imgIcon.setImageResource(ImgurServices.icon);
            } else if (service[position].equals("kaixin")) {
                holder.imgIcon.setImageResource(KaixinServices.icon);

            } else if (service[position].equals("500px")) {
                holder.imgIcon.setImageResource(S500pxService.icon);
            } else if (service[position].equals("sohu")) {
                holder.imgIcon.setImageResource(SohuServices.icon);
            } else if (service[position].equals("wordpressdotcom")) {
                holder.imgIcon.setImageResource(R.drawable.wordpressdotcom_icon);
            } else if (service[position].equals("wordpress")) {
                holder.imgIcon.setImageResource(R.drawable.icon_wordpress);
            } else if (service[position].equals("joomla")) {
                holder.imgIcon.setImageResource(R.drawable.joomla);
            }
            boolean c = false;

            try {
                c = PhimpMe.checked_accounts.get(id[position]);
            } catch (Exception e) {
                PhimpMe.checked_accounts.remove(id[position]);
                PhimpMe.checked_accounts.put(id[position], false);
            }

            holder.chkCheck.setChecked(c);

            String acc_name = name[position];

            holder.txtName.setText(acc_name);
            holder.chkCheck.setOnClickListener(new checkboxClickListener(position));
            return view;
        }
    }

    private class checkboxClickListener implements OnClickListener {
        private int pos;

        public checkboxClickListener(int position) {
            this.pos = position;
        }

        @Override
        public void onClick(View v) {
            String _id = id[this.pos];

            if (((CheckBox) v).isChecked()) {
                PhimpMe.checked_accounts.remove(_id);
                PhimpMe.checked_accounts.put(_id, true);
            } else {
                PhimpMe.checked_accounts.remove(_id);
                PhimpMe.checked_accounts.put(_id, false);
            }
        }
    }

    @Override
    public void onResume() {
        Log.d("thong", "Upload - onResume()");
        super.onResume();
        PhimpMe.showTabs();


        if (PhimpMe.add_account_upload) {
            reloadAccountsList();
            PhimpMe.add_account_upload = false;
        }
        if (!uploadGridList.isEmpty()) {
            listPhotoUpload.setAdapter(new ImageAdapter(ctx));
        }
        if (PhimpMe.IdList.size() == 5) {
            PhimpMe.IdList.clear();
            PhimpMe.IdList.add(0);
        }
        PhimpMe.IdList.add(4);
    }

    private void reloadAccountsList() {
        ArrayList<AccountItem> accounts = AccountItem.getAllAccounts(ctx);
        id = new String[accounts.size()];
        name = new String[accounts.size()];
        service = new String[accounts.size()];
        for (int i = 0; i < accounts.size(); i++) {
            AccountItem item = accounts.get(i);
            id[i] = item.getID();
            name[i] = item.getName();
            service[i] = item.getService();
        }
        accounts = null;

        listAccounts.setAdapter(new AccountsAdapter(getActivity(), id, name, service));
        if (listAccounts.getAdapter().isEmpty()) {
            listAccounts.setVisibility(View.GONE);
            getView().findViewById(R.id.noaccounts).setVisibility(View.VISIBLE);
        }

    }

    /**
     * create context menu
     */
    @SuppressWarnings("deprecation")
    //@Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case CONTEXT_MENU_ID:
                return iconContextMenu.createMenu(getString(R.string.services));
            case DIALOG_ADD_PHOTO:
                final CharSequence[] items = {
                        //this.getString(R.string.camera),
                        this.getString(R.string.gallery),
                        this.getString(R.string.cancel)
                };

                return new AlertDialog.Builder(getContext()).setIcon(R.drawable.icon).setTitle("Add photos")
                        .setItems(items, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
								/*case 0:
									startCameraActivity();
									break;*/
                                    case 0:
                                        Intent intent = new Intent(ctx, PhotoSelect.class);
                                        //progLoading = ProgressDialog.show(ctx, getString(R.string.loading), getString(R.string.photos_loading), true, false);
                                        startActivityForResult(intent, SELECT_IMAGE_FROM_GALLERY);
                                        break;
                                    case 1:
                                        dialog.cancel();
                                        break;
                                }
                            }
                        }).create();
            case DIALOG_ADD_ACCOUNT_DRUPAL:
                LayoutInflater inflater = (LayoutInflater) ctx.getSystemService(getActivity().LAYOUT_INFLATER_SERVICE);
                final View layout = inflater.inflate(R.layout.dialog_add_account_drupal, (ViewGroup) getView().findViewById(R.id.lytDialogAddAccountDrupal));
                Button btnDrupal = (Button) layout.findViewById(R.id.btnDrupalIntroduction);
                btnDrupal.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        Intent i = new Intent(ctx, DrupalWebviewActivity.class);
                        startActivity(i);
                    }
                });
                return new AlertDialog.Builder(getContext())
                        .setTitle(DrupalServices.title)
                        .setMessage("")
                        .setView(layout)
                        .setPositiveButton(getString(R.string.accept), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                try {
                                    String username = ((EditText) layout.findViewById(R.id.txtDialogAddAccountDrupalUsername)).getText().toString();
                                    String password = ((EditText) layout.findViewById(R.id.txtDialogAddAccountDrupalPassword)).getText().toString();
                                    String siteurl = ((EditText) layout.findViewById(R.id.txtDialogAddAccountDrupalSiteurl)).getText().toString();//"http://phimp.me/api/";

                                    String result = DrupalServices.login(username, password, siteurl);

                                    JSONObject json = new JSONObject(result);
                                    JSONObject user = json.getJSONObject("user");
                                    String user_id = user.getString("uid");
                                    String email = user.getString("mail");

                                    long account_id = AccountItem.insertAccount(ctx, null, username, "drupal", "1");

                                    if (account_id > 0) {
                                        if (DrupalItem.insertAccount(ctx, String.valueOf(account_id), user_id, username, password, siteurl, email)) {
                                            Toast.makeText(ctx, "Insert account '" + username + "' (Drupal) SUCCESS!", Toast.LENGTH_LONG).show();
                                        } else {
                                        }
                                    }

                                    PhimpMe.add_account_setting = true;

                                    reloadAccountsList();


                                } catch (Exception e) {
                                    Toast.makeText(getContext(), "Error: " + e.toString(), Toast.LENGTH_LONG).show();

                                    e.printStackTrace();
                                }
                            }
                        })
                        .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .create();
            //Imageshack Dialog
            case DIALOG_ADD_ACCOUNT_IMAGESHACK:
                LayoutInflater inflater1 = (LayoutInflater) ctx.getSystemService(getActivity().LAYOUT_INFLATER_SERVICE);
                final View layout1 = inflater1.inflate(R.layout.dialog_add_account_imageshack, (ViewGroup) getView().findViewById(R.id.lytDialogAddAccountImageshack));

                return new AlertDialog.Builder(getContext())
                        .setTitle(ImageshackServices.title)
                        .setMessage("")
                        .setView(layout1)
                        .setPositiveButton(getString(R.string.accept), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                try {
                                    String username = ((EditText) layout1.findViewById(R.id.txtDialogAddAccountImageshackUsername)).getText().toString();
                                    String password = ((EditText) layout1.findViewById(R.id.txtDialogAddAccountImageshackPassword)).getText().toString();

                                    String result = ImageshackServices.login(username, password);


                                    JSONObject json = new JSONObject(result);
                                    String status = json.getString("status");
                                    String registratorcode = "";
                                    if (status.equals("true")) {
                                        registratorcode = json.getString("myimages");
                                        long account_id = AccountItem.insertAccount(ctx, null, username, "imageshack", "1");

                                        if (account_id > 0) {
                                            if (ImageshackItem.insertAccount(ctx, String.valueOf(account_id), registratorcode, username)) {
                                                Toast.makeText(ctx, "Insert account '" + username + "' (Imageshack) SUCCESS!", Toast.LENGTH_LONG).show();
                                            } else {
                                                Toast.makeText(ctx, "Insert account '" + username + "' (Imageshack) FAIL!", Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    } else {
                                        Toast.makeText(ctx, "Login Imageshack Fail !", Toast.LENGTH_LONG).show();
                                    }

                                    PhimpMe.add_account_setting = true;

                                    reloadAccountsList();
                                } catch (Exception e) {
                                    Toast.makeText(getActivity(), "Error: " + e.toString(), Toast.LENGTH_LONG).show();

                                    e.printStackTrace();
                                }
                            }
                        })
                        .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .create();
            //wordpress
            case DIALOG_ADD_ACCOUNT_WORDPRESS:

                LayoutInflater inflater2 = (LayoutInflater) ctx.getSystemService(getActivity().LAYOUT_INFLATER_SERVICE);
                final View layout2 = inflater2.inflate(R.layout.dialog_add_account_wordpress, (ViewGroup) getView().findViewById(R.id.lytDialogAddAccountWordpress));
                Button btnWordpress = (Button) layout2.findViewById(R.id.btnWordpressIntroduction);
                btnWordpress.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        Intent i = new Intent(ctx, WordpressWebviewActivity.class);
                        startActivity(i);
                    }
                });
                return new AlertDialog.Builder(getContext())
                        .setTitle("Wordpress")
                        .setMessage("")
                        .setView(layout2)
                        .setPositiveButton(getString(R.string.accept), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                try {

                                    String username = ((EditText) layout2.findViewById(R.id.txtDialogAddAccountWordpressUsername)).getText().toString();
                                    String password = ((EditText) layout2.findViewById(R.id.txtDialogAddAccountWordpressPassword)).getText().toString();
                                    String siteurl = ((EditText) layout2.findViewById(R.id.txtDialogAddAccountWordpressSiteurl)).getText().toString();
                                    Wordpress w = new Wordpress();
                                    w.login(ctx, username, password, siteurl);
                                    PhimpMe.add_account_upload = true;
                                    PhimpMe.add_account_setting = true;
                                    reloadAccountsList();
                                } catch (Exception e) {
                                    Toast.makeText(getContext(), "Error: " + e.toString(), Toast.LENGTH_LONG).show();

                                    e.printStackTrace();
                                }
                            }
                        })
                        .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .create();
            case DIALOG_ADD_ACCOUNT_JOOMLA:
                LayoutInflater inflater3 = (LayoutInflater) ctx.getSystemService(getActivity().LAYOUT_INFLATER_SERVICE);
                final View layout3 = inflater3.inflate(R.layout.dialog_add_account_joomla, (ViewGroup) getView().findViewById(R.id.lytDialogAddAccountWordpress));
                Button btnJoomla = (Button) layout3.findViewById(R.id.btnJoomlaIntroduction);
                btnJoomla.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        Intent i = new Intent(ctx, JoomlaWebviewActivity.class);
                        startActivity(i);
                    }
                });
                return new AlertDialog.Builder(getContext())
                        .setTitle("Joomla")
                        .setMessage("")
                        .setView(layout3)
                        .setPositiveButton(getString(R.string.accept), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                try {

                                    String username = ((EditText) layout3.findViewById(R.id.txtAddJoomlaUsername)).getText().toString();
                                    String password = ((EditText) layout3.findViewById(R.id.txtAddJoomlaPassword)).getText().toString();
                                    String siteurl = ((EditText) layout3.findViewById(R.id.txtAddJoomlaLink)).getText().toString();
                                    JoooidRpc rpcClient = JoooidRpc.getInstance(siteurl, Constants.TASK_WS_URI_J17, username, password, User.JOOMLA_16);
                                    User clientUser = rpcClient.userInfo(username, password);
                                    if (clientUser.getId() != null) {
                                        Log.e("User url", clientUser.getJoomlaUrl());
                                        Log.e("User uri", clientUser.getJoomlaUri());
                                        long account_id = AccountItem.insertAccount(ctx, null, username, "joomla", "1");
                                        String cat_id = rpcClient.findCategory(username, password, "phimpme");
                                        if (cat_id.equals("0"))
                                            cat_id = rpcClient.newCategory(username, password, "phimpme", "phimpme", "phimpme category", 1, 1, 1);
                                        else
                                            rpcClient.editCategory(username, password, "phimpme", "phimpme", "phimpme category", 1, 1, 1, Integer.parseInt(cat_id));
                                        JoomlaItem.insertJoomlaAccount(ctx, String.valueOf(account_id), clientUser.getJoomlaUrl(), username, password, "joomla", cat_id);
                                        Toast.makeText(ctx, "Insert account '" + username + "' (Joomla) SUCCESS!", Toast.LENGTH_LONG).show();
                                    } else
                                        Toast.makeText(ctx, "Login Joomla Fail ! Please check again !", Toast.LENGTH_LONG).show();
                                    PhimpMe.add_account_upload = true;
                                    PhimpMe.add_account_setting = true;
                                    reloadAccountsList();
                                    //Wordpress w=new Wordpress();
                                    //w.login(ctx,username,password,siteurl);
                                    //PhimpMe.add_account_upload=true;
                                    //PhimpMe.add_account_setting = true;
                                    //reloadAccountsList();
                                } catch (Exception e) {
                                    Toast.makeText(getContext(), "Error: " + e.toString(), Toast.LENGTH_LONG).show();

                                    e.printStackTrace();
                                }
                            }
                        })
                        .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .create();
            default:
                return null;//super.onCreateDialog(id);
        }
    }

    /*
     * Start Camera Activity
     */
    public void startCameraActivity() {
        try {
            Date d = new Date();
            String filename = "phimp.me_" + d.getTime() + ".jpg";

            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.TITLE, filename);
            values.put(MediaStore.Images.Media.DESCRIPTION, "Image capture by Camera using Phimp.Me");

            camera_img_uri = getActivity().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, camera_img_uri);
            startActivityForResult(cameraIntent, TAKE_PICTURE);
        } catch (Exception e) {

        }
    }

    // TODO: this might not be safe; was protected.
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {

            case TAKE_PICTURE: {
                if (resultCode == Activity.RESULT_OK) {
                    String[] projection = {MediaStore.Images.Media.DATA};
                    Cursor cursor = getActivity().getContentResolver().query(
                            camera_img_uri,
                            projection,
                            null,
                            null,
                            null
                    );
                    if (cursor != null) {
                        int column_index_data = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                        cursor.moveToFirst();
                        uploadGridList.add(cursor.getString(column_index_data));
                        listPhotoUpload.setAdapter(new ImageAdapter(getContext()));
                        cursor.close();
                    }
                }
                break;
            }
            case TYPE_MULTI_PICKER: {
                if (resultCode == Activity.RESULT_OK) {
                    ArrayList<Image> imagesList = data.getParcelableArrayListExtra(vn.mbm.phimp.me.utils.Constants.KEY_BUNDLE_LIST);
                    if (imagesList.size() > 0) {
                        for (int i = 0; i < imagesList.size(); i++) {
                            uploadGridList.add(imagesList.get(i).imagePath);
                        }
                        listPhotoUpload.setAdapter(new ImageAdapter(getContext()));
                    }
                }
                break;
            }
            case CROP_IMAGE: {
                if (resultCode == Activity.RESULT_OK) {
                    String imagepath = data.getStringExtra("Impath");
                    String saveUri = data.getStringExtra("saveUri");
                    String lati = data.getStringExtra("lati");
                    String logi = data.getStringExtra("logi");
                    String name = data.getStringExtra("name");
                    String tag = data.getStringExtra("tags");
                    String json = "{\"name\":\"" + name + "\"," + "\"tags\":\"" + tag + "\"," + "\"lati\":\"" + lati + "\"," + "\"logi\":\"" + logi + "\"}";
                    uploadGridList.remove(imagepath);
                    uploadGridList.add(saveUri + ";" + json);
                    listPhotoUpload.setAdapter(new ImageAdapter(getContext()));
                }
                break;
            }
            case GET_POSITION_ON_MAP: {
                if (resultCode == Activity.RESULT_OK) {
                    txtLatitude.setText(PhimpMe.UploadLatitude + "");
                    txtLongtitude.setText(PhimpMe.UploadLongitude + "");
                }
                break;
            }
        }
    }


    /*public class MyLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location loc) {
            if (loc != null) {
                PhimpMe.curLatitude = loc.getLatitude();
                PhimpMe.curLongtitude = loc.getLongitude();
                PhimpMe.UploadLatitude = loc.getLatitude();
                PhimpMe.UploadLongitude = loc.getLongitude();
                int _lat = (int) (PhimpMe.curLatitude * 1000000);
                int _long = (int) (PhimpMe.curLongtitude * 1000000);
                PhimpMe.currentGeoPoint = new GeoPoint(_lat, _long);
                gpsloading.dismiss();
                txtLatitude.setText(PhimpMe.curLatitude + "");
                txtLongtitude.setText(PhimpMe.curLongtitude + "");
            } else {
            }
        }*/


       /* @Override
        public void onProviderDisabled(String provider) {
        }


        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    }*/

    /*@Override
    public boolean onKeyDown(int keycode, KeyEvent event)
    {
        if (keycode == KeyEvent.KEYCODE_BACK){
            PhimpMe.IdList.remove(PhimpMe.IdList.size()-1);
            PhimpMe.mTabHost.setCurrentTab(PhimpMe.IdList.get(PhimpMe.IdList.size()-1));
            PhimpMe.showTabs();
        }
        return super.onKeyDown(keycode, event);

    }*/

//    @Override
//    public void onBackPressed() {
//        PhimpMe.IdList.remove(PhimpMe.IdList.size() - 1);
////        PhimpMe.mTabHost.setCurrentTab(0);
//    }

}
