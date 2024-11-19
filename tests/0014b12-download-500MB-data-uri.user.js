// ==UserScript==
// @name         test: GM_download (base64-encoded data: URI)
// @namespace    WebViewWM
// @match        *://*
// @run-at       document-end
// ==/UserScript==

// https://testfile.org/
// https://testfile.org/all-sizes/

var run_download_test = function() {
  var response = GM_xmlhttpRequest({
    'method':       'GET',
    'url':          'https://link.testfile.org/500MB',
    'responseType': 'text_datauri',
    'synchronous':  true
  })

  var url  = response.response
  var name = 'testfile.zip'

  unsafeWindow.alert('downloading: ' + url.substring(0, 100))

  GM_download(url, name)
}

run_download_test()
