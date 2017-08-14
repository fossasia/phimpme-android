
//
// This file is auto-generated. Please don't modify it!
//
package org.opencv.ml;

import org.opencv.core.Mat;

// C++: class CvSVM
/**
 * <p>Support Vector Machines.</p>
 *
 * <p>Note:</p>
 * <ul>
 *   <li> (Python) An example of digit recognition using SVM can be found at
 * opencv_source/samples/python2/digits.py
 *   <li> (Python) An example of grid search digit recognition using SVM can be
 * found at opencv_source/samples/python2/digits_adjust.py
 *   <li> (Python) An example of video digit recognition using SVM can be found
 * at opencv_source/samples/python2/digits_video.py
 * </ul>
 *
 * @see <a href="http://docs.opencv.org/modules/ml/doc/support_vector_machines.html#cvsvm">org.opencv.ml.CvSVM : public CvStatModel</a>
 */
public class CvSVM extends CvStatModel {

    protected CvSVM(long addr) { super(addr); }


    public static final int
            C_SVC = 100,
            NU_SVC = 101,
            ONE_CLASS = 102,
            EPS_SVR = 103,
            NU_SVR = 104,
            LINEAR = 0,
            POLY = 1,
            RBF = 2,
            SIGMOID = 3,
            C = 0,
            GAMMA = 1,
            P = 2,
            NU = 3,
            COEF = 4,
            DEGREE = 5;


    //
    // C++:   CvSVM::CvSVM()
    //

/**
 * <p>Default and training constructors.</p>
 *
 * <p>The constructors follow conventions of "CvStatModel.CvStatModel". See
 * "CvStatModel.train" for parameters descriptions.</p>
 *
 * @see <a href="http://docs.opencv.org/modules/ml/doc/support_vector_machines.html#cvsvm-cvsvm">org.opencv.ml.CvSVM.CvSVM</a>
 */
    public   CvSVM()
    {

        super( CvSVM_0() );

        return;
    }


    //
    // C++:   CvSVM::CvSVM(Mat trainData, Mat responses, Mat varIdx = cv::Mat(), Mat sampleIdx = cv::Mat(), CvSVMParams params = CvSVMParams())
    //

/**
 * <p>Default and training constructors.</p>
 *
 * <p>The constructors follow conventions of "CvStatModel.CvStatModel". See
 * "CvStatModel.train" for parameters descriptions.</p>
 *
 * @param trainData a trainData
 * @param responses a responses
 * @param varIdx a varIdx
 * @param sampleIdx a sampleIdx
 * @param params a params
 *
 * @see <a href="http://docs.opencv.org/modules/ml/doc/support_vector_machines.html#cvsvm-cvsvm">org.opencv.ml.CvSVM.CvSVM</a>
 */
    public   CvSVM(Mat trainData, Mat responses, Mat varIdx, Mat sampleIdx, CvSVMParams params)
    {

        super( CvSVM_1(trainData.nativeObj, responses.nativeObj, varIdx.nativeObj, sampleIdx.nativeObj, params.nativeObj) );

        return;
    }

/**
 * <p>Default and training constructors.</p>
 *
 * <p>The constructors follow conventions of "CvStatModel.CvStatModel". See
 * "CvStatModel.train" for parameters descriptions.</p>
 *
 * @param trainData a trainData
 * @param responses a responses
 *
 * @see <a href="http://docs.opencv.org/modules/ml/doc/support_vector_machines.html#cvsvm-cvsvm">org.opencv.ml.CvSVM.CvSVM</a>
 */
    public   CvSVM(Mat trainData, Mat responses)
    {

        super( CvSVM_2(trainData.nativeObj, responses.nativeObj) );

        return;
    }


    //
    // C++:  void CvSVM::clear()
    //

    public  void clear()
    {

        clear_0(nativeObj);

        return;
    }


    //
    // C++:  int CvSVM::get_support_vector_count()
    //

    public  int get_support_vector_count()
    {

        int retVal = get_support_vector_count_0(nativeObj);

        return retVal;
    }


