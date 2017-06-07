package vn.mbm.phimp.me.accounts

import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import vn.mbm.phimp.me.R
import vn.mbm.phimp.me.base.ThemedActivity
import vn.mbm.phimp.me.leafpic.util.ThemeHelper
import vn.mbm.phimp.me.utilities.ActivitySwitchHelper

class AccountsActivity : ThemedActivity() {

    private var toolbar: Toolbar? = null
    private var themeHelper: ThemeHelper? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        themeHelper = ThemeHelper(this)
        toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        toolbar!!.setPopupTheme(themeHelper!!.getPopupToolbarStyle());
        toolbar!!.setBackgroundColor(themeHelper!!.getPrimaryColor());
        getSupportActionBar()!!.setTitle(R.string.title_account);

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_accounts_activity, menu)
        return true
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.action_add_account -> {
                val fragmentManager = supportFragmentManager
                val accountsPicker = AccountsPickerFragment()
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

    override fun onResume() {
        super.onResume()
        ActivitySwitchHelper.setContext(this)
        setNavigationBarColor(ThemeHelper.getPrimaryColor(this))
        setStatusBarColor()
    }
}
