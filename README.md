### [WebMonkey](https://github.com/warren-bank/Android-WebMonkey)

No-frills light-weight Android web browser with support for Greasemonkey userscripts.

Builds upon the [WebView GM library](https://github.com/wbayer/webview-gm) demo application.

#### Background

* the [WebView GM library](https://github.com/wbayer/webview-gm) enhances the native Android System [WebView](https://developer.chrome.com/multidevice/webview/overview)
  - with userscript management:
    * detecting and downloading `*.user.js` URLs
    * parsing and saving to a DB
    * automatic updates
  - with userscript injection:
    * on top-level HTML pages that match URL patterns
  - with support for Greasemonkey API ( [1](https://wiki.greasespot.net/Greasemonkey_Manual:API), [2](https://www.tampermonkey.net/documentation.php), [3](https://violentmonkey.github.io/api/gm/) ) functions:
    * `GM_addStyle`
    * `GM_deleteValue`
    * `GM_getResourceText`
    * `GM_getResourceURL`
    * `GM_getValue`
    * `GM_listValues`
    * `GM_log`
    * `GM_setValue`
    * `GM_xmlhttpRequest`

#### Improvements

* supplements the list of supported Greasemonkey API functions:
  - legacy:
    * `GM_addElement`
    * `GM_cookie.delete`
    * `GM_cookie.list`
    * `GM_cookie.set`
    * `GM_download`
      - where the `url` (or `details.url`) parameter accepts any of the following data types:
        * _String_
          - containing a valid URI having any of the following protocols:
            * _http:_
            * _https:_
            * _data:_
        * _ArrayBuffer_
        * _Uint8Array_
      - only works on devices running API 19 (Android 4.4 KitKat) and higher
        * requires [SAF](https://developer.android.com/guide/topics/providers/document-provider)
    * `GM_fetch`
      - drop-in replacement for `window.fetch` that uses `GM_xmlhttpRequest` to make network requests
    * `GM_info`
    * `GM_registerMenuCommand`
    * `GM_unregisterMenuCommand`
  - [GM 4](https://www.greasespot.net/2017/09/greasemonkey-4-for-script-authors.html):
    * `GM.addElement`
    * `GM.addStyle`
    * `GM.cookie.delete`
    * `GM.cookie.list`
    * `GM.cookie.set`
    * `GM.cookies.delete`
    * `GM.cookies.list`
    * `GM.cookies.set`
    * `GM.deleteValue`
    * `GM.download`
    * `GM.fetch`
    * `GM.getResourceText`
    * `GM.getResourceUrl`
    * `GM.getValue`
    * `GM.info`
    * `GM.listValues`
    * `GM.log`
    * `GM.registerMenuCommand`
    * `GM.setValue`
    * `GM.unregisterMenuCommand`
    * `GM.xmlHttpRequest`
* adds an additional Javascript API interface to expose Android-specific capabilities:
  - legacy:
    * `GM_exit()`
      - causes [WebMonkey](https://github.com/warren-bank/Android-WebMonkey) to close
    * `GM_getUrl()`
      - returns a String containing the URL that is currently loaded in the WebView
      - use case:
        * allows the userscript to detect whether the page has been redirected
          - server response status codes: 301, 302
      - example:
        * `var is_redirect = (GM_getUrl() !== unsafeWindow.location.href)`
    * `GM_getUserAgent()`
      - returns a String containing the _User Agent_ that is currently configured in [_Settings_](#settings) for use by the WebView
    * `GM_loadFrame(urlFrame, urlParent, proxyFrame)`
      - loads an iframe into the WebView
      - where:
        * [required] `urlFrame`   is a String URL: the page loaded into the iframe
        * [required] `urlParent`  is a String URL: value for `window.top.location.href` and `window.parent.location.href` as observed from within the iframe
        * [optional] `proxyFrame` is a boolean: a truthy value causes `urlFrame` to be downloaded in Java
          - `urlParent` is sent in the _Referer_ header
          - a successful (200-299) response is dynamically loaded into [_iframe.srcdoc_](https://developer.mozilla.org/en-US/docs/Web/API/HTMLIFrameElement/srcdoc)
          - the benefit:
            * same-origin policy does not apply
            * when `urlParent` and `urlFrame` belong to different domains, a userscript running in the top window can access the DOM within the iframe window
          - special use case:
            * when `urlFrame` only serves the desired web page content if `urlParent` is sent in the _Referer_ header
      - example:
        * `('http://example.com/iframe_window.html', 'http://example.com/parent_window.html')`
      - use case:
        * _"parent_window.html"_ contains:
          - an iframe to display _"iframe_window.html"_
          - other content that is not wanted
        * though a userscript could easily do the necessary housekeeping:
          - detach the iframe
          - remove all other DOM elements from body
          - reattach the iframe
        * this method provides a better solution:
          - removes all scripts that are loaded into the parent window
          - handles all the css needed to resize the iframe to maximize its display within the parent window
          - makes it easy to handle this common case
      - why this is a common case:
        * _"iframe_window.html"_ performs a check to verify that it is loaded in the proper parent window
        * example 1:
          ```javascript
            const urlParent = 'http://example.com/parent_window.html'
            try {
              // will throw when either:
              // - `top` is loaded from a different domain
              // - `top` is loaded from the same origin, but the URL path does not match 'parent_window.html'
              if(window.top.location.href !== urlParent)
                throw ''
            }
            catch(e) {
              // will redirect `top` window to the proper parent window
              window.top.location = urlParent
            }
          ```
        * example 2:
          ```javascript
            const urlParent = 'http://example.com/parent_window.html'
            {
              // will redirect to proper parent window when 'iframe_window.html' is loaded without a `top` window
              if(window === window.top)
                window.location = urlParent
            }
          ```
    * `GM_loadUrl(url, ...headers)`
      - loads a URL into the WebView with additional HTTP request headers
      - where:
        * [required] `url`     is a String URL
        * [optional] `headers` is a list of String name/value pairs
      - example:
        * `('http://example.com/iframe_window.html', 'Referer', 'http://example.com/parent_window.html')`
    * `GM_removeAllCookies()`
      - completely removes _all_ cookies for _all_ web sites
    * `GM_resolveUrl(urlRelative, urlBase)`
      - returns a String containing `urlRelative` resolved relative to `urlBase`
      - where:
        * [required] `urlRelative` is a String URL: relative path
        * [optional] `urlBase`     is a String URL: absolute path
          - default value: the URL that is currently loaded in the WebView
      - examples:
        * `('video.mp4', 'http://example.com/iframe_window.html')`
        * `('video.mp4')`
    * `GM_setUserAgent(value)`
      - changes the _User Agent_ value that is configured in [_Settings_](#settings)
      - where:
        * [optional] `value` is a String
          - special cases:
            * `WebView` (or falsy)
            * `Chrome`
    * `GM_startIntent(action, data, type, ...extras)`
      - starts an implicit [Intent](https://developer.android.com/training/basics/intents/sending)
      - where:
        * [required, can be empty] `action` is a String
        * [required, can be empty] `data`   is a String URL
        * [required, can be empty] `type`   is a String mime-type for format of `data`
        * [optional] `extras` is a list of String name/value pairs
      - example:
        * `('android.intent.action.VIEW', 'http://example.com/video.mp4', 'video/mp4', 'referUrl', 'http://example.com/videos.html')`
    * `GM_toastLong(message)`
    * `GM_toastShort(message)`
  - [GM 4](https://www.greasespot.net/2017/09/greasemonkey-4-for-script-authors.html):
    * `GM.exit`
    * `GM.getUrl`
    * `GM.getUserAgent`
    * `GM.loadFrame`
    * `GM.loadUrl`
    * `GM.removeAllCookies`
    * `GM.resolveUrl`
    * `GM.setUserAgent`
    * `GM.startIntent`
    * `GM.toastLong`
    * `GM.toastShort`

#### Settings

* default browser home page
  - _Continue where you left off_
  - [_Blank page_](about:blank)
  - [_Userscripts by developer_](https://warren-bank.github.io/Android-WebMonkey/index.html)
  - [_Userscripts at Greasy Fork_](https://greasyfork.org/)
  - _Custom URL_
* _User Agent_
  - _WebView_
  - _Chrome_ desktop
  - _Custom User Agent_
* implementation for `@run-at document-end`
  - document: [_DOMContentLoaded_](https://developer.mozilla.org/en-US/docs/Web/API/Document/DOMContentLoaded_event)
  - WebViewClient: [_onPageFinished_](https://developer.android.com/reference/android/webkit/WebViewClient#onPageFinished(android.webkit.WebView,%20java.lang.String))
    * this option can cause userscripts to run more than once per page load
      - [issue](https://issuetracker.google.com/issues/36983315)
      - [discussion](https://stackoverflow.com/q/18282892)
      - [utility: _WebViewClient_ event observer](https://github.com/warren-bank/Android-WebViewClientObserver)
* page load behavior on HTTPS certificate error
  - cancel
  - proceed
  - ask
* script update interval
  - number of days to wait between checks
  - special case: `0` disables automatic script updates
* shared secret for JS to access low-level API method:
  ```javascript
    window.WebViewWM.getUserscriptJS(secret, url)
  ```
  - specific use case: _mitmproxy_ script to inject JS code to bootstrap userscripts in [iframes](./IFRAMES.md)
* enable remote debugger
  - allows remote access over an _adb_ connection, such as:
    ```bash
      adb connect "${IP_of_phone_on_LAN}:5555"
    ```
  - remote debugger is accessible in _Chrome_ at:
    ```text
      chrome://inspect/#devices
    ```
  - the interface uses _Chrome DevTools_

#### AdBlock Settings

* enable AdBlock
  - default: _true_
* custom Blocklist URL
  - default: [pgl.yoyo.org](https://pgl.yoyo.org/adservers/serverlist.php?hostformat=nohtml&showintro=0&mimetype=plaintext)
  - note: if this URL is empty or cannot be downloaded, a static copy of the default blocklist that is included in the app will be used
* custom Blocklist update interval
  - number of days to wait before downloading a fresh copy of the blocklist
  - default: _7_

#### Security

1. closure
   * by default, all JS code injected into web pages is wrapped by a closure
   * the closure is implemented as a [self-executing anonymous function](https://developer.mozilla.org/en-US/docs/Glossary/Self-Executing_Anonymous_Function), also known as an [immediately invoked function expression](https://developer.mozilla.org/en-US/docs/Glossary/IIFE)
   * this security feature can be disabled by a userscript by adding any of the following declarations to its header block:
     ```text
     // @unwrap
     // @flag noJsClosure
     // @flags noJsClosure
     ```
   * [_SANDBOX.txt_](./SANDBOX.txt) contains more details
2. sandbox
   * when a closure is disabled, a sandbox is also disabled
   * when a closure is enabled, by default, all JS global variables saved to the `window` Object are stored in a sandbox
   * as such, JS code outside of the userscript cannot see or access these variables
   * however, the JS code inside of the userscript can see and access all global variables&hellip; including its own
   * the sandbox is implemented as an ES6 [`Proxy`](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Proxy)
   * this security feature can be disabled by a userscript by adding any of the following declarations to its header block:
     ```text
     // @grant none
     // @flag noJsSandbox
     // @flags noJsSandbox
     ```
   * [_SANDBOX.txt_](./SANDBOX.txt) contains more details
3. API-level permissions
   * `// @grant <API>` is only required to use API methods that I would consider to be potentially _dangerous_
   * several of these API methods are grouped together,<br>and permission granted for any one&hellip;<br>also grants permission to use all other API methods in the same group
     1. group:
        * `GM_setValue`
        * `GM_getValue`
        * `GM_deleteValue`
        * `GM_listValues`
        * `GM.setValue`
        * `GM.getValue`
        * `GM.deleteValue`
        * `GM.listValues`
     2. group:
        * `GM_cookie`
        * `GM_cookie.list`
        * `GM_cookie.set`
        * `GM_cookie.delete`
        * `GM.cookie`
        * `GM.cookie.list`
        * `GM.cookie.set`
        * `GM.cookie.delete`
        * `GM.cookies`
        * `GM.cookies.list`
        * `GM.cookies.set`
        * `GM.cookies.delete`
     3. group:
        * `GM_removeAllCookies`
        * `GM.removeAllCookies`
     4. group:
        * `GM_setUserAgent`
        * `GM.setUserAgent`

#### Caveats

* userscripts only run in the top window
  - a _mitmproxy_ script is required to load userscripts into [iframes](./IFRAMES.md)

#### Legal:

* copyright: [Warren Bank](https://github.com/warren-bank)
* license: [GPL-2.0](https://www.gnu.org/licenses/old-licenses/gpl-2.0.txt)
