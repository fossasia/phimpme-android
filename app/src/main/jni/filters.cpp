#include <filters.h>

using namespace std;
using namespace cv;

extern "C"{

void applyBW(cv::Mat &src, cv::Mat &dst, int val){
    register int x,y;
    double opacity = val/100.0;
    cvtColor(src,src,CV_BGRA2BGR);
    dst = Mat::zeros( src.size(), src.type() );
    uchar grey;
    for (y = 0; y < src.rows; y++) {
        for (x = 0; x < src.cols; x++) {
            grey = (uchar)blackAndWhite(src.at<Vec3b>(y, x)[2], src.at<Vec3b>(y, x)[1], src.at<Vec3b>(y, x)[0]);

            dst.at<Vec3b>(y, x)[0] =
                    saturate_cast<uchar>((1-opacity) * (src.at<Vec3b>(y, x)[0]) + opacity * grey);
            dst.at<Vec3b>(y, x)[1] =
                    saturate_cast<uchar>((1-opacity) * (src.at<Vec3b>(y, x)[1]) + opacity * grey);
            dst.at<Vec3b>(y, x)[2] =
                    saturate_cast<uchar>((1-opacity) * (src.at<Vec3b>(y, x)[2]) + opacity * grey);
        }
    }
}


void applyAnsel(cv::Mat &src, cv::Mat &dst, int val){
    register int x,y;
    double opacity = val/100.0;
    cvtColor(src,src,CV_BGRA2BGR);
    dst = Mat::zeros( src.size(), src.type() );
    uchar grey;
    uchar eff;
    for (y = 0; y < src.rows; y++) {
        for (x = 0; x < src.cols; x++) {
            grey = (uchar) blackAndWhite(src.at<Vec3b>(y, x)[2], src.at<Vec3b>(y, x)[1], src.at<Vec3b>(y, x)[0]);
            eff = (uchar) hardLightLayerPixelComponent(grey, grey);

            dst.at<Vec3b>(y, x)[0] =
                    saturate_cast<uchar>((1-opacity) * (src.at<Vec3b>(y, x)[0]) + opacity * eff);
            dst.at<Vec3b>(y, x)[1] =
                    saturate_cast<uchar>((1-opacity) * (src.at<Vec3b>(y, x)[1]) + opacity * eff);
            dst.at<Vec3b>(y, x)[2] =
                    saturate_cast<uchar>((1-opacity) * (src.at<Vec3b>(y, x)[2]) + opacity * eff);
        }
    }
}

void applySepia(cv::Mat &src, cv::Mat &dst, int val){
    register int x,y;
    double opacity = val/100.0;
    cvtColor(src,src,CV_BGRA2BGR);
    dst = Mat::zeros( src.size(), src.type() );
    register uchar sepia;
    for (y = 0; y < src.rows; y++) {
        for (x = 0; x < src.cols; x++) {
            sepia = (uchar)sepiaLum(src.at<Vec3b>(y, x)[0],src.at<Vec3b>(y, x)[1],src.at<Vec3b>(y, x)[2]);

            dst.at<Vec3b>(y, x)[2] =
                    saturate_cast<uchar>((1-opacity) * (src.at<Vec3b>(y, x)[2]) + opacity * LUTsepiaBlue[sepia]);
            dst.at<Vec3b>(y, x)[1] =
                    saturate_cast<uchar>((1-opacity) * (src.at<Vec3b>(y, x)[1]) + opacity * LUTsepiaGreen[sepia]);
            dst.at<Vec3b>(y, x)[0] =
                    saturate_cast<uchar>((1-opacity) * (src.at<Vec3b>(y, x)[0]) + opacity * LUTsepiaRed[sepia]);
        }
    }
}

void applyXpro(cv::Mat &src, cv::Mat &dst, int val){
    register int i,j,x,y,tr,tb,tg;
    double opacity = val/100.0;
    cvtColor(src,src,CV_BGRA2BGR);
    dst = Mat::zeros( src.size(), src.type() );
    register uchar sepia,red,green,blue;
    short int overlayLut[256][256];

    for (i = 256; i--;){
        for (j = 256; j--;){
            overlayLut[i][j] = -1;}}

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
                    saturate_cast<uchar>((1-opacity) * (src.at<Vec3b>(y, x)[2]) + opacity * tb);
            dst.at<Vec3b>(y, x)[1] =
                    saturate_cast<uchar>((1-opacity) * (src.at<Vec3b>(y, x)[1]) + opacity * tg);
            dst.at<Vec3b>(y, x)[0] =
                    saturate_cast<uchar>((1-opacity) * (src.at<Vec3b>(y, x)[0]) + opacity * tr);
        }
    }
}

