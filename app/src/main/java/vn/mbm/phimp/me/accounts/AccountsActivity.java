package vn.mbm.phimp.me.accounts;

import vn.mbm.phimp.me.R;
import vn.mbm.phimp.me.base.BaseActivity;

public class AccountsActivity extends BaseActivity {

    @Override
    public int getContentViewId() {
        return R.layout.content_accounts;
    }

    @Override
    public int getNavigationMenuItemId() {
        return R.id.navigation_accounts;
    }
}
