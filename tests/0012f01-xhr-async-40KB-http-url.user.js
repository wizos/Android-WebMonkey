// ==UserScript==
// @name         test: GM_xmlhttpRequest (40 KB)
// @namespace    WebViewWM
// @match        *://*
// @run-at       document-end
// ==/UserScript==

var progress_meter;

var progress_event_handler = function(response) {
  progress_meter.value = response.loaded / response.total;
};

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

var init_dom = function() {
  while(document.body.childNodes.length) {
    document.body.removeChild(document.body.childNodes[0]);
  }

  progress_meter = document.createElement('progress');
  progress_meter.max   = 1.0;
  progress_meter.value = 0.0;
  progress_meter.style.marginBottom = '2em';
  document.body.appendChild(progress_meter);
}

init_dom();

GM_xmlhttpRequest({
  'method':       'GET',
  'url':          'https://github.com/warren-bank/Android-WebMonkey/raw/v04.02.03/android-studio-project/WebMonkey/src/main/res/drawable/launcher.png',
  'synchronous':  true,
  'responseType': 'arraybuffer',
  'onprogress':   progress_event_handler,
  'onload':       arraybuffer_onload_handler.bind(null, 'KB'),
  'onerror':      error_event_handler
});
