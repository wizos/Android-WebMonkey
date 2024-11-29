// ==UserScript==
// @name         test: with closure, with sandbox
// @namespace    WebViewWM
// @match        *://*/*
// @grant        unsafeWindow
// @run-at       document-end
// ==/UserScript==

"use strict";

var clean_dom = function() {
  while(document.body.childNodes.length) {
    document.body.removeChild(document.body.childNodes[0]);
  }
}

var append_to_dom = function(text) {
  var div = document.createElement('div');
  var pre = document.createElement('pre');
  var hr  = document.createElement('hr');

  pre.innerText = text;
  div.appendChild(pre);
  document.body.appendChild(div);
  document.body.appendChild(hr);
}

var append_table_to_dom = function() {
  var table = document.createElement('table');

  table.setAttribute('border', '1px');
  table.setAttribute('style', 'white-space: nowrap;');

  document.body.appendChild(table);
  return table
}

var append_table_row = function(table, cols, is_heading) {
  var tr = document.createElement('tr');
  var col, td;

  for (var i=0; i < cols.length; i++) {
    col = cols[i];
    td = document.createElement(is_heading? 'th' : 'td');
    td.textContent = col;
    tr.appendChild(td);
  }

  table.appendChild(tr);
}

var append_table_heading = function(table) {
  var cols = ['', 'foo', 'unsafeWindow.foo', 'window.foo', 'self.foo', 'this.foo', 'globalThis.foo'];
  append_table_row(table, cols, true);
}

var append_table_row_for_variable = function(table, what, variable_name, typeof_variable) {
  var cols = [what, typeof_variable, typeof unsafeWindow[variable_name], typeof window[variable_name], typeof self[variable_name], typeof this[variable_name], typeof globalThis[variable_name]];
  append_table_row(table, cols, false);
}

clean_dom();
append_to_dom('with closure, with sandbox:');

var table = append_table_to_dom();
append_table_heading(table);

var variable_01 = 1;
append_table_row_for_variable.call(this, table, 'var foo = 1', 'variable_01', typeof variable_01);

unsafeWindow.variable_02 = 1;
append_table_row_for_variable.call(this, table, 'unsafeWindow.foo = 1', 'variable_02', typeof variable_02);

window.variable_03 = 1;
append_table_row_for_variable.call(this, table, 'window.foo = 1', 'variable_03', typeof variable_03);

self.variable_04 = 1;
append_table_row_for_variable.call(this, table, 'self.foo = 1', 'variable_04', typeof variable_04);

this.variable_05 = 1;
append_table_row_for_variable.call(this, table, 'this.foo = 1', 'variable_05', typeof variable_05);

globalThis.variable_06 = 1;
append_table_row_for_variable.call(this, table, 'globalThis.foo = 1', 'variable_06', typeof variable_06);
