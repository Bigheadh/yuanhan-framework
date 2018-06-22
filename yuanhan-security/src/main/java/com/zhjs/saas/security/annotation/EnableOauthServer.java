package com.yuanhan.yuanhan.security.annotation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Import;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;

import com.yuanhan.yuanhan.security.config.Oauth2SecurityConfig;
import com.yuanhan.yuanhan.security.config.OauthServerConfig;
import com.yuanhan.yuanhan.security.config.ResourceServerConfig;

/**
 * 
 * @author:		yuanhan
 * @since:		2017-12-11
 * @modified:	2017-12-11
 * @version:	
 */
@Documented
@Retention(RUNTIME)
@Target(TYPE)
@EnableAuthorizationServer
@EnableResourceServer
@Import({OauthServerConfig.class, ResourceServerConfig.class, Oauth2SecurityConfig.class})
public @interface EnableOauthServer
{
	public static final String TokenExpired = "yuanhan.oauth.token-access.expired";

}
