package vn.mbm.phimp.me.services;

import java.net.URLEncoder;
import java.security.KeyStore;

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
import org.json.JSONException;
import org.json.JSONObject;
import org.scribe.oauth.OAuth20ServiceImpl;

import vn.mbm.phimp.me.utils.Commons.MySSLSocketFactory;
import vn.mbm.phimp.me.R;
import android.content.Context;
import android.util.Log;

public class FacebookServices 
{
	public static int icon = R.drawable.facebook;
	public static String title = "Facebook";
	/*
	 * api facebook of Hon register
	 * public static final String API_KEY = "254521501274154";
	 * public static final String API_SECRET = "a316f71f53c2597ea994f4a22215d07e"
	 * public static final String CALLBACK_URL = "http://facebook.mbm.vn/";
	 * public static final String CALLBACK_HOST = "mbm.vn";
	 */
	public static final String API_KEY = "360102914062791";
	public static final String API_SECRET = "1bc98ee6574acf5a9cb6e5ba7797b69d";
	
	public static final String CALLBACK_URL = "http://phimp.me";
	public static final String CALLBACK_HOST = "phimp.me";
	
	final static String TAG = "facebook";
	static Context ctx;
	public static OAuth20ServiceImpl service;
	
	public static String getAuthenticateLink()
	{
		try
		{
			String url = "https://www.facebook.com/dialog/oauth?client_id=" + API_KEY 
				+ "&redirect_uri=" + URLEncoder.encode(CALLBACK_URL)
					+ "&scope=" + URLEncoder.encode("email,read_stream,publish_stream,user_photos,read_requests,read_insights") 
					+ "&response_type=token";
			
			Log.d(TAG, "getAuthenticateLink: " + url);
			
			return url;
		}
		catch (Exception e) 
		{
			Log.e(TAG, e.toString());
			
			e.printStackTrace();
			
			return "";
		}
	}
	
	public static String getUserInfo(String access_token)
	{
		String result = "";
		
		try
		{
			String url = "https://graph.facebook.com/me/";
			
			url += "?access_token=" + URLEncoder.encode(access_token);
			
			Log.i(TAG, "URL: " + url);
			
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
	    	
	    	String response = httpClient.execute(get, res);
	    	
	    	Log.d(TAG, "Response: " + response);
	    	
	    	JSONObject json = new JSONObject(response);
	    	
	    	try
	    	{
	    		JSONObject json_err = json.getJSONObject("error");
	    		
	    		String msg = json_err.getString("message");
	    		
	    		result = msg;
	    	}
	    	catch (JSONException jse) 
	    	{
	    		String user_id = json.getString("id");
	    		String user_name = json.getString("first_name");
	    		String user_fullname = json.getString("name");
	    		String link = json.getString("link");
	    		String email = json.getString("email");
	    		
	    		JSONObject js = new JSONObject();
	    		js.put("user_id", user_id);
	    		js.put("user_name", user_name);
	    		js.put("fullname", user_fullname);
	    		js.put("link", link);
	    		js.put("email", email);
	    		
	    		result = js.toString();
			}
		}
		catch (Exception e) 
		{
			Log.e(TAG, e.toString());
			
			e.printStackTrace();
			
			result = "";
		}
		
		return result;
	}
	public static String change_access_token(String access_token)
	{
		String result = "";
		String access_token_change="";
		try
		{
			String url = "https://graph.facebook.com/oauth/access_token?";            
			url+="&client_id="+API_KEY;
			url+="&client_secret="+API_SECRET;
			url+="&grant_type=fb_exchange_token";
			url+="&fb_exchange_token="+URLEncoder.encode(access_token);
			
					
			Log.i(TAG, "URL: " + url);
			
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
	    	
	    	String response = httpClient.execute(get, res);
	    	
	    	Log.d(TAG, "Response: " + response);
	    	String []tmp1=response.split("&");
	    	access_token_change=tmp1[0].replace("access_token=", "");
	    	
	    	Log.d("webkit", "Access_token: " + access_token_change);
	    	return access_token_change;
	    	
		}
		catch (Exception e) 
		{
			Log.e(TAG, e.toString());
			
			e.printStackTrace();
			
			result = "";
		}
		
		return result;
	}
	
	public static String[] getAlbumList(String access_token, String uid)
	{
		String aid_list[] = null;
		String url = "https://graph.facebook.com/";
		url += uid;
		url += "/albums";
		url += "?access_token="+ access_token;
		Log.d("getAlbum url",url);
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
    			JSONArray arr_json = json.getJSONArray("data");
    			String aid[] = new String[arr_json.length()];
    			for(int i=0; i< arr_json.length(); i++){
    				JSONObject _json = arr_json.getJSONObject(i);
    				aid[i] = _json.getString("id");
    			}
    			aid_list=aid;
				
				}
			catch (Exception e) {
				e.printStackTrace();
			}	
    		
    	return aid_list;
	}
	
}
