package com.eora.dctm.mobile4webtop.importcontent;

import java.util.List;

import com.documentum.web.common.ArgumentList;
import com.documentum.web.formext.control.docbase.DocbaseAttributeList;
import com.documentum.web.formext.control.docbase.DocbaseAttributeList.CategoryInfo;
import com.documentum.webcomponent.library.contenttransfer.importcontent.ImportContent;
import com.eora.dctm.mobile4webtop.attributes.MobileAttributeHelper;

/**
 * 
 * @author S.Jonckheere
 * @since 1.0.0
 * 
 */
public class MobileImportContent extends ImportContent	{

	
	private String firstCategoryLabel;
	private int nrOfCategories = 0;

	/**
	 * Generated serial ID
	 */
	private static final long serialVersionUID = 5683997757028383167L;
	
	@Override
	public void onInit(ArgumentList argumentList) {
		final DocbaseAttributeList attrListCtrl = (DocbaseAttributeList) getControl("attrlist", DocbaseAttributeList.class);
		attrListCtrl.setAttrConfigId(MobileAttributeHelper.MOBILE_IMPORT_ATTRLIST_CONFIG_ID);
		attrListCtrl.setObject("docbaseObj");
		
		super.onInit(argumentList);

		final List<CategoryInfo> attrInfos = attrListCtrl.getCategoriesInfo();

		nrOfCategories = attrInfos.size();
		if (attrInfos.size() > 0) {
			final CategoryInfo catInfo = (CategoryInfo) attrInfos.get(0);
			firstCategoryLabel = catInfo.getLabel();
		}
	}
	
	public boolean hasMoreThanOneCategory() {
		return nrOfCategories > 1;
	}

	public String getFirstCategoryLabel() {
		return firstCategoryLabel;
	}
}
