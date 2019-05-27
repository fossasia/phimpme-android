package org.fossasia.phimpme.accounts;

import android.content.Context;
import io.realm.RealmQuery;
import org.fossasia.phimpme.base.MvpView;
import org.fossasia.phimpme.data.local.AccountDatabase;

/**
 * Created by pa1pal on 6/6/17.
 *
 * <p>Contract class have two interfaces one for View and one for Presenter. We here declare all the
 * required functions.
 */
public class AccountContract {
  public interface View extends MvpView {

    /** Setting up the recyclerView. The layout manager, decorator etc. */
    void setUpRecyclerView();

    /** Account Presenter calls this function after taking data from Database Helper Class */
    void setUpAdapter(RealmQuery<AccountDatabase> accountDetails);

    /** Shows the error log */
    void showError();

    /** Get the context */
    Context getContext();
  }

  interface Presenter {
    /** function to load data from database, using Database Helper class */
    void loadFromDatabase();

    /** setting up the recyclerView adapter from here */
    void handleResults(RealmQuery<AccountDatabase> accountDetails);

    /**
     * This function check if the selected account is already existed.
     *
     * @param s Name of the account from accountList e.g. Twitter
     * @return true is existed, false otherwise
     */
    boolean checkAlreadyExist(AccountDatabase.AccountName s);
  }
}
