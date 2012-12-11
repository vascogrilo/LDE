/**
 * MYFUNCTIONS.JS
 * 
 * This file includes all 'user'-defined functions (me), in order to
 * achieve certain goals.
 * 
 */
 
 
/**
 * elementSupportsAttribute
 * 
 * This function receives an element and an attribute and
 * tests if the element supports the specified attribute.
 * 
 * This exists mainly because of new HTML5 attributes which sometimes
 * are not supported in some browsers.
 */
function elementSupportsAttribute(element, attribute) {
  var test = document.createElement(element);
  if (attribute in test) {
    return true;
  } else {
    return false;
  }
};

/**
 * textareaApplyPlaceHolder
 * 
 * This function tests if the textarea for code input supports placeholder attribute.
 * Otherwise it will mimic the same behaviour using JQuery.
 * 
 */
function textareaApplyPlaceHolder() {
	if (!elementSupportsAttribute('textarea', 'placeholder')) {
	// Fallback for browsers that don't support HTML5 placeholder attribute
	$('#code')
		.data('Type your instruction here...', $('#code').text())
		.css('color', '#999')
		.focus(function() {
			var $el = $(this);
			if (this.value == $el.data('Type your instruction here...')) {
				this.value = '';
			}
		})
		.blur(function() {
			if (this.value == '') {
				this.value = $(this).data('Type your instruction here...');
			}
		});
	}
	else {
		// Browser does support HTML5 placeholder attribute, so use it.
		$('#code').attr('placeholder', 'Type your instruction here...');
	}
}

/**
 * EVENT HANDLER FOR PRESSING DOWN KEYS AND RELEASING FROM PRESSING
 * 
 * Later on we assign this handler to the keydown and keyup event of
 * the textarea's code input element.
 */
var isCtrl = false;
var keyDownHandler = function(event) {
	
	// KEY CODES FOR CTRL/CMD BUTTON ON DIFFERENT BROWSERS
	if(event.which == 17 || event.which == 224 || event.which == 91 || event.which == 93) 
		isCtrl=true;

	// KEY CODE FOR ENTER
    if(event.which == 13) {
		if(checkIfCanEvaluate($.trim($('#code').val()))){
			addInstructionHistory($.trim($('#code').val()));
			console.log(instructions);
			requestEvaluation($('#code').val());
			return false;
		}
		else return true;
    }
    
    // KEY CODE FOR UP ARROW
    if(event.which == 38) {
		if(instruction_counter > 0){
			instruction_counter--;
			$('#code').val(instructions[instruction_counter]);
			$('#code').keydown();
			$('#code').autosize();
		}
		return false;
	}
	
	// KEY CODE FOR DOWN ARROW
	if(event.which == 40) {
		if(instruction_counter < instructions.length){
			instruction_counter++;
			$('#code').val(instructions[instruction_counter]);
			$('#code').keydown();
			$('#code').autosize();
		}
		return false;
	}
}

var keyUpHandler = function(event) {
    if(event.which == 17 || event.which == 224 || event.which == 91 || event.which == 93)
		isCtrl=false;
}

var checkIfCanEvaluate = function(code) {
	if(code == "") return false;
	var can = false;
	if( ((($.trim( $('#code').val() )).match(/{/g)||[]).length == (($.trim( $('#code').val() )).match(/}/g)||[]).length) &&
		 ((($.trim( $('#code').val() )).match(/\(/g)||[]).length == (($.trim( $('#code').val() )).match(/\)/g)||[]).length) )
		can = true;
	return can;
}

/**
 * FUNCTIONS FOR STORING AND GETTING PREVIOUS INSTRUCTIONS
 * STORED IN MEMORY
 * 
 */
var instructions = new Array();
var instruction_counter = 0;
	
var addInstructionHistory = function(code) {
	instructions[instructions.length] = code;
	instruction_counter = instructions.length;
}

/**
 * FUNCTIONS FOR STORING ALL IDS RECEIVES FROM RESPONSES
 * 
 * WHEN AN EXISTING ID COMES ALONG, WE MUST UPDATE THE PREVIOUS DOM ELEMENT
 * 
 */
var identifiers = new Array();

/**
 * FUNCTIONS FOR SENDING CODE TO BE EVALUATED SERVERSIDE
 * 
 * requestEvaluation sends a code for evaluating and receives a new full response. It then appends it to the output body.
 * 
 * requestConversion sends a code for evaluating and receives a partial response to be appended to an existing result on the body.
 * 
 */

