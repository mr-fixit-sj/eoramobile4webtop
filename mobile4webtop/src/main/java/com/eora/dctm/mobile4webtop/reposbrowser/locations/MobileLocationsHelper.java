package com.eora.dctm.mobile4webtop.reposbrowser.locations;

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
import com.documentum.web.common.ArgumentList;
import com.documentum.web.common.WrapperRuntimeException;
import com.documentum.web.form.FormRequest;
import com.documentum.web.formext.component.Component;
import com.documentum.web.formext.control.docbase.DocbaseIcon;
import com.documentum.web.formext.docbase.FolderUtil;
import com.documentum.web.formext.session.SessionManagerHttpBinding;
import com.documentum.web.util.SafeHTMLString;
import com.eora.dctm.mobile4webtop.common.QueryManager;
import com.eora.dctm.mobile4webtop.reposbrowser.common.DocbaseIconUtil;
import com.eora.dctm.mobile4webtop.reposbrowser.common.MobileObjectBean;

/**
 * 
 * @author S.Jonckheere
 * @since 1.0.0
 * 
 */
public class MobileLocationsHelper {
	private static Logger LOGGER = DfLogger.getLogger(MobileLocationsHelper.class);

	/*
	 * Method called by JSP javascript
	 */
	public static void getObjectLocationsJSON(final Component component, final ArgumentList args) {
		long startTime = System.currentTimeMillis();

		FormRequest formRequest = component.getFormRequest();
		if (formRequest.isInlineRequest()) {
			final Map<String, Object> responseData = new HashMap<String, Object>();
			final String objectIdStr = args.get("objectId");

			final List<MobileObjectBean> locationBeans = new ArrayList<MobileObjectBean>();
			
			if (DfId.isObjectId(objectIdStr)) {
				locationBeans.addAll(getObjectLocationsAsBeanList(component, objectIdStr));
			}
			responseData.put("JSON_LOCATIONS_BEANS", locationBeans);

			// Render JSON output
			component.setRedirectJsonRendererUrl(responseData);
		}
		LOGGER.debug("Ready getObjectLocationsJSON: " + (System.currentTimeMillis() - startTime));
	}

	private static List<MobileObjectBean> getObjectLocationsAsBeanList(final Component component, final String objectIdStr) {
		final List<MobileObjectBean> objectLocations = new ArrayList<MobileObjectBean>();

		try {
			final DocbaseIcon icon = new DocbaseIcon();
			icon.setForm(component);
			icon.setSize("16");

			final String locationsQuery = QueryManager.getQuery("locations.query", new String[] { objectIdStr });
			
			final String objectHeaderAttr = getDisplayAttribute("page-display-config.locationspage.objectheader", "", component);
			final String objectInfoAttr = getDisplayAttribute("page-display-config.locationspage.objectinfo", "", component);
			

			final IDfQuery query = new DfQuery();
			query.setDQL(locationsQuery);

			final IDfSessionManager sMgr = SessionManagerHttpBinding.getSessionManager();
			IDfSession session = null;
			try {
				session = sMgr.getSession(SessionManagerHttpBinding.getCurrentDocbase());

				final IDfCollection col = query.execute(session, IDfQuery.DF_READ_QUERY);

				try {
					while (col.next()) {
						final MobileObjectBean bean = new MobileObjectBean();

						bean.setValue("objectId", col.getString("r_object_id"));
						bean.setValue("objectType", col.getString("r_object_type"));
						bean.setValue("objectHeader", SafeHTMLString.escapeText(col.getString(objectHeaderAttr)));
						bean.setValue("objectInfo", SafeHTMLString.escapeText(col.getString(objectInfoAttr)));
						
						final String primaryFolderPath = FolderUtil.getPrimaryFolderPath(col.getString("r_object_id"));
						bean.setValue("folderPath", SafeHTMLString.escapeText(primaryFolderPath));
						
						bean.setValue("linkCount", col.getString("r_link_cnt"));
						icon.setType(col.getString("r_object_type"));
						bean.setValue("iconUrl", DocbaseIconUtil.getDocbaseIconURL(icon));

						objectLocations.add(bean);
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
			throw new WrapperRuntimeException("Error getting contents", ex);
		}

		return objectLocations;
	}

	
	private static String getDisplayAttribute(final String lookupConfigString, final String defaultValue, final Component component) {
		String displayAttribute = component.lookupString(lookupConfigString);

		if (displayAttribute == null) {
			displayAttribute = defaultValue;
		}
		return displayAttribute;
	}
}
