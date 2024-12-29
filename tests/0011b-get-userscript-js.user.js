// ==UserScript==
// @name         test: window.WebViewWM.getUserscriptJS
// @namespace    WebViewWM
// @match        *://*/*
// @unwrap
// @run-at       document-end
// ==/UserScript==

var clean_dom = function() {
  while(document.body.childNodes.length) {
    document.body.removeChild(document.body.childNodes[0]);
  }
}

var append_to_dom = function(text) {
  var div = document.createElement('div');
  var pre = document.createElement('pre');

  pre.innerText = text;
  div.appendChild(pre);
  document.body.appendChild(div);
}

if (window.WebViewWM && window.WebViewWM.getUserscriptJS) {
  var secret = '1234'
  var url    = window.location.href
  var jsCode = window.WebViewWM.getUserscriptJS(secret, url)

  if (jsCode) {
    clean_dom()
    append_to_dom(jsCode)
  }
}
