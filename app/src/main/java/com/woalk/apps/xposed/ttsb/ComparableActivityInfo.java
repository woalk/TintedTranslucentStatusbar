package com.woalk.apps.xposed.ttsb;

import android.content.pm.ActivityInfo;

public class ComparableActivityInfo extends ActivityInfo implements
		Comparable<ActivityInfo> {
	public boolean is_set;

	public ComparableActivityInfo(ActivityInfo activityInfo) {
		this.applicationInfo = activityInfo.applicationInfo;
		this.configChanges = activityInfo.configChanges;
		this.descriptionRes = activityInfo.descriptionRes;
		this.enabled = activityInfo.enabled;
		this.exported = activityInfo.exported;
		this.flags = activityInfo.flags;
		this.icon = activityInfo.icon;
		this.labelRes = activityInfo.labelRes;
		this.launchMode = activityInfo.launchMode;
		this.logo = activityInfo.logo;
		this.metaData = activityInfo.metaData;
		this.name = activityInfo.name;
		this.nonLocalizedLabel = activityInfo.nonLocalizedLabel;
		this.packageName = activityInfo.packageName;
		this.parentActivityName = activityInfo.parentActivityName;
		this.permission = activityInfo.permission;
		this.processName = activityInfo.processName;
		this.screenOrientation = activityInfo.screenOrientation;
		this.softInputMode = activityInfo.softInputMode;
		this.targetActivity = activityInfo.targetActivity;
		this.taskAffinity = activityInfo.taskAffinity;
		this.theme = activityInfo.theme;
		this.uiOptions = activityInfo.uiOptions;
	}

	@Override
	public int compareTo(ActivityInfo another) {
		return this.name.compareTo(another.name);
	}

}
