
//
// This file is auto-generated. Please don't modify it!
//
package org.opencv.objdetect;

import org.opencv.core.Mat;
import org.opencv.core.MatOfInt;
import org.opencv.core.MatOfRect;

public class Objdetect {

    public static final int
            CASCADE_DO_CANNY_PRUNING = 1,
            CASCADE_SCALE_IMAGE = 2,
            CASCADE_FIND_BIGGEST_OBJECT = 4,
            CASCADE_DO_ROUGH_SEARCH = 8;


    //
    // C++:  void drawDataMatrixCodes(Mat& image, vector_string codes, Mat corners)
    //

    // Unknown type 'vector_string' (I), skipping the function


    //
    // C++:  void findDataMatrix(Mat image, vector_string& codes, Mat& corners = Mat(), vector_Mat& dmtx = vector_Mat())
    //

    // Unknown type 'vector_string' (O), skipping the function


    //
    // C++:  void groupRectangles(vector_Rect& rectList, vector_int& weights, int groupThreshold, double eps = 0.2)
    //

/**
 * <p>Groups the object candidate rectangles.</p>
 *
 * <p>The function is a wrapper for the generic function "partition". It clusters
 * all the input rectangles using the rectangle equivalence criteria that
 * combines rectangles with similar sizes and similar locations. The similarity
 * is defined by <code>eps</code>. When <code>eps=0</code>, no clustering is
 * done at all. If <em>eps-> +inf</em>, all the rectangles are put in one
 * cluster. Then, the small clusters containing less than or equal to
 * <code>groupThreshold</code> rectangles are rejected. In each other cluster,
 * the average rectangle is computed and put into the output rectangle list.</p>
 *
 * @param rectList Input/output vector of rectangles. Output vector includes
 * retained and grouped rectangles. (The Python list is not modified in place.)
 * @param weights a weights
 * @param groupThreshold Minimum possible number of rectangles minus 1. The
 * threshold is used in a group of rectangles to retain it.
 * @param eps Relative difference between sides of the rectangles to merge them
 * into a group.
 *
 * @see <a href="http://docs.opencv.org/modules/objdetect/doc/cascade_classification.html#grouprectangles">org.opencv.objdetect.Objdetect.groupRectangles</a>
 */
    public static void groupRectangles(MatOfRect rectList, MatOfInt weights, int groupThreshold, double eps)
    {
        Mat rectList_mat = rectList;
        Mat weights_mat = weights;
        groupRectangles_0(rectList_mat.nativeObj, weights_mat.nativeObj, groupThreshold, eps);

        return;
    }

/**
 * <p>Groups the object candidate rectangles.</p>
 *
 * <p>The function is a wrapper for the generic function "partition". It clusters
 * all the input rectangles using the rectangle equivalence criteria that
 * combines rectangles with similar sizes and similar locations. The similarity
 * is defined by <code>eps</code>. When <code>eps=0</code>, no clustering is
 * done at all. If <em>eps-> +inf</em>, all the rectangles are put in one
 * cluster. Then, the small clusters containing less than or equal to
 * <code>groupThreshold</code> rectangles are rejected. In each other cluster,
 * the average rectangle is computed and put into the output rectangle list.</p>
 *
 * @param rectList Input/output vector of rectangles. Output vector includes
 * retained and grouped rectangles. (The Python list is not modified in place.)
 * @param weights a weights
 * @param groupThreshold Minimum possible number of rectangles minus 1. The
 * threshold is used in a group of rectangles to retain it.
 *
 * @see <a href="http://docs.opencv.org/modules/objdetect/doc/cascade_classification.html#grouprectangles">org.opencv.objdetect.Objdetect.groupRectangles</a>
 */
    public static void groupRectangles(MatOfRect rectList, MatOfInt weights, int groupThreshold)
    {
        Mat rectList_mat = rectList;
        Mat weights_mat = weights;
        groupRectangles_1(rectList_mat.nativeObj, weights_mat.nativeObj, groupThreshold);

        return;
    }




    // C++:  void groupRectangles(vector_Rect& rectList, vector_int& weights, int groupThreshold, double eps = 0.2)
    private static native void groupRectangles_0(long rectList_mat_nativeObj, long weights_mat_nativeObj, int groupThreshold, double eps);
    private static native void groupRectangles_1(long rectList_mat_nativeObj, long weights_mat_nativeObj, int groupThreshold);

}
