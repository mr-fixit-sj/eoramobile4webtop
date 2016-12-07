/*
 * 
 * @author S.Jonckheere
 * @since 1.0.0
 * 
 */
                         

var currentFolderPathIds = null;
var scrollPosFolBrowser;
var selectedObject = {
	objectId: "",
	isFolderType: false
}

$(document).ready(function () {
	//Remove documentum theming
	$('link[href*="theme/documentum"]').remove();
});

$(document).on("pagebeforecreate", "#foldercontentspage", function() {
	
	var folderId = "";

	//	var folderIdInitParam should be defined in JSP itself so it can be initialized by component class

	if ( ( typeof folderIdInitParam !== "undefined") && folderIdInitParam.indexOf("0b") == 0) {
		folderId = folderIdInitParam;
		setCurrentFolderPathIds(null);
	}
	
	//Get folder contents
	getFolderContentsServer(folderId, getCurrentFolderPathIds());
	
	//Initialise showfolderpath option
	var showFolderPath = getShowFolderPathFromStorage();
	$("#showfolderpath_checkbox").prop( "checked", showFolderPath );
	$("#showfolderpath_checkbox").change(function() {
	    if( this.checked ) {
	    	$("#currentfolderpathlabel").show();
	    } else {
	    	$("#currentfolderpathlabel").hide();
	    }
	    //Store in persistant storage
	    storeShowFolderPathInLocalStorage( this.checked );
	});
	if ( showFolderPath !== true ){
		$("#currentfolderpathlabel").hide();
	}
	
	//Initialise showemptyattributes option
	var showEmptyAttributes = getShowEmptyAttributesFromStorage();
	$("#showemptyattrs_checkbox").prop( "checked", showEmptyAttributes );
	$("#showemptyattrs_checkbox").change(function() {
	    //Store in persistant storage
		storeShowEmptyAttributesInLocalStorage( this.checked );
	});

});

$(document).on("pageshow", "#foldercontentspage", function() {
	if (scrollPosFolBrowser > 0) {
		$.mobile.silentScroll(scrollPosFolBrowser);
		scrollPosFolBrowser = 0;
	}
});

$(document).on("pagebeforeshow", "#searchformpage", function(event,data) {
	var prevPageId = data.prevPage.attr('id');
	if ( (typeof prevPageId !== "undefined") && prevPageId == "foldercontentspage" ){
		$('#search-form-message').hide();
		$("#srch-objectname").val('');
		$("#srch-title").val('');
		$("#srch-keywords").val('');
		$("#srch-fulltextterm").val('');
	}
});

$(document).on("pageshow", "#searchresultspage", function() {
	if (scrollPosFolBrowser > 0) {
		$.mobile.silentScroll(scrollPosFolBrowser);
		scrollPosFolBrowser = 0;
	}
});

$(document).on("pagebeforeshow", "#searchresultspage", function(event,data) {
	var prevPageId = data.prevPage.attr('id');
	if ( (typeof prevPageId == "undefined") || prevPageId == null || prevPageId == "" ){
		$( ":mobile-pagecontainer" ).pagecontainer( "change","#foldercontentspage", {
		  transition: 'slide',
		  reverse: true
		});
	}
});

$(document).on("pagebeforeshow", "#subscriptionspage", function(event,data) {
	getSubscriptionsServer();
});

$(document).on("pageshow", "#subscriptionspage", function() {
	if (scrollPosFolBrowser > 0) {
		$.mobile.silentScroll(scrollPosFolBrowser);
		scrollPosFolBrowser = 0;
	}
});

$(document).on("pagebeforeshow", "#locationspage", function(event,data) {
	var objectId = getSelectedObjectId();
	getObjectLocationsServer(objectId);
});

$(document).on("pagebeforeshow", "#versionspage", function(event,data) {
	var objectId = getSelectedObjectId();
	getObjectVersionsServer(objectId);
});

