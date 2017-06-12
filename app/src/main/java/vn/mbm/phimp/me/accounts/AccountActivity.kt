package vn.mbm.phimp.me.accounts

import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import com.twitter.sdk.android.core.identity.TwitterLoginButton
import io.realm.Realm
import io.realm.RealmResults
import vn.mbm.phimp.me.R
import vn.mbm.phimp.me.base.PhimpmeProgressBarHandler
import vn.mbm.phimp.me.base.ThemedActivity
import vn.mbm.phimp.me.data.AccountDatabase
import vn.mbm.phimp.me.leafpic.util.ThemeHelper
import vn.mbm.phimp.me.utilities.ActivitySwitchHelper

class AccountActivity : ThemedActivity(), AccountContract.View {

    private var toolbar: Toolbar? = null
    private var themeHelper: ThemeHelper? = null
    private var accountsRecyclerView: RecyclerView? = null
    private var accountAdapter: AccountAdapter? = null
    private var accountPresenter: AccountPresenter? = null
    private var twitterLogin: TwitterLoginButton? = null
    private var realm: Realm = Realm.getDefaultInstance()
    private var realmResult: RealmResults<AccountDatabase>? = null
    private var phimpmeProgressBarHandler: PhimpmeProgressBarHandler ?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        themeHelper = ThemeHelper(this)
        accountAdapter = AccountAdapter()
        accountPresenter = AccountPresenter(realm)
        phimpmeProgressBarHandler = PhimpmeProgressBarHandler(this)
        accountPresenter!!.attachView(this)
        toolbar = findViewById(R.id.toolbar) as Toolbar
        accountsRecyclerView = findViewById(R.id.accounts_recycler_view) as RecyclerView
        setSupportActionBar(toolbar)
        toolbar!!.setPopupTheme(themeHelper!!.getPopupToolbarStyle());
        toolbar!!.setBackgroundColor(themeHelper!!.getPrimaryColor());
        setUpRecyclerView()
        // Calling presenter function to load data from database
        accountPresenter!!.loadFromDatabase()
        getSupportActionBar()!!.setTitle(R.string.title_account)
        toolbar!!.popupTheme = themeHelper!!.popupToolbarStyle
        toolbar!!.setBackgroundColor(themeHelper!!.primaryColor)
        supportActionBar!!.setTitle(R.string.title_account)
        phimpmeProgressBarHandler!!.show()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_accounts_activity, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_add_account -> {
                val fragmentManager = fragmentManager
                val accountsPicker = AccountPickerFragment().newInstance("Accounts Picker")
                accountsPicker.show(fragmentManager, "Accounts Picker")
            }
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    override fun getContentViewId(): Int {
        return R.layout.activity_accounts
    }

    override fun getNavigationMenuItemId(): Int {
        return R.id.navigation_accounts
    }

    override fun setUpRecyclerView() {
        val layoutManager = LinearLayoutManager(this)
        accountsRecyclerView!!.setLayoutManager(layoutManager)
        accountsRecyclerView!!.setAdapter(accountAdapter)
    }

    override fun setUpAdapter(accountDetails: RealmResults<AccountDatabase>) {
        this.realmResult = accountDetails
        accountAdapter!!.setResults(accountDetails)
    }

    override fun showError() {
        Toast.makeText(this, "No account signed in", LENGTH_SHORT).show()
    }

    override fun showComplete() {
        phimpmeProgressBarHandler!!.hide()
    }

    override fun onResume() {
        super.onResume()
        ActivitySwitchHelper.setContext(this)
        setNavigationBarColor(ThemeHelper.getPrimaryColor(this))
        setStatusBarColor()
        accountPresenter!!.loadFromDatabase()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val fragmentManager = fragmentManager
        val accountsPicker = AccountPickerFragment().newInstance("AP")
        accountsPicker.onActivityResult(requestCode, resultCode, data)
    }
}
