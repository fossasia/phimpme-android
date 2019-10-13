package org.fossasia.phimpme.accounts;

import static org.fossasia.phimpme.R.string.no_account_signed_in;
import static org.fossasia.phimpme.data.local.AccountDatabase.AccountName.BOX;
import static org.fossasia.phimpme.data.local.AccountDatabase.AccountName.DROPBOX;
import static org.fossasia.phimpme.data.local.AccountDatabase.AccountName.IMGUR;
import static org.fossasia.phimpme.data.local.AccountDatabase.AccountName.NEXTCLOUD;
import static org.fossasia.phimpme.data.local.AccountDatabase.AccountName.OWNCLOUD;
import static org.fossasia.phimpme.data.local.AccountDatabase.AccountName.PINTEREST;
import static org.fossasia.phimpme.utilities.Constants.BOX_CLIENT_ID;
import static org.fossasia.phimpme.utilities.Constants.BOX_CLIENT_SECRET;
import static org.fossasia.phimpme.utilities.Constants.PINTEREST_APP_ID;
import static org.fossasia.phimpme.utilities.Constants.SUCCESS;
import static org.fossasia.phimpme.utilities.Utils.checkNetwork;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.box.androidsdk.content.BoxConfig;
import com.box.androidsdk.content.auth.BoxAuthentication;
import com.box.androidsdk.content.models.BoxSession;
import com.dropbox.core.android.Auth;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;
import io.realm.Realm;
import io.realm.RealmQuery;
import java.util.ArrayList;
import org.fossasia.phimpme.R;
import org.fossasia.phimpme.base.PhimpmeProgressBarHandler;
import org.fossasia.phimpme.base.RecyclerItemClickListner;
import org.fossasia.phimpme.base.ThemedActivity;
import org.fossasia.phimpme.data.local.AccountDatabase;
import org.fossasia.phimpme.data.local.DatabaseHelper;
import org.fossasia.phimpme.gallery.activities.LFMainActivity;
import org.fossasia.phimpme.gallery.activities.SettingsActivity;
import org.fossasia.phimpme.gallery.util.AlertDialogsHelper;
import org.fossasia.phimpme.gallery.util.ThemeHelper;
import org.fossasia.phimpme.share.flickr.FlickrActivity;
import org.fossasia.phimpme.share.imgur.ImgurAuthActivity;
import org.fossasia.phimpme.share.nextcloud.NextCloudAuth;
import org.fossasia.phimpme.share.owncloud.OwnCloudActivity;
import org.fossasia.phimpme.share.pinterest.PinterestAuthActivity;
import org.fossasia.phimpme.share.twitter.LoginActivity;
import org.fossasia.phimpme.utilities.ActivitySwitchHelper;
import org.fossasia.phimpme.utilities.BasicCallBack;
import org.fossasia.phimpme.utilities.Constants;
import org.fossasia.phimpme.utilities.SnackBarHandler;
import org.jetbrains.annotations.NotNull;

