package vn.mbm.phimp.me.accounts;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import vn.mbm.phimp.me.R;
import vn.mbm.phimp.me.base.ThemedActivity;
import vn.mbm.phimp.me.leafpic.util.ThemeHelper;

public class AccountsActivity extends ThemedActivity {
    Toolbar toolbar;
    ThemeHelper themeHelper;

    @Override
    public int getContentViewId() {
        return R.layout.content_accounts;
    }

    @Override
    public int getNavigationMenuItemId() {
        return R.id.navigation_accounts;
    }

    @Override
    public void onResume() {
        super.onResume();
        setNavigationBarColor(ThemeHelper.getPrimaryColor(this));
        setStatusBarColor();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        themeHelper = new ThemeHelper(this);
        setToolbar();

    }

    private void setToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setPopupTheme(themeHelper.getPopupToolbarStyle());
        toolbar.setBackgroundColor(themeHelper.getPrimaryColor());
        getSupportActionBar().setTitle(R.string.title_account);
    }

}
