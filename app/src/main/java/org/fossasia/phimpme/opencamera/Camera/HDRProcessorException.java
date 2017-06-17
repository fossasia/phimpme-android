package org.fossasia.phimpme.opencamera.Camera;

/** Exception for HDRProcessor class.
 */
public class HDRProcessorException extends Exception {
	final static public int INVALID_N_IMAGES = 0; // the supplied number of images is not supported
	final static public int UNEQUAL_SIZES = 1; // images not of the same resolution

	final private int code;

	HDRProcessorException(int code) {
		this.code = code;
	}

	public int getCode() {
		return code;
	}
}
