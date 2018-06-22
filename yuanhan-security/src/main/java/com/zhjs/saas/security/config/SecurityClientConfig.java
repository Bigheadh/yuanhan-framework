package com.yuanhan.yuanhan.security.config;

import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import com.yuanhan.yuanhan.core.annotation.CommonAutowired;

/**
 * 
 * @author:		yuanhan
 * @since:		2017-12-12
 * @modified:	2017-12-12
 * @version:	
 */
@Configuration
@CommonAutowired
@EnableOAuth2Sso
public class SecurityClientConfig extends WebSecurityConfigurerAdapter
{

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception 
	{
		super.configure(auth);
	}
	
	@Override
	public void configure(HttpSecurity http) throws Exception 
	{
		http.authorizeRequests().anyRequest().authenticated();
	}
	
}
