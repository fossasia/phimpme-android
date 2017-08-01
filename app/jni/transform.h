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

#ifndef TRANSFORM
#define TRANSFORM
#endif

static const char FLIP_HORIZONTALLY = 'h';
static const char FLIP_VERTICALLY = 'v';
static const char ROTATE_90 = 'r';
static const char ROTATE_180 = 'u';
static const char CROP = 'c';

typedef struct {
	float cropBounds[4]; //left, top, right, bottom
	unsigned char* transforms;
	int size;
} TransformList;
