package vn.mbm.phimp.me.accounts

import vn.mbm.phimp.me.base.MvpView


/**
 * Created by pa1pal on 6/6/17.
 *
 * Contract class have two interfaces one for View and one for Presenter. We here declare all the
 * required functions.
 */

class AccountContract {
    internal interface View : MvpView{

        fun setUpRecyclerView()

        fun showError(message: String)

        fun showComplete()
    }

    internal interface Presenter {

        fun loadFromDatabase()
    }
}