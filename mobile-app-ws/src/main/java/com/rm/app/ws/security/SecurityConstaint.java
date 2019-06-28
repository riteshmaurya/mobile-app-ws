package com.rm.app.ws.security;

import com.rm.app.ws.SpringApplicationContext;
import com.rm.app.ws.shared.AppProperties;

public class SecurityConstaint {
	
	public static final String SIGN_UP_URL = "/users";
	public static final String VERIFICATION_EMAIL_URL = "/users/email-verification";
	public static final String TOKEN_PREFIX = "Bearer ";
	public static final String HEADER_STRING = "Authorization";
	
	public static final long EXPIRATION_TIME = 864000000;
	
	public static String getTokenSecret(){
		AppProperties appProperties = (AppProperties)SpringApplicationContext.getBean("AppProperties");
		return appProperties.getTokenSecret();
	}
	
	

}
