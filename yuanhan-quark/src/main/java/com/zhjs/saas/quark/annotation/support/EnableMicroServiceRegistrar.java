package com.yuanhan.yuanhan.quark.annotation.support;

import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

/**
 * 
 * @author:		yuanhan
 * @since:		2017-11-07
 * @modified:	2017-11-07
 * @version:	
 */
public class EnableMicroServiceRegistrar implements ImportBeanDefinitionRegistrar
{
	
	private static final Class<?> AnnotationProcessor = MicroServiceBeanPostProcessor.class;
	
	private static final String BeanName = AnnotationProcessor.getName();
	
	@Override
	public void registerBeanDefinitions(AnnotationMetadata metadata, BeanDefinitionRegistry registry)
	{
		BeanDefinitionBuilder builder = BeanDefinitionBuilder.rootBeanDefinition(AnnotationProcessor);
		registry.registerBeanDefinition(BeanName, builder.getBeanDefinition());
	}

}
