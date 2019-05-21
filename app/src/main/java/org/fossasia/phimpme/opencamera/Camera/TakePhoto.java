package org.fossasia.phimpme.opencamera.Camera;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import org.fossasia.phimpme.utilities.ActivitySwitchHelper;

/**
 * Entry Activity for the "take photo" widget (see MyWidgetProviderTakePhoto). This redirects to
 * CameraActivity, but uses an intent extra/bundle to pass the "take photo" request.
 */
public class TakePhoto extends Activity {
  public static final String TAKE_PHOTO = "vn.mbm.phimp.opencamera.TAKE_PHOTO";
  private static final String TAG = "TakePhoto";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    if (MyDebug.LOG) Log.d(TAG, "onCreate");
    super.onCreate(savedInstanceState);

    Intent intent = new Intent(this, CameraActivity.class);
    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
    intent.putExtra(TAKE_PHOTO, true);
    this.startActivity(intent);
    if (MyDebug.LOG) Log.d(TAG, "finish");
    this.finish();
  }

  protected void onResume() {
    ActivitySwitchHelper.setContext(this);
    if (MyDebug.LOG) Log.d(TAG, "onResume");
    super.onResume();
  }
}
