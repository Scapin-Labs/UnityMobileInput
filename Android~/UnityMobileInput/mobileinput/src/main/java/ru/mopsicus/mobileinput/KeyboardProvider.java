// ----------------------------------------------------------------------------
// The MIT License
// UnityMobileInput https://github.com/mopsicus/UnityMobileInput
// Copyright (c) 2018 Mopsicus <mail@mopsicus.ru>
// ----------------------------------------------------------------------------

package ru.mopsicus.mobileinput;

import android.app.Activity;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.util.Log;
import android.view.Display;
import android.view.Window;
import android.content.Context;
import android.util.DisplayMetrics;
import android.os.Build;

public class KeyboardProvider extends PopupWindow {

    private KeyboardObserver observer;
    private int keyboardLandscapeHeight;
    private int keyboardPortraitHeight;
    private View popupView;
    private View parentView;
    private Activity activity;

    // Constructor
    public KeyboardProvider(Activity activity, ViewGroup parent, KeyboardObserver listener) {
        
        super(activity);
        this.observer = listener;
        this.activity = activity;
        Resources resources = this.activity.getResources();
        String packageName = this.activity.getPackageName();
        int id = resources.getIdentifier("popup", "layout", packageName);
        LayoutInflater inflator = (LayoutInflater) activity.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        this.popupView = inflator.inflate(id, null, false);
        setContentView(popupView);
        setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE | WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
        
        parentView = parent;
        setWidth(0);
        setHeight(WindowManager.LayoutParams.MATCH_PARENT);
        setBackgroundDrawable(new ColorDrawable(0));
        showAtLocation(parentView, Gravity.NO_GRAVITY, 0, 0);

        navBarHeight = getNavigationBarHeight();

        popupView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (popupView != null) {
                    handleOnGlobalLayout();
                }
            }
        });

    }

    // Close fake popup
    public void disable() {
        dismiss();
    }

    // Return screen orientation
    private int getScreenOrientation() {
        return activity.getResources().getConfiguration().orientation;
    }

    private int heightMax;
    private int navBarHeight;

    // Handler to get keyboard height
    private void handleOnGlobalLayout() {
   
        Point screenSize = new Point();
        activity.getWindowManager().getDefaultDisplay().getSize(screenSize);
    
        Rect rect = new Rect();
        popupView.getWindowVisibleDisplayFrame(rect);
    
        // REMIND, you may like to change this using the fullscreen size of the phone
        // and also using the status bar and navigation bar heights of the phone to calculate
        // the keyboard height. But this worked fine on a Nexus.
        int orientation = getScreenOrientation();
        int keyboardHeight = screenSize.y - (rect.bottom - rect.top);
        
        if (keyboardHeight == 0) {
            notifyKeyboardHeight(0, 0, orientation);
        }
        else if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            //this.keyboardPortraitHeight = keyboardHeight; 
            //notifyKeyboardHeightChanged(keyboardPortraitHeight, orientation);
            
                   Rect r = new Rect();
                   View rootview = activity.getWindow().getDecorView(); // this = activity
                   rootview.getWindowVisibleDisplayFrame(r);
            
            notifyKeyboardHeight(r.top-r.bottom, keyboardHeight, orientation);
        } 
        else {
            this.keyboardLandscapeHeight = keyboardHeight; 
            //notifyKeyboardHeightChanged(keyboardLandscapeHeight, orientation);
        }
        
        
        
        
        // WORKING SOLUTION ABOVE!

/*
        Rect r = new Rect();
        parent.getWindowVisibleDisplayFrame(r);

        int screenHeight = parent.getRootView().getHeight();
        int heightDifference = screenHeight - (r.bottom - r.top);
        Log.d("Keyboard Size", "Size: " + heightDifference);
*/
/*
		Point screenSize = new Point();
                activity.getWindowManager().getDefaultDisplay().getSize(screenSize);
		Rect rect = new Rect();
        popupView.getWindowVisibleDisplayFrame(rect);
        if (rect.bottom > heightMax) {
            heightMax = rect.bottom;
        }
        int keyboardHeight = heightMax - rect.bottom;
        if (keyboardHeight > 0)
        {
        	keyboardHeight += navBarHeight;
        }
        int orientation = getScreenOrientation();
        
        notifyKeyboardHeight(screenSize.y, keyboardHeight, orientation);
        */
    }

    private int getNavigationBarHeight() {
    	if (!hasSoftKeys())
    	{
    		return 0;
    	}
        Resources resources = activity.getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            return resources.getDimensionPixelSize(resourceId);
        }
        return 0;
    }

    public boolean hasSoftKeys() {
        Display d = activity.getWindowManager().getDefaultDisplay();

        DisplayMetrics realDisplayMetrics = new DisplayMetrics();
        d.getRealMetrics(realDisplayMetrics);

        int realHeight = realDisplayMetrics.heightPixels;
        int realWidth = realDisplayMetrics.widthPixels;

        DisplayMetrics displayMetrics = new DisplayMetrics();
        d.getMetrics(displayMetrics);

        int displayHeight = displayMetrics.heightPixels;
        int displayWidth = displayMetrics.widthPixels;

        boolean hasSoftwareKeys =  (realWidth - displayWidth) > 0 || (realHeight - displayHeight) > 0;
	    return hasSoftwareKeys;
	}

    // Send data observer
    private void notifyKeyboardHeight(float rawHeight, int heightWithoutBottom, int orientation) {
        if (observer != null) { 
            observer.onKeyboardHeight(rawHeight, heightWithoutBottom, orientation, navBarHeight);
        }
    }
}