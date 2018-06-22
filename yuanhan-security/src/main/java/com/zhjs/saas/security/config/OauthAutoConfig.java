package com.yuanhan.yuanhan.security.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import com.yuanhan.yuanhan.security.exception.OAuthExceptionHandlerResolver;

/**
 * 
 * @author:		yuanhan
 * @since:		2018-04-12
 * @modified:	2018-04-12
 * @version:	
 */
@Configuration
public class OauthAutoConfig
{
	//@Bean
	@Order
	public OAuthExceptionHandlerResolver handlerExceptionResolver()
	{
		return new OAuthExceptionHandlerResolver();
	}

}