$(document).on("pagebeforeshow", "#propertiespage", function(event,data) {
	var prevPageId = data.prevPage.attr('id');
	
	if ( prevPageId == "foldercontentspage" || prevPageId == "searchresultspage" || prevPageId == "subscriptionspage") {
		$("#prevpagelink-propertiespage").attr("href", "#" + prevPageId);
	}
	
	var objectId = getSelectedObjectId();

	//Clear validator text
	$("#validator").text("");

	//Set url for edit properties button
	$("#btn_edit_attributes").attr("href", g_virtualRoot  + "/component/mobile_edit_attributes?objectId=" + objectId);
	//Hide header format/type icon
	$("#props_header_icon").hide();
	$("#props_header_lockicon").hide();
	
	// Email as DRL button clear href
	$("#btn_email_drl").attr("href", "#");
	$('#btn_email_drl').addClass("ui-state-disabled");

	// View button clear href and onclick
	$("#btn_view").attr("href", "#");
	$('#btn_view').addClass("ui-state-disabled");
	$('#btn_view').show();

	$('#btn_versions').show();

	if ( selectedObject.isFolderType === true ){
		$('#btn_versions').hide();
		$('#btn_view').hide();
	}

	//Default hide subscribe button
	$("#btn_subscribe").hide();
	//DeRegister previous events
	$("#btn_subscribe").unbind();
	
	// First call object properties
	getObjectPropertiesServer(objectId);

});

// JSON call to server
function getParentFolderContentsServer( folderPathIds) {

	// check if use can go back
	if (folderPathIds != null && folderPathIds != "") {

		var prefs = InlineRequestEngine.getPreferences(InlineRequestType.JSON);
		prefs.setCallback("onGetFolderContentsCallBack");

		//show loader
		if ( $('#pageloader-div').length == 0){
			$("#folderbrowsercontentdiv").prepend("<div id='pageloader-div'/>");
		}
		
		// Call server component method
		postInlineServerEvent(null, prefs, null, null, "getParentFolderContentsJSON", "currentFolderPathIds", folderPathIds);
	}
}

// JSON call to server
function getFolderContentsServer(folderId, folderPathIds) {
	if (folderId != null || folderPathIds != null) {
		var prefs = InlineRequestEngine.getPreferences(InlineRequestType.JSON);
		prefs.setCallback("onGetFolderContentsCallBack");

		//Insert loader div
		if ( $('#pageloader-div').length == 0){
			$("#folderbrowsercontentdiv").prepend("<div id='pageloader-div'/>");
		}

		// Call server component method
		postInlineServerEvent(null, prefs, null, null, "getFolderContentsJSON", "folderId", folderId, "folderPathIds", folderPathIds);
	}
}

// JSON call to server
function getObjectPropertiesServer(objectId) {

	if (objectId != null && objectId != "") {
		var prefs = InlineRequestEngine.getPreferences(InlineRequestType.JSON);
		prefs.setCallback("onGetObjectPropertiesCallBack");

		$('#propheaderinfo').hide();
		
		// First clear results
		$('#categorySet').empty()
		
		//Insert loader div
		if ( $('#pageloader-div').length == 0){
			$("#propertiescontentdiv").prepend("<div id='pageloader-div'/>");
		}

		// Call server component method
		postInlineServerEvent(null, prefs, null, null, "getObjectPropertiesJSON", "objectId", objectId, "showEmptyValues", $("#showemptyattrs_checkbox").prop('checked') );
	}
}

// JSON call to server
function subscribeObjectServer(objectId, subscribeAction){
	if (objectId != null && objectId != "") {
		var prefs = InlineRequestEngine.getPreferences(InlineRequestType.JSON);
		prefs.setCallback("onSubscribeObjectCallBack");

		//Insert loader div
		if ( $('#pageloader-div').length == 0){
			$("#propertiescontentdiv").prepend("<div id='pageloader-div'/>");
		}

		// Call server component method
		postInlineServerEvent(null, prefs, null, null, "subscribeObjectJSON", "objectId", objectId, "subscribeAction", subscribeAction );
		
	}	
}

// JSON call to server
function getObjectContentUrlServer(objectId) {

	if (objectId != null && objectId != "") {

		var prefs = InlineRequestEngine.getPreferences(InlineRequestType.JSON);
		prefs.setCallback("onGetObjectContentUrlCallBack");

		// Call server component method
		postInlineServerEvent(null, prefs, null, null, "getObjectContentUrlJSON", "objectId", objectId);
	}
}

