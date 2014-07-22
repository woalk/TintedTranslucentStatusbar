package com.woalk.apps.xposed.ttsb;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;

import com.woalk.apps.xposed.ttsb.community.Database;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Color;
import android.util.Log;


public class Settings {
	private Settings() { } // not extendable class
	
	public static class Parser {
		private String text;
		private Setting setting;
		
		private String err = "TTSB Settings Parser log:";
		
		public Parser(String line) {
			text = line;
		}
		public Parser(Setting set) {
			setting = set;
		}
		
		public String getLine() {
			return text;
		}
		
		public void setLine(String line) {
			text = line;
		}
		
		public Setting getSetting() {
			return setting;
		}
		
		public void setSetting(Setting set) {
			setting = set;
		}
		
		/**
		 * Parse this Parser instance from String to all settings.
		 * Settings then can be found in {@code getSetting();}.
		 * @return 'true' if ok, 'false' if there are syntax errors in the line.
		 */
		public boolean parseToSettings() {
			boolean result = true;
			String[] str_set = text.split(";");
			Setting setting = new Setting();
			if (!text.contains(";view:") || text.startsWith("view:")) setting.rules.view = null;
			for (int i = 0; i < str_set.length; i++) {
				if (!str_set[i].contains(":")) {
					result = false;
					err += "\n> Illegal argument: No command specified.";
					continue;
				}
				String cmd = str_set[i].substring(0, str_set[i].indexOf(":"));
				switch (cmd) {
				case "status":
					if (str_set[i].charAt(cmd.length() + 1) == '1') {
						setting.status = true;
					} else {
						setting.status = false;
					}
					break;
				case "nav":
					if (str_set[i].charAt(cmd.length() + 1) == '1') {
						setting.nav = true;
					} else {
						setting.nav = false;
					}
					break;
				case "s_color":
					setting.s_color = Helpers.getColor(str_set[i].substring(cmd.length() + 1));
					break;
				case "n_color":
					setting.n_color = Helpers.getColor(str_set[i].substring(cmd.length() + 1));
					break;
				case "s_plus":
					setting.rules.s_plus = Integer.valueOf(str_set[i].substring(cmd.length() + 1));
					break;
				case "n_plus":
					setting.rules.n_plus = Integer.valueOf(str_set[i].substring(cmd.length() + 1));
					break;
				case "cview":
					setting.rules.cview = parseViewSettings(str_set[i].substring(cmd.length() + 1));
					break;
				case "content":
					setting.rules.content = parseViewSettings(str_set[i].substring(cmd.length() + 1));
					break;
				case "decview":
					setting.rules.decview = parseViewSettings(str_set[i].substring(cmd.length() + 1));
					break;
				case "view":
					setting.rules.view.add(parseViewSettingsPack(str_set[i].substring(cmd.length() + 1)));
					break;
				case "":
					break;
				default:
					result = false;
					err += "\n> Unknown command at setting " + String.valueOf(i) + ".";
				}
			}
			this.setting = setting;
			//Log.i("TTSB", getErrorMessage());
			return result;
		}
		
		public Setting.ViewSettings parseViewSettings(String str) {
			String line = str;
			Setting.ViewSettings vset = new Setting.ViewSettings();
			if (str.startsWith("land(") && str.endsWith(")")) {
				line = str.substring("land(".length(), str.length() - ")".length());
				vset.if_land = true;
			} else if (str.contains("land(")) {
				int i = str.indexOf("land(");
				vset.land = parseViewSettings(str.substring(i, str.indexOf(")", i) + 1));
			} else {
				vset.if_land = false;
			}
			String[] parts = line.split("&");
			for (int i = 0; i < parts.length; i++) {
				if (parts[i] == "" || !parts[i].contains("=")) continue;
				String cmd = parts[i].substring(0, parts[i].indexOf("="));
				switch (cmd) {
				case "setFSW":
					vset.setFSW = true;
					if (parts[i].charAt(cmd.length() + 1) == '1') {
						vset.setFSW_value = true;
					} else {
						vset.setFSW_value = false;
					}
					break;
				case "setCTP":
					vset.setCTP = true;
					if (parts[i].charAt(cmd.length() + 1) == '1') {
						vset.setCTP_value = true;
					} else {
						vset.setCTP_value = false;
					}
					break;
				case "padding":
					vset.padding = new Setting.ViewSettings.IntOptPadding();
					String[] paddings = parts[i].substring(parts[i].indexOf("=") + 1).split("@");
					vset.padding.left = Integer.parseInt(paddings[0]);
					vset.padding.plus_status_h = paddings[1].contains("+status_h");
					vset.padding.plus_actionbar_h = paddings[1].contains("+actionbar_h");
					vset.padding.top = Integer.parseInt(paddings[1].replace("+status_h", "").replace("+actionbar_h", ""));
					vset.padding.plus_nav_w = paddings[2].contains("+nav_w");
					vset.padding.right = Integer.parseInt(paddings[2].replace("+nav_w", ""));
					vset.padding.plus_nav_h = paddings[3].contains("+nav_h");
					vset.padding.bottom = Integer.parseInt(paddings[3].replace("+nav_h", ""));
					break;
				case "":
					break;
				default:
					err += "\n> Unknown command in ViewSettings.";
				}
			}
			return vset;
		}
		
