package com.eora.dctm.mobile4webtop.importcontent;

import com.documentum.fc.client.IDfPersistentObject;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.DfId;
import com.documentum.web.common.ArgumentList;
import com.documentum.web.common.WrapperRuntimeException;
import com.documentum.web.formext.control.docbase.DocbaseIcon;
import com.documentum.webcomponent.library.contenttransfer.importcontent.HttpImportContainer;

/**
 * 
 * @author S.Jonckheere
 * @since 1.0.0
 * 
 */
public class MobileHttpImportContainer extends HttpImportContainer {

	
	/**
	 * Generated Serial UID
	 */
	private static final long serialVersionUID = -1531174312329150911L;

	@Override
	public void onInit(ArgumentList args) {
		super.onInit(args);
		
		final String folderIdStr = args.get("objectId");
		setModal(false);
		
		if ( folderIdStr != null && DfId.isObjectId(folderIdStr)){
			try {
				final IDfPersistentObject persObj = getDfSession().getObject( new DfId(folderIdStr));
				if ( persObj != null && persObj.hasAttr("r_object_type")){
					final DocbaseIcon iconCtrl = (DocbaseIcon)getControl("folderIcon",DocbaseIcon.class);
					iconCtrl.setType(persObj.getString("r_object_type"));
				}
				
			}catch (DfException ex ){
				throw new WrapperRuntimeException("Error initialising container");
			}
		}
	}
}
