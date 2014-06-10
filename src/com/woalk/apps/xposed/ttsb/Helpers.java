package com.woalk.apps.xposed.ttsb;

import de.robv.android.xposed.XposedBridge;
import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

public final class Helpers {
	
	public static String TTSB_PACKAGE_NAME = "com.woalk.apps.xposed.ttsb";
	public static String TTSB_PREFERENCES = "com.woalk.apps.xposed.ttsb.TTSB_PREFERENCES";
	public static String TTSB_SHOW_ACTIVITY_TOAST = "com.woalk.apps.xposed.ttsb.SHOW_TOAST";

	public static void setTranslucentStatus(Activity activity, boolean on) {
		Window win = activity.getWindow();
		WindowManager.LayoutParams winParams = win.getAttributes();
		if (on) {
			winParams.flags |= WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
			
		} else {
			winParams.flags &= ~WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
		}
		win.getDecorView().setFitsSystemWindows(true);
		win.setAttributes(winParams);
	}
	
	public static boolean isTranslucencyAllowed(Activity activity) {
		WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
		int i = lp.flags;
		boolean fullScreen = (i & WindowManager.LayoutParams.FLAG_FULLSCREEN) != 0;
		boolean forceNotFullScreen = (i & WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN) != 0;
		boolean translucentStatus = (i & WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS) != 0;
		if (!fullScreen || forceNotFullScreen && !translucentStatus) {
			return true;
		}
		else {
			return false;
		}
	}
	
	public static void logContentView(View parent, String indent) {
	    XposedBridge.log(">TTSB [VIEWTEST] " + indent + parent.getClass().getName());
	    if (parent instanceof ViewGroup) {
	        ViewGroup group = (ViewGroup)parent;
	        for (int i = 0; i < group.getChildCount(); i++)
	            logContentView(group.getChildAt(i), indent + " ");
	    }
	}
	
	public static View getContentView(View parent) {
	    if (parent instanceof ViewGroup) {
	        ViewGroup group = (ViewGroup)parent;
	        for (int i = 0; i < group.getChildCount(); i++) {
	        	if (group.getChildAt(i) instanceof ViewGroup) {
	        		ViewGroup group2 = (ViewGroup)group.getChildAt(i);
	        		for (int j = 0; j < group2.getChildCount(); j++) {
	    	        	if (group2.getChildAt(j) instanceof ViewGroup) {
	    	        		return group2.getChildAt(j);
	    	        	}
	        		}
	        	}
	        }
	    }
	    return parent;
	}

	public static View getContentViewWithouActionBar(View parent) {
	    if (parent instanceof ViewGroup) {
	        ViewGroup group = (ViewGroup)parent;
	        for (int i = 0; i < group.getChildCount(); i++) {
	        	if (group.getChildAt(i) instanceof ViewGroup) {
	        		ViewGroup group2 = (ViewGroup)group.getChildAt(i);
	        		return group2;
	        	}
	        }
	    }
	    return parent;
	}
}
