package vn.mbm.phimp.me;

import vn.mbm.phimp.me.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;

public class SplashScreen extends Activity {
	
	protected int _splashTime = 2000; 
	
	private Thread splashTread;


	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.splash);
	}

	@Override
	protected void onResume() {
		super.onResume();
		// thread for displaying the SplashScreen
		splashTread = new Thread() {
			@Override
			public void run() {
				try {
					synchronized(this){
						wait(_splashTime);
					}

					finish();
					Intent i = new Intent();
					i.setClass(SplashScreen.this, PhimpMe.class);
					startActivity(i);

				} catch(InterruptedException e) {}
			}
		};
		splashTread.start();
	}

	@Override
	protected void onPause() {
		super.onPause();
		if(splashTread!=null)
			splashTread.interrupt();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
	    if (event.getAction() == MotionEvent.ACTION_DOWN) {
	    	synchronized(splashTread){
	    		splashTread.notifyAll();
	    	}
	    }
	    return true;
	}
	
}
