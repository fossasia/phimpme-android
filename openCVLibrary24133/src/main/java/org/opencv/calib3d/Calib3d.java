
//
// This file is auto-generated. Please don't modify it!
//
package org.opencv.calib3d;

import java.util.ArrayList;
import java.util.List;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDouble;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.MatOfPoint3f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.core.TermCriteria;
import org.opencv.utils.Converters;

public class Calib3d {

    private static final int
            CV_LMEDS = 4,
            CV_RANSAC = 8,
            CV_FM_LMEDS = CV_LMEDS,
            CV_FM_RANSAC = CV_RANSAC,
            CV_FM_7POINT = 1,
            CV_FM_8POINT = 2,
            CV_CALIB_USE_INTRINSIC_GUESS = 1,
            CV_CALIB_FIX_ASPECT_RATIO = 2,
            CV_CALIB_FIX_PRINCIPAL_POINT = 4,
            CV_CALIB_ZERO_TANGENT_DIST = 8,
            CV_CALIB_FIX_FOCAL_LENGTH = 16,
            CV_CALIB_FIX_K1 = 32,
            CV_CALIB_FIX_K2 = 64,
            CV_CALIB_FIX_K3 = 128,
            CV_CALIB_FIX_K4 = 2048,
            CV_CALIB_FIX_K5 = 4096,
            CV_CALIB_FIX_K6 = 8192,
            CV_CALIB_RATIONAL_MODEL = 16384,
            CV_CALIB_FIX_INTRINSIC = 256,
            CV_CALIB_SAME_FOCAL_LENGTH = 512,
            CV_CALIB_ZERO_DISPARITY = 1024;


    public static final int
            CV_ITERATIVE = 0,
            CV_EPNP = 1,
            CV_P3P = 2,
            LMEDS = CV_LMEDS,
            RANSAC = CV_RANSAC,
            ITERATIVE = CV_ITERATIVE,
            EPNP = CV_EPNP,
            P3P = CV_P3P,
            CALIB_CB_ADAPTIVE_THRESH = 1,
            CALIB_CB_NORMALIZE_IMAGE = 2,
            CALIB_CB_FILTER_QUADS = 4,
            CALIB_CB_FAST_CHECK = 8,
            CALIB_CB_SYMMETRIC_GRID = 1,
            CALIB_CB_ASYMMETRIC_GRID = 2,
            CALIB_CB_CLUSTERING = 4,
            CALIB_USE_INTRINSIC_GUESS = CV_CALIB_USE_INTRINSIC_GUESS,
            CALIB_FIX_ASPECT_RATIO = CV_CALIB_FIX_ASPECT_RATIO,
            CALIB_FIX_PRINCIPAL_POINT = CV_CALIB_FIX_PRINCIPAL_POINT,
            CALIB_ZERO_TANGENT_DIST = CV_CALIB_ZERO_TANGENT_DIST,
            CALIB_FIX_FOCAL_LENGTH = CV_CALIB_FIX_FOCAL_LENGTH,
            CALIB_FIX_K1 = CV_CALIB_FIX_K1,
            CALIB_FIX_K2 = CV_CALIB_FIX_K2,
            CALIB_FIX_K3 = CV_CALIB_FIX_K3,
            CALIB_FIX_K4 = CV_CALIB_FIX_K4,
            CALIB_FIX_K5 = CV_CALIB_FIX_K5,
            CALIB_FIX_K6 = CV_CALIB_FIX_K6,
            CALIB_RATIONAL_MODEL = CV_CALIB_RATIONAL_MODEL,
            CALIB_FIX_INTRINSIC = CV_CALIB_FIX_INTRINSIC,
            CALIB_SAME_FOCAL_LENGTH = CV_CALIB_SAME_FOCAL_LENGTH,
            CALIB_ZERO_DISPARITY = CV_CALIB_ZERO_DISPARITY,
            FM_7POINT = CV_FM_7POINT,
            FM_8POINT = CV_FM_8POINT,
            FM_LMEDS = CV_FM_LMEDS,
            FM_RANSAC = CV_FM_RANSAC;


    //
    // C++:  Vec3d RQDecomp3x3(Mat src, Mat& mtxR, Mat& mtxQ, Mat& Qx = Mat(), Mat& Qy = Mat(), Mat& Qz = Mat())
    //

/**
 * <p>Computes an RQ decomposition of 3x3 matrices.</p>
 *
 * <p>The function computes a RQ decomposition using the given rotations. This
 * function is used in "decomposeProjectionMatrix" to decompose the left 3x3
 * submatrix of a projection matrix into a camera and a rotation matrix.</p>
 *
 * <p>It optionally returns three rotation matrices, one for each axis, and the
 * three Euler angles in degrees (as the return value) that could be used in
 * OpenGL. Note, there is always more than one sequence of rotations about the
 * three principle axes that results in the same orientation of an object, eg.
 * see [Slabaugh]. Returned tree rotation matrices and corresponding three Euler
 * angules are only one of the possible solutions.</p>
 *
 * @param src 3x3 input matrix.
 * @param mtxR Output 3x3 upper-triangular matrix.
 * @param mtxQ Output 3x3 orthogonal matrix.
 * @param Qx Optional output 3x3 rotation matrix around x-axis.
 * @param Qy Optional output 3x3 rotation matrix around y-axis.
 * @param Qz Optional output 3x3 rotation matrix around z-axis.
 *
 * @see <a href="http://docs.opencv.org/modules/calib3d/doc/camera_calibration_and_3d_reconstruction.html#rqdecomp3x3">org.opencv.calib3d.Calib3d.RQDecomp3x3</a>
 */
    public static double[] RQDecomp3x3(Mat src, Mat mtxR, Mat mtxQ, Mat Qx, Mat Qy, Mat Qz)
    {

        double[] retVal = RQDecomp3x3_0(src.nativeObj, mtxR.nativeObj, mtxQ.nativeObj, Qx.nativeObj, Qy.nativeObj, Qz.nativeObj);

        return retVal;
    }

/**
 * <p>Computes an RQ decomposition of 3x3 matrices.</p>
 *
 * <p>The function computes a RQ decomposition using the given rotations. This
 * function is used in "decomposeProjectionMatrix" to decompose the left 3x3
 * submatrix of a projection matrix into a camera and a rotation matrix.</p>
 *
 * <p>It optionally returns three rotation matrices, one for each axis, and the
 * three Euler angles in degrees (as the return value) that could be used in
 * OpenGL. Note, there is always more than one sequence of rotations about the
 * three principle axes that results in the same orientation of an object, eg.
 * see [Slabaugh]. Returned tree rotation matrices and corresponding three Euler
 * angules are only one of the possible solutions.</p>
 *
 * @param src 3x3 input matrix.
 * @param mtxR Output 3x3 upper-triangular matrix.
 * @param mtxQ Output 3x3 orthogonal matrix.
 *
 * @see <a href="http://docs.opencv.org/modules/calib3d/doc/camera_calibration_and_3d_reconstruction.html#rqdecomp3x3">org.opencv.calib3d.Calib3d.RQDecomp3x3</a>
 */
    public static double[] RQDecomp3x3(Mat src, Mat mtxR, Mat mtxQ)
    {

        double[] retVal = RQDecomp3x3_1(src.nativeObj, mtxR.nativeObj, mtxQ.nativeObj);

        return retVal;
    }


    //
    // C++:  void Rodrigues(Mat src, Mat& dst, Mat& jacobian = Mat())
    //

/**
 * <p>Converts a rotation matrix to a rotation vector or vice versa.</p>
 *
 * <p><em>theta <- norm(r)
 * r <- r/ theta
 * R = cos(theta) I + (1- cos(theta)) r r^T + sin(theta)
 * |0 -r_z r_y|
 * |r_z 0 -r_x|
 * |-r_y r_x 0|
 * </em></p>
 *
 * <p>Inverse transformation can be also done easily, since</p>
 *
 * <p><em>sin(theta)
 * |0 -r_z r_y|
 * |r_z 0 -r_x|
 * |-r_y r_x 0|
 * = (R - R^T)/2</em></p>
 *
 * <p>A rotation vector is a convenient and most compact representation of a
 * rotation matrix (since any rotation matrix has just 3 degrees of freedom).
 * The representation is used in the global 3D geometry optimization procedures
 * like "calibrateCamera", "stereoCalibrate", or "solvePnP".</p>
 *
 * @param src Input rotation vector (3x1 or 1x3) or rotation matrix (3x3).
 * @param dst Output rotation matrix (3x3) or rotation vector (3x1 or 1x3),
 * respectively.
 * @param jacobian Optional output Jacobian matrix, 3x9 or 9x3, which is a
 * matrix of partial derivatives of the output array components with respect to
 * the input array components.
 *
 * @see <a href="http://docs.opencv.org/modules/calib3d/doc/camera_calibration_and_3d_reconstruction.html#rodrigues">org.opencv.calib3d.Calib3d.Rodrigues</a>
 */
    public static void Rodrigues(Mat src, Mat dst, Mat jacobian)
    {

        Rodrigues_0(src.nativeObj, dst.nativeObj, jacobian.nativeObj);

        return;
    }

/**
 * <p>Converts a rotation matrix to a rotation vector or vice versa.</p>
 *
 * <p><em>theta <- norm(r)
 * r <- r/ theta
 * R = cos(theta) I + (1- cos(theta)) r r^T + sin(theta)
 * |0 -r_z r_y|
 * |r_z 0 -r_x|
 * |-r_y r_x 0|
 * </em></p>
 *
 * <p>Inverse transformation can be also done easily, since</p>
 *
 * <p><em>sin(theta)
 * |0 -r_z r_y|
 * |r_z 0 -r_x|
 * |-r_y r_x 0|
 * = (R - R^T)/2</em></p>
 *
 * <p>A rotation vector is a convenient and most compact representation of a
 * rotation matrix (since any rotation matrix has just 3 degrees of freedom).
 * The representation is used in the global 3D geometry optimization procedures
 * like "calibrateCamera", "stereoCalibrate", or "solvePnP".</p>
 *
 * @param src Input rotation vector (3x1 or 1x3) or rotation matrix (3x3).
 * @param dst Output rotation matrix (3x3) or rotation vector (3x1 or 1x3),
 * respectively.
 *
 * @see <a href="http://docs.opencv.org/modules/calib3d/doc/camera_calibration_and_3d_reconstruction.html#rodrigues">org.opencv.calib3d.Calib3d.Rodrigues</a>
 */
    public static void Rodrigues(Mat src, Mat dst)
    {

        Rodrigues_1(src.nativeObj, dst.nativeObj);

        return;
    }


    //
    // C++:  double calibrateCamera(vector_Mat objectPoints, vector_Mat imagePoints, Size imageSize, Mat& cameraMatrix, Mat& distCoeffs, vector_Mat& rvecs, vector_Mat& tvecs, int flags = 0, TermCriteria criteria = TermCriteria( TermCriteria::COUNT+TermCriteria::EPS, 30, DBL_EPSILON))
    //

/**
 * <p>Finds the camera intrinsic and extrinsic parameters from several views of a
 * calibration pattern.</p>
 *
 * <p>The function estimates the intrinsic camera parameters and extrinsic
 * parameters for each of the views. The algorithm is based on [Zhang2000] and
 * [BouguetMCT]. The coordinates of 3D object points and their corresponding 2D
 * projections in each view must be specified. That may be achieved by using an
 * object with a known geometry and easily detectable feature points.
 * Such an object is called a calibration rig or calibration pattern, and OpenCV
 * has built-in support for a chessboard as a calibration rig (see
 * "findChessboardCorners"). Currently, initialization of intrinsic parameters
 * (when <code>CV_CALIB_USE_INTRINSIC_GUESS</code> is not set) is only
 * implemented for planar calibration patterns (where Z-coordinates of the
 * object points must be all zeros). 3D calibration rigs can also be used as
 * long as initial <code>cameraMatrix</code> is provided.</p>
 *
 * <p>The algorithm performs the following steps:</p>
 * <ul>
 *   <li> Compute the initial intrinsic parameters (the option only available
 * for planar calibration patterns) or read them from the input parameters. The
 * distortion coefficients are all set to zeros initially unless some of
 * <code>CV_CALIB_FIX_K?</code> are specified.
 *   <li> Estimate the initial camera pose as if the intrinsic parameters have
 * been already known. This is done using "solvePnP".
 *   <li> Run the global Levenberg-Marquardt optimization algorithm to minimize
 * the reprojection error, that is, the total sum of squared distances between
 * the observed feature points <code>imagePoints</code> and the projected (using
 * the current estimates for camera parameters and the poses) object points
 * <code>objectPoints</code>. See "projectPoints" for details.
 * </ul>
 *
 * <p>The function returns the final re-projection error.</p>
 *
 * <p>Note:</p>
 *
 * <p>If you use a non-square (=non-NxN) grid and "findChessboardCorners" for
 * calibration, and <code>calibrateCamera</code> returns bad values (zero
 * distortion coefficients, an image center very far from <code>(w/2-0.5,h/2-0.5)</code>,
 * and/or large differences between <em>f_x</em> and <em>f_y</em> (ratios of
 * 10:1 or more)), then you have probably used <code>patternSize=cvSize(rows,cols)</code>
 * instead of using <code>patternSize=cvSize(cols,rows)</code> in
 * "findChessboardCorners".</p>
 *
 * @param objectPoints In the new interface it is a vector of vectors of
 * calibration pattern points in the calibration pattern coordinate space (e.g.
 * std.vector<std.vector<cv.Vec3f>>). The outer vector contains as many
 * elements as the number of the pattern views. If the same calibration pattern
 * is shown in each view and it is fully visible, all the vectors will be the
 * same. Although, it is possible to use partially occluded patterns, or even
 * different patterns in different views. Then, the vectors will be different.
 * The points are 3D, but since they are in a pattern coordinate system, then,
 * if the rig is planar, it may make sense to put the model to a XY coordinate
 * plane so that Z-coordinate of each input object point is 0.
 *
 * <p>In the old interface all the vectors of object points from different views
 * are concatenated together.</p>
 * @param imagePoints In the new interface it is a vector of vectors of the
 * projections of calibration pattern points (e.g. std.vector<std.vector<cv.Vec2f>>).
 * <code>imagePoints.size()</code> and <code>objectPoints.size()</code> and
 * <code>imagePoints[i].size()</code> must be equal to <code>objectPoints[i].size()</code>
 * for each <code>i</code>.
 *
 * <p>In the old interface all the vectors of object points from different views
 * are concatenated together.</p>
 * @param imageSize Size of the image used only to initialize the intrinsic
 * camera matrix.
 * @param cameraMatrix Output 3x3 floating-point camera matrix <em>A =
 * <p>|f_x 0 c_x|
 * |0 f_y c_y|
 * |0 0 1|
 * </em>. If <code>CV_CALIB_USE_INTRINSIC_GUESS</code> and/or <code>CV_CALIB_FIX_ASPECT_RATIO</code>
 * are specified, some or all of <code>fx, fy, cx, cy</code> must be initialized
 * before calling the function.</p>
 * @param distCoeffs Output vector of distortion coefficients <em>(k_1, k_2,
 * p_1, p_2[, k_3[, k_4, k_5, k_6]])</em> of 4, 5, or 8 elements.
 * @param rvecs Output vector of rotation vectors (see "Rodrigues") estimated
 * for each pattern view (e.g. std.vector<cv.Mat>>). That is, each k-th
 * rotation vector together with the corresponding k-th translation vector (see
 * the next output parameter description) brings the calibration pattern from
 * the model coordinate space (in which object points are specified) to the
 * world coordinate space, that is, a real position of the calibration pattern
 * in the k-th pattern view (k=0.. *M* -1).
 * @param tvecs Output vector of translation vectors estimated for each pattern
 * view.
 * @param flags Different flags that may be zero or a combination of the
 * following values:
 * <ul>
 *   <li> CV_CALIB_USE_INTRINSIC_GUESS <code>cameraMatrix</code> contains valid
 * initial values of <code>fx, fy, cx, cy</code> that are optimized further.
 * Otherwise, <code>(cx, cy)</code> is initially set to the image center
 * (<code>imageSize</code> is used), and focal distances are computed in a
 * least-squares fashion. Note, that if intrinsic parameters are known, there is
 * no need to use this function just to estimate extrinsic parameters. Use
 * "solvePnP" instead.
 *   <li> CV_CALIB_FIX_PRINCIPAL_POINT The principal point is not changed during
 * the global optimization. It stays at the center or at a different location
 * specified when <code>CV_CALIB_USE_INTRINSIC_GUESS</code> is set too.
 *   <li> CV_CALIB_FIX_ASPECT_RATIO The functions considers only <code>fy</code>
 * as a free parameter. The ratio <code>fx/fy</code> stays the same as in the
 * input <code>cameraMatrix</code>. When <code>CV_CALIB_USE_INTRINSIC_GUESS</code>
 * is not set, the actual input values of <code>fx</code> and <code>fy</code>
 * are ignored, only their ratio is computed and used further.
 *   <li> CV_CALIB_ZERO_TANGENT_DIST Tangential distortion coefficients
 * <em>(p_1, p_2)</em> are set to zeros and stay zero.
 *   <li> CV_CALIB_FIX_K1,...,CV_CALIB_FIX_K6 The corresponding radial
 * distortion coefficient is not changed during the optimization. If
 * <code>CV_CALIB_USE_INTRINSIC_GUESS</code> is set, the coefficient from the
 * supplied <code>distCoeffs</code> matrix is used. Otherwise, it is set to 0.
 *   <li> CV_CALIB_RATIONAL_MODEL Coefficients k4, k5, and k6 are enabled. To
 * provide the backward compatibility, this extra flag should be explicitly
 * specified to make the calibration function use the rational model and return
 * 8 coefficients. If the flag is not set, the function computes and returns
 * only 5 distortion coefficients.
 * </ul>
 * @param criteria Termination criteria for the iterative optimization
 * algorithm.
 *
 * @see <a href="http://docs.opencv.org/modules/calib3d/doc/camera_calibration_and_3d_reconstruction.html#calibratecamera">org.opencv.calib3d.Calib3d.calibrateCamera</a>
 * @see org.opencv.calib3d.Calib3d#initCameraMatrix2D
 * @see org.opencv.calib3d.Calib3d#stereoCalibrate
 * @see org.opencv.calib3d.Calib3d#findChessboardCorners
 * @see org.opencv.calib3d.Calib3d#solvePnP
 * @see org.opencv.imgproc.Imgproc#undistort
 */
    public static double calibrateCamera(List<Mat> objectPoints, List<Mat> imagePoints, Size imageSize, Mat cameraMatrix, Mat distCoeffs, List<Mat> rvecs, List<Mat> tvecs, int flags, TermCriteria criteria)
    {
        Mat objectPoints_mat = Converters.vector_Mat_to_Mat(objectPoints);
        Mat imagePoints_mat = Converters.vector_Mat_to_Mat(imagePoints);
        Mat rvecs_mat = new Mat();
        Mat tvecs_mat = new Mat();
        double retVal = calibrateCamera_0(objectPoints_mat.nativeObj, imagePoints_mat.nativeObj, imageSize.width, imageSize.height, cameraMatrix.nativeObj, distCoeffs.nativeObj, rvecs_mat.nativeObj, tvecs_mat.nativeObj, flags, criteria.type, criteria.maxCount, criteria.epsilon);
        Converters.Mat_to_vector_Mat(rvecs_mat, rvecs);
        rvecs_mat.release();
        Converters.Mat_to_vector_Mat(tvecs_mat, tvecs);
        tvecs_mat.release();
        return retVal;
    }

/**
 * <p>Finds the camera intrinsic and extrinsic parameters from several views of a
 * calibration pattern.</p>
 *
 * <p>The function estimates the intrinsic camera parameters and extrinsic
 * parameters for each of the views. The algorithm is based on [Zhang2000] and
 * [BouguetMCT]. The coordinates of 3D object points and their corresponding 2D
 * projections in each view must be specified. That may be achieved by using an
 * object with a known geometry and easily detectable feature points.
 * Such an object is called a calibration rig or calibration pattern, and OpenCV
 * has built-in support for a chessboard as a calibration rig (see
 * "findChessboardCorners"). Currently, initialization of intrinsic parameters
 * (when <code>CV_CALIB_USE_INTRINSIC_GUESS</code> is not set) is only
 * implemented for planar calibration patterns (where Z-coordinates of the
 * object points must be all zeros). 3D calibration rigs can also be used as
 * long as initial <code>cameraMatrix</code> is provided.</p>
 *
 * <p>The algorithm performs the following steps:</p>
 * <ul>
 *   <li> Compute the initial intrinsic parameters (the option only available
 * for planar calibration patterns) or read them from the input parameters. The
 * distortion coefficients are all set to zeros initially unless some of
 * <code>CV_CALIB_FIX_K?</code> are specified.
 *   <li> Estimate the initial camera pose as if the intrinsic parameters have
 * been already known. This is done using "solvePnP".
 *   <li> Run the global Levenberg-Marquardt optimization algorithm to minimize
 * the reprojection error, that is, the total sum of squared distances between
 * the observed feature points <code>imagePoints</code> and the projected (using
 * the current estimates for camera parameters and the poses) object points
 * <code>objectPoints</code>. See "projectPoints" for details.
 * </ul>
 *
 * <p>The function returns the final re-projection error.</p>
 *
 * <p>Note:</p>
 *
 * <p>If you use a non-square (=non-NxN) grid and "findChessboardCorners" for
 * calibration, and <code>calibrateCamera</code> returns bad values (zero
 * distortion coefficients, an image center very far from <code>(w/2-0.5,h/2-0.5)</code>,
 * and/or large differences between <em>f_x</em> and <em>f_y</em> (ratios of
 * 10:1 or more)), then you have probably used <code>patternSize=cvSize(rows,cols)</code>
 * instead of using <code>patternSize=cvSize(cols,rows)</code> in
 * "findChessboardCorners".</p>
 *
 * @param objectPoints In the new interface it is a vector of vectors of
 * calibration pattern points in the calibration pattern coordinate space (e.g.
 * std.vector<std.vector<cv.Vec3f>>). The outer vector contains as many
 * elements as the number of the pattern views. If the same calibration pattern
 * is shown in each view and it is fully visible, all the vectors will be the
 * same. Although, it is possible to use partially occluded patterns, or even
 * different patterns in different views. Then, the vectors will be different.
 * The points are 3D, but since they are in a pattern coordinate system, then,
 * if the rig is planar, it may make sense to put the model to a XY coordinate
 * plane so that Z-coordinate of each input object point is 0.
 *
 * <p>In the old interface all the vectors of object points from different views
 * are concatenated together.</p>
 * @param imagePoints In the new interface it is a vector of vectors of the
 * projections of calibration pattern points (e.g. std.vector<std.vector<cv.Vec2f>>).
 * <code>imagePoints.size()</code> and <code>objectPoints.size()</code> and
 * <code>imagePoints[i].size()</code> must be equal to <code>objectPoints[i].size()</code>
 * for each <code>i</code>.
 *
 * <p>In the old interface all the vectors of object points from different views
 * are concatenated together.</p>
 * @param imageSize Size of the image used only to initialize the intrinsic
 * camera matrix.
 * @param cameraMatrix Output 3x3 floating-point camera matrix <em>A =
 * <p>|f_x 0 c_x|
 * |0 f_y c_y|
 * |0 0 1|
 * </em>. If <code>CV_CALIB_USE_INTRINSIC_GUESS</code> and/or <code>CV_CALIB_FIX_ASPECT_RATIO</code>
 * are specified, some or all of <code>fx, fy, cx, cy</code> must be initialized
 * before calling the function.</p>
 * @param distCoeffs Output vector of distortion coefficients <em>(k_1, k_2,
 * p_1, p_2[, k_3[, k_4, k_5, k_6]])</em> of 4, 5, or 8 elements.
 * @param rvecs Output vector of rotation vectors (see "Rodrigues") estimated
 * for each pattern view (e.g. std.vector<cv.Mat>>). That is, each k-th
 * rotation vector together with the corresponding k-th translation vector (see
 * the next output parameter description) brings the calibration pattern from
 * the model coordinate space (in which object points are specified) to the
 * world coordinate space, that is, a real position of the calibration pattern
 * in the k-th pattern view (k=0.. *M* -1).
 * @param tvecs Output vector of translation vectors estimated for each pattern
 * view.
 * @param flags Different flags that may be zero or a combination of the
 * following values:
 * <ul>
 *   <li> CV_CALIB_USE_INTRINSIC_GUESS <code>cameraMatrix</code> contains valid
 * initial values of <code>fx, fy, cx, cy</code> that are optimized further.
 * Otherwise, <code>(cx, cy)</code> is initially set to the image center
 * (<code>imageSize</code> is used), and focal distances are computed in a
 * least-squares fashion. Note, that if intrinsic parameters are known, there is
 * no need to use this function just to estimate extrinsic parameters. Use
 * "solvePnP" instead.
 *   <li> CV_CALIB_FIX_PRINCIPAL_POINT The principal point is not changed during
 * the global optimization. It stays at the center or at a different location
 * specified when <code>CV_CALIB_USE_INTRINSIC_GUESS</code> is set too.
 *   <li> CV_CALIB_FIX_ASPECT_RATIO The functions considers only <code>fy</code>
 * as a free parameter. The ratio <code>fx/fy</code> stays the same as in the
 * input <code>cameraMatrix</code>. When <code>CV_CALIB_USE_INTRINSIC_GUESS</code>
 * is not set, the actual input values of <code>fx</code> and <code>fy</code>
 * are ignored, only their ratio is computed and used further.
 *   <li> CV_CALIB_ZERO_TANGENT_DIST Tangential distortion coefficients
 * <em>(p_1, p_2)</em> are set to zeros and stay zero.
 *   <li> CV_CALIB_FIX_K1,...,CV_CALIB_FIX_K6 The corresponding radial
 * distortion coefficient is not changed during the optimization. If
 * <code>CV_CALIB_USE_INTRINSIC_GUESS</code> is set, the coefficient from the
 * supplied <code>distCoeffs</code> matrix is used. Otherwise, it is set to 0.
 *   <li> CV_CALIB_RATIONAL_MODEL Coefficients k4, k5, and k6 are enabled. To
 * provide the backward compatibility, this extra flag should be explicitly
 * specified to make the calibration function use the rational model and return
 * 8 coefficients. If the flag is not set, the function computes and returns
 * only 5 distortion coefficients.
 * </ul>
 *
 * @see <a href="http://docs.opencv.org/modules/calib3d/doc/camera_calibration_and_3d_reconstruction.html#calibratecamera">org.opencv.calib3d.Calib3d.calibrateCamera</a>
 * @see org.opencv.calib3d.Calib3d#initCameraMatrix2D
 * @see org.opencv.calib3d.Calib3d#stereoCalibrate
 * @see org.opencv.calib3d.Calib3d#findChessboardCorners
 * @see org.opencv.calib3d.Calib3d#solvePnP
 * @see org.opencv.imgproc.Imgproc#undistort
 */
    public static double calibrateCamera(List<Mat> objectPoints, List<Mat> imagePoints, Size imageSize, Mat cameraMatrix, Mat distCoeffs, List<Mat> rvecs, List<Mat> tvecs, int flags)
    {
        Mat objectPoints_mat = Converters.vector_Mat_to_Mat(objectPoints);
        Mat imagePoints_mat = Converters.vector_Mat_to_Mat(imagePoints);
        Mat rvecs_mat = new Mat();
        Mat tvecs_mat = new Mat();
        double retVal = calibrateCamera_1(objectPoints_mat.nativeObj, imagePoints_mat.nativeObj, imageSize.width, imageSize.height, cameraMatrix.nativeObj, distCoeffs.nativeObj, rvecs_mat.nativeObj, tvecs_mat.nativeObj, flags);
        Converters.Mat_to_vector_Mat(rvecs_mat, rvecs);
        rvecs_mat.release();
        Converters.Mat_to_vector_Mat(tvecs_mat, tvecs);
        tvecs_mat.release();
        return retVal;
    }

/**
 * <p>Finds the camera intrinsic and extrinsic parameters from several views of a
 * calibration pattern.</p>
 *
 * <p>The function estimates the intrinsic camera parameters and extrinsic
 * parameters for each of the views. The algorithm is based on [Zhang2000] and
 * [BouguetMCT]. The coordinates of 3D object points and their corresponding 2D
 * projections in each view must be specified. That may be achieved by using an
 * object with a known geometry and easily detectable feature points.
 * Such an object is called a calibration rig or calibration pattern, and OpenCV
 * has built-in support for a chessboard as a calibration rig (see
 * "findChessboardCorners"). Currently, initialization of intrinsic parameters
 * (when <code>CV_CALIB_USE_INTRINSIC_GUESS</code> is not set) is only
 * implemented for planar calibration patterns (where Z-coordinates of the
 * object points must be all zeros). 3D calibration rigs can also be used as
 * long as initial <code>cameraMatrix</code> is provided.</p>
 *
 * <p>The algorithm performs the following steps:</p>
 * <ul>
 *   <li> Compute the initial intrinsic parameters (the option only available
 * for planar calibration patterns) or read them from the input parameters. The
 * distortion coefficients are all set to zeros initially unless some of
 * <code>CV_CALIB_FIX_K?</code> are specified.
 *   <li> Estimate the initial camera pose as if the intrinsic parameters have
 * been already known. This is done using "solvePnP".
 *   <li> Run the global Levenberg-Marquardt optimization algorithm to minimize
 * the reprojection error, that is, the total sum of squared distances between
 * the observed feature points <code>imagePoints</code> and the projected (using
 * the current estimates for camera parameters and the poses) object points
 * <code>objectPoints</code>. See "projectPoints" for details.
 * </ul>
 *
 * <p>The function returns the final re-projection error.</p>
 *
 * <p>Note:</p>
 *
 * <p>If you use a non-square (=non-NxN) grid and "findChessboardCorners" for
 * calibration, and <code>calibrateCamera</code> returns bad values (zero
 * distortion coefficients, an image center very far from <code>(w/2-0.5,h/2-0.5)</code>,
 * and/or large differences between <em>f_x</em> and <em>f_y</em> (ratios of
 * 10:1 or more)), then you have probably used <code>patternSize=cvSize(rows,cols)</code>
 * instead of using <code>patternSize=cvSize(cols,rows)</code> in
 * "findChessboardCorners".</p>
 *
 * @param objectPoints In the new interface it is a vector of vectors of
 * calibration pattern points in the calibration pattern coordinate space (e.g.
 * std.vector<std.vector<cv.Vec3f>>). The outer vector contains as many
 * elements as the number of the pattern views. If the same calibration pattern
 * is shown in each view and it is fully visible, all the vectors will be the
 * same. Although, it is possible to use partially occluded patterns, or even
 * different patterns in different views. Then, the vectors will be different.
 * The points are 3D, but since they are in a pattern coordinate system, then,
 * if the rig is planar, it may make sense to put the model to a XY coordinate
 * plane so that Z-coordinate of each input object point is 0.
 *
 * <p>In the old interface all the vectors of object points from different views
 * are concatenated together.</p>
 * @param imagePoints In the new interface it is a vector of vectors of the
 * projections of calibration pattern points (e.g. std.vector<std.vector<cv.Vec2f>>).
 * <code>imagePoints.size()</code> and <code>objectPoints.size()</code> and
 * <code>imagePoints[i].size()</code> must be equal to <code>objectPoints[i].size()</code>
 * for each <code>i</code>.
 *
 * <p>In the old interface all the vectors of object points from different views
 * are concatenated together.</p>
 * @param imageSize Size of the image used only to initialize the intrinsic
 * camera matrix.
 * @param cameraMatrix Output 3x3 floating-point camera matrix <em>A =
 * <p>|f_x 0 c_x|
 * |0 f_y c_y|
 * |0 0 1|
 * </em>. If <code>CV_CALIB_USE_INTRINSIC_GUESS</code> and/or <code>CV_CALIB_FIX_ASPECT_RATIO</code>
 * are specified, some or all of <code>fx, fy, cx, cy</code> must be initialized
 * before calling the function.</p>
 * @param distCoeffs Output vector of distortion coefficients <em>(k_1, k_2,
 * p_1, p_2[, k_3[, k_4, k_5, k_6]])</em> of 4, 5, or 8 elements.
 * @param rvecs Output vector of rotation vectors (see "Rodrigues") estimated
 * for each pattern view (e.g. std.vector<cv.Mat>>). That is, each k-th
 * rotation vector together with the corresponding k-th translation vector (see
 * the next output parameter description) brings the calibration pattern from
 * the model coordinate space (in which object points are specified) to the
 * world coordinate space, that is, a real position of the calibration pattern
 * in the k-th pattern view (k=0.. *M* -1).
 * @param tvecs Output vector of translation vectors estimated for each pattern
 * view.
 *
 * @see <a href="http://docs.opencv.org/modules/calib3d/doc/camera_calibration_and_3d_reconstruction.html#calibratecamera">org.opencv.calib3d.Calib3d.calibrateCamera</a>
 * @see org.opencv.calib3d.Calib3d#initCameraMatrix2D
 * @see org.opencv.calib3d.Calib3d#stereoCalibrate
 * @see org.opencv.calib3d.Calib3d#findChessboardCorners
 * @see org.opencv.calib3d.Calib3d#solvePnP
 * @see org.opencv.imgproc.Imgproc#undistort
 */
    public static double calibrateCamera(List<Mat> objectPoints, List<Mat> imagePoints, Size imageSize, Mat cameraMatrix, Mat distCoeffs, List<Mat> rvecs, List<Mat> tvecs)
    {
        Mat objectPoints_mat = Converters.vector_Mat_to_Mat(objectPoints);
        Mat imagePoints_mat = Converters.vector_Mat_to_Mat(imagePoints);
        Mat rvecs_mat = new Mat();
        Mat tvecs_mat = new Mat();
        double retVal = calibrateCamera_2(objectPoints_mat.nativeObj, imagePoints_mat.nativeObj, imageSize.width, imageSize.height, cameraMatrix.nativeObj, distCoeffs.nativeObj, rvecs_mat.nativeObj, tvecs_mat.nativeObj);
        Converters.Mat_to_vector_Mat(rvecs_mat, rvecs);
        rvecs_mat.release();
        Converters.Mat_to_vector_Mat(tvecs_mat, tvecs);
        tvecs_mat.release();
        return retVal;
    }


