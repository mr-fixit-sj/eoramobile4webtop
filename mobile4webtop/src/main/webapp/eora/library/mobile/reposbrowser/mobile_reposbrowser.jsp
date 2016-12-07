<%@ page contentType="text/html; charset=UTF-8"%>

<!--***********************************************************************-->
<!--                                                                       -->
<!-- Eora Mobile4Webtop                                                -->
<!--                                                                       -->
<!-- @author S.Jonckheere                             	  				   -->
<!-- @since 1.0.0                                           			   -->
<!--                                                                       -->
<!--***********************************************************************-->


<%@ page import="com.documentum.web.form.Form"%>
<%@ page import="com.documentum.web.formext.config.ConfigService"%>
<%@ page import="com.documentum.web.util.SafeHTMLString"%>
<%@ page import="com.eora.dctm.mobile4webtop.reposbrowser.MobileReposBrowser"%>

<%@ taglib uri="/WEB-INF/tlds/dmform_1_0.tld" prefix="dmf"%>
<%@ taglib uri="/WEB-INF/tlds/dmformext_1_0.tld" prefix="dmfx"%>


<dmf:html>
<dmf:head>

	<dmf:webform />

	<dmf:title><dmf:label nlsid='MSG_COMPONENT_TITLE'/></dmf:title>
	<meta name="viewport" content="width=device-width, initial-scale=1">

	<script src='<%=Form.makeUrl(request, "/eora/include/jquery/jquery-1.11.1.min.js")%>'/></script>
	<script src='<%=Form.makeUrl(request, "/eora/include/jquery/extensions/jquery.tmpl-1.0.4.js")%>'></script>
	<script src='<%=Form.makeUrl(request, "/eora/include/jquery/mobile/1.4.5/jquery.mobile-1.4.5.min.js")%>'></script>
	<script src='<%=Form.makeUrl(request, "/eora/include/mobile/reposbrowser/mobile_reposbrowser.js")%>'></script>

	<link rel="stylesheet" href='<%=Form.makeUrl(request,"/eora/include/mobile/reposbrowser/css/mobile_reposbrowser.css?version=1.0.0")%>'/>
	<link rel="stylesheet" href='<%=Form.makeUrl(request,"/eora/include/mobile/reposbrowser/css/jquery-mobile-custom-theme.min.css")%>'/>
	<link rel="stylesheet" href='<%=Form.makeUrl(request,"/eora/include/jquery/mobile/1.4.5/jquery.mobile-1.4.5.min.css")%>'/>
	<link rel="stylesheet" href='<%=Form.makeUrl(request,"/eora/include/jquery/mobile/1.4.5/jquery.mobile.structure-1.4.5.min.css")%>'/>
	<link rel="stylesheet" href='<%=Form.makeUrl(request,"/eora/include/jquery/mobile/1.4.5/jquery.mobile.inline-png-1.4.5.min.css")%>'/>

	<link rel="apple-touch-icon" href='<%=Form.makeUrl(request,"/eora/include/mobile/reposbrowser/images/ios_touch_icons/touch-icon-152x152.png")%>'>
	<link rel="apple-touch-icon" sizes="76x76" href='<%=Form.makeUrl(request,"/eora/include/mobile/reposbrowser/images/ios_touch_icons/touch-icon-76x76.png")%>'>
	<link rel="apple-touch-icon" sizes="120x120" href='<%=Form.makeUrl(request,"/eora/include/mobile/reposbrowser/images/ios_touch_icons/touch-icon-120x120.png")%>'>
	<link rel="apple-touch-icon" sizes="152x152" href='<%=Form.makeUrl(request,"/eora/include/mobile/reposbrowser/images/ios_touch_icons/touch-icon-152x152.png")%>'>

</dmf:head>
<dmf:body>
<dmf:form>
 
<%
	final MobileReposBrowser form = (MobileReposBrowser)pageContext.getAttribute(Form.FORM, PageContext.REQUEST_SCOPE);
	boolean enableAddEditContent = form.isAddContentAndEditAttrEnabled();
	final String headerLogoUrl = ConfigService.getConfigLookup().lookupString("component[id=" + MobileReposBrowser.MOBILE_REPOSITORY_BROWSER_COMPONENT + "]." + MobileReposBrowser.CFG_HEADER_LOGO_URL, form.getContext());
