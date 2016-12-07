<%@ page contentType="text/html; charset=UTF-8"%>
<!--***********************************************************************-->
<!--                                                                       -->
<!-- Eora Mobile4Webtop                                                	   -->
<!--                                                                       -->
<!-- @author S.Jonckheere                             	  				   -->
<!-- @since 1.0.0                                           			   -->
<!--                                                                       -->
<!--***********************************************************************-->

<%@ taglib uri="/WEB-INF/tlds/dmform_1_0.tld" prefix="dmf"%>
<%@ taglib uri="/WEB-INF/tlds/dmformext_1_0.tld" prefix="dmfx"%>
 
<%@page import="com.documentum.web.common.LocaleService"%>
<%@page import="com.documentum.web.form.Form"%>
<%@page import="com.documentum.web.formext.config.ConfigService"%>
<%@page import="com.documentum.web.util.SafeHTMLString"%>

<%@page import="com.eora.dctm.mobile4webtop.attributes.MobileEditAttributes"%>
<%@page import="com.eora.dctm.mobile4webtop.attributes.MobileAttributeHelper"%>
<%@page import="com.eora.dctm.mobile4webtop.reposbrowser.MobileReposBrowser"%>

<dmf:html>
<dmf:head>

	<dmf:webform />

	<dmf:title><dmf:label nlsid='MSG_COMPONENT_TITLE'/></dmf:title>
	<meta name="viewport" content="width=device-width, initial-scale=1">

	<script src='<%=Form.makeUrl(request, "/eora/include/jquery/jquery-1.11.1.min.js")%>'></script>
	<script src='<%=Form.makeUrl(request, "/eora/include/mobile/attributes/mobile_edit_attributes.js")%>'></script>
	<script src='<%=Form.makeUrl(request, "/eora/include/mobile/attributes/mobile_docbaseattributelist.js")%>'></script>
	<script src='<%=Form.makeUrl(request, "/eora/include/jquery/ui/1.10.4/jquery.ui.datepicker.min.js")%>'></script>
	<script src='<%=Form.makeUrl(request, "/eora/include/jquery/ui/1.10.4/jquery.ui.datepicker-i18n.js")%>'></script>
	<script src='<%=Form.makeUrl(request, "/eora/include/jquery/mobile/1.4.5/jquery.mobile-1.4.5.min.js")%>'></script>
    <script src='<%=Form.makeUrl(request, "/eora/include/jquery/mobile/extensions/jquery.mobile.datepicker-0.1.1.js")%>'></script>

	<link rel="stylesheet" href='<%=Form.makeUrl(request,"/eora/include/mobile/attributes/css/mobile_edit_attributes.css?version=1.0.0")%>'/>
	<link rel="stylesheet" href='<%=Form.makeUrl(request,"/eora/include/mobile/reposbrowser/css/jquery-mobile-custom-theme.min.css")%>'/>
	<link rel="stylesheet" href='<%=Form.makeUrl(request,"/eora/include/jquery/mobile/1.4.5/jquery.mobile-1.4.5.min.css")%>'/>
	<link rel="stylesheet" href='<%=Form.makeUrl(request,"/eora/include/jquery/mobile/1.4.5/jquery.mobile.structure-1.4.5.min.css")%>'/>
	<link rel="stylesheet" href='<%=Form.makeUrl(request,"/eora/include/jquery/mobile/1.4.5/jquery.mobile.inline-png-1.4.5.min.css")%>'/>
	<link rel="stylesheet" href='<%=Form.makeUrl(request,"/eora/include/jquery/mobile/extensions/jquery.mobile.datepicker-0.1.1.css")%>'/>
 
</dmf:head>
<dmf:body>
<dmf:form>

<%
	final MobileEditAttributes form = (MobileEditAttributes)pageContext.getAttribute(Form.FORM, PageContext.REQUEST_SCOPE);
	final String headerLogoUrl = ConfigService.getConfigLookup().lookupString("component[id=" + MobileReposBrowser.MOBILE_REPOSITORY_BROWSER_COMPONENT + "]." + MobileReposBrowser.CFG_HEADER_LOGO_URL, form.getContext());
%>

	<script type="text/javascript">
		var userLocale = '<%=LocaleService.getLocale().getLanguage()%>';
		var datePattern = '<%=SafeHTMLString.escapeText( MobileAttributeHelper.getDateFormat())%>';
		var msg_date = '<%=SafeHTMLString.escapeText( MobileAttributeHelper.getDefaultEmptyDateNlsString()) %>';
		var returnToViewProperties = <%=form.isReturnToViewProperties()%>;
	</script>

