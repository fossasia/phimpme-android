
//
// This file is auto-generated. Please don't modify it!
//
package org.opencv.photo;

import java.util.List;
import org.opencv.core.Mat;
import org.opencv.utils.Converters;

public class Photo {

    private static final int
            CV_INPAINT_NS = 0,
            CV_INPAINT_TELEA = 1;


    public static final int
            INPAINT_NS = CV_INPAINT_NS,
            INPAINT_TELEA = CV_INPAINT_TELEA;


    //
    // C++:  void fastNlMeansDenoising(Mat src, Mat& dst, float h = 3, int templateWindowSize = 7, int searchWindowSize = 21)
    //

/**
 * <p>Perform image denoising using Non-local Means Denoising algorithm
 * http://www.ipol.im/pub/algo/bcm_non_local_means_denoising/ with several
 * computational optimizations. Noise expected to be a gaussian white noise</p>
 *
 * <p>This function expected to be applied to grayscale images. For colored images
 * look at <code>fastNlMeansDenoisingColored</code>.
 * Advanced usage of this functions can be manual denoising of colored image in
 * different colorspaces.
 * Such approach is used in <code>fastNlMeansDenoisingColored</code> by
 * converting image to CIELAB colorspace and then separately denoise L and AB
 * components with different h parameter.</p>
 *
 * @param src Input 8-bit 1-channel, 2-channel or 3-channel image.
 * @param dst Output image with the same size and type as <code>src</code>.
 * @param h Parameter regulating filter strength. Big h value perfectly removes
 * noise but also removes image details, smaller h value preserves details but
 * also preserves some noise
 * @param templateWindowSize Size in pixels of the template patch that is used
 * to compute weights. Should be odd. Recommended value 7 pixels
 * @param searchWindowSize Size in pixels of the window that is used to compute
 * weighted average for given pixel. Should be odd. Affect performance linearly:
 * greater searchWindowsSize - greater denoising time. Recommended value 21
 * pixels
 *
 * @see <a href="http://docs.opencv.org/modules/photo/doc/denoising.html#fastnlmeansdenoising">org.opencv.photo.Photo.fastNlMeansDenoising</a>
 */
    public static void fastNlMeansDenoising(Mat src, Mat dst, float h, int templateWindowSize, int searchWindowSize)
    {

        fastNlMeansDenoising_0(src.nativeObj, dst.nativeObj, h, templateWindowSize, searchWindowSize);

        return;
    }

/**
 * <p>Perform image denoising using Non-local Means Denoising algorithm
 * http://www.ipol.im/pub/algo/bcm_non_local_means_denoising/ with several
 * computational optimizations. Noise expected to be a gaussian white noise</p>
 *
 * <p>This function expected to be applied to grayscale images. For colored images
 * look at <code>fastNlMeansDenoisingColored</code>.
 * Advanced usage of this functions can be manual denoising of colored image in
 * different colorspaces.
 * Such approach is used in <code>fastNlMeansDenoisingColored</code> by
 * converting image to CIELAB colorspace and then separately denoise L and AB
 * components with different h parameter.</p>
 *
 * @param src Input 8-bit 1-channel, 2-channel or 3-channel image.
 * @param dst Output image with the same size and type as <code>src</code>.
 *
 * @see <a href="http://docs.opencv.org/modules/photo/doc/denoising.html#fastnlmeansdenoising">org.opencv.photo.Photo.fastNlMeansDenoising</a>
 */
    public static void fastNlMeansDenoising(Mat src, Mat dst)
    {

        fastNlMeansDenoising_1(src.nativeObj, dst.nativeObj);

        return;
    }


