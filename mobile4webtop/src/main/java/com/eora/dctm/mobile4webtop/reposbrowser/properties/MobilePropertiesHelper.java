package com.eora.dctm.mobile4webtop.reposbrowser.properties;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.documentum.fc.client.DfQuery;
import com.documentum.fc.client.IDfCollection;
import com.documentum.fc.client.IDfQuery;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.client.IDfSessionManager;
import com.documentum.fc.client.IDfSysObject;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.DfId;
import com.documentum.fc.common.DfLogger;
import com.documentum.fc.common.IDfId;
import com.documentum.web.common.ArgumentList;
import com.documentum.web.common.LocaleService;
import com.documentum.web.form.FormRequest;
import com.documentum.web.formext.component.Component;
import com.documentum.web.formext.config.ConfigService;
import com.documentum.web.formext.config.Context;
import com.documentum.web.formext.config.IConfigElement;
import com.documentum.web.formext.config.IConfigLookup;
import com.documentum.web.formext.control.docbase.DocbaseIcon;
import com.documentum.web.formext.control.docbase.DocbaseLockIcon;
import com.documentum.web.formext.control.docbase.DocbaseObject;
import com.documentum.web.formext.drl.DRLComponent;
import com.documentum.web.formext.session.SessionManagerHttpBinding;
import com.documentum.web.util.SafeHTMLString;
import com.eora.dctm.mobile4webtop.common.QueryManager;
import com.eora.dctm.mobile4webtop.reposbrowser.MobileReposBrowser;
import com.eora.dctm.mobile4webtop.reposbrowser.common.DocbaseIconUtil;
import com.eora.dctm.mobile4webtop.reposbrowser.subscriptions.MobileSubscriptionsHelper;

/**
 * 
 * @author S.Jonckheere
 * @since 1.0.0
 * 
 */
public class MobilePropertiesHelper {

	private static Logger LOGGER = DfLogger.getLogger(MobilePropertiesHelper.class);

	/** Contains property categories for each type per locale */
	private static Map<String,Map<String, List<CategoryBean>>> categoriesLocalesMap = new HashMap<String, Map<String,List<CategoryBean>>>();
	
	private static final String APPLICATION_CONTEXT = "custom";

	private static final String MOBILE_VIEW_ATTRIBUTES_ATTRLIST = "mobile_view_attrlist";

