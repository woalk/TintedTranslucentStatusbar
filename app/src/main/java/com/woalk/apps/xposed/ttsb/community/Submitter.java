package com.woalk.apps.xposed.ttsb.community;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.woalk.apps.xposed.ttsb.R;
import com.woalk.apps.xposed.ttsb.community.SQL_Operations.CustomQ;
import com.woalk.apps.xposed.ttsb.community.SQL_Operations.Q;

public class Submitter {
	private Submitter() {
	}

	public static class SignInDialog {
		private Activity activity;

		public interface SignedInListener {
			public abstract void onSignedIn();
		}

		public SignInDialog(Activity context) {
			setActivity(context);
		}

		public Activity getActivity() {
			return activity;
		}

		public void setActivity(Activity activity) {
			this.activity = activity;
		}

		@SuppressLint("InflateParams")
		public void show() {
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setTitle(R.string.signin);
			builder.setView(getActivity().getLayoutInflater().inflate(
					R.layout.signin, null));
			builder.setPositiveButton(android.R.string.ok, null);
			builder.setNegativeButton(android.R.string.cancel,
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							setAccountDialogDismissed(getActivity(), true);
						}
					});
			builder.setNeutralButton(R.string.signup,
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							new SignUpDialog(getActivity()).show();
						}
					});
			final AlertDialog d = builder.create();
			d.setOnShowListener(new DialogInterface.OnShowListener() {
				@Override
				public void onShow(DialogInterface dialog) {
					final View pos_btn = d
							.getButton(AlertDialog.BUTTON_POSITIVE);
					final String KEY_SIGN_IN_SUCCESS = "signinsucc";
					pos_btn.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							final String sel_username = ((EditText) d
									.findViewById(R.id.editText1)).getText()
									.toString();
							final String sel_password = ((EditText) d
									.findViewById(R.id.editText2)).getText()
									.toString();
							CustomQ q = new CustomQ(Database.DATABASE_URL);
							q.addNameValuePair(Database.POST_PIN,
									Database.COMMUNITY_PIN);
							q.addNameValuePair(Database.POST_FUNCTION,
									Database.FUNCTION_SIGN_IN);
							q.addNameValuePair(Database.POST_ACC_USERNAME,
									sel_username);
							q.addNameValuePair(Database.POST_ACC_PASSWORD,
									sel_password);
							q.setPreExecuteListener(new CustomQ.PreExecuteListener() {
								@Override
								public void onPreExecute() {
									pos_btn.setEnabled(false);
								}
							});
							q.setHttpResultListener(new CustomQ.HttpResultListener() {
								@Override
								public Bundle onHttpResult(String result) {
									Bundle bundle = new Bundle();
									if (result.equals("0")) {
										bundle.putBoolean(KEY_SIGN_IN_SUCCESS,
												false);
									} else if (result.equals("1")) {
										bundle.putBoolean(KEY_SIGN_IN_SUCCESS,
												true);
									}
									return bundle;
								}
							});
							q.setPostExecuteListener(new CustomQ.PostExecuteListener() {
								@Override
								public void onPostExecute(Bundle processed) {
									if (processed
											.getBoolean(KEY_SIGN_IN_SUCCESS)) {
										saveAccount(getActivity(), new Account(
												sel_username, sel_password));
										d.dismiss();
									} else {
										d.findViewById(R.id.tV_err)
												.setVisibility(View.VISIBLE);
									}
									pos_btn.setEnabled(true);
								}
							});
							q.exec();
						}
					});
				}
			});
			d.show();
		}
	}

	public static class SignUpDialog {
		private Activity activity;

		public interface SignedUpListener {
			public abstract void onSignedIn();
		}

		public SignUpDialog(Activity context) {
			setActivity(context);
		}

		public Activity getActivity() {
			return activity;
		}

		public void setActivity(Activity activity) {
			this.activity = activity;
		}

		@SuppressLint("InflateParams")
		public void show() {
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setTitle(R.string.signup);
			View v = getActivity().getLayoutInflater().inflate(R.layout.signup,
					null);
			View tv_terms = v.findViewById(R.id.tV_terms);
			tv_terms.setVisibility(View.VISIBLE);
			tv_terms.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					new AlertDialog.Builder(getActivity())
							.setTitle(R.string.link_terms_of_use)
							.setMessage(R.string.community_terms_of_use)
							.setPositiveButton(android.R.string.ok, null)
							.show();
				}
			});
			builder.setView(v);
			builder.setPositiveButton(android.R.string.ok, null);
			builder.setNegativeButton(android.R.string.cancel, null);
			final AlertDialog d = builder.create();
			d.setOnShowListener(new DialogInterface.OnShowListener() {
				@Override
				public void onShow(DialogInterface dialog) {
					final View pos_btn = d
							.getButton(AlertDialog.BUTTON_POSITIVE);
					final String KEY_SIGN_UP_SUCCESS = "signupsucc";
					pos_btn.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							String sel_username = ((EditText) d
									.findViewById(R.id.editText1)).getText()
									.toString();
							final Account acc = new Account(sel_username, "");
							CustomQ q = new CustomQ(Database.DATABASE_URL);
							q.addNameValuePair(Database.POST_PIN,
									Database.COMMUNITY_PIN);
							q.addNameValuePair(Database.POST_FUNCTION,
									Database.FUNCTION_NEW_SIGN_UP);
							acc.addToQ(q);
							q.setPreExecuteListener(new CustomQ.PreExecuteListener() {
								@Override
								public void onPreExecute() {
									d.findViewById(R.id.tV_err_already_given)
											.setVisibility(View.GONE);
									pos_btn.setEnabled(false);
								}
							});
							q.setHttpResultListener(new CustomQ.HttpResultListener() {
								@Override
								public Bundle onHttpResult(String result) {
									Bundle bundle = new Bundle();
									if (result.equals("0")) {
										bundle.putBoolean(KEY_SIGN_UP_SUCCESS,
												true);
									} else {
										bundle.putString(KEY_SIGN_UP_SUCCESS,
												result);
									}
									return bundle;
								}
							});
							q.setPostExecuteListener(new CustomQ.PostExecuteListener() {
								@Override
								public void onPostExecute(Bundle processed) {
									if (processed
											.getBoolean(KEY_SIGN_UP_SUCCESS)) {
										d.findViewById(
												R.id.tV_err_already_given)
												.setVisibility(View.VISIBLE);
									} else {
										acc.setPassword(processed
												.getString(KEY_SIGN_UP_SUCCESS));
										TextView v = new TextView(getActivity());
										v.setTextSize(
												TypedValue.COMPLEX_UNIT_SP, 32);
										v.setTypeface(Typeface.MONOSPACE);
										v.setText("   " + acc.getPassword());
										new AlertDialog.Builder(getActivity())
												.setCancelable(false)
												.setTitle(R.string.signup)
												.setMessage(
														R.string.signuo_your_pw)
												.setView(v)
												.setPositiveButton(
														android.R.string.ok,
														new DialogInterface.OnClickListener() {

															@Override
															public void onClick(
																	DialogInterface dialog,
																	int which) {
																d.dismiss();
																saveAccount(
																		getActivity(),
																		acc);
															}
														}).show();
									}
									pos_btn.setEnabled(true);
								}
							});
							q.exec();
						}
					});
				}
			});
			d.show();
		}
	}

	public static class ChangeNamePwDialog {
		private Activity activity;

		public interface SignedUpListener {
			public abstract void onSignedIn();
		}

		public ChangeNamePwDialog(Activity context) {
			setActivity(context);
		}

		public Activity getActivity() {
			return activity;
		}

		public void setActivity(Activity activity) {
			this.activity = activity;
		}

		@SuppressLint("InflateParams")
		public void show() {
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setTitle(R.string.str_action_edit_name);
			builder.setView(getActivity().getLayoutInflater().inflate(
					R.layout.signup, null));
			builder.setPositiveButton(android.R.string.ok, null);
			builder.setNegativeButton(android.R.string.cancel, null);
			final AlertDialog d = builder.create();
			d.setOnShowListener(new DialogInterface.OnShowListener() {
				@Override
				public void onShow(DialogInterface dialog) {
					final View pos_btn = d
							.getButton(AlertDialog.BUTTON_POSITIVE);
					final String KEY_SIGN_UP_SUCCESS = "signupsucc";
					pos_btn.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							String sel_username = ((EditText) d
									.findViewById(R.id.editText1)).getText()
									.toString();
							CustomQ q = new CustomQ(Database.DATABASE_URL);
							q.addNameValuePair(Database.POST_PIN,
									Database.COMMUNITY_PIN);
							q.addNameValuePair(Database.POST_FUNCTION,
									Database.FUNCTION_CHANGE_USERNAME);
							Account s_acc = getSavedAccount(getActivity());
							s_acc.addToQ(q);
							final Account acc = new Account(sel_username, s_acc
									.getPassword());
							q.addNameValuePair(Database.POST_ACC_NEW_USERNAME,
									acc.getUsername());
							q.setPreExecuteListener(new CustomQ.PreExecuteListener() {
								@Override
								public void onPreExecute() {
									d.findViewById(R.id.tV_err_already_given)
											.setVisibility(View.GONE);
									pos_btn.setEnabled(false);
								}
							});
							q.setHttpResultListener(new CustomQ.HttpResultListener() {
								@Override
								public Bundle onHttpResult(String result) {
									Bundle bundle = new Bundle();
									if (result.equals("0")) {
										bundle.putBoolean(KEY_SIGN_UP_SUCCESS,
												false);
									} else if (result.equals("1")) {
										bundle.putBoolean(KEY_SIGN_UP_SUCCESS,
												true);
									}
									return bundle;
								}
							});
							q.setPostExecuteListener(new CustomQ.PostExecuteListener() {
								@Override
								public void onPostExecute(Bundle processed) {
									if (processed
											.getBoolean(KEY_SIGN_UP_SUCCESS)) {
										saveAccount(getActivity(), acc);
										d.dismiss();
										getActivity().finish();
									} else {
										d.findViewById(
												R.id.tV_err_already_given)
												.setVisibility(View.VISIBLE);
									}
									pos_btn.setEnabled(true);
								}
							});
							q.exec();
						}
					});
				}
			});
			d.show();
		}
	}

	public static void generateNewPassword(final Activity context) {
		CustomQ q = new CustomQ(Database.DATABASE_URL);
		q.addNameValuePair(Database.POST_PIN, Database.COMMUNITY_PIN);
		q.addNameValuePair(Database.POST_FUNCTION,
				Database.FUNCTION_CHANGE_PASSWORD);
		final Account acc = getSavedAccount(context);
		acc.addToQ(q);
		final String KEY_SIGN_UP_SUCCESS = "sigsucc";
		q.setHttpResultListener(new CustomQ.HttpResultListener() {
			@Override
			public Bundle onHttpResult(String result) {
				Bundle bundle = new Bundle();
				if (result.equals("0")) {
					bundle.putBoolean(KEY_SIGN_UP_SUCCESS, true);
				} else {
					bundle.putString(KEY_SIGN_UP_SUCCESS, result);
				}
				return bundle;
			}
		});
		q.setPostExecuteListener(new CustomQ.PostExecuteListener() {
			@Override
			public void onPostExecute(Bundle processed) {
				if (processed.getBoolean(KEY_SIGN_UP_SUCCESS)) {
					Toast.makeText(context, R.string.error_try_again,
							Toast.LENGTH_SHORT).show();
				} else {
					acc.setPassword(processed.getString(KEY_SIGN_UP_SUCCESS));
					TextView v = new TextView(context);
					v.setTextSize(TypedValue.COMPLEX_UNIT_SP, 32);
					v.setTypeface(Typeface.MONOSPACE);
					v.setText("   " + acc.getPassword());
					new AlertDialog.Builder(context)
							.setCancelable(false)
							.setTitle(R.string.signup)
							.setMessage(R.string.signuo_your_pw)
							.setView(v)
							.setPositiveButton(android.R.string.ok,
									new DialogInterface.OnClickListener() {

										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											saveAccount(context, acc);
										}
									}).show();
				}
			}
		});
		q.exec();
	}

	/**
	 * Simple combination class of a username and password.
	 * 
	 * @author woalk
	 */
	public static class Account {
		private String username;
		private String password;

		public Account() {
		}

		public Account(String username, String password) {
			setUsername(username);
			setPassword(password);
		}

		public String getUsername() {
			return username;
		}

		@SuppressLint("DefaultLocale")
		public void setUsername(String username) {
			this.username = username.toLowerCase();
		}

		public String getPassword() {
			return password;
		}

		public void setPassword(String password) {
			this.password = password;
		}

		/**
		 * Adds this account (combination of username and password) as
		 * {@code NameValuePairs} to a {@link CustomQ} instance.
		 * 
		 * @param q
		 *            The {@link CustomQ} instance to add the values to.
		 */
		public void addToQ(CustomQ q) {
			q.addNameValuePair(Database.POST_ACC_USERNAME, getUsername());
			q.addNameValuePair(Database.POST_ACC_PASSWORD, getPassword());
		}

		/**
		 * Adds this account (combination of username and password) as
		 * {@code NameValuePairs} to a {@link Q} instance.
		 * 
		 * @param q
		 *            The {@link Q} instance to add the values to.
		 */
		public void addToQ(Q q) {
			q.addNameValuePair(Database.POST_ACC_USERNAME, getUsername());
			q.addNameValuePair(Database.POST_ACC_PASSWORD, getPassword());
		}

		/**
		 * Checks if this account instance has the neccessary values set
		 * (username and password).
		 * 
		 * @return {@code true}, if there are these values in this account
		 *         object.
		 */
		public boolean isEmpty() {
			return username == null || username.equals("") || password == null
					|| password.equals("");
		}
	}

	/**
	 * Read the saved account from {@link SharedPreferences}.
	 * 
	 * @param context
	 *            The context Activity. Used to get the preferences.
	 * @return The saved {@link Account}, or {@code null}, if there is no or an
	 *         invalid saved account.
	 */
	public static Account getSavedAccount(Activity context) {
		SharedPreferences sPref = context.getSharedPreferences(
				Database.Preferences.COMMUNITY_PREF_NAME, Context.MODE_PRIVATE);
		String uname = sPref.getString(Database.Preferences.PREF_USERNAME, "");
		String pw = sPref.getString(Database.Preferences.PREF_PASSWORD, "");
		if (uname == "" || pw == "") {
			// invalid / no account registered
			return null;
		} else {
			return new Account(uname, pw);
		}
	}

	public static boolean isAccountDialogDismissed(Activity context) {
		SharedPreferences sPref = context.getSharedPreferences(
				Database.Preferences.COMMUNITY_PREF_NAME, Context.MODE_PRIVATE);
		return sPref.getBoolean(Database.Preferences.PREF_ACCOUNT_DISMISSED,
				false);
	}

	/**
	 * Save an account to {@link SharedPreferences}, e.g. when signed in.
	 * 
	 * @param context
	 *            The context Activity. Used to get the preferences.
	 * @param acc
	 *            The {@link Account} to save.
	 */
	public static void saveAccount(Activity context, Account acc) {
		SharedPreferences sPref = context.getSharedPreferences(
				Database.Preferences.COMMUNITY_PREF_NAME, Context.MODE_PRIVATE);
		if (acc != null && !acc.isEmpty()) {
			SharedPreferences.Editor edit = sPref.edit();
			edit.putString(Database.Preferences.PREF_USERNAME,
					acc.getUsername());
			edit.putString(Database.Preferences.PREF_PASSWORD,
					acc.getPassword());
			edit.apply();
		}
	}

	/**
	 * Delete the account saved in the {@link SharedPreferences} (log out).
	 * 
	 * @param context
	 *            The context Activity. Used to get the preferences.
	 */
	public static void deleteSavedAccount(Activity context) {
		SharedPreferences sPref = context.getSharedPreferences(
				Database.Preferences.COMMUNITY_PREF_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor edit = sPref.edit();
		edit.remove(Database.Preferences.PREF_USERNAME);
		edit.remove(Database.Preferences.PREF_PASSWORD);
		edit.apply();
	}

	public static void setAccountDialogDismissed(Activity context, boolean val) {
		SharedPreferences sPref = context.getSharedPreferences(
				Database.Preferences.COMMUNITY_PREF_NAME, Context.MODE_PRIVATE);
		sPref.edit()
				.putBoolean(Database.Preferences.PREF_ACCOUNT_DISMISSED, val)
				.apply();
	}
}
