package vn.mbm.phimp.me.services;

import java.io.File;
import java.net.URI;
import java.net.URLEncoder;

import oauth.signpost.OAuthConsumer;
import oauth.signpost.OAuthProvider;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthProvider;

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

import winterwell.jtwitter.OAuthSignpostClient;
import vn.mbm.phimp.me.R;
import android.util.Log;

public class TumblrServices 
{
	public static int icon = R.drawable.tumblr;
	public static String title = "Tumblr";
	
	public static final String CONSUMER_KEY = "xXORQTEkk4Yo7hn4hz67b4YtqqUD9YoZXJomOghDOtUyrx1SIm";
	public static final String CONSUMER_SECRET = "HzMqz2MnrfQLUU7k6bBXMgi5hF0V09fReTR6I1pfx0GO9LzOsD";
	
	public static final String URL_REQUEST_TOKEN = "http://www.tumblr.com/oauth/request_token";
	public static final String URL_ACCESS_TOKEN = "http://www.tumblr.com/oauth/access_token";
	public static final String URL_AUTHORIZE = "http://www.tumblr.com/oauth/authorize";
	
	public static final String CALLBACK_URL = "oauth://tumblr/";
	
	public static OAuthConsumer consumer = null;

	public static OAuthProvider provider = null;
	
	final static String TAG = "tumblr";
	
	public static final String tag_separate = ",";
	
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
				
				provider.setOAuth10a(true);
			}
			
		}
		catch (Exception e) 
		{
			e.printStackTrace();
			
			Log.e(TAG, "init() - " + e.toString());
		}
	}
	
	public static String oauthRequestToken()
	{
		String result = "";
		
		try
		{
			provider.setOAuth10a(true);
			
			String auth_url = provider.retrieveRequestToken(consumer, CALLBACK_URL);
			
			Log.d(TAG, "Authorize URL: " + auth_url);
			
			result = auth_url;
		}
		catch (Exception e) 
		{
			e.printStackTrace();
			
        	Log.e(TAG, "oauthRequestToken: - Exception ["+e.getMessage()+"]", e);
		}
		
		return result;
	}
	
	public static String oauthPutVerifierCode(String verifier_code)
	{
		String result = "";
		
		try
		{
			provider.retrieveAccessToken(consumer, verifier_code);
			
			String token = consumer.getToken();
			
			Log.d(TAG, "Token: " + token);
			
			String token_secret = consumer.getTokenSecret();
			
			Log.d(TAG, "Token Secret: " + token_secret);
			
			OAuthSignpostClient client = new OAuthSignpostClient(CONSUMER_KEY, CONSUMER_SECRET, token, token_secret);
			
			if (client.canAuthenticate())
			{
				Log.d(TAG, "Client can authenticate");
				
				HttpClient httpclient = new DefaultHttpClient();
				
				String url = "http://api.tumblr.com/v2/user/info";
				
				HttpGet httpget = new HttpGet(url);
				
				consumer.sign(httpget);
				
				ResponseHandler<String> res = new BasicResponseHandler();
				
				String httpResponse = httpclient.execute(httpget, res);
				
				Log.d(TAG, "Response: " + httpResponse);
				
				JSONObject json = new JSONObject(httpResponse);
				
				JSONObject json_meta = json.getJSONObject("meta");
				
				JSONObject json_response = json.getJSONObject("response");
				
				if (json_meta.getString("msg").equals("OK"))
				{
					JSONObject json_user = json_response.getJSONObject("user");
					
					String user_name = json_user.getString("name");
					
					String user_id = user_name;
					
					String link = "http://" + user_id + ".tumblr.com"; 
					
					JSONObject _user = new JSONObject();
					
					_user.put("token", consumer.getToken());
					_user.put("token_secret", consumer.getTokenSecret());
					_user.put("user_id", user_id);
					_user.put("user_name", user_name);
					_user.put("link", link);
					
					consumer = new CommonsHttpOAuthConsumer(CONSUMER_KEY, CONSUMER_SECRET);

					provider = new CommonsHttpOAuthProvider(URL_REQUEST_TOKEN, URL_ACCESS_TOKEN, URL_AUTHORIZE);
				
					provider.setOAuth10a(true);
					
					result = _user.toString();
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
			
        	Log.e(TAG, "oauthPutVerifierCode() - Exception ["+e.getMessage()+"]", e);
		}
		
		return result;
	}
	
	public static String uploadPhoto(String token, String token_secret, String title, String filepath, String base_hostname)
	{
		String result = "";
		
		try
		{
			String url = "http://api.tumblr.com/v2/blog/" + base_hostname + ".tumblr.com/post?type=photo&caption=" + URLEncoder.encode(title);
			
			Log.i(TAG, "Upload Photo URL: " + url);
			
			URI uri = new URI(url);
			
			HttpClient httpclient = new DefaultHttpClient();
			
			HttpPost httppost = new HttpPost(uri);
			
			consumer = new CommonsHttpOAuthConsumer(CONSUMER_KEY, CONSUMER_SECRET);
			
			consumer.setTokenWithSecret(token, token_secret);
			
			MultipartEntity multi = new MultipartEntity(HttpMultipartMode.STRICT);
			
			multi.addPart("type", new StringBody("photo"));
			multi.addPart("data", new FileBody(new File(filepath)));
			multi.addPart("caption", new StringBody(title));
			
			multi.consumeContent();
			
			Log.d(TAG, "Content Type: " + multi.getContentType().getName() + " - " + multi.getContentType().getValue());
			
			httppost.setEntity(multi);
							
			ResponseHandler<String> res = new BasicResponseHandler();
			
			consumer.sign(httppost);
			
			String httpResponse = httpclient.execute(httppost, res);
			
			Log.i(TAG, "Response from Upload: " + httpResponse);
			
			JSONObject json_meta = new JSONObject(httpResponse).getJSONObject("meta");
			
			if (json_meta.getInt("status") == 201)
			{
				result = httpResponse;
			}
			else
			{
				Log.e(TAG, "uploadPhoto() " + json_meta.getString("msg"));
			}
		}
		catch (Exception e) 
		{
			e.printStackTrace();
			
			Log.e(TAG, "uploadPhoto() - Exception ["+e.getMessage()+"]", e);
		}
		
		return result;
	}
}
