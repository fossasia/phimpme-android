package vn.mbm.phimp.me;

import vn.mbm.phimp.me.R;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

class GridviewNotWifiAddapter extends BaseAdapter{
	Activity ac=new Activity();
	private Context ctx;	
	LayoutInflater li;
	
	
	public GridviewNotWifiAddapter(Context ctx) {
		this.ctx = ctx;		
	}

	public int getCount() {
		return 6;

	}

	public void removeItem() {
		notifyDataSetChanged();
	}

	public Object getItem(int position) {
		return position;
	}

	public long getItemId(int position) {
		return position;
	}
	
	public View getView(int position, View convertView, ViewGroup parent) {
        ImageView picturesView;
        
        if (convertView == null) {			
            picturesView = new ImageView(ctx);                                 
            try {	
            	for(int i=0;i<6;i++){
	            	picturesView.setImageResource(R.drawable.wifi_icon);
		            picturesView.setPadding(2, 2, 2, 2);
		            picturesView.setScaleType(ScaleType.FIT_XY);	            
		            picturesView.setLayoutParams(new GridView.LayoutParams(PhimpMe.width, PhimpMe.height));
            	}
	            try{
	            }catch(NoSuchMethodError n){
	            	
	            }
	            
			} catch (Exception e) {

			}                   
        }
        else {
            picturesView = (ImageView)convertView;
        }        
        return picturesView;

    }

}
