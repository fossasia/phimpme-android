package vn.mbm.phimp.me.accounts;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterAuthToken;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import vn.mbm.phimp.me.R;
import vn.mbm.phimp.me.data.AccountDatabase;

/**
 * Created by pa1pal on 07/06/17.
 */

public class AccountPickerFragment extends DialogFragment {

    private TwitterAuthClient client = new TwitterAuthClient();
    private AccountAdapter accountAdapter = new AccountAdapter();
    Realm realm = Realm.getDefaultInstance();
    private AccountPresenter accountPresenter = new AccountPresenter(realm);
    AccountDatabase account;

    public String[] accountsList = {"Twitter", "Facebook", "Instagram"};

    public AccountPickerFragment newInstance(String title) {
        AccountPickerFragment fragment = new AccountPickerFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.account_list)
                // Creating a dialog fragment using char sequences
                .setItems(accountsList, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        switch (which) {
                            case 0:

                                /**
                                 * When user clicks then we first check if it is already exist.
                                 */

                                if (checkAlreadyExist(accountsList[0])) {
                                    Toast.makeText(getActivity(), R.string.already_signed_in,
                                            Toast.LENGTH_SHORT).show();
                                } else {
                                    client.authorize(getActivity(), new Callback<TwitterSession>() {
                                        @Override
                                        public void success(Result<TwitterSession> result) {

                                            // Begin realm transaction
                                            realm.beginTransaction();

                                            // Creating Realm object for AccountDatabase Class
                                            account = realm.createObject(AccountDatabase.class,
                                                    accountsList[0]);

                                            // Creating twitter session, after user authenticate
                                            // in twitter popup
                                            TwitterSession session = TwitterCore.getInstance()
                                                    .getSessionManager().getActiveSession();
                                            TwitterAuthToken authToken = session.getAuthToken();
                                            Log.d("Twitter Credentials", session.toString());


                                            // Writing values in Realm database
                                            account.setUsername(session.getUserName());
                                            account.setToken(String.valueOf(session.getAuthToken()));


                                            // Finally committing the whole data
                                            realm.commitTransaction();
                                        }

                                        @Override
                                        public void failure(TwitterException e) {

                                        }
                                    });
                                }
                        }
                    }
                });
        return builder.create();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        client.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * This function check if the selected account is already existed.
     *
     * @param s Name of the account from accountList e.g. Twitter
     * @return true is existed, false otherwise
     */
    public boolean checkAlreadyExist(String s) {

        // Query in the realm database
        RealmQuery<AccountDatabase> query = realm.where(AccountDatabase.class);

        // Checking if string equals to is exist or not
        query.equalTo("name", s);
        RealmResults<AccountDatabase> result1 = query.findAll();

        // Here checking if count of that values is greater than zero
        if (result1.size() > 0) {
            return true;
        } else {
            return false;
        }
    }
}
