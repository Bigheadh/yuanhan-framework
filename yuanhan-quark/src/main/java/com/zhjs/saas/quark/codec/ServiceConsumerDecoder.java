package com.yuanhan.yuanhan.quark.codec;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.LinkedList;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import feign.FeignException;
import feign.Response;
import feign.codec.Decoder;

/**
 * 
 * @author: 	yuanhan
 * @since: 		2017-12-18
 * @modified: 	2017-12-18
 * @version:
 */
public class ServiceConsumerDecoder implements Decoder
{

	private Decoder decoder;

	public ServiceConsumerDecoder(Decoder decoder)
	{
		this.decoder = decoder;
	}

	@Override
	public Object decode(final Response response, Type type) throws IOException, FeignException
	{
		if (isParameterizeHttpEntity(type))
		{
			type = ((ParameterizedType) type).getActualTypeArguments()[0];
			Object decodedObject = decoder.decode(response, type);

			return createResponse(decodedObject, response);
		}
		else if (isHttpEntity(type))
		{
			return createResponse(null, response);
		}
		else
		{
			return decoder.decode(response, type);
		}
	}

	private boolean isParameterizeHttpEntity(Type type)
	{
		if (type instanceof ParameterizedType)
		{
			return isHttpEntity(((ParameterizedType) type).getRawType());
		}
		return false;
	}

	private boolean isHttpEntity(Type type)
	{
		if (type instanceof Class)
			return HttpEntity.class.isAssignableFrom( (Class<?>) type);
		return false;
	}

	@SuppressWarnings("unchecked")
	private <T> ResponseEntity<T> createResponse(Object instance, Response response)
	{
		MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
		for (String key : response.headers().keySet())
		{
			headers.put(key, new LinkedList<>(response.headers().get(key)));
		}

		return new ResponseEntity<>((T) instance, headers, HttpStatus.valueOf(response.status()));
	}
}