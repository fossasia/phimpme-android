package org.fossasia.phimpme.accounts;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

import com.box.androidsdk.content.BoxConfig;
import com.box.androidsdk.content.auth.BoxAuthentication;
import com.box.androidsdk.content.models.BoxSession;
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
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.pinterest.android.pdk.PDKCallback;
import com.pinterest.android.pdk.PDKClient;
import com.pinterest.android.pdk.PDKException;
import com.pinterest.android.pdk.PDKResponse;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;

import org.fossasia.phimpme.R;
import org.fossasia.phimpme.base.PhimpmeProgressBarHandler;
import org.fossasia.phimpme.base.RecyclerItemClickListner;
import org.fossasia.phimpme.base.ThemedActivity;
import org.fossasia.phimpme.data.local.AccountDatabase;
import org.fossasia.phimpme.data.local.DatabaseHelper;
import org.fossasia.phimpme.share.nextcloud.NextCloudAuth;
import org.fossasia.phimpme.share.owncloud.OwnCloudActivity;
import org.fossasia.phimpme.share.imgur.ImgurAuthActivity;
import org.fossasia.phimpme.share.drupal.DrupalLogin;
import org.fossasia.phimpme.share.flickr.FlickrActivity;
import org.fossasia.phimpme.share.twitter.LoginActivity;
import org.fossasia.phimpme.share.wordpress.WordpressLoginActivity;
import org.fossasia.phimpme.utilities.ActivitySwitchHelper;
import org.fossasia.phimpme.utilities.BasicCallBack;
import org.fossasia.phimpme.utilities.SnackBarHandler;
import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmQuery;

import static org.fossasia.phimpme.data.local.AccountDatabase.AccountName.BOX;
import static org.fossasia.phimpme.data.local.AccountDatabase.AccountName.DROPBOX;
import static org.fossasia.phimpme.data.local.AccountDatabase.AccountName.FACEBOOK;
import static org.fossasia.phimpme.data.local.AccountDatabase.AccountName.GOOGLEPLUS;
import static org.fossasia.phimpme.data.local.AccountDatabase.AccountName.IMGUR;
import static org.fossasia.phimpme.data.local.AccountDatabase.AccountName.NEXTCLOUD;
import static org.fossasia.phimpme.data.local.AccountDatabase.AccountName.OWNCLOUD;
import static org.fossasia.phimpme.data.local.AccountDatabase.AccountName.PINTEREST;
import static org.fossasia.phimpme.data.local.AccountDatabase.AccountName.TWITTER;
import static org.fossasia.phimpme.utilities.Constants.BOX_CLIENT_ID;
import static org.fossasia.phimpme.utilities.Constants.BOX_CLIENT_SECRET;
import static org.fossasia.phimpme.utilities.Constants.SUCCESS;

/**
 * Created by pa1pal on 13/6/17.
 */

