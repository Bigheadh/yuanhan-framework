package com.zhjs.saas.core.exception;

import com.zhjs.saas.core.web.CommonResponse;

/**
 * 
 * @author:		Jackie Wang 
 * @since:		2017-05-17
 * @modified:	2017-05-17
 * @version:	
 */
public class ExceptionResponse extends CommonResponse
{	
	private String errorCode;
	private String traceID;
	
	public ExceptionResponse()
	{
		super();
		this.success = false;
	}

	/**
	 * @return the errorCode
	 */
	public String getErrorCode()
	{
		return errorCode;
	}

	/**
	 * @param errorCode the errorCode to set
	 */
	public void setErrorCode(String errorCode)
	{
		this.errorCode = errorCode;
	}
	
	/**
	 * @return the success
	 */
	public boolean isSuccess()
	{
		return false;
	}
	
	/**
	 * it will always be set value to false, even though you try to give a true value.
	 * @param success the success to set
	 */
	public void setSuccess(boolean success)
	{
		this.success = false;
	}

	public String getTraceID()
	{
		return traceID;
	}

	public void setTraceID(String traceID)
	{
		this.traceID = traceID;
	}


}
