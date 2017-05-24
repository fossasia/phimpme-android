/*
 * Copyright (C) 2012 Lightbox
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

#include <bitmap.h>
#include <mem_utils.h>
#include <colour_space.h>
#include <math.h>
#include <android/log.h>
#include <stdlib.h>

#define  LOG_TAG    "filter.c"
#define  LOGI(...)  __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)
#define  LOGE(...)  __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)

#undef PI
#define PI 3.1415926535897932f

#define min(x,y)  (x <= y) ? x : y
#define max(x,y)  (x >= y) ? x : y

#define componentCeiling(x) ((x > 255) ? 255 : x)
#define clampComponent(x) ((x > 255) ? 255 : (x < 0) ? 0 : x)

#define blackAndWhite(r, g, b) ((r * 0.3f) + (g * 0.59f) + (b * 0.11f))

#define TILT_SHIFT_BLUR_RADIUS 3
#define TILT_SHIFT_ALPHA_GRADIENT_SIZE 0.07f

// Same as hard light layer mode in GIMP
static unsigned char hardLightLayerPixelComponents(unsigned char maskComponent, unsigned char imageComponent) {
	return (maskComponent > 128) ? 255 - (( (255 - (2 * (maskComponent-128)) ) * (255-imageComponent) )/256) : (2*maskComponent*imageComponent)/256;
}

// Same as overlay layer mode in GIMP
// overlayComponent is one of the rgb components of the overlay colour (0 - 255)
// underlayComponent is one of the rgb components of the underlay colour (0 - 255)
static unsigned char overlayPixelComponents(unsigned int overlayComponent, unsigned char underlayComponent, float alpha) {
	float underlay = underlayComponent * alpha;
	return (unsigned char)((underlay / 255) * (underlay + ((2.0f * overlayComponent) / 255) * (255 - underlay)));
}

// Same as multiply layer mode in GIMP
// Alpha is in the range of 0.0 - 1.0
static unsigned char multiplyPixelComponentsWithAlpha(unsigned char overlayComponent, float alpha, unsigned char underlayComponent) {
	return ((float)((int)underlayComponent * ((int)overlayComponent * alpha))/255);
}

static unsigned char multiplyPixelComponents(unsigned char overlayComponent, unsigned char underlayComponent) {
	return (((int)underlayComponent * (overlayComponent))/255);
}

// Same as grain merge layer mode in GIMP
static unsigned char grainMergePixelsComponent(unsigned char overlayComponent, unsigned char underlayComponent) {
	register int component = ((int)underlayComponent+overlayComponent)-128;
	component = (component > 255) ? 255 : (component < 0) ? 0 : component;

	return component;
}

// Same as screen layer mode in GIMP
static unsigned char screenPixelComponent(unsigned char maskPixelComponent, float alpha, unsigned char imagePixelComponent) {
	return 255.0f - (((255.0f - ((float)maskPixelComponent*alpha)) * (255.0f - imagePixelComponent)) / 255.0f);
}

// Same as subtract layer mode in GIMP
static unsigned char subtractPixelComponent(unsigned char overlayComponent, unsigned char underlayComponent) {
	return max((int)underlayComponent-overlayComponent, 0);
}

// Same as darken layer mode in GIMP
static unsigned char darkenPixelsComponent(unsigned char overlay, unsigned char underlay) {
	return min(underlay, overlay);
}

static unsigned char greyscaleInvertMaskScreenComponent(unsigned char maskGreyPixel, float alpha, unsigned char imagePixelComponent) {
	return screenPixelComponent(maskGreyPixel, (1.0f-((float)maskGreyPixel/255.0f))*alpha, imagePixelComponent);
}

// brightness is between -1.0 to +1.0;
// colourChannel is 0.0 - 1.0 representing either the red, green, or blue channel
static float applyBrightnessToPixelComponent(float colourComponent, float brightness) {
	float scaled = brightness/2;
	if (scaled < 0.0) {
		return colourComponent * ( 1.0f + scaled);
	} else {
		return colourComponent + ((1.0f - colourComponent) * scaled);
	}
}

// contrast is between -1.0 to +1.0;
// pixelComponent is either r, g, or b scaled from 0.0 - 1.0
static float applyContrastToPixelComponent(float pixelComponent, float contrast) {
	return min(1.0f, ((pixelComponent - 0.5f) * (tan ((contrast + 1) * PI/4) ) + 0.5f));
}

void applyBlackAndWhiteFilter(Bitmap* bitmap) {
	register unsigned int i;
	unsigned int length = (*bitmap).width * (*bitmap).height;
	register unsigned char grey;
	unsigned char* red = (*bitmap).red;
	unsigned char* green = (*bitmap).green;
	unsigned char* blue = (*bitmap).blue;
	for (i = length; i--;) {
		grey = blackAndWhite(red[i], green[i], blue[i]);

		red[i] = grey;
		green[i] = grey;
		blue[i] = grey;
	}
}

void applyAnselFilter(Bitmap* bitmap) {
	applyBlackAndWhiteFilter(bitmap);
	register unsigned int i;
	unsigned int length = (*bitmap).width * (*bitmap).height;
	Bitmap localBitmap = *bitmap;
	register unsigned char grey;
	unsigned char* red = (*bitmap).red;
	unsigned char* green = (*bitmap).green;
	unsigned char* blue = (*bitmap).blue;
	for (i = length; i--; ) {
		grey = blackAndWhite(red[i], green[i], blue[i]);
		localBitmap.red[i] = green[i] = blue[i] = hardLightLayerPixelComponents(grey, grey);
	}
}

const unsigned char sepiaRedLut[256] = {24, 24, 25, 26, 27, 28, 29, 30, 30, 30, 31, 32, 33, 34, 35, 36, 37, 37, 38, 38, 39, 40, 41, 42, 43, 43, 44, 45, 46, 47, 47, 48, 49, 50, 50, 51, 52, 53, 54, 55, 56, 57, 57, 58, 58, 59, 60, 61, 62, 63, 64, 64, 65, 66, 67, 68, 69, 70, 71, 71, 72, 72, 73, 74, 75, 76, 77, 78, 78, 79, 80, 81, 82, 83, 84, 85, 85, 86, 87, 88, 89, 89, 90, 91, 92, 93, 93, 94, 95, 96, 97, 97, 98, 99, 100, 101, 102, 102, 103, 104, 105, 106, 107, 108, 109, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 118, 119, 120, 121, 122, 123, 124, 125, 126, 127, 128, 129, 129, 130, 131, 132, 133, 134, 135, 136, 137, 138, 139, 140, 141, 142, 143, 144, 145, 146, 146, 147, 148, 149, 150, 151, 152, 153, 153, 154, 155, 156, 157, 158, 159, 160, 161, 162, 163, 164, 165, 166, 167, 168, 169, 170, 171, 172, 173, 174, 175, 176, 177, 178, 178, 180, 181, 182, 183, 184, 185, 186, 186, 187, 188, 189, 190, 191, 193, 194, 195, 195, 196, 197, 198, 199, 200, 201, 202, 203, 204, 205, 206, 207, 208, 209, 210, 211, 212, 213, 214, 215, 216, 217, 218, 219, 220, 221, 222, 223, 224, 225, 226, 227, 228, 229, 230, 231, 232, 233, 234, 235, 236, 237, 238, 239, 240, 241, 242, 243, 244, 245, 246, 247, 248, 249, 250, 251, 252, 253, 255};
const unsigned char sepiaGreenLut[256] = {16, 16, 16, 17, 18, 18, 19, 20, 20, 20, 21, 22, 22, 23, 24, 24, 25, 25, 26, 26, 27, 28, 28, 29, 30, 30, 31, 31, 32, 33, 33, 34, 35, 36, 36, 36, 37, 38, 39, 39, 40, 41, 42, 43, 43, 44, 45, 46, 47, 47, 48, 48, 49, 50, 51, 51, 52, 53, 54, 54, 55, 55, 56, 57, 58, 59, 60, 61, 61, 61, 62, 63, 64, 65, 66, 67, 67, 68, 68, 69, 70, 72, 73, 74, 75, 75, 76, 77, 78, 78, 79, 80, 81, 81, 82, 83, 84, 85, 86, 87, 88, 90, 90, 91, 92, 93, 94, 95, 96, 97, 97, 98, 99, 100, 101, 103, 104, 105, 106, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 122, 123, 123, 124, 125, 127, 128, 129, 130, 131, 132, 132, 134, 135, 136, 137, 138, 139, 141, 141, 142, 144, 145, 146, 147, 148, 149, 150, 151, 152, 154, 155, 156, 157, 158, 160, 160, 161, 162, 163, 165, 166, 167, 168, 169, 170, 171, 173, 174, 175, 176, 177, 178, 179, 180, 182, 183, 184, 185, 187, 188, 189, 189, 191, 192, 193, 194, 196, 197, 198, 198, 200, 201, 202, 203, 205, 206, 207, 208, 209, 210, 211, 212, 213, 215, 216, 217, 218, 219, 220, 221, 223, 224, 225, 226, 227, 228, 229, 230, 231, 232, 233, 235, 236, 237, 238, 239, 240, 241, 242, 243, 244, 245, 246, 247, 248, 249, 250, 251, 252, 253, 255};
const unsigned char sepiaBlueLut[256] = {5, 5, 5, 5, 5, 6, 6, 7, 7, 8, 8, 9, 9, 9, 10, 10, 11, 11, 11, 11, 12, 12, 13, 13, 14, 14, 14, 14, 15, 15, 16, 16, 17, 17, 17, 18, 18, 19, 20, 20, 21, 21, 21, 22, 22, 23, 23, 24, 25, 25, 26, 27, 28, 28, 29, 29, 30, 31, 31, 31, 32, 33, 33, 34, 35, 36, 37, 38, 38, 39, 39, 40, 41, 42, 43, 43, 44, 45, 46, 47, 47, 48, 49, 50, 51, 52, 53, 53, 54, 55, 56, 57, 58, 59, 60, 60, 61, 62, 63, 65, 66, 67, 67, 68, 69, 70, 72, 73, 74, 75, 75, 76, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 90, 91, 92, 93, 93, 95, 97, 98, 99, 100, 101, 102, 104, 104, 106, 107, 108, 109, 111, 112, 114, 115, 115, 117, 118, 120, 121, 122, 123, 124, 125, 127, 128, 129, 131, 132, 133, 135, 136, 137, 138, 139, 141, 142, 144, 145, 147, 147, 149, 150, 151, 153, 154, 156, 157, 159, 159, 161, 162, 164, 165, 167, 168, 169, 170, 172, 173, 174, 176, 177, 178, 180, 181, 182, 184, 185, 186, 188, 189, 191, 192, 193, 194, 196, 197, 198, 200, 201, 203, 204, 205, 206, 207, 209, 210, 211, 213, 214, 215, 216, 218, 219, 220, 221, 223, 224, 225, 226, 227, 229, 230, 231, 232, 234, 235, 236, 237, 238, 239, 241, 242, 243, 244, 245, 246, 247, 248, 249, 250, 251, 252, 253, 255};
void applySepia(Bitmap* bitmap) {
	register unsigned int i;
	unsigned int length = (*bitmap).width * (*bitmap).height;
	unsigned char* red = (*bitmap).red;
	unsigned char* green = (*bitmap).green;
	unsigned char* blue = (*bitmap).blue;
	for (i = length; i--; ) {
		register float r = (float) red[i] / 255;
		register float g = (float) green[i] / 255;
		register float b = (float) blue[i] / 255;

		//create grey scale luminosity
		register float luminosity =  (0.21f * r + 0.72f * g + 0.07 * b) * 255;

		red[i] = sepiaRedLut[(int)luminosity];
		green[i] = sepiaGreenLut[(int)luminosity];
		blue[i] = sepiaBlueLut[(int)luminosity];

		/*
		//increase brightness of the luminosity
		register unsigned char brightGrey = applyBrightnessToPixelComponent(luminosity, 0.234375f) * 255;

		//overlay a brown on the grey scale luminosity (sepia brown = rgb(107, 66, 12));
		register unsigned char tintedR = overlayPixelComponents(107, brightGrey, 1);
		register unsigned char tintedG = overlayPixelComponents(66, brightGrey, 1);
		register unsigned char tintedB = overlayPixelComponents(12, brightGrey, 1);

		register float invertMask = 1.0f - luminosity;
		luminosity *= 255;
		float r2 = (luminosity * (1.0f - invertMask)
				+ ((float) tintedR * invertMask));
		float g2 = (luminosity * (1.0f - invertMask)
				+ ((float) tintedG * invertMask));
		float b2 = (luminosity * (1.0f - invertMask)
				+ ((float) tintedB * invertMask));

		float r3 = (r2 * (1.0f - invertMask)
				+ ((float) tintedR * invertMask));
		float g3 = (g2 * (1.0f - invertMask)
				+ ((float) tintedG * invertMask));
		float b3 = (b2 * (1.0f - invertMask)
				+ ((float) tintedB * invertMask));

		red[i] = r3;
		green[i] = g3;
		blue[i] = b3;*/


	}
}

