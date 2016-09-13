package vn.mbm.phimp.me;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.security.KeyStore;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import oauth.signpost.OAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import oauth.signpost.http.HttpParameters;
import oauth.signpost.signature.HmacSha1MessageSigner;
import oauth.signpost.signature.OAuthMessageSigner;

import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.wordpress.android.AddCatagory;
import org.wordpress.android.models.MediaFile;
import org.xml.sax.InputSource;
import org.xmlrpc.android.XMLRPCClient;
import org.xmlrpc.android.XMLRPCException;

import vn.mbm.phimp.me.database.DeviantArtItem;
import vn.mbm.phimp.me.database.DrupalItem;
import vn.mbm.phimp.me.database.FacebookItem;
import vn.mbm.phimp.me.database.FlickrItem;
import vn.mbm.phimp.me.database.ImageshackItem;
import vn.mbm.phimp.me.database.ImageshackPhotoList;
import vn.mbm.phimp.me.database.ImgurItem;
import vn.mbm.phimp.me.database.JoomlaItem;
import vn.mbm.phimp.me.database.PicasaItem;
import vn.mbm.phimp.me.database.SohuItem;
import vn.mbm.phimp.me.database.TumblrItem;
import vn.mbm.phimp.me.database.TwitterItem;
import vn.mbm.phimp.me.database.VkItem;
import vn.mbm.phimp.me.database.WordpressItem;
import vn.mbm.phimp.me.services.DeviantArtService;
import vn.mbm.phimp.me.services.DrupalServices;
import vn.mbm.phimp.me.services.FacebookServices;
import vn.mbm.phimp.me.services.FlickrServices;
import vn.mbm.phimp.me.services.ImageshackServices;
import vn.mbm.phimp.me.services.ImgurServices;
import vn.mbm.phimp.me.services.KaixinServices;
import vn.mbm.phimp.me.services.PicasaServices;
import vn.mbm.phimp.me.services.S500pxService;
import vn.mbm.phimp.me.services.SohuServices;
import vn.mbm.phimp.me.services.TumblrServices;
import vn.mbm.phimp.me.services.TwitterServices;
import vn.mbm.phimp.me.services.VKServices;
import vn.mbm.phimp.me.utils.Commons;
import vn.mbm.phimp.me.utils.Commons.MySSLSocketFactory;
import vn.mbm.phimp.me.utils.CustomFilePartEntity;
import vn.mbm.phimp.me.utils.CustomMultiPartEntity;
import vn.mbm.phimp.me.utils.CustomMultiPartEntity.ProgressListener;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;
import com.joooid.android.model.User;
import com.joooid.android.xmlrpc.Constants;
import com.joooid.android.xmlrpc.JoooidRpc;

public class UploadProgress extends Activity
{
	Context ctx;

	LinearLayout lytUploadProgress;
	SharePhotoContent content;

