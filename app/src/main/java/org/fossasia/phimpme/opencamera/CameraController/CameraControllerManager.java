package org.fossasia.phimpme.opencamera.CameraController;

/**
 * Provides additional support related to the Android camera APIs. This is to support functionality
 * that doesn't require a camera to have been opened.
 */
public abstract class CameraControllerManager {
  public abstract int getNumberOfCameras();

  public abstract boolean isFrontFacing(int cameraId);
}
