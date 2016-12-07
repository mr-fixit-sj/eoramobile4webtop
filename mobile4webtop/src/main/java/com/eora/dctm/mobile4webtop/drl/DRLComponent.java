package com.eora.dctm.mobile4webtop.drl;

import org.apache.log4j.Logger;

import com.documentum.fc.client.IDfSysObject;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.DfLogger;
import com.documentum.fc.common.IDfId;
import com.documentum.web.common.ArgumentList;
import com.documentum.web.form.control.Checkbox;
import com.documentum.web.form.control.Label;
import com.documentum.web.form.control.Panel;
import com.documentum.web.formext.component.Component;
import com.documentum.web.formext.docbase.ObjectCacheUtil;
import com.documentum.web.formext.docbase.PrimaryFolderPathLinkUtil;
import com.documentum.web.formext.session.SessionManagerHttpBinding;
import com.eora.dctm.mobile4webtop.common.MobileComponentHelper;
import com.eora.dctm.mobile4webtop.reposbrowser.MobileReposBrowser;
import com.eora.dctm.mobile4webtop.reposbrowser.content.MobileContentHelper;

/**
 * 
 * @author S.Jonckheere
 * @since 1.0.0
 * 
 */
public class DRLComponent extends com.documentum.web.formext.drl.DRLComponent {

	public final static String CTRL_OPEN_LOCATION_BTN = "__CTRL_OPEN_LOCATION_BTN";
	public final static String CTRL_CONTENT_SIZE_LABEL	= "__CTRL_CONTENT_SIZE_LABEL";

	private static final Logger LOGGER = DfLogger.getLogger(DRLComponent.class);
	private static final long serialVersionUID = 5704030992477936182L;

	private IDfId selectedObjectId;

	@Override
	public void onInit(ArgumentList args) {
		super.onInit(args);

		if (isAuthenticated(SessionManagerHttpBinding.getCurrentDocbase())) {

			// Set open current by default
			final Checkbox openCurrentChkBx = (Checkbox) getControl(OPEN_CURRENT, Checkbox.class);
			openCurrentChkBx.setValue(true);
			// Trigger update of current metadata
			onOpenCurrent(null, null);

			if (selectedObjectId != null) {
				if ( MobileComponentHelper.setMobileComponentPageForMobileClients(this) ){
					updateContentSize();
				}
			}
		}
	}
	
	@Override
	public void onOpenCurrent(final Checkbox checkbox, final ArgumentList args) {
		super.onOpenCurrent(checkbox, args);
		updateContentSize();
	}
	
	public String getMobileReposBrowerUrl() {
		final StringBuffer buf = new StringBuffer();
		buf.append("/component/").append(MobileReposBrowser.MOBILE_REPOSITORY_BROWSER_COMPONENT);

		if (selectedObjectId != null) {
			final String folderIdStr = PrimaryFolderPathLinkUtil.getPrimaryFolderId(selectedObjectId.getId());
			if (folderIdStr != null) {
				buf.append("?objectId=");
				buf.append(folderIdStr);
			}
		}
		final String mobileReposBrowerUrl = Component.makeUrl(getPageContext().getRequest(), buf.toString());
		return mobileReposBrowerUrl;
	}

	public void setSelectedObjectId(final IDfId objectId) {
		this.selectedObjectId = objectId;
	}

	public String getContentUrl() {
		final ArgumentList args = new ArgumentList();
		args.add("objectId", selectedObjectId.getId());

		return MobileContentHelper.getObjectContentUrl(this, args);
	}

	public void setNoContentErrMessage() {
		final Panel errorPanel = (Panel) getControl(ERROR_PANEL, Panel.class);
		errorPanel.setVisible(true);

		final Label errorLbl = (Label) getControl(ERROR_MESSAGE, Label.class);
		errorLbl.setLabel(getString("MSG_NO_CONTENT_URL"));
	}
	
	private void updateContentSize(){
		if ( selectedObjectId != null ){
			final Label contentSizeLabel = (Label)getControl(CTRL_CONTENT_SIZE_LABEL, Label.class);
			
			try {
				final IDfSysObject sysObject = (IDfSysObject) ObjectCacheUtil.getObject(getDfSession(), selectedObjectId.getId());
				contentSizeLabel.setLabel( sysObject.getString("r_full_content_size"));
			} catch (DfException ex) {
				LOGGER.error("Error retrieving content size for object: " + selectedObjectId + ";",ex);
			}
		}
	}

}