//JSON call to server
function executeSearchServer(searchInfo) {

	if (searchInfo != null ) {

		var prefs = InlineRequestEngine.getPreferences(InlineRequestType.JSON);
		prefs.setCallback("onExecuteSearchCallBack");

		//Insert loader div
		if ( $('#pageloader-div').length == 0){
			$("#searchresultscontentdiv").prepend("<div id='pageloader-div'/>");
		}
		$("#search-result-cnt").html('');
		$("#search-query-description").html( getNlsString('MSG_SEARCH_QUERY') + "&nbsp; .....");
		
		//Remove previous results
		$('#searchresultscontentslist').empty()
		
		// Call server component method
		postInlineServerEvent(null, prefs, null, null, "executeSearchJSON", "searchInfo", JSON.stringify(searchInfo));
	}
}

//JSON call to server
function getNextSearchResultsServer( fromIndex ){
	var prefs = InlineRequestEngine.getPreferences(InlineRequestType.JSON);
	prefs.setCallback("onGetNextSearchResultsCallBack");

	// Call server component method
	postInlineServerEvent(null, prefs, null, null, "getNextSearchResultsJSON", "fromIndex", fromIndex);
}

//JSON call to server
function getSubscriptionsServer() {

	var prefs = InlineRequestEngine.getPreferences(InlineRequestType.JSON);
	prefs.setCallback("onGetSubscriptionsCallBack");

	//Insert loader div
	if ( $('#pageloader-div').length == 0){
		$("#subscriptionscontentdiv").prepend("<div id='pageloader-div'/>");
	}
	//Remove subscription results
	$('#subscriptionscontentslist').empty()
	
	// Call server component method
	postInlineServerEvent(null, prefs, null, null, "getSubscriptionsJSON");
}

//JSON call to server
function getObjectLocationsServer(objectId) {
	
	if (objectId != null && objectId != "") {
		if (isEventPostingLocked()) {
			console.log("releasing lock");
			releaseEventPostingLock();
		}

		var prefs = InlineRequestEngine.getPreferences(InlineRequestType.JSON);
		prefs.setCallback("onGetObjectLocationsCallBack");

		//Remove subscription results
		$('#locationslist').empty()

		//Insert loader div
		if ( $('#pageloader-div').length == 0){
			$("#locationscontentdiv").prepend("<div id='pageloader-div'/>");
		}
		
		// Call server component method
		postInlineServerEvent(null, prefs, null, null, "getObjectLocationsJSON", "objectId", objectId);
	}
}

//JSON call to server
function getObjectVersionsServer(objectId) {
	
	if (objectId != null && objectId != "") {
		if (isEventPostingLocked()) {
			console.log("releasing lock");
			releaseEventPostingLock();
		}

		var prefs = InlineRequestEngine.getPreferences(InlineRequestType.JSON);
		prefs.setCallback("onGetObjectVersionsCallBack");

		//Remove subscription results
		$('#versionslist').empty()

		//Insert loader div
		if ( $('#pageloader-div').length == 0){
			$("#versionscontentdiv").prepend("<div id='pageloader-div'/>");
		}
		
		// Call server component method
		postInlineServerEvent(null, prefs, null, null, "getObjectVersionsJSON", "objectId", objectId);
	}
}


//JSON CALLBACK from server
function onExecuteSearchCallBack(data) {
	
	if (isEventPostingLocked()) {
		releaseEventPostingLock();
	}
	if (data['TIMEOUTOP']){
		alert( getNlsString('MSG_TIMEOUT_MESSAGE'));
		relogin();
	} else {
		//now get results
		getNextSearchResultsServer(0);	
		
		//var searchEventMessages = data['JSON_SEARCH_EVENT_MESSAGES'];
		var queryDescription = data['JSON_SEARCHQUERY_DESCRIPTION'];
	
		$("#search-query-description").html( getNlsString('MSG_SEARCH_QUERY') + "&nbsp;" + queryDescription);
	}
}

