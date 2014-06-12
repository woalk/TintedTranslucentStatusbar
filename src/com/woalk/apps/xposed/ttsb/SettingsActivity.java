package com.woalk.apps.xposed.ttsb;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

public class SettingsActivity extends Activity {

	SharedPreferences sPref;
	
	@SuppressLint("WorldReadableFiles")
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
		
		PackageInfo pInfo = null;
		try {
			pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		if (pInfo != null) {
			String version = pInfo.versionName;
			TextView textView2 = (TextView) findViewById(R.id.textView2);
			textView2.setText(getString(R.string.version_prefix) + " " + version);
		}
		
		sPref = getApplicationContext().getSharedPreferences(Helpers.TTSB_PREFERENCES, Context.MODE_WORLD_READABLE);
		
		CheckBox checkBox1 = (CheckBox) findViewById(R.id.checkBox1);
		checkBox1.setChecked(sPref.getBoolean(Helpers.TTSB_SHOW_ACTIVITY_TOAST, false));
		checkBox1.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				SharedPreferences.Editor edit = sPref.edit();
				edit.putBoolean(Helpers.TTSB_SHOW_ACTIVITY_TOAST, isChecked);
				edit.commit();
			}
		});
		CheckBox checkBox2 = (CheckBox) findViewById(R.id.checkBox2);
		checkBox2.setChecked(sPref.getBoolean(Helpers.TTSB_OVERWRITE_EXISTING, false));
		checkBox2.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				SharedPreferences.Editor edit = sPref.edit();
				edit.putBoolean(Helpers.TTSB_OVERWRITE_EXISTING, isChecked);
				edit.commit();
			}
		});
	}
}
