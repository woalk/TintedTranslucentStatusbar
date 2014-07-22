package com.woalk.apps.xposed.ttsb.community;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import com.woalk.apps.xposed.ttsb.*;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.Html;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

public class MyAppsActivity extends Activity {
	public static final String PASS_ALL_APPS = Helpers.TTSB_PACKAGE_NAME + ".community.MyAppsActivity.PASS_ALL_APPS";
	
	protected ListView lv;
	protected MyAppsAdapter lA;
	
	protected List<ApplicationInfo> all_apps;
	protected SortedMap<String, ApplicationInfo> all_apps_p = null;

	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_apps);
		
		Serializable intentextra = getIntent().getSerializableExtra(PASS_ALL_APPS);
		all_apps = (List<ApplicationInfo>) intentextra;
		
		lv = (ListView) findViewById(R.id.listView1);
		lA = new MyAppsAdapter(this, new ArrayList<ApplicationInfo>(), new ArrayList<Boolean>(), new ArrayList<Boolean>());
		lv.setAdapter(lA);
		
		lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				openAppDetails(lA.apps.get(position));
			}
		});
		
		getSyncables();
	}

	protected void getSyncables() {
		new readDatabaseTask().execute("http://ext.woalk.de/ttsb_community/dboperations0200.php");
	}
	
	private class readDatabaseTask extends AsyncTask<String, String, MyAppsAdapter> {
		private AlertDialog progress;

		@Override
		protected void onPreExecute() {
			AlertDialog.Builder builder = new AlertDialog.Builder(MyAppsActivity.this);
			builder.setMessage(R.string.loadingsync_msg);
			builder.setView(new ProgressBar(MyAppsActivity.this));
			progress = builder.create();
			progress.show();
		}

		@Override
		protected MyAppsAdapter doInBackground(String... params) {
			if (all_apps_p == null) {
				all_apps_p = new TreeMap<String, ApplicationInfo>();
				for (ApplicationInfo app : all_apps) {
					all_apps_p.put(app.packageName, app);
				}
			}
			
			List<ApplicationInfo> apps = new ArrayList<ApplicationInfo>();
			List<Boolean> is_set = new ArrayList<Boolean>();
			List<Boolean> has_update = new ArrayList<Boolean>();

			String result = "";
			try {
				// get database connection & package entries
				HttpClient httpclient = new DefaultHttpClient();
				HttpPost httppost = new HttpPost(params[0]);
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
				nameValuePairs.add(new BasicNameValuePair(Database.POST_PIN, Database.COMMUNITY_PIN));
				nameValuePairs.add(new BasicNameValuePair(Database.POST_FUNCTION, Database.FUNCTION_GET_PACKAGES));
				httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
				HttpResponse response = httpclient.execute(httppost);
				HttpEntity entity = response.getEntity();
				
				InputStream is = entity.getContent();
				BufferedReader reader = new BufferedReader(new InputStreamReader(is, "utf-8"), 8);
				StringBuilder sb = new StringBuilder();
				String line = null;
				while ((line = reader.readLine()) != null) {
					sb.append(line + "\n");
				} 
				is.close();
				result = sb.toString();
				JSONArray jArray = new JSONArray(result);
				for (int i = 0; i < jArray.length(); i++) {
					JSONObject json_data = jArray.getJSONObject(i);
					String packageName = json_data.getString("package");
					apps.add(all_apps_p.get(packageName));
					boolean is_current_set = Settings.Loader.containsPackage(getApplicationContext(), packageName);
					boolean has_current_update = false;
					if (is_current_set) {
						SharedPreferences communityPref = getApplicationContext().getSharedPreferences(
								Database.Preferences.COMMUNITY_PREF_NAME, Context.MODE_PRIVATE);
						if (communityPref.getBoolean(Database.Preferences.PREF_PREFIX_IS_TOPVOTED_USED + packageName, false)) {
							int topvoted_id = communityPref.getInt(Database.Preferences.PREF_PREFIX_USED_SUBMIT_ID + packageName, -1);
							has_current_update = topvoted_id != Integer.valueOf(json_data.getString("id")) && topvoted_id != -1;
						}
					}
					is_set.add(is_current_set);
					has_update.add(has_current_update);
				}
			} catch (UnknownHostException e) {
				publishProgress("404");
				return null;
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
			
			return new MyAppsAdapter(MyAppsActivity.this, apps, is_set, has_update);
		}
		
		@Override
		protected void onProgressUpdate(String... progress) {
			if (progress[0] == "404")
				Toast.makeText(MyAppsActivity.this, R.string.no_connection_e, Toast.LENGTH_LONG).show();
			else {
				AlertDialog.Builder builder = new AlertDialog.Builder(MyAppsActivity.this);
				builder.setMessage(Html.fromHtml(progress[0]));
				builder.show();
			}
		}
		
		@Override
		protected void onPostExecute(MyAppsAdapter result) {
			if (result != null) {
				lA.apps.clear();
				lA.is_set.clear();
				lA.has_update.clear();
				
				lA.apps.addAll(result.apps);
				lA.is_set.addAll(result.is_set);
				lA.has_update.addAll(result.has_update);
				
				lA.notifyDataSetChanged();
				
				lv.setFastScrollEnabled(true);
			}
			progress.dismiss();
		}

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
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_goto_user:
			
			return true;
		case R.id.action_goto_my:
			
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
}
