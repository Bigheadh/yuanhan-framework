package com.zhjs.saas.security.exception;

import java.io.IOException;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.InsufficientScopeException;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.security.oauth2.provider.error.DefaultWebResponseExceptionTranslator;

/**
 * 
 * @author:		Jackie Wang 
 * @since:		2018-04-13
 * @modified:	2018-04-13
 * @version:	
 */
public class OAuthResponseExceptionTranslator extends DefaultWebResponseExceptionTranslator
{
	

	public ResponseEntity<OAuth2Exception> translate(Exception e) throws Exception 
	{
		ResponseEntity<OAuth2Exception> response = super.translate(e);
		OAuth2Exception oe = response.getBody();
		OAuthResponseException ose = new OAuthResponseException(oe.getOAuth2ErrorCode(), oe.getMessage(), oe);
		return handleOAuth2Exception(ose);
	}

	private ResponseEntity<OAuth2Exception> handleOAuth2Exception(OAuth2Exception e) throws IOException 
	{
		int status = e.getHttpErrorCode();
		HttpHeaders headers = new HttpHeaders();
		headers.set("Cache-Control", "no-store");
		headers.set("Pragma", "no-cache");
		if (status == HttpStatus.UNAUTHORIZED.value() || (e instanceof InsufficientScopeException)) {
			headers.set("WWW-Authenticate", String.format("%s %s", OAuth2AccessToken.BEARER_TYPE, e.getSummary()));
		}
		ResponseEntity<OAuth2Exception> response = new ResponseEntity<OAuth2Exception>(e, headers,
				HttpStatus.valueOf(status));

		return response;
	}
}
