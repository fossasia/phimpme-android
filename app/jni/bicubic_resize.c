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

#include <stdlib.h>
#include <mem_utils.h>

int resizeChannelBicubic(const unsigned char *src, int srcWidth, int srcHeight, unsigned char *dst, int dstWidth, int dstHeight) {
	unsigned char *xVector;
	int i, nextCol, nextRow, numRows, ty, x, y;
	double factor, *s, *scanline, *scaleScanline, *t, xScale, xSpan,
			yScale, ySpan, *yVector;

	factor = (double) dstWidth / (double) srcWidth;

	if (dst == NULL) {
		return -1;
	}

	/* No scaling needed. */
	if (srcWidth == dstWidth) {
		memcpy(dst, src, srcWidth * srcHeight);
		return 0;
	}

	int returnCode = newUnsignedCharArray(srcWidth, &xVector);
	if (returnCode != MEMORY_OK) {
		return returnCode;
	}

	returnCode = newDoubleArray(srcWidth, &yVector);
	if (returnCode != MEMORY_OK) {
		freeUnsignedCharArray(&xVector);
		return returnCode;
	}

	returnCode = newDoubleArray(srcWidth, &scanline);
	if (returnCode != MEMORY_OK) {
		freeUnsignedCharArray(&xVector);
		freeDoubleArray(&yVector);
		return returnCode;
	}

	returnCode = newDoubleArray((dstWidth + 1), &scaleScanline);
	if (returnCode != MEMORY_OK) {
		freeUnsignedCharArray(&xVector);
		freeDoubleArray(&yVector);
		freeDoubleArray(&scanline);
		return returnCode;
	}

	numRows = 0;
	nextRow = 1;
	ySpan = 1.0;
	yScale = factor;
	i = 0;

	for (y = 0; y < dstHeight; y++) {
		ty = y * dstWidth;

		memset(yVector, 0, srcWidth * sizeof(double));
		memset(scaleScanline, 0, dstWidth * sizeof(double));

		/* Scale Y-dimension. */
		while (yScale < ySpan) {
			if (nextRow && numRows < srcHeight) {
				/* Read a new scanline.  */
				memcpy(xVector, src, srcWidth);
				src += srcWidth;
				numRows++;
			}
			for (x = 0; x < srcWidth; x++) {
				yVector[x] += yScale * (double) xVector[x];
			}
			ySpan -= yScale;
			yScale = factor;
			nextRow = 1;
		}
		if (nextRow && numRows < srcHeight) {
			/* Read a new scanline.  */
			memcpy(xVector, src, srcWidth);
			src += srcWidth;
			numRows++;
			nextRow = 0;
		}
		s = scanline;
		for (x = 0; x < srcWidth; x++) {
			yVector[x] += ySpan * (double) xVector[x];
			*s = yVector[x];
			s++;
		}
		yScale -= ySpan;
		if (yScale <= 0) {
			yScale = factor;
			nextRow = 1;
		}
		ySpan = 1.0;

		nextCol = 0;
		xSpan = 1.0;
		s = scanline;
		t = scaleScanline;

		/* Scale X dimension. */
		for (x = 0; x < srcWidth; x++) {
			xScale = factor;
			while (xScale >= xSpan) {
				if (nextCol) {
					t++;
				}
				t[0] += xSpan * s[0];
				xScale -= xSpan;
				xSpan = 1.0;
				nextCol = 1;
			}
			if (xScale > 0) {
				if (nextCol) {
					nextCol = 0;
					t++;
				}
				t[0] += xScale * s[0];
				xSpan -= xScale;
			}
			s++;
		}

		/* Copy scanline to target. */
		t = scaleScanline;
		for (x = 0; x < dstWidth; x++) {
			dst[ty + x] = (unsigned char) t[x];
		}
	}

	freeUnsignedCharArray(&xVector);
	freeDoubleArray(&yVector);
	freeDoubleArray(&scanline);
	freeDoubleArray(&scaleScanline);

	return MEMORY_OK;
}
