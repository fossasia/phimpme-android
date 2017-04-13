package vn.mbm.phimp.me.wordpress;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.wordpress.android.fluxc.Dispatcher;
import org.wordpress.android.fluxc.action.AccountAction;
import org.wordpress.android.fluxc.network.rest.wpcom.account.AccountRestClient.IsAvailable;
import org.wordpress.android.fluxc.store.AccountStore;
import org.wordpress.android.fluxc.store.AccountStore.AuthenticationErrorType;
import org.wordpress.android.fluxc.store.AccountStore.OnAccountChanged;
import org.wordpress.android.fluxc.store.AccountStore.OnAuthenticationChanged;
import org.wordpress.android.fluxc.store.AccountStore.OnAvailabilityChecked;
import org.wordpress.android.fluxc.store.SiteStore;
import org.wordpress.android.fluxc.store.SiteStore.OnSiteChanged;
import org.wordpress.android.util.EditTextUtils;
import org.wordpress.android.util.ToastUtils;

import java.util.regex.Pattern;

import javax.inject.Inject;

import vn.mbm.phimp.me.MyApplication;
import vn.mbm.phimp.me.R;

/**
 * Created by rohanagarwal94 on 6/4/17.
 */

public class SignInFragment extends AbstractFragment implements TextWatcher {
    public static final String TAG = "sign_in_fragment_tag";
    public static final int MAX_EMAIL_LENGTH = 100;
    private static final int WPCOM_ERRONEOUS_LOGIN_THRESHOLD = 3;
    private static final Pattern WPCOM_DOMAIN = Pattern.compile("[a-z0-9]+\\.wordpress\\.com");

    protected EditText mUsernameEditText;
    protected EditText mPasswordEditText;

    protected View mWpcomLogotype;
    protected LinearLayout mBottomButtonsLayout;
    protected RelativeLayout mUsernameLayout;
    protected RelativeLayout mPasswordLayout;
    protected RelativeLayout mProgressBarSignIn;

    protected int mErroneousLogInCount;
    protected String mUsername;
    protected String mPassword;

    protected TextView mSignInButton;
    protected TextView mProgressTextSignIn;

    protected @Inject AccountStore mAccountStore;
    protected @Inject Dispatcher mDispatcher;

    protected boolean mSitesFetched = false;
    protected boolean mAccountSettingsFetched = false;
    protected boolean mAccountFetched = false;

