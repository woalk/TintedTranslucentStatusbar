package com.woalk.apps.xposed.ttsb;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
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
		
		sPref = getApplicationContext().getSharedPreferences(Helpers.TTSB_PREFERENCES, Context.MODE_WORLD_READABLE);
		
		PackageInfo pInfo = null;
		try {
			pInfo = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_META_DATA);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		if (pInfo != null) {
			TextView textView2 = (TextView) findViewById(R.id.textView2);
			TextView textView4 = (TextView) findViewById(R.id.textView4);
			
			String te_ver, ui_ver;
			float te_ver_f = pInfo.applicationInfo.metaData.getFloat("tintengine_version");
			if (te_ver_f != 0) te_ver = String.valueOf(te_ver_f); else te_ver = pInfo.applicationInfo.metaData.getString("tintengine_version");
			float ui_ver_f = pInfo.applicationInfo.metaData.getFloat("ui_version");
			if (ui_ver_f != 0) ui_ver = String.valueOf(ui_ver_f); else ui_ver = pInfo.applicationInfo.metaData.getString("ui_version");
			
			textView2.setText(getString(R.string.version_prefix) + ":\n\n"
					+ getString(R.string.tintengine_ver_prefix) + ":\n" 
					+ getString(R.string.ui_ver_prefix) + ":\n"
					+ getString(R.string.database_prefix) + ":\n");
			textView4.setText(pInfo.versionName + "\n\n"
					+ te_ver + "\n"
					+ ui_ver + "\n"
					+ sPref.getString(Helpers.TTSB_PREF_LASTUPDATE, getString(R.string.never)));
		}
		
		CheckBox checkBox1 = (CheckBox) findViewById(R.id.checkBox_status);
		checkBox1.setChecked(sPref.getBoolean(Helpers.TTSB_PREF_SHOW_ACTIVITY_TOAST, false));
		checkBox1.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				SharedPreferences.Editor edit = sPref.edit();
				edit.putBoolean(Helpers.TTSB_PREF_SHOW_ACTIVITY_TOAST, isChecked);
				edit.commit();
			}
		});
	}
}
