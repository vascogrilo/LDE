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
			Session.set("i"+good_responses,data);
			good_responses++;
			Session.set("counter",good_responses);
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

var createNewConversion = function() {
	var conv = "implicit def conversion" + instructions.length + "(x: " + $('#conversionType').val() + ") = new Object {\n" +
				"def " + $('#conversionName').val() + " = { (" + $('#conversionCode').val() + ").toString }\n" +
				"}";
	$.ajax({
		type: 'POST',
		//url: 'http://visual-scala.herokuapp.com/repl',
		url: 'http://localhost:8080/repl',
		data: { 
			code: conv + " :!: conversion"
		},
		beforeSend: function(xhr,opts) {
			$('#buttonSubmit').hide();
			$('#loaderG').show();
		},
		success: function(data) {
			
			$('#loaderG').hide();
			$('#buttonSubmit').show();
			
			if(data.toString() == "SUCCESS"){
				//type_conversions.convs
				$('#conversionType').val("");
				$('#conversionName').val("");
				$('#conversionDesc').val("");
				$('#conversionCode').val("");
				$('#closeModal').click();
				alert("Success!");
			}
			else alert("Your conversion was malformed. Please review.");
		},
		error: function( jqXHR, exception ){
			
			alertError(jqXHR,exception);
            
            $('#loaderG').hide();
			$('#buttonSubmit').show();
		}
	});
}


function requestConversionsUpdate(id) {
	
	console.log($('#text_' + id).val());
	
	$.ajax({
		type: 'POST',
		//url: 'http://visual-scala.herokuapp.com/repl',
		url: 'http://localhost:8080/repl',
		data: { 
			code: $('#text_' + id).val().toString()
		},
		beforeSend: function(xhr,opts) {
			$('#buttonSubmit').hide();
			$('#loaderG').show();
		},
		success: function(data) {
			
			$('#loaderG').hide();
			$('#buttonSubmit').show();
			
			console.log("Success: " + data);
		},
		error: function( jqXHR, exception ){
			
			alertError(jqXHR,exception);
            
            $('#loaderG').hide();
			$('#buttonSubmit').show();
		}
	});
}


function requestConversionsOp(op,sufix) {
	$.ajax({
		type: 'POST',
		//url: 'http://visual-scala.herokuapp.com/repl',
		url: 'http://localhost:8080/repl',
		dataType: 'html',
		data: { 
			code: "conversions :!: " + sufix
		},
		beforeSend: function(xhr,opts) {
			$('#buttonSubmit').hide();
			$('#loaderG').show();
		},
		success: function(data) {
			
			$('#loaderG').hide();
			$('#buttonSubmit').show();
			
			console.log(data);
			
			if(data===""){
				console.log("Got empty data from conversions file. Something went wrong.");
			}
			else {
				if(op == 0) { //RELOAD
					if(data==="true")
						alert(sufix.substr(sufix.indexOf('-')+1,sufix.length) + " was reverted to the original content.");
					else alert("Something went wrong.")
				}
				else { //REQUEST
					$('#text_' + sufix).val(data);
				}
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
