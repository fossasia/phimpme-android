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
#include <stdlib.h>
#include <bitmap.h>
#include <mem_utils.h>
#include <android/log.h>

#define  LOG_TAG    "PREVIEW_CACHE_IMAGE_PROCESSING"
#define  LOGI(...)  __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)
#define  LOGE(...)  __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)

static Bitmap bitmap;

int Java_org_fossasia_phimpme_editor_editimage_filter_PhotoProcessing_nativeInitBitmap(JNIEnv* env, jobject thiz, jint width, jint height) {
	return initBitmapMemory(&bitmap, width, height);
}

void Java_org_fossasia_phimpme_editor_editimage_filter_PhotoProcessing_nativeGetBitmapRow(JNIEnv* env, jobject thiz, jint y, jintArray pixels) {
	int cpixels[bitmap.width];
	getBitmapRowAsIntegers(&bitmap, (int)y, &cpixels);
	(*env)->SetIntArrayRegion(env, pixels, 0, bitmap.width, cpixels);
}

void Java_org_fossasia_phimpme_editor_editimage_filter_PhotoProcessing_nativeSetBitmapRow(JNIEnv* env, jobject thiz, jint y, jintArray pixels) {
	int cpixels[bitmap.width];
	(*env)->GetIntArrayRegion(env, pixels, 0, bitmap.width, cpixels);
	setBitmapRowFromIntegers(&bitmap, (int)y, &cpixels);
}

int Java_org_fossasia_phimpme_editor_editimage_filter_PhotoProcessing_nativeGetBitmapWidth(JNIEnv* env, jobject thiz) {
	return bitmap.width;
}

int Java_org_fossasia_phimpme_editor_editimage_filter_PhotoProcessing_nativeGetBitmapHeight(JNIEnv* env, jobject thiz) {
	return bitmap.height;
}

void Java_org_fossasia_phimpme_editor_editimage_filter_PhotoProcessing_nativeDeleteBitmap(JNIEnv* env, jobject thiz) {
	deleteBitmap(&bitmap);
}

void Java_org_fossasia_phimpme_editor_editimage_filter_PhotoProcessing_nativeFlipHorizontally(JNIEnv* env, jobject thiz) {
	flipHorizontally(&bitmap, 1, 1, 1);
}

int Java_org_fossasia_phimpme_editor_editimage_filter_PhotoProcessing_nativeRotate90(JNIEnv* env, jobject thiz) {
	int resultCode = rotate90(&bitmap, 1, 1, 1);
	if (resultCode != MEMORY_OK) {
		return resultCode;
	}

	//All the component dimensions should have changed, so copy the correct dimensions
	bitmap.width = bitmap.redWidth;
	bitmap.height = bitmap.redHeight;
}

void Java_org_fossasia_phimpme_editor_editimage_filter_PhotoProcessing_nativeRotate180(JNIEnv* env, jobject thiz) {
	rotate180(&bitmap, 1, 1, 1);
}

void Java_org_fossasia_phimpme_editor_editimage_filter_PhotoProcessing_nativeApplyInstafix(JNIEnv* env, jobject thiz, jint value) {
	applyInstafix(&bitmap,value);
}

void Java_org_fossasia_phimpme_editor_editimage_filter_PhotoProcessing_nativeApplyAnsel(JNIEnv* env, jobject thiz, jint value) {
	applyAnselFilter(&bitmap,value);
}

void Java_org_fossasia_phimpme_editor_editimage_filter_PhotoProcessing_nativeApplyTestino(JNIEnv* env, jobject thiz, jint value) {
	applyTestino(&bitmap,value);
}

void Java_org_fossasia_phimpme_editor_editimage_filter_PhotoProcessing_nativeApplyXPro(JNIEnv* env, jobject thiz, jint value) {
	applyXPro(&bitmap,value);
}

void Java_org_fossasia_phimpme_editor_editimage_filter_PhotoProcessing_nativeApplyRetro(JNIEnv* env, jobject thiz, jint value) {
	applyRetro(&bitmap,value);
}

void Java_org_fossasia_phimpme_editor_editimage_filter_PhotoProcessing_nativeApplyBW(JNIEnv* env, jobject thiz, jint value) {
	applyBlackAndWhiteFilter(&bitmap, value);
}

void Java_org_fossasia_phimpme_editor_editimage_filter_PhotoProcessing_nativeApplySepia(JNIEnv* env, jobject thiz, jint value) {
	applySepia(&bitmap,value);
}

void Java_org_fossasia_phimpme_editor_editimage_filter_PhotoProcessing_nativeApplyCyano(JNIEnv* env, jobject thiz, jint value) {
	applyCyano(&bitmap,value);
}

void Java_org_fossasia_phimpme_editor_editimage_filter_PhotoProcessing_nativeApplyGeorgia(JNIEnv* env, jobject thiz, jint value) {
	applyGeorgia(&bitmap,value);
}

void Java_org_fossasia_phimpme_editor_editimage_filter_PhotoProcessing_nativeApplySahara(JNIEnv* env, jobject thiz, jint value) {
	applySahara(&bitmap,value);
}

