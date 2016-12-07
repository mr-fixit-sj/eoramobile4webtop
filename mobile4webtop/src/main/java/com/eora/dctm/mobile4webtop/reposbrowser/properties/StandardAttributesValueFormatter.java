package com.eora.dctm.mobile4webtop.reposbrowser.properties;

import org.apache.log4j.Logger;

import com.documentum.fc.client.IDfTypedObject;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.DfLogger;
import com.documentum.fc.common.IDfAttr;
import com.documentum.fc.common.IDfTime;
import com.documentum.records.util.DataDictionaryUtil;
import com.documentum.web.common.LocaleService;
import com.documentum.web.form.control.format.BooleanFormatter;
import com.documentum.web.form.control.format.DateValueFormatter;
import com.documentum.web.form.control.format.NumberFormatter;
import com.documentum.web.formext.component.Component;
import com.documentum.web.formext.control.docbase.format.DocFormatValueFormatter;
import com.documentum.web.formext.control.docbase.format.DocsizeValueFormatter;
import com.documentum.web.formext.control.docbase.format.ObjectIdFormatter;
import com.documentum.web.formext.control.docbase.format.PolicyStateNameFormatter;

/**
 * 
 * @author S.Jonckheere
 * @since 1.0.0
 * 
 */
public class StandardAttributesValueFormatter implements IAttributeValueFormatter {

	private static Logger LOGGER = DfLogger.getLogger(StandardAttributesValueFormatter.class);

	final DocsizeValueFormatter sizeFormatter = new DocsizeValueFormatter();
	final DocFormatValueFormatter contentTypeFormatter = new DocFormatValueFormatter();
	final PolicyStateNameFormatter policyFormatter = new PolicyStateNameFormatter();
	final BooleanFormatter booleanFormatter = new BooleanFormatter();
	final DateValueFormatter dateFormatter = new DateValueFormatter();
	final ObjectIdFormatter idFormatter = new ObjectIdFormatter();
	final NumberFormatter numberFormatter = new NumberFormatter();

	public StandardAttributesValueFormatter(Component component) {
		booleanFormatter.setTrueNlsValue(component.getString("MSG_TRUE"));
		booleanFormatter.setFalseNlsValue(component.getString("MSG_FALSE"));
	}

	@Override
	public synchronized String formatAttributeValue(final String attributeName, final Component component, final IDfTypedObject typedObject) {
		String value = null;
		try {
			boolean isTimeAttr = (IDfAttr.DM_TIME == typedObject.getAttrDataType(attributeName));

			// Repeating attributes as one string
			if (typedObject.isAttrRepeating(attributeName)) {

				final StringBuffer repeatingValue = new StringBuffer();

				for (int index = 0; index < typedObject.getValueCount(attributeName); index++) {

					if ( isTimeAttr ){ 
						repeatingValue.append( formatTimeValue(typedObject.getRepeatingTime(attributeName, index)));
					} else {
						repeatingValue.append( formatValue(typedObject.getRepeatingString(attributeName, index), attributeName, typedObject));
					}

					if ((index + 1) < typedObject.getValueCount(attributeName)) {
						repeatingValue.append(", ");
					}
				}
				value = repeatingValue.toString();
				
			} else { // Single value attribute
				if ( isTimeAttr ){
					value = formatTimeValue(typedObject.getTime(attributeName));
				} else { 
					value = formatValue(typedObject.getString(attributeName), attributeName, typedObject);
				}
			}
		} catch (DfException ex) {
			LOGGER.error("Error formatting attr value (" + value + ") for attribute : " + attributeName);
		}
		return value;
	}

	private String formatTimeValue(final IDfTime timeObj) {
		String formattedValue = "";
		if ( timeObj != null && !timeObj.isNullDate() ){
			
			formattedValue = dateFormatter.format(Long.toString(timeObj.getDate().getTime()));
		}
		return formattedValue;
	}

	private String formatValue(final String value, final String attributeName, final IDfTypedObject typedObj) throws DfException {
		String formattedValue = "";
		if (value != null && !value.equals("")) {

			// Format content size
			if ("r_full_content_size".equals(attributeName) || "r_content_size".equals(attributeName)) {
				formattedValue = sizeFormatter.format(value);
			} else if ("a_content_type".equals(attributeName)) {
				formattedValue = contentTypeFormatter.format(value);
			} else if ("r_current_state".equals(attributeName)) {
				policyFormatter.setPolicyid(typedObj.getId("r_policy_id").getId());
				formattedValue = policyFormatter.format(value);
			} else if ("r_object_type".equals(attributeName)) {
				String typeName = DataDictionaryUtil.getTranslatedObjectTypeName(typedObj.getSession(), value, LocaleService.getLocale().getLanguage());
				if ((typeName == null || typeName.equals("")) && !LocaleService.getLocale().getLanguage().equals("en")) {
					typeName = DataDictionaryUtil.getTranslatedObjectTypeName(typedObj.getSession(), value, "en");
				}
				if (typeName != null && !typeName.equals("")) {
					formattedValue = typeName;
				}
			} else if (IDfAttr.DM_ID == typedObj.getAttrDataType(attributeName)) {
				formattedValue = idFormatter.format(value);
			} else if (IDfAttr.DM_BOOLEAN == typedObj.getAttrDataType(attributeName)) {
				formattedValue = booleanFormatter.format(value);
			} else if (IDfAttr.DM_INTEGER == typedObj.getAttrDataType(attributeName)) {
				numberFormatter.setType(NumberFormatter.INTEGER);
				numberFormatter.setBlankIfZero(Boolean.FALSE);
				formattedValue = numberFormatter.format(value);
			} else if (IDfAttr.DM_DOUBLE == typedObj.getAttrDataType(attributeName)) {
				numberFormatter.setType(NumberFormatter.FLOAT);
				numberFormatter.setBlankIfZero(Boolean.FALSE);
				formattedValue = numberFormatter.format(value);
			} else {
				formattedValue = value;
			}
		}
		return formattedValue;
	}

}