    //
    // C++:  void calibrationMatrixValues(Mat cameraMatrix, Size imageSize, double apertureWidth, double apertureHeight, double& fovx, double& fovy, double& focalLength, Point2d& principalPoint, double& aspectRatio)
    //

/**
 * <p>Computes useful camera characteristics from the camera matrix.</p>
 *
 * <p>The function computes various useful camera characteristics from the
 * previously estimated camera matrix.</p>
 *
 * <p>Note:</p>
 *
 * <p>Do keep in mind that the unity measure 'mm' stands for whatever unit of
 * measure one chooses for the chessboard pitch (it can thus be any value).</p>
 *
 * @param cameraMatrix Input camera matrix that can be estimated by
 * "calibrateCamera" or "stereoCalibrate".
 * @param imageSize Input image size in pixels.
 * @param apertureWidth Physical width in mm of the sensor.
 * @param apertureHeight Physical height in mm of the sensor.
 * @param fovx Output field of view in degrees along the horizontal sensor axis.
 * @param fovy Output field of view in degrees along the vertical sensor axis.
 * @param focalLength Focal length of the lens in mm.
 * @param principalPoint Principal point in mm.
 * @param aspectRatio <em>f_y/f_x</em>
 *
 * @see <a href="http://docs.opencv.org/modules/calib3d/doc/camera_calibration_and_3d_reconstruction.html#calibrationmatrixvalues">org.opencv.calib3d.Calib3d.calibrationMatrixValues</a>
 */
    public static void calibrationMatrixValues(Mat cameraMatrix, Size imageSize, double apertureWidth, double apertureHeight, double[] fovx, double[] fovy, double[] focalLength, Point principalPoint, double[] aspectRatio)
    {
        double[] fovx_out = new double[1];
        double[] fovy_out = new double[1];
        double[] focalLength_out = new double[1];
        double[] principalPoint_out = new double[2];
        double[] aspectRatio_out = new double[1];
        calibrationMatrixValues_0(cameraMatrix.nativeObj, imageSize.width, imageSize.height, apertureWidth, apertureHeight, fovx_out, fovy_out, focalLength_out, principalPoint_out, aspectRatio_out);
        if(fovx!=null) fovx[0] = (double)fovx_out[0];
        if(fovy!=null) fovy[0] = (double)fovy_out[0];
        if(focalLength!=null) focalLength[0] = (double)focalLength_out[0];
        if(principalPoint!=null){ principalPoint.x = principalPoint_out[0]; principalPoint.y = principalPoint_out[1]; }
        if(aspectRatio!=null) aspectRatio[0] = (double)aspectRatio_out[0];
        return;
    }


    //
    // C++:  void composeRT(Mat rvec1, Mat tvec1, Mat rvec2, Mat tvec2, Mat& rvec3, Mat& tvec3, Mat& dr3dr1 = Mat(), Mat& dr3dt1 = Mat(), Mat& dr3dr2 = Mat(), Mat& dr3dt2 = Mat(), Mat& dt3dr1 = Mat(), Mat& dt3dt1 = Mat(), Mat& dt3dr2 = Mat(), Mat& dt3dt2 = Mat())
    //

/**
 * <p>Combines two rotation-and-shift transformations.</p>
 *
 * <p>The functions compute:</p>
 *
 * <p><em>rvec3 = rodrigues ^(-1)(rodrigues(rvec2) * rodrigues(rvec1))
 * tvec3 = rodrigues(rvec2) * tvec1 + tvec2,</em></p>
 *
 * <p>where <em>rodrigues</em> denotes a rotation vector to a rotation matrix
 * transformation, and <em>rodrigues^(-1)</em> denotes the inverse
 * transformation. See "Rodrigues" for details.</p>
 *
 * <p>Also, the functions can compute the derivatives of the output vectors with
 * regards to the input vectors (see "matMulDeriv").
 * The functions are used inside "stereoCalibrate" but can also be used in your
 * own code where Levenberg-Marquardt or another gradient-based solver is used
 * to optimize a function that contains a matrix multiplication.</p>
 *
 * @param rvec1 First rotation vector.
 * @param tvec1 First translation vector.
 * @param rvec2 Second rotation vector.
 * @param tvec2 Second translation vector.
 * @param rvec3 Output rotation vector of the superposition.
 * @param tvec3 Output translation vector of the superposition.
 * @param dr3dr1 Optional output derivatives of <code>rvec3</code> or
 * <code>tvec3</code> with regard to <code>rvec1</code>, <code>rvec2</code>,
 * <code>tvec1</code> and <code>tvec2</code>, respectively.
 * @param dr3dt1 Optional output derivatives of <code>rvec3</code> or
 * <code>tvec3</code> with regard to <code>rvec1</code>, <code>rvec2</code>,
 * <code>tvec1</code> and <code>tvec2</code>, respectively.
 * @param dr3dr2 Optional output derivatives of <code>rvec3</code> or
 * <code>tvec3</code> with regard to <code>rvec1</code>, <code>rvec2</code>,
 * <code>tvec1</code> and <code>tvec2</code>, respectively.
 * @param dr3dt2 Optional output derivatives of <code>rvec3</code> or
 * <code>tvec3</code> with regard to <code>rvec1</code>, <code>rvec2</code>,
 * <code>tvec1</code> and <code>tvec2</code>, respectively.
 * @param dt3dr1 Optional output derivatives of <code>rvec3</code> or
 * <code>tvec3</code> with regard to <code>rvec1</code>, <code>rvec2</code>,
 * <code>tvec1</code> and <code>tvec2</code>, respectively.
 * @param dt3dt1 Optional output derivatives of <code>rvec3</code> or
 * <code>tvec3</code> with regard to <code>rvec1</code>, <code>rvec2</code>,
 * <code>tvec1</code> and <code>tvec2</code>, respectively.
 * @param dt3dr2 Optional output derivatives of <code>rvec3</code> or
 * <code>tvec3</code> with regard to <code>rvec1</code>, <code>rvec2</code>,
 * <code>tvec1</code> and <code>tvec2</code>, respectively.
 * @param dt3dt2 Optional output derivatives of <code>rvec3</code> or
 * <code>tvec3</code> with regard to <code>rvec1</code>, <code>rvec2</code>,
 * <code>tvec1</code> and <code>tvec2</code>, respectively.
 *
 * @see <a href="http://docs.opencv.org/modules/calib3d/doc/camera_calibration_and_3d_reconstruction.html#composert">org.opencv.calib3d.Calib3d.composeRT</a>
 */
    public static void composeRT(Mat rvec1, Mat tvec1, Mat rvec2, Mat tvec2, Mat rvec3, Mat tvec3, Mat dr3dr1, Mat dr3dt1, Mat dr3dr2, Mat dr3dt2, Mat dt3dr1, Mat dt3dt1, Mat dt3dr2, Mat dt3dt2)
    {

        composeRT_0(rvec1.nativeObj, tvec1.nativeObj, rvec2.nativeObj, tvec2.nativeObj, rvec3.nativeObj, tvec3.nativeObj, dr3dr1.nativeObj, dr3dt1.nativeObj, dr3dr2.nativeObj, dr3dt2.nativeObj, dt3dr1.nativeObj, dt3dt1.nativeObj, dt3dr2.nativeObj, dt3dt2.nativeObj);

        return;
    }

/**
 * <p>Combines two rotation-and-shift transformations.</p>
 *
 * <p>The functions compute:</p>
 *
 * <p><em>rvec3 = rodrigues ^(-1)(rodrigues(rvec2) * rodrigues(rvec1))
 * tvec3 = rodrigues(rvec2) * tvec1 + tvec2,</em></p>
 *
 * <p>where <em>rodrigues</em> denotes a rotation vector to a rotation matrix
 * transformation, and <em>rodrigues^(-1)</em> denotes the inverse
 * transformation. See "Rodrigues" for details.</p>
 *
 * <p>Also, the functions can compute the derivatives of the output vectors with
 * regards to the input vectors (see "matMulDeriv").
 * The functions are used inside "stereoCalibrate" but can also be used in your
 * own code where Levenberg-Marquardt or another gradient-based solver is used
 * to optimize a function that contains a matrix multiplication.</p>
 *
 * @param rvec1 First rotation vector.
 * @param tvec1 First translation vector.
 * @param rvec2 Second rotation vector.
 * @param tvec2 Second translation vector.
 * @param rvec3 Output rotation vector of the superposition.
 * @param tvec3 Output translation vector of the superposition.
 *
 * @see <a href="http://docs.opencv.org/modules/calib3d/doc/camera_calibration_and_3d_reconstruction.html#composert">org.opencv.calib3d.Calib3d.composeRT</a>
 */
    public static void composeRT(Mat rvec1, Mat tvec1, Mat rvec2, Mat tvec2, Mat rvec3, Mat tvec3)
    {

        composeRT_1(rvec1.nativeObj, tvec1.nativeObj, rvec2.nativeObj, tvec2.nativeObj, rvec3.nativeObj, tvec3.nativeObj);

        return;
    }


    //
    // C++:  void computeCorrespondEpilines(Mat points, int whichImage, Mat F, Mat& lines)
    //

/**
 * <p>For points in an image of a stereo pair, computes the corresponding epilines
 * in the other image.</p>
 *
 * <p>For every point in one of the two images of a stereo pair, the function finds
 * the equation of the corresponding epipolar line in the other image.</p>
 *
 * <p>From the fundamental matrix definition (see "findFundamentalMat"), line
 * <em>l^2_i</em> in the second image for the point <em>p^1_i</em> in the first
 * image (when <code>whichImage=1</code>) is computed as:</p>
 *
 * <p><em>l^2_i = F p^1_i</em></p>
 *
 * <p>And vice versa, when <code>whichImage=2</code>, <em>l^1_i</em> is computed
 * from <em>p^2_i</em> as:</p>
 *
 * <p><em>l^1_i = F^T p^2_i</em></p>
 *
 * <p>Line coefficients are defined up to a scale. They are normalized so that
 * <em>a_i^2+b_i^2=1</em>.</p>
 *
 * @param points Input points. <em>N x 1</em> or <em>1 x N</em> matrix of type
 * <code>CV_32FC2</code> or <code>vector<Point2f></code>.
 * @param whichImage Index of the image (1 or 2) that contains the
 * <code>points</code>.
 * @param F Fundamental matrix that can be estimated using "findFundamentalMat"
 * or "stereoRectify".
 * @param lines Output vector of the epipolar lines corresponding to the points
 * in the other image. Each line <em>ax + by + c=0</em> is encoded by 3 numbers
 * <em>(a, b, c)</em>.
 *
 * @see <a href="http://docs.opencv.org/modules/calib3d/doc/camera_calibration_and_3d_reconstruction.html#computecorrespondepilines">org.opencv.calib3d.Calib3d.computeCorrespondEpilines</a>
 */
    public static void computeCorrespondEpilines(Mat points, int whichImage, Mat F, Mat lines)
    {

        computeCorrespondEpilines_0(points.nativeObj, whichImage, F.nativeObj, lines.nativeObj);

        return;
    }


