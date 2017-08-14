
//
// This file is auto-generated. Please don't modify it!
//
package org.opencv.objdetect;

import java.lang.String;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDouble;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfRect;
import org.opencv.core.Size;

// C++: class HOGDescriptor
public class HOGDescriptor {

    protected final long nativeObj;
    protected HOGDescriptor(long addr) { nativeObj = addr; }


    public static final int
            L2Hys = 0,
            DEFAULT_NLEVELS = 64;


    //
    // C++:   HOGDescriptor::HOGDescriptor()
    //

    public   HOGDescriptor()
    {

        nativeObj = HOGDescriptor_0();

        return;
    }


    //
    // C++:   HOGDescriptor::HOGDescriptor(Size _winSize, Size _blockSize, Size _blockStride, Size _cellSize, int _nbins, int _derivAperture = 1, double _winSigma = -1, int _histogramNormType = HOGDescriptor::L2Hys, double _L2HysThreshold = 0.2, bool _gammaCorrection = false, int _nlevels = HOGDescriptor::DEFAULT_NLEVELS)
    //

    public   HOGDescriptor(Size _winSize, Size _blockSize, Size _blockStride, Size _cellSize, int _nbins, int _derivAperture, double _winSigma, int _histogramNormType, double _L2HysThreshold, boolean _gammaCorrection, int _nlevels)
    {

        nativeObj = HOGDescriptor_1(_winSize.width, _winSize.height, _blockSize.width, _blockSize.height, _blockStride.width, _blockStride.height, _cellSize.width, _cellSize.height, _nbins, _derivAperture, _winSigma, _histogramNormType, _L2HysThreshold, _gammaCorrection, _nlevels);

        return;
    }

    public   HOGDescriptor(Size _winSize, Size _blockSize, Size _blockStride, Size _cellSize, int _nbins)
    {

        nativeObj = HOGDescriptor_2(_winSize.width, _winSize.height, _blockSize.width, _blockSize.height, _blockStride.width, _blockStride.height, _cellSize.width, _cellSize.height, _nbins);

        return;
    }


    //
    // C++:   HOGDescriptor::HOGDescriptor(String filename)
    //

    public   HOGDescriptor(String filename)
    {

        nativeObj = HOGDescriptor_3(filename);

        return;
    }


    //
    // C++:  bool HOGDescriptor::checkDetectorSize()
    //

    public  boolean checkDetectorSize()
    {

        boolean retVal = checkDetectorSize_0(nativeObj);

        return retVal;
    }


    //
    // C++:  void HOGDescriptor::compute(Mat img, vector_float& descriptors, Size winStride = Size(), Size padding = Size(), vector_Point locations = vector<Point>())
    //

    public  void compute(Mat img, MatOfFloat descriptors, Size winStride, Size padding, MatOfPoint locations)
    {
        Mat descriptors_mat = descriptors;
        Mat locations_mat = locations;
        compute_0(nativeObj, img.nativeObj, descriptors_mat.nativeObj, winStride.width, winStride.height, padding.width, padding.height, locations_mat.nativeObj);

        return;
    }

    public  void compute(Mat img, MatOfFloat descriptors)
    {
        Mat descriptors_mat = descriptors;
        compute_1(nativeObj, img.nativeObj, descriptors_mat.nativeObj);

        return;
    }


    //
    // C++:  void HOGDescriptor::computeGradient(Mat img, Mat& grad, Mat& angleOfs, Size paddingTL = Size(), Size paddingBR = Size())
    //

    public  void computeGradient(Mat img, Mat grad, Mat angleOfs, Size paddingTL, Size paddingBR)
    {

        computeGradient_0(nativeObj, img.nativeObj, grad.nativeObj, angleOfs.nativeObj, paddingTL.width, paddingTL.height, paddingBR.width, paddingBR.height);

        return;
    }

    public  void computeGradient(Mat img, Mat grad, Mat angleOfs)
    {

        computeGradient_1(nativeObj, img.nativeObj, grad.nativeObj, angleOfs.nativeObj);

        return;
    }


    //
    // C++:  void HOGDescriptor::detect(Mat img, vector_Point& foundLocations, vector_double& weights, double hitThreshold = 0, Size winStride = Size(), Size padding = Size(), vector_Point searchLocations = vector<Point>())
    //

