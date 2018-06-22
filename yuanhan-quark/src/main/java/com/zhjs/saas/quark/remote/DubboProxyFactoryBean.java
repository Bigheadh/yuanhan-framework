package com.yuanhan.yuanhan.quark.remote;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.remoting.support.UrlBasedRemoteAccessor;

import com.alibaba.dubbo.config.spring.ReferenceBean;
import com.yuanhan.yuanhan.core.util.ApplicationContextUtil;
import com.yuanhan.yuanhan.core.util.Assert;

/**
 * 
 * @author:		yuanhan
 * @since:		2017-12-19
 * @modified:	2017-12-19
 * @version:	
 */
public class DubboProxyFactoryBean<T> extends UrlBasedRemoteAccessor implements FactoryBean<T>
{
	
	private ReferenceBean<T> bean = new ReferenceBean<>();
	
	public DubboProxyFactoryBean()
	{
		bean.setApplicationContext(ApplicationContextUtil.getApplicationContext());		
	}

	@Override
	public T getObject() throws Exception
	{
		return bean.get();
	}

	@SuppressWarnings("unchecked")
	public Class<T> getObjectType()
	{
		return (Class<T>)bean.getObjectType();
	}

	@Override
	public boolean isSingleton()
	{
		return true;
	}
	

	@Override
	public void afterPropertiesSet() 
	{
		Assert.notNull(this.getServiceInterface(), "Mirco service class must be declared.");
		try
		{
			bean.afterPropertiesSet();
		}
		catch (Exception e)
		{
			logger.error(e.getMessage(), e);
		}
	}
}
