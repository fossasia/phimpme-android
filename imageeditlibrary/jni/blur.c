// Stack Blur Algorithm by Mario Klingemann <mario@quasimondo.com>
// Ported to C by Nilesh Patel from the source code found here
// http://incubator.quasimondo.com/processing/stackblur.pde
// and altered to process each colour channel (r, g, b) separately.

#include <mem_utils.h>
#include <stdlib.h>
#include <android/log.h>

#define  LOG_TAG    "blur.c"
#define  LOGI(...)  __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)
#define  LOGE(...)  __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)

static int fastBlur(int radius, unsigned char* srcRed, unsigned char* srcGreen, unsigned char* srcBlue, int width, int height, unsigned char* dstRed, unsigned char* dstGreen, unsigned char* dstBlue) {
	int windowSize = radius * 2 + 1;
	int radiusPlusOne = radius + 1;

	int sumRed;
	int sumGreen;
	int sumBlue;

	int srcIndex = 0;
	int dstIndex;

	int sumLookupTableSize = 256 * windowSize;
	int* sumLookupTable;
	int returnCode = newIntArray(sumLookupTableSize, &sumLookupTable);
	if (returnCode != MEMORY_OK) {
		return returnCode;
	}

	int i;
	for (i = 0; i < sumLookupTableSize; i++) {
		sumLookupTable[i] = i / windowSize;
	}

	int* indexLookupTable;
	returnCode = newIntArray(radiusPlusOne, &indexLookupTable);
	if (returnCode != MEMORY_OK) {
		free(sumLookupTable);
		return returnCode;
	}

	if (radius < width) {
		for (i = 0; i < radiusPlusOne; i++) {
			indexLookupTable[i] = i;
		}
	} else {
		for (i = 0; i < width; i++) {
			indexLookupTable[i] = i;
		}
		for (i = width; i < radiusPlusOne; i++) {
			indexLookupTable[i] = width - 1;
		}
	}

	int x, y;
	for (y = 0; y < height; y++) {
		sumRed = sumGreen = sumBlue = 0;
		dstIndex = y;


		sumRed += radiusPlusOne * srcRed[srcIndex];
		sumGreen += radiusPlusOne * srcGreen[srcIndex];
		sumBlue += radiusPlusOne * srcBlue[srcIndex];

		for (i = 1; i <= radius; i++) {
			sumRed += srcRed[srcIndex + indexLookupTable[i]];
			sumGreen += srcGreen[srcIndex + indexLookupTable[i]];
			sumBlue += srcBlue[srcIndex + indexLookupTable[i]];
		}

		for (x = 0; x < width; x++) {
			dstRed[dstIndex] = sumLookupTable[sumRed];
			dstGreen[dstIndex] = sumLookupTable[sumGreen];
			dstBlue[dstIndex] = sumLookupTable[sumBlue];
			dstIndex += height;

			int nextPixelIndex = x + radiusPlusOne;
			if (nextPixelIndex >= width) {
				nextPixelIndex = width - 1;
			}

			int previousPixelIndex = x - radius;
			if (previousPixelIndex < 0) {
				previousPixelIndex = 0;
			}

			sumRed += srcRed[srcIndex + nextPixelIndex];
			sumRed -= srcRed[srcIndex + previousPixelIndex];

			sumGreen += srcGreen[srcIndex + nextPixelIndex];
			sumGreen -= srcGreen[srcIndex + previousPixelIndex];

			sumBlue += srcBlue[srcIndex + nextPixelIndex];
			sumBlue -= srcBlue[srcIndex + previousPixelIndex];
		}

		srcIndex += width;
	}

	free(sumLookupTable);
	free(indexLookupTable);

	return MEMORY_OK;
}

