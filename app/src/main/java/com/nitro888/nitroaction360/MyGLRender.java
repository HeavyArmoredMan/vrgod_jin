package com.nitro888.nitroaction360;
import java.io.File;
import java.io.IOException;
import java.util.Random;

import javax.microedition.khronos.opengles.GL10;


 
import android.content.Context;
import android.opengl.GLSurfaceView.Renderer;
import android.opengl.GLES20;
import android.opengl.GLU;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
 

public class MyGLRender implements Renderer {
	
	private GLBitmap glBitmap;  
	int mFrameCount = 0;
	
    Context context; // Application's context
    Random r = new Random();
    //private Square square;
    private int width = 0;
    private int height = 0;
    private long frameSeq = 0;
    private int viewportOffset = 0;
    private int maxOffset = 400;
    
    
    private static final int TEST_R0 = 0;
    private static final int TEST_G0 = 136;
    private static final int TEST_B0 = 0;
    private static final int TEST_R1 = 236;
    private static final int TEST_G1 = 50;
    private static final int TEST_B1 = 186;
 
    public MyGLRender(Context context) {
        this.context = context;
        glBitmap = new GLBitmap();  
    }
 
    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        if (height == 0) { // Prevent A Divide By Zero By
            height = 1; // Making Height Equal One
        }
        this.width = width;
        this.height = height;
        gl.glViewport(0, 0, width, height); // Reset The
                                            // Current
        // Viewport
        gl.glMatrixMode(GL10.GL_PROJECTION); // Select The Projection Matrix
        gl.glLoadIdentity(); // Reset The Projection Matrix
 
        // Calculate The Aspect Ratio Of The Window
        GLU.gluPerspective(gl, 45.0f, (float) width / (float) height, 0.1f,
                100.0f);
 
        gl.glMatrixMode(GL10.GL_MODELVIEW); // Select The Modelview Matrix
        gl.glLoadIdentity();
    }
 

    @Override
    public void onDrawFrame(GL10 gl) {
    	
    	//Log.i("gl","c" + mFrameCount);
    	
    	//GLRecoder.beginDraw();
    	//GLRecoder.beginDraw();
    	gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);  
        // Reset the Modelview Matrix  
        gl.glLoadIdentity();  
        gl.glTranslatef(0.0f, 0.0f, -5.0f); // move 5 units INTO the screen is  
                                            // the same as moving the camera 5  
                                            // units away  
        // square.draw(gl);  
        glBitmap.draw(gl);  
        changeGLViewport(gl);  
        if(true)return;
        
    	
    	int frameIndex = mFrameCount ++;
    	int mWidth = 400;
    	int mHeight = 400;
    	
    	frameIndex %= 8;
        
        int startX, startY;
        if (frameIndex < 4) {
            // (0,0) is bottom-left in GL
            startX = frameIndex * (mWidth / 4);
            startY = mHeight / 2;
        } else {
            startX = (7 - frameIndex) * (mWidth / 4);
            startY = 0;
        }

        GLES20.glClearColor(TEST_R0 / 255.0f, TEST_G0 / 255.0f, TEST_B0 / 255.0f, 1.0f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

        GLES20.glEnable(GLES20.GL_SCISSOR_TEST);
        GLES20.glScissor(startX, startY, mWidth / 4, mHeight / 2);
        GLES20.glClearColor(TEST_R1 / 255.0f, TEST_G1 / 255.0f, TEST_B1 / 255.0f, 1.0f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        GLES20.glDisable(GLES20.GL_SCISSOR_TEST);
        
        
        //GLRecoder.endDraw();
       
    }

    private void changeGLViewport(GL10 gl) {
        System.out.println("time=" + System.currentTimeMillis());
        frameSeq++;
        viewportOffset++;
        // The
        // Current
        if (frameSeq % 100 == 0) {// 每隔100帧，重置
            gl.glViewport(0, 0, width, height);
            viewportOffset = 0;
        } else {
            int k = 4;
            gl.glViewport(-maxOffset + viewportOffset * k, -maxOffset
                    + viewportOffset * k, this.width - viewportOffset * 2 * k
                    + maxOffset * 2, this.height - viewportOffset * 2 * k
                    + maxOffset * 2);
        }
    }
 
    @Override
    public void onSurfaceCreated(GL10 gl,
            javax.microedition.khronos.egl.EGLConfig arg1) {
    	glBitmap.loadGLTexture(gl, this.context);  
    	   
        gl.glEnable(GL10.GL_TEXTURE_2D); // Enable Texture Mapping ( NEW )  
        gl.glShadeModel(GL10.GL_SMOOTH); // Enable Smooth Shading  
        gl.glClearColor(0.0f, 0.0f, 0.0f, 0.5f); // Black Background  
        gl.glClearDepthf(1.0f); // Depth Buffer Setup  
        gl.glEnable(GL10.GL_DEPTH_TEST); // Enables Depth Testing  
        gl.glDepthFunc(GL10.GL_LEQUAL); // The Type Of Depth Testing To Do  
   
        gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST);  
    }
}