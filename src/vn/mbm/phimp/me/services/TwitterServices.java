package vn.mbm.phimp.me.services;

import java.io.File;
import java.net.URI;
import java.security.KeyStore;

import oauth.signpost.OAuthConsumer;
import oauth.signpost.OAuthProvider;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthProvider;

import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONObject;

import vn.mbm.phimp.me.utils.Commons.MySSLSocketFactory;
import winterwell.jtwitter.OAuthSignpostClient;
import vn.mbm.phimp.me.R;
import android.util.Log;

public class TwitterServices 
{
	public static int icon = R.drawable.twitter;
	public static String title = "Twitter";
	
	public static final String CONSUMER_KEY = "toro9gZ33Li6LOGsB7pRPQ";//"mMT7n87RNG5l7E5LuxCbiQ";
	public static final String CONSUMER_SECRET = "toro9gZ33Li6LOGsB7pRPQ";//"4bYILcSZr5F4NusiF9hQoJyVCqmLOLIQOYVkMiuFVb4";
	
	public static final String URL_REQUEST_TOKEN = "https://api.twitter.com/oauth/request_token";
	public static final String URL_ACCESS_TOKEN = "https://api.twitter.com/oauth/access_token";
	public static final String URL_AUTHORIZE = "https://api.twitter.com/oauth/authorize";
	
	public static final String CALLBACK_URL = "oauth://twitter/";
	
	static final String TAG = "twitter";
	
	public static OAuthConsumer consumer = null;
	public static OAuthProvider provider = null;
	
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
			
			Log.e(TAG, e.toString());
		}
	}
	
	public static String OAuthRequestToken()
	{
		String result = "";
		
		try
		{
			provider.setOAuth10a(true);
			
			String auth_url = provider.retrieveRequestToken(consumer, CALLBACK_URL);
			
			Log.d("twitter", "Authorize URL: " + auth_url);
			
			result = auth_url;
		}
		catch (Exception e) 
		{
			Log.e(TAG, e.toString());
			
			e.printStackTrace();
		}
		
		return result;
	}
	
	public static String OAuthPutVerifierCode(String verifier_code)
	{
		String result = "";
		
		try
		{
			provider.retrieveAccessToken(consumer, verifier_code);
			
			String token = consumer.getToken();
			
			String token_secret = consumer.getTokenSecret();
			
			OAuthSignpostClient client = new OAuthSignpostClient(CONSUMER_KEY, CONSUMER_SECRET, token, token_secret);
			
			if (client.canAuthenticate())
			{
				HttpClient httpclient = new DefaultHttpClient();
				
				String url = "https://api.twitter.com/1/account/verify_credentials.json?skip_status=true&include_entities=true";
				
				HttpGet httpget = new HttpGet(url);
				
				consumer.sign(httpget);
				
				ResponseHandler<String> res = new BasicResponseHandler();
				
				String httpResponse = httpclient.execute(httpget, res);
				
				Log.d(TAG, "Response: " + httpResponse);
				
				JSONObject json = new JSONObject(httpResponse);
				
				String link = "http://twitter.com/#!/" + json.getString("screen_name");
				
				JSONObject user = new JSONObject();
				
				user.put("user_name", json.getString("screen_name"));
				user.put("user_fullname", json.getString("name"));
				user.put("user_id", json.getString("id_str"));
				user.put("profile_url", link);
				user.put("token", consumer.getToken());
				user.put("token_secret", consumer.getTokenSecret());
				
				result = user.toString();
			}
			else
			{
				Log.w(TAG, "Client can not authenticate");
			}
		}
		catch (Exception e) 
		{
			Log.e(TAG, e.toString());
        	
			e.printStackTrace();
		}
		
		return result;
	}
	
	public static String UploadPhoto(String token, String token_secret, String filepath, String title, String latitude, String longitude)
	{
		String result = "";
		
		try
		{
			String url = "https://upload.twitter.com/1/statuses/update_with_media.json";
			
			Log.i(TAG, "Upload Photo URL: " + url);
			
			URI uri = new URI(url);
			
			HttpClient httpclient = new DefaultHttpClient();
			
			HttpPost httppost = new HttpPost(uri);
			
			MultipartEntity multi = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
			
			multi.addPart("media[]", new FileBody(new File(filepath)));
			
			httppost.setEntity(multi);
			
			consumer = new CommonsHttpOAuthConsumer(CONSUMER_KEY, CONSUMER_SECRET);
			
			consumer.setTokenWithSecret(token, token_secret);

			consumer.sign(httppost);
							
			ResponseHandler<String> res = new BasicResponseHandler();
			
			String httpResponse = httpclient.execute(httppost, res);
			
			Log.i(TAG, "Response from Upload: " + httpResponse);
			
			JSONObject json = new JSONObject(httpResponse);
			
			if (json.getString("id_str") != null)
			{
				result = httpResponse;
			}
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		
		return result;
	}
	
	public static String getUser(String access_token, String uid){
		String userurl = "";
		String url = "https://api.twitter.com/1/statuses/user_timeline.json?screen_name=" + CONSUMER_KEY + "&include_entities=true";
		Log.d("url",url);
		HttpGet get = new HttpGet(url);
		
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
    	
    	ResponseHandler<String> res = new BasicResponseHandler();

    	try {
			String response = httpClient.execute(get, res);
			Log.d("reponse",response);
			
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return userurl;
	}
}
