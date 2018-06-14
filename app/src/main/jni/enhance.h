
#include <opencv2/core/core.hpp>
#include <opencv2/imgproc/imgproc.hpp>
#undef PI
#define PI 3.1415926535897932f

extern "C" {
    void adjustBrightness(cv::Mat &inp, cv::Mat &out, int val);
    void adjustContrast(cv::Mat &inp, cv::Mat &out, int val);
    void adjustHue(cv::Mat &inp, cv::Mat &out, int val);
    void adjustSaturation(cv::Mat &inp, cv::Mat &out, int val);
    void adjustTemperature(cv::Mat &inp, cv::Mat &out, int val);
    void adjustTint(cv::Mat &inp, cv::Mat &out, int val);
    void adjustVignette(cv::Mat &inp, cv::Mat &out, int val);
    void adjustSharpness(cv::Mat &inp, cv::Mat &out, int val);
}