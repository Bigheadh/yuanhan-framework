package com.yuanhan.yuanhan.core.annotation.support;

import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.TypeConverter;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.InitDestroyAnnotationBeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.DependencyDescriptor;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ReflectionUtils;

import com.yuanhan.yuanhan.core.annotation.AutowiredMethod;
import com.yuanhan.yuanhan.core.annotation.CommonAutowired;
import com.yuanhan.yuanhan.core.annotation.CommonQualifier;
import com.yuanhan.yuanhan.core.annotation.InitMethod;
import com.yuanhan.yuanhan.core.logger.Logger;
import com.yuanhan.yuanhan.core.logger.LoggerFactory;
import com.yuanhan.yuanhan.core.util.AopUtil;

/**
 * 
 * @author:		yuanhan
 * @since:		2017-05-18
 * @modified:	2017-05-18
 * @version:	
 */

public class CommonAutowiredAnnotationBeanPostProcessor extends
InitDestroyAnnotationBeanPostProcessor implements
//InstantiationAwareBeanPostProcessorAdapter implements
		InstantiationAwareBeanPostProcessor, BeanFactoryAware, Serializable {

	private static final long serialVersionUID = -6829468833505630007L;

	private static Logger logger = LoggerFactory
			.getLogger(CommonAutowiredAnnotationBeanPostProcessor.class);

	private Class<? extends Annotation> autowiredAnnotationType = CommonAutowired.class;

	private Class<? extends Annotation> qualifierType = CommonQualifier.class;
	
	private Class<? extends Annotation> initMethodType = InitMethod.class;

	private final String autowiredParameterName = "method";
	
	private final String autowiredCallBack = "callBack";
	
	private Map<String, Method> autowiredCallBackMap = new HashMap<String, Method>();

	private AutowiredMethod autowiredParameterValue;

	private ConfigurableListableBeanFactory beanFactory;
	
	protected void autowiredCallBackMethod(Annotation annotation, Object bean, int i)
	{
		Method method = ReflectionUtils.findMethod(annotation.annotationType(), this.autowiredCallBack);
		String[] callBackMethods = (String[])ReflectionUtils.invokeMethod(method,annotation);
		if(callBackMethods!=null && callBackMethods.length>0)
		{
			if(callBackMethods.length>i)
			{
				if(callBackMethods[i]!=null && !callBackMethods[i].equals(""))
				{
					Method callBack = ReflectionUtils.findMethod(bean.getClass(),callBackMethods[i]);
					ReflectionUtils.invokeMethod(callBack, bean);
				}
			}
		}
	}
	
	protected void autowiredCallBackMethod(String fieldName, Object bean)
	{
		Method callBack = (Method)autowiredCallBackMap.get(fieldName);
		if(callBack!=null)
			ReflectionUtils.invokeMethod(callBack, bean);
	}
	
	protected void setAutowiredCallBackMap(Annotation annotation, Object bean)
	{
		Method method = ReflectionUtils.findMethod(annotation.annotationType(), this.autowiredCallBack);
		String[] callBackMethods = (String[])ReflectionUtils.invokeMethod(method,annotation);
		if(callBackMethods!=null && callBackMethods.length>0)
		{
			for(String callBackMethod : callBackMethods)
			{
				if(callBackMethod!=null && !callBackMethod.equals(""))
				{
					String[] methodMap = callBackMethod.split(":");
					//Method callBack = ReflectionUtils.findMethod(bean.getClass(),methodMap[1]);
					this.autowiredCallBackMap.put(methodMap[0], ReflectionUtils.findMethod(bean.getClass(),methodMap[1]));
				}
			}
		}		
	}

	protected AutowiredMethod getAutowiredParameterValue(Annotation annotation)
	{
		Method method = ReflectionUtils.findMethod(annotation.annotationType(), this.autowiredParameterName);
		this.autowiredParameterValue = (AutowiredMethod) ReflectionUtils.invokeMethod(method, annotation);
		return this.autowiredParameterValue;
	}
	
	protected void invokeInitMethod(final Object bean)
	{
		ReflectionUtils.doWithMethods(AopUtil.getFinalTargetClass(bean), new ReflectionUtils.MethodCallback() {
					public void doWith(Method method) throws IllegalArgumentException, IllegalAccessException
					{
                        ReflectionUtils.makeAccessible(method);
                        ReflectionUtils.invokeMethod(method, bean);
					}
				}
				,new ReflectionUtils.MethodFilter() {
		            public boolean matches(Method method) {
		                return AnnotationUtils.findAnnotation(method, initMethodType)!=null;
		            }
				});
	}

	/* (non-Javadoc)
	 * @see org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor#postProcessAfterInstantiation(java.lang.Object, java.lang.String)
	 */
	public boolean postProcessAfterInstantiation(Object bean, String beanName) throws BeansException
	{
		// TODO Auto-generated method stub
		Annotation commonAutowired = AnnotationUtils.findAnnotation(bean.getClass(), autowiredAnnotationType);
		if (commonAutowired!=null)
		{
			String[] packages = this.getClass().getPackage().getName().split("\\.");
			String package_prefix = packages[0] + "." + packages[1];
			setAutowiredCallBackMap(commonAutowired, bean);
			switch (getAutowiredParameterValue(commonAutowired))
			{
				// need to do something to support "byType" annotation 
				case BY_NAME:
				{
					Class<?> searchType = bean.getClass();
					while(searchType.getName().startsWith(package_prefix) && searchType!=null)
					{
						// get all fields of the bean by reflecting
						Field[] fields = searchType.getDeclaredFields();
						for (Field field : fields)
						{
							try {
								field.setAccessible(true);
								if(field.get(bean)!=null)
									continue;
							} catch (IllegalArgumentException e) {
								continue;
							} catch (IllegalAccessException e) {
								continue;
							}
							String autowiredBeanName = "";
							//if(field.isAnnotationPresent(qualifierType))
							Annotation annotation = AnnotationUtils.findAnnotation(field,qualifierType);
							if(annotation!=null)
								autowiredBeanName = (String)AnnotationUtils.getValue(annotation);
							if(autowiredBeanName.equals(""))
							{
								autowiredBeanName = field.getName();
							}
							Object value = beanFactory.getBean(autowiredBeanName);
							if (value != null)
							{
								field.setAccessible(true);
								try
								{
									//inject the value to the bean 
									//value = AopUtil.getFinalTarget(value);
									field.set(bean, value);
									// invoke callBack method
									//autowiredCallBackMethod(bean.getClass().getAnnotation(AutowiredAnnotationType),bean,i);
									autowiredCallBackMethod(field.getName(), bean);
								}
								catch (Exception e)
								{
									logger.error("Could not autowire field by name: {}", field);
									logger.error("", e);
								}
							}
						}
						searchType = searchType.getSuperclass();
					}
					break;
				}
				case BY_TYPE:
				{
					Class<?> searchType = bean.getClass();
					while(searchType.getName().startsWith(package_prefix) && searchType!=null)
					{
						// get all fields of the bean by reflecting
						Field[] fields = searchType.getDeclaredFields();
						for (Field field : fields)
						{
							try {
								field.setAccessible(true);
								if(field.get(bean)!=null)
									continue;
							} catch (IllegalArgumentException e) {
								continue;
							} catch (IllegalAccessException e) {
								continue;
							}
							Set<String> autowiredBeanNames = new LinkedHashSet<String>(1);
							TypeConverter typeConverter = beanFactory.getTypeConverter();
							DependencyDescriptor descriptor = new DependencyDescriptor(field, false);
							Object value;
	
							//if(field.isAnnotationPresent(qualifierType))
							Annotation annotation = AnnotationUtils.findAnnotation(field,qualifierType);
							if(annotation!=null)
							{
								String autowiredBeanName = (String)AnnotationUtils.getValue(annotation);
								if(autowiredBeanName.equals(""))
								{
									autowiredBeanName = field.getName();
								}
								autowiredBeanNames.add(autowiredBeanName);
								value = beanFactory.getBean(autowiredBeanName);
							}
							else
								value = beanFactory.resolveDependency(descriptor, beanName, autowiredBeanNames, typeConverter);
							
							if (value != null)
							{
								registerDependentBeans(beanName, autowiredBeanNames);
								if (autowiredBeanNames.size() == 1)
								{
									String autowiredBeanName = autowiredBeanNames.iterator().next();
									if (beanFactory.containsBean(autowiredBeanName))
									{
										if (beanFactory.isTypeMatch(autowiredBeanName, field.getType()))
										{
											field.setAccessible(true);
											try
											{
												//inject the value to the bean 
												//value = AopUtil.getFinalTarget(value);
												field.set(bean, value);
												// invoke callBack method
												//AutowiredCallBackMethod(bean.getClass().getAnnotation(AutowiredAnnotationType),bean,i);
												autowiredCallBackMethod(field.getName(), bean);
											}
											catch (Exception e)
											{
													 logger.error("Could not autowire field by type: {}", field);
													 logger.error("",e);
											}
										}
									}
								}
							}
						}
						searchType = searchType.getSuperclass();
					}
					break;
				}
			}
		}
		invokeInitMethod(bean);
		return true;
	}

	/**
	 * Register the specified bean as dependent on the autowired beans.
	 */
	private void registerDependentBeans(String beanName, Set<String> autowiredBeanNames)
	{
		if (beanName != null)
		{
			for (Iterator<String> it = autowiredBeanNames.iterator(); it.hasNext();)
			{
				String autowiredBeanName = it.next();
				beanFactory.registerDependentBean(autowiredBeanName, beanName);
				logger.debug("Autowiring by type from bean name '{}' to bean named '{}'", beanName, autowiredBeanName);
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor#postProcessBeforeInstantiation(java.lang.Class, java.lang.String)
	 */
	public Object postProcessBeforeInstantiation(Class<?> beanClass, String beanName) throws BeansException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor#postProcessPropertyValues(org.springframework.beans.PropertyValues, java.beans.PropertyDescriptor[], java.lang.Object, java.lang.String)
	 */
	public PropertyValues postProcessPropertyValues(PropertyValues pvs, PropertyDescriptor[] pds, Object bean, String beanName)
			throws BeansException {
		// TODO Auto-generated method stub
		return pvs;
	}

	/* (non-Javadoc)
	 * @see org.springframework.beans.factory.BeanFactoryAware#setBeanFactory(org.springframework.beans.factory.BeanFactory)
	 */
	public void setBeanFactory(BeanFactory beanFactory) throws BeansException
	{
		// TODO Auto-generated method stub  
		if (!(beanFactory instanceof ConfigurableListableBeanFactory))
		{
			throw new IllegalArgumentException("AutowiredAnnotationBeanPostProcessor requires a ConfigurableListableBeanFactory!!!");
		}
		this.beanFactory = (ConfigurableListableBeanFactory) beanFactory;
	}

}
