package com.yuanhan.yuanhan.core.util;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.AdvisedSupport;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.DirectFieldAccessor;

/**
 * 
 * @author:		yuanhan
 * @since:		2017-05-18
 * @modified:	2017-05-18
 * @version:	
 */
public abstract class AopUtil extends AopUtils {
	
	private static Logger logger = LoggerFactory.getLogger(AopUtil.class);

	/**
	 * Private constructor to prevent instantiation.
	 */
	private AopUtil(){}

	private static final String ADVISED_FIELD_NAME = "advised";

	private static final String CLASS_JDK_DYNAMIC_AOP_PROXY = "org.springframework.aop.framework.JdkDynamicAopProxy";
	
	public static Object getFinalTarget(Object candidate)
	{
		if(isJdkDynamicProxy(candidate))
		{
			try {
				return getFinalTarget(getAdvised(candidate).getTargetSource().getTarget());
			} catch (Exception e) {
				logger.error("get target object from " + CLASS_JDK_DYNAMIC_AOP_PROXY + " error", e);
			}
		}
		return candidate;
	}
	
	public static AdvisedSupport getAdvised(Object candidate)
	{
		if(isJdkDynamicProxy(candidate))
		{
			try {
				InvocationHandler invocationHandler = Proxy.getInvocationHandler(candidate);
				if (!invocationHandler.getClass().getName().equals(CLASS_JDK_DYNAMIC_AOP_PROXY)) {
					logger.warn("the invocationHandler of JdkDynamicProxy isn`t the instance of " + CLASS_JDK_DYNAMIC_AOP_PROXY);
					return null;
				}
				AdvisedSupport advised = (AdvisedSupport) new DirectFieldAccessor(invocationHandler).getPropertyValue(ADVISED_FIELD_NAME);
				return advised;
			} catch (Exception e) {
				logger.error("get target object from " + CLASS_JDK_DYNAMIC_AOP_PROXY + " error", e);
			}
		}
		return null;
	}

	public static Class<?> getFinalTargetClass(Object candidate) {
		
		if (!isJdkDynamicProxy(candidate)) {
			return getTargetClass(candidate);
		}

		return getTargetClassFromJdkDynamicAopProxy(candidate);
	}
	
	public static InvocationHandler getFinalInvocationHandler(final Object candidate)
	{
		if(isJdkDynamicProxy(candidate))
		{
			try{
				final InvocationHandler invocationHandler = Proxy.getInvocationHandler(candidate);
				/*if (!invocationHandler.getClass().getName().equals(CLASS_JDK_DYNAMIC_AOP_PROXY)) {
					logger.warn("the invocationHandler of JdkDynamicProxy isn`t the instance of " + CLASS_JDK_DYNAMIC_AOP_PROXY);
					return invocationHandler;
				}*/
				AdvisedSupport advised = (AdvisedSupport) new DirectFieldAccessor(invocationHandler).getPropertyValue(ADVISED_FIELD_NAME);
				if(!isJdkDynamicProxy(advised.getTargetSource().getTarget()))
					return invocationHandler;
				return getFinalInvocationHandler(advised.getTargetSource().getTarget());
			} catch (Exception e) {
				logger.error("get InvocationHandler from " + CLASS_JDK_DYNAMIC_AOP_PROXY + " error", e);
			}		
		}
		throw new IllegalStateException("Should never get here");
	}

	private static Class<?> getTargetClassFromJdkDynamicAopProxy(Object candidate) {
		try {
			AdvisedSupport advised = getAdvised(candidate);
			Class<?> targetClass = advised.getTargetClass();
			if (Proxy.isProxyClass(targetClass)) {
				Object target = advised.getTargetSource().getTarget();
				return getTargetClassFromJdkDynamicAopProxy(target);
			}
			return targetClass;
		} catch (Exception e) {
			logger.error("get target class from " + CLASS_JDK_DYNAMIC_AOP_PROXY + " error", e);
			return candidate.getClass();
		}
	}

}
