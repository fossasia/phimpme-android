
//
// This file is auto-generated. Please don't modify it!
//
package org.opencv.contrib;

import java.util.List;
import org.opencv.core.Mat;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfPoint;
import org.opencv.utils.Converters;

public class Contrib {

    public static final int
            RETINA_COLOR_RANDOM = 0,
            RETINA_COLOR_DIAGONAL = 1,
            RETINA_COLOR_BAYER = 2,
            ROTATION = 1,
            TRANSLATION = 2,
            RIGID_BODY_MOTION = 4,
            COLORMAP_AUTUMN = 0,
            COLORMAP_BONE = 1,
            COLORMAP_JET = 2,
            COLORMAP_WINTER = 3,
            COLORMAP_RAINBOW = 4,
            COLORMAP_OCEAN = 5,
            COLORMAP_SUMMER = 6,
            COLORMAP_SPRING = 7,
            COLORMAP_COOL = 8,
            COLORMAP_HSV = 9,
            COLORMAP_PINK = 10,
            COLORMAP_HOT = 11;


    //
    // C++:  void applyColorMap(Mat src, Mat& dst, int colormap)
    //

/**
 * <p>Applies a GNU Octave/MATLAB equivalent colormap on a given image.</p>
 *
 * <p>Currently the following GNU Octave/MATLAB equivalent colormaps are
 * implemented: enum <code></p>
 *
 * <p>// C++ code:</p>
 *
 *
 * <p>COLORMAP_AUTUMN = 0,</p>
 *
 * <p>COLORMAP_BONE = 1,</p>
 *
 * <p>COLORMAP_JET = 2,</p>
 *
 * <p>COLORMAP_WINTER = 3,</p>
 *
 * <p>COLORMAP_RAINBOW = 4,</p>
 *
 * <p>COLORMAP_OCEAN = 5,</p>
 *
 * <p>COLORMAP_SUMMER = 6,</p>
 *
 * <p>COLORMAP_SPRING = 7,</p>
 *
 * <p>COLORMAP_COOL = 8,</p>
 *
 * <p>COLORMAP_HSV = 9,</p>
 *
 * <p>COLORMAP_PINK = 10,</p>
 *
 * <p>COLORMAP_HOT = 11</p>
 *
 *
 * @param src The source image, grayscale or colored does not matter.
 * @param dst The result is the colormapped source image. Note: "Mat.create" is
 * called on dst.
 * @param colormap The colormap to apply, see the list of available colormaps
 * below.
 *
 * @see <a href="http://docs.opencv.org/modules/contrib/doc/colormaps.html#applycolormap">org.opencv.contrib.Contrib.applyColorMap</a>
 */
    public static void applyColorMap(Mat src, Mat dst, int colormap)
    {

        applyColorMap_0(src.nativeObj, dst.nativeObj, colormap);

        return;
    }


    //
    // C++:  int chamerMatching(Mat img, Mat templ, vector_vector_Point& results, vector_float& cost, double templScale = 1, int maxMatches = 20, double minMatchDistance = 1.0, int padX = 3, int padY = 3, int scales = 5, double minScale = 0.6, double maxScale = 1.6, double orientationWeight = 0.5, double truncate = 20)
    //

    public static int chamerMatching(Mat img, Mat templ, List<MatOfPoint> results, MatOfFloat cost, double templScale, int maxMatches, double minMatchDistance, int padX, int padY, int scales, double minScale, double maxScale, double orientationWeight, double truncate)
    {
        Mat results_mat = new Mat();
        Mat cost_mat = cost;
        int retVal = chamerMatching_0(img.nativeObj, templ.nativeObj, results_mat.nativeObj, cost_mat.nativeObj, templScale, maxMatches, minMatchDistance, padX, padY, scales, minScale, maxScale, orientationWeight, truncate);
        Converters.Mat_to_vector_vector_Point(results_mat, results);
        results_mat.release();
        return retVal;
    }

    public static int chamerMatching(Mat img, Mat templ, List<MatOfPoint> results, MatOfFloat cost)
    {
        Mat results_mat = new Mat();
        Mat cost_mat = cost;
        int retVal = chamerMatching_1(img.nativeObj, templ.nativeObj, results_mat.nativeObj, cost_mat.nativeObj);
        Converters.Mat_to_vector_vector_Point(results_mat, results);
        results_mat.release();
        return retVal;
    }


    //
    // C++:  Ptr_FaceRecognizer createEigenFaceRecognizer(int num_components = 0, double threshold = DBL_MAX)
    //

    // Return type 'Ptr_FaceRecognizer' is not supported, skipping the function


    //
    // C++:  Ptr_FaceRecognizer createFisherFaceRecognizer(int num_components = 0, double threshold = DBL_MAX)
    //

    // Return type 'Ptr_FaceRecognizer' is not supported, skipping the function


    //
    // C++:  Ptr_FaceRecognizer createLBPHFaceRecognizer(int radius = 1, int neighbors = 8, int grid_x = 8, int grid_y = 8, double threshold = DBL_MAX)
    //

    // Return type 'Ptr_FaceRecognizer' is not supported, skipping the function




    // C++:  void applyColorMap(Mat src, Mat& dst, int colormap)
    private static native void applyColorMap_0(long src_nativeObj, long dst_nativeObj, int colormap);

    // C++:  int chamerMatching(Mat img, Mat templ, vector_vector_Point& results, vector_float& cost, double templScale = 1, int maxMatches = 20, double minMatchDistance = 1.0, int padX = 3, int padY = 3, int scales = 5, double minScale = 0.6, double maxScale = 1.6, double orientationWeight = 0.5, double truncate = 20)
    private static native int chamerMatching_0(long img_nativeObj, long templ_nativeObj, long results_mat_nativeObj, long cost_mat_nativeObj, double templScale, int maxMatches, double minMatchDistance, int padX, int padY, int scales, double minScale, double maxScale, double orientationWeight, double truncate);
    private static native int chamerMatching_1(long img_nativeObj, long templ_nativeObj, long results_mat_nativeObj, long cost_mat_nativeObj);

}
