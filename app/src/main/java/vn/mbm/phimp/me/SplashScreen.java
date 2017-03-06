package vn.mbm.phimp.me;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

//splash screen
public class SplashScreen extends Activity {
	private static int SPLASH_TIME_OUT = 2000;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash);
		new Handler().postDelayed(new Runnable() {
			public void run() {
				SplashScreen.this.startActivity(new Intent(SplashScreen.this, PhimpMe.class));
				SplashScreen.this.finish();
			}
		}, (long) SPLASH_TIME_OUT);
	}
}
