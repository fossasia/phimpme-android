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

#ifndef BITMAP
#define BITMAP
#endif

#include <transform.h>

static const int INCONSISTENT_BITMAP_ERROR = 5;

typedef struct {
	unsigned int width;
	unsigned int height;

	unsigned int redWidth;
	unsigned int redHeight;
	unsigned int greenWidth;
	unsigned int greenHeight;
	unsigned int blueWidth;
	unsigned int blueHeight;

	unsigned char* red;
	unsigned char* green;
	unsigned char* blue;

	TransformList transformList;
} Bitmap;
