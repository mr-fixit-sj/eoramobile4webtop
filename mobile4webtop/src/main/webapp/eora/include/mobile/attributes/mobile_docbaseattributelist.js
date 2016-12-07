/*
 * 
 * @author S.Jonckheere
 * @since 1.0.0
 * 
 */


//Insert data attributes to elements here before page is created
$(document).on("pagebeforecreate", function(event,data){

	//Enable clear button on all text fields
	$("input[type='text']").attr("data-clear-btn", 'true');

	//Find all date input fields
	$.each($("span.dateTime"), function() {
		//For each date file ( = input[text] for date and input[text] for time
		
	    //add custom clear button into date field 
	    $(this).prepend("<a href='#' class='clearDateButton' data-role='button' data-icon='delete' data-iconpos='notext'>Delete</a>");

	    //Find date field and insert data roles for datepicker widget
		var dateInput = $(this).find("input").first();
		$(dateInput).attr('data-role','date');
		$(dateInput).attr('data-inline','false');
		
		//Disable input[text] clear button since we add a custom clear button
		$(dateInput).attr("data-clear-btn", 'false');
		
		//Add extra dummy class for identification of element
		$(dateInput).addClass("dateinputfield");

		//Find time field and disable input[text] clear button since we add custom clear button
		var timeInput = $(this).find("input").last();
		$(timeInput).addClass("timeinputfield");
		$(timeInput).attr("data-clear-btn", 'false');
		
		//Now remove calendar image element rendered by Document TAG
		$(this).find("img").remove();
	});
});

//Jquery mobile (widgets) are initialised 
$(document).on("pagecreate ", function(event,data) {
	
	$('input[type=text]').textinput({ preventFocusZoom: true });
	
	//Remove <div> and <ul> temporary tags to correct html
	$("#removeme").remove();
	
	//If no validator message is present clear text
	var validator = $("#validator")
	var validatorMessageElem = $(validator).children("span.validatorMessageStyle")
	if ( validatorMessageElem.length === 0 ){
		//Clear validator text
		$("#validator").empty();
	} else {
		//Remove Breaks
		$(validatorMessageElem).children("br").remove();
	}

	//Move all TD elements generated for docbaseattribute values 
	//directly under the root value <P> tag
	//After that remove table(s)
	
	$.each($("#categorySet").find($("div.props-value table td")), function() {
		var tdElement = this;
		
		//Find required field asterisk and move it before the label name
		var requiredFieldAsteriskSpan = $(tdElement).find( "span" );
		if ( $(requiredFieldAsteriskSpan).hasClass( "requiredFieldAsterisk" )){
			
			var liElement = $(tdElement).closest("li");
			$(liElement).find("div.props-label").prepend(requiredFieldAsteriskSpan);
		}

		//Find parent table tag of td element
		var tableElement = $(tdElement).closest("table");
		
		//Now select the property value div
		var propsValueDivElement = $(tableElement).parent();
		
		//Add all child elements of TD to P element
		$(propsValueDivElement).append($(tdElement).children());
	});

	//Set datepicker default value
	$.datepicker.setDefaults( $.datepicker.regional[ "" ] );
	//Now set it to user locale
	$.datepicker.setDefaults( $.datepicker.regional[ userLocale ] );
	//Set date format (depends on locale) 
	$(".dateinputfield").datepicker( "option", "dateFormat", datePattern );
	
	//Register event hook when user selects date, so time input is shown
	$(".dateinputfield").datepicker( "option", "onSelect", function (){
		//First find parent props-value div
		var propValElem = $(this).closest( $("div.props-value") );
		//Now searhc in div for timeinput field
		var timeInputElem = $(propValElem).find('.timeinputfield');
		//show (parent) div
		$(timeInputElem).parent().show();
	});
	
	//For each dateinput
	$(".dateinputfield").each(function() {
		//Check if no date was set (empty date)
		if ( msg_date === $(this).prop("defaultValue")){
			//Set datepicker to null date
			$(this).datepicker('setDate', null );
			
			//Hide time input field
			var propValElem = $(this).closest( $("div.props-value") );
			var timeInputElem = $(propValElem).find('.timeinputfield');
			$(timeInputElem).parent().hide();
			
		} else {
			//Date was already set, set datepicker date to value
	    	$(this).datepicker('setDate', $(this).prop("defaultValue"));
		}
	});
	
	//Register clear button hook
	$(".clearDateButton").on('click', function(){
		var propValElem = $(this).closest( $("div.props-value") );

		//Empty date input and datepicker
		var dateInputElem = $(propValElem).find('.dateinputfield');
		$(dateInputElem).attr('value', '');
		$(dateInputElem).datepicker( "setDate", null);

		//Hide timeinput field
		var timeInputElem = $(propValElem).find('.timeinputfield');
		$(timeInputElem).parent().hide();
	});
	
	//Remove all other table structures under each div.proper-value element
	$("div.props-value table").remove();

	//Reset last opened expanded collapsible
	var expandedCategoryLabelName = getStoredExpansionState();
	$(".categoryCollapsible").each(function() {
		if ( expandedCategoryLabelName != null ){
			var labelName = $(this).find("span.defaultAttributeListCategoryStyle").text();
			if ( expandedCategoryLabelName === labelName){
				$(this).collapsible( "expand" );
			}
		}
	});
	//Clear cat label
	storeExpansionState(null);
	
	//Enable on click events on edit buttons
	$(".props-value a[name*='editattributelink']").each( function(){
		$(this).on('click', function(){
			storeCurrentScrollPosition();
			storeExpansionState($(this).closest(".categoryCollapsible"));
		});
	});
});

//When page is loaded ready and displayed set scroll position
$(document).on('pagecontainershow', function(e, ui) {
	restoreCurrentScrollPosition();
});

//LOCAL method
function storeCurrentScrollPosition(){
	if (typeof (Storage) !== "undefined") {
		sessionStorage.setItem("edit_attr_scrollposition", $(window).scrollTop());
	}
}

//LOCAL method
function restoreCurrentScrollPosition(){
	if (typeof (Storage) !== "undefined") {
		var prevScrollPos = sessionStorage.getItem("edit_attr_scrollposition");
		if ( prevScrollPos != null ){
			setTimeout('$.mobile.silentScroll(' + prevScrollPos + ')',20);
			sessionStorage.removeItem("edit_attr_scrollposition");
		}
	}
}

function storeExpansionState(expandedCollapsible){
	if (typeof (Storage) !== "undefined") {
		// session storage is supported
		if ( typeof expandedCollapsible != "undefined" && expandedCollapsible != null && expandedCollapsible.length > 0 ){
			var labelName = $(expandedCollapsible).find("span.defaultAttributeListCategoryStyle").text();
			sessionStorage.setItem("edit_attr_expanded_collapsible", JSON.stringify(labelName));
		} else {
			sessionStorage.removeItem("edit_attr_expanded_collapsible");
		}
	}
}

function getStoredExpansionState(){
	var expandedCategoryLabelName = null;

	if (typeof (Storage) !== "undefined") {
		var labelNameStorage = sessionStorage.getItem("edit_attr_expanded_collapsible");
		if ( labelNameStorage != null && labelNameStorage != "null"){
			expandedCategoryLabelName = $.parseJSON(labelNameStorage);
		}
	}
	return expandedCategoryLabelName;
}


