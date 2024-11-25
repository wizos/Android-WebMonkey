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
    String pref_default = context.getString(R.string.pref_enableadblock_default);
    boolean val_default = "true".equals(pref_default);

    return prefs.getBoolean(pref_key, val_default);
  }

  public static String getEnableAdBlockPreferenceKey(Context context) {
    return context.getString(R.string.pref_enableadblock_key);
  }

}
