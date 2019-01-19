/*
 * Copyright 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hmct.screencapture;

import java.lang.ref.WeakReference;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.ViewAnimator;
import android.support.v4.app.FragmentActivity;
import android.view.Gravity;
import android.util.Log;
import android.app.Activity;
import android.content.res.Configuration;
import android.widget.Toast;

import com.app360.app360.R;

/**
 * A simple launcher activity containing a summary sample description, sample
 * log and a custom {@link android.support.v4.app.Fragment} which can display a
 * view.
 * <p>
 * For devices with displays with a width of 720dp or greater, the sample log is
 * always visible, on other devices it's visibility is controlled by an item on
 * the Action Bar.
 */
public class VRMainActivity extends FragmentActivity {

    public static final String TAG = "MainActivity";

    // Whether the Log Fragment is currently shown
    private boolean mLogShown;
    
    
	public int getStatusBarHeight() {
		int result = 0;
		int resourceId = getResources().getIdentifier("status_bar_height",
				"dimen", "android");
		if (resourceId > 0) {
			result = getResources().getDimensionPixelSize(resourceId);
		}
		return result;
	}
	private static WeakReference<Activity> mAct;
	public static void finishActivity(){
		if(mAct != null  && mAct.get() != null){
			mAct.get().finish();
		}
	}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAct = new WeakReference<Activity>(this);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,  
                WindowManager.LayoutParams.FLAG_FULLSCREEN);  
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            FragmentTransaction transaction = getSupportFragmentManager()
                    .beginTransaction();
            ScreenCaptureFragment fragment = new ScreenCaptureFragment();
            transaction.replace(R.id.sample_content_fragment, fragment);
            transaction.commit();
        }
        getWindow().setGravity(Gravity.RIGHT);
        getWindow().setLayout(getResources().getDisplayMetrics().widthPixels,getResources().getDisplayMetrics().heightPixels + getStatusBarHeight());
        if (VRMainActivity.this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            Log.v(TAG, "is port we return");
            
            //MainActivity.this.finish();
        }
        // getActionBar().hide();
    }
}
