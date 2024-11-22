// ==UserScript==
// @name         test: GM_toastLong
// @namespace    WebViewWM
// @match        *://*/*
// @grant        GM_toastLong
// @run-at       document-start
// ==/UserScript==

GM_toastLong("Hello from " + unsafeWindow.location.href);
