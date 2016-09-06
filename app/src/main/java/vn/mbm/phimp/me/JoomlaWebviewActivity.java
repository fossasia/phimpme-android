package vn.mbm.phimp.me;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;


public class JoomlaWebviewActivity extends Activity{
	private WebView webView;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.joomla_introduction);

		webView = (WebView) findViewById(R.id.wvJoomlaIntroduction);
		webView.getSettings().setJavaScriptEnabled(true);

		String customHtml = "<html><body><h1>Joomla Introduction</h1></body></html>";
		webView.loadData(customHtml, "text/html", "UTF-8");

	}
}