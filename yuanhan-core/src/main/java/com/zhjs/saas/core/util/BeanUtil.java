package com.yuanhan.yuanhan.core.util;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.DynaProperty;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.beanutils.PropertyUtilsBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanInstantiationException;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeansException;
import org.springframework.beans.FatalBeanException;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.bind.support.WebRequestDataBinder;
import org.springframework.web.context.request.ServletWebRequest;

import com.alibaba.fastjson.JSONObject;
import com.yuanhan.yuanhan.core.pojo.BaseObject;

/**
 * 
 * @author:		yuanhan
 * @since:		2017-05-10
 * @modified:	2017-05-10
 * @version:	
 */

@SuppressWarnings({"unchecked","rawtypes"})
public abstract class BeanUtil extends BeanUtils {
	
	private static Logger logger = LoggerFactory.getLogger(BeanUtil.class);

	/**
	 * Private constructor to prevent instantiation.
	 */
	private BeanUtil(){}
	    
    /** Used to access properties*/
    private static PropertyUtilsBean propertyUtilsBean = new PropertyUtilsBean();
    
	private static String underscoreName(String name) {
		StringBuffer result = new StringBuffer();
		if (name != null && name.length() > 0) {
			result.append(name.substring(0, 1).toLowerCase());
			for (int i = 1; i < name.length(); i++) {
				String s = name.substring(i, i + 1);
				if (s.equals(s.toUpperCase())) {
					result.append("_");
					result.append(s.toLowerCase());
				}
				else {
					result.append(s);
				}
			}
		}
		return result.toString();
	}
	
	private static void mapping(Class<?> clazz, Map<String,String> mappedFields)
	{//SequenceGenerator
		PropertyDescriptor[] pds = getPropertyDescriptors(clazz);
		for (PropertyDescriptor pd : pds) {
			Class<?> type = pd.getPropertyType();
			if (pd.getWriteMethod()!=null && pd.getReadMethod()!=null 
					&& !Class.class.isAssignableFrom(type)
					&& !BaseObject.class.isAssignableFrom(type)
					&& !Collection.class.isAssignableFrom(type)) {
				//mappedFields.put(pd.getName().toLowerCase(), pd);
				//mappedFields.put(pd.getName().toLowerCase(), pd.getName());
				String underscoredName = underscoreName(pd.getName());
				mappedFields.put(underscoredName, pd.getName());
				mappedFields.put(pd.getName(), pd.getName());
			}
		}
	}
	
	public static <T> T bindRequestToBean(Class<T> clazz)
	{
		T t = BeanUtil.instantiateClass(clazz);
		return bindRequestToBean(t);
	}
	
	public static <T> T bindRequestToBean(T t)
	{
		WebRequestDataBinder binder = new WebRequestDataBinder(t, t.getClass().getName());
		ServletWebRequest webRequest = new ServletWebRequest(WebUtil.getRequest());
		binder.bind(webRequest);
		t = (T)binder.getTarget();
		return t;
	}
    
    public static <T> T dataToBean(Map data, Class<T> clazz)
    {
    	Map<String,String> keyPropertyMap = new HashMap<String,String>();
    	mapping(clazz, keyPropertyMap);
    	T t = instantiateClass(clazz);
		BeanWrapper bw = PropertyAccessorFactory.forBeanPropertyAccess(t);
		Iterator it = data.keySet().iterator();
		while(it.hasNext())
		{
			String key = (String)it.next();
			if(keyPropertyMap.containsKey(key))
			{
				bw.setPropertyValue(keyPropertyMap.get(key), data.get(key));
			}
		}
    	return t;
    }

	/**
	 * Convenience method to instantiate a class using its no-arg constructor.
	 * As this method doesn't try to load classes by name, it should avoid
	 * class-loading issues.
	 * <p>Note that this method tries to set the constructor accessible
	 * if given a non-accessible (that is, non-public) constructor.
	 * @param clazz class to instantiate
	 * @return the new instance
	 * @throws BeanInstantiationException if the bean cannot be instantiated
	 */
	public static <T> T instantiateClass(Class<T> clazz) throws BeanInstantiationException {
		Assert.notNull(clazz, "Class must not be null");
		if (clazz.isInterface()) {
			throw new BeanInstantiationException(clazz, "Specified class is an interface");
		}
		try {
			return instantiateClass(clazz.getDeclaredConstructor());
		}
		catch (NoSuchMethodException ex) {
			throw new BeanInstantiationException(clazz, "No default constructor found", ex);
		}
	}

