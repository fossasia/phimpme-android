package vn.mbm.phimp.me.wordpress;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import org.wordpress.android.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import vn.mbm.phimp.me.MyApplication;

/**
 * Created by rohanagarwal94 on 6/4/17.
 */

public class AppPrefs {
    public static final int MAX_RECENTLY_PICKED_SITES = 4;

    public interface PrefKey {
        String name();
        String toString();
    }

    /**
     * Application related preferences. When the user disconnects, these preferences are erased.
     */
    public enum DeletablePrefKey implements PrefKey {
        // name of last shown activity
        LAST_ACTIVITY_STR,

        // last selected tag in the reader
        READER_TAG_NAME,
        READER_TAG_TYPE,

        // title of the last active page in ReaderSubsActivity
        READER_SUBS_PAGE_TITLE,

        // email retrieved and attached to mixpanel profile
        MIXPANEL_EMAIL_ADDRESS,

        // index of the last active tab in main activity
        MAIN_TAB_INDEX,

        // index of the last active item in Stats activity
        STATS_ITEM_INDEX,

        // Keep the associations between each widget_id/blog_id added to the app
        STATS_WIDGET_KEYS_BLOGS,

        // last data stored for the Stats Widgets
        STATS_WIDGET_DATA,

        // aztec editor enabled
        AZTEC_EDITOR_ENABLED,

        // visual editor enabled
        VISUAL_EDITOR_ENABLED,

        // Store the number of times Stats are loaded without errors. It's used to show the Widget promo dialog.
        STATS_WIDGET_PROMO_ANALYTICS,

        // index of the last active status type in Comments activity
        COMMENTS_STATUS_TYPE_INDEX,

        // index of the last active people list filter in People Management activity
        PEOPLE_LIST_FILTER_INDEX,

        // selected site in the main activity
        SELECTED_SITE_LOCAL_ID,

        // wpcom ID of the last push notification received
        PUSH_NOTIFICATIONS_LAST_NOTE_ID,

        // local time of the last push notification received
        PUSH_NOTIFICATIONS_LAST_NOTE_TIME,

        // local IDs of sites recently chosen in the site picker
        RECENTLY_PICKED_SITE_IDS,

        // list of last time a notification has been created for a draft
        PENDING_DRAFTS_NOTIFICATION_LAST_NOTIFICATION_DATES,
    }

    /**
     * These preferences won't be deleted when the user disconnects. They should be used for device specifics or user
     * independent prefs.
     */

    private static SharedPreferences prefs() {
        return PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext());
    }

    private static String getString(PrefKey key) {
        return getString(key, "");
    }

    private static String getString(PrefKey key, String defaultValue) {
        return prefs().getString(key.name(), defaultValue);
    }

    private static void setString(PrefKey key, String value) {
        SharedPreferences.Editor editor = prefs().edit();
        if (TextUtils.isEmpty(value)) {
            editor.remove(key.name());
        } else {
            editor.putString(key.name(), value);
        }
        editor.apply();
    }

    private static int getInt(PrefKey key, int def) {
        try {
            String value = getString(key);
            if (value.isEmpty()) {
                return def;
            }
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return def;
        }
    }

    private static void setInt(PrefKey key, int value) {
        setString(key, Integer.toString(value));
    }

    // Exposed methods

    /**
     * remove all user-related preferences
     */
    public static void reset() {
        SharedPreferences.Editor editor = prefs().edit();
        for (DeletablePrefKey key : DeletablePrefKey.values()) {
            editor.remove(key.name());
        }
        editor.apply();
    }

    public static int getSelectedSite() {
        return getInt(DeletablePrefKey.SELECTED_SITE_LOCAL_ID, -1);
    }

    public static void setSelectedSite(int selectedSite) {
        setInt(DeletablePrefKey.SELECTED_SITE_LOCAL_ID, selectedSite);
    }

    /*
     * returns a list of local IDs of sites recently chosen in the site picker
     */
    public static ArrayList<Integer> getRecentlyPickedSiteIds() {
        String idsAsString = getString(DeletablePrefKey.RECENTLY_PICKED_SITE_IDS, "");
        List<String> items = Arrays.asList(idsAsString.split(","));

        ArrayList<Integer> siteIds = new ArrayList<>();
        for (String item : items) {
            siteIds.add(StringUtils.stringToInt(item));
        }

        return siteIds;
    }

    /*
     * adds a local site ID to the top of list of recently chosen sites
     */
    public static void addRecentlyPickedSiteId(Integer localId) {
        if (localId == 0) return;

        ArrayList<Integer> currentIds = getRecentlyPickedSiteIds();

        // remove this ID if it already exists in the list
        int index = currentIds.indexOf(localId);
        if (index > -1) {
            currentIds.remove(index);
        }

        // add this ID to the front of the list
        currentIds.add(0, localId);

        // remove at max
        if (currentIds.size() > MAX_RECENTLY_PICKED_SITES) {
            currentIds.remove(MAX_RECENTLY_PICKED_SITES);
        }

        // store in prefs
        String idsAsString = TextUtils.join(",", currentIds);
        setString(DeletablePrefKey.RECENTLY_PICKED_SITE_IDS, idsAsString);
    }
}
