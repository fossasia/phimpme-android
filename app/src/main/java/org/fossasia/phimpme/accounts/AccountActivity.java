package org.fossasia.phimpme.accounts;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Switch;
import android.widget.Toast;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.session.AppKeyPair;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.pinterest.android.pdk.PDKCallback;
import com.pinterest.android.pdk.PDKClient;
import com.pinterest.android.pdk.PDKException;
import com.pinterest.android.pdk.PDKResponse;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;

import org.fossasia.phimpme.R;
import org.fossasia.phimpme.base.PhimpmeProgressBarHandler;
import org.fossasia.phimpme.base.RecyclerItemClickListner;
import org.fossasia.phimpme.base.ThemedActivity;
import org.fossasia.phimpme.data.local.AccountDatabase;
import org.fossasia.phimpme.data.local.DatabaseHelper;
import org.fossasia.phimpme.nextcloud.NextCloudAuth;
import org.fossasia.phimpme.sharedimgur.ImgurAuthActivity;
import org.fossasia.phimpme.sharedrupal.DrupalLogin;
import org.fossasia.phimpme.sharewordpress.WordpressLoginActivity;
import org.fossasia.phimpme.utilities.ActivitySwitchHelper;
import org.fossasia.phimpme.utilities.BasicCallBack;
import org.fossasia.phimpme.utilities.Constants;
import org.fossasia.phimpme.utilities.SnackBarHandler;
import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmQuery;

/**
 * Created by pa1pal on 13/6/17.
 */

