package com.eora.dctm.mobile4webtop.reposbrowser.common;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 
 * @author S.Jonckheere
 * @since 1.0.0
 * 
 */
public class MobileObjectBean {
	
	
	private Map<String,Object> valuesMap = new LinkedHashMap<String, Object>();
	
	public void setValue( String key, Object value){
		valuesMap.put( key, value );
	}
	
	public Object getValue( String key ){
		return valuesMap.get(key);
	}

	public Map<String, Object> getValuesMap() {
		return valuesMap;
	}

	public void setValuesMap(Map<String, Object> valuesMap) {
		this.valuesMap = valuesMap;
	}
	
	
}