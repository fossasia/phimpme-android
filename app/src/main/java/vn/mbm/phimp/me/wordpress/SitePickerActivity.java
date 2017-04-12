package vn.mbm.phimp.me.wordpress;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.SearchView;


import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.wordpress.android.fluxc.Dispatcher;
import org.wordpress.android.fluxc.model.SiteModel;
import org.wordpress.android.fluxc.store.AccountStore;
import org.wordpress.android.fluxc.store.SiteStore;
import org.wordpress.android.fluxc.store.SiteStore.OnSiteChanged;
import org.wordpress.android.util.helpers.Debouncer;

import java.util.List;

import javax.inject.Inject;

import vn.mbm.phimp.me.MyApplication;
import vn.mbm.phimp.me.R;

/**
 * Created by rohanagarwal94 on 6/4/17.
 */

public class SitePickerActivity extends AppCompatActivity
        implements SitePickerAdapter.OnSiteClickListener,
        SitePickerAdapter.OnSelectedCountChangedListener,
        SearchView.OnQueryTextListener {

    public static final String KEY_LOCAL_ID = "local_id";
    private static final String KEY_IS_IN_SEARCH_MODE = "is_in_search_mode";
    private static final String KEY_LAST_SEARCH = "last_search";

    private SitePickerAdapter mAdapter;
    private RecyclerView mRecycleView;
    private ActionMode mActionMode;
    private SiteModel mSelectedSite;
    private SearchView mSearchView;
    private MenuItem mMenuSearch;
    private int mCurrentLocalId;
    private Debouncer mDebouncer = new Debouncer();

    @Inject AccountStore mAccountStore;
    @Inject SiteStore mSiteStore;
    @Inject Dispatcher mDispatcher;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((MyApplication) getApplication()).component().inject(this);

        setContentView(R.layout.site_picker_activity);
        restoreSavedInstanceState(savedInstanceState);
        setupActionBar();
        setupRecycleView();


        if (savedInstanceState == null) {
            if (!FluxCUtils.isSignedInWPComOrHasWPOrgSite(mAccountStore, mSiteStore)) {
                Intent intent = new Intent(this, SignInActivity.class);
                startActivity(intent);
            }
        }
    }

    public void initSitesList() {
        List<SiteModel> sites;

        // Else select the first in the list
        sites = mSiteStore.getSites();
        if (sites.size() != 0) {
            setSelectedSite(sites.get(0));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
//        initSitesList();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(KEY_LOCAL_ID, mCurrentLocalId);
        outState.putBoolean(KEY_IS_IN_SEARCH_MODE, getAdapter().getIsInSearchMode());
        outState.putString(KEY_LAST_SEARCH, getAdapter().getLastSearch());
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.site_picker, menu);
        return true;
    }


    @Override
    protected void onStop() {
        super.onStop();
        mDispatcher.unregister(this);
        mDebouncer.shutdown();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mDispatcher.register(this);
        EventBus.getDefault().register(this);
    }


    @SuppressWarnings("unused")
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSiteChanged(OnSiteChanged event) {
        if (getSelectedSite() == null && mSiteStore.hasSite()) {
            setSelectedSite(mSiteStore.getSites().get(0));
        }
        if (getSelectedSite() == null) {
            return;
        }

        SiteModel site = mSiteStore.getSiteByLocalId(getSelectedSite().getId());
    }
