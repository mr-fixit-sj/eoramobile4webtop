package com.eora.dctm.mobile4webtop.reposbrowser.versions;

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
import com.documentum.fc.client.IDfSysObject;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.DfId;
import com.documentum.fc.common.DfLogger;
import com.documentum.web.common.ArgumentList;
import com.documentum.web.common.WrapperRuntimeException;
import com.documentum.web.form.FormRequest;
import com.documentum.web.formext.component.Component;
import com.documentum.web.formext.control.docbase.DocbaseIcon;
import com.documentum.web.formext.control.docbase.format.DocsizeValueFormatter;
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
public class MobileObjectVersionHelper {

	private static Logger LOGGER = DfLogger.getLogger(MobileObjectVersionHelper.class);

	public static void getObjectVersionsJSON(final Component component, final ArgumentList args) {
		long startTime = System.currentTimeMillis();

		FormRequest formRequest = component.getFormRequest();
		if (formRequest.isInlineRequest()) {

			final Map<String, Object> responseData = new HashMap<String, Object>();
			final String objectIdStr = args.get("objectId");

			final List<MobileObjectBean> versionBeans = new ArrayList<MobileObjectBean>();

			if (DfId.isObjectId(objectIdStr)) {
				versionBeans.addAll(getObjectVersionsAsBeanList(component, objectIdStr));
			}

			responseData.put("JSON_VERSIONS_BEANS", versionBeans);

			component.setRedirectJsonRendererUrl(responseData);
		}
		LOGGER.debug("Ready getObjectVersionsJSON: " + (System.currentTimeMillis() - startTime));
	}

	private static List<MobileObjectBean> getObjectVersionsAsBeanList(final Component component, final String objectIdStr) {
		final List<MobileObjectBean> objectVersionBeans = new ArrayList<MobileObjectBean>();

		try {
			final DocbaseIcon icon = new DocbaseIcon();
			icon.setForm(component);
			icon.setSize("16");
			
			final DocsizeValueFormatter sizeFormatter = new DocsizeValueFormatter();

			final IDfSessionManager sMgr = SessionManagerHttpBinding.getSessionManager();
			IDfSession session = null;
			try {
				session = sMgr.getSession(SessionManagerHttpBinding.getCurrentDocbase());

				final IDfSysObject sysobject = (IDfSysObject) session.getObjectByQualification("dm_sysobject(all) where r_object_id = ID('" + new DfId(objectIdStr) + "')");

				if (sysobject != null) {

					final String versionsQuery = QueryManager.getQuery("versions.query", new String[] { sysobject.getChronicleId().getId() });

					final IDfQuery query = new DfQuery();
					query.setDQL(versionsQuery);

					final IDfCollection col = query.execute(session, IDfQuery.DF_READ_QUERY);

					try {
						while (col.next()) {
							final MobileObjectBean bean = new MobileObjectBean();

							bean.setValue("objectId", col.getString("r_object_id"));
							bean.setValue("objectType", col.getString("r_object_type"));
							bean.setValue("versionLabels", col.getAllRepeatingStrings("r_version_label", ","));
							bean.setValue("contentSize", sizeFormatter.format(col.getString("r_full_content_size")));
							bean.setValue("modifiedOn", col.getString("r_modify_date"));
							bean.setValue("lockOwner", col.getString("r_lock_owner"));
							bean.setValue("versionDescription", SafeHTMLString.escapeText(col.getString("log_entry")));
		
							icon.setFormat(col.getString("a_content_type"));
							icon.setType(col.getString("r_object_type"));
							icon.setIsVirtualDocument(col.getBoolean("r_is_virtual_doc"));
							icon.setIsReference(col.getBoolean("i_is_reference"));

							bean.setValue("iconUrl", DocbaseIconUtil.getDocbaseIconURL(icon));
							
							objectVersionBeans.add(bean);
						}
					} finally {
						if (col != null) {
							col.close();
						}
					}
				}
			} finally {
				if (session != null) {
					sMgr.release(session);
				}
			}

		} catch (DfException ex) {
			throw new WrapperRuntimeException("Error getting object versions", ex);
		}

		return objectVersionBeans;
	}

}
