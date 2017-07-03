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

int newIntArray(unsigned int size, int** arrayPointer) {
	unsigned int numBytes = size * sizeof(int);
	*arrayPointer = (int*) malloc(numBytes);
	if (arrayPointer == NULL) {
		return INT_ARRAY_ERROR;
	}

	memset(*arrayPointer, 0, numBytes);
	return MEMORY_OK;
}

int newUnsignedIntArray(unsigned int size, unsigned int** arrayPointer) {
	unsigned int numBytes = size * sizeof(unsigned int);
	*arrayPointer = (int*) malloc(numBytes);
	if (arrayPointer == NULL) {
		return INT_ARRAY_ERROR;
	}

	memset(*arrayPointer, 0, numBytes);
	return MEMORY_OK;
}

int newDoubleArray(unsigned int size, double** arrayPointer) {
	unsigned int numBytes = size * sizeof(double);
	*arrayPointer = (double*) malloc(numBytes);
	if (arrayPointer == NULL) {
		return DOUBLE_ARRAY_ERROR;
	}

	memset(*arrayPointer, 0, numBytes);
	return MEMORY_OK;
}

int newUnsignedCharArray(unsigned int size, unsigned char** arrayPointer) {
	unsigned int numBytes = size * sizeof(unsigned char);
	*arrayPointer = (unsigned char*) malloc(numBytes);
	if (arrayPointer == NULL) {
		return UCHAR_ARRAY_ERROR;
	}

	memset(*arrayPointer, 0, numBytes);
	return MEMORY_OK;
}

int newFloatArray(unsigned int size, float** arrayPointer) {
	unsigned int numBytes = size * sizeof(float);
	*arrayPointer = (float*) malloc(numBytes);
	if (arrayPointer == NULL) {
		return FLOAT_ARRAY_ERROR;
	}

	memset(*arrayPointer, 0, numBytes);
	return MEMORY_OK;
}

void freeIntArray(int** arrayPointer) {
	if (*arrayPointer != NULL) {
		free(*arrayPointer);
		*arrayPointer = NULL;
	}
}

void freeUnsignedIntArray(unsigned int** arrayPointer) {
	if (*arrayPointer != NULL) {
		free(*arrayPointer);
		*arrayPointer = NULL;
	}
}

void freeDoubleArray(double** arrayPointer) {
	if (*arrayPointer != NULL) {
		free(*arrayPointer);
		*arrayPointer = NULL;
	}
}

void freeUnsignedCharArray(unsigned char** arrayPointer) {
	if (*arrayPointer != NULL) {
		free(*arrayPointer);
		*arrayPointer = NULL;
	}
}

void freeFloatArray(float** arrayPointer) {
	if (*arrayPointer != NULL) {
		free(*arrayPointer);
		*arrayPointer = NULL;
	}
}
