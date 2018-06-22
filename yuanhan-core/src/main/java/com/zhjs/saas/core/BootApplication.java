package com.yuanhan.yuanhan.core;

import java.util.Arrays;

import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.io.DefaultResourceLoader;

import com.yuanhan.yuanhan.core.event.ApplicationBootedEvent;
import com.yuanhan.yuanhan.core.event.EventDispatcher;
import com.yuanhan.yuanhan.core.logger.Logger;
import com.yuanhan.yuanhan.core.logger.LoggerFactory;
import com.yuanhan.yuanhan.core.util.ApplicationContextUtil;
import com.yuanhan.yuanhan.core.util.ClassUtil;

/**
 * 
 * @author:		yuanhan
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
