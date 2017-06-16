package vn.mbm.phimp.me.accounts;

import org.jetbrains.annotations.NotNull;

import io.realm.Realm;
import io.realm.RealmResults;
import vn.mbm.phimp.me.base.BasePresenter;
import vn.mbm.phimp.me.data.AccountDatabase;
import vn.mbm.phimp.me.data.DatabaseHelper;

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
