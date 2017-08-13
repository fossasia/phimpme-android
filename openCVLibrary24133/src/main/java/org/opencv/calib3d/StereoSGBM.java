
//
// This file is auto-generated. Please don't modify it!
//
package org.opencv.calib3d;

import org.opencv.core.Mat;

// C++: class StereoSGBM
/**
 * <p>Class for computing stereo correspondence using the semi-global block
 * matching algorithm.</p>
 *
 * <p>class StereoSGBM <code></p>
 *
 * <p>// C++ code:</p>
 *
 *
 * <p>StereoSGBM();</p>
 *
 * <p>StereoSGBM(int minDisparity, int numDisparities, int SADWindowSize,</p>
 *
 * <p>int P1=0, int P2=0, int disp12MaxDiff=0,</p>
 *
 * <p>int preFilterCap=0, int uniquenessRatio=0,</p>
 *
 * <p>int speckleWindowSize=0, int speckleRange=0,</p>
 *
 * <p>bool fullDP=false);</p>
 *
 * <p>virtual ~StereoSGBM();</p>
 *
 * <p>virtual void operator()(InputArray left, InputArray right, OutputArray disp);</p>
 *
 * <p>int minDisparity;</p>
 *
 * <p>int numberOfDisparities;</p>
 *
 * <p>int SADWindowSize;</p>
 *
 * <p>int preFilterCap;</p>
 *
 * <p>int uniquenessRatio;</p>
 *
 * <p>int P1, P2;</p>
 *
 * <p>int speckleWindowSize;</p>
 *
 * <p>int speckleRange;</p>
 *
 * <p>int disp12MaxDiff;</p>
 *
 * <p>bool fullDP;...</p>
 *
 * <p>};</p>
 *
 * <p>The class implements the modified H. Hirschmuller algorithm [HH08] that
 * differs from the original one as follows: </code></p>
 * <ul>
 *   <li> By default, the algorithm is single-pass, which means that you
 * consider only 5 directions instead of 8. Set <code>fullDP=true</code> to run
 * the full variant of the algorithm but beware that it may consume a lot of
 * memory.
 *   <li> The algorithm matches blocks, not individual pixels. Though, setting
 * <code>SADWindowSize=1</code> reduces the blocks to single pixels.
 *   <li> Mutual information cost function is not implemented. Instead, a
 * simpler Birchfield-Tomasi sub-pixel metric from [BT98] is used. Though, the
 * color images are supported as well.
 *   <li> Some pre- and post- processing steps from K. Konolige algorithm
 * :ocv:funcx:"StereoBM.operator()" are included, for example: pre-filtering
 * (<code>CV_STEREO_BM_XSOBEL</code> type) and post-filtering (uniqueness check,
 * quadratic interpolation and speckle filtering).
 * </ul>
 *
 * <p>Note:</p>
 * <ul>
 *   <li> (Python) An example illustrating the use of the StereoSGBM matching
 * algorithm can be found at opencv_source_code/samples/python2/stereo_match.py
 * </ul>
 *
 * @see <a href="http://docs.opencv.org/modules/calib3d/doc/camera_calibration_and_3d_reconstruction.html#stereosgbm">org.opencv.calib3d.StereoSGBM</a>
 */
public class StereoSGBM {

    protected final long nativeObj;
    protected StereoSGBM(long addr) { nativeObj = addr; }


    public static final int
            DISP_SHIFT = 4,
            DISP_SCALE = (1<<DISP_SHIFT);


    //
    // C++:   StereoSGBM::StereoSGBM()
    //

/**
 * <p>Initializes <code>StereoSGBM</code> and sets parameters to custom values.??</p>
 *
 * <p>The first constructor initializes <code>StereoSGBM</code> with all the
 * default parameters. So, you only have to set <code>StereoSGBM.numberOfDisparities</code>
 * at minimum. The second constructor enables you to set each parameter to a
 * custom value.</p>
 *
 * @see <a href="http://docs.opencv.org/modules/calib3d/doc/camera_calibration_and_3d_reconstruction.html#stereosgbm-stereosgbm">org.opencv.calib3d.StereoSGBM.StereoSGBM</a>
 */
    public   StereoSGBM()
    {

        nativeObj = StereoSGBM_0();

        return;
    }


