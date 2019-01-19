package com.nitro888.nitroaction360;

import java.io.File;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Presentation;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.ImageReader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Toast;


import com.app360.app360.R;


import com.google.vrtoolkit.cardboard.CardboardView;
import com.nibiru.lib.controller.BaseEvent;
import com.nibiru.lib.controller.Controller;
import com.nibiru.lib.controller.ControllerDevice;
import com.nibiru.lib.controller.ControllerKeyEvent;
import com.nibiru.lib.controller.ControllerService;
import com.nibiru.lib.controller.ControllerServiceException;
import com.nibiru.lib.controller.OnKeyListener;
import com.nibiru.lib.controller.OnSimpleStickListener;
import com.nibiru.lib.controller.OnSpecEventListener;
import com.nibiru.lib.controller.OnStateListener;
import com.nibiru.lib.controller.OnVoiceListener;
import com.nibiru.lib.feedback.FeedbackService;
import com.nitro888.nitroaction360.cardboard.NACardboardOverlayView;
import com.google.vrtoolkit.cardboard.CardboardActivity;
import com.nitro888.nitroaction360.nitroaction.NAMediaPlayer;
import com.nitro888.nitroaction360.nitroaction.NAScreenGLRenderer;
import com.nitro888.nitroaction360.nitroaction.NAViewsToGLRenderer;
import com.nitro888.nitroaction360.utils.ScreenTypeHelper;



public class MainActivity extends CardboardActivity  implements OnKeyListener, OnStateListener, ControllerService.OnControllerSeviceListener, OnSimpleStickListener, OnSpecEventListener, OnVoiceListener {
    private static final String         TAG                     = MainActivity.class.getSimpleName();
    
    //add for virture display vr test code
    private static final int WIDTH = 1920;
    private static final int HEIGHT = 1080;
    private static final int DENSITY = DisplayMetrics.DENSITY_MEDIUM;
    private static final int TIMEOUT = 10000;

    // Colors that we use as a signal to determine whether some desired content was
    // drawn.  The colors themselves doesn't matter but we choose them to have with distinct
    // values for each color channel so as to detect possible RGBA vs. BGRA buffer format issues.
    // We should only observe RGBA buffers but some graphics drivers might incorrectly
    // deliver BGRA buffers to virtual displays instead.
    private static final int BLUEISH = 0xff1122ee;
    private static final int GREENISH = 0xff33dd44;

    private DisplayManager mDisplayManager;
    private Handler mHandler;
    private final Lock mImageReaderLock = new ReentrantLock(true /*fair*/);
    private ImageReader mImageReader;
    private Surface mSurface;
	private boolean mLeaving = false;
    
    
    private final class TestPresentation extends Presentation {
        private final int mColor;
        private final int mWindowType;
        private final int mWindowFlags;
        private View glView;

        public TestPresentation(Context context, Display display,
                int color, int windowType, int windowFlags,View v) {
            super(context, display);
            mColor = color;
            mWindowType = windowType;
            mWindowFlags = windowFlags;
            glView = v;
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            setTitle(TAG);
            getWindow().setType(mWindowType);
            getWindow().addFlags(mWindowFlags);
            //if(true)return;
            // Create a solid color image to use as the content of the presentation.
            //ImageView view = new ImageView(getContext());
            ViewGroup parent = (ViewGroup) glView.getParent();
            parent.removeView(glView);
           
            glView.setLayoutParams(new LayoutParams(
                    LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
            setContentView(glView);
        }
        
        
    }
    public void setVRMode(View v,Surface vrSurface){
    	mDisplayManager = (DisplayManager)getSystemService(Context.DISPLAY_SERVICE);
        
        
        VirtualDisplay virtualDisplay = mDisplayManager.createVirtualDisplay("gl",
                WIDTH, HEIGHT, DENSITY, vrSurface, 0);

        Display display = virtualDisplay.getDisplay();
        
        TestPresentation pst = new TestPresentation(this, display,
        		BLUEISH, WindowManager.LayoutParams.TYPE_PRIVATE_PRESENTATION, 0,v);
        pst.show();
    }
    //add end .....................................

	protected ControllerService mControllerService;

	protected FeedbackService mFeedbackService;

	
	static HashMap<String,String> mPkg2Entry = new HashMap<String,String>();
	

    private NACardboardOverlayView      mNACardboardOverlayView;
    private CardboardView               mCardboardView;

    private NAViewsToGLRenderer         mNAViewsToGLRenderer;
    private NAScreenGLRenderer          mNAScreenGLRenderer;
    private NAMediaPlayer               mNAMediaPlayer;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.common_ui);
        
