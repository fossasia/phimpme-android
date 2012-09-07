package vn.mbm.phimp.me.utils;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.UUID;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.security.cert.CertificateException;
import javax.security.cert.X509Certificate;

import org.apache.http.conn.ssl.SSLSocketFactory;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Display;

public class Commons 
{
	/*
	 * Thong Tran - Check device is tablet or not
	 */
	public static boolean is_tablet(Context ctx)
	{
		Display display = ((Activity) ctx).getWindowManager().getDefaultDisplay(); 
		int width = display.getWidth();
		int height = display.getHeight();
		
		if ((width > 1000) || (height > 1000)) return true;
		else return false;
	}
	
	/*
	 * Thong - Check internet conenction
	 */
	public static boolean checkConnection(Activity a)
	{
		boolean hasConnectedWifi = false;
	    boolean hasConnectedMobile = false;
	 
	    ConnectivityManager cm = (ConnectivityManager) a.getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo[] netInfo = cm.getAllNetworkInfo();
	    for (NetworkInfo ni : netInfo) {
	        if (ni.getTypeName().equalsIgnoreCase("wifi"))
	            if (ni.isConnected())
	                hasConnectedWifi = true;
	        if (ni.getTypeName().equalsIgnoreCase("mobile"))
	            if (ni.isConnected())
	                hasConnectedMobile = true;
	    }
	    return hasConnectedWifi || hasConnectedMobile;
	}
	
	/*
	 * Thong - Create an Alertbox
	 */
	public static AlertDialog AlertLog(Context ctx, String msg, String btn)
	{
		AlertDialog.Builder alertbox = new AlertDialog.Builder(ctx);
        alertbox.setMessage(msg);
        alertbox.setNegativeButton(btn, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
            	
            }
        });
        return alertbox.create();
	}
	
	/*
	 * Get External Data Directory
	 */
	public static File getDataDirectory(Context ctx)
	{
		File f;
		
		if (Environment.getExternalStorageState().equals("mounted"))
		{
			//Has SD Card mounted
			f = Environment.getExternalStorageDirectory();
		}
		else
		{
			ContextWrapper c = new ContextWrapper(ctx);
			
			f = c.getFilesDir();
		}
		
		return f;
	}
	
	public static String generateID()
	{
		UUID id = UUID.randomUUID();
		return id.toString();
	}
	
	public static boolean hasImageCaptureBug() {

	    // list of known devices that have the bug
	    ArrayList<String> devices = new ArrayList<String>();
	    devices.add("android-devphone1/dream_devphone/dream");
	    devices.add("generic/sdk/generic");
	    devices.add("vodafone/vfpioneer/sapphire");
	    devices.add("tmobile/kila/dream");
	    devices.add("verizon/voles/sholes");
	    devices.add("google_ion/google_ion/sapphire");

	    return devices.contains(android.os.Build.BRAND + "/" + android.os.Build.PRODUCT + "/"
	            + android.os.Build.DEVICE);

	}
	
	public static String getRealPathFromURI(Uri contentUri, Context ctx) 
	{
		Log.d("thong", "Uri: " + contentUri.toString());
		
		try
		{
			String realpath = "";
			
			String[] proj = { MediaStore.Images.Media.DATA };
			
			//Cursor cursor = ((Activity) ctx).managedQuery(contentUri, proj, null, null, null);
			
			Cursor cursor = ctx.getContentResolver().query(contentUri, proj, null, null, null);
			
			Log.d("thong", "Column count: " + cursor.getColumnCount());
			
			int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			
			cursor.moveToFirst();
			
			realpath = cursor.getString(column_index);
			
			cursor.close();
			
			return realpath;
		}
		catch (Exception e) 
		{
			e.printStackTrace();
			return null; 
		}
	}
	
	public static void setLanguge(Context ctx, String languageToLoad)
    {
    	Locale locale = new Locale(languageToLoad);   
        Locale.setDefault(locale);  
        Configuration config = new Configuration();  
        config.locale = locale;  
        ctx.getResources().updateConfiguration(config, ctx.getResources().getDisplayMetrics());
    }
	
	public static class MySSLSocketFactory extends SSLSocketFactory {
        SSLContext sslContext = SSLContext.getInstance("TLS");

        public MySSLSocketFactory(KeyStore truststore) throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException, UnrecoverableKeyException {
            super(truststore);

            TrustManager tm = new X509TrustManager() {
                @SuppressWarnings("unused")
				public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                }

                @SuppressWarnings("unused")
				public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                }

                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return null;
                }

				@Override
				public void checkClientTrusted(
						java.security.cert.X509Certificate[] arg0, String arg1)
						throws java.security.cert.CertificateException {
				}

				@Override
				public void checkServerTrusted(
						java.security.cert.X509Certificate[] arg0, String arg1)
						throws java.security.cert.CertificateException {
				}
            };

            sslContext.init(null, new TrustManager[] { tm }, null);
        }

        @Override
        public Socket createSocket(Socket socket, String host, int port, boolean autoClose) throws IOException, UnknownHostException {
            return sslContext.getSocketFactory().createSocket(socket, host, port, autoClose);
        }

        @Override
        public Socket createSocket() throws IOException {
            return sslContext.getSocketFactory().createSocket();
        }
    }
}
