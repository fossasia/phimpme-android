package org.fossasia.phimpme.opencamera.Camera;

/** Exception for HDRProcessor class. */
public class HDRProcessorException extends Exception {

  private final int code;

  HDRProcessorException(int code) {
    this.code = code;
  }

  public int getCode() {
    return code;
  }
}
