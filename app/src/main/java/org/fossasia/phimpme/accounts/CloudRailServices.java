package org.fossasia.phimpme.accounts;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import com.cloudrail.si.CloudRail;
import com.cloudrail.si.exceptions.ParseException;
import com.cloudrail.si.interfaces.CloudStorage;
import com.cloudrail.si.services.Dropbox;
import com.cloudrail.si.services.GoogleDrive;

import org.fossasia.phimpme.utilities.BasicCallBack;
import org.fossasia.phimpme.utilities.Constants;

import java.io.InputStream;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by suneetsri on 27/9/17.
 */

public class CloudRailServices {

    private static final String CLOUDRAIL_LICENSE_KEY = Constants.CLOUDRAIL_LICENSE_KEY;
    public static final CloudRailServices instance = new CloudRailServices();
    private static final String TAG = "CloudRailServices" ;
    private final AtomicReference<CloudStorage> dropbox = new AtomicReference<>();
    private Activity context = null;
    DropboxLogin dropboxLogin;
    public GoogleDrive googleDrive;
    public static BasicCallBack basicCallBack;
    Dropbox db;

    public static CloudRailServices getInstance(){
        return instance;
    }

    private CloudRailServices(){
    }

    public static void setCallBack(BasicCallBack basicCallBack){
        CloudRailServices.basicCallBack=basicCallBack;
    }

    private void initDropbox(){

        db = new Dropbox(context, Constants.DROPBOX_APP_KEY,
                Constants.DROPBOX_APP_SECRET,"https://auth.cloudrail.com/org.fossasia.phimpme", "login-state");
        dropbox.set(db);
    }

    private void initGoogleDrive(){

        googleDrive = new GoogleDrive(context,Constants.GOOGLEDRIVE_APP_KEY,Constants.GOOGLEDRIVE_SECRET_KEY
        ,"org.fossasia.phimpme:/oauth2redirect","login-googledrive");
    }

    public void prepare(Activity context)
    {

        this.context= context;
        CloudRail.setAppKey(CLOUDRAIL_LICENSE_KEY);
        this.initDropbox();
        this.initGoogleDrive();
    }

    public String getToken() {
        return dropbox.get().saveAsString();
    }

    public String getGoogleDriveToken(){
        return googleDrive.saveAsString();
    }

    public void login()
    {

        dropboxLogin = new DropboxLogin();
        dropboxLogin.execute();

    }

    public void googleDriveLogin(){
            DriveLogin driveLogin = new DriveLogin();
            driveLogin.execute();
    }

    public void upload(String path, InputStream inputStream, Long size , Boolean overwrite)
    {
        dropbox.get().upload(path,inputStream,size,overwrite);
    }

   public class DropboxLogin extends AsyncTask<Void,Void,Void>{


       @Override
       protected void onPostExecute(Void aVoid) {
           Log.e(TAG, "Dropbox Login token "+db.saveAsString());
           basicCallBack.callBack(1,db.saveAsString());
       }

       @Override
       protected Void doInBackground(Void... params) {
           db.login();
           if(!(db.exists("/phimpme_uploads"))) {
               db.createFolder("/phimpme_uploads");
           }
           return null;

       }
   }

   public class DriveLogin extends AsyncTask<Void,Void,Void>{

       @Override
       protected void onPostExecute(Void aVoid) {
           Log.e(TAG, "GoogleDriveLogin "+googleDrive.saveAsString() );
           basicCallBack.callBack(2,googleDrive.saveAsString());
       }

       @Override
       protected Void doInBackground(Void... voids) {
           googleDrive.useAdvancedAuthentication();
           googleDrive.login();
           if(!(googleDrive.exists("/phimpme_uploads"))){
               googleDrive.createFolder("/phimpme_uploads");
           }
           return null;
       }
   }

   public int loadAsString(){
       /*if the data is present for login then returns 1
       else 0
        */
       try {
           db.loadAsString(db.saveAsString().toString());
           return 1;
       } catch (ParseException e) {
           e.printStackTrace();
           return 0;
       }
   }

   public void driveLoadAsString(String s){
       try{
           Log.e(TAG,"GOOGLE DRIVE"+s);
           googleDrive.loadAsString(s);
       } catch (ParseException e) {
           e.printStackTrace();
       }
   }
   public void loadAsString(String s){
       try {
           Log.e(TAG, "loadAsString:Dropbox Token "+s );
           db.loadAsString(s);
       } catch (ParseException e) {
           e.printStackTrace();
       }
   }

   public String getDropboxFolderPath(){
       return ("/phimpme_uploads");
   }

   public String getGoogleDriveFolderPath(){return ("/phimpme_uploads");}

   public boolean checkDriveFolderExist(){ return googleDrive.exists(("/phimpme_uploads"));}

   public boolean checkFolderExist(){
       return db.exists("/phimpme_uploads");
   }

   public GoogleDrive getGoogleDrive(){
       return googleDrive;
   }

}