void applyGeorgia(Bitmap* bitmap) {
	register unsigned int i;
	unsigned int length = (*bitmap).width * (*bitmap).height;
	unsigned char r, g, b;
	unsigned char* red = (*bitmap).red;
	unsigned char* green = (*bitmap).green;
	unsigned char* blue = (*bitmap).blue;

	unsigned char brightnessLut[256];
	unsigned char contrastLut[256];
	unsigned char multiplyLut250[256];
	unsigned char multiplyLut220[256];
	unsigned char multiplyLut175[256];
	for (i = 0; i < 256; i++) {
		float pixelf = i/255.0f;
		brightnessLut[i] = 255*applyBrightnessToPixelComponent(pixelf, 0.4724f);
		contrastLut[i] = 255*applyContrastToPixelComponent(pixelf, 0.3149f);
		multiplyLut250[i] = multiplyPixelComponents(250, i);
		multiplyLut220[i] = multiplyPixelComponents(220, i);
		multiplyLut175[i] = multiplyPixelComponents(175, i);
	}

	for (i = length; i--; ) {
		//r = (float)(red[i])/255;
		//g = (float)(green[i])/255;
		//b = (float)(blue[i])/255;

		r = brightnessLut[red[i]]; //applyBrightnessToPixelComponent(r, 0.4724f);
		g = brightnessLut[green[i]]; //applyBrightnessToPixelComponent(g, 0.4724f);
		b = brightnessLut[blue[i]]; //applyBrightnessToPixelComponent(b, 0.4724f);

		r = contrastLut[r]; //applyContrastToPixelComponent(r, 0.3149f);
		g = contrastLut[g]; //applyContrastToPixelComponent(g, 0.3149f);
		b = contrastLut[b]; //applyContrastToPixelComponent(b, 0.3149f);

		//r *= 255;
		g = (g * 0.87f/*222*/) + 33; //compress the green channel between 33 - 255
		b = (b * 0.439f/*112*/) + 143; //compress the blue channel between 143 - 255

		// multiply by a wheat colour rgb(250, 220, 175)
		red[i] = multiplyLut250[r]; //multiplyPixelComponents(250, r);
		green[i] = multiplyLut220[g]; //multiplyPixelComponents(220, g);
		blue[i] = multiplyLut175[b]; //multiplyPixelComponents(175, b);
	}
}