    //
    // C++:  int CvSVM::get_var_count()
    //

/**
 * <p>Returns the number of used features (variables count).</p>
 *
 * @see <a href="http://docs.opencv.org/modules/ml/doc/support_vector_machines.html#cvsvm-get-var-count">org.opencv.ml.CvSVM.get_var_count</a>
 */
    public  int get_var_count()
    {

        int retVal = get_var_count_0(nativeObj);

        return retVal;
    }


    //
    // C++:  float CvSVM::predict(Mat sample, bool returnDFVal = false)
    //

/**
 * <p>Predicts the response for input sample(s).</p>
 *
 * <p>If you pass one sample then prediction result is returned. If you want to get
 * responses for several samples then you should pass the <code>results</code>
 * matrix where prediction results will be stored.</p>
 *
 * <p>The function is parallelized with the TBB library.</p>
 *
 * @param sample Input sample for prediction.
 * @param returnDFVal Specifies a type of the return value. If <code>true</code>
 * and the problem is 2-class classification then the method returns the
 * decision function value that is signed distance to the margin, else the
 * function returns a class label (classification) or estimated function value
 * (regression).
 *
 * @see <a href="http://docs.opencv.org/modules/ml/doc/support_vector_machines.html#cvsvm-predict">org.opencv.ml.CvSVM.predict</a>
 */
    public  float predict(Mat sample, boolean returnDFVal)
    {

        float retVal = predict_0(nativeObj, sample.nativeObj, returnDFVal);

        return retVal;
    }

/**
 * <p>Predicts the response for input sample(s).</p>
 *
 * <p>If you pass one sample then prediction result is returned. If you want to get
 * responses for several samples then you should pass the <code>results</code>
 * matrix where prediction results will be stored.</p>
 *
 * <p>The function is parallelized with the TBB library.</p>
 *
 * @param sample Input sample for prediction.
 *
 * @see <a href="http://docs.opencv.org/modules/ml/doc/support_vector_machines.html#cvsvm-predict">org.opencv.ml.CvSVM.predict</a>
 */
    public  float predict(Mat sample)
    {

        float retVal = predict_1(nativeObj, sample.nativeObj);

        return retVal;
    }


    //
    // C++:  void CvSVM::predict(Mat samples, Mat& results)
    //

/**
 * <p>Predicts the response for input sample(s).</p>
 *
 * <p>If you pass one sample then prediction result is returned. If you want to get
 * responses for several samples then you should pass the <code>results</code>
 * matrix where prediction results will be stored.</p>
 *
 * <p>The function is parallelized with the TBB library.</p>
 *
 * @param samples Input samples for prediction.
 * @param results Output prediction responses for corresponding samples.
 *
 * @see <a href="http://docs.opencv.org/modules/ml/doc/support_vector_machines.html#cvsvm-predict">org.opencv.ml.CvSVM.predict</a>
 */
    public  void predict_all(Mat samples, Mat results)
    {

        predict_all_0(nativeObj, samples.nativeObj, results.nativeObj);

        return;
    }


