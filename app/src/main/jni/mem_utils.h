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

#ifndef MEM_UTILS
#define MEM_UTILS
#endif

static const int MEMORY_OK = 0;
static const int INT_ARRAY_ERROR = 1;
static const int DOUBLE_ARRAY_ERROR = 2;
static const int UCHAR_ARRAY_ERROR = 3;
static const int FLOAT_ARRAY_ERROR = 4;
static const int JNI_GET_INT_ARRAY_ERROR = 5;

int newIntArray(unsigned int size, int** arrayPointer);
int newUnsignedIntArray(unsigned int size, unsigned int** arrayPointer);
int newDoubleArray(unsigned int size, double** arrayPointer);
int newUnsignedCharArray(unsigned int size, unsigned char** arrayPointer);
int newFloatArray(unsigned int size, float** arrayPointer);
void freeIntArray(int** arrayPointer);
void freeUnsignedIntArray(unsigned int** arrayPointer);
void freeDoubleArray(double** arrayPointer);
void freeUnsignedCharArray(unsigned char** arrayPointer);
void freeFloatArray(float** arrayPointer);
