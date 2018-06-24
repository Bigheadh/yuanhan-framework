package com.zhjs.saas.security.matcher;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;

/**
 * 
 * @author:		Jackie Wang 
 * @since:		2018-02-27
 * @modified:	2018-02-27
 * @version:	
 */
public interface HttpSecurityMatcher
{
	
	public static final int Ant = 1;
	public static final int Regrex = 2;
	
	public void config(HttpSecurity http) throws Exception;
	
	public void refresh() throws Exception;
	
	public void setHttpSecurity(HttpSecurity http);
	
	public HttpSecurity getHttpSecurity();

}
