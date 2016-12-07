/*
 * 
 * @author S.Jonckheere
 * @since 1.0.0
 * 
 */

$( document ).on( "mobileinit", function() {
	$.mobile.ajaxEnabled = false;
});

$(document).on('pageinit', function() {
	$("head", top.document).append("<meta name='viewport' content='width=device-width, initial-scale=1'>");
	
	//Remove documentum theming
	$('link[href*="theme/documentum"]').remove();	
});


//Repeating attributes page. Creating editable & sortable list
$(document).on("pagebeforecreate", function() {
	
	$(document).on("click", "a.optionBtn" , function() {
        var listItem = $(this).closest("li");
        optionPopup(listItem);
    });
	
	//Make the repeatingAttribute list sortable
    $( "#repeatingAttrList" ).sortable();
    $( "#repeatingAttrList" ).disableSelection();
    
    //Refresh list to the end of sort to have a correct display
    $( "#repeatingAttrList" ).bind( "sortstop", function(event, ui) {
      	$('#repeatingAttrList').listview( "refresh" );
      	updateHiddenText();
    });
    
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
	
	//Initialise radio selection button
	$("#openValueDiv").hide();
	
	$("input.openValueRadio").change(function () {
		toggleOpenValueDiv();
	});
	
	$("input.varadio").change(function () {
		toggleVaRadioDiv();
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
		var propValElem = $(this).closest( $("#openValueDiv") );
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
			var propValElem = $(this).closest( $("#openValueDiv") );
			var timeInputElem = $(propValElem).find('.timeinputfield');
			$(timeInputElem).parent().hide();
			
		} else {
			//Date was already set, set datepicker date to value
	    	$(this).datepicker('setDate', $(this).prop("defaultValue"));
		}
	});
	
	//Register clear button hook
	$(".clearDateButton").on('click', function(){
		var propValElem = $(this).closest( $("#openValueDiv") );

		//Empty date input and datepicker
		var dateInputElem = $(propValElem).find('.dateinputfield');
		$(dateInputElem).attr('value', '');
		$(dateInputElem).datepicker( "setDate", null);

		//Hide timeinput field
		var timeInputElem = $(propValElem).find('.timeinputfield');
		$(timeInputElem).parent().hide();
	});
});

function editListItem (item)
{		
	//Get selected listItem
	var listItem = item.closest("li");
	var itemValue = listItem.text();
	
	//Move item from list to appropriate text/boolean/date field (if value assistance with completed list then only remove the item)
	var controlName = $("#hiddenControl").val();
	if (controlName == "noVAbooleanAddEntry")
	{
		if (itemValue == "T") {
			$("#noVAbooleanAddEntry").prop("checked",true).checkboxradio("refresh");
		} else {
			$("#noVAbooleanAddEntry").prop("checked",false).checkboxradio("refresh");
		}
		
		listItem.remove();
		updateHiddenText();
		
	} else if (controlName == "noVAaddEntry") {
		
		$("#noVAaddEntry").val(itemValue);
		listItem.remove();
		updateHiddenText();
		
	} else if (controlName == "booleanAddEntry") {
		
		if (itemValue == "T") {
			$("#booleanAddEntry").prop("checked", true).checkboxradio( "refresh" );
		} else {
			$("#booleanAddEntry").prop("checked", false).checkboxradio( "refresh" );
		}
		$("input.openValueRadio").prop('checked', true).checkboxradio( "refresh" );
		$("input.varadio").prop('checked', false).checkboxradio( "refresh" );
		toggleOpenValueDiv();
		listItem.remove();
		updateHiddenText();
	
	} else if (controlName == "addEntry") {

		$("#addEntry").val(itemValue);
		
		$("input.openValueRadio").prop('checked', true).checkboxradio( "refresh" );
		$("input.varadio").prop('checked', false).checkboxradio( "refresh" );
		toggleOpenValueDiv();
		listItem.remove();
		updateHiddenText();
		
	} else {
		listItem.remove();
		updateHiddenText();
	}
	
}


function updateHiddenText ()
{
   var newText = "";
   
   $.each($("#repeatingAttrList li a h2"), function() {
	   
	   if (newText != "")
	   {
	      newText += '\n';
	   }
	   
	   newText += $(this).text();
   });
	   
   $("#hiddenText").val(newText);
   
}

function toggleOpenValueDiv ()
{
	$("#openValueDiv").show();
	$("#varadioDiv").hide();
}

function toggleVaRadioDiv ()
{
	$("#varadioDiv").show();
	$("#openValueDiv").hide();
}


