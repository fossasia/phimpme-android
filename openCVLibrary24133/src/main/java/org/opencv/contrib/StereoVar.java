
//
// This file is auto-generated. Please don't modify it!
//
package org.opencv.contrib;

import org.opencv.core.Mat;

// C++: class StereoVar
/**
 * <p>Class for computing stereo correspondence using the variational matching
 * algorithm</p>
 *
 * <p>class StereoVar <code></p>
 *
 * <p>// C++ code:</p>
 *
 *
 * <p>StereoVar();</p>
 *
 * <p>StereoVar(int levels, double pyrScale,</p>
 *
 * <p>int nIt, int minDisp, int maxDisp,</p>
 *
 * <p>int poly_n, double poly_sigma, float fi,</p>
 *
 * <p>float lambda, int penalization, int cycle,</p>
 *
 * <p>int flags);</p>
 *
 * <p>virtual ~StereoVar();</p>
 *
 * <p>virtual void operator()(InputArray left, InputArray right, OutputArray disp);</p>
 *
 * <p>int levels;</p>
 *
 * <p>double pyrScale;</p>
 *
 * <p>int nIt;</p>
 *
 * <p>int minDisp;</p>
 *
 * <p>int maxDisp;</p>
 *
 * <p>int poly_n;</p>
 *
 * <p>double poly_sigma;</p>
 *
 * <p>float fi;</p>
 *
 * <p>float lambda;</p>
 *
 * <p>int penalization;</p>
 *
 * <p>int cycle;</p>
 *
 * <p>int flags;...</p>
 *
 * <p>};</p>
 *
 * <p>The class implements the modified S. G. Kosov algorithm [KTS09] that differs
 * from the original one as follows: </code></p>
 * <ul>
 *   <li> The automatic initialization of method's parameters is added.
 *   <li> The method of Smart Iteration Distribution (SID) is implemented.
 *   <li> The support of Multi-Level Adaptation Technique (MLAT) is not
 * included.
 *   <li> The method of dynamic adaptation of method's parameters is not
 * included.
 * </ul>
 *
 * @see <a href="http://docs.opencv.org/modules/contrib/doc/stereo.html#stereovar">org.opencv.contrib.StereoVar</a>
 */
public class StereoVar {

    protected final long nativeObj;
    protected StereoVar(long addr) { nativeObj = addr; }


    public static final int
            USE_INITIAL_DISPARITY = 1,
            USE_EQUALIZE_HIST = 2,
            USE_SMART_ID = 4,
            USE_AUTO_PARAMS = 8,
            USE_MEDIAN_FILTERING = 16,
            CYCLE_O = 0,
            CYCLE_V = 1,
            PENALIZATION_TICHONOV = 0,
            PENALIZATION_CHARBONNIER = 1,
            PENALIZATION_PERONA_MALIK = 2;


    //
    // C++:   StereoVar::StereoVar()
    //

/**
 * <p>The constructor</p>
 *
 * <p>The first constructor initializes <code>StereoVar</code> with all the default
 * parameters. So, you only have to set <code>StereoVar.maxDisp</code> and / or
 * <code>StereoVar.minDisp</code> at minimum. The second constructor enables
 * you to set each parameter to a custom value.</p>
 *
 * @see <a href="http://docs.opencv.org/modules/contrib/doc/stereo.html#stereovar-stereovar">org.opencv.contrib.StereoVar.StereoVar</a>
 */
    public   StereoVar()
    {

        nativeObj = StereoVar_0();

        return;
    }


