package com.eora.dctm.mobile4webtop.main;

import com.documentum.web.common.ArgumentList;
import com.eora.dctm.mobile4webtop.common.MobileComponentHelper;

/**
 * 
 * @author S.Jonckheere
 * @since 1.0.0
 * 
 */
public class MainEx extends com.documentum.webtop.webcomponent.main.MainEx {

	private static final long serialVersionUID = 7247821722933782079L;

	@Override
	public void onInit(ArgumentList arguments) {
		super.onInit(arguments);
		MobileComponentHelper.redirectMobileClientsToMobileReposBrowser(this, arguments);
	}
}
