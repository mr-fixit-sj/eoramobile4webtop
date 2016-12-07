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
<%@page import="com.eora.dctm.mobile4webtop.login.Login"%>
<%@page import="com.eora.dctm.mobile4webtop.reposbrowser.MobileReposBrowser"%>

<dmf:html>
<dmf:head>


	<dmf:title><dmf:label nlsid='MSG_TITLE'/></dmf:title>
	<meta name="viewport" content="width=device-width, initial-scale=1">

	<script src='<%=Form.makeUrl(request, "/eora/include/jquery/jquery-1.11.1.min.js")%>'/></script>
	<script src='<%=Form.makeUrl(request, "/eora/include/jquery/mobile/1.4.5/jquery.mobile-1.4.5.min.js")%>'></script>

	<link rel="stylesheet" href='<%=Form.makeUrl(request,"/eora/include/mobile/login/css/mobile_login.css")%>'/>
	<link rel="stylesheet" href='<%=Form.makeUrl(request,"/eora/include/mobile/reposbrowser/css/jquery-mobile-custom-theme.min.css")%>'/>
	<link rel="stylesheet" href='<%=Form.makeUrl(request,"/eora/include/jquery/mobile/1.4.5/jquery.mobile-1.4.5.min.css")%>'/>
	<link rel="stylesheet" href='<%=Form.makeUrl(request,"/eora/include/jquery/mobile/1.4.5/jquery.mobile.structure-1.4.5.min.css")%>'/>
	<link rel="stylesheet" href='<%=Form.makeUrl(request,"/eora/include/jquery/mobile/1.4.5/jquery.mobile.inline-png-1.4.5.min.css")%>'/>

	<link rel="apple-touch-icon" href='<%=Form.makeUrl(request,"/eora/include/mobile/reposbrowser/images/ios_touch_icons/touch-icon-152x152.png")%>'>
	<link rel="apple-touch-icon" sizes="76x76" href='<%=Form.makeUrl(request,"/eora/include/mobile/reposbrowser/images/ios_touch_icons/touch-icon-76x76.png")%>'>
	<link rel="apple-touch-icon" sizes="120x120" href='<%=Form.makeUrl(request,"/eora/include/mobile/reposbrowser/images/ios_touch_icons/touch-icon-120x120.png")%>'>
	<link rel="apple-touch-icon" sizes="152x152" href='<%=Form.makeUrl(request,"/eora/include/mobile/reposbrowser/images/ios_touch_icons/touch-icon-152x152.png")%>'>

	<script type="text/javascript">
		$(document).on("pagebeforecreate", function() {
			//Remove documentum theming
			$('link[href*="theme/documentum"]').remove();
		});	
	</script>

	<dmf:webform />
</dmf:head>
<dmf:body>
<dmf:form>

<%
	final Login form = (Login)pageContext.getAttribute(Form.FORM, PageContext.REQUEST_SCOPE);
	final String headerLogoUrl = ConfigService.getConfigLookup().lookupString("component[id=" + MobileReposBrowser.MOBILE_REPOSITORY_BROWSER_COMPONENT + "]." + MobileReposBrowser.CFG_HEADER_LOGO_URL, form.getContext());
%>

