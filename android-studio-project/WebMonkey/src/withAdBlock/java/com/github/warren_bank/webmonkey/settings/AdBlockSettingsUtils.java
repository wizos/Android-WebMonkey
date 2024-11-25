package com.github.warren_bank.webmonkey.settings;

import com.github.warren_bank.webmonkey.R;
import com.github.warren_bank.webmonkey.settings.SettingsUtils;

import android.content.Context;
import android.content.SharedPreferences;

public class AdBlockSettingsUtils {

  public static boolean getEnableAdBlockPreference(Context context) {
    return getEnableAdBlockPreference(context, SettingsUtils.getPrefs(context));
  }

  private static boolean getEnableAdBlockPreference(Context context, SharedPreferences prefs) {
    String pref_key     = getEnableAdBlockPreferenceKey(context);
    String pref_default = context.getString(R.string.pref_adblock_enable_default);
    boolean val_default = "true".equals(pref_default);

    return prefs.getBoolean(pref_key, val_default);
  }

  public static String getEnableAdBlockPreferenceKey(Context context) {
    return context.getString(R.string.pref_adblock_enable_key);
  }

  // --------------------

  public static String getCustomAdBlockListUrl(Context context) {
    return getCustomAdBlockListUrl(context, SettingsUtils.getPrefs(context));
  }

  private static String getCustomAdBlockListUrl(Context context, SharedPreferences prefs) {
    String pref_key     = getCustomAdBlockListUrlKey(context);
    String pref_default = context.getString(R.string.pref_adblock_custom_blocklist_url_default);
    String pref_value   = prefs.getString(pref_key, pref_default);

    return pref_value;
  }

  public static String getCustomAdBlockListUrlKey(Context context) {
    return context.getString(R.string.pref_adblock_custom_blocklist_url_key);
  }

  // --------------------

  public static int getCustomAdBlockListUpdateIntervalDays(Context context) {
    return getCustomAdBlockListUpdateIntervalDays(context, SettingsUtils.getPrefs(context));
  }

  private static int getCustomAdBlockListUpdateIntervalDays(Context context, SharedPreferences prefs) {
    String pref_key     = getCustomAdBlockListUpdateIntervalDaysKey(context);
    String pref_default = context.getString(R.string.pref_adblock_custom_blocklist_updateinterval_default);
    String pref_value   = prefs.getString(pref_key, pref_default);

    return Integer.parseInt(pref_value, 10);
  }

  public static String getCustomAdBlockListUpdateIntervalDaysKey(Context context) {
    return context.getString(R.string.pref_adblock_custom_blocklist_updateinterval_key);
  }

}
