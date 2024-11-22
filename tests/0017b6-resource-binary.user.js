// ==UserScript==
// @name         test: @resource
// @description  test: import a ~20MB binary file resource, read its content (as a base64-encoded data: URI) with GM_getResourceURL, and check its integrity (hash)
// @namespace    WebViewWM
// @match        https://*/*
// @resource     npmData https://registry.npmjs.org/@ffmpeg/core/-/core-0.12.6.tgz
// @grant        GM_getResourceURL
// @run-at       document-end
// ==/UserScript==

// related docs:
//   https://www.tampermonkey.net/documentation.php#meta:resource
//   https://www.tampermonkey.net/documentation.php#api:GM_getResourceURL
// sample data:
//   https://registry.npmjs.com/-/v1/search?text=@ffmpeg/core&size=1
//   https://registry.npmjs.com/@ffmpeg/core/0.12.6
//   https://registry.npmjs.org/@ffmpeg/core/-/core-0.12.6.tgz
//     size:    19.4 MB
//     SHA-1   (hex): 9a61866fdd662118bcf419b96db76d5fa3c6c270
//     SHA-512 (b64): PrjWBTfGn2WVn9T7wGnzfFwChbqWeZc7tM9vvJZVRadYFUDakfzy7W0LpYC0cvvK0xT82qlBsk38lQhJ/Hps5A==

var constants = {
  npmSha1Hex:   '9a61866fdd662118bcf419b96db76d5fa3c6c270',
  npmSha512B64: 'PrjWBTfGn2WVn9T7wGnzfFwChbqWeZc7tM9vvJZVRadYFUDakfzy7W0LpYC0cvvK0xT82qlBsk38lQhJ/Hps5A=='
}

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

var assert = function(algorithm, actual, expected) {
  if (actual !== expected) {
    unsafeWindow.alert('Error: ' + algorithm + ' hash of binary resource is not equal to its expected value.' + "\n\nCalculated value:\n" + actual);
    console.error({actual, expected, algorithm})
  }
  else {
    unsafeWindow.alert(algorithm + ' hash is correct');
  }
}

var run_test = async function() {
  try {
    var npmData  = GM_getResourceURL('npmData') // base64-encoded data: URI
    var npmUint8 = await fetch(npmData).then(res => res.arrayBuffer()).then(arrayBuffer => new Uint8Array(arrayBuffer))

    // cleanup
    npmData = null

    // generate SHA-1 (hex)
    var npmSha1Hex = await getDigestAsHex('SHA-1', npmUint8)

    // generate SHA-512 (base64)
    var npmSha512B64 = await getDigestAsBase64('SHA-512', npmUint8)

    // cleanup
    npmUint8 = null

    assert('SHA-1',   npmSha1Hex,   constants.npmSha1Hex)
    assert('SHA-512', npmSha512B64, constants.npmSha512B64)
  }
  catch(e) {
    unsafeWindow.alert('Error: ' + (e.message));
  }
};

run_test();
