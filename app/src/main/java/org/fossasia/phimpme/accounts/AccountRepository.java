package org.fossasia.phimpme.accounts;

import io.realm.Realm;
import io.realm.RealmQuery;
import org.fossasia.phimpme.data.local.AccountDatabase;
import org.fossasia.phimpme.data.local.DatabaseHelper;

/** Created by @codedsun on 09/Oct/2019 */
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
}
