package com.yuanhan.yuanhan.core.util;

import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.util.Assert;
import org.springframework.web.context.ContextLoader;

/**
 * 
 * @author:		yuanhan
 * @since:		2017-05-10
 * @modified:	2017-05-10
 * @version:	
 */
public abstract class ApplicationContextUtil {

	/**
	 * Private constructor to prevent instantiation.
	 */
	private ApplicationContextUtil(){}

    private static ApplicationContext applicationContext;   
    
    public static void setApplicationContext(ApplicationContext applicaton)
    {
    	applicationContext = applicaton;
    }
  
    public static ApplicationContext getApplicationContext()
    {   

    	if(applicationContext == null)
    	{
    		applicationContext = ContextLoader.getCurrentWebApplicationContext();
			while(applicationContext.getParent()!=null)
			{
				applicationContext = applicationContext.getParent();
			}	    	
    	}    	
        return applicationContext;   
    }   
  

    public static Object getBean(String name) throws BeansException {   
        return getApplicationContext().getBean(name);   
    }   

    /**
     * Return a single bean of the given type or subtypes
     * 
     * @param lbf
     * @param type
     * @return
     * @throws BeansException
     */
	public static <T> T getBeanOfType(ListableBeanFactory lbf, Class<T> type) throws BeansException {
		Assert.notNull(lbf, "ListableBeanFactory must not be null");
		Map<String, T> beansOfType = lbf.getBeansOfType(type);
		if (beansOfType.size() == 1) {
			return beansOfType.values().iterator().next();
		}
		else {
			throw new NoSuchBeanDefinitionException(type, "expected single bean but found " + beansOfType.size());
		}
	}

    /**
     * Return a single bean of the given type or subtypes
     * 
     * @param lbf
     * @param type
     * @return
     * @throws BeansException
     */
	public static <T> T getBeanOfType(Class<T> type) throws BeansException {
		Assert.notNull(getApplicationContext(), "ListableBeanFactory must not be null");
		Map<String,T> beansOfType = getApplicationContext().getBeansOfType(type);
		if (beansOfType.size() == 1) {
			return beansOfType.values().iterator().next();
		}
		else {
			throw new NoSuchBeanDefinitionException(type, "expected single bean but found " + beansOfType.size());
		}
	}

	/**
	 * Return a single bean of the given type or subtypes
	 * 
	 * @param lbf
	 * @param type
	 * @param includeNonSingletons
	 * @param allowEagerInit
	 * @return
	 * @throws BeansException
	 */
	public static <T> T getBeanOfType(
			ListableBeanFactory lbf, Class<T> type, boolean includeNonSingletons, boolean allowEagerInit)
	    throws BeansException {

		Assert.notNull(lbf, "ListableBeanFactory must not be null");
		Map<String,T> beansOfType = lbf.getBeansOfType(type, includeNonSingletons, allowEagerInit);
		if (beansOfType.size() == 1) {
			return beansOfType.values().iterator().next();
		}
		else {
			throw new NoSuchBeanDefinitionException(type, "expected single bean but found " + beansOfType.size());
		}
	}

	/**
	 * Return a single bean of the given type or subtypes
	 * 
	 * @param lbf
	 * @param type
	 * @param includeNonSingletons
	 * @param allowEagerInit
	 * @return
	 * @throws BeansException
	 */
	public static <T> T getBeanOfType(Class<T> type, boolean includeNonSingletons, boolean allowEagerInit)
	    throws BeansException {

		Assert.notNull(getApplicationContext(), "ListableBeanFactory must not be null");
		Map<String,T> beansOfType = getApplicationContext().getBeansOfType(type, includeNonSingletons, allowEagerInit);
		if (beansOfType.size() == 1) {
			return beansOfType.values().iterator().next();
		}
		else {
			throw new NoSuchBeanDefinitionException(type, "expected single bean but found " + beansOfType.size());
		}
	}
  
}