%>

<%@include file="/eora/library/mobile/reposbrowser/listitem_templates.tmpl" %>

<script type="text/javascript">

	//Define init parameter here, this is used in 'beforepagecreate' event of folderbrowser page
	var folderIdInitParam = "<%=form.getInitFolderId()%>";

	function getNlsString( nlsid ){
		var nlsStr = null;
		switch (nlsid ){
			case 'MSG_SEARCH_QUERY': nlsStr = '<%=SafeHTMLString.escapeText(form.getString("MSG_SEARCH_QUERY"))%>';break 
			case 'MSG_NO_RESULTS':  nlsStr = '<%=SafeHTMLString.escapeText(form.getString("MSG_NO_RESULTS"))%>';break;
			case 'MSG_FOUND_RESULTS': nlsStr = '<%=SafeHTMLString.escapeText(form.getString("MSG_FOUND_RESULTS"))%>'; break;
			case 'MSG_ALREADY_FOUND_RESULTS': nlsStr = '<%=SafeHTMLString.escapeText(form.getString("MSG_ALREADY_FOUND_RESULTS"))%>';break;
			case 'MSG_SEARCH_TERM_REQUIRED' : nlsStr = '<%=SafeHTMLString.escapeText(form.getString("MSG_SEARCH_TERM_REQUIRED"))%>';break;
			case 'MSG_TIMEOUT_MESSAGE' : nlsStr = '<%=SafeHTMLString.escapeText(form.getString("MSG_TIMEOUT_MESSAGE"))%>';break;
			case 'MSG_SUBSCRIBE' : nlsStr = '<%=SafeHTMLString.escapeText(form.getString("MSG_SUBSCRIBE"))%>';break;
			case 'MSG_UNSUBSCRIBE' : nlsStr = '<%=SafeHTMLString.escapeText(form.getString("MSG_UNSUBSCRIBE"))%>';break;
		}
		return nlsStr;
	}
	
</script>


<div data-role="page" id="foldercontentspage" data-theme="c" data-title="<%=form.getString("MSG_COMPONENT_TITLE")%>">
	<!-- Panel -->
	<div data-role="panel" id="foldercontentsnavpanel" data-position="left" data-display="push" data-position-fixed="true">
		<br/>
	    <a href="#searchformpage" data-role="button" data-icon="search" data-transition="slide" data-mini="true"><%=form.getString("MSG_SEARCH")%></a>
	    <a href="#searchresultspage" id="lastSrchResultsBtn" data-role="button" data-icon="search" data-transition="slide" data-mini="true" style="display: none;"><%=form.getString("MSG_LAST_SEARCH_RESULTS")%></a>
	    <a href="#subscriptionspage" data-role="button" data-icon="star" data-transition="slide" data-mini="true"><%=form.getString("MSG_SUBSCRIPTIONS")%></a>
	    <a href="#" onclick="onClickShowCabinets()" data-role="button" data-icon="home"  data-mini="true"><%=form.getString("MSG_DOCBASE_FOLDERS")%></a>
	    <a href="#optionsPage" data-role="button" data-icon="gear" data-transition="slide" data-mini="true"><%=form.getString("MSG_OPTIONS")%></a>
		<br>
		<a href='<%=Form.makeUrl(request,"/action/logout")%>' data-transition="slide" data-rel="external" rel="external" data-ajax="false" data-role="button" data-icon="delete" data-mini="true"><%=form.getString("MSG_LOGOUT")%></a>
	</div>

	<!-- Header -->
	<div data-position="fixed" data-role="header" data-tap-toggle="false">
		<a href="#foldercontentsnavpanel" class="folderbrowser-header-icon" data-role="button" data-icon="bars" data-transition="down" data-iconpos="notext"></a>
		<h1 style="display: inline;">
			<img onclick="$.mobile.silentScroll(0)" src="<%=Form.makeUrl(request, headerLogoUrl)%>" width="177" height="43"/>
		</h1>
