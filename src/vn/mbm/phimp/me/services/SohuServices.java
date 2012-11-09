package vn.mbm.phimp.me.services;

import java.net.URI;

import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthProvider;
import oauth.signpost.signature.HmacSha1MessageSigner;
import oauth.signpost.signature.OAuthMessageSigner;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import winterwell.jtwitter.OAuthSignpostClient;
import vn.mbm.phimp.me.R;
import android.util.Log;

public class SohuServices {
	public static int icon = R.drawable.sohu;
	public static String title = "Sohu";
	
	public static final String URL_REQUEST_TOKEN = "http://api.t.sohu.com/oauth/request_token";
	public static final String URL_ACCESS_TOKEN = "http://api.t.sohu.com/oauth/access_token";
	public static final String URL_AUTHORIZE = "http://api.t.sohu.com/oauth/authorize";
	
	public static final String CONSUMER_KEY = "ODZcdzyaU3YbgknkocYz";
	public static final String CONSUMER_SECRET = "qI*6Om^MIuf9tsuIqRPe-gck$c5noBS1VtWv3Tbq";
	public static final String CALLBACK_URL = "oauth://sohu/";
	
	final static String TAG = "Sohu";
	
	private static CommonsHttpOAuthConsumer consumer = new CommonsHttpOAuthConsumer(CONSUMER_KEY, CONSUMER_SECRET);
	private static CommonsHttpOAuthProvider provider = new CommonsHttpOAuthProvider(URL_REQUEST_TOKEN, URL_ACCESS_TOKEN, URL_AUTHORIZE);
	
	public static String OAuthRequestToken()
	{
		String result = "";
		
		try
		{
			provider.setOAuth10a(true);
			String auth_url = provider.retrieveRequestToken(consumer, CALLBACK_URL);		
			Log.d("Sohu", "Authorize URL: " + auth_url);
			
			result = auth_url;
		}
		catch (Exception e) 
		{
			Log.e(TAG, "error2 :"+e.toString());
			
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
				
				String url = "http://api.t.sohu.com/users/show.json";
				
				HttpGet httpget = new HttpGet(url);
				
				consumer.sign(httpget);
				
				ResponseHandler<String> res = new BasicResponseHandler();				
				String httpResponse = httpclient.execute(httpget, res);				
				Log.d(TAG, "Response: " + httpResponse);
				
				JSONObject json = new JSONObject(httpResponse);						
				JSONObject user = new JSONObject();
				
				user.put("user_id", json.getString("id"));
				user.put("user_name", json.getString("screen_name"));
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
			Log.e(TAG, "error :"+e.toString());
        	
			e.printStackTrace();
		}
		
		return result;
	}
	public static String getPersonalPhotos(String token, String token_secret,String user_id)
	{
		String response = null;
		
		try
		{
			String url = "http://api.t.sohu.com/statuses/user_timeline/"+user_id+".json";
			Log.d("Sohu services", "url : "+url);
			URI uri = new URI(url);
			
			HttpClient httpclient = new DefaultHttpClient();
			
			HttpGet httpget = new HttpGet(uri);
			
			consumer = new CommonsHttpOAuthConsumer(CONSUMER_KEY, CONSUMER_SECRET);
			
			OAuthMessageSigner oms = new HmacSha1MessageSigner();
			
			consumer.setMessageSigner(oms);
			
			consumer.setTokenWithSecret(token, token_secret);
			
			ResponseHandler<String> res = new BasicResponseHandler();
			
			consumer.sign(httpget);
			
		    response = httpclient.execute(httpget, res);
			
			
			Log.d("Sohu","Sohu getphoto response : "+ response);
		}
		catch (Exception e) 
		{
			e.printStackTrace();
			
			Log.e(TAG, "Sohu getphoto error: " + e.toString());

		}
		
		return response;
	}
	
}
