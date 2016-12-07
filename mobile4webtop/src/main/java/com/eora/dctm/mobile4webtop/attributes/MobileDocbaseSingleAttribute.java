package com.eora.dctm.mobile4webtop.attributes;

import com.documentum.web.common.ArgumentList;
import com.documentum.web.common.LocaleService;
import com.documentum.web.form.control.DateInput;
import com.documentum.web.formext.control.docbase.DocbaseSingleAttribute;
import com.documentum.web.util.DateUtil;
import com.eora.dctm.mobile4webtop.common.MobileComponentHelper;

/**
 * 
 * @author S.Jonckheere
 * @since 1.0.0
 * 
 */
public class MobileDocbaseSingleAttribute extends DocbaseSingleAttribute {


	private static final long serialVersionUID = -2495525860012508215L;

	@Override
	public void onInit(ArgumentList argumentList) {
		super.onInit(argumentList);
		MobileComponentHelper.setMobileComponentPageForMobileClients(this);
		
		//Make sure scrolling is enabled
		setModal(false);
	}

}
