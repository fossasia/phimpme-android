package vn.mbm.phimp.me.services;

import java.io.IOException;
import java.net.URLEncoder;
import java.security.KeyStore;

import org.apache.http.HttpVersion;
import org.apache.http.client.ClientProtocolException;
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

import vn.mbm.phimp.me.utils.Commons.MySSLSocketFactory;
import vn.mbm.phimp.me.R;
import android.util.Log;


public class VKServices {
	public static String title  = "Vkontakte";
	public static int icon = R.drawable.vk;
	public static String application_id = "2951901";
	public static String application_secret = "fsNw1WFNPE0rNOhaeQJf";
	public static final String URL_REQUEST_API = "https://api.vk.com/method/";	
	public static final String URL_AUTHORIZE = "http://api.vk.com/oauth/authorize";
	public static final String Redirect_url = "http://api.vk.com/blank.html";	
	
	public static String getAuthorzingUrl(){
		return URL_AUTHORIZE+"?client_id="+application_id+"&scope=6&redirect_uri="+Redirect_url+"&response_type=token";
	}
	public static String getUserInfo(String access_token,String uid){
		
		String url = URL_REQUEST_API+"getProfiles?uid="+uid+"&access_token="+access_token;
		String user ="";
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
			JSONObject resp = new JSONObject(response);
			user = resp.getString("response").replace("[", "").replace("]", "");			
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return user;
		
	}
	public static String createAlbums(String access_token){
		String aid = "";
		String url = URL_REQUEST_API+"photos.createAlbum?title=phimp.me&privacy=0&comment_privacy=0&description=phimp.me_albums&access_token="+access_token;
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
			JSONObject json1 = new JSONObject(json.getString("response").replace("[{", "").replace("]}", ""));
			aid = json1.getString("aid");			
    	}catch(Exception e){
    		e.printStackTrace();
    	}    
		return aid;
	}
	public static String getPhotoUploadUrl(String access_token, String uid){
		String userurl = "";
		String url = URL_REQUEST_API+"photos.getUploadServer?aid="+uid+"&access_token="+access_token;
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
			JSONObject json1 = new JSONObject(json.getString("response"));
			userurl = json1.getString("upload_url");
			
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return userurl;
	}
	public static boolean savePhoto(String server, String photo, String hash,String aid,String access_token){		
		String url = URL_REQUEST_API+"photos.save?aid="+URLEncoder.encode(aid)+"&server="+server+"&photos_list="+URLEncoder.encode(photo)+"&hash="+URLEncoder.encode(hash)+"&access_token="+URLEncoder.encode(access_token);
		Log.d("uri",url);		
		boolean result = false;
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
    	HttpGet get = new HttpGet(url);		
    	ResponseHandler<String> res = new BasicResponseHandler();
    	try {
			String response = httpClient.execute(get, res);
			Log.d("reponse save",response);					
			try{
				JSONObject json = new JSONObject(response);
				@SuppressWarnings("unused")
				String error = json.getString("error");				
			}catch(JSONException e)
			{
				result = true;
			}
			
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return result;
	}
	
	public static String[] getAlbumList(String access_token){
		String aid_list[] = null;
		String url = URL_REQUEST_API+"photos.getAlbums?&access_token="+access_token;
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
	
}
