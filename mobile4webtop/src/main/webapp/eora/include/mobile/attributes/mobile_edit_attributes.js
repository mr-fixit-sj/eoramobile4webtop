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
	//Remove documentum theming
	$('link[href*="theme/documentum"]').remove();
	
	if ( returnToViewProperties ){
		returnToPropertiesPage();
	}	
});


function returnToPropertiesPage(){
	navigateToURL(g_virtualRoot + "/component/mobile_reposbrowser#propertiespage", "returntoprop",window,false);
	//window.location.href = g_virtualRoot + "/component/mobile_reposbrowser#propertiespage";
}



