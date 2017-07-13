package com.pinterest.android.pdk;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;


import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class PDKRequest extends JsonObjectRequest {

    private PDKCallback _callback;
    private Map<String, String> _headers = null;

    public PDKRequest(int method, String url, JSONObject jsonRequest,
        Response.Listener<JSONObject> listener,
        Response.ErrorListener errorListener) {
        super(method, url, jsonRequest, listener, errorListener);
    }

    public PDKRequest(int method, String url, JSONObject object, PDKCallback callback, Map<String, String> headers) {
        super(method, url, object, callback, callback);
        _callback = callback;
        _headers = headers;
    }


    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        if (null == _headers || _headers.equals(Collections.emptyMap())) {
            _headers = new HashMap<String, String>();
        }
        return _headers;
    }


    @Override
    protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
        _callback.setResponseHeaders(response.headers);
        _callback.setStatusCode(response.statusCode);
        return super.parseNetworkResponse(response);
    }


}
