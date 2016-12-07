package com.eora.dctm.mobile4webtop.attributes;

import java.util.Iterator;

import javax.servlet.jsp.PageContext;

import com.documentum.web.common.ArgumentList;
import com.documentum.web.common.WrapperRuntimeException;
import com.documentum.web.form.Control;
import com.documentum.web.formext.control.docbase.DocbaseAttributeValue;
import com.eora.dctm.mobile4webtop.common.MobileComponentHelper;

/**
 * 
 * @author S.Jonckheere
 * @since 1.0.0
 * 
 */
public class MobileDocbaseRepeatingAttribute
		extends com.documentum.web.formext.control.docbase.DocbaseRepeatingAttribute {

	/**
	 * Generated serial ID
	 */
	private static final long serialVersionUID = 3563918429469230227L;

	@Override
	public void onInit(ArgumentList argumentList) {
		MobileComponentHelper.setMobileComponentPageForMobileClients(this);
		super.onInit(argumentList);

		// Make sure scrolling is enabled
		setModal(false);
	}

	public String isOpenList() {
		DocbaseAttributeValue docbaseAttrValue = getDocbaseAttributeValueControl();
		boolean isUncomplete = !docbaseAttrValue.hasCompleteList();
		return new Boolean(isUncomplete).toString();
	}

	private DocbaseAttributeValue getDocbaseAttributeValueControl() {
		DocbaseAttributeValue docbaseAttributeValue = null;

		String elementName = (String) getPageContext().getAttribute(getControlId(), PageContext.SESSION_SCOPE);

		if (elementName != null) {
			Control controlObjWithName = findControlByElementName(getCallerForm(), elementName);

			if ((controlObjWithName instanceof DocbaseAttributeValue)) {
				docbaseAttributeValue = (DocbaseAttributeValue) controlObjWithName;
			}
		}

		if (docbaseAttributeValue == null) {
			throw new WrapperRuntimeException(
					"No DocbaseAttributeValue control found in session with element name: "
							+ elementName);
		}

		return docbaseAttributeValue;
	}

	@SuppressWarnings("rawtypes")
	private Control findControlByElementName(Control controlObj, String elementName) {
		Control controlObjWithName = null;
		Iterator iter = controlObj.getContainedControls();
		while ((iter.hasNext()) && (controlObjWithName == null)) {
			Control localControl = (Control) iter.next();
			if (localControl.getElementName().equalsIgnoreCase(elementName)) {
				controlObjWithName = localControl;
			} else {
				controlObjWithName = findControlByElementName(localControl, elementName);
			}
		}
		return controlObjWithName;
	}

}