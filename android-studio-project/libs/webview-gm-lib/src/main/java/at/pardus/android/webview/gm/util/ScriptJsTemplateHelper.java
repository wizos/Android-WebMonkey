package at.pardus.android.webview.gm.util;

import at.pardus.android.webview.gm.model.Script;

import org.apache.commons.lang3.StringUtils;

import java.util.UUID;

public class ScriptJsTemplateHelper {

  public static String initialize(String template, String GLOBAL_JS_OBJECT) {
    return StringUtils.replace(
      template,
      "{{GLOBAL_JS_OBJECT}}",
      GLOBAL_JS_OBJECT
    );
  }

  public static String interpolate(String template, Script script, String jsBridgeName, String secret) {
    StringBuilder sb;

    // defaultSignature
    sb = new StringBuilder(1 * 1024);
    sb.append("\"");
    sb.append(script.getName().replace("\"", "\\\""));
    sb.append("\", \"");
    sb.append(script.getNamespace().replace("\"", "\\\""));
    sb.append("\", \"");
    sb.append(secret);
    sb.append("\"");
    String defaultSignature = sb.toString();
    sb = null;

    // callbackPrefix
    sb = new StringBuilder(1 * 1024);
    sb.append("GM_");
    sb.append(script.getName());
    sb.append(script.getNamespace());
    sb.append(UUID.randomUUID().toString());
    String callbackPrefix = sb.toString().replaceAll("[^0-9a-zA-Z_]", "");
    sb = null;

    template = StringUtils.replace(
      template,
      "{{jsBridgeName}}",
      jsBridgeName
    );

    template = StringUtils.replace(
      template,
      "{{defaultSignature}}",
      defaultSignature
    );

    template = StringUtils.replace(
      template,
      "{{callbackPrefix}}",
      callbackPrefix
    );

    return template;
  }

}
