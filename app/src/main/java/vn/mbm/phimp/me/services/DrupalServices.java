package vn.mbm.phimp.me.services;

import java.io.ByteArrayOutputStream;
import java.io.File;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import vn.mbm.phimp.me.R;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

public class DrupalServices 
{
	public static int icon = R.drawable.drupal;
	public static String title = "Drupal";
	
	public static final String TAG = "drupal";
	
	public static String sessionid = "";
	public static String sessionname = "";
	
	public static String login(String username, String password, String host)
	{
		String result = "";
		
		try
		{
			String url = host + "user/login";
			
			Log.d(TAG, "URL: " + url);
			
			HttpClient client = new DefaultHttpClient();
			
			HttpPost post = new HttpPost(url);
			
			MultipartEntity multi = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
			
			multi.addPart("username", new StringBody(username));
			multi.addPart("password", new StringBody(password));
			
			post.setEntity(multi);
			
			ResponseHandler<String> res = new BasicResponseHandler();
			
			String httpResponse = client.execute(post, res);
			
			Log.i(TAG, "Response from Upload: " + httpResponse);
			
			result = httpResponse;
			
			JSONObject json = new JSONObject(httpResponse);
			
			sessionid = json.getString("sessid");
			sessionname = json.getString("session_name");
			
			/*
			 * Login success return: 
			 * {
			 * 		"sessid":"NxzdSyq-jsh-PbQu6eylg_4-kY0kyriYHavn81akkcg",
			 * 		"session_name":"SESS05cddb0d78ffe70657492dc1723c6a73",
			 * 		"user":{"uid":"1","name":"admin","mail":"quocnamcld@gmail.com","theme":"","signature":"","signature_format":"plain_text","created":"1324369346","access":"1328265895","login":1328265975,"status":"1","timezone":"Asia/Ho_Chi_Minh","language":"","picture":null,"init":"quocnamcld@gmail.com","data":false,"roles":{"2":"authenticated user"}}
			 * }
			 */
		}
		catch (Exception e) 
		{
			Log.e(TAG, "Error: " + e.toString());
			
			e.printStackTrace();
		}
		
		return result;
	}
	
	public static String uploadPhoto(String username, String password, String service_url, String title, String description, File f, String latitude, String longitude)
	{	
		String result = "";
		
		HttpClient client = null;
		HttpPost post = null;
		ResponseHandler<String> res = new BasicResponseHandler();
		String session_id = "";
		String session_name = "";
		String file_id = "";
		String url;
		
		/*
		 * Thong - Login first
		 */
		try
		{			
			url = service_url + "user/login";
			
			Log.d(TAG, "URL: " + url);
			
			client = new DefaultHttpClient();
			
			post = new HttpPost(url);
			
			MultipartEntity multi = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
			
			multi.addPart("username", new StringBody(username));
			multi.addPart("password", new StringBody(password));
			
			post.setEntity(multi);
			
			String httpResponse = client.execute(post, res);
			
			result = httpResponse;
			
			JSONObject json = new JSONObject(httpResponse);
			
			session_id = json.getString("sessid");
			session_name = json.getString("session_name");
		}
		catch (Exception e) 
		{
			Log.e(TAG, "Error: " + e.toString());
			
			e.printStackTrace();
			
			return "0|Error on Login";
		}
		
		/*
		 * Thong - Upload file
		 */
		try
		{
			url = service_url + "file";
			
			Log.d(TAG, "URL: " + url);
			
			client = new DefaultHttpClient();
			
			post = new HttpPost(url);
			
			//Authorize
			post.setHeader("Cookie", session_name + "=" + session_id);
			
			Log.d(TAG, session_name);
			Log.d(TAG, session_id);
			
			//Get imagedata
			Bitmap bm = BitmapFactory.decodeFile(f.getAbsolutePath());
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
			byte[] b = baos.toByteArray();
			baos.flush();
			baos.close();
			baos = null;
			String img_data_b64 = Base64.encodeToString(b, Base64.DEFAULT);
			
			MultipartEntity multi = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
			
			multi.addPart("file", new StringBody(img_data_b64));
			multi.addPart("filename", new StringBody(f.getName()));

			post.setEntity(multi);
			
			String httpResponse = client.execute(post, res);
			
			img_data_b64 = "";
			
			Log.i(TAG, "Response from Upload: " + httpResponse);
			
			JSONObject json = new JSONObject(httpResponse);
			
			file_id = json.getString("fid");
			
			/*
			 * upload success return
			 * {
			 * 		"fid":"178",
			 * 		"uri":"http://gallery.mbm.vn/android_services/file/178"
			 * }
			 */
		}
		catch (Exception e) 
		{
			Log.e(TAG, "Error: " + e.toString());
			
			e.printStackTrace();
			
			return "0|Error on upload file";
		}
		
		/*
		 * Thong - Create node
		 */
		try
		{
			url = service_url + "node";
			
			Log.d(TAG, "URL: " + url);
			
			client = new DefaultHttpClient();
			
			post = new HttpPost(url);
			
			//Authorize
			post.setHeader("Cookie", session_name + "=" + session_id);
			
			MultipartEntity multi = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
			
			multi.addPart("title", new StringBody(title));
			multi.addPart("body", new StringBody(description));
			multi.addPart("type", new StringBody("media_gallery"));
			multi.addPart("language", new StringBody(""));
			
			multi.addPart("media_gallery_media[und][0][fid]", new StringBody(file_id));
			multi.addPart("field_position[und][0][latitude]", new StringBody(latitude));
			multi.addPart("field_position[und][0][longitude]", new StringBody(longitude));
			
			post.setEntity(multi);
			
			String httpResponse = client.execute(post, res);
			
			Log.i(TAG, "Response from Create Node: " + httpResponse);
		}
		catch (Exception e) 
		{
			Log.e(TAG, "Error: " + e.toString());
			
			e.printStackTrace();
			
			return "0|Error on upload file";
		}
		
		/*
		 * Thong - Logout
		 */
		try
		{
			url = service_url + "user/logout";
			
			Log.d(TAG, "URL: " + url);
			
			client = new DefaultHttpClient();
			
			post = new HttpPost(url);
			
			//Authorize
			post.setHeader("Cookie", session_name + "=" + session_id);
			
			String httpResponse = client.execute(post, res);
			
			Log.i(TAG, "Response from Logout: " + httpResponse);
		}
		catch (Exception e) 
		{
			Log.e(TAG, "Error: " + e.toString());
			
			e.printStackTrace();
		}
		
		return result;
	}
	
	public static String logout(String service_url)
	{
		String result = "";
		
		try
		{
			String url = service_url + "user/logout";
			
			Log.d(TAG, "URL: " + url);
			
			HttpClient client = new DefaultHttpClient();
			
			HttpPost post = new HttpPost(url);
			
			//Authorize
			post.setHeader("Cookie", sessionname + "=" + sessionid);
			
			ResponseHandler<String> res = new BasicResponseHandler();
			
			String httpResponse = client.execute(post, res);
			
			Log.i(TAG, "Response from Upload: " + httpResponse);
		}
		catch (Exception e) 
		{
			Log.e(TAG, "Error: " + e.toString());
			
			e.printStackTrace();
		}
		
		return result;
	}
}