    //
    // C++:  void convertPointsFromHomogeneous(Mat src, Mat& dst)
    //

/**
 * <p>Converts points from homogeneous to Euclidean space.</p>
 *
 * <p>The function converts points homogeneous to Euclidean space using perspective
 * projection. That is, each point <code>(x1, x2,... x(n-1), xn)</code> is
 * converted to <code>(x1/xn, x2/xn,..., x(n-1)/xn)</code>. When
 * <code>xn=0</code>, the output point coordinates will be <code>(0,0,0,...)</code>.</p>
 *
 * @param src Input vector of <code>N</code>-dimensional points.
 * @param dst Output vector of <code>N-1</code>-dimensional points.
 *
 * @see <a href="http://docs.opencv.org/modules/calib3d/doc/camera_calibration_and_3d_reconstruction.html#convertpointsfromhomogeneous">org.opencv.calib3d.Calib3d.convertPointsFromHomogeneous</a>
 */
    public static void convertPointsFromHomogeneous(Mat src, Mat dst)
    {

        convertPointsFromHomogeneous_0(src.nativeObj, dst.nativeObj);

        return;
    }


    //
    // C++:  void convertPointsToHomogeneous(Mat src, Mat& dst)
    //

/**
 * <p>Converts points from Euclidean to homogeneous space.</p>
 *
 * <p>The function converts points from Euclidean to homogeneous space by appending
 * 1's to the tuple of point coordinates. That is, each point <code>(x1, x2,...,
 * xn)</code> is converted to <code>(x1, x2,..., xn, 1)</code>.</p>
 *
 * @param src Input vector of <code>N</code>-dimensional points.
 * @param dst Output vector of <code>N+1</code>-dimensional points.
 *
 * @see <a href="http://docs.opencv.org/modules/calib3d/doc/camera_calibration_and_3d_reconstruction.html#convertpointstohomogeneous">org.opencv.calib3d.Calib3d.convertPointsToHomogeneous</a>
 */
    public static void convertPointsToHomogeneous(Mat src, Mat dst)
    {

        convertPointsToHomogeneous_0(src.nativeObj, dst.nativeObj);

        return;
    }


    //
    // C++:  void correctMatches(Mat F, Mat points1, Mat points2, Mat& newPoints1, Mat& newPoints2)
    //

/**
 * <p>Refines coordinates of corresponding points.</p>
 *
 * <p>The function implements the Optimal Triangulation Method (see Multiple View
 * Geometry for details). For each given point correspondence points1[i] <->
 * points2[i], and a fundamental matrix F, it computes the corrected
 * correspondences newPoints1[i] <-> newPoints2[i] that minimize the geometric
 * error <em>d(points1[i], newPoints1[i])^2 + d(points2[i],newPoints2[i])^2</em>
 * (where <em>d(a,b)</em> is the geometric distance between points <em>a</em>
 * and <em>b</em>) subject to the epipolar constraint <em>newPoints2^T * F *
 * newPoints1 = 0</em>.</p>
 *
 * @param F 3x3 fundamental matrix.
 * @param points1 1xN array containing the first set of points.
 * @param points2 1xN array containing the second set of points.
 * @param newPoints1 The optimized points1.
 * @param newPoints2 The optimized points2.
 *
 * @see <a href="http://docs.opencv.org/modules/calib3d/doc/camera_calibration_and_3d_reconstruction.html#correctmatches">org.opencv.calib3d.Calib3d.correctMatches</a>
 */
    public static void correctMatches(Mat F, Mat points1, Mat points2, Mat newPoints1, Mat newPoints2)
    {

        correctMatches_0(F.nativeObj, points1.nativeObj, points2.nativeObj, newPoints1.nativeObj, newPoints2.nativeObj);

        return;
    }


    //
    // C++:  void decomposeProjectionMatrix(Mat projMatrix, Mat& cameraMatrix, Mat& rotMatrix, Mat& transVect, Mat& rotMatrixX = Mat(), Mat& rotMatrixY = Mat(), Mat& rotMatrixZ = Mat(), Mat& eulerAngles = Mat())
    //

/**
 * <p>Decomposes a projection matrix into a rotation matrix and a camera matrix.</p>
 *
 * <p>The function computes a decomposition of a projection matrix into a
 * calibration and a rotation matrix and the position of a camera.</p>
 *
 * <p>It optionally returns three rotation matrices, one for each axis, and three
 * Euler angles that could be used in OpenGL. Note, there is always more than
 * one sequence of rotations about the three principle axes that results in the
 * same orientation of an object, eg. see [Slabaugh]. Returned tree rotation
 * matrices and corresponding three Euler angules are only one of the possible
 * solutions.</p>
 *
 * <p>The function is based on "RQDecomp3x3".</p>
 *
 * @param projMatrix 3x4 input projection matrix P.
 * @param cameraMatrix Output 3x3 camera matrix K.
 * @param rotMatrix Output 3x3 external rotation matrix R.
 * @param transVect Output 4x1 translation vector T.
 * @param rotMatrixX a rotMatrixX
 * @param rotMatrixY a rotMatrixY
 * @param rotMatrixZ a rotMatrixZ
 * @param eulerAngles Optional three-element vector containing three Euler
 * angles of rotation in degrees.
 *
 * @see <a href="http://docs.opencv.org/modules/calib3d/doc/camera_calibration_and_3d_reconstruction.html#decomposeprojectionmatrix">org.opencv.calib3d.Calib3d.decomposeProjectionMatrix</a>
 */
    public static void decomposeProjectionMatrix(Mat projMatrix, Mat cameraMatrix, Mat rotMatrix, Mat transVect, Mat rotMatrixX, Mat rotMatrixY, Mat rotMatrixZ, Mat eulerAngles)
    {

        decomposeProjectionMatrix_0(projMatrix.nativeObj, cameraMatrix.nativeObj, rotMatrix.nativeObj, transVect.nativeObj, rotMatrixX.nativeObj, rotMatrixY.nativeObj, rotMatrixZ.nativeObj, eulerAngles.nativeObj);

        return;
    }

/**
 * <p>Decomposes a projection matrix into a rotation matrix and a camera matrix.</p>
 *
 * <p>The function computes a decomposition of a projection matrix into a
 * calibration and a rotation matrix and the position of a camera.</p>
 *
 * <p>It optionally returns three rotation matrices, one for each axis, and three
 * Euler angles that could be used in OpenGL. Note, there is always more than
 * one sequence of rotations about the three principle axes that results in the
 * same orientation of an object, eg. see [Slabaugh]. Returned tree rotation
 * matrices and corresponding three Euler angules are only one of the possible
 * solutions.</p>
 *
 * <p>The function is based on "RQDecomp3x3".</p>
 *
 * @param projMatrix 3x4 input projection matrix P.
 * @param cameraMatrix Output 3x3 camera matrix K.
 * @param rotMatrix Output 3x3 external rotation matrix R.
 * @param transVect Output 4x1 translation vector T.
 *
 * @see <a href="http://docs.opencv.org/modules/calib3d/doc/camera_calibration_and_3d_reconstruction.html#decomposeprojectionmatrix">org.opencv.calib3d.Calib3d.decomposeProjectionMatrix</a>
 */
    public static void decomposeProjectionMatrix(Mat projMatrix, Mat cameraMatrix, Mat rotMatrix, Mat transVect)
    {

        decomposeProjectionMatrix_1(projMatrix.nativeObj, cameraMatrix.nativeObj, rotMatrix.nativeObj, transVect.nativeObj);

        return;
    }


    //
    // C++:  void drawChessboardCorners(Mat& image, Size patternSize, vector_Point2f corners, bool patternWasFound)
    //

/**
 * <p>Renders the detected chessboard corners.</p>
 *
 * <p>The function draws individual chessboard corners detected either as red
 * circles if the board was not found, or as colored corners connected with
 * lines if the board was found.</p>
 *
 * @param image Destination image. It must be an 8-bit color image.
 * @param patternSize Number of inner corners per a chessboard row and column
 * <code>(patternSize = cv.Size(points_per_row,points_per_column))</code>.
 * @param corners Array of detected corners, the output of <code>findChessboardCorners</code>.
 * @param patternWasFound Parameter indicating whether the complete board was
 * found or not. The return value of "findChessboardCorners" should be passed
 * here.
 *
 * @see <a href="http://docs.opencv.org/modules/calib3d/doc/camera_calibration_and_3d_reconstruction.html#drawchessboardcorners">org.opencv.calib3d.Calib3d.drawChessboardCorners</a>
 */
    public static void drawChessboardCorners(Mat image, Size patternSize, MatOfPoint2f corners, boolean patternWasFound)
    {
        Mat corners_mat = corners;
        drawChessboardCorners_0(image.nativeObj, patternSize.width, patternSize.height, corners_mat.nativeObj, patternWasFound);

        return;
    }


    //
    // C++:  int estimateAffine3D(Mat src, Mat dst, Mat& out, Mat& inliers, double ransacThreshold = 3, double confidence = 0.99)
    //

/**
 * <p>Computes an optimal affine transformation between two 3D point sets.</p>
 *
 * <p>The function estimates an optimal 3D affine transformation between two 3D
 * point sets using the RANSAC algorithm.</p>
 *
 * @param src First input 3D point set.
 * @param dst Second input 3D point set.
 * @param out Output 3D affine transformation matrix <em>3 x 4</em>.
 * @param inliers Output vector indicating which points are inliers.
 * @param ransacThreshold Maximum reprojection error in the RANSAC algorithm to
 * consider a point as an inlier.
 * @param confidence Confidence level, between 0 and 1, for the estimated
 * transformation. Anything between 0.95 and 0.99 is usually good enough. Values
 * too close to 1 can slow down the estimation significantly. Values lower than
 * 0.8-0.9 can result in an incorrectly estimated transformation.
 *
 * @see <a href="http://docs.opencv.org/modules/calib3d/doc/camera_calibration_and_3d_reconstruction.html#estimateaffine3d">org.opencv.calib3d.Calib3d.estimateAffine3D</a>
 */
    public static int estimateAffine3D(Mat src, Mat dst, Mat out, Mat inliers, double ransacThreshold, double confidence)
    {

        int retVal = estimateAffine3D_0(src.nativeObj, dst.nativeObj, out.nativeObj, inliers.nativeObj, ransacThreshold, confidence);

        return retVal;
    }

/**
 * <p>Computes an optimal affine transformation between two 3D point sets.</p>
 *
 * <p>The function estimates an optimal 3D affine transformation between two 3D
 * point sets using the RANSAC algorithm.</p>
 *
 * @param src First input 3D point set.
 * @param dst Second input 3D point set.
 * @param out Output 3D affine transformation matrix <em>3 x 4</em>.
 * @param inliers Output vector indicating which points are inliers.
 *
 * @see <a href="http://docs.opencv.org/modules/calib3d/doc/camera_calibration_and_3d_reconstruction.html#estimateaffine3d">org.opencv.calib3d.Calib3d.estimateAffine3D</a>
 */
    public static int estimateAffine3D(Mat src, Mat dst, Mat out, Mat inliers)
    {

        int retVal = estimateAffine3D_1(src.nativeObj, dst.nativeObj, out.nativeObj, inliers.nativeObj);

        return retVal;
    }


    //
    // C++:  void filterSpeckles(Mat& img, double newVal, int maxSpeckleSize, double maxDiff, Mat& buf = Mat())
    //

/**
 * <p>Filters off small noise blobs (speckles) in the disparity map</p>
 *
 * @param img The input 16-bit signed disparity image
 * @param newVal The disparity value used to paint-off the speckles
 * @param maxSpeckleSize The maximum speckle size to consider it a speckle.
 * Larger blobs are not affected by the algorithm
 * @param maxDiff Maximum difference between neighbor disparity pixels to put
 * them into the same blob. Note that since StereoBM, StereoSGBM and may be
 * other algorithms return a fixed-point disparity map, where disparity values
 * are multiplied by 16, this scale factor should be taken into account when
 * specifying this parameter value.
 * @param buf The optional temporary buffer to avoid memory allocation within
 * the function.
 *
 * @see <a href="http://docs.opencv.org/modules/calib3d/doc/camera_calibration_and_3d_reconstruction.html#filterspeckles">org.opencv.calib3d.Calib3d.filterSpeckles</a>
 */
    public static void filterSpeckles(Mat img, double newVal, int maxSpeckleSize, double maxDiff, Mat buf)
    {

        filterSpeckles_0(img.nativeObj, newVal, maxSpeckleSize, maxDiff, buf.nativeObj);

        return;
    }

/**
 * <p>Filters off small noise blobs (speckles) in the disparity map</p>
 *
 * @param img The input 16-bit signed disparity image
 * @param newVal The disparity value used to paint-off the speckles
 * @param maxSpeckleSize The maximum speckle size to consider it a speckle.
 * Larger blobs are not affected by the algorithm
 * @param maxDiff Maximum difference between neighbor disparity pixels to put
 * them into the same blob. Note that since StereoBM, StereoSGBM and may be
 * other algorithms return a fixed-point disparity map, where disparity values
 * are multiplied by 16, this scale factor should be taken into account when
 * specifying this parameter value.
 *
 * @see <a href="http://docs.opencv.org/modules/calib3d/doc/camera_calibration_and_3d_reconstruction.html#filterspeckles">org.opencv.calib3d.Calib3d.filterSpeckles</a>
 */
    public static void filterSpeckles(Mat img, double newVal, int maxSpeckleSize, double maxDiff)
    {

        filterSpeckles_1(img.nativeObj, newVal, maxSpeckleSize, maxDiff);

        return;
    }


    //
    // C++:  bool findChessboardCorners(Mat image, Size patternSize, vector_Point2f& corners, int flags = CALIB_CB_ADAPTIVE_THRESH+CALIB_CB_NORMALIZE_IMAGE)
    //

/**
 * <p>Finds the positions of internal corners of the chessboard.</p>
 *
 * <p>The function attempts to determine whether the input image is a view of the
 * chessboard pattern and locate the internal chessboard corners. The function
 * returns a non-zero value if all of the corners are found and they are placed
 * in a certain order (row by row, left to right in every row). Otherwise, if
 * the function fails to find all the corners or reorder them, it returns 0. For
 * example, a regular chessboard has 8 x 8 squares and 7 x 7 internal corners,
 * that is, points where the black squares touch each other.
 * The detected coordinates are approximate, and to determine their positions
 * more accurately, the function calls "cornerSubPix".
 * You also may use the function "cornerSubPix" with different parameters if
 * returned coordinates are not accurate enough.
 * Sample usage of detecting and drawing chessboard corners: <code></p>
 *
 * <p>// C++ code:</p>
 *
 * <p>Size patternsize(8,6); //interior number of corners</p>
 *
 * <p>Mat gray =....; //source image</p>
 *
 * <p>vector<Point2f> corners; //this will be filled by the detected corners</p>
 *
 * <p>//CALIB_CB_FAST_CHECK saves a lot of time on images</p>
 *
 * <p>//that do not contain any chessboard corners</p>
 *
 * <p>bool patternfound = findChessboardCorners(gray, patternsize, corners,</p>
 *
 * <p>CALIB_CB_ADAPTIVE_THRESH + CALIB_CB_NORMALIZE_IMAGE</p>
 *
 * <p>+ CALIB_CB_FAST_CHECK);</p>
 *
 * <p>if(patternfound)</p>
 *
 * <p>cornerSubPix(gray, corners, Size(11, 11), Size(-1, -1),</p>
 *
 * <p>TermCriteria(CV_TERMCRIT_EPS + CV_TERMCRIT_ITER, 30, 0.1));</p>
 *
 * <p>drawChessboardCorners(img, patternsize, Mat(corners), patternfound);</p>
 *
 * <p>Note: The function requires white space (like a square-thick border, the
 * wider the better) around the board to make the detection more robust in
 * various environments. Otherwise, if there is no border and the background is
 * dark, the outer black squares cannot be segmented properly and so the square
 * grouping and ordering algorithm fails.
 * </code></p>
 *
 * @param image Source chessboard view. It must be an 8-bit grayscale or color
 * image.
 * @param patternSize Number of inner corners per a chessboard row and column
 * <code>(patternSize = cvSize(points_per_row,points_per_colum) =
 * cvSize(columns,rows))</code>.
 * @param corners Output array of detected corners.
 * @param flags Various operation flags that can be zero or a combination of the
 * following values:
 * <ul>
 *   <li> CALIB_CB_ADAPTIVE_THRESH Use adaptive thresholding to convert the
 * image to black and white, rather than a fixed threshold level (computed from
 * the average image brightness).
 *   <li> CALIB_CB_NORMALIZE_IMAGE Normalize the image gamma with "equalizeHist"
 * before applying fixed or adaptive thresholding.
 *   <li> CALIB_CB_FILTER_QUADS Use additional criteria (like contour area,
 * perimeter, square-like shape) to filter out false quads extracted at the
 * contour retrieval stage.
 *   <li> CALIB_CB_FAST_CHECK Run a fast check on the image that looks for
 * chessboard corners, and shortcut the call if none is found. This can
 * drastically speed up the call in the degenerate condition when no chessboard
 * is observed.
 * </ul>
 *
 * @see <a href="http://docs.opencv.org/modules/calib3d/doc/camera_calibration_and_3d_reconstruction.html#findchessboardcorners">org.opencv.calib3d.Calib3d.findChessboardCorners</a>
 */
    public static boolean findChessboardCorners(Mat image, Size patternSize, MatOfPoint2f corners, int flags)
    {
        Mat corners_mat = corners;
        boolean retVal = findChessboardCorners_0(image.nativeObj, patternSize.width, patternSize.height, corners_mat.nativeObj, flags);

        return retVal;
    }

/**
 * <p>Finds the positions of internal corners of the chessboard.</p>
 *
 * <p>The function attempts to determine whether the input image is a view of the
 * chessboard pattern and locate the internal chessboard corners. The function
 * returns a non-zero value if all of the corners are found and they are placed
 * in a certain order (row by row, left to right in every row). Otherwise, if
 * the function fails to find all the corners or reorder them, it returns 0. For
 * example, a regular chessboard has 8 x 8 squares and 7 x 7 internal corners,
 * that is, points where the black squares touch each other.
 * The detected coordinates are approximate, and to determine their positions
 * more accurately, the function calls "cornerSubPix".
 * You also may use the function "cornerSubPix" with different parameters if
 * returned coordinates are not accurate enough.
 * Sample usage of detecting and drawing chessboard corners: <code></p>
 *
 * <p>// C++ code:</p>
 *
 * <p>Size patternsize(8,6); //interior number of corners</p>
 *
 * <p>Mat gray =....; //source image</p>
 *
 * <p>vector<Point2f> corners; //this will be filled by the detected corners</p>
 *
 * <p>//CALIB_CB_FAST_CHECK saves a lot of time on images</p>
 *
 * <p>//that do not contain any chessboard corners</p>
 *
 * <p>bool patternfound = findChessboardCorners(gray, patternsize, corners,</p>
 *
 * <p>CALIB_CB_ADAPTIVE_THRESH + CALIB_CB_NORMALIZE_IMAGE</p>
 *
 * <p>+ CALIB_CB_FAST_CHECK);</p>
 *
 * <p>if(patternfound)</p>
 *
 * <p>cornerSubPix(gray, corners, Size(11, 11), Size(-1, -1),</p>
 *
 * <p>TermCriteria(CV_TERMCRIT_EPS + CV_TERMCRIT_ITER, 30, 0.1));</p>
 *
 * <p>drawChessboardCorners(img, patternsize, Mat(corners), patternfound);</p>
 *
 * <p>Note: The function requires white space (like a square-thick border, the
 * wider the better) around the board to make the detection more robust in
 * various environments. Otherwise, if there is no border and the background is
 * dark, the outer black squares cannot be segmented properly and so the square
 * grouping and ordering algorithm fails.
 * </code></p>
 *
 * @param image Source chessboard view. It must be an 8-bit grayscale or color
 * image.
 * @param patternSize Number of inner corners per a chessboard row and column
 * <code>(patternSize = cvSize(points_per_row,points_per_colum) =
 * cvSize(columns,rows))</code>.
 * @param corners Output array of detected corners.
 *
 * @see <a href="http://docs.opencv.org/modules/calib3d/doc/camera_calibration_and_3d_reconstruction.html#findchessboardcorners">org.opencv.calib3d.Calib3d.findChessboardCorners</a>
 */
    public static boolean findChessboardCorners(Mat image, Size patternSize, MatOfPoint2f corners)
    {
        Mat corners_mat = corners;
        boolean retVal = findChessboardCorners_1(image.nativeObj, patternSize.width, patternSize.height, corners_mat.nativeObj);

        return retVal;
    }


    //
    // C++:  bool findCirclesGrid(Mat image, Size patternSize, Mat& centers, int flags = CALIB_CB_SYMMETRIC_GRID, Ptr_FeatureDetector blobDetector = new SimpleBlobDetector())
    //

    // Unknown type 'Ptr_FeatureDetector' (I), skipping the function


    //
    // C++:  bool findCirclesGridDefault(Mat image, Size patternSize, Mat& centers, int flags = CALIB_CB_SYMMETRIC_GRID)
    //

    public static boolean findCirclesGridDefault(Mat image, Size patternSize, Mat centers, int flags)
    {

        boolean retVal = findCirclesGridDefault_0(image.nativeObj, patternSize.width, patternSize.height, centers.nativeObj, flags);

        return retVal;
    }

    public static boolean findCirclesGridDefault(Mat image, Size patternSize, Mat centers)
    {

        boolean retVal = findCirclesGridDefault_1(image.nativeObj, patternSize.width, patternSize.height, centers.nativeObj);

        return retVal;
    }