    //
    // C++:  void fastNlMeansDenoisingColored(Mat src, Mat& dst, float h = 3, float hColor = 3, int templateWindowSize = 7, int searchWindowSize = 21)
    //

/**
 * <p>Modification of <code>fastNlMeansDenoising</code> function for colored images</p>
 *
 * <p>The function converts image to CIELAB colorspace and then separately denoise
 * L and AB components with given h parameters using <code>fastNlMeansDenoising</code>
 * function.</p>
 *
 * @param src Input 8-bit 3-channel image.
 * @param dst Output image with the same size and type as <code>src</code>.
 * @param h Parameter regulating filter strength for luminance component. Bigger
 * h value perfectly removes noise but also removes image details, smaller h
 * value preserves details but also preserves some noise
 * @param hColor The same as h but for color components. For most images value
 * equals 10 will be enought to remove colored noise and do not distort colors
 * @param templateWindowSize Size in pixels of the template patch that is used
 * to compute weights. Should be odd. Recommended value 7 pixels
 * @param searchWindowSize Size in pixels of the window that is used to compute
 * weighted average for given pixel. Should be odd. Affect performance linearly:
 * greater searchWindowsSize - greater denoising time. Recommended value 21
 * pixels
 *
 * @see <a href="http://docs.opencv.org/modules/photo/doc/denoising.html#fastnlmeansdenoisingcolored">org.opencv.photo.Photo.fastNlMeansDenoisingColored</a>
 */
    public static void fastNlMeansDenoisingColored(Mat src, Mat dst, float h, float hColor, int templateWindowSize, int searchWindowSize)
    {

        fastNlMeansDenoisingColored_0(src.nativeObj, dst.nativeObj, h, hColor, templateWindowSize, searchWindowSize);

        return;
    }

/**
 * <p>Modification of <code>fastNlMeansDenoising</code> function for colored images</p>
 *
 * <p>The function converts image to CIELAB colorspace and then separately denoise
 * L and AB components with given h parameters using <code>fastNlMeansDenoising</code>
 * function.</p>
 *
 * @param src Input 8-bit 3-channel image.
 * @param dst Output image with the same size and type as <code>src</code>.
 *
 * @see <a href="http://docs.opencv.org/modules/photo/doc/denoising.html#fastnlmeansdenoisingcolored">org.opencv.photo.Photo.fastNlMeansDenoisingColored</a>
 */
    public static void fastNlMeansDenoisingColored(Mat src, Mat dst)
    {

        fastNlMeansDenoisingColored_1(src.nativeObj, dst.nativeObj);

        return;
    }


    //
    // C++:  void fastNlMeansDenoisingColoredMulti(vector_Mat srcImgs, Mat& dst, int imgToDenoiseIndex, int temporalWindowSize, float h = 3, float hColor = 3, int templateWindowSize = 7, int searchWindowSize = 21)
    //

/**
 * <p>Modification of <code>fastNlMeansDenoisingMulti</code> function for colored
 * images sequences</p>
 *
 * <p>The function converts images to CIELAB colorspace and then separately denoise
 * L and AB components with given h parameters using <code>fastNlMeansDenoisingMulti</code>
 * function.</p>
 *
 * @param srcImgs Input 8-bit 3-channel images sequence. All images should have
 * the same type and size.
 * @param dst Output image with the same size and type as <code>srcImgs</code>
 * images.
 * @param imgToDenoiseIndex Target image to denoise index in <code>srcImgs</code>
 * sequence
 * @param temporalWindowSize Number of surrounding images to use for target
 * image denoising. Should be odd. Images from <code>imgToDenoiseIndex -
 * temporalWindowSize / 2</code> to <code>imgToDenoiseIndex - temporalWindowSize
 * / 2</code> from <code>srcImgs</code> will be used to denoise
 * <code>srcImgs[imgToDenoiseIndex]</code> image.
 * @param h Parameter regulating filter strength for luminance component. Bigger
 * h value perfectly removes noise but also removes image details, smaller h
 * value preserves details but also preserves some noise.
 * @param hColor The same as h but for color components.
 * @param templateWindowSize Size in pixels of the template patch that is used
 * to compute weights. Should be odd. Recommended value 7 pixels
 * @param searchWindowSize Size in pixels of the window that is used to compute
 * weighted average for given pixel. Should be odd. Affect performance linearly:
 * greater searchWindowsSize - greater denoising time. Recommended value 21
 * pixels
 *
 * @see <a href="http://docs.opencv.org/modules/photo/doc/denoising.html#fastnlmeansdenoisingcoloredmulti">org.opencv.photo.Photo.fastNlMeansDenoisingColoredMulti</a>
 */
    public static void fastNlMeansDenoisingColoredMulti(List<Mat> srcImgs, Mat dst, int imgToDenoiseIndex, int temporalWindowSize, float h, float hColor, int templateWindowSize, int searchWindowSize)
    {
        Mat srcImgs_mat = Converters.vector_Mat_to_Mat(srcImgs);
        fastNlMeansDenoisingColoredMulti_0(srcImgs_mat.nativeObj, dst.nativeObj, imgToDenoiseIndex, temporalWindowSize, h, hColor, templateWindowSize, searchWindowSize);

        return;
    }

/**
 * <p>Modification of <code>fastNlMeansDenoisingMulti</code> function for colored
 * images sequences</p>
 *
 * <p>The function converts images to CIELAB colorspace and then separately denoise
 * L and AB components with given h parameters using <code>fastNlMeansDenoisingMulti</code>
 * function.</p>
 *
 * @param srcImgs Input 8-bit 3-channel images sequence. All images should have
 * the same type and size.
 * @param dst Output image with the same size and type as <code>srcImgs</code>
 * images.
 * @param imgToDenoiseIndex Target image to denoise index in <code>srcImgs</code>
 * sequence
 * @param temporalWindowSize Number of surrounding images to use for target
 * image denoising. Should be odd. Images from <code>imgToDenoiseIndex -
 * temporalWindowSize / 2</code> to <code>imgToDenoiseIndex - temporalWindowSize
 * / 2</code> from <code>srcImgs</code> will be used to denoise
 * <code>srcImgs[imgToDenoiseIndex]</code> image.
 *
 * @see <a href="http://docs.opencv.org/modules/photo/doc/denoising.html#fastnlmeansdenoisingcoloredmulti">org.opencv.photo.Photo.fastNlMeansDenoisingColoredMulti</a>
 */
    public static void fastNlMeansDenoisingColoredMulti(List<Mat> srcImgs, Mat dst, int imgToDenoiseIndex, int temporalWindowSize)
    {
        Mat srcImgs_mat = Converters.vector_Mat_to_Mat(srcImgs);
        fastNlMeansDenoisingColoredMulti_1(srcImgs_mat.nativeObj, dst.nativeObj, imgToDenoiseIndex, temporalWindowSize);

        return;
    }


