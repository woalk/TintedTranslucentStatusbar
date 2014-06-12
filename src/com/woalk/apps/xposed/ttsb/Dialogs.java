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
		
		public void setPkgNames(String[] pkgs, String[] appNamesAlphabetical) {
			if (pkgs.length == appNamesAlphabetical.length) {
				String[] appNames = new String[pkgs.length]; 
				for (int i = 0; i < pkgs.length; i++) {
					String appName = appNamesAlphabetical[i];
					int index_startName = appName.lastIndexOf("::");
					int i_pkgs = Integer.valueOf(appName.substring(index_startName + 2));
					appNames[i] = appName.substring(0, index_startName) + "\n (" + pkgs[i_pkgs] + ")";
				}
				pkgNames = appNames;
			} else {
				throw new IllegalArgumentException("Arrays must have the same length.");
			}
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
			activityNames = new String[actvts.length];
			for (int i = 0; i < actvts.length; i++) {
				activityNames[i] = actvts[i].substring(0, actvts[i].lastIndexOf("::"));
			}
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

