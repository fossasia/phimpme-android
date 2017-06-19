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
import vn.mbm.phimp.me.database.VkItem;
import vn.mbm.phimp.me.services.VKServices;
import vn.mbm.phimp.me.utils.Commons.MySSLSocketFactory;
import vn.mbm.phimp.me.utils.RSSPhotoItem_Personal;
import vn.mbm.phimp.me.R;
import android.content.Context;
import android.util.Log;

public class Vkontakte {
	static final String TAG = "Vkontakte";
	
	public static final String PRIVATE_TAG = "Vkontakte Photos";
	public static final int ICON = R.drawable.vk;
	
	public static ArrayList<RSSPhotoItem_Personal> getOwn(Context ctx, String tag)
	{
		ArrayList<RSSPhotoItem_Personal> list = new ArrayList<RSSPhotoItem_Personal>();
		String[] id;
		String[] name;
		String[] service;
		String account_vkontakte="";
		
		
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
        			if(service[i].toString().equals("vkontakte"))
        			{
        				account_vkontakte +=name[i]+";";
        				Log.e(TAG,"account vk :"+ account_vkontakte);
        				try
        				{
        					VkItem acc = VkItem.getItem(ctx, id[i]);
        					String access_token = acc.getToken();
        					Log.d("token", access_token);
        					String ab_list[] = VKServices.getAlbumList(access_token);
        					Log.d("Vkontakte", "number album: " + ab_list.length);
        					String user_id = acc.getUserID();
        					
        					for(int count = 0; count< ab_list.length; count++){
        					String url ="https://api.vk.com/method/photos.get?&uid=" + user_id + "&aid=" + ab_list[count] + "&access_token=" + access_token;
        					Log.d("Vkontakte", url);
        					
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
        					Log.d("Vkontakte", response);
    						
        					//get list photos of Album
        					JSONObject json = new JSONObject(response);
        					JSONArray arr_json = json.getJSONArray("response");
        					Log.d("Vkontakte", "number of photos: " + arr_json.length());
        					
        					for(int j=0; j< arr_json.length(); j++){
        						JSONObject _json = arr_json.getJSONObject(j);
        						String link = _json.getString("src_big");
        						String thumbnails = _json.getString("src_small");
        						RSSPhotoItem_Personal rssitem = new RSSPhotoItem_Personal();
								rssitem.setDescription("");
								rssitem.setTitle("");
								rssitem.setLink(link);
								rssitem.setURL(thumbnails);
								rssitem.setService("personal_vkontakte");
								
								list.add(rssitem);
        					}
        				}
        				}
        				catch (Exception e) 
        				{
        					Log.e(TAG, "VK Error: " + e.toString());
        					e.printStackTrace();
        				}       				 
        			}
        		}
        	}

			return list;
	}
}