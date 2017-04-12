package vn.mbm.phimp.me.wordpress;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.method.PasswordTransformationMethod;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;

import vn.mbm.phimp.me.R;

/**
 * Created by rohanagarwal94 on 6/4/17.
 */

/**
 * A fragment representing a single step in a wizard. The fragment shows a dummy title indicating
 * the page number, along with some dummy text.
 */
public abstract class AbstractFragment extends Fragment {
    protected ConnectivityManager mSystemService;
    protected boolean mPasswordVisible;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSystemService = (ConnectivityManager) getActivity().getApplicationContext().
                getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    protected void startProgress(String message) {
    }

    protected void endProgress() {
    }

    protected abstract void onDoneAction();

    protected abstract boolean isUserDataValid();

    protected boolean onDoneEvent(int actionId, KeyEvent event) {
        if (didPressEnterKey(actionId, event)) {
            if (!isUserDataValid()) {
                return true;
            }

            // hide keyboard before calling the done action
            if (getActivity() != null) {
                View view = getActivity().getCurrentFocus();
                if (view != null) {
                    InputMethodManager inputMethodManager = (InputMethodManager) view.getContext()
                            .getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
            }

            // call child action
            onDoneAction();
            return true;
        }
        return false;
    }

    protected boolean didPressNextKey(int actionId, KeyEvent event) {
        return actionId == EditorInfo.IME_ACTION_NEXT || event != null && (event.getAction() == KeyEvent.ACTION_DOWN
                && event.getKeyCode() == KeyEvent.KEYCODE_NAVIGATE_NEXT);
    }

    protected boolean didPressEnterKey(int actionId, KeyEvent event) {
        return actionId == EditorInfo.IME_ACTION_DONE || event != null && (event.getAction() == KeyEvent.ACTION_DOWN
                && event.getKeyCode() == KeyEvent.KEYCODE_ENTER);
    }

    protected void initPasswordVisibilityButton(View rootView, final EditText passwordEditText) {
        final ImageView passwordVisibility = (ImageView) rootView.findViewById(R.id.password_visibility);
        if (passwordVisibility == null) {
            return;
        }
        passwordVisibility.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mPasswordVisible = !mPasswordVisible;
                if (mPasswordVisible) {
                    passwordVisibility.setImageResource(R.drawable.ic_visible_on_black_24dp);
                    passwordVisibility.setColorFilter(v.getContext().getResources().getColor(R.color.grey_lighten_10));
                    passwordEditText.setTransformationMethod(null);
                } else {
                    passwordVisibility.setImageResource(R.drawable.ic_visible_off_black_24dp);
                    passwordVisibility.setColorFilter(v.getContext().getResources().getColor(R.color.grey_lighten_20));
                    passwordEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
                passwordEditText.setSelection(passwordEditText.length());
            }
        });
    }

    protected boolean specificShowError(int messageId) {
        return false;
    }

    protected void showError(int messageId) {
        if (!isAdded()) {
            return;
        }
        if (specificShowError(messageId)) {
            return;
        }
        // Failback if it's not a specific error
        showError(getString(messageId));
    }

    protected void showError(String message) {
        if (!isAdded()) {
            return;
        }
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        SignInDialogFragment nuxAlert = SignInDialogFragment.newInstance(getString(R.string.error), message,
                R.drawable.ic_notice_white_64dp, getString(R.string.nux_tap_continue));
        ft.add(nuxAlert, "alert");
        ft.commitAllowingStateLoss();
    }

    protected ErrorType getErrorType(int messageId) {
        if (messageId == R.string.username_only_lowercase_letters_and_numbers ||
                messageId == R.string.username_required || messageId == R.string.username_not_allowed ||
                messageId == R.string.username_must_be_at_least_four_characters ||
                messageId == R.string.username_contains_invalid_characters ||
                messageId == R.string.username_must_include_letters || messageId == R.string.username_exists ||
                messageId == R.string.username_reserved_but_may_be_available ||
                messageId == R.string.username_invalid) {
            return ErrorType.USERNAME;
        } else if (messageId == R.string.password_invalid) {
            return ErrorType.PASSWORD;
        }
        return ErrorType.UNDEFINED;
    }

    protected enum ErrorType {USERNAME, PASSWORD, UNDEFINED}

}
