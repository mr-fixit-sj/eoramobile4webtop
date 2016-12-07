package com.eora.dctm.mobile4webtop.attributes;

import java.io.IOException;

import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;

import com.documentum.web.formext.control.docbase.DocbaseAttributeValue;
import com.documentum.web.formext.control.docbase.DocbaseAttributeValueTag;
import com.documentum.web.formext.control.docbase.DocbaseObject;

/**
 * 
 * @author S.Jonckheere
 * @since 1.0.0
 * 
 */
public class MobileSingleAttributeValueTag extends DocbaseAttributeValueTag {
	

	private static final long serialVersionUID = 6584891110297023370L;

	@Override
	protected void renderSingleAttribute(String formattedVal, String value, boolean isReadOnly, boolean hasCompleteList, JspWriter out) throws IOException, JspTagException {
		final DocbaseAttributeValue valueControl = (DocbaseAttributeValue) getControl();
		final boolean hasValueAssistance = valueControl.hasValueAssistance();
		final int dataType = valueControl.getDataType();
		
		final DocbaseObject docbaseObjControl = (DocbaseObject) valueControl.getForm().getControl( valueControl.getObject());
		docbaseObjControl.setConfigId(MobileAttributeHelper.DEFAULT_DOCBASEOBJECT_CONFIG_ID);

		if ( hasValueAssistance && !hasCompleteList && (dataType != 0 && dataType != 4)) {
			docbaseObjControl.setConfigId( MobileAttributeHelper.MOBILE_DOCBASEOBJECT_CONFIG_ID);
		}
		super.renderSingleAttribute(formattedVal, value, isReadOnly, hasCompleteList, out);
		
		docbaseObjControl.setConfigId(MobileAttributeHelper.MOBILE_DOCBASEOBJECT_CONFIG_ID);
	}
}