	public static void getObjectPropertiesJSON(final Component component, final ArgumentList args) {
		long startTime = System.currentTimeMillis();

		FormRequest formRequest = component.getFormRequest();
		if (formRequest.isInlineRequest()) {

			final String objectIdStr = args.get("objectId");
			final String showEmptyValuesStr = args.get("showEmptyValues");

			List<CategoryBean> categoryBeans = new ArrayList<CategoryBean>();
			String emailBodyDrl = null;
			String objectName = null;
			String objectDocbaseIconUrl = null;
			boolean showEmptyValues =  Boolean.valueOf(showEmptyValuesStr);
			String objectLockIconUrl = null; 
			boolean canSubscribeObject = false;
			boolean canUnSubscribeObject = false;
			
			if (DfId.isObjectId(objectIdStr)) {
				try {
					long startFetchObject = System.currentTimeMillis();

					final IDfSessionManager sMgr = SessionManagerHttpBinding.getSessionManager();
					IDfSession session = null;
					try {
						session = sMgr.getSession(SessionManagerHttpBinding.getCurrentDocbase());

						final IDfSysObject sysobject = (IDfSysObject) session.getObjectByQualification("dm_sysobject(all) where r_object_id = ID('" + new DfId(objectIdStr) + "')");
						LOGGER.debug("Ready fetch object: " + (System.currentTimeMillis() - startFetchObject));

						if (sysobject != null) {
							
							//Get docbase icon url
							objectDocbaseIconUrl = getObjectDocbaseIconUrl(sysobject, component);
							
							//Get lockincon url
							objectLockIconUrl = getObjectDocbaseLockIconUrl(sysobject, component);
							
							//Get objectName
							objectName = SafeHTMLString.escapeText(sysobject.getObjectName());
							
							// read attributes to show for this type from component config
							categoryBeans = getAttributeCategoriesForType(component, sysobject.getTypeName(), args, session);

							// now populate values
							populateObjectPropertiesEx(component, sysobject, categoryBeans, showEmptyValues);

							//Get email DRL
							emailBodyDrl = getEmailBodyDrl(component, sysobject);
							
							//Check subscription options
							canSubscribeObject = MobileSubscriptionsHelper.canSubscribeObject(sysobject.getObjectId(), component);
							canUnSubscribeObject = MobileSubscriptionsHelper.canUnSubscribeObject(sysobject.getObjectId(), component);
						}
					} finally {
						if (session != null) {
							sMgr.release(session);
						}
					}

				} catch (DfException ex) {
					LOGGER.warn("Error getObjectPropertiesJSON", ex);
				}
			}

			final Map<String, Object> responseData = new HashMap<String, Object>();
			responseData.put("JSON_CATEGORY_BEANS", categoryBeans);
			responseData.put("JSON_EMAIL_DRL", emailBodyDrl);
			responseData.put("JSON_OBJECT_DOCBASE_FORMAT_ICON_URL", objectDocbaseIconUrl);
			responseData.put("JSON_OBJECT_LOCK_ICON_URL", objectLockIconUrl);
			responseData.put("JSON_OBJECT_NAME", objectName);
			responseData.put("JSON_CAN_SUBSCRIBE_OBJECT", canSubscribeObject);
			responseData.put("JSON_CAN_UNSUBSCRIBE_OBJECT", canUnSubscribeObject);
			
			// Render JSON output
			component.setRedirectJsonRendererUrl(responseData);
		}
		LOGGER.debug("Ready getObjectPropertiesJSON: " + (System.currentTimeMillis() - startTime));
	}

	private static List<CategoryBean> readTypeAttributeCategories(final Component component, final String typeName, final ArgumentList args, final IDfSession session) throws DfException {
		long startTime = System.currentTimeMillis();

		final List<CategoryBean> categories = new ArrayList<CategoryBean>();			
		
		//Set the context
		Context con = component.getContext();
		con.set("type", typeName);
		con.set("application", APPLICATION_CONTEXT);
		
		//Initialize DocbaseObject control
	    DocbaseObject localDocbaseObject = (DocbaseObject) component.getControl("obj", DocbaseObject.class);
	    localDocbaseObject.setType(typeName);
	    
	    //Retrieve config lookup
	    IConfigLookup localIConfigLookup = ConfigService.getConfigLookup();
	    
	    //Retrieve expanded category (Category which will initially be expanded in the user interface)
	    String expandedCategory = retrieveExpandedCategory(localIConfigLookup, con);

		//Check if DataDictionaryPopulation is enabled in the attributelist xml
	    boolean enableDataDictionaryPopulation = isEnabledDataDictionary(localIConfigLookup, con);
	    
	    //Retrieve the application name from the ddscopes in the attributelist xml
	    String datadictionaryApplicationName = getApplicationName(localIConfigLookup, con);

	    LOGGER.debug("Enable DataDictionary Population value: " + enableDataDictionaryPopulation + " Scope/Application: " + datadictionaryApplicationName);
	    
	    //Get the list of attributes that should be visible in the ui, based on the is_hidden value
	    Map<String, String> visibleAttributesMap = getVisibleAttributesMap(localDocbaseObject, component);

	    //Retrieve categories and attributes 
	    if (enableDataDictionaryPopulation) {
	    	//Retrieve categories and attributes from the data dictionary
		    StringBuffer localStringBuffer = new StringBuffer(64);
		    localStringBuffer.append("displayconfig.root.").append(datadictionaryApplicationName);	
		    IConfigElement configElem = com.documentum.web.formext.config.ConfigService.getConfigLookup().lookupElement(localStringBuffer.toString(), component.getContext());
		    readDOMObjects(configElem, visibleAttributesMap, categories, expandedCategory);

	    } else {
	    	//Retrieve categories and attributes from the attributelist configuration xml
	    	IConfigElement localIConfigElement = ConfigService.getConfigLookup().lookupElement(getLookupKey(MOBILE_VIEW_ATTRIBUTES_ATTRLIST), component.getContext());
	    	readDOMObjects(localIConfigElement, visibleAttributesMap, categories, expandedCategory);
	    }
		
		LOGGER.debug("Ready readTypeAttributeCategories: " + (System.currentTimeMillis() - startTime));
		return categories;
	}
	