//JSON CALLBACK from server
function onGetNextSearchResultsCallBack(data) {
	
	if (isEventPostingLocked()) {
		releaseEventPostingLock();
	}
	
	if (data['TIMEOUTOP']){
		alert( getNlsString('MSG_TIMEOUT_MESSAGE'));
		relogin();
	} else {
	
		var searchResultBeans = data['JSON_SEARCHRESULTS_BEANS'];
		//var searchEventMessages = data['JSON_SEARCH_EVENT_MESSAGES'];
		var isSearchComplete = data['JSON_SEARCH_IS_COMPLETED'];
		var isSearchTruncated = data['JSON_SEARCH_IS_TRUNCATED'];
		var searchLastIndex = data['JSON_SEARCH_LAST_RESULT_INDEX'];
	
		if ( searchResultBeans.length > 0){
			$("#searchListItemTmpl").tmpl(searchResultBeans).appendTo("#searchresultscontentslist");
			$('#searchresultscontentslist').listview("refresh");
			$('#searchresultscontentslist').trigger("updatelayout");
		}
		
		var totalResultsCount = $("#searchresultscontentslist li").size();		
		
		//update result message 
		updateSearchResultMessage( totalResultsCount, isSearchComplete, isSearchTruncated);
	
		//now check if more results need to be fetched
		if ( !isSearchComplete ){
			getNextSearchResultsServer((searchLastIndex + 1));
		}
		
		if ( totalResultsCount > 0 ){
			$("#lastSrchResultsBtn").show();
		} else {
			$("#lastSrchResultsBtn").hide();
		}
	}
	//Remove loader
	$("#pageloader-div").remove();
}

//JSON CALLBACK from server
function onGetSubscriptionsCallBack(data) {
	if (isEventPostingLocked()) {
		releaseEventPostingLock();
	}
	
	if (data['TIMEOUTOP']){
		alert( getNlsString('MSG_TIMEOUT_MESSAGE'));
		relogin();
		
	} else {
		
		var subscriptionObjectBeans = data['JSON_SUBSCRIPTIONS_BEANS'];
		
		// First clear results
		$('#subscriptionscontentslist').empty()
	
		// Format list using template
		$("#subscriptionListItemTmpl").tmpl(subscriptionObjectBeans).appendTo("#subscriptionscontentslist");
	
		// Refresh and trigger update
		$('#subscriptionscontentslist').listview("refresh");
		$('#subscriptionscontentslist').trigger("updatelayout");
	
		// Scroll to top
		$.mobile.silentScroll(0);
	}
	//Remove page loader
	$("#pageloader-div").remove();
}


// JSON CALLBACK from server
function onGetFolderContentsCallBack(data) {
	if (isEventPostingLocked()) {
		releaseEventPostingLock();
	}
	
	if (data['TIMEOUTOP']){
		alert( getNlsString('MSG_TIMEOUT_MESSAGE'));
		relogin();
	} else {
	
		var folderContentBeans = data['JSON_FOLDER_CONTENTS'];
		var folderPathIds = data['JSON_FOLDER_PATH_IDS'];
		var currentFolderPathLabel = data['JSON_FOLDER_PATH'];
		
		$('#currentfolderpathlabel').html(currentFolderPathLabel);
		
		// Set current folderpathids
		setCurrentFolderPathIds( folderPathIds );
	
		// First clear results
		$('#foldercontentslist').empty()
	
		if ( folderPathIds != null && folderPathIds != "" ){
			//Append parent folder navigation listitem to top
			$('#foldercontentslist').append("<li data-icon='false'><a onclick='getParentFolderContentsServer(getCurrentFolderPathIds())'><img class='ui-li-icon' src='" 
					+ g_virtualRoot  + "/eora/include/mobile/reposbrowser/images/icon_up_bw.png' width='32' height='32'/><p class='folcontents-objectInfo'>..</p></a></li>");
		}
		
		// Format list using template
		$("#listItemTmpl").tmpl(folderContentBeans).appendTo("#foldercontentslist");
		
	
		// Refresh and trigger update
		$('#foldercontentslist').listview("refresh");
		$('#foldercontentslist').trigger("updatelayout");
	
		// Scroll to top
		$.mobile.silentScroll(0);
	}
	//Remove page loader
	$("#pageloader-div").remove();
}

// JSON CALLBACK from server
function onGetObjectContentUrlCallBack(data) {

	if (isEventPostingLocked()) {
		releaseEventPostingLock();
	}

	if (data['TIMEOUTOP']){
		alert( getNlsString('MSG_TIMEOUT_MESSAGE'));
		relogin();
	} else {

		// set content url to view button
		var contentUrl = data['JSON_CONTENT_URL'];
		if (contentUrl != null && contentUrl != "") {
			$("#btn_view").attr("href", contentUrl);
			$('#btn_view').removeClass("ui-state-disabled");
		}
	}
}

