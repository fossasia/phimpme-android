package vn.mbm.phimp.me;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import vn.mbm.phimp.me.libraries.CustomRequest;
import vn.mbm.phimp.me.libraries.VolleyLibrary;


public class WordpressWebviewActivity extends Activity{
	private WebView webView;
	String client_id="52266";
	String client_secret="kY198VU8cNsdV2WOfw0tHasIWYa45vPk4NcCSo6jVdhIfeP57hec5Vak8XEHuUq9";
	String redirect_url="http://labs.fossasia.org";
	String url="https://public-api.wordpress.com/oauth2/token";
	EditText eusername,epassword;
	Button btnlogin;
	String username,password;
	ProgressDialog pd;
	String access_token,token_type;

	void init(){
		eusername=(EditText)findViewById(R.id.input_email);
		epassword=(EditText)findViewById(R.id.input_password);
		btnlogin=(Button)findViewById(R.id.btn_login);
		btnlogin.setOnClickListener(clickListener);
	}

	View.OnClickListener clickListener=new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			int id=v.getId();
			switch (id){
				case R.id.btn_login:
					username=eusername.getText().toString().trim();
					password=epassword.getText().toString().trim();
					pd=new ProgressDialog(WordpressWebviewActivity.this);
					pd.setMessage("Please Wait...");
					pd.show();
					// Sending request to get access token form wordpress
					handler.sendEmptyMessage(100);
					break;
			}
		}
	};

	Handler handler=new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if(msg.what==100){
				final HashMap<String, String> params = new HashMap<>();
				params.put("client_id", client_id);
				params.put("client_secret", client_secret);
				params.put("grant_type", "password");
				params.put("username", username);
				params.put("password", password);


				CustomRequest request = new CustomRequest(Request.Method.POST, url, params,
						new Response.Listener<JSONObject>() {
							@Override
							public void onResponse(JSONObject json) {
								Log.e("GET_PROFILE", json.toString());

								try {
									 access_token=json.getString("access_token");
									 token_type=json.getString("token_type");
								} catch (JSONException e) {
									e.printStackTrace();
								}

								pd.dismiss();
							}
						},
						new Response.ErrorListener() {
							@Override
							public void onErrorResponse(VolleyError error) {
								if(error != null)
									Log.e("GET_PROFILE", ""+error.getMessage());
									Toast.makeText(WordpressWebviewActivity.this,error.getMessage().toString(),Toast.LENGTH_SHORT).show();
								 	pd.dismiss();
							}
						});

				VolleyLibrary.getInstance(WordpressWebviewActivity.this).addToRequestQueue(request, "", false);
			}
		}
	};


	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.wordpress_introduction);
		init();


//		webView = (WebView) findViewById(R.id.wvWordpressIntroduction);
//		webView.getSettings().setJavaScriptEnabled(true);

//		String customHtml =
//		"<html>"+
//		    "<body style='background:#cfe'>"+
//		        "<center><h2>Wordpress Introduction</h2> " +
//		        "------------------------" +
//		        "</center>"+
//		        "<p>"+
//		            "If you want to implement wordpress for Phimpme applicaion, please install some plugin below :"+
//		            "<ol>"+
//		                "<li>Drupal to WP XML_RPC <a href='http://wordpress.org/extend/plugins/drupal-to-wp-xml-rpc/'>http://wordpress.org/extend/plugins/drupal-to-wp-xml-rpc/</a> </li>"+
//		                "<li>Wp-xmlrpc-modernization <a href='http://wordpress.org/extend/plugins/xml-rpc-modernization/'>http://wordpress.org/extend/plugins/xml-rpc-modernization/</a></li>"+
//		                "<li>XML-RPC Extended Media Upload <a href='http://wordpress.org/extend/plugins/xml-rpc-extended-media-upload/'>http://wordpress.org/extend/plugins/xml-rpc-extended-media-upload/</a></li>"+
//		            "</ol>"+
//		            "Then, please access admin page <a href='http://yourdomain/wp-admin/options-writing.php'>http://yourdomain/wp-admin/options-writing.php</a> and check enable XML_RPC"+
//		        "</p>"+
//		    "</body>"+
//		"</html>";
//		webView.loadData(customHtml, "text/html", "UTF-8");



	}
}