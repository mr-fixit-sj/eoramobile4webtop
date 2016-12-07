package com.eora.dctm.mobile4webtop.reposbrowser.search;

/**
 * 
 * @author S.Jonckheere
 * @since 1.0.0
 * 
 */
public class SearchInfoBean {
	private String objectName;
	private String keywords;
	private String title;
	private String fulltextTerm;
	private boolean showSummary;
	
	public String getObjectName() {
		return objectName;
	}
	public void setObjectName(String objectName) {
		this.objectName = objectName;
	}
	public String getKeywords() {
		return keywords;
	}
	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getFulltextTerm() {
		return fulltextTerm;
	}
	public void setFulltextTerm(String fulltextTerm) {
		this.fulltextTerm = fulltextTerm;
	}
	public boolean isShowSummary() {
		return showSummary;
	}
	public void setShowSummary(boolean showSummary) {
		this.showSummary = showSummary;
	}
}
