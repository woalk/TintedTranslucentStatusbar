package com.woalk.apps.xposed.ttsb;

import java.util.List;

import de.robv.android.xposed.XposedBridge;
import android.app.Activity;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.content.pm.PackageInfo;

public final class Helpers {

	public static String TTSB_PACKAGE_NAME = "com.woalk.apps.xposed.ttsb";
	public static String TTSB_PREFERENCES = "com.woalk.apps.xposed.ttsb.TTSB_PREFERENCES";
	public static String TTSB_PREF_SHOW_ACTIVITY_TOAST = "com.woalk.apps.xposed.ttsb.SHOW_TOAST";
	public static String TTSB_PREF_LASTUPDATE = "com.woalk.apps.xposed.ttsb.LAST_UPDATE";
	public static String TTSB_PREF_DEBUGLOG = "com.woalk.apps.xposed.ttsb.DEBUGLOG";

	public static final int FLAG_FLOATING_WINDOW = 0x00002000;

	public static Object clipboard_sav;

	public static List<PackageInfo> pkgs;

	public static String[] pkgNames;
	public static String[] appNames;

	public static void setTranslucentStatus(Activity activity, boolean on) {
		Window win = activity.getWindow();
		WindowManager.LayoutParams winParams = win.getAttributes();
		if (on)
			winParams.flags |= WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
		else
			winParams.flags &= ~WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
		win.setAttributes(winParams);
	}

	public static void setTranslucentNavigation(Activity activity, boolean on) {
		Window win = activity.getWindow();
		WindowManager.LayoutParams winParams = win.getAttributes();
		if (on)
			winParams.flags |= WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION;
		else
			winParams.flags &= ~WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION;
		win.setAttributes(winParams);
	}

	public static boolean isTranslucencyAllowed(Activity activity) {
		WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
		int i = lp.flags;
		boolean fullScreen = (i & WindowManager.LayoutParams.FLAG_FULLSCREEN) != 0;
		boolean forceNotFullScreen = (i & WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN) != 0;
		boolean translucentStatus = (i & WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS) != 0;
		return (!fullScreen || forceNotFullScreen && !translucentStatus);
	}

	public static void logContentView(View parent, String indent) {
		View content = parent.findViewById(android.R.id.content);
		String isCnt = "";
		if (parent == content)
			isCnt = " (ContentView)";
		XposedBridge.log(">TTSB [VIEWTEST] " + indent
				+ parent.getClass().getName() + isCnt);
		if (parent instanceof ViewGroup) {
			ViewGroup group = (ViewGroup) parent;
			for (int i = 0; i < group.getChildCount(); i++) {
				logContentView(group.getChildAt(i), indent + "╾");
			}
		}
	}

	public static View getContentView(View parent) {
		if (parent instanceof ViewGroup) {
			ViewGroup group = (ViewGroup) parent;
			for (int i = 0; i < group.getChildCount(); i++) {
				if (group.getChildAt(i) instanceof ViewGroup) {
					ViewGroup group2 = (ViewGroup) group.getChildAt(i);
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
			ViewGroup group = (ViewGroup) parent;
			for (int i = 0; i < group.getChildCount(); i++) {
				if (group.getChildAt(i) instanceof ViewGroup) {
					ViewGroup group2 = (ViewGroup) group.getChildAt(i);
					return group2;
				}
			}
		}
		return parent;
	}

	public static int getColor(String colorHex) {
		if (!colorHex.startsWith("#"))
			colorHex = "#" + colorHex;
		int a;
		int r;
		int g;
		int b;
		if (colorHex.length() == 7) {
			a = 0xFF;
			r = Integer.parseInt(colorHex.substring(1, 3), 16);
			g = Integer.parseInt(colorHex.substring(3, 5), 16);
			b = Integer.parseInt(colorHex.substring(5, 7), 16);
		} else if (colorHex.length() == 9) {
			a = Integer.parseInt(colorHex.substring(1, 3), 16);
			r = Integer.parseInt(colorHex.substring(3, 5), 16);
			g = Integer.parseInt(colorHex.substring(5, 7), 16);
			b = Integer.parseInt(colorHex.substring(7, 9), 16);
		} else
			return Color.BLACK;
		int c = Color.argb(a, r, g, b);
		return c;
	}

	public static String getColorHexString(int color) {
		String colorHex = "#";
		String a = Integer.toHexString(Color.alpha(color));
		if (a.length() == 1)
			a = "0" + a;
		String r = Integer.toHexString(Color.red(color));
		if (r.length() == 1)
			r = "0" + r;
		String g = Integer.toHexString(Color.green(color));
		if (g.length() == 1)
			g = "0" + g;
		String b = Integer.toHexString(Color.blue(color));
		if (b.length() == 1)
			b = "0" + b;
		colorHex += a + r + g + b;
		return colorHex;
	}
}
