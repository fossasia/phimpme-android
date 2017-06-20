package vn.mbm.phimp.me.services;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.security.KeyStore;

import org.apache.http.HttpVersion;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.FileEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;
import org.scribe.oauth.OAuth20ServiceImpl;

import vn.mbm.phimp.me.utils.Commons;
import vn.mbm.phimp.me.utils.Commons.MySSLSocketFactory;
import vn.mbm.phimp.me.R;
import android.util.Log;


public class PicasaServices 
{
	public static int icon = R.drawable.picasa;
	public static String title = "Picasa";
	
	public static final String API_KEY = "246137449925-5ci9pge3t3u5l0inn8to1fgghr8e16v2.apps.googleusercontent.com";
	public static final String API_SECRET = "ZqRoHwo8f1P613Kz4h68nLwO";
	
	public static final String CALLBACK_URL = "http://phimp.me/picasa";
	
	public static final String CALLBACK_HOST = "/picasa";
	
	final static String TAG = "picasa";
	
	public static OAuth20ServiceImpl service;
	
	public static String OAuthGetAuthenticateLink()
	{
		String result = "";
		try
		{
			String scope="https://www.googleapis.com/auth/userinfo.email ";
			scope += "https://www.googleapis.com/auth/userinfo.profile ";
			scope += "https://www.googleapis.com/auth/plus.me ";
			scope += "https://picasaweb.google.com/data/";
			
			String url = "https://accounts.google.com/o/oauth2/auth?client_id=" + API_KEY 
					+ "&redirect_uri=" + URLEncoder.encode(CALLBACK_URL) 
					+ "&scope=" + URLEncoder.encode(scope) + "&response_type=code&access_type=offline&approval_prompt=force";
			
			Log.d(TAG, "OAuthGetAuthenticateLink: " + url);
			
			result = url;
		}
		catch (Exception e) 
		{
			Log.e(TAG, e.toString());
			
			e.printStackTrace();
		}
		return result;
	}
	
	public static String OAuthGetAccessToken(String code, String type)
	{
		String result = "";
		try
		{
			String url = "https://accounts.google.com/o/oauth2/token";
			
			HttpClient client = new DefaultHttpClient();
			
			HttpPost post = new HttpPost(url);
			
			MultipartEntity multi = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
			
			multi.addPart("client_id", new StringBody(API_KEY));
			
			multi.addPart("client_secret", new StringBody(API_SECRET));
			
			if (type.equals("code"))
			{
				//Get code for first time
				multi.addPart("code", new StringBody(code));
				multi.addPart("redirect_uri", new StringBody(CALLBACK_URL));
				multi.addPart("grant_type", new StringBody("authorization_code"));
			}
			else
			{
				multi.addPart("refresh_token", new StringBody(code));
				multi.addPart("grant_type", new StringBody("refresh_token"));
			}
			
			post.setEntity(multi);
			
			ResponseHandler<String> handler = new BasicResponseHandler();
			
			String response = client.execute(post, handler);
			
			Log.d(TAG, response);
			
			result = response;
		}
		catch (Exception e) 
		{
			Log.e(TAG, e.toString());
			
			e.printStackTrace();
		}
		return result;
	}
	
	public static String GetUserInfo(String access_token, String token_type)
	{
		/*
		 * Using Web-server with offline mode
		 * https://code.google.com/apis/accounts/docs/OAuth2WebServer.html
		 * 
		*/
		
		String result = "";
		
		try
		{
			String url = "https://www.googleapis.com/oauth2/v1/userinfo";
			
			url += "?access_token=" + URLEncoder.encode(access_token);
			
			Log.i(TAG, "URL: " + url);
			
			HttpGet get = new HttpGet(url);
			
			HttpClient httpClient = null;
	    	try 
		    {
	    		KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
		        trustStore.load(null, null);
		
		        SSLSocketFactory sf = new Commons.MySSLSocketFactory(trustStore);
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
	    	
	    	get.addHeader("Authorization", token_type + " " + access_token);
	    	
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
	    		result = response;
			}
		}
		catch (Exception e) 
		{
			Log.e(TAG, e.toString());
			
			e.printStackTrace();
		}
		
		return result;
	}
	