void gammaCorrection(Bitmap* bitmap) {
	unsigned char* red = (*bitmap).red;
	unsigned char* green = (*bitmap).green;
	unsigned char* blue = (*bitmap).blue;
	unsigned int length = (*bitmap).width * (*bitmap).height;

	unsigned int i;
	float redAverage = 0;
	float greenAverage = 0;
	float blueAverage = 0;
	unsigned int n = 1;
	for (i = 0; i < length; i++) {
		redAverage = ((n-1)*redAverage + red[i])/n;
		greenAverage = ((n-1)*greenAverage + green[i])/n;
		blueAverage = ((n-1)*blueAverage + blue[i])/n;
		n++;
	}

	float gammaRed = log(128.0f/255)/log(redAverage/255);
	float gammaGreen = log(128.0f/255)/log(greenAverage/255);
	float gammaBlue = log(128.0f/255)/log(blueAverage/255);
	int redLut[256];
	int greenLut[256];
	int blueLut[256];
	for (i = 0; i < 256; i++) {
		redLut[i] = -1;
		greenLut[i] = -1;
		blueLut[i] = -1;
	}
	for (i = 0; i < length; i++) {
		if (redLut[red[i]] == -1) {
			redLut[red[i]] = clampComponent(255.0f * powf((red[i]/255.0f), gammaRed));
		}
		red[i] = redLut[red[i]];

		if (greenLut[green[i]] == -1) {
			greenLut[green[i]] = clampComponent(255.0f * powf((green[i]/255.0f), gammaGreen));
		}
		green[i] = greenLut[green[i]];

		if (blueLut[blue[i]] == -1) {
			blueLut[blue[i]] = clampComponent(255.0f * powf((blue[i]/255.0f), gammaBlue));
		}
		blue[i] = blueLut[blue[i]];
	}
}

