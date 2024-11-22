// ==UserScript==
// @name         test: GM_xmlhttpRequest
// @namespace    WebViewWM
// @match        *://*/*
// @grant        GM_xmlhttpRequest
// @run-at       document-end
// ==/UserScript==

var generic_event_handler = function(event_type, response) {
  var div = document.createElement('div');
  var pre = document.createElement('pre');
  var hr  = document.createElement('hr');

  pre.innerText = event_type + ":\n" + JSON.stringify(response, null, 2);
  div.appendChild(pre);
  document.body.appendChild(div);
  document.body.appendChild(hr);
};

var generic_details = {
  'method':             'GET',
  'url':                'https://httpbin.org/headers',
  'onabort':            generic_event_handler.bind(null, 'onabort'),
  'onerror':            generic_event_handler.bind(null, 'onerror'),
  'onload':             generic_event_handler.bind(null, 'onload'),
  'onprogress':         generic_event_handler.bind(null, 'onprogress'),
  'onreadystatechange': generic_event_handler.bind(null, 'onreadystatechange'),
  'ontimeout':          generic_event_handler.bind(null, 'ontimeout')
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

var responseType, context;

for (var i=0; i < response_types.length; i++) {
  responseType = response_types[i];

  context = {
    'request':      (i + 1),
    'responseType': ((responseType === undefined) ? null : responseType)
  };

  GM_xmlhttpRequest(Object.assign({}, generic_details, {context, responseType}));
}
