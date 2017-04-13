package vn.mbm.phimp.me.wordpress;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;

import org.wordpress.android.fluxc.store.AccountStore;

import java.lang.ref.WeakReference;

import vn.mbm.phimp.me.MyApplication;
import vn.mbm.phimp.me.R;

/**
 * Created by rohanagarwal94 on 13/4/17.
 */

public class Logout {

    private ProgressDialog mDisconnectProgressDialog;
    private Activity activity;
    private AccountStore mAccountStore;

    Logout(Activity activity, AccountStore mAccountStore)
    {
        this.activity = activity;
        this.mAccountStore = mAccountStore;
    }

    public void signOutWordPressComWithConfirmation() {
        String message = String.format(activity.getString(R.string.sign_out_wpcom_confirm),
                mAccountStore.getAccount().getUserName());

        new AlertDialog.Builder(activity)
                .setMessage(message)
                .setPositiveButton(R.string.signout, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        signOutWordPressCom(activity);
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .setCancelable(true)
                .create().show();
    }

    private void signOutWordPressCom(Context context) {
        // note that signing out sends a CoreEvents.UserSignedOutWordPressCom EventBus event,
        // which will cause the main activity to recreate this fragment
        (new SignOutWordPressComAsync(context)).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void showDisconnectDialog(Activity activity) {
        mDisconnectProgressDialog = ProgressDialog.show(activity, null, activity.getText(R.string.signing_out), false);
    }
    
    private class SignOutWordPressComAsync extends AsyncTask<Void, Void, Void> {
        WeakReference<Context> mWeakContext;

        public SignOutWordPressComAsync(Context context) {
            mWeakContext = new WeakReference<Context>(context);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Context context = mWeakContext.get();
            if (context != null) {
                showDisconnectDialog(activity);
            }
        }

        @Override
        protected Void doInBackground(Void... params) {
            Context context = mWeakContext.get();
            if (context != null) {
                ((MyApplication) activity.getApplication()).wordPressComSignOut();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (mDisconnectProgressDialog != null && mDisconnectProgressDialog.isShowing()) {
                mDisconnectProgressDialog.dismiss();
            }
            mDisconnectProgressDialog = null;
        }
    }
}
