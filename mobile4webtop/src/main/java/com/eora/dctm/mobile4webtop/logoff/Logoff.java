package com.eora.dctm.mobile4webtop.logoff;

import com.documentum.web.common.ArgumentList;
import com.eora.dctm.mobile4webtop.common.MobileComponentHelper;

/**
 * 
 * @author S.Jonckheere
 * @since 1.0.0
 * 
 */
public class Logoff extends com.documentum.web.formext.session.Logoff {

	/**
	 * Generated serial ID
	 */
	private static final long serialVersionUID = -1117127442701746596L;

	@Override
	public void onInit(ArgumentList argumentList) {
		MobileComponentHelper.setMobileComponentPageForMobileClients(this);
		super.onInit(argumentList);
	}

}
