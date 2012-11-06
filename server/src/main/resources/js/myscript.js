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
	
	$('textarea').autosize();
	
	
	/**
	 * HANDLERS FOR EVENTS
	 */
	$('textarea').keydown(keyDownHandler);
	$('textarea').keyup(keyUpHandler);
	//$('#buttonSubmit').click(onClickEvaluateHandler);
});

