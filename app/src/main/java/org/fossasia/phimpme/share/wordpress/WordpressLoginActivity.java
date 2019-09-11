package org.fossasia.phimpme.share.wordpress;

import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.widget.Toolbar;

import butterknife.BindView;
import org.fossasia.phimpme.R;
import org.fossasia.phimpme.base.ThemedActivity;
import org.fossasia.phimpme.gallery.util.ThemeHelper;
import org.fossasia.phimpme.utilities.SnackBarHandler;

public class WordpressLoginActivity extends ThemedActivity {

  ThemeHelper themeHelper;

  @BindView(R.id.email)
  EditText user_name;

  @BindView(R.id.password)
  EditText user_password;

  Button login_button;
  View parent;
  Toolbar toolbar;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_wordpress_login);
    themeHelper = new ThemeHelper(this);
    init();
  }

  private void init() {
    toolbar = findViewById(R.id.toolbar);
    toolbar.setTitle(R.string.sharewordpress);
    toolbar.setBackgroundColor(themeHelper.getPrimaryColor());
    parent = findViewById(R.id.wordpress_login);
    setSupportActionBar(toolbar);
    login_button = findViewById(R.id.login_button);
    login_button.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            SnackBarHandler.create(parent, getString(R.string.feature_not_present)).show();
            // Snackbar.make(parent,"Feature not present",
            // BaseTransientBottomBar.LENGTH_LONG).show();
          }
        });
  }
}
