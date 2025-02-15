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

package at.pardus.android.webview.gm.model;

import android.text.TextUtils;
import android.util.Base64;
import android.webkit.MimeTypeMap;

import java.io.UnsupportedEncodingException;

/**
 * Object containing one @resource Metadata entry.
 *
 * @see <a href="http://wiki.greasespot.net/Metadata_Block">Metadata Block</a>
 */
public class ScriptResource {
  private String name;
  private String url;
  private byte[] data;

  public ScriptResource(String name, String url, byte[] data) {
    this.name = name;
    this.url = url;
    this.data = data;
  }

  public String getName() {
    return this.name;
  }

  public String getUrl() {
    return this.url;
  }

  public byte[] getData() {
    return this.data;
  }

  /**
   * Converts the "data" byte array into a base64 String.
   *
   * @return A base64 encoded String representing this class' 'data' variable
   */
  public String getDataBase64() {
    return Base64.encodeToString(this.data, (Base64.DEFAULT | Base64.NO_WRAP));
  }

  /**
   * Converts the "data" byte array into a String which can be used as a
   * javascript data URI.
   *
   * @return A RFC 2397 data URI String representing this class' 'data'
   *         variable
   * @see <tt><a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/data_URIs">Data URIs</a></tt>
   */
  public String getJavascriptUrl() {
    String mimeType;

    try {
      String extension = MimeTypeMap.getFileExtensionFromUrl(this.url);
      if (TextUtils.isEmpty(extension))
        throw new Exception();

      extension = extension.toLowerCase();
      MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
      mimeType = mimeTypeMap.getMimeTypeFromExtension(extension);
      if (TextUtils.isEmpty(mimeType)) {
        switch(extension) {
          case "js":
            mimeType = "text/javascript";
            break;
          case "json":
            mimeType = "application/json";
            break;
          case "wasm":
            mimeType = "application/wasm";
            break;
          default:
            throw new Exception();
        }
      }
    }
    catch(Exception e) {
      // Fallback to "bytes" if we can't determine the actual mimetype.
      mimeType = "application/octet-stream";
    }

    return "data:" + mimeType + ";base64," + getDataBase64();
  }

  /**
   * Converts the "data" byte array into a UTF-8 String which can be used
   * passed to a userscript.
   *
   * @return A UTF-8 String representing this class' 'data' variable
   */
  public String getJavascriptString() {
    try {
      return new String(this.data, "UTF-8");
    } catch (UnsupportedEncodingException e) {
      return "";
    }
  }
}
