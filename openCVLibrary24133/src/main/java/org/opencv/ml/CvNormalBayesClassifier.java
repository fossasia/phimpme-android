
//
// This file is auto-generated. Please don't modify it!
//
package org.opencv.ml;

import org.opencv.core.Mat;

// C++: class CvNormalBayesClassifier
/**
 * <p>Bayes classifier for normally distributed data.</p>
 *
 * @see <a href="http://docs.opencv.org/modules/ml/doc/normal_bayes_classifier.html#cvnormalbayesclassifier">org.opencv.ml.CvNormalBayesClassifier : public CvStatModel</a>
 */
public class CvNormalBayesClassifier extends CvStatModel {

    protected CvNormalBayesClassifier(long addr) { super(addr); }


    //
    // C++:   CvNormalBayesClassifier::CvNormalBayesClassifier()
    //

/**
 * <p>Default and training constructors.</p>
 *
 * <p>The constructors follow conventions of "CvStatModel.CvStatModel". See
 * "CvStatModel.train" for parameters descriptions.</p>
 *
 * @see <a href="http://docs.opencv.org/modules/ml/doc/normal_bayes_classifier.html#cvnormalbayesclassifier-cvnormalbayesclassifier">org.opencv.ml.CvNormalBayesClassifier.CvNormalBayesClassifier</a>
 */
    public   CvNormalBayesClassifier()
    {

        super( CvNormalBayesClassifier_0() );

        return;
    }


