package vn.mbm.phimp.me.services;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.net.URI;
import java.net.URLEncoder;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import oauth.signpost.OAuthConsumer;
import oauth.signpost.OAuthProvider;
import oauth.signpost.basic.DefaultOAuthProvider;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthProvider;
import oauth.signpost.http.HttpParameters;
import oauth.signpost.signature.HmacSha1MessageSigner;
import oauth.signpost.signature.OAuthMessageSigner;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import winterwell.jtwitter.OAuthSignpostClient;
import vn.mbm.phimp.me.R;
import android.util.Log;

public class FlickrServices 
{
	public static int icon = R.drawable.flickr;
	public static String title = "Flickr";
	
	public static final String URL_REQUEST_TOKEN = "http://www.flickr.com/services/oauth/request_token";
	public static final String URL_ACCESS_TOKEN = "http://www.flickr.com/services/oauth/access_token";
	public static final String URL_AUTHORIZE = "http://www.flickr.com/services/oauth/authorize";
	
	public static final String CONSUMER_KEY = "64880e486331b93765e3949a7c8b6207";
	public static final String CONSUMER_SECRET = "493328c917262ca2";
	public static final String CALLBACK_URL = "oauth://flickr/";
	
	final static String TAG = "flickr";
	
	public static OAuthConsumer consumer = null;
	public static OAuthProvider provider = null;
	
	public static String upload_result = "";
	
	public static String upload_token, upload_token_secret, upload_title, upload_filepath, upload_lat, upload_lon;
	
	public static final String tag_separate = " ";
	
	public static void init()
	{
		try
		{
			if (consumer == null)
			{
				consumer = new CommonsHttpOAuthConsumer(CONSUMER_KEY, CONSUMER_SECRET);
			}
			
			if (provider == null)
			{
				provider = new CommonsHttpOAuthProvider(URL_REQUEST_TOKEN, URL_ACCESS_TOKEN, URL_AUTHORIZE);
				
			}
			
		}
		catch (Exception e) 
		{
			e.printStackTrace();
			
			Log.e(TAG, "init() - " + e.toString());
		}
	}
	
	public static String OAuthRequestToken()
	{
		String result = "";
		
		try
		{
			String auth_url = provider.retrieveRequestToken(consumer, CALLBACK_URL) + "&perms=write";
			
			Log.d(TAG, "Authorize URL: " + auth_url);
			
			result = auth_url;
		}
		catch (Exception e) 
		{
			e.printStackTrace();
			
        	Log.e("flickr","OAuthRequestToken - Exception ["+e.getMessage()+"]", e);
		}
		
		return result;
	}
	
	public static String OAuthPutVerifierCode(String verifier_code)
	{
		String result = "";
		
		try
		{
			Log.d(TAG, "Verifier Code: " + verifier_code);
			
			provider.retrieveAccessToken(consumer, verifier_code);
			
			String token = consumer.getToken();
			
			Log.d("flickr", "Token: " + token);
			
			String token_secret = consumer.getTokenSecret();
			
			Log.d("flickr", "Token Secret: " + token_secret);
			
			OAuthSignpostClient client = new OAuthSignpostClient(CONSUMER_KEY, CONSUMER_SECRET, token, token_secret);
			
			if (client.canAuthenticate())
			{
				Log.d("flickr", "Client can authenticate");
				
				HttpClient httpclient = new DefaultHttpClient();
				
				String url = "http://api.flickr.com/services/rest/?method=flickr.test.login&nojsoncallback=1&format=json";
				
				HttpGet httpget = new HttpGet(url);
				
				consumer.sign(httpget);
				
				ResponseHandler<String> res = new BasicResponseHandler();
				
				String httpResponse = httpclient.execute(httpget, res);
				
				Log.d("flickr", "Response: " + httpResponse);
				
				JSONObject json = new JSONObject(httpResponse);
				
				Log.d("flickr", "Stat: " + json.getString("stat"));
				
				if (json.getString("stat").equals("ok"))
				{
					String user_id = json.getJSONObject("user").getString("id");
					
					Log.d("flickr", "User ID: " + user_id);
					
					String user_name = json.getJSONObject("user").getJSONObject("username").getString("_content");
					
					Log.d("flickr", "User Name: " + user_name);
					
					JSONObject _user = new JSONObject();
					
					_user.put("token", consumer.getToken());
					_user.put("token_secret", consumer.getTokenSecret());
					_user.put("user_id", user_id);
					_user.put("user_name", user_name);
					_user.put("profile_url", "http://www.flickr.com/photos/" + user_id);
					
					consumer = new CommonsHttpOAuthConsumer(CONSUMER_KEY, CONSUMER_SECRET);

					provider = new DefaultOAuthProvider(URL_REQUEST_TOKEN, URL_ACCESS_TOKEN, URL_AUTHORIZE);
					
					result = _user.toString();
				}
				else
				{
					String error_code = json.getString("code");
					
					String error_message = json.getString("message");
					
					Log.e(TAG, "OAuthPutVerifierCode: (" + error_code + ") " + error_message);
				}
			}
			else
			{
				Log.w(TAG, "Client can not authenticate");
			}
		}
		catch (Exception e) 
		{
			e.printStackTrace();
			
        	Log.e(TAG, "OAuthPutVerifierCode - Exception ["+e.getMessage()+"]", e);
		}
		
		return result;
	}
	
