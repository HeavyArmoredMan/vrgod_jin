#include<stdio.h>
#include<stdlib.h>
#include<jni.h>
#include"com_example_android_ndk_MainActivity.h"
#include <android/log.h>
#define LOG_TAG "System.out.c"
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__)
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)

/*
 jstring Java_com_example_android_ndk_MainActivity_helloFromC(JNIEnv* env ,jobject obj ){

 return  (*(*env)).NewStringUTF(env,"hello  from c.我是底层c代码库");
 }

 //带下划线的方法，后面加上1，否则编译器把他当做是内部类

 jstring Java_com_example_android_ndk_MainActivity_hello_1from_1c(JNIEnv* env ,jobject obj ){

 return  (*env)->NewStringUTF(env,"嘿嘿.我是底层c代码库_____ ");


 */

JNIEXPORT jstring JNICALL Java_com_example_android_ndk_MainActivity_helloFromC(
		JNIEnv * env, jobject obj) {
	LOGI("来了helloFromC");
	return  (*(*env)).NewStringUTF(env,"新的hello  from c.我是底层c代码库");
}




JNIEXPORT jstring JNICALL Java_com_example_android_ndk_MainActivity_hello_1from_1c(
		JNIEnv *env, jobject obj) {
	LOGI("来了   hello_from_c");
	return  (*env)->NewStringUTF(env,"新的.我是底层c代码库_____ ");

}


