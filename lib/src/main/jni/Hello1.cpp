#include<stdio.h>
#include<stdlib.h>
#include<jni.h>
#include<string.h>
#include <cassert>
#include <android/log.h>
#include <Student.h>
#include <list>
#define LOG_TAG "System.out.c"
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__)
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define JNIREG_CLASS "com/app360/app360/wxapi/Util"
using namespace std;
jobject  globalObj;
typedef list<Student*> ListStudent;
ListStudent listStudent;
  
/*
 jstring Java_com_example_android_ndk_MainActivity_helloFromC(JNIEnv* env ,jobject obj ){

 return  (*(*env)).NewStringUTF(env,"hello  from c.我是底层c代码库");
 }

 //带下划线的方法，后面加上1，否则编译器把他当做是内部类

 jstring Java_com_example_android_ndk_MainActivity_hello_1from_1c(JNIEnv* env ,jobject obj ){

 return  (*env)->NewStringUTF(env,"嘿嘿.我是底层c代码库_____ ");


 */

JNIEXPORT jstring JNICALL helloFromC(JNIEnv * env, jobject obj) {
	LOGI("来了helloFromC");
	return env->NewStringUTF("新的hello  from c.我是底层c代码库  动态注册");
}


JNIEXPORT jobject JNICALL getCode(JNIEnv * env, jobject obj,jobject imei, jobject stringBuilder) {
	LOGI("ddd 00000" );
	jclass stringClass = env->GetObjectClass(imei);
 LOGI("ddd 111111111111" );
    jmethodID subStringMethodId  = env->GetMethodID(stringClass,"substring","(II)Ljava/lang/String;");
    jobject newImei = env->CallObjectMethod(imei, subStringMethodId, 0 ,10);
	 LOGI("ddd 222222222" );
    jint *aa= new jint[10];//{7,4,6,3,1,8,9,2,5,0};
	aa[0] = 7;
	aa[1] = 4;
	aa[2] = 6;	
	aa[3] = 3;
	aa[4] = 1;
	aa[5] = 8;
	aa[6] = 9;
	aa[7] = 2;
	aa[8] = 5;
	aa[9] = 0;
	 LOGI("ddd 3333333333333333" );
	 jint *bb= new jint[10];
	
    //jmethodID lengthMethodId  = env->GetMethodID(stringClass,"length","()I");
	LOGI("ddd 3!!!!!!!!!!!!!!!" );
   // jint length = env->CallIntMethod(imei, lengthMethodId);
	 LOGI("ddd 3444444444444444444444" );

	
	
    jmethodID charAtMethodId = env->GetMethodID(stringClass,"charAt", "(I)C");
    for(jint i = 0;i < 10; i++){
		jint index = env->CallCharMethod(newImei, charAtMethodId, i) ;
		 LOGI("ddd et from java  !!! index  = %d",index );
    	bb[i] = aa[index - 48];
		
        LOGI("ddd et from java  !!! n  = %d",bb[i] );
    }
	
	
	jclass stringBuilderClass = env->GetObjectClass(stringBuilder);
   //第一种构造方法
	   jmethodID constructorID = env->GetMethodID(stringBuilderClass,"<init>", "()V");
   jobject stringBuilderObj = env->NewObject(stringBuilderClass,constructorID);
	

    jmethodID appendID = env->GetMethodID(stringBuilderClass,"append","(I)Ljava/lang/StringBuffer;"); 
	 
	for(jint i = 0;i < 10; i++){
		LOGI("ddd et from java  !!! bb[i]  =%d  ",bb[i]);
        env->CallObjectMethod(stringBuilderObj, appendID, bb[i]);
		LOGI("ddd 6666666666666 stringBuilderObj = %c ",  stringBuilderObj);
	}
	 
	return stringBuilderObj;
}
JNIEXPORT jstring JNICALL hello_1from_1c(JNIEnv *env, jobject obj) {
	LOGI("来了   hello_from_c");
	return env->NewStringUTF("新的.我是底层c代码库_____动态注册 ");

}

/*
 * Class:     com_example_android_ndk_MainActivity
 * Method:    nativeGetStudent
 * Signature: ()Lcom/example/android/ndk/Student;
 *
 * 第二个参数是调用者对象（非静态），或者调用者类（静态）
 * 也就是native函数所在的类的对象，或者类
 */
JNIEXPORT jobject JNICALL nativeGetStudent(JNIEnv * env, jobject obj) {
	LOGI("1111 nativeGetStudent");
   jclass studentClass = env->FindClass("com/example/android/ndk/Student");
   //第一种构造方法
   jobject studentObj = env->AllocObject(studentClass);
   //第二种构造方法
   jmethodID constructorID = env->GetMethodID(studentClass,"<init>", "()V");
   jobject studentObj1 = env->NewObject(studentClass,constructorID);

   jfieldID ageID = env->GetFieldID(studentClass, "age" , "I");
   jfieldID nameID = env->GetFieldID(studentClass, "name" , "Ljava/lang/String;");
   env->SetIntField(studentObj1 , ageID , 66);
   env->SetObjectField(studentObj1 ,nameID, (env)->NewStringUTF("laoke") );//char * 转 jstring
   return studentObj1;
}


