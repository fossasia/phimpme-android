
//
// This file is auto-generated. Please don't modify it!
//
package org.opencv.ml;

import org.opencv.core.Mat;
import org.opencv.core.Range;

// C++: class CvGBTrees
/**
 * <p>The class implements the Gradient boosted tree model as described in the
 * beginning of this section.</p>
 *
 * @see <a href="http://docs.opencv.org/modules/ml/doc/gradient_boosted_trees.html#cvgbtrees">org.opencv.ml.CvGBTrees : public CvStatModel</a>
 */
public class CvGBTrees extends CvStatModel {

    protected CvGBTrees(long addr) { super(addr); }


    public static final int
            SQUARED_LOSS = 0,
            ABSOLUTE_LOSS = 0+1,
            HUBER_LOSS = 3,
            DEVIANCE_LOSS = 3+1;


    //
    // C++:   CvGBTrees::CvGBTrees()
    //

/**
 * <p>Default and training constructors.</p>
 *
 * <p>The constructors follow conventions of "CvStatModel.CvStatModel". See
 * "CvStatModel.train" for parameters descriptions.</p>
 *
 * @see <a href="http://docs.opencv.org/modules/ml/doc/gradient_boosted_trees.html#cvgbtrees-cvgbtrees">org.opencv.ml.CvGBTrees.CvGBTrees</a>
 */
    public   CvGBTrees()
    {

        super( CvGBTrees_0() );

        return;
    }


    //
    // C++:   CvGBTrees::CvGBTrees(Mat trainData, int tflag, Mat responses, Mat varIdx = cv::Mat(), Mat sampleIdx = cv::Mat(), Mat varType = cv::Mat(), Mat missingDataMask = cv::Mat(), CvGBTreesParams params = CvGBTreesParams())
    //

/**
 * <p>Default and training constructors.</p>
 *
 * <p>The constructors follow conventions of "CvStatModel.CvStatModel". See
 * "CvStatModel.train" for parameters descriptions.</p>
 *
 * @param trainData a trainData
 * @param tflag a tflag
 * @param responses a responses
 * @param varIdx a varIdx
 * @param sampleIdx a sampleIdx
 * @param varType a varType
 * @param missingDataMask a missingDataMask
 * @param params a params
 *
 * @see <a href="http://docs.opencv.org/modules/ml/doc/gradient_boosted_trees.html#cvgbtrees-cvgbtrees">org.opencv.ml.CvGBTrees.CvGBTrees</a>
 */
    public   CvGBTrees(Mat trainData, int tflag, Mat responses, Mat varIdx, Mat sampleIdx, Mat varType, Mat missingDataMask, CvGBTreesParams params)
    {

        super( CvGBTrees_1(trainData.nativeObj, tflag, responses.nativeObj, varIdx.nativeObj, sampleIdx.nativeObj, varType.nativeObj, missingDataMask.nativeObj, params.nativeObj) );

        return;
    }

/**
 * <p>Default and training constructors.</p>
 *
 * <p>The constructors follow conventions of "CvStatModel.CvStatModel". See
 * "CvStatModel.train" for parameters descriptions.</p>
 *
 * @param trainData a trainData
 * @param tflag a tflag
 * @param responses a responses
 *
 * @see <a href="http://docs.opencv.org/modules/ml/doc/gradient_boosted_trees.html#cvgbtrees-cvgbtrees">org.opencv.ml.CvGBTrees.CvGBTrees</a>
 */
    public   CvGBTrees(Mat trainData, int tflag, Mat responses)
    {

        super( CvGBTrees_2(trainData.nativeObj, tflag, responses.nativeObj) );

        return;
    }


    //
    // C++:  void CvGBTrees::clear()
    //

/**
 * <p>Clears the model.</p>
 *
 * <p>The function deletes the data set information and all the weak models and
 * sets all internal variables to the initial state. The function is called in
 * "CvGBTrees.train" and in the destructor.</p>
 *
 * @see <a href="http://docs.opencv.org/modules/ml/doc/gradient_boosted_trees.html#cvgbtrees-clear">org.opencv.ml.CvGBTrees.clear</a>
 */
    public  void clear()
    {

        clear_0(nativeObj);

        return;
    }


