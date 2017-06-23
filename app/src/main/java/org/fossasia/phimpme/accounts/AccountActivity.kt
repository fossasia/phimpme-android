package org.fossasia.phimpme.accounts

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import io.realm.Realm
import io.realm.RealmResults
import org.fossasia.phimpme.R
import org.fossasia.phimpme.base.PhimpmeProgressBarHandler
import org.fossasia.phimpme.base.RecyclerItemClickListner
import org.fossasia.phimpme.base.ThemedActivity
import org.fossasia.phimpme.data.local.AccountDatabase
import org.fossasia.phimpme.data.local.DatabaseHelper
import org.fossasia.phimpme.leafpic.util.ThemeHelper
import org.fossasia.phimpme.utilities.ActivitySwitchHelper

class AccountActivity : ThemedActivity(), AccountContract.View,
        RecyclerItemClickListner.OnItemClickListener {

    private var toolbar: Toolbar? = null
    private var themeHelper: ThemeHelper? = null
    private var accountsRecyclerView: RecyclerView? = null
    private var accountAdapter: AccountAdapter? = null
    private var accountPresenter: AccountPresenter? = null
    private var realm: Realm = Realm.getDefaultInstance()
    private var realmResult: RealmResults<AccountDatabase>? = null
    private var phimpmeProgressBarHandler: PhimpmeProgressBarHandler? = null
    private var databaseHelper: DatabaseHelper? = null

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
        databaseHelper = DatabaseHelper(realm)
        toolbar!!.setPopupTheme(themeHelper!!.getPopupToolbarStyle())
        toolbar!!.setBackgroundColor(themeHelper!!.getPrimaryColor())
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
        return R.id.navigation_camera
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
        Toast.makeText(this, getString(R.string.no_account_signed_in), LENGTH_SHORT).show()
    }

    override fun showComplete() {
        phimpmeProgressBarHandler!!.hide()
    }

    override fun onItemClick(childView: View?, position: Int) {
        val signOutDialog = AlertDialog.Builder(this)
        signOutDialog.setMessage(getString(R.string.sign_out_dialog_message) + realmResult
                ?.get(position)?.name)

                .setTitle(getString(R.string.sign_out_dialog_title))
                .setPositiveButton(R.string.yes_action, DialogInterface.OnClickListener {
                    dialog, which ->
                    databaseHelper!!.deleteSignedOutAccount(realmResult!!.get(position).name)
                    accountAdapter!!.notifyDataSetChanged()
                    accountPresenter!!.loadFromDatabase()

                })
                .setNegativeButton(R.string.no_action, DialogInterface.OnClickListener {
                    dialog, which ->

                })

        var dialog = signOutDialog.create()
        dialog.show()
    }

    override fun onItemLongPress(childView: View?, position: Int) {
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
        val accountsPicker = AccountPickerFragment().newInstance("AP")
        accountsPicker.onActivityResult(requestCode, resultCode, data)
    }
}
