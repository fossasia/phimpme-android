#include <enhance.h>

using namespace std;
using namespace cv;
extern "C" {

    void adjustBrightness(Mat &src, Mat &dst, int val) {
        int x,y,bright;
        cvtColor(src,src,CV_BGRA2BGR);
        bright = (int)(((val - 50) / 100.0) * 255);
        dst = Mat::zeros( src.size(), src.type() );
        for (y = 0; y < src.rows; y++) {
            for (x = 0; x < src.cols; x++) {
                dst.at<Vec3b>(y, x)[0] =
                           saturate_cast<uchar>((src.at<Vec3b>(y, x)[0]) + bright);
                dst.at<Vec3b>(y, x)[1] =
                        saturate_cast<uchar>((src.at<Vec3b>(y, x)[1]) + bright);
                dst.at<Vec3b>(y, x)[2] =
                        saturate_cast<uchar>((src.at<Vec3b>(y, x)[2]) + bright);
            }
        }
    }

    void adjustContrast(Mat &src, Mat &dst, int val) {
        int x,y,contrast;
        cvtColor(src,src,CV_BGRA2BGR);
        contrast = (int)(((float)(val-50)/100)*255);
        float factor = (float)(259*(contrast + 255))/(255*(259-contrast));

        dst = Mat::zeros( src.size(), src.type());
        for (y = 0; y < src.rows; y++) {
            for (x = 0; x < src.cols; x++) {
                dst.at<Vec3b>(y, x)[0] =
                        saturate_cast<uchar>((factor*(src.at<Vec3b>(y, x)[0]-128))+128);
                dst.at<Vec3b>(y, x)[1] =
                        saturate_cast<uchar>((factor*(src.at<Vec3b>(y, x)[1]-128))+128);
                dst.at<Vec3b>(y, x)[2] =
                        saturate_cast<uchar>((factor*(src.at<Vec3b>(y, x)[2]-128))+128);
            }
        }
    }

    void adjustHue(Mat &src, Mat &dst, int val) {
        int x,y;
        cvtColor(src,src,CV_BGRA2BGR);
        dst = Mat::zeros( src.size(), src.type());
        double H = 3.6*val;
        double h_cos = cos(H*PI/180);
        double h_sin = sin(H*PI/180);
        double r,g,b;
        for (y = 0; y < src.rows; y++) {
            for (x = 0; x < (src.cols); x++) {
                r = src.at<Vec3b>(y, x)[2]/255.0;
                g = src.at<Vec3b>(y, x)[1]/255.0;
                b = src.at<Vec3b>(y, x)[0]/255.0;

                dst.at<Vec3b>(y, x)[2] = saturate_cast<uchar>(255*((.299+.701*h_cos+.168*h_sin)*r
                                             + (.587-.587*h_cos+.330*h_sin)*g
                                             + (.114-.114*h_cos-.497*h_sin)*b));
                dst.at<Vec3b>(y, x)[1] = saturate_cast<uchar>(255*((.299-.299*h_cos-.328*h_sin)*r
                                               + (.587+.413*h_cos+.035*h_sin)*g
                                               + (.114-.114*h_cos+.292*h_sin)*b));
                dst.at<Vec3b>(y, x)[0] = saturate_cast<uchar>(255*((.299-.3*h_cos+1.25*h_sin)*r
                                              + (.587-.588*h_cos-1.05*h_sin)*g
                                              + (.114+.886*h_cos-.203*h_sin)*b));
            }
        }
    }

    void adjustSaturation(Mat &src, Mat &dst, int val) {
        int x,y;
        cvtColor(src,src,CV_BGRA2BGR);
        dst = Mat::zeros( src.size(), src.type());
        double sat = 2*(val/100.0);
        double temp;
        double r_val = 0.299, g_val = 0.587, b_val = 0.114;
        double r,g,b;
        for (y = 0; y < src.rows; y++) {
            for (x = 0; x < (4*src.cols)/3; x++) {
                r = src.at<Vec3b>(y, x)[2]/255.0;
                g = src.at<Vec3b>(y, x)[1]/255.0;
                b = src.at<Vec3b>(y, x)[0]/255.0;
                temp = sqrt( r * r * r_val +
                             g * g * g_val +
                             b * b * b_val );
                dst.at<Vec3b>(y, x)[2] = saturate_cast<uchar>(255*(temp + (r - temp) * sat));
                dst.at<Vec3b>(y, x)[1] = saturate_cast<uchar>(255*(temp + (g - temp) * sat));
                dst.at<Vec3b>(y, x)[0] = saturate_cast<uchar>(255*(temp + (b - temp) * sat));
            }
        }
    }

    void adjustTemperature(Mat &src, Mat &dst, int val) {
        int x,y,temp;
        cvtColor(src,src,CV_BGRA2BGR);
        temp = (int)(2.2*(val-50));
        dst = Mat::zeros( src.size(), src.type() );
        for (y = 0; y < src.rows; y++) {
            for (x = 0; x < src.cols; x++) {
                dst.at<Vec3b>(y, x)[0] =
                        saturate_cast<uchar>((src.at<Vec3b>(y, x)[0]) + temp);
                dst.at<Vec3b>(y, x)[1] =
                        saturate_cast<uchar>((src.at<Vec3b>(y, x)[1]));
                dst.at<Vec3b>(y, x)[2] =
                        saturate_cast<uchar>((src.at<Vec3b>(y, x)[2]) - temp);
            }
        }
    }

    void adjustTint(Mat &src, Mat &dst, int val) {
        int x,y;
        cvtColor(src,src,CV_BGRA2BGR);
        int tint = (int)(2.2*(val-50));
        dst = Mat::zeros( src.size(), src.type() );
        for (y = 0; y < src.rows; y++) {
            for (x = 0; x < src.cols; x++) {
                dst.at<Vec3b>(y, x)[0] =
                        saturate_cast<uchar>((src.at<Vec3b>(y, x)[0]));
                dst.at<Vec3b>(y, x)[1] =
                        saturate_cast<uchar>((src.at<Vec3b>(y, x)[1]) - tint);
                dst.at<Vec3b>(y, x)[2] =
                        saturate_cast<uchar>((src.at<Vec3b>(y, x)[2]));
            }
        }
    }

    double fastCos(double x){
        //x += 1.57079632;
        if (x >  3.14159265)
            x -= 6.28318531;
        double sq = x * x;
        if (x < 0)
            return 1.0 + 0.5 * sq + 0.0416 * sq * sq;
        else
            return 1.0 - 0.5 * sq + 0.0416 * sq * sq;
    }

    double dist(double ax, double ay,double bx, double by){
        return sqrt((ax - bx)*(ax - bx) + (ay - by)*(ay - by));
    }

    void adjustVignette(Mat &src, Mat &dst, int val){
        cvtColor(src,src,CV_BGRA2BGR);
        dst = Mat::zeros(src.size(), src.type());
        double radius = 1.0 - 0.5*((double)val/100);
        double cx = (double)src.cols/2, cy = (double)src.rows/2;
        double maxDis = radius * dist(0,0,cx,cy);
        double temp;
        int x,y;
        for (y = 0; y < src.rows; y++) {
            for (x = 0; x < src.cols; x++) {
                temp = fastCos(dist(cx, cy, x, y) / maxDis);
                temp *= temp;
                dst.at<Vec3b>(y, x)[0] =
                        saturate_cast<uchar>((src.at<Vec3b>(y, x)[0]) * temp);
                dst.at<Vec3b>(y, x)[1] =
                        saturate_cast<uchar>((src.at<Vec3b>(y, x)[1]) * temp );
                dst.at<Vec3b>(y, x)[2] =
                        saturate_cast<uchar>((src.at<Vec3b>(y, x)[2]) * temp);

            }
        }
    }

    void adjustSharpness(Mat &src, Mat &dst, int val) {
        cvtColor(src,src,CV_BGRA2BGR);
        int i = 2*(val/5)+1;
        dst = Mat::zeros(src.size(), src.type());

        //GaussianBlur( src, dst, Size( i, i ), 0, 0 );

        medianBlur( src, dst, i);
        addWeighted(src, 1.5, dst, -0.5, 0, dst);
    }
}
