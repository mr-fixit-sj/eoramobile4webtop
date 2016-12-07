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
<%@ page import="java.util.Iterator"%>

<%@ taglib uri="/WEB-INF/tlds/dmform_1_0.tld" prefix="dmf"%>
<%@ taglib uri="/WEB-INF/tlds/dmformext_1_0.tld" prefix="dmfx"%>

<%@page import="com.documentum.web.common.LocaleService"%>
<%@page import="com.documentum.web.form.Form"%>
<%@page import="com.documentum.web.formext.config.ConfigService"%>
<%@page import="com.documentum.web.util.SafeHTMLString"%>

<%@page import="com.documentum.web.formext.config.ConfigService"%>
<%@ page import="com.eora.dctm.mobile4webtop.attributes.MobileDocbaseRepeatingAttribute" %>
<%@page import="com.eora.dctm.mobile4webtop.attributes.MobileAttributeHelper"%>
<%@page import="com.eora.dctm.mobile4webtop.reposbrowser.MobileReposBrowser"%>


<dmf:html>
<dmf:head>

	<meta name="viewport" content="width=device-width, initial-scale=1">

	<script src='<%=Form.makeUrl(request, "/eora/include/jquery/jquery-1.11.1.min.js")%>'></script>
	<script src='<%=Form.makeUrl(request, "/eora/include/mobile/attributes/mobile_repeatingattribute.js")%>'></script>
	<script src='<%=Form.makeUrl(request, "/eora/include/jquery/ui/1.10.4/jquery-ui-1.10.4.min.js")%>'></script>
	<script src='<%=Form.makeUrl(request, "/eora/include/jquery/ui/1.10.4/jquery.ui.datepicker-i18n.js")%>'></script>
	
	<script src='<%=Form.makeUrl(request, "/eora/include/jquery/ui/extensions/jquery.ui.touch-punch-0.2.3.min.js")%>'></script>
	<script src='<%=Form.makeUrl(request, "/eora/include/jquery/mobile/1.4.5/jquery.mobile-1.4.5.min.js")%>'></script>
	<script src='<%=Form.makeUrl(request, "/eora/include/jquery/mobile/extensions/jquery.mobile.datepicker-0.1.1.js")%>'></script>
	
	<dmf:webform />
	<dmf:title><dmf:label nlsid='MSG_COMPONENT_TITLE'/></dmf:title>
		
	<link rel="stylesheet" href='<%=Form.makeUrl(request,"/eora/include/mobile/attributes/css/mobile_repeatingattribute.css?version=1.0.0")%>'/>
	<link rel="stylesheet" href='<%=Form.makeUrl(request,"/eora/include/mobile/reposbrowser/css/jquery-mobile-custom-theme.min.css")%>'/>
	<link rel="stylesheet" href='<%=Form.makeUrl(request,"/eora/include/jquery/mobile/1.4.5/jquery.mobile-1.4.5.min.css")%>'/>
	<link rel="stylesheet" href='<%=Form.makeUrl(request,"/eora/include/jquery/mobile/1.4.5/jquery.mobile.structure-1.4.5.min.css")%>'/>
	<link rel="stylesheet" href='<%=Form.makeUrl(request,"/eora/include/jquery/mobile/1.4.5/jquery.mobile.inline-png-1.4.5.min.css")%>'/>
	<link rel="stylesheet" href='<%=Form.makeUrl(request,"/eora/include/jquery/mobile/extensions/jquery.mobile.datepicker-0.1.1.css")%>'/>
	
	

</dmf:head>
<dmf:body>
<dmf:form>

<%
	final MobileDocbaseRepeatingAttribute form = (MobileDocbaseRepeatingAttribute)pageContext.getAttribute(Form.FORM, PageContext.REQUEST_SCOPE);
	final String headerLogoUrl = ConfigService.getConfigLookup().lookupString("component[id=" + MobileReposBrowser.MOBILE_REPOSITORY_BROWSER_COMPONENT + "]." + MobileReposBrowser.CFG_HEADER_LOGO_URL, form.getContext());
%>

<script type="text/javascript">
repeatingAttributesFormId = "<%=form.getElementName()%>";
var userLocale = '<%=LocaleService.getLocale().getLanguage()%>';
var datePattern = '<%=SafeHTMLString.escapeText(MobileAttributeHelper.getDateFormat())%>';
var msg_date = '<%=SafeHTMLString.escapeText(form.getString("MSG_DATE"))%>';
</script>


