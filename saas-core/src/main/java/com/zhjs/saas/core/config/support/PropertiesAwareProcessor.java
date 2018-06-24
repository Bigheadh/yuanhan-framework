package com.zhjs.saas.core.config.support;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.zhjs.saas.core.config.PropertiesAware;
import com.zhjs.saas.core.config.PropertiesHolder;
import com.zhjs.saas.core.config.PropertyConfig;

/**
 * 
 * @author:		Jackie Wang 
 * @since:		2017-05-19
 * @modified:	2017-05-19
 * @version:	
 */
public class PropertiesAwareProcessor implements BeanPostProcessor, ApplicationContextAware {
	
	private ApplicationContext context;

	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		this.context = applicationContext;
	}

	public Object postProcessAfterInitialization(Object bean, String beanName)
			throws BeansException {
		return bean;
	}

	public Object postProcessBeforeInitialization(Object bean, String beanName)
			throws BeansException {
		if(bean instanceof PropertiesAware)
		{
			/*String[] beanNames = this.context.getBeanNamesForType(PropertiesHolder.class);
			if(beanName!=null)
				((PropertiesAware) bean).setProperties(this.context.getBean(beanNames[0],PropertiesHolder.class).getProperties());*/
			((PropertiesAware) bean).setProperties(this.context.getBean(PropertyConfig.PropertyBeanName,PropertiesHolder.class).getProperties());
		}
		return bean;
	}

}
