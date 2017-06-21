package org.fossasia.phimpme.accounts;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterAuthToken;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;

import org.fossasia.phimpme.R;
import org.fossasia.phimpme.data.AccountDatabase;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

import static android.support.design.widget.Snackbar.LENGTH_LONG;

/**
 * Created by pa1pal on 07/06/17.
 */

public class AccountPickerFragment extends DialogFragment {

    private TwitterAuthClient client = new TwitterAuthClient();
    private CallbackManager callbackManager = CallbackManager.Factory.create();
    private LoginManager loginManager = LoginManager.getInstance();
    Realm realm = Realm.getDefaultInstance();
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

                            case 1:
                                FacebookSdk.sdkInitialize(getActivity());

                                List<String> permissionNeeds = Arrays.asList("publish_actions");

                                loginManager.logInWithReadPermissions(getActivity(), Arrays.asList("email", "public_profile"));
                                loginManager.logInWithPublishPermissions(getActivity(), permissionNeeds);

                                loginManager.registerCallback(callbackManager,
                                        new FacebookCallback<LoginResult>() {
                                            @Override
                                            public void onSuccess(LoginResult loginResult) {
                                                // Begin realm transaction
                                                realm.beginTransaction();

                                                // Creating Realm object for AccountDatabase Class
                                                account = realm.createObject(AccountDatabase.class,
                                                        accountsList[1]);

                                                // Writing values in Realm database
                                                account.setUsername(loginResult
                                                        .getAccessToken().getUserId());
                                                account.setToken(String.valueOf(loginResult
                                                        .getAccessToken().getToken()));

                                                GraphRequest.newMeRequest(
                                                        loginResult.getAccessToken(),
                                                        new GraphRequest.GraphJSONObjectCallback() {
                                                            @Override
                                                            public void onCompleted(JSONObject jsonObject
                                                                    , GraphResponse graphResponse) {
                                                                /*try {
                                                                    account.setName(jsonObject
                                                                            .getString("name"));
                                                                } catch (JSONException e) {
                                                                    e.printStackTrace();
                                                                }*/
                                                            }
                                                        });

                                                // Finally committing the whole data
                                                realm.commitTransaction();
                                            }

                                            @Override
                                            public void onCancel() {
                                                Snackbar.make(getActivity().getCurrentFocus(),
                                                        getString(R.string.facebook_login_cancel), LENGTH_LONG).show();
                                            }

                                            @Override
                                            public void onError(FacebookException e) {
                                                Snackbar.make(getActivity().getCurrentFocus(),
                                                        getString(R.string.facebook_login_error), LENGTH_LONG).show();

                                                Log.d("error", e.toString());
                                            }
                                        });
                        }
                    }
                });
        return builder.create();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        client.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
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