/** Created by pa1pal on 13/6/17. */
public class AccountActivity extends ThemedActivity
    implements AccountContract.View, RecyclerItemClickListner.OnItemClickListener {

  private static final int NEXTCLOUD_REQUEST_CODE = 3;
  private static final int OWNCLOUD_REQUEST_CODE = 9;
  private static final int RESULT_OK = 1;
  public static final String BROWSABLE = "android.intent.category.BROWSABLE";

  @BindView(R.id.accounts_parent)
  RelativeLayout parentLayout;

  @BindView(R.id.accounts_recycler_view)
  RecyclerView accountsRecyclerView;

  @BindView(R.id.toolbar)
  Toolbar toolbar;

  @BindView(R.id.bottombar)
  BottomNavigationView bottomNavigationView;

  @BindView(R.id.accounts)
  CoordinatorLayout coordinatorLayout;

  private AccountAdapter accountAdapter;
  private AccountPresenter accountPresenter;
  private Realm realm = Realm.getDefaultInstance();
  private RealmQuery<AccountDatabase> realmResult;
  private PhimpmeProgressBarHandler phimpmeProgressBarHandler;
  private TwitterAuthClient client;
  private AccountDatabase account;
  private DatabaseHelper databaseHelper;
  private Context context;
//  private PDKClient pdkClient;
  // private GoogleApiClient mGoogleApiClient;
  private BoxSession sessionBox;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    ButterKnife.bind(this);
    ActivitySwitchHelper.setContext(this);
    parentLayout.setBackgroundColor(getBackgroundColor());
    overridePendingTransition(R.anim.right_to_left, R.anim.left_to_right);
    parentLayout.setBackgroundColor(getBackgroundColor());
    accountAdapter = new AccountAdapter();
    accountPresenter = new AccountPresenter(realm);
    phimpmeProgressBarHandler = new PhimpmeProgressBarHandler(this);
    accountPresenter.attachView(this);
    databaseHelper = new DatabaseHelper(realm);
    client = new TwitterAuthClient();
    setSupportActionBar(toolbar);
    ThemeHelper themeHelper = new ThemeHelper(getContext());
    toolbar.setPopupTheme(getPopupToolbarStyle());
    toolbar.setBackgroundColor(themeHelper.getPrimaryColor());
    bottomNavigationView.setBackgroundColor(themeHelper.getPrimaryColor());
    setUpRecyclerView();
    accountPresenter.loadFromDatabase(); // Calling presenter function to load data from database
    getSupportActionBar().setTitle(R.string.title_account);
    phimpmeProgressBarHandler.show();
//    pdkClient = PDKClient.configureInstance(this, PINTEREST_APP_ID);
//    pdkClient.onConnect(this);
//    setDebugMode(true);
    //  googleApiClient();
    configureBoxClient();
  }

  /*    private void googleApiClient(){
      // Configure sign-in to request the user's ID, email address, and basic
      // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
      GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
              .requestEmail()
              .build();
      // Build a GoogleApiClient with access to the Google Sign-In API and the
      // options specified by gso.
      mGoogleApiClient = new GoogleApiClient.Builder(this)
              .enableAutoManage(this, AccountActivity.this)
              .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
              .build();
  }*/
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
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.action_account_settings:
        startActivity(new Intent(AccountActivity.this, SettingsActivity.class));
        return true;
    }
    return super.onOptionsItemSelected(item);
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
    SnackBarHandler.create(coordinatorLayout, getString(no_account_signed_in)).show();
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
    if (!checkNetwork(this, parentLayout)) return;
    final SwitchCompat signInSignOut = childView.findViewById(R.id.sign_in_sign_out_switch);
    final String name = AccountDatabase.AccountName.values()[position].toString();

    if (!signInSignOut.isChecked()) {
      if (!checkNetwork(this, parentLayout)) return;
      switch (AccountDatabase.AccountName.values()[position]) {
        case TWITTER:
          signInTwitter();
          break;

          /*case DRUPAL:
          Intent drupalShare = new Intent(getContext(), DrupalLogin.class);
          startActivity(drupalShare);
          break;*/

        case NEXTCLOUD:
          Intent nextCloudShare = new Intent(getContext(), NextCloudAuth.class);
          startActivityForResult(nextCloudShare, NEXTCLOUD_REQUEST_CODE);
          break;

          /*case WORDPRESS:
          Intent WordpressShare = new Intent(this, WordpressLoginActivity.class);
          startActivity(WordpressShare);
          break;*/
          /* case GOOGLEDRIVE:
          signInGoogleDrive();
          break;*/

        case PINTEREST:
          signInPinterest();
          break;

        case FLICKR:
          signInFlickr();
          break;

        case IMGUR:
          signInImgur();
          break;

        case DROPBOX:
          Auth.startOAuth2Authentication(this, Constants.DROPBOX_APP_KEY);
          break;

        case OWNCLOUD:
          Intent ownCloudShare = new Intent(getContext(), OwnCloudActivity.class);
          startActivityForResult(ownCloudShare, OWNCLOUD_REQUEST_CODE);
          break;

        case BOX:
          sessionBox = new BoxSession(AccountActivity.this);
          sessionBox.authenticate();
          break;

        case TUMBLR:
          // signInTumblr();
          break;

          /*case ONEDRIVE:
          signInOneDrive();
          break;*/
      }
    } else {
      AlertDialog alertDialog =
          new AlertDialog.Builder(this)
              .setMessage(name)
              .setTitle(getString(R.string.sign_out_dialog_title))
              .setPositiveButton(
                  R.string.yes_action,
                  new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                      databaseHelper.deleteSignedOutAccount(name);
                      accountAdapter.notifyDataSetChanged();
                      accountPresenter.loadFromDatabase();
                      signInSignOut.setChecked(false);
                      BoxAuthentication.getInstance().logoutAllUsers(AccountActivity.this);
                    }
                  })
              .setNegativeButton(
                  R.string.no_action,
                  new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                      // TODO: Implement negative button action
                    }
                  })
              .create();
      alertDialog.show();
      AlertDialogsHelper.setButtonTextColor(
          new int[] {DialogInterface.BUTTON_POSITIVE, DialogInterface.BUTTON_NEGATIVE},
          getAccentColor(),
          alertDialog);
    }
  }

  private void signInFlickr() {
    BasicCallBack basicCallBack =
        new BasicCallBack() {
          @Override
          public void callBack(int status, Object data) {
            if (status == SUCCESS) {
              SnackBarHandler.create(coordinatorLayout, getString(R.string.logged_in_flickr))
                  .show();
            }
          }
        };
    Intent intent = new Intent(this, FlickrActivity.class);
    FlickrActivity.setBasicCallBack(basicCallBack);
    startActivity(intent);
  }

  /* private void signInTumblr() {
      LoginListener loginListener = new LoginListener() {
          @Override
          public void onLoginSuccessful(com.tumblr.loglr.LoginResult loginResult) {
              SnackBarHandler.show(coordinatorLayout, getString(R.string.logged_in_tumblr));
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
              SnackBarHandler.show(coordinatorLayout, R.string.error_volly);
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
  }*/

  private void signInImgur() {
    BasicCallBack basicCallBack =
        new BasicCallBack() {
          @Override
          public void callBack(int status, Object data) {
            if (status == SUCCESS) {
              SnackBarHandler.create(coordinatorLayout, getString(R.string.account_logged)).show();
              if (data instanceof Bundle) {
                Bundle bundle = (Bundle) data;
                realm.beginTransaction();
                account = realm.createObject(AccountDatabase.class, IMGUR.toString());
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

  private void signInPinterest() {
    startActivity(new Intent(this, PinterestAuthActivity.class));
//    ArrayList<String> scopes = new ArrayList<>();
//    scopes.add(PDKClient.PDKCLIENT_PERMISSION_READ_PUBLIC);
//    scopes.add(PDKClient.PDKCLIENT_PERMISSION_WRITE_PUBLIC);
//    scopes.add(PDKClient.PDKCLIENT_PERMISSION_READ_RELATIONSHIPS);
//    scopes.add(PDKClient.PDKCLIENT_PERMISSION_WRITE_RELATIONSHIPS);
//
//    pdkClient.login(
//        this,
//        scopes,
//        new PDKCallback() {
//          @Override
//          public void onSuccess(PDKResponse response) {
//            Log.d(getClass().getName(), response.getData().toString());
//            realm.beginTransaction();
//            account = realm.createObject(AccountDatabase.class, PINTEREST.toString());
//            account.setAccountname(PINTEREST);
//            account.setUsername(
//                response.getUser().getFirstName() + " " + response.getUser().getLastName());
//            realm.commitTransaction();
//            finish();
//            startActivity(getIntent());
//            SnackBarHandler.create(coordinatorLayout, getString(R.string.account_logged_pinterest))
//                .show();
//          }
//
//          @Override
//          public void onFailure(PDKException exception) {
//            Log.e(getClass().getName(), exception.getDetailMessage());
//            SnackBarHandler.create(coordinatorLayout, getString(R.string.pinterest_signIn_fail))
//                .show();
//          }
//        });
  }

  @Override
  public void onItemLongPress(View childView, int position) {
    // TODO: long press to implemented
  }

  /** Create twitter login and session */
  public void signInTwitter() {
    Intent i = new Intent(AccountActivity.this, LoginActivity.class);
    startActivity(i);
  }

  /** Create Facebook login and session */
  /* public void signInFacebook() {
      List<String> permissionNeeds = Arrays.asList("publish_actions");
      loginManager = LoginManager.getInstance();
      loginManager.logInWithPublishPermissions(this, permissionNeeds);
      //loginManager.logInWithReadPermissions(this, Arrays.asList("email", "public_profile"));
      loginManager.registerCallback(callbackManager,
              new FacebookCallback<LoginResult>() {
                  @Override
                  public void onSuccess(LoginResult loginResult) {
                      realm.beginTransaction();
                      account = realm.createObject(AccountDatabase.class, FACEBOOK.toString());
                      account.setUsername(loginResult.getAccessToken().getUserId());

                      GraphRequest request = GraphRequest.newMeRequest(
                              loginResult.getAccessToken(),
                              new GraphRequest.GraphJSONObjectCallback() {
                                  @Override
                                  public void onCompleted(@NonNls JSONObject jsonObject, GraphResponse graphResponse) {
                                      Log.v("LoginActivity", graphResponse.toString());
                                      try {
                                          account.setUsername(jsonObject.getString("name"));
                                          realm.commitTransaction();
                                          SnackBarHandler.show(coordinatorLayout, getString(R.string.logged_in_facebook));
                                      } catch (JSONException e) {
                                          Log.e("LoginAct", e.toString());
                                      }
                                  }
                              });
                      Bundle parameters = new Bundle();
                      parameters.putString("fields", "id,name");
                      request.setParameters(parameters);
                      request.executeAsync();
                  }

                  @Override
                  public void onCancel() {
                      SnackBarHandler.show(coordinatorLayout, getString(R.string.facebook_login_cancel));
                  }

                  @Override
                  public void onError(FacebookException e) {
                      SnackBarHandler.show(coordinatorLayout, getString(R.string.facebook_login_error));
                      Log.d("error", e.toString());
                  }
              });
  }*/

  @Override
  public Context getContext() {
    this.context = this;
    return context;
  }

  @Override
  public void onResume() {
    super.onResume();
    ActivitySwitchHelper.setContext(this);
    setNavigationBarColor(ThemeHelper.getPrimaryColor(this));
    toolbar.setBackgroundColor(ThemeHelper.getPrimaryColor(this));
    // dropboxAuthentication();
    boxAuthentication();
    if (Auth.getOAuth2Token() != null) {
      dropBoxAuthentication(Auth.getOAuth2Token());
    }
    setStatusBarColor();
    setNavBarColor();
    accountPresenter.loadFromDatabase();
    accountAdapter.updateTheme();
    accountAdapter.notifyDataSetChanged();
  }

  @Override
  public void onBackPressed() {
    Intent intent = new Intent(this, LFMainActivity.class);
    startActivity(intent);
    finish();
    overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
  }

  private void boxAuthentication() {
    if (sessionBox != null && sessionBox.getUser() != null) {
      String accessToken = sessionBox.getAuthInfo().accessToken();

      realm.beginTransaction();

      // Creating Realm object for AccountDatabase Class
      account = realm.createObject(AccountDatabase.class, BOX.toString());

      // Writing values in Realm database

      account.setUsername(sessionBox.getUser().getName());
      account.setToken(String.valueOf(accessToken));

      // Finally committing the whole data
      realm.commitTransaction();
      accountPresenter.loadFromDatabase();
    }
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    client.onActivityResult(requestCode, resultCode, data);
    // callbackManager.onActivityResult(requestCode, resultCode, data);
//    pdkClient.onOauthResponse(requestCode, resultCode, data);

    if ((requestCode == OWNCLOUD_REQUEST_CODE && resultCode == RESULT_OK)
        || (requestCode == NEXTCLOUD_REQUEST_CODE && resultCode == RESULT_OK)) {
      realm.beginTransaction();
      if (requestCode == NEXTCLOUD_REQUEST_CODE) {
        account = realm.createObject(AccountDatabase.class, NEXTCLOUD.toString());
      } else {
        account = realm.createObject(AccountDatabase.class, OWNCLOUD.toString());
      }
      account.setServerUrl(data.getStringExtra(getString(R.string.server_url)));
      account.setUsername(data.getStringExtra(getString(R.string.auth_username)));
      account.setPassword(data.getStringExtra(getString(R.string.auth_password)));
      realm.commitTransaction();
    }
    /*   if (requestCode == RC_SIGN_IN) {
        GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
        handleSignInResult(result);
    }*/
  }

  /*private void handleSignInResult(GoogleSignInResult result) {
      if (result.isSuccess()) {
          GoogleSignInAccount acct = result.getSignInAccount();//acct.getDisplayName()
          SnackBarHandler.show(parentLayout,R.string.success);
          realm.beginTransaction();
          account = realm.createObject(AccountDatabase.class, GOOGLEPLUS.name());account.setUsername(acct.getDisplayName());
          account.setUserId(acct.getId());
          realm.commitTransaction();
      } else {
          SnackBarHandler.show(parentLayout,R.string.google_auth_fail);
      }
  }*/

  // Handles the Auth Activity result of DropBox signIn flow
  private void dropBoxAuthentication(String token) {
    realm.beginTransaction();
    account = realm.createObject(AccountDatabase.class, DROPBOX.toString());
    account.setUsername(DROPBOX.toString());
    account.setToken(token);
    realm.commitTransaction();
  }
}