// JSON CALLBACK from server
function onGetObjectPropertiesCallBack(data) {
	if (isEventPostingLocked()) {
		releaseEventPostingLock();
	}

	if (data['TIMEOUTOP']){
		alert( getNlsString('MSG_TIMEOUT_MESSAGE'));
		relogin();
	} else {

		//Get object content URL
		objectId = getSelectedObjectId();
		if ( selectedObject.isFolderType === false ){
			// Get content url
			getObjectContentUrlServer(objectId);
		}
	
		// set mail as url to mail button
		var mailtoBody = data['JSON_EMAIL_DRL'];
		var mailtoHref = "mailto:?body=" + escape(mailtoBody);
		
		$("#btn_email_drl").attr("href", mailtoHref);
		$("#btn_email_drl").removeClass("ui-state-disabled");
	
		//Set docbase format icon url
		var objectDocbaseFormatIconUrl = data['JSON_OBJECT_DOCBASE_FORMAT_ICON_URL'];
		$("#props_header_icon").attr( "src", objectDocbaseFormatIconUrl );
		$("#props_header_icon").show();
		
		var objectLockIconUrl = data['JSON_OBJECT_LOCK_ICON_URL'];
		if ( objectLockIconUrl != null){
			$("#props_header_lockicon").attr( "src", objectLockIconUrl );
			$("#props_header_lockicon").show();
		}
	
		//Set object Name in header
		var objectName = data['JSON_OBJECT_NAME'];
		$("#props_header_objectname").html( objectName );
		
		//Show info
		$('#propheaderinfo').show();
		
		// Set object properties
		var objectPropertiesCategoryBeans = data['JSON_CATEGORY_BEANS'];
	
		// First clear results
		$('#categorySet').empty()
	
		$.each(objectPropertiesCategoryBeans, function() {
			var categoryBean = this;
	
			if (categoryBean.attributes.length > 0) {
			
				var newdiv = $("<div align='left' data-role='collapsible' data-mini='true' data-collapsed='" + categoryBean.collapsed + "'/>")
				$(newdiv).append("<h2>" + categoryBean.categoryLabel + "</h2>");
		
				var newlist = $("<ul data-role='listview' data-inset='false' data-filter='false' data-theme='a'/>");
				$(newdiv).append(newlist);
		
				// now for each attribute in this category
				$.each(categoryBean.attributes, function() {
					var attributeBean = this;
					var newli = $("<li class='ui-li-static-props' data-icon='false'/>");
		
					var attrValue = attributeBean.value;
					if (attrValue == null) {
						attrValue = "";
					}
		
					$(newli).append("<p class='props-label'>" + attributeBean.label + "</p>");
					$(newli).append("<p class='props-value'>" + attrValue + "</p>");
		
					$(newlist).append(newli);
				});
				//Now append categorydiv
				$('#categorySet').append(newdiv);
			}
		});
	
		$('#categorySet').trigger('create');
		$('#categorySet').trigger("updatelayout");
	
		//Handle subscribe button
		var canSubscribeObject = data['JSON_CAN_SUBSCRIBE_OBJECT'];
		var canUnSubscribeObject = data['JSON_CAN_UNSUBSCRIBE_OBJECT'];
		
		updateSubscribeObjectButton( canSubscribeObject, canUnSubscribeObject, objectId);
		
		// Scroll to top
		$.mobile.silentScroll(0);
	}
	//Remove page loader
	$("#pageloader-div").remove();
}

//JSON CALLBACK from server
function onGetObjectLocationsCallBack(data){
	
	if (isEventPostingLocked()) {
		releaseEventPostingLock();
	}
	
	if (data['TIMEOUTOP']){
		alert( getNlsString('MSG_TIMEOUT_MESSAGE'));
		relogin();
	} else {

		// Get locations
		var objectLocationBeans = data['JSON_LOCATIONS_BEANS'];
		
		// Format list using template
		$("#locationsFolderItemTmpl").tmpl(objectLocationBeans).appendTo("#locationslist");
	
		// Refresh and trigger update
		$('#locationslist').listview("refresh");
		$('#locationslist').trigger("updatelayout");
	}
	//Remove page loader
	$("#pageloader-div").remove();
}

