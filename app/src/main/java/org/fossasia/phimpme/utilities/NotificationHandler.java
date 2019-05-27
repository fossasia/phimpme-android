package org.fossasia.phimpme.utilities;

import android.app.NotificationManager;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.Builder;
import org.fossasia.phimpme.R;

/** Created by anant on 23/7/17. */
public class NotificationHandler {

  private static NotificationManager mNotifyManager;
  private static Builder mBuilder;
  private static int id = 1;

  public static void make(@StringRes int title, @StringRes int action, @DrawableRes int iconid) {
    mNotifyManager =
        (NotificationManager)
            ActivitySwitchHelper.getContext().getSystemService(Context.NOTIFICATION_SERVICE);
    mBuilder = new NotificationCompat.Builder(ActivitySwitchHelper.getContext());
    mBuilder
        .setContentTitle(
            ActivitySwitchHelper.getContext().getString(action)
                + " "
                + ActivitySwitchHelper.getContext().getResources().getString(title))
        .setLargeIcon(
            BitmapFactory.decodeResource(
                ActivitySwitchHelper.getContext().getResources(), R.mipmap.ic_launcher))
        .setContentText(ActivitySwitchHelper.getContext().getString(R.string.progress))
        .setSmallIcon(iconid)
        .setOngoing(true);
    mBuilder.setProgress(0, 0, true);
    // Issues the notification
    mNotifyManager.notify(id, mBuilder.build());
  }

  public static void actionProgress(int uploaded, int total, int percent, @StringRes int action) {
    mBuilder.setProgress(total, uploaded, false);
    mBuilder.setContentTitle(
        ActivitySwitchHelper.getContext().getString(action)
            + " ("
            + Integer.toString(percent)
            + "%)");
    // Issues the notification
    mNotifyManager.notify(id, mBuilder.build());
  }

  public static void actionPassed(@StringRes int actionsuccess) {
    mBuilder
        .setContentText(ActivitySwitchHelper.getContext().getString(R.string.upload_done))
        // Removes the progress bar
        .setProgress(0, 0, false)
        .setContentTitle(ActivitySwitchHelper.getContext().getString(actionsuccess))
        .setOngoing(false);
    mNotifyManager.notify(0, mBuilder.build());
    mNotifyManager.cancel(id);
  }

  public static void actionFailed() {
    mBuilder
        .setContentText(ActivitySwitchHelper.getContext().getString(R.string.try_again))
        // Removes the progress bar
        .setProgress(0, 0, false)
        .setContentTitle(ActivitySwitchHelper.getContext().getString(R.string.upload_failed))
        .setOngoing(false);
    mNotifyManager.notify(0, mBuilder.build());
    mNotifyManager.cancel(id);
  }
}