    public  void detect(Mat img, MatOfPoint foundLocations, MatOfDouble weights, double hitThreshold, Size winStride, Size padding, MatOfPoint searchLocations)
    {
        Mat foundLocations_mat = foundLocations;
        Mat weights_mat = weights;
        Mat searchLocations_mat = searchLocations;
        detect_0(nativeObj, img.nativeObj, foundLocations_mat.nativeObj, weights_mat.nativeObj, hitThreshold, winStride.width, winStride.height, padding.width, padding.height, searchLocations_mat.nativeObj);

        return;
    }

    public  void detect(Mat img, MatOfPoint foundLocations, MatOfDouble weights)
    {
        Mat foundLocations_mat = foundLocations;
        Mat weights_mat = weights;
        detect_1(nativeObj, img.nativeObj, foundLocations_mat.nativeObj, weights_mat.nativeObj);

        return;
    }


    //
    // C++:  void HOGDescriptor::detectMultiScale(Mat img, vector_Rect& foundLocations, vector_double& foundWeights, double hitThreshold = 0, Size winStride = Size(), Size padding = Size(), double scale = 1.05, double finalThreshold = 2.0, bool useMeanshiftGrouping = false)
    //

    public  void detectMultiScale(Mat img, MatOfRect foundLocations, MatOfDouble foundWeights, double hitThreshold, Size winStride, Size padding, double scale, double finalThreshold, boolean useMeanshiftGrouping)
    {
        Mat foundLocations_mat = foundLocations;
        Mat foundWeights_mat = foundWeights;
        detectMultiScale_0(nativeObj, img.nativeObj, foundLocations_mat.nativeObj, foundWeights_mat.nativeObj, hitThreshold, winStride.width, winStride.height, padding.width, padding.height, scale, finalThreshold, useMeanshiftGrouping);

        return;
    }

    public  void detectMultiScale(Mat img, MatOfRect foundLocations, MatOfDouble foundWeights)
    {
        Mat foundLocations_mat = foundLocations;
        Mat foundWeights_mat = foundWeights;
        detectMultiScale_1(nativeObj, img.nativeObj, foundLocations_mat.nativeObj, foundWeights_mat.nativeObj);

        return;
    }


    //
    // C++: static vector_float HOGDescriptor::getDaimlerPeopleDetector()
    //

    public static MatOfFloat getDaimlerPeopleDetector()
    {

        MatOfFloat retVal = MatOfFloat.fromNativeAddr(getDaimlerPeopleDetector_0());

        return retVal;
    }


    //
    // C++: static vector_float HOGDescriptor::getDefaultPeopleDetector()
    //

    public static MatOfFloat getDefaultPeopleDetector()
    {

        MatOfFloat retVal = MatOfFloat.fromNativeAddr(getDefaultPeopleDetector_0());

        return retVal;
    }


    //
    // C++:  size_t HOGDescriptor::getDescriptorSize()
    //

    public  long getDescriptorSize()
    {

        long retVal = getDescriptorSize_0(nativeObj);

        return retVal;
    }


    //
    // C++:  double HOGDescriptor::getWinSigma()
    //

    public  double getWinSigma()
    {

        double retVal = getWinSigma_0(nativeObj);

        return retVal;
    }


    //
    // C++:  bool HOGDescriptor::load(String filename, String objname = String())
    //

    public  boolean load(String filename, String objname)
    {

        boolean retVal = load_0(nativeObj, filename, objname);

        return retVal;
    }

    public  boolean load(String filename)
    {

        boolean retVal = load_1(nativeObj, filename);

        return retVal;
    }


    //
    // C++:  void HOGDescriptor::save(String filename, String objname = String())
    //

    public  void save(String filename, String objname)
    {

        save_0(nativeObj, filename, objname);

        return;
    }

    public  void save(String filename)
    {

        save_1(nativeObj, filename);

        return;
    }


    //
    // C++:  void HOGDescriptor::setSVMDetector(Mat _svmdetector)
    //

    public  void setSVMDetector(Mat _svmdetector)
    {

        setSVMDetector_0(nativeObj, _svmdetector.nativeObj);

        return;
    }


    //
    // C++: Size HOGDescriptor::winSize
    //

    public  Size get_winSize()
    {

        Size retVal = new Size(get_winSize_0(nativeObj));

        return retVal;
    }


    //
    // C++: Size HOGDescriptor::blockSize
    //

