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

public class ImgurServices {
	public static int icon = R.drawable.imgur;
	public static String title = "Imgur";
	
	public static final String CONSUMER_KEY = "f237ba472fe690b0ed430ed28ebb6b6a04fd5a018";
	public static final String CONSUMER_SECRET = "d741aba89822a81e8e397ef34e1e20e7";
	
	public static final String REQUEST_URL = "http://api.imgur.com/oauth/request_token";
	public static final String ACCESS_URL = "http://api.imgur.com/oauth/access_token";
	public static final String AUTHORIZE_URL = "http://api.imgur.com/oauth/authorize";
	
	public static final String	OAUTH_CALLBACK_URL		= "oauthflow-imgur://imgur";
	
	static final String TAG = "Imgur";
		

	private static CommonsHttpOAuthConsumer consumer = new CommonsHttpOAuthConsumer(CONSUMER_KEY, CONSUMER_SECRET);
	private static CommonsHttpOAuthProvider provider = new CommonsHttpOAuthProvider(REQUEST_URL, ACCESS_URL, AUTHORIZE_URL);
	
	
	public static String OAuthRequestToken()
	{
		String result = "";
		
		try
		{
			provider.setOAuth10a(true);
			String auth_url = provider.retrieveRequestToken(consumer, OAUTH_CALLBACK_URL);		
			Log.d("Imgur", "Authorize URL: " + auth_url);
			
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
				
				String url = "http://api.imgur.com/2/account.json";
				
				HttpGet httpget = new HttpGet(url);
				
				consumer.sign(httpget);
				
				ResponseHandler<String> res = new BasicResponseHandler();				
				String httpResponse = httpclient.execute(httpget, res);				
				Log.d(TAG, "Response: " + httpResponse);
				
				JSONObject json = new JSONObject(httpResponse);
				JSONObject json2=new JSONObject(json.getString("account"));			
				JSONObject user = new JSONObject();
				
				user.put("user_name", json2.getString("url"));
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
	
	public static String getPersonalPhotos(String token, String token_secret)
	{
		String response = null;
		
		try
		{
			String url = "http://api.imgur.com/2/account/images.json";
		
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
			
			
			Log.d("Imgur","Imgur getphoto response : "+ response);
		}
		catch (Exception e) 
		{
			e.printStackTrace();
			
			Log.e(TAG, "Imgur getphoto error: " + e.toString());

		}
		
		return response;
	}
}