    //
    // C++:  bool CvSVM::train(Mat trainData, Mat responses, Mat varIdx = cv::Mat(), Mat sampleIdx = cv::Mat(), CvSVMParams params = CvSVMParams())
    //

/**
 * <p>Trains an SVM.</p>
 *
 * <p>The method trains the SVM model. It follows the conventions of the generic
 * "CvStatModel.train" approach with the following limitations:</p>
 * <ul>
 *   <li> Only the <code>CV_ROW_SAMPLE</code> data layout is supported.
 *   <li> Input variables are all ordered.
 *   <li> Output variables can be either categorical (<code>params.svm_type=CvSVM.C_SVC</code>
 * or <code>params.svm_type=CvSVM.NU_SVC</code>), or ordered (<code>params.svm_type=CvSVM.EPS_SVR</code>
 * or <code>params.svm_type=CvSVM.NU_SVR</code>), or not required at all
 * (<code>params.svm_type=CvSVM.ONE_CLASS</code>).
 *   <li> Missing measurements are not supported.
 * </ul>
 *
 * <p>All the other parameters are gathered in the "CvSVMParams" structure.</p>
 *
 * @param trainData a trainData
 * @param responses a responses
 * @param varIdx a varIdx
 * @param sampleIdx a sampleIdx
 * @param params a params
 *
 * @see <a href="http://docs.opencv.org/modules/ml/doc/support_vector_machines.html#cvsvm-train">org.opencv.ml.CvSVM.train</a>
 */
    public  boolean train(Mat trainData, Mat responses, Mat varIdx, Mat sampleIdx, CvSVMParams params)
    {

        boolean retVal = train_0(nativeObj, trainData.nativeObj, responses.nativeObj, varIdx.nativeObj, sampleIdx.nativeObj, params.nativeObj);

        return retVal;
    }

/**
 * <p>Trains an SVM.</p>
 *
 * <p>The method trains the SVM model. It follows the conventions of the generic
 * "CvStatModel.train" approach with the following limitations:</p>
 * <ul>
 *   <li> Only the <code>CV_ROW_SAMPLE</code> data layout is supported.
 *   <li> Input variables are all ordered.
 *   <li> Output variables can be either categorical (<code>params.svm_type=CvSVM.C_SVC</code>
 * or <code>params.svm_type=CvSVM.NU_SVC</code>), or ordered (<code>params.svm_type=CvSVM.EPS_SVR</code>
 * or <code>params.svm_type=CvSVM.NU_SVR</code>), or not required at all
 * (<code>params.svm_type=CvSVM.ONE_CLASS</code>).
 *   <li> Missing measurements are not supported.
 * </ul>
 *
 * <p>All the other parameters are gathered in the "CvSVMParams" structure.</p>
 *
 * @param trainData a trainData
 * @param responses a responses
 *
 * @see <a href="http://docs.opencv.org/modules/ml/doc/support_vector_machines.html#cvsvm-train">org.opencv.ml.CvSVM.train</a>
 */
    public  boolean train(Mat trainData, Mat responses)
    {

        boolean retVal = train_1(nativeObj, trainData.nativeObj, responses.nativeObj);

        return retVal;
    }


