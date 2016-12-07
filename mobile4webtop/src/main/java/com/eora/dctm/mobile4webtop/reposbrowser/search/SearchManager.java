package com.eora.dctm.mobile4webtop.reposbrowser.search;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.documentum.fc.client.IDfEnumeration;
import com.documentum.fc.client.search.IDfQueryBuilder;
import com.documentum.fc.client.search.IDfQueryEvent;
import com.documentum.fc.client.search.IDfQueryListener;
import com.documentum.fc.client.search.IDfQueryProcessor;
import com.documentum.fc.client.search.IDfResultEntry;
import com.documentum.fc.client.search.IDfResultsSet;
import com.documentum.fc.client.search.IDfSearchService;
import com.documentum.fc.common.DfException;
import com.documentum.web.form.control.format.TermsHighlightingFormatter;
import com.documentum.web.formext.component.Component;
import com.documentum.web.formext.control.docbase.DocbaseIcon;
import com.documentum.web.formext.control.docbase.format.DocsizeValueFormatter;
import com.documentum.web.formext.docbase.TypeUtil;
import com.documentum.web.util.SafeHTMLString;
import com.documentum.web.util.SearchUtil;
import com.eora.dctm.mobile4webtop.reposbrowser.MobileReposBrowser;
import com.eora.dctm.mobile4webtop.reposbrowser.common.DocbaseIconUtil;
import com.eora.dctm.mobile4webtop.reposbrowser.common.MobileObjectBean;

/**
 * 
 * @author S.Jonckheere
 * @since 1.0.0
 * 
 */
public class SearchManager implements IDfQueryListener {

	private final Object lockWaitObject = new Object();
	private volatile boolean isSearchCompleted = false;
	private volatile boolean isSearchTruncated = false;

	private List<SearchEventMessage> eventMessages = null;
	private IDfQueryBuilder queryBuilder = null;
	private IDfSearchService searchService;
	private IDfQueryProcessor queryProcessor;

	public SearchManager(final IDfSearchService searchService, final IDfQueryBuilder queryBuilder) {
		this.searchService = searchService;
		this.queryBuilder = queryBuilder;
		this.eventMessages = Collections.synchronizedList(new ArrayList<SearchEventMessage>(10));
	}

	public boolean isExecutionCompleted() {
		synchronized (this.lockWaitObject) {
			return this.isSearchCompleted;
		}
	}

	public boolean isSearchTruncated() {
		synchronized (this.lockWaitObject) {
			return this.isSearchTruncated;
		}
	}

	public boolean isEnoughResults(int resultCount) {
		return (this.isSearchCompleted) || (queryProcessor.getResultsSize() >= resultCount);
	}

	public void execute() {
		queryProcessor = searchService.newQueryProcessor(queryBuilder, true);
		queryProcessor.addListener(this);
		queryProcessor.search();

	}

	public void stopExecution() {
		synchronized (this.lockWaitObject) {
			queryProcessor.stop();
		}
	}

	public void waitForEnoughResults(int resultCount) {
		synchronized (this.lockWaitObject) {
			while ((!this.isSearchCompleted) && (queryProcessor.getResultsSize() <= resultCount)) {
				try {
					this.lockWaitObject.wait();
				} catch (InterruptedException ex) {
				}
			}
		}
	}

	public List<MobileObjectBean> getResultBeans(final int lastResultsIndex, final MobileReposBrowser browserComponent) {
		final List<MobileObjectBean> searchResultBeans = new ArrayList<MobileObjectBean>(100);

		final String objectHeaderAttr = getDisplayAttribute("page-display-config.searchresultspage.objectheader", "", browserComponent);
		final String objectInfoAttr = getDisplayAttribute("page-display-config.searchresultspage.objectinfo", "", browserComponent);

		boolean highlightResults = true;

		final DocbaseIcon icon = new DocbaseIcon();
		icon.setForm(browserComponent);
		icon.setSize("16");

		final DocsizeValueFormatter sizeFormatter = new DocsizeValueFormatter();
		final TermsHighlightingFormatter highlightFormatter = new TermsHighlightingFormatter();
		highlightFormatter.setSeparator("\n");
		highlightFormatter.setCssClass("searchresults-termshighlight");

		final IDfResultsSet partialSet = queryProcessor.getResults(lastResultsIndex);
		while (partialSet.next()) {
			final IDfResultEntry resultEntry = partialSet.getResult();

			if (highlightResults) {
				highlightFormatter.setTerms(getMatchingTerms(resultEntry));
			}

			final MobileObjectBean objectBean = new MobileObjectBean();

			objectBean.setValue("objectId", resultEntry.getString("r_object_id"));

			objectBean.setValue("objectHeader", SafeHTMLString.escapeText(resultEntry.getString(objectHeaderAttr)));
			objectBean.setValue("objectInfo", SafeHTMLString.escapeText(resultEntry.getString(objectInfoAttr)));
			objectBean.setValue("modifiedOn", resultEntry.getString("r_modify_date"));
			objectBean.setValue("linkCount", resultEntry.getString("r_link_cnt"));
			objectBean.setValue("lockOwner", resultEntry.getString("r_lock_owner"));


			final String objectType = resultEntry.getString("r_object_type");
			objectBean.setValue("objectType", objectType);

			sizeFormatter.setType(objectType);
			objectBean.setValue("contentSize", sizeFormatter.format(resultEntry.getString("r_full_content_size")));

			final boolean isFolder = TypeUtil.isSubtypeOf(objectType, "dm_folder") || TypeUtil.isSubtypeOf(objectType, "dm_cabinet");
			objectBean.setValue("isFolder", isFolder);

			if (isFolder && resultEntry.hasAttr("r_folder_path")) {
				String folderPath = resultEntry.getString("r_folder_path");
				if (folderPath != null && folderPath.trim().length() > 0) {
					objectBean.setValue("r_folder_path", folderPath);
				}
			}

			if (resultEntry.hasAttr("summary")) {
				final String summaryStr = resultEntry.getString("summary");
				if (summaryStr != null && !summaryStr.trim().equals("")) {
					final String highLightedSummary = highlightFormatter.format(SafeHTMLString.escapeText(summaryStr));
					objectBean.setValue("summary", highLightedSummary);
				}
			}

			icon.setFormat(resultEntry.getString("a_content_type"));
			icon.setType( objectType);
			icon.setIsVirtualDocument(resultEntry.getBoolean("r_is_virtual_doc"));
			icon.setIsReference(resultEntry.getBoolean("i_is_reference"));

			objectBean.setValue("iconUrl", DocbaseIconUtil.getDocbaseIconURL(icon));

			searchResultBeans.add(objectBean);
		}

		return searchResultBeans;
	}