    //
    // C++:  Mat findFundamentalMat(vector_Point2f points1, vector_Point2f points2, int method = FM_RANSAC, double param1 = 3., double param2 = 0.99, Mat& mask = Mat())
    //

/**
 * <p>Calculates a fundamental matrix from the corresponding points in two images.</p>
 *
 * <p>The epipolar geometry is described by the following equation:</p>
 *
 * <p><em>[p_2; 1]^T F [p_1; 1] = 0</em></p>
 *
 * <p>where <em>F</em> is a fundamental matrix, <em>p_1</em> and <em>p_2</em> are
 * corresponding points in the first and the second images, respectively.</p>
 *
 * <p>The function calculates the fundamental matrix using one of four methods
 * listed above and returns the found fundamental matrix. Normally just one
 * matrix is found. But in case of the 7-point algorithm, the function may
 * return up to 3 solutions (<em>9 x 3</em> matrix that stores all 3 matrices
 * sequentially).</p>
 *
 * <p>The calculated fundamental matrix may be passed further to "computeCorrespondEpilines"
 * that finds the epipolar lines corresponding to the specified points. It can
 * also be passed to"stereoRectifyUncalibrated" to compute the rectification
 * transformation.
 * <code></p>
 *
 * <p>// C++ code:</p>
 *
 * <p>// Example. Estimation of fundamental matrix using the RANSAC algorithm</p>
 *
 * <p>int point_count = 100;</p>
 *
 * <p>vector<Point2f> points1(point_count);</p>
 *
 * <p>vector<Point2f> points2(point_count);</p>
 *
 * <p>// initialize the points here... * /</p>
 *
 * <p>for(int i = 0; i < point_count; i++)</p>
 *
 *
 * <p>points1[i] =...;</p>
 *
 * <p>points2[i] =...;</p>
 *
 *
 * <p>Mat fundamental_matrix =</p>
 *
 * <p>findFundamentalMat(points1, points2, FM_RANSAC, 3, 0.99);</p>
 *
 * @param points1 Array of <code>N</code> points from the first image. The point
 * coordinates should be floating-point (single or double precision).
 * @param points2 Array of the second image points of the same size and format
 * as <code>points1</code>.
 * @param method Method for computing a fundamental matrix.
 * <ul>
 *   <li> CV_FM_7POINT for a 7-point algorithm. <em>N = 7</em>
 *   <li> CV_FM_8POINT for an 8-point algorithm. <em>N >= 8</em>
 *   <li> CV_FM_RANSAC for the RANSAC algorithm. <em>N >= 8</em>
 *   <li> CV_FM_LMEDS for the LMedS algorithm. <em>N >= 8</em>
 * </ul>
 * @param param1 Parameter used for RANSAC. It is the maximum distance from a
 * point to an epipolar line in pixels, beyond which the point is considered an
 * outlier and is not used for computing the final fundamental matrix. It can be
 * set to something like 1-3, depending on the accuracy of the point
 * localization, image resolution, and the image noise.
 * @param param2 Parameter used for the RANSAC or LMedS methods only. It
 * specifies a desirable level of confidence (probability) that the estimated
 * matrix is correct.
 * @param mask Output array of N elements, every element of which is set to 0
 * for outliers and to 1 for the other points. The array is computed only in the
 * RANSAC and LMedS methods. For other methods, it is set to all 1's.
 *
 * @see <a href="http://docs.opencv.org/modules/calib3d/doc/camera_calibration_and_3d_reconstruction.html#findfundamentalmat">org.opencv.calib3d.Calib3d.findFundamentalMat</a>
 */
    public static Mat findFundamentalMat(MatOfPoint2f points1, MatOfPoint2f points2, int method, double param1, double param2, Mat mask)
    {
        Mat points1_mat = points1;
        Mat points2_mat = points2;
        Mat retVal = new Mat(findFundamentalMat_0(points1_mat.nativeObj, points2_mat.nativeObj, method, param1, param2, mask.nativeObj));

        return retVal;
    }

/**
 * <p>Calculates a fundamental matrix from the corresponding points in two images.</p>
 *
 * <p>The epipolar geometry is described by the following equation:</p>
 *
 * <p><em>[p_2; 1]^T F [p_1; 1] = 0</em></p>
 *
 * <p>where <em>F</em> is a fundamental matrix, <em>p_1</em> and <em>p_2</em> are
 * corresponding points in the first and the second images, respectively.</p>
 *
 * <p>The function calculates the fundamental matrix using one of four methods
 * listed above and returns the found fundamental matrix. Normally just one
 * matrix is found. But in case of the 7-point algorithm, the function may
 * return up to 3 solutions (<em>9 x 3</em> matrix that stores all 3 matrices
 * sequentially).</p>
 *
 * <p>The calculated fundamental matrix may be passed further to "computeCorrespondEpilines"
 * that finds the epipolar lines corresponding to the specified points. It can
 * also be passed to"stereoRectifyUncalibrated" to compute the rectification
 * transformation.
 * <code></p>
 *
 * <p>// C++ code:</p>
 *
 * <p>// Example. Estimation of fundamental matrix using the RANSAC algorithm</p>
 *
 * <p>int point_count = 100;</p>
 *
 * <p>vector<Point2f> points1(point_count);</p>
 *
 * <p>vector<Point2f> points2(point_count);</p>
 *
 * <p>// initialize the points here... * /</p>
 *
 * <p>for(int i = 0; i < point_count; i++)</p>
 *
 *
 * <p>points1[i] =...;</p>
 *
 * <p>points2[i] =...;</p>
 *
 *
 * <p>Mat fundamental_matrix =</p>
 *
 * <p>findFundamentalMat(points1, points2, FM_RANSAC, 3, 0.99);</p>
 *
 * @param points1 Array of <code>N</code> points from the first image. The point
 * coordinates should be floating-point (single or double precision).
 * @param points2 Array of the second image points of the same size and format
 * as <code>points1</code>.
 * @param method Method for computing a fundamental matrix.
 * <ul>
 *   <li> CV_FM_7POINT for a 7-point algorithm. <em>N = 7</em>
 *   <li> CV_FM_8POINT for an 8-point algorithm. <em>N >= 8</em>
 *   <li> CV_FM_RANSAC for the RANSAC algorithm. <em>N >= 8</em>
 *   <li> CV_FM_LMEDS for the LMedS algorithm. <em>N >= 8</em>
 * </ul>
 * @param param1 Parameter used for RANSAC. It is the maximum distance from a
 * point to an epipolar line in pixels, beyond which the point is considered an
 * outlier and is not used for computing the final fundamental matrix. It can be
 * set to something like 1-3, depending on the accuracy of the point
 * localization, image resolution, and the image noise.
 * @param param2 Parameter used for the RANSAC or LMedS methods only. It
 * specifies a desirable level of confidence (probability) that the estimated
 * matrix is correct.
 *
 * @see <a href="http://docs.opencv.org/modules/calib3d/doc/camera_calibration_and_3d_reconstruction.html#findfundamentalmat">org.opencv.calib3d.Calib3d.findFundamentalMat</a>
 */
    public static Mat findFundamentalMat(MatOfPoint2f points1, MatOfPoint2f points2, int method, double param1, double param2)
    {
        Mat points1_mat = points1;
        Mat points2_mat = points2;
        Mat retVal = new Mat(findFundamentalMat_1(points1_mat.nativeObj, points2_mat.nativeObj, method, param1, param2));

        return retVal;
    }

/**
 * <p>Calculates a fundamental matrix from the corresponding points in two images.</p>
 *
 * <p>The epipolar geometry is described by the following equation:</p>
 *
 * <p><em>[p_2; 1]^T F [p_1; 1] = 0</em></p>
 *
 * <p>where <em>F</em> is a fundamental matrix, <em>p_1</em> and <em>p_2</em> are
 * corresponding points in the first and the second images, respectively.</p>
 *
 * <p>The function calculates the fundamental matrix using one of four methods
 * listed above and returns the found fundamental matrix. Normally just one
 * matrix is found. But in case of the 7-point algorithm, the function may
 * return up to 3 solutions (<em>9 x 3</em> matrix that stores all 3 matrices
 * sequentially).</p>
 *
 * <p>The calculated fundamental matrix may be passed further to "computeCorrespondEpilines"
 * that finds the epipolar lines corresponding to the specified points. It can
 * also be passed to"stereoRectifyUncalibrated" to compute the rectification
 * transformation.
 * <code></p>
 *
 * <p>// C++ code:</p>
 *
 * <p>// Example. Estimation of fundamental matrix using the RANSAC algorithm</p>
 *
 * <p>int point_count = 100;</p>
 *
 * <p>vector<Point2f> points1(point_count);</p>
 *
 * <p>vector<Point2f> points2(point_count);</p>
 *
 * <p>// initialize the points here... * /</p>
 *
 * <p>for(int i = 0; i < point_count; i++)</p>
 *
 *
 * <p>points1[i] =...;</p>
 *
 * <p>points2[i] =...;</p>
 *
 *
 * <p>Mat fundamental_matrix =</p>
 *
 * <p>findFundamentalMat(points1, points2, FM_RANSAC, 3, 0.99);</p>
 *
 * @param points1 Array of <code>N</code> points from the first image. The point
 * coordinates should be floating-point (single or double precision).
 * @param points2 Array of the second image points of the same size and format
 * as <code>points1</code>.
 *
 * @see <a href="http://docs.opencv.org/modules/calib3d/doc/camera_calibration_and_3d_reconstruction.html#findfundamentalmat">org.opencv.calib3d.Calib3d.findFundamentalMat</a>
 */
    public static Mat findFundamentalMat(MatOfPoint2f points1, MatOfPoint2f points2)
    {
        Mat points1_mat = points1;
        Mat points2_mat = points2;
        Mat retVal = new Mat(findFundamentalMat_2(points1_mat.nativeObj, points2_mat.nativeObj));

        return retVal;
    }


    //
    // C++:  Mat findHomography(vector_Point2f srcPoints, vector_Point2f dstPoints, int method = 0, double ransacReprojThreshold = 3, Mat& mask = Mat())
    //

/**
 * <p>Finds a perspective transformation between two planes.</p>
 *
 * <p>The functions find and return the perspective transformation <em>H</em>
 * between the source and the destination planes:</p>
 *
 * <p><em>s_i [x'_i y'_i 1] ~ H [x_i y_i 1]</em></p>
 *
 * <p>so that the back-projection error</p>
 *
 * <p><em>sum _i(x'_i- (h_11 x_i + h_12 y_i + h_13)/(h_(31) x_i + h_32 y_i +
 * h_33))^2+ (y'_i- (h_21 x_i + h_22 y_i + h_23)/(h_(31) x_i + h_32 y_i +
 * h_33))^2</em></p>
 *
 * <p>is minimized. If the parameter <code>method</code> is set to the default
 * value 0, the function uses all the point pairs to compute an initial
 * homography estimate with a simple least-squares scheme.</p>
 *
 * <p>However, if not all of the point pairs (<em>srcPoints_i</em>,
 * <em>dstPoints_i</em>) fit the rigid perspective transformation (that is,
 * there are some outliers), this initial estimate will be poor.
 * In this case, you can use one of the two robust methods. Both methods,
 * <code>RANSAC</code> and <code>LMeDS</code>, try many different random subsets
 * of the corresponding point pairs (of four pairs each), estimate the
 * homography matrix using this subset and a simple least-square algorithm, and
 * then compute the quality/goodness of the computed homography (which is the
 * number of inliers for RANSAC or the median re-projection error for LMeDs).
 * The best subset is then used to produce the initial estimate of the
 * homography matrix and the mask of inliers/outliers.</p>
 *
 * <p>Regardless of the method, robust or not, the computed homography matrix is
 * refined further (using inliers only in case of a robust method) with the
 * Levenberg-Marquardt method to reduce the re-projection error even more.</p>
 *
 * <p>The method <code>RANSAC</code> can handle practically any ratio of outliers
 * but it needs a threshold to distinguish inliers from outliers.
 * The method <code>LMeDS</code> does not need any threshold but it works
 * correctly only when there are more than 50% of inliers. Finally, if there are
 * no outliers and the noise is rather small, use the default method
 * (<code>method=0</code>).</p>
 *
 * <p>The function is used to find initial intrinsic and extrinsic matrices.
 * Homography matrix is determined up to a scale. Thus, it is normalized so that
 * <em>h_33=1</em>. Note that whenever an H matrix cannot be estimated, an empty
 * one will be returned.</p>
 *
 * <p>Note:</p>
 * <ul>
 *   <li> A example on calculating a homography for image matching can be found
 * at opencv_source_code/samples/cpp/video_homography.cpp
 * </ul>
 *
 * @param srcPoints Coordinates of the points in the original plane, a matrix of
 * the type <code>CV_32FC2</code> or <code>vector<Point2f></code>.
 * @param dstPoints Coordinates of the points in the target plane, a matrix of
 * the type <code>CV_32FC2</code> or a <code>vector<Point2f></code>.
 * @param method Method used to computed a homography matrix. The following
 * methods are possible:
 * <ul>
 *   <li> 0 - a regular method using all the points
 *   <li> CV_RANSAC - RANSAC-based robust method
 *   <li> CV_LMEDS - Least-Median robust method
 * </ul>
 * @param ransacReprojThreshold Maximum allowed reprojection error to treat a
 * point pair as an inlier (used in the RANSAC method only). That is, if
 *
 * <p><em>| dstPoints _i - convertPointsHomogeneous(H * srcPoints _i)| &gt
 * ransacReprojThreshold</em></p>
 *
 * <p>then the point <em>i</em> is considered an outlier. If <code>srcPoints</code>
 * and <code>dstPoints</code> are measured in pixels, it usually makes sense to
 * set this parameter somewhere in the range of 1 to 10.</p>
 * @param mask Optional output mask set by a robust method (<code>CV_RANSAC</code>
 * or <code>CV_LMEDS</code>). Note that the input mask values are ignored.
 *
 * @see <a href="http://docs.opencv.org/modules/calib3d/doc/camera_calibration_and_3d_reconstruction.html#findhomography">org.opencv.calib3d.Calib3d.findHomography</a>
 * @see org.opencv.imgproc.Imgproc#warpPerspective
 * @see org.opencv.core.Core#perspectiveTransform
 * @see org.opencv.video.Video#estimateRigidTransform
 * @see org.opencv.imgproc.Imgproc#getAffineTransform
 * @see org.opencv.imgproc.Imgproc#getPerspectiveTransform
 */
    public static Mat findHomography(MatOfPoint2f srcPoints, MatOfPoint2f dstPoints, int method, double ransacReprojThreshold, Mat mask)
    {
        Mat srcPoints_mat = srcPoints;
        Mat dstPoints_mat = dstPoints;
        Mat retVal = new Mat(findHomography_0(srcPoints_mat.nativeObj, dstPoints_mat.nativeObj, method, ransacReprojThreshold, mask.nativeObj));

        return retVal;
    }

/**
 * <p>Finds a perspective transformation between two planes.</p>
 *
 * <p>The functions find and return the perspective transformation <em>H</em>
 * between the source and the destination planes:</p>
 *
 * <p><em>s_i [x'_i y'_i 1] ~ H [x_i y_i 1]</em></p>
 *
 * <p>so that the back-projection error</p>
 *
 * <p><em>sum _i(x'_i- (h_11 x_i + h_12 y_i + h_13)/(h_(31) x_i + h_32 y_i +
 * h_33))^2+ (y'_i- (h_21 x_i + h_22 y_i + h_23)/(h_(31) x_i + h_32 y_i +
 * h_33))^2</em></p>
 *
 * <p>is minimized. If the parameter <code>method</code> is set to the default
 * value 0, the function uses all the point pairs to compute an initial
 * homography estimate with a simple least-squares scheme.</p>
 *
 * <p>However, if not all of the point pairs (<em>srcPoints_i</em>,
 * <em>dstPoints_i</em>) fit the rigid perspective transformation (that is,
 * there are some outliers), this initial estimate will be poor.
 * In this case, you can use one of the two robust methods. Both methods,
 * <code>RANSAC</code> and <code>LMeDS</code>, try many different random subsets
 * of the corresponding point pairs (of four pairs each), estimate the
 * homography matrix using this subset and a simple least-square algorithm, and
 * then compute the quality/goodness of the computed homography (which is the
 * number of inliers for RANSAC or the median re-projection error for LMeDs).
 * The best subset is then used to produce the initial estimate of the
 * homography matrix and the mask of inliers/outliers.</p>
 *
 * <p>Regardless of the method, robust or not, the computed homography matrix is
 * refined further (using inliers only in case of a robust method) with the
 * Levenberg-Marquardt method to reduce the re-projection error even more.</p>
 *
 * <p>The method <code>RANSAC</code> can handle practically any ratio of outliers
 * but it needs a threshold to distinguish inliers from outliers.
 * The method <code>LMeDS</code> does not need any threshold but it works
 * correctly only when there are more than 50% of inliers. Finally, if there are
 * no outliers and the noise is rather small, use the default method
 * (<code>method=0</code>).</p>
 *
 * <p>The function is used to find initial intrinsic and extrinsic matrices.
 * Homography matrix is determined up to a scale. Thus, it is normalized so that
 * <em>h_33=1</em>. Note that whenever an H matrix cannot be estimated, an empty
 * one will be returned.</p>
 *
 * <p>Note:</p>
 * <ul>
 *   <li> A example on calculating a homography for image matching can be found
 * at opencv_source_code/samples/cpp/video_homography.cpp
 * </ul>
 *
 * @param srcPoints Coordinates of the points in the original plane, a matrix of
 * the type <code>CV_32FC2</code> or <code>vector<Point2f></code>.
 * @param dstPoints Coordinates of the points in the target plane, a matrix of
 * the type <code>CV_32FC2</code> or a <code>vector<Point2f></code>.
 * @param method Method used to computed a homography matrix. The following
 * methods are possible:
 * <ul>
 *   <li> 0 - a regular method using all the points
 *   <li> CV_RANSAC - RANSAC-based robust method
 *   <li> CV_LMEDS - Least-Median robust method
 * </ul>
 * @param ransacReprojThreshold Maximum allowed reprojection error to treat a
 * point pair as an inlier (used in the RANSAC method only). That is, if
 *
 * <p><em>| dstPoints _i - convertPointsHomogeneous(H * srcPoints _i)| &gt
 * ransacReprojThreshold</em></p>
 *
 * <p>then the point <em>i</em> is considered an outlier. If <code>srcPoints</code>
 * and <code>dstPoints</code> are measured in pixels, it usually makes sense to
 * set this parameter somewhere in the range of 1 to 10.</p>
 *
 * @see <a href="http://docs.opencv.org/modules/calib3d/doc/camera_calibration_and_3d_reconstruction.html#findhomography">org.opencv.calib3d.Calib3d.findHomography</a>
 * @see org.opencv.imgproc.Imgproc#warpPerspective
 * @see org.opencv.core.Core#perspectiveTransform
 * @see org.opencv.video.Video#estimateRigidTransform
 * @see org.opencv.imgproc.Imgproc#getAffineTransform
 * @see org.opencv.imgproc.Imgproc#getPerspectiveTransform
 */
    public static Mat findHomography(MatOfPoint2f srcPoints, MatOfPoint2f dstPoints, int method, double ransacReprojThreshold)
    {
        Mat srcPoints_mat = srcPoints;
        Mat dstPoints_mat = dstPoints;
        Mat retVal = new Mat(findHomography_1(srcPoints_mat.nativeObj, dstPoints_mat.nativeObj, method, ransacReprojThreshold));

        return retVal;
    }

/**
 * <p>Finds a perspective transformation between two planes.</p>
 *
 * <p>The functions find and return the perspective transformation <em>H</em>
 * between the source and the destination planes:</p>
 *
 * <p><em>s_i [x'_i y'_i 1] ~ H [x_i y_i 1]</em></p>
 *
 * <p>so that the back-projection error</p>
 *
 * <p><em>sum _i(x'_i- (h_11 x_i + h_12 y_i + h_13)/(h_(31) x_i + h_32 y_i +
 * h_33))^2+ (y'_i- (h_21 x_i + h_22 y_i + h_23)/(h_(31) x_i + h_32 y_i +
 * h_33))^2</em></p>
 *
 * <p>is minimized. If the parameter <code>method</code> is set to the default
 * value 0, the function uses all the point pairs to compute an initial
 * homography estimate with a simple least-squares scheme.</p>
 *
 * <p>However, if not all of the point pairs (<em>srcPoints_i</em>,
 * <em>dstPoints_i</em>) fit the rigid perspective transformation (that is,
 * there are some outliers), this initial estimate will be poor.
 * In this case, you can use one of the two robust methods. Both methods,
 * <code>RANSAC</code> and <code>LMeDS</code>, try many different random subsets
 * of the corresponding point pairs (of four pairs each), estimate the
 * homography matrix using this subset and a simple least-square algorithm, and
 * then compute the quality/goodness of the computed homography (which is the
 * number of inliers for RANSAC or the median re-projection error for LMeDs).
 * The best subset is then used to produce the initial estimate of the
 * homography matrix and the mask of inliers/outliers.</p>
 *
 * <p>Regardless of the method, robust or not, the computed homography matrix is
 * refined further (using inliers only in case of a robust method) with the
 * Levenberg-Marquardt method to reduce the re-projection error even more.</p>
 *
 * <p>The method <code>RANSAC</code> can handle practically any ratio of outliers
 * but it needs a threshold to distinguish inliers from outliers.
 * The method <code>LMeDS</code> does not need any threshold but it works
 * correctly only when there are more than 50% of inliers. Finally, if there are
 * no outliers and the noise is rather small, use the default method
 * (<code>method=0</code>).</p>
 *
 * <p>The function is used to find initial intrinsic and extrinsic matrices.
 * Homography matrix is determined up to a scale. Thus, it is normalized so that
 * <em>h_33=1</em>. Note that whenever an H matrix cannot be estimated, an empty
 * one will be returned.</p>
 *
 * <p>Note:</p>
 * <ul>
 *   <li> A example on calculating a homography for image matching can be found
 * at opencv_source_code/samples/cpp/video_homography.cpp
 * </ul>
 *
 * @param srcPoints Coordinates of the points in the original plane, a matrix of
 * the type <code>CV_32FC2</code> or <code>vector<Point2f></code>.
 * @param dstPoints Coordinates of the points in the target plane, a matrix of
 * the type <code>CV_32FC2</code> or a <code>vector<Point2f></code>.
 *
 * @see <a href="http://docs.opencv.org/modules/calib3d/doc/camera_calibration_and_3d_reconstruction.html#findhomography">org.opencv.calib3d.Calib3d.findHomography</a>
 * @see org.opencv.imgproc.Imgproc#warpPerspective
 * @see org.opencv.core.Core#perspectiveTransform
 * @see org.opencv.video.Video#estimateRigidTransform
 * @see org.opencv.imgproc.Imgproc#getAffineTransform
 * @see org.opencv.imgproc.Imgproc#getPerspectiveTransform
 */
    public static Mat findHomography(MatOfPoint2f srcPoints, MatOfPoint2f dstPoints)
    {
        Mat srcPoints_mat = srcPoints;
        Mat dstPoints_mat = dstPoints;
        Mat retVal = new Mat(findHomography_2(srcPoints_mat.nativeObj, dstPoints_mat.nativeObj));

        return retVal;
    }


