package com.woalk.apps.xposed.ttsb;

import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.pm.ApplicationInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;

public class AppSyncListAdapter extends ArrayAdapter<ApplicationInfo> {
	public final Activity context;
	public List<ApplicationInfo> apps;
	public List<Boolean> is_set;
	public List<Boolean> checked;

	public AppSyncListAdapter(Activity context, List<ApplicationInfo> apps, List<Boolean> is_set, List<Boolean> checked) {
		super(context, R.layout.item_applist, apps);
		this.context = context;
		
		this.apps = apps;
		this.is_set = is_set;
		this.checked = checked;
	}

	@SuppressLint({ "ViewHolder", "InflateParams" })
	@Override
	public View getView(int position, View view, ViewGroup parent) {
		final int pos = position;
		LayoutInflater inflater = context.getLayoutInflater();
		View rowView = inflater.inflate(R.layout.item_appsynclist, null, true);
		TextView txtName = (TextView) rowView.findViewById(R.id.textName);
		TextView txtPkg = (TextView) rowView.findViewById(R.id.textPkg);
		ImageView imgIcon = (ImageView) rowView.findViewById(R.id.imageIcon);
		ImageView imgCheck = (ImageView) rowView.findViewById(R.id.imageCheck);
		CheckBox checkSync = (CheckBox) rowView.findViewById(R.id.checkSync);
		txtName.setText(context.getPackageManager().getApplicationLabel(apps.get(pos)));
		txtPkg.setText(apps.get(pos).packageName);
		imgIcon.setImageDrawable(context.getPackageManager().getApplicationIcon(apps.get(pos)));
		if (is_set != null && is_set.size() > pos) if (is_set.get(pos)) imgCheck.setVisibility(View.VISIBLE);
		if (checked != null && checked.size() > pos) checkSync.setChecked(checked.get(pos));
		checkSync.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				checked.set(pos, isChecked);
			}
		});
		return rowView;
	}

}
