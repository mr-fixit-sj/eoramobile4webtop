package com.eora.dctm.mobile4webtop.reposbrowser.subscriptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.documentum.fc.client.DfQuery;
import com.documentum.fc.client.IDfCollection;
import com.documentum.fc.client.IDfQuery;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.client.IDfSessionManager;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.DfId;
import com.documentum.fc.common.DfLogger;
import com.documentum.fc.common.IDfId;
import com.documentum.services.subscriptions.ISubscriptions;
import com.documentum.web.common.ArgumentList;
import com.documentum.web.common.WrapperRuntimeException;
import com.documentum.web.form.FormRequest;
import com.documentum.web.formext.action.ActionService;
import com.documentum.web.formext.action.IActionCompleteListener;
import com.documentum.web.formext.component.Component;
import com.documentum.web.formext.control.docbase.DocbaseIcon;
import com.documentum.web.formext.session.SessionManagerHttpBinding;
import com.documentum.web.util.SafeHTMLString;
import com.documentum.webcomponent.library.subscription.SubscriptionsHttpBinding;
import com.eora.dctm.mobile4webtop.reposbrowser.MobileReposBrowser;
import com.eora.dctm.mobile4webtop.reposbrowser.common.DocbaseIconUtil;
import com.eora.dctm.mobile4webtop.reposbrowser.common.MobileObjectBean;
import com.eora.dctm.mobile4webtop.reposbrowser.properties.IAttributeValueFormatter;

/**
 * 
 * @author S.Jonckheere
 * @since 1.0.0
 * 
 */
public class MobileSubscriptionsHelper {

	private static final String[] ATTRS = { "r_object_type", "object_name", "title", "r_full_content_size", "subject", "r_modify_date", "r_link_cnt", "r_lock_owner", "a_content_type",
			"r_is_virtual_doc", "i_is_reference" };

	private static Logger LOGGER = DfLogger.getLogger(MobileSubscriptionsHelper.class);

	public static void getSubscriptionsJSON(final Component component, final ArgumentList args) {
		long startTime = System.currentTimeMillis();

		FormRequest formRequest = component.getFormRequest();
		if (formRequest.isInlineRequest()) {

			final Map<String, Object> responseData = new HashMap<String, Object>();

			responseData.put("JSON_SUBSCRIPTIONS_BEANS", getSubscriptionsAsBeanList(component));

			component.setRedirectJsonRendererUrl(responseData);
		}
		LOGGER.debug("Ready getObjectPropertiesJSON: " + (System.currentTimeMillis() - startTime));
	}
	
	public static void subscribeObjectJSON(final Component component, final ArgumentList args) {
		long startTime = System.currentTimeMillis();

		FormRequest formRequest = component.getFormRequest();
		if (formRequest.isInlineRequest()) {
			
			boolean isSubscribeActionSuccess = false;
			String subscribeActionResultMessage = null;
			boolean canSubscribeObject = false;
			boolean canUnSubscribeObject = false;			


			final String objectIdStr = args.get("objectId");
			final String subscribeAction = args.get("subscribeAction");
			
			if (DfId.isObjectId(objectIdStr)) {
				
				if ( subscribeAction.equals("mobileunsubscribe") || subscribeAction.equals("mobilesubscribe") ){

					boolean canExecuteAction = false;
					if ( subscribeAction.equals("mobilesubscribe") ){
						canExecuteAction = canSubscribeObject( new DfId(objectIdStr), component);
					} else {
						canExecuteAction = canUnSubscribeObject( new DfId(objectIdStr), component);
					}
					
					if ( canExecuteAction ){
						final ArgumentList actionArgs = new ArgumentList(args);
						IActionCompleteListener actionListener = new IActionCompleteListener() {
							
							@Override
							public void onComplete(String actionName, boolean isSuccess, Map completionArgs) {
							}
						};
						isSubscribeActionSuccess = ActionService.execute(subscribeAction, actionArgs,component.getContext(), component, actionListener);
						if ( isSubscribeActionSuccess ){
							if ( subscribeAction.equals("mobilesubscribe")){
								subscribeActionResultMessage = component.getString("MSG_SUCCESS_SUBSCRIBE");
							} else {
								subscribeActionResultMessage = component.getString("MSG_SUCCESS_UNSUBSCRIBE");
							}
						} else {
							if ( subscribeAction.equals("mobilesubscribe")){
								subscribeActionResultMessage = component.getString("MSG_FAILED_TO_SUBSCRIBE_OBJECT");
							} else {
								subscribeActionResultMessage = component.getString("MSG_FAILED_TO_UNSUBSCRIBE_OBJECT");
							}
						}
					} else {
						if ( subscribeAction.equals("mobilesubscribe")){ 
							subscribeActionResultMessage = component.getString("MSG_FAILED_TO_SUBSCRIBE_OBJECT");
						} else {
							subscribeActionResultMessage = component.getString("MSG_FAILED_TO_UNSUBSCRIBE_OBJECT");
						}
					}
				} 
				//Recheck subscription options
				canSubscribeObject = canSubscribeObject( new DfId(objectIdStr), component);
				canUnSubscribeObject = canUnSubscribeObject( new DfId(objectIdStr), component);
			}						
			final Map<String, Object> responseData = new HashMap<String, Object>();
			responseData.put("JSON_SUBSCRIBE_ACTION_SUCCESS", isSubscribeActionSuccess);
			responseData.put("JSON_SUBSCRIBE_ACTION_RESULT_MSG", subscribeActionResultMessage);
			responseData.put("JSON_CAN_SUBSCRIBE_OBJECT", canSubscribeObject);
			responseData.put("JSON_CAN_UNSUBSCRIBE_OBJECT", canUnSubscribeObject);
			
			// Render JSON output
			component.setRedirectJsonRendererUrl(responseData);
		}
		LOGGER.debug("Ready subscribeObjectJSON: " + (System.currentTimeMillis() - startTime));
	}
	

