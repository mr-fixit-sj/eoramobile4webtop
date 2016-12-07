<%@ page contentType="text/html; charset=UTF-8"%>

<!--***********************************************************************-->
<!--                                                                       -->
<!-- Eora Mobile4Webtop                                                -->
<!--                                                                       -->
<!-- @author S.Jonckheere                             	  				   -->
<!-- @since 1.0.0                                           			   -->
<!--                                                                       -->
<!--***********************************************************************-->

<%@ taglib uri="/WEB-INF/tlds/dmform_1_0.tld" prefix="dmf"%>
<%@ taglib uri="/WEB-INF/tlds/dmformext_1_0.tld" prefix="dmfx"%>

<%@page import="com.documentum.web.form.Form"%>
<%@page import="com.eora.dctm.mobile4webtop.logoff.Logoff"%>
<%@page import="com.eora.dctm.mobile4webtop.reposbrowser.MobileReposBrowser"%>
<%@page import="com.documentum.web.formext.config.ConfigService"%>

<dmf:html>
<dmf:head>

	<dmf:esapiscriptinclude/>
	<dmf:title><dmf:label nlsid='MSG_TITLE'/></dmf:title>
	<meta name="viewport" content="width=device-width, initial-scale=1">

	<script src='<%=Form.makeUrl(request, "/eora/include/jquery/jquery-1.11.1.min.js")%>'/></script>
	<script src='<%=Form.makeUrl(request, "/eora/include/jquery/mobile/1.4.5/jquery.mobile-1.4.5.min.js")%>'></script>

	<link rel="stylesheet" href='<%=Form.makeUrl(request,"/eora/include/mobile/reposbrowser/css/jquery-mobile-custom-theme.min.css")%>'/>
	<link rel="stylesheet" href='<%=Form.makeUrl(request,"/eora/include/jquery/mobile/1.4.5/jquery.mobile-1.4.5.min.css")%>'/>
	<link rel="stylesheet" href='<%=Form.makeUrl(request,"/eora/include/jquery/mobile/1.4.5/jquery.mobile.structure-1.4.5.min.css")%>'/>
	<link rel="stylesheet" href='<%=Form.makeUrl(request,"/eora/include/jquery/mobile/1.4.5/jquery.mobile.inline-png-1.4.5.min.css")%>'/>
	
	<link rel="apple-touch-icon" href='<%=Form.makeUrl(request,"/eora/include/mobile/reposbrowser/images/ios_touch_icons/touch-icon-152x152.png")%>'>
	<link rel="apple-touch-icon" sizes="76x76" href='<%=Form.makeUrl(request,"/eora/include/mobile/reposbrowser/images/ios_touch_icons/touch-icon-76x76.png")%>'>
	<link rel="apple-touch-icon" sizes="120x120" href='<%=Form.makeUrl(request,"/eora/include/mobile/reposbrowser/images/ios_touch_icons/touch-icon-120x120.png")%>'>
	<link rel="apple-touch-icon" sizes="152x152" href='<%=Form.makeUrl(request,"/eora/include/mobile/reposbrowser/images/ios_touch_icons/touch-icon-152x152.png")%>'>

<script type="text/javascript">
	//Remove documentum theming
	$(document).ready(function () {
		$('link[href*="theme/documentum"]').remove();
	});

	function loginAgain(){
		var strUrl = addBrowserIdToURL("<%=request.getContextPath()%>");
		navigateToURL(strUrl, "logoff", window);
	}
</script>

	<dmf:webform />
</dmf:head>
<dmf:body>
<dmf:form>

<%
	final Logoff form = (Logoff)pageContext.getAttribute(Form.FORM, PageContext.REQUEST_SCOPE);
	final String headerLogoUrl = ConfigService.getConfigLookup().lookupString("component[id=" + MobileReposBrowser.MOBILE_REPOSITORY_BROWSER_COMPONENT + "]." + MobileReposBrowser.CFG_HEADER_LOGO_URL, form.getContext());
%>


<div data-role="page" data-theme="c">

	<!-- Header -->
	<div data-position="fixed" data-role="header" data-tap-toggle="false">
		<div align="center">
			<img onclick="$.mobile.silentScroll(0)" src="<%=Form.makeUrl(request, headerLogoUrl)%>" width="177" height="45"/>
		</div>  
	</div>
		
	<div data-role="content">
		<div>
			<dmf:label nlsid='MSG_TO_LOGIN_1' cssclass='logoff-labels'/>
		</div>
		<div>
			<dmf:label nlsid='MSG_TO_LOGIN_2' cssclass='logoff-labels'/>
		</div>
		<dmf:button onclick='loginAgain' nlsid="MSG_LOGIN_AGAIN" runatclient="true"/>
	</div>
</div>


</dmf:form>
</dmf:body>
</dmf:html>
<%
session.invalidate();
%>
