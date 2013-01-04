/**
 * MYFUNCTIONS.JS
 * 
 * This file includes all 'user'-defined functions (me), in order to
 * achieve certain goals.
 * 
 */
 
/**
 * 
 * CONVERSIONS
 * 
 */
var conversions = ["ClassConversions","IterableConversions","MapConversions","StringConversions","XMLConversions","Conversions"];

var generateConversionsArea = function() {
	var element;
	for(var i=0;i<conversions.length;i++){
		element = "<div class='conversions_item'>" +
					"<span class='label' data-toggle='collapse' data-target='#conversions_" + conversions[i] + "'>" +
						conversions[i] + "</span>" +
					"<div class='conversionsOps'>" +
					"<a id='submit" + conversions[i] + "' href='javascript:void(0)' role='button' class='label label-info' onclick='requestConversionsUpdate(\"" + conversions[i].trim() + "\");'>Submit</a>" +
					"<a id='revert" + conversions[i] + "' href='javascript:void(0)' role='button' class='label label-info' style='margin-left: 5px' onclick='requestConversionsOp(0,'reload-" + conversions[i] + "');'>Revert</a>" +
					"</div>" +
					"<div id='conversions_" + conversions[i] + "' class='collapse'>" + 
					"<textarea id='text_" + conversions[i] + "' class='text_conversions'></textarea></div></div>";
		$('.conversions_list').append(element);
	}
}

var generateConversionsRequests = function() {
	for(var i=0;i<conversions.length;i++)
		requestConversionsOp(1,conversions[i]);
}

 
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
var good_responses = 0;

var addInstructionHistory = function(code) {
	instructions[instructions.length] = code;
	instruction_counter = instructions.length;
}



/**
 * getCategory
 * 
 * This method converts a string representing a type to a numeric value representing it's category.
 * The string is received from the user's new conversion type chosen.
 * 
 */
 
var type_conversions = {
	'Iterable':
		[
			{ 'name':'toBinaryTree','desc':'Binary Tree'},
			{ 'name':'toD3BarChart','desc':'Bar Chart'},
			{ 'name':'toHtml','desc':'Paginated List'},
			{ 'name':'toHtmlList','desc':'HTML List'},
			{ 'name':'toString','desc':'Text'}
		],
	'Map':
		[
			{ 'name':'toHtml','desc':'HTML Table'},
			{ 'name':'toPieChart','desc':'Pie Chart'},
			{ 'name':'toString','desc':'Text'}
		],
	'Class':
		[
			{ 'name':'toClass','desc':'Graphic class'},
			{ 'name':'toString','desc':'Text'}
		]
};

var getCategory = function(str) {
	var str_aux = str;
	
	if(str.indexOf("[") > -1)
		str_aux = str.substr(0,str.indexOf("["));
	
	if(str_aux.lastIndexOf(".") > -1)
		str_aux = str_aux.substr(str_aux.lastIndexOf(".")+1,str_aux.length);
		
	if( str == "Iterable" 
		|| str == "List"
		|| str == "Range"
		|| str == "Set" )
		return 1;
		
	if(str == "Map" 
		|| str == "HashMap"
		|| str == "TreeMap")
		return 2;
}

var fromIntToCategory = function(i) {
	switch(i){
			case 1: return 'Iterable';
			case 2: return 'Map';
	}
}

var fillConversionsMenu = function(div_id,category,name,iCounter) {
	
	console.log("div_id: " + div_id);
	console.log("category: " + category);
	console.log("name: " + name);
	console.log("iCounter: " + iCounter);
	
	if(category > 0) {
		$('#' + div_id).append("<span class='dropdown-span' data-dropdown='#dropdown-"+name+"'>View as</span><div id='dropdown-"+name+"' class='dropdown-menu'><ul id='drop_" + name + iCounter + "'></ul></div>");
		for(var i=0;i<type_conversions[fromIntToCategory(category)].length;i++)
			$('#drop_' + name + iCounter).append("<li><a href='javascript:void(0)' onclick='requestConversion(\""+name+"_TEMPORARYID"+iCounter+"\",\""+name+" :!: "+ type_conversions[fromIntToCategory(category)][i].name +"\");'>"+ type_conversions[fromIntToCategory(category)][i].desc + "</a></li>");
	}
};

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
