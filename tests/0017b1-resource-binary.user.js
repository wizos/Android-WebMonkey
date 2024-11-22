// ==UserScript==
// @name         test: @resource
// @description  test: import PNG binary image resource, and read its content (as a base64-encoded data: URI) with GM_getResourceURL
// @namespace    WebViewWM
// @match        *://*/*
// @resource     imgData https://github.com/warren-bank/Android-WebMonkey/raw/v04.02.02/android-studio-project/WebMonkey/src/main/res/drawable/launcher.png
// @grant        GM_getResourceURL
// @run-at       document-end
// ==/UserScript==

// related docs:
//   https://www.tampermonkey.net/documentation.php#meta:resource
//   https://www.tampermonkey.net/documentation.php#api:GM_getResourceURL

var run_test = function() {
  try {
    var imgData = GM_getResourceURL('imgData') // base64-encoded data: URI

    var imgNode = unsafeWindow.document.createElement('img')
    imgNode.setAttribute('src', imgData)
    imgNode.setAttribute('style', 'position: fixed; top: 0; left: 0; z-index: 9999; max-width: ' + (unsafeWindow.document.body.scrollWidth || unsafeWindow.document.body.clientWidth || '400') + 'px; height: auto;')

    unsafeWindow.document.body.appendChild(imgNode)
  }
  catch(e) {
    unsafeWindow.alert('Error: ' + (e.message));
  }
};

run_test();
