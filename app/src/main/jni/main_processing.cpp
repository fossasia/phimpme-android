#include <jni.h>
#include <opencv2/core/core.hpp>
#include <opencv2/imgproc/imgproc.hpp>

using namespace std;
using namespace cv;
extern "C" {
    JNIEXPORT void JNICALL
    Java_org_fossasia_phimpme_editor_editimage_filter_PhotoProcessing_nativeApplyFilter(JNIEnv *env,
                                                                                    jclass type,
                                                                                    jint mode,
                                                                                    jint val,
                                                                                    jlong inpAddr,
                                                                                    jlong outAddr) {


        int lowThreshold = val;
        int ratio = 3;
        int kernel_size = 3;

        Mat &src = *(Mat*)inpAddr;
        Mat &dst = *(Mat*)outAddr;
        Mat grey,detected_edges;

        cvtColor( src, grey, CV_BGR2GRAY );

        blur( grey, detected_edges, Size(3,3) );
        dst.create( grey.size(), grey.type() );

        Canny( detected_edges, detected_edges, lowThreshold, lowThreshold*ratio, kernel_size );

        dst = Scalar::all(0);

        detected_edges.copyTo( dst, detected_edges);

        switch (mode){
            case 0:
                break;
        }
    }

    JNIEXPORT void JNICALL
    Java_org_fossasia_phimpme_editor_editimage_filter_PhotoProcessing_nativeEnhanceImage(JNIEnv *env,
                                                                                     jclass type,
                                                                                     jint mode,
                                                                                     jint val,
                                                                                     jlong inpAddr,
                                                                                     jlong outAddr) {

        int lowThreshold = val;
        int ratio = 3;
        int kernel_size = 3;

        Mat &src = *(Mat*)inpAddr;
        Mat &dst = *(Mat*)outAddr;
        Mat grey,detected_edges;

        cvtColor( src, grey, CV_BGR2GRAY );

        blur( grey, detected_edges, Size(3,3) );
        dst.create( grey.size(), grey.type() );

        Canny( detected_edges, detected_edges, lowThreshold, lowThreshold*ratio, kernel_size );

        dst = Scalar::all(0);

        src.copyTo( dst, detected_edges);

        switch (mode){
            case 0:
                break;
        }

    }

}

