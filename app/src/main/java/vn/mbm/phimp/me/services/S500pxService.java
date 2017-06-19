package vn.mbm.phimp.me.services;

import java.security.KeyStore;

import oauth.signpost.OAuthConsumer;
import oauth.signpost.OAuthProvider;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthProvider;

import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONObject;

import vn.mbm.phimp.me.utils.Commons.MySSLSocketFactory;
import winterwell.jtwitter.OAuthSignpostClient;
import vn.mbm.phimp.me.R;
import android.content.Context;
import android.util.Log;

public class S500pxService {
	public static int icon = R.drawable.s500px;
	public static String title = "500px";
	
	static Context ctx;	

	public static final String CONSUMER_KEY = "PVY1KlhFEHijAMvRL4d8l2SZoSPGyfnSDPIjxj1C";
	public static final String CONSUMER_SECRET = "U30Fa39arlGmfKjPazBXifHkqXP5YJpRyC6hDYWg";
	
	public static final String URL_REQUEST_TOKEN = "https://api.500px.com/v1/oauth/request_token";
	public static final String URL_ACCESS_TOKEN = "https://api.500px.com/v1/oauth/access_token";
	public static final String URL_AUTHORIZE = "https://api.500px.com/v1/oauth/authorize";
	
	public static final String CALLBACK_URL = "oauth://500px/";
	
	final static String TAG = "500px";
	
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
			
			String auth_url = provider.retrieveRequestToken(consumer,CALLBACK_URL );
			
			Log.d("500px", "Authorize URL:" + auth_url);
			
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
				
				//String url = "https://api.twitter.com/1/account/verify_credentials.json?skip_status=true&include_entities=true";
				String url= "https://api.500px.com/v1/users";
				
				HttpGet httpget = new HttpGet(url);
				
				consumer.sign(httpget);
				
				ResponseHandler<String> res = new BasicResponseHandler();
				
				String httpResponse = httpclient.execute(httpget, res);
				
				Log.d(TAG, "Response: " + httpResponse);
				
				JSONObject json = new JSONObject(httpResponse);
				
				
				JSONObject user = new JSONObject(json.getString("user"));
				String link = user.getString("domain");
				
							
				user.put("user_name", user.getString("username"));
				user.put("user_fullname", user.getString("fullname"));
				user.put("user_id", user.getString("id"));
				user.put("profile_url", link);
				user.put("token", consumer.getToken());
				user.put("token_secret", consumer.getTokenSecret());
				
				result = user.toString();	
				Log.d("json moi ne=>>>",result);
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
	public static String getUser(String access_token, String uid){
		String userurl = "";
		String url="https://api.500px.com/v1/users";
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
	public static String[] getAlbumList(){
		String aid_list[] = null;
		String url="https://api.500px.com/v1/photos?feature=editors&page=2&consumer_key="+CONSUMER_KEY ;
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
			JSONObject json = new JSONObject(response);
			JSONArray arr_json = json.getJSONArray("response");
			String aid[] = new String[arr_json.length()];
			for(int i=0; i< arr_json.length(); i++){
				JSONObject json1 = new JSONObject(arr_json.getString(i));
				aid[i] = json1.getString("aid");
			}
			Log.d("aid", aid[0].toString());
			aid_list=aid;
			
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return aid_list;
	}
	public static String getUploadkeyandId(){
		String userurl = "";
		String url="https://api.500px.com/v1/photos?";
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
