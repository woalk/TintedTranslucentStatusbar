package com.woalk.apps.xposed.ttsb.community;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.SortedMap;
import java.util.TimeZone;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.R.color;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.woalk.apps.xposed.ttsb.Helpers;
import com.woalk.apps.xposed.ttsb.R;
import com.woalk.apps.xposed.ttsb.Settings;
import com.woalk.apps.xposed.ttsb.community.SQL_Operations.CustomQ;
import com.woalk.apps.xposed.ttsb.community.SQL_Operations.Q;

public class OneSubmitActivity extends Activity {
	public static final String PASS_APP = Helpers.TTSB_PACKAGE_NAME
			+ ".community.OneSubmitActivity.PASS_APP"; // Parcelable
	public static final String PASS_DESCR = Helpers.TTSB_PACKAGE_NAME
			+ ".community.OneSubmitActivity.PASS_DESCR";
	public static final String PASS_ID = Helpers.TTSB_PACKAGE_NAME
			+ ".community.OneSubmitActivity.PASS_ID";
	public static final String PASS_IS_TOPVOTE = Helpers.TTSB_PACKAGE_NAME
			+ ".community.OneSubmitActivity.PASS_IS_TOPVOTE";
	public static final String PASS_VOTES = Helpers.TTSB_PACKAGE_NAME
			+ ".community.OneSubmitActivity.PASS_VOTES";
	public static final String PASS_VERSION = Helpers.TTSB_PACKAGE_NAME
			+ ".community.OneSubmitActivity.PASS_VERSION";
	public static final String PASS_INSTALLED = Helpers.TTSB_PACKAGE_NAME
			+ ".community.OneSubmitActivity.PASS_INSTALLED";
	public static final String PASS_USER = Helpers.TTSB_PACKAGE_NAME
			+ ".community.OneSubmitActivity.PASS_USER";
	public static final String PASS_USER_TRUST = Helpers.TTSB_PACKAGE_NAME
			+ ".community.OneSubmitActivity.PASS_USER_TRUST";
	public static final String PASS_TIMESTAMP = Helpers.TTSB_PACKAGE_NAME
			+ ".community.OneSubmitActivity.PASS_TIMESTAMP"; // Serializable
	public static final String PASS_SETTINGS = Helpers.TTSB_PACKAGE_NAME
			+ ".community.OneSubmitActivity.PASS_SETTINGS";

	protected ApplicationInfo app;
	protected String description;
	protected int id;
	protected boolean is_topvote;
	protected int votes;
	protected int version;
	protected boolean installed;
	protected String user;
	protected boolean user_trust;
	protected Date timestamp;
	protected SortedMap<String, String> settings;

	protected ListView lv;
	protected SubmitCommentsAdapter lA;