<div data-role="page" id="mobileeditattributespage" data-theme="c">

	<!-- Header -->
	<div data-position="fixed" data-role="header" data-tap-toggle="false">
		<h1 style="display: inline;">
			<img onclick="$.mobile.silentScroll(0)" src="<%=Form.makeUrl(request,headerLogoUrl)%>" width="177" height="43"/>
		</h1>
	</div>
     
    <!--  Main content -->
	<div align="left" data-role="content">
		<dmf:label cssclass="pagelabel" nlsid='MSG_COMPONENT_TITLE'/>


		<div class="props-header-info">
			<div class="props-header-icon">
				<dmfx:docbaseicon cssclass='ui-li-icon' size='32' name="icon" />
				<dmfx:docbaselockicon cssclass='lockIconOverlay' name='lockIcon' size='16'/>
			</div>
			<div>
				<p id="props_header_objectname" class='props-header-label'><dmf:label name='object_name'/></p>
			</div>		
		</div>

		<div id="validator">
			<dmfx:docbaseobject name="obj" configid="<%=MobileAttributeHelper.MOBILE_DOCBASEOBJECT_CONFIG_ID%>"/>
		</div>
		
		<%
			if ( form.hasMoreThanOneCategory() ){
		%>
		<div id="categorySet" align="left" data-role="collapsible-set" data-theme="c" data-content-theme="a" data-inset="false">
       		
        	<div id="removeme"><ul>
		        	<dmfx:docbaseattributelist name="attrlist" object="obj" attrconfigid="<%=MobileAttributeHelper.MOBILE_EDIT_ATTRLIST_CONFIG_ID%>" inlinerefresh="true"
		        		showcategorynames="true" 
		        		categorypre="</ul></div><div align='left' class='categoryCollapsible' data-role='collapsible' data-mini='true' data-collapsed='true'><h2>"
		        		categorypost="</h2><ul data-role='listview' data-filter='false' data-theme='a'>"
		        		pre="<li class='ui-li-static-props' data-icon='false'><div class='props-label'>"
		        		col1="</div><div class='props-value'>"
		        		col2=""
		        		separator=""
		        		post="</div></li>"
		        		morelinkpre="<li class='ui-li-static-props' data-icon='false'>"
		        		morelinkpost="</li>"/>
			</ul></div>
			</div>
		<%
			} else {
				// When type only has one category (tab), the docbaseattributelist does not show label name
				// so we have to set it manually
		%>
		<div id="categorySet" align="left" data-role="collapsible-set" data-theme="c" data-content-theme="a" data-inset="false">
        	<div align='left' data-role='collapsible' data-mini='true' data-collapsed='false'>
        		<h2><%=form.getFirstCategoryLabel()%></h2>
        		
        		<ul data-role='listview' data-filter='false' data-theme='a'>
        		
		        	<!-- the Docbase object -->
		        	<dmfx:docbaseattributelist name="attrlist" object="obj" attrconfigid="<%=MobileAttributeHelper.MOBILE_EDIT_ATTRLIST_CONFIG_ID%>" inlinerefresh="true"
		        		showcategorynames="true" 
		        		categorypre=""
		        		categorypost=""
		        		pre="<li class='ui-li-static-props' data-icon='false'><div class='props-label'>"
		        		col1="</div><div class='props-value'>"
		        		col2=""
		        		separator=""
		        		post="</div></li>"
		        		morelinkpre="<li class='ui-li-static-props' data-icon='false'>"
		        		morelinkpost="</li>"/>
		        </ul>
			</div>
		</div>
		<%
			}
		 %>
	</div>
	 <!--  Footer -->
	<div data-role="footer" data-position="fixed" data-tap-toggle="false" class="ui-grid-a">
		<div class="ui-block-a footerBlock">
			<dmf:button name='ok' cssclass="footerButton" nlsid='MSG_OK' onclick='onOk'/>
		</div>
		<div class="ui-block-b footerBlock">
			<dmf:button name='cancel' cssclass="footerButton" nlsid='MSG_CANCEL' runatclient="true" onclick='returnToPropertiesPage'/>
		</div>
	</div>
	
	
</div> 
</dmf:form>
</dmf:body>
</dmf:html>



