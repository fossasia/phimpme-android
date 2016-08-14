package vn.mbm.phimp.me.utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.util.Log;

public class ImageUtil 
{
	public static Bitmap getBitmapfromURL(String url) 
	{ 
        try 
        { 
            HttpURLConnection conn = (HttpURLConnection) (new URL(url)).openConnection();
            
            conn.setRequestMethod("GET");

            conn.setInstanceFollowRedirects(true);
            conn.setUseCaches(true);
            conn.setChunkedStreamingMode(8*1024);
            conn.setDoInput(true);
            conn.setConnectTimeout(60*1000);
            conn.setReadTimeout(60*1000);
            conn.connect();

            InputStream bmpIs = conn.getInputStream();
            BufferedInputStream bmpBis = new BufferedInputStream(bmpIs); 
            Bitmap bmpThumb = null;

            BitmapFactory.Options bfOpt = new BitmapFactory.Options();
            
            bfOpt.inScaled = true;
            bfOpt.inSampleSize = 2;
            bfOpt.inPurgeable = true;

            bmpThumb = BitmapFactory.decodeStream(bmpBis,null,bfOpt);
            if(bmpThumb == null)
            {
            	bmpThumb = BitmapFactory.decodeStream(conn.getInputStream());
            }
            bmpBis.close();
            bmpIs.close();
            return bmpThumb;
        } 
        catch (Exception ex) 
        { 
            return null; 
        } 
    }
	
	public static boolean SaveBitmap(Context context, Bitmap bitmap, String filename)  
    { 
		FileOutputStream fos;
		File file;
		Boolean result = false;
		
		fos = null;
		
        try 
        {
        	file = new File(filename);
            fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,fos);
            fos.flush(); 
        	fos.close();
        	result = true;
        } 
        catch (Exception e) 
        {
        	//Toast.makeText(context, "SaveBitmap (Error):\n" + e.toString(), Toast.LENGTH_LONG).show();
        	Log.e("mbmphotos", "SaveBitmap: " + e.toString());
            result = false;
        }
        finally
        {
        	file = null;
        	if (fos != null)
        	{
	        	try 
	        	{
					fos.close();
				} 
	        	catch (IOException e) {}
        	}
        	bitmap.recycle();
        }
        
        return result;
    }
	
	public static Bitmap BitmapResize(Bitmap bitmap, int newWidth, int newHeight) 
	{ 
        int width = bitmap.getWidth(); 
        int height = bitmap.getHeight(); 
        
        float scaleWidth = ((float) newWidth) / width; 
        float scaleHeight = ((float) newHeight) / height; 

        // createa matrix for the manipulation 
        Matrix matrix = new Matrix(); 
        // resize the bit map 
        matrix.postScale(scaleWidth, scaleHeight);

        // recreate the new Bitmap 
        Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, false);
        bitmap.recycle();
        matrix = null;
        return resizedBitmap; 
    }
	
	/*
	 * http://stackoverflow.com/questions/8112715/how-to-crop-bitmap-center-like-imageview
	 */
	public static Bitmap scaleCenterCrop(Bitmap source, int newHeight, int newWidth) 
	{
	    int sourceWidth = source.getWidth();
	    int sourceHeight = source.getHeight();

	    // Compute the scaling factors to fit the new height and width, respectively.
	    // To cover the final image, the final scaling will be the bigger 
	    // of these two.
	    float xScale = (float) newWidth / sourceWidth;
	    float yScale = (float) newHeight / sourceHeight;
	    float scale = Math.max(xScale, yScale);

	    // Now get the size of the source bitmap when scaled
	    float scaledWidth = scale * sourceWidth;
	    float scaledHeight = scale * sourceHeight;

	    // Let's find out the upper left coordinates if the scaled bitmap
	    // should be centered in the new size give by the parameters
	    float left = (newWidth - scaledWidth) / 2;
	    float top = (newHeight - scaledHeight) / 2;

	    // The target rectangle for the new, scaled version of the source bitmap will now
	    // be
	    RectF targetRect = new RectF(left, top, left + scaledWidth, top + scaledHeight);

	    // Finally, we create a new bitmap of the specified size and draw our new,
	    // scaled bitmap onto it.
	    Bitmap dest = Bitmap.createBitmap(newWidth, newHeight, source.getConfig());
	    Canvas canvas = new Canvas(dest);
	    canvas.drawBitmap(source, null, targetRect, null);

	    return dest;
	}
}
