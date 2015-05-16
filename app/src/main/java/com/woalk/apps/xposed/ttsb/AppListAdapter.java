package com.woalk.apps.xposed.ttsb;

import java.util.List;

import android.app.Activity;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
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
	public List<Drawable> icons;

	private PackageManager pkgMan;

	public AppListAdapter(Activity context, List<ApplicationInfo> apps,
			List<Boolean> is_set, List<Drawable> icons) {
		super(context, R.layout.item_applist, apps);
		this.context = context;

		this.apps = apps;
		this.is_set = is_set;
		this.icons = icons;

		pkgMan = context.getPackageManager();
	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {
		View rowView;
		ViewHolder vHold;
		if (view == null) {
			LayoutInflater inflater = context.getLayoutInflater();
			rowView = inflater.inflate(R.layout.item_applist, parent, false);
			vHold = new ViewHolder();
			vHold.txtName = (TextView) rowView.findViewById(R.id.textName);
			vHold.txtPkg = (TextView) rowView.findViewById(R.id.textPkg);
			vHold.imgIcon = (ImageView) rowView.findViewById(R.id.imageIcon);
			vHold.imgCheck = (ImageView) rowView.findViewById(R.id.imageCheck);
			rowView.setTag(vHold);
		} else {
			rowView = view;
			vHold = (ViewHolder) view.getTag();
		}
		vHold.txtName.setText(pkgMan.getApplicationLabel(apps.get(position)));
		vHold.txtPkg.setText(apps.get(position).packageName);
		vHold.imgIcon.setImageDrawable(icons.get(position));
		if (is_set != null && is_set.size() > position)
			vHold.imgCheck.setVisibility(is_set.get(position) ? View.VISIBLE
					: View.GONE);
		else
			vHold.imgCheck.setVisibility(View.GONE);
		return rowView;
	}

	private static class ViewHolder {
		TextView txtName;
		TextView txtPkg;
		ImageView imgIcon;
		ImageView imgCheck;
	}
}
