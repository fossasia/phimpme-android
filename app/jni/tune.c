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

double dist(int ax, int ay,int bx, int by){
    return sqrt(pow((double) (ax - bx), 2) + pow((double) (ay - by), 2));
}


void tuneVignette(Bitmap* bitmap, int val) {
	register unsigned int i,x,y;
	unsigned int width = (*bitmap).width, height = (*bitmap).height;
	unsigned int length = width * height;
	unsigned char* red = (*bitmap).red;
	unsigned char* green = (*bitmap).green;
	unsigned char* blue = (*bitmap).blue;

    double radius = 1.5-((double)val/100), power = 0.8;
    double cx = (double)width/2, cy = (double)height/2;
    double maxDis = radius * dist(0,0,cx,cy);
    double temp,temp_s;

    for (y = 0; y < height; y++){
        for (x = 0; x < width; x++ ) {
            temp = dist(cx, cy, x, y) / maxDis;
            temp = temp * power;
            temp_s = pow(cos(temp), 4);
            red[x+y*width] = truncate((int)(red[x+y*width]*temp_s));
            green[x+y*width] = truncate((int)(green[x+y*width]*temp_s));
            blue[x+y*width] = truncate((int)(blue[x+y*width]*temp_s));
        }
    }
}


void smoothenBitmap(Bitmap* src, Bitmap* dest, int r){
    register unsigned int x,y;
    register signed int j,k;
    unsigned int width = (*src).width, height = (*src).height;
    unsigned int length =  width*height;
    unsigned char* sred = (*src).red;
    unsigned char* sgreen = (*src).green;
    unsigned char* sblue = (*src).blue;

    unsigned char* dred = (*dest).red;
    unsigned char* dgreen = (*dest).green;
    unsigned char* dblue = (*dest).blue;

    double sr,sg,sb;
    for (y = r; y < height - r; y++){
        for (x = r; x < width - r; x++ ) {
    	    sr = sb = sg = 0.0;
            for (k = -r; k <= r; k++){
                for (j = -r; j <= r; j++){
                    sr += (1/(double)((2*r+1)*(2*r+1))) * sred[x-k+(y-j)*width];
                    sg += (1/(double)((2*r+1)*(2*r+1))) * sgreen[x-k+(y-j)*width];
                    sb += (1/(double)((2*r+1)*(2*r+1))) * sblue[x-k+(y-j)*width];
                }
            }
            dred[x+y*width] = (unsigned char)sr;
            dgreen[x+y*width] = (unsigned char)sg;
            dblue[x+y*width] = (unsigned char)sb;
        }
  	}
}

void tuneBlur(Bitmap* bitmap, int val) {
	register unsigned int i;
	unsigned int length = (*bitmap).width * (*bitmap).height;
	unsigned char* red = (*bitmap).red;
	unsigned char* green = (*bitmap).green;
	unsigned char* blue = (*bitmap).blue;

    static Bitmap tempImg;
    unsigned int width = (*bitmap).width, height = (*bitmap).height;
    initBitmapMemory(&tempImg, width, height);
    int rad = (int)val*0.07;
    smoothenBitmap(bitmap, &tempImg, rad);

    unsigned char* dred = (tempImg).red;
    unsigned char* dgreen = (tempImg).green;
    unsigned char* dblue = (tempImg).blue;

	for (i = length; i--; ) {
        red[i] =  truncate(dred[i]);
        green[i] = truncate(dgreen[i]);
        blue[i] =  truncate(dblue[i]);
	}
	deleteBitmap(&tempImg);
}


void tuneSharpen(Bitmap* bitmap, int val) {
	register unsigned int i;
	unsigned int length = (*bitmap).width * (*bitmap).height;
	unsigned char* red = (*bitmap).red;
	unsigned char* green = (*bitmap).green;
	unsigned char* blue = (*bitmap).blue;
    static Bitmap tempImg;
    unsigned int width = (*bitmap).width, height = (*bitmap).height;
    initBitmapMemory(&tempImg, width, height);
    int rad = (int)val*0.07;
    smoothenBitmap(bitmap, &tempImg, rad);

    unsigned char* dred = (tempImg).red;
    unsigned char* dgreen = (tempImg).green;
    unsigned char* dblue = (tempImg).blue;

	for (i = length; i--; ) {
        red[i] =  truncate(red[i] + (red[i] - dred[i]));
        green[i] = truncate(green[i] + (green[i] - dgreen[i]));
        blue[i] =  truncate(blue[i] + (blue[i] - dblue[i]));
	}
	deleteBitmap(&tempImg);
}
