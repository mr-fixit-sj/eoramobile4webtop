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

//Jquery mobile (widgets) are initialised 
$(document).on("pagecreate ", function(event,data) {
	
	$("#varadioDiv").hide();
	
	$("input.openValueRadio").change(function () {
		$("#openValueDiv").show();
		$("#varadioDiv").hide();
	});
	
	$("input.varadio").change(function () {
		$("#openValueDiv").hide();
		$("#varadioDiv").show();
	});
});
	