    //
    // C++:  Mat getOptimalNewCameraMatrix(Mat cameraMatrix, Mat distCoeffs, Size imageSize, double alpha, Size newImgSize = Size(), Rect* validPixROI = 0, bool centerPrincipalPoint = false)
    //

/**
 * <p>Returns the new camera matrix based on the free scaling parameter.</p>
 *
 * <p>The function computes and returns the optimal new camera matrix based on the
 * free scaling parameter. By varying this parameter, you may retrieve only
 * sensible pixels <code>alpha=0</code>, keep all the original image pixels if
 * there is valuable information in the corners <code>alpha=1</code>, or get
 * something in between. When <code>alpha>0</code>, the undistortion result is
 * likely to have some black pixels corresponding to "virtual" pixels outside of
 * the captured distorted image. The original camera matrix, distortion
 * coefficients, the computed new camera matrix, and <code>newImageSize</code>
 * should be passed to "initUndistortRectifyMap" to produce the maps for
 * "remap".</p>
 *
 * @param cameraMatrix Input camera matrix.
 * @param distCoeffs Input vector of distortion coefficients <em>(k_1, k_2, p_1,
 * p_2[, k_3[, k_4, k_5, k_6]])</em> of 4, 5, or 8 elements. If the vector is
 * NULL/empty, the zero distortion coefficients are assumed.
 * @param imageSize Original image size.
 * @param alpha Free scaling parameter between 0 (when all the pixels in the
 * undistorted image are valid) and 1 (when all the source image pixels are
 * retained in the undistorted image). See "stereoRectify" for details.
 * @param newImgSize a newImgSize
 * @param validPixROI Optional output rectangle that outlines all-good-pixels
 * region in the undistorted image. See <code>roi1, roi2</code> description in
 * "stereoRectify".
 * @param centerPrincipalPoint Optional flag that indicates whether in the new
 * camera matrix the principal point should be at the image center or not. By
 * default, the principal point is chosen to best fit a subset of the source
 * image (determined by <code>alpha</code>) to the corrected image.
 *
 * @see <a href="http://docs.opencv.org/modules/calib3d/doc/camera_calibration_and_3d_reconstruction.html#getoptimalnewcameramatrix">org.opencv.calib3d.Calib3d.getOptimalNewCameraMatrix</a>
 */
    public static Mat getOptimalNewCameraMatrix(Mat cameraMatrix, Mat distCoeffs, Size imageSize, double alpha, Size newImgSize, Rect validPixROI, boolean centerPrincipalPoint)
    {
        double[] validPixROI_out = new double[4];
        Mat retVal = new Mat(getOptimalNewCameraMatrix_0(cameraMatrix.nativeObj, distCoeffs.nativeObj, imageSize.width, imageSize.height, alpha, newImgSize.width, newImgSize.height, validPixROI_out, centerPrincipalPoint));
        if(validPixROI!=null){ validPixROI.x = (int)validPixROI_out[0]; validPixROI.y = (int)validPixROI_out[1]; validPixROI.width = (int)validPixROI_out[2]; validPixROI.height = (int)validPixROI_out[3]; }
        return retVal;
    }

/**
 * <p>Returns the new camera matrix based on the free scaling parameter.</p>
 *
 * <p>The function computes and returns the optimal new camera matrix based on the
 * free scaling parameter. By varying this parameter, you may retrieve only
 * sensible pixels <code>alpha=0</code>, keep all the original image pixels if
 * there is valuable information in the corners <code>alpha=1</code>, or get
 * something in between. When <code>alpha>0</code>, the undistortion result is
 * likely to have some black pixels corresponding to "virtual" pixels outside of
 * the captured distorted image. The original camera matrix, distortion
 * coefficients, the computed new camera matrix, and <code>newImageSize</code>
 * should be passed to "initUndistortRectifyMap" to produce the maps for
 * "remap".</p>
 *
 * @param cameraMatrix Input camera matrix.
 * @param distCoeffs Input vector of distortion coefficients <em>(k_1, k_2, p_1,
 * p_2[, k_3[, k_4, k_5, k_6]])</em> of 4, 5, or 8 elements. If the vector is
 * NULL/empty, the zero distortion coefficients are assumed.
 * @param imageSize Original image size.
 * @param alpha Free scaling parameter between 0 (when all the pixels in the
 * undistorted image are valid) and 1 (when all the source image pixels are
 * retained in the undistorted image). See "stereoRectify" for details.
 *
 * @see <a href="http://docs.opencv.org/modules/calib3d/doc/camera_calibration_and_3d_reconstruction.html#getoptimalnewcameramatrix">org.opencv.calib3d.Calib3d.getOptimalNewCameraMatrix</a>
 */
    public static Mat getOptimalNewCameraMatrix(Mat cameraMatrix, Mat distCoeffs, Size imageSize, double alpha)
    {

        Mat retVal = new Mat(getOptimalNewCameraMatrix_1(cameraMatrix.nativeObj, distCoeffs.nativeObj, imageSize.width, imageSize.height, alpha));

        return retVal;
    }


    //
    // C++:  Rect getValidDisparityROI(Rect roi1, Rect roi2, int minDisparity, int numberOfDisparities, int SADWindowSize)
    //

    public static Rect getValidDisparityROI(Rect roi1, Rect roi2, int minDisparity, int numberOfDisparities, int SADWindowSize)
    {

        Rect retVal = new Rect(getValidDisparityROI_0(roi1.x, roi1.y, roi1.width, roi1.height, roi2.x, roi2.y, roi2.width, roi2.height, minDisparity, numberOfDisparities, SADWindowSize));

        return retVal;
    }


    //
    // C++:  Mat initCameraMatrix2D(vector_vector_Point3f objectPoints, vector_vector_Point2f imagePoints, Size imageSize, double aspectRatio = 1.)
    //

/**
 * <p>Finds an initial camera matrix from 3D-2D point correspondences.</p>
 *
 * <p>The function estimates and returns an initial camera matrix for the camera
 * calibration process.
 * Currently, the function only supports planar calibration patterns, which are
 * patterns where each object point has z-coordinate =0.</p>
 *
 * @param objectPoints Vector of vectors of the calibration pattern points in
 * the calibration pattern coordinate space. In the old interface all the
 * per-view vectors are concatenated. See "calibrateCamera" for details.
 * @param imagePoints Vector of vectors of the projections of the calibration
 * pattern points. In the old interface all the per-view vectors are
 * concatenated.
 * @param imageSize Image size in pixels used to initialize the principal point.
 * @param aspectRatio If it is zero or negative, both <em>f_x</em> and
 * <em>f_y</em> are estimated independently. Otherwise, <em>f_x = f_y *
 * aspectRatio</em>.
 *
 * @see <a href="http://docs.opencv.org/modules/calib3d/doc/camera_calibration_and_3d_reconstruction.html#initcameramatrix2d">org.opencv.calib3d.Calib3d.initCameraMatrix2D</a>
 */
    public static Mat initCameraMatrix2D(List<MatOfPoint3f> objectPoints, List<MatOfPoint2f> imagePoints, Size imageSize, double aspectRatio)
    {
        List<Mat> objectPoints_tmplm = new ArrayList<Mat>((objectPoints != null) ? objectPoints.size() : 0);
        Mat objectPoints_mat = Converters.vector_vector_Point3f_to_Mat(objectPoints, objectPoints_tmplm);
        List<Mat> imagePoints_tmplm = new ArrayList<Mat>((imagePoints != null) ? imagePoints.size() : 0);
        Mat imagePoints_mat = Converters.vector_vector_Point2f_to_Mat(imagePoints, imagePoints_tmplm);
        Mat retVal = new Mat(initCameraMatrix2D_0(objectPoints_mat.nativeObj, imagePoints_mat.nativeObj, imageSize.width, imageSize.height, aspectRatio));

        return retVal;
    }

/**
 * <p>Finds an initial camera matrix from 3D-2D point correspondences.</p>
 *
 * <p>The function estimates and returns an initial camera matrix for the camera
 * calibration process.
 * Currently, the function only supports planar calibration patterns, which are
 * patterns where each object point has z-coordinate =0.</p>
 *
 * @param objectPoints Vector of vectors of the calibration pattern points in
 * the calibration pattern coordinate space. In the old interface all the
 * per-view vectors are concatenated. See "calibrateCamera" for details.
 * @param imagePoints Vector of vectors of the projections of the calibration
 * pattern points. In the old interface all the per-view vectors are
 * concatenated.
 * @param imageSize Image size in pixels used to initialize the principal point.
 *
 * @see <a href="http://docs.opencv.org/modules/calib3d/doc/camera_calibration_and_3d_reconstruction.html#initcameramatrix2d">org.opencv.calib3d.Calib3d.initCameraMatrix2D</a>
 */
    public static Mat initCameraMatrix2D(List<MatOfPoint3f> objectPoints, List<MatOfPoint2f> imagePoints, Size imageSize)
    {
        List<Mat> objectPoints_tmplm = new ArrayList<Mat>((objectPoints != null) ? objectPoints.size() : 0);
        Mat objectPoints_mat = Converters.vector_vector_Point3f_to_Mat(objectPoints, objectPoints_tmplm);
        List<Mat> imagePoints_tmplm = new ArrayList<Mat>((imagePoints != null) ? imagePoints.size() : 0);
        Mat imagePoints_mat = Converters.vector_vector_Point2f_to_Mat(imagePoints, imagePoints_tmplm);
        Mat retVal = new Mat(initCameraMatrix2D_1(objectPoints_mat.nativeObj, imagePoints_mat.nativeObj, imageSize.width, imageSize.height));

        return retVal;
    }


    //
    // C++:  void matMulDeriv(Mat A, Mat B, Mat& dABdA, Mat& dABdB)
    //

/**
 * <p>Computes partial derivatives of the matrix product for each multiplied
 * matrix.</p>
 *
 * <p>The function computes partial derivatives of the elements of the matrix
 * product <em>A*B</em> with regard to the elements of each of the two input
 * matrices. The function is used to compute the Jacobian matrices in
 * "stereoCalibrate" but can also be used in any other similar optimization
 * function.</p>
 *
 * @param A First multiplied matrix.
 * @param B Second multiplied matrix.
 * @param dABdA First output derivative matrix <code>d(A*B)/dA</code> of size
 * <em>A.rows*B.cols x (A.rows*A.cols)</em>.
 * @param dABdB Second output derivative matrix <code>d(A*B)/dB</code> of size
 * <em>A.rows*B.cols x (B.rows*B.cols)</em>.
 *
 * @see <a href="http://docs.opencv.org/modules/calib3d/doc/camera_calibration_and_3d_reconstruction.html#matmulderiv">org.opencv.calib3d.Calib3d.matMulDeriv</a>
 */
    public static void matMulDeriv(Mat A, Mat B, Mat dABdA, Mat dABdB)
    {

        matMulDeriv_0(A.nativeObj, B.nativeObj, dABdA.nativeObj, dABdB.nativeObj);

        return;
    }


    //
    // C++:  void projectPoints(vector_Point3f objectPoints, Mat rvec, Mat tvec, Mat cameraMatrix, vector_double distCoeffs, vector_Point2f& imagePoints, Mat& jacobian = Mat(), double aspectRatio = 0)
    //

/**
 * <p>Projects 3D points to an image plane.</p>
 *
 * <p>The function computes projections of 3D points to the image plane given
 * intrinsic and extrinsic camera parameters. Optionally, the function computes
 * Jacobians - matrices of partial derivatives of image points coordinates (as
 * functions of all the input parameters) with respect to the particular
 * parameters, intrinsic and/or extrinsic. The Jacobians are used during the
 * global optimization in "calibrateCamera", "solvePnP", and "stereoCalibrate".
 * The function itself can also be used to compute a re-projection error given
 * the current intrinsic and extrinsic parameters.</p>
 *
 * <p>Note: By setting <code>rvec=tvec=(0,0,0)</code> or by setting
 * <code>cameraMatrix</code> to a 3x3 identity matrix, or by passing zero
 * distortion coefficients, you can get various useful partial cases of the
 * function. This means that you can compute the distorted coordinates for a
 * sparse set of points or apply a perspective transformation (and also compute
 * the derivatives) in the ideal zero-distortion setup.</p>
 *
 * @param objectPoints Array of object points, 3xN/Nx3 1-channel or 1xN/Nx1
 * 3-channel (or <code>vector<Point3f></code>), where N is the number of points
 * in the view.
 * @param rvec Rotation vector. See "Rodrigues" for details.
 * @param tvec Translation vector.
 * @param cameraMatrix Camera matrix <em>A =
 * <p>|f_x 0 c_x|
 * |0 f_y c_y|
 * |0 0 _1|
 * </em>.</p>
 * @param distCoeffs Input vector of distortion coefficients <em>(k_1, k_2, p_1,
 * p_2[, k_3[, k_4, k_5, k_6]])</em> of 4, 5, or 8 elements. If the vector is
 * NULL/empty, the zero distortion coefficients are assumed.
 * @param imagePoints Output array of image points, 2xN/Nx2 1-channel or 1xN/Nx1
 * 2-channel, or <code>vector<Point2f></code>.
 * @param jacobian Optional output 2Nx(10+<numDistCoeffs>) jacobian matrix of
 * derivatives of image points with respect to components of the rotation
 * vector, translation vector, focal lengths, coordinates of the principal point
 * and the distortion coefficients. In the old interface different components of
 * the jacobian are returned via different output parameters.
 * @param aspectRatio Optional "fixed aspect ratio" parameter. If the parameter
 * is not 0, the function assumes that the aspect ratio (*fx/fy*) is fixed and
 * correspondingly adjusts the jacobian matrix.
 *
 * @see <a href="http://docs.opencv.org/modules/calib3d/doc/camera_calibration_and_3d_reconstruction.html#projectpoints">org.opencv.calib3d.Calib3d.projectPoints</a>
 */
    public static void projectPoints(MatOfPoint3f objectPoints, Mat rvec, Mat tvec, Mat cameraMatrix, MatOfDouble distCoeffs, MatOfPoint2f imagePoints, Mat jacobian, double aspectRatio)
    {
        Mat objectPoints_mat = objectPoints;
        Mat distCoeffs_mat = distCoeffs;
        Mat imagePoints_mat = imagePoints;
        projectPoints_0(objectPoints_mat.nativeObj, rvec.nativeObj, tvec.nativeObj, cameraMatrix.nativeObj, distCoeffs_mat.nativeObj, imagePoints_mat.nativeObj, jacobian.nativeObj, aspectRatio);

        return;
    }

/**
 * <p>Projects 3D points to an image plane.</p>
 *
 * <p>The function computes projections of 3D points to the image plane given
 * intrinsic and extrinsic camera parameters. Optionally, the function computes
 * Jacobians - matrices of partial derivatives of image points coordinates (as
 * functions of all the input parameters) with respect to the particular
 * parameters, intrinsic and/or extrinsic. The Jacobians are used during the
 * global optimization in "calibrateCamera", "solvePnP", and "stereoCalibrate".
 * The function itself can also be used to compute a re-projection error given
 * the current intrinsic and extrinsic parameters.</p>
 *
 * <p>Note: By setting <code>rvec=tvec=(0,0,0)</code> or by setting
 * <code>cameraMatrix</code> to a 3x3 identity matrix, or by passing zero
 * distortion coefficients, you can get various useful partial cases of the
 * function. This means that you can compute the distorted coordinates for a
 * sparse set of points or apply a perspective transformation (and also compute
 * the derivatives) in the ideal zero-distortion setup.</p>
 *
 * @param objectPoints Array of object points, 3xN/Nx3 1-channel or 1xN/Nx1
 * 3-channel (or <code>vector<Point3f></code>), where N is the number of points
 * in the view.
 * @param rvec Rotation vector. See "Rodrigues" for details.
 * @param tvec Translation vector.
 * @param cameraMatrix Camera matrix <em>A =
 * <p>|f_x 0 c_x|
 * |0 f_y c_y|
 * |0 0 _1|
 * </em>.</p>
 * @param distCoeffs Input vector of distortion coefficients <em>(k_1, k_2, p_1,
 * p_2[, k_3[, k_4, k_5, k_6]])</em> of 4, 5, or 8 elements. If the vector is
 * NULL/empty, the zero distortion coefficients are assumed.
 * @param imagePoints Output array of image points, 2xN/Nx2 1-channel or 1xN/Nx1
 * 2-channel, or <code>vector<Point2f></code>.
 *
 * @see <a href="http://docs.opencv.org/modules/calib3d/doc/camera_calibration_and_3d_reconstruction.html#projectpoints">org.opencv.calib3d.Calib3d.projectPoints</a>
 */
    public static void projectPoints(MatOfPoint3f objectPoints, Mat rvec, Mat tvec, Mat cameraMatrix, MatOfDouble distCoeffs, MatOfPoint2f imagePoints)
    {
        Mat objectPoints_mat = objectPoints;
        Mat distCoeffs_mat = distCoeffs;
        Mat imagePoints_mat = imagePoints;
        projectPoints_1(objectPoints_mat.nativeObj, rvec.nativeObj, tvec.nativeObj, cameraMatrix.nativeObj, distCoeffs_mat.nativeObj, imagePoints_mat.nativeObj);

        return;
    }


    //
    // C++:  float rectify3Collinear(Mat cameraMatrix1, Mat distCoeffs1, Mat cameraMatrix2, Mat distCoeffs2, Mat cameraMatrix3, Mat distCoeffs3, vector_Mat imgpt1, vector_Mat imgpt3, Size imageSize, Mat R12, Mat T12, Mat R13, Mat T13, Mat& R1, Mat& R2, Mat& R3, Mat& P1, Mat& P2, Mat& P3, Mat& Q, double alpha, Size newImgSize, Rect* roi1, Rect* roi2, int flags)
    //

    public static float rectify3Collinear(Mat cameraMatrix1, Mat distCoeffs1, Mat cameraMatrix2, Mat distCoeffs2, Mat cameraMatrix3, Mat distCoeffs3, List<Mat> imgpt1, List<Mat> imgpt3, Size imageSize, Mat R12, Mat T12, Mat R13, Mat T13, Mat R1, Mat R2, Mat R3, Mat P1, Mat P2, Mat P3, Mat Q, double alpha, Size newImgSize, Rect roi1, Rect roi2, int flags)
    {
        Mat imgpt1_mat = Converters.vector_Mat_to_Mat(imgpt1);
        Mat imgpt3_mat = Converters.vector_Mat_to_Mat(imgpt3);
        double[] roi1_out = new double[4];
        double[] roi2_out = new double[4];
        float retVal = rectify3Collinear_0(cameraMatrix1.nativeObj, distCoeffs1.nativeObj, cameraMatrix2.nativeObj, distCoeffs2.nativeObj, cameraMatrix3.nativeObj, distCoeffs3.nativeObj, imgpt1_mat.nativeObj, imgpt3_mat.nativeObj, imageSize.width, imageSize.height, R12.nativeObj, T12.nativeObj, R13.nativeObj, T13.nativeObj, R1.nativeObj, R2.nativeObj, R3.nativeObj, P1.nativeObj, P2.nativeObj, P3.nativeObj, Q.nativeObj, alpha, newImgSize.width, newImgSize.height, roi1_out, roi2_out, flags);
        if(roi1!=null){ roi1.x = (int)roi1_out[0]; roi1.y = (int)roi1_out[1]; roi1.width = (int)roi1_out[2]; roi1.height = (int)roi1_out[3]; }
        if(roi2!=null){ roi2.x = (int)roi2_out[0]; roi2.y = (int)roi2_out[1]; roi2.width = (int)roi2_out[2]; roi2.height = (int)roi2_out[3]; }
        return retVal;
    }