//
//    @SuppressWarnings("unused")
//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void onSiteChanged(OnSiteChanged event) {
//        mDebouncer.debounce(Void.class, new Runnable() {
//            @Override public void run() {
//                if (!isFinishing()) {
//                    getAdapter().loadSites();
//                }
//            }
//        }, 200, TimeUnit.MILLISECONDS);
//    }

    private void setupRecycleView() {
        mRecycleView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecycleView.setLayoutManager(new LinearLayoutManager(this));
        mRecycleView.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);
        mRecycleView.setItemAnimator(null);
        mRecycleView.setAdapter(getAdapter());
    }

    private void restoreSavedInstanceState(Bundle savedInstanceState) {
        boolean isInSearchMode = false;
        String lastSearch = "";

        if (savedInstanceState != null) {
            mCurrentLocalId = savedInstanceState.getInt(KEY_LOCAL_ID);
            isInSearchMode = savedInstanceState.getBoolean(KEY_IS_IN_SEARCH_MODE);
            lastSearch = savedInstanceState.getString(KEY_LAST_SEARCH);
        } else if (getIntent() != null) {
            mCurrentLocalId = getIntent().getIntExtra(KEY_LOCAL_ID, 0);
        }

        setNewAdapter(lastSearch, isInSearchMode);
    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.ic_cross_white_24dp);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(R.string.site_picker_title);
        }
    }

    private void setIsInSearchModeAndSetNewAdapter(boolean isInSearchMode) {
        String lastSearch = getAdapter().getLastSearch();
        setNewAdapter(lastSearch, isInSearchMode);
    }

    private SitePickerAdapter getAdapter() {
        if (mAdapter == null) {
            setNewAdapter("", false);
        }
        return mAdapter;
    }

    private void setNewAdapter(String lastSearch, boolean isInSearchMode) {
        mAdapter = new SitePickerAdapter(
                this,
                mCurrentLocalId,
                lastSearch,
                isInSearchMode,
                new SitePickerAdapter.OnDataLoadedListener() {
                    @Override
                    public void onBeforeLoad(boolean isEmpty) {
                        if (isEmpty) {
                            showProgress(true);
                        }
                    }
                    @Override
                    public void onAfterLoad() {
                        showProgress(false);
                    }
                });
        mAdapter.setOnSiteClickListener(this);
        mAdapter.setOnSelectedCountChangedListener(this);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        mMenuSearch = menu.findItem(R.id.menu_search);

        updateMenuItemVisibility();
        setupSearchView();

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            onBackPressed();
            return true;
        } else if (itemId == R.id.menu_search) {
            mSearchView.requestFocus();
            showSoftKeyboard();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateMenuItemVisibility() {
        if (mMenuSearch == null) return;

        // no point showing search if there aren't multiple blogs
        mMenuSearch.setVisible(mSiteStore.getSitesCount() > 1);
    }

    private void updateActionModeTitle() {
        if (mActionMode != null) {
            int numSelected = getAdapter().getNumSelected();
            String cabSelected = getString(R.string.cab_selected);
            mActionMode.setTitle(String.format(cabSelected, numSelected));
        }
    }

    private void setupSearchView() {
        mSearchView = (SearchView) mMenuSearch.getActionView();
        mSearchView.setIconifiedByDefault(false);
        mSearchView.setOnQueryTextListener(this);

        MenuItemCompat.setOnActionExpandListener(mMenuSearch, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                enableSearchMode();
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                disableSearchMode();
                return true;
            }
        });

        setQueryIfInSearch();
    }

    private void setQueryIfInSearch() {
        if (getAdapter().getIsInSearchMode()) {
            mMenuSearch.expandActionView();
            mSearchView.setQuery(getAdapter().getLastSearch(), false);
        }
    }

    private void enableSearchMode() {
        setIsInSearchModeAndSetNewAdapter(true);
        mRecycleView.swapAdapter(getAdapter(), true);
        updateMenuItemVisibility();
    }

    private void disableSearchMode() {
        hideSoftKeyboard();
        setIsInSearchModeAndSetNewAdapter(false);
        mRecycleView.swapAdapter(getAdapter(), true);
        updateMenuItemVisibility();
    }

    private void hideSoftKeyboard() {
        if (!hasHardwareKeyboard()) {
            InputMethodManager inputMethodManager = (InputMethodManager) mSearchView.getContext()
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(mSearchView.getWindowToken(), 0);
        }
    }

    private void showSoftKeyboard() {
        if (!hasHardwareKeyboard()) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    private boolean hasHardwareKeyboard() {
        return (getResources().getConfiguration().keyboard != Configuration.KEYBOARD_NOKEYS);
    }

    @Override
    public void onSelectedCountChanged(int numSelected) {
        if (mActionMode != null) {
            updateActionModeTitle();
            mActionMode.invalidate();
        }
    }

    @Override
    public void onSiteClick(SitePickerAdapter.SiteRecord siteRecord) {
        if (mActionMode == null) {
            hideSoftKeyboard();
            AppPrefs.addRecentlyPickedSiteId(siteRecord.localId);
            setSelectedSite(mSiteStore.getSiteByLocalId(siteRecord.localId));
            Intent intent = new Intent(this, MediaBrowserActivity.class);
            intent.putExtra(MyApplication.SITE, getSelectedSite());
            startActivity(intent);
        }
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        hideSoftKeyboard();
        return true;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        getAdapter().setLastSearch(s);
        getAdapter().searchSites(s);
        return true;
    }

    public void showProgress(boolean show) {
        findViewById(R.id.progress).setVisibility(show ? View.VISIBLE : View.GONE);
    }

    // Events

    @SuppressWarnings("unused")
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAuthenticationChanged(AccountStore.OnAuthenticationChanged event) {
        if (event.isError() && mSelectedSite != null) {
            AuthenticationDialogUtils.showAuthErrorView(this, mSelectedSite);
        }
    }

    @SuppressWarnings("unused")
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAccountChanged(AccountStore.OnAccountChanged event) {
        if (!FluxCUtils.isSignedInWPComOrHasWPOrgSite(mAccountStore, mSiteStore)) {
            // User signed out
            Intent intent = new Intent(this, SignInActivity.class);
            startActivity(intent);
        }
    }

    /**
     * @return null if there is no site or if there is no selected site
     */
    public @Nullable
    SiteModel getSelectedSite() {
        return mSelectedSite;
    }

    public void setSelectedSite(@Nullable SiteModel selectedSite) {
        mSelectedSite = selectedSite;
        if (selectedSite == null) {
            AppPrefs.setSelectedSite(-1);
            return;
        }

        // When we select a site, we want to update its informations or options
        mDispatcher.dispatch(SiteActionBuilder.newFetchSiteAction(selectedSite));

        // Make selected site visible
        selectedSite.setIsVisible(true);
//        AppPrefs.setSelectedSite(selectedSite.getId());
    }

//    /**
//     * This should not be moved to a SiteUtils.getSelectedSite() or similar static method. We don't want
//     * this to be used globally like WordPress.getCurrentBlog() was used. The state is maintained by this
//     * Activity and the selected site parameter is passed along to other activities / fragments.
//     */
//    public void initSelectedSite() {
//        int siteLocalId = AppPrefs.getSelectedSite();
//
//        if (siteLocalId != -1) {
//            // Site previously selected, use it
//            mSelectedSite = mSiteStore.getSiteByLocalId(siteLocalId);
//            // If saved site exist, then return, else (site has been removed?) try to select another site
//            if (mSelectedSite != null) {
//                return;
//            }
//        }
//
//        // Try to select the primary wpcom site
//        long siteId = mAccountStore.getAccount().getPrimarySiteId();
//        SiteModel primarySite = mSiteStore.getSiteBySiteId(siteId);
//        // Primary site found, select it
//        if (primarySite != null) {
//            setSelectedSite(primarySite);
//            return;
//        }
//
//        // Else select the first visible site in the list
//        List<SiteModel> sites = mSiteStore.getVisibleSites();
//        if (sites.size() != 0) {
//            setSelectedSite(sites.get(0));
//            return;
//        }
//
//        // Else select the first in the list
//        sites = mSiteStore.getSites();
//        if (sites.size() != 0) {
//            setSelectedSite(sites.get(0));
//        }
//
//        // Else no site selected
//    }

}
