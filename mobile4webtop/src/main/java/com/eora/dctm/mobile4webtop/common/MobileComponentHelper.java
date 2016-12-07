package com.eora.dctm.mobile4webtop.common;

import javax.servlet.http.HttpServletRequest;

import com.documentum.web.common.ArgumentList;
import com.documentum.web.formext.component.Component;
import com.documentum.web.formext.config.Context;
import com.eora.dctm.mobile4webtop.reposbrowser.MobileReposBrowser;

/**
 * 
 * @author S.Jonckheere
 * @since 1.0.0
 * 
 */
public class MobileComponentHelper {
	
	private final static String MOBILE_PAGE = "mobile";

	public static boolean setMobileComponentPageForMobileClients( final Component component ) {
		boolean isMobilePage = false;
		if (UserAgentUtil.MOBILE_USERAGENT_TYPE.equals(UserAgentUtil.getRequestUserAgentType((HttpServletRequest) component.getPageContext().getRequest()))) {
			component.setComponentPage(MOBILE_PAGE);
			isMobilePage = true;
		}
		return isMobilePage;
	}
	
	public static boolean redirectMobileClientsToMobileReposBrowser ( final Component component, final ArgumentList arguments ){
		boolean isRedirected = false;
		if (UserAgentUtil.MOBILE_USERAGENT_TYPE.equals(UserAgentUtil.getRequestUserAgentType((HttpServletRequest) component.getPageContext().getRequest()))) {
			component.setComponentJump(MobileReposBrowser.MOBILE_REPOSITORY_BROWSER_COMPONENT, arguments, new Context(component.getContext()));
			isRedirected = true;
		}
		return isRedirected;
	}

}
