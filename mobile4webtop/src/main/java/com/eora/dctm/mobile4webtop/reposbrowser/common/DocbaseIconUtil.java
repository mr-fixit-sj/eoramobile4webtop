package com.eora.dctm.mobile4webtop.reposbrowser.common;

import javax.servlet.jsp.PageContext;

import com.documentum.fc.client.IDfSession;
import com.documentum.fc.client.IDfSessionManager;
import com.documentum.fc.client.IDfType;
import com.documentum.fc.common.DfException;
import com.documentum.web.common.BrandingService;
import com.documentum.web.common.WrapperRuntimeException;
import com.documentum.web.formext.control.docbase.DocbaseIcon;
import com.documentum.web.formext.control.docbase.DocbaseLockIcon;
import com.documentum.web.formext.session.SessionManagerHttpBinding;

/**
 * 
 * @author S.Jonckheere
 * @since 1.0.0
 * 
 */
public class DocbaseIconUtil {
	
	
	public static String getDocbaseLockIconURL(DocbaseLockIcon lockIcon) {
		String imageUrlStr = null;
		
		if ( lockIcon.getLockowner() != null && !lockIcon.getLockowner().trim().equals("")){
			final IDfSessionManager sMgr = SessionManagerHttpBinding.getSessionManager();
			IDfSession session = null;

			try {
				session = sMgr.getSession(SessionManagerHttpBinding.getCurrentDocbase());
				
				if ( lockIcon.getLockowner().equals(session.getLoginUserName())){
					imageUrlStr = BrandingService.getThemeResolver().getResourcePath("icons/indicator/i_locked_by_you_16.gif", lockIcon.getForm().getPageContext(), false);
				} else {
					imageUrlStr = BrandingService.getThemeResolver().getResourcePath("icons/indicator/i_locked_by_another_16.gif", lockIcon.getForm().getPageContext(), false);
				}
				
			} catch (DfException ex) {
				throw new WrapperRuntimeException("Error resolving lockicon image", ex);
			} finally {
				if (session != null) {
					sMgr.release(session);
				}
			}
		}
		return imageUrlStr;
	}

	public static String getDocbaseIconURL(DocbaseIcon icon) {
		String imageUrlStr = null;
		
		int iconSize = 0;
		try {
			iconSize = Integer.parseInt(icon.getSize());
		} catch (Exception e) {
		}

		if ((iconSize != 16) && (iconSize != 32)) {
			iconSize = 16;
		}
		if (icon.isVirtualDocument()) {
			imageUrlStr = getIconImageUrl( "vdoc", iconSize, icon.getForm().getPageContext());
		} else if (icon.isFrozenAssembly()) {
			imageUrlStr = getIconImageUrl( "fdoc", iconSize,icon.getForm().getPageContext());
		} else if (icon.isAssembly()) {
			imageUrlStr = getIconImageUrl( "adoc", iconSize,icon.getForm().getPageContext());
		} else {
			imageUrlStr = getIconImageUrl( icon.getFormat(), icon.getType(), iconSize,icon.getForm().getPageContext());
		}
		return imageUrlStr;
	}

	private static String getIconImageUrl( String typeName, int iconSize,final PageContext pageContext) {
		return getIconImageUrl(null, typeName, iconSize, pageContext);
	}

	private static String getIconImageUrl(final String format, String typeName, int iconSize,final PageContext pageContext) {
		final IDfSessionManager sMgr = SessionManagerHttpBinding.getSessionManager();
		IDfSession session = null;
		String imageUrlStr = null;

		try {
			session = sMgr.getSession(SessionManagerHttpBinding.getCurrentDocbase());

			if ((format != null) && (format.length() != 0)) {
				imageUrlStr = getFormatIconImageUrl(format, iconSize,pageContext);
			}

			if ((imageUrlStr == null) && (typeName != null) && (typeName.length() != 0)) {
				String currentTypeName = typeName;
				imageUrlStr = getTypeIconImageUrl(currentTypeName, iconSize, pageContext);
				
				while ((imageUrlStr == null) && (currentTypeName != null) && (currentTypeName.length() != 0)) {
					IDfType type = session.getType(currentTypeName);

					if (type == null){
						break;
					}
					currentTypeName = type.getSuperName();

					if ((currentTypeName != null) && (currentTypeName.length() != 0)) {
						imageUrlStr = getTypeIconImageUrl(currentTypeName, iconSize, pageContext);
					}
				}
			}

			if (imageUrlStr == null) {
				if (iconSize == 32) {
					imageUrlStr = BrandingService.getThemeResolver().getResourcePath("icons/type/t_unknown_32.gif", pageContext, false);
				} else {
					imageUrlStr = BrandingService.getThemeResolver().getResourcePath("icons/type/t_unknown_16.gif", pageContext, false);
				}
			}

		} catch (DfException ex) {
			throw new WrapperRuntimeException("Error resolving icon image", ex);
		} finally {
			if (session != null) {
				sMgr.release(session);
			}
		}
		return imageUrlStr;
	}


	private static String getFormatIconImageUrl(final String format, int iconSize, final PageContext pageContext) {
		String imageUrlStr = null;
		if (iconSize == 32) {
			imageUrlStr = BrandingService.getThemeResolver().getResourcePath("icons/format/f_" + format + "_32.gif", pageContext, false);
		} else {
			imageUrlStr = BrandingService.getThemeResolver().getResourcePath("icons/format/f_" + format + "_16.gif", pageContext, false);
		}
		return imageUrlStr;
	}


	private static String getTypeIconImageUrl( final String typeName, final int iconSize, final PageContext pageContext) {
		String imageUrlStr = null;
		if (iconSize == 32) {
			imageUrlStr = BrandingService.getThemeResolver().getResourcePath("icons/type/t_" + typeName + "_32.gif", pageContext, false);
		} else {
			imageUrlStr = BrandingService.getThemeResolver().getResourcePath("icons/type/t_" + typeName + "_16.gif", pageContext, false);
		}
		return imageUrlStr;
	}
}
