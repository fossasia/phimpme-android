package com.pinterest.android.pdk;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONObject;


import java.util.HashMap;
import java.util.Map;

public class PDKCallback implements Response.Listener<JSONObject>, Response.ErrorListener {

    private int _statusCode;
    private Map<String, String> _responseHeaders;
    private String _path;
    private HashMap<String, String> _params;

    @Override
    public void onResponse(JSONObject response) {
        try {
            onSuccess(response);
        } catch (Exception e) {
        }
    }

    public void onErrorResponse(VolleyError error) {
        onFailure(new PDKException(error));
    }

    public void onSuccess(JSONObject response) {
        final PDKResponse apiResponse = new PDKResponse(response);
        apiResponse.setStatusCode(_statusCode);
        apiResponse.setPath(_path);
        apiResponse.setParams(_params);
        onSuccess(apiResponse);
    }

    public void onSuccess(PDKResponse response) {
    }

    public void onFailure(PDKException exception) {
    }

    public void setResponseHeaders(Map<String, String> map) {
        _responseHeaders = map;
    }

    public void setStatusCode(int code) {
        _statusCode = code;
    }

    public void setPath(String path) {
        _path = path;
    }

    public void setParams(HashMap<String, String> params) {
        _params = params;
    }
}
