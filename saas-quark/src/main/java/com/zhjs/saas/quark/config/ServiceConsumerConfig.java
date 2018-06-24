package com.zhjs.saas.quark.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.web.HttpMessageConverters;
import org.springframework.cloud.netflix.feign.AnnotatedParameterProcessor;
import org.springframework.cloud.netflix.feign.FeignFormatterRegistrar;
import org.springframework.cloud.netflix.feign.support.SpringDecoder;
import org.springframework.cloud.netflix.feign.support.SpringEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.core.convert.ConversionService;
import org.springframework.format.support.DefaultFormattingConversionService;
import org.springframework.format.support.FormattingConversionService;

import com.netflix.hystrix.HystrixCommand;
import com.zhjs.saas.quark.annotation.support.ServiceConsumerContract;
import com.zhjs.saas.quark.codec.ServiceConsumerDecoder;

import feign.Contract;
import feign.Feign;
import feign.Retryer;
import feign.codec.Decoder;
import feign.codec.Encoder;
import feign.hystrix.HystrixFeign;

/**
 * 
 * @author: 	Jackie Wang
 * @since: 		2017-12-16
 * @modified: 	2017-12-16
 * @version:
 */
@Configuration
public class ServiceConsumerConfig
{
	@Autowired
	private ObjectFactory<HttpMessageConverters> messageConverters;

	@Autowired(required = false)
	private List<AnnotatedParameterProcessor> parameterProcessors = new ArrayList<>();

	@Autowired(required = false)
	private List<FeignFormatterRegistrar> feignFormatterRegistrars = new ArrayList<>();

	@Bean
	@ConditionalOnMissingBean
	public Decoder feignDecoder()
	{
		return new ServiceConsumerDecoder(new SpringDecoder(this.messageConverters));
	}

	@Bean
	@ConditionalOnMissingBean
	public Encoder feignEncoder()
	{
		return new SpringEncoder(this.messageConverters);
	}

	@Bean
	@ConditionalOnMissingBean
	public Contract feignContract(ConversionService feignConversionService)
	{
		return new ServiceConsumerContract(this.parameterProcessors, feignConversionService);
	}

	@Bean
	public FormattingConversionService feignConversionService()
	{
		FormattingConversionService conversionService = new DefaultFormattingConversionService();
		for (FeignFormatterRegistrar feignFormatterRegistrar : feignFormatterRegistrars)
		{
			feignFormatterRegistrar.registerFormatters(conversionService);
		}
		return conversionService;
	}

	@Configuration
	@ConditionalOnClass({ HystrixCommand.class, HystrixFeign.class })
	protected static class HystrixFeignConfiguration
	{
		@Bean
		@Scope("prototype")
		@ConditionalOnMissingBean
		@ConditionalOnProperty(name = "feign.hystrix.enabled", matchIfMissing = false)
		public Feign.Builder feignHystrixBuilder()
		{
			return HystrixFeign.builder();
		}
	}

	@Bean
	@ConditionalOnMissingBean
	public Retryer feignRetryer()
	{
		return Retryer.NEVER_RETRY;
	}

	@Bean
	@Scope("prototype")
	@ConditionalOnMissingBean
	public Feign.Builder feignBuilder(Retryer retryer)
	{
		return Feign.builder().retryer(retryer);
	}


}
