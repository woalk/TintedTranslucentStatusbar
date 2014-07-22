package com.woalk.apps.xposed.ttsb.community;

import java.util.List;
import java.util.SortedMap;

import android.app.Activity;
import android.content.pm.ApplicationInfo;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

public class SubmitCommentsAdapter extends ArrayAdapter<String> {
	private Activity context;
	
	protected ApplicationInfo app;
	protected String author;
	protected boolean author_trust;
	protected boolean installed;
	protected SortedMap<String, String> settings;
	protected List<String> comments;
	protected List<String> users;
	protected List<Boolean> users_trust;

	public SubmitCommentsAdapter(Activity context, List<String> comments) {
		super(context, 0, comments);
		this.context = context;
		this.comments = comments;
	}


	public void addBegin() {
		
	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {
		return null;
	}
}
