
//
// This file is auto-generated. Please don't modify it!
//
package org.opencv.ml;



// C++: class CvBoostParams
/**
 * <p>Boosting training parameters.</p>
 *
 * <p>There is one structure member that you can set directly:</p>
 *
 * <p>Splitting criteria used to choose optimal splits during a weak tree
 * construction. Possible values are:</p>
 *
 * <ul>
 *   <li> CvBoost.DEFAULT Use the default for the particular boosting method,
 * see below.
 * </ul>
 * <p><code></p>
 *
 * <p>// C++ code:</p>
 * <ul>
 *   <li> CvBoost.GINI Use Gini index. This is default option for Real
 * AdaBoost; may be also used for Discrete AdaBoost.
 *   <li> CvBoost.MISCLASS Use misclassification rate. This is default option
 * for Discrete AdaBoost; may be also used for Real AdaBoost.
 *   <li> CvBoost.SQERR Use least squares criteria. This is default and the
 * only option for LogitBoost and Gentle AdaBoost.
 * </ul>
 *
 * <p>The structure is derived from "CvDTreeParams" but not all of the decision
 * tree parameters are supported. In particular, cross-validation is not
 * supported.
 * </code></p>
 *
 * <p>All parameters are public. You can initialize them by a constructor and then
 * override some of them directly if you want.</p>
 *
 * @see <a href="http://docs.opencv.org/modules/ml/doc/boosting.html#cvboostparams">org.opencv.ml.CvBoostParams : public CvDTreeParams</a>
 */
public class CvBoostParams extends CvDTreeParams {

    protected CvBoostParams(long addr) { super(addr); }


    //
    // C++:   CvBoostParams::CvBoostParams()
    //

/**
 * <p>The constructors.</p>
 *
 * <p>See "CvDTreeParams.CvDTreeParams" for description of other parameters.</p>
 *
 * <p>Default parameters are:</p>
 *
 * <p><code></p>
 *
 * <p>// C++ code:</p>
 *
 * <p>CvBoostParams.CvBoostParams()</p>
 *
 *
 * <p>boost_type = CvBoost.REAL;</p>
 *
 * <p>weak_count = 100;</p>
 *
 * <p>weight_trim_rate = 0.95;</p>
 *
 * <p>cv_folds = 0;</p>
 *
 * <p>max_depth = 1;</p>
 *
 *
 * @see <a href="http://docs.opencv.org/modules/ml/doc/boosting.html#cvboostparams-cvboostparams">org.opencv.ml.CvBoostParams.CvBoostParams</a>
 */
    public   CvBoostParams()
    {

        super( CvBoostParams_0() );

        return;
    }


    //
    // C++: int CvBoostParams::boost_type
    //

    public  int get_boost_type()
    {

        int retVal = get_boost_type_0(nativeObj);

        return retVal;
    }


    //
    // C++: void CvBoostParams::boost_type
    //

    public  void set_boost_type(int boost_type)
    {

        set_boost_type_0(nativeObj, boost_type);

        return;
    }


    //
    // C++: int CvBoostParams::weak_count
    //

    public  int get_weak_count()
    {

        int retVal = get_weak_count_0(nativeObj);

        return retVal;
    }


    //
    // C++: void CvBoostParams::weak_count
    //

    public  void set_weak_count(int weak_count)
    {

        set_weak_count_0(nativeObj, weak_count);

        return;
    }


    //
    // C++: int CvBoostParams::split_criteria
    //

    public  int get_split_criteria()
    {

        int retVal = get_split_criteria_0(nativeObj);

        return retVal;
    }


    //
    // C++: void CvBoostParams::split_criteria
    //

    public  void set_split_criteria(int split_criteria)
    {

        set_split_criteria_0(nativeObj, split_criteria);

        return;
    }


    //
    // C++: double CvBoostParams::weight_trim_rate
    //

    public  double get_weight_trim_rate()
    {

        double retVal = get_weight_trim_rate_0(nativeObj);

        return retVal;
    }


    //
    // C++: void CvBoostParams::weight_trim_rate
    //

    public  void set_weight_trim_rate(double weight_trim_rate)
    {

        set_weight_trim_rate_0(nativeObj, weight_trim_rate);

        return;
    }


    @Override
    protected void finalize() throws Throwable {
        delete(nativeObj);
    }



    // C++:   CvBoostParams::CvBoostParams()
    private static native long CvBoostParams_0();

    // C++: int CvBoostParams::boost_type
    private static native int get_boost_type_0(long nativeObj);

    // C++: void CvBoostParams::boost_type
    private static native void set_boost_type_0(long nativeObj, int boost_type);

    // C++: int CvBoostParams::weak_count
    private static native int get_weak_count_0(long nativeObj);

    // C++: void CvBoostParams::weak_count
    private static native void set_weak_count_0(long nativeObj, int weak_count);

    // C++: int CvBoostParams::split_criteria
    private static native int get_split_criteria_0(long nativeObj);

    // C++: void CvBoostParams::split_criteria
    private static native void set_split_criteria_0(long nativeObj, int split_criteria);

    // C++: double CvBoostParams::weight_trim_rate
    private static native double get_weight_trim_rate_0(long nativeObj);

    // C++: void CvBoostParams::weight_trim_rate
    private static native void set_weight_trim_rate_0(long nativeObj, double weight_trim_rate);

    // native support for java finalize()
    private static native void delete(long nativeObj);

}
