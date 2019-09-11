package org.fossasia.phimpme.share.drupal;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.widget.Toolbar;

import com.google.android.material.snackbar.Snackbar;

import butterknife.BindView;
import org.fossasia.phimpme.R;
import org.fossasia.phimpme.base.ThemedActivity;
import org.fossasia.phimpme.gallery.util.ThemeHelper;
import org.fossasia.phimpme.utilities.SnackBarHandler;

public class DrupalLogin extends ThemedActivity implements View.OnClickListener {

  ThemeHelper themeHelper;

  @BindView(R.id.login_title)
  EditText user_name;

  @BindView(R.id.password)
  EditText user_password;

  Button login_button;
  View parent;
  Toolbar toolbar;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_drupal_login);
    themeHelper = new ThemeHelper(this);
    init();
  }

  private void init() {
    toolbar = findViewById(R.id.toolbar);
    toolbar.setTitle(R.string.sharedrupal);
    toolbar.setBackgroundColor(themeHelper.getPrimaryColor());
    setSupportActionBar(toolbar);
    parent = findViewById(R.id.drupal_login);
    login_button = findViewById(R.id.login_button);
    login_button.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            loginAuth();
          }
        });
  }

  private void loginAuth() {
    SnackBarHandler.create(parent, getString(R.string.drupal_link), Snackbar.LENGTH_LONG).show();
  }

  @Override
  public void onClick(View v) {}
}
