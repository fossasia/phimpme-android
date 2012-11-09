package vn.mbm.phimp.me.services;

import java.net.URLEncoder;
import java.security.KeyStore;

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
import android.util.Log;

public class KaixinServices 
{
	public static int icon = R.drawable.kaixin;
	public static String title = "Kaixin";
	
	public static final String client_id = "100035256";
	public static final String api_key = "1353969102557175821fccba0ee873a8";
	public static final String secret_key = "f7635daf32af5518331835e6e44a3e92";
	
	public static final String CALLBACK_URL = "http://mbmluong.com/kaixin";
	public static final String CALLBACK_HOST = "mbmluong.com";

	final static String TAG = "Kaixin";
	
	public static OAuth20ServiceImpl service;
			public static String getAuthenticateCode()
			{
				try
				{
					
					String url = "http://api.kaixin001.com/oauth2/authorize?";
					url += "client_id=" + api_key;
					url += "&response_type=code";
					url += "&redirect_uri=" + URLEncoder.encode(CALLBACK_URL);
					url += "&scope=create_album upload_photo user_photo friends_photo";
					Log.d(TAG, "getAuthenticateCode Kaixin: " + url);
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
				
					String url = "https://api.kaixin001.com/oauth2/access_token?";
						url += "grant_type=authorization_code";
						url += "&client_id=" + api_key;
						url += "&client_secret="+secret_key;
						url += "&code="+ code;
						url += "&redirect_uri=" + URLEncoder.encode(CALLBACK_URL);
					Log.d(TAG, "getAccessToken: " + url);
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
					String url = "https://api.kaixin001.com/users/me.json";
					
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
			    		String user_name = json.getString("name");	    		
			    		String uid = json.getString("uid");	    		
			    		
			    		JSONObject js = new JSONObject();	    		
			    		js.put("user_name", user_name);	    		
			    		js.put("user_id", uid);	    			    		
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
			public static String createAlbums(String access_token){
				String aid = "";
				String url = "https://api.kaixin001.com/album/create.json";
				url += "?access_token=" + URLEncoder.encode(access_token);
				url += "&title=PhimpMe";
				//url += "&description=PhimpMeAlbum";
				Log.d("url",url);
				HttpPost post = new HttpPost(url);
				
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
					String response = httpClient.execute(post, res);
					Log.d("reponse",response);
					JSONObject json = new JSONObject(response);
					aid = json.getString("albumid");			
		    	}catch(Exception e){
		    		e.printStackTrace();
		    	}    
				return aid;
			}
			public static String getAlbumList(String access_token){
				String url = "https://api.kaixin001.com/album/show.json?&access_token="+access_token;
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
					return response;
					
		    	}catch(Exception e){
		    		e.printStackTrace();
		    		return "";
		    	}
			}
			public static String checkAlbum(String getalbum){
				try {
					int index=-1;
					JSONObject json = new JSONObject(getalbum);
					JSONArray arr_json = json.getJSONArray("data");
					if(arr_json.length()>0){
						for(int i=0; i<arr_json.length(); i++){
							if(arr_json.getJSONObject(i).getString("title").equals("PhimpMe")){
								index = i;
								break;
							}		
						}
						if(index!=-1){
								return arr_json.getJSONObject(index).getString("albumid");
						}else return "empty";
					}else return "empty";
				} catch (JSONException e) {
					return "empty";
				}
			}
}