	/**
	 * Convenience method to instantiate a class using the given constructor.
	 * As this method doesn't try to load classes by name, it should avoid
	 * class-loading issues.
	 * <p>Note that this method tries to set the constructor accessible
	 * if given a non-accessible (that is, non-public) constructor.
	 * @param ctor the constructor to instantiate
	 * @param args the constructor arguments to apply
	 * @return the new instance
	 * @throws BeanInstantiationException if the bean cannot be instantiated
	 */
	public static <T> T instantiateClass(Constructor<T> ctor, Object... args) throws BeanInstantiationException {
		Assert.notNull(ctor, "Constructor must not be null");
		try {
			ReflectionUtils.makeAccessible(ctor);
			return ctor.newInstance(args);
		}
		catch (InstantiationException ex) {
			throw new BeanInstantiationException(ctor.getDeclaringClass(),
					"Is it an abstract class?", ex);
		}
		catch (IllegalAccessException ex) {
			throw new BeanInstantiationException(ctor.getDeclaringClass(),
					"Is the constructor accessible?", ex);
		}
		catch (IllegalArgumentException ex) {
			throw new BeanInstantiationException(ctor.getDeclaringClass(),
					"Illegal arguments for constructor", ex);
		}
		catch (InvocationTargetException ex) {
			throw new BeanInstantiationException(ctor.getDeclaringClass(),
					"Constructor threw exception", ex.getTargetException());
		}
	}
	
	/**
	 * merge proper key/value from a map into a bean
	 * if the property in the bean is not support, or can't find
	 * the corresponding key from the map, it will be ignored.
	 * 
	 * @param map
	 * @param obj
	 * @return
	 */
	public static Object mapToBean(Map map, Object obj)
	{
		/*T object = (T) BeanUtils.instantiateClass(obj.getClass());
		BeanWrapper beanWrapper = new BeanWrapperImpl(obj); 
		beanWrapper.setPropertyValues(m); */
		PropertyDescriptor[] pds = getPropertyDescriptors(obj.getClass());
		for(PropertyDescriptor targetProperty : pds)
		{
			String key = targetProperty.getName();
			if(map.containsKey(key) && targetProperty.getWriteMethod()!=null )
			{
				try
				{
					Method writeMethod = targetProperty.getWriteMethod();
					if (!Modifier.isPublic(writeMethod.getDeclaringClass().getModifiers()))
					{
						writeMethod.setAccessible(true);
					}
					writeMethod.invoke(obj, map.get(key));
				}
				catch (Throwable ex) {
					throw new FatalBeanException("Could not copy properties from source to target", ex);
				}
			}
		}
		return obj;
	}

	/**
	 * Retrieve the JavaBeans <code>PropertyDescriptor</code>s of a given class.
	 * @param clazz the Class to retrieve the PropertyDescriptors for
	 * @return an array of <code>PropertyDescriptors</code> for the given class
	 * @throws BeansException if PropertyDescriptor look fails
	 */
	public static PropertyDescriptor[] getPropertyDescriptors(Class<?> clazz) throws BeansException {
		return org.springframework.beans.BeanUtils.getPropertyDescriptors(clazz);
	}	
	
