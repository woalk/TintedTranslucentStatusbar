package com.woalk.apps.xposed.ttsb;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;

public class SettingsFragment extends PreferenceFragment {

	@SuppressLint({ "WorldReadableFiles", "SimpleDateFormat" })
	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		getPreferenceManager().setSharedPreferencesMode(
				Context.MODE_WORLD_READABLE);
		getPreferenceManager().setSharedPreferencesName(Helpers.TTSB_PREFERENCES);
		addPreferencesFromResource(R.xml.preferences);

		PackageInfo pInfo = null;
		try {
			pInfo = getActivity().getPackageManager().getPackageInfo(
					getActivity().getPackageName(),
					PackageManager.GET_META_DATA);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		if (pInfo != null) {

			String te_ver = "";
			String ui_ver = "";
			try {
				te_ver = pInfo.applicationInfo.metaData
						.getString("tintengine_version");
				ui_ver = pInfo.applicationInfo.metaData.getString("ui_version");
			} catch (Throwable e) {
				e.printStackTrace();
			}

			findPreference("main_ver").setSummary(
					getResources().getString(R.string.pref_version_string,
							pInfo.versionName));

			findPreference("te_ver").setSummary(
					getResources().getString(R.string.pref_version_string,
							te_ver));

			findPreference("ui_ver").setSummary(
					getResources().getString(R.string.pref_version_string,
							ui_ver));
		}

		findPreference("ad_translucentstyle").setOnPreferenceClickListener(
				new Preference.OnPreferenceClickListener() {
					@Override
					public boolean onPreferenceClick(Preference preference) {
						Intent browserIntent = new Intent(
								Intent.ACTION_VIEW,
								Uri.parse("http://repo.xposed.info/module/com.woalk.apps.xposed.translucentstyle"));
						startActivity(browserIntent);
						return true;
					}
				});
	}
}