void applyThreshold(cv::Mat &src, cv::Mat &dst, int val){
    register int x,y;
    double opacity = val/100.0;
    cvtColor(src,src,CV_BGRA2GRAY);
    dst = Mat::zeros( src.size(), src.type() );
    int thres = 220 - (int)(opacity * 190);
    uchar color;
    for (y = 0; y < src.rows; y++) {
        for (x = 0; x < src.cols; x++) {
            if (src.at<uchar>(y,x) < thres) color = 0;
            else color = 255;
            dst.at<uchar>(y, x) =
                    saturate_cast<uchar>(color);
        }
    }
}

void applyEdges(cv::Mat &src, cv::Mat &dst, int val){
    int lowThreshold = (int)((val/100.0)*70);
    int ratio = 3;
    int kernel_size = 3;
    Mat grey, detected_edges;
    cvtColor(src, grey, CV_BGRA2GRAY);
    blur(grey, detected_edges, Size(3, 3));
    dst.create(grey.size(), grey.type());
    Canny(detected_edges, detected_edges, lowThreshold, lowThreshold * ratio,kernel_size);
    dst = Scalar::all(0);
    bitwise_not(detected_edges,detected_edges);
    detected_edges.copyTo(dst, detected_edges);
    detected_edges.release();
    grey.release();
}

void applyGrain(cv::Mat &src, cv::Mat &dst, int val){
    register int x,y;
    double opacity = val/100.0;
    cvtColor(src,src,CV_BGRA2BGR);
    dst = src.clone();
    time_t t;
    srand((unsigned) time(&t));
    for (y = 0; y < src.rows; y++) {
        for (x = 0; x < src.cols; x++) {
            int rval = rand()%255;
            if (rand()%100 < (int)(opacity * 20)) {
                dst.at<Vec3b>(y, x)[2] =
                        saturate_cast<uchar>((1-opacity) * (dst.at<Vec3b>(y, x)[2]) + opacity * rval);
                dst.at<Vec3b>(y, x)[1] =
                        saturate_cast<uchar>((1-opacity) * (dst.at<Vec3b>(y, x)[1]) + opacity * rval);
                dst.at<Vec3b>(y, x)[0] =
                        saturate_cast<uchar>((1-opacity) * (dst.at<Vec3b>(y, x)[0]) + opacity * rval);
            }
        }
    }
}


void imHist( Mat &src, int rhist[], int ghist[], int bhist[])
{
    register unsigned int i,x,y;

    for(i = 0; i < 256; i++){
        rhist[i] = 0;
        ghist[i] = 0;
        bhist[i] = 0;
    }
    for (y = 0; y < src.rows; y++) {
        for (x = 0; x < src.cols; x++) {
            rhist[(int)src.at<Vec3b>(y,x)[0]]++;
            ghist[(int)src.at<Vec3b>(y,x)[1]]++;
            bhist[(int)src.at<Vec3b>(y,x)[2]]++;
        }
    }
}

void cumHist(int histogram[], int cumhistogram[]) {
    register unsigned int i;
    cumhistogram[0] = histogram[0];
    for(i = 1; i < 256; i++)
        cumhistogram[i] = histogram[i] + cumhistogram[i-1];
}