	public void waitForCompletion() {
		synchronized (this.lockWaitObject) {
			while (!this.isSearchCompleted) {
				try {
					this.lockWaitObject.wait();
				} catch (InterruptedException localInterruptedException) {
				}
			}
		}
	}

	/**
	 * This method is called when the query execution finished (whether
	 * sucessfully or with errors).
	 * 
	 * @param processor
	 *            the IDfQueryProcessor instance executing the query.
	 */
	public void onQueryCompleted(IDfQueryProcessor processor) {
		synchronized (this.lockWaitObject) {
			this.isSearchCompleted = true;
			this.lockWaitObject.notifyAll();
		}
	}

	/**
	 * This method is called when an event occurred (error or information)
	 * 
	 * @param processor
	 *            the IDfQueryProcessor instance executing the query.
	 * @param event
	 *            an event occurring while executing the query.
	 */
	public void onStatusChange(IDfQueryProcessor processor, IDfQueryEvent event) {
		synchronized (this.lockWaitObject) {
			String sourceName;
			try {
				sourceName = SearchUtil.getDisplaySourceName(event.getSource());
			} catch (DfException localDfException) {
				sourceName = event.getSource();
			}
			if (IDfQueryEvent.ERROR == event.getId()) {
				final SearchEventMessage evtMsg = new SearchEventMessage("MSG_QUERY_EXEC_SOURCE_STATUS", new String[] { sourceName, event.getMessage() });
				this.eventMessages.add(evtMsg);

			} else if ((IDfQueryEvent.FILTERED == event.getId()) && (!"0".equals(event.getMessage()))) {
				final SearchEventMessage evtMsg = new SearchEventMessage("MSG_QUERY_EXEC_SOURCE_STATUS_FILTERED", new String[] { sourceName, event.getMessage() });
				this.eventMessages.add(evtMsg);

			} else if (IDfQueryEvent.TRUNCATED == event.getId()) {
				this.isSearchTruncated = true;
			}
		}
	}

	/**
	 * This method is called when new results has been received from the
	 * sources.
	 * 
	 * @param processor
	 *            the IDfQueryProcessor instance executing the query.
	 * @param from
	 *            the start index of the new results in the list of
	 *            IDfResultEntry hold by the query processor
	 * @param to
	 *            the end index of the new results in the list of IDfResultEntry
	 *            hold by the query processor
	 */
	public void onResultChange(IDfQueryProcessor processor, int from, int to) {
		synchronized (this.lockWaitObject) {
			this.lockWaitObject.notifyAll();
		}
	}

	public List<SearchEventMessage> getStatusEventMsg() {
		return this.eventMessages;
	}

	public void clearStatusEventMsg() {
		this.eventMessages.clear();
	}

	private String getMatchingTerms(IDfResultEntry resultEntry) {
		final StringBuffer terms = new StringBuffer(50);
		final IDfEnumeration iter = resultEntry.getMatchingTerms();
		while (iter.hasMoreElements()) {
			terms.append((String) iter.nextElement());
			terms.append("\n");
		}
		return terms.toString();
	}

	private static String getDisplayAttribute(final String lookupConfigString, final String defaultValue, final Component component) {
		String displayAttribute = component.lookupString(lookupConfigString);

		if (displayAttribute == null) {
			displayAttribute = defaultValue;
		}
		return displayAttribute;
	}
}