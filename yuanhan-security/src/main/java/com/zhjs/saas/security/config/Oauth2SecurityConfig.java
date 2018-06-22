package com.yuanhan.yuanhan.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.provider.client.ClientCredentialsTokenEndpointFilter;
import org.springframework.security.oauth2.provider.error.OAuth2AuthenticationEntryPoint;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.cors.CorsUtils;

import com.yuanhan.yuanhan.core.annotation.CommonAutowired;
import com.yuanhan.yuanhan.core.annotation.CommonQualifier;
import com.yuanhan.yuanhan.security.exception.OAuthExceptionRenderer;
import com.yuanhan.yuanhan.security.matcher.HttpSecurityMatcher;

/**
 * 
 * @author:		yuanhan
 * @since:		2017-12-12
 * @modified:	2017-12-12
 * @version:	
 */
@CommonAutowired
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled=true, securedEnabled=true, jsr250Enabled=true)
public class Oauth2SecurityConfig extends WebSecurityConfigurerAdapter
{
	@CommonQualifier
	private UserDetailsService userDetailsService;
	
	private HttpSecurityMatcher httpMatcher;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService==null ? userDetailsService() : userDetailsService)
        	.passwordEncoder(new BCryptPasswordEncoder());
    }

    //不定义没有password grant_type
    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
    
    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() throws Exception {
    	OAuth2AuthenticationEntryPoint entryPoint = new OAuth2AuthenticationEntryPoint();
    	entryPoint.setExceptionRenderer(new OAuthExceptionRenderer());
    	return entryPoint;
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers(HttpMethod.OPTIONS);
    }
	
	@Override
	public void configure(HttpSecurity http) throws Exception 
	{
		ClientCredentialsTokenEndpointFilter filter = new ClientCredentialsTokenEndpointFilter("/oauth/token");
		filter.setAuthenticationManager(http.getSharedObject(AuthenticationManager.class));
		OAuth2AuthenticationEntryPoint entryPoint = new OAuth2AuthenticationEntryPoint();
		entryPoint.setTypeName("Form");
		entryPoint.setRealmName("oauth2/client");
    	entryPoint.setExceptionRenderer(new OAuthExceptionRenderer());
    	//entryPoint.setExceptionTranslator(exceptionTranslator());
		filter.setAuthenticationEntryPoint(entryPoint);
        http.addFilterBefore(filter, BasicAuthenticationFilter.class);

        /*UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration corsCfg = new CorsConfiguration();
        corsCfg.setAllowCredentials(true);
        corsCfg.addAllowedOrigin("*");
        corsCfg.addAllowedHeader("*");
        corsCfg.addAllowedMethod("*");
        source.registerCorsConfiguration("/**", corsCfg);
        http.addFilterBefore(new CorsFilter(source), BasicAuthenticationFilter.class);*/
		
		http.cors().and().csrf().disable()
			.exceptionHandling().authenticationEntryPoint(authenticationEntryPoint())
			.and()
			.requestMatchers().antMatchers(HttpMethod.OPTIONS)
			.and()
			.authorizeRequests().requestMatchers(CorsUtils::isPreFlightRequest).permitAll();
		
		if(httpMatcher!=null)
			httpMatcher.config(http);
		else
			http.anonymous().disable()
				.requestMatchers().antMatchers("/**/oauth/**")
				.and()
				.authorizeRequests().antMatchers("/**/oauth/**").permitAll();
	}
	
}
