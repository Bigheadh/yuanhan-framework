package com.zhjs.saas.core.dao;

import java.util.Arrays;
import java.util.List;

import javax.persistence.AttributeConverter;

import com.zhjs.saas.core.util.StringUtil;

/**
 * 
 * @author:		Jackie Wang 
 * @since:		2018-01-03
 * @modified:	2018-01-03
 * @version:	
 */
public class CollectionConverter implements AttributeConverter<List<String>, String>
{

	@Override
	public String convertToDatabaseColumn(List<String> attribute)
	{
		if(attribute==null)
			return null;
		if(attribute.size()==0)
			return StringUtil.EMPTY;
		
		return String.join(",", attribute);
	}

	@Override
	public List<String> convertToEntityAttribute(String dbData)
	{
		if(StringUtil.isEmpty(dbData))
			return null;
		return Arrays.asList(dbData.split(","));
	}
}
