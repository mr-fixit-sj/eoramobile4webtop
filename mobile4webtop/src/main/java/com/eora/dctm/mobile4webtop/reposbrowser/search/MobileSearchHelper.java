package com.eora.dctm.mobile4webtop.reposbrowser.search;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;

import com.documentum.com.DfClientX;
import com.documentum.fc.client.search.IDfExpressionSet;
import com.documentum.fc.client.search.IDfQueryBuilder;
import com.documentum.fc.client.search.IDfQueryManager;
import com.documentum.fc.client.search.IDfSearchService;
import com.documentum.fc.client.search.IDfSimpleAttrExpression;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.DfLogger;
import com.documentum.fc.common.IDfValue;
import com.documentum.web.common.ArgumentList;
import com.documentum.web.common.WrapperRuntimeException;
import com.documentum.web.form.FormRequest;
import com.documentum.web.formext.component.Component;
import com.documentum.web.formext.session.SessionManagerHttpBinding;
import com.documentum.webcomponent.library.search.SearchInfo;
import com.eora.dctm.mobile4webtop.reposbrowser.MobileReposBrowser;
import com.eora.dctm.mobile4webtop.reposbrowser.common.MobileObjectBean;
import com.google.gson.Gson;

/**
 * 
 * @author S.Jonckheere
 * @since 1.0.0
 * 
 */
public class MobileSearchHelper {

	private static Logger LOGGER = DfLogger.getLogger(MobileSearchHelper.class);

	private final String[] ATTRS = { "r_object_type", "object_name", "title", "r_full_content_size", "subject", "r_modify_date", "r_link_cnt", "r_lock_owner", "a_content_type", "r_is_virtual_doc",
			"i_is_reference" };

	private SearchManager searchExecMgr;
	private IDfSearchService searchService;

	public MobileSearchHelper() {
		final String currentDocbase = SessionManagerHttpBinding.getCurrentDocbase();
		try {
			this.searchService = new DfClientX().getLocalClient().newSearchService(SessionManagerHttpBinding.getSessionManager(), currentDocbase);
			
		} catch (Exception ex) {
			throw new WrapperRuntimeException("Error initializing search service" , ex);
		}
	}

	/*
	 * Method called by JSP javascript
	 */
	public void executeSearchJSON(final Component component, final ArgumentList args) {
		long startTime = System.currentTimeMillis();

		final FormRequest formRequest = component.getFormRequest();
		if (formRequest.isInlineRequest()) {

			final Map<String, Object> responseData = new HashMap<String, Object>();

			try {

				String queryDescription = "";

				final String searchInfoJsonStr = args.get("searchInfo");
				if (searchInfoJsonStr != null && !searchInfoJsonStr.equals("")) {

					final Gson gsonParser = new Gson();
					final SearchInfoBean searchInfoBean = gsonParser.fromJson(searchInfoJsonStr, SearchInfoBean.class);

					final IDfQueryBuilder queryBuilder = buildQuery(component, searchService, searchInfoBean);

					if ( this.searchExecMgr != null && !this.searchExecMgr.isExecutionCompleted()){
						//If previous search is still running, stop it first
						this.searchExecMgr.stopExecution();
					}
					this.searchExecMgr = new SearchManager(searchService, queryBuilder);
					this.searchExecMgr.execute();

					queryDescription = SearchInfo.getDescription(queryBuilder);
					//Check if queryDescription is not null
					if ( queryDescription == null ){
						queryDescription = "";
					}
				}
				responseData.put("JSON_SEARCH_EVENT_MESSAGES", searchExecMgr.getStatusEventMsg());
				responseData.put("JSON_SEARCHQUERY_DESCRIPTION", queryDescription );

			} catch (DfException ex) {
				
				LOGGER.error("Error executing search", ex);
				
				responseData.put("JSON_SERVER_ERROR", component.getString("MSG_ERROR_EXECUTING_SEARCH"));
			}

			// Render JSON output
			component.setRedirectJsonRendererUrl(responseData);
		}
		LOGGER.debug("Ready executeSearchJSON: " + (System.currentTimeMillis() - startTime));
	}

	/*
	 * Method called by JSP javascript
	 */
	public void getNextSearchResultsJSON(final MobileReposBrowser browserComponent, final ArgumentList args) {
		long startTime = System.currentTimeMillis();

		final FormRequest formRequest = browserComponent.getFormRequest();
		if (formRequest.isInlineRequest()) {
			
			final List<MobileObjectBean> searchResultsBeans = new ArrayList<MobileObjectBean>();

			final String fromIndexStr = args.get("fromIndex");
			int lastResultIndex = -1;
			boolean isSearchCompleted = false;
			boolean isSearchTruncated = false;

			if (fromIndexStr != null && !fromIndexStr.equals("")) {

				int fromIndex = Integer.parseInt(fromIndexStr);

				// wait for next 50
				searchExecMgr.waitForEnoughResults(fromIndex + 50);

				isSearchTruncated = searchExecMgr.isSearchTruncated();
				isSearchCompleted = searchExecMgr.isExecutionCompleted();

				searchResultsBeans.addAll(searchExecMgr.getResultBeans(fromIndex, browserComponent));
				lastResultIndex = (fromIndex + (searchResultsBeans.size() - 1));
			}

			final Map<String, Object> responseData = new HashMap<String, Object>();
			responseData.put("JSON_SEARCHRESULTS_BEANS", searchResultsBeans);
			responseData.put("JSON_SEARCH_LAST_RESULT_INDEX", lastResultIndex);
			responseData.put("JSON_SEARCH_IS_TRUNCATED", isSearchTruncated);
			responseData.put("JSON_SEARCH_IS_COMPLETED", isSearchCompleted);
			responseData.put("JSON_SEARCH_EVENT_MESSAGES", searchExecMgr.getStatusEventMsg());

			// Render JSON output
			browserComponent.setRedirectJsonRendererUrl(responseData);
		}
		LOGGER.debug("Ready getNextSearchResultsJSON: " + (System.currentTimeMillis() - startTime));
	}