    //
    // C++:   StereoVar::StereoVar(int levels, double pyrScale, int nIt, int minDisp, int maxDisp, int poly_n, double poly_sigma, float fi, float lambda, int penalization, int cycle, int flags)
    //

/**
 * <p>The constructor</p>
 *
 * <p>The first constructor initializes <code>StereoVar</code> with all the default
 * parameters. So, you only have to set <code>StereoVar.maxDisp</code> and / or
 * <code>StereoVar.minDisp</code> at minimum. The second constructor enables
 * you to set each parameter to a custom value.</p>
 *
 * @param levels The number of pyramid layers, including the initial image.
 * levels=1 means that no extra layers are created and only the original images
 * are used. This parameter is ignored if flag USE_AUTO_PARAMS is set.
 * @param pyrScale Specifies the image scale (<1) to build the pyramids for each
 * image. pyrScale=0.5 means the classical pyramid, where each next layer is
 * twice smaller than the previous. (This parameter is ignored if flag
 * USE_AUTO_PARAMS is set).
 * @param nIt The number of iterations the algorithm does at each pyramid level.
 * (If the flag USE_SMART_ID is set, the number of iterations will be
 * redistributed in such a way, that more iterations will be done on more
 * coarser levels.)
 * @param minDisp Minimum possible disparity value. Could be negative in case
 * the left and right input images change places.
 * @param maxDisp Maximum possible disparity value.
 * @param poly_n Size of the pixel neighbourhood used to find polynomial
 * expansion in each pixel. The larger values mean that the image will be
 * approximated with smoother surfaces, yielding more robust algorithm and more
 * blurred motion field. Typically, poly_n = 3, 5 or 7
 * @param poly_sigma Standard deviation of the Gaussian that is used to smooth
 * derivatives that are used as a basis for the polynomial expansion. For
 * poly_n=5 you can set poly_sigma=1.1, for poly_n=7 a good value would be
 * poly_sigma=1.5
 * @param fi The smoothness parameter, ot the weight coefficient for the
 * smoothness term.
 * @param lambda The threshold parameter for edge-preserving smoothness. (This
 * parameter is ignored if PENALIZATION_CHARBONNIER or PENALIZATION_PERONA_MALIK
 * is used.)
 * @param penalization Possible values: PENALIZATION_TICHONOV - linear
 * smoothness; PENALIZATION_CHARBONNIER - non-linear edge preserving smoothness;
 * PENALIZATION_PERONA_MALIK - non-linear edge-enhancing smoothness. (This
 * parameter is ignored if flag USE_AUTO_PARAMS is set).
 * @param cycle Type of the multigrid cycle. Possible values: CYCLE_O and
 * CYCLE_V for null- and v-cycles respectively. (This parameter is ignored if
 * flag USE_AUTO_PARAMS is set).
 * @param flags The operation flags; can be a combination of the following:
 * <ul>
 *   <li> USE_INITIAL_DISPARITY: Use the input flow as the initial flow
 * approximation.
 *   <li> USE_EQUALIZE_HIST: Use the histogram equalization in the
 * pre-processing phase.
 *   <li> USE_SMART_ID: Use the smart iteration distribution (SID).
 *   <li> USE_AUTO_PARAMS: Allow the method to initialize the main parameters.
 *   <li> USE_MEDIAN_FILTERING: Use the median filer of the solution in the post
 * processing phase.
 * </ul>
 *
 * @see <a href="http://docs.opencv.org/modules/contrib/doc/stereo.html#stereovar-stereovar">org.opencv.contrib.StereoVar.StereoVar</a>
 */
    public   StereoVar(int levels, double pyrScale, int nIt, int minDisp, int maxDisp, int poly_n, double poly_sigma, float fi, float lambda, int penalization, int cycle, int flags)
    {

        nativeObj = StereoVar_1(levels, pyrScale, nIt, minDisp, maxDisp, poly_n, poly_sigma, fi, lambda, penalization, cycle, flags);

        return;
    }


    //
    // C++:  void StereoVar::operator ()(Mat left, Mat right, Mat& disp)
    //

    public  void compute(Mat left, Mat right, Mat disp)
    {

        compute_0(nativeObj, left.nativeObj, right.nativeObj, disp.nativeObj);

        return;
    }


    //
    // C++: int StereoVar::levels
    //

    public  int get_levels()
    {

        int retVal = get_levels_0(nativeObj);

        return retVal;
    }


    //
    // C++: void StereoVar::levels
    //

    public  void set_levels(int levels)
    {

        set_levels_0(nativeObj, levels);

        return;
    }


    //
    // C++: double StereoVar::pyrScale
    //

    public  double get_pyrScale()
    {

        double retVal = get_pyrScale_0(nativeObj);

        return retVal;
    }


    //
    // C++: void StereoVar::pyrScale
    //

    public  void set_pyrScale(double pyrScale)
    {

        set_pyrScale_0(nativeObj, pyrScale);

        return;
    }


    //
    // C++: int StereoVar::nIt
    //

    public  int get_nIt()
    {

        int retVal = get_nIt_0(nativeObj);

        return retVal;
    }


    //
    // C++: void StereoVar::nIt
    //

    public  void set_nIt(int nIt)
    {

        set_nIt_0(nativeObj, nIt);

        return;
    }


    //
    // C++: int StereoVar::minDisp
    //

    public  int get_minDisp()
    {

        int retVal = get_minDisp_0(nativeObj);

        return retVal;
    }


    //
    // C++: void StereoVar::minDisp
    //

    public  void set_minDisp(int minDisp)
    {

        set_minDisp_0(nativeObj, minDisp);

        return;
    }


    //
    // C++: int StereoVar::maxDisp
    //

    public  int get_maxDisp()
    {

        int retVal = get_maxDisp_0(nativeObj);

        return retVal;
    }


    //
    // C++: void StereoVar::maxDisp
    //

    public  void set_maxDisp(int maxDisp)
    {

        set_maxDisp_0(nativeObj, maxDisp);

        return;
    }


    //
    // C++: int StereoVar::poly_n
    //

    public  int get_poly_n()
    {

        int retVal = get_poly_n_0(nativeObj);

        return retVal;
    }


    //
    // C++: void StereoVar::poly_n
    //

    public  void set_poly_n(int poly_n)
    {

        set_poly_n_0(nativeObj, poly_n);

        return;
    }


    //
    // C++: double StereoVar::poly_sigma
    //

    public  double get_poly_sigma()
    {

        double retVal = get_poly_sigma_0(nativeObj);

        return retVal;
    }


    //
    // C++: void StereoVar::poly_sigma
    //

