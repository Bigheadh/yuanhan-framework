package com.yuanhan.yuanhan.core.web.listener;

import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;
import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpServletRequest;

import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.RequestContextListener;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.yuanhan.yuanhan.core.logger.Logger;
import com.yuanhan.yuanhan.core.logger.LoggerFactory;

/**
 * 
 * @author:		yuanhan
 * @since:		2017-09-20
 * @modified:	2017-09-20
 * @version:
 */
@WebListener
public class WebContextHolderListener implements ServletRequestListener
{
	private Logger logger = LoggerFactory.getLogger(getClass());

	private static final String REQUEST_ATTRIBUTES_ATTRIBUTE = RequestContextListener.class.getName() + ".REQUEST_ATTRIBUTES";

	@Override
	public void requestInitialized(ServletRequestEvent requestEvent)
	{
		if (!(requestEvent.getServletRequest() instanceof HttpServletRequest))
		{
			logger.error("yuanhanFramework gets an invalid request type, which will be ingored and all next processure continue on.",
						new IllegalArgumentException("Request is not an HttpServletRequest: " + requestEvent.getServletRequest()));
			return;
		}
		HttpServletRequest request = (HttpServletRequest) requestEvent.getServletRequest();
		ServletRequestAttributes attributes = new ServletRequestAttributes(request);
		request.setAttribute(REQUEST_ATTRIBUTES_ATTRIBUTE, attributes);
		LocaleContextHolder.setLocale(request.getLocale());
		RequestContextHolder.setRequestAttributes(attributes);
	}

	@Override
	public void requestDestroyed(ServletRequestEvent requestEvent)
	{
		ServletRequestAttributes attributes = null;
		Object reqAttr = requestEvent.getServletRequest().getAttribute(REQUEST_ATTRIBUTES_ATTRIBUTE);
		if (reqAttr instanceof ServletRequestAttributes)
		{
			attributes = (ServletRequestAttributes) reqAttr;
		}
		RequestAttributes threadAttributes = RequestContextHolder.getRequestAttributes();
		if (threadAttributes != null)
		{
			// We assume within the original request thread...
			LocaleContextHolder.resetLocaleContext();
			RequestContextHolder.resetRequestAttributes();
			if (attributes == null && threadAttributes instanceof ServletRequestAttributes)
			{
				attributes = (ServletRequestAttributes) threadAttributes;
			}
		}
		if (attributes != null)
		{
			attributes.requestCompleted();
		}
		logger.debug("RequestContextHolder has been clear.");
	}

}