//JSON CALLBACK from server
function onGetObjectVersionsCallBack(data){
	
	if (isEventPostingLocked()) {
		releaseEventPostingLock();
	}
	
	if (data['TIMEOUTOP']){
		alert( getNlsString('MSG_TIMEOUT_MESSAGE'));
		relogin();
	} else {
	
		// Get versions
		var objectVersionBeans = data['JSON_VERSIONS_BEANS'];
		
		// Format list using template
		$("#versionsObjectItemTmpl").tmpl(objectVersionBeans).appendTo("#versionslist");
	
		// Refresh and trigger update
		$('#versionslist').listview("refresh");
		$('#versionslist').trigger("updatelayout");
		
	}
	//Remove page loader
	$("#pageloader-div").remove();
}

//JSON CALLBACK from server
function onSubscribeObjectCallBack(data){
	
	if (isEventPostingLocked()) {
		releaseEventPostingLock();
	}
	
	if (data['TIMEOUTOP']){
		alert( getNlsString('MSG_TIMEOUT_MESSAGE'));
		relogin();
	} else {

		//Set popup text and show
		$("#popupCloseRight p").text(data['JSON_SUBSCRIBE_ACTION_RESULT_MSG']);
		$("#popupCloseRight").popup("open", { 	corners: false,
												positionTo: "window",
												transition: "flip",
												history: false,
												overlayTheme: "b"});

		//Handle subscribe button
		var canSubscribeObject = data['JSON_CAN_SUBSCRIBE_OBJECT'];
		var canUnSubscribeObject = data['JSON_CAN_UNSUBSCRIBE_OBJECT'];
		
		updateSubscribeObjectButton( canSubscribeObject, canUnSubscribeObject, getSelectedObjectId());
	}
	//Remove page loader
	$("#pageloader-div").remove();
}


// LOCAL method
function getSelectedObjectId(){
	var objectId = "";
	if (selectedObject != null && selectedObject.objectId != null && selectedObject.objectId != "") {
		objectId = selectedObject.objectId;

	} else if (typeof (Storage) !== "undefined") {
		// session storage is defined
		var sessSelectedObjStr = sessionStorage.getItem('selectedObject');
		if ( sessSelectedObjStr != null && sessSelectedObjStr != "null"){
			selectedObject = $.parseJSON(sessSelectedObjStr);
			objectId = selectedObject.objectId;
		}
	}
	return objectId;
}

// LOCAL method
//function setSelectedObject(objectId, isFolderType) {
function onClickShowObjectProperties(objectId, isFolderType, storeScrollPosition ){
	
	// use local var to set selection
	selectedObject.objectId = objectId;
	selectedObject.isFolderType = isFolderType;

	if (typeof (Storage) !== "undefined") {
		// session storage is supported
		// Store selected objectid on session te retain on refresh
		sessionStorage.setItem("selectedObject", JSON.stringify(selectedObject));
	}
	
	if ( storeScrollPosition ){
		//first store scrolling position
		storeCurrentScrollPosition();
	}
	
	//Change to properties page
	$( ":mobile-pagecontainer" ).pagecontainer( "change","#propertiespage", {
		  transition: 'slide'
		});
}

//LOCAL method
function storeCurrentScrollPosition(){
	scrollPosFolBrowser = $(window).scrollTop();
}

// LOCAL method
function setCurrentFolderPathIds( folderPathIds ){
	
	// Set current folderpathids to new string
	currentFolderPathIds = folderPathIds;
	
	// store on session for refresh restore
	if (typeof (Storage) !== "undefined") {
		// session storage is supported
		// Store selected objectid on session
		sessionStorage.setItem("folderPathIds", folderPathIds);
	}
}

//LOCAL method
function getCurrentFolderPathIds(){
	var folderPathIds = "";
	
	if (currentFolderPathIds != null ) {
		folderPathIds = currentFolderPathIds;

	} else if (typeof (Storage) !== "undefined") {
		// session storage is defined
		var sessFolderPathIds = sessionStorage.getItem("folderPathIds");
		if ( sessFolderPathIds != null && sessFolderPathIds != "undefined" && sessFolderPathIds != "null"){
			folderPathIds = sessFolderPathIds;
		}
	}
	return folderPathIds;
}

//LOCAL method
function onClickShowCabinets(){
	getFolderContentsServer('',null);
	$("#foldercontentsnavpanel").panel("close");
}

//
//SEARCH RELATED METHODS
//


