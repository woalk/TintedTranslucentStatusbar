package com.woalk.apps.xposed.ttsb;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

public class ViewSettingsPackDialog extends DialogFragment {
	
	protected Settings.Setting.ViewSettingsPack vsetpk;
	private String title;
	private int title_id;
	private boolean set_title;

	public interface ViewSettingsPackDialogListener {
		public void onDialogPositiveClick(Settings.Setting.ViewSettingsPack vsetpk);
		public void onDialogNegativeClick(Settings.Setting.ViewSettingsPack vsetpk);
		public void onDialogDeleteClick(Settings.Setting.ViewSettingsPack vsetpk);
	}
	
	ViewSettingsPackDialogListener listener;
	
	public ViewSettingsPackDialog(Settings.Setting.ViewSettingsPack vsetpk) {
		this.vsetpk = vsetpk;
	}
	
	public void setTitle(String title) {
		this.title = title;
		set_title = true;
	}
	public void setTitle(int id) {
		this.title = null;
		this.title_id = id;
		set_title = true;
	}
	
	public void setViewSettingsDialogListener(ViewSettingsPackDialogListener listener) {
		this.listener = listener;
	}

	Spinner spinnerFrom;
	EditText edit_levels;
	EditText edit_childindexes;
	
	CheckBox checkFSW;
	CheckBox checkCTP;
	RadioButton radioFSW1;
	RadioButton radioFSW0;
	RadioButton radioCTP1;
	RadioButton radioCTP0;
	CheckBox checkPadding;
	EditText editLeft;
	EditText editTop;
	EditText editRight;
	EditText editBottom;
	TextView txtLeft;
	TextView txtTop;
	TextView txtRight;
	TextView txtBottom;
	CheckBox check_status_h;
	CheckBox check_actionbar_h;
	CheckBox check_nav_h;
	CheckBox check_nav_w;

	@SuppressLint("InflateParams")
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
	    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	    LayoutInflater inflater = getActivity().getLayoutInflater();
	    if (set_title) {
	    	if (title == null || title.equals(""))
	    		builder.setTitle(title_id);
	    	else
	    		builder.setTitle(title);
	    }
	    