		private Setting.ViewSettingsPack parseViewSettingsPack(String str) {
			Setting.ViewSettingsPack setpk = new Setting.ViewSettingsPack();
			String[] parts = str.split(",");
			for (int i = 0; i < parts.length; i++) {
				String cmd = parts[i].substring(0, parts[i].length() - parts[i].substring(parts[i].indexOf(">")).length());
				switch (cmd) {
				case "from":
					switch (parts[i].substring(cmd.length() + 1)) {
					case "cview":
						setpk.from = Setting.ViewSettingsPack.FROM_CVIEW;
						break;
					case "content":
						setpk.from = Setting.ViewSettingsPack.FROM_CONTENT;
						break;
					case "decview":
						setpk.from = Setting.ViewSettingsPack.FROM_DECVIEW;
						break;
					default:
						err += "\n> Illegal 'from' argument.";
					}
					break;
				case "levels":
					setpk.levels = Integer.valueOf(parts[i].substring(cmd.length() + 1));
					break;
				case "childindexes":
					String cnt = parts[i].substring(cmd.length() + 1);
					if (!cnt.contains("&")) {
						setpk.childindexes = new int[]{Integer.valueOf(cnt)};
					} else {
						String[] i_str = cnt.split("&");
						int[] i_int = new int[i_str.length];
						for (int j = 0; j < i_str.length; j++) {
							i_int[j] = Integer.valueOf(i_str[j]);
						}
						setpk.childindexes = i_int;
					}
					break;
				case "settings":
					setpk.settings = parseViewSettings(parts[i].substring(cmd.length() + 1));
					break;
				case "":
					break;
				default:
					err += "\n> Unknown argument in 'view'.";
				}
			}
			return setpk;
		}
		
		/**
		 * Parse this Parser instance's settings to a settings string.
		 * String then can be found with {@code getLine();}.
		 */
		public void parseToString() {
			String line = "";
			if (setting.status) {
				line += "status:1;";
				line += "s_color:" + Helpers.getColorHexString(setting.s_color) + ";";
				line += "s_plus:" + String.valueOf(setting.rules.s_plus) + ";";
			}
			if (setting.nav) {
				line += "nav:1;";
				line += "n_color:" + Helpers.getColorHexString(setting.n_color) + ";";
				line += "n_plus:" + String.valueOf(setting.rules.n_plus) + ";";
			}
			if (setting.rules.cview != null) {
				line += "cview:" + parseViewSettingsToString(setting.rules.cview) + ";";
			}
			if (setting.rules.content != null) {
				line += "content:" + parseViewSettingsToString(setting.rules.content) + ";";
			}
			if (setting.rules.decview != null) {
				line += "decview:" + parseViewSettingsToString(setting.rules.decview) + ";";
			}
			if (setting.rules.view != null && setting.rules.view.size() > 0) {
				for (int i = 0; i < setting.rules.view.size(); i++) {
					line += "view:"
							+ parseViewSettingsPackToString(setting.rules.view.get(i))
							+ ";";
				}
			}
			setLine(line);
		}
		
