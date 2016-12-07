package com.eora.dctm.mobile4webtop.reposbrowser.search;


/**
 * 
 * @author S.Jonckheere
 * @since 1.0.0
 * 
 */
public class SearchEventMessage {
	private String m_msgPropId;
	private String[] m_params;

	protected SearchEventMessage(String propId, String[] params) {
		this.m_msgPropId = propId;
		this.m_params = params;
	}

	protected String getMessagePropId() {
		return this.m_msgPropId;
	}

	protected String[] getParams() {
		return this.m_params;
	}
}
