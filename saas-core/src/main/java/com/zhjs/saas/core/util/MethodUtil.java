/**
 * 
 */
package com.zhjs.saas.core.util;

import java.lang.reflect.InvocationTargetException;

import org.apache.commons.beanutils.MethodUtils;

/**
 * @author:		Jackie Wang 
 * @since:		2017-05-10
 * @modified:	2017-05-10
 * @version:	
 */
public abstract class MethodUtil extends MethodUtils {

	/**
	 * Private constructor to prevent instantiation.
	 */
	private MethodUtil(){}
	
	public static Object invoke(Object object, String methodName, Object... args)
	{
		Object obj = new Object();
		try {
			obj = MethodUtils.invokeMethod(object, methodName, args);
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return obj;
	}

}