	public ShareDialog shareDialog;
	public static String[] account_id;
	public static String[] account_name;
	static String[] account_service;
	static String MAINTAG = "MBM";
	static String DRUPALTAG = "MBM.Drupal";
	static String imagelist;
	static ArrayList<Integer> checked_ids = new ArrayList<Integer>();
	static ArrayList<ProgressBar> progressbar_array = new ArrayList<ProgressBar>();
	static Map<Integer, ViewHolder> viewholders = new TreeMap<Integer, ViewHolder>();
	static String[] path;
	static final String default_tag_separate = ",";
	private static int progressStatus=0,progressStatus1=0;
	private ConnectivityManager mSystemService;
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.upload_progress);
		shareDialog = new ShareDialog(this);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		lytUploadProgress = (LinearLayout) findViewById(R.id.linearUploadProgress);
		checked_ids.clear();
		lytUploadProgress.removeAllViews();

		ctx = this;
		mSystemService = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

		try
		{
			Bundle data = getIntent().getExtras();
			account_id = data.getStringArray("id");
			account_service = data.getStringArray("service");
			account_name = data.getStringArray("name");
			imagelist = data.getString("imagelist");
			Log.d("imagelist",imagelist);
			path = imagelist.split("#");
			Log.d(MAINTAG, "Checked accounts: " + PhimpMe.checked_accounts.size());

			for (int i = 0; i < PhimpMe.checked_accounts.size(); i++)
			{
				boolean _b = PhimpMe.checked_accounts.get(account_id[i]);

				if (_b)
				{
					checked_ids.add(i);
				}
			}

			Log.d(MAINTAG, "checked_ids: " + checked_ids.size());
			Log.d(MAINTAG, "account_name: " + account_name.toString());
			for (int i = 0; i < checked_ids.size();i++)
			{
				ViewHolder holder;

				View view = new View(ctx);

				final int position = checked_ids.get(i);

				LayoutInflater inflater = ((Activity) ctx).getLayoutInflater();
				view = inflater.inflate(R.layout.upload_progress_item, null, true);
				holder = new ViewHolder();

				holder.imgIcon = (ImageView) view.findViewById(R.id.imgUploadProgressItemServiceIcon);
				holder.txtName = (TextView) view.findViewById(R.id.tvUploadProgressItemName);
				holder.imgStatusOK = (ImageView) view.findViewById(R.id.imgUploadProgressItemStatusOK);
				holder.imgStatusFail = (ImageView) view.findViewById(R.id.imgUploadProgressItemStatusFail);
				holder.pgbar = (ProgressBar) view.findViewById(R.id.prgbarUploadProgressItemProgressBar);
				holder.txtProcess = (TextView) view.findViewById(R.id.tvUploadProgressItemProcess);

				viewholders.put(position, holder);

				if (account_service[position].equals("tumblr"))
				{
					holder.imgIcon.setImageResource(TumblrServices.icon);
				}
				else if (account_service[position].equals("facebook"))
				{
					holder.imgIcon.setImageResource(FacebookServices.icon);
				}
				else if (account_service[position].equals("flickr"))
				{
					holder.imgIcon.setImageResource(FlickrServices.icon);
				}
				else if (account_service[position].equals("picasa"))
				{
					holder.imgIcon.setImageResource(PicasaServices.icon);
				}
				else if (account_service[position].equals("twitter"))
				{
					holder.imgIcon.setImageResource(TwitterServices.icon);
				}
				else if (account_service[position].equals("drupal"))
				{
					holder.imgIcon.setImageResource(DrupalServices.icon);
				}
				else if (account_service[position].equals("deviantart"))
				{
					holder.imgIcon.setImageResource(DeviantArtService.icon);
				}
				else if (account_service[position].equals("imageshack"))
				{
					holder.imgIcon.setImageResource(ImageshackServices.icon);
				}
				else if (account_service[position].equals("vkontakte"))
				{
					holder.imgIcon.setImageResource(VKServices.icon);
				}

				else if (account_service[position].equals("imgur"))
				{
					holder.imgIcon.setImageResource(ImgurServices.icon);
				}

				else if (account_service[position].equals("kaixin"))
				{
					holder.imgIcon.setImageResource(KaixinServices.icon);
				}
				else if (account_service[position].equals("500px"))
				{
					holder.imgIcon.setImageResource(S500pxService.icon);

				}
				else if (account_service[position].equals("sohu"))
				{
					holder.imgIcon.setImageResource(SohuServices.icon);

				}
				else if (account_service[position].equals("wordpress"))
				{
					holder.imgIcon.setImageResource(R.drawable.icon_wordpress);

				}
				else if (account_service[position].equals("wordpressdotcom"))
				{
					holder.imgIcon.setImageResource(R.drawable.wordpressdotcom_icon);

				}
				else if (account_service[position].equals("joomla"))
				{
					holder.imgIcon.setImageResource(R.drawable.joomla);

				}
				String acc_name = account_name[position];

				holder.txtName.setText(acc_name);

				lytUploadProgress.addView(view);

				new UploadProgressAsyncTask().execute(position);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			setResult(RESULT_CANCELED);
			finish();
		}
	}

	@Override
	public void onBackPressed()
	{
		try
		{
			checked_ids.clear();
			lytUploadProgress.removeAllViews();
			finish();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	class ViewHolder
	{
		public ImageView imgIcon;
		public TextView txtName;
		public ImageView imgStatusOK;
		public ImageView imgStatusFail;
		public ProgressBar pgbar;
		public TextView txtProcess;
	}

	private class UploadProgressAsyncTask extends AsyncTask<Integer, Integer, Boolean>
	{
		private int pos;
		private ProgressBar pb;
		private ViewHolder vh;
		private ImageView ivOK;
		private ImageView ivFail;
		private TextView tvPC;
		long totalSize;
		ResponseHandler<String> res = new BasicResponseHandler();
		DrupalItem acc = null;
		String session_id = "";
		String session_name = "";
		String service_url = "";

		/* Login */
		protected String login(String username, String password)
		{
			try
			{
				String url = service_url + "user/login";
				HttpPost post = new HttpPost(url);
				HttpClient client = new DefaultHttpClient();
				/* Why does each subroutines only works with its own HttpClient and not share HttpClient
				 * with each other? */

				MultipartEntity multi = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);

				multi.addPart("username", new StringBody(username));
				multi.addPart("password", new StringBody(password));

				post.setEntity(multi);

				String httpResponse = client.execute(post, res);

				Log.d(DRUPALTAG, "Login step: ");
				Log.v(DRUPALTAG, httpResponse);

				Log.d(DRUPALTAG, "Create JSONObject");
				JSONObject json = new JSONObject(httpResponse);

				session_id = json.getString("sessid");
				session_name = json.getString("session_name");
				String userId = json.getJSONObject("user").getString("uid");
				Log.d(DRUPALTAG, "OK! Logged in");
				return userId;
			}
			catch (Exception e)
			{
				Log.e(DRUPALTAG, "login: " + e.toString());
				e.printStackTrace();
				return "";
			}
		}

		/* Retrieve the nodes owned by user */
		protected String[] getOwnNodes(String uid)
		{
			String[] list = null;
			String url = "";
			if (uid != "") {
				url = service_url + "own_nodes.json";
			}
			else {
				url = service_url + "own_nodes/" + uid + ".json";
			}
			try
			{
				Log.d(DRUPALTAG, "url: " + url);
				HttpGet get = new HttpGet(url);
				HttpClient client = new DefaultHttpClient();

				//Authorize
				get.setHeader("Cookie", session_name + "=" + session_id);
				Log.d(DRUPALTAG, "Cookie: " + session_name + "=" + session_id);

				String textresponse = client.execute(get, res);
				Log.d(DRUPALTAG, "getOwnNodes: textresponse: " + textresponse);
				JSONArray jsonResult = new JSONArray(textresponse);
				int n = jsonResult.length();
				if (n == 0) {
					return null;
				}
				Log.d(DRUPALTAG, "n: " + Integer.toString(n));
				list = new String[n];
				for (int i = 0; i < n; i++)
				{
					list[i] = jsonResult.getJSONObject(i).getString("nid");
				}
				Log.d(DRUPALTAG, "getOwnNodes: List length: " + Integer.toString(list.length));
				return list;
			}
			catch (Exception e)
			{
				Log.e(DRUPALTAG, "getOwnNodes: " + e.toString());
				e.printStackTrace();
				return list;
			}
		}

		/* Count the photos in the node */
		protected int countMedia(String nodeId) throws Exception
		{
			String service_url = acc.getSerivceURL();
			int total = 0;

			try
			{
				String url = service_url + "node/" + nodeId + ".json";
				HttpGet get = new HttpGet(url);
				HttpClient client = new DefaultHttpClient();

				//Authorize
				get.setHeader("Cookie", session_name + "=" + session_id);

				String textresponse = client.execute(get, res);
				JSONObject jsonResult = new JSONObject(textresponse);
				JSONArray medias = jsonResult.getJSONObject("media_gallery_media").getJSONArray("und");
				total = medias.length();
			}
			catch (Exception e)
			{
				Log.e(DRUPALTAG, "countMedia: Failed to count the photos.");
				e.printStackTrace();
				throw e;
			}
			return total;
		}

		/* Upload photo */
		@SuppressWarnings("resource")
		protected String uploadPhoto(String path)
		{
			String fileId = "";
			try
			{
				String url = service_url + "file";
				File f = new File(path.split(";")[0]);
				HttpPost post = new HttpPost(url);
				HttpClient client = new DefaultHttpClient();

				Log.d("Hon",path);
				//Authorize
				post.setHeader("Cookie", session_name + "=" + session_id);

				//Get imagedata
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				FileInputStream fis = new FileInputStream(f);
				byte[] buf = new byte[1024];
				try {
					for (int readNum; (readNum = fis.read(buf)) != -1;) {
						baos.write(buf, 0, readNum);
					}
				} catch (IOException ex) {

				}
				byte[] b = baos.toByteArray();
				baos.flush();
				baos.close();
				baos = null;
				String img_data_b64 = Base64.encodeToString(b, Base64.DEFAULT);
				CustomMultiPartEntity multi = new CustomMultiPartEntity(new ProgressListener()
				{
					@Override
					public void transferred(long num)
					{
						publishProgress((int) ((num / (float) totalSize) * 100));
					}
				});

				multi.addPart("file", new StringBody(img_data_b64));
				File jpeg=new File(path);
				String filename=jpeg.getName();
				Log.i("Danh-UploadProgress","Filename : "+filename);
				multi.addPart("filename", new StringBody(filename));
				totalSize = multi.getContentLength();
				Log.d("Size",String.valueOf(totalSize));
				post.setEntity(multi);
				String httpResponse = client.execute(post,res);
				Log.d("drupal", "Upload file step: ");
				Log.d("drupal", "httpResponse : "+httpResponse);
				img_data_b64 = "";
				JSONObject json = new JSONObject(httpResponse);
				Log.d("Hon",json.toString());
				// The file ID, will be used for attaching the file to a node.
				fileId = json.getString("fid");
				return fileId;
			}
			catch (Exception e)
			{
				Log.e(DRUPALTAG, "uploadPhoto");
				e.printStackTrace();
				return fileId;
			}
		}

		/* Attached the just-uploaded photo to node */
		protected Boolean attachPhoto(String fileId, String nodeId, int newIndex)
		{
			try
			{
				String url = service_url + "node/" + nodeId + ".json";

				HttpPut put = new HttpPut(url);
				HttpClient client = new DefaultHttpClient();

				//Authorize
				put.setHeader("Cookie", session_name + "=" + session_id);
				Log.d("Hon",session_id.toString());
				// Add your data
				String param = "media_gallery_media[und][" + Integer.toString(newIndex) + "][fid]";
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
				nameValuePairs.add(new BasicNameValuePair(param, fileId));
				nameValuePairs.add(new BasicNameValuePair("type", "media_gallery"));
				put.setEntity(new UrlEncodedFormEntity(nameValuePairs));

				Log.d(MAINTAG, param + fileId);

				String httpResponse = client.execute(put, res);

				Log.d("drupal", "Update node: ");
				Log.v("drupal", httpResponse);
				return true;
			}
			catch (Exception e)
			{
				Log.e(DRUPALTAG, "attachPhoto");
				e.printStackTrace();
				return false;
			}
		}

		/* Log out */
		protected Boolean logout()
		{
			try
			{
				String url = service_url + "user/logout";
				HttpPost post = new HttpPost(url);
				HttpClient client = new DefaultHttpClient();

				//Authorize
				post.setHeader("Cookie", session_name + "=" + session_id);

				String httpResponse = client.execute(post, res);
				Log.d("drupal", "Log out step: ");
				Log.v("drupal", httpResponse);
				return true;
			}
			catch (Exception e)
			{
				Log.e(DRUPALTAG, "logout");
				e.printStackTrace();
				return false;
			}
		}

		@SuppressWarnings("deprecation")
		@Override
		protected Boolean doInBackground(Integer... params)
		{
			Boolean result = false;

			pos = params[0];
			vh = viewholders.get(pos);
			pb = vh.pgbar;
			ivOK = vh.imgStatusOK;
			ivFail = vh.imgStatusFail;
			tvPC = vh.txtProcess;

			String _s = account_service[pos].toLowerCase();

			if (DrupalServices.title.toLowerCase().equals(_s))
			{
				acc = DrupalItem.getItem(ctx, account_id[pos]);
				String username = acc.getUsername();
				String password = acc.getPassword();
				service_url = acc.getSerivceURL();

				/* Login */
				for (int i = 0; i < (path.length) ; i++){
					String userId = login(username, password);
					if (userId == "") {
						return false;
					}

				/* Get list of nodes */
					String[] list = getOwnNodes(userId);
					if (list == null) {
						return false;
					}
				/* Choose the first list */
					String nodeId = list[0];
					if (nodeId == "") {
						return false;
					}
					// The index for the new photo is determined by the amount of existing photos
					int newIndex;
					try {

						newIndex = countMedia(nodeId);


					} catch (Exception e) {
						e.printStackTrace();
						return false;
					}
					String fileId = uploadPhoto(path[i]);
					if (fileId == "") {
						return false;
					}

					Boolean attached = attachPhoto(fileId, nodeId, newIndex);
					if (!attached) {
						return false;
					}
					logout();
				}

				result = true;
			}
			else if (TwitterServices.title.toLowerCase().equals(_s))
			{
				TwitterItem acc = TwitterItem.getItem(ctx, account_id[pos]);
				String token = acc.getToken();
				String token_secret = acc.getTokenSecret();
				Log.d("Danh","token: "+token+",token_secret : "+token_secret);
				try
				{
					String url = "https://upload.twitter.com/1/statuses/update_with_media.json";

					URI uri = new URI(url);

					HttpClient httpclient = null;
					try
					{
						KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
						trustStore.load(null, null);

						SSLSocketFactory sf = new MySSLSocketFactory(trustStore);
						sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

						HttpParams _params = new BasicHttpParams();
						HttpProtocolParams.setVersion(_params, HttpVersion.HTTP_1_1);
						HttpProtocolParams.setContentCharset(_params, HTTP.UTF_8);

						SchemeRegistry registry = new SchemeRegistry();
						registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
						registry.register(new Scheme("https", sf, 443));

						ClientConnectionManager ccm = new ThreadSafeClientConnManager(_params, registry);

						httpclient = new DefaultHttpClient(ccm, _params);

						trustStore = null; //Clean up memory
						sf = null; //Clean up memory
						_params = null; //Clean up memory
						registry = null; //Clean up memory
						ccm = null; //Clean up memory
					}
					catch (Exception e)
					{
						httpclient = new DefaultHttpClient();
					}

					OAuthConsumer consumer = new CommonsHttpOAuthConsumer(TwitterServices.CONSUMER_KEY, TwitterServices.CONSUMER_SECRET);
					for (int i = 0; i < path.length ; i++){
						HttpPost httppost = new HttpPost(uri);
						CustomMultiPartEntity multi = new CustomMultiPartEntity(new ProgressListener()
						{
							@Override
							public void transferred(long num)
							{
								publishProgress((int) ((num / (float) totalSize) * 100));
							}
						});

						multi.addPart("media", new FileBody(new File(path[i].split(";")[0])));
						if (path[i].split(";").length == 2){
							JSONObject js = new JSONObject(path[i].split(";")[1]);
							String lat = js.getString("lati");
							String logi = js.getString("logi");
							String name = js.getString("name");
							multi.addPart("status", new StringBody(name));
							multi.addPart("lat", new StringBody(lat));
							multi.addPart("long", new StringBody(logi));
						}
						totalSize = multi.getContentLength();
						httppost.setEntity(multi);

						consumer.setTokenWithSecret(token, token_secret);

						consumer.sign(httppost);

						ResponseHandler<String> res = new BasicResponseHandler();

						String httpResponse = httpclient.execute(httppost, res);
						Log.d("Upload Twitter","response : "+httpResponse);
						JSONObject json = new JSONObject(httpResponse);

						if (json.getString("id_str") != null)
						{
							result = true;
						}
						else
						{
							result = false;
						}
						Thread.sleep(15000);
					}

				}
				catch (Exception e)
				{
					e.printStackTrace();

					return false;
				}
			}
			else if (FacebookServices.title.toLowerCase().equals(_s))
			{
				String[] images = imagelist.split("#");
				ArrayList<Bitmap> imagesBitmapArray = new ArrayList<Bitmap>();

				for (int i=0; i<images.length; i++){
					BitmapFactory.Options bmOptions = new BitmapFactory.Options();
					imagesBitmapArray.add(i, BitmapFactory.decodeFile(images[i], bmOptions));
				}

				List<SharePhoto> photos = new ArrayList<SharePhoto>();
				for (int i=0; i<images.length; i++){
					SharePhoto photo = new SharePhoto.Builder()
							.setBitmap(imagesBitmapArray.get(i))
							.build();
					photos.add(photo);
				}

				content = new SharePhotoContent.Builder()
						.addPhotos(photos)
						.build();

				result = true;


//				try
//				{
//					FacebookItem acc = FacebookItem.getItem(ctx, account_id[pos]);
//					String access_token = acc.getAccessToken();
//					String url = "https://graph.facebook.com/v2.7/me/photos";
//
//					url += "?access_token=" + access_token;
//
//					Log.i("facebook", "Upload Photo URL: " + url);
//
//					URI uri = new URI(url);
//
//					HttpClient httpClient = null;
//
//
//			    	try
//				    {
//			    		KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
//				        trustStore.load(null, null);
//
//				        SSLSocketFactory sf = new MySSLSocketFactory(trustStore);
//				        sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
//
//				        HttpParams _params = new BasicHttpParams();
//				        HttpProtocolParams.setVersion(_params, HttpVersion.HTTP_1_1);
//				        HttpProtocolParams.setContentCharset(_params, HTTP.UTF_8);
//
//				        SchemeRegistry registry = new SchemeRegistry();
//				        registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
//				        registry.register(new Scheme("https", sf, 443));
//
//				        ClientConnectionManager ccm = new ThreadSafeClientConnManager(_params, registry);
//
//				        httpClient = new DefaultHttpClient(ccm, _params);
//
//				        trustStore = null; //Clean up memory
//				        sf = null; //Clean up memory
//				        _params = null; //Clean up memory
//				        registry = null; //Clean up memory
//				        ccm = null; //Clean up memory
//				    }
//				    catch (Exception e)
//				    {
//				       	httpClient = new DefaultHttpClient();
//				    }
//
//					HttpPost httppost = new HttpPost(uri);
//					for (int i = 0; i < path.length ; i++){
//					CustomMultiPartEntity multi = new CustomMultiPartEntity(new ProgressListener()
//					{
//						@Override
//						public void transferred(long num)
//						{
//							publishProgress((int) ((num / (float) totalSize) * 100));
//						}
//					});
//
//					multi.addPart("source", new FileBody(new File(path[i].split(";")[0])));
//					if (path[i].split(";").length ==2){
//						JSONObject js = new JSONObject(path[i].split(";")[1]);
//						String name = js.getString("name");
//					multi.addPart("name", new StringBody(name));
//					}
//					totalSize = multi.getContentLength();
//
//					httppost.setEntity(multi);
//
//					ResponseHandler<String> res = new BasicResponseHandler();
//
//					String httpResponse = httpClient.execute(httppost, res);
//
//					Log.i("facebook", "Response from Upload: " + httpResponse);
//
//					JSONObject json = new JSONObject(httpResponse);
//
//					if (json.getString("id") != null)
//					{
//						result = true;
//					}
//					else
//					{
//						result = false;
//					}
//					}
//				}
//				catch (Exception e)
//				{
//					Log.e("facebook", e.toString());
//					Log.d("facebook err :","Your account has expired time access, please login again !");
//					e.printStackTrace();
//
//					return false;
//				}
			}
			else if (FlickrServices.title.toLowerCase().equals(_s))
			{

				for (int i = 0; i < path.length ; i++){
					try
					{
						FlickrItem acc = FlickrItem.getItem(ctx, account_id[pos]);
						String token = acc.getToken();

						String token_secret = acc.getTokenSecret();
						Log.d("Danh","token : "+token+",token_secret : "+token_secret);
						String p[] = path[i].split(";");
						String filename = "";
						String tags ="";
						String latitude ="";
						String longitude="";
						if (p.length ==2 ){
							JSONObject js = new JSONObject(p[1]);
							filename = js.getString("name");
							tags = js.getString("tags");
							latitude = js.getString("lati");
							longitude = js.getString("logi");
						}else{
							filename = new File(p[0]).getName();
						}

						String url = "http://api.flickr.com/services/upload/";

						url += "?title=" + URLEncoder.encode(filename);
						url += "&content_type=" + URLEncoder.encode("1");
						String tag_str = "";

						if (!tags.equals(""))
						{
							String[] tags_arr = tags.split(default_tag_separate);
							for (int ii = 0; ii < tags_arr.length; ii++)
							{
								String tag = tags_arr[ii];
								if (tag.contains(" ")) tag = "\"" + tag + "\"";
								tags_arr[i] = tag;
							}
							tag_str = TextUtils.join(FlickrServices.tag_separate, tags_arr);
						}

						if (!tag_str.equals(""))
						{
							url += "&tags=" + URLEncoder.encode(tag_str);
						}

						URI uri = new URI(url);

						Log.d("flickr", "URL: " + url);

						HttpClient httpclient = new DefaultHttpClient();

						HttpPost httppost = new HttpPost(uri);

						OAuthConsumer consumer = new CommonsHttpOAuthConsumer(FlickrServices.CONSUMER_KEY, FlickrServices.CONSUMER_SECRET);

						HttpParameters hp = new HttpParameters();
						hp.put("title", filename);
						hp.put("tags", tag_str);

						consumer.setAdditionalParameters(hp);

						OAuthMessageSigner oms = new HmacSha1MessageSigner();

						consumer.setMessageSigner(oms);

						consumer.setTokenWithSecret(token, token_secret);


						CustomMultiPartEntity multi = new CustomMultiPartEntity(new ProgressListener()
						{
							@Override
							public void transferred(long num)
							{
								publishProgress((int) ((num / (float) totalSize) * 100));
							}
						});
						multi.addPart("title", new StringBody(filename));
						multi.addPart("content_type", new StringBody("1"));
						multi.addPart("tags", new StringBody(tag_str));
						multi.addPart("photo", new FileBody(new File(p[0])));

						totalSize = multi.getContentLength();

						multi.consumeContent();
						httppost.setEntity(multi);

						ResponseHandler<String> res = new BasicResponseHandler();

						consumer.sign(httppost);

						String response = httpclient.execute(httppost, res);
						Log.d("Flickr", response);

					/*
					 * Add geolocation infor for photos
					 */
						if ((!latitude.equals("")) && (!longitude.equals("")))
						{
							try
							{
								DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();

								Document doc = builder.parse(new InputSource(new ByteArrayInputStream(response.getBytes("utf-8"))));

								String photoid = "";

								if (doc != null)
								{
									NodeList nl = doc.getElementsByTagName("photoid");

									if (nl.getLength() > 0)
									{
										photoid = nl.item(0).getTextContent();
									}
								}

								url = "http://api.flickr.com/services/rest/?method=flickr.photos.geo.setLocation";

								uri = new URI(url);

								httpclient = new DefaultHttpClient();

								httppost = new HttpPost(uri);

								consumer = new CommonsHttpOAuthConsumer(FlickrServices.CONSUMER_KEY, FlickrServices.CONSUMER_SECRET);

								HttpParameters hp2 = new HttpParameters();
								hp2.put("photo_id", photoid);
								hp2.put("lat", latitude);
								hp2.put("lon", longitude);

								consumer.setAdditionalParameters(hp2);

								oms = new HmacSha1MessageSigner();

								consumer.setMessageSigner(oms);

								consumer.setTokenWithSecret(token, token_secret);

								MultipartEntity multi1 = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);

								multi1.addPart("photo_id", new StringBody(photoid));
								multi1.addPart("lat", new StringBody(latitude));
								multi1.addPart("lon", new StringBody(longitude));

								multi1.consumeContent();
								httppost.setEntity(multi1);

								ResponseHandler<String> res2 = new BasicResponseHandler();

								consumer.sign(httppost);

								String _response = httpclient.execute(httppost, res2);

								Log.d("flickr", "Response from setLocation");
								Log.v("flickr", _response);
							}
							catch (Exception e)
							{
								Log.e("flickr", "Error on Get PhotoID: " + e.toString());

								e.printStackTrace();
							}
						}

						result = true;
					}
					catch (Exception e)
					{
						e.printStackTrace();

						Log.e("flickr", "UploadPhoto: " + e.toString());

						result = false;
					}
				}
			}
			else if (TumblrServices.title.toLowerCase().equals(_s))
			{
				TumblrItem acc = TumblrItem.getItem(ctx, account_id[pos]);
				String token = acc.getToken();
				String token_secret = acc.getTokenSecret();
				String base_hostname = acc.getUserID();
				Log.d("Danh","token : "+token+",token_secret : "+token_secret+",base_hostname: "+base_hostname);
				for(int i =0 ; i< path.length; i++){
					try
					{
						String p[] = path[i].split(";");
						String filename = "";
						String tags ="";
						if (p.length ==2 ){
							JSONObject js = new JSONObject(p[1]);
							filename = js.getString("name");
							tags = js.getString("tags");
						}else{
							filename = new File(p[0]).getName();
						}
						String url = "http://api.tumblr.com/v2/blog/" + base_hostname + ".tumblr.com/post?type=photo"+"&caption=" + URLEncoder.encode(filename);

						String tag_str = "";

						if (!tags.equals(""))
						{
							String[] tags_arr = tags.split(default_tag_separate);

							tag_str = TextUtils.join(TumblrServices.tag_separate, tags_arr);
						}

						if (!tag_str.equals(""))
						{
							url += "&tags=" + URLEncoder.encode(tag_str);
						}

						Log.d("tumblr", "URL: " + url);

						URI uri = new URI(url);

						HttpClient httpclient = new DefaultHttpClient();

						HttpPost httppost = new HttpPost(uri);

						OAuthConsumer consumer = new CommonsHttpOAuthConsumer(TumblrServices.CONSUMER_KEY, TumblrServices.CONSUMER_SECRET);

						consumer.setTokenWithSecret(token, token_secret);

						CustomMultiPartEntity multi = new CustomMultiPartEntity(new ProgressListener()
						{
							@Override
							public void transferred(long num)
							{
								publishProgress((int) ((num / (float) totalSize) * 100));
							}
						});

						if (!tag_str.equals(""))
						{
							multi.addPart("tags", new StringBody(tag_str));
						}

						multi.addPart("type", new StringBody("photo"));
						multi.addPart("data", new FileBody(new File(p[0])));
						multi.addPart("caption", new StringBody(filename));
						totalSize = multi.getContentLength();

						multi.consumeContent();

						httppost.setEntity(multi);

						ResponseHandler<String> res = new BasicResponseHandler();

						consumer.sign(httppost);

						String httpResponse = httpclient.execute(httppost, res);

						Log.i("tumblr", "Response from Upload: " + httpResponse);

						JSONObject json_meta = new JSONObject(httpResponse).getJSONObject("meta");

						if (json_meta.getInt("status") == 201)
						{
							result = true;
						}
						else
						{
							result = false;
						}
					}
					catch (Exception e)
					{
						e.printStackTrace();

						return false;
					}
				}
			}
			else if (PicasaServices.title.toLowerCase().equals(_s))
			{
				PicasaItem acc = PicasaItem.getItem(ctx, account_id[pos]);
				String TAG = "picasa";
				HttpClient client = null;
				HttpPost httppost = null;

				try
				{
					KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
					trustStore.load(null, null);

					SSLSocketFactory sf = new Commons.MySSLSocketFactory(trustStore);
					sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

					HttpParams _params = new BasicHttpParams();
					HttpProtocolParams.setVersion(_params, HttpVersion.HTTP_1_1);
					HttpProtocolParams.setContentCharset(_params, HTTP.UTF_8);

					SchemeRegistry registry = new SchemeRegistry();
					registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
					registry.register(new Scheme("https", sf, 443));

					ClientConnectionManager ccm = new ThreadSafeClientConnManager(_params, registry);

					client = new DefaultHttpClient(ccm, _params);

					trustStore = null; //Clean up memory
					sf = null; //Clean up memory
					_params = null; //Clean up memory
					registry = null; //Clean up memory
					ccm = null; //Clean up memory
				}
				catch (Exception e)
				{
					client = new DefaultHttpClient();
				}

				String user_infor = PicasaServices.GetUserInfo(acc.getAccessToken(), acc.getTokenType());

				if (user_infor.equals(""))
				{
					String a = PicasaServices.GetRefreshToken(acc.getAccessToken(), acc.getRefreshToken());

					Log.d(MAINTAG, "Response from GetRefreshToken: " + a);

					try
					{
						JSONObject json = new JSONObject(a);
						String new_access_token = json.getString("access_token");
						String new_token_type = json.getString("token_type");
						String new_id_token = json.getString("id_token");
						if (PicasaItem.updateRefreshToken(ctx, acc.getAccountID(), acc.getUserID(), acc.getUserName(), new_access_token, new_token_type, new_id_token, acc.getRefreshToken()))
						{
							acc = PicasaItem.getItem(ctx, account_id[pos]);
						}
						else
						{
							return false;
						}
					}
					catch (Exception e)
					{
						return false;
					}
				}
				for (int j =0; j < path.length ; j++){
					String p[] = path[j].split(";");
					String url = "https://picasaweb.google.com/data/feed/api";

					url += "/user/" + acc.getUserID() + "/albumid/default";

					Log.i(TAG, "Upload Photo URL: " + url);

					try
					{
						URI uri = new URI(url);

						httppost = new HttpPost(uri);

						CustomFilePartEntity fe = new CustomFilePartEntity(
								new File(p[0]),
								"image/jpeg",
								new CustomFilePartEntity.ProgressListener()
								{
									@Override
									public void transferred(long num)
									{
										publishProgress((int) ((num / (float) totalSize) * 100));
									}
								});

						fe.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "image/jpeg"));

						totalSize = fe.getContentLength();

						httppost.setEntity(fe);

						httppost.addHeader("Authorization", "Bearer " + acc.getAccessToken());

						httppost.addHeader("GData-Version", "2");

						ResponseHandler<String> res = new BasicResponseHandler();

						String httpResponseUpload = client.execute(httppost, res);

						Log.i(TAG, "Response from Upload: " + httpResponseUpload);
						result = true;
					}
					catch (Exception e)
					{
						Log.e(TAG, e.toString());

						e.printStackTrace();

						return false;
					}
				}

			}
			else if (DeviantArtService.title.toLowerCase().equals(_s))
			{
				try
				{

					DeviantArtItem acc = DeviantArtItem	.getItem(ctx, account_id[pos]);
					String access_token = acc.getAcessToken();



					String url = "https://www.deviantart.com/api/draft15/stash/submit";

					url += "?access_token=" + access_token;

					Log.i("deviantart", "Upload Photo URL: " + url );

					URI uri = new URI(url);

					HttpClient httpClient = null;


					try
					{
						KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
						trustStore.load(null, null);

						SSLSocketFactory sf = new MySSLSocketFactory(trustStore);
						sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

						HttpParams _params = new BasicHttpParams();
						HttpProtocolParams.setVersion(_params, HttpVersion.HTTP_1_1);
						HttpProtocolParams.setContentCharset(_params, HTTP.UTF_8);

						SchemeRegistry registry = new SchemeRegistry();
						registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
						registry.register(new Scheme("https", sf, 443));

						ClientConnectionManager ccm = new ThreadSafeClientConnManager(_params, registry);

						httpClient = new DefaultHttpClient(ccm, _params);

						trustStore = null; //Clean up memory
						sf = null; //Clean up memory
						_params = null; //Clean up memory
						registry = null; //Clean up memory
						ccm = null; //Clean up memory
					}
					catch (Exception e)
					{
						httpClient = new DefaultHttpClient();
					}

					HttpPost httppost = new HttpPost(uri);
					for (int i = 0; i < path.length ; i++){
						String p[] = path[i].split(";");
						CustomMultiPartEntity multi = new CustomMultiPartEntity(new ProgressListener()
						{
							@Override
							public void transferred(long num)
							{
								publishProgress((int) ((num / (float) totalSize) * 100));
							}
						});

						multi.addPart("source", new FileBody(new File(p[0])));


						totalSize = multi.getContentLength();

						httppost.setEntity(multi);

						ResponseHandler<String> res = new BasicResponseHandler();

						String httpResponse = httpClient.execute(httppost, res);

						Log.i("deviantart", "Response from Upload: " + httpResponse);

						JSONObject json = new JSONObject(httpResponse);

						if (json.getString("status").equals("success"))
						{
							result = true;
						}
						else
						{
							result = false;
						}
					}
				}
				catch (Exception e)
				{
					//expired access token -> use refresh token to assign new access token
					DeviantArtItem acc = DeviantArtItem	.getItem(ctx, account_id[pos]);

					String refresh_token = acc.getRefreshToken();

					String respone =DeviantArtService.RefreshToken(refresh_token);

					JSONObject js;
					try {
						js = new JSONObject(respone);
						String access_token_new = js.getString("access_token");
						String refresh_token_new = js.getString("refresh_token");
						Log.d("deviantart", "Access token new: " + access_token_new+ "refresh token new :"+refresh_token_new);
						DeviantArtItem.updateDeviantArtAccount(ctx,String.valueOf(account_id[pos]), access_token_new, refresh_token_new);
					} catch (JSONException e1) {
						e1.printStackTrace();
					}

					Log.e("DeviantArt", e.toString());
					Log.e("DeviantArt", e.toString());

					e.printStackTrace();

					return false;
				}
			}
			else if (ImageshackServices.title.toLowerCase().equals(_s)){
				try{
					ImageshackItem acc = ImageshackItem.getItem(ctx, account_id[pos]);
					String registratorcode = acc.getRegistratorCode();
					String user_id = acc.getAccountID();
					Log.d("registrator code",registratorcode);
					String url = "http://www.imageshack.us/upload_api.php";
					Log.i("imageshack", "Upload Photo URL: " + url);
					HttpClient httpClient = new DefaultHttpClient();
					HttpPost httppost = new HttpPost(url);

					for(int i =0 ; i < path.length ; i++){
						String p[] = path[i].split(";");
						CustomMultiPartEntity multi = new CustomMultiPartEntity(new ProgressListener()
						{
							@Override
							public void transferred(long num)
							{
								publishProgress((int) ((num / (float) totalSize) * 100));
							}
						});

						multi.addPart("fileupload", new FileBody(new File(p[0])));
						multi.addPart("optsize", new StringBody("resample"));
						multi.addPart("cookie", new StringBody(registratorcode));
						multi.addPart("key", new StringBody(ImageshackServices.API_KEY));
						totalSize = multi.getContentLength();
						httppost.setEntity(multi);
						Log.d("totalsize",String.valueOf(totalSize));
						ResponseHandler<String> res = new BasicResponseHandler();

						String httpResponse = httpClient.execute(httppost, res);
						String imagelink = "";
						imagelink = httpResponse.substring(httpResponse.indexOf("<image_link>")+12, httpResponse.indexOf("</image_link>"));
						Log.i("Imageshack", "Response from Upload: " + httpResponse);
						Log.i("Imageshack", "Link image: " + imagelink);
						ImageshackPhotoList.insertPhoto(ctx, null, user_id, path[i],"personal_imageshack");
						if (imagelink == "") result = false; else	result = true;
					}
				}catch(Exception e){
					e.printStackTrace();
				}
			}
			else if (VKServices.title.toLowerCase().equals(_s)){
				HttpClient httpClient = null;
				try
				{
					KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
					trustStore.load(null, null);

					SSLSocketFactory sf = new MySSLSocketFactory(trustStore);
					sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

					HttpParams _params = new BasicHttpParams();
					HttpProtocolParams.setVersion(_params, HttpVersion.HTTP_1_1);
					HttpProtocolParams.setContentCharset(_params, HTTP.UTF_8);

					SchemeRegistry registry = new SchemeRegistry();
					registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
					registry.register(new Scheme("https", sf, 443));

					ClientConnectionManager ccm = new ThreadSafeClientConnManager(_params, registry);

					httpClient = new DefaultHttpClient(ccm, _params);

					trustStore = null; //Clean up memory
					sf = null; //Clean up memory
					_params = null; //Clean up memory
					registry = null; //Clean up memory
					ccm = null; //Clean up memory
				}
				catch (Exception e)
				{
					httpClient = new DefaultHttpClient();
				}
				VkItem acc = VkItem.getItem(ctx, account_id[pos]);
				String uri = acc.getUserUpload();
				String access_token = acc.getToken();
				HttpPost httppost = new HttpPost(uri);
				Log.d("uri",uri);
				for (int i = 0; i < path.length ; i++){
					String p[] = path[i].split(";");
					CustomMultiPartEntity multi = new CustomMultiPartEntity(new ProgressListener()
					{
						@Override
						public void transferred(long num)
						{
							publishProgress((int) ((num / (float) totalSize) * 100));
						}
					});

					multi.addPart("file2", new FileBody(new File(p[0])));
					try {
						multi.addPart("photo", new StringBody("image/jpg"));
					} catch (UnsupportedEncodingException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					totalSize = multi.getContentLength();

					httppost.setEntity(multi);

					ResponseHandler<String> res = new BasicResponseHandler();

					String httpResponse;
					try {
						httpResponse = httpClient.execute(httppost, res);
						Log.i("VK", "Response from Upload: " + httpResponse);
						JSONObject json = new JSONObject(httpResponse);
						String server = json.getString("server");
						String photos = json.getString("photos_list");
						String aid = json.getString("aid");
						String hash = json.getString("hash");
						result = VKServices.savePhoto(server, photos, hash, aid, access_token);
					} catch (ClientProtocolException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}

			else if (ImgurServices.title.toLowerCase().equals(_s))
			{
				ImgurItem acc = ImgurItem.getItem(ctx, account_id[pos]);
				String token = acc.getToken();
				String token_secret = acc.getTokenSecret();
				Log.d("Danh","token : "+token+",token_secret : "+token_secret);
				try
				{
					String url = "http://api.imgur.com/2/upload.json";

					URI uri = new URI(url);

					HttpClient httpclient = new DefaultHttpClient();

					HttpPost httppost = new HttpPost(uri);
					OAuthConsumer consumer = new CommonsHttpOAuthConsumer(ImgurServices.CONSUMER_KEY, ImgurServices.CONSUMER_SECRET);
					for (int i = 0; i < path.length ; i++){
						CustomMultiPartEntity multi = new CustomMultiPartEntity(new ProgressListener()
						{
							@Override
							public void transferred(long num)
							{
								publishProgress((int) ((num / (float) totalSize) * 100));
							}
						});

						multi.addPart("image", new FileBody(new File(path[i].split(";")[0])));
						if (path[i].split(";").length == 2){
							JSONObject js = new JSONObject(path[i].split(";")[1]);
							String lat = js.getString("lati");
							String logi = js.getString("logi");
							String name = js.getString("name");
							multi.addPart("status", new StringBody(name));
							multi.addPart("lat", new StringBody(lat));
							multi.addPart("long", new StringBody(logi));
						}
						totalSize = multi.getContentLength();
						Log.e("UploadProgress", "total size : "+totalSize);
						httppost.setEntity(multi);

						consumer.setTokenWithSecret(token, token_secret);

						consumer.sign(httppost);

						ResponseHandler<String> res = new BasicResponseHandler();

						String httpResponse = httpclient.execute(httppost, res);
						Log.d("Upload Progress", "http upload response : "+httpResponse );
						JSONObject json = new JSONObject(httpResponse);

						if (json.getString("upload")!=null)
						{
							result = true;
						}
						else
						{
							result = false;
						}
					}

				}
				catch (Exception e)
				{
					e.printStackTrace();

					return false;
				}
			}

			else if (SohuServices.title.toLowerCase().equals(_s))
			{
				SohuItem acc = SohuItem.getItem(ctx, account_id[pos]);
				String token = acc.getToken();
				String token_secret = acc.getTokenSecret();
				Log.d("Danh","token : "+token+",token_secret : "+token_secret);
				try
				{
					String url = "http://api.t.sohu.com/statuses/upload.json";

					URI uri = new URI(url);

					HttpClient httpclient = new DefaultHttpClient();
					OAuthConsumer consumer = new CommonsHttpOAuthConsumer(SohuServices.CONSUMER_KEY, SohuServices.CONSUMER_SECRET);
					for (int i = 0; i < path.length ; i++){
						HttpPost httppost = new HttpPost(uri);

						CustomMultiPartEntity multi = new CustomMultiPartEntity(new ProgressListener()
						{
							@Override
							public void transferred(long num)
							{
								publishProgress((int) ((num / (float) totalSize) * 100));
							}
						});

						multi.addPart("pic", new FileBody(new File(path[i].split(";")[0])));
						totalSize = multi.getContentLength();
						httppost.setEntity(multi);

						consumer.setTokenWithSecret(token, token_secret);

						consumer.sign(httppost);

						ResponseHandler<String> res = new BasicResponseHandler();

						String httpResponse = httpclient.execute(httppost, res);
						Log.d("Upload Progress", "http upload response : "+httpResponse );
						JSONObject json = new JSONObject(httpResponse);

						if (json.getString("created_at")!=null)
						{
							result = true;
						}
						else
						{
							result = false;
						}
					}

				}
				catch (Exception e)
				{
					e.printStackTrace();

					return false;
				}
			}

			else if ("wordpressdotcom".equals(_s)){
				if (mSystemService.getActiveNetworkInfo() == null) {
					return false;
				}else{

					for (int i = 0; i < path.length ; i++){
						progressStatus = 0;
						String content="";
						int featuredImageID=-1;
						MediaFile mf=new MediaFile();

						WordpressItem acc = WordpressItem.getItem(ctx, account_id[pos]);
						String username=acc.getUsername();
						String password=acc.getPassword();
						String url=acc.getUrl();
						//progress bar
						new Thread(new Runnable() {
							public void run() {
								while (progressStatus < 100) {
									try{
										myHandle.sendMessage(myHandle.obtainMessage());
										Thread.sleep(100);

									}
									catch(Throwable t){   }
								}
							}
						}).start();

						// check catagory and set phimpme is default catagory
						AddCatagory add=new AddCatagory();
						add.getCategories(url, username, password);

						String[] theCategories = null;
						theCategories = new String[1];
						theCategories[0] = "phimpme mobile";

						XMLRPCClient client = new XMLRPCClient(url,"", "");
						//create temp file for media upload
						String tempFileName = "wp-" + System.currentTimeMillis();

						String imagepath=path[i].split(";")[0];
						try {
							ctx.openFileOutput(tempFileName, Context.MODE_PRIVATE);
						} catch (FileNotFoundException e) {
							e.printStackTrace();
						}

						File tempFile = ctx.getFileStreamPath(tempFileName);

						//=================get link upload==================================

						mf.setFilePath(imagepath);
						Map<String, Object> m = new HashMap<String, Object>();
						File jpeg=new File(imagepath);
						String filename=jpeg.getName();
						Log.d("UploadProgress", "filename : "+filename);
						m.put("name", filename);
						m.put("type", "image/jpeg");
						m.put("bits", mf);
						m.put("overwrite", true);

						Object[] params1 = { 1, username,password, m };
						Object result1 = null;

						try {
							result1 = (Object) client.callUploadFile("wp.uploadFile",params1, tempFile);
							Log.d("UploadProgress","result : "+result1.toString());
						} catch (XMLRPCException e) {
							e.printStackTrace();
						}

						//=================content to upload=================================
						HashMap<?, ?> contentHash = new HashMap<Object, Object>();

						contentHash = (HashMap<?, ?>) result1;

						String resultURL = contentHash.get("url").toString();


						try {
							if (contentHash.get("id") != null) {
								featuredImageID = Integer.parseInt(contentHash.get("id").toString());
							}
						} catch (NumberFormatException e) {
							e.printStackTrace();
						}

						String alignmentCSS = "class=\"" + "alignnone"+ "\" ";
						if (resultURL != null) {
							content = content
									+ "<a href=\""
									+ resultURL
									+ "\"><img title=\""
									+ mf.getTitle() + "\" "
									+ alignmentCSS
									+ "alt=\"image\" src=\""
									+ resultURL + "\" /></a>";

						}

						//========start upload photo to wordpress====================
						Map<String, Object> contentStruct = new HashMap<String, Object>();
						contentStruct.put("wp_post_format","standard");
						contentStruct.put("post_type", "post");
						contentStruct.put("title", "");
						contentStruct.put("wp_password", "");
						contentStruct.put("description", content);
						contentStruct.put("mt_keywords", "");
						contentStruct.put("categories", theCategories);
						contentStruct.put("mt_excerpt", "");
						contentStruct.put("post_status","publish");

						if (featuredImageID != -1)
							contentStruct.put("wp_post_thumbnail", featuredImageID);

						Object[] params2;
						params2 = new Object[] { 1,username, password,contentStruct, false };
						XMLRPCClient client1 = new XMLRPCClient(url,"", "");

						try {
							Object result3=null;
							result3=(Object)client1.call("metaWeblog.newPost", params2);
							Log.d("UploadProgress","result upload : "+result3.toString());
							result=true;

						} catch (final XMLRPCException e) {
							e.printStackTrace();
						}


					}

				}
			}
			else if ("wordpress".equals(_s)){
				if (mSystemService.getActiveNetworkInfo() == null) {
					return false;
				}else{
					for (int i = 0; i < path.length ; i++){
						progressStatus1 = 0;
						String content="";
						int featuredImageID=-1;
						MediaFile mf=new MediaFile();

						WordpressItem acc = WordpressItem.getItem(ctx, account_id[pos]);
						String username=acc.getUsername();
						String password=acc.getPassword();
						String url=acc.getUrl();
						//progress bar
						new Thread(new Runnable() {
							public void run() {
								while (progressStatus1 < 100) {
									try{
										myHandle1.sendMessage(myHandle1.obtainMessage());
										Thread.sleep(1);

									}
									catch(Throwable t){   }
								}
							}
						}).start();

						AddCatagory add=new AddCatagory();
						add.getCategories(url, username, password);

						String[] theCategories = null;
						theCategories = new String[1];
						theCategories[0] = "phimpme mobile";

						XMLRPCClient client = new XMLRPCClient(url,"", "");
						//create temp file for media upload
						String tempFileName = "wp-" + System.currentTimeMillis();

						String imagepath = path[i].split(";")[0];
						try {
							ctx.openFileOutput(tempFileName, Context.MODE_PRIVATE);
						} catch (FileNotFoundException e) {
							e.printStackTrace();
						}

						File tempFile = ctx.getFileStreamPath(tempFileName);
						mf.setFilePath(imagepath);

						Map<String, Object> m = new HashMap<String, Object>();
						File jpeg=new File(imagepath);
						String filename=jpeg.getName();
						Log.d("UploadProgress", "filename : "+filename);
						m.put("name", filename);
						m.put("type", "image/jpeg");
						m.put("bits", mf);
						m.put("overwrite", true);

						Object[] params1 = { 1, username,password, m };
						Object result1 = null;

						try {
							result1 = (Object) client.callUploadFile("wp.uploadFile",params1, tempFile);
							Log.d("UploadProgress","result : "+result1.toString());
						} catch (XMLRPCException e) {
							e.printStackTrace();
						}
						HashMap<?, ?> contentHash = new HashMap<Object, Object>();

						contentHash = (HashMap<?, ?>) result1;

						String resultURL = contentHash.get("url").toString();


						try {
							if (contentHash.get("id") != null) {
								featuredImageID = Integer.parseInt(contentHash.get("id").toString());
							}
						} catch (NumberFormatException e) {
							e.printStackTrace();
						}

						String alignmentCSS = "class=\"" + "alignnone"+ "\" ";
						if (resultURL != null) {
							content = content
									+ "<a href=\""
									+ resultURL
									+ "\"><img title=\""
									+ mf.getTitle() + "\" "
									+ alignmentCSS
									+ "alt=\"image\" src=\""
									+ resultURL + "\" /></a>";

						}

						Map<String, Object> contentStruct = new HashMap<String, Object>();
						contentStruct.put("wp_post_format","standard");
						contentStruct.put("post_type", "post");
						contentStruct.put("title", "");
						contentStruct.put("wp_password", "");
						contentStruct.put("description", content);
						contentStruct.put("mt_keywords", "");
						contentStruct.put("categories", theCategories);
						contentStruct.put("mt_excerpt", "");
						contentStruct.put("post_status","publish");

						if (featuredImageID != -1)
							contentStruct.put("wp_post_thumbnail", featuredImageID);

						Object[] params2;
						params2 = new Object[] { 1,username, password,contentStruct, false };
						XMLRPCClient client1 = new XMLRPCClient(url,"", "");

						try {
							Object result3=null;
							result3=(Object)client1.call("metaWeblog.newPost", params2);
							Log.d("UploadProgress","result upload : "+result3.toString());
							result=true;

						} catch (final XMLRPCException e) {
							e.printStackTrace();
						}
					}

				}
			}
			else if ("joomla".equals(_s)){
				if (mSystemService.getActiveNetworkInfo() == null) {
					return false;
				}else{
					JoomlaItem user = JoomlaItem.getItem(ctx, account_id[pos]);
					for (int i = 0; i < path.length ; i++){
						JoooidRpc rpcClient = JoooidRpc.getInstance(user.getUrl(),Constants.TASK_WS_URI_J17,user.getUsername(),user.getPassword(),User.JOOMLA_16);

						try {
							File mFile = new File(path[i].split(";")[0]);
							Date mDate = new Date();
							String name = String.valueOf(mDate.getTime())+"_"+ mFile.getName();
							if (path[i].split(";").length == 2){
								JSONObject js = new JSONObject(path[i].split(";")[1]);
								//String lat = js.getString("lati");
								//tring logi = js.getString("logi");
								name = js.getString("name");
							}
							pb.setProgress(0);
							pb.setProgress(100);
							name = name.replaceAll("\\s", "");
							String imgUrl = rpcClient.uploadFile(user.getUsername(), user.getPassword(),name, mFile, "phimpme");
							String fulltext ="<p><img src=images/phimpme/"+name +" /></p>" ;
							Log.e("Image Url",imgUrl);
							SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
							String currentDate = formatter.format(mDate);
							String res = rpcClient.newPost(user.getUsername(), user.getPassword(), user.getCatId(), name, name, "Content has create by phimpme app", fulltext, 1, 1, true, currentDate);
							try{
								Integer.parseInt(res);
								result = true;
							}
							catch(NumberFormatException e){
								result = false;
							}
						} catch (com.joooid.android.xmlrpc.XMLRPCException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}
			return result;
		}
		@Override
		protected void onProgressUpdate(Integer... progress)
		{

			int _p = (int) progress[0];
			tvPC.setText(_p + "...%");
			pb.setProgress(_p);
		}

		@Override
		protected void onPostExecute(Boolean b)
		{
			tvPC.setVisibility(View.GONE);

			if (b)
			{
				shareDialog.show(content);
				ivOK.setVisibility(View.VISIBLE);
				ivFail.setVisibility(View.GONE);
			}
			else
			{
				ivOK.setVisibility(View.GONE);
				ivFail.setVisibility(View.VISIBLE);
			}
		}

		Handler myHandle = new Handler(){
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				progressStatus++;
				if(progressStatus<100){
					tvPC.setText(progressStatus + "...%");
				}else{
					tvPC.setText("100%");
				}
				pb.setProgress(progressStatus);

			}
		};
		Handler myHandle1 = new Handler(){
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				progressStatus1++;
				if(progressStatus1<100){
					tvPC.setText(progressStatus1 + "...%");
				}else{
					tvPC.setText("100%");
				}

				pb.setProgress(progressStatus1);


			}
		};

	}
}
