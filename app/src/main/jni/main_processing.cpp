#include <jni.h>
#include <opencv2/core/core.hpp>
#include <opencv2/imgproc/imgproc.hpp>
#include <enhance.h>
#include <filters.h>

using namespace std;
using namespace cv;
extern "C" {
    JNIEXPORT void JNICALL
    Java_org_fossasia_phimpme_editor_filter_PhotoProcessing_nativeApplyFilter(JNIEnv *env,
                                                                                    jclass type,
                                                                                    jint mode,
                                                                                    jint val,
                                                                                    jlong inpAddr,
                                                                                    jlong outAddr) {

        Mat &src = *(Mat *) inpAddr;
        Mat &dst = *(Mat *) outAddr;

        switch (mode) {
            case 0:
                dst = src.clone();
                break;
            case 1: // Black and White
                applyBW(src, dst, val);
                break;
            case 2: // Testino
                applyAnsel(src, dst, val);
                break;
            case 3: // XPro
                applySepia(src, dst, val);
                break;
            case 4: // Retro
                applyXpro(src, dst, val);
                break;
            case 5: // Sepia
                applyThreshold(src, dst, val);
                break;
            case 6: // Cyano
                applyEdges(src, dst, val);
                break;
            case 7: // Georgia
                applyGrain(src, dst, val);
                break;
            case 8: // Sahara
                applyHistEq(src, dst, val);
                break;
            case 9: // HDR
                applyCyano(src, dst, val);
                break;/*
            case 10: // Black & White
                nativeApplyBW(value);
                break;
            case 11: // Ansel
                nativeApplyAnsel(value);
                break;
            case 12: // HistEqual
                nativeEqualizeHist(value);
                break;
            case 13: // Threshold
                nativeApplyThreshold(value);
                break;
            case 14: // Grain
                nativeApplyGrain(value);
                break;*/

            default:
                int lowThreshold = val;
                int ratio = 3;
                int kernel_size = 3;

                Mat grey, detected_edges;

                cvtColor(src, grey, CV_BGR2GRAY);

                blur(grey, detected_edges, Size(3, 3));
                dst.create(grey.size(), grey.type());

                Canny(detected_edges, detected_edges, lowThreshold, lowThreshold * ratio,
                      kernel_size);

                dst = Scalar::all(0);

                detected_edges.copyTo(dst, detected_edges);
                break;
        }
    }



    JNIEXPORT void JNICALL
    Java_org_fossasia_phimpme_editor_filter_PhotoProcessing_nativeEnhanceImage(JNIEnv *env,
                                                                                     jclass type,
                                                                                     jint mode,
                                                                                     jint val,
                                                                                     jlong inpAddr,
                                                                                     jlong outAddr) {
        Mat &src = *(Mat*)inpAddr;
        Mat &dst = *(Mat*)outAddr;

        switch (mode){
            case 0:
                adjustBrightness(src, dst, val);
                break;
            case 1:
                adjustContrast(src, dst, val);
                break;
            case 2:
                adjustHue(src, dst, val);
                break;
            case 3:
                adjustSaturation(src, dst, val);
                break;
            case 4:
                adjustTemperature(src, dst, val);
                break;
            case 5:
                adjustTint(src, dst, val);
                break;
            case 6:
                adjustVignette(src, dst, val);
                break;
            case 7:
                adjustSharpness(src, dst, val);
                break;
            default:
                break;
        }

    }



}

