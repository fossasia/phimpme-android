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
#include <stdlib.h>
#include <android/log.h>

#define  LOG_TAG    "transform.c"
#define  LOGI(...)  __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)
#define  LOGE(...)  __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)

void expandTransformListByOne(Bitmap* bitmap) {
	if ((*bitmap).transformList.transforms == NULL) {
		newUnsignedCharArray(1, &(*bitmap).transformList.transforms); //TODO worth checking the result code here?
		(*bitmap).transformList.size = 1;
	} else {
		unsigned char* newTransforms;
		newUnsignedCharArray((*bitmap).transformList.size + 1, &newTransforms); //TODO worth checking the result code here?
		memmove(newTransforms, (*bitmap).transformList.transforms, (*bitmap).transformList.size);
		freeUnsignedCharArray(&(*bitmap).transformList.transforms);
		(*bitmap).transformList.transforms = newTransforms;
		(*bitmap).transformList.size++;
	}
}

void addTransformFlipHorizontally(Bitmap* bitmap) {
	expandTransformListByOne(bitmap);
	(*bitmap).transformList.transforms[(*bitmap).transformList.size-1] = FLIP_HORIZONTALLY;
}

void addTransformFlipVertically(Bitmap* bitmap) {
	expandTransformListByOne(bitmap);
	(*bitmap).transformList.transforms[(*bitmap).transformList.size-1] = FLIP_VERTICALLY;
}

void addTransformRotate90(Bitmap* bitmap) {
	expandTransformListByOne(bitmap);
	(*bitmap).transformList.transforms[(*bitmap).transformList.size-1] = ROTATE_90;
}

void addTransformRotate180(Bitmap* bitmap) {
	expandTransformListByOne(bitmap);
	(*bitmap).transformList.transforms[(*bitmap).transformList.size-1] = ROTATE_180;
}

void addTransformCrop(Bitmap* bitmap, float* left, float* top, float* right, float* bottom) {
	expandTransformListByOne(bitmap);
	(*bitmap).transformList.transforms[(*bitmap).transformList.size-1] = CROP;
	(*bitmap).transformList.cropBounds[0] = *left;
	(*bitmap).transformList.cropBounds[1] = *top;
	(*bitmap).transformList.cropBounds[2] = *right;
	(*bitmap).transformList.cropBounds[3] = *bottom;
}

// In memory horizontal flip
// Can perform flip on entire bitmap on specific colour components specified using the parameter flags
void flipHorizontally(Bitmap* bitmap, int doRed, int doGreen, int doBlue) {
	int y, x, leftPos, rightPos;

	if (doRed) {
		unsigned char leftR;
		int width = (*bitmap).redWidth;
		int height = (*bitmap).redHeight;
		for (y = 0; y < height; y++) {
			for (x = 0; x < width/2; x++) {
				leftPos = (y * width) + x;
				rightPos = (y * width) + (width-1-x);
				// Copy the red component from the left column
				leftR = (*bitmap).red[leftPos];
				// Copy red component from right column to left column
				(*bitmap).red[leftPos] = (*bitmap).red[rightPos];
				// Copy original left column red component in the right column
				(*bitmap).red[rightPos] = leftR;
			}
		}
	}

	if (doGreen) {
		unsigned char leftG;
		int width = (*bitmap).greenWidth;
		int height = (*bitmap).greenHeight;
		for (y = 0; y < height; y++) {
			for (x = 0; x < width/2; x++) {
				leftPos = (y * width) + x;
				rightPos = (y * width) + (width-1-x);
				// Copy the green component from the left column
				leftG = (*bitmap).green[leftPos];
				// Copy green component from right column to left column
				(*bitmap).green[leftPos] = (*bitmap).green[rightPos];
				// Copy original left column green component in the right column
				(*bitmap).green[rightPos] = leftG;
			}
		}
	}

	if (doBlue) {
		unsigned char leftB;
		int width = (*bitmap).blueWidth;
		int height = (*bitmap).blueHeight;
		for (y = 0; y < height; y++) {
			for (x = 0; x < width/2; x++) {
				leftPos = (y * width) + x;
				rightPos = (y * width) + (width-1-x);
				// Copy the blue component from the left column
				leftB = (*bitmap).blue[leftPos];
				// Copy blue component from right column to left column
				(*bitmap).blue[leftPos] = (*bitmap).blue[rightPos];
				// Copy original left column blue component in the right column
				(*bitmap).blue[rightPos] = leftB;
			}
		}
	}
}

