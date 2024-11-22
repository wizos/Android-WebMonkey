// ==UserScript==
// @name         test: regex @include pattern
// @namespace    WebViewWM
// @include      /^https?:.*$/
// @grant        GM_toastLong
// @run-at       document-start
// ==/UserScript==

GM_toastLong("Hello from " + unsafeWindow.location.href);
