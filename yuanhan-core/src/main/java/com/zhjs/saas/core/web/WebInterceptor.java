package com.yuanhan.yuanhan.core.web;

import com.yuanhan.yuanhan.core.pojo.BaseObject;

/**
 * 
 * @author:		yuanhan
 * @since:		2017-11-13
 * @modified:	2017-11-13
 * @version:	
 */
public interface WebInterceptor
{
	
	public <T extends BaseObject> void before(T entity);
	
	public <T extends BaseObject> void after(T entity);

}
