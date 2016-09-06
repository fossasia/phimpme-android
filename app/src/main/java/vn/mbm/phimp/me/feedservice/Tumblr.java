package vn.mbm.phimp.me.feedservice;

import java.security.KeyStore;
import java.util.ArrayList;

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

import vn.mbm.phimp.me.PhimpMe;
import vn.mbm.phimp.me.database.AccountItem;
import vn.mbm.phimp.me.database.TumblrItem;
import vn.mbm.phimp.me.utils.Commons.MySSLSocketFactory;
import vn.mbm.phimp.me.utils.RSSPhotoItem_Personal;
import vn.mbm.phimp.me.R;
import android.content.Context;
import android.util.Log;

public class Tumblr {
	static final String TAG = "Vkontakte";
	
	public static final String PRIVATE_TAG = "Tumlbr Personal Photos";
	public static final int ICON = R.drawable.tumblr;
	public static final String API_KEY = "pyiJHL3Bnabjrrs5CCqwTlYqVCYPlhEqNCZ6atPICBd0WHEau2";
	
	public static ArrayList<RSSPhotoItem_Personal> getOwn(Context ctx, String tag)
	{
		ArrayList<RSSPhotoItem_Personal> list = new ArrayList<RSSPhotoItem_Personal>();
		String[] id;
		String[] name;
		String[] service;
		String account_tumblr="";
		
		
			ArrayList<AccountItem> accounts = AccountItem.getAllAccounts(ctx);
			id = new String[accounts.size()];
			name = new String[accounts.size()];
			service = new String[accounts.size()];
			for (int i = 0; i < accounts.size(); i++)
			{
				AccountItem item = accounts.get(i);
				id[i] = item.getID();
				name[i] = item.getName();
				service[i] = item.getService();
			}
			accounts = null;
			for (int i = 0; i < PhimpMe.checked_accounts.size(); i++)
        	{
				
        		boolean _b = PhimpMe.checked_accounts.get(id[i]);        		
        		
        		if (_b)
        		{
        			if(service[i].toString().equals("tumblr"))
        			{
        				account_tumblr +=name[i]+";";
        				Log.e(TAG,"account tumblr :"+ account_tumblr);
        				try
        				{
        					TumblrItem acc = TumblrItem.getItem(ctx, id[i]);
        					String hostname = acc.getUserID();
        					Log.d("user_id", hostname);        					
        					String url ="http://api.tumblr.com/v2/blog/";
        					url += hostname + ".tumblr.com";
        					url += "/posts?id=";
        					url += hostname;
        					url += "&api_key=";
        					url += API_KEY;		
        					Log.d("Tumblr", url);
        					
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
        					Log.d("Tumblr: ", response);
  
        					JSONObject json = new JSONObject(response);
        					JSONObject _json = json.getJSONObject("meta");
        					String msg = _json.getString("msg");
        					if(msg.equals("OK")){
        						_json = json.getJSONObject("response");
        						JSONArray arr_json = _json.getJSONArray("posts");
        						Log.d("number of posts: ", arr_json.length()+"");
        						for(int j=0; j<arr_json.length(); j++){								
        							_json = arr_json.getJSONObject(j);
        							
        							JSONArray array_json1 = _json.getJSONArray("photos");
        							_json = array_json1.getJSONObject(0);
        							
        							JSONArray array_json2 = _json.getJSONArray("alt_sizes"); 
        							Log.d("number of photo: ", array_json2.length()+"");
        								_json = array_json2.getJSONObject(0);
        								Log.d("luong: ", _json.toString());
        								String width = _json.getString("width");
        								String height = _json.getString("height");
        								String link = _json.getString("url");
        								RSSPhotoItem_Personal rssitem = new RSSPhotoItem_Personal();
        								rssitem.setDescription("");
        								rssitem.setTitle("");
        								rssitem.setLink(link);
        								rssitem.setURL(link);
        								rssitem.setHeight(height);
        								rssitem.setWidth(width);
        								rssitem.setService("personal_tumblr");
        								list.add(rssitem);
        						}							
        					}
        				
        				}
        				catch (Exception e) 
        				{
        					Log.e(TAG, "Tumblr Error: " + e.toString());
        					e.printStackTrace();
        				}
        			}
        		}
        	}
             Log.d("list", list.size() + "");
			return list;
	}
}