package com.zhjs.saas.security.exception;

import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.web.method.ControllerAdviceBean;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.method.annotation.ExceptionHandlerMethodResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;
import org.springframework.web.servlet.mvc.method.annotation.ServletInvocableHandlerMethod;

/**
 * 
 * @author:		Jackie Wang 
 * @since:		2018-04-12
 * @modified:	2018-04-12
 * @version:	
 */
public class OAuthExceptionHandlerResolver extends ExceptionHandlerExceptionResolver
{

	private final Map<Class<?>, ExceptionHandlerMethodResolver> exceptionHandlerCache =
			new ConcurrentHashMap<Class<?>, ExceptionHandlerMethodResolver>(64);

	private final Map<ControllerAdviceBean, ExceptionHandlerMethodResolver> exceptionHandlerAdviceCache =
			new LinkedHashMap<ControllerAdviceBean, ExceptionHandlerMethodResolver>();
	

	protected ModelAndView doResolveHandlerMethodException(HttpServletRequest request,
			HttpServletResponse response, HandlerMethod handlerMethod, Exception exception) 
	{
		if(exception instanceof OAuth2Exception)
		{
			
		}
		return super.doResolveHandlerMethodException(request, response, handlerMethod, exception);
	}

	protected ServletInvocableHandlerMethod getExceptionHandlerMethod(HandlerMethod handlerMethod, Exception exception) 
	{		
		Class<?> handlerType = (handlerMethod != null ? handlerMethod.getBeanType() : null);

		if (handlerMethod != null) {
			ExceptionHandlerMethodResolver resolver = this.exceptionHandlerCache.get(handlerType);
			if (resolver == null) {
				resolver = new ExceptionHandlerMethodResolver(handlerType);
				this.exceptionHandlerCache.put(handlerType, resolver);
			}
			Method method = resolver.resolveMethod(exception);
			if (method != null) {
				return new OAuthInvocableHandlerMethod(handlerMethod.getBean(), method);
			}
		}

		for (Entry<ControllerAdviceBean, ExceptionHandlerMethodResolver> entry : this.exceptionHandlerAdviceCache.entrySet()) {
			if (entry.getKey().isApplicableToBeanType(handlerType)) {
				ExceptionHandlerMethodResolver resolver = entry.getValue();
				Method method = resolver.resolveMethod(exception);
				if (method != null) {
					return new OAuthInvocableHandlerMethod(entry.getKey().resolveBean(), method);
				}
			}
		}

		return null;
	}

}
