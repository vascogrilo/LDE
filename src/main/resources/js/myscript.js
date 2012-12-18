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
	
	generateConversionsArea();
	
	requestConversionsOp(1,"IterableConversions");
	requestConversionsOp(1,"MapConversions");
	requestConversionsOp(1,"StringConversions");
	requestConversionsOp(1,"XMLConversions");
	requestConversionsOp(1,"ClassConversions");
	requestConversionsOp(1,"Conversions");
	
	//RE-INSERT EVERY INSTRUCTION PREVIOUSLY RECEIVED
	//SO THEY CAN SURVIVE REFRESHING THE WINDOW

	for(i=0;i<Session.get("counter");i++) {
		var tempDOM = Session.get("i" + i.toString());
		$('#outputBody').append(tempDOM);
	}
});

