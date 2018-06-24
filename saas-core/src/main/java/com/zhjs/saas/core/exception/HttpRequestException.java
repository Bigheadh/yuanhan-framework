package com.zhjs.saas.core.exception;

/**
 * 
 * @author:		Jackie Wang 
 * @since:		2017-10-22
 * @modified:	2017-10-22
 * @version:	
 */
public class HttpRequestException extends BaseException
{

	private static final long serialVersionUID = -540034043968947014L;

	public HttpRequestException(String errorCode, Throwable cause)
	{
		super(errorCode, cause);
	}

	public HttpRequestException(String errorCode, String causeMessage, Throwable cause)
	{
		super(errorCode, causeMessage, cause);
	}


}
