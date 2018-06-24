package com.zhjs.saas.core.exception;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.support.spring.FastJsonJsonView;

/**
 * 
 * @author:		Jackie Wang 
 * @since:		2017-06-02
 * @modified:	2017-06-02
 * @version:	
 */
public class CommonHandlerExceptionResolver implements HandlerExceptionResolver
{
	@Override
	public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response,
			Object handler, Exception ex)
	{
		ModelAndView mv = new ModelAndView(new FastJsonJsonView());
		if(!(ex instanceof BaseException))
		{
			//mv.
		}
		
		return mv;
	}

}
