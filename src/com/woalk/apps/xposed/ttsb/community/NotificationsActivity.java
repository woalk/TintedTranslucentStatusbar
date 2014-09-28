package com.woalk.apps.xposed.ttsb.community;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.woalk.apps.xposed.ttsb.R;
import com.woalk.apps.xposed.ttsb.community.SQL_Operations.Q;

public class NotificationsActivity extends Activity {

	protected ListView lv;
	protected NotificationsAdapter lA;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_notifications);

		lv = (ListView) findViewById(R.id.listView1);
		lA = new NotificationsAdapter(this,
				new ArrayList<NotificationsAdapter.Notification>());
		lv.setAdapter(lA);
		lv.setOnItemClickListener(new ListView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				lA.getItem(position).openSubmit(NotificationsActivity.this);
			}
		});

		Submitter.Account acc = Submitter.getSavedAccount(this);
		if (acc == null || acc.isEmpty()) {
			new Submitter.SignInDialog(this).show();
		} else {
			Q q = new Q(Database.DATABASE_URL);
			q.addNameValuePair(Database.POST_PIN, Database.COMMUNITY_PIN);
			q.addNameValuePair(Database.POST_FUNCTION,
					Database.FUNCTION_GET_NOTIFICATIONS);
			acc.addToQ(q);
			final AlertDialog progress = new AlertDialog.Builder(this)
					.setMessage(R.string.loadingsync_msg)
					.setView(new ProgressBar(this)).create();
			q.setPreExecuteListener(new Q.PreExecuteListener() {
				@Override
				public void onPreExecute() {
					progress.show();
				}
			});
			final String KEY_NOTIFICATIONS = "notifs";
			q.setDataLoadedListener(new Q.DataLoadedListener() {

				@Override
				public Bundle onDataLoaded(JSONArray data) throws JSONException {
					ArrayList<NotificationsAdapter.Notification> notifications = new ArrayList<NotificationsAdapter.Notification>();
					for (int i = 0; i < data.length(); i++) {
						JSONObject json_data = data.getJSONObject(i);
						NotificationsAdapter.Notification n = new NotificationsAdapter.Notification();
						String vote_t = json_data.getString("vote_t");
						String comment = json_data.getString("comment");
						String mention = json_data.getString("mention");
						if (!vote_t.equals("null")) {
							n.setType(vote_t.equals("1") ? NotificationsAdapter.Notification.TYPE_VOTE_PLUS
									: NotificationsAdapter.Notification.TYPE_VOTE_MINUS);
						} else if (!comment.equals("null")) {
							n.setType(NotificationsAdapter.Notification.TYPE_COMMENT);
						} else if (!mention.equals("null")) {
							n.setType(NotificationsAdapter.Notification.TYPE_MENTION);
						}
						n.setSubmitId(Integer.parseInt(json_data
								.getString("submit_id")));
						n.setResponsibleUser(json_data.getString("username"));
						notifications.add(n);
					}
					Bundle bundle = new Bundle();
					bundle.putParcelableArrayList(KEY_NOTIFICATIONS,
							notifications);
					return bundle;
				}
			});
			q.setPostExecuteListener(new Q.PostExecuteListener() {

				@Override
				public void onPostExecute(Bundle processed) {
					lA.clear();
					ArrayList<NotificationsAdapter.Notification> notifs = processed
							.getParcelableArrayList(KEY_NOTIFICATIONS);
					lA.addAll(notifs);
					lA.notifyDataSetChanged();
					progress.dismiss();
				}
			});
			q.exec();
		}
	}
}
