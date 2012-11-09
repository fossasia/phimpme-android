package vn.mbm.phimp.me.services;

import java.net.URLEncoder;
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

import vn.mbm.phimp.me.utils.Commons.MySSLSocketFactory;
import vn.mbm.phimp.me.R;
import android.util.Log;


public class QQServices {
	public static String title  = "QQ";
	public static int icon = R.drawable.tencent_qq;
	public static String client_id = "801146389";
	public static String client_secret = "b98e28e4d6e58e7edb5c6cf228802871";
	public static final String URL_REQUEST_TOKEN = "https://open.t.qq.com/cgi-bin/request_token";
	public static final String URL_ACCESS_TOKEN = "https://open.t.qq.com/cgi-bin/oauth2/access_token";
	public static final String URL_AUTHORIZE = " https://open.t.qq.com/cgi-bin/oauth2/authorize";	
	public static final String CALLBACK_URL = "http://phimp.me/qq";
	public static final String CALLBACK_HOST = "phimp.me";
	public static String TAG = "QQ";
	public static OAuthConsumer consumer = null;
	public static OAuthProvider provider = null;
	public static void init()
	{
		try
		{
			if (consumer == null)
			{
				consumer = new CommonsHttpOAuthConsumer(client_id, client_secret);
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
	public static String getAuthenticateCode()
	{
		try
		{
			String url = "https://open.t.qq.com/cgi-bin/oauth2/authorize?client_id=" + client_id 
				+ "&redirect_uri=" + URLEncoder.encode(CALLBACK_URL)+ "&response_type=code";
					//+ "&scope=" + URLEncoder.encode("email,read_stream,publish_stream,offline_access") + "&response_type=token";
			
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
	public static String getAccessToken(String code)
	{
		String response ="";
		
			String url = "https://open.t.qq.com/cgi-bin/oauth2/access_token?grant_type=authorization_code&client_id=" + client_id 
				 +"&client_secret="+client_secret + "&code="+ code  + "&response_type=token"+"&redirect_uri=" + URLEncoder.encode(CALLBACK_URL);
					//+ "&scope=" + URLEncoder.encode("email,read_stream,publish_stream,offline_access") + "&response_type=token";
			
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
