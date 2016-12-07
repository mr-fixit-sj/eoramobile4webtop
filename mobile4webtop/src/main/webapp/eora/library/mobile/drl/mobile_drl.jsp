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
<%@page import="com.eora.dctm.mobile4webtop.drl.DRLComponent"%>
<%@page import="com.documentum.web.formext.config.ConfigService"%>
<%@page import="com.eora.dctm.mobile4webtop.reposbrowser.MobileReposBrowser"%>

<dmf:html>
<dmf:head>

	<dmf:webform />

	<dmf:title><dmf:label nlsid='MSG_COMPONENT_TITLE'/></dmf:title>
	<meta name="viewport" content="width=device-width, initial-scale=1">

	<script src='<%=Form.makeUrl(request, "/eora/include/jquery/jquery-1.11.1.min.js")%>'/></script>
	<script src='<%=Form.makeUrl(request, "/eora/include/jquery/mobile/1.4.5/jquery.mobile-1.4.5.min.js")%>'></script>

	<link rel="stylesheet" href='<%=Form.makeUrl(request,"/eora/include/mobile/drl/css/mobile_drl.css?version=1.0.0")%>'/>
	<link rel="stylesheet" href='<%=Form.makeUrl(request,"/eora/include/mobile/reposbrowser/css/jquery-mobile-custom-theme.min.css")%>'/>
	<link rel="stylesheet" href='<%=Form.makeUrl(request,"/eora/include/jquery/mobile/1.4.5/jquery.mobile-1.4.5.min.css")%>'/>
	<link rel="stylesheet" href='<%=Form.makeUrl(request,"/eora/include/jquery/mobile/1.4.5/jquery.mobile.structure-1.4.5.min.css")%>'/>
	<link rel="stylesheet" href='<%=Form.makeUrl(request,"/eora/include/jquery/mobile/1.4.5/jquery.mobile.inline-png-1.4.5.min.css")%>'/>

</dmf:head>
<dmf:body>
<dmf:form>
 
<%
	final DRLComponent form = (DRLComponent)pageContext.getAttribute(Form.FORM, PageContext.REQUEST_SCOPE);
	final String headerLogoUrl = ConfigService.getConfigLookup().lookupString("component[id=" + MobileReposBrowser.MOBILE_REPOSITORY_BROWSER_COMPONENT + "]." + MobileReposBrowser.CFG_HEADER_LOGO_URL, form.getContext());
%>

<script type="text/javascript">

	//Remove documentum theming
	$(document).ready(function () {
		$('link[href*="theme/documentum"]').remove();
	});

</script>

<!-- Define DRL Page -->
<div data-role="page" id="mobiledrlpage" data-theme="c">
	<!-- Panel -->
	<div data-role="panel" id="drlnavpanel" data-position="left" data-display="push" data-position-fixed="true">
		<br/>
		<a href='<%=form.getMobileReposBrowerUrl()%>' data-transition="slide" data-rel="external" rel="external" data-ajax="false" data-role="button" data-icon="action" data-mini="true"/><%=form.getString("MSG_OPEN_LOCATION")%></a>
		<br>
		<a href='<%=Form.makeUrl(request,"/action/logout")%>' data-transition="slide" data-rel="external" rel="external" data-ajax="false" data-role="button" data-icon="delete" data-mini="true"/><%=form.getString("MSG_LOGOUT")%></a>
	</div>

	<!-- Header -->
	<div data-position="fixed" data-role="header" data-tap-toggle="false">
		<a href="#drlnavpanel" class="drl-header-icon" data-role="button" data-icon="bars" data-transition="down" data-iconpos="notext"></a>
		<h1 style="display: inline;">
			<img onclick="$.mobile.silentScroll(0)" src="<%=Form.makeUrl(request, headerLogoUrl)%>" width="177" height="43"/>
		</h1>
	</div>

	<!--  Main content -->
	<div id="drlcontentdiv" align="left" data-role="content">
		<div>
			<table>
				<tbody>
					<tr>
						<td><dmfx:docbaseicon size='32' name="<%=DRLComponent.OBJ_ICON%>"/></td>
						<td><dmf:label name='object_name' cssclass='drl-text'/></td>
					</tr>
				</tbody>
			</table>
		</div>
		<dmf:panel name="<%=DRLComponent.DETAILS_PANEL%>">
			<ul data-role='listview' data-inset='true' data-filter='false' data-theme="a" data-content-theme="b"/>
				<li data-icon='false'>
					<p><dmf:label nlsid='MSG_VERSION' cssclass='drl-props-label'/></p>
					<p><dmf:label name='<%=DRLComponent.OBJ_VERSION%>' cssclass='drl-props-value'/></p>
				</li>
				<li data-icon='false'>
					<p><dmf:label nlsid='MSG_FORMAT' cssclass='drl-props-label'/></p>
					<p><dmfx:docformatvalueformatter><dmf:label name='<%=DRLComponent.OBJ_FORMAT%>' cssclass='drl-props-value'/></dmfx:docformatvalueformatter></p>
				</li>
				<li data-icon='false'>
					<p><dmf:label nlsid='MSG_SIZE' cssclass='drl-props-label'/></p>
					<p><dmfx:docsizevalueformatter><dmf:label name='<%=DRLComponent.CTRL_CONTENT_SIZE_LABEL%>' cssclass='drl-props-value'/></dmfx:docsizevalueformatter></p>
				</li>
			</ul>
			<div align="left" data-role="fieldcontain">
				<dmf:checkbox name='opencurrent' nlsid='MSG_OPENCURRENT' onclick='onOpenCurrent'/>
			</div>
<%
	String contentUrl = form.getContentUrl();
	if ( contentUrl != null ){
%>

		<a id="btn_view" href="<%=contentUrl%>" rel="external" data-rel="external" data-disabled="true" data-role="button"><%=form.getString("MSG_VIEW")%></a>
<%
	} else {
		form.setNoContentErrMessage();
	}
%>
		</dmf:panel>
		<dmf:panel name='<%=DRLComponent.ERROR_PANEL%>' visible='true'>
			<div align='left' data-role="fieldcontain" >
				<div>
					<dmf:label name="error_message" cssclass='drl-text'/>
				</div>
			</div>
		</dmf:panel>
		<br/>
	</div>

</div> 

</dmf:form>
</dmf:body>
</dmf:html>