    public  void set_poly_sigma(double poly_sigma)
    {

        set_poly_sigma_0(nativeObj, poly_sigma);

        return;
    }


    //
    // C++: float StereoVar::fi
    //

    public  float get_fi()
    {

        float retVal = get_fi_0(nativeObj);

        return retVal;
    }


    //
    // C++: void StereoVar::fi
    //

    public  void set_fi(float fi)
    {

        set_fi_0(nativeObj, fi);

        return;
    }


    //
    // C++: float StereoVar::lambda
    //

    public  float get_lambda()
    {

        float retVal = get_lambda_0(nativeObj);

        return retVal;
    }


    //
    // C++: void StereoVar::lambda
    //

    public  void set_lambda(float lambda)
    {

        set_lambda_0(nativeObj, lambda);

        return;
    }


    //
    // C++: int StereoVar::penalization
    //

    public  int get_penalization()
    {

        int retVal = get_penalization_0(nativeObj);

        return retVal;
    }


    //
    // C++: void StereoVar::penalization
    //

    public  void set_penalization(int penalization)
    {

        set_penalization_0(nativeObj, penalization);

        return;
    }


    //
    // C++: int StereoVar::cycle
    //

    public  int get_cycle()
    {

        int retVal = get_cycle_0(nativeObj);

        return retVal;
    }


    //
    // C++: void StereoVar::cycle
    //

    public  void set_cycle(int cycle)
    {

        set_cycle_0(nativeObj, cycle);

        return;
    }


    //
    // C++: int StereoVar::flags
    //

    public  int get_flags()
    {

        int retVal = get_flags_0(nativeObj);

        return retVal;
    }


    //
    // C++: void StereoVar::flags
    //

    public  void set_flags(int flags)
    {

        set_flags_0(nativeObj, flags);

        return;
    }


    @Override
    protected void finalize() throws Throwable {
        delete(nativeObj);
    }



    // C++:   StereoVar::StereoVar()
    private static native long StereoVar_0();

    // C++:   StereoVar::StereoVar(int levels, double pyrScale, int nIt, int minDisp, int maxDisp, int poly_n, double poly_sigma, float fi, float lambda, int penalization, int cycle, int flags)
    private static native long StereoVar_1(int levels, double pyrScale, int nIt, int minDisp, int maxDisp, int poly_n, double poly_sigma, float fi, float lambda, int penalization, int cycle, int flags);

    // C++:  void StereoVar::operator ()(Mat left, Mat right, Mat& disp)
    private static native void compute_0(long nativeObj, long left_nativeObj, long right_nativeObj, long disp_nativeObj);

    // C++: int StereoVar::levels
    private static native int get_levels_0(long nativeObj);

    // C++: void StereoVar::levels
    private static native void set_levels_0(long nativeObj, int levels);

    // C++: double StereoVar::pyrScale
    private static native double get_pyrScale_0(long nativeObj);

    // C++: void StereoVar::pyrScale
    private static native void set_pyrScale_0(long nativeObj, double pyrScale);

    // C++: int StereoVar::nIt
    private static native int get_nIt_0(long nativeObj);

    // C++: void StereoVar::nIt
    private static native void set_nIt_0(long nativeObj, int nIt);

    // C++: int StereoVar::minDisp
    private static native int get_minDisp_0(long nativeObj);

    // C++: void StereoVar::minDisp
    private static native void set_minDisp_0(long nativeObj, int minDisp);

    // C++: int StereoVar::maxDisp
    private static native int get_maxDisp_0(long nativeObj);

    // C++: void StereoVar::maxDisp
    private static native void set_maxDisp_0(long nativeObj, int maxDisp);

    // C++: int StereoVar::poly_n
    private static native int get_poly_n_0(long nativeObj);

    // C++: void StereoVar::poly_n
    private static native void set_poly_n_0(long nativeObj, int poly_n);

    // C++: double StereoVar::poly_sigma
    private static native double get_poly_sigma_0(long nativeObj);

    // C++: void StereoVar::poly_sigma
    private static native void set_poly_sigma_0(long nativeObj, double poly_sigma);

    // C++: float StereoVar::fi
    private static native float get_fi_0(long nativeObj);

    // C++: void StereoVar::fi
    private static native void set_fi_0(long nativeObj, float fi);

    // C++: float StereoVar::lambda
    private static native float get_lambda_0(long nativeObj);

    // C++: void StereoVar::lambda
    private static native void set_lambda_0(long nativeObj, float lambda);

    // C++: int StereoVar::penalization
    private static native int get_penalization_0(long nativeObj);

    // C++: void StereoVar::penalization
    private static native void set_penalization_0(long nativeObj, int penalization);

    // C++: int StereoVar::cycle
    private static native int get_cycle_0(long nativeObj);

    // C++: void StereoVar::cycle
    private static native void set_cycle_0(long nativeObj, int cycle);

    // C++: int StereoVar::flags
    private static native int get_flags_0(long nativeObj);

    // C++: void StereoVar::flags
    private static native void set_flags_0(long nativeObj, int flags);

    // native support for java finalize()
    private static native void delete(long nativeObj);

}
