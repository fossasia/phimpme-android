/*
 * Copyright (C) 2011 Patrik �kerfeldt
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package vn.mbm.phimp.me;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;

import vn.mbm.phimp.me.PhotoSelect.ImageItem;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class ImageAdapter extends BaseAdapter {
	
	private LayoutInflater mInflater;
	private ArrayList<String> filepath;
	private String imagepath;
	public ImageAdapter(Context context,ArrayList<String> filepath,String imagepath) {
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.filepath = filepath;
		this.imagepath = imagepath;
	}
	

	public int getCount() {
		//return ids.length;
		return filepath.size();
	}


	public Object getItem(int position) {
		return position;
	}


	public long getItemId(int position) {
		return position;
	}


	public View getView(int position, View convertView, ViewGroup parent) {
		

		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.image_item, null);
		}
		try {
					String url;
					url= filepath.get(position);												
					Log.i("ImageAdapter","url : "+url+" at position :"+position);
					
					InputStream is = new FileInputStream(new File(url));					
					BitmapFactory.Options bfOpt = new BitmapFactory.Options();
	
					bfOpt.inScaled = true;
					bfOpt.inSampleSize = 2;
					bfOpt.inPurgeable = true;
	
					PhimpMeGallery.bmp = BitmapFactory.decodeStream(is, null, bfOpt);
					ImageView iv = (ImageView)convertView.findViewById(R.id.imgView);
					//get orientation value : http://sylvana.net/jpegcrop/exif_orientation.html
					ExifInterface exif=new ExifInterface(url);
					int orientation=exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
					Matrix matrix=new Matrix();
					if(orientation==3){
						Log.i("ImageAdapter","orientation 180");
						matrix.postRotate(180);						
					}
					else if(orientation==6){
						Log.i("ImageAdapter","orientation 90");
						matrix.postRotate(90);
					
					}
					if(orientation==8){
						Log.i("ImageAdapter","orientation 270");
						matrix.postRotate(270);
						
					}

						PhimpMeGallery.bmp=Bitmap.createBitmap(PhimpMeGallery.bmp, 0, 0, PhimpMeGallery.bmp.getWidth(), PhimpMeGallery.bmp.getHeight(), matrix, true);
						matrix.reset();
						iv.setImageBitmap(PhimpMeGallery.bmp);
					
					
					
					if(position==1){
						url=imagepath;										
						Log.i("ImageAdapter","imagepath : "+imagepath+" at position :"+position);
						
						
						InputStream is1 = new FileInputStream(new File(url));
						
						BitmapFactory.Options bfOpt1 = new BitmapFactory.Options();

						bfOpt1.inScaled = true;
						bfOpt1.inSampleSize = 4;
						bfOpt1.inPurgeable = true;

						PhimpMeGallery.bmp1 = BitmapFactory.decodeStream(is1, null, bfOpt1);
						
						ExifInterface exif1=new ExifInterface(url);
						int orientation1=exif1.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);

						Matrix matrix1=new Matrix();
						ImageView iv1 = (ImageView)convertView.findViewById(R.id.imgView);					
						
						if(orientation1==3){
							matrix1.postRotate(180);							
						}
						if(orientation1==6){
							matrix1.postRotate(90);
						}
						if(orientation1==8){
							matrix1.postRotate(270);
						}
						
						PhimpMeGallery.bmp1=Bitmap.createBitmap(PhimpMeGallery.bmp1, 0, 0, PhimpMeGallery.bmp1.getWidth(), PhimpMeGallery.bmp1.getHeight(), matrix1, true);
						matrix1.reset();
						iv1.setImageBitmap(PhimpMeGallery.bmp1);
						
						
					}
				
					
				
			} catch (Exception e) {
			}
		

		return convertView;
		
	}

}

