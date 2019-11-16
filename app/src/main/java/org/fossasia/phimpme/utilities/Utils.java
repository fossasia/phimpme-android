package org.fossasia.phimpme.utilities;

import static android.content.Context.CLIPBOARD_SERVICE;
import static org.fossasia.phimpme.utilities.Constants.PACKAGE_INSTAGRAM;
import static org.fossasia.phimpme.utilities.Constants.PACKAGE_MESSENGER;
import static org.fossasia.phimpme.utilities.Constants.PACKAGE_SNAPCHAT;
import static org.fossasia.phimpme.utilities.Constants.PACKAGE_WHATSAPP;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.speech.RecognizerIntent;
import android.util.Base64;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.NonNull;
import com.google.android.material.snackbar.Snackbar;
import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Locale;
import org.fossasia.phimpme.R;
import org.fossasia.phimpme.data.local.AccountDatabase;

/** Created by pa1pal on 23/5/17. */
public class Utils {
  public static Bitmap getBitmapFromPath(String path) {

    BitmapFactory.Options bmOptions = new BitmapFactory.Options();
    return BitmapFactory.decodeFile(path, bmOptions);
  }

  public static void copyToClipBoard(Context context, String msg) {
    ClipboardManager myClipboard = (ClipboardManager) context.getSystemService(CLIPBOARD_SERVICE);
    ClipData myClip = ClipData.newPlainText("text", msg);
    myClipboard.setPrimaryClip(myClip);
  }

  public static String getStringImage(Bitmap bmp) {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
    byte[] imageBytes = baos.toByteArray();
    return Base64.encodeToString(imageBytes, Base64.DEFAULT);
  }

  public static void shareMsgOnIntent(Context context, String msg) {
    Intent sendIntent = new Intent();
    sendIntent.setAction(Intent.ACTION_SEND);
    sendIntent.putExtra(Intent.EXTRA_TEXT, msg);
    sendIntent.setType("text/plain");
    context.startActivity(sendIntent);
  }

  public static boolean isAppInstalled(String packageName, PackageManager pm) {
    boolean installed;
    try {
      pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
      installed = true;
    } catch (PackageManager.NameNotFoundException e) {
      installed = false;
    }
    return installed;
  }

  public static boolean isInternetOn(Context context) {
    try {
      ConnectivityManager connectivityManager =
          (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
      if (connectivityManager != null) {
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
      }
    } catch (Exception ex) {
      ex.printStackTrace();
    }
    return false;
  }

  /**
   * This function check if the selected account is already existed.
   *
   * @param s Name of the account from accountList e.g. Twitter
   * @return true is existed, false otherwise
   */
  public static boolean checkAlreadyExist(AccountDatabase.AccountName s) {
    Realm realm = Realm.getDefaultInstance();
    // Query in the realm database
    RealmQuery<AccountDatabase> query = realm.where(AccountDatabase.class);

    // Checking if string equals to is exist or not
    query.equalTo("name", s.toString());
    RealmResults<AccountDatabase> result1 = query.findAll();

    // Here checking if count of that values is greater than zero
    return (result1.size() > 0);
  }

  public static ArrayList<AccountDatabase.AccountName> getLoggedInAccountsList() {
    ArrayList<AccountDatabase.AccountName> list = new ArrayList<>();
    for (AccountDatabase.AccountName account : AccountDatabase.AccountName.values()) {
      if (checkAlreadyExist(account)) list.add(account);
    }
    return list;
  }

  public static ArrayList<AccountDatabase.AccountName> getSharableAccountsList() {
    ArrayList<AccountDatabase.AccountName> list = new ArrayList<>();
    PackageManager packageManager = (ActivitySwitchHelper.context).getPackageManager();
    if (isAppInstalled(PACKAGE_INSTAGRAM, packageManager))
      list.add(AccountDatabase.AccountName.INSTAGRAM);

    if (isAppInstalled(PACKAGE_WHATSAPP, packageManager))
      list.add(AccountDatabase.AccountName.WHATSAPP);

    /*if (isAppInstalled(PACKAGE_GOOGLEPLUS,packageManager))
    list.add(AccountDatabase.AccountName.GOOGLEPLUS);*/

    if (isAppInstalled(PACKAGE_MESSENGER, packageManager))
      list.add(AccountDatabase.AccountName.MESSENGER);

    if (isAppInstalled(PACKAGE_SNAPCHAT, packageManager))
      list.add(AccountDatabase.AccountName.SNAPCHAT);

    list.addAll(getLoggedInAccountsList());
    if (!list.contains(AccountDatabase.AccountName.IMGUR))
      list.add(AccountDatabase.AccountName.IMGUR);

    list.add(AccountDatabase.AccountName.OTHERS);
    return list;
  }

  public static boolean checkNetwork(Context context, @NonNull View view) {
    if (isInternetOn(context)) {
      return true;
    } else {
      SnackBarHandler.create(view, "You are not connected to the internet", Snackbar.LENGTH_SHORT)
          .show();
    }

    return false;
  }

  public static void promptSpeechInput(
      Activity activity, int requestCode, View parentView, String promtMsg) {
    Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
    intent.putExtra(
        RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
    intent.putExtra(RecognizerIntent.EXTRA_PROMPT, promtMsg);
    try {
      activity.startActivityForResult(intent, requestCode);
    } catch (ActivityNotFoundException a) {
      SnackBarHandler.create(parentView, activity.getString(R.string.speech_not_supported)).show();
    }
  }

  public static void showToastShort(Context context, String message) {
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
  }
}