	    View v = inflater.inflate(R.layout.viewsettingspack, null);
	    spinnerFrom = ((Spinner) v.findViewById(R.id.spinner_from));
	    edit_levels = ((EditText) v.findViewById(R.id.editText_levels));
	    edit_childindexes = ((EditText) v.findViewById(R.id.editText_childindexes));
	    checkFSW = ((CheckBox) v.findViewById(R.id.checkBox_setFSW));
	    checkCTP = ((CheckBox) v.findViewById(R.id.checkBox_setCTP));
	    radioFSW1 = ((RadioButton) v.findViewById(R.id.radio_FSW1));
	    radioFSW0 = ((RadioButton) v.findViewById(R.id.radio_FSW0));
	    radioCTP1 = ((RadioButton) v.findViewById(R.id.radio_CTP1));
	    radioCTP0 = ((RadioButton) v.findViewById(R.id.radio_CTP0));
	    checkPadding = ((CheckBox) v.findViewById(R.id.checkBox_setPadding));
	    editLeft = ((EditText) v.findViewById(R.id.editText_left));
	    editTop = ((EditText) v.findViewById(R.id.editText_top));
	    editRight = ((EditText) v.findViewById(R.id.editText_right));
	    editBottom = ((EditText) v.findViewById(R.id.editText_bottom));
	    txtLeft = ((TextView) v.findViewById(R.id.textView_pleft));
	    txtTop = ((TextView) v.findViewById(R.id.textView_ptop));
	    txtRight = ((TextView) v.findViewById(R.id.textView_pright));
	    txtBottom = ((TextView) v.findViewById(R.id.textView_pbottom));
	    check_status_h = ((CheckBox) v.findViewById(R.id.checkBox_status_h));
	    check_actionbar_h = ((CheckBox) v.findViewById(R.id.checkBox_actionbar_h));
	    check_nav_h = ((CheckBox) v.findViewById(R.id.checkBox_nav_h));
	    check_nav_w = ((CheckBox) v.findViewById(R.id.checkBox_nav_w));
	    checkFSW.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				radioFSW1.setEnabled(isChecked);
				radioFSW0.setEnabled(isChecked);
			}
	    });
	    checkFSW.setChecked(vsetpk.settings.setFSW);
	    checkCTP.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				radioCTP1.setEnabled(isChecked);
				radioCTP0.setEnabled(isChecked);
			}
	    });
	    checkCTP.setChecked(vsetpk.settings.setCTP);
	    spinnerFrom.setSelection(vsetpk.from);
	    edit_levels.setText(String.valueOf(vsetpk.levels));
	    edit_childindexes.setText(vsetpk.getChildIndexesString());
	    if (vsetpk.settings.setFSW_value) {
	    	radioFSW1.setChecked(true);
	    } else {
	    	radioFSW1.setChecked(false);
	    	radioFSW0.setChecked(true);
	    }
	    if (vsetpk.settings.setCTP_value) {
	    	radioCTP1.setChecked(true);
	    } else {
	    	radioCTP1.setChecked(false);
	    	radioCTP0.setChecked(true);
	    }
	    checkPadding.setChecked(vsetpk.settings.padding != null);
	    checkPadding.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				editLeft.setEnabled(isChecked);
				editTop.setEnabled(isChecked);
				editRight.setEnabled(isChecked);
				editBottom.setEnabled(isChecked);
				txtLeft.setEnabled(isChecked);
				txtRight.setEnabled(isChecked);
				txtTop.setEnabled(isChecked);
				txtBottom.setEnabled(isChecked);
				check_status_h.setEnabled(isChecked);
				check_actionbar_h.setEnabled(isChecked);
				check_nav_h.setEnabled(isChecked);
				check_nav_w.setEnabled(isChecked);
			}
		});
	    if (vsetpk.settings.padding != null) {
	    	editLeft.setText(String.valueOf(vsetpk.settings.padding.left));
	    	editTop.setText(String.valueOf(vsetpk.settings.padding.top));
	    	editRight.setText(String.valueOf(vsetpk.settings.padding.right));
	    	editBottom.setText(String.valueOf(vsetpk.settings.padding.bottom));
	    	check_status_h.setChecked(vsetpk.settings.padding.plus_status_h);
	    	check_actionbar_h.setChecked(vsetpk.settings.padding.plus_actionbar_h);
	    	check_nav_h.setChecked(vsetpk.settings.padding.plus_nav_h);
	    	check_nav_w.setChecked(vsetpk.settings.padding.plus_nav_w);
	    }
	    
	    builder.setView(v)
	           .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
	               @Override
	               public void onClick(DialogInterface dialog, int id) {
	            	   vsetpk.from = spinnerFrom.getSelectedItemPosition();
	            	   vsetpk.levels = Integer.parseInt(edit_levels.getText().toString());
	            	   vsetpk.setChildIndexesFromString(edit_childindexes.getText().toString());
	            	   if (checkFSW.isChecked()) {
	            		   vsetpk.settings.setFSW = true;
	            		   vsetpk.settings.setFSW_value = radioFSW1.isChecked();
	            	   } else {
	            		   vsetpk.settings.setFSW = false;
	            	   }
	            	   if (checkCTP.isChecked()) {
	            		   vsetpk.settings.setCTP = true;
	            		   vsetpk.settings.setCTP_value = radioCTP1.isChecked();
	            	   } else {
	            		   vsetpk.settings.setCTP = false;
	            	   }
	            	   if (checkPadding.isChecked()) {
	            		   vsetpk.settings.padding = new Settings.Setting.ViewSettings.IntOptPadding();
	            		   vsetpk.settings.padding.left = Integer.valueOf(editLeft.getText().toString());
	            		   vsetpk.settings.padding.top = Integer.valueOf(editTop.getText().toString());
	            		   vsetpk.settings.padding.right = Integer.valueOf(editRight.getText().toString());
	            		   vsetpk.settings.padding.bottom = Integer.valueOf(editBottom.getText().toString());
	            		   vsetpk.settings.padding.plus_status_h = check_status_h.isChecked();
	            		   vsetpk.settings.padding.plus_actionbar_h = check_actionbar_h.isChecked();
	            		   vsetpk.settings.padding.plus_nav_h = check_nav_h.isChecked();
	            		   vsetpk.settings.padding.plus_nav_w = check_nav_w.isChecked();
	            	   } else {
	            		   vsetpk.settings.padding = null;
	            	   }
	                   listener.onDialogPositiveClick(vsetpk);
	               }
	           })
	           .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
	               public void onClick(DialogInterface dialog, int id) {
	                   ViewSettingsPackDialog.this.getDialog().cancel();
	                   listener.onDialogNegativeClick(vsetpk);
	               }
	           })
	           .setNeutralButton(R.string.v_delete, new DialogInterface.OnClickListener() {
	        	   public void onClick(DialogInterface dialog, int which) {
	        		   listener.onDialogDeleteClick(vsetpk);
	        	   }
	           });      
	    return builder.create();
	}
	
}
