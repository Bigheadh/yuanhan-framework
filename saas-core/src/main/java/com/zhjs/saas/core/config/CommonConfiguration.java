package com.zhjs.saas.core.config;

import static com.zhjs.saas.core.config.PropertyConfig.Auto_CommonConfig_Enable;
import static com.zhjs.saas.core.config.PropertyConfig.Fastjson_Enable;
import static com.zhjs.saas.core.config.PropertyConfig.Fastjson_Filter_Exclude;
import static com.zhjs.saas.core.config.PropertyConfig.Global_DateTimeFormat;
import static com.zhjs.saas.core.config.PropertyConfig.PropertyBeanName;

import java.io.IOException;
import java.util.EventListener;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.web.ErrorAttributes;
import org.springframework.boot.autoconfigure.web.ErrorMvcAutoConfiguration;
import org.springframework.boot.autoconfigure.web.HttpMessageConverters;
import org.springframework.boot.autoconfigure.web.WebMvcRegistrationsAdapter;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import com.zhjs.saas.core.annotation.support.ApiMappingHandlerMapping;
import com.zhjs.saas.core.annotation.support.CommonAutowiredAnnotationBeanPostProcessor;
import com.zhjs.saas.core.config.support.PropertiesAwareProcessor;
import com.zhjs.saas.core.dao.sql.SQLResolver;
import com.zhjs.saas.core.exception.CommonErrorController;
import com.zhjs.saas.core.exception.CommonExceptionHandler;
import com.zhjs.saas.core.exception.CommonResponseErrorHandler;
import com.zhjs.saas.core.util.ApplicationContextUtil;
import com.zhjs.saas.core.util.CodecUtil;
import com.zhjs.saas.core.web.FastJsonHttpMsgConverter;
import com.zhjs.saas.core.web.listener.WebContextHolderListener;

/**
 * 
 * @author:		Jackie Wang
 * @since: 		2017-05-18
 * @modified: 	2017-05-18
 * @version:
 */
@PropertySource(value={"classpath:application.properties",
						"classpath:application-${spring.profiles.active}.properties",
						"classpath:saas.properties",
						"classpath:saas-${spring.profiles.active}.properties"},
				ignoreResourceNotFound=true, encoding=CodecUtil.Charset)
@SpringBootConfiguration
@AutoConfigureBefore(ErrorMvcAutoConfiguration.class)
@EnableConfigurationProperties
@ConditionalOnProperty(name=Auto_CommonConfig_Enable, havingValue="true", matchIfMissing=true)
public class CommonConfiguration implements ApplicationContextAware
{
	@Autowired
    private Environment env;
	private ApplicationContext context;
	
	@Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {  
        return new PropertySourcesPlaceholderConfigurer();  
    }	      
	
	@Bean(PropertyBeanName)
	public static CommonPropertyPlaceholderConfigurer propertiesHolder()
	{
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        Resource[] resources;
		try
		{
			resources = resolver.getResources("classpath:/**/*.properties");
		} catch (IOException e)
		{
			resources = null;
		}
		return new CommonPropertyPlaceholderConfigurer(resources);
	}

	@Bean
	public static CommonAutowiredAnnotationBeanPostProcessor commonAutowired()
	{
		return new CommonAutowiredAnnotationBeanPostProcessor();
	}
	
	@Bean
	public static PropertiesAwareProcessor propertiesAware()
	{
		return new PropertiesAwareProcessor();
	}

	@Bean
	public SQLResolver sqlResolver()
	{
		SQLResolver resovler = new SQLResolver();
		resovler.init(env);
		return resovler;
	}

	@Bean
	@ConditionalOnProperty(name=Fastjson_Enable, havingValue="true")
	public HttpMessageConverters fastJsonHttpMessageConverters()
	{
		return new HttpMessageConverters(
				new FastJsonHttpMsgConverter(env.getProperty(Fastjson_Filter_Exclude),env.getProperty(Global_DateTimeFormat)));
	}

	@Bean
	@ConditionalOnProperty(name=Fastjson_Enable, havingValue="true")
	public RestTemplate restTemplate()
	{
		RestTemplate template = new RestTemplate();
		template.setErrorHandler(new CommonResponseErrorHandler());
		template.setMessageConverters(context.getBean(HttpMessageConverters.class).getConverters());
		return template;
	}


	/**
	 * customize RequestMappingHandlerMapping
	 * @return
	 */
	@Bean
	public WebMvcRegistrationsAdapter webMvcRegistrationsHandlerMapping()
	{
		return new WebMvcRegistrationsAdapter()
		{
			@Override
			public RequestMappingHandlerMapping getRequestMappingHandlerMapping()
			{
				return new ApiMappingHandlerMapping();
			}
		};
	}
	
	
    @Bean
    @ConditionalOnWebApplication
    public ServletListenerRegistrationBean<EventListener> servletListenerRegistrationBean(){
        ServletListenerRegistrationBean<EventListener> servletListenerRegistrationBean = new ServletListenerRegistrationBean<>();
        servletListenerRegistrationBean.setListener(new WebContextHolderListener());
        return servletListenerRegistrationBean;
    }
    
    
    @Bean
    @Order
    public CorsFilter corsFilter() {
        final UrlBasedCorsConfigurationSource urlBasedCorsConfigurationSource = new UrlBasedCorsConfigurationSource();
        final CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowCredentials(true);
        corsConfiguration.addAllowedOrigin("*");
        corsConfiguration.addAllowedHeader("*");
        corsConfiguration.addAllowedMethod("*");
        urlBasedCorsConfigurationSource.registerCorsConfiguration("/**", corsConfiguration);
        return new CorsFilter(urlBasedCorsConfigurationSource);
    }
    

	@Bean
	public CommonErrorController errorController(ErrorAttributes errorAttributes)
	{
		return new CommonErrorController(errorAttributes);
	}
    

	@Bean
	public CommonExceptionHandler exceptionHandler()
	{
		return new CommonExceptionHandler();
	}
	
	
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException
	{
		this.context = applicationContext;		
		ApplicationContextUtil.setApplicationContext(applicationContext);
	}
	
/*
	@Bean
	public CommonHandlerExceptionResolver exceptionResolver()
	{
		return new CommonHandlerExceptionResolver();
	}
*/
}
