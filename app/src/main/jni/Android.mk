LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)

LOCAL_SRC_FILES:= VideoConvert.cpp

LOCAL_MODULE:= VideoConvert

include $(BUILD_SHARED_LIBRARY)