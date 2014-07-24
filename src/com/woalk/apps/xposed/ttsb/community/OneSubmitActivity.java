package com.woalk.apps.xposed.ttsb.community;

import java.util.ArrayList;
import java.util.Date;
import java.util.SortedMap;

import com.woalk.apps.xposed.ttsb.Helpers;
import com.woalk.apps.xposed.ttsb.R;
import com.woalk.apps.xposed.ttsb.Settings;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ListView;

public class OneSubmitActivity extends Activity {
	public static final String PASS_APP = Helpers.TTSB_PACKAGE_NAME + ".community.OneSubmitActivity.PASS_APP"; // Parcelable
	public static final String PASS_DESCR = Helpers.TTSB_PACKAGE_NAME + ".community.OneSubmitActivity.PASS_DESCR";
	public static final String PASS_VOTES = Helpers.TTSB_PACKAGE_NAME + ".community.OneSubmitActivity.PASS_VOTES";
	public static final String PASS_VERSION = Helpers.TTSB_PACKAGE_NAME + ".community.OneSubmitActivity.PASS_VERSION";
	public static final String PASS_INSTALLED = Helpers.TTSB_PACKAGE_NAME + ".community.OneSubmitActivity.PASS_INSTALLED";
	public static final String PASS_USER = Helpers.TTSB_PACKAGE_NAME + ".community.OneSubmitActivity.PASS_USER";
	public static final String PASS_USER_TRUST = Helpers.TTSB_PACKAGE_NAME + ".community.OneSubmitActivity.PASS_USER_TRUST";
	public static final String PASS_TIMESTAMP = Helpers.TTSB_PACKAGE_NAME + ".community.OneSubmitActivity.PASS_TIMESTAMP"; // Serializable
	public static final String PASS_SETTINGS = Helpers.TTSB_PACKAGE_NAME + ".community.OneSubmitActivity.PASS_SETTINGS";

	protected ApplicationInfo app;
	protected String description;
	protected int votes;
	protected int version;
	protected boolean installed;
	protected String user;
	protected boolean user_trust;
	protected Date timestamp;
	protected SortedMap<String, String> settings;
	
	protected ListView lv;
	protected SubmitCommentsAdapter lA;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_one_submit);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		Intent it = getIntent();
		app = (ApplicationInfo) it.getParcelableExtra(PASS_APP);
		description = it.getStringExtra(PASS_DESCR);
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
		lA.description = description;
		lA.installed = installed;
		lA.settings = settings;
		lA.timestamp = timestamp;
		lA.addBegin();
		
		lv = (ListView) findViewById(R.id.listView1);
		lv.setAdapter(lA);
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
}
