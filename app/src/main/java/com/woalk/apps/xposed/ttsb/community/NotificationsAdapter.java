package com.woalk.apps.xposed.ttsb.community;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.woalk.apps.xposed.ttsb.R;
import com.woalk.apps.xposed.ttsb.community.SQL_Operations.Q;

public class NotificationsAdapter extends
		ArrayAdapter<NotificationsAdapter.Notification> {
	private LayoutInflater inflater;

	public NotificationsAdapter(Context context,
			List<Notification> notifications) {
		super(context, R.layout.item_notification, notifications);

		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public View getView(final int pos, View v, ViewGroup parent) {
		ViewHolder vh;
		if (v == null) {
			v = inflater.inflate(R.layout.item_notification, parent, false);
			vh = new ViewHolder();
			vh.head = (TextView) v.findViewById(R.id.textView_title);
			vh.descr = (TextView) v.findViewById(R.id.textView_descr);
			vh.icon = (ImageView) v.findViewById(R.id.imageIcon);
			v.setTag(vh);
		} else {
			vh = (ViewHolder) v.getTag();
		}
		final Notification n = getItem(pos);
		switch (n.getType()) {
		case Notification.TYPE_COMMENT:
			vh.head.setText(R.string.str_notif_comment);
			vh.descr.setText(getContext().getString(
					R.string.strf_notif_comment, n.getResponsibleUser()));
			vh.icon.setImageResource(R.drawable.ic_action_chat);
			break;
		case Notification.TYPE_MENTION:
			vh.head.setText(R.string.str_notif_mention);
			vh.descr.setText(getContext().getString(
					R.string.strf_notif_mention, n.getResponsibleUser()));
			vh.icon.setImageResource(R.drawable.ic_action_reply);
			break;
		case Notification.TYPE_VOTE_PLUS:
			vh.head.setText(R.string.str_notif_vote);
			vh.descr.setText(getContext().getString(
					R.string.strf_notif_vote_plus, n.getResponsibleUser()));
			vh.icon.setImageResource(R.drawable.ic_action_import_export);
			break;
		case Notification.TYPE_VOTE_MINUS:
			vh.head.setText(R.string.str_notif_vote);
			vh.descr.setText(getContext().getString(
					R.string.strf_notif_vote_minus, n.getResponsibleUser()));
			vh.icon.setImageResource(R.drawable.ic_action_import_export);
			break;
		}

		return v;
	}

	private static class ViewHolder {
		public TextView head;
		public TextView descr;
		public ImageView icon;
	}

	public static class Notification implements Parcelable {
		public static final int TYPE_COMMENT = 1;
		public static final int TYPE_MENTION = 2;
		public static final int TYPE_VOTE_PLUS = 3;
		public static final int TYPE_VOTE_MINUS = 4;

		private int type;
		private String by_user;
		private int submitid;

		public Notification() {
		}

		public Notification(Parcel from) {
			setType(from.readInt());
			setResponsibleUser(from.readString());
			setSubmitId(from.readInt());
		}

		public int getType() {
			return type;
		}

		public void setType(int type) {
			this.type = type;
		}

		public String getResponsibleUser() {
			return by_user;
		}

		public void setResponsibleUser(String by_user) {
			this.by_user = by_user;
		}

		public int getSubmitId() {
			return submitid;
		}

		public void setSubmitId(int submitid) {
			this.submitid = submitid;
		}

		/**
		 * Creates an {@link Intent} containing the submit of this
		 * {@link Notification} for {@link OneSubmitActivity} and starts it.
		 * 
		 * @param context
		 *            The app's {@link Context} to create the Intent with.
		 */
		public void openSubmit(final Context context) {
			Q q = new Q(Database.DATABASE_URL);
			q.addNameValuePair(Database.POST_PIN, Database.COMMUNITY_PIN);
			q.addNameValuePair(Database.POST_FUNCTION,
					Database.FUNCTION_GET_ONE_SUBMIT);
			q.addNameValuePair(Database.POST_SUBMIT,
					String.valueOf(getSubmitId()));
			final AlertDialog progress = new AlertDialog.Builder(context)
					.setMessage(R.string.loadingsync_msg)
					.setView(new ProgressBar(context)).create();
			q.setPreExecuteListener(new Q.PreExecuteListener() {
				@Override
				public void onPreExecute() {
					progress.show();
				}
			});
			q.setDataLoadedListener(new Q.DataLoadedListener() {
				@Override
				public Bundle onDataLoaded(JSONArray data) throws JSONException {
					Bundle bundle = new Bundle();
					JSONObject obj = data.getJSONObject(0);
					String id = obj.getString("id");
					String pkg = obj.getString("package");
					String descr = obj.getString("description");
					String timestamp = obj.getString("timestamp");
					String settings = obj.getString("settings");
					String user = obj.getString("username");
					String user_trust = obj.getString("user_trust");
					String ver = obj.getString("version");
					String votes = obj.getString("votes");
					bundle.putString(OneSubmitActivity.PASS_ID, id);
					bundle.putString(OneSubmitActivity.PASS_APP, pkg);
					bundle.putString(OneSubmitActivity.PASS_DESCR, descr);
					bundle.putString(OneSubmitActivity.PASS_TIMESTAMP,
							timestamp);
					bundle.putString(OneSubmitActivity.PASS_SETTINGS, settings);
					bundle.putString(OneSubmitActivity.PASS_USER, user);
					bundle.putString(OneSubmitActivity.PASS_USER_TRUST,
							user_trust);
					bundle.putString(OneSubmitActivity.PASS_VERSION, ver);
					bundle.putString(OneSubmitActivity.PASS_VOTES, votes);
					return bundle;
				}
			});
			q.setPostExecuteListener(new Q.PostExecuteListener() {
				@SuppressLint("SimpleDateFormat")
				@Override
				public void onPostExecute(Bundle processed) {
					if (processed == null) {
						Toast.makeText(context, R.string.error_try_again,
								Toast.LENGTH_SHORT).show();
						progress.dismiss();
						return;
					}
					Intent intent = new Intent(context, OneSubmitActivity.class);
					String pkg = processed
							.getString(OneSubmitActivity.PASS_APP);
					try {
						intent.putExtra(
								OneSubmitActivity.PASS_APP,
								context.getPackageManager().getApplicationInfo(
										pkg, PackageManager.GET_ACTIVITIES));
					} catch (NameNotFoundException e) {
						Toast.makeText(context, R.string.app_not_installed,
								Toast.LENGTH_SHORT).show();
						e.printStackTrace();
						progress.dismiss();
						return;
					}
					intent.putExtra(OneSubmitActivity.PASS_DESCR,
							processed.getString(OneSubmitActivity.PASS_DESCR));
					DateFormat d_f = new SimpleDateFormat("yyyy-MM-dd-HH-mm");
					d_f.setTimeZone(TimeZone.getTimeZone("GMT"));
					Date timestamp;
					try {
						timestamp = d_f.parse(processed
								.getString(OneSubmitActivity.PASS_TIMESTAMP));
					} catch (ParseException e) {
						timestamp = new Date();
						e.printStackTrace();
					}
					intent.putExtra(OneSubmitActivity.PASS_TIMESTAMP, timestamp);
					intent.putExtra(OneSubmitActivity.PASS_ID, getSubmitId());
					intent.putExtra(OneSubmitActivity.PASS_IS_TOPVOTE, false);
					SharedPreferences sPref = context.getSharedPreferences(
							Database.Preferences.COMMUNITY_PREF_NAME,
							Context.MODE_PRIVATE);
					int topvote_id = sPref.getInt(
							Database.Preferences.PREF_PREFIX_USED_SUBMIT_ID
									+ pkg, -1);
					intent.putExtra(OneSubmitActivity.PASS_INSTALLED,
							topvote_id == getSubmitId());
					intent.putExtra(OneSubmitActivity.PASS_SETTINGS, processed
							.getString(OneSubmitActivity.PASS_SETTINGS));
					intent.putExtra(OneSubmitActivity.PASS_USER,
							processed.getString(OneSubmitActivity.PASS_USER));
					intent.putExtra(
							OneSubmitActivity.PASS_USER_TRUST,
							processed.getString(
									OneSubmitActivity.PASS_USER_TRUST).equals(
									"1"));
					intent.putExtra(OneSubmitActivity.PASS_VERSION, Integer
							.parseInt(processed
									.getString(OneSubmitActivity.PASS_VERSION)));
					intent.putExtra(OneSubmitActivity.PASS_VOTES, Integer
							.parseInt(processed
									.getString(OneSubmitActivity.PASS_VOTES)));
					progress.dismiss();
					context.startActivity(intent);
				}
			});
			q.exec();
		}

		@Override
		public int describeContents() {
			return 0;
		}

		@Override
		public void writeToParcel(Parcel dest, int flags) {
			dest.writeInt(getType());
			dest.writeString(getResponsibleUser());
			dest.writeInt(getSubmitId());
		}

		public static final Parcelable.Creator<Notification> CREATOR = new Parcelable.Creator<Notification>() {

			@Override
			public Notification createFromParcel(Parcel source) {
				return new Notification(source);
			}

			@Override
			public Notification[] newArray(int size) {
				return new Notification[size];
			}
		};
	}

}
