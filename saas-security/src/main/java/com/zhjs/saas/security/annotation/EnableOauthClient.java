package com.zhjs.saas.security.annotation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;
import org.springframework.context.annotation.Import;

import com.zhjs.saas.security.config.SecurityClientConfig;

/**
 * 
 * @author:		Jackie Wang 
 * @since:		2017-12-11
 * @modified:	2017-12-11
 * @version:	
 */
@Documented
@Retention(RUNTIME)
@Target(TYPE)
@EnableOAuth2Sso
@Import(SecurityClientConfig.class)
public @interface EnableOauthClient
{

}
