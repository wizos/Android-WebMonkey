// ==UserScript==
// @name         test: GM_xmlhttpRequest (500 MB)
// @namespace    WebViewWM
// @match        *://*/*
// @grant        GM_xmlhttpRequest
// @run-at       document-end
// ==/UserScript==

// https://testfile.org/
// https://testfile.org/all-sizes/

var arraybuffer_onload_handler = function(units, response) {
  if (response.error) {
    error_event_handler(response)
    return
  }

  var decimals = 2
  var div = document.createElement('div');
  var pre = document.createElement('pre');

  pre.innerText = [
    'Actual size: ' + format_bytes(units, decimals, response.total              ) + ' ' + units,
    'Buffer size: ' + format_bytes(units, decimals, response.response.byteLength) + ' ' + units
  ].join("\n");
  div.appendChild(pre);
  document.body.appendChild(div);
};

var error_event_handler = function(response) {
  var div = document.createElement('div');
  var pre = document.createElement('pre');

  pre.innerText = 'Error: ' + JSON.stringify(response, null, 2);
  div.appendChild(pre);
  document.body.appendChild(div);
};

var format_bytes = function(units, decimals, bytes) {
  decimals = decimals || 0
  decimals = Math.pow(10, decimals)

  switch(units) {
    case 'B':
    case 'bytes':
      return bytes
    case 'KB':
    case 'kilobytes':
      return Math.floor((bytes * decimals) / 1024) / decimals
    case 'MB':
    case 'megabytes':
      return Math.floor((bytes * decimals) / Math.pow(1024, 2)) / decimals
    case 'GB':
    case 'gigabytes':
      return Math.floor((bytes * decimals) / Math.pow(1024, 3)) / decimals
    default:
      return bytes
  }
}

while(document.body.childNodes.length) {
  document.body.removeChild(document.body.childNodes[0]);
}

arraybuffer_onload_handler('MB',
  GM_xmlhttpRequest({
    'method':       'GET',
    'url':          'https://link.testfile.org/500MB',
    'synchronous':  true,
    'responseType': 'arraybuffer'
  })
);
