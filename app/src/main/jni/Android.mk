LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)

OPENCV_INSTALL_MODULES:=on
OPENCV_CAMERA_MODULES:=off
OPENCV_LIB_TYPE := STATIC
include $(LOCAL_PATH)/OpenCV.mk

LOCAL_MODULE    := nativeimageprocessing

LOCAL_CFLAGS := -DANDROID_NDK \
                -DDISABLE_IMPORTGL

LOCAL_SRC_FILES := main_processing.cpp enhance.cpp filters.cpp colour_space.cpp
LOCAL_C_INCLUDES := $(LOCAL_PATH)/include
LOCAL_LDLIBS    += -lm -llog

include $(BUILD_SHARED_LIBRARY)