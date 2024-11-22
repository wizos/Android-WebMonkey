// ==UserScript==
// @name         test: GM_toastShort
// @namespace    WebViewWM
// @match        *://*/*
// @grant        GM_toastShort
// @run-at       document-start
// ==/UserScript==

GM_toastShort("Hello from " + unsafeWindow.location.href);
