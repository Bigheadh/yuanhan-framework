package com.zhjs.saas.security.exception;

import com.zhjs.saas.core.exception.BaseException;

/**
 * 
 * @author:		Jackie Wang 
 * @since:		2018-03-02
 * @modified:	2018-03-02
 * @version:	
 */
public class OAuthException extends BaseException
{

	private static final long serialVersionUID = 1632075076761715294L;
	
	public final static String OAuth_Unauthorized = "isp.oauth.error.unauthorized";
	
	public final static String OAuth_Error_Prefix = "isp.oauth.error.";

	public OAuthException()
	{
		super(OAuth_Unauthorized);
	}
	
	public OAuthException(String errorCode)
	{
		super(errorCode);
	}

}