    //
    // C++:   StereoSGBM::StereoSGBM(int minDisparity, int numDisparities, int SADWindowSize, int P1 = 0, int P2 = 0, int disp12MaxDiff = 0, int preFilterCap = 0, int uniquenessRatio = 0, int speckleWindowSize = 0, int speckleRange = 0, bool fullDP = false)
    //

/**
 * <p>Initializes <code>StereoSGBM</code> and sets parameters to custom values.??</p>
 *
 * <p>The first constructor initializes <code>StereoSGBM</code> with all the
 * default parameters. So, you only have to set <code>StereoSGBM.numberOfDisparities</code>
 * at minimum. The second constructor enables you to set each parameter to a
 * custom value.</p>
 *
 * @param minDisparity Minimum possible disparity value. Normally, it is zero
 * but sometimes rectification algorithms can shift images, so this parameter
 * needs to be adjusted accordingly.
 * @param numDisparities Maximum disparity minus minimum disparity. The value is
 * always greater than zero. In the current implementation, this parameter must
 * be divisible by 16.
 * @param SADWindowSize Matched block size. It must be an odd number
 * <code>>=1</code>. Normally, it should be somewhere in the <code>3..11</code>
 * range.
 * @param P1 The first parameter controlling the disparity smoothness. See
 * below.
 * @param P2 The second parameter controlling the disparity smoothness. The
 * larger the values are, the smoother the disparity is. <code>P1</code> is the
 * penalty on the disparity change by plus or minus 1 between neighbor pixels.
 * <code>P2</code> is the penalty on the disparity change by more than 1 between
 * neighbor pixels. The algorithm requires <code>P2 > P1</code>. See
 * <code>stereo_match.cpp</code> sample where some reasonably good
 * <code>P1</code> and <code>P2</code> values are shown (like <code>8*number_of_image_channels*SADWindowSize*SADWindowSize</code>
 * and <code>32*number_of_image_channels*SADWindowSize*SADWindowSize</code>,
 * respectively).
 * @param disp12MaxDiff Maximum allowed difference (in integer pixel units) in
 * the left-right disparity check. Set it to a non-positive value to disable the
 * check.
 * @param preFilterCap Truncation value for the prefiltered image pixels. The
 * algorithm first computes x-derivative at each pixel and clips its value by
 * <code>[-preFilterCap, preFilterCap]</code> interval. The result values are
 * passed to the Birchfield-Tomasi pixel cost function.
 * @param uniquenessRatio Margin in percentage by which the best (minimum)
 * computed cost function value should "win" the second best value to consider
 * the found match correct. Normally, a value within the 5-15 range is good
 * enough.
 * @param speckleWindowSize Maximum size of smooth disparity regions to consider
 * their noise speckles and invalidate. Set it to 0 to disable speckle
 * filtering. Otherwise, set it somewhere in the 50-200 range.
 * @param speckleRange Maximum disparity variation within each connected
 * component. If you do speckle filtering, set the parameter to a positive
 * value, it will be implicitly multiplied by 16. Normally, 1 or 2 is good
 * enough.
 * @param fullDP Set it to <code>true</code> to run the full-scale two-pass
 * dynamic programming algorithm. It will consume O(W*H*numDisparities) bytes,
 * which is large for 640x480 stereo and huge for HD-size pictures. By default,
 * it is set to <code>false</code>.
 *
 * @see <a href="http://docs.opencv.org/modules/calib3d/doc/camera_calibration_and_3d_reconstruction.html#stereosgbm-stereosgbm">org.opencv.calib3d.StereoSGBM.StereoSGBM</a>
 */
    public   StereoSGBM(int minDisparity, int numDisparities, int SADWindowSize, int P1, int P2, int disp12MaxDiff, int preFilterCap, int uniquenessRatio, int speckleWindowSize, int speckleRange, boolean fullDP)
    {

        nativeObj = StereoSGBM_1(minDisparity, numDisparities, SADWindowSize, P1, P2, disp12MaxDiff, preFilterCap, uniquenessRatio, speckleWindowSize, speckleRange, fullDP);

        return;
    }

/**
 * <p>Initializes <code>StereoSGBM</code> and sets parameters to custom values.??</p>
 *
 * <p>The first constructor initializes <code>StereoSGBM</code> with all the
 * default parameters. So, you only have to set <code>StereoSGBM.numberOfDisparities</code>
 * at minimum. The second constructor enables you to set each parameter to a
 * custom value.</p>
 *
 * @param minDisparity Minimum possible disparity value. Normally, it is zero
 * but sometimes rectification algorithms can shift images, so this parameter
 * needs to be adjusted accordingly.
 * @param numDisparities Maximum disparity minus minimum disparity. The value is
 * always greater than zero. In the current implementation, this parameter must
 * be divisible by 16.
 * @param SADWindowSize Matched block size. It must be an odd number
 * <code>>=1</code>. Normally, it should be somewhere in the <code>3..11</code>
 * range.
 *
 * @see <a href="http://docs.opencv.org/modules/calib3d/doc/camera_calibration_and_3d_reconstruction.html#stereosgbm-stereosgbm">org.opencv.calib3d.StereoSGBM.StereoSGBM</a>
 */
    public   StereoSGBM(int minDisparity, int numDisparities, int SADWindowSize)
    {

        nativeObj = StereoSGBM_2(minDisparity, numDisparities, SADWindowSize);

        return;
    }


