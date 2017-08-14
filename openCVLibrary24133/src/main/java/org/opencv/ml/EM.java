
//
// This file is auto-generated. Please don't modify it!
//
package org.opencv.ml;

import org.opencv.core.Algorithm;
import org.opencv.core.Mat;
import org.opencv.core.TermCriteria;

// C++: class EM
/**
 * <p>The class implements the EM algorithm as described in the beginning of this
 * section. It is inherited from "Algorithm".</p>
 *
 * @see <a href="http://docs.opencv.org/modules/ml/doc/expectation_maximization.html#em">org.opencv.ml.EM : public Algorithm</a>
 */
public class EM extends Algorithm {

    protected EM(long addr) { super(addr); }


    public static final int
            COV_MAT_SPHERICAL = 0,
            COV_MAT_DIAGONAL = 1,
            COV_MAT_GENERIC = 2,
            COV_MAT_DEFAULT = COV_MAT_DIAGONAL,
            DEFAULT_NCLUSTERS = 5,
            DEFAULT_MAX_ITERS = 100,
            START_E_STEP = 1,
            START_M_STEP = 2,
            START_AUTO_STEP = 0;


    //
    // C++:   EM::EM(int nclusters = EM::DEFAULT_NCLUSTERS, int covMatType = EM::COV_MAT_DIAGONAL, TermCriteria termCrit = TermCriteria(TermCriteria::COUNT+TermCriteria::EPS, EM::DEFAULT_MAX_ITERS, FLT_EPSILON))
    //

/**
 * <p>The constructor of the class</p>
 *
 * @param nclusters The number of mixture components in the Gaussian mixture
 * model. Default value of the parameter is <code>EM.DEFAULT_NCLUSTERS=5</code>.
 * Some of EM implementation could determine the optimal number of mixtures
 * within a specified value range, but that is not the case in ML yet.
 * @param covMatType Constraint on covariance matrices which defines type of
 * matrices. Possible values are:
 * <ul>
 *   <li> EM.COV_MAT_SPHERICAL A scaled identity matrix <em>mu_k * I</em>.
 * There is the only parameter <em>mu_k</em> to be estimated for each matrix.
 * The option may be used in special cases, when the constraint is relevant, or
 * as a first step in the optimization (for example in case when the data is
 * preprocessed with PCA). The results of such preliminary estimation may be
 * passed again to the optimization procedure, this time with <code>covMatType=EM.COV_MAT_DIAGONAL</code>.
 *   <li> EM.COV_MAT_DIAGONAL A diagonal matrix with positive diagonal
 * elements. The number of free parameters is <code>d</code> for each matrix.
 * This is most commonly used option yielding good estimation results.
 *   <li> EM.COV_MAT_GENERIC A symmetric positively defined matrix. The number
 * of free parameters in each matrix is about <em>d^2/2</em>. It is not
 * recommended to use this option, unless there is pretty accurate initial
 * estimation of the parameters and/or a huge number of training samples.
 * </ul>
 * @param termCrit The termination criteria of the EM algorithm. The EM
 * algorithm can be terminated by the number of iterations <code>termCrit.maxCount</code>
 * (number of M-steps) or when relative change of likelihood logarithm is less
 * than <code>termCrit.epsilon</code>. Default maximum number of iterations is
 * <code>EM.DEFAULT_MAX_ITERS=100</code>.
 *
 * @see <a href="http://docs.opencv.org/modules/ml/doc/expectation_maximization.html#em-em">org.opencv.ml.EM.EM</a>
 */
    public   EM(int nclusters, int covMatType, TermCriteria termCrit)
    {

        super( EM_0(nclusters, covMatType, termCrit.type, termCrit.maxCount, termCrit.epsilon) );

        return;
    }

/**
 * <p>The constructor of the class</p>
 *
 * @see <a href="http://docs.opencv.org/modules/ml/doc/expectation_maximization.html#em-em">org.opencv.ml.EM.EM</a>
 */
    public   EM()
    {

        super( EM_1() );

        return;
    }


