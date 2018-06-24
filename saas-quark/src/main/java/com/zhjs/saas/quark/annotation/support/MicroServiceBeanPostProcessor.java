package com.zhjs.saas.quark.annotation.support;

import java.io.Serializable;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessorAdapter;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.remoting.caucho.HessianServiceExporter;
import org.springframework.remoting.httpinvoker.HttpInvokerServiceExporter;
import org.springframework.remoting.rmi.RmiServiceExporter;

import com.zhjs.saas.core.logger.Logger;
import com.zhjs.saas.core.logger.LoggerFactory;
import com.zhjs.saas.quark.annotation.MicroService;
import com.zhjs.saas.quark.protocol.ServiceProtocol;
import com.zhjs.saas.quark.remote.DubboServiceExporter;
import com.zhjs.saas.quark.remote.QuarkServiceExporter;

/**
 * 
 * @author:		Jackie Wang 
 * @since:		2017-11-07
 * @modified:	2017-11-07
 * @version:	
 */
public class MicroServiceBeanPostProcessor extends InstantiationAwareBeanPostProcessorAdapter
implements BeanFactoryAware, ApplicationContextAware, PriorityOrdered, Serializable
{

	private static final long serialVersionUID = -2752985822026107210L;

	private Logger logger = LoggerFactory.getLogger(getClass());
	
	private int order = Ordered.LOWEST_PRECEDENCE;

	private DefaultListableBeanFactory beanFactory;
	private ApplicationContext context;
	

	@Override
	public boolean postProcessAfterInstantiation(Object bean, String beanName) throws BeansException {
		MicroService micro = AnnotationUtils.findAnnotation(bean.getClass(), MicroService.class);
		if(micro!=null)
		{
			ServiceProtocol[] protocols = micro.value();
			for(ServiceProtocol protocol : protocols)
			{
				registerMicroServiceBean(protocol, micro, bean, beanName);
			}
		}
		return true;
	}

	private void registerMicroServiceBean(ServiceProtocol protocol, MicroService micro, Object bean, String beanName)
	{
		Class<?> interfaceClass = micro.interfaceClass();
		if(interfaceClass.equals(void.class))
		{
			Class<?>[] interfaces = bean.getClass().getInterfaces();
			if(interfaces.length>0)
				interfaceClass = interfaces[0];
			else
				throw new IllegalStateException("Failed to expose micro-service [" + bean.getClass().getName()
						+ "] cause: The @MicroService undefined interfaceClass or the service class unimplemented any interfaces.");
		}
		String urlName = micro.path() + "/" + interfaceClass.getSimpleName() + "-" + micro.version();
		BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition();
		
		switch(protocol)
		{
			case Dubbo:
			default:
				urlName += ".dubbo";
				if(micro.dubbo().length==0)
					throw new IllegalArgumentException("Failded to expose micro-service by dubbo, because at lease one dubbo @Service"
							+ " is required to set by @MircoService.dubbo() method.");
				builder.getRawBeanDefinition().setBeanClass(DubboServiceExporter.class);
				builder.addPropertyValue(MicroService.Annotation, micro.dubbo());
			break;
			
			case Quark:
				urlName += ".quark";
				builder.getRawBeanDefinition().setBeanClass(QuarkServiceExporter.class);
			break;
				
			case Hessian:
				urlName += ".hessian";
				builder.getRawBeanDefinition().setBeanClass(HessianServiceExporter.class);
			break;
				
			case RMI:
				urlName += ".rmi";
				builder.getRawBeanDefinition().setBeanClass(RmiServiceExporter.class);
				builder.addPropertyValue("serviceName", urlName);
				builder.addPropertyValue(MicroService.Port, 
						micro.port()==0?context.getEnvironment().getProperty(MicroService.PortConfig):micro.port());
			break;
				
			case HttpInvoker:
				urlName += ".httpinvoker";
				builder.getRawBeanDefinition().setBeanClass(HttpInvokerServiceExporter.class);
			break;
		}
		builder.addPropertyValue(MicroService.ServiceInterface, interfaceClass);
		builder.addPropertyReference(MicroService.Service, beanName);
		builder.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
		beanFactory.registerBeanDefinition(urlName, builder.getBeanDefinition());
		logger.info("Expose a micro-service bean [{}] with url [{}]", beanName, urlName);
	}


	public void setBeanFactory(BeanFactory beanFactory) throws BeansException
	{
		if (!(beanFactory instanceof DefaultListableBeanFactory))
		{
			throw new IllegalArgumentException("MicroServiceBeanPostProcessor requires a DefaultListableBeanFactory!!!");
		}
		this.beanFactory = (DefaultListableBeanFactory) beanFactory;
	}


	public void setOrder(int order) {
		this.order = order;
	}

	@Override
	public int getOrder() {
		return this.order;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException
	{
		this.context = applicationContext;
	}

}
