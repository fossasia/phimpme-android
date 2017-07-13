package com.pinterest.android.pdk;

import org.json.JSONException;
import org.json.JSONObject;


import java.util.HashMap;
import java.util.List;

public class PDKResponse {


    protected Object _data;
    protected String _path;
    protected HashMap<String, String> _params;
    protected String _cursor = null;
    protected int _statusCode = -1;

    public PDKResponse(JSONObject obj) {
        if (obj == null)
            return;

        if (obj.has("data")) {
            try {
                setData(obj.get("data"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        if (obj.has("page")) {
            try {
                JSONObject pageObj = obj.getJSONObject("page");
                if (pageObj.has("cursor")) {
                    _cursor = pageObj.getString("cursor");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean isValid() {
        return _data != null;
    }
    public PDKPin getPin() {
        return PDKPin.makePin(_data);
    }

    public List<PDKPin> getPinList() {
        return PDKPin.makePinList(_data);
    }

    public PDKUser getUser() {
        return PDKUser.makeUser(_data);
    }

    public List<PDKUser> getUserList() {
        return PDKUser.makeUserList(_data);
    }

    public PDKBoard getBoard() {
        return PDKBoard.makeBoard(_data);
    }

    public List<PDKBoard> getBoardList() {
        return PDKBoard.makeBoardList(_data);
    }

    public PDKInterest getInterest() {
        return PDKInterest.makeInterest(_data);
    }

    public List<PDKInterest> getInterestList() {
        return PDKInterest.makeInterestList(_data);
    }

    public void loadNext(PDKCallback callback) {
        _params.put(PDKClient.PDK_QUERY_PARAM_CURSOR, _cursor);
        PDKClient.getInstance().getPath(_path, _params, callback);
    }

    public boolean hasNext() {
        return _cursor != null && _cursor.length() > 0 && !_cursor.equalsIgnoreCase("null");
    }

    //Setter & Getters

    public void setData(Object data) {
        _data = data;
    }

    public Object getData() {
        return _data;
    }

    public final void setStatusCode(int code) {
        _statusCode = code;
    }

    public final int getStatusCode() {
        return _statusCode;
    }

    public void setPath(String path) { _path = path; }

    public void setParams(HashMap<String, String> map) { _params = map; }
}
