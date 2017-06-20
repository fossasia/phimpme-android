package vn.mbm.phimp.me.utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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
import android.media.ExifInterface;
import android.util.Log;

import vn.mbm.phimp.me.R;

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


    public static float getOrientation(String f)
    {
        float degress = 0;
        try {

            ExifInterface exif = new ExifInterface(f);
            int orientation=exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
            if(orientation == 3){
                degress = 180;
            }
            if(orientation == 6){
                degress = 90;
            }
            if(orientation == 8){
                degress = 270;
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return degress;
    }

    public static int calculateInSampleSize(

            String mfile, int reqWidth) {
        // Raw height and width of image
        //Bitmap bm = null;
        BitmapFactory.Options bounds = new BitmapFactory.Options();
        bounds.inTempStorage = new byte[32 * 1024];
        bounds.inDither=false;
        bounds.inPurgeable=true;
        bounds.inInputShareable=true;
        bounds.inJustDecodeBounds = true;
        File file=new File(mfile);
        float rotate = getOrientation(mfile);
        FileInputStream fs=null;
        try {
            fs = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            //TODO do something intelligent
            e.printStackTrace();
        }

        try {
            if(fs!=null) BitmapFactory.decodeFileDescriptor(fs.getFD(), null, bounds);
        } catch (IOException e) {
            //TODO do something intelligent
            e.printStackTrace();
        } finally{
            if(fs!=null) {
                try {
                    fs.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        //final int height = bounds.outHeight;
        final int width;
        if (rotate == 0)
            width = bounds.outWidth; else
            width = bounds.outHeight;

        Log.e("Width",String.valueOf(width));
        System.gc();
        int inSampleSize = 1;

        if (width > reqWidth) {
            inSampleSize = Math.round((float)width / (float)reqWidth);
        }
        return inSampleSize;
    }
    public static Bitmap decodeSampledBitmapFromFile(Context ctx, String mfile,
                                                     int reqWidth) {
        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        Bitmap bm = null;
        System.gc();
        // BitmapFactory.decodeResource(res, resId, options);
        options.inTempStorage = new byte[32* 1024];
        options.inDither = false;
        options.inPurgeable = true;
        options.inInputShareable = true;
        // Calculate inSampleSize
        int inSampleSize = calculateInSampleSize(mfile, reqWidth);
        options.inSampleSize = inSampleSize;
        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        File file=new File(mfile);
        FileInputStream fs=null;
        try {
            fs = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            //TODO do something intelligent
            e.printStackTrace();
        }
        Matrix matrix = new Matrix();
        float rotate = getOrientation(mfile);
        try {
            if(fs!=null) {
                Log.e("dsfsdfds","sdffsdfds");
                if (rotate == 0) return BitmapFactory.decodeFileDescriptor(fs.getFD(), null, options);
                else {
                    matrix.postRotate(rotate);

                    bm = BitmapFactory.decodeFileDescriptor(fs.getFD(), null, options);

                    return Bitmap.createBitmap(bm,0,0,bm.getWidth(),bm.getHeight(),matrix,true);
                }
            }
            else return BitmapFactory.decodeResource(ctx.getResources(), R.drawable.image_not_found, options);
        } catch (IOException e) {
            //TODO do something intelligent
            e.printStackTrace();
        } finally{
            if(fs!=null) {
                try {
                    fs.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
}
