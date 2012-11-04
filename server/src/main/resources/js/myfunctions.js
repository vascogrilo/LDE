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
	//alert('entrou1');
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
		//alert('entrou');
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
	
	//console.log('Key Down triggered.');
	
	// KEY CODES FOR CTRL/CMD BUTTON ON DIFFERENT BROWSERS
	if(event.which == 17 || event.which == 224 || event.which == 91 || event.which == 93) 
		isCtrl=true;

	// KEY CODE FOR ENTER
    if(event.which == 13) {
		if(isCtrl == true) {
			$('#form').submit();
			return false;
		}
    }
}

var keyUpHandler = function(event) {
	
	//console.log('Key Up triggered.');
	
    if(event.which == 17 || event.which == 224 || event.which == 91 || event.which == 93)
		isCtrl=false;
}


var onClickLabelHandler = function(event) {
	
	//$('#).slideUp();
}
