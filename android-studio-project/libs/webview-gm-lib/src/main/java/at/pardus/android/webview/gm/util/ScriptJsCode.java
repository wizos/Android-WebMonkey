package at.pardus.android.webview.gm.util;

import at.pardus.android.webview.gm.R;
import at.pardus.android.webview.gm.model.Script;
import at.pardus.android.webview.gm.model.ScriptRequire;
import at.pardus.android.webview.gm.util.ResourceHelper;
import at.pardus.android.webview.gm.util.ScriptInfo;
import at.pardus.android.webview.gm.util.ScriptJsTemplateHelper;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;

public class ScriptJsCode {
  protected static final boolean useES6 = (Build.VERSION.SDK_INT >= 21);  // use ES5 in Android <= 4.4 because WebView is outdated and cannot be updated

  private static final String GLOBAL_JS_OBJECT = "unsafeWindow.wrappedJSObject";

  private static String GM_API_LEGACY_MISSING  = "";
  private static String GM_API_LEGACY          = "";
  private static String GM_API_LEGACY_TEMPLATE = "";
  private static String GM_API_V4_POLYFILL     = "";

  private static String JS_CLOSURE_1 = "";  // from: start of closure.     => to: start of GM API.
  private static String JS_CLOSURE_2 = "";  // from: end of GM API.        => to: start of userscript.
  private static String JS_CLOSURE_3 = "";  // from: end of userscript.    => to: start of dynamic input.
  private static String JS_CLOSURE_4 = "";  // from: end of dynamic input. => to: end of closure.

  public static void initStaticResources(Context context) {
    if (TextUtils.isEmpty(GM_API_LEGACY_MISSING)) {
      try {
        GM_API_LEGACY_MISSING = ResourceHelper.getRawStringResource(context, R.raw.gm_api_legacy_missing);
      }
      catch(Exception e) {}
    }
    if (TextUtils.isEmpty(GM_API_LEGACY)) {
      try {
        GM_API_LEGACY = ResourceHelper.getRawStringResource(context, R.raw.gm_api_legacy);
      }
      catch(Exception e) {}
    }
    if (TextUtils.isEmpty(GM_API_LEGACY_TEMPLATE)) {
      try {
        GM_API_LEGACY_TEMPLATE = ScriptJsTemplateHelper.initialize(
          ResourceHelper.getRawStringResource(context, R.raw.gm_api_legacy_template),
          GLOBAL_JS_OBJECT
        );
      }
      catch(Exception e) {}
    }
    if (TextUtils.isEmpty(GM_API_V4_POLYFILL)) {
      try {
        if (useES6)
          GM_API_V4_POLYFILL = ResourceHelper.getRawStringResource(context, R.raw.gm_api_v4_polyfill);
      }
      catch(Exception e) {}
    }

    if (TextUtils.isEmpty(JS_CLOSURE_1)) {
      try {
        JS_CLOSURE_1 = ResourceHelper.getRawStringResource(context, R.raw.js_closure_1);
      }
      catch(Exception e) {}
    }
    if (TextUtils.isEmpty(JS_CLOSURE_2)) {
      try {
        if (useES6)
          JS_CLOSURE_2 = ResourceHelper.getRawStringResource(context, R.raw.js_closure_2_es6);
        else
          JS_CLOSURE_2 = ResourceHelper.getRawStringResource(context, R.raw.js_closure_2_es5);
      }
      catch(Exception e) {}
    }
    if (TextUtils.isEmpty(JS_CLOSURE_3)) {
      try {
        JS_CLOSURE_3 = ResourceHelper.getRawStringResource(context, R.raw.js_closure_3);
      }
      catch(Exception e) {}
    }
    if (TextUtils.isEmpty(JS_CLOSURE_4)) {
      try {
        JS_CLOSURE_4 = ResourceHelper.getRawStringResource(context, R.raw.js_closure_4);
      }
      catch(Exception e) {}
    }

    ScriptInfo.initStaticResources(context);
  }

