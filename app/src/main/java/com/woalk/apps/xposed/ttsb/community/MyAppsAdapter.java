package com.woalk.apps.xposed.ttsb.community;

import java.util.List;

import com.woalk.apps.xposed.ttsb.R;

import android.app.Activity;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MyAppsAdapter extends ArrayAdapter<ApplicationInfo> {
	private Activity context;

	protected List<ApplicationInfo> apps;
	protected List<Boolean> is_set;
	protected List<Boolean> has_update;

	public MyAppsAdapter(Activity context, List<ApplicationInfo> apps,
			List<Boolean> is_set, List<Boolean> has_update) {
		super(context, R.layout.item_myapps, apps);

		this.context = context;
		this.apps = apps;
		this.is_set = is_set;
		this.has_update = has_update;
	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {
		View rowView;
		if (view == null) {
			LayoutInflater inflater = context.getLayoutInflater();
			rowView = inflater.inflate(R.layout.item_myapps, parent, false);
		} else
			rowView = view;
		PackageManager pkgMan = context.getPackageManager();
		TextView tv1 = (TextView) rowView.findViewById(R.id.textName);
		TextView tv2 = (TextView) rowView.findViewById(R.id.textPkg);
		ImageView img1 = (ImageView) rowView.findViewById(R.id.imageIcon);
		ImageView img2 = (ImageView) rowView.findViewById(R.id.imageCheck);
		ImageView img3 = (ImageView) rowView.findViewById(R.id.imageUpdateInfo);
		tv1.setText(pkgMan.getApplicationLabel(apps.get(position)));
		tv2.setText(apps.get(position).packageName);
		img1.setImageDrawable(pkgMan.getApplicationIcon(apps.get(position)));
		boolean is_set = this.is_set.get(position);
		boolean has_update = this.has_update.get(position);
		img2.setVisibility(is_set ? View.VISIBLE : View.GONE);
		img3.setVisibility(has_update ? View.VISIBLE : View.GONE);
		boolean is_new = !is_set || has_update;
		img1.setImageAlpha(is_new ? 255 : 175);
		tv1.setTextColor(is_new ? Color.BLACK : 0x9F000000);
		tv2.setTextColor(is_new ? Color.BLACK : 0x9F000000);
		return rowView;
	}
}