	/**
	 * merge proper key/value from a map into a bean
	 * if the property in the bean is not support, or can't find
	 * the corresponding key from the map, it will be ignored.
	 * 
	 * @param map
	 * @param obj
	 * @return
	 */
	public static Object mapToBeanNotCaseSensitive(Map map, Object obj)
	{
		/*T object = (T) BeanUtils.instantiateClass(obj.getClass());
		BeanWrapper beanWrapper = new BeanWrapperImpl(obj); 
		beanWrapper.setPropertyValues(m); */
		PropertyDescriptor[] pds = getPropertyDescriptors(obj.getClass());
		for(PropertyDescriptor targetProperty : pds)
		{
			String key = targetProperty.getName().toLowerCase();
			if(map.containsKey(key) && map.get(key)!=null && targetProperty.getWriteMethod()!=null )
			{
				try
				{
					Method writeMethod = targetProperty.getWriteMethod();
					if (!Modifier.isPublic(writeMethod.getDeclaringClass().getModifiers()))
					{
						writeMethod.setAccessible(true);
					}
					Object value;				
					Object valuetmp = map.get(key);				
					Class type = writeMethod.getParameterTypes()[0];
					if(type.isAssignableFrom(Long.class) && (valuetmp instanceof Integer || valuetmp instanceof String))
					{
						if(valuetmp instanceof Integer)
							value = Long.valueOf((Integer)valuetmp);
						else
							value = Long.valueOf((String)valuetmp);
					}
					else if(type.isAssignableFrom(Integer.class) && (valuetmp instanceof Long || valuetmp instanceof String))
					{
						if(valuetmp instanceof Long)
							value = Integer.valueOf(((Long)valuetmp).intValue());
						else
							value = Integer.valueOf((String)valuetmp);
					}
					/*if(type.isPrimitive() && ClassUtil.isPrimitiveWrapper(valuetmp.getClass()))
						value = ClassUtils.wrapperToPrimitive(valuetmp.getClass());
					else if(ClassUtil.isPrimitiveWrapper(type))
					{
						if(type.isAssignableFrom(Long.class))
							value = Long.valueOf(valuetmp.toString());
						else if(type.isAssignableFrom(Integer.class))
							value = Integer.valueOf(valuetmp.toString());
						else if(type.isAssignableFrom(Double.class))
							value = Double.valueOf(valuetmp.toString());
						else if(type.isAssignableFrom(Float.class))
							value = Float.valueOf(valuetmp.toString());
						else if(type.isAssignableFrom(Boolean.class))
							value = Boolean.valueOf(valuetmp.toString());
						else if(type.isAssignableFrom(Number.class))
							NumberUtils.createNumber(valuetmp.toString());
						else (type.isAssignableFrom(Number.class))
							NumberUtils.createNumber(valuetmp.toString());
					}
					if(type.isAssignableFrom(cls))*/
					else
						value = valuetmp;
					
					if(value instanceof JSONObject && ((JSONObject)value).isEmpty())
						continue;
						
					//Constructor cstr = ClassUtil.getConstructorIfAvailable(type, new Class[]{value.getClass()});
					writeMethod.invoke(obj, value);
				}
				catch (Throwable ex) {
					throw new FatalBeanException("Could not copy properties from source to target", ex);
				}
			}
		}
		return obj;
	}
	
	public static Object instantiateClass(String className) throws BeanInstantiationException
	{
		Assert.notNull(className, "Class name must not be null");
		try
		{
			Class clazz = Class.forName(className);
			return instantiateClass(clazz);
		}
		catch(ClassNotFoundException ex)
		{
			throw new BeanInstantiationException(null, className + " class not found.");
		}
		catch(BeanInstantiationException ex)
		{
			throw ex;
		}
	}
	
	public static <T> void clearProperties(T t, String...properties)
	{
		
	}
	