// In memory vertical flip
// Can perform flip on entire bitmap on specific colour components specified using the parameter flags
void flipVertically(Bitmap* bitmap, int doRed, int doGreen, int doBlue) {
	int y, x, topPos, bottomPos;

	if (doRed) {
		unsigned char topR;
		int width = (*bitmap).redWidth;
		int height = (*bitmap).redHeight;
		for (y = 0; y < height/2; y++) {
			for (x = 0; x < width; x++) {
				topPos = (y * width) + x;
				bottomPos = (((height - 1) - y) * width) + x;
				// Copy the red component from the top row
				topR = (*bitmap).red[topPos];
				// Copy red component from bottom row to top row
				(*bitmap).red[topPos] = (*bitmap).red[bottomPos];
				// Copy original top row red component to the bottom row
				(*bitmap).red[bottomPos] = topR;
			}
		}
	}

	if (doGreen) {
		unsigned char topG;
		int width = (*bitmap).greenWidth;
		int height = (*bitmap).greenHeight;
		for (y = 0; y < height/2; y++) {
			for (x = 0; x < width; x++) {
				topPos = (y * width) + x;
				bottomPos = (((height - 1) - y) * width) + x;
				// Copy the green component from the top row
				topG = (*bitmap).green[topPos];
				// Copy green component from bottom row to top row
				(*bitmap).green[topPos] = (*bitmap).green[bottomPos];
				// Copy original top row green component to the bottom row
				(*bitmap).green[bottomPos] = topG;
			}
		}
	}

	if (doBlue) {
		unsigned char topB;
		int width = (*bitmap).blueWidth;
		int height = (*bitmap).blueHeight;
		for (y = 0; y < height/2; y++) {
			for (x = 0; x < width; x++) {
				topPos = (y * width) + x;
				bottomPos = (((height - 1) - y) * width) + x;
				// Copy the blue component from the top row
				topB = (*bitmap).blue[topPos];
				// Copy blue component from bottom row to top row
				(*bitmap).blue[topPos] = (*bitmap).blue[bottomPos];
				// Copy original top row blue component to the bottom row
				(*bitmap).blue[bottomPos] = topB;
			}
		}
	}
}

// Rotates a colour component by 90 degrees clockwise
// The rotated values are inside unsigned char* rotated
static void rotate90Component(unsigned char* componentPixels, unsigned char* rotated, int width, int height) {
	int x, y, newPos, oldPos;

	for (y = 0; y < height; y++) {
		for (x = 0; x < width; x++) {
			newPos = (x * height) + (height - y - 1);
			oldPos = (y * width) + x;

			rotated[newPos] = componentPixels[oldPos];
		}
	}
}

// Rotates an entire bitmap or just individual colour components by 90 degrees clockwise
// Use the parameter flags to indicate which colour components should be rotated.
//TODO what about the bitmaps width and height being swapped?
int rotate90(Bitmap* bitmap, int doRed, int doGreen, int doBlue) {
	unsigned char* rotatedRed;
	unsigned char* rotatedGreen;
	unsigned char* rotatedBlue;

	if (doRed) {
		int returnCode = newUnsignedCharArray((*bitmap).redWidth * (*bitmap).redHeight, &rotatedRed);
		if (returnCode != MEMORY_OK) {
			return returnCode;
		}

		rotate90Component((*bitmap).red, rotatedRed, (*bitmap).redWidth, (*bitmap).redHeight);

		freeUnsignedCharArray(&(*bitmap).red);
		(*bitmap).red = rotatedRed;

		//Swap the red dimensions
		int temp = (*bitmap).redWidth;
		(*bitmap).redWidth = (*bitmap).redHeight;
		(*bitmap).redHeight = temp;
	}

	if (doGreen) {
		int returnCode = newUnsignedCharArray((*bitmap).greenWidth * (*bitmap).greenHeight, &rotatedGreen);
		if (returnCode != MEMORY_OK) {
			freeUnsignedCharArray(&rotatedRed);
			return returnCode;
		}

		rotate90Component((*bitmap).green, rotatedGreen, (*bitmap).greenWidth, (*bitmap).greenHeight);

		freeUnsignedCharArray(&(*bitmap).green);
		(*bitmap).green = rotatedGreen;

		//Swap the green dimensions
		int temp = (*bitmap).greenWidth;
		(*bitmap).greenWidth = (*bitmap).greenHeight;
		(*bitmap).greenHeight = temp;
	}

	if (doBlue) {
		int returnCode = newUnsignedCharArray((*bitmap).blueWidth * (*bitmap).blueHeight, &rotatedBlue);
		if (returnCode != MEMORY_OK) {
			freeUnsignedCharArray(&rotatedRed);
			freeUnsignedCharArray(&rotatedGreen);
			return returnCode;
		}

		rotate90Component((*bitmap).blue, rotatedBlue, (*bitmap).blueWidth, (*bitmap).blueHeight);

		freeUnsignedCharArray(&(*bitmap).blue);
		(*bitmap).blue = rotatedBlue;

		//Swap the blue dimensions
		int temp = (*bitmap).blueWidth;
		(*bitmap).blueWidth = (*bitmap).blueHeight;
		(*bitmap).blueHeight = temp;
	}
}

// Performs an in-memory 180 degrees rotation of an entire bitmap or just a colour component
void rotate180(Bitmap* bitmap, int doRed, int doGreen, int doBlue) {
	flipVertically(bitmap, doRed, doGreen, doBlue);
	flipHorizontally(bitmap, doRed, doGreen, doBlue);
}