	public static String UploadPhoto(String token, String token_secret, String title, String filepath, String lat, String lon)
	{
		String result = "";
		
		try
		{
			String url = "http://api.flickr.com/services/upload/";

			url += "?title=" + URLEncoder.encode(title);
			
			URI uri = new URI(url);
			
			HttpClient httpclient = new DefaultHttpClient();
			
			HttpPost httppost = new HttpPost(uri);
			
			consumer = new CommonsHttpOAuthConsumer(CONSUMER_KEY, CONSUMER_SECRET);
			
			HttpParameters hp = new HttpParameters();
			hp.put("title", title);
			
			consumer.setAdditionalParameters(hp);
			
			OAuthMessageSigner oms = new HmacSha1MessageSigner();
			
			consumer.setMessageSigner(oms);
			
			consumer.setTokenWithSecret(token, token_secret);
			
			MultipartEntity multi = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
			
			multi.addPart("title", new StringBody(title));
			multi.addPart("photo", new FileBody(new File(filepath)));
			
			multi.consumeContent();
			
			httppost.setEntity(multi);
			
			ResponseHandler<String> res = new BasicResponseHandler();
			
			consumer.sign(httppost);
			
			String response = httpclient.execute(httppost, res);
			
			/*
			 * Add geolocation infor for photos
			 */
			if ((!lat.equals("")) && (!lon.equals("")))
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
					
					consumer = new CommonsHttpOAuthConsumer(CONSUMER_KEY, CONSUMER_SECRET);
					
					HttpParameters hp2 = new HttpParameters();
					hp2.put("photo_id", photoid);
					hp2.put("lat", lat);
					hp2.put("lon", lon);
					
					consumer.setAdditionalParameters(hp2);
					
					oms = new HmacSha1MessageSigner();
					
					consumer.setMessageSigner(oms);
					
					consumer.setTokenWithSecret(token, token_secret);
					
					multi = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
					
					multi.addPart("photo_id", new StringBody(photoid));
					multi.addPart("lat", new StringBody(lat));
					multi.addPart("lon", new StringBody(lon));
					
					multi.consumeContent();
					
					httppost.setEntity(multi);
					
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
			
			/*
			 * Add geolocation infor for photos - Done
			 */
			
			result = "1|" + response;
		}
		catch (Exception e) 
		{
			e.printStackTrace();
			
			Log.e(TAG, "UploadPhoto: " + e.toString());
			
			result = "0|" + e.toString();
		}
		
		return result;
	}
	
	public static void UploadPhotoAsync(String token, String token_secret, String title, String filepath, String lat, String lon)
	{
		upload_token = token;
		upload_token_secret = token_secret;
		upload_title = title;
		upload_filepath = filepath;
		upload_lat = lat;
		upload_lon = lon;
	}
	public static String getPhotos(String token, String token_secret, String uid)
	{
		String response = null;
		
		try
		{
			String url = "http://api.flickr.com/services/rest/?method=flickr.people.getPhotos";

			url += "&api_key=" + CONSUMER_KEY;
			url += "&user_id=" + uid;
			url += "&nojsoncallback=1&format=json";
			
			URI uri = new URI(url);
			
			HttpClient httpclient = new DefaultHttpClient();
			
			HttpPost httppost = new HttpPost(uri);
			
			consumer = new CommonsHttpOAuthConsumer(CONSUMER_KEY, CONSUMER_SECRET);
			
			OAuthMessageSigner oms = new HmacSha1MessageSigner();
			
			consumer.setMessageSigner(oms);
			
			consumer.setTokenWithSecret(token, token_secret);
			
			ResponseHandler<String> res = new BasicResponseHandler();
			
			consumer.sign(httppost);
			
		    response = httpclient.execute(httppost, res);
			
			
			Log.d("flickr", response);
		}
		catch (Exception e) 
		{
			e.printStackTrace();
			
			Log.e(TAG, "UploadPhoto: " + e.toString());

		}
		
		return response;
	}
}