	public static boolean canSubscribeObject(final IDfId objectId, final Component component){
		final ArgumentList actionArgs = new ArgumentList();
		actionArgs.add("objectId", objectId.getId());

		return ActionService.queryExecute("mobilesubscribe", actionArgs, component.getContext(), component);
	}

	public static boolean canUnSubscribeObject(final IDfId objectId, final Component component){
		final ArgumentList actionArgs = new ArgumentList();
		actionArgs.add("objectId", objectId.getId());
		
		return ActionService.queryExecute("mobileunsubscribe", actionArgs, component.getContext(), component);
	}

	
	private static List<MobileObjectBean> getSubscriptionsAsBeanList(final Component component) {
		List<MobileObjectBean> subscriptionObjectBeans = new ArrayList<MobileObjectBean>();

		try {
			final ISubscriptions subscriptionSrv = new SubscriptionsHttpBinding().getSubscriptionsService();

			final String objectHeaderAttr = getDisplayAttribute("page-display-config.subscriptionspage.objectheader", "", component);
			final String objectInfoAttr = getDisplayAttribute("page-display-config.subscriptionspage.objectinfo", "", component);
			
			final IAttributeValueFormatter sizeFormatter = ((MobileReposBrowser)component).getAttributeValueFormater("r_full_content_size");
			final IAttributeValueFormatter modifiedDateFormatter = ((MobileReposBrowser)component).getAttributeValueFormater("r_modify_date");

			final DocbaseIcon icon = new DocbaseIcon();
			icon.setForm(component);
			icon.setSize("16");
			
			final String subscriptionsQuery = subscriptionSrv.getFolderIncludedSubscribedObjectsQuery("dm_sysobject", ATTRS, true);

			final IDfQuery query = new DfQuery();
			query.setDQL(subscriptionsQuery);

			final IDfSessionManager sMgr = SessionManagerHttpBinding.getSessionManager();

			IDfSession session = null;
			try {
				session = sMgr.getSession(SessionManagerHttpBinding.getCurrentDocbase());

				final IDfCollection col = query.execute(component.getDfSession(), IDfQuery.DF_READ_QUERY);

				try {
					while (col.next()) {
						final MobileObjectBean bean = new MobileObjectBean();
						bean.setValue("objectId", col.getString("r_object_id"));
						bean.setValue("objectType", col.getString("r_object_type"));
						bean.setValue("objectHeader", SafeHTMLString.escapeText(col.getString(objectHeaderAttr)));
						bean.setValue("objectInfo", SafeHTMLString.escapeText(col.getString(objectInfoAttr)));

						bean.setValue("contentSize", sizeFormatter.formatAttributeValue("r_full_content_size", component, col.getTypedObject()));

						bean.setValue("isFolder", col.getBoolean("isfolder"));
						bean.setValue("modifiedOn", modifiedDateFormatter.formatAttributeValue("r_modify_date", component, col.getTypedObject()));
						bean.setValue("linkCount", col.getString("r_link_cnt"));
						bean.setValue("lockOwner", col.getString("r_lock_owner"));

						icon.setFormat(col.getString("a_content_type"));
						icon.setType(col.getString("r_object_type"));
						icon.setIsVirtualDocument(col.getBoolean("r_is_virtual_doc"));
						icon.setIsReference(col.getBoolean("i_is_reference"));

						bean.setValue("iconUrl", DocbaseIconUtil.getDocbaseIconURL(icon));

						subscriptionObjectBeans.add(bean);
					}
				} finally {
					if (col != null) {
						col.close();
					}
				}
			} finally {
				if (session != null) {
					sMgr.release(session);
				}
			}

		} catch (DfException ex) {
			throw new WrapperRuntimeException("Error getting subscriptions", ex);
		}

		return subscriptionObjectBeans;
	}

	private static String getDisplayAttribute(final String lookupConfigString, final String defaultValue, final Component component) {
		String displayAttribute = component.lookupString(lookupConfigString);

		if (displayAttribute == null) {
			displayAttribute = defaultValue;
		}
		return displayAttribute;
	}

}
