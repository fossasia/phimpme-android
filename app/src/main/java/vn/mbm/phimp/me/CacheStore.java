package vn.mbm.phimp.me;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.text.SimpleDateFormat;
import java.util.HashMap;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

public class CacheStore {
    private static CacheStore INSTANCE = null;
    @SuppressWarnings("rawtypes")
	private HashMap cacheMap;
    @SuppressWarnings("rawtypes")
	private HashMap bitmapMap;
    private static final String cacheDir = "/.PhimpMeCache/";
    private static final String CACHE_FILENAME = ".cache";

    @SuppressWarnings({ "rawtypes" })
    private CacheStore() {
        cacheMap = new HashMap();
        bitmapMap = new HashMap();
        File fullCacheDir = new File(Environment.getExternalStorageDirectory().toString(),cacheDir);
        if(!fullCacheDir.exists()) {
            Log.i("CACHE", "Directory doesn't exist");
            cleanCacheStart();
            return;
        }
        try {
            ObjectInputStream is = new ObjectInputStream(new BufferedInputStream(new FileInputStream(new File(fullCacheDir.toString(), CACHE_FILENAME))));
            cacheMap = (HashMap)is.readObject();
            is.close();
        } catch (StreamCorruptedException e) {
            Log.i("CACHE", "Corrupted stream");
            cleanCacheStart();
        } catch (FileNotFoundException e) {
            Log.i("CACHE", "File not found");
            cleanCacheStart();
        } catch (IOException e) {
            Log.i("CACHE", "Input/Output error");
            cleanCacheStart();
        } catch (ClassNotFoundException e) {
            Log.i("CACHE", "Class not found");
            cleanCacheStart();
        }
    }

    @SuppressWarnings("rawtypes")
	private void cleanCacheStart() {
        cacheMap = new HashMap();
        File fullCacheDir = new File(Environment.getExternalStorageDirectory().toString(),cacheDir);
        fullCacheDir.mkdirs();
        File noMedia = new File(fullCacheDir.toString(), ".nomedia");
        try {
            noMedia.createNewFile();
            Log.i("CACHE", "Cache created");
        } catch (IOException e) {
            Log.i("CACHE", "Couldn't create .nomedia file");
            e.printStackTrace();
        }
    }

    private synchronized static void createInstance() {
        if(INSTANCE == null) {
            INSTANCE = new CacheStore();
        }
    }

    public static CacheStore getInstance() {
        if(INSTANCE == null) createInstance();
        Log.i("CacheStore", "getInstance");
        return INSTANCE;
    }
    public boolean check(String cachePath) {
    	try{
	    	if(cacheMap.containsKey("path@"+cachePath)){
	    		return true;
	    	}else{
			    return false;
	    	}
    	}catch(Exception e){
    		return false;
    	}
    }
    @SuppressWarnings("unchecked")
	public synchronized void saveCacheFile(String cacheUri, Bitmap image, int cacheId) {
    	try{
	        File fullCacheDir = new File(Environment.getExternalStorageDirectory().toString(),cacheDir);
	        String fileLocalName = new SimpleDateFormat("ddMMyyhhmmssSSS").format(new java.util.Date())+".png";
	        File fileUri = new File(fullCacheDir.toString(), fileLocalName);
	        FileOutputStream outStream = null;
	        try {
	            outStream = new FileOutputStream(fileUri);
	            image.compress(Bitmap.CompressFormat.PNG, 50, outStream);
	            outStream.flush();
	            outStream.close();
	            cacheMap.put(cacheUri, fileLocalName);
	            cacheMap.put("path@"+cacheUri, cacheUri);
	            cacheMap.put("id@"+cacheUri, cacheId);
	            Log.i("CACHE", "Saved file "+cacheUri+" (which is now "+fileUri.toString()+") correctly");
	            bitmapMap.put(cacheUri, image);
	            ObjectOutputStream os = new ObjectOutputStream(new BufferedOutputStream(
	                    new FileOutputStream(new File(fullCacheDir.toString(), CACHE_FILENAME))));
	            os.writeObject(cacheMap);
	            os.close();
	            
	        } catch (FileNotFoundException e) {
	            Log.i("CACHE", "Error: File "+cacheUri+" was not found!");
	            e.printStackTrace();
	        } catch (IOException e) {
	            Log.i("CACHE", "Error: File could not be stuffed!");
	            e.printStackTrace();
	       }
    	}catch(Exception e){
    	}
    }

    @SuppressWarnings("unchecked")
	public synchronized Bitmap getCachePath(String cacheUri) {
        if(bitmapMap.containsKey(cacheUri)) return (Bitmap)bitmapMap.get(cacheUri);

        if(!cacheMap.containsKey(cacheUri)) return null;
        String fileLocalName = (String) cacheMap.get(cacheUri);
        File fullCacheDir = new File(Environment.getExternalStorageDirectory().toString(),cacheDir);
        File fileUri = new File(fullCacheDir.toString(), fileLocalName);
        if(!fileUri.exists()) return null;

        //Log.i("CACHE", "File "+cacheUri+" has been found in the Cache");
        Bitmap bm = BitmapFactory.decodeFile(fileUri.toString());
        bitmapMap.put(cacheUri, bm);
        return bm;
    }
    public synchronized Integer getCacheId(String cachePath) {
        if(cacheMap.containsKey("id@"+cachePath)) return (Integer) cacheMap.get("id@"+cachePath);
        else return -1;

    }
    public void clearCache(){
    	bitmapMap.clear();
    	cacheMap.clear();
    	File f = new File(Environment.getExternalStorageDirectory().toString(),cacheDir);
    	String f_list[] = f.list(); 
    	for(int i=0; i<f_list.length; i++){
    		File file = new File(Environment.getExternalStorageDirectory() + cacheDir + f_list[i]);
    		file.delete();
    		Log.d("Luong", "Delete file " + file.getAbsolutePath());
    	}
    }
}