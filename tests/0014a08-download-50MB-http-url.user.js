// ==UserScript==
// @name         test: GM_download (50 MB, network URL)
// @namespace    WebViewWM
// @match        *://*
// @run-at       document-end
// ==/UserScript==

// https://testfile.org/
// https://testfile.org/all-sizes/

var run_download_test = function() {
  var url  = 'https://link.testfile.org/PDF50MB'
  var name = 'testfile.pdf'

  unsafeWindow.alert('downloading: ' + url.substring(0, 100))

  GM_download(url, name)
}

run_download_test()