// amount is 0.0 to 1.0
// threshold is 0 to 255
int unsharpMask(Bitmap* bitmap, int radius, float amount, int threshold) {
	unsigned char* red = (*bitmap).red;
	unsigned char* green = (*bitmap).green;
	unsigned char* blue = (*bitmap).blue;
	unsigned int length = (*bitmap).width * (*bitmap).height;

	// Create blur
	unsigned char* blurRed;
	unsigned char* blurGreen;
	unsigned char* blurBlue;
	int resultCode = newUnsignedCharArray(length, &blurRed);
	if (resultCode != MEMORY_OK) {
		return resultCode;
	}
	resultCode = newUnsignedCharArray(length, &blurGreen);
	if (resultCode != MEMORY_OK) {
		freeUnsignedCharArray(&blurRed);
		return resultCode;
	}
	resultCode = newUnsignedCharArray(length, &blurBlue);
	if (resultCode != MEMORY_OK) {
		freeUnsignedCharArray(&blurRed);
		freeUnsignedCharArray(&blurGreen);
		return resultCode;
	}

	float blurRadius = radius/3.0f;
	resultCode = stackBlur(&blurRadius, (*bitmap).red, (*bitmap).green, (*bitmap).blue, &((*bitmap).width), &((*bitmap).height), blurRed, blurGreen, blurBlue);
	if (resultCode != MEMORY_OK) {
		freeUnsignedCharArray(&blurRed);
		freeUnsignedCharArray(&blurGreen);
		freeUnsignedCharArray(&blurBlue);
		return resultCode;
	}

	int i, j;
	short int lut[256][256];
	float a = (4 * amount) + 1;
	for (i = 0; i < 256; i++) {
		for (j = 0; j < 256; j++) {
			lut[i][j] = -1;//clampComponent((int) (a * (i - j) + j));
		}
	}
	for (i = length; i--;) {
		int r1 = red[i];
		int g1 = green[i];
		int b1 = blue[i];

		int r2 = blurRed[i];
		int g2 = blurGreen[i];
		int b2 = blurBlue[i];

		if (fabs(r1 - r2) >= threshold) {
			if (lut[r1][r2] == -1) {
				lut[r1][r2] = clampComponent((int) ((a + 1) * (r1 - r2) + r2));
			}
			r1 = lut[r1][r2]; //clampComponent((int) ((a + 1) * (r1 - r2) + r2));
		}
		if (fabs(g1 - g2) >= threshold) {
			if (lut[g1][g2] == -1) {
				lut[g1][g2] = clampComponent((int) ((a + 1) * (g1 - g2) + g2));
			}
			g1 = lut[g1][g2]; //clampComponent((int) ((a + 1) * (g1 - g2) + g2));
		}
		if (fabs(b1 - b2) >= threshold) {
			if (lut[b1][b2] == -1) {
				lut[b1][b2] = clampComponent((int) ((a + 1) * (b1 - b2) + b2));
			}
			b1 = lut[b1][b2]; //clampComponent((int) ((a + 1) * (b1 - b2) + b2));
		}

		red[i] = r1;
		green[i] = g1;
		blue[i] = b1;
	}

	freeUnsignedCharArray(&blurRed);
	freeUnsignedCharArray(&blurGreen);
	freeUnsignedCharArray(&blurBlue);
}

// Normalise the colours
void normaliseColours(Bitmap* bitmap) {
	unsigned char* red = (*bitmap).red;
	unsigned char* green = (*bitmap).green;
	unsigned char* blue = (*bitmap).blue;

	unsigned int histogram[3][256];

	unsigned int channel, i;
	for (channel = 3; channel--;) {
		for (i = 256; i--;) {
			histogram[channel][i] = 0;
		}
	}

	unsigned int width = (*bitmap).width;
	unsigned int height = (*bitmap).height;

	register unsigned int n = 0;
	unsigned int x, y;
	for (y = height; y--;) {
		for (x = width; x--;) {
			histogram[0][red[n]]++;
			histogram[1][green[n]]++;
			histogram[2][blue[n]]++;
			n++;
		}
	}

	float count = width * height;
	float percentage;
	float nextPercentage;
	unsigned int low = 0;
	unsigned int high = 255;
	double mult;

	for (channel = 3; channel--;) {
		nextPercentage = (float) histogram[channel][0] / count;
		for (i = 0; i <= 255; i++) {
			percentage = nextPercentage;
			nextPercentage += (float) histogram[channel][i + 1] / count;
			if (fabs(percentage - 0.006) < fabs(nextPercentage - 0.006)) {
				low = i;
				break;
			}
		}

		nextPercentage = (float) histogram[channel][255] / count;
		for (i = 255; i >= 0; i--) {
			percentage = nextPercentage;
			nextPercentage += histogram[channel][i - 1] / count;
			if (fabs(percentage - 0.006) < fabs(nextPercentage - 0.006)) {
				high = i;
				break;
			}
		}

		mult = (float) 255.0 / (high - low);
		for (i = low; i--;) {
			histogram[channel][i] = 0;
		}
		for (i = 255; i > high; i--) {
			histogram[channel][i] = 255;
		}

		float base = 0;
		for (i = low; i <= high; i++) {
			histogram[channel][i] = (int) base;
			base += mult;
		}
	}

	n = 0;
	for (y = height; y--;) {
		for (x = width; x--;) {
			red[n] = histogram[0][red[n]];
			green[n] = histogram[1][green[n]];
			blue[n] = histogram[2][blue[n]];
			n++;
		}
	}
}

void applyInstafix(Bitmap* bitmap) {
	//unsharpMask(bitmap, 3, 0.25f, 2);
	gammaCorrection(bitmap);
	normaliseColours(bitmap);
}