	private static String getApplicationName (IConfigLookup localIConfigLookup, Context con) {
		String datadictionaryApplicationName = null;
	    StringBuffer localLookupStringBuffer = new StringBuffer(128);
	    localLookupStringBuffer.append(getLookupKey(MOBILE_VIEW_ATTRIBUTES_ATTRLIST)).append(".").append("data_dictionary_population");
	    IConfigElement configElementApplication = localIConfigLookup.lookupElement(localLookupStringBuffer.toString(), con).getChildElement("ddscopes").getChildElement("ddscope");
	    if (configElementApplication != null)
	    {
	      String applicationName = configElementApplication.getValue();
	      if (applicationName != null) {
	    	  datadictionaryApplicationName = applicationName;
	      }
	    }
	    return datadictionaryApplicationName;
	}
	
	private static boolean isEnabledDataDictionary(IConfigLookup localIConfigLookup, Context con) {
		boolean localEnableDDPopulation = false;
	    StringBuffer localLookupStringBuffer = new StringBuffer(128);
	    localLookupStringBuffer.append(getLookupKey(MOBILE_VIEW_ATTRIBUTES_ATTRLIST)).append(".").append("data_dictionary_population");
	    IConfigElement configElementEnabled = localIConfigLookup.lookupElement(localLookupStringBuffer.toString(), con).getChildElement("enable");
	    if (configElementEnabled != null)
	    {
	      Boolean isEnabled = configElementEnabled.getValueAsBoolean();
	      if (isEnabled != null) {
	    	  localEnableDDPopulation = isEnabled.booleanValue();
	      }
	    }
	    return localEnableDDPopulation;
	}
	
	private static String retrieveExpandedCategory(IConfigLookup localIConfigLookup, Context con) {
	    String expandedCategory = "";
		StringBuffer expandedLookupStringBuffer = new StringBuffer(128);
	    expandedLookupStringBuffer.append(getLookupKey(MOBILE_VIEW_ATTRIBUTES_ATTRLIST)).append(".").append("expanded_category_id");
	    IConfigElement configElementExpanded = localIConfigLookup.lookupElement(expandedLookupStringBuffer.toString(), con);
	    if (configElementExpanded != null)
	    {
	      String expandedName = configElementExpanded.getValue();
	      if (expandedName != null) {
	    	  expandedCategory = expandedName;
	      }
	    }
	    
	    return expandedCategory;
	}
	