		private static String parseViewSettingsToString(Setting.ViewSettings vset) {
			String line = "";
			if (vset.if_land) line += "land(";
			if (vset.setFSW) {
				line += "setFSW=";
				if (vset.setFSW_value) line += "1"; else line += "0";
				line += "&";
			}
			if (vset.setCTP) {
				line += "setCTP=";
				if (vset.setCTP_value) line += "1"; else line += "0";
				line += "&";
			}
			if (vset.padding != null) {
				line += "padding=";
				line += String.valueOf(vset.padding.left);
				line += "@";
				line += String.valueOf(vset.padding.top);
				if (vset.padding.plus_status_h)
					line += "+status_h";
				if (vset.padding.plus_actionbar_h)
					line += "+actionbar_h";
				line += "@";
				line += String.valueOf(vset.padding.right);
				if (vset.padding.plus_nav_w) 
					line += "+nav_w";
				line += "@";
				line += String.valueOf(vset.padding.bottom);
				if (vset.padding.plus_nav_h)
					line += "+nav_h";
			}
			if (vset.if_land) line += ")";
			if (vset.land != null) line += "&" + parseViewSettingsToString(vset.land);
			return line;
		}
		
		public static String parseViewSettingsPackToString(Setting.ViewSettingsPack vsetpk) {
			String line = "";
			if (0 <= vsetpk.from && vsetpk.from <= 2) {
				line += "from>";
				switch (vsetpk.from) {
				case Setting.ViewSettingsPack.FROM_DECVIEW:
					line += "decview";
					break;
				case Setting.ViewSettingsPack.FROM_CVIEW:
					line += "cview";
					break;
				case Setting.ViewSettingsPack.FROM_CONTENT:
					line += "content";
					break;
				}
				line += ",";
			}
			line += "levels>" + String.valueOf(vsetpk.levels) + ",";
			if (vsetpk.childindexes != null && vsetpk.childindexes.length == 1)
				line += "childindexes>" + String.valueOf(vsetpk.childindexes[0]) + ",";
			else if (vsetpk.childindexes.length > 1) {
				line += "childindexes>";
				for (int i = 0; i < vsetpk.childindexes.length; i++) {
					line += String.valueOf(vsetpk.childindexes[i]) + "&";
				}
				line += ",";
			}
			if (vsetpk.settings != null)
				line += "settings>"
						+ parseViewSettingsToString(vsetpk.settings);
			return line;
		}
		
		public String getErrorMessage() {
			return err;
		}
	}
	
	public static class Setting {
		public boolean status = false;
		public boolean nav = false;
		public int s_color = Color.TRANSPARENT;
		public int n_color = Color.TRANSPARENT;
		public Rules rules;
		
		public Setting() {
			rules = new Rules();
			rules.view = new ArrayList<ViewSettingsPack>();
		}
		
		public static class ViewSettingsPack {
			/**
			 * One of {@code FROM_CVIEW}, {@code FROM_CONTENT}, {@code FROM_DEC_VIEW}.
			 */
			public int from;
			/*\->*/ public final static int FROM_DECVIEW = 0;
			/*\->*/ public final static int FROM_CVIEW = 1;
			/*\->*/ public final static int FROM_CONTENT = 2;
			public int levels;
			public int[] childindexes;
			
			public String getChildIndexesString() {
				if (childindexes == null || childindexes.length == 0) return "0";
				String chi = "";
				for (int i = 0; i < childindexes.length; i++) {
					chi += childindexes[i];
					if (i < childindexes.length - 1) chi += ",";
				}
				return chi;
			}
			
			public void setChildIndexesFromString(String chi_string) {
				String[] chi = chi_string.split(",");
				int[] int_chi = new int[chi.length];
				for (int i = 0; i < chi.length; i++) {
					if (!chi[i].equals("")) int_chi[i] = Integer.parseInt(chi[i]);
				}
				this.childindexes = int_chi;
			}
			
			public ViewSettings settings;
			
			public ViewSettingsPack() {
				settings = new ViewSettings();
			}
		}
		
		public static class ViewSettings {
			public boolean if_land;
			
			public ViewSettings land;
			
			public boolean setFSW = false;
			public boolean setCTP = false;
			public boolean setFSW_value;
			public boolean setCTP_value;
			public IntOptPadding padding;
			
			public static class IntOptPadding {
				public int left;
				public int top;
				public int right;
				public int bottom;
				
				public boolean plus_status_h;
				public boolean plus_actionbar_h;
				public boolean plus_nav_h;
				public boolean plus_nav_w;
			}
		}
		
		public static class Rules {
			public int s_plus;
			public int n_plus;
			
			public ViewSettings cview;
			public ViewSettings content;
			public ViewSettings decview;
			public List<ViewSettingsPack> view;
		}
	}
	
	@SuppressLint("WorldReadableFiles")
	@SuppressWarnings("deprecation")
	public static class Loader {
		private Loader() { }
		