    //
    // C++:  void EM::clear()
    //

    public  void clear()
    {

        clear_0(nativeObj);

        return;
    }


    //
    // C++:  bool EM::isTrained()
    //

    public  boolean isTrained()
    {

        boolean retVal = isTrained_0(nativeObj);

        return retVal;
    }


    //
    // C++:  Vec2d EM::predict(Mat sample, Mat& probs = Mat())
    //

/**
 * <p>Returns a likelihood logarithm value and an index of the most probable
 * mixture component for the given sample.</p>
 *
 * <p>The method returns a two-element <code>double</code> vector. Zero element is
 * a likelihood logarithm value for the sample. First element is an index of the
 * most probable mixture component for the given sample.</p>
 *
 * @param sample A sample for classification. It should be a one-channel matrix
 * of <em>1 x dims</em> or <em>dims x 1</em> size.
 * @param probs Optional output matrix that contains posterior probabilities of
 * each component given the sample. It has <em>1 x nclusters</em> size and
 * <code>CV_64FC1</code> type.
 *
 * @see <a href="http://docs.opencv.org/modules/ml/doc/expectation_maximization.html#em-predict">org.opencv.ml.EM.predict</a>
 */
    public  double[] predict(Mat sample, Mat probs)
    {

        double[] retVal = predict_0(nativeObj, sample.nativeObj, probs.nativeObj);

        return retVal;
    }

/**
 * <p>Returns a likelihood logarithm value and an index of the most probable
 * mixture component for the given sample.</p>
 *
 * <p>The method returns a two-element <code>double</code> vector. Zero element is
 * a likelihood logarithm value for the sample. First element is an index of the
 * most probable mixture component for the given sample.</p>
 *
 * @param sample A sample for classification. It should be a one-channel matrix
 * of <em>1 x dims</em> or <em>dims x 1</em> size.
 *
 * @see <a href="http://docs.opencv.org/modules/ml/doc/expectation_maximization.html#em-predict">org.opencv.ml.EM.predict</a>
 */
    public  double[] predict(Mat sample)
    {

        double[] retVal = predict_1(nativeObj, sample.nativeObj);

        return retVal;
    }


