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
    WebViewXmlHttpRequest request = new WebViewXmlHttpRequest(view, jsonRequestString);
    String url = request.getUrl();

    if ((url == null) || url.isEmpty())
      return null;

    String base64   = null;
    String mimeType = null;

    if (url.substring(0, 5).toLowerCase().equals("data:")) {
      // cleanup
      request = null;

      int index_start = 5;
      int index_end   = url.indexOf(";");

      if (index_end > index_start) {
        mimeType = url.substring(index_start, index_end);
      }

      index_start = url.indexOf(",");

      if (index_start != -1) {
        base64 = url.substring(index_start);
      }

      // cleanup
      url = null;
    }
    else {
      WebViewXmlHttpResponse response = request.execute();

      if (response == null)
        return null;

      base64   = response.getResponseText();
      mimeType = response.getMimeType();

      // cleanup
      request  = null;
      response = null;
      url      = null;
    }

    return convertBase64(base64, mimeType);
  }

  public static Download convertBase64(String base64, String mimeType) {
    if ((base64 == null) || base64.isEmpty())
      return null;

    byte[] buffer = Base64.decode(base64, Base64.DEFAULT);

    return new Download(buffer, mimeType);
  }
}