    //
    // C++:  float CvGBTrees::predict(Mat sample, Mat missing = cv::Mat(), Range slice = cv::Range::all(), int k = -1)
    //

/**
 * <p>Predicts a response for an input sample.</p>
 *
 * <p>The method predicts the response corresponding to the given sample (see
 * "Predicting with GBT").
 * The result is either the class label or the estimated function value. The
 * "CvGBTrees.predict" method enables using the parallel version of the GBT
 * model prediction if the OpenCV is built with the TBB library. In this case,
 * predictions of single trees are computed in a parallel fashion.</p>
 *
 * @param sample Input feature vector that has the same format as every training
 * set element. If not all the variables were actually used during training,
 * <code>sample</code> contains forged values at the appropriate places.
 * @param missing Missing values mask, which is a dimensional matrix of the same
 * size as <code>sample</code> having the <code>CV_8U</code> type.
 * <code>1</code> corresponds to the missing value in the same position in the
 * <code>sample</code> vector. If there are no missing values in the feature
 * vector, an empty matrix can be passed instead of the missing mask.
 * @param slice Parameter defining the part of the ensemble used for prediction.
 * <p>If <code>slice = Range.all()</code>, all trees are used. Use this parameter
 * to get predictions of the GBT models with different ensemble sizes learning
 * only one model.</p>
 * @param k Number of tree ensembles built in case of the classification problem
 * (see "Training GBT"). Use this parameter to change the output to sum of the
 * trees' predictions in the <code>k</code>-th ensemble only. To get the total
 * GBT model prediction, <code>k</code> value must be -1. For regression
 * problems, <code>k</code> is also equal to -1.
 *
 * @see <a href="http://docs.opencv.org/modules/ml/doc/gradient_boosted_trees.html#cvgbtrees-predict">org.opencv.ml.CvGBTrees.predict</a>
 */
    public  float predict(Mat sample, Mat missing, Range slice, int k)
    {

        float retVal = predict_0(nativeObj, sample.nativeObj, missing.nativeObj, slice.start, slice.end, k);

        return retVal;
    }

/**
 * <p>Predicts a response for an input sample.</p>
 *
 * <p>The method predicts the response corresponding to the given sample (see
 * "Predicting with GBT").
 * The result is either the class label or the estimated function value. The
 * "CvGBTrees.predict" method enables using the parallel version of the GBT
 * model prediction if the OpenCV is built with the TBB library. In this case,
 * predictions of single trees are computed in a parallel fashion.</p>
 *
 * @param sample Input feature vector that has the same format as every training
 * set element. If not all the variables were actually used during training,
 * <code>sample</code> contains forged values at the appropriate places.
 *
 * @see <a href="http://docs.opencv.org/modules/ml/doc/gradient_boosted_trees.html#cvgbtrees-predict">org.opencv.ml.CvGBTrees.predict</a>
 */
    public  float predict(Mat sample)
    {

        float retVal = predict_1(nativeObj, sample.nativeObj);

        return retVal;
    }


