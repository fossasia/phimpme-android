package org.fossasia.phimpme.accounts;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import io.realm.RealmQuery;
import org.fossasia.phimpme.data.local.AccountDatabase;

/** Created by @codedsun on 09/Oct/2019 */
public class AccountViewModel extends ViewModel {

  final int NEXTCLOUD_REQUEST_CODE = 3;
  final int OWNCLOUD_REQUEST_CODE = 9;
  final int RESULT_OK = 1;

  private AccountRepository accountRepository = new AccountRepository();
  MutableLiveData<Boolean> error = new MutableLiveData<>();
  MutableLiveData<RealmQuery<AccountDatabase>> accountDetails = new MutableLiveData<>();

  public AccountViewModel() {}

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
}
