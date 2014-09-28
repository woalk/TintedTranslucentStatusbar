package com.woalk.apps.xposed.ttsb.community;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map.Entry;
import java.util.SortedMap;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.woalk.apps.xposed.ttsb.Helpers;
import com.woalk.apps.xposed.ttsb.R;
import com.woalk.apps.xposed.ttsb.Settings;
import com.woalk.apps.xposed.ttsb.community.SQL_Operations.CustomQ;

public class SubmitCommentsAdapter extends ArrayAdapter<String> {
	private Activity context;

	protected ApplicationInfo app;
	protected String author;
	protected boolean author_trust;
	protected int id;
	protected boolean is_topvote;
	protected boolean installed;
	protected String description;
	protected Date timestamp;
	protected int votes;
	protected SortedMap<String, String> settings;
	protected List<Integer> ids;
	protected List<String> comments;
	protected List<String> users;
	protected List<Boolean> users_trust;
	protected List<Date> timestamps;
	protected List<Integer> spamvotes;

	private PackageManager pkgMan;
	private SharedPreferences sPref;
	private DateFormat d_f;

	protected boolean isLoading;

	@SuppressLint("WorldReadableFiles")
	@SuppressWarnings("deprecation")
	public SubmitCommentsAdapter(Activity context, List<String> comments) {
		super(context, 0, comments);
		this.context = context;
		this.comments = comments;
		this.ids = new ArrayList<Integer>();
		this.users = new ArrayList<String>();
		this.users_trust = new ArrayList<Boolean>();
		this.timestamps = new ArrayList<Date>();
		this.spamvotes = new ArrayList<Integer>();
		pkgMan = context.getPackageManager();
		sPref = context.getSharedPreferences(Helpers.TTSB_PREFERENCES,
				Context.MODE_WORLD_READABLE);
		d_f = DateFormat.getInstance();
		isLoading = false;
	}

	public void addBegin() {
		this.ids.add(null);
		this.comments.add(null);
		this.users_trust.add(null);
		this.users.add(null);
		this.timestamps.add(null);
		this.spamvotes.add(null);

		this.ids.add(null);
		this.comments.add(null);
		this.users_trust.add(null);
		this.users.add(null);
		this.timestamps.add(null);
		this.spamvotes.add(null);
	}

	public void add(OneSubmitActivity.Comment comment) {
		ids.add(comments.size() - 1, comment.id);
		comments.add(comments.size() - 1, comment.comment);
		users.add(users.size() - 1, comment.user);
		users_trust.add(users_trust.size() - 1, comment.user_trust);
		timestamps.add(timestamps.size() - 1, comment.timestamp);
		spamvotes.add(spamvotes.size() - 1, comment.spamvotes);
	}

	public void addAll(List<OneSubmitActivity.Comment> comments) {
		for (OneSubmitActivity.Comment comment : comments) {
			this.comments.add(this.comments.size() - 1, comment.comment);
			this.ids.add(this.comments.size() - 1, comment.id);
			this.users.add(this.users.size() - 1, comment.user);
			this.users_trust.add(this.users_trust.size() - 1,
					comment.user_trust);
			this.timestamps.add(this.timestamps.size() - 1, comment.timestamp);
			this.spamvotes.add(this.spamvotes.size() - 1, comment.spamvotes);
		}
	}

