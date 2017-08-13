
//
// This file is auto-generated. Please don't modify it!
//
package org.opencv.ml;

import org.opencv.core.TermCriteria;

// C++: class CvRTParams
/**
 * <p>Training parameters of random trees.</p>
 *
 * <p>The set of training parameters for the forest is a superset of the training
 * parameters for a single tree. However, random trees do not need all the
 * functionality/features of decision trees. Most noticeably, the trees are not
 * pruned, so the cross-validation parameters are not used.</p>
 *
 * @see <a href="http://docs.opencv.org/modules/ml/doc/random_trees.html#cvrtparams">org.opencv.ml.CvRTParams : public CvDTreeParams</a>
 */
public class CvRTParams extends CvDTreeParams {

    protected CvRTParams(long addr) { super(addr); }


    //
    // C++:   CvRTParams::CvRTParams()
    //

    public   CvRTParams()
    {

        super( CvRTParams_0() );

        return;
    }


    //
    // C++: bool CvRTParams::calc_var_importance
    //

    public  boolean get_calc_var_importance()
    {

        boolean retVal = get_calc_var_importance_0(nativeObj);

        return retVal;
    }


    //
    // C++: void CvRTParams::calc_var_importance
    //

    public  void set_calc_var_importance(boolean calc_var_importance)
    {

        set_calc_var_importance_0(nativeObj, calc_var_importance);

        return;
    }


    //
    // C++: int CvRTParams::nactive_vars
    //

    public  int get_nactive_vars()
    {

        int retVal = get_nactive_vars_0(nativeObj);

        return retVal;
    }


    //
    // C++: void CvRTParams::nactive_vars
    //

    public  void set_nactive_vars(int nactive_vars)
    {

        set_nactive_vars_0(nativeObj, nactive_vars);

        return;
    }


    //
    // C++: TermCriteria CvRTParams::term_crit
    //

    public  TermCriteria get_term_crit()
    {

        TermCriteria retVal = new TermCriteria(get_term_crit_0(nativeObj));

        return retVal;
    }


    //
    // C++: void CvRTParams::term_crit
    //

    public  void set_term_crit(TermCriteria term_crit)
    {

        set_term_crit_0(nativeObj, term_crit.type, term_crit.maxCount, term_crit.epsilon);

        return;
    }


    @Override
    protected void finalize() throws Throwable {
        delete(nativeObj);
    }



    // C++:   CvRTParams::CvRTParams()
    private static native long CvRTParams_0();

    // C++: bool CvRTParams::calc_var_importance
    private static native boolean get_calc_var_importance_0(long nativeObj);

    // C++: void CvRTParams::calc_var_importance
    private static native void set_calc_var_importance_0(long nativeObj, boolean calc_var_importance);

    // C++: int CvRTParams::nactive_vars
    private static native int get_nactive_vars_0(long nativeObj);

    // C++: void CvRTParams::nactive_vars
    private static native void set_nactive_vars_0(long nativeObj, int nactive_vars);

    // C++: TermCriteria CvRTParams::term_crit
    private static native double[] get_term_crit_0(long nativeObj);

    // C++: void CvRTParams::term_crit
    private static native void set_term_crit_0(long nativeObj, int term_crit_type, int term_crit_maxCount, double term_crit_epsilon);

    // native support for java finalize()
    private static native void delete(long nativeObj);

}
