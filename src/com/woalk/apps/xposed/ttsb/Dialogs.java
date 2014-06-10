package com.woalk.apps.xposed.ttsb;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

public class Dialogs {
	public static class SelectPkgDialog extends DialogFragment {
		private String[] pkgNames;
		private DialogInterface.OnClickListener ocl;
		
		public void setPkgNames(String[] pkgs) {
			pkgNames = pkgs;
		}
		
		public void setOnClick(DialogInterface.OnClickListener listener) {
			ocl = listener;
		}
		
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setTitle(getString(R.string.select_app_from_list));
			builder.setItems(pkgNames, ocl);
			return builder.create();
		}
	}

	public static class SelectActivityDialog extends DialogFragment {
		private String pkgName;
		private String[] activityNames;
		private DialogInterface.OnClickListener ocl;
		
		public void setPkgName(String pkg) {
			pkgName = pkg;
		}
		
		public void setActivityNames(String[] actvts) {
			activityNames = actvts;
		}

		public void setOnClick(DialogInterface.OnClickListener listener) {
			ocl = listener;
		}
		
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setTitle(getString(R.string.select_activity_from_list) + " " + pkgName);
			builder.setItems(activityNames, ocl);
			return builder.create();
		}
	}
}

