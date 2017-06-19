package vn.mbm.phimp.me;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;


public class DrupalWebviewActivity extends Activity{
	private WebView webView;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.drupal_introduction);

		webView = (WebView) findViewById(R.id.wvDrupalIntroduction);
		webView.getSettings().setJavaScriptEnabled(true);

		String customHtml = "<html><body><h1>Drupal Introduction</h1></body></html>";
		webView.loadData(customHtml, "text/html", "UTF-8");

	}
}