    public  Size get_blockSize()
    {

        Size retVal = new Size(get_blockSize_0(nativeObj));

        return retVal;
    }


    //
    // C++: Size HOGDescriptor::blockStride
    //

    public  Size get_blockStride()
    {

        Size retVal = new Size(get_blockStride_0(nativeObj));

        return retVal;
    }


    //
    // C++: Size HOGDescriptor::cellSize
    //

    public  Size get_cellSize()
    {

        Size retVal = new Size(get_cellSize_0(nativeObj));

        return retVal;
    }


    //
    // C++: int HOGDescriptor::nbins
    //

    public  int get_nbins()
    {

        int retVal = get_nbins_0(nativeObj);

        return retVal;
    }


    //
    // C++: int HOGDescriptor::derivAperture
    //

    public  int get_derivAperture()
    {

        int retVal = get_derivAperture_0(nativeObj);

        return retVal;
    }


    //
    // C++: double HOGDescriptor::winSigma
    //

    public  double get_winSigma()
    {

        double retVal = get_winSigma_0(nativeObj);

        return retVal;
    }


    //
    // C++: int HOGDescriptor::histogramNormType
    //

    public  int get_histogramNormType()
    {

        int retVal = get_histogramNormType_0(nativeObj);

        return retVal;
    }


    //
    // C++: double HOGDescriptor::L2HysThreshold
    //

    public  double get_L2HysThreshold()
    {

        double retVal = get_L2HysThreshold_0(nativeObj);

        return retVal;
    }


    //
    // C++: bool HOGDescriptor::gammaCorrection
    //

    public  boolean get_gammaCorrection()
    {

        boolean retVal = get_gammaCorrection_0(nativeObj);

        return retVal;
    }


    //
    // C++: vector_float HOGDescriptor::svmDetector
    //

    public  MatOfFloat get_svmDetector()
    {

        MatOfFloat retVal = MatOfFloat.fromNativeAddr(get_svmDetector_0(nativeObj));

        return retVal;
    }


    //
    // C++: int HOGDescriptor::nlevels
    //

    public  int get_nlevels()
    {

        int retVal = get_nlevels_0(nativeObj);

        return retVal;
    }


    @Override
    protected void finalize() throws Throwable {
        delete(nativeObj);
    }



    // C++:   HOGDescriptor::HOGDescriptor()
    private static native long HOGDescriptor_0();

    // C++:   HOGDescriptor::HOGDescriptor(Size _winSize, Size _blockSize, Size _blockStride, Size _cellSize, int _nbins, int _derivAperture = 1, double _winSigma = -1, int _histogramNormType = HOGDescriptor::L2Hys, double _L2HysThreshold = 0.2, bool _gammaCorrection = false, int _nlevels = HOGDescriptor::DEFAULT_NLEVELS)
    private static native long HOGDescriptor_1(double _winSize_width, double _winSize_height, double _blockSize_width, double _blockSize_height, double _blockStride_width, double _blockStride_height, double _cellSize_width, double _cellSize_height, int _nbins, int _derivAperture, double _winSigma, int _histogramNormType, double _L2HysThreshold, boolean _gammaCorrection, int _nlevels);
    private static native long HOGDescriptor_2(double _winSize_width, double _winSize_height, double _blockSize_width, double _blockSize_height, double _blockStride_width, double _blockStride_height, double _cellSize_width, double _cellSize_height, int _nbins);

    // C++:   HOGDescriptor::HOGDescriptor(String filename)
    private static native long HOGDescriptor_3(String filename);

    // C++:  bool HOGDescriptor::checkDetectorSize()
    private static native boolean checkDetectorSize_0(long nativeObj);

    // C++:  void HOGDescriptor::compute(Mat img, vector_float& descriptors, Size winStride = Size(), Size padding = Size(), vector_Point locations = vector<Point>())
    private static native void compute_0(long nativeObj, long img_nativeObj, long descriptors_mat_nativeObj, double winStride_width, double winStride_height, double padding_width, double padding_height, long locations_mat_nativeObj);
    private static native void compute_1(long nativeObj, long img_nativeObj, long descriptors_mat_nativeObj);

