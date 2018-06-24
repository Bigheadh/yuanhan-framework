package com.zhjs.saas.quark.annotation.support;

import java.util.Map;

import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

import com.zhjs.saas.quark.annotation.MicroService;
import com.zhjs.saas.quark.protocol.ServiceProtocol;

/**
 * 
 * @author:		Jackie Wang 
 * @since:		2017-10-27
 * @modified:	2017-10-27
 * @version:	
 */
public class MicroServiceRegistrar implements ImportBeanDefinitionRegistrar
{

	@Override
	public void registerBeanDefinitions(AnnotationMetadata metadata, BeanDefinitionRegistry registry)
	{
		Map<String,Object> attrs = metadata.getAnnotationAttributes(MicroService.class.getName(), true);
		if (attrs!=null)
		{
			if(attrs.containsKey("value"))
			{
				ServiceProtocol[] protocols = (ServiceProtocol[]) attrs.get("value");
				for(ServiceProtocol protocol : protocols)
				{
					registerMicroServiceBean(protocol, metadata, registry);
				}
			}
		}		
	}

	private void registerMicroServiceBean(ServiceProtocol protocol, AnnotationMetadata metadata, BeanDefinitionRegistry registry)
	{
		switch(protocol)
		{
			case Dubbo:
				break;
				
			case Hessian:
				break;
				
			case Rest:
			case Feign:
				break;
				
			case RMI:
				break;
				
			case HttpInvoker:
				break;
				
			case Quark:
				break;
		}
	}
}
