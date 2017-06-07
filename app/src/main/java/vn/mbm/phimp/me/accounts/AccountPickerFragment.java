package vn.mbm.phimp.me.accounts;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterAuthToken;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;

import vn.mbm.phimp.me.R;

/**
 * Created by pa1pal on 07/06/17.
 */

public class AccountPickerFragment extends DialogFragment {

    private TwitterAuthClient client = new TwitterAuthClient();

    //TwitterAuthConfig authConfig = new TwitterAuthConfig(getString(R.string.twitter_CONSUMER_KEY), getString(R.string.twitter_CONSUMER_SECRET));


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
                .setItems(R.array.accounts_array, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case 0:
                                client.authorize(getActivity(), new Callback<TwitterSession>() {
                                    @Override
                                    public void success(Result<TwitterSession> result) {

                                        TwitterSession session = TwitterCore.getInstance().getSessionManager().getActiveSession();
                                        TwitterAuthToken authToken = session.getAuthToken();

                                        Log.d("Twitter Credentials", session.toString());
                                    }

                                    @Override
                                    public void failure(TwitterException e) {

                                    }
                                });
                        }
                        // The 'which' argument contains the index position
                        // of the selected item
                    }
                });
        return builder.create();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        client.onActivityResult(requestCode, resultCode, data);
    }

}
