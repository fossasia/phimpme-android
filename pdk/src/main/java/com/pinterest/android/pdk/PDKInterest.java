package com.pinterest.android.pdk;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by sahilm on 6/3/15.
 */
public class PDKInterest extends PDKModel {

    private String uid;
    private String name;


    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getUid() {
        return uid;
    }

    public String getName() {
        return name;
    }

    public static PDKInterest makeInterest(Object obj) {
        PDKInterest interest = new PDKInterest();
        try {
            if (obj instanceof JSONObject) {
                JSONObject dataObj = (JSONObject)obj;
                if (dataObj.has("id")) {
                    interest.setUid(dataObj.getString("id"));
                }
                if (dataObj.has("name")) {
                    interest.setName(dataObj.getString("name"));
                }
            }
        } catch (JSONException e) {
            Utils.loge("PDK: PDKInterest parse JSON error %s", e.getLocalizedMessage());
        }
        return interest;
    }

    public static List<PDKInterest> makeInterestList(Object obj) {
        List<PDKInterest> interestList = new ArrayList<PDKInterest>();
        try {
            if (obj instanceof JSONArray) {

                JSONArray jAarray = (JSONArray)obj;
                int size = jAarray.length();
                for (int i = 0; i < size; i++) {
                    JSONObject dataObj = jAarray.getJSONObject(i);
                    interestList.add(makeInterest(dataObj));
                }
            }
        } catch (JSONException e) {
            Utils.loge("PDK: PDKInterst parse JSON error %s", e.getLocalizedMessage());
        }
        return interestList;
    }

}
