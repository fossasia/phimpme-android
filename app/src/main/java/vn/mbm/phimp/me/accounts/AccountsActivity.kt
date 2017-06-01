package vn.mbm.phimp.me.accounts

import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.v7.widget.Toolbar
import vn.mbm.phimp.me.R
import vn.mbm.phimp.me.base.ThemedActivity
import vn.mbm.phimp.me.leafpic.util.ThemeHelper

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

        val fab = findViewById(R.id.fab) as FloatingActionButton
        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }
    }

    override fun getContentViewId(): Int {
        return R.layout.activity_accounts
    }

    override fun getNavigationMenuItemId(): Int {
        return R.id.navigation_accounts
    }

    override fun onResume() {
        super.onResume()
        setNavigationBarColor(ThemeHelper.getPrimaryColor(this))
        setStatusBarColor()
    }

}
