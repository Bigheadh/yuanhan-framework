package com.yuanhan.yuanhan.quark.remote;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.remoting.support.UrlBasedRemoteAccessor;
import org.springframework.web.client.RestTemplate;

import com.yuanhan.yuanhan.core.util.Assert;

/**
 * 
 * @author:		yuanhan
 * @since:		2017-11-16
 * @modified:	2017-11-16
 * @version:	
 */
public class RestProxyFactoryBean<T> extends UrlBasedRemoteAccessor implements FactoryBean<T>
{
	private RestTemplate restTemplate;
	
	public RestProxyFactoryBean()
	{
		restTemplate = new RestTemplate();
	}

	@Override
	public void afterPropertiesSet() 
	{
		Assert.notNull(this.getServiceUrl(), "Micro service url must not be null, please call setServiceUrl().");
		Assert.notNull(this.getServiceInterface(), "Mirco service class must be declared.");
	}

	@Override
	public T getObject() throws Exception
	{
		return restTemplate.getForObject(this.getServiceUrl(), getObjectType());
	}

	@SuppressWarnings("unchecked")
	public Class<T> getObjectType()
	{
		return (Class<T>)getServiceInterface();
	}

	@Override
	public boolean isSingleton()
	{
		return true;
	}

}
