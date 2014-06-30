package com.woalk.apps.xposed.ttsb;

import com.woalk.apps.xposed.ttsb.SystemBarTintManager;
import com.woalk.apps.xposed.ttsb.Helpers;
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedHelpers;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewParent;
import android.widget.Toast;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;


public class X_TranslucentTint implements IXposedHookZygoteInit {

	@Override
	public void initZygote(StartupParam startupParam) throws Throwable {
		
		final Class<?> ActivityClass = XposedHelpers.findClass("android.app.Activity", null);
		
		findAndHookMethod(ActivityClass, "onPostCreate", android.os.Bundle.class, new XC_MethodHook() {
			@Override
			protected void afterHookedMethod(MethodHookParam param) throws Throwable {
				de.robv.android.xposed.XposedBridge.log(">TTSB: Hooked in onPostCreate().");
				
				Object currentObj = param.thisObject;
				Activity currentActivity;
				if (currentObj instanceof Activity) {
					de.robv.android.xposed.XposedBridge.log(">TTSB: [SUCCESS] The created object is an activity. Got the instance. Proceed.");
					currentActivity = (Activity) currentObj;
				}
				else {
					de.robv.android.xposed.XposedBridge.log(">TTSB: [ ERROR ] The created object is not an activity. Return.");
					return;
				}
				
				String activityFullName = currentActivity.getComponentName().getClassName();
				String packageName = currentActivity.getPackageName();
				de.robv.android.xposed.XposedBridge.log(">TTSB: [ INFO: ] Activity is " + activityFullName);

				Helpers.logContentView(currentActivity.getWindow().getDecorView(), "┕");

				XSharedPreferences XsPref = new XSharedPreferences(Helpers.TTSB_PACKAGE_NAME, Helpers.TTSB_PREFERENCES);

				if (Settings.Loader.contains(XsPref, packageName, activityFullName)) {
					Settings.Parser settings = Settings.Loader.load((SharedPreferences) XsPref, packageName, activityFullName);
					de.robv.android.xposed.XposedBridge.log(">TTSB: [ INFO: ] Code is: " + settings.getLine());
					setEverything(currentActivity, settings.getSetting());
				} else if (Settings.Loader.containsAll(XsPref, packageName)) {
					Settings.Parser settings = Settings.Loader.loadAll((SharedPreferences) XsPref, packageName);
					de.robv.android.xposed.XposedBridge.log(">TTSB: [ INFO: ] Code is: " + settings.getLine());
					setEverything(currentActivity, settings.getSetting());
				}
				
				de.robv.android.xposed.XposedBridge.log(">TTSB: [SUCCESS] Set tint and translucency, everything should be working here.");
				
				

				//de.robv.android.xposed.XposedBridge.log(">TTSB: [SUCCESS] Layout should now be adjusted.");
			}
		});

		findAndHookMethod(ActivityClass, "performResume", new XC_MethodHook() {
			@Override
			protected void afterHookedMethod(MethodHookParam param) throws Throwable {
				Object currentObj = param.thisObject;
				Activity currentActivity;
				if (currentObj instanceof Activity) {
					//de.robv.android.xposed.XposedBridge.log(">TTSB: [SUCCESS] The created object is an activity. Got the instance. Proceed.");
					currentActivity = (Activity) currentObj;
				}
				else {
					//de.robv.android.xposed.XposedBridge.log(">TTSB: [ ERROR ] The created object is not an activity. Return.");
					return;
				}
				
				XSharedPreferences XsPref = new XSharedPreferences(Helpers.TTSB_PACKAGE_NAME, Helpers.TTSB_PREFERENCES);
				
				boolean showToast = XsPref.getBoolean(Helpers.TTSB_PREF_SHOW_ACTIVITY_TOAST, false);
				if (showToast) {
					Toast toast = Toast.makeText(currentActivity.getApplicationContext(), "Current Activity:\n" + currentActivity.getPackageName() + "." + currentActivity.getLocalClassName(), Toast.LENGTH_SHORT);
					toast.show();
				}
			}
		});
		
		findAndHookMethod(ActivityClass, "onConfigurationChanged", Configuration.class, new XC_MethodHook() {
			@Override
			protected void afterHookedMethod(MethodHookParam param) throws Throwable {
				if (param.args.length == 0 ||
						!(param.args[0] instanceof Configuration) ||
						!(((Configuration) param.args[0]).orientation == Configuration.ORIENTATION_LANDSCAPE) ||
						!(((Configuration) param.args[0]).orientation == Configuration.ORIENTATION_PORTRAIT)) {
					de.robv.android.xposed.XposedBridge.log("config changed");
					return;
				}
				
			}
		});
	}
	
