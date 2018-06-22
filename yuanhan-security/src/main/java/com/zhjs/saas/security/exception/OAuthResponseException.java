package com.yuanhan.yuanhan.security.exception;

import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.security.oauth2.common.exceptions.OAuth2ExceptionJackson2Deserializer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;

/**
 * 
 * @author:		yuanhan
 * @since:		2018-04-13
 * @modified:	2018-04-13
 * @version:	
 */
@SuppressWarnings("serial")
@com.fasterxml.jackson.databind.annotation.JsonSerialize(using = OAuthResponseExceptionSerializer.class)
@com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = OAuth2ExceptionJackson2Deserializer.class)
public class OAuthResponseException extends OAuth2Exception
{	
	private String errorCode;
	
	public OAuthResponseException(String errorCode, String msg, Throwable t)
	{
		super(msg, t);
		this.errorCode = OAuthException.OAuth_Error_Prefix+errorCode;
	}
	
	public String toString()
	{
		return JSON.toJSONString(this,
				SerializerFeature.DisableCircularReferenceDetect,
				SerializerFeature.WriteDateUseDateFormat,
				SerializerFeature.SkipTransientField,
				SerializerFeature.SortField);		
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

}