    //
    // C++:  void reprojectImageTo3D(Mat disparity, Mat& _3dImage, Mat Q, bool handleMissingValues = false, int ddepth = -1)
    //

/**
 * <p>Reprojects a disparity image to 3D space.</p>
 *
 * <p>The function transforms a single-channel disparity map to a 3-channel image
 * representing a 3D surface. That is, for each pixel <code>(x,y)</code> andthe
 * corresponding disparity <code>d=disparity(x,y)</code>, it computes:</p>
 *
 * <p><em>[X Y Z W]^T = Q *[x y disparity(x,y) 1]^T
 * _3dImage(x,y) = (X/W, Y/W, Z/W) </em></p>
 *
 * <p>The matrix <code>Q</code> can be an arbitrary <em>4 x 4</em> matrix (for
 * example, the one computed by "stereoRectify"). To reproject a sparse set of
 * points {(x,y,d),...} to 3D space, use "perspectiveTransform".</p>
 *
 * @param disparity Input single-channel 8-bit unsigned, 16-bit signed, 32-bit
 * signed or 32-bit floating-point disparity image.
 * @param _3dImage Output 3-channel floating-point image of the same size as
 * <code>disparity</code>. Each element of <code>_3dImage(x,y)</code> contains
 * 3D coordinates of the point <code>(x,y)</code> computed from the disparity
 * map.
 * @param Q <em>4 x 4</em> perspective transformation matrix that can be
 * obtained with "stereoRectify".
 * @param handleMissingValues Indicates, whether the function should handle
 * missing values (i.e. points where the disparity was not computed). If
 * <code>handleMissingValues=true</code>, then pixels with the minimal disparity
 * that corresponds to the outliers (see :ocv:funcx:"StereoBM.operator()") are
 * transformed to 3D points with a very large Z value (currently set to 10000).
 * @param ddepth The optional output array depth. If it is <code>-1</code>, the
 * output image will have <code>CV_32F</code> depth. <code>ddepth</code> can
 * also be set to <code>CV_16S</code>, <code>CV_32S</code> or <code>CV_32F</code>.
 *
 * @see <a href="http://docs.opencv.org/modules/calib3d/doc/camera_calibration_and_3d_reconstruction.html#reprojectimageto3d">org.opencv.calib3d.Calib3d.reprojectImageTo3D</a>
 */
    public static void reprojectImageTo3D(Mat disparity, Mat _3dImage, Mat Q, boolean handleMissingValues, int ddepth)
    {

        reprojectImageTo3D_0(disparity.nativeObj, _3dImage.nativeObj, Q.nativeObj, handleMissingValues, ddepth);

        return;
    }

/**
 * <p>Reprojects a disparity image to 3D space.</p>
 *
 * <p>The function transforms a single-channel disparity map to a 3-channel image
 * representing a 3D surface. That is, for each pixel <code>(x,y)</code> andthe
 * corresponding disparity <code>d=disparity(x,y)</code>, it computes:</p>
 *
 * <p><em>[X Y Z W]^T = Q *[x y disparity(x,y) 1]^T
 * _3dImage(x,y) = (X/W, Y/W, Z/W) </em></p>
 *
 * <p>The matrix <code>Q</code> can be an arbitrary <em>4 x 4</em> matrix (for
 * example, the one computed by "stereoRectify"). To reproject a sparse set of
 * points {(x,y,d),...} to 3D space, use "perspectiveTransform".</p>
 *
 * @param disparity Input single-channel 8-bit unsigned, 16-bit signed, 32-bit
 * signed or 32-bit floating-point disparity image.
 * @param _3dImage Output 3-channel floating-point image of the same size as
 * <code>disparity</code>. Each element of <code>_3dImage(x,y)</code> contains
 * 3D coordinates of the point <code>(x,y)</code> computed from the disparity
 * map.
 * @param Q <em>4 x 4</em> perspective transformation matrix that can be
 * obtained with "stereoRectify".
 * @param handleMissingValues Indicates, whether the function should handle
 * missing values (i.e. points where the disparity was not computed). If
 * <code>handleMissingValues=true</code>, then pixels with the minimal disparity
 * that corresponds to the outliers (see :ocv:funcx:"StereoBM.operator()") are
 * transformed to 3D points with a very large Z value (currently set to 10000).
 *
 * @see <a href="http://docs.opencv.org/modules/calib3d/doc/camera_calibration_and_3d_reconstruction.html#reprojectimageto3d">org.opencv.calib3d.Calib3d.reprojectImageTo3D</a>
 */
    public static void reprojectImageTo3D(Mat disparity, Mat _3dImage, Mat Q, boolean handleMissingValues)
    {

        reprojectImageTo3D_1(disparity.nativeObj, _3dImage.nativeObj, Q.nativeObj, handleMissingValues);

        return;
    }

/**
 * <p>Reprojects a disparity image to 3D space.</p>
 *
 * <p>The function transforms a single-channel disparity map to a 3-channel image
 * representing a 3D surface. That is, for each pixel <code>(x,y)</code> andthe
 * corresponding disparity <code>d=disparity(x,y)</code>, it computes:</p>
 *
 * <p><em>[X Y Z W]^T = Q *[x y disparity(x,y) 1]^T
 * _3dImage(x,y) = (X/W, Y/W, Z/W) </em></p>
 *
 * <p>The matrix <code>Q</code> can be an arbitrary <em>4 x 4</em> matrix (for
 * example, the one computed by "stereoRectify"). To reproject a sparse set of
 * points {(x,y,d),...} to 3D space, use "perspectiveTransform".</p>
 *
 * @param disparity Input single-channel 8-bit unsigned, 16-bit signed, 32-bit
 * signed or 32-bit floating-point disparity image.
 * @param _3dImage Output 3-channel floating-point image of the same size as
 * <code>disparity</code>. Each element of <code>_3dImage(x,y)</code> contains
 * 3D coordinates of the point <code>(x,y)</code> computed from the disparity
 * map.
 * @param Q <em>4 x 4</em> perspective transformation matrix that can be
 * obtained with "stereoRectify".
 *
 * @see <a href="http://docs.opencv.org/modules/calib3d/doc/camera_calibration_and_3d_reconstruction.html#reprojectimageto3d">org.opencv.calib3d.Calib3d.reprojectImageTo3D</a>
 */
    public static void reprojectImageTo3D(Mat disparity, Mat _3dImage, Mat Q)
    {

        reprojectImageTo3D_2(disparity.nativeObj, _3dImage.nativeObj, Q.nativeObj);

        return;
    }


    //
    // C++:  bool solvePnP(vector_Point3f objectPoints, vector_Point2f imagePoints, Mat cameraMatrix, vector_double distCoeffs, Mat& rvec, Mat& tvec, bool useExtrinsicGuess = false, int flags = ITERATIVE)
    //

/**
 * <p>Finds an object pose from 3D-2D point correspondences.</p>
 *
 * <p>The function estimates the object pose given a set of object points, their
 * corresponding image projections, as well as the camera matrix and the
 * distortion coefficients.</p>
 *
 * <p>Note:</p>
 * <ul>
 *   <li> An example of how to use solvePNP for planar augmented reality can be
 * found at opencv_source_code/samples/python2/plane_ar.py
 * </ul>
 *
 * @param objectPoints Array of object points in the object coordinate space,
 * 3xN/Nx3 1-channel or 1xN/Nx1 3-channel, where N is the number of points.
 * <code>vector<Point3f></code> can be also passed here.
 * @param imagePoints Array of corresponding image points, 2xN/Nx2 1-channel or
 * 1xN/Nx1 2-channel, where N is the number of points. <code>vector<Point2f></code>
 * can be also passed here.
 * @param cameraMatrix Input camera matrix <em>A =
 * <p>|fx 0 cx|
 * |0 fy cy|
 * |0 0 1|
 * </em>.</p>
 * @param distCoeffs Input vector of distortion coefficients <em>(k_1, k_2, p_1,
 * p_2[, k_3[, k_4, k_5, k_6]])</em> of 4, 5, or 8 elements. If the vector is
 * NULL/empty, the zero distortion coefficients are assumed.
 * @param rvec Output rotation vector (see "Rodrigues") that, together with
 * <code>tvec</code>, brings points from the model coordinate system to the
 * camera coordinate system.
 * @param tvec Output translation vector.
 * @param useExtrinsicGuess If true (1), the function uses the provided
 * <code>rvec</code> and <code>tvec</code> values as initial approximations of
 * the rotation and translation vectors, respectively, and further optimizes
 * them.
 * @param flags Method for solving a PnP problem:
 * <ul>
 *   <li> CV_ITERATIVE Iterative method is based on Levenberg-Marquardt
 * optimization. In this case the function finds such a pose that minimizes
 * reprojection error, that is the sum of squared distances between the observed
 * projections <code>imagePoints</code> and the projected (using
 * "projectPoints") <code>objectPoints</code>.
 *   <li> CV_P3P Method is based on the paper of X.S. Gao, X.-R. Hou, J. Tang,
 * H.-F. Chang "Complete Solution Classification for the Perspective-Three-Point
 * Problem". In this case the function requires exactly four object and image
 * points.
 *   <li> CV_EPNP Method has been introduced by F.Moreno-Noguer, V.Lepetit and
 * P.Fua in the paper "EPnP: Efficient Perspective-n-Point Camera Pose
 * Estimation".
 * </ul>
 *
 * @see <a href="http://docs.opencv.org/modules/calib3d/doc/camera_calibration_and_3d_reconstruction.html#solvepnp">org.opencv.calib3d.Calib3d.solvePnP</a>
 */
    public static boolean solvePnP(MatOfPoint3f objectPoints, MatOfPoint2f imagePoints, Mat cameraMatrix, MatOfDouble distCoeffs, Mat rvec, Mat tvec, boolean useExtrinsicGuess, int flags)
    {
        Mat objectPoints_mat = objectPoints;
        Mat imagePoints_mat = imagePoints;
        Mat distCoeffs_mat = distCoeffs;
        boolean retVal = solvePnP_0(objectPoints_mat.nativeObj, imagePoints_mat.nativeObj, cameraMatrix.nativeObj, distCoeffs_mat.nativeObj, rvec.nativeObj, tvec.nativeObj, useExtrinsicGuess, flags);

        return retVal;
    }

/**
 * <p>Finds an object pose from 3D-2D point correspondences.</p>
 *
 * <p>The function estimates the object pose given a set of object points, their
 * corresponding image projections, as well as the camera matrix and the
 * distortion coefficients.</p>
 *
 * <p>Note:</p>
 * <ul>
 *   <li> An example of how to use solvePNP for planar augmented reality can be
 * found at opencv_source_code/samples/python2/plane_ar.py
 * </ul>
 *
 * @param objectPoints Array of object points in the object coordinate space,
 * 3xN/Nx3 1-channel or 1xN/Nx1 3-channel, where N is the number of points.
 * <code>vector<Point3f></code> can be also passed here.
 * @param imagePoints Array of corresponding image points, 2xN/Nx2 1-channel or
 * 1xN/Nx1 2-channel, where N is the number of points. <code>vector<Point2f></code>
 * can be also passed here.
 * @param cameraMatrix Input camera matrix <em>A =
 * <p>|fx 0 cx|
 * |0 fy cy|
 * |0 0 1|
 * </em>.</p>
 * @param distCoeffs Input vector of distortion coefficients <em>(k_1, k_2, p_1,
 * p_2[, k_3[, k_4, k_5, k_6]])</em> of 4, 5, or 8 elements. If the vector is
 * NULL/empty, the zero distortion coefficients are assumed.
 * @param rvec Output rotation vector (see "Rodrigues") that, together with
 * <code>tvec</code>, brings points from the model coordinate system to the
 * camera coordinate system.
 * @param tvec Output translation vector.
 *
 * @see <a href="http://docs.opencv.org/modules/calib3d/doc/camera_calibration_and_3d_reconstruction.html#solvepnp">org.opencv.calib3d.Calib3d.solvePnP</a>
 */
    public static boolean solvePnP(MatOfPoint3f objectPoints, MatOfPoint2f imagePoints, Mat cameraMatrix, MatOfDouble distCoeffs, Mat rvec, Mat tvec)
    {
        Mat objectPoints_mat = objectPoints;
        Mat imagePoints_mat = imagePoints;
        Mat distCoeffs_mat = distCoeffs;
        boolean retVal = solvePnP_1(objectPoints_mat.nativeObj, imagePoints_mat.nativeObj, cameraMatrix.nativeObj, distCoeffs_mat.nativeObj, rvec.nativeObj, tvec.nativeObj);

        return retVal;
    }


    //
    // C++:  void solvePnPRansac(vector_Point3f objectPoints, vector_Point2f imagePoints, Mat cameraMatrix, vector_double distCoeffs, Mat& rvec, Mat& tvec, bool useExtrinsicGuess = false, int iterationsCount = 100, float reprojectionError = 8.0, int minInliersCount = 100, Mat& inliers = Mat(), int flags = ITERATIVE)
    //

/**
 * <p>Finds an object pose from 3D-2D point correspondences using the RANSAC
 * scheme.</p>
 *
 * <p>The function estimates an object pose given a set of object points, their
 * corresponding image projections, as well as the camera matrix and the
 * distortion coefficients. This function finds such a pose that minimizes
 * reprojection error, that is, the sum of squared distances between the
 * observed projections <code>imagePoints</code> and the projected (using
 * "projectPoints") <code>objectPoints</code>. The use of RANSAC makes the
 * function resistant to outliers. The function is parallelized with the TBB
 * library.</p>
 *
 * @param objectPoints Array of object points in the object coordinate space,
 * 3xN/Nx3 1-channel or 1xN/Nx1 3-channel, where N is the number of points.
 * <code>vector<Point3f></code> can be also passed here.
 * @param imagePoints Array of corresponding image points, 2xN/Nx2 1-channel or
 * 1xN/Nx1 2-channel, where N is the number of points. <code>vector<Point2f></code>
 * can be also passed here.
 * @param cameraMatrix Input camera matrix <em>A =
 * <p>|fx 0 cx|
 * |0 fy cy|
 * |0 0 1|
 * </em>.</p>
 * @param distCoeffs Input vector of distortion coefficients <em>(k_1, k_2, p_1,
 * p_2[, k_3[, k_4, k_5, k_6]])</em> of 4, 5, or 8 elements. If the vector is
 * NULL/empty, the zero distortion coefficients are assumed.
 * @param rvec Output rotation vector (see "Rodrigues") that, together with
 * <code>tvec</code>, brings points from the model coordinate system to the
 * camera coordinate system.
 * @param tvec Output translation vector.
 * @param useExtrinsicGuess If true (1), the function uses the provided
 * <code>rvec</code> and <code>tvec</code> values as initial approximations of
 * the rotation and translation vectors, respectively, and further optimizes
 * them.
 * @param iterationsCount Number of iterations.
 * @param reprojectionError Inlier threshold value used by the RANSAC procedure.
 * The parameter value is the maximum allowed distance between the observed and
 * computed point projections to consider it an inlier.
 * @param minInliersCount Number of inliers. If the algorithm at some stage
 * finds more inliers than <code>minInliersCount</code>, it finishes.
 * @param inliers Output vector that contains indices of inliers in
 * <code>objectPoints</code> and <code>imagePoints</code>.
 * @param flags Method for solving a PnP problem (see "solvePnP").
 *
 * @see <a href="http://docs.opencv.org/modules/calib3d/doc/camera_calibration_and_3d_reconstruction.html#solvepnpransac">org.opencv.calib3d.Calib3d.solvePnPRansac</a>
 */
    public static void solvePnPRansac(MatOfPoint3f objectPoints, MatOfPoint2f imagePoints, Mat cameraMatrix, MatOfDouble distCoeffs, Mat rvec, Mat tvec, boolean useExtrinsicGuess, int iterationsCount, float reprojectionError, int minInliersCount, Mat inliers, int flags)
    {
        Mat objectPoints_mat = objectPoints;
        Mat imagePoints_mat = imagePoints;
        Mat distCoeffs_mat = distCoeffs;
        solvePnPRansac_0(objectPoints_mat.nativeObj, imagePoints_mat.nativeObj, cameraMatrix.nativeObj, distCoeffs_mat.nativeObj, rvec.nativeObj, tvec.nativeObj, useExtrinsicGuess, iterationsCount, reprojectionError, minInliersCount, inliers.nativeObj, flags);

        return;
    }

/**
 * <p>Finds an object pose from 3D-2D point correspondences using the RANSAC
 * scheme.</p>
 *
 * <p>The function estimates an object pose given a set of object points, their
 * corresponding image projections, as well as the camera matrix and the
 * distortion coefficients. This function finds such a pose that minimizes
 * reprojection error, that is, the sum of squared distances between the
 * observed projections <code>imagePoints</code> and the projected (using
 * "projectPoints") <code>objectPoints</code>. The use of RANSAC makes the
 * function resistant to outliers. The function is parallelized with the TBB
 * library.</p>
 *
 * @param objectPoints Array of object points in the object coordinate space,
 * 3xN/Nx3 1-channel or 1xN/Nx1 3-channel, where N is the number of points.
 * <code>vector<Point3f></code> can be also passed here.
 * @param imagePoints Array of corresponding image points, 2xN/Nx2 1-channel or
 * 1xN/Nx1 2-channel, where N is the number of points. <code>vector<Point2f></code>
 * can be also passed here.
 * @param cameraMatrix Input camera matrix <em>A =
 * <p>|fx 0 cx|
 * |0 fy cy|
 * |0 0 1|
 * </em>.</p>
 * @param distCoeffs Input vector of distortion coefficients <em>(k_1, k_2, p_1,
 * p_2[, k_3[, k_4, k_5, k_6]])</em> of 4, 5, or 8 elements. If the vector is
 * NULL/empty, the zero distortion coefficients are assumed.
 * @param rvec Output rotation vector (see "Rodrigues") that, together with
 * <code>tvec</code>, brings points from the model coordinate system to the
 * camera coordinate system.
 * @param tvec Output translation vector.
 *
 * @see <a href="http://docs.opencv.org/modules/calib3d/doc/camera_calibration_and_3d_reconstruction.html#solvepnpransac">org.opencv.calib3d.Calib3d.solvePnPRansac</a>
 */
    public static void solvePnPRansac(MatOfPoint3f objectPoints, MatOfPoint2f imagePoints, Mat cameraMatrix, MatOfDouble distCoeffs, Mat rvec, Mat tvec)
    {
        Mat objectPoints_mat = objectPoints;
        Mat imagePoints_mat = imagePoints;
        Mat distCoeffs_mat = distCoeffs;
        solvePnPRansac_1(objectPoints_mat.nativeObj, imagePoints_mat.nativeObj, cameraMatrix.nativeObj, distCoeffs_mat.nativeObj, rvec.nativeObj, tvec.nativeObj);

        return;
    }


    //
    // C++:  double stereoCalibrate(vector_Mat objectPoints, vector_Mat imagePoints1, vector_Mat imagePoints2, Mat& cameraMatrix1, Mat& distCoeffs1, Mat& cameraMatrix2, Mat& distCoeffs2, Size imageSize, Mat& R, Mat& T, Mat& E, Mat& F, TermCriteria criteria = TermCriteria(TermCriteria::COUNT+TermCriteria::EPS, 30, 1e-6), int flags = CALIB_FIX_INTRINSIC)
    //

/**
 * <p>Calibrates the stereo camera.</p>
 *
 * <p>The function estimates transformation between two cameras making a stereo
 * pair. If you have a stereo camera where the relative position and orientation
 * of two cameras is fixed, and if you computed poses of an object relative to
 * the first camera and to the second camera, (R1, T1) and (R2, T2),
 * respectively (this can be done with "solvePnP"), then those poses definitely
 * relate to each other. This means that, given (<em>R_1</em>,<em>T_1</em>), it
 * should be possible to compute (<em>R_2</em>,<em>T_2</em>). You only need to
 * know the position and orientation of the second camera relative to the first
 * camera. This is what the described function does. It computes
 * (<em>R</em>,<em>T</em>) so that:</p>
 *
 * <p><em>R_2=R*R_1&ltBR&gtT_2=R*T_1 + T,</em></p>
 *
 * <p>Optionally, it computes the essential matrix E:</p>
 *
 * <p><em>E=
 * |0 -T_2 T_1|
 * |T_2 0 -T_0|
 * |-T_1 T_0 0|</p>
 * <ul>
 *   <li>R</em>
 * </ul>
 *
 * <p>where <em>T_i</em> are components of the translation vector <em>T</em> :
 * <em>T=[T_0, T_1, T_2]^T</em>. And the function can also compute the
 * fundamental matrix F:</p>
 *
 * <p><em>F = cameraMatrix2^(-T) E cameraMatrix1^(-1)</em></p>
 *
 * <p>Besides the stereo-related information, the function can also perform a full
 * calibration of each of two cameras. However, due to the high dimensionality
 * of the parameter space and noise in the input data, the function can diverge
 * from the correct solution. If the intrinsic parameters can be estimated with
 * high accuracy for each of the cameras individually (for example, using
 * "calibrateCamera"), you are recommended to do so and then pass
 * <code>CV_CALIB_FIX_INTRINSIC</code> flag to the function along with the
 * computed intrinsic parameters. Otherwise, if all the parameters are estimated
 * at once, it makes sense to restrict some parameters, for example, pass
 * <code>CV_CALIB_SAME_FOCAL_LENGTH</code> and <code>CV_CALIB_ZERO_TANGENT_DIST</code>
 * flags, which is usually a reasonable assumption.</p>
 *
 * <p>Similarly to "calibrateCamera", the function minimizes the total
 * re-projection error for all the points in all the available views from both
 * cameras. The function returns the final value of the re-projection error.</p>
 *
 * @param objectPoints Vector of vectors of the calibration pattern points.
 * @param imagePoints1 Vector of vectors of the projections of the calibration
 * pattern points, observed by the first camera.
 * @param imagePoints2 Vector of vectors of the projections of the calibration
 * pattern points, observed by the second camera.
 * @param cameraMatrix1 Input/output first camera matrix: <em>
 * <p>|f_x^j 0 c_x^j|
 * |0 f_y^j c_y^j|
 * |0 0 1|
 * </em>, <em>j = 0, 1</em>. If any of <code>CV_CALIB_USE_INTRINSIC_GUESS</code>,
 * <code>CV_CALIB_FIX_ASPECT_RATIO</code>, <code>CV_CALIB_FIX_INTRINSIC</code>,
 * or <code>CV_CALIB_FIX_FOCAL_LENGTH</code> are specified, some or all of the
 * matrix components must be initialized. See the flags description for details.</p>
 * @param distCoeffs1 Input/output vector of distortion coefficients <em>(k_1,
 * k_2, p_1, p_2[, k_3[, k_4, k_5, k_6]])</em> of 4, 5, or 8 elements. The
 * output vector length depends on the flags.
 * @param cameraMatrix2 Input/output second camera matrix. The parameter is
 * similar to <code>cameraMatrix1</code>.
 * @param distCoeffs2 Input/output lens distortion coefficients for the second
 * camera. The parameter is similar to <code>distCoeffs1</code>.
 * @param imageSize Size of the image used only to initialize intrinsic camera
 * matrix.
 * @param R Output rotation matrix between the 1st and the 2nd camera coordinate
 * systems.
 * @param T Output translation vector between the coordinate systems of the
 * cameras.
 * @param E Output essential matrix.
 * @param F Output fundamental matrix.
 * @param criteria a criteria
 * @param flags Different flags that may be zero or a combination of the
 * following values:
 * <ul>
 *   <li> CV_CALIB_FIX_INTRINSIC Fix <code>cameraMatrix?</code> and
 * <code>distCoeffs?</code> so that only <code>R, T, E</code>, and
 * <code>F</code> matrices are estimated.
 *   <li> CV_CALIB_USE_INTRINSIC_GUESS Optimize some or all of the intrinsic
 * parameters according to the specified flags. Initial values are provided by
 * the user.
 *   <li> CV_CALIB_FIX_PRINCIPAL_POINT Fix the principal points during the
 * optimization.
 *   <li> CV_CALIB_FIX_FOCAL_LENGTH Fix <em>f^j_x</em> and <em>f^j_y</em>.
 *   <li> CV_CALIB_FIX_ASPECT_RATIO Optimize <em>f^j_y</em>. Fix the ratio
 * <em>f^j_x/f^j_y</em>.
 *   <li> CV_CALIB_SAME_FOCAL_LENGTH Enforce <em>f^0_x=f^1_x</em> and
 * <em>f^0_y=f^1_y</em>.
 *   <li> CV_CALIB_ZERO_TANGENT_DIST Set tangential distortion coefficients for
 * each camera to zeros and fix there.
 *   <li> CV_CALIB_FIX_K1,...,CV_CALIB_FIX_K6 Do not change the corresponding
 * radial distortion coefficient during the optimization. If <code>CV_CALIB_USE_INTRINSIC_GUESS</code>
 * is set, the coefficient from the supplied <code>distCoeffs</code> matrix is
 * used. Otherwise, it is set to 0.
 *   <li> CV_CALIB_RATIONAL_MODEL Enable coefficients k4, k5, and k6. To provide
 * the backward compatibility, this extra flag should be explicitly specified to
 * make the calibration function use the rational model and return 8
 * coefficients. If the flag is not set, the function computes and returns only
 * 5 distortion coefficients.
 * </ul>
 *
 * @see <a href="http://docs.opencv.org/modules/calib3d/doc/camera_calibration_and_3d_reconstruction.html#stereocalibrate">org.opencv.calib3d.Calib3d.stereoCalibrate</a>
 */
    public static double stereoCalibrate(List<Mat> objectPoints, List<Mat> imagePoints1, List<Mat> imagePoints2, Mat cameraMatrix1, Mat distCoeffs1, Mat cameraMatrix2, Mat distCoeffs2, Size imageSize, Mat R, Mat T, Mat E, Mat F, TermCriteria criteria, int flags)
    {
        Mat objectPoints_mat = Converters.vector_Mat_to_Mat(objectPoints);
        Mat imagePoints1_mat = Converters.vector_Mat_to_Mat(imagePoints1);
        Mat imagePoints2_mat = Converters.vector_Mat_to_Mat(imagePoints2);
        double retVal = stereoCalibrate_0(objectPoints_mat.nativeObj, imagePoints1_mat.nativeObj, imagePoints2_mat.nativeObj, cameraMatrix1.nativeObj, distCoeffs1.nativeObj, cameraMatrix2.nativeObj, distCoeffs2.nativeObj, imageSize.width, imageSize.height, R.nativeObj, T.nativeObj, E.nativeObj, F.nativeObj, criteria.type, criteria.maxCount, criteria.epsilon, flags);

        return retVal;
    }

/**
 * <p>Calibrates the stereo camera.</p>
 *
 * <p>The function estimates transformation between two cameras making a stereo
 * pair. If you have a stereo camera where the relative position and orientation
 * of two cameras is fixed, and if you computed poses of an object relative to
 * the first camera and to the second camera, (R1, T1) and (R2, T2),
 * respectively (this can be done with "solvePnP"), then those poses definitely
 * relate to each other. This means that, given (<em>R_1</em>,<em>T_1</em>), it
 * should be possible to compute (<em>R_2</em>,<em>T_2</em>). You only need to
 * know the position and orientation of the second camera relative to the first
 * camera. This is what the described function does. It computes
 * (<em>R</em>,<em>T</em>) so that:</p>
 *
 * <p><em>R_2=R*R_1&ltBR&gtT_2=R*T_1 + T,</em></p>
 *
 * <p>Optionally, it computes the essential matrix E:</p>
 *
 * <p><em>E=
 * |0 -T_2 T_1|
 * |T_2 0 -T_0|
 * |-T_1 T_0 0|</p>
 * <ul>
 *   <li>R</em>
 * </ul>
 *
 * <p>where <em>T_i</em> are components of the translation vector <em>T</em> :
 * <em>T=[T_0, T_1, T_2]^T</em>. And the function can also compute the
 * fundamental matrix F:</p>
 *
 * <p><em>F = cameraMatrix2^(-T) E cameraMatrix1^(-1)</em></p>
 *
 * <p>Besides the stereo-related information, the function can also perform a full
 * calibration of each of two cameras. However, due to the high dimensionality
 * of the parameter space and noise in the input data, the function can diverge
 * from the correct solution. If the intrinsic parameters can be estimated with
 * high accuracy for each of the cameras individually (for example, using
 * "calibrateCamera"), you are recommended to do so and then pass
 * <code>CV_CALIB_FIX_INTRINSIC</code> flag to the function along with the
 * computed intrinsic parameters. Otherwise, if all the parameters are estimated
 * at once, it makes sense to restrict some parameters, for example, pass
 * <code>CV_CALIB_SAME_FOCAL_LENGTH</code> and <code>CV_CALIB_ZERO_TANGENT_DIST</code>
 * flags, which is usually a reasonable assumption.</p>
 *
 * <p>Similarly to "calibrateCamera", the function minimizes the total
 * re-projection error for all the points in all the available views from both
 * cameras. The function returns the final value of the re-projection error.</p>
 *
 * @param objectPoints Vector of vectors of the calibration pattern points.
 * @param imagePoints1 Vector of vectors of the projections of the calibration
 * pattern points, observed by the first camera.
 * @param imagePoints2 Vector of vectors of the projections of the calibration
 * pattern points, observed by the second camera.
 * @param cameraMatrix1 Input/output first camera matrix: <em>
 * <p>|f_x^j 0 c_x^j|
 * |0 f_y^j c_y^j|
 * |0 0 1|
 * </em>, <em>j = 0, 1</em>. If any of <code>CV_CALIB_USE_INTRINSIC_GUESS</code>,
 * <code>CV_CALIB_FIX_ASPECT_RATIO</code>, <code>CV_CALIB_FIX_INTRINSIC</code>,
 * or <code>CV_CALIB_FIX_FOCAL_LENGTH</code> are specified, some or all of the
 * matrix components must be initialized. See the flags description for details.</p>
 * @param distCoeffs1 Input/output vector of distortion coefficients <em>(k_1,
 * k_2, p_1, p_2[, k_3[, k_4, k_5, k_6]])</em> of 4, 5, or 8 elements. The
 * output vector length depends on the flags.
 * @param cameraMatrix2 Input/output second camera matrix. The parameter is
 * similar to <code>cameraMatrix1</code>.
 * @param distCoeffs2 Input/output lens distortion coefficients for the second
 * camera. The parameter is similar to <code>distCoeffs1</code>.
 * @param imageSize Size of the image used only to initialize intrinsic camera
 * matrix.
 * @param R Output rotation matrix between the 1st and the 2nd camera coordinate
 * systems.
 * @param T Output translation vector between the coordinate systems of the
 * cameras.
 * @param E Output essential matrix.
 * @param F Output fundamental matrix.
 *
 * @see <a href="http://docs.opencv.org/modules/calib3d/doc/camera_calibration_and_3d_reconstruction.html#stereocalibrate">org.opencv.calib3d.Calib3d.stereoCalibrate</a>
 */
    public static double stereoCalibrate(List<Mat> objectPoints, List<Mat> imagePoints1, List<Mat> imagePoints2, Mat cameraMatrix1, Mat distCoeffs1, Mat cameraMatrix2, Mat distCoeffs2, Size imageSize, Mat R, Mat T, Mat E, Mat F)
    {
        Mat objectPoints_mat = Converters.vector_Mat_to_Mat(objectPoints);
        Mat imagePoints1_mat = Converters.vector_Mat_to_Mat(imagePoints1);
        Mat imagePoints2_mat = Converters.vector_Mat_to_Mat(imagePoints2);
        double retVal = stereoCalibrate_1(objectPoints_mat.nativeObj, imagePoints1_mat.nativeObj, imagePoints2_mat.nativeObj, cameraMatrix1.nativeObj, distCoeffs1.nativeObj, cameraMatrix2.nativeObj, distCoeffs2.nativeObj, imageSize.width, imageSize.height, R.nativeObj, T.nativeObj, E.nativeObj, F.nativeObj);

        return retVal;
    }


