package com.woalk.apps.xposed.ttsb;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.TreeMap;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.woalk.apps.xposed.ttsb.community.MyAppsActivity;

public class MainActivity extends Activity {

	protected ListView lv;
	protected AppListAdapter lA;
	protected Activity context;

	protected Settings.Setting s;

	protected MenuItem prog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		context = this;
		lv = (ListView) findViewById(R.id.listView1);
		lA = new AppListAdapter(context, new ArrayList<ApplicationInfo>(),
				new ArrayList<Boolean>(), new ArrayList<Drawable>());
		lv.setAdapter(lA);
		lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Intent intent = new Intent(context, ActivitiesActivity.class);
				intent.putExtra(ActivitiesActivity.APP_INFO_SELECTED,
						lA.getItem(arg2));
				context.startActivity(intent);
			}
		});
	}

	/**
	 * Update the List of apps.
	 */
	public void update() {
		new LoadAppsTask().execute(true);
	}

	private class LoadAppsTask extends
			AsyncTask<Boolean, Integer, AppListAdapter> {
		protected void onPreExecute() {
			if (prog != null)
				prog.setVisible(true);
		}

		@SuppressLint("WorldReadableFiles")
		@SuppressWarnings("deprecation")
		protected AppListAdapter doInBackground(Boolean... bools) {
			AppListAdapter lA1 = new AppListAdapter(context,
					new ArrayList<ApplicationInfo>(), new ArrayList<Boolean>(),
					new ArrayList<Drawable>());
			PackageManager pkgMan = context.getPackageManager();
			if (Helpers.pkgs == null) {
				Helpers.pkgs = pkgMan
						.getInstalledPackages(PackageManager.GET_ACTIVITIES);
			}
			for (int i = 0; i < Helpers.pkgs.size(); i++) {
				if (Helpers.pkgs.get(i).activities == null
						|| Helpers.pkgs.get(i).activities.length == 0)
					continue;
				lA1.apps.add(Helpers.pkgs.get(i).applicationInfo);
			}
			Collections.sort(lA1.apps,
					new ApplicationInfo.DisplayNameComparator(pkgMan));
			SharedPreferences sPref = context.getSharedPreferences(
					Helpers.TTSB_PREFERENCES, Context.MODE_WORLD_READABLE);
			TreeMap<String, ?> tree = new TreeMap<String, Object>(
					sPref.getAll());
			for (int i = 0; i < lA1.apps.size(); i++) {
				lA1.is_set.add(Settings.Loader.containsPackage(tree,
						lA1.apps.get(i).packageName));
				lA1.icons.add(pkgMan.getApplicationIcon(lA1.apps.get(i)));
			}
			return lA1;
		}

		protected void onPostExecute(AppListAdapter result) {
			lA.apps.clear();
			lA.is_set.clear();
			lA.apps.addAll(result.apps);
			lA.is_set.addAll(result.is_set);
			lA.icons.addAll(result.icons);
			lA.notifyDataSetChanged();
			lv.setFastScrollEnabled(true);
			if (prog != null)
				prog.setVisible(false);
		}
	}

	@Override
	protected void onResume() {
		update();
		super.onResume();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		prog = menu.getItem(0);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle presses on the action bar items
		switch (item.getItemId()) {
		case R.id.action_sync_with_database:
			launchSync();
			return true;
		case R.id.action_settings:
			launchSettings();
			return true;
		case R.id.action_update:
			update();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void launchSettings() {
		Intent sett_Intent = new Intent(this, SettingsActivity.class);
		startActivity(sett_Intent);
	}

	private void launchSync() {
		Intent sync_Intent = new Intent(this, MyAppsActivity.class);
		sync_Intent.putExtra(MyAppsActivity.PASS_ALL_APPS,
				(Serializable) lA.apps);
		startActivity(sync_Intent);
	}
}