    //
    // C++:  bool CvSVM::train_auto(Mat trainData, Mat responses, Mat varIdx, Mat sampleIdx, CvSVMParams params, int k_fold = 10, CvParamGrid Cgrid = CvSVM::get_default_grid(CvSVM::C), CvParamGrid gammaGrid = CvSVM::get_default_grid(CvSVM::GAMMA), CvParamGrid pGrid = CvSVM::get_default_grid(CvSVM::P), CvParamGrid nuGrid = CvSVM::get_default_grid(CvSVM::NU), CvParamGrid coeffGrid = CvSVM::get_default_grid(CvSVM::COEF), CvParamGrid degreeGrid = CvSVM::get_default_grid(CvSVM::DEGREE), bool balanced = false)
    //

/**
 * <p>Trains an SVM with optimal parameters.</p>
 *
 * <p>The method trains the SVM model automatically by choosing the optimal
 * parameters <code>C</code>, <code>gamma</code>, <code>p</code>,
 * <code>nu</code>, <code>coef0</code>, <code>degree</code> from "CvSVMParams".
 * Parameters are considered optimal when the cross-validation estimate of the
 * test set error is minimal.</p>
 *
 * <p>If there is no need to optimize a parameter, the corresponding grid step
 * should be set to any value less than or equal to 1. For example, to avoid
 * optimization in <code>gamma</code>, set <code>gamma_grid.step = 0</code>,
 * <code>gamma_grid.min_val</code>, <code>gamma_grid.max_val</code> as arbitrary
 * numbers. In this case, the value <code>params.gamma</code> is taken for
 * <code>gamma</code>.</p>
 *
 * <p>And, finally, if the optimization in a parameter is required but the
 * corresponding grid is unknown, you may call the function "CvSVM.get_default_grid".
 * To generate a grid, for example, for <code>gamma</code>, call
 * <code>CvSVM.get_default_grid(CvSVM.GAMMA)</code>.</p>
 *
 * <p>This function works for the classification (<code>params.svm_type=CvSVM.C_SVC</code>
 * or <code>params.svm_type=CvSVM.NU_SVC</code>) as well as for the regression
 * (<code>params.svm_type=CvSVM.EPS_SVR</code> or <code>params.svm_type=CvSVM.NU_SVR</code>).
 * If <code>params.svm_type=CvSVM.ONE_CLASS</code>, no optimization is made and
 * the usual SVM with parameters specified in <code>params</code> is executed.</p>
 *
 * @param trainData a trainData
 * @param responses a responses
 * @param varIdx a varIdx
 * @param sampleIdx a sampleIdx
 * @param params a params
 * @param k_fold Cross-validation parameter. The training set is divided into
 * <code>k_fold</code> subsets. One subset is used to test the model, the others
 * form the train set. So, the SVM algorithm is executed <code>k_fold</code>
 * times.
 * @param Cgrid a Cgrid
 * @param gammaGrid Iteration grid for the corresponding SVM parameter.
 * @param pGrid Iteration grid for the corresponding SVM parameter.
 * @param nuGrid Iteration grid for the corresponding SVM parameter.
 * @param coeffGrid Iteration grid for the corresponding SVM parameter.
 * @param degreeGrid Iteration grid for the corresponding SVM parameter.
 * @param balanced If <code>true</code> and the problem is 2-class
 * classification then the method creates more balanced cross-validation subsets
 * that is proportions between classes in subsets are close to such proportion
 * in the whole train dataset.
 *
 * @see <a href="http://docs.opencv.org/modules/ml/doc/support_vector_machines.html#cvsvm-train-auto">org.opencv.ml.CvSVM.train_auto</a>
 */
    public  boolean train_auto(Mat trainData, Mat responses, Mat varIdx, Mat sampleIdx, CvSVMParams params, int k_fold, CvParamGrid Cgrid, CvParamGrid gammaGrid, CvParamGrid pGrid, CvParamGrid nuGrid, CvParamGrid coeffGrid, CvParamGrid degreeGrid, boolean balanced)
    {

        boolean retVal = train_auto_0(nativeObj, trainData.nativeObj, responses.nativeObj, varIdx.nativeObj, sampleIdx.nativeObj, params.nativeObj, k_fold, Cgrid.nativeObj, gammaGrid.nativeObj, pGrid.nativeObj, nuGrid.nativeObj, coeffGrid.nativeObj, degreeGrid.nativeObj, balanced);

        return retVal;
    }

/**
 * <p>Trains an SVM with optimal parameters.</p>
 *
 * <p>The method trains the SVM model automatically by choosing the optimal
 * parameters <code>C</code>, <code>gamma</code>, <code>p</code>,
 * <code>nu</code>, <code>coef0</code>, <code>degree</code> from "CvSVMParams".
 * Parameters are considered optimal when the cross-validation estimate of the
 * test set error is minimal.</p>
 *
 * <p>If there is no need to optimize a parameter, the corresponding grid step
 * should be set to any value less than or equal to 1. For example, to avoid
 * optimization in <code>gamma</code>, set <code>gamma_grid.step = 0</code>,
 * <code>gamma_grid.min_val</code>, <code>gamma_grid.max_val</code> as arbitrary
 * numbers. In this case, the value <code>params.gamma</code> is taken for
 * <code>gamma</code>.</p>
 *
 * <p>And, finally, if the optimization in a parameter is required but the
 * corresponding grid is unknown, you may call the function "CvSVM.get_default_grid".
 * To generate a grid, for example, for <code>gamma</code>, call
 * <code>CvSVM.get_default_grid(CvSVM.GAMMA)</code>.</p>
 *
 * <p>This function works for the classification (<code>params.svm_type=CvSVM.C_SVC</code>
 * or <code>params.svm_type=CvSVM.NU_SVC</code>) as well as for the regression
 * (<code>params.svm_type=CvSVM.EPS_SVR</code> or <code>params.svm_type=CvSVM.NU_SVR</code>).
 * If <code>params.svm_type=CvSVM.ONE_CLASS</code>, no optimization is made and
 * the usual SVM with parameters specified in <code>params</code> is executed.</p>
 *
 * @param trainData a trainData
 * @param responses a responses
 * @param varIdx a varIdx
 * @param sampleIdx a sampleIdx
 * @param params a params
 *
 * @see <a href="http://docs.opencv.org/modules/ml/doc/support_vector_machines.html#cvsvm-train-auto">org.opencv.ml.CvSVM.train_auto</a>
 */
    public  boolean train_auto(Mat trainData, Mat responses, Mat varIdx, Mat sampleIdx, CvSVMParams params)
    {

        boolean retVal = train_auto_1(nativeObj, trainData.nativeObj, responses.nativeObj, varIdx.nativeObj, sampleIdx.nativeObj, params.nativeObj);

        return retVal;
    }