int applySahara(Bitmap* bitmap) {
	int length = (*bitmap).width * (*bitmap).height;
	int i;
	unsigned char r, g, b;

	//HSBColour hsb;
	unsigned char* red = (*bitmap).red;
	unsigned char* green = (*bitmap).green;
	unsigned char* blue = (*bitmap).blue;
	unsigned char brightnessLut[256];
	unsigned char contrastLut[256];
	for (i = 0; i < 256; i++) {
		float pixelf = i/255.0f;
		//brightnessLut[i] = 255*applyBrightnessToPixelComponent(pixelf, 0.35433f);
		//contrastLut[i] = 255*applyContrastToPixelComponent(pixelf, 0.1496f);
		brightnessLut[i] = 255*applyBrightnessToPixelComponent(pixelf, 0.45f);
		contrastLut[i] = 255*applyContrastToPixelComponent(pixelf, 0.1f);
	}
	for (i = length; i--; ) {
		r = brightnessLut[red[i]];
		g = brightnessLut[green[i]];
		b = brightnessLut[blue[i]];

		r = contrastLut[r];
		green[i] = contrastLut[g];
		b = contrastLut[b];

		red[i] = (r*0.8431f/*215*/)+40; //compress the red channel between 18 - 237
		blue[i] = (b*0.8823f/*225*/)+30; //compress the blue channel between 50 - 205

		//rgbToHsb(red[i], green[i], blue[i], &hsb);
		//hsb.s = hsb.s * 0.55f;

		//hsbToRgb(&hsb, &red[i], &green[i], &blue[i]);
	}

	float matrix[4][4];
	identMatrix(matrix);
	float saturation = 0.65f;
	saturateMatrix(matrix, &saturation);
	applyMatrix(bitmap, matrix);

	unsigned char* blurRed;
	unsigned char* blurGreen;
	unsigned char* blurBlue;
	int resultCode = newUnsignedCharArray(length, &blurRed);
	if (resultCode != MEMORY_OK) {
		return resultCode;
	}
	resultCode = newUnsignedCharArray(length, &blurGreen);
	if (resultCode != MEMORY_OK) {
		freeUnsignedCharArray(&blurRed);
		return resultCode;
	}
	resultCode = newUnsignedCharArray(length, &blurBlue);
	if (resultCode != MEMORY_OK) {
		freeUnsignedCharArray(&blurRed);
		freeUnsignedCharArray(&blurGreen);
		return resultCode;
	}

	float blurRadius = 1.0f;
	resultCode = stackBlur(&blurRadius, (*bitmap).red, (*bitmap).green, (*bitmap).blue, &((*bitmap).width), &((*bitmap).height), blurRed, blurGreen, blurBlue);
	if (resultCode != MEMORY_OK) {
		freeUnsignedCharArray(&blurRed);
		freeUnsignedCharArray(&blurGreen);
		freeUnsignedCharArray(&blurBlue);
		return resultCode;
	}

	short int overlayLut[256][256];
	unsigned char multiplyLut255[256];
	unsigned char multiplyLut227[256];
	unsigned char multiplyLut187[256];
	unsigned int j;
	for (i = 0; i < 256; i++) {
		for (j = 0; j < 256; j++) {
			overlayLut[i][j] = -1;//overlayPixelComponents(i, j, 1.0f);
		}
		multiplyLut255[i] = multiplyPixelComponents(255, i);
		multiplyLut227[i] = multiplyPixelComponents(227, i);
		multiplyLut187[i] = multiplyPixelComponents(187, i);
	}
	for (i = length; i--; ) {
		if (overlayLut[blurRed[i]][red[i]] == -1) {
			overlayLut[blurRed[i]][red[i]] = overlayPixelComponents(blurRed[i], red[i], 1.0f);
		}
		red[i] = overlayLut[blurRed[i]][red[i]];//overlayPixelComponents(blurRed[i], red[i], 1.0f);

		if (overlayLut[blurGreen[i]][green[i]] == -1) {
			overlayLut[blurGreen[i]][green[i]] = overlayPixelComponents(blurGreen[i], green[i], 1.0f);
		}
		green[i] = overlayLut[blurGreen[i]][green[i]];//overlayPixelComponents(blurGreen[i], green[i], 1.0f);

		if (overlayLut[blurBlue[i]][blue[i]] == -1) {
			overlayLut[blurBlue[i]][blue[i]] = overlayPixelComponents(blurBlue[i], blue[i], 1.0f);
		}
		blue[i] = overlayLut[blurBlue[i]][blue[i]];//overlayPixelComponents(blurBlue[i], blue[i], 1.0f);

		// Multiply by a wheat colour rgb(255, 227, 187)
		red[i] = multiplyLut255[red[i]];//multiplyPixelComponents(255, red[i]);
		green[i] = multiplyLut227[green[i]];//multiplyPixelComponents(227, green[i]);
		blue[i] = multiplyLut187[blue[i]];//multiplyPixelComponents(187, blue[i]);
	}

	freeUnsignedCharArray(&blurRed);
	freeUnsignedCharArray(&blurGreen);
	freeUnsignedCharArray(&blurBlue);

	return MEMORY_OK;
}

