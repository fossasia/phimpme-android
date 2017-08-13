
//
// This file is auto-generated. Please don't modify it!
//
package org.opencv.ml;

import org.opencv.core.Mat;

// C++: class CvERTrees
/**
 * <p>The class implements the Extremely randomized trees algorithm.
 * <code>CvERTrees</code> is inherited from "CvRTrees" and has the same
 * interface, so see description of "CvRTrees" class to get details. To set the
 * training parameters of Extremely randomized trees the same class "CvRTParams"
 * is used.</p>
 *
 * @see <a href="http://docs.opencv.org/modules/ml/doc/ertrees.html#cvertrees">org.opencv.ml.CvERTrees : public CvRTrees</a>
 */
public class CvERTrees extends CvRTrees {

    protected CvERTrees(long addr) { super(addr); }


    //
    // C++:   CvERTrees::CvERTrees()
    //

    public   CvERTrees()
    {

        super( CvERTrees_0() );

        return;
    }


    //
    // C++:  bool CvERTrees::train(Mat trainData, int tflag, Mat responses, Mat varIdx = cv::Mat(), Mat sampleIdx = cv::Mat(), Mat varType = cv::Mat(), Mat missingDataMask = cv::Mat(), CvRTParams params = CvRTParams())
    //

    public  boolean train(Mat trainData, int tflag, Mat responses, Mat varIdx, Mat sampleIdx, Mat varType, Mat missingDataMask, CvRTParams params)
    {

        boolean retVal = train_0(nativeObj, trainData.nativeObj, tflag, responses.nativeObj, varIdx.nativeObj, sampleIdx.nativeObj, varType.nativeObj, missingDataMask.nativeObj, params.nativeObj);

        return retVal;
    }

    public  boolean train(Mat trainData, int tflag, Mat responses)
    {

        boolean retVal = train_1(nativeObj, trainData.nativeObj, tflag, responses.nativeObj);

        return retVal;
    }


    @Override
    protected void finalize() throws Throwable {
        delete(nativeObj);
    }



    // C++:   CvERTrees::CvERTrees()
    private static native long CvERTrees_0();

    // C++:  bool CvERTrees::train(Mat trainData, int tflag, Mat responses, Mat varIdx = cv::Mat(), Mat sampleIdx = cv::Mat(), Mat varType = cv::Mat(), Mat missingDataMask = cv::Mat(), CvRTParams params = CvRTParams())
    private static native boolean train_0(long nativeObj, long trainData_nativeObj, int tflag, long responses_nativeObj, long varIdx_nativeObj, long sampleIdx_nativeObj, long varType_nativeObj, long missingDataMask_nativeObj, long params_nativeObj);
    private static native boolean train_1(long nativeObj, long trainData_nativeObj, int tflag, long responses_nativeObj);

    // native support for java finalize()
    private static native void delete(long nativeObj);

}
