package com.zhjs.saas.core.logger.support;

import org.slf4j.LoggerFactory;

import com.zhjs.saas.core.logger.ILoggerFactory;
import com.zhjs.saas.core.logger.Logger;

/**
 * 
 * @author:		Jackie Wang 
 * @since:		2017-11-08
 * @modified:	2017-11-08
 * @version:	
 */
public class DefaultLoggerFactory implements ILoggerFactory
{

	@Override
	public Logger getLogger(String name)
	{
		return new DefaultLogger(LoggerFactory.getLogger(name));
	}

	@Override
	public Logger getLogger(Class<?> clazz)
	{
		return new DefaultLogger(LoggerFactory.getLogger(clazz));
	}

}