//LOCAL method
function onClickGoSearch(){
	var searchInfo = {
			objectName : $("#srch-objectname").val(),
			keywords: $("#srch-keywords").val(),
			title: $("#srch-title").val(),
			fulltextTerm: $("#srch-fulltextterm").val(),
			showSummary: $("#srch-show-summary").prop("checked")
	}
	$('#search-form-message').hide();
	
	if ( searchInfo.objectName == "" && searchInfo.keywords == "" && searchInfo.title == "" && searchInfo.fulltextTerm == ""){
		
		$('#search-form-message').html( getNlsString('MSG_SEARCH_TERM_REQUIRED') );
		$('#search-form-message').show();
		
	} else {
		executeSearchServer( searchInfo );
		$( ":mobile-pagecontainer" ).pagecontainer( "change","#searchresultspage", {
		  	transition: 'slide',
			});
	}
}

//LOCAL METHOD
function getShowFolderPathFromStorage(){
	var showFolderPath = true;
	if (typeof (Storage) !== "undefined") {
		var showFolderPathStorage = localStorage.getItem("showFolderPath");
		if ( showFolderPathStorage != null && showFolderPathStorage != "null"){
			showFolderPath = JSON.parse(showFolderPathStorage);
		}
	}
	return showFolderPath;
}

//LOCAL method
function storeShowFolderPathInLocalStorage( showFolderPath ){
	if (typeof (Storage) !== "undefined") {
		localStorage.setItem("showFolderPath", showFolderPath);
	}
}

//LOCAL METHOD
function getShowEmptyAttributesFromStorage(){
	var showEmptyAttributes = false;
	if (typeof (Storage) !== "undefined") {
		var showEmptyAttributesStorage = localStorage.getItem("showEmptyAttributes");
		if ( showEmptyAttributesStorage != null && showEmptyAttributesStorage != "null"){
			showEmptyAttributes = JSON.parse(showEmptyAttributesStorage);
		}
	}
	return showEmptyAttributes;
}

//LOCAL method 
function storeShowEmptyAttributesInLocalStorage( showEmptyAttributes ){
	if (typeof (Storage) !== "undefined") {
		localStorage.setItem("showEmptyAttributes", showEmptyAttributes);
	}
}


//LOCAL method
function updateSubscribeObjectButton(canSubscribeObject,canUnSubscribeObject, objectId){
	//Default hide subscribe button
	$("#btn_subscribe").hide();

	//DeRegister previous events
	$("#btn_subscribe").unbind();
	
	if (canSubscribeObject) {
		//Register click for this objectid
		$("#btn_subscribe").on('click', function(){
			subscribeObjectServer(objectId,'mobilesubscribe');
		});	
		
		//Set label
		$("#btn_subscribe").text( getNlsString('MSG_SUBSCRIBE'));
		$("#btn_subscribe").show();
	} else if (canUnSubscribeObject) {
		//Register click for this objectid
		$("#btn_subscribe").on('click', function(){
			subscribeObjectServer(objectId,'mobileunsubscribe');
		});	

		//Set label
		$("#btn_subscribe").text( getNlsString('MSG_UNSUBSCRIBE'));
		$("#btn_subscribe").show();
	}
}

//LOCAL method
function updateSearchResultMessage( itemCount, isSearchComplete, isSearchTruncated ){

	//No results
	if ( itemCount == 0){
		$('#search-result-cnt').html( getNlsString('MSG_NO_RESULTS') );
		
	//Already fetched results, more coming
	} else  if ( itemCount > 0 && isSearchComplete === false){
		$('#search-result-cnt').html( getNlsString('MSG_ALREADY_FOUND_RESULTS') + "&nbsp;"+ itemCount);
	
	//Search is complete and is not truncated
	} else if ( itemCount > 0 && isSearchComplete === true && isSearchTruncated === false ){
		$('#search-result-cnt').html( getNlsString('MSG_FOUND_RESULTS') + "&nbsp;"+ itemCount);
		
	//Search is complete and is truncated
	} else if ( itemCount > 0 && isSearchComplete === true && isSearchTruncated === true ){
		$('#search-result-cnt').html( getNlsString('MSG_FOUND_RESULTS') + "&nbsp;"+ itemCount + "+");
		
	// Other cases make empty
	} else {
		$('#search-result-cnt').html('');
	}
}

function onClickImportButton(){
	var folderPathIds = getCurrentFolderPathIds();
	postServerEvent(null, null, null, "onClickImport","folderPathIds", folderPathIds);
}

