package com.eora.dctm.mobile4webtop.login;

import com.documentum.web.common.ArgumentList;
import com.eora.dctm.mobile4webtop.common.MobileComponentHelper;

/**
 * 
 * @author S.Jonckheere
 * @since 1.0.0
 * 
 */
public class Login extends com.documentum.web.formext.session.Login {

	/**
	 * Generated serial ID
	 */
	private static final long serialVersionUID = 3563918229469230228L;

	@Override
	public void onInit(ArgumentList argumentList) {
		MobileComponentHelper.setMobileComponentPageForMobileClients(this);
		super.onInit(argumentList);
	}
}
