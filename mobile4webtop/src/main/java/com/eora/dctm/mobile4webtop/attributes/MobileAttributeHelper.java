package com.eora.dctm.mobile4webtop.attributes;

import java.util.ArrayList;
import java.util.List;

import com.documentum.web.common.LocaleService;
import com.documentum.web.form.control.DateInput;
import com.documentum.web.form.control.DateInputTag;
import com.documentum.web.util.DateUtil;

/**
 * 
 * @author S.Jonckheere
 * @since 1.0.0
 * 
 */
public class MobileAttributeHelper {

	public static String getDateFormat() {
		final DateInput dateInput = new DateInput();
		String strDateFormat = DateUtil.getDateFormatPattern4DigitYear(dateInput.getDateFormat(), LocaleService.getLocale());
		strDateFormat = strDateFormat.replaceAll("EEEE", "DD");
		strDateFormat = strDateFormat.replaceAll("EEE", "D");
		strDateFormat = strDateFormat.replaceAll("EE", "D");
		strDateFormat = strDateFormat.replaceAll("E", "D");
		strDateFormat = strDateFormat.replaceAll("M", "m");
		strDateFormat = strDateFormat.replaceAll("mmmm", "MM");
		strDateFormat = strDateFormat.replaceAll("mmm", "M");
		strDateFormat = strDateFormat.replaceAll("yy", "y");
		return strDateFormat;
	}

	public static String getDefaultEmptyDateNlsString(){
		final List<String> defaultEmptyDateStr = new ArrayList<String>();
		final DateInputTag dateInputTag = new DateInputTag(){
			{
				defaultEmptyDateStr.add(getString("MSG_DATE"));
			}
		};
		return defaultEmptyDateStr.get(0);
	}

	public final static String MOBILE_EDIT_ATTRLIST_CONFIG_ID = "mobile_edit_attrlist";
	public final static String MOBILE_IMPORT_ATTRLIST_CONFIG_ID = "mobile_import_attrlist";
	public final static String MOBILE_DOCBASEOBJECT_CONFIG_ID = "mobile_docbaseobjectconfig";
	public final static String DEFAULT_DOCBASEOBJECT_CONFIG_ID = "attributes";
}
