package com.woalk.apps.xposed.ttsb.community;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.woalk.apps.xposed.ttsb.Helpers;
import com.woalk.apps.xposed.ttsb.R;
import com.woalk.apps.xposed.ttsb.Settings;
import com.woalk.apps.xposed.ttsb.community.SQL_Operations.Q;

public class MyAppsActivity extends Activity {
	public static final String PASS_ALL_APPS = Helpers.TTSB_PACKAGE_NAME
			+ ".community.MyAppsActivity.PASS_ALL_APPS";

	protected ListView lv;
	protected MyAppsAdapter lA;

	protected List<ApplicationInfo> all_apps;
	protected SortedMap<String, ApplicationInfo> all_apps_p = null;

	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_apps);

		Serializable intentextra = getIntent().getSerializableExtra(
				PASS_ALL_APPS);
		all_apps = (List<ApplicationInfo>) intentextra;

		lv = (ListView) findViewById(R.id.listView1);
		lA = new MyAppsAdapter(this, new ArrayList<ApplicationInfo>(),
				new ArrayList<Boolean>(), new ArrayList<Boolean>());
		lv.setAdapter(lA);

		lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				openAppDetails(lA.apps.get(position));
			}
		});

		if (Submitter.getSavedAccount(this) == null
				&& !Submitter.isAccountDialogDismissed(this)) {
			new Submitter.SignInDialog(this).show();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		getSyncables();
	}

	private AlertDialog progress;

	protected void getSyncables() {
		Q q = new Q(Database.DATABASE_URL);
		q.setPreExecuteListener(new Q.PreExecuteListener() {

			@Override
			public void onPreExecute() {
				AlertDialog.Builder builder = new AlertDialog.Builder(
						MyAppsActivity.this);
				builder.setMessage(R.string.loadingsync_msg);
				builder.setView(new ProgressBar(MyAppsActivity.this));
				progress = builder.create();
				progress.show();
			}
		});
		final String key_apps = "apps";
		final String key_packageNames = "pkgs";
		final String key_is_set = "isset";
		final String key_has_update = "update";
		q.setDataLoadedListener(new Q.DataLoadedListener() {

			@Override
			public Bundle onDataLoaded(JSONArray data) throws JSONException {
				if (all_apps_p == null) {
					all_apps_p = new TreeMap<String, ApplicationInfo>();
					for (ApplicationInfo app : all_apps) {
						all_apps_p.put(app.packageName, app);
					}
				}

				ArrayList<ApplicationInfo> apps = new ArrayList<ApplicationInfo>();
				ArrayList<String> packageNames = new ArrayList<String>();
				ArrayList<Integer> is_set = new ArrayList<Integer>();
				ArrayList<Integer> has_update = new ArrayList<Integer>();

				try {
					for (int i = 0; i < data.length(); i++) {
						JSONObject json_data = data.getJSONObject(i);
						String packageName = json_data.getString("package");
						if (packageNames.contains(packageName)
								|| !all_apps_p.containsKey(packageName))
							continue;
						packageNames.add(packageName);
						apps.add(all_apps_p.get(packageName));
						boolean is_current_set = Settings.Loader
								.containsPackage(getApplicationContext(),
										packageName);
						boolean has_current_update = false;
						if (is_current_set) {
							SharedPreferences communityPref = getApplicationContext()
									.getSharedPreferences(
											Database.Preferences.COMMUNITY_PREF_NAME,
											Context.MODE_PRIVATE);
							if (communityPref
									.getBoolean(
											Database.Preferences.PREF_PREFIX_IS_TOPVOTED_USED
													+ packageName, false)) {
								int topvoted_id = communityPref
										.getInt(Database.Preferences.PREF_PREFIX_USED_SUBMIT_ID
												+ packageName, -1);
								has_current_update = topvoted_id != Integer
										.valueOf(json_data.getString("id"))
										&& topvoted_id != -1;
							}
						}
						is_set.add(is_current_set ? 1 : 0);
						has_update.add(has_current_update ? 1 : 0);
					}

					Bundle bundle = new Bundle();
					bundle.putParcelableArrayList(key_apps, apps);
					bundle.putStringArrayList(key_packageNames, packageNames);
					bundle.putIntegerArrayList(key_is_set, is_set);
					bundle.putIntegerArrayList(key_has_update, has_update);
					return bundle;
				} catch (Exception e) {
					e.printStackTrace();
					return null;
				}
			}
		});
		q.setPostExecuteListener(new Q.PostExecuteListener() {

			@Override
			public void onPostExecute(Bundle processed) {
				if (processed != null) {
					lA.apps.clear();
					lA.is_set.clear();
					lA.has_update.clear();

					ArrayList<ApplicationInfo> apps = processed
							.getParcelableArrayList(key_apps);
					lA.apps.addAll(apps);
					for (Integer i : processed.getIntegerArrayList(key_is_set)) {
						lA.is_set.add(i == 1);
					}
					for (Integer i : processed
							.getIntegerArrayList(key_has_update)) {
						lA.has_update.add(i == 1);
					}

					lA.notifyDataSetChanged();

					lv.setFastScrollEnabled(true);
				}
				progress.dismiss();
			}
		});
		q.addNameValuePair(Database.POST_PIN, Database.COMMUNITY_PIN);
		q.addNameValuePair(Database.POST_FUNCTION,
				Database.FUNCTION_GET_PACKAGES);
		q.exec();
	}

	protected void openAppDetails(ApplicationInfo app) {
		Intent intent = new Intent(this, AppDetailsActivity.class);
		intent.putExtra(AppDetailsActivity.PASS_APPINFO, (Parcelable) app);
		startActivity(intent);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.myapps, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@SuppressLint("InflateParams")
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_goto_user:
			LayoutInflater inflater = getLayoutInflater();
			final View v = inflater.inflate(R.layout.goto_user, null);
			new AlertDialog.Builder(this)
					.setTitle(R.string.str_action_goto_user)
					.setView(v)
					.setPositiveButton(android.R.string.yes,
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									EditText editTextUser = (EditText) v
											.findViewById(R.id.editText1);
									String user = editTextUser.getText()
											.toString();
									// show user details
									Intent intent = new Intent(
											MyAppsActivity.this,
											OneUserActivity.class);
									intent.putExtra(
											OneUserActivity.PASS_USERNAME, user);
									startActivity(intent);
								}
							}).setNegativeButton(android.R.string.no, null)
					.show();
			return true;
		case R.id.action_goto_my:

			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
}
