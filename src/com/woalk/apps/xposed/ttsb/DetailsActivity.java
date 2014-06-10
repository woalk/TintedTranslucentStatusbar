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

	public ActivityInfo sActivity;
	
	private boolean isTranslucentChecked = false;
	
	protected SharedPreferences sPref;
	
	private TextView textView1;
	private CheckBox checkBox1;
	private EditText editText1;
	private RadioGroup radioGroup1;
	private RadioButton radio0;
	private RadioButton radio1;
	private RadioButton radio2;
		
	@SuppressLint("WorldReadableFiles")
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_details);
		
		sActivity = (ActivityInfo) getIntent().getExtras().get(SEL_PACKAGE_ACTIVITY_INFO);
		sPref = getApplicationContext().getSharedPreferences(Helpers.TTSB_PREFERENCES, Context.MODE_WORLD_READABLE);
		
		try {
			setTitle(getString(R.string.title_activity_details) + " " + sActivity.name);
		} catch (NotFoundException e) {
			e.printStackTrace();
		}
		
		textView1 = (TextView) findViewById(R.id.textView1);
		checkBox1 = (CheckBox) findViewById(R.id.checkBox1);
		editText1 = (EditText) findViewById(R.id.editText1);
		radioGroup1 = (RadioGroup) findViewById(R.id.radioGroup1);
		radio0 = (RadioButton) findViewById(R.id.radio0);
		radio1 = (RadioButton) findViewById(R.id.radio1);
		radio2 = (RadioButton) findViewById(R.id.radio2);
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
	}
	
	@Override
	protected void onPause() {
		SharedPreferences.Editor sPrefEdit = sPref.edit();
		if (isTranslucentChecked) {
			sPrefEdit.putString(sActivity.name, editText1.getText().toString());
			int radioVal;
			switch (radioGroup1.getCheckedRadioButtonId()) {
			case R.id.radio0:
				radioVal = 0;
				break;
			case R.id.radio1:
				radioVal = 1;
				break;
			case R.id.radio2:
				radioVal = 2;
				break;
			default:
				radioVal = 2;
			}
			sPrefEdit.putInt(sActivity.name + "+s", radioVal);
			sPrefEdit.putString(sActivity.name + "+p", sActivity.packageName);
		}
		else {
			sPrefEdit.remove(sActivity.name);
			sPrefEdit.remove(sActivity.name + "+s");
			sPrefEdit.remove(sActivity.name + "+p");
		}
		sPrefEdit.commit();
		Toast toast = Toast.makeText(getApplicationContext(), getString(R.string.saved_info), Toast.LENGTH_SHORT);
		toast.show();
		super.onStop();
	}
}
