package com.woalk.apps.xposed.ttsb.community;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.woalk.apps.xposed.ttsb.R;

public class UserSubmitsAdapter extends ArrayAdapter<String> {
	public List<Integer> votes;
	public List<Integer> ids;
	public List<ApplicationInfo> apps;
	public List<String> pkgs;
	public List<String> descriptions;
	public List<Integer> versions;
	public List<String> timestamps;
	public List<String> settings;

	public String username;
	public int user_votes;
	public boolean user_trust;

	public List<Integer> disabled_items;

	protected Activity context;
	private LayoutInflater inflater;
	private PackageManager pkg_man;

	public UserSubmitsAdapter(Activity context, List<String> descr) {
		super(context, R.layout.item_submit, descr);
		this.context = context;
		this.inflater = context.getLayoutInflater();
		this.pkg_man = context.getPackageManager();

		this.votes = new ArrayList<Integer>();
		this.ids = new ArrayList<Integer>();
		this.apps = new ArrayList<ApplicationInfo>();
		this.pkgs = new ArrayList<String>();
		this.descriptions = descr;
		this.versions = new ArrayList<Integer>();
		this.settings = new ArrayList<String>();
		this.timestamps = new ArrayList<String>();
		this.disabled_items = new ArrayList<Integer>();
	}

	public UserSubmitsAdapter(UserSubmitsAdapter copy) {
		super(copy.context, R.layout.item_submit, copy.descriptions);
		this.context = copy.context;
		this.ids = new ArrayList<Integer>(copy.ids);
		this.votes = new ArrayList<Integer>(copy.votes);
		this.apps = new ArrayList<ApplicationInfo>(copy.apps);
		this.pkgs = new ArrayList<String>(copy.pkgs);
		this.descriptions = new ArrayList<String>(copy.descriptions);
		this.versions = new ArrayList<Integer>(copy.versions);
		this.username = copy.username;
		this.user_trust = copy.user_trust;
		this.timestamps = new ArrayList<String>(copy.timestamps);
		this.username = copy.username;
		this.user_votes = copy.user_votes;
		this.disabled_items = new ArrayList<Integer>(copy.disabled_items);

		this.inflater = context.getLayoutInflater();
	}

	public void addBegin() {
		ids.add(null);
		votes.add(null);
		apps.add(null);
		pkgs.add(null);
		descriptions.add(null);
		versions.add(null);
		timestamps.add(null);
		settings.add(null);
	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {
		if (position == 0) {
			View rowView;
			if (view == null || view.getId() != R.id.item_one_user)
				rowView = inflater.inflate(R.layout.item_one_user, parent,
						false);
			else
				rowView = view;

			rowView.setBackground(new ColorDrawable(0xACFFFFFF));

			TextView tv1 = (TextView) rowView.findViewById(R.id.tV_user_name);
			TextView tv2 = (TextView) rowView.findViewById(R.id.tV_user_votes);

			tv1.setText(username);
			tv1.setCompoundDrawablesWithIntrinsicBounds(
					null,
					null,
					user_trust ? context.getResources().getDrawable(
							R.drawable.ic_community_trust) : null, null);

			StringBuilder votesB = new StringBuilder();
			if (user_votes < 0) {
				tv2.setTextColor(context.getResources().getColor(
						R.color.votes_negative));
			} else if (user_votes > 0) {
				votesB.append('+');
				tv2.setTextColor(context.getResources().getColor(
						R.color.votes_positive));
			} else {
				votesB.append('±');
				tv2.setTextColor(context.getResources().getColor(
						android.R.color.primary_text_light));
			}
			votesB.append(user_votes);
			tv2.setText(votesB.toString());

			return rowView;
		} else {
			int pos = position; // for quick changing
			View rowView;
			if (view == null || view.getId() != R.id.item_submit) {
				rowView = inflater.inflate(R.layout.item_submit, parent, false);
			} else
				rowView = view;

			TextView tvVote = (TextView) rowView.findViewById(R.id.textVotes);
			TextView tvDescr = (TextView) rowView
					.findViewById(R.id.textDescription);
			TextView tvUser = (TextView) rowView.findViewById(R.id.textUser);
			TextView tvTime = (TextView) rowView.findViewById(R.id.textTime);
			TextView tvChosen = (TextView) rowView
					.findViewById(R.id.textView_chosen);

			CharSequence label;
			if (apps.get(pos) != null) {
				label = pkg_man.getApplicationLabel(apps.get(pos));
			} else {
				label = pkgs.get(pos);
				rowView.setAlpha(0.6f);
				if (!disabled_items.contains(pos)) {
					disabled_items.add(pos);
				}
			}
			tvDescr.setText(Html.fromHtml("<b>" + label + "</b><br />"
					+ descriptions.get(pos)));
			tvUser.setText(context.getString(R.string.community_prefix_by)
					+ " " + username);
			tvUser.setCompoundDrawablesWithIntrinsicBounds(
					null,
					null,
					user_trust ? context.getResources().getDrawable(
							R.drawable.ic_community_trust_small) : null, null);
			tvTime.setText(context.getString(R.string.community_prefix_at)
					+ " " + timestamps.get(pos) + " "
					+ context.getString(R.string.community_prefix_for_ver)
					+ " " + String.valueOf(versions.get(pos)));

			int this_votes = votes.get(pos);
			String str_votes = String.valueOf(this_votes);
			if (this_votes > 0) {
				str_votes = "+" + str_votes;
				tvVote.setTextColor(context.getResources().getColor(
						R.color.votes_positive));
			} else if (this_votes < 0) {
				tvVote.setTextColor(context.getResources().getColor(
						R.color.votes_negative));
			} else {
				str_votes = "±" + str_votes;
				tvVote.setTextColor(Color.BLACK);
			}
			tvVote.setText(str_votes);

			tvChosen.setVisibility(View.GONE);

			return rowView;

		}
	}

	@Override
	public boolean isEnabled(int position) {
		if (position == 0 || disabled_items.contains(position))
			return false;
		else
			return super.isEnabled(position);
	}
}
