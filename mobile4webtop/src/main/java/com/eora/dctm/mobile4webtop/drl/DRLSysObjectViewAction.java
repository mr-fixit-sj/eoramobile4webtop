package com.eora.dctm.mobile4webtop.drl;

import javax.servlet.http.HttpServletRequest;

import com.documentum.fc.common.DfId;
import com.documentum.fc.common.IDfId;
import com.documentum.web.common.ArgumentList;
import com.documentum.web.formext.component.Component;
import com.documentum.web.formext.config.Context;
import com.documentum.web.formext.config.IConfigElement;
import com.eora.dctm.mobile4webtop.common.UserAgentUtil;

/**
 * 
 * @author S.Jonckheere
 * @since 1.0.0
 * 
 */
public class DRLSysObjectViewAction extends com.documentum.webtop.webcomponent.drl.DRLSysObjectViewAction {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 694614721661992744L;

	@Override
	public boolean queryExecute(String strAction, IConfigElement config, ArgumentList args, Context context, Component component) {
		boolean canView = false;
		if (component != null && UserAgentUtil.MOBILE_USERAGENT_TYPE.equals(UserAgentUtil.getRequestUserAgentType((HttpServletRequest) component.getPageContext().getRequest()))) {
			canView = false;
			if ( component instanceof DRLComponent){
				final IDfId objectId = new DfId(args.get("objectId"));
				
				final DRLComponent drlComponent = (DRLComponent)component;
				drlComponent.setSelectedObjectId( ( objectId.isObjectId() ? objectId : null) );
			}
		} else {
			canView = super.queryExecute(strAction, config, args, context, component);
		}
		return canView;
	}
}
