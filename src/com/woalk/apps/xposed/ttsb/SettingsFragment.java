package com.woalk.apps.xposed.ttsb;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

public class SettingsFragment extends PreferenceFragment {

	SharedPreferences sPref;

	@SuppressLint({ "WorldReadableFiles", "SimpleDateFormat" })
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);

		sPref = getApplicationContext().getSharedPreferences(
				Helpers.TTSB_PREFERENCES, Context.MODE_WORLD_READABLE);

		PackageInfo pInfo = null;
		try {
			pInfo = getPackageManager().getPackageInfo(getPackageName(),
					PackageManager.GET_META_DATA);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		if (pInfo != null) {
			TextView textView2 = (TextView) findViewById(R.id.textView2);
			TextView textView4 = (TextView) findViewById(R.id.textView4);

			String te_ver = "";
			String ui_ver = "";
			try {
				te_ver = pInfo.applicationInfo.metaData
						.getString("tintengine_version");
				ui_ver = pInfo.applicationInfo.metaData.getString("ui_version");
			} catch (Throwable e) {
				e.printStackTrace();
			}

			textView2.setText(getString(R.string.version_prefix) + ":\n\n"
					+ getString(R.string.tintengine_ver_prefix) + ":\n"
					+ getString(R.string.ui_ver_prefix) + ":\n");
			textView4.setText(pInfo.versionName + "\n\n" + te_ver + "\n"
					+ ui_ver);
		}

		CheckBox checkBox1 = (CheckBox) findViewById(R.id.checkBox_status);
		checkBox1.setChecked(sPref.getBoolean(
				Helpers.TTSB_PREF_SHOW_ACTIVITY_TOAST, false));
		checkBox1.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				SharedPreferences.Editor edit = sPref.edit();
				edit.putBoolean(Helpers.TTSB_PREF_SHOW_ACTIVITY_TOAST,
						isChecked);
				edit.commit();
			}
		});
		CheckBox checkBox2 = (CheckBox) findViewById(R.id.checkBox_log);
		checkBox2.setChecked(sPref
				.getBoolean(Helpers.TTSB_PREF_DEBUGLOG, false));
		checkBox2.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				SharedPreferences.Editor edit = sPref.edit();
				edit.putBoolean(Helpers.TTSB_PREF_DEBUGLOG, isChecked);
				edit.commit();
			}
		});
		CheckBox checkBox3 = (CheckBox) findViewById(R.id.checkBox_immersive);
		checkBox3.setChecked(sPref.getBoolean(Helpers.TTSB_PREF_IMMERSIVE,
				true));
		checkBox3.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				SharedPreferences.Editor edit = sPref.edit();
				edit.putBoolean(Helpers.TTSB_PREF_IMMERSIVE, isChecked);
				edit.commit();
			}
		});

		findViewById(R.id.button1).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent browserIntent = new Intent(
						Intent.ACTION_VIEW,
						Uri.parse("http://repo.xposed.info/module/com.woalk.apps.xposed.translucentstyle"));
				startActivity(browserIntent);
			}
		});
	}
}
