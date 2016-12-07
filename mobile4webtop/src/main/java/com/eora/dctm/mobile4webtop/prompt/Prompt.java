package com.eora.dctm.mobile4webtop.prompt;

import com.documentum.web.common.ArgumentList;
import com.eora.dctm.mobile4webtop.common.MobileComponentHelper;

/**
 * 
 * @author S.Jonckheere
 * @since 1.0.0
 * 
 */
public class Prompt extends com.documentum.web.formext.component.Prompt {
	
	private static final long serialVersionUID = -8407743673964186508L;

	@Override
	public void onInit(ArgumentList argumentList) {
		super.onInit(argumentList);
		MobileComponentHelper.setMobileComponentPageForMobileClients(this);
	}


}
