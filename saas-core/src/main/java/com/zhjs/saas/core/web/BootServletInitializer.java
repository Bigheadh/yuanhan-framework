package com.zhjs.saas.core.web;

import javax.servlet.ServletContext;

import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.web.context.WebApplicationContext;

import com.zhjs.saas.core.event.ApplicationBootedEvent;
import com.zhjs.saas.core.event.EventDispatcher;
import com.zhjs.saas.core.util.ApplicationContextUtil;

/**
 * 
 * @author:		Jackie Wang 
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
