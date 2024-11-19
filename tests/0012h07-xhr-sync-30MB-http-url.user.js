// ==UserScript==
// @name         test: GM_xmlhttpRequest (30 MB)
// @namespace    WebViewWM
// @match        *://*
// @run-at       document-end
// ==/UserScript==

// https://test-videos.co.uk/bigbuckbunny/mp4-h264

var base64_onload_handler = function(units, response) {
  var decimals = 2
  var div = document.createElement('div');
  var pre = document.createElement('pre');

  pre.innerText = [
    'Actual size: ' + format_bytes(units, decimals, response.total) + ' ' + units,
    'Base64 size: ' + format_bytes(units, decimals, get_approx_base64_bytes(response)) + ' ' + units
  ].join("\n");
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

var get_approx_base64_bytes = function(response) {
  return Math.floor(response.response.length / 4) * 3
}

while(document.body.childNodes.length) {
  document.body.removeChild(document.body.childNodes[0]);
}

base64_onload_handler('MB',
  GM_xmlhttpRequest({
    'method':       'GET',
    'url':          'https://test-videos.co.uk/vids/bigbuckbunny/mp4/h264/1080/Big_Buck_Bunny_1080_10s_30MB.mp4',
    'synchronous':  true,
    'responseType': 'text_base64'
  })
);
