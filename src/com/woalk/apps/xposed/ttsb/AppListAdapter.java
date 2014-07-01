package com.woalk.apps.xposed.ttsb;

import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.pm.ApplicationInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class AppListAdapter extends ArrayAdapter<ApplicationInfo> {
	
	public final Activity context;
	public List<ApplicationInfo> apps;
	public List<Boolean> is_set;

	public AppListAdapter(Activity context, List<ApplicationInfo> apps, List<Boolean> is_set) {
		super(context, R.layout.item_applist, apps);
		this.context = context;
		
		this.apps = apps;
		this.is_set = is_set;
	}

	@SuppressLint("ViewHolder")
	@Override
	public View getView(int position, View view, ViewGroup parent) {
		LayoutInflater inflater = context.getLayoutInflater();
		View rowView = inflater.inflate(R.layout.item_applist, null, true);
		TextView txtName = (TextView) rowView.findViewById(R.id.textName);
		TextView txtPkg = (TextView) rowView.findViewById(R.id.textPkg);
		ImageView imgIcon = (ImageView) rowView.findViewById(R.id.imageIcon);
		ImageView imgCheck = (ImageView) rowView.findViewById(R.id.imageCheck);
		txtName.setText(context.getPackageManager().getApplicationLabel(apps.get(position)));
		txtPkg.setText(apps.get(position).packageName);
		imgIcon.setImageDrawable(context.getPackageManager().getApplicationIcon(apps.get(position)));
		if (is_set != null && is_set.size() > position) if (is_set.get(position)) imgCheck.setVisibility(View.VISIBLE);
		return rowView;
	}

}
