// ==UserScript==
// @name         test: @resource
// @description  test: import JSON text file resource, and read its content with GM_getResourceText
// @namespace    WebViewWM
// @match        *://*
// @run-at       document-end
// @resource     pkgJson https://github.com/warren-bank/Android-WebMonkey/raw/v04.02.02/package.json
// @grant        GM_getResourceText
// ==/UserScript==

// related docs:
//   https://www.tampermonkey.net/documentation.php#meta:resource
//   https://www.tampermonkey.net/documentation.php#api:GM_getResourceText

var run_test = function() {
  try {
    var pkgJson = GM_getResourceText('pkgJson')
    var pkgData = JSON.parse(pkgJson)

    unsafeWindow.alert('License: ' + pkgData.license);
  }
  catch(e) {
    unsafeWindow.alert('Error: ' + (e.message));
  }
};

run_test();
