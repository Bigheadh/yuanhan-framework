package com.zhjs.saas.core;

import java.util.Arrays;

import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.io.DefaultResourceLoader;

import com.zhjs.saas.core.event.ApplicationBootedEvent;
import com.zhjs.saas.core.event.EventDispatcher;
import com.zhjs.saas.core.logger.Logger;
import com.zhjs.saas.core.logger.LoggerFactory;
import com.zhjs.saas.core.util.ApplicationContextUtil;
import com.zhjs.saas.core.util.ClassUtil;

/**
 * 
 * @author:		Jackie Wang 
 * @since:		2017-05-19
 * @modified:	2017-05-19
 * @version:	
 */
public class BootApplication
{
	protected static Logger logger = LoggerFactory.getLogger(BootApplication.class);
	
	public static ConfigurableApplicationContext run(Object source, String... args) {
		SpringApplication app = new SpringApplication(new DefaultResourceLoader(ClassUtil.getDefaultClassLoader()), source);
		//app.setBanner(banner);
		logger.info("Starting application with arguments: {}", Arrays.toString(args));
		ConfigurableApplicationContext application = app.run(args);
		ApplicationContextUtil.setApplicationContext(application);
		EventDispatcher.dispatchEvent(new ApplicationBootedEvent(application));
		return application;
	}
	
}
