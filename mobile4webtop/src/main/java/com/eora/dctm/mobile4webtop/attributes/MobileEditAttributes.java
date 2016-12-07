package com.eora.dctm.mobile4webtop.attributes;

import java.util.List;
import java.util.Map;

import com.documentum.fc.client.IDfFormat;
import com.documentum.fc.client.IDfPersistentObject;
import com.documentum.fc.client.IDfSysObject;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.DfId;
import com.documentum.web.common.ArgumentList;
import com.documentum.web.common.WrapperRuntimeException;
import com.documentum.web.form.Form;
import com.documentum.web.form.FormActionReturnListener;
import com.documentum.web.form.control.Button;
import com.documentum.web.form.control.Label;
import com.documentum.web.formext.control.docbase.DocbaseAttributeList;
import com.documentum.web.formext.control.docbase.DocbaseAttributeList.CategoryInfo;
import com.documentum.web.formext.control.docbase.DocbaseIcon;
import com.documentum.web.formext.control.docbase.DocbaseLockIcon;
import com.documentum.webcomponent.library.attributes.Attributes;

/**
 * 
 * @author S.Jonckheere
 * @since 1.0.0
 * 
 */
public class MobileEditAttributes extends Attributes {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4198246316692201703L;
	
	public final static String MOBILE_EDIT_ATTRIBUTES_COMPONENT = "mobile_edit_attributes";

	private String firstCategoryLabel;
	private int nrOfCategories = 0;
	private int vStamp;
	private boolean overwriteVStamp = false;
	private boolean returnToViewProperties = false;

	@SuppressWarnings("unchecked")
	@Override
	public void onInit(ArgumentList argumentList) {
		final DocbaseAttributeList attrListCtrl = (DocbaseAttributeList) getControl("attrlist", DocbaseAttributeList.class);
		attrListCtrl.setAttrConfigId(MobileAttributeHelper.MOBILE_EDIT_ATTRLIST_CONFIG_ID);

		super.onInit(argumentList);

		final List<CategoryInfo> attrInfos = attrListCtrl.getCategoriesInfo();

		nrOfCategories = attrInfos.size();
		if (attrInfos.size() > 0) {
			final CategoryInfo catInfo = (CategoryInfo) attrInfos.get(0);
			firstCategoryLabel = catInfo.getLabel();
		}
		initPropertiesInfo();
	}

	private void initPropertiesInfo() {
		final DocbaseIcon icon = (DocbaseIcon) getControl("icon", DocbaseIcon.class);
		final DocbaseLockIcon lockIcon = (DocbaseLockIcon) getControl("lockIcon", DocbaseLockIcon.class);
		final Label objectNameLbl = (Label) getControl("object_name", Label.class);

		try {

			if (DfId.isObjectId(getInitArgs().get("objectId"))) {
				final IDfPersistentObject obj = getDfSession().getObject(new DfId(getInitArgs().get("objectId")));
				this.vStamp = obj.getVStamp();

				icon.setType(obj.getType().getName());

				if (obj instanceof IDfSysObject) {
					final IDfSysObject sysObj = (IDfSysObject) obj;
					objectNameLbl.setLabel(sysObj.getObjectName());

					final IDfFormat formatObj = sysObj.getFormat();
					if (formatObj != null) {
						icon.setFormat(formatObj.getName());
					}
					
					if ( sysObj.getLockOwner() != null ){
						lockIcon.setLockowner(sysObj.getLockOwner());
					} else {
						lockIcon.setVisible(false);
					}
				}
			}
		} catch (DfException ex) {
			throw new WrapperRuntimeException("Error onInit", ex);
		}
	}

	public boolean hasMoreThanOneCategory() {
		return nrOfCategories > 1;
	}

	public String getFirstCategoryLabel() {
		return firstCategoryLabel;
	}

	public void onOk(Button button, ArgumentList argumentList) {
		validate();
		if (getIsValid() && canCommitChanges() && onCommitChanges()) {
			initPropertiesInfo();
			returnToViewProperties = true;
		}
	}
	
	public boolean isReturnToViewProperties(){
		return returnToViewProperties;
	}

	@Override
	public boolean onCommitChanges() {
		boolean commited = false;

		if ( ! overwriteVStamp ){
			int currentVStamp = -1;
			try {
				final IDfPersistentObject obj = getDfSession().getObject(new DfId(getInitArgs().get("objectId")));
				currentVStamp = obj.getVStamp();
			} catch (DfException ex) {
				throw new WrapperRuntimeException("Failed to obtain vstamp from object", ex);
			}
	
			if (vStamp == currentVStamp) {
				commited = super.onCommitChanges();
			} else {
				ArgumentList argList = new ArgumentList();
				argList.add("title", getString("MSG_WARNING_TITLE"));
				argList.add("message",getString("MSG_OVERWRITE"));
				argList.add("button", new String[] { "yes", "no" });
				argList.add("dontshowagain", "false");
				setComponentNested("prompt", argList, getContext(), new FormActionReturnListener(this, "onReturnFromPrompt"));
			}
		} else { 
			commited = super.onCommitChanges();
		}
		return commited;
	}

	@SuppressWarnings("rawtypes")
	public void onReturnFromPrompt(Form form, Map returnMap) {
		final String selectedButton = (String) returnMap.get("button");
		if ((selectedButton != null) && (selectedButton.equals("yes"))) {
			overwriteVStamp = true;
			onOk(null, null);
		}
	}

}