	@SuppressWarnings("unchecked")
	private static void readDOMObjects (IConfigElement configElem, Map<String, String> mapAttrToVisibility, List<CategoryBean> categoryList, String expandedCategoryId) throws DfException {
		if (configElem != null)
		{
			List<String> categoriesFromXml = new ArrayList<String>(101);
			Iterator<IConfigElement> iterCategories = configElem.getChildElements("category");
			
			//Iterate over the categories and turn them into categoryBeans
			while (iterCategories.hasNext())
			{
				IConfigElement configCategory = (IConfigElement)iterCategories.next();
	
				String strCategoryName = configCategory.getAttributeValue("id");
				if (strCategoryName == null)
				{
					throw new IllegalStateException("Category id is not being specified in the configuration file");
				}
				if (categoriesFromXml.contains(strCategoryName))
				{
					throw new IllegalStateException("Duplicate category entry for: " + strCategoryName);
				}
				categoriesFromXml.add(strCategoryName);
	
				IConfigElement elemName = configCategory.getChildElement("name");
				String strCategoryDisplayName = null;
				if (elemName != null)
				{
					strCategoryDisplayName = elemName.getValue();
				} else {
					throw new IllegalStateException("Category has no name: " + elemName);
				}
		  
				if ((strCategoryDisplayName == null) || (strCategoryDisplayName.length() == 0))
				{
					strCategoryDisplayName = strCategoryName;
				}
		  
				final CategoryBean categoryBean = new CategoryBean();
				categoryBean.setCategoryLabel(strCategoryDisplayName);
				
				if (expandedCategoryId.equals(strCategoryName)) {
					categoryBean.setCollapsed(false);
				} else {
					categoryBean.setCollapsed(true);
				}

				categoryList.add(categoryBean);
	
				//Iterate over the attributes and moreattributes child elements, turn them in to attributeBeans and add the attributeBeans to the current categoryBean. For this application there is no difference between the attributes and moreattributes, they are all displayed equally
				LOGGER.debug("CategoryName: " + strCategoryName + ", CategoryDisplayName: " + strCategoryDisplayName);
				
				IConfigElement attributesConfigElem = configCategory.getChildElement("attributes");
				addAttributeBeansFromConfigElement(attributesConfigElem, mapAttrToVisibility, categoryBean);

				IConfigElement moreAttributesConfigElem = configCategory.getChildElement("moreattributes");
				addAttributeBeansFromConfigElement(moreAttributesConfigElem, mapAttrToVisibility, categoryBean);

			}	
		}
	}
	
	/**
	 * This method takes attributes from the given configElement, checks if they are visible and if so adds them to the given category bean.
	 * @param configElement
	 * @param visibilityMap
	 * @param categoryBean
	 */
	@SuppressWarnings("unchecked")
	private static void addAttributeBeansFromConfigElement(IConfigElement configElement, Map<String, String> visibilityMap, CategoryBean categoryBean) {
		
		if (configElement==null) {
			return;
		}
		
		Iterator<IConfigElement> iterMoreChildElements = configElement.getChildElements();
		while (iterMoreChildElements.hasNext())
		{
			IConfigElement elm = (IConfigElement)iterMoreChildElements.next();
			if (elm != null)
			{
				String strPrimaryElementName = elm.getName();
				if (strPrimaryElementName.equals("attribute"))
				{
					 String strAttrName = elm.getAttributeValue("name");
	        
					 if (visibilityMap.containsKey(strAttrName))
					 {

						 //Create AttributeBean
						 final AttributeBean attrBean = new AttributeBean();
						 attrBean.setName(strAttrName);
						 attrBean.setLabel(SafeHTMLString.escapeText(visibilityMap.get(strAttrName)));

						 categoryBean.addAttributeBean(attrBean);
						 
						 LOGGER.debug("AttrName: " + strAttrName);

					 }

				}
			}
		}
	}

