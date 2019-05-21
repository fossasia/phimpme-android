package org.fossasia.phimpme.share.drupal;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import butterknife.BindView;
import org.fossasia.phimpme.R;
import org.fossasia.phimpme.base.ThemedActivity;
import org.fossasia.phimpme.gallery.util.ThemeHelper;

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
    Snackbar.make(parent, R.string.drupal_link, Snackbar.LENGTH_LONG).show();
  }

  @Override
  public void onClick(View v) {}
}