	public void removeAt(int i) {
		this.ids.remove(i);
		this.comments.remove(i);
		this.users.remove(i);
		this.users_trust.remove(i);
		this.timestamps.remove(i);
		this.spamvotes.remove(i);
	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {
		if (position == 0) {
			LayoutInflater inflater = context.getLayoutInflater();
			View rowView = inflater.inflate(R.layout.item_one_submit, parent,
					false);

			TextView tv_app = (TextView) rowView
					.findViewById(R.id.textView_app);
			TextView tv_pkg = (TextView) rowView
					.findViewById(R.id.textView_package);
			TextView tv_descr = (TextView) rowView
					.findViewById(R.id.textView_descr);
			TextView tv_author = (TextView) rowView
					.findViewById(R.id.textView_user);
			TextView tv_timestamp = (TextView) rowView
					.findViewById(R.id.textView_timestamp);
			TextView tv_chosen = (TextView) rowView
					.findViewById(R.id.textView_chosen);
			TextView tv_votes = (TextView) rowView
					.findViewById(R.id.textView_votes);

			tv_app.setText(pkgMan.getApplicationLabel(app));
			tv_pkg.setText(app.packageName);
			tv_descr.setText(Html.fromHtml(description));
			tv_author.setText(Html.fromHtml(context
					.getString(R.string.community_prefix_by)
					+ " <b>"
					+ author
					+ "</b>"));
			tv_author.setCompoundDrawablesWithIntrinsicBounds(
					null,
					null,
					author_trust ? context.getResources().getDrawable(
							R.drawable.ic_community_trust_small) : null, null);
			String date = d_f.format(timestamp);
			tv_timestamp.setText(context
					.getString(R.string.community_prefix_at) + " " + date);
			tv_chosen.setVisibility(installed ? View.VISIBLE : View.GONE);

			int this_votes = votes;
			String str_votes = String.valueOf(this_votes);
			if (this_votes > 0) {
				str_votes = "+" + str_votes;
				tv_votes.setTextColor(context.getResources().getColor(
						R.color.votes_positive));
			} else if (this_votes < 0) {
				tv_votes.setTextColor(context.getResources().getColor(
						R.color.votes_negative));
			} else {
				str_votes = "±" + str_votes;
				tv_votes.setTextColor(Color.BLACK);
			}
			tv_votes.setText(str_votes);

			tv_author.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent intent = new Intent(context, OneUserActivity.class);
					intent.putExtra(OneUserActivity.PASS_USERNAME, author);
					context.startActivity(intent);
				}
			});

			Button btn_upvote = (Button) rowView.findViewById(R.id.button_up);
			Button btn_downvote = (Button) rowView
					.findViewById(R.id.button_down);