    //
    // C++:  void stereoRectify(Mat cameraMatrix1, Mat distCoeffs1, Mat cameraMatrix2, Mat distCoeffs2, Size imageSize, Mat R, Mat T, Mat& R1, Mat& R2, Mat& P1, Mat& P2, Mat& Q, int flags = CALIB_ZERO_DISPARITY, double alpha = -1, Size newImageSize = Size(), Rect* validPixROI1 = 0, Rect* validPixROI2 = 0)
    //

/**
 * <p>Computes rectification transforms for each head of a calibrated stereo
 * camera.</p>
 *
 * <p>The function computes the rotation matrices for each camera that (virtually)
 * make both camera image planes the same plane. Consequently, this makes all
 * the epipolar lines parallel and thus simplifies the dense stereo
 * correspondence problem. The function takes the matrices computed by
 * "stereoCalibrate" as input. As output, it provides two rotation matrices and
 * also two projection matrices in the new coordinates. The function
 * distinguishes the following two cases:</p>
 * <ul>
 *   <li> Horizontal stereo: the first and the second camera views are shifted
 * relative to each other mainly along the x axis (with possible small vertical
 * shift). In the rectified images, the corresponding epipolar lines in the left
 * and right cameras are horizontal and have the same y-coordinate. P1 and P2
 * look like:
 * </ul>
 *
 * <p><em>P1 = f 0 cx_1 0
 * 0 f cy 0
 * 0 0 1 0 </em></p>
 *
 *
 *
 * <p><em>P2 = f 0 cx_2 T_x*f
 * 0 f cy 0
 * 0 0 1 0,</em></p>
 *
 * <p>where <em>T_x</em> is a horizontal shift between the cameras and
 * <em>cx_1=cx_2</em> if <code>CV_CALIB_ZERO_DISPARITY</code> is set.</p>
 * <ul>
 *   <li> Vertical stereo: the first and the second camera views are shifted
 * relative to each other mainly in vertical direction (and probably a bit in
 * the horizontal direction too). The epipolar lines in the rectified images are
 * vertical and have the same x-coordinate. P1 and P2 look like:
 * </ul>
 *
 * <p><em>P1 = f 0 cx 0
 * 0 f cy_1 0
 * 0 0 1 0 </em></p>
 *
 *
 *
 * <p><em>P2 = f 0 cx 0
 * 0 f cy_2 T_y*f
 * 0 0 1 0,</em></p>
 *
 * <p>where <em>T_y</em> is a vertical shift between the cameras and
 * <em>cy_1=cy_2</em> if <code>CALIB_ZERO_DISPARITY</code> is set.</p>
 *
 * <p>As you can see, the first three columns of <code>P1</code> and
 * <code>P2</code> will effectively be the new "rectified" camera matrices.
 * The matrices, together with <code>R1</code> and <code>R2</code>, can then be
 * passed to "initUndistortRectifyMap" to initialize the rectification map for
 * each camera.</p>
 *
 * <p>See below the screenshot from the <code>stereo_calib.cpp</code> sample. Some
 * red horizontal lines pass through the corresponding image regions. This means
 * that the images are well rectified, which is what most stereo correspondence
 * algorithms rely on. The green rectangles are <code>roi1</code> and
 * <code>roi2</code>. You see that their interiors are all valid pixels.</p>
 *
 * @param cameraMatrix1 First camera matrix.
 * @param distCoeffs1 First camera distortion parameters.
 * @param cameraMatrix2 Second camera matrix.
 * @param distCoeffs2 Second camera distortion parameters.
 * @param imageSize Size of the image used for stereo calibration.
 * @param R Rotation matrix between the coordinate systems of the first and the
 * second cameras.
 * @param T Translation vector between coordinate systems of the cameras.
 * @param R1 Output 3x3 rectification transform (rotation matrix) for the first
 * camera.
 * @param R2 Output 3x3 rectification transform (rotation matrix) for the second
 * camera.
 * @param P1 Output 3x4 projection matrix in the new (rectified) coordinate
 * systems for the first camera.
 * @param P2 Output 3x4 projection matrix in the new (rectified) coordinate
 * systems for the second camera.
 * @param Q Output <em>4 x 4</em> disparity-to-depth mapping matrix (see
 * "reprojectImageTo3D").
 * @param flags Operation flags that may be zero or <code>CV_CALIB_ZERO_DISPARITY</code>.
 * If the flag is set, the function makes the principal points of each camera
 * have the same pixel coordinates in the rectified views. And if the flag is
 * not set, the function may still shift the images in the horizontal or
 * vertical direction (depending on the orientation of epipolar lines) to
 * maximize the useful image area.
 * @param alpha Free scaling parameter. If it is -1 or absent, the function
 * performs the default scaling. Otherwise, the parameter should be between 0
 * and 1. <code>alpha=0</code> means that the rectified images are zoomed and
 * shifted so that only valid pixels are visible (no black areas after
 * rectification). <code>alpha=1</code> means that the rectified image is
 * decimated and shifted so that all the pixels from the original images from
 * the cameras are retained in the rectified images (no source image pixels are
 * lost). Obviously, any intermediate value yields an intermediate result
 * between those two extreme cases.
 * @param newImageSize New image resolution after rectification. The same size
 * should be passed to "initUndistortRectifyMap" (see the <code>stereo_calib.cpp</code>
 * sample in OpenCV samples directory). When (0,0) is passed (default), it is
 * set to the original <code>imageSize</code>. Setting it to larger value can
 * help you preserve details in the original image, especially when there is a
 * big radial distortion.
 * @param validPixROI1 Optional output rectangles inside the rectified images
 * where all the pixels are valid. If <code>alpha=0</code>, the ROIs cover the
 * whole images. Otherwise, they are likely to be smaller (see the picture
 * below).
 * @param validPixROI2 Optional output rectangles inside the rectified images
 * where all the pixels are valid. If <code>alpha=0</code>, the ROIs cover the
 * whole images. Otherwise, they are likely to be smaller (see the picture
 * below).
 *
 * @see <a href="http://docs.opencv.org/modules/calib3d/doc/camera_calibration_and_3d_reconstruction.html#stereorectify">org.opencv.calib3d.Calib3d.stereoRectify</a>
 */
    public static void stereoRectify(Mat cameraMatrix1, Mat distCoeffs1, Mat cameraMatrix2, Mat distCoeffs2, Size imageSize, Mat R, Mat T, Mat R1, Mat R2, Mat P1, Mat P2, Mat Q, int flags, double alpha, Size newImageSize, Rect validPixROI1, Rect validPixROI2)
    {
        double[] validPixROI1_out = new double[4];
        double[] validPixROI2_out = new double[4];
        stereoRectify_0(cameraMatrix1.nativeObj, distCoeffs1.nativeObj, cameraMatrix2.nativeObj, distCoeffs2.nativeObj, imageSize.width, imageSize.height, R.nativeObj, T.nativeObj, R1.nativeObj, R2.nativeObj, P1.nativeObj, P2.nativeObj, Q.nativeObj, flags, alpha, newImageSize.width, newImageSize.height, validPixROI1_out, validPixROI2_out);
        if(validPixROI1!=null){ validPixROI1.x = (int)validPixROI1_out[0]; validPixROI1.y = (int)validPixROI1_out[1]; validPixROI1.width = (int)validPixROI1_out[2]; validPixROI1.height = (int)validPixROI1_out[3]; }
        if(validPixROI2!=null){ validPixROI2.x = (int)validPixROI2_out[0]; validPixROI2.y = (int)validPixROI2_out[1]; validPixROI2.width = (int)validPixROI2_out[2]; validPixROI2.height = (int)validPixROI2_out[3]; }
        return;
    }

/**
 * <p>Computes rectification transforms for each head of a calibrated stereo
 * camera.</p>
 *
 * <p>The function computes the rotation matrices for each camera that (virtually)
 * make both camera image planes the same plane. Consequently, this makes all
 * the epipolar lines parallel and thus simplifies the dense stereo
 * correspondence problem. The function takes the matrices computed by
 * "stereoCalibrate" as input. As output, it provides two rotation matrices and
 * also two projection matrices in the new coordinates. The function
 * distinguishes the following two cases:</p>
 * <ul>
 *   <li> Horizontal stereo: the first and the second camera views are shifted
 * relative to each other mainly along the x axis (with possible small vertical
 * shift). In the rectified images, the corresponding epipolar lines in the left
 * and right cameras are horizontal and have the same y-coordinate. P1 and P2
 * look like:
 * </ul>
 *
 * <p><em>P1 = f 0 cx_1 0
 * 0 f cy 0
 * 0 0 1 0 </em></p>
 *
 *
 *
 * <p><em>P2 = f 0 cx_2 T_x*f
 * 0 f cy 0
 * 0 0 1 0,</em></p>
 *
 * <p>where <em>T_x</em> is a horizontal shift between the cameras and
 * <em>cx_1=cx_2</em> if <code>CV_CALIB_ZERO_DISPARITY</code> is set.</p>
 * <ul>
 *   <li> Vertical stereo: the first and the second camera views are shifted
 * relative to each other mainly in vertical direction (and probably a bit in
 * the horizontal direction too). The epipolar lines in the rectified images are
 * vertical and have the same x-coordinate. P1 and P2 look like:
 * </ul>
 *
 * <p><em>P1 = f 0 cx 0
 * 0 f cy_1 0
 * 0 0 1 0 </em></p>
 *
 *
 *
 * <p><em>P2 = f 0 cx 0
 * 0 f cy_2 T_y*f
 * 0 0 1 0,</em></p>
 *
 * <p>where <em>T_y</em> is a vertical shift between the cameras and
 * <em>cy_1=cy_2</em> if <code>CALIB_ZERO_DISPARITY</code> is set.</p>
 *
 * <p>As you can see, the first three columns of <code>P1</code> and
 * <code>P2</code> will effectively be the new "rectified" camera matrices.
 * The matrices, together with <code>R1</code> and <code>R2</code>, can then be
 * passed to "initUndistortRectifyMap" to initialize the rectification map for
 * each camera.</p>
 *
 * <p>See below the screenshot from the <code>stereo_calib.cpp</code> sample. Some
 * red horizontal lines pass through the corresponding image regions. This means
 * that the images are well rectified, which is what most stereo correspondence
 * algorithms rely on. The green rectangles are <code>roi1</code> and
 * <code>roi2</code>. You see that their interiors are all valid pixels.</p>
 *
 * @param cameraMatrix1 First camera matrix.
 * @param distCoeffs1 First camera distortion parameters.
 * @param cameraMatrix2 Second camera matrix.
 * @param distCoeffs2 Second camera distortion parameters.
 * @param imageSize Size of the image used for stereo calibration.
 * @param R Rotation matrix between the coordinate systems of the first and the
 * second cameras.
 * @param T Translation vector between coordinate systems of the cameras.
 * @param R1 Output 3x3 rectification transform (rotation matrix) for the first
 * camera.
 * @param R2 Output 3x3 rectification transform (rotation matrix) for the second
 * camera.
 * @param P1 Output 3x4 projection matrix in the new (rectified) coordinate
 * systems for the first camera.
 * @param P2 Output 3x4 projection matrix in the new (rectified) coordinate
 * systems for the second camera.
 * @param Q Output <em>4 x 4</em> disparity-to-depth mapping matrix (see
 * "reprojectImageTo3D").
 *
 * @see <a href="http://docs.opencv.org/modules/calib3d/doc/camera_calibration_and_3d_reconstruction.html#stereorectify">org.opencv.calib3d.Calib3d.stereoRectify</a>
 */
    public static void stereoRectify(Mat cameraMatrix1, Mat distCoeffs1, Mat cameraMatrix2, Mat distCoeffs2, Size imageSize, Mat R, Mat T, Mat R1, Mat R2, Mat P1, Mat P2, Mat Q)
    {

        stereoRectify_1(cameraMatrix1.nativeObj, distCoeffs1.nativeObj, cameraMatrix2.nativeObj, distCoeffs2.nativeObj, imageSize.width, imageSize.height, R.nativeObj, T.nativeObj, R1.nativeObj, R2.nativeObj, P1.nativeObj, P2.nativeObj, Q.nativeObj);

        return;
    }


    //
    // C++:  bool stereoRectifyUncalibrated(Mat points1, Mat points2, Mat F, Size imgSize, Mat& H1, Mat& H2, double threshold = 5)
    //

/**
 * <p>Computes a rectification transform for an uncalibrated stereo camera.</p>
 *
 * <p>The function computes the rectification transformations without knowing
 * intrinsic parameters of the cameras and their relative position in the space,
 * which explains the suffix "uncalibrated". Another related difference from
 * "stereoRectify" is that the function outputs not the rectification
 * transformations in the object (3D) space, but the planar perspective
 * transformations encoded by the homography matrices <code>H1</code> and
 * <code>H2</code>. The function implements the algorithm [Hartley99].</p>
 *
 * <p>Note:</p>
 *
 * <p>While the algorithm does not need to know the intrinsic parameters of the
 * cameras, it heavily depends on the epipolar geometry. Therefore, if the
 * camera lenses have a significant distortion, it would be better to correct it
 * before computing the fundamental matrix and calling this function. For
 * example, distortion coefficients can be estimated for each head of stereo
 * camera separately by using "calibrateCamera". Then, the images can be
 * corrected using "undistort", or just the point coordinates can be corrected
 * with "undistortPoints".</p>
 *
 * @param points1 Array of feature points in the first image.
 * @param points2 The corresponding points in the second image. The same formats
 * as in "findFundamentalMat" are supported.
 * @param F Input fundamental matrix. It can be computed from the same set of
 * point pairs using "findFundamentalMat".
 * @param imgSize Size of the image.
 * @param H1 Output rectification homography matrix for the first image.
 * @param H2 Output rectification homography matrix for the second image.
 * @param threshold Optional threshold used to filter out the outliers. If the
 * parameter is greater than zero, all the point pairs that do not comply with
 * the epipolar geometry (that is, the points for which <em>|points2[i]^T*F*points1[i]|&gtthreshold</em>)
 * are rejected prior to computing the homographies. Otherwise,all the points
 * are considered inliers.
 *
 * @see <a href="http://docs.opencv.org/modules/calib3d/doc/camera_calibration_and_3d_reconstruction.html#stereorectifyuncalibrated">org.opencv.calib3d.Calib3d.stereoRectifyUncalibrated</a>
 */
    public static boolean stereoRectifyUncalibrated(Mat points1, Mat points2, Mat F, Size imgSize, Mat H1, Mat H2, double threshold)
    {

        boolean retVal = stereoRectifyUncalibrated_0(points1.nativeObj, points2.nativeObj, F.nativeObj, imgSize.width, imgSize.height, H1.nativeObj, H2.nativeObj, threshold);

        return retVal;
    }

/**
 * <p>Computes a rectification transform for an uncalibrated stereo camera.</p>
 *
 * <p>The function computes the rectification transformations without knowing
 * intrinsic parameters of the cameras and their relative position in the space,
 * which explains the suffix "uncalibrated". Another related difference from
 * "stereoRectify" is that the function outputs not the rectification
 * transformations in the object (3D) space, but the planar perspective
 * transformations encoded by the homography matrices <code>H1</code> and
 * <code>H2</code>. The function implements the algorithm [Hartley99].</p>
 *
 * <p>Note:</p>
 *
 * <p>While the algorithm does not need to know the intrinsic parameters of the
 * cameras, it heavily depends on the epipolar geometry. Therefore, if the
 * camera lenses have a significant distortion, it would be better to correct it
 * before computing the fundamental matrix and calling this function. For
 * example, distortion coefficients can be estimated for each head of stereo
 * camera separately by using "calibrateCamera". Then, the images can be
 * corrected using "undistort", or just the point coordinates can be corrected
 * with "undistortPoints".</p>
 *
 * @param points1 Array of feature points in the first image.
 * @param points2 The corresponding points in the second image. The same formats
 * as in "findFundamentalMat" are supported.
 * @param F Input fundamental matrix. It can be computed from the same set of
 * point pairs using "findFundamentalMat".
 * @param imgSize Size of the image.
 * @param H1 Output rectification homography matrix for the first image.
 * @param H2 Output rectification homography matrix for the second image.
 *
 * @see <a href="http://docs.opencv.org/modules/calib3d/doc/camera_calibration_and_3d_reconstruction.html#stereorectifyuncalibrated">org.opencv.calib3d.Calib3d.stereoRectifyUncalibrated</a>
 */
    public static boolean stereoRectifyUncalibrated(Mat points1, Mat points2, Mat F, Size imgSize, Mat H1, Mat H2)
    {

        boolean retVal = stereoRectifyUncalibrated_1(points1.nativeObj, points2.nativeObj, F.nativeObj, imgSize.width, imgSize.height, H1.nativeObj, H2.nativeObj);

        return retVal;
    }


