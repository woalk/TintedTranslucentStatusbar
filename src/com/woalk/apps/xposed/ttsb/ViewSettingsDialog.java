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
import android.widget.TextView;

public class ViewSettingsDialog extends DialogFragment {

	protected Settings.Setting.ViewSettings vset;
	private String title;
	private int title_id;
	private boolean set_title;

	public interface ViewSettingsDialogListener {
		public void onDialogPositiveClick(Settings.Setting.ViewSettings vset);

		public void onDialogNegativeClick(Settings.Setting.ViewSettings vset);

		public void onDialogDeleteClick(Settings.Setting.ViewSettings vset);
	}

	ViewSettingsDialogListener listener;

	public ViewSettingsDialog(Settings.Setting.ViewSettings vset) {
		if (vset != null)
			this.vset = vset;
		else
			this.vset = new Settings.Setting.ViewSettings();
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

	public void setViewSettingsDialogListener(
			ViewSettingsDialogListener listener) {
		this.listener = listener;
	}

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

		View v = inflater.inflate(R.layout.viewsettings, null);
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
		check_actionbar_h = ((CheckBox) v
				.findViewById(R.id.checkBox_actionbar_h));
		check_nav_h = ((CheckBox) v.findViewById(R.id.checkBox_nav_h));
		check_nav_w = ((CheckBox) v.findViewById(R.id.checkBox_nav_w));
		checkFSW.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				radioFSW1.setEnabled(isChecked);
				radioFSW0.setEnabled(isChecked);
			}
		});
		checkFSW.setChecked(vset.setFSW);
		checkCTP.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				radioCTP1.setEnabled(isChecked);
				radioCTP0.setEnabled(isChecked);
			}
		});
		checkCTP.setChecked(vset.setCTP);
		if (vset.setFSW_value) {
			radioFSW1.setChecked(true);
		} else {
			radioFSW1.setChecked(false);
			radioFSW0.setChecked(true);
		}
		if (vset.setCTP_value) {
			radioCTP1.setChecked(true);
		} else {
			radioCTP1.setChecked(false);
			radioCTP0.setChecked(true);
		}
		checkPadding.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
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
		checkPadding.setChecked(vset.padding != null);
		if (vset.padding != null) {
			editLeft.setText(String.valueOf(vset.padding.left));
			editTop.setText(String.valueOf(vset.padding.top));
			editRight.setText(String.valueOf(vset.padding.right));
			editBottom.setText(String.valueOf(vset.padding.bottom));
			check_status_h.setChecked(vset.padding.plus_status_h);
			check_actionbar_h.setChecked(vset.padding.plus_actionbar_h);
			check_nav_h.setChecked(vset.padding.plus_nav_h);
			check_nav_w.setChecked(vset.padding.plus_nav_w);
		} else {
			editLeft.setText("0");
			editTop.setText("0");
			editRight.setText("0");
			editBottom.setText("0");
		}

		builder.setView(v)
				.setPositiveButton(android.R.string.ok,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int id) {
								if (checkFSW.isChecked()) {
									vset.setFSW = true;
									vset.setFSW_value = radioFSW1.isChecked();
								} else {
									vset.setFSW = false;
								}
								if (checkCTP.isChecked()) {
									vset.setCTP = true;
									vset.setCTP_value = radioCTP1.isChecked();
								} else {
									vset.setCTP = false;
								}
								if (checkPadding.isChecked()) {
									vset.padding = new Settings.Setting.ViewSettings.IntOptPadding();
									if (!editLeft.getText().equals("")
											&& !editTop.getText().equals("")
											&& !editRight.getText().equals("")
											&& !editBottom.getText().equals("")) {
										try {
											vset.padding.left = Integer
													.valueOf(editLeft.getText()
															.toString());
											vset.padding.top = Integer
													.valueOf(editTop.getText()
															.toString());
											vset.padding.right = Integer
													.valueOf(editRight
															.getText()
															.toString());
											vset.padding.bottom = Integer
													.valueOf(editBottom
															.getText()
															.toString());
										} catch (NumberFormatException e) {
											// Log.e("TTSB UI",
											// "Padding was not a valid value.");
										}
									}
									vset.padding.plus_status_h = check_status_h
											.isChecked();
									vset.padding.plus_actionbar_h = check_actionbar_h
											.isChecked();
									vset.padding.plus_nav_h = check_nav_h
											.isChecked();
									vset.padding.plus_nav_w = check_nav_w
											.isChecked();
								} else {
									vset.padding = null;
								}
								listener.onDialogPositiveClick(vset);
							}
						})
				.setNegativeButton(android.R.string.cancel,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								ViewSettingsDialog.this.getDialog().cancel();
								listener.onDialogNegativeClick(vset);
							}
						})
				.setNeutralButton(R.string.v_delete,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								listener.onDialogDeleteClick(vset);
							}
						});
		return builder.create();
	}

}