	private static List<CategoryBean> getAttributeCategoriesForType( final Component component, final String typeName, final ArgumentList args, final IDfSession session) throws DfException {
		final String localeStr = LocaleService.getLocale().getLanguage();
		
		List<CategoryBean> attributeCategoryConfigList;
		synchronized (categoriesLocalesMap) {
			/** Contains key: typename value: list of category beans */
			Map<String, List<CategoryBean>> attributeCategoriesMap;
			if ( categoriesLocalesMap.containsKey(localeStr)){
				attributeCategoriesMap = categoriesLocalesMap.get(localeStr);
			} else { 
				attributeCategoriesMap = new HashMap<String, List<CategoryBean>>();
				categoriesLocalesMap.put(localeStr, attributeCategoriesMap);
			}
				
			if (attributeCategoriesMap.containsKey(typeName)) { 
				attributeCategoryConfigList = attributeCategoriesMap.get(typeName);
			} else { 
				attributeCategoryConfigList = readTypeAttributeCategories(component, typeName, args, session);
				attributeCategoriesMap.put(typeName, attributeCategoryConfigList);
			}
		}
		//Now clone config list
		final List<CategoryBean> clonedCategoryList = new ArrayList<CategoryBean>(attributeCategoryConfigList.size());
		for ( CategoryBean categoryConfigBean: attributeCategoryConfigList){
			clonedCategoryList.add(categoryConfigBean.clone());
		}
		return clonedCategoryList;
	}
	
	
	/**
	 * Returns a hashmap of attribute names and labels. It only holds visible attributes (based on their datadictionary is_hidden value)
	 * @param localDocbaseObject
	 * @param component
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static Map<String, String> getVisibleAttributesMap(DocbaseObject localDocbaseObject, Component component) throws DfException {
		
		long startTime = System.currentTimeMillis();

		String lifeCycleState = localDocbaseObject.getLifecycleState();
		if ((lifeCycleState == null) || (lifeCycleState.length() == 0)) {
			lifeCycleState = " ";
		}
		
		String lifeCycleId = localDocbaseObject.getLifecycleId();
		if (!hasTypeOverride(lifeCycleId, lifeCycleState, localDocbaseObject, component)) {
			lifeCycleId = DfId.DF_NULLID.toString();
			lifeCycleState = " ";
		}
		
		String ddLifecycleQuery = QueryManager.getQuery("datadictionary.query", new String[] { lifeCycleId, lifeCycleState, localDocbaseObject.getType() });

		//Retrieve visibility map
		Map<String, String> visibleAttributesMap = new HashMap();
		
		final IDfQuery query = new DfQuery();
		query.setDQL(ddLifecycleQuery);
		
		final IDfSessionManager sMgr = SessionManagerHttpBinding.getSessionManager();
		IDfSession session = null;
		try {
			session = sMgr.getSession(SessionManagerHttpBinding.getCurrentDocbase());

			final IDfCollection col = query.execute(session, IDfQuery.DF_READ_QUERY);

			try {

				while (col.next()) {
					String attributeName = col.getString("attr_name");
					String attributeLabel = col.getString("label_text");
					String isHidden = col.getString("is_hidden");
					if ((isHidden != null) && (isHidden.equals("0"))) {
						((Map<String, String>) visibleAttributesMap).put(attributeName, attributeLabel);
					}
				}
			} finally {
				if (col != null) {
					col.close();
				}
			}
		} finally {
			if (session != null) {
				sMgr.release(session);
			}
		}
		
		LOGGER.debug("Ready getting visible attributes map: " + (System.currentTimeMillis() - startTime));
		return visibleAttributesMap;
			
	}
	
	private static String getLookupKey(String id)
	{
		StringBuffer strBuffer = new StringBuffer(128);
		strBuffer.append("attributelist[id=").append(id).append("]");
	    return strBuffer.toString();
	}
	
	//Check if lifecycle has type override set for the specific state
	private static boolean hasTypeOverride(String lifeCycleId, String lifeCycleState, DocbaseObject localDocbaseObject, Component component) throws DfException {
		long startTime = System.currentTimeMillis();
		boolean hasTypeOverride = false;
		
		if ((lifeCycleId != null) && (!lifeCycleId.equals(DfId.DF_NULLID.toString()))) {
			
			final String lifecycleOverrideQuery = QueryManager.getQuery("lifecycle.type_override.query", new String[] { lifeCycleId });

			final IDfQuery query = new DfQuery();
			query.setDQL(lifecycleOverrideQuery);
			
			final IDfSessionManager sMgr = SessionManagerHttpBinding.getSessionManager();
			IDfSession session = null;
			
			try {
				session = sMgr.getSession(SessionManagerHttpBinding.getCurrentDocbase());

				final IDfCollection col = query.execute(session, IDfQuery.DF_READ_QUERY);

				try {
					while (col.next()) {
						String stateName = col.getString("state_name");
						if ((stateName != null) && (stateName.equals(lifeCycleState))) {
							IDfId localIDfId = col.getId("type_override_id");
							if (!localIDfId.isNull()) {
								hasTypeOverride = true;
							}
						}					
					}
				} finally {
					if (col != null) {
						col.close();
					}
				}
			} finally {
				if (session != null) {
					sMgr.release(session);
				}
			}			

		}
		LOGGER.debug("Has Type Override: " + hasTypeOverride + " " + (System.currentTimeMillis() - startTime));
		return hasTypeOverride;
	}

	private static void populateObjectPropertiesEx(final Component component, final IDfSysObject sysobject, final List<CategoryBean> categoryBeans, final boolean showEmptyValues) throws DfException {
		long startTime = System.currentTimeMillis();
		for (CategoryBean categoryBean : categoryBeans) {
			final List<AttributeBean> attributeBeans = new ArrayList<AttributeBean>();
			// now populate bean
			for (AttributeBean attrBean : categoryBean.getAttributes()) {
				final IAttributeValueFormatter formatter = ((MobileReposBrowser)component).getAttributeValueFormater( attrBean.getName() );
				final String value = formatter.formatAttributeValue(attrBean.getName(), component, sysobject);
				attrBean.setValue( SafeHTMLString.escapeText(value));
				
				if ( (value != null && !value.equals("")) || (( value == null || value.equals("")) && showEmptyValues )){
					attributeBeans.add(attrBean);
				}
			}
			categoryBean.setAttributes(attributeBeans);
		}
		LOGGER.debug("Ready populateObjectPropertiesEx: " + (System.currentTimeMillis() - startTime));
	}
	
	private static String getEmailBodyDrl(final Component component, final IDfSysObject sysobject) throws DfException {
		long startTime = System.currentTimeMillis();

		final StringBuffer emailBodyTextBuf = new StringBuffer();

		final String partialUrl = DRLComponent.constructDRL(sysobject.getObjectId().getId(), null, sysobject.getContentType(), component);
		final String fullUrl = MobileReposBrowser.makeFullUrl(component.getPageContext().getRequest(), partialUrl);

		String messageId = "MSG_EMAIL_BODY_OBJ";
		if (sysobject.getTypeName().equals("dm_folder") || sysobject.getType().isSubTypeOf("dm_folder")) {
			messageId = "MSG_EMAIL_BODY_FOL";
		} else if (sysobject.getTypeName().equals("dm_document") || sysobject.getType().isSubTypeOf("dm_document")) {
			messageId = "MSG_EMAIL_BODY_DOC";
		}
		emailBodyTextBuf.append(component.getString(messageId, new String[] { SafeHTMLString.escapeAttribute(fullUrl), sysobject.getObjectName() }));

		LOGGER.debug("Ready getEmailBodyDrl: " + (System.currentTimeMillis() - startTime));
		return emailBodyTextBuf.toString();
	}
	
	private static String getObjectDocbaseIconUrl( final IDfSysObject sysobject, final Component component ) throws DfException {
		
		String docbaseIconUrl = null;
		
		final DocbaseIcon icon = new DocbaseIcon();
		icon.setForm(component);
		icon.setSize("32");
		
		icon.setFormat(sysobject.getContentType());
		icon.setType(sysobject.getTypeName());
		icon.setIsVirtualDocument(sysobject.isVirtualDocument());
		icon.setIsReference(sysobject.isReference());
		
		docbaseIconUrl = DocbaseIconUtil.getDocbaseIconURL(icon);
		
		return docbaseIconUrl;
	}
	
	private static String getObjectDocbaseLockIconUrl( final IDfSysObject sysobject, final Component component ) throws DfException {
		
		String docbaseLockIconUrl = null;
		
		final DocbaseLockIcon lockIcon = new DocbaseLockIcon();
		lockIcon.setForm(component);
		lockIcon.setLockowner( sysobject.getLockOwner());
		
		docbaseLockIconUrl = DocbaseIconUtil.getDocbaseLockIconURL(lockIcon);
		
		return docbaseLockIconUrl;
	}
	
}