  public ScriptJsCode() {
  }

  public String getJsCode(Script script, String jsBeforeScript, String jsAfterScript, String jsBridgeName, String secret, String readyState) {
    String[] readyStates = new String[] {readyState};

    return getJsCode(script, jsBeforeScript, jsAfterScript, jsBridgeName, secret, readyStates);
  }

  public String getJsCode(Script script, String jsBeforeScript, String jsAfterScript, String jsBridgeName, String secret, String[] readyStates) {
    String runAt = script.getRunAt();
    if (runAt == null)
      runAt = Script.RUNATIDLE;

    boolean runNow = false;
    if (readyStates != null) {
      for (String readyState : readyStates) {
        if (runAt.equals(readyState)) {
          runNow = true;
          break;
        }
      }
    }

    return (!runNow)
      ? ""
      : getJsCode(script, jsBeforeScript, jsAfterScript, jsBridgeName, secret);
  }

  private String getJsCode(Script script, String jsBeforeScript, String jsAfterScript, String jsBridgeName, String secret) {
    return script.useJsClosure()
      ? getJsCodeWithClosure(script, jsBeforeScript, jsAfterScript, jsBridgeName, secret)
      : getJsCodeNoClosure  (script, jsBeforeScript, jsAfterScript);
  }

  private String getJsCodeNoClosure(Script script, String jsBeforeScript, String jsAfterScript) {
    return getJsUserscript(script, jsBeforeScript, jsAfterScript);
  }

  private String getJsCodeWithClosure(Script script, String jsBeforeScript, String jsAfterScript, String jsBridgeName, String secret) {
    StringBuilder sb = new StringBuilder(4 * 1024);
    sb.append(JS_CLOSURE_1);
    sb.append(getJsApi(script, jsBridgeName, secret));
    sb.append(JS_CLOSURE_2);
    sb.append(getJsUserscript(script, jsBeforeScript, jsAfterScript));
    sb.append(JS_CLOSURE_3);
    sb.append(script.useJsSandbox() ? "true" : "false");
    sb.append(JS_CLOSURE_4);
    return sb.toString();
  }

  protected String getJsApi(Script script, String jsBridgeName, String secret) {
    StringBuilder sb;

    // jsApi
    sb = new StringBuilder(4 * 1024);
    sb.append(GLOBAL_JS_OBJECT + " = " + GLOBAL_JS_OBJECT + " || {};\n");
    sb.append(GM_API_LEGACY_MISSING);
    sb.append(GM_API_LEGACY);

    sb.append(GLOBAL_JS_OBJECT + "._GM_formatXmlHttpResponse = _GM_formatXmlHttpResponse;\n");

    // -------------------------
    // Greasemonkey API (legacy)
    // -------------------------

    sb.append("var GM_info = ");
    sb.append(  ScriptInfo.toJSONString(script));
    sb.append(";");
    sb.append("\n");

    sb.append(
      ScriptJsTemplateHelper.interpolate(
        GM_API_LEGACY_TEMPLATE, script, jsBridgeName, secret
      )
    );

    sb.append(GM_API_V4_POLYFILL);

    return sb.toString();
  }

  protected String getJsUserscript(Script script, String jsBeforeScript, String jsAfterScript) {
    StringBuilder sb = new StringBuilder(4 * 1024);

    // Get @require'd scripts to inject for this script.
    ScriptRequire[] requires = script.getRequires();
    if (requires != null) {
      for (ScriptRequire currentRequire : requires) {
        sb.append(currentRequire.getContent());
        sb.append("\n");
      }
    }

    if (!TextUtils.isEmpty(jsBeforeScript))
      sb.append(jsBeforeScript);

    sb.append(script.getContent());

    if (!TextUtils.isEmpty(jsAfterScript))
      sb.append(jsAfterScript);

    return sb.toString();
  }

}
