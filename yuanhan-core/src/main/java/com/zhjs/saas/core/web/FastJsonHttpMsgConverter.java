package com.yuanhan.yuanhan.core.web;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.ToStringSerializer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import com.yuanhan.yuanhan.core.exception.ExceptionResponse;
import com.yuanhan.yuanhan.core.util.StringUtil;
import com.yuanhan.yuanhan.core.util.WebUtil;

/**
 * 
 * @author:		 yuanhan
 * @since:		 2017-05-20
 * @modified:	 2017-05-20
 * @version:
 */
public class FastJsonHttpMsgConverter extends FastJsonHttpMessageConverter
{
	
	private String excludePath;

	public FastJsonHttpMsgConverter(String excludePath, String dateFormat)
	{
		this.excludePath = excludePath;
		FastJsonConfig config = new FastJsonConfig();
		config.setSerializerFeatures(SerializerFeature.PrettyFormat,
				SerializerFeature.WriteNullStringAsEmpty,
				SerializerFeature.WriteNullBooleanAsFalse,
				SerializerFeature.WriteNullNumberAsZero,
				SerializerFeature.WriteNullListAsEmpty,
				SerializerFeature.WriteMapNullValue,
				SerializerFeature.WriteEnumUsingName,
				SerializerFeature.IgnoreNonFieldGetter,
				SerializerFeature.SkipTransientField,
				SerializerFeature.WriteDateUseDateFormat,
				SerializerFeature.DisableCircularReferenceDetect);
		if(StringUtil.isNotBlank(dateFormat))
			config.setDateFormat(dateFormat);
		SerializeConfig sc = SerializeConfig.globalInstance;
		sc.put(BigDecimal.class, ToStringSerializer.instance);
		sc.put(Double.TYPE, ToStringSerializer.instance);
		sc.put(Double.class, ToStringSerializer.instance);
		sc.put(Float.TYPE, ToStringSerializer.instance);
		sc.put(Float.class, ToStringSerializer.instance);
		config.setSerializeConfig(sc);
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
    	if( !(obj instanceof CommonResponse) && match() )
    		obj = new CommonResponse(obj);
    	
        HttpHeaders headers = outputMessage.getHeaders();
        ByteArrayOutputStream outnew = new ByteArrayOutputStream();
		int len = JSON.writeJSONString(outnew, //
                this.getFastJsonConfig().getCharset(), //
                obj, //
                this.getFastJsonConfig().getSerializeConfig(), //
                this.getFastJsonConfig().getSerializeFilters(), //
                this.getFastJsonConfig().getDateFormat(), //
                JSON.DEFAULT_GENERATE_FEATURE, //
                this.getFastJsonConfig().getSerializerFeatures());
        if (this.getFastJsonConfig().isWriteContentLength()) 
        {
            headers.setContentLength(len);
        }
        OutputStream out = outputMessage.getBody();
        outnew.writeTo(out);
        outnew.close();
    }

    private boolean match()
    {
    	HttpServletRequest request = WebUtil.getRequest();
    	if(request!=null && StringUtil.isNotBlank(excludePath))
			return !request.getRequestURI().matches(excludePath+"(.*)");
    	return true;
    }
    
    protected List<MediaType> initMediaType()
    {
		List<MediaType> supportedMediaTypes = new ArrayList<>();
		supportedMediaTypes.add(MediaType.APPLICATION_JSON);
		supportedMediaTypes.add(MediaType.APPLICATION_JSON_UTF8);
		supportedMediaTypes.add(MediaType.APPLICATION_ATOM_XML);
		supportedMediaTypes.add(MediaType.APPLICATION_FORM_URLENCODED);
		supportedMediaTypes.add(MediaType.APPLICATION_RSS_XML);
		supportedMediaTypes.add(MediaType.APPLICATION_XHTML_XML);
		supportedMediaTypes.add(MediaType.APPLICATION_XML);
		supportedMediaTypes.add(MediaType.MULTIPART_FORM_DATA);
		supportedMediaTypes.add(MediaType.TEXT_EVENT_STREAM);
		supportedMediaTypes.add(MediaType.TEXT_HTML);
		supportedMediaTypes.add(MediaType.TEXT_MARKDOWN);
		supportedMediaTypes.add(MediaType.TEXT_PLAIN);
		supportedMediaTypes.add(MediaType.TEXT_XML);
		return supportedMediaTypes;
    }
}
