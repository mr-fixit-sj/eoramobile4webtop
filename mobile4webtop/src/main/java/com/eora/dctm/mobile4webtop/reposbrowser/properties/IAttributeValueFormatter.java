package com.eora.dctm.mobile4webtop.reposbrowser.properties;

import com.documentum.fc.client.IDfTypedObject;
import com.documentum.web.formext.component.Component;

/**
 * 
 * @author S.Jonckheere
 * @since 1.0.0
 * 
 */
public interface IAttributeValueFormatter {
	
	public String formatAttributeValue( final String attributeName, final Component component, final IDfTypedObject typedObject);

}
