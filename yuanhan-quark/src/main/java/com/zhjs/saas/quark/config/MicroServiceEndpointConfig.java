package com.yuanhan.yuanhan.quark.config;

import org.springframework.boot.actuate.condition.ConditionalOnEnabledEndpoint;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.yuanhan.yuanhan.quark.endpoint.MicroServiceEndpoint;
import com.yuanhan.yuanhan.quark.endpoint.MicroServiceMvcEndpoint;

/**
 * 
 * @author:		yuanhan
 * @since:		2017-12-08
 * @modified:	2017-12-08
 * @version:	
 */
@Configuration
public class MicroServiceEndpointConfig
{
	
	@Bean
	@ConditionalOnMissingBean
	public MicroServiceEndpoint microserviceEndpoint()
	{
		return new MicroServiceEndpoint();
	}
	
	@Bean
	@ConditionalOnMissingBean
	@ConditionalOnBean(MicroServiceEndpoint.class)
	@ConditionalOnEnabledEndpoint("microservice")
	public MicroServiceMvcEndpoint microserviceMvcEndpoint(MicroServiceEndpoint delegate)
	{
		return new MicroServiceMvcEndpoint(delegate);
	}
	
}
