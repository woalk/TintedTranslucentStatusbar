package com.woalk.apps.xposed.ttsb;

import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class ViewSettingsPackListAdapter extends
		ArrayAdapter<Settings.Setting.ViewSettingsPack> {

	public final Activity context;
	public List<Settings.Setting.ViewSettingsPack> vsetpks;

	public ViewSettingsPackListAdapter(Activity context,
			List<Settings.Setting.ViewSettingsPack> vsetpks) {
		super(context, R.layout.item_applist, vsetpks);
		this.context = context;

		this.vsetpks = vsetpks;
	}

	@SuppressLint("ViewHolder")
	@Override
	public View getView(int position, View view, ViewGroup parent) {
		LayoutInflater inflater = context.getLayoutInflater();
		View rowView = inflater.inflate(android.R.layout.simple_list_item_1,
				null, true);
		TextView txt = (TextView) rowView.findViewById(android.R.id.text1);
		if (vsetpks != null && vsetpks.size() > position) {
			Settings.Setting.ViewSettingsPack vsetpk = vsetpks.get(position);
			txt.setText(Settings.Parser.parseViewSettingsPackToString(vsetpk));
		}
		return rowView;
	}

}
