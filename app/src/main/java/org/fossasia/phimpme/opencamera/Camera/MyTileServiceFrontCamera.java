package org.fossasia.phimpme.opencamera.Camera;

import android.content.Intent;
import android.os.Build;
import android.service.quicksettings.TileService;
import android.support.annotation.RequiresApi;
import android.util.Log;

/** Provides service for quick settings tile. */
@RequiresApi(api = Build.VERSION_CODES.N)
public class MyTileServiceFrontCamera extends TileService {
  private static final String TAG = "MyTileServiceFrontCam";
  public static final String TILE_ID = "vn.mbm.phimp.opencamera.TILE_FRONT_CAMERA";

  @Override
  public void onDestroy() {
    super.onDestroy();
  }

  @Override
  public void onTileAdded() {
    super.onTileAdded();
  }

  @Override
  public void onTileRemoved() {
    super.onTileRemoved();
  }

  @Override
  public void onStartListening() {
    super.onStartListening();
  }

  @Override
  public void onStopListening() {
    super.onStopListening();
  }

  @Override
  public void onClick() {
    if (MyDebug.LOG) Log.d(TAG, "onClick");
    super.onClick();
    Intent intent = new Intent(this, CameraActivity.class);
    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
    intent.setAction(TILE_ID);
    /* This collapse the navigation drawer/shade, so that user is focused on the functionality */
    startActivityAndCollapse(intent);
  }
}
