package com.nitro888.nitroaction360;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.view.KeyEvent;
import android.view.MotionEvent;

import com.hmct.event.callback;
import com.hmct.event.event;

public class KeyDispatchService extends Service {
	
	static callback mCallBack;

	
	public static void dispatchMotionEvent(MotionEvent me){
		if(mCallBack != null){
			try {
				mCallBack.dispatchMotionEvent(me);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	}
	public static void dispatchKeyEvent(KeyEvent me){
		if(mCallBack != null){
			try {
				mCallBack.dispatchKeyEvent(me);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
    public IBinder onBind(Intent intent) {
        return (IBinder) new MyBinder();
    }
    private class MyBinder extends event.Stub{

		@Override
		public void regCallback(callback cb) throws RemoteException {
			mCallBack = cb;
		}

    }

}
