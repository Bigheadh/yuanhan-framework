package com.yuanhan.yuanhan.security.exception;

import java.lang.reflect.Method;

import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.mvc.method.annotation.ServletInvocableHandlerMethod;

/**
 * 
 * @author:		yuanhan
 * @since:		2018-04-12
 * @modified:	2018-04-12
 * @version:	
 */
public class OAuthInvocableHandlerMethod extends ServletInvocableHandlerMethod
{

	public OAuthInvocableHandlerMethod(HandlerMethod handlerMethod)
	{
		super(handlerMethod);
	}
	
	public OAuthInvocableHandlerMethod(Object handler, Method method) {
		super(handler, method);
	}
	

	@SuppressWarnings("unchecked")
	public Object invokeForRequest(NativeWebRequest request, ModelAndViewContainer mavContainer, Object... providedArgs) throws Exception 
	{
		Object returnValue = super.invokeForRequest(request, mavContainer, providedArgs);
		if(returnValue instanceof ResponseEntity && ((ResponseEntity<?>)returnValue).getBody() instanceof OAuth2Exception)
		{
			ResponseEntity<OAuth2Exception> responseEntity = (ResponseEntity<OAuth2Exception>)returnValue;
			/*ExceptionResponse response = new ExceptionResponse();
			response.setErrorCode(OAuthException.OAuth_Error_Prefix+responseEntity.getBody().getOAuth2ErrorCode());
			response.setMessage(MessageUtil.getMessage(response.getErrorCode()));
			response.setData(responseEntity.getBody());*/
			OAuth2Exception e = responseEntity.getBody();
			OAuthResponseException oe = new OAuthResponseException(e.getOAuth2ErrorCode(), e.getMessage(), e);
			returnValue =  new ResponseEntity<OAuthResponseException>(oe, responseEntity.getHeaders(), responseEntity.getStatusCode());
		}
		return returnValue;
	}
}