	public static <T> void clearProperties(T t, Class...properties)
	{
		
	}
    /**
     * <p>Copy property values from the origin bean to the destination bean
     * for all cases where the property names are the same.  For each
     * property, a conversion is attempted as necessary.  All combinations of
     * standard JavaBeans and DynaBeans as origin and destination are
     * supported.  Properties that exist in the origin bean, but do not exist
     * in the destination bean (or are read-only in the destination bean) are
     * silently ignored.</p>
     *
     * <p>If the origin "bean" is actually a <code>Map</code>, it is assumed
     * to contain String-valued <strong>simple</strong> property names as the keys, pointing at
     * the corresponding property values that will be converted (if necessary)
     * and set in the destination bean. <strong>Note</strong> that this method
     * is intended to perform a "shallow copy" of the properties and so complex
     * properties (for example, nested ones) will not be copied.</p>
     *
     * <p>This method differs from <code>populate()</code>, which
     * was primarily designed for populating JavaBeans from the map of request
     * parameters retrieved on an HTTP request, is that no scalar->indexed
     * or indexed->scalar manipulations are performed.  If the origin property
     * is indexed, the destination property must be also.</p>
     *
     * <p>If you know that no type conversions are required, the
     * <code>copyProperties()</code> method in {@link PropertyUtils} will
     * execute faster than this method.</p>
     *
     * <p><strong>FIXME</strong> - Indexed and mapped properties that do not
     * have getter and setter methods for the underlying array or Map are not
     * copied by this method.</p>
     *
     * @param dest Destination bean whose properties are modified
     * @param orig Origin bean whose properties are retrieved
     *
     * @exception IllegalAccessException if the caller does not have
     *  access to the property accessor method
     * @exception IllegalArgumentException if the <code>dest</code> or
     *  <code>orig</code> argument is null or if the <code>dest</code> 
     *  property type is different from the source type and the relevant
     *  converter has not been registered.
     * @exception InvocationTargetException if the property accessor method
     *  throws an exception
     */
    public static void copyProperties(Object dest, Object orig)
        throws IllegalAccessException, InvocationTargetException {

        // Validate existence of the specified beans
        if (dest == null) {
            throw new IllegalArgumentException
                    ("No destination bean specified");
        }
        if (orig == null) {
            throw new IllegalArgumentException("No origin bean specified");
        }
        if (logger.isDebugEnabled()) {
            logger.debug("BeanUtils.copyProperties(" + dest + ", " +
                      orig + ")");
        }

        // Copy the properties, converting as necessary
        if (orig instanceof DynaBean) {
            DynaProperty[] origDescriptors =
                ((DynaBean) orig).getDynaClass().getDynaProperties();
            for (int i = 0; i < origDescriptors.length; i++) {
                String name = origDescriptors[i].getName();
                // Need to check isReadable() for WrapDynaBean
                // (see Jira issue# BEANUTILS-61)
                if (propertyUtilsBean.isReadable(orig, name) &&
                		propertyUtilsBean.isWriteable(dest, name)) {
                    Object value = ((DynaBean) orig).get(name);
                    if(value==null)
                    	continue;
                    copyProperty(dest, name, value);
                }
            }
        } else if (orig instanceof Map) {
            Iterator entries = ((Map) orig).entrySet().iterator();
            while (entries.hasNext()) {
                Map.Entry entry = (Map.Entry) entries.next();
                String name = (String)entry.getKey();
                if (propertyUtilsBean.isWriteable(dest, name)) {
                    copyProperty(dest, name, entry.getValue());
                }
            }
        } else /* if (orig is a standard JavaBean) */ {
            PropertyDescriptor[] origDescriptors =
            	propertyUtilsBean.getPropertyDescriptors(orig);
            for (int i = 0; i < origDescriptors.length; i++) {
                String name = origDescriptors[i].getName();
                if ("class".equals(name)) {
                    continue; // No point in trying to set an object's class
                }
                if (propertyUtilsBean.isReadable(orig, name) &&
                		propertyUtilsBean.isWriteable(dest, name)) {
                    try {
                        Object value =
                        	propertyUtilsBean.getSimpleProperty(orig, name);
                        if(value==null)
                        	continue;
                        copyProperty(dest, name, value);
                    } catch (NoSuchMethodException e) {
                        // Should not happen
                    	logger.error("Oh, it should not happen!!!", e);
                    }
                }
            }
        }

    }

    public static <T extends Object> T copyProperties(T dest, Object orig, boolean copyNullValue)
        throws IllegalAccessException, InvocationTargetException {
    	if(copyNullValue)
    		BeanUtilsBean.getInstance().copyProperties(dest, orig);
    	else
    		copyProperties(dest, orig);
    	return dest;
    }

}

