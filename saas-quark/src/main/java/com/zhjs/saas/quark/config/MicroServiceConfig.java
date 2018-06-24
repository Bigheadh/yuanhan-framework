package com.zhjs.saas.quark.config;

import static com.zhjs.saas.core.config.PropertyConfig.Fastjson_Enable;
import static com.zhjs.saas.core.config.PropertyConfig.Global_DateFormat;
import static com.zhjs.saas.core.config.PropertyConfig.LoadBalance_All_Enable;

import org.springframework.beans.BeansException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.zhjs.saas.core.web.ServiceTemplate;
import com.zhjs.saas.core.web.ServiceTemplateConverter;

/**
 * 
 * @author:		Jackie Wang 
 * @since:		2017-11-07
 * @modified:	2017-11-07
 * @version:	
 */
@Configuration
public class MicroServiceConfig implements ApplicationContextAware
{
	private ApplicationContext context;
	
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException
	{
		this.context = applicationContext;		
	}
/*
	@Bean
	public static MicroServiceBeanPostProcessor microServiceProcessor()
	{
		return new MicroServiceBeanPostProcessor();
	}
*/
	
	@Bean
	@ConditionalOnProperty(name=Fastjson_Enable, havingValue="true")
	public ServiceTemplateConverter serviceTemplateConverter()
	{
		return new ServiceTemplateConverter(context.getEnvironment().getProperty(Global_DateFormat));
	}
	
	@Bean
	@LoadBalanced
	@ConditionalOnBean(ServiceTemplateConverter.class)
	@ConditionalOnProperty(name=LoadBalance_All_Enable, havingValue="true")
	public ServiceTemplate serviceTemplate()
	{
		return new ServiceTemplate().addMessageConverter(context.getBean(ServiceTemplateConverter.class),false);
	}

	@Bean
	@ConditionalOnMissingBean
	@ConditionalOnBean(ServiceTemplateConverter.class)
	public ServiceTemplate restTemplate()
	{
		return new ServiceTemplate().addMessageConverter(context.getBean(ServiceTemplateConverter.class),false);
	}

	
}
