package com.eora.dctm.mobile4webtop.reposbrowser.properties;

/**
 * 
 * @author S.Jonckheere
 * @since 1.0.0
 * 
 */
public class AttributeBean implements Cloneable{
	private String name;
	private String label;
	private String value;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	@Override
	protected AttributeBean clone() {
		AttributeBean clone = null;
		try {
			clone = (AttributeBean) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}
		return clone;
	}

}