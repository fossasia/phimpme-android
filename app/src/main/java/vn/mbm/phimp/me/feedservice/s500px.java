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
import vn.mbm.phimp.me.services.S500pxService;
import vn.mbm.phimp.me.utils.Commons.MySSLSocketFactory;
import vn.mbm.phimp.me.utils.RSSPhotoItem;
import vn.mbm.phimp.me.utils.RSSPhotoItem_Personal;
import vn.mbm.phimp.me.R;
import android.content.Context;
import android.util.Log;

public class s500px {
static final String TAG = "500px";
public static final String PUBLIC_TAG = "500px Public Photos";
	public static final String PRIVATE_TAG = "500px Private Photos";
	public static final int ICON = R.drawable.s500px;
	
	public static ArrayList<RSSPhotoItem_Personal> getPrivatePhotos(Context ctx, String tag)
	{
		Log.d("s500px=====>", "getprivatephoto");
		ArrayList<RSSPhotoItem_Personal> list = new ArrayList<RSSPhotoItem_Personal>();
		String[] id;
		String[] name;
		String[] service;
		String account_500px="";
		
			S500pxService.OAuthRequestToken();
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
				Log.d("Account==>>>", name[i]);
			}
			accounts = null;
			
			Log.d("Quang Tri", PhimpMe.checked_accounts.size()+"");
			for (int i = 0; i < PhimpMe.checked_accounts.size(); i++)
        	{
				
        		boolean _b = PhimpMe.checked_accounts.get(id[i]);        		
        		Log.d("service==>>",service[i].toString());
        		if (_b)
        		{
        			if(service[i].toString().equals("500px"))
        			{
        				account_500px +=name[i];
        				Log.e(TAG,"account 500px :"+ account_500px);
        				try
        				{
        					    String url ="https://api.500px.com/v1/photos?feature=user&username="+account_500px+"&consumer_key="+S500pxService.CONSUMER_KEY;
	        					Log.d("person 500px url ==>>>", url);
	        					
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
	        					Log.d("500px", response);
	        					JSONObject json = new JSONObject(response);
	        					JSONArray arr_json = json.getJSONArray("photos");
	        					//get list photos of Album
	        					Log.d("500px=>>", "number of photos: " + arr_json.length());
	        					
	        					for(int j=0; j< arr_json.length(); j++){
	        						try{
	        						JSONObject _json = arr_json.getJSONObject(j);
	        						String link = _json.getString("image_url");
	        						String width = _json.getString("width");
	        						String height = _json.getString("height");
	        						String thumbnails = _json.getString("image_url");
	        						
	        						RSSPhotoItem_Personal rssitem = new RSSPhotoItem_Personal();
									rssitem.setDescription("");
									rssitem.setTitle("");
									rssitem.setLink(link);
									rssitem.setURL(thumbnails);
									rssitem.setHeight(height);
									rssitem.setWidth(width);
									rssitem.setService("personal_500px");
									
									list.add(rssitem);
	        						}catch(Exception e){
	        							continue;
	        						}
	        						
	        					}
        				//}
        				}
        				catch (Exception e) 
        				{
        					Log.e(TAG, "500PX Error: " + e.toString());
        					e.printStackTrace();
        				}
				 
        			}
        		}
        	}
			Log.d("s500px","list photos count : "+list.size());
			return list;
	}
	public static ArrayList<RSSPhotoItem> get500pxPublic(Context ctx)
	{
		S500pxService.OAuthRequestToken();
		ArrayList<RSSPhotoItem> list = new ArrayList<RSSPhotoItem>();
		String url="https://api.500px.com/v1/photos?feature=fresh_today&consumer_key="+S500pxService.CONSUMER_KEY;
		HttpGet get = new HttpGet(url);		
		HttpClient httpClient = null;
		try{
			httpClient = new DefaultHttpClient();
			ResponseHandler<String> res = new BasicResponseHandler();
	    	String response = httpClient.execute(get, res);
			Log.d("500====>>>>>px", response);
			JSONObject json = new JSONObject(response);
			JSONArray arr_json = json.getJSONArray("photos");
			for(int j=0; j< arr_json.length(); j++){
				try{
				JSONObject _json = arr_json.getJSONObject(j);
				//JSONArray arr_j = _json.getJSONArray("images");
				//JSONObject _json1 = arr_j.getJSONObject(0);
				
				String link = _json.getString("image_url");
				String width = _json.getString("width");
				String height = _json.getString("height");
				Log.d("link====>>>>>>>>>>>>>>>>>",link);
				//JSONObject _json2 = arr_j.getJSONObject(1);
				String thumbnails = _json.getString("image_url");
				
				RSSPhotoItem rssitem = new RSSPhotoItem();
				rssitem.setDescription(_json.getString("description"));
				rssitem.setTitle(_json.getString("name"));
				rssitem.setLink(link);
				rssitem.setURL(thumbnails);
				rssitem.setHeight(height);
				rssitem.setWidth(width);
				rssitem.setService("public_500px");
				
				list.add(rssitem);
				}catch(Exception e){
					continue;
				}
				
			}
			}
		catch (Exception e) {
			// TODO: handle exception
		}
		
				
		
		return list;
	}
}
