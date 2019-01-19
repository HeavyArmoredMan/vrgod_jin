package com.lody.virtual.client.hook.delegate;

import android.app.Activity;
import android.app.Application;
import android.app.Instrumentation;
import android.app.Presentation;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.InputEvent;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.lody.virtual.client.VClientImpl;
import com.lody.virtual.client.core.VirtualCore;
import com.lody.virtual.client.fixer.ActivityFixer;
import com.lody.virtual.client.fixer.ContextFixer;
import com.lody.virtual.client.interfaces.IInjector;
import com.lody.virtual.client.ipc.ActivityClientRecord;
import com.lody.virtual.client.ipc.VActivityManager;
import com.lody.virtual.helper.compat.BundleCompat;
import com.lody.virtual.os.VUserHandle;
import com.lody.virtual.server.interfaces.IUiCallback;
import com.hmct.event.callback;
import com.hmct.event.event;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.Properties;

import mirror.android.app.ActivityThread;

import static mirror.android.app.job.JobParameters.callback;


final class VRParamManager {
    public static int getRealWidth(Activity activity) {
        int w = activity.getResources().getDisplayMetrics().widthPixels;
        int h = activity.getResources().getDisplayMetrics().heightPixels;
        int width = w > h ? w : h;
        int height = w > h ? h : w;
        int virtual_width = !activity.getPackageName().contains("gifmaker") ? width : height;
        return virtual_width;
    }

    public static int getRealHeight(Activity activity) {
        int w = activity.getResources().getDisplayMetrics().widthPixels;
        int h = activity.getResources().getDisplayMetrics().heightPixels;
        int width = w > h ? w : h;
        int height = w > h ? h : w;
        int virtual_height = !activity.getPackageName().contains("gifmaker") ? height : width;
        return virtual_height;
    }
}

final class ActivityCyleInfo {
    BroadcastReceiver receiver;
}

final class TestPresentation extends Presentation {
    private final int mColor;
    private final int mWindowType;
    private final int mWindowFlags;
    private View glView;
    private int index;

    public TestPresentation(Context context, Display display,
                            int color, int windowType, int windowFlags, View v) {
        super(context, display);
        mColor = color;
        mWindowType = windowType;
        mWindowFlags = windowFlags;
        glView = v;
    }

    public SurfaceView findSurfaceView(ViewGroup vg, String viewname) {
        for (int i = 0; i < vg.getChildCount(); i++) {
            View v = vg.getChildAt(i);
            if (v instanceof ViewGroup) {
                return findSurfaceView((ViewGroup) v, viewname);
            } else if (v instanceof SurfaceView && v.toString().contains(viewname)) {
                return (SurfaceView) v;
            }
        }
        return null;
    }

    //@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setType(mWindowType);
        getWindow().addFlags(mWindowFlags);
        //if(true)return;
        // Create a solid color image to use as the content of the presentation.
        //ImageView view = new ImageView(getContext());
        ViewGroup parent = (ViewGroup) glView.getParent();
        parent.removeView(glView);

        glView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        setContentView(glView);

        SurfaceView sv = findSurfaceView((ViewGroup) glView, "id/play_view");
        if (sv != null) {
            Log.i("VR", "find gifmaker:" + sv);
            FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) sv.getLayoutParams();
            int w = glView.getResources().getDisplayMetrics().widthPixels;
            int h = glView.getResources().getDisplayMetrics().heightPixels;
            int width = w > h ? w : h;
            int height = w > h ? h : w;
            lp.width = height;
            lp.height = width;
            lp.topMargin = height;
            sv.setLayoutParams(lp);
        }
    }


}

final class SurfaceChangeBroadcastReceiver extends BroadcastReceiver {

    Activity mActivity;
    private event mRemoteService;
    private View glView;
    private ViewGroup.LayoutParams glLayoutParams;
    private View glParentView;
    private int glIndex;
    private boolean mDispatch2View;

    public boolean getKeyModeDispatch2View() {
        Properties prop = new Properties();
        try {
            //读取属性文件a.properties
            InputStream in = new BufferedInputStream(new FileInputStream("/sdcard/Nibiru/KeyMap/keymap.xml"));
            prop.load(in);     ///加载属性列表
            Iterator<String> it = prop.stringPropertyNames().iterator();
            while (it.hasNext()) {
                String key = it.next();
                Log.i("VR", key + ":" + prop.getProperty(key));
                if (key.equals("keymode")) {
                    String value = prop.getProperty(key);
                    in.close();
                    return Boolean.valueOf(value);

                }
            }
            in.close();
        } catch (Exception e) {
            System.out.println(e);
        }
        return true;
    }


