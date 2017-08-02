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
import android.widget.RelativeLayout;

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
import com.tumblr.loglr.Interfaces.ExceptionHandler;
import com.tumblr.loglr.Interfaces.LoginListener;
import com.tumblr.loglr.Loglr;
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
import org.fossasia.phimpme.share.drupal.DrupalLogin;
import org.fossasia.phimpme.share.flickr.FlickrActivity;
import org.fossasia.phimpme.share.imgur.ImgurAuthActivity;
import org.fossasia.phimpme.share.nextcloud.NextCloudAuth;
import org.fossasia.phimpme.share.owncloud.OwnCloudActivity;
import org.fossasia.phimpme.share.tumblr.TumblrClient;
import org.fossasia.phimpme.share.flickr.FlickrHelper;
import org.fossasia.phimpme.share.twitter.LoginActivity;
import org.fossasia.phimpme.share.wordpress.WordpressLoginActivity;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmQuery;

import static com.pinterest.android.pdk.PDKClient.setDebugMode;
import static org.fossasia.phimpme.data.local.AccountDatabase.AccountName.BOX;
import static org.fossasia.phimpme.data.local.AccountDatabase.AccountName.DROPBOX;
import static org.fossasia.phimpme.data.local.AccountDatabase.AccountName.FACEBOOK;
import static org.fossasia.phimpme.data.local.AccountDatabase.AccountName.GOOGLEPLUS;
import static org.fossasia.phimpme.data.local.AccountDatabase.AccountName.IMGUR;
import static org.fossasia.phimpme.data.local.AccountDatabase.AccountName.NEXTCLOUD;
import static org.fossasia.phimpme.data.local.AccountDatabase.AccountName.OWNCLOUD;
import static org.fossasia.phimpme.data.local.AccountDatabase.AccountName.PINTEREST;
import static org.fossasia.phimpme.data.local.AccountDatabase.AccountName.TUMBLR;
import static org.fossasia.phimpme.data.local.AccountDatabase.AccountName.TWITTER;
import static org.fossasia.phimpme.utilities.Constants.BOX_CLIENT_ID;
import static org.fossasia.phimpme.utilities.Constants.BOX_CLIENT_SECRET;
import static org.fossasia.phimpme.utilities.Constants.SUCCESS;

/**
 * Created by pa1pal on 13/6/17.
 */

