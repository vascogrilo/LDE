(function() {
  CodeMirror.simpleHint = function(editor, getHints, givenOptions) {

    var options = {}, defaults = CodeMirror.simpleHint.defaults;
    for (var opt in defaults)
      if (defaults.hasOwnProperty(opt))
        options[opt] = (givenOptions && givenOptions.hasOwnProperty(opt) ? givenOptions : defaults)[opt];
    
    function collectHints(previousToken) {
      if (editor.somethingSelected()) return;

      var tempToken = editor.getTokenAt(editor.getCursor());

      if (options.closeOnTokenChange && previousToken != null &&
          (tempToken.start != previousToken.start || tempToken.className != previousToken.className)) {
        return;
      }

      var result = getHints(editor);
      if (!result || !result.list.length) return;
      var completions = result.list;
      function insert(str) {
        editor.replaceRange(str, result.from, result.to);
      }
      if (completions.length == 1) {insert(completions[0]); return true;}

      var complete = document.createElement("div");
      complete.className = "CodeMirror-completions";
      var sel = complete.appendChild(document.createElement("select"));

      if (!window.opera) sel.multiple = true;
      for (var i = 0; i < completions.length; ++i) {
        var opt = sel.appendChild(document.createElement("option"));
        opt.appendChild(document.createTextNode(completions[i]));
      }
      sel.firstChild.selected = true;
      sel.size = Math.min(10, completions.length);
      var pos = editor.cursorCoords();
      complete.style.left = pos.x + "px";
      complete.style.top = pos.yBot + "px";
      document.body.appendChild(complete);
      var winW = window.innerWidth || Math.max(document.body.offsetWidth, document.documentElement.offsetWidth);
      if(winW - pos.x < sel.clientWidth)
        complete.style.left = (pos.x - sel.clientWidth) + "px";
      if (completions.length <= 10)
        complete.style.width = (sel.clientWidth - 1) + "px";

      var done = false;
      function close() {
        if (done) return;
        done = true;
        complete.parentNode.removeChild(complete);
      }
      function pick() {
        insert(completions[sel.selectedIndex]);
        close();
        setTimeout(function(){editor.focus();}, 50);
      }
      CodeMirror.connect(sel, "blur", close);
      CodeMirror.connect(sel, "keydown", function(event) {
        var code = event.keyCode;
        if (code == 13) {CodeMirror.e_stop(event); pick();}
        else if (code == 27) {CodeMirror.e_stop(event); close(); editor.focus();}
        else if (code != 38 && code != 40 && code != 33 && code != 34) {
          close(); editor.focus();
          editor.triggerOnKeyDown(event);
          if (!options.closeOnBackspace || code != 8) {
            setTimeout(function(){collectHints(tempToken);}, 50);
          }
        }
      });
      CodeMirror.connect(sel, "dblclick", pick);

      sel.focus();
      if (window.opera) setTimeout(function(){if (!done) sel.focus();}, 100);
      return true;
    }
    return collectHints();
  };
  CodeMirror.simpleHint.defaults = {
    closeOnBackspace: true,
    closeOnTokenChange: false
  };
})();
