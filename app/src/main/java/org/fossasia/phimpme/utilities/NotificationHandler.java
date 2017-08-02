package org.fossasia.phimpme.utilities;

import android.app.NotificationManager;
import android.content.Context;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.Builder;

import org.fossasia.phimpme.R;

/**
 * Created by anant on 23/7/17.
 */

public class NotificationHandler {

    private static NotificationManager mNotifyManager;
    private static Builder mBuilder;
    private static int id = 1;

    public static void make(){
        mNotifyManager = (NotificationManager) ActivitySwitchHelper.getContext().getSystemService(Context.NOTIFICATION_SERVICE);
        mBuilder = new NotificationCompat.Builder(ActivitySwitchHelper.getContext());
        mBuilder.setContentTitle(ActivitySwitchHelper.getContext().getString(R.string.upload_progress))
                .setContentText(ActivitySwitchHelper.getContext().getString(R.string.progress))
                .setSmallIcon(R.drawable.ic_cloud_upload_black_24dp)
                .setOngoing(true);
        mBuilder.setProgress(0, 0, true);
        // Issues the notification
        mNotifyManager.notify(id, mBuilder.build());
    }

    public static void updateProgress(int uploaded, int total, int percent){
        mBuilder.setProgress(total, uploaded, false);
        mBuilder.setContentTitle(ActivitySwitchHelper.getContext().getString(R.string.upload_progress)+" ("+Integer.toString(percent)+"%)");
        // Issues the notification
        mNotifyManager.notify(id, mBuilder.build());
    }

    public static void uploadPassed(){
        mBuilder.setContentText(ActivitySwitchHelper.getContext().getString(R.string.upload_done))
                // Removes the progress bar
                .setProgress(0,0,false)
                .setContentTitle(ActivitySwitchHelper.getContext().getString(R.string.upload_complete))
                .setOngoing(false);
        mNotifyManager.notify(0, mBuilder.build());
        mNotifyManager.cancel(id);
    }

    public static void uploadFailed(){
        mBuilder.setContentText(ActivitySwitchHelper.getContext().getString(R.string.try_again))
                // Removes the progress bar
                .setProgress(0,0,false)
                .setContentTitle(ActivitySwitchHelper.getContext().getString(R.string.upload_failed))
                .setOngoing(false);
        mNotifyManager.notify(0, mBuilder.build());
        mNotifyManager.cancel(id);
    }

}