    private boolean mIsActivityFinishing;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((MyApplication) getActivity().getApplication()).component().inject(this);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.signin_fragment, container, false);
        mWpcomLogotype = rootView.findViewById(R.id.nux_wordpress_logotype);
        mUsernameLayout = (RelativeLayout) rootView.findViewById(R.id.nux_username_layout);
        mUsernameLayout.setOnClickListener(mOnLoginFormClickListener);
        mPasswordLayout = (RelativeLayout) rootView.findViewById(R.id.nux_password_layout);
        mPasswordLayout.setOnClickListener(mOnLoginFormClickListener);

        mUsernameEditText = (EditText) rootView.findViewById(R.id.nux_username);
        mUsernameEditText.addTextChangedListener(this);
        mUsernameEditText.setOnClickListener(mOnLoginFormClickListener);
        mPasswordEditText = (EditText) rootView.findViewById(R.id.nux_password);
        mPasswordEditText.addTextChangedListener(this);
        mPasswordEditText.setOnClickListener(mOnLoginFormClickListener);
        mSignInButton = (TextView) rootView.findViewById(R.id.nux_sign_in_button);
        mSignInButton.setOnClickListener(mSignInClickListener);
        mProgressBarSignIn = (RelativeLayout) rootView.findViewById(R.id.nux_sign_in_progress_bar);
        mProgressTextSignIn = (TextView) rootView.findViewById(R.id.nux_sign_in_progress_text);

        mPasswordEditText.setOnEditorActionListener(mEditorAction);

        mBottomButtonsLayout = (LinearLayout) rootView.findViewById(R.id.nux_bottom_buttons);
        initPasswordVisibilityButton(rootView, mPasswordEditText);

        autofillFromBuildConfig();
        showPasswordFieldAndFocus();

        mUsernameEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((didPressNextKey(actionId, event) || didPressEnterKey(actionId, event))) {
                    signIn();
                    return true;
                } else {
                    return false;
                }
            }
        });
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        showPasswordFieldAndFocus();
    }

    private void showPasswordFieldAndFocus() {
        showPasswordField();
    }

    private void showPasswordField() {
        if (isAdded()) {
            endProgress();
            mPasswordEditText.requestFocus();
            mPasswordLayout.setVisibility(View.VISIBLE);
            mPasswordEditText.setImeOptions(EditorInfo.IME_ACTION_DONE);
            showWPComLogoType(true);
            mSignInButton.setText(getString(R.string.sign_in));
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
            imm.showSoftInput(mPasswordEditText, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    private void showWPComLogoType(boolean show) {
        int visibility = show ? View.VISIBLE : View.GONE;
        mWpcomLogotype.setVisibility(visibility);
    }

    /*
     * autofill the username and password from BuildConfig/gradle.properties (developer feature,
     * only enabled for DEBUG releases)
     */
    private void autofillFromBuildConfig() {
        if (!BuildConfig.DEBUG) return;

        String userName = (String) MyApplication.getBuildConfigValue(getActivity().getApplication(),
                "DEBUG_DOTCOM_LOGIN_USERNAME");
        String password = (String) MyApplication.getBuildConfigValue(getActivity().getApplication(),
                "DEBUG_DOTCOM_LOGIN_PASSWORD");
        if (!TextUtils.isEmpty(userName)) {
            mUsernameEditText.setText(userName);
        }
        if (!TextUtils.isEmpty(password)) {
            mPasswordEditText.setText(password);
        }
    }

    private final OnClickListener mOnLoginFormClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            // Don't change layout if we are performing a network operation
            if (mProgressBarSignIn.getVisibility() == View.VISIBLE) return;
        }
    };

    protected void onDoneAction() {
        signIn();
    }

    private final TextView.OnEditorActionListener mEditorAction = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if (mPasswordEditText == v) {
                return onDoneEvent(actionId, event);
            }
            return onDoneEvent(actionId, event);
        }
    };

    private void finishCurrentActivity() {
        if (mIsActivityFinishing) {
            return;
        }

        mIsActivityFinishing = true;
        if (getActivity() == null) {
            return;
        }

            // move on the the main activity
            Intent intent = new Intent(getActivity(), SitePickerActivity.class);
            getActivity().startActivity(intent);
            getActivity().finish();

    }

    protected void signIn() {
        if (!isUserDataValid()) {
            return;
        }
        mUsername = EditTextUtils.getText(mUsernameEditText).trim().toLowerCase();
        mPassword = EditTextUtils.getText(mPasswordEditText).trim();

            // If the user is already logged in a wordpress.com account, bail out
            if (checkIfUserIsAlreadyLoggedIn()) {
                return;
            }
            signInAndFetchBlogListWPCom();
        }

    private final OnClickListener mSignInClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            signIn();
        }
    };

    @Override
    public void afterTextChanged(Editable s) {
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (fieldsFilled()) {
            mSignInButton.setEnabled(true);
        } else {
            mSignInButton.setEnabled(false);
        }
        mPasswordEditText.setError(null);
        mUsernameEditText.setError(null);
    }

    private boolean fieldsFilled() {
        return EditTextUtils.getText(mUsernameEditText).trim().length() > 0
                && (mPasswordLayout.getVisibility() == View.GONE || EditTextUtils.getText(mPasswordEditText).trim().length() > 0);
    }

    protected boolean isUserDataValid() {
        final String username = EditTextUtils.getText(mUsernameEditText).trim();
        final String password = EditTextUtils.getText(mPasswordEditText).trim();
        boolean retValue = true;

        if (TextUtils.isEmpty(password)) {
            mPasswordEditText.setError(getString(R.string.required_field));
            mPasswordEditText.requestFocus();
            retValue = false;
        }

        if (TextUtils.isEmpty(username)) {
            mUsernameEditText.setError(getString(R.string.required_field));
            mUsernameEditText.requestFocus();
            retValue = false;
        }

        return retValue;
    }

    private void showPasswordError(int messageId) {
        mPasswordEditText.setError(getString(messageId));
        mPasswordEditText.requestFocus();
    }

    private void showUsernameError(int messageId) {
        mUsernameEditText.setError(getString(messageId));
        mUsernameEditText.requestFocus();
    }

    protected boolean specificShowError(int messageId) {
        switch (getErrorType(messageId)) {
            case USERNAME:
            case PASSWORD:
                showPasswordError(messageId);
                showUsernameError(messageId);
                return true;
            default:
                return false;
        }
    }

    protected void startProgress(String message) {
        mProgressBarSignIn.setVisibility(View.VISIBLE);
        mProgressTextSignIn.setVisibility(View.VISIBLE);
        mSignInButton.setVisibility(View.GONE);
        mProgressBarSignIn.setEnabled(false);
        mProgressTextSignIn.setText(message);
        mUsernameEditText.setEnabled(false);
        mPasswordEditText.setEnabled(false);
    }

    protected void endProgress() {
        mProgressBarSignIn.setVisibility(View.GONE);
        mProgressTextSignIn.setVisibility(View.GONE);
        mSignInButton.setVisibility(View.VISIBLE);
        mUsernameEditText.setEnabled(true);
        mPasswordEditText.setEnabled(true);
    }

    protected void showInvalidUsernameOrPasswordDialog() {
        // Show a dialog
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        SignInDialogFragment nuxAlert;
        nuxAlert = SignInDialogFragment.newInstance(getString(R.string.nux_cannot_log_in),
                getString(R.string.username_or_password_incorrect),
                R.drawable.ic_notice_white_64dp, getString(
                        R.string.cancel));

        ft.add(nuxAlert, "alert");
        ft.commitAllowingStateLoss();
    }

    protected void handleInvalidUsernameOrPassword(int messageId) {
        mErroneousLogInCount += 1;
        if (mErroneousLogInCount >= WPCOM_ERRONEOUS_LOGIN_THRESHOLD) {
            // Clear previous errors
            mPasswordEditText.setError(null);
            mUsernameEditText.setError(null);
            showInvalidUsernameOrPasswordDialog();
        } else {
            showPasswordError(messageId);
            showUsernameError(messageId);
        }
        endProgress();
    }

    private void showGenericErrorDialog(String errorMessage) {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        SignInDialogFragment nuxAlert;

        nuxAlert = SignInDialogFragment.newInstance(getString(R.string.nux_cannot_log_in),
                errorMessage, R.drawable.ic_notice_white_64dp,
                getString(R.string.cancel));
        ft.add(nuxAlert, "alert");
        ft.commitAllowingStateLoss();
    }

    @Override
    public void onStart() {
        super.onStart();
        // Autofill username / password if string fields are set (only useful after an error in sign up).
        // This can't be done in onCreateView
        if (mUsername != null) {
            mUsernameEditText.setText(mUsername);
        }
        if (mPassword != null) {
            mPasswordEditText.setText(mPassword);
        }
        mDispatcher.register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        mDispatcher.unregister(this);
    }


    // OnChanged events

    @SuppressWarnings("unused")
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAccountChanged(OnAccountChanged event) {
        if (event.isError()) {
            showAccountError(event.error.type, event.error.message);
            endProgress();
            return;
        }

        // Success
        mAccountSettingsFetched |= event.causeOfChange == AccountAction.FETCH_SETTINGS;
        mAccountFetched |= event.causeOfChange == AccountAction.FETCH_ACCOUNT;

        // Finish activity if sites have been fetched
        if (mSitesFetched && mAccountSettingsFetched && mAccountFetched) {
            finishCurrentActivity();
        }
    }

    @SuppressWarnings("unused")
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAuthenticationChanged(OnAuthenticationChanged event) {
        if (event.isError()) {
            showAuthError(event.error.type, event.error.message);
            endProgress();
            return;
        }

        fetchAccountSettingsAndSites();
    }

    @SuppressWarnings("unused")
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSiteChanged(OnSiteChanged event) {

        if (event.isError()) {
            endProgress();
            if (!isAdded()) {
                return;
            }
            if (event.error.type == SiteStore.SiteErrorType.DUPLICATE_SITE) {
                if (event.rowsAffected == 0) {
                    // If there is a duplicate site and not any site has been added, show an error and
                    // stop the sign in process
                    ToastUtils.showToast(getContext(), R.string.cannot_add_duplicate_site);
                    return;
                } else {
                    // If there is a duplicate site, notify the user something could be wrong,
                    // but continue the sign in process
                    ToastUtils.showToast(getContext(), R.string.duplicate_site_detected);
                }
            } else {
                return;
            }
        }

        // Login Successful
        mSitesFetched = true;

        // Finish activity if account settings have been fetched or if it's a wporg site
        if ((mAccountSettingsFetched && mAccountFetched)) {
            finishCurrentActivity();
        }
    }

    @SuppressWarnings("unused")
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAvailabilityChecked(OnAvailabilityChecked event) {
        if (event.isError()) {
            Log.d("error is", event.error.message);
        }
        handleUsernameAvailabilityEvent(event);
    }

    /**
     * Handler for a username availability event. If a user enters a wpcom domain as their username
     * an API call is made to check if the subdomain is a valid username. This method handles
     * the result of that API call, showing an error if the subdomain was not a valid username,
     * or showing the password field if it was a valid username.
     *
     * @param event
     */
    private void handleUsernameAvailabilityEvent(OnAvailabilityChecked event) {
        endProgress();
        if (event.type != IsAvailable.USERNAME ) {
            return;
        }
        if (event.isAvailable) {
            // Username doesn't exist in WordPress.com, show just show an error.
            showUsernameError(R.string.username_invalid);
            return;
        }
        // Username exists in WordPress.com. Update the form and show the password field.
        mUsername = event.value;
        mUsernameEditText.setText(event.value);
        showPasswordFieldAndFocus();
    }

    private boolean checkIfUserIsAlreadyLoggedIn() {
        if (mAccountStore.hasAccessToken()) {
            String currentUsername = mAccountStore.getAccount().getUserName();
            if (getActivity() != null) {
                if (currentUsername.equals(mUsername)) {
                    ToastUtils.showToast(getActivity(), R.string.already_logged_in_wpcom_same_username, ToastUtils.Duration.LONG);
                } else {
                    ToastUtils.showToast(getActivity(), R.string.already_logged_in_wpcom, ToastUtils.Duration.LONG);
                }
            }
            return true;
        }
        return false;
    }

    private void showAccountError(AccountStore.AccountErrorType error, String errorMessage) {
        switch (error) {
            case ACCOUNT_FETCH_ERROR:
                showError(R.string.error_fetch_my_profile);
                break;
            case SETTINGS_FETCH_ERROR:
                showError(R.string.error_fetch_account_settings);
                break;
            case SETTINGS_POST_ERROR:
                showError(R.string.error_post_account_settings);
                break;
            case GENERIC_ERROR:
            default:
                showError(errorMessage);
                break;
        }
    }

    private void signInAndFetchBlogListWPCom() {
        startProgress(getString(R.string.connecting_wpcom));
        AccountStore.AuthenticatePayload payload = new AccountStore.AuthenticatePayload(mUsername, mPassword);
        mDispatcher.dispatch(org.wordpress.android.fluxc.generated.AuthenticationActionBuilder.newAuthenticateAction(payload));
    }

    private void showAuthError(AuthenticationErrorType error, String errorMessage) {
        switch (error) {
            case INCORRECT_USERNAME_OR_PASSWORD:
            case NOT_AUTHENTICATED: // NOT_AUTHENTICATED is the generic error from XMLRPC response on first call.
                handleInvalidUsernameOrPassword(R.string.username_or_password_incorrect);
                break;
            case INVALID_REQUEST:
                // TODO: FluxC: could be specific?
            default:
                // For all other kind of error, show a dialog with API Response error message
                showGenericErrorDialog(errorMessage);
                break;
        }
    }

    private void fetchAccountSettingsAndSites() {
        if (mAccountStore.hasAccessToken()) {
            // Fetch user infos
            mDispatcher.dispatch(AccountActionBuilder.newFetchAccountAction());
            mDispatcher.dispatch(AccountActionBuilder.newFetchSettingsAction());
            // Fetch sites
            mDispatcher.dispatch(SiteActionBuilder.newFetchSitesAction());
        }
    }
}
