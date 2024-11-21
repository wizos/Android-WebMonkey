// ==UserScript==
// @name         test: GM_download (200 MB, network URL)
// @namespace    WebViewWM
// @match        *://*
// @run-at       document-end
// ==/UserScript==

// https://testfile.org/
// https://testfile.org/all-sizes/

var progress_meter;

var progress_event_handler = function(response) {
  progress_meter.value = response.loaded / response.total;
};

var load_event_handler = function(response) {
  var h4 = document.createElement('h4');
  h4.innerText = 'download complete!';
  document.body.appendChild(h4);

  var hr = document.createElement('hr');
  document.body.appendChild(hr);

  var div = document.createElement('div');
  var pre = document.createElement('pre');
  pre.innerText = JSON.stringify(response, null, 2);
  div.appendChild(pre);
  document.body.appendChild(div);
};

var init_dom = function(title) {
  while(document.body.childNodes.length) {
    document.body.removeChild(document.body.childNodes[0]);
  }

  var h4 = document.createElement('h4');
  h4.innerText = title;
  document.body.appendChild(h4);

  progress_meter = document.createElement('progress');
  progress_meter.max   = 1.0;
  progress_meter.value = 0.0;
  document.body.appendChild(progress_meter);
}

var run_download_test = function() {
  var url  = 'https://link.testfile.org/PDF200MB'
  var name = 'testfile.pdf'

  init_dom('downloading: ' + url.substring(0, 100))

  GM_download({
    url,
    name,
    onprogress: progress_event_handler,
    onload: load_event_handler
  })
}

run_download_test()
