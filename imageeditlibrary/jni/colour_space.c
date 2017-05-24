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

#include <colour_space.h>
#include <math.h>
#include <android/log.h>

#define  LOG_TAG    "colour_space.c"
#define  LOGI(...)  __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)
#define  LOGE(...)  __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)

void rgbToHsb(unsigned char red, unsigned char green, unsigned char blue, HSBColour* hsb) {
	float min, max;
	if (red < green) {
		min = red;
		max = green;
	} else {
		min = green;
		max = red;
	}
	if (blue > max) {
		max = blue;
	} else if (blue < min) {
		min = blue;
	}
	float delta = max - min;

	(*hsb).b = max/255;
	if (max != 0.0) {
		(*hsb).s = delta/max;
	} else {
		(*hsb).s = 0.0;
	}

	if ((*hsb).s == 0.0) {
		(*hsb).h = 0;
	} else {
		delta *= 6;
		if (red == max) {
			(*hsb).h = (green - blue)/delta;
		} else if (green == max) {
			(*hsb).h = 0.333333f + (blue - red)/delta;
		} else if (blue == max) {
			(*hsb).h = 0.666666f + (red - green)/delta;
		}

		if ((*hsb).h < 0) {
			(*hsb).h++;
		}
	}
}

void getBrightness(unsigned char red, unsigned char green, unsigned char blue, float* brightness) {
	float min, max;
	if (red < green) {
		min = red;
		max = green;
	} else {
		min = green;
		max = red;
	}
	if (blue > max) {
		max = blue;
	} else if (blue < min) {
		min = blue;
	}
	float delta = max - min;

	(*brightness) = max/255;
}

inline unsigned char convert(float val) {
	return floorf((255 * val) + 0.5f);
}

void hsbToRgb(HSBColour* hsb, unsigned char* red, unsigned char* green, unsigned char* blue) {
	if ((*hsb).s == 0) {
		*red = *green = *blue = convert((*hsb).b);
	} else {
		register unsigned int i;
		register float aa, bb, cc, f;

		register float h = (*hsb).h;
		register float s = (*hsb).s;
		register float b = (*hsb).b;

		if (h == 1.0) {
			h = 0;
		}

		h *= 6.0;
		i = floorf(h);
		f = h - i;
		aa = b * (1 - s);
		bb = b * (1 - (s * f));
		cc = b * (1 - (s * (1 - f)));
		switch (i) {
		case 0:
			*red = convert(b);
			*green = convert(cc);
			*blue = convert(aa);
			break;
		case 1:
			*red = convert(bb);
			*green = convert(b);
			*blue = convert(aa);
			break;
		case 2:
			*red = convert(aa);
			*green = convert(b);
			*blue = convert(cc);
			break;
		case 3:
			*red = convert(aa);
			*green = convert(bb);
			*blue = convert(b);
			break;
		case 4:
			*red = convert(cc);
			*green = convert(aa);
			*blue = convert(b);
			break;
		case 5:
			*red = convert(b);
			*green = convert(aa);
			*blue = convert(bb);
			break;
		}
	}
}