<div data-role="page" id="mobilerepeatingattributespage" data-theme="c">

	<!-- Header -->
	<div data-position="fixed" data-role="header" data-tap-toggle="false">
		<div align="center">
			<img onclick="$.mobile.silentScroll(0)" src="<%=Form.makeUrl(request, headerLogoUrl)%>" width="177" height="45"/>
		</div>  
	</div>
     
    <!--  Main content -->
	<div align="left" data-role="content">

		<% 
			String isOpenList = form.isOpenList(); 
		%>
	
		<dmf:label name="attrLabel" cssclass='pagelabel'/>
	
		<dmf:label name="attrError" cssclass='validatorMessageStyle'/>
	
		<dmf:panel name="valueAssistancePanel">
		
			<dmf:panel name="useroptionPanel" visible='<%=isOpenList%>'>
			<div id="useroption" class="ui-field-contain">
			<fieldset data-role="controlgroup">
				<label>
					<dmf:radio name='openValueRadio' group='attributeValue' value='false' cssclass="openValueRadio"/><dmf:label nlsid='MSG_ENTER_VALUE'/>
				</label>
				<label>
					<dmf:radio name='varadio' group='attributeValue' value='true' cssclass="varadio" /><dmf:label nlsid='MSG_SELECT_FROM_LIST'/>
				</label>
			</fieldset>
			</div>
			</dmf:panel>
			
			<div id="openValueDiv">
				<dmf:panel name="datetimePanel">
					<dmf:datetime name="dateTimeAddEntry" runatclient="true" width="400" dateselector="calendar" timeselector="text" />
					<dmf:datetimevalidator name="dateTimeAddEntryValidator" controltovalidate="dateTimeAddEntry" nlsid="MSG_INVALID_DATE"/>
				</dmf:panel>
				<dmf:panel name="booleanPanel">
					<dmf:checkbox name="booleanAddEntry" id="booleanAddEntry" runatclient="true" tooltipnlsid='MSG_ENTER_VALUE'/>
				</dmf:panel>
				<dmf:panel name="defaultPanel">
					<dmf:text name="addEntry" id="addEntry" defaultonenter='true' tooltipnlsid='MSG_ENTER_VALUE' autocompleteid="ac_addEntry"/>
				</dmf:panel>
			</div>
			
			<div id="varadioDiv">
				
				<dmf:listbox name='va' id="va" tooltipnlsid='MSG_SELECT_FROM_LIST'>
	
				<%					
					Iterator vaIt = form.vaRecords();
						while (vaIt.hasNext() == true)
						{
							String strVaValue = (String) vaIt.next();
				%>
							<dmf:option value="<%= strVaValue %>" label="<%= strVaValue %>"/>
				<%
						}
				%>
				</dmf:listbox>
			</div>
				
		</dmf:panel>

			<dmf:panel name="noVAdatetimePanel">
				<dmf:datetime name="dateTimeAddEntry" id="noVAdateTimeAddEntry" runatclient="true" width="400" dateselector="calendar" timeselector="text" />
				<dmf:datetimevalidator name="dateTimeAddEntryValidator" controltovalidate="dateTimeAddEntry" nlsid="MSG_INVALID_DATE"/>
			</dmf:panel>			
			<dmf:panel name="noVAbooleanPanel">
				<dmf:checkbox name="noVAbooleanAddEntry" id="noVAbooleanAddEntry" runatclient="true" tooltipnlsid="MSG_ENTER_VALUE"/>
			</dmf:panel>
			<dmf:panel name="noVAdefaultPanel">
				<dmf:text name="noVAaddEntry" id="noVAaddEntry" defaultonenter='true' tooltipnlsid="MSG_ENTER_VALUE" autocompleteid="false"/>
			</dmf:panel>
			
			<dmf:button id="addbutton" name="addbutton" tooltipnlsid='MSG_ADD_BUTTON_MOUSEOVER_INFO' runatclient="true" onclick='onClickAdd' nlsid='MSG_ADD' cssclass='buttonLink, listBoxButton' default="true"/>
			
			<ul id="repeatingAttrList" data-role="listview" data-inset="true" data-filter="true" data-theme="c" data-content-theme="c">						
			</ul>

		
		<fieldset class="ui-grid-a">
    		<div class="ui-block-a"><dmf:button name='ok' nlsid='MSG_OK' onclick='onClickOK' /></div>
    		<div class="ui-block-b"><dmf:button name='cancel' nlsid='MSG_CANCEL' onclick='onClickCancel' /></div>
		</fieldset>
		
		<!-- delete popup -->
	    <div id="confirm" class="ui-content" data-role="popup" data-theme="c">
	        <p id="question"><%=form.getString("MSG_OPTION_PROMPT")%></p>
	        <div class="ui-grid-a">
	            <div class="ui-block-a">
	                <a id="edit" data-role="button" data-mini="true" data-shadow="false" data-theme="b" data-rel="back"><%=form.getString("MSG_EDIT")%></a>
	            </div>
	            <div class="ui-block-b">
	                <a id="delete" data-role="button" data-mini="true" data-shadow="false" data-theme="b" data-rel="back"><%=form.getString("MSG_DELETE")%></a>
	            </div>	            
	            <div class="ui-block-c">
	                <a id="cancel" data-role="button" data-mini="true" data-shadow="false" data-theme="b" data-rel="back"><%=form.getString("MSG_CANCEL")%></a>
	            </div>
	        </div>
    	</div>
	</div>

</div>
<dmf:hidden name="hiddenText" id="hiddenText"/>
<dmf:hidden name="hiddenControl" id="hiddenControl"/>
<dmf:hidden name="hiddenVaControl" id="hiddenVaControl"/>
<script>setTimeout('initListbox()', 10);</script>
</dmf:form>
</dmf:body>
</dmf:html>



