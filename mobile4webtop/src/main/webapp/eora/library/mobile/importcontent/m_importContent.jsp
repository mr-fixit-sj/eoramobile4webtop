<%@ page contentType="text/html; charset=UTF-8" %>

<!--***********************************************************************-->
<!--                                                                       -->
<!-- Eora Mobile4Webtop                                                -->
<!--                                                                       -->
<!-- @author S.Jonckheere                             	  				   -->
<!-- @since 1.0.0                                           			   -->
<!--                                                                       -->
<!--***********************************************************************-->

<%@ taglib uri="/WEB-INF/tlds/dmform_1_0.tld" prefix="dmf" %>
<%@ taglib uri="/WEB-INF/tlds/dmformext_1_0.tld" prefix="dmfx" %>

<%@ page import="com.documentum.web.form.Form"%>
<%@ page import="com.eora.dctm.mobile4webtop.attributes.MobileAttributeHelper"%>
<%@ page import="com.eora.dctm.mobile4webtop.importcontent.MobileImportContent"%>

	<ul id="headerimportattrs" data-role='listview' data-inset='true' data-filter='false' data-theme='a'>
		<li class='ui-li-static-props' data-icon='false'>
			<div class='props-label'>
				<dmf:label nlsid="MSG_NAME"/>
			</div>
			<div class='props-value'>
				<dmf:text name="attribute_object_name" id="attribute_object_name" autocompleteenabled="false"/>
				<dmf:requiredfieldvalidator name="validator" controltovalidate="attribute_object_name" nlsid="MSG_MUST_HAVE_NAME"/>
				<dmf:utf8stringlengthvalidator name="attribute_object_name_lengthValidator" controltovalidate="attribute_object_name" maxbytelength="255" nlsid="MSG_NAME_TOO_LONG"/>
				<dmfx:docbaseattributeproxy name="docbaseobjectnameproxy" object="docbaseObj" controltorepresent="attribute_object_name"  attribute="object_name"/>
			</div>
		</li>
		<li class='ui-li-static-props' data-icon='false'>
			<div class='props-label'>
				<dmf:label nlsid="MSG_TYPE"/>
			</div>
			<div class='props-value'>
				<dmf:datadropdownlist name="objectTypeList" onselect="onSelectType" tooltipnlsid="MSG_TYPE">
					<dmf:dataoptionlist>
						<dmf:option datafield="type_name" labeldatafield="label_text"/>
					</dmf:dataoptionlist>
				</dmf:datadropdownlist>
			</div>
		</li>
		<li class='ui-li-static-props' data-icon='false'>
			<div class='props-label'>
				<dmf:label nlsid="MSG_FORMAT"/>
			</div>
			<div class='props-value'>
				<dmf:datadropdownlist name="formatList" tooltipnlsid="MSG_FORMAT">
					<dmf:dataoptionlist>
						<dmf:option datafield="name" labeldatafield="description"/>
					</dmf:dataoptionlist>
				</dmf:datadropdownlist>
				<dmfx:docbaseattributeproxy name="docbaseobjectformatproxy" object="docbaseObj" controltorepresent="formatList"  attribute="a_content_type"/>
				<dmf:panel name="unknownFormatInfoLabelPanel" >
					<dmf:label name='unknownFormatWarningLabel' nlsid='MSG_ENFORCE_SELECT_FORMAT' cssclass="validatorMessageStyle"/>
				</dmf:panel>
			</div>
		</li>	
	</ul>
	
	<div id="validator">
		<dmfx:docbaseobject name="docbaseObj" configid="<%=MobileAttributeHelper.MOBILE_DOCBASEOBJECT_CONFIG_ID %>"/>
	</div>
	
	<%
		final MobileImportContent component = (MobileImportContent)pageContext.getAttribute(Form.FORM, PageContext.REQUEST_SCOPE);
		if ( component.hasMoreThanOneCategory() ){
	%>
		<div id="categorySet" align="left" data-role="collapsible-set" data-theme="c" data-content-theme="a" data-inset="false">
       		
        	<div id="removeme"><ul>
		        	<dmfx:docbaseattributelist name="attrlist" object="docbaseObj" attrconfigid="<%=MobileAttributeHelper.MOBILE_IMPORT_ATTRLIST_CONFIG_ID%>" inlinerefresh="true"
		        		showcategorynames="true" 
		        		categorypre="</ul></div><div align='left' data-role='collapsible' data-mini='true' data-collapsed='false'><h2>"
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
		<div id="categorySet" align="left" data-role="collapsible-set" data-theme="c" data-content-theme="a" data-inset="true">
        	<div align='left' data-role='collapsible' data-mini='true' data-collapsed='false'>
        		<h2><%=component.getFirstCategoryLabel()%></h2>
        		
        		<ul data-role='listview' data-filter='false' data-theme='a'>
        		
		        	<!-- the Docbase object -->
		        	<dmfx:docbaseattributelist name="attrlist" object="docbaseObj" attrconfigid="<%=MobileAttributeHelper.MOBILE_IMPORT_ATTRLIST_CONFIG_ID%>" inlinerefresh="true"
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