    //
    // C++:  void fastNlMeansDenoisingMulti(vector_Mat srcImgs, Mat& dst, int imgToDenoiseIndex, int temporalWindowSize, float h = 3, int templateWindowSize = 7, int searchWindowSize = 21)
    //

/**
 * <p>Modification of <code>fastNlMeansDenoising</code> function for images
 * sequence where consequtive images have been captured in small period of time.
 * For example video. This version of the function is for grayscale images or
 * for manual manipulation with colorspaces.
 * For more details see http://citeseerx.ist.psu.edu/viewdoc/summary?doi=10.1.1.131.6394</p>
 *
 * @param srcImgs Input 8-bit 1-channel, 2-channel or 3-channel images sequence.
 * All images should have the same type and size.
 * @param dst Output image with the same size and type as <code>srcImgs</code>
 * images.
 * @param imgToDenoiseIndex Target image to denoise index in <code>srcImgs</code>
 * sequence
 * @param temporalWindowSize Number of surrounding images to use for target
 * image denoising. Should be odd. Images from <code>imgToDenoiseIndex -
 * temporalWindowSize / 2</code> to <code>imgToDenoiseIndex - temporalWindowSize
 * / 2</code> from <code>srcImgs</code> will be used to denoise
 * <code>srcImgs[imgToDenoiseIndex]</code> image.
 * @param h Parameter regulating filter strength for luminance component. Bigger
 * h value perfectly removes noise but also removes image details, smaller h
 * value preserves details but also preserves some noise
 * @param templateWindowSize Size in pixels of the template patch that is used
 * to compute weights. Should be odd. Recommended value 7 pixels
 * @param searchWindowSize Size in pixels of the window that is used to compute
 * weighted average for given pixel. Should be odd. Affect performance linearly:
 * greater searchWindowsSize - greater denoising time. Recommended value 21
 * pixels
 *
 * @see <a href="http://docs.opencv.org/modules/photo/doc/denoising.html#fastnlmeansdenoisingmulti">org.opencv.photo.Photo.fastNlMeansDenoisingMulti</a>
 */
    public static void fastNlMeansDenoisingMulti(List<Mat> srcImgs, Mat dst, int imgToDenoiseIndex, int temporalWindowSize, float h, int templateWindowSize, int searchWindowSize)
    {
        Mat srcImgs_mat = Converters.vector_Mat_to_Mat(srcImgs);
        fastNlMeansDenoisingMulti_0(srcImgs_mat.nativeObj, dst.nativeObj, imgToDenoiseIndex, temporalWindowSize, h, templateWindowSize, searchWindowSize);

        return;
    }

/**
 * <p>Modification of <code>fastNlMeansDenoising</code> function for images
 * sequence where consequtive images have been captured in small period of time.
 * For example video. This version of the function is for grayscale images or
 * for manual manipulation with colorspaces.
 * For more details see http://citeseerx.ist.psu.edu/viewdoc/summary?doi=10.1.1.131.6394</p>
 *
 * @param srcImgs Input 8-bit 1-channel, 2-channel or 3-channel images sequence.
 * All images should have the same type and size.
 * @param dst Output image with the same size and type as <code>srcImgs</code>
 * images.
 * @param imgToDenoiseIndex Target image to denoise index in <code>srcImgs</code>
 * sequence
 * @param temporalWindowSize Number of surrounding images to use for target
 * image denoising. Should be odd. Images from <code>imgToDenoiseIndex -
 * temporalWindowSize / 2</code> to <code>imgToDenoiseIndex - temporalWindowSize
 * / 2</code> from <code>srcImgs</code> will be used to denoise
 * <code>srcImgs[imgToDenoiseIndex]</code> image.
 *
 * @see <a href="http://docs.opencv.org/modules/photo/doc/denoising.html#fastnlmeansdenoisingmulti">org.opencv.photo.Photo.fastNlMeansDenoisingMulti</a>
 */
    public static void fastNlMeansDenoisingMulti(List<Mat> srcImgs, Mat dst, int imgToDenoiseIndex, int temporalWindowSize)
    {
        Mat srcImgs_mat = Converters.vector_Mat_to_Mat(srcImgs);
        fastNlMeansDenoisingMulti_1(srcImgs_mat.nativeObj, dst.nativeObj, imgToDenoiseIndex, temporalWindowSize);

        return;
    }


