package com.yuanhan.yuanhan.core.web;

import static com.yuanhan.yuanhan.core.config.PropertyConfig.Remote_Gateway;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestTemplate;

import com.alibaba.fastjson.util.ParameterizedTypeImpl;
import com.yuanhan.yuanhan.core.exception.CommonResponseErrorHandler;
import com.yuanhan.yuanhan.core.util.StringUtil;

/**
 * 
 * @author: 	yuanhan
 * @since: 		2017-12-13
 * @modified: 	2017-12-13
 * @version:
 */
public class ServiceTemplate extends RestTemplate
{

	@Value("${"+Remote_Gateway+":gateway}")
	private String gateway;
	

	public ServiceTemplate() {
		super();
		this.setErrorHandler(new CommonResponseErrorHandler());
	}

	public ServiceTemplate(ClientHttpRequestFactory requestFactory) {
		super(requestFactory);
		this.setErrorHandler(new CommonResponseErrorHandler());
	}

	public ServiceTemplate(List<HttpMessageConverter<?>> messageConverters) {
		super(messageConverters);
		this.setErrorHandler(new CommonResponseErrorHandler());
	}
	
	/**
	 * 
	 * @param messageConverter	
	 * @param clear	indicate if you want to clear the original converter list, or just append a new one
	 * @return
	 */
	public ServiceTemplate addMessageConverter(HttpMessageConverter<?> messageConverter, boolean clear)
	{
		if(clear)
		{
			List<HttpMessageConverter<?>> converters = new ArrayList<>();
			converters.add(messageConverter);
			this.setMessageConverters(converters);
		}
		else
			this.getMessageConverters().add(messageConverter);
		return this;
	}

	/**
	 * 
	 * @param messageConverter	list of HttpMessageConverter
	 * @param clear  indicate if you want to clear the original converter list, or just append a new one
	 * @return
	 */
	public ServiceTemplate addMessageConverters(List<HttpMessageConverter<?>> converters, boolean clear)
	{
		if(clear)
		{
			this.setMessageConverters(converters);
		}
		else
			this.getMessageConverters().addAll(converters);
		return this;
	}
	
	/*
	public <T> T post(String url, Object request, Class<T> responseType, Object...uriVariables) throws Exception 
	{
		url = resolve(url);
		return super.postForObject(url, request, responseType, uriVariables);
	}


	public <T> T post(String url, Object request, Class<T> responseType, Map<String,?> uriVariables) throws Exception 
	{
		StringBuilder uri = new StringBuilder(resolve(url));
		if(uriVariables!=null && uriVariables.keySet().size()>0)
		{
			if(url.indexOf("?")==-1)
			{
				uri.append("?");
			}
			uriVariables.keySet().stream().filter(key -> !key.equalsIgnoreCase("method")).forEach(key -> {
				uri.append("&").append(key).append("={").append(key).append("}");
			});
		}
		return super.postForObject(uri.toString(), request, responseType, uriVariables);
	}
	
	public <T> T get(String url, Class<T> responseType, Object...uriVariables) throws Exception 
	{
		url = resolve(url);
		return super.getForObject(url, responseType, uriVariables);
	}


	public <T> T get(String url, Class<T> responseType, Map<String,?> uriVariables) throws Exception 
	{
		StringBuilder uri = new StringBuilder(resolve(url));
		if(uriVariables!=null && uriVariables.keySet().size()>0)
		{
			if(url.indexOf("?")==-1)
			{
				uri.append("?");
			}
			uriVariables.keySet().stream().filter(key -> !key.equalsIgnoreCase("method")).forEach(key -> {
				uri.append("&").append(key).append("={").append(key).append("}");
			});
		}
		return super.getForObject(uri.toString(), responseType, uriVariables);
	}
	*/

	public <T,E> T post(String url, E reqParam, Class<T> responseType, Object...uriVariables) throws Exception 
	{
		url = resolve(url);
		RequestCallback requestCallback = httpEntityCallback(reqParam, responseType);
		ResponseExtractor<ResponseEntity<ServiceResponse<T>>> responseExtractor = responseEntityExtractor(responseType);
		return execute(url, HttpMethod.POST, requestCallback, responseExtractor, uriVariables).getBody().getData();
	}


	public <T,E> T post(String url, E reqParam, Class<T> responseType, Map<String,?> uriVariables) throws Exception 
	{
		StringBuilder uri = new StringBuilder(resolve(url));
		if(uriVariables!=null && uriVariables.keySet().size()>0)
		{
			if(url.indexOf("?")==-1)
			{
				uri.append("?");
			}
			uriVariables.keySet().stream().filter(key -> !key.equalsIgnoreCase("method")).forEach(key -> {
				uri.append("&").append(key).append("={").append(key).append("}");
			});
		}
		RequestCallback requestCallback = httpEntityCallback(reqParam, responseType);
		ResponseExtractor<ResponseEntity<ServiceResponse<T>>> responseExtractor = responseEntityExtractor(responseType);
		return execute(uri.toString(), HttpMethod.POST, requestCallback, responseExtractor, uriVariables).getBody().getData();
	}
	