function onClickAdd (obj)
{
   var newValue = "";
   var controlName = $("#hiddenControl").val();
   if (controlName == "noVAbooleanAddEntry")
   {
      if ($("#noVAbooleanAddEntry").prop('checked'))
      {
         newValue = "T";
      }
      else
      {
         newValue = "F";
      }
      $("#noVAbooleanAddEntry").first().focus();
   }
   else if (controlName == "noVAaddEntry")
   {
      newValue = $("#noVAaddEntry").val();
      $("#noVAaddEntry").val("");
      $("#noVAaddEntry").first().focus();
   }
   else if (controlName == "booleanAddEntry")
   {
      if ($("#booleanAddEntry").prop('checked'))
      {
         newValue =  "T";
      }
      else
      {
         newValue =  "F";
      }
      $("#booleanAddEntry").first().focus();
   }
   else if (controlName == "addEntry")
   {
      newValue = $("#addEntry").val();
      $("#addEntry").val("");
      $("#addEntry").first().focus();
   }

   var valueIsValid = true;
   if (typeof(isValueValid) != "undefined")
   {
      valueIsValid = isValueValid(newValue);
   }

   if (valueIsValid)
   {
      // find out if there is any value in va listbox
      var newVaValue;
      var vaControlName = $("#hiddenVaControl").val();
      if (vaControlName == "va")
      {
         newVaValue = $("#va").val();
      }

      // add value to the repeatingAttrList
      if ((newValue != null) && (newValue != ""))
      {
      	  $("#repeatingAttrList").append("<li><a href=''><h2>" + newValue + "</h2></a><a data-icon='action' class='optionBtn'></a></li>");
    	  $("#repeatingAttrList").listview( "refresh" );
      }
      else if ((newVaValue != null) && (newVaValue != ""))
      {
      	  $("#repeatingAttrList").append("<li><a href=''><h2>" + newVaValue + "</h2></a><a data-icon='action' class='optionBtn'></a></li>");
    	  $("#repeatingAttrList").listview( "refresh" );
    	  
  		
      }

      updateHiddenText();

   }

}

function initListbox ()
{

   var testIdPrefix = "__RepeatingAttrOption_";

   // update attrvalue listbox upon page is loaded
   var strHiddenText = $("#hiddenText").val();

   var lineDelimiter;
   /*
     IE 10 abandons \r in favor of \n. Hence the check for IE version is needed.
     For IE 10, just use \n as line delimiter.
   */
   var isIE10 = !!navigator.userAgent.toLowerCase().match(/msie 10/);
   if (document.all && !isIE10) //is IE
   {
      lineDelimiter = '\r';
   }
   else
   {
      lineDelimiter = '\n';
   }

   // add the listbox options
   var iFrom = 0;
   var iTo = 0;
   var j = 0;
   while (iTo < strHiddenText.length && iFrom < strHiddenText.length)
   {
      iTo = strHiddenText.indexOf(lineDelimiter, iFrom);
      if (iTo == -1)
      {
         iTo = strHiddenText.length;
      }

      if (iTo > iFrom)
      {
         var strOption = strHiddenText.substring(iFrom, iTo);

         // strip leading or trailing '\r' or '\n' characters
         while (strOption.length > 0 && (strOption.charAt(0) == '\r' ||
                                         strOption.charAt(0) == '\n'))
         {
            strOption = strOption.substring(1);

         }
         while (strOption.length > 0 && (strOption.charAt(strOption.length - 1) == '\r' ||
                                         strOption.charAt(strOption.length - 1) == '\n'))
         {
            strOption = strOption.substring(0, strOption.length - 1);
         }

         // append option
         if (strOption.length > 0)
         {
            var option = new Option(strOption, strOption);
            option.id = testIdPrefix + strOption;
            
            //Add to attributelist
            $("#repeatingAttrList").append("<li><a href=''><h2>" + option.value + "</h2></a><a data-icon='action' class='optionBtn'></a></li>");   
            j++;
         }
      }
      iFrom = iTo + 1;
   }
   $("#repeatingAttrList").listview( "refresh" );
}


//Function showing a confirmation popup when deleting an item in the repeating attributes page
function optionPopup(listitem) {
    
    // Show the confirmation popup
    $("#confirm").popup( "open" );
    
    // Proceed when the user chooses edit
    $("#confirm #edit").on( "click", function() {
    	editListItem(listitem);
    });
    
    $("#confirm #delete").on( "click", function() {
		listitem.remove();
		$( "#repeatingAttrList" ).listview( "refresh" );
		updateHiddenText();
    });
    
    // Remove active state and unbind when the cancel button is clicked
    $("#confirm #cancel").on( "click", function() {
        $("#confirm #edit").off();
        $("#confirm #delete").off();
    });
}