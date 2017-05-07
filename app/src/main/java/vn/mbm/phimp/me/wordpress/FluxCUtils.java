package vn.mbm.phimp.me.wordpress;

import org.wordpress.android.fluxc.store.AccountStore;
import org.wordpress.android.fluxc.store.SiteStore;

public class FluxCUtils {
    /**
     * This method doesn't do much, but insure we're doing the same check in all parts of the app.
     * @return true if the user is signed in a WordPress.com account or if he has a .org site.
     */
    public static boolean isSignedInWPComOrHasWPOrgSite(AccountStore accountStore, SiteStore siteStore) {
        return accountStore.hasAccessToken() || siteStore.hasSelfHostedSite();
    }

}
