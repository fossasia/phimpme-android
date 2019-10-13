package org.fossasia.phimpme.accounts;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Base64;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;

import io.realm.Realm;
import io.realm.RealmQuery;

import org.fossasia.phimpme.data.local.AccountDatabase;
import org.fossasia.phimpme.data.local.DatabaseHelper;
import org.fossasia.phimpme.utilities.BasicCallBack;
import org.fossasia.phimpme.utilities.Constants;
import org.fossasia.phimpme.utilities.MultipartRequest;
import org.fossasia.phimpme.utilities.Utils;
import org.fossasia.phimpme.utilities.VolleyClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by @codedsun on 09/Oct/2019
 */
class AccountRepository {

    private Realm realm = Realm.getDefaultInstance();
    private DatabaseHelper databaseHelper = new DatabaseHelper(realm);

    // Fetches the details of all accounts
    RealmQuery<AccountDatabase> fetchAllAccounts() {
        return databaseHelper.fetchAccountDetails();
    }

    // saves username, password and serverUrl for an account in database
    void saveUsernamePasswordServerUrlForAccount(
            AccountDatabase.AccountName accountName, String serverUrl, String username, String password) {
        realm.beginTransaction();
        AccountDatabase account = realm.createObject(AccountDatabase.class, accountName.toString());
        account.setServerUrl(serverUrl);
        account.setUsername(username);
        account.setPassword(password);
        realm.commitTransaction();
    }

    // saves username and token for an account in database
    void saveUsernameAndToken(
            AccountDatabase.AccountName accountName, String username, String token) {
        realm.beginTransaction();
        AccountDatabase account = realm.createObject(AccountDatabase.class, accountName.toString());
        account.setUsername(username);
        account.setToken(token);
        realm.commitTransaction();
    }

    // deletes an account from database
    void deleteAccount(String accountName) {
        databaseHelper.deleteSignedOutAccount(accountName);
    }

//    void uploadImageToPinterest(BasicCallBack callBack, Context context, String imagePath, String note, String board) {
//        AccountDatabase pinterestAccount = databaseHelper.getAccountByName(AccountDatabase.AccountName.PINTEREST.name());
//        if (pinterestAccount != null && pinterestAccount.getToken() != null) {
//            JSONObject params = new JSONObject();
//            ByteArrayOutputStream baos = new ByteArrayOutputStream();
//            Bitmap bitmap =  Utils.getBitmapFromPath(imagePath);
//            int numPixels = bitmap.getHeight() * bitmap.getWidth();
//            if(numPixels > 3150000) {
//                Log.e("sUneet - photo error","Image too large");
//                return;
//            }
//            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
//            String b64Str = Base64.encodeToString(baos.toByteArray(), Base64.NO_WRAP);
//            try {
//                params.put("image_base64", b64Str);
//                params.put("note", note);
//                params.put("board", "suneetbond/vrindavan");
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//            JsonObjectRequest uploadImageRequest = new JsonObjectRequest(Request.Method.POST,Constants.PINTEREST_POST_CREATE_PIN,params, response -> {
//                VolleyLog.e("Suneet - reponse",response.toString());
//            },
//                    error -> {
//                        VolleyLog.e("Suneet - error",error.toString());
//                    });
//
//            uploadImageRequest.setRetryPolicy(new DefaultRetryPolicy( 50000, 5, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
//            VolleyClient.getInstance(context).getRequestQueue().add(uploadImageRequest);
//
//        }else{
//            //No account found for pinterest
//            callBack.callBack(Constants.FAIL, null);
//        }
//
//
//    }

    void uploadImageToPinterest(BasicCallBack callBack, Context context, String imagePath, String note, String board) {
        AccountDatabase pinterestAccount = databaseHelper.getAccountByName(AccountDatabase.AccountName.PINTEREST.name());
        if (pinterestAccount != null && pinterestAccount.getToken() != null) {
            byte[] byteFile = new byte[]{};
            try {
                byteFile = Files.readAllBytes(Paths.get(imagePath));
            } catch (IOException e) {
                e.printStackTrace();
            }
            MultipartRequest multipartRequest = new MultipartRequest();
            MultipartRequest.MultipartRequestBuild  uploadRequst = multipartRequest.sendMultipartRequest(Request.Method.POST,
                    Constants.PINTEREST_POST_CREATE_PIN, byteFile, null, "Suneetsro", new Response.Listener<NetworkResponse>() {
                        @Override
                        public void onResponse(NetworkResponse response) {
                         VolleyLog.e("suneet - repsonse");
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            VolleyLog.e("suneet - error");
                        }
                    });
            uploadRequst.setRetryPolicy(new DefaultRetryPolicy(50000 , 5, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            VolleyClient.getInstance(context).getRequestQueue().add(uploadRequst);

        }else{
            //No account found for pinterest
            callBack.callBack(Constants.FAIL, null);
        }


    }

    //get pinterest boards of user
    void getPinterestBoards(BasicCallBack callBack, Context context) {
        AccountDatabase pinterestAccount = databaseHelper.getAccountByName(AccountDatabase.AccountName.PINTEREST.name());
        if (pinterestAccount != null && pinterestAccount.getToken() != null) {
            JsonObjectRequest pinterestBoardsRequest = new JsonObjectRequest(Request.Method.GET, Constants.PINTEREST_GET_USER_BOARDS + pinterestAccount.getToken(),
                    response -> {
                        callBack.callBack(Constants.SUCCESS, response);
                    }, error -> {
                callBack.callBack(Constants.FAIL, error);
            });
            VolleyClient.getInstance(context).getRequestQueue().add(pinterestBoardsRequest);
        } else {
            //No account found for pinterest
            callBack.callBack(Constants.FAIL, null);
        }

    }

    //Creates Pinterest Board
    void createBoard(String name, Context context, BasicCallBack basicCallBack) {
        JSONObject params = new JSONObject();
        try {
            params.put("name", name);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest createBoardRequest = new JsonObjectRequest(Request.Method.POST, Constants.PINTEREST_POST_CREATE_BOARD, params,
                response -> {

                },
                error -> {
                }

                );

        VolleyClient.getInstance(context).getRequestQueue().add(createBoardRequest);
    }

}
