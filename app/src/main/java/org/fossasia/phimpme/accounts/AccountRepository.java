package org.fossasia.phimpme.accounts;

import android.graphics.Bitmap;
import io.realm.Realm;
import io.realm.RealmQuery;
import java.io.ByteArrayOutputStream;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import org.fossasia.phimpme.data.local.AccountDatabase;
import org.fossasia.phimpme.data.local.DatabaseHelper;
import org.fossasia.phimpme.share.pinterest.PinterestBoardsResp;
import org.fossasia.phimpme.share.pinterest.PinterestUploadImgResp;
import org.fossasia.phimpme.utilities.BasicCallBack;
import org.fossasia.phimpme.utilities.Constants;
import org.fossasia.phimpme.utilities.PinterestApi;
import org.fossasia.phimpme.utilities.RetrofitClient;
import org.fossasia.phimpme.utilities.Utils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/** Created by @codedsun on 09/Oct/2019 */
class AccountRepository {

  private Realm realm = Realm.getDefaultInstance();
  private DatabaseHelper databaseHelper = new DatabaseHelper(realm);
  private PinterestApi pinterestApi =
      RetrofitClient.getRetrofitClient(Constants.PINTEREST_BASE_URL).create(PinterestApi.class);

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

  void uploadImageToPinterest(BasicCallBack callBack, String imagePath, String note, String board) {
    AccountDatabase pinterestAccount =
        databaseHelper.getAccountByName(AccountDatabase.AccountName.PINTEREST.name());
    if (pinterestAccount != null && pinterestAccount.getToken() != null) {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      Bitmap bitmap = Utils.getBitmapFromPath(imagePath);
      int numPixels = bitmap.getHeight() * bitmap.getWidth();
      if (numPixels > 3150000) {
        PinterestUploadImgResp resp = new PinterestUploadImgResp();
        resp.setMessage("Image Size too large");
        callBack.callBack(Constants.FAIL, resp);
        return;
      }
      bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
      RequestBody reqFile =
          RequestBody.create(baos.toByteArray(), MediaType.parse("multipart/form-data"));
      MultipartBody.Part multipartBody =
          MultipartBody.Part.createFormData("image", imagePath, reqFile);
      pinterestApi
          .uploadImageToPinterest(pinterestAccount.getToken(), note, board, multipartBody)
          .enqueue(
              new Callback<PinterestUploadImgResp>() {
                @Override
                public void onResponse(
                    Call<PinterestUploadImgResp> call, Response<PinterestUploadImgResp> response) {
                  if (response.isSuccessful()) {
                    callBack.callBack(Constants.SUCCESS, response.body());
                  } else {
                    PinterestUploadImgResp resp = new PinterestUploadImgResp();
                    resp.setMessage(response.message());
                    callBack.callBack(Constants.FAIL, resp);
                  }
                }

                @Override
                public void onFailure(Call<PinterestUploadImgResp> call, Throwable t) {
                  PinterestUploadImgResp resp = new PinterestUploadImgResp();
                  resp.setMessage(t.toString());
                  callBack.callBack(Constants.FAIL, resp);
                }
              });
    } else {
      // No account found for pinterest
      callBack.callBack(Constants.FAIL, null);
    }
  }

  // get pinterest boards of user
  void getPinterestBoards(BasicCallBack callBack) {
    AccountDatabase pinterestAccount =
        databaseHelper.getAccountByName(AccountDatabase.AccountName.PINTEREST.name());
    if (pinterestAccount != null && pinterestAccount.getToken() != null) {
      pinterestApi
          .getUserBoards(pinterestAccount.getToken())
          .enqueue(
              new Callback<PinterestBoardsResp>() {
                @Override
                public void onResponse(
                    Call<PinterestBoardsResp> call, Response<PinterestBoardsResp> response) {
                  if (response.body() != null && response.isSuccessful()) {
                    callBack.callBack(Constants.SUCCESS, response.body());
                  } else {
                    callBack.callBack(Constants.FAIL, "Unable to get Boards");
                  }
                }

                @Override
                public void onFailure(Call<PinterestBoardsResp> call, Throwable t) {
                  callBack.callBack(Constants.FAIL, null);
                }
              });
    }
  }
}
