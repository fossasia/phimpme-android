package com.pinterest.android.pdk;

import android.util.Log;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class Utils {

    private static final String TAG = "PDK";
    private static DateFormat _dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

    public static <T> boolean isEmpty(Collection<T> c) {
        return (c == null) || (c.size() == 0);
    }

    public static boolean isEmpty(Map m) {
        return (m == null) || (m.size() == 0);
    }

    public static boolean isEmpty(String s) {
        return s == null || s.length() == 0;
    }

    /**
     * Log errors
     *
     * @param s base String
     * @param params Objects to format in
     */
    public static void loge(String s, Object... params) {
        if (PDKClient.isDebugMode())
            Log.e(TAG, String.format(s, params));
    }

    /**
     * Log info
     *
     * @param s base String
     * @param params Objects to format in
     */
    public static void log(String s, Object... params) {
        if (PDKClient.isDebugMode())
            Log.i(TAG, String.format(s, params));
    }

    public static DateFormat getDateFormatter() {
        return _dateFormat;
    }

    public static String getUrlWithQueryParams(String url, List<NameValuePair> params) {
        if (url == null) {
            return null;
        }

        url = url.replace(" ", "%20");

        if(!url.endsWith("?"))
            url += "?";

        if (params != null && params.size() > 0) {
            String paramString = URLEncodedUtils.format(params, "utf-8");
            url += paramString;
        }
        return url;
    }
}