    //
    // C++:  bool CvGBTrees::train(Mat trainData, int tflag, Mat responses, Mat varIdx = cv::Mat(), Mat sampleIdx = cv::Mat(), Mat varType = cv::Mat(), Mat missingDataMask = cv::Mat(), CvGBTreesParams params = CvGBTreesParams(), bool update = false)
    //

/**
 * <p>Trains a Gradient boosted tree model.</p>
 *
 * <p>The first train method follows the common template (see "CvStatModel.train").
 * Both <code>tflag</code> values (<code>CV_ROW_SAMPLE</code>, <code>CV_COL_SAMPLE</code>)
 * are supported.
 * <code>trainData</code> must be of the <code>CV_32F</code> type.
 * <code>responses</code> must be a matrix of type <code>CV_32S</code> or
 * <code>CV_32F</code>. In both cases it is converted into the <code>CV_32F</code>
 * matrix inside the training procedure. <code>varIdx</code> and
 * <code>sampleIdx</code> must be a list of indices (<code>CV_32S</code>) or a
 * mask (<code>CV_8U</code> or <code>CV_8S</code>). <code>update</code> is a
 * dummy parameter.</p>
 *
 * <p>The second form of "CvGBTrees.train" function uses "CvMLData" as a data set
 * container. <code>update</code> is still a dummy parameter.</p>
 *
 * <p>All parameters specific to the GBT model are passed into the training
 * function as a "CvGBTreesParams" structure.</p>
 *
 * @param trainData a trainData
 * @param tflag a tflag
 * @param responses a responses
 * @param varIdx a varIdx
 * @param sampleIdx a sampleIdx
 * @param varType a varType
 * @param missingDataMask a missingDataMask
 * @param params a params
 * @param update a update
 *
 * @see <a href="http://docs.opencv.org/modules/ml/doc/gradient_boosted_trees.html#cvgbtrees-train">org.opencv.ml.CvGBTrees.train</a>
 */
    public  boolean train(Mat trainData, int tflag, Mat responses, Mat varIdx, Mat sampleIdx, Mat varType, Mat missingDataMask, CvGBTreesParams params, boolean update)
    {

        boolean retVal = train_0(nativeObj, trainData.nativeObj, tflag, responses.nativeObj, varIdx.nativeObj, sampleIdx.nativeObj, varType.nativeObj, missingDataMask.nativeObj, params.nativeObj, update);

        return retVal;
    }

/**
 * <p>Trains a Gradient boosted tree model.</p>
 *
 * <p>The first train method follows the common template (see "CvStatModel.train").
 * Both <code>tflag</code> values (<code>CV_ROW_SAMPLE</code>, <code>CV_COL_SAMPLE</code>)
 * are supported.
 * <code>trainData</code> must be of the <code>CV_32F</code> type.
 * <code>responses</code> must be a matrix of type <code>CV_32S</code> or
 * <code>CV_32F</code>. In both cases it is converted into the <code>CV_32F</code>
 * matrix inside the training procedure. <code>varIdx</code> and
 * <code>sampleIdx</code> must be a list of indices (<code>CV_32S</code>) or a
 * mask (<code>CV_8U</code> or <code>CV_8S</code>). <code>update</code> is a
 * dummy parameter.</p>
 *
 * <p>The second form of "CvGBTrees.train" function uses "CvMLData" as a data set
 * container. <code>update</code> is still a dummy parameter.</p>
 *
 * <p>All parameters specific to the GBT model are passed into the training
 * function as a "CvGBTreesParams" structure.</p>
 *
 * @param trainData a trainData
 * @param tflag a tflag
 * @param responses a responses
 *
 * @see <a href="http://docs.opencv.org/modules/ml/doc/gradient_boosted_trees.html#cvgbtrees-train">org.opencv.ml.CvGBTrees.train</a>
 */
    public  boolean train(Mat trainData, int tflag, Mat responses)
    {

        boolean retVal = train_1(nativeObj, trainData.nativeObj, tflag, responses.nativeObj);

        return retVal;
    }


    @Override
    protected void finalize() throws Throwable {
        delete(nativeObj);
    }



    // C++:   CvGBTrees::CvGBTrees()
    private static native long CvGBTrees_0();

    // C++:   CvGBTrees::CvGBTrees(Mat trainData, int tflag, Mat responses, Mat varIdx = cv::Mat(), Mat sampleIdx = cv::Mat(), Mat varType = cv::Mat(), Mat missingDataMask = cv::Mat(), CvGBTreesParams params = CvGBTreesParams())
    private static native long CvGBTrees_1(long trainData_nativeObj, int tflag, long responses_nativeObj, long varIdx_nativeObj, long sampleIdx_nativeObj, long varType_nativeObj, long missingDataMask_nativeObj, long params_nativeObj);
    private static native long CvGBTrees_2(long trainData_nativeObj, int tflag, long responses_nativeObj);

    // C++:  void CvGBTrees::clear()
    private static native void clear_0(long nativeObj);

    // C++:  float CvGBTrees::predict(Mat sample, Mat missing = cv::Mat(), Range slice = cv::Range::all(), int k = -1)
    private static native float predict_0(long nativeObj, long sample_nativeObj, long missing_nativeObj, int slice_start, int slice_end, int k);
    private static native float predict_1(long nativeObj, long sample_nativeObj);

    // C++:  bool CvGBTrees::train(Mat trainData, int tflag, Mat responses, Mat varIdx = cv::Mat(), Mat sampleIdx = cv::Mat(), Mat varType = cv::Mat(), Mat missingDataMask = cv::Mat(), CvGBTreesParams params = CvGBTreesParams(), bool update = false)
    private static native boolean train_0(long nativeObj, long trainData_nativeObj, int tflag, long responses_nativeObj, long varIdx_nativeObj, long sampleIdx_nativeObj, long varType_nativeObj, long missingDataMask_nativeObj, long params_nativeObj, boolean update);
    private static native boolean train_1(long nativeObj, long trainData_nativeObj, int tflag, long responses_nativeObj);

    // native support for java finalize()
    private static native void delete(long nativeObj);

}
