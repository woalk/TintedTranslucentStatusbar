package com.woalk.apps.xposed.ttsb.community;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.woalk.apps.xposed.ttsb.R;

import android.app.Activity;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class SubmitsAdapter extends ArrayAdapter<String> {
	public List<Integer> votes;
	public List<String> descriptions;
	public List<Integer> versions;
	public List<String> users;
	public List<Boolean> users_trust;
	public List<Date> timestamps;
	public List<String> settings;

	public ApplicationInfo app;
	public int selected_pos;

	protected Activity context;
	private PackageManager pkgMan;

	public SubmitsAdapter(Activity context, List<String> descr) {
		super(context, R.layout.item_submit, descr);
		this.context = context;
		this.pkgMan = context.getPackageManager();

		this.votes = new ArrayList<Integer>();
		this.descriptions = descr;
		this.versions = new ArrayList<Integer>();
		this.users = new ArrayList<String>();
		this.users_trust = new ArrayList<Boolean>();
		this.settings = new ArrayList<String>();
		this.timestamps = new ArrayList<Date>();
	}
	public SubmitsAdapter(SubmitsAdapter copy) {
		super(copy.context, R.layout.item_submit, copy.descriptions);
		this.context = copy.context;
		this.votes = new ArrayList<Integer>(copy.votes);
		this.descriptions = new ArrayList<String>(copy.descriptions);
		this.versions = new ArrayList<Integer>(copy.versions);
		this.users = new ArrayList<String>(copy.users);
		this.users_trust = new ArrayList<Boolean>(copy.users_trust);
		this.timestamps = new ArrayList<Date>(copy.timestamps);
		this.app = copy.app;
		this.selected_pos = copy.selected_pos;

		this.pkgMan = context.getPackageManager();
	}

	public void addBegin() {
		votes.add(null);
		descriptions.add(null);
		versions.add(null);
		users.add(null);
		users_trust.add(null);
		timestamps.add(null);
	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {
		if (position == 0) {
			LayoutInflater inflater = context.getLayoutInflater();
			View rowView = inflater.inflate(R.layout.item_applist, parent, false);

			rowView.setBackground(new ColorDrawable(0xACFFFFFF));

			TextView tv1 = (TextView) rowView.findViewById(R.id.textName);
			TextView tv2 = (TextView) rowView.findViewById(R.id.textPkg);
			ImageView img1 = (ImageView) rowView.findViewById(R.id.imageIcon);
			ImageView img2 = (ImageView) rowView.findViewById(R.id.imageCheck);

			tv1.setText(pkgMan.getApplicationLabel(app));
			tv2.setText(app.packageName);
			img1.setImageDrawable(pkgMan.getApplicationIcon(app));
			img2.setVisibility(View.GONE);

			return rowView;
		} else {
			int pos = position; // for quick changing
			View rowView;
			if (view == null || view.getId() != R.layout.item_submit) {
				LayoutInflater inflater = context.getLayoutInflater();
				rowView = inflater.inflate(R.layout.item_submit, parent, false);
			} else
				rowView = view;

			TextView tvVote = (TextView) rowView.findViewById(R.id.textVotes);
			TextView tvDescr = (TextView) rowView.findViewById(R.id.textDescription);
			TextView tvUser = (TextView) rowView.findViewById(R.id.textUser);
			TextView tvTime = (TextView) rowView.findViewById(R.id.textTime);
			TextView tvChosen = (TextView) rowView.findViewById(R.id.textView_chosen);

			DateFormat d_f = DateFormat.getInstance();
			String date = d_f.format(timestamps.get(pos)); 

			tvDescr.setText(descriptions.get(pos));
			tvUser.setText(context.getString(R.string.community_prefix_by) + " " + users.get(pos));
			tvUser.setCompoundDrawablesWithIntrinsicBounds(null, null,
					users_trust.get(pos) ? context.getResources().getDrawable(R.drawable.ic_community_trust_small) : null, null);
			tvTime.setText(context.getString(R.string.community_prefix_at) + " " + date + " " +
					context.getString(R.string.community_prefix_for_ver) + " " + String.valueOf(versions.get(pos)));

			int this_votes = votes.get(pos);
			String str_votes = String.valueOf(this_votes);
			if (this_votes > 0) {
				str_votes = "+" + str_votes;
				tvVote.setTextColor(context.getResources().getColor(R.color.votes_positive));
			} else if (this_votes < 0) {
				tvVote.setTextColor(context.getResources().getColor(R.color.votes_negative));
			} else {
				str_votes = "±" + str_votes;
				tvVote.setTextColor(Color.BLACK);
			}
			tvVote.setText(str_votes);

			tvChosen.setVisibility(pos == selected_pos + 1 ? View.VISIBLE : View.GONE);

			return rowView;

		}
	}

	@Override
	public boolean isEnabled(int position) {
		if (position == 0) return false;
		else return super.isEnabled(position);
	}
}
