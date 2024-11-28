package com.github.warren_bank.webmonkey;

import at.pardus.android.webview.gm.model.Script;
import at.pardus.android.webview.gm.util.ResourceHelper;
import at.pardus.android.webview.gm.util.ScriptJsCode;

import android.content.Context;
import android.text.TextUtils;

public class WmScriptJsCode extends ScriptJsCode {

  private static String WM_API_LEGACY          = "";
  private static String WM_API_LEGACY_TEMPLATE = "";
  private static String WM_API_V4_POLYFILL     = "";
  private static String WM_CLOSURE             = "";

  public static void initStaticResources(Context context) {
    if (TextUtils.isEmpty(WM_API_LEGACY)) {
      try {
        WM_API_LEGACY = ResourceHelper.getRawStringResource(context, R.raw.wm_api_legacy);
      }
      catch(Exception e) {}
    }
    if (TextUtils.isEmpty(WM_API_LEGACY_TEMPLATE)) {
      try {
        WM_API_LEGACY_TEMPLATE = ResourceHelper.getRawStringResource(context, R.raw.wm_api_legacy_template_all);

        WM_API_LEGACY_TEMPLATE += (useES6)
          ? ResourceHelper.getRawStringResource(context, R.raw.wm_api_legacy_template_es6)
          : ResourceHelper.getRawStringResource(context, R.raw.wm_api_legacy_template_es5);

        WM_API_LEGACY_TEMPLATE = WmJsApi.initializeTemplate(WM_API_LEGACY_TEMPLATE);
      }
      catch(Exception e) {}
    }
    if (TextUtils.isEmpty(WM_API_V4_POLYFILL)) {
      try {
        if (useES6)
          WM_API_V4_POLYFILL = ResourceHelper.getRawStringResource(context, R.raw.wm_api_v4_polyfill);
      }
      catch(Exception e) {}
    }
    if (TextUtils.isEmpty(WM_CLOSURE)) {
      try {
        if (useES6)
          WM_CLOSURE = ResourceHelper.getRawStringResource(context, R.raw.wm_closure);
      }
      catch(Exception e) {}
    }

    ScriptJsCode.initStaticResources(context);
  }

  private WmJsApi jsApi;

  protected WmScriptJsCode(WmJsApi jsApi) {
    this.jsApi = jsApi;
  }

  @Override
  protected String getJsApi(Script script, String jsBridgeName, String secret) {
    // jsApi
    StringBuilder sb = new StringBuilder(4 * 1024);
    sb.append(
      super.getJsApi(script, jsBridgeName, secret)
    );
    sb.append(WM_API_LEGACY);
    sb.append(
      jsApi.getJsApi(WM_API_LEGACY_TEMPLATE, script)
    );
    sb.append(WM_API_V4_POLYFILL);
    return sb.toString();
  }

  @Override
  protected String getJsUserscript(Script script, String jsBeforeScript, String jsAfterScript) {
    if (script.useJsClosure()) {
      StringBuilder sb = new StringBuilder(4 * 1024);
      sb.append(WM_CLOSURE);
      sb.append(
        super.getJsUserscript(script, jsBeforeScript, jsAfterScript)
      );
      return sb.toString();
    }
    else {
      return super.getJsUserscript(script, jsBeforeScript, jsAfterScript);
    }
  }

}