	private IDfQueryBuilder buildQuery(final Component component, final IDfSearchService searchService, final SearchInfoBean searchInfoBean) throws DfException {

		final IDfQueryManager querymgr = searchService.newQueryMgr();
		final IDfQueryBuilder queryBuilder = querymgr.newQueryBuilder("dm_sysobject");

		final int maxResultsCountFromConfig = component.lookupInteger("max-results-count");
		queryBuilder.setMaxResultCount(maxResultsCountFromConfig);

		for (String attr : ATTRS) {
			queryBuilder.addResultAttribute(attr);
		}
		
		//Check if summary should be included
		if ( searchInfoBean.isShowSummary()){
			queryBuilder.addResultAttribute("summary");
		}

		final String currentDocbase = SessionManagerHttpBinding.getCurrentDocbase();
		queryBuilder.addSelectedSource(currentDocbase);

		final IDfExpressionSet rootExpressionSet = queryBuilder.getRootExpressionSet();

		// check fulltextTerm
		final String fulltextSearchValue = searchInfoBean.getFulltextTerm();
		if (fulltextSearchValue != null && !fulltextSearchValue.equals("")) {
			rootExpressionSet.addFullTextExpression(fulltextSearchValue);
		}

		// check objectname
		final String objectNameSearchValue = searchInfoBean.getObjectName();
		if (objectNameSearchValue != null && !objectNameSearchValue.equals("")) {
			for (String value : getSearchValues(objectNameSearchValue)) {
				final IDfExpressionSet setAnd = rootExpressionSet.addExpressionSet(IDfExpressionSet.LOGICAL_OP_AND);
				setAnd.addSimpleAttrExpression("object_name", IDfValue.DF_STRING, IDfSimpleAttrExpression.SEARCH_OP_CONTAINS, false, false, value);
			}
		}

		// Check title/subject
		final String titleOrSubjectSearchValue = searchInfoBean.getTitle();
		if (titleOrSubjectSearchValue != null && !titleOrSubjectSearchValue.equals("")) {
			for (String value : getSearchValues(titleOrSubjectSearchValue)) {
				final IDfExpressionSet setAnd = rootExpressionSet.addExpressionSet(IDfExpressionSet.LOGICAL_OP_AND);
				final IDfExpressionSet orAnd = setAnd.addExpressionSet(IDfExpressionSet.LOGICAL_OP_OR);
				orAnd.addSimpleAttrExpression("title", IDfValue.DF_STRING, IDfSimpleAttrExpression.SEARCH_OP_CONTAINS, false, false, value);
				orAnd.addSimpleAttrExpression("subject", IDfValue.DF_STRING, IDfSimpleAttrExpression.SEARCH_OP_CONTAINS, false, false, value);
			}
		}

		// check keywords
		final String keywordsSearchValue = searchInfoBean.getKeywords();
		if (keywordsSearchValue != null && !keywordsSearchValue.equals("")) {
			for (String value : getSearchValues(keywordsSearchValue)) {
				final IDfExpressionSet setAnd = rootExpressionSet.addExpressionSet(IDfExpressionSet.LOGICAL_OP_AND);
				setAnd.addSimpleAttrExpression("keywords", IDfValue.DF_STRING, IDfSimpleAttrExpression.SEARCH_OP_CONTAINS, false, true, value);
			}
		}
		return queryBuilder;
	}

	private List<String> getSearchValues(final String untokenizedWords) {
		final List<String> searchValues = new ArrayList<String>();

		String phraseStr = null;
		boolean inPhrase = false;

		final StringTokenizer tokenizer = new StringTokenizer(untokenizedWords, "\"", true);
		while (tokenizer.hasMoreTokens()) {
			final String token = tokenizer.nextToken();
			String value = null;
			if (token.equals("\"")) {
				if (!inPhrase) {
					// Start of phrase
					inPhrase = true;
				} else {
					// End of phrase
					inPhrase = false;
					if (phraseStr != null && phraseStr.trim().length() > 0) {
						searchValues.add("\"" + phraseStr.trim() + "\"");
						phraseStr = null;
					}
				}
			} else if (inPhrase) {
				phraseStr = token;
			} else {
				value = token;
				searchValues.addAll(getTerms(value));
			}
		}
		if (inPhrase && phraseStr != null) {
			searchValues.addAll(getTerms(phraseStr));
		}
		return searchValues;
	}

	private static List<String> getTerms(final String untokenizedWords) {
		List<String> individualWords = new ArrayList<String>();

		StringTokenizer tokenizer = new StringTokenizer(untokenizedWords, " ");
		while (tokenizer.hasMoreTokens()) {
			final String word = tokenizer.nextToken().trim();
			if (word.length() > 0) {
				individualWords.add(word);
			}
		}
		return individualWords;
	}
}
