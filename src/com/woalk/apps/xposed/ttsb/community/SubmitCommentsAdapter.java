package com.woalk.apps.xposed.ttsb.community;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.SortedMap;

import com.woalk.apps.xposed.ttsb.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class SubmitCommentsAdapter extends ArrayAdapter<String> {
	private Activity context;
	
	protected ApplicationInfo app;
	protected String author;
	protected boolean author_trust;
	protected boolean installed;
	protected String description;
	protected Date timestamp;
	protected int votes;
	protected SortedMap<String, String> settings;
	protected List<String> comments;
	protected List<String> users;
	protected List<Boolean> users_trust;
	
	private PackageManager pkgMan;

	public SubmitCommentsAdapter(Activity context, List<String> comments) {
		super(context, 0, comments);
		this.context = context;
		this.comments = comments;
		this.users = new ArrayList<String>();
		this.users_trust = new ArrayList<Boolean>();
		pkgMan = context.getPackageManager();
	}


	public void addBegin() {
		this.comments.add(null);
		this.users_trust.add(null);
		this.users.add(null);
	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {
		if (position == 0) {
			LayoutInflater inflater = context.getLayoutInflater();
			View rowView = inflater.inflate(R.layout.item_one_submit, parent, false);

			TextView tv_app = (TextView) rowView.findViewById(R.id.textView_app);
			TextView tv_pkg = (TextView) rowView.findViewById(R.id.textView_package);
			TextView tv_descr = (TextView) rowView.findViewById(R.id.textView_descr);
			TextView tv_author = (TextView) rowView.findViewById(R.id.textView_user);
			TextView tv_timestamp = (TextView) rowView.findViewById(R.id.textView_timestamp);
			TextView tv_chosen = (TextView) rowView.findViewById(R.id.textView_chosen);
			TextView tv_votes = (TextView) rowView.findViewById(R.id.textView_votes);

			tv_app.setText(pkgMan.getApplicationLabel(app));
			tv_pkg.setText(app.packageName);
			tv_descr.setText(description);
			tv_author.setText(Html.fromHtml(context.getString(R.string.community_prefix_by) + " <b>" + author + "</b>"));
			tv_author.setCompoundDrawablesWithIntrinsicBounds(null, null, author_trust ?
					context.getResources().getDrawable(R.drawable.ic_community_trust_small) : null, null);
			DateFormat d_f = DateFormat.getInstance();
			String date = d_f.format(timestamp); 
			tv_timestamp.setText(context.getString(R.string.community_prefix_at) + " " + date);
			tv_chosen.setVisibility(installed ? View.VISIBLE : View.GONE);
			
			int this_votes = votes;
			String str_votes = String.valueOf(this_votes);
			if (this_votes > 0) {
				str_votes = "+" + str_votes;
				tv_votes.setTextColor(context.getResources().getColor(R.color.votes_positive));
			} else if (this_votes < 0) {
				tv_votes.setTextColor(context.getResources().getColor(R.color.votes_negative));
			} else {
				str_votes = "±" + str_votes;
				tv_votes.setTextColor(Color.BLACK);
			}
			tv_votes.setText(str_votes);
			
			Button btn_choose = (Button) rowView.findViewById(R.id.button_choose);
			Button btn_choose_layout = (Button) rowView.findViewById(R.id.button_choose_layout);
			Button btn_view = (Button) rowView.findViewById(R.id.button_view_settings);
			
			btn_view.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					List<String> act = new ArrayList<String>();
					List<String> set = new ArrayList<String>();
					ActivityInfo[] act_inf = null;
					try {
						act_inf = pkgMan.getPackageInfo(app.packageName, PackageManager.GET_ACTIVITIES).activities;
					} catch (NameNotFoundException e) {
						e.printStackTrace();
					}
					if (act_inf != null) {
						if (settings.containsKey("")) {
							act.add("All");
							set.add(settings.get(""));
						}
						for (ActivityInfo inf : act_inf) {
							if (settings.containsKey(inf.name)) {
								act.add(inf.name);
								set.add(settings.get(inf.name));
							}
						}
					}
					AlertDialog.Builder builder = new AlertDialog.Builder(context);
					builder.setAdapter(new SettingsPreviewAdapter(context, act, set), null);
					builder.setTitle(R.string.title_view_settings);
					builder.setCancelable(true);
					builder.setNegativeButton(android.R.string.ok, null);
					builder.show();
				}
			});

			return rowView;
		} else {
			/*
			View rowView;
			if (view == null) {
				LayoutInflater inflater = context.getLayoutInflater();
				rowView = inflater.inflate(R.layout.item_comment, parent, false);
				
			} else {
				rowView = view;
				
			}
			return rowView;
			*/
			return null;
		}
	}
	
	@Override
	public boolean isEnabled(int position) {
		return position != 0;
	}
	
}
