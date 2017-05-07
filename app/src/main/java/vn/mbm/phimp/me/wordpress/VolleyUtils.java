package vn.mbm.phimp.me.wordpress;

import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;
import org.wordpress.android.util.JSONUtils;

import java.io.UnsupportedEncodingException;

public class VolleyUtils {

    public static int statusCodeFromVolleyError(VolleyError volleyError) {
        if (volleyError == null || volleyError.networkResponse == null) {
            return 0;
        }
        return volleyError.networkResponse.statusCode;
    }

    /*
     * Attempts to return JSON from a volleyError - useful for WP REST API failures, which often
     * contain JSON in the response
     */
    public static JSONObject volleyErrorToJSON(VolleyError volleyError) {
        if (volleyError == null || volleyError.networkResponse == null || volleyError.networkResponse.data == null
                || volleyError.networkResponse.headers == null) {
            return null;
        }

        String contentType = volleyError.networkResponse.headers.get("Content-Type");
        if (contentType == null || !contentType.equals("application/json")) {
            return null;
        }

        try {
            String response = new String(volleyError.networkResponse.data, "UTF-8");
            JSONObject json = new JSONObject(response);
            return json;
        } catch (UnsupportedEncodingException e) {
            return null;
        } catch (JSONException e) {
            return null;
        }
    }

}