<div data-role="page" data-theme="c">

	<!-- Overlay panel -->
	<div data-role="panel" id="loginpanel" data-position="left" data-display="push" >
	
		<!-- Overlay panel -->
		<div data-role="fieldcontain">
				<div align='left'>
					<dmf:label nlsid='MSG_DOCBASE'/>
				</div>
				<dmf:datadropdownlist cssclass="login-ddl" name='<%=Login.CONTROL_DOCBASE%>' id='DocbaseName' tooltipnlsid='MSG_DOCBASE' runatclient='true' onselect="onSelectDocbaseFromDropDown">
					<dmf:dataoptionlist>
						<dmf:option datafield="docbase" labeldatafield="docbase"/>
					</dmf:dataoptionlist>
				</dmf:datadropdownlist>
		</div>

		<div class="ui-field-contain">
			<div align='left'>
				<dmf:label nlsid='MSG_LANGUAGE'/>
			</div>
			<dmf:datadropdownlist cssclass="login-ddl" id='<%=Login.CONTROL_LANGUAGE%>' name='<%=Login.CONTROL_LANGUAGE%>' onselect="onChangeLanguage" tooltipnlsid='MSG_LANGUAGE'>
				<dmf:dataoptionlist>
					<dmf:option datafield="locale" labeldatafield="language"/>
				</dmf:dataoptionlist>
			</dmf:datadropdownlist>			
		</div>
	    <dmf:panel name='<%=Login.CONTROL_CREDENTIAL_PANEL%>' id='<%=Login.CONTROL_CREDENTIAL_PANEL%>' renderifinvisible='true'>
			<dmf:checkbox cssclass="login-ddl" name='<%=Login.CONTROL_SAVE_CREDENTIAL%>' nlsid='MSG_SAVE_OPTION' visible='true'/>
		</dmf:panel>
	</div>


	<!-- Header -->
	<div data-position="fixed" data-role="header" data-tap-toggle="false">
		<a href="#loginpanel" class="login-header-icon" data-role="button" data-icon="bars" data-transition="down" data-iconpos="notext"></a>
		<h1 style="display: inline;">
			<img onclick="$.mobile.silentScroll(0)" src="<%=Form.makeUrl(request, headerLogoUrl)%>" width="177" height="43"/>
		</h1>
	</div>

	<!--  Main content -->
	<div data-role="content" align="center" id="contentConfirmation">
		<div data-role="fieldcontain">
			<div align='left'>
				<dmf:label nlsid='MSG_USERNAME'/>
			</div>
			<dmf:text autocompleteenabled="false" id='LoginUsername' name='<%=Login.CONTROL_USERNAME%>' size='10' tooltipnlsid='MSG_USERNAME' />
			<dmf:requiredfieldvalidator name='<%=Login.CONTROL_USERNAME_VALIDATOR%>' cssclass='login-redWarningText' controltovalidate='<%=Login.CONTROL_USERNAME%>' nlsid='<%=Login.MSG_USERNAME_REQUIRED%>' indicator=""/>
		</div>
		<div data-role="fieldcontain">
			<div align='left'>
				<dmf:label nlsid='MSG_PASSWORD'/>
			</div>
			<dmf:password cssclass="ui-body-a" id='<%=Login.CONTROL_PWD %>' name='<%=Login.CONTROL_PWD%>' size='10' defaultonenter='true' tooltipnlsid='MSG_PASSWORD' />
			<dmf:requiredfieldvalidator  name='<%=Login.CONTROL_PWD_VALIDATOR%>' cssclass='login-redWarningText' controltovalidate='<%=Login.CONTROL_PWD%>' nlsid='<%=Login.MSG_PWD_REQUIRED%>' indicator=""/>
		</div>

		<dmf:panel name='<%=Login.CONTROL_ERRMSGPANEL%>'>
		<div data-role="fieldcontain">
			<div class='loginerrorspacing' align="left">
				<dmf:label name='<%=Login.CONTROL_ERRMSG%>' id='<%=Login.CONTROL_ERRMSG%>' cssclass='login-redWarningText' />
			</div>
		</div>
		</dmf:panel>
		<dmf:button name='<%=Login.CONTROL_LOGINBUTTON%>' nlsid='MSG_LOGIN' onclick='onLogin' default='true' />
	    <dmf:panel name='<%=Login.CONTROL_CREDENTIAL_PANEL%>' id='<%=Login.CONTROL_CREDENTIAL_PANEL%>' renderifinvisible='true'>
			<dmf:checkbox name='<%=Login.CONTROL_SAVE_CREDENTIAL%>' nlsid='MSG_SAVE_OPTION' visible='false'/>
		</dmf:panel>
	</div>
</div>
</dmf:form>
</dmf:body>
</dmf:html>