    //
    // C++:  void triangulatePoints(Mat projMatr1, Mat projMatr2, Mat projPoints1, Mat projPoints2, Mat& points4D)
    //

/**
 * <p>Reconstructs points by triangulation.</p>
 *
 * <p>The function reconstructs 3-dimensional points (in homogeneous coordinates)
 * by using their observations with a stereo camera. Projections matrices can be
 * obtained from "stereoRectify".</p>
 *
 * <p>Note:</p>
 *
 * <p>Keep in mind that all input data should be of float type in order for this
 * function to work.</p>
 *
 * @param projMatr1 3x4 projection matrix of the first camera.
 * @param projMatr2 3x4 projection matrix of the second camera.
 * @param projPoints1 2xN array of feature points in the first image. In case of
 * c++ version it can be also a vector of feature points or two-channel matrix
 * of size 1xN or Nx1.
 * @param projPoints2 2xN array of corresponding points in the second image. In
 * case of c++ version it can be also a vector of feature points or two-channel
 * matrix of size 1xN or Nx1.
 * @param points4D 4xN array of reconstructed points in homogeneous coordinates.
 *
 * @see <a href="http://docs.opencv.org/modules/calib3d/doc/camera_calibration_and_3d_reconstruction.html#triangulatepoints">org.opencv.calib3d.Calib3d.triangulatePoints</a>
 * @see org.opencv.calib3d.Calib3d#reprojectImageTo3D
 */
    public static void triangulatePoints(Mat projMatr1, Mat projMatr2, Mat projPoints1, Mat projPoints2, Mat points4D)
    {

        triangulatePoints_0(projMatr1.nativeObj, projMatr2.nativeObj, projPoints1.nativeObj, projPoints2.nativeObj, points4D.nativeObj);

        return;
    }


    //
    // C++:  void validateDisparity(Mat& disparity, Mat cost, int minDisparity, int numberOfDisparities, int disp12MaxDisp = 1)
    //

    public static void validateDisparity(Mat disparity, Mat cost, int minDisparity, int numberOfDisparities, int disp12MaxDisp)
    {

        validateDisparity_0(disparity.nativeObj, cost.nativeObj, minDisparity, numberOfDisparities, disp12MaxDisp);

        return;
    }

    public static void validateDisparity(Mat disparity, Mat cost, int minDisparity, int numberOfDisparities)
    {

        validateDisparity_1(disparity.nativeObj, cost.nativeObj, minDisparity, numberOfDisparities);

        return;
    }




    // C++:  Vec3d RQDecomp3x3(Mat src, Mat& mtxR, Mat& mtxQ, Mat& Qx = Mat(), Mat& Qy = Mat(), Mat& Qz = Mat())
    private static native double[] RQDecomp3x3_0(long src_nativeObj, long mtxR_nativeObj, long mtxQ_nativeObj, long Qx_nativeObj, long Qy_nativeObj, long Qz_nativeObj);
    private static native double[] RQDecomp3x3_1(long src_nativeObj, long mtxR_nativeObj, long mtxQ_nativeObj);

    // C++:  void Rodrigues(Mat src, Mat& dst, Mat& jacobian = Mat())
    private static native void Rodrigues_0(long src_nativeObj, long dst_nativeObj, long jacobian_nativeObj);
    private static native void Rodrigues_1(long src_nativeObj, long dst_nativeObj);

    // C++:  double calibrateCamera(vector_Mat objectPoints, vector_Mat imagePoints, Size imageSize, Mat& cameraMatrix, Mat& distCoeffs, vector_Mat& rvecs, vector_Mat& tvecs, int flags = 0, TermCriteria criteria = TermCriteria( TermCriteria::COUNT+TermCriteria::EPS, 30, DBL_EPSILON))
    private static native double calibrateCamera_0(long objectPoints_mat_nativeObj, long imagePoints_mat_nativeObj, double imageSize_width, double imageSize_height, long cameraMatrix_nativeObj, long distCoeffs_nativeObj, long rvecs_mat_nativeObj, long tvecs_mat_nativeObj, int flags, int criteria_type, int criteria_maxCount, double criteria_epsilon);
    private static native double calibrateCamera_1(long objectPoints_mat_nativeObj, long imagePoints_mat_nativeObj, double imageSize_width, double imageSize_height, long cameraMatrix_nativeObj, long distCoeffs_nativeObj, long rvecs_mat_nativeObj, long tvecs_mat_nativeObj, int flags);
    private static native double calibrateCamera_2(long objectPoints_mat_nativeObj, long imagePoints_mat_nativeObj, double imageSize_width, double imageSize_height, long cameraMatrix_nativeObj, long distCoeffs_nativeObj, long rvecs_mat_nativeObj, long tvecs_mat_nativeObj);

    // C++:  void calibrationMatrixValues(Mat cameraMatrix, Size imageSize, double apertureWidth, double apertureHeight, double& fovx, double& fovy, double& focalLength, Point2d& principalPoint, double& aspectRatio)
    private static native void calibrationMatrixValues_0(long cameraMatrix_nativeObj, double imageSize_width, double imageSize_height, double apertureWidth, double apertureHeight, double[] fovx_out, double[] fovy_out, double[] focalLength_out, double[] principalPoint_out, double[] aspectRatio_out);

    // C++:  void composeRT(Mat rvec1, Mat tvec1, Mat rvec2, Mat tvec2, Mat& rvec3, Mat& tvec3, Mat& dr3dr1 = Mat(), Mat& dr3dt1 = Mat(), Mat& dr3dr2 = Mat(), Mat& dr3dt2 = Mat(), Mat& dt3dr1 = Mat(), Mat& dt3dt1 = Mat(), Mat& dt3dr2 = Mat(), Mat& dt3dt2 = Mat())
    private static native void composeRT_0(long rvec1_nativeObj, long tvec1_nativeObj, long rvec2_nativeObj, long tvec2_nativeObj, long rvec3_nativeObj, long tvec3_nativeObj, long dr3dr1_nativeObj, long dr3dt1_nativeObj, long dr3dr2_nativeObj, long dr3dt2_nativeObj, long dt3dr1_nativeObj, long dt3dt1_nativeObj, long dt3dr2_nativeObj, long dt3dt2_nativeObj);
    private static native void composeRT_1(long rvec1_nativeObj, long tvec1_nativeObj, long rvec2_nativeObj, long tvec2_nativeObj, long rvec3_nativeObj, long tvec3_nativeObj);

    // C++:  void computeCorrespondEpilines(Mat points, int whichImage, Mat F, Mat& lines)
    private static native void computeCorrespondEpilines_0(long points_nativeObj, int whichImage, long F_nativeObj, long lines_nativeObj);

    // C++:  void convertPointsFromHomogeneous(Mat src, Mat& dst)
    private static native void convertPointsFromHomogeneous_0(long src_nativeObj, long dst_nativeObj);

    // C++:  void convertPointsToHomogeneous(Mat src, Mat& dst)
    private static native void convertPointsToHomogeneous_0(long src_nativeObj, long dst_nativeObj);

    // C++:  void correctMatches(Mat F, Mat points1, Mat points2, Mat& newPoints1, Mat& newPoints2)
    private static native void correctMatches_0(long F_nativeObj, long points1_nativeObj, long points2_nativeObj, long newPoints1_nativeObj, long newPoints2_nativeObj);

    // C++:  void decomposeProjectionMatrix(Mat projMatrix, Mat& cameraMatrix, Mat& rotMatrix, Mat& transVect, Mat& rotMatrixX = Mat(), Mat& rotMatrixY = Mat(), Mat& rotMatrixZ = Mat(), Mat& eulerAngles = Mat())
    private static native void decomposeProjectionMatrix_0(long projMatrix_nativeObj, long cameraMatrix_nativeObj, long rotMatrix_nativeObj, long transVect_nativeObj, long rotMatrixX_nativeObj, long rotMatrixY_nativeObj, long rotMatrixZ_nativeObj, long eulerAngles_nativeObj);
    private static native void decomposeProjectionMatrix_1(long projMatrix_nativeObj, long cameraMatrix_nativeObj, long rotMatrix_nativeObj, long transVect_nativeObj);

    // C++:  void drawChessboardCorners(Mat& image, Size patternSize, vector_Point2f corners, bool patternWasFound)
    private static native void drawChessboardCorners_0(long image_nativeObj, double patternSize_width, double patternSize_height, long corners_mat_nativeObj, boolean patternWasFound);

    // C++:  int estimateAffine3D(Mat src, Mat dst, Mat& out, Mat& inliers, double ransacThreshold = 3, double confidence = 0.99)
    private static native int estimateAffine3D_0(long src_nativeObj, long dst_nativeObj, long out_nativeObj, long inliers_nativeObj, double ransacThreshold, double confidence);
    private static native int estimateAffine3D_1(long src_nativeObj, long dst_nativeObj, long out_nativeObj, long inliers_nativeObj);

    // C++:  void filterSpeckles(Mat& img, double newVal, int maxSpeckleSize, double maxDiff, Mat& buf = Mat())
    private static native void filterSpeckles_0(long img_nativeObj, double newVal, int maxSpeckleSize, double maxDiff, long buf_nativeObj);
    private static native void filterSpeckles_1(long img_nativeObj, double newVal, int maxSpeckleSize, double maxDiff);

    // C++:  bool findChessboardCorners(Mat image, Size patternSize, vector_Point2f& corners, int flags = CALIB_CB_ADAPTIVE_THRESH+CALIB_CB_NORMALIZE_IMAGE)
    private static native boolean findChessboardCorners_0(long image_nativeObj, double patternSize_width, double patternSize_height, long corners_mat_nativeObj, int flags);
    private static native boolean findChessboardCorners_1(long image_nativeObj, double patternSize_width, double patternSize_height, long corners_mat_nativeObj);

    // C++:  bool findCirclesGridDefault(Mat image, Size patternSize, Mat& centers, int flags = CALIB_CB_SYMMETRIC_GRID)
    private static native boolean findCirclesGridDefault_0(long image_nativeObj, double patternSize_width, double patternSize_height, long centers_nativeObj, int flags);
    private static native boolean findCirclesGridDefault_1(long image_nativeObj, double patternSize_width, double patternSize_height, long centers_nativeObj);

    // C++:  Mat findFundamentalMat(vector_Point2f points1, vector_Point2f points2, int method = FM_RANSAC, double param1 = 3., double param2 = 0.99, Mat& mask = Mat())
    private static native long findFundamentalMat_0(long points1_mat_nativeObj, long points2_mat_nativeObj, int method, double param1, double param2, long mask_nativeObj);
    private static native long findFundamentalMat_1(long points1_mat_nativeObj, long points2_mat_nativeObj, int method, double param1, double param2);
    private static native long findFundamentalMat_2(long points1_mat_nativeObj, long points2_mat_nativeObj);

    // C++:  Mat findHomography(vector_Point2f srcPoints, vector_Point2f dstPoints, int method = 0, double ransacReprojThreshold = 3, Mat& mask = Mat())
    private static native long findHomography_0(long srcPoints_mat_nativeObj, long dstPoints_mat_nativeObj, int method, double ransacReprojThreshold, long mask_nativeObj);
    private static native long findHomography_1(long srcPoints_mat_nativeObj, long dstPoints_mat_nativeObj, int method, double ransacReprojThreshold);
    private static native long findHomography_2(long srcPoints_mat_nativeObj, long dstPoints_mat_nativeObj);

    // C++:  Mat getOptimalNewCameraMatrix(Mat cameraMatrix, Mat distCoeffs, Size imageSize, double alpha, Size newImgSize = Size(), Rect* validPixROI = 0, bool centerPrincipalPoint = false)
    private static native long getOptimalNewCameraMatrix_0(long cameraMatrix_nativeObj, long distCoeffs_nativeObj, double imageSize_width, double imageSize_height, double alpha, double newImgSize_width, double newImgSize_height, double[] validPixROI_out, boolean centerPrincipalPoint);
    private static native long getOptimalNewCameraMatrix_1(long cameraMatrix_nativeObj, long distCoeffs_nativeObj, double imageSize_width, double imageSize_height, double alpha);

    // C++:  Rect getValidDisparityROI(Rect roi1, Rect roi2, int minDisparity, int numberOfDisparities, int SADWindowSize)
    private static native double[] getValidDisparityROI_0(int roi1_x, int roi1_y, int roi1_width, int roi1_height, int roi2_x, int roi2_y, int roi2_width, int roi2_height, int minDisparity, int numberOfDisparities, int SADWindowSize);

    // C++:  Mat initCameraMatrix2D(vector_vector_Point3f objectPoints, vector_vector_Point2f imagePoints, Size imageSize, double aspectRatio = 1.)
    private static native long initCameraMatrix2D_0(long objectPoints_mat_nativeObj, long imagePoints_mat_nativeObj, double imageSize_width, double imageSize_height, double aspectRatio);
    private static native long initCameraMatrix2D_1(long objectPoints_mat_nativeObj, long imagePoints_mat_nativeObj, double imageSize_width, double imageSize_height);

    // C++:  void matMulDeriv(Mat A, Mat B, Mat& dABdA, Mat& dABdB)
    private static native void matMulDeriv_0(long A_nativeObj, long B_nativeObj, long dABdA_nativeObj, long dABdB_nativeObj);

    // C++:  void projectPoints(vector_Point3f objectPoints, Mat rvec, Mat tvec, Mat cameraMatrix, vector_double distCoeffs, vector_Point2f& imagePoints, Mat& jacobian = Mat(), double aspectRatio = 0)
    private static native void projectPoints_0(long objectPoints_mat_nativeObj, long rvec_nativeObj, long tvec_nativeObj, long cameraMatrix_nativeObj, long distCoeffs_mat_nativeObj, long imagePoints_mat_nativeObj, long jacobian_nativeObj, double aspectRatio);
    private static native void projectPoints_1(long objectPoints_mat_nativeObj, long rvec_nativeObj, long tvec_nativeObj, long cameraMatrix_nativeObj, long distCoeffs_mat_nativeObj, long imagePoints_mat_nativeObj);

    // C++:  float rectify3Collinear(Mat cameraMatrix1, Mat distCoeffs1, Mat cameraMatrix2, Mat distCoeffs2, Mat cameraMatrix3, Mat distCoeffs3, vector_Mat imgpt1, vector_Mat imgpt3, Size imageSize, Mat R12, Mat T12, Mat R13, Mat T13, Mat& R1, Mat& R2, Mat& R3, Mat& P1, Mat& P2, Mat& P3, Mat& Q, double alpha, Size newImgSize, Rect* roi1, Rect* roi2, int flags)
    private static native float rectify3Collinear_0(long cameraMatrix1_nativeObj, long distCoeffs1_nativeObj, long cameraMatrix2_nativeObj, long distCoeffs2_nativeObj, long cameraMatrix3_nativeObj, long distCoeffs3_nativeObj, long imgpt1_mat_nativeObj, long imgpt3_mat_nativeObj, double imageSize_width, double imageSize_height, long R12_nativeObj, long T12_nativeObj, long R13_nativeObj, long T13_nativeObj, long R1_nativeObj, long R2_nativeObj, long R3_nativeObj, long P1_nativeObj, long P2_nativeObj, long P3_nativeObj, long Q_nativeObj, double alpha, double newImgSize_width, double newImgSize_height, double[] roi1_out, double[] roi2_out, int flags);

    // C++:  void reprojectImageTo3D(Mat disparity, Mat& _3dImage, Mat Q, bool handleMissingValues = false, int ddepth = -1)
    private static native void reprojectImageTo3D_0(long disparity_nativeObj, long _3dImage_nativeObj, long Q_nativeObj, boolean handleMissingValues, int ddepth);
    private static native void reprojectImageTo3D_1(long disparity_nativeObj, long _3dImage_nativeObj, long Q_nativeObj, boolean handleMissingValues);
    private static native void reprojectImageTo3D_2(long disparity_nativeObj, long _3dImage_nativeObj, long Q_nativeObj);

    // C++:  bool solvePnP(vector_Point3f objectPoints, vector_Point2f imagePoints, Mat cameraMatrix, vector_double distCoeffs, Mat& rvec, Mat& tvec, bool useExtrinsicGuess = false, int flags = ITERATIVE)
    private static native boolean solvePnP_0(long objectPoints_mat_nativeObj, long imagePoints_mat_nativeObj, long cameraMatrix_nativeObj, long distCoeffs_mat_nativeObj, long rvec_nativeObj, long tvec_nativeObj, boolean useExtrinsicGuess, int flags);
    private static native boolean solvePnP_1(long objectPoints_mat_nativeObj, long imagePoints_mat_nativeObj, long cameraMatrix_nativeObj, long distCoeffs_mat_nativeObj, long rvec_nativeObj, long tvec_nativeObj);

    // C++:  void solvePnPRansac(vector_Point3f objectPoints, vector_Point2f imagePoints, Mat cameraMatrix, vector_double distCoeffs, Mat& rvec, Mat& tvec, bool useExtrinsicGuess = false, int iterationsCount = 100, float reprojectionError = 8.0, int minInliersCount = 100, Mat& inliers = Mat(), int flags = ITERATIVE)
    private static native void solvePnPRansac_0(long objectPoints_mat_nativeObj, long imagePoints_mat_nativeObj, long cameraMatrix_nativeObj, long distCoeffs_mat_nativeObj, long rvec_nativeObj, long tvec_nativeObj, boolean useExtrinsicGuess, int iterationsCount, float reprojectionError, int minInliersCount, long inliers_nativeObj, int flags);
    private static native void solvePnPRansac_1(long objectPoints_mat_nativeObj, long imagePoints_mat_nativeObj, long cameraMatrix_nativeObj, long distCoeffs_mat_nativeObj, long rvec_nativeObj, long tvec_nativeObj);

    // C++:  double stereoCalibrate(vector_Mat objectPoints, vector_Mat imagePoints1, vector_Mat imagePoints2, Mat& cameraMatrix1, Mat& distCoeffs1, Mat& cameraMatrix2, Mat& distCoeffs2, Size imageSize, Mat& R, Mat& T, Mat& E, Mat& F, TermCriteria criteria = TermCriteria(TermCriteria::COUNT+TermCriteria::EPS, 30, 1e-6), int flags = CALIB_FIX_INTRINSIC)
    private static native double stereoCalibrate_0(long objectPoints_mat_nativeObj, long imagePoints1_mat_nativeObj, long imagePoints2_mat_nativeObj, long cameraMatrix1_nativeObj, long distCoeffs1_nativeObj, long cameraMatrix2_nativeObj, long distCoeffs2_nativeObj, double imageSize_width, double imageSize_height, long R_nativeObj, long T_nativeObj, long E_nativeObj, long F_nativeObj, int criteria_type, int criteria_maxCount, double criteria_epsilon, int flags);
    private static native double stereoCalibrate_1(long objectPoints_mat_nativeObj, long imagePoints1_mat_nativeObj, long imagePoints2_mat_nativeObj, long cameraMatrix1_nativeObj, long distCoeffs1_nativeObj, long cameraMatrix2_nativeObj, long distCoeffs2_nativeObj, double imageSize_width, double imageSize_height, long R_nativeObj, long T_nativeObj, long E_nativeObj, long F_nativeObj);

    // C++:  void stereoRectify(Mat cameraMatrix1, Mat distCoeffs1, Mat cameraMatrix2, Mat distCoeffs2, Size imageSize, Mat R, Mat T, Mat& R1, Mat& R2, Mat& P1, Mat& P2, Mat& Q, int flags = CALIB_ZERO_DISPARITY, double alpha = -1, Size newImageSize = Size(), Rect* validPixROI1 = 0, Rect* validPixROI2 = 0)
    private static native void stereoRectify_0(long cameraMatrix1_nativeObj, long distCoeffs1_nativeObj, long cameraMatrix2_nativeObj, long distCoeffs2_nativeObj, double imageSize_width, double imageSize_height, long R_nativeObj, long T_nativeObj, long R1_nativeObj, long R2_nativeObj, long P1_nativeObj, long P2_nativeObj, long Q_nativeObj, int flags, double alpha, double newImageSize_width, double newImageSize_height, double[] validPixROI1_out, double[] validPixROI2_out);
    private static native void stereoRectify_1(long cameraMatrix1_nativeObj, long distCoeffs1_nativeObj, long cameraMatrix2_nativeObj, long distCoeffs2_nativeObj, double imageSize_width, double imageSize_height, long R_nativeObj, long T_nativeObj, long R1_nativeObj, long R2_nativeObj, long P1_nativeObj, long P2_nativeObj, long Q_nativeObj);

    // C++:  bool stereoRectifyUncalibrated(Mat points1, Mat points2, Mat F, Size imgSize, Mat& H1, Mat& H2, double threshold = 5)
    private static native boolean stereoRectifyUncalibrated_0(long points1_nativeObj, long points2_nativeObj, long F_nativeObj, double imgSize_width, double imgSize_height, long H1_nativeObj, long H2_nativeObj, double threshold);
    private static native boolean stereoRectifyUncalibrated_1(long points1_nativeObj, long points2_nativeObj, long F_nativeObj, double imgSize_width, double imgSize_height, long H1_nativeObj, long H2_nativeObj);

    // C++:  void triangulatePoints(Mat projMatr1, Mat projMatr2, Mat projPoints1, Mat projPoints2, Mat& points4D)
    private static native void triangulatePoints_0(long projMatr1_nativeObj, long projMatr2_nativeObj, long projPoints1_nativeObj, long projPoints2_nativeObj, long points4D_nativeObj);

    // C++:  void validateDisparity(Mat& disparity, Mat cost, int minDisparity, int numberOfDisparities, int disp12MaxDisp = 1)
    private static native void validateDisparity_0(long disparity_nativeObj, long cost_nativeObj, int minDisparity, int numberOfDisparities, int disp12MaxDisp);
    private static native void validateDisparity_1(long disparity_nativeObj, long cost_nativeObj, int minDisparity, int numberOfDisparities);

}