<%
	if (enableAddEditContent){
%>
		<a href='#' data-icon='plus' onclick='onClickImportButton()' class="folderbrowser-header-icon" data-role="button" data-iconpos="notext"></a>
<%
	}
 %>
	</div>

	<!--  Main content -->
	<div id="folderbrowsercontentdiv" align="left" data-role="content" >
		<p id="currentfolderpathlabel" class="folcontents-pathlabel"></p>
		<ul id="foldercontentslist" data-role="listview" data-inset="false" data-filter="true" data-theme="c" data-content-theme="c"></ul>
	</div>
</div> 


<div data-role="page" id="propertiespage" data-theme="c" data-title="<%=form.getString("MSG_COMPONENT_TITLE")%>">

	<!-- Header -->
	<div data-position="fixed" data-role="header" data-tap-toggle="false">
		<a id="prevpagelink-propertiespage" href="#foldercontentspage" data-direction="reverse" data-transition="slide" class="ui-btn-left ui-btn ui-btn-inline ui-mini ui-corner-all ui-btn-icon-left ui-icon-back"><%=form.getString("MSG_BACK")%></a>
		<h1 style="display: inline;">
			<img onclick="$.mobile.silentScroll(0)" src="<%=Form.makeUrl(request, headerLogoUrl)%>" width="177" height="43"/>
		</h1>
	</div>

	<!--  Main content -->
	<div id="propertiescontentdiv" align="left" data-role="content">
		<dmf:label cssclass="pagelabel" nlsid='MSG_PROPERTIES'/>

		<div class="props-header-info">
			<div class="props-header-icon">
				<img id="props_header_icon" class='ui-li-icon' width='32' height='32'/>
				<img id="props_header_lockicon" class='props-header-lockIconOverlay' width='16' height='16'/>
			</div>
			<div ><p id="props_header_objectname" class='props-header-label'></p></div>		
		</div>
		<div id="validator">
			<dmfx:docbaseobject name="obj" configid="attributes" type="dm_sysobject"/>
		</div>	
		<div id="categorySet" align="left" data-role="collapsible-set" data-theme="c" data-content-theme="a" data-inset="false" >
		</div>
		<br/>
	</div>

	<!-- Footer -->
	<div data-role="footer" data-position="fixed" data-transition="slide" class="ui-bar" >
		<a id="btn_view" rel="external" data-rel="external" data-disabled="true" data-role="button" data-mini="true" class="props-buttons" ><%=form.getString("MSG_OPEN")%></a>
		<a href="#versionspage" id="btn_versions"  data-role="button" data-transition="slideup" data-mini="true" class="props-buttons"><%=form.getString("MSG_VERSIONS")%></a>
		<a href="#locationspage" id="btn_locations" data-role="button" data-transition="slideup"  data-mini="true" class="props-buttons"><%=form.getString("MSG_LOCATIONS")%></a>
		<a id="btn_email_drl" rel="external" data-rel="external" data-disabled="true" data-role="button" data-mini="true" class="props-buttons"><%=form.getString("MSG_EMAIL_DRL")%></a>
<%
	if (enableAddEditContent){
%>
		<a id="btn_edit_attributes" rel="external" data-rel="external" data-disabled="true" data-role="button" data-mini="true" data-transition="slide" class="props-buttons" ><%=form.getString("MSG_EDIT_ATTRIBUTES")%></a>
<%
	}
%>
		<a href="#" id="btn_subscribe" data-role="button" data-transition="slideup" data-mini="true" class="props-buttons"></a>
	</div> 
	<div data-role="popup" id="popupCloseRight" class="ui-content">
	   	<a href="#" data-rel="back" class="ui-btn ui-corner-all ui-shadow ui-btn-a ui-icon-delete ui-btn-icon-notext ui-btn-right">Close</a>
	   	<p></p>
	</div>	

</div> 

