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

		String customHtml = 
		"<html>"+
		    "<body style='background:#cfe'>"+
		        "<center><h2>Wordpress Introduction</h2> " +
		        "------------------------" +
		        "</center>"+
		        "<p>"+
		            "If you want to implement wordpress for Phimpme applicaion, please install some plugin below :"+
		            "<ol>"+
		                "<li>Drupal to WP XML_RPC <a href='http://wordpress.org/extend/plugins/drupal-to-wp-xml-rpc/'>http://wordpress.org/extend/plugins/drupal-to-wp-xml-rpc/</a> </li>"+
		                "<li>Wp-xmlrpc-modernization <a href='http://wordpress.org/extend/plugins/xml-rpc-modernization/'>http://wordpress.org/extend/plugins/xml-rpc-modernization/</a></li>"+
		                "<li>XML-RPC Extended Media Upload <a href='http://wordpress.org/extend/plugins/xml-rpc-extended-media-upload/'>http://wordpress.org/extend/plugins/xml-rpc-extended-media-upload/</a></li>"+
		            "</ol>"+
		            "Then, please access admin page <a href='http://yourdomain/wp-admin/options-writing.php'>http://yourdomain/wp-admin/options-writing.php</a> and check enable XML_RPC"+
		        "</p>"+
		    "</body>"+
		"</html>";
		webView.loadData(customHtml, "text/html", "UTF-8");

	}
}