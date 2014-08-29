package com.woalk.apps.xposed.ttsb.community;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.view.View;

import com.woalk.apps.xposed.ttsb.R;
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
			builder.setNegativeButton(android.R.string.ok, null);
			builder.setNeutralButton(R.string.signup,
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO: Signup
						}
					});
			final Dialog d = builder.create();
			d.setOnShowListener(new DialogInterface.OnShowListener() {
				@Override
				public void onShow(DialogInterface dialog) {
					View pos_btn = d
							.findViewById(DialogInterface.BUTTON_POSITIVE);
					pos_btn.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {

						}
					});
				}
			});
		}

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

		public void setUsername(String username) {
			this.username = username;
		}

		public String getPassword() {
			return password;
		}

		public void setPassword(String password) {
			this.password = password;
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
			return username != null && !username.equals("") && password != null
					&& !password.equals("");
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
}
