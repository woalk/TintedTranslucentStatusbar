package com.woalk.apps.xposed.ttsb.community;

import com.woalk.apps.xposed.ttsb.Helpers;

public class Database {
	private Database() {
	}

	public static final String DATABASE_URL = "";

	public static final String COMMUNITY_PIN = "";

	public static final String POST_PIN = "";
	public static final String POST_FUNCTION = "";

	public static final String POST_SUBMITS_PACKAGE = "";
	public static final String POST_USERNAME = "";
	public static final String POST_SUBMIT = "";
	public static final String POST_VOTE_TYPE = "";
	public static final String POST_COMMENT_TEXT = "";

	public static final String POST_SUBMIT_SETTINGS = "";
	public static final String POST_SUBMIT_DESCRIPTION = "";
	public static final String POST_SUBMIT_VERSION = "";

	public static final String POST_ACC_USERNAME = "";
	public static final String POST_ACC_PASSWORD = "";

	public static final String POST_ACC_NEW_USERNAME = "";
	public static final String POST_ACC_NEW_PASSWORD = "";

	public static final String POST_ACC_DELETION_SURE = "";
	public static final String DELETION_SURE_PIN = "";

	public static final String FUNCTION_GET_PACKAGES = "";
	public static final String FUNCTION_GET_SUBMITS_FOR_PACKAGE = "";
	public static final String FUNCTION_GET_ONE_SUBMIT = "";
	public static final String FUNCTION_GET_COMMENTS_FOR_SUBMIT = "";
	public static final String FUNCTION_GET_SUBMITS_BY_USER = "";
	public static final String FUNCTION_GET_USER_VOTES = "";
	public static final String FUNCTION_GET_NOTIFICATIONS = "";
	public static final String FUNCTION_SIGN_IN = "";
	public static final String FUNCTION_SIGN_UP = "";
	public static final String FUNCTION_NEW_SIGN_UP = "";
	public static final String FUNCTION_SUBMIT = "";
	public static final String FUNCTION_DELETE_SUBMIT = "";
	public static final String FUNCTION_CHANGE_USERNAME = "";
	public static final String FUNCTION_CHANGE_PASSWORD = "";
	public static final String FUNCTION_DELETE_PROFILE = "";
	public static final String FUNCTION_VOTE = "";
	public static final String FUNCTION_COMMENT = "";
	public static final String FUNCTION_DELETE_COMMENT = "";

	public static class Constants {
		private Constants() {
		}

		public static final int COMMENT_MAX_LENGTH = 150;
	}

	public static class Preferences {
		private Preferences() {
		}

		public static final String COMMUNITY_PREF_NAME = Helpers.TTSB_PACKAGE_NAME
				+ ".COMMUNITYPREF";

		public static final String PREF_PREFIX_USED_SUBMIT_ID = "SUBMIT_ID//";
		public static final String PREF_PREFIX_IS_TOPVOTED_USED = "TOPVOTED//";

		public static final String PREF_USERNAME = Helpers.TTSB_PACKAGE_NAME
				+ ".USERNAME";
		public static final String PREF_PASSWORD = Helpers.TTSB_PACKAGE_NAME
				+ ".PASSWORD";
		public static final String PREF_ACCOUNT_DISMISSED = Helpers.TTSB_PACKAGE_NAME
				+ ".ACCOUNT_DISMISSED";
	}
}