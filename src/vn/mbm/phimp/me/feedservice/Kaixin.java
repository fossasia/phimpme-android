package vn.mbm.phimp.me.feedservice;

import java.security.KeyStore;
import java.util.ArrayList;

import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
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
import org.json.JSONObject;

import vn.mbm.phimp.me.PhimpMe;
import vn.mbm.phimp.me.database.AccountItem;
import vn.mbm.phimp.me.database.KaixinDBItem;
import vn.mbm.phimp.me.services.KaixinServices;
import vn.mbm.phimp.me.utils.Commons.MySSLSocketFactory;
import vn.mbm.phimp.me.utils.RSSPhotoItem_Personal;
import vn.mbm.phimp.me.R;
import android.content.Context;
import android.util.Log;

public class Kaixin {
	static final String TAG = "Kaixin";
	
	public static final String PRIVATE_TAG = "Kaixin Photos";
	public static final int ICON = R.drawable.kaixin;
	
	public static ArrayList<RSSPhotoItem_Personal> getPersonal(Context ctx, String tag)
	{
		ArrayList<RSSPhotoItem_Personal> list = new ArrayList<RSSPhotoItem_Personal>();
		String[] id;
		String[] name;
		String[] service;
		String account_kaixin="";
		
		
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
        			if(service[i].toString().equals("kaixin"))
        			{
        				account_kaixin +=name[i]+";";
        				Log.e(TAG,"account kaixin :"+ account_kaixin);
        				try
        				{
        					KaixinDBItem acc = KaixinDBItem.getItem(ctx, id[i]);
        					String access_token = acc.getToken();
        					String user_id = acc.getUserID();
        					String album_id = acc.getAlbumId();
        					Log.d("token", access_token);
        					String ab_res = KaixinServices.getAlbumList(access_token);
        					JSONObject json = new JSONObject(ab_res);
        					JSONArray arr_json = json.getJSONArray("data");
        					Log.d("Kaixin", "number album: " + arr_json.length());
        					for(int j=0; j<arr_json.length(); j++){
        						String url =  "https://api.kaixin001.com/photo/show.json?";
        							   url += "access_token=" + access_token; 	
        							   url += "&uid=" + user_id;
        							   url += "&albumid=" + album_id;
        					    Log.d("Kaixin","Link get photo" + url);	 
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
            			    	String response = httpClient.execute(post, res);
            					Log.d("Kaixin", "Photo response" + response);
            					//get list photos of Album
            					JSONObject _json = new JSONObject(response);
            					JSONArray a_json = _json.getJSONArray("data");
            					Log.d("Kaixin", "number of photos: " + a_json.length());
            					
            					for(int k=0; k< a_json.length(); k++){
            						JSONObject js = a_json.getJSONObject(k);
   
            						String link = js.getString("pic_big");
            						String thumbnails = js.getString("pic_big");
            						
            						RSSPhotoItem_Personal rssitem = new RSSPhotoItem_Personal();
    								rssitem.setDescription("");
    								rssitem.setTitle("");
    								rssitem.setLink(link);
    								rssitem.setURL(thumbnails);
    								rssitem.setService("personal_kaixin");
    								
    								list.add(rssitem);
            					}
            					
        						
        					}
        				}
        				catch (Exception e) 
        				{
        					Log.e(TAG, "Kaixin Error: " + e.toString());
        					e.printStackTrace();
        				} 
        			}
        		}
        	}

			return list;
	}
}