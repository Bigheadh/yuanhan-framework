package com.yuanhan.yuanhan.security.config;

import java.util.Arrays;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.client.ClientCredentialsTokenEndpointFilter;
import org.springframework.security.oauth2.provider.client.JdbcClientDetailsService;
import org.springframework.security.oauth2.provider.error.OAuth2AuthenticationEntryPoint;
import org.springframework.security.oauth2.provider.error.WebResponseExceptionTranslator;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.web.AuthenticationEntryPoint;

import com.yuanhan.yuanhan.core.annotation.CommonAutowired;
import com.yuanhan.yuanhan.core.annotation.CommonQualifier;
import com.yuanhan.yuanhan.security.exception.OAuthExceptionRenderer;
import com.yuanhan.yuanhan.security.exception.OAuthResponseExceptionTranslator;

/**
 * 
 * @author: 	yuanhan
 * @since: 		2017-12-11
 * @modified: 	2017-12-11
 * @version:
 */
@Configuration
@CommonAutowired
public class OauthServerConfig extends AuthorizationServerConfigurerAdapter
{
	private AuthenticationManager authenticationManager;
	private DataSource dataSource;
	@CommonQualifier
	private TokenEnhancer customTokenEnhancer;

	@Bean
	public TokenStore tokenStore()
	{
		return new JdbcTokenStore(dataSource);
	}
	
	@Bean
    public JwtAccessTokenConverter accessTokenConverter() {
        JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
        converter.setSigningKey(JwtCryptor.SignatureKey);
        return converter;
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

	@Bean
	@Primary
	public ClientDetailsService clientDetails()
	{
		return new JdbcClientDetailsService(dataSource);
	}

	@Primary
	@Bean
	public AuthorizationServerTokenServices tokenServices(AuthorizationServerEndpointsConfigurer endpoints)
	{
		DefaultTokenServices tokenServices = new DefaultTokenServices();
		tokenServices.setTokenStore(endpoints.getTokenStore());
		tokenServices.setSupportRefreshToken(true);
		tokenServices.setReuseRefreshToken(true);
		tokenServices.setClientDetailsService(endpoints.getClientDetailsService());
		tokenServices.setTokenEnhancer(endpoints.getTokenEnhancer());
		/*long seconds = TimeUnit.DAYS.toSeconds(env.getProperty(EnableOauthServer.TokenExpired, int.class, 30));
		tokenServices.setAccessTokenValiditySeconds((int)seconds);
		tokenServices.setRefreshTokenValiditySeconds((int)seconds);*/
		return tokenServices;
	}

	@Override
	public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception
	{
		TokenEnhancerChain tokenEnhancerChain = new TokenEnhancerChain();
		tokenEnhancerChain.setTokenEnhancers(Arrays.asList(customTokenEnhancer, accessTokenConverter()));
		endpoints.setClientDetailsService(clientDetails());
		endpoints.authenticationManager(authenticationManager)
				.allowedTokenEndpointRequestMethods(HttpMethod.GET, HttpMethod.POST, HttpMethod.OPTIONS)
				.tokenStore(tokenStore())
				.accessTokenConverter(accessTokenConverter())
				.tokenEnhancer(tokenEnhancerChain)
				.tokenServices(tokenServices(endpoints))
				.exceptionTranslator(exceptionTranslator());
	}
	
	@Override
	public void configure(AuthorizationServerSecurityConfigurer security) throws Exception 
	{
		security.tokenKeyAccess("permitAll()")
				.checkTokenAccess("isAuthenticated()")
				.allowFormAuthenticationForClients()
				.authenticationEntryPoint(authenticationEntryPoint());
				//.addTokenEndpointAuthenticationFilter(clientCredentialsFilter(security));
	}

	public ClientCredentialsTokenEndpointFilter clientCredentialsFilter(AuthorizationServerSecurityConfigurer security)
	{
		ClientCredentialsTokenEndpointFilter filter = new ClientCredentialsTokenEndpointFilter("/oauth/token");
		//filter.setAuthenticationManager(http.getSharedObject(AuthenticationManager.class));
		OAuth2AuthenticationEntryPoint entryPoint = new OAuth2AuthenticationEntryPoint();
		entryPoint.setTypeName("Form");
		entryPoint.setRealmName("oauth2/client");
    	entryPoint.setExceptionRenderer(new OAuthExceptionRenderer());
    	entryPoint.setExceptionTranslator(exceptionTranslator());
		filter.setAuthenticationEntryPoint(entryPoint);
		return filter;
	}

	@Override
	public void configure(ClientDetailsServiceConfigurer clients) throws Exception 
	{
		clients.withClientDetails(clientDetails());
	}

}
