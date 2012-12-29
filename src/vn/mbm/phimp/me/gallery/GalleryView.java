package vn.mbm.phimp.me.gallery;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Gallery;

public class GalleryView  extends Gallery{
public GalleryView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        // TODO Auto-generated constructor stub
    }

public GalleryView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
    }


    public GalleryView(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }


    /*@Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                           float velocityY) {        
    	 if (velocityX > 1200.0f)
         {
             velocityX = 1200.0f;
         }
         else if(velocityX < 1200.0f)
         {
             velocityX = -1200.0f;
         }    	     	     	
         return super.onFling(e1, e2, velocityX, velocityY);
    }*/
    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        int kEvent;    
        if(isScrollingLeft(e1, e2)) {   
            //Check if scrolling left       
            kEvent = KeyEvent.KEYCODE_DPAD_LEFT;
            if (this.getSelectedItemPosition() == 0) {
            	PhimpMeGallery.overscrollleft.setVisibility(VISIBLE);            	
            	new Handler().postDelayed(new Runnable() {
            	    public void run() {
            	            PhimpMeGallery.overscrollleft.setVisibility(View.GONE);
            	        }
            	    }, 400);
            }
        }  
        else {   
            //Otherwise scrolling right      
            kEvent = KeyEvent.KEYCODE_DPAD_RIGHT;   
            if (this.getSelectedItemPosition() == this.getCount()-1){
            	PhimpMeGallery.overscrollright.setVisibility(VISIBLE);            	
            	new Handler().postDelayed(new Runnable() {
            	    public void run() {
            	            PhimpMeGallery.overscrollright.setVisibility(View.GONE);
            	        }
            	    }, 400);
            }
        }    
        onKeyDown(kEvent, null);    
        return false;
    }

    private boolean isScrollingLeft(MotionEvent e1, MotionEvent e2) {
        return e2.getX() > e1.getX();   
    }  
    protected void onOverScrolled(int scrollX, int scrollY, boolean clampedX, boolean clampedY) 
    {
       Log.e("fade","onOverScrolled\n");
    }
    @Override
    protected android.view.ViewGroup.LayoutParams generateLayoutParams (android.view.ViewGroup.LayoutParams p) {
        return super.generateLayoutParams(p);
    }

}