    //
    // C++:  void StereoSGBM::operator ()(Mat left, Mat right, Mat& disp)
    //

    public  void compute(Mat left, Mat right, Mat disp)
    {

        compute_0(nativeObj, left.nativeObj, right.nativeObj, disp.nativeObj);

        return;
    }


    //
    // C++: int StereoSGBM::minDisparity
    //

    public  int get_minDisparity()
    {

        int retVal = get_minDisparity_0(nativeObj);

        return retVal;
    }


    //
    // C++: void StereoSGBM::minDisparity
    //

    public  void set_minDisparity(int minDisparity)
    {

        set_minDisparity_0(nativeObj, minDisparity);

        return;
    }


    //
    // C++: int StereoSGBM::numberOfDisparities
    //

    public  int get_numberOfDisparities()
    {

        int retVal = get_numberOfDisparities_0(nativeObj);

        return retVal;
    }


    //
    // C++: void StereoSGBM::numberOfDisparities
    //

    public  void set_numberOfDisparities(int numberOfDisparities)
    {

        set_numberOfDisparities_0(nativeObj, numberOfDisparities);

        return;
    }


    //
    // C++: int StereoSGBM::SADWindowSize
    //

    public  int get_SADWindowSize()
    {

        int retVal = get_SADWindowSize_0(nativeObj);

        return retVal;
    }


    //
    // C++: void StereoSGBM::SADWindowSize
    //

    public  void set_SADWindowSize(int SADWindowSize)
    {

        set_SADWindowSize_0(nativeObj, SADWindowSize);

        return;
    }


    //
    // C++: int StereoSGBM::preFilterCap
    //

    public  int get_preFilterCap()
    {

        int retVal = get_preFilterCap_0(nativeObj);

        return retVal;
    }


    //
    // C++: void StereoSGBM::preFilterCap
    //

    public  void set_preFilterCap(int preFilterCap)
    {

        set_preFilterCap_0(nativeObj, preFilterCap);

        return;
    }


    //
    // C++: int StereoSGBM::uniquenessRatio
    //

    public  int get_uniquenessRatio()
    {

        int retVal = get_uniquenessRatio_0(nativeObj);

        return retVal;
    }


    //
    // C++: void StereoSGBM::uniquenessRatio
    //

    public  void set_uniquenessRatio(int uniquenessRatio)
    {

        set_uniquenessRatio_0(nativeObj, uniquenessRatio);

        return;
    }


    //
    // C++: int StereoSGBM::P1
    //

    public  int get_P1()
    {

        int retVal = get_P1_0(nativeObj);

        return retVal;
    }


    //
    // C++: void StereoSGBM::P1
    //

    public  void set_P1(int P1)
    {

        set_P1_0(nativeObj, P1);

        return;
    }


    //
    // C++: int StereoSGBM::P2
    //

    public  int get_P2()
    {

        int retVal = get_P2_0(nativeObj);

        return retVal;
    }


    //
    // C++: void StereoSGBM::P2
    //

    public  void set_P2(int P2)
    {

        set_P2_0(nativeObj, P2);

        return;
    }


    //
    // C++: int StereoSGBM::speckleWindowSize
    //

    public  int get_speckleWindowSize()
    {

        int retVal = get_speckleWindowSize_0(nativeObj);

        return retVal;
    }


    //
    // C++: void StereoSGBM::speckleWindowSize
    //

    public  void set_speckleWindowSize(int speckleWindowSize)
    {

        set_speckleWindowSize_0(nativeObj, speckleWindowSize);

        return;
    }


    //
    // C++: int StereoSGBM::speckleRange
    //

    public  int get_speckleRange()
    {

        int retVal = get_speckleRange_0(nativeObj);

        return retVal;
    }


    //
    // C++: void StereoSGBM::speckleRange
    //

    public  void set_speckleRange(int speckleRange)
    {

        set_speckleRange_0(nativeObj, speckleRange);

        return;
    }


    //
    // C++: int StereoSGBM::disp12MaxDiff
    //