		public static Parser load(Context context, String key) {
			SharedPreferences sPref = context.getSharedPreferences(Helpers.TTSB_PREFERENCES, Context.MODE_WORLD_READABLE);
			return load(sPref, key);
		}
		public static Parser load(Context context, String packageName, String className) {
			SharedPreferences sPref = context.getSharedPreferences(Helpers.TTSB_PREFERENCES, Context.MODE_WORLD_READABLE);
			return load(sPref, packageName, className);
		}
		public static Parser load(SharedPreferences sPref, String key) {
			Parser parser = new Parser(sPref.getString(key, ""));
			parser.parseToSettings();
			return parser;
		}
		public static Parser load(SharedPreferences sPref, String packageName, String className) {
			String line = sPref.getString(packageName + "/" + className, "");
			if (line.equals("")) return new Parser("");
			Parser parser = new Parser(line);
			parser.parseToSettings();
			return parser;
		}
		public static Parser loadWithNull(SharedPreferences sPref, String packageName, String className) {
			String line = sPref.getString(packageName + "/" + className, "{}");
			if (line.equals("{}")) return null;
			Parser parser = new Parser(line);
			parser.parseToSettings();
			return parser;
		}
		
		public static Parser loadAll(Context context, String packageName) {
			SharedPreferences sPref = context.getSharedPreferences(Helpers.TTSB_PREFERENCES, Context.MODE_WORLD_READABLE);
			return loadAll(sPref, packageName);
		}
		public static Parser loadAll(SharedPreferences sPref, String packageName) {
			Parser parser = new Parser(sPref.getString(packageName + "/", ""));
			parser.parseToSettings();
			return parser;
		}
		
		public static boolean contains(Context context, String key) {
			SharedPreferences sPref = context.getSharedPreferences(Helpers.TTSB_PREFERENCES, Context.MODE_WORLD_READABLE);
			return contains(sPref, key);
		}
		public static boolean contains(Context context, String packageName, String className) {
			SharedPreferences sPref = context.getSharedPreferences(Helpers.TTSB_PREFERENCES, Context.MODE_WORLD_READABLE);
			return contains(sPref, packageName, className);
		}
		public static boolean contains(SharedPreferences sPref, String key) {
			return sPref.contains(key);
		}
		public static boolean contains(SharedPreferences sPref, String packageName, String className) {
			return sPref.contains(packageName + "/" + className);
		}
		
		public static boolean containsAll(Context context, String packageName) {
			SharedPreferences sPref = context.getSharedPreferences(Helpers.TTSB_PREFERENCES, Context.MODE_WORLD_READABLE);
			return containsAll(sPref, packageName);
		}
		public static boolean containsAll(SharedPreferences sPref, String packageName) {
			return contains(sPref, packageName + "/");
		}
		
		public static boolean containsPackage(Context context, String packageName) {
			SharedPreferences sPref = context.getSharedPreferences(Helpers.TTSB_PREFERENCES, Context.MODE_WORLD_READABLE);
			return containsPackage(sPref, packageName);
		}
		public static boolean containsPackage(SharedPreferences sPref, String packageName) {
			TreeMap<String, ?> map = new TreeMap<String, Object>(sPref.getAll());
			return containsPackage(map, packageName);
		}
		public static boolean containsPackage(SortedMap<String, ?> map, String packageName) {
			SortedMap<String, ?> smap = map.tailMap(packageName + "/");
			return (!smap.isEmpty() && smap.firstKey().startsWith(packageName + "/"));
		}
		
		public static SortedMap<String, String> importStringToSettingsString(String fullsettings) {
			SortedMap<String, String> result = new TreeMap<String, String>();
			if (fullsettings.equals("")) return result;
			String[] sets = fullsettings.split("||");
			for (String set : sets) {
				String[] set_spl = set.split("|");
				String act_name = set_spl[0];
				String setting_str = set_spl[1];
				result.put(act_name, setting_str);
			}
			return result;
		}
		
		public static void importApp(String packageName, String fullsettings, SharedPreferences sPref_TTSB, SharedPreferences sPref_community) {
			String[] sets = fullsettings.split("||");
			for (String set : sets) {
				String[] set_spl = set.split("|");
				String act_name = set_spl[0];
				String setting_str = set_spl[1];
				Saver.save(sPref_TTSB, packageName, act_name, new Parser(setting_str));
			}
		}
		public static void importApp(String packageName, String fullsettings, Context context) {
			SharedPreferences sPref_TTSB = context.getSharedPreferences(Helpers.TTSB_PREFERENCES, Context.MODE_WORLD_READABLE);
			SharedPreferences sPref_community = context.getSharedPreferences(Database.Preferences.COMMUNITY_PREF_NAME, Context.MODE_PRIVATE);
			importApp(packageName, fullsettings, sPref_TTSB, sPref_community);
		}
	}
	