	public static String GetRefreshToken(String token, String refresh_token)
	{
		String result = "";
		
		try
		{
			HttpClient client = null;
			
			HttpPost httppost = null;
			
			try 
		    {
	    		KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
		        trustStore.load(null, null);
		
		        SSLSocketFactory sf = new Commons.MySSLSocketFactory(trustStore);
		        sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
		
		        HttpParams _params = new BasicHttpParams();
		        HttpProtocolParams.setVersion(_params, HttpVersion.HTTP_1_1);
		        HttpProtocolParams.setContentCharset(_params, HTTP.UTF_8);
		
		        SchemeRegistry registry = new SchemeRegistry();
		        registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
		        registry.register(new Scheme("https", sf, 443));
		
		        ClientConnectionManager ccm = new ThreadSafeClientConnManager(_params, registry);
		
		        client = new DefaultHttpClient(ccm, _params);
		        
		        trustStore = null; //Clean up memory
		        sf = null; //Clean up memory
		        _params = null; //Clean up memory
		        registry = null; //Clean up memory
		        ccm = null; //Clean up memory
		    } 
		    catch (Exception e) 
		    {
		    	client = new DefaultHttpClient();
		    }
			
			String _url = "https://accounts.google.com/o/oauth2/token";
			
			httppost = new HttpPost(_url);
			
			MultipartEntity multi = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
			
			multi.addPart("client_id", new StringBody(API_KEY));
			
			multi.addPart("client_secret", new StringBody(API_SECRET));
			
			multi.addPart("refresh_token", new StringBody(refresh_token));
			
			multi.addPart("grant_type", new StringBody("refresh_token"));
			
			httppost.setEntity(multi);
			
			ResponseHandler<String> handler = new BasicResponseHandler();
			
			String response = client.execute(httppost, handler);
			
			Log.d(TAG, response);
			
			result = response;
		}
		catch (Exception e) 
		{
			e.printStackTrace();
			
			result = "";
		}
		
		return result;
	}
	
	public static String UploadPhoto(String token, String refresh_token, String title, String filepath, String user_id)
	{
		String result = "";
		
		/*
		 * Document
		 * http://code.google.com/apis/picasaweb/docs/2.0/developers_guide_protocol.html#Auth
		 */
		
		try
		{
			HttpClient client = null;
			
			HttpPost httppost = null;
			
			try 
		    {
	    		KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
		        trustStore.load(null, null);
		
		        SSLSocketFactory sf = new Commons.MySSLSocketFactory(trustStore);
		        sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
		
		        HttpParams _params = new BasicHttpParams();
		        HttpProtocolParams.setVersion(_params, HttpVersion.HTTP_1_1);
		        HttpProtocolParams.setContentCharset(_params, HTTP.UTF_8);
		
		        SchemeRegistry registry = new SchemeRegistry();
		        registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
		        registry.register(new Scheme("https", sf, 443));
		
		        ClientConnectionManager ccm = new ThreadSafeClientConnManager(_params, registry);
		
		        client = new DefaultHttpClient(ccm, _params);
		        
		        trustStore = null; //Clean up memory
		        sf = null; //Clean up memory
		        _params = null; //Clean up memory
		        registry = null; //Clean up memory
		        ccm = null; //Clean up memory
		    } 
		    catch (Exception e) 
		    {
		    	client = new DefaultHttpClient();
		    }

			/*
			 * Get refresh Token
			 */			
			String url = "https://picasaweb.google.com/data/feed/api";
			
			url += "/user/" + user_id + "/albumid/default";
			
			Log.i(TAG, "Upload Photo URL: " + url);
			
			URI uri = new URI(url);
			
			httppost = new HttpPost(uri);
			
			FileEntity fe = new FileEntity(new File(filepath), "image/jpeg");
			
			fe.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "image/jpeg"));
			
			httppost.setEntity(fe);
			
			httppost.addHeader("Authorization", "Bearer " + token);
			
			httppost.addHeader("GData-Version", "2");
							
			ResponseHandler<String> res = new BasicResponseHandler();
			
			String httpResponseUpload = client.execute(httppost, res);
			
			Log.i(TAG, "Response from Upload: " + httpResponseUpload);

			result = httpResponseUpload;
		}
		catch (Exception e) 
		{
			Log.e(TAG, e.toString());
			
			e.printStackTrace();
		}
		
		return result;
	}
	public static String getLink(String uid, String access_token)
	{
		String url = "https://picasaweb.google.com/data/feed/api/user/" + uid + "?access_token=" + access_token + "&kind=photo&alt=rss";
		return url;       
	}
	public static void getPhoto(String link){

			HttpGet get = new HttpGet(link);
			
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
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
}
