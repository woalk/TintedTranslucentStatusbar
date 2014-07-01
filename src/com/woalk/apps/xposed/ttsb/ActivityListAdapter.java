package com.woalk.apps.xposed.ttsb;

import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ActivityListAdapter extends ArrayAdapter<ComparableActivityInfo> {
	
	public final Activity context;
	public List<ComparableActivityInfo> activities;

	public ActivityListAdapter(Activity context, List<ComparableActivityInfo> activities) {
		super(context, R.layout.item_applist, activities);
		this.context = context;
		
		this.activities = activities;
	}

	@SuppressLint("ViewHolder")
	@Override
	public View getView(int position, View view, ViewGroup parent) {
		LayoutInflater inflater = context.getLayoutInflater();
		View rowView = inflater.inflate(R.layout.item_activitylist, null, true);
		TextView txt = (TextView) rowView.findViewById(R.id.textActivity);
		ImageView imgCheck = (ImageView) rowView.findViewById(R.id.imageCheck);
		if (activities != null && activities.size() > position) {
			txt.setText(activities.get(position).name);
			if (activities.get(position).is_set) imgCheck.setVisibility(View.VISIBLE);
		}
		return rowView;
	}

}
