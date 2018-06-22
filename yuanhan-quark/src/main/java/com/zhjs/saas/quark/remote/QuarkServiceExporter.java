package com.yuanhan.yuanhan.quark.remote;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.remoting.support.RemoteExporter;

import com.yuanhan.yuanhan.core.logger.Logger;
import com.yuanhan.yuanhan.core.logger.LoggerFactory;

/**
 * 
 * @author: 	yuanhan
 * @since: 		2017-11-08
 * @modified: 	2017-11-08
 * @version:
 */
public class QuarkServiceExporter extends RemoteExporter implements InitializingBean, DisposableBean, ApplicationContextAware
{

	private Logger logger = LoggerFactory.getLogger(getClass());

    private ApplicationContext context;    

	public void destroy() throws Exception
	{
		logger.info("This is an unimplemneted method.");
	}

	@Override
	public void afterPropertiesSet() throws Exception
	{
		logger.info("[{}]: This is an unimplemneted method.", context.getApplicationName());
	}

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
    }

}
