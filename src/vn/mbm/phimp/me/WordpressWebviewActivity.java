package vn.mbm.phimp.me;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;


public class WordpressWebviewActivity extends Activity{
	private WebView webView;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.wordpress_introduction);

		webView = (WebView) findViewById(R.id.wvWordpressIntroduction);
		webView.getSettings().setJavaScriptEnabled(true);

		String customHtml = "<html><body><h1>Hello, WebView</h1></body></html>";
		webView.loadData(customHtml, "text/html", "UTF-8");

	}
}