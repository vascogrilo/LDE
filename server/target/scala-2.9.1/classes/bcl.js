// $Id$

function bcl_focus()
{
  var bclline = document.getElementById( "bclline" );
  bclline.focus();
}

function arguments_to_array( args )
{
  var arr = new Array();
  for (var i=0; i<args.length; ++i) {
    arr[i] = args[i];
  }
  return arr;
}

function bcl_go( e )
{
  var cmd = e.bclline.value;
  bcl_run( cmd );
  e.bclline.focus();
  return false;
}

function bcl_parse( cmd )
{
  return cmd.split( /\s+/ );
}

function bcl_remove_blank_words( words )
{
  // Remove leading and trailing blank words.
  while (words.length>0 && words[0]==="") {
    words = words.slice( 1 );
  }
  while (words.length>0 && words[words.length-1]==="") {
    words = words.slice( 0, words.length-1 );
  }
  return words;
}

function bcl_run( cmd )
{
  words = bcl_parse( cmd );
  words = bcl_remove_blank_words( words );

  var last_cmd_word = null;

  for (var i=0; i<words.length; ++i) {
    var fun_name = words.slice( 0, i+1 ).join( "_" );
    if (window[fun_name] == undefined) {
      break;
    } else {
      last_cmd_word = i;
    }
  }

  if (last_cmd_word===null || words.length==0) {
    alert( "No such command \""+words[0]+"\"" );
    return;
  }

  var fun_name = words.slice( 0, last_cmd_word+1 ).join( "_" );
  var fun = window[fun_name];
  var args = words.slice( last_cmd_word+1 );
  fun.apply( this, args );
}

function bcl_jump_cgi( url, kvs )
{
  var url = url+"?";
  for (var k in kvs) {
    var v = kvs[k];
    url += k+"="+escape( v );
  }
  location = url;
}
