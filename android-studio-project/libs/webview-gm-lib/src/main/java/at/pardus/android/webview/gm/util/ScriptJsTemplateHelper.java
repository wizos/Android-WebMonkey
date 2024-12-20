package at.pardus.android.webview.gm.util;

import at.pardus.android.webview.gm.model.Script;

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ScriptJsTemplateHelper {

  public static String initialize(String template, String GLOBAL_JS_OBJECT) {
    return replace(template, "{{GLOBAL_JS_OBJECT}}", GLOBAL_JS_OBJECT);
  }

  public static String interpolate(String template, Script script, String jsBridgeName, String secret) {
    return interpolate(template, script, jsBridgeName, secret, true);
  }

  public static String interpolate(String template, Script script, String jsBridgeName, String secret, boolean addCallbackPrefix) {
    Map<String, String> replacements = new HashMap();
    replacements.put("{{jsBridgeName}}", jsBridgeName);

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
    replacements.put("{{defaultSignature}}", defaultSignature);

    if (addCallbackPrefix) {
      // callbackPrefix
      sb = new StringBuilder(1 * 1024);
      sb.append("GM_");
      sb.append(script.getName());
      sb.append(script.getNamespace());
      sb.append(UUID.randomUUID().toString());
      String callbackPrefix = sb.toString().replaceAll("[^0-9a-zA-Z_]", "");
      sb = null;
      replacements.put("{{callbackPrefix}}", callbackPrefix);
    }

    return replace(template, replacements);
  }

  public static String replace(String template, String key, String value) {
    Map<String, String> replacements = new HashMap();
    replacements.put(key, value);

    return replace(template, replacements);
  }

  public static String replace(String template, Map<String, String> replacements) {
    for (Map.Entry<String, String> replacement : replacements.entrySet()) {
      template = StringUtils.replace(
        template,
        replacement.getKey(),
        replacement.getValue()
      );
    }
    return template;
  }

}