//TODO memory usage may be reduced by using component based blur
int applyHDR(Bitmap* bitmap) {
	//Cache to local variables
	unsigned char* red = (*bitmap).red;
	unsigned char* green = (*bitmap).green;
	unsigned char* blue = (*bitmap).blue;

	unsigned char* blurRed;
	unsigned char* blurGreen;
	unsigned char* blurBlue;
	int length = (*bitmap).width * (*bitmap).height;
	int resultCode = newUnsignedCharArray(length, &blurRed);
	if (resultCode != MEMORY_OK) {
		return resultCode;
	}
	resultCode = newUnsignedCharArray(length, &blurGreen);
	if (resultCode != MEMORY_OK) {
		freeUnsignedCharArray(&blurRed);
		return resultCode;
	}
	resultCode = newUnsignedCharArray(length, &blurBlue);
	if (resultCode != MEMORY_OK) {
		freeUnsignedCharArray(&blurRed);
		freeUnsignedCharArray(&blurGreen);
		return resultCode;
	}
	float blurRadius = 9.0f;
	resultCode = stackBlur(&blurRadius, red, green, blue, &((*bitmap).width), &((*bitmap).height), blurRed, blurGreen, blurBlue);
	if (resultCode != MEMORY_OK) {
		freeUnsignedCharArray(&blurRed);
		freeUnsignedCharArray(&blurGreen);
		freeUnsignedCharArray(&blurBlue);
		return resultCode;
	}

	unsigned int i, j;
	unsigned char r1, g1, b1, r2, g2, b2;
	float matrix[4][4];
	identMatrix(matrix);
	float saturation = 1.3f;
	saturateMatrix(matrix, &saturation);
	for (i = length; i--;) {
		// invert the blurred pixel
		r1 = 255 - blurRed[i];
		g1 = 255 - blurGreen[i];
		b1 = 255 - blurBlue[i];

		// Grain merge the inverted blurred pixel with the original
		r1 = grainMergePixelsComponent(r1, red[i]);
		g1 = grainMergePixelsComponent(g1, green[i]);
		b1 = grainMergePixelsComponent(b1, blue[i]);

		// boost the saturation of the original pixel
		//HSBColour hsb;
		//rgbToHsb(red[i], green[i], blue[i], &hsb);
		//hsb.s = min(1.0f, hsb.s * 1.3f);
		r2 = red[i];
		g2 = green[i];
		b2 = blue[i];
		applyMatrixToPixel(&r2, &g2, &b2, matrix);
		//hsbToRgb(&hsb, &r2, &g2, &b2);

		// grain merge the saturated pixel with the inverted grain merged pixel
		red[i] = grainMergePixelsComponent(r2, r1);
		green[i] = grainMergePixelsComponent(g2, g1);
		blue[i] = grainMergePixelsComponent(b2, g1);
	}

	applyMatrix(bitmap, matrix);

	freeUnsignedCharArray(&blurRed);
	freeUnsignedCharArray(&blurGreen);
	freeUnsignedCharArray(&blurBlue);

	return MEMORY_OK;
}

void applyTestino(Bitmap* bitmap) {
	//Cache to local variables
	unsigned char* red = (*bitmap).red;
	unsigned char* green = (*bitmap).green;
	unsigned char* blue = (*bitmap).blue;

	unsigned int length = (*bitmap).width * (*bitmap).height;
	register unsigned int i;
	unsigned char r, g, b;
	//HSBColour hsb;
	register unsigned char grey;
	short int greyscaleInvertMaskScreenComponentLut[256][256];
	short int overlayLut[256][256];
	unsigned int j;
	for (i = 256; i--;) {
		for (j = 256; j--;) {
			greyscaleInvertMaskScreenComponentLut[i][j] = -1;
			overlayLut[i][j] = -1;
		}
	}

	float matrix[4][4];
	identMatrix(matrix);
	float saturation = 1.5f;
	saturateMatrix(matrix, &saturation);
	applyMatrix(bitmap, matrix);

	for (i = length; i--;) {
		//rgbToHsb(red[i], green[i], blue[i], &hsb);
		//hsb.s = min(hsb.s * 1.5f, 1.0f);
		//hsbToRgb(&hsb, &r, &g, &b);

		r = red[i];
		g = green[i];
		b = blue[i];

		grey = ((unsigned int)red[i] + (unsigned int)green[i] + (unsigned int)blue[i])/3;
		r = (greyscaleInvertMaskScreenComponentLut[grey][r] == -1) ? greyscaleInvertMaskScreenComponentLut[grey][r] = greyscaleInvertMaskScreenComponent(grey, 0.5f, r) : greyscaleInvertMaskScreenComponentLut[grey][r];
		g = (greyscaleInvertMaskScreenComponentLut[grey][g] == -1) ? greyscaleInvertMaskScreenComponentLut[grey][g] = greyscaleInvertMaskScreenComponent(grey, 0.5f, g) : greyscaleInvertMaskScreenComponentLut[grey][g];
		b = (greyscaleInvertMaskScreenComponentLut[grey][b] == -1) ? greyscaleInvertMaskScreenComponentLut[grey][b] = greyscaleInvertMaskScreenComponent(grey, 0.5f, b) : greyscaleInvertMaskScreenComponentLut[grey][b];

		// Create black and white pixel
		grey = blackAndWhite(red[i], green[i], blue[i]);

		r = (overlayLut[grey][r] == -1) ? overlayLut[grey][r] = overlayPixelComponents(grey, r, 1.0f) : overlayLut[grey][r];
		g = (overlayLut[grey][g] == -1) ? overlayLut[grey][g] = overlayPixelComponents(grey, g, 1.0f) : overlayLut[grey][g];
		b = (overlayLut[grey][b] == -1) ? overlayLut[grey][b] = overlayPixelComponents(grey, b, 1.0f) : overlayLut[grey][b];
		red[i] = (overlayLut[grey][r] == -1) ? overlayLut[grey][r] = overlayPixelComponents(grey, r, 1.0f) : overlayLut[grey][r];
		green[i] = (overlayLut[grey][g] == -1) ? overlayLut[grey][g] = overlayPixelComponents(grey, g, 1.0f) : overlayLut[grey][g];
		blue[i] = (overlayLut[grey][b] == -1) ? overlayLut[grey][b] = overlayPixelComponents(grey, b, 1.0f) : overlayLut[grey][b];
	}
}

