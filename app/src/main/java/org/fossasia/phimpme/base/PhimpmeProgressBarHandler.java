package org.fossasia.phimpme.base;


import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

/** Created by pa1pal on 12/6/17. */
public class PhimpmeProgressBarHandler {
  private ProgressBar mProgressBar;

  public PhimpmeProgressBarHandler(Context context) {
    ViewGroup layout =
        (ViewGroup) ((Activity) context).findViewById(android.R.id.content).getRootView();

    mProgressBar = new ProgressBar(context, null, android.R.attr.progressBarStyleLarge);
    mProgressBar.setIndeterminate(true);

    RelativeLayout.LayoutParams params =
        new RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);

    RelativeLayout rl = new RelativeLayout(context);

    rl.setGravity(Gravity.CENTER);
    rl.addView(mProgressBar);

    layout.addView(rl, params);

    hide();
  }

  public void show() {
    mProgressBar.setVisibility(View.VISIBLE);
  }

  public void hide() {
    mProgressBar.setVisibility(View.INVISIBLE);
  }
}
