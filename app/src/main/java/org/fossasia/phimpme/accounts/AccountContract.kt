package org.fossasia.phimpme.accounts

import io.realm.RealmResults
import org.fossasia.phimpme.base.MvpView
import org.fossasia.phimpme.data.AccountDatabase


/**
 * Created by pa1pal on 6/6/17.
 *
 * Contract class have two interfaces one for View and one for Presenter. We here declare all the
 * required functions.
 */

class AccountContract {
    internal interface View : MvpView{

        /**
         * Setting up the recyclerView. The layout manager, decorator etc.
         */
        fun setUpRecyclerView()

        /**
         * Account Presenter calls this function after taking data from Database Helper Class
         */
        fun setUpAdapter(accountDetails: RealmResults<AccountDatabase>)

        /**
         * Shows the error log
         */
        fun showError()
    }

    internal interface Presenter {

        /**
         * function to load data from database, using Database Helper class
         */
        fun loadFromDatabase()

        /**
         * setting up the recyclerView adapter from here
         */
        fun handleResults(accountDetails: RealmResults<AccountDatabase>)
    }
}