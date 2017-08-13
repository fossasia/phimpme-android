
//
// This file is auto-generated. Please don't modify it!
//
package org.opencv.video;



// C++: class BackgroundSubtractorMOG
/**
 * <p>Gaussian Mixture-based Background/Foreground Segmentation Algorithm.</p>
 *
 * <p>The class implements the algorithm described in P. KadewTraKuPong and R.
 * Bowden, *An improved adaptive background mixture model for real-time tracking
 * with shadow detection*, Proc. 2nd European Workshop on Advanced Video-Based
 * Surveillance Systems, 2001: http://personal.ee.surrey.ac.uk/Personal/R.Bowden/publications/avbs01/avbs01.pdf</p>
 *
 * @see <a href="http://docs.opencv.org/modules/video/doc/motion_analysis_and_object_tracking.html#backgroundsubtractormog">org.opencv.video.BackgroundSubtractorMOG : public BackgroundSubtractor</a>
 */
public class BackgroundSubtractorMOG extends BackgroundSubtractor {

    protected BackgroundSubtractorMOG(long addr) { super(addr); }


    //
    // C++:   BackgroundSubtractorMOG::BackgroundSubtractorMOG()
    //

/**
 * <p>The constructors.</p>
 *
 * <p>Default constructor sets all parameters to default values.</p>
 *
 * @see <a href="http://docs.opencv.org/modules/video/doc/motion_analysis_and_object_tracking.html#backgroundsubtractormog-backgroundsubtractormog">org.opencv.video.BackgroundSubtractorMOG.BackgroundSubtractorMOG</a>
 */
    public   BackgroundSubtractorMOG()
    {

        super( BackgroundSubtractorMOG_0() );

        return;
    }


    //
    // C++:   BackgroundSubtractorMOG::BackgroundSubtractorMOG(int history, int nmixtures, double backgroundRatio, double noiseSigma = 0)
    //

/**
 * <p>The constructors.</p>
 *
 * <p>Default constructor sets all parameters to default values.</p>
 *
 * @param history Length of the history.
 * @param nmixtures Number of Gaussian mixtures.
 * @param backgroundRatio Background ratio.
 * @param noiseSigma Noise strength.
 *
 * @see <a href="http://docs.opencv.org/modules/video/doc/motion_analysis_and_object_tracking.html#backgroundsubtractormog-backgroundsubtractormog">org.opencv.video.BackgroundSubtractorMOG.BackgroundSubtractorMOG</a>
 */
    public   BackgroundSubtractorMOG(int history, int nmixtures, double backgroundRatio, double noiseSigma)
    {

        super( BackgroundSubtractorMOG_1(history, nmixtures, backgroundRatio, noiseSigma) );

        return;
    }

/**
 * <p>The constructors.</p>
 *
 * <p>Default constructor sets all parameters to default values.</p>
 *
 * @param history Length of the history.
 * @param nmixtures Number of Gaussian mixtures.
 * @param backgroundRatio Background ratio.
 *
 * @see <a href="http://docs.opencv.org/modules/video/doc/motion_analysis_and_object_tracking.html#backgroundsubtractormog-backgroundsubtractormog">org.opencv.video.BackgroundSubtractorMOG.BackgroundSubtractorMOG</a>
 */
    public   BackgroundSubtractorMOG(int history, int nmixtures, double backgroundRatio)
    {

        super( BackgroundSubtractorMOG_2(history, nmixtures, backgroundRatio) );

        return;
    }


    @Override
    protected void finalize() throws Throwable {
        delete(nativeObj);
    }



    // C++:   BackgroundSubtractorMOG::BackgroundSubtractorMOG()
    private static native long BackgroundSubtractorMOG_0();

    // C++:   BackgroundSubtractorMOG::BackgroundSubtractorMOG(int history, int nmixtures, double backgroundRatio, double noiseSigma = 0)
    private static native long BackgroundSubtractorMOG_1(int history, int nmixtures, double backgroundRatio, double noiseSigma);
    private static native long BackgroundSubtractorMOG_2(int history, int nmixtures, double backgroundRatio);

    // native support for java finalize()
    private static native void delete(long nativeObj);

}
