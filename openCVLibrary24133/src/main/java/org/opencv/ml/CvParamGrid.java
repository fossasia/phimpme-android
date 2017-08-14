
//
// This file is auto-generated. Please don't modify it!
//
package org.opencv.ml;



// C++: class CvParamGrid
/**
 * <p>The structure represents the logarithmic grid range of statmodel parameters.
 * It is used for optimizing statmodel accuracy by varying model parameters, the
 * accuracy estimate being computed by cross-validation.</p>
 *
 * <p>Minimum value of the statmodel parameter.</p>
 *
 * <p>Maximum value of the statmodel parameter.
 * <code></p>
 *
 * <p>// C++ code:</p>
 *
 * <p>Logarithmic step for iterating the statmodel parameter.</p>
 *
 * <p>The grid determines the following iteration sequence of the statmodel
 * parameter values: </code></p>
 *
 * <p><em>(min_val, min_val*step, min_val*(step)^2, dots, min_val*(step)^n),</em></p>
 *
 * <p>where <em>n</em> is the maximal index satisfying</p>
 *
 * <p><em>min_val * step ^n &lt max_val</em></p>
 *
 * <p>The grid is logarithmic, so <code>step</code> must always be greater then 1.</p>
 *
 * @see <a href="http://docs.opencv.org/modules/ml/doc/support_vector_machines.html#cvparamgrid">org.opencv.ml.CvParamGrid</a>
 */
public class CvParamGrid {

    protected final long nativeObj;
    protected CvParamGrid(long addr) { nativeObj = addr; }


    public static final int
            SVM_C = 0,
            SVM_GAMMA = 1,
            SVM_P = 2,
            SVM_NU = 3,
            SVM_COEF = 4,
            SVM_DEGREE = 5;


    //
    // C++:   CvParamGrid::CvParamGrid()
    //

/**
 * <p>The constructors.</p>
 *
 * <p>The full constructor initializes corresponding members. The default
 * constructor creates a dummy grid:</p>
 *
 * <p><code></p>
 *
 * <p>// C++ code:</p>
 *
 * <p>CvParamGrid.CvParamGrid()</p>
 *
 *
 * <p>min_val = max_val = step = 0;</p>
 *
 *
 * @see <a href="http://docs.opencv.org/modules/ml/doc/support_vector_machines.html#cvparamgrid-cvparamgrid">org.opencv.ml.CvParamGrid.CvParamGrid</a>
 */
    public   CvParamGrid()
    {

        nativeObj = CvParamGrid_0();

        return;
    }


    //
    // C++: double CvParamGrid::min_val
    //

    public  double get_min_val()
    {

        double retVal = get_min_val_0(nativeObj);

        return retVal;
    }


    //
    // C++: void CvParamGrid::min_val
    //

    public  void set_min_val(double min_val)
    {

        set_min_val_0(nativeObj, min_val);

        return;
    }


    //
    // C++: double CvParamGrid::max_val
    //

    public  double get_max_val()
    {

        double retVal = get_max_val_0(nativeObj);

        return retVal;
    }


    //
    // C++: void CvParamGrid::max_val
    //

    public  void set_max_val(double max_val)
    {

        set_max_val_0(nativeObj, max_val);

        return;
    }


    //
    // C++: double CvParamGrid::step
    //

    public  double get_step()
    {

        double retVal = get_step_0(nativeObj);

        return retVal;
    }


    //
    // C++: void CvParamGrid::step
    //

    public  void set_step(double step)
    {

        set_step_0(nativeObj, step);

        return;
    }


    @Override
    protected void finalize() throws Throwable {
        delete(nativeObj);
    }



    // C++:   CvParamGrid::CvParamGrid()
    private static native long CvParamGrid_0();

    // C++: double CvParamGrid::min_val
    private static native double get_min_val_0(long nativeObj);

    // C++: void CvParamGrid::min_val
    private static native void set_min_val_0(long nativeObj, double min_val);

    // C++: double CvParamGrid::max_val
    private static native double get_max_val_0(long nativeObj);

    // C++: void CvParamGrid::max_val
    private static native void set_max_val_0(long nativeObj, double max_val);

    // C++: double CvParamGrid::step
    private static native double get_step_0(long nativeObj);

    // C++: void CvParamGrid::step
    private static native void set_step_0(long nativeObj, double step);

    // native support for java finalize()
    private static native void delete(long nativeObj);

}
