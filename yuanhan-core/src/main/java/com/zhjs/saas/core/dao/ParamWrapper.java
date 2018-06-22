package com.yuanhan.yuanhan.core.dao;

import java.util.Map;

import com.yuanhan.yuanhan.core.util.ClassUtil;

/**
 * 
 * @author:		yuanhan
 * @since:		2017-10-14
 * @modified:	2017-10-14
 * @version:	
 */
public class ParamWrapper
{
	
	private Class<?> outType;
	private Map<String,Class<?>> outMap;
	
	public ParamWrapper(Class<?> outType)
	{
		this.outType = outType;
	}
	
	public ParamWrapper(Map<String,Class<?>> outMap)
	{
		this.outMap = outMap;
	}
	
	public boolean isBeanWrap()
	{
		if(outType!=null && !ClassUtil.isPrimitiveOrWrapper(outType))
			return true;
		return false;
	}

	/**
	 * @return the outType
	 */
	public Class<?> getOutType()
	{
		return outType;
	}

	/**
	 * @param outType the outType to set
	 */
	public void setOutType(Class<?> outType)
	{
		this.outType = outType;
	}

	/**
	 * @return the outMap
	 */
	public Map<String,Class<?>> getOutMap()
	{
		return outMap;
	}

	/**
	 * @param outMap the outMap to set
	 */
	public void setOutMap(Map<String,Class<?>> outMap)
	{
		this.outMap = outMap;
	}

}
