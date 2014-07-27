package com.woalk.apps.xposed.ttsb.community;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

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

import com.woalk.apps.xposed.ttsb.Helpers;
import com.woalk.apps.xposed.ttsb.R;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager.NameNotFoundException;
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

public class AppDetailsActivity extends Activity {  
	public static final String PASS_APPINFO = Helpers.TTSB_PACKAGE_NAME + ".community.AppDetailsActivity.PASS_APPINFO";
	
	protected ApplicationInfo app;
	
	protected ListView lv;
	protected SubmitsAdapter lA;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_app_details);
		
		app = (ApplicationInfo) getIntent().getParcelableExtra(PASS_APPINFO);
		
		lv = (ListView) findViewById(R.id.listView1);
		lA = new SubmitsAdapter(this, new ArrayList<String>());
		lA.app = app;
		lv.setAdapter(lA);
		
		lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent intent = new Intent(AppDetailsActivity.this, OneSubmitActivity.class);
				intent.putExtra(OneSubmitActivity.PASS_APP, (Parcelable) app);
				intent.putExtra(OneSubmitActivity.PASS_DESCR, lA.descriptions.get(position));
				intent.putExtra(OneSubmitActivity.PASS_TIMESTAMP, lA.timestamps.get(position));
				intent.putExtra(OneSubmitActivity.PASS_ID, lA.ids.get(position));
				intent.putExtra(OneSubmitActivity.PASS_IS_TOPVOTE, position == 1);
				intent.putExtra(OneSubmitActivity.PASS_INSTALLED, lA.selected_pos + 1 == position);
				intent.putExtra(OneSubmitActivity.PASS_SETTINGS, lA.settings.get(position));
				intent.putExtra(OneSubmitActivity.PASS_USER, lA.users.get(position));
				intent.putExtra(OneSubmitActivity.PASS_USER_TRUST, lA.users_trust.get(position));
				intent.putExtra(OneSubmitActivity.PASS_VERSION, lA.versions.get(position));
				intent.putExtra(OneSubmitActivity.PASS_VOTES, lA.votes.get(position));
				startActivity(intent);
			}
		});
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		getSyncables();
	}

	protected void getSyncables() {
		new readDatabaseTask().execute("http://ext.woalk.de/ttsb_community/dboperations0200.php");
	}
	
	private class readDatabaseTask extends AsyncTask<String, String, SubmitsAdapter> {
		private AlertDialog progress;

		@Override
		protected void onPreExecute() {
			AlertDialog.Builder builder = new AlertDialog.Builder(AppDetailsActivity.this);
			builder.setMessage(R.string.loadingsync_msg);
			builder.setView(new ProgressBar(AppDetailsActivity.this));
			progress = builder.create();
			progress.show();
		}

		@SuppressLint("SimpleDateFormat")
		@Override
		protected SubmitsAdapter doInBackground(String... params) {
			List<Integer> ids = new ArrayList<Integer>();
			List<Integer> votes = new ArrayList<Integer>();
			List<String> descriptions = new ArrayList<String>();
			List<Integer> versions = new ArrayList<Integer>();
			List<String> users = new ArrayList<String>();
			List<Boolean> users_trust = new ArrayList<Boolean>();
			List<Date> timestamps = new ArrayList<Date>();
			List<String> settings = new ArrayList<String>();

			String result = "";
			int selected_pos = -1;
			try {
				HttpClient httpclient = new DefaultHttpClient();
				HttpPost httppost = new HttpPost(params[0]);
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
				nameValuePairs.add(new BasicNameValuePair(Database.POST_PIN, Database.COMMUNITY_PIN));
				nameValuePairs.add(new BasicNameValuePair(Database.POST_FUNCTION, Database.FUNCTION_GET_SUBMITS_FOR_PACKAGE));
				nameValuePairs.add(new BasicNameValuePair(Database.POST_SUBMITS_PACKAGE, app.packageName));
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
				int topvote_id = -1;
				SharedPreferences sPref = getApplicationContext().getSharedPreferences(Database.Preferences.COMMUNITY_PREF_NAME, Context.MODE_PRIVATE);
				if (sPref.getBoolean(Database.Preferences.PREF_PREFIX_IS_TOPVOTED_USED + app.packageName, false)) {
					topvote_id = sPref.getInt(Database.Preferences.PREF_PREFIX_USED_SUBMIT_ID + app.packageName, -1);
				}
				for (int i = 0; i < jArray.length(); i++) {
					JSONObject json_data = jArray.getJSONObject(i);
					Integer id = Integer.valueOf(json_data.getString("id"));
					ids.add(id);
					if (id.equals(topvote_id)) selected_pos = i;
					votes.add(Integer.valueOf(json_data.getString("votes")));
					descriptions.add(json_data.getString("description"));
					versions.add(Integer.valueOf(json_data.getString("version")));
					users.add(json_data.getString("username"));
					users_trust.add(json_data.getString("user_trust").equals("1"));
					DateFormat d_f = new SimpleDateFormat("yyyy-MM-dd-HH-mm");
					d_f.setTimeZone(TimeZone.getTimeZone("GMT"));
					Date timestamp = d_f.parse(json_data.getString("timestamp"));
					timestamps.add(timestamp);
					settings.add(json_data.getString("settings"));
				}
			} catch (UnknownHostException e) {
				publishProgress("404");
				return null;
			} catch (Exception e) {
				e.printStackTrace();
				publishProgress(result);
				return null;
			}
			
			SubmitsAdapter sA = new SubmitsAdapter(AppDetailsActivity.this, descriptions);
			sA.ids = ids;
			sA.versions = versions;
			sA.timestamps = timestamps;
			sA.users = users;
			sA.users_trust = users_trust;
			sA.settings = settings;
			sA.votes = votes;
			sA.selected_pos = selected_pos;
			return sA;
		}
		
		@Override
		protected void onProgressUpdate(String... progress) {
			if (progress[0] == "404")
				Toast.makeText(AppDetailsActivity.this, R.string.no_connection_e, Toast.LENGTH_LONG).show();
			else {
				AlertDialog.Builder builder = new AlertDialog.Builder(AppDetailsActivity.this);
				builder.setMessage(Html.fromHtml(progress[0]));
				builder.show();
			}
		}
		
		@Override
		protected void onPostExecute(SubmitsAdapter result) {
			if (result != null) {
				lA.descriptions.clear();
				lA.ids.clear();
				lA.timestamps.clear();
				lA.users.clear();
				lA.users_trust.clear();
				lA.settings.clear();
				lA.versions.clear();
				lA.votes.clear();
				
				lA.addBegin();
				
				lA.descriptions.addAll(result.descriptions);
				lA.ids.addAll(result.ids);
				lA.timestamps.addAll(result.timestamps);
				lA.users.addAll(result.users);
				lA.users_trust.addAll(result.users_trust);
				lA.settings.addAll(result.settings);
				lA.versions.addAll(result.versions);
				lA.votes.addAll(result.votes);
				
				lA.selected_pos = result.selected_pos;
				
				lA.notifyDataSetChanged();
				
				lv.setFastScrollEnabled(true);
			}
			progress.dismiss();
		}
	}
	
	boolean isFiltered = false;
	SubmitsAdapter backup;
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.appdetails, menu);
	    menu.findItem(R.id.action_show_only_current_ver).setVisible(!isFiltered);
	    menu.findItem(R.id.action_show_all).setVisible(isFiltered);
	    return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_show_only_current_ver:
			isFiltered = true;
			invalidateOptionsMenu();
			
			backup = new SubmitsAdapter(lA);
			int ver = -1;
			try {
				ver = getPackageManager().getPackageInfo(Helpers.TTSB_PACKAGE_NAME, 0).versionCode;
			} catch (NameNotFoundException e) { }
			for (int i = 1; i < lA.descriptions.size(); i++) {
				if (lA.versions.get(i) != ver) {
					lA.descriptions.remove(i);
					lA.timestamps.remove(i);
					lA.users.remove(i);
					lA.users_trust.remove(i);
					lA.versions.remove(i);
					lA.votes.remove(i);
					if (lA.selected_pos > i) lA.selected_pos--;
					else if (lA.selected_pos == i) lA.selected_pos = -1;
				}
			}
			
			lA.notifyDataSetChanged();
			
			return true;
		case R.id.action_show_all:
			isFiltered = false;
			invalidateOptionsMenu();
			
			lA.descriptions.clear();
			lA.timestamps.clear();
			lA.users.clear();
			lA.users_trust.clear();
			lA.versions.clear();
			lA.votes.clear();
			
			lA.descriptions.addAll(backup.descriptions);
			lA.timestamps.addAll(backup.timestamps);
			lA.users.addAll(backup.users);
			lA.users_trust.addAll(backup.users_trust);
			lA.versions.addAll(backup.versions);
			lA.votes.addAll(backup.votes);
			
			lA.selected_pos = backup.selected_pos;
			
			lA.notifyDataSetChanged();
			
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
}
