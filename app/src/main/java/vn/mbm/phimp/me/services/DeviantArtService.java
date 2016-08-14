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
import org.json.JSONException;
import org.json.JSONObject;
import org.scribe.oauth.OAuth20ServiceImpl;

import vn.mbm.phimp.me.utils.Commons.MySSLSocketFactory;
import vn.mbm.phimp.me.R;
import android.util.Log;

public class DeviantArtService 
{
	public static int icon = R.drawable.devart;
	public static String title = "DeviantART";

	public static final String client_id = "183";
	public static final String client_secret = "eb7b8980680c9a803dbc74aeea22d71c";

	public static final String CALLBACK_URL ="http://phimp.me/deviantart";
	
	public static final String CALLBACK_HOST = "/deviantart";



	final static String TAG = "deviantart";
	
	public static OAuth20ServiceImpl service;
			
	public static String getAuthenticateCode()
	{
		try
		{

			String url = "https://www.deviantart.com/oauth2/draft15/authorize?client_id=" + client_id 
				+ "&redirect_uri=" + URLEncoder.encode(CALLBACK_URL)+ "&response_type=code";
			
			Log.d(TAG, "getAuthenticateLink deviant art: " + url);
			
			return url;
		}
		catch (Exception e) 
		{
			Log.e(TAG, e.toString());
			
			e.printStackTrace();
			
			return "";
		}
	}
	
	public static String getAccessToken(String code)
	{
		String response ="";
		
			String url = "https://www.deviantart.com/oauth2/draft15/token?grant_type=authorization_code&client_id=" + client_id 
				 +"&client_secret="+client_secret + "&code="+ code  + "&response_type=token";
						
			Log.d(TAG, "getAuthenticateLink: " + url);
			try{
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
	    	
	    	response = httpClient.execute(get, res);			
	    	
	    	Log.d(TAG, "Response: " + response);
			}catch(Exception e)
			{
				e.printStackTrace();
			}
		return response;				
	}	
	
	public static String getUserInfo(String access_token)
	{
		String result = "";
		
		try
		{
			String url = "https://www.deviantart.com/api/draft15/user/whoami";
			
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
	    		String user_name = json.getString("username");	    		
	    		String link = json.getString("usericonurl");	    		
	    		
	    		JSONObject js = new JSONObject();	    		
	    		js.put("user_name", user_name);	    		
	    		js.put("link", link);	    			    		
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
	public static String RefreshToken(String refresh_token){
		String response ="";
		
		String url = "https://www.deviantart.com/oauth2/draft15/token?grant_type=refresh_token&client_id=" + client_id 
			 +"&client_secret="+client_secret   + "&refresh_token="+refresh_token;
			
		
		Log.d(TAG, "getAuthenticateLink: " + url);
		try{
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
    	
    	response = httpClient.execute(get, res);			
    	
    	Log.d(TAG, "Response: " + response);
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	return response;	
	}
}
