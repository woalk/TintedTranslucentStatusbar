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
	private boolean isTranslucentExcluded = false;
	private boolean isNavBarExcluded = false;
	
	protected SharedPreferences sPref;
	
	private TextView textView1;
	private TextView textView01;
	private CheckBox checkBox1;
	private CheckBox checkBox01;
	private CheckBox checkBox2;
	private CheckBox checkBox02;
	private EditText editText1;
	private EditText editText01;
	private RadioGroup radioGroup1;
	private RadioButton radio0;
	private RadioButton radio1;
	private RadioButton radio2;
	private RadioButton radio3;
	private RadioGroup radioGroup01;
	private RadioButton radio00;
	private RadioButton radio01;
	private RadioButton radio02;
	private RadioButton radio03;
	
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
		checkBox2 = (CheckBox) findViewById(R.id.checkBox2);
		checkBox02 = (CheckBox) findViewById(R.id.checkBox02);
		editText1 = (EditText) findViewById(R.id.editText1);
		editText01 = (EditText) findViewById(R.id.editText01);
		radioGroup1 = (RadioGroup) findViewById(R.id.radioGroup1);
		radio0 = (RadioButton) findViewById(R.id.radio0);
		radio1 = (RadioButton) findViewById(R.id.radio1);
		radio2 = (RadioButton) findViewById(R.id.radio2);
		radio3 = (RadioButton) findViewById(R.id.radio3);
		radioGroup01 = (RadioGroup) findViewById(R.id.radioGroup01);
		radio00 = (RadioButton) findViewById(R.id.radio00);
		radio01 = (RadioButton) findViewById(R.id.radio01);
		radio02 = (RadioButton) findViewById(R.id.radio02);
		radio03 = (RadioButton) findViewById(R.id.radio03);
		checkBox1.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				textView1.setEnabled(isChecked);
				editText1.setEnabled(isChecked);
				radio0.setEnabled(isChecked);
				radio1.setEnabled(isChecked);
				radio2.setEnabled(isChecked);
				radio3.setEnabled(isChecked);
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
				radio02.setEnabled(isChecked);
				radio03.setEnabled(isChecked);
				isNavBarChecked = isChecked;
			}
		});
		checkBox2.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) checkBox1.setChecked(false);
				checkBox1.setEnabled(!isChecked);
				isTranslucentExcluded = isChecked;
			}
		});
		checkBox02.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked) checkBox01.setChecked(false);
				checkBox01.setEnabled(!isChecked);
				isNavBarExcluded = isChecked;
			}
		});

		if (sPref.contains(sActivity.name)) {
			checkBox1.setChecked(true);
			editText1.setText(sPref.getString(sActivity.name, getString(R.string.defaultColorCode)));
		} else {
			checkBox1.setChecked(true);
			checkBox1.setChecked(false);
		}
		switch (sPref.getInt(sActivity.name + "+s", 0)) {
		case 0:
			radioGroup1.check(R.id.radio0);
			break;
		case 1:
			radioGroup1.check(R.id.radio1);
			break;
		case 2:
			radioGroup1.check(R.id.radio2);
			break;
		case 3:
			radioGroup1.check(R.id.radio3);
			break;
		}
		
		if (sPref.contains(sActivity.name + "+n")) {
			checkBox01.setChecked(true);
			editText01.setText(sPref.getString(sActivity.name + "+n", getString(R.string.defaultColorCode)));
		} else {
			checkBox01.setChecked(true);
			checkBox01.setChecked(false);
		}
		switch (sPref.getInt(sActivity.name + "+sn", 0)) {
		case 0:
			radioGroup01.check(R.id.radio00);
			break;
		case 1:
			radioGroup01.check(R.id.radio01);
			break;
		case 2:
			radioGroup01.check(R.id.radio02);
			break;
		case 3:
			radioGroup01.check(R.id.radio03);
			break;
		}
		
		if (sPref.contains(sActivity.name + "+e")) {
			checkBox2.setChecked(true);
		}
		if (sPref.contains(sActivity.name + "+en")) {
			checkBox02.setChecked(true);
		}
	}
	
	@SuppressLint("DefaultLocale")
	@Override
	protected void onPause() {
		SharedPreferences.Editor sPrefEdit = sPref.edit();
		if (isTranslucentChecked) {
			String color = editText1.getText().toString().toUpperCase();
			if (color.length() == 6) {
				color = "FF" + color;
			} else if (color.length() < 6) {
				color = "FF" + color;
				for (int i = 0; i < 8 - color.length(); i++) {
					color = color + "0";
				}
			}
			sPrefEdit.putString(sActivity.name, color);
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
			case R.id.radio3:
				radioVal1 = 3;
				break;
			default:
				radioVal1 = 0;
			}
			sPrefEdit.putInt(sActivity.name + "+s", radioVal1);
			
			sPrefEdit.putString(sActivity.name + "+p", sActivity.packageName);
		}
		else {
			sPrefEdit.remove(sActivity.name);
			sPrefEdit.remove(sActivity.name + "+s");
		}
		
		if (isNavBarChecked) {
			String color = editText01.getText().toString().toUpperCase();
			if (color.length() == 6) {
				color = "FF" + color;
			} else if (color.length() < 6) {
				color = "FF" + color;
				for (int i = 0; i < 8 - color.length(); i++) {
					color = color + "0";
				}
			}
			sPrefEdit.putString(sActivity.name + "+n", color);
			int radioVal2;
			switch (radioGroup01.getCheckedRadioButtonId()) {
			case R.id.radio00:
				radioVal2 = 0;
				break;
			case R.id.radio01:
				radioVal2 = 1;
				break;
			case R.id.radio02:
				radioVal2 = 2;
				break;
			case R.id.radio03:
				radioVal2 = 3;
				break;
			default:
				radioVal2 = 0;
			}
			sPrefEdit.putInt(sActivity.name + "+sn", radioVal2);
			
			sPrefEdit.putString(sActivity.name + "+p", sActivity.packageName);
		}
		else {
			sPrefEdit.remove(sActivity.name + "+n");
			sPrefEdit.remove(sActivity.name + "+sn");
		}
		sPrefEdit.apply();
		
		if (isTranslucentExcluded) {
			sPrefEdit.putBoolean(sActivity.name + "+e", true);

			sPrefEdit.putString(sActivity.name + "+p", sActivity.packageName);
			
			sPrefEdit.apply();
		}
		else {
			sPrefEdit.remove(sActivity.name + "+e");
			
			sPrefEdit.apply();
		}

		if (isNavBarExcluded) {
			sPrefEdit.putBoolean(sActivity.name + "+en", true);

			sPrefEdit.putString(sActivity.name + "+p", sActivity.packageName);
			
			sPrefEdit.apply();
		}
		else {
			sPrefEdit.remove(sActivity.name + "+en");

			sPrefEdit.apply();
		}
		
		if (!sPref.contains(sActivity.name) && !sPref.contains(sActivity.name + "+n") && !sPref.contains(sActivity.name + "+e") && !sPref.contains(sActivity.name + "+en")) {
			sPrefEdit.remove(sActivity.name + "+p");
			sPrefEdit.apply();
		}
		Toast toast = Toast.makeText(getApplicationContext(), getString(R.string.saved_info), Toast.LENGTH_SHORT);
		toast.show();
		super.onStop();
	}
}
