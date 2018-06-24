package com.zhjs.saas.security.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.error.OAuth2AuthenticationEntryPoint;
import org.springframework.security.oauth2.provider.error.WebResponseExceptionTranslator;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.web.cors.CorsUtils;

import com.zhjs.saas.core.annotation.CommonAutowired;
import com.zhjs.saas.security.exception.OAuthExceptionRenderer;
import com.zhjs.saas.security.exception.OAuthResponseExceptionTranslator;

/**
 * 
 * @author:		Jackie Wang 
 * @since:		2018-01-03
 * @modified:	2018-01-03
 * @version:	
 */
@Configuration
@CommonAutowired
public class ResourceServerConfig extends ResourceServerConfigurerAdapter
{
	@Value("${spring.application.name}")
	private String resourceId;

	
	@Bean
    public JwtAccessTokenConverter accessTokenConverter() {
        JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
        converter.setSigningKey(JwtCryptor.SignatureKey);
        return converter;
    }
	
	@Override
	public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
		resources.resourceId(resourceId)
				.authenticationEntryPoint(authenticationEntryPoint());
	}
	
	@Bean
	public WebResponseExceptionTranslator exceptionTranslator() {
		return new OAuthResponseExceptionTranslator();
	}
    
    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() throws Exception {
    	OAuth2AuthenticationEntryPoint entryPoint = new OAuth2AuthenticationEntryPoint();
    	entryPoint.setExceptionRenderer(new OAuthExceptionRenderer());
    	entryPoint.setExceptionTranslator(exceptionTranslator());
    	return entryPoint;
    }
    
	@Override
	public void configure(HttpSecurity http) throws Exception 
	{
        /*UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration corsCfg = new CorsConfiguration();
        corsCfg.setAllowCredentials(true);
        corsCfg.addAllowedOrigin("*");
        corsCfg.addAllowedHeader("*");
        corsCfg.addAllowedMethod("*");
        source.registerCorsConfiguration("/**", corsCfg);
        http.addFilterBefore(new CorsFilter(source), BasicAuthenticationFilter.class);*/
        
		http.cors().and().anonymous().disable().csrf().disable()
			.exceptionHandling().authenticationEntryPoint(authenticationEntryPoint())
			.and()
			.authorizeRequests().requestMatchers(CorsUtils::isPreFlightRequest).permitAll()
			.and()
			.authorizeRequests().antMatchers(HttpMethod.OPTIONS).permitAll().anyRequest().authenticated();
	}



}
