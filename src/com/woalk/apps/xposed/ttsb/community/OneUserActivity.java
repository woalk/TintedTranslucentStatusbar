package com.woalk.apps.xposed.ttsb.community;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.woalk.apps.xposed.ttsb.Helpers;
import com.woalk.apps.xposed.ttsb.R;
import com.woalk.apps.xposed.ttsb.community.SQL_Operations.Q;

public class OneUserActivity extends Activity {
	public static final String PASS_USERNAME = Helpers.TTSB_PACKAGE_NAME
			+ ".PASS_USERNAME";

	private String username;

	private ListView lv;
	private UserSubmitsAdapter lA;
	private AlertDialog progress;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_one_user);
		
		username = getIntent().getStringExtra(PASS_USERNAME);

		lv = (ListView) findViewById(R.id.listView1);
		lA = new UserSubmitsAdapter(this, new ArrayList<String>());
		lv.setAdapter(lA);

		Q q = new Q(Database.DATABASE_URL);
		q.setPreExecuteListener(new Q.PreExecuteListener() {

			@Override
			public void onPreExecute() {
				AlertDialog.Builder builder = new AlertDialog.Builder(
						OneUserActivity.this);
				builder.setMessage(R.string.loadingsync_msg);
				builder.setView(new ProgressBar(OneUserActivity.this));
				progress = builder.create();
				progress.show();
			}
		});
		final String key_ids = "ids";
		final String key_votes = "votes";
		final String key_apps = "apps";
		final String key_pkgs = "pkgs";
		final String key_descriptions = "descr";
		final String key_versions = "ver";
		final String key_user = "user";
		final String key_user_trust = "trust";
		final String key_user_votes = "uvotes";
		final String key_timestamps = "time";
		final String key_settings = "set";
		q.setDataLoadedListener(new Q.DataLoadedListener() {

			@SuppressLint("SimpleDateFormat")
			@Override
			public Bundle onDataLoaded(JSONArray data) throws JSONException {
				ArrayList<Integer> ids = new ArrayList<Integer>();
				ArrayList<Integer> votes = new ArrayList<Integer>();
				ArrayList<ApplicationInfo> apps = new ArrayList<ApplicationInfo>();
				ArrayList<String> pkgs = new ArrayList<String>();
				ArrayList<String> descriptions = new ArrayList<String>();
				ArrayList<Integer> versions = new ArrayList<Integer>();
				ArrayList<String> timestamps = new ArrayList<String>();
				ArrayList<String> settings = new ArrayList<String>();
				String user;
				boolean user_trust;
				int user_votes;

				user = data.getJSONObject(0).getString("username");
				user_trust = data.getJSONObject(0).getString("user_trust")
						.equals("1");
				user_votes = Integer.valueOf(data.getJSONObject(0).getString(
						"user_votes"));
				for (int i = 0; i < data.length(); i++) {
					JSONObject json_data = data.getJSONObject(i);
					Integer id = Integer.valueOf(json_data.getString("id"));
					ids.add(id);
					votes.add(Integer.valueOf(json_data.getString("votes")));
					descriptions.add(json_data.getString("description"));
					versions.add(Integer.valueOf(json_data.getString("version")));
					DateFormat d_f = new SimpleDateFormat("yyyy-MM-dd-HH-mm");
					d_f.setTimeZone(TimeZone.getTimeZone("GMT"));
					try {
						Date timestamp = d_f.parse(json_data
								.getString("timestamp"));
						DateFormat d_f_new = DateFormat.getInstance();
						String date = d_f_new.format(timestamp);
						timestamps.add(date);
					} catch (ParseException e) {
						timestamps.add("‹?›");
						e.printStackTrace();
					}
					settings.add(json_data.getString("settings"));
					String pkg = json_data.getString("package");
					try {
						apps.add(OneUserActivity.this.getPackageManager()
								.getApplicationInfo(pkg, 0));
					} catch (NameNotFoundException e) {
						apps.add(null);
					}
					pkgs.add(pkg);
				}

				Bundle bundle = new Bundle();
				bundle.putIntegerArrayList(key_ids, ids);
				bundle.putIntegerArrayList(key_votes, votes);
				bundle.putParcelableArrayList(key_apps, apps);
				bundle.putStringArrayList(key_pkgs, pkgs);
				bundle.putStringArrayList(key_descriptions, descriptions);
				bundle.putIntegerArrayList(key_versions, versions);
				bundle.putStringArrayList(key_timestamps, timestamps);
				bundle.putStringArrayList(key_settings, settings);
				bundle.putString(key_user, user);
				bundle.putBoolean(key_user_trust, user_trust);
				bundle.putInt(key_user_votes, user_votes);
				return bundle;
			}
		});
		q.setPostExecuteListener(new Q.PostExecuteListener() {
			
			@Override
			public void onPostExecute(Bundle processed) {
				lA.apps.clear();
				lA.descriptions.clear();
				lA.ids.clear();
				lA.pkgs.clear();
				lA.settings.clear();
				lA.timestamps.clear();
				lA.versions.clear();
				lA.votes.clear();

				ArrayList<ApplicationInfo> apps = processed
						.getParcelableArrayList(key_apps);
				lA.apps.addAll(apps);
				lA.descriptions.addAll(processed
						.getStringArrayList(key_descriptions));
				lA.ids.addAll(processed.getIntegerArrayList(key_ids));
				lA.pkgs.addAll(processed.getStringArrayList(key_pkgs));
				lA.settings.addAll(processed.getStringArrayList(key_settings));
				lA.timestamps.addAll(processed
						.getStringArrayList(key_timestamps));
				lA.versions.addAll(processed.getIntegerArrayList(key_versions));
				lA.votes.addAll(processed.getIntegerArrayList(key_votes));

				lA.username = processed.getString(key_user);
				lA.user_trust = processed.getBoolean(key_user_trust);
				lA.user_votes = processed.getInt(key_user_votes);

				lA.notifyDataSetChanged();
			}
		});
		q.addNameValuePair(Database.POST_PIN, Database.COMMUNITY_PIN);
		q.addNameValuePair(Database.POST_FUNCTION,
				Database.FUNCTION_GET_SUBMITS_BY_USER);
		q.addNameValuePair(Database.POST_USERNAME, username);
		q.exec();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.one_user, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return super.onOptionsItemSelected(item);
	}
}
