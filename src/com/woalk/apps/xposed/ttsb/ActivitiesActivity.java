package com.woalk.apps.xposed.ttsb;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class ActivitiesActivity extends Activity {
	
	public static String APP_INFO_SELECTED = "com.woalk.apps.xposed.ttsb.APP_INFO_SELECTED";

	protected ListView lv;
	protected ActivityListAdapter lA;
	protected Activity context;
	
	protected ApplicationInfo app;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_activities);
		context = this;
		
		app = (ApplicationInfo) getIntent().getParcelableExtra(APP_INFO_SELECTED);
		
		if (app == null) { finish(); return; }
		
		this.setTitle(getString(R.string.title_activity_activities) + " " + getPackageManager().getApplicationLabel(app));
		
		lv = (ListView) findViewById(R.id.listView1);
		lA = new ActivityListAdapter(context, new ArrayList<ComparableActivityInfo>());
		lv.setAdapter(lA);
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				Intent intent = new Intent(context, EasySettingsActivity.class);
				intent.putExtra(EasySettingsActivity.ACTIVITY_INFO_SELECTED, lA.getItem(arg2));
				context.startActivity(intent);
			}
			
		});
		update();
	}
	
	/**
	 * Update the List of apps.
	 */
	public void update() {
		new LoadActivitiesTask().execute(true);
	}

	private class LoadActivitiesTask extends AsyncTask<Boolean, Integer, List<ComparableActivityInfo>> {
		@SuppressLint("WorldReadableFiles")
		@SuppressWarnings("deprecation")
		protected List<ComparableActivityInfo> doInBackground(Boolean... bools) {
			if (lA == null || lA.activities == null) return null;
			List<ComparableActivityInfo> list = new ArrayList<ComparableActivityInfo>();
			SharedPreferences sPref = context.getSharedPreferences(Helpers.TTSB_PREFERENCES, Context.MODE_WORLD_READABLE);
			ComparableActivityInfo all = new ComparableActivityInfo(new ActivityInfo());
			all.applicationInfo = app;
			all.name = "All";
			all.packageName = app.packageName;
			all.is_set = Settings.Loader.containsAll(sPref, app.packageName);
			list.add(all);
			PackageManager pkgMan = context.getPackageManager();
			ActivityInfo[] a;
			try {
				a = pkgMan.getPackageInfo(app.packageName, PackageManager.GET_ACTIVITIES).activities;
			} catch (NameNotFoundException e) {
				e.printStackTrace();
				return null;
			}
			for (int i = 0; i < a.length; i++) {
				ComparableActivityInfo ai = new ComparableActivityInfo(a[i]);
				if (ai.name != "All") ai.is_set = Settings.Loader.contains(sPref, app.packageName, ai.name);
				else ai.is_set = Settings.Loader.containsAll(sPref, app.packageName);
				list.add(ai);
			}
			Collections.sort(list);
			return list;
		}
		
		protected void onPostExecute(List<ComparableActivityInfo> result) {
			try {
				lA.activities.clear();
				lA.activities.addAll(result);
				lA.notifyDataSetChanged();
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.activities, menu);
	    return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle presses on the action bar items
	    switch (item.getItemId()) {
	    default:
	    	return super.onOptionsItemSelected(item);
	    }
	}
	
	@Override
	protected void onResume() {
		update();
		super.onResume();
	}
}
