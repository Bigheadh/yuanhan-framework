package com.yuanhan.yuanhan.core.web;

import javax.servlet.ServletContext;

import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.web.context.WebApplicationContext;

import com.yuanhan.yuanhan.core.event.ApplicationBootedEvent;
import com.yuanhan.yuanhan.core.event.EventDispatcher;
import com.yuanhan.yuanhan.core.util.ApplicationContextUtil;

/**
 * 
 * @author:		yuanhan
 * @since:		2017-10-16
 * @modified:	2017-10-16
 * @version:	
 */
public abstract class BootServletInitializer extends SpringBootServletInitializer
{
	protected WebApplicationContext createRootApplicationContext(ServletContext servletContext)
	{
		WebApplicationContext application = super.createRootApplicationContext(servletContext);
		ApplicationContextUtil.setApplicationContext(application);
		EventDispatcher.dispatchEvent(new ApplicationBootedEvent(application));
		return application;
	}

}
