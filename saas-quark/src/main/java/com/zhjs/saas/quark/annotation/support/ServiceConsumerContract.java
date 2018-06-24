package com.zhjs.saas.quark.annotation.support;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.netflix.feign.AnnotatedParameterProcessor;
import org.springframework.cloud.netflix.feign.support.SpringMvcContract;
import org.springframework.context.ApplicationContext;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;

import com.zhjs.saas.core.annotation.ApiMapping;
import com.zhjs.saas.core.logger.Logger;
import com.zhjs.saas.core.logger.LoggerFactory;
import com.zhjs.saas.core.util.AnnotationUtil;
import com.zhjs.saas.core.util.StringUtil;
import com.zhjs.saas.quark.annotation.ServiceConsumer;

import feign.MethodMetadata;

/**
 * 
 * @author:		Jackie Wang 
 * @since:		2017-12-16
 * @modified:	2017-12-16
 * @version:	
 */
public class ServiceConsumerContract extends SpringMvcContract
{
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private ApplicationContext context;

	public ServiceConsumerContract() {
		this(Collections.<AnnotatedParameterProcessor> emptyList());
	}

	public ServiceConsumerContract(List<AnnotatedParameterProcessor> annotatedParameterProcessors) {
		this(annotatedParameterProcessors, new DefaultConversionService());
	}

	public ServiceConsumerContract(List<AnnotatedParameterProcessor> annotatedParameterProcessors, ConversionService conversionService) {
		super(annotatedParameterProcessors, conversionService);
	}

	@Override
	protected void processAnnotationOnClass(MethodMetadata data, Class<?> clz)
	{
		super.processAnnotationOnClass(data, clz);
		ServiceConsumer service = AnnotationUtil.findAnnotation(clz, ServiceConsumer.class);
		if(service!=null)
		{
			List<ServiceInstance> instances = context.getBean(DiscoveryClient.class).getInstances(service.name());
			if(instances!=null && instances.size()>0)
			{
				ServiceInstance instance = instances.get(0);
				String rootPath = instance.getMetadata().get("root-path");
				if (StringUtil.isNotEmpty(rootPath) && !rootPath.startsWith("/"))
					rootPath = "/" + rootPath;
				/*if(StringUtil.isNotEmpty(rootPath) && !rootPath.equals("/"))
					data.template().insert(0, rootPath);*/
			}
			logger.debug("Mapping ServiceConsumer[feign] url [{}] to serviceId [{}]", data.template().url(), service.name());
		}
	}

	@Override
	protected void processAnnotationOnMethod(MethodMetadata data, Annotation annotation, Method method)
	{
		super.processAnnotationOnMethod(data, annotation, method);
		ApiMapping api = AnnotationUtil.findAnnotation(method, ApiMapping.class);
		if(api!=null)
		{
			data.template().query("method", StringUtil.isBlank(api.value()) ? method.getName() : api.value());
			if(StringUtil.isNotBlank(api.v()))
				data.template().query("v", api.v());
		}
	}

	@Override
	protected boolean processAnnotationsOnParameter(MethodMetadata data, Annotation[] annotations, int paramIndex)
	{
		return super.processAnnotationsOnParameter(data, annotations, paramIndex);
	}

}
