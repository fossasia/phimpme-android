package org.fossasia.phimpme.accounts;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import org.fossasia.phimpme.base.BasePresenter;
import org.fossasia.phimpme.data.local.AccountDatabase;
import org.fossasia.phimpme.data.local.DatabaseHelper;
import org.jetbrains.annotations.NotNull;

/** Created by pa1pal on 10/6/17. */
public class AccountPresenter extends BasePresenter<AccountContract.View>
    implements AccountContract.Presenter {

  public DatabaseHelper databaseHelper;
  private Realm realm = Realm.getDefaultInstance();

  public AccountPresenter(Realm realm) {
    databaseHelper = new DatabaseHelper(realm);
  }

  @Override
  public void loadFromDatabase() {
    handleResults(databaseHelper.fetchAccountDetails());
  }

  @Override
  public void handleResults(@NotNull RealmQuery<AccountDatabase> accountDetails) {
    if (accountDetails.findAll().size() != 0) {
      getMvpView().setUpAdapter(accountDetails);
      getMvpView().showComplete();
    } else {
      getMvpView().showError();
      getMvpView().showComplete();
    }
  }

  /**
   * This function check if the selected account is already existed.
   *
   * @param s Name of the account from accountList e.g. Twitter
   * @return true is existed, false otherwise
   */
  @Override
  public boolean checkAlreadyExist(AccountDatabase.AccountName s) {

    // Query in the realm database
    RealmQuery<AccountDatabase> query = realm.where(AccountDatabase.class);

    // Checking if string equals to is exist or not
    query.equalTo("name", s.toString());
    RealmResults<AccountDatabase> result1 = query.findAll();

    // Here checking if count of that values is greater than zero
    return (result1.size() > 0) ? true : false;
  }
}
