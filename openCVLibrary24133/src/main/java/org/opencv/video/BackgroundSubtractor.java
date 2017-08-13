
//
// This file is auto-generated. Please don't modify it!
//
package org.opencv.video;

import org.opencv.core.Algorithm;
import org.opencv.core.Mat;

// C++: class BackgroundSubtractor
/**
 * <p>Base class for background/foreground segmentation.</p>
 *
 * <p>class BackgroundSubtractor : public Algorithm <code></p>
 *
 * <p>// C++ code:</p>
 *
 *
 * <p>public:</p>
 *
 * <p>virtual ~BackgroundSubtractor();</p>
 *
 * <p>virtual void operator()(InputArray image, OutputArray fgmask, double
 * learningRate=0);</p>
 *
 * <p>virtual void getBackgroundImage(OutputArray backgroundImage) const;</p>
 *
 * <p>};</p>
 *
 * <p>The class is only used to define the common interface for the whole family of
 * background/foreground segmentation algorithms.
 * </code></p>
 *
 * @see <a href="http://docs.opencv.org/modules/video/doc/motion_analysis_and_object_tracking.html#backgroundsubtractor">org.opencv.video.BackgroundSubtractor : public Algorithm</a>
 */
public class BackgroundSubtractor extends Algorithm {

    protected BackgroundSubtractor(long addr) { super(addr); }


    //
    // C++:  void BackgroundSubtractor::operator ()(Mat image, Mat& fgmask, double learningRate = 0)
    //

/**
 * <p>Computes a foreground mask.</p>
 *
 * @param image Next video frame.
 * @param fgmask The output foreground mask as an 8-bit binary image.
 * @param learningRate a learningRate
 *
 * @see <a href="http://docs.opencv.org/modules/video/doc/motion_analysis_and_object_tracking.html#backgroundsubtractor-operator">org.opencv.video.BackgroundSubtractor.operator()</a>
 */
    public  void apply(Mat image, Mat fgmask, double learningRate)
    {

        apply_0(nativeObj, image.nativeObj, fgmask.nativeObj, learningRate);

        return;
    }

/**
 * <p>Computes a foreground mask.</p>
 *
 * @param image Next video frame.
 * @param fgmask The output foreground mask as an 8-bit binary image.
 *
 * @see <a href="http://docs.opencv.org/modules/video/doc/motion_analysis_and_object_tracking.html#backgroundsubtractor-operator">org.opencv.video.BackgroundSubtractor.operator()</a>
 */
    public  void apply(Mat image, Mat fgmask)
    {

        apply_1(nativeObj, image.nativeObj, fgmask.nativeObj);

        return;
    }


    @Override
    protected void finalize() throws Throwable {
        delete(nativeObj);
    }



    // C++:  void BackgroundSubtractor::operator ()(Mat image, Mat& fgmask, double learningRate = 0)
    private static native void apply_0(long nativeObj, long image_nativeObj, long fgmask_nativeObj, double learningRate);
    private static native void apply_1(long nativeObj, long image_nativeObj, long fgmask_nativeObj);

    // native support for java finalize()
    private static native void delete(long nativeObj);

}