	/**
	 * Sets all TTSB settings in the Settings Parser object to the chosen Activity.
	 * @param currentActivity The Activity to apply the settings to.
	 * @param settings The settings to apply.
	 */
	public void setEverything(Activity currentActivity, Settings.Setting settings) {
		SystemBarTintManager tintMan = null;
		if (settings.status) Helpers.setTranslucentStatus(currentActivity, true);
		if (settings.nav) Helpers.setTranslucentNavigation(currentActivity, true);
		if (settings.status || settings.nav) {
			tintMan = new SystemBarTintManager(currentActivity);
		}
		if (tintMan != null) {
			tintMan.setStatusBarTintEnabled(settings.status);
			tintMan.setNavigationBarTintEnabled(settings.nav);
			
			tintMan.setStatusBarTintColor(settings.s_color);
			tintMan.setNavigationBarTintColor(settings.n_color);
			
			if (settings.s_plus != 0) {
				LayoutParams s_params = tintMan.mStatusBarTintView.getLayoutParams();
				s_params.height += settings.s_plus;
				tintMan.mStatusBarTintView.setLayoutParams(s_params);
			}
			if (settings.n_plus != 0) {
				LayoutParams n_params = tintMan.mNavBarTintView.getLayoutParams();
				n_params.height += settings.n_plus;
				tintMan.mNavBarTintView.setLayoutParams(n_params);
			}
		}
		
		boolean landscape = (currentActivity.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE);
		
		ViewGroup content = (ViewGroup) currentActivity.findViewById(android.R.id.content);
		ViewGroup cview = (ViewGroup) content.getChildAt(0);
		ViewGroup decview = (ViewGroup) currentActivity.getWindow().getDecorView();
		
		if (settings.cview != null) {
			setViewSettings(cview, settings.cview, landscape, tintMan);
		}
		if (settings.content != null) {
			setViewSettings(content, settings.content, landscape, tintMan);
		}
		if (settings.decview != null) {
			setViewSettings(decview, settings.decview, landscape, tintMan);
		}
		
		if (settings.view == null) return;
		for (int i = 0; i < settings.view.size(); i++) {
			if (settings.view.get(i).levels == 0) continue;
			setViewSettingsPack(cview, content, decview, settings.view.get(i), landscape, tintMan);
		}
	}
	
	/**
	 * Sets all TTSB layout options from a ViewSettingsPack in an Activity.
	 * ViewSettingsPack contains how to find the View in the Activity, and a ViewSettings object that will be applied to this View. 
	 * @param currentActivity The Activity containing the View.
	 * @param vsetpk The options to apply.
	 * @param landscape Set 'true' if screen orientation is landscape.
	 */
	public void setViewSettingsPack(ViewGroup cview, ViewGroup content, ViewGroup decview, Settings.Setting.ViewSettingsPack vsetpk, boolean landscape, SystemBarTintManager tintMan) {
		ViewGroup view;
		switch (vsetpk.from) {
		case Settings.Setting.ViewSettingsPack.FROM_DECVIEW:
			view = decview;
			break;
		case Settings.Setting.ViewSettingsPack.FROM_CVIEW:
			view = cview;
			break;
		case Settings.Setting.ViewSettingsPack.FROM_CONTENT:
			view = content;
			break;
		default:
			return;
		}
		
		if (vsetpk.levels > 0) {
			boolean seperatechilds = false;
			int all_index = 0;
			if (vsetpk.childindexes.length > 1) seperatechilds = true;
			else if (vsetpk.childindexes.length > 0) all_index = vsetpk.childindexes[0];
			for (int i = 1; i <= vsetpk.levels; i++) {
				int childindex = 0;
				if (!seperatechilds) childindex = all_index;
				else if (vsetpk.childindexes.length >= i) childindex = vsetpk.childindexes[i - 1];
				
				View v = view.getChildAt(childindex);
				if (v instanceof ViewGroup) view = (ViewGroup) v;
				else break;
			}
		}
		else if (vsetpk.levels < 0) {
			for (int i = 1; i <= -vsetpk.levels; i++) {
				ViewParent v = view.getParent();
				if (v != null && v instanceof ViewGroup) view = (ViewGroup) v;
				else break;
			}
		}
		
		setViewSettings(view, vsetpk.settings, landscape, tintMan);
	}
	
	/**
	 * Sets all TTSB layout options from the ViewSettings to the specified View.
	 * @param view The View to apply the settings to.
	 * @param vset The options to apply.
	 * @param landscape Set 'true' if screen orientation is landscape.
	 */
	public void setViewSettings(ViewGroup view, Settings.Setting.ViewSettings vset, boolean landscape, SystemBarTintManager tintMan) {
		if (vset.if_land && !landscape || view == null) return;
		
		if (vset.setFSW) view.setFitsSystemWindows(vset.setFSW_value);
		if (vset.setCTP) view.setClipToPadding(vset.setCTP_value);
		
		if (vset.padding != null) {
			int left;
			int top;
			int right;
			int bottom;
			
			final float scale = view.getResources().getDisplayMetrics().density;
			
			left = (int) (vset.padding.left * scale + 0.5f);
			top = (int) (vset.padding.top * scale + 0.5f);
			right = (int) (vset.padding.right * scale + 0.5f);
			bottom = (int) (vset.padding.bottom * scale + 0.5f);
			
			if (tintMan != null) {
				SystemBarTintManager.SystemBarConfig config = tintMan.getConfig();
				if (vset.padding.plus_status_h)
					top += config.getStatusBarHeight();
				if (vset.padding.plus_actionbar_h)
					top += config.getActionBarHeight();
				if (vset.padding.plus_nav_w)
					right += config.getNavigationBarWidth();
				if (vset.padding.plus_nav_h)
					bottom += config.getNavigationBarHeight();
			}
			
			view.setPadding(view.getPaddingLeft() + left, view.getPaddingTop() + top, view.getPaddingRight() + right, view.getPaddingBottom() + bottom);
			
			de.robv.android.xposed.XposedBridge.log(">TTSB: [ INFO: ] Padding: " + String.valueOf(left) + "," + String.valueOf(top) + "," + String.valueOf(right) + "," + String.valueOf(bottom) + ".");
			
		}
	}

}