    //
    // C++:  bool EM::train(Mat samples, Mat& logLikelihoods = Mat(), Mat& labels = Mat(), Mat& probs = Mat())
    //

/**
 * <p>Estimates the Gaussian mixture parameters from a samples set.</p>
 *
 * <p>Three versions of training method differ in the initialization of Gaussian
 * mixture model parameters and start step:</p>
 * <ul>
 *   <li> train - Starts with Expectation step. Initial values of the model
 * parameters will be estimated by the k-means algorithm.
 *   <li> trainE - Starts with Expectation step. You need to provide initial
 * means <em>a_k</em> of mixture components. Optionally you can pass initial
 * weights <em>pi_k</em> and covariance matrices <em>S_k</em> of mixture
 * components.
 *   <li> trainM - Starts with Maximization step. You need to provide initial
 * probabilities <em>p_(i,k)</em> to use this option.
 * </ul>
 *
 * <p>The methods return <code>true</code> if the Gaussian mixture model was
 * trained successfully, otherwise it returns <code>false</code>.</p>
 *
 * <p>Unlike many of the ML models, EM is an unsupervised learning algorithm and it
 * does not take responses (class labels or function values) as input. Instead,
 * it computes the *Maximum Likelihood Estimate* of the Gaussian mixture
 * parameters from an input sample set, stores all the parameters inside the
 * structure: <em>p_(i,k)</em> in <code>probs</code>, <em>a_k</em> in
 * <code>means</code>, <em>S_k</em> in <code>covs[k]</code>, <em>pi_k</em> in
 * <code>weights</code>, and optionally computes the output "class label" for
 * each sample: <em>labels_i=arg max_k(p_(i,k)), i=1..N</em> (indices of the
 * most probable mixture component for each sample).</p>
 *
 * <p>The trained model can be used further for prediction, just like any other
 * classifier. The trained model is similar to the "CvNormalBayesClassifier".</p>
 *
 * @param samples Samples from which the Gaussian mixture model will be
 * estimated. It should be a one-channel matrix, each row of which is a sample.
 * If the matrix does not have <code>CV_64F</code> type it will be converted to
 * the inner matrix of such type for the further computing.
 * @param logLikelihoods The optional output matrix that contains a likelihood
 * logarithm value for each sample. It has <em>nsamples x 1</em> size and
 * <code>CV_64FC1</code> type.
 * @param labels The optional output "class label" for each sample:
 * <em>labels_i=arg max_k(p_(i,k)), i=1..N</em> (indices of the most probable
 * mixture component for each sample). It has <em>nsamples x 1</em> size and
 * <code>CV_32SC1</code> type.
 * @param probs The optional output matrix that contains posterior probabilities
 * of each Gaussian mixture component given the each sample. It has <em>nsamples
 * x nclusters</em> size and <code>CV_64FC1</code> type.
 *
 * @see <a href="http://docs.opencv.org/modules/ml/doc/expectation_maximization.html#em-train">org.opencv.ml.EM.train</a>
 */
    public  boolean train(Mat samples, Mat logLikelihoods, Mat labels, Mat probs)
    {

        boolean retVal = train_0(nativeObj, samples.nativeObj, logLikelihoods.nativeObj, labels.nativeObj, probs.nativeObj);

        return retVal;
    }

/**
 * <p>Estimates the Gaussian mixture parameters from a samples set.</p>
 *
 * <p>Three versions of training method differ in the initialization of Gaussian
 * mixture model parameters and start step:</p>
 * <ul>
 *   <li> train - Starts with Expectation step. Initial values of the model
 * parameters will be estimated by the k-means algorithm.
 *   <li> trainE - Starts with Expectation step. You need to provide initial
 * means <em>a_k</em> of mixture components. Optionally you can pass initial
 * weights <em>pi_k</em> and covariance matrices <em>S_k</em> of mixture
 * components.
 *   <li> trainM - Starts with Maximization step. You need to provide initial
 * probabilities <em>p_(i,k)</em> to use this option.
 * </ul>
 *
 * <p>The methods return <code>true</code> if the Gaussian mixture model was
 * trained successfully, otherwise it returns <code>false</code>.</p>
 *
 * <p>Unlike many of the ML models, EM is an unsupervised learning algorithm and it
 * does not take responses (class labels or function values) as input. Instead,
 * it computes the *Maximum Likelihood Estimate* of the Gaussian mixture
 * parameters from an input sample set, stores all the parameters inside the
 * structure: <em>p_(i,k)</em> in <code>probs</code>, <em>a_k</em> in
 * <code>means</code>, <em>S_k</em> in <code>covs[k]</code>, <em>pi_k</em> in
 * <code>weights</code>, and optionally computes the output "class label" for
 * each sample: <em>labels_i=arg max_k(p_(i,k)), i=1..N</em> (indices of the
 * most probable mixture component for each sample).</p>
 *
 * <p>The trained model can be used further for prediction, just like any other
 * classifier. The trained model is similar to the "CvNormalBayesClassifier".</p>
 *
 * @param samples Samples from which the Gaussian mixture model will be
 * estimated. It should be a one-channel matrix, each row of which is a sample.
 * If the matrix does not have <code>CV_64F</code> type it will be converted to
 * the inner matrix of such type for the further computing.
 *
 * @see <a href="http://docs.opencv.org/modules/ml/doc/expectation_maximization.html#em-train">org.opencv.ml.EM.train</a>
 */
    public  boolean train(Mat samples)
    {

        boolean retVal = train_1(nativeObj, samples.nativeObj);

        return retVal;
    }


    //
    // C++:  bool EM::trainE(Mat samples, Mat means0, Mat covs0 = Mat(), Mat weights0 = Mat(), Mat& logLikelihoods = Mat(), Mat& labels = Mat(), Mat& probs = Mat())
    //