    public  int get_disp12MaxDiff()
    {

        int retVal = get_disp12MaxDiff_0(nativeObj);

        return retVal;
    }


    //
    // C++: void StereoSGBM::disp12MaxDiff
    //

    public  void set_disp12MaxDiff(int disp12MaxDiff)
    {

        set_disp12MaxDiff_0(nativeObj, disp12MaxDiff);

        return;
    }


    //
    // C++: bool StereoSGBM::fullDP
    //

    public  boolean get_fullDP()
    {

        boolean retVal = get_fullDP_0(nativeObj);

        return retVal;
    }


    //
    // C++: void StereoSGBM::fullDP
    //

    public  void set_fullDP(boolean fullDP)
    {

        set_fullDP_0(nativeObj, fullDP);

        return;
    }


    @Override
    protected void finalize() throws Throwable {
        delete(nativeObj);
    }



    // C++:   StereoSGBM::StereoSGBM()
    private static native long StereoSGBM_0();

    // C++:   StereoSGBM::StereoSGBM(int minDisparity, int numDisparities, int SADWindowSize, int P1 = 0, int P2 = 0, int disp12MaxDiff = 0, int preFilterCap = 0, int uniquenessRatio = 0, int speckleWindowSize = 0, int speckleRange = 0, bool fullDP = false)
    private static native long StereoSGBM_1(int minDisparity, int numDisparities, int SADWindowSize, int P1, int P2, int disp12MaxDiff, int preFilterCap, int uniquenessRatio, int speckleWindowSize, int speckleRange, boolean fullDP);
    private static native long StereoSGBM_2(int minDisparity, int numDisparities, int SADWindowSize);

    // C++:  void StereoSGBM::operator ()(Mat left, Mat right, Mat& disp)
    private static native void compute_0(long nativeObj, long left_nativeObj, long right_nativeObj, long disp_nativeObj);

    // C++: int StereoSGBM::minDisparity
    private static native int get_minDisparity_0(long nativeObj);

    // C++: void StereoSGBM::minDisparity
    private static native void set_minDisparity_0(long nativeObj, int minDisparity);

    // C++: int StereoSGBM::numberOfDisparities
    private static native int get_numberOfDisparities_0(long nativeObj);

    // C++: void StereoSGBM::numberOfDisparities
    private static native void set_numberOfDisparities_0(long nativeObj, int numberOfDisparities);

    // C++: int StereoSGBM::SADWindowSize
    private static native int get_SADWindowSize_0(long nativeObj);

    // C++: void StereoSGBM::SADWindowSize
    private static native void set_SADWindowSize_0(long nativeObj, int SADWindowSize);

    // C++: int StereoSGBM::preFilterCap
    private static native int get_preFilterCap_0(long nativeObj);

    // C++: void StereoSGBM::preFilterCap
    private static native void set_preFilterCap_0(long nativeObj, int preFilterCap);

    // C++: int StereoSGBM::uniquenessRatio
    private static native int get_uniquenessRatio_0(long nativeObj);

    // C++: void StereoSGBM::uniquenessRatio
    private static native void set_uniquenessRatio_0(long nativeObj, int uniquenessRatio);

    // C++: int StereoSGBM::P1
    private static native int get_P1_0(long nativeObj);

    // C++: void StereoSGBM::P1
    private static native void set_P1_0(long nativeObj, int P1);

    // C++: int StereoSGBM::P2
    private static native int get_P2_0(long nativeObj);

    // C++: void StereoSGBM::P2
    private static native void set_P2_0(long nativeObj, int P2);

    // C++: int StereoSGBM::speckleWindowSize
    private static native int get_speckleWindowSize_0(long nativeObj);

    // C++: void StereoSGBM::speckleWindowSize
    private static native void set_speckleWindowSize_0(long nativeObj, int speckleWindowSize);

    // C++: int StereoSGBM::speckleRange
    private static native int get_speckleRange_0(long nativeObj);

    // C++: void StereoSGBM::speckleRange
    private static native void set_speckleRange_0(long nativeObj, int speckleRange);

    // C++: int StereoSGBM::disp12MaxDiff
    private static native int get_disp12MaxDiff_0(long nativeObj);

    // C++: void StereoSGBM::disp12MaxDiff
    private static native void set_disp12MaxDiff_0(long nativeObj, int disp12MaxDiff);

    // C++: bool StereoSGBM::fullDP
    private static native boolean get_fullDP_0(long nativeObj);

    // C++: void StereoSGBM::fullDP
    private static native void set_fullDP_0(long nativeObj, boolean fullDP);

    // native support for java finalize()
    private static native void delete(long nativeObj);

}
