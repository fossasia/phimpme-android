package org.fossasia.phimpme.utilities;

import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import org.fossasia.phimpme.gallery.util.ThemeHelper;

/** Created by pa1pal on 16/7/17. */
public class SnackBarHandler {
  public static final int INDEFINITE = Snackbar.LENGTH_INDEFINITE;
  public static final int LONG = Snackbar.LENGTH_LONG;
  public static final int SHORT = Snackbar.LENGTH_SHORT;

  public static Snackbar show(View view, String text, int duration) {
    final Snackbar snackbar = Snackbar.make(view, text, duration);
    ThemeHelper themeHelper = new ThemeHelper(ActivitySwitchHelper.getContext());
    View sbView = snackbar.getView();
    TextView textView = sbView.findViewById(android.support.design.R.id.snackbar_text);
    textView.setTextColor(Color.WHITE);
    textView.setTextSize(12);
    snackbar.setActionTextColor(themeHelper.getAccentColor());
    return snackbar;
  }

  public static Snackbar showWithBottomMargin(
      View view, String text, int bottomMargin, int duration) {
    ThemeHelper themeHelper = new ThemeHelper(ActivitySwitchHelper.getContext());
    final Snackbar snackbar = Snackbar.make(view, text, duration);
    View sbView = snackbar.getView();
    final FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) sbView.getLayoutParams();
    params.setMargins(
        params.leftMargin,
        params.topMargin,
        params.rightMargin,
        params.bottomMargin + bottomMargin);
    sbView.setLayoutParams(params);

    TextView textView = sbView.findViewById(android.support.design.R.id.snackbar_text);
    textView.setTextColor(Color.WHITE);
    textView.setTextSize(12);
    snackbar.setActionTextColor(themeHelper.getAccentColor());
    snackbar.show();
    return snackbar;
  }

  public static Snackbar showWithBottomMargin2(
      View view, String text, int bottomMargin, int duration) {
    ThemeHelper themeHelper = new ThemeHelper(ActivitySwitchHelper.getContext());
    final Snackbar snackbar = Snackbar.make(view, text, duration);
    View sbView = snackbar.getView();
    final FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) sbView.getLayoutParams();
    params.setMargins(
        params.leftMargin,
        params.topMargin,
        params.rightMargin,
        params.bottomMargin + bottomMargin);
    sbView.setLayoutParams(params);

    TextView textView = sbView.findViewById(android.support.design.R.id.snackbar_text);
    textView.setTextColor(Color.WHITE);
    textView.setTextSize(12);
    snackbar.setActionTextColor(themeHelper.getAccentColor());
    return snackbar;
  }

  public static void show(View view, int res, int duration) {
    show(view, ActivitySwitchHelper.getContext().getResources().getString(res), duration);
  }

  public static Snackbar show(View view, String text) {
    return show(view, text, Snackbar.LENGTH_LONG);
  }

  public static Snackbar show(View view, int res) {
    return show(view, ActivitySwitchHelper.getContext().getResources().getString(res));
  }

  public static void showWithBottomMargin(View view, int res, int duration, int bottomMargin) {
    showWithBottomMargin(
        view,
        ActivitySwitchHelper.getContext().getResources().getString(res),
        bottomMargin,
        duration);
  }

  public static Snackbar showWithBottomMargin(View view, String text, int bottomMargin) {
    return showWithBottomMargin(view, text, bottomMargin, Snackbar.LENGTH_LONG);
  }

  public static void showWithBottomMargin(View view, int res, int bottomMargin) {
    showWithBottomMargin(
        view, ActivitySwitchHelper.getContext().getResources().getString(res), bottomMargin);
  }
}
