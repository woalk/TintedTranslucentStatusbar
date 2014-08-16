package com.woalk.apps.xposed.ttsb;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import com.woalk.apps.xposed.ttsb.community.Database;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.ProgressBar;
import android.widget.Toast;
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

		app = (ApplicationInfo) getIntent().getParcelableExtra(
				APP_INFO_SELECTED);

		if (app == null) {
			finish();
			return;
		}

		this.setTitle(getString(R.string.title_activity_activities) + " "
				+ getPackageManager().getApplicationLabel(app));

		lv = (ListView) findViewById(R.id.listView1);
		lA = new ActivityListAdapter(context,
				new ArrayList<ComparableActivityInfo>());
		lv.setAdapter(lA);
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Intent intent = new Intent(context, EasySettingsActivity.class);
				intent.putExtra(EasySettingsActivity.ACTIVITY_INFO_SELECTED,
						lA.getItem(arg2));
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

	private class LoadActivitiesTask extends
			AsyncTask<Boolean, Integer, List<ComparableActivityInfo>> {
		@SuppressLint("WorldReadableFiles")
		@SuppressWarnings("deprecation")
		protected List<ComparableActivityInfo> doInBackground(Boolean... bools) {
			if (lA == null || lA.activities == null)
				return null;
			List<ComparableActivityInfo> list = new ArrayList<ComparableActivityInfo>();
			SharedPreferences sPref = context.getSharedPreferences(
					Helpers.TTSB_PREFERENCES, Context.MODE_WORLD_READABLE);
			ComparableActivityInfo all = new ComparableActivityInfo(
					new ActivityInfo());
			all.applicationInfo = app;
			all.name = "All";
			all.packageName = app.packageName;
			all.is_set = Settings.Loader.containsAll(sPref, app.packageName);
			list.add(all);
			PackageManager pkgMan = context.getPackageManager();
			ActivityInfo[] a;
			try {
				a = pkgMan.getPackageInfo(app.packageName,
						PackageManager.GET_ACTIVITIES).activities;
			} catch (NameNotFoundException e) {
				e.printStackTrace();
				return null;
			}
			for (int i = 0; i < a.length; i++) {
				ComparableActivityInfo ai = new ComparableActivityInfo(a[i]);
				if (ai.name != "All")
					ai.is_set = Settings.Loader.contains(sPref,
							app.packageName, ai.name);
				else
					ai.is_set = Settings.Loader.containsAll(sPref,
							app.packageName);
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
		case R.id.action_send_to_database:
			AlertDialog.Builder alert = new AlertDialog.Builder(this);
			alert.setTitle(R.string.suggest_qu_title);
			alert.setMessage(R.string.suggest_qu_msg);
			alert.setNegativeButton(android.R.string.no, null);
			alert.setPositiveButton(android.R.string.yes,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {
							submitToDB();
						}
					});
			alert.show();
			return true;
		case R.id.action_delete_package_settings:
			AlertDialog.Builder alert1 = new AlertDialog.Builder(this);
			alert1.setTitle(R.string.delete_pkg_title);
			alert1.setMessage(R.string.delete_pkg_msg);
			alert1.setNegativeButton(android.R.string.no, null);
			alert1.setPositiveButton(android.R.string.yes,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {
							Settings.Saver.deleteEverythingFromPackage(
									(Context) context, app.packageName);
							((Activity) context).finish();
							SharedPreferences sPref_c = context
									.getSharedPreferences(
											Database.Preferences.COMMUNITY_PREF_NAME,
											Context.MODE_PRIVATE);
							SharedPreferences.Editor edit = sPref_c.edit();
							edit.remove(Database.Preferences.PREF_PREFIX_IS_TOPVOTED_USED
									+ app.packageName);
							edit.remove(Database.Preferences.PREF_PREFIX_USED_SUBMIT_ID
									+ app.packageName);
							edit.apply();
						}
					});
			alert1.show();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	protected void submitToDB() {
		new SuggestTask()
				.execute("http://ext.woalk.de/ttsb_database/insertsuggestion.php");
	}

	private class SuggestTask extends AsyncTask<String, Void, HttpResponse> {
		private AlertDialog progress;

		protected void onPreExecute() {
			AlertDialog.Builder builder = new AlertDialog.Builder(context);
			builder.setMessage(R.string.suggestsync_msg);
			builder.setView(new ProgressBar(context));
			progress = builder.create();
			progress.show();
		}

		@SuppressLint("WorldReadableFiles")
		@SuppressWarnings("deprecation")
		protected HttpResponse doInBackground(String... params) {
			String activities = "";
			String settings = "";

			SharedPreferences sPref = context.getSharedPreferences(
					Helpers.TTSB_PREFERENCES, Context.MODE_WORLD_READABLE);

			for (int i = 0; i < lA.activities.size(); i++) {
				String activity = lA.activities.get(i).name;
				if (activity == "" || activity == null)
					continue;
				if (activity == "All")
					activity = "";

				Settings.Parser parser = Settings.Loader.loadWithNull(sPref,
						app.packageName, activity);
				if (parser != null) {
					activities += activity;
					settings += parser.getLine();

					if (i < (lA.activities.size() - 1)) {
						activities += "|";
						settings += "|";
					}
				}
			}

			try {
				HttpClient httpclient = new DefaultHttpClient();
				HttpPost httppost = new HttpPost(params[0]);
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
				nameValuePairs.add(new BasicNameValuePair("package",
						app.packageName));
				nameValuePairs.add(new BasicNameValuePair("activities",
						activities));
				nameValuePairs
						.add(new BasicNameValuePair("settings", settings));
				httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
				return httpclient.execute(httppost);
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}

		protected void onPostExecute(HttpResponse result) {
			if (result != null)
				Toast.makeText(context, R.string.settings_suggest_success,
						Toast.LENGTH_SHORT).show();
			progress.dismiss();
		}
	}

	@Override
	protected void onResume() {
		update();
		super.onResume();
	}
}