var requestEvaluation = function(code_str) {
	$.ajax({
		type: 'POST',
		//url: 'http://visual-scala.herokuapp.com/repl',
		url: 'http://localhost:8080/repl',
		dataType: 'html',
		data: { 
			code: code_str
		},
		beforeSend: function(xhr,opts) {
			$('#buttonSubmit').hide();
			$('#loaderG').show();
		},
		success: function(data) {
			$('#loaderG').hide();
			$('#buttonSubmit').show();
			
			$('#code').val('');
			$('#outputBody').append(data);
		},
		error: function( jqXHR, exception ){
			
			alertError(jqXHR,exception);
            
            $('#loaderG').hide();
			$('#buttonSubmit').show();
		}
		
	});
}

var requestConversion = function(div_id,instr) {
	$.ajax({
		type: 'POST',
		//url: 'http://visual-scala.herokuapp.com/repl',
		url: 'http://localhost:8080/repl',
		dataType: 'html',
		data: { 
			code: instr + " :!: partial"
		},
		beforeSend: function(xhr,opts) {
			$('#buttonSubmit').hide();
			$('#loaderG').show();
		},
		success: function(data) {
			
			$('#loaderG').hide();
			$('#buttonSubmit').show();
			
			if(data===""){
				console.log("Got empty data from conversion. Something went wrong.");
			}
			else {
				$('#well_' + div_id).empty();
				$('#well_' + div_id).append(data);
			}
		},
		error: function( jqXHR, exception ){
			
			alertError(jqXHR,exception);
            
            $('#loaderG').hide();
			$('#buttonSubmit').show();
		}
	});
}

function alertError(jqXHR,exception) {
	if (jqXHR.status === 0) {
		alert('Error.\nNot connected or Server is down!');
	} else if (jqXHR.status == 404) {
		alert('Error.\nRequested page not found. [404]');
	} else if (jqXHR.status == 500) {
		alert('Error.\nInternal Server Error [500].');
	} else if (exception === 'parsererror') {
		alert('Error.\nRequested JSON parse failed.');
	} else if (exception === 'timeout') {
		alert('Error.\nTime out error.');
	} else if (exception === 'abort') {
		alert('Error.\nAjax request aborted.');
	} else {
		alert('Error.\n' + jqXHR.responseText);
	}
}






/**
 * 
 * 
 * TOOLMAN
 * 
 * 
 */
var ESCAPE = 27;
var ENTER = 13;
var TAB = 9;
var coordinates = ToolMan.coordinates();
var dragsort = ToolMan.dragsort();

function join(name, isDoubleClick) {
	var view = document.getElementById(name + 'View');
	view.editor = document.getElementById(name + 'Edit');

	var showEditor = function(event) {
		event = fixEvent(event);

		var view = this;
		var editor = view.editor;

		if (!editor) return true;

		if (editor.currentView != null) {
			editor.blur();
		}
		editor.currentView = view;

		var topLeft = coordinates.topLeftOffset(view);
		topLeft.reposition(editor);
		if (editor.nodeName == 'TEXTAREA') {
			editor.style['width'] = view.offsetWidth + 'px';
			editor.style['height'] = view.offsetHeight + 'px';
		}
		editor.value = view.innerHTML;
		editor.style['visibility'] = 'visible';
		view.style['visibility'] = 'hidden';
		editor.focus();
		return false;
	}

	if (isDoubleClick) {
		view.ondblclick = showEditor;
	} else {
		view.onclick = showEditor;
	}

	view.editor.onblur = function(event) {
		event = fixEvent(event);

		var editor = event.target;
		var view = editor.currentView;

		if (!editor.abandonChanges) view.innerHTML = editor.value;
		editor.abandonChanges = false;
		editor.style['visibility'] = 'hidden';
		editor.value = '';
		view.style['visibility'] = 'visible';
		editor.currentView = null;

		return true;
	}
	
	view.editor.onkeydown = function(event) {
		event = fixEvent(event);
		
		var editor = event.target;
		if (event.keyCode == TAB) {
			editor.blur();
			return false;
		}
	}

	view.editor.onkeyup = function(event) {
		event = fixEvent(event);

		var editor = event.target;
		if (event.keyCode == ESCAPE) {
			editor.abandonChanges = true;
			editor.blur();
			return false;
		} else if (event.keyCode == TAB) {
			return false;
		} else {
			return true;
		}
	}

	function fixEvent(event) {
		if (!event) event = window.event;
		if (event.target) {
			if (event.target.nodeType == 3) event.target = event.target.parentNode;
		} else if (event.srcElement) {
			event.target = event.srcElement;
		}

		return event;
	}
}