    private callback cb = new callback.Stub() {

        @Override
        public void dispatchMotionEvent(final MotionEvent motionevent) throws RemoteException {
            // TODO 自动生成的方法存根
            Log.i("VR", "dispatchMotionEvent:" + motionevent);
            if (mActivity != null) {
                if (!mDispatch2View) {
                    try {
                        Method md_root = Class.forName("android.view.View").getDeclaredMethod("getViewRootImpl");
                        Object root = md_root.invoke(mActivity.getWindow().getDecorView());


                        Field fd = Class.forName("android.view.ViewRootImpl").getDeclaredField("mInputEventReceiver");
                        fd.setAccessible(true);
                        Object obj = fd.get(root);

                        /*Field fd1 = Class.forName("android.view.ViewRootImpl").getDeclaredField("mInputQueueCallback");
                        fd1.setAccessible(true);
                        Object obj1 = fd1.get(root);*/
                        if (true) {
                            Method md_dis = Class.forName("android.view.InputEventReceiver").getDeclaredMethod("dispatchInputEvent", int.class, InputEvent.class);
                            md_dis.setAccessible(true);
                            Log.i("VR", "injectkeyevent");
                            md_dis.invoke(obj, (int) motionevent.getEventTime(), motionevent);
                            return;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                mActivity.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        // TODO 自动生成的方法存根
                        try {
                            if (glView != null) {
                                glView.dispatchTouchEvent(motionevent);
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }


        }

        @Override
        public void dispatchKeyEvent(final KeyEvent keyevent) throws RemoteException {
            // TODO 自动生成的方法存根
            Log.i("VR", "dispatchKeyEvent:" + keyevent);
            if (mActivity != null) {
                mActivity.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        // TODO 自动生成的方法存根
                        try {
                            if (glView != null) {
                                glView.dispatchKeyEvent(keyevent);
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }


        }
    };

    private ServiceConnection mRemoteConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            mRemoteService = event.Stub.asInterface(service);
            try {
                mRemoteService.regCallback(cb);
            } catch (RemoteException e) {
                // TODO 自动生成的 catch 块
                e.printStackTrace();
            }

            Log.i("VR", "onServiceConnected" + className);
        }

        public void onServiceDisconnected(ComponentName className) {
            mRemoteService = null;
            Log.i("VR", "onServiceDisconnected" + className);
        }
    };

    SurfaceChangeBroadcastReceiver(Activity act) {
        super();
        mActivity = act;
    }

    //接收到广播后自动调用该方法
    @Override
    public void onReceive(Context context, Intent intent) {
        //写入接收广播后的操作
        Log.i("vrtest", "SurfaceChangeBroadcastReceiver onReceive:" + mActivity);
        mDispatch2View = getKeyModeDispatch2View();
        Log.i("vrtest", "mDispatch2View :" + mDispatch2View);
        //mActivity.unregisterReceiver(this);
        if (intent.hasExtra("restore")) {
            Log.i("vrtest", "SurfaceChangeBroadcastReceiver resotre:");
            mActivity.unregisterReceiver(this);
            ((ViewGroup) (glView.getParent())).removeView(glView);
            ((ViewGroup) (glParentView)).addView(glView, glIndex, glLayoutParams);
            try {
                Method resume = Activity.class.getDeclaredMethod("onPause");
                resume.setAccessible(true);
                resume.invoke(mActivity);
                Method windowfocuschanged = Window.Callback.class.getDeclaredMethod("onWindowFocusChanged", boolean.class);
                windowfocuschanged.setAccessible(true);
                windowfocuschanged.invoke(mActivity, false);

            } catch (Exception e) {
                Log.i("vrtest", "VRChangedBroadcastReceiver exc:" + e);
            }
            return;
        }
        if (mActivity.findViewById(android.R.id.content) == null) {
            return;
        }
        try {
            Method resume = Activity.class.getDeclaredMethod("onResume");
            resume.setAccessible(true);
            resume.invoke(mActivity);
            Method windowfocuschanged = Window.Callback.class.getDeclaredMethod("onWindowFocusChanged", boolean.class);
            windowfocuschanged.setAccessible(true);
            windowfocuschanged.invoke(mActivity, true);

        } catch (Exception e) {
            Log.i("vrtest", "VRChangedBroadcastReceiver exc:" + e);
        }
        Surface sf = intent.getParcelableExtra("surface");
        DisplayManager mDisplayManager = (DisplayManager) mActivity.getSystemService(Context.DISPLAY_SERVICE);
        VirtualDisplay virtualDisplay = mDisplayManager.createVirtualDisplay("gl",
                VRParamManager.getRealWidth(mActivity), VRParamManager.getRealHeight(mActivity), DisplayMetrics.DENSITY_MEDIUM, sf, 0);

        Display display = virtualDisplay.getDisplay();


        glView = mActivity.findViewById(android.R.id.content);
        glParentView = (View) glView.getParent();
        glIndex = ((ViewGroup) (glView.getParent())).indexOfChild(glView);
        glLayoutParams = glView.getLayoutParams();
        TestPresentation pst = new TestPresentation(mActivity, display,
                0xffffffff, WindowManager.LayoutParams.TYPE_PRIVATE_PRESENTATION, 0, glView);
        pst.show();

        Intent keybindingintent = new Intent("com.hmct.keydispatch");
        keybindingintent.setComponent(new ComponentName("com.app360.app360", "com.nitro888.nitroaction360.KeyDispatchService"));
        mActivity.bindService(keybindingintent,
                mRemoteConnection, Context.BIND_AUTO_CREATE);

    }
}

final class VRChangedBroadcastReceiver extends BroadcastReceiver {

    Activity mActivity;

    VRChangedBroadcastReceiver(Activity act) {
        super();
        mActivity = act;
    }

    //接收到广播后自动调用该方法
    @Override
    public void onReceive(Context context, Intent intent) {
        //写入接收广播后的操作
        Log.i("vrtest", "VRChangedBroadcastReceiver onReceive:" + mActivity);
        SurfaceChangeBroadcastReceiver receiver = new SurfaceChangeBroadcastReceiver(mActivity);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.surface");
        mActivity.registerReceiver(receiver, intentFilter);

        Intent myIntent = new Intent();
        myIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        myIntent.setComponent(new ComponentName("com.app360.app360", "com.nitro888.nitroaction360.MainActivity"));
        myIntent.putExtra("type", "vr_mode");
        myIntent.putExtra("width", VRParamManager.getRealWidth(mActivity));
        myIntent.putExtra("height", VRParamManager.getRealHeight(mActivity));

        mActivity.startActivity(myIntent);

    }
}

/**
 * @author Lody
 */
public final class AppInstrumentation extends InstrumentationDelegate implements IInjector {

    private static final String TAG = AppInstrumentation.class.getSimpleName();

    private static AppInstrumentation gDefault;

    private AppInstrumentation(Instrumentation base) {
        super(base);
    }

    public static AppInstrumentation getDefault() {
        if (gDefault == null) {
            synchronized (AppInstrumentation.class) {
                if (gDefault == null) {
                    gDefault = create();
                }
            }
        }
        return gDefault;
    }

    private static AppInstrumentation create() {
        Instrumentation instrumentation = ActivityThread.mInstrumentation.get(VirtualCore.mainThread());
        if (instrumentation instanceof AppInstrumentation) {
            return (AppInstrumentation) instrumentation;
        }
        return new AppInstrumentation(instrumentation);
    }

    @Override
    public void inject() throws Throwable {
        base = ActivityThread.mInstrumentation.get(VirtualCore.mainThread());
        ActivityThread.mInstrumentation.set(VirtualCore.mainThread(), this);
    }

    @Override
    public boolean isEnvBad() {
        return !(ActivityThread.mInstrumentation.get(VirtualCore.mainThread()) instanceof AppInstrumentation);
    }

    @Override
    public void callActivityOnCreate(Activity activity, Bundle icicle) {
        VirtualCore.get().getComponentDelegate().beforeActivityCreate(activity);
        IBinder token = mirror.android.app.Activity.mToken.get(activity);
        ActivityClientRecord r = VActivityManager.get().getActivityRecord(token);
        if (r != null) {
            r.activity = activity;
        }
        ContextFixer.fixContext(activity);
        ActivityFixer.fixActivity(activity);
        ActivityInfo info = null;
        if (r != null) {
            info = r.info;
        }
        if (info != null) {
            if (info.theme != 0) {
                activity.setTheme(info.theme);
            }
            if (activity.getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
                    && info.screenOrientation != ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED) {
                activity.setRequestedOrientation(info.screenOrientation);
            }
        }
        super.callActivityOnCreate(activity, icicle);
        VirtualCore.get().getComponentDelegate().afterActivityCreate(activity);

        //
        Log.i("jin", "111111111111111111 oncreate  activity.getClass().getName() =" + activity.getClass().getName());
        if (activity.getClass().getName().startsWith("com.ss.android.ugc.aweme")||activity.getClass().getName().startsWith("com.ss.android.article") || activity.getClass().getName().startsWith("com.ss.android.ugc.live.detail.DetailActivity")) {
            Log.i("jin", "111111111111111111");
            WindowManager windowManager = (WindowManager) activity.getSystemService(Context.WINDOW_SERVICE);
            Display d = windowManager.getDefaultDisplay();


            DisplayMetrics realDisplayMetrics = new DisplayMetrics();
            d.getRealMetrics(realDisplayMetrics);


            int realHeight = realDisplayMetrics.heightPixels;
            int realWidth = realDisplayMetrics.widthPixels;
            //activity.getWindow().setLayout(realWidth/2,realHeight/2);
            Log.i("vrtest", "registerReceiver:" + activity);
            VRChangedBroadcastReceiver receiver = new VRChangedBroadcastReceiver(activity);
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("android.intent.action.vrchanged");
            activity.registerReceiver(receiver, intentFilter);

            Object obj = activity.getWindow().getDecorView().getTag();
            ActivityCyleInfo aci = new ActivityCyleInfo();
            aci.receiver = receiver;
            activity.getWindow().getDecorView().setTag(aci);
        }


    }

    @Override
    public void callActivityOnResume(Activity activity) {
        VirtualCore.get().getComponentDelegate().beforeActivityResume(activity);
        VActivityManager.get().onActivityResumed(activity);
        super.callActivityOnResume(activity);
        VirtualCore.get().getComponentDelegate().afterActivityResume(activity);
        Intent intent = activity.getIntent();
        if (intent != null) {
            Bundle bundle = intent.getBundleExtra("_VA_|_sender_");
            if (bundle != null) {
                IBinder callbackToken = BundleCompat.getBinder(bundle, "_VA_|_ui_callback_");
                IUiCallback callback = IUiCallback.Stub.asInterface(callbackToken);
                if (callback != null) {
                    try {
                        callback.onAppOpened(VClientImpl.get().getCurrentPackage(), VUserHandle.myUserId());


                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        //activity.getWindow().setGravity(Gravity.LEFT);
        WindowManager windowManager = (WindowManager) activity.getSystemService(Context.WINDOW_SERVICE);
        Display d = windowManager.getDefaultDisplay();


        DisplayMetrics realDisplayMetrics = new DisplayMetrics();
        d.getRealMetrics(realDisplayMetrics);


        int realHeight = realDisplayMetrics.heightPixels;
        int realWidth = realDisplayMetrics.widthPixels;
        //activity.getWindow().setLayout(realWidth/2,realHeight/2);
        Log.i("vrtest", "registerReceiver:" + activity);
        VRChangedBroadcastReceiver receiver = new VRChangedBroadcastReceiver(activity);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.vrchanged");
        activity.registerReceiver(receiver, intentFilter);

        Object obj = activity.getWindow().getDecorView().getTag();
        ActivityCyleInfo aci = new ActivityCyleInfo();
        aci.receiver = receiver;
        activity.getWindow().getDecorView().setTag(aci);
        Log.i("jin", "111111111111111111 onResume  activity.getClass().getName() =" + activity.getClass().getName());
    }


    @Override
    public void callActivityOnDestroy(Activity activity) {
        VirtualCore.get().getComponentDelegate().beforeActivityDestroy(activity);
        super.callActivityOnDestroy(activity);
        VirtualCore.get().getComponentDelegate().afterActivityDestroy(activity);
    }

    @Override
    public void callActivityOnPause(Activity activity) {
        VirtualCore.get().getComponentDelegate().beforeActivityPause(activity);
        super.callActivityOnPause(activity);
        VirtualCore.get().getComponentDelegate().afterActivityPause(activity);

        if (activity.getWindow().getDecorView().getTag() instanceof ActivityCyleInfo) {
            Log.i("vrtest", "unregisterReceiver:" + activity);
            ActivityCyleInfo aci = (ActivityCyleInfo) activity.getWindow().getDecorView().getTag();
            activity.unregisterReceiver(aci.receiver);
        }

    }


    @Override
    public void callApplicationOnCreate(Application app) {
        super.callApplicationOnCreate(app);
    }

}
