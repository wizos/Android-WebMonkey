// ==UserScript==
// @name         test: GM_download (20 MB, network URL)
// @namespace    WebViewWM
// @match        *://*/*
// @grant        GM_download
// @run-at       document-end
// ==/UserScript==

// https://test-videos.co.uk/bigbuckbunny/mp4-h264

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
  var url  = 'https://test-videos.co.uk/vids/bigbuckbunny/mp4/h264/1080/Big_Buck_Bunny_1080_10s_20MB.mp4'
  var name = 'BigBuckBunny.mp4'

  init_dom('downloading: ' + url.substring(0, 100))

  var response = GM_download({
    url,
    name,
    synchronous: true
  })

  display_response(response)
}

run_download_test()