    @Override
    protected void finalize() throws Throwable {
        delete(nativeObj);
    }



    // C++:   CvSVM::CvSVM()
    private static native long CvSVM_0();

    // C++:   CvSVM::CvSVM(Mat trainData, Mat responses, Mat varIdx = cv::Mat(), Mat sampleIdx = cv::Mat(), CvSVMParams params = CvSVMParams())
    private static native long CvSVM_1(long trainData_nativeObj, long responses_nativeObj, long varIdx_nativeObj, long sampleIdx_nativeObj, long params_nativeObj);
    private static native long CvSVM_2(long trainData_nativeObj, long responses_nativeObj);

    // C++:  void CvSVM::clear()
    private static native void clear_0(long nativeObj);

    // C++:  int CvSVM::get_support_vector_count()
    private static native int get_support_vector_count_0(long nativeObj);

    // C++:  int CvSVM::get_var_count()
    private static native int get_var_count_0(long nativeObj);

    // C++:  float CvSVM::predict(Mat sample, bool returnDFVal = false)
    private static native float predict_0(long nativeObj, long sample_nativeObj, boolean returnDFVal);
    private static native float predict_1(long nativeObj, long sample_nativeObj);

    // C++:  void CvSVM::predict(Mat samples, Mat& results)
    private static native void predict_all_0(long nativeObj, long samples_nativeObj, long results_nativeObj);

    // C++:  bool CvSVM::train(Mat trainData, Mat responses, Mat varIdx = cv::Mat(), Mat sampleIdx = cv::Mat(), CvSVMParams params = CvSVMParams())
    private static native boolean train_0(long nativeObj, long trainData_nativeObj, long responses_nativeObj, long varIdx_nativeObj, long sampleIdx_nativeObj, long params_nativeObj);
    private static native boolean train_1(long nativeObj, long trainData_nativeObj, long responses_nativeObj);

    // C++:  bool CvSVM::train_auto(Mat trainData, Mat responses, Mat varIdx, Mat sampleIdx, CvSVMParams params, int k_fold = 10, CvParamGrid Cgrid = CvSVM::get_default_grid(CvSVM::C), CvParamGrid gammaGrid = CvSVM::get_default_grid(CvSVM::GAMMA), CvParamGrid pGrid = CvSVM::get_default_grid(CvSVM::P), CvParamGrid nuGrid = CvSVM::get_default_grid(CvSVM::NU), CvParamGrid coeffGrid = CvSVM::get_default_grid(CvSVM::COEF), CvParamGrid degreeGrid = CvSVM::get_default_grid(CvSVM::DEGREE), bool balanced = false)
    private static native boolean train_auto_0(long nativeObj, long trainData_nativeObj, long responses_nativeObj, long varIdx_nativeObj, long sampleIdx_nativeObj, long params_nativeObj, int k_fold, long Cgrid_nativeObj, long gammaGrid_nativeObj, long pGrid_nativeObj, long nuGrid_nativeObj, long coeffGrid_nativeObj, long degreeGrid_nativeObj, boolean balanced);
    private static native boolean train_auto_1(long nativeObj, long trainData_nativeObj, long responses_nativeObj, long varIdx_nativeObj, long sampleIdx_nativeObj, long params_nativeObj);

    // native support for java finalize()
    private static native void delete(long nativeObj);

}
