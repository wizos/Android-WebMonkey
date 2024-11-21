// ==UserScript==
// @name         test: GM_xmlhttpRequest
// @namespace    WebViewWM
// @match        *://*
// @run-at       document-end
// ==/UserScript==

var datauri_onload_handler = function(response) {
  if (response.error) {
    error_event_handler(response)
    return
  }

  var img = document.createElement('img');

  img.setAttribute('src',   response.response);
  img.setAttribute('style', 'max-width: 100%; height: auto;');

  document.body.appendChild(img);
};

var error_event_handler = function(response) {
  var div = document.createElement('div');
  var pre = document.createElement('pre');

  pre.innerText = 'Error: ' + JSON.stringify(response, null, 2);
  div.appendChild(pre);
  document.body.appendChild(div);
};

while(document.body.childNodes.length) {
  document.body.removeChild(document.body.childNodes[0]);
}

datauri_onload_handler(
  GM_xmlhttpRequest({
    'method':       'GET',
    'url':          'https://github.com/warren-bank/Android-WebMonkey/raw/v04.02.03/android-studio-project/WebMonkey/src/main/res/drawable/launcher.png',
    'synchronous':  true,
    'responseType': 'text_datauri'
  })
);
