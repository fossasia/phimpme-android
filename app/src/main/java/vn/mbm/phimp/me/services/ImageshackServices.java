package vn.mbm.phimp.me.services;

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
import org.scribe.oauth.OAuth20ServiceImpl;

import vn.mbm.phimp.me.utils.Commons.MySSLSocketFactory;
import vn.mbm.phimp.me.R;
import android.util.Log;

public class ImageshackServices 
{
	public static int icon = R.drawable.imageshack;
	public static String title = "Imageshack";

	public static final String API_KEY = "68GIMPTYc8652aa94644d1a36a1abb2095a72625";

	final static String TAG = "Imageshack";
	final static String HOST = "http://imageshack.us";
	public static OAuth20ServiceImpl service;
	public static String login(String username, String password)
	{
		String result = "";
		
		try
		{
			String url = HOST  + "/auth.php?username="+username+"&password="+password+"&format=json";
			
			Log.d(TAG, "URL: " + url);
			
			HttpGet get = new HttpGet(url);
			HttpClient client = null;
						
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
			
			ResponseHandler<String> res = new BasicResponseHandler();
	    	
	    	result = client.execute(get, res);			
	    	
	    	Log.d(TAG, "Response: " + result);
		}
		catch (Exception e) 
		{
			Log.e(TAG, "Error: " + e.toString());
			
			e.printStackTrace();
		}
		
		return result;
	}
	
	

}