        buildNibiruService();

        

        // ViewsToGLRenderer
        mNAViewsToGLRenderer    = new NAViewsToGLRenderer(this);

        
        // MediaPlayer
        mNAMediaPlayer          = new NAMediaPlayer(this);
        mNAMediaPlayer.setViewToGLRenderer(mNAViewsToGLRenderer);
        
        
        
        

        // Screen
        mNAScreenGLRenderer     = new NAScreenGLRenderer(this);
        mNAScreenGLRenderer.setViewToGLRenderer(mNAViewsToGLRenderer);

      
        

        // Cardboard
        mCardboardView          = (CardboardView) findViewById(R.id.cardboard_view);
        mCardboardView.setRenderer((CardboardView.StereoRenderer) mNAScreenGLRenderer);

        mNACardboardOverlayView = (NACardboardOverlayView) findViewById(R.id.overlay);
        //mNACardboardOverlayView.show3DToast("NitroAction 360 Start");

        setCardboardView(mCardboardView);
        
       
    }
    

    @Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode != Activity.RESULT_OK){
			return;
		}
	}


	@Override
    protected void onStart() {
        super.onStart();
		//nibiru
        if( mControllerService != null ){
			mControllerService.onStart();
		}
    }
   @Override
	public boolean dispatchKeyEvent(KeyEvent event) {
    	Log.i("vr","dispatchKeyEvent:" + event);
		// TODO
    	if(KeyEvent.KEYCODE_BACK == event.getKeyCode() ){
			if(event.getAction() == KeyEvent.ACTION_UP && !mLeaving){
				mLeaving = true;
				Intent intent = new Intent("_VA_protected_android.intent.action.surface");
				intent.putExtra("restore", mNAViewsToGLRenderer
						.getSurface(NAViewsToGLRenderer.SURFACE_TEXTURE_FOR_MEDIAPLAYER));
				sendBroadcast(intent);
				this.getWindow().getDecorView().postDelayed(new Runnable(){
					public void run()
					{
                         finish();
					}
				},2000);
			};
    	}else if(KeyEvent.KEYCODE_VOLUME_UP == event.getKeyCode() && event.getAction() == KeyEvent.ACTION_DOWN){
    		setScreenScale(+1);
    	} else if(KeyEvent.KEYCODE_VOLUME_DOWN == event.getKeyCode() && event.getAction() == KeyEvent.ACTION_DOWN){
    		setScreenScale(-1);
    	}else{
    		if( mControllerService != null && mControllerService.handleExternalInput(event) ){
    			return true;
    		}
    	}
		return true;//super.dispatchKeyEvent(event);
	}


	@Override
	public boolean dispatchTrackballEvent(MotionEvent ev) {
		// TODO
		Log.i("vr","dispatchTrackballEvent:" + ev);
		return true;//super.dispatchTrackballEvent(ev);
	}


    // for setting controller
    public void setScreenShapeType(int screenID) {
        mNAScreenGLRenderer.setScreenShapeType(screenID);
    }
    public void setScreenRenderType(int renderType) {
        mNAScreenGLRenderer.setScreenRenderType(renderType);
    }
    public void setScreenScale(float step) {
        mNAScreenGLRenderer.setScreenScale(step);
    }

    @Override
    public void onCardboardTrigger() {
        
    }
    public void onGUIButtonClick(View view) {
    	
    }

    @Override
    public void onResume() {
        super.onResume();
        mNAMediaPlayer.resume();
        if( mControllerService != null ){
			mControllerService.onResume();
			mControllerService.handleFullScreenMode();
		}
        if("vr_set".equals(getIntent().getStringExtra("type"))){
			findViewById(R.id.game_img).setVisibility(View.VISIBLE);
			Bitmap bm = BitmapFactory.decodeFile(getIntent().getStringExtra("file"));
			findViewById(R.id.game_img).setBackground(new BitmapDrawable(bm));
        }
        
        
    }
    public void onSurfaceChanged(int w,int h) {
        //mNAMediaPlayer.onSurfaceChanged(w,h);
    	Configuration cf = getResources().getConfiguration();
		cf.orientation = Configuration.ORIENTATION_LANDSCAPE;
		getResources().updateConfiguration(cf, getResources().getDisplayMetrics());
		if("vr_mode".equals(getIntent().getStringExtra("type"))){
			
			int width = getIntent().getIntExtra("width",getResources().getDisplayMetrics().widthPixels);
			int height = getIntent().getIntExtra("height",getResources().getDisplayMetrics().heightPixels);
			mNAViewsToGLRenderer.setTextureWidth(NAViewsToGLRenderer.SURFACE_TEXTURE_FOR_MEDIAPLAYER,width);
	        mNAViewsToGLRenderer.setTextureHeight(NAViewsToGLRenderer.SURFACE_TEXTURE_FOR_MEDIAPLAYER,height);
	        mNAViewsToGLRenderer.createSurface(NAViewsToGLRenderer.SURFACE_TEXTURE_FOR_MEDIAPLAYER);
	        
	        Intent intent = new Intent("_VA_protected_android.intent.action.surface");
	        intent.putExtra("surface", mNAViewsToGLRenderer
									.getSurface(NAViewsToGLRenderer.SURFACE_TEXTURE_FOR_MEDIAPLAYER));
	        sendBroadcast(intent);
	        if(true)return;
			this.runOnUiThread(new Runnable() {

				@Override
				public void run() {
					// TODO
					setVRMode(
							MMActivity.glView,
							mNAViewsToGLRenderer
									.getSurface(NAViewsToGLRenderer.SURFACE_TEXTURE_FOR_MEDIAPLAYER));

				}
			});

	        
			
			return;
		}
		
		if(true)return;
		
		
    	mNAViewsToGLRenderer.setTextureWidth(NAViewsToGLRenderer.SURFACE_TEXTURE_FOR_MEDIAPLAYER,getResources().getDisplayMetrics().widthPixels);
        mNAViewsToGLRenderer.setTextureHeight(NAViewsToGLRenderer.SURFACE_TEXTURE_FOR_MEDIAPLAYER,getResources().getDisplayMetrics().heightPixels);
        mNAViewsToGLRenderer.createSurface(NAViewsToGLRenderer.SURFACE_TEXTURE_FOR_MEDIAPLAYER);

    }
    @Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		// TODO Auto-generated method stub
    	KeyDispatchService.dispatchMotionEvent(ev);
		Log.i("vr","onTouchEvent");
		//mActivityView.setSurface(null);
		//mActivityView.setSurface(mNAViewsToGLRenderer.getSurface(NAViewsToGLRenderer.SURFACE_TEXTURE_FOR_MEDIAPLAYER));
		return true;//super.dispatchTouchEvent(ev);
	}
    

	@Override
    public void onPause() {
        super.onPause();
        mNAMediaPlayer.pause();
		//nibiru
		if( mControllerService != null ){
			mControllerService.onPause();
			setScreenOnMode(false);
		}
    }
    @Override
    public void onStop() {
        super.onStop();
        mNAMediaPlayer.stop();
        if( mControllerService != null ){
			mControllerService.onStop();
		}
    }
    void buildNibiruService(){
		
		//Get nibiru controller service instance
		mControllerService = Controller.getControllerService(this);
		
		//Get Nibiru feedback service, transfer game data to devices
		mFeedbackService = mControllerService.getFeedbackService();
		
		//set key listener
		mControllerService.setKeyListener(this);
		
		//Set single separated stick listener
		mControllerService.setSimpleStickListener(this);
		
		mControllerService.setSpecEventListener(this);
		
		//Set gamepad state listener
		mControllerService.setStateListener(this);
		
		//Set a service listener to know when the service is registered.
		mControllerService.setControllerServiceListener(this);
		
		//Set a voice listener, the identified text will callback to VoiceListener
		mControllerService.setVoiceListener(this);

		//Set ControllerService handler, default is the current thread handler.
		mControllerService.setHandler(new Handler());
		
		mControllerService.getContinusKeyService().registerContinuesDirectionKey();
		
		
		if( mControllerService != null && !mControllerService.isServiceEnable()){
			
			//register controller service, the parameter must be a Activity instance
			try {
				mControllerService.register();
			} catch (ControllerServiceException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if( mControllerService != null ){
			mControllerService.unregister();
			mControllerService = null;
		}
	}

	public void startCursorMode(int resId){
		//Start mouse mode, resId is the resouce Id
		if( mControllerService != null && mControllerService.getCursorService() != null ){
			mControllerService.getCursorService().createCursor(resId);
		}
	}
	
	//Display gamepad control guide
	public void showGameGuide(boolean isShow){
		if( mControllerService == null )return;
		if(isShow)
		mControllerService.showGameGuide(isShow);
		
	}
	//Check exist device connection
	public boolean hasDeviceConnected() throws ControllerServiceException{
		if( mControllerService == null ){
			throw new ControllerServiceException("Controller Service is not connected to driver, please install your driver first and wait for connection build");
		}
		
		return mControllerService.hasDeviceConnected();
	}
	//Set mouse resource Id
	public void setArrowResId(int resId){
		if( mControllerService != null && mControllerService.getCursorService() != null ){
			mControllerService.getCursorService().setCursorResource(resId);
		}
	}
	//keep screen on or not
	protected void setScreenOnMode(boolean isHold){
		if( isHold ){
			getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
			getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
					WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		}else{
			getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		}
		
	}
	
	//listen controller state changed
	@Override
	public void onControllerStateChanged(int playerOrder, int state,
			ControllerDevice device) {
		// TODO Auto-generated method stub
		
	}
	//listen bluetooth state changed
	@Override
	public void onBluetoothStateChanged(int state) {
		// TODO Auto-generated method stub
		
	}

	
	//listen keydown
	@Override
	public void onControllerKeyDown(int playerOrder, int keyCode,
			ControllerKeyEvent event) {
	}

	
	//listen keyup
	@Override
	public void onControllerKeyUp(int playerOrder, int keycode,
			ControllerKeyEvent event) {
		// TODO Auto-generated method stub
		
	}

	//check current service is available
	public boolean isControllerServiceEnable(){
		return mControllerService != null && mControllerService.isServiceEnable();
	}
	
	//Get the instance of ControllerService
	public ControllerService getControllerService(){
		return mControllerService;
	}

	//listen service registered result
	@Override
	public void onControllerServiceReady(boolean isSucc) {
		// TODO Auto-generated method stub
	}

	//listen left stick changed
	@Override
	public boolean onLeftStickChanged(int playerOrder, float x, float y) {
		// TODO Auto-generated method stub
		return false;
	}

	//listen right stick changed
	@Override
	public boolean onRightStickChanged(int playerOrder, float x, float y) {
		// TODO Auto-generated method stub
		return false;
	}


	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
	@Override
	public boolean dispatchGenericMotionEvent(MotionEvent ev) {
		// TODO Auto-generated method stub

		//Add next 2 lines handle code, you can directly copy them
		if( mControllerService != null && mControllerService.handleExternalInput(ev) ){
			return true;
		}
		
		return super.dispatchGenericMotionEvent(ev);
	}
	@Override
	public void onRevSpecEvent(BaseEvent event) {
		// TODO Auto-generated method stub

	}

	public boolean startVoice(){
		if( mControllerService != null ){
			return mControllerService.startVoice(this);
		}
		
		return false;
	}
	
	public void setVoiceParam(int speechTime, int delayTime){
		if( mControllerService != null ){
			mControllerService.setVoiceParam(speechTime, delayTime);
		}
	}
	
	@Override
	public void onVoiceResult(int state, String errorInfo, String result) {
		// TODO Auto-generated method stub
		
	}
}