// Crops a bitmap's colour component to new dimensions
// Cropped bitmap component will be in unsigned char* cropped
static void cropComponent(unsigned char* componentPixels, unsigned char* cropped, int originalWidth, int newWidth, int newHeight, int newTop, int newLeft) {
	int x, y;
	for (y = 0; y < newHeight; y++) {
		for (x = 0; x < newWidth; x++) {
			cropped[(y * newWidth) + x] = componentPixels[((newTop+y) * originalWidth) + (newLeft + x)];
		}
	}
}

// Crops a bitmap or just specific colour components to the new bounds
int crop(Bitmap* bitmap, float* leftPtr, float* topPtr, float* rightPtr, float* bottomPtr, int doRed, int doGreen, int doBlue) {
	float left = *leftPtr;
	float top = *topPtr;
	float right = *rightPtr;
	float bottom = *bottomPtr;
	unsigned char* croppedRed;
	unsigned char* croppedGreen;
	unsigned char* croppedBlue;
	if (doRed) {
		int newLeft = (float)(*bitmap).redWidth * left;
		int newRight = (float)(*bitmap).redWidth * right;
		int newTop = (float)(*bitmap).redHeight * top;
		int newBottom = (float)(*bitmap).redHeight * bottom;
		int newWidth = newRight - newLeft;
		int newHeight = newBottom - newTop;
		int newSize = newWidth*newHeight;
		int returnCode = newUnsignedCharArray(newSize, &croppedRed);
		if (returnCode != MEMORY_OK) {
			return returnCode;
		}

		cropComponent((*bitmap).red, croppedRed, (*bitmap).redWidth, newWidth, newHeight, newTop, newLeft);

		freeUnsignedCharArray(&(*bitmap).red);
		(*bitmap).red = croppedRed;

		(*bitmap).redWidth = newWidth;
		(*bitmap).redHeight = newHeight;
	}

	if (doGreen) {
		int newLeft = (float)(*bitmap).greenWidth * left;
		int newRight = (float)(*bitmap).greenWidth * right;
		int newTop = (float)(*bitmap).greenHeight * top;
		int newBottom = (float)(*bitmap).greenHeight * bottom;
		int newWidth = newRight - newLeft;
		int newHeight = newBottom - newTop;
		int newSize = newWidth*newHeight;
		int returnCode = newUnsignedCharArray(newSize, &croppedGreen);
		if (returnCode != MEMORY_OK) {
			freeUnsignedCharArray(&croppedRed);
			return returnCode;
		}

		cropComponent((*bitmap).green, croppedGreen, (*bitmap).greenWidth, newWidth, newHeight, newTop, newLeft);

		freeUnsignedCharArray(&(*bitmap).green);
		(*bitmap).green = croppedGreen;

		(*bitmap).greenWidth = newWidth;
		(*bitmap).greenHeight = newHeight;
	}

	if (doBlue) {
		int newLeft = (float)(*bitmap).blueWidth * left;
		int newRight = (float)(*bitmap).blueWidth * right;
		int newTop = (float)(*bitmap).blueHeight * top;
		int newBottom = (float)(*bitmap).blueHeight * bottom;
		int newWidth = newRight - newLeft;
		int newHeight = newBottom - newTop;
		int newSize = newWidth*newHeight;
		int returnCode = newUnsignedCharArray(newSize, &croppedBlue);
		if (returnCode != MEMORY_OK) {
			freeUnsignedCharArray(&croppedRed);
			freeUnsignedCharArray(&croppedGreen);
			return returnCode;
		}

		cropComponent((*bitmap).blue, croppedBlue, (*bitmap).blueWidth, newWidth, newHeight, newTop, newLeft);

		freeUnsignedCharArray(&(*bitmap).blue);
		(*bitmap).blue = croppedBlue;

		(*bitmap).blueWidth = newWidth;
		(*bitmap).blueHeight = newHeight;
	}

	return MEMORY_OK;
}

int doTransforms(Bitmap* bitmap, int doRed, int doGreen, int doBlue) {
	if ((*bitmap).transformList.transforms == NULL) {
		return MEMORY_OK;
	}

	int numTransforms = (*bitmap).transformList.size;
	int i;
	for (i = 0; i < numTransforms; i++) {
		char type = (*bitmap).transformList.transforms[i];
		if (type == FLIP_HORIZONTALLY) {
			flipHorizontally(bitmap, doRed, doGreen, doBlue);
		} else if (type == FLIP_VERTICALLY) {
			flipVertically(bitmap, doRed, doGreen, doBlue);
		} else if (type == ROTATE_90) {
			rotate90(bitmap, doRed, doGreen, doBlue);
		} else if (type == ROTATE_180) {
			rotate180(bitmap, doRed, doGreen, doBlue);
		} else if (type == CROP) {
			float left = (*bitmap).transformList.cropBounds[0];
			float top = (*bitmap).transformList.cropBounds[1];
			float right = (*bitmap).transformList.cropBounds[2];
			float bottom = (*bitmap).transformList.cropBounds[3];
			crop(bitmap, &left, &top, &right, &bottom, doRed, doGreen, doBlue);
		}
	}
}
