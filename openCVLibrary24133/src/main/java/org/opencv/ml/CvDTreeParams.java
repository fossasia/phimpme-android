
//
// This file is auto-generated. Please don't modify it!
//
package org.opencv.ml;



// C++: class CvDTreeParams
/**
 * <p>The structure contains all the decision tree training parameters. You can
 * initialize it by default constructor and then override any parameters
 * directly before training, or the structure may be fully initialized using the
 * advanced variant of the constructor.</p>
 *
 * @see <a href="http://docs.opencv.org/modules/ml/doc/decision_trees.html#cvdtreeparams">org.opencv.ml.CvDTreeParams</a>
 */
public class CvDTreeParams {

    protected final long nativeObj;
    protected CvDTreeParams(long addr) { nativeObj = addr; }


    //
    // C++:   CvDTreeParams::CvDTreeParams()
    //

/**
 * <p>The constructors.</p>
 *
 * <p>The default constructor initializes all the parameters with the default
 * values tuned for the standalone classification tree:</p>
 *
 * <p><code></p>
 *
 * <p>// C++ code:</p>
 *
 * <p>CvDTreeParams() : max_categories(10), max_depth(INT_MAX), min_sample_count(10),</p>
 *
 * <p>cv_folds(10), use_surrogates(true), use_1se_rule(true),</p>
 *
 * <p>truncate_pruned_tree(true), regression_accuracy(0.01f), priors(0)</p>
 *
 * <p>{}</p>
 *
 * @see <a href="http://docs.opencv.org/modules/ml/doc/decision_trees.html#cvdtreeparams-cvdtreeparams">org.opencv.ml.CvDTreeParams.CvDTreeParams</a>
 */
    public   CvDTreeParams()
    {

        nativeObj = CvDTreeParams_0();

        return;
    }


    //
    // C++: int CvDTreeParams::max_categories
    //

    public  int get_max_categories()
    {

        int retVal = get_max_categories_0(nativeObj);

        return retVal;
    }


    //
    // C++: void CvDTreeParams::max_categories
    //

    public  void set_max_categories(int max_categories)
    {

        set_max_categories_0(nativeObj, max_categories);

        return;
    }


    //
    // C++: int CvDTreeParams::max_depth
    //

    public  int get_max_depth()
    {

        int retVal = get_max_depth_0(nativeObj);

        return retVal;
    }


    //
    // C++: void CvDTreeParams::max_depth
    //

    public  void set_max_depth(int max_depth)
    {

        set_max_depth_0(nativeObj, max_depth);

        return;
    }


    //
    // C++: int CvDTreeParams::min_sample_count
    //

    public  int get_min_sample_count()
    {

        int retVal = get_min_sample_count_0(nativeObj);

        return retVal;
    }


    //
    // C++: void CvDTreeParams::min_sample_count
    //

    public  void set_min_sample_count(int min_sample_count)
    {

        set_min_sample_count_0(nativeObj, min_sample_count);

        return;
    }


    //
    // C++: int CvDTreeParams::cv_folds
    //

    public  int get_cv_folds()
    {

        int retVal = get_cv_folds_0(nativeObj);

        return retVal;
    }


    //
    // C++: void CvDTreeParams::cv_folds
    //

    public  void set_cv_folds(int cv_folds)
    {

        set_cv_folds_0(nativeObj, cv_folds);

        return;
    }


    //
    // C++: bool CvDTreeParams::use_surrogates
    //

    public  boolean get_use_surrogates()
    {

        boolean retVal = get_use_surrogates_0(nativeObj);

        return retVal;
    }


    //
    // C++: void CvDTreeParams::use_surrogates
    //

    public  void set_use_surrogates(boolean use_surrogates)
    {

        set_use_surrogates_0(nativeObj, use_surrogates);

        return;
    }


    //
    // C++: bool CvDTreeParams::use_1se_rule
    //

    public  boolean get_use_1se_rule()
    {

        boolean retVal = get_use_1se_rule_0(nativeObj);

        return retVal;
    }


    //
    // C++: void CvDTreeParams::use_1se_rule
    //

    public  void set_use_1se_rule(boolean use_1se_rule)
    {

        set_use_1se_rule_0(nativeObj, use_1se_rule);

        return;
    }


    //
    // C++: bool CvDTreeParams::truncate_pruned_tree
    //

    public  boolean get_truncate_pruned_tree()
    {

        boolean retVal = get_truncate_pruned_tree_0(nativeObj);

        return retVal;
    }


    //
    // C++: void CvDTreeParams::truncate_pruned_tree
    //

    public  void set_truncate_pruned_tree(boolean truncate_pruned_tree)
    {

        set_truncate_pruned_tree_0(nativeObj, truncate_pruned_tree);

        return;
    }


    //
    // C++: float CvDTreeParams::regression_accuracy
    //

    public  float get_regression_accuracy()
    {

        float retVal = get_regression_accuracy_0(nativeObj);

        return retVal;
    }


    //
    // C++: void CvDTreeParams::regression_accuracy
    //

    public  void set_regression_accuracy(float regression_accuracy)
    {

        set_regression_accuracy_0(nativeObj, regression_accuracy);

        return;
    }


    @Override
    protected void finalize() throws Throwable {
        delete(nativeObj);
    }



    // C++:   CvDTreeParams::CvDTreeParams()
    private static native long CvDTreeParams_0();

    // C++: int CvDTreeParams::max_categories
    private static native int get_max_categories_0(long nativeObj);

    // C++: void CvDTreeParams::max_categories
    private static native void set_max_categories_0(long nativeObj, int max_categories);

    // C++: int CvDTreeParams::max_depth
    private static native int get_max_depth_0(long nativeObj);

    // C++: void CvDTreeParams::max_depth
    private static native void set_max_depth_0(long nativeObj, int max_depth);

    // C++: int CvDTreeParams::min_sample_count
    private static native int get_min_sample_count_0(long nativeObj);

    // C++: void CvDTreeParams::min_sample_count
    private static native void set_min_sample_count_0(long nativeObj, int min_sample_count);

    // C++: int CvDTreeParams::cv_folds
    private static native int get_cv_folds_0(long nativeObj);

    // C++: void CvDTreeParams::cv_folds
    private static native void set_cv_folds_0(long nativeObj, int cv_folds);

    // C++: bool CvDTreeParams::use_surrogates
    private static native boolean get_use_surrogates_0(long nativeObj);

    // C++: void CvDTreeParams::use_surrogates
    private static native void set_use_surrogates_0(long nativeObj, boolean use_surrogates);

    // C++: bool CvDTreeParams::use_1se_rule
    private static native boolean get_use_1se_rule_0(long nativeObj);

    // C++: void CvDTreeParams::use_1se_rule
    private static native void set_use_1se_rule_0(long nativeObj, boolean use_1se_rule);

    // C++: bool CvDTreeParams::truncate_pruned_tree
    private static native boolean get_truncate_pruned_tree_0(long nativeObj);

    // C++: void CvDTreeParams::truncate_pruned_tree
    private static native void set_truncate_pruned_tree_0(long nativeObj, boolean truncate_pruned_tree);

    // C++: float CvDTreeParams::regression_accuracy
    private static native float get_regression_accuracy_0(long nativeObj);

    // C++: void CvDTreeParams::regression_accuracy
    private static native void set_regression_accuracy_0(long nativeObj, float regression_accuracy);

    // native support for java finalize()
    private static native void delete(long nativeObj);

}