    //
    // C++:   CvNormalBayesClassifier::CvNormalBayesClassifier(Mat trainData, Mat responses, Mat varIdx = cv::Mat(), Mat sampleIdx = cv::Mat())
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
 *
 * @see <a href="http://docs.opencv.org/modules/ml/doc/normal_bayes_classifier.html#cvnormalbayesclassifier-cvnormalbayesclassifier">org.opencv.ml.CvNormalBayesClassifier.CvNormalBayesClassifier</a>
 */
    public   CvNormalBayesClassifier(Mat trainData, Mat responses, Mat varIdx, Mat sampleIdx)
    {

        super( CvNormalBayesClassifier_1(trainData.nativeObj, responses.nativeObj, varIdx.nativeObj, sampleIdx.nativeObj) );

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
 * @see <a href="http://docs.opencv.org/modules/ml/doc/normal_bayes_classifier.html#cvnormalbayesclassifier-cvnormalbayesclassifier">org.opencv.ml.CvNormalBayesClassifier.CvNormalBayesClassifier</a>
 */
    public   CvNormalBayesClassifier(Mat trainData, Mat responses)
    {

        super( CvNormalBayesClassifier_2(trainData.nativeObj, responses.nativeObj) );

        return;
    }


    //
    // C++:  void CvNormalBayesClassifier::clear()
    //

    public  void clear()
    {

        clear_0(nativeObj);

        return;
    }


    //
    // C++:  float CvNormalBayesClassifier::predict(Mat samples, Mat* results = 0)
    //

/**
 * <p>Predicts the response for sample(s).</p>
 *
 * <p>The method estimates the most probable classes for input vectors. Input
 * vectors (one or more) are stored as rows of the matrix <code>samples</code>.
 * In case of multiple input vectors, there should be one output vector
 * <code>results</code>. The predicted class for a single input vector is
 * returned by the method.</p>
 *
 * <p>The function is parallelized with the TBB library.</p>
 *
 * @param samples a samples
 * @param results a results
 *
 * @see <a href="http://docs.opencv.org/modules/ml/doc/normal_bayes_classifier.html#cvnormalbayesclassifier-predict">org.opencv.ml.CvNormalBayesClassifier.predict</a>
 */
    public  float predict(Mat samples, Mat results)
    {

        float retVal = predict_0(nativeObj, samples.nativeObj, results.nativeObj);

        return retVal;
    }

/**
 * <p>Predicts the response for sample(s).</p>
 *
 * <p>The method estimates the most probable classes for input vectors. Input
 * vectors (one or more) are stored as rows of the matrix <code>samples</code>.
 * In case of multiple input vectors, there should be one output vector
 * <code>results</code>. The predicted class for a single input vector is
 * returned by the method.</p>
 *
 * <p>The function is parallelized with the TBB library.</p>
 *
 * @param samples a samples
 *
 * @see <a href="http://docs.opencv.org/modules/ml/doc/normal_bayes_classifier.html#cvnormalbayesclassifier-predict">org.opencv.ml.CvNormalBayesClassifier.predict</a>
 */
    public  float predict(Mat samples)
    {

        float retVal = predict_1(nativeObj, samples.nativeObj);

        return retVal;
    }


    //
    // C++:  bool CvNormalBayesClassifier::train(Mat trainData, Mat responses, Mat varIdx = cv::Mat(), Mat sampleIdx = cv::Mat(), bool update = false)
    //

/**
 * <p>Trains the model.</p>
 *
 * <p>The method trains the Normal Bayes classifier. It follows the conventions of
 * the generic "CvStatModel.train" approach with the following limitations:</p>
 * <ul>
 *   <li> Only <code>CV_ROW_SAMPLE</code> data layout is supported.
 *   <li> Input variables are all ordered.
 *   <li> Output variable is categorical, which means that elements of
 * <code>responses</code> must be integer numbers, though the vector may have
 * the <code>CV_32FC1</code> type.
 *   <li> Missing measurements are not supported.
 * </ul>
 *
 * @param trainData a trainData
 * @param responses a responses
 * @param varIdx a varIdx
 * @param sampleIdx a sampleIdx
 * @param update Identifies whether the model should be trained from scratch
 * (<code>update=false</code>) or should be updated using the new training data
 * (<code>update=true</code>).
 *
 * @see <a href="http://docs.opencv.org/modules/ml/doc/normal_bayes_classifier.html#cvnormalbayesclassifier-train">org.opencv.ml.CvNormalBayesClassifier.train</a>
 */
    public  boolean train(Mat trainData, Mat responses, Mat varIdx, Mat sampleIdx, boolean update)
    {

        boolean retVal = train_0(nativeObj, trainData.nativeObj, responses.nativeObj, varIdx.nativeObj, sampleIdx.nativeObj, update);

        return retVal;
    }

/**
 * <p>Trains the model.</p>
 *
 * <p>The method trains the Normal Bayes classifier. It follows the conventions of
 * the generic "CvStatModel.train" approach with the following limitations:</p>
 * <ul>
 *   <li> Only <code>CV_ROW_SAMPLE</code> data layout is supported.
 *   <li> Input variables are all ordered.
 *   <li> Output variable is categorical, which means that elements of
 * <code>responses</code> must be integer numbers, though the vector may have
 * the <code>CV_32FC1</code> type.
 *   <li> Missing measurements are not supported.
 * </ul>
 *
 * @param trainData a trainData
 * @param responses a responses
 *
 * @see <a href="http://docs.opencv.org/modules/ml/doc/normal_bayes_classifier.html#cvnormalbayesclassifier-train">org.opencv.ml.CvNormalBayesClassifier.train</a>
 */
    public  boolean train(Mat trainData, Mat responses)
    {

        boolean retVal = train_1(nativeObj, trainData.nativeObj, responses.nativeObj);

        return retVal;
    }


    @Override
    protected void finalize() throws Throwable {
        delete(nativeObj);
    }



    // C++:   CvNormalBayesClassifier::CvNormalBayesClassifier()
    private static native long CvNormalBayesClassifier_0();

    // C++:   CvNormalBayesClassifier::CvNormalBayesClassifier(Mat trainData, Mat responses, Mat varIdx = cv::Mat(), Mat sampleIdx = cv::Mat())
    private static native long CvNormalBayesClassifier_1(long trainData_nativeObj, long responses_nativeObj, long varIdx_nativeObj, long sampleIdx_nativeObj);
    private static native long CvNormalBayesClassifier_2(long trainData_nativeObj, long responses_nativeObj);

    // C++:  void CvNormalBayesClassifier::clear()
    private static native void clear_0(long nativeObj);

    // C++:  float CvNormalBayesClassifier::predict(Mat samples, Mat* results = 0)
    private static native float predict_0(long nativeObj, long samples_nativeObj, long results_nativeObj);
    private static native float predict_1(long nativeObj, long samples_nativeObj);

    // C++:  bool CvNormalBayesClassifier::train(Mat trainData, Mat responses, Mat varIdx = cv::Mat(), Mat sampleIdx = cv::Mat(), bool update = false)
    private static native boolean train_0(long nativeObj, long trainData_nativeObj, long responses_nativeObj, long varIdx_nativeObj, long sampleIdx_nativeObj, boolean update);
    private static native boolean train_1(long nativeObj, long trainData_nativeObj, long responses_nativeObj);

    // native support for java finalize()
    private static native void delete(long nativeObj);

}
