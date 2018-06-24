package com.zhjs.saas.core.web;

import java.net.URLDecoder;

import javax.servlet.http.HttpServletRequest;

import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import com.alibaba.fastjson.JSONObject;
import com.zhjs.saas.core.annotation.ApiMapping;
import com.zhjs.saas.core.annotation.JsonQuery;
import com.zhjs.saas.core.logger.Logger;
import com.zhjs.saas.core.logger.LoggerFactory;
import com.zhjs.saas.core.util.CodecUtil;
import com.zhjs.saas.core.util.StringUtil;

/**
 * 
 * @author:		Jackie Wang 
 * @since:		2017-12-25
 * @modified:	2017-12-25
 * @version:	
 */
public class CommonControllerArgumentResolver implements HandlerMethodArgumentResolver
{
	private Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	public boolean supportsParameter(MethodParameter parameter)
	{
		if(parameter.hasMethodAnnotation(ApiMapping.class)
			&& parameter.hasParameterAnnotation(JsonQuery.class))
		{
			return true;
		}
		return false;
	}

	@Override
	public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
			NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception
	{
		HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
		JsonQuery json = parameter.getParameterAnnotation(JsonQuery.class);
		String queryKey = StringUtil.isBlank(json.value()) ? parameter.getParameterName() : json.value();
		String queryValue = URLDecoder.decode(request.getParameter(queryKey),CodecUtil.Charset);
		if(StringUtil.isNotBlank(queryValue))
		{
			try {
				Object value = JSONObject.parseObject(queryValue, parameter.getParameterType());
				return value;
			}
			catch(Exception e)
			{
				logger.error("Error occured when parsing request["+queryKey+"].", e);
				return null;
			}
		}
		
		return null;
	}

}