    public  boolean trainE(Mat samples, Mat means0, Mat covs0, Mat weights0, Mat logLikelihoods, Mat labels, Mat probs)
    {

        boolean retVal = trainE_0(nativeObj, samples.nativeObj, means0.nativeObj, covs0.nativeObj, weights0.nativeObj, logLikelihoods.nativeObj, labels.nativeObj, probs.nativeObj);

        return retVal;
    }

    public  boolean trainE(Mat samples, Mat means0)
    {

        boolean retVal = trainE_1(nativeObj, samples.nativeObj, means0.nativeObj);

        return retVal;
    }


    //
    // C++:  bool EM::trainM(Mat samples, Mat probs0, Mat& logLikelihoods = Mat(), Mat& labels = Mat(), Mat& probs = Mat())
    //

    public  boolean trainM(Mat samples, Mat probs0, Mat logLikelihoods, Mat labels, Mat probs)
    {

        boolean retVal = trainM_0(nativeObj, samples.nativeObj, probs0.nativeObj, logLikelihoods.nativeObj, labels.nativeObj, probs.nativeObj);

        return retVal;
    }

    public  boolean trainM(Mat samples, Mat probs0)
    {

        boolean retVal = trainM_1(nativeObj, samples.nativeObj, probs0.nativeObj);

        return retVal;
    }


    @Override
    protected void finalize() throws Throwable {
        delete(nativeObj);
    }



    // C++:   EM::EM(int nclusters = EM::DEFAULT_NCLUSTERS, int covMatType = EM::COV_MAT_DIAGONAL, TermCriteria termCrit = TermCriteria(TermCriteria::COUNT+TermCriteria::EPS, EM::DEFAULT_MAX_ITERS, FLT_EPSILON))
    private static native long EM_0(int nclusters, int covMatType, int termCrit_type, int termCrit_maxCount, double termCrit_epsilon);
    private static native long EM_1();

    // C++:  void EM::clear()
    private static native void clear_0(long nativeObj);

    // C++:  bool EM::isTrained()
    private static native boolean isTrained_0(long nativeObj);

    // C++:  Vec2d EM::predict(Mat sample, Mat& probs = Mat())
    private static native double[] predict_0(long nativeObj, long sample_nativeObj, long probs_nativeObj);
    private static native double[] predict_1(long nativeObj, long sample_nativeObj);

    // C++:  bool EM::train(Mat samples, Mat& logLikelihoods = Mat(), Mat& labels = Mat(), Mat& probs = Mat())
    private static native boolean train_0(long nativeObj, long samples_nativeObj, long logLikelihoods_nativeObj, long labels_nativeObj, long probs_nativeObj);
    private static native boolean train_1(long nativeObj, long samples_nativeObj);

    // C++:  bool EM::trainE(Mat samples, Mat means0, Mat covs0 = Mat(), Mat weights0 = Mat(), Mat& logLikelihoods = Mat(), Mat& labels = Mat(), Mat& probs = Mat())
    private static native boolean trainE_0(long nativeObj, long samples_nativeObj, long means0_nativeObj, long covs0_nativeObj, long weights0_nativeObj, long logLikelihoods_nativeObj, long labels_nativeObj, long probs_nativeObj);
    private static native boolean trainE_1(long nativeObj, long samples_nativeObj, long means0_nativeObj);

    // C++:  bool EM::trainM(Mat samples, Mat probs0, Mat& logLikelihoods = Mat(), Mat& labels = Mat(), Mat& probs = Mat())
    private static native boolean trainM_0(long nativeObj, long samples_nativeObj, long probs0_nativeObj, long logLikelihoods_nativeObj, long labels_nativeObj, long probs_nativeObj);
    private static native boolean trainM_1(long nativeObj, long samples_nativeObj, long probs0_nativeObj);

    // native support for java finalize()
    private static native void delete(long nativeObj);

}
