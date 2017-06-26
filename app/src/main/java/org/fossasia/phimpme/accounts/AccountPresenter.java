package org.fossasia.phimpme.accounts;

import org.jetbrains.annotations.NotNull;

import io.realm.Realm;
import io.realm.RealmResults;
import org.fossasia.phimpme.base.BasePresenter;
import org.fossasia.phimpme.data.local.AccountDatabase;
import org.fossasia.phimpme.data.local.DatabaseHelper;

/**
 * Created by pa1pal on 10/6/17.
 */

public class AccountPresenter extends BasePresenter<AccountContract.View>
        implements AccountContract.Presenter {

    public DatabaseHelper databaseHelper;

    public AccountPresenter(Realm realm) {
        databaseHelper = new DatabaseHelper(realm);
    }

    @Override
    public void loadFromDatabase() {
        handleResults(databaseHelper.fetchAccountDetails());
    }

    @Override
    public void handleResults(@NotNull RealmResults<AccountDatabase> accountDetails) {
        if (accountDetails.size() != 0){
            getMvpView().setUpAdapter(accountDetails);
            getMvpView().showComplete();
        } else {
            getMvpView().showError();
            getMvpView().showComplete();
        }
    }
}
