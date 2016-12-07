package com.eora.dctm.mobile4webtop.reposbrowser;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.documentum.fc.common.DfId;
import com.documentum.web.common.ArgumentList;
import com.documentum.web.common.WrapperRuntimeException;
import com.documentum.web.form.Control;
import com.documentum.web.form.control.Button;
import com.documentum.web.formext.action.ActionService;
import com.documentum.web.formext.component.Component;
import com.documentum.web.formext.config.ConfigElement;
import com.documentum.web.formext.config.IConfigElement;
import com.eora.dctm.mobile4webtop.reposbrowser.browser.MobileFolderBrowserHelper;
import com.eora.dctm.mobile4webtop.reposbrowser.content.MobileContentHelper;
import com.eora.dctm.mobile4webtop.reposbrowser.locations.MobileLocationsHelper;
import com.eora.dctm.mobile4webtop.reposbrowser.properties.IAttributeValueFormatter;
import com.eora.dctm.mobile4webtop.reposbrowser.properties.MobilePropertiesHelper;
import com.eora.dctm.mobile4webtop.reposbrowser.properties.StandardAttributesValueFormatter;
import com.eora.dctm.mobile4webtop.reposbrowser.search.MobileSearchHelper;
import com.eora.dctm.mobile4webtop.reposbrowser.subscriptions.MobileSubscriptionsHelper;
import com.eora.dctm.mobile4webtop.reposbrowser.versions.MobileObjectVersionHelper;

/**
 * 
 * Main Entry class for client javascript calls
 * 
 * @author S.Jonckheere
 * @since 1.0.0
 * 
 */
public class MobileReposBrowser extends Component {

	public final static String MOBILE_REPOSITORY_BROWSER_COMPONENT = "mobile_reposbrowser";
	public final static String CFG_HEADER_LOGO_URL = "header-logo-url";

	/**
	 * Generated SerialID
	 */
	private static final long serialVersionUID = -699808538235991126L;
	private static final String FOLDERID_PREFIX = "0b";

	private String initFolderIdStr;
	private MobileSearchHelper searchHelper;
	private Map<String, IAttributeValueFormatter> attributeValueFormatterMap;
	private IAttributeValueFormatter standardAttributeValueFormatter;

	@Override
	public void onInit(ArgumentList args) {
		super.onInit(args);

		final String objectIdStr = args.get("objectId");
		if ( objectIdStr != null && new DfId(objectIdStr).isObjectId() && objectIdStr.startsWith(FOLDERID_PREFIX)){
			initFolderIdStr = objectIdStr;
		}
		// Init search helper
		this.searchHelper = new MobileSearchHelper();

		//Initialize default and custom attribute value formatters
		initAttributeValueFormatters();
	}
	
	@SuppressWarnings("unchecked")
	private void initAttributeValueFormatters(){
		this.standardAttributeValueFormatter = new StandardAttributesValueFormatter(this);
		this.attributeValueFormatterMap = new HashMap<String, IAttributeValueFormatter>();
		
		final IConfigElement icfgElement = lookupElement("attribute-value-formatters");
		if (icfgElement != null) {
			final Iterator<ConfigElement> formatterIter = icfgElement.getChildElements();
			while (formatterIter.hasNext()) {
				final ConfigElement formatterElem = (ConfigElement) formatterIter.next();
				final String attributeName = formatterElem.getChildValue("attribute");
				final String className = formatterElem.getChildValue("class");
				
				if ( !attributeValueFormatterMap.containsKey(attributeName)){
					try {
						attributeValueFormatterMap.put(attributeName, (IAttributeValueFormatter) Class.forName(className).newInstance());
					} catch (Exception ex) {
						throw new WrapperRuntimeException("Error initializing attribute fomatters",ex);
					}
				}
			}
		}
	}
	
	public IAttributeValueFormatter getAttributeValueFormater( final String attributeName ){
		if ( this.attributeValueFormatterMap.containsKey(attributeName)){
			return this.attributeValueFormatterMap.get(attributeName);
		} else {
			return this.standardAttributeValueFormatter;
		}
	}

	public String getInitFolderId() {
		return initFolderIdStr;
	}
	
	public boolean isAddContentAndEditAttrEnabled(){
		return lookupBoolean("enable-addedit-content");
	}
	
	/*
	 * Method called by JSP javascript
	 */
	public void getParentFolderContentsJSON(final Control control, final ArgumentList args) {
		MobileFolderBrowserHelper.getParentFolderContentsJSON(this, args);
	}

	/*
	 * Method called by JSP javascript
	 */
	public void getFolderContentsJSON(final Control control, final ArgumentList args) {
		MobileFolderBrowserHelper.getFolderContentsJSON(this, args);
	}

	/*
	 * Method called by JSP javascript
	 */
	public void getObjectPropertiesJSON(final Control control, final ArgumentList args) {
		MobilePropertiesHelper.getObjectPropertiesJSON(this, args);
	}

	/*
	 * Method called by JSP javascript
	 */
	public void getObjectContentUrlJSON(final Control control, final ArgumentList args) {
		MobileContentHelper.getObjectContentUrlJSON(this, args);
	}

	/*
	 * Method called by JSP javascript
	 */
	public void executeSearchJSON(final Control control, final ArgumentList arguments) {
		searchHelper.executeSearchJSON(this,arguments);
	}

	/*
	 * Method called by JSP javascript
	 */
	public void getNextSearchResultsJSON(final Control control, final ArgumentList arguments) {
		searchHelper.getNextSearchResultsJSON(this,arguments);
	}

	/*
	 * Method called by JSP javascript
	 */
	public void getSubscriptionsJSON(final Control control, final ArgumentList arguments) {
		MobileSubscriptionsHelper.getSubscriptionsJSON(this, arguments);
	}
	
	/*
	 * Method called by JSP javascript
	 */
	public void subscribeObjectJSON(final Control control, final ArgumentList arguments) {
		MobileSubscriptionsHelper.subscribeObjectJSON(this, arguments);
	}

	/*
	 * Method called by JSP javascript
	 */
	public void getObjectLocationsJSON( final Control control, final ArgumentList arguments ){
		MobileLocationsHelper.getObjectLocationsJSON(this, arguments);
	}

	/*
	 * Method called by JSP javascript
	 */
	public void getObjectVersionsJSON( final Control control, final ArgumentList arguments ){
		MobileObjectVersionHelper.getObjectVersionsJSON(this, arguments);
	}

	public void onClickImport(final Button button,final ArgumentList argList ){
		final String currentFolderPathIds = argList.get("folderPathIds");
		
		if ( currentFolderPathIds != null && !currentFolderPathIds.trim().equals("")){
			
			int startPosLastId = currentFolderPathIds.lastIndexOf(".") + 1;
			if (startPosLastId >= 0) {
			
				final String folderIdStr = currentFolderPathIds.substring(startPosLastId);
				
				final ArgumentList actionArgs = new ArgumentList();
				actionArgs.add("objectId",folderIdStr);
				
				boolean canExecute = ActionService.queryExecute("mobileimport", actionArgs, getContext(), this);
				boolean hasExecuted = false;
				if ( canExecute ){
					hasExecuted = ActionService.execute("mobileimport", actionArgs, getContext(), this, null);
				}
			}
		}
	}
	
}