static int fastBlurComponent(int radius, unsigned char* srcComponent, int width, int height, unsigned char* dstComponent) {
	unsigned int windowSize = radius * 2 + 1;
	unsigned int radiusPlusOne = radius + 1;

	unsigned int sumComponent;

	register unsigned int srcIndex = 0;
	register unsigned int dstIndex;

	unsigned int sumLookupTableSize = 256 * windowSize;
	unsigned int* sumLookupTable;
	int returnCode = newUnsignedIntArray(sumLookupTableSize, &sumLookupTable);
	if (returnCode != MEMORY_OK) {
		return returnCode;
	}

	register unsigned int i;
	for (i = sumLookupTableSize; i--;) {
		sumLookupTable[i] = i / windowSize;
	}

	unsigned int* indexLookupTable;
	returnCode = newUnsignedIntArray(radiusPlusOne, &indexLookupTable);
	if (returnCode != MEMORY_OK) {
		freeUnsignedIntArray(&sumLookupTable);
		return returnCode;
	}

	if (radius < width) {
		for (i = radiusPlusOne; i--; ) {
			indexLookupTable[i] = i;
		}
	} else {
		for (i = width; i--; ) {
			indexLookupTable[i] = i;
		}
		for (i = width; i < radiusPlusOne; i++) {
			indexLookupTable[i] = width - 1;
		}
	}

	register unsigned int x, y;
	register int nextPixelIndex;
	register int previousPixelIndex;
	for (y = 0; y < height; y++) {
		sumComponent = 0;
		dstIndex = y;

		sumComponent += radiusPlusOne * srcComponent[srcIndex];

		for (i = 1; i <= radius; i++) {
			sumComponent += srcComponent[srcIndex + indexLookupTable[i]];
		}

		for (x = 0; x < width; x++) {
			dstComponent[dstIndex] = sumLookupTable[sumComponent];
			dstIndex += height;

			nextPixelIndex = x + radiusPlusOne;
			if (nextPixelIndex >= width) {
				nextPixelIndex = width - 1;
			}

			previousPixelIndex = x - radius;
			if (previousPixelIndex < 0) {
				previousPixelIndex = 0;
			}

			sumComponent += srcComponent[srcIndex + nextPixelIndex];
			sumComponent -= srcComponent[srcIndex + previousPixelIndex];
		}

		srcIndex += width;
	}

	freeUnsignedIntArray(&sumLookupTable);
	freeUnsignedIntArray(&indexLookupTable);

	return MEMORY_OK;
}

int stackBlur(float* radius, unsigned char* srcRed, unsigned char* srcGreen, unsigned char* srcBlue, int* width, int* height,
		unsigned char* dstRed, unsigned char* dstGreen, unsigned char* dstBlue) {
	unsigned int size = (*width) * (*height);

	unsigned char* srcComponentCopy;
	int returnCode = newUnsignedCharArray(size, &srcComponentCopy);
	if (returnCode != MEMORY_OK) {
		return returnCode;
	}

	unsigned int i, c;
	unsigned char* srcComponent;
	unsigned char* dstComponent;
	for (c = 3; c--; ) {
		if (c == 0) {
			srcComponent = srcRed;
			dstComponent = dstRed;
		} else if (c == 1) {
			srcComponent = srcGreen;
			dstComponent = dstGreen;
		} else {
			srcComponent = srcBlue;
			dstComponent = dstBlue;
		}

		memcpy(srcComponentCopy, srcComponent, size);
		for (i = 3; i--; ) {
			//horizontal pass
			returnCode = fastBlurComponent((int)(*radius), srcComponentCopy, *width, *height, dstComponent);
			if (returnCode != MEMORY_OK) {
				freeUnsignedCharArray(&srcComponentCopy);
				return returnCode;
			}
			//vertical pass
			returnCode = fastBlurComponent((int)(*radius), dstComponent, *height, *width, srcComponentCopy);
			if (returnCode != MEMORY_OK) {
				freeUnsignedCharArray(&srcComponentCopy);
				return returnCode;
			}
		}
		memcpy(dstComponent, srcComponentCopy, size);
	}
	freeUnsignedCharArray(&srcComponentCopy);
	return MEMORY_OK;
}

int stackBlurComponent(float* radius, unsigned char* srcComponent, int* width, int* height, unsigned char* dstComponent) {
	int size = (*width) * (*height);
	unsigned char* srcComponentCopy;
	int returnCode = newUnsignedCharArray(size, &srcComponentCopy);
	if (returnCode != MEMORY_OK) {
		return returnCode;
	}

	int i;
	memcpy(srcComponentCopy, srcComponent, size);
	for (i = 3; i--; ) {
		//horizontal pass
		returnCode = fastBlurComponent((int)(*radius), srcComponentCopy, *width, *height, dstComponent);
		if (returnCode != MEMORY_OK) {
			freeUnsignedCharArray(&srcComponentCopy);
			return returnCode;
		}
		//vertical pass
		returnCode = fastBlurComponent((int)(*radius), dstComponent, *height, *width, srcComponentCopy);
		if (returnCode != MEMORY_OK) {
			freeUnsignedCharArray(&srcComponentCopy);
			return returnCode;
		}
	}
	memcpy(dstComponent, srcComponentCopy, size);
	freeUnsignedCharArray(&srcComponentCopy);

	return MEMORY_OK;
}
