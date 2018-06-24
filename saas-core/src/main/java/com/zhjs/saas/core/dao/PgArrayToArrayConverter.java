package com.zhjs.saas.core.dao;

import java.lang.reflect.Array;
import java.util.Collections;
import java.util.Set;

import org.postgresql.jdbc.PgArray;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.ConditionalGenericConverter;
import org.springframework.util.StringUtils;

/**
 * 
 * @author:		Jackie Wang 
 * @since:		2018-06-13
 * @modified:	2018-06-13
 * @version:	
 */
public class PgArrayToArrayConverter implements ConditionalGenericConverter
{

	private final ConversionService conversionService;

	public PgArrayToArrayConverter(ConversionService conversionService) {
		this.conversionService = conversionService;
	}

	@Override
	public Set<ConvertiblePair> getConvertibleTypes()
	{
		return Collections.singleton(new ConvertiblePair(PgArray.class, Object[].class));
	}

	@Override
	public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType)
	{
		if (source == null) {
			return null;
		}
		String value = source.toString();
		String[] fields = StringUtils.commaDelimitedListToStringArray(value.substring(1,value.length()-1));
		Object target = Array.newInstance(targetType.getElementTypeDescriptor().getType(), fields.length);
		for (int i = 0; i < fields.length; i++) {
			String sourceElement = fields[i];
			Object targetElement = this.conversionService.convert(sourceElement.trim().replaceAll("\"",""),
											TypeDescriptor.forObject(sourceElement), targetType.getElementTypeDescriptor());
			Array.set(target, i, targetElement);
		}
		return target;
	}

	@Override
	public boolean matches(TypeDescriptor sourceType, TypeDescriptor targetType)
	{
		return sourceType.getType().isAssignableFrom(PgArray.class);
				// && conversionService.canConvert(sourceType, targetType.getElementTypeDescriptor());;
	}


}
