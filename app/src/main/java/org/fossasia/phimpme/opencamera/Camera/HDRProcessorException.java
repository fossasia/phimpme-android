package org.fossasia.phimpme.opencamera.Camera;

/** Exception for HDRProcessor class. */
public class HDRProcessorException extends Exception {
  public static final int INVALID_N_IMAGES = 0; // the supplied number of images is not supported
  public static final int UNEQUAL_SIZES = 1; // images not of the same resolution

  private final int code;

  HDRProcessorException(int code) {
    this.code = code;
  }

  public int getCode() {
    return code;
  }
}