public class AccountActivity extends ThemedActivity implements AccountContract.View,
        RecyclerItemClickListner.OnItemClickListener, GoogleApiClient.OnConnectionFailedListener {

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
    private static final int NEXTCLOUD_REQUEST_CODE = 3;
    private static final int OWNCLOUD_REQUEST_CODE = 9;
    private static final int RESULT_OK = 1;
    final static private String APP_KEY = "APP_KEY";
    final static private String APP_SECRET = "API_SECRET";
    private static final int RC_SIGN_IN = 9001;
    private DropboxAPI<AndroidAuthSession> mDBApi;
    private BoxSession sessionBox;

    @BindView(R.id.accounts_parent)
    RelativeLayout parentLayout;

    @BindView(R.id.accounts_recycler_view)
    RecyclerView accountsRecyclerView;

    @BindView(R.id.toolbar)
    Toolbar toolbar;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        ActivitySwitchHelper.setContext(this);
        parentLayout.setBackgroundColor(getBackgroundColor());
        accountAdapter = new AccountAdapter(getAccentColor(), getPrimaryColor());
        accountPresenter = new AccountPresenter(realm);
        phimpmeProgressBarHandler = new PhimpmeProgressBarHandler(this);
        accountPresenter.attachView(this);
        databaseHelper = new DatabaseHelper(realm);
        client = new TwitterAuthClient();
        callbackManager = CallbackManager.Factory.create();
        setSupportActionBar(toolbar);
        loginManager = LoginManager.getInstance();
        toolbar.setPopupTheme(getPopupToolbarStyle());
        toolbar.setBackgroundColor(getPrimaryColor());
        setUpRecyclerView();
        accountPresenter.loadFromDatabase();  // Calling presenter function to load data from database
        getSupportActionBar().setTitle(R.string.title_account);
        phimpmeProgressBarHandler.show();
        pdkClient = PDKClient.configureInstance(this, getResources().getString(R.string.pinterest_app_id));
        pdkClient.onConnect(this);
        setDebugMode(true);
        setupDropBox();
        googleApiClient();
        configureBoxClient();
    }

    private void setupDropBox(){
        AppKeyPair appKeys = new AppKeyPair(APP_KEY, APP_SECRET);
        AndroidAuthSession session = new AndroidAuthSession(appKeys);
        mDBApi = new DropboxAPI<AndroidAuthSession>(session);
    }
    private void googleApiClient(){
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
        SnackBarHandler.show(parentLayout, getString(R.string.no_account_signed_in));
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
                    signInFacebook();
                    accountPresenter.loadFromDatabase();
                    break;

                case TWITTER:
                    signInTwitter();
                    accountPresenter.loadFromDatabase();
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
                    FlickrHelper.getInstance().setFilename(null);
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
                case TUMBLR:
                    signInTumblr();
                    break;

                default:
                    SnackBarHandler.show(parentLayout,R.string.feature_not_present);
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

    private void signInTumblr() {
        LoginListener loginListener = new LoginListener() {
            @Override
            public void onLoginSuccessful(com.tumblr.loglr.LoginResult loginResult) {
                SnackBarHandler.show(parentLayout,getString(R.string.logged_in_tumblr));
                realm.beginTransaction();
                account = realm.createObject(AccountDatabase.class,
                        TUMBLR.toString());
                account.setToken(loginResult.getOAuthToken());
                account.setSecret(loginResult.getOAuthTokenSecret());
                account.setUsername(TUMBLR.toString());
                realm.commitTransaction();
                TumblrClient tumblrClient = new TumblrClient();
                realm.beginTransaction();
                BasicCallBack basicCallBack = new BasicCallBack() {
                    @Override
                    public void callBack(int status, Object data) {
                        account.setUsername(data.toString());
                        realm.commitTransaction();
                    }
                };
                tumblrClient.getName(basicCallBack);
            }
        };
        ExceptionHandler exceptionHandler = new ExceptionHandler() {
            @Override
            public void onLoginFailed(RuntimeException e) {
            SnackBarHandler.show(parentLayout,R.string.error_volly);
            }
        };

        Loglr.getInstance()
                .setConsumerKey(Constants.TUMBLR_CONSUMER_KEY)
                .setConsumerSecretKey(Constants.TUMBLR_CONSUMER_SECRET)
                .setLoginListener(loginListener)
                .setExceptionHandler(exceptionHandler)
                .enable2FA(true)
                .setUrlCallBack(Constants.CALL_BACK_TUMBLR)
                .initiateInActivity(AccountActivity.this);
    }

    private void signInGooglePlus() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void signInDropbox() {
        if (accountPresenter.checkAlreadyExist(DROPBOX))
            SnackBarHandler.show(parentLayout,R.string.already_signed_in);
        else
            mDBApi.getSession().startOAuth2Authentication(this);
    }

    private void signInImgur() {
        if (accountPresenter.checkAlreadyExist(IMGUR)) {
            SnackBarHandler.show(parentLayout,R.string.already_signed_in);
        }else {
            BasicCallBack basicCallBack = new BasicCallBack() {
                @Override
                public void callBack(int status, Object data) {
                    if (status == SUCCESS){
                        SnackBarHandler.show(parentLayout,R.string.account_logged);
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
            SnackBarHandler.show(parentLayout,R.string.already_signed_in);
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
                    SnackBarHandler.show(parentLayout,R.string.success);
                }

                @Override
                public void onFailure(PDKException exception) {
                    Log.e(getClass().getName(), exception.getDetailMessage());
                    SnackBarHandler.show(parentLayout,R.string.fail);
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
            SnackBarHandler.show(parentLayout, getString(R.string.already_signed_in));
        } else {
            BasicCallBack basicCallBack = new BasicCallBack() {
                @Override
                public void callBack(int status, Object data) {
                    if (status == SUCCESS){
                        SnackBarHandler.show(parentLayout, getString(R.string.account_logged_twitter));
                        if (data instanceof Bundle){
                            Bundle bundle = (Bundle) data;
                            realm.beginTransaction();
                            account = realm.createObject(AccountDatabase.class, TWITTER.toString());
                            account.setAccountname(TWITTER);
                            account.setUsername(bundle.getString(getString(R.string.auth_username)));
                            account.setToken(bundle.getString(getString(R.string.auth_token)));
                            account.setSecret(bundle.getString(getString(R.string.auth_secret)));
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
     */
    public void signInFacebook() {
        loginManager = LoginManager.getInstance();
        if (accountPresenter.checkAlreadyExist(FACEBOOK)) {
            SnackBarHandler.show(parentLayout,R.string.already_signed_in);
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
                            SnackBarHandler.show(parentLayout,
                                    getString(R.string.facebook_login_cancel));
                        }

                        @Override
                        public void onError(FacebookException e) {
                            SnackBarHandler.show(parentLayout,
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
            SnackBarHandler.show(parentLayout,R.string.success);
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
            SnackBarHandler.show(parentLayout,R.string.fail);
            //updateUI(false);
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        SnackBarHandler.show(parentLayout,"Connection Failed");
    }


}