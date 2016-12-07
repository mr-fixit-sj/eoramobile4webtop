package com.eora.dctm.mobile4webtop.attributes;

import java.io.IOException;

import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;

import com.documentum.web.formext.control.docbase.DocbaseAttributeValue;
import com.documentum.web.formext.control.docbase.DocbaseObject;
import com.documentum.web.formext.control.docbase.RequiredDocbaseAttributeValueTag;

/**
 * 
 * @author S.Jonckheere
 * @since 1.0.0
 * 
 */
public class MobileRequiredDocbaseAttributeValueTag extends RequiredDocbaseAttributeValueTag {

	private static final long serialVersionUID = -8083889322070484699L;

	@Override
	protected void renderSingleAttribute(String formattedVal, String value, boolean isReadOnly, boolean hasCompleteList, JspWriter out) throws IOException, JspTagException {
		final DocbaseAttributeValue valueControl = (DocbaseAttributeValue) getControl();
		final boolean hasValueAssistance = valueControl.hasValueAssistance();
		final int dataType = valueControl.getDataType();
		
		final DocbaseObject docbaseObjControl = (DocbaseObject) valueControl.getForm().getControl( valueControl.getObject());
		docbaseObjControl.setConfigId( MobileAttributeHelper.DEFAULT_DOCBASEOBJECT_CONFIG_ID);

		if ( hasValueAssistance && !hasCompleteList && (dataType != 0 && dataType != 4)) {
			docbaseObjControl.setConfigId(MobileAttributeHelper.MOBILE_DOCBASEOBJECT_CONFIG_ID);
		}
		super.renderSingleAttribute(formattedVal, value, isReadOnly, hasCompleteList, out);
		
		docbaseObjControl.setConfigId(MobileAttributeHelper.MOBILE_DOCBASEOBJECT_CONFIG_ID);
	}
}
