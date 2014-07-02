package com.woalk.apps.xposed.ttsb;

import java.util.ArrayList;

import com.woalk.apps.xposed.ttsb.Settings.Setting;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ListView;

public class RulesActivity extends Activity {
	public static String RULES_ACTIVITY_INFO_SELECTED = "com.woalk.apps.xposed.ttsb.RULES_ACTIVIITY_INFO_SELECTED";
	
	protected ActivityInfo act_inf;
	protected Settings.Parser mSettings;
	protected Settings.Setting setting;
	
	protected ViewSettingsPackListAdapter lA;
	protected ListView lv;
	
	protected EditText edit_s_plus;
	protected EditText edit_n_plus;
	protected CheckBox check_cview;
	protected CheckBox check_content;
	protected CheckBox check_decview;
	protected Button btn_cview;
	protected Button btn_content;
	protected Button btn_decview;
	protected Button btn_add;
	
	private Menu menu;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_rules);
		
		act_inf = getIntent().getParcelableExtra(RULES_ACTIVITY_INFO_SELECTED);
		
		mSettings = Settings.Loader.load(this, act_inf.packageName, act_inf.name);
		setting = mSettings.getSetting();
		
		edit_s_plus = (EditText) findViewById(R.id.editText_s_plus);
		edit_n_plus = (EditText) findViewById(R.id.editText_n_plus);
		check_cview = (CheckBox) findViewById(R.id.checkBox_cview);
		check_content = (CheckBox) findViewById(R.id.checkBox_content);
		check_decview = (CheckBox) findViewById(R.id.checkBox_decview);
		btn_cview = (Button) findViewById(R.id.button_edit_cview);
		btn_content = (Button) findViewById(R.id.button_edit_content);
		btn_decview = (Button) findViewById(R.id.button_edit_decview);
		btn_add = (Button) findViewById(R.id.button_addRule);
		
		lA = new ViewSettingsPackListAdapter(this, new ArrayList<Settings.Setting.ViewSettingsPack>());
		lv = (ListView) findViewById(R.id.listView1);
		lv.setAdapter(lA);
		
		check_cview.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				btn_cview.setEnabled(isChecked);
			}
		});
		check_content.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				btn_content.setEnabled(isChecked);
			}
		});
		check_decview.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				btn_decview.setEnabled(isChecked);
			}
		});
		
		btn_cview.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ViewSettingsDialog fragment = new ViewSettingsDialog(setting.rules.cview);
				fragment.setTitle(R.string.cview_edit_title);
				fragment.setViewSettingsDialogListener(new ViewSettingsDialog.ViewSettingsDialogListener() {
					@Override
					public void onDialogPositiveClick(Settings.Setting.ViewSettings vset) {
						setting.rules.cview = vset;
					}
					@Override
					public void onDialogNegativeClick(Settings.Setting.ViewSettings vset) {
					}
					@Override
					public void onDialogDeleteClick(Settings.Setting.ViewSettings vset) {
						setting.rules.cview = null;
						check_cview.setChecked(false);
					}
				});
				fragment.show(getFragmentManager(), "vset_cview");
			}
		});
		btn_content.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ViewSettingsDialog fragment = new ViewSettingsDialog(setting.rules.content);
				fragment.setTitle(R.string.content_edit_title);
				fragment.setViewSettingsDialogListener(new ViewSettingsDialog.ViewSettingsDialogListener() {
					@Override
					public void onDialogPositiveClick(Settings.Setting.ViewSettings vset) {
						setting.rules.content = vset;
					}
					@Override
					public void onDialogNegativeClick(Settings.Setting.ViewSettings vset) {
					}
					@Override
					public void onDialogDeleteClick(Settings.Setting.ViewSettings vset) {
						setting.rules.content = null;
						check_content.setChecked(false);
					}
				});
				fragment.show(getFragmentManager(), "vset_content");
			}
		});
		btn_decview.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ViewSettingsDialog fragment = new ViewSettingsDialog(setting.rules.decview);
				fragment.setTitle(R.string.decview_edit_title);
				fragment.setViewSettingsDialogListener(new ViewSettingsDialog.ViewSettingsDialogListener() {
					@Override
					public void onDialogPositiveClick(Settings.Setting.ViewSettings vset) {
						setting.rules.decview = vset;
					}
					@Override
					public void onDialogNegativeClick(Settings.Setting.ViewSettings vset) {
					}
					@Override
					public void onDialogDeleteClick(Settings.Setting.ViewSettings vset) {
						setting.rules.decview = null;
						check_decview.setChecked(false);
					}
				});
				fragment.show(getFragmentManager(), "vset_decview");
			}
		});
		
		btn_add.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ViewSettingsPackDialog fragment = new ViewSettingsPackDialog(new Settings.Setting.ViewSettingsPack());
				fragment.setViewSettingsDialogListener(new ViewSettingsPackDialog.ViewSettingsPackDialogListener() {
					@Override
					public void onDialogPositiveClick(Settings.Setting.ViewSettingsPack vsetpk) {
						addViewRule(vsetpk);
					}
					@Override
					public void onDialogNegativeClick(Settings.Setting.ViewSettingsPack vsetpk) {
					}
					@Override
					public void onDialogDeleteClick(Settings.Setting.ViewSettingsPack vsetpk) {
					}
				});
				fragment.show(getFragmentManager(), "vsetpk_add");
			}
		});
		lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				ViewSettingsPackDialog fragment = new ViewSettingsPackDialog(lA.getItem(position));
				final int pos = position;
				fragment.setViewSettingsDialogListener(new ViewSettingsPackDialog.ViewSettingsPackDialogListener() {
					@Override
					public void onDialogPositiveClick(Settings.Setting.ViewSettingsPack vsetpk) {
						setting.rules.view.remove(pos);
						setting.rules.view.add(pos, vsetpk);
						lA.vsetpks.clear();
						lA.vsetpks.addAll(setting.rules.view);
						lA.notifyDataSetChanged();
						rulesUpdated();
					}
					@Override
					public void onDialogNegativeClick(Settings.Setting.ViewSettingsPack vsetpk) {
					}
					@Override
					public void onDialogDeleteClick(Settings.Setting.ViewSettingsPack vsetpk) {
						lA.vsetpks.remove(pos);
						lA.notifyDataSetChanged();
					}
				});
				fragment.show(getFragmentManager(), "vsetpk_edit");
			}
		});
		
		rulesUpdated();
	}
	
	protected void rulesUpdated() {
		edit_s_plus.setText(String.valueOf(setting.rules.s_plus));
		edit_n_plus.setText(String.valueOf(setting.rules.n_plus));
		if (setting.rules.cview != null) check_cview.setChecked(true);
		if (setting.rules.content != null) check_content.setChecked(true);
		if (setting.rules.decview != null) check_decview.setChecked(true);
		if (setting.rules.view != null) {
			lA.vsetpks.clear();
			lA.vsetpks.addAll(setting.rules.view);
			lA.notifyDataSetChanged();
		}
	}
	
	protected void addViewRule(Settings.Setting.ViewSettingsPack vsetpk) {
		if (setting.rules.view == null || setting.rules.view.size() == 0)
			setting.rules.view = new ArrayList<Setting.ViewSettingsPack>();
		setting.rules.view.add(vsetpk);
		lA.vsetpks.clear();
		lA.vsetpks.addAll(setting.rules.view);
		lA.notifyDataSetChanged();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		this.menu = menu;
	    inflater.inflate(R.menu.rules, menu);
	    menu.getItem(2).setEnabled((Helpers.clipboard_sav instanceof Settings.Setting.Rules));
	    return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle presses on the action bar items
	    switch (item.getItemId()) {
	    case R.id.action_save_rules:
			setting.rules.s_plus = Integer.valueOf(edit_s_plus.getText().toString());
			setting.rules.n_plus = Integer.valueOf(edit_n_plus.getText().toString());
			if (!check_cview.isChecked()) setting.rules.cview = null;
			if (!check_content.isChecked()) setting.rules.content = null;
			if (!check_decview.isChecked()) setting.rules.decview = null;
			mSettings.setSetting(setting);
			Settings.Saver.save(this, act_inf.packageName, act_inf.name, mSettings);
			Toast.makeText(getApplicationContext(), R.string.str_saved, Toast.LENGTH_SHORT).show();
			finish();
	    	return true;
	    case R.id.action_copy_rules:
	    	Helpers.clipboard_sav = setting.rules;
	    	menu.getItem(2).setEnabled(true);
	    	return true;
	    case R.id.action_paste_rules:
	    	if (Helpers.clipboard_sav != null && Helpers.clipboard_sav instanceof Settings.Setting.Rules)
	    		setting.rules = (Settings.Setting.Rules) Helpers.clipboard_sav;
	    	return true;
	    default:
	    	return super.onOptionsItemSelected(item);
	    }
	}
	
	@Override
	public void onStop() {
		Intent resultIntent = new Intent();
		setResult(Activity.RESULT_OK, resultIntent);
		super.onStop();
	}
}
