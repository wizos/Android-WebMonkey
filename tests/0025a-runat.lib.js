var is_dom_init = false
var pre_dom_init_messages = []

var init_dom = function() {
  if (is_dom_init) return
  if (document.readyState === 'loading') return
  is_dom_init = true

  clean_dom()

  while (pre_dom_init_messages.length) {
    append_to_dom(
      pre_dom_init_messages.shift()
    )
  }
}

var clean_dom = function() {
  while(document.body.childNodes.length) {
    document.body.removeChild(document.body.childNodes[0]);
  }
}

var append_to_dom = function(text) {
  if (!is_dom_init) {
    pre_dom_init_messages.push(text)
    return
  }

  var div = document.createElement('div');
  var pre = document.createElement('pre');
  var hr  = document.createElement('hr');

  pre.innerText = text;
  div.appendChild(pre);
  document.body.appendChild(div);
  document.body.appendChild(hr);
}

var get_url = function() {
  return 'URL: ' + ((typeof GM_getUrl === 'function')
    ? GM_getUrl()
    : unsafeWindow.location.href
  )
}

var get_readystate = function() {
  return 'readyState: ' + document.readyState
}

var timer = Date.now()

var get_elapsed_time_ms = function() {
  return Date.now() - timer
}

var get_elapsed_time = function() {
  return 'elapsed time (ms): ' + get_elapsed_time_ms()
}

var append_elapsed_time_to_dom = function() {
  init_dom()

  append_to_dom(
    [
      get_url(),
      get_readystate(),
      get_elapsed_time()
    ].join("\n")
  )
}

var total_network_delay = 0

var add_script_with_network_delay = function(event_to_delay, delay_secs) {
  if (!delay_secs) delay_secs = total_network_delay + 2
  total_network_delay += delay_secs

  // https://requestly.com/blog/adding-delay-to-network-requests/
  var url_to_be_delayed      = 'https://cdn.jsdelivr.net/gh/warren-bank/js-url/es5-browser/jsURL.js' /* cache bust */ + '?event=' + event_to_delay
  var url_with_network_delay = 'https://app.requestly.io/delay/' + (delay_secs * 1000) + '/' + url_to_be_delayed

  var attr = ''
  switch(event_to_delay) {
    case 'load':
      attr = 'async'
      break
  }

  if (document.readyState === 'loading') {
    document.write('<' + 'script ' + attr + ' src="' + url_with_network_delay + '">' + '<' + '/script>')
  }
  else {
    var scr = document.createElement('script')
    scr.setAttribute('src', url_with_network_delay)
    if (attr)
      scr.setAttribute(attr, 'true')
    document.body.appendChild(scr)
  }
}

if (document.readyState === 'loading')
  document.open()

append_elapsed_time_to_dom()

switch(document.readyState) {
  case 'loading':
    document.addEventListener('DOMContentLoaded', append_elapsed_time_to_dom)
    add_script_with_network_delay('DOMContentLoaded')

  case 'interactive':
    unsafeWindow.addEventListener('load', append_elapsed_time_to_dom)
    add_script_with_network_delay('load')

  case 'complete':
  default:
    break
}

if (document.readyState === 'loading')
  document.close()
