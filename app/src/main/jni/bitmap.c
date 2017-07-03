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


#include <jni.h>
#include <math.h>
#include <android/log.h>

#include <stdio.h>
#include <stdlib.h>

#include <mem_utils.h>
#include <bitmap.h>

#define  LOG_TAG    "bitmap.c"
#define  LOGI(...)  __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)
#define  LOGE(...)  __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)


inline int rgb(int red, int green, int blue) {
	return (0xFF << 24) | (red << 16) | (green << 8) | blue;
}

inline unsigned char red(int color) {
	return (unsigned char)((color >> 16) & 0xFF);
}

inline unsigned char green(int color) {
	return (unsigned char)((color >> 8) & 0xFF);
}

inline unsigned char blue(int color) {
	return (unsigned char)(color & 0xFF);
}

int getPixelAsInt(Bitmap* bitmap, int x, int y) {
	unsigned int pos = ((*bitmap).width * y) + x;

	return rgb((int)(*bitmap).red[pos], (int)(*bitmap).green[pos], (int)(*bitmap).blue[pos]);
}

static void getScaledSize(int srcWidth, int srcHeight, int numPixels, int* dstWidth, int* dstHeight) {
	float ratio = (float)srcWidth/srcHeight;

	*dstHeight = (int)sqrt((float)numPixels/ratio);
	*dstWidth = (int)(ratio * sqrt((float)numPixels/ratio));
}

void deleteBitmap(Bitmap* bitmap) {
	freeUnsignedCharArray(&(*bitmap).red);
	freeUnsignedCharArray(&(*bitmap).green);
	freeUnsignedCharArray(&(*bitmap).blue);
	freeUnsignedCharArray(&(*bitmap).transformList.transforms);
	(*bitmap).transformList.size = 0;
	(*bitmap).width = 0;
	(*bitmap).height = 0;
}

int initBitmapMemory(Bitmap* bitmap, int width, int height) {
	deleteBitmap(bitmap);

	(*bitmap).width = width;
	(*bitmap).height = height;

	(*bitmap).redWidth = width;
	(*bitmap).redHeight = height;

	(*bitmap).greenWidth = width;
	(*bitmap).greenHeight = height;

	(*bitmap).blueWidth = width;
	(*bitmap).blueHeight = height;

	int size = width*height;

	int resultCode = newUnsignedCharArray(size, &(*bitmap).red);
	if (resultCode != MEMORY_OK) {
		return resultCode;
	}

	resultCode = newUnsignedCharArray(size, &(*bitmap).green);
	if (resultCode != MEMORY_OK) {
		return resultCode;
	}

	resultCode = newUnsignedCharArray(size, &(*bitmap).blue);
	if (resultCode != MEMORY_OK) {
		return resultCode;
	}
}

int decodeJpegData(char* jpegData, int jpegSize, int maxPixels, Bitmap* bitmap) {
	int returnCode;

	int maxWidth;
	int maxHeight;

	// Decode red channel
	returnCode = decodeJpegChannel(jpegData, jpegSize, 0, &(*bitmap).red, &(*bitmap).redWidth, &(*bitmap).redHeight);
	if (returnCode != MEMORY_OK) {
		LOGE("Failed to decode red channel");
		njDone();
		freeUnsignedCharArray(&(*bitmap).red);
		return returnCode;
	}

	doTransforms(bitmap, 1, 0, 0);
	// Resize red channel
	getScaledSize((*bitmap).redWidth, (*bitmap).redHeight, maxPixels, &maxWidth, &maxHeight); //We only need to do this once as r, g, b should be the same sizes
	returnCode = resizeChannel(&(*bitmap).red, (*bitmap).redWidth, (*bitmap).redHeight, maxWidth, maxHeight);
	if (returnCode != MEMORY_OK) {
		njDone();
		freeUnsignedCharArray(&(*bitmap).red);
		return returnCode;
	}
	// Set red channel dimensions
	if ((*bitmap).redWidth >= maxWidth && (*bitmap).redHeight >= maxHeight) {
		(*bitmap).redWidth = maxWidth;
		(*bitmap).redHeight = maxHeight;
	}

	// Decode green channel
	returnCode = decodeJpegChannel(jpegData, jpegSize, 1, &(*bitmap).green, &(*bitmap).greenWidth, &(*bitmap).greenHeight);
	if (returnCode != MEMORY_OK) {
		LOGE("Failed to decode green channel");
		njDone();
		freeUnsignedCharArray(&(*bitmap).red);
		freeUnsignedCharArray(&(*bitmap).green);
		return returnCode;
	}

	doTransforms(bitmap, 0, 1, 0);
	// Resize green channel
	returnCode = resizeChannel(&(*bitmap).green, (*bitmap).greenWidth, (*bitmap).greenHeight, maxWidth, maxHeight);
	if (returnCode != MEMORY_OK) {
		njDone();
		freeUnsignedCharArray(&(*bitmap).red);
		freeUnsignedCharArray(&(*bitmap).green);
		return returnCode;
	}
	// Set green channel dimensions
	if ((*bitmap).greenWidth >= maxWidth && (*bitmap).greenHeight >= maxHeight) {
		(*bitmap).greenWidth = maxWidth;
		(*bitmap).greenHeight = maxHeight;
	}

	// Decode blue channel
	returnCode = decodeJpegChannel(jpegData, jpegSize, 2, &(*bitmap).blue, &(*bitmap).blueWidth, &(*bitmap).blueHeight);
	if (returnCode != MEMORY_OK) {
		LOGE("Failed to decode blue channel");
		njDone();
		freeUnsignedCharArray(&(*bitmap).red);
		freeUnsignedCharArray(&(*bitmap).green);
		freeUnsignedCharArray(&(*bitmap).blue);
		return returnCode;
	}

	doTransforms(bitmap, 0, 0, 1);
	// Resize blue channel
	returnCode = resizeChannel(&(*bitmap).blue, (*bitmap).blueWidth, (*bitmap).blueHeight, maxWidth, maxHeight);
	if (returnCode != MEMORY_OK) {
		njDone();
		freeUnsignedCharArray(&(*bitmap).red);
		freeUnsignedCharArray(&(*bitmap).green);
		freeUnsignedCharArray(&(*bitmap).blue);
		return returnCode;
	}
	// Set blue channel dimensions
	if ((*bitmap).blueWidth >= maxWidth && (*bitmap).blueHeight >= maxHeight) {
		(*bitmap).blueWidth = maxWidth;
		(*bitmap).blueHeight = maxHeight;
	}

	// Set the final bitmap dimensions
	if ((*bitmap).redWidth == (*bitmap).greenWidth && (*bitmap).redWidth == (*bitmap).blueWidth
			&& (*bitmap).redHeight == (*bitmap).greenHeight && (*bitmap).redHeight == (*bitmap).blueHeight) {
		(*bitmap).width = (*bitmap).redWidth;
		(*bitmap).height = (*bitmap).redHeight;
	} else {
		njDone();
		freeUnsignedCharArray(&(*bitmap).red);
		freeUnsignedCharArray(&(*bitmap).green);
		freeUnsignedCharArray(&(*bitmap).blue);
		return INCONSISTENT_BITMAP_ERROR;
	}

	njDoneLeaveRGBData();

	return MEMORY_OK;
}

