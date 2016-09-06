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
import vn.mbm.phimp.me.database.FacebookItem;
import vn.mbm.phimp.me.services.FacebookServices;
import vn.mbm.phimp.me.utils.Commons.MySSLSocketFactory;
import vn.mbm.phimp.me.utils.RSSPhotoItem_Personal;
import vn.mbm.phimp.me.R;
import android.content.Context;
import android.util.Log;

public class Facebook {
	static final String TAG = "Facebook";
	
	public static final String PRIVATE_TAG = "Facebook Private Photos";
	public static final int ICON = R.drawable.facebook;
	
	public static ArrayList<RSSPhotoItem_Personal> getPrivatePhotos(Context ctx, String tag)
	{
		
		ArrayList<RSSPhotoItem_Personal> list = new ArrayList<RSSPhotoItem_Personal>();
		String[] id;
		String[] name;
		String[] service;
		String account_facebook="";
		
		
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
			//Log.d("luong", id[0]);
			for (int i = 0; i < PhimpMe.checked_accounts.size(); i++)
        	{
				
        		boolean _b = PhimpMe.checked_accounts.get(id[i]);        		
        		
        		if (_b)
        		{
        			if(service[i].toString().equals("facebook"))
        			{
        				account_facebook +=name[i]+";";
        				Log.e(TAG,"account fb :"+ account_facebook);
        				try
        				{
        					FacebookItem acc = FacebookItem.getItem(ctx, id[i]);
        					String access_token = acc.getAccessToken();
        					Log.d("token", access_token);
        					String uid = acc.getUserID();
        					String album_list[] = FacebookServices.getAlbumList(access_token, uid);
        					Log.d("Facebook", "number album: " + album_list.length);     					
        					for(int count = 0; count< album_list.length; count++){
	        					String url ="https://graph.facebook.com/";
	        					url += album_list[count];
	        					url += "/photos";
	        					url += "?access_token=" + access_token;
	        					Log.d("Facebook", url);
	        					
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
	        					Log.d("Facebook", response);
	        					JSONObject json = new JSONObject(response);
	        					JSONArray arr_json = json.getJSONArray("data");
	        					//get list photos of Album
	        					Log.d("Facebook", "number of photos: " + arr_json.length());
	        					
	        					for(int j=0; j< arr_json.length(); j++){
	        						try{
	        						JSONObject _json = arr_json.getJSONObject(j);
	        						JSONArray arr_j = _json.getJSONArray("images");
	        						JSONObject _json1 = arr_j.getJSONObject(0);
	        						
	        						String link = _json1.getString("source");
	        						String width = _json1.getString("width");
	        						String height = _json1.getString("height");
	        						
	        						JSONObject _json2 = arr_j.getJSONObject(1);
	        						String thumbnails = _json2.getString("source");
	        						
	        						RSSPhotoItem_Personal rssitem = new RSSPhotoItem_Personal();
									rssitem.setDescription("");
									rssitem.setTitle("");
									rssitem.setLink(link);
									rssitem.setURL(thumbnails);
									rssitem.setHeight(height);
									rssitem.setWidth(width);
									rssitem.setService("personal_facebook");
									
									list.add(rssitem);
	        						}catch(Exception e){
	        							continue;
	        						}
	        						
	        					}
        				}
        				}
        				catch (Exception e) 
        				{
        					Log.e(TAG, "FACEBOOK Error: " + e.toString());
        					e.printStackTrace();
        				}
				 
        			}
        		}
        	}

			return list;
	}
}