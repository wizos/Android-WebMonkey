// ==UserScript==
// @name         test: @resource
// @description  test: import a ~32MB binary file resource, read its content (as a base64-encoded data: URI) with GM_getResourceURL, and print its hash
// @namespace    WebViewWM
// @match        https://*/*
// @resource     wasmData https://cdn.jsdelivr.net/npm/@ffmpeg/core@0.12.6/dist/umd/ffmpeg-core.wasm
// @grant        GM_getResourceURL
// @run-at       document-end
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
    var wasmData  = GM_getResourceURL('wasmData') // base64-encoded data: URI
    var wasmUint8 = await fetch(wasmData).then(res => res.arrayBuffer()).then(arrayBuffer => new Uint8Array(arrayBuffer))

    // cleanup
    wasmData = null

    // generate SHA-1
    var wasmSha1Hex = await getDigestAsHex(   'SHA-1', wasmUint8)
    var wasmSha1B64 = await getDigestAsBase64('SHA-1', wasmUint8)

    // cleanup
    wasmUint8 = null

    unsafeWindow.alert('SHA-1 (hex): ' + wasmSha1Hex);
    unsafeWindow.alert('SHA-1 (b64): ' + wasmSha1B64);
  }
  catch(e) {
    unsafeWindow.alert('Error: ' + (e.message));
  }
};

run_test();
