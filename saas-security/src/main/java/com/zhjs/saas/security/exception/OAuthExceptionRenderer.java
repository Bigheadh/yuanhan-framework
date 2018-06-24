package com.zhjs.saas.security.exception;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.security.oauth2.http.converter.jaxb.JaxbOAuth2ExceptionMessageConverter;
import org.springframework.security.oauth2.provider.error.OAuth2ExceptionRenderer;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.ServletWebRequest;

import com.zhjs.saas.core.exception.ExceptionResponse;
import com.zhjs.saas.core.logger.Logger;
import com.zhjs.saas.core.logger.LoggerFactory;
import com.zhjs.saas.core.util.MessageUtil;

/**
 * 
 * @author:		Jackie Wang 
 * @since:		2018-03-01
 * @modified:	2018-03-01
 * @version:	
 */
public class OAuthExceptionRenderer implements OAuth2ExceptionRenderer
{

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	private List<HttpMessageConverter<?>> messageConverters = getDefaultMessageConverters();

	public void setMessageConverters(List<HttpMessageConverter<?>> messageConverters) {
		this.messageConverters = messageConverters;
	}

	public void handleHttpEntityResponse(HttpEntity<?> responseEntity, ServletWebRequest webRequest) throws Exception {
		if (responseEntity == null) {
			return;
		}
		HttpInputMessage inputMessage = createHttpInputMessage(webRequest);
		HttpOutputMessage outputMessage = createHttpOutputMessage(webRequest);
		if (responseEntity instanceof ResponseEntity && outputMessage instanceof ServerHttpResponse) {
			((ServerHttpResponse) outputMessage).setStatusCode(((ResponseEntity<?>) responseEntity).getStatusCode());
		}
		HttpHeaders entityHeaders = responseEntity.getHeaders();
		if (!entityHeaders.isEmpty()) {
			outputMessage.getHeaders().putAll(entityHeaders);
		}
		Object body = responseEntity.getBody();		
		if (body != null) {
			if(body instanceof OAuth2Exception)
			{
				ExceptionResponse response = new ExceptionResponse();
				response.setErrorCode(routeErrorCode((OAuth2Exception)body));
				response.setMessage(MessageUtil.getMessage(response.getErrorCode()));
				response.setData(body);
				writeWithMessageConverters(response, inputMessage, outputMessage);
			}
			else
				writeWithMessageConverters(body, inputMessage, outputMessage);
		}
		else {
			// flush headers
			outputMessage.getBody();
		}
	}
	
	private String routeErrorCode(OAuth2Exception e)
	{
		/*
		if(e instanceof InvalidClientException)
		{
			
		}
		else if (e instanceof UnauthorizedClientException) {

		}
		else if (e instanceof InvalidGrantException) {

		}
		else if (e instanceof InvalidScopeException) {

		}
		else if (e instanceof InvalidTokenException) {

		}
		else if (e instanceof InvalidRequestException) {

		}
		else if (e instanceof RedirectMismatchException) {

		}
		else if (e instanceof UnsupportedGrantTypeException) {

		}
		else if (e instanceof UnsupportedResponseTypeException) {

		}
		else if (e instanceof UserDeniedAuthorizationException) {

		}
		*/
		return OAuthException.OAuth_Error_Prefix+e.getOAuth2ErrorCode();
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void writeWithMessageConverters(Object returnValue, HttpInputMessage inputMessage,
			HttpOutputMessage outputMessage) throws IOException, HttpMediaTypeNotAcceptableException {
		List<MediaType> acceptedMediaTypes = inputMessage.getHeaders().getAccept();
		if (acceptedMediaTypes.isEmpty()) {
			acceptedMediaTypes = Collections.singletonList(MediaType.ALL);
		}
		MediaType.sortByQualityValue(acceptedMediaTypes);
		Class<?> returnValueType = returnValue.getClass();
		List<MediaType> allSupportedMediaTypes = new ArrayList<MediaType>();
		for (MediaType acceptedMediaType : acceptedMediaTypes) {
			for (HttpMessageConverter messageConverter : messageConverters) {
				if (messageConverter.canWrite(returnValueType, acceptedMediaType)) {
					messageConverter.write(returnValue, acceptedMediaType, outputMessage);
					if (logger.isDebugEnabled()) {
						MediaType contentType = outputMessage.getHeaders().getContentType();
						if (contentType == null) {
							contentType = acceptedMediaType;
						}
						logger.debug("Written [" + returnValue + "] as \"" + contentType + "\" using ["
								+ messageConverter + "]");
					}
					return;
				}
			}
		}
		for (HttpMessageConverter messageConverter : messageConverters) {
			allSupportedMediaTypes.addAll(messageConverter.getSupportedMediaTypes());
		}
		throw new HttpMediaTypeNotAcceptableException(allSupportedMediaTypes);
	}

	private List<HttpMessageConverter<?>> getDefaultMessageConverters() {
		List<HttpMessageConverter<?>> result = new ArrayList<HttpMessageConverter<?>>();
		result.addAll(new RestTemplate().getMessageConverters());
		result.add(new JaxbOAuth2ExceptionMessageConverter());
		return result;
	}

	private HttpInputMessage createHttpInputMessage(NativeWebRequest webRequest) throws Exception {
		HttpServletRequest servletRequest = webRequest.getNativeRequest(HttpServletRequest.class);
		return new ServletServerHttpRequest(servletRequest);
	}

	private HttpOutputMessage createHttpOutputMessage(NativeWebRequest webRequest) throws Exception {
		HttpServletResponse servletResponse = (HttpServletResponse) webRequest.getNativeResponse();
		return new ServletServerHttpResponse(servletResponse);
	}

}
