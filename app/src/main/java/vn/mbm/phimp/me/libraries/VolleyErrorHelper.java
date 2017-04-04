package vn.mbm.phimp.me.libraries;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;

public class VolleyErrorHelper {
    /**
    * Returns appropriate message which is to be displayed to the user 
    * against the specified error object.
    * 
    * @param error
    * @param context
    * @return
    */
	 public static String getMessage(VolleyError error, Context context) {
	     if (error instanceof TimeoutError) {
	         return "Connection timed out!";
	     }
	     else if (isServerProblem(error)) {
	         return "No response from server!";
	     }
	     else if (isNetworkProblem(error)) {
	         return "Internet connection problem!";
	     }
	     return error.getMessage();
	 }

	 /**
	 * Determines whether the error is related to server
	 * @param error
	 * @return
	 */
	 private static boolean isServerProblem(Object error) {
	     return (error instanceof ServerError) || (error instanceof AuthFailureError);
	 }
	 
	 /**
	 * Determines whether the error is related to network
	 * @param error
	 * @return
	 */
	 private static boolean isNetworkProblem(Object error) {
	     return (error instanceof NetworkError) || (error instanceof NoConnectionError);
	 }
 
}
