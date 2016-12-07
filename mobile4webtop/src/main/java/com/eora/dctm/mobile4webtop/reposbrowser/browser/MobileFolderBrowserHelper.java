package com.eora.dctm.mobile4webtop.reposbrowser.browser;

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
public class MobileFolderBrowserHelper {

	private static Logger LOGGER = DfLogger.getLogger(MobileFolderBrowserHelper.class);

	/*
	 * Method called by JSP javascript
	 */
	public static void getParentFolderContentsJSON(final Component component, final ArgumentList args) {
		long startTime = System.currentTimeMillis();

		FormRequest formRequest = component.getFormRequest();
		if (formRequest.isInlineRequest()) {
			final Map<String, Object> responseData = new HashMap<String, Object>();

			String currentFolderPathIdsStr = args.get("currentFolderPathIds");
			if (currentFolderPathIdsStr != null && currentFolderPathIdsStr.trim().length() > 0) {

				// currentFolderPathIds always reflect all objectids of the
				// folder path including current folder object id
				// To update currentfolderpath we should update remove current
				// folder object id
				// Get new selected folder id = parent folder id
				int pos = currentFolderPathIdsStr.lastIndexOf(".");
				if (pos == -1) {
					currentFolderPathIdsStr = "";
				} else {
					currentFolderPathIdsStr = currentFolderPathIdsStr.substring(0, pos);
				}

				// we should now get the object id of the parent folder to get
				// the contents;
				int index = currentFolderPathIdsStr.lastIndexOf(".");
				if (index > -1) {
					index = index + 1;
				} else {
					index = 0;
				}
				final String folderIdStr = currentFolderPathIdsStr.substring(index);

				responseData.put("JSON_FOLDER_CONTENTS", getFolderContentsAsBeanListEx(component, folderIdStr));
				responseData.put("JSON_FOLDER_PATH_IDS", currentFolderPathIdsStr);
				responseData.put("JSON_FOLDER_PATH", getFormattedFullFolderPathStr(currentFolderPathIdsStr));
			}
			// Render JSON output
			component.setRedirectJsonRendererUrl(responseData);
		}
		LOGGER.debug("Ready getParentFolderContentsJSON: " + (System.currentTimeMillis() - startTime));
	}

	/*
	 * Method called by JSP javascript
	 */
	public static void getFolderContentsJSON(final Component component, final ArgumentList args) {
		long startTime = System.currentTimeMillis();

		FormRequest formRequest = component.getFormRequest();
		if (formRequest.isInlineRequest()) {

			String folderIdStr = (args.get("folderId") != null && !args.get("folderId").equals("") && DfId.isObjectId(args.get("folderId")) ? args.get("folderId") : "");
			String currentFolderPathIds = args.get("folderPathIds");

			// No previous folder path specified
			// --> set primary path as default
			if (currentFolderPathIds == null || currentFolderPathIds.trim().length() == 0) {

				if ( FolderUtil.isCabinetType(folderIdStr)){
					currentFolderPathIds = folderIdStr;
				} else {
					final String primaryFolderPath = FolderUtil.getPrimaryFolderPath(folderIdStr, true);
					currentFolderPathIds = FolderUtil.getFolderIdsFromPath(primaryFolderPath);	
				}

			} else if (currentFolderPathIds != null && (folderIdStr == null || folderIdStr.equals(""))) {
				// restore current folder path since no folder was selected but
				// currentfolderpathids were specified
				// determine folderId
				int startPosLastId = currentFolderPathIds.lastIndexOf(".") + 1;
				if (startPosLastId >= 0) {
					folderIdStr = currentFolderPathIds.substring(startPosLastId);
				}

			} else {
				// previous folder path was already set so just append selected
				// folder id
				currentFolderPathIds = currentFolderPathIds + "." + folderIdStr;
			}
			final Map<String, Object> responseData = new HashMap<String, Object>();

			responseData.put("JSON_FOLDER_CONTENTS", getFolderContentsAsBeanListEx(component, folderIdStr));
			responseData.put("JSON_FOLDER_PATH_IDS", currentFolderPathIds);
			responseData.put("JSON_FOLDER_PATH", getFormattedFullFolderPathStr(currentFolderPathIds));

			// Render JSON output
			component.setRedirectJsonRendererUrl(responseData);
		}
		LOGGER.debug("Ready getFolderContentsJSON: " + (System.currentTimeMillis() - startTime));
	}
	
	private static String getFormattedFullFolderPathStr(final String currentFolderPathIds){
		String formattedFullFolderPathStr = "";

		final String fullFolderPathStr = FolderUtil.getFullFolderPathFromIds(currentFolderPathIds);
		if ( fullFolderPathStr != null && FolderUtil.isCabinetType(currentFolderPathIds)){
			formattedFullFolderPathStr = fullFolderPathStr;
		} else {
			formattedFullFolderPathStr = FolderUtil.formatFolderPath(fullFolderPathStr);
		}
		return formattedFullFolderPathStr;
	}

	private static List<MobileObjectBean> getFolderContentsAsBeanListEx(final Component component, final String folderIdStr) {
		List<MobileObjectBean> folderContents = new ArrayList<MobileObjectBean>();

		try {
			
			final DocbaseIcon icon = new DocbaseIcon();
			icon.setForm(component);
			icon.setSize("16");

			String queryId = "folder.contents.query";
			if (folderIdStr == null || folderIdStr.length() == 0 && !DfId.isObjectId(folderIdStr)) {
				queryId = "cabinets.contents.query";
			}

			final String objectHeaderAttr = getDisplayAttribute("page-display-config.foldercontentspage.objectheader", "", component);
			final String objectInfoAttr = getDisplayAttribute("page-display-config.foldercontentspage.objectinfo", "", component);

			final IAttributeValueFormatter sizeFormatter = ((MobileReposBrowser)component).getAttributeValueFormater("r_full_content_size");
			final IAttributeValueFormatter modifiedDateFormatter = ((MobileReposBrowser)component).getAttributeValueFormater("r_modify_date");
			
			final String foldercontentsQuery = QueryManager.getQuery(queryId, new String[] { folderIdStr });

			final IDfQuery query = new DfQuery();
			query.setDQL(foldercontentsQuery);

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

						folderContents.add(bean);
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

		return folderContents;
	}

	private static String getDisplayAttribute(final String lookupConfigString, final String defaultValue, final Component component) {
		String displayAttribute = component.lookupString(lookupConfigString);

		if (displayAttribute == null) {
			displayAttribute = defaultValue;
		}
		return displayAttribute;
	}
}
