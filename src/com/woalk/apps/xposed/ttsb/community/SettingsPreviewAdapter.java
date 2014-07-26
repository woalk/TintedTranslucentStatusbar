package com.woalk.apps.xposed.ttsb.community;

import java.util.List;

import com.woalk.apps.xposed.ttsb.R;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class SettingsPreviewAdapter extends ArrayAdapter<String> {
	private Activity context;
	protected List<String> activities;
	protected List<String> settings;

	public SettingsPreviewAdapter(Activity context, List<String> activities, List<String> settings) {
		super(context, R.layout.item_settings_preview, activities);
		this.activities = activities;
		this.settings = settings;
		this.context = context;
	}
	
	@Override
	public View getView(int position, View view, ViewGroup parent) {
		View rowView;
		if (view == null) {
			LayoutInflater inflater = context.getLayoutInflater();
			rowView = inflater.inflate(R.layout.item_settings_preview, parent, false);
		} else
			rowView = view;
		
		TextView tv_t = (TextView) rowView.findViewById(R.id.text_title);
		TextView tv_s = (TextView) rowView.findViewById(R.id.text_code);
		
		tv_t.setText(activities.get(position));
		tv_s.setText(settings.get(position));
		
		return rowView;
	}
	
	@Override
	public boolean isEnabled(int position) {
		return false;
	}
}