	@SuppressLint("WorldReadableFiles")
	@SuppressWarnings("deprecation")
	public static class Saver {
		private Saver() { }
		
		public static void save(Context context, String packageName, String className, Parser settings) {
			SharedPreferences sPref = context.getSharedPreferences(Helpers.TTSB_PREFERENCES, Context.MODE_WORLD_READABLE);
			save(sPref, packageName, className, settings);
		}
		public static void save(Context context, String key, Parser settings) {
			SharedPreferences sPref = context.getSharedPreferences(Helpers.TTSB_PREFERENCES, Context.MODE_WORLD_READABLE);
			save(sPref, key, settings);
		}
		public static void save(SharedPreferences sPref, String packageName, String className, Parser settings) {
			save(sPref, packageName + "/" + className, settings);
		}
		public static void save(SharedPreferences sPref, String key, Parser settings) {
			SharedPreferences.Editor edit = sPref.edit();
			settings.parseToString();
			edit.putString(key, settings.getLine());
			edit.apply();
		}
		
		public static void delete(Context context, String packageName, String className) {
			SharedPreferences sPref = context.getSharedPreferences(Helpers.TTSB_PREFERENCES, Context.MODE_WORLD_READABLE);
			delete(sPref, packageName, className);
		}
		public static void delete(SharedPreferences sPref, String packageName, String className) {
			delete(sPref, packageName + "/" + className);
		}
		public static void delete(SharedPreferences sPref, String key) {
			SharedPreferences.Editor edit = sPref.edit();
			edit.remove(key);
			edit.apply();
		}
		
		public static void deleteEverythingFromPackages(Context context, List<String> packageNames) {
			SharedPreferences sPref = context.getSharedPreferences(Helpers.TTSB_PREFERENCES, Context.MODE_WORLD_READABLE);
			deleteEverythingFromPackages(sPref, packageNames);
		}
		public static void deleteEverythingFromPackages(SharedPreferences sPref, List<String> packageNames) {
			SharedPreferences.Editor edit = sPref.edit();
			Map<String, ?> sPrefAll = sPref.getAll();
			for (Entry<String, ?> entry : sPrefAll.entrySet()) {
				if (!entry.getKey().contains("/")) return;
				String entryPackage = entry.getKey().substring(0, entry.getKey().indexOf("/"));
				if (packageNames.contains(entryPackage)) edit.remove(entry.getKey());
			}
			edit.apply();
		}
		public static void deleteEverythingFromPackage(Context context, String packageName) {
			SharedPreferences sPref = context.getSharedPreferences(Helpers.TTSB_PREFERENCES, Context.MODE_WORLD_READABLE);
			deleteEverythingFromPackage(sPref, packageName);
		}
		public static void deleteEverythingFromPackage(SharedPreferences sPref, String packageName) {
			SharedPreferences.Editor edit = sPref.edit();
			Map<String, ?> sPrefAll = sPref.getAll();
			for (Entry<String, ?> entry : sPrefAll.entrySet()) {
				if (!entry.getKey().contains("/")) continue;
				String entryPackage = entry.getKey().substring(0, entry.getKey().indexOf("/"));
				if (packageName.equals(entryPackage)) edit.remove(entry.getKey());
			}
			edit.apply();
		}
		
		public static String getExportAppString(Context context, String packageName) throws NameNotFoundException, StringIndexOutOfBoundsException {
			SharedPreferences sPref = context.getApplicationContext().getSharedPreferences(Helpers.TTSB_PREFERENCES, Context.MODE_WORLD_READABLE);
			ActivityInfo[] act_inf = context.getPackageManager().getPackageInfo(packageName, PackageManager.GET_ACTIVITIES).activities;
			StringBuilder sb = new StringBuilder();
			for (ActivityInfo act : act_inf) {
				String str = sPref.getString(packageName + "/" + act.name, "%%%%");
				if (str != "%%%%") {
					sb.append(act.name);
					sb.append("|");
					sb.append(str);
					sb.append("||");
				}
			}
			sb.deleteCharAt(sb.length() - 1);
			sb.deleteCharAt(sb.length() - 1);
			return sb.toString();
		}
	}
}
