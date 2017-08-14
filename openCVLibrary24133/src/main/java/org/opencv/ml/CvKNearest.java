
//
// This file is auto-generated. Please don't modify it!
//
package org.opencv.ml;

import org.opencv.core.Mat;

// C++: class CvKNearest
/**
 * <p>The class implements K-Nearest Neighbors model as described in the beginning
 * of this section.</p>
 *
 * <p>Note:</p>
 * <ul>
 *   <li> (Python) An example of digit recognition using KNearest can be found
 * at opencv_source/samples/python2/digits.py
 *   <li> (Python) An example of grid search digit recognition using KNearest
 * can be found at opencv_source/samples/python2/digits_adjust.py
 *   <li> (Python) An example of video digit recognition using KNearest can be
 * found at opencv_source/samples/python2/digits_video.py
 * </ul>
 *
 * @see <a href="http://docs.opencv.org/modules/ml/doc/k_nearest_neighbors.html#cvknearest">org.opencv.ml.CvKNearest : public CvStatModel</a>
 */
public class CvKNearest extends CvStatModel {

    protected CvKNearest(long addr) { super(addr); }


    //
    // C++:   CvKNearest::CvKNearest()
    //

/**
 * <p>Default and training constructors.</p>
 *
 * <p>See "CvKNearest.train" for additional parameters descriptions.</p>
 *
 * @see <a href="http://docs.opencv.org/modules/ml/doc/k_nearest_neighbors.html#cvknearest-cvknearest">org.opencv.ml.CvKNearest.CvKNearest</a>
 */
    public   CvKNearest()
    {

        super( CvKNearest_0() );

        return;
    }


    //
    // C++:   CvKNearest::CvKNearest(Mat trainData, Mat responses, Mat sampleIdx = cv::Mat(), bool isRegression = false, int max_k = 32)
    //

/**
 * <p>Default and training constructors.</p>
 *
 * <p>See "CvKNearest.train" for additional parameters descriptions.</p>
 *
 * @param trainData a trainData
 * @param responses a responses
 * @param sampleIdx a sampleIdx
 * @param isRegression a isRegression
 * @param max_k a max_k
 *
 * @see <a href="http://docs.opencv.org/modules/ml/doc/k_nearest_neighbors.html#cvknearest-cvknearest">org.opencv.ml.CvKNearest.CvKNearest</a>
 */
    public   CvKNearest(Mat trainData, Mat responses, Mat sampleIdx, boolean isRegression, int max_k)
    {

        super( CvKNearest_1(trainData.nativeObj, responses.nativeObj, sampleIdx.nativeObj, isRegression, max_k) );

        return;
    }

/**
 * <p>Default and training constructors.</p>
 *
 * <p>See "CvKNearest.train" for additional parameters descriptions.</p>
 *
 * @param trainData a trainData
 * @param responses a responses
 *
 * @see <a href="http://docs.opencv.org/modules/ml/doc/k_nearest_neighbors.html#cvknearest-cvknearest">org.opencv.ml.CvKNearest.CvKNearest</a>
 */
    public   CvKNearest(Mat trainData, Mat responses)
    {

        super( CvKNearest_2(trainData.nativeObj, responses.nativeObj) );

        return;
    }


    //
    // C++:  float CvKNearest::find_nearest(Mat samples, int k, Mat& results, Mat& neighborResponses, Mat& dists)
    //

/**
 * <p>Finds the neighbors and predicts responses for input vectors.</p>
 *
 * <p>For each input vector (a row of the matrix <code>samples</code>), the method
 * finds the <code>k</code> nearest neighbors. In case of regression, the
 * predicted result is a mean value of the particular vector's neighbor
 * responses. In case of classification, the class is determined by voting.</p>
 *
 * <p>For each input vector, the neighbors are sorted by their distances to the
 * vector.</p>
 *
 * <p>In case of C++ interface you can use output pointers to empty matrices and
 * the function will allocate memory itself.</p>
 *
 * <p>If only a single input vector is passed, all output matrices are optional and
 * the predicted value is returned by the method.</p>
 *
 * <p>The function is parallelized with the TBB library.</p>
 *
 * @param samples Input samples stored by rows. It is a single-precision
 * floating-point matrix of <em>number_of_samples x number_of_features</em>
 * size.
 * @param k Number of used nearest neighbors. It must satisfy constraint: <em>k
 * <= </em> "CvKNearest.get_max_k".
 * @param results Vector with results of prediction (regression or
 * classification) for each input sample. It is a single-precision
 * floating-point vector with <code>number_of_samples</code> elements.
 * @param neighborResponses Optional output values for corresponding
 * <code>neighbors</code>. It is a single-precision floating-point matrix of
 * <em>number_of_samples x k</em> size.
 * @param dists a dists
 *
 * @see <a href="http://docs.opencv.org/modules/ml/doc/k_nearest_neighbors.html#cvknearest-find-nearest">org.opencv.ml.CvKNearest.find_nearest</a>
 */
    public  float find_nearest(Mat samples, int k, Mat results, Mat neighborResponses, Mat dists)
    {

        float retVal = find_nearest_0(nativeObj, samples.nativeObj, k, results.nativeObj, neighborResponses.nativeObj, dists.nativeObj);

        return retVal;
    }


