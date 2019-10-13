package org.fossasia.phimpme.accounts;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.android.volley.VolleyError;

import io.realm.RealmQuery;
import org.fossasia.phimpme.data.local.AccountDatabase;
import org.fossasia.phimpme.utilities.BasicCallBack;
import org.fossasia.phimpme.utilities.Constants;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/** Created by @codedsun on 09/Oct/2019 */
public class AccountViewModel extends AndroidViewModel {

  final int NEXTCLOUD_REQUEST_CODE = 3;
  final int OWNCLOUD_REQUEST_CODE = 9;
  final int RESULT_OK = 1;

  private AccountRepository accountRepository = new AccountRepository();
  public MutableLiveData<Boolean> error = new MutableLiveData<>();
  MutableLiveData<RealmQuery<AccountDatabase>> accountDetails = new MutableLiveData<>();
  public MutableLiveData<JSONArray> boards = new MutableLiveData<>();

  public AccountViewModel(@NonNull Application application) {
    super(application);
  }


  // Used to fetch all the current logged in accounts
  void fetchAccountDetails() {
    RealmQuery<AccountDatabase> accountDetails = accountRepository.fetchAllAccounts();
    if (accountDetails.findAll().size() > 0) {
      this.accountDetails.postValue(accountDetails);
    } else {
      error.postValue(true);
    }
  }

  // used to save nextcloud or owncloud details
  void saveOwnCloudOrNextCloudToken(
      int requestCode, String serverUrl, String userName, String password) {
    switch (requestCode) {
      case NEXTCLOUD_REQUEST_CODE:
        accountRepository.saveUsernamePasswordServerUrlForAccount(
            AccountDatabase.AccountName.NEXTCLOUD, serverUrl, userName, password);
        break;

      case OWNCLOUD_REQUEST_CODE:
        accountRepository.saveUsernamePasswordServerUrlForAccount(
            AccountDatabase.AccountName.OWNCLOUD, serverUrl, userName, password);
        break;
    }
  }

  // to save dropbox details
  void saveDropboxToken(String token) {
    accountRepository.saveUsernameAndToken(
        AccountDatabase.AccountName.DROPBOX, AccountDatabase.AccountName.DROPBOX.toString(), token);
  }

  // to save box details
  void saveBoxToken(String username, String token) {
    accountRepository.saveUsernameAndToken(AccountDatabase.AccountName.BOX, username, token);
  }

  // to save pintrest details
  void savePinterestToken(String username) {
    accountRepository.saveUsernameAndToken(AccountDatabase.AccountName.PINTEREST, username, "");
  }

  // to delete a specific account from database
  void deleteAccountFromDatabase(String accountName) {
    accountRepository.deleteAccount(accountName);
  }

  // to save imgur account details
  void saveImgurAccount(String username, String token) {
    accountRepository.saveUsernameAndToken(AccountDatabase.AccountName.IMGUR, username, token);
  }

  public void savePinterestAccount(String username, String token) {
    accountRepository.saveUsernameAndToken(AccountDatabase.AccountName.PINTEREST, username, token);
  }

  public void getUserPinterestBoards(){
    accountRepository.getPinterestBoards((status, data) -> {
      if(status == Constants.SUCCESS) {
        try {
          boards.postValue(((JSONObject)data).getJSONArray("data"));
        } catch (JSONException e) {
          e.printStackTrace();
          error.postValue(true);
        }
      }else{
        if(data instanceof VolleyError) {
          error.postValue(true);
        }else{
          error.postValue(false);
        }
      }
    }, getApplication());
  }

  public void uploadImageToBoards(String image, String note, String board){
    accountRepository.uploadImageToPinterest(new BasicCallBack() {
      @Override
      public void callBack(int status, Object data) {
      }
    },getApplication(),image, note, board);
  }

}
