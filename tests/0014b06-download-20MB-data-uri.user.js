// ==UserScript==
// @name         test: GM_download (base64-encoded data: URI)
// @namespace    WebViewWM
// @match        *://*
// @run-at       document-end
// ==/UserScript==

// https://test-videos.co.uk/bigbuckbunny/mp4-h264

var run_download_test = function() {
  var response = GM_xmlhttpRequest({
    'method':       'GET',
    'url':          'https://test-videos.co.uk/vids/bigbuckbunny/mp4/h264/1080/Big_Buck_Bunny_1080_10s_20MB.mp4',
    'responseType': 'text_datauri',
    'synchronous':  true
  })

  var url  = response.response
  var name = 'BigBuckBunny.mp4'

  unsafeWindow.alert('downloading: ' + url.substring(0, 100))

  GM_download(url, name)
}

run_download_test()
