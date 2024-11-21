/*
 *    Copyright 2015 Richard Broker
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package at.pardus.android.webview.gm.run;

import at.pardus.android.webview.gm.util.CacheFileHelper;

import android.util.Base64;
import android.util.Log;
import android.webkit.WebView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class WebViewXmlHttpRequest {

  private static final String TAG = WebViewXmlHttpRequest.class.getName();

  private static final String GLOBAL_JS_OBJECT = "window.wrappedJSObject";

  private JSONObject context;
  private String data;
  private JSONObject headers;
  private String method;
  private String onError;
  private String onLoad;
  private String onProgress;
  private String onReadyStateChange;
  private String onTimeout;
  private String overrideMimeType;
  private String password;
  private String responseType;
  private boolean synchronous;
  private int timeout;
  private JSONObject upload;
  private String url;
  private String user;
  private final WebView view;

  public WebViewXmlHttpRequest(WebView view, String jsonRequestString) {
    // Register the view so that we can execute JS callbacks (e.g. onload)
    this.view = view;

    JSONObject jsonObject;

    try {
      // Get all required object members.
      jsonObject = new JSONObject(jsonRequestString);
      this.method = jsonObject.getString("method");
      this.url = jsonObject.getString("url");
    } catch (JSONException e) {
      Log.e(TAG, "JSON parsing exception:" + e.getMessage());
      return;
    }

    // Get optional object members.
    this.context = jsonObject.optJSONObject("context");
    this.data = jsonObject.optString("data");
    this.headers = jsonObject.optJSONObject("headers");
    this.onError = jsonObject.optString("onerror");
    this.onLoad = jsonObject.optString("onload");
    this.onProgress = jsonObject.optString("onprogress");
    this.onReadyStateChange = jsonObject.optString("onreadystatechange");
    this.onTimeout = jsonObject.optString("ontimeout");
    this.overrideMimeType = jsonObject.optString("overrideMimeType");
    this.password = jsonObject.optString("password");
    this.responseType = jsonObject.optString("responseType");
    this.synchronous = jsonObject.optBoolean("synchronous");
    this.timeout = jsonObject.optInt("timeout", 5000);
    this.upload = jsonObject.optJSONObject("upload");
    this.user = jsonObject.optString("user");
  }

  public String getUrl() {
    return this.url;
  }

  public byte[] getDataBytes() {
    if (this.data.length() == 0) {
      return null;
    }

    try {
      return this.data.getBytes("UTF-8");
    } catch (UnsupportedEncodingException e) {
      Log.e(TAG, "Unable to get UTF-8 bytes from string data: "
          + this.data);
      return null;
    }
  }

  public Map<String, String> getHeaders() {
    if (this.headers == null) {
      return null;
    }

    Map<String, String> headers = new HashMap<String, String>();
    for (Iterator<?> keyIterator = this.headers.keys(); keyIterator
        .hasNext();) {
      String keyName = (String) keyIterator.next();

      try {
        headers.put(keyName, this.headers.getString(keyName));
      } catch (JSONException e) {
        return null;
      }
    }

    return headers;
  }

  public String getUploadOnError() {
    if ((this.upload == null) || (this.upload.length() == 0)) {
      return "";
    }

    return this.upload.optString("onerror");
  }

  public String getUploadOnLoad() {
    if ((this.upload == null) || (this.upload.length() == 0)) {
      return "";
    }

    return this.upload.optString("onload");
  }

  /**
   * Initiates a cross-domain HTTP Request to the address specified by this
   * object's "url" member.
   */
  public WebViewXmlHttpResponse execute() {
    if (this.synchronous) {
      return executeHttpRequestSync();
    } else {
      return executeHttpRequestAsync();
    }
  }

  private WebViewXmlHttpResponse executeHttpRequestAsync() {
    WebViewXmlHttpResponse response = new WebViewXmlHttpResponse(this.context);
    Thread thread = new Thread() {
      @Override
      public void run() {
        executeHttpRequestSync();
      }
    };
    thread.start();

    return response;
  }

  private WebViewXmlHttpResponse executeHttpRequestSync() {
    WebViewXmlHttpResponse response = new WebViewXmlHttpResponse(this.context);
    URL url;
    int totalBytesRead = 0;
    int contentLength;
    boolean duringUpload = false;
    byte[] outputData = this.getDataBytes();

    String cacheUUID = String.valueOf(System.currentTimeMillis());
    response.setResponseCacheUUID(cacheUUID);

    try {
      url = new URL(this.url);
    } catch (MalformedURLException e) {
      Log.e(TAG, "Specified URL is malformed: " + this.url);
      executeOnErrorCallback(response);
      return response;
    }

    try {
      HttpURLConnection httpConn = (HttpURLConnection) url.openConnection(Proxy.NO_PROXY);

      response.setReadyState(WebViewXmlHttpResponse.READY_STATE_OPENED);
      executeOnReadyStateChangeCallback(response);

      // Set connection properties for the GM_xmlhttpRequest
      if (outputData != null) {
        httpConn.setDoOutput(true);
        httpConn.setRequestProperty("Content-Length", Integer.toString(outputData.length));
      }

      if ((!this.user.equals("")) && (!this.password.equals(""))) {
        httpConn.setRequestProperty(
          "Authorization",
          "Basic " + Base64.encodeToString(
            (this.user + ":" + this.password).getBytes("UTF-8"),
            (Base64.DEFAULT | Base64.NO_WRAP)
          )
        );
      }

      httpConn.setRequestMethod(this.method);
      httpConn.setRequestProperty("Connection", "close");

      Map<String, String> headers = this.getHeaders();
      if (this.headers != null) {
        for (String key : headers.keySet()) {
          httpConn.setRequestProperty(key, headers.get(key));
        }
      }

      if (!this.overrideMimeType.equals("")) {
        httpConn.setRequestProperty("Content-Type", this.overrideMimeType);
      }

      if (this.timeout >= 0) {
        // #TODO #FIXME this makes the timeouts cumulative, but it seems
        // most sensible doesn't seem like we can set a timeout for the
        // write either, so POST, PUT wont ever timeout... seems like there
        // should be a better way!
        httpConn.setConnectTimeout(this.timeout);
        httpConn.setReadTimeout(this.timeout);
      }

      // Explicitly initiate connection.
      httpConn.connect();

      // Begin transmitting data if requested.
      if (outputData != null) {
        duringUpload = true;
        OutputStream outputStream = httpConn.getOutputStream();
        outputStream.write(outputData);
        outputStream.close();
        executeUploadOnLoadCallback(response);
        duringUpload = false;
      }

      response.setStatus(httpConn.getResponseCode());
      response.setStatusText(httpConn.getResponseMessage());
      response.setMimeType(httpConn.getContentType());

      // Adjust URL after "Location:" redirects, should be final now.
      response.setFinalUrl(httpConn.getURL().toString());

      response.setReadyState(WebViewXmlHttpResponse.READY_STATE_HEADERS_RECEIVED);
      executeOnReadyStateChangeCallback(response);

      if (httpConn.getResponseCode() != HttpURLConnection.HTTP_OK) {
        Log.e(TAG, "HTTP error from url: " + this.url
            + " HTTP Response " + httpConn.getResponseCode());
        httpConn.disconnect();
        executeOnErrorCallback(response);
        return response;
      }

      // Save all the response headers to the response object.
      JSONObject responseHeaders = new JSONObject();
      Map<String, List<String>> headerFields = httpConn.getHeaderFields();
      for (String key : headerFields.keySet()) {
        if ((key == null) || key.isEmpty()) continue;

        List<String> values = headerFields.get(key);
        if ((values == null) || values.isEmpty()) continue;

        String value = (values.size() == 1)
          ? values.get(0)
          : values.toString();

        try {
          responseHeaders.put(key, value);
        } catch (JSONException e) {
          continue;
        }
      }
      response.setResponseHeaders(responseHeaders);

      // Setup progress parameters if applicable.
      contentLength = httpConn.getContentLength();

      if (contentLength > 0) {
        response.setLengthComputable(true);
        response.setTotal(contentLength);
      }

      response.setReadyState(WebViewXmlHttpResponse.READY_STATE_LOADING);
      executeOnReadyStateChangeCallback(response);

      if (contentLength > 0) {
        response.setLoaded(0);
        executeOnProgressCallback(response);
      }

      // Begin receiving any response data.
      InputStream inputStream = httpConn.getInputStream();

      byte[] buffer = new byte[1024];
      int bytesRead = -1;

      while((bytesRead = inputStream.read(buffer)) != -1) {
        if (bytesRead <= 0) {
          break;
        }

        totalBytesRead += bytesRead;

        if (contentLength > 0) {
          response.setLoaded(totalBytesRead);
          executeOnProgressCallback(response);
        }

        CacheFileHelper.write(view.getContext(), cacheUUID, buffer, 0, bytesRead);
      }

      // Clean up open resources & report completion.
      httpConn.disconnect();

      if (contentLength <= 0) {
        response.setLengthComputable(true);
        response.setTotal(totalBytesRead);
        response.setLoaded(totalBytesRead);
        executeOnProgressCallback(response);
      }

      response.setReadyState(WebViewXmlHttpResponse.READY_STATE_DONE);
      executeOnReadyStateChangeCallback(response);

      executeOnLoadCallback(response);
    } catch (SocketTimeoutException e) {
      Log.e(TAG, "Timeout issuing GM_xmlhttpRequest for: " + this.url
          + ": " + e.getMessage());
      executeOnTimeoutCallback(response);
    } catch (UnsupportedEncodingException e) {
      Log.e(TAG,
          "Unable to get UTF-8 bytes for HTTP Basic Auth username/password");
      return null;
    } catch (IOException e) {
      Log.e(TAG, "Exception issuing GM_xmlhttpRequest for: " + this.url
          + ": " + e.getMessage());

      if (duringUpload) {
        executeUploadOnErrorCallback(response);
      } else {
        executeOnErrorCallback(response);
      }
    }

    return response;
  }

  private void loadUrlOnUiThread(final String jsUrl) {
    view.post(new Runnable() {
      public void run() {
        view.loadUrl(jsUrl);
      }
    });
  }

  private void executeAbstractCallback(WebViewXmlHttpResponse response, String callback) {
    if (callback.equals("")) {
      return;
    }

    StringBuilder sb = new StringBuilder(1 * 1024);
    sb.append("javascript: (function() {");
    sb.append(  "var details = {responseType: '" + this.responseType + "'};");
    sb.append(  "var response = " + response.toJSONString() + ";");
    sb.append(  GLOBAL_JS_OBJECT + "." + callback + "(");
    sb.append(    GLOBAL_JS_OBJECT + "." + "_GM_formatXmlHttpResponse(details, response)");
    sb.append(  ");");
    sb.append("})()");

    loadUrlOnUiThread(sb.toString());
  }

  private void executeOnErrorCallback(WebViewXmlHttpResponse response) {
    executeAbstractCallback(response, this.onError);
  }

  private void executeOnLoadCallback(WebViewXmlHttpResponse response) {
    executeAbstractCallback(response, this.onLoad);
  }

  private void executeOnProgressCallback(WebViewXmlHttpResponse response) {
    executeAbstractCallback(response, this.onProgress);
  }

  private void executeOnReadyStateChangeCallback(WebViewXmlHttpResponse response) {
    executeAbstractCallback(response, this.onReadyStateChange);
  }

  private void executeOnTimeoutCallback(WebViewXmlHttpResponse response) {
    executeAbstractCallback(response, this.onTimeout);
  }

  private void executeUploadOnErrorCallback(WebViewXmlHttpResponse response) {
    executeAbstractCallback(response, getUploadOnError());
  }

  private void executeUploadOnLoadCallback(WebViewXmlHttpResponse response) {
    executeAbstractCallback(response, getUploadOnLoad());
  }
}
