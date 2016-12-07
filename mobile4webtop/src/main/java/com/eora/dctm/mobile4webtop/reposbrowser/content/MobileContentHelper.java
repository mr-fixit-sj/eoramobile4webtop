package com.eora.dctm.mobile4webtop.reposbrowser.content;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.documentum.com.DfClientX;
import com.documentum.com.IDfClientX;
import com.documentum.fc.client.IDfCollection;
import com.documentum.fc.client.IDfEnumeration;
import com.documentum.fc.client.IDfFormat;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.client.IDfSessionManager;
import com.documentum.fc.client.IDfSysObject;
import com.documentum.fc.client.acs.IDfAcsRequest;
import com.documentum.fc.client.acs.IDfAcsTransferPreferences;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.DfId;
import com.documentum.fc.common.DfLogger;
import com.documentum.web.common.ArgumentList;
import com.documentum.web.form.FormRequest;
import com.documentum.web.formext.component.Component;
import com.documentum.web.formext.preferredrenditions.PreferredRenditionsService;
import com.documentum.web.formext.session.SessionManagerHttpBinding;

/**
 * 
 * @author S.Jonckheere
 * @since 1.0.0
 * 
 */
public class MobileContentHelper {

	private static Logger LOGGER = DfLogger.getLogger(MobileContentHelper.class);

	/*
	 * Method called by JSP javascript
	 */
	public static void getObjectContentUrlJSON(final Component component, final ArgumentList args) {
		long startTime = System.currentTimeMillis();

		FormRequest formRequest = component.getFormRequest();
		if (formRequest.isInlineRequest()) {

			final String contentUrl = getObjectContentUrl(component, args);

			final Map<String, Object> responseData = new HashMap<String, Object>();
			responseData.put("JSON_CONTENT_URL", contentUrl);

			// Render JSON output
			component.setRedirectJsonRendererUrl(responseData);
		}
		LOGGER.debug("Ready getContentUrlJSON: " + (System.currentTimeMillis() - startTime));
	}

	public static String getObjectContentUrl(final Component component, final ArgumentList args) {
		String contentUrl = null;
		final String objectIdStr = args.get("objectId");

		if (DfId.isObjectId(objectIdStr)) {
			try {
				final IDfSessionManager sMgr = SessionManagerHttpBinding.getSessionManager();
				IDfSession session = null;
				try {
					session = sMgr.getSession(SessionManagerHttpBinding.getCurrentDocbase());

					String selectedFormat = null;
					String primaryFormatName = null;

					final IDfSysObject sysobject = (IDfSysObject) session.getObject(new DfId(objectIdStr));
					final IDfFormat primaryFormatObj = sysobject.getFormat();

					if (primaryFormatObj != null) {
						primaryFormatName = primaryFormatObj.getName();

						final PreferredRenditionsService.ViewRenditionSetting prefViewSetting = PreferredRenditionsService.getInstance().getViewRenditionSetting(sysobject.getType().getName(),
								primaryFormatName);

						if (prefViewSetting != null) {
							final String renditionFormat = prefViewSetting.getRenditionFormat();
							if ((renditionFormat != null) && (renditionFormat.length() > 0) && (!renditionFormat.equals(primaryFormatName))) {
								selectedFormat = renditionFormat;
							}
						}

						if (selectedFormat == null) {
							// no rendition is selected so use primary content
							// format
							selectedFormat = primaryFormatName;
						}

						if (isFormatValid(sysobject, selectedFormat)) {

							if (useDownloadServlet(component)) {
								contentUrl = getContentDownloadServletUrl(sysobject, selectedFormat, component);
							} else {
								contentUrl = getContentAcsUrl(sysobject, selectedFormat);

								// fallback to download servlet if content url
								// is null
								if (contentUrl == null) {
									contentUrl = getContentDownloadServletUrl(sysobject, selectedFormat, component);
								}
							}
						}
					}

				} finally {
					if (session != null) {
						sMgr.release(session);
					}
				}

			} catch (DfException ex) {
				LOGGER.warn("Error constructing ACS url, using getcontent url instead.", ex);
			}
		}
		return contentUrl;
	}