<div data-role="page" id="searchformpage" data-theme="c" data-title="<%=form.getString("MSG_COMPONENT_TITLE")%>">

	<!-- Header -->
	<div data-position="fixed" data-role="header" data-tap-toggle="false">
		<a href="#foldercontentspage" data-direction="reverse" data-transition="slide" class="ui-btn-left ui-btn ui-btn-inline ui-mini ui-corner-all ui-btn-icon-left ui-icon-back"><%=form.getString("MSG_BACK")%></a>
		<h1 style="display: inline;">
			<img onclick="$.mobile.silentScroll(0)" src="<%=Form.makeUrl(request, headerLogoUrl)%>" width="177" height="43"/>
		</h1>
	</div>

	<!--  Main content -->
	<div id="searchcontentdiv" align="left" data-role="content">
		<dmf:label cssclass="pagelabel" nlsid='MSG_SEARCH'/>
		<div class="ui-body ui-corner-all search-form-body">
			<dmf:label nlsid='MSG_OBJECT_NAME' cssclass='search-labels'/>
			<dmf:text id='srch-objectname' name='srchobjectname' cssclass="search-labels" autocompleteenabled="false"  defaultonenter='true' />
			
			<dmf:label nlsid='MSG_TITLE' cssclass='search-labels'/>
			<dmf:text id='srch-title' name='srchtitle' cssclass="search-labels" autocompleteenabled="false" defaultonenter='true' />
			
			<dmf:label nlsid='MSG_KEYWORD' cssclass='search-labels'/>
			<dmf:text id='srch-keywords' name='srchkeywords' cssclass="search-labels" autocompleteenabled="false" defaultonenter='true' />
		</div>
		<br/>
		<div>
			<p id='search-form-message' class='search-form-message-text' style="display:none">/>
		</div>
		<div class="ui-body ui-corner-all search-form-body">
			<dmf:label nlsid='MSG_FULLTEXT' cssclass='search-labels'/>
			<dmf:text id='srch-fulltextterm' name='srchfulltextterm' cssclass="search-labels" autocompleteenabled="false" defaultonenter='true' />
		</div>
		<br/>
		<input type="checkbox" name="srch-show-summary" id="srch-show-summary" data-mini="false">
		<label class="search-labels" for="srch-show-summary"><%=form.getString("MSG_SHOW_SUMMARY")%></label>
		<dmf:button id="btn_go_search" name="btn_go_search" nlsid='MSG_GO_TIP' onclick='onClickGoSearch()' runatclient="true" default='true' />
	</div>

</div> 

<div data-role="page" id="searchresultspage" data-theme="c" data-title="<%=form.getString("MSG_COMPONENT_TITLE")%>">

	<!-- Header -->
	<div data-position="fixed" data-role="header" data-tap-toggle="false">
		<a href="#searchformpage" data-direction="reverse" data-transition="slide" class="ui-btn-left ui-btn ui-btn-inline ui-mini ui-corner-all ui-btn-icon-left ui-icon-back"><%=form.getString("MSG_BACK")%></a>
		<h1 style="display: inline;">
			<img onclick="$.mobile.silentScroll(0)" src="<%=Form.makeUrl(request, headerLogoUrl)%>" width="177" height="43"/>
		</h1>
	</div>

	<!--  Main content -->
	<div id="searchresultscontentdiv" align="left" data-role="content">
		<p id='search-query-description' class='search-labels'></p>
		<p id='search-result-cnt' class='search-result-count'></p>
		<ul id="searchresultscontentslist" data-role="listview" data-inset="false" data-filter="true" data-theme="c" data-content-theme="c"></ul>
	</div>
</div> 

<div data-role="page" id="subscriptionspage" data-theme="c" data-title="<%=form.getString("MSG_COMPONENT_TITLE")%>">

	<!-- Header -->
	<div data-position="fixed" data-role="header" data-tap-toggle="false">
		<a href="#foldercontentspage" data-direction="reverse" data-transition="slide" class="ui-btn-left ui-btn ui-btn-inline ui-mini ui-corner-all ui-btn-icon-left ui-icon-back"><%=form.getString("MSG_BACK")%></a>
		<h1 style="display: inline;">
			<img onclick="$.mobile.silentScroll(0)" src="<%=Form.makeUrl(request, headerLogoUrl)%>" width="177" height="43"/>
		</h1>
	</div>

	<!--  Main content -->
	<div id="subscriptionscontentdiv" align="left" data-role="content" >
		<dmf:label cssclass="pagelabel" nlsid='MSG_SUBSCRIPTIONS'/>
		<ul id="subscriptionscontentslist" data-role="listview" data-inset="false" data-filter="true" data-theme="c" data-content-theme="c"></ul>
	</div>