void applyHistEq(cv::Mat &src, cv::Mat &dst, int val){
    register int i,x,y;
    double opacity = val/100.0;
    cvtColor(src,src,CV_BGRA2BGR);
    dst = Mat::zeros( src.size(), src.type() );
    int rhist[256],ghist[256],bhist[256];
    imHist(src,rhist,ghist,bhist);
    long int length = src.cols * src.rows;
    float alpha = 255.0/length;

    double PrRk[256], PrGk[256], PrBk[256];
    for(i = 0; i < 256; i++){
        PrRk[i] = (double)rhist[i] / length;
        PrGk[i] = (double)ghist[i] / length;
        PrBk[i] = (double)bhist[i] / length;
    }

    int rcumhist[256], gcumhist[256], bcumhist[256];
    cumHist(rhist,rcumhist);
    cumHist(ghist,gcumhist);
    cumHist(bhist,bcumhist);

    int rSk[256], gSk[256], bSk[256];
    for(i = 0; i < 256; i++){
        rSk[i] = saturate_cast<uchar>(rcumhist[i] * alpha);
        gSk[i] = saturate_cast<uchar>(gcumhist[i] * alpha);
        bSk[i] = saturate_cast<uchar>(bcumhist[i] * alpha);
    }

    float rPsSk[256], gPsSk[256], bPsSk[256];
    for(i = 0; i < 256; i++){
        rPsSk[i] = 0;
        gPsSk[i] = 0;
        bPsSk[i] = 0;
    }

    for(i = 0; i < 256; i++){
        rPsSk[rSk[i]] += PrRk[i];
        gPsSk[gSk[i]] += PrGk[i];
        bPsSk[bSk[i]] += PrBk[i];
    }

    for (y = 0; y < src.rows; y++) {
        for (x = 0; x < src.cols; x++) {
            dst.at<Vec3b>(y, x)[0] =
                    saturate_cast<uchar>((1-opacity) * (src.at<Vec3b>(y, x)[0]) + opacity * rSk[src.at<Vec3b>(y, x)[0]]);
            dst.at<Vec3b>(y, x)[1] =
                    saturate_cast<uchar>((1-opacity) * (src.at<Vec3b>(y, x)[1]) + opacity * gSk[src.at<Vec3b>(y, x)[1]]);
            dst.at<Vec3b>(y, x)[2] =
                    saturate_cast<uchar>((1-opacity) * (src.at<Vec3b>(y, x)[2]) + opacity * bSk[src.at<Vec3b>(y, x)[2]]);

        }
    }
}

void applyCyano(cv::Mat &src, cv::Mat &dst, int val){
    register int x,y;
    double opacity = val/100.0;
    cvtColor(src,src,CV_BGRA2BGR);
    dst = Mat::zeros( src.size(), src.type() );
    register uchar grey,r,g,b;
    for (y = 0; y < src.rows; y++) {
        for (x = 0; x < src.cols; x++) {
            grey = ((src.at<Vec3b>(y, x)[0] * 0.222f) + (src.at<Vec3b>(y, x)[1] * 0.222f) + (src.at<Vec3b>(y, x)[2] * 0.222f));
            r = ceilComponent(61.0f + grey);
            g = ceilComponent(87.0f + grey);
            b = ceilComponent(136.0f + grey);
            grey = blackAndWhite(src.at<Vec3b>(y, x)[0], src.at<Vec3b>(y, x)[1], src.at<Vec3b>(y, x)[2]);

            dst.at<Vec3b>(y, x)[2] =
                    saturate_cast<uchar>((1-opacity) * (src.at<Vec3b>(y, x)[2]) + opacity * overlayComponents(grey, b, 0.9 ));
            dst.at<Vec3b>(y, x)[1] =
                    saturate_cast<uchar>((1-opacity) * (src.at<Vec3b>(y, x)[1]) + opacity * overlayComponents(grey, g, 0.9 ));
            dst.at<Vec3b>(y, x)[0] =
                    saturate_cast<uchar>((1-opacity) * (src.at<Vec3b>(y, x)[0]) + opacity * overlayComponents(grey, r, 0.9 ));
        }
    }


}

}
