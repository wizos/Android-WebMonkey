// ==UserScript==
// @name         test: @resource
// @description  test: import PNG binary image resource, read its content (as a base64-encoded data: URI) with GM_getResourceURL, and print its hash
// @namespace    WebViewWM
// @match        https://*
// @run-at       document-end
// @resource     imgData https://github.com/warren-bank/Android-WebMonkey/raw/v04.02.02/android-studio-project/WebMonkey/src/main/res/drawable/launcher.png
// @grant        GM_getResourceURL
// ==/UserScript==

// related docs:
//   https://www.tampermonkey.net/documentation.php#meta:resource
//   https://www.tampermonkey.net/documentation.php#api:GM_getResourceURL

/**
 * @function Use hash function algorithm to generate a digest as an Uint8Array.
 * @param {"SHA-1"|"SHA-256"|"SHA-384"|"SHA-512"} algorithm
 * @param Uint8Array msgUint8
 * @returns Promise digestUint8
 *
 * https://developer.mozilla.org/en-US/docs/Web/API/SubtleCrypto/digest
 */
var getDigestAsUint8Array = async function(algorithm, msgUint8) {
  var hashBuffer = await unsafeWindow.crypto.subtle.digest(algorithm, msgUint8)
  return new Uint8Array(hashBuffer)
}

/**
 * @function Use hash function algorithm to generate a digest as a hex string.
 * @param {"SHA-1"|"SHA-256"|"SHA-384"|"SHA-512"} algorithm
 * @param Uint8Array msgUint8
 * @returns Promise hexString
 *
 * https://developer.mozilla.org/en-US/docs/Web/API/SubtleCrypto/digest#converting_a_digest_to_a_hex_string
 */
var getDigestAsHex = async function(algorithm, msgUint8) {
  var digestUint8 = await getDigestAsUint8Array(algorithm, msgUint8)
  var hashArray = Array.from(digestUint8)

  // convert bytes to hex string
  return hashArray.map(function(b) {
    return b.toString(16).padStart(2, '0')
  }).join('');
}

/**
 * @function Use hash function algorithm to generate a digest as a base64 string.
 * @param {"SHA-1"|"SHA-256"|"SHA-384"|"SHA-512"} algorithm
 * @param Uint8Array msgUint8
 * @returns Promise base64String
 */
var getDigestAsBase64 = async function(algorithm, msgUint8) {
  var digestUint8 = await getDigestAsUint8Array(algorithm, msgUint8)
  var hashArray = Array.from(digestUint8)

  // convert bytes to base64 string
  return btoa(
    String.fromCharCode.apply(null, hashArray)
  )
}

var run_test = async function() {
  try {
    var imgData  = GM_getResourceURL('imgData') // base64-encoded data: URI
    var imgUint8 = await fetch(imgData).then(res => res.arrayBuffer()).then(arrayBuffer => new Uint8Array(arrayBuffer))

    // cleanup
    imgData = null

    // generate SHA-1
    var imgSha1Hex = await getDigestAsHex(   'SHA-1', imgUint8)
    var imgSha1B64 = await getDigestAsBase64('SHA-1', imgUint8)

    // cleanup
    imgUint8 = null

    unsafeWindow.alert('SHA-1 (hex): ' + imgSha1Hex);
    unsafeWindow.alert('SHA-1 (b64): ' + imgSha1B64);
  }
  catch(e) {
    unsafeWindow.alert('Error: ' + (e.message));
  }
};

run_test();