const unsigned char xproRedCurveLut[256] = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1,
		1, 2, 2, 2, 2, 2, 3, 3, 3, 3, 4, 4, 4, 4, 5, 5, 5, 6, 6, 6, 6, 7, 7, 7,
		8, 8, 8, 9, 9, 9, 10, 10, 11, 11, 11, 12, 12, 13, 13, 14, 14, 14, 15,
		15, 16, 16, 17, 18, 18, 19, 19, 20, 20, 21, 22, 22, 23, 24, 24, 25, 26,
		27, 27, 28, 29, 30, 30, 31, 32, 33, 34, 35, 36, 37, 37, 38, 39, 40, 41,
		42, 44, 44, 45, 46, 47, 49, 50, 52, 53, 54, 56, 57, 58, 60, 61, 63, 64,
		66, 68, 69, 71, 73, 75, 76, 78, 80, 81, 83, 85, 87, 89, 91, 93, 95, 97,
		98, 101, 103, 105, 107, 109, 111, 113, 115, 117, 119, 121, 123, 125,
		127, 129, 131, 133, 135, 137, 139, 141, 143, 145, 147, 149, 151, 154,
		156, 157, 159, 161, 163, 165, 167, 169, 171, 173, 175, 177, 178, 180,
		182, 184, 185, 187, 188, 191, 192, 193, 195, 197, 198, 200, 202, 203,
		205, 206, 208, 209, 211, 212, 214, 215, 217, 219, 220, 221, 223, 224,
		225, 227, 228, 230, 231, 232, 234, 235, 236, 237, 239, 240, 241, 242,
		243, 244, 245, 246, 247, 248, 249, 250, 251, 252, 253, 254, 255, 255,
		255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255,
		255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255,
		255, 255, 255 };
const unsigned char xproGreenCurveLut[256] = { 0, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9,
		10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27,
		28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45,
		47, 48, 49, 50, 51, 52, 53, 54, 55, 56, 58, 59, 60, 61, 62, 63, 64, 65,
		67, 69, 70, 71, 72, 73, 75, 76, 77, 78, 80, 81, 82, 83, 85, 86, 87, 88,
		90, 91, 92, 94, 95, 96, 97, 99, 100, 101, 103, 104, 105, 107, 108, 109,
		111, 112, 113, 115, 116, 117, 119, 120, 121, 123, 124, 125, 127, 129,
		130, 132, 133, 134, 136, 137, 138, 140, 141, 142, 144, 145, 146, 147,
		149, 149, 150, 152, 153, 154, 155, 157, 158, 159, 160, 162, 163, 164,
		166, 167, 168, 170, 171, 172, 173, 174, 176, 177, 178, 179, 181, 182,
		183, 184, 185, 187, 188, 189, 190, 191, 192, 193, 195, 196, 197, 198,
		199, 200, 201, 202, 204, 205, 206, 207, 208, 209, 210, 211, 212, 213,
		213, 215, 216, 217, 217, 218, 219, 220, 221, 222, 222, 223, 224, 225,
		226, 226, 227, 228, 229, 229, 230, 231, 231, 232, 233, 233, 234, 235,
		235, 236, 236, 237, 238, 238, 239, 239, 240, 240, 241, 241, 242, 242,
		243, 243, 244, 244, 245, 245, 245, 246, 246, 247, 247, 248, 248, 248,
		249, 249, 250, 250, 250, 251, 251, 251, 252, 252, 253, 253, 254, 254,
		255, 255, 255 };
const unsigned char xproBlueCurveLut[256] = { 21, 21, 21, 22, 23, 24, 25, 26,
		27, 28, 29, 29, 30, 31, 32, 33, 34, 34, 35, 36, 37, 38, 39, 39, 40, 41,
		42, 43, 44, 44, 45, 46, 47, 48, 49, 49, 50, 51, 52, 53, 54, 54, 55, 56,
		57, 58, 59, 59, 60, 61, 62, 63, 64, 64, 65, 66, 67, 68, 69, 69, 70, 71,
		72, 73, 74, 74, 75, 76, 77, 78, 79, 80, 80, 81, 82, 83, 84, 84, 85, 86,
		87, 88, 89, 89, 91, 91, 92, 93, 94, 95, 95, 96, 97, 98, 99, 100, 101,
		101, 102, 103, 104, 105, 106, 106, 107, 108, 109, 110, 111, 111, 112,
		113, 114, 115, 115, 116, 117, 118, 119, 120, 121, 121, 122, 123, 124,
		125, 126, 126, 127, 128, 129, 130, 131, 132, 132, 133, 134, 135, 136,
		137, 137, 138, 139, 140, 141, 142, 142, 143, 144, 145, 146, 147, 147,
		148, 149, 150, 151, 152, 152, 153, 154, 155, 156, 157, 157, 158, 159,
		160, 160, 162, 162, 163, 164, 165, 166, 167, 167, 168, 169, 170, 171,
		172, 172, 173, 174, 175, 176, 176, 177, 178, 179, 180, 181, 181, 182,
		183, 184, 185, 186, 186, 187, 188, 189, 190, 191, 191, 192, 193, 194,
		195, 196, 197, 198, 198, 199, 200, 201, 202, 203, 203, 204, 205, 206,
		207, 208, 208, 209, 210, 211, 212, 213, 213, 214, 215, 216, 217, 218,
		218, 219, 220, 221, 222, 223, 223, 224, 225, 226, 227, 228, 228, 229,
		230, 231, 232, 232, 233 };