void printStudentInfo(JNIEnv *env,  jobject student){
    jclass studentClass = env->GetObjectClass(student);
	jfieldID ageID = env->GetFieldID(studentClass, "age" , "I");
	jfieldID nameID = env->GetFieldID(studentClass, "name" , "Ljava/lang/String;");
	jint age = env->GetIntField(student, ageID);
	jstring name = (jstring)env->GetObjectField(student, nameID);
	const char * c_name = env->GetStringUTFChars(name ,NULL);//jstring 转换成 char *
	LOGI("et from java  !!! age = %d, name = %s",age,c_name);
}
/*
 * Class:     com_example_android_ndk_MainActivity
 * Method:    nativeSetStudent
 * Signature: (Lcom/example/android/ndk/Student;)V
 */
 void  nativeSetStudent(JNIEnv *env, jobject obj, jobject student) {
	jclass studentClass = env->GetObjectClass(student);
	jfieldID ageID = env->GetFieldID(studentClass, "age" , "I");
	jfieldID nameID = env->GetFieldID(studentClass, "name" , "Ljava/lang/String;");
	jint age = env->GetIntField(student, ageID);
	jstring name = (jstring)env->GetObjectField(student, nameID);
	const char * c_name = env->GetStringUTFChars(name ,NULL);//jstring 转换成 char *
	LOGI("age = %d",age);
	LOGI("name = %s",c_name);
}

  jobject getArraylist(JNIEnv *env){
      if(globalObj == NULL){

        jclass list_cls = env->FindClass("java/util/ArrayList");//获得ArrayList类引用

        jmethodID list_costruct = env->GetMethodID(list_cls , "<init>","()V"); //获得得构造函数Id

        jobject list_obj = env->NewObject(list_cls , list_costruct); //创建一个Arraylist集合对象

        //或得Arraylist类中的 add()方法ID，其方法原型为： boolean add(Object object) ;
        jmethodID list_add  = env->GetMethodID(list_cls,"add","(Ljava/lang/Object;)Z");

        globalObj = env->NewGlobalRef(list_obj);

    }
    return globalObj;
  }


 jboolean  nativeSetStudent1
  (JNIEnv *env, jobject mainActivity, jobject student){
      printStudentInfo(env,student);
      jobject arrayListObj = getArraylist(env);
      jclass list_cls = env->FindClass("java/util/ArrayList");//获得ArrayList类引用
      jmethodID list_add  = env->GetMethodID(list_cls,"add","(Ljava/lang/Object;)Z");
      jboolean flag = env->CallBooleanMethod(arrayListObj,list_add,student);

      //
      jclass studentClass = env->GetObjectClass(student);
      jfieldID ageID = env->GetFieldID(studentClass, "age" , "I");
      jfieldID nameID = env->GetFieldID(studentClass, "name" , "Ljava/lang/String;");
      jint age = env->GetIntField(student, ageID);
      jstring name = (jstring)env->GetObjectField(student, nameID);
      const char * c_name = env->GetStringUTFChars(name ,NULL);//jstring 转换成 char *
      Student *stu = new Student();
      stu->setAge(age);
      stu->setName(c_name);
	  listStudent.push_back(stu);
      LOGI("student.name = %s,age = %d",stu->getName(),stu->getAge());
	  ListStudent::iterator ite;
	  LOGI("--------------------------------------");
	  for(ite =listStudent.begin() ; ite != listStudent.end(); ite++  ){
		  Student* stu111 = (*ite);
		  LOGI("stu111.name = %s,age = %d",stu111->getName(),stu111->getAge());
	  }
      return flag;
  }


 jobject  convertStudentToJavaStudent(JNIEnv * env , Student * stu) {
	LOGI("1111 nativeGetStudent");
   jclass studentClass = env->FindClass("com/example/android/ndk/Student");
   //第一种构造方法
   jobject studentObj = env->AllocObject(studentClass);
   //第二种构造方法
   jmethodID constructorID = env->GetMethodID(studentClass,"<init>", "()V");
   jobject studentObj1 = env->NewObject(studentClass,constructorID);

   jfieldID ageID = env->GetFieldID(studentClass, "age" , "I");
   jfieldID nameID = env->GetFieldID(studentClass, "name" , "Ljava/lang/String;");
   env->SetIntField(studentObj1 , ageID , stu->getAge());
   env->SetObjectField(studentObj1 ,nameID, (env)->NewStringUTF(stu->getName()) );//char * 转 jstring
   return studentObj1;
}

 jobject  nativeGetStudentByQuery
