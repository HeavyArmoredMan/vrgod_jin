package com.hmct.event;
import android.view.MotionEvent;  
import android.view.KeyEvent;  
interface callback {     
    void dispatchMotionEvent( in MotionEvent motionevent); 
    void dispatchKeyEvent( in KeyEvent keyevent); 
}    