    // C++:  void HOGDescriptor::computeGradient(Mat img, Mat& grad, Mat& angleOfs, Size paddingTL = Size(), Size paddingBR = Size())
    private static native void computeGradient_0(long nativeObj, long img_nativeObj, long grad_nativeObj, long angleOfs_nativeObj, double paddingTL_width, double paddingTL_height, double paddingBR_width, double paddingBR_height);
    private static native void computeGradient_1(long nativeObj, long img_nativeObj, long grad_nativeObj, long angleOfs_nativeObj);

    // C++:  void HOGDescriptor::detect(Mat img, vector_Point& foundLocations, vector_double& weights, double hitThreshold = 0, Size winStride = Size(), Size padding = Size(), vector_Point searchLocations = vector<Point>())
    private static native void detect_0(long nativeObj, long img_nativeObj, long foundLocations_mat_nativeObj, long weights_mat_nativeObj, double hitThreshold, double winStride_width, double winStride_height, double padding_width, double padding_height, long searchLocations_mat_nativeObj);
    private static native void detect_1(long nativeObj, long img_nativeObj, long foundLocations_mat_nativeObj, long weights_mat_nativeObj);

    // C++:  void HOGDescriptor::detectMultiScale(Mat img, vector_Rect& foundLocations, vector_double& foundWeights, double hitThreshold = 0, Size winStride = Size(), Size padding = Size(), double scale = 1.05, double finalThreshold = 2.0, bool useMeanshiftGrouping = false)
    private static native void detectMultiScale_0(long nativeObj, long img_nativeObj, long foundLocations_mat_nativeObj, long foundWeights_mat_nativeObj, double hitThreshold, double winStride_width, double winStride_height, double padding_width, double padding_height, double scale, double finalThreshold, boolean useMeanshiftGrouping);
    private static native void detectMultiScale_1(long nativeObj, long img_nativeObj, long foundLocations_mat_nativeObj, long foundWeights_mat_nativeObj);

    // C++: static vector_float HOGDescriptor::getDaimlerPeopleDetector()
    private static native long getDaimlerPeopleDetector_0();

    // C++: static vector_float HOGDescriptor::getDefaultPeopleDetector()
    private static native long getDefaultPeopleDetector_0();

    // C++:  size_t HOGDescriptor::getDescriptorSize()
    private static native long getDescriptorSize_0(long nativeObj);

    // C++:  double HOGDescriptor::getWinSigma()
    private static native double getWinSigma_0(long nativeObj);

    // C++:  bool HOGDescriptor::load(String filename, String objname = String())
    private static native boolean load_0(long nativeObj, String filename, String objname);
    private static native boolean load_1(long nativeObj, String filename);

    // C++:  void HOGDescriptor::save(String filename, String objname = String())
    private static native void save_0(long nativeObj, String filename, String objname);
    private static native void save_1(long nativeObj, String filename);

    // C++:  void HOGDescriptor::setSVMDetector(Mat _svmdetector)
    private static native void setSVMDetector_0(long nativeObj, long _svmdetector_nativeObj);

    // C++: Size HOGDescriptor::winSize
    private static native double[] get_winSize_0(long nativeObj);

    // C++: Size HOGDescriptor::blockSize
    private static native double[] get_blockSize_0(long nativeObj);

    // C++: Size HOGDescriptor::blockStride
    private static native double[] get_blockStride_0(long nativeObj);

    // C++: Size HOGDescriptor::cellSize
    private static native double[] get_cellSize_0(long nativeObj);

    // C++: int HOGDescriptor::nbins
    private static native int get_nbins_0(long nativeObj);

    // C++: int HOGDescriptor::derivAperture
    private static native int get_derivAperture_0(long nativeObj);

    // C++: double HOGDescriptor::winSigma
    private static native double get_winSigma_0(long nativeObj);

    // C++: int HOGDescriptor::histogramNormType
    private static native int get_histogramNormType_0(long nativeObj);

    // C++: double HOGDescriptor::L2HysThreshold
    private static native double get_L2HysThreshold_0(long nativeObj);

    // C++: bool HOGDescriptor::gammaCorrection
    private static native boolean get_gammaCorrection_0(long nativeObj);

    // C++: vector_float HOGDescriptor::svmDetector
    private static native long get_svmDetector_0(long nativeObj);

    // C++: int HOGDescriptor::nlevels
    private static native int get_nlevels_0(long nativeObj);

    // native support for java finalize()
    private static native void delete(long nativeObj);

}
