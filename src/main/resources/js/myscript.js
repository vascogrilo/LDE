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
	
	requestConversionsOp("conv");
	
	//RE-INSERT EVERY INSTRUCTION PREVIOUSLY RECEIVED
	//SO THEY CAN SURVIVE REFRESHING THE WINDOW
	for(i=0;i<Session.get("counter");i++)
		$('#outputBody').append(Session.get("i"+i.toString()));
});

