package com.eora.dctm.mobile4webtop.common;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import com.documentum.fc.common.DfLogger;
import com.documentum.web.formext.common.ClientSessionState;

/**
 * 
 * @author S.Jonckheere
 * @since 1.0.0
 * 
 */public class UserAgentUtil {

	
	public static final String USERAGENT_TYPE = "useragent-type";
	
	public static final String OTHER_USERAGENT_TYPE = "other";
	public static final String MOBILE_USERAGENT_TYPE = "mobile";
	
	public static final String ANDROID = "android";
	public static final String IPHONE = "iphone";
	public static final String IPAD = "ipad";
	public static final String WINDOWS_PHONE = "windows phone";
	
	private static final Logger LOGGER = DfLogger.getLogger(UserAgentUtil.class);
	
	
	public static void setUserAgentType( final HttpServletRequest request) {
		final String userAgentType = getRequestUserAgentType(request);
		ClientSessionState.setAttribute(USERAGENT_TYPE, userAgentType);
	}
	
	public static String getRequestUserAgentType( final HttpServletRequest request ){
		String userAgentType = OTHER_USERAGENT_TYPE;
		
		String userAgentHeader = request.getHeader("User-Agent"); 		
		
		if ( userAgentHeader != null ){
			//first convert to lowercase
			userAgentHeader = userAgentHeader.toLowerCase();
			if ( userAgentHeader.indexOf(ANDROID) > -1 ){
				userAgentType = MOBILE_USERAGENT_TYPE;
			} else if ( userAgentHeader.indexOf(IPHONE) > -1 ){
				userAgentType = MOBILE_USERAGENT_TYPE;
			} else if ( userAgentHeader.indexOf(IPAD) > -1){
				userAgentType = MOBILE_USERAGENT_TYPE;
			} else if ( userAgentHeader.indexOf( WINDOWS_PHONE) > -1 ){
				userAgentType = MOBILE_USERAGENT_TYPE;
			}
		}
		if (LOGGER.isDebugEnabled()){
			LOGGER.debug("Client useragent: "  + userAgentHeader);
			LOGGER.debug("evaluated agent: " + userAgentType);
		}
		return userAgentType;
	}
	
}
