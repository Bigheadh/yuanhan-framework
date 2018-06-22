package com.yuanhan.yuanhan.quark.endpoint;

/**
 * 
 * @author:		yuanhan
 * @since:		2017-12-08
 * @modified:	2017-12-08
 * @version:	
 */
public class MicroServiceInfo
{
	private String application;
	private String ip;
	private int port;
	private String service;
	private String version;
	private String beanMethod;
	private String url;
	private long error = 0l;
	private long serviceError = 0l;
	private long clientError = 0l;
	private long systemError = 0l;
	private long success = 0l;
	private long requests = 0l;
	private long uptimes = 0l;
	private long maxResponse = 0l;
	private long minResponse = 0l;
	private long totalResponse = 0l;
	
	/**
	 * @return the application
	 */
	public String getApplication()
	{
		return application;
	}
	/**
	 * @param application the application to set
	 */
	public void setApplication(String application)
	{
		this.application = application;
	}
	/**
	 * @return the service
	 */
	public String getService()
	{
		return service;
	}
	/**
	 * @param service the service to set
	 */
	public void setService(String service)
	{
		this.service = service;
	}
	/**
	 * @return the version
	 */
	public String getVersion()
	{
		return version;
	}
	/**
	 * @param version the version to set
	 */
	public void setVersion(String version)
	{
		this.version = version;
	}
	/**
	 * @return the error
	 */
	public long getError()
	{
		return error;
	}
	/**
	 * @param error the error to set
	 */
	public void setError(long error)
	{
		this.error = error;
	}
	/**
	 * @return the serviceError
	 */
	public long getServiceError()
	{
		return serviceError;
	}
	/**
	 * @param serviceError the serviceError to set
	 */
	public void setServiceError(long serviceError)
	{
		this.serviceError = serviceError;
	}
	/**
	 * @return the clientError
	 */
	public long getClientError()
	{
		return clientError;
	}
	/**
	 * @param clientError the clientError to set
	 */
	public void setClientError(long clientError)
	{
		this.clientError = clientError;
	}
	/**
	 * @return the systemError
	 */
	public long getSystemError()
	{
		return systemError;
	}
	/**
	 * @param systemError the systemError to set
	 */
	public void setSystemError(long systemError)
	{
		this.systemError = systemError;
	}
	/**
	 * @return the success
	 */
	public long getSuccess()
	{
		return success;
	}
	/**
	 * @param success the success to set
	 */
	public void setSuccess(long success)
	{
		this.success = success;
	}
	/**
	 * @return the requests
	 */
	public long getRequests()
	{
		return requests;
	}
	/**
	 * @param requests the requests to set
	 */
	public void setRequests(long requests)
	{
		this.requests = requests;
	}
	/**
	 * @return the uptimes
	 */
	public long getUptimes()
	{
		return uptimes;
	}
	/**
	 * @param uptimes the uptimes to set
	 */
	public void setUptimes(long uptimes)
	{
		this.uptimes = uptimes;
	}
	/**
	 * @return the totalResponse
	 */
	public long getTotalResponse()
	{
		return totalResponse;
	}
	/**
	 * @param totalResponse the totalResponse to set
	 */
	public void setTotalResponse(long totalResponse)
	{
		this.totalResponse = totalResponse;
	}
	/**
	 * @return the maxResponse
	 */
	public long getMaxResponse()
	{
		return maxResponse;
	}
	/**
	 * @param maxResponse the maxResponse to set
	 */
	public void setMaxResponse(long maxResponse)
	{
		this.maxResponse = maxResponse;
	}
	/**
	 * @return the minResponse
	 */
	public long getMinResponse()
	{
		return minResponse;
	}
	/**
	 * @param minResponse the minResponse to set
	 */
	public void setMinResponse(long minResponse)
	{
		this.minResponse = minResponse;
	}
	/**
	 * @return the ip
	 */
	public String getIp()
	{
		return ip;
	}
	/**
	 * @param ip the ip to set
	 */
	public void setIp(String ip)
	{
		this.ip = ip;
	}
	/**
	 * @return the port
	 */
	public int getPort()
	{
		return port;
	}
	/**
	 * @param port the port to set
	 */
	public void setPort(int port)
	{
		this.port = port;
	}
	/**
	 * @return the beanMethod
	 */
	public String getBeanMethod()
	{
		return beanMethod;
	}
	/**
	 * @param beanMethod the beanMethod to set
	 */
	public void setBeanMethod(String beanMethod)
	{
		this.beanMethod = beanMethod;
	}
	/**
	 * @return the url
	 */
	public String getUrl()
	{
		return url;
	}
	/**
	 * @param url the url to set
	 */
	public void setUrl(String url)
	{
		this.url = url;
	}
	
	
	

}