public class AccountActivity extends ThemedActivity implements AccountContract.View,
        RecyclerItemClickListner.OnItemClickListener {

    private Toolbar toolbar;
    private RecyclerView accountsRecyclerView;
    private AccountAdapter accountAdapter;
    private AccountPresenter accountPresenter;
    private Realm realm = Realm.getDefaultInstance();
    private RealmQuery<AccountDatabase> realmResult;
    private PhimpmeProgressBarHandler phimpmeProgressBarHandler;
    private TwitterAuthClient client;
    private CallbackManager callbackManager;
    private LoginManager loginManager;
    private AccountDatabase account;
    private DatabaseHelper databaseHelper;
    private Context context;
    private PDKClient pdkClient;

    public static String[] accountName = {"Facebook", "Twitter", "Drupal", "NextCloud", "Wordpress", "Pinterest", "Flickr", "Imgur", "Dropbox"};
    private static final int NEXTCLOUD_REQUEST_CODE = 1;
    private static final int RESULT_OK = 1;
    public static final int IMGUR_KEY_LOGGED_IN = 2;

    final static private String APP_KEY = "APP_KEY";
    final static private String APP_SECRET = "API_SECRET";

    private DropboxAPI<AndroidAuthSession> mDBApi;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        accountAdapter = new AccountAdapter();
        accountPresenter = new AccountPresenter(realm);
        phimpmeProgressBarHandler = new PhimpmeProgressBarHandler(this);
        accountPresenter.attachView(this);
        databaseHelper = new DatabaseHelper(realm);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        accountsRecyclerView = (RecyclerView) findViewById(R.id.accounts_recycler_view);
        client = new TwitterAuthClient();
        callbackManager = CallbackManager.Factory.create();
        setSupportActionBar(toolbar);
        loginManager = LoginManager.getInstance();
        toolbar.setPopupTheme(getPopupToolbarStyle());
        toolbar.setBackgroundColor(getPrimaryColor());
        setUpRecyclerView();
        // Calling presenter function to load data from database
        accountPresenter.loadFromDatabase();
        getSupportActionBar().setTitle(R.string.title_account);

        phimpmeProgressBarHandler.show();

        pdkClient = PDKClient.configureInstance(this, getResources().getString(R.string.pinterest_app_id));
        pdkClient.onConnect(this);
        pdkClient.setDebugMode(true);
        AppKeyPair appKeys = new AppKeyPair(APP_KEY, APP_SECRET);
        AndroidAuthSession session = new AndroidAuthSession(appKeys);
        mDBApi = new DropboxAPI<AndroidAuthSession>(session);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_accounts_activity, menu);
        return true;
    }

    @Override
    public void setUpRecyclerView() {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        accountsRecyclerView.setLayoutManager(layoutManager);
        accountsRecyclerView.setAdapter(accountAdapter);
        accountsRecyclerView.addOnItemTouchListener(new RecyclerItemClickListner(this, this));
    }

    @Override
    public void setUpAdapter(@NotNull RealmQuery<AccountDatabase> accountDetails) {
        this.realmResult = accountDetails;
        accountAdapter.setResults(realmResult);
    }

    @Override
    public void showError() {
        Toast.makeText(this, "No account signed in", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showComplete() {
        phimpmeProgressBarHandler.hide();
    }

    @Override
    public int getContentViewId() {
        return R.layout.activity_accounts;
    }

    @Override
    public int getNavigationMenuItemId() {
        return R.id.navigation_accounts;
    }

    @Override
    public void onItemClick(final View childView, final int position) {
        final Switch signInSignOut = (Switch) childView.findViewById(R.id.sign_in_sign_out_switch);

       /* boolean isSignedIn = realmResult.equalTo("name"
                , accountsList[position]).isValid();
        boolean isChecked = signInSignOut.isChecked();*/
        //String name = realmResult.equalTo("name", accountsList[position]).findAll().get(0).getName();

        if (!signInSignOut.isChecked()) {
            switch (position) {
                case 0:
                    // FacebookSdk.sdkInitialize(this);
                    signInFacebook(childView);
                    accountPresenter.loadFromDatabase();
                    break;

                case 1:
                    signInTwitter();
                    accountPresenter.loadFromDatabase();
                    break;

                case 2:
                    Intent drupalShare = new Intent(getContext(), DrupalLogin.class);
                    startActivity(drupalShare);
                    break;

                case 3:
                    Intent nextCloudShare = new Intent(getContext(), NextCloudAuth.class);
                    startActivityForResult(nextCloudShare, NEXTCLOUD_REQUEST_CODE);
                    break;

                case 4:
                    Intent WordpressShare = new Intent(this, WordpressLoginActivity.class);
                    startActivity(WordpressShare);
                    break;

                case 5:
                    signInPinterest();break;
                case 7:
                    signInImgur();
                    break;

                case 8:
                    signInDropbox();
                    accountPresenter.loadFromDatabase();
                    break;

                default:
                    Toast.makeText(this, R.string.feature_not_present,
                            Toast.LENGTH_SHORT).show();
            }
        } else {
            new AlertDialog.Builder(this)
                    .setMessage(accountName[position])
                    .setTitle(getString(R.string.sign_out_dialog_title))
                    .setPositiveButton(R.string.yes_action,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    databaseHelper
                                            .deleteSignedOutAccount(accountName[position]);
                                    accountAdapter.notifyDataSetChanged();
                                    accountPresenter.loadFromDatabase();
                                    signInSignOut.setChecked(false);
                                }
                            })
                    .setNegativeButton(R.string.no_action,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //TODO: Implement negative button action
                                }
                            })
                    .show();
        }
    }

    private void signInDropbox() {
        if (accountPresenter.checkAlreadyExist(accountName[7]))
            Toast.makeText(getApplicationContext(), getString(R.string.already_signed_in), Toast.LENGTH_SHORT).show();
        else
            mDBApi.getSession().startOAuth2Authentication(this);
    }

    private void signInImgur() {
        if (accountPresenter.checkAlreadyExist(accountName[7])) {
            Toast.makeText(this, R.string.already_signed_in,
                    Toast.LENGTH_SHORT).show();
        }else {
            BasicCallBack basicCallBack = new BasicCallBack() {
                @Override
                public void callBack(int status, Object data) {
                    if (status == Constants.SUCCESS){
                        Toast.makeText(getContext(), getResources().getString(R.string.account_logged), Toast.LENGTH_LONG).show();
                        if (data instanceof Bundle){
                            Bundle bundle = (Bundle)data;
                            realm.beginTransaction();
                            account = realm.createObject(AccountDatabase.class,
                                    accountName[7]);
                            account.setUsername(bundle.getString(getString(R.string.auth_username)));
                            account.setToken(bundle.getString(getString(R.string.auth_token)));
                            realm.commitTransaction();

                        }

                    }

                }
            };
            Intent i = new Intent(AccountActivity.this, ImgurAuthActivity.class);
            ImgurAuthActivity.setBasicCallBack(basicCallBack);
            startActivity(i);
        }

    }

    private void signInPinterest() {

        if (accountPresenter.checkAlreadyExist(accountName[5])) {
            Toast.makeText(this, R.string.already_signed_in,
                    Toast.LENGTH_SHORT).show();
        } else {
            List scopes = new ArrayList<String>();
            scopes.add(PDKClient.PDKCLIENT_PERMISSION_READ_PUBLIC);
            scopes.add(PDKClient.PDKCLIENT_PERMISSION_WRITE_PUBLIC);
            scopes.add(PDKClient.PDKCLIENT_PERMISSION_READ_RELATIONSHIPS);
            scopes.add(PDKClient.PDKCLIENT_PERMISSION_WRITE_RELATIONSHIPS);

            pdkClient.login(this, scopes, new PDKCallback() {
                @Override
                public void onSuccess(PDKResponse response) {
                    Log.d(getClass().getName(), response.getData().toString());

                    // Begin realm transaction
                    realm.beginTransaction();

                    // Creating Realm object for AccountDatabase Class
                    account = realm.createObject(AccountDatabase.class,
                            accountName[5]);

                    PDKClient.getInstance().getPath("me/", null, new PDKCallback() {
                        @Override
                        public void onSuccess(PDKResponse response) {

                        }
                    });

                    // Writing values in Realm database
                    account.setUsername(response.getUser().getFirstName() + " " + response.getUser().getLastName());

                    // Finally committing the whole data
                    realm.commitTransaction();

                    Toast.makeText(AccountActivity.this, R.string.success, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(PDKException exception) {
                    Log.e(getClass().getName(), exception.getDetailMessage());
                    Toast.makeText(AccountActivity.this, R.string.fail, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public void onItemLongPress(View childView, int position) {
        // TODO: long press to implemented
    }

    /**
     * Create twitter login and session
     */
    public void signInTwitter() {
        /**
         * When user clicks then we first check if it is already exist.
         */
        if (accountPresenter.checkAlreadyExist(accountName[1])) {
            Toast.makeText(this, R.string.already_signed_in,
                    Toast.LENGTH_SHORT).show();
        } else {
            client.authorize(this, new Callback<TwitterSession>() {
                @Override
                public void success(Result<TwitterSession> result) {

                    // Begin realm transaction
                    realm.beginTransaction();

                    // Creating Realm object for AccountDatabase Class
                    account = realm.createObject(AccountDatabase.class,
                            accountName[1]);

                    // Creating twitter session, after user authenticate
                    // in twitter popup
                    TwitterSession session = TwitterCore.getInstance()
                            .getSessionManager().getActiveSession();
                    Log.d("Twitter Credentials", session.toString());


                    // Writing values in Realm database
                    account.setUsername(session.getUserName());
                    account.setToken(String.valueOf(session.getAuthToken()));

                    // Finally committing the whole data
                    realm.commitTransaction();
                }

                @Override
                public void failure(TwitterException e) {
                    // TODO: implement on failure
                }
            });
        }
    }

    /**
     * Create Facebook login and session
     *
     * @param childView
     */
    public void signInFacebook(final View childView) {
        loginManager = LoginManager.getInstance();
        if (accountPresenter.checkAlreadyExist(accountName[0])) {
            Toast.makeText(this, R.string.already_signed_in,
                    Toast.LENGTH_SHORT).show();
        } else {
            List<String> permissionNeeds = Arrays.asList("publish_actions");

            loginManager.logInWithReadPermissions(this, Arrays.asList("email", "public_profile"));
            //loginManager.logInWithPublishPermissions(this, permissionNeeds);

            loginManager.registerCallback(callbackManager,
                    new FacebookCallback<LoginResult>() {
                        @Override
                        public void onSuccess(LoginResult loginResult) {
                            // Begin realm transaction
                            realm.beginTransaction();

                            // Creating Realm object for AccountDatabase Class
                            account = realm.createObject(AccountDatabase.class,
                                    accountName[0]);

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
                                            Log.v("LoginActivity", graphResponse.toString());
                                            try {
                                                account.setUsername(jsonObject
                                                        .getString("email   "));
                                            } catch (JSONException e) {
                                                Log.e("LoginAct", e.toString());
                                            }
                                        }
                                    });

                            // Finally committing the whole data
                            realm.commitTransaction();

                        }

                        @Override
                        public void onCancel() {
                            SnackBarHandler.show(childView,
                                    getString(R.string.facebook_login_cancel));
                        }

                        @Override
                        public void onError(FacebookException e) {
                            SnackBarHandler.show(childView,
                                    getString(R.string.facebook_login_error));
                            Log.d("error", e.toString());
                        }
                    });
            accountPresenter.loadFromDatabase();
        }
    }

    @Override
    public Context getContext() {
        this.context = this;
        return context;
    }

    @Override
    public void onResume() {
        super.onResume();
        ActivitySwitchHelper.setContext(this);
        dropboxAuthentication();
        setStatusBarColor();
        setNavBarColor();
        accountPresenter.loadFromDatabase();
    }

    private void dropboxAuthentication() {
        if (mDBApi.getSession().authenticationSuccessful()) {
            try {
                // Required to complete auth, sets the access token on the session
                mDBApi.getSession().finishAuthentication();

                String accessToken = mDBApi.getSession().getOAuth2AccessToken();

                realm.beginTransaction();

                // Creating Realm object for AccountDatabase Class
                account = realm.createObject(AccountDatabase.class,
                        accountName[8]);

                // Writing values in Realm database

                account.setUsername(accountName[8]);
                account.setToken(String.valueOf(accessToken));

                // Finally committing the whole data
                realm.commitTransaction();

            } catch (IllegalStateException e) {
                Log.i("DbAuthLog", "Error authenticating", e);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        client.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        pdkClient.onOauthResponse(requestCode, resultCode,
                data);

        if (requestCode == NEXTCLOUD_REQUEST_CODE && resultCode == RESULT_OK){
            // Begin realm transaction
            realm.beginTransaction();

            // Creating Realm object for AccountDatabase Class
            account = realm.createObject(AccountDatabase.class,
                    accountName[3]);

            // Writing values in Realm database
            account.setServerUrl(data.getStringExtra(getString(R.string.server_url)));
            account.setUsername(data.getStringExtra(getString(R.string.auth_username)));
            account.setPassword(data.getStringExtra(getString(R.string.auth_password)));

            // Finally committing the whole data
            realm.commitTransaction();
        }
    }
}
