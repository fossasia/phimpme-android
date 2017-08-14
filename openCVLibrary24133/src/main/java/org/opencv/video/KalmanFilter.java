
//
// This file is auto-generated. Please don't modify it!
//
package org.opencv.video;

import org.opencv.core.Mat;

// C++: class KalmanFilter
/**
 * <p>Kalman filter class.</p>
 *
 * <p>The class implements a standard Kalman filter http://en.wikipedia.org/wiki/Kalman_filter,
 * [Welch95]. However, you can modify <code>transitionMatrix</code>,
 * <code>controlMatrix</code>, and <code>measurementMatrix</code> to get an
 * extended Kalman filter functionality. See the OpenCV sample <code>kalman.cpp</code>.</p>
 *
 * <p>Note:</p>
 * <ul>
 *   <li> An example using the standard Kalman filter can be found at
 * opencv_source_code/samples/cpp/kalman.cpp
 * </ul>
 *
 * @see <a href="http://docs.opencv.org/modules/video/doc/motion_analysis_and_object_tracking.html#kalmanfilter">org.opencv.video.KalmanFilter</a>
 */
public class KalmanFilter {

    protected final long nativeObj;
    protected KalmanFilter(long addr) { nativeObj = addr; }


    //
    // C++:   KalmanFilter::KalmanFilter()
    //

/**
 * <p>The constructors.</p>
 *
 * <p>The full constructor.</p>
 *
 * <p>Note: In C API when <code>CvKalman* kalmanFilter</code> structure is not
 * needed anymore, it should be released with <code>cvReleaseKalman(&kalmanFilter)</code></p>
 *
 * @see <a href="http://docs.opencv.org/modules/video/doc/motion_analysis_and_object_tracking.html#kalmanfilter-kalmanfilter">org.opencv.video.KalmanFilter.KalmanFilter</a>
 */
    public   KalmanFilter()
    {

        nativeObj = KalmanFilter_0();

        return;
    }


    //
    // C++:   KalmanFilter::KalmanFilter(int dynamParams, int measureParams, int controlParams = 0, int type = CV_32F)
    //

/**
 * <p>The constructors.</p>
 *
 * <p>The full constructor.</p>
 *
 * <p>Note: In C API when <code>CvKalman* kalmanFilter</code> structure is not
 * needed anymore, it should be released with <code>cvReleaseKalman(&kalmanFilter)</code></p>
 *
 * @param dynamParams Dimensionality of the state.
 * @param measureParams Dimensionality of the measurement.
 * @param controlParams Dimensionality of the control vector.
 * @param type Type of the created matrices that should be <code>CV_32F</code>
 * or <code>CV_64F</code>.
 *
 * @see <a href="http://docs.opencv.org/modules/video/doc/motion_analysis_and_object_tracking.html#kalmanfilter-kalmanfilter">org.opencv.video.KalmanFilter.KalmanFilter</a>
 */
    public   KalmanFilter(int dynamParams, int measureParams, int controlParams, int type)
    {

        nativeObj = KalmanFilter_1(dynamParams, measureParams, controlParams, type);

        return;
    }

/**
 * <p>The constructors.</p>
 *
 * <p>The full constructor.</p>
 *
 * <p>Note: In C API when <code>CvKalman* kalmanFilter</code> structure is not
 * needed anymore, it should be released with <code>cvReleaseKalman(&kalmanFilter)</code></p>
 *
 * @param dynamParams Dimensionality of the state.
 * @param measureParams Dimensionality of the measurement.
 *
 * @see <a href="http://docs.opencv.org/modules/video/doc/motion_analysis_and_object_tracking.html#kalmanfilter-kalmanfilter">org.opencv.video.KalmanFilter.KalmanFilter</a>
 */
    public   KalmanFilter(int dynamParams, int measureParams)
    {

        nativeObj = KalmanFilter_2(dynamParams, measureParams);

        return;
    }


    //
    // C++:  Mat KalmanFilter::correct(Mat measurement)
    //

/**
 * <p>Updates the predicted state from the measurement.</p>
 *
 * @param measurement The measured system parameters
 *
 * @see <a href="http://docs.opencv.org/modules/video/doc/motion_analysis_and_object_tracking.html#kalmanfilter-correct">org.opencv.video.KalmanFilter.correct</a>
 */
    public  Mat correct(Mat measurement)
    {

        Mat retVal = new Mat(correct_0(nativeObj, measurement.nativeObj));

        return retVal;
    }


    //
    // C++:  Mat KalmanFilter::predict(Mat control = Mat())
    //

/**
 * <p>Computes a predicted state.</p>
 *
 * @param control The optional input control
 *
 * @see <a href="http://docs.opencv.org/modules/video/doc/motion_analysis_and_object_tracking.html#kalmanfilter-predict">org.opencv.video.KalmanFilter.predict</a>
 */
    public  Mat predict(Mat control)
    {

        Mat retVal = new Mat(predict_0(nativeObj, control.nativeObj));

        return retVal;
    }

/**
 * <p>Computes a predicted state.</p>
 *
 * @see <a href="http://docs.opencv.org/modules/video/doc/motion_analysis_and_object_tracking.html#kalmanfilter-predict">org.opencv.video.KalmanFilter.predict</a>
 */
    public  Mat predict()
    {

        Mat retVal = new Mat(predict_1(nativeObj));

        return retVal;
    }


    @Override
    protected void finalize() throws Throwable {
        delete(nativeObj);
    }



    // C++:   KalmanFilter::KalmanFilter()
    private static native long KalmanFilter_0();

    // C++:   KalmanFilter::KalmanFilter(int dynamParams, int measureParams, int controlParams = 0, int type = CV_32F)
    private static native long KalmanFilter_1(int dynamParams, int measureParams, int controlParams, int type);
    private static native long KalmanFilter_2(int dynamParams, int measureParams);

    // C++:  Mat KalmanFilter::correct(Mat measurement)
    private static native long correct_0(long nativeObj, long measurement_nativeObj);

    // C++:  Mat KalmanFilter::predict(Mat control = Mat())
    private static native long predict_0(long nativeObj, long control_nativeObj);
    private static native long predict_1(long nativeObj);

    // native support for java finalize()
    private static native void delete(long nativeObj);

}