public class AccountActivity extends ThemedActivity implements AccountContract.View,
        RecyclerItemClickListner.OnItemClickListener, GoogleApiClient.OnConnectionFailedListener {

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
    private GoogleApiClient mGoogleApiClient;


    public static String[] accountName = { "Facebook", "Twitter", "Drupal", "NextCloud", "Wordpress"
            , "Pinterest", "Flickr", "Imgur", "Dropbox", "OwnCloud", "Googleplus", "Box"};

    private static final int NEXTCLOUD_REQUEST_CODE = 3;
    private static final int OWNCLOUD_REQUEST_CODE = 9;
    private static final int RESULT_OK = 1;
    public static final int IMGUR_KEY_LOGGED_IN = 2;

    final static private String APP_KEY = "APP_KEY";
    final static private String APP_SECRET = "API_SECRET";
    private static final int RC_SIGN_IN = 9001;
    private DropboxAPI<AndroidAuthSession> mDBApi;
    private BoxSession sessionBox;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        accountAdapter = new AccountAdapter(getAccentColor(), getPrimaryColor());
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
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        // Build a GoogleApiClient with access to the Google Sign-In API and the
        // options specified by gso.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, AccountActivity.this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        configureBoxClient();
    }

    private void configureBoxClient() {
        BoxConfig.CLIENT_ID = BOX_CLIENT_ID;
        BoxConfig.CLIENT_SECRET = BOX_CLIENT_SECRET;
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
        final SwitchCompat signInSignOut = (SwitchCompat) childView.findViewById(R.id.sign_in_sign_out_switch);
        final String name = AccountDatabase.AccountName.values()[position].toString();
       /* boolean isSignedIn = realmResult.equalTo("name"
                , accountsList[position]).isValid();
        boolean isChecked = signInSignOut.isChecked();*/
        //String name = realmResult.equalTo("name", accountsList[position]).findAll().get(0).getName();

        if (!signInSignOut.isChecked()) {
            switch (AccountDatabase.AccountName.values()[position]) {
                case FACEBOOK:
                    // FacebookSdk.sdkInitialize(this);
                    signInFacebook(childView);
                    accountPresenter.loadFromDatabase();
                    break;

                case TWITTER:
                    signInTwitter();

                    //accountPresenter.loadFromDatabase();
                    break;

                case DRUPAL:
                    Intent drupalShare = new Intent(getContext(), DrupalLogin.class);
                    startActivity(drupalShare);
                    break;

                case NEXTCLOUD:
                    Intent nextCloudShare = new Intent(getContext(), NextCloudAuth.class);
                    startActivityForResult(nextCloudShare, NEXTCLOUD_REQUEST_CODE);
                    break;

                case WORDPRESS:
                    Intent WordpressShare = new Intent(this, WordpressLoginActivity.class);
                    startActivity(WordpressShare);
                    break;

                case PINTEREST:
                    signInPinterest();
                    break;

                case FLICKR:
                    Intent intent = new Intent(getApplicationContext(),
                            FlickrActivity.class);
                    FlickrActivity.setFilename(null);
                    startActivity(intent);
                    break;

                case IMGUR:
                    signInImgur();
                    break;

                case DROPBOX:
                    signInDropbox();
                    break;

                case OWNCLOUD:
                    Intent ownCloudShare = new Intent(getContext(), OwnCloudActivity.class);
                    startActivityForResult(ownCloudShare, OWNCLOUD_REQUEST_CODE);
                    break;

                case GOOGLEPLUS:
                    signInGooglePlus();
                    break;

                case BOX:
                    sessionBox = new BoxSession(AccountActivity.this);
                    sessionBox.authenticate();
                    break;

                default:
                    Toast.makeText(this, R.string.feature_not_present,
                            Toast.LENGTH_SHORT).show();
            }
        } else {
            new AlertDialog.Builder(this)
                    .setMessage(name)
                    .setTitle(getString(R.string.sign_out_dialog_title))
                    .setPositiveButton(R.string.yes_action,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    databaseHelper
                                            .deleteSignedOutAccount(name);
                                    accountAdapter.notifyDataSetChanged();
                                    accountPresenter.loadFromDatabase();
                                    signInSignOut.setChecked(false);
                                    BoxAuthentication.getInstance().logoutAllUsers(AccountActivity.this);
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

    private void signInGooglePlus() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void signInDropbox() {
        if (accountPresenter.checkAlreadyExist(DROPBOX))
            Toast.makeText(getApplicationContext(), getString(R.string.already_signed_in), Toast.LENGTH_SHORT).show();
        else
            mDBApi.getSession().startOAuth2Authentication(this);
    }

    private void signInImgur() {
        if (accountPresenter.checkAlreadyExist(IMGUR)) {
            Toast.makeText(this, R.string.already_signed_in,
                    Toast.LENGTH_SHORT).show();
        }else {
            BasicCallBack basicCallBack = new BasicCallBack() {
                @Override
                public void callBack(int status, Object data) {
                    if (status == SUCCESS){
                        Toast.makeText(getContext(), getResources().getString(R.string.account_logged), Toast.LENGTH_LONG).show();
                        if (data instanceof Bundle){
                            Bundle bundle = (Bundle)data;
                            realm.beginTransaction();
                            account = realm.createObject(AccountDatabase.class,
                                    IMGUR.toString());
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

        if (accountPresenter.checkAlreadyExist(PINTEREST)) {
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
                            PINTEREST.toString());

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

        if (accountPresenter.checkAlreadyExist(TWITTER)) {
            Toast.makeText(this, R.string.already_signed_in,
                    Toast.LENGTH_SHORT).show();
        } else {

            BasicCallBack basicCallBack = new BasicCallBack() {
                @Override
                public void callBack(int status, Object data) {
                    if (status == SUCCESS){
                        Toast.makeText(getContext(), getResources().getString(R.string.account_logged), Toast.LENGTH_LONG).show();
                        if (data instanceof Bundle){
                            Bundle bundle = (Bundle)data;
                            realm.beginTransaction();
                            account = realm.createObject(AccountDatabase.class,
                                    TWITTER.toString());
                            account.setUsername(bundle.getString(getString(R.string.auth_username)));
                            account.setToken(bundle.getString(getString(R.string.auth_token)));
                            realm.commitTransaction();
                        }
                    }
                }
            };

            Intent i = new Intent(AccountActivity.this, LoginActivity.class);
            LoginActivity.setBasicCallBack(basicCallBack);
            startActivity(i);
        }

    }


    /**
     * Create Facebook login and session
     *
     * @param childView
     */
    public void signInFacebook(final View childView) {
        loginManager = LoginManager.getInstance();
        if (accountPresenter.checkAlreadyExist(FACEBOOK)) {
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
                                    FACEBOOK.toString());

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
        boxAuthentication();
        setStatusBarColor();
        setNavBarColor();
        accountPresenter.loadFromDatabase();
    }

    private void boxAuthentication() {
        if(sessionBox != null && sessionBox.getUser()!=null){
            String accessToken = sessionBox.getAuthInfo().accessToken();

            realm.beginTransaction();

            // Creating Realm object for AccountDatabase Class
            account = realm.createObject(AccountDatabase.class,
                    BOX.toString());

            // Writing values in Realm database

            account.setUsername(sessionBox.getUser().getName());
            account.setToken(String.valueOf(accessToken));

            // Finally committing the whole data
            realm.commitTransaction();
            accountPresenter.loadFromDatabase();
        }
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
                        DROPBOX.toString());

                // Writing values in Realm database

                account.setUsername(DROPBOX.toString());
                account.setToken(String.valueOf(accessToken));

                // Finally committing the whole data
                realm.commitTransaction();

            } catch (IllegalStateException e) {
                Log.i("DbAuthLog", "Error authenticating", e);
            }
            accountPresenter.loadFromDatabase();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        client.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        pdkClient.onOauthResponse(requestCode, resultCode,
                data);

        if ((requestCode == OWNCLOUD_REQUEST_CODE && resultCode == RESULT_OK)
                || (requestCode == NEXTCLOUD_REQUEST_CODE && resultCode == RESULT_OK)){
            // Begin realm transaction
            realm.beginTransaction();

            if (requestCode == NEXTCLOUD_REQUEST_CODE){
                account = realm.createObject(AccountDatabase.class,
                        NEXTCLOUD.toString());
            } else {
                account = realm.createObject(AccountDatabase.class,
                        OWNCLOUD.toString());
            }

            // Writing values in Realm database
            account.setServerUrl(data.getStringExtra(getString(R.string.server_url)));
            account.setUsername(data.getStringExtra(getString(R.string.auth_username)));
            account.setPassword(data.getStringExtra(getString(R.string.auth_password)));

            // Finally committing the whole data
            realm.commitTransaction();
        }
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();//acct.getDisplayName()
            Toast.makeText(AccountActivity.this, R.string.success, Toast.LENGTH_SHORT).show();
            // Begin realm transaction
            realm.beginTransaction();

            // Creating Realm object for AccountDatabase Class
            account = realm.createObject(AccountDatabase.class,
                    GOOGLEPLUS.name());

            account.setUsername(acct.getDisplayName());

            // Finally committing the whole data
            realm.commitTransaction();
        } else {
            // Signed out, show unauthenticated UI.
            Toast.makeText(AccountActivity.this, R.string.fail, Toast.LENGTH_SHORT).show();
            //updateUI(false);
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(AccountActivity.this, "Connection Failed", Toast.LENGTH_SHORT).show();
    }
}
