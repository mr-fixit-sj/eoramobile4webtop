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
<%@page import="com.documentum.web.formext.config.ConfigService"%>
<%@page import="com.documentum.web.formext.component.Component"%>
<%@page import="com.eora.dctm.mobile4webtop.reposbrowser.MobileReposBrowser"%>

<dmf:html>
<dmf:head>

	<dmf:title>Prompt</dmf:title>
	<meta name="viewport" content="width=device-width, initial-scale=1">

	<script src='<%=Form.makeUrl(request, "/eora/include/jquery/jquery-1.11.1.min.js")%>'/></script>
	<script src='<%=Form.makeUrl(request, "/eora/include/jquery/mobile/1.4.5/jquery.mobile-1.4.5.min.js")%>'></script>

	<link rel="stylesheet" href='<%=Form.makeUrl(request,"/eora/include/mobile/reposbrowser/css/jquery-mobile-custom-theme.min.css")%>'/>
	<link rel="stylesheet" href='<%=Form.makeUrl(request,"/eora/include/jquery/mobile/1.4.5/jquery.mobile-1.4.5.min.css")%>'/>
	<link rel="stylesheet" href='<%=Form.makeUrl(request,"/eora/include/jquery/mobile/1.4.5/jquery.mobile.structure-1.4.5.min.css")%>'/>
	<link rel="stylesheet" href='<%=Form.makeUrl(request,"/eora/include/jquery/mobile/1.4.5/jquery.mobile.inline-png-1.4.5.min.css")%>'/>

	<script type="text/javascript">
		$(document).on('pageinit', function() {
			//Make sure page is mobile scaled when Webtop is rendering framesets
			$("head", top.document).append("<meta name='viewport' content='width=device-width, initial-scale=1'>");

			//Remove documentum theming
			$('link[href*="theme/documentum"]').remove();
		});
	
	</script>

	<dmf:webform />
</dmf:head>
<dmf:body>
<dmf:form>

<%
	final Component form = (Component)pageContext.getAttribute(Form.FORM, PageContext.REQUEST_SCOPE);
	final String headerLogoUrl = ConfigService.getConfigLookup().lookupString("component[id=" + MobileReposBrowser.MOBILE_REPOSITORY_BROWSER_COMPONENT + "]." + MobileReposBrowser.CFG_HEADER_LOGO_URL, form.getContext());
%>

<div data-role="page" data-theme="c">
	<!-- Header -->
	<div data-position="fixed" data-role="header" data-tap-toggle="false">
		<div align="center">
			<img onclick="$.mobile.silentScroll(0)" src="<%=Form.makeUrl(request, headerLogoUrl)%>" width="177" height="45"/>
		</div>  
	</div>

	<!--  Main content -->
	<div data-role="content">
		<dmf:label name='title' cssclass='dialogTitle' nlsid='MSG_TITLE'/>
		<div data-role="fieldcontain">
			<dmf:image name='icon'/>
			<dmf:label name='message' nlsid='MSG_MESSAGE'/>
			<dmf:checkbox name='dontshowagain' nlsid='MSG_DONTSHOWAGAIN'/>
		</div>
		
		<dmf:button name='continue' cssclass="buttonLink" nlsid='MSG_CONTINUE' onclick='onContinue'/>
		<fieldset class="ui-grid-a">
    		<div class="ui-block-a"><dmf:button name='ok' cssclass="buttonLink" nlsid='MSG_OK' onclick='onOk'/></div>
    		<div class="ui-block-b"><dmf:button name='cancel' cssclass="buttonLink" nlsid='MSG_CANCEL' onclick='onCancel'/></div>
		</fieldset>
		<fieldset class="ui-grid-a">
    		<div class="ui-block-a"><dmf:button name='yes' cssclass="buttonLink" nlsid='MSG_YES' onclick='onYes'/></div>
    		<div class="ui-block-b"><dmf:button name='no' cssclass="buttonLink" nlsid='MSG_NO' onclick='onNo'/></div>
		</fieldset>
		<fieldset class="ui-grid-a">		
			<div class="ui-block-a"><dmf:button name='yestoall' cssclass="buttonLink" nlsid='MSG_YESTOALL' onclick='onYesToAll'/></div>
			<div class="ui-block-b"><dmf:button name='notoall' cssclass="buttonLink" nlsid='MSG_NOTOALL' onclick='onNoToAll'/></div>
		</fieldset>
	</div>
</dmf:form>
</dmf:body>
</dmf:html>