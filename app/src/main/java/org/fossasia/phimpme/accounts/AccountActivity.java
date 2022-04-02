package org.fossasia.phimpme.accounts;

import static com.pinterest.android.pdk.PDKClient.setDebugMode;
import static org.fossasia.phimpme.R.string.no_account_signed_in;
import static org.fossasia.phimpme.data.local.AccountDatabase.AccountName.BOX;
import static org.fossasia.phimpme.utilities.Constants.BOX_CLIENT_ID;
import static org.fossasia.phimpme.utilities.Constants.BOX_CLIENT_SECRET;
import static org.fossasia.phimpme.utilities.Constants.PINTEREST_APP_ID;
import static org.fossasia.phimpme.utilities.Constants.SUCCESS;
import static org.fossasia.phimpme.utilities.Utils.checkNetwork;

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
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.box.androidsdk.content.BoxConfig;
import com.box.androidsdk.content.auth.BoxAuthentication;
import com.box.androidsdk.content.models.BoxSession;
import com.dropbox.core.android.Auth;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.pinterest.android.pdk.PDKCallback;
import com.pinterest.android.pdk.PDKClient;
import com.pinterest.android.pdk.PDKException;
import com.pinterest.android.pdk.PDKResponse;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;
import io.realm.RealmQuery;
import java.util.ArrayList;
import org.fossasia.phimpme.MyApplication;
import org.fossasia.phimpme.R;
import org.fossasia.phimpme.base.PhimpmeProgressBarHandler;
import org.fossasia.phimpme.base.RecyclerItemClickListner;
import org.fossasia.phimpme.base.ThemedActivity;
import org.fossasia.phimpme.data.local.AccountDatabase;
import org.fossasia.phimpme.gallery.activities.LFMainActivity;
import org.fossasia.phimpme.gallery.activities.SettingsActivity;
import org.fossasia.phimpme.gallery.util.AlertDialogsHelper;
import org.fossasia.phimpme.gallery.util.ThemeHelper;
import org.fossasia.phimpme.share.flickr.FlickrActivity;
import org.fossasia.phimpme.share.imgur.ImgurAuthActivity;
import org.fossasia.phimpme.share.nextcloud.NextCloudAuth;
import org.fossasia.phimpme.share.owncloud.OwnCloudActivity;
import org.fossasia.phimpme.share.twitter.LoginActivity;
import org.fossasia.phimpme.utilities.ActivitySwitchHelper;
import org.fossasia.phimpme.utilities.BasicCallBack;
import org.fossasia.phimpme.utilities.Constants;
import org.fossasia.phimpme.utilities.SnackBarHandler;
import org.jetbrains.annotations.NotNull;

