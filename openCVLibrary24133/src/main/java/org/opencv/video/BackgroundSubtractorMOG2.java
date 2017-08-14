
//
// This file is auto-generated. Please don't modify it!
//
package org.opencv.video;



// C++: class BackgroundSubtractorMOG2
/**
 * <p>Gaussian Mixture-based Background/Foreground Segmentation Algorithm.</p>
 *
 * <p>Here are important members of the class that control the algorithm, which you
 * can set after constructing the class instance:</p>
 *
 * <p>Maximum allowed number of mixture components. Actual number is determined
 * dynamically per pixel.</p>
 *
 * <p>Threshold defining whether the component is significant enough to be included
 * into the background model (corresponds to <code>TB=1-cf</code> from the
 * paper??which paper??). <code>cf=0.1 => TB=0.9</code> is default. For
 * <code>alpha=0.001</code>, it means that the mode should exist for
 * approximately 105 frames before it is considered foreground.
 * <code></p>
 *
 * <p>// C++ code:</p>
 *
 * <p>Threshold for the squared Mahalanobis distance that helps decide when a
 * sample is close to the existing components (corresponds to <code>Tg</code>).
 * If it is not close to any component, a new component is generated. <code>3
 * sigma => Tg=3*3=9</code> is default. A smaller <code>Tg</code> value
 * generates more components. A higher <code>Tg</code> value may result in a
 * small number of components but they can grow too large.</p>
 *
 * <p>Initial variance for the newly generated components. It affects the speed of
 * adaptation. The parameter value is based on your estimate of the typical
 * standard deviation from the images. OpenCV uses 15 as a reasonable value.</p>
 *
 * <p>Parameter used to further control the variance.</p>
 *
 * <p>Parameter used to further control the variance.</p>
 *
 * <p>Complexity reduction parameter. This parameter defines the number of samples
 * needed to accept to prove the component exists. <code>CT=0.05</code> is a
 * default value for all the samples. By setting <code>CT=0</code> you get an
 * algorithm very similar to the standard Stauffer&Grimson algorithm.</p>
 *
 * <p>The value for marking shadow pixels in the output foreground mask. Default
 * value is 127.</p>
 *
 * <p>Shadow threshold. The shadow is detected if the pixel is a darker version of
 * the background. <code>Tau</code> is a threshold defining how much darker the
 * shadow can be. <code>Tau= 0.5</code> means that if a pixel is more than twice
 * darker then it is not shadow. See Prati,Mikic,Trivedi,Cucchiarra, *Detecting
 * Moving Shadows...*, IEEE PAMI,2003.</p>
 *
 * <p>The class implements the Gaussian mixture model background subtraction
 * described in: </code></p>
 * <ul>
 *   <li> Z.Zivkovic, *Improved adaptive Gausian mixture model for background
 * subtraction*, International Conference Pattern Recognition, UK, August, 2004,
 * http://www.zoranz.net/Publications/zivkovic2004ICPR.pdf. The code is very
 * fast and performs also shadow detection. Number of Gausssian components is
 * adapted per pixel.
 *   <li> Z.Zivkovic, F. van der Heijden, *Efficient Adaptive Density Estimapion
 * per Image Pixel for the Task of Background Subtraction*, Pattern Recognition
 * Letters, vol. 27, no. 7, pages 773-780, 2006. The algorithm similar to the
 * standard Stauffer&Grimson algorithm with additional selection of the number
 * of the Gaussian components based on: Z.Zivkovic, F.van der Heijden, Recursive
 * unsupervised learning of finite mixture models, IEEE Trans. on Pattern
 * Analysis and Machine Intelligence, vol.26, no.5, pages 651-656, 2004.
 * </ul>
 *
 * @see <a href="http://docs.opencv.org/modules/video/doc/motion_analysis_and_object_tracking.html#backgroundsubtractormog2">org.opencv.video.BackgroundSubtractorMOG2 : public BackgroundSubtractor</a>
 */
public class BackgroundSubtractorMOG2 extends BackgroundSubtractor {

    protected BackgroundSubtractorMOG2(long addr) { super(addr); }


    //
    // C++:   BackgroundSubtractorMOG2::BackgroundSubtractorMOG2()
    //

/**
 * <p>The constructors.</p>
 *
 * @see <a href="http://docs.opencv.org/modules/video/doc/motion_analysis_and_object_tracking.html#backgroundsubtractormog2-backgroundsubtractormog2">org.opencv.video.BackgroundSubtractorMOG2.BackgroundSubtractorMOG2</a>
 */
    public   BackgroundSubtractorMOG2()
    {

        super( BackgroundSubtractorMOG2_0() );

        return;
    }


    //
    // C++:   BackgroundSubtractorMOG2::BackgroundSubtractorMOG2(int history, float varThreshold, bool bShadowDetection = true)
    //

/**
 * <p>The constructors.</p>
 *
 * @param history Length of the history.
 * @param varThreshold Threshold on the squared Mahalanobis distance to decide
 * whether it is well described by the background model (see Cthr??). This
 * parameter does not affect the background update. A typical value could be 4
 * sigma, that is, <code>varThreshold=4*4=16;</code> (see Tb??).
 * @param bShadowDetection Parameter defining whether shadow detection should be
 * enabled (<code>true</code> or <code>false</code>).
 *
 * @see <a href="http://docs.opencv.org/modules/video/doc/motion_analysis_and_object_tracking.html#backgroundsubtractormog2-backgroundsubtractormog2">org.opencv.video.BackgroundSubtractorMOG2.BackgroundSubtractorMOG2</a>
 */
    public   BackgroundSubtractorMOG2(int history, float varThreshold, boolean bShadowDetection)
    {

        super( BackgroundSubtractorMOG2_1(history, varThreshold, bShadowDetection) );

        return;
    }

/**
 * <p>The constructors.</p>
 *
 * @param history Length of the history.
 * @param varThreshold Threshold on the squared Mahalanobis distance to decide
 * whether it is well described by the background model (see Cthr??). This
 * parameter does not affect the background update. A typical value could be 4
 * sigma, that is, <code>varThreshold=4*4=16;</code> (see Tb??).
 *
 * @see <a href="http://docs.opencv.org/modules/video/doc/motion_analysis_and_object_tracking.html#backgroundsubtractormog2-backgroundsubtractormog2">org.opencv.video.BackgroundSubtractorMOG2.BackgroundSubtractorMOG2</a>
 */
    public   BackgroundSubtractorMOG2(int history, float varThreshold)
    {

        super( BackgroundSubtractorMOG2_2(history, varThreshold) );

        return;
    }


    @Override
    protected void finalize() throws Throwable {
        delete(nativeObj);
    }



    // C++:   BackgroundSubtractorMOG2::BackgroundSubtractorMOG2()
    private static native long BackgroundSubtractorMOG2_0();

    // C++:   BackgroundSubtractorMOG2::BackgroundSubtractorMOG2(int history, float varThreshold, bool bShadowDetection = true)
    private static native long BackgroundSubtractorMOG2_1(int history, float varThreshold, boolean bShadowDetection);
    private static native long BackgroundSubtractorMOG2_2(int history, float varThreshold);

    // native support for java finalize()
    private static native void delete(long nativeObj);

}