void applyXPro(Bitmap* bitmap) {
	//Cache to local variables
	unsigned char* red = (*bitmap).red;
	unsigned char* green = (*bitmap).green;
	unsigned char* blue = (*bitmap).blue;

	unsigned int length = (*bitmap).width * (*bitmap).height;
	register unsigned int i;
	short int overlayLut[256][256];
	int j;
	for (i = 256; i--;) {
		for (j = 256; j--;) {
			overlayLut[i][j] = -1;
		}
	}
	for (i = length; i--;) {
		HSBColour hsb;
		//rgbToHsb(red[i], green[i], blue[i], &hsb);
		//float value = hsb.b;
		float value;
		getBrightness(red[i], green[i], blue[i], &value);

		unsigned char r = xproRedCurveLut[red[i]];
		unsigned char g = xproGreenCurveLut[green[i]];
		unsigned char b = xproBlueCurveLut[blue[i]];
		rgbToHsb(r, g, b, &hsb);
		hsb.b = value;

		hsbToRgb(&hsb, &r, &g, &b);

		if (overlayLut[red[i]][r] == -1) {
			overlayLut[red[i]][r] = overlayPixelComponents(red[i], r, 1.0f);
		}
		red[i] = overlayLut[red[i]][r]; //overlayPixelComponents(red[i], r, 1.0f);
		if (overlayLut[green[i]][g] == -1) {
			overlayLut[green[i]][g] = overlayPixelComponents(green[i], g, 1.0f);
		}
		green[i] = overlayLut[green[i]][g]; //overlayPixelComponents(green[i], g, 1.0f);
		if (overlayLut[blue[i]][b] == -1) {
			overlayLut[blue[i]][b] = overlayPixelComponents(blue[i], b, 1.0f);
		}
		blue[i] = overlayLut[blue[i]][b]; //overlayPixelComponents(blue[i], b, 1.0f);
	}
}

void applyCyano(Bitmap* bitmap) {
	//Cache to local variables
	unsigned char* red = (*bitmap).red;
	unsigned char* green = (*bitmap).green;
	unsigned char* blue = (*bitmap).blue;

	unsigned int length = (*bitmap).width * (*bitmap).height;
	register unsigned int i;
	register unsigned char grey, r, g, b;
	for (i = length; i--;) {
		grey = ((red[i] * 0.222f) + (green[i] * 0.222f) + (blue[i] * 0.222f));
		r = componentCeiling(61.0f + grey);
		g = componentCeiling(87.0f + grey);
		b = componentCeiling(136.0f + grey);

		grey = blackAndWhite(red[i], green[i], blue[i]);
		red[i] = overlayPixelComponents(grey, r, 0.9f);
		green[i] = overlayPixelComponents(grey, g, 0.9f);
		blue[i] = overlayPixelComponents(grey, b, 0.9f);
	}
}

void applyRetro(Bitmap* bitmap) {
	//Cache to local variables
	unsigned char* red = (*bitmap).red;
	unsigned char* green = (*bitmap).green;
	unsigned char* blue = (*bitmap).blue;

	unsigned int length = (*bitmap).width * (*bitmap).height;
	register unsigned int i;
	register unsigned int grey;
	register unsigned char r, g, b;

	short int overlayLut[256][256];
	unsigned int j;
	for (i = 256; i--; ) {
		for (j = 256; j--; ) {
			overlayLut[i][j] = -1;
		}
	}
	unsigned char multiply251Lut[256];
	unsigned char multiply242Lut[256];
	unsigned char multiply163Lut[256];
	unsigned char screen232Lut[256];
	unsigned char screen101Lut[256];
	unsigned char screen179Lut[256];
	unsigned char screen9Lut[256];
	unsigned char screen73Lut[256];
	unsigned char screen233Lut[256];
	for (i = 256; i--; ) {
		multiply251Lut[i] = multiplyPixelComponentsWithAlpha(251, 0.588235f, i);
		multiply242Lut[i] = multiplyPixelComponentsWithAlpha(242, 0.588235f, i);
		multiply163Lut[i] = multiplyPixelComponentsWithAlpha(163, 0.588235f, i);

		screen232Lut[i] = screenPixelComponent(232, 0.2f, i);
		screen101Lut[i] = screenPixelComponent(101, 0.2f, i);
		screen179Lut[i] = screenPixelComponent(179, 0.2f, i);

		screen9Lut[i] = screenPixelComponent(9, 0.168627f, i);
		screen73Lut[i] = screenPixelComponent(73, 0.168627f, i);
		screen233Lut[i] = screenPixelComponent(233, 0.168627f, i);
	}
	for (i = length; i--;) {
		// Overlay grey
		grey = blackAndWhite(red[i], green[i], blue[i]);
		r = overlayLut[grey][red[i]] == -1 ? overlayLut[grey][red[i]] = overlayPixelComponents(grey, red[i], 1.0f) : overlayLut[grey][red[i]];
		g = overlayLut[grey][green[i]] == -1 ? overlayLut[grey][green[i]] = overlayPixelComponents(grey, green[i], 1.0f) : overlayLut[grey][green[i]];
		b = overlayLut[grey][blue[i]] == -1 ? overlayLut[grey][blue[i]] = overlayPixelComponents(grey, blue[i], 1.0f) : overlayLut[grey][blue[i]];
		//r = overlayPixelComponents(grey, (*bitmap).red[i], 1.0f);
		//g = overlayPixelComponents(grey, (*bitmap).green[i], 1.0f);
		//b = overlayPixelComponents(grey, (*bitmap).blue[i], 1.0f);

		// Multiply rgba(251, 242, 163, 150) colour
		r = multiply251Lut[r]; //multiplyPixelComponentsWithAlpha(251, 0.588235f, r);
		g = multiply242Lut[g]; //multiplyPixelComponentsWithAlpha(242, 0.588235f, g);
		b = multiply163Lut[b]; //multiplyPixelComponentsWithAlpha(163, 0.588235f, b);

		// Screen merge rgba(232, 101, 179, 51) colour
		r = screen232Lut[r]; //screenPixelComponent(232, 0.2f, r);
		g = screen101Lut[g]; //screenPixelComponent(101, 0.2f, g);
		b = screen179Lut[b]; //screenPixelComponent(179, 0.2f, b);

		// Screen merge rgba(9, 73, 233, 43) colour
		red[i] = screen9Lut[r]; //screenPixelComponent(9, 0.168627f, r);
		green[i] = screen73Lut[g]; //screenPixelComponent(73, 0.168627f, g);
		blue[i] = screen233Lut[b]; //screenPixelComponent(233, 0.168627f, b);
	}
}

