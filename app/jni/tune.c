//
// Created by vinay on 31/5/17.
//

#include <bitmap.h>
#include <mem_utils.h>
#include <colour_space.h>
#include <math.h>
#include <android/log.h>
#include <stdlib.h>

#define  LOG_TAG    "tuning.c"
#define  LOGI(...)  __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)
#define  LOGE(...)  __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)

#undef PI
#define PI 3.1415926535897932f

unsigned char truncate(int val){
    if (val > 255) return 255;
    if (val < 0) return 0;
    return (unsigned char)val;
}

void tuneBrightness(Bitmap* bitmap, int val) {
	register unsigned int i;
	unsigned int length = (*bitmap).width * (*bitmap).height;
	unsigned char* red = (*bitmap).red;
	unsigned char* green = (*bitmap).green;
	unsigned char* blue = (*bitmap).blue;
	signed char bright = (signed char)(((float)(val-50)/100)*127);
	for (i = length; i--; ) {
        red[i] =  truncate(red[i]+bright);
        green[i] = truncate(green[i]+bright);
        blue[i] = truncate(blue[i]+bright);
	}
}


void tuneContrast(Bitmap* bitmap, int val) {
	register unsigned int i;
	unsigned int length = (*bitmap).width * (*bitmap).height;
	unsigned char* red = (*bitmap).red;
	unsigned char* green = (*bitmap).green;
	unsigned char* blue = (*bitmap).blue;
	int contrast = (int)(((float)(val-50)/100)*255);

	float factor = (float)(259*(contrast + 255))/(255*(259-contrast));

	for (i = length; i--; ) {
        red[i] = truncate((int)(factor*(red[i]-128))+128);
        green[i] = truncate((int)(factor*(green[i]-128))+128);
        blue[i] = truncate((int)(factor*(blue[i]-128))+128);
	}
}

void tuneHue(Bitmap* bitmap, int val) {
	register unsigned int i;
	unsigned int length = (*bitmap).width * (*bitmap).height;
	unsigned char* red = (*bitmap).red;
	unsigned char* green = (*bitmap).green;
	unsigned char* blue = (*bitmap).blue;
	double H = 3.6*val;
    double h_cos = cos(H*PI/180);
    double h_sin = sin(H*PI/180);
    double r,g,b;
	for (i = length; i--; ) {
        r = (double)red[i]/255;
        g = (double)green[i]/255;
        b = (double)blue[i]/255;

        red[i] = truncate((int)(255*((.299+.701*h_cos+.168*h_sin)*r
                             + (.587-.587*h_cos+.330*h_sin)*g
                             + (.114-.114*h_cos-.497*h_sin)*b)));
        green[i] = truncate((int)(255*((.299-.299*h_cos-.328*h_sin)*r
                                  + (.587+.413*h_cos+.035*h_sin)*g
                                  + (.114-.114*h_cos+.292*h_sin)*b)));
        blue[i] = truncate((int)(255*((.299-.3*h_cos+1.25*h_sin)*r
                                    + (.587-.588*h_cos-1.05*h_sin)*g
                                    + (.114+.886*h_cos-.203*h_sin)*b)));
	}
}


void tuneSaturation(Bitmap* bitmap, int val) {
	register unsigned int i;
	unsigned int length = (*bitmap).width * (*bitmap).height;
	unsigned char* red = (*bitmap).red;
	unsigned char* green = (*bitmap).green;
	unsigned char* blue = (*bitmap).blue;
	double sat = 2*((double)val/100);
	double temp;
	double r_val = 0.299, g_val = 0.587, b_val = 0.114;
    double r,g,b;
	for (i = length; i--; ) {
	    r = (double)red[i]/255;
	    g = (double)green[i]/255;
	    b = (double)blue[i]/255;

        temp = sqrt( r * r * r_val +
                      g * g * g_val +
                        b * b * b_val );
        red[i] = truncate((int)(255*(temp + (r - temp) * sat)));
        green[i] = truncate((int)(255*(temp + (g - temp) * sat)));
        blue[i] = truncate((int)(255*(temp + (b - temp) * sat)));

	}
}

void tuneTemperature(Bitmap* bitmap, int val) {
	register unsigned int i;
	unsigned int length = (*bitmap).width * (*bitmap).height;
	unsigned char* red = (*bitmap).red;
	unsigned char* green = (*bitmap).green;
	unsigned char* blue = (*bitmap).blue;
	int temperature = (int)1.5*(val-50);

	for (i = length; i--; ) {
        red[i] = truncate(red[i] + temperature);
        blue[i] = truncate(blue[i] - temperature);
	}
}

void tuneTint(Bitmap* bitmap, int val) {
	register unsigned int i;
	unsigned int length = (*bitmap).width * (*bitmap).height;
	unsigned char* red = (*bitmap).red;
	unsigned char* green = (*bitmap).green;
	unsigned char* blue = (*bitmap).blue;
	int tint = (int)(1.5*(val-50));

	for (i = length; i--; ) {
        green[i] = truncate(green[i] + tint);
	}
}


void tuneVignette(Bitmap* bitmap, int val) {
	register unsigned int i;
	unsigned int length = (*bitmap).width * (*bitmap).height;
	unsigned char* red = (*bitmap).red;
	unsigned char* green = (*bitmap).green;
	unsigned char* blue = (*bitmap).blue;

	for (i = length; i--; ) {

	}
}


void tuneBlur(Bitmap* bitmap, int val) {
	register unsigned int i;
	unsigned int length = (*bitmap).width * (*bitmap).height;
	unsigned char* red = (*bitmap).red;
	unsigned char* green = (*bitmap).green;
	unsigned char* blue = (*bitmap).blue;

	for (i = length; i--; ) {

	}
}


void tuneSharpen(Bitmap* bitmap, int val) {
	register unsigned int i;
	unsigned int length = (*bitmap).width * (*bitmap).height;
	unsigned char* red = (*bitmap).red;
	unsigned char* green = (*bitmap).green;
	unsigned char* blue = (*bitmap).blue;

	for (i = length; i--; ) {

	}
}