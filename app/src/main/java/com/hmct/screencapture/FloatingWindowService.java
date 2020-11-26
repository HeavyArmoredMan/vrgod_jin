package com.hmct.screencapture;

import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.IBinder;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

public class FloatingWindowService extends Service {
  private static Context mContext;


  @Override
  public IBinder onBind(Intent intent) {
    return null;
  }

  @Override
  public void onCreate() {
    super.onCreate();
    mContext = getApplicationContext();

    Notification notification = new Notification(0, null,
        System.currentTimeMillis());

    //startForeground(100, notification);
  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    // TODO Auto-generated method stub
      if (intent == null) {
          return super.onStartCommand(intent, flags, startId);
      }
    final String action = intent.getAction();
    if ("com.hmct.vrmode.off".equals(action)) {
      ScreenCaptureFragment.stopScreenCapture();
      ScreenCaptureFragment.tearDownMediaProjection();
      try {
        WindowManager wm = (WindowManager) getApplicationContext()
            .getSystemService(Context.WINDOW_SERVICE);
        wm.removeView(ScreenCaptureFragment.mView1);
      } catch (Exception e) {
      }
      FloatingWindowService.this.stopSelf();
    }
    if ("com.hmct.vrmode.on".equals(action)) {
      if (ScreenCaptureFragment.mView1 != null) {
        Toast.makeText(this, "please retry", Toast.LENGTH_LONG).show();
        System.exit(0);
      }
      Intent i = new Intent();
      i.setClass(this, VRMainActivity.class);
      i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
      startActivity(i);
    }
    return super.onStartCommand(intent, flags, startId);
  }

  @Override
  public void onDestroy() {
    //Settings.Secure.putInt(getContentResolver(),
    //       Settings.Secure.ACCESSIBILITY_DISPLAY_MAGNIFICATION_ENABLED, 0);
    //Settings.System.putInt(getContentResolver(), "vrmode", 0);
    WindowManager wm = (WindowManager) getApplicationContext()
        .getSystemService(Context.WINDOW_SERVICE);
    try {
      wm.removeView(ScreenCaptureFragment.mView1);
    } catch (Exception e) {
    }
    super.onDestroy();
  }

  @Override
  public void onConfigurationChanged(Configuration newConfig) {
    Log.v("vr", "111newConfig.orientation = " + newConfig.orientation);

    if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
      ScreenCaptureFragment.stopScreenCapture();
      ScreenCaptureFragment.tearDownMediaProjection();
      try {
        WindowManager wm = (WindowManager) getApplicationContext()
            .getSystemService(Context.WINDOW_SERVICE);
        wm.removeView(ScreenCaptureFragment.mView1);
      } catch (Exception e) {
      }
      stopSelf();
    }
  }
}
