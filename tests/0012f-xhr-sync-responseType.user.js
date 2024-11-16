// ==UserScript==
// @name         test: GM_xmlhttpRequest
// @namespace    WebViewWM
// @match        *://*
// @run-at       document-end
// ==/UserScript==

var response_handler = function(response) {
  var div = document.createElement('div');
  var pre = document.createElement('pre');
  var hr  = document.createElement('hr');

  pre.innerText = JSON.stringify(response, null, 2);
  div.appendChild(pre);
  document.body.appendChild(div);
  document.body.appendChild(hr);
};

var generic_details = {
  'method':      'GET',
  'url':         'https://httpbin.org/headers',
  'synchronous': true
};

var response_types = [
  undefined,
  '',
  'text',
  'json',
  'arraybuffer',
  'blob',
  'text_base64',
  'text_datauri'
];

while(document.body.childNodes.length) {
  document.body.removeChild(document.body.childNodes[0]);
}

var responseType, context, response;

for (var i=0; i < response_types.length; i++) {
  responseType = response_types[i];

  context = {
    'request':      (i + 1),
    'responseType': ((responseType === undefined) ? null : responseType)
  };

  response = GM_xmlhttpRequest(Object.assign({}, generic_details, {context, responseType}));

  response_handler(response);
}
