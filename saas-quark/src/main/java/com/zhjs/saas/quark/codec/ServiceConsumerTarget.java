package com.zhjs.saas.quark.codec;

import static feign.Util.checkNotNull;
import static feign.Util.emptyToNull;

import feign.Request;
import feign.RequestTemplate;
import feign.Target;

/**
 * 
 * @author: 	Jackie Wang
 * @since: 		2017-12-18
 * @modified: 	2017-12-18
 * @version:
 */
public class ServiceConsumerTarget<T> implements Target<T>
{

	private final Class<T> type;
	private final String name;
	private final String url;

	public ServiceConsumerTarget(Class<T> type, String url)
	{
		this(type, url, url);
	}

	public ServiceConsumerTarget(Class<T> type, String name, String url)
	{
		this.type = checkNotNull(type, "type");
		this.name = checkNotNull(emptyToNull(name), "name");
		this.url = checkNotNull(emptyToNull(url), "url");
	}

	@Override
	public Class<T> type()
	{
		return type;
	}

	@Override
	public String name()
	{
		return name;
	}

	@Override
	public String url()
	{
		return url;
	}

	/* no authentication or other special activity. just insert the url. */
	@Override
	public Request apply(RequestTemplate input)
	{
		if (input.url().indexOf("http") != 0)
		{
			input.insert(0, url());
		}
		return input.request();
	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj instanceof ServiceConsumerTarget)
		{
			ServiceConsumerTarget<?> other = (ServiceConsumerTarget<?>) obj;
			return type.equals(other.type) && name.equals(other.name) && url.equals(other.url);
		}
		return false;
	}

	@Override
	public int hashCode()
	{
		int result = 17;
		result = 31 * result + type.hashCode();
		result = 31 * result + name.hashCode();
		result = 31 * result + url.hashCode();
		return result;
	}

	@Override
	public String toString()
	{
		if (name.equals(url))
		{
			return "ServiceConsumerTarget(type=" + type.getSimpleName() + ", url=" + url + ")";
		}
		return "ServiceConsumerTarget(type=" + type.getSimpleName() + ", name=" + name + ", url=" + url + ")";
	}

}
