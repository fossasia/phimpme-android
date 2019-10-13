package org.fossasia.phimpme.utilities;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import okhttp3.MultipartBody;

/**
 * Created by @codedsun on 18/Oct/2019
 */

public class MultipartRequest {

    private String boundary = "apiclient-" + System.currentTimeMillis();
    private String mimeType = "multipart/form-data;boundary="+boundary;
    String twoHyphens = "--";
    String lineEnd = "\r\n";

    public MultipartRequestBuild sendMultipartRequest(int method, String url, byte[] fileData,Map<String,String> params, String fileName, Response.Listener<NetworkResponse> listener,
                                      Response.ErrorListener errorListener) {

        // Create multi part byte array
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(bos);
        buildMultipartContent(dos, fileData, fileName);
        byte[] multipartBody = bos.toByteArray();

        // Request header, if needed
        HashMap<String, String> headers = new LinkedHashMap<>();
        headers.put("content-Type", "multipart/form-data,boundary="+twoHyphens+boundary);
        headers.put("Content-Type", "application/x-www-form-urlencoded");

        return new MultipartRequestBuild(
                method,
                url,
                params,
                errorListener,
                listener,
                headers,
                mimeType,
                multipartBody);


    }

    private void buildMultipartContent(DataOutputStream dos, byte[] fileData, String fileName) {
        try {
            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"image\"; filename="+fileName+lineEnd);
            dos.writeBytes(lineEnd);
            dos.write(fileData);
            dos.writeBytes(lineEnd);
            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; note=\"suneet\"");
            dos.writeBytes(lineEnd);
            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; board=\"suneetbond/vrindavan\"");
            dos.writeBytes(lineEnd);
            dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
            VolleyLog.e("Suneet - Multipart Created");
        } catch (IOException e) {
            VolleyLog.e("Suneet - Multipart Failed");
            e.printStackTrace();
        }
    }

    public class MultipartRequestBuild extends Request<NetworkResponse> {

        private HashMap<String, String> headers;
        private byte[] multipartBody;
        private Response.Listener<NetworkResponse> listener;
        private Map<String,String> params;
        String mimeType;

        public MultipartRequestBuild(int method,
                                     String url,
                                     Map<String, String> params,
                                     Response.ErrorListener errorListener,
                                     Response.Listener<NetworkResponse> listener,
                                     HashMap<String, String> headers,
                                     String mimeType,
                                     byte[] multipartBody) {
            super(method, url, errorListener);
            this.listener = listener;
            this.headers = headers;
            this.mimeType = mimeType;
            this.params = params;
            this.multipartBody = multipartBody;
        }

        @Override
        public Map<String, String> getHeaders() throws AuthFailureError {
            if (headers.isEmpty())
                return super.getHeaders();
            else
                return headers;
        }

        @Override
        protected Map<String, String> getParams() throws AuthFailureError {
            return super.getParams();
//            else return params;
        }

        @Override
        public String getBodyContentType() {
            return mimeType;
        }

        @Override
        public byte[] getBody() throws AuthFailureError {
            return multipartBody;
        }

        @Override
        protected Response<NetworkResponse> parseNetworkResponse(NetworkResponse response) {
            try {
                return Response.success(response, HttpHeaderParser.parseCacheHeaders(response));
            } catch (Exception e) {
                return Response.error(new ParseError(e));
            }
        }

        @Override
        protected void deliverResponse(NetworkResponse response) {
            listener.onResponse(response);

        }
    }
}