	public <T,E> List<T> postForList(String url, E reqParam, Class<T> responseType, Object...uriVariables) throws Exception 
	{
		url = resolve(url);
		return super.exchange(url, HttpMethod.POST, new HttpEntity<E>(reqParam),
								resolveParameterizedType(responseType), uriVariables).getBody().getData();
	}
	
	public <T,E> List<T> postForList(String url, E reqParam, Class<T> responseType, Map<String,?> uriVariables) throws Exception 
	{
		StringBuilder uri = new StringBuilder(resolve(url));
		if(uriVariables!=null && uriVariables.keySet().size()>0)
		{
			if(url.indexOf("?")==-1)
			{
				uri.append("?");
			}
			uriVariables.keySet().stream().filter(key -> !key.equalsIgnoreCase("method")).forEach(key -> {
				uri.append("&").append(key).append("={").append(key).append("}");
			});
		}
		return super.exchange(uri.toString(), HttpMethod.POST, new HttpEntity<E>(reqParam),
								resolveParameterizedType(responseType), uriVariables).getBody().getData();
	}

	public <T> T get(String url, Class<T> responseType, Object...uriVariables) throws Exception 
	{
		url = resolve(url);
		RequestCallback requestCallback = acceptHeaderRequestCallback(responseType);
		ResponseExtractor<ResponseEntity<ServiceResponse<T>>> responseExtractor = responseEntityExtractor(responseType);
		return super.execute(url, HttpMethod.GET, requestCallback, responseExtractor, uriVariables).getBody().getData();
	}


	public <T> T get(String url, Class<T> responseType, Map<String,?> uriVariables) throws Exception 
	{
		StringBuilder uri = new StringBuilder(resolve(url));
		if(uriVariables!=null && uriVariables.keySet().size()>0)
		{
			if(url.indexOf("?")==-1)
			{
				uri.append("?");
			}
			uriVariables.keySet().stream().filter(key -> !key.equalsIgnoreCase("method")).forEach(key -> {
				uri.append("&").append(key).append("={").append(key).append("}");
			});
		}
		RequestCallback requestCallback = acceptHeaderRequestCallback(responseType);
		ResponseExtractor<ResponseEntity<ServiceResponse<T>>> responseExtractor = responseEntityExtractor(responseType);
		return super.execute(uri.toString(), HttpMethod.GET, requestCallback, responseExtractor, uriVariables).getBody().getData();
	}
	
	public <T> List<T> getForList(String url, Class<T> responseType, Object...uriVariables) throws Exception 
	{
		url = resolve(url);
		return super.exchange(url, HttpMethod.GET, null, resolveParameterizedType(responseType), uriVariables).getBody().getData();
	}
	
	public <T> List<T> getForList(String url, Class<T> responseType, Map<String,?> uriVariables) throws Exception 
	{
		StringBuilder uri = new StringBuilder(resolve(url));
		if(uriVariables!=null && uriVariables.keySet().size()>0)
		{
			if(url.indexOf("?")==-1)
			{
				uri.append("?");
			}
			uriVariables.keySet().stream().filter(key -> !key.equalsIgnoreCase("method")).forEach(key -> {
				uri.append("&").append(key).append("={").append(key).append("}");
			});
		}
		return super.exchange(uri.toString(), HttpMethod.GET, null, resolveParameterizedType(responseType), uriVariables).getBody().getData();
	}
	
	protected String resolve(String url)
	{
		if(StringUtil.isNotBlank(gateway) && !gateway.equals("gateway") && StringUtil.isNotBlank(url) 
			&& !StringUtil.startsWithIgnoreCase(url,"http://") && !StringUtil.startsWithIgnoreCase(url,"https://"))
		{			
			url = gateway + (gateway.endsWith("/")||url.startsWith("/")?"":"/") + url;
		}
		return url;
	}
	
	protected <T> ParameterizedTypeReference<ServiceResponse<List<T>>> resolveParameterizedType(Class<T> clazz)
	{
		return new ServiceParameterizedTypeReference<ServiceResponse<List<T>>>(clazz){};
	}
	

	private static abstract class ServiceParameterizedTypeReference<T> extends ParameterizedTypeReference<T> {

		private final Type type;
		
		protected ServiceParameterizedTypeReference(Class<?> clazz)
		{
			ParameterizedType list = new ParameterizedTypeImpl(new Type[]{clazz}, null, List.class);
			ParameterizedType service = new ParameterizedTypeImpl(new Type[]{list}, null, ServiceResponse.class);
			this.type = service;
		}

		public Type getType() {
			return this.type;
		}

		@Override
		public boolean equals(Object obj) {
			return (this == obj || (obj instanceof ServiceParameterizedTypeReference && this.type
					.equals(((ServiceParameterizedTypeReference<?>) obj).type)));
		}

		@Override
		public int hashCode() {
			return this.type.hashCode();
		}

		@Override
		public String toString() {
			return "ServiceTemplate.ServiceParameterizedTypeReference<" + this.type + ">";
		}
	}

	
	
}
