LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE    := photoprocessing

LOCAL_CFLAGS := -DANDROID_NDK \
                -DDISABLE_IMPORTGL
                
LOCAL_SRC_FILES := bicubic_resize.c bitmap.c blur.c colour_space.c filter.c matrix.c mem_utils.c nanojpeg.c photo_processing.c transform.c tune.c
LOCAL_LDLIBS    := -lm -llog

include $(BUILD_SHARED_LIBRARY)