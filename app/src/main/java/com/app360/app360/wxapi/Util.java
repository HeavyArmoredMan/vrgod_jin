package com.app360.app360.wxapi;

import android.content.Context;
import android.content.SharedPreferences;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.example.android.ndk.Student;

import java.util.ArrayList;

public class Util {
    private Context mContext;
    private static Util sInstance;

    public static Util getsInstance(Context context) {
        synchronized (Util.class) {
            if (sInstance == null) {
                sInstance = new Util(context);
            }
        }
        return sInstance;
    }

    private Util(Context context) {
        System.loadLibrary("Hello");
        this.mContext = context;
    }

    public static native String helloFromC();

    public static native String hello_from_c();

    public  static native StringBuffer getCode(String imei, StringBuffer sb);

    public static native Student nativeGetStudent();

    public static native void nativeSetStudent(Student student);

    public static native void nativeGet();

    public static native boolean nativeSetStudent1(Student student);

    public static  native ArrayList<Student> nativeGetStudentByQuery(String name);

    public static boolean setStudent(Student student) {
        return nativeSetStudent1(student);
    }

    public static ArrayList<Student> getStudentByQuery(String name) {
        Log.i("jin", "ZgjEngine  getStudentByQuery");
        return nativeGetStudentByQuery(name);
    }

    public void saveInfo() {

        TelephonyManager telephonyManager = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
        String imei = telephonyManager.getDeviceId();
        Log.i("jin", "imei = " + imei);
        String info = getCode(imei, new StringBuffer()).toString();
        Log.i("jin", "info = " + info);
        SharedPreferences sharedPreferences = mContext.getSharedPreferences("app2vr", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("code", info);
        editor.apply();

    }


    public String getInfo() {
        String info;
        SharedPreferences sharedPreferences = mContext.getSharedPreferences("app2vr", Context.MODE_PRIVATE);
        info = sharedPreferences.getString("code", "");
        return info;
    }


    public boolean isHasInfo() {
        String info = getInfo();
        TelephonyManager telephonyManager = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
        String imei = telephonyManager.getDeviceId();
        Log.i("jin", "imei = " + imei);
        String infoCal = getCode(imei, new StringBuffer()).toString();
        return info.equals(infoCal);
    }
}