</div> 

<div data-role="page" id="locationspage" data-theme="c" data-title="<%=form.getString("MSG_COMPONENT_TITLE")%>">

	<!-- Header -->
	<div data-position="fixed" data-role="header" data-tap-toggle="false">
		<a href="#propertiespage" data-direction="reverse" data-transition="slideup" class="ui-btn-left ui-btn ui-btn-inline ui-mini ui-corner-all ui-btn-icon-left ui-icon-back"><%=form.getString("MSG_BACK")%></a>
		<h1 style="display: inline;">
			<img onclick="$.mobile.silentScroll(0)" src="<%=Form.makeUrl(request, headerLogoUrl)%>" width="177" height="43"/>
		</h1>
	</div>

	<!--  Main content -->
	<div id="locationscontentdiv" align="left" data-role="content" >
		<dmf:label cssclass="pagelabel" nlsid='MSG_LOCATIONS'/>
		<ul id="locationslist" class="contentList" data-role="listview" data-inset="false" data-filter="false" data-theme="c" data-content-theme="c"></ul>
	</div>
</div> 

<div data-role="page" id="versionspage" data-theme="c" data-title="<%=form.getString("MSG_COMPONENT_TITLE")%>">

	<!-- Header -->
	<div data-position="fixed" data-role="header" data-tap-toggle="false">
		<a href="#propertiespage" data-direction="reverse" data-transition="slideup" class="ui-btn-left ui-btn ui-btn-inline ui-mini ui-corner-all ui-btn-icon-left ui-icon-back"><%=form.getString("MSG_BACK")%></a>
		<h1 style="display: inline;">
			<img onclick="$.mobile.silentScroll(0)" src="<%=Form.makeUrl(request, headerLogoUrl)%>" width="177" height="43"/>
		</h1>
	</div>

	<!--  Main content -->
	<div id="versionscontentdiv" align="left" data-role="content" >
		<dmf:label cssclass="pagelabel" nlsid='MSG_VERSIONS'/>
		<ul id="versionslist" class="contentList" data-role="listview" data-inset="false" data-filter="false" data-theme="c" data-content-theme="c"></ul>
	</div>
</div> 

<div data-role="page" id="optionsPage" data-theme="c" data-title="<%=form.getString("MSG_COMPONENT_TITLE")%>">

	<!-- Header -->
	<div data-position="fixed" data-role="header" data-tap-toggle="false">
		<a href="#foldercontentspage" data-direction="reverse" data-transition="slide" class="ui-btn-left ui-btn ui-btn-inline ui-mini ui-corner-all ui-btn-icon-left ui-icon-back"><%=form.getString("MSG_BACK")%></a>
		<h1 style="display: inline;">
			<img onclick="$.mobile.silentScroll(0)" src="<%=Form.makeUrl(request, headerLogoUrl)%>" width="177" height="43"/>
		</h1>
	</div>

	<!--  Main content -->
	<div align="left" data-role="content" >
		<dmf:label cssclass="pagelabel" nlsid='MSG_OPTIONS'/>
		<input type="checkbox" name="showfolderpath_checkbox" id="showfolderpath_checkbox" data-mini="true">
    	<label for="showfolderpath_checkbox"><%=form.getString("MSG_SHOW_FOLDERPATH")%></label>
		<input type="checkbox" name="showemptyattrs_checkbox" id="showemptyattrs_checkbox" data-mini="true">
    	<label for="showemptyattrs_checkbox"><%=form.getString("MSG_SHOW_EMPTY_ATTRIBUTES")%></label>
	</div>
</div> 


</dmf:form>
</dmf:body>
</dmf:html>