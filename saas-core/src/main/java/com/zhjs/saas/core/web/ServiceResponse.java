package com.zhjs.saas.core.web;

import com.alibaba.fastjson.JSON;

/**
 * 
 * @author:		Jackie Wang 
 * @since:		2017-12-22
 * @modified:	2017-12-22
 * @version:	
 */
public class ServiceResponse<T>
{
	protected boolean success = true;
	private String message;
	private T data;
	private Object requestData;
	
	public ServiceResponse(){}
	
	public ServiceResponse(T obj)
	{
		this.data = obj;
	}
	
	/**
	 * @return the success
	 */
	public boolean isSuccess()
	{
		return success;
	}
	/**
	 * @param success the success to set
	 */
	public void setSuccess(boolean success)
	{
		this.success = success;
	}
	/**
	 * @return the message
	 */
	public String getMessage()
	{
		return message;
	}
	/**
	 * @param message the message to set
	 */
	public void setMessage(String message)
	{
		this.message = message;
	}
	/**
	 * @return the data
	 */
	public T getData()
	{
		return data;
	}
	/**
	 * @param data the data to set
	 */
	public void setData(T data)
	{
		this.data = data;
	}

	/**
	 * @return the requestData
	 */
	public Object getRequestData()
	{
		return requestData;
	}

	/**
	 * @param requestData the requestData to set
	 */
	public void setRequestData(Object requestData)
	{
		this.requestData = requestData;
	}
	

	public String toString()
	{
		return JSON.toJSONString(this);
	}


}
