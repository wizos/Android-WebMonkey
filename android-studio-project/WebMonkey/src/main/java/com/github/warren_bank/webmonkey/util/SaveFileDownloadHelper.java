package com.github.warren_bank.webmonkey.util;

import android.util.Base64;
import android.webkit.WebView;

import at.pardus.android.webview.gm.run.WebViewXmlHttpRequest;
import at.pardus.android.webview.gm.run.WebViewXmlHttpResponse;

public class SaveFileDownloadHelper {

  public static final class Download {
    public byte[] buffer;
    public String mimeType;

    public Download(byte[] _buffer, String _mimeType) {
      buffer   = _buffer;
      mimeType = _mimeType;
    }
  }

  /**
   * Don't run on the UI thread!
   */
  public static Download download(WebView view, String jsonRequestString) {
    WebViewXmlHttpRequest  request  = new WebViewXmlHttpRequest(view, jsonRequestString);
    WebViewXmlHttpResponse response = request.execute();

    return (response == null)
      ? null
      : convertBase64(response.getResponseText(), response.getMimeType());
  }

  public static Download convertBase64(String base64, String mimeType) {
    if ((base64 == null) || base64.isEmpty())
      return null;

    byte[] buffer = Base64.decode(base64, Base64.DEFAULT);

    return new Download(buffer, mimeType);
  }
}
