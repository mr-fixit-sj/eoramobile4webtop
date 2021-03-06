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

<%@ page import="com.documentum.web.common.LocaleService"%>
<%@ page import="com.documentum.web.form.Form"%>
<%@ page import="com.documentum.web.formext.component.Component"%>
<%@ page import="com.documentum.web.formext.component.DialogContainer"%>
<%@ page import="com.documentum.web.formext.config.ConfigService"%>
<%@ page import="com.documentum.web.util.SafeHTMLString"%>
<%@ page import="com.eora.dctm.mobile4webtop.attributes.MobileAttributeHelper"%>
<%@ page import="com.eora.dctm.mobile4webtop.reposbrowser.MobileReposBrowser"%>


<dmf:html>
<dmf:head>

	<meta name="viewport" content="width=device-width, initial-scale=1">
	<dmf:webform/>
	<dmf:title><dmf:label nlsid='MSG_TITLE'/>&nbsp;:&nbsp;<dmf:label nlsid='MSG_OBJECT'/></dmf:title>

	<script src='<%=Form.makeUrl(request, "/eora/include/jquery/jquery-1.11.1.min.js")%>'/></script>
	<script src='<%=Form.makeUrl(request, "/eora/include/mobile/importcontent/mobile_importcontent.js")%>'></script>
	<script src='<%=Form.makeUrl(request, "/eora/include/mobile/attributes/mobile_docbaseattributelist.js")%>'></script>
	<script src='<%=Form.makeUrl(request, "/eora/include/jquery/ui/1.10.4/jquery.ui.datepicker.min.js")%>'></script>
	<script src='<%=Form.makeUrl(request, "/eora/include/jquery/ui/1.10.4/jquery.ui.datepicker-i18n.js")%>'></script>
	<script src='<%=Form.makeUrl(request, "/eora/include/jquery/mobile/1.4.5/jquery.mobile-1.4.5.min.js")%>'></script>
    <script src='<%=Form.makeUrl(request, "/eora/include/jquery/mobile/extensions/jquery.mobile.datepicker-0.1.1.js")%>'></script>
	
	<link rel="stylesheet" href='<%=Form.makeUrl(request,"/eora/include/mobile/importcontent/css/mobile_importcontent.css?version=1.0.0")%>'/>
	<link rel="stylesheet" href='<%=Form.makeUrl(request,"/eora/include/mobile/reposbrowser/css/jquery-mobile-custom-theme.min.css")%>'/>
	<link rel="stylesheet" href='<%=Form.makeUrl(request,"/eora/include/jquery/mobile/1.4.5/jquery.mobile-1.4.5.min.css")%>'/>
	<link rel="stylesheet" href='<%=Form.makeUrl(request,"/eora/include/jquery/mobile/1.4.5/jquery.mobile.structure-1.4.5.min.css")%>'/>
	<link rel="stylesheet" href='<%=Form.makeUrl(request,"/eora/include/jquery/mobile/1.4.5/jquery.mobile.inline-png-1.4.5.min.css")%>'/>
	<link rel="stylesheet" href='<%=Form.makeUrl(request,"/eora/include/jquery/mobile/extensions/jquery.mobile.datepicker-0.1.1.css")%>'/>

	<script type="text/javascript">
		var userLocale = '<%=LocaleService.getLocale().getLanguage()%>';
		var datePattern = '<%=SafeHTMLString.escapeText( MobileAttributeHelper.getDateFormat())%>';
		var msg_date = '<%=SafeHTMLString.escapeText( MobileAttributeHelper.getDefaultEmptyDateNlsString()) %>';

		$(document).on("pagebeforecreate", function() {
			//Remove documentum theming
			$('link[href*="theme/documentum"]').remove();
		});	
	</script>
	
</dmf:head>
<dmf:body>
<dmf:form>

<%
	final Form form = (Form)pageContext.getAttribute(Form.FORM, PageContext.REQUEST_SCOPE);
	final String headerLogoUrl = ConfigService.getConfigLookup().lookupString("component[id=" + MobileReposBrowser.MOBILE_REPOSITORY_BROWSER_COMPONENT + "]." + MobileReposBrowser.CFG_HEADER_LOGO_URL, ((Component)form).getContext());
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
		<p><dmf:label cssclass="pagelabel" nlsid='MSG_TITLE'/></p>
		<p>
			<dmfx:docbaseicon name='folderIcon' size='16'/><dmf:label cssclass='dialogFileName' nlsid='MSG_OBJECT'/>
		</p>
		<dmfx:containerinclude/>
	</div>
	 
	<div data-role="footer" data-position="fixed">
		<dmf:button name='prev' cssclass="buttonLink" nlsid='MSG_PREV' onclick='onPrev' tooltipnlsid='MSG_PREV_TIP'/>
		<dmf:button name='next' cssclass='buttonLink' nlsid='MSG_NEXT' onclick='onNext' tooltipnlsid='MSG_NEXT_TIP'/>
		<dmf:button name='ok' cssclass="buttonLink" nlsid='MSG_OK' onclick='onOk' tooltipnlsid='MSG_OK_TIP'/>
		<dmf:button name='cancel' cssclass='buttonLink' nlsid='MSG_CANCEL' onclick='onCancel' tooltipnlsid='MSG_CANCEL_TIP'/>
		<dmf:button name='close' cssclass="buttonLink" nlsid='MSG_CLOSE' onclick='onClose' tooltipnlsid='MSG_CLOSE_TIP'/>
	</div>
</div>
</dmf:form>
</dmf:body>
</dmf:html>
