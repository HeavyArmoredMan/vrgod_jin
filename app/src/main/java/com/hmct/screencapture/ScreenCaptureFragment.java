/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hmct.screencapture;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import android.view.MotionEvent;
import android.util.Log;
import android.widget.LinearLayout;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.graphics.PixelFormat;
import android.view.Gravity;
import android.widget.FrameLayout;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.app360.app360.R;

/**
 * Provides UI for the screen capture.
 */
public class ScreenCaptureFragment extends Fragment implements
    View.OnClickListener {

  private static final String TAG = "ScreenCaptureFragment";

  private static final String STATE_RESULT_CODE = "result_code";
  private static final String STATE_RESULT_DATA = "result_data";

  private static final int REQUEST_MEDIA_PROJECTION = 1;

  private int mScreenDensity;

  private int mResultCode;
  private Intent mResultData;
  private LinearLayout mView;
  public static LinearLayout mView1;
  private Surface mSurface;
  public static MediaProjection mMediaProjection;
  public static VirtualDisplay mVirtualDisplay;
  private MediaProjectionManager mMediaProjectionManager;
  public static Button mButtonToggle;
  private SurfaceView mSurfaceView;
  private static WindowManager wm;
  private static WindowManager.LayoutParams params;

  public int getStatusBarHeight() {
    int result = 0;
    int resourceId = getResources().getIdentifier("status_bar_height",
        "dimen", "android");
    if (resourceId > 0) {
      result = getResources().getDimensionPixelSize(resourceId);
    }
    return result;
  }

  private int getNavigationBarHeight() {
    Resources resources = getResources();
    int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
    int height = resources.getDimensionPixelSize(resourceId);
    Log.v("dbw", "Navi height:" + height);
    return height;
  }

  public static boolean checkDeviceHasNavigationBarAgain(Context activity) {

    boolean hasMenuKey = ViewConfiguration.get(activity)
        .hasPermanentMenuKey();
    boolean hasBackKey = KeyCharacterMap
        .deviceHasKey(KeyEvent.KEYCODE_BACK);

    if (!hasMenuKey && !hasBackKey) {
      return true;
    }
    return false;
  }

  public static boolean checkDeviceHasNavigationBarOld(Context context) {
    boolean hasNavigationBar = false;
    Resources rs = context.getResources();
    int id = rs.getIdentifier("config_showNavigationBar", "bool", "android");
    if (id > 0) {
      hasNavigationBar = rs.getBoolean(id);
      if (hasNavigationBar) {
        hasNavigationBar = checkDeviceHasNavigationBarAgain(context);
      }

    }
    try {
      Class systemPropertiesClass = Class.forName("android.os.SystemProperties");
      Method m = systemPropertiesClass.getMethod("get", String.class);
      String navBarOverride = (String) m.invoke(systemPropertiesClass, "qemu.hw.mainkeys");
      if ("1".equals(navBarOverride)) {
        hasNavigationBar = false;
      } else if ("0".equals(navBarOverride)) {
        hasNavigationBar = true;
      }
    } catch (Exception e) {

    }
    return hasNavigationBar;

  }

  private boolean checkDeviceHasNavigationBar(Context context) {
    WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
    Display d = windowManager.getDefaultDisplay();


    DisplayMetrics realDisplayMetrics = new DisplayMetrics();
    d.getRealMetrics(realDisplayMetrics);


    int realHeight = realDisplayMetrics.heightPixels;
    int realWidth = realDisplayMetrics.widthPixels;


    DisplayMetrics displayMetrics = new DisplayMetrics();
    d.getMetrics(displayMetrics);


    int displayHeight = displayMetrics.heightPixels;
    int displayWidth = displayMetrics.widthPixels;


    return (realWidth - displayWidth) > 0 || (realHeight - displayHeight) > 0;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    if (savedInstanceState != null) {
      mResultCode = savedInstanceState.getInt(STATE_RESULT_CODE);
      mResultData = savedInstanceState.getParcelable(STATE_RESULT_DATA);
    }
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_screen_capture, container,
        false);
  }

  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {
    mSurfaceView = (SurfaceView) view.findViewById(R.id.surface);
    mView = (LinearLayout) view.findViewById(R.id.LinearLayout);
    mView1 = (LinearLayout) view.findViewById(R.id.FrameLayout);
    mView.removeView(mView1);
    mSurface = mSurfaceView.getHolder().getSurface();
    mButtonToggle = (Button) view.findViewById(R.id.toggle);
    mButtonToggle.setOnClickListener(this);
    mButtonToggle.setVisibility(View.INVISIBLE);

    LinearLayout.LayoutParams lp =
        (android.widget.LinearLayout.LayoutParams) mSurfaceView.getLayoutParams();
    lp.width = getResources().getDisplayMetrics().widthPixels;
    lp.height = getResources().getDisplayMetrics().heightPixels;
    lp.leftMargin = lp.width / 2;

    mSurfaceView.setLayoutParams(lp);
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    Activity activity = getActivity();
    DisplayMetrics metrics = new DisplayMetrics();
    activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
    mScreenDensity = metrics.densityDpi;
    mMediaProjectionManager = (MediaProjectionManager) activity
        .getSystemService("media_projection");
    onClick(mButtonToggle);
  }

  @Override
  public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    if (mResultData != null) {
      outState.putInt(STATE_RESULT_CODE, mResultCode);
      outState.putParcelable(STATE_RESULT_DATA, mResultData);
    }
  }

  @Override
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.toggle:
        if (mVirtualDisplay == null) {
          startScreenCapture();
          wm = (WindowManager) getActivity().getApplicationContext()
              .getSystemService(Context.WINDOW_SERVICE);
          params = new WindowManager.LayoutParams();
          params.type = LayoutParams.TYPE_SYSTEM_ALERT;
          params.format = PixelFormat.RGBA_8888;
          params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
              | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
          params.x = getResources().getDisplayMetrics().widthPixels / 2 - getNavigationBarHeight();
          params.y = 0;
          params.width = WindowManager.LayoutParams.WRAP_CONTENT;
          params.height = WindowManager.LayoutParams.WRAP_CONTENT;
          LinearLayout.LayoutParams mParams = new LinearLayout.LayoutParams(
              getResources().getDisplayMetrics().widthPixels +
                  (checkDeviceHasNavigationBar(getActivity()) ? getNavigationBarHeight() : 0),
              getResources().getDisplayMetrics().heightPixels + getStatusBarHeight());
          Log.i("VR", "checkDeviceHasNavigationBar" + checkDeviceHasNavigationBar(getActivity()));
          mParams.setMargins(getResources().getDisplayMetrics().widthPixels / 2,
              0 - getStatusBarHeight() - getStatusBarHeight() / 2, 0, 0);

          mSurfaceView.setLayoutParams(mParams);
          wm.addView(mView1, params);
        } else {
          stopScreenCapture();
        }
        break;
    }
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (requestCode == REQUEST_MEDIA_PROJECTION) {
      if (resultCode != Activity.RESULT_OK) {
        Log.i(TAG, "User cancelled");
        ScreenCaptureFragment.this.getActivity().finish();

        return;
      }
      Activity activity = getActivity();
      if (activity == null) {
        return;
      }
      Log.i(TAG, "Starting screen capture");
      mResultCode = resultCode;
      mResultData = data;
      setUpMediaProjection();
      setUpVirtualDisplay();
    }
  }

  @Override
  public void onPause() {
    super.onPause();
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
  }

  private void setUpMediaProjection() {
    mMediaProjection = mMediaProjectionManager.getMediaProjection(
        mResultCode, mResultData);
  }

  public static void tearDownMediaProjection() {
    if (mMediaProjection != null) {
      mMediaProjection.stop();
      mMediaProjection = null;
    }
  }

  private void startScreenCapture() {
    Activity activity = getActivity();
    if (mSurface == null || activity == null) {
      return;
    }
    if (mMediaProjection != null) {
      setUpVirtualDisplay();
    } else if (mResultCode != 0 && mResultData != null) {
      setUpMediaProjection();
      setUpVirtualDisplay();
    } else {
      Log.i(TAG, "Requesting confirmation");
      // This initiates a prompt dialog for the user to confirm screen
      // projection.
      startActivityForResult(
          mMediaProjectionManager.createScreenCaptureIntent(),
          REQUEST_MEDIA_PROJECTION);
    }
  }

  private void setUpVirtualDisplay() {
    Log.i(TAG, "Setting up a VirtualDisplay: " + mSurfaceView.getWidth()
        + "x" + mSurfaceView.getHeight() + " (" + mScreenDensity + ")");
    mVirtualDisplay = mMediaProjection.createVirtualDisplay(
        "ScreenCapture", mSurfaceView.getWidth(),
        mSurfaceView.getHeight(), mScreenDensity,
        1 << 4, mSurface,
        null, null);
    Intent mIntent = new Intent(ScreenCaptureFragment.this.getActivity(),
        FloatingWindowService.class);
    ScreenCaptureFragment.this.getActivity().startService(mIntent);
    // ScreenCaptureFragment.this.getActivity().moveTaskToBack(false);
    ScreenCaptureFragment.this.getActivity().finish();
  }

  @SuppressLint("NewApi")
  public static void stopScreenCapture() {
    if (mVirtualDisplay == null) {
      return;
    }
    mVirtualDisplay.release();
    mVirtualDisplay = null;

  }

  private String getProductName() {
    String name = "";
    try {
      Class<?> c = Class.forName("android.os.SystemProperties");
      Method get = c.getMethod("get", String.class, String.class);
      name = (String) (get.invoke(c, "ro.product.model", "no message"));
    } catch (ClassNotFoundException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (NoSuchMethodException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (IllegalArgumentException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (IllegalAccessException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (InvocationTargetException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return name;
  }

}
