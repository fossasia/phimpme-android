package vn.mbm.phimp.me;

import org.json.JSONException;
import org.json.JSONObject;

import vn.mbm.phimp.me.database.AccountItem;
import vn.mbm.phimp.me.database.DeviantArtItem;
import vn.mbm.phimp.me.database.FacebookItem;
import vn.mbm.phimp.me.database.FlickrItem;
import vn.mbm.phimp.me.database.ImgurItem;
import vn.mbm.phimp.me.database.KaixinDBItem;
import vn.mbm.phimp.me.database.PicasaItem;
import vn.mbm.phimp.me.database.S500pxItem;
import vn.mbm.phimp.me.database.SohuItem;
import vn.mbm.phimp.me.database.TumblrItem;
import vn.mbm.phimp.me.database.TwitterItem;
import vn.mbm.phimp.me.database.VkItem;
import vn.mbm.phimp.me.services.DeviantArtService;
import vn.mbm.phimp.me.services.FacebookServices;
import vn.mbm.phimp.me.services.FlickrServices;
import vn.mbm.phimp.me.services.ImgurServices;
import vn.mbm.phimp.me.services.KaixinServices;
import vn.mbm.phimp.me.services.PicasaServices;
import vn.mbm.phimp.me.services.S500pxService;
import vn.mbm.phimp.me.services.SohuServices;
import vn.mbm.phimp.me.services.TumblrServices;
import vn.mbm.phimp.me.services.TwitterServices;
import vn.mbm.phimp.me.services.VKServices;
import vn.mbm.phimp.me.R;
import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class Webkit extends Activity 
{
	Context ctx;
	static Activity activity = new Activity();
	WebView myWebView;
	MyWebViewClient client;
	WebSettings ws;
	boolean check=false;
	
	@Override
    public void onCreate(Bundle savedInstanceState) 
    {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.webview);
		ctx = this;
		activity = (Activity) this;
		
		activity.getWindow().setFeatureInt( Window.FEATURE_PROGRESS, Window.PROGRESS_VISIBILITY_ON);
		
		client = new MyWebViewClient();
		
		CookieSyncManager.createInstance(this); 
		CookieManager cookieManager = CookieManager.getInstance(); 
		cookieManager.removeAllCookie();
		cookieManager.removeSessionCookie();
		
		myWebView = (WebView) findViewById(R.id.webview);
		if(check=false){
		myWebView.setWebChromeClient(new WebChromeClient() 
		{
            public void onProgressChanged(WebView view, int progress)   
            {
            	try
            	{
            		Log.d("Webkit","Run start");
            		activity.setTitle("Loading...");
            	
            		// Return the app name after finish loading
            		if(progress == 100)
            			activity.setTitle(R.string.application_title);
            	}
            	catch (Exception e) 
            	{
				}
            }
		});
		}
		myWebView.setWebViewClient(client);
		String page = getIntent().getStringExtra("URL");
		
		ws = myWebView.getSettings();
		
		ws.setJavaScriptEnabled(true);
		ws.setCacheMode(WebSettings.LOAD_NO_CACHE);	
		myWebView.loadUrl(page);
		
    }
	
	@Override
	public void onDestroy()
	{
		super.onDestroy();
		
		activity = null;
    }
	
	private class MyWebViewClient extends WebViewClient 
	{
		@Override
		public void onPageFinished(WebView view, String url)
		{
			Uri uri = Uri.parse(url);
			
			Log.d("webkit", "onPageFinished URL: " + url);
			Log.d("webkit", "onPageFinished Host: " + uri.getHost().toString());
			Log.d("webkit", "onPageFinished Path: " + uri.getPath().toString());
			
			if ((uri.getScheme().equals("oauth")) && (uri.getHost().equals("tumblr")))
			{
				/*
				 * Tumblr Service
				 */
				String verifier = uri.getQueryParameter("oauth_verifier");
				
				if (!verifier.equals(""))
				{
					String user_info = TumblrServices.oauthPutVerifierCode(verifier);
					try
					{
						JSONObject json = new JSONObject(user_info);
						String user_name = json.getString("user_name");
						String token = json.getString("token");
						String token_secret = json.getString("token_secret");
						String user_id = json.getString("user_id");
						String profile_url = json.getString("link");
						
						long account_id = AccountItem.insertAccount(ctx, null, user_name, "tumblr", "1");
						
						if (account_id > 0)
						{
							if (TumblrItem.insertTumblrAccount(ctx, String.valueOf(account_id), token, token_secret, user_id, user_name, profile_url))
							{
								Toast.makeText(ctx, "Insert account '" + user_name + "' (Tumblr) SUCCESS!", Toast.LENGTH_LONG).show();
							}
							else
							{
								Toast.makeText(ctx, "Insert account '" + user_name + "' (Tumblr) FAIL!", Toast.LENGTH_LONG).show();
							}
						}
					}
					catch (Exception e) 
					{
						e.printStackTrace();
						Log.e("webkit", "Tumblr Service - " + e.toString());
					}
				}
				
				activity.finish();
			}
			else if ((uri.getScheme().equals("oauth")) && (uri.getHost().equals("flickr")))
			{
				/*
				 * Flickr Service
				 */
				String verifier = uri.getQueryParameter("oauth_verifier");
				
				if (!verifier.equals(""))
				{
					
					String user_info = FlickrServices.OAuthPutVerifierCode(verifier);
					try
					{
						JSONObject json = new JSONObject(user_info);
						String user_name = json.getString("user_name");
						String token = json.getString("token");
						String token_secret = json.getString("token_secret");
						String user_id = json.getString("user_id");
						String profile_url = json.getString("profile_url");
						
						long account_id = AccountItem.insertAccount(ctx, null, user_name, "flickr", "1");
						
						if (account_id > 0)
						{
							if (FlickrItem.insertFlickrAccount(ctx, String.valueOf(account_id), token, token_secret, user_id, user_name, profile_url))
							{
								Toast.makeText(ctx, "Insert account '" + user_name + "' (Flickr) SUCCESS!", Toast.LENGTH_LONG).show();
							}
							else
							{
								Toast.makeText(ctx, "Insert account '" + user_name + "' (Flickr) FAIL!", Toast.LENGTH_LONG).show();
							}
						}
					}
					catch (Exception e) 
					{
						e.printStackTrace();
						Log.e("webkit", "Tumblr Service - " + e.toString());
					}
				}
				
				activity.finish();
			}
			if ((uri.getScheme().equals("oauth")) && (uri.getHost().equals("twitter")))
			{
				/*
				 * Twitter Service
				 */
				String verifier = uri.getQueryParameter("oauth_verifier");
				
				if (!verifier.equals(""))
				{
					String user_info = TwitterServices.OAuthPutVerifierCode(verifier);
					try
					{
						JSONObject json = new JSONObject(user_info);
						String user_name = json.getString("user_name");
						String user_fullname = json.getString("user_fullname");
						String token = json.getString("token");
						String token_secret = json.getString("token_secret");
						String user_id = json.getString("user_id");
						String profile_url = json.getString("profile_url");
						
						long account_id = AccountItem.insertAccount(ctx, null, user_fullname, "twitter", "1");
						
						if (account_id > 0)
						{
							if (TwitterItem.insertTwitterAccount(ctx, String.valueOf(account_id), token, token_secret, user_id, user_name, profile_url))
							{
								Toast.makeText(ctx, "Insert account '" + user_fullname + "' (Twitter) SUCCESS!", Toast.LENGTH_LONG).show();
							}
							else
							{
								Toast.makeText(ctx, "Insert account '" + user_fullname + "' (Twitter) FAIL!", Toast.LENGTH_LONG).show();
							}
						}
					}
					catch (Exception e) 
					{
						e.printStackTrace();
						Log.e("webkit", "Twitter Service - " + e.toString());
					}
				}
				
				activity.finish();
			}
			else if (check==false &&uri.getHost().toString().equals("phimp.me")&&uri.getPath().toString().equals("/"))			
			{
				/*www.facebook.com

				 * Facebook - Service
				 */
				String[] tmp = uri.getFragment().split("&");
				
				String access_token = tmp[0].replace("access_token=", "");
				Log.d("webkit", "Access_token: " + access_token);

				try
				{
					String access_token_change= FacebookServices.change_access_token(access_token);
					
			    	Log.d("webkit", "Access_token_change: " + access_token_change);
			    	
			    	String user_info = FacebookServices.getUserInfo(access_token_change);
					//String user_info = FacebookServices.getUserInfo(access_token);
					Log.d("webkit", "User Info:" + user_info);
					
					JSONObject json = new JSONObject(user_info);
					String user_id = json.getString("user_id");
		    		String user_name = json.getString("user_name");
		    		String user_fullname = json.getString("fullname");
		    		String profile_url = json.getString("link");
		    		String email = json.getString("email");
		    		
		    		long account_id = AccountItem.insertAccount(ctx, null, user_fullname, "facebook", "1");
					Log.d("ID",String.valueOf(account_id));
					if (account_id > 0)
					{
						if (FacebookItem.insertFacebookAccount(ctx, String.valueOf(account_id), access_token_change, user_id, user_name, user_fullname, email, profile_url))
						//if (FacebookItem.insertFacebookAccount(ctx, String.valueOf(account_id), access_token, user_id, user_name, user_fullname, email, profile_url))
						{
							Toast.makeText(ctx, "Insert account '" + user_fullname + "' (Facebook) SUCCESS!", Toast.LENGTH_LONG).show();
						}
						else
						{
							Toast.makeText(ctx, "Insert account '" + user_fullname + "' (Facebook) FAIL!", Toast.LENGTH_LONG).show();
						}
					}
				}
				catch (Exception e) 
				{
					e.printStackTrace();
					Log.e("webkit", "Facebook Service - " + e.toString());
				}
				check=true;
				activity.finish();
			}
			else if (uri.getPath().toString().equals(PicasaServices.CALLBACK_HOST))
			{
				/*
				 * Picasa - Service
				 */
				
				Log.d("webkit", "Picasa URL: " + uri.getQueryParameter("code"));
				
				String code = uri.getQueryParameter("code");
				
				String token_infor = PicasaServices.OAuthGetAccessToken(code, "code");
				Log.d("webkit", "User Info:" + token_infor);
				
				try
				{
					JSONObject _json = new JSONObject(token_infor);
					String access_token = _json.getString("access_token");
					String token_type = _json.getString("token_type");
					String id_token = _json.getString("id_token");
					String refresh_token = _json.getString("refresh_token");
					
					String user_info = PicasaServices.GetUserInfo(access_token, token_type);
					
					Log.d("webkit", "Picasa User Infor");
					Log.v("webkit", user_info);
					
					JSONObject json = new JSONObject(user_info);
					String user_id = json.getString("id");
		    		String user_name = json.getString("name");
		    		String profile_url = "https://plus.google.com/" + user_id;
		    		String email = json.getString("email");
		    		
		    		long account_id = AccountItem.insertAccount(ctx, null, user_name, "picasa", "1");
					
					if (account_id > 0)
					{
						if (PicasaItem.insertPicasaAccount(ctx, String.valueOf(account_id), user_id, user_name, email, profile_url, access_token, token_type, id_token, refresh_token))
						{
							Toast.makeText(ctx, "Insert account '" + user_name + "' (Picasa) SUCCESS!", Toast.LENGTH_LONG).show();
						}
						else
						{
							Toast.makeText(ctx, "Insert account '" + user_name + "' (Picasa) FAIL!", Toast.LENGTH_LONG).show();
						}
					}
				}
				catch (Exception e) 
				{
					e.printStackTrace();
					Log.e("webkit", "Picasa Service - " + e.toString());
				}
				
				activity.finish();
			}
			else if (uri.getPath().toString().equals(DeviantArtService.CALLBACK_HOST))			
			{
				Log.d("DeviantArt",uri.toString());
				String code = uri.getEncodedQuery().replace("code=", "");								
				Log.d("code",uri.getEncodedQuery());
				String response = DeviantArtService.getAccessToken(code);
				Log.d("Access Token",response);
				/*

				 * DeviantArt Service
				 */
				
				
				String access_token ="";
				String refresh_token = "";
				
				try{
				JSONObject js = new JSONObject(response);
				access_token = js.getString("access_token");
				refresh_token = js.getString("refresh_token");
				
				
				
				}catch(JSONException err)
				{
					err.printStackTrace();
				}
				
				String user_info = DeviantArtService.getUserInfo(access_token);
				Log.d("webkit", "User Info:" + user_info );
				
				try
				{
					JSONObject json = new JSONObject(user_info);					
		    		String user_name = json.getString("user_name");		    		
		    		String profile_url = json.getString("link");		    		
		    		
		    		long account_id = AccountItem.insertAccount(ctx, null, user_name, "deviantart", "1");
					
					if (account_id > 0)
					{
						if (DeviantArtItem.insertDeviantArtAccount(ctx, String.valueOf(account_id), access_token, refresh_token, user_name, profile_url))
						
						{
							Toast.makeText(ctx, "Insert account '" + user_name + "' (DeviantArt) SUCCESS!", Toast.LENGTH_LONG).show();
						}
						else
						{
							Toast.makeText(ctx, "Insert account '" + user_name + "' (DeviantArt) FAIL!", Toast.LENGTH_LONG).show();
						}
					}
				}
				catch (Exception e) 
				{
					e.printStackTrace();
					Log.e("webkit", "DeviantArt Service - " + e.toString());
				}
				activity.finish();
			}
			else if (uri.getPath().toString().equals("/blank.html")){
				String session = Uri.decode(uri.getEncodedFragment());
				String tmp[] = session.split("&");
				String access_token = tmp[0].replace("access_token=", "");
				String user_id = tmp[2].replace("user_id=", "");
				Log.d("access_token",access_token);
				Log.d("user_id",user_id);				
				try {
					JSONObject userinfo = new JSONObject(VKServices.getUserInfo(access_token, user_id));					
					String username = userinfo.getString("first_name");
					String aid = VKServices.createAlbums(access_token);
					Log.d("aid",aid);					
					String upload_url = VKServices.getPhotoUploadUrl(access_token, aid);
					Log.d("username",username);
					Log.d("username",upload_url);
					long account_id = AccountItem.insertAccount(ctx, null, username, "vkontakte", "1");					
					if (account_id > 0)
					{
						if (VkItem.insertVkAccount(ctx, String.valueOf(account_id),access_token,user_id,username,upload_url))
						
						{
							Toast.makeText(ctx, "Insert account '" + username + "' (Vkontakte) SUCCESS!", Toast.LENGTH_LONG).show();
						}
						else
						{
							Toast.makeText(ctx, "Insert account '" + username + "' (Vkontakte) FAIL!", Toast.LENGTH_LONG).show();
						}
					}
					
				} catch (JSONException e) {
					e.printStackTrace();
				}
				
				activity.finish();
			}
			else if (check==false && uri.getHost().toString().equals(KaixinServices.CALLBACK_HOST))			
			{
				Log.d("Kaixin",uri.toString());
				String code = uri.getEncodedQuery().replace("code=", "");								
				Log.d("code",uri.getEncodedQuery());
				String response = KaixinServices.getAccessToken(code);
				Log.d("Access Token",response);
				/*
				 * Kaixin Service
				 */	
				String access_token ="";
				String refresh_token = "";
				
				try{
				JSONObject js = new JSONObject(response);
				access_token = js.getString("access_token");
				refresh_token = js.getString("refresh_token");
				
				
				
				}catch(JSONException err)
				{
					err.printStackTrace();
				}
				String user_info = KaixinServices.getUserInfo(access_token);
				Log.d("webkit", "User Info:" + user_info );
				
				try
				{
					JSONObject json = new JSONObject(user_info);					
		    		String user_name = json.getString("user_name");		    		
		    		String user_id = json.getString("user_id");		    		
					String res_album = KaixinServices.getAlbumList(access_token);
					String album_id = KaixinServices.checkAlbum(res_album);
					Log.d("album", album_id);
					if(album_id.equals("empty")){
		    		//create album
					album_id =  KaixinServices.createAlbums(access_token);
					} 
		    		long account_id = AccountItem.insertAccount(ctx, null, user_name, "kaixin", "1");
					
					if (account_id > 0)
					{
						if (KaixinDBItem.insertKaixinAccount(ctx, String.valueOf(account_id), access_token, refresh_token, user_name, user_id, album_id))
						
						{
							Toast.makeText(ctx, "Insert account '" + user_name + "' (Kaixin) SUCCESS!", Toast.LENGTH_LONG).show();
						}
						else
						{
							Toast.makeText(ctx, "Insert account '" + user_name + "' (Kaixin) FAIL!", Toast.LENGTH_LONG).show();
						}
					}
				}
				catch (Exception e) 
				{
					e.printStackTrace();
					Log.e("webkit", "Kaixin Service - " + e.toString());
				}
				check=true;
				activity.finish();
			}
			
			/*
			 * Imgur services
			 */
			if ((uri.getScheme().equals("oauthflow-imgur")) && (uri.getHost().equals("imgur")))
			{
				String verifier = uri.getQueryParameter("oauth_verifier");
			
					String user_info = ImgurServices.OAuthPutVerifierCode(verifier);
					Log.d("Webkit_Imgur","response user info :" +user_info);
					try {
						JSONObject json = new JSONObject(user_info);
						String user_name = json.getString("user_name");
						String token = json.getString("token");
						String token_secret = json.getString("token_secret");

						Log.d("Webkit", "user name : "+user_name+" token : "+token+" token_secret :"+token_secret);
						
						long account_id = AccountItem.insertAccount(ctx, null, user_name, "imgur", "1");
						
						if (account_id > 0)
						{
							if (ImgurItem.insertImgurAccount(ctx, String.valueOf(account_id), token, token_secret, user_name))
							{
								Toast.makeText(ctx, "Insert account '" + user_name + "' (Imgur) SUCCESS!", Toast.LENGTH_LONG).show();
							}
							else
							{
								Toast.makeText(ctx, "Insert account '" + user_name + "' (Imgur) FAIL!", Toast.LENGTH_LONG).show();
							}
							
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
			activity.finish();
			}
			if ((uri.getScheme().equals("oauth")) && (uri.getHost().equals("500px")))
			{
				/*
				 * 500px Service
				 */
				String verifier = uri.getQueryParameter("oauth_verifier");
				Log.d("uri 500px=>>",verifier);
				if (!verifier.equals(""))
				{
					String user_info = S500pxService.OAuthPutVerifierCode(verifier);					
					try
					{
						JSONObject json = new JSONObject(user_info);
						String user_name = json.getString("username");
						
						String token = json.getString("token");
						String token_secret = json.getString("token_secret");
						String user_id = json.getString("user_id");
						String profile_url = json.getString("profile_url");
						
						long account_id = AccountItem.insertAccount(ctx, null, user_name, "500px", "1");
						
						if (account_id > 0)
						{
							if (S500pxItem.insert500pxAccount(ctx, String.valueOf(account_id), token, token_secret, user_id, user_name, profile_url))
							{
								Toast.makeText(ctx, "Insert account '" + user_name + "' (500px) SUCCESS!", Toast.LENGTH_LONG).show();
							}
							else
							{
								Toast.makeText(ctx, "Insert account '" + user_name + "' (500px) FAIL!", Toast.LENGTH_LONG).show();
							}
						}
					}
					catch (Exception e) 
					{
						e.printStackTrace();
						Log.e("webkit", "500px Service - " + e.toString());
					}
				}
				
				activity.finish();
			}	
			else if (check==false && (uri.getScheme().equals("oauth")) && (uri.getHost().equals("sohu")))
			{
				/*
				 * Sohu Service
				 */
				String verifier = uri.getQueryParameter("oauth_verifier");
				
				if (!verifier.equals(""))
				{
					
					String user_info = SohuServices.OAuthPutVerifierCode(verifier);
					Log.d("Webkit", "sohu user info : "+user_info);
					try
					{
						JSONObject json = new JSONObject(user_info);
						String user_id = json.getString("user_id");
						String user_name = json.getString("user_name");
						String token = json.getString("token");
						String token_secret = json.getString("token_secret");
						
						long account_id = AccountItem.insertAccount(ctx, null, user_name, "sohu", "1");
						
						if (account_id > 0)
						{
							if (SohuItem.insertSohuAccount(ctx, String.valueOf(account_id), user_id,token, token_secret, user_name))
							{
								Toast.makeText(ctx, "Insert account '" + user_name + "' (Sohu) SUCCESS!", Toast.LENGTH_LONG).show();
							}
							else
							{
								Toast.makeText(ctx, "Insert account '" + user_name + "' (Sohu) FAIL!", Toast.LENGTH_LONG).show();
							}
						}
					}
					catch (Exception e) 
					{
						e.printStackTrace();
						Log.e("webkit", "Sohu Service - " + e.toString());
					}
				}
				check=true;
				activity.finish();
			}
			
			
		}
	}
}
