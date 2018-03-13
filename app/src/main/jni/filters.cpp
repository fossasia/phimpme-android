#include <filters.h>

using namespace std;
using namespace cv;

extern "C" {

void gammaCorrection(Mat &src, Mat &dst) {
    register int x, y, i;
    dst = Mat::zeros(src.size(), src.type());
    uchar r, g, b;
    float redAverage = 0;
    float greenAverage = 0;
    float blueAverage = 0;

    Scalar avg = mean(src);
    redAverage = (float) avg[0];
    greenAverage = (float) avg[1];
    blueAverage = (float) avg[2];

    float gammaRed = log(128.0f / 255) / log(redAverage / 255);
    float gammaGreen = log(128.0f / 255) / log(greenAverage / 255);
    float gammaBlue = log(128.0f / 255) / log(blueAverage / 255);
    int redLut[256];
    int greenLut[256];
    int blueLut[256];
    for (i = 0; i < 256; i++) {
        redLut[i] = -1;
        greenLut[i] = -1;
        blueLut[i] = -1;
    }
    for (y = 0; y < src.rows; y++) {
        for (x = 0; x < src.cols; x++) {
            r = src.at<Vec3b>(y, x)[0];
            g = src.at<Vec3b>(y, x)[1];
            b = src.at<Vec3b>(y, x)[2];

            if (redLut[r] == -1)
                redLut[r] = saturate_cast<uchar>(255.0f * powf((r / 255.0f), gammaRed));
            dst.at<Vec3b>(y, x)[0] = saturate_cast<uchar>(redLut[r]);

            if (greenLut[g] == -1)
                greenLut[g] = saturate_cast<uchar>(255.0f * powf((g / 255.0f), gammaGreen));
            dst.at<Vec3b>(y, x)[1] = saturate_cast<uchar>(greenLut[g]);

            if (blueLut[b] == -1)
                blueLut[b] = saturate_cast<uchar>(255.0f * powf((b / 255.0f), gammaBlue));
            dst.at<Vec3b>(y, x)[2] = saturate_cast<uchar>(blueLut[b]);
        }
    }
}

void applyBW(cv::Mat &src, cv::Mat &dst, int val) {
    register int x, y;
    float opacity = val * 0.01f;
    cvtColor(src, src, CV_BGRA2BGR);
    dst = Mat::zeros(src.size(), src.type());
    uchar grey, r, g, b,bri;

    for (y = 0; y < src.rows; y++) {
        for (x = 0; x < src.cols; x++) {
            r = src.at<Vec3b>(y, x)[0];
            g = src.at<Vec3b>(y, x)[1];
            b = src.at<Vec3b>(y, x)[2];
            grey = (uchar) blackAndWhite(r, g, b);
            bri = saturate_cast<uchar>((0.6 * r - g * 0.4 - b * 0.4));
            dst.at<Vec3b>(y, x)[0] =
                    saturate_cast<uchar>((1 - opacity) * r + opacity * grey + opacity * bri);
            dst.at<Vec3b>(y, x)[1] =
                    saturate_cast<uchar>((1 - opacity) * g + opacity * grey + opacity * bri);
            dst.at<Vec3b>(y, x)[2] =
                    saturate_cast<uchar>((1 - opacity) * b + opacity * grey + opacity * bri);
        }
    }
}

void applyNegative(cv::Mat &src, cv::Mat &dst, int val) {
    register int x, y;
    float opacity = val * 0.01f;
    cvtColor(src, src, CV_BGRA2BGR);
    dst = Mat::zeros(src.size(), src.type());
    uchar r, g, b;

    for (y = 0; y < src.rows; y++) {
        for (x = 0; x < src.cols; x++) {
            r = src.at<Vec3b>(y, x)[0];
            g = src.at<Vec3b>(y, x)[1];
            b = src.at<Vec3b>(y, x)[2];
            dst.at<Vec3b>(y, x)[0] =
                    saturate_cast<uchar>(opacity * (255 - r));
            dst.at<Vec3b>(y, x)[1] =
                    saturate_cast<uchar>(opacity * (255 - g));
            dst.at<Vec3b>(y, x)[2] =
                    saturate_cast<uchar>(opacity * (255 - b));
        }
    }
}

void applyGreenBoostEffect(cv::Mat &src, cv::Mat &dst, int val) {
    register int x, y;
    float opacity = val * 0.01f;
    cvtColor(src, src, CV_BGRA2BGR);
    dst = Mat::zeros(src.size(), src.type());
    uchar r, g, b,val1;

    for (y = 0; y < src.rows; y++) {
        for (x = 0; x < src.cols; x++) {
            r = src.at<Vec3b>(y, x)[0];
            g = src.at<Vec3b>(y, x)[1];
            b = src.at<Vec3b>(y, x)[2];
            val1 = saturate_cast<uchar>(g*(1+opacity));
            if(val1 > 255) {
                val1 = 255;
            }
            dst.at<Vec3b>(y, x)[0] =
                    saturate_cast<uchar>(r);
            dst.at<Vec3b>(y, x)[1] =
                    saturate_cast<uchar>(val1);
            dst.at<Vec3b>(y, x)[2] =
                    saturate_cast<uchar>(b);
        }
    }
}

void applyColorBoostEffect(cv::Mat &src, cv::Mat &dst, int val) {
    register int x, y;
    float opacity = val * 0.01f;
    cvtColor(src, src, CV_BGRA2BGR);
    dst = Mat::zeros(src.size(), src.type());
    uchar r, g, b;
    uchar val1,val2,val3;

    for (y = 0; y < src.rows; y++) {
        for (x = 0; x < src.cols; x++) {
            r = src.at<Vec3b>(y, x)[0];
            g = src.at<Vec3b>(y, x)[1];
            b = src.at<Vec3b>(y, x)[2];
            val1 = saturate_cast<uchar>(r*(1+opacity));
            if(val1 > 255) {
                val1 = 255;
            }
            val2 = saturate_cast<uchar>(g*(1+opacity));
            if(val2 > 255) {
                val2 = 255;
            }
            val3 = saturate_cast<uchar>(b*(1+opacity));
            if(val3 > 255) {
                val3 = 255;
            }
            dst.at<Vec3b>(y, x)[0] =
                    saturate_cast<uchar>(val1);
            dst.at<Vec3b>(y, x)[1] =
                    saturate_cast<uchar>(val2);
            dst.at<Vec3b>(y, x)[2] =
                    saturate_cast<uchar>(val3);
        }
    }
}

void applyCyanise(cv::Mat &src, cv::Mat &dst, int val) {
    register int x, y;
    float opacity = val * 0.01f;
    cvtColor(src, src, CV_BGRA2BGR);
    dst = Mat::zeros(src.size(), src.type());
    uchar r, g, b;
    uchar val2;
    uchar val3;

    for (y = 0; y < src.rows; y++) {
        for (x = 0; x < src.cols; x++) {
            r = src.at<Vec3b>(y, x)[0];
            g = src.at<Vec3b>(y, x)[1];
            b = src.at<Vec3b>(y, x)[2];

            val2 = saturate_cast<uchar>(g*(1+opacity));
            if(val2 > 255) {
                val2 = 255;
            }

            val3 = saturate_cast<uchar>(b*(1+opacity));
            if(val3 > 255) {
                val3 = 255;
            }

            dst.at<Vec3b>(y, x)[0] =
                    saturate_cast<uchar>(r);
            dst.at<Vec3b>(y, x)[1] =
                    saturate_cast<uchar>(val2);
            dst.at<Vec3b>(y, x)[2] =
                    saturate_cast<uchar>(val3);
        }
    }
}

void applySajuno(cv::Mat &src, cv::Mat &dst, int val) {
    register int x, y;
    double op = 0.5 + 0.35 * val / 100.0;
    cvtColor(src, src, CV_BGRA2BGR);
    dst = Mat::zeros(src.size(), src.type());
    uchar r, g, b;
    int val1;

    for (y = 0; y < src.rows; y++) {
        for (x = 0; x < src.cols; x++) {
            r = src.at<Vec3b>(y, x)[0];
            g = src.at<Vec3b>(y, x)[1];
            b = src.at<Vec3b>(y, x)[2];

            val1 = saturate_cast<uchar>((op * r - g * 0.4 - b * 0.4));
            dst.at<Vec3b>(y, x)[0] =
                    saturate_cast<uchar>(r + val1);
            dst.at<Vec3b>(y, x)[1] =
                    saturate_cast<uchar>(g + val1);
            dst.at<Vec3b>(y, x)[2] =
                    saturate_cast<uchar>(b + val1);
        }
    }
}

void applyBoostRedEffect(cv::Mat &src, cv::Mat &dst, int val) {
    register int x, y;
    float opacity = val * 0.01f;
    cvtColor(src, src, CV_BGRA2BGR);
    dst = Mat::zeros(src.size(), src.type());
    uchar r, g, b,val1;

    for (y = 0; y < src.rows; y++) {
        for (x = 0; x < src.cols; x++) {
            r = src.at<Vec3b>(y, x)[0];
            g = src.at<Vec3b>(y, x)[1];
            b = src.at<Vec3b>(y, x)[2];
            val1 = saturate_cast<uchar>(r*(1+opacity));
            if(val1 > 255) {
                val1 = 255;
            }
            dst.at<Vec3b>(y, x)[0] =
                    saturate_cast<uchar>(val1);
            dst.at<Vec3b>(y, x)[1] =
                    saturate_cast<uchar>(g);
            dst.at<Vec3b>(y, x)[2] =
                    saturate_cast<uchar>(b);
        }
    }
}

void applyManglow(cv::Mat &src, cv::Mat &dst, int val) {
    register int x, y, i;
    double opacity = 0.01 * val;
    double bri_op = 0.4 + 0.0035 * val;
    cvtColor(src, src, CV_BGRA2BGR);

    dst = Mat::zeros(src.size(), src.type());
    uchar ri, gi, bi, r, g, b, bri;
    uchar brightnessLut[256], contrastLut[256], lut250[256], lut220[256], lut175[256];
    for (i = 0; i < 256; i++) {
        float pixelf = i / 255.0f;
        brightnessLut[i] = (uchar) (255 * applyBrightnessToPixelComponent(pixelf, 0.4724f));
        contrastLut[i] = (uchar) (255 * applyContrastToPixelComponent(pixelf, 0.3149f));
        lut250[i] = multiplyPixelComponents(250, (uchar) i);
        lut220[i] = multiplyPixelComponents(220, (uchar) i);
        lut175[i] = multiplyPixelComponents(175, (uchar) i);
    }

    for (y = 0; y < src.rows; y++) {
        for (x = 0; x < src.cols; x++) {

            ri = src.at<Vec3b>(y, x)[0];
            gi = src.at<Vec3b>(y, x)[1];
            bi = src.at<Vec3b>(y, x)[2];

            bri = saturate_cast<uchar>((bri_op * ri - gi * 0.4 - bi * 0.4));

            r = contrastLut[brightnessLut[ri]];
            g = contrastLut[brightnessLut[gi]];
            b = contrastLut[brightnessLut[bi]];

            g = saturate_cast<uchar> ((g * 0.87f) + 33);
            b = saturate_cast<uchar> ((b * 0.439f) + 143);

            dst.at<Vec3b>(y, x)[0] =
                    saturate_cast<uchar>((1 - opacity) * ri + opacity * lut250[r] + bri);
            dst.at<Vec3b>(y, x)[1] =
                    saturate_cast<uchar>((1 - opacity) * gi + opacity * lut220[g] + bri);
            dst.at<Vec3b>(y, x)[2] =
                    saturate_cast<uchar>((1 - opacity) * bi + opacity * lut175[b] + bri);
        }
    }
}

void applyPalacia(cv::Mat &src, cv::Mat &dst, int val) {

    int clip = 1 + (int) (val * 0.04);

    cvtColor(src, src, CV_BGRA2BGR);
    dst = Mat::zeros(src.size(), src.type());

    Mat gamma_mat;
    gammaCorrection(src, gamma_mat);

    Mat lab_image;
    cvtColor(gamma_mat, lab_image, CV_BGR2Lab);

    vector<Mat> lab_planes(3);
    split(lab_image, lab_planes);

    Ptr<CLAHE> clahe = createCLAHE();
    clahe->setClipLimit(clip);
    Mat clahe_mat;
    clahe->apply(lab_planes[0], clahe_mat);

    clahe_mat.copyTo(lab_planes[0]);
    merge(lab_planes, lab_image);
    cvtColor(lab_image, dst, CV_Lab2BGR);
}

void applyAnax(cv::Mat &src, cv::Mat &dst, int val) {
    register int i, j, x, y, tr, tb, tg;
    double opacity = val / 100.0;
    cvtColor(src, src, CV_BGRA2BGR);
    dst = Mat::zeros(src.size(), src.type());
    register uchar sepia, red, green, blue;
    short int overlayLut[256][256];

    for (i = 256; i--;) {
        for (j = 256; j--;) {
            overlayLut[i][j] = -1;
        }
    }

    for (y = 0; y < src.rows; y++) {
        for (x = 0; x < src.cols; x++) {
            HSBColour hsb;
            float value;
            red = src.at<Vec3b>(y, x)[0];
            green = src.at<Vec3b>(y, x)[1];
            blue = src.at<Vec3b>(y, x)[2];

            getBrightness(red, green, blue, &value);

            uchar r = LUTxproRed[red];
            uchar g = LUTxproGreen[green];
            uchar b = LUTxproBlue[blue];
            rgbToHsb(r, g, b, &hsb);
            hsb.b = value;
            hsbToRgb(&hsb, &r, &g, &b);

            if (overlayLut[red][r] == -1) {
                overlayLut[red][r] = overlayComponents(red, r, 1.0f);
            }
            tr = overlayLut[red][r];
            if (overlayLut[green][g] == -1) {
                overlayLut[green][g] = overlayComponents(green, g, 1.0f);
            }
            tg = overlayLut[green][g];
            if (overlayLut[blue][b] == -1) {
                overlayLut[blue][b] = overlayComponents(blue, b, 1.0f);
            }
            tb = overlayLut[blue][b];

            dst.at<Vec3b>(y, x)[2] =
                    saturate_cast<uchar>((1 - opacity) * (src.at<Vec3b>(y, x)[2]) + opacity * tb);
            dst.at<Vec3b>(y, x)[1] =
                    saturate_cast<uchar>((1 - opacity) * (src.at<Vec3b>(y, x)[1]) + opacity * tg);
            dst.at<Vec3b>(y, x)[0] =
                    saturate_cast<uchar>((1 - opacity) * (src.at<Vec3b>(y, x)[0]) + opacity * tr);
        }
    }
}

void applySepia(cv::Mat &src, cv::Mat &dst, int val) {
    register int x, y;
    double opacity = val / 100.0;
    cvtColor(src, src, CV_BGRA2BGR);
    dst = Mat::zeros(src.size(), src.type());
    uchar sepia;
    uchar r, g, b;
    int bri;
    for (y = 0; y < src.rows; y++) {
        for (x = 0; x < src.cols; x++) {
            r = src.at<Vec3b>(y, x)[0];
            g = src.at<Vec3b>(y, x)[1];
            b = src.at<Vec3b>(y, x)[2];
            sepia = (uchar) sepiaLum(src.at<Vec3b>(y, x)[0], src.at<Vec3b>(y, x)[1],
                                     src.at<Vec3b>(y, x)[2]);
            bri = saturate_cast<uchar>((0.75 * r - g * 0.4 - b * 0.4));

            dst.at<Vec3b>(y, x)[0] =
                    saturate_cast<uchar>((1 - opacity) * (src.at<Vec3b>(y, x)[0]) +
                                         opacity * LUTsepiaRed[sepia] + bri);
            dst.at<Vec3b>(y, x)[1] =
                    saturate_cast<uchar>((1 - opacity) * (src.at<Vec3b>(y, x)[1]) +
                                         opacity * LUTsepiaGreen[sepia] + bri);
            dst.at<Vec3b>(y, x)[2] =
                    saturate_cast<uchar>((1 - opacity) * (src.at<Vec3b>(y, x)[2]) +
                                         opacity * LUTsepiaBlue[sepia] + bri);
        }
    }
}

void applyBlueBoostEffect(cv::Mat &src, cv::Mat &dst, int val) {
    register int x, y;
    float opacity = val * 0.01f;
    cvtColor(src, src, CV_BGRA2BGR);
    dst = Mat::zeros(src.size(), src.type());
    uchar r, g, b,val1;

    for (y = 0; y < src.rows; y++) {
        for (x = 0; x < src.cols; x++) {
            r = src.at<Vec3b>(y, x)[0];
            g = src.at<Vec3b>(y, x)[1];
            b = src.at<Vec3b>(y, x)[2];
            val1 = saturate_cast<uchar>(b*(1+opacity));
            if(val1 > 255) {
                val1 = 255;
            }
            dst.at<Vec3b>(y, x)[0] =
                    saturate_cast<uchar>(r);
            dst.at<Vec3b>(y, x)[1] =
                    saturate_cast<uchar>(g);
            dst.at<Vec3b>(y, x)[2] =
                    saturate_cast<uchar>(val1);
        }
    }
}

void applyAnsel(cv::Mat &src, cv::Mat &dst, int val) {
    register int x, y;
    double opacity = val / 100.0;
    cvtColor(src, src, CV_BGRA2BGR);
    dst = Mat::zeros(src.size(), src.type());
    uchar grey, r, g, b, eff;
    for (y = 0; y < src.rows; y++) {
        for (x = 0; x < src.cols; x++) {
            r = src.at<Vec3b>(y, x)[0];
            g = src.at<Vec3b>(y, x)[1];
            b = src.at<Vec3b>(y, x)[2];
            grey = saturate_cast<uchar> blackAndWhite(r, g, b);
            eff = (uchar) hardLightLayerPixelComponent(grey, grey);

            dst.at<Vec3b>(y, x)[0] =
                    saturate_cast<uchar>((1 - opacity) * r + opacity * eff);
            dst.at<Vec3b>(y, x)[1] =
                    saturate_cast<uchar>((1 - opacity) * g + opacity * eff);
            dst.at<Vec3b>(y, x)[2] =
                    saturate_cast<uchar>((1 - opacity) * b + opacity * eff);
        }
    }
}

void applyHistEq(cv::Mat &src, cv::Mat &dst, int val) {
    float opacity = 0.01f * val;
    cvtColor(src, src, CV_BGRA2BGR);
    dst = Mat::zeros(src.size(), src.type());
    vector<Mat> planes(3), planes1(3);
    split(src, planes);

    equalizeHist(planes[0], planes1[0]);
    equalizeHist(planes[1], planes1[1]);
    equalizeHist(planes[2], planes1[2]);

    merge(planes1, dst);

    dst = (1 - opacity) * src + opacity * dst;
    planes.clear();
    planes1.clear();
}

void applyThreshold(cv::Mat &src, cv::Mat &dst, int val) {
    register int x, y;
    double opacity = val / 100.0;
    cvtColor(src, src, CV_BGRA2GRAY);
    dst = Mat::zeros(src.size(), src.type());
    int thres = 220 - (int) (opacity * 190);
    uchar color;
    for (y = 0; y < src.rows; y++) {
        for (x = 0; x < src.cols; x++) {
            if (src.at<uchar>(y, x) < thres) color = 0;
            else color = 255;
            dst.at<uchar>(y, x) =
                    saturate_cast<uchar>(color);
        }
    }
}

void applyGrain(cv::Mat &src, cv::Mat &dst, int val) {
    register int x, y;
    double opacity = val / 100.0;
    cvtColor(src, src, CV_BGRA2BGR);
    dst = src.clone();
    time_t t;
    srand((unsigned) time(&t));

    for (y = 0; y < src.rows; y++) {
        for (x = 0; x < src.cols; x++) {
            int rval = rand() % 255;
            if (rand() % 100 < (int) (opacity * 20)) {
                dst.at<Vec3b>(y, x)[2] =
                        saturate_cast<uchar>(
                                (1 - opacity) * (dst.at<Vec3b>(y, x)[2]) + opacity * rval);
                dst.at<Vec3b>(y, x)[1] =
                        saturate_cast<uchar>(
                                (1 - opacity) * (dst.at<Vec3b>(y, x)[1]) + opacity * rval);
                dst.at<Vec3b>(y, x)[0] =
                        saturate_cast<uchar>(
                                (1 - opacity) * (dst.at<Vec3b>(y, x)[0]) + opacity * rval);
            }
        }
    }
}

void applyCyano(cv::Mat &src, cv::Mat &dst, int val) {
    register int x, y;
    double opacity = val / 100.0;
    cvtColor(src, src, CV_BGRA2BGR);
    dst = Mat::zeros(src.size(), src.type());
    register uchar grey, r, g, b;
    for (y = 0; y < src.rows; y++) {
        for (x = 0; x < src.cols; x++) {
            grey = (uchar) ((src.at<Vec3b>(y, x)[0] * 0.5f) + (src.at<Vec3b>(y, x)[1] * 0.39f) +
                            (src.at<Vec3b>(y, x)[2] * 0.11f));
            r = (uchar) ceilComponent(61.0f + grey);
            g = (uchar) ceilComponent(87.0f + grey);
            b = (uchar) ceilComponent(136.0f + grey);

            dst.at<Vec3b>(y, x)[2] =
                    saturate_cast<uchar>((1 - opacity) * (src.at<Vec3b>(y, x)[2]) +
                                         opacity * overlayComponents(grey, b, 0.9));
            dst.at<Vec3b>(y, x)[1] =
                    saturate_cast<uchar>((1 - opacity) * (src.at<Vec3b>(y, x)[1]) +
                                         opacity * overlayComponents(grey, g, 0.9));
            dst.at<Vec3b>(y, x)[0] =
                    saturate_cast<uchar>((1 - opacity) * (src.at<Vec3b>(y, x)[0]) +
                                         opacity * overlayComponents(grey, r, 0.9));
        }
    }
}

void applyFade(cv::Mat &src, cv::Mat &dst, int val)
{
    cvtColor(src,src,COLOR_RGB2GRAY);
    dst = src.clone();
    for(int y=0 ;y < src.rows ; y++)
    {
        for(int x=0 ;x < src.rows ; x++) {
            Vec3b intensity = src.at<Vec3b>(y, x);
            uchar blue = intensity.val[0];
            uchar green = intensity.val[1];
            uchar red = intensity.val[2];

            dst.at<Vec3b>(y, x)[0] = saturate_cast<uchar> (blue + 0.5 *val );
            dst.at<Vec3b>(y, x)[1] = saturate_cast<uchar> (green + 0.5 *val);
            dst.at<Vec3b>(y, x)[2] = saturate_cast<uchar> (red + 0.5 *val);
        }
    }
}
}