    //
    // C++:  bool CvKNearest::train(Mat trainData, Mat responses, Mat sampleIdx = cv::Mat(), bool isRegression = false, int maxK = 32, bool updateBase = false)
    //

/**
 * <p>Trains the model.</p>
 *
 * <p>The method trains the K-Nearest model. It follows the conventions of the
 * generic "CvStatModel.train" approach with the following limitations:</p>
 * <ul>
 *   <li> Only <code>CV_ROW_SAMPLE</code> data layout is supported.
 *   <li> Input variables are all ordered.
 *   <li> Output variables can be either categorical (<code>is_regression=false</code>)
 * or ordered (<code>is_regression=true</code>).
 *   <li> Variable subsets (<code>var_idx</code>) and missing measurements are
 * not supported.
 * </ul>
 *
 * @param trainData a trainData
 * @param responses a responses
 * @param sampleIdx a sampleIdx
 * @param isRegression Type of the problem: <code>true</code> for regression and
 * <code>false</code> for classification.
 * @param maxK Number of maximum neighbors that may be passed to the method
 * "CvKNearest.find_nearest".
 * @param updateBase Specifies whether the model is trained from scratch
 * (<code>update_base=false</code>), or it is updated using the new training
 * data (<code>update_base=true</code>). In the latter case, the parameter
 * <code>maxK</code> must not be larger than the original value.
 *
 * @see <a href="http://docs.opencv.org/modules/ml/doc/k_nearest_neighbors.html#cvknearest-train">org.opencv.ml.CvKNearest.train</a>
 */
    public  boolean train(Mat trainData, Mat responses, Mat sampleIdx, boolean isRegression, int maxK, boolean updateBase)
    {

        boolean retVal = train_0(nativeObj, trainData.nativeObj, responses.nativeObj, sampleIdx.nativeObj, isRegression, maxK, updateBase);

        return retVal;
    }

/**
 * <p>Trains the model.</p>
 *
 * <p>The method trains the K-Nearest model. It follows the conventions of the
 * generic "CvStatModel.train" approach with the following limitations:</p>
 * <ul>
 *   <li> Only <code>CV_ROW_SAMPLE</code> data layout is supported.
 *   <li> Input variables are all ordered.
 *   <li> Output variables can be either categorical (<code>is_regression=false</code>)
 * or ordered (<code>is_regression=true</code>).
 *   <li> Variable subsets (<code>var_idx</code>) and missing measurements are
 * not supported.
 * </ul>
 *
 * @param trainData a trainData
 * @param responses a responses
 *
 * @see <a href="http://docs.opencv.org/modules/ml/doc/k_nearest_neighbors.html#cvknearest-train">org.opencv.ml.CvKNearest.train</a>
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



    // C++:   CvKNearest::CvKNearest()
    private static native long CvKNearest_0();

    // C++:   CvKNearest::CvKNearest(Mat trainData, Mat responses, Mat sampleIdx = cv::Mat(), bool isRegression = false, int max_k = 32)
    private static native long CvKNearest_1(long trainData_nativeObj, long responses_nativeObj, long sampleIdx_nativeObj, boolean isRegression, int max_k);
    private static native long CvKNearest_2(long trainData_nativeObj, long responses_nativeObj);

    // C++:  float CvKNearest::find_nearest(Mat samples, int k, Mat& results, Mat& neighborResponses, Mat& dists)
    private static native float find_nearest_0(long nativeObj, long samples_nativeObj, int k, long results_nativeObj, long neighborResponses_nativeObj, long dists_nativeObj);

    // C++:  bool CvKNearest::train(Mat trainData, Mat responses, Mat sampleIdx = cv::Mat(), bool isRegression = false, int maxK = 32, bool updateBase = false)
    private static native boolean train_0(long nativeObj, long trainData_nativeObj, long responses_nativeObj, long sampleIdx_nativeObj, boolean isRegression, int maxK, boolean updateBase);
    private static native boolean train_1(long nativeObj, long trainData_nativeObj, long responses_nativeObj);

    // native support for java finalize()
    private static native void delete(long nativeObj);

}
