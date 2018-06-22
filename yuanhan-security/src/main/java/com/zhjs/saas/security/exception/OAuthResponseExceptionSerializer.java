package com.yuanhan.yuanhan.security.exception;

import java.io.IOException;
import java.util.Map.Entry;

import org.springframework.web.util.HtmlUtils;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.yuanhan.yuanhan.core.util.MessageUtil;

/**
 * 
 * @author:		yuanhan
 * @since:		2018-04-13
 * @modified:	2018-04-13
 * @version:	
 */
@SuppressWarnings("serial")
public class OAuthResponseExceptionSerializer extends StdSerializer<OAuthResponseException>
{
	
	public OAuthResponseExceptionSerializer()
	{
		super(OAuthResponseException.class);
	}

	@Override
	public void serialize(OAuthResponseException value, JsonGenerator jgen, SerializerProvider provider) throws IOException,
			JsonProcessingException {
        jgen.writeStartObject();
		jgen.writeBooleanField("success", false);
		jgen.writeStringField("errorCode", value.getErrorCode());
		jgen.writeStringField("message", MessageUtil.getMessage(value.getErrorCode()));
		jgen.writeObjectFieldStart("data");
		jgen.writeStringField("error", value.getOAuth2ErrorCode());
		String errorMessage = value.getMessage();
		if (errorMessage != null) {
			errorMessage = HtmlUtils.htmlEscape(errorMessage);
		}
		jgen.writeStringField("error_description", errorMessage);
		jgen.writeEndObject();
		if (value.getAdditionalInformation()!=null) {
			for (Entry<String, String> entry : value.getAdditionalInformation().entrySet()) {
				String key = entry.getKey();
				String add = entry.getValue();
				jgen.writeStringField(key, add);				
			}
		}
        jgen.writeEndObject();
	}

}