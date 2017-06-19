package vn.mbm.phimp.me;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.app.IntentService;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.Log;

public class CollectUserData extends IntentService{

	public CollectUserData() {
		super("Collect User Data");
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		// TODO Auto-generated method stub
		String  ANDROID         =   android.os.Build.VERSION.RELEASE;       //The current development codename, or the string "REL" if this is a release build.
	    String  BOARD           =   android.os.Build.BOARD;                 //The name of the underlying board, like "goldfish".    
	    String  BOOTLOADER      =   android.os.Build.BOOTLOADER;            //  The system bootloader version number.
	    String  BRAND           =   android.os.Build.BRAND;                 //The brand (e.g., carrier) the software is customized for, if any.
	    String  CPU_ABI         =   android.os.Build.CPU_ABI;               //The name of the instruction set (CPU type + ABI convention) of native code.
	    //String  CPU_ABI2        =   android.os.Build.CPU_ABI2;              //  The name of the second instruction set (CPU type + ABI convention) of native code.
	    String  DEVICE          =   android.os.Build.DEVICE;                //  The name of the industrial design.
	    String  DISPLAY         =   android.os.Build.DISPLAY;               //A build ID string meant for displaying to the user
	    String  FINGERPRINT     =   android.os.Build.FINGERPRINT;           //A string that uniquely identifies this build.
	    String  HARDWARE        =   android.os.Build.HARDWARE;              //The name of the hardware (from the kernel command line or /proc).
	    String  HOST            =   android.os.Build.HOST;  
	    String  ID              =   android.os.Build.ID;                    //Either a changelist number, or a label like "M4-rc20".
	    String  MANUFACTURER    =   android.os.Build.MANUFACTURER;          //The manufacturer of the product/hardware.
	    String  MODEL           =   android.os.Build.MODEL;                 //The end-user-visible name for the end product.
	    String  PRODUCT         =   android.os.Build.PRODUCT;               //The name of the overall product.
	    String  RADIO           =   android.os.Build.PRODUCT;               //The radio firmware version number.
	    String  TAGS            =   android.os.Build.TAGS;                  //Comma-separated tags describing the build, like "unsigned,debug".
	    String  TYPE            =   android.os.Build.TYPE;                  //The type of build, like "user" or "eng".
	   // String  USER            =   android.os.Build.USER;                  //
	    String url = "http://mbmvn.dyndns.org:8080/pentaho/ServiceAction?&solution=bi-developers&path=demo&action=convert.xaction&userid=joe&password=password";	    
	    HttpClient client = new DefaultHttpClient();
	    try {
	    URI uri = new URI(url);
	    Log.e("url",url);
	    HttpPost post = new HttpPost(uri);
	    List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);    
	    nameValuePairs.add(new BasicNameValuePair("t", TAGS));
	    nameValuePairs.add(new BasicNameValuePair("m", MODEL));
	    nameValuePairs.add(new BasicNameValuePair("h", HOST));
	    nameValuePairs.add(new BasicNameValuePair("hw",HARDWARE));
	    nameValuePairs.add(new BasicNameValuePair("product", PRODUCT));
	    nameValuePairs.add(new BasicNameValuePair("radio", RADIO));
	    nameValuePairs.add(new BasicNameValuePair("brand", BRAND));
	    nameValuePairs.add(new BasicNameValuePair("manuf", MANUFACTURER));
	    nameValuePairs.add(new BasicNameValuePair("boot", BOOTLOADER));
	    nameValuePairs.add(new BasicNameValuePair("tpe", TYPE));
	    nameValuePairs.add(new BasicNameValuePair("board", BOARD));
	    nameValuePairs.add(new BasicNameValuePair("display", DISPLAY));
	    nameValuePairs.add(new BasicNameValuePair("finger", FINGERPRINT));
	    nameValuePairs.add(new BasicNameValuePair("cpu", CPU_ABI));
	    nameValuePairs.add(new BasicNameValuePair("device", DEVICE));
	    nameValuePairs.add(new BasicNameValuePair("version",ANDROID));
	    nameValuePairs.add(new BasicNameValuePair("id", ID));
	    post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
	    ResponseHandler<String> res = new BasicResponseHandler();
	    String httpResponse = client.execute(post, res);
	    Log.e("User data",httpResponse);
	    String max = httpResponse.substring(httpResponse.indexOf("<DATA-ITEM>")+20, httpResponse.indexOf("]]></DATA-ITEM>"));
	    int id ;
	    try{
	    id = Integer.valueOf(max);
	    }catch(NumberFormatException num){
	    	id = 1;
	    }
	    exportInstalledPakage(id);
	    //for (int i = 0 ; i < nodes.getLength(); i++)
	    //	Log.e("node",((Element) nodes.item(i)).toString());
	    }catch(Exception e){
	    	e.printStackTrace();
	    }
		
	}
	public void exportInstalledPakage(int id){		
		final PackageManager pm = getPackageManager();
		String contents = "";
		// get a list of installed apps.
		List<ApplicationInfo> packages = pm
				.getInstalledApplications(PackageManager.GET_META_DATA);

		for (ApplicationInfo packageInfo : packages) {

			Log.i("Danh", "Installed package :" + packageInfo.packageName +" version :"+ packageInfo.targetSdkVersion);
			contents += packageInfo.packageName +":"+ packageInfo.targetSdkVersion +";";							 
	}	
		try{
			URI uri = new URI("http://192.168.1.200:8080/pentaho/ServiceAction?&solution=bi-developers&path=demo&action=app_install.xaction&userid=joe&password=password");
			HttpClient client = new DefaultHttpClient();
			HttpPost post = new HttpPost(uri);
		    List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
		    nameValuePairs.add(new BasicNameValuePair("user", Integer.toString(id)));
		    nameValuePairs.add(new BasicNameValuePair("app", contents));
		    post.setEntity(new UrlEncodedFormEntity(nameValuePairs));		    
		    ResponseHandler<String> res = new BasicResponseHandler();
		    String httpResponse = client.execute(post, res);
		    Log.e("app",httpResponse);
		}catch(Exception e){
			
		}
	}

}
