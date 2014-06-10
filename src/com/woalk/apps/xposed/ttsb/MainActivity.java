package com.woalk.apps.xposed.ttsb;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MainActivity extends Activity {
	
	private PackageManager pkgMan;
	public List<PackageInfo> installedPkg;
	protected SharedPreferences sPref;
	protected List<ActivityInfo> loadedActivities = new ArrayList<ActivityInfo>();
	
	private ListView listView1;
	private ArrayAdapter<String> arrAdapter;
	private ArrayList<String> arr_activities = new ArrayList<String>();

	@SuppressLint("WorldReadableFiles")
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		pkgMan = getPackageManager();
		installedPkg = pkgMan.getInstalledPackages(PackageManager.GET_ACTIVITIES);
		sPref = getApplicationContext().getSharedPreferences(Helpers.TTSB_PREFERENCES, Context.MODE_WORLD_READABLE);
		
		listView1 = (ListView) findViewById(R.id.listView1);
		
		listView1.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				launchDetails(loadedActivities.get(position));
			}
		});
		
		arrAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, arr_activities);
		listView1.setAdapter(arrAdapter);
	}
	
	@Override
	protected void onResume() {
		reload();
		super.onResume();
	}
	
	protected void reload() {
		loadedActivities.clear();
		arr_activities.clear();
		Map<String, ?> sPrefAll = sPref.getAll();
		if (sPrefAll != null) {
			for (Map.Entry<String, ?> entry : sPrefAll.entrySet()) {
				if (!entry.getKey().endsWith("+s")) {
					String pkgName = sPref.getString(entry.getKey() + "+p", null);
					if (pkgName != null) {
						String activityClass = entry.getKey();
						ComponentName cName = new ComponentName(pkgName, activityClass);
						ActivityInfo entryInfo;
						try {
							entryInfo = pkgMan.getActivityInfo(cName, 0);
						} catch (NameNotFoundException e) {
							e.printStackTrace();
							return;
						}
						loadedActivities.add(entryInfo);
						arr_activities.add(entry.getKey());
					}
				}
			}
			arrAdapter.notifyDataSetChanged();
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.main, menu);
	    return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle presses on the action bar items
	    switch (item.getItemId()) {
	        case R.id.action_add:
	            addItem();
	            return true;
	        case R.id.action_settings:
	        	launchSettings();
	        	return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
	protected void addItem() {
		String[] pkgNames = new String[installedPkg.size()];
		for (int i = 0; i < installedPkg.size(); i++) {
			pkgNames[i] = pkgMan.getApplicationLabel(installedPkg.get(i).applicationInfo) + " (" + installedPkg.get(i).packageName + ")";
		}
		
		Dialogs.SelectPkgDialog dialog1 = new Dialogs.SelectPkgDialog();
		dialog1.setPkgNames(pkgNames);
		dialog1.setOnClick(ocl1);
		dialog1.show(getFragmentManager(), "SelectPkg");
	}
	
	private DialogInterface.OnClickListener ocl1 = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			addItem_part2(which);
		}
	};
	
	ActivityInfo[] activities = null;
	
	private void addItem_part2(int result1) {
		activities = installedPkg.get(result1).activities;
		String[] activityNames = new String[activities.length];
		for (int i = 0; i < activities.length; i++) {
			activityNames[i] = activities[i].name;
		}
		
		Dialogs.SelectActivityDialog dialog2 = new Dialogs.SelectActivityDialog();
		dialog2.setPkgName(pkgMan.getApplicationLabel(installedPkg.get(result1).applicationInfo).toString());
		dialog2.setActivityNames(activityNames);
		dialog2.setOnClick(ocl2);
		dialog2.show(getFragmentManager(), "SelectActivity");
	}
	
	private DialogInterface.OnClickListener ocl2 = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			addItem_part3(which);
		}
	};
	
	private void addItem_part3(int result2) {
		launchDetails(activities[result2]);
		
		activities = null;
	}
	
	private void launchDetails(ActivityInfo activity) {
		Intent add_Intent = new Intent(this, DetailsActivity.class);
		add_Intent.putExtra(DetailsActivity.SEL_PACKAGE_ACTIVITY_INFO, activity);
		startActivity(add_Intent);
	}
	
	private void launchSettings() {
		Intent sett_Intent = new Intent(this, SettingsActivity.class);
		startActivity(sett_Intent);
	}
}
