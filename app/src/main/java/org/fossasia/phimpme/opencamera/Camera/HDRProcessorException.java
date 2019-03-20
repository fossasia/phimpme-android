package org.fossasia.phimpme.opencamera.Camera;

/** Exception for HDRProcessor class.
 */
public class HDRProcessorException extends Exception {

	final private int code;

	HDRProcessorException(int code) {
		this.code = code;
	}

	public int getCode() {
		return code;
	}
}
