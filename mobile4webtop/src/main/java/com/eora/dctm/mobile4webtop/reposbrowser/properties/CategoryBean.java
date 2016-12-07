package com.eora.dctm.mobile4webtop.reposbrowser.properties;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author S.Jonckheere
 * @since 1.0.0
 * 
 */
public class CategoryBean implements Cloneable {
	private String categoryLabel;
	private boolean collapsed;
	private List<AttributeBean> attributes = new ArrayList<AttributeBean>();

	public String getCategoryLabel() {
		return categoryLabel;
	}

	public void setCategoryLabel(String categoryLabel) {
		this.categoryLabel = categoryLabel;
	}

	public List<AttributeBean> getAttributes() {
		return attributes;
	}

	public void setAttributes(List<AttributeBean> attributes) {
		this.attributes = attributes;
	}

	public void addAttributeBean(AttributeBean attrBean) {
		attributes.add(attrBean);
	}

	public boolean getCollapsed() {
		return collapsed;
	}

	public void setCollapsed(boolean collapsed) {
		this.collapsed = collapsed;
	}

	@Override
	protected CategoryBean clone() {
		CategoryBean clone = null;
		try {
			clone = (CategoryBean) super.clone();
			final List<AttributeBean> clonedAttributes = new ArrayList<AttributeBean>(attributes.size());
			for ( AttributeBean origAttrBean: attributes){
				clonedAttributes.add( origAttrBean.clone());
			}
			clone.setAttributes(clonedAttributes);
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}
		return clone;
	}
}
