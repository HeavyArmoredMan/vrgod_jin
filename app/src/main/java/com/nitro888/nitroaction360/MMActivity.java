package com.nitro888.nitroaction360;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;


import android.app.Activity;
import android.app.Presentation;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.drawable.ColorDrawable;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.Image;
import android.media.ImageReader;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLSurfaceView.Renderer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.TextureView;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.Toast;

public class MMActivity extends Activity {
	public static GLSurfaceView glView; // Use GLSurfaceView
    Random rnd = new Random();
    MyGLRender mRender = new MyGLRender(this);
    
   
    public class MyInvocationHandler implements InvocationHandler {  
       
        Object target;
        private EGLConfig mEGLConfig;
        int frameount = 0;
        MyInvocationHandler(Object t ) {  
            super();  
            target = t;
        }  
      
        @Override  
        public Object invoke(Object o, Method method, Object[] args) throws Throwable {  
        	Log.i("gl","++++++after " + method.getName() + "++++++");  
        	if("onSurfaceCreated".equals(method.getName())){  
        		mEGLConfig = (EGLConfig) args[1];
        		
        		 method.invoke(target,args);  
        	}
        	if("onSurfaceChanged".equals(method.getName())){  
        		//GLRecorder.init(720, 1080, mEGLConfig);
        		//GLRecorder.setRecordOutputFile("/sdcard/glrecord.mp4");  
        		//GLRecorder.startRecording();
        		 method.invoke(target,args);  
        	}
            if("onDrawFrame".equals(method.getName())){  
            	frameount ++;
                //GLRecorder.beginDraw();
            	//GLES20.glViewport(0, 0, 100, 100);
                Object result = method.invoke(target,args);  
                //GLRecorder.endDraw();
                
                if(frameount > 1000){
                	//GLRecorder.stopRecording();
                }
                return result;  
            }else{  
                Object result = method.invoke(target,args);  
                return result;  
            }  
      
        }  
    }  
    

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO 自动生成的方法存�?
		super.onCreate(savedInstanceState);
		setTheme(android.R.style.Theme_Translucent_NoTitleBar);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        glView = new GLSurfaceView(this); // Allocate a GLSurfaceView
        
        
        GLSurfaceView.Renderer rd = mRender;
		InvocationHandler invocationHandler = new MyInvocationHandler(rd);  
		GLSurfaceView.Renderer rdset = (GLSurfaceView.Renderer)Proxy.newProxyInstance(rd.getClass().getClassLoader(),  
                rd.getClass().getInterfaces(), invocationHandler);  
		glView.setRenderer(rdset); // Use a custom renderer
        glView.getAlpha();
        glView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
        this.setContentView(glView); 
        
        
	}

	@Override
	protected void onResume() {
		// TODO
		super.onResume();
		glView.onResume();

	}

	@Override
	protected void onDestroy() {
		// TODO
		
		super.onDestroy();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		
		Intent myIntent = new Intent();
		myIntent.setClass(MMActivity.this, MainActivity.class);
		myIntent.putExtra("type", "vr_mode");
		startActivity(myIntent);
		
		return super.onKeyDown(keyCode, event);
	}

}
