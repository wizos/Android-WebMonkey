// ==UserScript==
// @name         test: GM_download (300 MB, network URL)
// @namespace    WebViewWM
// @match        *://*
// @run-at       document-end
// ==/UserScript==

// https://testfile.org/
// https://testfile.org/all-sizes/

var display_response = function(response) {
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
}

var run_download_test = function() {
  var url  = 'https://link.testfile.org/300MB'
  var name = 'testfile.zip'

  init_dom('downloading: ' + url.substring(0, 100))

  var response = GM_download({
    url,
    name,
    synchronous: true
  })

  display_response(response)
}

run_download_test()
