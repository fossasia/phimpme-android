package vn.mbm.phimp.me.wordpress;

/**
 * Created by rohanagarwal94 on 6/4/17.
 */
import android.text.TextUtils;

import org.wordpress.android.fluxc.model.SiteModel;
import org.wordpress.android.util.GravatarUtils;
import org.wordpress.android.util.PhotonUtils;
import org.wordpress.android.util.StringUtils;
import org.wordpress.android.util.UrlUtils;

public class SiteUtils {
    public static String getSiteNameOrHomeURL(SiteModel site) {
        String siteName = getSiteName(site);
        if (siteName.trim().length() == 0) {
            siteName = getHomeURLOrHostName(site);
        }
        return siteName;
    }

    public static String getHomeURLOrHostName(SiteModel site) {
        String homeURL = UrlUtils.removeScheme(site.getUrl());
        homeURL = StringUtils.removeTrailingSlash(homeURL);
        if (TextUtils.isEmpty(homeURL)) {
            return UrlUtils.getHost(site.getXmlRpcUrl());
        }
        return homeURL;
    }

    public static String getSiteName(SiteModel site) {
        return StringUtils.unescapeHTML(site.getName());
    }

    /**
     * @return true if the site is WPCom or Jetpack and is not private
     */
    public static boolean isPhotonCapable(SiteModel site) {
        return SiteUtils.isAccessibleViaWPComAPI(site) && !site.isPrivate();
    }

    public static boolean isAccessibleViaWPComAPI(SiteModel site) {
        return site.isWPCom() || site.isJetpackConnected();
    }

    public static String getSiteIconUrl(SiteModel site, int size) {
        if (!TextUtils.isEmpty(site.getIconUrl())) {
            return PhotonUtils.getPhotonImageUrl(site.getIconUrl(), size, size, PhotonUtils.Quality.HIGH);
        } else {
            return GravatarUtils.blavatarFromUrl(site.getUrl(), size);
        }
    }
}