void Java_org_fossasia_phimpme_editor_editimage_filter_PhotoProcessing_nativeApplyHDR(JNIEnv* env, jobject thiz, jint value) {
	applyHDR(&bitmap,value);
}

int Java_org_fossasia_phimpme_editor_editimage_filter_PhotoProcessing_nativeLoadResizedJpegBitmap(JNIEnv* env, jobject thiz, jbyteArray bytes, jint jpegSize, jint maxPixels) {
	char* jpegData = (char*) (*env)->GetPrimitiveArrayCritical(env, bytes, NULL);

	if (jpegData == NULL) {
		LOGE("jpeg data was null");
		return JNI_GET_INT_ARRAY_ERROR;
	}

	int resultCode = decodeJpegData(jpegData, jpegSize, maxPixels, &bitmap);
	if (resultCode != MEMORY_OK) {
		deleteBitmap(&bitmap);
		LOGE("error decoding jpeg resultCode=%d", resultCode);
		return resultCode;
	}

	(*env)->ReleasePrimitiveArrayCritical(env, bytes, jpegData, 0);

	return MEMORY_OK;
}

int Java_org_fossasia_phimpme_editor_editimage_filter_PhotoProcessing_nativeResizeBitmap(JNIEnv* env, jobject thiz, jint newWidth, jint newHeight) {
	unsigned char* newRed;
	int resultCode = newUnsignedCharArray(newWidth*newHeight, &newRed);
	if (resultCode != MEMORY_OK) {
		return resultCode;
	}
	resizeChannelBicubic(bitmap.red, bitmap.width, bitmap.height, newRed, (int)newWidth, (int)newHeight);
	freeUnsignedCharArray(&bitmap.red);
	bitmap.red = newRed;
	bitmap.redWidth = newWidth;
	bitmap.redHeight = newHeight;

	unsigned char* newGreen;
	resultCode = newUnsignedCharArray(newWidth*newHeight, &newGreen);
	if (resultCode != MEMORY_OK) {
		return resultCode;
	}
	resizeChannelBicubic(bitmap.green, bitmap.width, bitmap.height, newGreen, (int)newWidth, (int)newHeight);
	freeUnsignedCharArray(&bitmap.green);
	bitmap.green = newGreen;
	bitmap.greenWidth = newWidth;
	bitmap.greenHeight = newHeight;

	unsigned char* newBlue;
	resultCode = newUnsignedCharArray(newWidth*newHeight, &newBlue);
	if (resultCode != MEMORY_OK) {
		return resultCode;
	}
	resizeChannelBicubic(bitmap.blue, bitmap.width, bitmap.height, newBlue, (int)newWidth, (int)newHeight);
	freeUnsignedCharArray(&bitmap.blue);
	bitmap.blue = newBlue;
	bitmap.blueWidth = newWidth;
	bitmap.blueHeight = newHeight;

	bitmap.width = newWidth;
	bitmap.height = newHeight;
}



////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////tuning/////////////////////////////////////////////////////


void Java_org_fossasia_phimpme_editor_editimage_filter_PhotoProcessing_nativeTuneContrast(JNIEnv* env, jobject thiz, jint value) {
	tuneContrast(&bitmap, value);
}


void Java_org_fossasia_phimpme_editor_editimage_filter_PhotoProcessing_nativeTuneBrightness(JNIEnv* env, jobject thiz, jint value) {
	tuneBrightness(&bitmap, value);
}


void Java_org_fossasia_phimpme_editor_editimage_filter_PhotoProcessing_nativeTuneTemperature(JNIEnv* env, jobject thiz, jint value) {
	tuneTemperature(&bitmap, value);
}


void Java_org_fossasia_phimpme_editor_editimage_filter_PhotoProcessing_nativeTuneTint(JNIEnv* env, jobject thiz, jint value) {
	tuneTint(&bitmap, value);
}

void Java_org_fossasia_phimpme_editor_editimage_filter_PhotoProcessing_nativeTuneHue(JNIEnv* env, jobject thiz, jint value) {
	tuneHue(&bitmap, value);
}

void Java_org_fossasia_phimpme_editor_editimage_filter_PhotoProcessing_nativeTuneSaturation(JNIEnv* env, jobject thiz, jint value) {
	tuneSaturation(&bitmap, value);
}

void Java_org_fossasia_phimpme_editor_editimage_filter_PhotoProcessing_nativeTuneSharpen(JNIEnv* env, jobject thiz, jint value) {
	tuneSharpen(&bitmap, value);
}

void Java_org_fossasia_phimpme_editor_editimage_filter_PhotoProcessing_nativeTuneBlur(JNIEnv* env, jobject thiz, jint value) {
	tuneBlur(&bitmap, value);
}

void Java_org_fossasia_phimpme_editor_editimage_filter_PhotoProcessing_nativeTuneVignette(JNIEnv* env, jobject thiz, jint value) {
	tuneVignette(&bitmap, value);
}
