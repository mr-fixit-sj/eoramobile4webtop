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

<%@page import="java.util.Iterator"%>

<%@page import="com.documentum.web.common.LocaleService"%>
<%@page import="com.documentum.web.form.Form"%>
<%@page import="com.documentum.web.formext.config.ConfigService"%>
<%@page import="com.documentum.web.util.SafeHTMLString"%>

<%@page import="com.eora.dctm.mobile4webtop.attributes.MobileDocbaseSingleAttribute"%>
<%@page import="com.eora.dctm.mobile4webtop.attributes.MobileAttributeHelper"%>
<%@page import="com.eora.dctm.mobile4webtop.reposbrowser.MobileReposBrowser"%>


<dmf:html>
<dmf:head>
	<meta name="viewport" content="width=device-width, initial-scale=1">

	<script src='<%=Form.makeUrl(request, "/eora/include/jquery/jquery-1.11.1.min.js")%>'></script>
	<script src='<%=Form.makeUrl(request, "/eora/include/mobile/attributes/mobile_singleattribute.js")%>'></script>	
	<script src='<%=Form.makeUrl(request, "/eora/include/mobile/attributes/mobile_docbaseattributelist.js")%>'></script>
	<script src='<%=Form.makeUrl(request, "/eora/include/jquery/ui/1.10.4/jquery.ui.datepicker.min.js")%>'></script>
	<script src='<%=Form.makeUrl(request, "/eora/include/jquery/ui/1.10.4/jquery.ui.datepicker-i18n.js")%>'></script>
	<script src='<%=Form.makeUrl(request, "/eora/include/jquery/mobile/1.4.5/jquery.mobile-1.4.5.min.js")%>'></script>
    <script src='<%=Form.makeUrl(request, "/eora/include/jquery/mobile/extensions/jquery.mobile.datepicker-0.1.1.js")%>'></script>

	<dmf:webform />
	<dmf:title><dmf:label nlsid='MSG_SINGLE_ATTRIBUTE_TITLE'/></dmf:title>
	    	
	<link rel="stylesheet" href='<%=Form.makeUrl(request,"/eora/include/mobile/attributes/css/mobile_singleattribute.css?version=1.0.0")%>'/>
	<link rel="stylesheet" href='<%=Form.makeUrl(request,"/eora/include/mobile/reposbrowser/css/jquery-mobile-custom-theme.min.css")%>'/>
	<link rel="stylesheet" href='<%=Form.makeUrl(request,"/eora/include/jquery/mobile/1.4.5/jquery.mobile-1.4.5.min.css")%>'/>
	<link rel="stylesheet" href='<%=Form.makeUrl(request,"/eora/include/jquery/mobile/1.4.5/jquery.mobile.structure-1.4.5.min.css")%>'/>
	<link rel="stylesheet" href='<%=Form.makeUrl(request,"/eora/include/jquery/mobile/1.4.5/jquery.mobile.inline-png-1.4.5.min.css")%>'/>
	<link rel="stylesheet" href='<%=Form.makeUrl(request,"/eora/include/jquery/mobile/extensions/jquery.mobile.datepicker-0.1.1.css")%>'/>

</dmf:head>
<dmf:body>
<dmf:form>

<%
	final MobileDocbaseSingleAttribute form = (MobileDocbaseSingleAttribute)pageContext.getAttribute(Form.FORM, PageContext.REQUEST_SCOPE);
	final String headerLogoUrl = ConfigService.getConfigLookup().lookupString("component[id=" + MobileReposBrowser.MOBILE_REPOSITORY_BROWSER_COMPONENT + "]." + MobileReposBrowser.CFG_HEADER_LOGO_URL, form.getContext());
%>

	<script type="text/javascript">
		var userLocale = '<%=LocaleService.getLocale().getLanguage()%>';
		var datePattern = '<%=SafeHTMLString.escapeText(MobileAttributeHelper.getDateFormat())%>';
		var msg_date = '<%=SafeHTMLString.escapeText(form.getString("MSG_DATE"))%>';
	</script>


<div data-role="page" data-theme="c">
	<!-- Header -->
	<div data-position="fixed" data-role="header" data-tap-toggle="false">
		<div align="center">
			<img onclick="$.mobile.silentScroll(0)" src="<%=Form.makeUrl(request, headerLogoUrl)%>" width="177" height="45"/>
		</div>  
	</div>
	
		<!--  Main content -->
	<div data-role="content">
		<div>
		<dmf:label nlsid='MSG_SINGLE_ATTRIBUTE_TITLE' cssclass='pagelabel'/>
		</div>
		<div>
			<dmf:label cssclass="attrLabel" name="attrLabel"/>
		</div>		

		<div id="useroption" class="ui-field-contain">
			
			<fieldset data-role="controlgroup">
				<label>
					<dmf:radio name='openValueRadio' group='attributeValue' value='true' cssclass="openValueRadio"/><dmf:label nlsid='MSG_ENTER_VALUE'/>
				</label>
				<label>
					<dmf:radio name='varadio' group='attributeValue' value='false' cssclass="varadio" /><dmf:label nlsid='MSG_SELECT_FROM_LIST'/>
				</label>
			</fieldset>

		</div>
		<div id="openValueDiv">
			<dmf:panel name="datetimePanel">
				<dmf:datetime name="dateTimeAddEntry" runatclient="true" width="400" dateselector="calendar" timeselector="text" />
				<dmf:datetimevalidator name="dateTimeAddEntryValidator" controltovalidate="dateTimeAddEntry" nlsid="MSG_INVALID_DATE"/>
			</dmf:panel>
			<dmf:panel name="booleanPanel">
				<dmf:checkbox name="booleanAddEntry" runatclient="true" tooltipnlsid='MSG_ENTER_VALUE'/>
			</dmf:panel>
			<dmf:panel name="defaultPanel" >
				<dmf:text name="addEntry" tooltipnlsid='MSG_ENTER_VALUE' autocompleteenabled="false"/>
			</dmf:panel>
		</div>
		
		<div id="varadioDiv">
			
			<dmf:listbox name='va' tooltipnlsid='MSG_SELECT_FROM_LIST'>

			<%
				Iterator vaIt = MobileDocbaseSingleAttribute.vaRecords();
					while (vaIt.hasNext() == true){
						String strVaValue = (String)vaIt.next();
			%>
				<dmf:option value="<%= strVaValue %>" label="<%= strVaValue %>"/>
			<%
				}
			%>
			</dmf:listbox>
		</div>
		<fieldset class="ui-grid-a">
    		<div class="ui-block-a footerBlockA"><dmf:button name='ok' nlsid='MSG_OK' onclick='onClickOK' /></div>
    		<div class="ui-block-b footerBlockB"><dmf:button name='cancel' nlsid='MSG_CANCEL' onclick='onClickCancel' /></div>
		</fieldset>
	</div>
</dmf:form>
</dmf:body>
</dmf:html>
</div>