	protected EditText edit_comment;
	protected TextView tv_comment_charremain;
	protected ImageButton btn_send;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_one_submit);
		getActionBar().setDisplayHomeAsUpEnabled(true);

		Intent it = getIntent();
		app = (ApplicationInfo) it.getParcelableExtra(PASS_APP);
		description = it.getStringExtra(PASS_DESCR);
		id = it.getIntExtra(PASS_ID, -1);
		is_topvote = it.getBooleanExtra(PASS_IS_TOPVOTE, false);
		votes = it.getIntExtra(PASS_VOTES, 0);
		version = it.getIntExtra(PASS_VERSION, 0);
		installed = it.getBooleanExtra(PASS_INSTALLED, false);
		user = it.getStringExtra(PASS_USER);
		user_trust = it.getBooleanExtra(PASS_USER_TRUST, false);
		timestamp = (Date) it.getSerializableExtra(PASS_TIMESTAMP);
		settings = Settings.Loader.importStringToSettingsString(it
				.getStringExtra(PASS_SETTINGS));

		lA = new SubmitCommentsAdapter(this, new ArrayList<String>());
		lA.app = app;
		lA.author = user;
		lA.author_trust = user_trust;
		lA.id = id;
		lA.is_topvote = is_topvote;
		lA.description = description;
		lA.installed = installed;
		lA.settings = settings;
		lA.timestamp = timestamp;
		lA.votes = votes;
		lA.addBegin();

		lv = (ListView) findViewById(R.id.listView1);
		lv.setAdapter(lA);

		lA.notifyDataSetChanged();

		lv.setOnItemClickListener(new ListView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				final int pos = position;
				AlertDialog.Builder builder = new AlertDialog.Builder(
						OneSubmitActivity.this);
				builder.setTitle(R.string.title_comment_options);
				int options_list_id;
				final Submitter.Account acc = Submitter
						.getSavedAccount(OneSubmitActivity.this);
				if (acc != null && !acc.isEmpty()
						&& acc.getUsername().equals(lA.users.get(pos)))
					options_list_id = R.array.comment_options;
				else
					options_list_id = R.array.comment_options_al;
				builder.setItems(options_list_id,
						new AlertDialog.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								switch (which) {
								case 0:
									ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
									ClipData clip = ClipData.newPlainText(
											"comment", lA.comments.get(pos));
									clipboard.setPrimaryClip(clip);
									break;
								case 1:
									CustomQ q = new CustomQ(
											Database.DATABASE_URL);
									q.addNameValuePair(Database.POST_PIN,
											Database.COMMUNITY_PIN);
									q.addNameValuePair(Database.POST_FUNCTION,
											Database.FUNCTION_DELETE_COMMENT);
									acc.addToQ(q);
									q.addNameValuePair(Database.POST_SUBMIT,
											String.valueOf(lA.ids.get(pos)));
									final AlertDialog progress = new AlertDialog.Builder(
											OneSubmitActivity.this)
											.setMessage(
													R.string.loadingsync_msg)
											.setView(
													new ProgressBar(
															OneSubmitActivity.this))
											.create();
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
												bundle.putInt(
														KEY_RESULT,
														Integer.parseInt(result));
											} catch (Throwable e) {
												e.printStackTrace();
											}
											return bundle;
										}
									});
									q.setPostExecuteListener(new CustomQ.PostExecuteListener() {
										@Override
										public void onPostExecute(
												Bundle processed) {
											progress.dismiss();
											if (processed.getInt(KEY_RESULT) != 1) {
												Toast.makeText(
														OneSubmitActivity.this,
														R.string.error_try_again,
														Toast.LENGTH_SHORT)
														.show();
											} else {
												lA.removeAt(pos);
												lA.notifyDataSetChanged();
											}
										}
									});
									q.exec();
									break;
								}
							}
						});
				builder.show();
			}
		});

		final String commentListKey = "commentlist";
		Q q = new Q(Database.DATABASE_URL);
		q.setPreExecuteListener(new Q.PreExecuteListener() {
			@Override
			public void onPreExecute() {
				lA.isLoading = true;
				lA.notifyDataSetChanged();
			}
		});
		q.setDataLoadedListener(new Q.DataLoadedListener() {
			@Override
			public Bundle onDataLoaded(JSONArray data) throws JSONException {
				ArrayList<Comment> comments = new ArrayList<Comment>();
				for (int i = 0; i < data.length(); i++) {
					JSONObject json_data = data.getJSONObject(i);
					DateFormat d_f = new SimpleDateFormat("yyyy-MM-dd-HH-mm");
					d_f.setTimeZone(TimeZone.getTimeZone("GMT"));
					Date timestamp;
					try {
						timestamp = d_f.parse(json_data.getString("timestamp"));
					} catch (ParseException e) {
						timestamp = new Date();
						e.printStackTrace();
					}
					comments.add(new Comment(Integer.valueOf(json_data
							.getString("id")), json_data.getString("comment"),
							json_data.getString("username"), timestamp,
							json_data.getString("user_trust").equals("1"),
							Integer.valueOf(json_data.getString("spamvotes"))));
				}
				Bundle bundle = new Bundle();
				bundle.putParcelableArrayList(commentListKey, comments);
				return bundle;
			}
		});
		q.setPostExecuteListener(new Q.PostExecuteListener() {
			@Override
			public void onPostExecute(Bundle processed) {
				List<Comment> list = processed
						.getParcelableArrayList(commentListKey);
				lA.addAll(list);
				lA.isLoading = false;
				lA.notifyDataSetChanged();
			}
		});
		q.addNameValuePair(Database.POST_PIN, Database.COMMUNITY_PIN);
		q.addNameValuePair(Database.POST_FUNCTION,
				Database.FUNCTION_GET_COMMENTS_FOR_SUBMIT);
		q.addNameValuePair(Database.POST_SUBMIT, String.valueOf(id));
		q.exec();

		edit_comment = (EditText) findViewById(R.id.editText1);
		tv_comment_charremain = (TextView) findViewById(R.id.textView1);
		btn_send = (ImageButton) findViewById(R.id.button_send);

		edit_comment.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				int length = s.length();
				tv_comment_charremain.setText(String
						.valueOf(Database.Constants.COMMENT_MAX_LENGTH - length));
				boolean isLengthOk = Database.Constants.COMMENT_MAX_LENGTH >= length;
				tv_comment_charremain.setTextColor(isLengthOk ? getResources()
						.getColor(color.darker_gray) : getResources().getColor(
						color.holo_red_light));
				btn_send.setEnabled(isLengthOk);
				btn_send.setImageAlpha(isLengthOk ? 0xFF : 0x60);
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});
		btn_send.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!edit_comment.getText().equals("")
						&& edit_comment.getText().length() <= Database.Constants.COMMENT_MAX_LENGTH) {
					CustomQ q = new CustomQ(Database.DATABASE_URL);
					q.addNameValuePair(Database.POST_PIN,
							Database.COMMUNITY_PIN);
					final Submitter.Account acc = Submitter
							.getSavedAccount(OneSubmitActivity.this);
					if (acc == null || acc.isEmpty()) {
						new Submitter.SignInDialog(OneSubmitActivity.this)
								.show();
						return;
					}
					acc.addToQ(q);
					q.addNameValuePair(Database.POST_FUNCTION,
							Database.FUNCTION_COMMENT);
					q.addNameValuePair(Database.POST_SUBMIT, String.valueOf(id));
					q.addNameValuePair(Database.POST_COMMENT_TEXT, edit_comment
							.getText().toString());

					final AlertDialog progress = new AlertDialog.Builder(
							OneSubmitActivity.this)
							.setMessage(R.string.loadingsync_msg)
							.setView(new ProgressBar(OneSubmitActivity.this))
							.create();
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
								bundle.putInt(KEY_RESULT,
										Integer.valueOf(result));
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
								Toast.makeText(OneSubmitActivity.this,
										R.string.error_try_again,
										Toast.LENGTH_SHORT).show();
							} else {
								lA.add(new Comment(
										processed.getInt(KEY_RESULT),
										edit_comment.getText().toString(), acc
												.getUsername(), new Date(),
										false, 0));
								lA.notifyDataSetChanged();
								edit_comment.setText("");
							}
							progress.dismiss();
						}
					});
					q.exec();
				}
			}
		});
		edit_comment.setText("");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		Submitter.Account acc = Submitter
				.getSavedAccount(OneSubmitActivity.this);
		if (acc != null && !acc.isEmpty()) {
			if (acc.getUsername().equals(user)) {
				MenuInflater inflater = getMenuInflater();
				inflater.inflate(R.menu.one_submit, menu);
			}
			return super.onCreateOptionsMenu(menu);
		} else {
			return false;
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		// Respond to the action bar's Up/Home button
		case android.R.id.home:
			finish();
			return true;
		case R.id.action_delete_submit:
			new AlertDialog.Builder(this)
					.setTitle(R.string.action_delete_submit)
					.setMessage(R.string.q_delete_submit)
					.setPositiveButton(android.R.string.yes,
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// delete entry
									Submitter.Account acc = Submitter
											.getSavedAccount(OneSubmitActivity.this);
									if (acc != null && !acc.isEmpty()) {
										CustomQ q = new CustomQ(
												Database.DATABASE_URL);
										q.addNameValuePair(Database.POST_PIN,
												Database.COMMUNITY_PIN);
										q.addNameValuePair(
												Database.POST_FUNCTION,
												Database.FUNCTION_DELETE_SUBMIT);
										acc.addToQ(q);
										q.addNameValuePair(
												Database.POST_SUBMIT,
												String.valueOf(id));
										final AlertDialog progress = new AlertDialog.Builder(
												OneSubmitActivity.this)
												.setMessage(
														R.string.loadingsync_msg)
												.setView(
														new ProgressBar(
																OneSubmitActivity.this))
												.create();
										q.setPreExecuteListener(new CustomQ.PreExecuteListener() {
											@Override
											public void onPreExecute() {
												progress.show();
											}
										});
										final String KEY_RESULT = "result";
										q.setHttpResultListener(new CustomQ.HttpResultListener() {
											@Override
											public Bundle onHttpResult(
													String result) {
												Bundle bundle = new Bundle();
												try {
													bundle.putInt(
															KEY_RESULT,
															Integer.parseInt(result));
												} catch (Throwable e) {
													e.printStackTrace();
												}
												return bundle;
											}
										});
										q.setPostExecuteListener(new CustomQ.PostExecuteListener() {
											@Override
											public void onPostExecute(
													Bundle processed) {
												progress.dismiss();
												if (processed
														.getInt(KEY_RESULT) != 1) {
													Toast.makeText(
															OneSubmitActivity.this,
															R.string.error_try_again,
															Toast.LENGTH_SHORT)
															.show();
												} else
													finish();
											}
										});
										q.exec();
									}
								}
							}).setNegativeButton(android.R.string.no, null)
					.show();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	protected static class Comment implements Parcelable {
		public int id;
		public String comment;
		public String user;
		public Date timestamp;
		public boolean user_trust;
		public int spamvotes;

		public Comment(int id, String comment, String username, Date timestamp,
				boolean user_trust, int spamvotes) {
			this.id = id;
			this.comment = comment;
			this.user = username;
			this.timestamp = timestamp;
			this.user_trust = user_trust;
			this.spamvotes = spamvotes;
		}

		public Comment(Parcel from) {
			this.id = from.readInt();
			this.comment = from.readString();
			this.user = from.readString();
			this.timestamp = (Date) from.readSerializable();
			this.user_trust = from.readByte() == (byte) 1;
			this.spamvotes = from.readInt();
		}

		@Override
		public int describeContents() {
			return 0;
		}

		@Override
		public void writeToParcel(Parcel dest, int flags) {
			dest.writeInt(id);
			dest.writeString(comment);
			dest.writeString(user);
			dest.writeSerializable(timestamp);
			dest.writeByte((user_trust ? (byte) 1 : (byte) 0));
			dest.writeInt(spamvotes);
		}

		public static final Parcelable.Creator<Comment> CREATOR = new Parcelable.Creator<Comment>() {
			public Comment createFromParcel(Parcel in) {
				return new Comment(in);
			}

			public Comment[] newArray(int size) {
				return new Comment[size];
			}
		};
	}
}
