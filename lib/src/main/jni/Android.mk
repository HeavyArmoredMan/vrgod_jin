LOCAL_PATH := $(call my-dir)
MAIN_LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)
LOCAL_MODULE := va++

LOCAL_CFLAGS := -Wno-error=format-security -fpermissive -DLOG_TAG=\"VA++\"
LOCAL_CFLAGS += -fno-rtti -fno-exceptions

LOCAL_C_INCLUDES += $(MAIN_LOCAL_PATH)
LOCAL_C_INCLUDES += $(MAIN_LOCAL_PATH)/Foundation
LOCAL_C_INCLUDES += $(MAIN_LOCAL_PATH)/Jni

LOCAL_SRC_FILES := Jni/VAJni.cpp \
				   Foundation/IOUniformer.cpp \
				   Foundation/VMPatch.cpp \
				   Foundation/SymbolFinder.cpp \
				   Foundation/Path.cpp \
				   Foundation/SandboxFs.cpp \
				   Substrate/hde64.c \
                   Substrate/SubstrateDebug.cpp \
                   Substrate/SubstrateHook.cpp \
                   Substrate/SubstratePosixMemory.cpp \

LOCAL_LDLIBS := -llog -latomic
LOCAL_STATIC_LIBRARIES := fb

include $(BUILD_SHARED_LIBRARY)


  #变量的初始化操作  特点 不会重新初始化LOCAL_PATH的变量
   include $(CLEAR_VARS)

#libhello.so   加上lib前缀， .so后缀 都是makefile的语法约定的
   LOCAL_MODULE    := Hello
   LOCAL_SRC_FILES := Hello1.cpp \
                      Student.cpp
 #c语言的log日志库
   LOCAL_LDLIBS += -llog
   include $(BUILD_SHARED_LIBRARY)


include $(MAIN_LOCAL_PATH)/fb/Android.mk