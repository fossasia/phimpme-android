package vn.mbm.phimp.me;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;

public class ViewImage  extends Activity{
	 private String filename;
     @Override
     public void onCreate(Bundle savedInstanceState) {
           super.onCreate(savedInstanceState);
           System.gc();
           Intent i = getIntent();
           Bundle extras = i.getExtras();
           BitmapFactory.Options bfo = new BitmapFactory.Options();
           bfo.inSampleSize = 2;
           filename = extras.getString("filename");
           ImageView iv = new ImageView(getApplicationContext());
           Bitmap bm = BitmapFactory.decodeFile(filename, bfo);
           iv.setImageBitmap(bm);
           setContentView(iv);
     } 
}
