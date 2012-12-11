/**
 * MYSCRIPT.JS
 * 
 * This file includes all function calling.
 * 
 */

$(document).ready(function() {
	
	/**
	 * FUNCTION CALLING
	 */
	textareaApplyPlaceHolder();
	
	$('#code').autosize();
	
	
	/**
	 * HANDLERS FOR EVENTS
	 */
	$('#code').keydown(keyDownHandler);
	$('#code').keyup(keyUpHandler);
	
	requestConversionsFile();
});

