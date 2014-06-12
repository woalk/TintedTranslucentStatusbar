package com.woalk.apps.xposed.ttsb;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Resources.NotFoundException;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class DetailsActivity extends Activity {
	
	public static String SEL_PACKAGE_ACTIVITY_INFO = "com.woalk.apps.xposed.ttsb.SEL_PACKAGE_ACTIVITY_INFO";
	public static String SEL_PACKAGE_ALL_PKGNAME = "com.woalk.apps.xposed.ttsb.SEL_PACKAGE_ALL_PKGNAME";

	public ActivityInfo sActivity;
	
	private boolean isTranslucentChecked = false;
	private boolean isNavBarChecked = false;
	
	protected SharedPreferences sPref;
	
	private TextView textView1;
	private TextView textView01;
	private CheckBox checkBox1;
	private CheckBox checkBox01;
	private EditText editText1;
	private EditText editText01;
	private RadioGroup radioGroup1;
	private RadioButton radio0;
	private RadioButton radio1;
	private RadioButton radio2;
	private RadioGroup radioGroup01;
	private RadioButton radio00;
	private RadioButton radio01;
	
	@SuppressLint("WorldReadableFiles")
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_details);
		
		if (getIntent().getExtras().get(SEL_PACKAGE_ACTIVITY_INFO) == null) {
			sActivity = new ActivityInfo();
			sActivity.name = (String) getIntent().getExtras().get(SEL_PACKAGE_ALL_PKGNAME);
			sActivity.packageName = sActivity.name.substring(0, sActivity.name.length() - "[.ALL]".length());
		}
		else {
			sActivity = (ActivityInfo) getIntent().getExtras().get(SEL_PACKAGE_ACTIVITY_INFO);
		}
		sPref = getApplicationContext().getSharedPreferences(Helpers.TTSB_PREFERENCES, Context.MODE_WORLD_READABLE);
		
		try {
			setTitle(getString(R.string.title_activity_details) + " " + sActivity.name);
		} catch (NotFoundException e) {
			e.printStackTrace();
		}
		
		textView1 = (TextView) findViewById(R.id.textView1);
		textView01 = (TextView) findViewById(R.id.textView01);
		checkBox1 = (CheckBox) findViewById(R.id.checkBox1);
		checkBox01 = (CheckBox) findViewById(R.id.checkBox01);
		editText1 = (EditText) findViewById(R.id.editText1);
		editText01 = (EditText) findViewById(R.id.editText01);
		radioGroup1 = (RadioGroup) findViewById(R.id.radioGroup1);
		radio0 = (RadioButton) findViewById(R.id.radio0);
		radio1 = (RadioButton) findViewById(R.id.radio1);
		radio2 = (RadioButton) findViewById(R.id.radio2);
		radioGroup01 = (RadioGroup) findViewById(R.id.radioGroup01);
		radio00 = (RadioButton) findViewById(R.id.radio00);
		radio01 = (RadioButton) findViewById(R.id.radio01);
		checkBox1.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				textView1.setEnabled(isChecked);
				editText1.setEnabled(isChecked);
				radio0.setEnabled(isChecked);
				radio1.setEnabled(isChecked);
				radio2.setEnabled(isChecked);
				isTranslucentChecked = isChecked;
			}
		});
		checkBox01.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				textView01.setEnabled(isChecked);
				editText01.setEnabled(isChecked);
				radio00.setEnabled(isChecked);
				radio01.setEnabled(isChecked);
				isNavBarChecked = isChecked;
			}
		});

		if (sPref.contains(sActivity.name)) {
			checkBox1.setChecked(true);
			editText1.setText(sPref.getString(sActivity.name, getString(R.string.defaultColorCode)));
			switch (sPref.getInt(sActivity.name + "+s", 2)) {
			case 0:
				radioGroup1.check(R.id.radio0);
				break;
			case 1:
				radioGroup1.check(R.id.radio1);
				break;
			case 2:
				radioGroup1.check(R.id.radio2);
				break;
			}
		} else {
			checkBox1.setChecked(true);
			checkBox1.setChecked(false);
		}
		if (sPref.contains(sActivity.name + "+n")) {
			checkBox01.setChecked(true);
			editText01.setText(sPref.getString(sActivity.name + "+n", getString(R.string.defaultColorCode)));
			switch (sPref.getInt(sActivity.name + "+sn", 1)) {
			case 0:
				radioGroup1.check(R.id.radio00);
				break;
			case 1:
				radioGroup1.check(R.id.radio01);
				break;
			}
		} else {
			checkBox01.setChecked(true);
			checkBox01.setChecked(false);
		}
	}
	
	@Override
	protected void onPause() {
		SharedPreferences.Editor sPrefEdit = sPref.edit();
		if (isTranslucentChecked) {
			sPrefEdit.putString(sActivity.name, editText1.getText().toString());
			int radioVal1;
			switch (radioGroup1.getCheckedRadioButtonId()) {
			case R.id.radio0:
				radioVal1 = 0;
				break;
			case R.id.radio1:
				radioVal1 = 1;
				break;
			case R.id.radio2:
				radioVal1 = 2;
				break;
			default:
				radioVal1 = 2;
			}
			sPrefEdit.putInt(sActivity.name + "+s", radioVal1);
			
			sPrefEdit.putString(sActivity.name + "+p", sActivity.packageName);
		}
		else {
			sPrefEdit.remove(sActivity.name);
			sPrefEdit.remove(sActivity.name + "+s");
		}
		if (isNavBarChecked) {
			sPrefEdit.putString(sActivity.name + "+n", editText01.getText().toString());
			int radioVal2;
			switch (radioGroup01.getCheckedRadioButtonId()) {
			case R.id.radio00:
				radioVal2 = 0;
				break;
			case R.id.radio01:
				radioVal2 = 1;
				break;
			default:
				radioVal2 = 1;
			}
			sPrefEdit.putInt(sActivity.name + "+sn", radioVal2);
		}
		else {
			sPrefEdit.remove(sActivity.name + "+n");
			sPrefEdit.remove(sActivity.name + "+sn");
		}
		if (!sPref.contains(sActivity.name) && !sPref.contains(sActivity.name + "+n")) {
			sPrefEdit.remove(sActivity.name + "+p");
		}
		sPrefEdit.commit();
		Toast toast = Toast.makeText(getApplicationContext(), getString(R.string.saved_info), Toast.LENGTH_SHORT);
		toast.show();
		super.onStop();
	}
}