int decodeJpegChannel(char* jpegData, int jpegSize, int channel, unsigned char** channelPixels, int* srcWidth, int* srcHeight) {
	int returnCode;
	if (channel == 0) { // Decode the red channel
		njInit();
		returnCode = njDecode(jpegData, jpegSize, 1, 0, 0);
	} else if (channel == 1) { // Decode the green channel
		njInit();
		returnCode = njDecode(jpegData, jpegSize, 0, 1, 0);
	} else if (channel == 2) { // Decode the blue channel
		njInit();
		returnCode = njDecode(jpegData, jpegSize, 0, 0, 1);
	}

	if (returnCode != 0) {
		LOGE("Failed to njDecode()");
		njDone();
		return returnCode;
	}

	*srcWidth = njGetWidth();
	*srcHeight = njGetHeight();

	if (channel == 0) { // Get the red channel pixels
		*channelPixels = (unsigned char*)njGetRedImage();
	} else if (channel == 1) { // Get the green channel pixels
		*channelPixels = (unsigned char*)njGetGreenImage();
	} else if (channel == 2) { // Get the blue channel pixels
		*channelPixels = (unsigned char*)njGetBlueImage();
	}

	return MEMORY_OK;
}

int resizeChannel(unsigned char** channelPixels, int srcWidth, int srcHeight, int maxWidth, int maxHeight) {
	// Resize channel
	if (srcWidth > maxWidth && srcHeight > maxHeight) {
		unsigned char* scaledPixels;
		int returnCode = newUnsignedCharArray(maxWidth * maxHeight, &scaledPixels);
		if (returnCode != MEMORY_OK) {
			freeUnsignedCharArray(&scaledPixels);
			return returnCode;
		}
		returnCode = resizeChannelBicubic(*channelPixels, srcWidth, srcHeight, scaledPixels, maxWidth, maxHeight);
		if (returnCode != MEMORY_OK) {
			freeUnsignedCharArray(&scaledPixels);
			return returnCode;
		}
		//No need for hi-res channel so free the memory
		freeUnsignedCharArray(channelPixels); //channelPixels is already a pointer to a pointer

		*channelPixels = scaledPixels;
	}

	return MEMORY_OK;
}

void getBitmapRowAsIntegers(Bitmap* bitmap, int y, int* pixels) {
	unsigned int width = (*bitmap).width;
	register unsigned int i = (width*y) + width - 1;
	register unsigned int x;
	for (x = width; x--; i--) {
		pixels[x] = rgb((int)(*bitmap).red[i], (int)(*bitmap).green[i], (int)(*bitmap).blue[i]);
	}
}

void setBitmapRowFromIntegers(Bitmap* bitmap, int y, int* pixels) {
	unsigned int width = (*bitmap).width;
	register unsigned int i = (width*y) + width - 1;
	register unsigned int x;
	for (x = width; x--; i--) {
		(*bitmap).red[i] = red(pixels[x]);
		(*bitmap).green[i] = green(pixels[x]);
		(*bitmap).blue[i] = blue(pixels[x]);
	}
}
