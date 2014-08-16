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
import java.util.SortedMap;
import java.util.TimeZone;
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

import com.woalk.apps.xposed.ttsb.Helpers;
import com.woalk.apps.xposed.ttsb.R;
import com.woalk.apps.xposed.ttsb.Settings;

import android.R.color;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class OneSubmitActivity extends Activity {
	public static final String PASS_APP = Helpers.TTSB_PACKAGE_NAME + ".community.OneSubmitActivity.PASS_APP"; // Parcelable
	public static final String PASS_DESCR = Helpers.TTSB_PACKAGE_NAME + ".community.OneSubmitActivity.PASS_DESCR";
	public static final String PASS_ID = Helpers.TTSB_PACKAGE_NAME + ".community.OneSubmitActivity.PASS_ID";
	public static final String PASS_IS_TOPVOTE = Helpers.TTSB_PACKAGE_NAME + ".community.OneSubmitActivity.PASS_IS_TOPVOTE";
	public static final String PASS_VOTES = Helpers.TTSB_PACKAGE_NAME + ".community.OneSubmitActivity.PASS_VOTES";
	public static final String PASS_VERSION = Helpers.TTSB_PACKAGE_NAME + ".community.OneSubmitActivity.PASS_VERSION";
	public static final String PASS_INSTALLED = Helpers.TTSB_PACKAGE_NAME + ".community.OneSubmitActivity.PASS_INSTALLED";
	public static final String PASS_USER = Helpers.TTSB_PACKAGE_NAME + ".community.OneSubmitActivity.PASS_USER";
	public static final String PASS_USER_TRUST = Helpers.TTSB_PACKAGE_NAME + ".community.OneSubmitActivity.PASS_USER_TRUST";
	public static final String PASS_TIMESTAMP = Helpers.TTSB_PACKAGE_NAME + ".community.OneSubmitActivity.PASS_TIMESTAMP"; // Serializable
	public static final String PASS_SETTINGS = Helpers.TTSB_PACKAGE_NAME + ".community.OneSubmitActivity.PASS_SETTINGS";
	
	protected ApplicationInfo app;
	protected String description;
	protected int id;
	protected boolean is_topvote;
	protected int votes;
	protected int version;
	protected boolean installed;
	protected String user;
	protected boolean user_trust;
	protected Date timestamp;
	protected SortedMap<String, String> settings;
	
	protected ListView lv;
	protected SubmitCommentsAdapter lA;
	
	protected EditText edit_comment;
	protected TextView tv_comment_charremain;
	protected ImageButton btn_send;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_one_submit);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		Intent it = getIntent();
		app = (ApplicationInfo) it.getParcelableExtra(PASS_APP);
		description = it.getStringExtra(PASS_DESCR);
		id = it.getIntExtra(PASS_ID, -1);
		is_topvote = it.getBooleanExtra(PASS_IS_TOPVOTE, false);
		votes = it.getIntExtra(PASS_VOTES, 0);
		version = it.getIntExtra(PASS_VERSION, 0);
		installed = it.getBooleanExtra(PASS_INSTALLED, false);
		user = it.getStringExtra(PASS_USER);
		user_trust = it.getBooleanExtra(PASS_USER_TRUST, false);
		timestamp = (Date) it.getSerializableExtra(PASS_TIMESTAMP);
		settings = Settings.Loader.importStringToSettingsString(it.getStringExtra(PASS_SETTINGS));
		
		lA = new SubmitCommentsAdapter(this, new ArrayList<String>());
		lA.app = app;
		lA.author = user;
		lA.author_trust = user_trust;
		lA.id = id;
		lA.is_topvote = is_topvote;
		lA.description = description;
		lA.installed = installed;
		lA.settings = settings;
		lA.timestamp = timestamp;
		lA.votes = votes;
		lA.addBegin();
		
		lv = (ListView) findViewById(R.id.listView1);
		lv.setAdapter(lA);
		
		lA.notifyDataSetChanged();
		
		lv.setOnItemClickListener(new ListView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				final int pos = position;
				AlertDialog.Builder builder = new AlertDialog.Builder(OneSubmitActivity.this);
				builder.setTitle(R.string.title_comment_options);
				builder.setItems(R.array.comment_options, new AlertDialog.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						switch (which) {
						case 0:
							ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE); 
							ClipData clip = ClipData.newPlainText("comment", lA.comments.get(pos));
							clipboard.setPrimaryClip(clip);
							break;
						case 1:
							
							break;
						}
					}
				});
				builder.show();
			}
		});
		
		new getCommentsTask().execute(Database.DATABASE_URL);
		
		edit_comment = (EditText) findViewById(R.id.editText1);
		tv_comment_charremain = (TextView) findViewById(R.id.textView1);
		btn_send = (ImageButton) findViewById(R.id.button_send);
		
		edit_comment.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				int length = s.length();
				tv_comment_charremain.setText(String.valueOf(Database.Constants.COMMENT_MAX_LENGTH - length));
				boolean isLengthOk = Database.Constants.COMMENT_MAX_LENGTH >= length;
				tv_comment_charremain.setTextColor(isLengthOk ? getResources().getColor(color.darker_gray) : getResources().getColor(color.holo_red_light));
				btn_send.setEnabled(isLengthOk);
				btn_send.setImageAlpha(isLengthOk ? 0xFF : 0x60);
			}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
			@Override
			public void afterTextChanged(Editable s) { }
		});
		edit_comment.setText("");
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    // Respond to the action bar's Up/Home button
	    case android.R.id.home:
	    	finish();
	        return true;
	    }
	    return super.onOptionsItemSelected(item);
	}
	
	
	private class getCommentsTask extends AsyncTask<String, Comment, Void> {
		@Override
		protected void onPreExecute() {
			lA.isLoading = true;
			lA.notifyDataSetChanged();
		}

		@SuppressLint("SimpleDateFormat")
		@Override
		protected Void doInBackground(String... params) {
			String result = "";
			try {
				// get database connection & package entries
				HttpClient httpclient = new DefaultHttpClient();
				HttpPost httppost = new HttpPost(params[0]);
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
				nameValuePairs.add(new BasicNameValuePair(Database.POST_PIN, Database.COMMUNITY_PIN));
				nameValuePairs.add(new BasicNameValuePair(Database.POST_FUNCTION, Database.FUNCTION_GET_COMMENTS_FOR_SUBMIT));
				nameValuePairs.add(new BasicNameValuePair(Database.POST_COMMENTS_SUBMIT, String.valueOf(id)));
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
					DateFormat d_f = new SimpleDateFormat("yyyy-MM-dd-HH-mm");
					d_f.setTimeZone(TimeZone.getTimeZone("GMT"));
					Date timestamp = d_f.parse(json_data.getString("timestamp"));
					publishProgress(new Comment(
							Integer.valueOf(json_data.getString("id")),
							json_data.getString("comment"),
							json_data.getString("username"),
							timestamp,
							json_data.getString("user_trust").equals("1"),
							Integer.valueOf(json_data.getString("spamvotes"))));
				}
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
			return null;
		}
		
		@Override
		protected void onProgressUpdate(Comment... progress) {
			for (Comment c : progress) {
				lA.add(c);
				lA.notifyDataSetChanged();
			}
		}
		
		@Override
		protected void onPostExecute(Void result) {
			lA.isLoading = false;
			lA.notifyDataSetChanged();
		}
	}
	
	protected static class Comment implements Parcelable {
		public int id;
		public String comment;
		public String user;
		public Date timestamp;
		public boolean user_trust;
		public int spamvotes;
		
		public Comment(int id, String comment, String username, Date timestamp, boolean user_trust, int spamvotes) {
			this.id = id;
			this.comment = comment;
			this.user = username;
			this.timestamp = timestamp;
			this.user_trust = user_trust;
			this.spamvotes = spamvotes;
		}

		public Comment(Parcel from) {
			this.id = from.readInt();
			this.comment = from.readString();
			this.user = from.readString();
			this.timestamp = (Date) from.readSerializable();
			this.user_trust = from.readByte() == (byte) 1;
			this.spamvotes = from.readInt();
		}

		@Override
		public int describeContents() {
			return 0;
		}

		@Override
		public void writeToParcel(Parcel dest, int flags) {
			dest.writeInt(id);
			dest.writeString(comment);
			dest.writeString(user);
			dest.writeSerializable(timestamp);
			dest.writeByte((user_trust ? (byte) 1 : (byte) 0));
			dest.writeInt(spamvotes);
		}
	}
}
