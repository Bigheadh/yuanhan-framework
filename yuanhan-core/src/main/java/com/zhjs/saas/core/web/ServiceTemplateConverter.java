package com.yuanhan.yuanhan.core.web;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import com.yuanhan.yuanhan.core.exception.ExceptionResponse;
import com.yuanhan.yuanhan.core.util.StringUtil;

/**
 * 
 * @author:		yuanhan
 * @since:		2017-12-19
 * @modified:	2017-12-19
 * @version:	
 */
public class ServiceTemplateConverter extends FastJsonHttpMessageConverter
{

	public ServiceTemplateConverter(String dateFormat)
	{
		FastJsonConfig config = new FastJsonConfig();
		config.setSerializerFeatures(SerializerFeature.PrettyFormat,
				SerializerFeature.WriteDateUseDateFormat,
				SerializerFeature.DisableCircularReferenceDetect);
		if(StringUtil.isNotBlank(dateFormat))
			config.setDateFormat(dateFormat);
		this.setFastJsonConfig(config);
		this.setSupportedMediaTypes(initMediaType());
	}

    @Override
    protected boolean supports(Class<?> paramClass) {
        return true;
    }
    
    @Override
    protected Object readInternal(Class<? extends Object> clazz, HttpInputMessage inputMessage)
    		throws IOException, HttpMessageNotReadableException
    {
    	FastJsonConfig config = this.getFastJsonConfig();
        try {
        	if(clazz.isAssignableFrom(CommonResponse.class))
        		return super.readInternal(clazz, inputMessage);
        	
        	String json = StringUtil.copyToString(inputMessage.getBody(), config.getCharset());
        	JSONObject response = JSONObject.parseObject(json, config.getFeatures());
        	if(response.getBoolean("success"))
    			return response.getObject("data", clazz);
            return response.toJavaObject(ExceptionResponse.class);
        } catch (JSONException ex) {
            throw new HttpMessageNotReadableException("JSON parse error: " + ex.getMessage(), ex);
        } catch (IOException ex) {
            throw new HttpMessageNotReadableException("I/O error while reading input message", ex);
        }
    }

    @Override
    protected void writeInternal(Object obj, HttpOutputMessage outputMessage)
    		throws IOException, HttpMessageNotWritableException
    {
    	super.writeInternal(obj, outputMessage);
    }
    
    protected List<MediaType> initMediaType()
    {
		List<MediaType> supportedMediaTypes = new ArrayList<>();
		supportedMediaTypes.add(MediaType.APPLICATION_JSON);
		supportedMediaTypes.add(MediaType.APPLICATION_JSON_UTF8);
		supportedMediaTypes.add(MediaType.APPLICATION_ATOM_XML);
		supportedMediaTypes.add(MediaType.APPLICATION_FORM_URLENCODED);
		supportedMediaTypes.add(MediaType.APPLICATION_OCTET_STREAM);
		supportedMediaTypes.add(MediaType.APPLICATION_PDF);
		supportedMediaTypes.add(MediaType.APPLICATION_RSS_XML);
		supportedMediaTypes.add(MediaType.APPLICATION_XHTML_XML);
		supportedMediaTypes.add(MediaType.APPLICATION_XML);
		supportedMediaTypes.add(MediaType.IMAGE_GIF);
		supportedMediaTypes.add(MediaType.IMAGE_JPEG);
		supportedMediaTypes.add(MediaType.IMAGE_PNG);
		supportedMediaTypes.add(MediaType.TEXT_EVENT_STREAM);
		supportedMediaTypes.add(MediaType.TEXT_HTML);
		supportedMediaTypes.add(MediaType.TEXT_MARKDOWN);
		supportedMediaTypes.add(MediaType.TEXT_PLAIN);
		supportedMediaTypes.add(MediaType.TEXT_XML);
		return supportedMediaTypes;
    }

}
