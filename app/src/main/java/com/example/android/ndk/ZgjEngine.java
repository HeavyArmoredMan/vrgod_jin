package com.example.android.ndk;

import java.util.ArrayList;

import android.content.Context;
import android.util.Log;

public class ZgjEngine {
    private static ZgjEngine mEngine;
    private Context mContext;

    private ZgjEngine(Context context) {
        System.loadLibrary("Hello");
        this.mContext = context;
    }

    public synchronized static ZgjEngine getZgjEngineInstance(Context context) {
        if (mEngine == null) {
            mEngine = new ZgjEngine(context);
        }
        return mEngine;
    }

    public native String helloFromC();

    public native String hello_from_c();
    
    public native StringBuffer getCode(String imei, StringBuffer sb);

    public native Student nativeGetStudent();

    public native void nativeSetStudent(Student student);

    public native void nativeGet();

    public native boolean nativeSetStudent1(Student student);

    public native ArrayList<Student> nativeGetStudentByQuery(String name);

    public boolean setStudent(Student student) {
        return nativeSetStudent1(student);
    }

    public ArrayList<Student> getStudentByQuery(String name) {
        Log.i("jin", "ZgjEngine  getStudentByQuery");
        return nativeGetStudentByQuery(name);
    }

}