(JNIEnv * env, jobject mainActivity, jstring queryStr){
    jobject arrayListObjAll = getArraylist(env);

    jclass list_cls = env->FindClass("java/util/ArrayList");//获得ArrayList类引用

    jmethodID list_costruct = env->GetMethodID(list_cls , "<init>","()V"); //获得得构造函数Id
    jmethodID list_size = env->GetMethodID(list_cls,"size","()I");
    jint studentsszie = env->CallIntMethod(arrayListObjAll,list_size);

    jobject list_obj = env->NewObject(list_cls , list_costruct); //创建一个Arraylist集合对象

    jmethodID list_add  = env->GetMethodID(list_cls,"add","(Ljava/lang/Object;)Z");
    jmethodID list_get  = env->GetMethodID(list_cls,"get","(I)Ljava/lang/Object;");

    for(jint i= 0 ; i <studentsszie; i++){
        jobject student = env->CallObjectMethod(arrayListObjAll,list_get,i);
    	jclass studentClass = env->GetObjectClass(student);
        jfieldID ageID = env->GetFieldID(studentClass, "age" , "I");
        jfieldID nameID = env->GetFieldID(studentClass, "name" , "Ljava/lang/String;");
        jint age = env->GetIntField(student, ageID);
        jstring name = (jstring)env->GetObjectField(student, nameID);
        const char * c_name = env->GetStringUTFChars(name ,NULL);//jstring 转换成 char *
        const char * c_query = env->GetStringUTFChars(queryStr, NULL);
        LOGI("c_name = %s, c_query = %s",c_name,c_query);
        if(strstr(c_name, c_query)){
           //env->CallBooleanMethod(list_obj,list_add,student);
        }
    }
	ListStudent::iterator ite;
	LOGI("--------------------------------------");
    const char * c_query = env->GetStringUTFChars(queryStr, NULL);
	for (ite = listStudent.begin(); ite != listStudent.end(); ite++) {
		Student* stu111 = (*ite);
		if(strstr(stu111->getName(), c_query) && c_query != NULL && *c_query != 0){
			env->CallBooleanMethod(list_obj,list_add,convertStudentToJavaStudent(env , stu111));
			LOGI("C++  stu111.name = %s,age = %d", stu111->getName(), stu111->getAge());
		}else{
			LOGI("query  null !!!");
		}

	}
    return list_obj;

}





/*
 * Class:     com_example_android_ndk_MainActivity
 * Method:    nativeGet
 * Signature: ()V
 */
 void  nativeGet(JNIEnv * env, jclass clazz) {

}
/*
 * JNI registration.
 */
static JNINativeMethod gMethods[] = {
/* name, signature, funcPtr */
{ "helloFromC", "()Ljava/lang/String;", (void*) helloFromC },
{ "getCode", "(Ljava/lang/String;Ljava/lang/StringBuffer;)Ljava/lang/StringBuffer;", (void*) getCode },
{ "hello_from_c","()Ljava/lang/String;", (void*) hello_1from_1c },
{"nativeGetStudent","()Lcom/example/android/ndk/Student;" , (void*)nativeGetStudent},
{"nativeSetStudent", "(Lcom/example/android/ndk/Student;)V" , (void*)nativeSetStudent},
{"nativeGet","()V",(void*)nativeGet},
{"nativeSetStudent1","(Lcom/example/android/ndk/Student;)Z" , (void*)nativeSetStudent1},
{"nativeGetStudentByQuery","(Ljava/lang/String;)Ljava/util/ArrayList;" , (void*)nativeGetStudentByQuery}
};



/*
 * Register several native methods for one class.
 */
static int registerNativeMethods(JNIEnv* env, const char* className,
		JNINativeMethod* gMethods, int numMethods) {
	jclass clazz;
	LOGI("来了   registerNativeMethods 111");
	clazz = env->FindClass(className);
    LOGI("来了   registerNativeMethods 222");
	if (clazz == NULL) {
     	LOGI("来了   registerNativeMethods 333");
		return JNI_FALSE;
	}
	 LOGI("来了   registerNativeMethods 444");
	if (env->RegisterNatives(clazz, gMethods, numMethods) < 0) {
		LOGI("来了   registerNativeMethods 555");
		return JNI_FALSE;
	}
	LOGI("来了   registerNativeMethods 666");
	return JNI_TRUE;
}

/*
 * Register native methods for all classes we know about.
 */
static int registerNatives(JNIEnv* env) {
	if (!registerNativeMethods(env, JNIREG_CLASS, gMethods,
			sizeof(gMethods) / sizeof(gMethods[0])))
		return JNI_FALSE;

	return JNI_TRUE;
}

/*
 * Set some test stuff up.
 *
 * Returns the JNI version on success, -1 on failure.
 */
 jint  JNI_OnLoad(JavaVM* vm, void* reserved) {
	 LOGI("来了   JNI_OnLoad 111");
	JNIEnv* env = NULL;
	jint result = -1;
	LOGI("来了   JNI_OnLoad 222");
	if (vm->GetEnv((void**) &env, JNI_VERSION_1_4) != JNI_OK) {
		return -1;
	}
	LOGI("来了   JNI_OnLoad  333 ");
	assert(env != NULL);

	if (!registerNatives(env)) { //注册
		return -1;
	}
	LOGI("来了   JNI_OnLoad 444 ");
	/* success -- return valid version number */
	result = JNI_VERSION_1_4;
	LOGI("来了   JNI_OnLoad  555");
	return result;
}