			btn_upvote.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					vote(VOTETYPE_UP);
				}
			});
			btn_downvote.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					vote(VOTETYPE_DOWN);
				}
			});

			Button btn_choose = (Button) rowView
					.findViewById(R.id.button_choose);
			Button btn_choose_layout = (Button) rowView
					.findViewById(R.id.button_choose_layout);
			Button btn_view = (Button) rowView
					.findViewById(R.id.button_view_settings);

			final boolean containsApp = Settings.Loader.containsPackage(sPref,
					app.packageName);
			btn_choose_layout.setEnabled(containsApp);

			btn_choose.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (containsApp) {
						AlertDialog.Builder builder = new AlertDialog.Builder(
								context);
						builder.setMessage(R.string.msg_already_set);
						builder.setTitle(R.string.title_already_set);
						builder.setIcon(R.drawable.ic_warning_holo_light);
						builder.setNegativeButton(android.R.string.cancel, null);
						builder.setNeutralButton(R.string.merge_already_set,
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										new AsyncSave().execute(SAVEMODE_MERGE);
									}
								});
						builder.setPositiveButton(
								R.string.overwrite_already_set,
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										new AsyncSave()
												.execute(SAVEMODE_OVERWRITE);
									}
								});
						builder.show();
					} else
						new AsyncSave().execute(SAVEMODE_JUSTSAVE);
				}
			});
			btn_choose_layout.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					AlertDialog.Builder builder = new AlertDialog.Builder(
							context);
					builder.setMessage(R.string.msg_already_set_layout);
					builder.setTitle(R.string.title_already_set_layout);
					builder.setIcon(R.drawable.ic_warning_holo_light);
					builder.setNegativeButton(android.R.string.no, null);
					builder.setPositiveButton(android.R.string.yes,
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									new AsyncSave().execute(SAVEMODE_LAYOUT);
								}
							});
					builder.show();
				}
			});
			btn_view.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					List<String> act = new ArrayList<String>();
					List<String> set = new ArrayList<String>();
					for (Entry<String, String> entry : settings.entrySet()) {
						act.add(entry.getKey());
						set.add(entry.getValue());
					}
					AlertDialog.Builder builder = new AlertDialog.Builder(
							context);
					builder.setAdapter(new SettingsPreviewAdapter(context, act,
							set), null);
					builder.setTitle(R.string.title_view_settings);
					builder.setCancelable(true);
					builder.setNegativeButton(android.R.string.ok, null);
					builder.show();
				}
			});

			return rowView;
		} else if (position == getCount() - 1) {
			ProgressBar pbar = new ProgressBar(parent.getContext());
			pbar.setVisibility(isLoading ? View.VISIBLE : View.INVISIBLE);
			final float scale = getContext().getResources().getDisplayMetrics().density;
			int pixels = (int) (36 * scale + 0.5f);
			pbar.setMinimumHeight(pixels);
			return pbar;
		} else {
			View rowView;
			if (view == null || view.getId() != R.layout.item_comment) {
				LayoutInflater inflater = context.getLayoutInflater();
				rowView = inflater
						.inflate(R.layout.item_comment, parent, false);
			} else {
				rowView = view;
			}
			TextView tv_comment = (TextView) rowView
					.findViewById(R.id.textDescription);
			TextView tv_user = (TextView) rowView.findViewById(R.id.textUser);
			TextView tv_timestamp = (TextView) rowView
					.findViewById(R.id.textTime);

			tv_comment.setText(comments.get(position));
			tv_user.setText(Html.fromHtml(context
					.getString(R.string.community_prefix_by)
					+ " <b>"
					+ users.get(position) + "</b>"));
			tv_user.setCompoundDrawablesWithIntrinsicBounds(null, null,
					users_trust.get(position) ? context.getResources()
							.getDrawable(R.drawable.ic_community_trust_small)
							: null, null);
			String date = d_f.format(timestamps.get(position));
			tv_timestamp.setText(context
					.getString(R.string.community_prefix_at) + " " + date);

			rowView.setAlpha(0xFF - (spamvotes.get(position) * 0x01));

			return rowView;
		}
	}

	public static final byte SAVEMODE_OVERWRITE = 0;
	public static final byte SAVEMODE_MERGE = 1;
	public static final byte SAVEMODE_JUSTSAVE = 2;
	public static final byte SAVEMODE_LAYOUT = 4;

	protected class AsyncSave extends AsyncTask<Byte, Void, Void> {

		@Override
		protected Void doInBackground(Byte... params) {
			if (params[0] == SAVEMODE_LAYOUT)
				saveLayout();
			else
				save(params[0]);
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			Toast.makeText(context, R.string.settings_synced_success,
					Toast.LENGTH_SHORT).show();
		}

	}

	public void save(byte savemode) {
		if (savemode == SAVEMODE_OVERWRITE)
			Settings.Saver.deleteEverythingFromPackage(sPref, app.packageName);
		for (Entry<String, String> entry : settings.entrySet()) {
			Settings.Parser parser = new Settings.Parser(entry.getValue());
			parser.parseToSettings();
			Settings.Saver.save(sPref, app.packageName, entry.getKey(), parser);
		}
		SharedPreferences sPref_c = context.getSharedPreferences(
				Database.Preferences.COMMUNITY_PREF_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor edit = sPref_c.edit();
		if (savemode == SAVEMODE_MERGE) {
			edit.remove(Database.Preferences.PREF_PREFIX_IS_TOPVOTED_USED
					+ app.packageName);
			edit.remove(Database.Preferences.PREF_PREFIX_USED_SUBMIT_ID
					+ app.packageName);
		} else {
			edit.putBoolean(Database.Preferences.PREF_PREFIX_IS_TOPVOTED_USED
					+ app.packageName, is_topvote);
			edit.putInt(Database.Preferences.PREF_PREFIX_USED_SUBMIT_ID
					+ app.packageName, id);
		}
		edit.apply();
		installed = savemode != SAVEMODE_MERGE;
	}

	public void saveLayout() {
		ActivityInfo[] act_inf = null;
		try {
			act_inf = pkgMan.getPackageInfo(app.packageName,
					PackageManager.GET_ACTIVITIES).activities;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		if (act_inf != null) {
			for (ActivityInfo inf : act_inf) {
				if (!settings.containsKey(inf.name)
						&& Settings.Loader.contains(sPref, app.packageName,
								inf.name)) {
					if (settings.containsKey("")) {
						Settings.Parser parser = Settings.Loader.load(sPref,
								app.packageName, inf.name);
						Settings.Parser parser_new = new Settings.Parser(
								settings.get(""));
						parser_new.parseToSettings();
						Settings.Setting setting = parser.getSetting();
						int s_plus_sav = setting.rules.s_plus;
						int n_plus_sav = setting.rules.n_plus;
						setting.rules = parser_new.getSetting().rules;
						setting.rules.s_plus = s_plus_sav;
						setting.rules.n_plus = n_plus_sav;
						parser.setSetting(setting);
						Settings.Saver.save(sPref, app.packageName, inf.name,
								parser);
					}
				}
			}
		}
		for (Entry<String, String> entry : settings.entrySet()) {
			if (!Settings.Loader.contains(sPref, app.packageName,
					entry.getKey()))
				continue;
			Settings.Parser parser = Settings.Loader.load(sPref,
					app.packageName, entry.getKey());
			Settings.Parser parser_new = new Settings.Parser(entry.getValue());
			parser_new.parseToSettings();
			Settings.Setting setting = parser.getSetting();
			int s_plus_sav = setting.rules.s_plus;
			int n_plus_sav = setting.rules.n_plus;
			setting.rules = parser_new.getSetting().rules;
			setting.rules.s_plus = s_plus_sav;
			setting.rules.n_plus = n_plus_sav;
			parser.setSetting(setting);
			Settings.Saver.save(sPref, app.packageName, entry.getKey(), parser);
		}
		if (!installed) {
			SharedPreferences sPref_c = context.getSharedPreferences(
					Database.Preferences.COMMUNITY_PREF_NAME,
					Context.MODE_PRIVATE);
			SharedPreferences.Editor edit = sPref_c.edit();
			edit.remove(Database.Preferences.PREF_PREFIX_IS_TOPVOTED_USED
					+ app.packageName);
			edit.remove(Database.Preferences.PREF_PREFIX_USED_SUBMIT_ID
					+ app.packageName);
			edit.apply();
		}
	}

	@Override
	public boolean isEnabled(int position) {
		return position != 0 && position != getCount() - 1;
	}

	private static final boolean VOTETYPE_UP = true;
	private static final boolean VOTETYPE_DOWN = false;

	private void vote(final boolean votetype) {
		String vote_t = votetype ? "1" : "0";

		CustomQ q = new CustomQ(Database.DATABASE_URL);
		q.addNameValuePair(Database.POST_PIN, Database.COMMUNITY_PIN);
		q.addNameValuePair(Database.POST_FUNCTION, Database.FUNCTION_VOTE);
		Submitter.Account acc = Submitter.getSavedAccount(context);
		if (acc == null || acc.isEmpty()) {
			new Submitter.SignInDialog(context).show();
			return;
		}
		acc.addToQ(q);
		q.addNameValuePair(Database.POST_VOTE_TYPE, vote_t);
		q.addNameValuePair(Database.POST_SUBMIT, String.valueOf(id));

		final AlertDialog progress = new AlertDialog.Builder(context)
				.setMessage(R.string.loadingsync_msg)
				.setView(new ProgressBar(context)).create();
		q.setPreExecuteListener(new CustomQ.PreExecuteListener() {
			@Override
			public void onPreExecute() {
				progress.show();
			}
		});
		final String KEY_RESULT = "result";
		q.setHttpResultListener(new CustomQ.HttpResultListener() {
			@Override
			public Bundle onHttpResult(String result) {
				Bundle bundle = new Bundle();
				try {
					bundle.putInt(KEY_RESULT, Integer.valueOf(result));
				} catch (Throwable e) {
					bundle.putString(KEY_RESULT, result);
					e.printStackTrace();
				}
				return bundle;
			}
		});
		q.setPostExecuteListener(new CustomQ.PostExecuteListener() {
			@Override
			public void onPostExecute(Bundle processed) {
				String procStr = processed.getString(KEY_RESULT);
				if (procStr != null) {
					String str;
					if (procStr.equals("dlyos")) {
						str = context.getString(R.string.dlyos);
					} else {
						str = context.getString(R.string.error_try_again);
					}
					Toast.makeText(context, str, Toast.LENGTH_SHORT).show();
				} else {
					votes = processed.getInt(KEY_RESULT);
					notifyDataSetChanged();
				}
				progress.dismiss();
			}
		});
		q.exec();
	}
}