    //
    // C++:  void inpaint(Mat src, Mat inpaintMask, Mat& dst, double inpaintRadius, int flags)
    //

/**
 * <p>Restores the selected region in an image using the region neighborhood.</p>
 *
 * <p>The function reconstructs the selected image area from the pixel near the
 * area boundary. The function may be used to remove dust and scratches from a
 * scanned photo, or to remove undesirable objects from still images or video.
 * See http://en.wikipedia.org/wiki/Inpainting for more details.</p>
 *
 * <p>Note:</p>
 * <ul>
 *   <li> An example using the inpainting technique can be found at
 * opencv_source_code/samples/cpp/inpaint.cpp
 *   <li> (Python) An example using the inpainting technique can be found at
 * opencv_source_code/samples/python2/inpaint.py
 * </ul>
 *
 * @param src Input 8-bit 1-channel or 3-channel image.
 * @param inpaintMask Inpainting mask, 8-bit 1-channel image. Non-zero pixels
 * indicate the area that needs to be inpainted.
 * @param dst Output image with the same size and type as <code>src</code>.
 * @param inpaintRadius Radius of a circular neighborhood of each point
 * inpainted that is considered by the algorithm.
 * @param flags Inpainting method that could be one of the following:
 * <ul>
 *   <li> INPAINT_NS Navier-Stokes based method.
 *   <li> INPAINT_TELEA Method by Alexandru Telea [Telea04].
 * </ul>
 *
 * @see <a href="http://docs.opencv.org/modules/photo/doc/inpainting.html#inpaint">org.opencv.photo.Photo.inpaint</a>
 */
    public static void inpaint(Mat src, Mat inpaintMask, Mat dst, double inpaintRadius, int flags)
    {

        inpaint_0(src.nativeObj, inpaintMask.nativeObj, dst.nativeObj, inpaintRadius, flags);

        return;
    }




    // C++:  void fastNlMeansDenoising(Mat src, Mat& dst, float h = 3, int templateWindowSize = 7, int searchWindowSize = 21)
    private static native void fastNlMeansDenoising_0(long src_nativeObj, long dst_nativeObj, float h, int templateWindowSize, int searchWindowSize);
    private static native void fastNlMeansDenoising_1(long src_nativeObj, long dst_nativeObj);

    // C++:  void fastNlMeansDenoisingColored(Mat src, Mat& dst, float h = 3, float hColor = 3, int templateWindowSize = 7, int searchWindowSize = 21)
    private static native void fastNlMeansDenoisingColored_0(long src_nativeObj, long dst_nativeObj, float h, float hColor, int templateWindowSize, int searchWindowSize);
    private static native void fastNlMeansDenoisingColored_1(long src_nativeObj, long dst_nativeObj);

    // C++:  void fastNlMeansDenoisingColoredMulti(vector_Mat srcImgs, Mat& dst, int imgToDenoiseIndex, int temporalWindowSize, float h = 3, float hColor = 3, int templateWindowSize = 7, int searchWindowSize = 21)
    private static native void fastNlMeansDenoisingColoredMulti_0(long srcImgs_mat_nativeObj, long dst_nativeObj, int imgToDenoiseIndex, int temporalWindowSize, float h, float hColor, int templateWindowSize, int searchWindowSize);
    private static native void fastNlMeansDenoisingColoredMulti_1(long srcImgs_mat_nativeObj, long dst_nativeObj, int imgToDenoiseIndex, int temporalWindowSize);

    // C++:  void fastNlMeansDenoisingMulti(vector_Mat srcImgs, Mat& dst, int imgToDenoiseIndex, int temporalWindowSize, float h = 3, int templateWindowSize = 7, int searchWindowSize = 21)
    private static native void fastNlMeansDenoisingMulti_0(long srcImgs_mat_nativeObj, long dst_nativeObj, int imgToDenoiseIndex, int temporalWindowSize, float h, int templateWindowSize, int searchWindowSize);
    private static native void fastNlMeansDenoisingMulti_1(long srcImgs_mat_nativeObj, long dst_nativeObj, int imgToDenoiseIndex, int temporalWindowSize);

    // C++:  void inpaint(Mat src, Mat inpaintMask, Mat& dst, double inpaintRadius, int flags)
    private static native void inpaint_0(long src_nativeObj, long inpaintMask_nativeObj, long dst_nativeObj, double inpaintRadius, int flags);

}
