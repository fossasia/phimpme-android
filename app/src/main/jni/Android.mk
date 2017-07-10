LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

OPENCV_INSTALL_MODULES:=on
OPENCV_CAMERA_MODULES:=on
OPENCV_LIB_TYPE := STATIC
include $(LOCAL_PATH)/OpenCV.mk

LOCAL_MODULE    := photoprocessing

LOCAL_CFLAGS := -DANDROID_NDK \
                -DDISABLE_IMPORTGL

LOCAL_SRC_FILES := main_processing.cpp bicubic_resize.c bitmap.c blur.c colour_space.c filter.c matrix.c mem_utils.c nanojpeg.c photo_processing.c transform.c tune.c
LOCAL_C_INCLUDES := $(LOCAL_PATH)/include
LOCAL_LDLIBS    += -lm -llog

include $(BUILD_SHARED_LIBRARY)