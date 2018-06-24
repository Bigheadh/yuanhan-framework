package com.zhjs.saas.security.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.util.OAuth2Utils;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;

import com.zhjs.saas.core.util.WebUtil;

/**
 * 
 * @author:		Jackie Wang 
 * @since:		2018-03-09
 * @modified:	2018-03-09
 * @version:	
 */
public abstract class OAuth2Util extends OAuth2Utils
{
	
	public static Object contextPrincipal() 
	{
		Authentication auth =SecurityContextHolder.getContext().getAuthentication();
		if(auth==null)
			return null;
		
		return auth.getPrincipal();
	}

	
	public static String tokenPrincipal() 
	{
		return String.valueOf(WebUtil.getRequest().getAttribute(OAuth2AuthenticationDetails.ACCESS_TOKEN_VALUE));
	}

	
	public static String token() 
	{
		return WebUtil.getRequest().getParameter(OAuth2AccessToken.ACCESS_TOKEN);
	}

	
	public static String tokenType() 
	{
		return String.valueOf(WebUtil.getRequest().getAttribute(OAuth2AuthenticationDetails.ACCESS_TOKEN_TYPE));
	}
}
