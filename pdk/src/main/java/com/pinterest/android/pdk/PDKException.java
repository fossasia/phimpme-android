package com.pinterest.android.pdk;

import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

public class PDKException extends Exception {

    static final long serialVersionUID = 1;
    private int _stausCode = -1;
    private String _detailMessage = "";
    protected String _baseUrl;
    protected String _method;

    public PDKException() {
        super();
    }

    public PDKException(String message) {
        super(message);
        _detailMessage = message;
    }

    public PDKException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public PDKException(VolleyError error) {
        super();
        String message = "";
        if (error != null && error.networkResponse != null && error.networkResponse.data != null) {
            message = new String(error.networkResponse.data);
            _detailMessage = message;
        }

        if (message.length() > 0 && message.startsWith("{")) {
            try {
                JSONObject errObj = new JSONObject(message);

                if (errObj.has("status")) {
                    _stausCode = errObj.getInt("status");
                }
                if (errObj.has("messsage")) {
                    _detailMessage = errObj.getString("message");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void setStausCode(int stausCode) {
        _stausCode = stausCode;
    }

    public void setDetailMessage(String detailMessage) {
        _detailMessage = detailMessage;
    }

    public int getStausCode() {

        return _stausCode;
    }

    public String getDetailMessage() {
        return _detailMessage;
    }

}