	private static boolean useDownloadServlet(final Component component) {
		boolean useDownloadServlet = false;
		if (component.lookupElement("use-download-servlet") != null) {
			useDownloadServlet = component.lookupBoolean("use-download-servlet");
		}
		return useDownloadServlet;
	}

	private static boolean isFormatValid(final IDfSysObject sysObj, final String renditionFormat) throws DfException {
		boolean exists = false;

		IDfCollection col = sysObj.getRenditions("full_format,full_content_size");
		try {
			while (col.next()) {
				if (col.getString("full_format").equals(renditionFormat) && col.getLong("full_content_size") > 0) {
					exists = true;
					break;
				}
			}

		} finally {
			col.close();
		}
		return exists;
	}

	private static String getContentDownloadServletUrl(final IDfSysObject sysobject, final String format, final Component component) throws DfException {
		long startTime = System.currentTimeMillis();

		String downloadServletUrl = null;
		if (format != null) {
			final StringBuffer buf = new StringBuffer();
			buf.append("/wdk5-download?objectId=");
			buf.append(sysobject.getObjectId());
			if (format != null) {
				buf.append("&format=").append(format);
			}

			downloadServletUrl = Component.makeUrl(component.getPageContext().getRequest(), buf.toString());
		}
		LOGGER.debug("Ready getContentDownloadServletUrl: " + (System.currentTimeMillis() - startTime));
		return downloadServletUrl;
	}

	private static String getContentAcsUrl(final IDfSysObject sysobject, String format) throws DfException {
		long startTime = System.currentTimeMillis();

		String acsUrl = null;
		if (format != null) {

			final IDfClientX clientX = new DfClientX();
			final IDfAcsTransferPreferences transferPrefs = clientX.getAcsTransferPreferences();
			transferPrefs.preferAcsTransfer(true);
			transferPrefs.allowBocsTransfer(false);

			final String objectName = sysobject.getObjectName();

			final IDfFormat formatObj = sysobject.getSession().getFormat(format);
			final String formatExtension = formatObj.getDOSExtension();
			final String formatFilename = getFormatFilename(objectName, formatExtension);

			try {
				final IDfEnumeration acsRequests = sysobject.getAcsRequests(format, 0, null, transferPrefs);

				if (acsRequests.hasMoreElements()) {
					final IDfAcsRequest acsRequest = (IDfAcsRequest) acsRequests.nextElement();
					acsRequest.addTransientField("Content-Disposition", getContentDispositionValue(formatFilename));
					//acsRequest.setMimeType("application/octet-stream");

					acsUrl = acsRequest.makeURL();
				}
			} catch (DfException ex) {
				LOGGER.error("Could not generate acsURL: "+ ex.getMessage(),ex );
			}
		}
		LOGGER.debug("Ready getContentAcsUrl: " + (System.currentTimeMillis() - startTime));
		return acsUrl;
	}

	private static String getContentDispositionValue(String strFileName) {
		StringBuffer contentDisposition = new StringBuffer(128);
		contentDisposition.append(" attachment; filename=\"");

		String strCleanFileName = removeIllegalCharacters(strFileName);
		contentDisposition.append(strCleanFileName);
		contentDisposition.append("\"");

		return contentDisposition.toString();
	}

	private static String removeIllegalCharacters(String strName) {
		StringBuffer filteredFileName = new StringBuffer();

		for (int i = 0; i < strName.length(); i++) {
			char currentChar = strName.charAt(i);

			if (isValidCharForFileName(currentChar)) {
				filteredFileName.append(currentChar);
			}
		}
		return filteredFileName.toString();
	}

	private static boolean isValidCharForFileName(char ch) {
		boolean isValidChar = true;

		if ((ch <= 0) || ((ch > 0) && (ch <= '\037')) || (ch == '')) {
			isValidChar = false;
		}
		switch (ch) {
		case '"':
		case '*':
		case '/':
		case ':':
		case '<':
		case '>':
		case '?':
		case '\\':
		case '|':
			isValidChar = false;
		}

		return isValidChar;
	}

	private static String getFormatFilename(String objectName, String extension) {
		int i = objectName.lastIndexOf(".");
		if (i == -1) {
			objectName = objectName + ".";
			i = objectName.indexOf(".");
		}
		objectName = objectName.substring(0, i + 1) + extension;
		return objectName;
	}

}