/** Created by pa1pal on 13/6/17. */
public class AccountActivity extends ThemedActivity
    implements RecyclerItemClickListner.OnItemClickListener {

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
  private PhimpmeProgressBarHandler phimpmeProgressBarHandler;
  private AccountViewModel accountViewModel;

  private TwitterAuthClient client;
  private PDKClient pdkClient;
  private BoxSession sessionBox;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    ButterKnife.bind(this);
    ActivitySwitchHelper.setContext(this);
    setSupportActionBar(toolbar);
    parentLayout.setBackgroundColor(getBackgroundColor());
    startSlideAnimation(2);
    phimpmeProgressBarHandler = new PhimpmeProgressBarHandler(this);
    ThemeHelper themeHelper = new ThemeHelper(this);
    toolbar.setPopupTheme(getPopupToolbarStyle());
    toolbar.setBackgroundColor(themeHelper.getPrimaryColor());
    bottomNavigationView.setBackgroundColor(themeHelper.getPrimaryColor());
    getSupportActionBar().setTitle(R.string.title_account);
    accountViewModel = ViewModelProviders.of(this).get(AccountViewModel.class);
    phimpmeProgressBarHandler.show();
    setUpRecyclerView();
    client = new TwitterAuthClient();
    pdkClient = PDKClient.configureInstance(this, PINTEREST_APP_ID);
    pdkClient.onConnect(this);
    setDebugMode(true);
    configureBoxClient();
    initObserver();
  }

  private void startSlideAnimation(int currentMenuItem) {
    if (((MyApplication) this.getApplication()).NavItem > currentMenuItem)
      overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    else if (((MyApplication) this.getApplication()).NavItem < currentMenuItem)
      overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    // Update the Global NavItem
    ((MyApplication) this.getApplication()).NavItem = currentMenuItem;
  }

  private void initObserver() {
    accountViewModel.error.observe(
        this,
        value -> {
          if (value) {
            SnackBarHandler.create(coordinatorLayout, getString(no_account_signed_in)).show();
            showComplete();
          }
        });

    accountViewModel.accountDetails.observe(this, this::setUpAdapter);
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
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.action_account_settings:
        startActivity(new Intent(AccountActivity.this, SettingsActivity.class));
        return true;
    }
    return super.onOptionsItemSelected(item);
  }

  public void setUpRecyclerView() {
    accountAdapter = new AccountAdapter();
    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
    accountsRecyclerView.setLayoutManager(layoutManager);
    accountsRecyclerView.setAdapter(accountAdapter);
    accountsRecyclerView.addOnItemTouchListener(new RecyclerItemClickListner(this, this));
  }

  public void setUpAdapter(@NotNull RealmQuery<AccountDatabase> accountDetails) {
    accountAdapter.setResults(accountDetails);
    showComplete();
  }

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

        case NEXTCLOUD:
          Intent nextCloudShare = new Intent(this, NextCloudAuth.class);
          startActivityForResult(nextCloudShare, accountViewModel.NEXTCLOUD_REQUEST_CODE);
          break;

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
          Intent ownCloudShare = new Intent(this, OwnCloudActivity.class);
          startActivityForResult(ownCloudShare, accountViewModel.OWNCLOUD_REQUEST_CODE);
          break;

        case BOX:
          sessionBox = new BoxSession(AccountActivity.this);
          sessionBox.authenticate(this);
          break;
      }
    } else {
      AlertDialog alertDialog =
          new AlertDialog.Builder(this)
              .setMessage(name)
              .setTitle(getString(R.string.sign_out_dialog_title))
              .setPositiveButton(
                  R.string.yes_action,
                  (dialog, which) -> {
                    signInSignOut.setChecked(false);
                    accountViewModel.deleteAccountFromDatabase(name);
                    accountViewModel.fetchAccountDetails();
                    if (name.equals(BOX.name())) {
                      BoxAuthentication.getInstance().logoutAllUsers(AccountActivity.this);
                    }
                  })
              .setNegativeButton(
                  R.string.no_action,
                  new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                      dialog.dismiss();
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

  private void signInImgur() {
    BasicCallBack basicCallBack =
        new BasicCallBack() {
          @Override
          public void callBack(int status, Object data) {
            if (status == SUCCESS) {
              SnackBarHandler.create(coordinatorLayout, getString(R.string.account_logged)).show();
              if (data instanceof Bundle) {
                Bundle bundle = (Bundle) data;
                accountViewModel.saveImgurAccount(
                    bundle.getString(getString(R.string.auth_username)),
                    bundle.getString(getString(R.string.auth_token)));
              }
            }
          }
        };
    Intent i = new Intent(AccountActivity.this, ImgurAuthActivity.class);
    ImgurAuthActivity.setBasicCallBack(basicCallBack);
    startActivity(i);
  }

  private void signInPinterest() {
    ArrayList<String> scopes = new ArrayList<>();
    scopes.add(PDKClient.PDKCLIENT_PERMISSION_READ_PUBLIC);
    scopes.add(PDKClient.PDKCLIENT_PERMISSION_WRITE_PUBLIC);
    scopes.add(PDKClient.PDKCLIENT_PERMISSION_READ_RELATIONSHIPS);
    scopes.add(PDKClient.PDKCLIENT_PERMISSION_WRITE_RELATIONSHIPS);

    pdkClient.login(
        this,
        scopes,
        new PDKCallback() {
          @Override
          public void onSuccess(PDKResponse response) {
            Log.d(getClass().getName(), response.getData().toString());
            accountViewModel.savePinterestToken(
                response.getUser().getFirstName() + " " + response.getUser().getLastName());
            finish();
            startActivity(getIntent());
            SnackBarHandler.create(coordinatorLayout, getString(R.string.account_logged_pinterest))
                .show();
          }

          @Override
          public void onFailure(PDKException exception) {
            Log.e(getClass().getName(), exception.getDetailMessage());
            SnackBarHandler.create(coordinatorLayout, getString(R.string.pinterest_signIn_fail))
                .show();
          }
        });
  }

  @Override
  public void onItemLongPress(View childView, int position) {
    // No need to be implemented
  }

  /** Create twitter login and session */
  public void signInTwitter() {
    Intent i = new Intent(AccountActivity.this, LoginActivity.class);
    startActivity(i);
  }

  @Override
  public void onResume() {
    super.onResume();
    ActivitySwitchHelper.setContext(this);
    setNavigationBarColor(ThemeHelper.getPrimaryColor(this));
    toolbar.setBackgroundColor(ThemeHelper.getPrimaryColor(this));
    boxAuthentication();
    if (Auth.getOAuth2Token() != null) {
      accountViewModel.saveDropboxToken(Auth.getOAuth2Token());
    }
    setStatusBarColor();
    setNavBarColor();
    accountViewModel.fetchAccountDetails();
    accountAdapter.updateTheme();
    accountAdapter.notifyDataSetChanged();
  }

  @Override
  public void onBackPressed() {
    Intent intent = new Intent(this, LFMainActivity.class);
    startActivity(intent);
    finish();
  }

  private void boxAuthentication() {
    if (sessionBox != null && sessionBox.getUser() != null) {
      accountViewModel.saveBoxToken(
          sessionBox.getUser().getName(), sessionBox.getAuthInfo().accessToken());
    }
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    client.onActivityResult(requestCode, resultCode, data);
    pdkClient.onOauthResponse(requestCode, resultCode, data);
    if ((requestCode == accountViewModel.OWNCLOUD_REQUEST_CODE
            && resultCode == accountViewModel.RESULT_OK)
        || (requestCode == accountViewModel.NEXTCLOUD_REQUEST_CODE
            && resultCode == accountViewModel.RESULT_OK)) {
      accountViewModel.saveOwnCloudOrNextCloudToken(
          requestCode,
          data.getStringExtra(getString(R.string.server_url)),
          data.getStringExtra(getString(R.string.auth_username)),
          data.getStringExtra(getString(R.string.auth_password)));
    }
  }
}
