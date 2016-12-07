/*
 * 
 * @author S.Jonckheere
 * @since 1.0.0
 * 
 */



$( document ).on( "mobileinit", function() {
	$.mobile.ajaxEnabled = false;
});



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
	
	//Find all date input fields
	$.each($(".props-value button"), function() {
		//Remove all button tags generated by dctm tags for VA with not completed lists 
		$(this).remove();
	});
});

//JQuery mobile (widgets) are initialised 
$(document).on("pagecreate ", function(event,data) {
	var requiredFieldAsteriskSpan = $("#headerimportattrs .requiredFieldAsterisk");
	if ( requiredFieldAsteriskSpan.length > 0 ){
		var liElement = $(requiredFieldAsteriskSpan).closest("li");
		$(liElement).find("div.props-label").prepend(requiredFieldAsteriskSpan);
	}
});

//JQuery mobile (widgets) are initialised 
$(document).on("pagecreate ", function(event,data) {
	
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
		var requiredFieldAsteriskSpan = $(tdElement).find( "span.requiredFieldAsterisk" );
		if ( $(requiredFieldAsteriskSpan).length > 0 ){
			
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
});